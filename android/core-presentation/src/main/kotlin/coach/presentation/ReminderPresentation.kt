package coach.presentation

import coach.model.ReminderPlanContent
import coach.model.ReminderPreference
import coach.model.ReminderTime

/**
 * The daily reminder's words (16E, `RMDX-v0`): the notification itself and Profile's reminder setting (`UXIA-v0`
 * Profile `reminders`), as pure functions.
 *
 * A reminder only says what today's plan holds, in the plan's own facts. It never counts days, keeps a series, names
 * an absence, asks for urgency, promises a result or says what the learner did not do (`SRR-v0`, `THUX-v0`, 11E); there
 * is no function here that could produce such a sentence, and the notification's title is the same every day.
 */
object ReminderCopy {
    const val CHANNEL_NAME = "Günlük hatırlatma"
    const val CHANNEL_DESCRIPTION = "Seçtiğin saatte, bugünün planında iş varsa günde en çok bir kez."

    const val TITLE = "Bugünün planı hazır"

    const val SETTINGS_TITLE = "Hatırlatma"
    // What the reminder is and is not: one a day at most, only with new work in today's plan, never an obligation.
    const val SETTINGS_NOTE = "Günde en çok bir kez gelir: yalnız bugünün planında henüz başlamadığın bir görev varsa. " +
        "Saat yaklaşıktır. Hatırlatma bir yükümlülük değildir; gün saymaz, seri tutmaz, ara vermeyi borç saymaz."
    const val TURN_ON = "Hatırlatmayı aç"
    const val TURN_OFF = "Hatırlatmayı kapat"
    const val TIME_LABEL = "Saat (SS:DD)"
    const val SAVE_TIME = "Saati kaydet"
    const val TIME_INVALID = "SS:DD biçiminde bir saat gir (ör. 19:00)."
    const val OPEN_SYSTEM_SETTINGS = "Android bildirim ayarlarını aç"

    fun off(time: String) = "Kapalı. Açarsan her gün $time sularında gelebilir."
    fun on(time: String) = "Açık · her gün $time sularında"
    fun onButBlocked(time: String) = "Açık ($time), ama Android bu uygulamanın bildirimlerini engelliyor; hatırlatma gösterilmez."
    const val PERMISSION_REFUSED = "Bildirim izni verilmedi; hatırlatma kapalı kaldı."
}

/** What Android allows for this app's notifications, as the composition root reads it. */
enum class NotificationAccess {
    /** Notifications can be shown. */
    ALLOWED,

    /** Android 13+ has not been asked yet; turning the reminder on asks first. */
    NEEDS_PERMISSION,

    /** Refused or turned off in Android's settings; only Android's own settings can change it. */
    BLOCKED,
}

data class ReminderNotificationText(val title: String, val body: String)

data class ReminderSettingsView(
    val title: String,
    val note: String,
    val enabled: Boolean,
    val time: String,
    val status: String,
    /**
     * Whether Profile offers Android's own notification settings: when Android blocks a reminder that is on, or right
     * after the learner refused the prompt — Android stops showing the prompt after repeated refusals, so its settings
     * are then the only way back.
     */
    val offersSystemSettings: Boolean,
)

object ReminderPresentation {

    /**
     * The notification: the same title every day, and a body built only from what today's plan holds — how many new
     * tasks, the plan's estimate of their minutes, and whether a review or a weekly/monthly assessment is among them.
     */
    fun notification(content: ReminderPlanContent): ReminderNotificationText {
        val size = if (content.plannedMinutes > 0) {
            "${content.taskCount} görev, yaklaşık ${content.plannedMinutes} dk"
        } else {
            "${content.taskCount} görev"
        }
        val inside = listOfNotNull(
            "tekrar".takeIf { content.includesReview },
            "haftalık değerlendirme".takeIf { content.includesWeeklyAssessment },
            "aylık değerlendirme".takeIf { content.includesMonthlyAssessment },
        )
        val body = if (inside.isEmpty()) size else "$size · içinde ${joined(inside)} var"
        return ReminderNotificationText(ReminderCopy.TITLE, body)
    }

    /** Profile's reminder setting. [refusedJustNow] is the moment after the learner said no to Android's prompt. */
    fun settings(p: ReminderPreference, access: NotificationAccess, refusedJustNow: Boolean = false): ReminderSettingsView {
        val time = ReminderTime.format(p.minuteOfDay)
        // Once Android allows notifications again (the learner granted them in its settings), the refusal is over.
        val refused = refusedJustNow && !p.enabled && access != NotificationAccess.ALLOWED
        val status = when {
            refused -> ReminderCopy.PERMISSION_REFUSED
            !p.enabled -> ReminderCopy.off(time)
            access == NotificationAccess.ALLOWED -> ReminderCopy.on(time)
            else -> ReminderCopy.onButBlocked(time)
        }
        return ReminderSettingsView(
            title = ReminderCopy.SETTINGS_TITLE,
            note = ReminderCopy.SETTINGS_NOTE,
            enabled = p.enabled,
            time = time,
            status = status,
            // On but not allowed (blocked, or a restored profile on a phone that never granted it), or just refused.
            offersSystemSettings = (p.enabled && access != NotificationAccess.ALLOWED) || refused,
        )
    }

    /** A typed time, read strictly as `SS:DD`, or nothing. */
    fun parseTime(typed: String): Int? = ReminderTime.parse(typed)

    private fun joined(parts: List<String>): String = when (parts.size) {
        1 -> parts[0]
        else -> parts.dropLast(1).joinToString(", ") + " ve " + parts.last()
    }
}
