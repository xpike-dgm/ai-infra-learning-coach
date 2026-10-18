package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import coach.presentation.AssessmentReportView
import coach.presentation.LearningHistoryView

/**
 * The learning history and the assessment report (16B), as `SPWX-v0` §§12–13 define them. This file renders and
 * decides nothing: every line, change sentence and family was settled in `HistoryPresentation` in core.
 *
 * A history is a list read top to bottom (`SPWX-v0` §18), newest first. There is no calendar, grid, streak or count of
 * days: only days on which something happened appear, so no gap can be drawn as a miss.
 */
@Composable
fun LearningHistoryScreen(
    view: LearningHistoryView,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.minimumTouchTarget()) { Text("Geri") }
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        StateChip(label = view.text, tone = view.state.tone, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        view.days.forEach { day ->
            HorizontalDivider()
            Text(day.studyDay, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
            day.lines.forEach { line ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${line.familyLabel} · ${line.skillName}", style = MaterialTheme.typography.bodyLarge)
                    Text(line.objectiveLabel, style = MaterialTheme.typography.bodySmall)
                    Text(line.happened, style = MaterialTheme.typography.bodyMedium)
                    line.changed.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}

/** Each session's families, never a grade, score or trend (`SPWX-v0` §13). */
@Composable
fun AssessmentReportScreen(
    view: AssessmentReportView,
    onOpenHistory: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.minimumTouchTarget()) { Text("Geri") }
        Text("Ölçme raporu", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        StateChip(label = view.text, tone = view.state.tone, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        // `assessment_report -> learning_history` is an accepted edge (`UXIA-v0`).
        TextButton(onClick = onOpenHistory, modifier = Modifier.minimumTouchTarget()) { Text("Öğrenme geçmişi") }
        view.sessions.forEach { session ->
            HorizontalDivider()
            Text("${session.title} · ${session.studyDay}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
            session.summary?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            session.families.forEach { block ->
                Text(block.label, style = MaterialTheme.typography.bodyLarge)
                block.statements.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}
