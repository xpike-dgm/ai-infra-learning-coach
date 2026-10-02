package coach.ai

import coach.model.CodeEvaluationInstructions
import coach.model.ComponentResult
import coach.model.CriterionVerdict
import coach.model.EvaluationResult
import coach.model.EvaluatorAvailability
import coach.model.EvaluatorRef
import coach.model.MisconceptionHypothesis
import coach.model.OpenResponseInstructions
import coach.model.OutcomeSignal
import coach.model.PendingReason
import coach.model.RubricFinding
import coach.ports.EvaluationRequest
import coach.ports.EvaluatorPort

/**
 * The evaluator's call sites (14G): an open response judged by its rubric (`OREX-v0`) and code with no tests where the
 * task allows a provisional result (`CDEX-v0`). Optional module: nothing outside app-wiring may reference this type.
 *
 * The router picks the task class from the request itself: a request that carries a rubric is an open response;
 * otherwise it is code — the only other caller (`EvaluateCode`). Each class has its own instructions, schema and model,
 * all from core or configuration. What comes back is at most `Provisional`, and core still decides what it means
 * (`OpenResponse.acceptAi`, `CodeEvaluation.acceptAi`); a reply that does not match its schema exactly is no answer.
 */
class AiEvaluator(
    private val client: OpenAiResponses?,
    private val keys: ApiKeySource? = null,
    private val provider: String = "openai",
) : EvaluatorPort {

    /** The build without a client (no adapter configured) is unavailable — never a crash, never a verdict. */
    constructor() : this(null)

    /** Available only with a client and a key the learner entered; removing the key returns the null-evaluator path. */
    val availability: EvaluatorAvailability
        get() = if (client != null && !keys?.current().isNullOrBlank()) EvaluatorAvailability.AVAILABLE else EvaluatorAvailability.UNAVAILABLE

    override fun evaluate(request: EvaluationRequest): EvaluationResult {
        val client = client ?: return EvaluationResult.EvaluationPending(PendingReason.UNAVAILABLE)
        return if (request.rubric.isNotEmpty()) openResponse(client, request) else code(client, request)
    }

    private fun openResponse(client: OpenAiResponses, request: EvaluationRequest): EvaluationResult {
        val message = OpenResponseInstructions.userMessage(request.objectiveRefs, request.promptText, request.rubric, request.learnerResponse, request.misconceptionCatalog)
        val outcome = client.call(TaskClass.OPEN_RESPONSE_EVALUATION, OpenResponseInstructions.TEXT, schemaName(OpenResponseInstructions.REPLY_SCHEMA_ID),
            OpenResponseInstructions.REPLY_SCHEMA, message)
        val delivered = outcome as? ProviderOutcome.Delivered ?: return EvaluationResult.EvaluationPending((outcome as ProviderOutcome.NotDelivered).reason)
        val json = delivered.json
        if (json.fields.keys != setOf("findings", "misconception_hypotheses")) return invalid()
        val findings = (json["findings"] as? JsonValue.Arr)?.items?.map { item ->
            val obj = item as? JsonValue.Obj ?: return invalid()
            if (obj.fields.keys != setOf("criterion", "verdict")) return invalid()
            val criterion = (obj["criterion"] as? JsonValue.Str)?.value ?: return invalid()
            val verdict = (obj["verdict"] as? JsonValue.Str)?.value?.let { id -> CriterionVerdict.entries.firstOrNull { it.id == id } } ?: return invalid()
            RubricFinding(criterion, verdict)
        } ?: return invalid()
        val hypotheses = labels(json["misconception_hypotheses"], request) ?: return invalid()
        // Core derives every Objective's signal from the findings; the evaluator is not asked for one.
        return EvaluationResult.Provisional(emptyList(), ref(delivered.model, OpenResponseInstructions.REPLY_SCHEMA_ID), hypotheses, findings)
    }

    private fun code(client: OpenAiResponses, request: EvaluationRequest): EvaluationResult {
        val message = CodeEvaluationInstructions.userMessage(request.objectiveRefs, request.promptText, request.learnerResponse, request.misconceptionCatalog)
        val outcome = client.call(TaskClass.CODE_EVALUATION, CodeEvaluationInstructions.TEXT, schemaName(CodeEvaluationInstructions.REPLY_SCHEMA_ID),
            CodeEvaluationInstructions.REPLY_SCHEMA, message)
        val delivered = outcome as? ProviderOutcome.Delivered ?: return EvaluationResult.EvaluationPending((outcome as ProviderOutcome.NotDelivered).reason)
        val json = delivered.json
        if (json.fields.keys != setOf("components", "misconception_hypotheses")) return invalid()
        val byId = request.objectiveRefs.associateBy { CodeEvaluationInstructions.objectiveId(it) }
        val signals = CodeEvaluationInstructions.SIGNAL_IDS.entries.associate { (signal, id) -> id to signal }
        val components = (json["components"] as? JsonValue.Arr)?.items?.map { item ->
            val obj = item as? JsonValue.Obj ?: return invalid()
            if (obj.fields.keys != setOf("objective", "signal")) return invalid()
            // An objective the request did not name is not the evaluator's to judge: no answer, never a guess.
            val objective = (obj["objective"] as? JsonValue.Str)?.value?.let { byId[it] } ?: return invalid()
            val signal: OutcomeSignal = (obj["signal"] as? JsonValue.Str)?.value?.let { signals[it] } ?: return invalid()
            ComponentResult(objective, signal)
        } ?: return invalid()
        val hypotheses = labels(json["misconception_hypotheses"], request) ?: return invalid()
        return EvaluationResult.Provisional(components, ref(delivered.model, CodeEvaluationInstructions.REPLY_SCHEMA_ID), hypotheses)
    }

    /**
     * Proposed labels, only from the catalog the request listed. The reply names no Objective for a label, so each is
     * proposed for every targeted Objective; the evidence pipeline keeps it only where the catalog declares it (14B).
     */
    private fun labels(value: JsonValue?, request: EvaluationRequest): List<MisconceptionHypothesis>? {
        val ids = (value as? JsonValue.Arr)?.items?.map { (it as? JsonValue.Str)?.value ?: return null } ?: return null
        if (ids.any { it !in request.misconceptionCatalog }) return null
        return ids.distinct().flatMap { id -> request.objectiveRefs.map { MisconceptionHypothesis(it, id) } }
    }

    private fun ref(model: String, schemaId: String) = EvaluatorRef(provider, model, schemaId)

    private fun invalid(): EvaluationResult = EvaluationResult.EvaluationPending(PendingReason.INVALID_RESPONSE)
}
