package coach.wiring

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import coach.application.ReminderCheck
import coach.application.ReminderSettings
import coach.model.ReminderDecision
import coach.model.ReminderPreference
import coach.model.ReminderSchedule
import coach.presentation.NotificationAccess
import coach.presentation.ReminderCopy
import coach.presentation.ReminderPresentation
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The daily reminder on Android (16E, `RMDX-v0`): an inexact alarm at the learner's time, and a notification only when
 * core says today's plan has something new to say.
 *
 * Everything here is mechanism. Whether a reminder is shown, and what it says, is decided in core ([ReminderCheck],
 * [ReminderPresentation]); this file only arms the alarm, waits for the store and posts what core returned.
 *
 * - **Inexact, no special permission** (user decision): `setWindow` with Android's shortest window, ten minutes
 *   (`AlarmManager.setWindow`: "apps should not pass in a window of less than 10 minutes"). It never fires early.
 * - **Off means nothing wakes the app:** with the reminder off no alarm is armed and the receiver for the system's
 *   reboot and clock signals is disabled, so the process is not started on their account.
 * - **Read-only:** the decision writes no truth; the only thing written here is the device-local note of the study day
 *   whose reminder was shown, which keeps it to one a day.
 */
internal object Reminders {
    private const val TAG = "coach.reminder"
    const val CHANNEL_ID = "daily_reminder"
    private const val NOTIFICATION_ID = 1601
    private const val ACTION_REMIND = "coach.wiring.action.REMIND"
    private const val EXTRA_SCHEDULED_AT = "scheduled_at"

    /** Android's shortest inexact window from API 31 (`AlarmManager.setWindow`). */
    private const val WINDOW_MILLIS = 10 * 60 * 1000L

    /**
     * How long the receiver waits for the store and the decision. A receiver has ten seconds in all, `goAsync` included
     * (`BroadcastReceiver.goAsync`); past this budget the reminder is silence, never a guess.
     */
    const val DECISION_BUDGET_MILLIS = 8_000L

    private const val DELIVERY_PREFS = "reminder_delivery"
    private const val LAST_SHOWN = "last_shown_study_day"

    /**
     * Arms today's or tomorrow's reminder from the stored setting, or disarms it. Idempotent; called on every open.
     * [nowMillis] comes from the app's one clock (`AppGraph.clock`), never from a second reading of the system clock.
     */
    fun applySetting(context: Context, preference: ReminderPreference, nowMillis: Long) {
        if (preference.enabled) arm(context, preference.minuteOfDay, nowMillis) else disarm(context)
    }

    private fun arm(context: Context, minuteOfDay: Int, nowMillis: Long) {
        val at = ReminderSchedule.nextTrigger(Instant.ofEpochMilli(nowMillis), ZoneId.systemDefault(), minuteOfDay)
        val alarms = context.getSystemService(AlarmManager::class.java) ?: return
        alarms.setWindow(AlarmManager.RTC_WAKEUP, at.toEpochMilli(), WINDOW_MILLIS, remindIntent(context, at.toEpochMilli()))
        setRearmReceiver(context, enabled = true)
    }

    private fun disarm(context: Context) {
        context.getSystemService(AlarmManager::class.java)?.cancel(remindIntent(context, 0L))
        setRearmReceiver(context, enabled = false)
    }

    /** One alarm for the reminder; arming again replaces it (the same request code and intent). */
    private fun remindIntent(context: Context, scheduledAt: Long): PendingIntent = PendingIntent.getBroadcast(
        context, 0,
        Intent(context, ReminderAlarmReceiver::class.java).setAction(ACTION_REMIND).putExtra(EXTRA_SCHEDULED_AT, scheduledAt),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private fun setRearmReceiver(context: Context, enabled: Boolean) {
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, ReminderRearmReceiver::class.java),
            if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
    }

    /** What Android allows: the runtime permission on 13+, then whether the app's notifications are turned on. */
    fun access(context: Context): NotificationAccess {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return NotificationAccess.NEEDS_PERMISSION
        }
        val manager = context.getSystemService(NotificationManager::class.java) ?: return NotificationAccess.BLOCKED
        if (!manager.areNotificationsEnabled()) return NotificationAccess.BLOCKED
        val channel = manager.getNotificationChannel(CHANNEL_ID)
        return if (channel != null && channel.importance == NotificationManager.IMPORTANCE_NONE) NotificationAccess.BLOCKED else NotificationAccess.ALLOWED
    }

    /** The alarm went off: decide in core, post only what core returned, and arm the next day's alarm. */
    fun onAlarm(receiver: BroadcastReceiver, context: Context, intent: Intent) {
        val app = context.applicationContext as CoachApplication
        val scheduledAt = intent.getLongExtra(EXTRA_SCHEDULED_AT, 0L)
        val pending = receiver.goAsync()
        val finished = AtomicBoolean(false)
        fun finish() { if (finished.compareAndSet(false, true)) pending.finish() }
        app.whenStoreReady(DECISION_BUDGET_MILLIS, onTimeout = { Log.i(TAG, "silent: store_not_ready"); finish() }) { opened ->
            try {
                val zone = ZoneId.systemDefault()
                val scheduledDay = ReminderSchedule.dateOf(Instant.ofEpochMilli(scheduledAt), zone).toString()
                val decision = ReminderCheck(opened.graph.persistence, opened.graph.content, opened.graph.clock, app.evaluatorAvailableNow())
                    .decide(scheduledDay, lastShown(context), access(context) == NotificationAccess.ALLOWED)
                // Past the budget the receiver has already been let go; nothing is posted after that.
                if (!finished.get()) {
                    when (decision) {
                        is ReminderDecision.Show -> {
                            post(context, decision)
                            context.getSharedPreferences(DELIVERY_PREFS, Context.MODE_PRIVATE).edit()
                                .putString(LAST_SHOWN, decision.studyDay).apply()
                        }
                        is ReminderDecision.Silent -> Log.i(TAG, "silent: ${decision.reason.id}")
                    }
                }
                applySetting(context, ReminderSettings(opened.graph.persistence, opened.graph.clock).read(), opened.graph.clock.now().instantEpochMillis)
            } catch (failure: Exception) {
                // A reminder that cannot be decided is silence; it never becomes a crash or a guessed note.
                Log.w(TAG, "silent: the reminder could not be decided", failure)
            } finally {
                finish()
            }
        }
    }

    /** Reboot, an app update or a clock change: arm again from the stored setting. Nothing is shown here. */
    fun onSystemSignal(receiver: BroadcastReceiver, context: Context, intent: Intent) {
        if (intent.action !in REARM_ACTIONS) return
        val app = context.applicationContext as CoachApplication
        val pending = receiver.goAsync()
        val finished = AtomicBoolean(false)
        fun finish() { if (finished.compareAndSet(false, true)) pending.finish() }
        app.whenStoreReady(DECISION_BUDGET_MILLIS, onTimeout = { finish() }) { opened ->
            try {
                applySetting(context, ReminderSettings(opened.graph.persistence, opened.graph.clock).read(), opened.graph.clock.now().instantEpochMillis)
            } catch (failure: Exception) {
                Log.w(TAG, "the reminder could not be armed again", failure)
            } finally {
                finish()
            }
        }
    }

    private val REARM_ACTIONS = setOf(
        Intent.ACTION_BOOT_COMPLETED,
        Intent.ACTION_MY_PACKAGE_REPLACED,
        Intent.ACTION_TIME_CHANGED,
        Intent.ACTION_TIMEZONE_CHANGED,
    )

    private fun lastShown(context: Context): String? =
        context.getSharedPreferences(DELIVERY_PREFS, Context.MODE_PRIVATE).getString(LAST_SHOWN, null)

    private fun post(context: Context, decision: ReminderDecision.Show) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        ensureChannel(manager)
        val text = ReminderPresentation.notification(decision.content)
        // A tap opens the app the way its icon does: Today is the start destination, and nothing is opened past the
        // checks every entry makes (`UXIA-v0` §15) — the notification carries no task to start.
        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java)
                .setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(text.title)
            .setContentText(text.body)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    /** The reminder's channel; creating it again with the same values changes nothing. */
    fun ensureChannel(context: Context) {
        context.getSystemService(NotificationManager::class.java)?.let(::ensureChannel)
    }

    private fun ensureChannel(manager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, ReminderCopy.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            .apply { description = ReminderCopy.CHANNEL_DESCRIPTION }
        manager.createNotificationChannel(channel)
    }
}

/** Our own alarm; not exported, reached only through the app's own PendingIntent. */
class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = Reminders.onAlarm(this, context, intent)
}

/**
 * The system's reboot, app-update and clock signals, which clear or move an alarm. Enabled only while the reminder is
 * on; any other action is ignored, and a re-arm shows nothing.
 */
class ReminderRearmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = Reminders.onSystemSignal(this, context, intent)
}
