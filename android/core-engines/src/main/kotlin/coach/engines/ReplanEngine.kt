package coach.engines

import coach.model.CapacitySource
import coach.model.CheckpointKind
import coach.model.ContinuationValue
import coach.model.DailyCapacity
import coach.model.GenerationKind
import coach.model.LearningNeed
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PlannedEntry
import coach.model.PriorityBand
import coach.model.ReentryContext
import coach.model.ReplanRecord
import coach.model.ReplanTrigger
import coach.model.ResumeContext
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Replanning and re-entry (12D): a new plan version, never an edited one (`PDT-v0` §15).
 *
 * Three things can be true when a plan is asked for, and each is handled the way its contract says:
 * - **no plan exists** — an initial plan (12C);
 * - **the newest plan belongs to another study day** — re-entry (`SRR-v0`): that plan is not replayed,
 *   absence is neither debt nor failure, and a fresh plan comes from current state;
 * - **the newest plan is today's and something happened** — a replan (D-033 §8): what the learner
 *   already started or finished is kept, and only the unstarted remainder is solved again.
 *
 * Everything here is a pure function of its arguments.
 */
object ReplanEngine {

    const val REPLAN_MODEL = "RPLX-v0"

    // `PDT-v0` §8.8, in the order a re-entry trace records them.
    const val REENTRY_RETURN = "reentry.return_after_absence"
    const val REENTRY_NOT_FAILURE = "reentry.absence_not_failure"
    const val REENTRY_NOT_DEBT = "reentry.absence_not_task_debt"
    const val REENTRY_STALE_NOT_REPLAYED = "reentry.stale_plan_not_replayed"
    const val REENTRY_REGENERATED = "reentry.current_state_regenerated"
    const val REENTRY_PAUSED_CANDIDATE = "reentry.paused_checkpoint_candidate"
    const val REENTRY_HIGH_STAKES_NOT_SCORED = "reentry.incomplete_high_stakes_attempt_not_scored"
    const val REENTRY_DUE_INVENTORY = "reentry.due_inventory_not_daily_plan"
    const val REPLAN_RETURN = "replan.return_after_absence"

    /** A plan from another study day is never today's plan (`SRR-v0` §4.1). */
    fun classify(previousStudyDay: String?, today: String): GenerationKind = when {
        previousStudyDay == null -> GenerationKind.INITIAL
        previousStudyDay != today -> GenerationKind.REENTRY
        else -> GenerationKind.REPLAN
    }

    /**
     * The day's hard budget a plan version was made under: its own remainder plus whatever it had
     * already kept from earlier versions. The budget is the learner's, so replanning never grows it.
     */
    fun dayHardBudget(trace: PlanTrace): Int = trace.capacity.hardBudgetMinutes + (trace.replan?.preservedMinutes ?: 0)

    /**
     * D-033 §8: what is left to plan. A new capacity for today replaces the day's budget; a declared
     * remaining time *is* the remainder; every other event keeps the day's budget. Minutes already
     * spent on kept tasks come off the top, and the remainder is never negative.
     */
    fun remainderMinutes(
        trigger: ReplanTrigger,
        previous: PlanTrace,
        preservedMinutes: Int,
        newDayMinutes: Int?,
        declaredRemainingMinutes: Int?,
    ): Int = when {
        trigger.setsRemainingTime -> requireNotNull(declaredRemainingMinutes) { "${trigger.id} declares the remaining time" }
        trigger == ReplanTrigger.TODAY_CAPACITY_CHANGED ->
            (requireNotNull(newDayMinutes) { "a capacity change names the new capacity" } - preservedMinutes).coerceAtLeast(0)
        else -> (dayHardBudget(previous) - preservedMinutes).coerceAtLeast(0)
    }.also { require(it >= 0) { "a remaining time is not negative" } }

    /** The remainder as a capacity, under the same D-033 rule as a whole day. */
    fun remainderCapacity(trigger: ReplanTrigger, previous: PlanTrace, minutes: Int): DailyCapacity =
        PlannerEngine.capacityOf(
            if (trigger == ReplanTrigger.TODAY_CAPACITY_CHANGED) CapacitySource.TODAY_OVERRIDE else previous.capacity.source,
            minutes,
        )

    /** What a stored pause says about continuing, for the needs it names. */
    data class Paused(
        val needs: List<LearningNeed>,
        val continuedNeedKeys: List<String>,
        val highStakesNotResumed: Int,
    )

    /**
     * `SRR-v0` §5: a safe checkpoint makes an open continuation need a paused one (`PBR-v0` P2), but it
     * is never selected automatically — the need is still ranked, gated and fitted like any other. A
     * high-stakes pause is not continued as independent work (§5.2); its need stays as it is and a fresh
     * candidate serves it. A checkpoint whose need is no longer open changes nothing: state moved on.
     */
    fun withPausedWork(needs: List<LearningNeed>, checkpoints: List<ResumeContext>): Paused {
        val latest = checkpoints.groupBy { it.learningNeedKey }.mapValues { it.value.last() }
        val continued = mutableListOf<String>()
        var highStakes = 0
        val updated = needs.map { need ->
            val pause = latest[need.needKey] ?: return@map need
            when {
                pause.kind == CheckpointKind.HIGH_STAKES_PAUSE -> { highStakes += 1; need }
                need.trigger == NeedTrigger.CONTINUE_LEARNING -> {
                    continued += need.needKey
                    need.copy(continuation = ContinuationValue.PAUSED_SAFE_CHECKPOINT)
                }
                else -> need
            }
        }
        return Paused(updated, continued.sorted(), highStakes)
    }

    /** Needs a kept task already serves are not served twice (`PBR-v0` §10). */
    fun unservedNeeds(needs: List<LearningNeed>, kept: List<PlannedEntry>): List<LearningNeed> {
        val served = kept.map { it.needKey }.toSet()
        return needs.filterNot { it.needKey in served }
    }

    /** Study days between the last plan and today, for the record only (`SRR-v0` §8). */
    fun absenceStudyDays(lastPlanned: String, today: String): Int =
        runCatching { ChronoUnit.DAYS.between(LocalDate.parse(lastPlanned), LocalDate.parse(today)).toInt() }
            .getOrDefault(0).coerceAtLeast(0)

    /**
     * A same-day replan: the kept tasks come first, in the order they were planned, and the remainder is
     * appended after them. The remainder's own invariants still hold, and the day as a whole may not
     * exceed the learner's budget.
     */
    fun composeReplan(
        fresh: PlanTrace,
        previous: PlanTrace,
        previousPlanVersionId: Long,
        trigger: ReplanTrigger,
        preservedPositions: List<Int>,
        dayHardMinutes: Int,
    ): PlanTrace {
        val kept = previous.selected.filter { it.position in preservedPositions }.sortedBy { it.position }
        val renumbered = kept.mapIndexed { i, entry -> entry.copy(position = i, preserved = true) } +
            fresh.selected.map { it.copy(position = it.position + kept.size, preserved = false) }
        val keptMinutes = kept.sumOf { it.plannedMinutes }
        val invalidated = previous.selected.map { it.position }.filterNot { it in preservedPositions }
        return fresh.copy(
            generationKind = GenerationKind.REPLAN.id,
            selected = renumbered,
            planReasonCodes = listOfNotNull(trigger.reasonCode) + fresh.planReasonCodes,
            invariantChecks = fresh.invariantChecks + mapOf(
                "day_within_hard_budget" to (keptMinutes + fresh.selected.sumOf { it.plannedMinutes } <= dayHardMinutes),
                "kept_tasks_unchanged" to (kept.size == preservedPositions.size),
            ),
            replan = ReplanRecord(
                trigger = trigger,
                previousPlanVersionId = previousPlanVersionId,
                previousStudyDay = previous.studyDay,
                preservedPositions = preservedPositions.sorted(),
                invalidatedPositions = invalidated,
                preservedMinutes = keptMinutes,
                remainderHardMinutes = fresh.capacity.hardBudgetMinutes,
            ),
        )
    }

    /**
     * Re-entry (`SRR-v0` §6, §17): the previous plan is not carried, and the trace says what the return
     * looked like. Nothing here feeds starvation (§11) and nothing is a penalty.
     */
    fun composeReentry(
        fresh: PlanTrace,
        previousPlanVersionId: Long,
        lastPlannedStudyDay: String,
        stalePlannedTaskCount: Int,
        paused: Paused,
        states: List<SkillPlanningState>,
    ): PlanTrace {
        val openByTrigger = fresh.needs.groupingBy { it.trigger.id }.eachCount().toSortedMap()
        val dueByRetention = states
            .filter { it.retention == RetentionAxis.REVIEW_DUE || it.retention == RetentionAxis.VERIFICATION_DUE || it.retention == RetentionAxis.AT_RISK }
            .groupingBy { it.retention.id }.eachCount().toSortedMap()
        val reasons = buildList {
            add(REPLAN_RETURN)
            add(REENTRY_RETURN)
            add(REENTRY_NOT_FAILURE)
            add(REENTRY_NOT_DEBT)
            add(REENTRY_STALE_NOT_REPLAYED)
            add(REENTRY_REGENERATED)
            if (paused.continuedNeedKeys.isNotEmpty()) add(REENTRY_PAUSED_CANDIDATE)
            if (paused.highStakesNotResumed > 0) add(REENTRY_HIGH_STAKES_NOT_SCORED)
            if (dueByRetention.isNotEmpty()) add(REENTRY_DUE_INVENTORY)
        }
        return fresh.copy(
            generationKind = GenerationKind.REENTRY.id,
            planReasonCodes = reasons + fresh.planReasonCodes,
            reentry = ReentryContext(
                previousPlanVersionId = previousPlanVersionId,
                lastPlannedStudyDay = lastPlannedStudyDay,
                returnedStudyDay = fresh.studyDay,
                absenceStudyDays = absenceStudyDays(lastPlannedStudyDay, fresh.studyDay),
                stalePlannedTaskCount = stalePlannedTaskCount,
                pausedCheckpointNeedKeys = paused.continuedNeedKeys,
                highStakesPausesNotResumed = paused.highStakesNotResumed,
                openNeedCountByTrigger = openByTrigger,
                dueSkillCountByRetention = dueByRetention,
                p0P1NeedCount = fresh.needs.count { it.band == PriorityBand.P0 || it.band == PriorityBand.P1 },
                resolvedDailyCapacityMinutes = fresh.capacity.hardBudgetMinutes,
            ),
        )
    }
}
