package coach.presentation

import coach.model.AnswerResult
import coach.model.CapabilityGap
import coach.model.AnsweredItem
import coach.model.AssessmentScope
import coach.model.CheckpointKind
import coach.model.DiagnosticResult
import coach.model.IndependenceClass
import coach.model.MeasurementKind
import coach.model.CodeVerdict
import coach.model.OpenResponseVerdict
import coach.model.RubricCriterion
import coach.model.RunAnswer
import coach.model.RunItemKind
import coach.model.VersionedRef
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

    // 16G: a code item. The learner runs the course's tests on their computer and pastes the report (14D; copy-paste).
    const val CODE_LABEL = "Kodun"
    const val REPORT_LABEL = "Çalıştırıcının raporu (tamamı)"
    const val COPY_RUNNER = "Çalıştırıcıyı kopyala"
    const val COPY_SUITE = "Testleri kopyala"
    const val RUNNER_FILE = "code_test_runner.py"
    const val CODE_BLANK = "Kod boş gönderilmez; atlamak yanlış sayılmaz."
    const val OPEN_LABEL = "Cevabın (kendi cümlelerinle)"
    const val REEVALUATE = "Yeniden değerlendirilsin"
    const val SELF_CHECK = "Ölçütleri göster"
    const val PAUSE = "Duraklat"
    const val NOTHING_TO_PAUSE = "Bu görevde sonra sürdürülecek bir soru kalmadı; duraklatmak yerine görev bitirildi."
    const val PAUSE_NOT_SAFE = "Burası güvenli bir duraklama noktası değil, bu yüzden kaydedilmedi. Soruyu bitirince ya da değerlendirme sonuçlanınca duraklatabilirsin."
    // `TRUX-v0` §7.2: high-stakes work pauses marked and is not continued as independent work.
    const val HIGH_STAKES_PAUSED = "Bu ölçme çalışmasıydı: aynı bağımsız deneme olarak sürdürülmez; aynı yetenek daha sonra görmediğin sorularla ölçülür."

    /** How to run the course's tests for this item on the learner's own computer (14D runner, 16G copy-paste). */
    fun codeSteps(suiteFile: String): List<String> = listOf(
        "1. Bilgisayarında bir klasör aç. Çalıştırıcıyı kopyala ve oraya $RUNNER_FILE adıyla kaydet (bir kez yeterli).",
        "2. Testleri kopyala ve aynı klasöre $suiteFile adıyla kaydet.",
        "3. Kodunu aynı klasörde, görevin söylediği dosya adıyla yaz.",
        "4. Klasörde şunu çalıştır: python $RUNNER_FILE $suiteFile",
        "5. Kodunu ve çalıştırıcının yazdığı raporun tamamını aşağıya yapıştır.",
    )
}

/** What the learner copies to run one code item's tests (16G): the runner and the item's suite, as shipped. */
data class CodeTransferView(val steps: List<String>, val runner: String, val suite: String)

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
    /** 16H: for a measurement, which session it is and what independence means here — said before any work (`ASUX-v0` §6). */
    val measurement: List<String> = emptyList(),
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
        measurement = run.measurement?.let { listOf(MeasurementCopy.scope(it.kind), SessionCopy.INDEPENDENCE_DISCLOSURE, MeasurementCopy.NOT_A_VERDICT) }.orEmpty(),
    )

    fun resultLine(result: AnswerResult): String = TaskRunCopy.resultLines.getValue(result)

    /** The accepted answer, shown only after the learner asked, with what seeing it changes said in measurement terms. */
    fun revealed(answers: List<String>): String =
        "${TaskRunCopy.ACCEPTED_ANSWER}: ${answers.firstOrNull().orEmpty()}\n${TaskRunCopy.AFTER_REVEAL}"

    /** The steps and texts for a code item, or `null` when either text is missing — then the item is not served at all. */
    fun codeTransfer(item: VersionedRef, runner: String?, suite: String?): CodeTransferView? {
        if (runner == null || suite == null) return null
        return CodeTransferView(TaskRunCopy.codeSteps("${item.logicalId}-v${item.version}.json"), runner, suite)
    }

    /** Why nothing was recorded for this answer: blank, too long, or a report that measured nothing (14D's words). */
    fun notRecorded(answer: RunAnswer.NotRecorded, kind: RunItemKind): String = when {
        answer.blank -> if (kind == RunItemKind.CODE_TESTS) TaskRunCopy.CODE_BLANK else TaskRunCopy.BLANK_IS_A_SKIP
        answer.tooLong -> TaskRunCopy.TOO_LONG
        else -> answer.code?.let(CodeEvaluationCopy::notMeasured) ?: TaskRunCopy.resultLines.getValue(AnswerResult.NOT_MEASURED)
    }

    /**
     * A recorded answer as the runner shows it (16G): the result line, then each Objective in the evaluation's own words —
     * the tests' (14D) or the rubric's, criterion by criterion (14F) — and what they do not prove. A pending answer says
     * only that it waits; nothing in it is a score.
     */
    fun answered(answer: RunAnswer.Recorded, kind: RunItemKind, label: (VersionedRef) -> String): TaskRunStage.Answered {
        val details = buildList {
            val code = answer.code
            val open = answer.open
            when {
                code is CodeVerdict.Measured -> {
                    addAll(CodeEvaluationPresentation.lines(code).map { (objective, line) -> "${label(objective)}: $line" })
                    addAll(CodeEvaluationPresentation.notes(code))
                }
                code is CodeVerdict.NotMeasured -> add(CodeEvaluationPresentation.notMeasured(code))
                open is OpenResponseVerdict.Measured -> {
                    addAll(OpenResponsePresentation.lines(open).map { (objective, line) -> "${label(objective)}: $line" })
                    addAll(OpenResponsePresentation.findings(open, answer.criteria))
                    addAll(OpenResponsePresentation.notes(open))
                }
                open is OpenResponseVerdict.NotMeasured -> {
                    addAll(OpenResponsePresentation.notMeasured(open))
                    add(OpenResponseCopy.SELF_CHECK_OFFER)
                }
            }
            answer.answered.independence?.let(ComprehensionPresentation::recheckNote)?.let(::add)
        }
        return TaskRunStage.Answered(
            result = resultLine(answer.answered.result),
            revealed = null,
            details = details,
            canReveal = kind == RunItemKind.ANSWER_KEY,
            pending = answer.answered.pending,
            canSelfCheck = kind == RunItemKind.RUBRIC,
        )
    }

    /** An Objective as the runner names it: the last part of its id, read as words — the same reading Progress uses (16A). */
    fun objectiveLabel(ref: VersionedRef): String = ref.logicalId.substringAfterLast('.').replace('_', ' ')

    /** The rubric for the learner's own check (14F): practice, never evidence. */
    fun selfCheck(criteria: List<RubricCriterion>): List<String> = OpenResponsePresentation.selfCheck(criteria)

    /**
     * The safe-checkpoint conditions this boundary confirms (`TRUX-v0` §7.1). Only between questions: the last question
     * is complete on its own and every artifact is already frozen in the store, nothing is half shown, and no independent
     * attempt waits for its evaluation. In the middle of a question nothing is confirmed, and nothing is assumed. Help
     * shown after the answer froze exposes nothing half-way; a help reply still on its way might, so it is not confirmed.
     */
    fun pauseConditions(betweenQuestions: Boolean, helpInFlight: Boolean, evaluationPending: Boolean): Set<SafeCheckpointCondition> {
        if (!betweenQuestions) return emptySet()
        return buildSet {
            add(SafeCheckpointCondition.SEGMENT_PEDAGOGICALLY_MEANINGFUL_ALONE)
            add(SafeCheckpointCondition.ARTIFACT_AND_RUNNER_STATE_PERSISTABLE_HONESTLY)
            if (!helpInFlight) add(SafeCheckpointCondition.NO_PARTIALLY_EXPOSED_SOLUTION_STATE)
            if (!evaluationPending) add(SafeCheckpointCondition.NO_HALF_EVALUATED_INDEPENDENT_ATTEMPT)
        }
    }

    /** What a durable pause says: the saved place (the state's own text), and for high-stakes work, what it does not resume. */
    fun paused(decision: PauseDecision.Durable, lines: List<String>): TaskRunStage.Paused =
        TaskRunStage.Paused(decision, listOfNotNull(TaskRunCopy.HIGH_STAKES_PAUSED.takeIf { decision.kind == CheckpointKind.HIGH_STAKES_PAUSE }) + lines)
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
    data class Question(
        val number: Int, val prompt: String, val lessonShown: List<String>?, val disclosure: Boolean, val notice: String?,
        // 16G: how the answer is given — typed, code with the course's tests, or an open answer — and the help panel.
        val kind: RunItemKind = RunItemKind.ANSWER_KEY,
        val code: CodeTransferView? = null,
        val help: HelpView? = null,
        /** 16H: in a measurement the learner may leave a question unanswered; that is not incorrect (`ASUX-v0` §7.3). */
        val canSkip: Boolean = false,
        /** 17C: how many questions this run can serve, for the learner's position ("Soru 2 / 5"); `null` when unknown. */
        val total: Int? = null,
    ) : TaskRunStage {
        override val state = if (lessonShown != null || disclosure || help?.panel != null) RunnerState.ASSISTANCE_OPEN else RunnerState.ACTIVE_WORK
    }

    /** The answer was shown before an answer was given: the question became learning, and nothing was measured. */
    data class ShownBeforeAnswer(val text: String) : TaskRunStage {
        override val state = RunnerState.ASSISTANCE_OPEN
    }

    data object Working : TaskRunStage {
        override val state = RunnerState.SUBMITTING
    }

    /**
     * How the frozen answer matched the key, and the accepted answer only if the learner asked for it. 16G adds what the
     * evaluation said per Objective ([details]), a pending evaluation the learner may ask again, the rubric for their own
     * check, what a wrong answer said (14B), the comprehension check (14E) and the help panel.
     */
    data class Answered(
        val result: String,
        val revealed: String?,
        val details: List<String> = emptyList(),
        val canReveal: Boolean = true,
        val pending: Boolean = false,
        val canSelfCheck: Boolean = false,
        val selfCheck: List<String>? = null,
        val wrongAnswer: WrongAnswerSummary.Summary? = null,
        val comprehension: ComprehensionView? = null,
        val help: HelpView? = null,
        val notice: String? = null,
        /** 16H: in a measurement an answered question may be reported as wrong or unanswerable (user decision). */
        val canContest: Boolean = false,
    ) : TaskRunStage {
        override val state = when {
            pending -> RunnerState.EVALUATION_PENDING
            help?.panel != null -> RunnerState.ASSISTANCE_OPEN
            else -> RunnerState.FEEDBACK_RESOLVED
        }
    }

    /** A durable pause only (11C): the place was written, so the state says it was saved; the run's end lines follow. */
    data class Paused(val decision: PauseDecision.Durable, val lines: List<String>) : TaskRunStage {
        override val state = PausePolicy.stateAfter(decision, RunnerState.FEEDBACK_RESOLVED)
    }

    /** The run is over: what canonical state and the plan actually did, or that nothing changed. */
    data class Finished(val lines: List<String>) : TaskRunStage {
        override val state = RunnerState.FEEDBACK_RESOLVED
    }
}

/**
 * A measurement run as the learner reads it (16H, `ASRX-v0`). Every sentence is `ASUX-v0`'s meaning: which session it is,
 * that a skipped question is not wrong, that a reported question is held rather than counted, and a result made of what
 * the engines said and what could not be measured — never a score, a grade, a percentage or a pass/fail.
 */
object MeasurementCopy {
    fun scope(kind: MeasurementKind): String = when (kind) {
        MeasurementKind.DAILY_ASSESSMENT -> "Günlük ölçme"
        MeasurementKind.WEEKLY_SLOT -> "Haftalık ölçme"
        MeasurementKind.MONTHLY_SLOT -> "Aylık ölçme"
        MeasurementKind.DIAGNOSTIC -> "Hızlı ilerleme kontrolü"
    }

    const val NOT_A_VERDICT = "Bu bir sınav notu değil: sonuç, motorların bu kanıttan ne söylediği ve neyin güvenilir biçimde ölçülemediğidir."
    const val SKIP = "Boş bırak"
    const val CONTEST = "Bu soruda sorun var"
    const val RESULT_READY = "Ölçüm tamamlandı."
    const val RESULT_PARTIAL = "Ölçümün bir kısmı yapıldı. Yapılmayan kısım yanlış sayılmadı ve borç olarak taşınmıyor."
    const val NOT_RELIABLY_MEASURED = "Güvenilir biçimde ölçülemeyenler"
    const val FAST_PATH_OFFER = "Bu konuyu zaten biliyorsan hızlı yolu deneyebilirsin: birkaç soruyla, gösterdiğin bölümlerin başlangıç anlatımları atlanır. Bu bir kestirme değil; aynı kanıtı daha erken toplar."
    const val FAST_PATH_REQUEST = "Bunu biliyorum, hızlı yolu dene"
    const val FAST_PATH_WITHDRAW = "Hızlı yoldan çık"
    const val FAST_PATH_REQUESTED = "Hızlı yol açıldı; kontrol soruları bugünün planına eklendi. Başladığın işler korundu."
    const val FAST_PATH_NOTHING = "Bu konuda kontrol edilecek bir bölüm kalmadı: hepsi zaten gösterilmiş ya da atlanmış."
    const val FAST_PATH_WITHDRAWN = "Hızlı yoldan çıktın. Atlanan bölümler atlanmış kalır; diğerleri normal öğrenme akışında devam eder. Bu bir kayıp değil."
    const val FAST_PATH_NOT_STARTABLE = "Bu konunun becerileri henüz yeni bir başlangıca açık değil; hızlı yol istenemedi."
    const val FAST_PATH_UNREADABLE = "Açık hızlı yol kontrolü şu an okunamadı; hiçbir şey tahmin edilmedi."
    const val CONTESTED_NOTICE = SessionCopy.CONTESTED
    const val SKIPPED_NOTICE = SessionCopy.SKIPPED
}

object MeasurementPresentation {

    /**
     * The session's result (`ASUX-v0` §13): one boundary per question shown — answered (frozen), skipped (unsubmitted),
     * reported (contested) or never answered (open) — and what could not be measured reliably from what was answered.
     */
    fun result(scope: AssessmentScope, shown: List<VersionedRef>, answered: List<AnsweredItem>, skipped: Set<VersionedRef>,
               contested: Set<VersionedRef>): SessionResult {
        val view = AssessmentSessionView(
            scope = scope, intents = emptyList(),
            blocks = if (shown.isEmpty()) emptyList() else listOf(Block("run", shown.map { Boundary(it.toString(), BoundaryKind.ITEM, listOf(it)) })),
            independenceMode = coach.model.IndependenceMode.H0_REQUIRED, allowedTools = coach.model.AllowedToolsPolicy(emptyList()),
            state = SessionState.RESULT_READY,
            statuses = shown.associate { ref ->
                ref.toString() to when {
                    ref in contested -> BoundaryStatus.CONTESTED
                    answered.any { it.item == ref } -> BoundaryStatus.FROZEN
                    ref in skipped -> BoundaryStatus.UNSUBMITTED
                    else -> BoundaryStatus.OPEN
                }
            },
        )
        val unreliable = buildSet {
            answered.forEach { a ->
                if (a.pending) add(NotReliablyMeasured.PROVISIONAL_EVALUATION)
                else if (a.result == AnswerResult.NOT_MEASURED) add(NotReliablyMeasured.INVALID_ITEM)
                when (a.independence) {
                    IndependenceClass.ASSISTED -> add(NotReliablyMeasured.ASSISTED_ATTEMPT)
                    IndependenceClass.REQUIRES_INDEPENDENT_RECHECK -> add(NotReliablyMeasured.SOLUTION_EXPOSED_ATTEMPT)
                    else -> Unit
                }
            }
        }
        return SessionResults.of(view, unreliable = unreliable)
    }

    /** The result as lines: its state, what could not be measured, then what the engines reported (13E). */
    fun lines(result: SessionResult, reported: List<String>): List<String> = buildList {
        add(if (result.partial) MeasurementCopy.RESULT_PARTIAL else MeasurementCopy.RESULT_READY)
        if (result.showsNotReliablyMeasured) {
            add("${MeasurementCopy.NOT_RELIABLY_MEASURED}: " + result.notReliablyMeasured.joinToString(", ") { HistoryCopy.notMeasuredLabels.getValue(it) })
        }
        addAll(reported)
    }
}

/** The fast path on a Topic (16H): offered, open with its result, or not available — never a verdict. */
sealed interface FastPathView {
    data class Offer(val text: String, val action: String) : FastPathView
    data class Open(val headline: String, val lines: List<String>, val action: String) : FastPathView
    data class Said(val text: String) : FastPathView

    companion object {
        fun offer() = Offer(MeasurementCopy.FAST_PATH_OFFER, MeasurementCopy.FAST_PATH_REQUEST)

        fun open(result: DiagnosticResult, label: (VersionedRef) -> String): Open {
            val summary = DiagnosticResults.summary(result, label)
            return Open(summary.headline, summary.lines, MeasurementCopy.FAST_PATH_WITHDRAW)
        }
    }
}

