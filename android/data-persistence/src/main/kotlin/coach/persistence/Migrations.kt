package coach.persistence

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import coach.model.RecoveryReason

/**
 * Forward-only migration (`LFPS-v0` §10).
 *
 * A migration may discard and rebuild derived state, but it may **never** delete, rewrite or
 * reinterpret evidence, exposure or provenance — and the truth triggers make that structural
 * rather than a rule the migration author has to remember.
 */
object Migrations {

    /** The policy version recorded before any engine has produced a projection. */
    const val INITIAL_POLICY_VERSION = "none"

    /**
     * Raised when a database cannot be brought to the current schema safely. It carries the core
     * [RecoveryReason] so the app can say *why* without parsing a message (`APHX-v0`).
     */
    class DataRecoveryRequired(
        val reason: RecoveryReason,
        message: String,
        cause: Throwable? = null,
    ) : IllegalStateException(message, cause)

    /** Statements that take a database from the key version to key + 1. */
    private fun steps(): Map<Int, List<String>> = mapOf(
        0 to Schema.v1,
        1 to Schema.v2,
        2 to Schema.v3,
        3 to Schema.v4,
        4 to Schema.v5,
    )

    fun currentVersion(connection: SQLiteConnection): Int {
        connection.prepare(
            "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'schema_metadata'"
        ).use { statement ->
            if (!statement.step()) return 0
        }
        connection.prepare("SELECT schema_version FROM schema_metadata LIMIT 1").use { statement ->
            return if (statement.step()) statement.getLong(0).toInt() else 0
        }
    }

    /**
     * Brings the database to [target].
     *
     * - **Forward only.** A database from a newer schema is refused rather than opened on a guess,
     *   because reading a shape written by a later build is how evidence gets silently misread.
     * - **All or nothing.** Each step runs inside one transaction, so a migration that fails part
     *   way through leaves the previous state intact and surfaces [DataRecoveryRequired].
     */
    fun migrate(connection: SQLiteConnection, target: Int = Schema.VERSION) {
        val from = currentVersion(connection)
        if (from > target) {
            throw DataRecoveryRequired(
                RecoveryReason.NEWER_SCHEMA,
                "database schema $from is newer than this build's $target; downgrade is not supported",
            )
        }
        val steps = steps()
        var version = from
        while (version < target) {
            val statements = steps[version]
                ?: throw DataRecoveryRequired(RecoveryReason.MIGRATION_INCOMPLETE, "no migration from schema $version")
            try {
                connection.execSQL("BEGIN")
                statements.forEach { connection.execSQL(it.trimIndent()) }
                writeVersion(connection, version + 1)
                connection.execSQL("COMMIT")
            } catch (error: Throwable) {
                runCatching { connection.execSQL("ROLLBACK") }
                throw DataRecoveryRequired(
                    RecoveryReason.MIGRATION_INCOMPLETE,
                    "migration from schema $version failed; previous state left intact",
                    error,
                )
            }
            version += 1
        }
    }

    /** Rewrites the single metadata row, keeping the policy version it already records. */
    private fun writeVersion(connection: SQLiteConnection, version: Int) {
        val policy = connection.prepare("SELECT policy_version FROM schema_metadata LIMIT 1").use {
            if (it.step()) it.getText(0) else INITIAL_POLICY_VERSION
        }
        connection.execSQL("DELETE FROM schema_metadata")
        connection.prepare(
            "INSERT INTO schema_metadata (schema_version, policy_version) VALUES (?, ?)"
        ).use { statement ->
            statement.bindLong(1, version.toLong())
            statement.bindText(2, policy)
            statement.step()
        }
    }
}
