package coach.presentation

import coach.model.ReminderPlanContent
import coach.model.ReminderPreference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 16E (`RMDX-v0`): the reminder's words say what today's plan holds, and never a count of days, a series or a debt. */
class ReminderPresentationTest {

    // Turkish casing, never the default locale's (10C).
    private val turkish = java.util.Locale.forLanguageTag("tr")

    /** Words a reminder may never use: a series, an absence, a debt, urgency, a verdict, a percentage or a goal. */
    private val forbidden = listOf(
        "seri", "streak", "kaçır", "kaybet", "borç", "gündür", "gün oldu", "özledik", "hâlâ", "henüz çalışmadın",
        "son şans", "hemen", "acele", "geride", "%", "hedef", "puan", "başarısız", "unutma", "unuttun",
    )

    private fun every(): List<ReminderPlanContent> = buildList {
        for (review in listOf(false, true)) for (weekly in listOf(false, true)) for (monthly in listOf(false, true)) {
            add(ReminderPlanContent(3, 45, review, weekly, monthly))
            add(ReminderPlanContent(1, 0, review, weekly, monthly))
        }
    }

    @Test
    fun `the title is the same every day, and the body is only what today's plan holds`() {
        val plain = ReminderPresentation.notification(ReminderPlanContent(3, 45, false, false, false))
        assertEquals("Bugünün planı hazır", plain.title)
        assertEquals("3 görev, yaklaşık 45 dk", plain.body)
        val all = ReminderPresentation.notification(ReminderPlanContent(2, 30, true, true, true))
        assertEquals("2 görev, yaklaşık 30 dk · içinde tekrar, haftalık değerlendirme ve aylık değerlendirme var", all.body)
        assertEquals("1 görev · içinde tekrar var", ReminderPresentation.notification(ReminderPlanContent(1, 0, true, false, false)).body)
        assertEquals(setOf("Bugünün planı hazır"), every().map { ReminderPresentation.notification(it).title }.toSet())
    }

    @Test
    fun `no reminder text can count days, keep a series, name an absence or press`() {
        val texts = every().flatMap { ReminderPresentation.notification(it).let { n -> listOf(n.title, n.body) } } +
            listOf(ReminderCopy.CHANNEL_NAME, ReminderCopy.CHANNEL_DESCRIPTION)
        texts.forEach { text ->
            forbidden.forEach { word -> assertFalse(text.lowercase(turkish).contains(word), "\"$word\" in \"$text\"") }
        }
    }

    @Test
    fun `Profile says off, on, or that Android blocks it, and offers Android's settings only then`() {
        val off = ReminderPresentation.settings(ReminderPreference.OFF, NotificationAccess.NEEDS_PERMISSION)
        assertFalse(off.enabled)
        assertEquals("19:00", off.time)
        assertTrue(off.status.startsWith("Kapalı"), off.status)
        assertFalse(off.offersSystemSettings)

        val on = ReminderPresentation.settings(ReminderPreference(true, 20 * 60 + 30, true), NotificationAccess.ALLOWED)
        assertEquals("Açık · her gün 20:30 sularında", on.status)
        assertFalse(on.offersSystemSettings)

        val blocked = ReminderPresentation.settings(ReminderPreference(true, 20 * 60, true), NotificationAccess.BLOCKED)
        assertTrue(blocked.status.contains("Android bu uygulamanın bildirimlerini engelliyor"), blocked.status)
        assertTrue(blocked.offersSystemSettings)
        // A restored profile with the reminder on, on a phone that never granted notifications.
        assertTrue(ReminderPresentation.settings(ReminderPreference(true, 20 * 60, true), NotificationAccess.NEEDS_PERMISSION).offersSystemSettings)

        // After a refusal Android still reports the permission as not granted, and may stop asking: its settings are
        // then the only way back, so they are offered.
        val refused = ReminderPresentation.settings(ReminderPreference.OFF, NotificationAccess.NEEDS_PERMISSION, refusedJustNow = true)
        assertEquals(ReminderCopy.PERMISSION_REFUSED, refused.status)
        assertTrue(refused.offersSystemSettings)
        assertFalse(ReminderPresentation.settings(ReminderPreference.OFF, NotificationAccess.BLOCKED).offersSystemSettings)
        // Granted later in Android's settings: the refusal is over and Profile says the reminder is simply off.
        val granted = ReminderPresentation.settings(ReminderPreference.OFF, NotificationAccess.ALLOWED, refusedJustNow = true)
        assertTrue(granted.status.startsWith("Kapalı"), granted.status)
        assertFalse(granted.offersSystemSettings)
    }

    @Test
    fun `the note says the reminder is no obligation and keeps no count`() {
        assertTrue(ReminderCopy.SETTINGS_NOTE.contains("Günde en çok bir kez"))
        assertTrue(ReminderCopy.SETTINGS_NOTE.contains("yükümlülük değildir"))
        assertTrue(ReminderCopy.SETTINGS_NOTE.contains("seri tutmaz"))
        assertEquals(19 * 60 + 15, ReminderPresentation.parseTime("19:15"))
        assertEquals(null, ReminderPresentation.parseTime("19.15"))
    }
}
