package coach.presentation

import coach.model.AssessmentBlueprint
import coach.model.AssessmentBlueprintResult
import coach.model.AssessmentBlueprintSlot
import coach.model.AssessmentIntent
import coach.model.AssessmentScope
import coach.model.BlueprintSessionStatus
import coach.model.Criticality
import coach.model.IndependenceMode
import coach.model.LifecycleStatus
import coach.model.MonthlyRole
import coach.model.NeedTrigger
import coach.model.PriorityBand
import coach.model.SlotStatus
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MonthlyAssessmentSessionTest {

    private fun slot(id: String, role: MonthlyRole, n: Int, tools: List<String>) = AssessmentBlueprintSlot(
        slotId = id, role = role, needKey = "need:$n", trigger = NeedTrigger.VERIFICATION_DUE,
        targetSkill = VersionedRef("skill.test.s$n", 1), criticality = Criticality.REQUIRED, band = PriorityBand.P1,
        sourceStateRefs = emptyList(), track = null, status = SlotStatus.READY, item = VersionedRef("item.test.i$n", 1),
        targetObjectives = listOf(VersionedRef("objective.test.o$n", 1)), evidenceType = "code_reading",
        variantFamilyId = "fam.$n", expectedActiveMinutes = 30, itemLifecycle = LifecycleStatus.TRUSTED, allowedTools = tools,
    )

    private val blueprint = AssessmentBlueprint(
        scope = AssessmentScope.MONTHLY_CAPABILITY,
        cycleId = "2026-10", studyDay = "2026-10-01", curriculumVersion = 1, truthWatermark = 1, policyVersion = "MCA-v0",
        evaluatorAvailable = false, recentSince = "2026-09-01",
        slots = listOf(
            slot("slot-1", MonthlyRole.DELAYED_RETENTION_SAMPLING, 1, listOf("compiler", "terminal")),
            slot("slot-2", MonthlyRole.PERSISTENT_WEAKNESS_OR_VERIFICATION, 2, listOf("compiler")),
            slot("slot-3", MonthlyRole.INTEGRATED_APPLICATION, 3, listOf("compiler", "debugger")),
        ),
        exclusions = emptyList(), reasonCodes = emptyList(), priorSessionId = 9,
    )

    @Test
    fun `the month runs in the same interior, blocked in its own selection order`() {
        val view = BlueprintSessionPresentation.view(blueprint)
        assertEquals(AssessmentScope.MONTHLY_CAPABILITY, view.scope)
        assertEquals(IndependenceMode.H0_REQUIRED, view.independenceMode)
        assertEquals(listOf(listOf("slot-2"), listOf("slot-1"), listOf("slot-3")), view.blocks.map { b -> b.boundaries.map { it.id } })
        assertEquals(listOf(AssessmentIntent.VERIFICATION, AssessmentIntent.INTEGRATION_CHECK), view.intents)
        assertEquals(listOf("compiler"), view.allowedTools.allowed)
        assertEquals(SessionState.SESSION_ORIENTATION, view.state)
    }

    @Test
    fun `a monthly result claims only what engines reported, and its lists are not a verdict`() {
        val view = BlueprintSessionPresentation.view(blueprint)
        val result = BlueprintSessionPresentation.result(AssessmentBlueprintResult(1, "2026-10", BlueprintSessionStatus.COMPLETE,
            listOf("slot-1", "slot-2", "slot-3"), emptyList(), listOf(1, 2, 3), listOf(1, 2, 3),
            listOf(VersionedRef("objective.test.o1", 1)), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
            emptyList(), emptyList(), emptyList(), "MCA-v0",
            revalidatedRetentionSkills = listOf(VersionedRef("skill.test.s1", 1)),
            integratedEvidenceObjectives = listOf(VersionedRef("objective.test.o3", 1)),
        ), view)
        // Revalidation and integration lists are evidence; with no engine report there is no state change to show.
        assertTrue(result.families.isEmpty())
        assertTrue(result.notReliablyMeasured.isEmpty())
    }

    @Test
    fun `a month's status is described in its own words, never judged`() {
        BlueprintSessionStatus.entries.forEach { status ->
            val text = BlueprintSessionPresentation.statusText(AssessmentScope.MONTHLY_CAPABILITY, status)
            listOf("başarısız oldun", "kaldın", "geçtin", "puan", "%", "final").forEach { word -> assertTrue(word !in text, "$status: $text") }
        }
        assertTrue("borç" in BlueprintSessionPresentation.statusText(AssessmentScope.MONTHLY_CAPABILITY, BlueprintSessionStatus.PARTIAL))
        assertTrue("ay" in BlueprintSessionPresentation.statusText(AssessmentScope.MONTHLY_CAPABILITY, BlueprintSessionStatus.DEFERRED))
        assertNotEquals(
            BlueprintSessionPresentation.statusText(AssessmentScope.WEEKLY_BLUEPRINT, BlueprintSessionStatus.DEFERRED),
            BlueprintSessionPresentation.statusText(AssessmentScope.MONTHLY_CAPABILITY, BlueprintSessionStatus.DEFERRED),
        )
        // A professional checkpoint gathers evidence; it never says ready.
        listOf("hazırsın", "yeterlisin", "sertifika").forEach { assertTrue(it !in MonthlyCopy.CHECKPOINT_NOT_A_GATE) }
    }
}
