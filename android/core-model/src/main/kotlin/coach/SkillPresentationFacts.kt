package coach.model

/**
 * The learner-facing Skill state (16A, `SKAX-v0`): `SPWX-v0` §4's eight presentation states and `TEPM-v0` §4.1's
 * derivation precedence, generalised to every Skill.
 *
 * It lives in `core-model` because two layers need the very same function: `core-application`, which writes
 * `skill_state.primary_presentation_state` when it assembles the row, and `core-presentation`, which projects the
 * screen. `MSBX-v0` lets neither depend on the other, and two copies of a precedence rule would be two answers for
 * one Skill — the contradiction `SPWX-v0` §8 forbids ("the same Skill opened from Learn and from Progress shows the
 * same state"). `WeaknessAxis.of` set the precedent: a small, total, deterministic derivation beside its vocabulary.
 *
 * Nothing here is a mastery decision. Every axis is read as its own engine wrote it; the derivation only orders them
 * (`SPWX-v0` §5) and never writes back.
 */

/**
 * Exactly eight states (`SPWX-v0` §4.1). The enum order **is** the precedence (`SPWX-v0` §4.2), so resolving a
 * multi-signal Skill is a pick, not a judgement.
 */
enum class SkillPresentationState(val id: String) {
    REMEDIATION_REQUIRED("remediation_required"),
    CONFIRMATION_VERIFICATION_DUE("confirmation_verification_due"),
    CONFIRMED_REVIEW_DUE("confirmed_review_due"),
    CONFIRMED_CURRENT("confirmed_current"),
    PREREQUISITE_UNRESOLVED("prerequisite_unresolved"),
    DEVELOPING_INDEPENDENT("developing_independent"),
    DEVELOPING_WITH_SUPPORT("developing_with_support"),
    NOT_YET_EVIDENCED("not_yet_evidenced"),
    ;

    companion object {
        /** `SPWX-v0` §4.2, in order. */
        val precedence: List<SkillPresentationState> = entries.toList()

        fun resolve(candidates: Set<SkillPresentationState>): SkillPresentationState =
            precedence.firstOrNull(candidates::contains) ?: NOT_YET_EVIDENCED

        fun of(id: String?): SkillPresentationState? = entries.firstOrNull { it.id == id }
    }
}

/**
 * The attention qualifiers `VDSX-v0` gave a tone to, and no others. A qualifier rides beside the state and never
 * replaces it (`SPWX-v0` §5.1): `at_risk` is the retention one, the other three come from the evidence itself.
 */
enum class AttentionQualifier(val id: String) {
    RETENTION_AT_RISK("retention_at_risk"),
    REQUIRES_INDEPENDENT_RECHECK("requires_independent_recheck"),
    CONTESTED_EVIDENCE_PRESENT("contested_evidence_present"),
    PROVISIONAL_EVALUATION_PRESENT("provisional_evaluation_present"),
}

/** An axis the derivation needed and no engine has written yet. Said, not guessed (user decision, 16A). */
enum class UnevaluatedAxis(val id: String) {
    MASTERY("mastery_axis_state"),
    RETENTION("retention_axis_state"),
    WEAKNESS("weakness_axis_state"),
    PREREQUISITE("prerequisite_axis_state"),
}

/**
 * Everything one Skill's state is derived from.
 *
 * [mastery] is `null` when the mastery engine has not written this Skill; [prerequisite] is the gate's answer for new
 * work on this Skill (`PRG-v0` §7 — whether the Skill's **own** prerequisites are resolved, not what the Skill is worth
 * as a prerequisite), `null` when it was not asked. [evidenced] says whether any evidence row exists for the Skill at
 * all, and [projectionStale] whether evidence was recorded after the stored row was built (`SPWX-v0` §16).
 */
data class SkillAxisReading(
    val mastery: MasteryAxisState?,
    val retention: RetentionAxis,
    val weakness: WeaknessAxis,
    val prerequisite: PrerequisiteEligibility?,
    val evidenced: Boolean,
    val projectionStale: Boolean = false,
    /** Evidence-level qualifiers the reader found on the Skill's rows; at_risk is added from [retention]. */
    val evidenceQualifiers: Set<AttentionQualifier> = emptySet(),
) {
    init {
        require(AttentionQualifier.RETENTION_AT_RISK !in evidenceQualifiers) { "at_risk is the retention axis's, not evidence's" }
    }
}

/** The derivation's answer: one of the eight states with its qualifiers, or a plain "not derivable yet". */
sealed interface SkillStateProjection {
    val reading: SkillAxisReading

    data class Derived(
        val state: SkillPresentationState,
        val qualifiers: List<AttentionQualifier>,
        override val reading: SkillAxisReading,
    ) : SkillStateProjection

    /**
     * A Skill whose state cannot be told yet: evidence exists that no engine has read, or an axis a rule needs is
     * unwritten. `SPWX-v0` §16 — a stale or partial projection is never presented as current — and no ninth state:
     * this is the projection's own status, not a Skill state.
     */
    data class Recomputing(
        val unevaluated: List<UnevaluatedAxis>,
        val stale: Boolean,
        override val reading: SkillAxisReading,
    ) : SkillStateProjection
}

object SkillPresentationProjection {

    /** What the store keeps in `primary_presentation_state` when the state cannot be derived. Never one of the eight. */
    const val NOT_DERIVED = "not_yet_evaluated"

    /**
     * `TEPM-v0` §4.1, generalised by `SPWX-v0` §4: the first rule that holds wins. A rule that needs an axis no engine
     * has written stops the derivation there — the later rules are not consulted, because the answer they would give
     * depends on what that axis would have said.
     */
    fun derive(reading: SkillAxisReading): SkillStateProjection {
        fun recomputing(vararg axes: UnevaluatedAxis) =
            SkillStateProjection.Recomputing(axes.toList(), reading.projectionStale, reading)

        // Evidence recorded after the row was built: the row is about older truth (`SPWX-v0` §16).
        if (reading.projectionStale) return recomputing()
        // A Skill with no evidence row at all has nothing for an engine to read: not evidenced, no weakness.
        val mastery = reading.mastery ?: if (reading.evidenced) return recomputing(UnevaluatedAxis.MASTERY)
        else MasteryAxisState.NOT_YET_EVIDENCED
        val weakness = if (!reading.evidenced && reading.weakness == WeaknessAxis.NOT_YET_EVALUATED) WeaknessAxis.NONE
        else reading.weakness

        // 1. A confirmed WLRM/GRE remediation gate is open.
        if (weakness == WeaknessAxis.NOT_YET_EVALUATED) return recomputing(UnevaluatedAxis.WEAKNESS)
        val state: SkillPresentationState = when {
            weakness == WeaknessAxis.REMEDIATION_REQUIRED -> SkillPresentationState.REMEDIATION_REQUIRED
            // 2. An unresolved GRE or RVR verification.
            mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE -> SkillPresentationState.CONFIRMATION_VERIFICATION_DUE
            mastery == MasteryAxisState.CONFIRMED_CURRENT -> when (reading.retention) {
                RetentionAxis.VERIFICATION_DUE -> SkillPresentationState.CONFIRMATION_VERIFICATION_DUE
                // 3. Confirmed and the scheduled review has come: an opportunity, never a demotion (`SPWX-v0` §15).
                RetentionAxis.REVIEW_DUE -> SkillPresentationState.CONFIRMED_REVIEW_DUE
                // 4. Confirmed and current. at_risk keeps the confirmation and rides as a qualifier (`RVR-v0` §2).
                RetentionAxis.FRESH, RetentionAxis.STABLE, RetentionAxis.AT_RISK -> SkillPresentationState.CONFIRMED_CURRENT
                // A confirmed Skill the retention engine has not scheduled: which of rules 2–4 holds is not known.
                RetentionAxis.UNTRACKED, RetentionAxis.NOT_YET_EVALUATED -> return recomputing(UnevaluatedAxis.RETENTION)
            }
            // 5. Its own hard prerequisite is unresolved and no clean independent target evidence can be read yet.
            mastery == MasteryAxisState.NOT_YET_EVIDENCED || mastery == MasteryAxisState.DEVELOPING_WITH_SUPPORT -> {
                val prerequisite = reading.prerequisite ?: return recomputing(UnevaluatedAxis.PREREQUISITE)
                when {
                    prerequisite.waits -> SkillPresentationState.PREREQUISITE_UNRESOLVED
                    // 7. Only assisted, provisional or practice evidence.
                    mastery == MasteryAxisState.DEVELOPING_WITH_SUPPORT -> SkillPresentationState.DEVELOPING_WITH_SUPPORT
                    // 8. Otherwise.
                    else -> SkillPresentationState.NOT_YET_EVIDENCED
                }
            }
            // 6. Independent H0 direct evidence, the gate not yet passed.
            else -> SkillPresentationState.DEVELOPING_INDEPENDENT
        }
        val qualifiers = buildList {
            if (reading.retention == RetentionAxis.AT_RISK) add(AttentionQualifier.RETENTION_AT_RISK)
            addAll(AttentionQualifier.entries.filter { it in reading.evidenceQualifiers })
        }
        return SkillStateProjection.Derived(state, qualifiers, reading)
    }

    /** The value `skill_state.primary_presentation_state` keeps for this reading: a state's id or [NOT_DERIVED]. */
    fun storedId(reading: SkillAxisReading): String =
        when (val projection = derive(reading)) {
            is SkillStateProjection.Derived -> projection.state.id
            is SkillStateProjection.Recomputing -> NOT_DERIVED
        }
}
