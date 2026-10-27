package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import coach.presentation.Spacing
import coach.presentation.ExplanationCopy
import coach.presentation.NotTodayExplanation
import coach.presentation.PlannerExplanationView
import coach.presentation.SkillPlanCopy
import coach.presentation.SkillPlanView
import coach.presentation.TaskExplanation

/**
 * The shared `planner_explanation` surface (12E). It renders and decides nothing: every sentence here is
 * `ExplanationCopy`'s template for a statement `PlannerExplanationPresentation` built from the plan's own
 * trace, so this screen can say nothing the planner did not record.
 *
 * Everything is text. There is no score, no percentage and no ranked list of what waits — a waiting need
 * is explained, never counted against the learner.
 */
@Composable
fun PlannerExplanationScreen(
    view: PlannerExplanationView,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    // 17E: opened from `skill_detail`, the same surface narrowed to one Skill ([SkillPlanView]); back returns to it.
    focus: SkillPlanView? = null,
) {
    ScreenColumn(modifier = modifier) {
        BackButton(onBack)
        Text(
            if (focus == null) "Bu plan neden böyle?" else SkillPlanCopy.TITLE,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )
        focus?.let { Text(ExplanationCopy.names(listOf(it.skill)), style = MaterialTheme.typography.bodyLarge) }
        Text(ExplanationCopy.text(view.state), style = MaterialTheme.typography.bodyMedium)
        if (focus?.notMentioned == true) Text(SkillPlanCopy.NOT_MENTIONED, style = MaterialTheme.typography.bodyMedium)

        if (view.plan.isNotEmpty()) {
            SectionHeading("Plan hakkında")
            view.plan.forEach { Text(ExplanationCopy.text(it), style = MaterialTheme.typography.bodyMedium) }
        }

        if (view.today.isNotEmpty()) {
            SectionHeading(if (focus == null) "Bugün neden bu işler?" else SkillPlanCopy.TODAY)
            view.today.forEach { TaskWhy(it) }
        }

        if (view.notToday.isNotEmpty()) {
            SectionHeading(if (focus == null) "Bugüne alınmayanlar ve bekleyenler" else SkillPlanCopy.NOT_TODAY)
            view.notToday.forEach { NotTodayWhy(it) }
        }

        TextButton(onClick = onBack, modifier = Modifier.minimumTouchTarget()) {
            Text(if (focus == null) "Bugün'e dön" else SkillPlanCopy.BACK)
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    SectionHeader(text)
}

@Composable
private fun TaskWhy(task: TaskExplanation) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
        Text(task.title, style = MaterialTheme.typography.bodyLarge)
        Text(ExplanationCopy.text(task.why), style = MaterialTheme.typography.bodyMedium)
        task.supporting.forEach { Text(ExplanationCopy.text(it), style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun NotTodayWhy(item: NotTodayExplanation) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
        Text(ExplanationCopy.shortNames(item.skills), style = MaterialTheme.typography.bodyLarge)
        Text(ExplanationCopy.text(item.need), style = MaterialTheme.typography.bodySmall)
        Text(ExplanationCopy.text(item.whyNot), style = MaterialTheme.typography.bodyMedium)
        item.reconsideration?.let {
            Text(ExplanationCopy.text(it, item.whyNot.skills), style = MaterialTheme.typography.bodySmall)
        }
    }
}
