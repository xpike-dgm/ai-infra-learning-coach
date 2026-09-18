package coach.persistence

import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import coach.persistence.Fixtures.disposition
import coach.persistence.Fixtures.evidence
import coach.persistence.Fixtures.exposure
import coach.persistence.Fixtures.skillState
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `LFPS-v0` §9–§11 and `TVSX-v0` §8.2–§8.3 against a real SQLite file.
 *
 * Migrations are exercised against a **populated** database. An empty-database migration test
 * passes on a migration that silently drops every evidence row, which is exactly the failure this
 * product cannot afford.
 */
class TransactionAndMigrationTest {

    private fun tempDb(): File =
        File.createTempFile("coach-", ".db").also { it.delete(); it.deleteOnExit() }

    // ---------------------------------------------------------------- transactions

    @Test
    fun `a failure inside one learner action leaves no partial state observable`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            assertFailsWith<IllegalStateException> {
                db.inTransaction {
                    db.appendTruth(evidence())
                    db.appendTruth(exposure())
                    error("evaluator crashed after two writes")
                }
            }
            assertEquals(0, db.count("evidence_event"), "evidence from the failed action leaked")
            assertEquals(0, db.count("exposure_record"), "exposure from the failed action leaked")
            assertEquals(0, db.truthWatermark(), "the truth sequence advanced for a rolled-back action")
        }
    }

    @Test
    fun `failure injected at each stage of an action always rolls back completely`() {
        val stages = listOf("evidence", "exposure", "projection")
        stages.indices.forEach { failAfter ->
            SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
                runCatching {
                    db.inTransaction {
                        db.appendTruth(evidence())
                        if (failAfter == 0) error("fail after ${stages[0]}")
                        db.appendTruth(exposure())
                        if (failAfter == 1) error("fail after ${stages[1]}")
                        db.writeProjection(skillState())
                        if (failAfter == 2) error("fail after ${stages[2]}")
                    }
                }
                val stage = stages[failAfter]
                assertEquals(0, db.count("evidence_event"), "partial evidence after failing at $stage")
                assertEquals(0, db.count("exposure_record"), "partial exposure after failing at $stage")
                assertEquals(0, db.count("skill_state"), "partial projection after failing at $stage")
            }
        }
    }

    @Test
    fun `a successful action commits every write together`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.inTransaction {
                db.appendTruth(evidence())
                db.appendTruth(exposure())
            }
            assertEquals(1, db.count("evidence_event"))
            assertEquals(1, db.count("exposure_record"))
        }
    }

    // ---------------------------------------------------------------- projections

    @Test
    fun `a projection records full provenance and a stale one is detectable`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(evidence())
            db.appendTruth(exposure())
            db.writeProjection(skillState(watermark = db.truthWatermark()))

            val read = assertNotNull(db.readProjection("skill_state:skill.os.paging@v1"))
            assertEquals("gre-v0", read.policyVersion)
            assertEquals(2L, read.truthWatermark)
            assertEquals(Fixtures.at.instantEpochMillis, read.builtAtInstant)
            assertEquals(1, read.inputCurriculumVersion)
            // The four axes come back separately; the presentation state did not replace them.
            assertEquals("developing", read.payload["mastery_axis_state"])
            assertEquals("developing_with_support", read.payload["primary_presentation_state"])

            // Any kind of truth arriving later makes the projection visibly stale.
            db.appendTruth(exposure())
            assertTrue(db.truthWatermark() > read.truthWatermark, "a stale projection must be detectable")
        }
    }

    @Test
    fun `a projection is rebuildable and may be replaced`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.writeProjection(skillState(state = "developing_with_support", watermark = 1))
            db.writeProjection(skillState(state = "confirmed_current", watermark = 2))
            assertEquals("confirmed_current", db.readProjection("skill_state:skill.os.paging@v1")?.payload?.get("primary_presentation_state"))
            assertNull(db.readProjection("skill_state:skill.os.unknown@v1"))
        }
    }

    @Test
    fun `a projection cannot be addressed without a version`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            assertFailsWith<IllegalArgumentException> { db.readProjection("skill_state:skill.os.paging") }
        }
    }

    @Test
    fun `projection tables are droppable without touching truth`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(evidence())
            db.writeProjection(skillState())
            Schema.projectionTables.forEach { db.execute("DELETE FROM $it") }
            assertEquals(0, db.count("skill_state"))
            assertEquals(1, db.count("evidence_event"), "clearing projections touched truth")
        }
    }

    // ---------------------------------------------------------------- migration

    private val evidenceSnapshotSql =
        """
        SELECT id, sequence, skill_logical_id, skill_version, outcome, evaluator_status,
               independence_class, contested, occurred_at_instant, occurred_on_study_day,
               utc_offset_minutes
        FROM evidence_event ORDER BY id
        """.trimIndent()

    private val row: (SQLiteStatement) -> String =
        { s -> (0 until s.getColumnCount()).joinToString("|") { s.getText(it) } }

    /** Builds a schema-1 database and fills it with truth, as a real older install would be. */
    private fun populatedV1(file: File) {
        BundledSQLiteDriver().open(file.absolutePath).also { connection ->
            connection.execSQL("PRAGMA foreign_keys = ON")
            Migrations.migrate(connection, target = 1)
            connection.close()
        }
        SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            repeat(25) { db.appendTruth(evidence(skill = "skill.fixture.s$it")) }
            repeat(10) { db.appendTruth(exposure(resource = "item.fixture.i$it")) }
            db.appendTruth(disposition(3))
        }
    }

    @Test
    fun `migrating a populated database preserves evidence exposure and provenance exactly`() {
        val file = tempDb()
        populatedV1(file)

        val (evidenceBefore, exposureBefore, dispositionsBefore) =
            SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
                Triple(db.query(evidenceSnapshotSql, row), db.count("exposure_record"), db.count("evidence_disposition"))
            }
        assertEquals(25, evidenceBefore.size, "fixture was not populated")

        SqlitePersistence.open(file.absolutePath).use { db ->
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            // Counts and content, not a sample.
            assertEquals(evidenceBefore, db.query(evidenceSnapshotSql, row), "evidence content changed during migration")
            assertEquals(exposureBefore, db.count("exposure_record"), "exposure row count changed")
            assertEquals(dispositionsBefore, db.count("evidence_disposition"), "provenance of a correction was lost")
            val indexes = db.query("SELECT name FROM sqlite_master WHERE type = 'index' AND name = 'evidence_by_skill'") { it.getText(0) }
            assertEquals(listOf("evidence_by_skill"), indexes, "the v2 migration did not apply")
        }
    }

    @Test
    fun `a migration that fails part way leaves the previous state intact and surfaces data recovery`() {
        val file = tempDb()
        populatedV1(file)

        val connection = BundledSQLiteDriver().open(file.absolutePath)
        // Sabotage the step **after** it has already changed something. The v2 migration creates
        // its indexes, then clears and rewrites the metadata row; this trigger makes the final
        // insert fail. By then the indexes exist and the metadata row is gone, so only a real
        // rollback can put the database back.
        //
        // An earlier version of this test failed the migration on its very first statement.
        // Nothing had changed yet, so replacing ROLLBACK with COMMIT still passed — the test was
        // not proving atomicity at all. Mutation testing caught that.
        connection.execSQL(
            "CREATE TRIGGER sabotage_metadata_write BEFORE INSERT ON schema_metadata " +
                "BEGIN SELECT RAISE(ABORT, 'injected failure after partial migration'); END"
        )
        val failure = assertFailsWith<Migrations.DataRecoveryRequired> { Migrations.migrate(connection) }
        assertTrue("previous state left intact" in failure.message.orEmpty())
        assertEquals(coach.model.RecoveryReason.MIGRATION_INCOMPLETE, failure.reason)
        assertEquals(1, Migrations.currentVersion(connection), "a partial migration moved or erased the schema version")
        val leftover = connection.prepare(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = 'index' AND name = 'evidence_by_skill'"
        ).use { it.step(); it.getLong(0) }
        assertEquals(0L, leftover, "part of the failed migration was left applied")
        connection.close()

        SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            assertEquals(25, db.count("evidence_event"), "evidence was lost by a failed migration")
        }
    }

    @Test
    fun `a database from a newer schema is refused rather than opened on a guess`() {
        val file = tempDb()
        SqlitePersistence.open(file.absolutePath).close()
        BundledSQLiteDriver().open(file.absolutePath).also { connection ->
            connection.execSQL("UPDATE schema_metadata SET schema_version = ${Schema.VERSION + 1}")
            connection.close()
        }
        val failure = assertFailsWith<Migrations.DataRecoveryRequired> { SqlitePersistence.open(file.absolutePath) }
        assertTrue("downgrade is not supported" in failure.message.orEmpty())
        assertEquals(coach.model.RecoveryReason.NEWER_SCHEMA, failure.reason)
    }

    @Test
    fun `curriculum references always carry a version`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.execute("INSERT INTO skill (logical_id, version, canonical_name, capability_statement, lifecycle_status, capability_kind, retention_profile, source_refs, provenance) VALUES ('skill.os.paging', 1, 'Paging', 'explain paging', 'published', 'conceptual', 'standard', 'src', 'prov')")
            // The foreign key needs both columns: a reference to a version that does not exist fails.
            assertFailsWith<Throwable> {
                db.execute(
                    "INSERT INTO objective (logical_id, version, parent_skill_logical_id, parent_skill_version, criticality, acceptable_evidence_types, direct_evidence_types) " +
                        "VALUES ('obj.os.paging.cost', 1, 'skill.os.paging', 2, 'critical', 'any', 'any')"
                )
            }
            assertEquals(0, db.count("objective"))
        }
    }

    @Test
    fun `an empty curriculum store and a published one are told apart`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            // A fresh install: Today must be able to say "nothing is published yet" rather than
            // "your plan is loading" (THUX-v0, 11A).
            assertEquals(false, db.curriculumPublished())
            db.execute(
                "INSERT INTO skill (logical_id, version, canonical_name, capability_statement, lifecycle_status, " +
                    "capability_kind, retention_profile, source_refs, provenance) " +
                    "VALUES ('skill.python.loops', 1, 'Loops', 'write a loop', 'published', 'procedural', 'standard', 'src', 'prov')"
            )
            assertEquals(true, db.curriculumPublished())
        }
    }

    // ---------------------------------------------------------------- a learner action (11B)

    /** The rows one submitted attempt writes, linked by the ids the store returns. */
    private fun recordAttempt(db: SqlitePersistence, failAfter: String? = null): Long {
        val attemptId = db.appendTruth(Fixtures.attempt())
        if (failAfter == "attempt") error("fail after attempt")
        val artifactId = db.appendTruth(
            coach.ports.TruthRecord("artifact", Fixtures.at, mapOf("attempt_id" to "$attemptId", "content_ref" to "artifact://1"))
        )
        if (failAfter == "artifact") error("fail after artifact")
        db.appendTruth(
            coach.ports.TruthRecord("artifact_provenance", Fixtures.at, mapOf("artifact_id" to "$artifactId", "origin" to "unknown_provenance"))
        )
        if (failAfter == "provenance") error("fail after provenance")
        db.appendTruth(
            coach.ports.TruthRecord("assistance_event", Fixtures.at, mapOf(
                "attempt_id" to "$attemptId", "level" to "H3", "timing" to "during_attempt",
                "target_scope" to "target_objective", "source" to "deterministic_content", "requested_by_user" to "1",
            ))
        )
        return attemptId
    }

    @Test
    fun `an attempt with its artifact provenance and assistance commits as one action`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val attemptId = db.inTransaction { recordAttempt(db) }
            assertEquals(1, db.count("attempt"))
            // The ids the store returned are the ones the rows really point at.
            assertEquals(listOf(attemptId), db.query("SELECT attempt_id FROM artifact") { it.getLong(0) })
            assertEquals(listOf(attemptId), db.query("SELECT attempt_id FROM assistance_event") { it.getLong(0) })
            assertEquals(listOf("unknown_provenance"), db.query("SELECT origin FROM artifact_provenance") { it.getText(0) })
            assertEquals(0, db.count("evidence_event"), "recording an attempt wrote evidence")
        }
    }

    @Test
    fun `an attempt never survives without its assistance metadata or provenance`() {
        listOf("attempt", "artifact", "provenance").forEach { stage ->
            SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
                runCatching { db.inTransaction { recordAttempt(db, failAfter = stage) } }
                listOf("attempt", "artifact", "artifact_provenance", "assistance_event").forEach { table ->
                    assertEquals(0, db.count(table), "$table survived a failure after $stage")
                }
            }
        }
    }

    @Test
    fun `a provenance the learner could not have given is refused`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val attemptId = db.appendTruth(Fixtures.attempt())
            val artifactId = db.appendTruth(
                coach.ports.TruthRecord("artifact", Fixtures.at, mapOf("attempt_id" to "$attemptId", "content_ref" to "artifact://1"))
            )
            assertFailsWith<Throwable> {
                db.appendTruth(
                    coach.ports.TruthRecord("artifact_provenance", Fixtures.at, mapOf("artifact_id" to "$artifactId", "origin" to "suspected_cheating"))
                )
            }
        }
    }

    @Test
    fun `no foreign key crosses from the user store into curriculum`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val crossing = Schema.truthTables.flatMap { table ->
                db.query("PRAGMA foreign_key_list($table)") { it.getText(2) }
                    .filter { it in Schema.curriculumTables }
                    .map { "$table -> $it" }
            }
            assertEquals(emptyList(), crossing, "a curriculum update could reach learner truth")
        }
    }
}
