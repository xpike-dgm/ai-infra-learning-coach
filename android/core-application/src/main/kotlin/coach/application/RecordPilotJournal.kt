package coach.application

import coach.model.PilotJournal
import coach.model.PilotJournalKind
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * The pilot journal (18G, `D-141`; `V1_SUCCESS_CRITERIA` SC-048/049): the learner reports that today's plan needed a
 * correction, or that the app broke one of its rules.
 *
 * It records and does nothing else: the plan is not replanned, no preference changes and nothing is evidence — a report
 * that changed the plan would correct the very thing SC-049 counts. The entry names the plan the learner was looking at,
 * so the PC report reads it against that plan's trace.
 */
class RecordPilotJournal(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    sealed interface Result {
        data class Recorded(val sequence: Long) : Result

        /** The note is longer than [PilotJournal.NOTE_LIMIT] once trimmed; nothing is written. */
        data object NoteTooLong : Result
    }

    fun record(kind: PilotJournalKind, note: String): Result {
        val trimmed = note.trim()
        if (trimmed.length > PilotJournal.NOTE_LIMIT) return Result.NoteTooLong
        val plan = persistence.latestPlan()?.planVersionId
        val payload = buildMap {
            put("entry_kind", kind.id)
            put("note", trimmed)
            plan?.let { put("plan_version_id", it.toString()) }
        }
        val at = clock.now()
        val sequence = persistence.inTransaction { persistence.appendTruth(TruthRecord(KIND, at, payload)) }
        return Result.Recorded(sequence)
    }

    companion object {
        const val KIND = "pilot_journal_entry"
    }
}
