package coach.application

import coach.engines.WeeklyBlueprintEngine
import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.AttemptSubmission
import coach.model.BlueprintEvidenceFact
import coach.model.BlueprintRole
import coach.model.BlueprintSlotOutcome
import coach.model.ComponentResult
import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
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
import coach.model.ObjectiveEvidenceProfile
import coach.model.OutcomeSignal
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteSnapshot
import coach.model.ProvenanceOrigin
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.UseCeiling
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.MonthlyBlueprintCodec
import coach.model.MonthlyReasonCodes
import coach.model.MonthlyRole
import coach.model.WeeklyReasonCodes
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The monthly assessment's use cases (13B) against a store that keeps what it is given, so each test sees
 * the sessions earlier calls really appended. The month shares the week's use case; these tests pin what
 * the scope changes — cycle, pool, format, prior session — and that nothing else is different.
 */
class MonthlyAssessmentTest {

    private val verify = VersionedRef("skill.c.pointer_dereference", 1)
    private val review = VersionedRef("skill.linux.filesystem_navigation", 1)
    private val basics = VersionedRef("skill.c.memory_model", 1)

    private class Store : PersistencePort {
        val skills = mutableListOf<SkillRow>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val rows = mutableListOf<StoredTruth>()
        val resources = mutableMapOf<VersionedRef, ResourceVersion>()
        val validations = mutableMapOf<VersionedRef, ValidationRecord>()
        val profiles = mutableMapOf<VersionedRef, ObjectiveEvidenceProfile>()
        val evidencedSince = mutableMapOf<String, List<VersionedRef>>()
        var published: Int? = 1
        private var inside = false
        private var nextId = 1L

        fun appended(kind: String) = rows.filter { it.record.kind == kind }

        override fun <T> inTransaction(block: () -> T): T {
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was appended outside a transaction" }
            val id = nextId++
            rows += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = rows.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) = error("composition writes no projection")
        override fun curriculumPublished(): Boolean = published != null
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = resources[ref]
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = validations[ref]
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = profiles[ref]
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("composition reads no evidence history")
        override fun truthWatermark(): Long = rows.size.toLong()
        override fun latestCurriculumVersion(): Int? = published
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = skills.sortedBy { it.ref.logicalId }
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? =
            appended("assessment_session").lastOrNull { it.record.payload["scope"] == scope.storedAs }
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> =
            appended("exposure_record").map { row ->
                val p = row.record.payload
                ExposureFact(VersionedRef(p.getValue("resource_logical_id"), p.getValue("resource_version").toInt()),
                    p["variant_family_id"], p.getValue("exposure_kind"))
            }.filter { it.resource in resources || it.variantFamilyId in variantFamilies }
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = evidencedSince[studyDay].orEmpty()

        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()


        override fun objectivesOf(skill: VersionedRef): List<coach.model.ObjectiveRow> = emptyList()
    }

    private class Content(val items: List<AssessmentItem>, val tasks: Map<String, List<TaskCandidate>> = emptyMap()) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = items.firstOrNull { it.ref == ref }
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = tasks[need.needKey].orEmpty()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = items.filter { skill in it.targetSkills }
    }

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private fun Store.publish(ref: VersionedRef, mastery: MasteryAxisState, retention: String = "not_yet_evaluated") {
        skills += SkillRow(ref, ref.logicalId, "capability", "published", "concept", "standard", false, "src", "authored")
        val key = "skill_state:${ref.logicalId}@v${ref.version}"
        projections[key] = ProjectionRecord(key, "GRE-v0", 1, 1, 1, mapOf(
            "mastery_axis_state" to mastery.id, "retention_axis_state" to retention,
            "prerequisite_axis_state" to "not_yet_evaluated", "weakness_axis_state" to "not_yet_evaluated",
            "primary_presentation_state" to "x",
        ))
    }

    /** An authored item, and the store's own record of it: published, validated as [storeTrust]. */
    private fun Store.item(name: String, skill: VersionedRef, storeTrust: LifecycleStatus = LifecycleStatus.VALIDATED,
                           claims: LifecycleStatus = LifecycleStatus.TRUSTED, required: List<VersionedRef> = emptyList()): AssessmentItem {
        val ref = VersionedRef("item.test.$name", 1)
        val objective = VersionedRef("objective.test.$name", 1)
        profiles[objective] = ObjectiveEvidenceProfile(objective, listOf("code_reading"), listOf("code_reading"))
        resources[ref] = ResourceVersion(ref, "content/$name", "code_reading", "compiler", ContentOrigin.HUMAN_AUTHORED, "fam.$name")
        validations[ref] = ValidationRecord(ref, 1, storeTrust, "validator.deterministic", ContentOrigin.HUMAN_AUTHORED)
        return AssessmentItem(
            ref = ref, targetObjectives = listOf(objective), targetSkills = listOf(skill), requiredSkills = required,
            evidenceType = "code_reading", expectedAnswerOrRubricRef = "key.$name",
            evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, false, "eval/1"),
            allowedTools = AllowedToolsPolicy(listOf("compiler")), independenceMode = IndependenceMode.H0_REQUIRED,
            difficultyClass = "standard_application", lifecycleStatus = claims, contentOrigin = ContentOrigin.HUMAN_AUTHORED,
            declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE, scopeEligibility = setOf(AssessmentScope.WEEKLY_BLUEPRINT, AssessmentScope.MONTHLY_CAPABILITY),
            variantFamilyId = "fam.$name", deterministicVerification = true, expectedActiveMinutes = 12,
            blueprintRoles = BlueprintRole.entries.toSet() + MonthlyRole.entries,
        )
    }

    private fun world(day: String = "2026-10-01"): Triple<Store, Content, Clock> {
        val store = Store()
        store.publish(verify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        store.publish(review, MasteryAxisState.CONFIRMED_CURRENT, retention = "review_due")
        val items = listOf(store.item("verify", verify), store.item("review", review))
        return Triple(store, Content(items), Clock(day))
    }

    private fun monthly(store: Store, content: Content, clock: Clock) =
        ComposeAssessmentBlueprint(AssessmentScope.MONTHLY_CAPABILITY, store, content, clock)

    private fun weekly(store: Store, content: Content, clock: Clock) =
        ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, content, clock)

    @Test
    fun `a month is composed once, stored in its own format, and asking again writes nothing`() {
        val (store, content, clock) = world()
        val use = monthly(store, content, clock)
        val first = assertIs<ComposeAssessmentBlueprint.Composed.Written>(use.compose(evaluatorAvailable = false))
        assertEquals("2026-10", first.blueprint.cycleId)
        assertEquals(AssessmentScope.MONTHLY_CAPABILITY, first.blueprint.scope)
        assertEquals(listOf(MonthlyRole.PERSISTENT_WEAKNESS_OR_VERIFICATION, MonthlyRole.DELAYED_RETENTION_SAMPLING),
            first.blueprint.readySlots.map { it.role })
        assertNull(first.blueprint.priorSessionId)
        val stored = store.appended("assessment_session").single().record.payload
        assertEquals("monthly", stored["scope"])
        assertEquals(first.blueprint, MonthlyBlueprintCodec.decode(stored.getValue("blueprint")))

        clock.day = "2026-10-31"
        val again = assertIs<ComposeAssessmentBlueprint.Composed.AlreadyComposed>(use.compose(evaluatorAvailable = false))
        assertEquals(first.sessionId, again.sessionId)
        assertEquals(1, store.appended("assessment_session").size)
    }

    @Test
    fun `a new month composes fresh, names the prior session and carries no debt`() {
        val (store, content, clock) = world()
        val use = monthly(store, content, clock)
        val first = assertIs<ComposeAssessmentBlueprint.Composed.Written>(use.compose(evaluatorAvailable = false))
        clock.day = "2026-12-03" // November simply never happened
        val next = assertIs<ComposeAssessmentBlueprint.Composed.Written>(use.compose(evaluatorAvailable = false))
        assertEquals("2026-12", next.blueprint.cycleId)
        assertEquals(first.sessionId, next.blueprint.priorSessionId)
        assertEquals("2026-10-01", next.blueprint.recentSince)
        assertTrue(MonthlyReasonCodes.NO_EXAM_DEBT in next.blueprint.reasonCodes)
        assertTrue(next.blueprint.reasonCodes.none { it.startsWith("assessment.weekly.") })
        assertEquals(2, store.appended("assessment_session").size)
    }

    @Test
    fun `the week and the month are composed independently and read back by their own scope`() {
        val (store, content, clock) = world()
        val week = assertIs<ComposeAssessmentBlueprint.Composed.Written>(weekly(store, content, clock).compose(false))
        val month = assertIs<ComposeAssessmentBlueprint.Composed.Written>(monthly(store, content, clock).compose(false))
        assertEquals(2, store.appended("assessment_session").size)
        assertIs<ComposeAssessmentBlueprint.Composed.AlreadyComposed>(weekly(store, content, clock).compose(false))
        assertIs<ComposeAssessmentBlueprint.Composed.AlreadyComposed>(monthly(store, content, clock).compose(false))
        assertEquals(week.sessionId, store.latestAssessmentSession(AssessmentScope.WEEKLY_BLUEPRINT)!!.id)
        assertEquals(month.sessionId, store.latestAssessmentSession(AssessmentScope.MONTHLY_CAPABILITY)!!.id)
        // The same items may serve both: a monthly label adds no evidence weight, so nothing is reserved.
        assertEquals(week.blueprint.readySlots.map { it.item }, month.blueprint.readySlots.map { it.item })
    }

    @Test
    fun `a stored blueprint of the other scope is never read as this one`() {
        val (store, content, clock) = world()
        store.inTransaction {
            store.appendTruth(TruthRecord("assessment_session", clock.now(), mapOf("scope" to "monthly", "blueprint" to "weekly_blueprint/1")))
        }
        assertIs<ComposeAssessmentBlueprint.Composed.Refused>(monthly(store, content, clock).compose(false))
        assertEquals(1, store.appended("assessment_session").size)
    }

    @Test
    fun `an item only weekly can use never fills a monthly slot`() {
        val store = Store()
        store.publish(verify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        val weeklyOnly = store.item("weekly_only", verify).copy(scopeEligibility = setOf(AssessmentScope.WEEKLY_BLUEPRINT))
        val result = monthly(store, Content(listOf(weeklyOnly)), Clock("2026-10-01")).compose(false)
        val nothing = assertIs<ComposeAssessmentBlueprint.Composed.NothingToMeasure>(result)
        assertTrue(MonthlyReasonCodes.NO_VALID_ITEM in nothing.blueprint.reasonCodes)
        assertTrue(store.rows.isEmpty())
    }

    @Test
    fun `week and month slots for one need are alternatives, and the planner takes at most one`() {
        val (store, content, clock) = world()
        weekly(store, content, clock).compose(false)
        monthly(store, content, clock).compose(false)
        val needs = PlannerEngineNeeds.of(store)
        val offered = BlueprintSlots.candidates(store, clock.now().studyDay, needs)
        assertEquals(listOf("weekly:2026-W40:slot-1", "weekly:2026-W40:slot-2", "monthly:2026-10:slot-1", "monthly:2026-10:slot-2"),
            offered.map { it.id })
        assertEquals(2, offered.map { it.needKey }.toSet().size)
        val plan = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, content, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)))
        assertEquals(2, plan.trace.selected.size)
        assertEquals(plan.trace.selected.map { it.needKey }.toSet().size, plan.trace.selected.size)
        // Next month offers nothing from this one.
        assertTrue(BlueprintSlots.candidates(store, "2026-11-02", needs).none { it.id.startsWith("monthly:") })
    }

    @Test
    fun `a month recomposes only itself, never a past month`() {
        val (store, content, clock) = world()
        val first = assertIs<ComposeAssessmentBlueprint.Composed.Written>(monthly(store, content, clock).compose(false))
        val alternative = store.item("verify_alt", verify)
        val use = monthly(store, Content(content.items + alternative), clock)
        val recomposed = assertIs<ComposeAssessmentBlueprint.Composed.Written>(use.recompose(setOf("slot-1"), evaluatorAvailable = false))
        assertEquals(first.sessionId, recomposed.blueprint.supersedesSessionId)
        assertEquals(AssessmentScope.MONTHLY_CAPABILITY, recomposed.blueprint.scope)
        assertEquals(alternative.ref, recomposed.blueprint.slots.single { it.slotId == "slot-1" }.item)
        assertTrue(MonthlyReasonCodes.INVALID_ITEM_REPLACED in recomposed.blueprint.slots.single { it.slotId == "slot-1" }.reasonCodes)
        assertEquals("monthly", store.appended("assessment_session").last().record.payload["scope"])
        clock.day = "2026-11-01"
        assertIs<ComposeAssessmentBlueprint.Composed.Refused>(use.recompose(setOf("slot-1"), evaluatorAvailable = false))
        // A weekly use case never recomposes the month.
        assertIs<ComposeAssessmentBlueprint.Composed.Refused>(
            weekly(store, content, Clock("2026-10-01")).recompose(setOf("slot-1"), evaluatorAvailable = false))
    }

    @Test
    fun `monthly slot evidence is recorded exactly like any other, contamination included`() {
        val store = Store()
        store.publish(basics, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        store.publish(verify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        val clock = Clock("2026-10-01")
        val items = listOf(store.item("basics", basics), store.item("verify", verify, required = listOf(basics)))
        val blueprint = assertIs<ComposeAssessmentBlueprint.Composed.Written>(monthly(store, Content(items), clock).compose(false)).blueprint
        val root = blueprint.slots.single { it.targetSkill == basics }
        val down = blueprint.slots.single { it.targetSkill == verify }
        val evaluation = EvaluationResult.Verified(listOf(ComponentResult(down.targetObjectives.single(), OutcomeSignal.MET)),
            EvaluatorRef("deterministic", "answer_key", "key/1"))
        val sessionSoFar = listOf(BlueprintSlotOutcome(root.slotId, true, 1, listOf(BlueprintEvidenceFact(1, root.targetObjectives.single(),
            EvidenceOutcome.NEGATIVE, EvaluatorStatus.VERIFIED, IndependenceClass.INDEPENDENT, false))))
        val written = RecordSlotEvidence(store, clock).record(blueprint, down.slotId, 7, evaluation, IndependenceClass.INDEPENDENT, sessionSoFar)
        val row = store.readTruth("evidence_event", written.evidenceIds.single())!!.payload
        assertEquals(PrerequisiteSnapshot.CONTAMINATED, row["prerequisite_snapshot"])
        // Nothing in the evidence row says monthly: the scope adds no weight (`MCA-v0` §2).
        assertTrue(row.values.none { "monthly" in it })
    }

    /** The needs the planner opens from the store's state, read the same way `BuildDailyPlan` reads them. */
    private object PlannerEngineNeeds {
        fun of(store: Store): List<LearningNeed> =
            coach.engines.PlannerEngine.needsFromSkillStates(PlanningStates.read(store, store.publishedSkills()))
    }
}
