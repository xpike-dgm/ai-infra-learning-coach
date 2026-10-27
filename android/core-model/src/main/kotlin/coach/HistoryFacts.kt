package coach.model

/**
 * The learning history and the longitudinal assessment report (16B, `LHRX-v0`; `SPWX-v0` §§12–13, `ASUX-v0` §§13–14).
 *
 * Both are read, never written. What happened is the stored evidence; what it changed is what the canonical engines,
 * replayed over that evidence, decide before and after each row (user decision) — the same transitions the program
 * change report (13E) names. Nothing here is a score, a streak, an attendance count or a trend: an entry records an
 * event and its consequence, a gap between entries is just time, and a session's report is its families, never a grade.
 */

/** `SPWX-v0` §12's event families. The first that applies wins: a row inside an assessment session is assessment. */
enum class HistoryFamily(val id: String) {
    ASSESSMENT("assessment"),
    /** Work on an Objective while the Skill's remediation was open, or the row that closed it. */
    REMEDIATION("remediation"),
    /** Work on a Skill that was already mastered: a review or a recheck. */
    REVIEW("review"),
    LEARNING("learning"),
}

/**
 * One evidence row as the history shows it: what happened (the Objective, the kind of evidence, the outcome, the
 * assistance recorded on the attempt) and what it changed — the engines' own transitions, empty when nothing changed.
 */
data class HistoryEntry(
    val sequence: Long,
    val studyDay: String?,
    val skill: VersionedRef,
    val objective: VersionedRef,
    val family: HistoryFamily,
    val kind: EvidenceKind,
    val outcome: EvidenceOutcome,
    val assistance: List<AssistanceLevel>,
    val attemptId: Long?,
    val assessmentSessionId: Long?,
    val changes: List<StateChange>,
    /** Raw facts the report names a not-measured reason with (`ASUX-v0` §13.3). */
    val contested: Boolean = false,
    val solutionExposed: Boolean = false,
    /** 16H: the correction appended to the row and its reason (13D), so the history can say why it does not count. */
    val disposition: String? = null,
    val dispositionReason: String? = null,
)

/**
 * `ASUX-v0` §13.3: what a session could not measure. A row that cannot count, a provisional judgement, assisted or
 * solution-exposed work, and a ready slot the session's own evidence never answered — never folded into failure.
 */
data class NotMeasured(
    val evidence: List<HistoryEntry>,
    /** Ready slots of the session's blueprint that no evidence carrying this session's id answered. */
    val unansweredSlots: List<String>,
)

/** One assessment session across time: what the engines said it changed, what it could not measure, and the plan it changed. */
data class SessionReport(
    val sessionId: Long,
    /** `daily`, `weekly` or `monthly`, as stored; a diagnostic is a daily session. */
    val scope: String,
    val studyDay: String,
    val evidence: List<HistoryEntry>,
    val stateChanges: List<StateChange>,
    val notMeasured: NotMeasured,
    /** The plan changes of the version this session's recompute wrote; empty when no such version exists. */
    val planChanges: List<PlanChange>,
    /** Whether a plan version written for this session was found at all, so "no plan change" is not claimed blindly. */
    val replanFound: Boolean,
) {
}

/** Everything the history and the report can say. Newest first, as a history is read. */
data class HistoryFacts(
    val curriculumLoaded: Boolean,
    val entries: List<HistoryEntry>,
    val sessions: List<SessionReport>,
    /** The published name of every Skill an entry or a change names. */
    val skillNames: Map<VersionedRef, String> = emptyMap(),
    /** 17A: each named Objective's lesson title, as authored; absent when no lesson is authored. */
    val objectiveTitles: Map<VersionedRef, String> = emptyMap(),
)
