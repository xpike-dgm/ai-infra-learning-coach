package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The item's minimum validity contract, the trust ceiling and evidence fit (11D). */
class AssessmentFactsTest {

    private val objectiveRef = VersionedRef("objective.python.loops.trace", 1)

    private fun item(
        lifecycle: LifecycleStatus = LifecycleStatus.VALIDATED,
        origin: ContentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declared: UseCeiling = UseCeiling.CRITICAL_MASTERY_ELIGIBLE,
        deterministic: Boolean = true,
        requiredStatus: EvaluatorStatusRequirement = EvaluatorStatusRequirement.VERIFIED,
        deterministicRequired: Boolean = false,
        evidenceType: String = "code_reading",
        scopes: Set<AssessmentScope> = setOf(AssessmentScope.DAILY_MICRO),
    ) = AssessmentItem(
        ref = VersionedRef("item.python.loops.q1", 1),
        targetObjectives = listOf(objectiveRef),
        targetSkills = listOf(VersionedRef("skill.python.loops", 1)),
        requiredSkills = emptyList(),
        evidenceType = evidenceType,
        expectedAnswerOrRubricRef = "key://item.python.loops.q1@v1",
        evaluatorRequirement = EvaluatorRequirement(requiredStatus, deterministicRequired, "policy.v1"),
        allowedTools = AllowedToolsPolicy(listOf("documentation")),
        independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "core",
        lifecycleStatus = lifecycle,
        contentOrigin = origin,
        declaredUseCeiling = declared,
        scopeEligibility = scopes,
        variantFamilyId = "family.python.loops.trace",
        deterministicVerification = deterministic,
    )

    private fun profile(
        acceptable: List<String> = listOf("code_reading", "explanation"),
        direct: List<String> = listOf("code_reading"),
        requiredDirect: String? = "code_reading",
    ) = mapOf(objectiveRef to ObjectiveEvidenceProfile(objectiveRef, acceptable, direct, requiredDirect))

    // ---------------------------------------------------------------- vocabularies

    @Test
    fun `vocabularies are their owners' in their order`() {
        assertEquals(listOf("daily_micro", "weekly_blueprint", "monthly_capability"), AssessmentScope.entries.map { it.id })
        assertEquals(
            listOf("checkpoint", "mastery_evidence", "verification", "integration_check"),
            AssessmentIntent.entries.map { it.id },
        )
        assertEquals(
            listOf("draft", "candidate", "validated", "trusted", "deprecated", "invalidated", "retired"),
            LifecycleStatus.entries.map { it.id },
        )
        assertEquals(
            listOf("practice_only", "low_stakes_assessment", "standard_mastery_eligible", "critical_mastery_eligible"),
            UseCeiling.entries.map { it.id },
        )
        assertEquals(
            listOf("human_authored", "parameterized_from_trusted_template", "ai_generated", "mixed_authorship",
                "imported_reference_based"),
            ContentOrigin.entries.map { it.id },
        )
        assertEquals(
            listOf("not_applicable", "guided_allowed", "independent_expected", "h0_required"),
            IndependenceMode.entries.map { it.id },
        )
    }

    @Test
    fun `an item without a target Objective or an answer key cannot be constructed`() {
        assertFailsWith<IllegalArgumentException> { item().copy(targetObjectives = emptyList()) }
        assertFailsWith<IllegalArgumentException> { item().copy(expectedAnswerOrRubricRef = " ") }
        assertFailsWith<IllegalArgumentException> { item().copy(variantFamilyId = "") }
    }

    // ---------------------------------------------------------------- trust ceiling

    @Test
    fun `a draft, invalidated or retired version is not selectable at all`() {
        listOf(LifecycleStatus.DRAFT, LifecycleStatus.INVALIDATED, LifecycleStatus.RETIRED).forEach { status ->
            assertFalse(status.selectable, status.id)
            assertNull(ItemTrust.effectiveCeiling(item(lifecycle = status), evaluatorAvailable = true))
        }
    }

    @Test
    fun `an unvalidated item is practice at best, whatever it declares about itself`() {
        assertEquals(
            UseCeiling.PRACTICE_ONLY,
            ItemTrust.effectiveCeiling(item(lifecycle = LifecycleStatus.CANDIDATE), evaluatorAvailable = true),
        )
    }

    @Test
    fun `an AI-generated item cannot reach the critical ceiling without being trusted`() {
        assertEquals(
            UseCeiling.STANDARD_MASTERY_ELIGIBLE,
            ItemTrust.effectiveCeiling(
                item(lifecycle = LifecycleStatus.VALIDATED, origin = ContentOrigin.AI_GENERATED),
                evaluatorAvailable = true,
            ),
        )
    }

    @Test
    fun `without deterministic verification a provisional evaluator caps the item at low stakes`() {
        assertEquals(
            UseCeiling.LOW_STAKES_ASSESSMENT,
            ItemTrust.effectiveCeiling(
                item(deterministic = false, requiredStatus = EvaluatorStatusRequirement.PROVISIONAL_ALLOWED),
                evaluatorAvailable = true,
            ),
        )
    }

    @Test
    fun `an item whose evaluator cannot run today may still teach but may not measure`() {
        assertEquals(
            UseCeiling.PRACTICE_ONLY,
            ItemTrust.effectiveCeiling(item(deterministic = false), evaluatorAvailable = false),
        )
        // A deterministically checkable item is unaffected by the evaluator being away.
        assertEquals(
            UseCeiling.CRITICAL_MASTERY_ELIGIBLE,
            ItemTrust.effectiveCeiling(item(deterministic = true), evaluatorAvailable = false),
        )
    }

    @Test
    fun `an item that requires deterministic verification and has none is practice only`() {
        assertEquals(
            UseCeiling.PRACTICE_ONLY,
            ItemTrust.effectiveCeiling(
                item(deterministic = false, deterministicRequired = true),
                evaluatorAvailable = true,
            ),
        )
    }

    @Test
    fun `the ceiling never rises above what the item declared`() {
        assertEquals(
            UseCeiling.LOW_STAKES_ASSESSMENT,
            ItemTrust.effectiveCeiling(item(declared = UseCeiling.LOW_STAKES_ASSESSMENT), evaluatorAvailable = true),
        )
    }

    @Test
    fun `mastery-bearing intents need a higher ceiling than checkpoints`() {
        assertEquals(UseCeiling.LOW_STAKES_ASSESSMENT, ItemTrust.required(AssessmentIntent.CHECKPOINT))
        assertEquals(UseCeiling.LOW_STAKES_ASSESSMENT, ItemTrust.required(AssessmentIntent.INTEGRATION_CHECK))
        assertEquals(UseCeiling.STANDARD_MASTERY_ELIGIBLE, ItemTrust.required(AssessmentIntent.MASTERY_EVIDENCE))
        assertEquals(UseCeiling.STANDARD_MASTERY_ELIGIBLE, ItemTrust.required(AssessmentIntent.VERIFICATION))
    }

    // ---------------------------------------------------------------- fit

    @Test
    fun `a validated, deterministic, well-attributed item fits a mastery measurement`() {
        val fit = ItemSelection.fit(item(), AssessmentIntent.MASTERY_EVIDENCE, AssessmentScope.DAILY_MICRO, profile(), true)
        assertEquals(ItemFit.Usable(UseCeiling.CRITICAL_MASTERY_ELIGIBLE), fit)
    }

    @Test
    fun `the Objective decides what counts as evidence for it, not the item`() {
        val fit = ItemSelection.fit(
            item(evidenceType = "recognition"), AssessmentIntent.CHECKPOINT, AssessmentScope.DAILY_MICRO, profile(), true,
        )
        assertIs<ItemFit.NotUsable>(fit)
        assertTrue(ItemUnfit.EVIDENCE_TYPE_NOT_ACCEPTED_BY_OBJECTIVE in fit.reasons)
    }

    @Test
    fun `a mastery measurement must produce the Objective's own direct evidence type`() {
        // `explanation` is accepted by the Objective, but its direct type is `code_reading`.
        val fit = ItemSelection.fit(
            item(evidenceType = "explanation"), AssessmentIntent.MASTERY_EVIDENCE, AssessmentScope.DAILY_MICRO, profile(), true,
        )
        assertIs<ItemFit.NotUsable>(fit)
        assertTrue(ItemUnfit.DIRECT_EVIDENCE_TYPE_REQUIRED in fit.reasons)

        // The same item is fine for a checkpoint, which does not claim mastery.
        assertIs<ItemFit.Usable>(
            ItemSelection.fit(item(evidenceType = "explanation"), AssessmentIntent.CHECKPOINT,
                AssessmentScope.DAILY_MICRO, profile(), true)
        )
    }

    @Test
    fun `an accepted but indirect evidence type cannot carry mastery even with no required direct type`() {
        // The Objective accepts `explanation` and names no single required type, but its direct
        // types are `code_reading` only — so `explanation` still cannot carry mastery evidence.
        val profiles = profile(
            acceptable = listOf("code_reading", "explanation"),
            direct = listOf("code_reading"),
            requiredDirect = null,
        )
        val fit = ItemSelection.fit(
            item(evidenceType = "explanation"), AssessmentIntent.MASTERY_EVIDENCE, AssessmentScope.DAILY_MICRO,
            profiles, true,
        )
        assertIs<ItemFit.NotUsable>(fit)
        assertEquals(setOf(ItemUnfit.DIRECT_EVIDENCE_TYPE_REQUIRED), fit.reasons)

        assertIs<ItemFit.Usable>(
            ItemSelection.fit(item(evidenceType = "explanation"), AssessmentIntent.CHECKPOINT,
                AssessmentScope.DAILY_MICRO, profiles, true)
        )
    }

    @Test
    fun `an unknown Objective profile is unfit rather than assumed compatible`() {
        val fit = ItemSelection.fit(item(), AssessmentIntent.CHECKPOINT, AssessmentScope.DAILY_MICRO, emptyMap(), true)
        assertIs<ItemFit.NotUsable>(fit)
        assertTrue(ItemUnfit.OBJECTIVE_PROFILE_UNKNOWN in fit.reasons)
    }

    @Test
    fun `an item not eligible for this scope is unfit`() {
        val fit = ItemSelection.fit(
            item(scopes = setOf(AssessmentScope.MONTHLY_CAPABILITY)), AssessmentIntent.CHECKPOINT,
            AssessmentScope.DAILY_MICRO, profile(), true,
        )
        assertIs<ItemFit.NotUsable>(fit)
        assertTrue(ItemUnfit.SCOPE_NOT_ELIGIBLE in fit.reasons)
    }

    @Test
    fun `an unvalidated item cannot carry mastery evidence, and says why`() {
        val fit = ItemSelection.fit(
            item(lifecycle = LifecycleStatus.CANDIDATE), AssessmentIntent.MASTERY_EVIDENCE,
            AssessmentScope.DAILY_MICRO, profile(), true,
        )
        assertIs<ItemFit.NotUsable>(fit)
        assertEquals(setOf(ItemUnfit.USE_CEILING_BELOW_INTENT), fit.reasons)
    }

    // ---------------------------------------------------------------- artifact body

    @Test
    fun `a short response round-trips through its content reference`() {
        listOf("42", "for i in range(3): print(i)", "Türkçe yanıt: döngü üç kez çalışır", "a,b=%20&x"). forEach { body ->
            val stored = ArtifactBody.store(body)
            assertIs<ArtifactBody.Stored.Inline>(stored)
            assertEquals(body, ArtifactBody.read(stored.contentRef))
        }
    }

    @Test
    fun `a body too large is refused rather than truncated`() {
        val stored = ArtifactBody.store("x".repeat(ArtifactBody.MAX_BODY_CHARS + 1))
        assertIs<ArtifactBody.Stored.TooLarge>(stored)
        assertEquals(ArtifactBody.MAX_BODY_CHARS, stored.limit)
    }

    @Test
    fun `a reference that is not an inline body reads as nothing, never as a guess`() {
        assertNull(ArtifactBody.read("artifact://draft/1"))
        assertNull(ArtifactBody.read("data:text/plain;charset=utf-8,%ZZ"))
        // Every character here is one the encoder would have left alone, so only the prefix tells
        // this apart from a body of ours — and without that check it would read as "plainanswer".
        assertNull(ArtifactBody.read("plainanswer"))
        assertNull(ArtifactBody.read("data:text/plain,plainanswer"))
    }
}
