package coach.presentation

import coach.model.ComprehensionOffer
import coach.model.ComprehensionResult
import coach.model.IndependenceClass
import coach.model.TutorOutcome

/**
 * What is said around the comprehension check (14E, `ACCX-v0 / D-109`).
 *
 * The offer names what happened without accusing anyone, and skipping is said to be fine. A written answer is judged
 * plainly, with the right choice shown, and always with the reminder that explaining code is not writing it. The
 * tutor's question is labelled as practice. When the learner did not write the code, the fresh independent check the
 * planner will schedule is said not to be a penalty. No score, count or percentage. Wording is working microcopy.
 */
object ComprehensionCopy {
    const val OFFER = "Bu kodu tek başına yazmadın. İstersen birkaç soruyla ne kadarını anladığına bakalım; geçmek yanlış sayılmaz."
    const val TUTOR_OFFER = "Bu görev için hazırlanmış soru yok. İstersen AI kodun hakkında bir soru sorsun; bu yalnız pratik içindir."
    const val CORRECT = "Doğru."
    const val NOT_CORRECT = "Bu seçenek doğru değil."
    const val RIGHT_CHOICE = "Doğru seçenek:"
    const val SKIPPED = "Geçtin; bu yanlış sayılmadı."
    const val NOT_PRODUCTION = "Bu bir anlama kontrolü: kodu açıklayabildiğini gösterir, kendin yazabildiğini değil."
    const val TUTOR_PRACTICE = "AI tarafından yazılmış bir soru: pratik içindir, kanıt sayılmaz."
    const val RECHECK = "Kod yazma hedefin için ileride yeni bir görevle bağımsız bir kontrol planlanacak. Bu bir ceza değil."
}

object ComprehensionPresentation {

    /** The offer, or nothing when the learner wrote the code themselves. */
    fun offer(offer: ComprehensionOffer): String? = when (offer) {
        ComprehensionOffer.NotOffered -> null
        is ComprehensionOffer.Written -> ComprehensionCopy.OFFER
        ComprehensionOffer.TutorPractice -> ComprehensionCopy.TUTOR_OFFER
    }

    /** What is said after one written check. */
    fun result(result: ComprehensionResult): List<String> = when (result) {
        ComprehensionResult.Skipped -> listOf(ComprehensionCopy.SKIPPED)
        is ComprehensionResult.Measured -> buildList {
            if (result.correct) {
                add(ComprehensionCopy.CORRECT)
            } else {
                add(ComprehensionCopy.NOT_CORRECT)
                add("${ComprehensionCopy.RIGHT_CHOICE} ${result.check.answer}) ${result.check.choices.getValue(result.check.answer)}")
            }
            add(ComprehensionCopy.NOT_PRODUCTION)
        }
    }

    /** Beside the tutor's question or its response: always labelled as practice. */
    fun practiceNotes(outcome: TutorOutcome.Shown): List<String> = listOf(ComprehensionCopy.TUTOR_PRACTICE) + TutorPresentation.notes(outcome)

    /** Said once after the submission, when the code the learner did not write opens a fresh independent check. */
    fun recheckNote(independence: IndependenceClass): String? =
        ComprehensionCopy.RECHECK.takeIf { independence == IndependenceClass.REQUIRES_INDEPENDENT_RECHECK }
}
