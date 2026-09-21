package coach.presentation

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentIntent
import coach.model.AssessmentScope
import coach.model.AssistanceLevel
import coach.model.IndependenceMode
import coach.model.UseCeiling
import coach.model.VersionedRef

/**
 * `ASUX-v0 / D-071` as pure functions: **an assessment session is an evidence-collection workflow.**
 * It never becomes a gradebook, a score-based mastery authority, a pass/fail verdict or a second
 * state engine.
 *
 * There is exactly **one** interior. The daily micro assessment is its first user (11D); weekly and
 * monthly differ only in blueprint composition, which belongs to 13. `assessment_scope` is
 * displayed context here and nothing else, so a "monthly" label cannot raise what an item proves.
 */

/** The nineteen semantic states `ASUX-v0` defines, in its order. */
enum class SessionState(val id: String) {
    ENTERING_REVALIDATING("entering_revalidating"),
    BLOCKED_NOT_STARTABLE("blocked_not_startable"),
    SESSION_ORIENTATION("session_orientation"),
    ITEM_ACTIVE("item_active"),
    ASSISTANCE_OPEN("assistance_open"),
    SUBMITTING_BOUNDARY("submitting_boundary"),
    BOUNDARY_FROZEN("boundary_frozen"),
    BLOCK_CHECKPOINT("block_checkpoint"),
    SESSION_PAUSED("session_paused"),
    RESUME_REVALIDATING("resume_revalidating"),
    SLOT_RECOMPOSED("slot_recomposed"),
    EVALUATION_PENDING("evaluation_pending"),
    RESULT_READY("result_ready"),
    RESULT_PARTIAL("result_partial"),
    ITEM_CONTESTED("item_contested"),
    OFFLINE_LOCAL_CAPABLE("offline_local_capable"),
    AI_UNAVAILABLE_DETERMINISTIC_CORE("ai_unavailable_deterministic_core"),
    ERROR_RECOVERABLE("error_recoverable"),
    DATA_RECOVERY_REQUIRED("data_recovery_required"),
}

/** Tones copied from `VDSX-v0`'s accepted map. Only the two system conditions wear the fault tone. */
val SessionState.tone: Tone
    get() = when (this) {
        SessionState.BLOCKED_NOT_STARTABLE -> Tone.ATTENTION
        SessionState.ITEM_ACTIVE, SessionState.ASSISTANCE_OPEN, SessionState.SUBMITTING_BOUNDARY -> Tone.ACTIVE
        SessionState.EVALUATION_PENDING, SessionState.RESULT_PARTIAL, SessionState.ITEM_CONTESTED ->
            Tone.PENDING_UNRESOLVED
        SessionState.ERROR_RECOVERABLE, SessionState.DATA_RECOVERY_REQUIRED -> Tone.SYSTEM_FAULT
        SessionState.ENTERING_REVALIDATING,
        SessionState.SESSION_ORIENTATION,
        SessionState.BOUNDARY_FROZEN,
        SessionState.BLOCK_CHECKPOINT,
        SessionState.SESSION_PAUSED,
        SessionState.RESUME_REVALIDATING,
        SessionState.SLOT_RECOMPOSED,
        SessionState.RESULT_READY,
        SessionState.OFFLINE_LOCAL_CAPABLE,
        SessionState.AI_UNAVAILABLE_DETERMINISTIC_CORE -> Tone.NEUTRAL
    }

// ---------------------------------------------------------------- blocks and boundaries

/** `ASUX-v0` §5: the unit of submission is the atomic evidence boundary, never a page or a screen. */
enum class BoundaryKind(val id: String) {
    ITEM("item"),
    TESTLET("testlet"),
}

/**
 * One atomic evidence boundary. A testlet carries more than one item and is still **one** boundary:
 * splitting it would break the evidence semantics its items only have together.
 */
data class Boundary(
    val id: String,
    val kind: BoundaryKind,
    val items: List<VersionedRef>,
    val dependencyGroupId: String? = null,
) {
    init {
        require(items.isNotEmpty()) { "a boundary presents at least one item" }
        require(kind == BoundaryKind.TESTLET || items.size == 1) { "an item boundary carries exactly one item" }
    }
}

data class Block(val id: String, val boundaries: List<Boundary>) {
    init {
        require(boundaries.isNotEmpty()) { "a block holds at least one boundary" }
    }
}

/** What a boundary is, right now, inside this session. */
enum class BoundaryStatus(val id: String) {
    /** Open: answerable, navigable, and nothing about it is decided. */
    OPEN("open"),

    /** Submitted and therefore frozen — not revisitable, not editable, not resubmittable. */
    FROZEN("frozen"),

    /** Left unanswered. Not incorrect, not negative evidence, not a penalty. */
    UNSUBMITTED("unsubmitted"),

    /** Replaced by a fresh measurement after one of the five recomposition conditions. */
    RECOMPOSED("recomposed"),

    /** Reported by the learner as wrong or unanswerable; its evidence is held as contested. */
    CONTESTED("contested"),
}

/**
 * The session as the interior sees it. There is deliberately **no** field that could hold a score,
 * a percentage, a grade, a passing threshold or a countdown.
 */
data class AssessmentSessionView(
    val scope: AssessmentScope,
    val intents: List<AssessmentIntent>,
    val blocks: List<Block>,
    val independenceMode: IndependenceMode,
    val allowedTools: AllowedToolsPolicy,
    val state: SessionState,
    val statuses: Map<String, BoundaryStatus> = emptyMap(),
) {
    fun statusOf(boundaryId: String): BoundaryStatus = statuses[boundaryId] ?: BoundaryStatus.OPEN

    /**
     * Orientation, never performance: which boundary of which block the learner is on. It carries
     * no score and no time, because `ASUX-v0` §6 forbids turning position into a verdict.
     */
    fun positionContext(boundaryId: String): String? {
        blocks.forEachIndexed { blockIndex, block ->
            val index = block.boundaries.indexOfFirst { it.id == boundaryId }
            if (index >= 0) {
                return "block ${blockIndex + 1}/${blocks.size}, boundary ${index + 1}/${block.boundaries.size}"
            }
        }
        return null
    }
}

/** Why a submission or a navigation was refused. */
enum class BoundaryRefusal(val id: String) {
    ALREADY_FROZEN("already_frozen"),
    UNKNOWN_BOUNDARY("unknown_boundary"),
    RECOMPOSED_SLOT("recomposed_slot"),
}

sealed interface BoundaryOutcome {
    data class Applied(val session: AssessmentSessionView) : BoundaryOutcome
    data class Refused(val reason: BoundaryRefusal) : BoundaryOutcome
}

object SessionNavigation {

    /**
     * Unsubmitted boundaries stay freely navigable within the session: answering out of order is
     * ordinary exam ergonomics and creates no evidence problem, because nothing is frozen until it
     * is submitted (`ASUX-v0` §7.2).
     */
    fun navigable(session: AssessmentSessionView): List<String> =
        session.blocks.flatMap { it.boundaries }
            .filter { session.statusOf(it.id) == BoundaryStatus.OPEN }
            .map { it.id }

    /** Submission freezes. A frozen boundary can never be revisited, edited or resubmitted. */
    fun submit(session: AssessmentSessionView, boundaryId: String): BoundaryOutcome =
        transition(session, boundaryId, BoundaryStatus.FROZEN)

    /** Skipping is allowed and is not incorrect; it leaves the measurement need unresolved. */
    fun skip(session: AssessmentSessionView, boundaryId: String): BoundaryOutcome =
        transition(session, boundaryId, BoundaryStatus.UNSUBMITTED)

    /**
     * A learner's report that the item was ambiguous, wrong or unanswerable. It does **not** mark
     * the item invalid by itself: the evidence is held as contested pending revalidation, and
     * nothing about the learner's state gets worse for having reported it.
     */
    fun contest(session: AssessmentSessionView, boundaryId: String): BoundaryOutcome =
        transition(session, boundaryId, BoundaryStatus.CONTESTED, allowFrozen = true)

    private fun transition(
        session: AssessmentSessionView,
        boundaryId: String,
        to: BoundaryStatus,
        allowFrozen: Boolean = false,
    ): BoundaryOutcome {
        val known = session.blocks.any { block -> block.boundaries.any { it.id == boundaryId } }
        if (!known) return BoundaryOutcome.Refused(BoundaryRefusal.UNKNOWN_BOUNDARY)
        val current = session.statusOf(boundaryId)
        if (current == BoundaryStatus.FROZEN && !allowFrozen) return BoundaryOutcome.Refused(BoundaryRefusal.ALREADY_FROZEN)
        if (current == BoundaryStatus.RECOMPOSED) return BoundaryOutcome.Refused(BoundaryRefusal.RECOMPOSED_SLOT)
        return BoundaryOutcome.Applied(session.copy(statuses = session.statuses + (boundaryId to to)))
    }
}

// ---------------------------------------------------------------- assistance

/** What an assistance level leaves the attempt able to prove (`ASUX-v0` §8.2). */
enum class AssistanceConsequence(val id: String) {
    ASSISTED("assisted"),
    PRACTICE_ONLY_SOLUTION_EXPOSED("practice_only_solution_exposed"),
    ;

    val producesIndependentMasteryEvidence: Boolean get() = false
    val requiresFreshUnseenItem: Boolean get() = this == PRACTICE_ONLY_SOLUTION_EXPOSED
}

object SessionAssistance {

    /** Help is never blocked inside an assessment; blocking it to force independence is forbidden. */
    const val BLOCKED: Boolean = false

    fun consequenceOf(level: AssistanceLevel): AssistanceConsequence =
        if (level.revealsTargetReasoning) AssistanceConsequence.PRACTICE_ONLY_SOLUTION_EXPOSED
        else AssistanceConsequence.ASSISTED

    /**
     * Taking revealing help on a measuring item converts it to learning **within the same session**.
     * The conversion is returned so the screen can show it; a silent conversion is exactly what
     * `ASUX-v0` §8.3 forbids, and it is never framed as a violation.
     */
    fun convertOnRevealingHelp(level: AssistanceLevel, mode: IndependenceMode): ModeConversion? =
        if (!level.revealsTargetReasoning || mode != IndependenceMode.H0_REQUIRED) null
        else ModeConversion(from = mode, to = IndependenceMode.GUIDED_ALLOWED, raisesIndependentRecheck = true)
}

/** An explicit, non-punitive conversion. The session raises the recheck; it never schedules it. */
data class ModeConversion(
    val from: IndependenceMode,
    val to: IndependenceMode,
    val raisesIndependentRecheck: Boolean,
) {
    val schedulesRecheck: Boolean get() = false
}

// ---------------------------------------------------------------- pause, resume, recomposition

/** The five conditions under which an unresolved slot is recomposed on resume (`ASUX-v0` §9). */
enum class RecompositionCondition(val id: String) {
    SOLUTION_OR_EXPLANATION_EXPOSED("solution_or_explanation_exposed"),
    ITEM_VERSION_OR_VALIDATION_CHANGED("item_version_or_validation_changed"),
    PREREQUISITE_STATE_CHANGED_MEANINGFULLY("prerequisite_state_changed_meaningfully"),
    FRESHNESS_NO_LONGER_TRUSTWORTHY_AFTER_LONG_GAP("freshness_no_longer_trustworthy_after_long_gap"),
    USER_REQUESTED_RESET_OR_ALTERNATIVE("user_requested_reset_or_alternative"),
}

object SessionRecomposition {

    /**
     * Recomposes the unresolved slots the conditions apply to. A frozen boundary is never touched:
     * completed valid evidence is not deleted by recomposition, and a recomposed slot is a fresh
     * measurement rather than a retry of a failure.
     */
    fun recompose(
        session: AssessmentSessionView,
        slots: Map<String, Set<RecompositionCondition>>,
    ): AssessmentSessionView {
        val recomposed = slots.filterValues { it.isNotEmpty() }.keys
            .filter { session.statusOf(it) != BoundaryStatus.FROZEN }
            .associateWith { BoundaryStatus.RECOMPOSED }
        return session.copy(statuses = session.statuses + recomposed)
    }
}

// ---------------------------------------------------------------- result

/** The six semantic families the result is made of (`ASUX-v0` §13.1), in its order. */
enum class ResultFamily(val id: String) {
    CONFIRMED_CAPABILITIES("confirmed_capabilities"),
    VERIFICATION_NEEDED("verification_needed"),
    PERSISTENT_TARGETED_GAPS("persistent_targeted_gaps"),
    RETENTION_REVALIDATED("retention_revalidated"),
    NOT_RELIABLY_MEASURED("not_reliably_measured"),
    PLAN_CHANGES("plan_changes"),
}

/** Why something could not be measured reliably (`ASUX-v0` §13.3), in its order. */
enum class NotReliablyMeasured(val id: String) {
    INVALID_ITEM("invalid_item"),
    PROVISIONAL_EVALUATION("provisional_evaluation"),
    ASSISTED_ATTEMPT("assisted_attempt"),
    SOLUTION_EXPOSED_ATTEMPT("solution_exposed_attempt"),
    CONTESTED_ITEM("contested_item"),
    UNSUBMITTED_SLOT("unsubmitted_slot"),
}

/**
 * Raw counts, if shown at all, are orientation and are kept structurally apart from state — a
 * separate type, so a count can never be read as the session's outcome (`ASUX-v0` §13.2).
 */
data class InformationalCounts(val correct: Int, val incorrect: Int, val partial: Int) {
    val isInformationalOnly: Boolean get() = true
}

/**
 * What just changed. Each family holds plain statements the canonical engines supplied; there is no
 * score, percentage, grade, threshold or pass/fail field, so the forbidden verdicts are not merely
 * discouraged here — they are unrepresentable.
 */
data class SessionResult(
    val families: Map<ResultFamily, List<String>>,
    val notReliablyMeasured: List<NotReliablyMeasured>,
    val counts: InformationalCounts?,
    val partial: Boolean,
) {
    /** `not_reliably_measured` is first class: never hidden, never folded into "incorrect". */
    val showsNotReliablyMeasured: Boolean get() = notReliablyMeasured.isNotEmpty()

    val state: SessionState get() = if (partial) SessionState.RESULT_PARTIAL else SessionState.RESULT_READY
}

object SessionResults {

    /**
     * Builds the result from what actually happened and what canonical state actually said.
     *
     * A state change is claimed **only** when the engines reported one, so today — with no evidence
     * pipeline (12) — a session can report unresolved slots and what could not be measured, and
     * nothing else. Manufacturing a progress claim from an answer is exactly the failure this
     * product exists to avoid.
     */
    fun of(
        session: AssessmentSessionView,
        canonicalChanges: Map<ResultFamily, List<String>> = emptyMap(),
        unreliable: Set<NotReliablyMeasured> = emptySet(),
        counts: InformationalCounts? = null,
    ): SessionResult {
        val statuses = session.blocks.flatMap { it.boundaries }.map { session.statusOf(it.id) }
        val unresolved = statuses.any { it == BoundaryStatus.OPEN || it == BoundaryStatus.UNSUBMITTED }
        val sources = buildSet {
            addAll(unreliable)
            if (statuses.any { it == BoundaryStatus.UNSUBMITTED }) add(NotReliablyMeasured.UNSUBMITTED_SLOT)
            if (statuses.any { it == BoundaryStatus.CONTESTED }) add(NotReliablyMeasured.CONTESTED_ITEM)
        }
        val families = ResultFamily.entries.associateWith { family ->
            canonicalChanges[family].orEmpty()
        }.filterValues { it.isNotEmpty() }

        return SessionResult(
            families = families,
            notReliablyMeasured = NotReliablyMeasured.entries.filter { it in sources },
            counts = counts,
            partial = unresolved,
        )
    }
}

/**
 * The session's fixed sentences whose *meaning* is canonical even though their wording is 14's.
 */
object SessionCopy {
    const val INDEPENDENCE_DISCLOSURE =
        "Bu ölçüm bağımsız çalışmanı görmek için. İzin verilen araçlar aşağıda yazıyor; " +
            "hedefin ölçtüğü aracı kullanmak bağımsızlığı bozmaz."

    const val ASSISTED_CONSEQUENCE =
        "Bu yardım denemeyi yardımlı yapar: bağımsız yetkinlik kanıtı üretmez ve yetkinlik daha " +
            "sonra ayrı bir kontrolle doğrulanabilir."

    const val SOLUTION_EXPOSED_CONSEQUENCE =
        "Bu yardım çözümü gösterir: bu deneme alıştırmaya döner ve aynı soru ya da yakın bir " +
            "varyantı bağımsız kontrol olarak kullanılamaz; daha sonra görmediğin yeni bir soru gelir."

    const val SKIPPED =
        "Bu soruyu boş bıraktın. Bu yanlış sayılmaz ve aleyhine kaydedilmez; ölçülmek istenen şey açık kalır."

    const val CONTESTED =
        "Bildirdiğin için teşekkürler. Soru gözden geçirilecek; bu arada ilgili kanıt itirazlı " +
            "olarak tutuluyor ve durumunu kötü etkilemiyor."

    const val INVALID_ITEM =
        "Bu soru güvenilir biçimde değerlendirilemedi. Ne lehine ne aleyhine sayıldı."

    const val NOTHING_CHANGED =
        "Kanonik durumda bir değişiklik olmadı. Bu oturumdan bir ilerleme iddiası üretilmiyor."
}

/** What an item may carry here, shown to nobody as a score: it is the ceiling, not a grade. */
data class ItemUseDisclosure(val ceiling: UseCeiling, val independenceMode: IndependenceMode) {
    val isScore: Boolean get() = false
}
