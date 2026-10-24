package coach.presentation

import coach.model.BaseProfileStatus
import coach.model.CefrBand
import coach.model.EnglishProfileProjection
import coach.model.EnglishProfileReading
import coach.model.EnglishSkillReading
import coach.model.EvidenceSummary
import coach.model.MasteryAxisState
import coach.model.MisconceptionContext
import coach.model.ObjectiveProgress
import coach.model.PrerequisiteContext
import coach.model.PrerequisiteEligibility
import coach.model.RetentionAxis
import coach.model.SkillAxisReading
import coach.model.SkillDetailFacts
import coach.model.SkillPresentationProjection
import coach.model.SkillProgressRow
import coach.model.TechnicalEnglishAnchors
import coach.model.TopicCoverage
import coach.model.TopicDetailFacts
import coach.model.TopicFacts
import coach.model.TopicMember
import coach.model.TopicReading
import coach.model.TopicRow
import coach.model.TopicState
import coach.model.TopicStateProjection
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import coach.model.WeaknessSignal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 16C (`PWPX-v0`): the Topic, English and weakness views say only what the derivations decided, in words, with the
 * labels `SPWX-v0` locked — no percentage, no level claim, no failure language, and a hypothesis only as a question.
 */
class TopicPresentationTest {

    private val skill = VersionedRef("skill.python.loops", 1)
    private val outside = VersionedRef("skill.python.basics", 1)
    private val objective = VersionedRef("objective.python.loops.trace", 1)
    private val topic = VersionedRef("topic.python.loops", 1)

    private fun row(
        ref: VersionedRef = skill,
        mastery: MasteryAxisState = MasteryAxisState.NOT_YET_EVIDENCED,
        retention: RetentionAxis = RetentionAxis.UNTRACKED,
        weakness: WeaknessAxis = WeaknessAxis.NONE,
    ) = SkillProgressRow(
        ref, ref.logicalId.substringAfterLast('.'),
        SkillPresentationProjection.derive(SkillAxisReading(mastery, retention, weakness, PrerequisiteEligibility.ELIGIBLE,
            evidenced = mastery != MasteryAxisState.NOT_YET_EVIDENCED)),
        PrerequisiteContext(PrerequisiteEligibility.ELIGIBLE, emptyList(), emptyList(), emptyList(), emptyList()),
    )

    private fun topicRow(started: Boolean, member: SkillProgressRow, blockers: List<VersionedRef> = emptyList(), coverage: TopicCoverage = TopicCoverage.NOT_COVERED) =
        TopicRow(topic, "Loops", TopicStateProjection.derive(TopicReading(topic, started,
            listOf(TopicMember(member.skill, member.projection, blockers.isEmpty(), blockers, emptyList(), false, listOf(objective to coverage))))))

    private val forbidden = listOf("%", "puan", "seviyen", "başarısız", "Resmî B", "sertifikalı", "İngilizcen", "geride kaldın", "seri")

    private fun assertClean(texts: List<String>) = texts.forEach { text ->
        forbidden.forEach { word -> assertFalse(text.contains(word, ignoreCase = true), "'$word' in: $text") }
    }

    @Test
    fun `every Topic state has its locked label and its declared tone, and none wears the fault tone`() {
        assertEquals(listOf("Kilitli", "Hazır", "Öğreniliyor", "Öğrenildi", "Tekrar Gerekebilir", "Pekiştirme Gerekli"),
            TopicState.entries.map(TopicCopy.stateLabels::getValue))
        assertTrue(TopicState.entries.none { it.tone.tone == Tone.SYSTEM_FAULT })
        assertEquals(LearningTone.POSITIVE_CONFIRMED, TopicState.MASTERED.tone)
        assertEquals(LearningTone.NEUTRAL, TopicState.WEAKENING.tone)
        assertClean(TopicCopy.stateTexts.values.toList())
    }

    @Test
    fun `the overview is an inventory of Topic states and each Topic's chip, never a ratio`() {
        val view = TopicPresentation.overview(TopicFacts(true, listOf(topicRow(false, row()), topicRow(false, row(), blockers = listOf(outside)))))!!
        assertEquals(listOf(TopicState.AVAILABLE to 1, TopicState.LOCKED to 1).sortedBy { it.first }, view.inventory.map { it.state to it.count }.sortedBy { it.first })
        assertEquals(0, view.recomputing)
        assertNull(TopicPresentation.overview(TopicFacts(false, emptyList())))
    }

    @Test
    fun `a locked Topic names what it waits for and says locked is not a verdict`() {
        val r = topicRow(false, row(), blockers = listOf(outside))
        val view = TopicPresentation.detail(TopicDetailFacts(r, listOf(row()), mapOf(outside to "Basics", skill to "Loops")))
        assertEquals("Kilitli", view.chip.label)
        assertTrue(view.text.contains("kalıcı bir hüküm değildir"))
        assertEquals(listOf(outside to "Basics"), view.reasons.single { it.title == TopicCopy.WAITING_FOR }.skills)
        assertEquals("Dersi henüz işlenmedi", view.coverage.single().coverage)
        assertTrue(view.placementNote.contains("ön koşul değildir"))
    }

    @Test
    fun `remediation is named on its own Skill and is never a penalty`() {
        val r = topicRow(true, row(mastery = MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = WeaknessAxis.REMEDIATION_REQUIRED))
        val view = TopicPresentation.detail(TopicDetailFacts(r, emptyList(), mapOf(skill to "Loops")))
        assertEquals("Pekiştirme Gerekli", view.chip.label)
        assertTrue(view.text.contains("ceza değil"))
        assertEquals(listOf(skill to "Loops"), view.reasons.single { it.title == TopicCopy.REMEDIATION }.skills)
    }

    @Test
    fun `skill detail shows a hypothesis only as the catalog's question and remediation with what closes it`() {
        val m = VersionedRef("misconception.python.loops.off_by_one", 1)
        val facts = SkillDetailFacts(
            row = row(mastery = MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = WeaknessAxis.REMEDIATION_REQUIRED),
            capabilityStatement = "…",
            objectives = listOf(ObjectiveProgress(objective, true, false, WeaknessSignal.CONFIRMED, EvidenceSummary.of(emptyList()),
                listOf(
                    MisconceptionContext(m, "Off by one", "Döngü bir eksik mi dönüyor?", WeaknessSignal.HYPOTHESIS),
                    MisconceptionContext(m, "Off by one", "Döngü bir eksik mi dönüyor?", WeaknessSignal.RESOLVED),
                ))),
            evidence = EvidenceSummary.of(emptyList()),
            skillNames = emptyMap(),
            topics = emptyList(),
        )
        val view = ProgressPresentation.detail(facts)
        val lines = view.objectives.single().misconceptions
        assertEquals("Açık soru: Döngü bir eksik mi dönüyor?", lines[0])
        assertTrue(lines[1].contains("artık güncel bir sorun değil"))
        val remediation = assertNotNull(view.remediation)
        assertTrue(assertNotNull(remediation.closure).contains("Bir görevi bitirmek tek başına kapatmaz"))
        assertNull(view.englishContext)
        assertClean(lines + remediation.text + remediation.closure!!)
        // No signal on the axis: nothing to say.
        assertNull(ProgressPresentation.detail(facts.copy(row = row())).remediation)
        assertTrue(assertNotNull(ProgressPresentation.detail(facts.copy(englishBand = CefrBand.A1)).englishContext).contains("kapı değildir"))
    }

    @Test
    fun `the English profile uses allowed wording only, names an unpublished band and keeps its history`() {
        val a1 = TechnicalEnglishAnchors.anchors.filter { it.second == CefrBand.A1 }
        val a2 = TechnicalEnglishAnchors.anchors.filter { it.second == CefrBand.A2 }
        fun reading(confirmedA1: Boolean, historical: CefrBand?) = EnglishProfileReading(
            skills = (a1 + a2).map { (id, band) ->
                val confirmed = confirmedA1 && band == CefrBand.A1
                EnglishSkillReading(id, band, row(VersionedRef(id, 1),
                    mastery = if (confirmed) MasteryAxisState.CONFIRMED_CURRENT else MasteryAxisState.NOT_YET_EVIDENCED,
                    retention = if (confirmed) RetentionAxis.FRESH else RetentionAxis.UNTRACKED), waived = false)
            } + TechnicalEnglishAnchors.anchors.filter { it.second == CefrBand.B1 }.map { (id, band) -> EnglishSkillReading(id, band, null, false) },
            historicalHighest = historical,
        )
        val complete = EnglishProfilePresentation.of(EnglishProfileProjection.derive(reading(true, CefrBand.A1)))!!
        assertEquals("Teknik İngilizce — A1 temel profili doğrulandı", complete.headline)
        assertEquals("Henüz yayımlanmadı", complete.bands.last().label)
        assertNull(complete.historicalNote)
        val lost = EnglishProfileProjection.derive(reading(false, CefrBand.A1))
        assertEquals(BaseProfileStatus.NOT_YET_COMPLETE, lost.status)
        val view = EnglishProfilePresentation.of(lost)!!
        assertEquals(EnglishProfileCopy.historical(CefrBand.A1), view.historicalNote)
        assertTrue(view.scopeNote.contains("resmî bir CEFR düzeyi") && view.gateNote.contains("kapı değildir"))
        assertClean(listOf(complete.headline, view.headline) + view.bands.map { it.label } + view.extensions.map { it.second })
        assertNull(EnglishProfilePresentation.of(null))
    }
}
