package coach.model

/**
 * What a day recorded (11E).
 *
 * The end of a day is a **boundary in time, not a verdict**. A day closes because the learner-local
 * study day changed, never because the learner finished or failed to finish something, and nothing
 * about their standing changes when it does.
 *
 * Everything here is either an inventory of what was really written (`SPWX-v0` §counting_rules:
 * counts are allowed, labelled as inventory, and never divided into a ratio) or a change some
 * canonical engine actually reported. There is no field that could hold a streak, a percentage, a
 * completion fraction, minutes studied or a debt.
 */

/** What can be counted for a day, each tied to the truth table it is counted from. */
enum class DayRecordKind(val id: String, val truthTable: String) {
    ATTEMPTS_RECORDED("attempts_recorded", "attempt"),
    ITEMS_SEEN("items_seen", "exposure_record"),
    CHECKPOINTS_SAVED("checkpoints_saved", "resume_checkpoint"),
    EVIDENCE_INTERPRETED("evidence_interpreted", "evidence_event"),
}

/**
 * A labelled inventory of the day. It is explicitly **not** progress: counting what was written is
 * not the same as claiming what it proved, and there is deliberately no total, ratio or percentage
 * here to be mistaken for one.
 */
data class DayInventory(val counts: Map<DayRecordKind, Int>) {
    init {
        require(counts.values.none { it < 0 }) { "an inventory cannot count below zero" }
    }

    val isLabelledInventory: Boolean get() = true

    fun countOf(kind: DayRecordKind): Int = counts[kind] ?: 0

    /** Whether anything at all was recorded. Not a success test — only the empty/non-empty split. */
    val recordedAnything: Boolean get() = counts.values.any { it > 0 }
}

/**
 * One thing that actually changed, in `SPWX-v0`'s `learning_history` event families. A day summary
 * may only carry a change a canonical engine reported (12): activity is not a change.
 */
enum class HistoryEventFamily(val id: String) {
    LEARNING("learning"),
    ASSESSMENT("assessment"),
    REVIEW("review"),
    REMEDIATION("remediation"),
}

data class DayChange(val family: HistoryEventFamily, val statement: String, val subject: VersionedRef?) {
    init {
        require(statement.isNotBlank()) { "a recorded change says what changed" }
    }
}

/**
 * What one study day holds. [studyDay] is the learner-local day from `DDM-v0`'s three-value time —
 * the day a row **says** it belongs to, never one recomputed from its instant, because recomputing
 * it is how a DST change or a flight silently moves work from one day to another.
 */
data class DayRecord(
    val studyDay: String,
    val inventory: DayInventory,
    val changes: List<DayChange> = emptyList(),
    /** Open learning needs are current state, owned by the planner (12); `null` while none exists. */
    val openNeedCount: Int? = null,
) {
    init {
        require(STUDY_DAY.matches(studyDay)) { "a day is an ISO local date: $studyDay" }
    }

    private companion object {
        val STUDY_DAY = Regex("""\d{4}-\d{2}-\d{2}""")
    }
}

/**
 * The day boundary itself.
 *
 * A new study day starts empty. Nothing is carried across it — not an unfinished plan, not a
 * counter, not an obligation — because `SRR-v0` is explicit that a day away is not a debt and an
 * unstarted task is not homework. There is no function here that could carry one.
 */
object DayBoundary {

    fun startOf(studyDay: String): DayRecord = DayRecord(studyDay, DayInventory(emptyMap()))

    /** Whether a row recorded on [rowStudyDay] belongs to [studyDay]. Only the recorded day decides. */
    fun belongsTo(rowStudyDay: String, studyDay: String): Boolean = rowStudyDay == studyDay

    /**
     * The next day, which is a fresh one. The previous day is history and is neither summed into the
     * new one nor charged against it.
     */
    fun rollOver(previous: DayRecord, nextStudyDay: String): DayRecord {
        require(nextStudyDay > previous.studyDay) { "a study day does not roll backwards" }
        return startOf(nextStudyDay)
    }
}

/**
 * Days as history (`SPWX-v0` §learning_history). It records what changed — it is not a streak
 * calendar, a contribution graph or an attendance heatmap.
 */
object DayHistory {

    /**
     * The days worth showing: the ones that actually recorded something or reported a change.
     *
     * Days in between are **not** filled in. A gap is not a missed obligation and not a failure, so
     * it is not drawn at all — an empty square in a grid is exactly how absence becomes a
     * scoreboard.
     */
    fun entries(records: List<DayRecord>): List<DayRecord> =
        records.filter { it.inventory.recordedAnything || it.changes.isNotEmpty() }
            .sortedByDescending { it.studyDay }
}
