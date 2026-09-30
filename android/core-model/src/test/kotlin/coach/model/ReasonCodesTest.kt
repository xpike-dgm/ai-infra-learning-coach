package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 12E: the reason codes are the contracts' own, and a trace can say which of them it recorded. */
class ReasonCodesTest {

    @Test
    fun `the catalogue is PDT-v0's ten families and PRG-v0's inputs, in their order`() {
        assertEquals(
            listOf("learning_need", "validation_trust", "prerequisite", "retention", "priority", "capacity",
                "diagnostic", "reentry", "selection", "replan", "prerequisite_input"),
            ReasonCatalog.codes.keys.map { it.id },
        )
        assertEquals(listOf(10, 7, 9, 7, 15, 13, 10, 8, 9, 13, 9), ReasonCatalog.codes.values.map { it.size })
        assertEquals(ReasonCatalog.all.size, ReasonCatalog.all.toSet().size, "a code appears twice")
    }

    @Test
    fun `every PDT-v0 code carries its family's namespace, and PRG-v0's inputs carry none`() {
        val namespaces = mapOf(
            ReasonCodeFamily.LEARNING_NEED to "need", ReasonCodeFamily.VALIDATION_TRUST to "candidate",
            ReasonCodeFamily.PREREQUISITE to "eligibility", ReasonCodeFamily.RETENTION to "retention",
            ReasonCodeFamily.PRIORITY to "priority", ReasonCodeFamily.CAPACITY to "capacity",
            ReasonCodeFamily.DIAGNOSTIC to "diagnostic", ReasonCodeFamily.REENTRY to "reentry",
            ReasonCodeFamily.SELECTION to "selection", ReasonCodeFamily.REPLAN to "replan",
        )
        namespaces.forEach { (family, namespace) ->
            ReasonCatalog.codes.getValue(family).forEach { code ->
                assertTrue(code.startsWith("$namespace.") && code.count { it == '.' } == 1, "$code is not $namespace.<name>")
            }
        }
        ReasonCatalog.codes.getValue(ReasonCodeFamily.PREREQUISITE_INPUT).forEach { assertFalse('.' in it, it) }
    }

    @Test
    fun `every code the planner's vocabulary names is in the catalogue`() {
        val named = NeedTrigger.entries.map { it.reasonCode } +
            PriorityBand.entries.map { it.reasonCode } +
            BlockingScope.entries.mapNotNull { it.reasonCode } +
            CapacitySource.entries.map { it.reasonCode } +
            ReplanTrigger.entries.mapNotNull { it.reasonCode }
        named.forEach { assertTrue(ReasonCatalog.isKnown(it), "$it is not a contract code") }
    }

    @Test
    fun `a code no contract defines has no family`() {
        assertNull(ReasonCatalog.familyOf("need.you_forgot_this"))
        assertNull(ReasonCatalog.familyOf("priority.score"))
        assertEquals(ReasonCodeFamily.PREREQUISITE_INPUT, ReasonCatalog.familyOf("independent_branch_available"))
    }

    @Test
    fun `a trace's recorded codes are exactly the codes it recorded`() {
        val skill = VersionedRef("skill.python.loops", 1)
        val rank = RankVector(BlockingScope.NON_BLOCKING, Criticality.REQUIRED, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
            TemporalUrgency.NOT_TIME_SENSITIVE, StarvationBucket.NONE, ContinuationValue.FRESH_NEW_CONTEXT,
            DecisionValue.NONE, TrackBalance.NONE, DurationFit.FITS_REMAINING, "k")
        val trace = PlanTrace(
            generationKind = "replan", studyDay = "2026-09-30", curriculumVersion = 1, truthWatermark = 1,
            policyVersions = emptyMap(),
            capacity = DailyCapacity(CapacitySource.NORMAL_PROFILE, 60, 54, reserveRelaxed = false, belowMinimumBlock = false),
            needs = listOf(NeedTrace("n", NeedTrigger.RETENTION_REVIEW_DUE, listOf(skill), emptyList(), PriorityBand.P3, rank,
                listOf("priority.p3_planned_progress"), "c", NeedDisposition.SELECTED, listOf("selection.selected"))),
            candidates = listOf(CandidateTrace("c", "n", LifecycleStatus.VALIDATED, PrerequisiteEligibility.ELIGIBLE, 10,
                CandidateDisposition.SELECTED, listOf("eligibility.ready"))),
            selected = emptyList(),
            planReasonCodes = listOf("capacity.source_normal_profile"),
            invariantChecks = emptyMap(),
            replan = ReplanRecord(ReplanTrigger.TASK_COMPLETED, 1, "2026-09-30", emptyList(), emptyList(), 0, 60),
        )
        assertEquals(
            setOf("capacity.source_normal_profile", "need.retention_review_due", "priority.p3_planned_progress",
                "selection.selected", "eligibility.ready"),
            trace.recordedReasonCodes(),
        )
        // An event with no code adds none.
        assertFalse(trace.recordedReasonCodes().any { it.startsWith("replan.") })
    }
}
