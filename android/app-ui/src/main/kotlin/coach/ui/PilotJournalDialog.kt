package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coach.model.PilotJournal
import coach.model.PilotJournalKind
import coach.presentation.PilotJournalCopy
import coach.presentation.PilotJournalPresentation
import coach.presentation.Spacing

/**
 * The pilot journal's dialog (18G, `D-141`): one kind, chosen as a radio row, and an optional short note. It decides
 * nothing: what may be saved is `PilotJournalPresentation`'s, and saving changes no plan.
 */
@Composable
fun PilotJournalDialog(
    onSave: (PilotJournalKind, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var chosen by rememberSaveable { mutableStateOf<String?>(null) }
    var typed by rememberSaveable { mutableStateOf("") }
    val kind = chosen?.let(PilotJournalKind::of)
    val fits = PilotJournalPresentation.note(typed) != null
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(PilotJournalCopy.TITLE) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.XS.dp),
            ) {
                Text(PilotJournalCopy.NOTE, style = MaterialTheme.typography.bodySmall)
                Column(Modifier.selectableGroup()) {
                    PilotJournalPresentation.kinds.forEach { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth().minimumTouchTarget()
                                .selectable(selected = kind == option, onClick = { chosen = option.id }, role = Role.RadioButton),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.XS.dp),
                        ) {
                            // The row carries the role and the state; the button only draws them.
                            RadioButton(selected = kind == option, onClick = null)
                            Text(PilotJournalCopy.kindLabels.getValue(option), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                if (kind == PilotJournalKind.PERSONAL_PREFERENCE) {
                    Text(PilotJournalCopy.PREFERENCE_NOTE, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it },
                    label = { Text(PilotJournalCopy.NOTE_LABEL) },
                    // 17D: the field's own supporting text says what is wrong; a long note is not a fault, so no error colour.
                    supportingText = {
                        Text(
                            if (fits) PilotJournalCopy.noteCount(typed.trim().length, PilotJournal.NOTE_LIMIT)
                            else PilotJournalCopy.noteTooLong(PilotJournal.NOTE_LIMIT),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { kind?.let { onSave(it, typed) } },
                enabled = PilotJournalPresentation.canSave(kind, typed),
                modifier = Modifier.minimumTouchTarget(),
            ) { Text(PilotJournalCopy.SAVE) }
        },
        dismissButton = { TextButton(onClick = onDismiss, modifier = Modifier.minimumTouchTarget()) { Text(PilotJournalCopy.CANCEL) } },
    )
}
