package coach.wiring

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import coach.application.StoreOpenOutcome
import coach.application.StoreStartup
import coach.persistence.StoreOpener
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Process scope for the store (`APHX-v0`).
 *
 * The store belongs to the process, not to an activity: an activity is recreated on rotation or a
 * theme change, and reopening the database each time would repeat the integrity check and could
 * race a write. So opening starts here, once, on a dedicated background thread, and activities only
 * observe it.
 */
class CoachApplication : Application() {

    lateinit var startup: StoreStartup<AppGraph>
        private set

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            // Debug builds log any disk read or write on the main thread, so a regression that
            // moves store work back onto it shows up in logcat on the device.
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().penaltyLog().build()
            )
        }

        val main = Handler(Looper.getMainLooper())
        startup = StoreStartup(
            open = ::openGraph,
            background = Executors.newSingleThreadExecutor { runnable ->
                Thread(runnable, "coach-store").apply { isDaemon = true }
            },
            deliver = Executor { main.post(it) },
        )
        startup.start()
    }

    /** Runs on the store thread. Resolving the path touches the filesystem too, so it happens here. */
    private fun openGraph(): StoreOpenOutcome<AppGraph> {
        val path: File = getDatabasePath(AppGraph.DATABASE_NAME)
        path.parentFile?.mkdirs()
        return when (val result = StoreOpener.open(path.absolutePath)) {
            is StoreOpener.Result.Opened -> StoreOpenOutcome.Opened(AppGraph(persistence = result.store))
            is StoreOpener.Result.NotOpened -> {
                // Diagnostics only. The store holds no credential, so nothing secret can be logged.
                Log.w(TAG, "store not opened: ${result.status}", result.cause)
                StoreOpenOutcome.NotOpened(result.status)
            }
        }
    }

    private companion object {
        const val TAG = "coach.store"
    }
}
