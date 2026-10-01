package coach.model

/**
 * `ACCX-v0 / D-109` — the comprehension check after code the learner did not write alone (14E).
 *
 * **Running code someone else wrote proves nothing about the learner; explaining it proves understanding, not
 * production.** When the learner submits code an AI or another source wrote, or that a shown solution largely gave
 * them, a comprehension check is offered right after, and may be skipped (user decision, 2026-10-01). Written checks
 * come first and are judged by their answer key (user decision); only where none is written does the tutor ask about
 * the learner's own code, and that is practice, never evidence. A correct answer is evidence of the kind its author
 * declared — never of the item's own production — so writing code is still shown by a fresh task of its own
 * (`2D` §8 scenario E, §9; `V1_SUCCESS_CRITERIA` SC-011, SC-012).
 */

/** `2D` §9's comprehension questions. "Apply the same logic with other variables" is production, not comprehension. */
enum class ComprehensionKind(val id: String) {
    /** "Bu satır neden gerekli?" */
    LINE_PURPOSE("line_purpose"),

    /** "Bunu kaldırırsak ne olur?" */
    REMOVAL_EFFECT("removal_effect"),

    /** "Buradaki `*p` neyi değiştiriyor?" */
    STATE_EFFECT("state_effect"),

    /** "Bu çözümdeki hatayı bul." */
    FIND_THE_BUG("find_the_bug"),
}

/**
 * One written check, `comprehension.<namespace>.<slug>` (`GNS-v0`), pinned to the item version it follows and to the
 * one Objective whose understanding it checks. Its answer is one of its choices, so it is judged by its key alone.
 * [evidenceType] is what a correct answer is evidence of, as its author declared — never the item's own evidence type.
 */
data class ComprehensionCheck(
    val ref: VersionedRef,
    val item: VersionedRef,
    val objective: VersionedRef,
    val kind: ComprehensionKind,
    val evidenceType: String,
    val prompt: String,
    val choices: Map<String, String>,
    val answer: String,
) {
    init {
        require(ID.matches(ref.logicalId)) { "a check id is comprehension.<namespace>.<slug> (GNS-v0): ${ref.logicalId}" }
        require(evidenceType.isNotBlank()) { "a check declares what its answer is evidence of" }
        require(prompt.isNotBlank()) { "a check asks something" }
        require(choices.size >= 2 && choices.keys.all { it in CHOICE_KEYS }) { "a check offers at least two choices, a to d" }
        require(choices.values.all { it.isNotBlank() }) { "every choice says something" }
        require(answer in choices) { "the answer is one of the choices" }
    }

    companion object {
        val ID = Regex("^comprehension(\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")
        val CHOICE_KEYS = listOf("a", "b", "c", "d")
    }
}

/** Where the check comes from, if one is offered. */
sealed interface ComprehensionOffer {
    /** The learner wrote the code: nothing to check here (their own work is evaluated as it is, 14D). */
    data object NotOffered : ComprehensionOffer

    /** Written checks, judged by their key: evidence of the kind each declares. */
    data class Written(val checks: List<ComprehensionCheck>) : ComprehensionOffer

    /** Nothing written: the tutor may ask about the learner's own code — practice, never evidence. */
    data object TutorPractice : ComprehensionOffer
}

sealed interface ComprehensionResult {
    /** Recorded by the evidence pipeline like any other evaluation, under the check's own evidence type. */
    data class Measured(val check: ComprehensionCheck, val result: EvaluationResult.Verified, val correct: Boolean) : ComprehensionResult

    /** Nothing chosen: skipping is not a wrong answer, and nothing is written. */
    data object Skipped : ComprehensionResult
}

object Comprehension {

    const val EVALUATOR_PROVIDER = "deterministic"
    const val EVALUATOR_MODEL = "comprehension_key"

    private val beforeTheAnswerFroze = setOf(AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT)

    /**
     * Offered when the learner did not write the code alone, by their own account or by the help they were shown:
     * they said it was generated, copied or co-written, or a partial or full solution to the target was shown before
     * the answer froze. Nothing is inferred from how the code looks (`TRUX-v0` §9).
     */
    fun isOffered(submission: AttemptSubmission): Boolean =
        submission.provenance == ProvenanceOrigin.GENERATED_OR_COPIED ||
            submission.provenance == ProvenanceOrigin.MIXED_AUTHORSHIP ||
            submission.assistance.any { it.scope == AssistanceScope.TARGET_OBJECTIVE && it.timing in beforeTheAnswerFroze && it.level.revealsTargetReasoning }

    /** Written first (user decision): the tutor is offered only where nothing is written for the item. */
    fun offer(submission: AttemptSubmission, written: List<ComprehensionCheck>): ComprehensionOffer = when {
        !isOffered(submission) -> ComprehensionOffer.NotOffered
        written.isNotEmpty() -> ComprehensionOffer.Written(written.sortedBy { it.ref.logicalId })
        else -> ComprehensionOffer.TutorPractice
    }

    /**
     * Judges one answer by the key. A check speaks only for its own Objective, which the item must target, and never as
     * the item's own evidence: understanding someone else's code is not having written it (`2D` §8 scenario E).
     */
    fun answer(item: AssessmentItem, check: ComprehensionCheck, chosen: String?): ComprehensionResult {
        require(check.item == item.ref) { "the check follows another item" }
        require(check.objective in item.targetObjectives) { "a check speaks only for an Objective the item targets" }
        require(check.evidenceType != item.evidenceType) { "a comprehension answer is never evidence of the item's own production" }
        if (chosen.isNullOrBlank()) return ComprehensionResult.Skipped
        require(chosen in check.choices) { "the answer is one of the offered choices" }
        val correct = chosen == check.answer
        return ComprehensionResult.Measured(
            check,
            EvaluationResult.Verified(
                componentResults = listOf(ComponentResult(check.objective, if (correct) OutcomeSignal.MET else OutcomeSignal.NOT_MET)),
                evaluatorRef = EvaluatorRef(EVALUATOR_PROVIDER, EVALUATOR_MODEL, "${check.ref.logicalId}@v${check.ref.version}"),
            ),
            correct,
        )
    }
}
