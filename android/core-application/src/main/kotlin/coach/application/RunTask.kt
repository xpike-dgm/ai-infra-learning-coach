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
import coach.model.AssessmentBlueprint
import coach.model.AssessmentScope
import coach.model.AuthoredTask
import coach.model.BlueprintCodecs
import coach.model.BlueprintEvidenceFact
import coach.model.BlueprintScopes
import coach.model.BlueprintSlotOutcome
import coach.model.EvidenceDispositions
import coach.model.Measurement
import coach.model.MeasurementKind
import coach.engines.BlueprintComposer
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

        val needOpen = BuildDailyPlan(persistence, content, clock, evaluatorAvailable).openNeeds().any { it.needKey == entry.needKey }
        // 16H: a planned blueprint slot or diagnostic check is a measurement, not an authored task (`ASUX-v0` §4).
        BlueprintComposer.scopeOfCandidate(entry.candidateId)?.let { scope -> return openSlot(stored, row.plannedTaskId, entry, scope, needOpen) }
        if (entry.candidateId.startsWith(DIAGNOSTIC_PREFIX)) return openDiagnostic(stored, row.plannedTaskId, entry, needOpen)

        val ref = AuthoredTask.refOfCandidate(entry.candidateId)
        val task = ref?.let(content::authoredTask)?.takeIf { it.primarySkill == entry.primarySkill }
        val prerequisites = task != null && !ResolvePrerequisites(persistence)
            .resolve(PrerequisiteCandidate(entry.candidateId, entry.primarySkill, task.requiredSkills)).eligibility.waits
        val compatible = task != null && persistence.skill(entry.primarySkill) != null
        if (task == null || !compatible) return TaskEntryFacts(true, prerequisites, needOpen, false, false)

        val (items, gap) = servable(task)
        val capable = gap == null
        val run = if (prerequisites && needOpen && capable) runnable(stored, row.plannedTaskId, entry, task, items) else null
        return TaskEntryFacts(true, prerequisites, needOpen, true, capable, gap, run)
    }

    /**
     * One slot of this cycle's weekly or monthly blueprint (13A, 13B). The slot must still be in the stored blueprint of
     * **this** cycle, ready, for this Skill, with its item published and judgeable here — an earlier cycle's slot is not
     * debt, and a slot the store cannot read is not guessed. Its item's own prerequisites are the gate's to judge.
     */
    private fun openSlot(stored: StoredPlan, plannedTaskId: Long, entry: PlannedEntry, scope: AssessmentScope, needOpen: Boolean): TaskEntryFacts {
        val session = persistence.latestAssessmentSession(scope)
        val blueprint = session?.record?.payload?.get("blueprint")?.let { BlueprintCodecs.decode(scope, it) }
            ?.takeIf { it.cycleId == BlueprintScopes.cycleOf(scope, clock.now().studyDay) }
        val slot = blueprint?.readySlots?.singleOrNull {
            BlueprintComposer.candidateId(scope, blueprint.cycleId, it.slotId) == entry.candidateId && it.targetSkill == entry.primarySkill
        }
        val item = slot?.item?.takeIf { persistence.resourceVersion(it) != null }?.let(content::assessmentItem)
        val prerequisites = slot != null && !ResolvePrerequisites(persistence)
            .resolve(PrerequisiteCandidate(entry.candidateId, entry.primarySkill, slot.itemRequiredSkills)).eligibility.waits
        val compatible = session != null && slot != null && item != null && persistence.skill(entry.primarySkill) != null
        if (!compatible) return TaskEntryFacts(true, prerequisites, needOpen, false, false)
        val measurement = Measurement(if (scope == AssessmentScope.MONTHLY_CAPABILITY) MeasurementKind.MONTHLY_SLOT else MeasurementKind.WEEKLY_SLOT,
            slot!!.role.intent, session!!.id, slot.slotId)
        return measured(stored, plannedTaskId, entry, item!!, slot.targetObjectives, measurement, prerequisites, needOpen)
    }

    /**
     * One check of the learner's open diagnostic (13F). Only the diagnostic the learner opened, and only while it is the
     * open one: a withdrawn or replaced request's checks are not run. Its item's prerequisites are the gate's.
     */
    private fun openDiagnostic(stored: StoredPlan, plannedTaskId: Long, entry: PlannedEntry, needOpen: Boolean): TaskEntryFacts {
        val parts = entry.candidateId.split(':')
        val sessionId = parts.getOrNull(1)?.toLongOrNull()
        val objective = parts.getOrNull(2)?.let(::refOf)
        val itemRef = parts.getOrNull(3)?.let(::refOf)
        val active = DiagnosticSessions.active(persistence)
        val target = active?.takeIf { parts.size == 4 && it.sessionId == sessionId }?.scope?.targets
            ?.singleOrNull { it.objective == objective && it.skill == entry.primarySkill }
        val item = itemRef?.takeIf { persistence.resourceVersion(it) != null }?.let(content::assessmentItem)
            ?.takeIf { objective in it.targetObjectives }
        val prerequisites = item != null && !ResolvePrerequisites(persistence)
            .resolve(PrerequisiteCandidate(entry.candidateId, entry.primarySkill, item.requiredSkills)).eligibility.waits
        val compatible = target != null && item != null && persistence.skill(entry.primarySkill) != null
        if (!compatible) return TaskEntryFacts(true, prerequisites, needOpen, false, false)
        val measurement = Measurement(MeasurementKind.DIAGNOSTIC, AssessmentIntent.MASTERY_EVIDENCE, sessionId!!)
        return measured(stored, plannedTaskId, entry, item!!, listOf(objective!!), measurement, prerequisites, needOpen)
    }

    /** A single-item measurement: judgeable here, usable for its intent, and never seen before. */
    private fun measured(stored: StoredPlan, plannedTaskId: Long, entry: PlannedEntry, item: AssessmentItem, objectives: List<VersionedRef>,
                         measurement: Measurement, prerequisites: Boolean, needOpen: Boolean): TaskEntryFacts {
        val server = ServeDailyMicroItem(persistence, content, clock)
        val gap = when {
            RunItems.kind(item, content) == null || !server.usable(item.ref, measurement.intent, evaluatorAvailable) -> CapabilityGap.NO_ITEM_THIS_APP_CAN_EVALUATE
            seen(item.ref) -> CapabilityGap.NO_UNSEEN_ITEM
            else -> null
        }
        val run = if (prerequisites && needOpen && gap == null) RunnableTask(
            plannedTaskId = plannedTaskId, position = entry.position, planVersionId = stored.planVersionId, task = null,
            title = entry.title, purpose = entry.purpose, skill = entry.primarySkill, targetObjectives = objectives,
            plannedMinutes = entry.plannedMinutes, needKey = entry.needKey, lesson = emptyList(), items = listOf(item.ref),
            measurement = measurement,
        ) else null
        return TaskEntryFacts(true, prerequisites, needOpen, true, gap == null, gap, run)
    }

    private fun refOf(text: String): VersionedRef? =
        Regex("^(.+)@v(\\d+)$").matchEntire(text)?.let { VersionedRef(it.groupValues[1], it.groupValues[2].toInt()) }

    /**
     * The task's items this app can serve now: judged by an answer key, the course's tests or a rubric (16G), usable as
     * the store's own validation allows for the task's intent, and never seen before. An `assess` task runs in the
     * assessment interior since 16H (`TRUX-v0` §5), its items trusted for a verification.
     */
    private fun servable(task: AuthoredTask): Pair<List<VersionedRef>, CapabilityGap?> {
        val server = ServeDailyMicroItem(persistence, content, clock)
        val intent = intentOf(task)
        // 16G: an answer key, the course's tests or a rubric the evaluator may judge — not only an answer key (16F).
        val judgeable = task.items.filter { ref -> content.assessmentItem(ref)?.let { RunItems.kind(it, content) } != null && server.usable(ref, intent, evaluatorAvailable) }
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
            // 16H: a daily assessment is a check, not teaching — it shows no lesson first.
            lesson = if (task.purpose == TaskPurpose.ASSESS) emptyList() else task.targetObjectives.flatMap { objective ->
                content.explanationsFor(objective).filter { it.form == ExplanationForm.CANONICAL }
            },
            items = items,
            measurement = if (task.purpose == TaskPurpose.ASSESS) Measurement(MeasurementKind.DAILY_ASSESSMENT, intentOf(task), sessionId = null) else null,
        )

    companion object {
        /** A task's own items are low-stakes checks of its Objectives (`DMA-v0` §3); none of them is a verification. */
        val INTENT = AssessmentIntent.CHECKPOINT

        /** An `assess` task serves a verification (`AuthoredTask`'s purpose → trigger); every other task's items are checks. */
        fun intentOf(task: AuthoredTask): AssessmentIntent = if (task.purpose == TaskPurpose.ASSESS) AssessmentIntent.VERIFICATION else INTENT

        const val DIAGNOSTIC_PREFIX = "diagnostic:"
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
            val offer = server.offer(ref, run.measurement?.intent ?: OpenTask.INTENT, evaluatorAvailable)
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
        val submission = submission(run, item, body, provenance, assistance)
        val recorded = SubmitAttempt(persistence, clock).submit(submission)

        val verdict = OpenResponse.byKey(item, key, response.trim())
        val evaluation = (verdict as? OpenResponseVerdict.Measured)?.result
        val signals = when (evaluation) {
            is EvaluationResult.Verified -> evaluation.componentResults.map { it.signal }
            is EvaluationResult.Provisional -> evaluation.componentResults.map { it.signal }
            else -> emptyList()
        }
        // 16H: one evidence path for every answer, so a blueprint slot's evidence is the slot's (13A).
        val evidence = evaluation?.let { evidence(run, item, recorded.attemptId, recorded.artifactId, it, submission.independence(run.purpose)) }.orEmpty()
        return AnsweredItem(item.ref, recorded.attemptId, AnswerResult.of(signals), evidence)
    }

    /**
     * Opens a daily assessment's session when the learner begins (16H; `DDM-v0` `assessment_session`): one `daily` row,
     * so its attempts are one session's. A slot's and a diagnostic's session already exist, and a run that is not a
     * measurement opens none — those return the run unchanged.
     */
    fun beginSession(run: RunnableTask): RunnableTask {
        val measurement = run.measurement?.takeIf { it.kind == MeasurementKind.DAILY_ASSESSMENT && it.sessionId == null } ?: return run
        val id = persistence.inTransaction {
            persistence.appendTruth(TruthRecord("assessment_session", clock.now(), mapOf("scope" to AssessmentScope.DAILY_MICRO.storedAs)))
        }
        return run.copy(measurement = measurement.copy(sessionId = id))
    }

    /**
     * The learner's report that an answered question was wrong or unanswerable (16H, user decision; `ASUX-v0` §10). Its
     * evidence is held as contested — a disposition is appended, the row is never edited (13D) — and nothing about the
     * learner gets worse for reporting it. Only in a measurement, and only on what that answer wrote.
     */
    fun contest(run: RunnableTask, answered: AnsweredItem): List<Long> {
        require(run.measurement != null) { "an item is contested inside an assessment session" }
        val disposition = RecordDisposition(persistence, clock)
        return answered.evidenceIds.map { disposition.record(it, EvidenceDispositions.CONTESTED, CONTESTED_REASON, DECIDED_BY_LEARNER) }
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
        val submission = submission(run, item, body, provenance, assistance)
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
        val submission = submission(run, item, body, provenance, assistance)
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

    /** One attempt with its help, provenance, planned task and — in a measurement — its assessment session (11B, 16H). */
    private fun submission(run: RunnableTask, item: AssessmentItem, body: ArtifactBody.Stored.Inline, provenance: ProvenanceOrigin,
                           assistance: List<AssistanceEvent>) =
        AttemptSubmission(item.ref, body.contentRef, provenance, assistance, plannedTaskId = run.plannedTaskId,
            assessmentSessionId = run.measurement?.sessionId)

    private fun evidence(run: RunnableTask, item: AssessmentItem, attemptId: Long, artifactId: Long?, result: EvaluationResult,
                         independence: coach.model.IndependenceClass): List<Long> {
        val skill = run.skill.takeIf { it in item.targetSkills } ?: item.targetSkills.first()
        val gate = ResolvePrerequisites(persistence).resolve(PrerequisiteCandidate("run:${run.plannedTaskId}", skill, item.requiredSkills))
        val gateSnapshot = if (gate.eligibility.waits) PrerequisiteSnapshot.CONTAMINATED else gate.readinessSnapshotRefs.joinToString(";").ifEmpty { null }
        // 16H: a blueprint slot's evidence is recorded as the slot's, so a prerequisite this same session just showed
        // missing does not count against its target (`WBA-v0` §25, `MCA-v0` §20).
        val measurement = run.measurement
        if (measurement != null && measurement.kind.isSlot) {
            val blueprint = checkNotNull(SlotSessions.blueprint(persistence, measurement)) { "the slot's blueprint was read at entry" }
            return RecordSlotEvidence(persistence, clock).record(
                blueprint = blueprint, slotId = measurement.slotId!!, attemptId = attemptId, evaluation = result, independence = independence,
                sessionSoFar = SlotSessions.outcomes(persistence, blueprint, measurement.sessionId!!), gateSnapshot = gateSnapshot, artifactId = artifactId,
            ).evidenceIds
        }
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
        // 16H: inside a blueprint session a prerequisite shown missing after a dependent slot was answered contaminates
        // that slot's evidence by an appended disposition, never an edit (13D).
        val measurement = run.measurement
        if (measurement != null && measurement.kind.isSlot) {
            SlotSessions.blueprint(persistence, measurement)?.let { blueprint ->
                ApplyRetroactiveContamination(persistence, clock).apply(blueprint, SlotSessions.outcomes(persistence, blueprint, measurement.sessionId!!))
            }
        }
        val touched = (listOf(run.skill) + items.flatMap { it.targetSkills }).distinct()
        val capacity = CapacitySettings(persistence, clock).read().input()
        return ReportProgramChanges(persistence, content, clock).report(before, touched, capacity, StartedTasks.today(persistence, clock))
    }

    companion object {
        /** Why a contested row is held, and who decided it: the learner's report (`DDM-v0`'s deciders). */
        const val CONTESTED_REASON = "learner_reported_item_problem"
        const val DECIDED_BY_LEARNER = "user_report"
    }
}

/**
 * A blueprint session as a slot run reads it (16H): its blueprint, from the session row the slot was planned from, and
 * what each of its slots has produced so far — read from the attempts that name this session, never kept in memory.
 */
internal object SlotSessions {

    fun blueprint(persistence: PersistencePort, measurement: Measurement): AssessmentBlueprint? {
        val id = measurement.sessionId ?: return null
        val row = persistence.readTruth("assessment_session", id) ?: return null
        return row.payload["blueprint"]?.let { BlueprintCodecs.decode(measurement.scope, it) }
    }

    fun outcomes(persistence: PersistencePort, blueprint: AssessmentBlueprint, sessionId: Long): List<BlueprintSlotOutcome> =
        blueprint.readySlots.mapNotNull { slot ->
            val rows = slot.targetObjectives.flatMap { persistence.evidenceFor(it) }
                .filter { it.assessmentSessionId == sessionId && it.resource == slot.item }
            if (rows.isEmpty()) return@mapNotNull null
            BlueprintSlotOutcome(
                slotId = slot.slotId,
                submitted = true,
                attemptId = rows.mapNotNull { it.attemptId }.firstOrNull(),
                evidence = rows.map {
                    BlueprintEvidenceFact(it.id, it.objective, it.outcome, it.evaluatorStatus, it.independenceClass, prerequisiteContaminated = !it.prerequisiteValid)
                },
            )
        }
}
