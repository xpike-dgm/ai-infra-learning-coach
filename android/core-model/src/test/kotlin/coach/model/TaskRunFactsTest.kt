package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 16F (`E2EX-v0`): what a task run works from, and how an answer may be worded. */
class TaskRunFactsTest {

    private val skill = VersionedRef("skill.computing.program_execution_model", 1)
    private val objective = VersionedRef("objective.computing.program_execution_model.explain_pipeline", 1)

    private fun run() = RunnableTask(
        plannedTaskId = 7, position = 0, planVersionId = 3, task = VersionedRef("task.computing.program_execution_model.teach", 1),
        title = "Ders", purpose = TaskPurpose.TEACH, skill = skill, targetObjectives = listOf(objective), plannedMinutes = 12,
        needKey = "new_learning:$skill", lesson = emptyList(), items = listOf(VersionedRef("item.computing.program_execution_model.explain_pipeline.f01", 1)),
    )

    @Test
    fun `an answer is right, partly right, not right or not measurable, and nothing else`() {
        assertEquals(AnswerResult.RIGHT, AnswerResult.of(listOf(OutcomeSignal.MET)))
        assertEquals(AnswerResult.NOT_RIGHT, AnswerResult.of(listOf(OutcomeSignal.NOT_MET)))
        assertEquals(AnswerResult.PARTLY, AnswerResult.of(listOf(OutcomeSignal.PARTIALLY_MET)))
        assertEquals(AnswerResult.PARTLY, AnswerResult.of(listOf(OutcomeSignal.MET, OutcomeSignal.NOT_MET)))
        // Not measurable is never a wrong answer, and nothing measured is not measurable.
        assertEquals(AnswerResult.NOT_MEASURED, AnswerResult.of(listOf(OutcomeSignal.NOT_RELIABLY_MEASURED)))
        assertEquals(AnswerResult.NOT_MEASURED, AnswerResult.of(emptyList()))
    }

    @Test
    fun `a task runs exactly when every entry condition is confirmed, and only a checked capability names its gap`() {
        assertTrue(TaskEntryFacts(true, true, true, true, true, run = run()).startable)
        assertFailsWith<IllegalArgumentException> { TaskEntryFacts(true, true, true, true, true) }
        assertFailsWith<IllegalArgumentException> { TaskEntryFacts(true, false, true, true, true, run = run()) }
        assertFailsWith<IllegalArgumentException> { TaskEntryFacts(true, true, true, true, true, CapabilityGap.NO_UNSEEN_ITEM, run()) }
        val unchecked = TaskEntryFacts(false, false, false, false, false)
        assertFalse(unchecked.startable)
        assertNull(unchecked.capabilityGap)
        assertEquals(CapabilityGap.NO_ITEM_THIS_APP_CAN_EVALUATE, TaskEntryFacts(true, true, true, true, false, CapabilityGap.NO_ITEM_THIS_APP_CAN_EVALUATE).capabilityGap)
    }

    @Test
    fun `a planned candidate names the authored task version it is, and nothing else is read as one`() {
        assertEquals(VersionedRef("task.computing.program_execution_model.teach", 2),
            AuthoredTask.refOfCandidate("authored:task.computing.program_execution_model.teach@v2:new_learning:skill.x@v1"))
        assertNull(AuthoredTask.refOfCandidate("weekly:2026-W40:slot-1"))
        assertNull(AuthoredTask.refOfCandidate("authored:task.x"))
    }

    @Test
    fun `the lesson a run shows is the course's own explanation, never an alternative`() {
        val alternative = ExplanationVariant(VersionedRef("explanation.computing.alt", 1), objective, ExplanationForm.entries.first { it != ExplanationForm.CANONICAL && it != ExplanationForm.MISCONCEPTION_CONTRAST }, AssistanceLevel.H2, "metin")
        assertFailsWith<IllegalArgumentException> { run().copy(lesson = listOf(alternative)) }
    }

    // 16G (`HLPX-v0`)

    @Test
    fun `a pending evaluation is recorded with no evidence, and an answer is either recorded or says why nothing was`() {
        val item = VersionedRef("item.computing.program_execution_model.explain_pipeline.f01", 1)
        val pending = AnsweredItem(item, 1, AnswerResult.NOT_MEASURED, emptyList(), pending = true)
        assertTrue(pending.pending)
        assertFailsWith<IllegalArgumentException> { AnsweredItem(item, 1, AnswerResult.NOT_MEASURED, listOf(9), pending = true) }
        assertEquals(ComprehensionOffer.NotOffered, RunAnswer.Recorded(pending).offer, "nothing follows unless the run says so")
        assertEquals(listOf(RunItemKind.ANSWER_KEY, RunItemKind.CODE_TESTS, RunItemKind.RUBRIC), RunItemKind.entries)
    }

    // 16H (`ASRX-v0`)

    @Test
    fun `a measurement names its session, a slot its slot, and only a slot or a diagnostic runs without an authored task`() {
        assertEquals(AssessmentScope.WEEKLY_BLUEPRINT, Measurement(MeasurementKind.WEEKLY_SLOT, AssessmentIntent.VERIFICATION, 3, "slot-1").scope)
        assertEquals(AssessmentScope.DAILY_MICRO, Measurement(MeasurementKind.DIAGNOSTIC, AssessmentIntent.MASTERY_EVIDENCE, 3).scope)
        assertFailsWith<IllegalArgumentException> { Measurement(MeasurementKind.WEEKLY_SLOT, AssessmentIntent.VERIFICATION, 3) }
        assertFailsWith<IllegalArgumentException> { Measurement(MeasurementKind.DIAGNOSTIC, AssessmentIntent.MASTERY_EVIDENCE, 3, "slot-1") }
        assertFailsWith<IllegalArgumentException> { Measurement(MeasurementKind.MONTHLY_SLOT, AssessmentIntent.VERIFICATION, null, "slot-1") }
        Measurement(MeasurementKind.DAILY_ASSESSMENT, AssessmentIntent.VERIFICATION, null)
        val slot = run().copy(task = null, lesson = emptyList(), measurement = Measurement(MeasurementKind.MONTHLY_SLOT, AssessmentIntent.VERIFICATION, 4, "slot-2"))
        assertNull(slot.task)
        assertFailsWith<IllegalArgumentException> { run().copy(task = null) }
        assertFailsWith<IllegalArgumentException> { run().copy(task = null, measurement = Measurement(MeasurementKind.DAILY_ASSESSMENT, AssessmentIntent.VERIFICATION, null)) }
        val lesson = ExplanationVariant(VersionedRef("explanation.computing.canonical", 1), objective, ExplanationForm.CANONICAL, null, "metin")
        assertFailsWith<IllegalArgumentException> { slot.copy(lesson = listOf(lesson)) }
        assertEquals(setOf(MeasurementKind.WEEKLY_SLOT, MeasurementKind.MONTHLY_SLOT), MeasurementKind.entries.filter { it.isSlot }.toSet())
    }
}

