package coach.application

import coach.engines.MisconceptionEngine
import coach.model.ObjectiveGateProfile
import coach.model.VersionedRef
import coach.model.WrongAnswerFinding
import coach.ports.PersistencePort

/**
 * What one wrong answer said (14B, `WAAX-v0 / D-106`), read from truth after its evidence was recorded.
 *
 * The analysis is **the stored attribution, not a second opinion**: the same events the weakness and misconception
 * memory are rebuilt from ([WeaknessEvents]), attributed by the weakness engine's own rules, with each catalog
 * label's state after the row. It writes nothing, asks no AI and sends nothing anywhere; it can only say what the
 * engines already decided.
 */
class AnalyzeWrongAnswer(private val persistence: PersistencePort) {

    fun analyze(skill: VersionedRef, profiles: List<ObjectiveGateProfile>, evidenceIds: Collection<Long>): List<WrongAnswerFinding> {
        val events = WeaknessEvents.of(persistence, skill, profiles)
        return profiles.flatMap { profile ->
            MisconceptionEngine.findings(profile.ref, skill, persistence.misconceptionsOf(profile.ref), events.getValue(profile.ref))
        }.filter { it.evidenceId in evidenceIds }
    }
}
