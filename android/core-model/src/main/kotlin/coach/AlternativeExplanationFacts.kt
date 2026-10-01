package coach.model

/**
 * `ALEX-v0 / D-107` — alternative explanation (14C).
 *
 * **When an explanation does not land, the method changes — the scope and the truth do not.** The learner chooses
 * how it is explained again (user decision, 2026-10-01); a verified, written explanation is shown first, and only
 * where none exists for that form does the tutor write one — grounded in the course's own explanation, labelled as
 * unverified, and never the last word: the course's explanation is always one tap away
 * (`LEARNING_BEHAVIOR_RULES` §9, §12).
 */

/**
 * The ways an explanation can be given again. Closed, and every form comes from an accepted source:
 * `LEARNING_BEHAVIOR_RULES` §9 ("daha sade anlatım, farklı örnek/analogy, worked example, prerequisite'e kısa geri
 * dönüş") and `WLRM-v0`'s remediation strategies. [strategy] names the `WLRM-v0` strategy a form realises, if any.
 *
 * [aiAllowed] is `false` where writing the explanation would need the learner's own state: a misconception contrast
 * is chosen *because of* what this learner may have confused, and that memory never leaves the device
 * (`AIAX-v0` §11), so only written content serves it. The course's own explanation is not an alternative at all.
 */
enum class ExplanationForm(val id: String, val strategy: String?, val aiAllowed: Boolean, val offered: Boolean) {
    PLAIN_RETEACH("plain_reteach", "strategy.targeted_reteach", aiAllowed = true, offered = true),
    DIFFERENT_EXAMPLE("different_example", null, aiAllowed = true, offered = true),
    WORKED_EXAMPLE("worked_example", "strategy.worked_example", aiAllowed = true, offered = true),
    STATE_TRACE("state_trace", "strategy.state_trace_reconstruction", aiAllowed = true, offered = true),
    PREREQUISITE_REFRESH("prerequisite_refresh", "strategy.prerequisite_refresh", aiAllowed = true, offered = true),
    MISCONCEPTION_CONTRAST("misconception_contrast", "strategy.misconception_contrast", aiAllowed = false, offered = true),

    /** The course's verified explanation of the Objective: what every alternative is grounded in and returns to. */
    CANONICAL("canonical", null, aiAllowed = false, offered = false),
}

/**
 * One written explanation of one Objective version, authored with the content (15) and served by the content port.
 *
 * [level] is how far it goes, declared by the person who wrote it, so it is recorded at that level if shown while an
 * answer is open (`TUTX-v0` §9). The course's explanation has none: it is the lesson, not help with an item. A
 * contrast names the catalog [misconception] it contrasts (`WAAX-v0`); nothing else does.
 */
data class ExplanationVariant(
    val ref: VersionedRef,
    val objective: VersionedRef,
    val form: ExplanationForm,
    val level: AssistanceLevel?,
    val text: String,
    val misconception: VersionedRef? = null,
) {
    init {
        require(ID.matches(ref.logicalId)) { "an explanation id is explanation.<namespace>.<slug> (GNS-v0): ${ref.logicalId}" }
        require(text.isNotBlank()) { "an explanation has text" }
        require((form == ExplanationForm.CANONICAL) == (level == null)) { "only the course's own explanation carries no level" }
        require((form == ExplanationForm.MISCONCEPTION_CONTRAST) == (misconception != null)) {
            "a contrast names exactly one catalog misconception, and nothing else does"
        }
    }

    fun asHelp(): AuthoredHelp = AuthoredHelp(TutorIntent.EXPLAIN_DIFFERENTLY, level ?: AssistanceLevel.H1, text, ref)

    companion object {
        val ID = Regex("^explanation(\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")
    }
}

/** Where an offered form would come from. Only a form with somewhere to come from is offered at all. */
enum class ExplanationSource(val id: String) {
    WRITTEN("written"),
    AI("ai"),
}

/** One entry of the menu the learner chooses from (user decision, 2026-10-01). */
data class ExplanationOption(val form: ExplanationForm, val source: ExplanationSource, val seenThisSession: Boolean)

object ExplanationMenu {

    /**
     * The forms the learner can choose, in the vocabulary's order — **no hidden ranking**: the learner decides. A form
     * with a written explanation is offered as written ("önce yazılmış", user decision); one without is offered from
     * the tutor only if the tutor may write it; a misconception contrast only if a written contrast exists for a label
     * this learner's memory holds open — and it is offered as a common mix-up, never as the learner's.
     */
    fun options(
        variants: List<ExplanationVariant>,
        openMisconceptions: Set<VersionedRef>,
        seen: Set<ExplanationForm>,
    ): List<ExplanationOption> = ExplanationForm.entries.filter { it.offered }.mapNotNull { form ->
        val written = variants.any { it.form == form && (form != ExplanationForm.MISCONCEPTION_CONTRAST || it.misconception in openMisconceptions) }
        val source = when {
            written -> ExplanationSource.WRITTEN
            form.aiAllowed -> ExplanationSource.AI
            else -> return@mapNotNull null
        }
        ExplanationOption(form, source, form in seen)
    }

    /** The written explanation for a chosen form, if one exists: the lowest level first, so the least is revealed. */
    fun written(variants: List<ExplanationVariant>, form: ExplanationForm, openMisconceptions: Set<VersionedRef>): ExplanationVariant? =
        variants.filter { it.form == form && (form != ExplanationForm.MISCONCEPTION_CONTRAST || it.misconception in openMisconceptions) }
            .minWithOrNull(compareBy<ExplanationVariant> { it.level?.ordinal ?: -1 }.thenBy { it.ref.logicalId })

    /** The course's own explanation of the Objective, if written: what an AI alternative is grounded in. */
    fun canonical(variants: List<ExplanationVariant>): ExplanationVariant? =
        variants.filter { it.form == ExplanationForm.CANONICAL }.minByOrNull { it.ref.logicalId }
}
