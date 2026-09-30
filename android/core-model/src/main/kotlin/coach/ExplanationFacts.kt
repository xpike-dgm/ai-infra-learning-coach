package coach.model

/**
 * What the shared `planner_explanation` surface is built from (12E): today's plan trace as the planner
 * stored it, and the names of the Skills it mentions. Nothing here is an explanation yet — that is a
 * projection of these facts (`PDT-v0` §2.2), and it can say nothing the trace did not record.
 */
sealed interface PlannerExplanationFacts {

    /** No plan has been produced yet, so there is no decision to explain. */
    data object NoPlan : PlannerExplanationFacts

    /** The newest plan belongs to another study day; it is not explained as today's (`SRR-v0`). */
    data class PlanFromAnotherDay(val planStudyDay: String) : PlannerExplanationFacts

    /** Today's plan exists but its trace cannot be trusted to describe it; no reason is guessed. */
    data class Unreadable(val reason: String) : PlannerExplanationFacts

    /**
     * Today's trace, read back and checked against the plan's own rows, and each mentioned Skill's
     * published name. A Skill with no published name is simply absent here; its reference is shown.
     */
    data class Readable(
        val trace: PlanTrace,
        val skillNames: Map<VersionedRef, String>,
    ) : PlannerExplanationFacts
}
