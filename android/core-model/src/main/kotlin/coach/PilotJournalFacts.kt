package coach.model

/**
 * The pilot journal (18G, `D-141`; `V1_SUCCESS_CRITERIA` SC-048/049): the learner's own record that a plan needed a
 * correction, or that the app broke one of its rules.
 *
 * The journal observes and never acts: an entry changes no plan, no preference and no learning state, and it is not
 * evidence. It is read on the PC from a backup, where SC-049 counts the study days with a structural correction and
 * SC-048 lists the rule violations. A personal preference is recorded but never counted as a structural correction —
 * SC-049 says so in its own words.
 */
enum class PilotJournalKind(val id: String, val structural: Boolean, val ruleViolation: Boolean) {
    /** SC-049: "bu görev prerequisite yüzünden imkânsız". */
    PREREQUISITE_IMPOSSIBLE("prerequisite_impossible", structural = true, ruleViolation = false),

    /** SC-049: "süre tamamen yanlış". */
    TIME_WRONG("time_wrong", structural = true, ruleViolation = false),

    /** SC-049: "yanlış dala götürüyor". */
    WRONG_BRANCH("wrong_branch", structural = true, ruleViolation = false),

    /** SC-049: a change for personal preference alone is not a structural correction. */
    PERSONAL_PREFERENCE("personal_preference", structural = false, ruleViolation = false),

    /** SC-048: a hard prerequisite bypassed, progress lost, a critical Skill counted mastered when it is not. */
    RULE_VIOLATION("rule_violation", structural = false, ruleViolation = true),
    ;

    companion object {
        fun of(id: String): PilotJournalKind? = entries.firstOrNull { it.id == id }
    }
}

/**
 * One journal row. [planVersionId] is the plan the learner was looking at when they wrote it, or null when there was
 * none; [note] is their own optional words, empty when they wrote none.
 */
data class PilotJournalEntry(
    val kind: PilotJournalKind,
    val note: String,
    val planVersionId: Long?,
    val studyDay: String,
    val sequence: Long,
) {
    init {
        require(note.length <= PilotJournal.NOTE_LIMIT) { "a journal note is at most ${PilotJournal.NOTE_LIMIT} characters" }
        require(note == note.trim()) { "a journal note is stored trimmed" }
    }
}

object PilotJournal {
    /** A short note, not a diary: the report reads it beside the kind, and the kind is what is counted. */
    const val NOTE_LIMIT = 200
}
