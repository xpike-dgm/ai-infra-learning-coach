package coach.application

import coach.model.DiagnosticSource
import coach.model.ReplanTrigger
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * The fast path from the app (16H, `ASRX-v0`; 13F `VDWX-v0`). Only the learner opens it, and saying "I know this" only
 * names the scope (`VDW-v0` §3): the request is recorded, and the checks join **today's** plan through a replan that keeps
 * every task the learner started (user decision, `D-132`). Leaving it is always possible and owes nothing.
 */
class FastPath(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
    private val evaluatorAvailable: Boolean = false,
) {
    class Requested(val request: RequestDiagnostic.Requested, val plan: BuildDailyPlan.Built?)

    /**
     * Asks for the fast path over [skills] — only those new learning may start on (`KGC-v0` §27); the rest are left out,
     * not refused for the others. Nothing is planned when nothing was requested.
     */
    fun request(skills: List<VersionedRef>): Requested {
        val startable = skills.filter { persistence.skill(it)?.lifecycleStatus == PUBLISHED }
        val request = RequestDiagnostic(persistence, clock).request(DiagnosticSource.USER_REQUESTED_FAST_PATH, startable)
        if (request !is RequestDiagnostic.Requested.Written) return Requested(request, null)
        val builder = BuildDailyPlan(persistence, content, clock, evaluatorAvailable)
        val capacity = CapacitySettings(persistence, clock).read().input()
        val plan = when (val built = builder.build(capacity)) {
            // Today's plan stands: the request is an event, and only the not-started rest is re-resolved (12D).
            is BuildDailyPlan.Built.AlreadyPlanned ->
                builder.replan(BuildDailyPlan.ReplanRequest(ReplanTrigger.DIAGNOSTIC_REQUESTED, capacity, keptPositions = StartedTasks.today(persistence, clock)))
            else -> built
        }
        return Requested(request, plan)
    }

    /** Leaves the fast path (`VDW-v0` §3): waivers already granted stay; the rest returns to normal learning. */
    fun withdraw(): WithdrawDiagnostic.Withdrawn = WithdrawDiagnostic(persistence, clock).withdraw()

    /** The open diagnostic's result, from the projection the planner reads. */
    fun result(): ReadDiagnosticResult.Read = ReadDiagnosticResult(persistence).read()

    private companion object {
        const val PUBLISHED = "published"
    }
}
