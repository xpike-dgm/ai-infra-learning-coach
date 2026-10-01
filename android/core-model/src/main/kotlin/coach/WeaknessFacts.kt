package coach.model

/**
 * `WLRM-v0 / D-061` as the weakness engine reads and writes it (13D).
 *
 * Weakness is **localized**: the unit is the Objective, a Skill's weakness is only what its Objectives
 * show, and nothing here can reset a Topic or a Domain. A failed attempt is not automatically a failed
 * Skill, an attempt nobody can attribute is not the learner's, and a remediation is closed by new
 * evidence — never by finishing a task.
 */

/** `WLRM-v0` §5's weakness signal lifecycle, in its order. */
enum class WeaknessSignal(val id: String, val strength: Int) {
    NONE("none", 0),
    HYPOTHESIS("hypothesis", 1),
    SUPPORTED("supported", 2),
    CONFIRMED("confirmed", 3),
    RESOLVED("resolved", 0),
}

/** `state_contract.yaml` `attempt_attribution_outcomes`, in its order: what one evidence row says about its target. */
enum class AttributionOutcome(val id: String) {
    NOT_ATTRIBUTABLE("not_attributable"),
    CONTENT_OR_ENVIRONMENT_ISSUE("content_or_environment_issue"),
    PREREQUISITE_SIGNAL("prerequisite_signal"),
    OBJECTIVE_WEAKNESS_HYPOTHESIS("objective_weakness_hypothesis"),
    OBJECTIVE_WEAKNESS_SUPPORTED("objective_weakness_supported"),
    VERIFICATION_DUE("verification_due"),
    REMEDIATION_REQUIRED("remediation_required"),
    POSITIVE_RECOVERY_EVIDENCE("positive_recovery_evidence"),
}

/**
 * `failure_attribution_rules.yaml`'s twelve rules, by priority; the first that matches a row decides.
 *
 * Three can never match a stored evidence row and are kept so the set stays the accepted one: nothing is
 * written for an attempt that did not happen (`unattempted_or_deferred`), an environment failure reaches
 * the store as an unmeasurable result, which `invalid_or_ambiguous` already takes, and evidence is written
 * per Objective component, so a project's global outcome is never a row (`integrated_global_outcome_guard`).
 * `review_due_without_negative_evidence` cannot match either: this engine never reads the retention axis.
 */
enum class FailureRule(val id: String, val priority: Int) {
    INVALID_OR_AMBIGUOUS("failure.invalid_or_ambiguous", 10),
    PREREQUISITE_CONTAMINATION("failure.prerequisite_contamination", 20),
    UNATTEMPTED_OR_DEFERRED("failure.unattempted_or_deferred", 30),
    ENVIRONMENT_OUTSIDE_TARGET("failure.environment_outside_target", 40),
    ASSISTED_H1_H4("failure.assisted_h1_h4", 50),
    PROVISIONAL_OR_PARTIAL("failure.provisional_or_partial", 60),
    CLEAN_PREMASTERY_H0_DIRECT("failure.clean_premastery_h0_direct", 70),
    FIRST_CLEAN_POSTMASTERY_CONTRADICTION("failure.first_clean_postmastery_contradiction", 80),
    FRESH_RECHECK_FAIL("failure.fresh_recheck_fail", 90),
    INTEGRATED_GLOBAL_OUTCOME_GUARD("failure.integrated_global_outcome_guard", 100),
    REVIEW_DUE_WITHOUT_NEGATIVE_EVIDENCE("failure.review_due_without_negative_evidence", 110),
    FRESH_RECOVERY_SUCCESS("failure.fresh_recovery_success", 120),
}

/**
 * The weakness axis on `skill_state`, derived from the Skill's Objectives. A confirmed Objective weakness is
 * `remediation_required` — the one value the prerequisite gate and the planner already read as open repair.
 */
enum class WeaknessAxis(val id: String) {
    NONE("none"),
    HYPOTHESIS("hypothesis"),
    SUPPORTED("supported"),
    REMEDIATION_REQUIRED("remediation_required"),
    RESOLVED("resolved"),
    NOT_YET_EVALUATED("not_yet_evaluated"),
    ;

    companion object {
        /** `WLRM-v0` §3: Objective first, then the Skill — the strongest open signal names the axis. */
        fun of(objectives: List<ObjectiveWeakness>): WeaknessAxis {
            val signals = objectives.map { it.signal }
            return when {
                WeaknessSignal.CONFIRMED in signals -> REMEDIATION_REQUIRED
                WeaknessSignal.SUPPORTED in signals -> SUPPORTED
                WeaknessSignal.HYPOTHESIS in signals -> HYPOTHESIS
                WeaknessSignal.RESOLVED in signals -> RESOLVED
                else -> NONE
            }
        }
    }
}

/**
 * One evidence row as the weakness engine reads it, with the mastery engine's decision about the Skill
 * before and after the row (`RebuildMastery`'s replay).
 */
data class WeaknessEvent(
    val evidenceId: Long,
    val sequence: Long,
    val studyDay: String,
    val outcome: EvidenceOutcome,
    val evaluatorStatus: EvaluatorStatus,
    val independence: IndependenceClass,
    val contested: Boolean,
    val prerequisiteValid: Boolean,
    val solutionExposed: Boolean,
    val direct: Boolean,
    val resource: VersionedRef?,
    val variantFamilyId: String?,
    val masteredBefore: Boolean,
    val masteredAfter: Boolean,
    /**
     * 13F (`VDW-v0` §12.1, user decision): the row answers "did you already know it?" — it was gathered inside a
     * diagnostic, the Objective had no evidence from ordinary learning before it, and the Skill was not mastered.
     * Not knowing something never taught is not a weakness.
     */
    val diagnosticBaseline: Boolean = false,
) {
    val failure: Boolean get() = outcome == EvidenceOutcome.NEGATIVE || outcome == EvidenceOutcome.PARTIAL

    /** `WLRM-v0` §4/§7: H0, direct, verified, prerequisite-valid, uncontested, no solution shown. */
    val clean: Boolean
        get() = !contested && prerequisiteValid && !solutionExposed && direct &&
            evaluatorStatus == EvaluatorStatus.VERIFIED && independence == IndependenceClass.INDEPENDENT
}

/**
 * One Objective's weakness state (`weakness_state`, `WLRM-v0` §5). [signalResources] and [signalFamilies]
 * are the items and variant families that showed the open signal: closing it needs a **fresh** one
 * (`recheck_contract.fresh_variant_or_context_required`).
 */
data class ObjectiveWeakness(
    val objective: VersionedRef,
    val skill: VersionedRef,
    val signal: WeaknessSignal = WeaknessSignal.NONE,
    val lastOutcome: AttributionOutcome? = null,
    val lastRule: FailureRule? = null,
    val verificationOpen: Boolean = false,
    val signalEvidenceIds: List<Long> = emptyList(),
    val signalResources: List<VersionedRef> = emptyList(),
    val signalFamilies: List<String> = emptyList(),
    val firstSeenDay: String? = null,
    val lastSeenDay: String? = null,
    val resolutionEvidenceId: Long? = null,
) {
    init {
        val open = signal == WeaknessSignal.HYPOTHESIS || signal == WeaknessSignal.SUPPORTED || signal == WeaknessSignal.CONFIRMED
        require(!open || (signalEvidenceIds.isNotEmpty() && firstSeenDay != null)) { "an open weakness names the evidence that showed it" }
        require(signal != WeaknessSignal.RESOLVED || resolutionEvidenceId != null) { "a weakness is resolved by evidence, never by time" }
        require(!verificationOpen || signal == WeaknessSignal.SUPPORTED) { "verification is open on a supported, not yet confirmed, contradiction" }
    }

    fun isFresh(event: WeaknessEvent): Boolean =
        (event.resource == null || event.resource !in signalResources) &&
            (event.variantFamilyId == null || event.variantFamilyId !in signalFamilies)
}

/**
 * What an appended `evidence_disposition` means for the row it names (13D). The row is never edited —
 * it is append-only truth — so a correction is read: the newest disposition of a row decides, and
 * `reinstated` restores the row as recorded.
 *
 * Each mapping is `WLRM-v0` §4's own rule, so every engine that reads evidence honours it without a
 * second exclusion rule: a contested row is contested, a row invalidated because a prerequisite was missing
 * is prerequisite-contaminated, and any other invalidated or superseded row's evaluation no longer stands.
 */
object EvidenceDispositions {
    const val INVALIDATED = "invalidated"
    const val CONTESTED = "contested"
    const val SUPERSEDED = "superseded"
    const val REINSTATED = "reinstated"

    /** The reason a deterministic rule records when it finds a row was built on a missing prerequisite. */
    const val PREREQUISITE_CONTAMINATED = "prerequisite_contaminated"

    val VALUES = listOf(INVALIDATED, CONTESTED, SUPERSEDED, REINSTATED)

    fun effective(row: EvidenceRow, disposition: String?, reason: String?): EvidenceRow = when (disposition) {
        null, REINSTATED -> row
        CONTESTED -> row.copy(contested = true)
        INVALIDATED ->
            if (reason == PREREQUISITE_CONTAMINATED) row.copy(prerequisiteValid = false)
            else row.copy(evaluatorStatus = EvaluatorStatus.INVALID)
        SUPERSEDED -> row.copy(evaluatorStatus = EvaluatorStatus.INVALID)
        else -> error("unknown disposition $disposition")
    }
}
