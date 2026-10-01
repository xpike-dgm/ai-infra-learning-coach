package coach.model

/**
 * What a learner's attempt is made of, in the vocabularies `DDM-v0` fixed and `TRUX-v0` §8 uses.
 *
 * These live in `core-model` because the runner (`core-presentation`) produces them and the
 * submission use case (`core-application`) persists them, and neither may depend on the other.
 * Every value set here is the one the schema's CHECK constraints enforce; the validator compares
 * them, so the runner cannot record a value the store would refuse.
 */

/** H1 orientation → H2 targeted concept → H3 partial scaffold → H4 full solution. */
enum class AssistanceLevel(val id: String) {
    H1("H1"),
    H2("H2"),
    H3("H3"),
    H4("H4"),
    ;

    /**
     * H3 and H4 reveal target reasoning, so the attempt can no longer count as independent
     * evidence (`TRUX-v0` §8.3). This is the whole reason consequence disclosure exists.
     */
    val revealsTargetReasoning: Boolean get() = this == H3 || this == H4
}

enum class AssistanceTiming(val id: String) {
    BEFORE_ATTEMPT("before_attempt"),
    DURING_ATTEMPT("during_attempt"),
    AFTER_SUBMIT("after_submit"),
    AFTER_FAILURE("after_failure"),
}

/** Help with the target reasoning is distinguished from help with everything around it. */
enum class AssistanceScope(val id: String) {
    TARGET_OBJECTIVE("target_objective"),
    NON_TARGET_SUPPORT("non_target_support"),
}

enum class AssistanceSource(val id: String) {
    DETERMINISTIC_CONTENT("deterministic_content"),
    AI_GENERATED("ai_generated"),
}

/** One granted piece of help, recorded exactly as `DDM-v0`'s `assistance_event` holds it. */
data class AssistanceEvent(
    val level: AssistanceLevel,
    val timing: AssistanceTiming,
    val scope: AssistanceScope,
    val source: AssistanceSource,
    val requestedByUser: Boolean,
)

/**
 * The learner's own answer to "who wrote this?" (`TRUX-v0` §9). Asked, never inferred — and
 * `UNKNOWN_PROVENANCE` is an honest answer, recorded as given rather than guessed.
 */
enum class ProvenanceOrigin(val id: String) {
    USER_AUTHORED("user_authored"),
    USER_AUTHORED_WITH_ASSISTANCE("user_authored_with_assistance"),
    MIXED_AUTHORSHIP("mixed_authorship"),
    GENERATED_OR_COPIED("generated_or_copied"),
    UNKNOWN_PROVENANCE("unknown_provenance"),
}

/**
 * A frozen attempt, ready to be recorded as one learner action (`LFPS-v0` §9).
 *
 * It carries no outcome, score or evidence. The runner is not the evidence evaluator
 * (`TRUX-v0`): what an attempt proves is decided later by the evidence pipeline (12), and while the
 * evaluator is unavailable it proves nothing yet — which is `evaluation_pending`, not a failure.
 */
data class AttemptSubmission(
    val resource: VersionedRef,
    val artifactContentRef: String,
    val provenance: ProvenanceOrigin,
    val assistance: List<AssistanceEvent> = emptyList(),
    /**
     * The assessment session this attempt was made in, when it was (13A). `DDM-v0` gives `attempt` an
     * `assessment_session_id`; a weekly result is read from the session's own attempts, never guessed.
     */
    val assessmentSessionId: Long? = null,
) {
    init {
        require(artifactContentRef.isNotBlank()) { "an attempt must reference the artifact it froze" }
    }

    /**
     * Derived from the recorded events, never stored beside them: a second copy of the same fact is
     * a second source of truth waiting to disagree with the first.
     */
    val highestAssistanceLevel: AssistanceLevel?
        get() = assistance.maxByOrNull { it.level.ordinal }?.level

    /**
     * Target reasoning was revealed, so a fresh unseen variant must later confirm the capability.
     * The runner raises this; it never schedules the recheck (`TRUX-v0` §8.4).
     */
    val requiresIndependentRecheck: Boolean
        get() = assistance.any { it.scope == AssistanceScope.TARGET_OBJECTIVE && it.level.revealsTargetReasoning }
}

/** What the store returned for a recorded attempt. */
data class AttemptRecorded(
    val attemptId: Long,
    val artifactId: Long,
    val assistanceEventIds: List<Long>,
)
