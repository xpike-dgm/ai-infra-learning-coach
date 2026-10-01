package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Alternative explanation as rules (14C, `ALEX-v0`): the learner chooses, written first, the tutor only where it may write. */
class AlternativeExplanationFactsTest {

    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val label = VersionedRef("misconception.c.pointers.address_value", 1)

    private fun variant(slug: String, form: ExplanationForm, level: AssistanceLevel? = AssistanceLevel.H1, misconception: VersionedRef? = null) =
        ExplanationVariant(VersionedRef("explanation.c.pointers.$slug", 1), objective, form, level, "Metin $slug", misconception)

    @Test
    fun `the tutor never writes a form that needs the learner's own state, and the course's own explanation is not an alternative`() {
        assertEquals(listOf(ExplanationForm.MISCONCEPTION_CONTRAST, ExplanationForm.CANONICAL), ExplanationForm.entries.filterNot { it.aiAllowed })
        assertEquals(listOf(ExplanationForm.CANONICAL), ExplanationForm.entries.filterNot { it.offered })
    }

    @Test
    fun `a written explanation declares how far it goes, and only the course's own carries no level`() {
        variant("canon", ExplanationForm.CANONICAL, level = null)
        assertFailsWith<IllegalArgumentException> { variant("canon", ExplanationForm.CANONICAL, level = AssistanceLevel.H1) }
        assertFailsWith<IllegalArgumentException> { variant("plain", ExplanationForm.PLAIN_RETEACH, level = null) }
        assertFailsWith<IllegalArgumentException> { ExplanationVariant(VersionedRef("pointer_note", 1), objective, ExplanationForm.PLAIN_RETEACH, AssistanceLevel.H1, "x") }
    }

    @Test
    fun `a contrast names exactly one catalog misconception, and nothing else does`() {
        variant("contrast", ExplanationForm.MISCONCEPTION_CONTRAST, misconception = label)
        assertFailsWith<IllegalArgumentException> { variant("contrast", ExplanationForm.MISCONCEPTION_CONTRAST) }
        assertFailsWith<IllegalArgumentException> { variant("plain", ExplanationForm.PLAIN_RETEACH, misconception = label) }
    }

    @Test
    fun `the menu offers only forms with somewhere to come from, in a fixed order, and marks what was seen`() {
        // The course's own explanation is written too, yet it is never offered as an alternative: it is where the way back leads.
        val options = ExplanationMenu.options(
            listOf(variant("worked", ExplanationForm.WORKED_EXAMPLE), variant("canon", ExplanationForm.CANONICAL, level = null)),
            emptySet(), setOf(ExplanationForm.PLAIN_RETEACH),
        )
        assertEquals(
            listOf(ExplanationForm.PLAIN_RETEACH, ExplanationForm.DIFFERENT_EXAMPLE, ExplanationForm.WORKED_EXAMPLE, ExplanationForm.STATE_TRACE, ExplanationForm.PREREQUISITE_REFRESH),
            options.map { it.form },
        )
        assertEquals(ExplanationSource.WRITTEN, options.single { it.form == ExplanationForm.WORKED_EXAMPLE }.source)
        assertEquals(ExplanationSource.AI, options.single { it.form == ExplanationForm.PLAIN_RETEACH }.source)
        assertEquals(listOf(ExplanationForm.PLAIN_RETEACH), options.filter { it.seenThisSession }.map { it.form })
    }

    @Test
    fun `a contrast is offered only from writing, for a misconception the learner's memory holds open`() {
        val contrast = variant("contrast", ExplanationForm.MISCONCEPTION_CONTRAST, misconception = label)
        assertTrue(ExplanationMenu.options(listOf(contrast), emptySet(), emptySet()).none { it.form == ExplanationForm.MISCONCEPTION_CONTRAST })
        val open = ExplanationMenu.options(listOf(contrast), setOf(label), emptySet()).single { it.form == ExplanationForm.MISCONCEPTION_CONTRAST }
        assertEquals(ExplanationSource.WRITTEN, open.source)
        assertTrue(ExplanationMenu.options(emptyList(), setOf(label), emptySet()).none { it.form == ExplanationForm.MISCONCEPTION_CONTRAST })
    }

    @Test
    fun `the least revealing written explanation of a form is the one shown`() {
        val deep = variant("worked_full", ExplanationForm.WORKED_EXAMPLE, AssistanceLevel.H3)
        val light = variant("worked_light", ExplanationForm.WORKED_EXAMPLE, AssistanceLevel.H2)
        assertEquals(light, ExplanationMenu.written(listOf(deep, light), ExplanationForm.WORKED_EXAMPLE, emptySet()))
        assertNull(ExplanationMenu.written(listOf(deep), ExplanationForm.STATE_TRACE, emptySet()))
        val canon = variant("canon", ExplanationForm.CANONICAL, level = null)
        assertEquals(canon, ExplanationMenu.canonical(listOf(deep, canon)))
    }

    private fun ask(form: ExplanationForm?, intent: TutorIntent = TutorIntent.EXPLAIN_DIFFERENTLY, timing: AssistanceTiming? = null, ceiling: AssistanceLevel? = null) =
        TutorAsk(intent, TaskPurpose.TEACH, timing, ceiling, false, InstructionMode.TURKISH_PRIMARY,
            TutorContext(listOf(objective), "Pointer üzerinden yazma.", canonicalExplanation = "*p = 5 değeri x'e yazar."), form = form)

    @Test
    fun `the tutor is never asked for a form only written content may give, and only an explanation carries a form`() {
        assertFailsWith<IllegalArgumentException> { TutorRules.prepare(ask(ExplanationForm.MISCONCEPTION_CONTRAST)) }
        assertFailsWith<IllegalArgumentException> { TutorRules.prepare(ask(ExplanationForm.CANONICAL)) }
        assertFailsWith<IllegalArgumentException> {
            TutorRules.prepare(ask(ExplanationForm.PLAIN_RETEACH, TutorIntent.HINT, AssistanceTiming.DURING_ATTEMPT, AssistanceLevel.H1))
        }
        assertEquals(ExplanationForm.WORKED_EXAMPLE, assertIs<TutorPreparation.Ready>(TutorRules.prepare(ask(ExplanationForm.WORKED_EXAMPLE))).request.form)
    }

    @Test
    fun `written help is shown without the tutor only if it fits the learner's ceiling, at its own level`() {
        val request = assertIs<TutorPreparation.Ready>(
            TutorRules.prepare(ask(ExplanationForm.WORKED_EXAMPLE, timing = AssistanceTiming.DURING_ATTEMPT, ceiling = AssistanceLevel.H2))
        ).request
        val fits = TutorRules.authored(request, variant("worked_light", ExplanationForm.WORKED_EXAMPLE, AssistanceLevel.H1).asHelp())
        assertEquals(AssistanceLevel.H1, fits!!.event!!.level)
        assertEquals(AssistanceSource.DETERMINISTIC_CONTENT, fits.source)
        assertNull(TutorRules.authored(request, variant("worked_full", ExplanationForm.WORKED_EXAMPLE, AssistanceLevel.H3).asHelp()))
        val lesson = assertIs<TutorPreparation.Ready>(TutorRules.prepare(ask(ExplanationForm.WORKED_EXAMPLE))).request
        assertNull(TutorRules.authored(lesson, variant("worked_full", ExplanationForm.WORKED_EXAMPLE, AssistanceLevel.H3).asHelp())!!.event,
            "outside an attempt nothing is recorded")
    }
}
