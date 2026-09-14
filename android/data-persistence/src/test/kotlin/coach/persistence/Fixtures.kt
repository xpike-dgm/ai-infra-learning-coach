package coach.persistence

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import coach.model.StudyTimestamp
import java.io.File
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord

/**
 * Rows shaped exactly as `DDM-v0` defines them. The values are taken from the accepted allowed
 * sets, so a test that passes here is exercising the real contract rather than a convenient
 * stand-in.
 */
internal object Fixtures {

    /** A study day in Istanbul (UTC+3): 180 minutes, stored as minutes per DDM-v0. */
    val at = StudyTimestamp(1_756_600_000_000, "2026-08-31", 3 * 3600)

    fun session() = TruthRecord("assessment_session", at, mapOf("scope" to "daily"))

    fun attempt(sessionId: Long? = null) = TruthRecord(
        "attempt", at,
        buildMap {
            put("resource_logical_id", "item.os.paging.q1")
            put("resource_version", "1")
            if (sessionId != null) put("assessment_session_id", sessionId.toString())
        },
    )

    fun evidence(skill: String = "skill.os.paging", outcome: String = "positive") = TruthRecord(
        "evidence_event", at,
        mapOf(
            "skill_logical_id" to skill,
            "skill_version" to "1",
            "evidence_type" to "independent_check",
            "outcome" to outcome,
            "evaluator_status" to "verified",
            "independence_class" to "independent",
            "contested" to "0",
        ),
    )

    fun exposure(resource: String = "item.os.paging.q1", kind: String = "item_version_seen") = TruthRecord(
        "exposure_record", at,
        mapOf("resource_logical_id" to resource, "resource_version" to "1", "exposure_kind" to kind),
    )

    fun disposition(evidenceId: Long, disposition: String = "invalidated") = TruthRecord(
        "evidence_disposition", at,
        mapOf(
            "evidence_event_id" to evidenceId.toString(),
            "disposition" to disposition,
            "reason_code" to "answer_key_error",
            "decided_by" to "validator",
        ),
    )

    fun skillState(
        state: String = "developing_with_support",
        watermark: Long = 1,
        key: String = "skill_state:skill.os.paging@v1",
    ) = ProjectionRecord(
        key = key,
        policyVersion = "gre-v0",
        truthWatermark = watermark,
        builtAtInstant = at.instantEpochMillis,
        inputCurriculumVersion = 1,
        payload = mapOf(
            "mastery_axis_state" to "developing",
            "retention_axis_state" to "not_due",
            "prerequisite_axis_state" to "satisfied",
            "weakness_axis_state" to "none",
            "primary_presentation_state" to state,
        ),
    )

    fun tempDb(): File =
        File.createTempFile("coach-", ".db").also { it.delete(); it.deleteOnExit() }

    /**
     * A database at schema [version] holding real truth, as a previous build would have left it:
     * 25 evidence events, 10 exposure records and a disposition.
     */
    fun populated(file: File, version: Int = Schema.VERSION, skillPrefix: String = "skill.fixture.s") {
        BundledSQLiteDriver().open(file.absolutePath).also { connection ->
            connection.execSQL("PRAGMA foreign_keys = ON")
            Migrations.migrate(connection, target = version)
            connection.close()
        }
        SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            repeat(25) { db.appendTruth(evidence(skill = "$skillPrefix$it")) }
            repeat(10) { db.appendTruth(exposure(resource = "item.fixture.i$it")) }
            db.appendTruth(disposition(3))
        }
    }

    /** Every row of every truth table plus the metadata row, as text, in a stable order. */
    fun truthContent(db: SqlitePersistence): Map<String, List<String>> =
        (Schema.truthTables + "schema_metadata").associateWith { table ->
            db.query("SELECT * FROM $table ORDER BY rowid") { s ->
                (0 until s.getColumnCount()).joinToString("|") { if (s.isNull(it)) "∅" else s.getText(it) }
            }
        }

    /** Seeds one valid row in [table], creating whatever parent rows it references. */
    fun seed(db: SqlitePersistence, table: String) {
        if (db.count(table) > 0) return
        when (table) {
            "assessment_session" -> db.appendTruth(session())
            "attempt" -> db.appendTruth(attempt())
            "artifact" -> {
                seed(db, "attempt")
                db.appendTruth(TruthRecord("artifact", at, mapOf("attempt_id" to "1", "content_ref" to "artifact://1")))
            }
            "assistance_event" -> {
                seed(db, "attempt")
                db.appendTruth(
                    TruthRecord("assistance_event", at, mapOf(
                        "attempt_id" to "1", "level" to "H1", "timing" to "during_attempt",
                        "target_scope" to "target_objective", "source" to "deterministic_content",
                        "requested_by_user" to "1",
                    ))
                )
            }
            "artifact_provenance" -> {
                seed(db, "artifact")
                db.appendTruth(TruthRecord("artifact_provenance", at, mapOf("artifact_id" to "1", "origin" to "user_authored")))
            }
            "evidence_event" -> db.appendTruth(evidence())
            "evidence_event_objective" -> {
                seed(db, "evidence_event")
                db.appendTruth(
                    TruthRecord("evidence_event_objective", at, mapOf(
                        "evidence_event_id" to "1", "objective_logical_id" to "obj.os.paging.cost", "objective_version" to "1",
                    ))
                )
            }
            "exposure_record" -> db.appendTruth(exposure())
            "plan_version" -> db.appendTruth(TruthRecord("plan_version", at, mapOf("policy_version" to "pdt-v0")))
            "planned_task" -> {
                seed(db, "plan_version")
                db.appendTruth(
                    TruthRecord("planned_task", at, mapOf(
                        "plan_version_id" to "1", "skill_logical_id" to "skill.os.paging", "skill_version" to "1", "position" to "0",
                    ))
                )
            }
            "planner_decision_trace" -> {
                seed(db, "plan_version")
                db.appendTruth(TruthRecord("planner_decision_trace", at, mapOf("plan_version_id" to "1", "trace" to "{}")))
            }
            "resume_checkpoint" -> db.appendTruth(TruthRecord("resume_checkpoint", at, mapOf("context" to "{}")))
            "evidence_disposition" -> {
                seed(db, "evidence_event")
                db.appendTruth(disposition(1, "contested"))
            }
            else -> error("no seed for $table")
        }
    }
}
