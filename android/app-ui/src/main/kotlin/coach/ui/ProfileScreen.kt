package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coach.model.CapacityProfile
import coach.model.TodayCapacityChoice
import coach.presentation.AiSettingsView
import coach.presentation.BackupView
import coach.presentation.CapacitySettingsView
import coach.presentation.SettingsCopy
import coach.presentation.SettingsPresentation
import coach.presentation.TodayCapacityOption

/**
 * Profile (16D): how this personal app operates for the learner — the daily time, backups and the AI key (`UXIA-v0`).
 * This file renders and decides nothing: every line was settled in `SettingsPresentation` and `AiSettingsPresentation`
 * in core. Minutes are typed and shown as numbers; there is no slider, bar or meter that could read as a target.
 */
@Composable
fun ProfileScreen(
    capacity: CapacitySettingsView?,
    onSetLength: (CapacityProfile, Int) -> Unit,
    backup: BackupView,
    onExport: () -> Unit,
    onRestore: () -> Unit,
    onConfirmRestore: () -> Unit,
    onCancelRestore: () -> Unit,
    ai: AiSettingsView,
    onSaveKey: (String) -> Unit,
    onRemoveKey: () -> Unit,
    onCheckKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        capacity?.let { CapacitySection(it, onSetLength) }

        HorizontalDivider()
        Text(backup.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        Text(backup.note, style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = onExport, enabled = !backup.busy, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text(SettingsCopy.EXPORT_ACTION)
        }
        OutlinedButton(onClick = onRestore, enabled = !backup.busy, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text(SettingsCopy.RESTORE_ACTION)
        }
        backup.status?.let { Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
        if (backup.confirming) {
            // Replacing a profile is explicit (`LFPS-v0` §11.2): nothing happens until the learner says so here.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onConfirmRestore, modifier = Modifier.minimumTouchTarget()) { Text(SettingsCopy.RESTORE_CONFIRM_ACTION) }
                TextButton(onClick = onCancelRestore, modifier = Modifier.minimumTouchTarget()) { Text(SettingsCopy.CANCEL) }
            }
        }

        HorizontalDivider()
        AiSettingsScreen(view = ai, onSave = onSaveKey, onRemove = onRemoveKey, onCheck = onCheckKey, embedded = true)
    }
}

@Composable
private fun CapacitySection(view: CapacitySettingsView, onSetLength: (CapacityProfile, Int) -> Unit) {
    Text(view.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
    Text(view.note, style = MaterialTheme.typography.bodySmall)
    view.lengths.forEach { line ->
        var typed by remember(line.minutes) { mutableStateOf(line.minutes.toString()) }
        val parsed = SettingsPresentation.parseMinutes(typed)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${line.label}: ${line.value} · ${line.source}", style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it.filter(Char::isDigit).take(4) },
                    label = { Text("dk") },
                    singleLine = true,
                    isError = parsed == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(
                    onClick = { parsed?.let { onSetLength(line.profile, it) } },
                    enabled = parsed != null && parsed != line.minutes,
                    modifier = Modifier.minimumTouchTarget(),
                ) { Text("Kaydet") }
            }
            if (parsed == null) Text(SettingsCopy.MINUTES_INVALID, style = MaterialTheme.typography.bodySmall)
        }
    }
    Text(view.defaultNote, style = MaterialTheme.typography.bodySmall)
    Text(view.today, style = MaterialTheme.typography.bodyMedium)
}

/** Today's capacity dialog (`THUX-v0` `change_today_capacity`): a profile, a number of minutes, or no choice. */
@Composable
fun TodayCapacityDialog(
    options: List<TodayCapacityOption>,
    onChoose: (TodayCapacityChoice) -> Unit,
    onDismiss: () -> Unit,
) {
    var typed by remember { mutableStateOf("") }
    val parsed = SettingsPresentation.parseMinutes(typed)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(SettingsCopy.TODAY_DIALOG_TITLE) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(SettingsCopy.TODAY_DIALOG_NOTE, style = MaterialTheme.typography.bodySmall)
                options.forEach { option ->
                    TextButton(onClick = { onChoose(option.choice) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(option.label) }
                }
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it.filter(Char::isDigit).take(4) },
                    label = { Text(SettingsCopy.TODAY_CUSTOM) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (typed.isNotEmpty() && parsed == null) Text(SettingsCopy.MINUTES_INVALID, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = { parsed?.let { onChoose(TodayCapacityChoice.Minutes(it)) } }, enabled = parsed != null,
                modifier = Modifier.minimumTouchTarget()) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss, modifier = Modifier.minimumTouchTarget()) { Text(SettingsCopy.CANCEL) } },
    )
}
