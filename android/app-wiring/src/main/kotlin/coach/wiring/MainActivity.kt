package coach.wiring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coach.application.StoreStartup
import coach.model.StoreStatus
import coach.model.TodayFacts
import coach.presentation.AppHealth
import coach.presentation.Destination
import coach.presentation.HealthAction
import coach.presentation.Revalidation
import coach.presentation.RunnerFlow
import coach.presentation.RunnerRevalidation
import coach.presentation.SessionEntrySource
import coach.presentation.SessionEvent
import coach.presentation.WorkingSession
import coach.presentation.WorkingSessions
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
import coach.ui.TaskRunnerScreen
import coach.ui.TodayScreen

class MainActivity : ComponentActivity() {

    private val storeStatus = mutableStateOf<StoreStatus>(StoreStatus.Opening)
    private val opened = mutableStateOf<OpenedApp?>(null)
    private val todayFacts = mutableStateOf<TodayFacts?>(null)
    private val listener = StoreStartup.Listener<OpenedApp> { status, store ->
        storeStatus.value = status
        opened.value = store
        todayFacts.value = store?.today
    }

    private val app: CoachApplication get() = application as CoachApplication
    private val startup: StoreStartup<OpenedApp> get() = app.startup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The store is opened by the process on its own thread. The activity only observes, so
        // the first frame never waits for disk work and a failure is a state, not a crash.
        startup.observe(listener)

        setContent {
            // Presentation is decided in core; this only feeds it the facts. Without a store in
            // hand the app is still opening, whatever status arrived first, so the blocking surface
            // always has a state to render.
            val current = opened.value
            val facts = todayFacts.value
            val status = if (current == null && storeStatus.value == StoreStatus.Ready) StoreStatus.Opening else storeStatus.value
            val health = AppHealth.of(status, evaluatorAvailability())

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
                    Shell(health, current, facts)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // A process kept open past midnight would otherwise keep yesterday's study day.
        app.refreshToday { todayFacts.value = it }
    }

    override fun onDestroy() {
        startup.stopObserving(listener)
        super.onDestroy()
    }
}

@Composable
private fun Shell(health: AppHealth, opened: OpenedApp, facts: TodayFacts) {
    var selected by remember { mutableStateOf(Destination.start) }
    // The focused flow in front of the shell, if any. Its entry decision is made in core.
    var runnerEntry by remember { mutableStateOf<Revalidation?>(null) }
    // The emergent working session (11C). It is not stored and lives no longer than the focused
    // flow's own state; it starts only when a run really starts, so while nothing is startable it
    // never starts at all.
    var session by remember { mutableStateOf<WorkingSession?>(null) }
    var sessionsStarted by remember { mutableStateOf(0L) }

    // The window class is computed by core-presentation from the accepted WFPX-v0
    // breakpoints, not by the UI toolkit's own bucketing, so the mapping stays canonical.
    val windowClass = WindowClass.ofWidthDp(LocalConfiguration.current.screenWidthDp)
    val today = TodayPresentation.of(todayInput(facts, health))

    val state = ShellState(
        selected = selected,
        // A focused flow suspends the shell; NSHX-v0 derives that from the surface itself.
        surface = if (runnerEntry != null) RunnerFlow.surface else Surface.rootOf(selected),
        windowClass = windowClass,
    )

    AppShell(state = state, onSelect = { selected = it }) {
        val entry = runnerEntry
        when {
            entry != null -> TaskRunnerScreen(
                state = RunnerRevalidation.stateAtEntry(entry),
                entry = entry,
                // Back to Today in one action; NavigationGraph's return rule would also land here,
                // because a run started from Today returns to Today.
                onExit = {
                    session = session?.let { WorkingSessions.on(it, SessionEvent.LearnerExited) }
                    runnerEntry = null
                    selected = Destination.TODAY
                },
            )
            selected == Destination.TODAY -> TodayScreen(
                // Today's own projection: which state applies and which task may be offered is
                // decided in core, never here (MSBX-v0).
                view = today,
                // Entry is revalidated in core against what can actually be confirmed; nothing
                // unconfirmed is assumed to hold (TRUX-v0 entry_revalidation).
                onStart = {
                    val entry = RunnerRevalidation.atEntry(RunnerRevalidation.confirmedFromToday(today))
                    runnerEntry = entry
                    val task = today.primaryTask
                    if (task != null) {
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
                },
                onOpen = {},
            )
            else -> Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Non-blocking context, only ever produced alongside a working core.
                health.contexts.forEach { HealthContextLine(it) }
                AppRoot(statusLine = "${state.surface.id} · study day ${opened.graph.clock.now().studyDay}")
            }
        }
    }
}
