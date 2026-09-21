package coach.engines

import coach.model.DifficultyClass
import coach.model.EvidenceExclusion
import coach.model.EvidenceOutcome
import coach.model.EvaluatorStatus
import coach.model.EvidenceRow
import coach.model.IndependenceClass
import coach.model.MasteryAxisState
import coach.model.ObjectiveGateProfile
import coach.model.SupportBand
import coach.model.VersionedRef

/**
 * `GRE-v0` as running code (12A): the mastery engine, and the only writer of the mastery state
 * family (`MSBX-v0` §engine_ownership).
 *
 * Its whole purpose is to refuse to be impressed. Mastery asks one question — **can they do it
 * without help?** — so assisted work, exposed solutions, unverified evaluations, contested items and
 * work done on a contaminated prerequisite are all kept out of the score, and none of that is a
 * penalty: they are simply answers to a different question.
 *
 * Every number here is an engineering heuristic `GRE-v0` declared for cold start, to be calibrated
 * at 18C. None of them is a probability, a confidence or a percentage of anything, and none is
 * shown to the learner as one.
 */
object MasteryEngine {

    /** `GRE-v0` §5. Recency is a bounded window, not a decay multiplier. */
    const val RECENT_WINDOW_MAX_GROUPS_V0 = 5

    /** `GRE-v0` §6. Not `P(L)`, not an ability estimate, and never rendered as "80% learned". */
    const val OBJECTIVE_MASTERY_THRESHOLD_V0 = 0.80

    /** `GRE-v0` §8 and §9 defaults; an Objective may raise or (with authoring reason) lower them. */
    const val MIN_INDEPENDENT_GROUPS_STANDARD = 2
    const val MIN_INDEPENDENT_GROUPS_CRITICAL = 3
    const val MIN_VARIANT_FAMILIES_STANDARD = 2
    const val MIN_VARIANT_FAMILIES_CRITICAL = 2

    /** The version stamped on every projection this engine writes (`DDM-v0` §projection_provenance). */
    const val MASTERY_FORMULA_VERSION = "GRE-v0"

    // ---------------------------------------------------------------- eligibility

    /**
     * `GRE-v0` §3.1: the conditions an evidence row must meet to enter a mastery score at all.
     *
     * Returns the rules it fails, so an exclusion can always be explained. An empty set means the
     * row is eligible; it never means "good".
     */
    fun exclusions(row: EvidenceRow, profile: ObjectiveGateProfile): Set<EvidenceExclusion> = buildSet {
        if (row.evidenceType !in profile.directEvidenceTypes) add(EvidenceExclusion.NOT_DIRECT_EVIDENCE_FOR_OBJECTIVE)
        // H1-H4 is formative evidence. It is kept, it can trigger a recheck, and it is not a score.
        if (row.independenceClass != IndependenceClass.INDEPENDENT) add(EvidenceExclusion.NOT_INDEPENDENT)
        if (row.evaluatorStatus != EvaluatorStatus.VERIFIED) add(EvidenceExclusion.EVALUATOR_NOT_VERIFIED)
        if (row.outcome == EvidenceOutcome.INVALID) add(EvidenceExclusion.OUTCOME_INVALID)
        if (row.contested) add(EvidenceExclusion.CONTESTED)
        // A wrong answer caused by something never taught says nothing about this Objective, and the
        // learner is not charged for it (§18, §19).
        if (!row.prerequisiteValid) add(EvidenceExclusion.PREREQUISITE_CONTAMINATED)
        if (row.solutionExposed) add(EvidenceExclusion.SOLUTION_EXPOSED)
        if (row.quality == null) add(EvidenceExclusion.NO_GROUP_RESULT)
    }

    // ---------------------------------------------------------------- grouping

    /**
     * One evidence group (`GRE-v0` §4). Correlated items — a testlet, a dependency group — are one
     * group, because answering five parts of the same question is one piece of evidence, not five.
     */
    data class EvidenceGroup(
        val key: String,
        val quality: Double,
        val sequence: Long,
        val variantFamilyId: String?,
        val rows: List<EvidenceRow>,
    ) {
        val isBasicOnly: Boolean get() = rows.all { row -> row.difficulty?.isBasic ?: true }
        val hasUserAuthoredArtifact: Boolean get() = rows.any { it.userAuthoredArtifact }
        val hasTransfer: Boolean get() = rows.any { it.difficulty == DifficultyClass.TRANSFER_INTEGRATION }
    }

    /**
     * Groups eligible rows. A dependency group collapses into one group whose quality is the mean of
     * its rows: `GRE-v0` asks for an objective-specific rubric here, and until rubrics exist (14),
     * the mean of the parts is the honest stand-in — and it can never *raise* the group count, which
     * is the guard that matters.
     */
    fun groupsOf(rows: List<EvidenceRow>): List<EvidenceGroup> =
        rows.groupBy { it.dependencyGroupId ?: "row:${it.id}" }
            .map { (key, grouped) ->
                EvidenceGroup(
                    key = key,
                    quality = grouped.mapNotNull { it.quality }.average(),
                    sequence = grouped.maxOf { it.sequence },
                    variantFamilyId = grouped.firstNotNullOfOrNull { it.variantFamilyId },
                    rows = grouped,
                )
            }
            .sortedBy { it.sequence }

    /** `GRE-v0` §5: at most the last five groups. Older evidence is history, not a running total. */
    fun recentWindow(groups: List<EvidenceGroup>): List<EvidenceGroup> =
        groups.takeLast(RECENT_WINDOW_MAX_GROUPS_V0)

    // ---------------------------------------------------------------- the objective decision

    /** Which gates an Objective passed and failed, and why it landed where it did. */
    data class ObjectiveDecision(
        val objective: VersionedRef,
        val passed: Boolean,
        val recentDirectScore: Double?,
        val independentGroups: Int,
        val variantFamilies: Int,
        val windowGroupKeys: List<String>,
        val passedGates: List<String>,
        val failedGates: List<String>,
        val excluded: Map<Long, Set<EvidenceExclusion>>,
        val assistedRowCount: Int,
        val unresolvedRecheck: Boolean,
        val verificationDue: Boolean,
        val supportBand: SupportBand,
    )

    /**
     * Decides one Objective (`GRE-v0` §8, §9).
     *
     * [previouslyMastered] carries the hysteresis rule of §16.2: the first clean, prerequisite-valid,
     * independent contradiction of something already confirmed opens `verification_due` — it does
     * **not** unmaster it. A single bad day is not evidence that knowledge vanished.
     */
    fun decide(
        profile: ObjectiveGateProfile,
        rows: List<EvidenceRow>,
        previouslyMastered: Boolean = false,
        unresolvedRecheck: Boolean = false,
        unresolvedVerification: Boolean = false,
    ): ObjectiveDecision {
        val excluded = rows.associate { it.id to exclusions(it, profile) }
        val eligible = rows.filter { excluded.getValue(it.id).isEmpty() }
        val window = recentWindow(groupsOf(eligible))
        val score = if (window.isEmpty()) null else window.map { it.quality }.average()
        val families = window.mapNotNull { it.variantFamilyId }.distinct().size

        val minGroups = profile.minIndependentGroups
            ?: if (profile.critical) MIN_INDEPENDENT_GROUPS_CRITICAL else MIN_INDEPENDENT_GROUPS_STANDARD
        val minFamilies = profile.minVariantFamilies
            ?: if (profile.critical) MIN_VARIANT_FAMILIES_CRITICAL else MIN_VARIANT_FAMILIES_STANDARD

        val passed = mutableListOf<String>()
        val failed = mutableListOf<String>()

        fun gate(name: String, ok: Boolean) = if (ok) passed += name else failed += name

        gate("recent_direct_score", score != null && score >= OBJECTIVE_MASTERY_THRESHOLD_V0)
        gate("min_independent_groups", window.size >= minGroups)
        gate("min_variant_families", families >= minFamilies)
        gate("required_direct_type", profile.requiredDirectType == null ||
            window.any { group -> group.rows.any { it.evidenceType == profile.requiredDirectType } })
        gate("no_unresolved_recheck", !unresolvedRecheck)
        gate("no_unresolved_verification", !unresolvedVerification)
        if (profile.critical || profile.requiresNonBasicEvidence) {
            gate("non_basic_evidence", window.any { !it.isBasicOnly })
        }
        if (profile.requiresUserAuthoredArtifact) {
            // An artifact the learner did not write proves what the tool can do, not what they can.
            gate("user_authored_artifact", window.any { it.hasUserAuthoredArtifact })
        }
        if (profile.requiresTransfer) {
            gate("transfer_evidence", window.any { it.hasTransfer })
        }

        val objectivePassed = failed.isEmpty()
        val contradicted = previouslyMastered && !objectivePassed &&
            eligible.any { it.outcome == EvidenceOutcome.NEGATIVE || it.outcome == EvidenceOutcome.PARTIAL }

        // §16.2, in two halves that must not be collapsed.
        //
        // The **first** clean, prerequisite-valid, independent contradiction of confirmed work opens
        // `verification_due` and keeps mastery: one bad day is not evidence that knowledge vanished.
        //
        // If verification was already open and the fresh check contradicts it too, that is no longer
        // noise. Mastery is not held up any longer, the gates decide again, and the verification is
        // resolved — negatively — rather than left open forever.
        val firstContradiction = contradicted && !unresolvedVerification
        val recheckAlsoFailed = contradicted && unresolvedVerification

        return ObjectiveDecision(
            objective = profile.ref,
            passed = objectivePassed || firstContradiction,
            recentDirectScore = score,
            independentGroups = window.size,
            windowGroupKeys = window.map { it.key },
            variantFamilies = families,
            passedGates = passed,
            failedGates = failed,
            excluded = excluded.filterValues { it.isNotEmpty() },
            assistedRowCount = rows.count { it.independenceClass != IndependenceClass.INDEPENDENT },
            unresolvedRecheck = unresolvedRecheck || rows.any {
                it.independenceClass == IndependenceClass.REQUIRES_INDEPENDENT_RECHECK
            },
            verificationDue = if (recheckAlsoFailed) false else unresolvedVerification || firstContradiction,
            supportBand = supportBand(window, families, profile, objectivePassed),
        )
    }

    /** `GRE-v0` §17. Explainable support, explicitly not a statistical confidence. */
    fun supportBand(
        window: List<EvidenceGroup>,
        families: Int,
        profile: ObjectiveGateProfile,
        passed: Boolean,
    ): SupportBand = when {
        !passed -> SupportBand.LOW
        window.size >= MIN_INDEPENDENT_GROUPS_CRITICAL && families >= MIN_VARIANT_FAMILIES_CRITICAL &&
            window.any { !it.isBasicOnly } &&
            (!profile.requiresUserAuthoredArtifact || window.any { it.hasUserAuthoredArtifact }) -> SupportBand.HIGH
        else -> SupportBand.MEDIUM
    }

    // ---------------------------------------------------------------- the skill decision

    data class SkillDecision(
        val skill: VersionedRef,
        val mastered: Boolean,
        val axisState: MasteryAxisState,
        val objectives: List<ObjectiveDecision>,
        val trace: MasteryDecisionTrace,
    )

    /** `GRE-v0` §20: every decision explains itself, including what it left out and why. */
    data class MasteryDecisionTrace(
        val skill: VersionedRef,
        val masteryFormulaVersion: String,
        val objectiveRecentScores: Map<String, Double?>,
        val windowGroupKeys: Map<String, List<String>>,
        val passedGates: Map<String, List<String>>,
        val failedGates: Map<String, List<String>>,
        val excludedEvidence: Map<Long, Set<EvidenceExclusion>>,
        val assistedEvidenceCount: Int,
        val unresolvedRechecks: List<String>,
        val evaluatorStatusSummary: Map<String, Int>,
        val decision: String,
        val reasonCodes: List<String>,
    )

    /**
     * Aggregates a Skill (`GRE-v0` §14). **Non-compensatory**: every required and every critical
     * Objective must pass on its own. A brilliant result on one Objective cannot cover a missing one,
     * which is exactly what an average would let it do.
     */
    fun decideSkill(
        skill: VersionedRef,
        decisions: List<ObjectiveDecision>,
        profiles: Map<VersionedRef, ObjectiveGateProfile>,
        previouslyMastered: Boolean = false,
        allRows: List<EvidenceRow> = emptyList(),
    ): SkillDecision {
        val gating = decisions.filter { decision ->
            profiles[decision.objective]?.let { it.required || it.critical } ?: true
        }
        val unresolvedCriticalRecheck = decisions.any { decision ->
            decision.unresolvedRecheck && profiles[decision.objective]?.critical == true
        }
        val mastered = gating.isNotEmpty() && gating.all { it.passed } && !unresolvedCriticalRecheck
        val verificationDue = decisions.any { it.verificationDue }

        val axis = when {
            decisions.all { it.recentDirectScore == null && it.assistedRowCount == 0 } -> MasteryAxisState.NOT_YET_EVIDENCED
            mastered && verificationDue -> MasteryAxisState.CONFIRMATION_VERIFICATION_DUE
            mastered -> MasteryAxisState.CONFIRMED_CURRENT
            decisions.any { it.recentDirectScore != null } -> MasteryAxisState.DEVELOPING_INDEPENDENT
            else -> MasteryAxisState.DEVELOPING_WITH_SUPPORT
        }

        val reasonCodes = buildList {
            if (mastered) add("all_required_and_critical_objectives_passed")
            if (verificationDue) add("verification_due_after_contradiction")
            if (unresolvedCriticalRecheck) add("unresolved_critical_recheck")
            decisions.filter { !it.passed }.forEach { decision ->
                decision.failedGates.forEach { add("objective_gate_failed:${decision.objective}:$it") }
            }
        }

        return SkillDecision(
            skill = skill,
            mastered = mastered,
            axisState = axis,
            objectives = decisions,
            trace = MasteryDecisionTrace(
                skill = skill,
                masteryFormulaVersion = MASTERY_FORMULA_VERSION,
                objectiveRecentScores = decisions.associate { it.objective.toString() to it.recentDirectScore },
                windowGroupKeys = decisions.associate { it.objective.toString() to it.windowGroupKeys },
                passedGates = decisions.associate { it.objective.toString() to it.passedGates },
                failedGates = decisions.associate { it.objective.toString() to it.failedGates },
                excludedEvidence = decisions.flatMap { it.excluded.entries }.associate { it.key to it.value },
                assistedEvidenceCount = decisions.sumOf { it.assistedRowCount },
                unresolvedRechecks = decisions.filter { it.unresolvedRecheck }.map { it.objective.toString() },
                evaluatorStatusSummary = allRows.groupingBy { it.evaluatorStatus.id }.eachCount(),
                decision = if (mastered) "mastered" else "not_mastered",
                reasonCodes = reasonCodes,
            ),
        )
    }
}
