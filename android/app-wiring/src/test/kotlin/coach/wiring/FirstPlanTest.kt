package coach.wiring

import coach.application.BuildDailyPlan
import coach.curriculum.FileContentSource
import coach.model.AssessmentScope
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.MisconceptionRow
import coach.model.NeedDisposition
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StoredPlannedTask
import coach.model.StudyTimestamp
import coach.model.TaskPurpose
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The first plan the app can make (15A, `CPFX-v0`): the package that ships, read by the real content adapter, planned
 * by the real gate and planner for a learner who has done nothing yet. Before 15A every need had no candidate and the
 * plan was honestly empty; this is the first time a need meets a task.
 *
 * The store is a small in-memory one holding exactly what publishing the package would hold (its Skills and edges) and
 * no learner state at all. Publishing into SQLite is `data-persistence`'s and is not re-tested here.
 */
class FirstPlanTest {

    private val text = File("src/main/assets/curriculum_package.txt").readText()
    private val content = FileContentSource { text }
    private val curriculum: CurriculumPackage = content.curriculumPackage()!!

    private class ZeroLearnerStore(private val curriculum: CurriculumPackage) : PersistencePort {
        val rows = mutableListOf<StoredTruth>()
        private var inside = false
        override fun <T> inTransaction(block: () -> T): T { inside = true; try { return block() } finally { inside = false } }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was appended outside a transaction" }
            rows += StoredTruth(rows.size + 1L, record)
            return rows.size.toLong()
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = rows.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("planning a day writes no learner state")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("already published")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = curriculum.resources.firstOrNull { it.ref == ref }
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = curriculum.validationRecords.lastOrNull { it.resource == ref }
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = curriculum.objectives.firstOrNull { it.ref == ref }?.profile()
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = emptyList()
        override fun truthWatermark(): Long = rows.size.toLong()
        override fun latestCurriculumVersion(): Int? = curriculum.version
        override fun skill(ref: VersionedRef): SkillRow? = curriculum.skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = curriculum.prerequisiteEdges.filter { it.target == target }
        override fun publishedSkills(): List<SkillRow> = curriculum.skills.sortedBy { it.ref.logicalId }
        override fun latestPlan(): StoredPlan? {
            val plan = rows.lastOrNull { it.record.kind == "plan_version" } ?: return null
            val tasks = rows.filter { it.record.kind == "planned_task" && it.record.payload["plan_version_id"] == plan.id.toString() }
            val trace = rows.lastOrNull { it.record.kind == "planner_decision_trace" }?.record?.payload?.get("trace")
            return StoredPlan(plan.id, plan.record.recordedAt, tasks.size, trace, tasks.map {
                StoredPlannedTask(it.id, it.record.payload.getValue("position").toInt(),
                    VersionedRef(it.record.payload.getValue("skill_logical_id"), it.record.payload.getValue("skill_version").toInt()))
            })
        }
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = curriculum.objectives.filter { it.parentSkill == skill }
        override fun misconceptionsOf(objective: VersionedRef): List<MisconceptionRow> = curriculum.misconceptions.filter { it.objective == objective }
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_942_400_000, "2026-10-02", 3 * 3600)
    }

    private fun skill(id: String) = VersionedRef(id, 1)

    @Test
    fun `a learner who has done nothing is offered the two entry lessons and nothing that waits on them`() {
        val store = ZeroLearnerStore(curriculum)
        val built = BuildDailyPlan(store, content, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))
        val trace = assertIs<BuildDailyPlan.Built.Planned>(built).trace

        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"))
        assertEquals(entry, trace.selected.map { it.primarySkill }.toSet(), "only Skills with no unmet prerequisite are started")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")

        // Every published Skill opened a new-learning need; those whose prerequisites are not yet shown wait.
        assertEquals(12, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(10, waiting.size, "everything downstream waits: $waiting")
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "no need is left without a task any more")

        // The plan is written as truth, once, with its trace.
        assertEquals(listOf("plan_version", "planned_task", "planned_task", "planner_decision_trace"), store.rows.map { it.record.kind })
    }
}
