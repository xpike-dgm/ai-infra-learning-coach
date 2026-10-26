package coach.presentation

import coach.model.AnswerResult
import coach.model.CapabilityGap
import coach.model.ProvenanceOrigin
import coach.model.RunnableTask
import coach.model.TaskEntryFacts
import coach.model.TaskPurpose
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 16F (`E2EX-v0`): what the runner says about entry, an answer and the end of a run — never a verdict or a level. */
class TaskRunPresentationTest {

    private val turkish = java.util.Locale.forLanguageTag("tr")

    /** Words a run may never use about the learner or their standing. */
    private val forbidden = listOf("yanlış yaptın", "başarısız oldun", "puan", "%", "seviye", "ustalaştın", "mastered", "hata yaptın", "suç", "ceza", "hile")

    private fun run(purpose: TaskPurpose) = RunnableTask(
        plannedTaskId = 1, position = 0, planVersionId = 1, task = VersionedRef("task.computing.program_execution_model.teach", 1),
        title = "Programın yolculuğu", purpose = purpose, skill = VersionedRef("skill.computing.program_execution_model", 1),
        targetObjectives = listOf(VersionedRef("objective.computing.program_execution_model.explain_pipeline", 1)), plannedMinutes = 12,
        needKey = "n", lesson = emptyList(), items = listOf(VersionedRef("item.computing.program_execution_model.explain_pipeline.f01", 1)),
    )

    @Test
    fun `entry confirms exactly what the facts confirmed, and the rest stays unmet`() {
        val all = TaskRunPresentation.confirmed(TaskEntryFacts(true, true, true, true, true, run = run(TaskPurpose.TEACH)))
        assertEquals(EntryCondition.entries.toSet(), all)
        assertEquals(RunnerRevalidation.atEntry(all), Revalidation.Startable)
        val partial = TaskEntryFacts(true, false, true, true, false, CapabilityGap.NO_UNSEEN_ITEM)
        val unmet = (RunnerRevalidation.atEntry(TaskRunPresentation.confirmed(partial)) as Revalidation.NotStartable).unmet
        assertEquals(setOf(EntryCondition.HARD_PREREQUISITES_SATISFIED.id, EntryCondition.REQUIRED_LOCAL_CAPABILITY_AVAILABLE.id), unmet)
        assertEquals(TaskRunCopy.capabilityLines.getValue(CapabilityGap.NO_UNSEEN_ITEM), TaskRunPresentation.capabilityLine(partial))
        assertNull(TaskRunPresentation.capabilityLine(TaskEntryFacts(false, false, false, false, false)))
    }

    @Test
    fun `no result, capability or end-of-run line blames the learner or claims a level`() {
        val lines = TaskRunCopy.resultLines.values + TaskRunCopy.capabilityLines.values +
            listOf(TaskRunCopy.NOTHING_ANSWERED, TaskRunCopy.NO_MORE_QUESTIONS, TaskRunCopy.BLANK_IS_A_SKIP, TaskRunCopy.AFTER_REVEAL, TaskRunCopy.TEACH_NOTE)
        lines.forEach { line -> forbidden.forEach { word -> assertFalse(line.lowercase(turkish).contains(word), "\"$word\" in \"$line\"") } }
        // Not measurable is said not to be a wrong answer; every result has its own line.
        assertTrue(TaskRunPresentation.resultLine(AnswerResult.NOT_MEASURED).contains("yanlış sayılmaz"))
        assertEquals(AnswerResult.entries.toSet(), TaskRunCopy.resultLines.keys)
        assertEquals(CapabilityGap.entries.toSet(), TaskRunCopy.capabilityLines.keys)
    }

    @Test
    fun `orientation says what, why and how long, the help policy before any work, and asks provenance with nothing chosen`() {
        val teach = TaskRunPresentation.orientation(run(TaskPurpose.TEACH))
        assertEquals("yaklaşık 12 dk", teach.minutes)
        assertEquals(RunnerCopy.ASSISTANCE_POLICY, teach.assistancePolicy)
        assertEquals(TaskRunCopy.TEACH_NOTE, teach.teachNote)
        assertEquals(ProvenanceOrigin.entries.toSet(), teach.provenanceOptions.map { it.first }.toSet())
        assertNull(TaskRunPresentation.orientation(run(TaskPurpose.PRACTICE)).teachNote)
    }

    @Test
    fun `the accepted answer is shown with what seeing it changes`() {
        val text = TaskRunPresentation.revealed(listOf("B", "b"))
        assertTrue(text.startsWith("${TaskRunCopy.ACCEPTED_ANSWER}: B"))
        assertTrue(text.contains(TaskRunCopy.AFTER_REVEAL))
    }

    @Test
    fun `each stage is the TRUX-v0 state it is, and asking for help opens assistance`() {
        assertEquals(RunnerState.BLOCKED_NOT_STARTABLE, TaskRunStage.Blocked(emptySet(), null).state)
        assertEquals(RunnerState.ACTIVE_WORK, TaskRunStage.Question(1, "p", null, disclosure = false, notice = null).state)
        assertEquals(RunnerState.ASSISTANCE_OPEN, TaskRunStage.Question(1, "p", listOf("ders"), disclosure = false, notice = null).state)
        assertEquals(RunnerState.ASSISTANCE_OPEN, TaskRunStage.Question(1, "p", null, disclosure = true, notice = null).state)
        assertEquals(RunnerState.FEEDBACK_RESOLVED, TaskRunStage.Answered("Doğru.", null).state)
        assertEquals(RunnerState.SUBMITTING, TaskRunStage.Working.state)
    }
}
