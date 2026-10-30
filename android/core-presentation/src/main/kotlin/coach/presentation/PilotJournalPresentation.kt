package coach.presentation

import coach.model.PilotJournal
import coach.model.PilotJournalKind

/**
 * The pilot journal's words (18G, `D-141`; SC-048/049). The row is on Today, beside the plan it is about; the dialog
 * names each kind in SC-049's own terms and says plainly that reporting changes nothing — the plan is not replanned and
 * nothing the learner reports is held against them.
 */
object PilotJournalCopy {
    const val ROW = "Planla ilgili bir sorun bildir"
    const val TITLE = "Planla ilgili ne oldu?"
    const val NOTE = "Bu bildirim yalnız pilot için kaydedilir; planı değiştirmez ve senin hakkında bir şey söylemez."
    const val NOTE_LABEL = "Kısa not (isteğe bağlı)"
    const val SAVE = "Bildirimi kaydet"
    const val CANCEL = "Vazgeç"
    const val SAVED = "Bildirimin kaydedildi. Plan değiştirilmedi."
    const val NOT_SAVED = "Bildirim kaydedilemedi. Plan değiştirilmedi; daha sonra yeniden deneyebilirsin."
    const val PREFERENCE_NOTE = "Kişisel tercih kaydedilir ama yapısal bir düzeltme sayılmaz."

    val kindLabels: Map<PilotJournalKind, String> = mapOf(
        PilotJournalKind.PREREQUISITE_IMPOSSIBLE to "Ön koşul yüzünden imkânsız",
        PilotJournalKind.TIME_WRONG to "Süre tamamen yanlış",
        PilotJournalKind.WRONG_BRANCH to "Yanlış dala götürüyor",
        PilotJournalKind.PERSONAL_PREFERENCE to "Kişisel tercih",
        PilotJournalKind.RULE_VIOLATION to "Kural ihlali gördüm",
    )

    fun noteTooLong(limit: Int) = "Not en çok $limit karakter olabilir."
    fun noteCount(length: Int, limit: Int) = "$length / $limit"
}

object PilotJournalPresentation {

    /** Every kind, in SC-049's order, then the preference that is not counted and the rule violation of SC-048. */
    val kinds: List<PilotJournalKind> = PilotJournalKind.entries

    /** The note as it will be stored, or null when it is too long to store. */
    fun note(typed: String): String? = typed.trim().takeIf { it.length <= PilotJournal.NOTE_LIMIT }

    /** A kind must be chosen and the note must fit; the note itself is optional. */
    fun canSave(kind: PilotJournalKind?, typed: String): Boolean = kind != null && note(typed) != null
}
