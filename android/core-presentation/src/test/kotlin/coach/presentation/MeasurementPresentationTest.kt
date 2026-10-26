package coach.presentation

import coach.model.AnswerResult
import coach.model.AnsweredItem
import coach.model.AssessmentIntent
import coach.model.AssessmentScope
import coach.model.IndependenceClass
import coach.model.Measurement
import coach.model.MeasurementKind
import coach.model.RunnableTask
import coach.model.TaskPurpose
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 16H (`ASRX-v0`): a measurement run as the learner reads it — never a score, a grade, a percentage or a pass/fail. */
class MeasurementPresentationTest {

    private val turkish = java.util.Locale.forLanguageTag("tr")
    private val forbidden = listOf("puan", "%", "seviye", "ustalaştın", "mastered", "geçtin", "kaldın", "başarısız oldun", "not:")
    private val a = VersionedRef("item.python.read_input.f01", 1)
    private val b = VersionedRef("item.python.read_input.f02", 1)

    private fun run(kind: MeasurementKind) = RunnableTask(
        plannedTaskId = 1, position = 0, planVersionId = 1, task = if (kind == MeasurementKind.DAILY_ASSESSMENT) VersionedRef("task.python.read_input.assess", 1) else null,
        title = "Ölçme", purpose = TaskPurpose.ASSESS, skill = VersionedRef("skill.python.read_input", 1), targetObjectives = emptyList(),
        plannedMinutes = 5, needKey = "verification_due:x", lesson = emptyList(), items = listOf(a),
        measurement = Measurement(kind, AssessmentIntent.VERIFICATION, sessionId = if (kind == MeasurementKind.DAILY_ASSESSMENT) null else 7,
            slotId = if (kind.isSlot) "slot-1" else null),
    )

    @Test
    fun `orientation names the session and what independence means, before any work`() {
        MeasurementKind.entries.forEach { kind ->
            val view = TaskRunPresentation.orientation(run(kind))
            assertEquals(listOf(MeasurementCopy.scope(kind), SessionCopy.INDEPENDENCE_DISCLOSURE, MeasurementCopy.NOT_A_VERDICT), view.measurement)
        }
        val practice = run(MeasurementKind.DAILY_ASSESSMENT).copy(measurement = null, purpose = TaskPurpose.PRACTICE)
        assertTrue(TaskRunPresentation.orientation(practice).measurement.isEmpty(), "a task that is not a measurement says nothing of one")
    }

    @Test
    fun `a skipped question leaves the session partial and is not incorrect, and a reported one is not measured`() {
        val answered = AnsweredItem(a, 1, AnswerResult.NOT_RIGHT, listOf(10), independence = IndependenceClass.INDEPENDENT)
        val partial = MeasurementPresentation.result(AssessmentScope.WEEKLY_BLUEPRINT, listOf(a, b), listOf(answered), skipped = setOf(b), contested = emptySet())
        assertTrue(partial.partial)
        assertEquals(listOf(NotReliablyMeasured.UNSUBMITTED_SLOT), partial.notReliablyMeasured)
        val reported = MeasurementPresentation.result(AssessmentScope.DAILY_MICRO, listOf(a), listOf(answered), emptySet(), contested = setOf(a))
        assertFalse(reported.partial)
        assertEquals(listOf(NotReliablyMeasured.CONTESTED_ITEM), reported.notReliablyMeasured)
        val helped = MeasurementPresentation.result(AssessmentScope.DAILY_MICRO, listOf(a),
            listOf(answered.copy(independence = IndependenceClass.ASSISTED)), emptySet(), emptySet())
        assertEquals(listOf(NotReliablyMeasured.ASSISTED_ATTEMPT), helped.notReliablyMeasured)
        val pending = MeasurementPresentation.result(AssessmentScope.DAILY_MICRO, listOf(a),
            listOf(AnsweredItem(a, 1, AnswerResult.NOT_MEASURED, emptyList(), pending = true)), emptySet(), emptySet())
        assertEquals(listOf(NotReliablyMeasured.PROVISIONAL_EVALUATION), pending.notReliablyMeasured)
    }

    @Test
    fun `the result says its state, what could not be measured and what the engines reported, and nothing else`() {
        val answered = AnsweredItem(a, 1, AnswerResult.RIGHT, listOf(10), independence = IndependenceClass.INDEPENDENT)
        val ready = MeasurementPresentation.result(AssessmentScope.DAILY_MICRO, listOf(a), listOf(answered), emptySet(), emptySet())
        assertEquals(listOf(MeasurementCopy.RESULT_READY, "motor satırı"), MeasurementPresentation.lines(ready, listOf("motor satırı")))
        val partial = MeasurementPresentation.result(AssessmentScope.DAILY_MICRO, listOf(a, b), listOf(answered), setOf(b), emptySet())
        val lines = MeasurementPresentation.lines(partial, emptyList())
        assertEquals(MeasurementCopy.RESULT_PARTIAL, lines.first())
        assertTrue(lines.any { HistoryCopy.notMeasuredLabels.getValue(NotReliablyMeasured.UNSUBMITTED_SLOT) in it })
        val all = lines + listOf(MeasurementCopy.NOT_A_VERDICT, MeasurementCopy.SKIP, MeasurementCopy.CONTEST, MeasurementCopy.FAST_PATH_OFFER,
            MeasurementCopy.FAST_PATH_REQUESTED, MeasurementCopy.FAST_PATH_NOTHING, MeasurementCopy.FAST_PATH_WITHDRAWN, MeasurementCopy.FAST_PATH_NOT_STARTABLE)
        all.forEach { line -> forbidden.forEach { word -> assertFalse(word in line.lowercase(turkish), "\"$word\" in: $line") } }
    }

    @Test
    fun `a measurement question may be left unanswered and an answered one reported, nothing else gains either`() {
        val question = TaskRunStage.Question(1, "soru", null, false, null, canSkip = true)
        assertEquals(RunnerState.ACTIVE_WORK, question.state, "skipping is ordinary work, not help or a fault")
        val answered = TaskRunStage.Answered("Doğru.", null, canContest = true)
        assertEquals(RunnerState.FEEDBACK_RESOLVED, answered.state)
        assertFalse(TaskRunStage.Question(1, "soru", null, false, null).canSkip)
        assertFalse(TaskRunStage.Answered("Doğru.", null).canContest)
    }

    @Test
    fun `a corrected history row says why it counts as it does`() {
        assertEquals("itiraz edildi: soru gözden geçirilene kadar sayılmıyor", HistoryCopy.disposition("contested", "learner_reported_item_problem"))
        assertEquals("ön koşul eksik göründüğü için hedefine karşı sayılmadı", HistoryCopy.disposition("invalidated", "prerequisite_contaminated"))
        assertEquals("geçersiz sayıldı (validator_rejected)", HistoryCopy.disposition("invalidated", "validator_rejected"))
    }
}
