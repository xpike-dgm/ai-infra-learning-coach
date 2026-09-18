package coach.presentation

import coach.model.AssistanceEvent
import coach.model.AssistanceLevel
import coach.model.AssistanceScope
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.EvaluationResult

/**
 * `TRUX-v0 / D-070` as pure functions: **the Task Runner is an execution surface.** It is not the
 * planner, not the mastery authority, not the prerequisite authority and not the evidence
 * evaluator — so nothing here can rank a task, change a Skill state, advance to "the next task in
 * the list" or turn an attempt into a pass or a fail.
 *
 * It lives in `core-presentation` because what the runner tells the learner — whether they may
 * start, what help costs, what stopping means — is the part most likely to become coercive or
 * untrue, and it has to be testable as a plain JVM function (`MSBX-v0` §presentation).
 */

/** The seventeen semantic states `TRUX-v0` defines, in its order. */
enum class RunnerState(val id: String) {
    ENTERING_REVALIDATING("entering_revalidating"),
    BLOCKED_NOT_STARTABLE("blocked_not_startable"),
    ORIENTATION("orientation"),
    ACTIVE_WORK("active_work"),
    ASSISTANCE_OPEN("assistance_open"),
    SUBMITTING("submitting"),
    FEEDBACK_RESOLVED("feedback_resolved"),
    EVALUATION_PENDING("evaluation_pending"),
    CHECKPOINT_PAUSED("checkpoint_paused"),
    RESUME_REVALIDATING("resume_revalidating"),
    RESUME_INVALIDATED("resume_invalidated"),
    REPLAN_INTERRUPTED("replan_interrupted"),
    STOPPED_NO_PENALTY("stopped_no_penalty"),
    OFFLINE_LOCAL_CAPABLE("offline_local_capable"),
    AI_UNAVAILABLE_DETERMINISTIC_CORE("ai_unavailable_deterministic_core"),
    ERROR_RECOVERABLE("error_recoverable"),
    DATA_RECOVERY_REQUIRED("data_recovery_required"),
}

/**
 * Each runner state's tone, copied from `VDSX-v0`'s accepted map. `blocked_not_startable` is
 * `attention` — something must be resolved, but nothing was done wrong — and `evaluation_pending`
 * is `pending_unresolved`, because it is neither a pass nor a fail.
 */
val RunnerState.tone: Tone
    get() = when (this) {
        RunnerState.BLOCKED_NOT_STARTABLE -> Tone.ATTENTION
        RunnerState.ACTIVE_WORK, RunnerState.ASSISTANCE_OPEN, RunnerState.SUBMITTING -> Tone.ACTIVE
        RunnerState.EVALUATION_PENDING -> Tone.PENDING_UNRESOLVED
        RunnerState.ERROR_RECOVERABLE, RunnerState.DATA_RECOVERY_REQUIRED -> Tone.SYSTEM_FAULT
        RunnerState.ENTERING_REVALIDATING,
        RunnerState.ORIENTATION,
        RunnerState.FEEDBACK_RESOLVED,
        RunnerState.CHECKPOINT_PAUSED,
        RunnerState.RESUME_REVALIDATING,
        RunnerState.RESUME_INVALIDATED,
        RunnerState.REPLAN_INTERRUPTED,
        RunnerState.STOPPED_NO_PENALTY,
        RunnerState.OFFLINE_LOCAL_CAPABLE,
        RunnerState.AI_UNAVAILABLE_DETERMINISTIC_CORE -> Tone.NEUTRAL
    }

/** The six lifecycle phases, in order (`TRUX-v0` §task_run_lifecycle). */
enum class RunnerPhase(val id: String) {
    ENTER("enter"),
    ORIENT("orient"),
    WORK("work"),
    SUBMIT("submit"),
    RESOLVE("resolve"),
    TRANSITION("transition"),
}

/** The five conditions a task must still meet at the moment it is entered. */
enum class EntryCondition(val id: String) {
    PLANNED_TASK_STILL_SELECTED("planned_task_still_selected"),
    HARD_PREREQUISITES_SATISFIED("hard_prerequisites_satisfied"),
    LEARNING_NEED_STILL_OPEN("learning_need_still_open"),
    CONTENT_VERSION_COMPATIBLE("content_version_compatible"),
    REQUIRED_LOCAL_CAPABILITY_AVAILABLE("required_local_capability_available"),
}

/** The five conditions a paused task must still meet when it is resumed. */
enum class ResumeCondition(val id: String) {
    CONTENT_VERSION_COMPATIBLE("content_version_compatible"),
    PREREQUISITES_STILL_ELIGIBLE("prerequisites_still_eligible"),
    LEARNING_NEED_STILL_OPEN("learning_need_still_open"),
    RUNNER_AND_ARTIFACT_STATE_INTACT("runner_and_artifact_state_intact"),
    HIGH_STAKES_GAP_INTEGRITY_ACCEPTABLE("high_stakes_gap_integrity_acceptable"),
}

/**
 * The outcome of checking a task at entry or resume. A failed check is not the learner's error and
 * not negative evidence: it simply means the plan moved, and the learner goes back to Today with
 * the reason the planner recorded. There is no "failure" field to set.
 */
sealed interface Revalidation {
    data object Startable : Revalidation

    /**
     * Returns to Today. `unmet` names the conditions that are not *confirmed* to hold — which is not
     * the same as "failed": a condition nothing can confirm yet (no prerequisite engine, no
     * published content) is unmet, and the runner does not start on an assumption.
     */
    data class NotStartable(val unmet: Set<String>) : Revalidation
}

object RunnerRevalidation {
    /** Every entry condition must be confirmed; the rest are named, never assumed to hold. */
    fun atEntry(confirmed: Set<EntryCondition>): Revalidation {
        val unmet = EntryCondition.entries.filterNot { it in confirmed }.map { it.id }.toSet()
        return if (unmet.isEmpty()) Revalidation.Startable else Revalidation.NotStartable(unmet)
    }

    /** A stale paused task may not bypass current state; a fresh alternative is offered instead. */
    fun atResume(confirmed: Set<ResumeCondition>): Revalidation {
        val unmet = ResumeCondition.entries.filterNot { it in confirmed }.map { it.id }.toSet()
        return if (unmet.isEmpty()) Revalidation.Startable else Revalidation.NotStartable(unmet)
    }

    /**
     * What Today can confirm about its own primary task: only that it is still the selected one.
     * Prerequisites, the open need, content compatibility and local capability are other engines'
     * facts (12, 15); none of them exists yet, so none is confirmed and the runner does not start.
     * That is the truthful outcome today, and it will change by those engines supplying facts — not
     * by this function being loosened.
     */
    fun confirmedFromToday(view: TodayView): Set<EntryCondition> =
        if (view.primaryTask != null) setOf(EntryCondition.PLANNED_TASK_STILL_SELECTED) else emptySet()

    fun stateAtEntry(result: Revalidation): RunnerState = when (result) {
        Revalidation.Startable -> RunnerState.ORIENTATION
        is Revalidation.NotStartable -> RunnerState.BLOCKED_NOT_STARTABLE
    }

    fun stateAtResume(result: Revalidation): RunnerState = when (result) {
        Revalidation.Startable -> RunnerState.ACTIVE_WORK
        is Revalidation.NotStartable -> RunnerState.RESUME_INVALIDATED
    }
}

/**
 * What asking for help returned.
 *
 * There is no path that grants help the learner did not ask for, and no path that grants H3 or H4
 * before the learner has been told what it changes — the second outcome exists precisely so the
 * disclosure cannot be skipped.
 */
sealed interface AssistanceOutcome {
    data class Granted(val event: AssistanceEvent) : AssistanceOutcome

    /**
     * H3/H4 were requested and the consequence has not yet been acknowledged. The screen shows
     * [RunnerCopy.CONSEQUENCE_DISCLOSURE] and asks again.
     */
    data class ConsequenceDisclosureRequired(val level: AssistanceLevel) : AssistanceOutcome
}

object AssistancePolicy {

    /**
     * Help is always requestable in teaching and practice (`TRUX-v0` §8.1): there is no state in
     * which this refuses, because withholding help to force independence is forbidden. What it
     * guards is the order of events — H3/H4 only after the learner has been told, in measurement
     * terms, what the help changes.
     */
    fun request(
        level: AssistanceLevel,
        timing: AssistanceTiming,
        scope: AssistanceScope,
        source: AssistanceSource,
        consequenceAcknowledged: Boolean,
    ): AssistanceOutcome {
        // `TRUX-v0` states the rule without a scope condition — "before granting H3 or H4" — so it
        // is applied as written. Narrowing it to target-scoped help would be a reasonable reading,
        // and exactly the kind of quiet reinterpretation of an accepted contract this repo forbids.
        if (level.revealsTargetReasoning && !consequenceAcknowledged) {
            return AssistanceOutcome.ConsequenceDisclosureRequired(level)
        }
        return AssistanceOutcome.Granted(
            AssistanceEvent(level = level, timing = timing, scope = scope, source = source, requestedByUser = true)
        )
    }

    /**
     * What the runner does after an incorrect attempt: nothing but offer help. An auto-revealed
     * solution on the first error is forbidden, so this returns no event — only the levels the
     * learner may now ask for.
     */
    fun afterIncorrectAttempt(): List<AssistanceLevel> = AssistanceLevel.entries
}

/** The three pause classes and what each is allowed to claim (`TRUX-v0` §checkpoint_model). */
enum class PauseClass(val id: String, val durable: Boolean, val producesResumeContext: Boolean) {
    CHECKPOINT_PAUSE("checkpoint_pause", durable = true, producesResumeContext = true),
    /** Not durable, so it may never be presented as saved progress. */
    MID_SEGMENT_PAUSE("mid_segment_pause", durable = false, producesResumeContext = false),
    /** Durable and marked: a long gap may not silently continue as independent work. */
    HIGH_STAKES_PAUSE("high_stakes_pause", durable = true, producesResumeContext = true),
    ;

    val mayBePresentedAsSavedProgress: Boolean get() = durable
}

/** Where the next task comes from after a run. There is exactly one source, and it is not a list. */
enum class NextTaskSource(val id: String) {
    RECOMPUTED_PLANNER_SELECTION("recomputed_planner_selection"),
}

object RunnerFlow {

    /** `TRUX-v0` §transition: the runner never advances a cached local list. */
    val nextTaskSource: NextTaskSource = NextTaskSource.RECOMPUTED_PLANNER_SELECTION

    /**
     * Stopping is always available and always the same state: no penalty, no debt, no streak, no
     * catch-up obligation. There is deliberately no parameter here that could make it otherwise.
     */
    fun stop(): RunnerState = RunnerState.STOPPED_NO_PENALTY

    /**
     * What the resolve phase shows after an evaluation. A pending evaluation is its own state —
     * never a pass and never a fail — and neither result becomes a mastery claim here.
     */
    fun afterEvaluation(result: EvaluationResult): RunnerState = when (result) {
        is EvaluationResult.EvaluationPending -> RunnerState.EVALUATION_PENDING
        is EvaluationResult.Verified, is EvaluationResult.Provisional -> RunnerState.FEEDBACK_RESOLVED
    }

    /** The exit is reachable in one deliberate action from every state (`TRUX-v0`, `WFPX-v0`). */
    fun exitAvailable(state: RunnerState): Boolean = true

    /** The runner is a focused flow: the shell is suspended and the safe exit is required (`NSHX-v0`). */
    val surface: Surface = Surface.TaskRunnerFlow
}

/**
 * The runner's fixed sentences whose *meaning* is canonical even though their wording is 14's:
 * the consequence of H3/H4 in measurement terms, and what stopping means.
 */
object RunnerCopy {
    const val CONSEQUENCE_DISCLOSURE =
        "Bu yardım, bu denemenin neyi kanıtlayabileceğini değiştirir: deneme bağımsız kanıt sayılmaz " +
            "ve yetkinlik daha sonra görmediğin yeni bir varyantla doğrulanır."

    const val STOPPED =
        "Durdun. Bu bir kayıp değil: borç, seri ya da telafi yükümlülüğü oluşmaz."

    /** Only for a durable pause (11C): the place really was written. */
    const val CHECKPOINT_SAVED =
        "Kaldığın yer kaydedildi. Devam etmek istediğinde, devam etmeden önce yeniden kontrol edilir."

    /** A resume that cannot be confirmed (11C): not the learner's error, and nothing was erased. */
    const val RESUME_INVALIDATED =
        "Bu çalışma olduğu gibi devam ettirilemiyor. Bu bir hata ya da başarısızlık değil; " +
            "kaydedilmiş hiçbir şey silinmedi."

    const val ASSISTANCE_POLICY =
        "Yardım her zaman istenebilir. İpucu ve kavram yardımı denemeyi yardımlı yapar; kısmi ya da " +
            "tam çözüm istersen, bunun ne değiştirdiği önce söylenir."
}
