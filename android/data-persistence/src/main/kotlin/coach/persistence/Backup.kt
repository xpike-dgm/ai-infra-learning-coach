package coach.persistence

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import coach.model.StoreStatus
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Backup, export and restore as `LFPS-v0` §11 defines them — the storage mechanism only.
 * Choosing a file, confirming a replacement and presenting a refusal are Profile controls owned
 * by 16D (`UXIA-v0` places backup/export/restore in Profile).
 *
 * The archive is a SQLite database of this product's own schema. That is complete by construction
 * — truth, exposure, provenance, curriculum pins and schema/policy version all live in the store —
 * and needs no second serialisation that could drift from the schema. No credential can be in it:
 * the API key lives in platform secure storage and the schema has no column for one (`AIAX-v0`).
 */
object Backup {

    /** Why a candidate archive was refused. The live profile is untouched in every case. */
    enum class Refusal {
        /** Not a readable database, or its integrity or foreign-key check failed. */
        INTEGRITY_CHECK_FAILED,

        /** A readable SQLite database, but not a profile written by this product. */
        NOT_A_PROFILE_ARCHIVE,

        /** Written by a newer build. Restoring it would be a guess (`LFPS-v0` §11.2). */
        NEWER_SCHEMA,

        /** An older archive whose forward migration could not complete. */
        MIGRATION_INCOMPLETE,
    }

    sealed interface ExportResult {
        data object Exported : ExportResult
        data class Failed(val refusal: Refusal) : ExportResult
    }

    sealed interface RestoreResult {
        data object Restored : RestoreResult
        data class Refused(val refusal: Refusal) : RestoreResult

        /**
         * The replacement happened but the reopened store did not come up ready. Verification ran
         * before replacing, so this should never occur; if it does it is reported, not hidden.
         */
        data class ReplacedButNotReady(val status: StoreStatus) : RestoreResult
    }

    private val sidecarSuffixes = listOf("-journal", "-wal", "-shm")

    /**
     * Writes a verified archive of [store] to [destination], which must not exist yet. The archive
     * is checked exactly as a restore would check it, so a backup that could not be restored is
     * found when it is made rather than on the day it is needed.
     */
    fun export(store: SqlitePersistence, destination: File, driver: SQLiteDriver = BundledSQLiteDriver()): ExportResult {
        require(!destination.exists()) { "refusing to overwrite an existing file: $destination" }
        store.vacuumInto(destination.absolutePath)
        val refusal = verifyCandidate(destination, driver)
        if (refusal != null) {
            destination.delete()
            return ExportResult.Failed(refusal)
        }
        return ExportResult.Exported
    }

    /**
     * Replaces the profile at [live] with [archive] — all of it or none of it.
     *
     * The caller must have closed any connection to [live]. The archive itself is never opened:
     * it is copied to a staging file beside the live database, and only the copy is verified and,
     * if older, migrated forward. The live file is replaced by one atomic rename, and only after
     * the staged copy has passed every check. A refused archive leaves the live profile
     * byte-for-byte as it was and removes the staging copy. There is no merge.
     */
    fun restore(archive: File, live: File, driver: SQLiteDriver = BundledSQLiteDriver()): RestoreResult {
        require(archive.isFile) { "no archive at $archive" }
        val staging = File(live.parentFile, live.name + ".restore-staging")
        deleteWithSidecars(staging)
        Files.copy(archive.toPath(), staging.toPath(), StandardCopyOption.REPLACE_EXISTING)

        val refusal = try {
            verifyCandidate(staging, driver, migrate = true)
        } catch (unexpected: Throwable) {
            Refusal.INTEGRITY_CHECK_FAILED
        }
        if (refusal != null) {
            deleteWithSidecars(staging)
            return RestoreResult.Refused(refusal)
        }

        replaceAtomically(staging, live)

        return when (val reopened = StoreOpener.open(live.absolutePath, driver)) {
            is StoreOpener.Result.Opened -> {
                reopened.store.close()
                RestoreResult.Restored
            }
            is StoreOpener.Result.NotOpened -> RestoreResult.ReplacedButNotReady(reopened.status)
        }
    }

    /**
     * Structural integrity and version compatibility (`LFPS-v0` §11.2), checked on [candidate].
     * With [migrate], an older archive is brought forward and checked again.
     */
    private fun verifyCandidate(candidate: File, driver: SQLiteDriver, migrate: Boolean = false): Refusal? {
        val connection = try {
            driver.open(candidate.absolutePath)
        } catch (unreadable: Throwable) {
            return Refusal.INTEGRITY_CHECK_FAILED
        }
        try {
            val problems = try {
                Integrity.fullProblems(connection)
            } catch (unreadable: Throwable) {
                return Refusal.INTEGRITY_CHECK_FAILED
            }
            if (problems.isNotEmpty()) return Refusal.INTEGRITY_CHECK_FAILED

            val version = Migrations.currentVersion(connection)
            if (version == 0) return Refusal.NOT_A_PROFILE_ARCHIVE
            if (version > Schema.VERSION) return Refusal.NEWER_SCHEMA

            if (migrate && version < Schema.VERSION) {
                connection.execSQL("PRAGMA foreign_keys = ON")
                try {
                    Migrations.migrate(connection)
                } catch (refused: Migrations.DataRecoveryRequired) {
                    return Refusal.MIGRATION_INCOMPLETE
                }
                if (Integrity.fullProblems(connection).isNotEmpty()) return Refusal.INTEGRITY_CHECK_FAILED
            }

            val tables = connection.prepare("SELECT name FROM sqlite_master WHERE type = 'table'").use { s ->
                buildSet { while (s.step()) add(s.getText(0)) }
            }
            val expected = Schema.curriculumTables + Schema.curriculumExtensionTables + Schema.truthTables + Schema.projectionTables
            if (!tables.containsAll(expected)) return Refusal.NOT_A_PROFILE_ARCHIVE
            return null
        } finally {
            runCatching { connection.close() }
        }
    }

    /**
     * One rename replaces the live file. A leftover journal beside it belongs to the **old**
     * database; SQLite would replay it into the new file on the next open, so it is moved aside
     * first and put back if the rename fails, leaving the old profile exactly recoverable.
     */
    private fun replaceAtomically(staging: File, live: File) {
        val setAside = sidecarSuffixes
            .map { File(live.parentFile, live.name + it) }
            .filter { it.exists() }
            .associateWith { File(it.parentFile, it.name + ".superseded") }
        setAside.forEach { (sidecar, aside) ->
            Files.move(sidecar.toPath(), aside.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        try {
            Files.move(
                staging.toPath(), live.toPath(),
                StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING,
            )
        } catch (failure: Throwable) {
            setAside.forEach { (sidecar, aside) ->
                runCatching { Files.move(aside.toPath(), sidecar.toPath(), StandardCopyOption.REPLACE_EXISTING) }
            }
            throw failure
        }
        setAside.values.forEach { it.delete() }
        sidecarSuffixes.forEach { File(staging.parentFile, staging.name + it).delete() }
    }

    private fun deleteWithSidecars(file: File) {
        file.delete()
        sidecarSuffixes.forEach { File(file.parentFile, file.name + it).delete() }
    }
}
