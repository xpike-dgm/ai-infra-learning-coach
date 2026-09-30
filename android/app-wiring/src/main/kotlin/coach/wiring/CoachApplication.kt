package coach.wiring

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import coach.application.DayCloseFacts
import coach.application.IngestCurriculum
import coach.application.PlannerExplanationQuery
import coach.application.StoreOpenOutcome
import coach.application.StoreStartup
import coach.application.TodayFactsQuery
import coach.model.PlannerExplanationFacts
import coach.model.TodayFacts
import coach.curriculum.FileContentSource
import coach.persistence.StoreOpener
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/** The opened store, plus what it could say about today — its plan and its record — when it opened. */
class OpenedApp(val graph: AppGraph, val today: TodayFacts, val day: DayCloseFacts.Loaded)

/**
 * Process scope for the store (`APHX-v0`).
 *
 * The store belongs to the process, not to an activity: an activity is recreated on rotation or a
 * theme change, and reopening the database each time would repeat the integrity check and could
 * race a write. So opening starts here, once, on a dedicated background thread, and activities only
 * observe it.
 *
 * Today's facts are read on that same thread, in the same job, because reading them is disk work
 * too — the thing 10E moved off the main thread in the first place.
 */
class CoachApplication : Application() {

    lateinit var startup: StoreStartup<OpenedApp>
        private set

    private val main = Handler(Looper.getMainLooper())

    private val storeThread: Executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "coach-store").apply { isDaemon = true }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            // Debug builds log any disk read or write on the main thread, so a regression that
            // moves store work back onto it shows up in logcat on the device.
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().penaltyLog().build()
            )
        }

        startup = StoreStartup(
            open = ::openApp,
            background = storeThread,
            deliver = Executor { main.post(it) },
        )
        startup.start()
    }

    /**
     * Re-reads today's facts off the main thread. The activity asks on resume, because a process
     * that stayed open past midnight would otherwise keep yesterday's study day — and a stale study
     * day is exactly how a stale plan gets shown as today's (`SRR-v0`).
     */
    fun refreshToday(onLoaded: (TodayFacts, DayCloseFacts.Loaded) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val facts = TodayFactsQuery(opened.graph.persistence, opened.graph.clock).load()
            // The day's own record is read on the same thread and against the same clock, so a
            // process kept open past midnight reports the new day rather than yesterday's (11E).
            val day = DayCloseFacts(opened.graph.persistence, opened.graph.clock).load()
            main.post { onLoaded(facts, day) }
        }
    }

    /**
     * Reads what the planner explanation may say, off the main thread (12E). It is disk work like
     * Today's facts, and it reads the same stored plan the same way.
     */
    fun loadExplanation(onLoaded: (PlannerExplanationFacts) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val facts = PlannerExplanationQuery(opened.graph.persistence, opened.graph.clock).load()
            main.post { onLoaded(facts) }
        }
    }

    /** Runs on the store thread. Resolving the path touches the filesystem too, so it happens here. */
    private fun openApp(): StoreOpenOutcome<OpenedApp> {
        val path: File = getDatabasePath(AppGraph.DATABASE_NAME)
        path.parentFile?.mkdirs()
        return when (val result = StoreOpener.open(path.absolutePath)) {
            is StoreOpener.Result.Opened -> {
                val graph = AppGraph(persistence = result.store, content = FileContentSource(::authoredPackage))
                // Ingestion runs here because it is disk work and because Today must be read after
                // it: publishing is what turns "nothing is published" into a curriculum Today can
                // report. With no authored package shipping, it publishes nothing and says so by
                // returning null (11D).
                val published = IngestCurriculum(graph.persistence, graph.content, graph.clock).ingest()
                Log.i(TAG, "curriculum ingestion: ${published ?: "no authored package"}")
                StoreOpenOutcome.Opened(
                    OpenedApp(
                        graph,
                        TodayFactsQuery(graph.persistence, graph.clock).load(),
                        DayCloseFacts(graph.persistence, graph.clock).load(),
                    )
                )
            }
            is StoreOpener.Result.NotOpened -> {
                // Diagnostics only. The store holds no credential, so nothing secret can be logged.
                Log.w(TAG, "store not opened: ${result.status}", result.cause)
                StoreOpenOutcome.NotOpened(result.status)
            }
        }
    }

    /**
     * The authored curriculum package, when one is bundled. None ships today — authoring the first
     * content is 15's — so this returns `null` and the app runs exactly as it did, with Today
     * reporting that nothing has been published.
     */
    private fun authoredPackage(): String? = runCatching {
        assets.open(AUTHORED_PACKAGE).bufferedReader().use { it.readText() }
    }.getOrNull()

    private companion object {
        const val AUTHORED_PACKAGE = "curriculum_package.txt"
        const val TAG = "coach.store"
    }
}
