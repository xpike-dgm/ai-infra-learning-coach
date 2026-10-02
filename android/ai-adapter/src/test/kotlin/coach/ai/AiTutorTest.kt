package coach.ai

import coach.ai.OpenAiResponsesTest.Companion.completed
import coach.ai.OpenAiResponsesTest.Companion.refusal
import coach.ai.OpenAiResponsesTest.Scripted
import coach.model.AssistanceLevel
import coach.model.AssistanceTiming
import coach.model.InstructionMode
import coach.model.PendingReason
import coach.model.TaskPurpose
import coach.model.TutorAsk
import coach.model.TutorContent
import coach.model.TutorContext
import coach.model.TutorInstructions
import coach.model.TutorIntent
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRef
import coach.model.TutorReply
import coach.model.TutorRules
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** The tutor's call site (14G): core's request in, core's schema out, and core still decides what is shown (`TUTX-v0`). */
class AiTutorTest {

    private val request = assertIs<TutorPreparation.Ready>(
        TutorRules.prepare(
            TutorAsk(
                TutorIntent.HINT, TaskPurpose.PRACTICE, AssistanceTiming.DURING_ATTEMPT, AssistanceLevel.H1, false,
                InstructionMode.TURKISH_PRIMARY, TutorContext(listOf(VersionedRef("objective.os.paging.cost", 1)), "What does a page fault cost?"),
            )
        )
    ).request

    private fun tutor(transport: HttpTransport) = AiTutor(OpenAiResponses(ProviderConfig(), { OpenAiResponsesTest.KEY }, transport) { 0L })

    @Test
    fun `without a client the tutor is unavailable, never a crash and never a reply`() {
        assertEquals(TutorReply.NotDelivered(PendingReason.UNAVAILABLE), AiTutor().assist(request))
    }

    @Test
    fun `it sends core's instructions, message and schema, and reads back exactly the schema's four fields`() {
        val transport = Scripted({ completed("{\"intent\":\"hint\",\"text\":\"Çekirdeğe geçişi düşün.\",\"revealed_level\":\"H1\",\"instruction_mode\":\"turkish_primary\"}") })
        val reply = assertIs<TutorReply.Delivered>(tutor(transport).assist(request))
        assertEquals(TutorContent(TutorIntent.HINT, "Çekirdeğe geçişi düşün.", AssistanceLevel.H1, InstructionMode.TURKISH_PRIMARY), reply.content)
        assertEquals(TutorRef("openai", "gpt-6-astra", TutorInstructions.VERSION), reply.tutorRef)
        val sent = Json.parse(transport.calls.single().third) as JsonValue.Obj
        assertEquals(Json.str(TutorInstructions.TEXT), sent["instructions"])
        assertEquals(Json.str(TutorInstructions.userMessage(request)), sent["input"])
        val format = (sent["text"] as JsonValue.Obj)["format"] as JsonValue.Obj
        assertEquals(Json.parse(TutorInstructions.REPLY_SCHEMA), format["schema"])
        assertEquals(Json.str("tutor_reply_2"), format["name"])
        // Core, not the adapter, decides whether it may be shown.
        assertIs<TutorOutcome.Shown>(TutorRules.accept(request, reply, null))
    }

    @Test
    fun `a reply above the ceiling arrives but core refuses to show it`() {
        val reply = tutor(Scripted({ completed("{\"intent\":\"hint\",\"text\":\"The full answer.\",\"revealed_level\":\"H4\",\"instruction_mode\":\"turkish_primary\"}") })).assist(request)
        assertIs<TutorReply.Delivered>(reply)
        assertIs<TutorOutcome.NotShown>(TutorRules.accept(request, reply, null))
    }

    @Test
    fun `an extra field, an unknown value or a refusal is no reply`() {
        for (bad in listOf(
            "{\"intent\":\"hint\",\"text\":\"x\",\"revealed_level\":\"H1\",\"instruction_mode\":\"turkish_primary\",\"verdict\":\"mastered\"}",
            "{\"intent\":\"grade\",\"text\":\"x\",\"revealed_level\":\"H1\",\"instruction_mode\":\"turkish_primary\"}",
            "{\"intent\":\"hint\",\"text\":\"x\",\"revealed_level\":\"H9\",\"instruction_mode\":\"turkish_primary\"}",
            "{\"intent\":\"hint\",\"text\":\" \",\"revealed_level\":null,\"instruction_mode\":\"turkish_primary\"}",
        )) {
            assertEquals(TutorReply.NotDelivered(PendingReason.INVALID_RESPONSE), tutor(Scripted({ completed(bad) })).assist(request), bad)
        }
        assertEquals(TutorReply.NotDelivered(PendingReason.REFUSED), tutor(Scripted({ refusal() })).assist(request))
    }
}
