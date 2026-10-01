package coach.model

/**
 * The common assessment blueprint contract (`WBA-v0` §28), shared by the weekly (13A) and monthly (13B)
 * compositions. `MCA-v0` §4 is explicit that monthly is **a policy extension of this contract, not a second
 * assessment architecture**: the scopes differ in which roles exist, how the pool is read and which cycle
 * a blueprint belongs to — never in what a slot, an item refusal, a session status or a result is.
 *
 * Nothing here can hold a score, a grade, a pass mark, a fixed question count or a fixed duration, and a
 * scope label adds no evidence weight (`ASUX-v0` §4: `monthly_scope != stronger_numeric_weight`).
 */

/**
 * A slot role of one scope. A role is why a slot exists — never a quota. [intent] is the `DMA-v0` §3
 * measurement intent an item must be trusted for, [purpose] the `TASK_TAXONOMY_SPEC` §3.1 purpose a planned
 * slot carries, and [evidenceKind] says which longitudinal result list a clean slot of this role feeds
 * (`MCA-v0` §27); weekly roles feed none.
 */
interface SlotRole {
    val id: String
    val reasonCode: String
    val intent: AssessmentIntent
    val purpose: TaskPurpose
    val evidenceKind: RoleEvidenceKind?
}

/** `MCA-v0` §27's four longitudinal lists, by the role that produces them. */
enum class RoleEvidenceKind(val id: String) {
    CRITICAL_REVALIDATION("critical_revalidation"),
    RETENTION_REVALIDATION("retention_revalidation"),
    TRANSFER("transfer"),
    INTEGRATION("integration"),
}

/** Which roles exist for a scope, and in which order they are selected (`WBA-v0` §9, `MCA-v0` §7). */
object BlueprintScopes {

    /** Only weekly and monthly compose a blueprint; a daily measurement is a single planned task (`DMA-v0` §6). */
    val COMPOSED: Set<AssessmentScope> = setOf(AssessmentScope.WEEKLY_BLUEPRINT, AssessmentScope.MONTHLY_CAPABILITY)

    fun selectionOrder(scope: AssessmentScope): List<SlotRole> = when (scope) {
        AssessmentScope.WEEKLY_BLUEPRINT -> BlueprintRole.SELECTION_ORDER
        AssessmentScope.MONTHLY_CAPABILITY -> MonthlyRole.SELECTION_ORDER
        AssessmentScope.DAILY_MICRO -> emptyList()
    }

    fun roles(scope: AssessmentScope): List<SlotRole> = when (scope) {
        AssessmentScope.WEEKLY_BLUEPRINT -> BlueprintRole.entries
        AssessmentScope.MONTHLY_CAPABILITY -> MonthlyRole.entries
        AssessmentScope.DAILY_MICRO -> emptyList()
    }

    fun cycleOf(scope: AssessmentScope, studyDay: String): String = when (scope) {
        AssessmentScope.WEEKLY_BLUEPRINT -> WeeklyCycle.of(studyDay)
        AssessmentScope.MONTHLY_CAPABILITY -> MonthlyCycle.of(studyDay)
        AssessmentScope.DAILY_MICRO -> error("a daily measurement has no cycle")
    }

    fun codes(scope: AssessmentScope): ScopeReasonCodes = when (scope) {
        AssessmentScope.WEEKLY_BLUEPRINT -> WeeklyReasonCodes
        AssessmentScope.MONTHLY_CAPABILITY -> MonthlyReasonCodes
        AssessmentScope.DAILY_MICRO -> error("a daily measurement composes no blueprint")
    }
}

/**
 * The reason codes every composed scope has, each in its own `assessment.<scope>.*` namespace
 * (`WBA-v0` §29, `MCA-v0` §29). The composer writes these; it never writes a code a scope does not define.
 */
interface ScopeReasonCodes {
    val due: String
    val blueprintGenerated: String
    val noEligibleTarget: String
    val noValidItem: String
    val partialSession: String
    val incompleteNotFailure: String
    val assistanceRecheckRequired: String
    val invalidItemReplaced: String
    val prerequisiteContaminated: String
    val evidenceRecorded: String
    val replanAfterResult: String
    val noExamDebt: String
    val catalog: List<String>
}

/** Whether a slot has an item to measure with. A slot without one is not coverage (`WBA-v0` §20, `MCA-v0` §19). */
enum class SlotStatus(val id: String) {
    READY("ready"),
    NO_VALID_ITEM("no_valid_item"),
}

/**
 * Why an open need is not a measurement target this cycle. Each value names a rule, never the learner, and
 * an excluded need stays exactly as open as it was.
 */
enum class BlueprintExclusion(val id: String) {
    /** Open remediation is repaired first; measuring it again is the bombardment `WBA-v0` §10 forbids. */
    REMEDIATION_OPEN("remediation_open"),

    /** Nothing has been taught yet, and a measurement before teaching is `diagnose`, not `assess` (`DMA-v0` §5). */
    NOT_TAUGHT_YET("not_taught_yet"),

    /** In progress, but nothing was learned on it since the previous blueprint of this scope. */
    NOT_ACTIVE_SINCE_LAST_CYCLE("not_active_since_last_cycle"),

    /** Already measured in this blueprint under a role that comes first; one Skill is measured once. */
    MEASURED_IN_ANOTHER_ROLE("measured_in_another_role"),

    /** A diagnostic or reinforcement opportunity: `VDW-v0`'s (13F), or not a measurement at all. */
    NOT_A_WEEKLY_MEASUREMENT("not_a_weekly_measurement"),

    /** The monthly counterpart (13B): a diagnostic, a reinforcement or a non-English track is not a monthly role. */
    NOT_A_MONTHLY_MEASUREMENT("not_a_monthly_measurement"),

    /**
     * In progress, but neither required nor critical: `MCA-v0` §5/§6 samples longitudinal progress on
     * **required** capabilities, and a supporting or optional Skill is not one (13B).
     */
    NOT_REQUIRED_CAPABILITY("not_required_capability"),
}

/** Why an item could not fill a slot. Each value names a rule about the item or the store, never the learner. */
enum class SlotItemRefusal(val id: String) {
    ROLE_NOT_DECLARED("role_not_declared"),
    NOT_USABLE_FOR_INTENT("not_usable_for_intent"),
    PREREQUISITE_WAITS("prerequisite_waits"),
    SOLUTION_EXPOSED("solution_exposed"),
    ALREADY_SEEN("already_seen"),
    VARIANT_FAMILY_IN_USE("variant_family_in_use"),
    DEPENDENCY_GROUP_IN_USE("dependency_group_in_use"),
    EXPECTED_MINUTES_MISSING("expected_minutes_missing"),
}

data class SlotItemRejection(val item: VersionedRef, val reasons: List<String>)

/** One excluded need, kept so the blueprint can say why a Skill is not in it. */
data class PoolExclusion(val needKey: String, val skill: VersionedRef, val reason: BlueprintExclusion)

/**
 * What the store says a learner has seen (`QAB-v0` §23–§24). Exposure is permanent (`LFPS-v0`), and a slot
 * that asks for independent evidence may not measure with something already seen or solved.
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
 * `WBA-v0` §6 / §28. Every field is one the contract names or one the slot needs to be planned and shown;
 * nothing can hold a score, a pass mark or the learner's mastery.
 *
 * The item fields are the slot's measurement, chosen **after** the slot existed. A slot with no item is
 * `NO_VALID_ITEM`: it is not coverage, it is not the learner's failure, and its need stays open.
 */
data class AssessmentBlueprintSlot(
    val slotId: String,
    val role: SlotRole,
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
    val rejections: List<SlotItemRejection> = emptyList(),
) {
    /**
     * `WBA-v0` §19, `MCA-v0` §18: a slot that carries a mastery, verification or transfer claim asks for
     * independent work. Help is still never blocked; asking for it changes what the attempt proves.
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

/** A block is a group of boundaries between safe checkpoints (`WBA-v0` §16, `MCA-v0` §16). */
data class BlueprintBlock(val id: String, val role: SlotRole, val slotIds: List<String>)

/**
 * `WBA-v0` §5 / §28 with `MCA-v0` §4's extension. There is deliberately no field for an overall score, a
 * passing mark, a fixed question count or a fixed duration: [expectedActiveMinutes] is an **estimate** the
 * planner fits into real capacity, never a length the learner owes.
 */
data class AssessmentBlueprint(
    val scope: AssessmentScope,
    val cycleId: String,
    val studyDay: String,
    val curriculumVersion: Int,
    val truthWatermark: Long,
    val policyVersion: String,
    val evaluatorAvailable: Boolean,
    /**
     * The study day recent progress was measured from — the previous blueprint of this scope; `null` when no
     * earlier blueprint exists. For monthly this is `MCA-v0` §4's `longitudinal_window_ref`.
     */
    val recentSince: String?,
    val slots: List<AssessmentBlueprintSlot>,
    val exclusions: List<PoolExclusion>,
    val reasonCodes: List<String>,
    /** The assessment session this blueprint recomposes, when it is a recomposition (`WBA-v0` §17). */
    val supersedesSessionId: Long? = null,
    /** The previous cycle's session of this scope (`MCA-v0` §4 `prior_monthly_result_ref`); weekly records none. */
    val priorSessionId: Long? = null,
) {
    init {
        require(scope in BlueprintScopes.COMPOSED) { "only weekly and monthly compose a blueprint" }
        val roles = BlueprintScopes.roles(scope)
        require(slots.all { it.role in roles }) { "a slot's role belongs to its blueprint's scope" }
        require(scope == AssessmentScope.MONTHLY_CAPABILITY || priorSessionId == null) { "weekly records no prior session" }
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

    /** Ready slots grouped by role in the scope's selection order; each group is one block. */
    val blocks: List<BlueprintBlock>
        get() = BlueprintScopes.selectionOrder(scope).mapNotNull { role ->
            readySlots.filter { it.role == role }.takeIf { it.isNotEmpty() }
                ?.let { BlueprintBlock("block-${role.id}", role, it.map(AssessmentBlueprintSlot::slotId)) }
        }

    /** An estimate for fitting into capacity; no cycle gets longer than its days allow (`WBA-v0` §15, `MCA-v0` §15). */
    val expectedActiveMinutes: Int get() = readySlots.sumOf { it.expectedActiveMinutes!! }

    val splittable: Boolean get() = readySlots.size > 1
}

/** `WBA-v0` §27 / `MCA-v0` §27, in their order. */
enum class BlueprintSessionStatus(val id: String) {
    COMPLETE("complete"),
    PARTIAL("partial"),
    DEFERRED("deferred"),
    INVALIDATED("invalidated"),
}

/** One evidence row a slot's attempt produced, as the evidence pipeline recorded it. */
data class BlueprintEvidenceFact(
    val evidenceId: Long,
    val objective: VersionedRef,
    val outcome: EvidenceOutcome,
    val evaluatorStatus: EvaluatorStatus,
    val independence: IndependenceClass,
    val prerequisiteContaminated: Boolean,
)

/**
 * What happened to one slot inside the session. A slot that was skipped or never reached is [submitted]
 * `false`, and that is not incorrect (`ASUX-v0` §7.3, `WBA-v0` §18, `MCA-v0` §17).
 */
data class BlueprintSlotOutcome(
    val slotId: String,
    val submitted: Boolean,
    val attemptId: Long? = null,
    val evidence: List<BlueprintEvidenceFact> = emptyList(),
) {
    init {
        require(submitted || (attemptId == null && evidence.isEmpty())) { "an unsubmitted slot has no attempt and no evidence" }
    }
}

/**
 * `WBA-v0` §27 / §28 with `MCA-v0` §27's longitudinal lists. It carries no `overall_mastery_score`, no
 * readiness verdict and no domain pass/fail, and cannot: which Objectives got which evidence is the whole
 * result, and what that did to canonical state is only what the engines reported.
 */
data class AssessmentBlueprintResult(
    val assessmentSessionId: Long,
    val cycleId: String,
    val sessionStatus: BlueprintSessionStatus,
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
    /** `MCA-v0` §27: critical Skills a clean independent positive revalidated this session. */
    val revalidatedCriticalSkills: List<VersionedRef> = emptyList(),
    /** `MCA-v0` §27: mastered Skills a clean delayed retrieval revalidated this session. */
    val revalidatedRetentionSkills: List<VersionedRef> = emptyList(),
    /** `MCA-v0` §27: Objectives with clean evidence from a transfer slot. */
    val transferEvidenceObjectives: List<VersionedRef> = emptyList(),
    /** `MCA-v0` §27: Objectives with clean evidence from an integrated slot. */
    val integratedEvidenceObjectives: List<VersionedRef> = emptyList(),
)
