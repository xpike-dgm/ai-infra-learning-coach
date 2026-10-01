package coach.engines

import coach.model.MasteryAxisState
import coach.model.ObjectiveCoverage
import coach.model.PlanChange
import coach.model.PlanChangeKind
import coach.model.PlanTrace
import coach.model.ProgramChangeReport
import coach.model.ProgramSnapshot
import coach.model.RetentionAxis
import coach.model.SkillAxes
import coach.model.StateChange
import coach.model.StateChangeKind
import coach.model.VersionedRef
import coach.model.WeaknessAxis

/**
 * The program change report (13E): a diff of two readings of canonical state and two plan versions.
 *
 * It decides nothing. Every state change it names is a transition between values the canonical engines wrote
 * (mastery, retention, weakness); every plan change is a difference between two traces the planner wrote.
 * A value no engine had written yet is not a "before" — no change is claimed from it — and a review coming
 * due is a schedule, not a change: time alone reports nothing here.
 *
 * Everything is a pure function of the two snapshots.
 */
object ProgramChangeEngine {

    const val MODEL = "PCRX-v0"

    private val MASTERED = setOf(MasteryAxisState.CONFIRMED_CURRENT.id, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id)
    private const val UNWRITTEN = "not_yet_evaluated"

    fun report(before: ProgramSnapshot, after: ProgramSnapshot): ProgramChangeReport {
        val unknown = mutableSetOf<VersionedRef>()
        val states = after.skills.values.sortedBy { it.skill.toString() }.flatMap { now ->
            val then = before.skills[now.skill]
            stateChanges(then, now, unknown)
        } + after.coverage.values.sortedBy { it.objective.toString() }.mapNotNull { now ->
            coverageChange(before.coverage[now.objective], now, unknown)
        }
        val firstPlan = before.plan == null && after.plan != null
        val plan0 = before.plan
        val plan1 = after.plan
        val plans = if (plan0 == null || plan1 == null || before.planVersionId == after.planVersionId) emptyList()
        else planChanges(plan0, plan1)
        val reasons = buildList {
            addAll(states.map { it.kind.reasonCode })
            // The new plan's own replan reasons, only when a new plan version actually exists.
            if (plans.isNotEmpty() || (before.planVersionId != null && before.planVersionId != after.planVersionId)) {
                addAll(after.plan?.planReasonCodes.orEmpty().filter { it.startsWith("replan.") })
            }
        }.distinct()
        return ProgramChangeReport(
            fromWatermark = before.truthWatermark,
            toWatermark = after.truthWatermark,
            stateChanges = states,
            planChanges = plans,
            reasonCodes = reasons,
            unknownBefore = unknown.sortedBy { it.toString() },
            firstPlan = firstPlan,
        )
    }

    /** One Skill's transitions, axis by axis, each named once. */
    fun stateChanges(before: SkillAxes?, after: SkillAxes, unknown: MutableSet<VersionedRef> = mutableSetOf()): List<StateChange> {
        val skill = after.skill
        val out = linkedMapOf<StateChangeKind, StateChange>()
        fun add(kind: StateChangeKind, from: String, to: String) { out.putIfAbsent(kind, StateChange(skill, kind, from, to)) }

        // ---- mastery
        val m0 = before?.mastery ?: UNWRITTEN
        val m1 = after.mastery
        if (m0 == UNWRITTEN) {
            if (m1 != UNWRITTEN) unknown += skill
        } else if (m0 != m1) {
            when {
                m0 !in MASTERED && m1 == MasteryAxisState.CONFIRMED_CURRENT.id -> add(StateChangeKind.CAPABILITY_CONFIRMED, m0, m1)
                m0 == MasteryAxisState.CONFIRMED_CURRENT.id && m1 == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id ->
                    add(StateChangeKind.VERIFICATION_OPENED, m0, m1)
                m0 == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id && m1 == MasteryAxisState.CONFIRMED_CURRENT.id ->
                    add(StateChangeKind.VERIFICATION_RESOLVED, m0, m1)
                m0 in MASTERED && m1 !in MASTERED -> add(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED, m0, m1)
            }
        }

        // ---- retention: only what evidence moved; fresh/stable -> review_due is the day, not a change
        val r0 = before?.retention ?: UNWRITTEN
        val r1 = after.retention
        if (r0 == UNWRITTEN) {
            if (r1 != UNWRITTEN) unknown += skill
        } else if (r0 != r1) {
            when {
                r1 == RetentionAxis.VERIFICATION_DUE.id -> add(StateChangeKind.VERIFICATION_OPENED, r0, r1)
                r1 == RetentionAxis.AT_RISK.id -> add(StateChangeKind.RETENTION_AT_RISK, r0, r1)
                r1 == RetentionAxis.STABLE.id && r0 in REVALIDATED_FROM -> add(StateChangeKind.RETENTION_REVALIDATED, r0, r1)
            }
        }

        // ---- weakness
        val w0 = before?.weakness ?: UNWRITTEN
        val w1 = after.weakness
        if (w0 == UNWRITTEN) {
            if (w1 != UNWRITTEN) unknown += skill
        } else if (w0 != w1) {
            when {
                w1 == WeaknessAxis.REMEDIATION_REQUIRED.id -> add(StateChangeKind.REMEDIATION_OPENED, w0, w1)
                w0 == WeaknessAxis.REMEDIATION_REQUIRED.id && w1 in CLOSED -> add(StateChangeKind.REMEDIATION_CLOSED, w0, w1)
                w1 == WeaknessAxis.SUPPORTED.id && w0 != WeaknessAxis.REMEDIATION_REQUIRED.id -> add(StateChangeKind.WEAKNESS_SUPPORTED, w0, w1)
                w1 == WeaknessAxis.HYPOTHESIS.id && w0 in CLOSED -> add(StateChangeKind.WEAKNESS_QUESTION_OPENED, w0, w1)
                w0 in OPEN_WEAKNESS && w1 == WeaknessAxis.RESOLVED.id -> add(StateChangeKind.WEAKNESS_RESOLVED, w0, w1)
            }
        }
        return out.values.toList()
    }

    /**
     * One Objective's coverage waiver (13F). A waiver granted is reported once; one whose evidence was corrected
     * away is reported as withdrawn. As with every axis, a row nobody had written is not a before.
     */
    fun coverageChange(before: ObjectiveCoverage?, after: ObjectiveCoverage, unknown: MutableSet<VersionedRef> = mutableSetOf()): StateChange? {
        val c0 = before?.waiver ?: UNWRITTEN
        val c1 = after.waiver
        if (c0 == UNWRITTEN) {
            if (c1 != UNWRITTEN) unknown += after.skill
            return null
        }
        return when {
            c0 == WAIVER_NONE && c1 == WAIVER_ACTIVE ->
                StateChange(after.skill, StateChangeKind.COVERAGE_WAIVED, c0, c1, objective = after.objective)
            c0 == WAIVER_ACTIVE && c1 == WAIVER_NONE ->
                StateChange(after.skill, StateChangeKind.COVERAGE_WAIVER_WITHDRAWN, c0, c1, objective = after.objective)
            else -> null
        }
    }

    private const val WAIVER_NONE = "none"
    private const val WAIVER_ACTIVE = "active"

    /** A Skill's retention became `stable` from a check — a due review, an open verification or a concern. */
    private val REVALIDATED_FROM = setOf(RetentionAxis.FRESH.id, RetentionAxis.REVIEW_DUE.id,
        RetentionAxis.VERIFICATION_DUE.id, RetentionAxis.AT_RISK.id)
    private val CLOSED = setOf(WeaknessAxis.NONE.id, WeaknessAxis.RESOLVED.id)
    private val OPEN_WEAKNESS = setOf(WeaknessAxis.HYPOTHESIS.id, WeaknessAxis.SUPPORTED.id)

    /** Needs that opened or closed and tasks selected or dropped between two plan versions. */
    fun planChanges(before: PlanTrace, after: PlanTrace): List<PlanChange> {
        val needs0 = before.needs.associateBy { it.needKey }
        val needs1 = after.needs.associateBy { it.needKey }
        val tasks0 = before.selected.associateBy { it.candidateId }
        val tasks1 = after.selected.associateBy { it.candidateId }
        return buildList {
            (needs1.keys - needs0.keys).sorted().forEach { key ->
                val n = needs1.getValue(key)
                add(PlanChange(PlanChangeKind.NEED_OPENED, key, n.trigger, n.targetSkills))
            }
            (needs0.keys - needs1.keys).sorted().forEach { key ->
                val n = needs0.getValue(key)
                add(PlanChange(PlanChangeKind.NEED_CLOSED, key, n.trigger, n.targetSkills))
            }
            (tasks1.keys - tasks0.keys).sorted().forEach { id ->
                val t = tasks1.getValue(id)
                add(PlanChange(PlanChangeKind.TASK_ADDED, t.needKey, needs1[t.needKey]?.trigger, listOf(t.primarySkill), id))
            }
            (tasks0.keys - tasks1.keys).sorted().forEach { id ->
                val t = tasks0.getValue(id)
                add(PlanChange(PlanChangeKind.TASK_REMOVED, t.needKey, needs0[t.needKey]?.trigger, listOf(t.primarySkill), id))
            }
        }
    }
}
