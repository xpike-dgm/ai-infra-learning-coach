package coach.wiring

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.net.Uri
import coach.application.CapacitySettings
import coach.application.ReminderSettings
import coach.model.ReminderPreference
import coach.model.StoreStatus
import coach.presentation.NotificationAccess
import coach.application.DayCloseFacts
import coach.application.PlanToday
import coach.model.CapacityPreferences
import coach.model.CapacityProfile
import coach.model.EvaluatorAvailability
import coach.model.TodayCapacityChoice
import coach.persistence.Backup
import coach.persistence.ProfileReplacement
import coach.persistence.SqlitePersistence
import coach.presentation.BackupRefusal
import coach.presentation.BackupState
import coach.application.EnglishProfileQuery
import coach.application.IngestCurriculum
import coach.application.PlannerExplanationQuery
import coach.application.LearningHistoryQuery
import coach.application.ProgressQuery
import coach.application.StoreOpenOutcome
import coach.application.StoreStartup
import coach.application.TodayFactsQuery
import coach.application.TopicQuery
import coach.model.EnglishProfile
import coach.model.TopicDetailFacts
import coach.model.TopicFacts
import coach.model.PlannerExplanationFacts
import coach.model.HistoryFacts
import coach.model.ProgressFacts
import coach.model.SkillDetailFacts
import coach.model.VersionedRef
import coach.model.TodayFacts
import coach.curriculum.FileContentSource
import coach.persistence.StoreOpener
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * The opened store. Today's plan and record are not read here: opening the store is what every start of the process
 * does — a reminder's alarm and a reboot signal included — and only the learner opening the app plans today (16E).
 */
class OpenedApp(val graph: AppGraph)

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

    // AI work (saving the key, the connection check) runs on its own thread: a network call must never hold up the store.
    private val aiThread: Executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "coach-ai").apply { isDaemon = true }
    }

    /** The AI settings behind Profile (14G); in the no-AI build there is nothing to configure. */
    internal val aiSettings: AiSettings get() = provideAiSettings()

    /** Runs [work] off the main thread and hands its result back on the main thread. */
    internal fun <T> aiWork(work: () -> T, onDone: (T) -> Unit) {
        aiThread.execute {
            val result = work()
            main.post { onDone(result) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Only resolves where the key would live; nothing is read here (14G).
        initAi(this)
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
            // 16D: a new study day gets its plan before Today is read — re-entry, never yesterday's plan replayed.
            planToday(opened.graph)
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

    /**
     * Reads what Progress may say, off the main thread (16A). It asks the prerequisite gate and reads each Skill's
     * evidence, which is disk work like Today's facts.
     */
    fun loadProgress(onLoaded: (ProgressFacts, TopicFacts) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val facts = ProgressQuery(opened.graph.persistence, opened.graph.content).load()
            // 16C: the Topics read the very same Skill rows, so a Topic can never contradict the Skills shown beside it.
            val topics = TopicQuery(opened.graph.persistence, opened.graph.content).load(facts)
            main.post { onLoaded(facts, topics) }
        }
    }

    /** One Topic's `topic_detail` facts, off the main thread (16C); `null` for a Topic nothing places. */
    fun loadTopicDetail(topic: VersionedRef, onLoaded: (TopicDetailFacts?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val facts = TopicQuery(opened.graph.persistence, opened.graph.content).detail(topic)
            main.post { onLoaded(facts) }
        }
    }

    /**
     * The Technical English profile, off the main thread (16C). Its historical band replays the mastery engine over the
     * English Skills' evidence, which is disk and CPU work. `null` when no curriculum is published.
     */
    fun loadEnglishProfile(onLoaded: (EnglishProfile?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val profile = EnglishProfileQuery(opened.graph.persistence, opened.graph.content).load()
            main.post { onLoaded(profile) }
        }
    }

    /** One Skill's `skill_detail` facts, off the main thread (16A); `null` for a Skill that is not published. */
    fun loadSkillDetail(skill: VersionedRef, onLoaded: (SkillDetailFacts?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val facts = ProgressQuery(opened.graph.persistence, opened.graph.content).detail(skill)
            main.post { onLoaded(facts) }
        }
    }

    /**
     * The learning history and the assessment report, off the main thread (16B). Replaying the engines over the stored
     * evidence is disk and CPU work; [skill] narrows it to one Skill for `skill_detail`'s history.
     */
    fun loadHistory(skill: VersionedRef?, onLoaded: (HistoryFacts) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val facts = LearningHistoryQuery(opened.graph.persistence).load(skill)
            main.post { onLoaded(facts) }
        }
    }

    /**
     * Today's plan, from the learner's capacity preferences (16D, `STGX-v0`). The planner decides; this only asks it,
     * once a day — a day that already has its plan writes nothing.
     */
    private fun planToday(graph: AppGraph) {
        PlanToday(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).ensure()
    }

    private fun evaluatorAvailable(): Boolean = evaluatorAvailability() == EvaluatorAvailability.AVAILABLE

    /** For the reminder's preview, which must route the same items the planner would (16E). */
    internal fun evaluatorAvailableNow(): Boolean = evaluatorAvailable()

    // ---------------------------------------------------------------- the daily reminder (16E)

    /**
     * Runs [work] on the store thread once the store is ready, or [onTimeout] if it is not ready within [budgetMillis].
     * A receiver uses this: its process may have been started by the alarm itself, with the store still opening.
     */
    internal fun whenStoreReady(budgetMillis: Long, onTimeout: () -> Unit, work: (OpenedApp) -> Unit) {
        val settled = java.util.concurrent.atomic.AtomicBoolean(false)
        lateinit var listener: StoreStartup.Listener<OpenedApp>
        val timeout = Runnable {
            if (settled.compareAndSet(false, true)) {
                startup.stopObserving(listener)
                onTimeout()
            }
        }
        listener = StoreStartup.Listener { status, store ->
            val ready = store != null && status == StoreStatus.Ready
            // A store that failed to open will not open on its own; that is silence too, not a wait to the end.
            val failed = store == null && status != StoreStatus.Opening && status != StoreStatus.Ready
            if ((ready || failed) && settled.compareAndSet(false, true)) {
                main.removeCallbacks(timeout)
                startup.stopObserving(listener)
                if (ready) storeThread.execute { work(store!!) } else onTimeout()
            }
        }
        main.postDelayed(timeout, budgetMillis)
        startup.observe(listener)
    }

    /** The reminder setting and what Android allows, off the main thread (16E). */
    fun loadReminder(onLoaded: (ReminderPreference, NotificationAccess) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val preference = ReminderSettings(opened.graph.persistence, opened.graph.clock).read()
            main.post { onLoaded(preference, Reminders.access(this)) }
        }
    }

    /** Records the learner's reminder setting and arms or disarms the alarm from it (16E). */
    fun setReminder(enabled: Boolean, minuteOfDay: Int, onDone: (ReminderPreference, NotificationAccess) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val settings = ReminderSettings(opened.graph.persistence, opened.graph.clock)
            settings.set(enabled, minuteOfDay)
            val preference = settings.read()
            if (preference.enabled) Reminders.ensureChannel(this)
            Reminders.applySetting(this, preference, opened.graph.clock.now().instantEpochMillis)
            main.post { onDone(preference, Reminders.access(this)) }
        }
    }

    /** The capacity preferences as they stand today, off the main thread (16D). */
    fun loadCapacity(onLoaded: (CapacityPreferences) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val preferences = CapacitySettings(opened.graph.persistence, opened.graph.clock).read()
            main.post { onLoaded(preferences) }
        }
    }

    /** One profile's standing length; today's plan is replanned only if today's budget changed (16D). */
    fun setProfileLength(profile: CapacityProfile, minutes: Int, onDone: (CapacityPreferences, TodayFacts) -> Unit) =
        changeCapacity(onDone) { it.setProfile(profile, minutes) }

    /** Today's own time (`THUX-v0` `change_today_capacity`); it replans today and applies to today only (16D). */
    fun setTodayCapacity(choice: TodayCapacityChoice, onDone: (CapacityPreferences, TodayFacts) -> Unit) =
        changeCapacity(onDone) { it.setToday(choice) }

    private fun changeCapacity(onDone: (CapacityPreferences, TodayFacts) -> Unit, change: (CapacitySettings) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val settings = CapacitySettings(opened.graph.persistence, opened.graph.clock)
            change(settings)
            PlanToday(opened.graph.persistence, opened.graph.content, opened.graph.clock, evaluatorAvailable()).afterCapacityChange()
            val facts = TodayFactsQuery(opened.graph.persistence, opened.graph.clock).load()
            val preferences = settings.read()
            main.post { onDone(preferences, facts) }
        }
    }

    // ---------------------------------------------------------------- backup, export, restore (16D)

    /** The backup the learner chose to restore, copied into the app's cache; only the store thread touches it. */
    private var restoreCandidate: File? = null

    /**
     * Writes a verified backup to [destination], a file the learner chose (`LFPS-v0` §11.1: user-initiated, nothing
     * uploaded). The archive is made in the app's cache, checked as a restore would check it, then copied out.
     */
    fun exportBackup(destination: Uri, onDone: (BackupState) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val staging = File(cacheDir, "export-${System.nanoTime()}.coachdb")
            val state = try {
                when (val exported = Backup.export(opened.graph.persistence as SqlitePersistence, staging)) {
                    Backup.ExportResult.Exported -> {
                        val out = contentResolver.openOutputStream(destination) ?: error("no stream for the chosen file")
                        out.use { stream -> staging.inputStream().use { it.copyTo(stream) } }
                        BackupState.Exported
                    }
                    is Backup.ExportResult.Failed -> BackupState.ExportFailed(refusalOf(exported.refusal))
                }
            } catch (failure: Exception) {
                Log.w(TAG, "export failed", failure)
                BackupState.ExportFailed(null)
            } finally {
                staging.delete()
            }
            main.post { onDone(state) }
        }
    }

    /** Copies the chosen backup into the cache and asks for confirmation; nothing is replaced yet. */
    fun prepareRestore(source: Uri, onDone: (BackupState) -> Unit) {
        storeThread.execute {
            val candidate = File(cacheDir, "restore-candidate.coachdb")
            val state = try {
                candidate.delete()
                val input = contentResolver.openInputStream(source) ?: error("no stream for the chosen file")
                input.use { stream -> candidate.outputStream().use { stream.copyTo(it) } }
                restoreCandidate = candidate
                BackupState.ConfirmRestore
            } catch (failure: Exception) {
                Log.w(TAG, "the chosen backup could not be read", failure)
                candidate.delete()
                restoreCandidate = null
                BackupState.RestoreRefused(BackupRefusal.INTEGRITY_CHECK_FAILED)
            }
            main.post { onDone(state) }
        }
    }

    fun cancelRestore() {
        storeThread.execute {
            restoreCandidate?.delete()
            restoreCandidate = null
        }
    }

    /**
     * Replaces the profile with the prepared backup, after the learner confirmed it (`LFPS-v0` §11.2). The store is
     * closed, the live profile is kept aside, the backup is verified and swapped in, and the store is opened again —
     * the reopened store reports its own status, and a refusal changes nothing.
     */
    fun confirmRestore(onDone: (BackupState) -> Unit) {
        storeThread.execute {
            val candidate = restoreCandidate
            if (candidate == null) {
                main.post { onDone(BackupState.Idle) }
                return@execute
            }
            restoreCandidate = null
            startup.replace(
                work = { opened ->
                    ProfileReplacement.replace(
                        liveStore = opened.graph.persistence as SqlitePersistence,
                        liveFile = getDatabasePath(AppGraph.DATABASE_NAME),
                        archive = candidate,
                        keepDirectory = File(filesDir, "replaced-profiles"),
                        stamp = opened.graph.clock.now().instantEpochMillis.toString(),
                    ).also { candidate.delete() }
                },
                onDone = { result ->
                    val state = when (val outcome = result.getOrNull()) {
                        is ProfileReplacement.Outcome.Restored -> BackupState.Restored
                        is ProfileReplacement.Outcome.Refused -> BackupState.RestoreRefused(refusalOf(outcome.refusal))
                        is ProfileReplacement.Outcome.NotKeptNotReplaced -> BackupState.NotKeptNotReplaced
                        is ProfileReplacement.Outcome.ReplacedButNotReady -> BackupState.ReplacedNotReady
                        null -> {
                            Log.w(TAG, "restore failed", result.exceptionOrNull())
                            BackupState.NotKeptNotReplaced
                        }
                    }
                    onDone(state)
                },
            )
        }
    }

    private fun refusalOf(refusal: Backup.Refusal): BackupRefusal = BackupRefusal.valueOf(refusal.name)

    /** Runs on the store thread. Resolving the path touches the filesystem too, so it happens here. */
    private fun openApp(): StoreOpenOutcome<OpenedApp> {
        // Whether a key is saved is read here, on the store thread, so the main thread only reads a flag (14G).
        primeAi()
        val path: File = getDatabasePath(AppGraph.DATABASE_NAME)
        path.parentFile?.mkdirs()
        return when (val result = StoreOpener.open(path.absolutePath)) {
            is StoreOpener.Result.Opened -> {
                val graph = AppGraph(persistence = result.store, content = FileContentSource(later = ::laterPackages, source = ::authoredPackage))
                // Ingestion runs here because it is disk work and because Today must be read after
                // it: publishing is what turns "nothing is published" into a curriculum Today can
                // report. With no authored package shipping, it publishes nothing and says so by
                // returning null (11D).
                // 15B: every package that ships is published once, oldest first; each later one only adds.
                val published = IngestCurriculum(graph.persistence, graph.content, graph.clock).ingestAll()
                Log.i(TAG, "curriculum ingestion: ${published.ifEmpty { "no authored package" }}")
                // 16E: opening the store plans nothing. Every start of the process opens it — a reminder's alarm and
                // a reboot signal too — and a plan written then would sit on a day the learner never opened. Today's
                // plan is made when the learner opens the app (MainActivity asks through refreshToday).
                // The reminder is armed again from the stored setting: an update or a restore may have cleared it.
                Reminders.applySetting(this, ReminderSettings(graph.persistence, graph.clock).read(), graph.clock.now().instantEpochMillis)
                StoreOpenOutcome.Opened(OpenedApp(graph))
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

    /**
     * The packages after the first (15B, `D-113`): `curriculum_package_v<N>.txt`, in the order of N. Each step of Stage
     * 15 ships its own; the content source checks that their versions rise and that nothing is authored twice.
     */
    private fun laterPackages(): List<String> = runCatching {
        assets.list("").orEmpty()
            .mapNotNull { name -> LATER_PACKAGE.matchEntire(name)?.let { it.groupValues[1].toInt() to name } }
            .sortedBy { it.first }
            .map { (_, name) -> assets.open(name).bufferedReader().use { it.readText() } }
    }.getOrDefault(emptyList())

    private companion object {
        val LATER_PACKAGE = Regex("^curriculum_package_v(\\d+)\\.txt$")
        const val AUTHORED_PACKAGE = "curriculum_package.txt"
        const val TAG = "coach.store"
    }
}
