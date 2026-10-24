package coach.application

import coach.engines.MasteryEngine
import coach.model.CefrBand
import coach.model.EnglishProfile
import coach.model.EnglishProfileProjection
import coach.model.EnglishProfileReading
import coach.model.EnglishSkillReading
import coach.model.ObjectiveGateProfiles
import coach.model.ProgressFacts
import coach.model.TechnicalEnglishAnchors
import coach.model.VersionedRef
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * Reads the Technical English profile (16C, `PWPX-v0`; `TEPM-v0`). It only reads and writes nothing.
 *
 * The English Skills' states are 16A's rows, the same ones Progress shows, so the profile can never contradict a Skill.
 * The one thing it adds is history: the highest band that was ever complete. That is not remembered in a column — a
 * remembered band would be a second truth that disagrees with the engines the moment a rule changes — but found by
 * replaying the mastery engine over the stored evidence in recording order, as 16B's history does (user decision,
 * 2026-10-24).
 */
class EnglishProfileQuery(
    private val persistence: PersistencePort,
    private val content: ContentPort,
) {
    /** `null` when no curriculum is published: there is no profile to derive, not an empty one. */
    fun load(progress: ProgressFacts = ProgressQuery(persistence, content).load()): EnglishProfile? {
        if (!progress.curriculumLoaded) return null
        val bySkill = progress.skills.associateBy { it.skill.logicalId }
        val skills = TechnicalEnglishAnchors.anchors.map { (id, band) ->
            val row = bySkill[id]
            EnglishSkillReading(
                skillLogicalId = id,
                band = band,
                row = row,
                waived = row != null && persistence.objectivesOf(row.skill).any { CoverageRows.read(persistence, it.ref)?.waiver != null },
            )
        }
        val published = skills.filter { it.row != null }
        return EnglishProfileProjection.derive(
            EnglishProfileReading(
                skills = skills,
                historicalHighest = historicalHighest(published.map { it.row!!.skill }, published.map { it.skillLogicalId }.toSet()),
            )
        )
    }

    /**
     * Every English Skill's mastery decision after each of its own rows, merged on the global truth sequence; after each
     * step the one band rule is asked again, and the highest answer is kept.
     */
    private fun historicalHighest(skills: List<VersionedRef>, published: Set<String>): CefrBand? {
        val steps = skills.flatMap { skill ->
            val profiles = persistence.objectivesOf(skill).map(ObjectiveGateProfiles::of)
            if (profiles.isEmpty()) return@flatMap emptyList()
            val rows = MasteryTimeline.rowsOf(persistence, profiles)
            // `replay` decides once before any evidence and once after each distinct row, in this order.
            val cutoffs = listOf(Long.MIN_VALUE) + rows.map { it.second.sequence }.distinct().sorted()
            cutoffs.zip(MasteryEngine.replay(skill, profiles, rows)) { cutoff, decision -> Triple(cutoff, skill.logicalId, decision.mastered) }
        }
        val mastered = mutableSetOf<String>()
        var highest: CefrBand? = null
        steps.sortedBy { it.first }.groupBy { it.first }.values.forEach { atOnce ->
            atOnce.forEach { (_, id, isMastered) -> if (isMastered) mastered += id else mastered -= id }
            val band = EnglishProfileProjection.highestComplete(mastered, published)
            if (band != null && (highest == null || band > highest!!)) highest = band
        }
        return highest
    }
}
