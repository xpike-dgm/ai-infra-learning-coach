package coach.model

/**
 * What recorded help means for one attempt's evidence (`2D` §5–§6, `TRUX-v0` §8.5, `TUTX-v0` §11).
 *
 * Until 14A this rule existed only in prose: the runner recorded every assistance event, but whoever
 * recorded the evidence passed an independence class it had decided for itself, so a tutor's full solution
 * could have been recorded as independent work. Now the class is **derived from what was recorded**, never
 * from how the answer looks and never from a guess about the learner.
 *
 * Four rules do the work:
 *
 * - **Only help before the answer froze touches it.** Help after submission or after a failure does not
 *   reach back into the completed attempt (`2D` §5.3); it is still recorded, because it can expose a solution.
 * - **Support around the target is not help with the target.** Environment, boilerplate and non-target
 *   language support never change the class (`TRUX-v0` §8.5, `TEIP-v0` §5.2).
 * - **Provenance is the learner's answer, taken as given.** Work they say was generated or copied shows a solution
 *   they did not produce: like a full solution shown, it opens an independent recheck (`2D` §8 scenario A; user
 *   decision, 14E, `D-109`) — practice in a teaching task, which never measured independence. Work they say they
 *   wrote with help is assisted. "Unknown" accuses no one and adds nothing (`2D` §11).
 * - **A teaching task is practice by design** (`2D` §6): it was never set up to measure independence.
 */
object AssistanceInterpretation {

    private val beforeTheAnswerFroze = setOf(AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT)

    fun independence(
        assistance: List<AssistanceEvent>,
        provenance: ProvenanceOrigin,
        purpose: TaskPurpose,
    ): IndependenceClass {
        val withTheTarget = assistance.filter { it.scope == AssistanceScope.TARGET_OBJECTIVE && it.timing in beforeTheAnswerFroze }
        return when {
            withTheTarget.any { it.level.revealsTargetReasoning } -> IndependenceClass.REQUIRES_INDEPENDENT_RECHECK
            purpose == TaskPurpose.TEACH -> IndependenceClass.PRACTICE_ONLY
            provenance == ProvenanceOrigin.GENERATED_OR_COPIED -> IndependenceClass.REQUIRES_INDEPENDENT_RECHECK
            withTheTarget.isNotEmpty() -> IndependenceClass.ASSISTED
            provenance == ProvenanceOrigin.USER_AUTHORED_WITH_ASSISTANCE ||
                provenance == ProvenanceOrigin.MIXED_AUTHORSHIP -> IndependenceClass.ASSISTED
            else -> IndependenceClass.INDEPENDENT
        }
    }
}

/** The submission's own independence, from its recorded help and the learner's provenance answer. */
fun AttemptSubmission.independence(purpose: TaskPurpose): IndependenceClass =
    AssistanceInterpretation.independence(assistance, provenance, purpose)
