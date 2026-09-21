package coach.persistence

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import coach.model.CurriculumPackage
import coach.model.ObjectiveEvidenceProfile
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.StudyTimestamp
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord

/**
 * Implements the core-owned `PersistencePort` on SQLite.
 *
 * The bundled driver runs on the JVM as well as on the device, which is what lets `TVSX-v0` tier
 * T2 exercise a **real** storage engine off-device instead of only on a phone. Nothing here is a
 * fake: the same schema, the same triggers, the same transactions.
 *
 * The adapter reads each table's columns from the database itself rather than keeping its own
 * list. A second hand-maintained column list is exactly the kind of thing that drifts from the
 * schema quietly; asking SQLite means the DDL is the only source.
 */
class SqlitePersistence private constructor(
    private val connection: SQLiteConnection,
) : PersistencePort, AutoCloseable {

    companion object {
        /** `:memory:` is a real SQLite database, so T2 checks are not testing a stub. */
        const val IN_MEMORY = ":memory:"

        /**
         * Opens and migrates, **throwing** [Migrations.DataRecoveryRequired] when that is unsafe.
         * The product does not call this: it uses [StoreOpener.open], which checks integrity first
         * and returns a status instead of throwing (`APHX-v0`). Tests use it where a throw is the
         * point.
         */
        fun open(path: String, driver: SQLiteDriver = BundledSQLiteDriver()): SqlitePersistence {
            val connection = driver.open(path)
            connection.execSQL("PRAGMA foreign_keys = ON")
            Migrations.migrate(connection)
            return SqlitePersistence(connection)
        }

        /** Wraps a connection [StoreOpener] has already checked and migrated. */
        internal fun onCheckedConnection(connection: SQLiteConnection): SqlitePersistence =
            SqlitePersistence(connection)

        /**
         * Opens a database at whatever schema it already has. Test support only: it exists so a
         * T2 check can populate an **older** schema exactly as a previous build would have left
         * it, and then prove the forward migration preserves that truth.
         */
        internal fun openWithoutMigrating(
            path: String,
            driver: SQLiteDriver = BundledSQLiteDriver(),
        ): SqlitePersistence {
            val connection = driver.open(path)
            connection.execSQL("PRAGMA foreign_keys = ON")
            return SqlitePersistence(connection)
        }
    }

    private data class Column(val name: String, val notNull: Boolean, val hasDefault: Boolean)

    private val columnCache = mutableMapOf<String, List<Column>>()

    private val curriculumStore = CurriculumStore(connection)

    private fun columns(table: String): List<Column> = columnCache.getOrPut(table) {
        query("PRAGMA table_info($table)") { s ->
            Column(name = s.getText(1), notNull = s.getLong(3) == 1L, hasDefault = !s.isNull(4))
        }
    }

    /**
     * One learner action commits as one transaction (`LFPS-v0` §9). A failure anywhere inside
     * leaves nothing observable behind, which is why the rollback is not optional and the original
     * error is rethrown rather than swallowed.
     */
    override fun <T> inTransaction(block: () -> T): T {
        connection.execSQL("BEGIN")
        return try {
            val result = block()
            connection.execSQL("COMMIT")
            result
        } catch (error: Throwable) {
            runCatching { connection.execSQL("ROLLBACK") }
            throw error
        }
    }

    /**
     * Appends a truth row. There is no update counterpart, here or in the schema: a correction is
     * an appended `evidence_disposition`.
     *
     * The adapter fills only what it owns — the id, the global truth sequence and the three time
     * columns. Every other `NOT NULL` column must arrive in the payload; a row cannot be written
     * without, say, an attempt's resource version or a disposition's reason.
     */
    override fun appendTruth(record: TruthRecord): Long {
        require(record.kind in Schema.truthTables) { "not a truth table: ${record.kind}" }
        val offsetSeconds = record.recordedAt.utcOffsetSeconds
        // DDM-v0 stores the offset in minutes. Every real zone offset is a whole number of
        // minutes; a value that is not is refused rather than silently truncated.
        require(offsetSeconds % 60 == 0) { "UTC offset $offsetSeconds s is not a whole number of minutes" }

        val all = columns(record.kind)
        // Timestamped rows carry all three time columns. A row that is part of another timestamped
        // row (an evidence event's targeted Objectives) carries none, and gets none.
        val instant = all.singleOrNull { it.name.endsWith("_at_instant") }?.name
        val studyDay = all.singleOrNull { it.name.endsWith("_study_day") }?.name
        val timeColumns = listOfNotNull(instant, studyDay, all.singleOrNull { it.name == "utc_offset_minutes" }?.name)
        check(timeColumns.isEmpty() || timeColumns.size == 3) {
            "${record.kind} must carry all three time columns or none (DDM-v0), found $timeColumns"
        }
        val adapterOwned = setOf("id", "sequence") + timeColumns

        val writable = all.map { it.name }.filterNot { it in adapterOwned }
        val required = all.filter { it.notNull && !it.hasDefault && it.name !in adapterOwned }.map { it.name }

        val missing = required.filterNot { record.payload.containsKey(it) }
        require(missing.isEmpty()) { "${record.kind} is missing required columns: $missing" }
        val unknown = record.payload.keys.filterNot { it in writable }
        require(unknown.isEmpty()) { "${record.kind} has no columns named: $unknown" }

        val sequence = nextSequence()
        val payloadColumns = writable.filter { record.payload.containsKey(it) }
        val insertColumns = payloadColumns + listOf("sequence") + timeColumns
        val sql = "INSERT INTO ${record.kind} (${insertColumns.joinToString(", ")}) " +
            "VALUES (${insertColumns.joinToString(", ") { "?" }})"

        connection.prepare(sql).use { statement ->
            var index = 1
            payloadColumns.forEach { column ->
                bindValue(statement, index++, record.payload.getValue(column))
            }
            statement.bindLong(index++, sequence)
            if (timeColumns.isNotEmpty()) {
                statement.bindLong(index++, record.recordedAt.instantEpochMillis)
                statement.bindText(index++, record.recordedAt.studyDay)
                statement.bindLong(index, (offsetSeconds / 60).toLong())
            }
            statement.step()
        }
        return query("SELECT last_insert_rowid()") { it.getLong(0) }.single()
    }

    /**
     * Reads a truth row back exactly as it was appended: the payload columns as text, the three time
     * columns as the [StudyTimestamp] they were written from. Id and sequence are the adapter's and
     * are not part of the payload, so a read-back record is the record that was appended.
     */
    override fun readTruth(kind: String, id: Long): TruthRecord? {
        require(kind in Schema.truthTables) { "not a truth table: $kind" }
        val all = columns(kind).map { it.name }
        // A row that is part of another row (an evidence event's Objectives) has no time of its own
        // and is read through its parent, not on its own.
        val instant = requireNotNull(all.singleOrNull { it.endsWith("_at_instant") }) { "$kind has no time of its own" }
        val studyDay = all.single { it.endsWith("_study_day") }
        val payloadColumns = all.filterNot { it in setOf("id", "sequence", instant, studyDay, "utc_offset_minutes") }
        val selected = listOf(instant, studyDay, "utc_offset_minutes") + payloadColumns
        connection.prepare("SELECT ${selected.joinToString(", ")} FROM $kind WHERE id = ?").use { statement ->
            statement.bindLong(1, id)
            if (!statement.step()) return null
            return TruthRecord(
                kind = kind,
                recordedAt = StudyTimestamp(
                    instantEpochMillis = statement.getLong(0),
                    studyDay = statement.getText(1),
                    utcOffsetSeconds = (statement.getLong(2) * 60).toInt(),
                ),
                payload = payloadColumns.withIndex()
                    .filterNot { (i, _) -> statement.isNull(i + 3) }
                    .associate { (i, column) -> column to statement.getText(i + 3) },
            )
        }
    }

    /**
     * Projection keys name the table and the entity, e.g. `skill_state:skill.os.paging@v1` or
     * `planner_summary:today`. Entity-keyed projections always carry the version, so a projection
     * cannot be looked up against a version-free reference either.
     */
    override fun readProjection(key: String): ProjectionRecord? {
        val target = ProjectionKey.parse(key)
        val stateColumns = stateColumns(target.table)
        val selected = stateColumns + listOf("policy_version", "truth_watermark", "built_at_instant", "input_curriculum_version")
        val sql = "SELECT ${selected.joinToString(", ")} FROM ${target.table} WHERE ${target.whereClause()}"
        connection.prepare(sql).use { statement ->
            target.bind(statement)
            if (!statement.step()) return null
            val base = stateColumns.size
            return ProjectionRecord(
                key = key,
                policyVersion = statement.getText(base),
                truthWatermark = statement.getLong(base + 1),
                builtAtInstant = statement.getLong(base + 2),
                inputCurriculumVersion = statement.getLong(base + 3).toInt(),
                payload = stateColumns.withIndex().associate { (i, column) ->
                    column to (if (statement.isNull(i)) "" else statement.getText(i))
                },
            )
        }
    }

    /**
     * Projections are rebuildable, so this one really does replace. Every row carries the
     * provenance `DDM-v0` requires, so a stale projection is detectable rather than
     * indistinguishable from a fresh one.
     */
    override fun writeProjection(record: ProjectionRecord) {
        val target = ProjectionKey.parse(record.key)
        val stateColumns = stateColumns(target.table)
        val required = columns(target.table)
            .filter { it.notNull && it.name in stateColumns }
            .map { it.name }
        val missing = required.filterNot { record.payload.containsKey(it) }
        require(missing.isEmpty()) { "${target.table} projection is missing: $missing" }
        val unknown = record.payload.keys.filterNot { it in stateColumns }
        require(unknown.isEmpty()) { "${target.table} has no state columns named: $unknown" }

        val provided = stateColumns.filter { record.payload.containsKey(it) }
        val columns = target.keyColumns + provided +
            listOf("policy_version", "truth_watermark", "built_at_instant", "input_curriculum_version")
        val updates = (provided + listOf("policy_version", "truth_watermark", "built_at_instant", "input_curriculum_version"))
            .joinToString(", ") { "$it = excluded.$it" }
        val sql = "INSERT INTO ${target.table} (${columns.joinToString(", ")}) " +
            "VALUES (${columns.joinToString(", ") { "?" }}) " +
            "ON CONFLICT (${target.keyColumns.joinToString(", ")}) DO UPDATE SET $updates"

        connection.prepare(sql).use { statement ->
            var index = target.bind(statement) + 1
            provided.forEach { bindValue(statement, index++, record.payload.getValue(it)) }
            statement.bindText(index++, record.policyVersion)
            statement.bindLong(index++, record.truthWatermark)
            statement.bindLong(index++, record.builtAtInstant)
            statement.bindLong(index, record.inputCurriculumVersion.toLong())
            statement.step()
        }
    }

    /**
     * Whether any curriculum version has been published. A published `skill` row is the anchor:
     * `KGC-v0` makes the Skill the capability identity everything else attributes to, so a store
     * with topics but no Skills has published nothing a learner can be planned against.
     *
     * Curriculum ingestion itself is not 11A's: this only reports what the store holds.
     */
    override fun curriculumPublished(): Boolean =
        query("SELECT EXISTS (SELECT 1 FROM skill)") { it.getLong(0) == 1L }.single()

    /**
     * Publishes one curriculum version in one transaction (11D). A refused or already-published
     * package leaves the store exactly as it was, because the refusal is decided before anything is
     * written and the whole publish rolls back together if a write fails.
     */
    override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome {
        curriculumStore.refusal(curriculum)?.let { return it }
        return inTransaction { curriculumStore.write(curriculum, publishedAtInstant) }
    }

    override fun resourceVersion(ref: VersionedRef): ResourceVersion? = curriculumStore.resourceVersion(ref)

    override fun latestValidation(ref: VersionedRef): ValidationRecord? = curriculumStore.latestValidation(ref)

    override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = curriculumStore.objectiveProfile(ref)

    /**
     * Counts by the row's own study-day column. A table whose rows carry no study day of their own
     * is refused rather than counted through its parent's day, and the instant columns are never
     * used for this: the day a row belongs to is the day it recorded, not the day its instant falls
     * in for whoever is reading (`DDM-v0` §three-value time, 11E).
     */
    override fun countTruth(kind: String, studyDay: String): Int {
        require(kind in Schema.truthTables) { "not a truth table: $kind" }
        val dayColumn = columns(kind).map { it.name }.singleOrNull { it.endsWith("_study_day") }
        requireNotNull(dayColumn) { "$kind has no study day of its own" }
        connection.prepare("SELECT COUNT(*) FROM $kind WHERE $dayColumn = ?").use { statement ->
            statement.bindText(1, studyDay)
            statement.step()
            return statement.getLong(0).toInt()
        }
    }

    /**
     * The global truth sequence: every truth row of every kind advances it. It is the watermark a
     * projection is computed from (`DDM-v0` §physical_schema).
     */
    fun truthWatermark(): Long = query("SELECT value FROM truth_sequence") { it.getLong(0) }.single()

    private fun nextSequence(): Long {
        connection.execSQL("UPDATE truth_sequence SET value = value + 1")
        return truthWatermark()
    }

    private fun stateColumns(table: String): List<String> {
        require(table in Schema.projectionTables) { "not a projection table: $table" }
        val provenance = setOf("policy_version", "truth_watermark", "built_at_instant", "input_curriculum_version")
        return columns(table).map { it.name }
            .filterNot { it in provenance || it in ProjectionKey.keyColumnsOf(table) }
    }

    private fun bindValue(statement: SQLiteStatement, index: Int, value: String) {
        val asLong = value.toLongOrNull()
        if (asLong != null) statement.bindLong(index, asLong) else statement.bindText(index, value)
    }

    fun execute(sql: String) = connection.execSQL(sql)

    fun <T> query(sql: String, read: (SQLiteStatement) -> T): List<T> {
        val rows = mutableListOf<T>()
        connection.prepare(sql).use { statement ->
            while (statement.step()) rows += read(statement)
        }
        return rows
    }

    fun count(table: String): Long = query("SELECT COUNT(*) FROM $table") { it.getLong(0) }.single()

    /**
     * Writes a consistent, compacted copy of the whole database to [path]. SQLite produces it from
     * a single read transaction, so the copy can never contain half of a learner action.
     */
    internal fun vacuumInto(path: String) {
        connection.prepare("VACUUM INTO ?").use { statement ->
            statement.bindText(1, path)
            statement.step()
        }
    }

    override fun close() = connection.close()
}

/** Parses and binds projection keys of the form `table:logical_id@vN` or `planner_summary:key`. */
internal class ProjectionKey private constructor(
    val table: String,
    val keyColumns: List<String>,
    private val values: List<Any>,
) {
    fun whereClause(): String = keyColumns.joinToString(" AND ") { "$it = ?" }

    /** Binds the key values starting at parameter 1 and returns how many were bound. */
    fun bind(statement: SQLiteStatement): Int {
        values.forEachIndexed { i, value ->
            when (value) {
                is Long -> statement.bindLong(i + 1, value)
                else -> statement.bindText(i + 1, value.toString())
            }
        }
        return values.size
    }

    companion object {
        private val entityKeyed = mapOf(
            "skill_state" to listOf("skill_logical_id", "skill_version"),
            "retention_state" to listOf("skill_logical_id", "skill_version"),
            "prerequisite_readiness" to listOf("skill_logical_id", "skill_version"),
            "english_profile" to listOf("skill_logical_id", "skill_version"),
            "objective_state" to listOf("objective_logical_id", "objective_version"),
            "weakness_state" to listOf("objective_logical_id", "objective_version"),
            "topic_state" to listOf("topic_logical_id", "topic_version"),
        )

        fun keyColumnsOf(table: String): List<String> =
            entityKeyed[table] ?: if (table == "planner_summary") listOf("summary_key") else emptyList()

        fun parse(key: String): ProjectionKey {
            val table = key.substringBefore(":", missingDelimiterValue = "")
            val rest = key.substringAfter(":", missingDelimiterValue = "")
            require(table in Schema.projectionTables && rest.isNotEmpty()) {
                "projection key must be 'table:entity', got: $key"
            }
            if (table == "planner_summary") return ProjectionKey(table, listOf("summary_key"), listOf(rest))
            val parts = rest.split("@v")
            // A projection looked up by logical id alone would be exactly the version-free
            // reference DDM-v0 forbids.
            require(parts.size == 2) { "projection entity must be pinned as 'logical_id@vN', got: $rest" }
            val version = parts[1].toLongOrNull() ?: error("bad version in projection key: $key")
            return ProjectionKey(table, entityKeyed.getValue(table), listOf(parts[0], version))
        }
    }
}
