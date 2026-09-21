package coach.application

import coach.model.DayInventory
import coach.model.DayRecord
import coach.model.DayRecordKind
import coach.ports.ClockPort
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
) {
    data class Loaded(val record: DayRecord, val unreadKinds: Set<String>)

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
            // Changes stay empty: only a canonical engine may report one, and none exists yet (12).
            record = DayRecord(studyDay = studyDay, inventory = DayInventory(counts)),
            unreadKinds = unread,
        )
    }
}
