package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coach.presentation.Spacing
import coach.presentation.EnglishProfileCopy
import coach.presentation.EnglishProfileView

/**
 * `technical_english_profile` (16C), as `TEPM-v0` defines it. This file renders and decides nothing: the headline, every
 * band and every Skill's state were settled in `EnglishProfilePresentation` in core. Bands are words, never a meter, and
 * the profile is never a general or official English level.
 *
 * Skills are named, not linked: `UXIA-v0` accepts no `technical_english_profile -> skill_detail` edge, and a surface
 * opens only what an accepted edge allows.
 */
@Composable
fun EnglishProfileScreen(
    view: EnglishProfileView,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenColumn(modifier = modifier) {
        BackButton(onBack)
        ScreenTitle(EnglishProfileCopy.TITLE)
        Text(view.headline, style = MaterialTheme.typography.bodyLarge)
        view.unevenNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        view.historicalNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        Text(view.scopeNote, style = MaterialTheme.typography.bodySmall)
        Text(view.gateNote, style = MaterialTheme.typography.bodySmall)
        view.bands.forEach { band ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(band.band.id, style = MaterialTheme.typography.bodyLarge)
                Text(band.label, style = MaterialTheme.typography.bodyMedium)
            }
            if (band.missing.isNotEmpty()) {
                Text(EnglishProfileCopy.MISSING, style = MaterialTheme.typography.bodySmall)
                band.missing.forEach { (_, name) -> Text(name, style = MaterialTheme.typography.bodyMedium) }
            }
        }

        // The uneven profile is first-class: every Skill's own state, band by band (`TEPM-v0` §8).
        view.groups.forEach { group ->
            SectionHeader(group.label)
            group.skills.forEach { chip ->
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
                    Text(chip.name, style = MaterialTheme.typography.bodyLarge)
                    StateChip(label = chip.label, tone = chip.tone, subject = chip.skill)
                    group.sources[chip.skill]?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    chip.qualifiers.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }

        if (view.extensions.isNotEmpty()) {
            SectionHeader(EnglishProfileCopy.EXTENSION_TITLE)
            view.extensions.forEach { (name, state) ->
                Text(name, style = MaterialTheme.typography.bodyMedium)
                Text(state, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
