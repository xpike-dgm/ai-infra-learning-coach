package coach.presentation

import coach.model.CandidateDisposition
import coach.model.NeedDisposition
import coach.model.NeedTrace
import coach.model.PlanTrace
import coach.model.PlannerExplanationFacts
import coach.model.ReasonCatalog
import coach.model.ReasonCodeFamily
import coach.model.VersionedRef
import coach.model.recordedReasonCodes

/**
 * The shared `planner_explanation` surface (12E): "why this task?", "why not today?", "why is it
 * waiting?" and "why did the plan change?" (`UXIA-v0`), answered from the plan's own trace.
 *
 * `PDT-v0` §2 is the whole rule: **an explanation is a projection of the decision trace**. Every
 * [Statement] is built from a reason code the trace recorded or from a fact the trace records in a
 * field; there is no free-text reason, and a code the trace did not record cannot be turned into one.
 * Wording is a template per code ([ExplanationCopy]) — `PDT-v0` §3's fallback that works with no model
 * at all — so an LLM could later only paraphrase what is already here.
 */

/** A fact the trace records in a field rather than as a reason code. None of them is a code. */
enum class TraceFact(val id: String) {
    /** `PlannedEntry.preserved`: carried from an earlier version because it had already started (12D). */
    KEPT_FROM_EARLIER_VERSION("kept_from_earlier_version"),

    /** `NeedDisposition.NO_VALID_CANDIDATE` with no code: nothing authored serves the need. 12C invents no code for it. */
    NO_TASK_FOR_NEED("no_task_for_need"),

    /** A need's disposition when no code says why: it was not taken today. Nothing more is claimed. */
    NOT_TAKEN_TODAY("not_taken_today"),

    /** `NeedDisposition.BLOCKED` when no code says which gate answer held it: it waits. Nothing more is claimed. */
    WAITING("waiting"),

    /** A replan record whose trigger has no `PDT-v0` §8.10 code (`task_completed`): the plan changed, and nothing more is claimed. */
    PLAN_REPLACED("plan_replaced"),

    /** The `day_within_hard_budget` check a replan records, when it does not hold (12D: kept work is never undone). */
    DAY_OVER_BUDGET_AFTER_KEEPING("day_over_budget_after_keeping"),
}

/** A Skill named in an explanation. [name] is its published name, or `null` when none is published. */
data class SkillMention(val ref: VersionedRef, val name: String?)

/**
 * One thing an explanation says, with where it came from: a recorded reason code, or a [TraceFact].
 *
 * The constructor is private and the only builders check the source against the trace, so a statement
 * the planner never recorded cannot be constructed.
 */
class Statement private constructor(
    val code: String?,
    val fact: TraceFact?,
    val skills: List<SkillMention>,
) {
    override fun equals(other: Any?): Boolean =
        other is Statement && other.code == code && other.fact == fact && other.skills == skills

    override fun hashCode(): Int = (code?.hashCode() ?: 0) * 31 + (fact?.hashCode() ?: 0) * 17 + skills.hashCode()

    override fun toString(): String = "Statement(${code ?: fact?.id}, $skills)"

    companion object {
        /** A statement for [code], or `null` when the trace did not record it or no contract defines it. */
        internal fun ofCode(code: String, recorded: Set<String>, skills: List<SkillMention> = emptyList()): Statement? =
            if (code in recorded && ReasonCatalog.isKnown(code)) Statement(code, null, skills) else null

        internal fun ofFact(fact: TraceFact, skills: List<SkillMention> = emptyList()): Statement = Statement(null, fact, skills)
    }
}

/** `PDT-v0` §21 `reconsideration_condition`. It is not a date promise: an open need is replanned fresh. */
enum class Reconsideration(val id: String) {
    NEXT_PLAN("next_plan"),
    WHEN_PREREQUISITE_READY("when_prerequisite_ready"),
    WHEN_A_TASK_IS_AVAILABLE("when_a_task_is_available"),
}

enum class ExplanationState(val id: String) {
    EXPLAINED("explained"),
    NO_PLAN_YET("no_plan_yet"),
    PLAN_FROM_ANOTHER_DAY("plan_from_another_day"),
    UNREADABLE("unreadable"),
}

/** `PDT-v0` §21 `why_today`: the need first, then whatever the trace says moved it or fitted it. */
data class TaskExplanation(
    val position: Int,
    val title: String,
    val skill: SkillMention,
    val kept: Boolean,
    val why: Statement,
    val supporting: List<Statement>,
)

/**
 * `PDT-v0` §21 `why_not_today`: which needs, why they did not come today, and when they are looked at
 * again. Needs that did not come for the same recorded reason are one entry (12F): a returning learner
 * with eighty Skills due for review reads one sentence about them, not eighty rows — `SRR-v0` §9.1's
 * `due state inventory != DailyPlan` holds for the explanation too.
 */
data class NotTodayExplanation(
    val needKeys: List<String>,
    val skills: List<SkillMention>,
    val need: Statement,
    val whyNot: Statement,
    val reconsideration: Reconsideration?,
)

data class PlannerExplanationView(
    val state: ExplanationState,
    val plan: List<Statement>,
    val today: List<TaskExplanation>,
    val notToday: List<NotTodayExplanation>,
) {
    /** Every statement the view says, in reading order. */
    val statements: List<Statement>
        get() = plan + today.flatMap { listOf(it.why) + it.supporting } +
            notToday.flatMap { listOfNotNull(it.need, it.whyNot) }
}

object PlannerExplanationPresentation {

    // `PDT-v0` §8.5: the band itself is rank bookkeeping. The user-facing reason is the decisive
    // difference (§9), so band codes stay in the trace and are never shown as the reason.
    private val SHOWN_PRIORITY = listOf(
        "priority.blocks_current_required_path",
        "priority.blocks_next_ready_dependency",
        "priority.continuation_value",
        "priority.starvation_promoted",
        "priority.decision_value",
        "priority.track_balance_pressure",
    )

    // §9: eligibility "if relevant" — `ready` adds nothing a learner needs to read.
    private val SHOWN_ELIGIBILITY = listOf(
        "eligibility.conditional_uncertain",
        "eligibility.soft_gap_support",
        "eligibility.ready_due_allowed",
    )

    // §9: capacity "if relevant" — only when the fit changed what was planned.
    private val SHOWN_FIT = listOf("capacity.split_to_fit", "capacity.smaller_alternative_to_fit")

    private const val LOWER_PRIORITY = "selection.not_selected_lower_priority"
    private const val CAPACITY_DEFERRED = "capacity.deferred_not_enough_time"
    private const val NOT_SELECTED_CAPACITY = "selection.not_selected_capacity"
    private const val INDEPENDENT_BRANCH = "independent_branch_available"
    private const val MICROTASK = "capacity.reserve_relaxed_for_microtask"

    private val SERVED = setOf(NeedDisposition.SELECTED, NeedDisposition.PARTIALLY_SERVED)

    fun of(facts: PlannerExplanationFacts): PlannerExplanationView = when (facts) {
        PlannerExplanationFacts.NoPlan -> empty(ExplanationState.NO_PLAN_YET)
        is PlannerExplanationFacts.PlanFromAnotherDay -> empty(ExplanationState.PLAN_FROM_ANOTHER_DAY)
        is PlannerExplanationFacts.Unreadable -> empty(ExplanationState.UNREADABLE)
        is PlannerExplanationFacts.Readable -> explain(facts.trace, facts.skillNames)
    }

    private fun empty(state: ExplanationState) = PlannerExplanationView(state, emptyList(), emptyList(), emptyList())

    private fun explain(trace: PlanTrace, names: Map<VersionedRef, String>): PlannerExplanationView {
        val recorded = trace.recordedReasonCodes()
        fun mention(ref: VersionedRef) = SkillMention(ref, names[ref])
        fun code(code: String, skills: List<VersionedRef> = emptyList()) = Statement.ofCode(code, recorded, skills.map(::mention))
        val needs = trace.needs.associateBy { it.needKey }

        val today = trace.selected.sortedBy { it.position }.map { entry ->
            if (entry.preserved) {
                return@map TaskExplanation(entry.position, entry.title, mention(entry.primarySkill), kept = true,
                    why = Statement.ofFact(TraceFact.KEPT_FROM_EARLIER_VERSION), supporting = emptyList())
            }
            val need = needs.getValue(entry.needKey)
            val chosen = trace.candidates.firstOrNull { it.candidateId == entry.candidateId && it.needKey == entry.needKey }
            val supporting = buildList {
                SHOWN_PRIORITY.filter { it in need.priorityReasonCodes }.mapNotNullTo(this) { code(it) }
                chosen?.reasonCodes?.filter { it in SHOWN_ELIGIBILITY }?.mapNotNullTo(this) { code(it, chosen.relatedSkills) }
                SHOWN_FIT.filter { it in need.finalReasonCodes }.mapNotNullTo(this) { code(it) }
            }
            TaskExplanation(entry.position, entry.title, mention(entry.primarySkill), kept = false,
                why = needStatement(need, recorded, ::mention), supporting = supporting)
        }

        val notToday = trace.needs.filter { it.disposition !in SERVED }.map { need ->
            val (whyNot, reconsider) = whyNot(trace, need, recorded, ::mention)
            NotTodayExplanation(
                needKeys = listOf(need.needKey),
                skills = need.targetSkills.map(::mention),
                need = needStatement(need, recorded, ::mention),
                whyNot = whyNot,
                reconsideration = reconsider,
            )
        }.let { grouped(it, recorded) }

        val plan = buildList {
            trace.replan?.let { replan ->
                add(replan.trigger.reasonCode?.let { code(it) } ?: Statement.ofFact(TraceFact.PLAN_REPLACED))
                if (trace.invariantChecks["day_within_hard_budget"] == false) add(Statement.ofFact(TraceFact.DAY_OVER_BUDGET_AFTER_KEEPING))
            }
            trace.planReasonCodes.filter { ReasonCatalog.familyOf(it) == ReasonCodeFamily.REENTRY }.mapNotNullTo(this) { code(it) }
            trace.planReasonCodes.firstOrNull { it.startsWith("capacity.source_") }?.let { code(it) }?.let(::add)
            if (MICROTASK in trace.planReasonCodes) code(MICROTASK)?.let(::add)
            if (INDEPENDENT_BRANCH in trace.planReasonCodes) code(INDEPENDENT_BRANCH)?.let(::add)
        }

        return PlannerExplanationView(ExplanationState.EXPLAINED, plan, today, notToday)
    }

    /**
     * Needs that share the same trigger, the same recorded reason for not coming and the same
     * reconsideration are one entry, in the order the planner ranked them. Nothing is dropped: every need
     * key and every Skill stays in the entry, and a waiting need whose blockers differ stays apart,
     * because its reason names different Skills.
     */
    private fun grouped(items: List<NotTodayExplanation>, recorded: Set<String>): List<NotTodayExplanation> =
        items.groupBy { listOf(it.need.code, it.need.fact, it.whyNot.code, it.whyNot.fact, it.whyNot.skills, it.reconsideration) }
            .values.map { group ->
                if (group.size == 1) return@map group.single()
                val skills = group.flatMap { it.skills }.distinct()
                val first = group.first()
                first.copy(
                    needKeys = group.flatMap { it.needKeys },
                    skills = skills,
                    need = requireNotNull(Statement.ofCode(requireNotNull(first.need.code), recorded, skills)),
                )
            }

    /** Why the need exists at all — its trigger, which the trace records for every need. */
    private fun needStatement(need: NeedTrace, recorded: Set<String>, mention: (VersionedRef) -> SkillMention): Statement =
        requireNotNull(Statement.ofCode(need.trigger.reasonCode, recorded, need.targetSkills.map(mention))) {
            "a need's trigger is always recorded"
        }

    /**
     * `PDT-v0` §10 and §11. A need that did not fit is deferred **for time** and is never called less
     * important (§18); only a recorded lower-priority code may say that. A waiting need names the real
     * Skill it waits on, as the gate answered at planning time.
     */
    private fun whyNot(
        trace: PlanTrace,
        need: NeedTrace,
        recorded: Set<String>,
        mention: (VersionedRef) -> SkillMention,
    ): Pair<Statement, Reconsideration?> {
        fun code(code: String, skills: List<VersionedRef> = emptyList()) = Statement.ofCode(code, recorded, skills.map(mention))
        val candidates = trace.candidates.filter { it.needKey == need.needKey }.sortedBy { it.candidateId }
        return when (need.disposition) {
            NeedDisposition.ELIGIBLE_NOT_SELECTED -> {
                val said = listOf(LOWER_PRIORITY, CAPACITY_DEFERRED, NOT_SELECTED_CAPACITY)
                    .firstOrNull { it in need.finalReasonCodes }?.let { code(it) }
                (said ?: Statement.ofFact(TraceFact.NOT_TAKEN_TODAY)) to Reconsideration.NEXT_PLAN
            }
            NeedDisposition.BLOCKED -> {
                val blocked = candidates.filter { it.disposition == CandidateDisposition.BLOCKED_PREREQUISITE }
                val skills = blocked.flatMap { it.relatedSkills }.distinct()
                val said = blocked.firstNotNullOfOrNull { c -> c.reasonCodes.firstOrNull { it.startsWith("eligibility.blocked_") } }
                    ?.let { code(it, skills) }
                (said ?: Statement.ofFact(TraceFact.WAITING, skills.map(mention))) to Reconsideration.WHEN_PREREQUISITE_READY
            }
            NeedDisposition.NO_VALID_CANDIDATE -> {
                val invalid = candidates.firstOrNull { it.disposition == CandidateDisposition.INVALID_CANDIDATE }
                val said = invalid?.reasonCodes?.firstOrNull { it.startsWith("candidate.") }?.let { code(it) }
                (said ?: Statement.ofFact(TraceFact.NO_TASK_FOR_NEED)) to Reconsideration.WHEN_A_TASK_IS_AVAILABLE
            }
            NeedDisposition.RESOLVED_BEFORE_SELECTION ->
                (code("selection.resolved_before_selection") ?: Statement.ofFact(TraceFact.NOT_TAKEN_TODAY)) to null
            NeedDisposition.SELECTED, NeedDisposition.PARTIALLY_SERVED -> error("a served need is explained as today's work")
        }
    }
}
