package coach.application

import coach.model.AssessmentBlueprint
import coach.model.AssessmentBlueprintSlot
import coach.model.AssessmentScope
import coach.model.BlueprintEvidenceFact
import coach.model.BlueprintRole
import coach.model.BlueprintSlotOutcome
import coach.model.Criticality
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvaluatorStatus
import coach.model.EvidenceDispositions
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveGateProfile
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteReadiness
import coach.model.PriorityBand
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.SlotStatus
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import coach.model.WeaknessSignal
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Weakness rebuilt from evidence (13D) against a store that keeps what it is given and reads dispositions the
 * way the real store does, and the planner and the composers receiving what it supplies.
 */
class RebuildWeaknessTest {

    private val skill = VersionedRef("skill.python.loops", 1)
    private val objective = VersionedRef("objective.python.loops.trace", 1)
    private val profiles = listOf(ObjectiveGateProfile(objective, required = true, critical = false,
        acceptableEvidenceTypes = listOf("code_reading"), directEvidenceTypes = listOf("code_reading")))

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private class Store(val skills: List<SkillRow>) : PersistencePort {
        var evidence: List<EvidenceRow> = emptyList()
        var watermark = 42L
        val projections = mutableMapOf<String, ProjectionRecord>()
        val truth = mutableListOf<StoredTruth>()

        fun dispositions() = truth.filter { it.record.kind == "evidence_disposition" }

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long {
            val id = 1000L + truth.size
            truth += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? =
            if (kind == "evidence_event") evidence.firstOrNull { it.id == id }?.let { TruthRecord(kind, StudyTimestamp(0, "2026-10-01", 0), emptyMap()) }
            else truth.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) { projections[record.key] = record }
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        /** Like the real store: the newest disposition of a row is how it is read. */
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence.filter { it.objective == objective }.map { row ->
            val newest = dispositions().lastOrNull { it.record.payload["evidence_event_id"] == row.id.toString() }?.record?.payload
            EvidenceDispositions.effective(row, newest?.get("disposition"), newest?.get("reason_code"))
        }
        override fun truthWatermark(): Long = watermark
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = skills
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: coach.model.AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? =
            truth.lastOrNull { it.record.kind == "assessment_session" && it.record.payload["scope"] == scope.storedAs }
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()

        override fun objectivesOf(skill: VersionedRef): List<coach.model.ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private object NoContent : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef) = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: coach.model.LearningNeed) = emptyList<coach.model.TaskCandidate>()
        override fun assessmentItemsFor(skill: VersionedRef) = emptyList<coach.model.AssessmentItem>()
    }

    private fun skillRow(ref: VersionedRef = skill) = SkillRow(ref, ref.logicalId, "capability", "published", "concept", "standard", false, "src", "authored")

    private var nextId = 1L

    private fun evidence(
        outcome: EvidenceOutcome = EvidenceOutcome.NEGATIVE,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        family: String = "family.$nextId",
        day: String = "2026-09-21",
    ): EvidenceRow {
        val id = nextId++
        return EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_reading",
            outcome = outcome, evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = independence,
            contested = false, quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null,
            variantFamilyId = family, resource = VersionedRef("item.$id", 1), studyDay = day)
    }

    private fun weaknessRow(store: Store) = assertNotNull(store.projections["weakness_state:${objective.logicalId}@v1"])
    private fun stateRow(store: Store) = assertNotNull(store.projections["skill_state:${skill.logicalId}@v1"])

    @Test
    fun `a clean failure before mastery is written as a supported weakness on its Objective and the Skill`() {
        val store = Store(listOf(skillRow()))
        store.evidence = listOf(evidence())
        val clock = Clock("2026-09-21")
        RebuildMastery(store, clock).rebuild(skill, profiles)
        store.watermark = 50
        val rebuilt = RebuildWeakness(store, clock).rebuild(skill, profiles)
        assertEquals(WeaknessAxis.SUPPORTED, rebuilt.axis)
        val row = weaknessRow(store).payload
        assertEquals("supported", row["state"])
        assertEquals("objective_weakness_supported", row["last_attribution_outcome"])
        assertEquals("failure.clean_premastery_h0_direct", row["last_failure_rule"])
        assertEquals(skill.logicalId, row["skill_logical_id"])
        assertEquals("2026-09-21", row["first_seen_on_study_day"])
        assertEquals("WLRM-v0", weaknessRow(store).policyVersion)
        val state = stateRow(store)
        assertEquals("supported", state.payload["weakness_axis_state"])
        // The mastery axis is carried exactly as the mastery engine wrote it.
        assertEquals(MasteryAxisState.DEVELOPING_INDEPENDENT.id, state.payload["mastery_axis_state"])
        assertEquals(42, state.truthWatermark)
        assertTrue(store.truth.isEmpty(), "a rebuild writes no truth")
    }

    @Test
    fun `a failure not direct for its Objective is only a hypothesis`() {
        val store = Store(listOf(skillRow()))
        store.evidence = listOf(evidence().copy(evidenceType = "recognition_quiz"))
        val rebuilt = RebuildWeakness(store, Clock("2026-09-21")).rebuild(skill, profiles)
        assertEquals(WeaknessAxis.HYPOTHESIS, rebuilt.axis)
        assertEquals("failure.provisional_or_partial", weaknessRow(store).payload["last_failure_rule"])
    }

    @Test
    fun `one contradiction after mastery keeps it and opens verification, exactly as the mastery engine decides`() {
        val store = Store(listOf(skillRow()))
        val clock = Clock("2026-10-01")
        val rows = listOf(evidence(EvidenceOutcome.POSITIVE), evidence(EvidenceOutcome.POSITIVE), evidence(day = "2026-09-25"))
        val mastery = RebuildMastery(store, clock)
        rows.indices.forEach { i -> store.evidence = rows.take(i + 1); mastery.rebuild(skill, profiles) }
        assertEquals(MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id, stateRow(store).payload["mastery_axis_state"])
        val rebuilt = RebuildWeakness(store, clock).rebuild(skill, profiles)
        assertEquals(WeaknessAxis.SUPPORTED, rebuilt.axis)
        assertTrue(rebuilt.objectives.single().verificationOpen)
        assertEquals("verification_due", weaknessRow(store).payload["last_attribution_outcome"])
        assertEquals("1", weaknessRow(store).payload["verification_open"])
    }

    @Test
    fun `a supported weakness reaches the planner and the weekly composer as the owner's need`() {
        val store = Store(listOf(skillRow()))
        store.evidence = listOf(evidence())
        val clock = Clock("2026-09-21")
        RebuildMastery(store, clock).rebuild(skill, profiles)
        RebuildWeakness(store, clock).rebuild(skill, profiles)

        val plan = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, NoContent, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)))
        val need = plan.trace.needs.single { it.trigger == NeedTrigger.WEAKNESS_DETECTED }
        assertEquals(listOf(skill), need.targetSkills)
        assertTrue(plan.trace.needs.none { it.trigger == NeedTrigger.REMEDIATION_REQUIRED })

        val composed = ComposeAssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, store, NoContent, clock).compose(evaluatorAvailable = false)
        val blueprint = assertIs<ComposeAssessmentBlueprint.Composed.NothingToMeasure>(composed).blueprint
        assertEquals(BlueprintRole.WEAKNESS_OR_VERIFICATION, blueprint.slots.single { it.targetSkill == skill }.role)
    }

    @Test
    fun `lost mastery after a contradiction is a confirmed remediation the gate and the planner already read`() {
        val store = Store(listOf(skillRow()))
        val clock = Clock("2026-10-01")
        val rows = listOf(evidence(EvidenceOutcome.POSITIVE), evidence(EvidenceOutcome.POSITIVE),
            evidence(day = "2026-09-25"), evidence(day = "2026-09-26"))
        val mastery = RebuildMastery(store, clock)
        rows.indices.forEach { i -> store.evidence = rows.take(i + 1); mastery.rebuild(skill, profiles) }
        val rebuilt = RebuildWeakness(store, clock).rebuild(skill, profiles)
        assertEquals(WeaknessAxis.REMEDIATION_REQUIRED, rebuilt.axis)
        assertEquals(WeaknessSignal.CONFIRMED, rebuilt.objectives.single().signal)
        assertEquals("remediation_required", stateRow(store).payload["weakness_axis_state"])
        assertEquals(PrerequisiteReadiness.NOT_READY, ResolvePrerequisites(store).readinessOf(skill).readiness)
        val plan = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, NoContent, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)))
        assertTrue(plan.trace.needs.any { it.trigger == NeedTrigger.REMEDIATION_REQUIRED })
        assertTrue(plan.trace.needs.none { it.trigger == NeedTrigger.WEAKNESS_DETECTED }, "one concern is one need")
    }

    @Test
    fun `only new evidence closes a remediation - assisted success and a finished task do not`() {
        val store = Store(listOf(skillRow()))
        val clock = Clock("2026-10-01")
        val confirmed = listOf(evidence(EvidenceOutcome.POSITIVE), evidence(EvidenceOutcome.POSITIVE),
            evidence(day = "2026-09-25"), evidence(day = "2026-09-26"))
        store.evidence = confirmed + evidence(EvidenceOutcome.POSITIVE, independence = IndependenceClass.ASSISTED, day = "2026-09-28")
        assertEquals(WeaknessAxis.REMEDIATION_REQUIRED, RebuildWeakness(store, clock).rebuild(skill, profiles).axis)
        // Two fresh successes are new evidence, but the mastery engine's recent window does not pass yet.
        val two = listOf(evidence(EvidenceOutcome.POSITIVE, day = "2026-09-28"), evidence(EvidenceOutcome.POSITIVE, day = "2026-09-29"))
        store.evidence = confirmed + two
        assertEquals(WeaknessAxis.REMEDIATION_REQUIRED, RebuildWeakness(store, clock).rebuild(skill, profiles).axis)
        // Once enough fresh independent evidence makes the gates pass again, the remediation is closed.
        store.evidence = confirmed + two + listOf("2026-09-30", "2026-10-01", "2026-10-02").map { evidence(EvidenceOutcome.POSITIVE, day = it) }
        val restored = RebuildWeakness(store, clock).rebuild(skill, profiles)
        assertEquals(WeaknessAxis.RESOLVED, restored.axis)
        assertEquals("resolved", weaknessRow(store).payload["state"])
        assertTrue(weaknessRow(store).payload["resolution_evidence_id"].orEmpty().isNotEmpty())
    }

    @Test
    fun `a disposition is appended, never written over the row, and refused when it names nothing`() {
        val store = Store(listOf(skillRow()))
        store.evidence = listOf(evidence())
        val record = RecordDisposition(store, Clock("2026-10-01"))
        record.record(1, "contested", "user_report", "user_report")
        assertTrue(store.evidenceFor(objective).single().contested)
        assertFailsWith<IllegalArgumentException> { record.record(1, "deleted", "x", "validator") }
        assertFailsWith<IllegalArgumentException> { record.record(1, "invalidated", "x", "the_learner") }
        assertFailsWith<IllegalArgumentException> { record.record(1, "invalidated", " ", "validator") }
        assertFailsWith<IllegalArgumentException> { record.record(99, "invalidated", "x", "validator") }
        assertEquals(1, store.dispositions().size)
    }

    private fun slot(id: String, target: VersionedRef, requires: List<VersionedRef>, n: Int) = AssessmentBlueprintSlot(
        slotId = id, role = BlueprintRole.WEAKNESS_OR_VERIFICATION, needKey = "need:$n", trigger = NeedTrigger.VERIFICATION_DUE,
        targetSkill = target, criticality = Criticality.REQUIRED, band = PriorityBand.P1, sourceStateRefs = emptyList(), track = null,
        status = SlotStatus.READY, item = VersionedRef("item.s$n", 1), targetObjectives = listOf(VersionedRef("objective.s$n", 1)),
        evidenceType = "code_reading", variantFamilyId = "fam.s$n", expectedActiveMinutes = 10, itemLifecycle = LifecycleStatus.TRUSTED,
        itemRequiredSkills = requires,
    )

    @Test
    fun `a root shown missing later in the session corrects the answers that needed it, and only those`() {
        val root = VersionedRef("skill.c.memory", 1)
        val independent = VersionedRef("skill.linux.shell", 1)
        val blueprint = AssessmentBlueprint(AssessmentScope.WEEKLY_BLUEPRINT, "2026-W40", "2026-10-01", 1, 1, "WBA-v0", false, null,
            listOf(slot("slot-1", root, emptyList(), 1), slot("slot-2", skill, listOf(root), 2), slot("slot-3", independent, emptyList(), 3)),
            emptyList(), emptyList())
        val store = Store(listOf(skillRow(), skillRow(root), skillRow(independent)))
        fun row(id: Long, objective: VersionedRef, target: VersionedRef, outcome: EvidenceOutcome) = EvidenceRow(id, id, objective, target,
            "code_reading", outcome, EvaluatorStatus.VERIFIED, IndependenceClass.INDEPENDENT, false, 1.0, null, "fam.$id", studyDay = "2026-10-01")
        store.evidence = listOf(row(11, VersionedRef("objective.s2", 1), skill, EvidenceOutcome.POSITIVE),
            row(12, VersionedRef("objective.s3", 1), independent, EvidenceOutcome.POSITIVE),
            row(13, VersionedRef("objective.s1", 1), root, EvidenceOutcome.NEGATIVE))
        fun fact(id: Long, obj: String, outcome: EvidenceOutcome) = BlueprintEvidenceFact(id, VersionedRef(obj, 1), outcome,
            EvaluatorStatus.VERIFIED, IndependenceClass.INDEPENDENT, false)
        // The downstream slot was answered before the root was shown missing.
        val outcomes = listOf(
            BlueprintSlotOutcome("slot-2", true, 2, listOf(fact(11, "objective.s2", EvidenceOutcome.POSITIVE))),
            BlueprintSlotOutcome("slot-3", true, 3, listOf(fact(12, "objective.s3", EvidenceOutcome.POSITIVE))),
            BlueprintSlotOutcome("slot-1", true, 1, listOf(fact(13, "objective.s1", EvidenceOutcome.NEGATIVE))),
        )
        val apply = ApplyRetroactiveContamination(store, Clock("2026-10-01"))
        assertEquals(listOf(11L), apply.apply(blueprint, outcomes))
        val disposition = store.dispositions().single().record.payload
        assertEquals("invalidated", disposition["disposition"])
        assertEquals("prerequisite_contaminated", disposition["reason_code"])
        assertEquals("deterministic_rule", disposition["decided_by"])
        assertEquals(false, store.evidenceFor(VersionedRef("objective.s2", 1)).single().prerequisiteValid)
        assertEquals(true, store.evidenceFor(VersionedRef("objective.s3", 1)).single().prerequisiteValid)
        // Asked again, it has nothing more to say.
        assertTrue(apply.apply(blueprint, outcomes).isEmpty())
        assertEquals(1, store.dispositions().size)
        // With no clean root failure nothing is corrected.
        assertTrue(ApplyRetroactiveContamination(store, Clock("2026-10-01")).apply(blueprint, outcomes.take(2)).isEmpty())
    }
}
