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
import coach.model.WeeklyBlueprintCodec
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
 * The weekly assessment's use cases (13A) against a store that keeps what it is given, so each test sees
 * the sessions earlier calls really appended.
 */
class WeeklyAssessmentTest {

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
        override fun latestAssessmentSessionIn(scope: coach.model.AssessmentScope, format: String): StoredTruth? = null
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
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private class Content(val items: List<AssessmentItem>, val tasks: Map<String, List<TaskCandidate>> = emptyMap()) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = items.firstOrNull { it.ref == ref }
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = tasks[need.needKey].orEmpty()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = items.filter { skill in it.targetSkills }
        override fun explanationsFor(objective: VersionedRef): List<coach.model.ExplanationVariant> = emptyList()
        override fun codeTestsFor(item: VersionedRef): coach.model.CodeTestSuite? = null
        override fun comprehensionChecksFor(item: VersionedRef): List<coach.model.ComprehensionCheck> = emptyList()
        override fun answerKeyFor(item: VersionedRef): coach.model.AcceptedAnswers? = null
        override fun rubricFor(item: VersionedRef): coach.model.Rubric? = null
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
            declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE, scopeEligibility = setOf(AssessmentScope.WEEKLY_BLUEPRINT),
            variantFamilyId = "fam.$name", deterministicVerification = true, expectedActiveMinutes = 12,
            blueprintRoles = BlueprintRole.entries.toSet(),
        )
    }

    private fun world(day: String = "2026-10-01"): Triple<Store, Content, Clock> {
        val store = Store()
        store.publish(verify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        store.publish(review, MasteryAxisState.CONFIRMED_CURRENT, retention = "review_due")
        val items = listOf(store.item("verify", verify), store.item("review", review))
        return Triple(store, Content(items), Clock(day))
    }

    @Test
    fun `a week is composed once, and asking again writes nothing`() {
        val (store, content, clock) = world()
        val use = ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, content, clock)
        val first = assertIs<ComposeAssessmentBlueprint.Composed.Written>(use.compose(evaluatorAvailable = false))
        assertEquals("2026-W40", first.blueprint.cycleId)
        assertEquals(2, first.blueprint.readySlots.size)
        val stored = store.appended("assessment_session").single().record.payload
        assertEquals("weekly", stored["scope"])
        assertEquals(first.blueprint, WeeklyBlueprintCodec.decode(stored.getValue("blueprint")))

        clock.day = "2026-10-04" // Sunday: still the same ISO week
        val again = assertIs<ComposeAssessmentBlueprint.Composed.AlreadyComposed>(use.compose(evaluatorAvailable = false))
        assertEquals(first.sessionId, again.sessionId)
        assertEquals(1, store.appended("assessment_session").size)
    }

    @Test
    fun `a new week composes fresh from current state and carries nothing over`() {
        val (store, content, clock) = world()
        val use = ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, content, clock)
        use.compose(evaluatorAvailable = false)
        clock.day = "2026-10-19" // two weeks later; the week in between simply never happened
        val next = assertIs<ComposeAssessmentBlueprint.Composed.Written>(use.compose(evaluatorAvailable = false))
        assertEquals("2026-W43", next.blueprint.cycleId)
        assertTrue(WeeklyReasonCodes.NO_EXAM_DEBT in next.blueprint.reasonCodes)
        assertEquals("2026-10-01", next.blueprint.recentSince)
        assertEquals(2, store.appended("assessment_session").size)
    }

    @Test
    fun `nothing worth measuring writes nothing, and nothing published composes nothing`() {
        val store = Store().apply { publish(basics, MasteryAxisState.CONFIRMED_CURRENT, retention = "stable") }
        val result = ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, Content(emptyList()), Clock("2026-10-01")).compose(evaluatorAvailable = false)
        assertTrue(WeeklyReasonCodes.NO_ELIGIBLE_TARGET in assertIs<ComposeAssessmentBlueprint.Composed.NothingToMeasure>(result).blueprint.reasonCodes)
        assertTrue(store.rows.isEmpty())
        store.published = null
        assertIs<ComposeAssessmentBlueprint.Composed.NothingPublished>(
            ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, Content(emptyList()), Clock("2026-10-01")).compose(evaluatorAvailable = false))
    }

    @Test
    fun `trust is the store's validation record, not what the item claims`() {
        val store = Store()
        store.publish(verify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        // The document claims trusted; the store only ever validated it as a candidate.
        val item = store.item("claims", verify, storeTrust = LifecycleStatus.CANDIDATE, claims = LifecycleStatus.TRUSTED)
        val result = ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, Content(listOf(item)), Clock("2026-10-01")).compose(evaluatorAvailable = false)
        val slot = assertIs<ComposeAssessmentBlueprint.Composed.NothingToMeasure>(result).blueprint.slots.single()
        assertTrue("not_usable_for_intent" in slot.rejections.single().reasons)
        // An item the store never published is not an item at all.
        val unpublished = item.copy(ref = VersionedRef("item.test.ghost", 1))
        val ghost = ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, Content(listOf(unpublished)), Clock("2026-10-01")).compose(evaluatorAvailable = false)
        assertTrue(assertIs<ComposeAssessmentBlueprint.Composed.NothingToMeasure>(ghost).blueprint.slots.single().rejections.isEmpty())
    }

    @Test
    fun `an unreadable stored blueprint is not guessed around`() {
        val (store, content, clock) = world()
        store.inTransaction {
            store.appendTruth(TruthRecord("assessment_session", clock.now(), mapOf("scope" to "weekly", "blueprint" to "weekly_blueprint/9")))
        }
        assertIs<ComposeAssessmentBlueprint.Composed.Refused>(ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, content, clock).compose(false))
        assertEquals(1, store.appended("assessment_session").size)
    }

    @Test
    fun `the planner is offered this week's unserved slots for needs it already opened`() {
        val (store, content, clock) = world()
        ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, content, clock).compose(evaluatorAvailable = false)
        val plan = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, content, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)))
        assertEquals(listOf("weekly:2026-W40:slot-1", "weekly:2026-W40:slot-2"), plan.trace.selected.map { it.candidateId })
        assertEquals(listOf(WeeklyBlueprintEngine.ACTIVITY), plan.trace.selected.map { it.activityKind }.distinct())
    }

    @Test
    fun `a served slot and last week's slots are not offered again`() {
        val (store, content, clock) = world()
        ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, content, clock).compose(evaluatorAvailable = false)
        store.inTransaction {
            store.appendTruth(TruthRecord("exposure_record", clock.now(), mapOf("resource_logical_id" to "item.test.verify",
                "resource_version" to "1", "variant_family_id" to "fam.verify", "exposure_kind" to ExposureFact.ITEM_VERSION_SEEN)))
        }
        val needs = PlannerEngineNeeds.of(store)
        assertEquals(listOf("weekly:2026-W40:slot-2"), BlueprintSlots.candidates(store, clock.now().studyDay, needs).map { it.id })
        assertTrue(BlueprintSlots.candidates(store, "2026-10-12", needs).isEmpty())
    }

    @Test
    fun `recomposition appends a new row that names the one it supersedes`() {
        val (store, content, clock) = world()
        val first = assertIs<ComposeAssessmentBlueprint.Composed.Written>(ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, content, clock).compose(false))
        val alternative = store.item("verify_alt", verify)
        val use = ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, Content(content.items + alternative), clock)
        val recomposed = assertIs<ComposeAssessmentBlueprint.Composed.Written>(use.recompose(setOf("slot-1"), evaluatorAvailable = false))
        assertEquals(first.sessionId, recomposed.blueprint.supersedesSessionId)
        assertEquals(alternative.ref, recomposed.blueprint.slots.single { it.slotId == "slot-1" }.item)
        assertEquals(2, store.appended("assessment_session").size)
        assertIs<ComposeAssessmentBlueprint.Composed.Refused>(use.recompose(setOf("slot-9"), evaluatorAvailable = false))
        clock.day = "2026-10-12"
        assertIs<ComposeAssessmentBlueprint.Composed.Refused>(use.recompose(setOf("slot-1"), evaluatorAvailable = false))
    }

    @Test
    fun `an attempt made in a weekly session names the session`() {
        val (store, _, clock) = world()
        val recorded = SubmitAttempt(store, clock).submit(AttemptSubmission(VersionedRef("item.test.verify", 1), "data:,x",
            ProvenanceOrigin.USER_AUTHORED, assessmentSessionId = 42))
        assertEquals("42", store.readTruth("attempt", recorded.attemptId)!!.payload["assessment_session_id"])
        val plain = SubmitAttempt(store, clock).submit(AttemptSubmission(VersionedRef("item.test.verify", 1), "data:,x", ProvenanceOrigin.USER_AUTHORED))
        assertNull(store.readTruth("attempt", plain.attemptId)!!.payload["assessment_session_id"])
    }

    @Test
    fun `work resting on a prerequisite this session just showed missing is recorded as contaminated`() {
        val store = Store()
        store.publish(basics, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        store.publish(verify, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        val clock = Clock("2026-10-01")
        val items = listOf(store.item("basics", basics), store.item("verify", verify, required = listOf(basics)))
        val blueprint = assertIs<ComposeAssessmentBlueprint.Composed.Written>(
            ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, Content(items), clock).compose(false)).blueprint
        val root = blueprint.slots.single { it.targetSkill == basics }
        val down = blueprint.slots.single { it.targetSkill == verify }
        val evaluation = EvaluationResult.Verified(listOf(ComponentResult(down.targetObjectives.single(), OutcomeSignal.MET)),
            EvaluatorRef("deterministic", "answer_key", "key/1"))
        val sessionSoFar = listOf(BlueprintSlotOutcome(root.slotId, true, 1, listOf(BlueprintEvidenceFact(1, root.targetObjectives.single(),
            EvidenceOutcome.NEGATIVE, EvaluatorStatus.VERIFIED, IndependenceClass.INDEPENDENT, false))))
        val written = RecordSlotEvidence(store, clock).record(blueprint, down.slotId, 7, evaluation, IndependenceClass.INDEPENDENT, sessionSoFar)
        assertEquals(PrerequisiteSnapshot.CONTAMINATED,
            store.readTruth("evidence_event", written.evidenceIds.single())!!.payload["prerequisite_snapshot"])
        val clean = RecordSlotEvidence(store, clock).record(blueprint, down.slotId, 8, evaluation, IndependenceClass.INDEPENDENT, emptyList())
        assertNull(store.readTruth("evidence_event", clean.evidenceIds.single())!!.payload["prerequisite_snapshot"])
    }

    /** The needs the planner opens from the store's state, read the same way `BuildDailyPlan` reads them. */
    private object PlannerEngineNeeds {
        fun of(store: Store): List<LearningNeed> =
            coach.engines.PlannerEngine.needsFromSkillStates(PlanningStates.read(store, store.publishedSkills()))
    }
}
