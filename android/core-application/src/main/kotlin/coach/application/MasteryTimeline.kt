package coach.application

import coach.engines.MasteryEngine
import coach.model.EvidenceRow
import coach.model.MasteryAxisState
import coach.model.ObjectiveGateProfile
import coach.model.VersionedRef

/**
 * The mastery engine's decision after every evidence row of one Skill (13C, shared since 13D).
 *
 * Mastery is path-dependent: a rebuild reads the previous axis, so a contradiction opens verification only
 * after mastery. Retention and weakness both need to know whether the Skill was mastered before and after
 * each row, and both must get the **same** answer — the one a rebuild after that row would have written.
 * This replay mirrors [RebuildMastery] exactly: the previous axis feeds the next decision the same way the
 * stored axis feeds a rebuild.
 */
internal object MasteryTimeline {

    /** Historical mastery is kept while a contradiction is being verified (`GRE-v0`, `RVR-v0` §9). */
    val MASTERED = setOf(MasteryAxisState.CONFIRMED_CURRENT, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)

    /** One row with the Skill's mastery before and after it. */
    data class Step(val objective: VersionedRef, val row: EvidenceRow, val masteredBefore: Boolean, val masteredAfter: Boolean)

    /** [rows] are each Objective's evidence paired with its Objective, in recording order. */
    fun of(skill: VersionedRef, profiles: List<ObjectiveGateProfile>, rows: List<Pair<VersionedRef, EvidenceRow>>): List<Step> {
        val byObjective = profiles.associateBy { it.ref }
        var previous: MasteryAxisState? = null
        return rows.mapIndexed { index, (objective, row) ->
            val prefix = rows.take(index + 1)
            val decisions = profiles.map { profile ->
                MasteryEngine.decide(
                    profile = profile,
                    rows = prefix.filter { it.first == profile.ref }.map { it.second },
                    previouslyMastered = previous in MASTERED,
                    unresolvedVerification = previous == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE,
                )
            }
            val decision = MasteryEngine.decideSkill(
                skill = skill,
                decisions = decisions,
                profiles = byObjective,
                previouslyMastered = previous == MasteryAxisState.CONFIRMED_CURRENT,
                allRows = prefix.map { it.second },
            )
            val before = previous in MASTERED
            previous = decision.axisState
            Step(objective, row, before, decision.axisState in MASTERED)
        }
    }

    /** A Skill's evidence across its Objectives, each row once, in recording order. */
    fun rowsOf(persistence: coach.ports.PersistencePort, profiles: List<ObjectiveGateProfile>): List<Pair<VersionedRef, EvidenceRow>> =
        profiles.flatMap { p -> persistence.evidenceFor(p.ref).map { p.ref to it } }
            .distinctBy { it.second.id }
            .sortedBy { it.second.sequence }
}
