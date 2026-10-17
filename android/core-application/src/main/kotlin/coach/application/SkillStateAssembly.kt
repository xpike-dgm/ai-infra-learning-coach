package coach.application

import coach.model.MasteryAxisState
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteEligibility
import coach.model.RetentionAxis
import coach.model.SkillAxisReading
import coach.model.SkillPresentationProjection
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord

/**
 * `skill_state` as its four axes say it (16A, `SKAX-v0`).
 *
 * Every engine that writes its own axis on the row also writes `primary_presentation_state`, and always the same way:
 * derived by `SPWX-v0`'s one precedence from the axes **as they now stand on the row**. 12A wrote its own mastery
 * value into the presentation column because the other axes had no engine yet; carrying an old presentation past a
 * new axis would make the column claim a state its own row no longer supports. A row whose axes do not yet determine
 * a state keeps [SkillPresentationProjection.NOT_DERIVED] — never a guess.
 */
internal object SkillStateRows {

    const val MASTERY = "mastery_axis_state"
    const val RETENTION = "retention_axis_state"
    const val PREREQUISITE = "prerequisite_axis_state"
    const val WEAKNESS = "weakness_axis_state"
    const val PRESENTATION = "primary_presentation_state"

    /** The four axes of a stored row, read as their engines wrote them; anything unwritten reads as unwritten. */
    fun reading(
        payload: Map<String, String>,
        evidenced: Boolean,
        projectionStale: Boolean = false,
        evidenceQualifiers: Set<coach.model.AttentionQualifier> = emptySet(),
        prerequisite: PrerequisiteEligibility? = prerequisiteOf(payload[PREREQUISITE]),
    ): SkillAxisReading = SkillAxisReading(
        mastery = MasteryAxisState.entries.firstOrNull { it.id == payload[MASTERY] },
        retention = RetentionAxis.of(payload[RETENTION]),
        weakness = WeaknessAxis.entries.firstOrNull { it.id == payload[WEAKNESS] } ?: WeaknessAxis.NOT_YET_EVALUATED,
        prerequisite = prerequisite,
        evidenced = evidenced,
        projectionStale = projectionStale,
        evidenceQualifiers = evidenceQualifiers,
    )

    fun prerequisiteOf(id: String?): PrerequisiteEligibility? = PrerequisiteEligibility.entries.firstOrNull { it.id == id }

    /**
     * The axes with the presentation they determine. A writer runs because the Skill is being recomputed, so it
     * reads the row as evidenced: a missing mastery axis is unwritten, never "nothing happened".
     */
    fun presented(axes: Map<String, String>): Map<String, String> =
        axes + (PRESENTATION to SkillPresentationProjection.storedId(reading(axes, evidenced = true)))
}

/**
 * The last step of recomputing a Skill (16A): the prerequisite axis and the presentation, under the row's own
 * watermark.
 *
 * 12B left the prerequisite axis to "where recomputation is orchestrated, under one watermark read": the gate is asked
 * here, for new work on this exact Skill, from the graph alone (a task's own requirements are the planner's
 * question), and its answer — `PRG-v0` §7's eligibility — becomes `prerequisite_axis_state`. Nothing new is read from
 * truth, so the row's watermark does not move. A Skill no engine has written has no row to assemble.
 *
 * The stored value is the row's answer at the moment it was assembled. A prerequisite Skill that changes later does
 * not re-assemble its dependents (reverse invalidation, 18E), which is why the Progress read asks the gate again
 * rather than trusting this column.
 */
class AssembleSkillState(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Assembled(val prerequisite: PrerequisiteEligibility, val presentation: String)

    fun assemble(skill: VersionedRef): Assembled? {
        val key = ResolvePrerequisites.skillStateKey(skill)
        val row = persistence.readProjection(key) ?: return null
        val eligibility = ResolvePrerequisites(persistence).resolve(candidate(skill)).eligibility
        val payload = SkillStateRows.presented(
            mapOf(
                SkillStateRows.MASTERY to (row.payload[SkillStateRows.MASTERY] ?: ResolvePrerequisites.NOT_YET_EVALUATED),
                SkillStateRows.RETENTION to (row.payload[SkillStateRows.RETENTION] ?: ResolvePrerequisites.NOT_YET_EVALUATED),
                SkillStateRows.PREREQUISITE to eligibility.id,
                SkillStateRows.WEAKNESS to (row.payload[SkillStateRows.WEAKNESS] ?: ResolvePrerequisites.NOT_YET_EVALUATED),
            )
        )
        persistence.writeProjection(
            ProjectionRecord(
                key = key,
                policyVersion = row.policyVersion,
                truthWatermark = row.truthWatermark,
                builtAtInstant = clock.now().instantEpochMillis,
                inputCurriculumVersion = row.inputCurriculumVersion,
                payload = payload,
            )
        )
        return Assembled(eligibility, payload.getValue(SkillStateRows.PRESENTATION))
    }

    internal companion object {
        /** New work on the Skill itself, with nothing but the graph's edges: the question the axis answers. */
        fun candidate(skill: VersionedRef) = PrerequisiteCandidate(candidateId = "skill_state:$skill", target = skill)
    }
}
