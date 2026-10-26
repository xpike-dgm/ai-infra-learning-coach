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
    /**
     * An `assess` task when the assessment session was not wired (16F). Since 16H the session runs here and nothing
     * produces this gap; it stays so a name that was once shown keeps its meaning.
     */
    ASSESSMENT_INTERIOR_NOT_AVAILABLE("assessment_interior_not_available"),

    /** None of the task's items can be judged here: code items need their test report, a rubric needs an evaluator. */
    NO_ITEM_THIS_APP_CAN_EVALUATE("no_item_this_app_can_evaluate"),

    /** Every item the app could judge has already been seen, so none is a fresh question (`LFPS-v0` exposure). */
    NO_UNSEEN_ITEM("no_unseen_item"),
}

/**
 * What kind of measurement a run is (16H, `ASRX-v0`): a daily assessment task, one slot of this week's or this month's
 * blueprint, or one check of the learner's open diagnostic. Every one runs in the **one** assessment interior
 * (`ASUX-v0` §4); the kind is context, never extra evidence weight.
 */
enum class MeasurementKind(val id: String, val scope: AssessmentScope) {
    DAILY_ASSESSMENT("daily_assessment", AssessmentScope.DAILY_MICRO),
    WEEKLY_SLOT("weekly_slot", AssessmentScope.WEEKLY_BLUEPRINT),
    MONTHLY_SLOT("monthly_slot", AssessmentScope.MONTHLY_CAPABILITY),
    DIAGNOSTIC("diagnostic", AssessmentScope.DAILY_MICRO),
    ;

    val isSlot: Boolean get() = this == WEEKLY_SLOT || this == MONTHLY_SLOT
}

/**
 * The assessment session a measuring run's attempts belong to (`DDM-v0`: `attempt.assessment_session_id`). A slot's and
 * a diagnostic's session exist before the run (the blueprint, the learner's request); a daily assessment's is opened
 * when the learner begins, so [sessionId] is `null` only until then. [intent] is what its items must be trusted for.
 */
data class Measurement(
    val kind: MeasurementKind,
    val intent: AssessmentIntent,
    val sessionId: Long?,
    val slotId: String? = null,
) {
    init {
        require(kind.isSlot == (slotId != null)) { "only a blueprint slot names its slot" }
        require(kind == MeasurementKind.DAILY_ASSESSMENT || sessionId != null) { "a slot's and a diagnostic's session exist before the run" }
    }

    val scope: AssessmentScope get() = kind.scope
}

/** What a task run works from: the planned task, its authored content and the items the app can serve and judge. */
data class RunnableTask(
    val plannedTaskId: Long,
    val position: Int,
    val planVersionId: Long,
    /** The authored task; `null` for a blueprint slot or a diagnostic check, which are planned items, not authored tasks (16H). */
    val task: VersionedRef?,
    val title: String,
    val purpose: TaskPurpose,
    val skill: VersionedRef,
    val targetObjectives: List<VersionedRef>,
    /** The plan's own estimate for today; planning context, never a promise or a countdown (`TRUX-v0` §6.2). */
    val plannedMinutes: Int,
    val needKey: String,
    /** The course's own explanation of each target Objective (`ExplanationForm.CANONICAL`), in the task's order. */
    val lesson: List<ExplanationVariant>,
    /** Unseen items the app can judge — by an answer key, the course's tests or a rubric (16G) — in the task's authored order. */
    val items: List<VersionedRef>,
    /** 16H: the measurement this run is, or `null` for a teaching or practice task. */
    val measurement: Measurement? = null,
) {
    init {
        require(lesson.all { it.form == ExplanationForm.CANONICAL }) { "the lesson is the course's own explanation" }
        require(task != null || (measurement != null && measurement.kind != MeasurementKind.DAILY_ASSESSMENT)) {
            "only a blueprint slot or a diagnostic check runs without an authored task"
        }
        require(measurement == null || lesson.isEmpty()) { "a measurement shows no lesson first: it is a check, not teaching" }
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
    /**
     * 16G: the attempt is recorded but no evaluator answered (`TRUX-v0` §12.2): `evaluation_pending`, neither passed nor
     * failed, and no evidence is written. [result] is then [AnswerResult.NOT_MEASURED].
     */
    val pending: Boolean = false,
    val artifactId: Long? = null,
    /** 16G: the independence its recorded help and provenance gave it (`2D` §6), kept so a later judgement uses the same. */
    val independence: IndependenceClass? = null,
) {
    init {
        require(!pending || evidenceIds.isEmpty()) { "a pending evaluation writes no evidence" }
    }
}

/**
 * How the app can judge an item it serves (16G). An answer key judges a short answer on the device (14F); the course's
 * tests judge code, run by the learner on their computer and pasted back (14D); a rubric is judged by the evaluator,
 * at most provisionally (14F). An item none of these can judge is not served.
 */
enum class RunItemKind(val id: String) {
    ANSWER_KEY("answer_key"),
    CODE_TESTS("code_tests"),
    RUBRIC("rubric"),
}

/**
 * What submitting an answer did (16G). [Recorded] froze an attempt; [NotRecorded] wrote nothing at all — a code answer
 * whose report cannot be read is sent back to be fixed, never counted against the learner (14D).
 */
sealed interface RunAnswer {
    data class Recorded(
        val answered: AnsweredItem,
        val code: CodeVerdict? = null,
        val open: OpenResponseVerdict? = null,
        val criteria: List<RubricCriterion> = emptyList(),
        /** 14E: what follows code the learner did not write alone; nothing for an open answer or code they wrote. */
        val offer: ComprehensionOffer = ComprehensionOffer.NotOffered,
    ) : RunAnswer

    data class NotRecorded(val code: CodeNotMeasured? = null, val tooLong: Boolean = false, val blank: Boolean = false) : RunAnswer
}
