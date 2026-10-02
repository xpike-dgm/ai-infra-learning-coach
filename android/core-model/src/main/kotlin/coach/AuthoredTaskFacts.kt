package coach.model

/**
 * `CPFX-v0 / D-112` — the authored task (15A).
 *
 * **A task is authored content that serves a need the planner already opened; it never opens one, and it never
 * decides what the learner needs.** The planner reads needs from state (12C) and asks the content for candidates;
 * until 15A no format could carry a task, so every need was honestly recorded as having no valid candidate.
 *
 * An authored task is the content side of 3B §15's `TaskCandidate`: it names the one Skill and the Objectives it
 * works on, its single primary purpose (3B §3.1), what the learner does (3B §3.2), the needs it may serve, its
 * authoring estimate of active minutes (uncalibrated; 18B) and exactly which written explanations and items it
 * presents. It carries no priority, no score and nothing about the learner.
 */
data class AuthoredTask(
    val ref: VersionedRef,
    val title: String,
    val primarySkill: VersionedRef,
    val targetObjectives: List<VersionedRef>,
    val purpose: TaskPurpose,
    val activityKind: String,
    val serves: Set<NeedTrigger>,
    val costMinutes: Int,
    val validationStatus: LifecycleStatus,
    val contentOrigin: ContentOrigin,
    val requiredSkills: List<VersionedRef> = emptyList(),
    val explanations: List<VersionedRef> = emptyList(),
    val items: List<VersionedRef> = emptyList(),
    val track: String? = null,
    val splittable: Boolean = false,
    val minimumSafeChunkMinutes: Int? = null,
    val atomicEvidenceBoundary: Boolean = false,
) {
    init {
        require(ID.matches(ref.logicalId)) { "a task id is task.<namespace>.<slug> (GNS-v0): ${ref.logicalId}" }
        require(title.isNotBlank()) { "a task has a title the learner can read" }
        require(targetObjectives.isNotEmpty()) { "a task that names no Objective is never treated as covering one (3B §15)" }
        require(activityKind in TaskServing.ACTIVITY_KINDS) { "unknown activity kind $activityKind (3B §3.2)" }
        require(serves.isNotEmpty()) { "a task serves at least one need" }
        require(serves.all { it in TaskServing.servedBy(purpose) }) {
            "a ${purpose.id} task cannot serve ${serves.filterNot { it in TaskServing.servedBy(purpose) }.map { it.id }}"
        }
        require(explanations.isNotEmpty() || items.isNotEmpty()) { "a task presents something" }
        require(primarySkill !in requiredSkills) { "a task's own Skill is not its prerequisite" }
    }

    /**
     * The planner's candidate for [need], or `null` when this task does not serve it. A task serves a need only if the
     * need's trigger is one it declares **and** the need is about its own Skill: content never widens a need, and it
     * never answers a need about another Skill. The candidate id carries the need, so one task offered to two needs is
     * two candidates rather than one decision shared between them.
     */
    fun candidateFor(need: LearningNeed): TaskCandidate? {
        if (need.trigger !in serves || primarySkill !in need.targetSkills) return null
        return TaskCandidate(
            id = "authored:$ref:${need.needKey}",
            needKey = need.needKey,
            purpose = purpose,
            activityKind = activityKind,
            title = title,
            primarySkill = primarySkill,
            costMinutes = costMinutes,
            validationStatus = validationStatus,
            track = track,
            requiredSkills = requiredSkills,
            splittable = splittable,
            minimumSafeChunkMinutes = minimumSafeChunkMinutes,
            atomicEvidenceBoundary = atomicEvidenceBoundary,
            generationVersion = "authored:$ref",
            targetObjectives = targetObjectives,
        )
    }

    companion object {
        val ID = Regex("^task(\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")
    }
}

/**
 * Which needs a task of each purpose may serve — closed, and taken from the accepted contracts rather than invented:
 * 3B §3.1's purposes and §19's worked examples (`new_learning → teach`, `continue_learning → practice`,
 * `retention_review_due → retain`, `remediation_required → remediate`, `parallel_track_due → practice`), `PBR-v0`'s
 * verification work (`verification_due → assess`), `WLRM-v0`'s targeted repair (`weakness_detected → remediate`) and
 * 3B's `reinforce` (`reinforcement_opportunity`, `integration_opportunity`).
 *
 * `diagnose` serves nothing here: a diagnostic check is composed from the learner's own open diagnostic (13F), never
 * authored as a standing task.
 */
object TaskServing {

    private val SERVES: Map<TaskPurpose, Set<NeedTrigger>> = mapOf(
        TaskPurpose.TEACH to setOf(NeedTrigger.NEW_LEARNING),
        TaskPurpose.PRACTICE to setOf(NeedTrigger.CONTINUE_LEARNING, NeedTrigger.PARALLEL_TRACK_DUE),
        TaskPurpose.ASSESS to setOf(NeedTrigger.VERIFICATION_DUE),
        TaskPurpose.RETAIN to setOf(NeedTrigger.RETENTION_REVIEW_DUE),
        TaskPurpose.REMEDIATE to setOf(NeedTrigger.REMEDIATION_REQUIRED, NeedTrigger.WEAKNESS_DETECTED),
        TaskPurpose.REINFORCE to setOf(NeedTrigger.REINFORCEMENT_OPPORTUNITY, NeedTrigger.INTEGRATION_OPPORTUNITY),
        TaskPurpose.DIAGNOSE to emptySet(),
    )

    fun servedBy(purpose: TaskPurpose): Set<NeedTrigger> = SERVES.getValue(purpose)

    /** 3B §3.2's canonical activity kinds, in its order. */
    val ACTIVITY_KINDS: List<String> = listOf(
        "content_explanation",
        "worked_example",
        "recognition_selection",
        "recall_free_response",
        "code_reading_trace",
        "coding_production",
        "debugging_diagnosis",
        "hands_on_system_task",
        "explanation_justification",
        "transfer_problem",
        "integrated_project_task",
        "language_activity",
    )
}
