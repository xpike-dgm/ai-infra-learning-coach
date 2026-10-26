package coach.application

import coach.model.AnswerResult
import coach.model.AnsweredItem
import coach.model.ArtifactBody
import coach.model.AssessmentIntent
import coach.model.AssessmentItem
import coach.model.AssistanceEvent
import coach.model.AssistanceLevel
import coach.model.AssistanceScope
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.AttemptSubmission
import coach.model.AuthoredTask
import coach.model.CapabilityGap
import coach.model.EvaluationResult
import coach.model.ExplanationForm
import coach.model.ExposureFact
import coach.model.OpenResponse
import coach.model.OpenResponseVerdict
import coach.model.PlanTrace
import coach.model.PlannedEntry
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteSnapshot
import coach.model.ProgramSnapshot
import coach.model.ProvenanceOrigin
import coach.model.RunnableTask
import coach.model.StoredPlan
import coach.model.TaskEntryFacts
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.model.independence
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Which of today's planned tasks the learner has started (16F): those an attempt was made for in this plan version, and
 * those an earlier version of today's plan kept because they were started then (12D). A replan keeps exactly these
 * (`TRUX-v0` §10.1), and Today does not offer them again.
 */
object StartedTasks {
    fun positions(persistence: PersistencePort, stored: StoredPlan, trace: PlanTrace): Set<Int> {
        val started = persistence.startedPlannedTasks(stored.plannedTasks.map { it.plannedTaskId })
        return trace.selected.filter { it.preserved }.map { it.position }.toSet() +
            stored.plannedTasks.filter { it.plannedTaskId in started }.map { it.position }
    }

    /** Today's started positions, or nothing when today has no readable plan. */
    fun today(persistence: PersistencePort, clock: ClockPort): Set<Int> {
        val stored = persistence.latestPlan() ?: return emptySet()
        val read = PlanReading.read(stored, clock.now().studyDay) as? PlanReading.Read.Today ?: return emptySet()
        return positions(persistence, stored, read.trace)
    }
}

/**
 * Entering one planned task (16F; `TRUX-v0` §6.1, `RNRX-v0` §4.1). Every condition is **confirmed** from the store, the
 * gate, the open needs and the authored content — never assumed. What cannot be confirmed is reported as such, and the
 * task does not start; that is never negative evidence and never the learner's error. It writes no truth.
 */
class OpenTask(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
    private val evaluatorAvailable: Boolean = false,
) {
    fun open(plannedTaskId: Long): TaskEntryFacts {
        val notSelected = TaskEntryFacts(false, false, false, false, false)
        val stored = persistence.latestPlan() ?: return notSelected
        val read = PlanReading.read(stored, clock.now().studyDay) as? PlanReading.Read.Today ?: return notSelected
        val row = stored.plannedTasks.singleOrNull { it.plannedTaskId == plannedTaskId } ?: return notSelected
        val entry = read.trace.selected.singleOrNull { it.position == row.position } ?: return notSelected
        // A started task is not offered again: its run is the learner's history, and continuing is resume's path (11C).
        if (row.position in StartedTasks.positions(persistence, stored, read.trace)) return notSelected

        val ref = AuthoredTask.refOfCandidate(entry.candidateId)
        val task = ref?.let(content::authoredTask)?.takeIf { it.primarySkill == entry.primarySkill }
        val prerequisites = task != null && !ResolvePrerequisites(persistence)
            .resolve(PrerequisiteCandidate(entry.candidateId, entry.primarySkill, task.requiredSkills)).eligibility.waits
        val needOpen = BuildDailyPlan(persistence, content, clock, evaluatorAvailable).openNeeds().any { it.needKey == entry.needKey }
        val compatible = task != null && persistence.skill(entry.primarySkill) != null
        if (task == null || !compatible) return TaskEntryFacts(true, prerequisites, needOpen, false, false)

        val (items, gap) = servable(task)
        val capable = gap == null
        val run = if (prerequisites && needOpen && capable) runnable(stored, row.plannedTaskId, entry, task, items) else null
        return TaskEntryFacts(true, prerequisites, needOpen, true, capable, gap, run)
    }

    /**
     * The task's items this app can serve now: judged by an answer key (code reports and rubric evaluators are not wired
     * yet), usable as the store's own validation allows, and never seen before. An `assess` task's interior is the
     * assessment session's, which is not wired here at all (`TRUX-v0` §5).
     */
    private fun servable(task: AuthoredTask): Pair<List<VersionedRef>, CapabilityGap?> {
        if (task.purpose == TaskPurpose.ASSESS) return emptyList<VersionedRef>() to CapabilityGap.ASSESSMENT_INTERIOR_NOT_AVAILABLE
        val server = ServeDailyMicroItem(persistence, content, clock)
        val judgeable = task.items.filter { content.answerKeyFor(it) != null && server.usable(it, INTENT, evaluatorAvailable) }
        if (judgeable.isEmpty()) return judgeable to CapabilityGap.NO_ITEM_THIS_APP_CAN_EVALUATE
        val unseen = judgeable.filterNot { seen(it) }
        return if (unseen.isEmpty()) unseen to CapabilityGap.NO_UNSEEN_ITEM else unseen to null
    }

    private fun seen(ref: VersionedRef): Boolean {
        val family = content.assessmentItem(ref)?.variantFamilyId
        return persistence.exposuresFor(listOf(ref), listOfNotNull(family)).any { fact ->
            fact.resource == ref || (fact.kind == ExposureFact.SOLUTION_EXPOSURE && fact.variantFamilyId == family)
        }
    }

    private fun runnable(stored: StoredPlan, plannedTaskId: Long, entry: PlannedEntry, task: AuthoredTask, items: List<VersionedRef>) =
        RunnableTask(
            plannedTaskId = plannedTaskId,
            position = entry.position,
            planVersionId = stored.planVersionId,
            task = task.ref,
            title = entry.title,
            purpose = entry.purpose,
            skill = entry.primarySkill,
            targetObjectives = task.targetObjectives,
            plannedMinutes = entry.plannedMinutes,
            needKey = entry.needKey,
            lesson = task.targetObjectives.flatMap { objective ->
                content.explanationsFor(objective).filter { it.form == ExplanationForm.CANONICAL }
            },
            items = items,
        )

    companion object {
        /** A task's own items are low-stakes checks of its Objectives (`DMA-v0` §3); none of them is a verification. */
        val INTENT = AssessmentIntent.CHECKPOINT
    }
}

/**
 * Working through one running task (16F; `TRUX-v0` §6.3–§6.6). The runner serves the task's unseen items in their
 * authored order, records each frozen answer as one learner action, has it judged by its answer key and hands the
 * result to the evidence pipeline; the planner alone decides what comes after.
 */
class RunTask(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
    private val evaluatorAvailable: Boolean = false,
) {
    /** The next unseen item of the run not yet served in it, recorded as seen when shown; `null` when none is left. */
    fun serve(run: RunnableTask, served: Set<VersionedRef>): AssessmentItem? {
        val server = ServeDailyMicroItem(persistence, content, clock)
        run.items.filterNot { it in served }.forEach { ref ->
            val offer = server.offer(ref, OpenTask.INTENT, evaluatorAvailable)
            if (offer is ItemOffer.Ready) return offer.item
        }
        return null
    }

    /**
     * Records one frozen answer and what its answer key says it showed. A blank answer is a skip, and a skip is not a
     * wrong answer (`ASUX-v0`): nothing is written and `null` returns. An answer too long to carry is refused, never cut.
     *
     * The attempt (with its help and provenance) is one transaction (11B); the evidence another, written by the
     * pipeline with the independence the recorded help and provenance give it (`2D` §6) — a teaching task's is practice.
     */
    fun answer(run: RunnableTask, item: AssessmentItem, response: String, provenance: ProvenanceOrigin, assistance: List<AssistanceEvent>): AnsweredItem? {
        if (response.isBlank()) return null
        val body = ArtifactBody.store(response.trim()) as? ArtifactBody.Stored.Inline ?: return null
        val key = content.answerKeyFor(item.ref) ?: return null
        val submission = AttemptSubmission(item.ref, body.contentRef, provenance, assistance, plannedTaskId = run.plannedTaskId)
        val recorded = SubmitAttempt(persistence, clock).submit(submission)

        val verdict = OpenResponse.byKey(item, key, response.trim())
        val evaluation = (verdict as? OpenResponseVerdict.Measured)?.result
        val signals = when (evaluation) {
            is EvaluationResult.Verified -> evaluation.componentResults.map { it.signal }
            is EvaluationResult.Provisional -> evaluation.componentResults.map { it.signal }
            else -> emptyList()
        }
        val skill = run.skill.takeIf { it in item.targetSkills } ?: item.targetSkills.first()
        val gate = ResolvePrerequisites(persistence).resolve(PrerequisiteCandidate("run:${run.plannedTaskId}", skill, item.requiredSkills))
        val evidence = evaluation?.let {
            RecordEvidence(persistence, clock).record(
                attemptId = recorded.attemptId,
                skill = skill,
                evidenceType = item.evidenceType,
                evaluation = it,
                independence = submission.independence(run.purpose),
                artifactId = recorded.artifactId,
                resource = item.ref,
                variantFamilyId = item.variantFamilyId,
                // Work on a Skill whose prerequisites are not there now is not the target's to blame (12B).
                prerequisiteSnapshot = if (gate.eligibility.waits) PrerequisiteSnapshot.CONTAMINATED else gate.readinessSnapshotRefs.joinToString(";").ifEmpty { null },
            ).evidenceIds
        }.orEmpty()
        return AnsweredItem(item.ref, recorded.attemptId, AnswerResult.of(signals), evidence)
    }

    /**
     * Shows the course's accepted answer after the answer froze, only because the learner asked (user decision,
     * 2026-10-26). The frozen attempt keeps what it showed (`TRUX-v0` §6.4); the reveal is recorded as H4 help after
     * submission and as a solution exposure, so this item and its variant family are never again a fresh check.
     */
    fun reveal(item: AssessmentItem, attemptId: Long): List<String> {
        val key = content.answerKeyFor(item.ref) ?: return emptyList()
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "assistance_event", at,
                    mapOf(
                        "attempt_id" to attemptId.toString(),
                        "level" to AssistanceLevel.H4.id,
                        "timing" to AssistanceTiming.AFTER_SUBMIT.id,
                        "target_scope" to AssistanceScope.TARGET_OBJECTIVE.id,
                        "source" to AssistanceSource.DETERMINISTIC_CONTENT.id,
                        "requested_by_user" to "1",
                    ),
                )
            )
            persistence.appendTruth(
                TruthRecord(
                    "exposure_record", at,
                    mapOf(
                        "resource_logical_id" to item.ref.logicalId,
                        "resource_version" to item.ref.version.toString(),
                        "variant_family_id" to item.variantFamilyId,
                        "exposure_kind" to ServeDailyMicroItem.SOLUTION_EXPOSURE,
                        "max_exposure_level" to AssistanceLevel.H4.id,
                        "source_attempt_id" to attemptId.toString(),
                    ),
                )
            )
        }
        return key.answers
    }

    /**
     * Shows the accepted answer **before** any answer was given, after the learner heard what that changes (`TRUX-v0`
     * §8.3, §8.6): the question becomes learning. No attempt exists, so nothing is measured and no evidence is written;
     * the solution exposure is recorded, so this item and its family are never again a fresh check.
     */
    fun revealBeforeAnswer(item: AssessmentItem): List<String> {
        val key = content.answerKeyFor(item.ref) ?: return emptyList()
        ServeDailyMicroItem(persistence, content, clock).recordSolutionExposure(item, attemptId = null, maxLevel = AssistanceLevel.H4.id)
        return key.answers
    }

    /**
     * The end of a run, finished or stopped: the touched Skills are recomputed, the report says what canonical state
     * actually changed, and only a change asks the planner for a new plan — keeping every task the learner started
     * (`TRUX-v0` §10.1, 13E). With nothing answered nothing is recomputed and nothing is replanned.
     */
    fun finish(run: RunnableTask, before: ProgramSnapshot, answered: List<AnsweredItem>, items: List<AssessmentItem>): ReportProgramChanges.Reported? {
        if (answered.none { it.evidenceIds.isNotEmpty() }) return null
        val touched = (listOf(run.skill) + items.flatMap { it.targetSkills }).distinct()
        val capacity = CapacitySettings(persistence, clock).read().input()
        return ReportProgramChanges(persistence, content, clock).report(before, touched, capacity, StartedTasks.today(persistence, clock))
    }
}
