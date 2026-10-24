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
import coach.model.VersionedRef
import coach.presentation.TopicCopy
import coach.presentation.TopicDetailView
import coach.presentation.TopicOverviewView

/**
 * Topics (16C), as `SPWX-v0` §§6, 9 define them. This file renders and decides nothing: every state, count and sentence
 * was settled in `TopicPresentation` in core. A Topic's state is drawn as words beside a chip — never a bar, ring,
 * percentage or score — because it is orchestration, not a measure of mastery.
 */
@Composable
fun TopicsSection(view: TopicOverviewView, onOpenTopic: (VersionedRef) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(TopicCopy.TITLE, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        Text(TopicCopy.INVENTORY_NOTE, style = MaterialTheme.typography.bodySmall)
        view.inventory.forEach { TopicCount(it.label, it.count) }
        if (view.recomputing > 0) TopicCount(TopicCopy.RECOMPUTING, view.recomputing)
        view.topics.forEach { chip ->
            // `progress -> topic_detail` is an accepted edge (`UXIA-v0`).
            TextButton(onClick = { onOpenTopic(chip.topic) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(chip.name, style = MaterialTheme.typography.bodyLarge)
                    StateChip(label = chip.label, tone = chip.tone)
                }
            }
        }
    }
}

@Composable
private fun TopicCount(label: String, count: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(count.toString(), style = MaterialTheme.typography.bodyMedium)
    }
}

/** `topic_detail`: the state and why it holds, the Skills (each opens `skill_detail`) and every Objective's coverage. */
@Composable
fun TopicDetailScreen(
    view: TopicDetailView,
    onOpenSkill: (VersionedRef) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.minimumTouchTarget()) { Text("Geri") }
        Text(view.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        StateChip(label = view.chip.label, tone = view.chip.tone)
        Text(view.text, style = MaterialTheme.typography.bodyMedium)
        view.metadataNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        view.reasons.forEach { reason ->
            Text(reason.title, style = MaterialTheme.typography.bodyLarge)
            // `topic_detail -> skill_detail` is an accepted edge; a named Skill is always reachable.
            reason.skills.forEach { (ref, name) ->
                TextButton(onClick = { onOpenSkill(ref) }, modifier = Modifier.minimumTouchTarget()) { Text(name) }
            }
        }

        HorizontalDivider()
        Text(TopicCopy.SKILLS, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        Text(view.placementNote, style = MaterialTheme.typography.bodySmall)
        view.skills.forEach { chip ->
            TextButton(onClick = { onOpenSkill(chip.skill) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(chip.name, style = MaterialTheme.typography.bodyLarge)
                    StateChip(label = chip.label, tone = chip.tone)
                    chip.qualifiers.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }

        HorizontalDivider()
        Text(TopicCopy.COVERAGE, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        view.coverage.forEach { line ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(line.label, style = MaterialTheme.typography.bodyMedium)
                Text(line.coverage, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
