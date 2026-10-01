package coach.persistence

/**
 * `DDM-v0 / D-077`'s physical schema, table for table.
 *
 * The schema **enforces** the architecture rather than describing it. Append-only truth, version
 * pinning, immutable curriculum and permanent exposure are guarantees a schema can quietly repeal,
 * so each is expressed as something the storage engine refuses — not something calling code is
 * trusted to avoid.
 *
 * Three store regions (`DDM-v0` §store_regions), and **no foreign key crosses from the user store
 * into the curriculum store**: a curriculum update must never be able to change what a learner
 * has demonstrated. Pinning is structural instead — every user-side reference carries both the
 * logical id and the version.
 *
 * Every timestamped row stores instant, learner-local study day and UTC offset in minutes
 * (`DDM-v0` §time_representation): after a DST change or travel, none of the three can be derived
 * reliably from the others.
 */
object Schema {

    const val VERSION = 3

    // ---------------------------------------------------------------- table inventories

    /** Region 1 — curriculum. Immutable: a correction is a new version, never an edit. */
    val curriculumTables = listOf(
        "curriculum_version", "domain", "module", "topic", "skill", "objective",
        "topic_skill_link", "skill_prerequisite_edge",
        "assessment_resource", "assessment_resource_version", "resource_validation_record",
    )

    /**
     * Region 2 — user truth. Append-only. Matches `DDM-v0` §truth_entities exactly, plus
     * `evidence_event_objective`, the relational form of `evidence_event`'s plural
     * `objective_logical_ids` / `objective_versions` fields: one row per targeted Objective, so
     * each keeps its own version pin and `evidence_by_objective` can be indexed.
     */
    val truthTables = listOf(
        "assessment_session", "attempt", "artifact", "assistance_event", "artifact_provenance",
        "evidence_event", "evidence_event_objective", "exposure_record",
        "plan_version", "planned_task", "planner_decision_trace", "resume_checkpoint",
        "evidence_disposition",
    )

    /** Region 3 — projections. Droppable and rebuildable without touching truth. */
    val projectionTables = listOf(
        "skill_state", "objective_state", "retention_state", "prerequisite_readiness",
        "topic_state", "weakness_state", "english_profile", "planner_summary",
    )

    /** `DDM-v0` §independent_axes — the allowed value sets, stated once. */
    val outcomeValues = listOf("positive", "negative", "partial", "invalid")
    val evaluatorStatusValues = listOf("verified", "provisional", "invalid")
    val independenceClassValues =
        listOf("independent", "assisted", "practice_only", "requires_independent_recheck")
    val dispositionValues = listOf("invalidated", "contested", "superseded", "reinstated")
    val decidedByValues = listOf("deterministic_rule", "validator", "user_report")
    val assistanceLevelValues = listOf("H1", "H2", "H3", "H4")
    val assistanceTimingValues = listOf("before_attempt", "during_attempt", "after_submit", "after_failure")
    val assistanceScopeValues = listOf("target_objective", "non_target_support")
    val assistanceSourceValues = listOf("deterministic_content", "ai_generated")
    val provenanceOriginValues = listOf(
        "user_authored", "user_authored_with_assistance", "mixed_authorship",
        "generated_or_copied", "unknown_provenance",
    )
    val exposureKindValues = listOf("solution_exposure", "item_version_seen", "variant_family_exposure")

    private fun oneOf(column: String, values: List<String>) =
        "CHECK ($column IN (${values.joinToString(", ") { "'$it'" }}))"

    /** The three time columns, with the instant/day names `DDM-v0` gives each entity. */
    private fun time(prefix: String = "occurred_at", dayPrefix: String = "occurred_on") = """
            ${prefix}_instant           INTEGER NOT NULL,
            ${dayPrefix}_study_day      TEXT    NOT NULL,
            utc_offset_minutes          INTEGER NOT NULL"""

    /**
     * `DDM-v0` §physical_schema: truth tables carry a monotonic sequence, and that sequence is the
     * projection watermark. It is global across truth tables, so a projection can tell it is stale
     * whichever kind of truth arrived after it was built.
     */
    private const val SEQUENCE = "sequence INTEGER NOT NULL UNIQUE"

    // ---------------------------------------------------------------- metadata

    private val metadata = listOf(
        """
        CREATE TABLE IF NOT EXISTS schema_metadata (
            schema_version  INTEGER NOT NULL,
            policy_version  TEXT    NOT NULL
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS truth_sequence (
            value INTEGER NOT NULL
        )
        """,
        "INSERT INTO truth_sequence (value) SELECT 0 WHERE NOT EXISTS (SELECT 1 FROM truth_sequence)",
    )

    // ---------------------------------------------------------------- region 1: curriculum

    private val curriculum = listOf(
        """
        CREATE TABLE IF NOT EXISTS curriculum_version (
            version              INTEGER PRIMARY KEY,
            published_at_instant INTEGER,
            source_refs          TEXT    NOT NULL,
            provenance           TEXT    NOT NULL
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS domain (
            logical_id TEXT NOT NULL, version INTEGER NOT NULL, name TEXT NOT NULL,
            PRIMARY KEY (logical_id, version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS module (
            logical_id TEXT NOT NULL, version INTEGER NOT NULL, name TEXT NOT NULL,
            PRIMARY KEY (logical_id, version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS topic (
            logical_id TEXT NOT NULL, version INTEGER NOT NULL, name TEXT NOT NULL,
            PRIMARY KEY (logical_id, version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS skill (
            logical_id                   TEXT    NOT NULL,
            version                      INTEGER NOT NULL,
            canonical_name               TEXT    NOT NULL,
            capability_statement         TEXT    NOT NULL,
            lifecycle_status             TEXT    NOT NULL,
            capability_kind              TEXT    NOT NULL,
            retention_profile            TEXT    NOT NULL,
            critical_prerequisite        INTEGER NOT NULL DEFAULT 0,
            remediation_tags             TEXT,
            professional_capability_tags TEXT,
            project_capability_tags      TEXT,
            source_refs                  TEXT    NOT NULL,
            provenance                   TEXT    NOT NULL,
            freshness_policy_ref         TEXT,
            aliases                      TEXT,
            PRIMARY KEY (logical_id, version),
            CHECK (critical_prerequisite IN (0, 1))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS objective (
            logical_id                TEXT    NOT NULL,
            version                   INTEGER NOT NULL,
            parent_skill_logical_id   TEXT    NOT NULL,
            parent_skill_version      INTEGER NOT NULL,
            required                  INTEGER NOT NULL DEFAULT 1,
            criticality               TEXT    NOT NULL,
            acceptable_evidence_types TEXT    NOT NULL,
            direct_evidence_types     TEXT    NOT NULL,
            required_direct_type      TEXT,
            PRIMARY KEY (logical_id, version),
            -- A reference without the version column would re-point at the newest Skill the
            -- moment curriculum is updated; the key carries both (DDM-v0 §5.3).
            FOREIGN KEY (parent_skill_logical_id, parent_skill_version)
                REFERENCES skill (logical_id, version),
            CHECK (required IN (0, 1))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS topic_skill_link (
            topic_logical_id TEXT NOT NULL, topic_version INTEGER NOT NULL,
            skill_logical_id TEXT NOT NULL, skill_version INTEGER NOT NULL,
            PRIMARY KEY (topic_logical_id, topic_version, skill_logical_id, skill_version),
            FOREIGN KEY (topic_logical_id, topic_version) REFERENCES topic (logical_id, version),
            FOREIGN KEY (skill_logical_id, skill_version) REFERENCES skill (logical_id, version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS skill_prerequisite_edge (
            prerequisite_skill_logical_id TEXT    NOT NULL,
            prerequisite_skill_version    INTEGER NOT NULL,
            target_skill_logical_id       TEXT    NOT NULL,
            target_skill_version          INTEGER NOT NULL,
            edge_version                  INTEGER NOT NULL,
            edge_kind                     TEXT    NOT NULL,
            reason_kind                   TEXT    NOT NULL,
            strictness_profile            TEXT    NOT NULL,
            lifecycle_status              TEXT    NOT NULL,
            provenance                    TEXT    NOT NULL,
            PRIMARY KEY (prerequisite_skill_logical_id, prerequisite_skill_version,
                         target_skill_logical_id, target_skill_version, edge_version),
            FOREIGN KEY (prerequisite_skill_logical_id, prerequisite_skill_version)
                REFERENCES skill (logical_id, version),
            FOREIGN KEY (target_skill_logical_id, target_skill_version)
                REFERENCES skill (logical_id, version),
            CHECK (edge_kind IN ('hard', 'soft'))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS assessment_resource (
            logical_id TEXT PRIMARY KEY
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS assessment_resource_version (
            logical_id           TEXT    NOT NULL,
            version              INTEGER NOT NULL,
            content_ref          TEXT    NOT NULL,
            rubric_ref           TEXT,
            evidence_type        TEXT    NOT NULL,
            allowed_tools_policy TEXT    NOT NULL,
            variant_family_id    TEXT,
            dependency_group_id  TEXT,
            content_origin       TEXT    NOT NULL,
            PRIMARY KEY (logical_id, version),
            FOREIGN KEY (logical_id) REFERENCES assessment_resource (logical_id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS resource_validation_record (
            logical_id           TEXT    NOT NULL,
            version              INTEGER NOT NULL,
            validated_at_instant INTEGER NOT NULL,
            validation_status    TEXT    NOT NULL,
            validator            TEXT    NOT NULL,
            origin               TEXT    NOT NULL,
            PRIMARY KEY (logical_id, version, validated_at_instant),
            FOREIGN KEY (logical_id, version) REFERENCES assessment_resource_version (logical_id, version)
        )
        """,
    )

    // ---------------------------------------------------------------- region 2: user truth

    private val truth = listOf(
        """
        CREATE TABLE IF NOT EXISTS assessment_session (
            id       INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            scope    TEXT    NOT NULL,${time()},
            CHECK (scope IN ('daily', 'weekly', 'monthly'))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS attempt (
            id                    INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            assessment_session_id INTEGER,
            resource_logical_id   TEXT    NOT NULL,
            resource_version      INTEGER NOT NULL,${time()},
            FOREIGN KEY (assessment_session_id) REFERENCES assessment_session (id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS artifact (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            attempt_id  INTEGER NOT NULL,
            content_ref TEXT    NOT NULL,${time()},
            FOREIGN KEY (attempt_id) REFERENCES attempt (id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS assistance_event (
            id                INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            attempt_id        INTEGER NOT NULL,
            level             TEXT    NOT NULL,
            timing            TEXT    NOT NULL,
            target_scope      TEXT    NOT NULL,
            source            TEXT    NOT NULL,
            requested_by_user INTEGER NOT NULL,${time()},
            FOREIGN KEY (attempt_id) REFERENCES attempt (id),
            ${oneOf("level", assistanceLevelValues)},
            ${oneOf("timing", assistanceTimingValues)},
            ${oneOf("target_scope", assistanceScopeValues)},
            ${oneOf("source", assistanceSourceValues)},
            CHECK (requested_by_user IN (0, 1))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS artifact_provenance (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            artifact_id INTEGER NOT NULL,
            -- Asked, not inferred (TRUX-v0): the value is the learner's answer.
            origin      TEXT    NOT NULL,${time("answered_at", "answered_on")},
            FOREIGN KEY (artifact_id) REFERENCES artifact (id),
            ${oneOf("origin", provenanceOriginValues)}
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS evidence_event (
            id                           INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,${time()},
            skill_logical_id             TEXT    NOT NULL,
            skill_version                INTEGER NOT NULL,
            evidence_type                TEXT    NOT NULL,
            source_attempt_id            INTEGER,
            resource_logical_id          TEXT,
            resource_version             INTEGER,
            artifact_id                  INTEGER,
            variant_family_id            TEXT,
            -- DDM-v0: four independent axes, four columns. Folding evaluator status into outcome
            -- would make provisional indistinguishable from settled.
            outcome                      TEXT    NOT NULL,
            evaluator_status             TEXT    NOT NULL,
            independence_class           TEXT    NOT NULL,
            contested                    INTEGER NOT NULL DEFAULT 0,
            correctness_or_rubric_result TEXT,
            difficulty                   TEXT,
            novelty_familiarity          TEXT,
            assistance_context           TEXT,
            duration_ms                  INTEGER,
            prerequisite_snapshot        TEXT,
            delay_since_last_exposure_ms INTEGER,
            -- AIAX-v0: an AI-derived row records provider, model and prompt/schema version.
            evaluator                    TEXT,
            provenance                   TEXT,
            misconception_tags           TEXT,
            FOREIGN KEY (source_attempt_id) REFERENCES attempt (id),
            FOREIGN KEY (artifact_id) REFERENCES artifact (id),
            ${oneOf("outcome", outcomeValues)},
            ${oneOf("evaluator_status", evaluatorStatusValues)},
            ${oneOf("independence_class", independenceClassValues)},
            CHECK (contested IN (0, 1)),
            -- A resource reference is pinned or absent, never version-free.
            CHECK ((resource_logical_id IS NULL) = (resource_version IS NULL))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS evidence_event_objective (
            id                   INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            evidence_event_id    INTEGER NOT NULL,
            objective_logical_id TEXT    NOT NULL,
            objective_version    INTEGER NOT NULL,
            FOREIGN KEY (evidence_event_id) REFERENCES evidence_event (id),
            UNIQUE (evidence_event_id, objective_logical_id, objective_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS exposure_record (
            id                  INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            resource_logical_id TEXT    NOT NULL,
            resource_version    INTEGER NOT NULL,
            variant_family_id   TEXT,
            exposure_kind       TEXT    NOT NULL,
            max_exposure_level  TEXT,${time()},
            source_attempt_id   INTEGER,
            FOREIGN KEY (source_attempt_id) REFERENCES attempt (id),
            ${oneOf("exposure_kind", exposureKindValues)}
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS plan_version (
            id             INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            policy_version TEXT    NOT NULL,${time()}
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS planned_task (
            id               INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            plan_version_id  INTEGER NOT NULL,
            skill_logical_id TEXT    NOT NULL,
            skill_version    INTEGER NOT NULL,
            position         INTEGER NOT NULL,${time()},
            FOREIGN KEY (plan_version_id) REFERENCES plan_version (id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS planner_decision_trace (
            id              INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            plan_version_id INTEGER NOT NULL,
            trace           TEXT    NOT NULL,${time()},
            FOREIGN KEY (plan_version_id) REFERENCES plan_version (id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS resume_checkpoint (
            id      INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            context TEXT    NOT NULL,${time()}
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS evidence_disposition (
            id                INTEGER PRIMARY KEY AUTOINCREMENT,
            $SEQUENCE,
            evidence_event_id INTEGER NOT NULL,
            disposition       TEXT    NOT NULL,
            reason_code       TEXT    NOT NULL,
            decided_by        TEXT    NOT NULL,${time("decided_at", "decided_on")},
            FOREIGN KEY (evidence_event_id) REFERENCES evidence_event (id),
            ${oneOf("disposition", dispositionValues)},
            ${oneOf("decided_by", decidedByValues)}
        )
        """,
    )

    /**
     * `DDM-v0` §physical_schema index intent, one index per stated read path. Exposure is looked
     * up on the selection hot path: never serving a solution-exposed item as a fresh independent
     * check has to be cheap to ask.
     */
    private val indexes = listOf(
        "CREATE INDEX IF NOT EXISTS evidence_by_skill ON evidence_event (skill_logical_id, skill_version)",
        "CREATE INDEX IF NOT EXISTS evidence_by_objective ON evidence_event_objective (objective_logical_id, objective_version)",
        "CREATE INDEX IF NOT EXISTS evidence_by_time_range ON evidence_event (occurred_at_instant)",
        "CREATE INDEX IF NOT EXISTS exposure_by_resource_logical_id ON exposure_record (resource_logical_id, resource_version)",
        "CREATE INDEX IF NOT EXISTS exposure_by_variant_family ON exposure_record (variant_family_id)",
        "CREATE INDEX IF NOT EXISTS exposure_by_exposure_kind ON exposure_record (exposure_kind)",
        "CREATE INDEX IF NOT EXISTS attempts_by_assessment_session ON attempt (assessment_session_id)",
        "CREATE INDEX IF NOT EXISTS planned_tasks_by_plan_version ON planned_task (plan_version_id)",
        "CREATE INDEX IF NOT EXISTS dispositions_by_evidence_event ON evidence_disposition (evidence_event_id)",
    )

    // ---------------------------------------------------------------- region 3: projections

    /** `DDM-v0` §projection_provenance: required on every projection row. */
    private const val PROJECTION_PROVENANCE = """
            policy_version           TEXT    NOT NULL,
            truth_watermark          INTEGER NOT NULL,
            built_at_instant         INTEGER NOT NULL,
            input_curriculum_version INTEGER NOT NULL"""

    private val projection = listOf(
        """
        CREATE TABLE IF NOT EXISTS skill_state (
            skill_logical_id           TEXT    NOT NULL,
            skill_version              INTEGER NOT NULL,
            -- The four axes stay separate in storage too; the presentation state is derived from
            -- them and never replaces them (DDM-v0, SPWX-v0).
            mastery_axis_state         TEXT    NOT NULL,
            retention_axis_state       TEXT    NOT NULL,
            prerequisite_axis_state    TEXT    NOT NULL,
            weakness_axis_state        TEXT    NOT NULL,
            primary_presentation_state TEXT    NOT NULL,$PROJECTION_PROVENANCE,
            PRIMARY KEY (skill_logical_id, skill_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS objective_state (
            objective_logical_id TEXT NOT NULL, objective_version INTEGER NOT NULL,
            state TEXT NOT NULL,$PROJECTION_PROVENANCE,
            PRIMARY KEY (objective_logical_id, objective_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS retention_state (
            skill_logical_id TEXT NOT NULL, skill_version INTEGER NOT NULL,
            state TEXT NOT NULL,$PROJECTION_PROVENANCE,
            PRIMARY KEY (skill_logical_id, skill_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS prerequisite_readiness (
            skill_logical_id TEXT NOT NULL, skill_version INTEGER NOT NULL,
            state TEXT NOT NULL,$PROJECTION_PROVENANCE,
            PRIMARY KEY (skill_logical_id, skill_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS topic_state (
            topic_logical_id TEXT NOT NULL, topic_version INTEGER NOT NULL,
            state TEXT NOT NULL,$PROJECTION_PROVENANCE,
            PRIMARY KEY (topic_logical_id, topic_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS weakness_state (
            objective_logical_id TEXT NOT NULL, objective_version INTEGER NOT NULL,
            state TEXT NOT NULL,$PROJECTION_PROVENANCE,
            PRIMARY KEY (objective_logical_id, objective_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS english_profile (
            skill_logical_id TEXT NOT NULL, skill_version INTEGER NOT NULL,
            state TEXT NOT NULL,
            qualified_band_summary TEXT,$PROJECTION_PROVENANCE,
            PRIMARY KEY (skill_logical_id, skill_version)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS planner_summary (
            summary_key TEXT PRIMARY KEY,
            summary     TEXT NOT NULL,$PROJECTION_PROVENANCE
        )
        """,
    )

    // ---------------------------------------------------------------- guards

    /**
     * An UPDATE and a DELETE guard on every truth table. A correction is an appended
     * `evidence_disposition`, so there is no legitimate UPDATE path to leave open, and `TVSX-v0`
     * requires the refusal to come from the layer that forbids it.
     */
    private fun truthGuards(): List<String> = truthTables.flatMap { table ->
        listOf(
            """
            CREATE TRIGGER IF NOT EXISTS ${table}_no_update BEFORE UPDATE ON $table
            BEGIN SELECT RAISE(ABORT, '$table is append-only: correct by appending a disposition'); END
            """,
            """
            CREATE TRIGGER IF NOT EXISTS ${table}_no_delete BEFORE DELETE ON $table
            BEGIN SELECT RAISE(ABORT, '$table is append-only: rows are never deleted'); END
            """,
        )
    }

    /** Curriculum rows are never overwritten once written; a change is a new version. */
    private fun curriculumGuards(): List<String> = curriculumTables.flatMap { table ->
        listOf(
            """
            CREATE TRIGGER IF NOT EXISTS ${table}_no_update BEFORE UPDATE ON $table
            BEGIN SELECT RAISE(ABORT, '$table is immutable: publish a new version instead'); END
            """,
            """
            CREATE TRIGGER IF NOT EXISTS ${table}_no_delete BEFORE DELETE ON $table
            BEGIN SELECT RAISE(ABORT, '$table is immutable: user references are pinned to it'); END
            """,
        )
    }

    // ---------------------------------------------------------------- migrations

    /** Everything needed to create schema version 1. */
    val v1: List<String>
        get() = metadata + curriculum + curriculumGuards() + truth + truthGuards() + projection

    /**
     * Version 2 adds the `DDM-v0` read-path indexes. It is a real forward migration, so migrating
     * a **populated** database is exercised rather than described.
     */
    val v2: List<String>
        get() = indexes

    /**
     * Version 3 completes `assessment_session` (13A). `DDM-v0` describes it as "one session, its blocks
     * and boundaries", and 10D fixed only its scope, disclosing that 13 owns the rest. A weekly session's
     * blocks and boundaries are its blueprint, stored as strict `weekly_blueprint/1` text in one column —
     * no column is invented per slot.
     *
     * A weekly row without its blueprint is refused: a session whose measurement nobody can read is a
     * half-record. Rows written before this version are untouched (a column added to an append-only
     * table fills them with nothing, and nothing had ever written a session).
     */
    val v3: List<String>
        get() = listOf(
            "ALTER TABLE assessment_session ADD COLUMN blueprint TEXT CHECK (scope <> 'weekly' OR blueprint IS NOT NULL)",
            "CREATE INDEX IF NOT EXISTS assessment_sessions_by_scope ON assessment_session (scope, sequence)",
            "CREATE INDEX IF NOT EXISTS evidence_by_study_day ON evidence_event (occurred_on_study_day)",
        )
}
