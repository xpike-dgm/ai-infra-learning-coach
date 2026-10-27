package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coach.presentation.SettingsCopy
import coach.presentation.SettingsPresentation
import coach.presentation.SetupCopy
import coach.presentation.SetupView
import coach.presentation.Spacing

/**
 * The first-run setup (18A, `D-139`; `UXIA-v0` §7.4): pre-shell and shown once. It renders [SetupView] and decides
 * nothing — the length is checked by `SettingsPresentation.parseMinutes`, the areas' Skills by `SetupPresentation`.
 *
 * Skipping is the safe exit and is offered first, before anything is asked; nothing is ticked in advance, because an
 * area the learner did not choose would be a claim they did not make.
 */
@Composable
fun SetupScreen(
    view: SetupView,
    onFinish: (normalMinutes: Int, ticked: Set<String>) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Kept across a rotation or a theme change (review, 18A): the learner's typing and ticks are not lost.
    var typed by rememberSaveable(view.normalMinutes) { mutableStateOf(view.normalMinutes.toString()) }
    var ticked by rememberSaveable(stateSaver = TICKED_SAVER) { mutableStateOf(emptySet<String>()) }
    val minutes = SettingsPresentation.parseMinutes(typed)
    ScreenColumn(modifier = modifier) {
        TextButton(onClick = onSkip, modifier = Modifier.minimumTouchTarget()) { Text(SetupCopy.SKIP) }
        // What skipping does, beside the skip itself (review, 18A), so it is read before anything is asked.
        Text(SetupCopy.SKIP_NOTE, style = MaterialTheme.typography.bodySmall)
        ScreenTitle(SetupCopy.TITLE)
        Text(SetupCopy.INTRO, style = MaterialTheme.typography.bodyLarge)
        Text(SetupCopy.PRINCIPLE, style = MaterialTheme.typography.bodyMedium)

        SectionHeader(SetupCopy.LENGTH_TITLE)
        // The error is the field's own supporting text, in the body colour: a mistyped number is not a fault (17A, 17D).
        OutlinedTextField(
            value = typed,
            onValueChange = { typed = it },
            label = { Text(SetupCopy.LENGTH_LABEL) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = if (minutes == null) { { Text(SettingsCopy.MINUTES_INVALID) } } else null,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(SetupCopy.LENGTH_NOTE, style = MaterialTheme.typography.bodySmall)

        SectionHeader(SetupCopy.KNOWN_TITLE)
        Text(SetupCopy.KNOWN_NOTE, style = MaterialTheme.typography.bodyMedium)
        Text(SetupCopy.KNOWN_OPTIONAL, style = MaterialTheme.typography.bodySmall)
        Column {
            view.areas.forEach { area ->
                val checked = area.id in ticked
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().minimumTouchTarget()
                        .toggleable(value = checked, onValueChange = { on -> ticked = if (on) ticked + area.id else ticked - area.id }, role = Role.Checkbox),
                ) {
                    Checkbox(checked = checked, onCheckedChange = null)
                    Column(modifier = Modifier.padding(start = Spacing.S.dp), verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
                        Text(area.label, style = MaterialTheme.typography.bodyLarge)
                        Text(SetupCopy.topics(area.topicCount), style = MaterialTheme.typography.bodySmall)
                        area.note?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }

        Button(
            onClick = { minutes?.let { onFinish(it, ticked) } },
            enabled = minutes != null,
            modifier = Modifier.fillMaxWidth().minimumTouchTarget(),
        ) { Text(SetupCopy.START) }
    }
}

/** The ticked areas, saved as a list (a `Set` is not a type the saved state holds). */
private val TICKED_SAVER = listSaver<Set<String>, String>(save = { it.toList() }, restore = { it.toSet() })
