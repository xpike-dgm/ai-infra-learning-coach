package coach.presentation

import coach.model.CapacityPreferences
import coach.model.CapacityProfile
import coach.model.PreferenceRecord
import coach.model.TodayCapacityChoice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 16D (`STGX-v0`): Profile shows time as time — minutes and where they came from, never a target or a measure — and
 * every backup refusal says that nothing was changed, because nothing was.
 */
class SettingsPresentationTest {

    // "hedef" is said only to deny it ("hedef değildir"), so it is not on this list.
    private val forbidden = listOf("%", "puan", "seri", "başarısız oldun", "kaçırdın", "borcun")

    @Test
    fun `the three lengths show their minutes and whether they are the preset or the learner's own`() {
        val p = CapacityPreferences.of(listOf(PreferenceRecord(CapacityPreferences.NORMAL_KEY, "45", null, 1)), "2026-10-25")
        val view = SettingsPresentation.capacity(p)
        assertEquals(listOf("30 dk", "45 dk", "90 dk"), view.lengths.map { it.value })
        assertEquals(listOf(SettingsCopy.PRESET, SettingsCopy.CUSTOM, SettingsCopy.PRESET), view.lengths.map { it.source })
        assertEquals(SettingsCopy.TODAY_NONE, view.today)
        assertTrue(view.note.contains("asla aşmaz") && view.note.contains("ilerleme ya da hedef değildir"))
    }

    @Test
    fun `today's options are the three profiles at their lengths, and clearing appears only when there is a choice`() {
        val none = CapacityPreferences.of(emptyList(), "2026-10-25")
        assertEquals(3, SettingsPresentation.todayOptions(none).size)
        val chosen = none.copy(today = TodayCapacityChoice.Profile(CapacityProfile.SHORT))
        val options = SettingsPresentation.todayOptions(chosen)
        assertEquals(TodayCapacityChoice.None, options.last().choice)
        assertEquals("Bugün: Kısa gün (30 dk)", SettingsPresentation.todayLine(chosen))
        assertEquals("Bugün: 25 dk", SettingsPresentation.todayLine(none.copy(today = TodayCapacityChoice.Minutes(25))))
    }

    @Test
    fun `typed minutes are read strictly`() {
        assertEquals(45, SettingsPresentation.parseMinutes(" 45 "))
        listOf("", "abc", "1441", "-3", "4.5").forEach { assertNull(SettingsPresentation.parseMinutes(it), it) }
    }

    @Test
    fun `every refusal says nothing was changed, the confirmation says the old profile is kept, and nothing blames`() {
        BackupRefusal.entries.forEach { refusal ->
            val status = SettingsPresentation.backup(BackupState.RestoreRefused(refusal)).status!!
            assertTrue(status.endsWith("Hiçbir şey değiştirilmedi."), status)
        }
        val confirm = SettingsPresentation.backup(BackupState.ConfirmRestore)
        assertTrue(confirm.confirming && confirm.status!!.contains("silinmez"))
        assertTrue(SettingsPresentation.backup(BackupState.Restored).status!!.contains("Önceki profil cihazda ayrıca saklandı"))
        assertTrue(SettingsPresentation.backup(BackupState.NotKeptNotReplaced).status!!.contains("Hiçbir şey değiştirilmedi"))
        assertTrue(SettingsPresentation.backup(BackupState.Working).busy)
        assertNull(SettingsPresentation.backup(BackupState.Idle).status)
        val all = listOf(SettingsCopy.CAPACITY_NOTE, SettingsCopy.TODAY_DIALOG_NOTE, SettingsCopy.NORMAL_IS_DEFAULT, SettingsCopy.BACKUP_NOTE) +
            BackupRefusal.entries.map(SettingsPresentation::refusalText)
        all.forEach { text -> forbidden.forEach { assertFalse(text.contains(it, ignoreCase = true), "'$it' in $text") } }
    }
}
