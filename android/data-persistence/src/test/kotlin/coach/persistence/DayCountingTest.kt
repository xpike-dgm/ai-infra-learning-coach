package coach.persistence

import coach.model.StudyTimestamp
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Counting a day against real SQLite (11E, `TVSX-v0` tier T2).
 *
 * The rule that needs a real engine is which day a row belongs to: the study day the row carries,
 * never a range over instants. The check below writes rows whose instant and study day deliberately
 * disagree — exactly what a midnight crossing, a DST change or a flight produces — and proves the
 * count follows the recorded day.
 */
class DayCountingTest {

    private fun attemptAt(instant: Long, studyDay: String, offsetSeconds: Int = 3 * 3600) = TruthRecord(
        "attempt",
        StudyTimestamp(instant, studyDay, offsetSeconds),
        mapOf("resource_logical_id" to "item.python.loops.q1", "resource_version" to "1"),
    )

    @Test
    fun `a day counts the rows that recorded that day`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.inTransaction {
                db.appendTruth(attemptAt(1_789_000_000_000, "2026-09-21"))
                db.appendTruth(attemptAt(1_789_000_100_000, "2026-09-21"))
                db.appendTruth(attemptAt(1_788_900_000_000, "2026-09-20"))
            }
            assertEquals(2, db.countTruth("attempt", "2026-09-21"))
            assertEquals(1, db.countTruth("attempt", "2026-09-20"))
            assertEquals(0, db.countTruth("attempt", "2026-09-19"))
        }
    }

    @Test
    fun `the recorded study day decides, even when the instant says otherwise`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.inTransaction {
                // Two rows one second apart in absolute time, recorded on different study days:
                // the second crossed midnight. A count by instant range would put them together.
                db.appendTruth(attemptAt(1_789_000_000_000, "2026-09-21"))
                db.appendTruth(attemptAt(1_789_000_001_000, "2026-09-22"))
                // And a row whose offset moved: same study day, far-apart instants.
                db.appendTruth(attemptAt(1_789_050_000_000, "2026-09-21", offsetSeconds = 2 * 3600))
            }
            assertEquals(2, db.countTruth("attempt", "2026-09-21"))
            assertEquals(1, db.countTruth("attempt", "2026-09-22"))
        }
    }

    @Test
    fun `every kind the day summary counts is countable, and the rest are refused`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            listOf("attempt", "exposure_record", "resume_checkpoint", "evidence_event").forEach { kind ->
                assertEquals(0, db.countTruth(kind, "2026-09-21"), kind)
            }
            // A row that is part of another row has no day of its own and is not counted alone —
            // and the refusal names that rule rather than failing for some incidental reason.
            val dayless = assertFailsWith<IllegalArgumentException> { db.countTruth("evidence_event_objective", "2026-09-21") }
            assertTrue(dayless.message!!.contains("study day"), "refused for the wrong reason: ${dayless.message}")

            // Neither a projection nor curriculum is truth to be counted by day. Each refusal must
            // say so: a projection happening to have no study-day column is not the reason it is
            // not truth, and relying on that would hide the rule that actually matters.
            listOf("skill_state", "skill", "planner_summary").forEach { table ->
                val refusal = assertFailsWith<IllegalArgumentException> { db.countTruth(table, "2026-09-21") }
                assertTrue(
                    refusal.message!!.contains("not a truth table"),
                    "$table was refused for the wrong reason: ${refusal.message}",
                )
            }
        }
    }

    @Test
    fun `counting a day writes nothing`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.inTransaction { db.appendTruth(attemptAt(1_789_000_000_000, "2026-09-21")) }
            val watermark = db.truthWatermark()
            db.countTruth("attempt", "2026-09-21")
            assertEquals(watermark, db.truthWatermark())
            assertEquals(1, db.count("attempt"))
        }
    }
}
