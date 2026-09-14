package coach.persistence

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import coach.persistence.Fixtures.populated
import coach.persistence.Fixtures.tempDb
import java.io.File
import java.io.RandomAccessFile
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * `LFPS-v0` §11, `TVSX-v0` §8.4 and `V1_SUCCESS_CRITERIA` SC-037, against real files.
 *
 * Every refusal is checked by comparing the live profile **and the archive** byte for byte with
 * what they were before, because "refused" is only true if nothing moved.
 */
class BackupTest {

    private fun dir(): File = tempDb().parentFile.resolve("coach-backup-${System.nanoTime()}").also {
        it.mkdirs(); it.deleteOnExit()
    }

    private fun content(file: File): Map<String, List<String>> =
        SqlitePersistence.openWithoutMigrating(file.absolutePath).use(Fixtures::truthContent)

    private fun exportOf(source: File, into: File): File {
        val archive = into.resolve("archive.coachdb")
        SqlitePersistence.open(source.absolutePath).use { db ->
            assertEquals(Backup.ExportResult.Exported, Backup.export(db, archive))
        }
        return archive
    }

    /** A live profile with different truth, so a merge or a partial restore would be visible. */
    private fun otherLiveProfile(into: File): File =
        into.resolve("coach.db").also { populated(it, skillPrefix = "skill.live.other") }

    private fun assertRefusedAndNothingMoved(archive: File, live: File, refusal: Backup.Refusal) {
        val liveBefore = live.readBytes()
        val archiveBefore = archive.readBytes()
        assertEquals(Backup.RestoreResult.Refused(refusal), Backup.restore(archive, live))
        assertContentEquals(liveBefore, live.readBytes(), "a refused restore changed the live profile")
        assertContentEquals(archiveBefore, archive.readBytes(), "a restore wrote to the archive")
        assertFalse(File(live.parentFile, live.name + ".restore-staging").exists(), "staging copy left behind")
    }

    // ---------------------------------------------------------------- export

    @Test
    fun `an export is a verified archive carrying all truth and the schema and policy version`() {
        val work = dir()
        val source = work.resolve("source.db").also { populated(it) }
        val archive = exportOf(source, work)
        assertEquals(content(source), content(archive), "the export lost or altered truth")
        val metadata = SqlitePersistence.openWithoutMigrating(archive.absolutePath).use { db ->
            db.query("SELECT schema_version, policy_version FROM schema_metadata") { it.getLong(0) to it.getText(1) }.single()
        }
        assertEquals(Schema.VERSION.toLong() to Migrations.INITIAL_POLICY_VERSION, metadata)
    }

    @Test
    fun `an export never overwrites an existing file`() {
        val work = dir()
        val existing = work.resolve("taken.coachdb").also { it.writeText("someone else's file") }
        SqlitePersistence.open(work.resolve("source.db").absolutePath).use { db ->
            assertFailsWith<IllegalArgumentException> { Backup.export(db, existing) }
        }
        assertEquals("someone else's file", existing.readText())
    }

    @Test
    fun `no column in an archive can hold a credential`() {
        val work = dir()
        val archive = exportOf(work.resolve("source.db").also { populated(it) }, work)
        val credentialLike = Regex("key|secret|token|password|credential", RegexOption.IGNORE_CASE)
        val hits = SqlitePersistence.openWithoutMigrating(archive.absolutePath).use { db ->
            val tables = db.query("SELECT name FROM sqlite_master WHERE type = 'table'") { it.getText(0) }
            tables.flatMap { table ->
                db.query("PRAGMA table_info($table)") { it.getText(1) }
                    .filter { credentialLike.containsMatchIn(it) && !it.endsWith("summary_key") }
                    .map { "$table.$it" }
            }
        }
        assertEquals(emptyList(), hits, "an archive column could carry a credential (AIAX-v0)")
    }

    // ---------------------------------------------------------------- restore

    @Test
    fun `backup then restore onto a clean install preserves truth row for row`() {
        val work = dir()
        val source = work.resolve("source.db").also { populated(it) }
        val archive = exportOf(source, work)
        val cleanInstall = work.resolve("fresh").also { it.mkdirs() }.resolve("coach.db")

        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, cleanInstall))
        assertEquals(content(source), content(cleanInstall), "SC-037: restored truth differs from the backup")
    }

    @Test
    fun `restore replaces the live profile entirely and never merges`() {
        val work = dir()
        val archive = exportOf(work.resolve("source.db").also { populated(it) }, work)
        val live = otherLiveProfile(work)

        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, live))
        assertEquals(content(archive), content(live), "the live profile is not exactly the archive")
        val leftovers = SqlitePersistence.openWithoutMigrating(live.absolutePath).use { db ->
            db.query("SELECT COUNT(*) FROM evidence_event WHERE skill_logical_id LIKE 'skill.live.other%'") { it.getLong(0) }.single()
        }
        assertEquals(0L, leftovers, "restore merged the previous profile's evidence")
    }

    @Test
    fun `an older archive is migrated forward on a copy and restored with its truth intact`() {
        val work = dir()
        val archive = work.resolve("v1.coachdb").also { populated(it, version = 1) }
        val archiveBefore = archive.readBytes()
        val truthBefore = content(archive)
        val live = otherLiveProfile(work)

        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, live))
        SqlitePersistence.openWithoutMigrating(live.absolutePath).use { db ->
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.truthTables.forEach { assertEquals(truthBefore[it], after[it], "$it changed during restore migration") }
        }
        assertContentEquals(archiveBefore, archive.readBytes(), "the archive itself was migrated in place")
    }

    @Test
    fun `a corrupted archive is refused and the live profile is untouched`() {
        val work = dir()
        val archive = exportOf(work.resolve("source.db").also { populated(it) }, work)
        RandomAccessFile(archive, "rw").use { it.setLength(it.length() / 2) }
        assertRefusedAndNothingMoved(archive, otherLiveProfile(work), Backup.Refusal.INTEGRITY_CHECK_FAILED)
    }

    @Test
    fun `a file that is not a database is refused and the live profile is untouched`() {
        val work = dir()
        val archive = work.resolve("photo.jpg").also { it.writeBytes(ByteArray(4096) { (it * 13).toByte() }) }
        assertRefusedAndNothingMoved(archive, otherLiveProfile(work), Backup.Refusal.INTEGRITY_CHECK_FAILED)
    }

    @Test
    fun `an archive from a newer schema is refused rather than restored best effort`() {
        val work = dir()
        val archive = exportOf(work.resolve("source.db").also { populated(it) }, work)
        BundledSQLiteDriver().open(archive.absolutePath).also {
            it.execSQL("UPDATE schema_metadata SET schema_version = ${Schema.VERSION + 1}")
            it.close()
        }
        assertRefusedAndNothingMoved(archive, otherLiveProfile(work), Backup.Refusal.NEWER_SCHEMA)
    }

    @Test
    fun `a database that is not a profile is refused`() {
        val work = dir()
        val archive = work.resolve("notes.db")
        BundledSQLiteDriver().open(archive.absolutePath).also {
            it.execSQL("CREATE TABLE notes (body TEXT)")
            it.execSQL("INSERT INTO notes VALUES ('not a learning profile')")
            it.close()
        }
        assertRefusedAndNothingMoved(archive, otherLiveProfile(work), Backup.Refusal.NOT_A_PROFILE_ARCHIVE)
    }

    @Test
    fun `an older archive whose migration cannot complete is refused and the live profile is untouched`() {
        val work = dir()
        val archive = work.resolve("v1-broken.coachdb").also { populated(it, version = 1) }
        BundledSQLiteDriver().open(archive.absolutePath).also {
            it.execSQL(
                "CREATE TRIGGER sabotage_metadata_write BEFORE INSERT ON schema_metadata " +
                    "BEGIN SELECT RAISE(ABORT, 'injected failure'); END"
            )
            it.close()
        }
        assertRefusedAndNothingMoved(archive, otherLiveProfile(work), Backup.Refusal.MIGRATION_INCOMPLETE)
    }

    @Test
    fun `an archive missing part of the profile schema is refused`() {
        val work = dir()
        val archive = exportOf(work.resolve("source.db").also { populated(it) }, work)
        // Current schema version, intact metadata, sound integrity — and no exposure table. Restoring
        // it would silently lose every exposure record, which LFPS-v0 classifies as data loss.
        BundledSQLiteDriver().open(archive.absolutePath).also {
            it.execSQL("PRAGMA foreign_keys = OFF")
            it.execSQL("DROP TABLE exposure_record")
            it.close()
        }
        assertRefusedAndNothingMoved(archive, otherLiveProfile(work), Backup.Refusal.NOT_A_PROFILE_ARCHIVE)
    }

    /**
     * Captures the files of [db] as a crash would leave them: a write transaction has spilled pages
     * into the database file and its journal is hot. Both files are copied mid-transaction to
     * [into], then the original transaction is rolled back.
     */
    private fun crashImage(db: File, into: File): File {
        into.mkdirs()
        val image = into.resolve(db.name)
        BundledSQLiteDriver().open(db.absolutePath).also { connection ->
            connection.execSQL("PRAGMA cache_size = 2")
            connection.execSQL("BEGIN")
            connection.execSQL("CREATE TABLE crash_probe (b BLOB)")
            repeat(300) { connection.execSQL("INSERT INTO crash_probe VALUES (randomblob(2000))") }
            val journal = File(db.path + "-journal")
            check(journal.isFile && journal.length() > 0) { "no journal was written; the image would not be a crash image" }
            java.nio.file.Files.copy(db.toPath(), image.toPath())
            java.nio.file.Files.copy(journal.toPath(), File(image.path + "-journal").toPath())
            connection.execSQL("ROLLBACK")
            connection.close()
        }
        return image
    }

    @Test
    fun `a hot journal left by the old live file is not replayed into the restored profile`() {
        val work = dir()
        val archive = exportOf(work.resolve("source.db").also { populated(it) }, work)
        val live = otherLiveProfile(work)

        // Precondition: the journal really is hot — opening the crash image replays it.
        val probe = crashImage(live, work.resolve("probe"))
        SqlitePersistence.openWithoutMigrating(probe.absolutePath).use { db ->
            val probeTable = db.query("SELECT COUNT(*) FROM sqlite_master WHERE name = 'crash_probe'") { it.getLong(0) }.single()
            assertEquals(0L, probeTable, "the crash image's journal was not hot, so this test would prove nothing")
        }

        val crashed = crashImage(live, work.resolve("crashed"))
        assertTrue(File(crashed.path + "-journal").isFile)
        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, crashed))
        assertFalse(File(crashed.path + "-journal").exists(), "the old database's journal is still beside the restored file")
        assertFalse(File(crashed.path + "-journal.superseded").exists(), "the set-aside journal was not cleaned up")
        assertEquals(content(archive), content(crashed), "the old journal was replayed into the restored profile")
    }
}
