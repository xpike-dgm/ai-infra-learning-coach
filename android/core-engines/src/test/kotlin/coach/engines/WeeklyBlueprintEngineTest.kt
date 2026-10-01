package coach.engines

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BlueprintRole
import coach.model.ContentOrigin
import coach.model.Criticality
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatus
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceOutcome
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteEligibility
import coach.model.PriorityBand
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.SlotStatus
import coach.model.TaskPurpose
import coach.model.UseCeiling
import coach.model.VersionedRef
import coach.model.WeeklyEvidenceFact
import coach.model.WeeklyExclusion
import coach.model.WeeklyItemRefusal
import coach.model.WeeklyReasonCodes
import coach.model.WeeklySessionStatus
import coach.model.WeeklySlotOutcome
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WeeklyBlueprintEngineTest {

    private fun skill(name: String) = VersionedRef("skill.test.$name", 1)
    private fun objective(name: String) = VersionedRef("objective.test.$name", 1)

    private val verify = skill("verify")
    private val review = skill("review")
    private val progress = skill("progress")
    private val stale = skill("stale")
    private val fresh = skill("fresh")
    private val repair = skill("repair")
    private val criticalBlocker = skill("critical_blocker")

    private fun state(skill: VersionedRef, mastery: MasteryAxisState?, retention: RetentionAxis = RetentionAxis.NOT_YET_EVALUATED,
                      critical: Boolean = false, weakness: String? = null) =
        SkillPlanningState(skill, "published", critical, mastery, retention, weakness, "skill_state:$skill#watermark=9")

    private val states = listOf(
        state(verify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE),
        state(review, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE),
        state(progress, MasteryAxisState.DEVELOPING_INDEPENDENT),
        state(stale, MasteryAxisState.DEVELOPING_WITH_SUPPORT),
        state(fresh, null),
        state(repair, MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = "remediation_required"),
        state(criticalBlocker, MasteryAxisState.DEVELOPING_INDEPENDENT, critical = true),
    )

    private val profiles = mutableMapOf<VersionedRef, ObjectiveEvidenceProfile>()

    private fun item(
        name: String, target: VersionedRef, roles: Set<BlueprintRole> = BlueprintRole.entries.toSet(),
        lifecycle: LifecycleStatus = LifecycleStatus.VALIDATED, minutes: Int? = 10, family: String = "fam.$name",
        group: String? = null, required: List<VersionedRef> = emptyList(), deterministic: Boolean = true,
        scope: Set<AssessmentScope> = setOf(AssessmentScope.WEEKLY_BLUEPRINT),
    ): AssessmentItem {
        val obj = objective(target.logicalId.substringAfterLast('.'))
        profiles[obj] = ObjectiveEvidenceProfile(obj, listOf("code_reading"), listOf("code_reading"))
        return AssessmentItem(
            ref = VersionedRef("item.test.$name", 1), targetObjectives = listOf(obj), targetSkills = listOf(target),
            requiredSkills = required, evidenceType = "code_reading", expectedAnswerOrRubricRef = "key.$name",
            evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, false, "eval/1"),
            allowedTools = AllowedToolsPolicy(listOf("compiler")), independenceMode = IndependenceMode.H0_REQUIRED,
            difficultyClass = "standard_application", lifecycleStatus = lifecycle, contentOrigin = ContentOrigin.HUMAN_AUTHORED,
            declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE, scopeEligibility = scope, variantFamilyId = family,
            dependencyGroupId = group, deterministicVerification = deterministic, expectedActiveMinutes = minutes,
            blueprintRoles = roles,
        )
    }

    private fun decision(item: AssessmentItem, eligibility: PrerequisiteEligibility = PrerequisiteEligibility.ELIGIBLE) =
        PrerequisiteDecision(item.ref.toString(), item.targetSkills.first(), eligibility, emptyList(), emptyList(), emptyList(),
            emptyList(), emptyList(), emptyList(), false, "PRG-v0", emptyList(), emptyList())

    private fun compose(
        items: List<AssessmentItem>,
        decisions: Map<VersionedRef, PrerequisiteDecision> = items.associate { it.ref to decision(it) },
        exposures: List<ExposureFact> = emptyList(),
        recent: Set<VersionedRef>? = setOf(progress),
        holdingBack: Set<VersionedRef> = emptySet(),
        ownerNeeds: List<LearningNeed> = emptyList(),
        previousCycle: String? = null,
    ) = WeeklyBlueprintEngine.compose(
        cycleId = "2026-W40", studyDay = "2026-10-01", curriculumVersion = 1, truthWatermark = 50,
        pool = WeeklyBlueprintEngine.targetPool(states, ownerNeeds, recent, holdingBack),
        items = items.groupBy { it.targetSkills.first() }, profiles = profiles, decisions = decisions,
        exposures = exposures, evaluatorAvailable = false, recentSince = "2026-09-24", previousCycleId = previousCycle,
    )

    @Test
    fun `the pool comes from state, one role per Skill, in section 9 order`() {
        val pool = WeeklyBlueprintEngine.targetPool(states, emptyList(), setOf(progress), setOf(criticalBlocker))
        assertEquals(
            listOf(verify to BlueprintRole.WEAKNESS_OR_VERIFICATION,
                criticalBlocker to BlueprintRole.CRITICAL_PREREQUISITE_CONFIDENCE,
                progress to BlueprintRole.RECENT_REQUIRED_PROGRESS,
                review to BlueprintRole.RETENTION_DUE),
            pool.entries.map { it.skill to it.role },
        )
        val excluded = pool.exclusions.associate { it.skill to it.reason }
        assertEquals(WeeklyExclusion.NOT_TAUGHT_YET, excluded[fresh])
        assertEquals(WeeklyExclusion.NOT_ACTIVE_SINCE_LAST_CYCLE, excluded[stale])
        // Open remediation is repaired first: the Skill is measured under no role this week, even though
        // it is also in progress and recent.
        val recentRepair = WeeklyBlueprintEngine.targetPool(states, emptyList(), setOf(progress, repair), emptySet())
        assertEquals(listOf(WeeklyExclusion.REMEDIATION_OPEN, WeeklyExclusion.REMEDIATION_OPEN),
            recentRepair.exclusions.filter { it.skill == repair }.map { it.reason })
        assertTrue(recentRepair.entries.none { it.skill == repair })
    }

    @Test
    fun `a Skill due both for verification and review is measured once, under verification`() {
        val both = listOf(state(verify, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.VERIFICATION_DUE),
            state(review, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE))
        val owner = LearningNeed("parallel_track_due:$verify", NeedTrigger.PARALLEL_TRACK_DUE, listOf(verify), Criticality.REQUIRED,
            track = WeeklyBlueprintEngine.ENGLISH_TRACK)
        val pool = WeeklyBlueprintEngine.targetPool(both, listOf(owner), null, emptySet())
        assertEquals(BlueprintRole.WEAKNESS_OR_VERIFICATION, pool.entries.single { it.skill == verify }.role)
        assertEquals(WeeklyExclusion.MEASURED_IN_ANOTHER_ROLE, pool.exclusions.single { it.skill == verify }.reason)
    }

    @Test
    fun `with no earlier blueprint every Skill in progress is recent`() {
        val pool = WeeklyBlueprintEngine.targetPool(states, emptyList(), null, emptySet())
        assertTrue(pool.entries.any { it.skill == stale && it.role == BlueprintRole.RECENT_REQUIRED_PROGRESS })
    }

    @Test
    fun `a critical Skill in progress is a confidence slot only when it really holds work back`() {
        val without = WeeklyBlueprintEngine.targetPool(states, emptyList(), setOf(progress), emptySet())
        assertTrue(without.entries.none { it.role == BlueprintRole.CRITICAL_PREREQUISITE_CONFIDENCE })
        assertEquals(WeeklyExclusion.NOT_ACTIVE_SINCE_LAST_CYCLE, without.exclusions.single { it.skill == criticalBlocker }.reason)
    }

    @Test
    fun `owner needs bring the parallel track and integration, never a quota`() {
        val english = skill("english_errors")
        val integration = skill("integration")
        val pool = WeeklyBlueprintEngine.targetPool(emptyList(), listOf(
            LearningNeed("parallel_track_due:$english", NeedTrigger.PARALLEL_TRACK_DUE, listOf(english), Criticality.REQUIRED,
                track = WeeklyBlueprintEngine.ENGLISH_TRACK),
            LearningNeed("integration_opportunity:$integration", NeedTrigger.INTEGRATION_OPPORTUNITY, listOf(integration), Criticality.REQUIRED),
            LearningNeed("reinforcement_opportunity:$integration", NeedTrigger.REINFORCEMENT_OPPORTUNITY, listOf(integration), Criticality.REQUIRED),
        ), null, emptySet())
        assertEquals(listOf(BlueprintRole.INTEGRATION_OR_TRANSFER, BlueprintRole.PARALLEL_ENGLISH), pool.entries.map { it.role })
        assertEquals(WeeklyExclusion.NOT_A_WEEKLY_MEASUREMENT, pool.exclusions.single().reason)
        // A week with nothing due holds nothing: no role is forced in.
        assertTrue(WeeklyBlueprintEngine.targetPool(emptyList(), emptyList(), null, emptySet()).entries.isEmpty())
    }

    @Test
    fun `each slot takes the first item the store trusts, the gate allows and the learner has not seen`() {
        val blueprint = compose(listOf(
            item("verify", verify), item("review", review), item("progress", progress),
        ))
        assertTrue(blueprint.slots.all { it.status == SlotStatus.READY })
        assertEquals(30, blueprint.expectedActiveMinutes)
        assertEquals(listOf(WeeklyReasonCodes.DUE, WeeklyReasonCodes.BLUEPRINT_GENERATED), blueprint.reasonCodes)
        // Verification is repair work, so the session is not closed without it; recent progress is welcome but optional.
        assertTrue(blueprint.slots.single { it.targetSkill == verify }.requiredForSessionClosure)
        assertFalse(blueprint.slots.single { it.targetSkill == progress }.requiredForSessionClosure)
        assertEquals(listOf("block-weakness_or_verification", "block-recent_required_progress", "block-retention_due"),
            blueprint.blocks.map { it.id })
    }

    @Test
    fun `every item refusal names a rule and leaves the slot open rather than weakened`() {
        val cases = mapOf(
            WeeklyItemRefusal.ROLE_NOT_DECLARED to item("r1", verify, roles = setOf(BlueprintRole.RETENTION_DUE)),
            WeeklyItemRefusal.NOT_USABLE_FOR_INTENT to item("r2", verify, lifecycle = LifecycleStatus.CANDIDATE),
            WeeklyItemRefusal.EXPECTED_MINUTES_MISSING to item("r3", verify, minutes = null),
        )
        cases.forEach { (refusal, candidate) ->
            val slot = compose(listOf(candidate)).slots.single { it.targetSkill == verify }
            assertEquals(SlotStatus.NO_VALID_ITEM, slot.status, refusal.id)
            assertTrue(refusal.id in slot.rejections.single().reasons, refusal.id)
            assertTrue(WeeklyReasonCodes.NO_VALID_ITEM in slot.reasonCodes)
        }
    }

    @Test
    fun `the gate fails closed and a waiting item never measures`() {
        val waiting = item("wait", verify)
        val blocked = compose(listOf(waiting), decisions = mapOf(waiting.ref to decision(waiting, PrerequisiteEligibility.BLOCKED)))
        assertEquals(listOf(WeeklyItemRefusal.PREREQUISITE_WAITS.id), blocked.slots.first().rejections.single().reasons)
        val unanswered = compose(listOf(waiting), decisions = emptyMap())
        assertEquals(SlotStatus.NO_VALID_ITEM, unanswered.slots.first().status)
        val conditional = compose(listOf(waiting), decisions = mapOf(waiting.ref to decision(waiting, PrerequisiteEligibility.CONDITIONAL_ELIGIBLE)))
        assertEquals(SlotStatus.READY, conditional.slots.first().status)
    }

    @Test
    fun `a seen item or a solved family is never a fresh measurement`() {
        val seen = item("seen", verify, family = "fam.shared")
        val other = item("other", verify, family = "fam.shared")
        val alreadySeen = compose(listOf(seen), exposures = listOf(ExposureFact(seen.ref, "fam.shared", ExposureFact.ITEM_VERSION_SEEN)))
        assertEquals(listOf(WeeklyItemRefusal.ALREADY_SEEN.id), alreadySeen.slots.first().rejections.single().reasons)
        // A solution shown for one item contaminates its near variants too (QAB-v0 §24).
        val solved = compose(listOf(other), exposures = listOf(ExposureFact(seen.ref, "fam.shared", ExposureFact.SOLUTION_EXPOSURE)))
        assertEquals(listOf(WeeklyItemRefusal.SOLUTION_EXPOSED.id), solved.slots.first().rejections.single().reasons)
    }

    @Test
    fun `near variants and one dependency group never measure two slots`() {
        val a = item("a", verify, family = "fam.same", group = "g")
        val b = item("b", review, family = "fam.same")
        val c = item("c", progress, family = "fam.c", group = "g")
        val blueprint = compose(listOf(a, b, c))
        assertEquals(SlotStatus.READY, blueprint.slots.single { it.targetSkill == verify }.status)
        assertEquals(listOf(WeeklyItemRefusal.VARIANT_FAMILY_IN_USE.id), blueprint.slots.single { it.targetSkill == review }.rejections.single().reasons)
        assertEquals(listOf(WeeklyItemRefusal.DEPENDENCY_GROUP_IN_USE.id), blueprint.slots.single { it.targetSkill == progress }.rejections.single().reasons)
    }

    @Test
    fun `the read per slot is bounded after the indexed facets, and better-validated items come first`() {
        val many = (1..7).map { item("m$it", verify, lifecycle = LifecycleStatus.VALIDATED) }
        val exposures = many.map { ExposureFact(it.ref, it.variantFamilyId, ExposureFact.ITEM_VERSION_SEEN) }
        val slot = compose(many, exposures = exposures).slots.single { it.targetSkill == verify }
        assertEquals(WeeklyBlueprintEngine.MAX_ITEMS_PER_SLOT_V0, slot.rejections.size)
        val trusted = item("z_trusted", verify, lifecycle = LifecycleStatus.TRUSTED)
        assertEquals(trusted.ref, compose(listOf(item("a_valid", verify), trusted)).slots.single { it.targetSkill == verify }.item)
        // Not eligible for the weekly scope at all: not even read.
        val dailyOnly = item("daily", verify, scope = setOf(AssessmentScope.DAILY_MICRO))
        assertTrue(compose(listOf(dailyOnly)).slots.single { it.targetSkill == verify }.rejections.isEmpty())
    }

    @Test
    fun `a week that passed leaves nothing behind`() {
        assertTrue(WeeklyReasonCodes.NO_EXAM_DEBT in compose(emptyList(), previousCycle = "2026-W38").reasonCodes)
        assertFalse(WeeklyReasonCodes.NO_EXAM_DEBT in compose(emptyList()).reasonCodes)
        val empty = WeeklyBlueprintEngine.compose("2026-W40", "2026-10-01", 1, 1,
            WeeklyBlueprintEngine.targetPool(emptyList(), emptyList(), null, emptySet()), emptyMap(), emptyMap(), emptyMap(), emptyList(), false, null)
        assertTrue(WeeklyReasonCodes.NO_ELIGIBLE_TARGET in empty.reasonCodes)
        assertEquals(0, empty.expectedActiveMinutes)
    }

    @Test
    fun `slots are offered to the planner for the needs it already opened, and never twice`() {
        val blueprint = compose(listOf(item("verify", verify), item("review", review)))
        val candidates = WeeklyBlueprintEngine.slotCandidates(blueprint, emptySet())
        assertEquals(listOf("verification_due:$verify", "retention_review_due:$review"), candidates.map { it.needKey })
        assertEquals(listOf(TaskPurpose.ASSESS, TaskPurpose.RETAIN), candidates.map { it.purpose })
        assertTrue(candidates.all { it.atomicEvidenceBoundary && !it.splittable && it.validationStatus == LifecycleStatus.VALIDATED })
        val served = WeeklyBlueprintEngine.slotCandidates(blueprint, setOf(VersionedRef("item.test.verify", 1)))
        assertEquals(listOf("retention_review_due:$review"), served.map { it.needKey })
    }

    @Test
    fun `slot bands are the planner's own bands`() {
        val blueprint = compose(listOf(item("verify", verify), item("review", review), item("progress", progress)))
        assertEquals(PriorityBand.P1, blueprint.slots.single { it.targetSkill == verify }.band)
        assertEquals(PriorityBand.P3, blueprint.slots.single { it.targetSkill == review }.band)
    }

    private fun evidence(id: Long, obj: VersionedRef, outcome: EvidenceOutcome, status: EvaluatorStatus = EvaluatorStatus.VERIFIED,
                         independence: IndependenceClass = IndependenceClass.INDEPENDENT, contaminated: Boolean = false) =
        WeeklyEvidenceFact(id, obj, outcome, status, independence, contaminated)

    @Test
    fun `a root prerequisite shown missing in the session contaminates what depends on it and nothing else`() {
        val root = item("root", verify)
        val downstream = item("down", progress, required = listOf(verify))
        val independent = item("indep", review)
        val blueprint = compose(listOf(root, downstream, independent))
        val rootSlot = blueprint.slots.single { it.targetSkill == verify }
        val failed = WeeklyBlueprintEngine.cleanlyFailedSkills(blueprint, listOf(
            WeeklySlotOutcome(rootSlot.slotId, true, 1, listOf(evidence(1, objective("verify"), EvidenceOutcome.NEGATIVE))),
        ))
        assertEquals(setOf(verify), failed)
        assertTrue(WeeklyBlueprintEngine.contaminatedBy(blueprint.slots.single { it.targetSkill == progress }, failed))
        assertFalse(WeeklyBlueprintEngine.contaminatedBy(blueprint.slots.single { it.targetSkill == review }, failed))
        // An assisted or provisional failure is not a clean root failure.
        assertTrue(WeeklyBlueprintEngine.cleanlyFailedSkills(blueprint, listOf(
            WeeklySlotOutcome(rootSlot.slotId, true, 1, listOf(
                evidence(1, objective("verify"), EvidenceOutcome.NEGATIVE, independence = IndependenceClass.ASSISTED),
                evidence(2, objective("verify"), EvidenceOutcome.NEGATIVE, status = EvaluatorStatus.PROVISIONAL),
            ))),
        ).isEmpty())
    }

    @Test
    fun `the result is evidence by Objective, and a skip is not incorrect`() {
        val blueprint = compose(listOf(item("verify", verify), item("review", review), item("progress", progress)))
        val (v, r, p) = listOf(verify, review, progress).map { s -> blueprint.slots.single { it.targetSkill == s }.slotId }
        val result = WeeklyBlueprintEngine.result(blueprint, 11, listOf(
            WeeklySlotOutcome(v, true, 1, listOf(evidence(1, objective("verify"), EvidenceOutcome.POSITIVE))),
            WeeklySlotOutcome(r, true, 2, listOf(evidence(2, objective("review"), EvidenceOutcome.NEGATIVE, independence = IndependenceClass.ASSISTED))),
            WeeklySlotOutcome(p, false),
        ), stateChangeRefs = emptyList())
        assertEquals(WeeklySessionStatus.PARTIAL, result.sessionStatus)
        assertEquals(listOf(objective("verify")), result.verifiedPositiveObjectives)
        // Assisted work is neither a clean negative nor a failure; it asks for an independent recheck.
        assertTrue(result.verifiedNegativeObjectives.isEmpty())
        assertEquals(listOf(objective("review")), result.assistanceRecheckObjectives)
        assertEquals(listOf(p), result.unresolvedSlotIds)
        assertTrue(WeeklyReasonCodes.INCOMPLETE_NOT_FAILURE in result.reasonCodes)
        assertFalse(WeeklyReasonCodes.REPLAN_AFTER_RESULT in result.reasonCodes)
    }

    @Test
    fun `session status follows what was submitted, never a mark`() {
        val blueprint = compose(listOf(item("verify", verify)))
        val slot = blueprint.readySlots.single().slotId
        assertEquals(WeeklySessionStatus.DEFERRED, WeeklyBlueprintEngine.result(blueprint, 1, emptyList(), emptyList()).sessionStatus)
        val complete = WeeklyBlueprintEngine.result(blueprint, 1, listOf(WeeklySlotOutcome(slot, true, 3, listOf(
            evidence(3, objective("verify"), EvidenceOutcome.INVALID),
            evidence(4, objective("verify"), EvidenceOutcome.POSITIVE, contaminated = true),
            evidence(5, objective("verify"), EvidenceOutcome.POSITIVE, status = EvaluatorStatus.PROVISIONAL),
        ))), listOf("mastery:verification_opened"))
        assertEquals(WeeklySessionStatus.COMPLETE, complete.sessionStatus)
        assertEquals(listOf(3L, 4L), complete.invalidOrUnusableEvidenceIds)
        assertEquals(listOf(5L), complete.provisionalEvidenceIds)
        assertTrue(complete.verifiedPositiveObjectives.isEmpty())
        assertEquals(listOf(slot), complete.prerequisiteContaminatedSlotIds)
        assertTrue(WeeklyReasonCodes.REPLAN_AFTER_RESULT in complete.reasonCodes)
    }

    @Test
    fun `recomposition replaces only the named slots and never reuses the replaced item or its family`() {
        val first = item("first", verify, family = "fam.v")
        val sameFamily = item("second", verify, family = "fam.v")
        val newFamily = item("third", verify, family = "fam.w")
        val keep = item("keep", review)
        val blueprint = compose(listOf(first, keep))
        val slot = blueprint.slots.single { it.targetSkill == verify }
        val recomposed = WeeklyBlueprintEngine.recompose(blueprint, 21, setOf(slot.slotId),
            listOf(first, sameFamily, newFamily, keep).groupBy { it.targetSkills.first() }, profiles,
            listOf(first, sameFamily, newFamily, keep).associate { it.ref to decision(it) }, emptyList(), false)
        assertEquals(newFamily.ref, recomposed.slots.single { it.slotId == slot.slotId }.item)
        assertEquals(blueprint.slots.single { it.targetSkill == review }, recomposed.slots.single { it.targetSkill == review })
        assertEquals(21L, recomposed.supersedesSessionId)
        assertTrue(WeeklyReasonCodes.INVALID_ITEM_REPLACED in recomposed.slots.single { it.slotId == slot.slotId }.reasonCodes)
    }

    @Test
    fun `the same inputs always compose the same blueprint`() {
        val items = listOf(item("verify", verify), item("review", review), item("progress", progress))
        assertEquals(compose(items), compose(items.reversed()))
    }
}
