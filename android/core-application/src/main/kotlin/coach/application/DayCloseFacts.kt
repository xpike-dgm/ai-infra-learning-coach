package coach.application

import coach.model.DayInventory
import coach.model.DayRecord
import coach.model.DayRecordKind
import coach.model.HistoryFacts
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * Reads what one study day actually recorded (11E).
 *
 * It only counts. It interprets nothing, ranks nothing and decides nothing about the learner: what
 * the day's attempts proved is the evidence pipeline's (12), and whether the day was "good" is not
 * a question this product asks.
 *
 * The day comes from the clock, and every count is taken against the **row's own study day**, so a
 * process that stayed open past midnight reads the new day rather than yesterday's — the same
 * defect 11A closed for Today.
 */
class DayCloseFacts(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
    // 17E: names each changed Skill; without it a change is still read, and the Skill is named by its published name.
    private val content: ContentPort? = null,
) {
    /**
     * [changes] (17E, `D-138`) is what the engines changed on this day, replayed from the stored evidence exactly as the
     * learning history replays it ([LearningHistoryQuery.day]) — `null` when it could not be read, which is not the same
     * claim as "nothing changed".
     */
    data class Loaded(val record: DayRecord, val unreadKinds: Set<String>, val changes: HistoryFacts? = null)

    fun load(studyDay: String = clock.now().studyDay): Loaded {
        val counts = mutableMapOf<DayRecordKind, Int>()
        val unread = mutableSetOf<String>()
        DayRecordKind.entries.forEach { kind ->
            // A kind that cannot be read is reported as unread, never as a zero: a missing count
            // and a count of nothing are different claims about the day.
            runCatching { persistence.countTruth(kind.truthTable, studyDay) }
                .onSuccess { counts[kind] = it }
                .onFailure { unread += kind.id }
        }
        return Loaded(
            // The record carries no change of its own: a change is the engines', read below and worded in core-presentation.
            record = DayRecord(studyDay = studyDay, inventory = DayInventory(counts)),
            unreadKinds = unread,
            changes = runCatching { LearningHistoryQuery(persistence, content).day(studyDay) }.getOrNull(),
        )
    }
}
