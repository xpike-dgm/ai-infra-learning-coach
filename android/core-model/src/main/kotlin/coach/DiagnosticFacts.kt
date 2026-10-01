package coach.model

/**
 * `VDW-v0 / D-037` as the diagnostic waiver reads and writes it (13F).
 *
 * A diagnostic is **not** an easier road to mastery. It collects the same `GRE-v0` evidence through the same
 * pipeline, only sooner: when the gates of one Objective first pass on evidence from a diagnostic, the starting
 * instruction for that Objective is waived — and only for that Objective. The learner saying "I know this" can
 * open a diagnostic; it is never evidence, and it never waives anything.
 *
 * A waiver is coverage, not competence: it says why the starting lesson is no longer required, never that the
 * learner still knows it. Current competence is always the mastery and retention engines'.
 */

/**
 * `VDW-v0` §3: what opened a diagnostic. Only the learner can (13F, user decision): the three sources here are
 * all the learner asking. `planner_diagnostic_opportunity` needs a calibrated decision-value rule (18B) and
 * `curriculum_entry_placement` an entry flow (16D); neither is representable until its owner builds it.
 */
enum class DiagnosticSource(val id: String) {
    USER_REQUESTED_FAST_PATH("user_requested_fast_path"),
    PRIOR_EXPERIENCE_CLAIM("prior_experience_claim"),
    RESUME_AFTER_EXTERNAL_LEARNING("resume_after_external_learning"),
    ;

    /** `PDT-v0` §8.7: every source here is the learner asking for the fast path, so the reason is one code. */
    val reasonCode: String get() = DiagnosticCodes.USER_REQUESTED_FAST_PATH
}

/** `PDT-v0` §8.7, the closed diagnostic family as the code writes it. */
object DiagnosticCodes {
    const val USER_REQUESTED_FAST_PATH = "diagnostic.user_requested_fast_path"
    const val PROBE_SELECTED = "diagnostic.probe_selected"
    const val CONFIRM_NEEDED = "diagnostic.confirm_needed"
    const val CRITICAL_CONFIRM_NEEDED = "diagnostic.critical_confirm_needed"
    const val PARTIAL_COVERAGE_WAIVER = "diagnostic.partial_coverage_waiver"
    const val FULL_COVERAGE_WAIVER = "diagnostic.full_coverage_waiver"
    const val NO_WAIVER = "diagnostic.no_waiver"
    const val H0_REQUIRED_FOR_WAIVER = "diagnostic.h0_required_for_waiver"
    const val PREREQUISITE_BLOCKED = "diagnostic.prerequisite_blocked"
}

/** `VDW-v0` §5 `diagnostic_stage`: how the next check is routed. Never a mastery value. */
enum class DiagnosticStage(val id: String, val reasonCode: String) {
    PROBE("probe", DiagnosticCodes.PROBE_SELECTED),
    CONFIRM("confirm", DiagnosticCodes.CONFIRM_NEEDED),
    CRITICAL_CONFIRM("critical_confirm", DiagnosticCodes.CRITICAL_CONFIRM_NEEDED),
    TRANSFER_CONFIRM("transfer_confirm", DiagnosticCodes.CONFIRM_NEEDED),
}

/**
 * Where one Objective of the active diagnostic stands. Only the first two are open; the rest end the fast path
 * for that Objective, and none of them is a failure:
 *
 * - [WAIVED]: its gates first passed on diagnostic evidence; the starting instruction is not required.
 * - [ALREADY_DEMONSTRATED]: its gates already pass without a waiver; there is nothing to test (`VDW-v0` §17).
 * - [NOT_DEMONSTRATED]: a clean, independent answer in this diagnostic did not show it; it goes back to normal
 *   learning, and that is not remediation (`VDW-v0` §12.1).
 * - [ASSISTANCE_ENDED_FAST_PATH]: help was taken or a solution seen; the fast path ends for it (13F, user
 *   decision) and normal learning continues — asking for help is never penalised.
 */
enum class DiagnosticObjectiveState(val id: String, val open: Boolean) {
    PROBE_NEEDED("probe_needed", true),
    CONFIRM_NEEDED("confirm_needed", true),
    WAIVED("waived", false),
    ALREADY_DEMONSTRATED("already_demonstrated", false),
    NOT_DEMONSTRATED("not_demonstrated", false),
    ASSISTANCE_ENDED_FAST_PATH("assistance_ended_fast_path", false),
}

/** One required Objective inside a diagnostic, pinned as the curriculum published it when it was requested. */
data class DiagnosticTarget(
    val objective: VersionedRef,
    val skill: VersionedRef,
    val critical: Boolean,
)

/**
 * The learner's request, stored as the content of one `daily` assessment session (`diagnostic_scope/1`). The
 * scope is Objectives and Skills, never a Topic (`VDW-v0` §4): a Topic is orchestration, and whether its
 * coverage is complete is read from its Objectives' waivers by the Topic state's owner (16C).
 */
data class DiagnosticScope(
    val source: DiagnosticSource,
    val studyDay: String,
    val curriculumVersion: Int,
    val targets: List<DiagnosticTarget>,
) {
    init {
        require(targets.isNotEmpty()) { "a diagnostic checks at least one Objective" }
        require(targets.map { it.objective }.distinct().size == targets.size) { "an Objective is checked once per diagnostic" }
    }

    val skills: List<VersionedRef> get() = targets.map { it.skill }.distinct()

    fun target(objective: VersionedRef): DiagnosticTarget? = targets.firstOrNull { it.objective == objective }
}

/**
 * What the newest `daily` session row says. The newest row decides whether a diagnostic is active: a new
 * request replaces an open one (no debt is left behind), and a withdrawal ends it — the diagnostic is never
 * mandatory (`VDW-v0` §3).
 */
sealed interface DiagnosticRecord {
    data class Request(val scope: DiagnosticScope) : DiagnosticRecord
    data class Withdrawal(val withdrawnSessionId: Long, val studyDay: String) : DiagnosticRecord
}

/**
 * `VDW-v0` §9's coverage waiver. [sourceEvidenceIds] are the evidence rows of the window whose gates passed;
 * a waiver that cannot name them does not exist.
 */
data class CoverageWaiver(
    val objective: VersionedRef,
    val skill: VersionedRef,
    val sessionId: Long,
    val sourceEvidenceIds: List<Long>,
    val grantedAtSequence: Long,
    val grantedOnStudyDay: String,
) {
    init {
        require(sourceEvidenceIds.isNotEmpty()) { "a waiver names the evidence that validated it" }
    }

    /** `VDW-v0` §9: the only reason a waiver is ever issued. */
    val reason: String get() = REASON

    companion object {
        const val REASON = "validated_prior_knowledge"
    }
}

/** One Objective as the diagnostic sees it now. */
data class ObjectiveDiagnosis(
    val target: DiagnosticTarget,
    val state: DiagnosticObjectiveState,
    val stage: DiagnosticStage?,
    val failedGates: List<String>,
    val windowVariantFamilies: List<String>,
    val windowDependencyGroups: List<String>,
    val reasonCodes: List<String>,
    val waiver: CoverageWaiver?,
) {
    init {
        require(state.open == (stage != null)) { "only an open Objective has a next stage" }
        require((state == DiagnosticObjectiveState.WAIVED) == (waiver != null)) { "a waived Objective names its waiver, and only a waived one" }
    }
}

/** `VDW-v0` §10. A diagnostic's outcome is about coverage, never a grade. */
enum class WaiverOutcome(val id: String, val reasonCode: String) {
    FULL("full_validated_waiver", DiagnosticCodes.FULL_COVERAGE_WAIVER),
    PARTIAL("partial_validated_waiver", DiagnosticCodes.PARTIAL_COVERAGE_WAIVER),
    NONE("no_waiver", DiagnosticCodes.NO_WAIVER),
}

/**
 * A diagnostic's result. While any Objective is still open the result is [inProgress]: what is waived so far is
 * stated, and nothing is called "not shown" before it was actually checked.
 */
data class DiagnosticResult(
    val sessionId: Long,
    val scope: DiagnosticScope,
    val objectives: List<ObjectiveDiagnosis>,
    val outcome: WaiverOutcome,
    val inProgress: Boolean,
    val reasonCodes: List<String>,
) {
    val waived: List<ObjectiveDiagnosis> get() = objectives.filter { it.state == DiagnosticObjectiveState.WAIVED }
}

/**
 * How a planner treats a teaching task whose Objectives are covered (`VDW-v0` §17): [waived] coverage resolves
 * the lesson, an Objective still in the learner's fast path holds it until the diagnostic has looked. The
 * reason is a `PDT-v0` §8.7 code.
 */
data class CoverageHold(val waived: Boolean, val reasonCode: String)

/**
 * `diagnostic_scope/1`, the text one `daily` session row stores. Like every stored format here it is strict: an
 * unknown version, section, field or value decodes to `null`, never to a guess.
 */
object DiagnosticScopeCodec {
    const val FORMAT = "diagnostic_scope/1"

    fun encode(record: DiagnosticRecord): String = when (record) {
        is DiagnosticRecord.Request -> buildList {
            add(FORMAT)
            with(record.scope) {
                add(CodecText.line("request", "source" to source.id, "study_day" to studyDay,
                    "curriculum_version" to curriculumVersion.toString()))
                targets.forEach {
                    add(CodecText.line("target", "objective" to it.objective.toString(), "skill" to it.skill.toString(),
                        "critical" to it.critical.toString()))
                }
            }
        }.joinToString("\n")
        is DiagnosticRecord.Withdrawal -> listOf(
            FORMAT,
            CodecText.line("withdrawal", "session" to record.withdrawnSessionId.toString(), "study_day" to record.studyDay),
        ).joinToString("\n")
    }

    fun decode(stored: String): DiagnosticRecord? = runCatching { decodeOrThrow(stored) }.getOrNull()

    private fun decodeOrThrow(stored: String): DiagnosticRecord {
        val lines = stored.split("\n")
        if (lines.firstOrNull() != FORMAT || lines.size < 2) throw CodecText.Malformed()
        val (section, head) = CodecText.fields(lines[1])
        fun v(fields: Map<String, String>, key: String) = fields[key] ?: throw CodecText.Malformed()
        return when (section) {
            "withdrawal" -> {
                if (lines.size != 2 || head.keys != setOf("session", "study_day")) throw CodecText.Malformed()
                DiagnosticRecord.Withdrawal(v(head, "session").toLongOrNull() ?: throw CodecText.Malformed(), v(head, "study_day"))
            }
            "request" -> {
                if (head.keys != setOf("source", "study_day", "curriculum_version")) throw CodecText.Malformed()
                val targets = lines.drop(2).map { raw ->
                    val (kind, f) = CodecText.fields(raw)
                    if (kind != "target" || f.keys != setOf("objective", "skill", "critical")) throw CodecText.Malformed()
                    DiagnosticTarget(CodecText.ref(v(f, "objective")), CodecText.ref(v(f, "skill")), CodecText.bool(v(f, "critical")))
                }
                DiagnosticRecord.Request(
                    DiagnosticScope(
                        source = DiagnosticSource.entries.firstOrNull { it.id == v(head, "source") } ?: throw CodecText.Malformed(),
                        studyDay = v(head, "study_day").ifEmpty { throw CodecText.Malformed() },
                        curriculumVersion = v(head, "curriculum_version").toIntOrNull() ?: throw CodecText.Malformed(),
                        targets = targets,
                    )
                )
            }
            else -> throw CodecText.Malformed()
        }
    }
}
