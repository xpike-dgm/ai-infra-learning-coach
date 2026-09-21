package coach.presentation

import coach.model.CheckpointKind
import coach.model.ResumeContext
import coach.model.StoredCheckpoint
import coach.model.StudyTimestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SessionStateTest {

    private val at = StudyTimestamp(1_789_000_000_000, "2026-09-19", 3 * 3600)

    // ---------------------------------------------------------------- vocabularies from TRUX-v0

    @Test
    fun `vocabularies are TRUX-v0's in its order`() {
        assertEquals(
            listOf("segment_pedagogically_meaningful_alone", "artifact_and_runner_state_persistable_honestly",
                "no_partially_exposed_solution_state", "no_half_evaluated_independent_attempt"),
            SafeCheckpointCondition.entries.map { it.id },
        )
        assertEquals(
            listOf("today_primary_action", "today_queue_row", "entity_context", "resume_entry"),
            SessionEntrySource.entries.map { it.id },
        )
        assertEquals(
            listOf("user_stopped", "plan_exhausted", "capacity_reached", "interrupted", "recovery_required"),
            SessionEndedReason.entries.map { it.id },
        )
    }

    @Test
    fun `stored checkpoint kinds are exactly the durable pause classes`() {
        assertEquals(
            PauseClass.entries.filter { it.durable }.map { it.id },
            CheckpointKind.entries.map { it.id },
        )
    }

    // ---------------------------------------------------------------- pause

    @Test
    fun `an ordinary pause is durable only when every safe-checkpoint condition is confirmed`() {
        val all = SafeCheckpointCondition.entries.toSet()
        assertEquals(PauseDecision.Durable(CheckpointKind.CHECKPOINT_PAUSE), PausePolicy.classify(false, all))
        all.forEach { missing ->
            val decision = PausePolicy.classify(false, all - missing)
            assertIs<PauseDecision.NotDurable>(decision)
            assertEquals(setOf(missing.id), decision.unmet)
            assertEquals(PauseClass.MID_SEGMENT_PAUSE, decision.pauseClass)
        }
    }

    @Test
    fun `nothing unconfirmed is assumed, so today no ordinary pause is durable`() {
        val decision = PausePolicy.classify(false, emptySet())
        assertIs<PauseDecision.NotDurable>(decision)
        assertEquals(SafeCheckpointCondition.entries.map { it.id }.toSet(), decision.unmet)
    }

    @Test
    fun `high-stakes work pauses durably and marked wherever it stops`() {
        val decision = PausePolicy.classify(true, emptySet())
        assertEquals(PauseDecision.Durable(CheckpointKind.HIGH_STAKES_PAUSE), decision)
        assertEquals(PauseClass.HIGH_STAKES_PAUSE, decision.pauseClass)
    }

    @Test
    fun `only a durable pause is shown as saved`() {
        val durable = PausePolicy.classify(false, SafeCheckpointCondition.entries.toSet())
        assertEquals(RunnerState.CHECKPOINT_PAUSED, PausePolicy.stateAfter(durable, RunnerState.ACTIVE_WORK))
        assertTrue(durable.pauseClass.mayBePresentedAsSavedProgress)

        val transient = PausePolicy.classify(false, emptySet())
        assertEquals(RunnerState.ACTIVE_WORK, PausePolicy.stateAfter(transient, RunnerState.ACTIVE_WORK))
        assertFalse(transient.pauseClass.mayBePresentedAsSavedProgress)
    }

    // ---------------------------------------------------------------- resume

    private fun stored(kind: CheckpointKind = CheckpointKind.CHECKPOINT_PAUSE, artifact: String? = null, decodes: Boolean = true) =
        StoredCheckpoint(
            checkpointRowId = 7,
            recordedAt = at,
            context = if (!decodes) null else ResumeContext(
                kind, "need.skill.python.loops@v1", "42", "checkpoint.after_example",
                listOf("seg.a"), listOf("seg.b"), artifact,
            ),
        )

    @Test
    fun `an intact ordinary checkpoint confirms state intact and the high-stakes condition, nothing more`() {
        assertEquals(
            setOf(ResumeCondition.RUNNER_AND_ARTIFACT_STATE_INTACT, ResumeCondition.HIGH_STAKES_GAP_INTEGRITY_ACCEPTABLE),
            ResumeConfirmation.fromCheckpoint(stored()),
        )
    }

    @Test
    fun `a checkpoint that does not decode or does not exist confirms nothing`() {
        assertEquals(emptySet(), ResumeConfirmation.fromCheckpoint(stored(decodes = false)))
        assertEquals(emptySet(), ResumeConfirmation.fromCheckpoint(null))
    }

    @Test
    fun `referenced artifact state is not assumed intact while nothing can check it`() {
        assertFalse(ResumeCondition.RUNNER_AND_ARTIFACT_STATE_INTACT in
            ResumeConfirmation.fromCheckpoint(stored(artifact = "artifact://draft/7")))
    }

    @Test
    fun `a high-stakes gap is never confirmed acceptable without an accepted gap policy`() {
        assertFalse(ResumeCondition.HIGH_STAKES_GAP_INTEGRITY_ACCEPTABLE in
            ResumeConfirmation.fromCheckpoint(stored(kind = CheckpointKind.HIGH_STAKES_PAUSE)))
    }

    @Test
    fun `no checkpoint is resumable today and an invalidated resume is not a failure`() {
        listOf(stored(), stored(kind = CheckpointKind.HIGH_STAKES_PAUSE), stored(decodes = false)).forEach {
            val result = RunnerRevalidation.atResume(ResumeConfirmation.fromCheckpoint(it))
            assertIs<Revalidation.NotStartable>(result)
            assertTrue(ResumeCondition.CONTENT_VERSION_COMPATIBLE.id in result.unmet)
            assertEquals(RunnerState.RESUME_INVALIDATED, RunnerRevalidation.stateAtResume(result))
            assertTrue(RunnerState.RESUME_INVALIDATED.tone != Tone.SYSTEM_FAULT)
        }
    }

    // ---------------------------------------------------------------- working session

    private fun started(source: SessionEntrySource = SessionEntrySource.TODAY_PRIMARY_ACTION) =
        WorkingSessions.startIfRunStarted(Revalidation.Startable, 1, at, source, "42")!!

    @Test
    fun `a blocked entry starts no session`() {
        assertNull(WorkingSessions.startIfRunStarted(Revalidation.NotStartable(setOf("x")), 1, at,
            SessionEntrySource.TODAY_PRIMARY_ACTION, "42"))
    }

    @Test
    fun `a started session holds its first run and its entry source`() {
        val session = started(SessionEntrySource.RESUME_ENTRY)
        assertEquals(SessionEntrySource.RESUME_ENTRY, session.entrySource)
        assertEquals(listOf(TaskRun("42", RunnerState.ORIENTATION)), session.taskRuns)
        assertNull(session.remainingCapacityContextRef)
        assertFalse(session.ended)
    }

    @Test
    fun `each event ends a session with its own reason, and none is a verdict`() {
        assertEquals(SessionEndedReason.USER_STOPPED, WorkingSessions.endedReason(SessionEvent.LearnerExited))
        assertEquals(SessionEndedReason.PLAN_EXHAUSTED,
            WorkingSessions.endedReason(SessionEvent.SelectionRecomputed(selectionEmpty = true, capacityReached = false)))
        assertEquals(SessionEndedReason.CAPACITY_REACHED,
            WorkingSessions.endedReason(SessionEvent.SelectionRecomputed(selectionEmpty = true, capacityReached = true)))
        assertEquals(SessionEndedReason.INTERRUPTED, WorkingSessions.endedReason(SessionEvent.FlowLeftWithoutExit))
        assertEquals(SessionEndedReason.RECOVERY_REQUIRED, WorkingSessions.endedReason(SessionEvent.StoreNeedsRecovery))
    }

    @Test
    fun `a non-empty recomputed selection continues the session`() {
        val session = started()
        assertNull(WorkingSessions.endedReason(SessionEvent.SelectionRecomputed(selectionEmpty = false, capacityReached = true)))
        assertSame(session, WorkingSessions.on(session, SessionEvent.SelectionRecomputed(false, false)))
    }

    @Test
    fun `a session ends once and its history cannot be rewritten or extended`() {
        val stopped = WorkingSessions.on(started(), SessionEvent.LearnerExited)
        assertEquals(SessionEndedReason.USER_STOPPED, stopped.endedReason)
        assertEquals(SessionEndedReason.USER_STOPPED,
            WorkingSessions.on(stopped, SessionEvent.StoreNeedsRecovery).endedReason)
        assertFailsWith<IllegalArgumentException> {
            WorkingSessions.withRun(stopped, TaskRun("43", RunnerState.ORIENTATION))
        }
    }

    @Test
    fun `continuing adds a run without ending the session`() {
        val next = WorkingSessions.withRun(started(), TaskRun("43", RunnerState.ORIENTATION))
        assertEquals(listOf("42", "43"), next.taskRuns.map { it.sourceTaskId })
        assertFalse(next.ended)
    }

    @Test
    fun `a session can hold no score, grade, percentage, duration or required count`() {
        val forbidden = listOf("score", "grade", "percent", "duration", "elapsed", "minutes", "required", "streak", "success")
        // Compared case-insensitively rather than case-transformed: this repo has no locale-naive casing.
        val fields = WorkingSession::class.java.declaredFields.map { it.name } +
            TaskRun::class.java.declaredFields.map { it.name }
        forbidden.forEach { word ->
            assertTrue(fields.none { it.contains(word, ignoreCase = true) }, "a session field names '$word': $fields")
        }
    }
}
