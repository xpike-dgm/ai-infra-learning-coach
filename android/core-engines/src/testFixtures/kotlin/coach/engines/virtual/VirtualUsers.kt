package coach.engines.virtual

import coach.engines.PlannerEngine
import coach.engines.PrerequisiteEngine
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DailyCapacity
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteEdge
import coach.model.ReadinessInputs
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.VersionedRef

/**
 * The 3H virtual users (`docs/PLANNER_SIMULATION_SUITE.md` §3) as state the real engines read (12F).
 *
 * 3H ran these at policy level, by hand, before any planner code existed. Here each one is Skill state,
 * a prerequisite graph and authored candidates — nothing more — and the plan comes from the real gate
 * ([PrerequisiteEngine]) and the real planner ([PlannerEngine]). A virtual user never names the answer:
 * needs come from Skill state the way the planner opens them, decisions from the gate, bands and
 * selection from the planner. The only needs supplied directly are the ones the planner says come from
 * their owners (the parallel-track cadence and integration opportunities, `PLNX-v0` §5).
 *
 * Defined once, used by the engine, application and presentation tests, so a scenario cannot mean one
 * thing to the planner and another to its explanation.
 */
object VirtualUsers {

    const val STUDY_DAY = "2026-10-01"
    const val ENGLISH_TRACK = "technical_english"

    fun skill(id: String) = VersionedRef("skill.$id", 1)

    val pointer = skill("c.pointer_dereference")
    val linkedList = skill("c.linked_list_insert")
    val cArrays = skill("c.arrays")
    val cFunctions = skill("c.functions")
    val englishErrors = skill("english.error_messages")
    val pythonLoops = skill("python.loops")
    val integrationProject = skill("c.small_cli_project")

    /** One Skill as need generation and the gate read it. `null` weakness means 13 has not written it. */
    data class Learner(
        val skill: VersionedRef,
        val mastery: MasteryAxisState?,
        val retention: RetentionAxis = RetentionAxis.NOT_YET_EVALUATED,
        val remediationRequired: Boolean? = null,
        val critical: Boolean = false,
        val lifecycle: String = "published",
    ) {
        val planning: SkillPlanningState
            get() = SkillPlanningState(skill, lifecycle, critical, mastery, retention,
                weaknessAxis = when (remediationRequired) { true -> "remediation_required"; false -> "none"; null -> null })

        val readiness: ReadinessInputs
            get() = ReadinessInputs(skill, mastery, retention, remediationRequired)
    }

    fun hardEdge(from: VersionedRef, to: VersionedRef, kind: String = "hard") = PrerequisiteEdge(
        prerequisite = from, target = to, edgeVersion = 1, edgeKind = kind, reasonKind = "conceptual",
        strictnessProfile = PrerequisiteEngine.DEFAULT_STRICTNESS_PROFILE, lifecycleStatus = "published",
        provenance = "virtual_user_fixture",
    )

    fun task(
        id: String,
        needKey: String,
        skill: VersionedRef,
        minutes: Int,
        purpose: TaskPurpose = TaskPurpose.PRACTICE,
        status: LifecycleStatus = LifecycleStatus.VALIDATED,
        track: String? = null,
        splittable: Boolean = false,
        chunk: Int? = null,
    ) = TaskCandidate(id, needKey, purpose, "coding", "Görev $id", skill, minutes, status, track = track,
        splittable = splittable, minimumSafeChunkMinutes = chunk)

    fun needKey(trigger: NeedTrigger, skill: VersionedRef) = "${trigger.id}:$skill"

    /** A need the planner says comes from its owner, not from Skill state (`PLNX-v0` §5). */
    fun ownerNeed(trigger: NeedTrigger, skill: VersionedRef, track: String? = null, requiredByCurriculum: Boolean = false) =
        LearningNeed(needKey(trigger, skill), trigger, listOf(skill), Criticality.REQUIRED, track = track,
            continuation = ContinuationValue.FRESH_NEW_CONTEXT, requiredByCurriculum = requiredByCurriculum)

    /**
     * One virtual user on one day: what the engines are given. [plan] runs the real gate for every
     * candidate and then the real planner; nothing here decides anything.
     */
    data class Scenario(
        val id: String,
        val profile: String,
        val capacity: DailyCapacity,
        val learners: List<Learner>,
        val edges: List<PrerequisiteEdge>,
        val candidates: List<TaskCandidate>,
        val ownerNeeds: List<LearningNeed> = emptyList(),
    ) {
        val needs: List<LearningNeed>
            get() = PlannerEngine.needsFromSkillStates(learners.map { it.planning }) + ownerNeeds

        /** The real gate's answer for every candidate, from the graph and the learners' readiness. */
        val decisions: Map<String, PrerequisiteDecision>
            get() {
                val readiness = learners.associate { it.skill to PrerequisiteEngine.readiness(it.readiness) }
                val critical = learners.filter { it.critical }.map { it.skill }.toSet()
                val published = learners.map { it.skill }.toSet() + ownerNeeds.flatMap { it.targetSkills }
                return candidates.associate { c ->
                    val asked = PrerequisiteCandidate(c.id, c.primarySkill, c.requiredSkills, c.requiresStrictPrerequisiteConfidence)
                    val requirements = PrerequisiteEngine.requirements(asked, edges.filter { it.target == c.primarySkill }) { it in published }
                    c.id to PrerequisiteEngine.decide(asked, requirements, readiness) { it in critical }
                }
            }

        fun plan(): PlanTrace = PlannerEngine.plan(capacity, needs, candidates, decisions, STUDY_DAY, 1, 1)
    }

    private fun day(minutes: Int, source: CapacitySource = CapacitySource.NORMAL_PROFILE) = PlannerEngine.capacityOf(source, minutes)

    private fun notStarted(skill: VersionedRef) = Learner(skill, MasteryAxisState.NOT_YET_EVIDENCED)

    // ------------------------------------------------------------------------------------ 3H S01–S13

    /** S01 — U-NOVICE, a 60-minute day: new C learning, the parallel English track and an optional integration. */
    fun s01() = Scenario(
        id = "S01", profile = "U-NOVICE", capacity = day(60),
        learners = listOf(notStarted(cArrays)),
        edges = emptyList(),
        ownerNeeds = listOf(
            ownerNeed(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors, ENGLISH_TRACK),
            ownerNeed(NeedTrigger.INTEGRATION_OPPORTUNITY, integrationProject),
        ),
        candidates = listOf(
            task("c-arrays", needKey(NeedTrigger.NEW_LEARNING, cArrays), cArrays, 20, TaskPurpose.TEACH),
            task("english", needKey(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors), englishErrors, 10, track = ENGLISH_TRACK),
            task("integration", needKey(NeedTrigger.INTEGRATION_OPPORTUNITY, integrationProject), integrationProject, 30),
        ),
    )

    /** S02 — U-BLOCKED: a critical prerequisite has an open verification; Linked List depends on it. */
    fun s02() = Scenario(
        id = "S02", profile = "U-BLOCKED", capacity = day(30),
        learners = listOf(
            Learner(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, critical = true),
            notStarted(linkedList),
        ),
        edges = listOf(hardEdge(pointer, linkedList)),
        ownerNeeds = listOf(ownerNeed(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors, ENGLISH_TRACK)),
        candidates = listOf(
            task("pointer-verify", needKey(NeedTrigger.VERIFICATION_DUE, pointer), pointer, 15, TaskPurpose.ASSESS),
            task("linked-list", needKey(NeedTrigger.NEW_LEARNING, linkedList), linkedList, 15, TaskPurpose.TEACH),
            task("english", needKey(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors), englishErrors, 10, track = ENGLISH_TRACK),
        ),
    )

    /** S03 — U-REVIEW: a mastered critical prerequisite is due for review, with no negative evidence. */
    fun s03() = Scenario(
        id = "S03", profile = "U-REVIEW", capacity = day(30),
        learners = listOf(
            Learner(pointer, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, remediationRequired = false, critical = true),
            notStarted(linkedList),
        ),
        edges = listOf(hardEdge(pointer, linkedList)),
        candidates = listOf(
            task("pointer-review", needKey(NeedTrigger.RETENTION_REVIEW_DUE, pointer), pointer, 10, TaskPurpose.RETAIN),
            task("linked-list", needKey(NeedTrigger.NEW_LEARNING, linkedList), linkedList, 15, TaskPurpose.TEACH),
        ),
    )

    /** S04 — U-TIGHT: 12 planning minutes; the repair does not fit and cannot be split, English does. */
    fun s04() = Scenario(
        id = "S04", profile = "U-TIGHT", capacity = day(14, CapacitySource.TODAY_OVERRIDE),
        learners = listOf(Learner(cFunctions, MasteryAxisState.DEVELOPING_INDEPENDENT, remediationRequired = true)),
        edges = emptyList(),
        ownerNeeds = listOf(ownerNeed(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors, ENGLISH_TRACK)),
        candidates = listOf(
            task("repair", needKey(NeedTrigger.REMEDIATION_REQUIRED, cFunctions), cFunctions, 25, TaskPurpose.REMEDIATE),
            task("english", needKey(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors), englishErrors, 10, track = ENGLISH_TRACK),
        ),
    )

    /**
     * S05 — an 8-minute micro-session: one short retrieval and one heavy new topic. [shortLesson] adds a
     * 4-minute lesson that would fit, which is what makes "nothing new is taught below the minimum
     * block" a rule rather than an accident of size.
     */
    fun s05(shortLesson: Boolean = false) = Scenario(
        id = "S05", profile = "U-TIGHT", capacity = day(8, CapacitySource.TODAY_OVERRIDE),
        learners = listOf(
            Learner(pythonLoops, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, remediationRequired = false),
            notStarted(cArrays),
        ),
        edges = emptyList(),
        candidates = listOf(
            task("loops-retrieval", needKey(NeedTrigger.RETENTION_REVIEW_DUE, pythonLoops), pythonLoops, 5, TaskPurpose.RETAIN),
            task("arrays-lesson", needKey(NeedTrigger.NEW_LEARNING, cArrays), cArrays, 20, TaskPurpose.TEACH),
        ) + if (shortLesson) listOf(task("arrays-micro-lesson", needKey(NeedTrigger.NEW_LEARNING, cArrays), cArrays, 3, TaskPurpose.TEACH))
        else emptyList(),
    )

    /**
     * S07 — U-RETURNING, the day of return: a critical verification that holds dependent work, a critical
     * review, new C learning, English, and [otherDueReviews] more Skills due for review.
     * [reviewsHaveTasks] decides whether those reviews have authored tasks at all.
     */
    fun s07(otherDueReviews: Int = 78, reviewsHaveTasks: Boolean = false): Scenario {
        val due = (1..otherDueReviews).map { skill("python.review_topic_%02d".format(it)) }
        return Scenario(
            id = "S07", profile = "U-RETURNING", capacity = day(50),
            learners = listOf(
                Learner(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, critical = true),
                notStarted(linkedList),
                Learner(cFunctions, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, remediationRequired = false, critical = true),
                notStarted(cArrays),
            ) + due.map { Learner(it, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, remediationRequired = false) },
            edges = listOf(hardEdge(pointer, linkedList)),
            ownerNeeds = listOf(ownerNeed(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors, ENGLISH_TRACK)),
            candidates = listOf(
                task("pointer-verify", needKey(NeedTrigger.VERIFICATION_DUE, pointer), pointer, 15, TaskPurpose.ASSESS),
                task("linked-list", needKey(NeedTrigger.NEW_LEARNING, linkedList), linkedList, 15, TaskPurpose.TEACH),
                task("functions-review", needKey(NeedTrigger.RETENTION_REVIEW_DUE, cFunctions), cFunctions, 10, TaskPurpose.RETAIN),
                task("arrays-lesson", needKey(NeedTrigger.NEW_LEARNING, cArrays), cArrays, 15, TaskPurpose.TEACH),
                task("english", needKey(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors), englishErrors, 10, track = ENGLISH_TRACK),
            ) + if (reviewsHaveTasks) due.map { task("review-${it.logicalId}", needKey(NeedTrigger.RETENTION_REVIEW_DUE, it), it, 5, TaskPurpose.RETAIN) }
            else emptyList(),
        )
    }

    /** S11 — U-AUDIT: a critical verification whose first task is untrusted for high-stakes use. */
    fun s11(withTrustedAlternative: Boolean) = Scenario(
        id = "S11", profile = "U-AUDIT", capacity = day(60),
        learners = listOf(Learner(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, critical = true)),
        edges = emptyList(),
        candidates = listOf(
            task("a-untrusted", needKey(NeedTrigger.VERIFICATION_DUE, pointer), pointer, 10, TaskPurpose.ASSESS, LifecycleStatus.CANDIDATE),
        ) + if (withTrustedAlternative) {
            listOf(task("b-trusted", needKey(NeedTrigger.VERIFICATION_DUE, pointer), pointer, 12, TaskPurpose.ASSESS, LifecycleStatus.TRUSTED))
        } else {
            emptyList()
        },
    )

    /** S12 — U-AUDIT: two alternatives for one retention need. */
    fun s12() = Scenario(
        id = "S12", profile = "U-AUDIT", capacity = day(60),
        learners = listOf(Learner(pointer, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, remediationRequired = false)),
        edges = emptyList(),
        candidates = listOf(
            task("a-coding", needKey(NeedTrigger.RETENTION_REVIEW_DUE, pointer), pointer, 12, TaskPurpose.RETAIN),
            task("b-debugging", needKey(NeedTrigger.RETENTION_REVIEW_DUE, pointer), pointer, 10, TaskPurpose.RETAIN),
        ),
    )

    /** S13 — a healthy critical Skill with only ordinary progress: the label alone must not make P0. */
    fun s13() = Scenario(
        id = "S13", profile = "U-AUDIT", capacity = day(60),
        learners = listOf(
            Learner(pointer, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.STABLE, remediationRequired = false, critical = true),
            Learner(cArrays, MasteryAxisState.DEVELOPING_INDEPENDENT, critical = true),
        ),
        edges = listOf(hardEdge(pointer, cArrays)),
        ownerNeeds = listOf(ownerNeed(NeedTrigger.REINFORCEMENT_OPPORTUNITY, pointer)),
        candidates = listOf(
            task("arrays-continue", needKey(NeedTrigger.CONTINUE_LEARNING, cArrays), cArrays, 20),
            task("pointer-reinforce", needKey(NeedTrigger.REINFORCEMENT_OPPORTUNITY, pointer), pointer, 10),
        ),
    )

    /** Every engine-level scenario, for checks that hold on all of them. */
    fun all(): List<Scenario> = listOf(s01(), s02(), s03(), s04(), s05(), s05(shortLesson = true), s07(), s07(reviewsHaveTasks = true),
        s11(true), s11(false), s12(), s13())
}
