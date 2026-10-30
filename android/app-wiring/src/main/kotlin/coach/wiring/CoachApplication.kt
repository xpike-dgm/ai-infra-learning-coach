package coach.wiring

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.net.Uri
import coach.application.CapacitySettings
import coach.application.ReminderSettings
import coach.application.CaptureProgramSnapshot
import coach.application.OpenTask
import coach.application.FastPath
import coach.application.ReadDiagnosticResult
import coach.application.RequestDiagnostic
import coach.application.RunHelp
import coach.presentation.FastPathView
import coach.presentation.MeasurementCopy
import coach.presentation.MeasurementPresentation
import coach.application.RunItems
import coach.application.RunTask
import coach.model.AnswerResult
import coach.model.CheckpointKind
import coach.model.ComprehensionCheck
import coach.model.ComprehensionResult
import coach.model.ExplanationForm
import coach.model.ExplanationOption
import coach.model.RunAnswer
import coach.model.RunItemKind
import coach.model.TutorAsk
import coach.presentation.RunHelpPresentation
import coach.presentation.TaskRunPresentation
import coach.presentation.WrongAnswerSummary
import coach.model.AnsweredItem
import coach.model.AssessmentItem
import coach.model.AssistanceEvent
import coach.model.ProgramSnapshot
import coach.model.ProvenanceOrigin
import coach.model.RunnableTask
import coach.model.TaskEntryFacts
import coach.presentation.ProgramChangeResults
import coach.presentation.TaskRunCopy
import coach.model.ReminderPreference
import coach.model.StoreStatus
import coach.presentation.NotificationAccess
import coach.application.AiUsage
import coach.application.DayCloseFacts
import coach.application.DisplaySettings
import coach.application.InitialSetup
import coach.application.RecordPilotJournal
import coach.model.SetupOutcome
import coach.presentation.SetupPresentation
import coach.presentation.CurriculumNames
import coach.presentation.SetupView
import coach.application.PlanToday
import coach.model.CapacityPreferences
import coach.model.CapacityProfile
import coach.model.EvaluatorAvailability
import coach.model.PilotJournalKind
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
import coach.model.AiCallRecord
import coach.model.DisplayPreference
import coach.model.ThemeChoice
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
        // 17E: every AI call that left the device is counted on the store thread, after whatever job made it, so a count
        // never lands inside another action's transaction. A count that cannot be written is lost, never a failure.
        listenForAiCalls { call ->
            storeThread.execute {
                startup.store?.let { opened -> runCatching { AiUsage(opened.graph.persistence, opened.graph.clock).record(call) } }
            }
        }
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
    fun refreshToday(onLoaded: (TodayFacts) -> Unit, onDay: (DayCloseFacts.Loaded) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            // 16D: a new study day gets its plan before Today is read — re-entry, never yesterday's plan replayed.
            planToday(opened.graph)
            // The day is read once, and both Today and the day's own record are read for it, so a process kept open past
            // midnight reports the new day rather than yesterday's (11E).
            val studyDay = opened.graph.clock.now().studyDay
            val facts = TodayFactsQuery(opened.graph.persistence, opened.graph.clock).load()
            main.post { onLoaded(facts) }
            // 17E: the day's record now replays the engines for the day's Skills, so Today is shown first and does not
            // wait for it; the record follows on the same thread.
            val day = DayCloseFacts(opened.graph.persistence, opened.graph.clock, opened.graph.content).load(studyDay)
            main.post { onDay(day) }
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
            // 16H: with the clock, Progress can name what an assessment session could measure now.
            val facts = ProgressQuery(opened.graph.persistence, opened.graph.content, opened.graph.clock).load()
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
            val facts = LearningHistoryQuery(opened.graph.persistence, opened.graph.content).load(skill)
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

    // ---------------------------------------------------------------- running a planned task (16F)

    /**
     * Entry for one of today's planned tasks, off the main thread (16F): every condition is confirmed in core, and when
     * the task can run the program's state is captured first, so the end of the run can say what really changed.
     */
    fun openTask(plannedTaskId: Long, onDone: (TaskEntryFacts, ProgramSnapshot?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val facts = OpenTask(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).open(plannedTaskId)
            val before = facts.run?.let { CaptureProgramSnapshot(graph.persistence, graph.clock).capture() }
            main.post { onDone(facts, before) }
        }
    }

    /**
     * The run's next unseen question and its text, recorded as seen when shown; `null` when none is left. 16G: with how
     * it is answered and, for a code item, the runner and the item's suite to copy.
     */
    internal fun serveNext(run: RunnableTask, served: Set<VersionedRef>, onDone: (AssessmentItem?, String?, ServedItem?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val item = RunTask(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).serve(run, served)
            val prompt = item?.let { graph.content.resource(it.ref)?.body }
            val kind = item?.let { RunItems.kind(it, graph.content) }
            val code = if (item != null && kind == RunItemKind.CODE_TESTS) {
                TaskRunPresentation.codeTransfer(item.ref, graph.content.codeRunnerText(), graph.content.codeSuiteText(item.ref))
            } else null
            main.post { onDone(item, prompt, kind?.let { ServedItem(it, code) }) }
        }
    }

    /** Records one frozen answer and its evidence; `null` for a skip or an answer too long to carry. */
    fun answerItem(
        run: RunnableTask, item: AssessmentItem, response: String, provenance: ProvenanceOrigin, assistance: List<AssistanceEvent>,
        onDone: (AnsweredItem?) -> Unit,
    ) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val answered = RunTask(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).answer(run, item, response, provenance, assistance)
            main.post { onDone(answered) }
        }
    }

    /** The accepted answer, after the learner asked; recorded as help after submission and as a solution exposure. */
    fun revealAnswer(item: AssessmentItem, attemptId: Long?, onDone: (List<String>) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val run = RunTask(graph.persistence, graph.content, graph.clock, evaluatorAvailable())
            val answers = if (attemptId != null) run.reveal(item, attemptId) else run.revealBeforeAnswer(item)
            main.post { onDone(answers) }
        }
    }

    /**
     * The end of a run, finished or stopped: what canonical state and the plan really did (13E), worded in core, and
     * Today's facts read again, because a replan may have changed today's plan.
     */
    fun finishRun(
        run: RunnableTask, before: ProgramSnapshot, answered: List<AnsweredItem>, items: List<AssessmentItem>,
        // 16H: in a measurement, the questions left unanswered and those reported, for the session's result.
        skipped: Set<VersionedRef> = emptySet(),
        contested: Set<VersionedRef> = emptySet(),
        onDone: (List<String>, TodayFacts, DayCloseFacts.Loaded) -> Unit,
    ) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val reported = RunTask(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).finish(run, before, answered, items)
            // 18A: the Skill's Turkish name, as everywhere else since 17A — first runs now report a change, which showed the
            // authored English name here.
            val label: (VersionedRef) -> String = { ref -> CurriculumNames.skill(ref, graph.persistence.skill(ref)?.canonicalName) }
            val reportedLines = when (reported) {
                null -> listOf(TaskRunCopy.NOTHING_ANSWERED)
                else -> ProgramChangeResults.summary(reported.report)?.let(::listOf)
                    ?: ProgramChangeResults.canonicalChanges(reported.report, label).values.flatten()
            }
            // 16H: a measurement's result is `ASUX-v0`'s — its state, what could not be measured, then what changed.
            val measurement = run.measurement
            val lines = if (measurement == null) reportedLines else MeasurementPresentation.lines(
                MeasurementPresentation.result(measurement.scope, items.map { it.ref }, answered, skipped, contested), reportedLines)
            val facts = TodayFactsQuery(graph.persistence, graph.clock).load()
            val day = DayCloseFacts(graph.persistence, graph.clock, graph.content).load()
            main.post { onDone(lines, facts, day) }
        }
    }

    // ---------------------------------------------------------------- judged answers and help inside a run (16G)
    //
    // On the store thread, tutor and evaluator calls included: what the tutor showed, and what the evaluator said, are
    // written in the same job, and the store has one writer. The learner is waiting on that answer in any case.

    private fun runTask(graph: AppGraph) = RunTask(graph.persistence, graph.content, graph.clock, evaluatorAvailable(), graph.evaluator)
    private fun runHelp(graph: AppGraph) = RunHelp(graph.persistence, graph.content, graph.clock, graph.tutor)

    /**
     * A code answer with the pasted report, or an open answer (16G). Recorded or not, worded in core: what the tests or
     * the rubric said per Objective, what a wrong answer said (14B), and the comprehension check (14E).
     */
    internal fun answerJudged(
        run: RunnableTask, item: AssessmentItem, kind: RunItemKind, response: String, report: String,
        provenance: ProvenanceOrigin, assistance: List<AssistanceEvent>, onDone: (Judged) -> Unit,
    ) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val answer = when (kind) {
                RunItemKind.CODE_TESTS -> runTask(graph).answerCode(run, item, response, report, provenance, assistance)
                RunItemKind.RUBRIC -> runTask(graph).answerOpen(run, item, response, provenance, assistance)
                RunItemKind.ANSWER_KEY -> error("an answer-key item is answered by answerItem")
            }
            val judged = judged(graph, run, item, kind, answer)
            main.post { onDone(judged) }
        }
    }

    /** A pending open answer judged again, only because the learner asked (14F). */
    internal fun reevaluate(run: RunnableTask, item: AssessmentItem, pending: AnsweredItem, response: String, onDone: (Judged) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val judged = judged(graph, run, item, RunItemKind.RUBRIC, runTask(graph).reevaluate(run, item, pending, response))
            main.post { onDone(judged) }
        }
    }

    private fun judged(graph: AppGraph, run: RunnableTask, item: AssessmentItem, kind: RunItemKind, answer: RunAnswer): Judged = when (answer) {
        is RunAnswer.NotRecorded -> Judged(answer, null, TaskRunPresentation.notRecorded(answer, kind))
        is RunAnswer.Recorded -> Judged(
            answer,
            TaskRunPresentation.answered(answer, kind, ::objectiveName).copy(
                wrongAnswer = wrongAnswerOn(graph, run, item, answer.answered),
                comprehension = RunHelpPresentation.comprehension(answer.offer, 0),
            ),
            null,
        )
    }

    /**
     * 17A: an Objective in words — its lesson's own title, read from the content — and the identifier's words only when no
     * lesson is authored. The store is read on the store thread; the content is in memory.
     */
    private fun objectiveName(ref: VersionedRef): String =
        startup.store?.graph?.content?.lessonTitleFor(ref) ?: TaskRunPresentation.objectiveLabel(ref)

    /** What a wrong answer said, read from the engines' attribution (14B); `null` when it said nothing or was not wrong. */
    fun wrongAnswer(run: RunnableTask, item: AssessmentItem, answered: AnsweredItem, onDone: (WrongAnswerSummary.Summary?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val summary = wrongAnswerOn(opened.graph, run, item, answered)
            main.post { onDone(summary) }
        }
    }

    private fun wrongAnswerOn(graph: AppGraph, run: RunnableTask, item: AssessmentItem, answered: AnsweredItem): WrongAnswerSummary.Summary? {
        if (answered.evidenceIds.isEmpty() || (answered.result != AnswerResult.NOT_RIGHT && answered.result != AnswerResult.PARTLY)) return null
        val skill = run.skill.takeIf { it in item.targetSkills } ?: item.targetSkills.first()
        val findings = runHelp(graph).wrongAnswer(skill, answered.evidenceIds)
        return WrongAnswerSummary.of(findings, ::objectiveName).takeIf { it.lines.isNotEmpty() }
    }

    /** The rubric for the learner's own check of a frozen open answer (14F), recorded as the exposure it is. */
    fun selfCheck(item: AssessmentItem, attemptId: Long, onDone: (List<String>) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val lines = TaskRunPresentation.selfCheck(runTask(opened.graph).selfCheck(item, attemptId))
            main.post { onDone(lines) }
        }
    }

    /** One ask of the tutor (14A); nothing is sent until the rules say it is ready. */
    fun askHelp(ask: TutorAsk, item: AssessmentItem, attemptId: Long?, onDone: (RunHelp.Asked) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val asked = runHelp(opened.graph).ask(ask, item, attemptId)
            main.post { onDone(asked) }
        }
    }

    /** The forms another explanation can come in (14C), for the question's first Objective. */
    fun explanationMenu(objective: VersionedRef, seen: Set<ExplanationForm>, onDone: (List<ExplanationOption>) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val options = runHelp(opened.graph).menu(objective, seen)
            main.post { onDone(options) }
        }
    }

    /** One form shown, written first (14C); `null` when the form has nothing true to show. */
    fun explain(base: TutorAsk, objective: VersionedRef, form: ExplanationForm, item: AssessmentItem, attemptId: Long?, onDone: (RunHelp.Asked?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val asked = runHelp(opened.graph).explain(base, objective, form, item, attemptId)
            main.post { onDone(asked) }
        }
    }

    /** The tutor's practice question about the learner's own code, or its response (14E): never evidence. */
    fun practice(ask: TutorAsk, item: AssessmentItem, attemptId: Long, onDone: (RunHelp.Asked?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val asked = runHelp(opened.graph).practice(ask, item, attemptId)
            main.post { onDone(asked) }
        }
    }

    /** The written checks for this code item (14E), to answer them one by one. */
    fun comprehensionChecks(item: AssessmentItem, onDone: (List<ComprehensionCheck>) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val checks = opened.graph.content.comprehensionChecksFor(item.ref)
            main.post { onDone(checks) }
        }
    }

    /** One answer to a written comprehension check (14E): evidence of the kind the check declares, or a skip. */
    fun comprehensionAnswer(
        run: RunnableTask, item: AssessmentItem, check: ComprehensionCheck, chosen: String?, attemptId: Long,
        onDone: (ComprehensionResult) -> Unit,
    ) {
        val opened = startup.store ?: return
        storeThread.execute {
            val result = runHelp(opened.graph).comprehensionAnswer(run, item, check, chosen, attemptId)
            main.post { onDone(result) }
        }
    }

    /** A durable pause, written as one checkpoint (11C); `null` when nothing is left to come back to. */
    fun pauseRun(run: RunnableTask, kind: CheckpointKind, shown: List<VersionedRef>, remaining: List<VersionedRef>, onDone: (Long?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val id = runHelp(opened.graph).pause(run, kind, shown, remaining)
            main.post { onDone(id) }
        }
    }

    // ---------------------------------------------------------------- measurement sessions and the fast path (16H)

    /** Opens a daily assessment's session when the learner begins; any other run comes back unchanged. */
    fun beginRun(run: RunnableTask, onDone: (RunnableTask) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val begun = runTask(opened.graph).beginSession(run)
            main.post { onDone(begun) }
        }
    }

    /** The learner's report that an answered question was wrong or unanswerable: its evidence is held as contested. */
    fun contestAnswer(run: RunnableTask, answered: AnsweredItem, onDone: () -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            runTask(opened.graph).contest(run, answered)
            main.post { onDone() }
        }
    }

    /** The fast path on a Topic: the open diagnostic's result when it checks one of [skills], else the offer. */
    fun loadFastPath(skills: List<VersionedRef>, onDone: (FastPathView) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val view = when (val read = FastPath(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).result()) {
                is ReadDiagnosticResult.Read.Result ->
                    if (read.result.scope.skills.any { it in skills }) FastPathView.open(read.result, ::objectiveName) else FastPathView.offer()
                ReadDiagnosticResult.Read.NothingOpen -> FastPathView.offer()
                is ReadDiagnosticResult.Read.Unreadable -> FastPathView.Said(MeasurementCopy.FAST_PATH_UNREADABLE)
            }
            main.post { onDone(view) }
        }
    }

    /** Asks for the fast path over a Topic's Skills; today's plan is replanned with its checks (`D-132`). */
    fun requestFastPath(skills: List<VersionedRef>, onDone: (FastPathView, TodayFacts) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val view = when (FastPath(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).request(skills).request) {
                is RequestDiagnostic.Requested.Written -> FastPathView.Said(MeasurementCopy.FAST_PATH_REQUESTED)
                RequestDiagnostic.Requested.NothingToDiagnose -> FastPathView.Said(MeasurementCopy.FAST_PATH_NOTHING)
                else -> FastPathView.Said(MeasurementCopy.FAST_PATH_NOT_STARTABLE)
            }
            val facts = TodayFactsQuery(graph.persistence, graph.clock).load()
            main.post { onDone(view, facts) }
        }
    }

    /** Leaves the fast path; what was waived stays waived. */
    fun withdrawFastPath(onDone: (FastPathView) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            FastPath(graph.persistence, graph.content, graph.clock, evaluatorAvailable()).withdraw()
            main.post { onDone(FastPathView.Said(MeasurementCopy.FAST_PATH_WITHDRAWN)) }
        }
    }

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

    /**
     * Whether the first-run setup is to be shown, and what it offers (18A), off the main thread. `null` when it is not:
     * the learner went through it, or the store was used before (`InitialSetup.needed`).
     */
    fun checkSetup(onLoaded: (SetupView?) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val graph = opened.graph
            val setup = InitialSetup(graph.persistence, graph.content, graph.clock)
            val view = if (!setup.needed()) null else SetupPresentation.of(
                setup.topics().map { it.topic to it.skills },
                CapacitySettings(graph.persistence, graph.clock).read().normalMinutes,
            )
            main.post { onLoaded(view) }
        }
    }

    /** Records how the setup ended (18A); Today is planned after it, by the caller's refresh. */
    fun finishSetup(outcome: SetupOutcome, normalMinutes: Int?, placement: List<VersionedRef>, onDone: () -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            InitialSetup(opened.graph.persistence, opened.graph.content, opened.graph.clock).finish(outcome, normalMinutes, placement)
            main.post { onDone() }
        }
    }

    /**
     * One pilot journal entry (18G); it changes no plan, so nothing is refreshed after it. [onDone] hears whether it was
     * saved: a closed store or a failed write is said, never passed over in silence.
     */
    fun recordPilotJournal(kind: PilotJournalKind, note: String, onDone: (Boolean) -> Unit) {
        val opened = startup.store ?: return onDone(false)
        storeThread.execute {
            val saved = runCatching { RecordPilotJournal(opened.graph.persistence, opened.graph.clock).record(kind, note) }
                .getOrNull() is RecordPilotJournal.Result.Recorded
            main.post { onDone(saved) }
        }
    }

    /** The theme and motion setting, off the main thread (17E). */
    fun loadDisplay(onLoaded: (DisplayPreference) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val preference = runCatching { DisplaySettings(opened.graph.persistence, opened.graph.clock).read() }.getOrDefault(DisplayPreference.SYSTEM_DEFAULT)
            main.post { onLoaded(preference) }
        }
    }

    /** Records the learner's theme and motion setting (17E); it replans nothing. */
    fun setDisplay(theme: ThemeChoice, reduceMotion: Boolean, onDone: (DisplayPreference) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val settings = DisplaySettings(opened.graph.persistence, opened.graph.clock)
            settings.set(theme, reduceMotion)
            val preference = settings.read()
            main.post { onDone(preference) }
        }
    }

    /** The recorded AI calls, counts only, off the main thread (17E). */
    fun loadAiUsage(onLoaded: (List<AiCallRecord>, String) -> Unit) {
        val opened = startup.store ?: return
        storeThread.execute {
            val calls = AiUsage(opened.graph.persistence, opened.graph.clock).read()
            val today = opened.graph.clock.now().studyDay
            main.post { onLoaded(calls, today) }
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
                // 16G: the course's code-test suites and the PC runner ship as assets, for the learner to copy (copy-paste).
                val content = FileContentSource(later = ::laterPackages, codeSuites = ::codeSuite, codeRunner = ::codeRunner, source = ::authoredPackage)
                val graph = AppGraph(persistence = result.store, content = content)
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

    /** One shipped suite by its asset name (`tools/ship_code_suites.py`); `null` when none ships under that name. */
    private fun codeSuite(name: String): String? = runCatching {
        assets.open("$CODE_SUITES/$name").bufferedReader().use { it.readText() }
    }.getOrNull()

    private fun codeRunner(): String? = runCatching {
        assets.open(CODE_RUNNER).bufferedReader().use { it.readText() }
    }.getOrNull()

    private companion object {
        const val CODE_SUITES = "code_suites"
        const val CODE_RUNNER = "code_test_runner.py"
        val LATER_PACKAGE = Regex("^curriculum_package_v(\\d+)\\.txt$")
        const val AUTHORED_PACKAGE = "curriculum_package.txt"
        const val TAG = "coach.store"
    }
}
