package coach.ai

import coach.ai.OpenAiResponsesTest.Companion.completed
import coach.ai.OpenAiResponsesTest.Companion.refusal
import coach.ai.OpenAiResponsesTest.Scripted
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
import coach.model.RubricCriterion
import coach.model.RubricFinding
import coach.model.VersionedRef
import coach.ports.EvaluationRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * The evaluator's call sites (14G): an open response by its rubric, code by its objectives — each with core's own
 * instructions and schema — and only an exact reply is an answer, at most provisional.
 */
class AiEvaluatorTest {

    private val objective = VersionedRef("objective.c.pointers.explain_write_through", 1)
    private val rubric = listOf(RubricCriterion("names_target", objective, "*p, p'nin gösterdiği yeri ifade eder."))
    private val label = "misconception.c.pointers.address_value"

    private fun evaluator(transport: HttpTransport, key: String? = OpenAiResponsesTest.KEY) =
        AiEvaluator(OpenAiResponses(ProviderConfig(), { key }, transport) { 0L }, { key })

    @Test
    fun `without a client or a key the evaluator is unavailable, never a crash and never a verdict`() {
        val request = EvaluationRequest(listOf(objective), "Explain.", "It writes through p.")
        assertEquals(EvaluationResult.EvaluationPending(PendingReason.UNAVAILABLE), AiEvaluator().evaluate(request))
        assertEquals(EvaluatorAvailability.UNAVAILABLE, AiEvaluator().availability)
        assertEquals(EvaluatorAvailability.UNAVAILABLE, evaluator(Scripted(), key = null).availability)
        assertEquals(EvaluatorAvailability.AVAILABLE, evaluator(Scripted()).availability)
    }

    @Test
    fun `an open response goes with its rubric and comes back as findings only, for core to decide`() {
        val transport = Scripted({ completed("{\"findings\":[{\"criterion\":\"names_target\",\"verdict\":\"met\"}],\"misconception_hypotheses\":[\"$label\"]}") })
        val request = EvaluationRequest(listOf(objective), "*p ne demektir?", "gösterdiği yer", rubric, listOf(label))
        val result = assertIs<EvaluationResult.Provisional>(evaluator(transport).evaluate(request))
        assertEquals(emptyList(), result.componentResults, "the evaluator is not asked for a verdict")
        assertEquals(listOf(RubricFinding("names_target", CriterionVerdict.MET)), result.rubricFindings)
        assertEquals(listOf(MisconceptionHypothesis(objective, label)), result.misconceptionHypotheses)
        assertEquals(EvaluatorRef("openai", "gpt-6-astra", "open_response_evaluation/1"), result.evaluatorRef)
        val sent = Json.parse(transport.calls.single().third) as JsonValue.Obj
        assertEquals(Json.str(OpenResponseInstructions.TEXT), sent["instructions"])
        assertEquals(Json.str(OpenResponseInstructions.userMessage(listOf(objective), "*p ne demektir?", rubric, "gösterdiği yer", listOf(label))), sent["input"])
    }

    @Test
    fun `code with no tests goes with its objectives and comes back per objective`() {
        val transport = Scripted({ completed("{\"components\":[{\"objective\":\"objective.c.pointers.explain_write_through@v1\",\"signal\":\"partially_met\"}],\"misconception_hypotheses\":[]}") })
        val request = EvaluationRequest(listOf(objective), "x'i p üzerinden 5 yap.", "p = 5;")
        val result = assertIs<EvaluationResult.Provisional>(evaluator(transport).evaluate(request))
        assertEquals(listOf(ComponentResult(objective, OutcomeSignal.PARTIALLY_MET)), result.componentResults)
        assertEquals(EvaluatorRef("openai", "gpt-6-astra", "code_evaluation/1"), result.evaluatorRef)
        val sent = Json.parse(transport.calls.single().third) as JsonValue.Obj
        assertEquals(Json.str(CodeEvaluationInstructions.TEXT), sent["instructions"])
        assertEquals(Json.str(CodeEvaluationInstructions.userMessage(listOf(objective), "x'i p üzerinden 5 yap.", "p = 5;", emptyList())), sent["input"])
    }

    @Test
    fun `a label outside the catalog, an unnamed objective, an extra field or a refusal is no answer`() {
        val open = EvaluationRequest(listOf(objective), "t", "r", rubric, listOf(label))
        val code = EvaluationRequest(listOf(objective), "t", "c")
        val invalid = EvaluationResult.EvaluationPending(PendingReason.INVALID_RESPONSE)
        assertEquals(invalid, evaluator(Scripted({ completed("{\"findings\":[],\"misconception_hypotheses\":[\"misconception.x.invented\"]}") })).evaluate(open))
        assertEquals(invalid, evaluator(Scripted({ completed("{\"findings\":[{\"criterion\":\"names_target\",\"verdict\":\"great\"}],\"misconception_hypotheses\":[]}") })).evaluate(open))
        assertEquals(invalid, evaluator(Scripted({ completed("{\"findings\":[],\"misconception_hypotheses\":[],\"score\":0.9}") })).evaluate(open))
        assertEquals(invalid, evaluator(Scripted({ completed("{\"components\":[{\"objective\":\"objective.other@v1\",\"signal\":\"met\"}],\"misconception_hypotheses\":[]}") })).evaluate(code))
        assertEquals(invalid, evaluator(Scripted({ completed("{\"components\":[{\"objective\":\"objective.c.pointers.explain_write_through@v1\",\"signal\":\"MET\"}],\"misconception_hypotheses\":[]}") })).evaluate(code))
        assertEquals(EvaluationResult.EvaluationPending(PendingReason.REFUSED), evaluator(Scripted({ refusal() })).evaluate(code))
    }
}
