package coach.application

import coach.engines.MasteryEngine
import coach.model.EvidenceRow
import coach.model.MasteryAxisState
import coach.model.ObjectiveGateProfile
import coach.model.VersionedRef

/**
 * The mastery engine's decision after every evidence row of one Skill (13C, shared since 13D).
 *
 * Retention and weakness both need to know whether the Skill was mastered before and after each row, and both must
 * get the **same** answer the stored mastery axis has. Since 16B (`D-123`) that answer is [MasteryEngine.replay] —
 * mastery decided from the evidence itself, in recording order — which [RebuildMastery] writes too, so the timeline
 * and the stored axis cannot drift apart, and rebuilding twice gives the same answer.
 */
internal object MasteryTimeline {

    /** Historical mastery is kept while a contradiction is being verified (`GRE-v0`, `RVR-v0` §9). */
    val MASTERED = setOf(MasteryAxisState.CONFIRMED_CURRENT, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)

    /** One row with the Skill's mastery before and after it. */
    data class Step(val objective: VersionedRef, val row: EvidenceRow, val masteredBefore: Boolean, val masteredAfter: Boolean)

    /** [rows] are each Objective's evidence paired with its Objective, in recording order. */
    fun of(skill: VersionedRef, profiles: List<ObjectiveGateProfile>, rows: List<Pair<VersionedRef, EvidenceRow>>): List<Step> {
        val decisions = MasteryEngine.replay(skill, profiles, rows)
        val sequences = rows.map { it.second.sequence }.distinct().sorted()
        val afterSequence = sequences.withIndex().associate { (index, sequence) -> sequence to decisions[index + 1] }
        val beforeSequence = sequences.withIndex().associate { (index, sequence) -> sequence to decisions[index] }
        return rows.map { (objective, row) ->
            Step(objective, row, beforeSequence.getValue(row.sequence).axisState in MASTERED,
                afterSequence.getValue(row.sequence).axisState in MASTERED)
        }
    }

    /** A Skill's evidence across its Objectives, each row once, in recording order. */
    fun rowsOf(persistence: coach.ports.PersistencePort, profiles: List<ObjectiveGateProfile>): List<Pair<VersionedRef, EvidenceRow>> =
        profiles.flatMap { p -> persistence.evidenceFor(p.ref).map { p.ref to it } }
            .distinctBy { it.second.id }
            .sortedBy { it.second.sequence }
}
