package coach.persistence

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import coach.model.RecoveryReason
import coach.model.StoreStatus
import coach.persistence.Fixtures.populated
import coach.persistence.Fixtures.tempDb
import java.io.File
import java.io.RandomAccessFile
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * `LFPS-v0` §12 and `APHX-v0`: integrity is checked on open, every failure is a status rather than
 * a crash, and **no failure path resets the learner's data**.
 *
 * The no-reset claim is proven the only way it can be: the file is compared byte for byte before
 * and after the refused open. A check that only asserted the status would pass an opener that
 * reported recovery and then quietly recreated an empty database.
 */
class StoreOpenerTest {

    private fun assertRecoveryLeavesFileUntouched(file: File, reason: RecoveryReason) {
        val before = file.readBytes()
        val result = StoreOpener.open(file.absolutePath)
        val notOpened = assertIs<StoreOpener.Result.NotOpened>(result, "an unsafe store was opened")
        assertEquals(StoreStatus.RecoveryRequired(reason), notOpened.status)
        assertTrue(file.exists(), "the database file was removed")
        assertContentEquals(before, file.readBytes(), "a refused open changed the database file")
        assertTrue(
            listOf("-journal", "-wal", "-shm").none { File(file.path + it).exists() },
            "a refused open left a sidecar behind",
        )
    }

    // ---------------------------------------------------------------- healthy paths

    @Test
    fun `a clean install creates a current store and reports it ready`() {
        val file = tempDb()
        val result = assertIs<StoreOpener.Result.Opened>(StoreOpener.open(file.absolutePath))
        result.store.use { db ->
            assertEquals(StoreStatus.Ready, result.status)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            db.appendTruth(Fixtures.evidence())
            assertEquals(1, db.count("evidence_event"))
        }
    }

    @Test
    fun `an older populated store is migrated checked and opened with its truth intact`() {
        val file = tempDb()
        populated(file, version = 1)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use(Fixtures::truthContent)
        val result = assertIs<StoreOpener.Result.Opened>(StoreOpener.open(file.absolutePath))
        result.store.use { db ->
            val after = Fixtures.truthContent(db)
            Schema.truthTables.forEach { assertEquals(before[it], after[it], "$it content changed on open") }
        }
    }

    @Test
    fun `a directory that cannot hold a database is a recoverable failure, not a recovery`() {
        val missing = File(tempDb().parentFile, "no-such-dir-${System.nanoTime()}/coach.db")
        val result = assertIs<StoreOpener.Result.NotOpened>(StoreOpener.open(missing.absolutePath))
        assertEquals(StoreStatus.RecoverableFailure, result.status)
        assertTrue(!missing.exists(), "a failed open must not create anything")
    }

    // ---------------------------------------------------------------- corruption is surfaced, never reset

    @Test
    fun `a file that is not a database surfaces recovery and is left untouched`() {
        val file = tempDb()
        file.writeBytes(ByteArray(8192) { (it * 31 + 7).toByte() })
        assertRecoveryLeavesFileUntouched(file, RecoveryReason.INTEGRITY_CHECK_FAILED)
    }

    @Test
    fun `a truncated database surfaces recovery and is left untouched`() {
        val file = tempDb()
        populated(file)
        RandomAccessFile(file, "rw").use { it.setLength(it.length() / 2) }
        assertRecoveryLeavesFileUntouched(file, RecoveryReason.INTEGRITY_CHECK_FAILED)
    }

    @Test
    fun `a corrupted evidence page behind a valid header surfaces recovery and is left untouched`() {
        val file = tempDb()
        populated(file)
        val (pageSize, rootPage) = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            db.query("PRAGMA page_size") { it.getLong(0) }.single() to
                db.query("SELECT rootpage FROM sqlite_master WHERE name = 'evidence_event'") { it.getLong(0) }.single()
        }
        // The file header is intact, so SQLite opens it happily. The evidence table's b-tree page
        // header is overwritten: only an integrity check notices.
        RandomAccessFile(file, "rw").use { raf ->
            raf.seek((rootPage - 1) * pageSize)
            raf.write(ByteArray(12) { 0xFF.toByte() })
        }
        assertRecoveryLeavesFileUntouched(file, RecoveryReason.INTEGRITY_CHECK_FAILED)
    }

    @Test
    fun `a foreign key violation written behind the engine's back surfaces recovery`() {
        val file = tempDb()
        SqlitePersistence.open(file.absolutePath).use { db ->
            db.execute("PRAGMA foreign_keys = OFF")
            db.appendTruth(
                coach.ports.TruthRecord("artifact", Fixtures.at, mapOf("attempt_id" to "999", "content_ref" to "artifact://orphan"))
            )
        }
        assertRecoveryLeavesFileUntouched(file, RecoveryReason.INTEGRITY_CHECK_FAILED)
    }

    @Test
    fun `damage only the full check can see is caught after a migration`() {
        val file = tempDb()
        populated(file, version = 1)
        // An index whose stored definition no longer matches its entries. quick_check does not
        // compare index content with table content, so the open-time check passes; the full
        // integrity_check that follows a migration is what catches it.
        BundledSQLiteDriver().open(file.absolutePath).also { connection ->
            connection.execSQL("CREATE INDEX probe ON exposure_record(resource_logical_id)")
            connection.execSQL("PRAGMA writable_schema = ON")
            connection.execSQL(
                "UPDATE sqlite_master SET sql = 'CREATE INDEX probe ON exposure_record(exposure_kind)' WHERE name = 'probe'"
            )
            connection.execSQL("PRAGMA writable_schema = OFF")
            connection.close()
        }
        val quick = BundledSQLiteDriver().open(file.absolutePath).let { connection ->
            Integrity.quickProblems(connection).also { connection.close() }
        }
        assertEquals(emptyList(), quick, "the fixture must pass quick_check, or it proves nothing about the full check")

        val result = assertIs<StoreOpener.Result.NotOpened>(StoreOpener.open(file.absolutePath))
        assertEquals(StoreStatus.RecoveryRequired(RecoveryReason.INTEGRITY_CHECK_FAILED), result.status)
        SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            assertEquals(25, db.count("evidence_event"), "evidence was lost while surfacing recovery")
        }
    }

    // ---------------------------------------------------------------- version problems

    @Test
    fun `a newer schema surfaces recovery and is left untouched`() {
        val file = tempDb()
        populated(file)
        BundledSQLiteDriver().open(file.absolutePath).also { connection ->
            connection.execSQL("UPDATE schema_metadata SET schema_version = ${Schema.VERSION + 1}")
            connection.close()
        }
        assertRecoveryLeavesFileUntouched(file, RecoveryReason.NEWER_SCHEMA)
    }

    @Test
    fun `a migration that cannot complete surfaces recovery and leaves the older store as it was`() {
        val file = tempDb()
        populated(file, version = 1)
        BundledSQLiteDriver().open(file.absolutePath).also { connection ->
            // Fails the v2 step after it has created indexes and cleared the metadata row.
            connection.execSQL(
                "CREATE TRIGGER sabotage_metadata_write BEFORE INSERT ON schema_metadata " +
                    "BEGIN SELECT RAISE(ABORT, 'injected failure after partial migration'); END"
            )
            connection.close()
        }
        assertRecoveryLeavesFileUntouched(file, RecoveryReason.MIGRATION_INCOMPLETE)
    }

    @Test
    fun `an opener never throws whatever it finds`() {
        val shapes = listOf<(File) -> Unit>(
            { it.writeBytes(ByteArray(0)) },
            { it.writeBytes("SQLite format 3 ".toByteArray() + ByteArray(84)) },
            { it.writeBytes(ByteArray(100_000) { 0x5A }) },
        )
        shapes.forEach { shape ->
            val file = tempDb()
            shape(file)
            val result = runCatching { StoreOpener.open(file.absolutePath) }
            assertTrue(result.isSuccess, "the opener threw: ${result.exceptionOrNull()}")
            (result.getOrNull() as? StoreOpener.Result.Opened)?.store?.close()
        }
    }
}
