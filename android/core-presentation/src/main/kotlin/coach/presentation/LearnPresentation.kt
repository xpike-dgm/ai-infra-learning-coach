package coach.presentation

import coach.model.TopicFacts

/**
 * `learn_overview` (17A, `VPLX-v0 / D-133`; `UXIA-v0` §5: "What is the learning map and where can I go?").
 *
 * Learn is the curriculum's **organization**: each area and its Topics, each with its state as 16C derived it, each
 * opening `topic_detail`. It is browse, not truth — every chip is the same [TopicPresentation.chip] Progress shows, so
 * the two can never disagree (`UXIA-v0` §7.2) — and the order is orientation, never a prerequisite chain (`UXIA-v0`
 * §10.2): prerequisites live at Skill level and are shown on the Topic and Skill details.
 *
 * Nothing here counts, ranks or completes: there is no "n of m", no percentage and no area-level state.
 */
object LearnCopy {
    const val TITLE = "Öğrenme haritası"
    const val INTRO = "Müfredat alanlara ve konulara ayrılmıştır. Bir konuyu açınca becerilerini, durumlarını ve neyi beklediklerini görürsün."
    const val ORDER_NOTE = "Bu sıra yalnız yol göstermek içindir, ön koşul zinciri değildir; ön koşullar beceriler arasındadır."
    const val ENGLISH_NOTE = "Teknik İngilizce paralel bir alandır; hiçbir teknik konu onu beklemez."
    const val LOADING = "Müfredat okunuyor."
    const val NOT_LOADED = "Müfredat henüz yüklenmedi; harita gösterilemiyor."
}

data class LearnArea(val id: String?, val label: String, val note: String?, val topics: List<TopicChip>)

data class LearnView(val intro: String, val orderNote: String, val areas: List<LearnArea>)

object LearnPresentation {

    /** `null` until the curriculum is read; an unloaded curriculum is said, never drawn as an empty map. */
    fun overview(facts: TopicFacts?): LearnView? {
        if (facts == null || !facts.curriculumLoaded) return null
        val order = CurriculumNames.topics.keys.withIndex().associate { (i, id) -> id to i }
        val chips = facts.topics.map(TopicPresentation::chip)
            .sortedWith(compareBy<TopicChip> { order[it.topic.logicalId] ?: Int.MAX_VALUE }.thenBy { it.topic.logicalId })
        val byArea = chips.groupBy { CurriculumNames.areaOf(it.topic.logicalId) }
        val known = CurriculumNames.areas.map { it.first }
        val areas = (known + byArea.keys.filter { it !in known }).mapNotNull { area ->
            byArea[area]?.let { topics ->
                LearnArea(
                    id = area,
                    label = CurriculumNames.areaLabel(area),
                    note = if (area == "english") LearnCopy.ENGLISH_NOTE else null,
                    topics = topics,
                )
            }
        }
        return LearnView(LearnCopy.INTRO, LearnCopy.ORDER_NOTE, areas)
    }
}
