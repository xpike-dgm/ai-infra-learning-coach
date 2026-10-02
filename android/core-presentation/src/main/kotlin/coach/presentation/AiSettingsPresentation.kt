package coach.presentation

/**
 * The AI settings section on Profile (14G, `PRVX-v0 / D-111`; user decision: a small screen in 14G).
 *
 * It says plainly what AI is in this product before asking for anything: optional, the app works fully without it,
 * the key stays on this device and is never shown, exported or backed up, only the current task leaves the device,
 * and removing the key turns AI off without deleting anything (`AIAX-v0` §10–§11). The connection check reports what
 * it found and never blames the learner. Wording is working microcopy.
 */
enum class AiKeyState {
    /** This build has no AI adapter (`-PwithAiAdapter=false`): there is nothing to configure. */
    NOT_IN_BUILD,
    NO_KEY,
    KEY_SAVED,
}

enum class AiCheckState {
    IDLE,
    CHECKING,
    WORKS,
    NO_KEY,
    KEY_REJECTED,
    UNREACHABLE,
    TIMED_OUT,
    UNEXPECTED_REPLY,
}

data class AiSettingsView(
    val title: String,
    val lines: List<String>,
    val status: String,
    val showKeyField: Boolean,
    val canRemove: Boolean,
    val canCheck: Boolean,
    val checkLine: String?,
    /** The key field's label; `null` when there is no field. */
    val keyFieldLabel: String?,
)

object AiSettingsCopy {
    const val TITLE = "AI bağlantısı"
    /** The provider's name comes from the adapter's configuration: core never names a provider (`AIAX-v0` §8). */
    fun provider(name: String): String = "Sağlayıcı: $name. Anahtarı kendi $name hesabından alırsın."
    const val OPTIONAL = "AI isteğe bağlıdır: anahtar olmadan da uygulamanın her şeyi çalışır."
    const val KEY_STAYS = "Anahtar yalnız bu cihazda, şifreli saklanır; hiçbir zaman gösterilmez, dışa aktarılmaz ya da yedeklenmez."
    const val WHAT_LEAVES = "AI'a yalnız o anki görev, senin cevabın ve dersin ilgili metni gider; geçmişin, durumun ve planın gitmez."
    const val REMOVE_IS_SAFE = "Anahtarı kaldırırsan AI kapanır; hiçbir verin silinmez."
    const val NOT_IN_BUILD = "Bu sürümde AI yok; uygulama AI olmadan çalışıyor."
    const val NO_KEY = "Anahtar girilmedi; AI kapalı."
    const val KEY_SAVED = "Anahtar kayıtlı; AI açık."
    fun keyField(name: String): String = "$name API anahtarı"
    const val KEY_FIELD_GENERIC = "API anahtarı"
    const val SAVE = "Anahtarı kaydet"
    const val REMOVE = "Anahtarı kaldır"
    const val CHECK = "Bağlantıyı dene"

    fun check(state: AiCheckState): String? = when (state) {
        AiCheckState.IDLE -> null
        AiCheckState.CHECKING -> "Deneniyor…"
        AiCheckState.WORKS -> "Bağlantı çalışıyor."
        AiCheckState.NO_KEY -> "Önce bir anahtar kaydet."
        AiCheckState.KEY_REJECTED -> "Sağlayıcı bu anahtarı kabul etmedi. Anahtarı kontrol edip yeniden kaydedebilirsin."
        AiCheckState.UNREACHABLE -> "Sağlayıcıya ulaşılamadı. İnternet bağlantını kontrol edip yeniden deneyebilirsin."
        AiCheckState.TIMED_OUT -> "Sağlayıcı zamanında cevap vermedi; daha sonra yeniden deneyebilirsin."
        AiCheckState.UNEXPECTED_REPLY -> "Sağlayıcı beklenmeyen bir cevap verdi; daha sonra yeniden deneyebilirsin."
    }
}

object AiSettingsPresentation {

    /** [providerName] is the adapter configuration's display name; `null` in a build without AI. */
    fun of(key: AiKeyState, check: AiCheckState, providerName: String?): AiSettingsView = when (key) {
        AiKeyState.NOT_IN_BUILD -> AiSettingsView(
            title = AiSettingsCopy.TITLE,
            lines = listOf(AiSettingsCopy.OPTIONAL),
            status = AiSettingsCopy.NOT_IN_BUILD,
            showKeyField = false,
            canRemove = false,
            canCheck = false,
            checkLine = null,
            keyFieldLabel = null,
        )
        AiKeyState.NO_KEY, AiKeyState.KEY_SAVED -> AiSettingsView(
            title = AiSettingsCopy.TITLE,
            lines = listOfNotNull(providerName?.let { AiSettingsCopy.provider(it) }, AiSettingsCopy.OPTIONAL, AiSettingsCopy.KEY_STAYS, AiSettingsCopy.WHAT_LEAVES, AiSettingsCopy.REMOVE_IS_SAFE),
            status = if (key == AiKeyState.KEY_SAVED) AiSettingsCopy.KEY_SAVED else AiSettingsCopy.NO_KEY,
            showKeyField = true,
            canRemove = key == AiKeyState.KEY_SAVED,
            canCheck = key == AiKeyState.KEY_SAVED && check != AiCheckState.CHECKING,
            checkLine = AiSettingsCopy.check(check),
            keyFieldLabel = providerName?.let { AiSettingsCopy.keyField(it) } ?: AiSettingsCopy.KEY_FIELD_GENERIC,
        )
    }
}
