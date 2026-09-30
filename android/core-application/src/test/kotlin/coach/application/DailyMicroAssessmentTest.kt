package coach.application

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentIntent
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.LearningNeed
import coach.model.TaskCandidate
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatusRequirement
import coach.model.IndependenceMode
import coach.model.ItemUnfit
import coach.model.LifecycleStatus
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StudyTimestamp
import coach.model.UseCeiling
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.EvidenceRow
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import coach.model.PrerequisiteEdge
import coach.model.StoredPlan
import coach.ports.StoredTruth

/**
 * Ingesting authored curriculum and offering one daily micro item. The storage guarantees — one
 * transaction, no overwrite of a published version — are proven against real SQLite in T2.
 */
class DailyMicroAssessmentTest {

    private val itemRef = VersionedRef("item.python.loops.q1", 1)
    private val objectiveRef = VersionedRef("objective.python.loops.trace", 1)
    private val skillRef = VersionedRef("skill.python.loops", 1)

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-21", 3 * 3600)
    }

    private fun authoredItem(
        lifecycle: LifecycleStatus = LifecycleStatus.TRUSTED,
        origin: ContentOrigin = ContentOrigin.HUMAN_AUTHORED,
        evidenceType: String = "code_reading",
    ) = AssessmentItem(
        ref = itemRef,
        targetObjectives = listOf(objectiveRef),
        targetSkills = listOf(skillRef),
        requiredSkills = emptyList(),
        evidenceType = evidenceType,
        expectedAnswerOrRubricRef = "key://item.python.loops.q1@v1",
        evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, false, "policy.v1"),
        allowedTools = AllowedToolsPolicy(listOf("documentation")),
        independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "core",
        lifecycleStatus = lifecycle,
        contentOrigin = origin,
        declaredUseCeiling = UseCeiling.CRITICAL_MASTERY_ELIGIBLE,
        scopeEligibility = setOf(AssessmentScope.DAILY_MICRO),
        variantFamilyId = "family.python.loops.trace",
        deterministicVerification = true,
    )

    private val curriculum = CurriculumPackage(
        version = 1,
        sourceRefs = "curriculum/decomposition/6c_foundations",
        provenance = "authored",
        skills = listOf(
            SkillRow(skillRef, "Loops", "Trace a loop", "stable", "concept", "standard", false, "src", "authored")
        ),
        objectives = listOf(
            ObjectiveRow(objectiveRef, skillRef, true, "standard", listOf("code_reading", "explanation"),
                listOf("code_reading"), "code_reading")
        ),
    )

    private class FakeContent(
        val item: AssessmentItem?,
        val curriculum: CurriculumPackage? = null,
    ) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = item?.takeIf { it.ref == ref }
        override fun curriculumPackage(): CurriculumPackage? = curriculum
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
    }

    private class FakeStore(
        val published: MutableSet<Int> = mutableSetOf(),
        var resource: ResourceVersion? = null,
        var validation: ValidationRecord? = null,
        var profile: ObjectiveEvidenceProfile? = null,
    ) : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        var transactions = 0
        var inside = false
        var publishedPackages = 0

        override fun <T> inTransaction(block: () -> T): T {
            transactions += 1
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was written outside a transaction" }
            appended += record
            return appended.size.toLong()
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("no projection is written here")
        override fun curriculumPublished(): Boolean = published.isNotEmpty()
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome {
            if (!published.add(curriculum.version)) return PublishOutcome.AlreadyPublished(curriculum.version)
            publishedPackages += 1
            return PublishOutcome.Published(curriculum.version, rows = 1)
        }
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = resource
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = validation
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = profile
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = emptyList()
        override fun truthWatermark(): Long = 0
        override fun latestCurriculumVersion(): Int? = null
        override fun skill(ref: VersionedRef): SkillRow? = null
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = emptyList()
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
    }

    private fun storeWithItem(
        validation: LifecycleStatus? = LifecycleStatus.TRUSTED,
        origin: ContentOrigin = ContentOrigin.HUMAN_AUTHORED,
        evidenceType: String = "code_reading",
    ) = FakeStore(
        resource = ResourceVersion(itemRef, "item://q1", evidenceType, "documentation", origin,
            variantFamilyId = "family.python.loops.trace"),
        validation = validation?.let { ValidationRecord(itemRef, 1_788_000_000_000, it, "human_review", origin) },
        profile = ObjectiveEvidenceProfile(objectiveRef, listOf("code_reading", "explanation"), listOf("code_reading"), "code_reading"),
    )

    // ---------------------------------------------------------------- ingestion

    @Test
    fun `ingestion publishes the authored package once and never overwrites it`() {
        val store = FakeStore()
        val ingest = IngestCurriculum(store, FakeContent(null, curriculum), clock)
        assertEquals(PublishOutcome.Published(1, 1), ingest.ingest())
        assertEquals(PublishOutcome.AlreadyPublished(1), ingest.ingest())
        assertEquals(1, store.publishedPackages)
    }

    @Test
    fun `with no authored package nothing is published and nothing is invented`() {
        val store = FakeStore()
        assertEquals(null, IngestCurriculum(store, FakeContent(null, null), clock).ingest())
        assertEquals(0, store.publishedPackages)
        assertTrue(store.appended.isEmpty())
    }

    // ---------------------------------------------------------------- serving one item

    @Test
    fun `a published, trusted, well-attributed item is offered and its exposure is recorded`() {
        val store = storeWithItem()
        val offer = ServeDailyMicroItem(store, FakeContent(authoredItem()), clock)
            .offer(itemRef, AssessmentIntent.MASTERY_EVIDENCE, evaluatorAvailable = true)

        val ready = assertIs<ItemOffer.Ready>(offer)
        assertEquals(UseCeiling.CRITICAL_MASTERY_ELIGIBLE, ready.fit.ceiling)
        val exposure = store.appended.single()
        assertEquals("exposure_record", exposure.kind)
        assertEquals("item_version_seen", exposure.payload.getValue("exposure_kind"))
        assertEquals(itemRef.logicalId, exposure.payload.getValue("resource_logical_id"))
        assertEquals("1", exposure.payload.getValue("resource_version"))
        assertEquals(clock.now(), exposure.recordedAt)
        assertEquals(1, store.transactions)
    }

    @Test
    fun `the store's validation record decides trust, not the item's claim about itself`() {
        // The document claims `trusted`; the store has only ever validated it as a candidate.
        val store = storeWithItem(validation = LifecycleStatus.CANDIDATE)
        val offer = ServeDailyMicroItem(store, FakeContent(authoredItem(lifecycle = LifecycleStatus.TRUSTED)), clock)
            .offer(itemRef, AssessmentIntent.MASTERY_EVIDENCE, evaluatorAvailable = true)

        val unusable = assertIs<ItemOffer.Unusable>(offer)
        assertEquals(setOf(ItemUnfit.USE_CEILING_BELOW_INTENT), unusable.reasons)
    }

    @Test
    fun `an unvalidated item is a candidate, never a trusted one by default`() {
        val store = storeWithItem(validation = null)
        val offer = ServeDailyMicroItem(store, FakeContent(authoredItem()), clock)
            .offer(itemRef, AssessmentIntent.CHECKPOINT, evaluatorAvailable = true)
        assertIs<ItemOffer.Unusable>(offer)
    }

    @Test
    fun `an unusable item records no exposure, because the learner never saw it`() {
        val store = storeWithItem(validation = LifecycleStatus.CANDIDATE)
        ServeDailyMicroItem(store, FakeContent(authoredItem()), clock)
            .offer(itemRef, AssessmentIntent.VERIFICATION, evaluatorAvailable = true)
        assertTrue(store.appended.isEmpty())
    }

    @Test
    fun `an item that is not published, or has no authored document, is unknown rather than guessed`() {
        val unpublished = FakeStore(profile = storeWithItem().profile)
        assertEquals(
            ItemOffer.Unknown,
            ServeDailyMicroItem(unpublished, FakeContent(authoredItem()), clock)
                .offer(itemRef, AssessmentIntent.CHECKPOINT, evaluatorAvailable = true),
        )
        assertEquals(
            ItemOffer.Unknown,
            ServeDailyMicroItem(storeWithItem(), FakeContent(null), clock)
                .offer(itemRef, AssessmentIntent.CHECKPOINT, evaluatorAvailable = true),
        )
    }

    @Test
    fun `the published evidence type and origin override the document's`() {
        // The document says `explanation`; the published row says `code_reading`, which the
        // Objective requires as its direct type.
        val store = storeWithItem(evidenceType = "code_reading")
        val offer = ServeDailyMicroItem(store, FakeContent(authoredItem(evidenceType = "explanation")), clock)
            .offer(itemRef, AssessmentIntent.MASTERY_EVIDENCE, evaluatorAvailable = true)
        assertIs<ItemOffer.Ready>(offer)
    }

    @Test
    fun `a solution exposure is recorded permanently and names the attempt it came from`() {
        val store = storeWithItem()
        ServeDailyMicroItem(store, FakeContent(authoredItem()), clock)
            .recordSolutionExposure(authoredItem(), attemptId = 7, maxLevel = "H4")
        val exposure = store.appended.single()
        assertEquals("solution_exposure", exposure.payload.getValue("exposure_kind"))
        assertEquals("H4", exposure.payload.getValue("max_exposure_level"))
        assertEquals("7", exposure.payload.getValue("source_attempt_id"))
        assertEquals("family.python.loops.trace", exposure.payload.getValue("variant_family_id"))
    }

    @Test
    fun `serving an item writes no evidence and no attempt`() {
        val store = storeWithItem()
        ServeDailyMicroItem(store, FakeContent(authoredItem()), clock)
            .offer(itemRef, AssessmentIntent.CHECKPOINT, evaluatorAvailable = true)
        assertTrue(store.appended.none { it.kind in setOf("evidence_event", "attempt", "artifact") })
    }
}
