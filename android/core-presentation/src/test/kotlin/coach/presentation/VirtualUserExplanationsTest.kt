package coach.presentation

import coach.engines.ReplanEngine
import coach.engines.virtual.VirtualUsers
import coach.engines.virtual.VirtualUsers.needKey
import coach.engines.virtual.VirtualUsers.pointer
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PlannerExplanationFacts
import coach.model.RetentionAxis
import coach.model.VersionedRef
import coach.model.recordedReasonCodes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 3H S15 against real plans (12F): the explanations of the S02, S03, S04 and S07 virtual users' days,
 * built from the traces the real gate and planner wrote — never from a hand-made trace. Everything the
 * learner reads is the template fallback, so what is checked here is what they would read with no
 * model at all (invariant 16).
 */
class VirtualUserExplanationsTest {

    private val names = mapOf(pointer to "Pointer dereference", VirtualUsers.linkedList to "Bağlı liste")

    private fun explain(trace: PlanTrace, skillNames: Map<VersionedRef, String> = names) =
        PlannerExplanationPresentation.of(PlannerExplanationFacts.Readable(trace, skillNames))

    private fun PlannerExplanationView.allText(): List<String> =
        statements.map(ExplanationCopy::text) + notToday.mapNotNull { n -> n.reconsideration?.let { ExplanationCopy.text(it, n.whyNot.skills) } }

    private fun reentered(): PlanTrace {
        val s07 = VirtualUsers.s07()
        return ReplanEngine.composeReentry(
            fresh = s07.plan(), previousPlanVersionId = 3, lastPlannedStudyDay = "2026-09-01", stalePlannedTaskCount = 25,
            paused = ReplanEngine.Paused(s07.needs, emptyList(), 0), states = s07.learners.map { it.planning },
        )
    }

    private val days: Map<String, PlanTrace>
        get() = mapOf("S02" to VirtualUsers.s02().plan(), "S03" to VirtualUsers.s03().plan(),
            "S04" to VirtualUsers.s04().plan(), "S07" to reentered())

    @Test
    fun `S15 every explanation of a real plan says only what that plan's trace recorded`() {
        days.forEach { (id, trace) ->
            val view = explain(trace)
            assertEquals(ExplanationState.EXPLAINED, view.state, id)
            val recorded = trace.recordedReasonCodes()
            view.statements.forEach { s -> s.code?.let { assertTrue(it in recorded, "$id: $it was never recorded") } }
            assertEquals(trace.selected.size, view.today.size, id)
            // Every need that did not come is in exactly one entry; none is dropped by grouping.
            assertEquals(trace.needs.filter { it.selectedCandidateId == null }.map { it.needKey }.sorted(),
                view.notToday.flatMap { it.needKeys }.sorted(), id)
        }
    }

    @Test
    fun `S15 the S02 blocker comes only from the trace's Skill ref, and the waiting branch is not a failure`() {
        val trace = VirtualUsers.s02().plan()
        val waiting = explain(trace).notToday.single { needKey(NeedTrigger.NEW_LEARNING, VirtualUsers.linkedList) in it.needKeys }
        assertEquals("eligibility.blocked_critical_verification", waiting.whyNot.code)
        assertEquals(listOf(SkillMention(pointer, "Pointer dereference")), waiting.whyNot.skills)
        assertEquals(trace.candidates.single { it.candidateId == "linked-list" }.relatedSkills, waiting.whyNot.skills.map { it.ref })
        val text = ExplanationCopy.text(waiting.whyNot)
        assertTrue("Pointer dereference" in text, text)
        assertEquals(Reconsideration.WHEN_PREREQUISITE_READY, waiting.reconsideration)
        // The pointer verification is explained by its need and the dependent work it holds, never by its band.
        val verify = explain(trace).today.first()
        assertEquals("need.verification_due", verify.why.code)
        assertTrue(verify.supporting.any { it.code == "priority.blocks_next_ready_dependency" })
        assertTrue(explain(trace).statements.none { it.code?.startsWith("priority.p") == true })
    }

    @Test
    fun `S15 the S03 review is due, not forgotten, and it did not lock the dependent work`() {
        val view = explain(VirtualUsers.s03().plan())
        val review = view.today.single { it.why.code == "need.retention_review_due" }
        assertTrue("unuttuğun anlamına gelmez" in ExplanationCopy.text(review.why))
        val linked = view.today.single { it.why.code == "need.new_learning_available" }
        assertEquals(listOf("eligibility.ready_due_allowed"), linked.supporting.map { it.code })
        assertTrue(view.notToday.isEmpty())
        view.allText().forEach { assertFalse("unuttun" in it, it) }
    }

    @Test
    fun `S15 the S04 repair did not fit today, and nothing calls it less important`() {
        val view = explain(VirtualUsers.s04().plan())
        val repair = view.notToday.single { needKey(NeedTrigger.REMEDIATION_REQUIRED, VirtualUsers.cFunctions) in it.needKeys }
        assertEquals("capacity.deferred_not_enough_time", repair.whyNot.code)
        assertEquals(Reconsideration.NEXT_PLAN, repair.reconsideration)
        view.allText().forEach { assertFalse("öncelik" in it, "deferral for time worded as priority: $it") }
        assertTrue("borç veya başarısızlık değildir" in ExplanationCopy.text(repair.whyNot))
    }

    @Test
    fun `S15 the S07 return says absence is not failure or debt, and no sentence says how long`() {
        val view = explain(reentered())
        val plan = view.plan.mapNotNull { it.code }
        listOf("reentry.absence_not_failure", "reentry.absence_not_task_debt", "reentry.stale_plan_not_replayed").forEach {
            assertTrue(it in plan, it)
        }
        view.allText().forEach { text ->
            assertFalse(Regex("\\d").containsMatchIn(text), "a number in: $text")
            listOf("geri kaldın", "kaçırdın", "borcun").forEach { assertFalse(it in text, text) }
        }
        // Seventy-eight due Skills with no task are one entry, not seventy-eight rows of overdue work
        // (SRR-v0 §9.1): one sentence, every Skill kept, and no count said as a debt.
        val due = view.notToday.filter { it.need.code == "need.retention_review_due" }
        assertEquals(1, due.size, "the due inventory became a list")
        assertEquals(78, due.single().needKeys.size)
        assertEquals(78, due.single().skills.size)
        assertEquals(TraceFact.NO_TASK_FOR_NEED, due.single().whyNot.fact)
        assertTrue(ExplanationCopy.shortNames(due.single().skills).endsWith("ve 75 beceri daha"))
    }

    @Test
    fun `Today's rows for the virtual users' plans show trace facts, English only as the parallel track`() {
        val s02 = VirtualUsers.s02().plan()
        val english = s02.selected.single { it.track == VirtualUsers.ENGLISH_TRACK }
        assertEquals(needKey(NeedTrigger.PARALLEL_TRACK_DUE, VirtualUsers.englishErrors), english.needKey)
        // The retention state the planner read is RVR-v0's value, never a forgetting verdict.
        assertTrue(VirtualUsers.s03().learners.any { it.retention == RetentionAxis.REVIEW_DUE })
        val verify = explain(s02).today.first()
        assertFalse(verify.kept)
    }
}
