package coach.model

/**
 * The first-run setup (18A, `D-139`; `UXIA-v0` §7.4 `initial_setup_flow`): the minimum local setup before Today.
 *
 * One row records that the learner went through it — finishing it or skipping it — so it is shown once, and a restored
 * backup carries that it was done. Nothing else is decided here: the daily length is a 16D preference, and "I already
 * know some of this" opens a diagnostic (`VDW-v0` §3 `curriculum_entry_placement`) that is never evidence and never
 * mandatory.
 */
enum class SetupOutcome(val id: String) {
    COMPLETED("completed"),
    SKIPPED("skipped"),
    ;

    companion object {
        fun of(id: String): SetupOutcome? = entries.firstOrNull { it.id == id }
    }
}

data class SetupRecord(val outcome: SetupOutcome, val studyDay: String, val sequence: Long)
