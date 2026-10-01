package coach.application

import coach.model.ExplanationForm
import coach.model.ExplanationMenu
import coach.model.ExplanationOption
import coach.model.ExplanationVariant
import coach.model.MisconceptionRow
import coach.model.TutorAsk
import coach.model.TutorIntent
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRequest
import coach.model.TutorRules
import coach.model.VersionedRef
import coach.model.WeaknessSignal
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * Explaining a concept again (14C, `ALEX-v0 / D-107`).
 *
 * The learner chooses the form (user decision); this use case only says which forms have somewhere to come from and
 * then shows the chosen one — **written first** ("önce yazılmış", user decision): a verified explanation for that form
 * is shown without asking the tutor, and only when none fits is the tutor asked, grounded in the course's own
 * explanation. The learner's misconception memory decides which written contrasts may be offered, on the device; it is
 * never sent anywhere (`AIAX-v0` §11).
 */
class ExplainDifferently(
    private val content: ContentPort,
    private val persistence: PersistencePort,
    private val ask: AskTutor,
) {
    /** The menu for one Objective. [seen] is this session's own memory of what was already shown; nothing is stored. */
    fun options(objective: VersionedRef, seen: Set<ExplanationForm>): List<ExplanationOption> =
        ExplanationMenu.options(content.explanationsFor(objective), openMisconceptions(objective), seen)

    /** The course's own explanation, to ground a tutor request in and to return to at any time. */
    fun canonical(objective: VersionedRef): ExplanationVariant? = ExplanationMenu.canonical(content.explanationsFor(objective))

    /**
     * Prepares the learner's ask for [form]: always `explain_differently`, the form named only where the tutor may write
     * it, and the course's own explanation attached as grounding whenever one is written — so no AI alternative is asked
     * without the text it must not contradict, when that text exists.
     */
    fun prepare(base: TutorAsk, objective: VersionedRef, form: ExplanationForm): TutorPreparation =
        TutorRules.prepare(
            base.copy(
                intent = TutorIntent.EXPLAIN_DIFFERENTLY,
                form = form.takeIf { it.aiAllowed },
                context = base.context.copy(canonicalExplanation = canonical(objective)?.text),
            )
        )

    /**
     * Shows [form] for [objective]. [request] must be the learner's prepared `explain_differently` ask for this form;
     * if a written explanation for the form fits it is shown, otherwise the tutor is asked. A form only written content
     * may give, with nothing written for it, shows nothing — there is nothing true to show.
     */
    fun explain(
        objective: VersionedRef,
        form: ExplanationForm,
        request: TutorRequest,
        item: AskTutor.Item? = null,
        attemptId: Long? = null,
    ): TutorOutcome? {
        require(form.offered) { "the course's own explanation is shown as it is, not as an alternative" }
        // A form only written content may give is asked without a form: the tutor is never asked for it (`TutorRules`).
        require(request.intent == TutorIntent.EXPLAIN_DIFFERENTLY && (request.form == null || request.form == form)) {
            "an explanation is asked as explain_differently, for the form the learner chose"
        }
        val written = ExplanationMenu.written(content.explanationsFor(objective), form, openMisconceptions(objective))
        if (written != null) {
            ask.showWritten(request, written.asHelp(), item, attemptId)?.let { return it }
        }
        if (!form.aiAllowed || request.form != form) return null
        return ask.ask(request, item, attemptId = attemptId)
    }

    /** Catalog labels this learner's memory holds open for the Objective (`WAAX-v0`): read here, never sent. */
    private fun openMisconceptions(objective: VersionedRef): Set<VersionedRef> =
        persistence.misconceptionsOf(objective).map(MisconceptionRow::ref).filter { label ->
            val state = persistence.readProjection(MisconceptionRows.key(label))?.payload?.get("state")
            state == WeaknessSignal.HYPOTHESIS.id || state == WeaknessSignal.SUPPORTED.id || state == WeaknessSignal.CONFIRMED.id
        }.toSet()
}
