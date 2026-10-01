package coach.model

import java.time.LocalDate
import java.time.temporal.IsoFields

/**
 * The weekly assessment's vocabulary (13A), as `WBA-v0 / D-045` fixed it: a blueprint of measurement
 * slots is composed from current state **before** any item is chosen, the six role families are
 * reasons a slot exists and never quotas, and nothing here can hold a score, a grade, a pass mark, a
 * fixed question count or a fixed duration.
 *
 * These live in `core-model` because the composer (`core-engines`), the use cases (`core-application`)
 * and the session interior (`core-presentation`) all read them, and none of those may depend on another.
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
    val id: String,
    val reasonCode: String,
    val intent: AssessmentIntent,
    val purpose: TaskPurpose,
) {
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
object WeeklyReasonCodes {
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
}

/** Whether a slot has an item to measure with. A slot without one is not coverage (`WBA-v0` §20). */
enum class SlotStatus(val id: String) {
    READY("ready"),
    NO_VALID_ITEM("no_valid_item"),
}

/**
 * Why an open need is not a weekly measurement target. Each value names a rule, never the learner, and
 * an excluded need stays exactly as open as it was.
 */
enum class WeeklyExclusion(val id: String) {
    /** Open remediation is repaired first; measuring it again is the bombardment `WBA-v0` §10 forbids. */
    REMEDIATION_OPEN("remediation_open"),

    /** Nothing has been taught yet, and a measurement before teaching is `diagnose`, not `assess` (`DMA-v0` §5). */
    NOT_TAUGHT_YET("not_taught_yet"),

    /** In progress, but nothing was learned on it since the last weekly blueprint (`WBA-v0` §8.1). */
    NOT_ACTIVE_SINCE_LAST_CYCLE("not_active_since_last_cycle"),

    /** Already measured in this blueprint under a role that comes first; one Skill is measured once. */
    MEASURED_IN_ANOTHER_ROLE("measured_in_another_role"),

    /** A diagnostic or reinforcement opportunity: `VDW-v0`'s, or not a measurement at all. */
    NOT_A_WEEKLY_MEASUREMENT("not_a_weekly_measurement"),
}

/** Why an item could not fill a slot. Each value names a rule about the item or the store, never the learner. */
enum class WeeklyItemRefusal(val id: String) {
    ROLE_NOT_DECLARED("role_not_declared"),
    NOT_USABLE_FOR_INTENT("not_usable_for_intent"),
    PREREQUISITE_WAITS("prerequisite_waits"),
    SOLUTION_EXPOSED("solution_exposed"),
    ALREADY_SEEN("already_seen"),
    VARIANT_FAMILY_IN_USE("variant_family_in_use"),
    DEPENDENCY_GROUP_IN_USE("dependency_group_in_use"),
    EXPECTED_MINUTES_MISSING("expected_minutes_missing"),
}

data class WeeklyItemRejection(val item: VersionedRef, val reasons: List<String>)

/** One excluded need, kept so the blueprint can say why a Skill is not in it. */
data class PoolExclusion(val needKey: String, val skill: VersionedRef, val reason: WeeklyExclusion)

/**
 * What the store says a learner has seen (`QAB-v0` §23–§24). Exposure is permanent (`LFPS-v0`), and a
 * weekly slot that asks for independent evidence may not measure with something already seen or solved.
 */
data class ExposureFact(val resource: VersionedRef, val variantFamilyId: String?, val kind: String) {
    val solutionExposed: Boolean get() = kind == SOLUTION_EXPOSURE

    companion object {
        const val ITEM_VERSION_SEEN = "item_version_seen"
        const val SOLUTION_EXPOSURE = "solution_exposure"
        const val VARIANT_FAMILY_EXPOSURE = "variant_family_exposure"
    }
}

/**
 * `WBA-v0` §6. Every field is one the contract names or one the slot needs to be planned and shown;
 * nothing can hold a score, a pass mark or the learner's mastery.
 *
 * The item fields are the slot's measurement, chosen **after** the slot existed. A slot with no item is
 * `NO_VALID_ITEM`: it is not coverage, it is not the learner's failure, and its need stays open.
 */
data class AssessmentBlueprintSlot(
    val slotId: String,
    val role: BlueprintRole,
    val needKey: String,
    val trigger: NeedTrigger,
    val targetSkill: VersionedRef,
    val criticality: Criticality,
    val band: PriorityBand,
    val sourceStateRefs: List<String>,
    val track: String?,
    val status: SlotStatus,
    val item: VersionedRef? = null,
    val targetObjectives: List<VersionedRef> = emptyList(),
    val evidenceType: String? = null,
    val variantFamilyId: String? = null,
    val dependencyGroupId: String? = null,
    val expectedActiveMinutes: Int? = null,
    val itemLifecycle: LifecycleStatus? = null,
    val itemRequiredSkills: List<VersionedRef> = emptyList(),
    /** The item's allowed tools (`QAB-v0` §21), disclosed before the learner answers (`ASUX-v0` §6). */
    val allowedTools: List<String> = emptyList(),
    val requiredForSessionClosure: Boolean = false,
    val reasonCodes: List<String> = emptyList(),
    val rejections: List<WeeklyItemRejection> = emptyList(),
) {
    /**
     * `WBA-v0` §19: a slot that carries a mastery or verification claim asks for independent work.
     * Help is still never blocked; asking for it changes what the attempt proves, not whether it may happen.
     */
    val independenceMode: IndependenceMode get() = IndependenceMode.H0_REQUIRED

    init {
        require(slotId.isNotBlank() && needKey.isNotBlank()) { "a slot names itself and the need it measures" }
        val ready = status == SlotStatus.READY
        require(ready == (item != null)) { "a slot is ready exactly when it has an item" }
        require(!ready || (expectedActiveMinutes != null && expectedActiveMinutes > 0 && itemLifecycle != null &&
            targetObjectives.isNotEmpty() && evidenceType != null)) {
            "a ready slot knows its minutes, its item's trust and what it measures"
        }
        require(!requiredForSessionClosure || ready) { "only a slot that can run can be required to close a session" }
    }
}

/** A block is a group of boundaries between safe checkpoints (`WBA-v0` §16); pausing between blocks is always safe. */
data class WeeklyBlock(val id: String, val role: BlueprintRole, val slotIds: List<String>)

/**
 * `WBA-v0` §5. There is deliberately no field for an overall score, a passing mark, a fixed question
 * count or a fixed duration: [expectedActiveMinutes] is an **estimate** the planner fits into real
 * capacity, never a length the learner owes.
 */
data class WeeklyAssessmentBlueprint(
    val cycleId: String,
    val studyDay: String,
    val curriculumVersion: Int,
    val truthWatermark: Long,
    val policyVersion: String,
    val evaluatorAvailable: Boolean,
    /** The study day recent progress was measured from; `null` when no earlier blueprint exists. */
    val recentSince: String?,
    val slots: List<AssessmentBlueprintSlot>,
    val exclusions: List<PoolExclusion>,
    val reasonCodes: List<String>,
    /** The assessment session this blueprint recomposes, when it is a recomposition (`WBA-v0` §17). */
    val supersedesSessionId: Long? = null,
) {
    init {
        require(slots.map { it.slotId }.toSet().size == slots.size) { "slot ids are unique within a blueprint" }
        require(slots.map { it.targetSkill }.toSet().size == slots.size) { "one Skill is measured once per blueprint" }
        val ready = slots.filter { it.status == SlotStatus.READY }
        require(ready.mapNotNull { it.variantFamilyId }.let { it.toSet().size == it.size }) {
            "a variant family measures one slot: near variants do not multiply independent evidence (QAB-v0 §15)"
        }
        require(ready.mapNotNull { it.dependencyGroupId }.let { it.toSet().size == it.size }) {
            "a dependency group measures one slot (QAB-v0 §16)"
        }
    }

    val readySlots: List<AssessmentBlueprintSlot> get() = slots.filter { it.status == SlotStatus.READY }

    /** Ready slots grouped by role in selection order; each group is one block. */
    val blocks: List<WeeklyBlock>
        get() = BlueprintRole.SELECTION_ORDER.mapNotNull { role ->
            readySlots.filter { it.role == role }.takeIf { it.isNotEmpty() }
                ?.let { WeeklyBlock("block-${role.id}", role, it.map(AssessmentBlueprintSlot::slotId)) }
        }

    /** An estimate for fitting into capacity (`WBA-v0` §15); the week never gets longer than the days allow. */
    val expectedActiveMinutes: Int get() = readySlots.sumOf { it.expectedActiveMinutes!! }

    val splittable: Boolean get() = readySlots.size > 1
}

/** `WBA-v0` §27, in its order. */
enum class WeeklySessionStatus(val id: String) {
    COMPLETE("complete"),
    PARTIAL("partial"),
    DEFERRED("deferred"),
    INVALIDATED("invalidated"),
}

/** One evidence row a slot's attempt produced, as the evidence pipeline recorded it. */
data class WeeklyEvidenceFact(
    val evidenceId: Long,
    val objective: VersionedRef,
    val outcome: EvidenceOutcome,
    val evaluatorStatus: EvaluatorStatus,
    val independence: IndependenceClass,
    val prerequisiteContaminated: Boolean,
)

/**
 * What happened to one slot inside the session. A slot that was skipped or never reached is
 * [submitted] `false`, and that is not incorrect (`ASUX-v0` §7.3, `WBA-v0` §18).
 */
data class WeeklySlotOutcome(
    val slotId: String,
    val submitted: Boolean,
    val attemptId: Long? = null,
    val evidence: List<WeeklyEvidenceFact> = emptyList(),
) {
    init {
        require(submitted || (attemptId == null && evidence.isEmpty())) { "an unsubmitted slot has no attempt and no evidence" }
    }
}

/**
 * `WBA-v0` §27. It carries no `overall_mastery_score` and cannot: which Objectives got which evidence
 * is the whole result, and what that did to canonical state is only what the engines reported.
 */
data class WeeklyAssessmentResult(
    val assessmentSessionId: Long,
    val cycleId: String,
    val sessionStatus: WeeklySessionStatus,
    val completedSlotIds: List<String>,
    val unresolvedSlotIds: List<String>,
    val attemptIds: List<Long>,
    val evidenceIds: List<Long>,
    val verifiedPositiveObjectives: List<VersionedRef>,
    val verifiedNegativeObjectives: List<VersionedRef>,
    val partialObjectives: List<VersionedRef>,
    val invalidOrUnusableEvidenceIds: List<Long>,
    val provisionalEvidenceIds: List<Long>,
    val assistanceRecheckObjectives: List<VersionedRef>,
    val prerequisiteContaminatedSlotIds: List<String>,
    /** What the canonical engines said changed; never derived here from answers. */
    val stateChangeRefs: List<String>,
    val reasonCodes: List<String>,
    val assessmentPolicyVersion: String,
)
