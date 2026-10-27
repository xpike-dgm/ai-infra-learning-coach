package coach.ui

import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.PlatformTextInputMethodRequest
import coach.presentation.AnswerField
import coach.presentation.AnswerInput

/**
 * 17C (`USBX-v0`): a field whose text must arrive exactly as typed ([AnswerInput.exact]) asks the keyboard for no
 * suggestions at all.
 *
 * Compose's `autoCorrectEnabled = false` only leaves out the auto-correct flag; on a multi-line field the common
 * keyboards keep correcting anyway (the independent 17C review found this in the compiled Compose 1.12 and in AOSP's
 * LatinIME). The one flag keyboards honour is `TYPE_TEXT_FLAG_NO_SUGGESTIONS`, so it is added to the editor's input type
 * here, after Compose has filled it in. Turkish letters stay typeable: only suggestions and corrections are turned off.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExactTextInput(field: AnswerField, content: @Composable () -> Unit) {
    if (!AnswerInput.exact(field)) {
        content()
        return
    }
    InterceptPlatformTextInput(
        interceptor = { request, nextHandler ->
            nextHandler.startInputMethod(object : PlatformTextInputMethodRequest {
                override fun createInputConnection(outAttributes: EditorInfo): InputConnection {
                    val connection = request.createInputConnection(outAttributes)
                    outAttributes.inputType = noSuggestions(outAttributes.inputType)
                    return connection
                }
            })
        },
        content = content,
    )
}

/** The editor's input type with suggestions turned off; everything else Compose set is kept. */
fun noSuggestions(inputType: Int): Int = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
