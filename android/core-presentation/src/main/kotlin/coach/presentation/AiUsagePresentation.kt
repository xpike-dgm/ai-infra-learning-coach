package coach.presentation

import coach.model.AiCallOutcome
import coach.model.AiCallRecord
import coach.model.AiCallUse

/**
 * The AI calls the app made, as counts (17E, `D-138`; user decision 2026-10-27: calls and the provider's tokens, no money).
 *
 * Each use is one line: how many calls, how they ended, and the input and output tokens the provider reported. A call
 * whose reply reported no tokens is counted as such, never as zero tokens. There is no price, cost, budget or limit
 * here: the provider's reply carries tokens, not money, and a price the app does not know is not invented. The counts
 * are inventory, never a measure of learning (`SPWX-v0` §14).
 */
object AiUsageCopy {
    const val TITLE = "AI kullanımı"
    const val THIS_MONTH = "Bu ay"
    const val ALL = "Tüm kayıtlar"
    const val NONE = "Henüz kayıtlı bir AI çağrısı yok."
    const val NOT_IN_BUILD = "Bu sürümde AI yok; hiçbir çağrı yapılamaz."
    const val WHAT_IS_COUNTED =
        "Yalnız cihazdan çıkan çağrılar sayılır: ne için yapıldığı, nasıl bittiği ve sağlayıcının bildirdiği token sayısı. Ne sorulduğu ve ne cevaplandığı saklanmaz."
    const val NO_PRICE =
        "Uygulama bir ücret hesaplamaz: sağlayıcının yanıtı token sayısını verir, fiyatı vermez. Ücreti sağlayıcının kendi fiyatları ve hesabın belirler."

    val useLabels: Map<AiCallUse, String> = mapOf(
        AiCallUse.TUTOR_HELP to "Görev içi yardım",
        AiCallUse.OPEN_RESPONSE_EVALUATION to "Açık cevap değerlendirme",
        AiCallUse.CODE_EVALUATION to "Kod değerlendirme",
        AiCallUse.CONNECTION_CHECK to "Bağlantı denemesi",
    )
    val outcomeLabels: Map<AiCallOutcome, String> = mapOf(
        // The provider's reply arrived; whether the tutor or evaluator could use it is theirs to say (review, 17E).
        AiCallOutcome.ANSWERED to "yanıt geldi",
        AiCallOutcome.REFUSED to "reddedildi",
        AiCallOutcome.INVALID_RESPONSE to "kullanılamaz yanıt",
        AiCallOutcome.TIMED_OUT to "zaman aşımı",
        AiCallOutcome.TRANSPORT_ERROR to "bağlantı ya da sağlayıcı hatası",
        AiCallOutcome.KEY_REJECTED to "anahtar reddedildi",
    )
}

/** One use's counts, already in words. [tokens] is `null` when no call of this use reported any. */
data class AiUsageLine(val use: AiCallUse, val label: String, val calls: String, val tokens: String?)

data class AiUsagePeriod(val title: String, val lines: List<AiUsageLine>)

data class AiUsageView(val title: String, val text: String?, val periods: List<AiUsagePeriod>, val notes: List<String>)

object AiUsagePresentation {

    fun of(calls: List<AiCallRecord>?, today: String, aiInBuild: Boolean): AiUsageView {
        val notes = listOf(AiUsageCopy.WHAT_IS_COUNTED, AiUsageCopy.NO_PRICE)
        if (!aiInBuild) return AiUsageView(AiUsageCopy.TITLE, AiUsageCopy.NOT_IN_BUILD, emptyList(), emptyList())
        if (calls.isNullOrEmpty()) return AiUsageView(AiUsageCopy.TITLE, AiUsageCopy.NONE, emptyList(), notes)
        // The calendar month of the study day each row recorded, never one recomputed from its instant (11E).
        val month = today.take(MONTH_PREFIX)
        val periods = listOf(
            AiUsagePeriod(AiUsageCopy.THIS_MONTH, lines(calls.filter { it.studyDay.take(MONTH_PREFIX) == month })),
            AiUsagePeriod(AiUsageCopy.ALL, lines(calls)),
        ).filter { it.lines.isNotEmpty() }
        return AiUsageView(AiUsageCopy.TITLE, null, periods, notes)
    }

    private fun lines(calls: List<AiCallRecord>): List<AiUsageLine> =
        AiCallUse.entries.mapNotNull { use ->
            val ofUse = calls.filter { it.call.use == use }.map { it.call }
            if (ofUse.isEmpty()) return@mapNotNull null
            val outcomes = AiCallOutcome.entries.mapNotNull { outcome ->
                ofUse.count { it.outcome == outcome }.takeIf { it > 0 }?.let { "${number(it.toLong())} ${AiUsageCopy.outcomeLabels.getValue(outcome)}" }
            }
            val reported = ofUse.filter { it.inputTokens != null || it.outputTokens != null }
            val unreported = ofUse.size - reported.size
            val tokens = if (reported.isEmpty()) null else buildString {
                append("${number(reported.sumOf { it.inputTokens ?: 0 })} giriş, ${number(reported.sumOf { it.outputTokens ?: 0 })} çıkış token")
                if (unreported > 0) append(" · ${number(unreported.toLong())} çağrı token bildirmedi")
            }
            AiUsageLine(
                use = use,
                label = AiUsageCopy.useLabels.getValue(use),
                calls = "${number(ofUse.size.toLong())} çağrı: " + outcomes.joinToString(", "),
                tokens = tokens ?: "Sağlayıcı bu çağrılar için token bildirmedi.",
            )
        }

    /** Digits grouped by three with a dot, as Turkish writes them; no locale is consulted. */
    fun number(value: Long): String {
        val digits = value.toString()
        if (value < 0) return digits
        return digits.reversed().chunked(3).joinToString(".").reversed()
    }

    private const val MONTH_PREFIX = 7
}
