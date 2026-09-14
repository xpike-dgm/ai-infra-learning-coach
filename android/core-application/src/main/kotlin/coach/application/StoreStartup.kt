package coach.application

import coach.model.StoreStatus
import java.util.concurrent.Executor

/**
 * What opening the store produced: a usable store, or the status that explains why there is none.
 * [T] is whatever the composition root builds from an open store; core never sees its type.
 */
sealed interface StoreOpenOutcome<out T : Any> {
    data class Opened<T : Any>(val value: T) : StoreOpenOutcome<T>
    data class NotOpened(val status: StoreStatus) : StoreOpenOutcome<Nothing>
}

/**
 * Brings the local store up without ever doing it on the caller's thread (`APHX-v0`).
 *
 * Opening means reading the file, checking its integrity and possibly migrating it — disk work
 * whose duration grows with years of history. On Android the caller is the main thread, where
 * that work freezes the first frame and, past a few seconds, gets the app killed. So the open is
 * handed to [background], results come back through [deliver], and [status] is truthful at every
 * moment in between: [StoreStatus.Opening] until an outcome exists.
 *
 * The orchestration lives in core so this is a JVM test rather than something noticed on a phone.
 *
 * - [start] is idempotent. A second call while opening, or after the store is ready, opens
 *   nothing — so recreating the activity (rotation, dark mode) never opens the database twice.
 * - [recheck] exists only where retrying is meaningful and harmless: after a failure. It re-runs
 *   the same non-mutating open; there is no reset path to reach from here.
 * - An opener that throws is a defect, but it still never crashes the app: it becomes
 *   [StoreStatus.RecoverableFailure].
 */
class StoreStartup<T : Any>(
    private val open: () -> StoreOpenOutcome<T>,
    private val background: Executor,
    private val deliver: Executor,
) {
    fun interface Listener<T : Any> {
        fun onChanged(status: StoreStatus, store: T?)
    }

    private val lock = Any()
    private val listeners = mutableListOf<Listener<T>>()
    private var started = false

    @Volatile
    var status: StoreStatus = StoreStatus.Opening
        private set

    @Volatile
    var store: T? = null
        private set

    /** Registers [listener] and immediately tells it the current status. */
    fun observe(listener: Listener<T>) {
        synchronized(lock) { listeners += listener }
        deliver.execute { listener.onChanged(status, store) }
    }

    fun stopObserving(listener: Listener<T>) {
        synchronized(lock) { listeners -= listener }
    }

    fun start() {
        synchronized(lock) {
            if (started) return
            started = true
        }
        launch()
    }

    /** Re-runs the open after a failure. Ignored while opening or once ready. */
    fun recheck() {
        synchronized(lock) {
            val current = status
            if (current is StoreStatus.Opening || current is StoreStatus.Ready) return
            status = StoreStatus.Opening
        }
        publish()
        launch()
    }

    private fun launch() {
        background.execute {
            val outcome = try {
                open()
            } catch (defect: Throwable) {
                StoreOpenOutcome.NotOpened(StoreStatus.RecoverableFailure)
            }
            synchronized(lock) {
                when (outcome) {
                    is StoreOpenOutcome.Opened -> {
                        store = outcome.value
                        status = StoreStatus.Ready
                    }
                    is StoreOpenOutcome.NotOpened -> {
                        store = null
                        // Ready and Opening are outcomes this type produces itself; an opener
                        // reporting either without a store would be claiming something untrue.
                        status = when (outcome.status) {
                            StoreStatus.Ready, StoreStatus.Opening -> StoreStatus.RecoverableFailure
                            else -> outcome.status
                        }
                    }
                }
            }
            publish()
        }
    }

    private fun publish() {
        val snapshot = synchronized(lock) { listeners.toList() }
        val currentStatus = status
        val currentStore = store
        deliver.execute { snapshot.forEach { it.onChanged(currentStatus, currentStore) } }
    }
}
