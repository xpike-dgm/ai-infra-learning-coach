package coach.wiring

import coach.model.AnsweredItem
import coach.model.AssessmentItem
import coach.model.ProgramSnapshot
import coach.model.ProvenanceOrigin
import coach.model.RunnableTask

/**
 * One task run's working memory on the main thread (16F). It holds what the run showed and what the learner answered
 * — nothing it decides: every judgement is core's, and everything that counts is already in the store the moment it is
 * recorded. Like 11C's working session it is not stored, so losing it loses nothing the learner did.
 */
internal class TaskRunSession(val run: RunnableTask, val before: ProgramSnapshot) {
    /** The learner's own answer to the provenance question, asked once at orientation (`TRUX-v0` §9). */
    var provenance: ProvenanceOrigin? = null

    /** Every question shown in this run, in order; none is shown twice. */
    val shown = mutableListOf<AssessmentItem>()
    var current: AssessmentItem? = null
    var currentPrompt: String = ""

    /** Whether the learner asked to see the lesson again during the current question: help recorded with the answer. */
    var lessonShown = false

    val answered = mutableListOf<AnsweredItem>()
    var lastAnswer: AnsweredItem? = null

    /** Whether the end-of-run processing already ran, so a stop after finishing does not run it twice. */
    var finished = false
}
