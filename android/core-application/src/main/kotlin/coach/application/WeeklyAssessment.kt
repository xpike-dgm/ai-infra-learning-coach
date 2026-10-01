package coach.application

import coach.engines.WeeklyBlueprintEngine
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.CandidateDisposition
import coach.model.EvaluationResult
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.ObjectiveEvidenceProfile
import coach.model.PlanTraceCodec
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteSnapshot
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.SkillRow
import coach.model.TaskCandidate
import coach.model.VersionedRef
import coach.model.WeeklyAssessmentBlueprint
import coach.model.WeeklyBlueprintCodec
import coach.model.WeeklyCycle
import coach.model.WeeklySlotOutcome
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.StoredTruth
import coach.ports.TruthRecord

/**
 * The weekly assessment (13A): composing the week's blueprint, offering its slots to the planner, and
 * recording what a slot proved.
 *
 * None of this is an authority. Composition decides what is worth measuring this week and with which
 * trusted item; **the planner** decides which slot runs on which day, inside the day's real capacity; the
 * **evidence pipeline** and the engines decide what an answer proved. A week is an identity, not a
 * deadline: a week that passed without its assessment leaves nothing behind.
 */
class ComposeWeeklyAssessment(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
) {
    sealed interface Composed {
        /** No curriculum is published; there is nothing to measure against and nothing is written. */
        data object NothingPublished : Composed

        /** This week already has its blueprint; nothing is written and it is returned as stored. */
        data class AlreadyComposed(val sessionId: Long, val blueprint: WeeklyAssessmentBlueprint) : Composed

        /**
         * Nothing is worth measuring this week, or nothing trusted can measure it. That is not an exam
         * the learner missed: nothing is written, so a later composition this week can still find one.
         */
        data class NothingToMeasure(val blueprint: WeeklyAssessmentBlueprint) : Composed

        /** The last blueprint could not be read; nothing is guessed and nothing is written. */
        data class Refused(val reason: String) : Composed

        /** A blueprint was composed and appended as one new `assessment_session` row. */
        data class Written(val sessionId: Long, val blueprint: WeeklyAssessmentBlueprint) : Composed
    }

    /**
     * Composes this week's blueprint once. [ownerNeeds] are the needs only their owners open — the
     * parallel track's cadence, integration opportunities — exactly as the planner receives them.
     */
    fun compose(evaluatorAvailable: Boolean, ownerNeeds: List<LearningNeed> = emptyList()): Composed {
        // Read first, like every projection: the blueprint can never claim more truth than it saw.
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion() ?: return Composed.NothingPublished
        val now = clock.now()
        val cycle = WeeklyCycle.of(now.studyDay)

        val previous = persistence.latestAssessmentSession(AssessmentScope.WEEKLY_BLUEPRINT)
        val previousBlueprint = previous?.let(::decode)
        if (previous != null && previousBlueprint == null) return Composed.Refused("the last weekly blueprint cannot be read")
        if (previousBlueprint != null && previousBlueprint.cycleId == cycle) {
            return Composed.AlreadyComposed(previous!!.id, previousBlueprint)
        }

        val states = PlanningStates.read(persistence, persistence.publishedSkills())
        // "Since the last cycle" (`WBA-v0` §8): from the day the previous blueprint was composed. With no
        // previous blueprint there is no "since", and every Skill in progress is recent.
        val recentSince = previousBlueprint?.studyDay
        val recent = recentSince?.let { persistence.skillsEvidencedSince(it).toSet() }
        val pool = WeeklyBlueprintEngine.targetPool(states, ownerNeeds, recent, holdingBack())
        val inputs = gather(pool.entries.map { it.skill })

        val blueprint = WeeklyBlueprintEngine.compose(
            cycleId = cycle,
            studyDay = now.studyDay,
            curriculumVersion = curriculumVersion,
            truthWatermark = watermark,
            pool = pool,
            items = inputs.items,
            profiles = inputs.profiles,
            decisions = inputs.decisions,
            exposures = inputs.exposures,
            evaluatorAvailable = evaluatorAvailable,
            recentSince = recentSince,
            previousCycleId = previousBlueprint?.cycleId,
        )
        if (blueprint.readySlots.isEmpty()) return Composed.NothingToMeasure(blueprint)
        return Composed.Written(append(blueprint), blueprint)
    }

    /**
     * Recomposes unresolved slots of this week's blueprint (`WBA-v0` §17, `ASUX-v0` §9). The caller —
     * the session, which knows which of the five conditions happened — names the slots; submitted slots
     * are never among them. The recomposition is a **new** session row that names the one it supersedes;
     * the old row is not edited, and the attempts already made in it stay where they were made.
     */
    fun recompose(slotIds: Set<String>, evaluatorAvailable: Boolean): Composed {
        persistence.latestCurriculumVersion() ?: return Composed.NothingPublished
        val now = clock.now()
        val previous = persistence.latestAssessmentSession(AssessmentScope.WEEKLY_BLUEPRINT)
            ?: return Composed.Refused("there is no weekly blueprint to recompose")
        val blueprint = decode(previous) ?: return Composed.Refused("the last weekly blueprint cannot be read")
        if (blueprint.cycleId != WeeklyCycle.of(now.studyDay)) {
            return Composed.Refused("a past week is not recomposed; this week composes its own")
        }
        val unknown = slotIds - blueprint.slots.map { it.slotId }.toSet()
        if (unknown.isNotEmpty()) return Composed.Refused("slots $unknown are not in this week's blueprint")

        val targets = blueprint.slots.filter { it.slotId in slotIds }.map { it.targetSkill }
        val inputs = gather(targets)
        val recomposed = WeeklyBlueprintEngine.recompose(
            previous = blueprint,
            previousSessionId = previous.id,
            slotIds = slotIds,
            items = inputs.items,
            profiles = inputs.profiles,
            decisions = inputs.decisions,
            exposures = inputs.exposures,
            evaluatorAvailable = evaluatorAvailable,
        )
        return Composed.Written(append(recomposed), recomposed)
    }

    private fun append(blueprint: WeeklyAssessmentBlueprint): Long = persistence.inTransaction {
        persistence.appendTruth(
            TruthRecord(
                "assessment_session", clock.now(),
                mapOf(
                    "scope" to AssessmentScope.WEEKLY_BLUEPRINT.storedAs,
                    "blueprint" to WeeklyBlueprintCodec.encode(blueprint),
                ),
            )
        )
    }

    /**
     * The Skills that held dependent work back when the planner last planned, as the gate answered at
     * the time (`planner_trace/3` records it). The gate is not re-run to find out.
     */
    private fun holdingBack(): Set<VersionedRef> {
        val trace = persistence.latestPlan()?.traceText?.let(PlanTraceCodec::decode) ?: return emptySet()
        return trace.candidates.filter { it.disposition == CandidateDisposition.BLOCKED_PREREQUISITE }
            .flatMap { it.relatedSkills }.toSet()
    }

    private class Inputs(
        val items: Map<VersionedRef, List<AssessmentItem>>,
        val profiles: Map<VersionedRef, ObjectiveEvidenceProfile>,
        val decisions: Map<VersionedRef, PrerequisiteDecision>,
        val exposures: List<ExposureFact>,
    )

    /**
     * Everything the composer is told about the candidate items, each fact from its owner: trust from
     * the store's validation record (never the item's own claim), evidence fit from the Objective's
     * profile, eligibility from the prerequisite gate, and exposure from the learner's own record.
     */
    private fun gather(skills: List<VersionedRef>): Inputs {
        val items = skills.associateWith { skill -> content.assessmentItemsFor(skill).mapNotNull(::withStoreTrust) }
        val all = items.values.flatten().distinctBy { it.ref }
        val profiles = all.flatMap { it.targetObjectives }.distinct()
            .mapNotNull { persistence.objectiveProfile(it) }.associateBy { it.ref }
        val gate = ResolvePrerequisites(persistence)
        val decisions = items.flatMap { (skill, list) ->
            list.map { item ->
                item.ref to gate.resolve(PrerequisiteCandidate(candidateId = item.ref.toString(), target = skill,
                    requiredSkills = item.requiredSkills))
            }
        }.toMap()
        val exposures = if (all.isEmpty()) emptyList()
        else persistence.exposuresFor(all.map { it.ref }, all.map { it.variantFamilyId }.distinct())
        return Inputs(items, profiles, decisions, exposures)
    }

    /**
     * An item as the store knows it: published or not there at all, and trusted as far as its latest
     * validation record says — exactly as a daily item is served (11D).
     */
    private fun withStoreTrust(item: AssessmentItem): AssessmentItem? {
        val published = persistence.resourceVersion(item.ref) ?: return null
        return item.copy(
            lifecycleStatus = persistence.latestValidation(item.ref)?.status ?: LifecycleStatus.CANDIDATE,
            contentOrigin = published.contentOrigin,
            evidenceType = published.evidenceType,
        )
    }

    private fun decode(row: StoredTruth): WeeklyAssessmentBlueprint? =
        row.record.payload["blueprint"]?.let(WeeklyBlueprintCodec::decode)
}

/**
 * This week's slots as the planner's candidates (13A). Nothing here is a second planner: the slots
 * serve needs the planner already opened, and which of them fits today is `PBR-v0`'s and D-033's.
 */
internal object WeeklySlots {

    fun candidates(persistence: PersistencePort, studyDay: String, openNeeds: List<LearningNeed>): List<TaskCandidate> {
        val row = persistence.latestAssessmentSession(AssessmentScope.WEEKLY_BLUEPRINT) ?: return emptyList()
        val blueprint = row.record.payload["blueprint"]?.let(WeeklyBlueprintCodec::decode) ?: return emptyList()
        // Only this week's blueprint is ever offered; an earlier week's unfinished slots are not debt.
        if (blueprint.cycleId != WeeklyCycle.of(studyDay)) return emptyList()
        val ready = blueprint.readySlots.mapNotNull { it.item }
        if (ready.isEmpty()) return emptyList()
        // Every item in a ready slot was unseen when the blueprint was composed, so any exposure now means
        // the slot has been served and is not offered again.
        val served = persistence.exposuresFor(ready, emptyList()).map { it.resource }.toSet()
        val open = openNeeds.map { it.needKey }.toSet()
        return WeeklyBlueprintEngine.slotCandidates(blueprint, served).filter { it.needKey in open }
    }
}

/** Skill state as need generation reads it; the planner and the weekly composer read it the same way. */
internal object PlanningStates {

    fun read(persistence: PersistencePort, skills: List<SkillRow>): List<SkillPlanningState> = skills.map { skill ->
        val row = persistence.readProjection(ResolvePrerequisites.skillStateKey(skill.ref))
        val axes = row?.payload.orEmpty()
        SkillPlanningState(
            skill = skill.ref,
            lifecycleStatus = skill.lifecycleStatus,
            critical = skill.criticalPrerequisite,
            mastery = MasteryAxisState.entries.firstOrNull { it.id == axes["mastery_axis_state"] },
            retention = RetentionAxis.of(axes["retention_axis_state"]),
            weaknessAxis = axes["weakness_axis_state"]?.takeUnless { it.isEmpty() || it == ResolvePrerequisites.NOT_YET_EVALUATED },
            snapshotRef = row?.let { "${it.key}#watermark=${it.truthWatermark}" },
        )
    }
}

/**
 * Recording what one weekly slot's attempt proved (13A). The interpretation is the evidence pipeline's
 * (`RecordEvidence`, 12A); this only supplies the one fact a weekly session knows and a single attempt
 * does not: whether a Skill this item needs has **just** been shown missing in this same session
 * (`WBA-v0` §25). If so the snapshot says contaminated and the target is not blamed for it.
 */
class RecordWeeklySlotEvidence(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun record(
        blueprint: WeeklyAssessmentBlueprint,
        slotId: String,
        attemptId: Long,
        evaluation: EvaluationResult,
        independence: IndependenceClass,
        sessionSoFar: List<WeeklySlotOutcome>,
        gateSnapshot: String? = null,
        artifactId: Long? = null,
    ): RecordEvidence.Recorded {
        val slot = blueprint.readySlots.singleOrNull { it.slotId == slotId }
        requireNotNull(slot) { "slot $slotId is not a ready slot of this blueprint" }
        val failed = WeeklyBlueprintEngine.cleanlyFailedSkills(blueprint, sessionSoFar)
        val snapshot = if (WeeklyBlueprintEngine.contaminatedBy(slot, failed)) PrerequisiteSnapshot.CONTAMINATED else gateSnapshot
        return RecordEvidence(persistence, clock).record(
            attemptId = attemptId,
            skill = slot.targetSkill,
            evidenceType = slot.evidenceType!!,
            evaluation = evaluation,
            independence = independence,
            artifactId = artifactId,
            resource = slot.item,
            variantFamilyId = slot.variantFamilyId,
            prerequisiteSnapshot = snapshot,
        )
    }
}
