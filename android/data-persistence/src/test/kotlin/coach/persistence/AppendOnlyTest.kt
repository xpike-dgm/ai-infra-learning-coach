package coach.persistence

import coach.model.StudyTimestamp
import coach.persistence.Fixtures.at
import coach.persistence.Fixtures.disposition
import coach.persistence.Fixtures.evidence
import coach.persistence.Fixtures.seed
import coach.ports.TruthRecord
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * `TVSX-v0` §6 and §8.1, against a **real** SQLite database.
 *
 * Each prohibition is proven by attempting the forbidden thing and requiring the storage engine to
 * refuse it. A test that only inserted rows would prove nothing about append-only: the rule is
 * about what cannot happen, so the forbidden statement has to be executed.
 */
class AppendOnlyTest {

    private lateinit var db: SqlitePersistence

    @BeforeTest
    fun open() {
        db = SqlitePersistence.open(SqlitePersistence.IN_MEMORY)
    }

    @AfterTest
    fun close() = db.close()

    @Test
    fun `every truth table refuses update and delete at the storage layer`() {
        Schema.truthTables.forEach { table ->
            seed(db, table)
            val update = assertFailsWith<Throwable>("UPDATE on $table must be refused") {
                db.execute("UPDATE $table SET sequence = sequence")
            }
            assertTrue("append-only" in update.message.orEmpty(), "$table: unexpected refusal ${update.message}")
            val delete = assertFailsWith<Throwable>("DELETE on $table must be refused") {
                db.execute("DELETE FROM $table")
            }
            assertTrue("append-only" in delete.message.orEmpty(), "$table: unexpected refusal ${delete.message}")
            assertEquals(1, db.count(table), "$table lost a row")
        }
    }

    @Test
    fun `every curriculum table refuses update and delete`() {
        db.execute("INSERT INTO skill (logical_id, version, canonical_name, capability_statement, lifecycle_status, capability_kind, retention_profile, source_refs, provenance) VALUES ('skill.os.paging', 1, 'Paging', 'explain paging', 'published', 'conceptual', 'standard', 'src', 'prov')")
        assertFailsWith<Throwable> { db.execute("UPDATE skill SET canonical_name = 'rewritten'") }
        assertFailsWith<Throwable> { db.execute("DELETE FROM skill") }
        assertEquals(1, db.count("skill"))
    }

    @Test
    fun `a correction is an appended disposition and the original row is unchanged`() {
        db.appendTruth(evidence(outcome = "positive"))
        val before = db.query("SELECT id, outcome, evaluator_status FROM evidence_event") {
            Triple(it.getLong(0), it.getText(1), it.getText(2))
        }

        db.appendTruth(disposition(before.single().first, "invalidated"))

        val after = db.query("SELECT id, outcome, evaluator_status FROM evidence_event") {
            Triple(it.getLong(0), it.getText(1), it.getText(2))
        }
        assertEquals(before, after, "the original evidence row must be untouched by a correction")
        assertEquals(1, db.count("evidence_disposition"))
    }

    @Test
    fun `exposure records are permanent`() {
        seed(db, "exposure_record")
        assertFailsWith<Throwable> { db.execute("DELETE FROM exposure_record") }
        assertEquals(1, db.count("exposure_record"))
    }

    @Test
    fun `a required column cannot be omitted through the port or behind it`() {
        val withoutVersion = TruthRecord(
            "attempt", at, mapOf("resource_logical_id" to "item.os.paging.q1"),
        )
        assertFailsWith<IllegalArgumentException> { db.appendTruth(withoutVersion) }

        // The schema refuses it even if calling code bypasses the port.
        assertFailsWith<Throwable> {
            db.execute(
                "INSERT INTO attempt (sequence, resource_logical_id, occurred_at_instant, occurred_on_study_day, utc_offset_minutes) " +
                    "VALUES (999, 'item.os.paging.q1', 0, '2026-08-31', 0)"
            )
        }
        assertEquals(0, db.count("attempt"))
    }

    @Test
    fun `a resource reference is pinned or absent never version free`() {
        val versionFree = TruthRecord(
            "evidence_event", at,
            evidence().payload + mapOf("resource_logical_id" to "item.os.paging.q1"),
        )
        assertFailsWith<Throwable> { db.appendTruth(versionFree) }
        assertEquals(0, db.count("evidence_event"))
    }

    @Test
    fun `the four evidence axes are four independent columns with the accepted value sets`() {
        val columns = db.query("PRAGMA table_info(evidence_event)") { it.getText(1) }.toSet()
        listOf("outcome", "evaluator_status", "independence_class", "contested").forEach { axis ->
            assertTrue(axis in columns, "missing axis column $axis")
        }
        // Every accepted value is storable...
        Schema.outcomeValues.forEach { db.appendTruth(evidence(outcome = it)) }
        Schema.independenceClassValues.forEach {
            db.appendTruth(TruthRecord("evidence_event", at, evidence().payload + ("independence_class" to it)))
        }
        // ...and a value outside the set is refused.
        assertFailsWith<Throwable> { db.appendTruth(evidence(outcome = "passed")) }
    }

    @Test
    fun `every timestamped truth row keeps instant study day and offset in minutes`() {
        db.appendTruth(evidence())
        val row = db.query(
            "SELECT occurred_at_instant, occurred_on_study_day, utc_offset_minutes FROM evidence_event"
        ) { Triple(it.getLong(0), it.getText(1), it.getLong(2)) }.single()
        assertEquals(Triple(at.instantEpochMillis, at.studyDay, 180L), row)
    }

    @Test
    fun `an offset that is not a whole number of minutes is refused rather than truncated`() {
        val odd = StudyTimestamp(at.instantEpochMillis, at.studyDay, 3 * 3600 + 30)
        assertFailsWith<IllegalArgumentException> {
            db.appendTruth(TruthRecord("evidence_event", odd, evidence().payload))
        }
        assertEquals(0, db.count("evidence_event"))
    }

    @Test
    fun `the truth sequence is global and monotonic across truth kinds`() {
        db.appendTruth(evidence())
        Fixtures.seed(db, "exposure_record")
        db.appendTruth(evidence())
        val sequences = Schema.truthTables.flatMap { table ->
            db.query("SELECT sequence FROM $table") { it.getLong(0) }
        }.sorted()
        assertEquals(sequences.distinct(), sequences, "a sequence value was reused")
        assertEquals(sequences.last(), db.truthWatermark())
    }
}
