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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coach.presentation.Spacing
import coach.model.CapacityProfile
import coach.model.TodayCapacityChoice
import coach.presentation.AiSettingsView
import coach.presentation.AiUsageView
import coach.presentation.AppearanceCopy
import coach.presentation.AppearanceView
import coach.model.ThemeChoice
import coach.presentation.BackupView
import coach.presentation.CapacitySettingsView
import coach.presentation.ReminderCopy
import coach.presentation.ReminderPresentation
import coach.presentation.ReminderSettingsView
import coach.presentation.SettingsCopy
import coach.presentation.SettingsPresentation
import coach.presentation.TodayCapacityOption

/**
 * Profile (16D): how this personal app operates for the learner — the daily time, the reminder (16E), backups and the AI
 * key (`UXIA-v0`). This file renders and decides nothing: every line was settled in `SettingsPresentation`,
 * `ReminderPresentation` and `AiSettingsPresentation` in core. Minutes are typed and shown as numbers; there is no slider, bar or meter that could read as a target.
 */
@Composable
fun ProfileScreen(
    capacity: CapacitySettingsView?,
    onSetLength: (CapacityProfile, Int) -> Unit,
    reminder: ReminderSettingsView?,
    onReminderOn: (Int) -> Unit,
    onReminderOff: () -> Unit,
    onReminderTime: (Int) -> Unit,
    onOpenNotificationSettings: () -> Unit,
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
    // 17E: the theme and motion setting, and the AI calls counted (no money).
    appearance: AppearanceView? = null,
    onAppearance: (ThemeChoice, Boolean) -> Unit = { _, _ -> },
    aiUsage: AiUsageView? = null,
) {
    ScreenColumn(modifier = modifier) {
        ScreenTitle("Profil")
        capacity?.let { CapacitySection(it, onSetLength) }

        appearance?.let { AppearanceSection(it, onAppearance) }

        reminder?.let {
            ReminderSection(it, onReminderOn, onReminderOff, onReminderTime, onOpenNotificationSettings)
        }

        SectionHeader(backup.title)
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
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XS.dp)) {
                OutlinedButton(onClick = onConfirmRestore, modifier = Modifier.minimumTouchTarget()) { Text(SettingsCopy.RESTORE_CONFIRM_ACTION) }
                TextButton(onClick = onCancelRestore, modifier = Modifier.minimumTouchTarget()) { Text(SettingsCopy.CANCEL) }
            }
        }
        AiSettingsScreen(view = ai, onSave = onSaveKey, onRemove = onRemoveKey, onCheck = onCheckKey, embedded = true)
        aiUsage?.let { AiUsageSection(it) }
    }
}

/**
 * 17E: how the app looks and moves. The theme is one choice of three, announced as one group; the motion switch is a row
 * whose whole width toggles it. Neither replans anything.
 */
@Composable
private fun AppearanceSection(view: AppearanceView, onChange: (ThemeChoice, Boolean) -> Unit) {
    SectionHeader(AppearanceCopy.TITLE)
    Text(AppearanceCopy.THEME, style = MaterialTheme.typography.bodyLarge)
    Column(modifier = Modifier.selectableGroup()) {
        view.themeOptions.forEach { (choice, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().minimumTouchTarget()
                    .selectable(selected = view.theme == choice, onClick = { onChange(choice, view.reduceMotion) }, role = Role.RadioButton),
            ) {
                RadioButton(selected = view.theme == choice, onClick = null)
                Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = Spacing.S.dp))
            }
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().minimumTouchTarget()
            .toggleable(value = view.reduceMotion, onValueChange = { onChange(view.theme, it) }, role = Role.Switch),
    ) {
        Text(AppearanceCopy.MOTION, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = view.reduceMotion, onCheckedChange = null)
    }
    Text(view.motionNote, style = MaterialTheme.typography.bodySmall, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
}

/** 17E: the AI calls the app made, counted per use; tokens as the provider reported them, and no money. */
@Composable
private fun AiUsageSection(view: AiUsageView) {
    SectionHeader(view.title)
    view.text?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
    view.periods.forEach { period ->
        Text(period.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        period.lines.forEach { line ->
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
                Text(line.label, style = MaterialTheme.typography.bodyLarge)
                Text(line.calls, style = MaterialTheme.typography.bodyMedium)
                line.tokens?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
    view.notes.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
}

@Composable
private fun CapacitySection(view: CapacitySettingsView, onSetLength: (CapacityProfile, Int) -> Unit) {
    SectionHeader(view.title)
    Text(view.note, style = MaterialTheme.typography.bodySmall)
    view.lengths.forEach { line ->
        var typed by remember(line.minutes) { mutableStateOf(line.minutes.toString()) }
        val parsed = SettingsPresentation.parseMinutes(typed)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
            Text("${line.label}: ${line.value} · ${line.source}", style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XS.dp), verticalAlignment = Alignment.CenterVertically) {
                // 17D: the error belongs to its field (its supporting text), not loose text below it; in the body colour, because a
                // mistyped number is not a fault (17A, `VDSX-v0`).
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it.filter(Char::isDigit).take(4) },
                    label = { Text(SettingsCopy.lengthField(line.label)) },
                    singleLine = true,
                    supportingText = if (parsed == null) { { Text(SettingsCopy.MINUTES_INVALID) } } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(
                    onClick = { parsed?.let { onSetLength(line.profile, it) } },
                    enabled = parsed != null && parsed != line.minutes,
                    modifier = Modifier.minimumTouchTarget().semantics { contentDescription = SettingsCopy.saveLength(line.label) },
                ) { Text("Kaydet") }
            }
        }
    }
    Text(view.defaultNote, style = MaterialTheme.typography.bodySmall)
    Text(view.today, style = MaterialTheme.typography.bodyMedium)
}

/**
 * The reminder (16E): on or off, and the time typed as `SS:DD`. A switch-like pair of buttons and a text field; there is
 * no count, calendar or series anywhere to draw.
 */
@Composable
private fun ReminderSection(
    view: ReminderSettingsView,
    onOn: (Int) -> Unit,
    onOff: () -> Unit,
    onTime: (Int) -> Unit,
    onOpenSystemSettings: () -> Unit,
) {
    SectionHeader(view.title)
    Text(view.note, style = MaterialTheme.typography.bodySmall)
    Text(view.status, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
    var typed by remember(view.time) { mutableStateOf(view.time) }
    val parsed = ReminderPresentation.parseTime(typed)
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XS.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = typed,
            onValueChange = { typed = it.filter { c -> c.isDigit() || c == ':' }.take(5) },
            label = { Text(ReminderCopy.TIME_LABEL) },
            singleLine = true,
            supportingText = if (parsed == null) { { Text(ReminderCopy.TIME_INVALID) } } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
        if (view.enabled) {
            OutlinedButton(
                onClick = { parsed?.let(onTime) },
                enabled = parsed != null && typed.trim() != view.time,
                modifier = Modifier.minimumTouchTarget(),
            ) { Text(ReminderCopy.SAVE_TIME) }
        }
    }
    if (view.enabled) {
        OutlinedButton(onClick = onOff, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(ReminderCopy.TURN_OFF) }
    } else {
        OutlinedButton(onClick = { parsed?.let(onOn) }, enabled = parsed != null, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text(ReminderCopy.TURN_ON)
        }
    }
    if (view.offersSystemSettings) {
        TextButton(onClick = onOpenSystemSettings, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(ReminderCopy.OPEN_SYSTEM_SETTINGS) }
    }
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
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS.dp)) {
                Text(SettingsCopy.TODAY_DIALOG_NOTE, style = MaterialTheme.typography.bodySmall)
                options.forEach { option ->
                    TextButton(onClick = { onChoose(option.choice) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(option.label) }
                }
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it.filter(Char::isDigit).take(4) },
                    label = { Text(SettingsCopy.TODAY_CUSTOM) },
                    singleLine = true,
                    supportingText = if (typed.isNotEmpty() && parsed == null) { { Text(SettingsCopy.MINUTES_INVALID) } } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { parsed?.let { onChoose(TodayCapacityChoice.Minutes(it)) } }, enabled = parsed != null,
                modifier = Modifier.minimumTouchTarget()) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss, modifier = Modifier.minimumTouchTarget()) { Text(SettingsCopy.CANCEL) } },
    )
}
