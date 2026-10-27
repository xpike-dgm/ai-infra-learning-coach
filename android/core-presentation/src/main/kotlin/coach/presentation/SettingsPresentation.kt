package coach.presentation

import coach.model.CapacityPreferences
import coach.model.CapacityProfile
import coach.model.TodayCapacityChoice

/**
 * Profile's own settings (16D, `STGX-v0`; `UXIA-v0` Profile: capacity preferences, backup/export/restore) and Today's
 * one capacity action (`THUX-v0` `change_today_capacity`), as pure functions.
 *
 * Time is written as time and nothing else: minutes, described as a budget for the day. It is never a target, a
 * minimum for success, a streak or a measure of progress (D-033, `THUX-v0` capacity context).
 */
object SettingsCopy {
    const val CAPACITY_TITLE = "Günlük süre"
    // D-033: the budget is a ceiling, never a target.
    const val CAPACITY_NOTE = "Plan, ayırdığın sürenin içine sığar ve onu asla aşmaz. Süre bir zaman bütçesidir; ilerleme ya da hedef değildir."
    val profileLabels: Map<CapacityProfile, String> = mapOf(
        CapacityProfile.SHORT to "Kısa gün",
        CapacityProfile.NORMAL to "Normal gün",
        CapacityProfile.INTENSIVE to "Yoğun gün",
    )
    const val PRESET = "önerilen başlangıç"
    const val CUSTOM = "senin ayarın"
    const val NORMAL_IS_DEFAULT = "Bugün için ayrı bir seçim yapmazsan normal gün geçerlidir."
    fun minutes(value: Int) = "$value dk"
    const val TODAY_NONE = "Bugün için ayrı bir seçim yok"
    fun todayProfile(label: String, minutes: Int) = "Bugün: $label (${minutes(minutes)})"
    fun todayMinutes(minutes: Int) = "Bugün: ${minutes(minutes)}"
    const val MINUTES_INVALID = "0 ile 1440 arasında tam bir dakika gir."
    // 17D: each length's field and save button name their own day, so a screen reader can tell three rows apart; the
    // spoken name contains the visible "Kaydet" (label in name).
    fun lengthField(label: String) = "$label süresi (dakika)"
    fun saveLength(label: String) = "$label süresini kaydet"

    const val TODAY_ACTION = "Bugünkü süreyi değiştir"
    const val TODAY_DIALOG_TITLE = "Bugün ne kadar zamanın var?"
    // A smaller day is not a failure, and a larger one promises nothing (`THUX-v0`).
    const val TODAY_DIALOG_NOTE = "Plan yeni süreye göre yeniden kurulur. Daha kısa bir gün başarısızlık değildir ve borç bırakmaz."
    const val TODAY_CUSTOM = "Başka bir süre (dakika)"
    const val TODAY_CLEAR = "Bugünkü seçimi kaldır"

    const val BACKUP_TITLE = "Yedekleme"
    const val BACKUP_NOTE = "Yedek bu cihazdan yalnız senin seçtiğin yere gider. Otomatik bir yükleme yok."
    const val EXPORT_ACTION = "Yedeği dışa aktar"
    const val RESTORE_ACTION = "Yedekten geri yükle"
    // `LFPS-v0` §11.2: replacing a profile is explicit, and the replaced one is kept (user decision, 2026-10-25).
    const val RESTORE_CONFIRM = "Geri yüklemek bu cihazdaki profili seçtiğin yedekle değiştirir. Mevcut profil silinmez; cihazda ayrıca saklanır."
    const val RESTORE_CONFIRM_ACTION = "Değiştir"
    const val CANCEL = "Vazgeç"
}

/** One profile length as Profile shows it. */
data class ProfileLengthLine(val profile: CapacityProfile, val label: String, val minutes: Int, val value: String, val source: String)

data class CapacitySettingsView(
    val title: String,
    val note: String,
    val lengths: List<ProfileLengthLine>,
    val defaultNote: String,
    val today: String,
)

/** One option of Today's capacity dialog. */
data class TodayCapacityOption(val label: String, val choice: TodayCapacityChoice)

/** Why a backup could not be used — the storage layer's reasons, named here so presentation needs no adapter type. */
enum class BackupRefusal {
    INTEGRITY_CHECK_FAILED,
    NOT_A_PROFILE_ARCHIVE,
    NEWER_SCHEMA,
    MIGRATION_INCOMPLETE,
}

/** What the backup controls are doing. Every refusal says that nothing was changed, because nothing was. */
sealed interface BackupState {
    data object Idle : BackupState
    data object Working : BackupState
    data object Exported : BackupState
    data class ExportFailed(val refusal: BackupRefusal?) : BackupState
    data object ConfirmRestore : BackupState
    data object Restored : BackupState
    data class RestoreRefused(val refusal: BackupRefusal) : BackupState
    /** The live profile could not be kept aside, so it was not replaced. */
    data object NotKeptNotReplaced : BackupState
    /** Replaced, but the store did not come back ready; the health surface takes over, and the old profile is kept. */
    data object ReplacedNotReady : BackupState
}

data class BackupView(val title: String, val note: String, val status: String?, val confirming: Boolean, val busy: Boolean)

object SettingsPresentation {

    fun capacity(p: CapacityPreferences): CapacitySettingsView = CapacitySettingsView(
        title = SettingsCopy.CAPACITY_TITLE,
        note = SettingsCopy.CAPACITY_NOTE,
        lengths = CapacityProfile.entries.map { profile ->
            ProfileLengthLine(
                profile = profile,
                label = SettingsCopy.profileLabels.getValue(profile),
                minutes = p.minutesOf(profile),
                value = SettingsCopy.minutes(p.minutesOf(profile)),
                source = if (profile in p.customized) SettingsCopy.CUSTOM else SettingsCopy.PRESET,
            )
        },
        defaultNote = SettingsCopy.NORMAL_IS_DEFAULT,
        today = todayLine(p),
    )

    fun todayLine(p: CapacityPreferences): String = when (val today = p.today) {
        TodayCapacityChoice.None -> SettingsCopy.TODAY_NONE
        is TodayCapacityChoice.Profile -> SettingsCopy.todayProfile(SettingsCopy.profileLabels.getValue(today.profile), p.minutesOf(today.profile))
        is TodayCapacityChoice.Minutes -> SettingsCopy.todayMinutes(today.minutes)
    }

    /** Today's dialog: the three profiles at their current lengths, and clearing today's choice when there is one. */
    fun todayOptions(p: CapacityPreferences): List<TodayCapacityOption> =
        CapacityProfile.entries.map { profile ->
            TodayCapacityOption("${SettingsCopy.profileLabels.getValue(profile)} · ${SettingsCopy.minutes(p.minutesOf(profile))}", TodayCapacityChoice.Profile(profile))
        } + listOfNotNull(if (p.today != TodayCapacityChoice.None) TodayCapacityOption(SettingsCopy.TODAY_CLEAR, TodayCapacityChoice.None) else null)

    /** Typed minutes, read strictly: a whole number from 0 to 1440, or nothing. */
    fun parseMinutes(typed: String): Int? = CapacityPreferences.minutesOf(typed.trim())

    fun backup(state: BackupState): BackupView = BackupView(
        title = SettingsCopy.BACKUP_TITLE,
        note = SettingsCopy.BACKUP_NOTE,
        status = statusOf(state),
        confirming = state == BackupState.ConfirmRestore,
        busy = state == BackupState.Working,
    )

    fun refusalText(refusal: BackupRefusal): String = when (refusal) {
        BackupRefusal.INTEGRITY_CHECK_FAILED -> "Dosya okunamadı ya da bütünlük kontrolünden geçmedi."
        BackupRefusal.NOT_A_PROFILE_ARCHIVE -> "Bu dosya bu uygulamanın bir yedeği değil."
        BackupRefusal.NEWER_SCHEMA -> "Bu yedek uygulamanın daha yeni bir sürümüyle alınmış; tahminle açılmıyor."
        BackupRefusal.MIGRATION_INCOMPLETE -> "Bu eski yedek güncel biçime getirilemedi."
    }

    private fun statusOf(state: BackupState): String? = when (state) {
        BackupState.Idle -> null
        BackupState.Working -> "Çalışıyor…"
        BackupState.Exported -> "Yedek kaydedildi."
        is BackupState.ExportFailed -> (state.refusal?.let(::refusalText)?.plus(" ") ?: "") + "Yedek alınamadı; profilin değişmedi."
        BackupState.ConfirmRestore -> SettingsCopy.RESTORE_CONFIRM
        BackupState.Restored -> "Yedek geri yüklendi. Önceki profil cihazda ayrıca saklandı."
        is BackupState.RestoreRefused -> refusalText(state.refusal) + " Hiçbir şey değiştirilmedi."
        BackupState.NotKeptNotReplaced -> "Mevcut profil ayrıca saklanamadığı için geri yükleme yapılmadı. Hiçbir şey değiştirilmedi."
        BackupState.ReplacedNotReady -> "Yedek yerleştirildi ama açılırken bir sorun çıktı. Önceki profil cihazda ayrıca saklandı."
    }
}
