package coach.presentation

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentIntent
import coach.model.AssessmentScope
import coach.model.IndependenceMode
import coach.model.NeedTrigger
import coach.model.PlanChange
import coach.model.PlanChangeKind
import coach.model.ProgramChangeReport
import coach.model.StateChange
import coach.model.StateChangeKind
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The report as the result's families (13E): every statement comes from a recorded change, nothing is said
 * that was not recorded, and an unchanged session says so plainly (`ASUX-v0` §13.4).
 */
class ProgramChangePresentationTest {

    private val loops = VersionedRef("skill.python.loops", 1)
    private val label: (VersionedRef) -> String = { if (it == loops) "Döngüler" else it.logicalId }

    private fun report(states: List<StateChange> = emptyList(), plans: List<PlanChange> = emptyList(), firstPlan: Boolean = false) =
        ProgramChangeReport(1, 2, states, plans, emptyList(), emptyList(), firstPlan)

    private fun change(kind: StateChangeKind) = StateChange(loops, kind, "a", "b")

    @Test
    fun `each change lands in its own family and nowhere else`() {
        val families = ProgramChangeResults.canonicalChanges(
            report(listOf(change(StateChangeKind.REMEDIATION_OPENED), change(StateChangeKind.RETENTION_REVALIDATED))), label)
        assertEquals(setOf(ResultFamily.PERSISTENT_TARGETED_GAPS, ResultFamily.RETENTION_REVALIDATED), families.keys)
        assertEquals(listOf("Döngüler: ${ProgramChangeCopy.stateTemplates.getValue(StateChangeKind.REMEDIATION_OPENED)}"),
            families.getValue(ResultFamily.PERSISTENT_TARGETED_GAPS))
        // The families feed the session result unchanged, and nothing is invented for the empty ones.
        assertFalse(ResultFamily.CONFIRMED_CAPABILITIES in families)
        assertFalse(ResultFamily.NOT_RELIABLY_MEASURED in families)
    }

    @Test
    fun `a weakness question is never filed or worded as a gap`() {
        val families = ProgramChangeResults.canonicalChanges(report(listOf(change(StateChangeKind.WEAKNESS_QUESTION_OPENED))), label)
        assertEquals(setOf(ResultFamily.VERIFICATION_NEEDED), families.keys)
        val sentence = ProgramChangeCopy.stateTemplates.getValue(StateChangeKind.WEAKNESS_QUESTION_OPENED)
        assertTrue("henüz bir eksik değil" in sentence)
    }

    @Test
    fun `plan changes are their own section and a first plan is not a change`() {
        val opened = PlanChange(PlanChangeKind.NEED_OPENED, "remediation_required:$loops", NeedTrigger.REMEDIATION_REQUIRED, listOf(loops))
        val dropped = PlanChange(PlanChangeKind.TASK_REMOVED, "new_learning:$loops", NeedTrigger.NEW_LEARNING, listOf(loops), "c1")
        val families = ProgramChangeResults.canonicalChanges(report(plans = listOf(opened, dropped)), label)
        assertEquals(listOf("Döngüler: Planda yeni bir ihtiyaç açıldı (hedefli onarım).", "Döngüler: Bu görev yeni planda yer almıyor."),
            families.getValue(ResultFamily.PLAN_CHANGES))
        assertNull(ProgramChangeResults.summary(report(plans = listOf(opened))))

        assertEquals(ProgramChangeCopy.FIRST_PLAN, ProgramChangeResults.summary(report(firstPlan = true)))
        assertEquals(ProgramChangeCopy.NOTHING_CHANGED, ProgramChangeResults.summary(report()))
        assertTrue(ProgramChangeResults.canonicalChanges(report(), label).isEmpty())
    }

    @Test
    fun `every state change has a sentence and every plan change kind has one`() {
        assertEquals(StateChangeKind.entries.toSet(), ProgramChangeCopy.stateTemplates.keys)
        PlanChangeKind.entries.forEach { kind ->
            assertTrue(ProgramChangeCopy.planTemplate(PlanChange(kind, "k", null, emptyList())).isNotBlank())
        }
        assertEquals("Planda yeni bir ihtiyaç açıldı (nedeni kayıtlı değil).",
            ProgramChangeCopy.planTemplate(PlanChange(PlanChangeKind.NEED_OPENED, "k", null, emptyList())))
    }

    @Test
    fun `no sentence scores, grades, blames or calls dropped work less important`() {
        val forbidden = listOf("%", "puan", "not ", "başarısız", "geçti", "kaldın", "borç", "daha az önemli", "geride", "unuttun", "seri")
        val sentences = ProgramChangeCopy.stateTemplates.values + PlanChangeKind.entries.map {
            ProgramChangeCopy.planTemplate(PlanChange(it, "k", NeedTrigger.NEW_LEARNING, emptyList()))
        } + listOf(ProgramChangeCopy.NOTHING_CHANGED, ProgramChangeCopy.FIRST_PLAN, ProgramChangeCopy.UNKNOWN_BEFORE)
        sentences.forEach { s -> forbidden.forEach { word -> assertFalse(word in s.lowercase(java.util.Locale.ROOT), "$word in: $s") } }
        // No combining dot from a locale-naive case transform (11B).
        sentences.forEach { assertFalse(Char(0x0307) in it, it) }
    }

    @Test
    fun `the session result takes the families as its canonical changes`() {
        val families = ProgramChangeResults.canonicalChanges(report(listOf(change(StateChangeKind.CAPABILITY_CONFIRMED))), label)
        val session = AssessmentSessionView(AssessmentScope.WEEKLY_BLUEPRINT, listOf(AssessmentIntent.CHECKPOINT),
            listOf(Block("block.1", listOf(Boundary("b1", BoundaryKind.ITEM, listOf(VersionedRef("item.b1", 1)))))),
            IndependenceMode.H0_REQUIRED, AllowedToolsPolicy(listOf("documentation")), SessionState.RESULT_READY,
            statuses = mapOf("b1" to BoundaryStatus.FROZEN))
        val result = SessionResults.of(session, canonicalChanges = families)
        assertEquals(families, result.families)
    }
}
