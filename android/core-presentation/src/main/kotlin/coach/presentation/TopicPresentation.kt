package coach.presentation

import coach.model.TopicCoverage
import coach.model.TopicDetailFacts
import coach.model.TopicFacts
import coach.model.TopicProjection
import coach.model.TopicRow
import coach.model.TopicState
import coach.model.VersionedRef

/**
 * `SPWX-v0 / D-072` §§6, 9 as pure functions (16C, `PWPX-v0`): a Topic's state is **orchestration, not mastery**. It is
 * never a percentage, never an average of Skills and never a prerequisite claim — none of these types holds a total to
 * divide by — and every state comes from [coach.model.TopicStateProjection], the one function the read uses.
 */

/** Working Turkish microcopy (17 owns its polish). The six state labels are `SPWX-v0` §6's, locked. */
object TopicCopy {
    val stateLabels: Map<TopicState, String> = mapOf(
        TopicState.LOCKED to "Kilitli",
        TopicState.AVAILABLE to "Hazır",
        TopicState.LEARNING to "Öğreniliyor",
        TopicState.MASTERED to "Öğrenildi",
        TopicState.WEAKENING to "Tekrar Gerekebilir",
        TopicState.REMEDIATION_REQUIRED to "Pekiştirme Gerekli",
    )

    /** What each state means, said plainly; none of them is a verdict about the learner (`TSM-v0` §§4–9). */
    val stateTexts: Map<TopicState, String> = mapOf(
        // `locked_is_permanent_verdict: false` (`SPWX-v0` §6) — and the reason is always reachable.
        TopicState.LOCKED to "Bu konuya başlamak için önce başka konulardaki bazı beceriler gerekiyor. Kilitli kalıcı bir hüküm değildir.",
        TopicState.AVAILABLE to "Başlanabilir. Bu konuda henüz kayıtlı bir çalışma yok; bu öğrenildiği anlamına gelmez.",
        TopicState.LEARNING to "Bu konuda çalışma başladı. Öğrenildi sayılması için her becerinin doğrulanması ve her hedefin dersinin işlenmesi ya da tanısal olarak geçilmesi gerekiyor.",
        TopicState.MASTERED to "Konunun her becerisi doğrulandı ve her hedefi kapsandı. Bu, konunun bir daha sorulmayacağı anlamına gelmez.",
        // `weakening` never means the learning was erased (`TSM-v0` §8).
        TopicState.WEAKENING to "Konu öğrenilmişti; bazı becerilerde kalıcılık sinyali var. Öğrenme silinmedi; kısa bir kontrol yeterli olabilir.",
        // Not a penalty, and coverage stays (`TSM-v0` §9).
        TopicState.REMEDIATION_REQUIRED to "Bazı becerilerde hedefli pekiştirme gerekiyor. Bu bir ceza değil; konunun geri kalanı ve işlenmiş dersler silinmez.",
    )

    const val RECOMPUTING = "Yeniden hesaplanıyor"
    const val RECOMPUTING_TEXT = "Bazı becerilerin durumu yeniden hesaplanıyor; konunun durumu tahmin edilmiyor."
    const val TITLE = "Konular"
    const val INVENTORY_NOTE = "Bugünkü durumun envanteri: her satır o durumdaki konu sayısı."
    const val WAITING_FOR = "Önce bunlar gerekiyor"
    const val METADATA = "Ön koşul bilgisinde sorun var; konu tahminle açılmıyor."
    const val NOT_CONFIRMED = "Henüz doğrulanmamış beceriler"
    const val RETENTION_SIGNAL = "Kalıcılık sinyali olan beceriler"
    const val REMEDIATION = "Pekiştirme gereken beceriler"
    const val SKILLS = "Bu konudaki beceriler"
    const val COVERAGE = "Hedeflerin kapsamı"
    // Placement is orientation only, never a dependency chain (`SPWX-v0` §9).
    const val PLACEMENT_NOTE = "Bir becerinin bu konuda yer alması bir ön koşul değildir; ön koşullar beceri düzeyindedir."

    val coverageLabels: Map<TopicCoverage, String> = mapOf(
        TopicCoverage.LESSON_WORKED to "Dersi işlendi",
        TopicCoverage.DIAGNOSTIC_WAIVER to "Tanısal olarak geçildi",
        TopicCoverage.NOT_COVERED to "Dersi henüz işlenmedi",
        TopicCoverage.NO_LESSON_AUTHORED to "Bu hedef için henüz ders yazılmadı",
    )
}

/** One Topic in a list: its state as text and tone, or the projection's recomputing status. */
data class TopicChip(
    val topic: VersionedRef,
    val name: String,
    val state: TopicState?,
    val label: String,
    val tone: Tone,
)

data class TopicInventoryLine(val state: TopicState, val label: String, val count: Int)

data class TopicOverviewView(
    val inventory: List<TopicInventoryLine>,
    val recomputing: Int,
    val topics: List<TopicChip>,
)

data class NamedSkills(val title: String, val skills: List<Pair<VersionedRef, String>>)

data class CoverageLine(val objective: VersionedRef, val label: String, val coverage: String)

data class TopicDetailView(
    val topic: VersionedRef,
    val name: String,
    val chip: TopicChip,
    val text: String,
    /** The facts behind the state, each list naming its Skills so they can be opened. */
    val reasons: List<NamedSkills>,
    val metadataNote: String?,
    val skills: List<SkillChip>,
    val coverage: List<CoverageLine>,
    val placementNote: String,
)

object TopicPresentation {

    /** The Topics section of Progress: a labelled inventory and each Topic's chip, never a ratio or a bar. */
    fun overview(facts: TopicFacts?): TopicOverviewView? {
        if (facts == null || !facts.curriculumLoaded) return null
        val chips = facts.topics.map(::chip)
        val inventory = TopicState.entries.mapNotNull { state ->
            val count = chips.count { it.state == state }
            if (count == 0) null else TopicInventoryLine(state, TopicCopy.stateLabels.getValue(state), count)
        }
        return TopicOverviewView(inventory, chips.count { it.state == null }, chips)
    }

    /** `topic_detail` (`SPWX-v0` §9): the state, why it holds, the Skill structure and each Objective's coverage. */
    fun detail(facts: TopicDetailFacts): TopicDetailView {
        val row = facts.row
        fun named(title: String, refs: List<VersionedRef>) =
            if (refs.isEmpty()) null else NamedSkills(title, refs.map { it to CurriculumNames.skill(it, facts.skillNames[it]) })
        val projection = row.projection
        val (text, reasons, metadata) = when (projection) {
            is TopicProjection.Recomputing -> Triple(
                TopicCopy.RECOMPUTING_TEXT,
                listOfNotNull(named(TopicCopy.RECOMPUTING, projection.skills)),
                null,
            )
            is TopicProjection.Derived -> Triple(
                TopicCopy.stateTexts.getValue(projection.state),
                listOfNotNull(
                    named(TopicCopy.WAITING_FOR, projection.waitingFor),
                    named(TopicCopy.REMEDIATION, projection.remediation),
                    named(TopicCopy.RETENTION_SIGNAL, projection.retentionSignal),
                    named(TopicCopy.NOT_CONFIRMED, projection.notYetConfirmed),
                ),
                if (projection.metadataProblem) TopicCopy.METADATA else null,
            )
        }
        val coverage = row.projection.reading.members.flatMap { member ->
            // 17A: an Objective is named by its lesson's own title, or by its Skill when no lesson is authored — never by
            // its identifier.
            member.objectives.map { (objective, c) ->
                val label = facts.objectiveTitles[objective] ?: CurriculumNames.skill(member.skill, facts.skillNames[member.skill])
                CoverageLine(objective, label, TopicCopy.coverageLabels.getValue(c))
            }
        }
        return TopicDetailView(
            topic = row.topic,
            name = CurriculumNames.topic(row.topic, row.name),
            chip = chip(row),
            text = text,
            reasons = reasons,
            metadataNote = metadata,
            skills = facts.members.map(ProgressPresentation::chip),
            coverage = coverage,
            placementNote = TopicCopy.PLACEMENT_NOTE,
        )
    }

    fun chip(row: TopicRow): TopicChip = when (val p = row.projection) {
        is TopicProjection.Derived -> TopicChip(row.topic, CurriculumNames.topic(row.topic, row.name), p.state, TopicCopy.stateLabels.getValue(p.state), p.state.tone.tone)
        is TopicProjection.Recomputing -> TopicChip(row.topic, CurriculumNames.topic(row.topic, row.name), null, TopicCopy.RECOMPUTING, ProgressState.RECOMPUTING_PROJECTION.tone)
    }
}
