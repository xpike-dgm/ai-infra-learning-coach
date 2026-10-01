package coach.presentation

import coach.model.AssistanceLevel
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.PendingReason
import coach.model.TutorIntent
import coach.model.TutorMissing
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRedirectReason

/**
 * The tutor as the learner meets it (`TUTX-v0` §14): contextual help inside a focused flow, never a
 * destination (`UXIA-v0` §6.3) and never a chat that decides anything.
 *
 * What it says is the part most likely to become coercive or untrue — a help ladder that shames, an
 * unavailable service that looks like the learner's fault, a disclosure that misstates what an answer can
 * still prove — so it is a plain JVM function of the contract's outcomes. Wording is working microcopy.
 */
enum class TutorPanelState(val id: String) {
    LEVEL_NEEDED("level_needed"),
    DISCLOSURE_NEEDED("disclosure_needed"),
    REDIRECTED("redirected"),
    INCOMPLETE("incomplete"),
    WAITING("waiting"),
    SHOWN("shown"),
    NOT_SHOWN("not_shown"),
}

/**
 * Only a pending reply is `pending_unresolved`; everything else is neutral. **No panel state is a fault**:
 * a tutor that cannot answer is a capability fact (`AIAX-v0` §7.2), exactly as `ai_unavailable` is for Today.
 */
val TutorPanelState.tone: Tone
    get() = when (this) {
        TutorPanelState.WAITING -> Tone.PENDING_UNRESOLVED
        TutorPanelState.LEVEL_NEEDED,
        TutorPanelState.DISCLOSURE_NEEDED,
        TutorPanelState.REDIRECTED,
        TutorPanelState.INCOMPLETE,
        TutorPanelState.SHOWN,
        TutorPanelState.NOT_SHOWN -> Tone.NEUTRAL
    }

object TutorPresentation {

    fun state(preparation: TutorPreparation): TutorPanelState = when (preparation) {
        is TutorPreparation.Ready -> TutorPanelState.WAITING
        TutorPreparation.LevelNeeded -> TutorPanelState.LEVEL_NEEDED
        is TutorPreparation.DisclosureNeeded -> TutorPanelState.DISCLOSURE_NEEDED
        is TutorPreparation.Redirect -> TutorPanelState.REDIRECTED
        is TutorPreparation.Incomplete -> TutorPanelState.INCOMPLETE
    }

    fun state(outcome: TutorOutcome): TutorPanelState = when (outcome) {
        is TutorOutcome.Shown -> TutorPanelState.SHOWN
        is TutorOutcome.NotShown -> TutorPanelState.NOT_SHOWN
    }

    /**
     * What H3/H4 changes, said for the moment it is asked. While an answer is open the attempt stops being
     * independent evidence (`TRUX-v0` §8.3); once it is frozen the attempt is untouched and only the item's
     * freshness changes (`2D` §5.3) — telling the learner otherwise would be untrue.
     */
    fun disclosure(timing: AssistanceTiming): String = when (timing) {
        AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT -> RunnerCopy.CONSEQUENCE_DISCLOSURE
        AssistanceTiming.AFTER_SUBMIT, AssistanceTiming.AFTER_FAILURE -> TutorCopy.DISCLOSURE_AFTER_ANSWER
    }

    /** The lines shown beside help the learner received: where it came from, and what it changed, if anything. */
    fun notes(outcome: TutorOutcome.Shown): List<String> = buildList {
        add(if (outcome.source == AssistanceSource.AI_GENERATED) TutorCopy.AI_LABEL else TutorCopy.AUTHORED_LABEL)
        if (outcome.convertsItemToLearning) add(TutorCopy.CONVERTED_TO_LEARNING)
        if (outcome.endsDiagnosticFastPath) add(TutorCopy.FAST_PATH_ENDED)
    }

    /** Nothing was shown and nothing was recorded; the sentence says so and never blames the learner. */
    fun notShown(reason: PendingReason): String = when (reason) {
        PendingReason.UNAVAILABLE -> TutorCopy.UNAVAILABLE
        PendingReason.REFUSED -> TutorCopy.REFUSED
        PendingReason.TIMED_OUT -> TutorCopy.TIMED_OUT
        PendingReason.TRANSPORT_ERROR -> TutorCopy.TRANSPORT_ERROR
        PendingReason.INVALID_RESPONSE -> TutorCopy.INVALID_RESPONSE
    }

    fun redirect(reason: TutorRedirectReason): String = when (reason) {
        TutorRedirectReason.NO_ITEM_IS_BEING_WORKED -> TutorCopy.REDIRECT_NO_ITEM
        TutorRedirectReason.NO_ANSWER_IS_FROZEN_YET -> TutorCopy.REDIRECT_NO_ANSWER_YET
        TutorRedirectReason.ANSWER_ALREADY_FROZEN -> TutorCopy.REDIRECT_ANSWER_FROZEN
        TutorRedirectReason.SEGMENT_IS_THE_TARGET -> TutorCopy.REDIRECT_TARGET_SEGMENT
    }

    fun missing(missing: TutorMissing): String = when (missing) {
        TutorMissing.QUESTION_TEXT -> TutorCopy.MISSING_QUESTION
        TutorMissing.FROZEN_ANSWER -> TutorCopy.MISSING_FROZEN_ANSWER
        TutorMissing.SEGMENT -> TutorCopy.MISSING_SEGMENT
    }
}

/**
 * The tutor's working microcopy. Every ask is offered on equal terms: asking is never a slower or
 * discouraged path, no level is labelled as weakness, and nothing here claims what the learner can do.
 */
object TutorCopy {

    fun intent(intent: TutorIntent): String = when (intent) {
        TutorIntent.HINT -> "İpucu iste"
        TutorIntent.EXPLAIN_DIFFERENTLY -> "Başka türlü anlat"
        TutorIntent.QUESTION -> "Soru sor"
        TutorIntent.EXPLAIN_MISTAKE -> "Cevabımı açıkla"
        TutorIntent.GLOSS -> "Bu ifade ne demek?"
    }

    /** The levels in the learner's words, least revealing first. */
    fun level(level: AssistanceLevel): String = when (level) {
        AssistanceLevel.H1 -> "Yalnız yön göster"
        AssistanceLevel.H2 -> "İlgili kavramı göster"
        AssistanceLevel.H3 -> "Çözümün bir kısmını göster"
        AssistanceLevel.H4 -> "Tam çözümü göster"
    }

    const val LEVEL_PROMPT =
        "Cevap en fazla ne kadarını açabilir? Seçimin, bu denemenin neyi kanıtlayabileceğini belirler; hangisini seçersen seç, yardım istemek cezalandırılmaz."

    const val AI_LABEL = "AI yardımı: bu bir öğretim metnidir; bir değerlendirme ya da ilerleme bilgisi değildir."
    const val AUTHORED_LABEL = "Hazırlanmış yardım: bu bir öğretim metnidir; bir değerlendirme ya da ilerleme bilgisi değildir."

    const val DISCLOSURE_AFTER_ANSWER =
        "Gönderdiğin cevap bundan etkilenmez. Bu sorunun çözümünü görmüş olacağın için, bu yetkinlik ileride görmediğin yeni bir soruyla doğrulanır."

    const val CONVERTED_TO_LEARNING =
        "Bu soru artık öğrenme için kullanılıyor; bağımsız ölçüm daha sonra görmediğin bir soruyla yapılır. Bu bir ihlal değil."

    const val FAST_PATH_ENDED =
        "Bu bölüm için hızlı yol burada bitiyor ve normal öğrenmeyle devam ediliyor. Yardım istemek cezalandırılmaz."

    const val UNAVAILABLE = "AI yardımı şu an kullanılamıyor. Hiçbir şey kaydedilmedi; hazırlanmış yardım varsa onu kullanabilirsin."
    const val REFUSED = "AI bu isteğe cevap vermedi. Bu senin hatan değil ve hiçbir şey kaydedilmedi."
    const val TIMED_OUT = "AI zamanında cevap vermedi. Hiçbir şey kaydedilmedi; istersen yeniden sorabilirsin."
    const val TRANSPORT_ERROR = "AI'ya ulaşılamadı. Hiçbir şey kaydedilmedi; istersen yeniden sorabilirsin."
    const val INVALID_RESPONSE = "AI'nın cevabı kullanılamadığı için gösterilmedi. Hiçbir şey kaydedilmedi."

    const val REDIRECT_NO_ITEM = "Şu an üzerinde çalışılan bir soru yok; konuyu başka türlü anlatmasını isteyebilirsin."
    const val REDIRECT_NO_ANSWER_YET = "Henüz gönderilmiş bir cevap yok; istersen bir ipucu isteyebilirsin."
    const val REDIRECT_ANSWER_FROZEN = "Cevabın gönderildi; şimdi cevabını açıklamasını isteyebilir ya da bir soru sorabilirsin."
    /** The screen offers the redirect's own intent as the next action; this sentence only says why. */
    const val REDIRECT_TARGET_SEGMENT = "Bu ifade görevin öğrenme hedefi; anlamını açmak cevabı vermek olur."

    const val MISSING_QUESTION = "Sorunu yaz."
    const val MISSING_FROZEN_ANSWER = "Açıklanacak gönderilmiş bir cevap bulunamadı."
    const val MISSING_SEGMENT = "Anlamını merak ettiğin ifadeyi seç."
}
