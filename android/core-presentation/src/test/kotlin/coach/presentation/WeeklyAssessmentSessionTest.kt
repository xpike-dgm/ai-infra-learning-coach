package coach.presentation

import coach.model.AssessmentBlueprintSlot
import coach.model.AssessmentIntent
import coach.model.AssessmentScope
import coach.model.BlueprintRole
import coach.model.Criticality
import coach.model.IndependenceMode
import coach.model.LifecycleStatus
import coach.model.NeedTrigger
import coach.model.PriorityBand
import coach.model.SlotStatus
import coach.model.VersionedRef
import coach.model.WeeklyAssessmentBlueprint
import coach.model.WeeklyAssessmentResult
import coach.model.WeeklySessionStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeeklyAssessmentSessionTest {

    private fun slot(id: String, role: BlueprintRole, n: Int, tools: List<String>, ready: Boolean = true) = AssessmentBlueprintSlot(
        slotId = id, role = role, needKey = "need:$n", trigger = NeedTrigger.VERIFICATION_DUE,
        targetSkill = VersionedRef("skill.test.s$n", 1), criticality = Criticality.REQUIRED, band = PriorityBand.P1,
        sourceStateRefs = emptyList(), track = null, status = if (ready) SlotStatus.READY else SlotStatus.NO_VALID_ITEM,
        item = if (ready) VersionedRef("item.test.i$n", 1) else null,
        targetObjectives = if (ready) listOf(VersionedRef("objective.test.o$n", 1)) else emptyList(),
        evidenceType = if (ready) "code_reading" else null, variantFamilyId = if (ready) "fam.$n" else null,
        expectedActiveMinutes = if (ready) 10 else null, itemLifecycle = if (ready) LifecycleStatus.VALIDATED else null,
        allowedTools = tools,
    )

    private val blueprint = WeeklyAssessmentBlueprint(
        cycleId = "2026-W40", studyDay = "2026-10-01", curriculumVersion = 1, truthWatermark = 1, policyVersion = "WBA-v0",
        evaluatorAvailable = false, recentSince = null,
        slots = listOf(
            slot("slot-1", BlueprintRole.WEAKNESS_OR_VERIFICATION, 1, listOf("compiler", "terminal")),
            slot("slot-2", BlueprintRole.RETENTION_DUE, 2, listOf("compiler")),
            slot("slot-3", BlueprintRole.RETENTION_DUE, 3, listOf("compiler", "debugger")),
            slot("slot-4", BlueprintRole.RECENT_REQUIRED_PROGRESS, 4, emptyList(), ready = false),
        ),
        exclusions = emptyList(), reasonCodes = emptyList(),
    )

    @Test
    fun `the week runs in the one interior, blocks by role, each slot one boundary`() {
        val view = WeeklySessionPresentation.view(blueprint)
        assertEquals(AssessmentScope.WEEKLY_BLUEPRINT, view.scope)
        assertEquals(IndependenceMode.H0_REQUIRED, view.independenceMode)
        assertEquals(listOf(listOf("slot-1"), listOf("slot-2", "slot-3")), view.blocks.map { b -> b.boundaries.map { it.id } })
        assertEquals(listOf(AssessmentIntent.VERIFICATION), view.intents)
        // A slot with no item is not a boundary the learner could be asked to answer.
        assertTrue(view.blocks.flatMap { it.boundaries }.none { it.id == "slot-4" })
        assertEquals(SessionState.SESSION_ORIENTATION, view.state)
    }

    @Test
    fun `the session never discloses a tool one of its items forbids`() {
        assertEquals(listOf("compiler"), WeeklySessionPresentation.view(blueprint).allowedTools.allowed)
    }

    @Test
    fun `the interior rules apply unchanged - a submitted boundary freezes and a skip is not incorrect`() {
        val view = WeeklySessionPresentation.view(blueprint)
        val submitted = (SessionNavigation.submit(view, "slot-1") as BoundaryOutcome.Applied).session
        assertEquals(BoundaryOutcome.Refused(BoundaryRefusal.ALREADY_FROZEN), SessionNavigation.submit(submitted, "slot-1"))
        val skipped = (SessionNavigation.skip(submitted, "slot-2") as BoundaryOutcome.Applied).session
        val result = WeeklySessionPresentation.result(emptyResult(), skipped)
        assertTrue(result.partial)
        assertEquals(listOf(NotReliablyMeasured.UNSUBMITTED_SLOT), result.notReliablyMeasured)
        assertTrue(result.families.isEmpty(), "no change is claimed that no engine reported")
    }

    @Test
    fun `what could not be measured is first class`() {
        val view = WeeklySessionPresentation.view(blueprint)
        val result = WeeklySessionPresentation.result(emptyResult().copy(
            invalidOrUnusableEvidenceIds = listOf(3), provisionalEvidenceIds = listOf(4),
            assistanceRecheckObjectives = listOf(VersionedRef("objective.test.o1", 1)),
        ), view)
        assertEquals(
            listOf(NotReliablyMeasured.INVALID_ITEM, NotReliablyMeasured.PROVISIONAL_EVALUATION, NotReliablyMeasured.ASSISTED_ATTEMPT),
            result.notReliablyMeasured,
        )
    }

    @Test
    fun `a status is described, never judged`() {
        WeeklySessionStatus.entries.forEach { status ->
            val text = WeeklySessionPresentation.statusText(status)
            listOf("başarısız oldun", "kaldın", "geçtin", "puan", "%").forEach { word -> assertTrue(word !in text, "$status: $text") }
        }
        assertTrue("borç" in WeeklySessionPresentation.statusText(WeeklySessionStatus.PARTIAL))
    }

    @Test
    fun `an empty week shows nothing to start rather than an empty exam`() {
        val empty = blueprint.copy(slots = listOf(slot("slot-4", BlueprintRole.RECENT_REQUIRED_PROGRESS, 4, emptyList(), ready = false)))
        assertEquals(SessionState.BLOCKED_NOT_STARTABLE, WeeklySessionPresentation.view(empty).state)
    }

    private fun emptyResult() = WeeklyAssessmentResult(1, "2026-W40", WeeklySessionStatus.PARTIAL, emptyList(), emptyList(),
        emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
        emptyList(), emptyList(), "WBA-v0")
}
