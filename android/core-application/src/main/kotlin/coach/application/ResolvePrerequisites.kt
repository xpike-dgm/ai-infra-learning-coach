package coach.application

import coach.engines.PrerequisiteEngine
import coach.model.MasteryAxisState
import coach.model.MetadataProblem
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteDecision
import coach.model.ReadinessInputs
import coach.model.RetentionAxis
import coach.model.SkillReadiness
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord

/**
 * Asking the prerequisite gate about one candidate (12B).
 *
 * Readiness is computed from the axes their own engines last wrote, at the moment of asking, so a
 * decision is never older than the state it names. It only reads: deciding is not the same as
 * planning, and what to do about a blocked candidate is the planner's (12C).
 */
class ResolvePrerequisites(private val persistence: PersistencePort) {

    fun resolve(candidate: PrerequisiteCandidate): PrerequisiteDecision {
        val requirements = PrerequisiteEngine.requirements(
            candidate = candidate,
            edgesIntoTarget = persistence.prerequisiteEdgesInto(candidate.target),
            isPublished = { persistence.skill(it) != null },
        )
        val cyclic = PrerequisiteEngine.onCycle(candidate.target) { persistence.prerequisiteEdgesInto(it) }
        val cycle = "${MetadataProblem.PREREQUISITE_CYCLE.id}:${candidate.target}"
        val checked = if (cyclic) requirements.copy(problems = requirements.problems + cycle) else requirements
        val skills = checked.requirements.map { it.skill }.distinct()
        return PrerequisiteEngine.decide(
            candidate = candidate,
            requirements = checked,
            readiness = skills.associateWith { readinessOf(it) },
            criticalPrerequisite = { persistence.skill(it)?.criticalPrerequisite == true },
        )
    }

    /** One Skill's readiness as a prerequisite, from the projection row its axes live on. */
    fun readinessOf(skill: VersionedRef): SkillReadiness =
        PrerequisiteEngine.readiness(inputsFrom(skill, persistence.readProjection(skillStateKey(skill))))

    internal companion object {
        fun skillStateKey(skill: VersionedRef) = "skill_state:${skill.logicalId}@v${skill.version}"

        /**
         * The axes as their engines wrote them. A value an engine has not written yet reads as
         * "not evaluated" — never as a good or a bad answer — and remediation counts as open only
         * when the weakness axis actually says `remediation_required`.
         */
        fun inputsFrom(skill: VersionedRef, row: ProjectionRecord?): ReadinessInputs {
            val axes = row?.payload.orEmpty()
            val weakness = axes["weakness_axis_state"]
            return ReadinessInputs(
                skill = skill,
                mastery = MasteryAxisState.entries.firstOrNull { it.id == axes["mastery_axis_state"] },
                retention = RetentionAxis.of(axes["retention_axis_state"]),
                remediationRequired = when (weakness) {
                    null, "", NOT_YET_EVALUATED -> null
                    REMEDIATION_REQUIRED -> true
                    else -> false
                },
                snapshotRef = row?.let { "${skillStateKey(skill)}#watermark=${it.truthWatermark}" },
            )
        }

        /** What an axis says before its engine has ever written it (12A's carried value). */
        const val NOT_YET_EVALUATED = "not_yet_evaluated"

        /** `SPWX-v0`'s state for open remediation: the one weakness value that makes a Skill unusable. */
        const val REMEDIATION_REQUIRED = "remediation_required"
    }
}

/**
 * Rebuilding one Skill's `prerequisite_readiness` projection (12B) — the one state family `PRG-v0`
 * owns (`MSBX-v0`).
 *
 * It writes nothing else. In particular it does not write the prerequisite axis on `skill_state`:
 * that row carries four engines' axes under one watermark, and an engine that wrote its own axis
 * there under its own watermark would make another engine's stale axis look current. Assembling
 * that row belongs where recomputation is orchestrated (12D), under one watermark read.
 */
class RebuildReadiness(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Rebuilt(val readiness: SkillReadiness, val written: Boolean)

    fun rebuild(skill: VersionedRef): Rebuilt {
        val row = persistence.readProjection(ResolvePrerequisites.skillStateKey(skill))
        val readiness = PrerequisiteEngine.readiness(ResolvePrerequisites.inputsFrom(skill, row))
        // Nothing published means nothing to pin the row to; it is not written at all.
        val version = persistence.latestCurriculumVersion() ?: return Rebuilt(readiness, written = false)
        persistence.writeProjection(
            ProjectionRecord(
                key = "prerequisite_readiness:${skill.logicalId}@v${skill.version}",
                policyVersion = PrerequisiteEngine.PREREQUISITE_POLICY_VERSION,
                // Readiness is a function of the axes on that row, so it has seen exactly what the
                // row had seen. With no row it has seen no truth at all, and says so.
                truthWatermark = row?.truthWatermark ?: 0L,
                builtAtInstant = clock.now().instantEpochMillis,
                inputCurriculumVersion = version,
                payload = mapOf("state" to readiness.readiness.id),
            )
        )
        return Rebuilt(readiness, written = true)
    }
}
