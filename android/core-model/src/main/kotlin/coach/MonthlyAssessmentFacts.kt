package coach.model

import java.time.LocalDate

/**
 * The monthly assessment's own vocabulary (13B), as `MCA-v0 / D-046` fixed it: the cycle, the eight roles
 * and the `assessment.monthly.*` reason codes. Everything else is the common contract it extends
 * (`MCA-v0` §4, `WBA-v0` §28) — monthly is not a second assessment architecture.
 */

/**
 * Which month a study day belongs to (13B): the calendar month of the **learner-local study day the row
 * recorded**, never of an instant — the same rule as the week (13A). `30 gün geçti -> zorunlu eski sınav
 * borcu` is not canonical (`MCA-v0` §3): only the month the learner is in can ever be composed, so two
 * missed months cannot stack.
 */
object MonthlyCycle {

    fun of(studyDay: String): String {
        val day = LocalDate.parse(studyDay)
        return day.year.toString().padStart(4, '0') + "-" + day.monthValue.toString().padStart(2, '0')
    }
}

/**
 * `MCA-v0` §5's eight slot roles, in its order. None is a quota: a month with nothing due under a role
 * has no slot for it (`MCA-v0` §5, §7).
 *
 * Retention stays `retain` (`DMA-v0` §2). A professional evidence checkpoint produces evidence and is never
 * a readiness gate (`MCA-v0` §14).
 */
enum class MonthlyRole(
    override val id: String,
    override val reasonCode: String,
    override val intent: AssessmentIntent,
    override val purpose: TaskPurpose,
    override val evidenceKind: RoleEvidenceKind?,
) : SlotRole {
    LONGITUDINAL_REQUIRED_CAPABILITY("longitudinal_required_capability", "assessment.monthly.slot_longitudinal_required",
        AssessmentIntent.MASTERY_EVIDENCE, TaskPurpose.ASSESS, null),
    PERSISTENT_WEAKNESS_OR_VERIFICATION("persistent_weakness_or_verification", "assessment.monthly.slot_persistent_weakness",
        AssessmentIntent.VERIFICATION, TaskPurpose.ASSESS, null),
    CRITICAL_CAPABILITY_REVALIDATION("critical_capability_revalidation", "assessment.monthly.slot_critical_revalidation",
        AssessmentIntent.VERIFICATION, TaskPurpose.ASSESS, RoleEvidenceKind.CRITICAL_REVALIDATION),
    DELAYED_RETENTION_SAMPLING("delayed_retention_sampling", "assessment.monthly.slot_delayed_retention",
        AssessmentIntent.VERIFICATION, TaskPurpose.RETAIN, RoleEvidenceKind.RETENTION_REVALIDATION),
    CROSS_TOPIC_TRANSFER("cross_topic_transfer", "assessment.monthly.slot_cross_topic_transfer",
        AssessmentIntent.MASTERY_EVIDENCE, TaskPurpose.ASSESS, RoleEvidenceKind.TRANSFER),
    INTEGRATED_APPLICATION("integrated_application", "assessment.monthly.slot_integrated_application",
        AssessmentIntent.INTEGRATION_CHECK, TaskPurpose.ASSESS, RoleEvidenceKind.INTEGRATION),
    PARALLEL_TECHNICAL_ENGLISH("parallel_technical_english", "assessment.monthly.slot_parallel_english",
        AssessmentIntent.MASTERY_EVIDENCE, TaskPurpose.ASSESS, null),
    PROFESSIONAL_EVIDENCE_CHECKPOINT("professional_evidence_checkpoint", "assessment.monthly.slot_professional_checkpoint",
        AssessmentIntent.INTEGRATION_CHECK, TaskPurpose.ASSESS, RoleEvidenceKind.INTEGRATION),
    ;

    companion object {
        /**
         * `MCA-v0` §7's selection semantics: unresolved integrity and verification first, then high-impact
         * critical confidence, then decision-changing required gaps, meaningful delayed retention, transfer and
         * integration, and last the parallel track and the professional checkpoint. An order, never a weight
         * and never a percentage (`MCA-v0` §7's forbidden `%50 + %30 + %20`).
         */
        val SELECTION_ORDER: List<MonthlyRole> = listOf(
            PERSISTENT_WEAKNESS_OR_VERIFICATION,
            CRITICAL_CAPABILITY_REVALIDATION,
            LONGITUDINAL_REQUIRED_CAPABILITY,
            DELAYED_RETENTION_SAMPLING,
            CROSS_TOPIC_TRANSFER,
            INTEGRATED_APPLICATION,
            PARALLEL_TECHNICAL_ENGLISH,
            PROFESSIONAL_EVIDENCE_CHECKPOINT,
        )
    }
}

/** `MCA-v0` §29, the `assessment.monthly.*` extension of `PDT-v0`'s `assessment.*` namespace, in its order. */
object MonthlyReasonCodes : ScopeReasonCodes {
    const val DUE = "assessment.monthly.due"
    const val BLUEPRINT_GENERATED = "assessment.monthly.blueprint_generated"
    const val NO_ELIGIBLE_TARGET = "assessment.monthly.no_eligible_target"
    const val NO_VALID_ITEM = "assessment.monthly.no_valid_item"
    const val CAPACITY_SPLIT = "assessment.monthly.capacity_split"
    const val PARTIAL_SESSION = "assessment.monthly.partial_session"
    const val INCOMPLETE_NOT_FAILURE = "assessment.monthly.incomplete_not_failure"
    const val NO_EXAM_DEBT = "assessment.monthly.no_exam_debt"
    const val PREREQUISITE_CONTAMINATED = "assessment.monthly.prerequisite_contaminated"
    const val ASSISTANCE_RECHECK_REQUIRED = "assessment.monthly.assistance_recheck_required"
    const val INVALID_ITEM_REPLACED = "assessment.monthly.invalid_item_replaced"
    const val CAPABILITY_EVIDENCE_RECORDED = "assessment.monthly.capability_evidence_recorded"
    const val REPLAN_AFTER_RESULT = "assessment.monthly.replan_after_result"

    val CATALOG: List<String> = listOf(
        DUE,
        BLUEPRINT_GENERATED,
        "assessment.monthly.slot_longitudinal_required",
        "assessment.monthly.slot_persistent_weakness",
        "assessment.monthly.slot_critical_revalidation",
        "assessment.monthly.slot_delayed_retention",
        "assessment.monthly.slot_cross_topic_transfer",
        "assessment.monthly.slot_integrated_application",
        "assessment.monthly.slot_parallel_english",
        "assessment.monthly.slot_professional_checkpoint",
        NO_ELIGIBLE_TARGET,
        NO_VALID_ITEM,
        CAPACITY_SPLIT,
        PARTIAL_SESSION,
        INCOMPLETE_NOT_FAILURE,
        NO_EXAM_DEBT,
        PREREQUISITE_CONTAMINATED,
        ASSISTANCE_RECHECK_REQUIRED,
        INVALID_ITEM_REPLACED,
        CAPABILITY_EVIDENCE_RECORDED,
        REPLAN_AFTER_RESULT,
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
    override val evidenceRecorded get() = CAPABILITY_EVIDENCE_RECORDED
    override val replanAfterResult get() = REPLAN_AFTER_RESULT
    override val noExamDebt get() = NO_EXAM_DEBT
    override val catalog get() = CATALOG
}
