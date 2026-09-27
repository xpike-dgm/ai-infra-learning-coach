package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** `PRG-v0`'s vocabulary as types: what the gate can say, and what it cannot be made to say. */
class PrerequisiteFactsTest {

    private val target = VersionedRef("skill.c.linked_list_insert", 1)

    private fun decision(eligibility: PrerequisiteEligibility) = PrerequisiteDecision(
        candidateId = "candidate.1",
        target = target,
        eligibility = eligibility,
        hardBlockerSkills = emptyList(),
        uncertainSkills = emptyList(),
        softGapSkills = emptyList(),
        reviewDueSkills = emptyList(),
        metadataProblems = emptyList(),
        readinessSnapshotRefs = emptyList(),
        requiresStrictPrerequisiteConfidence = false,
        prerequisitePolicyVersion = "PRG-v0",
        reasonInputs = emptyList(),
        notes = emptyList(),
    )

    @Test
    fun `readiness has exactly the four values PRG-v0 names`() {
        assertEquals(
            listOf("ready", "ready_due", "uncertain", "not_ready"),
            PrerequisiteReadiness.entries.map { it.id },
        )
    }

    @Test
    fun `eligibility has exactly the five values PRG-v0 names`() {
        assertEquals(
            listOf("eligible", "conditional_eligible", "eligible_with_support", "blocked", "invalid_prerequisite_metadata"),
            PrerequisiteEligibility.entries.map { it.id },
        )
    }

    @Test
    fun `only a blocked or invalid candidate waits`() {
        assertEquals(
            setOf(PrerequisiteEligibility.BLOCKED, PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA),
            PrerequisiteEligibility.entries.filter { it.waits }.toSet(),
        )
    }

    @Test
    fun `work on a candidate that should have waited is recorded as contaminated evidence`() {
        PrerequisiteEligibility.entries.forEach { eligibility ->
            val snapshot = decision(eligibility).evidenceSnapshot
            if (eligibility.waits) {
                assertEquals(PrerequisiteSnapshot.CONTAMINATED, snapshot, eligibility.id)
            } else {
                assertEquals(eligibility.id, snapshot)
                assertFalse(snapshot == PrerequisiteSnapshot.CONTAMINATED)
            }
        }
    }

    @Test
    fun `a retention value nobody has written reads as not evaluated, never as a guess`() {
        assertEquals(RetentionAxis.NOT_YET_EVALUATED, RetentionAxis.of(null))
        assertEquals(RetentionAxis.NOT_YET_EVALUATED, RetentionAxis.of("forgotten"))
        assertEquals(RetentionAxis.REVIEW_DUE, RetentionAxis.of("review_due"))
    }

    @Test
    fun `there are two edge kinds and no third`() {
        assertEquals(listOf("hard", "soft"), EdgeKind.entries.map { it.id })
        assertEquals(null, EdgeKind.of("medium"))
    }

    @Test
    fun `a decision has no priority, score or failure field`() {
        val fields = PrerequisiteDecision::class.java.declaredFields.map { it.name }
        // `requiresStrictPrerequisiteConfidence` is PRG-v0's own flag, not a confidence value, so the word itself is not banned.
        listOf("priority", "score", "percent", "fail", "rank", "probability").forEach { word ->
            assertTrue(fields.none { it.contains(word, ignoreCase = true) }, "a decision carries '$word': $fields")
        }
    }
}
