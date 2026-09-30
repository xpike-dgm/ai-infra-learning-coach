package coach.presentation

import coach.model.BlockingScope
import coach.model.CandidateDisposition
import coach.model.CandidateTrace
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DailyCapacity
import coach.model.DecisionValue
import coach.model.DurationFit
import coach.model.EvidenceSeverity
import coach.model.LifecycleStatus
import coach.model.NeedDisposition
import coach.model.NeedTrace
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PlannedEntry
import coach.model.PlannerExplanationFacts
import coach.model.PrerequisiteEligibility
import coach.model.PriorityBand
import coach.model.RankVector
import coach.model.ReentryContext
import coach.model.ReplanRecord
import coach.model.ReplanTrigger
import coach.model.StarvationBucket
import coach.model.TaskPurpose
import coach.model.TemporalUrgency
import coach.model.TrackBalance
import coach.model.VersionedRef
import coach.model.recordedReasonCodes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `PDT-v0` §2.2 as tests: `user_facing_explanation ⊆ facts_in_decision_trace`. The rules worth breaking
 * are the ones a friendly explanation drifts into — calling deferred work less important, a waiting task
 * "locked" without its real blocker, a review "forgotten", absence a debt — so each is built directly.
 */
class PlannerExplanationTest {

    private val loops = VersionedRef("skill.python.loops", 1)
    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val address = VersionedRef("skill.c.address_of", 1)
    private val lists = VersionedRef("skill.c.linked_list_insert", 1)
    private val review = VersionedRef("skill.linux.paths", 1)
    private val words = VersionedRef("skill.english.error_messages", 1)

    private fun rank(continuation: ContinuationValue = ContinuationValue.FRESH_NEW_CONTEXT) = RankVector(
        BlockingScope.NON_BLOCKING, Criticality.REQUIRED, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
        TemporalUrgency.NOT_TIME_SENSITIVE, StarvationBucket.NONE, continuation, DecisionValue.NONE,
        TrackBalance.NONE, DurationFit.FITS_REMAINING, "k")

    private fun need(key: String, trigger: NeedTrigger, skill: VersionedRef, disposition: NeedDisposition,
                     priority: List<String> = listOf("priority.p3_planned_progress"), final: List<String>,
                     continuation: ContinuationValue = ContinuationValue.FRESH_NEW_CONTEXT) =
        NeedTrace(key, trigger, listOf(skill), emptyList(), PriorityBand.P3, rank(continuation), priority,
            if (disposition == NeedDisposition.SELECTED || disposition == NeedDisposition.PARTIALLY_SERVED) "c-$key" else null,
            disposition, final)

    private fun entry(position: Int, key: String, skill: VersionedRef, preserved: Boolean = false, split: Boolean = false) =
        PlannedEntry(position, "c-$key", key, TaskPurpose.PRACTICE, "coding", "Task $key", skill, null, 20,
            if (split) 10 else 20, split, preserved)

    /** A day with a selected task, a split one, a paused one, a deferred one, a waiting one and one nothing serves. */
    private fun day() = PlanTrace(
        generationKind = "initial", studyDay = "2026-09-30", curriculumVersion = 1, truthWatermark = 1,
        policyVersions = emptyMap(),
        capacity = DailyCapacity(CapacitySource.SELECTED_SHORT, 30, 27, reserveRelaxed = false, belowMinimumBlock = false),
        needs = listOf(
            need("repair", NeedTrigger.REMEDIATION_REQUIRED, pointer, NeedDisposition.SELECTED,
                priority = listOf("priority.p0_integrity_blocker", "priority.blocks_next_ready_dependency"),
                final = listOf("selection.selected", "capacity.selected_within_budget")),
            need("paused", NeedTrigger.CONTINUE_LEARNING, loops, NeedDisposition.PARTIALLY_SERVED,
                priority = listOf("priority.p2_maintain_or_continue", "priority.continuation_value"),
                final = listOf("selection.selected_split", "capacity.split_to_fit"),
                continuation = ContinuationValue.PAUSED_SAFE_CHECKPOINT),
            need("review", NeedTrigger.RETENTION_REVIEW_DUE, review, NeedDisposition.ELIGIBLE_NOT_SELECTED,
                final = listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time")),
            need("lists", NeedTrigger.NEW_LEARNING, lists, NeedDisposition.BLOCKED, final = listOf("selection.blocked_prerequisite")),
            need("words", NeedTrigger.PARALLEL_TRACK_DUE, words, NeedDisposition.NO_VALID_CANDIDATE, final = emptyList()),
        ),
        candidates = listOf(
            CandidateTrace("c-repair", "repair", LifecycleStatus.VALIDATED, PrerequisiteEligibility.CONDITIONAL_ELIGIBLE, 20,
                CandidateDisposition.SELECTED, listOf("eligibility.conditional_uncertain", "selection.selected",
                    "capacity.selected_within_budget"), listOf(address)),
            CandidateTrace("c-lists", "lists", LifecycleStatus.VALIDATED, PrerequisiteEligibility.BLOCKED, 15,
                CandidateDisposition.BLOCKED_PREREQUISITE, listOf("eligibility.blocked_hard_prerequisite"), listOf(pointer, address)),
        ),
        selected = listOf(entry(0, "repair", pointer), entry(1, "paused", loops, split = true)),
        planReasonCodes = listOf("capacity.source_short_profile", "capacity.reserve_applied",
            "capacity.hard_budget_not_exceeded", "independent_branch_available"),
        invariantChecks = linkedMapOf("hard_budget_not_exceeded" to true),
    )

    private val names = mapOf(pointer to "Pointer dereference", address to "Adres operatörü", lists to "Bağlı liste")

    private fun explain(trace: PlanTrace, skillNames: Map<VersionedRef, String> = names) =
        PlannerExplanationPresentation.of(PlannerExplanationFacts.Readable(trace, skillNames))

    @Test
    fun `every statement comes from a code the trace recorded or from a trace fact`() {
        val trace = day()
        val view = explain(trace)
        assertEquals(ExplanationState.EXPLAINED, view.state)
        val recorded = trace.recordedReasonCodes()
        view.statements.forEach { statement ->
            val code = statement.code
            if (code != null) assertTrue(code in recorded, "$code was never recorded")
            else assertTrue(statement.fact != null, "a statement with neither a code nor a fact")
        }
        // A code the trace did not record cannot be made into a statement at all.
        assertNull(Statement.ofCode("need.verification_due", recorded))
        assertNull(Statement.ofCode("need.you_forgot_this", recorded + "need.you_forgot_this"))
    }

    @Test
    fun `why today is the need first, then what moved or fitted it, and the band is never the reason`() {
        val view = explain(day())
        val repair = view.today.single { it.position == 0 }
        assertEquals("need.remediation_required", repair.why.code)
        assertEquals(listOf("priority.blocks_next_ready_dependency", "eligibility.conditional_uncertain"),
            repair.supporting.map { it.code })
        assertEquals(listOf(SkillMention(address, "Adres operatörü")), repair.supporting.last().skills)
        val paused = view.today.single { it.position == 1 }
        assertEquals("need.continue_learning_active", paused.why.code)
        assertEquals(listOf("priority.continuation_value", "capacity.split_to_fit"), paused.supporting.map { it.code })
        assertTrue(view.statements.none { it.code?.matches(Regex("priority\\.p[0-4]_.*")) == true },
            "a band was shown as the reason")
    }

    @Test
    fun `a need deferred for time is deferred for time, never called less important`() {
        val deferred = explain(day()).notToday.single { it.needKey == "review" }
        assertEquals("need.retention_review_due", deferred.need.code)
        assertEquals("capacity.deferred_not_enough_time", deferred.whyNot.code)
        assertEquals(Reconsideration.NEXT_PLAN, deferred.reconsideration)
        assertFalse(ExplanationCopy.text(deferred.whyNot).contains("öncelik"))
        // Only a recorded lower-priority code may say that.
        val lower = day().let { t ->
            t.copy(needs = t.needs.map { if (it.needKey == "review") it.copy(finalReasonCodes = listOf("selection.not_selected_lower_priority")) else it })
        }
        assertEquals("selection.not_selected_lower_priority", explain(lower).notToday.single { it.needKey == "review" }.whyNot.code)
    }

    @Test
    fun `a waiting need names the real Skill it waits on`() {
        val waiting = explain(day()).notToday.single { it.needKey == "lists" }
        assertEquals("eligibility.blocked_hard_prerequisite", waiting.whyNot.code)
        assertEquals(listOf(SkillMention(pointer, "Pointer dereference"), SkillMention(address, "Adres operatörü")), waiting.whyNot.skills)
        assertEquals(Reconsideration.WHEN_PREREQUISITE_READY, waiting.reconsideration)
        val text = ExplanationCopy.text(waiting.whyNot)
        assertTrue("Pointer dereference" in text && "Adres operatörü" in text, text)
        // With no published name, the reference is shown rather than nothing.
        val unnamed = explain(day(), emptyMap()).notToday.single { it.needKey == "lists" }
        assertTrue(pointer.logicalId in ExplanationCopy.text(unnamed.whyNot))
        // The branch that waited did not stop the day, and the plan says so.
        assertTrue(explain(day()).plan.any { it.code == "independent_branch_available" })
    }

    @Test
    fun `a need nothing serves says so without inventing a code`() {
        val nothing = explain(day()).notToday.single { it.needKey == "words" }
        assertNull(nothing.whyNot.code)
        assertEquals(TraceFact.NO_TASK_FOR_NEED, nothing.whyNot.fact)
        assertEquals(Reconsideration.WHEN_A_TASK_IS_AVAILABLE, nothing.reconsideration)
        // An untrusted candidate is said to be untrusted, which is a code the planner did record.
        val untrusted = day().let { t ->
            t.copy(
                needs = t.needs.map { if (it.needKey == "words") it.copy(finalReasonCodes = listOf("selection.invalid_candidate")) else it },
                candidates = t.candidates + CandidateTrace("c-words", "words", LifecycleStatus.DRAFT, null, 10,
                    CandidateDisposition.INVALID_CANDIDATE, listOf("candidate.untrusted_for_high_stakes_use")),
            )
        }
        assertEquals("candidate.untrusted_for_high_stakes_use", explain(untrusted).notToday.single { it.needKey == "words" }.whyNot.code)
    }

    @Test
    fun `served needs are today's work and never appear as not today`() {
        val view = explain(day())
        assertEquals(listOf("review", "lists", "words"), view.notToday.map { it.needKey })
        assertEquals(listOf(0, 1), view.today.map { it.position })
    }

    @Test
    fun `review due is never forgetting`() {
        val text = ExplanationCopy.text(explain(day()).notToday.single { it.needKey == "review" }.need)
        assertTrue("unuttuğun anlamına gelmez" in text, text)
    }

    @Test
    fun `kept work is explained as kept, not justified again`() {
        val replanned = day().copy(
            generationKind = "replan",
            selected = listOf(entry(0, "earlier", review, preserved = true)) + day().selected.map { it.copy(position = it.position + 1) },
            replan = ReplanRecord(ReplanTrigger.TASK_FINISHED_EARLY, 4, "2026-09-30", listOf(0), listOf(1), 20, 10),
            planReasonCodes = listOf("replan.task_finished_early") + day().planReasonCodes,
        )
        val view = explain(replanned)
        val kept = view.today.first()
        assertTrue(kept.kept)
        assertEquals(TraceFact.KEPT_FROM_EARLIER_VERSION, kept.why.fact)
        assertTrue(kept.supporting.isEmpty())
        assertEquals("replan.task_finished_early", view.plan.first().code)
    }

    @Test
    fun `a replan says why, and an event with no code says only that the plan changed`() {
        fun replanned(trigger: ReplanTrigger, withinDay: Boolean = true) = day().copy(
            generationKind = "replan",
            replan = ReplanRecord(trigger, 4, "2026-09-30", emptyList(), listOf(0), 0, 30),
            planReasonCodes = listOfNotNull(trigger.reasonCode) + day().planReasonCodes,
            invariantChecks = day().invariantChecks + ("day_within_hard_budget" to withinDay),
        )
        assertEquals("replan.capacity_changed", explain(replanned(ReplanTrigger.TODAY_CAPACITY_CHANGED)).plan.first().code)
        val uncoded = explain(replanned(ReplanTrigger.TASK_COMPLETED)).plan.first()
        assertEquals(TraceFact.PLAN_REPLACED, uncoded.fact)
        assertTrue(explain(replanned(ReplanTrigger.TODAY_CAPACITY_CHANGED, withinDay = false)).plan
            .any { it.fact == TraceFact.DAY_OVER_BUDGET_AFTER_KEEPING })
        assertTrue(explain(day()).plan.none { it.fact != null }, "a plan that was not replaced claims a change")
    }

    @Test
    fun `re-entry says absence is not failure or debt, and replays nothing`() {
        val reentered = day().copy(
            generationKind = "reentry",
            planReasonCodes = listOf("replan.return_after_absence", "reentry.return_after_absence", "reentry.absence_not_failure",
                "reentry.absence_not_task_debt", "reentry.stale_plan_not_replayed", "reentry.current_state_regenerated") + day().planReasonCodes,
            reentry = ReentryContext(3, "2026-09-20", "2026-09-30", 10, 4, emptyList(), 0, emptyMap(), emptyMap(), 1, 30),
        )
        val plan = explain(reentered).plan.mapNotNull { it.code }
        assertEquals(listOf("reentry.return_after_absence", "reentry.absence_not_failure", "reentry.absence_not_task_debt",
            "reentry.stale_plan_not_replayed", "reentry.current_state_regenerated", "capacity.source_short_profile",
            "independent_branch_available"), plan)
        // How long the learner was away is recorded, never said.
        explain(reentered).statements.forEach { assertFalse("10" in ExplanationCopy.text(it), ExplanationCopy.text(it)) }
    }

    @Test
    fun `with no plan, another day's plan or an unreadable one, nothing is explained`() {
        listOf(
            PlannerExplanationFacts.NoPlan to ExplanationState.NO_PLAN_YET,
            PlannerExplanationFacts.PlanFromAnotherDay("2026-09-29") to ExplanationState.PLAN_FROM_ANOTHER_DAY,
            PlannerExplanationFacts.Unreadable("the trace does not decode") to ExplanationState.UNREADABLE,
        ).forEach { (facts, state) ->
            val view = PlannerExplanationPresentation.of(facts)
            assertEquals(state, view.state)
            assertTrue(view.statements.isEmpty(), "$state explained something")
        }
    }

    @Test
    fun `the explanation carries no score, percentage, rank or free text`() {
        val forbidden = Regex("score|percent|rank|grade|streak|text|note|comment", RegexOption.IGNORE_CASE)
        listOf(Statement::class.java, TaskExplanation::class.java, NotTodayExplanation::class.java, PlannerExplanationView::class.java)
            .forEach { type ->
                val fields = type.declaredFields.filterNot { it.isSynthetic || java.lang.reflect.Modifier.isStatic(it.modifiers) }.map { it.name }
                assertEquals(emptyList(), fields.filter { forbidden.containsMatchIn(it) }, "${type.simpleName}: $fields")
            }
    }
}
