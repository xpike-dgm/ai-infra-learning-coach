package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coach.presentation.AiSettingsCopy
import coach.presentation.AiSettingsView

/**
 * The AI settings section on Profile (14G). It renders [AiSettingsView] and nothing more: what is said is decided in
 * core-presentation. The key field is masked and is cleared as soon as the key is saved, so the key is never shown —
 * not even back to the learner who typed it.
 */
@Composable
fun AiSettingsScreen(
    view: AiSettingsView,
    onSave: (String) -> Unit,
    onRemove: () -> Unit,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var typed by remember { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = view.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        view.lines.forEach { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
        Text(text = view.status, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        if (view.showKeyField) {
            OutlinedTextField(
                value = typed,
                onValueChange = { typed = it },
                label = { Text(view.keyFieldLabel ?: AiSettingsCopy.KEY_FIELD_GENERIC) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedButton(
                onClick = { onSave(typed.trim()); typed = "" },
                enabled = typed.isNotBlank(),
                modifier = Modifier.fillMaxWidth().minimumTouchTarget(),
            ) { Text(AiSettingsCopy.SAVE) }
        }
        if (view.canCheck) {
            OutlinedButton(onClick = onCheck, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(AiSettingsCopy.CHECK) }
        }
        view.checkLine?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
        if (view.canRemove) {
            OutlinedButton(onClick = onRemove, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(AiSettingsCopy.REMOVE) }
        }
    }
}
