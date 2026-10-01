package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `TUTX-v0` as plain rules (14A): what may be asked, what it may reveal, what is shown and what is recorded.
 * Every test name states a rule; a mutant that breaks the rule has to fail one of them.
 */
class TutorFactsTest {

    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val context = TutorContext(targetObjectives = listOf(objective), taskText = "Write *p = 5 so that x becomes 5.")
    private val ref = TutorRef("provider", "model", TutorInstructions.VERSION)

    private fun ask(
        intent: TutorIntent = TutorIntent.HINT,
        purpose: TaskPurpose = TaskPurpose.PRACTICE,
        timing: AssistanceTiming? = AssistanceTiming.DURING_ATTEMPT,
        ceiling: AssistanceLevel? = AssistanceLevel.H2,
        acknowledged: Boolean = false,
        mode: InstructionMode = InstructionMode.TURKISH_PRIMARY,
        ctx: TutorContext = context,
        question: String? = null,
    ) = TutorAsk(intent, purpose, timing, ceiling, acknowledged, mode, ctx, question)

    private fun ready(ask: TutorAsk): TutorRequest = assertIs<TutorPreparation.Ready>(TutorRules.prepare(ask)).request

    private fun reply(
        request: TutorRequest,
        revealed: AssistanceLevel? = request.ceiling,
        intent: TutorIntent = request.intent,
        text: String = "Adres ile değer arasındaki farkı düşün.",
        mode: InstructionMode = request.instructionMode,
    ) = TutorReply.Delivered(TutorContent(intent, text, revealed, mode), ref)

    @Test
    fun `help is always requestable, so every ask is either ready or says what to do next`() {
        val timings = listOf(null) + AssistanceTiming.entries
        val ceilings = listOf(null) + AssistanceLevel.entries
        for (intent in TutorIntent.entries) for (timing in timings) for (ceiling in ceilings) for (purpose in TaskPurpose.entries) {
            for (segmentIsTarget in listOf(false, true)) {
                val ctx = context.copy(
                    learnerWork = "*p = 5",
                    segment = if (intent == TutorIntent.GLOSS) GlossSegment("dereference", segmentIsTarget) else null,
                )
                val question = if (intent == TutorIntent.QUESTION) "Neden yıldız kullanıyoruz?" else null
                val first = TutorRules.prepare(ask(intent, purpose, timing, ceiling, acknowledged = true, ctx = ctx, question = question))
                if (first is TutorPreparation.Redirect) {
                    assertTrue(first.to != intent, "a redirect points somewhere else: $intent")
                    val followed = TutorRules.prepare(
                        ask(first.to, purpose, timing, ceiling ?: AssistanceLevel.H1, acknowledged = true,
                            ctx = ctx.copy(segment = null), question = if (first.to == TutorIntent.QUESTION) question else null)
                    )
                    assertTrue(followed !is TutorPreparation.Redirect, "one redirect reaches help: $intent $timing -> ${first.to}")
                }
            }
        }
    }

    @Test
    fun `while an answer is open the learner chooses how much the reply may reveal`() {
        for (intent in listOf(TutorIntent.HINT, TutorIntent.EXPLAIN_DIFFERENTLY, TutorIntent.QUESTION)) {
            for (timing in listOf(AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT)) {
                val question = if (intent == TutorIntent.QUESTION) "Bu neden çalışmıyor?" else null
                assertEquals(TutorPreparation.LevelNeeded, TutorRules.prepare(ask(intent, timing = timing, ceiling = null, question = question)))
                val request = ready(ask(intent, timing = timing, ceiling = AssistanceLevel.H2, question = question))
                assertEquals(AssistanceLevel.H2, request.ceiling)
                assertEquals(AssistanceLevel.H2, request.recordedLevel)
                assertEquals(AssistanceScope.TARGET_OBJECTIVE, request.scope)
            }
        }
    }

    @Test
    fun `full or partial solutions are not sent before the learner is told what they change`() {
        for (level in listOf(AssistanceLevel.H3, AssistanceLevel.H4)) {
            assertEquals(
                TutorPreparation.DisclosureNeeded(level, AssistanceTiming.DURING_ATTEMPT),
                TutorRules.prepare(ask(ceiling = level, acknowledged = false)),
            )
            assertEquals(level, ready(ask(ceiling = level, acknowledged = true)).recordedLevel)
        }
        for (level in listOf(AssistanceLevel.H1, AssistanceLevel.H2)) {
            assertIs<TutorPreparation.Ready>(TutorRules.prepare(ask(ceiling = level, acknowledged = false)))
        }
    }

    @Test
    fun `after an answer is frozen no level is asked and the record assumes the solution may have been shown`() {
        for (timing in listOf(AssistanceTiming.AFTER_SUBMIT, AssistanceTiming.AFTER_FAILURE)) {
            val mistake = ask(TutorIntent.EXPLAIN_MISTAKE, timing = timing, ceiling = null, ctx = context.copy(learnerWork = "p = 5"))
            assertEquals(TutorPreparation.DisclosureNeeded(AssistanceLevel.H4, timing), TutorRules.prepare(mistake))
            val request = ready(mistake.copy(consequenceAcknowledged = true))
            assertNull(request.ceiling)
            assertEquals(AssistanceLevel.H4, request.recordedLevel)

            val question = ask(TutorIntent.QUESTION, timing = timing, ceiling = null, acknowledged = true, question = "Neden?")
            assertEquals(AssistanceLevel.H4, ready(question).recordedLevel)
        }
    }

    @Test
    fun `outside an attempt nothing is measured, so nothing is recorded`() {
        val request = ready(ask(TutorIntent.EXPLAIN_DIFFERENTLY, purpose = TaskPurpose.TEACH, timing = null, ceiling = null))
        assertNull(request.recordedLevel)
        assertNull(request.ceiling)
        val shown = assertIs<TutorOutcome.Shown>(TutorRules.accept(request, reply(request, revealed = null)))
        assertNull(shown.event)
        assertFalse(shown.revealsTargetReasoning)
    }

    @Test
    fun `a ceiling binds only help with the target while an answer is open`() {
        val gloss = ready(ask(TutorIntent.GLOSS, ceiling = AssistanceLevel.H2, ctx = context.copy(segment = GlossSegment("value", isTarget = false))))
        assertNull(gloss.ceiling)
        assertIs<TutorOutcome.Shown>(TutorRules.accept(gloss, reply(gloss, revealed = null)))
        val after = ready(ask(TutorIntent.QUESTION, timing = AssistanceTiming.AFTER_SUBMIT, ceiling = AssistanceLevel.H1, acknowledged = true, question = "Neden?"))
        assertNull(after.ceiling)
        assertIs<TutorOutcome.Shown>(TutorRules.accept(after, reply(after, revealed = AssistanceLevel.H4)))
        val lesson = ready(ask(TutorIntent.EXPLAIN_DIFFERENTLY, timing = null, ceiling = AssistanceLevel.H1))
        assertNull(lesson.ceiling)
    }

    @Test
    fun `a gloss of a non-target segment is support and never touches the target`() {
        val request = ready(ask(TutorIntent.GLOSS, ceiling = null, ctx = context.copy(segment = GlossSegment("write through", isTarget = false))))
        assertEquals(AssistanceScope.NON_TARGET_SUPPORT, request.scope)
        assertEquals(AssistanceLevel.H1, request.recordedLevel)
        val shown = assertIs<TutorOutcome.Shown>(TutorRules.accept(request, reply(request, revealed = null)))
        assertEquals(AssistanceScope.NON_TARGET_SUPPORT, shown.event!!.scope)
        assertFalse(shown.revealsTargetReasoning)
    }

    @Test
    fun `glossing the target segment would answer it, so it is a hint`() {
        val target = context.copy(segment = GlossSegment("dereference", isTarget = true))
        assertEquals(
            TutorPreparation.Redirect(TutorIntent.HINT, TutorRedirectReason.SEGMENT_IS_THE_TARGET),
            TutorRules.prepare(ask(TutorIntent.GLOSS, ceiling = null, ctx = target)),
        )
        assertEquals(
            TutorPreparation.Redirect(TutorIntent.EXPLAIN_DIFFERENTLY, TutorRedirectReason.SEGMENT_IS_THE_TARGET),
            TutorRules.prepare(ask(TutorIntent.GLOSS, timing = null, ceiling = null, ctx = target)),
        )
    }

    @Test
    fun `an ask that does not fit its moment is pointed at the one that does`() {
        assertEquals(
            TutorPreparation.Redirect(TutorIntent.HINT, TutorRedirectReason.NO_ANSWER_IS_FROZEN_YET),
            TutorRules.prepare(ask(TutorIntent.EXPLAIN_MISTAKE, ctx = context.copy(learnerWork = "x"))),
        )
        assertEquals(
            TutorPreparation.Redirect(TutorIntent.EXPLAIN_MISTAKE, TutorRedirectReason.ANSWER_ALREADY_FROZEN),
            TutorRules.prepare(ask(TutorIntent.HINT, timing = AssistanceTiming.AFTER_SUBMIT)),
        )
        assertEquals(
            TutorPreparation.Redirect(TutorIntent.EXPLAIN_DIFFERENTLY, TutorRedirectReason.NO_ITEM_IS_BEING_WORKED),
            TutorRules.prepare(ask(TutorIntent.HINT, timing = null)),
        )
    }

    @Test
    fun `a question needs its text, an explanation its frozen answer and a gloss its segment`() {
        assertEquals(TutorPreparation.Incomplete(TutorMissing.QUESTION_TEXT), TutorRules.prepare(ask(TutorIntent.QUESTION, question = " ")))
        assertEquals(
            TutorPreparation.Incomplete(TutorMissing.FROZEN_ANSWER),
            TutorRules.prepare(ask(TutorIntent.EXPLAIN_MISTAKE, timing = AssistanceTiming.AFTER_SUBMIT)),
        )
        assertEquals(TutorPreparation.Incomplete(TutorMissing.SEGMENT), TutorRules.prepare(ask(TutorIntent.GLOSS)))
        assertEquals(
            TutorPreparation.Incomplete(TutorMissing.SEGMENT),
            TutorRules.prepare(ask(TutorIntent.GLOSS, ctx = context.copy(segment = GlossSegment(" ", isTarget = false)))),
        )
    }

    @Test
    fun `the answer cannot leave the device while it could still give something away`() {
        val withKey = context.copy(referenceSolution = "*p = 5;")
        assertFailsWith<IllegalArgumentException> { TutorRules.prepare(ask(ceiling = AssistanceLevel.H2, ctx = withKey)) }
        assertFailsWith<IllegalArgumentException> { TutorRules.prepare(ask(ceiling = AssistanceLevel.H3, acknowledged = true, ctx = withKey)) }
        assertIs<TutorPreparation.Ready>(TutorRules.prepare(ask(ceiling = AssistanceLevel.H4, acknowledged = true, ctx = withKey)))
        assertIs<TutorPreparation.Ready>(
            TutorRules.prepare(
                ask(TutorIntent.EXPLAIN_MISTAKE, timing = AssistanceTiming.AFTER_SUBMIT, ceiling = null, acknowledged = true,
                    ctx = withKey.copy(learnerWork = "p = 5;"))
            )
        )
    }

    @Test
    fun `a reply that admits going past the ceiling is not shown and nothing is recorded`() {
        val request = ready(ask(ceiling = AssistanceLevel.H2))
        assertEquals(TutorOutcome.NotShown(PendingReason.INVALID_RESPONSE), TutorRules.accept(request, reply(request, revealed = AssistanceLevel.H3)))
        assertEquals(TutorOutcome.NotShown(PendingReason.INVALID_RESPONSE), TutorRules.accept(request, reply(request, revealed = AssistanceLevel.H4)))
        assertIs<TutorOutcome.Shown>(TutorRules.accept(request, reply(request, revealed = AssistanceLevel.H2)))
    }

    @Test
    fun `a reply that does not say how far it goes while a ceiling applies is not shown`() {
        val request = ready(ask(ceiling = AssistanceLevel.H2))
        assertEquals(TutorOutcome.NotShown(PendingReason.INVALID_RESPONSE), TutorRules.accept(request, reply(request, revealed = null)))
    }

    @Test
    fun `a reply for another intent, without text or in another language mode is an error, not help`() {
        val request = ready(ask(ceiling = AssistanceLevel.H2))
        val invalid = TutorOutcome.NotShown(PendingReason.INVALID_RESPONSE)
        assertEquals(invalid, TutorRules.accept(request, reply(request, intent = TutorIntent.EXPLAIN_DIFFERENTLY)))
        assertEquals(invalid, TutorRules.accept(request, reply(request, text = "  ")))
        assertEquals(invalid, TutorRules.accept(request, reply(request, mode = InstructionMode.ENGLISH_UNSCAFFOLDED)))
    }

    @Test
    fun `the record never claims less help than the learner allowed`() {
        val request = ready(ask(ceiling = AssistanceLevel.H2))
        val shown = assertIs<TutorOutcome.Shown>(TutorRules.accept(request, reply(request, revealed = AssistanceLevel.H1)))
        assertEquals(
            AssistanceEvent(AssistanceLevel.H2, AssistanceTiming.DURING_ATTEMPT, AssistanceScope.TARGET_OBJECTIVE, AssistanceSource.AI_GENERATED, requestedByUser = true),
            shown.event,
        )
        assertEquals(ref, shown.tutorRef)
    }

    @Test
    fun `a refusal, a timeout or no tutor at all records nothing and blames no one`() {
        val request = ready(ask(ceiling = AssistanceLevel.H1))
        for (reason in PendingReason.entries) {
            assertEquals(TutorOutcome.NotShown(reason), TutorRules.accept(request, TutorReply.NotDelivered(reason)))
        }
    }

    @Test
    fun `authored help is shown when the tutor gives nothing, at its own known level`() {
        val request = ready(ask(ceiling = AssistanceLevel.H2))
        val authored = AuthoredHelp(TutorIntent.HINT, AssistanceLevel.H1, "Hangi değişkenin değişmesi gerekiyor?", VersionedRef("hint.c.pointers.1", 1))
        for (reason in PendingReason.entries) {
            val shown = assertIs<TutorOutcome.Shown>(TutorRules.accept(request, TutorReply.NotDelivered(reason), authored))
            assertEquals(AssistanceSource.DETERMINISTIC_CONTENT, shown.source)
            assertEquals(AssistanceSource.DETERMINISTIC_CONTENT, shown.event!!.source)
            assertEquals(AssistanceLevel.H1, shown.event!!.level)
            assertEquals(authored.text, shown.text)
            assertNull(shown.tutorRef)
        }
        val invalid = TutorRules.accept(request, reply(request, revealed = AssistanceLevel.H4), authored)
        assertEquals(AssistanceSource.DETERMINISTIC_CONTENT, assertIs<TutorOutcome.Shown>(invalid).source)
    }

    @Test
    fun `authored help above the learner's ceiling or for another intent is not shown`() {
        val request = ready(ask(ceiling = AssistanceLevel.H2))
        val tooMuch = AuthoredHelp(TutorIntent.HINT, AssistanceLevel.H3, "int *p = &x;", VersionedRef("hint.c.pointers.3", 1))
        val otherIntent = AuthoredHelp(TutorIntent.EXPLAIN_DIFFERENTLY, AssistanceLevel.H1, "Bir adres bir konumdur.", VersionedRef("explain.c.pointers", 1))
        assertEquals(TutorOutcome.NotShown(PendingReason.UNAVAILABLE), TutorRules.accept(request, TutorReply.NotDelivered(PendingReason.UNAVAILABLE), tooMuch))
        assertEquals(TutorOutcome.NotShown(PendingReason.UNAVAILABLE), TutorRules.accept(request, TutorReply.NotDelivered(PendingReason.UNAVAILABLE), otherIntent))
    }

    @Test
    fun `a solution shown on a measuring item turns it into learning and says so`() {
        for (purpose in TaskPurpose.entries) {
            val request = ready(ask(purpose = purpose, ceiling = AssistanceLevel.H4, acknowledged = true))
            val shown = assertIs<TutorOutcome.Shown>(TutorRules.accept(request, reply(request)))
            assertTrue(shown.revealsTargetReasoning)
            assertEquals(purpose in setOf(TaskPurpose.ASSESS, TaskPurpose.RETAIN, TaskPurpose.DIAGNOSE), shown.convertsItemToLearning, "$purpose")
        }
        val hint = ready(ask(purpose = TaskPurpose.ASSESS, ceiling = AssistanceLevel.H2))
        assertFalse(assertIs<TutorOutcome.Shown>(TutorRules.accept(hint, reply(hint))).convertsItemToLearning)
        val after = ready(ask(TutorIntent.EXPLAIN_MISTAKE, purpose = TaskPurpose.ASSESS, timing = AssistanceTiming.AFTER_SUBMIT, ceiling = null,
            acknowledged = true, ctx = context.copy(learnerWork = "p = 5")))
        val explained = assertIs<TutorOutcome.Shown>(TutorRules.accept(after, reply(after, revealed = AssistanceLevel.H4)))
        assertTrue(explained.revealsTargetReasoning)
        assertFalse(explained.convertsItemToLearning, "a frozen answer is not converted after the fact")
    }

    @Test
    fun `help with the target in a diagnostic ends that Objective's fast path, and nothing else does`() {
        val hint = ready(ask(purpose = TaskPurpose.DIAGNOSE, ceiling = AssistanceLevel.H1))
        assertTrue(assertIs<TutorOutcome.Shown>(TutorRules.accept(hint, reply(hint))).endsDiagnosticFastPath)
        val gloss = ready(ask(TutorIntent.GLOSS, purpose = TaskPurpose.DIAGNOSE, ceiling = null, ctx = context.copy(segment = GlossSegment("value", false))))
        assertFalse(assertIs<TutorOutcome.Shown>(TutorRules.accept(gloss, reply(gloss, revealed = null))).endsDiagnosticFastPath)
        val after = ready(ask(TutorIntent.QUESTION, purpose = TaskPurpose.DIAGNOSE, timing = AssistanceTiming.AFTER_SUBMIT, ceiling = null,
            acknowledged = true, question = "Neden?"))
        assertFalse(assertIs<TutorOutcome.Shown>(TutorRules.accept(after, reply(after, revealed = AssistanceLevel.H4))).endsDiagnosticFastPath)
        val practice = ready(ask(purpose = TaskPurpose.PRACTICE, ceiling = AssistanceLevel.H1))
        assertFalse(assertIs<TutorOutcome.Shown>(TutorRules.accept(practice, reply(practice))).endsDiagnosticFastPath)
    }
}
