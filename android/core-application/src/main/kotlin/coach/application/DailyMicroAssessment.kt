package coach.application

import coach.model.AssessmentIntent
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.ItemFit
import coach.model.ItemSelection
import coach.model.ItemUnfit
import coach.model.LifecycleStatus
import coach.model.ObjectiveEvidenceProfile
import coach.model.PublishOutcome
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Ingesting authored curriculum, and serving one daily micro assessment item (11D).
 *
 * Neither of these is an authority. Ingestion publishes what was authored and refuses the rest;
 * serving reports whether an item may be used and records that the learner saw it. What to measure
 * and when stays with the planner (12), and what an attempt proves stays with the evidence
 * pipeline (12).
 */
class IngestCurriculum(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
) {
    /**
     * Publishes the authored package, if one ships. Running it twice publishes nothing the second
     * time: curriculum is immutable once published, so the second outcome is `AlreadyPublished`
     * rather than an overwrite.
     */
    fun ingest(): PublishOutcome? {
        val authored = content.curriculumPackage() ?: return null
        return persistence.publishCurriculum(authored, clock.now().instantEpochMillis)
    }

    /**
     * Publishes every authored package in order (15B, `D-113`). One already published answers `AlreadyPublished` and
     * nothing is written for it; the first refusal stops the run, because every later package builds on the earlier
     * ones and would only be refused for references that do not exist. The outcomes say exactly what happened.
     */
    fun ingestAll(): List<PublishOutcome> = buildList {
        val now = clock.now().instantEpochMillis
        for (authored in content.curriculumPackages().sortedBy { it.version }) {
            val outcome = persistence.publishCurriculum(authored, now)
            add(outcome)
            if (outcome is PublishOutcome.Refused) break
        }
    }
}

/** Why the item cannot be served, or the item and what it may be used for. */
sealed interface ItemOffer {
    data class Ready(val item: AssessmentItem, val fit: ItemFit.Usable) : ItemOffer

    /**
     * The item may not carry this measurement. It is a fact about the item and the store, never
     * about the learner: nothing negative is recorded and nothing is presented as their error.
     */
    data class Unusable(val reasons: Set<ItemUnfit>) : ItemOffer

    /** No such published item, or no authored document behind the published reference. */
    data object Unknown : ItemOffer
}

class ServeDailyMicroItem(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
) {
    /**
     * Decides whether a pinned item may be used for this intent, and records the exposure when it
     * is served.
     *
     * Trust comes from the **store**, not from the document: the published validation record
     * overrides whatever lifecycle status the authored item claims for itself, because an item that
     * could promote itself would make `AIV-v0`'s validation pipeline decorative.
     */
    fun offer(
        ref: VersionedRef,
        intent: AssessmentIntent,
        evaluatorAvailable: Boolean,
        scope: AssessmentScope = AssessmentScope.DAILY_MICRO,
    ): ItemOffer {
        val published = persistence.resourceVersion(ref) ?: return ItemOffer.Unknown
        val authored = content.assessmentItem(ref) ?: return ItemOffer.Unknown
        val item = authored.copy(
            lifecycleStatus = persistence.latestValidation(ref)?.status ?: LifecycleStatus.CANDIDATE,
            contentOrigin = published.contentOrigin,
            evidenceType = published.evidenceType,
        )
        val profiles: Map<VersionedRef, ObjectiveEvidenceProfile> = item.targetObjectives
            .mapNotNull { persistence.objectiveProfile(it) }
            .associateBy { it.ref }

        return when (val fit = ItemSelection.fit(item, intent, scope, profiles, evaluatorAvailable)) {
            is ItemFit.NotUsable -> ItemOffer.Unusable(fit.reasons)
            is ItemFit.Usable -> {
                recordExposure(item.ref, item.variantFamilyId, ITEM_VERSION_SEEN, null)
                ItemOffer.Ready(item, fit)
            }
        }
    }

    /**
     * Records that a solution was exposed for this item (`QAB-v0` §24). Exposure is permanent
     * (`LFPS-v0`): the same item can never again serve as a fresh independent check, and forgetting
     * that is how a product starts re-measuring with an answer the learner has already seen.
     */
    fun recordSolutionExposure(item: AssessmentItem, attemptId: Long?, maxLevel: String) {
        recordExposure(item.ref, item.variantFamilyId, SOLUTION_EXPOSURE, attemptId, maxLevel)
    }

    private fun recordExposure(
        ref: VersionedRef,
        variantFamilyId: String,
        kind: String,
        attemptId: Long?,
        maxLevel: String? = null,
    ) {
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "exposure_record", at,
                    buildMap {
                        put("resource_logical_id", ref.logicalId)
                        put("resource_version", ref.version.toString())
                        put("variant_family_id", variantFamilyId)
                        put("exposure_kind", kind)
                        maxLevel?.let { put("max_exposure_level", it) }
                        attemptId?.let { put("source_attempt_id", it.toString()) }
                    },
                )
            )
        }
    }

    companion object {
        const val ITEM_VERSION_SEEN = "item_version_seen"
        const val SOLUTION_EXPOSURE = "solution_exposure"
    }
}
