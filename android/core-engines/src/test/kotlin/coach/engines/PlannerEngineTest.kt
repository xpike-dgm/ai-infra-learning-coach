package coach.engines

import coach.model.BlockingScope
import coach.model.CandidateDisposition
import coach.model.CapacityProfile
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DailyCapacity
import coach.model.DailyCapacityInput
import coach.model.EvidenceSeverity
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.NeedDisposition
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteEligibility
import coach.model.PriorityBand
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.StarvationBucket
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.TemporalUrgency
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The Stage 3 planner contracts as code. `PBR-v0`'s 80-minute worked example, `PDT-v0` §18's
 * "higher priority did not fit" case and the anti-patterns of `PBR-v0` §22 are tests here: each is a
 * plausible way a study planner quietly optimises for the wrong thing.
 */
class PlannerEngineTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val addressValue = VersionedRef("skill.c.address_value", 1)
    private val cLesson = VersionedRef("skill.c.arrays", 1)
    private val linkedList = VersionedRef("skill.c.linked_list_insert", 1)
    private val linux = VersionedRef("skill.linux.filesystem_navigation", 1)
    private val english = VersionedRef("skill.english.present_simple", 1)
    private val shell = VersionedRef("skill.linux.shell_shortcuts", 1)

    private fun need(
        trigger: NeedTrigger,
        skill: VersionedRef,
        criticality: Criticality = Criticality.REQUIRED,
        continuation: ContinuationValue = ContinuationValue.FRESH_NEW_CONTEXT,
        urgency: TemporalUrgency = TemporalUrgency.NOT_TIME_SENSITIVE,
        severity: EvidenceSeverity = EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
        track: String? = null,
    ) = LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), criticality, track = track,
        evidenceSeverity = severity, temporalUrgency = urgency, continuation = continuation)

    private fun candidate(
        id: String,
        need: LearningNeed,
        minutes: Int,
        purpose: TaskPurpose = TaskPurpose.PRACTICE,
        status: LifecycleStatus = LifecycleStatus.VALIDATED,
        splittable: Boolean = false,
        chunk: Int? = null,
        atomic: Boolean = false,
    ) = TaskCandidate(id, need.needKey, purpose, "coding", "Task $id", need.targetSkills.first(), minutes, status,
        splittable = splittable, minimumSafeChunkMinutes = chunk, atomicEvidenceBoundary = atomic)

    private fun decision(id: String, target: VersionedRef, eligibility: PrerequisiteEligibility = PrerequisiteEligibility.ELIGIBLE,
                         hard: List<VersionedRef> = emptyList(), uncertain: List<VersionedRef> = emptyList()) =
        PrerequisiteDecision(id, target, eligibility, hard, uncertain, emptyList(), emptyList(), emptyList(), emptyList(),
            false, "PRG-v0", emptyList(), emptyList())

    private fun capacity(planning: Int, hard: Int = planning) =
        DailyCapacity(CapacitySource.TODAY_OVERRIDE, hard, planning, reserveRelaxed = false, belowMinimumBlock = false)

    private fun plan(
        capacity: DailyCapacity,
        needs: List<LearningNeed>,
        candidates: List<TaskCandidate>,
        decisions: Map<String, PrerequisiteDecision> = candidates.associate { it.id to decision(it.id, it.primarySkill) },
        starvation: Map<String, StarvationBucket> = emptyMap(),
    ): PlanTrace = PlannerEngine.plan(capacity, needs, candidates, decisions, "2026-09-28", 1, 10, starvation)

    private fun PlanTrace.need(key: String) = needs.single { it.needKey == key }
    private fun PlanTrace.selectedIds() = selected.map { it.candidateId }

    // ------------------------------------------------------------------------------------ capacity

    @Test
    fun `capacity is resolved in the declared order and the hard budget is the learner's number`() {
        val input = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)
        assertEquals(CapacitySource.NORMAL_PROFILE, PlannerEngine.resolveCapacity(input).source)
        assertEquals(CapacitySource.SCHEDULED_DEFAULT,
            PlannerEngine.resolveCapacity(input.copy(scheduledDefaultMinutes = 45)).source)
        val short = PlannerEngine.resolveCapacity(input.copy(scheduledDefaultMinutes = 45, selectedProfile = CapacityProfile.SHORT))
        assertEquals(CapacitySource.SELECTED_SHORT to 30, short.source to short.hardBudgetMinutes)
        val today = PlannerEngine.resolveCapacity(input.copy(selectedProfile = CapacityProfile.INTENSIVE, todayOverrideMinutes = 20))
        assertEquals(CapacitySource.TODAY_OVERRIDE to 20, today.source to today.hardBudgetMinutes)
    }

    @Test
    fun `the planning budget keeps a ten percent reserve, rounded down`() {
        fun planning(minutes: Int) = PlannerEngine.resolveCapacity(DailyCapacityInput(minutes, 30, 90)).planningBudgetMinutes
        assertEquals(54, planning(60))
        assertEquals(40, planning(45))
        assertEquals(9, planning(10))
        (10..600).forEach { minutes -> assertEquals(Math.floorDiv(minutes * 9, 10), planning(minutes)) }
    }

    @Test
    fun `below the minimum block the reserve relaxes and nothing new is taught`() {
        val tiny = PlannerEngine.resolveCapacity(DailyCapacityInput(60, 30, 90, todayOverrideMinutes = 8))
        assertTrue(tiny.belowMinimumBlock && tiny.reserveRelaxed)
        assertEquals(8, tiny.planningBudgetMinutes)

        val teach = need(NeedTrigger.NEW_LEARNING, linux)
        val review = need(NeedTrigger.RETENTION_REVIEW_DUE, english, urgency = TemporalUrgency.JUST_DUE)
        // The teaching task would fit in the minutes left after the review; it is held back because this
        // is a micro-session, not because it does not fit.
        val trace = plan(tiny, listOf(teach, review), listOf(
            candidate("teach", teach, 3, purpose = TaskPurpose.TEACH),
            candidate("retrieve", review, 5, purpose = TaskPurpose.RETAIN),
        ))
        assertEquals(listOf("retrieve"), trace.selectedIds())
        assertEquals(NeedDisposition.ELIGIBLE_NOT_SELECTED, trace.need(teach.needKey).disposition)
        assertTrue("capacity.reserve_relaxed_for_microtask" in trace.planReasonCodes)
        assertTrue(trace.invariantChecks.getValue("no_teaching_below_minimum_block"))
    }

    // ------------------------------------------------------------------------------------ PBR-v0 §16

    @Test
    fun `eighty minutes of needs in a fifty minute budget is PBR-v0's worked example`() {
        val a = need(NeedTrigger.VERIFICATION_DUE, pointer, Criticality.CRITICAL_PREREQUISITE,
            severity = EvidenceSeverity.CLEAN_CONTRADICTION_OR_VERIFICATION_DUE)
        val b = need(NeedTrigger.REMEDIATION_REQUIRED, addressValue, severity = EvidenceSeverity.CONFIRMED_REPEATED_FAILURE)
        val c = need(NeedTrigger.CONTINUE_LEARNING, cLesson, continuation = ContinuationValue.PAUSED_SAFE_CHECKPOINT)
        val d = need(NeedTrigger.RETENTION_REVIEW_DUE, linux, urgency = TemporalUrgency.JUST_DUE)
        val e = need(NeedTrigger.PARALLEL_TRACK_DUE, english, track = "english")
        val f = need(NeedTrigger.REINFORCEMENT_OPPORTUNITY, shell)
        // Dependent C work that waits on the pointer verification: this is what makes A a real blocker.
        val dependent = need(NeedTrigger.NEW_LEARNING, linkedList)
        val candidates = listOf(
            candidate("A", a, 15, TaskPurpose.ASSESS), candidate("B", b, 15, TaskPurpose.REMEDIATE),
            candidate("C", c, 20), candidate("D", d, 10, TaskPurpose.RETAIN),
            candidate("E", e, 10), candidate("F", f, 10, TaskPurpose.REINFORCE),
            candidate("LL", dependent, 20, TaskPurpose.TEACH),
        )
        val decisions = candidates.associate { it.id to decision(it.id, it.primarySkill) } +
            ("LL" to decision("LL", linkedList, PrerequisiteEligibility.BLOCKED, uncertain = listOf(pointer)))

        val trace = plan(capacity(planning = 50, hard = 56), listOf(a, b, c, d, e, f, dependent), candidates, decisions)

        assertEquals(listOf("A", "B", "C"), trace.selectedIds())
        assertEquals(PriorityBand.P0, trace.need(a.needKey).band)
        assertEquals(PriorityBand.P1, trace.need(b.needKey).band)
        assertEquals(PriorityBand.P2, trace.need(c.needKey).band)
        listOf(d, e).forEach { assertEquals(PriorityBand.P3, trace.need(it.needKey).band) }
        assertEquals(PriorityBand.P4, trace.need(f.needKey).band)
        // D, E and F did not fail and are owed nothing: they stay open and simply did not fit today.
        listOf(d, e, f).forEach {
            assertEquals(NeedDisposition.ELIGIBLE_NOT_SELECTED, trace.need(it.needKey).disposition)
            assertTrue("capacity.deferred_not_enough_time" in trace.need(it.needKey).finalReasonCodes)
        }
        assertEquals(NeedDisposition.BLOCKED, trace.need(dependent.needKey).disposition)
        // The branch that waited did not stop the day (PRG-v0 §20).
        assertTrue(PlannerEngine.INDEPENDENT_BRANCH_AVAILABLE in trace.planReasonCodes)
        assertEquals(50, trace.selected.sumOf { it.plannedMinutes })
        assertTrue(trace.invariantChecks.values.all { it }, trace.invariantChecks.toString())
    }

    @Test
    fun `a critical label alone does not make an integrity blocker`() {
        val verification = need(NeedTrigger.VERIFICATION_DUE, pointer, Criticality.CRITICAL_PREREQUISITE)
        val trace = plan(capacity(60), listOf(verification), listOf(candidate("v", verification, 10, TaskPurpose.ASSESS)))
        assertEquals(PriorityBand.P1, trace.need(verification.needKey).band)
        assertEquals(BlockingScope.NON_BLOCKING, trace.need(verification.needKey).rank.blockingScope)
        assertFalse(PlannerEngine.INDEPENDENT_BRANCH_AVAILABLE in trace.planReasonCodes)

        // And real blocking alone is not enough either: an integrity blocker is a critical Skill that
        // holds work back. A non-critical one that does is repair work, ranked ahead by its scope.
        val remediation = need(NeedTrigger.REMEDIATION_REQUIRED, addressValue)
        assertEquals(PriorityBand.P1, PlannerEngine.band(remediation, BlockingScope.BLOCKS_CURRENT_REQUIRED_PATH, StarvationBucket.NONE))
        assertEquals(PriorityBand.P0, PlannerEngine.band(remediation.copy(criticality = Criticality.CRITICAL_PREREQUISITE),
            BlockingScope.BLOCKS_CURRENT_REQUIRED_PATH, StarvationBucket.NONE))
    }

    @Test
    fun `review due is maintenance, not forgetting, and never an integrity blocker`() {
        val critical = need(NeedTrigger.RETENTION_REVIEW_DUE, pointer, Criticality.CRITICAL_PREREQUISITE, urgency = TemporalUrgency.JUST_DUE)
        val standard = need(NeedTrigger.RETENTION_REVIEW_DUE, linux, urgency = TemporalUrgency.JUST_DUE)
        assertEquals(PriorityBand.P2, PlannerEngine.band(critical, BlockingScope.BLOCKS_CURRENT_REQUIRED_PATH, StarvationBucket.NONE))
        assertEquals(PriorityBand.P3, PlannerEngine.band(standard, BlockingScope.NON_BLOCKING, StarvationBucket.NONE))
        assertEquals(PriorityBand.P2, PlannerEngine.band(standard.copy(temporalUrgency = TemporalUrgency.OVERDUE_HIGH),
            BlockingScope.NON_BLOCKING, StarvationBucket.NONE))
    }

    @Test
    fun `a transfer opportunity is ranked exactly like an integration opportunity, never above learning`() {
        // 15G (`D-120`): `MCA-v0` §7 item 5 groups integration and transfer; the planner gives them one row.
        for (scope in BlockingScope.entries) {
            for (starvation in StarvationBucket.entries) {
                for (required in listOf(true, false)) {
                    val integration = need(NeedTrigger.INTEGRATION_OPPORTUNITY, linux).copy(requiredByCurriculum = required)
                    val transfer = integration.copy(needKey = "transfer_opportunity:$linux", trigger = NeedTrigger.TRANSFER_OPPORTUNITY)
                    assertEquals(PlannerEngine.band(integration, scope, starvation), PlannerEngine.band(transfer, scope, starvation),
                        "$scope $starvation $required")
                }
            }
        }
    }

    @Test
    fun `priority never rescues a blocked or untrusted candidate`() {
        val verification = need(NeedTrigger.VERIFICATION_DUE, pointer, Criticality.CRITICAL_PREREQUISITE)
        val blocked = candidate("blocked", verification, 10, TaskPurpose.ASSESS)
        val unvalidated = candidate("unvalidated", verification, 10, TaskPurpose.ASSESS, status = LifecycleStatus.CANDIDATE)
        val draft = candidate("draft", verification, 10, TaskPurpose.PRACTICE, status = LifecycleStatus.DRAFT)
        val badMetadata = candidate("bad-metadata", verification, 10, TaskPurpose.PRACTICE)
        val trace = plan(capacity(60), listOf(verification), listOf(blocked, unvalidated, draft, badMetadata),
            mapOf("blocked" to decision("blocked", pointer, PrerequisiteEligibility.BLOCKED, hard = listOf(addressValue)),
                "unvalidated" to decision("unvalidated", pointer), "draft" to decision("draft", pointer),
                "bad-metadata" to decision("bad-metadata", pointer, PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA)))
        assertTrue(trace.selected.isEmpty())
        assertEquals(NeedDisposition.BLOCKED, trace.need(verification.needKey).disposition)
        val byId = trace.candidates.associateBy { it.candidateId }
        assertEquals(CandidateDisposition.BLOCKED_PREREQUISITE, byId.getValue("blocked").disposition)
        assertEquals(listOf("candidate.untrusted_for_high_stakes_use"), byId.getValue("unvalidated").reasonCodes)
        assertEquals(listOf("candidate.invalid_content"), byId.getValue("draft").reasonCodes)
        assertEquals(CandidateDisposition.INVALID_CANDIDATE, byId.getValue("bad-metadata").disposition)
        assertEquals(listOf("candidate.invalid_prerequisite_metadata", "eligibility.invalid_prerequisite_metadata"),
            byId.getValue("bad-metadata").reasonCodes)
    }

    @Test
    fun `a candidate the gate never answered for is blocked, because the gate fails closed`() {
        val learning = need(NeedTrigger.NEW_LEARNING, linux)
        val trace = plan(capacity(60), listOf(learning), listOf(candidate("x", learning, 10, TaskPurpose.TEACH)), decisions = emptyMap())
        assertTrue(trace.selected.isEmpty())
        assertEquals(NeedDisposition.BLOCKED, trace.need(learning.needKey).disposition)
    }

    @Test
    fun `a short easy task never jumps a long important one`() {
        // PBR-v0 §9: five 10-minute P4 tasks are not worth more than one 30-minute P1 repair.
        val repair = need(NeedTrigger.REMEDIATION_REQUIRED, pointer, severity = EvidenceSeverity.CONFIRMED_REPEATED_FAILURE)
        val extras = (1..5).map { need(NeedTrigger.REINFORCEMENT_OPPORTUNITY, VersionedRef("skill.extra.s$it", 1)) }
        val candidates = listOf(candidate("repair", repair, 30, TaskPurpose.REMEDIATE)) +
            extras.mapIndexed { i, n -> candidate("extra$i", n, 10, TaskPurpose.REINFORCE) }
        val trace = plan(capacity(30), listOf(repair) + extras, candidates)
        assertEquals(listOf("repair"), trace.selectedIds())

        // Fit is the ninth rank field, not the first: a repair that only fits by a safe split still goes
        // ahead of planned progress that fits whole.
        val lesson = need(NeedTrigger.NEW_LEARNING, linux)
        val splitFirst = plan(capacity(30), listOf(repair, lesson), listOf(
            candidate("repair", repair, 40, TaskPurpose.REMEDIATE, splittable = true, chunk = 10),
            candidate("lesson", lesson, 20, TaskPurpose.TEACH),
        ))
        assertEquals(listOf("repair"), splitFirst.selectedIds())
        assertEquals(30, splitFirst.selected.single().plannedMinutes)
    }

    @Test
    fun `a higher priority task that does not fit is deferred for time, not called less important`() {
        // PDT-v0 §18.
        val verification = need(NeedTrigger.VERIFICATION_DUE, pointer)
        val progress = need(NeedTrigger.NEW_LEARNING, linux)
        val trace = plan(capacity(12), listOf(verification, progress), listOf(
            candidate("big", verification, 25, TaskPurpose.ASSESS, atomic = true),
            candidate("small", progress, 10, TaskPurpose.TEACH),
        ))
        assertEquals(listOf("small"), trace.selectedIds())
        val deferred = trace.need(verification.needKey)
        assertEquals(PriorityBand.P1, deferred.band)
        assertEquals(listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time"), deferred.finalReasonCodes)
        assertFalse(deferred.finalReasonCodes.any { "lower_priority" in it })
    }

    @Test
    fun `a safe split plans the part that fits, and an atomic evidence boundary is never cut`() {
        val lesson = need(NeedTrigger.CONTINUE_LEARNING, cLesson, continuation = ContinuationValue.ACTIVE_LEARNING_CONTEXT)
        val split = plan(capacity(20), listOf(lesson), listOf(candidate("lesson", lesson, 40, splittable = true, chunk = 15)))
        assertEquals(20, split.selected.single().plannedMinutes)
        assertEquals(40, split.selected.single().estimatedMinutes)
        assertTrue(split.selected.single().split)
        assertEquals(NeedDisposition.PARTIALLY_SERVED, split.need(lesson.needKey).disposition)

        val tooSmallForChunk = plan(capacity(10), listOf(lesson), listOf(candidate("lesson", lesson, 40, splittable = true, chunk = 15)))
        assertTrue(tooSmallForChunk.selected.isEmpty())

        val assessment = need(NeedTrigger.VERIFICATION_DUE, pointer)
        val atomic = plan(capacity(20), listOf(assessment), listOf(candidate("exam", assessment, 40, TaskPurpose.ASSESS, atomic = true)))
        assertTrue(atomic.selected.isEmpty())
    }

    @Test
    fun `a smaller alternative for the same need is tried before deferring, and only one task serves a need`() {
        val repair = need(NeedTrigger.REMEDIATION_REQUIRED, pointer)
        val trace = plan(capacity(15), listOf(repair), listOf(
            candidate("a-long", repair, 30, TaskPurpose.REMEDIATE),
            candidate("b-short", repair, 12, TaskPurpose.REMEDIATE),
            candidate("c-shorter", repair, 8, TaskPurpose.REMEDIATE),
        ))
        assertEquals(listOf("b-short"), trace.selectedIds())
        val byId = trace.candidates.associateBy { it.candidateId }
        assertEquals(CandidateDisposition.SELECTED_SMALLER_ALTERNATIVE, byId.getValue("b-short").disposition)
        assertEquals(CandidateDisposition.SUPERSEDED_SAME_NEED_ALTERNATIVE, byId.getValue("a-long").disposition)
        assertEquals(CandidateDisposition.SUPERSEDED_SAME_NEED_ALTERNATIVE, byId.getValue("c-shorter").disposition)

        val roomy = plan(capacity(60), listOf(repair), listOf(
            candidate("a-long", repair, 30, TaskPurpose.REMEDIATE), candidate("b-short", repair, 12, TaskPurpose.REMEDIATE),
        ))
        assertEquals(listOf("a-long"), roomy.selectedIds())
        assertTrue(roomy.invariantChecks.getValue("one_task_per_need"))
    }

    @Test
    fun `the candidate set per need is bounded, in a stable order, deprecated last`() {
        val learning = need(NeedTrigger.NEW_LEARNING, linux)
        val candidates = listOf(candidate("a0", learning, 50, TaskPurpose.TEACH, status = LifecycleStatus.DEPRECATED)) +
            (1..6).map { candidate("c$it", learning, 50, TaskPurpose.TEACH) }
        val trace = plan(capacity(60), listOf(learning), candidates)
        assertEquals(listOf("c1"), trace.selectedIds())
        val suppressed = trace.candidates.filter { it.disposition == CandidateDisposition.DUPLICATE_SUPPRESSED }.map { it.candidateId }
        assertEquals(listOf("a0", "c6"), suppressed.sorted())
        assertEquals(PlannerEngine.MAX_CANDIDATES_PER_NEED_V0, trace.candidates.size - suppressed.size)
    }

    @Test
    fun `starvation lifts planned progress one band and never past repair work`() {
        val track = need(NeedTrigger.PARALLEL_TRACK_DUE, english, track = "english")
        val optional = need(NeedTrigger.REINFORCEMENT_OPPORTUNITY, shell)
        val repair = need(NeedTrigger.REMEDIATION_REQUIRED, pointer)
        assertEquals(PriorityBand.P2, PlannerEngine.band(track, BlockingScope.NON_BLOCKING, StarvationBucket.PROMOTE))
        assertEquals(PriorityBand.P4, PlannerEngine.band(optional, BlockingScope.NON_BLOCKING, StarvationBucket.PROMOTE))
        assertEquals(PriorityBand.P1, PlannerEngine.band(repair, BlockingScope.NON_BLOCKING, StarvationBucket.PROMOTE))

        val trace = plan(capacity(60), listOf(track), listOf(candidate("e", track, 10)),
            starvation = mapOf(track.needKey to StarvationBucket.PROMOTE))
        assertTrue("priority.starvation_promoted" in trace.need(track.needKey).priorityReasonCodes)
    }

    @Test
    fun `no pressure is invented when none is supplied`() {
        val track = need(NeedTrigger.PARALLEL_TRACK_DUE, english, track = "english")
        val trace = plan(capacity(60), listOf(track), listOf(candidate("e", track, 10)))
        assertEquals(StarvationBucket.NONE, trace.need(track.needKey).rank.starvation)
        assertEquals(PriorityBand.P3, trace.need(track.needKey).band)
    }

    @Test
    fun `the same state gives the same plan whatever order it arrives in`() {
        val needs = listOf(
            need(NeedTrigger.NEW_LEARNING, linux), need(NeedTrigger.NEW_LEARNING, english),
            need(NeedTrigger.RETENTION_REVIEW_DUE, shell, urgency = TemporalUrgency.JUST_DUE),
            need(NeedTrigger.REMEDIATION_REQUIRED, pointer),
        )
        val candidates = needs.mapIndexed { i, n -> candidate("c$i", n, 10 + i, TaskPurpose.PRACTICE) }
        val first = plan(capacity(30), needs, candidates)
        val second = plan(capacity(30), needs.reversed(), candidates.reversed())
        assertEquals(first, second)
    }

    @Test
    fun `a candidate for a need that is no longer open is recorded, not planned`() {
        val open = need(NeedTrigger.NEW_LEARNING, linux)
        val stale = TaskCandidate("stale", "verification_due:gone@v1", TaskPurpose.ASSESS, "quiz", "Old", pointer, 5, LifecycleStatus.VALIDATED)
        val trace = plan(capacity(60), listOf(open), listOf(stale))
        assertEquals(CandidateDisposition.RESOLVED_BEFORE_SELECTION, trace.candidates.single().disposition)
        assertTrue(trace.selected.isEmpty())
    }

    @Test
    fun `a need nothing authored serves says so without inventing a reason`() {
        val learning = need(NeedTrigger.NEW_LEARNING, linux)
        val trace = plan(capacity(60), listOf(learning), emptyList())
        assertEquals(NeedDisposition.NO_VALID_CANDIDATE, trace.need(learning.needKey).disposition)
        assertTrue(trace.need(learning.needKey).finalReasonCodes.isEmpty())
    }

    // ------------------------------------------------------------------------------------ needs

    private fun state(skill: VersionedRef, mastery: MasteryAxisState?, retention: RetentionAxis = RetentionAxis.NOT_YET_EVALUATED,
                      weakness: String? = null, lifecycle: String = "published", critical: Boolean = false) =
        SkillPlanningState(skill, lifecycle, critical, mastery, retention, weakness)

    @Test
    fun `needs come from the axis each engine owns, and from nothing else`() {
        val needs = PlannerEngine.needsFromSkillStates(listOf(
            state(linux, null),
            state(english, MasteryAxisState.NOT_YET_EVIDENCED),
            state(cLesson, MasteryAxisState.DEVELOPING_INDEPENDENT),
            state(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, critical = true),
            state(shell, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE),
            state(addressValue, MasteryAxisState.CONFIRMED_CURRENT, weakness = "remediation_required"),
            state(linkedList, MasteryAxisState.CONFIRMED_CURRENT),
        )).associate { it.needKey to it }
        assertEquals(
            setOf("new_learning:$linux", "new_learning:$english", "continue_learning:$cLesson",
                "verification_due:$pointer", "retention_review_due:$shell", "remediation_required:$addressValue"),
            needs.keys,
        )
        assertEquals(Criticality.CRITICAL_PREREQUISITE, needs.getValue("verification_due:$pointer").criticality)
        assertEquals(ContinuationValue.ACTIVE_LEARNING_CONTEXT, needs.getValue("continue_learning:$cLesson").continuation)
        // Review due is due, not a contradiction; how overdue is RVR-v0's to say.
        val review = needs.getValue("retention_review_due:$shell")
        assertEquals(EvidenceSeverity.NO_NEGATIVE_EVIDENCE, review.evidenceSeverity)
        assertEquals(TemporalUrgency.JUST_DUE, review.temporalUrgency)
        assertEquals(EvidenceSeverity.CONFIRMED_REPEATED_FAILURE, needs.getValue("remediation_required:$addressValue").evidenceSeverity)
        assertEquals(EvidenceSeverity.CLEAN_CONTRADICTION_OR_VERIFICATION_DUE, needs.getValue("verification_due:$pointer").evidenceSeverity)
    }

    @Test
    fun `a Skill off the route opens nothing, and a deprecated one opens nothing new`() {
        val needs = PlannerEngine.needsFromSkillStates(listOf(
            state(linux, null, lifecycle = "draft"),
            state(english, null, lifecycle = "retired"),
            state(shell, null, lifecycle = "deprecated"),
            state(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, lifecycle = "deprecated"),
        ))
        assertEquals(listOf("verification_due:$pointer"), needs.map { it.needKey })
    }

    @Test
    fun `an unevaluated axis opens no need`() {
        val needs = PlannerEngine.needsFromSkillStates(listOf(
            state(pointer, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.NOT_YET_EVALUATED, weakness = null),
        ))
        assertTrue(needs.isEmpty())
    }

    // ------------------------------------------------------------------------------------ explanation (12E)

    private fun gate(id: String, target: VersionedRef, eligibility: PrerequisiteEligibility,
                     hard: List<VersionedRef> = emptyList(), uncertain: List<VersionedRef> = emptyList(),
                     soft: List<VersionedRef> = emptyList(), reviewDue: List<VersionedRef> = emptyList()) =
        PrerequisiteDecision(id, target, eligibility, hard, uncertain, soft, reviewDue, emptyList(), emptyList(),
            false, "PRG-v0", emptyList(), emptyList())

    @Test
    fun `a waiting candidate names the Skill it waits on, and one that went ahead names what it went ahead with`() {
        val insert = need(NeedTrigger.NEW_LEARNING, linkedList)
        val arrays = need(NeedTrigger.NEW_LEARNING, cLesson)
        val paths = need(NeedTrigger.NEW_LEARNING, linux)
        val review = need(NeedTrigger.CONTINUE_LEARNING, shell)
        val candidates = listOf(candidate("insert", insert, 10), candidate("arrays", arrays, 10),
            candidate("paths", paths, 10), candidate("shortcuts", review, 10))
        val trace = plan(capacity(60), listOf(insert, arrays, paths, review), candidates, decisions = mapOf(
            "insert" to gate("insert", linkedList, PrerequisiteEligibility.BLOCKED, hard = listOf(pointer, addressValue)),
            "arrays" to gate("arrays", cLesson, PrerequisiteEligibility.CONDITIONAL_ELIGIBLE, uncertain = listOf(pointer)),
            "paths" to gate("paths", linux, PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT, soft = listOf(english)),
            "shortcuts" to gate("shortcuts", shell, PrerequisiteEligibility.ELIGIBLE, reviewDue = listOf(linux)),
        ))
        fun related(id: String) = trace.candidates.single { it.candidateId == id }.relatedSkills
        assertEquals(listOf(pointer, addressValue), related("insert"))
        assertEquals(listOf(pointer), related("arrays"))
        assertEquals(listOf(english), related("paths"))
        assertEquals(listOf(linux), related("shortcuts"))
        // A blocker that is not missing outright is the prerequisite whose confidence the work waits on.
        val strict = plan(capacity(60), listOf(insert), listOf(candidate("insert", insert, 10)), decisions = mapOf(
            "insert" to gate("insert", linkedList, PrerequisiteEligibility.BLOCKED, uncertain = listOf(pointer))))
        assertEquals(listOf(pointer), strict.candidates.single().relatedSkills)
        // A candidate the gate never answered for waits too, and names no Skill it cannot know.
        val unanswered = plan(capacity(60), listOf(insert), listOf(candidate("insert", insert, 10)), decisions = emptyMap())
        assertEquals(emptyList(), unanswered.candidates.single().relatedSkills)
    }

    @Test
    fun `every code the planner writes is a contract code`() {
        val repair = need(NeedTrigger.REMEDIATION_REQUIRED, pointer, criticality = Criticality.CRITICAL_PREREQUISITE,
            severity = EvidenceSeverity.CONFIRMED_REPEATED_FAILURE)
        val insert = need(NeedTrigger.NEW_LEARNING, linkedList)
        val arrays = need(NeedTrigger.CONTINUE_LEARNING, cLesson, continuation = ContinuationValue.PAUSED_SAFE_CHECKPOINT)
        val paths = need(NeedTrigger.NEW_LEARNING, linux)
        val words = need(NeedTrigger.PARALLEL_TRACK_DUE, english)
        val candidates = listOf(
            candidate("repair", repair, 25, purpose = TaskPurpose.REMEDIATE),
            candidate("insert", insert, 10),
            candidate("arrays", arrays, 30, splittable = true, chunk = 10),
            candidate("paths-long", paths, 40), candidate("paths-short", paths, 5),
            candidate("words", words, 10, purpose = TaskPurpose.ASSESS, status = LifecycleStatus.DRAFT),
        )
        val trace = plan(capacity(45), listOf(repair, insert, arrays, paths, words), candidates, decisions = mapOf(
            "repair" to gate("repair", pointer, PrerequisiteEligibility.ELIGIBLE),
            "insert" to gate("insert", linkedList, PrerequisiteEligibility.BLOCKED, hard = listOf(pointer)),
            "arrays" to gate("arrays", cLesson, PrerequisiteEligibility.ELIGIBLE),
            "paths-long" to gate("paths-long", linux, PrerequisiteEligibility.ELIGIBLE),
            "paths-short" to gate("paths-short", linux, PrerequisiteEligibility.ELIGIBLE),
        ), starvation = mapOf(paths.needKey to StarvationBucket.PROMOTE))
        val written = trace.planReasonCodes + trace.needs.flatMap { it.priorityReasonCodes + it.finalReasonCodes + it.trigger.reasonCode } +
            trace.candidates.flatMap { it.reasonCodes }
        assertTrue(written.size > 15, "the case should exercise many codes: $written")
        written.forEach { assertTrue(coach.model.ReasonCatalog.isKnown(it), "$it is not a PDT-v0 or PRG-v0 code") }
    }
}
