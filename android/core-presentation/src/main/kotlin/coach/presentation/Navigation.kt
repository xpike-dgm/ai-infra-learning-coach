package coach.presentation

/**
 * The navigation model lives in core-presentation, not in the UI toolkit.
 *
 * `UXIA-v0 / D-068` fixes the destination set, their order and which surfaces are shared; a shell
 * that decided those things inside Compose could only be tested on a device. Here every rule is a
 * pure function, and `app-ui` is left with rendering.
 */

/**
 * Exactly four semantic top-level destinations, in fixed order (`UXIA-v0` §5.1). Enum order **is**
 * the canonical order, so a reordering is a source change rather than a runtime accident.
 */
enum class Destination(val id: String) {
    TODAY("today"),
    LEARN("learn"),
    PROGRESS("progress"),
    PROFILE("profile"),
    ;

    /** 1-based semantic order, as recorded in the accepted IA contract. */
    val order: Int get() = ordinal + 1

    companion object {
        /** `today` is the normal start destination (`UXIA-v0` §5.1). */
        val start: Destination = TODAY

        fun byId(id: String): Destination? = entries.firstOrNull { it.id == id }
    }
}

/**
 * Every surface the shell can show. Shared details are a **single** semantic surface reachable
 * from several destinations — `UXIA-v0` forbids contradictory Skill detail pages under Learn and
 * Progress, and modelling one route type per entity makes that impossible rather than discouraged.
 */
sealed interface Surface {
    val id: String

    /** A focused flow suspends the shell and must always offer a safe exit (`TRUX-v0`, `WFPX-v0`). */
    val isFocusedFlow: Boolean get() = false

    sealed interface ShellRoot : Surface {
        val destination: Destination
    }

    data object TodayOverview : ShellRoot {
        override val id = "today_overview"
        override val destination = Destination.TODAY
    }

    data object LearnOverview : ShellRoot {
        override val id = "learn_overview"
        override val destination = Destination.LEARN
    }

    data object ProgressOverview : ShellRoot {
        override val id = "progress_overview"
        override val destination = Destination.PROGRESS
    }

    data object ProfileOverview : ShellRoot {
        override val id = "profile_overview"
        override val destination = Destination.PROFILE
    }

    /** Shared detail surfaces. One surface per canonical entity, whatever the origin. */
    data object SkillDetail : Surface { override val id = "skill_detail" }
    data object TopicDetail : Surface { override val id = "topic_detail" }
    data object PlannerExplanation : Surface { override val id = "planner_explanation" }
    data object AssessmentReport : Surface { override val id = "assessment_report" }
    data object LearningHistory : Surface { override val id = "learning_history" }
    data object TechnicalEnglishProfile : Surface { override val id = "technical_english_profile" }

    /** Focused flows. Not destinations: assessment is a workflow, not a tab (`UXIA-v0` §6.1). */
    data object TaskRunnerFlow : Surface {
        override val id = "task_runner_flow"
        override val isFocusedFlow = true
    }

    data object AssessmentSessionFlow : Surface {
        override val id = "assessment_session_flow"
        override val isFocusedFlow = true
    }

    companion object {
        // These are computed on access rather than held as initialised fields. A `val` here is
        // filled while the companion initialises, which can run before the nested objects it names
        // exist — and then the registry silently contains nulls, as it did the first time another
        // module referenced a surface from its own initialiser (11A). The same Kotlin
        // initialisation-order trap cost 10D a whole failing suite; `get()` removes it structurally.
        val shellRoots: List<ShellRoot>
            get() = listOf(TodayOverview, LearnOverview, ProgressOverview, ProfileOverview)

        val sharedDetails: List<Surface>
            get() = listOf(
                SkillDetail, TopicDetail, PlannerExplanation,
                AssessmentReport, LearningHistory, TechnicalEnglishProfile,
            )

        val focusedFlows: List<Surface>
            get() = listOf(TaskRunnerFlow, AssessmentSessionFlow)

        val all: List<Surface>
            get() = shellRoots + sharedDetails + focusedFlows

        fun rootOf(destination: Destination): ShellRoot =
            shellRoots.first { it.destination == destination }
    }
}

/**
 * Window classes and their breakpoints are `WFPX-v0`'s, not this module's invention. They decide
 * how the shell is presented and never what the destinations mean (`UXIA-v0` §16).
 */
enum class WindowClass {
    COMPACT,
    MEDIUM,
    EXPANDED,
    ;

    companion object {
        const val MEDIUM_MIN_WIDTH_DP = 600
        const val EXPANDED_MIN_WIDTH_DP = 840

        fun ofWidthDp(widthDp: Int): WindowClass = when {
            widthDp >= EXPANDED_MIN_WIDTH_DP -> EXPANDED
            widthDp >= MEDIUM_MIN_WIDTH_DP -> MEDIUM
            else -> COMPACT
        }
    }
}

/** How the shell is drawn for a window class. The set of destinations is unaffected. */
enum class ShellPresentation {
    BOTTOM_NAVIGATION_BAR,
    NAVIGATION_RAIL,
    NAVIGATION_RAIL_WITH_DETAIL_PANE,
    ;

    companion object {
        fun of(windowClass: WindowClass): ShellPresentation = when (windowClass) {
            WindowClass.COMPACT -> BOTTOM_NAVIGATION_BAR
            WindowClass.MEDIUM -> NAVIGATION_RAIL
            WindowClass.EXPANDED -> NAVIGATION_RAIL_WITH_DETAIL_PANE
        }
    }
}

/**
 * The reachability rules of `UXIA-v0` §9, expressed once.
 *
 * Peer switching is total: from any shell root the user can reach any other. Contextual edges are
 * enumerated, because "anything can open anything" is how a browse hierarchy quietly starts
 * implying prerequisite truth.
 */
object NavigationGraph {

    /** Contextual edges, exactly as accepted in the IA contract. */
    val contextualEdges: Set<Pair<String, String>> = setOf(
        "today" to "task_runner_flow",
        "today" to "planner_explanation",
        "today" to "skill_detail",
        "today" to "topic_detail",
        "today" to "assessment_session_flow",
        "learn" to "topic_detail",
        "learn" to "skill_detail",
        "topic_detail" to "skill_detail",
        "progress" to "skill_detail",
        "progress" to "topic_detail",
        "progress" to "technical_english_profile",
        "progress" to "assessment_report",
        "progress" to "learning_history",
        "skill_detail" to "learning_history",
        "assessment_report" to "learning_history",
    )

    /** Any shell root may switch to any other shell root (`UXIA-v0` §9.1). */
    fun canSwitch(from: Destination, to: Destination): Boolean = from != to

    fun canOpen(fromSurfaceId: String, toSurfaceId: String): Boolean =
        (fromSurfaceId to toSurfaceId) in contextualEdges

    /**
     * A completed or paused focused flow returns to a deterministic destination (`UXIA-v0` §9.3).
     * The rule exists so a replan can never strand the learner in an unrelated section.
     */
    fun returnTarget(
        flow: Surface,
        originSurfaceId: String?,
        originStillValid: Boolean,
    ): String {
        require(flow.isFocusedFlow) { "${flow.id} is not a focused flow" }
        val startedFromEntityContext = originSurfaceId != null && originSurfaceId != Destination.TODAY.id
        return when {
            !startedFromEntityContext -> Destination.TODAY.id
            originStillValid -> originSurfaceId!!
            else -> Destination.TODAY.id
        }
    }
}

/**
 * What the shell shows right now.
 *
 * A focused flow suppresses the navigation shell, which is why [showsShell] is derived rather than
 * set: a flow that forgot to hide the bar, or a shell that lost its exit, would be a state this
 * type cannot represent.
 */
data class ShellState(
    val selected: Destination,
    val surface: Surface,
    val windowClass: WindowClass,
) {
    val presentation: ShellPresentation = ShellPresentation.of(windowClass)

    val showsShell: Boolean = !surface.isFocusedFlow

    val requiresSafeExit: Boolean = surface.isFocusedFlow

    /** The detail pane exists only in the expanded class, and shows the same surface and truth. */
    val showsDetailPane: Boolean =
        presentation == ShellPresentation.NAVIGATION_RAIL_WITH_DETAIL_PANE && !surface.isFocusedFlow
}
