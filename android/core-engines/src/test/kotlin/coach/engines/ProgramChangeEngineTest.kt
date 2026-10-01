package coach.engines

import coach.model.BlockingScope
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DailyCapacity
import coach.model.DecisionValue
import coach.model.DurationFit
import coach.model.EvidenceSeverity
import coach.model.NeedDisposition
import coach.model.NeedTrace
import coach.model.NeedTrigger
import coach.model.PlanChangeKind
import coach.model.PlanTrace
import coach.model.PlannedEntry
import coach.model.PriorityBand
import coach.model.ProgramChangeReport
import coach.model.ProgramSnapshot
import coach.model.RankVector
import coach.model.SkillAxes
import coach.model.StarvationBucket
import coach.model.StateChangeKind
import coach.model.TaskPurpose
import coach.model.TemporalUrgency
import coach.model.TrackBalance
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `ASUX-v0` §13.4 as code: a change is claimed only where canonical state actually changed, a state no
 * engine had written is not a "before", time alone changes nothing, and a plan change is a difference
 * between two recorded plan versions.
 */
class ProgramChangeEngineTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val linux = VersionedRef("skill.linux.filesystem_navigation", 1)

    private fun axes(mastery: String = "developing_independent", retention: String = "untracked", weakness: String = "none", skill: VersionedRef = pointer) =
        SkillAxes(skill, mastery, retention, weakness)

    private fun kinds(before: SkillAxes?, after: SkillAxes) = ProgramChangeEngine.stateChanges(before, after).map { it.kind }

    private fun snapshot(watermark: Long, vararg skills: SkillAxes, planId: Long? = null, plan: PlanTrace? = null) =
        ProgramSnapshot(watermark, "2026-10-01", skills.associateBy { it.skill }, planId, plan)

    private val rank = RankVector(BlockingScope.entries.first(), Criticality.entries.first(), EvidenceSeverity.entries.first(),
        TemporalUrgency.entries.first(), StarvationBucket.entries.first(), ContinuationValue.entries.first(),
        DecisionValue.entries.first(), TrackBalance.entries.first(), DurationFit.entries.first(), "k")

    private fun need(trigger: NeedTrigger, skill: VersionedRef = pointer) = NeedTrace("${trigger.id}:$skill", trigger, listOf(skill),
        emptyList(), PriorityBand.P1, rank, emptyList(), null, NeedDisposition.SELECTED, emptyList())

    private fun entry(position: Int, needKey: String, skill: VersionedRef = pointer) =
        PlannedEntry(position, "c$position", needKey, TaskPurpose.PRACTICE, "coding", "Task $position", skill, null, 10, 10, split = false)

    private fun trace(needs: List<NeedTrace>, selected: List<PlannedEntry>, reasons: List<String> = emptyList()) =
        PlanTrace("initial", "2026-10-01", 1, 10, emptyMap(), DailyCapacity(CapacitySource.NORMAL_PROFILE, 60, 54, false, false),
            needs, emptyList(), selected, reasons, emptyMap())

    // ---------------------------------------------------------------- mastery

    @Test
    fun `mastery reached is a confirmed capability`() {
        assertEquals(listOf(StateChangeKind.CAPABILITY_CONFIRMED), kinds(axes(), axes(mastery = "confirmed_current")))
        assertEquals(listOf(StateChangeKind.CAPABILITY_CONFIRMED),
            kinds(axes(mastery = "not_yet_evidenced"), axes(mastery = "confirmed_current")))
    }

    @Test
    fun `a contradiction opens a verification and does not take mastery away`() {
        val changes = ProgramChangeEngine.stateChanges(axes(mastery = "confirmed_current"), axes(mastery = "confirmation_verification_due"))
        assertEquals(listOf(StateChangeKind.VERIFICATION_OPENED), changes.map { it.kind })
        assertEquals("confirmed_current", changes.single().from)
        assertEquals("confirmation_verification_due", changes.single().to)
        assertEquals(listOf(StateChangeKind.VERIFICATION_RESOLVED),
            kinds(axes(mastery = "confirmation_verification_due"), axes(mastery = "confirmed_current")))
    }

    @Test
    fun `mastery is reported lost only when the gates decided it`() {
        assertEquals(listOf(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED),
            kinds(axes(mastery = "confirmation_verification_due"), axes(mastery = "developing_independent")))
        assertEquals(listOf(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED),
            kinds(axes(mastery = "confirmed_current"), axes(mastery = "developing_with_support")))
        // Moving between non-mastered states is development, not a reportable change.
        assertEquals(emptyList(), kinds(axes(mastery = "developing_with_support"), axes(mastery = "developing_independent")))
    }

    // ---------------------------------------------------------------- retention

    @Test
    fun `a review coming due is the day, not a change`() {
        assertEquals(emptyList(), kinds(axes(retention = "stable"), axes(retention = "review_due")))
        assertEquals(emptyList(), kinds(axes(retention = "fresh"), axes(retention = "review_due")))
        assertEquals(emptyList(), kinds(axes(retention = "untracked"), axes(retention = "fresh")))
    }

    @Test
    fun `retention changes are the ones evidence made`() {
        assertEquals(listOf(StateChangeKind.RETENTION_REVALIDATED), kinds(axes(retention = "review_due"), axes(retention = "stable")))
        assertEquals(listOf(StateChangeKind.RETENTION_REVALIDATED), kinds(axes(retention = "fresh"), axes(retention = "stable")))
        assertEquals(listOf(StateChangeKind.RETENTION_REVALIDATED), kinds(axes(retention = "verification_due"), axes(retention = "stable")))
        assertEquals(listOf(StateChangeKind.RETENTION_REVALIDATED), kinds(axes(retention = "at_risk"), axes(retention = "stable")))
        assertEquals(listOf(StateChangeKind.VERIFICATION_OPENED), kinds(axes(retention = "review_due"), axes(retention = "verification_due")))
        assertEquals(listOf(StateChangeKind.RETENTION_AT_RISK), kinds(axes(retention = "verification_due"), axes(retention = "at_risk")))
        // A Skill that left retention (mastery lost) is not "revalidated".
        assertEquals(emptyList(), kinds(axes(retention = "untracked"), axes(retention = "stable")))
    }

    @Test
    fun `one verification is named once, whichever axis opened it`() {
        val changes = kinds(axes(mastery = "confirmed_current", retention = "review_due"),
            axes(mastery = "confirmation_verification_due", retention = "verification_due"))
        assertEquals(listOf(StateChangeKind.VERIFICATION_OPENED), changes)
    }

    // ---------------------------------------------------------------- weakness

    @Test
    fun `a hypothesis is an open question, support is not yet a deficiency`() {
        assertEquals(listOf(StateChangeKind.WEAKNESS_QUESTION_OPENED), kinds(axes(weakness = "none"), axes(weakness = "hypothesis")))
        assertEquals(listOf(StateChangeKind.WEAKNESS_QUESTION_OPENED), kinds(axes(weakness = "resolved"), axes(weakness = "hypothesis")))
        assertEquals(listOf(StateChangeKind.WEAKNESS_SUPPORTED), kinds(axes(weakness = "hypothesis"), axes(weakness = "supported")))
        assertEquals(listOf(StateChangeKind.WEAKNESS_SUPPORTED), kinds(axes(weakness = "none"), axes(weakness = "supported")))
        // Support corrected down to a hypothesis is not a new question.
        assertEquals(emptyList(), kinds(axes(weakness = "supported"), axes(weakness = "hypothesis")))
    }

    @Test
    fun `remediation opens on confirmation and closes only to resolved or none`() {
        assertEquals(listOf(StateChangeKind.REMEDIATION_OPENED), kinds(axes(weakness = "supported"), axes(weakness = "remediation_required")))
        assertEquals(listOf(StateChangeKind.REMEDIATION_OPENED), kinds(axes(weakness = "none"), axes(weakness = "remediation_required")))
        assertEquals(listOf(StateChangeKind.REMEDIATION_CLOSED), kinds(axes(weakness = "remediation_required"), axes(weakness = "resolved")))
        assertEquals(listOf(StateChangeKind.REMEDIATION_CLOSED), kinds(axes(weakness = "remediation_required"), axes(weakness = "none")))
        // Remediation downgraded to support by a correction is not described as a newly supported weakness.
        assertEquals(emptyList(), kinds(axes(weakness = "remediation_required"), axes(weakness = "supported")))
        assertEquals(listOf(StateChangeKind.WEAKNESS_RESOLVED), kinds(axes(weakness = "hypothesis"), axes(weakness = "resolved")))
        assertEquals(listOf(StateChangeKind.WEAKNESS_RESOLVED), kinds(axes(weakness = "supported"), axes(weakness = "resolved")))
        assertEquals(emptyList(), kinds(axes(weakness = "none"), axes(weakness = "resolved")))
    }

    // ---------------------------------------------------------------- unknown before

    @Test
    fun `a state nobody had written is not a before`() {
        val unknown = mutableSetOf<VersionedRef>()
        val changes = ProgramChangeEngine.stateChanges(null, axes(mastery = "confirmed_current", weakness = "remediation_required"), unknown)
        assertEquals(emptyList(), changes)
        assertEquals(setOf(pointer), unknown)

        val axisUnknown = mutableSetOf<VersionedRef>()
        val partial = ProgramChangeEngine.stateChanges(axes(weakness = "not_yet_evaluated"),
            axes(mastery = "confirmed_current", weakness = "remediation_required"), axisUnknown)
        assertEquals(listOf(StateChangeKind.CAPABILITY_CONFIRMED), partial.map { it.kind })
        assertEquals(setOf(pointer), axisUnknown)
    }

    @Test
    fun `nothing changed says so`() {
        val same = snapshot(5, axes(), planId = 1, plan = trace(listOf(need(NeedTrigger.NEW_LEARNING)), listOf(entry(1, "new_learning:$pointer"))))
        val report = ProgramChangeEngine.report(same, same.copy(truthWatermark = 9))
        assertTrue(report.nothingChanged)
        assertEquals(emptyList(), report.reasonCodes)
        assertFalse(report.firstPlan)
    }

    // ---------------------------------------------------------------- plan

    @Test
    fun `the plan diff is needs opened and closed and tasks added and removed`() {
        val newKey = "new_learning:$pointer"
        val remKey = "remediation_required:$pointer"
        val linuxKey = "new_learning:$linux"
        val before = trace(listOf(need(NeedTrigger.NEW_LEARNING), need(NeedTrigger.NEW_LEARNING, linux)),
            listOf(entry(1, newKey), entry(2, linuxKey, linux)))
        val after = trace(listOf(need(NeedTrigger.REMEDIATION_REQUIRED), need(NeedTrigger.NEW_LEARNING, linux)),
            listOf(PlannedEntry(1, "r1", remKey, TaskPurpose.PRACTICE, "coding", "Fix", pointer, null, 10, 10, false), entry(2, linuxKey, linux)))
        val changes = ProgramChangeEngine.planChanges(before, after)
        assertEquals(
            listOf(PlanChangeKind.NEED_OPENED to remKey, PlanChangeKind.NEED_CLOSED to newKey,
                PlanChangeKind.TASK_ADDED to remKey, PlanChangeKind.TASK_REMOVED to newKey),
            changes.map { it.kind to it.needKey },
        )
        assertEquals(NeedTrigger.REMEDIATION_REQUIRED, changes[0].trigger)
        assertEquals(listOf(pointer), changes[2].skills)
        assertEquals("r1", changes[2].candidateId)
        assertEquals("c1", changes[3].candidateId)
    }

    @Test
    fun `a report joins state and plan changes and the new plan's own replan reasons`() {
        val before = snapshot(5, axes(weakness = "supported"), planId = 1,
            plan = trace(listOf(need(NeedTrigger.NEW_LEARNING)), listOf(entry(1, "new_learning:$pointer")), listOf("capacity.source_normal_profile")))
        val after = snapshot(8, axes(weakness = "remediation_required"), planId = 2,
            plan = trace(listOf(need(NeedTrigger.REMEDIATION_REQUIRED)), listOf(entry(1, "remediation_required:$pointer").copy(candidateId = "r1")),
                listOf("capacity.source_normal_profile", "replan.new_remediation_created")))
        val report = ProgramChangeEngine.report(before, after)
        assertEquals(listOf(StateChangeKind.REMEDIATION_OPENED), report.stateChanges.map { it.kind })
        assertEquals(4, report.planChanges.size)
        assertEquals(listOf("replan.new_remediation_created"), report.reasonCodes)
        assertEquals("skill_state:$pointer#remediation_opened", report.stateChanges.single().ref)
        assertFalse(report.nothingChanged)
    }

    @Test
    fun `the same plan version is not a plan change, and a first plan is not one either`() {
        val plan = trace(listOf(need(NeedTrigger.NEW_LEARNING)), listOf(entry(1, "new_learning:$pointer")), listOf("replan.capacity_changed"))
        val other = trace(emptyList(), emptyList())
        val same = ProgramChangeEngine.report(snapshot(1, axes(), planId = 3, plan = other), snapshot(2, axes(), planId = 3, plan = plan))
        assertEquals(emptyList(), same.planChanges)
        assertEquals(emptyList(), same.reasonCodes)

        val first = ProgramChangeEngine.report(snapshot(1, axes()), snapshot(2, axes(), planId = 1, plan = plan))
        assertTrue(first.firstPlan)
        assertEquals(emptyList(), first.planChanges)
        assertEquals(emptyList(), first.reasonCodes)
        assertTrue(first.nothingChanged)
    }

    @Test
    fun `a report never reads backwards in truth`() {
        assertFailsWith<IllegalArgumentException> {
            ProgramChangeReport(9, 5, emptyList(), emptyList(), emptyList(), emptyList(), false)
        }
    }

    @Test
    fun `Skills are reported in a stable order`() {
        val before = snapshot(1, axes(skill = linux), axes())
        val after = snapshot(2, axes(skill = linux, mastery = "confirmed_current"), axes(mastery = "confirmed_current"))
        assertEquals(listOf(pointer, linux), ProgramChangeEngine.report(before, after).stateChanges.map { it.skill })
    }
}
