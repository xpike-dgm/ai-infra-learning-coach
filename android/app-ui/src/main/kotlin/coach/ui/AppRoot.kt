package coach.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * app-ui renders and nothing more. It may not compute presentation state: that lives in
 * core-presentation as a pure function (MSBX-v0 §presentation).
 *
 * The real surfaces arrive at 10B (navigation) and 10C (design system). The theme built here
 * will use lightColorScheme()/darkColorScheme() with the WFPX-v0 measured tokens; the dynamic
 * colour functions are forbidden and their absence is checked.
 */
@Composable
fun AppRoot(statusLine: String) {
    Text(text = statusLine)
}
