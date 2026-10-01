package coach.model

import java.time.LocalDate
import java.time.temporal.IsoFields

/**
 * The weekly assessment's own vocabulary (13A), as `WBA-v0 / D-045` fixed it: the cycle, the six roles and
 * the `assessment.weekly.*` reason codes. Everything a weekly and a monthly blueprint share — the slot, the
 * blueprint, its statuses, refusals and result — is the common contract in `AssessmentBlueprint.kt`
 * (`WBA-v0` §28), generalised at 13B without changing what any weekly value means.
 */

/**
 * Which week a study day belongs to (13A). The week is the ISO-8601 week of the **learner-local study
 * day the row recorded** (`DDM-v0`), never of an instant, so a DST change or a flight cannot move work
 * from one week into another.
 *
 * A cycle is an identity, not a countdown: a week in which no assessment ran leaves nothing behind, and
 * the only cycle that can ever be composed is the one the learner is in (`WBA-v0` §3, no exam debt).
 */
object WeeklyCycle {

    fun of(studyDay: String): String {
        val day = LocalDate.parse(studyDay)
        val year = day.get(IsoFields.WEEK_BASED_YEAR).toString().padStart(4, '0')
        val week = day.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR).toString().padStart(2, '0')
        return "$year-W$week"
    }
}

/**
 * `WBA-v0` §7's six slot roles, in its order. A role is why a slot exists; it is **not a quota** — no
 * week has to contain every role, or any given role at all.
 *
 * [intent] is the `DMA-v0` §3 measurement intent an item must be trusted for, and [purpose] is the
 * `TASK_TAXONOMY_SPEC` §3.1 purpose a planned slot carries: retention stays `retain` even when it is
 * measured inside a weekly session (`DMA-v0` §2, `question-like UI != assess`).
 */
enum class BlueprintRole(
    override val id: String,
    override val reasonCode: String,
    override val intent: AssessmentIntent,
    override val purpose: TaskPurpose,
) : SlotRole {
    RECENT_REQUIRED_PROGRESS("recent_required_progress", "assessment.weekly.slot_recent_progress",
        AssessmentIntent.MASTERY_EVIDENCE, TaskPurpose.ASSESS),
    WEAKNESS_OR_VERIFICATION("weakness_or_verification", "assessment.weekly.slot_weakness_or_verification",
        AssessmentIntent.VERIFICATION, TaskPurpose.ASSESS),
    CRITICAL_PREREQUISITE_CONFIDENCE("critical_prerequisite_confidence", "assessment.weekly.slot_critical_prerequisite",
        AssessmentIntent.VERIFICATION, TaskPurpose.ASSESS),
    RETENTION_DUE("retention_due", "assessment.weekly.slot_retention_due",
        AssessmentIntent.VERIFICATION, TaskPurpose.RETAIN),
    INTEGRATION_OR_TRANSFER("integration_or_transfer", "assessment.weekly.slot_integration_transfer",
        AssessmentIntent.INTEGRATION_CHECK, TaskPurpose.ASSESS),
    PARALLEL_ENGLISH("parallel_english", "assessment.weekly.slot_parallel_english",
        AssessmentIntent.MASTERY_EVIDENCE, TaskPurpose.ASSESS),
    ;

    /** `WBA-v0` §27 has no longitudinal lists; those are `MCA-v0`'s. */
    override val evidenceKind: RoleEvidenceKind? get() = null

    companion object {
        /**
         * `WBA-v0` §9's selection order, which is not §7's listing order: integrity, verification and
         * repair first, then critical evidence gaps, then recent progress, due retention, integration and
         * the parallel track. The order decides which role a Skill is measured under and in which order
         * slots are offered; it assigns no weight.
         */
        val SELECTION_ORDER: List<BlueprintRole> = listOf(
            WEAKNESS_OR_VERIFICATION,
            CRITICAL_PREREQUISITE_CONFIDENCE,
            RECENT_REQUIRED_PROGRESS,
            RETENTION_DUE,
            INTEGRATION_OR_TRANSFER,
            PARALLEL_ENGLISH,
        )
    }
}

/** `WBA-v0` §29, the `assessment.weekly.*` extension of `PDT-v0`'s `assessment.*` namespace, in its order. */
object WeeklyReasonCodes : ScopeReasonCodes {
    const val DUE = "assessment.weekly.due"
    const val BLUEPRINT_GENERATED = "assessment.weekly.blueprint_generated"
    const val NO_ELIGIBLE_TARGET = "assessment.weekly.no_eligible_target"
    const val NO_VALID_ITEM = "assessment.weekly.no_valid_item"
    const val CAPACITY_SPLIT = "assessment.weekly.capacity_split"
    const val PARTIAL_SESSION = "assessment.weekly.partial_session"
    const val INCOMPLETE_NOT_FAILURE = "assessment.weekly.incomplete_not_failure"
    const val ASSISTANCE_RECHECK_REQUIRED = "assessment.weekly.assistance_recheck_required"
    const val INVALID_ITEM_REPLACED = "assessment.weekly.invalid_item_replaced"
    const val PREREQUISITE_CONTAMINATED = "assessment.weekly.prerequisite_contaminated"
    const val EVIDENCE_BUNDLE_RECORDED = "assessment.weekly.evidence_bundle_recorded"
    const val REPLAN_AFTER_RESULT = "assessment.weekly.replan_after_result"
    const val NO_EXAM_DEBT = "assessment.weekly.no_exam_debt"

    val CATALOG: List<String> = listOf(
        DUE,
        BLUEPRINT_GENERATED,
        "assessment.weekly.slot_recent_progress",
        "assessment.weekly.slot_weakness_or_verification",
        "assessment.weekly.slot_critical_prerequisite",
        "assessment.weekly.slot_retention_due",
        "assessment.weekly.slot_integration_transfer",
        "assessment.weekly.slot_parallel_english",
        NO_ELIGIBLE_TARGET,
        NO_VALID_ITEM,
        CAPACITY_SPLIT,
        PARTIAL_SESSION,
        INCOMPLETE_NOT_FAILURE,
        ASSISTANCE_RECHECK_REQUIRED,
        INVALID_ITEM_REPLACED,
        PREREQUISITE_CONTAMINATED,
        EVIDENCE_BUNDLE_RECORDED,
        REPLAN_AFTER_RESULT,
        NO_EXAM_DEBT,
    )

    override val due get() = DUE
    override val blueprintGenerated get() = BLUEPRINT_GENERATED
    override val noEligibleTarget get() = NO_ELIGIBLE_TARGET
    override val noValidItem get() = NO_VALID_ITEM
    override val partialSession get() = PARTIAL_SESSION
    override val incompleteNotFailure get() = INCOMPLETE_NOT_FAILURE
    override val assistanceRecheckRequired get() = ASSISTANCE_RECHECK_REQUIRED
    override val invalidItemReplaced get() = INVALID_ITEM_REPLACED
    override val prerequisiteContaminated get() = PREREQUISITE_CONTAMINATED
    override val evidenceRecorded get() = EVIDENCE_BUNDLE_RECORDED
    override val replanAfterResult get() = REPLAN_AFTER_RESULT
    override val noExamDebt get() = NO_EXAM_DEBT
    override val catalog get() = CATALOG
}
