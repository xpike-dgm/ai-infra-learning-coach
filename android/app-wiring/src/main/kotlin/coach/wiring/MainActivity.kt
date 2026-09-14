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
import coach.presentation.AppHealth
import coach.presentation.Destination
import coach.presentation.HealthAction
import coach.presentation.ShellState
import coach.presentation.Surface
import coach.presentation.WindowClass
import coach.ui.AppRoot
import coach.ui.AppShell
import coach.ui.CoachTheme
import coach.ui.HealthBlockingSurface
import coach.ui.HealthContextLine

class MainActivity : ComponentActivity() {

    private val storeStatus = mutableStateOf<StoreStatus>(StoreStatus.Opening)
    private val graph = mutableStateOf<AppGraph?>(null)
    private val listener = StoreStartup.Listener<AppGraph> { status, store ->
        storeStatus.value = status
        graph.value = store
    }

    private val startup: StoreStartup<AppGraph>
        get() = (application as CoachApplication).startup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The store is opened by the process on its own thread. The activity only observes, so
        // the first frame never waits for disk work and a failure is a state, not a crash.
        startup.observe(listener)

        setContent {
            // Presentation is decided in core; this only feeds it the facts. Without a store in
            // hand the app is still opening, whatever status arrived first, so the blocking surface
            // always has a state to render.
            val current = graph.value
            val status = if (current == null && storeStatus.value == StoreStatus.Ready) StoreStatus.Opening else storeStatus.value
            val health = AppHealth.of(status, evaluatorAvailability())

            CoachTheme {
                if (!health.showsShell || current == null) {
                    HealthBlockingSurface(
                        health = health,
                        onAction = { action ->
                            when (action) {
                                HealthAction.RECHECK -> startup.recheck()
                            }
                        },
                    )
                } else {
                    Shell(health, current)
                }
            }
        }
    }

    override fun onDestroy() {
        startup.stopObserving(listener)
        super.onDestroy()
    }
}

@Composable
private fun Shell(health: AppHealth, graph: AppGraph) {
    var selected by remember { mutableStateOf(Destination.start) }

    // The window class is computed by core-presentation from the accepted WFPX-v0
    // breakpoints, not by the UI toolkit's own bucketing, so the mapping stays canonical.
    val windowClass = WindowClass.ofWidthDp(LocalConfiguration.current.screenWidthDp)

    val state = ShellState(
        selected = selected,
        surface = Surface.rootOf(selected),
        windowClass = windowClass,
    )

    AppShell(state = state, onSelect = { selected = it }) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Non-blocking context, only ever produced alongside a working core.
            health.contexts.forEach { HealthContextLine(it) }
            AppRoot(statusLine = "${state.surface.id} · study day ${graph.clock.now().studyDay}")
        }
    }
}
