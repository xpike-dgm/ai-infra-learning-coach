package coach.model

/**
 * The daily micro assessment's vocabularies and item contract (11D), copied from their owners:
 * scopes, intents and the interior's rules from `ASUX-v0`; the item's minimum validity contract from
 * `DMA-v0` §15; lifecycle, use ceiling, content origin and evaluator requirement from `QAB-v0`;
 * the promotion rules that cap an unvalidated item from `AIV-v0`.
 *
 * These live in `core-model` because the session interior (`core-presentation`) shows them and the
 * use cases (`core-application`) read and record them, and neither may depend on the other.
 */

/** `ASUX-v0` §4: three scopes, one interior. The scope is displayed context, never a rule set. */
enum class AssessmentScope(val id: String, val storedAs: String) {
    DAILY_MICRO("daily_micro", "daily"),
    WEEKLY_BLUEPRINT("weekly_blueprint", "weekly"),
    MONTHLY_CAPABILITY("monthly_capability", "monthly"),
    ;

    // [storedAs] is the value `assessment_session.scope` holds (10D's CHECK); the interior shows [id].
}

/** `DMA-v0` §3: why a measurement runs. */
enum class AssessmentIntent(val id: String) {
    CHECKPOINT("checkpoint"),
    MASTERY_EVIDENCE("mastery_evidence"),
    VERIFICATION("verification"),
    INTEGRATION_CHECK("integration_check"),
}

/** `QAB-v0` §5, in its order. */
enum class LifecycleStatus(val id: String) {
    DRAFT("draft"),
    CANDIDATE("candidate"),
    VALIDATED("validated"),
    TRUSTED("trusted"),
    DEPRECATED("deprecated"),
    INVALIDATED("invalidated"),
    RETIRED("retired"),
    ;

    /**
     * Whether a new selection may use this version at all. `deprecated` is not preferred for new
     * selection but is not forbidden here — preference is the selector's (12) — while `draft`,
     * `invalidated` and `retired` are never selectable.
     */
    val selectable: Boolean
        get() = this == CANDIDATE || this == VALIDATED || this == TRUSTED || this == DEPRECATED
}

/**
 * `QAB-v0` §6 / `AIV-v0` §22: a semantic ceiling on what a resource may be used for, ordered from
 * most to least restricted. It is not a score, and the effective ceiling is always the **most
 * restrictive** applicable rule.
 */
enum class UseCeiling(val id: String) {
    PRACTICE_ONLY("practice_only"),
    LOW_STAKES_ASSESSMENT("low_stakes_assessment"),
    STANDARD_MASTERY_ELIGIBLE("standard_mastery_eligible"),
    CRITICAL_MASTERY_ELIGIBLE("critical_mastery_eligible"),
    ;

    fun atMost(other: UseCeiling): UseCeiling = if (ordinal <= other.ordinal) this else other

    fun permits(required: UseCeiling): Boolean = ordinal >= required.ordinal
}

/** `QAB-v0` §7. `ai_generated` is not invalid by itself, and is not trusted by itself either. */
enum class ContentOrigin(val id: String) {
    HUMAN_AUTHORED("human_authored"),
    PARAMETERIZED_FROM_TRUSTED_TEMPLATE("parameterized_from_trusted_template"),
    AI_GENERATED("ai_generated"),
    MIXED_AUTHORSHIP("mixed_authorship"),
    IMPORTED_REFERENCE_BASED("imported_reference_based"),
}

/** `TASK_TAXONOMY_SPEC` §independence_mode, in its order. */
enum class IndependenceMode(val id: String) {
    NOT_APPLICABLE("not_applicable"),
    GUIDED_ALLOWED("guided_allowed"),
    INDEPENDENT_EXPECTED("independent_expected"),
    H0_REQUIRED("h0_required"),
}

/**
 * `QAB-v0` §20. `deterministicRequired` and what the item can actually be checked with decide
 * whether the item may carry mastery-bearing evidence at all.
 */
data class EvaluatorRequirement(
    val requiredStatus: EvaluatorStatusRequirement,
    val deterministicRequired: Boolean,
    val evaluatorPolicyVersion: String,
) {
    init {
        require(evaluatorPolicyVersion.isNotBlank()) { "an evaluator requirement carries its policy version" }
    }
}

enum class EvaluatorStatusRequirement(val id: String) {
    VERIFIED("verified"),
    PROVISIONAL_ALLOWED("provisional_allowed"),
}

/**
 * `QAB-v0` §21: what the learner may use. Objective-appropriate tool use does not break H0
 * (`ASUX-v0` §6.1), so the policy is disclosed rather than policed after the fact.
 */
data class AllowedToolsPolicy(
    val allowed: List<String>,
    val prohibitedSolutionSources: List<String> = emptyList(),
) {
    /** `external_ai` is a tool like any other here: what it changes is disclosed, never punished. */
    val allowsExternalAi: Boolean get() = EXTERNAL_AI in allowed

    companion object {
        const val EXTERNAL_AI = "external_ai"
    }
}

/**
 * `DMA-v0` §15, the item's minimum validity contract. Every field here is one the contract names;
 * nothing else is invented, and there is no field that could hold a score, a grade or the learner's
 * current mastery state.
 */
data class AssessmentItem(
    val ref: VersionedRef,
    val targetObjectives: List<VersionedRef>,
    val targetSkills: List<VersionedRef>,
    val requiredSkills: List<VersionedRef>,
    val evidenceType: String,
    val expectedAnswerOrRubricRef: String,
    val evaluatorRequirement: EvaluatorRequirement,
    val allowedTools: AllowedToolsPolicy,
    val independenceMode: IndependenceMode,
    val difficultyClass: String,
    val lifecycleStatus: LifecycleStatus,
    val contentOrigin: ContentOrigin,
    val declaredUseCeiling: UseCeiling,
    val scopeEligibility: Set<AssessmentScope>,
    val variantFamilyId: String,
    val dependencyGroupId: String? = null,
    val forbiddenNotYetConcepts: List<String> = emptyList(),
    val deterministicVerification: Boolean = false,
    /**
     * `QAB-v0` §8 / §22: the item's expected active cost. 11D did not need it; a weekly slot does,
     * because a blueprint is fitted into real capacity and an item that says nothing about its duration
     * cannot be planned against a time budget (13A). `null` is "not declared", never a guessed default.
     */
    val expectedActiveMinutes: Int? = null,
    /**
     * `QAB-v0` §14: the blueprint roles the item is declared eligible for. The declaration is not a
     * priority — current state produces the role — but an item nobody declared for a role does not fill it.
     * Weekly (13A) and monthly (13B) role ids never collide, so one declaration list serves both scopes.
     */
    val blueprintRoles: Set<SlotRole> = emptySet(),
    /**
     * `QAB-v0` §17 (15G, `D-120`): the context the item asks for its target capability in. `null` is "not declared", and
     * an undeclared context is no transfer claim at all — however hard the item is (`AIV-v0` §16).
     */
    val transferProfile: TransferProfile? = null,
    /** `QAB-v0` §17: which context the item's problem is built in, so two items of one context are not two transfers. */
    val contextFamilyId: String? = null,
) {
    init {
        require(transferProfile == null || !contextFamilyId.isNullOrBlank()) { "a transfer claim names its context family (QAB-v0 §17)" }
        require(expectedActiveMinutes == null || expectedActiveMinutes > 0) { "an item takes some time, if it says" }
        require(targetObjectives.isNotEmpty()) { "an item with no target Objective attributes to nothing" }
        require(evidenceType.isNotBlank()) { "an item declares the evidence type it produces" }
        require(expectedAnswerOrRubricRef.isNotBlank()) { "an item carries its answer key or rubric reference" }
        require(variantFamilyId.isNotBlank()) { "an item belongs to a variant family (QAB-v0 §15)" }
    }
}

/**
 * `QAB-v0` §17's transfer profiles (15G, `D-120`). They are not numeric mastery multipliers. Only a context built from
 * another Topic can carry the month's cross-topic transfer slot (`MCA-v0` §5, §9); `same_context` and `near_context` are
 * not transfer at all, and a `novel_application` of the lesson's own Topic is not cross-topic.
 */
enum class TransferProfile(val id: String, val crossTopic: Boolean) {
    SAME_CONTEXT("same_context", false),
    NEAR_CONTEXT("near_context", false),
    CROSS_TOPIC_CONTEXT("cross_topic_context", true),
    NOVEL_APPLICATION("novel_application", false),
    INTEGRATED_SYSTEM_CONTEXT("integrated_system_context", true),
}

/**
 * What the Objective accepts as evidence (`KGC-v0`, carried by the curriculum store). It is the
 * Objective's, not the item's, which is exactly why an item cannot declare itself a fit.
 */
data class ObjectiveEvidenceProfile(
    val ref: VersionedRef,
    val acceptableEvidenceTypes: List<String>,
    val directEvidenceTypes: List<String>,
    val requiredDirectType: String? = null,
)

/**
 * The effective use ceiling of an item: the most restrictive applicable rule (`QAB-v0` §6,
 * `AIV-v0` §22–§27). Nothing here can raise an item above what it declared.
 */
object ItemTrust {

    fun effectiveCeiling(item: AssessmentItem, evaluatorAvailable: Boolean): UseCeiling? {
        if (!item.lifecycleStatus.selectable) return null
        var ceiling = item.declaredUseCeiling

        // AIV-v0 §23: a generated item that has not been validated is practice at best, whatever it
        // declares about itself. An item may not promote itself.
        if (item.lifecycleStatus == LifecycleStatus.CANDIDATE) ceiling = ceiling.atMost(UseCeiling.PRACTICE_ONLY)
        if (item.contentOrigin == ContentOrigin.AI_GENERATED && item.lifecycleStatus != LifecycleStatus.TRUSTED) {
            ceiling = ceiling.atMost(UseCeiling.STANDARD_MASTERY_ELIGIBLE)
        }

        // QAB-v0 §19–§20: without deterministic verification, a single uncalibrated evaluator does
        // not produce settled critical evidence.
        if (!item.deterministicVerification) {
            ceiling = ceiling.atMost(
                if (item.evaluatorRequirement.requiredStatus == EvaluatorStatusRequirement.PROVISIONAL_ALLOWED) {
                    UseCeiling.LOW_STAKES_ASSESSMENT
                } else {
                    UseCeiling.STANDARD_MASTERY_ELIGIBLE
                }
            )
        }
        if (item.evaluatorRequirement.deterministicRequired && !item.deterministicVerification) {
            ceiling = ceiling.atMost(UseCeiling.PRACTICE_ONLY)
        }
        // An item whose evaluator cannot run at all measures nothing today; it may still teach.
        if (!evaluatorAvailable && !item.deterministicVerification) ceiling = ceiling.atMost(UseCeiling.PRACTICE_ONLY)
        return ceiling
    }

    /** What an intent needs before an item may carry it (`DMA-v0` §3, `ASUX-v0` §12). */
    fun required(intent: AssessmentIntent): UseCeiling = when (intent) {
        AssessmentIntent.CHECKPOINT -> UseCeiling.LOW_STAKES_ASSESSMENT
        AssessmentIntent.INTEGRATION_CHECK -> UseCeiling.LOW_STAKES_ASSESSMENT
        AssessmentIntent.MASTERY_EVIDENCE -> UseCeiling.STANDARD_MASTERY_ELIGIBLE
        AssessmentIntent.VERIFICATION -> UseCeiling.STANDARD_MASTERY_ELIGIBLE
    }
}

/** Why an item may not be used here. Each value names a rule, never the learner. */
enum class ItemUnfit(val id: String) {
    LIFECYCLE_NOT_SELECTABLE("lifecycle_not_selectable"),
    SCOPE_NOT_ELIGIBLE("scope_not_eligible"),
    USE_CEILING_BELOW_INTENT("use_ceiling_below_intent"),
    EVIDENCE_TYPE_NOT_ACCEPTED_BY_OBJECTIVE("evidence_type_not_accepted_by_objective"),
    DIRECT_EVIDENCE_TYPE_REQUIRED("direct_evidence_type_required"),
    OBJECTIVE_PROFILE_UNKNOWN("objective_profile_unknown"),
}

sealed interface ItemFit {
    data class Usable(val ceiling: UseCeiling) : ItemFit

    /** Not usable for this intent. `reasons` names the rules, and it is never negative evidence. */
    data class NotUsable(val reasons: Set<ItemUnfit>) : ItemFit
}

/**
 * `DMA-v0` §12 evidence fit plus `QAB-v0` §13 scope eligibility and the trust ceiling, applied
 * together. The Objective decides what counts as evidence for it; the item only declares what it
 * produces, so a convenient MCQ cannot become production evidence by claiming to be.
 */
object ItemSelection {

    fun fit(
        item: AssessmentItem,
        intent: AssessmentIntent,
        scope: AssessmentScope,
        profiles: Map<VersionedRef, ObjectiveEvidenceProfile>,
        evaluatorAvailable: Boolean,
    ): ItemFit {
        val reasons = buildSet {
            if (!item.lifecycleStatus.selectable) add(ItemUnfit.LIFECYCLE_NOT_SELECTABLE)
            if (scope !in item.scopeEligibility) add(ItemUnfit.SCOPE_NOT_ELIGIBLE)

            val ceiling = ItemTrust.effectiveCeiling(item, evaluatorAvailable)
            if (ceiling == null || !ceiling.permits(ItemTrust.required(intent))) add(ItemUnfit.USE_CEILING_BELOW_INTENT)

            item.targetObjectives.forEach { objective ->
                val profile = profiles[objective]
                when {
                    profile == null -> add(ItemUnfit.OBJECTIVE_PROFILE_UNKNOWN)
                    item.evidenceType !in profile.acceptableEvidenceTypes ->
                        add(ItemUnfit.EVIDENCE_TYPE_NOT_ACCEPTED_BY_OBJECTIVE)
                    // A mastery-bearing measurement must produce the Objective's own direct type.
                    mastering(intent) && profile.requiredDirectType != null &&
                        profile.requiredDirectType != item.evidenceType -> add(ItemUnfit.DIRECT_EVIDENCE_TYPE_REQUIRED)
                    mastering(intent) && item.evidenceType !in profile.directEvidenceTypes ->
                        add(ItemUnfit.DIRECT_EVIDENCE_TYPE_REQUIRED)
                }
            }
        }
        val ceiling = ItemTrust.effectiveCeiling(item, evaluatorAvailable)
        return if (reasons.isEmpty() && ceiling != null) ItemFit.Usable(ceiling) else ItemFit.NotUsable(reasons)
    }

    private fun mastering(intent: AssessmentIntent): Boolean =
        intent == AssessmentIntent.MASTERY_EVIDENCE || intent == AssessmentIntent.VERIFICATION
}
