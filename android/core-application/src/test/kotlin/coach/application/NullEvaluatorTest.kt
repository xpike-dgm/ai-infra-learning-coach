package coach.application

import coach.model.EvaluationResult
import coach.model.PendingReason
import coach.model.VersionedRef
import coach.ports.EvaluationRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * TVSX-v0 T5-NULLEVAL-02 in its off-device form: with no evaluator available an open-ended
 * attempt becomes evaluation_pending and no evidence is produced. Neither pass nor fail.
 */
class NullEvaluatorTest {

    private val request = EvaluationRequest(
        objectiveRefs = listOf(VersionedRef("obj.example", 1)),
        promptText = "explain what a page fault costs",
        learnerResponse = "it is a trap into the kernel",
    )

    @Test
    fun `absent evaluator yields evaluation pending`() {
        val result = NullEvaluator.evaluate(request)
        val pending = assertIs<EvaluationResult.EvaluationPending>(result)
        assertEquals(PendingReason.UNAVAILABLE, pending.reason)
    }

    @Test
    fun `absent evaluator never produces a verdict`() {
        val result = NullEvaluator.evaluate(request)
        // A missing evaluator must not be readable as a judgement in either direction.
        assertIs<EvaluationResult.EvaluationPending>(result)
    }
}
