package coach.application

import coach.engines.ReplanEngine
import coach.model.CapacityContext
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.NeedDisposition
import coach.model.NeedTrace
import coach.model.NeedTrigger
import coach.model.PlanSnapshot
import coach.model.PlanTrace
import coach.model.PlanTraceCodec
import coach.model.PlannedEntry
import coach.model.PlannedTaskFact
import coach.model.ReasonFamily
import coach.model.StoredPlan

/**
 * Reading a stored plan back (12E): the only way Today and the planner explanation learn what the
 * planner decided.
 *
 * A plan is `plan_version` + `planned_task` rows + one trace. The rows say which Skill each position
 * serves; everything a reader shows — title, purpose, minutes, and why — is in the trace (12C). So the
 * trace is only trusted when it describes *these* rows: the same study day, the same positions and the
 * same Skill at each position. Anything else is read as unreadable, and nothing is filled in to cover
 * the gap.
 */
object PlanReading {

    /** The track `THUX-v0` §6.1 names for the parallel English line. A track is context, never a purpose. */
    const val TECHNICAL_ENGLISH_TRACK = "technical_english"

    private const val CAPACITY_DEFERRED = "capacity.deferred_not_enough_time"
    private const val SPLIT_TO_FIT = "capacity.split_to_fit"
    private const val SMALLER_TO_FIT = "capacity.smaller_alternative_to_fit"

    sealed interface Read {
        /**
         * The newest plan belongs to another study day. It is not today's plan and is not read as one
         * (`SRR-v0`): the day is the row's own, so this holds even when its trace does not decode.
         */
        data class FromAnotherDay(val planStudyDay: String) : Read

        /** Today's plan exists but its trace cannot be trusted to describe it. The reason says why. */
        data class Unreadable(val reason: String) : Read

        /** Only [read] builds this, after the checks above; a reader cannot skip them. */
        class Today internal constructor(val stored: StoredPlan, val trace: PlanTrace) : Read
    }

    fun read(stored: StoredPlan, studyDay: String): Read {
        val planDay = stored.recordedAt.studyDay
        if (planDay != studyDay) return Read.FromAnotherDay(planDay)
        val text = stored.traceText ?: return Read.Unreadable("the plan has no trace")
        val trace = PlanTraceCodec.decode(text) ?: return Read.Unreadable("the trace does not decode")
        if (trace.studyDay != planDay) return Read.Unreadable("the trace names another study day")
        val traced = trace.selected.map { it.position to it.primarySkill }.sortedBy { it.first }
        val rows = stored.plannedTasks.map { it.position to it.skill }.sortedBy { it.first }
        if (traced != rows) return Read.Unreadable("the trace does not describe the plan's own tasks")
        if (traced.map { it.first }.toSet().size != traced.size) return Read.Unreadable("a position appears twice")
        // Every task this version chose itself must have the need decision that chose it; a kept task
        // was chosen by an earlier version, and its need is deliberately not planned twice (12D).
        trace.selected.filterNot { it.preserved }.forEach { entry ->
            val need = trace.needs.firstOrNull { it.needKey == entry.needKey }
                ?: return Read.Unreadable("a selected task has no need decision")
            if (need.selectedCandidateId != entry.candidateId ||
                need.disposition !in setOf(NeedDisposition.SELECTED, NeedDisposition.PARTIALLY_SERVED)
            ) {
                return Read.Unreadable("a selected task is not what its need decision chose")
            }
        }
        return Read.Today(stored, trace)
    }

    /** Today's plan as Today's rows. Each row points at its own stored `planned_task` id. */
    fun snapshot(read: Read.Today): PlanSnapshot {
        val ids = read.stored.plannedTasks.associate { it.position to it.plannedTaskId }
        val trace = read.trace
        return PlanSnapshot(
            planVersionId = read.stored.planVersionId,
            policyVersion = trace.policyVersions["planner"].orEmpty(),
            studyDay = trace.studyDay,
            tasks = trace.selected.sortedBy { it.position }.map { entry ->
                PlannedTaskFact(
                    plannedTaskId = ids.getValue(entry.position),
                    displayTitle = entry.title,
                    primaryPurpose = entry.purpose,
                    targetSkillRefs = listOf(entry.primarySkill),
                    position = entry.position,
                    traceFacts = if (entry.preserved) emptyList() else reasonFamilies(entry, trace.needs.first { it.needKey == entry.needKey }),
                    activityKind = entry.activityKind,
                    curriculumTrack = entry.track,
                    // What is planned for today; below the whole estimate only when the task was split.
                    estimatedMinutes = entry.plannedMinutes,
                    kept = entry.preserved,
                )
            },
        )
    }

    /**
     * `THUX-v0` §4.2's five capacity fields, every one of them read from the trace the planner wrote:
     * the day's budget is the learner's (12D carries it across replans), and "nothing fits" is the
     * planner's own recorded finding — never a comparison made here.
     */
    fun capacity(trace: PlanTrace): CapacityContext {
        val fresh = trace.selected.filterNot { it.preserved }
        return CapacityContext(
            resolvedDailyMinutes = ReplanEngine.dayHardBudget(trace),
            currentDayOverride = trace.capacity.source == CapacitySource.TODAY_OVERRIDE,
            estimatedTotalPlannedMinutes = trace.selected.sumOf { it.plannedMinutes },
            estimatedRemainingPlannedMinutes = fresh.sumOf { it.plannedMinutes },
            planRecalculated = trace.replan != null,
            tooSmallForAnyCandidate = fresh.isEmpty() && trace.needs.any {
                it.disposition == NeedDisposition.ELIGIBLE_NOT_SELECTED && CAPACITY_DEFERRED in it.finalReasonCodes
            },
        )
    }

    /**
     * The `THUX-v0` §7.1 families a Today row may show, in order: one primary, at most one supporting.
     * Each stands for a fact the trace recorded — the need's trigger, its continuation value, the fit
     * the planner chose and the task's track — and nothing else.
     *
     * - The primary is the need's: why this work exists at all. A safe pause the planner continued is
     *   the more specific fact (`PBR-v0` P2), so it wins over plain continuation.
     * - New learning, reinforcement and integration are planned progress on the route (`PBR-v0` P3/P4),
     *   which `THUX-v0` calls continuing current learning; the row's words must not claim the learner
     *   already started it.
     * - An unconfirmed weakness is a state to verify, not a confirmed one to repair (`SPWX-v0`:
     *   hypothesis != deficiency).
     * - English is a track and never the reason a task exists, except when the need *is* the parallel
     *   track's own cadence; otherwise it can only be the supporting fact.
     */
    fun reasonFamilies(entry: PlannedEntry, need: NeedTrace): List<ReasonFamily> {
        val english = entry.track == TECHNICAL_ENGLISH_TRACK
        val primary = when {
            need.rank.continuation == ContinuationValue.PAUSED_SAFE_CHECKPOINT -> ReasonFamily.RESUME_VALID_PAUSED_WORK
            else -> when (need.trigger) {
                NeedTrigger.REMEDIATION_REQUIRED -> ReasonFamily.REPAIR_CONFIRMED_WEAKNESS
                NeedTrigger.VERIFICATION_DUE, NeedTrigger.WEAKNESS_DETECTED -> ReasonFamily.VERIFY_UNCERTAIN_STATE
                NeedTrigger.RETENTION_REVIEW_DUE -> ReasonFamily.REVIEW_DUE_KNOWLEDGE
                NeedTrigger.DIAGNOSTIC_OPPORTUNITY -> ReasonFamily.COLLECT_MISSING_EVIDENCE
                NeedTrigger.PARALLEL_TRACK_DUE ->
                    if (english) ReasonFamily.PARALLEL_TECHNICAL_ENGLISH else ReasonFamily.CONTINUE_CURRENT_LEARNING
                NeedTrigger.CONTINUE_LEARNING, NeedTrigger.NEW_LEARNING,
                NeedTrigger.REINFORCEMENT_OPPORTUNITY, NeedTrigger.INTEGRATION_OPPORTUNITY ->
                    ReasonFamily.CONTINUE_CURRENT_LEARNING
            }
        }
        val fitted = SPLIT_TO_FIT in need.finalReasonCodes || SMALLER_TO_FIT in need.finalReasonCodes
        val supporting = when {
            fitted -> ReasonFamily.FIT_AVAILABLE_CAPACITY
            english && primary != ReasonFamily.PARALLEL_TECHNICAL_ENGLISH -> ReasonFamily.PARALLEL_TECHNICAL_ENGLISH
            else -> null
        }
        return listOfNotNull(primary, supporting)
    }
}
