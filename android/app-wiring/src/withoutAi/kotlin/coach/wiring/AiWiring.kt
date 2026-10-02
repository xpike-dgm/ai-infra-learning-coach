package coach.wiring

import android.content.Context
import coach.presentation.AiCheckState
import coach.presentation.AiKeyState

/**
 * The build without :ai-adapter (14G). There is no key, no client and no network permission (see the no-AI manifest):
 * nothing can leave the device, and the settings section says AI is not in this build.
 */
internal fun initAi(context: Context) = Unit
internal fun primeAi() = Unit

internal fun provideAiSettings(): AiSettings = object : AiSettings {
    override fun providerName(): String? = null
    override fun state(): AiKeyState = AiKeyState.NOT_IN_BUILD
    override fun save(key: String) = Unit
    override fun remove() = Unit
    override fun check(): AiCheckState = AiCheckState.IDLE
}
