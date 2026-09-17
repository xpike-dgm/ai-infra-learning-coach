package coach.persistence

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import coach.model.RecoveryReason
import coach.model.StoreStatus

/**
 * The product's only way to open the store (`APHX-v0`, `LFPS-v0` §12).
 *
 * It never throws and it never writes to a database it has not checked. The order is the point:
 *
 * 1. **integrity first** — `quick_check` and `foreign_key_check` run before anything can write,
 *    so a corrupted file is reported exactly as it was found rather than "repaired" by a migration;
 * 2. **forward migration** — refused for a newer schema, all-or-nothing otherwise (`LDBX-v0`);
 * 3. **integrity again after a migration** — the full `integrity_check`, because a migration is the
 *    one moment this build rewrote the file.
 *
 * Every failure becomes a [StoreStatus]. None of them deletes, truncates or recreates the file:
 * a silent reset would turn a recoverable problem into permanent evidence loss.
 */
object StoreOpener {

    sealed interface Result {
        val status: StoreStatus

        class Opened(val store: SqlitePersistence) : Result {
            override val status: StoreStatus get() = StoreStatus.Ready
        }

        class NotOpened(override val status: StoreStatus, val cause: Throwable?) : Result
    }

    fun open(path: String, driver: SQLiteDriver = BundledSQLiteDriver()): Result {
        // Nothing has been read yet, so integrity is not in question and a retry is meaningful.
        val connection = try {
            driver.open(path)
        } catch (failure: Throwable) {
            return Result.NotOpened(StoreStatus.RecoverableFailure, failure)
        }

        return try {
            val found = Integrity.quickProblems(connection)
            if (found.isNotEmpty()) {
                return recovery(connection, RecoveryReason.INTEGRITY_CHECK_FAILED, IllegalStateException(found.joinToString("; ")))
            }
            connection.execSQL("PRAGMA foreign_keys = ON")

            val before = Migrations.currentVersion(connection)
            Migrations.migrate(connection)
            if (before != Schema.VERSION) {
                val afterMigration = Integrity.fullProblems(connection)
                if (afterMigration.isNotEmpty()) {
                    return recovery(connection, RecoveryReason.INTEGRITY_CHECK_FAILED, IllegalStateException(afterMigration.joinToString("; ")))
                }
            }
            Result.Opened(SqlitePersistence.onCheckedConnection(connection))
        } catch (refused: Migrations.DataRecoveryRequired) {
            recovery(connection, refused.reason, refused)
        } catch (unreadable: Throwable) {
            // A file SQLite cannot read as a database fails here ("file is not a database",
            // "database disk image is malformed"). Integrity is exactly what is in question.
            recovery(connection, RecoveryReason.INTEGRITY_CHECK_FAILED, unreadable)
        }
    }

    private fun recovery(connection: SQLiteConnection, reason: RecoveryReason, cause: Throwable): Result {
        runCatching { connection.close() }
        return Result.NotOpened(StoreStatus.RecoveryRequired(reason), cause)
    }
}

/** SQLite's own integrity checks, reported as the list of problems they found. Empty means sound. */
internal object Integrity {

    /** Page and structure checks, fast enough for every open; plus foreign-key consistency. */
    fun quickProblems(connection: SQLiteConnection): List<String> =
        pragmaProblems(connection, "PRAGMA quick_check") + foreignKeyProblems(connection)

    /** Adds index-content verification. Used after a migration and on every restore candidate. */
    fun fullProblems(connection: SQLiteConnection): List<String> =
        pragmaProblems(connection, "PRAGMA integrity_check") + foreignKeyProblems(connection)

    private fun pragmaProblems(connection: SQLiteConnection, pragma: String): List<String> {
        val rows = connection.prepare(pragma).use { statement ->
            buildList { while (statement.step()) add(statement.getText(0)) }
        }
        return if (rows == listOf("ok")) emptyList() else rows.ifEmpty { listOf("$pragma returned nothing") }
    }

    private fun foreignKeyProblems(connection: SQLiteConnection): List<String> =
        connection.prepare("PRAGMA foreign_key_check").use { statement ->
            buildList {
                while (statement.step()) add("foreign key violation in ${statement.getText(0)} row ${statement.getLong(1)}")
            }
        }
}
