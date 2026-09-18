package coach.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `UXIA-v0` and `WFPX-v0` guarantees as checks. Each one fails if the shell drifts, which is the
 * point: an IA rule nothing fails on is a preference (`TVSX-v0`).
 */
class NavigationTest {

    @Test
    fun `there are exactly four destinations in the accepted order`() {
        assertEquals(
            listOf("today", "learn", "progress", "profile"),
            Destination.entries.map { it.id },
        )
        assertEquals(listOf(1, 2, 3, 4), Destination.entries.map { it.order })
    }

    @Test
    fun `today is the start destination`() {
        assertEquals(Destination.TODAY, Destination.start)
    }

    @Test
    fun `assessment english and ai are not destinations`() {
        val ids = Destination.entries.map { it.id }.toSet()
        listOf("assessment", "exams", "english", "ai_chat", "ai_tutor",
               "mastery", "retention", "remediation", "weakness", "streak", "leaderboard")
            .forEach { forbidden ->
                assertFalse(forbidden in ids, "$forbidden must not be a top-level destination")
            }
    }

    @Test
    fun `every destination has exactly one shell root`() {
        Destination.entries.forEach { destination ->
            val roots = Surface.shellRoots.filter { it.destination == destination }
            assertEquals(1, roots.size, "destination ${destination.id} must own one shell root")
        }
    }

    @Test
    fun `skill detail is one shared surface whatever the origin`() {
        // Reachable from Today, Learn and Progress, and the same object each time. A second
        // Skill detail surface is what UXIA-v0 forbids, so there is only one to reach.
        assertTrue(NavigationGraph.canOpen("today", "skill_detail"))
        assertTrue(NavigationGraph.canOpen("learn", "skill_detail"))
        assertTrue(NavigationGraph.canOpen("progress", "skill_detail"))
        assertEquals(1, Surface.all.count { it.id == "skill_detail" })
    }

    @Test
    fun `the surface registry is computed on access, not held as an initialised field`() {
        // A `val` here is filled while the companion initialises, which can run before the nested
        // objects it names exist — and the registry then holds nulls. That really happened when
        // another module first referenced a surface from its own initialiser (11A). A getter has no
        // backing field, so this fails the moment someone turns it back into an eager list.
        val backingFields = Surface.Companion::class.java.declaredFields.map { it.name }
        listOf("shellRoots", "sharedDetails", "focusedFlows", "all").forEach { property ->
            assertFalse(property in backingFields, "$property must be computed on access")
        }
        assertTrue(Surface.all.none { it == null }, "the registry contains an uninitialised surface")
    }

    @Test
    fun `peer switching is total between shell roots`() {
        Destination.entries.forEach { from ->
            Destination.entries.forEach { to ->
                assertEquals(from != to, NavigationGraph.canSwitch(from, to))
            }
        }
    }

    @Test
    fun `an unlisted contextual edge is not navigable`() {
        assertFalse(NavigationGraph.canOpen("profile", "skill_detail"))
        assertFalse(NavigationGraph.canOpen("learn", "assessment_report"))
        assertFalse(NavigationGraph.canOpen("learn", "task_runner_flow"))
    }

    @Test
    fun `window classes use the accepted breakpoints`() {
        assertEquals(WindowClass.COMPACT, WindowClass.ofWidthDp(599))
        assertEquals(WindowClass.MEDIUM, WindowClass.ofWidthDp(600))
        assertEquals(WindowClass.MEDIUM, WindowClass.ofWidthDp(839))
        assertEquals(WindowClass.EXPANDED, WindowClass.ofWidthDp(840))
    }

    @Test
    fun `presentation changes with the window class but the destinations do not`() {
        assertEquals(ShellPresentation.BOTTOM_NAVIGATION_BAR, ShellPresentation.of(WindowClass.COMPACT))
        assertEquals(ShellPresentation.NAVIGATION_RAIL, ShellPresentation.of(WindowClass.MEDIUM))
        assertEquals(
            ShellPresentation.NAVIGATION_RAIL_WITH_DETAIL_PANE,
            ShellPresentation.of(WindowClass.EXPANDED),
        )
        // The set and order are identical in every class.
        WindowClass.entries.forEach {
            assertEquals(listOf("today", "learn", "progress", "profile"), Destination.entries.map { d -> d.id })
        }
    }

    @Test
    fun `a focused flow suppresses the shell and requires a safe exit`() {
        val state = ShellState(Destination.TODAY, Surface.TaskRunnerFlow, WindowClass.COMPACT)
        assertFalse(state.showsShell)
        assertTrue(state.requiresSafeExit)
        assertFalse(state.showsDetailPane)
    }

    @Test
    fun `an ordinary surface keeps the shell`() {
        val state = ShellState(Destination.PROGRESS, Surface.SkillDetail, WindowClass.EXPANDED)
        assertTrue(state.showsShell)
        assertFalse(state.requiresSafeExit)
        assertTrue(state.showsDetailPane)
    }

    @Test
    fun `normal daily work returns to today`() {
        assertEquals(
            "today",
            NavigationGraph.returnTarget(Surface.TaskRunnerFlow, originSurfaceId = "today", originStillValid = true),
        )
    }

    @Test
    fun `a flow opened from an entity returns there only while the origin is valid`() {
        assertEquals(
            "skill_detail",
            NavigationGraph.returnTarget(Surface.TaskRunnerFlow, "skill_detail", originStillValid = true),
        )
        // A replan invalidated the origin: return somewhere real instead of stranding the learner.
        assertEquals(
            "today",
            NavigationGraph.returnTarget(Surface.TaskRunnerFlow, "skill_detail", originStillValid = false),
        )
    }

    @Test
    fun `a missing origin still returns somewhere deterministic`() {
        assertEquals(
            "today",
            NavigationGraph.returnTarget(Surface.AssessmentSessionFlow, null, originStillValid = false),
        )
    }

    @Test
    fun `only a focused flow has a return target`() {
        assertFailsWith<IllegalArgumentException> {
            NavigationGraph.returnTarget(Surface.SkillDetail, "today", originStillValid = true)
        }
    }
}
