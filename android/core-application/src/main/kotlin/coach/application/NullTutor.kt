package coach.application

import coach.model.PendingReason
import coach.model.TutorReply
import coach.model.TutorRequest
import coach.ports.TutorPort

/**
 * Ships with the product, like [NullEvaluator] — not a test fixture (`MSBX-v0` §ai_absence, `TUTX-v0` §15).
 *
 * With no adapter the tutor gives no answer, and that is a capability fact, not a fault: authored help is
 * still shown where the content has it, nothing is recorded for help that was not shown, and nothing
 * leaves the device.
 */
object NullTutor : TutorPort {
    override fun assist(request: TutorRequest): TutorReply = TutorReply.NotDelivered(PendingReason.UNAVAILABLE)
}
