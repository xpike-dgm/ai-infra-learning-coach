package coach.ai

import coach.model.AssistanceLevel
import coach.model.AssistanceTiming
import coach.model.InstructionMode
import coach.model.PendingReason
import coach.model.TaskPurpose
import coach.model.TutorAsk
import coach.model.TutorContext
import coach.model.TutorIntent
import coach.model.TutorPreparation
import coach.model.TutorReply
import coach.model.TutorRules
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** An adapter with no call site yet is unavailable — never a crash and never a reply (`TUTX-v0`, 14G). */
class AiTutorTest {

    @Test
    fun `an adapter without a call site is unavailable, never a crash and never a reply`() {
        val request = assertIs<TutorPreparation.Ready>(
            TutorRules.prepare(
                TutorAsk(
                    TutorIntent.HINT, TaskPurpose.PRACTICE, AssistanceTiming.DURING_ATTEMPT, AssistanceLevel.H1, false,
                    InstructionMode.TURKISH_PRIMARY, TutorContext(listOf(VersionedRef("objective.os.paging.cost", 1)), "What does a page fault cost?"),
                )
            )
        ).request
        assertEquals(TutorReply.NotDelivered(PendingReason.UNAVAILABLE), AiTutor().assist(request))
    }
}
