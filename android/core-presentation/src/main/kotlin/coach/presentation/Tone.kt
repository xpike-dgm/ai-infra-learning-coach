package coach.presentation

/**
 * `VDSX-v0 / D-073`: the design system is an **expression layer**. It may not add severity the
 * canonical state does not claim, so tone is assigned from meaning, never from feel.
 */
enum class Tone(val id: String) {
    NEUTRAL("neutral"),
    ACTIVE("active"),
    POSITIVE_CONFIRMED("positive_confirmed"),
    ATTENTION("attention"),
    PENDING_UNRESOLVED("pending_unresolved"),
    SYSTEM_FAULT("system_fault"),
}

/**
 * The tones a **learning** state may take.
 *
 * `system_fault` is deliberately absent. `VDSX-v0` forbids any learning state from wearing the
 * fault tone, and a five-value type makes that impossible to write rather than something a
 * reviewer has to notice: there is no `LearningTone.SYSTEM_FAULT` to assign.
 */
enum class LearningTone(val tone: Tone) {
    NEUTRAL(Tone.NEUTRAL),
    ACTIVE(Tone.ACTIVE),
    POSITIVE_CONFIRMED(Tone.POSITIVE_CONFIRMED),
    ATTENTION(Tone.ATTENTION),
    PENDING_UNRESOLVED(Tone.PENDING_UNRESOLVED),
}

/** Exactly one declared tone per Skill state (`VDSX-v0` §skill_state_tones). */
val SkillPresentationState.tone: LearningTone
    get() = when (this) {
        SkillPresentationState.NOT_YET_EVIDENCED -> LearningTone.NEUTRAL
        SkillPresentationState.DEVELOPING_WITH_SUPPORT -> LearningTone.ACTIVE
        SkillPresentationState.DEVELOPING_INDEPENDENT -> LearningTone.ACTIVE
        SkillPresentationState.CONFIRMED_CURRENT -> LearningTone.POSITIVE_CONFIRMED
        SkillPresentationState.CONFIRMED_REVIEW_DUE -> LearningTone.NEUTRAL
        SkillPresentationState.CONFIRMATION_VERIFICATION_DUE -> LearningTone.ATTENTION
        SkillPresentationState.REMEDIATION_REQUIRED -> LearningTone.ATTENTION
        SkillPresentationState.PREREQUISITE_UNRESOLVED -> LearningTone.NEUTRAL
    }

/** Attention qualifiers carry their own tone and never change the state's (`VDSX-v0`). */
enum class QualifierTone(val id: String, val tone: LearningTone) {
    RETENTION_AT_RISK("retention_at_risk", LearningTone.ATTENTION),
    REQUIRES_INDEPENDENT_RECHECK("requires_independent_recheck", LearningTone.ATTENTION),
    CONTESTED_EVIDENCE_PRESENT("contested_evidence_present", LearningTone.PENDING_UNRESOLVED),
    PROVISIONAL_EVALUATION_PRESENT("provisional_evaluation_present", LearningTone.PENDING_UNRESOLVED),
}

/**
 * Appearing in an attention group is a grouping fact, not a severity fact. Returning the state's
 * own tone unchanged is the whole rule; it exists as a named function so the intent is testable
 * rather than implied by the absence of code.
 */
fun toneInAttentionGroup(state: SkillPresentationState): LearningTone = state.tone

/**
 * The only states that may wear the fault tone. Both are system conditions, not judgements about
 * the learner (`VDSX-v0` §fault_tone_allowed_states).
 */
enum class SystemFaultState(val id: String) {
    ERROR_RECOVERABLE("error_recoverable"),
    DATA_RECOVERY_REQUIRED("data_recovery_required"),
    ;

    val tone: Tone get() = Tone.SYSTEM_FAULT
}
