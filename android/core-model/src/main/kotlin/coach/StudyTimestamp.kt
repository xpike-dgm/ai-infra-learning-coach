package coach.model

/**
 * DDM-v0: three-value time. After a DST change or travel, neither value can be derived
 * reliably from the other, so every timestamped row stores all three.
 *
 * - [instantEpochMillis]: the absolute instant
 * - [studyDay]: the learner-local study day the event belongs to (ISO-8601 local date)
 * - [utcOffsetSeconds]: the offset in force when it was recorded
 *
 * Nothing in core reads the system clock; time arrives through ClockPort (MSBX-v0).
 */
data class StudyTimestamp(
    val instantEpochMillis: Long,
    val studyDay: String,
    val utcOffsetSeconds: Int,
) {
    init {
        require(STUDY_DAY.matches(studyDay)) { "studyDay must be an ISO local date: $studyDay" }
    }

    companion object {
        private val STUDY_DAY = Regex("""\d{4}-\d{2}-\d{2}""")
    }
}
