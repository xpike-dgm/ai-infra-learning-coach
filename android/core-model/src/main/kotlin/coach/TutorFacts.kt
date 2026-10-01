package coach.model

/**
 * `TUTX-v0 / D-105` — the tutor's behaviour, as types and pure rules.
 *
 * **The tutor teaches on request and never decides.** It answers only what was asked, never reveals more
 * than the learner chose, never claims what the learner can do, and every piece of help it actually shows
 * is recorded for what it is. Help it did not show is recorded nowhere, and nothing it says is evidence.
 *
 * These rules live in `core-model` because both sides of the port need them: `core-application` asks the
 * tutor and records what was shown, and `core-presentation` tells the learner what an ask will change.
 * Neither may depend on the other (`MSBX-v0`).
 */

/**
 * The five things the learner can ask the tutor for (`TUTX-v0` §5). Closed: there is no "anything" intent,
 * so a request the contract has no rule for cannot be built.
 */
enum class TutorIntent(val id: String) {
    /** Help with the item being worked, at the level the learner chose. */
    HINT("hint"),

    /** The concept explained another way (`LEARNING_BEHAVIOR_RULES` §9; content of the explanation is 14C's). */
    EXPLAIN_DIFFERENTLY("explain_differently"),

    /** The learner's own question, in their own words (user decision, 2026-10-01). */
    QUESTION("question"),

    /** Why a frozen answer went wrong. Only after the answer is submitted (content is 14B's). */
    EXPLAIN_MISTAKE("explain_mistake"),

    /** What a non-target language segment means (`TEIP-v0` §5.1). Support, not help with the target. */
    GLOSS("gloss"),
}

/**
 * `TEIP-v0` §5 — the instruction/scaffold modes, in its order. The reply's language follows the mode the
 * task declares; the tutor never picks a language to "force English" (`TEIP-v0` §5.2).
 */
enum class InstructionMode(val id: String) {
    TURKISH_PRIMARY("turkish_primary"),
    BILINGUAL_PARALLEL("bilingual_parallel"),
    ENGLISH_WITH_TARGETED_GLOSS("english_with_targeted_gloss"),
    ENGLISH_PRIMARY_WITH_NON_TARGET_SUPPORT("english_primary_with_non_target_support"),
    ENGLISH_UNSCAFFOLDED("english_unscaffolded"),
}

/**
 * One language segment the learner wants glossed. Whether it is the learning target is the task's
 * metadata (`TEIP-v0` §4), never guessed here, so it has no default.
 */
data class GlossSegment(val text: String, val isTarget: Boolean)

/**
 * **The only content that may leave the device for one tutor request** (`AIAX-v0` §11, `TUTX-v0` §7).
 *
 * Evidence history, mastery, retention, weakness and readiness state, the plan, the profile, exposure
 * records, provenance, planner traces and other attempts have no field here, so they cannot be sent by
 * mistake. The reference solution is curriculum content, not learner data, and it may travel only once it
 * can no longer give anything away (see [TutorRules.prepare]).
 */
data class TutorContext(
    val targetObjectives: List<VersionedRef>,
    val taskText: String,
    val learnerWork: String? = null,
    val segment: GlossSegment? = null,
    val referenceSolution: String? = null,
    /**
     * The course's own explanation of the Objective (14C, `ALEX-v0`): curriculum text, not the answer to any item and
     * not learner data, so it may travel whenever the learner asks for the concept again. It is what an AI
     * alternative is grounded in and must not contradict.
     */
    val canonicalExplanation: String? = null,
)

/**
 * What the learner asked, before any rule has looked at it. The screen builds this; only
 * [TutorRules.prepare] can turn it into a [TutorRequest] the port will see.
 *
 * [timing] is `null` when no attempt is in play — a lesson or a worked example — and is otherwise the
 * moment relative to the attempt, in `2D`'s own vocabulary. [ceiling] is the most the answer may reveal,
 * chosen by the learner while an attempt is open; outside one it is not asked (user decision, 2026-10-01).
 */
data class TutorAsk(
    val intent: TutorIntent,
    val purpose: TaskPurpose,
    val timing: AssistanceTiming?,
    val ceiling: AssistanceLevel?,
    val consequenceAcknowledged: Boolean,
    val instructionMode: InstructionMode,
    val context: TutorContext,
    val learnerQuestion: String? = null,
    /** How the concept should be explained again (14C): the learner's choice, only for `explain_differently`. */
    val form: ExplanationForm? = null,
)

/**
 * A request that passed [TutorRules.prepare]. Its constructor is `internal`, so no other module can build
 * one that skipped the rules — not the screen, not a use case, not an adapter.
 *
 * [recordedLevel] and [scope] are what will be recorded if help is shown; [recordedLevel] is `null` exactly
 * when no attempt is in play, because then nothing is being measured and nothing is recorded.
 */
class TutorRequest internal constructor(
    val intent: TutorIntent,
    val purpose: TaskPurpose,
    val timing: AssistanceTiming?,
    val ceiling: AssistanceLevel?,
    val scope: AssistanceScope,
    val recordedLevel: AssistanceLevel?,
    val instructionMode: InstructionMode,
    val context: TutorContext,
    val learnerQuestion: String?,
    val form: ExplanationForm? = null,
) {
    /** An attempt is open — the learner has not yet frozen an answer to the item being worked. */
    val attemptOpen: Boolean get() = timing == AssistanceTiming.BEFORE_ATTEMPT || timing == AssistanceTiming.DURING_ATTEMPT
}

/** Why an ask is not yet a request. None of these is a refusal of help: each says what to ask instead. */
enum class TutorRedirectReason(val id: String) {
    /** A hint is about an item being worked; in a lesson, ask for another explanation. */
    NO_ITEM_IS_BEING_WORKED("no_item_is_being_worked"),

    /** Nothing has been answered yet, so there is no mistake to explain; ask for a hint. */
    NO_ANSWER_IS_FROZEN_YET("no_answer_is_frozen_yet"),

    /** A hint is help *before* an answer; after one, ask why it went wrong or ask a question. */
    ANSWER_ALREADY_FROZEN("answer_already_frozen"),

    /** The segment is the learning target: glossing it would be help with the target, which is a hint. */
    SEGMENT_IS_THE_TARGET("segment_is_the_target"),
}

/** What an ask is missing before it can be sent. The learner fills it in; nothing is refused. */
enum class TutorMissing(val id: String) {
    QUESTION_TEXT("question_text"),
    FROZEN_ANSWER("frozen_answer"),
    SEGMENT("segment"),
}

/**
 * The result of looking at an ask. **There is no "no help" outcome**: help is always requestable
 * (`TRUX-v0` §8.1, `ASUX-v0` §8.1), so every non-ready outcome names what would make it ready.
 */
sealed interface TutorPreparation {
    data class Ready(val request: TutorRequest) : TutorPreparation

    /**
     * An attempt is open and the ask touches the target: the learner chooses how much the answer may
     * reveal before anything is sent (user decision, 2026-10-01).
     */
    data object LevelNeeded : TutorPreparation

    /**
     * What will be recorded reveals target reasoning, and the learner has not yet been told what that
     * changes (`TRUX-v0` §8.3). [timing] decides the wording: after an answer is frozen the attempt itself
     * is untouched, and saying otherwise would be untrue.
     */
    data class DisclosureNeeded(val level: AssistanceLevel, val timing: AssistanceTiming) : TutorPreparation

    data class Redirect(val to: TutorIntent, val reason: TutorRedirectReason) : TutorPreparation

    data class Incomplete(val missing: TutorMissing) : TutorPreparation
}

/** Which model and which instructions produced a reply. Shown to the learner, never stored as evidence. */
data class TutorRef(
    val provider: String,
    val model: String,
    val instructionsVersion: String,
)

/**
 * A schema-valid reply (`tutor_reply/1`). It has no field for a verdict, a score, a mastery claim, a plan,
 * a confirmed misconception or anything else the tutor may not decide, so the adapter has nowhere to put
 * one. [text] is teaching text for the learner and is never parsed for a verdict (`AIAX-v0` §5.1).
 *
 * [revealedLevel] is the tutor's own declaration of how far its text goes. It cannot be checked, so it is
 * used only to **refuse** a reply that admits going past the learner's ceiling — never to record less help.
 */
data class TutorContent(
    val intent: TutorIntent,
    val text: String,
    val revealedLevel: AssistanceLevel?,
    val instructionMode: InstructionMode,
)

/**
 * What the port returned. A non-answer is `AIAX-v0`'s taxonomy unchanged: refused, timed out, transport
 * error, invalid response or unavailable. **A refusal is not the learner's fault** and is never recorded.
 */
sealed interface TutorReply {
    data class Delivered(val content: TutorContent, val tutorRef: TutorRef) : TutorReply
    data class NotDelivered(val reason: PendingReason) : TutorReply
}

/**
 * Help written by a person and shipped as content (15). It is what the learner sees when the tutor gives
 * no usable answer — nothing is fabricated in its place. Its level is known, because a person wrote it
 * for that level.
 */
data class AuthoredHelp(
    val intent: TutorIntent,
    val level: AssistanceLevel,
    val text: String,
    val content: VersionedRef,
) {
    init {
        require(text.isNotBlank()) { "authored help has text" }
    }
}

/** What happened to one request, after the reply was checked. */
sealed interface TutorOutcome {
    /**
     * Help the learner actually saw. [event] is what goes with the attempt (`TRUX-v0` §8.5) and is `null`
     * exactly when no attempt is in play. [revealsTargetReasoning] means a solution was shown, which is an
     * exposure from that moment on (`QAB-v0` §24). [convertsItemToLearning] means a measuring item became
     * learning (`ASUX-v0` §8.3); it is shown, never silent, and never a violation.
     */
    data class Shown(
        val text: String,
        val source: AssistanceSource,
        val event: AssistanceEvent?,
        val tutorRef: TutorRef?,
        val revealsTargetReasoning: Boolean,
        val convertsItemToLearning: Boolean,
        val endsDiagnosticFastPath: Boolean,
    ) : TutorOutcome

    /**
     * Nothing was shown, so **nothing is recorded**: the attempt keeps whatever independence it had and the
     * learner may ask again or use the help ladder. The reason is for the screen's honesty only.
     */
    data class NotShown(val reason: PendingReason) : TutorOutcome
}

object TutorRules {

    /** Purposes whose items measure independent capability (`ASUX-v0` §8.3, `TRUX-v0` §8.6, `VDW-v0`). */
    val measuringPurposes: Set<TaskPurpose> = setOf(TaskPurpose.ASSESS, TaskPurpose.RETAIN, TaskPurpose.DIAGNOSE)

    private val frozen = setOf(AssistanceTiming.AFTER_SUBMIT, AssistanceTiming.AFTER_FAILURE)

    /**
     * Turns an ask into a request, or says what it still needs. The order is the learner's order: what to
     * ask, then what is missing, then how much it may reveal, then what that changes.
     */
    fun prepare(ask: TutorAsk): TutorPreparation {
        val attemptOpen = ask.timing == AssistanceTiming.BEFORE_ATTEMPT || ask.timing == AssistanceTiming.DURING_ATTEMPT
        val answerFrozen = ask.timing in frozen

        when (ask.intent) {
            TutorIntent.HINT -> {
                if (ask.timing == null) return TutorPreparation.Redirect(TutorIntent.EXPLAIN_DIFFERENTLY, TutorRedirectReason.NO_ITEM_IS_BEING_WORKED)
                if (answerFrozen) return TutorPreparation.Redirect(TutorIntent.EXPLAIN_MISTAKE, TutorRedirectReason.ANSWER_ALREADY_FROZEN)
            }
            TutorIntent.EXPLAIN_MISTAKE -> {
                if (ask.timing == null) return TutorPreparation.Redirect(TutorIntent.EXPLAIN_DIFFERENTLY, TutorRedirectReason.NO_ITEM_IS_BEING_WORKED)
                if (!answerFrozen) return TutorPreparation.Redirect(TutorIntent.HINT, TutorRedirectReason.NO_ANSWER_IS_FROZEN_YET)
                if (ask.context.learnerWork.isNullOrBlank()) return TutorPreparation.Incomplete(TutorMissing.FROZEN_ANSWER)
            }
            TutorIntent.GLOSS -> {
                val segment = ask.context.segment ?: return TutorPreparation.Incomplete(TutorMissing.SEGMENT)
                if (segment.text.isBlank()) return TutorPreparation.Incomplete(TutorMissing.SEGMENT)
                // Glossing the target English would answer it for the learner (`TEIP-v0` §5.3).
                if (segment.isTarget) {
                    return if (ask.timing == null || answerFrozen) {
                        TutorPreparation.Redirect(TutorIntent.EXPLAIN_DIFFERENTLY, TutorRedirectReason.SEGMENT_IS_THE_TARGET)
                    } else {
                        TutorPreparation.Redirect(TutorIntent.HINT, TutorRedirectReason.SEGMENT_IS_THE_TARGET)
                    }
                }
            }
            TutorIntent.QUESTION ->
                if (ask.learnerQuestion.isNullOrBlank()) return TutorPreparation.Incomplete(TutorMissing.QUESTION_TEXT)
            TutorIntent.EXPLAIN_DIFFERENTLY -> Unit
        }

        val scope = if (ask.intent == TutorIntent.GLOSS) AssistanceScope.NON_TARGET_SUPPORT else AssistanceScope.TARGET_OBJECTIVE
        val recordedLevel: AssistanceLevel? = when {
            ask.timing == null -> null
            // A gloss orients and solves nothing; its scope is what keeps it from touching the target.
            scope == AssistanceScope.NON_TARGET_SUPPORT -> AssistanceLevel.H1
            attemptOpen -> ask.ceiling ?: return TutorPreparation.LevelNeeded
            // After an answer is frozen no level is asked, and the explanation may well show the solution:
            // the record never claims less help than may have been given.
            else -> AssistanceLevel.H4
        }

        if (recordedLevel != null && scope == AssistanceScope.TARGET_OBJECTIVE && recordedLevel.revealsTargetReasoning && !ask.consequenceAcknowledged) {
            return TutorPreparation.DisclosureNeeded(recordedLevel, ask.timing!!)
        }

        // The reference solution can give nothing away once the answer is frozen, or once the learner chose
        // to see the full solution; any earlier and it would be the answer leaving the device (TUTX-v0 §7).
        if (ask.context.referenceSolution != null) {
            require(answerFrozen || (attemptOpen && ask.ceiling == AssistanceLevel.H4) || ask.timing == null) {
                "a reference solution may travel only after the answer is frozen, with H4 chosen, or outside an attempt"
            }
        }
        require(ask.context.segment == null || ask.intent == TutorIntent.GLOSS) { "only a gloss carries a segment" }
        require(ask.learnerQuestion == null || ask.intent == TutorIntent.QUESTION) { "only a question carries question text" }
        require(ask.form == null || ask.intent == TutorIntent.EXPLAIN_DIFFERENTLY) { "only an explanation carries a form" }
        // A form the tutor may not write (a misconception contrast, the course's own explanation) never reaches it (14C).
        require(ask.form == null || ask.form.aiAllowed) { "the tutor is never asked for a form only written content may give" }

        return TutorPreparation.Ready(
            TutorRequest(
                intent = ask.intent,
                purpose = ask.purpose,
                timing = ask.timing,
                ceiling = if (attemptOpen && scope == AssistanceScope.TARGET_OBJECTIVE) ask.ceiling else null,
                scope = scope,
                recordedLevel = recordedLevel,
                instructionMode = ask.instructionMode,
                context = ask.context,
                learnerQuestion = ask.learnerQuestion,
                form = ask.form,
            )
        )
    }

    /**
     * Written help shown without asking the tutor (14C: "önce yazılmış", user decision): the same fit and the same
     * record as written help shown after a non-answer — its own intent, at or below the learner's ceiling, recorded at
     * its own known level. `null` when it does not fit, so the caller asks the tutor instead.
     */
    fun authored(request: TutorRequest, help: AuthoredHelp): TutorOutcome.Shown? =
        fallbackOr(request, help, PendingReason.UNAVAILABLE) as? TutorOutcome.Shown

    /**
     * Checks a reply against the request and decides what the learner sees.
     *
     * A reply is shown only if it answers the same intent, has text, stays in the requested language mode
     * and does not admit to going past the learner's ceiling. Anything else is `invalid_response` — an error,
     * never a verdict — and like every non-answer it falls back to [fallback] if one fits, or shows nothing
     * and records nothing.
     */
    fun accept(request: TutorRequest, reply: TutorReply, fallback: AuthoredHelp? = null): TutorOutcome = when (reply) {
        is TutorReply.Delivered ->
            if (conforms(request, reply.content)) {
                shown(request, reply.content.text, AssistanceSource.AI_GENERATED, request.recordedLevel, reply.tutorRef)
            } else {
                fallbackOr(request, fallback, PendingReason.INVALID_RESPONSE)
            }
        is TutorReply.NotDelivered -> fallbackOr(request, fallback, reply.reason)
    }

    private fun conforms(request: TutorRequest, content: TutorContent): Boolean {
        if (content.intent != request.intent) return false
        if (content.text.isBlank()) return false
        if (content.instructionMode != request.instructionMode) return false
        val ceiling = request.ceiling ?: return true
        val revealed = content.revealedLevel ?: return false
        return revealed.ordinal <= ceiling.ordinal
    }

    private fun fallbackOr(request: TutorRequest, fallback: AuthoredHelp?, reason: PendingReason): TutorOutcome {
        val help = fallback?.takeIf {
            it.intent == request.intent && (request.ceiling == null || it.level.ordinal <= request.ceiling.ordinal)
        } ?: return TutorOutcome.NotShown(reason)
        // A person wrote this help for a known level, so that level is recorded rather than the ceiling.
        val level = when {
            request.recordedLevel == null -> null
            request.scope == AssistanceScope.NON_TARGET_SUPPORT -> request.recordedLevel
            else -> help.level
        }
        return shown(request, help.text, AssistanceSource.DETERMINISTIC_CONTENT, level, null)
    }

    private fun shown(request: TutorRequest, text: String, source: AssistanceSource, level: AssistanceLevel?, ref: TutorRef?): TutorOutcome.Shown {
        val event = level?.let {
            AssistanceEvent(level = it, timing = request.timing!!, scope = request.scope, source = source, requestedByUser = true)
        }
        val reveals = event != null && event.scope == AssistanceScope.TARGET_OBJECTIVE && event.level.revealsTargetReasoning
        return TutorOutcome.Shown(
            text = text,
            source = source,
            event = event,
            tutorRef = ref,
            revealsTargetReasoning = reveals,
            convertsItemToLearning = reveals && request.attemptOpen && request.purpose in measuringPurposes,
            // In a diagnostic any help with the target ends that Objective's fast path (D-104, user decision).
            endsDiagnosticFastPath = event != null && request.purpose == TaskPurpose.DIAGNOSE &&
                event.scope == AssistanceScope.TARGET_OBJECTIVE && request.attemptOpen,
        )
    }
}
