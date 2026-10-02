package coach.wiring

import coach.presentation.AiCheckState
import coach.presentation.AiKeyState

/**
 * The AI settings seam (14G). Exactly one implementation is compiled: the AI build's (`src/withAi`) keeps the key in
 * Android Keystore-encrypted storage; the no-AI build's (`src/withoutAi`) has nothing to configure. All of these are
 * disk or network work and are called off the main thread, except [state], which reads a value held in memory.
 */
internal interface AiSettings {
    /** The provider's display name from the adapter's configuration, or `null` in a build without AI. */
    fun providerName(): String?
    fun state(): AiKeyState
    fun save(key: String)
    fun remove()
    fun check(): AiCheckState
}
