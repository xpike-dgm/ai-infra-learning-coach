package coach.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coach.model.VersionedRef
import coach.presentation.LearnCopy
import coach.presentation.LearnView

/**
 * `learn_overview` (17A, `VPLX-v0`): the learning map — each area and its Topics, each opening `topic_detail`. This file
 * renders and decides nothing; the grouping, the order and every chip come from `LearnPresentation` in core, and a
 * chip here is the very chip Progress shows.
 *
 * `view` is `null` while the curriculum is being read, and that is said rather than drawn as an empty map.
 */
@Composable
fun LearnScreen(
    view: LearnView?,
    onOpenTopic: (VersionedRef) -> Unit,
    modifier: Modifier = Modifier,
    curriculumMissing: Boolean = false,
) {
    ScreenColumn(modifier = modifier) {
        ScreenTitle(LearnCopy.TITLE)
        if (view == null) {
            Text(if (curriculumMissing) LearnCopy.NOT_LOADED else LearnCopy.LOADING, style = MaterialTheme.typography.bodyLarge)
            return@ScreenColumn
        }
        Text(view.intro, style = MaterialTheme.typography.bodyMedium)
        Text(view.orderNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        view.areas.forEach { area ->
            SectionHeader(area.label)
            area.note?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            area.topics.forEach { chip -> TopicLine(chip, onOpenTopic) }
        }
    }
}
