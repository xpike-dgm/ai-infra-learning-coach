package coach.application

import coach.engines.TransferEngine
import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatus
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.MonthlyRole
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.TaskCandidate
import coach.model.TransferProfile
import coach.model.UseCeiling
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The transfer owner's facts (15G, `D-120`): a learned Skill opens a `transfer_opportunity` need only when an authored
 * cross-topic item could measure it now — trusted by the store, admitted by the gate, unseen — and no clean transfer
 * measurement exists yet.
 */
class TransferPlanningTest {

    private val loops = VersionedRef("skill.python.for_iteration", 1)
    private val strings = VersionedRef("skill.python.string_text_operations", 1)
    private val objective = VersionedRef("objective.python.for_iteration.iterate_sequence", 1)

    private class Store : PersistencePort {
        val skills = mutableListOf<SkillRow>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val resources = mutableMapOf<VersionedRef, ResourceVersion>()
        val validations = mutableMapOf<VersionedRef, ValidationRecord>()
        val evidence = mutableListOf<EvidenceRow>()
        val exposures = mutableListOf<ExposureFact>()
        val objectives = mutableMapOf<VersionedRef, List<ObjectiveRow>>()

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long = error("planning facts are read only")
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) = error("planning facts are read only")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = resources[ref]
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = validations[ref]
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence.filter { it.objective == objective }
        override fun truthWatermark(): Long = 7
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = skills
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> =
            exposures.filter { it.resource in resources || it.variantFamilyId in variantFamilies }
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = objectives[skill].orEmpty()
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private class Content(val items: List<AssessmentItem>) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = items.firstOrNull { it.ref == ref }
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = items.filter { skill in it.targetSkills }
        override fun explanationsFor(objective: VersionedRef): List<coach.model.ExplanationVariant> = emptyList()
        override fun codeTestsFor(item: VersionedRef): coach.model.CodeTestSuite? = null
        override fun comprehensionChecksFor(item: VersionedRef): List<coach.model.ComprehensionCheck> = emptyList()
        override fun answerKeyFor(item: VersionedRef): coach.model.AcceptedAnswers? = null
        override fun rubricFor(item: VersionedRef): coach.model.Rubric? = null
    }

    private fun skillRow(ref: VersionedRef) = SkillRow(ref, ref.logicalId, "test", "published", "concept", "standard", false, "test", "test")

    private fun item(name: String, profile: TransferProfile? = TransferProfile.CROSS_TOPIC_CONTEXT) = AssessmentItem(
        ref = VersionedRef("item.python.for_iteration.iterate_sequence.$name", 1), targetObjectives = listOf(objective),
        targetSkills = listOf(loops), requiredSkills = listOf(strings), evidenceType = "authored_code",
        expectedAnswerOrRubricRef = "codetest://$name",
        evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, true, "policy.deterministic.v1"),
        allowedTools = AllowedToolsPolicy(emptyList()), independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "transfer_integration", lifecycleStatus = LifecycleStatus.VALIDATED, contentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE, scopeEligibility = setOf(AssessmentScope.MONTHLY_CAPABILITY),
        variantFamilyId = "family.python.for_iteration.iterate_sequence.$name", deterministicVerification = true, expectedActiveMinutes = 8,
        blueprintRoles = setOf(MonthlyRole.CROSS_TOPIC_TRANSFER), transferProfile = profile,
        contextFamilyId = profile?.let { "context.vowel_counter" },
    )

    private val transfer = item("t01")

    private fun store(items: List<AssessmentItem> = listOf(transfer), status: LifecycleStatus = LifecycleStatus.VALIDATED,
                      stringsReady: Boolean = true) = Store().apply {
        skills += skillRow(loops)
        skills += skillRow(strings)
        objectives[loops] = listOf(ObjectiveRow(objective, loops, true, "standard", listOf("authored_code"), listOf("authored_code")))
        for (i in items) {
            resources[i.ref] = ResourceVersion(i.ref, "item://${i.ref}", "authored_code", "documentation", ContentOrigin.HUMAN_AUTHORED, i.variantFamilyId)
            validations[i.ref] = ValidationRecord(i.ref, 1791115200000, status, "independent_review", ContentOrigin.HUMAN_AUTHORED)
        }
        if (stringsReady) {
            val key = ResolvePrerequisites.skillStateKey(strings)
            projections[key] = ProjectionRecord(key, "SPWX-v0", 7, 0, 1, mapOf("mastery_axis_state" to MasteryAxisState.CONFIRMED_CURRENT.id))
        }
    }

    private fun state(mastery: MasteryAxisState? = MasteryAxisState.CONFIRMED_CURRENT) =
        SkillPlanningState(loops, "published", false, mastery, RetentionAxis.NOT_YET_EVALUATED, null, "skill_state:$loops#watermark=7")

    private fun needs(store: Store, items: List<AssessmentItem> = listOf(transfer), mastery: MasteryAxisState? = MasteryAxisState.CONFIRMED_CURRENT) =
        TransferPlanning.needs(store, Content(items), listOf(state(mastery)))

    private fun row(outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE, evaluator: EvaluatorStatus = EvaluatorStatus.VERIFIED,
                    resource: VersionedRef = transfer.ref) =
        EvidenceRow(1, 1, objective, loops, "authored_code", outcome, evaluator, IndependenceClass.INDEPENDENT, false, null, null,
            transfer.variantFamilyId, resource = resource)

    @Test
    fun `a learned Skill with an unseen, trusted, admitted transfer item opens one need`() {
        val need = needs(store()).single()
        assertEquals(TransferEngine.needKey(loops), need.needKey)
        assertEquals(NeedTrigger.TRANSFER_OPPORTUNITY, need.trigger)
        assertEquals(listOf(loops), need.targetSkills)
    }

    @Test
    fun `a Skill still being learned opens nothing`() {
        assertTrue(needs(store(), mastery = MasteryAxisState.DEVELOPING_INDEPENDENT).isEmpty())
        assertTrue(needs(store(), mastery = null).isEmpty())
    }

    @Test
    fun `only a cross-topic item the store trusts can measure transfer`() {
        // Trust is the store's, never the item's own claim.
        assertTrue(needs(store(status = LifecycleStatus.CANDIDATE)).isEmpty())
        assertTrue(needs(Store().apply { skills += skillRow(loops) }).isEmpty(), "an item the store never published is not served")
        val sameLesson = item("g01", profile = null)
        assertTrue(needs(store(items = listOf(sameLesson)), items = listOf(sameLesson)).isEmpty())
        val near = item("g02", profile = TransferProfile.NEAR_CONTEXT)
        assertTrue(needs(store(items = listOf(near)), items = listOf(near)).isEmpty())
    }

    @Test
    fun `the gate decides, so a context Skill not yet ready keeps the item waiting`() {
        assertTrue(needs(store(stringsReady = false)).isEmpty())
    }

    @Test
    fun `a seen item or a family whose solution was shown is not a fresh measurement`() {
        assertTrue(needs(store().apply { exposures += ExposureFact(transfer.ref, transfer.variantFamilyId, ExposureFact.ITEM_VERSION_SEEN) }).isEmpty())
        val other = VersionedRef("item.python.for_iteration.iterate_sequence.t00", 1)
        assertTrue(needs(store().apply { exposures += ExposureFact(other, transfer.variantFamilyId, ExposureFact.SOLUTION_EXPOSURE) }).isEmpty())
        // A second, unseen transfer item keeps the opportunity open.
        val second = item("t02")
        val both = store(items = listOf(transfer, second)).apply {
            exposures += ExposureFact(transfer.ref, transfer.variantFamilyId, ExposureFact.ITEM_VERSION_SEEN)
        }
        assertEquals(1, needs(both, items = listOf(transfer, second)).size)
    }

    @Test
    fun `one clean transfer measurement closes the question, and unclean work leaves it open`() {
        assertTrue(needs(store().apply { evidence += row() }).isEmpty())
        assertTrue(needs(store().apply { evidence += row(outcome = EvidenceOutcome.NEGATIVE) }).isEmpty())
        assertEquals(1, needs(store().apply { evidence += row(evaluator = EvaluatorStatus.PROVISIONAL) }).size)
        assertEquals(1, needs(store().apply { evidence += row(resource = VersionedRef("item.python.for_iteration.iterate_sequence.g01", 1)) }).size)
    }
}
