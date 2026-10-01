package coach.application

import coach.model.AssessmentItem
import coach.model.BlockingScope
import coach.model.CandidateDisposition
import coach.model.CandidateTrace
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.CurriculumPackage
import coach.model.DailyCapacity
import coach.model.DailyCapacityInput
import coach.model.DecisionValue
import coach.model.DurationFit
import coach.model.EvidenceRow
import coach.model.EvidenceSeverity
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedDisposition
import coach.model.NeedTrace
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.PlanTrace
import coach.model.PlanTraceCodec
import coach.model.PlannedEntry
import coach.model.PlannerExplanationFacts
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteEligibility
import coach.model.PriorityBand
import coach.model.PublishOutcome
import coach.model.RankVector
import coach.model.ReasonCatalog
import coach.model.ReasonFamily
import coach.model.ReplanTrigger
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StarvationBucket
import coach.model.StoredPlan
import coach.model.StoredPlannedTask
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.TemporalUrgency
import coach.model.TrackBalance
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.recordedReasonCodes
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Reading a plan back (12E): Today and the planner explanation learn what the planner decided only from
 * the stored plan and its trace, and only when the trace describes the stored rows. The store here keeps
 * what it is given, so each test reads the plan earlier calls really appended.
 */
class PlanReadingTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val linux = VersionedRef("skill.linux.filesystem_navigation", 1)
    private val shell = VersionedRef("skill.linux.shell_basics", 1)
    private val english = VersionedRef("skill.english.error_messages", 1)
    private val capacity = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)

    private class Store : PersistencePort {
        val skills = mutableListOf<SkillRow>()
        val rows = mutableListOf<StoredTruth>()
        var writes = 0
        private var inside = false
        private var nextId = 1L

        fun appended(kind: String) = rows.filter { it.record.kind == kind }

        override fun <T> inTransaction(block: () -> T): T {
            writes += 1
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
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("reading writes no projection")
        override fun curriculumPublished(): Boolean = skills.isNotEmpty()
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("reading a plan reads no evidence")
        override fun truthWatermark(): Long = rows.size.toLong()
        override fun latestCurriculumVersion(): Int? = if (skills.isEmpty()) null else 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = skills.sortedBy { it.ref.logicalId }
        override fun latestPlan(): StoredPlan? {
            val plan = appended("plan_version").lastOrNull() ?: return null
            val tasks = appended("planned_task").filter { it.record.payload["plan_version_id"] == plan.id.toString() }
            val trace = appended("planner_decision_trace").lastOrNull { it.record.payload["plan_version_id"] == plan.id.toString() }
            return StoredPlan(plan.id, plan.record.recordedAt, tasks.size, trace?.record?.payload?.get("trace"),
                tasks.map { row ->
                    StoredPlannedTask(row.id, row.record.payload.getValue("position").toInt(),
                        VersionedRef(row.record.payload.getValue("skill_logical_id"), row.record.payload.getValue("skill_version").toInt()))
                }.sortedBy { it.position })
        }
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: coach.model.AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: coach.model.AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<coach.model.ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()

        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()


        override fun objectivesOf(skill: VersionedRef): List<coach.model.ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private class Content(val tasks: Map<String, List<TaskCandidate>>) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = tasks[need.needKey].orEmpty()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
    }

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private fun Store.publish(ref: VersionedRef, name: String = ref.logicalId) {
        skills += SkillRow(ref, name, "capability", "published", "concept", "standard", false, "src", "authored")
    }

    private fun task(needKey: String, skill: VersionedRef, minutes: Int, title: String = "Task $needKey") =
        TaskCandidate("t-$needKey", needKey, TaskPurpose.PRACTICE, "coding", title, skill, minutes, LifecycleStatus.VALIDATED)

    /** Three open needs of 20 minutes each, in a 60-minute day: 54 planning minutes hold two of them. */
    private fun world(): Triple<Store, Content, Clock> {
        val store = Store().apply { publish(pointer, "Pointer dereference"); publish(linux, "Dosya sistemi"); publish(shell) }
        val content = Content(mapOf(
            "new_learning:$pointer" to listOf(task("new_learning:$pointer", pointer, 20, "İşaretçiler")),
            "new_learning:$linux" to listOf(task("new_learning:$linux", linux, 20, "Yollar")),
            "new_learning:$shell" to listOf(task("new_learning:$shell", shell, 20)),
        ))
        return Triple(store, content, Clock("2026-09-30"))
    }

    // ------------------------------------------------------------------------------------ Today

    @Test
    fun `Today reads today's plan from its trace, each row pointing at its own stored task`() {
        val (store, content, clock) = world()
        val built = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        val facts = TodayFactsQuery(store, clock).load()

        val plan = facts.plan!!
        assertEquals(built.planVersionId, plan.planVersionId)
        assertEquals("2026-09-30", plan.studyDay)
        val storedIds = store.appended("planned_task").map { it.id }
        assertEquals(storedIds, plan.tasks.map { it.plannedTaskId })
        assertEquals(built.trace.selected.map { it.title }, plan.tasks.map { it.displayTitle })
        assertEquals(listOf(20, 20), plan.tasks.map { it.estimatedMinutes })
        assertTrue(plan.tasks.all { it.primaryPurpose == TaskPurpose.PRACTICE && !it.kept && !it.blocked })
        // New learning is progress on the route; the trace records nothing that would make it more.
        assertTrue(plan.tasks.all { it.traceFacts == listOf(ReasonFamily.CONTINUE_CURRENT_LEARNING) })

        val capacityFacts = facts.capacity!!
        assertEquals(60, capacityFacts.resolvedDailyMinutes)
        assertEquals(40, capacityFacts.estimatedTotalPlannedMinutes)
        assertEquals(40, capacityFacts.estimatedRemainingPlannedMinutes)
        assertFalse(capacityFacts.currentDayOverride)
        assertFalse(capacityFacts.planRecalculated)
        assertFalse(capacityFacts.tooSmallForAnyCandidate)
        assertFalse(facts.planUnreadable)
        assertFalse(facts.planReplaced)
    }

    @Test
    fun `a plan from another study day is passed on only with its own day`() {
        val (store, content, clock) = world()
        BuildDailyPlan(store, content, clock).build(capacity)
        clock.day = "2026-10-01"
        val facts = TodayFactsQuery(store, clock).load()
        assertEquals("2026-09-30", facts.plan!!.studyDay)
        assertEquals(emptyList(), facts.plan!!.tasks)
        assertNull(facts.capacity, "yesterday's capacity is not today's")
        assertFalse(facts.planUnreadable)
    }

    @Test
    fun `kept work is part of today's plan but is not new work, and the replan is said`() {
        val (store, content, clock) = world()
        BuildDailyPlan(store, content, clock).build(capacity)
        BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.TASK_FINISHED_EARLY, capacity, keptPositions = setOf(0)))
        val facts = TodayFactsQuery(store, clock).load()
        val tasks = facts.plan!!.tasks
        assertTrue(tasks.first().kept)
        assertEquals(emptyList(), tasks.first().traceFacts, "a kept task was chosen by an earlier version")
        assertTrue(tasks.drop(1).none { it.kept })
        assertTrue(facts.planReplaced)
        val capacityFacts = facts.capacity!!
        assertTrue(capacityFacts.planRecalculated)
        assertEquals(60, capacityFacts.resolvedDailyMinutes, "the day's budget is carried across the replan")
        assertEquals(tasks.sumOf { it.estimatedMinutes!! }, capacityFacts.estimatedTotalPlannedMinutes)
        assertEquals(tasks.filterNot { it.kept }.sumOf { it.estimatedMinutes!! }, capacityFacts.estimatedRemainingPlannedMinutes)
    }

    @Test
    fun `a split task shows the part planned for today, and its fit is the supporting reason`() {
        val store = Store().apply { publish(pointer) }
        val content = Content(mapOf("new_learning:$pointer" to listOf(
            TaskCandidate("long", "new_learning:$pointer", TaskPurpose.PRACTICE, "coding", "Uzun görev", pointer, 40,
                LifecycleStatus.VALIDATED, splittable = true, minimumSafeChunkMinutes = 10))))
        val clock = Clock("2026-09-30")
        BuildDailyPlan(store, content, clock).build(capacity.copy(todayOverrideMinutes = 30))
        val task = TodayFactsQuery(store, clock).load().plan!!.tasks.single()
        assertEquals(27, task.estimatedMinutes, "the row shows the whole task instead of today's part")
        assertEquals(listOf(ReasonFamily.CONTINUE_CURRENT_LEARNING, ReasonFamily.FIT_AVAILABLE_CAPACITY), task.traceFacts)
    }

    @Test
    fun `a day nothing fits is the planner's own finding`() {
        val (store, content, clock) = world()
        BuildDailyPlan(store, content, clock).build(capacity.copy(todayOverrideMinutes = 15))
        val facts = TodayFactsQuery(store, clock).load()
        assertEquals(emptyList(), facts.plan!!.tasks)
        assertTrue(facts.capacity!!.tooSmallForAnyCandidate)
        assertTrue(facts.capacity!!.currentDayOverride)
        assertEquals(15, facts.capacity!!.resolvedDailyMinutes)
    }

    // ------------------------------------------------------------------------------------ unreadable

    private fun rank(continuation: ContinuationValue = ContinuationValue.FRESH_NEW_CONTEXT) = RankVector(
        BlockingScope.NON_BLOCKING, Criticality.REQUIRED, EvidenceSeverity.NO_NEGATIVE_EVIDENCE, TemporalUrgency.NOT_TIME_SENSITIVE,
        StarvationBucket.NONE, continuation, DecisionValue.NONE, TrackBalance.NONE, DurationFit.FITS_REMAINING, "k")

    private fun entry(position: Int, skill: VersionedRef, needKey: String, track: String? = null, split: Boolean = false) =
        PlannedEntry(position, "c-$needKey", needKey, TaskPurpose.PRACTICE, "coding", "Task $position", skill, track,
            20, if (split) 10 else 20, split)

    private fun needTrace(needKey: String, trigger: NeedTrigger, skill: VersionedRef,
                          disposition: NeedDisposition = NeedDisposition.SELECTED, final: List<String> = listOf("selection.selected"),
                          continuation: ContinuationValue = ContinuationValue.FRESH_NEW_CONTEXT) =
        NeedTrace(needKey, trigger, listOf(skill), emptyList(), PriorityBand.P3, rank(continuation), emptyList(),
            if (disposition == NeedDisposition.SELECTED || disposition == NeedDisposition.PARTIALLY_SERVED) "c-$needKey" else null,
            disposition, final)

    private fun trace(day: String = "2026-09-30", selected: List<PlannedEntry>, needs: List<NeedTrace>,
                      candidates: List<CandidateTrace> = emptyList()) = PlanTrace(
        generationKind = "initial", studyDay = day, curriculumVersion = 1, truthWatermark = 1,
        policyVersions = linkedMapOf("planner" to "PLNX-v0"),
        capacity = DailyCapacity(CapacitySource.NORMAL_PROFILE, 60, 54, reserveRelaxed = false, belowMinimumBlock = false),
        needs = needs, candidates = candidates, selected = selected,
        planReasonCodes = listOf("capacity.source_normal_profile"), invariantChecks = emptyMap(),
    )

    private val at = StudyTimestamp(1_789_000_000_000, "2026-09-30", 3 * 3600)

    private fun stored(trace: PlanTrace?, rows: List<Pair<Int, VersionedRef>>, text: String? = trace?.let(PlanTraceCodec::encode)) =
        StoredPlan(9, at, rows.size, text, rows.mapIndexed { i, (position, skill) -> StoredPlannedTask(100L + i, position, skill) })

    private val good = trace(
        selected = listOf(entry(0, pointer, "new_learning:$pointer")),
        needs = listOf(needTrace("new_learning:$pointer", NeedTrigger.NEW_LEARNING, pointer)),
    )

    @Test
    fun `a trace that does not describe the stored rows is unreadable, and nothing is guessed`() {
        assertIs<PlanReading.Read.Today>(PlanReading.read(stored(good, listOf(0 to pointer)), "2026-09-30"))
        val cases = mapOf(
            "no trace" to stored(null, listOf(0 to pointer), text = null),
            "does not decode" to stored(good, listOf(0 to pointer), text = "planner_trace/3\nmystery"),
            "another position" to stored(good, listOf(1 to pointer)),
            "another Skill" to stored(good, listOf(0 to linux)),
            "a row the trace lacks" to stored(good, listOf(0 to pointer, 1 to linux)),
            "another day in the trace" to stored(good.copy(studyDay = "2026-09-29"), listOf(0 to pointer)),
            "no need decision" to stored(good.copy(needs = emptyList()), listOf(0 to pointer)),
            // Kept, so no other check can notice it: only the duplicate position is wrong.
            "a position twice" to stored(good.copy(selected = good.selected + good.selected.first().copy(preserved = true)),
                listOf(0 to pointer, 0 to pointer)),
            "not what the need chose" to stored(good.copy(needs = listOf(
                needTrace("new_learning:$pointer", NeedTrigger.NEW_LEARNING, pointer, disposition = NeedDisposition.ELIGIBLE_NOT_SELECTED))),
                listOf(0 to pointer)),
        )
        cases.forEach { (case, plan) ->
            assertIs<PlanReading.Read.Unreadable>(PlanReading.read(plan, "2026-09-30"), case)
        }
    }

    @Test
    fun `an unreadable plan for today reaches Today as unreadable, with no rows and no capacity`() {
        val store = Store().apply { publish(pointer) }
        val clock = Clock("2026-09-30")
        store.inTransaction {
            val plan = store.appendTruth(TruthRecord("plan_version", clock.now(), mapOf("policy_version" to "PLNX-v0")))
            store.appendTruth(TruthRecord("planned_task", clock.now(), mapOf("plan_version_id" to plan.toString(),
                "skill_logical_id" to linux.logicalId, "skill_version" to "1", "position" to "0")))
            store.appendTruth(TruthRecord("planner_decision_trace", clock.now(),
                mapOf("plan_version_id" to plan.toString(), "trace" to PlanTraceCodec.encode(good))))
        }
        val facts = TodayFactsQuery(store, clock).load()
        assertTrue(facts.planUnreadable)
        assertNull(facts.plan)
        assertNull(facts.capacity)
        assertIs<PlannerExplanationFacts.Unreadable>(PlannerExplanationQuery(store, clock).load())
    }

    // ------------------------------------------------------------------------------------ reasons

    @Test
    fun `a row's reasons are the trace's facts - the need, its continuation, its fit and its track`() {
        fun families(trigger: NeedTrigger, track: String? = null, split: Boolean = false,
                     continuation: ContinuationValue = ContinuationValue.FRESH_NEW_CONTEXT): List<ReasonFamily> {
            val final = if (split) listOf("selection.selected_split", "capacity.split_to_fit") else listOf("selection.selected")
            return PlanReading.reasonFamilies(entry(0, pointer, "n", track, split),
                needTrace("n", trigger, pointer, final = final, continuation = continuation))
        }
        val english = PlanReading.TECHNICAL_ENGLISH_TRACK
        assertEquals(listOf(ReasonFamily.CONTINUE_CURRENT_LEARNING), families(NeedTrigger.NEW_LEARNING))
        assertEquals(listOf(ReasonFamily.CONTINUE_CURRENT_LEARNING), families(NeedTrigger.CONTINUE_LEARNING))
        assertEquals(listOf(ReasonFamily.RESUME_VALID_PAUSED_WORK),
            families(NeedTrigger.CONTINUE_LEARNING, continuation = ContinuationValue.PAUSED_SAFE_CHECKPOINT))
        assertEquals(listOf(ReasonFamily.REPAIR_CONFIRMED_WEAKNESS), families(NeedTrigger.REMEDIATION_REQUIRED))
        // An unconfirmed weakness is a state to verify, never a confirmed one to repair.
        assertEquals(listOf(ReasonFamily.VERIFY_UNCERTAIN_STATE), families(NeedTrigger.WEAKNESS_DETECTED))
        assertEquals(listOf(ReasonFamily.VERIFY_UNCERTAIN_STATE), families(NeedTrigger.VERIFICATION_DUE))
        assertEquals(listOf(ReasonFamily.REVIEW_DUE_KNOWLEDGE), families(NeedTrigger.RETENTION_REVIEW_DUE))
        assertEquals(listOf(ReasonFamily.COLLECT_MISSING_EVIDENCE), families(NeedTrigger.DIAGNOSTIC_OPPORTUNITY))
        // English is a track: it is the reason only when the need is the parallel track's own cadence.
        assertEquals(listOf(ReasonFamily.PARALLEL_TECHNICAL_ENGLISH), families(NeedTrigger.PARALLEL_TRACK_DUE, english))
        assertEquals(listOf(ReasonFamily.CONTINUE_CURRENT_LEARNING, ReasonFamily.PARALLEL_TECHNICAL_ENGLISH),
            families(NeedTrigger.NEW_LEARNING, english))
        assertEquals(listOf(ReasonFamily.CONTINUE_CURRENT_LEARNING), families(NeedTrigger.PARALLEL_TRACK_DUE, "python"))
        // A fit the planner had to make is the supporting reason, and there is never more than one.
        assertEquals(listOf(ReasonFamily.REVIEW_DUE_KNOWLEDGE, ReasonFamily.FIT_AVAILABLE_CAPACITY),
            families(NeedTrigger.RETENTION_REVIEW_DUE, english, split = true))
        NeedTrigger.entries.forEach { trigger -> assertTrue(families(trigger, english, split = true).size <= 2) }
    }

    @Test
    fun `a need that waited on a prerequisite is reported, and the explanation names its Skills`() {
        val store = Store().apply { publish(pointer, "Pointer dereference"); publish(linux, "Dosya sistemi") }
        val clock = Clock("2026-09-30")
        val blocker = VersionedRef("skill.c.address_of", 1)
        store.publish(blocker, "Adres operatörü")
        val waiting = good.copy(
            needs = good.needs + needTrace("new_learning:$linux", NeedTrigger.NEW_LEARNING, linux,
                disposition = NeedDisposition.BLOCKED, final = listOf("selection.blocked_prerequisite")),
            candidates = listOf(CandidateTrace("c-new_learning:$linux", "new_learning:$linux", LifecycleStatus.VALIDATED,
                PrerequisiteEligibility.BLOCKED, 20, CandidateDisposition.BLOCKED_PREREQUISITE,
                listOf("eligibility.blocked_hard_prerequisite"), listOf(blocker))),
            planReasonCodes = good.planReasonCodes + "independent_branch_available",
        )
        store.inTransaction {
            val plan = store.appendTruth(TruthRecord("plan_version", clock.now(), mapOf("policy_version" to "PLNX-v0")))
            store.appendTruth(TruthRecord("planned_task", clock.now(), mapOf("plan_version_id" to plan.toString(),
                "skill_logical_id" to pointer.logicalId, "skill_version" to "1", "position" to "0")))
            store.appendTruth(TruthRecord("planner_decision_trace", clock.now(),
                mapOf("plan_version_id" to plan.toString(), "trace" to PlanTraceCodec.encode(waiting))))
        }
        assertTrue(TodayFactsQuery(store, clock).load().prerequisiteWaiting)
        val explained = assertIs<PlannerExplanationFacts.Readable>(PlannerExplanationQuery(store, clock).load())
        assertEquals(waiting, explained.trace)
        assertEquals(mapOf(pointer to "Pointer dereference", linux to "Dosya sistemi", blocker to "Adres operatörü"), explained.skillNames)
    }

    // ------------------------------------------------------------------------------------ explanation facts

    @Test
    fun `the explanation reads the same plan the same way`() {
        val (store, content, clock) = world()
        assertEquals(PlannerExplanationFacts.NoPlan, PlannerExplanationQuery(store, clock).load())
        val built = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        val readable = assertIs<PlannerExplanationFacts.Readable>(PlannerExplanationQuery(store, clock).load())
        assertEquals(built.trace, readable.trace)
        assertEquals("Pointer dereference", readable.skillNames[pointer])
        clock.day = "2026-10-01"
        assertEquals(PlannerExplanationFacts.PlanFromAnotherDay("2026-09-30"), PlannerExplanationQuery(store, clock).load())
    }

    @Test
    fun `an initial plan, a replan and a re-entry record only contract codes`() {
        val (store, content, clock) = world()
        val traces = listOf(
            (BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned).trace,
            (BuildDailyPlan(store, content, clock).replan(BuildDailyPlan.ReplanRequest(
                ReplanTrigger.TODAY_CAPACITY_CHANGED, capacity.copy(todayOverrideMinutes = 30), keptPositions = setOf(0)))
                as BuildDailyPlan.Built.Planned).trace,
            (BuildDailyPlan(store, content, clock.apply { day = "2026-10-04" }).build(capacity) as BuildDailyPlan.Built.Planned).trace,
        )
        traces.forEach { trace ->
            trace.recordedReasonCodes().forEach { assertTrue(ReasonCatalog.isKnown(it), "${trace.generationKind}: $it") }
        }
        assertTrue(traces[1].recordedReasonCodes().contains("replan.capacity_changed"))
        assertTrue(traces[2].recordedReasonCodes().contains("reentry.absence_not_task_debt"))
    }

    @Test
    fun `reading today's plan and its explanation writes nothing`() {
        val (store, content, clock) = world()
        BuildDailyPlan(store, content, clock).build(capacity)
        val rowsBefore = store.rows.size
        val writesBefore = store.writes
        TodayFactsQuery(store, clock).load()
        PlannerExplanationQuery(store, clock).load()
        assertEquals(rowsBefore, store.rows.size)
        assertEquals(writesBefore, store.writes)
    }
}
