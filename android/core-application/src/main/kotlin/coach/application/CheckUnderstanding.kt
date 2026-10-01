package coach.application

import coach.model.AssessmentItem
import coach.model.AttemptSubmission
import coach.model.Comprehension
import coach.model.ComprehensionCheck
import coach.model.ComprehensionOffer
import coach.model.ComprehensionResult
import coach.model.TutorIntent
import coach.model.TutorOutcome
import coach.model.TutorRequest
import coach.ports.ContentPort

/**
 * The comprehension check after code the learner did not write alone (14E, `ACCX-v0 / D-109`).
 *
 * Offered right after such a submission and always skippable (user decision). Written checks come first and are
 * judged by their key; this decides what an answer proves and records nothing — the caller hands a
 * [ComprehensionResult.Measured] to the evidence pipeline under the check's own evidence type, and a skip writes
 * nothing. Only where nothing is written may the tutor ask about the learner's own code (user decision): that is
 * help, recorded as help by [AskTutor] (`TUTX-v0`), and never evidence.
 */
class CheckUnderstanding(
    private val content: ContentPort,
    private val ask: AskTutor,
) {
    fun offer(item: AssessmentItem, submission: AttemptSubmission): ComprehensionOffer {
        require(submission.resource == item.ref) { "the submission answered another item" }
        return Comprehension.offer(submission, content.comprehensionChecksFor(item.ref))
    }

    /** One answer to one written check; [chosen] is the learner's choice, or nothing if they skipped it. */
    fun answer(item: AssessmentItem, check: ComprehensionCheck, chosen: String?): ComprehensionResult {
        require(check in content.comprehensionChecksFor(item.ref)) { "only a check written for this item version is answered" }
        return Comprehension.answer(item, check, chosen)
    }

    /**
     * The tutor's practice question, or its response to the learner's answer. `null` where a written check exists:
     * written first. [request] must be a prepared `check_understanding` ask about this item's submitted code.
     */
    fun practice(item: AssessmentItem, request: TutorRequest, itemRef: AskTutor.Item? = null, attemptId: Long? = null): TutorOutcome? {
        require(request.intent == TutorIntent.CHECK_UNDERSTANDING) { "practice is asked as check_understanding" }
        if (content.comprehensionChecksFor(item.ref).isNotEmpty()) return null
        return ask.ask(request, itemRef, attemptId = attemptId)
    }
}
