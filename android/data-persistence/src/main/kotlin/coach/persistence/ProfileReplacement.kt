package coach.persistence

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import coach.model.StoreStatus
import java.io.File

/**
 * Replacing the learner's profile with a backup, as the Profile control does it (16D, `STGX-v0`; `LFPS-v0` §11.2).
 *
 * 10E built the mechanism ([Backup.restore]: verified, atomic, never a merge). 16D adds what the user decided the
 * control must do around it: **the profile being replaced is kept, never deleted** (user decision, 2026-10-25). Before
 * anything is replaced, a verified copy of the live profile is written beside it; only if that copy is made does the
 * restore run. A refused archive leaves the live profile exactly as it was, and its unneeded copy is removed.
 */
object ProfileReplacement {

    sealed interface Outcome {
        /** The backup is now the profile; the previous one is kept at [keptAt]. */
        data class Restored(val keptAt: File) : Outcome

        /** The backup was refused; nothing was replaced and nothing was kept, because nothing was lost. */
        data class Refused(val refusal: Backup.Refusal) : Outcome

        /** The live profile could not be copied aside, so nothing was replaced (`silent_progress_reset: false`). */
        data class NotKeptNotReplaced(val refusal: Backup.Refusal?) : Outcome

        /** Replaced, but the reopened store is not ready; the previous profile is kept at [keptAt]. */
        data class ReplacedButNotReady(val status: StoreStatus, val keptAt: File) : Outcome
    }

    /**
     * Keeps [liveStore]'s profile in [keepDirectory] as `replaced-<stamp>.db`, closes [liveStore] and restores
     * [archive] over [liveFile]. [liveStore] is closed whatever happens after the copy: the caller reopens the store.
     */
    fun replace(
        liveStore: SqlitePersistence,
        liveFile: File,
        archive: File,
        keepDirectory: File,
        stamp: String,
        driver: SQLiteDriver = BundledSQLiteDriver(),
    ): Outcome {
        require(stamp.isNotBlank() && stamp.all { it.isLetterOrDigit() || it == '-' }) { "a stamp is letters, digits and dashes: $stamp" }
        keepDirectory.mkdirs()
        val kept = File(keepDirectory, "replaced-$stamp.db")
        require(!kept.exists()) { "refusing to overwrite a kept profile: $kept" }
        val copy = runCatching { Backup.export(liveStore, kept, driver) }.getOrNull()
        if (copy !is Backup.ExportResult.Exported) {
            kept.delete()
            return Outcome.NotKeptNotReplaced((copy as? Backup.ExportResult.Failed)?.refusal)
        }
        liveStore.close()
        return when (val restored = Backup.restore(archive, liveFile, driver)) {
            Backup.RestoreResult.Restored -> Outcome.Restored(kept)
            is Backup.RestoreResult.Refused -> {
                kept.delete()
                Outcome.Refused(restored.refusal)
            }
            is Backup.RestoreResult.ReplacedButNotReady -> Outcome.ReplacedButNotReady(restored.status, kept)
        }
    }
}
