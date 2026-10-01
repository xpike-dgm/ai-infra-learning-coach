package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * What recorded help means for one attempt's evidence (14A, `2D` §5–§6). The class is derived from what was
 * recorded and from the learner's own provenance answer — never from how the answer looks.
 */
class AssistanceInterpretationTest {

    private fun help(
        level: AssistanceLevel,
        timing: AssistanceTiming = AssistanceTiming.DURING_ATTEMPT,
        scope: AssistanceScope = AssistanceScope.TARGET_OBJECTIVE,
        source: AssistanceSource = AssistanceSource.AI_GENERATED,
    ) = AssistanceEvent(level, timing, scope, source, requestedByUser = true)

    private fun independence(
        vararg events: AssistanceEvent,
        provenance: ProvenanceOrigin = ProvenanceOrigin.USER_AUTHORED,
        purpose: TaskPurpose = TaskPurpose.PRACTICE,
    ) = AssistanceInterpretation.independence(events.toList(), provenance, purpose)

    @Test
    fun `no help and the learner's own work is independent evidence`() {
        for (purpose in TaskPurpose.entries - TaskPurpose.TEACH) {
            assertEquals(IndependenceClass.INDEPENDENT, independence(purpose = purpose))
        }
    }

    @Test
    fun `a hint or a concept before the answer froze makes the attempt assisted`() {
        for (timing in listOf(AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT)) {
            for (level in listOf(AssistanceLevel.H1, AssistanceLevel.H2)) {
                for (source in AssistanceSource.entries) {
                    assertEquals(IndependenceClass.ASSISTED, independence(help(level, timing, source = source)))
                }
            }
        }
    }

    @Test
    fun `a partial or full solution before the answer froze needs a fresh independent check`() {
        for (timing in listOf(AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT)) {
            for (level in listOf(AssistanceLevel.H3, AssistanceLevel.H4)) {
                assertEquals(IndependenceClass.REQUIRES_INDEPENDENT_RECHECK, independence(help(AssistanceLevel.H1, timing), help(level, timing)))
                assertEquals(
                    IndependenceClass.REQUIRES_INDEPENDENT_RECHECK,
                    independence(help(level, timing), provenance = ProvenanceOrigin.GENERATED_OR_COPIED, purpose = TaskPurpose.TEACH),
                )
            }
        }
    }

    @Test
    fun `help after the answer froze does not reach back into it`() {
        for (timing in listOf(AssistanceTiming.AFTER_SUBMIT, AssistanceTiming.AFTER_FAILURE)) {
            for (level in AssistanceLevel.entries) {
                assertEquals(IndependenceClass.INDEPENDENT, independence(help(level, timing)))
            }
        }
    }

    @Test
    fun `support around the target never changes what the attempt proves`() {
        for (level in AssistanceLevel.entries) {
            for (timing in AssistanceTiming.entries) {
                assertEquals(IndependenceClass.INDEPENDENT, independence(help(level, timing, AssistanceScope.NON_TARGET_SUPPORT)))
            }
        }
    }

    @Test
    fun `work the learner says was generated or copied is practice, and an unknown origin accuses no one`() {
        assertEquals(IndependenceClass.PRACTICE_ONLY, independence(provenance = ProvenanceOrigin.GENERATED_OR_COPIED))
        assertEquals(IndependenceClass.PRACTICE_ONLY, independence(help(AssistanceLevel.H1), provenance = ProvenanceOrigin.GENERATED_OR_COPIED))
        assertEquals(IndependenceClass.INDEPENDENT, independence(provenance = ProvenanceOrigin.UNKNOWN_PROVENANCE))
        assertEquals(IndependenceClass.ASSISTED, independence(help(AssistanceLevel.H2), provenance = ProvenanceOrigin.UNKNOWN_PROVENANCE))
    }

    @Test
    fun `work the learner says they wrote with help, or with another author, is assisted`() {
        assertEquals(IndependenceClass.ASSISTED, independence(provenance = ProvenanceOrigin.USER_AUTHORED_WITH_ASSISTANCE))
        assertEquals(IndependenceClass.ASSISTED, independence(provenance = ProvenanceOrigin.MIXED_AUTHORSHIP))
    }

    @Test
    fun `a teaching task is practice by design`() {
        assertEquals(IndependenceClass.PRACTICE_ONLY, independence(purpose = TaskPurpose.TEACH))
        assertEquals(IndependenceClass.PRACTICE_ONLY, independence(help(AssistanceLevel.H1), purpose = TaskPurpose.TEACH))
    }

    @Test
    fun `a submission reads its own independence from what it recorded`() {
        val submission = AttemptSubmission(
            resource = VersionedRef("item.c.pointers.q1", 1),
            artifactContentRef = "data:,x",
            provenance = ProvenanceOrigin.USER_AUTHORED,
            assistance = listOf(help(AssistanceLevel.H4, AssistanceTiming.AFTER_SUBMIT), help(AssistanceLevel.H2)),
        )
        assertEquals(IndependenceClass.ASSISTED, submission.independence(TaskPurpose.ASSESS))
    }
}
