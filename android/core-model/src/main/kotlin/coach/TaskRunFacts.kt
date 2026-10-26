package coach.model

/**
 * Running one planned task end to end (16F, `E2EX-v0`; `TRUX-v0` §6, `RNRX-v0`).
 *
 * These are the facts the runner works from. They are read from the stored plan, the gate, the open needs and the
 * authored task — never decided by the runner, which stays an execution surface: it serves what the task holds, records
 * what the learner did, and hands the attempt to the evidence pipeline.
 */

/**
 * Why a task's activity cannot run in this app now. A fact about the app and the content, never about the learner, and
 * never negative evidence.
 */
enum class CapabilityGap(val id: String) {
    /** An `assess` task: its interior is the assessment session's (`TRUX-v0` §5), which is not wired yet. */
    ASSESSMENT_INTERIOR_NOT_AVAILABLE("assessment_interior_not_available"),

    /** None of the task's items can be judged here: code items need their test report, a rubric needs an evaluator. */
    NO_ITEM_THIS_APP_CAN_EVALUATE("no_item_this_app_can_evaluate"),

    /** Every item the app could judge has already been seen, so none is a fresh question (`LFPS-v0` exposure). */
    NO_UNSEEN_ITEM("no_unseen_item"),
}

/** What a task run works from: the planned task, its authored content and the items the app can serve and judge. */
data class RunnableTask(
    val plannedTaskId: Long,
    val position: Int,
    val planVersionId: Long,
    val task: VersionedRef,
    val title: String,
    val purpose: TaskPurpose,
    val skill: VersionedRef,
    val targetObjectives: List<VersionedRef>,
    /** The plan's own estimate for today; planning context, never a promise or a countdown (`TRUX-v0` §6.2). */
    val plannedMinutes: Int,
    val needKey: String,
    /** The course's own explanation of each target Objective (`ExplanationForm.CANONICAL`), in the task's order. */
    val lesson: List<ExplanationVariant>,
    /** Unseen items the app can judge by their answer key, in the task's authored order. */
    val items: List<VersionedRef>,
) {
    init {
        require(lesson.all { it.form == ExplanationForm.CANONICAL }) { "the lesson is the course's own explanation" }
    }
}

/**
 * What entry could confirm about one planned task (`TRUX-v0` §6.1). Each flag is a confirmation; `false` means it could
 * not be confirmed, which is not a failure. [run] exists only when every one is confirmed.
 */
data class TaskEntryFacts(
    val stillSelected: Boolean,
    val prerequisitesSatisfied: Boolean,
    val needStillOpen: Boolean,
    val contentCompatible: Boolean,
    val capabilityAvailable: Boolean,
    val capabilityGap: CapabilityGap? = null,
    val run: RunnableTask? = null,
) {
    val startable: Boolean get() = stillSelected && prerequisitesSatisfied && needStillOpen && contentCompatible && capabilityAvailable

    init {
        require((run != null) == startable) { "a task runs exactly when every entry condition is confirmed" }
        // A gap is named only when the capability was actually checked and is missing; when an earlier condition already
        // could not be confirmed, the capability was never checked and nothing is named.
        require(capabilityGap == null || !capabilityAvailable) { "an available capability has no gap" }
    }
}

/** How one answer was judged, as the runner may show it: right, partly, not right, or not measurable. Never a level. */
enum class AnswerResult(val id: String) {
    RIGHT("right"),
    PARTLY("partly"),
    NOT_RIGHT("not_right"),
    NOT_MEASURED("not_measured"),
    ;

    companion object {
        /** The answer-key evaluation's signal for the item's Objective. */
        fun of(signals: List<OutcomeSignal>): AnswerResult = when {
            signals.isEmpty() || signals.all { it == OutcomeSignal.NOT_RELIABLY_MEASURED } -> NOT_MEASURED
            signals.all { it == OutcomeSignal.MET } -> RIGHT
            signals.any { it == OutcomeSignal.MET || it == OutcomeSignal.PARTIALLY_MET } -> PARTLY
            else -> NOT_RIGHT
        }
    }
}

/** One submitted answer: the attempt it froze, how it was judged, and the evidence the pipeline wrote for it. */
data class AnsweredItem(
    val item: VersionedRef,
    val attemptId: Long,
    val result: AnswerResult,
    val evidenceIds: List<Long>,
)
