package coach.engines

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.CandidateDisposition
import coach.model.ContentOrigin
import coach.model.CoverageHold
import coach.model.Criticality
import coach.model.DecisionValue
import coach.model.DiagnosticCodes
import coach.model.DiagnosticObjectiveState
import coach.model.DiagnosticScope
import coach.model.DiagnosticSource
import coach.model.DiagnosticStage
import coach.model.DiagnosticTarget
import coach.model.DifficultyClass
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatus
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.NeedDisposition
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveGateProfile
import coach.model.PriorityBand
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteEligibility
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.StarvationBucket
import coach.model.BlockingScope
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.UseCeiling
import coach.model.VersionedRef
import coach.model.WaiverOutcome
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The diagnostic waiver (13F, `VDW-v0`): the same gates as mastery, gathered sooner — never a lower bar. A waiver
 * is granted only where an Objective's gates first pass on diagnostic evidence; help, a clean miss, a missing
 * prerequisite and an unmeasurable answer are each told apart; and a lesson is held only for what was waived or is
 * still being checked.
 */
class DiagnosticWaiverEngineTest {

    private val skill = VersionedRef("skill.c.pointer_basics", 1)
    private val o1 = VersionedRef("objective.c.pointer_basics.address_vs_value", 1)
    private val o2 = VersionedRef("objective.c.pointer_basics.declaration", 1)
    private val session = 40L

    private fun profile(ref: VersionedRef = o1, critical: Boolean = false, transfer: Boolean = false, requiredDirect: String? = null) =
        ObjectiveGateProfile(ref, required = true, critical = critical, acceptableEvidenceTypes = listOf("code_reading", "coding", "recognition"),
            directEvidenceTypes = listOf("code_reading", "coding"), requiredDirectType = requiredDirect, requiresTransfer = transfer)

    private var next = 1L

    private fun row(
        family: String,
        outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        session: Long? = this.session,
        objective: VersionedRef = o1,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        evaluator: EvaluatorStatus = EvaluatorStatus.VERIFIED,
        type: String = "code_reading",
        difficulty: DifficultyClass? = DifficultyClass.BASIC,
        prerequisiteValid: Boolean = true,
        contested: Boolean = false,
        solutionExposed: Boolean = false,
        group: String? = null,
    ): EvidenceRow {
        val id = next++
        return EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = type, outcome = outcome,
            evaluatorStatus = evaluator, independenceClass = independence, contested = contested,
            quality = when (outcome) { EvidenceOutcome.POSITIVE -> 1.0; EvidenceOutcome.PARTIAL -> 0.5; else -> 0.0 },
            difficulty = difficulty, variantFamilyId = family, dependencyGroupId = group, resource = VersionedRef("item.$id", 1),
            prerequisiteValid = prerequisiteValid, solutionExposed = solutionExposed, studyDay = "2026-10-01", assessmentSessionId = session)
    }

    /** A row counts as diagnostic exactly when it was gathered in [session], as the store's cache answers it. */
    private val inDiagnostic: (EvidenceRow) -> Long? = { it.assessmentSessionId?.takeIf { id -> id == session } }

    private fun waiver(rows: List<EvidenceRow>, p: ObjectiveGateProfile = profile()) = DiagnosticWaiverEngine.waiver(p, skill, rows, inDiagnostic)

    // ------------------------------------------------------------------------------------ the waiver

    @Test
    fun `gates that first pass on diagnostic evidence waive the starting lesson and name the evidence`() {
        val rows = listOf(row("fam.a"), row("fam.b"))
        val w = assertNotNull(waiver(rows))
        assertEquals(session, w.sessionId)
        assertEquals(listOf(1L, 2L), w.sourceEvidenceIds)
        assertEquals(2L, w.grantedAtSequence)
        assertEquals("2026-10-01", w.grantedOnStudyDay)
        assertEquals("validated_prior_knowledge", w.reason)
    }

    @Test
    fun `gates that first pass on ordinary learning waive nothing, whatever a diagnostic shows later`() {
        assertNull(waiver(listOf(row("fam.a", session = null), row("fam.b", session = null), row("fam.c"))))
        assertNull(waiver(listOf(row("fam.a", session = null), row("fam.b", session = null))))
    }

    @Test
    fun `evidence the learner already had counts, and the diagnostic only gathers what is missing`() {
        val w = assertNotNull(waiver(listOf(row("fam.a", session = null), row("fam.b"))))
        assertEquals(listOf(1L, 2L), w.sourceEvidenceIds)
    }

    @Test
    fun `one item, or the same family twice, never waives anything`() {
        assertNull(waiver(listOf(row("fam.a"))))
        assertNull(waiver(listOf(row("fam.a"), row("fam.a"))))
        // A dependency group is one piece of evidence however many parts it has.
        assertNull(waiver(listOf(row("fam.a", group = "g1"), row("fam.b", group = "g1"))))
    }

    @Test
    fun `help, a seen solution, an unverified evaluation, a missing prerequisite or a contested item never count`() {
        listOf(
            row("fam.b", independence = IndependenceClass.ASSISTED),
            row("fam.b", solutionExposed = true),
            row("fam.b", evaluator = EvaluatorStatus.PROVISIONAL),
            row("fam.b", prerequisiteValid = false),
            row("fam.b", contested = true),
            row("fam.b", type = "recognition"),
        ).forEach { second -> assertNull(waiver(listOf(row("fam.a"), second)), second.toString()) }
    }

    @Test
    fun `a critical Objective keeps the critical gates`() {
        val critical = profile(critical = true)
        assertNull(waiver(listOf(row("fam.a"), row("fam.b"), row("fam.c")), critical))
        val w = assertNotNull(waiver(listOf(row("fam.a"), row("fam.b"), row("fam.c", difficulty = DifficultyClass.AUTHENTIC_APPLICATION)), critical))
        assertEquals(3, w.sourceEvidenceIds.size)
    }

    @Test
    fun `a waiver whose evidence was corrected away no longer stands`() {
        // The store reads the newest disposition into the row; an invalidated source is no longer evidence.
        assertNull(waiver(listOf(row("fam.a"), row("fam.b", evaluator = EvaluatorStatus.INVALID))))
    }

    @Test
    fun `nothing gathered after the fast path ended can waive the lesson`() {
        // Help taken, then two clean successes: the fast path had already ended for this Objective (13F, user decision).
        assertNull(waiver(listOf(row("fam.a", independence = IndependenceClass.ASSISTED, outcome = EvidenceOutcome.NEGATIVE), row("fam.b"), row("fam.c"))))
        assertNull(waiver(listOf(row("fam.a", solutionExposed = true), row("fam.b"), row("fam.c"))))
        // A clean miss, then two successes in the same diagnostic.
        assertNull(waiver(listOf(row("fam.a", outcome = EvidenceOutcome.NEGATIVE), row("fam.b"), row("fam.c"))))
        // A miss that says nothing about the learner ends nothing.
        assertNotNull(waiver(listOf(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, prerequisiteValid = false), row("fam.b"), row("fam.c"))))
        assertNotNull(waiver(listOf(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, contested = true), row("fam.b"), row("fam.c"))))
        // A new diagnostic is a new fast path; the earlier miss still stays in the gates' window, which is GRE-v0's
        // own rule, so it takes four successes to reach the same bar — the bar is never lowered.
        assertNull(waiver(listOf(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, session = 7), row("fam.b"), row("fam.c"))))
        assertEquals(session, waiver(listOf(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, session = 7), row("fam.b"), row("fam.c"),
            row("fam.d"), row("fam.e")))?.sessionId)
    }

    @Test
    fun `the fast path ends on help or a clean miss and on nothing else`() {
        val p = profile()
        assertTrue(DiagnosticWaiverEngine.endsFastPath(row("fam.a", independence = IndependenceClass.ASSISTED), p))
        assertTrue(DiagnosticWaiverEngine.endsFastPath(row("fam.a", independence = IndependenceClass.PRACTICE_ONLY), p))
        assertTrue(DiagnosticWaiverEngine.endsFastPath(row("fam.a", solutionExposed = true), p))
        assertTrue(DiagnosticWaiverEngine.endsFastPath(row("fam.a", outcome = EvidenceOutcome.NEGATIVE), p))
        assertTrue(DiagnosticWaiverEngine.endsFastPath(row("fam.a", outcome = EvidenceOutcome.PARTIAL), p))
        assertFalse(DiagnosticWaiverEngine.endsFastPath(row("fam.a"), p))
        assertFalse(DiagnosticWaiverEngine.endsFastPath(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, prerequisiteValid = false), p))
        assertFalse(DiagnosticWaiverEngine.endsFastPath(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, contested = true), p))
        assertFalse(DiagnosticWaiverEngine.endsFastPath(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, evaluator = EvaluatorStatus.PROVISIONAL), p))
        assertFalse(DiagnosticWaiverEngine.endsFastPath(row("fam.a", outcome = EvidenceOutcome.INVALID), p))
        assertFalse(DiagnosticWaiverEngine.endsFastPath(row("fam.a", outcome = EvidenceOutcome.NEGATIVE, type = "recognition"), p))
    }

    @Test
    fun `an independent re-check still owed holds the waiver until it is done`() {
        // A row from ordinary work that asked for an independent re-check: the gates do not count it, and while the
        // re-check is owed nothing passes — not even two clean diagnostic successes.
        val owed = row("fam.z", session = null, independence = IndependenceClass.REQUIRES_INDEPENDENT_RECHECK)
        assertNull(waiver(listOf(owed, row("fam.a"), row("fam.b"))))
        assertEquals(DiagnosticObjectiveState.CONFIRM_NEEDED, diagnose(listOf(owed, row("fam.a"), row("fam.b"))).state)
    }

    @Test
    fun `only a diagnostic whose scope holds the Objective can waive it`() {
        assertNull(DiagnosticWaiverEngine.waiver(profile(), skill, listOf(row("fam.a"), row("fam.b"))) { null })
    }

    // ------------------------------------------------------------------------------------ one Objective

    private val target = DiagnosticTarget(o1, skill, critical = false)

    private fun diagnose(rows: List<EvidenceRow>, p: ObjectiveGateProfile = profile(), t: DiagnosticTarget = target) =
        DiagnosticWaiverEngine.diagnose(t, p, rows, session, DiagnosticWaiverEngine.waiver(p, skill, rows, inDiagnostic))

    @Test
    fun `nothing known yet is a probe, one success is a confirm for exactly the missing gates`() {
        val probe = diagnose(emptyList())
        assertEquals(DiagnosticObjectiveState.PROBE_NEEDED, probe.state)
        assertEquals(DiagnosticStage.PROBE, probe.stage)
        assertEquals(listOf(DiagnosticCodes.PROBE_SELECTED), probe.reasonCodes)

        val confirm = diagnose(listOf(row("fam.a")))
        assertEquals(DiagnosticObjectiveState.CONFIRM_NEEDED, confirm.state)
        assertEquals(DiagnosticStage.CONFIRM, confirm.stage)
        assertTrue("min_independent_groups" in confirm.failedGates && "min_variant_families" in confirm.failedGates, confirm.failedGates.toString())
        assertEquals(listOf("fam.a"), confirm.windowVariantFamilies)
        assertEquals(listOf(DiagnosticCodes.CONFIRM_NEEDED), confirm.reasonCodes)
    }

    @Test
    fun `a critical Objective is confirmed as critical, and a missing transfer as transfer`() {
        val critical = diagnose(listOf(row("fam.a")), profile(critical = true), target.copy(critical = true))
        assertEquals(DiagnosticStage.CRITICAL_CONFIRM, critical.stage)
        assertEquals(listOf(DiagnosticCodes.CRITICAL_CONFIRM_NEEDED), critical.reasonCodes)
        val transfer = diagnose(listOf(row("fam.a"), row("fam.b")), profile(transfer = true))
        assertEquals(DiagnosticStage.TRANSFER_CONFIRM, transfer.stage)
    }

    @Test
    fun `a probe that could not be settled is not a fresh probe`() {
        val provisional = diagnose(listOf(row("fam.a", evaluator = EvaluatorStatus.PROVISIONAL)))
        assertEquals(DiagnosticObjectiveState.CONFIRM_NEEDED, provisional.state)
        assertEquals(DiagnosticStage.CONFIRM, provisional.stage)
    }

    @Test
    fun `gates passed on diagnostic evidence are waived`() {
        val waived = diagnose(listOf(row("fam.a"), row("fam.b")))
        assertEquals(DiagnosticObjectiveState.WAIVED, waived.state)
        assertNotNull(waived.waiver)
        assertNull(waived.stage)
    }

    @Test
    fun `an Objective already shown is not tested again`() {
        val shown = diagnose(listOf(row("fam.a", session = null), row("fam.b", session = null)))
        assertEquals(DiagnosticObjectiveState.ALREADY_DEMONSTRATED, shown.state)
        assertNull(shown.waiver)
    }

    @Test
    fun `help taken ends the fast path for the Objective, never as a penalty`() {
        listOf(row("fam.a", independence = IndependenceClass.ASSISTED, outcome = EvidenceOutcome.NEGATIVE),
            row("fam.a", solutionExposed = true)).forEach { assisted ->
            val d = diagnose(listOf(assisted, row("fam.b"), row("fam.c")))
            assertEquals(DiagnosticObjectiveState.ASSISTANCE_ENDED_FAST_PATH, d.state, assisted.toString())
            assertEquals(listOf(DiagnosticCodes.H0_REQUIRED_FOR_WAIVER), d.reasonCodes)
        }
    }

    @Test
    fun `a clean miss in this diagnostic ends the fast path, it is no remediation`() {
        listOf(EvidenceOutcome.NEGATIVE, EvidenceOutcome.PARTIAL).forEach { outcome ->
            val d = diagnose(listOf(row("fam.a", outcome = outcome)))
            assertEquals(DiagnosticObjectiveState.NOT_DEMONSTRATED, d.state)
            assertEquals(listOf(DiagnosticCodes.NO_WAIVER), d.reasonCodes)
        }
    }

    @Test
    fun `a miss that says nothing about the learner does not end the fast path`() {
        listOf(
            row("fam.a", outcome = EvidenceOutcome.NEGATIVE, prerequisiteValid = false),
            row("fam.a", outcome = EvidenceOutcome.NEGATIVE, contested = true),
            row("fam.a", outcome = EvidenceOutcome.INVALID),
            row("fam.a", outcome = EvidenceOutcome.NEGATIVE, evaluator = EvaluatorStatus.PROVISIONAL),
            row("fam.a", outcome = EvidenceOutcome.NEGATIVE, type = "recognition"),
            // A miss during ordinary learning is not this diagnostic's answer.
            row("fam.a", outcome = EvidenceOutcome.NEGATIVE, session = null),
            row("fam.a", outcome = EvidenceOutcome.NEGATIVE, session = 99),
        ).forEach { miss -> assertTrue(diagnose(listOf(miss)).state.open, miss.toString()) }
    }

    @Test
    fun `work on a missing prerequisite is passed over even when help was taken`() {
        val d = diagnose(listOf(row("fam.a", independence = IndependenceClass.ASSISTED, prerequisiteValid = false)))
        assertTrue(d.state.open)
    }

    // ------------------------------------------------------------------------------------ the outcome

    private fun diagnosisOf(state: DiagnosticObjectiveState, objective: VersionedRef = o1): coach.model.ObjectiveDiagnosis {
        val waiver = if (state == DiagnosticObjectiveState.WAIVED) coach.model.CoverageWaiver(objective, skill, session, listOf(1), 1, "2026-10-01") else null
        return coach.model.ObjectiveDiagnosis(DiagnosticTarget(objective, skill, false), state,
            if (state.open) DiagnosticStage.CONFIRM else null, emptyList(), emptyList(), emptyList(), emptyList(), waiver)
    }

    @Test
    fun `full needs every Objective covered and every Skill mastered, some waived is partial, none is no waiver`() {
        val waived = diagnosisOf(DiagnosticObjectiveState.WAIVED)
        val shown = diagnosisOf(DiagnosticObjectiveState.ALREADY_DEMONSTRATED, o2)
        val missed = diagnosisOf(DiagnosticObjectiveState.NOT_DEMONSTRATED, o2)
        assertEquals(WaiverOutcome.FULL, DiagnosticWaiverEngine.outcome(listOf(waived, shown), setOf(skill)))
        // Diagnostic success alone never makes a Skill mastered (`VDW-v0` §11).
        assertEquals(WaiverOutcome.PARTIAL, DiagnosticWaiverEngine.outcome(listOf(waived, shown), emptySet()))
        assertEquals(WaiverOutcome.PARTIAL, DiagnosticWaiverEngine.outcome(listOf(waived, missed), setOf(skill)))
        assertEquals(WaiverOutcome.NONE, DiagnosticWaiverEngine.outcome(listOf(missed, shown), setOf(skill)))
    }

    @Test
    fun `nothing is called not waived before it was checked`() {
        val scope = DiagnosticScope(DiagnosticSource.USER_REQUESTED_FAST_PATH, "2026-10-01", 1, listOf(target))
        assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH), DiagnosticWaiverEngine.resultReasons(scope, WaiverOutcome.NONE, inProgress = true))
        assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH, DiagnosticCodes.NO_WAIVER),
            DiagnosticWaiverEngine.resultReasons(scope, WaiverOutcome.NONE, inProgress = false))
        assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH, DiagnosticCodes.PARTIAL_COVERAGE_WAIVER),
            DiagnosticWaiverEngine.resultReasons(scope, WaiverOutcome.PARTIAL, inProgress = true))
    }

    // ------------------------------------------------------------------------------------ needs

    private fun state(lifecycle: String = "published", critical: Boolean = false) =
        SkillPlanningState(skill, lifecycle, critical, MasteryAxisState.NOT_YET_EVIDENCED, RetentionAxis.NOT_YET_EVALUATED, null)

    @Test
    fun `an open Objective opens one decisive planned-progress need for its Skill`() {
        val scope = DiagnosticScope(DiagnosticSource.USER_REQUESTED_FAST_PATH, "2026-10-01", 1,
            listOf(target, DiagnosticTarget(o2, skill, false)))
        val needs = DiagnosticWaiverEngine.needs(session, scope,
            listOf(diagnosisOf(DiagnosticObjectiveState.WAIVED), diagnosisOf(DiagnosticObjectiveState.PROBE_NEEDED, o2)), listOf(state(critical = true)))
        val need = needs.single()
        assertEquals("diagnostic_opportunity:$skill", need.needKey)
        assertEquals(NeedTrigger.DIAGNOSTIC_OPPORTUNITY, need.trigger)
        assertEquals(DecisionValue.DECISIVE, need.decisionValue)
        assertEquals(Criticality.CRITICAL_PREREQUISITE, need.criticality)
        assertEquals(listOf("assessment_session:$session"), need.sourceStateRefs)
        // Planned progress: it never passes repair or verification, even for a critical Skill.
        assertEquals(PriorityBand.P3, PlannerEngine.band(need, BlockingScope.NON_BLOCKING, StarvationBucket.NONE))
    }

    @Test
    fun `nothing open, nothing published or a Skill off the route opens nothing`() {
        val scope = DiagnosticScope(DiagnosticSource.USER_REQUESTED_FAST_PATH, "2026-10-01", 1, listOf(target))
        val open = listOf(diagnosisOf(DiagnosticObjectiveState.PROBE_NEEDED))
        assertTrue(DiagnosticWaiverEngine.needs(session, scope, listOf(diagnosisOf(DiagnosticObjectiveState.NOT_DEMONSTRATED)), listOf(state())).isEmpty())
        assertTrue(DiagnosticWaiverEngine.needs(session, scope, open, emptyList()).isEmpty())
        assertTrue(DiagnosticWaiverEngine.needs(session, scope, open, listOf(state("retired"))).isEmpty())
    }

    // ------------------------------------------------------------------------------------ routing

    private fun item(
        name: String,
        family: String = "fam.$name",
        objective: VersionedRef = o1,
        lifecycle: LifecycleStatus = LifecycleStatus.TRUSTED,
        mode: IndependenceMode = IndependenceMode.H0_REQUIRED,
        minutes: Int? = 6,
        deterministic: Boolean = true,
        type: String = "code_reading",
        difficulty: String = "basic",
        scopes: Set<AssessmentScope> = setOf(AssessmentScope.DAILY_MICRO),
        ceiling: UseCeiling = UseCeiling.CRITICAL_MASTERY_ELIGIBLE,
        group: String? = null,
    ) = AssessmentItem(
        ref = VersionedRef("item.c.pointer_basics.$name", 1), targetObjectives = listOf(objective), targetSkills = listOf(skill),
        requiredSkills = emptyList(), evidenceType = type, expectedAnswerOrRubricRef = "key.$name",
        evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, deterministicRequired = false, evaluatorPolicyVersion = "e1"),
        allowedTools = AllowedToolsPolicy(listOf("editor")), independenceMode = mode, difficultyClass = difficulty,
        lifecycleStatus = lifecycle, contentOrigin = ContentOrigin.HUMAN_AUTHORED, declaredUseCeiling = ceiling, scopeEligibility = scopes,
        variantFamilyId = family, dependencyGroupId = group, deterministicVerification = deterministic, expectedActiveMinutes = minutes,
    )

    private val profiles = listOf(o1, o2).associateWith {
        ObjectiveEvidenceProfile(it, listOf("code_reading", "coding", "recognition"), listOf("code_reading", "coding"))
    }

    private fun route(diagnosis: coach.model.ObjectiveDiagnosis, items: List<AssessmentItem>, p: ObjectiveGateProfile = profile(),
                      exposures: List<ExposureFact> = emptyList(), used: Set<VersionedRef> = emptySet(), evaluator: Boolean = false) =
        DiagnosticWaiverEngine.route(diagnosis, p, items, profiles, exposures, evaluator, used)

    @Test
    fun `a probe takes the best trusted, fresh, H0 daily item for the Objective`() {
        val probe = diagnose(emptyList())
        val chosen = route(probe, listOf(item("z", lifecycle = LifecycleStatus.VALIDATED), item("b"), item("a", deterministic = false)))
        // Trusted before validated, deterministic before an evaluator, then a stable order — never the shortest.
        assertEquals("item.c.pointer_basics.b", chosen?.ref?.logicalId)
    }

    @Test
    fun `an item that cannot carry waiver evidence is never routed`() {
        val probe = diagnose(emptyList())
        listOf(
            item("other", objective = o2),
            item("weekly", scopes = setOf(AssessmentScope.WEEKLY_BLUEPRINT)),
            item("candidate", lifecycle = LifecycleStatus.CANDIDATE),
            item("guided", mode = IndependenceMode.INDEPENDENT_EXPECTED),
            item("untimed", minutes = null),
            item("recognition", type = "recognition"),
            item("practice", ceiling = UseCeiling.PRACTICE_ONLY),
            // Without an evaluator, an item that needs one measures nothing.
            item("needs_evaluator", deterministic = false),
        ).forEach { assertNull(route(probe, listOf(it)), it.ref.toString()) }
    }

    @Test
    fun `a seen item, a family whose solution was shown, or an item already used is not fresh`() {
        val probe = diagnose(emptyList())
        val a = item("a")
        assertNull(route(probe, listOf(a), exposures = listOf(ExposureFact(a.ref, a.variantFamilyId, ExposureFact.ITEM_VERSION_SEEN))))
        assertNull(route(probe, listOf(a), exposures = listOf(ExposureFact(VersionedRef("item.other", 1), a.variantFamilyId, ExposureFact.SOLUTION_EXPOSURE))))
        assertNull(route(probe, listOf(a), used = setOf(a.ref)))
    }

    @Test
    fun `a confirm asks only for what the gates still miss`() {
        val first = row("fam.a", group = "g1")
        val confirm = diagnose(listOf(first))
        // Same family cannot raise family diversity; same dependency group is not a new group.
        assertNull(route(confirm, listOf(item("again", family = "fam.a"))))
        assertNull(route(confirm, listOf(item("part", family = "fam.z", group = "g1"))))
        assertEquals("item.c.pointer_basics.new", route(confirm, listOf(item("again", family = "fam.a"), item("new")))?.ref?.logicalId)

        val direct = profile(requiredDirect = "coding")
        val needsCoding = diagnose(listOf(first), direct)
        assertNull(route(needsCoding, listOf(item("reading")), direct))
        assertNotNull(route(needsCoding, listOf(item("coding", type = "coding")), direct))

        val critical = profile(critical = true)
        val criticalConfirm = diagnose(listOf(row("fam.b"), row("fam.c"), row("fam.d")), critical, target.copy(critical = true))
        assertTrue("non_basic_evidence" in criticalConfirm.failedGates, criticalConfirm.failedGates.toString())
        assertNull(route(criticalConfirm, listOf(item("basic")), critical))
        assertNotNull(route(criticalConfirm, listOf(item("applied", difficulty = "authentic_application")), critical))
        // A critical check needs an item trusted for critical mastery.
        assertNull(route(criticalConfirm, listOf(item("applied", difficulty = "authentic_application", ceiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE)), critical))

        val transfer = profile(transfer = true)
        val transferConfirm = diagnose(listOf(row("fam.b"), row("fam.c")), transfer)
        assertNull(route(transferConfirm, listOf(item("plain", difficulty = "authentic_application")), transfer))
        assertNotNull(route(transferConfirm, listOf(item("transfer", difficulty = "transfer_integration")), transfer))
    }

    @Test
    fun `a closed Objective is never routed`() {
        assertNull(route(diagnose(listOf(row("fam.a", outcome = EvidenceOutcome.NEGATIVE))), listOf(item("a"))))
    }

    @Test
    fun `the routed item is the diagnostic need's atomic, single-Objective candidate`() {
        val probe = diagnose(emptyList())
        val chosen = assertNotNull(route(probe, listOf(item("a"))))
        val candidate = DiagnosticWaiverEngine.candidate(session, probe, chosen)
        assertEquals("diagnostic_opportunity:$skill", candidate.needKey)
        assertEquals(TaskPurpose.DIAGNOSE, candidate.purpose)
        assertEquals(listOf(o1), candidate.targetObjectives)
        assertEquals(6, candidate.costMinutes)
        assertTrue(candidate.atomicEvidenceBoundary)
        assertEquals(LifecycleStatus.TRUSTED, candidate.validationStatus)
        assertTrue(candidate.id.startsWith("diagnostic:$session:"))
    }

    // ------------------------------------------------------------------------------------ the planner's holds

    private fun teach(id: String, objectives: List<VersionedRef>, purpose: TaskPurpose = TaskPurpose.TEACH, needKey: String = "continue_learning:$skill") =
        TaskCandidate(id, needKey, purpose, "lesson", "Ders $id", skill, 10, LifecycleStatus.VALIDATED, targetObjectives = objectives)

    private fun plan(candidates: List<TaskCandidate>, coverage: Map<VersionedRef, CoverageHold>, decisions: Map<String, PrerequisiteDecision>? = null) =
        PlannerEngine.plan(
            capacity = PlannerEngine.capacityOf(coach.model.CapacitySource.NORMAL_PROFILE, 60),
            needs = PlannerEngine.needsFromSkillStates(listOf(state().copy(mastery = MasteryAxisState.DEVELOPING_INDEPENDENT))),
            candidates = candidates,
            decisions = decisions ?: candidates.associate { it.id to decision(it.id, PrerequisiteEligibility.ELIGIBLE) },
            studyDay = "2026-10-01", curriculumVersion = 1, truthWatermark = 1, coverage = coverage,
        )

    private fun decision(id: String, eligibility: PrerequisiteEligibility, hard: List<VersionedRef> = emptyList()) =
        PrerequisiteDecision(id, skill, eligibility, hard, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
            false, PrerequisiteEngine.PREREQUISITE_POLICY_VERSION, emptyList(), emptyList())

    private val waived = CoverageHold(waived = true, reasonCode = DiagnosticCodes.PARTIAL_COVERAGE_WAIVER)
    private val checking = CoverageHold(waived = false, reasonCode = DiagnosticCodes.USER_REQUESTED_FAST_PATH)

    @Test
    fun `a lesson whose Objectives were all waived is not taught again`() {
        val trace = plan(listOf(teach("t1", listOf(o1))), mapOf(o1 to waived))
        val candidate = trace.candidates.single()
        assertEquals(CandidateDisposition.RESOLVED_BEFORE_SELECTION, candidate.disposition)
        assertEquals(listOf(DiagnosticCodes.PARTIAL_COVERAGE_WAIVER), candidate.reasonCodes)
        assertEquals(NeedDisposition.RESOLVED_BEFORE_SELECTION, trace.needs.single().disposition)
        assertTrue(trace.selected.isEmpty())
        val full = plan(listOf(teach("t1", listOf(o1))), mapOf(o1 to waived.copy(reasonCode = DiagnosticCodes.FULL_COVERAGE_WAIVER)))
        assertEquals(listOf(DiagnosticCodes.FULL_COVERAGE_WAIVER), full.candidates.single().reasonCodes)
    }

    @Test
    fun `a lesson the learner's fast path is still checking waits for it`() {
        val trace = plan(listOf(teach("t1", listOf(o1, o2))), mapOf(o1 to waived, o2 to checking))
        assertEquals(CandidateDisposition.CONDITIONAL_NOT_SELECTED, trace.candidates.single().disposition)
        assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH), trace.candidates.single().reasonCodes)
        assertEquals(NeedDisposition.NO_VALID_CANDIDATE, trace.needs.single().disposition)
        assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH), trace.needs.single().finalReasonCodes)
    }

    @Test
    fun `only what was shown is skipped, everything else is taught and practised as before`() {
        val trace = plan(listOf(
            teach("t1", listOf(o1)),
            teach("t2", listOf(o1, o2)),
            teach("t3", emptyList()),
            teach("p1", listOf(o1), purpose = TaskPurpose.PRACTICE, needKey = "x"),
        ), mapOf(o1 to waived))
        val byId = trace.candidates.associateBy { it.candidateId }
        assertEquals(CandidateDisposition.RESOLVED_BEFORE_SELECTION, byId.getValue("t1").disposition)
        // A lesson that also teaches an Objective not shown is still taught.
        assertTrue(trace.selected.any { it.candidateId == "t2" } || byId.getValue("t2").disposition != CandidateDisposition.RESOLVED_BEFORE_SELECTION)
        assertTrue(byId["t3"] == null || byId.getValue("t3").disposition != CandidateDisposition.RESOLVED_BEFORE_SELECTION)
        assertEquals("t2", trace.selected.single().candidateId)
    }

    @Test
    fun `a diagnostic that waits on a prerequisite says so`() {
        val diag = TaskCandidate("d1", "continue_learning:$skill", TaskPurpose.DIAGNOSE, "assessment_session", "Kontrol", skill, 6,
            LifecycleStatus.TRUSTED, atomicEvidenceBoundary = true, targetObjectives = listOf(o1))
        val blocker = VersionedRef("skill.c.memory_model", 1)
        val trace = plan(listOf(diag), emptyMap(), mapOf("d1" to decision("d1", PrerequisiteEligibility.BLOCKED, listOf(blocker))))
        val candidate = trace.candidates.single()
        assertEquals(CandidateDisposition.BLOCKED_PREREQUISITE, candidate.disposition)
        assertEquals(listOf("eligibility.blocked_hard_prerequisite", DiagnosticCodes.PREREQUISITE_BLOCKED), candidate.reasonCodes)
        assertEquals(listOf(blocker), candidate.relatedSkills)
    }

    // ------------------------------------------------------------------------------------ weakness (13F narrowing)

    private fun event(id: Long, outcome: EvidenceOutcome, baseline: Boolean, masteredBefore: Boolean = false) = coach.model.WeaknessEvent(
        evidenceId = id, sequence = id, studyDay = "2026-10-01", outcome = outcome, evaluatorStatus = EvaluatorStatus.VERIFIED,
        independence = IndependenceClass.INDEPENDENT, contested = false, prerequisiteValid = true, solutionExposed = false, direct = true,
        resource = VersionedRef("item.$id", 1), variantFamilyId = "fam.$id", masteredBefore = masteredBefore, masteredAfter = masteredBefore,
        diagnosticBaseline = baseline,
    )

    @Test
    fun `not knowing something never taught is not a weakness`() {
        val baseline = WeaknessEngine.replay(o1, skill, listOf(event(1, EvidenceOutcome.NEGATIVE, baseline = true),
            event(2, EvidenceOutcome.PARTIAL, baseline = true)))
        assertEquals(coach.model.WeaknessSignal.NONE, baseline.signal)
        assertTrue(baseline.signalEvidenceIds.isEmpty())
        // The same miss during ordinary learning is the accepted `WLRM-v0` rule, unchanged.
        val learning = WeaknessEngine.replay(o1, skill, listOf(event(1, EvidenceOutcome.NEGATIVE, baseline = false)))
        assertEquals(coach.model.WeaknessSignal.SUPPORTED, learning.signal)
        // After mastery, a diagnostic-looking miss is a contradiction like any other.
        val mastered = WeaknessEngine.replay(o1, skill, listOf(event(1, EvidenceOutcome.NEGATIVE, baseline = true, masteredBefore = true)))
        assertEquals(coach.model.AttributionOutcome.VERIFICATION_DUE, mastered.lastOutcome)
    }

    // ------------------------------------------------------------------------------------ the report (13F extension)

    private fun coverage(waiver: String) = coach.model.ObjectiveCoverage(o1, skill, waiver)

    @Test
    fun `a waiver granted or withdrawn is reported once, and an unwritten row is not a before`() {
        val granted = ProgramChangeEngine.coverageChange(coverage("none"), coverage("active"))
        assertEquals(coach.model.StateChangeKind.COVERAGE_WAIVED, granted?.kind)
        assertEquals(o1, granted?.objective)
        assertEquals("diagnostic_coverage:$o1#coverage_waived", granted?.ref)
        assertEquals("replan.prerequisite_state_changed", granted?.kind?.reasonCode)
        assertEquals("confirmed_capabilities", granted?.kind?.family)
        val withdrawn = ProgramChangeEngine.coverageChange(coverage("active"), coverage("none"))
        assertEquals(coach.model.StateChangeKind.COVERAGE_WAIVER_WITHDRAWN, withdrawn?.kind)
        assertEquals("not_reliably_measured", withdrawn?.kind?.family)
        assertNull(ProgramChangeEngine.coverageChange(coverage("active"), coverage("active")))
        val unknown = mutableSetOf<VersionedRef>()
        assertNull(ProgramChangeEngine.coverageChange(null, coverage("active"), unknown))
        assertNull(ProgramChangeEngine.coverageChange(coverage("not_yet_evaluated"), coverage("active"), unknown))
        assertEquals(setOf(skill), unknown)
    }
}
