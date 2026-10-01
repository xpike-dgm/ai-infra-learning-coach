package coach.presentation

import coach.model.AssistanceSource
import coach.model.ExplanationForm
import coach.model.ExplanationOption
import coach.model.ExplanationSource
import coach.model.TutorOutcome

/**
 * The "explain it another way" menu and what is said beside an alternative explanation (14C, `ALEX-v0 / D-107`).
 *
 * The learner chooses (user decision): the menu lists only forms that have somewhere to come from, in a fixed order,
 * and marks those already seen this session — it ranks nothing and hides nothing. A misconception contrast is offered as
 * a common mix-up, never as "your mistake". A tutor-written explanation is always labelled as unverified, and the
 * course's own explanation is always one action away. Wording is working microcopy.
 */
object AlternativeExplanationCopy {

    fun form(form: ExplanationForm): String = when (form) {
        ExplanationForm.PLAIN_RETEACH -> "Daha sade anlat"
        ExplanationForm.DIFFERENT_EXAMPLE -> "Başka bir örnekle anlat"
        ExplanationForm.WORKED_EXAMPLE -> "Çözümlü bir örnek göster"
        ExplanationForm.STATE_TRACE -> "Adım adım izleyerek göster"
        ExplanationForm.PREREQUISITE_REFRESH -> "Önce gereken konuyu hatırlat"
        ExplanationForm.MISCONCEPTION_CONTRAST -> "Sık yapılan bir karışıklıkla karşılaştır"
        ExplanationForm.CANONICAL -> "Asıl anlatıma dön"
    }

    const val MENU_PROMPT = "Nasıl anlatılsın? İstediğini seç; hangisini seçersen seç, bu bir değerlendirme değildir."
    const val SEEN = "bu oturumda gösterildi"
    const val FROM_AI = "AI ile"
    const val WRITTEN_LABEL = "Hazırlanmış anlatım: dersin doğrulanmış içeriğidir."
    const val AI_LABEL =
        "AI tarafından üretildi: dersin doğrulanmış içeriği değildir. Bir şey asıl anlatımla çelişiyor görünürse asıl anlatım geçerlidir."
    const val BACK_TO_CANONICAL = "Asıl anlatıma dön"
    const val NOTHING_TRUE_TO_SHOW = "Bu biçim için hazırlanmış bir anlatım yok; asıl anlatıma dönebilir ya da başka bir biçim seçebilirsin."
}

object AlternativeExplanationPresentation {

    /** One menu line: the form's label, where it comes from when that is the tutor, and whether it was seen already. */
    fun line(option: ExplanationOption): String = buildString {
        append(AlternativeExplanationCopy.form(option.form))
        if (option.source == ExplanationSource.AI) append(" · ").append(AlternativeExplanationCopy.FROM_AI)
        if (option.seenThisSession) append(" · ").append(AlternativeExplanationCopy.SEEN)
    }

    /**
     * What is said beside a shown alternative: whether it is verified, and — for every alternative — the way back to the
     * course's own explanation. The 14A notes (AI label, a conversion, an ended fast path) still apply and come after.
     */
    fun notes(outcome: TutorOutcome.Shown): List<String> = buildList {
        add(if (outcome.source == AssistanceSource.AI_GENERATED) AlternativeExplanationCopy.AI_LABEL else AlternativeExplanationCopy.WRITTEN_LABEL)
        add(AlternativeExplanationCopy.BACK_TO_CANONICAL)
        addAll(TutorPresentation.notes(outcome).drop(1))
    }

    /** Nothing fits and the tutor may not write it: said plainly, with the way back. */
    fun nothingToShow(): String = AlternativeExplanationCopy.NOTHING_TRUE_TO_SHOW
}
