package coach.model

/**
 * The Topic state (16C, `PWPX-v0`): `TSM-v0`'s six states, derived — never stored as a second truth and never a score.
 *
 * **A Topic's state is not its mastery** (`TSM-v0` §1). It is an orchestration and orientation answer read from what the
 * canonical engines already say about the Topic's Skills, from whether the learner has worked in the Topic at all, and
 * from whether each of its Objectives was taught or diagnostically waived. It is never an average of Skills
 * (`SPWX-v0` §6), never a percentage, and never a prerequisite claim: whether new work on a Skill waits is the gate's
 * answer for that Skill, and the Topic only reads it.
 *
 * It lives in `core-model` for the reason [SkillPresentationProjection] does: the read path and the screen need the
 * very same function, and `MSBX-v0` lets neither depend on the other.
 */

/** `TSM-v0` §2's six states, with their `SPWX-v0` §6 labels kept in the presentation layer. No seventh exists. */
enum class TopicState(val id: String) {
    LOCKED("locked"),
    AVAILABLE("available"),
    LEARNING("learning"),
    MASTERED("mastered"),
    WEAKENING("weakening"),
    REMEDIATION_REQUIRED("remediation_required"),
}

/**
 * How one of the Topic's Objectives is covered (`TSM-v0` §12; user decision, 2026-10-24): the lesson was worked — an
 * attempt was recorded on an item of a teach task targeting it — or a 13F diagnostic waived it. Coverage is a separate
 * gate from mastery, which is exactly what keeps "finished the lesson" from reading as "learned it".
 */
enum class TopicCoverage(val id: String, val covered: Boolean) {
    LESSON_WORKED("lesson_worked", true),
    DIAGNOSTIC_WAIVER("diagnostic_waiver", true),
    NOT_COVERED("not_covered", false),
    /** No teach task targets this Objective, so nothing can say it was taught; it is never assumed covered. */
    NO_LESSON_AUTHORED("no_lesson_authored", false),
}

/**
 * One Skill of the Topic as the Topic reads it: its own projection (16A's one function) and the gate's answer for new
 * work on it. A Topic can be entered when **one** of its Skills can be started (user decision, 2026-10-24, revised the
 * same day: the first rule — every outside edge satisfied — showed 12 of the 27 shipped Topics as locked while the
 * planner offered one of their Skills). What a locked Topic waits for is named by the hard prerequisites from Skills
 * outside it; edges between its own Skills are its internal order.
 */
data class TopicMember(
    val skill: VersionedRef,
    val projection: SkillStateProjection,
    /** The gate lets new work on this Skill start now (`PRG-v0` §7): `eligibility.waits` is false. */
    val startable: Boolean,
    val externalBlockers: List<VersionedRef>,
    val externalUncertain: List<VersionedRef>,
    /** The gate could not read this Skill's prerequisite metadata; it fails closed, so the Topic does too. */
    val metadataProblem: Boolean,
    val objectives: List<Pair<VersionedRef, TopicCoverage>>,
)

/**
 * Everything one Topic's state is derived from. [started] is whether any work on the Topic was recorded — an attempt on
 * an item of one of its Skills, or evidence on one of its Objectives. Truth is append-only, so a started Topic stays
 * started, and `TSM-v0` §10's invariant follows: a started Topic never goes back to `locked`.
 */
data class TopicReading(
    val topic: VersionedRef,
    val started: Boolean,
    val members: List<TopicMember>,
)

/** The derivation's answer: a state with the facts behind it, or "cannot be told yet" — never a guessed state. */
sealed interface TopicProjection {
    val reading: TopicReading

    data class Derived(
        val state: TopicState,
        /** `TSM-v0` §14's reason code for the state, where it names one; `locked` has none of its own. */
        val reasonCode: String?,
        /** Outside Skills a locked Topic waits for, by the gate's rule (blocked or uncertain); empty unless locked. */
        val waitingFor: List<VersionedRef>,
        val metadataProblem: Boolean,
        /** Skills the mastery engine has not confirmed. */
        val notYetConfirmed: List<VersionedRef>,
        /** Objectives neither taught nor waived. */
        val notCovered: List<VersionedRef>,
        /** Confirmed Skills with a retention or verification signal (`TSM-v0` §8): never elapsed time alone. */
        val retentionSignal: List<VersionedRef>,
        /** Skills with a confirmed, localized remediation need. */
        val remediation: List<VersionedRef>,
        override val reading: TopicReading,
    ) : TopicProjection

    /** A started Topic with a Skill whose own state cannot be told yet (16A). The projection's status, not a state. */
    data class Recomputing(val skills: List<VersionedRef>, override val reading: TopicReading) : TopicProjection
}

object TopicStateProjection {

    /** `TSM-v0` §14's reason codes for the states that have one. */
    val reasonCodes: Map<TopicState, String> = mapOf(
        TopicState.AVAILABLE to "hard_prerequisites_satisfied",
        TopicState.LEARNING to "topic_started",
        TopicState.MASTERED to "mastery_gate_satisfied",
        TopicState.WEAKENING to "retention_risk_confirmed",
        TopicState.REMEDIATION_REQUIRED to "remediation_triggered",
    )

    /**
     * `TSM-v0` §10, the first rule that holds wins. An unstarted Topic is `available` when one of its Skills can be
     * started and `locked` when none can — so Progress never calls a Topic locked while Today offers one of its Skills. A
     * started Topic is decided by its Skills and its coverage, and its entry is no longer read at all.
     */
    fun derive(reading: TopicReading): TopicProjection {
        val waitingFor = reading.members.flatMap { it.externalBlockers + it.externalUncertain }.distinct()
        val metadata = reading.members.any { it.metadataProblem }
        fun derived(
            state: TopicState,
            notYetConfirmed: List<VersionedRef> = emptyList(),
            notCovered: List<VersionedRef> = emptyList(),
            retentionSignal: List<VersionedRef> = emptyList(),
            remediation: List<VersionedRef> = emptyList(),
        ) = TopicProjection.Derived(
            state = state,
            reasonCode = reasonCodes[state],
            waitingFor = if (state == TopicState.LOCKED) waitingFor else emptyList(),
            metadataProblem = state == TopicState.LOCKED && metadata,
            notYetConfirmed = notYetConfirmed,
            notCovered = notCovered,
            retentionSignal = retentionSignal,
            remediation = remediation,
            reading = reading,
        )

        // 10.1 — not started: only the entry decides, and one startable Skill opens it.
        if (!reading.started) {
            return derived(if (reading.members.any { it.startable }) TopicState.AVAILABLE else TopicState.LOCKED)
        }
        // A Skill whose state cannot be told makes the Topic's untellable too; no state is guessed around it.
        val recomputing = reading.members.filter { it.projection is SkillStateProjection.Recomputing }.map { it.skill }
        if (recomputing.isNotEmpty()) return TopicProjection.Recomputing(recomputing, reading)

        val members = reading.members.map { it to (it.projection as SkillStateProjection.Derived) }
        val remediation = members.filter { (_, p) -> p.state == SkillPresentationState.REMEDIATION_REQUIRED }.map { it.first.skill }
        val notYetConfirmed = members.filterNot { (_, p) -> confirmed(p) }.map { it.first.skill }
        val notCovered = reading.members.flatMap { m -> m.objectives.filterNot { it.second.covered }.map { it.first } }
        val retentionSignal = members.filter { (_, p) -> confirmed(p) && retentionSignal(p) }.map { it.first.skill }
        return when {
            // 10.2.1 — a confirmed, localized remediation need. Not a penalty; coverage and history stay.
            remediation.isNotEmpty() -> derived(TopicState.REMEDIATION_REQUIRED, notYetConfirmed, notCovered, retentionSignal, remediation)
            // 10.2.2 / 10.2.3 — every Skill confirmed and every Objective covered; a retention signal on one of them is
            // `weakening`. Mastery does not decay with time (`GRE-v0`), so "was mastered" is read now, not remembered.
            notYetConfirmed.isEmpty() && notCovered.isEmpty() ->
                if (retentionSignal.isNotEmpty()) derived(TopicState.WEAKENING, retentionSignal = retentionSignal)
                else derived(TopicState.MASTERED)
            // 10.2.4
            else -> derived(TopicState.LEARNING, notYetConfirmed, notCovered, retentionSignal)
        }
    }

    /** The mastery engine has confirmed the Skill; an open verification keeps the confirmation (`GRE-v0` §16.2). */
    private fun confirmed(p: SkillStateProjection.Derived): Boolean =
        p.reading.mastery == MasteryAxisState.CONFIRMED_CURRENT || p.reading.mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE

    /**
     * `TSM-v0` §8's "retention risk": `at_risk` (a mixed or repeated signal) or an open verification. A scheduled review
     * is an opportunity, never a decline (`RVR-v0`, `SPWX-v0` §15), so `review_due` alone keeps the Topic `mastered`.
     */
    private fun retentionSignal(p: SkillStateProjection.Derived): Boolean =
        p.reading.retention == RetentionAxis.AT_RISK || p.reading.retention == RetentionAxis.VERIFICATION_DUE ||
            p.reading.mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE
}

/** One Topic as Progress and `topic_detail` read it. */
data class TopicRow(
    val topic: VersionedRef,
    val name: String,
    val projection: TopicProjection,
)

/** Every Topic the curriculum places Skills in, in a stable order. */
data class TopicFacts(
    val curriculumLoaded: Boolean,
    val topics: List<TopicRow>,
)

/** `topic_detail` (`SPWX-v0` §9): the Topic's state and its Skill structure, linking each Skill to `skill_detail`. */
data class TopicDetailFacts(
    val row: TopicRow,
    val members: List<SkillProgressRow>,
    /** The names of every Skill the detail names — its own and the outside ones its entry waits for. */
    val skillNames: Map<VersionedRef, String>,
)
