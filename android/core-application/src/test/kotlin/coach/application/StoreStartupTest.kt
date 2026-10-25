package coach.application

import coach.model.RecoveryReason
import coach.model.StoreStatus
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `APHX-v0`: the store is never opened on the caller's thread, the status is truthful while it
 * opens, and no outcome — including a defect in the opener — becomes a crash.
 *
 * On a device the caller is the main thread. Here it is the test thread, and the proof is the same:
 * the opener is held on a latch, so if [StoreStartup.start] waited for it the test would hang
 * rather than observe `Opening`.
 */
class StoreStartupTest {

    private val direct = Executor { it.run() }

    private fun background(): Executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "store-open").apply { isDaemon = true }
    }

    @Test
    fun `start returns at once and the open runs on another thread`() {
        val release = CountDownLatch(1)
        val opened = CountDownLatch(1)
        val openThread = CopyOnWriteArrayList<Thread>()
        val startup = StoreStartup(
            open = {
                openThread += Thread.currentThread()
                release.await(5, TimeUnit.SECONDS)
                StoreOpenOutcome.Opened("store")
            },
            background = background(),
            deliver = direct,
        )
        startup.observe { status, _ -> if (status == StoreStatus.Ready) opened.countDown() }

        startup.start()
        assertEquals(StoreStatus.Opening, startup.status, "the status must say opening while the open is in flight")
        assertNull(startup.store, "no store may be handed out before it is open")

        release.countDown()
        assertTrue(opened.await(5, TimeUnit.SECONDS), "the store never became ready")
        assertEquals("store", startup.store)
        assertNotEquals(Thread.currentThread(), openThread.single(), "the store was opened on the caller's thread")
    }

    @Test
    fun `starting twice opens once, so recreating the activity never reopens the database`() {
        val opens = AtomicInteger()
        val startup = StoreStartup(
            open = { opens.incrementAndGet(); StoreOpenOutcome.Opened("store") },
            background = direct,
            deliver = direct,
        )
        repeat(3) { startup.start() }
        assertEquals(1, opens.get())
        startup.recheck()
        assertEquals(1, opens.get(), "a ready store must not be rechecked")
    }

    @Test
    fun `a recovery outcome is reported as it was found`() {
        val seen = CopyOnWriteArrayList<StoreStatus>()
        val startup = StoreStartup<String>(
            open = { StoreOpenOutcome.NotOpened(StoreStatus.RecoveryRequired(RecoveryReason.NEWER_SCHEMA)) },
            background = direct,
            deliver = direct,
        )
        startup.observe { status, _ -> seen += status }
        startup.start()
        assertEquals(listOf(StoreStatus.Opening, StoreStatus.RecoveryRequired(RecoveryReason.NEWER_SCHEMA)), seen)
        assertNull(startup.store)
    }

    @Test
    fun `an opener that throws becomes a recoverable failure instead of a crash`() {
        val startup = StoreStartup<String>(
            open = { error("defect in the opener") },
            background = direct,
            deliver = direct,
        )
        startup.start()
        assertEquals(StoreStatus.RecoverableFailure, startup.status)
    }

    @Test
    fun `an opener cannot claim ready without handing over a store`() {
        listOf(StoreStatus.Ready, StoreStatus.Opening).forEach { claimed ->
            val startup = StoreStartup<String>(
                open = { StoreOpenOutcome.NotOpened(claimed) },
                background = direct,
                deliver = direct,
            )
            startup.start()
            assertEquals(StoreStatus.RecoverableFailure, startup.status, "NotOpened($claimed) was believed")
        }
    }

    @Test
    fun `recheck after a failure reruns the open and can reach ready`() {
        var attempt = 0
        val seen = CopyOnWriteArrayList<StoreStatus>()
        val startup = StoreStartup(
            open = {
                attempt += 1
                if (attempt == 1) StoreOpenOutcome.NotOpened(StoreStatus.RecoverableFailure)
                else StoreOpenOutcome.Opened("store")
            },
            background = direct,
            deliver = direct,
        )
        startup.observe { status, _ -> seen += status }
        startup.start()
        startup.recheck()
        assertEquals(
            listOf(StoreStatus.Opening, StoreStatus.RecoverableFailure, StoreStatus.Opening, StoreStatus.Ready),
            seen,
        )
        assertEquals("store", startup.store)
    }

    @Test
    fun `results reach listeners only through the delivery executor`() {
        val delivered = CopyOnWriteArrayList<Runnable>()
        val deliver = Executor { delivered += it }
        val seen = CopyOnWriteArrayList<StoreStatus>()
        val startup = StoreStartup(
            open = { StoreOpenOutcome.Opened("store") },
            background = direct,
            deliver = deliver,
        )
        startup.observe { status, _ -> seen += status }
        startup.start()
        assertTrue(seen.isEmpty(), "a listener was called outside the delivery executor")
        delivered.forEach { it.run() }
        assertEquals(StoreStatus.Ready, seen.last())
    }

    @Test
    fun `replacing the profile closes the open store, runs the work off the caller's thread, and reopens`() {
        var opens = 0
        val seen = CopyOnWriteArrayList<StoreStatus>()
        val workThread = CopyOnWriteArrayList<Thread>()
        val startup = StoreStartup(
            open = { opens += 1; StoreOpenOutcome.Opened("store-$opens") },
            background = direct,
            deliver = direct,
        )
        startup.observe { status, _ -> seen += status }
        startup.start()
        assertEquals("store-1", startup.store)
        var result: Result<String>? = null
        startup.replace(work = { open -> workThread += Thread.currentThread(); "replaced $open" }, onDone = { result = it })
        // While the file is being replaced nothing can read the old store; afterwards a freshly opened one is handed out.
        assertTrue(StoreStatus.Opening in seen.drop(seen.indexOf(StoreStatus.Ready) + 1), seen.toString())
        assertEquals("replaced store-1", result!!.getOrThrow())
        assertEquals("store-2", startup.store)
        assertEquals(StoreStatus.Ready, startup.status)
    }

    @Test
    fun `replacing is ignored unless the store is ready, and a failing work still reopens`() {
        val startup = StoreStartup<String>(
            open = { StoreOpenOutcome.NotOpened(StoreStatus.RecoverableFailure) },
            background = direct,
            deliver = direct,
        )
        startup.start()
        var ran = false
        startup.replace(work = { ran = true }, onDone = {})
        assertTrue(!ran, "there was no store to replace")

        var opens = 0
        val ready = StoreStartup(open = { opens += 1; StoreOpenOutcome.Opened("s$opens") }, background = direct, deliver = direct)
        ready.start()
        var outcome: Result<Unit>? = null
        ready.replace(work = { error("restore failed") }, onDone = { outcome = it })
        assertTrue(outcome!!.isFailure)
        assertEquals(StoreStatus.Ready, ready.status, "a failed replacement still reopens the store rather than leaving it closed")
        assertEquals("s2", ready.store)
    }
}
