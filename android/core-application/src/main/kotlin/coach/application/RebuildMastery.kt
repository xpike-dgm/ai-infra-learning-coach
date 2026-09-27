package coach.application

import coach.engines.MasteryEngine
import coach.model.MasteryAxisState
import coach.model.ObjectiveGateProfile
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord

/**
 * Recomputing mastery from evidence (12A).
 *
 * The projection is **rebuilt, never edited**. Everything it says is a function of the evidence rows
 * that exist right now, so dropping the whole projection and running this again produces the same
 * answer — which is what `LFPS-v0` means by a projection being rebuildable, and what stops mastery
 * from quietly becoming a second source of truth that drifts from its evidence.
 *
 * It writes only the state families the mastery engine owns (`MSBX-v0`): the mastery axis on
 * `skill_state` and the Objective's own state. Retention, readiness, topic and weakness belong to
 * other engines and are read-only here — the row keeps whatever they last wrote.
 */
class RebuildMastery(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Rebuilt(
        val skill: VersionedRef,
        val decision: MasteryEngine.SkillDecision,
        val truthWatermark: Long,
    )

    /**
     * Rebuilds one Skill from its Objectives' evidence.
     *
     * The watermark and curriculum version are read **before** the evidence, so a projection can
     * never claim to have seen more than it did: if a write lands during the rebuild, the row is
     * stamped with the older watermark and is detectably stale rather than silently wrong.
     */
    fun rebuild(skill: VersionedRef, profiles: List<ObjectiveGateProfile>): Rebuilt {
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion()
        val previous = previousAxis(skill)

        val allRows = profiles.flatMap { persistence.evidenceFor(it.ref) }
        val decisions = profiles.map { profile ->
            val rows = persistence.evidenceFor(profile.ref)
            MasteryEngine.decide(
                profile = profile,
                rows = rows,
                previouslyMastered = previous == MasteryAxisState.CONFIRMED_CURRENT ||
                    previous == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE,
                unresolvedVerification = previous == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE,
            )
        }
        val decision = MasteryEngine.decideSkill(
            skill = skill,
            decisions = decisions,
            profiles = profiles.associateBy { it.ref },
            previouslyMastered = previous == MasteryAxisState.CONFIRMED_CURRENT,
            allRows = allRows,
        )

        val builtAt = clock.now().instantEpochMillis
        // No curriculum published means nothing to pin the projection to, so it is not written at
        // all rather than pinned to a version that does not exist.
        val version = curriculumVersion ?: return Rebuilt(skill, decision, watermark)

        val existing = persistence.readProjection(skillKey(skill))
        persistence.writeProjection(
            ProjectionRecord(
                key = skillKey(skill),
                policyVersion = MasteryEngine.MASTERY_FORMULA_VERSION,
                truthWatermark = watermark,
                builtAtInstant = builtAt,
                inputCurriculumVersion = version,
                payload = buildMap {
                    put("mastery_axis_state", decision.axisState.id)
                    // The other three axes are other engines' and are carried, never decided here.
                    put("retention_axis_state", existing?.payload?.get("retention_axis_state") ?: UNKNOWN)
                    put("prerequisite_axis_state", existing?.payload?.get("prerequisite_axis_state") ?: UNKNOWN)
                    put("weakness_axis_state", existing?.payload?.get("weakness_axis_state") ?: UNKNOWN)
                    // The presentation state is derived by SPWX-v0's precedence from all four axes,
                    // which needs axes this engine does not own; until those engines exist (12B, 13),
                    // the mastery axis is reported as-is rather than a presentation state being
                    // invented from one axis and presented as the whole truth.
                    put("primary_presentation_state", decision.axisState.id)
                },
            )
        )
        decisions.forEach { objectiveDecision ->
            persistence.writeProjection(
                ProjectionRecord(
                    key = objectiveKey(objectiveDecision.objective),
                    policyVersion = MasteryEngine.MASTERY_FORMULA_VERSION,
                    truthWatermark = watermark,
                    builtAtInstant = builtAt,
                    inputCurriculumVersion = version,
                    payload = mapOf("state" to if (objectiveDecision.passed) "passed" else "not_passed"),
                )
            )
        }
        return Rebuilt(skill, decision, watermark)
    }

    private fun previousAxis(skill: VersionedRef): MasteryAxisState? =
        persistence.readProjection(skillKey(skill))
            ?.payload?.get("mastery_axis_state")
            ?.let { id -> MasteryAxisState.entries.firstOrNull { it.id == id } }

    private fun skillKey(skill: VersionedRef) = "skill_state:${skill.logicalId}@v${skill.version}"

    private fun objectiveKey(objective: VersionedRef) =
        "objective_state:${objective.logicalId}@v${objective.version}"

    private companion object {
        /** What an axis this engine does not own says before its engine has ever written it. */
        const val UNKNOWN = "not_yet_evaluated"
    }
}
