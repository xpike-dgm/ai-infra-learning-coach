package coach.application

import coach.model.CapacityProfile
import coach.model.DiagnosticSource
import coach.model.SetupOutcome
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * The first-run setup (18A, `D-139`; `UXIA-v0` §7.4 `initial_setup_flow`: pre-shell, the minimum local setup, then Today).
 *
 * It asks for two things and decides neither for the learner: the length of a normal day (a 16D preference, the planner's
 * capacity) and, optionally, the areas the learner already knows something of. Ticking an area opens one diagnostic over
 * its Skills (`VDW-v0` §3 `curriculum_entry_placement`): saying "I know this" only names the scope — it is not evidence,
 * nothing is waived by it, and no diagnostic is mandatory (`UXIA-v0`: `mandatory_diagnostic_invented_by_ia: false`).
 * Skipping is always possible and costs nothing: the presets stand and learning starts at the beginning.
 *
 * It plans nothing: Today's plan is made after it, by the planner, from what was recorded here.
 */
class InitialSetup(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
) {
    /** A Topic and the Skills it places, as authored — the setup groups them by area. */
    data class TopicSkills(val topic: VersionedRef, val skills: List<VersionedRef>)

    data class Finished(val outcome: SetupOutcome, val placement: RequestDiagnostic.Requested?)

    /**
     * Shown once: until the learner has gone through it, and only on a store no plan was ever made in. A store from
     * before 18A, or a restored backup with work in it, has been used — it is not asked again. With nothing published
     * there are no areas to offer, and the honest empty Today stands instead.
     */
    fun needed(): Boolean =
        persistence.curriculumPublished() && persistence.setupRecords().isEmpty() && persistence.latestPlan() == null

    /** The newest version of every authored Topic and the published Skills it places, in a stable order. */
    fun topics(): List<TopicSkills> {
        val packages = content.curriculumPackages()
        val links = packages.flatMap { it.topicSkillLinks }
        return packages.flatMap { it.topics }.groupBy { it.logicalId }.values
            .map { versions -> versions.maxBy { it.version } }.sortedBy { it.logicalId }
            .map { topic ->
                val ref = VersionedRef(topic.logicalId, topic.version)
                TopicSkills(ref, links.filter { it.topic == ref }.map { it.skill }.distinct().filter { persistence.skill(it) != null })
            }
            .filter { it.skills.isNotEmpty() }
    }

    /**
     * Records how the setup ended. [normalMinutes] is the learner's normal day, or `null` to keep the preset; [placement]
     * the Skills of the areas ticked, each asked of the diagnostic only if new learning may start on it. The setup row is
     * appended last, so a setup interrupted before it is simply offered again.
     */
    fun finish(outcome: SetupOutcome, normalMinutes: Int? = null, placement: List<VersionedRef> = emptyList()): Finished {
        // Review (18A): a setup interrupted after its diagnostic was asked for but before its row was written is offered
        // again, and what the learner chooses now replaces that attempt — a leftover placement diagnostic is withdrawn
        // first, so a skip (or nothing ticked) leaves no diagnostic the learner declined.
        if (persistence.setupRecords().isEmpty() &&
            DiagnosticSessions.active(persistence)?.scope?.source == DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT
        ) {
            WithdrawDiagnostic(persistence, clock).withdraw()
        }
        if (outcome == SetupOutcome.COMPLETED) {
            normalMinutes?.let { CapacitySettings(persistence, clock).setProfile(CapacityProfile.NORMAL, it) }
        }
        val startable = placement.distinct().filter { persistence.skill(it)?.lifecycleStatus == PUBLISHED }
        val request = if (outcome == SetupOutcome.COMPLETED && startable.isNotEmpty()) {
            RequestDiagnostic(persistence, clock).request(DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT, startable)
        } else {
            null
        }
        val at = clock.now()
        persistence.inTransaction { persistence.appendTruth(TruthRecord(KIND, at, mapOf("setup_outcome" to outcome.id))) }
        return Finished(outcome, request)
    }

    companion object {
        const val KIND = "setup_record"
        private const val PUBLISHED = "published"
    }
}
