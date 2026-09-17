package coach.ai

import coach.model.EvaluationResult
import coach.model.EvaluatorAvailability
import coach.model.PendingReason
import coach.model.VersionedRef
import coach.ports.EvaluationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

/** An adapter with no call sites yet is unavailable, never a crash and never a verdict (AIAX-v0). */
class AiEvaluatorTest {

    @Test
    fun `an adapter without call sites degrades to evaluation pending instead of throwing`() {
        val result = AiEvaluator().evaluate(
            EvaluationRequest(
                objectiveRefs = listOf(VersionedRef("obj.os.paging.cost", 1)),
                promptText = "Explain what a page fault costs.",
                learnerResponse = "It traps into the kernel.",
            )
        )
        assertEquals(EvaluationResult.EvaluationPending(PendingReason.UNAVAILABLE), result)
    }

    @Test
    fun `the adapter reports itself unavailable`() {
        assertEquals(EvaluatorAvailability.UNAVAILABLE, AiEvaluator().availability)
    }
}
