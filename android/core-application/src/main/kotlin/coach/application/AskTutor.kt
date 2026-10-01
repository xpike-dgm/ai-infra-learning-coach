package coach.application

import coach.model.AuthoredHelp
import coach.model.TutorOutcome
import coach.model.TutorRequest
import coach.model.TutorRules
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord
import coach.ports.TutorPort

/**
 * Asking the tutor (`TUTX-v0` §10).
 *
 * The tutor proposes a reply; [TutorRules.accept] decides what the learner sees. This use case adds the one
 * thing only a store can do: **a solution shown is an exposure from the moment it is shown** (`QAB-v0` §24,
 * `LFPS-v0`), whether or not an attempt ever follows. Without that record the same item, or a near variant,
 * could later be served as a fresh independent check of something the learner has already been shown — the
 * exact promise the H3/H4 disclosure makes would be untrue.
 *
 * Nothing else is written here. The assistance event goes with the attempt when it is submitted
 * (`SubmitAttempt`, one transaction, 11B); help that was not shown is recorded nowhere; the tutor's text is
 * not stored, because it is teaching material, not truth (`TUTX-v0` §10).
 */
class AskTutor(
    private val tutor: TutorPort,
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    /** The item being worked, when there is one: the resource and its variant family. */
    data class Item(val resource: VersionedRef, val variantFamilyId: String)

    fun ask(
        request: TutorRequest,
        item: Item? = null,
        fallback: AuthoredHelp? = null,
        attemptId: Long? = null,
    ): TutorOutcome {
        val outcome = TutorRules.accept(request, tutor.assist(request), fallback)
        if (outcome is TutorOutcome.Shown && outcome.revealsTargetReasoning && item != null) recordSolutionExposure(outcome, item, attemptId)
        return outcome
    }

    /**
     * Written help shown **without asking the tutor** (14C, "önce yazılmış", user decision): the tutor is not called and
     * nothing leaves the device. `null` when the written help does not fit the request (another intent, or above the
     * learner's ceiling), so the caller may ask the tutor instead. A written solution shown is an exposure exactly like
     * one the tutor wrote.
     */
    fun showWritten(request: TutorRequest, help: AuthoredHelp, item: Item? = null, attemptId: Long? = null): TutorOutcome.Shown? {
        val outcome = TutorRules.authored(request, help) ?: return null
        if (outcome.revealsTargetReasoning && item != null) recordSolutionExposure(outcome, item, attemptId)
        return outcome
    }

    private fun recordSolutionExposure(outcome: TutorOutcome.Shown, item: Item, attemptId: Long?) {
        val event = outcome.event!!
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "exposure_record", at,
                    buildMap {
                        put("resource_logical_id", item.resource.logicalId)
                        put("resource_version", item.resource.version.toString())
                        put("variant_family_id", item.variantFamilyId)
                        put("exposure_kind", ServeDailyMicroItem.SOLUTION_EXPOSURE)
                        put("max_exposure_level", event.level.id)
                        attemptId?.let { put("source_attempt_id", it.toString()) }
                    },
                )
            )
        }
    }
}
