package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import coach.model.VersionedRef
import coach.presentation.ProgressCopy
import coach.presentation.ProgressOverview
import coach.presentation.SkillChip
import coach.presentation.SkillDetailView
import coach.presentation.SkillPlanCopy
import coach.presentation.Spacing
import androidx.compose.ui.unit.dp
import coach.presentation.TopicOverviewView

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
    // 16C: `progress -> topic_detail` and `progress -> technical_english_profile` are accepted edges (`UXIA-v0`).
    topics: TopicOverviewView? = null,
    onOpenTopic: ((VersionedRef) -> Unit)? = null,
    onOpenEnglish: (() -> Unit)? = null,
) {
    ScreenColumn(modifier = modifier) {
        ScreenTitle("İlerleme")
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
        onOpenHistory?.let { open -> NavRow("Öğrenme geçmişi", open) }
        onOpenReport?.let { open -> NavRow("Ölçme raporu", open) }
        onOpenEnglish?.let { open -> NavRow("Teknik İngilizce profili", open) }

        // 1. demonstrated_capability_inventory
        if (view.inventory.isNotEmpty() || view.recomputing > 0) {
            SectionHeader(ProgressCopy.INVENTORY_TITLE)
            Text(ProgressCopy.INVENTORY_NOTE, style = MaterialTheme.typography.bodySmall)
            view.inventory.forEach { CountLine(it.label, it.count) }
            if (view.recomputing > 0) CountLine(ProgressCopy.RECOMPUTING, view.recomputing)
        }

        // 2. attention_set, grouped by reason
        if (view.inventory.isNotEmpty()) {
            SectionHeader(ProgressCopy.ATTENTION_TITLE)
            if (view.attention.isEmpty()) Text(ProgressCopy.ATTENTION_EMPTY, style = MaterialTheme.typography.bodyMedium)
            view.attention.forEach { CountLine(it.label, it.count) }
        }

        // 16C: the Topics, each a state in words that opens topic_detail.
        if (topics != null && onOpenTopic != null && topics.topics.isNotEmpty()) {
            TopicsSection(topics, onOpenTopic)
        }

        // The Skills themselves, grouped for orientation; each opens the one shared skill_detail.
        view.groups.forEach { group ->
            SectionHeader(group.label)
            group.skills.forEach { SkillLine(it, onOpenSkill) }
        }
    }
}

@Composable
private fun CountLine(label: String, count: Int) = CountRow(label, count.toString())

@Composable
private fun SkillLine(chip: SkillChip, onOpenSkill: (VersionedRef) -> Unit) {
    NavRow(chip.name, onClick = { onOpenSkill(chip.skill) }) {
        StateChip(label = chip.label, tone = chip.tone, subject = chip.skill)
        // Qualifiers are text beside the state, never a replacement for it (`SPWX-v0` §5.1).
        chip.qualifiers.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
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
    // 17E: `skill_detail -> planner_explanation` (`D-138`), today's plan as it concerns this Skill.
    onOpenPlan: (() -> Unit)? = null,
) {
    ScreenColumn(modifier = modifier) {
        BackButton(onBack)
        ScreenTitle(view.name)
        Text(view.statement, style = MaterialTheme.typography.bodyMedium)
        StateChip(label = view.chip.label, tone = view.chip.tone, subject = view.skill)
        view.chip.qualifiers.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        onOpenHistory?.let { open -> NavRow("Bu becerinin geçmişi", open) }
        onOpenPlan?.let { open -> NavRow(SkillPlanCopy.ENTRY, open) }

        SectionBlock {
            view.axes.forEach { axis -> CountRow(axis.title, axis.value) }
        }
        view.englishContext?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

        // 16C: remediation_context — a located gap and what closes it, never a verdict about the learner.
        view.remediation?.let { r ->
            SectionHeader(r.title)
            Text(r.text, style = MaterialTheme.typography.bodyMedium)
            r.closure?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }

        SectionHeader("Hedef kırılımı")
        view.objectives.forEach { objective ->
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
                val required = if (objective.required) " · ${ProgressCopy.OBJECTIVE_REQUIRED}" else ""
                Text(objective.label + required, style = MaterialTheme.typography.bodyLarge)
                Text(objective.shown, style = MaterialTheme.typography.bodyMedium)
                objective.weakness?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                objective.misconceptions.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                Text(objective.evidence.filter { it.count > 0 }.joinToString(" · ") { "${it.label}: ${it.count}" }
                    .ifEmpty { "Kanıt yok" }, style = MaterialTheme.typography.bodySmall)
            }
        }

        SectionHeader("Kanıt özeti")
        view.evidence.forEach { CountLine(it.label, it.count) }

        SectionHeader("Ön koşul bağlamı")
        view.prerequisiteNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        view.prerequisite.forEach { line ->
            Text(line.title, style = MaterialTheme.typography.bodyMedium)
            // A blocker is named and linked to its own skill_detail (`SPWX-v0` §8).
            line.skills.forEach { (ref, name) -> NavRow(name, onClick = { onOpenSkill(ref) }) }
        }

        if (view.topics.isNotEmpty()) {
            SectionHeader("Yer aldığı konular")
            view.topics.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
