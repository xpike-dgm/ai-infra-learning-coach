package coach.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import coach.presentation.Destination
import coach.presentation.ShellState

/**
 * The shell renders what `core-presentation` decided. It does not choose destinations, their
 * order, or whether a focused flow hides the bar — those are pure functions in the core, which is
 * what keeps the most safety-critical part of navigation testable off a device (`MSBX-v0`).
 *
 * Turkish labels are working microcopy owned by 14; the canonical thing here is the destination
 * **id** and its order, which come from `UXIA-v0`.
 */

private val labelsTr: Map<Destination, String> = mapOf(
    Destination.TODAY to "Bugün",
    Destination.LEARN to "Öğren",
    Destination.PROGRESS to "İlerleme",
    Destination.PROFILE to "Profil",
)

private val icons: Map<Destination, ImageVector> = mapOf(
    Destination.TODAY to Icons.Filled.DateRange,
    Destination.LEARN to Icons.Filled.Menu,
    Destination.PROGRESS to Icons.Filled.Star,
    Destination.PROFILE to Icons.Filled.Person,
)

@Composable
fun AppShell(
    state: ShellState,
    onSelect: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!state.showsShell) {
        // A focused flow suspends the shell. Its own safe exit belongs to the flow chrome
        // (`TRUX-v0`), so the shell must not draw navigation over it.
        Box(modifier.fillMaxSize()) { content() }
        return
    }

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            // Iterating the enum is deliberate: the order cannot drift from the accepted one
            // because there is no second list to keep in sync.
            Destination.entries.forEach { destination ->
                val label = labelsTr.getValue(destination)
                item(
                    selected = destination == state.selected,
                    onClick = { onSelect(destination) },
                    icon = {
                        // An icon is never the only carrier of meaning (`UXIA-v0` §17).
                        Icon(imageVector = icons.getValue(destination), contentDescription = null)
                    },
                    label = { Text(label) },
                    modifier = Modifier.semantics {
                        traversalIndex = destination.order.toFloat()
                        stateDescription = if (destination == state.selected) "seçili" else "seçili değil"
                    },
                )
            }
        },
        content = content,
    )
}
