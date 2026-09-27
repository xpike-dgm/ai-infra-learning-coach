package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The planner's vocabulary as types, and the trace's stored form. */
class PlannerFactsTest {

    private val skill = VersionedRef("skill.c.pointer_dereference", 1)

    private fun rank(
        scope: BlockingScope = BlockingScope.NON_BLOCKING,
        criticality: Criticality = Criticality.REQUIRED,
        severity: EvidenceSeverity = EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
        fit: DurationFit = DurationFit.FITS_REMAINING,
        tie: String = "b",
    ) = RankVector(scope, criticality, severity, TemporalUrgency.NOT_TIME_SENSITIVE, StarvationBucket.NONE,
        ContinuationValue.FRESH_NEW_CONTEXT, DecisionValue.NONE, TrackBalance.NONE, fit, tie)

    @Test
    fun `the need triggers are exactly the ten 3B names`() {
        assertEquals(
            listOf("new_learning", "continue_learning", "weakness_detected", "remediation_required",
                "retention_review_due", "verification_due", "diagnostic_opportunity", "reinforcement_opportunity",
                "parallel_track_due", "integration_opportunity"),
            NeedTrigger.entries.map { it.id },
        )
        assertTrue(NeedTrigger.entries.all { it.reasonCode.startsWith("need.") })
    }

    @Test
    fun `there are five bands and they are names, not grades`() {
        assertEquals(
            listOf("integrity_blocker", "repair_or_verify", "maintain_or_continue", "planned_progress", "reinforce_or_optimize"),
            PriorityBand.entries.map { it.id },
        )
    }

    @Test
    fun `the rank vector is compared field by field, never summed`() {
        // A blocking scope beats every later field, however strong they are.
        val blocking = rank(scope = BlockingScope.BLOCKS_NEXT_READY_DEPENDENCY, criticality = Criticality.OPTIONAL,
            severity = EvidenceSeverity.NO_NEGATIVE_EVIDENCE, fit = DurationFit.CANNOT_FIT_TODAY, tie = "z")
        val everythingElse = rank(criticality = Criticality.CRITICAL_PREREQUISITE,
            severity = EvidenceSeverity.CONFIRMED_REPEATED_FAILURE, tie = "a")
        assertTrue(blocking < everythingElse)
        // Duration fit is considered only after the semantic fields are equal.
        assertTrue(rank(fit = DurationFit.CANNOT_FIT_TODAY, criticality = Criticality.CRITICAL_PREREQUISITE) <
            rank(fit = DurationFit.FITS_REMAINING))
        // The tie-break makes the order total.
        assertTrue(rank(tie = "a") < rank(tie = "b"))
        assertEquals(0, rank().compareTo(rank()))
    }

    @Test
    fun `an atomic evidence boundary cannot be split and a split names a smaller safe chunk`() {
        fun candidate(splittable: Boolean, chunk: Int?, atomic: Boolean) = TaskCandidate(
            "c", "need", TaskPurpose.PRACTICE, "coding", "Pointers", skill, 20, LifecycleStatus.VALIDATED,
            splittable = splittable, minimumSafeChunkMinutes = chunk, atomicEvidenceBoundary = atomic,
        )
        assertFailsWith<IllegalArgumentException> { candidate(splittable = true, chunk = 5, atomic = true) }
        assertFailsWith<IllegalArgumentException> { candidate(splittable = true, chunk = null, atomic = false) }
        assertFailsWith<IllegalArgumentException> { candidate(splittable = true, chunk = 20, atomic = false) }
        candidate(splittable = true, chunk = 5, atomic = false)
        assertFailsWith<IllegalArgumentException> {
            TaskCandidate("c", "need", TaskPurpose.TEACH, "reading", "x", skill, 0, LifecycleStatus.VALIDATED)
        }
    }

    @Test
    fun `a time budget is never negative`() {
        assertFailsWith<IllegalArgumentException> { DailyCapacityInput(60, 30, 90, todayOverrideMinutes = -1) }
    }

    @Test
    fun `nothing in the plan is a score, a percentage or a debt`() {
        listOf(PlannedEntry::class.java, NeedTrace::class.java, CandidateTrace::class.java, PlanTrace::class.java,
            DailyCapacity::class.java, LearningNeed::class.java).forEach { type ->
            val fields = type.declaredFields.map { it.name }
            listOf("score", "percent", "debt", "streak", "fail", "grade").forEach { word ->
                assertTrue(fields.none { it.contains(word, ignoreCase = true) }, "${type.simpleName} carries '$word': $fields")
            }
        }
    }

    private fun trace(title: String = "Pointers\tand, \"arrays\" = 100% İşaretçi\nline two") = PlanTrace(
        generationKind = "initial",
        studyDay = "2026-09-28",
        curriculumVersion = 3,
        truthWatermark = 42,
        policyVersions = linkedMapOf("priority_policy" to "PBR-v0", "planner" to "PLNX-v0"),
        capacity = DailyCapacity(CapacitySource.TODAY_OVERRIDE, 20, 18, reserveRelaxed = false, belowMinimumBlock = false),
        needs = listOf(
            NeedTrace("verification_due:$skill", NeedTrigger.VERIFICATION_DUE, listOf(skill), listOf("skill_state:x#watermark=4"),
                PriorityBand.P1, rank(tie = "verification_due:$skill"), listOf("priority.p1_repair_or_verify"),
                "cand,1", NeedDisposition.SELECTED, listOf("selection.selected")),
            NeedTrace("new_learning:$skill", NeedTrigger.NEW_LEARNING, listOf(skill), emptyList(),
                PriorityBand.P3, rank(), emptyList(), null, NeedDisposition.NO_VALID_CANDIDATE, emptyList()),
        ),
        candidates = listOf(
            CandidateTrace("cand,1", "verification_due:$skill", LifecycleStatus.VALIDATED, PrerequisiteEligibility.ELIGIBLE,
                15, CandidateDisposition.SELECTED, listOf("eligibility.ready", "selection.selected")),
            CandidateTrace("cand-2", "verification_due:$skill", LifecycleStatus.DRAFT, null,
                5, CandidateDisposition.INVALID_CANDIDATE, listOf("candidate.invalid_content")),
        ),
        selected = listOf(
            PlannedEntry(0, "cand,1", "verification_due:$skill", TaskPurpose.ASSESS, "code_reading", title, skill,
                null, 15, 15, split = false),
        ),
        planReasonCodes = listOf("capacity.source_today_override", "capacity.reserve_applied"),
        invariantChecks = linkedMapOf("planned_within_planning_budget" to true, "one_task_per_need" to true),
        skillsNotOnRoute = 7,
    )

    @Test
    fun `the stored trace reads back exactly, whatever the title contains`() {
        val original = trace()
        val stored = PlanTraceCodec.encode(original)
        assertTrue(stored.startsWith(PlanTraceCodec.FORMAT + "\n"))
        assertEquals(original, PlanTraceCodec.decode(stored))
        // Nothing a title holds can break a line or a field.
        // format, plan, 2 policies, capacity, reasons, 2 invariants, 1 selected, 2 needs, 2 candidates
        assertEquals(13, stored.split("\n").size)
    }

    @Test
    fun `a trace in another format or with an unknown part is not guessed at`() {
        val stored = PlanTraceCodec.encode(trace())
        assertNull(PlanTraceCodec.decode(stored.replace(PlanTraceCodec.FORMAT, "planner_trace/2")))
        assertNull(PlanTraceCodec.decode(stored + "\nmystery\tkey=value"))
        assertNull(PlanTraceCodec.decode(stored.replace("band=repair_or_verify", "band=urgent")))
        assertNull(PlanTraceCodec.decode(stored.replace("planned=15", "planned=fifteen")))
        assertNull(PlanTraceCodec.decode(stored.lines().filterNot { it.startsWith("capacity") }.joinToString("\n")))
        assertNull(PlanTraceCodec.decode(""))
    }
}
