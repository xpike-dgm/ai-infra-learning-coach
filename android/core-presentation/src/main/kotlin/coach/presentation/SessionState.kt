package coach.presentation

import coach.model.CheckpointKind
import coach.model.StoredCheckpoint
import coach.model.StudyTimestamp

/**
 * Session state (11C) as pure functions: when a pause is durable, what a resume can confirm from a
 * stored checkpoint, and the emergent working session with its ended reasons (`TRUX-v0` §4, §7).
 *
 * Like the runner, none of this is an authority. A pause is not an outcome, a resume that cannot be
 * confirmed is not a failure, and a session is history — never a score, a grade or a completion
 * percentage.
 */

// ---------------------------------------------------------------- pause

/** The four conditions that make a boundary a safe checkpoint (`TRUX-v0` §7.1), in its order. */
enum class SafeCheckpointCondition(val id: String) {
    SEGMENT_PEDAGOGICALLY_MEANINGFUL_ALONE("segment_pedagogically_meaningful_alone"),
    ARTIFACT_AND_RUNNER_STATE_PERSISTABLE_HONESTLY("artifact_and_runner_state_persistable_honestly"),
    NO_PARTIALLY_EXPOSED_SOLUTION_STATE("no_partially_exposed_solution_state"),
    NO_HALF_EVALUATED_INDEPENDENT_ATTEMPT("no_half_evaluated_independent_attempt"),
}

/** What pausing here amounts to. */
sealed interface PauseDecision {
    val pauseClass: PauseClass

    /** Written as one `resume_checkpoint` row. Only a durable class can be here. */
    data class Durable(val kind: CheckpointKind) : PauseDecision {
        override val pauseClass: PauseClass
            get() = when (kind) {
                CheckpointKind.CHECKPOINT_PAUSE -> PauseClass.CHECKPOINT_PAUSE
                CheckpointKind.HIGH_STAKES_PAUSE -> PauseClass.HIGH_STAKES_PAUSE
            }
    }

    /**
     * A mid-segment pause: nothing is written, and nothing may later claim it was. `unmet` names the
     * safe-checkpoint conditions that were not confirmed — not "failed", for the same reason entry
     * says `unmet`.
     */
    data class NotDurable(val unmet: Set<String>) : PauseDecision {
        override val pauseClass: PauseClass get() = PauseClass.MID_SEGMENT_PAUSE
    }
}

object PausePolicy {

    /**
     * High-stakes work (an independent/H0 attempt, a diagnostic, a verification) pauses durably and
     * **marked**, wherever it stops (`TRUX-v0` §7.2). Any other work pauses durably only at a
     * boundary whose four safe-checkpoint conditions are all confirmed; otherwise the pause is
     * mid-segment. Nothing unconfirmed is assumed to hold — the same rule as entry.
     *
     * Whether a boundary is pedagogically meaningful and whether artifact state can be persisted are
     * content and artifact facts (11D), so today no ordinary pause is durable. That is the truthful
     * outcome, and it changes by those facts arriving, not by this function being loosened.
     */
    fun classify(highStakesWork: Boolean, confirmed: Set<SafeCheckpointCondition>): PauseDecision {
        if (highStakesWork) return PauseDecision.Durable(CheckpointKind.HIGH_STAKES_PAUSE)
        val unmet = SafeCheckpointCondition.entries.filterNot { it in confirmed }.map { it.id }.toSet()
        return if (unmet.isEmpty()) PauseDecision.Durable(CheckpointKind.CHECKPOINT_PAUSE) else PauseDecision.NotDurable(unmet)
    }

    /**
     * Only a durable pause becomes `checkpoint_paused` — the state whose label says the place was
     * saved. A mid-segment pause leaves the runner where it was: it is transient UI state and may
     * never be shown as saved progress.
     */
    fun stateAfter(decision: PauseDecision, current: RunnerState): RunnerState = when (decision) {
        is PauseDecision.Durable -> RunnerState.CHECKPOINT_PAUSED
        is PauseDecision.NotDurable -> current
    }
}

// ---------------------------------------------------------------- resume

object ResumeConfirmation {

    /**
     * What a stored checkpoint can confirm on its own — and it is at most two of the five resume
     * conditions:
     *
     * - `runner_and_artifact_state_intact`, when the stored context decodes exactly **and** it
     *   points at no artifact state. Artifact storage is 11D's, so a referenced artifact cannot yet
     *   be checked and is not assumed intact.
     * - `high_stakes_gap_integrity_acceptable`, for an ordinary `checkpoint_pause` only. `TRUX-v0`
     *   scopes that condition to high-stakes work, and the pause kind is a recorded fact, not an
     *   assumption. For a high-stakes pause no gap policy has been accepted (13, calibrated in 18),
     *   so the gap is never confirmed acceptable and the work is never silently continued as
     *   independent.
     *
     * Content compatibility, prerequisites and the open need are other engines' facts (12, 11D) and
     * none exists yet, so no checkpoint is resumable today — it re-enters later as the planner's
     * `continue_learning` need, never as tomorrow's automatic first task.
     */
    fun fromCheckpoint(stored: StoredCheckpoint?): Set<ResumeCondition> {
        val context = stored?.context ?: return emptySet()
        return buildSet {
            if (context.artifactStateRef == null) add(ResumeCondition.RUNNER_AND_ARTIFACT_STATE_INTACT)
            if (context.kind == CheckpointKind.CHECKPOINT_PAUSE) add(ResumeCondition.HIGH_STAKES_GAP_INTEGRITY_ACCEPTABLE)
        }
    }
}

// ---------------------------------------------------------------- working session

/** How a session began (`TRUX-v0` §4), in its order. */
enum class SessionEntrySource(val id: String) {
    TODAY_PRIMARY_ACTION("today_primary_action"),
    TODAY_QUEUE_ROW("today_queue_row"),
    ENTITY_CONTEXT("entity_context"),
    RESUME_ENTRY("resume_entry"),
}

/**
 * Why a session ended (`TRUX-v0` §4), in its order. None of them is a verdict: `plan_exhausted`
 * does not mean the day succeeded and `user_stopped` does not mean it failed, so there is no
 * success flag, tone or score attached to any of them.
 */
enum class SessionEndedReason(val id: String) {
    USER_STOPPED("user_stopped"),
    PLAN_EXHAUSTED("plan_exhausted"),
    CAPACITY_REACHED("capacity_reached"),
    INTERRUPTED("interrupted"),
    RECOVERY_REQUIRED("recovery_required"),
}

/** One focused task run: which task, and the workflow state it was left in — never a result. */
data class TaskRun(val sourceTaskId: String, val lastState: RunnerState)

/**
 * The emergent sequence of task runs the learner chose to do. It is not a container the planner
 * fills and not a unit of achievement: there is no required count, duration or percentage, and no
 * field could hold one.
 *
 * It is **not stored.** `DDM-v0` names no working-session entity and 10D forbade inventing one;
 * what a session did is already in the truth it produced (attempts, checkpoints). Durable session
 * history belongs to 16B. So a session lives as long as the process: a killed process resumes no
 * session, and a later resume starts a new one from `resume_entry`.
 */
class WorkingSession internal constructor(
    val sessionId: Long,
    val startedAt: StudyTimestamp,
    val entrySource: SessionEntrySource,
    val taskRuns: List<TaskRun>,
    /** The planner's capacity context (12); nothing supplies it yet, and nothing is derived here. */
    val remainingCapacityContextRef: String?,
    val endedReason: SessionEndedReason?,
) {
    val ended: Boolean get() = endedReason != null
}

/** Something that happened while a session was open. */
sealed interface SessionEvent {
    /** The learner used the exit. */
    data object LearnerExited : SessionEvent

    /**
     * After a run, the planner recomputed its selection (`TRUX-v0` §6.6). `capacityReached` is the
     * planner's own verdict, reported and never derived here (11A).
     */
    data class SelectionRecomputed(val selectionEmpty: Boolean, val capacityReached: Boolean) : SessionEvent

    /** The focused flow went away without the learner, the planner or the store ending it. */
    data object FlowLeftWithoutExit : SessionEvent

    /** Store integrity became uncertain; recovery supersedes normal work (`APHX-v0`). */
    data object StoreNeedsRecovery : SessionEvent
}

object WorkingSessions {

    /**
     * A session begins with the first run that actually starts. A blocked entry is not a run, so it
     * starts no session — which, while nothing is startable, means no session starts at all.
     */
    fun startIfRunStarted(
        entry: Revalidation,
        sessionId: Long,
        at: StudyTimestamp,
        source: SessionEntrySource,
        sourceTaskId: String,
    ): WorkingSession? = when (entry) {
        is Revalidation.NotStartable -> null
        Revalidation.Startable -> WorkingSession(
            sessionId = sessionId,
            startedAt = at,
            entrySource = source,
            taskRuns = listOf(TaskRun(sourceTaskId, RunnerState.ORIENTATION)),
            remainingCapacityContextRef = null,
            endedReason = null,
        )
    }

    /** Continuing adds a run; an ended session is history and does not grow. */
    fun withRun(session: WorkingSession, run: TaskRun): WorkingSession {
        require(!session.ended) { "an ended session is history; a new run starts a new session" }
        return WorkingSession(
            session.sessionId, session.startedAt, session.entrySource, session.taskRuns + run,
            session.remainingCapacityContextRef, null,
        )
    }

    /** The reason an event ends the session, or `null` when it does not end it. */
    fun endedReason(event: SessionEvent): SessionEndedReason? = when (event) {
        SessionEvent.LearnerExited -> SessionEndedReason.USER_STOPPED
        is SessionEvent.SelectionRecomputed -> when {
            !event.selectionEmpty -> null
            event.capacityReached -> SessionEndedReason.CAPACITY_REACHED
            else -> SessionEndedReason.PLAN_EXHAUSTED
        }
        SessionEvent.FlowLeftWithoutExit -> SessionEndedReason.INTERRUPTED
        SessionEvent.StoreNeedsRecovery -> SessionEndedReason.RECOVERY_REQUIRED
    }

    /**
     * Applies an event. A session ends once: the first ending event is its reason, and a later one
     * cannot rewrite that history.
     */
    fun on(session: WorkingSession, event: SessionEvent): WorkingSession {
        if (session.ended) return session
        val reason = endedReason(event) ?: return session
        return WorkingSession(
            session.sessionId, session.startedAt, session.entrySource, session.taskRuns,
            session.remainingCapacityContextRef, reason,
        )
    }
}
