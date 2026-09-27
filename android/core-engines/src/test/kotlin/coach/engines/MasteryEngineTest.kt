package coach.engines

import coach.model.DifficultyClass
import coach.model.EvaluatorStatus
import coach.model.EvidenceExclusion
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.IndependenceClass
import coach.model.MasteryAxisState
import coach.model.ObjectiveGateProfile
import coach.model.SupportBand
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `GRE-v0` as code. The guardrail cases of §18 and §19 are tests here, not prose: each one is a
 * plausible way a product decides someone has learned something when they have not.
 */
class MasteryEngineTest {

    private val objective = VersionedRef("objective.python.loops.trace", 1)
    private val skill = VersionedRef("skill.python.loops", 1)

    private fun profile(
        critical: Boolean = false,
        required: Boolean = true,
        directTypes: List<String> = listOf("code_reading"),
        requiredDirect: String? = null,
        minGroups: Int? = null,
        minFamilies: Int? = null,
        nonBasic: Boolean = false,
        userAuthored: Boolean = false,
        transfer: Boolean = false,
    ) = ObjectiveGateProfile(
        ref = objective,
        required = required,
        critical = critical,
        acceptableEvidenceTypes = listOf("code_reading", "explanation", "authored_code"),
        directEvidenceTypes = directTypes,
        requiredDirectType = requiredDirect,
        minIndependentGroups = minGroups,
        minVariantFamilies = minFamilies,
        requiresNonBasicEvidence = nonBasic,
        requiresUserAuthoredArtifact = userAuthored,
        requiresTransfer = transfer,
    )

    private var nextId = 1L

    private fun row(
        quality: Double? = 1.0,
        outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        evaluator: EvaluatorStatus = EvaluatorStatus.VERIFIED,
        family: String? = "family.a",
        evidenceType: String = "code_reading",
        dependencyGroupId: String? = null,
        difficulty: DifficultyClass? = DifficultyClass.AUTHENTIC_APPLICATION,
        contested: Boolean = false,
        prerequisiteValid: Boolean = true,
        solutionExposed: Boolean = false,
        userAuthored: Boolean = false,
    ): EvidenceRow {
        val id = nextId++
        return EvidenceRow(
            id = id,
            sequence = id,
            objective = objective,
            skill = skill,
            evidenceType = evidenceType,
            outcome = outcome,
            evaluatorStatus = evaluator,
            independenceClass = independence,
            contested = contested,
            quality = quality,
            difficulty = difficulty,
            variantFamilyId = family,
            dependencyGroupId = dependencyGroupId,
            prerequisiteValid = prerequisiteValid,
            solutionExposed = solutionExposed,
            userAuthoredArtifact = userAuthored,
        )
    }

    private fun twoCleanGroups() = listOf(row(family = "family.a"), row(family = "family.b"))

    // ---------------------------------------------------------------- declared constants

    @Test
    fun `the declared cold-start constants are GRE-v0's`() {
        assertEquals(5, MasteryEngine.RECENT_WINDOW_MAX_GROUPS_V0)
        assertEquals(0.80, MasteryEngine.OBJECTIVE_MASTERY_THRESHOLD_V0)
        assertEquals(2, MasteryEngine.MIN_INDEPENDENT_GROUPS_STANDARD)
        assertEquals(3, MasteryEngine.MIN_INDEPENDENT_GROUPS_CRITICAL)
        assertEquals(2, MasteryEngine.MIN_VARIANT_FAMILIES_STANDARD)
        assertEquals("GRE-v0", MasteryEngine.MASTERY_FORMULA_VERSION)
    }

    // ---------------------------------------------------------------- eligibility

    @Test
    fun `only independent, verified, uncontested, prerequisite-valid direct evidence is eligible`() {
        val clean = row()
        assertEquals(emptySet(), MasteryEngine.exclusions(clean, profile()))

        val cases = mapOf(
            EvidenceExclusion.NOT_DIRECT_EVIDENCE_FOR_OBJECTIVE to row(evidenceType = "explanation"),
            EvidenceExclusion.NOT_INDEPENDENT to row(independence = IndependenceClass.ASSISTED),
            EvidenceExclusion.EVALUATOR_NOT_VERIFIED to row(evaluator = EvaluatorStatus.PROVISIONAL),
            EvidenceExclusion.OUTCOME_INVALID to row(outcome = EvidenceOutcome.INVALID),
            EvidenceExclusion.CONTESTED to row(contested = true),
            EvidenceExclusion.PREREQUISITE_CONTAMINATED to row(prerequisiteValid = false),
            EvidenceExclusion.SOLUTION_EXPOSED to row(solutionExposed = true),
            EvidenceExclusion.NO_GROUP_RESULT to row(quality = null),
        )
        cases.forEach { (expected, evidence) ->
            assertTrue(expected in MasteryEngine.exclusions(evidence, profile()), expected.id)
        }
    }

    // ---------------------------------------------------------------- §18 false-positive guardrails

    @Test
    fun `one easy correct answer is not mastery`() {
        val decision = MasteryEngine.decide(profile(), listOf(row()))
        assertFalse(decision.passed)
        assertTrue("min_independent_groups" in decision.failedGates)
        assertEquals(SupportBand.LOW, decision.supportBand)
    }

    @Test
    fun `the same question answered ten times is still one kind of evidence`() {
        // A dependency group is one group however many rows it holds.
        val rows = (1..10).map { row(dependencyGroupId = "group.same_question", family = "family.a") }
        val decision = MasteryEngine.decide(profile(), rows)
        assertEquals(1, decision.independentGroups)
        assertFalse(decision.passed)
        assertTrue("min_variant_families" in decision.failedGates)
    }

    @Test
    fun `five correct answers with help are not independent evidence`() {
        val rows = (1..5).map { row(independence = IndependenceClass.ASSISTED, family = "family.$it") }
        val decision = MasteryEngine.decide(profile(), rows)
        assertNull(decision.recentDirectScore)
        assertFalse(decision.passed)
        assertEquals(5, decision.assistedRowCount)
        assertTrue(decision.excluded.values.all { EvidenceExclusion.NOT_INDEPENDENT in it })
    }

    @Test
    fun `code the AI wrote does not pass an Objective that requires the learner's own artifact`() {
        val rows = listOf(
            row(family = "family.a", userAuthored = false),
            row(family = "family.b", userAuthored = false),
            row(family = "family.c", userAuthored = false),
        )
        val decision = MasteryEngine.decide(profile(userAuthored = true), rows)
        assertFalse(decision.passed)
        assertTrue("user_authored_artifact" in decision.failedGates)

        val withOwnWork = MasteryEngine.decide(profile(userAuthored = true), rows + row(family = "family.d", userAuthored = true))
        assertTrue(withOwnWork.passed)
    }

    @Test
    fun `a wrong answer caused by an untaught prerequisite is excluded, not counted against the learner`() {
        val rows = twoCleanGroups() + row(quality = 0.0, outcome = EvidenceOutcome.NEGATIVE, prerequisiteValid = false)
        val decision = MasteryEngine.decide(profile(), rows)
        assertTrue(decision.passed, "a contaminated failure dragged the score down")
        assertEquals(1.0, decision.recentDirectScore)
    }

    @Test
    fun `a critical Objective cannot be passed on basic evidence alone`() {
        val basic = (1..3).map { row(family = "family.$it", difficulty = DifficultyClass.BASIC) }
        val decision = MasteryEngine.decide(profile(critical = true), basic)
        assertFalse(decision.passed)
        assertTrue("non_basic_evidence" in decision.failedGates)
    }

    @Test
    fun `a critical Objective needs three groups where a standard one needs two`() {
        val two = twoCleanGroups()
        assertTrue(MasteryEngine.decide(profile(), two).passed)
        assertFalse(MasteryEngine.decide(profile(critical = true), two).passed)
        val three = two + row(family = "family.c")
        assertTrue(MasteryEngine.decide(profile(critical = true), three).passed)
    }

    // ---------------------------------------------------------------- score and window

    @Test
    fun `the score is the mean of the last five groups, with no multipliers`() {
        val rows = (1..7).map { index ->
            row(quality = if (index <= 2) 0.0 else 1.0, family = "family.$index")
        }
        val decision = MasteryEngine.decide(profile(), rows)
        // The two zeroes fall out of the window; the five recent groups are all 1.0.
        assertEquals(5, decision.independentGroups)
        assertEquals(1.0, decision.recentDirectScore)
    }

    @Test
    fun `a score below the threshold does not pass, however many groups there are`() {
        val rows = (1..5).map { row(quality = 0.6, family = "family.$it") }
        val decision = MasteryEngine.decide(profile(), rows)
        assertEquals(0.6, decision.recentDirectScore)
        assertFalse(decision.passed)
        assertTrue("recent_direct_score" in decision.failedGates)
    }

    @Test
    fun `a required direct type must actually appear in the window`() {
        val rows = twoCleanGroups()
        assertFalse(MasteryEngine.decide(profile(requiredDirect = "authored_code"), rows).passed)
        val withAuthored = rows + row(evidenceType = "authored_code", family = "family.c")
        assertTrue(
            MasteryEngine.decide(
                profile(directTypes = listOf("code_reading", "authored_code"), requiredDirect = "authored_code"),
                withAuthored,
            ).passed
        )
    }

    // ---------------------------------------------------------------- §16 hysteresis

    @Test
    fun `the first clean contradiction opens verification and does not unmaster`() {
        val rows = twoCleanGroups() + row(quality = 0.0, outcome = EvidenceOutcome.NEGATIVE, family = "family.c")
        val decision = MasteryEngine.decide(profile(), rows, previouslyMastered = true)
        assertTrue(decision.passed, "one contradiction unmastered a confirmed Objective")
        assertTrue(decision.verificationDue)
    }

    @Test
    fun `a failed recheck resolves the verification and lets the gates decide again`() {
        val rows = twoCleanGroups() + row(quality = 0.0, outcome = EvidenceOutcome.NEGATIVE, family = "family.c")
        val decision = MasteryEngine.decide(
            profile(), rows, previouslyMastered = true, unresolvedVerification = true,
        )
        assertFalse(decision.passed)
        assertFalse(decision.verificationDue, "the verification stayed open forever")
    }

    @Test
    fun `an unresolved recheck blocks the Objective without being a negative result`() {
        val decision = MasteryEngine.decide(profile(), twoCleanGroups(), unresolvedRecheck = true)
        assertFalse(decision.passed)
        assertTrue("no_unresolved_recheck" in decision.failedGates)
        assertEquals(1.0, decision.recentDirectScore)
    }

    // ---------------------------------------------------------------- skill aggregation

    private fun decisionFor(profile: ObjectiveGateProfile, rows: List<EvidenceRow>) =
        MasteryEngine.decide(profile, rows)

    @Test
    fun `a Skill is mastered only when every required and critical Objective passes`() {
        val first = profile().copy(ref = VersionedRef("objective.one", 1))
        val second = profile(critical = true).copy(ref = VersionedRef("objective.two", 1))
        val profiles = mapOf(first.ref to first, second.ref to second)

        val strongFirst = decisionFor(first, twoCleanGroups())
        val weakSecond = decisionFor(second, listOf(row(family = "family.x")))
        val notMastered = MasteryEngine.decideSkill(skill, listOf(strongFirst, weakSecond), profiles)
        assertFalse(notMastered.mastered, "a strong Objective covered a missing one")
        assertEquals(MasteryAxisState.DEVELOPING_INDEPENDENT, notMastered.axisState)

        val strongSecond = decisionFor(second, (1..3).map { row(family = "family.$it") })
        val mastered = MasteryEngine.decideSkill(skill, listOf(strongFirst, strongSecond), profiles)
        assertTrue(mastered.mastered)
        assertEquals(MasteryAxisState.CONFIRMED_CURRENT, mastered.axisState)
    }

    @Test
    fun `an optional Objective does not gate the Skill`() {
        val required = profile().copy(ref = VersionedRef("objective.one", 1))
        val optional = profile(required = false).copy(ref = VersionedRef("objective.two", 1))
        val profiles = mapOf(required.ref to required, optional.ref to optional)
        val decision = MasteryEngine.decideSkill(
            skill,
            listOf(decisionFor(required, twoCleanGroups()), decisionFor(optional, emptyList())),
            profiles,
        )
        assertTrue(decision.mastered)
    }

    @Test
    fun `a Skill with no evidence at all is not yet evidenced, not failed`() {
        val only = profile()
        val decision = MasteryEngine.decideSkill(
            skill, listOf(decisionFor(only, emptyList())), mapOf(only.ref to only),
        )
        assertFalse(decision.mastered)
        assertEquals(MasteryAxisState.NOT_YET_EVIDENCED, decision.axisState)
    }

    @Test
    fun `a Skill whose only evidence is assisted is developing with support`() {
        val only = profile()
        val assisted = decisionFor(only, listOf(row(independence = IndependenceClass.ASSISTED)))
        val decision = MasteryEngine.decideSkill(skill, listOf(assisted), mapOf(only.ref to only))
        assertEquals(MasteryAxisState.DEVELOPING_WITH_SUPPORT, decision.axisState)
    }

    @Test
    fun `an unresolved critical recheck keeps a Skill from being mastered`() {
        val critical = profile(critical = true).copy(ref = VersionedRef("objective.two", 1))
        val passing = MasteryEngine.decide(critical, (1..3).map { row(family = "family.$it") })
        val withRecheck = passing.copy(unresolvedRecheck = true)
        val decision = MasteryEngine.decideSkill(skill, listOf(withRecheck), mapOf(critical.ref to critical))
        assertFalse(decision.mastered)
        assertTrue("unresolved_critical_recheck" in decision.trace.reasonCodes)
    }

    // ---------------------------------------------------------------- support band and trace

    @Test
    fun `the support band is a band, and a failing Objective is always low`() {
        assertEquals(SupportBand.LOW, MasteryEngine.decide(profile(), listOf(row())).supportBand)
        assertEquals(SupportBand.MEDIUM, MasteryEngine.decide(profile(), twoCleanGroups()).supportBand)
        val strong = (1..3).map { row(family = "family.$it") }
        assertEquals(SupportBand.HIGH, MasteryEngine.decide(profile(), strong).supportBand)
        assertEquals(listOf("low", "medium", "high"), SupportBand.entries.map { it.id })
    }

    @Test
    fun `every decision explains itself, including what it excluded and why`() {
        val only = profile()
        val rows = twoCleanGroups() + row(independence = IndependenceClass.ASSISTED, family = "family.z")
        val decision = MasteryEngine.decideSkill(
            skill, listOf(decisionFor(only, rows)), mapOf(only.ref to only), allRows = rows,
        )
        val trace = decision.trace
        assertEquals("GRE-v0", trace.masteryFormulaVersion)
        assertEquals("mastered", trace.decision)
        assertTrue(trace.objectiveRecentScores.values.all { it != null })
        assertTrue(trace.windowGroupKeys.values.single().isNotEmpty(), "the trace named no window groups")
        assertTrue(trace.excludedEvidence.values.any { EvidenceExclusion.NOT_INDEPENDENT in it })
        assertEquals(1, trace.assistedEvidenceCount)
        assertEquals(mapOf("verified" to 3), trace.evaluatorStatusSummary)
        assertTrue(trace.passedGates.values.single().contains("recent_direct_score"))
    }

    @Test
    fun `the decision carries no percentage, level or confidence field`() {
        val forbidden = listOf("percent", "level", "confidence", "probability", "grade", "rank", "streak")
        val fields = MasteryEngine.ObjectiveDecision::class.java.declaredFields.map { it.name } +
            MasteryEngine.SkillDecision::class.java.declaredFields.map { it.name } +
            MasteryEngine.MasteryDecisionTrace::class.java.declaredFields.map { it.name }
        forbidden.forEach { word ->
            assertTrue(fields.none { it.contains(word, ignoreCase = true) }, "a decision field names '$word': $fields")
        }
    }
}
