package coach.model

/**
 * The facts Progress is built from (16A, `SKAX-v0`): what the store says about each published Skill, read for the
 * screen and never written back. Like [TodayFacts] they live here because the read path (`core-application`) and the
 * projection (`core-presentation`) both need them and `MSBX-v0` points both at this module.
 */

/**
 * How one evidence row may be shown (`SPWX-v0` §8: "independent, assisted, provisional and invalid evidence rather
 * than merging them into one count"). The first that applies wins: a row that cannot count is never shown as counting
 * in some weaker way, and a provisional judgement is never shown as an independent one.
 */
enum class EvidenceKind(val id: String) {
    /** Invalid, evaluated invalid, contested, or produced while the target's prerequisites were missing (`TEPM-v0` §11). */
    NOT_COUNTED("not_counted"),
    /** Judged by an uncalibrated evaluator; it states nothing settled yet (`AIAX-v0`). */
    PROVISIONAL("provisional"),
    /** Done with help, as practice, after a shown solution, or with code someone else wrote (`TEPM-v0` §9). */
    ASSISTED("assisted"),
    /** Done alone, judged and verified. */
    INDEPENDENT("independent"),
    ;

    companion object {
        fun of(row: EvidenceRow): EvidenceKind = when {
            row.outcome == EvidenceOutcome.INVALID || row.evaluatorStatus == EvaluatorStatus.INVALID ||
                row.contested || !row.prerequisiteValid -> NOT_COUNTED
            row.evaluatorStatus == EvaluatorStatus.PROVISIONAL -> PROVISIONAL
            row.independenceClass != IndependenceClass.INDEPENDENT || row.solutionExposed -> ASSISTED
            else -> INDEPENDENT
        }
    }
}

/** How many rows of each kind exist — an inventory of rows, never a score and never divided by anything. */
data class EvidenceSummary(val counts: Map<EvidenceKind, Int>) {
    val total: Int get() = counts.values.sum()

    companion object {
        fun of(rows: List<EvidenceRow>): EvidenceSummary =
            EvidenceSummary(EvidenceKind.entries.associateWith { kind -> rows.count { EvidenceKind.of(it) == kind } })
    }
}

/** The evidence-level attention qualifiers `VDSX-v0` named, read from a Skill's rows grouped by Objective. */
object EvidenceQualifiers {
    fun of(rowsByObjective: Collection<List<EvidenceRow>>): Set<AttentionQualifier> = buildSet {
        val rows = rowsByObjective.flatten()
        if (rows.any { it.contested }) add(AttentionQualifier.CONTESTED_EVIDENCE_PRESENT)
        if (rows.any { EvidenceKind.of(it) == EvidenceKind.PROVISIONAL }) add(AttentionQualifier.PROVISIONAL_EVALUATION_PRESENT)
        // A recheck is open while no independent row on that Objective came after the row that asked for it (14E).
        val recheckOpen = rowsByObjective.any { objectiveRows ->
            val sorted = objectiveRows.sortedBy { it.sequence }
            val last = sorted.indexOfLast {
                it.independenceClass == IndependenceClass.REQUIRES_INDEPENDENT_RECHECK && EvidenceKind.of(it) != EvidenceKind.NOT_COUNTED
            }
            last >= 0 && sorted.drop(last + 1).none { EvidenceKind.of(it) == EvidenceKind.INDEPENDENT }
        }
        if (recheckOpen) add(AttentionQualifier.REQUIRES_INDEPENDENT_RECHECK)
    }
}

/** What the gate says about new work on one Skill, kept with the projection so its reason stays reachable. */
data class PrerequisiteContext(
    val eligibility: PrerequisiteEligibility,
    val hardBlockers: List<VersionedRef>,
    val uncertain: List<VersionedRef>,
    val softGaps: List<VersionedRef>,
    val metadataProblems: List<String>,
)

/** One published Skill as Progress reads it. */
data class SkillProgressRow(
    val skill: VersionedRef,
    val name: String,
    val projection: SkillStateProjection,
    val prerequisite: PrerequisiteContext,
)

/** One Objective of a Skill (`SPWX-v0` §8: Skill state never hides an Objective-level gap). */
data class ObjectiveProgress(
    val objective: VersionedRef,
    val required: Boolean,
    /** `true`/`false` as the mastery engine stored it; `null` when it has not judged this Objective. */
    val gatesPassed: Boolean?,
    /** The weakness engine's signal for this Objective, `null` when it has not written one. */
    val weakness: WeaknessSignal?,
    val evidence: EvidenceSummary,
    /** 16C: the catalogued misconceptions the memory holds an open signal or a history for on this Objective. */
    val misconceptions: List<MisconceptionContext> = emptyList(),
)

/**
 * One catalogued misconception as `skill_detail` may show it (16C; `SPWX-v0` §10). The signal is what 14B's memory
 * wrote, never more: an AI-proposed label never rose above a hypothesis there, and a hypothesis is shown only as the
 * catalog's own question.
 */
data class MisconceptionContext(
    val misconception: VersionedRef,
    val name: String,
    val openQuestion: String,
    val signal: WeaknessSignal,
)

/** Everything `skill_detail` shows about one Skill. */
data class SkillDetailFacts(
    val row: SkillProgressRow,
    val capabilityStatement: String,
    val objectives: List<ObjectiveProgress>,
    val evidence: EvidenceSummary,
    /** The names of the Skills [SkillProgressRow.prerequisite] names, so a blocker is shown by name and linked. */
    val skillNames: Map<VersionedRef, String>,
    val topics: List<NamedEntity>,
    /** 16C: a Technical English Skill's `TECP-v0` anchor band; `null` for every other Skill. */
    val englishBand: CefrBand? = null,
)

/** Everything the store can say about Progress right now. */
data class ProgressFacts(
    val curriculumLoaded: Boolean,
    val skills: List<SkillProgressRow>,
    /**
     * 16H: Skills an assessment session could measure now — a ready, unserved slot of this week's or this month's
     * blueprint, or an Objective the open diagnostic still checks. A fact about the session, never a priority (`SPWX-v0` §7.2).
     */
    val assessmentOpportunities: Set<VersionedRef> = emptySet(),
)
