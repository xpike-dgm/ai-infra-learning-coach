package coach.application

import coach.model.AnswerResult
import coach.model.AnsweredItem
import coach.model.AssessmentItem
import coach.model.AssistanceLevel
import coach.model.AssistanceTiming
import coach.model.AttemptSubmission
import coach.model.CheckpointKind
import coach.model.ComprehensionCheck
import coach.model.ComprehensionOffer
import coach.model.ComprehensionResult
import coach.model.ExplanationForm
import coach.model.ExplanationOption
import coach.model.AssistanceInterpretation
import coach.model.InstructionMode
import coach.model.ObjectiveGateProfiles
import coach.model.ProvenanceOrigin
import coach.model.ResumeContext
import coach.model.RunnableTask
import coach.model.TutorAsk
import coach.model.TutorContext
import coach.model.TutorIntent
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRules
import coach.model.VersionedRef
import coach.model.WrongAnswerFinding
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord
import coach.ports.TutorPort

/**
 * Help inside a running task (16G, `HLPX-v0`): the tutor, another explanation, what a wrong answer said, the
 * comprehension check after code the learner did not write alone, and a durable pause.
 *
 * Every rule here is an earlier step's (`TUTX-v0` 14A, `WAAX-v0` 14B, `ALEX-v0` 14C, `ACCX-v0` 14E, `SESX-v0` 11C); this
 * only calls them from the run. The one thing added is where help shown **after** an answer froze is recorded: on that
 * attempt, as help after submission — before the answer froze, help travels with the attempt when it is submitted
 * (11B), and help never shown is recorded nowhere.
 */
class RunHelp(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
    private val tutor: TutorPort,
) {
    /** What one ask did: it was not ready yet (and says what it needs), or it was asked and something came back. */
    sealed interface Asked {
        data class NotReady(val preparation: TutorPreparation) : Asked
        data class Answered(val outcome: TutorOutcome) : Asked
    }

    private val askTutor get() = AskTutor(tutor, persistence, clock)

    /**
     * Asks the tutor. [item] is the question being worked, if any; [attemptId] the frozen attempt, if the answer froze.
     * Nothing is sent until the rules say the ask is ready (`TutorRules.prepare`): a level to choose, a consequence to say
     * first, a missing question — each comes back as [Asked.NotReady].
     */
    fun ask(ask: TutorAsk, item: AssessmentItem?, attemptId: Long?): Asked =
        when (val preparation = TutorRules.prepare(ask)) {
            is TutorPreparation.Ready -> {
                val outcome = askTutor.ask(preparation.request, item?.let { AskTutor.Item(it.ref, it.variantFamilyId) }, attemptId = attemptId)
                recordAfterAnswer(outcome, attemptId)
                Asked.Answered(outcome)
            }
            else -> Asked.NotReady(preparation)
        }

    /** The forms another explanation of [objective] can come in (14C); [seen] is this run's own memory, never stored. */
    fun menu(objective: VersionedRef, seen: Set<ExplanationForm>): List<ExplanationOption> =
        ExplainDifferently(content, persistence, askTutor).options(objective, seen)

    /** The course's own explanation of [objective], to return to at any time. */
    fun canonical(objective: VersionedRef) = ExplainDifferently(content, persistence, askTutor).canonical(objective)

    /**
     * Shows [form] for [objective]: written first, the tutor only when nothing written fits (14C). `null` when the form
     * has nothing true to show.
     */
    fun explain(base: TutorAsk, objective: VersionedRef, form: ExplanationForm, item: AssessmentItem?, attemptId: Long?): Asked? {
        val explain = ExplainDifferently(content, persistence, askTutor)
        return when (val preparation = explain.prepare(base, objective, form)) {
            is TutorPreparation.Ready -> {
                val outcome = explain.explain(objective, form, preparation.request, item?.let { AskTutor.Item(it.ref, it.variantFamilyId) }, attemptId)
                    ?: return null
                recordAfterAnswer(outcome, attemptId)
                Asked.Answered(outcome)
            }
            else -> Asked.NotReady(preparation)
        }
    }

    /** What the wrong answer said, read from the attribution the engines already made (14B); writes nothing. */
    fun wrongAnswer(skill: VersionedRef, evidenceIds: List<Long>): List<WrongAnswerFinding> =
        AnalyzeWrongAnswer(persistence).analyze(skill, persistence.objectivesOf(skill).map(ObjectiveGateProfiles::of), evidenceIds)

    /** The comprehension check offered after code the learner says they did not write alone (14E). */
    fun comprehensionOffer(item: AssessmentItem, submission: AttemptSubmission): ComprehensionOffer =
        CheckUnderstanding(content, askTutor).offer(item, submission)

    /**
     * The tutor's practice question about the learner's own code, or its response to their answer (14E): only where no
     * written check exists (written first), only once the answer froze, and never evidence. Help shown is still recorded
     * on the attempt as help after submission.
     */
    fun practice(ask: TutorAsk, item: AssessmentItem, attemptId: Long): Asked? =
        when (val preparation = TutorRules.prepare(ask.copy(intent = TutorIntent.CHECK_UNDERSTANDING))) {
            is TutorPreparation.Ready -> {
                val outcome = CheckUnderstanding(content, askTutor).practice(item, preparation.request, AskTutor.Item(item.ref, item.variantFamilyId), attemptId)
                    ?: return null
                recordAfterAnswer(outcome, attemptId)
                Asked.Answered(outcome)
            }
            else -> Asked.NotReady(preparation)
        }

    /**
     * One answer to a written check (14E), judged by its key. A choice is evidence of the kind the check declares, for
     * its own Objective, on the code's attempt — never of the item's own production; a skip writes nothing. The learner
     * answers the check themselves with no help, so it is independent — except in a teaching task, which is practice by
     * design (`2D` §6): the same rule as every answer (`AssistanceInterpretation`).
     */
    fun comprehensionAnswer(run: RunnableTask, item: AssessmentItem, check: ComprehensionCheck, chosen: String?, attemptId: Long): ComprehensionResult {
        val result = CheckUnderstanding(content, askTutor).answer(item, check, chosen)
        if (result is ComprehensionResult.Measured) {
            val skill = run.skill.takeIf { it in item.targetSkills } ?: item.targetSkills.first()
            val independence = AssistanceInterpretation.independence(emptyList(), ProvenanceOrigin.USER_AUTHORED, run.purpose)
            RecordEvidence(persistence, clock).record(attemptId, skill, check.evidenceType, result.result, independence)
        }
        return result
    }

    /**
     * A durable pause (11C): where the run is — the questions shown and the ones left — saved as one checkpoint, so the
     * planner can offer the work again as continued learning (`TRUX-v0` §7.3, 12D). Nothing about how it went is saved,
     * and nothing is scored. [kind] is what `PausePolicy` decided — a high-stakes pause is marked and is never continued
     * as independent work (§7.2). `null` when there is nothing to come back to: nothing done yet, or nothing left.
     */
    fun pause(run: RunnableTask, kind: CheckpointKind, shown: List<VersionedRef>, remaining: List<VersionedRef>): Long? {
        if (shown.isEmpty() || remaining.isEmpty()) return null
        return ResumeCheckpoints(persistence, clock).record(
            ResumeContext(
                kind = kind,
                learningNeedKey = run.needKey,
                sourceTaskId = "planned_task:${run.plannedTaskId}",
                checkpointId = "run:${run.plannedTaskId}:${shown.size}",
                completedSegments = shown.map { it.toString() },
                remainingSegments = remaining.map { it.toString() },
            )
        )
    }

    companion object {
        /**
         * The learner's ask for one question of the run, before any rule has looked at it. While the answer is open it is
         * `during_attempt`; once it froze, `after_failure` when it did not match and `after_submit` otherwise (`2D`). What
         * may leave the device is only [TutorContext]: the question, its Objectives, the frozen answer once it froze, and
         * the course's own explanation of those Objectives. The instruction mode is the course's: every shipped task is
         * Turkish-primary and none declares another (`TEIP-v0` §5) — the tutor never picks a language.
         */
        fun askFor(
            run: RunnableTask, item: AssessmentItem, prompt: String, intent: TutorIntent, frozen: AnsweredItem?, frozenWork: String?,
            ceiling: AssistanceLevel? = null, acknowledged: Boolean = false, question: String? = null,
            checkQuestion: String? = null, learnerAnswer: String? = null,
        ): TutorAsk = TutorAsk(
            intent = intent,
            purpose = run.purpose,
            timing = when {
                frozen == null -> AssistanceTiming.DURING_ATTEMPT
                frozen.result == AnswerResult.NOT_RIGHT || frozen.result == AnswerResult.PARTLY -> AssistanceTiming.AFTER_FAILURE
                else -> AssistanceTiming.AFTER_SUBMIT
            },
            ceiling = ceiling.takeIf { frozen == null },
            consequenceAcknowledged = acknowledged,
            instructionMode = InstructionMode.TURKISH_PRIMARY,
            context = TutorContext(
                targetObjectives = item.targetObjectives,
                taskText = prompt,
                learnerWork = frozenWork?.takeIf { frozen != null },
                canonicalExplanation = run.lesson.filter { it.objective in item.targetObjectives }.joinToString("\n\n") { it.text }.ifEmpty { null },
            ),
            learnerQuestion = question?.takeIf { intent == TutorIntent.QUESTION },
            checkQuestion = checkQuestion,
            learnerAnswer = learnerAnswer,
        )
    }

    /** Help shown after the answer froze is recorded on that attempt (`TRUX-v0` §8.5); before, it goes with the attempt. */
    private fun recordAfterAnswer(outcome: TutorOutcome, attemptId: Long?) {
        val event = (outcome as? TutorOutcome.Shown)?.event ?: return
        if (attemptId == null || event.timing !in setOf(AssistanceTiming.AFTER_SUBMIT, AssistanceTiming.AFTER_FAILURE)) return
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "assistance_event", at,
                    mapOf(
                        "attempt_id" to attemptId.toString(),
                        "level" to event.level.id,
                        "timing" to event.timing.id,
                        "target_scope" to event.scope.id,
                        "source" to event.source.id,
                        "requested_by_user" to if (event.requestedByUser) "1" else "0",
                    ),
                )
            )
        }
    }
}
