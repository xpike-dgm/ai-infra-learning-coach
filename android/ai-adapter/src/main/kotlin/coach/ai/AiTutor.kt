package coach.ai

import coach.model.PendingReason
import coach.model.TutorReply
import coach.model.TutorRequest
import coach.ports.TutorPort

/**
 * Optional module, like [AiEvaluator]: the product builds, runs and teaches without it (`MSBX-v0` §ai_absence).
 *
 * The behaviour it must have is fixed by `TUTX-v0 / D-105` and its call site is 14G's (user decision,
 * 2026-10-01):
 *  - it sends `TutorInstructions.TEXT` and `TutorInstructions.userMessage(request)` and nothing else — the
 *    message is built in core, so what leaves the device is checked off the device,
 *  - it asks for `TutorInstructions.REPLY_SCHEMA` and maps only a schema-valid reply to `Delivered`;
 *    anything else is `invalid_response`, never a reply read out of free text,
 *  - it inspects the stop reason before the content; a refusal is `refused`, never the learner's fault,
 *  - the timeout budget is end-to-end across retries, with no silent retry against the learner's key,
 *  - the model and provider it names in `TutorRef` come from configuration, verified against a current
 *    provider reference when the call site is written (`AIAX-v0` §8.2).
 *
 * Until then it is **unavailable**, said the way every non-answer is said — not a crash, not a reply.
 */
class AiTutor : TutorPort {
    override fun assist(request: TutorRequest): TutorReply = TutorReply.NotDelivered(PendingReason.UNAVAILABLE)
}
