package coach.persistence

import coach.model.CapacitySource
import coach.model.CurriculumPackage
import coach.model.DailyCapacity
import coach.model.PlanTrace
import coach.model.PlanTraceCodec
import coach.model.PlannedEntry
import coach.model.PublishOutcome
import coach.model.SkillRow
import coach.model.StudyTimestamp
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Plans and the Skills they are opened from, against real SQLite (12C, `TVSX-v0` tier T2).
 *
 * `data-persistence` may not depend on `core-engines` or `core-application`, so the planner's choices
 * are T1 checks; what needs the real store lives here — that a plan is appended as truth with its tasks
 * and trace, that the trace survives storage exactly, and that the newest version of each Skill is the
 * one planning reads.
 */
class PlanStorageTest {

    private val at = StudyTimestamp(1_789_000_000_000, "2026-09-28", 3 * 3600)
    private val linux = VersionedRef("skill.linux.filesystem_navigation", 1)

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use(block)

    private fun skillRow(ref: VersionedRef, lifecycle: String = "published") =
        SkillRow(ref, ref.logicalId, "capability", lifecycle, "concept", "standard", false, "src", "authored")

    private fun trace() = PlanTrace(
        generationKind = "initial", studyDay = at.studyDay, curriculumVersion = 1, truthWatermark = 3,
        policyVersions = linkedMapOf("planner" to "PLNX-v0"),
        capacity = DailyCapacity(CapacitySource.NORMAL_PROFILE, 60, 54, reserveRelaxed = false, belowMinimumBlock = false),
        needs = emptyList(), candidates = emptyList(),
        selected = listOf(PlannedEntry(0, "linux-1", "new_learning:$linux", TaskPurpose.TEACH, "reading",
            "Dosya sistemi: ağaç, yollar ve 'İ' ile ı", linux, "linux", 20, 20, split = false)),
        planReasonCodes = listOf("capacity.source_normal_profile", "capacity.reserve_applied"),
        invariantChecks = linkedMapOf("planned_within_planning_budget" to true),
    )

    @Test
    fun `a plan, its tasks and its trace append as truth and read back exactly`() {
        withStore { db ->
            val stored = PlanTraceCodec.encode(trace())
            val (planId, taskId, traceId) = db.inTransaction {
                val plan = db.appendTruth(TruthRecord("plan_version", at, mapOf("policy_version" to "PLNX-v0")))
                val task = db.appendTruth(TruthRecord("planned_task", at, mapOf(
                    "plan_version_id" to plan.toString(), "skill_logical_id" to linux.logicalId,
                    "skill_version" to "1", "position" to "0")))
                val trace = db.appendTruth(TruthRecord("planner_decision_trace", at, mapOf(
                    "plan_version_id" to plan.toString(), "trace" to stored)))
                Triple(plan, task, trace)
            }
            assertEquals("PLNX-v0", db.readTruth("plan_version", planId)?.payload?.get("policy_version"))
            val task = db.readTruth("planned_task", taskId)!!
            assertEquals(planId.toString(), task.payload["plan_version_id"])
            assertEquals(at, task.recordedAt)
            val readBack = db.readTruth("planner_decision_trace", traceId)!!.payload.getValue("trace")
            assertEquals(stored, readBack)
            assertEquals(trace(), PlanTraceCodec.decode(readBack))
        }
    }

    @Test
    fun `a planned task cannot point at a plan that does not exist`() {
        withStore { db ->
            assertFailsWith<Exception> {
                db.inTransaction {
                    db.appendTruth(TruthRecord("planned_task", at, mapOf(
                        "plan_version_id" to "999", "skill_logical_id" to linux.logicalId, "skill_version" to "1", "position" to "0")))
                }
            }
            assertEquals(0, db.count("planned_task"))
        }
    }

    @Test
    fun `a plan is never edited in place`() {
        withStore { db ->
            db.inTransaction { db.appendTruth(TruthRecord("plan_version", at, mapOf("policy_version" to "PLNX-v0"))) }
            assertFailsWith<Exception> { db.query("UPDATE plan_version SET policy_version = 'edited'") { } }
            assertFailsWith<Exception> { db.query("DELETE FROM plan_version") { } }
            assertEquals(1, db.count("plan_version"))
        }
    }

    @Test
    fun `planning reads the newest version of every Skill, lifecycle included, in a stable order`() {
        withStore { db ->
            val shell = VersionedRef("skill.linux.shell", 1)
            assertIs<PublishOutcome.Published>(db.publishCurriculum(CurriculumPackage(
                version = 1, sourceRefs = "curriculum/decomposition", provenance = "authored",
                skills = listOf(skillRow(linux), skillRow(shell, lifecycle = "draft")),
            ), at.instantEpochMillis))
            val linuxV2 = VersionedRef(linux.logicalId, 2)
            assertIs<PublishOutcome.Published>(db.publishCurriculum(CurriculumPackage(
                version = 2, sourceRefs = "curriculum/decomposition", provenance = "authored",
                skills = listOf(skillRow(linuxV2, lifecycle = "deprecated")),
            ), at.instantEpochMillis))

            val skills = db.publishedSkills()
            assertEquals(listOf(linuxV2, shell), skills.map { it.ref })
            assertEquals(listOf("deprecated", "draft"), skills.map { it.lifecycleStatus })
        }
    }

    @Test
    fun `reading the Skills writes nothing`() {
        withStore { db ->
            db.publishCurriculum(CurriculumPackage(version = 1, sourceRefs = "s", provenance = "p",
                skills = listOf(skillRow(linux))), at.instantEpochMillis)
            val watermark = db.truthWatermark()
            repeat(3) { db.publishedSkills() }
            assertEquals(watermark, db.truthWatermark())
            assertTrue(db.count("plan_version") == 0L)
        }
    }
}
