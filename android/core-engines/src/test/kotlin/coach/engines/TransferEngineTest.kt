package coach.engines

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.ContentOrigin
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatus
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.EvidenceSeverity
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.MonthlyRole
import coach.model.NeedTrigger
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.TemporalUrgency
import coach.model.TransferProfile
import coach.model.UseCeiling
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransferEngineTest {

    private val skill = VersionedRef("skill.test.loops", 1)
    private val objective = VersionedRef("objective.test.loops", 1)

    private fun state(skill: VersionedRef = this.skill, mastery: MasteryAxisState? = MasteryAxisState.CONFIRMED_CURRENT,
                      lifecycle: String = "published", critical: Boolean = false) =
        SkillPlanningState(skill, lifecycle, critical, mastery, RetentionAxis.NOT_YET_EVALUATED, null, "skill_state:$skill#watermark=3")

    private fun item(
        name: String = "transfer", profile: TransferProfile? = TransferProfile.CROSS_TOPIC_CONTEXT,
        roles: Set<MonthlyRole> = setOf(MonthlyRole.CROSS_TOPIC_TRANSFER),
        scope: Set<AssessmentScope> = setOf(AssessmentScope.MONTHLY_CAPABILITY),
        lifecycle: LifecycleStatus = LifecycleStatus.VALIDATED,
    ) = AssessmentItem(
        ref = VersionedRef("item.test.$name", 1), targetObjectives = listOf(objective), targetSkills = listOf(skill),
        requiredSkills = listOf(VersionedRef("skill.test.strings", 1)), evidenceType = "code_reading",
        expectedAnswerOrRubricRef = "key.$name",
        evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, false, "eval/1"),
        allowedTools = AllowedToolsPolicy(emptyList()), independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "transfer_integration", lifecycleStatus = lifecycle, contentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE, scopeEligibility = scope, variantFamilyId = "family.$name",
        deterministicVerification = true, expectedActiveMinutes = 8, blueprintRoles = roles,
        transferProfile = profile, contextFamilyId = profile?.let { "context.strings" },
    )

    private var nextId = 1L

    private fun row(
        resource: VersionedRef? = item().ref, outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        evaluator: EvaluatorStatus = EvaluatorStatus.VERIFIED, independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        contested: Boolean = false, prerequisiteValid: Boolean = true, solutionExposed: Boolean = false,
    ): EvidenceRow {
        val id = nextId++
        return EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_reading",
            outcome = outcome, evaluatorStatus = evaluator, independenceClass = independence, contested = contested,
            quality = null, difficulty = null, variantFamilyId = "family.transfer", resource = resource,
            prerequisiteValid = prerequisiteValid, solutionExposed = solutionExposed)
    }

    @Test
    fun `only a learned Skill on route can be asked for transfer`() {
        assertTrue(TransferEngine.learned(state()))
        assertTrue(TransferEngine.learned(state(lifecycle = "deprecated")))
        // Still being learned or being verified: its own needs come first.
        for (m in MasteryAxisState.entries - MasteryAxisState.CONFIRMED_CURRENT) {
            assertFalse(TransferEngine.learned(state(mastery = m)), "$m")
        }
        assertFalse(TransferEngine.learned(state(mastery = null)))
        assertFalse(TransferEngine.learned(state(lifecycle = "draft")))
    }

    @Test
    fun `a transfer item is one whose context comes from another Topic, for the monthly transfer slot`() {
        assertTrue(TransferEngine.isTransferItem(item()))
        assertTrue(TransferEngine.isTransferItem(item(profile = TransferProfile.INTEGRATED_SYSTEM_CONTEXT)))
        // Declaring the role without a cross-topic context is a claim the structure does not support (`AIV-v0` §16).
        assertFalse(TransferEngine.isTransferItem(item(profile = null)))
        assertFalse(TransferEngine.isTransferItem(item(profile = TransferProfile.NEAR_CONTEXT)))
        assertFalse(TransferEngine.isTransferItem(item(profile = TransferProfile.NOVEL_APPLICATION)))
        assertFalse(TransferEngine.isTransferItem(item(roles = setOf(MonthlyRole.INTEGRATED_APPLICATION))))
        assertFalse(TransferEngine.isTransferItem(item(scope = setOf(AssessmentScope.WEEKLY_BLUEPRINT))))
    }

    @Test
    fun `a profile without a named context is not an item`() {
        assertFailsWith<IllegalArgumentException> { item().copy(contextFamilyId = null) }
    }

    @Test
    fun `trust is the store's lifecycle, never a candidate`() {
        assertTrue(TransferEngine.trusted(item()))
        assertTrue(TransferEngine.trusted(item(lifecycle = LifecycleStatus.TRUSTED)))
        assertFalse(TransferEngine.trusted(item(lifecycle = LifecycleStatus.CANDIDATE)))
        assertFalse(TransferEngine.trusted(item(lifecycle = LifecycleStatus.DRAFT)))
        assertFalse(TransferEngine.trusted(item(lifecycle = LifecycleStatus.INVALIDATED)))
    }

    @Test
    fun `one clean measurement answers the question, positive or negative`() {
        val items = setOf(item().ref)
        assertFalse(TransferEngine.measured(emptyList(), items))
        assertTrue(TransferEngine.measured(listOf(row()), items))
        assertTrue(TransferEngine.measured(listOf(row(outcome = EvidenceOutcome.NEGATIVE)), items))
    }

    @Test
    fun `work that measured something else leaves the question open`() {
        val items = setOf(item().ref)
        val unclean = listOf(
            row(evaluator = EvaluatorStatus.PROVISIONAL),
            row(independence = IndependenceClass.ASSISTED),
            row(contested = true),
            row(prerequisiteValid = false),
            row(solutionExposed = true),
            row(outcome = EvidenceOutcome.INVALID),
            // Evidence from an ordinary item of the same Objective is not a transfer measurement.
            row(resource = VersionedRef("item.test.lesson", 1)),
            row(resource = null),
        )
        for (r in unclean) assertFalse(TransferEngine.measured(listOf(r), items), "$r")
    }

    @Test
    fun `the need opens only for a learned Skill the application layer found open`() {
        val other = VersionedRef("skill.test.other", 1)
        val learning = VersionedRef("skill.test.learning", 1)
        val states = listOf(state(), state(skill = other), state(skill = learning, mastery = MasteryAxisState.DEVELOPING_INDEPENDENT))
        assertTrue(TransferEngine.needs(states, emptySet()).isEmpty())
        // A Skill not learned opens nothing, whatever the content holds.
        assertTrue(TransferEngine.needs(states, setOf(learning)).isEmpty())
        val need = TransferEngine.needs(states, setOf(skill, learning)).single()
        assertEquals("transfer_opportunity:$skill", need.needKey)
        assertEquals(NeedTrigger.TRANSFER_OPPORTUNITY, need.trigger)
        assertEquals(listOf(skill), need.targetSkills)
        assertEquals(Criticality.REQUIRED, need.criticality)
        assertEquals(listOf("skill_state:$skill#watermark=3"), need.sourceStateRefs)
        // An opportunity, not a debt: nothing negative, nothing time-sensitive.
        assertEquals(EvidenceSeverity.NO_NEGATIVE_EVIDENCE, need.evidenceSeverity)
        assertEquals(TemporalUrgency.NOT_TIME_SENSITIVE, need.temporalUrgency)
        assertEquals(ContinuationValue.FRESH_NEW_CONTEXT, need.continuation)
    }

    @Test
    fun `a critical Skill keeps its criticality and the needs are ordered by key`() {
        val critical = VersionedRef("skill.test.a_critical", 1)
        val needs = TransferEngine.needs(listOf(state(), state(skill = critical, critical = true)), setOf(skill, critical))
        assertEquals(listOf(critical, skill), needs.map { it.targetSkills.single() })
        assertEquals(Criticality.CRITICAL_PREREQUISITE, needs.first().criticality)
    }
}
