package coach.presentation

import coach.model.AnswerResult
import coach.model.CapabilityGap
import coach.model.ProvenanceOrigin
import coach.model.RunnableTask
import coach.model.TaskEntryFacts
import coach.model.TaskPurpose

/**
 * A planned task run from Today to its end (16F, `E2EX-v0`; `TRUX-v0` §6), as pure functions: what entry confirmed, what
 * orientation says, how an answer's result is worded and what the learner may do next.
 *
 * The runner is still an execution surface. It says whether an answer matched the course's answer key — never what the
 * learner "is", never a level, a score or a mastery change; what canonical state changed is the program change report's,
 * shown at the end only when it really changed.
 */
object TaskRunCopy {
    const val START = "Başla"
    const val TO_QUESTIONS = "Sorulara geç"
    const val SUBMIT = "Gönder"
    const val NEXT = "Sonraki soru"
    const val FINISH = "Görevi bitir"
    const val SHOW_LESSON = "Dersin anlatımını göster"
    const val SHOW_SOLUTION_BEFORE = "Cevabı göster"
    const val SHOW_ANSWER = "Doğru cevabı göster"
    const val ANSWER_LABEL = "Cevabın"
    const val LESSON_TITLE = "Ders"
    const val NO_MORE_QUESTIONS = "Bu görevde sana gösterilmemiş başka soru kalmadı."
    const val NOTHING_ANSWERED = "Bu görevde bir cevap kaydedilmedi; hiçbir şey değişmedi. Bu bir başarısızlık değil."
    const val BLANK_IS_A_SKIP = "Boş cevap gönderilmez; atlamak yanlış sayılmaz."
    const val TOO_LONG = "Cevap çok uzun; kesilmeden kaydedilemez."
    // `TRUX-v0` §9: asked, not inferred, cheap and never framed as a confession.
    const val PROVENANCE_QUESTION = "Bu görevdeki cevaplarını nasıl veriyorsun?"
    val provenanceLabels: Map<ProvenanceOrigin, String> = mapOf(
        ProvenanceOrigin.USER_AUTHORED to "Kendim",
        ProvenanceOrigin.USER_AUTHORED_WITH_ASSISTANCE to "Biraz yardımla",
        ProvenanceOrigin.MIXED_AUTHORSHIP to "Kısmen kendim, kısmen başka bir kaynaktan",
        ProvenanceOrigin.GENERATED_OR_COPIED to "Başka bir kaynaktan",
        ProvenanceOrigin.UNKNOWN_PROVENANCE to "Söylemek istemiyorum",
    )
    val purposeLabels: Map<TaskPurpose, String> = mapOf(
        TaskPurpose.TEACH to "Yeni bir konu öğrenmek",
        TaskPurpose.PRACTICE to "Pratik",
        TaskPurpose.ASSESS to "Ölçme",
        TaskPurpose.REMEDIATE to "Bir eksiği onarmak",
        TaskPurpose.RETAIN to "Tekrar",
        TaskPurpose.DIAGNOSE to "Ne bildiğini görmek",
        TaskPurpose.REINFORCE to "Pekiştirme",
    )
    // `TRUX-v0` §6.5: correctness feedback, never a mastery claim. A wrong answer is information, not a verdict (14B).
    val resultLines: Map<AnswerResult, String> = mapOf(
        AnswerResult.RIGHT to "Doğru.",
        AnswerResult.PARTLY to "Kısmen doğru.",
        AnswerResult.NOT_RIGHT to "Bu cevap dersin kabul ettiği cevapla eşleşmedi.",
        AnswerResult.NOT_MEASURED to "Bu cevap ölçülemedi; bu bir yanlış sayılmaz.",
    )
    // The teaching task's answers are practice by design (`2D` §6): saying so before the work starts.
    const val TEACH_NOTE = "Bu bir ders: cevapların öğrenmenin parçası, bağımsız ölçüm sayılmaz."
    const val ACCEPTED_ANSWER = "Kabul edilen cevap"
    const val AFTER_REVEAL = "Bu soru artık senin için taze bir ölçüm değil; aynı yetenek daha sonra görmediğin bir soruyla doğrulanır."
    val capabilityLines: Map<CapabilityGap, String> = mapOf(
        CapabilityGap.ASSESSMENT_INTERIOR_NOT_AVAILABLE to "Bu bir ölçme görevi; ölçme oturumu bu uygulamada henüz çalıştırılamıyor.",
        CapabilityGap.NO_ITEM_THIS_APP_CAN_EVALUATE to "Bu görevin soruları (kod testi ya da uzun cevap) bu uygulamada henüz değerlendirilemiyor.",
        CapabilityGap.NO_UNSEEN_ITEM to "Bu görevin değerlendirilebilen bütün sorularını daha önce gördün; taze bir soru kalmadı.",
    )
    fun minutes(value: Int) = "yaklaşık $value dk"
}

/** Orientation (`TRUX-v0` §6.2): what, why, how long as planning context, and the assistance policy before any work. */
data class TaskOrientationView(
    val title: String,
    val purpose: String,
    val minutes: String?,
    val assistancePolicy: String,
    val teachNote: String?,
    val provenanceQuestion: String,
    val provenanceOptions: List<Pair<ProvenanceOrigin, String>>,
    val hasLesson: Boolean,
)

object TaskRunPresentation {

    /** The entry conditions [facts] confirmed; the rest are `unmet`, never failed (`RNRX-v0` §4.1). */
    fun confirmed(facts: TaskEntryFacts): Set<EntryCondition> = buildSet {
        if (facts.stillSelected) add(EntryCondition.PLANNED_TASK_STILL_SELECTED)
        if (facts.prerequisitesSatisfied) add(EntryCondition.HARD_PREREQUISITES_SATISFIED)
        if (facts.needStillOpen) add(EntryCondition.LEARNING_NEED_STILL_OPEN)
        if (facts.contentCompatible) add(EntryCondition.CONTENT_VERSION_COMPATIBLE)
        if (facts.capabilityAvailable) add(EntryCondition.REQUIRED_LOCAL_CAPABILITY_AVAILABLE)
    }

    /** Why the activity cannot run here, when that is what stands in the way; a fact about the app, never the learner. */
    fun capabilityLine(facts: TaskEntryFacts): String? = facts.capabilityGap?.let(TaskRunCopy.capabilityLines::getValue)

    fun orientation(run: RunnableTask): TaskOrientationView = TaskOrientationView(
        title = run.title,
        purpose = TaskRunCopy.purposeLabels.getValue(run.purpose),
        minutes = run.plannedMinutes.takeIf { it > 0 }?.let(TaskRunCopy::minutes),
        assistancePolicy = RunnerCopy.ASSISTANCE_POLICY,
        teachNote = TaskRunCopy.TEACH_NOTE.takeIf { run.purpose == TaskPurpose.TEACH },
        provenanceQuestion = TaskRunCopy.PROVENANCE_QUESTION,
        provenanceOptions = TaskRunCopy.provenanceLabels.toList(),
        hasLesson = run.lesson.isNotEmpty(),
    )

    fun resultLine(result: AnswerResult): String = TaskRunCopy.resultLines.getValue(result)

    /** The accepted answer, shown only after the learner asked, with what seeing it changes said in measurement terms. */
    fun revealed(answers: List<String>): String =
        "${TaskRunCopy.ACCEPTED_ANSWER}: ${answers.firstOrNull().orEmpty()}\n${TaskRunCopy.AFTER_REVEAL}"
}

/**
 * Where a task run is, as the runner screen draws it (16F). Each stage carries only text core already decided and the
 * `TRUX-v0` state it is; the screen adds no judgement. There is no stage that shows a score, a level or a mastery change.
 */
sealed interface TaskRunStage {
    val state: RunnerState

    /** Entry could not confirm everything; the unmet conditions are named, never blamed (`RNRX-v0` §4.1). */
    data class Blocked(val unmet: Set<String>, val capabilityLine: String?) : TaskRunStage {
        override val state = RunnerState.BLOCKED_NOT_STARTABLE
    }

    data class Orient(val view: TaskOrientationView) : TaskRunStage {
        override val state = RunnerState.ORIENTATION
    }

    /** The course's own explanation of the task's Objectives. Teaching segments may end without an attempt (§6.4). */
    data class Lesson(val texts: List<String>) : TaskRunStage {
        override val state = RunnerState.ACTIVE_WORK
    }

    /**
     * One question. [lessonShown] is the explanation the learner asked to see again (help, recorded with the answer);
     * [disclosure] is true while the consequence of seeing the answer is being said, before anything is shown (§8.3).
     */
    data class Question(val number: Int, val prompt: String, val lessonShown: List<String>?, val disclosure: Boolean, val notice: String?) : TaskRunStage {
        override val state = if (lessonShown != null || disclosure) RunnerState.ASSISTANCE_OPEN else RunnerState.ACTIVE_WORK
    }

    /** The answer was shown before an answer was given: the question became learning, and nothing was measured. */
    data class ShownBeforeAnswer(val text: String) : TaskRunStage {
        override val state = RunnerState.ASSISTANCE_OPEN
    }

    data object Working : TaskRunStage {
        override val state = RunnerState.SUBMITTING
    }

    /** How the frozen answer matched the key, and the accepted answer only if the learner asked for it. */
    data class Answered(val result: String, val revealed: String?) : TaskRunStage {
        override val state = RunnerState.FEEDBACK_RESOLVED
    }

    /** The run is over: what canonical state and the plan actually did, or that nothing changed. */
    data class Finished(val lines: List<String>) : TaskRunStage {
        override val state = RunnerState.FEEDBACK_RESOLVED
    }
}
