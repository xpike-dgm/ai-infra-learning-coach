package coach.wiring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import coach.presentation.Destination
import coach.presentation.ShellState
import coach.presentation.Surface
import coach.presentation.WindowClass
import coach.ui.AppRoot
import coach.ui.AppShell
import coach.ui.CoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Opened once per process. 10E owns moving this off the main thread and surfacing
        // data_recovery_required as a real state rather than a crash.
        val graph = AppGraph.open(getDatabasePath(AppGraph.DATABASE_NAME).absolutePath)
        setContent {
            var selected by remember { mutableStateOf(Destination.start) }

            // The window class is computed by core-presentation from the accepted WFPX-v0
            // breakpoints, not by the UI toolkit's own bucketing, so the mapping stays canonical.
            val windowClass = WindowClass.ofWidthDp(LocalConfiguration.current.screenWidthDp)

            val state = ShellState(
                selected = selected,
                surface = Surface.rootOf(selected),
                windowClass = windowClass,
            )

            CoachTheme {
                AppShell(state = state, onSelect = { selected = it }) {
                    AppRoot(statusLine = "${state.surface.id} · study day ${graph.clock.now().studyDay}")
                }
            }
        }
    }
}
