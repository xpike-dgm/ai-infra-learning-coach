package coach.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coach.presentation.BlockKind
import coach.presentation.Spacing
import coach.presentation.TextBlocks
import coach.presentation.TypeRole

/**
 * 17A (`VPLX-v0 / D-133`): the few layout pieces every screen shares, so spacing, grouping and the `mono` role are
 * decided once. None of them decides what is shown; each only draws what its caller passes.
 */

/**
 * A screen's scrolling column: the safe drawing area (status bar, cutout and navigation bar are never drawn over, the
 * same on API 34 and on the target device's API 36, where edge-to-edge is enforced), the 16dp side margin, and the
 * 4dp rhythm between items.
 */
@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    spacing: Int = Spacing.M,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.M.dp, vertical = Spacing.L.dp),
        verticalArrangement = Arrangement.spacedBy(spacing.dp),
        content = content,
    )
}

/** A screen's own title (`title_large`), announced as a heading. */
@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleLarge, modifier = modifier.semantics { heading() })
}

/** A section's heading (`title`), announced as a heading. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = modifier.padding(top = Spacing.XS.dp).semantics { heading() })
}

/**
 * Related information grouped by structure (`VDSX-v0` §8): a quiet surface-variant block. It is grouping only — it
 * carries no tone, so it never implies a state.
 */
@Composable
fun SectionBlock(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .continuousSize()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(Spacing.S.dp))
            .padding(Spacing.M.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.XS.dp),
        content = content,
    )
}

/**
 * Code, terminal output or an identifier, in the `mono` role (`VDSX-v0` §6.1). It never wraps — a wrapped line of code
 * reads as two — so a long line scrolls sideways, and the text can be selected.
 */
@Composable
fun CodeBlock(text: String, modifier: Modifier = Modifier) {
    SelectionContainer(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(Spacing.XS.dp))
            .horizontalScroll(rememberScrollState())
            .padding(Spacing.S.dp),
    ) {
        Text(text, style = roleStyle(TypeRole.MONO), softWrap = false)
    }
}

/**
 * Authored text — a lesson, a prompt, an explanation — drawn as written: each paragraph core reads as code
 * ([TextBlocks]) in a [CodeBlock], every other paragraph in [style]. Nothing is reworded, reordered or dropped.
 */
@Composable
fun AuthoredText(text: String, style: TextStyle = MaterialTheme.typography.bodyLarge) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.S.dp)) {
        TextBlocks.split(text).forEach { block ->
            when (block.kind) {
                BlockKind.CODE -> CodeBlock(block.text)
                BlockKind.PROSE -> Text(block.text, style = style)
            }
        }
    }
}

/**
 * A row that opens another surface: its title in the body colour (a list of names is not a list of blue links), what
 * the caller adds beneath it, and a trailing chevron that says it opens. The whole row is the 48dp target, and its
 * accessible name is the title.
 */
@Composable
fun NavRow(title: String, onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.XS.dp))
            .clickable(onClickLabel = title, role = Role.Button, onClick = onClick)
            .minimumTouchTarget()
            .padding(vertical = Spacing.XXS.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.S.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.XXS.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            content()
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Back to where the detail was opened from: one action, a full 48dp target, an arrow beside the word. */
@Composable
fun BackButton(onBack: () -> Unit) {
    TextButton(onClick = onBack, modifier = Modifier.minimumTouchTarget()) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.padding(end = Spacing.XS.dp))
        Text("Geri")
    }
}

/** A label and its count, side by side: inventory, never a bar or a ratio (`SPWX-v0` §14). */
@Composable
fun CountRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.M.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.labelLarge)
    }
}

/** The text style for a field the learner types code or a pasted report into. */
val codeFieldStyle: TextStyle get() = roleStyle(TypeRole.MONO)
