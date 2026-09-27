package coach.engines

import coach.model.EdgeKind
import coach.model.MasteryAxisState
import coach.model.MetadataProblem
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteEligibility
import coach.model.PrerequisiteReadiness
import coach.model.PrerequisiteReason
import coach.model.PrerequisiteRequirement
import coach.model.ReadinessInputs
import coach.model.ReadinessNote
import coach.model.RequirementSource
import coach.model.RetentionAxis
import coach.model.SkillReadiness
import coach.model.VersionedRef

/**
 * `PRG-v0` as code (12B): the prerequisite readiness gate.
 *
 * Its job is to keep target evidence **interpretable and fair** — a failure on something never
 * taught says nothing about the target — and not to lock the curriculum. So a missing prerequisite
 * holds back only the work that really depends on it, `review_due` is not forgetting, a soft gap
 * never locks anything, and nothing here takes a priority as input: priority cannot buy its way
 * past a hard prerequisite.
 *
 * Everything is a pure function of its arguments (§21): the same graph, the same readiness and the
 * same candidate always give the same decision.
 */
object PrerequisiteEngine {

    const val PREREQUISITE_POLICY_VERSION = "PRG-v0"

    /**
     * The only strictness profile any authored edge names (`KGC-v0` §12.2: the profile expresses
     * existing `PRG-v0` metadata and does not rewrite it). A profile this engine does not know is a
     * metadata problem, not something to guess the meaning of.
     */
    const val DEFAULT_STRICTNESS_PROFILE = "default_prg_v0"

    /** `KGC-v0` §27: the edge lifecycles whose edges gate at runtime. `deprecated` is not `wrong`. */
    private val EDGE_IN_EFFECT = setOf("published", "deprecated")

    /** `KGC-v0` §27: an edge deliberately taken off the active route. */
    private const val EDGE_RETIRED = "retired"

    /**
     * `PRG-v0` §3. Readiness is read from the axes other engines own and is never a blend of them.
     *
     * - Remediation that is actually open makes a prerequisite unusable, mastered or not (§12).
     * - A contradicted mastery is `uncertain`: historical mastery is not erased (§12).
     * - Anything short of confirmed mastery is `not_ready`.
     * - Confirmed mastery is `ready`; `review_due` makes it `ready_due`, which still does not block
     *   (§11); `verification_due` and `at_risk` make it `uncertain`.
     *
     * An axis whose engine has not written it yet is **named** in [SkillReadiness.notes]. It is
     * never read as bad news: until the retention engine exists (13), nothing has said a confirmed
     * Skill needs review, and inventing that it does would lock work on no evidence at all.
     */
    fun readiness(inputs: ReadinessInputs): SkillReadiness {
        val notes = buildList {
            if (inputs.mastery == null) add(ReadinessNote.MASTERY_NOT_YET_EVALUATED)
            if (inputs.retention == RetentionAxis.NOT_YET_EVALUATED || inputs.retention == RetentionAxis.UNTRACKED) {
                add(ReadinessNote.RETENTION_NOT_YET_EVALUATED)
            }
            if (inputs.remediationRequired == null) add(ReadinessNote.REMEDIATION_NOT_YET_EVALUATED)
        }
        val remediation = inputs.remediationRequired == true
        val readiness = when {
            remediation -> PrerequisiteReadiness.NOT_READY
            inputs.mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE -> PrerequisiteReadiness.UNCERTAIN
            inputs.mastery != MasteryAxisState.CONFIRMED_CURRENT -> PrerequisiteReadiness.NOT_READY
            else -> when (inputs.retention) {
                RetentionAxis.REVIEW_DUE -> PrerequisiteReadiness.READY_DUE
                RetentionAxis.VERIFICATION_DUE, RetentionAxis.AT_RISK -> PrerequisiteReadiness.UNCERTAIN
                RetentionAxis.FRESH, RetentionAxis.STABLE,
                RetentionAxis.UNTRACKED, RetentionAxis.NOT_YET_EVALUATED -> PrerequisiteReadiness.READY
            }
        }
        return SkillReadiness(inputs.skill, readiness, remediation, notes, inputs.snapshotRef)
    }

    /** Several versions of one edge may exist; the newest `edge_version` is the one in force. */
    fun latestEdges(edges: List<PrerequisiteEdge>): List<PrerequisiteEdge> =
        edges.groupBy { it.prerequisite to it.target }
            .map { (_, versions) -> versions.maxBy { it.edgeVersion } }
            .sortedWith(compareBy({ it.prerequisite.logicalId }, { it.prerequisite.version }))

    /** The result of reading what a candidate needs: its requirements and anything wrong with them. */
    data class Requirements(
        val requirements: List<PrerequisiteRequirement>,
        val problems: List<String>,
    )

    /**
     * Collects a candidate's requirements from the edges into its target (the graph) and from the
     * candidate itself (the exact task). A task requirement is always hard (§6): the graph not
     * mentioning it is no reason to ignore it.
     *
     * An edge still in `draft` is **not** silently dropped: a hard edge that is not in force would let
     * a task through on a prerequisite the learner may not have, which is exactly the contamination
     * this gate exists to prevent. It is reported, and the candidate waits until the edge is
     * published or retired.
     */
    fun requirements(
        candidate: PrerequisiteCandidate,
        edgesIntoTarget: List<PrerequisiteEdge>,
        isPublished: (VersionedRef) -> Boolean,
    ): Requirements {
        val requirements = mutableListOf<PrerequisiteRequirement>()
        val problems = mutableListOf<String>()
        if (!isPublished(candidate.target)) problems += "${MetadataProblem.UNPUBLISHED_TARGET.id}:${candidate.target}"

        latestEdges(edgesIntoTarget.filter { it.target == candidate.target }).forEach { edge ->
            val source = edge.prerequisite
            when {
                edge.lifecycleStatus == EDGE_RETIRED -> return@forEach
                edge.lifecycleStatus == "invalidated" ->
                    problems += "${MetadataProblem.EDGE_INVALIDATED.id}:$source"
                edge.lifecycleStatus == "draft" ->
                    problems += "${MetadataProblem.EDGE_NOT_PUBLISHED.id}:$source"
                edge.lifecycleStatus !in EDGE_IN_EFFECT ->
                    problems += "${MetadataProblem.UNKNOWN_EDGE_LIFECYCLE.id}:$source:${edge.lifecycleStatus}"
                edge.strictnessProfile != DEFAULT_STRICTNESS_PROFILE ->
                    problems += "${MetadataProblem.UNKNOWN_STRICTNESS_PROFILE.id}:$source:${edge.strictnessProfile}"
                source == candidate.target ->
                    problems += "${MetadataProblem.SELF_REQUIREMENT.id}:$source"
                else -> requirements += PrerequisiteRequirement(
                    skill = source,
                    kind = EdgeKind.of(edge.edgeKind) ?: EdgeKind.HARD,
                    source = RequirementSource.GRAPH_EDGE,
                )
            }
        }
        candidate.requiredSkills.forEach { skill ->
            when {
                skill == candidate.target -> problems += "${MetadataProblem.SELF_REQUIREMENT.id}:$skill"
                !isPublished(skill) -> problems += "${MetadataProblem.UNPUBLISHED_REQUIRED_SKILL.id}:$skill"
                else -> requirements += PrerequisiteRequirement(skill, EdgeKind.HARD, RequirementSource.TASK_REQUIRED)
            }
        }
        return Requirements(requirements, problems.distinct())
    }

    /**
     * Whether [target] sits on a cycle of edges in force (§21: a cycle is an invalid graph). The walk
     * reads each Skill's incoming edges at most once, so it is bounded by the graph, not by the
     * number of paths through it.
     */
    fun onCycle(target: VersionedRef, edgesInto: (VersionedRef) -> List<PrerequisiteEdge>): Boolean {
        val seen = mutableSetOf<VersionedRef>()
        val pending = ArrayDeque(sourcesInForce(edgesInto(target), target))
        while (pending.isNotEmpty()) {
            val skill = pending.removeFirst()
            if (skill == target) return true
            if (!seen.add(skill)) continue
            pending.addAll(sourcesInForce(edgesInto(skill), skill))
        }
        return false
    }

    private fun sourcesInForce(edges: List<PrerequisiteEdge>, target: VersionedRef): List<VersionedRef> =
        latestEdges(edges.filter { it.target == target })
            .filter { it.lifecycleStatus in EDGE_IN_EFFECT }
            .map { it.prerequisite }

    /**
     * `PRG-v0` §4 and §5 applied to one candidate.
     *
     * | readiness    | hard, normal          | hard, strict   | soft                  |
     * |--------------|-----------------------|----------------|-----------------------|
     * | ready        | eligible              | eligible       | eligible              |
     * | ready_due    | eligible              | eligible       | eligible              |
     * | uncertain    | conditional_eligible  | blocked        | eligible_with_support |
     * | not_ready    | blocked               | blocked        | eligible_with_support |
     *
     * Strict means the prerequisite Skill is a `critical_prerequisite` or the candidate asked for
     * strict confidence (§4.2). A Skill needed both ways is needed hard. A readiness that was not
     * supplied is treated as `not_ready`: the gate fails closed, never open.
     */
    fun decide(
        candidate: PrerequisiteCandidate,
        requirements: Requirements,
        readiness: Map<VersionedRef, SkillReadiness>,
        criticalPrerequisite: (VersionedRef) -> Boolean,
    ): PrerequisiteDecision {
        val kinds = requirements.requirements
            .groupBy { it.skill }
            .mapValues { (_, all) -> if (all.any { it.kind == EdgeKind.HARD }) EdgeKind.HARD else EdgeKind.SOFT }
            .toList()
            .sortedWith(compareBy({ it.first.logicalId }, { it.first.version }))

        val hardBlockers = mutableListOf<VersionedRef>()
        val uncertain = mutableListOf<VersionedRef>()
        val softGaps = mutableListOf<VersionedRef>()
        val reviewDue = mutableListOf<VersionedRef>()
        val reasons = linkedSetOf<PrerequisiteReason>()
        var strictUncertain = false
        var conditional = false

        kinds.forEach { (skill, kind) ->
            val state = readiness[skill]
            val value = state?.readiness ?: PrerequisiteReadiness.NOT_READY
            val strict = candidate.requiresStrictPrerequisiteConfidence || criticalPrerequisite(skill)
            when (kind) {
                EdgeKind.HARD -> when (value) {
                    PrerequisiteReadiness.READY -> Unit
                    PrerequisiteReadiness.READY_DUE -> {
                        reviewDue += skill
                        reasons += PrerequisiteReason.ELIGIBLE_PREREQUISITE_REVIEW_DUE_NOT_BLOCKING
                    }
                    PrerequisiteReadiness.UNCERTAIN -> {
                        uncertain += skill
                        if (strict) {
                            strictUncertain = true
                            reasons += PrerequisiteReason.BLOCKED_CRITICAL_PREREQUISITE_VERIFICATION_DUE
                        } else {
                            conditional = true
                            reasons += PrerequisiteReason.CONDITIONAL_PREREQUISITE_UNCERTAIN
                        }
                    }
                    PrerequisiteReadiness.NOT_READY -> {
                        hardBlockers += skill
                        reasons += if (state?.remediationRequired == true) {
                            PrerequisiteReason.BLOCKED_PREREQUISITE_REMEDIATION_REQUIRED
                        } else {
                            PrerequisiteReason.BLOCKED_MISSING_HARD_PREREQUISITE
                        }
                    }
                }
                EdgeKind.SOFT -> when (value) {
                    PrerequisiteReadiness.READY -> Unit
                    PrerequisiteReadiness.READY_DUE -> {
                        reviewDue += skill
                        reasons += PrerequisiteReason.ELIGIBLE_PREREQUISITE_REVIEW_DUE_NOT_BLOCKING
                    }
                    PrerequisiteReadiness.UNCERTAIN, PrerequisiteReadiness.NOT_READY -> {
                        softGaps += skill
                        reasons += PrerequisiteReason.ELIGIBLE_WITH_SOFT_PREREQUISITE_GAP
                    }
                }
            }
        }

        val eligibility = when {
            requirements.problems.isNotEmpty() -> PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA
            hardBlockers.isNotEmpty() || strictUncertain -> PrerequisiteEligibility.BLOCKED
            conditional -> PrerequisiteEligibility.CONDITIONAL_ELIGIBLE
            softGaps.isNotEmpty() -> PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT
            else -> PrerequisiteEligibility.ELIGIBLE
        }

        val considered = kinds.mapNotNull { readiness[it.first] }
        return PrerequisiteDecision(
            candidateId = candidate.candidateId,
            target = candidate.target,
            eligibility = eligibility,
            hardBlockerSkills = hardBlockers,
            uncertainSkills = uncertain,
            softGapSkills = softGaps,
            reviewDueSkills = reviewDue,
            metadataProblems = requirements.problems,
            readinessSnapshotRefs = considered.mapNotNull { it.snapshotRef }.distinct(),
            requiresStrictPrerequisiteConfidence = candidate.requiresStrictPrerequisiteConfidence,
            prerequisitePolicyVersion = PREREQUISITE_POLICY_VERSION,
            reasonInputs = reasons.toList(),
            notes = considered.flatMap { it.notes }.distinct(),
        )
    }
}
