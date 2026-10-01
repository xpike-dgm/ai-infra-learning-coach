package coach.persistence

import coach.model.AssessmentScope
import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 13F's storage against a real SQLite file: `diagnostic_coverage` added by a forward migration to version 7, the
 * waiver and the diagnostic's values enforced by SQLite itself, a daily session that can carry content only as the
 * learner's diagnostic, the newest diagnostic found past any other daily row, and an evidence row that says which
 * session its attempt was made in.
 */
class DiagnosticStorageTest {

    private val objective = VersionedRef("objective.c.pointer_basics.address_vs_value", 1)
    private val diagnostic = "diagnostic_scope/1\nrequest\tsource=user_requested_fast_path\tstudy_day=2026-10-01\tcurriculum_version=1"

    private fun coverage(waiver: String = "none", state: String = "probe_needed", stage: String = "probe",
                         session: String = "", evidence: String = "") = ProjectionRecord(
        key = "diagnostic_coverage:${objective.logicalId}@v1",
        policyVersion = "VDW-v0",
        truthWatermark = 1,
        builtAtInstant = Fixtures.at.instantEpochMillis,
        inputCurriculumVersion = 1,
        payload = mapOf(
            "skill_logical_id" to "skill.c.pointer_basics", "skill_version" to "1",
            "waiver" to waiver, "waiver_session_id" to session, "waiver_source_evidence_ids" to evidence,
            "waiver_granted_at_sequence" to "", "waiver_granted_on_study_day" to "",
            "diagnostic_session_id" to "1", "diagnostic_state" to state, "diagnostic_stage" to stage,
            "failed_gates" to "", "window_variant_families" to "", "window_dependency_groups" to "",
            "reason_codes" to "diagnostic.probe_selected", "as_of_study_day" to "2026-10-01",
        ),
    )

    @Test
    fun `migrating a populated schema-6 database adds diagnostic_coverage and preserves every truth row`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 6)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            db.appendTruth(Fixtures.session())
            Fixtures.truthContent(db)
        }
        SqlitePersistence.open(file.absolutePath).use { db ->
            // Narrowed at 14B: a later version (v8, the misconception catalog and memory) migrates through this one.
            assertTrue(Schema.VERSION >= 7)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.truthTables.forEach { assertEquals(before[it], after[it], it) }
            val columns = db.query("PRAGMA table_info(diagnostic_coverage)") { it.getText(1) }
            listOf("waiver", "waiver_session_id", "waiver_source_evidence_ids", "diagnostic_session_id", "diagnostic_state",
                "diagnostic_stage", "failed_gates", "window_variant_families", "policy_version", "truth_watermark")
                .forEach { assertTrue(it in columns, it) }
            assertEquals(listOf("diagnostic_coverage_by_skill"),
                db.query("SELECT name FROM sqlite_master WHERE type = 'index' AND name = 'diagnostic_coverage_by_skill'") { it.getText(0) })
            // The row a previous build wrote is untouched: a trigger guards inserts only.
            assertEquals(1L, db.count("assessment_session"))
        }
    }

    @Test
    fun `diagnostic_coverage holds only VDW-v0's values, and an active waiver names its evidence and session`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            assertFailsWith<Exception> { db.writeProjection(coverage(waiver = "skipped")) }
            assertFailsWith<Exception> { db.writeProjection(coverage(state = "failed")) }
            assertFailsWith<Exception> { db.writeProjection(coverage(stage = "easy_probe")) }
            // A waiver that cannot name what validated it does not exist.
            assertFailsWith<Exception> { db.writeProjection(coverage(waiver = "active", state = "waived", stage = "")) }
            assertFailsWith<Exception> { db.writeProjection(coverage(waiver = "active", state = "waived", stage = "", session = "4")) }
            db.writeProjection(coverage())
            db.writeProjection(coverage(waiver = "active", state = "waived", stage = "", session = "4", evidence = "7,9"))
            val row = db.readProjection("diagnostic_coverage:${objective.logicalId}@v1")!!
            assertEquals("active", row.payload["waiver"])
            assertEquals("7,9", row.payload["waiver_source_evidence_ids"])
            assertEquals("VDW-v0", row.policyVersion)
            val update = assertFailsWith<Exception> { db.execute("UPDATE diagnostic_coverage SET waiver = 'skipped'") }
            assertTrue("CHECK constraint failed" in update.message.orEmpty(), update.message)
            // It is a projection, droppable without touching truth.
            db.execute("DELETE FROM diagnostic_coverage")
        }
    }

    @Test
    fun `a daily session carries content only as the learner's diagnostic`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val refused = assertFailsWith<Exception> {
                db.appendTruth(TruthRecord("assessment_session", Fixtures.at, mapOf("scope" to "daily", "blueprint" to "weekly_blueprint/1\n")))
            }
            assertTrue("diagnostic_scope" in refused.message.orEmpty(), refused.message)
            db.appendTruth(TruthRecord("assessment_session", Fixtures.at, mapOf("scope" to "daily")))
            db.appendTruth(TruthRecord("assessment_session", Fixtures.at, mapOf("scope" to "daily", "blueprint" to diagnostic)))
            assertEquals(2L, db.count("assessment_session"))
        }
    }

    @Test
    fun `the newest diagnostic is found past a newer daily row of another kind`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            assertNull(db.latestAssessmentSessionIn(AssessmentScope.DAILY_MICRO, "diagnostic_scope/1"))
            val first = db.appendTruth(TruthRecord("assessment_session", Fixtures.at, mapOf("scope" to "daily", "blueprint" to diagnostic)))
            val second = db.appendTruth(TruthRecord("assessment_session", Fixtures.at, mapOf("scope" to "daily", "blueprint" to diagnostic)))
            db.appendTruth(TruthRecord("assessment_session", Fixtures.at, mapOf("scope" to "daily")))
            val found = db.latestAssessmentSessionIn(AssessmentScope.DAILY_MICRO, "diagnostic_scope/1")!!
            assertEquals(second, found.id)
            assertTrue(first < second)
            assertEquals(diagnostic, found.record.payload["blueprint"])
            // A weekly row is never a diagnostic, whatever it holds.
            assertNull(db.latestAssessmentSessionIn(AssessmentScope.WEEKLY_BLUEPRINT, "diagnostic_scope/1"))
        }
    }

    @Test
    fun `an evidence row says which session its attempt was made in`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val session = db.appendTruth(TruthRecord("assessment_session", Fixtures.at, mapOf("scope" to "daily", "blueprint" to diagnostic)))
            val inSession = db.appendTruth(Fixtures.attempt(sessionId = session))
            val outside = db.appendTruth(Fixtures.attempt())
            listOf(inSession, outside, null).forEach { attempt ->
                val id = db.appendTruth(Fixtures.evidence(skill = "skill.c.pointer_basics").let { record ->
                    if (attempt == null) record else record.copy(payload = record.payload + ("source_attempt_id" to attempt.toString()))
                })
                db.appendTruth(TruthRecord("evidence_event_objective", Fixtures.at, mapOf("evidence_event_id" to id.toString(),
                    "objective_logical_id" to objective.logicalId, "objective_version" to "1")))
            }
            val rows = db.evidenceFor(objective)
            assertEquals(listOf(session, null, null), rows.map { it.assessmentSessionId })
        }
    }

    @Test
    fun `nothing in schema 7 lets a diagnostic edit or remove truth`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(TruthRecord("assessment_session", StudyTimestamp(1, "2026-10-01", 0), mapOf("scope" to "daily", "blueprint" to diagnostic)))
            assertFailsWith<Exception> { db.execute("UPDATE assessment_session SET blueprint = NULL") }
            assertFailsWith<Exception> { db.execute("DELETE FROM assessment_session") }
        }
    }
}
