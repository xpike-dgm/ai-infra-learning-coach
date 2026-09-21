package coach.presentation

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentIntent
import coach.model.AssessmentScope
import coach.model.AssistanceLevel
import coach.model.IndependenceMode
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The one assessment session interior (`ASUX-v0`), first used by the daily micro assessment (11D). */
class AssessmentSessionTest {

    private fun boundary(id: String) = Boundary(id, BoundaryKind.ITEM, listOf(VersionedRef("item.$id", 1)))

    private fun session(scope: AssessmentScope = AssessmentScope.DAILY_MICRO) = AssessmentSessionView(
        scope = scope,
        intents = listOf(AssessmentIntent.CHECKPOINT),
        blocks = listOf(Block("block.1", listOf(boundary("b1"), boundary("b2"))), Block("block.2", listOf(boundary("b3")))),
        independenceMode = IndependenceMode.H0_REQUIRED,
        allowedTools = AllowedToolsPolicy(listOf("documentation")),
        state = SessionState.ITEM_ACTIVE,
    )

    // ---------------------------------------------------------------- vocabularies and tones

    @Test
    fun `the nineteen states are ASUX-v0's in its order`() {
        assertEquals(
            listOf("entering_revalidating", "blocked_not_startable", "session_orientation", "item_active",
                "assistance_open", "submitting_boundary", "boundary_frozen", "block_checkpoint", "session_paused",
                "resume_revalidating", "slot_recomposed", "evaluation_pending", "result_ready", "result_partial",
                "item_contested", "offline_local_capable", "ai_unavailable_deterministic_core", "error_recoverable",
                "data_recovery_required"),
            SessionState.entries.map { it.id },
        )
    }

    @Test
    fun `only the two system conditions wear the fault tone`() {
        assertEquals(
            setOf(SessionState.ERROR_RECOVERABLE, SessionState.DATA_RECOVERY_REQUIRED),
            SessionState.entries.filter { it.tone == Tone.SYSTEM_FAULT }.toSet(),
        )
        assertEquals(Tone.ATTENTION, SessionState.BLOCKED_NOT_STARTABLE.tone)
        assertEquals(Tone.PENDING_UNRESOLVED, SessionState.EVALUATION_PENDING.tone)
        assertEquals(Tone.PENDING_UNRESOLVED, SessionState.ITEM_CONTESTED.tone)
        assertEquals(Tone.PENDING_UNRESOLVED, SessionState.RESULT_PARTIAL.tone)
        assertEquals(Tone.NEUTRAL, SessionState.RESULT_READY.tone)
    }

    @Test
    fun `the scope is context, and every scope runs the same interior`() {
        AssessmentScope.entries.forEach { scope ->
            val view = session(scope)
            assertEquals(listOf("b1", "b2", "b3"), SessionNavigation.navigable(view))
            assertEquals(IndependenceMode.H0_REQUIRED, view.independenceMode)
        }
    }

    // ---------------------------------------------------------------- boundaries

    @Test
    fun `a boundary is the submission unit and a testlet is never split`() {
        val testlet = Boundary("t1", BoundaryKind.TESTLET, listOf(VersionedRef("item.a", 1), VersionedRef("item.b", 1)))
        assertEquals(2, testlet.items.size)
        assertFailsWith<IllegalArgumentException> {
            Boundary("bad", BoundaryKind.ITEM, listOf(VersionedRef("item.a", 1), VersionedRef("item.b", 1)))
        }
        assertFailsWith<IllegalArgumentException> { Boundary("empty", BoundaryKind.ITEM, emptyList()) }
    }

    @Test
    fun `submission freezes, and a frozen boundary can never be revisited or resubmitted`() {
        val submitted = assertIs<BoundaryOutcome.Applied>(SessionNavigation.submit(session(), "b1")).session
        assertEquals(BoundaryStatus.FROZEN, submitted.statusOf("b1"))
        assertFalse("b1" in SessionNavigation.navigable(submitted))
        listOf(SessionNavigation.submit(submitted, "b1"), SessionNavigation.skip(submitted, "b1")).forEach {
            assertEquals(BoundaryRefusal.ALREADY_FROZEN, assertIs<BoundaryOutcome.Refused>(it).reason)
        }
    }

    @Test
    fun `unsubmitted boundaries stay navigable and answerable in any order`() {
        val afterB2 = assertIs<BoundaryOutcome.Applied>(SessionNavigation.submit(session(), "b2")).session
        assertEquals(listOf("b1", "b3"), SessionNavigation.navigable(afterB2))
    }

    @Test
    fun `skipping is not incorrect and leaves the need unresolved`() {
        val skipped = assertIs<BoundaryOutcome.Applied>(SessionNavigation.skip(session(), "b1")).session
        assertEquals(BoundaryStatus.UNSUBMITTED, skipped.statusOf("b1"))
        val result = SessionResults.of(skipped)
        assertTrue(NotReliablyMeasured.UNSUBMITTED_SLOT in result.notReliablyMeasured)
        assertTrue(result.partial)
        assertTrue(result.families.values.all { it.isEmpty() } || result.families.isEmpty())
    }

    @Test
    fun `an unknown boundary is refused rather than invented`() {
        assertEquals(
            BoundaryRefusal.UNKNOWN_BOUNDARY,
            assertIs<BoundaryOutcome.Refused>(SessionNavigation.submit(session(), "nope")).reason,
        )
    }

    @Test
    fun `position context orients without scoring or timing`() {
        assertEquals("block 1/2, boundary 2/2", session().positionContext("b2"))
        assertNull(session().positionContext("unknown"))
    }

    @Test
    fun `the session view can hold no score, grade, percentage or countdown`() {
        val forbidden = listOf("score", "grade", "percent", "threshold", "countdown", "timer", "streak", "rank")
        val fields = AssessmentSessionView::class.java.declaredFields.map { it.name } +
            SessionResult::class.java.declaredFields.map { it.name } +
            Boundary::class.java.declaredFields.map { it.name }
        forbidden.forEach { word ->
            assertTrue(fields.none { it.contains(word, ignoreCase = true) }, "a session field names '$word': $fields")
        }
    }

    // ---------------------------------------------------------------- assistance

    @Test
    fun `help is never blocked and its consequence is measurement, not penalty`() {
        assertFalse(SessionAssistance.BLOCKED)
        assertEquals(AssistanceConsequence.ASSISTED, SessionAssistance.consequenceOf(AssistanceLevel.H1))
        assertEquals(AssistanceConsequence.ASSISTED, SessionAssistance.consequenceOf(AssistanceLevel.H2))
        listOf(AssistanceLevel.H3, AssistanceLevel.H4).forEach {
            val consequence = SessionAssistance.consequenceOf(it)
            assertEquals(AssistanceConsequence.PRACTICE_ONLY_SOLUTION_EXPOSED, consequence)
            assertTrue(consequence.requiresFreshUnseenItem)
        }
        AssistanceConsequence.entries.forEach { assertFalse(it.producesIndependentMasteryEvidence) }
    }

    @Test
    fun `revealing help converts the mode explicitly and raises a recheck it never schedules`() {
        val conversion = assertNotNull(
            SessionAssistance.convertOnRevealingHelp(AssistanceLevel.H4, IndependenceMode.H0_REQUIRED)
        )
        assertEquals(IndependenceMode.H0_REQUIRED, conversion.from)
        assertTrue(conversion.raisesIndependentRecheck)
        assertFalse(conversion.schedulesRecheck)
        // A hint on an item that was never independent converts nothing.
        assertNull(SessionAssistance.convertOnRevealingHelp(AssistanceLevel.H1, IndependenceMode.H0_REQUIRED))
        assertNull(SessionAssistance.convertOnRevealingHelp(AssistanceLevel.H4, IndependenceMode.GUIDED_ALLOWED))
    }

    // ---------------------------------------------------------------- recomposition and dispute

    @Test
    fun `the five recomposition conditions are ASUX-v0's in its order`() {
        assertEquals(
            listOf("solution_or_explanation_exposed", "item_version_or_validation_changed",
                "prerequisite_state_changed_meaningfully", "freshness_no_longer_trustworthy_after_long_gap",
                "user_requested_reset_or_alternative"),
            RecompositionCondition.entries.map { it.id },
        )
    }

    @Test
    fun `recomposition replaces unresolved slots and never deletes completed evidence`() {
        val submitted = assertIs<BoundaryOutcome.Applied>(SessionNavigation.submit(session(), "b1")).session
        val recomposed = SessionRecomposition.recompose(
            submitted,
            mapOf(
                "b1" to setOf(RecompositionCondition.SOLUTION_OR_EXPLANATION_EXPOSED),
                "b2" to setOf(RecompositionCondition.ITEM_VERSION_OR_VALIDATION_CHANGED),
                "b3" to emptySet(),
            ),
        )
        assertEquals(BoundaryStatus.FROZEN, recomposed.statusOf("b1"), "a frozen boundary was recomposed")
        assertEquals(BoundaryStatus.RECOMPOSED, recomposed.statusOf("b2"))
        assertEquals(BoundaryStatus.OPEN, recomposed.statusOf("b3"))
        assertEquals(
            BoundaryRefusal.RECOMPOSED_SLOT,
            assertIs<BoundaryOutcome.Refused>(SessionNavigation.submit(recomposed, "b2")).reason,
        )
    }

    @Test
    fun `a dispute holds the evidence as contested without invalidating the item`() {
        val submitted = assertIs<BoundaryOutcome.Applied>(SessionNavigation.submit(session(), "b1")).session
        val contested = assertIs<BoundaryOutcome.Applied>(SessionNavigation.contest(submitted, "b1")).session
        assertEquals(BoundaryStatus.CONTESTED, contested.statusOf("b1"))
        val result = SessionResults.of(contested)
        assertTrue(NotReliablyMeasured.CONTESTED_ITEM in result.notReliablyMeasured)
    }

    // ---------------------------------------------------------------- result

    @Test
    fun `the six result families are ASUX-v0's in its order`() {
        assertEquals(
            listOf("confirmed_capabilities", "verification_needed", "persistent_targeted_gaps",
                "retention_revalidated", "not_reliably_measured", "plan_changes"),
            ResultFamily.entries.map { it.id },
        )
        assertEquals(
            listOf("invalid_item", "provisional_evaluation", "assisted_attempt", "solution_exposed_attempt",
                "contested_item", "unsubmitted_slot"),
            NotReliablyMeasured.entries.map { it.id },
        )
    }

    @Test
    fun `a state change is claimed only when canonical state actually changed`() {
        val complete = session().copy(
            statuses = mapOf("b1" to BoundaryStatus.FROZEN, "b2" to BoundaryStatus.FROZEN, "b3" to BoundaryStatus.FROZEN)
        )
        val nothingChanged = SessionResults.of(complete)
        assertTrue(nothingChanged.families.isEmpty(), "a change was claimed with no canonical change")
        assertFalse(nothingChanged.partial)
        assertEquals(SessionState.RESULT_READY, nothingChanged.state)

        val changed = SessionResults.of(
            complete,
            canonicalChanges = mapOf(ResultFamily.CONFIRMED_CAPABILITIES to listOf("skill.python.loops@v1")),
        )
        assertEquals(listOf("skill.python.loops@v1"), changed.families[ResultFamily.CONFIRMED_CAPABILITIES])
    }

    @Test
    fun `not reliably measured is first class and is never folded into incorrect`() {
        val skipped = assertIs<BoundaryOutcome.Applied>(SessionNavigation.skip(session(), "b1")).session
        val result = SessionResults.of(
            skipped,
            unreliable = setOf(NotReliablyMeasured.PROVISIONAL_EVALUATION, NotReliablyMeasured.INVALID_ITEM),
        )
        assertTrue(result.showsNotReliablyMeasured)
        assertEquals(
            listOf(NotReliablyMeasured.INVALID_ITEM, NotReliablyMeasured.PROVISIONAL_EVALUATION,
                NotReliablyMeasured.UNSUBMITTED_SLOT),
            result.notReliablyMeasured,
        )
    }

    @Test
    fun `an incomplete session is partial, not a failure`() {
        val result = SessionResults.of(session())
        assertTrue(result.partial)
        assertEquals(SessionState.RESULT_PARTIAL, result.state)
        assertEquals(Tone.PENDING_UNRESOLVED, result.state.tone)
    }

    @Test
    fun `raw counts stay informational and separate from state`() {
        val complete = session().copy(
            statuses = mapOf("b1" to BoundaryStatus.FROZEN, "b2" to BoundaryStatus.FROZEN, "b3" to BoundaryStatus.FROZEN)
        )
        val result = SessionResults.of(complete, counts = InformationalCounts(correct = 2, incorrect = 1, partial = 0))
        assertTrue(assertNotNull(result.counts).isInformationalOnly)
        assertTrue(result.families.isEmpty(), "counts became a state claim")
    }
}
