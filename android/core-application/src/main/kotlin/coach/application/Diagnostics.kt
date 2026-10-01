package coach.application

import coach.engines.DiagnosticWaiverEngine
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.CoverageHold
import coach.model.CoverageWaiver
import coach.model.DiagnosticCodes
import coach.model.DiagnosticObjectiveState
import coach.model.DiagnosticRecord
import coach.model.DiagnosticResult
import coach.model.DiagnosticScope
import coach.model.DiagnosticScopeCodec
import coach.model.DiagnosticSource
import coach.model.DiagnosticStage
import coach.model.DiagnosticTarget
import coach.model.EvidenceRow
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.ObjectiveDiagnosis
import coach.model.ObjectiveGateProfile
import coach.model.ObjectiveGateProfiles
import coach.model.SkillPlanningState
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord

/**
 * The diagnostic waiver as use cases (13F, `VDW-v0 / D-037`).
 *
 * A diagnostic is a `daily` assessment session whose content is the learner's request (`diagnostic_scope/1`). Its
 * attempts go through the ordinary pipeline — `SubmitAttempt` with the session's id, then `RecordEvidence` — so
 * nothing here records evidence or decides a gate. What is added is the projection of what the diagnostic showed
 * (`diagnostic_coverage`, owned by `VDW-v0`), the planner's view of it, and the learner's request and withdrawal.
 */

/** The diagnostic sessions as the store holds them. */
internal object DiagnosticSessions {

    data class Active(val sessionId: Long, val scope: DiagnosticScope)

    /**
     * The learner's open diagnostic: the newest diagnostic row, if it is a request. A newer request replaces an
     * older one and a withdrawal ends it; an undecodable row is not a request anyone can act on.
     */
    fun active(persistence: PersistencePort): Active? {
        val row = persistence.latestAssessmentSessionIn(AssessmentScope.DAILY_MICRO, DiagnosticScopeCodec.FORMAT) ?: return null
        val record = row.record.payload["blueprint"]?.let(DiagnosticScopeCodec::decode) as? DiagnosticRecord.Request ?: return null
        return Active(row.id, record.scope)
    }

    /** Reads each session a row names once per use, and answers which diagnostic (if any) holds an Objective. */
    class Cache(private val persistence: PersistencePort) {
        private val scopes = mutableMapOf<Long, DiagnosticScope?>()

        fun scope(sessionId: Long): DiagnosticScope? = scopes.getOrPut(sessionId) {
            val row = persistence.readTruth("assessment_session", sessionId) ?: return@getOrPut null
            if (row.payload["scope"] != AssessmentScope.DAILY_MICRO.storedAs) return@getOrPut null
            (row.payload["blueprint"]?.let(DiagnosticScopeCodec::decode) as? DiagnosticRecord.Request)?.scope
        }

        /** The diagnostic [row] was gathered in, when that diagnostic's scope holds [objective]; otherwise `null`. */
        fun diagnosticOf(row: EvidenceRow, objective: VersionedRef): Long? {
            val id = row.assessmentSessionId ?: return null
            return id.takeIf { scope(id)?.target(objective) != null }
        }
    }
}

/** `diagnostic_coverage`, one row per Objective (13F, schema v7). An empty column is "none". */
internal object CoverageRows {

    fun key(objective: VersionedRef) = "diagnostic_coverage:${objective.logicalId}@v${objective.version}"

    /** One Objective's stored coverage, as the planner and the result read it. */
    data class Stored(
        val objective: VersionedRef,
        val skill: VersionedRef,
        val waiver: CoverageWaiver?,
        val diagnosticSessionId: Long?,
        val state: DiagnosticObjectiveState?,
        val stage: DiagnosticStage?,
        val failedGates: List<String>,
        val windowVariantFamilies: List<String>,
        val windowDependencyGroups: List<String>,
        val reasonCodes: List<String>,
    ) {
        /** The Objective as the diagnostic [sessionId] sees it, or `null` when this row was not built for it. */
        fun diagnosis(target: DiagnosticTarget, sessionId: Long): ObjectiveDiagnosis? {
            if (waiver != null) {
                return ObjectiveDiagnosis(target, DiagnosticObjectiveState.WAIVED, null, failedGates, windowVariantFamilies,
                    windowDependencyGroups, emptyList(), waiver)
            }
            if (diagnosticSessionId != sessionId || state == null) return null
            return ObjectiveDiagnosis(target, state, stage, failedGates, windowVariantFamilies, windowDependencyGroups, reasonCodes, null)
        }
    }

    fun write(
        persistence: PersistencePort,
        objective: VersionedRef,
        skill: VersionedRef,
        waiver: CoverageWaiver?,
        diagnosticSessionId: Long?,
        diagnosis: ObjectiveDiagnosis?,
        today: String,
        watermark: Long,
        version: Int,
        builtAt: Long,
    ) {
        persistence.writeProjection(
            ProjectionRecord(
                key = key(objective),
                policyVersion = DiagnosticWaiverEngine.POLICY_VERSION,
                truthWatermark = watermark,
                builtAtInstant = builtAt,
                inputCurriculumVersion = version,
                payload = mapOf(
                    "skill_logical_id" to skill.logicalId,
                    "skill_version" to skill.version.toString(),
                    "waiver" to if (waiver != null) ACTIVE else NONE,
                    "waiver_session_id" to waiver?.sessionId?.toString().orEmpty(),
                    "waiver_source_evidence_ids" to waiver?.sourceEvidenceIds.orEmpty().joinToString(","),
                    "waiver_granted_at_sequence" to waiver?.grantedAtSequence?.toString().orEmpty(),
                    "waiver_granted_on_study_day" to waiver?.grantedOnStudyDay.orEmpty(),
                    "diagnostic_session_id" to (diagnosticSessionId?.takeIf { diagnosis != null }?.toString().orEmpty()),
                    "diagnostic_state" to diagnosis?.state?.id.orEmpty(),
                    "diagnostic_stage" to diagnosis?.stage?.id.orEmpty(),
                    "failed_gates" to diagnosis?.failedGates.orEmpty().joinToString(","),
                    "window_variant_families" to diagnosis?.windowVariantFamilies.orEmpty().joinToString(","),
                    "window_dependency_groups" to diagnosis?.windowDependencyGroups.orEmpty().joinToString(","),
                    "reason_codes" to diagnosis?.reasonCodes.orEmpty().joinToString(","),
                    "as_of_study_day" to today,
                ),
            )
        )
    }

    fun read(persistence: PersistencePort, objective: VersionedRef): Stored? {
        val row = persistence.readProjection(key(objective)) ?: return null
        val p = row.payload
        fun list(column: String) = p[column].orEmpty().split(",").filter { it.isNotEmpty() }
        val skill = VersionedRef(p.getValue("skill_logical_id"), p.getValue("skill_version").toInt())
        val waiver = if (p["waiver"] == ACTIVE) {
            CoverageWaiver(
                objective = objective,
                skill = skill,
                sessionId = p.getValue("waiver_session_id").toLong(),
                sourceEvidenceIds = list("waiver_source_evidence_ids").map { it.toLong() },
                grantedAtSequence = p.getValue("waiver_granted_at_sequence").toLong(),
                grantedOnStudyDay = p.getValue("waiver_granted_on_study_day"),
            )
        } else {
            null
        }
        return Stored(
            objective = objective,
            skill = skill,
            waiver = waiver,
            diagnosticSessionId = p["diagnostic_session_id"]?.toLongOrNull(),
            state = DiagnosticObjectiveState.entries.firstOrNull { it.id == p["diagnostic_state"] },
            stage = DiagnosticStage.entries.firstOrNull { it.id == p["diagnostic_stage"] },
            failedGates = list("failed_gates"),
            windowVariantFamilies = list("window_variant_families"),
            windowDependencyGroups = list("window_dependency_groups"),
            reasonCodes = list("reason_codes"),
        )
    }

    const val ACTIVE = "active"
    const val NONE = "none"
}

/**
 * Rebuilding one Skill's diagnostic coverage from evidence (13F) — the state family `VDW-v0` owns. Each Objective's
 * waiver is replayed with the mastery engine's own gates; the Objectives of the open diagnostic also get where they
 * stand and what to check next. It writes no truth, and no other engine's state.
 */
class RebuildDiagnosticCoverage(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Rebuilt(val skill: VersionedRef, val waivers: List<CoverageWaiver>, val diagnoses: List<ObjectiveDiagnosis>, val written: Boolean)

    fun rebuild(skill: VersionedRef, profiles: List<ObjectiveGateProfile>): Rebuilt {
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion()
        val active = DiagnosticSessions.active(persistence)
        val sessions = DiagnosticSessions.Cache(persistence)
        val built = profiles.map { profile ->
            val rows = persistence.evidenceFor(profile.ref)
            val waiver = DiagnosticWaiverEngine.waiver(profile, skill, rows) { sessions.diagnosticOf(it, profile.ref) }
            val diagnosis = active?.scope?.target(profile.ref)?.let { target ->
                DiagnosticWaiverEngine.diagnose(target, profile, rows, active.sessionId, waiver)
            }
            Triple(profile.ref, waiver, diagnosis)
        }
        val waivers = built.mapNotNull { it.second }
        val diagnoses = built.mapNotNull { it.third }
        val version = curriculumVersion ?: return Rebuilt(skill, waivers, diagnoses, written = false)
        val now = clock.now()
        persistence.inTransaction {
            built.forEach { (objective, waiver, diagnosis) ->
                CoverageRows.write(persistence, objective, skill, waiver, active?.sessionId, diagnosis, now.studyDay, watermark,
                    version, now.instantEpochMillis)
            }
        }
        return Rebuilt(skill, waivers, diagnoses, written = true)
    }
}

/**
 * The learner asking to show what they already know (13F, `VDW-v0` §3). Saying "I know this" opens a diagnostic and
 * names its scope; it is never evidence and waives nothing.
 *
 * The scope is the required and critical Objectives of the named Skills, minus those already shown or already
 * waived — they are not tested again (§17). One diagnostic is open at a time: a new request replaces an open one,
 * and nothing about the old one is owed.
 */
class RequestDiagnostic(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    sealed interface Requested {
        /** No curriculum is published; there is nothing to check against and nothing is written. */
        data object NothingPublished : Requested

        /** The request names something that cannot be checked; nothing is written, and the reason says why. */
        data class Refused(val reason: String) : Requested

        /** Every Objective it names is already shown or waived; there is nothing to check and nothing is written. */
        data object NothingToDiagnose : Requested

        data class Written(val sessionId: Long, val scope: DiagnosticScope) : Requested
    }

    fun request(source: DiagnosticSource, skills: List<VersionedRef>): Requested {
        val version = persistence.latestCurriculumVersion() ?: return Requested.NothingPublished
        if (skills.isEmpty()) return Requested.Refused("a diagnostic names the Skills it checks")
        val sessions = DiagnosticSessions.Cache(persistence)
        val profilesBySkill = linkedMapOf<VersionedRef, List<ObjectiveGateProfile>>()
        val targets = mutableListOf<DiagnosticTarget>()
        for (skill in skills.distinct()) {
            val row = persistence.skill(skill) ?: return Requested.Refused("$skill is not published")
            // Only a Skill new learning may start on can have its starting lessons skipped (`KGC-v0` §27).
            if (row.lifecycleStatus != PUBLISHED) return Requested.Refused("$skill takes no new start (${row.lifecycleStatus})")
            val profiles = persistence.objectivesOf(skill).map(ObjectiveGateProfiles::of)
            profilesBySkill[skill] = profiles
            profiles.filter { it.required || it.critical }.forEach { profile ->
                val rows = persistence.evidenceFor(profile.ref)
                val waived = DiagnosticWaiverEngine.waiver(profile, skill, rows) { sessions.diagnosticOf(it, profile.ref) } != null
                if (waived || DiagnosticWaiverEngine.gatesPass(profile, rows)) return@forEach
                targets += DiagnosticTarget(profile.ref, skill, profile.critical)
            }
        }
        if (targets.isEmpty()) return Requested.NothingToDiagnose

        val now = clock.now()
        val scope = DiagnosticScope(source, now.studyDay, version, targets)
        val sessionId = persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "assessment_session", now,
                    mapOf("scope" to AssessmentScope.DAILY_MICRO.storedAs, "blueprint" to DiagnosticScopeCodec.encode(DiagnosticRecord.Request(scope))),
                )
            )
        }
        // The planner reads where each Objective stands from the projection, never from evidence.
        val rebuild = RebuildDiagnosticCoverage(persistence, clock)
        scope.skills.forEach { rebuild.rebuild(it, profilesBySkill.getValue(it)) }
        return Requested.Written(sessionId, scope)
    }

    private companion object {
        const val PUBLISHED = "published"
    }
}

/**
 * The learner leaving the fast path (13F). A diagnostic is never mandatory (`VDW-v0` §3): the withdrawal is
 * appended, waivers already granted stay, and the Objectives still open simply return to normal learning.
 */
class WithdrawDiagnostic(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    sealed interface Withdrawn {
        data object NothingOpen : Withdrawn
        data class Written(val withdrawnSessionId: Long, val rowId: Long) : Withdrawn
    }

    fun withdraw(): Withdrawn {
        val active = DiagnosticSessions.active(persistence) ?: return Withdrawn.NothingOpen
        val now = clock.now()
        val id = persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "assessment_session", now,
                    mapOf(
                        "scope" to AssessmentScope.DAILY_MICRO.storedAs,
                        "blueprint" to DiagnosticScopeCodec.encode(DiagnosticRecord.Withdrawal(active.sessionId, now.studyDay)),
                    ),
                )
            )
        }
        return Withdrawn.Written(active.sessionId, id)
    }
}

/**
 * The open diagnostic's result (13F, `VDW-v0` §10, §21), read from the projection the planner also reads — so the
 * result can never describe a diagnostic other than the one being planned.
 */
class ReadDiagnosticResult(
    private val persistence: PersistencePort,
) {
    sealed interface Read {
        data object NothingOpen : Read

        /** A target's coverage was not built for this diagnostic; nothing is guessed. */
        data class Unreadable(val objectives: List<VersionedRef>) : Read

        data class Result(val result: DiagnosticResult) : Read
    }

    fun read(): Read {
        val active = DiagnosticSessions.active(persistence) ?: return Read.NothingOpen
        val read = active.scope.targets.map { target ->
            target to CoverageRows.read(persistence, target.objective)?.diagnosis(target, active.sessionId)
        }
        val missing = read.filter { it.second == null }.map { it.first.objective }
        if (missing.isNotEmpty()) return Read.Unreadable(missing)
        val diagnoses = read.map { it.second!! }
        val mastered = active.scope.skills.filter { skill ->
            val axis = persistence.readProjection(ResolvePrerequisites.skillStateKey(skill))?.payload?.get("mastery_axis_state")
            axis == MasteryAxisState.CONFIRMED_CURRENT.id || axis == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id
        }.toSet()
        val outcome = DiagnosticWaiverEngine.outcome(diagnoses, mastered)
        val inProgress = diagnoses.any { it.state.open }
        return Read.Result(
            DiagnosticResult(active.sessionId, active.scope, diagnoses, outcome, inProgress,
                DiagnosticWaiverEngine.resultReasons(active.scope, outcome, inProgress))
        )
    }
}

/**
 * The diagnostic as the planner sees it (13F): one need per Skill with an open Objective, the next fresh trusted
 * item for each open Objective as that need's candidate, and the coverage holds a lesson is checked against. All of
 * it is read from projections, curriculum, content and exposure — planning a day still reads no evidence history.
 */
internal object DiagnosticPlanning {

    class Planning(val sessionId: Long, val scope: DiagnosticScope, val diagnoses: List<ObjectiveDiagnosis>, val needs: List<LearningNeed>)

    fun read(persistence: PersistencePort, states: List<SkillPlanningState>): Planning? {
        val active = DiagnosticSessions.active(persistence) ?: return null
        // A target whose coverage was not built for this diagnostic is not known, and nothing is planned on a guess.
        val diagnoses = active.scope.targets.mapNotNull { target ->
            CoverageRows.read(persistence, target.objective)?.diagnosis(target, active.sessionId)
        }
        return Planning(active.sessionId, active.scope, diagnoses,
            DiagnosticWaiverEngine.needs(active.sessionId, active.scope, diagnoses, states))
    }

    fun candidates(
        persistence: PersistencePort,
        content: ContentPort,
        planning: Planning?,
        openNeeds: List<LearningNeed>,
        evaluatorAvailable: Boolean,
    ): List<TaskCandidate> {
        planning ?: return emptyList()
        val open = openNeeds.map { it.needKey }.toSet()
        val used = mutableSetOf<VersionedRef>()
        return planning.diagnoses.filter { it.state.open && DiagnosticWaiverEngine.needKey(it.target.skill) in open }
            .groupBy { it.target.skill }
            .flatMap { (skill, diagnoses) ->
                val items = content.assessmentItemsFor(skill).mapNotNull { StoreTrust.apply(persistence, it) }
                val profiles = items.flatMap { it.targetObjectives }.distinct()
                    .mapNotNull { persistence.objectiveProfile(it) }.associateBy { it.ref }
                val gates = persistence.objectivesOf(skill).map(ObjectiveGateProfiles::of).associateBy { it.ref }
                val exposures = if (items.isEmpty()) emptyList()
                else persistence.exposuresFor(items.map { it.ref }, items.map { it.variantFamilyId }.distinct())
                diagnoses.mapNotNull { diagnosis ->
                    val gate = gates[diagnosis.target.objective] ?: return@mapNotNull null
                    val item = DiagnosticWaiverEngine.route(diagnosis, gate, items, profiles, exposures, evaluatorAvailable, used)
                        ?: return@mapNotNull null
                    used += item.ref
                    DiagnosticWaiverEngine.candidate(planning.sessionId, diagnosis, item)
                }
            }
    }

    /**
     * `VDW-v0` §17 for each Objective a lesson declares: waived coverage resolves it — fully when every required
     * Objective of its Skill is waived — and an Objective the open diagnostic is still checking holds it.
     */
    fun holds(persistence: PersistencePort, candidates: List<TaskCandidate>, planning: Planning?): Map<VersionedRef, CoverageHold> {
        val declared = candidates.filter { it.purpose == TaskPurpose.TEACH }.flatMap { it.targetObjectives }.distinct()
        if (declared.isEmpty()) return emptyMap()
        val stored = mutableMapOf<VersionedRef, CoverageRows.Stored?>()
        fun row(objective: VersionedRef) = stored.getOrPut(objective) { CoverageRows.read(persistence, objective) }
        val fullBySkill = mutableMapOf<VersionedRef, Boolean>()
        fun full(skill: VersionedRef) = fullBySkill.getOrPut(skill) {
            persistence.objectivesOf(skill).map(ObjectiveGateProfiles::of).filter { it.required || it.critical }
                .all { row(it.ref)?.waiver != null }
        }
        return declared.mapNotNull { objective ->
            val r = row(objective) ?: return@mapNotNull null
            when {
                r.waiver != null -> objective to CoverageHold(
                    waived = true,
                    reasonCode = if (full(r.skill)) DiagnosticCodes.FULL_COVERAGE_WAIVER else DiagnosticCodes.PARTIAL_COVERAGE_WAIVER,
                )
                planning != null && r.diagnosticSessionId == planning.sessionId && r.state?.open == true ->
                    objective to CoverageHold(waived = false, reasonCode = DiagnosticCodes.USER_REQUESTED_FAST_PATH)
                else -> null
            }
        }.toMap()
    }
}

/**
 * An item as the store knows it: published or not there at all, and trusted as far as its latest validation record
 * says — never the item document's own claim (11D, 13A).
 */
internal object StoreTrust {
    fun apply(persistence: PersistencePort, item: AssessmentItem): AssessmentItem? {
        val published = persistence.resourceVersion(item.ref) ?: return null
        return item.copy(
            lifecycleStatus = persistence.latestValidation(item.ref)?.status ?: LifecycleStatus.CANDIDATE,
            contentOrigin = published.contentOrigin,
            evidenceType = published.evidenceType,
        )
    }
}
