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
import coach.model.CodeEvaluation
import coach.model.CodeEvaluationRoute
import coach.model.CodeNotMeasured
import coach.model.CodeVerdict
import coach.model.Comprehension
import coach.model.OpenResponseNotMeasured
import coach.model.OpenResponseRoute
import coach.model.RubricCriterion
import coach.model.RunAnswer
import coach.model.RunItemKind
import coach.ports.EvaluatorPort
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
 * How the app can judge one item (16G), or `null` when it cannot: an answer key (14F), the course's tests with their
 * shipped suite (14D; the learner runs them on their computer), or a rubric the evaluator may judge provisionally (14F).
 * A rubric item whose task needs a verified result is not served — no AI is asked for it.
 */
object RunItems {
    fun kind(item: AssessmentItem, content: ContentPort): RunItemKind? {
        if (content.answerKeyFor(item.ref) != null) return RunItemKind.ANSWER_KEY
        val suite = content.codeTestsFor(item.ref)
        if (suite != null) {
            return RunItemKind.CODE_TESTS.takeIf { CodeEvaluation.route(item, suite) == CodeEvaluationRoute.TESTS && content.codeSuiteText(item.ref) != null }
        }
        return RunItemKind.RUBRIC.takeIf { OpenResponse.route(item, null, content.rubricFor(item.ref)) == OpenResponseRoute.AI_RUBRIC }
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
        // 16G: an answer key, the course's tests or a rubric the evaluator may judge — not only an answer key (16F).
        val judgeable = task.items.filter { ref -> content.assessmentItem(ref)?.let { RunItems.kind(it, content) } != null && server.usable(ref, INTENT, evaluatorAvailable) }
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
    /** 16G: judges a rubric answer (provisional at most) and asks nothing for keys or tests. */
    private val evaluator: EvaluatorPort = NullEvaluator,
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
     * Records one code answer and what the course's tests say it showed (16G; 14D). The learner ran the tests on their
     * computer and pasted the report: a report that cannot be read, is for another item or suite, or reports an
     * environment error measures nothing, and **nothing is written** — the learner fixes it and sends again. Only a
     * report the suite can judge freezes an attempt (the code is its artifact) and becomes evidence.
     */
    fun answerCode(run: RunnableTask, item: AssessmentItem, code: String, report: String, provenance: ProvenanceOrigin, assistance: List<AssistanceEvent>): RunAnswer {
        if (code.isBlank()) return RunAnswer.NotRecorded(code = CodeNotMeasured.NOTHING_SUBMITTED, blank = true)
        val body = ArtifactBody.store(code) as? ArtifactBody.Stored.Inline ?: return RunAnswer.NotRecorded(tooLong = true)
        val verdict = EvaluateCode(content, evaluator).evaluate(item, code, report)
        if (verdict is CodeVerdict.NotMeasured && verdict.reason != CodeNotMeasured.EVALUATOR_DID_NOT_ANSWER) return RunAnswer.NotRecorded(code = verdict.reason)
        val submission = AttemptSubmission(item.ref, body.contentRef, provenance, assistance, plannedTaskId = run.plannedTaskId)
        val answered = record(run, item, submission, body, (verdict as? CodeVerdict.Measured)?.result)
        // 14E: code the learner did not write alone is followed by a comprehension check, written ones first.
        return RunAnswer.Recorded(answered, code = verdict, offer = Comprehension.offer(submission, content.comprehensionChecksFor(item.ref)))
    }

    /**
     * Records one open answer, then has the evaluator judge it by the rubric, criterion by criterion — provisional at
     * most (16G; 14F). The answer freezes first: if no evaluator answers, the attempt stays `evaluation_pending` with no
     * evidence, and it is judged again only when the learner asks ([reevaluate]).
     */
    fun answerOpen(run: RunnableTask, item: AssessmentItem, response: String, provenance: ProvenanceOrigin, assistance: List<AssistanceEvent>): RunAnswer {
        if (response.isBlank()) return RunAnswer.NotRecorded(blank = true)
        val body = ArtifactBody.store(response.trim()) as? ArtifactBody.Stored.Inline ?: return RunAnswer.NotRecorded(tooLong = true)
        // Frozen first: the learner's answer is in the store before any evaluator is asked.
        val submission = AttemptSubmission(item.ref, body.contentRef, provenance, assistance, plannedTaskId = run.plannedTaskId)
        val frozen = record(run, item, submission, body, result = null)
        return judge(run, item, frozen, response)
    }

    /**
     * Judges a pending open answer again, only because the learner asked (14F: nothing retries on its own). A result now
     * becomes evidence for the **same** frozen attempt; no evaluator again leaves it pending.
     */
    fun reevaluate(run: RunnableTask, item: AssessmentItem, pending: AnsweredItem, response: String): RunAnswer {
        require(pending.pending) { "only a pending evaluation is asked again" }
        return judge(run, item, pending, response)
    }

    /** The evaluator's judgement of a frozen open answer, as evidence for that same attempt; none leaves it pending. */
    private fun judge(run: RunnableTask, item: AssessmentItem, pending: AnsweredItem, response: String): RunAnswer {
        val verdict = EvaluateOpenResponse(content, persistence, clock, evaluator).evaluate(item, response.trim())
        val result = (verdict as? OpenResponseVerdict.Measured)?.result ?: return RunAnswer.Recorded(pending, open = verdict, criteria = content.rubricFor(item.ref)?.criteria.orEmpty())
        val evidence = evidence(run, item, pending.attemptId, pending.artifactId, result, checkNotNull(pending.independence) { "the attempt's independence was recorded with it" })
        return RunAnswer.Recorded(pending.copy(result = AnswerResult.of(signals(result)), evidenceIds = evidence, pending = false), open = verdict,
            criteria = content.rubricFor(item.ref)?.criteria.orEmpty())
    }

    /** The rubric, for the learner to check a frozen answer against (14F): practice, recorded as a solution exposure. */
    fun selfCheck(item: AssessmentItem, attemptId: Long): List<RubricCriterion> =
        EvaluateOpenResponse(content, persistence, clock, evaluator).selfCheck(item, attemptId).orEmpty()

    /** Freezes the attempt (one action, 11B) and, when there is a result, hands it to the evidence pipeline. */
    private fun record(run: RunnableTask, item: AssessmentItem, submission: AttemptSubmission, body: ArtifactBody.Stored.Inline,
                       result: EvaluationResult?): AnsweredItem {
        require(submission.artifactContentRef == body.contentRef) { "the submission carries this body" }
        val recorded = SubmitAttempt(persistence, clock).submit(submission)
        val independence = submission.independence(run.purpose)
        if (result == null) return AnsweredItem(item.ref, recorded.attemptId, AnswerResult.NOT_MEASURED, emptyList(), pending = true, artifactId = recorded.artifactId, independence = independence)
        val evidence = evidence(run, item, recorded.attemptId, recorded.artifactId, result, independence)
        return AnsweredItem(item.ref, recorded.attemptId, AnswerResult.of(signals(result)), evidence, artifactId = recorded.artifactId, independence = independence)
    }

    private fun evidence(run: RunnableTask, item: AssessmentItem, attemptId: Long, artifactId: Long?, result: EvaluationResult,
                         independence: coach.model.IndependenceClass): List<Long> {
        val skill = run.skill.takeIf { it in item.targetSkills } ?: item.targetSkills.first()
        val gate = ResolvePrerequisites(persistence).resolve(PrerequisiteCandidate("run:${run.plannedTaskId}", skill, item.requiredSkills))
        return RecordEvidence(persistence, clock).record(
            attemptId = attemptId, skill = skill, evidenceType = item.evidenceType, evaluation = result, independence = independence,
            artifactId = artifactId, resource = item.ref, variantFamilyId = item.variantFamilyId,
            prerequisiteSnapshot = if (gate.eligibility.waits) PrerequisiteSnapshot.CONTAMINATED else gate.readinessSnapshotRefs.joinToString(";").ifEmpty { null },
        ).evidenceIds
    }

    private fun signals(result: EvaluationResult) = when (result) {
        is EvaluationResult.Verified -> result.componentResults.map { it.signal }
        is EvaluationResult.Provisional -> result.componentResults.map { it.signal }
        is EvaluationResult.EvaluationPending -> emptyList()
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
