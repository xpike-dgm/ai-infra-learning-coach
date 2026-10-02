package coach.presentation

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The AI settings section (14G): what AI is, where the key stays, what leaves the device — before anything is asked. */
class AiSettingsPresentationTest {

    @Test
    fun `before a key is asked for, the screen says AI is optional, where the key stays and what leaves the device`() {
        val view = AiSettingsPresentation.of(AiKeyState.NO_KEY, AiCheckState.IDLE, "OpenAI")
        assertEquals(listOf(AiSettingsCopy.provider("OpenAI"), AiSettingsCopy.OPTIONAL, AiSettingsCopy.KEY_STAYS, AiSettingsCopy.WHAT_LEAVES, AiSettingsCopy.REMOVE_IS_SAFE), view.lines)
        assertEquals("OpenAI API anahtarı", view.keyFieldLabel, "the provider's name comes from configuration, never from core")
        assertEquals("Sağlayıcı: Başka. Anahtarı kendi Başka hesabından alırsın.", AiSettingsPresentation.of(AiKeyState.NO_KEY, AiCheckState.IDLE, "Başka").lines.first())
        assertTrue(view.showKeyField)
        assertFalse(view.canRemove)
        assertFalse(view.canCheck, "nothing to check without a key")
        assertEquals(AiSettingsCopy.NO_KEY, view.status)
        assertTrue("gösterilmez" in AiSettingsCopy.KEY_STAYS && "yedeklenmez" in AiSettingsCopy.KEY_STAYS)
        assertTrue("silinmez" in AiSettingsCopy.REMOVE_IS_SAFE)
    }

    @Test
    fun `with a key saved it can be checked and removed, and a check in flight cannot be started twice`() {
        val saved = AiSettingsPresentation.of(AiKeyState.KEY_SAVED, AiCheckState.IDLE, "OpenAI")
        assertTrue(saved.canRemove && saved.canCheck)
        assertEquals(AiSettingsCopy.KEY_SAVED, saved.status)
        assertNull(saved.checkLine)
        assertFalse(AiSettingsPresentation.of(AiKeyState.KEY_SAVED, AiCheckState.CHECKING, "OpenAI").canCheck)
    }

    @Test
    fun `a build without AI has nothing to configure, and every check result is said without blame`() {
        val none = AiSettingsPresentation.of(AiKeyState.NOT_IN_BUILD, AiCheckState.IDLE, null)
        assertFalse(none.showKeyField || none.canRemove || none.canCheck)
        assertEquals(AiSettingsCopy.NOT_IN_BUILD, none.status)
        for (state in AiCheckState.entries - AiCheckState.IDLE) assertTrue(!AiSettingsCopy.check(state).isNullOrBlank(), state.name)
        val all = (AiCheckState.entries.mapNotNull { AiSettingsCopy.check(it) } + listOf(AiSettingsCopy.KEY_STAYS, AiSettingsCopy.WHAT_LEAVES)).joinToString(" ")
        for (word in listOf("hata yaptın", "yanlış girdin", "başarısız")) assertFalse(word in all.lowercase(Locale.forLanguageTag("tr")), word)
    }
}
