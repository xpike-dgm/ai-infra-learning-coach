package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coach.model.VersionedRef
import coach.presentation.FastPathView
import coach.presentation.Spacing
import coach.presentation.TopicChip
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
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS.dp)) {
        SectionHeader(TopicCopy.TITLE)
        Text(TopicCopy.INVENTORY_NOTE, style = MaterialTheme.typography.bodySmall)
        view.inventory.forEach { CountRow(it.label, it.count.toString()) }
        if (view.recomputing > 0) CountRow(TopicCopy.RECOMPUTING, view.recomputing.toString())
        // `progress -> topic_detail` is an accepted edge (`UXIA-v0`).
        view.topics.forEach { chip -> TopicLine(chip, onOpenTopic) }
    }
}

/** One Topic that opens `topic_detail`: its name and its state chip — the same line in Progress and in Learn (17A). */
@Composable
fun TopicLine(chip: TopicChip, onOpenTopic: (VersionedRef) -> Unit) {
    NavRow(chip.name, onClick = { onOpenTopic(chip.topic) }) {
        StateChip(label = chip.label, tone = chip.tone, subject = chip.topic)
    }
}

/** `topic_detail`: the state and why it holds, the Skills (each opens `skill_detail`) and every Objective's coverage. */
@Composable
fun TopicDetailScreen(
    view: TopicDetailView,
    onOpenSkill: (VersionedRef) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    // 16H: the fast path on this Topic — offered, open with its result, or a sentence about what just happened.
    fastPath: FastPathView? = null,
    onRequestFastPath: () -> Unit = {},
    onWithdrawFastPath: () -> Unit = {},
) {
    ScreenColumn(modifier = modifier) {
        BackButton(onBack)
        ScreenTitle(view.name)
        StateChip(label = view.chip.label, tone = view.chip.tone, subject = view.topic)
        Text(view.text, style = MaterialTheme.typography.bodyMedium)
        view.metadataNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        view.reasons.forEach { reason ->
            SectionHeader(reason.title)
            // `topic_detail -> skill_detail` is an accepted edge; a named Skill is always reachable.
            reason.skills.forEach { (ref, name) -> NavRow(name, onClick = { onOpenSkill(ref) }) }
        }
        when (fastPath) {
            null -> Unit
            is FastPathView.Offer -> SectionBlock {
                Text(fastPath.text, style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = onRequestFastPath, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(fastPath.action) }
            }
            is FastPathView.Open -> SectionBlock {
                Text(fastPath.headline, style = MaterialTheme.typography.bodyLarge)
                fastPath.lines.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                TextButton(onClick = onWithdrawFastPath, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(fastPath.action) }
            }
            is FastPathView.Said -> Text(fastPath.text, style = MaterialTheme.typography.bodyMedium)
        }

        SectionHeader(TopicCopy.SKILLS)
        Text(view.placementNote, style = MaterialTheme.typography.bodySmall)
        view.skills.forEach { chip ->
            NavRow(chip.name, onClick = { onOpenSkill(chip.skill) }) {
                StateChip(label = chip.label, tone = chip.tone, subject = chip.skill)
                chip.qualifiers.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }

        SectionHeader(TopicCopy.COVERAGE)
        view.coverage.forEach { line ->
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
                Text(line.label, style = MaterialTheme.typography.bodyMedium)
                Text(line.coverage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
