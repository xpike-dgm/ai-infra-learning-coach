package coach.engines

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BlueprintCodecs
import coach.model.BlueprintEvidenceFact
import coach.model.BlueprintExclusion
import coach.model.BlueprintRole
import coach.model.BlueprintSessionStatus
import coach.model.BlueprintSlotOutcome
import coach.model.ContentOrigin
import coach.model.Criticality
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatus
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceOutcome
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.MonthlyBlueprintCodec
import coach.model.MonthlyReasonCodes
import coach.model.MonthlyRole
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteEligibility
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.SlotItemRefusal
import coach.model.SlotStatus
import coach.model.TaskPurpose
import coach.model.UseCeiling
import coach.model.VersionedRef
import coach.model.WeeklyReasonCodes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MonthlyBlueprintEngineTest {

    private fun skill(name: String) = VersionedRef("skill.test.$name", 1)
    private fun objective(name: String) = VersionedRef("objective.test.$name", 1)

    private val verify = skill("verify")
    private val criticalVerify = skill("critical_verify")
    private val review = skill("review")
    private val criticalReview = skill("critical_review")
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
        state(criticalVerify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, critical = true),
        state(review, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE),
        state(criticalReview, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, critical = true),
        state(progress, MasteryAxisState.DEVELOPING_INDEPENDENT),
        state(stale, MasteryAxisState.DEVELOPING_WITH_SUPPORT),
        state(fresh, null),
        state(repair, MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = "remediation_required"),
        state(criticalBlocker, MasteryAxisState.DEVELOPING_INDEPENDENT, critical = true),
    )

    private val profiles = mutableMapOf<VersionedRef, ObjectiveEvidenceProfile>()

    private fun item(
        name: String, target: VersionedRef, roles: Set<MonthlyRole> = MonthlyRole.entries.toSet(),
        lifecycle: LifecycleStatus = LifecycleStatus.VALIDATED, family: String = "fam.$name",
        required: List<VersionedRef> = emptyList(),
        scope: Set<AssessmentScope> = setOf(AssessmentScope.MONTHLY_CAPABILITY),
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
            deterministicVerification = true, expectedActiveMinutes = 25, blueprintRoles = roles,
        )
    }

    private fun decision(item: AssessmentItem) =
        PrerequisiteDecision(item.ref.toString(), item.targetSkills.first(), PrerequisiteEligibility.ELIGIBLE, emptyList(), emptyList(),
            emptyList(), emptyList(), emptyList(), emptyList(), false, "PRG-v0", emptyList(), emptyList())

    private fun pool(recent: Set<VersionedRef>? = setOf(progress), holdingBack: Set<VersionedRef> = emptySet(),
                     ownerNeeds: List<LearningNeed> = emptyList()) =
        MonthlyBlueprintEngine.targetPool(states, ownerNeeds, recent, holdingBack)

    private fun compose(items: List<AssessmentItem>, holdingBack: Set<VersionedRef> = emptySet(), previousCycle: String? = null) =
        MonthlyBlueprintEngine.compose(
            cycleId = "2026-10", studyDay = "2026-10-01", curriculumVersion = 1, truthWatermark = 50,
            pool = pool(holdingBack = holdingBack),
            items = items.groupBy { it.targetSkills.first() }, profiles = profiles,
            decisions = items.associate { it.ref to decision(it) }, exposures = emptyList(), evaluatorAvailable = false,
            recentSince = "2026-09-02", previousCycleId = previousCycle, priorSessionId = 4,
        )

    private fun evidence(id: Long, obj: VersionedRef, outcome: EvidenceOutcome, status: EvaluatorStatus = EvaluatorStatus.VERIFIED,
                         independence: IndependenceClass = IndependenceClass.INDEPENDENT) =
        BlueprintEvidenceFact(id, obj, outcome, status, independence, false)

    @Test
    fun `the pool comes from state, one role per Skill, in section 7 order`() {
        val pool = pool(holdingBack = setOf(criticalBlocker))
        assertEquals(
            listOf(
                verify to MonthlyRole.PERSISTENT_WEAKNESS_OR_VERIFICATION,
                // Inside one role the planner's own band decides: an open verification comes before a review,
                // and a review before work that is only holding a branch back.
                criticalVerify to MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION,
                criticalReview to MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION,
                criticalBlocker to MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION,
                progress to MonthlyRole.LONGITUDINAL_REQUIRED_CAPABILITY,
                review to MonthlyRole.DELAYED_RETENTION_SAMPLING,
            ),
            pool.entries.map { it.skill to it.role },
        )
        val excluded = pool.exclusions.associate { it.skill to it.reason }
        assertEquals(BlueprintExclusion.NOT_TAUGHT_YET, excluded[fresh])
        assertEquals(BlueprintExclusion.NOT_ACTIVE_SINCE_LAST_CYCLE, excluded[stale])
        // Open remediation is repaired first and measured under no monthly role.
        assertTrue(pool.entries.none { it.skill == repair })
        assertTrue(pool.exclusions.filter { it.skill == repair }.all { it.reason == BlueprintExclusion.REMEDIATION_OPEN })
    }

    @Test
    fun `a critical Skill is revalidated only for a reason, never because it is critical`() {
        // In progress and holding nothing back: not a revalidation, and not recent either.
        val without = pool(recent = setOf(progress))
        assertTrue(without.entries.none { it.skill == criticalBlocker })
        assertEquals(BlueprintExclusion.NOT_ACTIVE_SINCE_LAST_CYCLE, without.exclusions.single { it.skill == criticalBlocker }.reason)
        // Recent progress alone makes it a longitudinal sample, still not a revalidation.
        val recent = pool(recent = setOf(progress, criticalBlocker))
        assertEquals(MonthlyRole.LONGITUDINAL_REQUIRED_CAPABILITY, recent.entries.single { it.skill == criticalBlocker }.role)
        // A mastered critical Skill with nothing due is not in the month at all.
        val quiet = MonthlyBlueprintEngine.targetPool(
            listOf(state(skill("quiet_critical"), MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.STABLE, critical = true)),
            emptyList(), null, emptySet())
        assertTrue(quiet.entries.isEmpty())
    }

    @Test
    fun `longitudinal sampling is for required capabilities inside the window`() {
        val supporting = skill("supporting")
        val owner = LearningNeed("continue_learning:$supporting", NeedTrigger.CONTINUE_LEARNING, listOf(supporting), Criticality.SUPPORTING)
        val pool = MonthlyBlueprintEngine.targetPool(emptyList(), listOf(owner), null, emptySet())
        assertEquals(BlueprintExclusion.NOT_REQUIRED_CAPABILITY, pool.exclusions.single().reason)
        // With no earlier monthly blueprint every required Skill in progress is inside the window.
        assertTrue(pool(recent = null).entries.any { it.skill == stale && it.role == MonthlyRole.LONGITUDINAL_REQUIRED_CAPABILITY })
    }

    @Test
    fun `owner needs bring integration and the parallel track, transfer and the checkpoint have no producer`() {
        val english = skill("english_errors")
        val integration = skill("integration")
        val pool = MonthlyBlueprintEngine.targetPool(emptyList(), listOf(
            LearningNeed("parallel_track_due:$english", NeedTrigger.PARALLEL_TRACK_DUE, listOf(english), Criticality.REQUIRED,
                track = WeeklyBlueprintEngine.ENGLISH_TRACK),
            LearningNeed("integration_opportunity:$integration", NeedTrigger.INTEGRATION_OPPORTUNITY, listOf(integration), Criticality.REQUIRED),
            LearningNeed("diagnostic_opportunity:$integration", NeedTrigger.DIAGNOSTIC_OPPORTUNITY, listOf(integration), Criticality.REQUIRED),
            LearningNeed("parallel_track_due:other", NeedTrigger.PARALLEL_TRACK_DUE, listOf(skill("other")), Criticality.REQUIRED, track = "other"),
        ), null, emptySet())
        assertEquals(listOf(MonthlyRole.INTEGRATED_APPLICATION, MonthlyRole.PARALLEL_TECHNICAL_ENGLISH), pool.entries.map { it.role })
        assertEquals(listOf(BlueprintExclusion.NOT_A_MONTHLY_MEASUREMENT, BlueprintExclusion.NOT_A_MONTHLY_MEASUREMENT),
            pool.exclusions.map { it.reason })
        // No state today opens a transfer or a professional checkpoint slot, so none is ever invented.
        val everything = pool(recent = null, holdingBack = setOf(criticalBlocker)).entries.map { it.role }
        assertFalse(MonthlyRole.CROSS_TOPIC_TRANSFER in everything)
        assertFalse(MonthlyRole.PROFESSIONAL_EVIDENCE_CHECKPOINT in everything)
        assertTrue(MonthlyBlueprintEngine.targetPool(emptyList(), emptyList(), null, emptySet()).entries.isEmpty())
    }

    @Test
    fun `slots take only items eligible for the monthly scope and declared for the monthly role`() {
        val weeklyOnly = item("weekly_only", verify, scope = setOf(AssessmentScope.WEEKLY_BLUEPRINT))
        val wrongRole = item("wrong_role", review, roles = setOf(MonthlyRole.INTEGRATED_APPLICATION))
        val good = item("good", progress)
        val blueprint = compose(listOf(weeklyOnly, wrongRole, good))
        assertEquals(AssessmentScope.MONTHLY_CAPABILITY, blueprint.scope)
        assertEquals(SlotStatus.NO_VALID_ITEM, blueprint.slots.single { it.targetSkill == verify }.status)
        // A weekly-only item is not even indexed for a monthly slot.
        assertTrue(blueprint.slots.single { it.targetSkill == verify }.rejections.isEmpty())
        assertEquals(listOf(SlotItemRefusal.ROLE_NOT_DECLARED.id),
            blueprint.slots.single { it.targetSkill == review }.rejections.single().reasons)
        assertEquals(good.ref, blueprint.slots.single { it.targetSkill == progress }.item)
        assertTrue(MonthlyReasonCodes.NO_VALID_ITEM in blueprint.reasonCodes)
        assertTrue(blueprint.reasonCodes.none { it.startsWith("assessment.weekly.") })
        assertTrue(blueprint.slots.flatMap { it.reasonCodes }.none { it.startsWith("assessment.weekly.") })
    }

    @Test
    fun `the blueprint names its prior session and a missed month leaves nothing behind`() {
        val blueprint = compose(listOf(item("progress", progress)), previousCycle = "2026-08")
        assertEquals(4L, blueprint.priorSessionId)
        assertEquals("MCA-v0", blueprint.policyVersion)
        assertEquals(listOf(MonthlyReasonCodes.DUE, MonthlyReasonCodes.BLUEPRINT_GENERATED, MonthlyReasonCodes.NO_VALID_ITEM,
            MonthlyReasonCodes.NO_EXAM_DEBT), blueprint.reasonCodes)
        assertEquals(blueprint, MonthlyBlueprintCodec.decode(BlueprintCodecs.encode(blueprint)))
    }

    @Test
    fun `slots are offered as the planner's own needs, with the monthly id and format`() {
        val blueprint = compose(listOf(item("review", review), item("progress", progress)))
        val candidates = MonthlyBlueprintEngine.slotCandidates(blueprint, emptySet())
        assertEquals(blueprint.readySlots.map { MonthlyBlueprintEngine.candidateId("2026-10", it.slotId) }, candidates.map { it.id })
        assertTrue(candidates.all { it.id.startsWith("monthly:2026-10:") && it.generationVersion == "monthly_blueprint/1:2026-10" })
        assertTrue(candidates.all { it.atomicEvidenceBoundary && it.activityKind == "assessment_session" })
        assertEquals(TaskPurpose.RETAIN, candidates.single { it.primarySkill == review }.purpose)
        assertEquals(blueprint.readySlots.map { it.needKey }, candidates.map { it.needKey })
        val served = MonthlyBlueprintEngine.slotCandidates(blueprint, setOf(blueprint.readySlots.first().item!!))
        assertEquals(candidates.drop(1), served)
    }

    @Test
    fun `revalidation lists name only clean independent positives of their own role`() {
        val blueprint = compose(listOf(item("crit", criticalReview), item("review", review), item("progress", progress)))
        val (c, r, p) = listOf(criticalReview, review, progress).map { s -> blueprint.slots.single { it.targetSkill == s }.slotId }
        val result = MonthlyBlueprintEngine.result(blueprint, 31, listOf(
            BlueprintSlotOutcome(c, true, 1, listOf(evidence(1, objective("critical_review"), EvidenceOutcome.POSITIVE))),
            BlueprintSlotOutcome(r, true, 2, listOf(evidence(2, objective("review"), EvidenceOutcome.POSITIVE, independence = IndependenceClass.ASSISTED))),
            BlueprintSlotOutcome(p, true, 3, listOf(evidence(3, objective("progress"), EvidenceOutcome.POSITIVE))),
        ), emptyList())
        assertEquals(BlueprintSessionStatus.COMPLETE, result.sessionStatus)
        assertEquals(listOf(criticalReview), result.revalidatedCriticalSkills)
        // Assisted retrieval revalidates nothing; it asks for an independent recheck.
        assertTrue(result.revalidatedRetentionSkills.isEmpty())
        assertEquals(listOf(objective("review")), result.assistanceRecheckObjectives)
        // A longitudinal positive is evidence, not a revalidation.
        assertEquals(listOf(objective("critical_review"), objective("progress")), result.verifiedPositiveObjectives)
        assertEquals("MCA-v0", result.assessmentPolicyVersion)
        assertTrue(MonthlyReasonCodes.CAPABILITY_EVIDENCE_RECORDED in result.reasonCodes)
        assertTrue(result.reasonCodes.none { it in WeeklyReasonCodes.CATALOG })
    }

    @Test
    fun `a clean negative on a critical Skill revalidates nothing and is not a verdict`() {
        val blueprint = compose(listOf(item("crit", criticalReview), item("review", review)))
        val (c, r) = listOf(criticalReview, review).map { s -> blueprint.slots.single { it.targetSkill == s }.slotId }
        val result = MonthlyBlueprintEngine.result(blueprint, 32, listOf(
            BlueprintSlotOutcome(c, true, 1, listOf(evidence(1, objective("critical_review"), EvidenceOutcome.NEGATIVE))),
            BlueprintSlotOutcome(r, false),
        ), emptyList())
        assertTrue(result.revalidatedCriticalSkills.isEmpty())
        assertEquals(listOf(objective("critical_review")), result.verifiedNegativeObjectives)
        assertEquals(BlueprintSessionStatus.PARTIAL, result.sessionStatus)
        assertTrue(MonthlyReasonCodes.INCOMPLETE_NOT_FAILURE in result.reasonCodes)
        assertTrue(MonthlyReasonCodes.PARTIAL_SESSION in result.reasonCodes)
        assertFalse(MonthlyReasonCodes.REPLAN_AFTER_RESULT in result.reasonCodes)
    }

    @Test
    fun `integrated evidence is listed by Objective, provisional evidence is not`() {
        val integration = skill("integration")
        val blueprint = MonthlyBlueprintEngine.compose(
            cycleId = "2026-10", studyDay = "2026-10-01", curriculumVersion = 1, truthWatermark = 50,
            pool = MonthlyBlueprintEngine.targetPool(emptyList(), listOf(LearningNeed("integration_opportunity:$integration",
                NeedTrigger.INTEGRATION_OPPORTUNITY, listOf(integration), Criticality.REQUIRED)), null, emptySet()),
            items = mapOf(integration to listOf(item("int", integration))), profiles = profiles,
            decisions = mapOf(VersionedRef("item.test.int", 1) to decision(item("int", integration))), exposures = emptyList(),
            evaluatorAvailable = false, recentSince = null,
        )
        val slot = blueprint.readySlots.single().slotId
        val result = MonthlyBlueprintEngine.result(blueprint, 33, listOf(BlueprintSlotOutcome(slot, true, 1, listOf(
            evidence(1, objective("integration"), EvidenceOutcome.PARTIAL),
            evidence(2, objective("other"), EvidenceOutcome.POSITIVE, status = EvaluatorStatus.PROVISIONAL),
        ))), emptyList())
        assertEquals(listOf(objective("integration")), result.integratedEvidenceObjectives)
        assertEquals(listOf(2L), result.provisionalEvidenceIds)
        assertTrue(result.transferEvidenceObjectives.isEmpty())
    }

    @Test
    fun `the monthly engine recomposes only a monthly blueprint, and the weekly engine only a weekly one`() {
        val first = item("first", progress, family = "fam.p")
        val next = item("next", progress, family = "fam.q")
        val blueprint = compose(listOf(first))
        val slot = blueprint.readySlots.single().slotId
        val recomposed = MonthlyBlueprintEngine.recompose(blueprint, 40, setOf(slot), mapOf(progress to listOf(first, next)), profiles,
            listOf(first, next).associate { it.ref to decision(it) }, emptyList(), false)
        assertEquals(next.ref, recomposed.readySlots.single().item)
        assertEquals(40L, recomposed.supersedesSessionId)
        assertEquals(4L, recomposed.priorSessionId)
        assertTrue(MonthlyReasonCodes.INVALID_ITEM_REPLACED in recomposed.readySlots.single().reasonCodes)
        assertFailsWith<IllegalArgumentException> {
            WeeklyBlueprintEngine.recompose(blueprint, 40, setOf(slot), emptyMap(), profiles, emptyMap(), emptyList(), false)
        }
        assertTrue(BlueprintRole.entries.none { it in blueprint.slots.map { s -> s.role } })
    }

    @Test
    fun `the same inputs always compose the same blueprint`() {
        val items = listOf(item("verify", verify), item("review", review), item("progress", progress), item("crit", criticalReview))
        assertEquals(compose(items), compose(items.reversed()))
    }
}
