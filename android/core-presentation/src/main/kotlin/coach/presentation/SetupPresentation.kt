package coach.presentation

import coach.model.VersionedRef

/**
 * The first-run setup (18A, `D-139`; `UXIA-v0` §7.4): what it says, and which Skills an area ticked names.
 *
 * Areas are the curriculum's own grouping (the same as Learn's, `CurriculumNames.areaOf`), in Learn's order; ticking one
 * names the Skills its Topics place. Nothing here is a level, a test or a promise: the copy says that "I know this" is
 * not evidence, that a check not passed is not a failure, and that nothing has to be ticked.
 */
object SetupCopy {
    const val TITLE = "Hoş geldin"
    const val INTRO = "Bu uygulama her gün, senin ayırdığın süre içinde ne çalışacağını planlar."
    const val PRINCIPLE = "Zaman geçirmek ilerleme sayılmaz; ilerlemeyi kendi cevapların gösterir."
    const val LENGTH_TITLE = "Normal bir günde ne kadar zamanın var?"
    const val LENGTH_LABEL = "Normal gün süresi (dakika)"
    const val LENGTH_NOTE = "Bu bir üst sınır, hedef değil. Kısa ve yoğun gün sürelerini Profil'den değiştirebilirsin."
    const val KNOWN_TITLE = "Bildiğin alanlar var mı?"
    // Review (18A): a check joins the plan only when its Skill may start (`PRG-v0`); most of an area waits on a prerequisite
    // at first, so the copy promises neither "today" nor "short".
    const val KNOWN_NOTE =
        "İşaretlediğin alanlarda, derslerin bir kısmını atlayıp atlayamayacağını gösteren kontroller, her beceri başlatılabilir olduğunda planına girer; " +
            "o zamana kadar o dersler atlanmaz. 'Biliyorum' demek kanıt sayılmaz; kontrolü geçmeyen dersler normal sırasıyla gelir ve bu bir başarısızlık değildir."
    const val KNOWN_OPTIONAL = "Hiçbirini işaretlemek zorunda değilsin."
    const val START = "Başla"
    const val SKIP = "Şimdilik atla"
    const val SKIP_NOTE = "Atlarsan önerilen süreler geçerli olur ve öğrenme baştan başlar; hepsini sonra Profil'den ve konu ayrıntılarından değiştirebilirsin."

    fun topics(count: Int): String = "$count konu"
}

data class SetupArea(val id: String, val label: String, val note: String?, val topicCount: Int, val skills: List<VersionedRef>)

data class SetupView(val normalMinutes: Int, val areas: List<SetupArea>)

object SetupPresentation {

    /** [topics]: each Topic with the Skills it places. [normalMinutes]: the normal day as it stands (the preset until set). */
    fun of(topics: List<Pair<VersionedRef, List<VersionedRef>>>, normalMinutes: Int): SetupView {
        val byArea = topics.groupBy { CurriculumNames.areaOf(it.first.logicalId) }
        val known = CurriculumNames.areas.map { it.first }
        val areas = (known + byArea.keys.filter { it !in known }).mapNotNull { area ->
            val inArea = byArea[area] ?: return@mapNotNull null
            SetupArea(
                id = area ?: OTHER,
                label = CurriculumNames.areaLabel(area),
                note = if (area == "english") LearnCopy.ENGLISH_NOTE else null,
                topicCount = inArea.size,
                skills = inArea.flatMap { it.second }.distinct(),
            )
        }
        return SetupView(normalMinutes, areas)
    }

    /** The Skills the ticked areas name, each once; none when nothing is ticked. */
    fun placement(view: SetupView, ticked: Set<String>): List<VersionedRef> =
        view.areas.filter { it.id in ticked }.flatMap { it.skills }.distinct()

    private const val OTHER = "other"
}
