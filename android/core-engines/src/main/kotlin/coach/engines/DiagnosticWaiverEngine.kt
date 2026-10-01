package coach.engines

import coach.model.AssessmentIntent
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.CoverageWaiver
import coach.model.Criticality
import coach.model.DecisionValue
import coach.model.DiagnosticCodes
import coach.model.DiagnosticObjectiveState
import coach.model.DiagnosticScope
import coach.model.DiagnosticStage
import coach.model.DiagnosticTarget
import coach.model.DifficultyClass
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.ItemFit
import coach.model.ItemSelection
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedTrigger
import coach.model.ObjectiveDiagnosis
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveGateProfile
import coach.model.SkillPlanningState
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.UseCeiling
import coach.model.VersionedRef
import coach.model.WaiverOutcome

/**
 * The validated diagnostic waiver (13F, `VDW-v0 / D-037`): **which starting lessons can this learner skip,
 * because they already showed — independently, under the full mastery gates — that they can do the Objective?**
 *
 * It owns no gate of its own. Whether an Objective's gates pass is the mastery engine's question, asked with the
 * same profile and the same evidence rules; a diagnostic only gathers that evidence sooner. Nothing here lowers a
 * threshold, counts a self-report, or lets one easy item stand for a Topic. A waiver is coverage, not competence:
 * it never becomes the Skill's mastery or retention, and it is never shown as either.
 *
 * Everything is a pure function of its arguments.
 */
object DiagnosticWaiverEngine {

    const val MODEL = "VDWX-v0"
    const val POLICY_VERSION = "VDW-v0"

    /** Working text for the task; the words are 14's. */
    const val TITLE = "Hızlı ilerleme kontrolü"

    // ------------------------------------------------------------------------------------ the waiver

    /**
     * `VDW-v0` §9: an Objective's starting lesson is waived when its gates **first** pass on evidence gathered
     * inside a diagnostic. [diagnosticSession] answers, for one row, the diagnostic it was gathered in — `null`
     * when it was not gathered in one whose scope holds this Objective.
     *
     * The gates are `GRE-v0`'s own, read with nothing held over: no hysteresis and no open re-check. If they first
     * passed on ordinary learning, the Objective was covered by learning and there is nothing to waive. The waiver
     * names the evidence of the window that passed, so it can always be traced — and when a correction takes that
     * evidence away, the same replay no longer grants it.
     */
    fun waiver(
        profile: ObjectiveGateProfile,
        skill: VersionedRef,
        rows: List<EvidenceRow>,
        diagnosticSession: (EvidenceRow) -> Long?,
    ): CoverageWaiver? {
        val ordered = rows.sortedBy { it.sequence }
        // Once a diagnostic's fast path has ended for this Objective — help taken, or a clean miss — what follows in
        // that session is no longer the diagnostic's evidence (13F, user decision): it cannot waive the lesson.
        val ended = mutableSetOf<Long>()
        val sessionOf = ordered.associate { row ->
            val session = diagnosticSession(row)?.takeIf { it !in ended }
            if (session != null && endsFastPath(row, profile)) ended += session
            row.id to session
        }
        // Nothing inside a diagnostic, nothing to waive; the replay is only paid for Objectives a diagnostic touched.
        val last = ordered.indexOfLast { sessionOf[it.id] != null }
        if (last < 0) return null
        for (i in 0..last) {
            val prefix = ordered.subList(0, i + 1)
            if (!gatesPass(profile, prefix)) continue
            val row = ordered[i]
            val session = sessionOf[row.id] ?: return null
            val day = requireNotNull(row.studyDay) { "an evidence row with no study day cannot be placed in time" }
            val sources = window(profile, prefix).flatMap { group -> group.rows.map { it.id } }.sorted()
            return CoverageWaiver(profile.ref, skill, session, sources, row.sequence, day)
        }
        return null
    }

    /** `GRE-v0`'s gates as the mastery engine decides them, with nothing held over from before. */
    fun gatesPass(profile: ObjectiveGateProfile, rows: List<EvidenceRow>): Boolean {
        val decision = MasteryEngine.decide(profile, rows)
        return decision.failedGates.isEmpty() && !decision.unresolvedRecheck
    }

    private fun window(profile: ObjectiveGateProfile, rows: List<EvidenceRow>): List<MasteryEngine.EvidenceGroup> =
        MasteryEngine.recentWindow(MasteryEngine.groupsOf(rows.filter { MasteryEngine.exclusions(it, profile).isEmpty() }))

    // ------------------------------------------------------------------------------------ one Objective

    /**
     * Where one Objective of the active diagnostic [sessionId] stands (`VDW-v0` §6, §12, §14, §17).
     *
     * The learner's own answers inside this diagnostic are read in the order given, and the first that ends the
     * fast path decides: help taken or a solution seen ends it (13F, user decision) — never as a penalty; a clean,
     * independent, verified answer that did not show the Objective ends it too, and that is no remediation
     * (`VDW-v0` §12.1). An answer built on a missing prerequisite, an unmeasurable one and a contested one say
     * nothing about the learner and are passed over.
     *
     * Otherwise an Objective whose gates already pass is not tested again, and an open one is routed: a probe when
     * nothing usable is known yet, a confirm for exactly the gates still failing.
     */
    fun diagnose(
        target: DiagnosticTarget,
        profile: ObjectiveGateProfile,
        rows: List<EvidenceRow>,
        sessionId: Long,
        waiver: CoverageWaiver?,
    ): ObjectiveDiagnosis {
        require(profile.ref == target.objective) { "the profile is the target's own" }
        val ordered = rows.sortedBy { it.sequence }
        val decision = MasteryEngine.decide(profile, ordered)
        val window = window(profile, ordered)
        fun result(state: DiagnosticObjectiveState, stage: DiagnosticStage?, codes: List<String>) = ObjectiveDiagnosis(
            target = target,
            state = state,
            stage = stage,
            failedGates = decision.failedGates,
            windowVariantFamilies = window.mapNotNull { it.variantFamilyId }.distinct().sorted(),
            windowDependencyGroups = window.flatMap { g -> g.rows.mapNotNull { it.dependencyGroupId } }.distinct().sorted(),
            reasonCodes = codes,
            waiver = if (state == DiagnosticObjectiveState.WAIVED) waiver else null,
        )
        if (waiver != null) return result(DiagnosticObjectiveState.WAIVED, null, emptyList())

        val inThisDiagnostic = ordered.filter { it.assessmentSessionId == sessionId }
        inThisDiagnostic.firstOrNull { endsFastPath(it, profile) }?.let { row ->
            return if (assisted(row)) result(DiagnosticObjectiveState.ASSISTANCE_ENDED_FAST_PATH, null, listOf(DiagnosticCodes.H0_REQUIRED_FOR_WAIVER))
            else result(DiagnosticObjectiveState.NOT_DEMONSTRATED, null, listOf(DiagnosticCodes.NO_WAIVER))
        }

        if (gatesPass(profile, ordered)) return result(DiagnosticObjectiveState.ALREADY_DEMONSTRATED, null, emptyList())

        // A provisional or indirect answer is not nothing: the Objective was probed, and only a verified check of
        // the right kind can settle it.
        val stage = when {
            window.isEmpty() && inThisDiagnostic.isEmpty() -> DiagnosticStage.PROBE
            profile.critical -> DiagnosticStage.CRITICAL_CONFIRM
            "transfer_evidence" in decision.failedGates -> DiagnosticStage.TRANSFER_CONFIRM
            else -> DiagnosticStage.CONFIRM
        }
        val state = if (stage == DiagnosticStage.PROBE) DiagnosticObjectiveState.PROBE_NEEDED else DiagnosticObjectiveState.CONFIRM_NEEDED
        return result(state, stage, listOf(stage.reasonCode))
    }

    /**
     * Whether one diagnostic answer ends the fast path for its Objective: help taken or a solution seen (13F, user
     * decision), or a clean, independent, verified, direct miss (`VDW-v0` §12.1). An answer built on a missing
     * prerequisite, an unmeasurable one and a contested one say nothing about the learner and end nothing.
     */
    fun endsFastPath(row: EvidenceRow, profile: ObjectiveGateProfile): Boolean {
        // Work done on a prerequisite that was not there is not the learner's (`VDW-v0` §13).
        if (!row.prerequisiteValid) return false
        if (assisted(row)) return true
        if (row.outcome == EvidenceOutcome.INVALID || row.evaluatorStatus == EvaluatorStatus.INVALID || row.contested) return false
        val clean = row.evaluatorStatus == EvaluatorStatus.VERIFIED && row.evidenceType in profile.directEvidenceTypes
        return clean && (row.outcome == EvidenceOutcome.NEGATIVE || row.outcome == EvidenceOutcome.PARTIAL)
    }

    private fun assisted(row: EvidenceRow) = row.independenceClass != IndependenceClass.INDEPENDENT || row.solutionExposed

    // ------------------------------------------------------------------------------------ the outcome

    /**
     * `VDW-v0` §10–§11. Full only when every Objective in scope is covered — waived, or already shown — at least
     * one was waived, and every Skill in scope is mastered by the mastery engine; diagnostic success alone never
     * makes a Topic or Skill mastered. Some waived is partial; none is no waiver, which is not a penalty.
     */
    fun outcome(diagnoses: List<ObjectiveDiagnosis>, masteredSkills: Set<VersionedRef>): WaiverOutcome {
        if (diagnoses.none { it.state == DiagnosticObjectiveState.WAIVED }) return WaiverOutcome.NONE
        val covered = diagnoses.all {
            it.state == DiagnosticObjectiveState.WAIVED || it.state == DiagnosticObjectiveState.ALREADY_DEMONSTRATED
        }
        return if (covered && diagnoses.map { it.target.skill }.all { it in masteredSkills }) WaiverOutcome.FULL else WaiverOutcome.PARTIAL
    }

    /**
     * The result's reasons (`PDT-v0` §8.7): the learner's request, then the coverage outcome — "no waiver" only once
     * nothing is still open, so nothing is called not shown before it was checked.
     */
    fun resultReasons(scope: DiagnosticScope, outcome: WaiverOutcome, inProgress: Boolean): List<String> = buildList {
        add(scope.source.reasonCode)
        if (outcome != WaiverOutcome.NONE || !inProgress) add(outcome.reasonCode)
    }

    // ------------------------------------------------------------------------------------ the planner's needs

    /**
     * One `diagnostic_opportunity` per Skill of the scope that still has an open Objective (3B §2.1). It is planned
     * progress (`PBR-v0` P3) and never bypasses repair or verification; the learner asked for it, so it is decisive
     * within its band (`VDW-v0` §19). A Skill off the route opens nothing.
     */
    fun needs(sessionId: Long, scope: DiagnosticScope, diagnoses: List<ObjectiveDiagnosis>, states: List<SkillPlanningState>): List<LearningNeed> {
        val byskill = states.associateBy { it.skill }
        return scope.skills.mapNotNull { skill ->
            if (diagnoses.none { it.target.skill == skill && it.state.open }) return@mapNotNull null
            val state = byskill[skill] ?: return@mapNotNull null
            if (!PlannerEngine.onRoute(state.lifecycleStatus)) return@mapNotNull null
            LearningNeed(
                needKey = needKey(skill),
                trigger = NeedTrigger.DIAGNOSTIC_OPPORTUNITY,
                targetSkills = listOf(skill),
                criticality = if (state.critical) Criticality.CRITICAL_PREREQUISITE else Criticality.REQUIRED,
                sourceStateRefs = listOf("assessment_session:$sessionId"),
                decisionValue = DecisionValue.DECISIVE,
            )
        }.sortedBy { it.needKey }
    }

    fun needKey(skill: VersionedRef) = "${NeedTrigger.DIAGNOSTIC_OPPORTUNITY.id}:$skill"

    // ------------------------------------------------------------------------------------ routing

    /**
     * `VDW-v0` §5–§7, §14–§15: the next item for one open Objective, or `null` when nothing trusted can measure it
     * — which is not the learner's failure, and the Objective stays open.
     *
     * The item must target the Objective, be eligible for a daily measurement and require H0; the store's validation
     * record must trust it far enough to carry evidence the Objective takes as direct at the strength its criticality
     * needs (an unvalidated item is practice at most, `AIV-v0`); it must declare its minutes and be fresh: never seen, never a family whose solution was shown, never [used] by another Objective in this
     * plan. A confirm asks only for what the gates still miss: a new independent group, a new variant family, the
     * required direct type, non-basic or transfer evidence. Same-family repetition can never inflate diversity.
     */
    fun route(
        diagnosis: ObjectiveDiagnosis,
        profile: ObjectiveGateProfile,
        available: List<AssessmentItem>,
        profiles: Map<VersionedRef, ObjectiveEvidenceProfile>,
        exposures: List<ExposureFact>,
        evaluatorAvailable: Boolean,
        used: Set<VersionedRef> = emptySet(),
    ): AssessmentItem? {
        if (!diagnosis.state.open) return null
        val needed = if (profile.critical) UseCeiling.CRITICAL_MASTERY_ELIGIBLE else UseCeiling.STANDARD_MASTERY_ELIGIBLE
        val seen = exposures.map { it.resource }.toSet()
        val exposedFamilies = exposures.filter { it.solutionExposed }.mapNotNull { it.variantFamilyId }.toSet()
        val failed = diagnosis.failedGates.toSet()
        val confirming = diagnosis.stage != DiagnosticStage.PROBE
        return available
            .filter { item ->
                diagnosis.target.objective in item.targetObjectives &&
                    AssessmentScope.DAILY_MICRO in item.scopeEligibility &&
                    item.independenceMode == IndependenceMode.H0_REQUIRED &&
                    item.expectedActiveMinutes != null &&
                    item.ref !in used && item.ref !in seen && item.variantFamilyId !in exposedFamilies &&
                    when (val fit = ItemSelection.fit(item, AssessmentIntent.MASTERY_EVIDENCE, AssessmentScope.DAILY_MICRO, profiles, evaluatorAvailable)) {
                        is ItemFit.Usable -> fit.ceiling.permits(needed)
                        is ItemFit.NotUsable -> false
                    } &&
                    (!confirming || item.dependencyGroupId == null || item.dependencyGroupId !in diagnosis.windowDependencyGroups) &&
                    ("min_variant_families" !in failed || item.variantFamilyId !in diagnosis.windowVariantFamilies) &&
                    ("required_direct_type" !in failed || item.evidenceType == profile.requiredDirectType) &&
                    ("non_basic_evidence" !in failed || item.difficultyClass != DifficultyClass.BASIC.id) &&
                    ("transfer_evidence" !in failed || item.difficultyClass == DifficultyClass.TRANSFER_INTEGRATION.id)
            }
            .sortedWith(itemOrder)
            .firstOrNull()
    }

    /** `QAB-v0` §32's order: better-validated first, then a deterministic check, then a stable tie-break. */
    private val itemOrder: Comparator<AssessmentItem> = compareBy<AssessmentItem>(
        { when (it.lifecycleStatus) { LifecycleStatus.TRUSTED -> 0; LifecycleStatus.VALIDATED -> 1; else -> 2 } },
        { if (it.deterministicVerification) 0 else 1 },
        { it.ref.toString() },
    )

    /**
     * The routed item as the planner's candidate for the Skill's diagnostic need. It is an atomic evidence boundary,
     * declares the one Objective it is checking, and takes exactly the minutes the item declares.
     */
    fun candidate(sessionId: Long, diagnosis: ObjectiveDiagnosis, item: AssessmentItem): TaskCandidate = TaskCandidate(
        id = "diagnostic:$sessionId:${diagnosis.target.objective}:${item.ref}",
        needKey = needKey(diagnosis.target.skill),
        purpose = TaskPurpose.DIAGNOSE,
        activityKind = BlueprintComposer.ACTIVITY,
        title = TITLE,
        primarySkill = diagnosis.target.skill,
        costMinutes = item.expectedActiveMinutes!!,
        validationStatus = item.lifecycleStatus,
        requiredSkills = item.requiredSkills,
        atomicEvidenceBoundary = true,
        generationVersion = "diagnostic_scope/1:$sessionId",
        targetObjectives = listOf(diagnosis.target.objective),
    )
}
