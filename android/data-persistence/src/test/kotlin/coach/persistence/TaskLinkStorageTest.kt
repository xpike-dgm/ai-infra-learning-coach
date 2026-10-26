package coach.persistence

import coach.model.StudyTimestamp
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 16F (`E2EX-v0`): an attempt names the planned task it was made for. Schema v11 adds the column to a populated v10
 * database without rewriting any truth; the store answers which planned tasks were started, and refuses a link to a
 * planned task that does not exist.
 */
class TaskLinkStorageTest {

    private val at = StudyTimestamp(1_790_942_400_000, "2026-10-26", 3 * 3600)

    private fun plan(db: SqlitePersistence): List<Long> {
        val planId = db.appendTruth(TruthRecord("plan_version", at, mapOf("policy_version" to "PLNX-v0")))
        return (0..2).map { position ->
            db.appendTruth(TruthRecord("planned_task", at, mapOf("plan_version_id" to planId.toString(),
                "skill_logical_id" to "skill.fixture.s$position", "skill_version" to "1", "position" to position.toString())))
        }
    }

    private fun attempt(db: SqlitePersistence, plannedTaskId: Long?) = db.appendTruth(TruthRecord("attempt", at, buildMap {
        put("resource_logical_id", "item.fixture.i")
        put("resource_version", "1")
        plannedTaskId?.let { put("planned_task_id", it.toString()) }
    }))

    @Test
    fun `schema v11 links attempts to planned tasks on a populated v10 database and leaves every truth row as it was`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 10)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { Fixtures.truthContent(it) }
        SqlitePersistence.open(file.absolutePath).use { db ->
            assertEquals(11, Schema.VERSION)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            // Every stored row is the same, with the new column empty on each: nothing was rewritten.
            val after = Fixtures.truthContent(db)
            Schema.appendOnlyTables.filterNot { it == "attempt" }.forEach { assertEquals(before[it], after[it], it) }
            assertEquals(before["attempt"]!!.map { "$it|∅" }, after["attempt"])
            assertTrue("planned_task_id" in db.query("PRAGMA table_info(attempt)") { it.getText(1) })
        }
    }

    @Test
    fun `the store says which planned tasks were started, and an attempt outside a planned task names none`() {
        SqlitePersistence.open(Fixtures.tempDb().absolutePath).use { db ->
            val tasks = plan(db)
            assertEquals(emptySet(), db.startedPlannedTasks(tasks))
            attempt(db, tasks[1])
            attempt(db, tasks[1])
            attempt(db, null)
            assertEquals(setOf(tasks[1]), db.startedPlannedTasks(tasks))
            assertEquals(emptySet(), db.startedPlannedTasks(emptyList()))
            // The link is append-only like the attempt, and it can only name a planned task that exists.
            assertFailsWith<Throwable> { db.execute("UPDATE attempt SET planned_task_id = ${tasks[0]}") }
            assertFailsWith<Throwable> { attempt(db, 999_999) }
        }
    }
}
