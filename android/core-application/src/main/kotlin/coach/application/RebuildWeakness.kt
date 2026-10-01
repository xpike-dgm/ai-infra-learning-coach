package coach.application

import coach.engines.BlueprintComposer
import coach.engines.WeaknessEngine
import coach.model.AssessmentBlueprint
import coach.model.BlueprintSlotOutcome
import coach.model.EvidenceDispositions
import coach.model.ObjectiveGateProfile
import coach.model.ObjectiveWeakness
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import coach.model.WeaknessEvent
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord

/**
 * Rebuilding one Skill's weakness from evidence (13D) — the state family `WLRM-v0` owns (`MSBX-v0`).
 *
 * Each Objective is replayed on its own, with the mastery engine's decision about the Skill before and after
 * every row ([MasteryTimeline], the same timeline retention reads). The Objective's row is written to
 * `weakness_state`, and the Skill's weakness axis — only what its Objectives show — to `skill_state` with the
 * **older** watermark of the row and this rebuild, so the row never claims more truth than its oldest axis saw.
 * It writes no truth and decides no mastery.
 */
class RebuildWeakness(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Rebuilt(
        val skill: VersionedRef,
        val objectives: List<ObjectiveWeakness>,
        val axis: WeaknessAxis,
        val written: Boolean,
    )

    fun rebuild(skill: VersionedRef, profiles: List<ObjectiveGateProfile>): Rebuilt {
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion()
        val rows = MasteryTimeline.rowsOf(persistence, profiles)
        require(rows.all { it.second.studyDay != null }) { "an evidence row with no study day cannot be placed in time" }
        val byObjective = profiles.associateBy { it.ref }
        val steps = MasteryTimeline.of(skill, profiles, rows)
        val sessions = DiagnosticSessions.Cache(persistence)
        val objectives = profiles.map { profile ->
            // 13F: an Objective stops being a diagnostic baseline the moment ordinary learning reaches it.
            var learnedHere = false
            val events = steps.filter { it.objective == profile.ref }.map { step ->
                val row = step.row
                val inDiagnostic = sessions.diagnosticOf(row, profile.ref) != null
                val baseline = inDiagnostic && !learnedHere
                if (!inDiagnostic) learnedHere = true
                WeaknessEvent(
                    evidenceId = row.id, sequence = row.sequence, studyDay = row.studyDay!!, outcome = row.outcome,
                    evaluatorStatus = row.evaluatorStatus, independence = row.independenceClass, contested = row.contested,
                    prerequisiteValid = row.prerequisiteValid, solutionExposed = row.solutionExposed,
                    direct = byObjective[profile.ref]?.directEvidenceTypes?.contains(row.evidenceType) == true,
                    resource = row.resource, variantFamilyId = row.variantFamilyId,
                    masteredBefore = step.masteredBefore, masteredAfter = step.masteredAfter,
                    diagnosticBaseline = baseline,
                )
            }
            WeaknessEngine.replay(profile.ref, skill, events)
        }
        val axis = WeaknessAxis.of(objectives)
        val version = curriculumVersion ?: return Rebuilt(skill, objectives, axis, written = false)
        val builtAt = clock.now().instantEpochMillis
        val today = clock.now().studyDay
        persistence.inTransaction {
            objectives.forEach { WeaknessRows.writeObjective(persistence, it, today, watermark, version, builtAt) }
            WeaknessRows.writeAxis(persistence, skill, axis, watermark, version, builtAt)
        }
        return Rebuilt(skill, objectives, axis, written = true)
    }
}

/** `weakness_state` and the weakness axis of `skill_state`, as stored (13D). An empty column is "none". */
internal object WeaknessRows {

    fun key(objective: VersionedRef) = "weakness_state:${objective.logicalId}@v${objective.version}"

    fun writeObjective(persistence: PersistencePort, w: ObjectiveWeakness, today: String, watermark: Long, version: Int, builtAt: Long) {
        persistence.writeProjection(
            ProjectionRecord(
                key = key(w.objective),
                policyVersion = WeaknessEngine.POLICY_VERSION,
                truthWatermark = watermark,
                builtAtInstant = builtAt,
                inputCurriculumVersion = version,
                payload = mapOf(
                    "state" to w.signal.id,
                    "skill_logical_id" to w.skill.logicalId,
                    "skill_version" to w.skill.version.toString(),
                    "last_attribution_outcome" to w.lastOutcome?.id.orEmpty(),
                    "last_failure_rule" to w.lastRule?.id.orEmpty(),
                    "verification_open" to if (w.verificationOpen) "1" else "0",
                    "signal_evidence_ids" to w.signalEvidenceIds.joinToString(","),
                    "first_seen_on_study_day" to w.firstSeenDay.orEmpty(),
                    "last_seen_on_study_day" to w.lastSeenDay.orEmpty(),
                    "resolution_evidence_id" to w.resolutionEvidenceId?.toString().orEmpty(),
                    "as_of_study_day" to today,
                ),
            )
        )
    }

    fun writeAxis(persistence: PersistencePort, skill: VersionedRef, axis: WeaknessAxis, watermark: Long, version: Int, builtAt: Long) {
        val stateKey = ResolvePrerequisites.skillStateKey(skill)
        val existing = persistence.readProjection(stateKey)
        fun carried(column: String) = existing?.payload?.get(column)?.ifEmpty { null } ?: WeaknessAxis.NOT_YET_EVALUATED.id
        persistence.writeProjection(
            ProjectionRecord(
                key = stateKey,
                // The row's policy is its mastery engine's; weakness only adds its own axis to it.
                policyVersion = existing?.policyVersion ?: WeaknessEngine.POLICY_VERSION,
                truthWatermark = minOf(existing?.truthWatermark ?: watermark, watermark),
                builtAtInstant = builtAt,
                inputCurriculumVersion = version,
                payload = mapOf(
                    "mastery_axis_state" to carried("mastery_axis_state"),
                    "retention_axis_state" to carried("retention_axis_state"),
                    "prerequisite_axis_state" to carried("prerequisite_axis_state"),
                    "weakness_axis_state" to axis.id,
                    "primary_presentation_state" to carried("primary_presentation_state"),
                ),
            )
        )
    }
}

/**
 * Appending a correction to one evidence row (13D). The row is append-only truth and is never edited; the
 * disposition is read back by the store so every engine sees the corrected row (`EvidenceDispositions`).
 */
class RecordDisposition(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun record(evidenceId: Long, disposition: String, reasonCode: String, decidedBy: String): Long {
        require(disposition in EvidenceDispositions.VALUES) { "unknown disposition $disposition" }
        require(decidedBy in DECIDED_BY) { "unknown decider $decidedBy" }
        require(reasonCode.isNotBlank()) { "a correction says why" }
        requireNotNull(persistence.readTruth("evidence_event", evidenceId)) { "evidence $evidenceId does not exist" }
        return persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "evidence_disposition", clock.now(),
                    mapOf(
                        "evidence_event_id" to evidenceId.toString(),
                        "disposition" to disposition,
                        "reason_code" to reasonCode,
                        "decided_by" to decidedBy,
                    ),
                )
            )
        }
    }

    companion object {
        /** `DDM-v0`'s deciders, as the store's CHECK holds them. */
        val DECIDED_BY = listOf("deterministic_rule", "validator", "user_report")
    }
}

/**
 * Retroactive root-cause contamination (13D; 13A and 13B left it here). Inside a blueprint session a Skill
 * may be shown cleanly missing **after** a downstream slot that needed it was already answered. That answer
 * was recorded as clean, and `WBA-v0` §25 / `MCA-v0` §20 say it must not count against its target. It is not
 * edited: a disposition is appended — `invalidated`, `prerequisite_contaminated`, decided by a deterministic
 * rule — and the store's reading of dispositions makes it prerequisite-contaminated for every engine.
 *
 * Only slots whose item really requires a Skill the session showed cleanly missing are touched; independent
 * branches never are, and a row already contaminated or already corrected is left alone.
 */
class ApplyRetroactiveContamination(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun apply(blueprint: AssessmentBlueprint, outcomes: List<BlueprintSlotOutcome>): List<Long> {
        val failed = BlueprintComposer.cleanlyFailedSkills(blueprint, outcomes)
        if (failed.isEmpty()) return emptyList()
        val slots = blueprint.slots.associateBy { it.slotId }
        val targets = outcomes.filter { outcome ->
            val slot = slots[outcome.slotId] ?: return@filter false
            BlueprintComposer.contaminatedBy(slot, failed) && slot.targetSkill !in failed
        }.flatMap { it.evidence }.filterNot { it.prerequisiteContaminated }
        val recorder = RecordDisposition(persistence, clock)
        return targets.mapNotNull { fact ->
            val stored = persistence.evidenceFor(fact.objective).firstOrNull { it.id == fact.evidenceId } ?: return@mapNotNull null
            // Already corrected (or contaminated when recorded): nothing more to say.
            if (!stored.prerequisiteValid) return@mapNotNull null
            recorder.record(fact.evidenceId, EvidenceDispositions.INVALIDATED, EvidenceDispositions.PREREQUISITE_CONTAMINATED, "deterministic_rule")
            fact.evidenceId
        }
    }
}
