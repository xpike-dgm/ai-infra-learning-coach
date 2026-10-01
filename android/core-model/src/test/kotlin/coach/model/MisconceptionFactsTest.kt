package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/** The closed catalog and the stored tags (14B, `WAAX-v0`). */
class MisconceptionFactsTest {

    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val label = VersionedRef("misconception.c.pointers.address_value", 1)

    @Test
    fun `a catalog label has a dotted misconception id and is put to the learner as a question`() {
        MisconceptionRow(label, objective, "adres ile değer", "Adres ile değeri karıştırmış olabilir misin?")
        for (bad in listOf("pointer_confusion", "misconception.pointers", "misconception.C.pointers.x", "misconception.c..x", "misconception.c.pointers.address-value")) {
            assertFailsWith<IllegalArgumentException>(bad) { MisconceptionRow(VersionedRef(bad, 1), objective, "x", "x?") }
        }
        assertFailsWith<IllegalArgumentException> { MisconceptionRow(label, objective, "adres ile değer", "Adres ile değeri karıştırdın.") }
        assertFailsWith<IllegalArgumentException> { MisconceptionRow(label, objective, " ", "Olabilir mi?") }
    }

    @Test
    fun `stored tags decode completely or not at all`() {
        val tags = listOf(
            MisconceptionTag(label, MisconceptionSource.DETERMINISTIC),
            MisconceptionTag(VersionedRef("misconception.c.pointers.star_amp_roles", 2), MisconceptionSource.AI_PROPOSED),
        )
        val text = MisconceptionTags.encode(tags)
        assertEquals("misconception_tags/1|misconception.c.pointers.address_value@v1:deterministic|misconception.c.pointers.star_amp_roles@v2:ai_proposed", text)
        assertEquals(tags, MisconceptionTags.decode(text))
        assertNull(MisconceptionTags.encode(emptyList()))
        assertEquals(emptyList(), MisconceptionTags.decode(null))
        for (bad in listOf("misconception_tags/2|misconception.c.pointers.address_value@v1:deterministic", "misconception_tags/1",
                           "misconception_tags/1|misconception.c.pointers.address_value@v1:llm", "misconception_tags/1|misconception.c.pointers.address_value:deterministic",
                           "misconception_tags/1|invented@v1:deterministic", "misconception_tags/1|misconception.c.pointers.address_value@v0:deterministic")) {
            assertNull(MisconceptionTags.decode(bad), bad)
        }
    }

    @Test
    fun `an AI-proposed label can never be held stronger than a hypothesis`() {
        MisconceptionSignalState(label, objective, objective, WeaknessSignal.HYPOTHESIS, MisconceptionSource.AI_PROPOSED)
        for (signal in listOf(WeaknessSignal.SUPPORTED, WeaknessSignal.CONFIRMED)) {
            assertFailsWith<IllegalArgumentException> { MisconceptionSignalState(label, objective, objective, signal, MisconceptionSource.AI_PROPOSED) }
            MisconceptionSignalState(label, objective, objective, signal, MisconceptionSource.DETERMINISTIC)
        }
        assertEquals(WeaknessSignal.HYPOTHESIS, MisconceptionSource.AI_PROPOSED.ceiling)
    }

    @Test
    fun `a label is resolved by evidence, never by time`() {
        assertFailsWith<IllegalArgumentException> {
            MisconceptionSignalState(label, objective, objective, WeaknessSignal.RESOLVED, MisconceptionSource.DETERMINISTIC)
        }
        MisconceptionSignalState(label, objective, objective, WeaknessSignal.RESOLVED, MisconceptionSource.DETERMINISTIC, resolutionEvidenceId = 7)
    }

    @Test
    fun `a label pinned to an Objective nobody published refuses the package`() {
        val pkg = CurriculumPackage(
            version = 1, sourceRefs = "src", provenance = "authored",
            misconceptions = listOf(MisconceptionRow(label, objective, "adres ile değer", "Adres ile değeri karıştırmış olabilir misin?")),
        )
        assertEquals(listOf("misconception $label -> objective $objective"), pkg.unresolvedReferences())
        assertEquals(emptyList(), pkg.unresolvedReferences { kind, ref -> kind == CurriculumPackage.OBJECTIVE && ref == objective })
    }
}
