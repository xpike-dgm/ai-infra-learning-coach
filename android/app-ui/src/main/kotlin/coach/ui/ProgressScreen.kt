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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coach.model.VersionedRef
import coach.presentation.ProgressCopy
import coach.presentation.ProgressOverview
import coach.presentation.SkillChip
import coach.presentation.SkillDetailView

/**
 * Progress, as `SPWX-v0` defined it and `WFPX-v0` placed it (16A). This file renders and decides nothing: every state,
 * count and label was settled in `ProgressPresentation` in core (`MSBX-v0` §presentation).
 *
 * Counts are drawn as numbers beside their label and nothing else — no bar, ring, gauge or percentage — because an
 * inventory of current state is not a measure of competence (`SPWX-v0` §14).
 */
@Composable
fun ProgressScreen(
    view: ProgressOverview,
    onOpenSkill: (VersionedRef) -> Unit,
    modifier: Modifier = Modifier,
    // 16B: `progress -> learning_history` and `progress -> assessment_report` are accepted edges (`UXIA-v0`).
    onOpenHistory: (() -> Unit)? = null,
    onOpenReport: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // The state is always said in words and announced when it changes (`SPWX-v0` §17).
        StateChip(
            label = ProgressCopy.stateTexts.getValue(view.state),
            tone = view.state.tone,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )
        if (view.text != ProgressCopy.stateTexts[view.state]) {
            Text(view.text, style = MaterialTheme.typography.bodyLarge)
        }
        view.contexts.forEach { Text(ProgressCopy.stateTexts.getValue(it), style = MaterialTheme.typography.bodySmall) }
        onOpenHistory?.let { open -> TextButton(onClick = open, modifier = Modifier.minimumTouchTarget()) { Text("Öğrenme geçmişi") } }
        onOpenReport?.let { open -> TextButton(onClick = open, modifier = Modifier.minimumTouchTarget()) { Text("Ölçme raporu") } }

        // 1. demonstrated_capability_inventory
        if (view.inventory.isNotEmpty() || view.recomputing > 0) {
            Text(ProgressCopy.INVENTORY_TITLE, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
            Text(ProgressCopy.INVENTORY_NOTE, style = MaterialTheme.typography.bodySmall)
            view.inventory.forEach { CountLine(it.label, it.count) }
            if (view.recomputing > 0) CountLine(ProgressCopy.RECOMPUTING, view.recomputing)
        }

        // 2. attention_set, grouped by reason
        if (view.inventory.isNotEmpty()) {
            HorizontalDivider()
            Text(ProgressCopy.ATTENTION_TITLE, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
            if (view.attention.isEmpty()) Text(ProgressCopy.ATTENTION_EMPTY, style = MaterialTheme.typography.bodyMedium)
            view.attention.forEach { CountLine(it.label, it.count) }
        }

        // The Skills themselves, grouped for orientation; each opens the one shared skill_detail.
        view.groups.forEach { group ->
            HorizontalDivider()
            Text(group.label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
            group.skills.forEach { SkillLine(it, onOpenSkill) }
        }
    }
}

@Composable
private fun CountLine(label: String, count: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(count.toString(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SkillLine(chip: SkillChip, onOpenSkill: (VersionedRef) -> Unit) {
    TextButton(
        onClick = { onOpenSkill(chip.skill) },
        modifier = Modifier.fillMaxWidth().minimumTouchTarget(),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(chip.name, style = MaterialTheme.typography.bodyLarge)
            StateChip(label = chip.label, tone = chip.tone)
            // Qualifiers are text beside the state, never a replacement for it (`SPWX-v0` §5.1).
            chip.qualifiers.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

/** `skill_detail`: the chip **and** all four axes, the Objectives, the evidence by kind and the prerequisite context. */
@Composable
fun SkillDetailScreen(
    view: SkillDetailView,
    onOpenSkill: (VersionedRef) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    // 16B: `skill_detail -> learning_history` (`UXIA-v0`), the evidence history entry 16A deferred.
    onOpenHistory: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.minimumTouchTarget()) { Text("Geri") }
        Text(view.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        Text(view.statement, style = MaterialTheme.typography.bodyMedium)
        StateChip(label = view.chip.label, tone = view.chip.tone)
        view.chip.qualifiers.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        onOpenHistory?.let { open -> TextButton(onClick = open, modifier = Modifier.minimumTouchTarget()) { Text("Bu becerinin geçmişi") } }

        HorizontalDivider()
        view.axes.forEach { axis -> CountLineText(axis.title, axis.value) }

        HorizontalDivider()
        Text("Objective kırılımı", style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        view.objectives.forEach { objective ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                val required = if (objective.required) " · ${ProgressCopy.OBJECTIVE_REQUIRED}" else ""
                Text(objective.label + required, style = MaterialTheme.typography.bodyLarge)
                Text(objective.shown, style = MaterialTheme.typography.bodyMedium)
                objective.weakness?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                Text(objective.evidence.filter { it.count > 0 }.joinToString(" · ") { "${it.label}: ${it.count}" }
                    .ifEmpty { "Kanıt yok" }, style = MaterialTheme.typography.bodySmall)
            }
        }

        HorizontalDivider()
        Text("Kanıt özeti", style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        view.evidence.forEach { CountLine(it.label, it.count) }

        HorizontalDivider()
        Text("Ön koşul bağlamı", style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        view.prerequisiteNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        view.prerequisite.forEach { line ->
            Text(line.title, style = MaterialTheme.typography.bodyMedium)
            // A blocker is named and linked to its own skill_detail (`SPWX-v0` §8).
            line.skills.forEach { (ref, name) ->
                TextButton(onClick = { onOpenSkill(ref) }, modifier = Modifier.minimumTouchTarget()) { Text(name) }
            }
        }

        if (view.topics.isNotEmpty()) {
            HorizontalDivider()
            Text("Yer aldığı konular", style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
            view.topics.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun CountLineText(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
