package coach.application

import coach.model.EvidenceQualifiers
import coach.model.EvidenceRow
import coach.model.EvidenceSummary
import coach.model.MisconceptionContext
import coach.model.NamedEntity
import coach.model.ObjectiveProgress
import coach.model.PrerequisiteContext
import coach.model.ProgressFacts
import coach.model.SkillDetailFacts
import coach.model.SkillPresentationProjection
import coach.model.SkillProgressRow
import coach.model.SkillRow
import coach.model.TechnicalEnglishAnchors
import coach.model.VersionedRef
import coach.model.WeaknessSignal
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * Reads what the store can truthfully say about every published Skill (16A, `SKAX-v0`; `SPWX-v0` §§7–8).
 *
 * It only reads. The axes are taken as their engines wrote them; two things are asked fresh instead of trusted from the
 * row. The prerequisite axis is asked of the gate now, because a prerequisite Skill that changed does not re-assemble
 * its dependents (18E). And the Skill's evidence is read, so a row built before the newest evidence is reported as
 * recomputing rather than as current (`SPWX-v0` §16), and evidence no engine has read yet is never shown as "nothing
 * yet". One function, [SkillPresentationProjection.derive], decides the state for this read and for the stored
 * column alike.
 */
class ProgressQuery(
    private val persistence: PersistencePort,
    private val content: ContentPort? = null,
) {
    fun load(): ProgressFacts {
        val curriculumLoaded = persistence.curriculumPublished()
        if (!curriculumLoaded) return ProgressFacts(curriculumLoaded = false, skills = emptyList())
        val gate = ResolvePrerequisites(persistence)
        return ProgressFacts(
            curriculumLoaded = true,
            skills = persistence.publishedSkills().map { skill -> row(skill, gate, evidenceOf(skill.ref)) },
        )
    }

    /** `skill_detail`: the same row as the overview, with its Objectives, evidence and placement. `null` if unpublished. */
    fun detail(skill: VersionedRef): SkillDetailFacts? {
        val published = persistence.skill(skill) ?: return null
        val evidence = evidenceOf(skill)
        val row = row(published, ResolvePrerequisites(persistence), evidence)
        val objectives = persistence.objectivesOf(skill).map { objective ->
            val rows = evidence[objective.ref].orEmpty()
            ObjectiveProgress(
                objective = objective.ref,
                required = objective.required,
                gatesPassed = when (persistence.readProjection(objectiveStateKey(objective.ref))?.payload?.get("state")) {
                    "passed" -> true
                    "not_passed" -> false
                    else -> null
                },
                weakness = persistence.readProjection(WeaknessRows.key(objective.ref))?.payload?.get("state")
                    ?.let { id -> WeaknessSignal.entries.firstOrNull { it.id == id } },
                evidence = EvidenceSummary.of(rows),
                misconceptions = misconceptionsOf(objective.ref),
            )
        }
        val named = (row.prerequisite.hardBlockers + row.prerequisite.uncertain + row.prerequisite.softGaps).distinct()
            .associateWith { persistence.skill(it)?.canonicalName ?: it.logicalId }
        return SkillDetailFacts(
            row = row,
            capabilityStatement = published.capabilityStatement,
            objectives = objectives,
            evidence = EvidenceSummary.of(evidence.values.flatten()),
            skillNames = named,
            topics = topicsOf(skill),
            englishBand = TechnicalEnglishAnchors.bandOf(skill.logicalId),
        )
    }

    /**
     * 16C: the catalogued misconceptions 14B's memory holds a signal for on [objective] — open or resolved — as it wrote
     * them. A label with no stored row, or one stored as `none`, is not shown: the memory has nothing to say about it.
     */
    private fun misconceptionsOf(objective: VersionedRef): List<MisconceptionContext> =
        persistence.misconceptionsOf(objective).mapNotNull { m ->
            val signal = persistence.readProjection(MisconceptionRows.key(m.ref))?.payload?.get("state")
                ?.let { id -> WeaknessSignal.entries.firstOrNull { it.id == id } }
            signal?.takeIf { it != WeaknessSignal.NONE }?.let { MisconceptionContext(m.ref, m.name, m.openQuestion, it) }
        }

    private fun evidenceOf(skill: VersionedRef): Map<VersionedRef, List<EvidenceRow>> =
        persistence.objectivesOf(skill).associate { it.ref to persistence.evidenceFor(it.ref) }

    private fun row(skill: SkillRow, gate: ResolvePrerequisites, evidence: Map<VersionedRef, List<EvidenceRow>>): SkillProgressRow {
        val stored = persistence.readProjection(ResolvePrerequisites.skillStateKey(skill.ref))
        val rows = evidence.values.flatten()
        val decision = gate.resolve(AssembleSkillState.candidate(skill.ref))
        val reading = SkillStateRows.reading(
            payload = stored?.payload.orEmpty(),
            evidenced = rows.isNotEmpty(),
            // Evidence recorded after the row was built: the row speaks for older truth. No row and some evidence is
            // the same thing — the mastery axis reads as unwritten and the derivation says so.
            projectionStale = stored != null && rows.any { it.sequence > stored.truthWatermark },
            evidenceQualifiers = EvidenceQualifiers.of(evidence.values),
            prerequisite = decision.eligibility,
        )
        return SkillProgressRow(
            skill = skill.ref,
            name = skill.canonicalName,
            projection = SkillPresentationProjection.derive(reading),
            prerequisite = PrerequisiteContext(
                eligibility = decision.eligibility,
                hardBlockers = decision.hardBlockerSkills,
                uncertain = decision.uncertainSkills,
                softGaps = decision.softGapSkills,
                metadataProblems = decision.metadataProblems,
            ),
        )
    }

    /** Where the curriculum places the Skill, for orientation only: placement is never a prerequisite (`SPWX-v0` §6). */
    private fun topicsOf(skill: VersionedRef): List<NamedEntity> {
        val packages = content?.curriculumPackages().orEmpty()
        val topicRefs = packages.flatMap { it.topicSkillLinks }.filter { it.skill == skill }.map { it.topic }.toSet()
        return packages.flatMap { it.topics }.filter { VersionedRef(it.logicalId, it.version) in topicRefs }
            .distinctBy { it.logicalId to it.version }
    }

    private fun objectiveStateKey(objective: VersionedRef) = "objective_state:${objective.logicalId}@v${objective.version}"
}
