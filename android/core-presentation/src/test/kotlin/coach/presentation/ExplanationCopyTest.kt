package coach.presentation

import coach.model.ReasonCatalog
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `PDT-v0` §3's template fallback (12E). The words are working microcopy (14); what they may claim is
 * not, so every sentence is held to the contracts here.
 */
class ExplanationCopyTest {

    private val all: List<String>
        get() = ExplanationCopy.codeTemplates.values + ExplanationCopy.skillTemplates.values +
            ExplanationCopy.factTemplates.values + ExplanationCopy.reconsiderationTemplates.values +
            ExplanationCopy.stateTemplates.values

    private fun words(sentence: String): List<String> =
        sentence.split(Regex("[^\\p{L}]+")).filter { it.isNotEmpty() }.map { it.lowercase(java.util.Locale.ROOT) }

    @Test
    fun `every catalogue code has a sentence, in the catalogue's order, and nothing else does`() {
        assertTrue(ExplanationCopy.coversCatalogue())
        assertEquals(ReasonCatalog.all, ExplanationCopy.codeTemplates.keys.toList())
        assertTrue(ExplanationCopy.skillTemplates.keys.all { it in ReasonCatalog.all })
        assertTrue(ExplanationCopy.skillTemplates.values.all { "{skills}" in it })
        assertEquals(TraceFact.entries.toSet(), ExplanationCopy.factTemplates.keys)
        assertEquals(Reconsideration.entries.toSet(), ExplanationCopy.reconsiderationTemplates.keys)
        assertEquals(ExplanationState.entries.toSet(), ExplanationCopy.stateTemplates.keys)
        assertTrue(all.all { it.isNotBlank() && it.trim() == it })
    }

    @Test
    fun `no sentence claims forgetting, debt, falling behind, a score or a percentage`() {
        val forbidden = listOf("unuttun", "unutmuş", "borcun", "borçlu", "geride kaldın", "geri kaldın", "kaçırdın",
            "telafi", "başarısız oldun", "tembel", "%")
        // Whole words only: "yüzden" is not "yüzde" and "ayarından" is not "yarın".
        val forbiddenWords = setOf("skor", "puan", "yüzde", "streak", "seri", "not")
        all.forEach { sentence ->
            forbidden.forEach { word -> assertFalse(word in sentence, "\"$sentence\" says '$word'") }
            words(sentence).forEach { word -> assertFalse(word in forbiddenWords, "\"$sentence\" says '$word'") }
            // The one place forgetting is named is to say review due is not it (`PDT-v0` §12).
            if ("unut" in sentence) assertTrue("unuttuğun anlamına gelmez" in sentence, sentence)
        }
        assertEquals(1, all.count { "unut" in it })
    }

    @Test
    fun `work deferred for time is never worded as less important`() {
        val forTime = ReasonCatalog.all.filter { it.startsWith("capacity.") } + "selection.not_selected_capacity"
        forTime.forEach { code ->
            val sentence = ExplanationCopy.codeTemplates.getValue(code)
            assertFalse("öncelik" in sentence || "önemsiz" in sentence, "$code: $sentence")
        }
        assertTrue("borç veya başarısızlık değildir" in ExplanationCopy.codeTemplates.getValue("capacity.deferred_not_enough_time"))
    }

    @Test
    fun `absence is said to be neither failure nor debt`() {
        assertTrue("başarısızlık değildir" in ExplanationCopy.codeTemplates.getValue("reentry.absence_not_failure"))
        assertTrue("borç olarak taşınmadı" in ExplanationCopy.codeTemplates.getValue("reentry.absence_not_task_debt"))
    }

    @Test
    fun `a waiting task names its Skill blocker, by name or by reference`() {
        val skill = VersionedRef("skill.c.pointer_dereference", 1)
        fun waiting(name: String?) = ExplanationCopy.text(
            requireNotNull(Statement.ofCode("eligibility.blocked_hard_prerequisite", setOf("eligibility.blocked_hard_prerequisite"),
                listOf(SkillMention(skill, name)))))
        assertTrue("Pointer dereference" in waiting("Pointer dereference"))
        assertTrue("skill.c.pointer_dereference" in waiting(null))
        assertTrue("Pointer dereference hazır olduğunda" in
            ExplanationCopy.text(Reconsideration.WHEN_PREREQUISITE_READY, listOf(SkillMention(skill, "Pointer dereference"))))
        // With no Skill, the sentence says only that a prerequisite is not ready.
        val bare = ExplanationCopy.text(requireNotNull(Statement.ofCode("eligibility.blocked_hard_prerequisite",
            setOf("eligibility.blocked_hard_prerequisite"))))
        assertFalse("{skills}" in bare)
    }

    @Test
    fun `a date is never promised`() {
        assertTrue("tarih sözü değil" in ExplanationCopy.reconsiderationTemplates.getValue(Reconsideration.NEXT_PLAN))
        all.forEach { sentence ->
            assertFalse("yarın" in words(sentence) || Regex("\\d").containsMatchIn(sentence), sentence)
        }
    }
}
