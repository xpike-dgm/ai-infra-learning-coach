package coach.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
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
import coach.presentation.MotionPurpose
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
    // 17A: Learn is the learning map, and Progress an inventory. A star read as a rating or a reward, which
    // `VDSX-v0` §11.1 excludes from every surface, and a menu glyph named no destination at all.
    Destination.LEARN to Icons.Filled.LocationOn,
    Destination.PROGRESS to Icons.AutoMirrored.Filled.List,
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
        // 17B: a focused flow is never faded in or out. Its exit is the first thing in it and must be visible and act
        // from the first frame (`TRUX-v0`, `VDSX-v0` §10.3).
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
                    // 17E: the destinations are not 48dp-modifier targets, so they draw the keyboard focus ring themselves.
                    modifier = Modifier.focusRing().semantics {
                        traversalIndex = destination.order.toFloat()
                        stateDescription = if (destination == state.selected) "seçili" else "seçili değil"
                    },
                )
            }
        },
        // 17B: a destination or a detail arriving is orientation.
        content = { ArriveOnChange(state.surface.id, MotionPurpose.ORIENTATION) { content() } },
    )
}
