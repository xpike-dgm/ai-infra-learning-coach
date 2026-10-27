package coach.wiring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coach.application.DayCloseFacts
import coach.application.StoreStartup
import coach.model.StoreStatus
import coach.model.PlannerExplanationFacts
import coach.model.TodayFacts
import coach.presentation.AppHealth
import coach.presentation.DaySummaryInput
import coach.presentation.Destination
import coach.presentation.HealthAction
import coach.presentation.NavigationGraph
import coach.presentation.PlannerExplanationPresentation
import coach.presentation.PlannerExplanationView
import coach.presentation.AssessmentReportView
import coach.presentation.HistoryPresentation
import coach.presentation.LearningHistoryView
import coach.presentation.ProgressOverview
import coach.presentation.ProgressPresentation
import coach.presentation.SkillDetailView
import coach.presentation.EnglishProfilePresentation
import coach.presentation.EnglishProfileView
import coach.presentation.TopicDetailView
import coach.presentation.TopicOverviewView
import coach.presentation.TopicPresentation
import coach.presentation.LearnPresentation
import coach.presentation.LearnView
import coach.presentation.Revalidation
import coach.presentation.RunnerFlow
import coach.presentation.RunnerRevalidation
import coach.presentation.SessionEntrySource
import coach.presentation.SessionEvent
import coach.presentation.WorkingSession
import coach.presentation.WorkingSessions
import coach.presentation.EndOfDay
import coach.presentation.ShellState
import coach.presentation.Surface
import coach.presentation.TodayPresentation
import coach.presentation.WindowClass
import coach.presentation.todayInput
import coach.ui.AppRoot
import coach.ui.AppShell
import coach.ui.CoachTheme
import coach.ui.HealthBlockingSurface
import coach.ui.HealthContextLine
import coach.ui.PlannerExplanationScreen
import coach.ui.AssessmentReportScreen
import coach.ui.LearningHistoryScreen
import coach.ui.ProgressScreen
import coach.ui.SkillDetailScreen
import coach.ui.EnglishProfileScreen
import coach.ui.TopicDetailScreen
import coach.ui.LearnScreen
import coach.ui.TaskRunnerScreen
import coach.ui.TodayScreen
import coach.ui.ProfileScreen
import coach.ui.TodayCapacityDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coach.model.CapacityPreferences
import coach.presentation.BackupState
import coach.presentation.SettingsPresentation
import coach.presentation.AiCheckState
import coach.presentation.AiSettingsPresentation
import coach.model.AssistanceLevel
import coach.model.AssistanceScope
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.presentation.AssistanceOutcome
import coach.presentation.AssistancePolicy
import coach.presentation.TaskRunCopy
import coach.presentation.TaskRunPresentation
import coach.presentation.TaskRunStage
import coach.model.RunItemKind
import coach.presentation.FastPathView
import coach.model.VersionedRef
import coach.ui.TaskRunActions
import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import coach.model.ReminderPreference
import coach.presentation.NotificationAccess
import coach.presentation.ReminderPresentation

class MainActivity : ComponentActivity() {

    private val storeStatus = mutableStateOf<StoreStatus>(StoreStatus.Opening)
    private val opened = mutableStateOf<OpenedApp?>(null)
    private val todayFacts = mutableStateOf<TodayFacts?>(null)
    private val dayFacts = mutableStateOf<DayCloseFacts.Loaded?>(null)
    // 16D: a restore closes and reopens the store, and the shell leaves composition while it does; the backup
    // controls' state lives here so the restore's outcome is still there when Profile is shown again.
    private val backupState = mutableStateOf<BackupState>(BackupState.Idle)
    // 16E: the reminder setting and what Android allows. Read again on every start, because the learner may have
    // changed this app's notifications in Android's own settings in the meantime.
    private val reminderState = mutableStateOf<Pair<ReminderPreference, NotificationAccess>?>(null)
    private val listener = StoreStartup.Listener<OpenedApp> { status, store ->
        storeStatus.value = status
        opened.value = store
        // 16E: the learner is opening the app, so today is planned now and then read; opening the store alone plans
        // nothing. Until the facts arrive the app is still opening, and the blocking surface says so.
        todayFacts.value = null
        dayFacts.value = null
        if (store != null) {
            app.refreshToday { today, day ->
                todayFacts.value = today
                dayFacts.value = day
            }
            app.loadReminder { preference, access -> reminderState.value = preference to access }
        }
    }

    private val app: CoachApplication get() = application as CoachApplication
    private val startup: StoreStartup<OpenedApp> get() = app.startup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 17A: the app draws edge to edge on every API level, as Android 15+ enforces for this target SDK, so the
        // emulator and the target device (API 36) lay out the same; every screen pads itself to the safe drawing area.
        enableEdgeToEdge()
        // The store is opened by the process on its own thread. The activity only observes, so
        // the first frame never waits for disk work and a failure is a state, not a crash.
        startup.observe(listener)

        setContent {
            // Presentation is decided in core; this only feeds it the facts. Without a store in
            // hand the app is still opening, whatever status arrived first, so the blocking surface
            // always has a state to render.
            val current = opened.value
            val facts = todayFacts.value
            // 17A: until the store and today's facts are both in hand the app is still opening (`AppHealth.atStartup`).
            val health = AppHealth.atStartup(storeStatus.value, evaluatorAvailability(), storeInHand = current != null, todayInHand = facts != null)

            CoachTheme {
                if (!health.showsShell || current == null || facts == null) {
                    HealthBlockingSurface(
                        health = health,
                        onAction = { action ->
                            when (action) {
                                HealthAction.RECHECK -> startup.recheck()
                            }
                        },
                    )
                } else {
                    Shell(health, current, facts, dayFacts.value, explain = app::loadExplanation, app = app,
                        onTodayFacts = { todayFacts.value = it }, backupState = backupState, reminderState = reminderState,
                        openNotificationSettings = {
                            startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName))
                        })
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // A process kept open past midnight would otherwise keep yesterday's study day.
        app.refreshToday { today, day ->
            todayFacts.value = today
            dayFacts.value = day
        }
        app.loadReminder { preference, access -> reminderState.value = preference to access }
    }

    override fun onDestroy() {
        startup.stopObserving(listener)
        super.onDestroy()
    }
}

@Composable
private fun Shell(
    health: AppHealth,
    opened: OpenedApp,
    facts: TodayFacts,
    day: DayCloseFacts.Loaded?,
    explain: ((PlannerExplanationFacts) -> Unit) -> Unit,
    app: CoachApplication,
    // 16D: a capacity change replans today; Today's facts are the activity's, so the new ones are handed back.
    onTodayFacts: (TodayFacts) -> Unit,
    backupState: MutableState<BackupState>,
    reminderState: MutableState<Pair<ReminderPreference, NotificationAccess>?>,
    openNotificationSettings: () -> Unit,
) {
    var selected by remember { mutableStateOf(Destination.start) }
    // The focused flow in front of the shell, if any. Its entry decision is made in core.
    var runnerEntry by remember { mutableStateOf<Revalidation?>(null) }
    // The emergent working session (11C). It is not stored and lives no longer than the focused
    // flow's own state; it starts only when a run really starts, so while nothing is startable it
    // never starts at all.
    var session by remember { mutableStateOf<WorkingSession?>(null) }
    var sessionsStarted by remember { mutableStateOf(0L) }
    // 16F: the task run in front of the shell — its working memory and the stage core decided. Neither is stored; what
    // the learner did is already in the store the moment it is recorded.
    var runSession by remember { mutableStateOf<TaskRunSession?>(null) }
    var runStage by remember { mutableStateOf<TaskRunStage?>(null) }
    // The shared planner explanation opened from Today (12E); its facts are read on the store thread.
    var explanation by remember { mutableStateOf<PlannerExplanationView?>(null) }
    // The AI settings on Profile (14G). The key state is read from memory; saving, removing and checking run off the
    // main thread, and the key itself is never held here beyond the moment it is handed over to be stored.
    var aiKey by remember { mutableStateOf(app.aiSettings.state()) }
    var aiCheck by remember { mutableStateOf(AiCheckState.IDLE) }
    // Progress (16A): the overview read when the destination is chosen, and the one shared skill_detail opened from it.
    // Both are read on the store thread and projected in core; nothing here decides a Skill's state.
    var progress by remember { mutableStateOf<ProgressOverview?>(null) }
    var skillDetail by remember { mutableStateOf<SkillDetailView?>(null) }
    // 16B: the learning history (whole, or one Skill's) and the longitudinal assessment report, read on the store thread.
    var history by remember { mutableStateOf<Pair<String, LearningHistoryView>?>(null) }
    var report by remember { mutableStateOf<AssessmentReportView?>(null) }
    // 16C: the Topics shown on Progress, the one topic_detail opened from it, and the Technical English profile.
    var topics by remember { mutableStateOf<TopicOverviewView?>(null) }
    // 17A: `learn_overview` — the learning map, from the same Topic facts Progress reads.
    var learn by remember { mutableStateOf<LearnView?>(null) }
    var learnMissing by remember { mutableStateOf(false) }
    var topicDetail by remember { mutableStateOf<TopicDetailView?>(null) }
    // 16H: the open Topic's Skills, and the fast path as it stands for them.
    var topicSkills by remember { mutableStateOf<List<VersionedRef>>(emptyList()) }
    var fastPath by remember { mutableStateOf<FastPathView?>(null) }
    var english by remember { mutableStateOf<EnglishProfileView?>(null) }
    // 16D: the capacity preferences (Profile and Today's capacity dialog) and the backup controls' state.
    var capacity by remember { mutableStateOf<CapacityPreferences?>(null) }
    var todayDialog by remember { mutableStateOf(false) }
    var backup by backupState
    // The system's own file pickers: the learner chooses where a backup goes and which one comes back (`LFPS-v0` §11).
    val exportTo = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri == null) backup = BackupState.Idle else app.exportBackup(uri) { backup = it }
    }
    val restoreFrom = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) backup = BackupState.Idle else app.prepareRestore(uri) { backup = it }
    }
    // 16E: the reminder. Turning it on asks Android's permission first where Android requires one (13+); a refusal
    // records nothing and leaves the reminder off.
    var reminder by reminderState
    var reminderRefused by remember { mutableStateOf(false) }
    var minuteAwaitingPermission by remember { mutableStateOf<Int?>(null) }
    val reminderChanged: (ReminderPreference, NotificationAccess) -> Unit = { preference, access ->
        reminder = preference to access
        reminderRefused = false
    }
    val askNotificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val minute = minuteAwaitingPermission
        minuteAwaitingPermission = null
        if (granted && minute != null) {
            app.setReminder(true, minute, reminderChanged)
        } else {
            app.loadReminder { preference, access -> reminder = preference to access; reminderRefused = true }
        }
    }
    val capacityChanged: (CapacityPreferences, TodayFacts) -> Unit = { preferences, todays ->
        capacity = preferences
        onTodayFacts(todays)
    }

    // The window class is computed by core-presentation from the accepted WFPX-v0
    // breakpoints, not by the UI toolkit's own bucketing, so the mapping stays canonical.
    val windowClass = WindowClass.ofWidthDp(LocalConfiguration.current.screenWidthDp)
    val today = TodayPresentation.of(todayInput(facts, health))

    val state = ShellState(
        selected = selected,
        // A focused flow suspends the shell; NSHX-v0 derives that from the surface itself.
        surface = when {
            runnerEntry != null -> RunnerFlow.surface
            explanation != null -> Surface.PlannerExplanation
            history != null -> Surface.LearningHistory
            report != null -> Surface.AssessmentReport
            skillDetail != null -> Surface.SkillDetail
            topicDetail != null -> Surface.TopicDetail
            english != null -> Surface.TechnicalEnglishProfile
            else -> Surface.rootOf(selected)
        },
        windowClass = windowClass,
    )

    val prefs = capacity
    if (todayDialog && prefs != null) {
        TodayCapacityDialog(
            options = SettingsPresentation.todayOptions(prefs),
            onChoose = { choice -> todayDialog = false; app.setTodayCapacity(choice, capacityChanged) },
            onDismiss = { todayDialog = false },
        )
    }

    AppShell(state = state, onSelect = { destination ->
        selected = destination
        explanation = null
        skillDetail = null
        history = null
        report = null
        topicDetail = null
        english = null
        if (destination == Destination.PROFILE) app.loadCapacity { capacity = it }
        if (destination == Destination.LEARN) {
            learn = null
            app.loadProgress { _, topicFacts ->
                learn = LearnPresentation.overview(topicFacts)
                learnMissing = !topicFacts.curriculumLoaded
            }
        }
        if (destination == Destination.PROGRESS) {
            progress = null
            topics = null
            app.loadProgress { facts, topicFacts ->
                progress = ProgressPresentation.overview(facts, health)
                topics = TopicPresentation.overview(topicFacts)
            }
        }
    }) {
        val entry = runnerEntry
        val shown = explanation
        when {
            shown != null -> PlannerExplanationScreen(view = shown, onBack = { explanation = null })
            history != null -> LearningHistoryScreen(
                view = history!!.second,
                title = history!!.first,
                onBack = { history = null },
            )
            report != null -> AssessmentReportScreen(
                view = report!!,
                onOpenHistory = {
                    if (NavigationGraph.canOpen(Surface.AssessmentReport.id, Surface.LearningHistory.id)) {
                        app.loadHistory(null) { facts -> history = "Öğrenme geçmişi" to HistoryPresentation.history(facts, health) }
                    }
                },
                onBack = { report = null },
            )
            skillDetail != null -> SkillDetailScreen(
                view = skillDetail!!,
                onOpenHistory = {
                    val shown = skillDetail!!
                    if (NavigationGraph.canOpen(Surface.SkillDetail.id, Surface.LearningHistory.id)) {
                        app.loadHistory(shown.skill) { facts -> history = shown.name to HistoryPresentation.history(facts, health) }
                    }
                },
                // A blocker opens its own skill_detail: the same surface, never a second Skill truth.
                onOpenSkill = { ref -> app.loadSkillDetail(ref) { facts -> facts?.let { skillDetail = ProgressPresentation.detail(it) } } },
                onBack = { skillDetail = null },
            )
            topicDetail != null -> TopicDetailScreen(
                view = topicDetail!!,
                onOpenSkill = { ref ->
                    if (NavigationGraph.canOpen(Surface.TopicDetail.id, Surface.SkillDetail.id)) {
                        app.loadSkillDetail(ref) { facts -> facts?.let { skillDetail = ProgressPresentation.detail(it) } }
                    }
                },
                onBack = { topicDetail = null },
                // 16H: the fast path lives on the Topic (user decision); only the learner opens it.
                fastPath = fastPath,
                onRequestFastPath = { app.requestFastPath(topicSkills) { view, facts -> fastPath = view; onTodayFacts(facts) } },
                onWithdrawFastPath = { app.withdrawFastPath { view -> fastPath = view } },
            )
            english != null -> EnglishProfileScreen(view = english!!, onBack = { english = null })
            entry != null -> {
                val run = runSession
                // 16F: the next question, or the end of the run when none is left. The runner never advances a cached
                // list: each question is the next unseen one the task holds (`TRUX-v0` §6.6).
                fun serveNext(current: TaskRunSession) {
                    runStage = TaskRunStage.Working
                    app.serveNext(current.run, current.shown.map { it.ref }.toSet()) { item, prompt, served ->
                        if (item == null) {
                            finishRun(app, current, onTodayFacts) { lines -> runStage = TaskRunStage.Finished(listOf(TaskRunCopy.NO_MORE_QUESTIONS) + lines) }
                        } else {
                            // 16G: how it is answered (key, tests or rubric) and, for code, what the learner copies.
                            current.newQuestion(item, prompt.orEmpty(), served)
                            runStage = current.questionStage()
                        }
                    }
                }
                fun question(current: TaskRunSession, disclosure: Boolean = false, notice: String? = null) = current.questionStage(disclosure, notice)
                // 16G: judged answers, help, the comprehension check and a durable pause; every rule stays in core.
                val flow = run?.let { current ->
                    TaskRunFlow(app, current, show = { stage -> runStage = stage }, finish = { onLines -> finishRun(app, current, onTodayFacts, onLines) })
                }
                TaskRunnerScreen(
                    state = runStage?.state ?: RunnerRevalidation.stateAtEntry(entry),
                    entry = entry,
                    // Back to Today in one action; NavigationGraph's return rule would also land here,
                    // because a run started from Today returns to Today. Stopping costs nothing; whatever was recorded
                    // is processed once, so the plan reflects it (`TRUX-v0` §7.4).
                    onExit = {
                        session = session?.let { WorkingSessions.on(it, SessionEvent.LearnerExited) }
                        run?.let { finishRun(app, it, onTodayFacts) {} }
                        runSession = null
                        runStage = null
                        runnerEntry = null
                        selected = Destination.TODAY
                    },
                    stage = runStage,
                    actions = if (run == null || flow == null) TaskRunActions() else TaskRunActions(
                        onBegin = { provenance ->
                            run.provenance = provenance
                            // 16H: a daily assessment opens its session as the learner begins; any other run is unchanged.
                            app.beginRun(run.run) { begun ->
                                run.run = begun
                                if (run.run.lesson.isNotEmpty()) runStage = TaskRunStage.Lesson(run.run.lesson.map { it.text }) else serveNext(run)
                            }
                        },
                        onToQuestions = { serveNext(run) },
                        onShowLesson = { run.lessonShown = true; runStage = question(run) },
                        onRequestSolution = {
                            // H4 before an answer: the consequence is said first, and nothing is shown until acknowledged.
                            val outcome = AssistancePolicy.request(AssistanceLevel.H4, AssistanceTiming.DURING_ATTEMPT,
                                AssistanceScope.TARGET_OBJECTIVE, AssistanceSource.DETERMINISTIC_CONTENT, consequenceAcknowledged = false)
                            if (outcome is AssistanceOutcome.ConsequenceDisclosureRequired) runStage = question(run, disclosure = true)
                        },
                        onAcknowledgeSolution = {
                            val item = run.current
                            if (item != null) app.revealAnswer(item, attemptId = null) { answers ->
                                runStage = TaskRunStage.ShownBeforeAnswer(TaskRunPresentation.revealed(answers))
                            }
                        },
                        onDeclineSolution = { runStage = question(run) },
                        onSubmit = { typed ->
                            val item = run.current
                            val provenance = run.provenance
                            if (item != null && provenance != null && run.kind == RunItemKind.RUBRIC) {
                                // 16G: an open answer is judged by its rubric (14F), provisional at most.
                                flow.submitJudged(typed, report = "")
                            } else if (item != null && provenance != null) {
                                // The lesson seen again, and help shown while the answer was open, go with it (`TRUX-v0` §8.5).
                                val help = run.helpWithAnswer()
                                runStage = TaskRunStage.Working
                                app.answerItem(run.run, item, typed, provenance, help) { answered ->
                                    if (answered == null) {
                                        runStage = question(run, notice = if (typed.isBlank()) TaskRunCopy.BLANK_IS_A_SKIP else TaskRunCopy.TOO_LONG)
                                    } else {
                                        run.answered += answered
                                        run.lastAnswer = answered
                                        val stage = TaskRunStage.Answered(TaskRunPresentation.resultLine(answered.result), revealed = null)
                                        runStage = stage
                                        flow.keyAnswered(answered, typed, stage)
                                    }
                                }
                            }
                        },
                        onReveal = {
                            val item = run.current
                            val answered = run.lastAnswer
                            if (item != null && answered != null) app.revealAnswer(item, answered.attemptId) { answers ->
                                val shown = (run.answeredStage ?: TaskRunStage.Answered(TaskRunPresentation.resultLine(answered.result), revealed = null))
                                    .copy(revealed = TaskRunPresentation.revealed(answers))
                                run.answeredStage = shown
                                flow.redraw()
                            }
                        },
                        onNext = { serveNext(run) },
                        onSubmitCode = { code, report -> flow.submitJudged(code, report) },
                        onReevaluate = { flow.reevaluate() },
                        onSelfCheck = { flow.selfCheck() },
                        onOpenHelp = { flow.openHelp() },
                        onCloseHelp = { flow.closeHelp() },
                        onHelpIntent = { flow.intent(it) },
                        onHelpLevel = { flow.level(it) },
                        onHelpAcknowledge = { flow.acknowledge() },
                        onHelpQuestion = { flow.question(it) },
                        onExplanationForm = { flow.form(it) },
                        onCheckChoice = { flow.checkChoice(it) },
                        onNextCheck = { flow.nextCheck() },
                        onPracticeAsk = { flow.practiceAsk() },
                        onPracticeAnswer = { flow.practiceAnswer(it) },
                        onPause = { flow.pause() },
                        onSkip = { flow.skip { serveNext(run) } },
                        onContest = { flow.contest() },
                        onFinish = {
                            runStage = TaskRunStage.Working
                            finishRun(app, run, onTodayFacts) { lines -> runStage = TaskRunStage.Finished(lines) }
                        },
                    ),
                )
            }
            selected == Destination.TODAY -> TodayScreen(
                // The day's own record, projected in core: what today recorded, never how it went.
                daySummary = day?.let {
                    EndOfDay.of(DaySummaryInput(record = it.record, health = health, unreadKinds = it.unreadKinds))
                },
                // Today's own projection: which state applies and which task may be offered is
                // decided in core, never here (MSBX-v0).
                view = today,
                // 16D: `change_today_capacity` — the dialog's options are the learner's own profile lengths.
                onChangeTodayCapacity = {
                    app.loadCapacity { capacity = it; todayDialog = true }
                },
                // Entry is revalidated in core against what can actually be confirmed; nothing
                // unconfirmed is assumed to hold (TRUX-v0 entry_revalidation).
                onStart = {
                    val task = today.primaryTask
                    if (task != null) {
                        // 16F: every entry condition is confirmed in core from the store, the gate, the open needs and
                        // the authored task (`TRUX-v0` §6.1); nothing unconfirmed is assumed to hold.
                        app.openTask(task.plannedTaskRef) { facts, before ->
                            val entry = RunnerRevalidation.atEntry(TaskRunPresentation.confirmed(facts))
                            runnerEntry = entry
                            val runnable = facts.run
                            if (runnable != null && before != null) {
                                runSession = TaskRunSession(runnable, before)
                                runStage = TaskRunStage.Orient(TaskRunPresentation.orientation(runnable))
                            } else {
                                runSession = null
                                runStage = TaskRunStage.Blocked((entry as? Revalidation.NotStartable)?.unmet.orEmpty(), TaskRunPresentation.capabilityLine(facts))
                            }
                            WorkingSessions.startIfRunStarted(
                                entry = entry,
                                sessionId = sessionsStarted + 1,
                                at = opened.graph.clock.now(),
                                source = SessionEntrySource.TODAY_PRIMARY_ACTION,
                                sourceTaskId = task.plannedTaskRef.toString(),
                            )?.let { started ->
                                sessionsStarted += 1
                                session = started
                            }
                        }
                    }
                },
                // Only the accepted contextual edges open anything; the explanation is the one with an
                // interior so far, and what it says is projected in core from the stored trace.
                onOpen = { surface ->
                    if (surface == Surface.PlannerExplanation &&
                        NavigationGraph.canOpen(Destination.TODAY.id, surface.id)
                    ) {
                        explain { facts -> explanation = PlannerExplanationPresentation.of(facts) }
                    }
                },
            )
            selected == Destination.PROGRESS -> ProgressScreen(
                // Until the store has been read the overview is the projection's own loading state, never zero.
                view = progress ?: ProgressPresentation.overview(null, health),
                onOpenSkill = { ref ->
                    if (NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.SkillDetail.id)) {
                        app.loadSkillDetail(ref) { facts -> facts?.let { skillDetail = ProgressPresentation.detail(it) } }
                    }
                },
                onOpenHistory = {
                    if (NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.LearningHistory.id)) {
                        app.loadHistory(null) { facts -> history = "Öğrenme geçmişi" to HistoryPresentation.history(facts, health) }
                    }
                },
                onOpenReport = {
                    if (NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.AssessmentReport.id)) {
                        app.loadHistory(null) { facts -> report = HistoryPresentation.report(facts, health) }
                    }
                },
                topics = topics,
                onOpenTopic = { ref ->
                    if (NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.TopicDetail.id)) {
                        app.loadTopicDetail(ref) { facts ->
                            facts?.let {
                                topicDetail = TopicPresentation.detail(it)
                                topicSkills = it.members.map { member -> member.skill }
                                fastPath = null
                                app.loadFastPath(topicSkills) { view -> fastPath = view }
                            }
                        }
                    }
                },
                onOpenEnglish = {
                    if (NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.TechnicalEnglishProfile.id)) {
                        app.loadEnglishProfile { profile -> english = EnglishProfilePresentation.of(profile) }
                    }
                },
            )
            selected == Destination.PROFILE -> ProfileScreen(
                capacity = capacity?.let(SettingsPresentation::capacity),
                onSetLength = { profile, minutes -> app.setProfileLength(profile, minutes, capacityChanged) },
                reminder = reminder?.let { (preference, access) -> ReminderPresentation.settings(preference, access, reminderRefused) },
                onReminderOn = { minute ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && reminder?.second == NotificationAccess.NEEDS_PERMISSION) {
                        minuteAwaitingPermission = minute
                        askNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        app.setReminder(true, minute, reminderChanged)
                    }
                },
                onReminderOff = { reminder?.let { (preference, _) -> app.setReminder(false, preference.minuteOfDay, reminderChanged) } },
                onReminderTime = { minute -> app.setReminder(true, minute, reminderChanged) },
                onOpenNotificationSettings = openNotificationSettings,
                backup = SettingsPresentation.backup(backup),
                onExport = { backup = BackupState.Working; exportTo.launch("coach-yedek.coachdb") },
                onRestore = { backup = BackupState.Working; restoreFrom.launch(arrayOf("*/*")) },
                onConfirmRestore = {
                    backup = BackupState.Working
                    // The reopened store is a different profile: its own preferences are read again.
                    app.confirmRestore { outcome ->
                        backup = outcome
                        app.loadCapacity { capacity = it }
                        app.loadReminder { preference, access -> reminder = preference to access }
                    }
                },
                onCancelRestore = { app.cancelRestore(); backup = BackupState.Idle },
                ai = AiSettingsPresentation.of(aiKey, aiCheck, app.aiSettings.providerName()),
                onSaveKey = { key ->
                    aiCheck = AiCheckState.IDLE
                    app.aiWork({ app.aiSettings.save(key); app.aiSettings.state() }) { aiKey = it }
                },
                onRemoveKey = {
                    aiCheck = AiCheckState.IDLE
                    app.aiWork({ app.aiSettings.remove(); app.aiSettings.state() }) { aiKey = it }
                },
                onCheckKey = {
                    aiCheck = AiCheckState.CHECKING
                    app.aiWork({ app.aiSettings.check() }) { aiCheck = it }
                },
            )
            // 17A: `learn_overview`. `learn -> topic_detail` is an accepted edge (`UXIA-v0`).
            else -> LearnScreen(
                view = learn,
                curriculumMissing = learnMissing,
                onOpenTopic = { ref ->
                    if (NavigationGraph.canOpen(Destination.LEARN.id, Surface.TopicDetail.id)) {
                        app.loadTopicDetail(ref) { facts ->
                            facts?.let {
                                topicDetail = TopicPresentation.detail(it)
                                topicSkills = it.members.map { member -> member.skill }
                                fastPath = null
                                app.loadFastPath(topicSkills) { view -> fastPath = view }
                            }
                        }
                    }
                },
            )
        }
    }
}

// The end of a run, once (16F): what was recorded is processed — recomputed, reported, and replanned only if canonical
// state changed — and Today's facts come back, because the plan may have changed. A run with nothing answered changes
// nothing, and says so. Line comments on purpose: earlier gates strip block comments, and the restore picker's MIME pattern
// above reads to them as the start of one.
private fun finishRun(app: CoachApplication, run: TaskRunSession, onTodayFacts: (TodayFacts) -> Unit, onLines: (List<String>) -> Unit) {
    if (run.finished) return
    run.finished = true
    app.finishRun(run.run, run.before, run.answered.toList(), run.shown.toList(), run.skipped.toSet(), run.contested.toSet()) { lines, facts, _ ->
        onTodayFacts(facts)
        onLines(lines)
    }
}
