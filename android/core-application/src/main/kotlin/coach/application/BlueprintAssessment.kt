package coach.application

import coach.engines.BlueprintComposer
import coach.engines.MonthlyBlueprintEngine
import coach.engines.WeaknessEngine
import coach.engines.WeeklyBlueprintEngine
import coach.model.AssessmentBlueprint
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
import coach.model.BlueprintCodecs
import coach.model.BlueprintScopes
import coach.model.BlueprintSlotOutcome
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.StoredTruth
import coach.ports.TruthRecord

/**
 * The weekly (13A) and monthly (13B) assessment: composing a cycle's blueprint, offering its slots to the
 * planner, and recording what a slot proved. One use case serves both scopes because `MCA-v0` §4 makes
 * monthly a policy extension of `WBA-v0` §28's contract, not a second architecture; the scope decides the
 * cycle, the target pool and the stored format, never what a slot or a result means.
 *
 * None of this is an authority. Composition decides what is worth measuring this cycle and with which
 * trusted item; **the planner** decides which slot runs on which day, inside the day's real capacity; the
 * **evidence pipeline** and the engines decide what an answer proved. A week or a month is an identity,
 * not a deadline: a cycle that passed without its assessment leaves nothing behind.
 */
class ComposeAssessmentBlueprint(
    private val scope: AssessmentScope,
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
) {
    init {
        require(scope in BlueprintScopes.COMPOSED) { "only weekly and monthly compose a blueprint" }
    }

    sealed interface Composed {
        /** No curriculum is published; there is nothing to measure against and nothing is written. */
        data object NothingPublished : Composed

        /** This cycle already has its blueprint; nothing is written and it is returned as stored. */
        data class AlreadyComposed(val sessionId: Long, val blueprint: AssessmentBlueprint) : Composed

        /**
         * Nothing is worth measuring this cycle, or nothing trusted can measure it. That is not an exam
         * the learner missed: nothing is written, so a later composition this cycle can still find one.
         */
        data class NothingToMeasure(val blueprint: AssessmentBlueprint) : Composed

        /** The last blueprint could not be read; nothing is guessed and nothing is written. */
        data class Refused(val reason: String) : Composed

        /** A blueprint was composed and appended as one new `assessment_session` row. */
        data class Written(val sessionId: Long, val blueprint: AssessmentBlueprint) : Composed
    }

    /**
     * Composes this cycle's blueprint once. [ownerNeeds] are the needs only their owners open — the
     * parallel track's cadence, integration opportunities — exactly as the planner receives them.
     */
    fun compose(evaluatorAvailable: Boolean, ownerNeeds: List<LearningNeed> = emptyList()): Composed {
        // Read first, like every projection: the blueprint can never claim more truth than it saw.
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion() ?: return Composed.NothingPublished
        val now = clock.now()
        val cycle = BlueprintScopes.cycleOf(scope, now.studyDay)

        val previous = persistence.latestAssessmentSession(scope)
        val previousBlueprint = previous?.let(::decode)
        if (previous != null && previousBlueprint == null) return Composed.Refused("the last ${scope.storedAs} blueprint cannot be read")
        if (previousBlueprint != null && previousBlueprint.cycleId == cycle) {
            return Composed.AlreadyComposed(previous!!.id, previousBlueprint)
        }

        val states = PlanningStates.read(persistence, persistence.publishedSkills())
        // "Since the last cycle" (`WBA-v0` §8, `MCA-v0` §4's longitudinal window): from the day the previous
        // blueprint of this scope was composed. With none there is no "since", and every Skill in progress is
        // inside the window.
        val recentSince = previousBlueprint?.studyDay
        val recent = recentSince?.let { persistence.skillsEvidencedSince(it).toSet() }
        // The weakness owner's `weakness_detected` needs reach the pool like any other owner's (13D).
        val owners = (ownerNeeds + WeaknessEngine.needs(states)).distinctBy { it.needKey }
        val pool = when (scope) {
            AssessmentScope.MONTHLY_CAPABILITY -> MonthlyBlueprintEngine.targetPool(states, owners, recent, holdingBack())
            else -> WeeklyBlueprintEngine.targetPool(states, owners, recent, holdingBack())
        }
        val inputs = gather(pool.entries.map { it.skill })

        val blueprint = when (scope) {
            // The previous month's session is a reference (`MCA-v0` §4 `prior_monthly_result_ref`), never a debt.
            AssessmentScope.MONTHLY_CAPABILITY -> MonthlyBlueprintEngine.compose(
                cycleId = cycle, studyDay = now.studyDay, curriculumVersion = curriculumVersion, truthWatermark = watermark,
                pool = pool, items = inputs.items, profiles = inputs.profiles, decisions = inputs.decisions,
                exposures = inputs.exposures, evaluatorAvailable = evaluatorAvailable, recentSince = recentSince,
                previousCycleId = previousBlueprint?.cycleId, priorSessionId = previous?.id,
            )
            else -> WeeklyBlueprintEngine.compose(
                cycleId = cycle, studyDay = now.studyDay, curriculumVersion = curriculumVersion, truthWatermark = watermark,
                pool = pool, items = inputs.items, profiles = inputs.profiles, decisions = inputs.decisions,
                exposures = inputs.exposures, evaluatorAvailable = evaluatorAvailable, recentSince = recentSince,
                previousCycleId = previousBlueprint?.cycleId,
            )
        }
        if (blueprint.readySlots.isEmpty()) return Composed.NothingToMeasure(blueprint)
        return Composed.Written(append(blueprint), blueprint)
    }

    /**
     * Recomposes unresolved slots of this cycle's blueprint (`WBA-v0` §17, `MCA-v0` §16, `ASUX-v0` §9). The caller —
     * the session, which knows which of the five conditions happened — names the slots; submitted slots
     * are never among them. The recomposition is a **new** session row that names the one it supersedes;
     * the old row is not edited, and the attempts already made in it stay where they were made.
     */
    fun recompose(slotIds: Set<String>, evaluatorAvailable: Boolean): Composed {
        persistence.latestCurriculumVersion() ?: return Composed.NothingPublished
        val now = clock.now()
        val previous = persistence.latestAssessmentSession(scope)
            ?: return Composed.Refused("there is no ${scope.storedAs} blueprint to recompose")
        val blueprint = decode(previous) ?: return Composed.Refused("the last ${scope.storedAs} blueprint cannot be read")
        if (blueprint.cycleId != BlueprintScopes.cycleOf(scope, now.studyDay)) {
            return Composed.Refused("a past cycle is not recomposed; this cycle composes its own")
        }
        val unknown = slotIds - blueprint.slots.map { it.slotId }.toSet()
        if (unknown.isNotEmpty()) return Composed.Refused("slots $unknown are not in this cycle's blueprint")

        val targets = blueprint.slots.filter { it.slotId in slotIds }.map { it.targetSkill }
        val inputs = gather(targets)
        val recomposed = BlueprintComposer.recompose(
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

    private fun append(blueprint: AssessmentBlueprint): Long = persistence.inTransaction {
        persistence.appendTruth(
            TruthRecord(
                "assessment_session", clock.now(),
                mapOf(
                    "scope" to scope.storedAs,
                    "blueprint" to BlueprintCodecs.encode(blueprint),
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
    private fun withStoreTrust(item: AssessmentItem): AssessmentItem? = StoreTrust.apply(persistence, item)

    private fun decode(row: StoredTruth): AssessmentBlueprint? =
        row.record.payload["blueprint"]?.let { BlueprintCodecs.decode(scope, it) }
}

/**
 * This cycle's weekly and monthly slots as the planner's candidates (13A, 13B). Nothing here is a second
 * planner: the slots serve needs the planner already opened, and which of them fits today is `PBR-v0`'s and
 * D-033's. When a week and a month both hold a slot for the same need, both are offered as alternatives of
 * that one need, and the planner selects at most one task per need.
 */
internal object BlueprintSlots {

    fun candidates(persistence: PersistencePort, studyDay: String, openNeeds: List<LearningNeed>): List<TaskCandidate> =
        BlueprintScopes.COMPOSED.sortedBy { it.ordinal }.flatMap { candidates(persistence, it, studyDay, openNeeds) }

    private fun candidates(persistence: PersistencePort, scope: AssessmentScope, studyDay: String, openNeeds: List<LearningNeed>): List<TaskCandidate> {
        val row = persistence.latestAssessmentSession(scope) ?: return emptyList()
        val blueprint = row.record.payload["blueprint"]?.let { BlueprintCodecs.decode(scope, it) } ?: return emptyList()
        // Only this cycle's blueprint is ever offered; an earlier cycle's unfinished slots are not debt.
        if (blueprint.cycleId != BlueprintScopes.cycleOf(scope, studyDay)) return emptyList()
        val ready = blueprint.readySlots.mapNotNull { it.item }
        if (ready.isEmpty()) return emptyList()
        // Every item in a ready slot was unseen when the blueprint was composed, so any exposure now means
        // the slot has been served and is not offered again.
        val served = persistence.exposuresFor(ready, emptyList()).map { it.resource }.toSet()
        val open = openNeeds.map { it.needKey }.toSet()
        val offered = when (scope) {
            AssessmentScope.MONTHLY_CAPABILITY -> MonthlyBlueprintEngine.slotCandidates(blueprint, served)
            else -> WeeklyBlueprintEngine.slotCandidates(blueprint, served)
        }
        return offered.filter { it.needKey in open }
    }
}

/** Skill state as need generation reads it; the planner and the blueprint composers read it the same way. */
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
 * Recording what one weekly or monthly slot's attempt proved (13A, 13B). The interpretation is the evidence
 * pipeline's (`RecordEvidence`, 12A); this only supplies the one fact a blueprint session knows and a single
 * attempt does not: whether a Skill this item needs has **just** been shown missing in this same session
 * (`WBA-v0` §25, `MCA-v0` §20). If so the snapshot says contaminated and the target is not blamed for it.
 * A monthly slot's evidence is weighed exactly like any other (`MCA-v0` §2).
 */
class RecordSlotEvidence(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun record(
        blueprint: AssessmentBlueprint,
        slotId: String,
        attemptId: Long,
        evaluation: EvaluationResult,
        independence: IndependenceClass,
        sessionSoFar: List<BlueprintSlotOutcome>,
        gateSnapshot: String? = null,
        artifactId: Long? = null,
    ): RecordEvidence.Recorded {
        val slot = blueprint.readySlots.singleOrNull { it.slotId == slotId }
        requireNotNull(slot) { "slot $slotId is not a ready slot of this blueprint" }
        val failed = BlueprintComposer.cleanlyFailedSkills(blueprint, sessionSoFar)
        val snapshot = if (BlueprintComposer.contaminatedBy(slot, failed)) PrerequisiteSnapshot.CONTAMINATED else gateSnapshot
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
