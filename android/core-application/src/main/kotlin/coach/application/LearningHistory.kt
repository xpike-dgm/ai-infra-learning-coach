package coach.application

import coach.engines.ProgramChangeEngine
import coach.model.AssessmentScope
import coach.model.BlueprintCodecs
import coach.model.EvidenceKind
import coach.model.EvidenceRow
import coach.model.GenerationKind
import coach.model.HistoryEntry
import coach.model.HistoryFacts
import coach.model.HistoryFamily
import coach.model.NotMeasured
import coach.model.ObjectiveGateProfiles
import coach.model.ObjectiveRow
import coach.model.PlanTraceCodec
import coach.model.ProgramSnapshot
import coach.model.SessionReport
import coach.model.SkillAxes
import coach.model.StateChange
import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord

/**
 * The store as it stood at one point of the evidence sequence (16B), for replaying the engines without touching it.
 *
 * Evidence is cut at [cutoff]; the watermark is the cutoff; every projection the engines read or write lives in memory
 * here and never reaches the real store; no truth can be written. Everything else — the published curriculum, its
 * misconception catalog, the dispositions the store already applies to each row — is the real store's.
 */
internal class PrefixPersistence(
    private val base: PersistencePort,
    private val evidence: Map<VersionedRef, List<EvidenceRow>>,
) : PersistencePort by base {
    var cutoff: Long = 0L
    private val projections = mutableMapOf<String, ProjectionRecord>()

    override fun <T> inTransaction(block: () -> T): T = block()
    override fun appendTruth(record: TruthRecord): Long = error("a replay writes no truth")
    override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence[objective].orEmpty().filter { it.sequence <= cutoff }
    override fun truthWatermark(): Long = cutoff
    override fun readProjection(key: String): ProjectionRecord? = projections[key]
    override fun writeProjection(record: ProjectionRecord) { projections[record.key] = record }
}

/**
 * One Skill's history replayed (16B, user decision): after every evidence row, in recording order, the mastery,
 * retention and weakness engines are run over exactly the evidence up to that row, on that row's study day — the
 * same rebuilds `RecomputeSkillState` runs after a row. Before the first row they are run over no evidence at all, so
 * the starting state is what the engines say about nothing, not an unknown. What a row changed is
 * [ProgramChangeEngine.stateChanges] between the two readings: the program change report's own vocabulary.
 */
internal object HistoryReplay {

    data class Step(val row: EvidenceRow, val before: SkillAxes, val after: SkillAxes, val changes: List<StateChange>)

    private val MASTERED = MasteryTimeline.MASTERED.map { it.id }.toSet()

    fun steps(base: PersistencePort, skill: VersionedRef, objectives: List<ObjectiveRow>): List<Step> {
        val evidence = objectives.associate { it.ref to base.evidenceFor(it.ref) }
        val rows = evidence.values.flatten().sortedBy { it.sequence }
        if (rows.isEmpty()) return emptyList()
        val profiles = objectives.map(ObjectiveGateProfiles::of)
        val overlay = PrefixPersistence(base, evidence)

        fun read(cutoff: Long, day: String): SkillAxes {
            overlay.cutoff = cutoff
            val clock = object : ClockPort {
                override fun now() = StudyTimestamp(0L, day, 0)
            }
            RebuildMastery(overlay, clock).rebuild(skill, profiles)
            RebuildRetention(overlay, clock).rebuild(skill, profiles)
            RebuildWeakness(overlay, clock).rebuild(skill, profiles)
            val axes = overlay.readProjection(ResolvePrerequisites.skillStateKey(skill))?.payload.orEmpty()
            fun axis(column: String) = axes[column]?.ifEmpty { null } ?: ResolvePrerequisites.NOT_YET_EVALUATED
            return SkillAxes(skill, axis(SkillStateRows.MASTERY), axis(SkillStateRows.RETENTION), axis(SkillStateRows.WEAKNESS))
        }

        var previous = read(rows.first().sequence - 1, requireNotNull(rows.first().studyDay) { "a row without its study day cannot be replayed" })
        return rows.map { row ->
            val after = read(row.sequence, requireNotNull(row.studyDay) { "a row without its study day cannot be replayed" })
            Step(row, previous, after, ProgramChangeEngine.stateChanges(previous, after)).also { previous = after }
        }
    }

    /** `SPWX-v0` §12's families; the first that applies wins. */
    fun family(step: Step): HistoryFamily = when {
        step.row.assessmentSessionId != null -> HistoryFamily.ASSESSMENT
        // The row that closes a remediation was made while it was open, so it is here too (13E's REMEDIATION_CLOSED
        // needs `remediation_required` before).
        step.before.weakness == ResolvePrerequisites.REMEDIATION_REQUIRED -> HistoryFamily.REMEDIATION
        step.before.mastery in MASTERED -> HistoryFamily.REVIEW
        else -> HistoryFamily.LEARNING
    }
}

/**
 * The learning history and the longitudinal assessment report (16B, `LHRX-v0`): read-only.
 *
 * `SPWX-v0` §12: a chronology of what happened and what it changed — never a streak, attendance or a gap marked as a
 * miss. §13: across sessions, the six `ASUX-v0` families, owning no mastery and aggregating nothing into a score. The
 * changes are the engines' own, replayed over the stored evidence ([HistoryReplay]); the plan changes are the
 * difference between two recorded plan versions, found by the replan's own trace watermark.
 */
class LearningHistoryQuery(
    private val persistence: PersistencePort,
    // 17A: the content port names each Objective by its lesson's title; without it Objectives are named by their Skill.
    private val content: ContentPort? = null,
) {

    /** The whole history, or one Skill's ([skill], for `skill_detail`'s history entry). Newest first. */
    fun load(skill: VersionedRef? = null): HistoryFacts {
        if (!persistence.curriculumPublished()) return HistoryFacts(curriculumLoaded = false, entries = emptyList(), sessions = emptyList())
        val skills = if (skill != null) listOfNotNull(persistence.skill(skill)) else persistence.publishedSkills()
        val steps = skills.flatMap { HistoryReplay.steps(persistence, it.ref, persistence.objectivesOf(it.ref)) }
        val entries = steps.map(::entry).sortedByDescending { it.sequence }
        val sessions = if (skill != null) emptyList() else reports(steps, entries)
        return HistoryFacts(curriculumLoaded = true, entries = entries, sessions = sessions.sortedByDescending { it.sessionId },
            skillNames = skills.associate { it.ref to it.canonicalName },
            objectiveTitles = content?.let { c ->
                entries.map { it.objective }.distinct().mapNotNull { o -> c.lessonTitleFor(o)?.let { o to it } }.toMap()
            }.orEmpty())
    }

    private fun entry(step: HistoryReplay.Step) = HistoryEntry(
        sequence = step.row.sequence,
        studyDay = step.row.studyDay,
        skill = step.row.skill,
        objective = step.row.objective,
        family = HistoryReplay.family(step),
        kind = EvidenceKind.of(step.row),
        outcome = step.row.outcome,
        assistance = step.row.assistanceLevels,
        attemptId = step.row.attemptId,
        assessmentSessionId = step.row.assessmentSessionId,
        changes = step.changes,
        contested = step.row.contested,
        solutionExposed = step.row.solutionExposed,
        disposition = step.row.disposition,
        dispositionReason = step.row.dispositionReason,
    )

    private fun reports(steps: List<HistoryReplay.Step>, entries: List<HistoryEntry>): List<SessionReport> {
        val plans = persistence.planVersions().mapNotNull { plan -> plan.traceText?.let(PlanTraceCodec::decode)?.let { plan to it } }
        val allSequences = steps.map { it.row.sequence }.sorted()
        return persistence.assessmentSessions().map { session ->
            val mine = steps.filter { it.row.assessmentSessionId == session.id }.sortedBy { it.row.sequence }
            val mineEntries = entries.filter { it.assessmentSessionId == session.id }.sortedBy { it.sequence }
            // Each change one of this session's own rows caused, named once: a session's slots may spread over days
            // with other work in between, and a net diff would credit the session with that work.
            val changes = mine.flatMap { it.changes }.distinctBy { it.skill to it.kind }
            val answered = mine.mapNotNull { it.row.resource }.toSet()
            val unanswered = readySlots(session).filter { (_, item) -> item == null || item !in answered }.map { it.first }
            val last = mine.lastOrNull()
            val replan = last?.let { row ->
                val next = allSequences.firstOrNull { it > row.row.sequence } ?: Long.MAX_VALUE
                plans.indexOfFirst { (_, trace) ->
                    trace.generationKind == GenerationKind.REPLAN.id && trace.truthWatermark >= row.row.sequence &&
                        trace.truthWatermark < next && trace.studyDay == row.row.studyDay
                }.takeIf { it > 0 }
            }
            val planChanges = replan?.let { index ->
                val (beforePlan, beforeTrace) = plans[index - 1]
                val (afterPlan, afterTrace) = plans[index]
                ProgramChangeEngine.report(
                    ProgramSnapshot(beforeTrace.truthWatermark, beforeTrace.studyDay, emptyMap(), beforePlan.planVersionId, beforeTrace),
                    ProgramSnapshot(afterTrace.truthWatermark, afterTrace.studyDay, emptyMap(), afterPlan.planVersionId, afterTrace),
                ).planChanges
            }.orEmpty()
            SessionReport(
                sessionId = session.id,
                scope = session.record.payload["scope"].orEmpty(),
                studyDay = session.record.recordedAt.studyDay,
                evidence = mineEntries,
                stateChanges = changes,
                notMeasured = NotMeasured(mineEntries.filter { it.kind != EvidenceKind.INDEPENDENT }, unanswered),
                planChanges = planChanges,
                replanFound = replan != null,
            )
        }
    }

    /** The weekly or monthly blueprint's ready slots and their items; a daily session composes none. */
    private fun readySlots(session: StoredTruth): List<Pair<String, VersionedRef?>> {
        val scope = when (session.record.payload["scope"]) {
            AssessmentScope.WEEKLY_BLUEPRINT.storedAs -> AssessmentScope.WEEKLY_BLUEPRINT
            AssessmentScope.MONTHLY_CAPABILITY.storedAs -> AssessmentScope.MONTHLY_CAPABILITY
            else -> return emptyList()
        }
        val blueprint = session.record.payload["blueprint"]?.let { runCatching { BlueprintCodecs.decode(scope, it) }.getOrNull() }
            ?: return emptyList()
        return blueprint.readySlots.map { it.slotId to it.item }
    }
}
