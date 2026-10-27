package coach.presentation

import coach.model.CapacityProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** 17D (`A11X-v0`): repeated controls are told apart by name, and a spoken name keeps the visible label. */
class AccessibleNamesTest {

    @Test
    fun `each day length's field and save button name their own day`() {
        val labels = CapacityProfile.entries.map { SettingsCopy.profileLabels.getValue(it) }
        val fields = labels.map(SettingsCopy::lengthField)
        val saves = labels.map(SettingsCopy::saveLength)
        assertEquals(labels.size, fields.toSet().size, "three fields, three names: $fields")
        assertEquals(labels.size, saves.toSet().size, "three buttons, three names: $saves")
        labels.zip(fields).forEach { (label, field) -> assertTrue(field.startsWith(label), field) }
        labels.zip(saves).forEach { (label, save) -> assertTrue(save.startsWith(label), save) }
    }

    @Test
    fun `the spoken name of a save button contains the word the learner sees`() {
        // Label in name: a voice-control user says what they see, so the visible "Kaydet" must be inside the spoken name.
        SettingsCopy.profileLabels.values.forEach { label ->
            assertTrue(SettingsCopy.saveLength(label).contains("kaydet", ignoreCase = true), SettingsCopy.saveLength(label))
        }
        assertEquals("Kısa gün süresini kaydet", SettingsCopy.saveLength("Kısa gün"))
        assertEquals("Kısa gün süresi (dakika)", SettingsCopy.lengthField("Kısa gün"))
    }

    @Test
    fun `an allowed tool is said in words and never as its id`() {
        assertEquals("terminal, belgeler", ToolWords.of(listOf("terminal", "documentation")))
        assertEquals("dış yapay zekâ", ToolWords.of(listOf("external_ai")))
        // A tool the app has no word for is said as such, once, not shown as its id.
        assertEquals(ToolWords.UNNAMED, ToolWords.of(listOf("x_tool", "y_tool")))
        ToolWords.labels.values.forEach { assertTrue(!it.contains('_'), it) }
    }
}
