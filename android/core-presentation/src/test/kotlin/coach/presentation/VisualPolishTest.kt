package coach.presentation

import coach.model.AuthoredTask
import coach.model.ContentOrigin
import coach.model.EvaluatorAvailability
import coach.model.LessonTitles
import coach.model.LifecycleStatus
import coach.model.TaskPurpose
import coach.model.TaskServing
import coach.model.EvidenceSummary
import coach.model.NamedEntity
import coach.model.ObjectiveProgress
import coach.model.SkillDetailFacts
import coach.model.MasteryAxisState
import coach.model.PrerequisiteContext
import coach.model.PrerequisiteEligibility
import coach.model.RetentionAxis
import coach.model.SkillAxisReading
import coach.model.SkillPresentationProjection
import coach.model.SkillProgressRow
import coach.model.StoreStatus
import coach.model.TopicCoverage
import coach.model.TopicDetailFacts
import coach.model.TopicFacts
import coach.model.TopicMember
import coach.model.TopicReading
import coach.model.TopicRow
import coach.model.TopicStateProjection
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 17A (`VPLX-v0 / D-133`): the visual system is `VDSX-v0` applied, and it adds no meaning. Contrast is recomputed from
 * the token hex values for every Material role pair the theme draws, in both themes.
 */
class VisualPolishTest {

    private fun round2(value: Double) = round(value * 100) / 100

    @Test
    fun `the type scale has exactly VDSX-v0's eight roles and every Material slot maps to one of them`() {
        assertEquals(listOf("display", "title_large", "title", "body_large", "body", "label", "caption", "mono"), TypeRole.entries.map { it.id })
        assertEquals(TypeRole.entries.toSet(), TypeScale.roles.keys)
        // Only mono is monospace; nothing is smaller than 12sp, and leading is never tighter than the size.
        assertEquals(setOf(TypeRole.MONO), TypeScale.roles.filterValues { it.monospace }.keys)
        assertTrue(TypeScale.roles.values.all { it.sizeSp >= 12 && it.lineHeightSp >= it.sizeSp })
        assertEquals(15, TypeScale.materialSlots.size)
        assertTrue(TypeScale.materialSlots.values.none { it == TypeRole.MONO }, "mono is asked for by name, never through a Material slot")
        assertTrue(TypeScale.roles.getValue(TypeRole.BODY_LARGE).sizeSp >= 16, "the body text is the platform's size")
    }

    @Test
    fun `spacing is the declared 4dp rhythm and the touch target is one of its steps`() {
        assertEquals(listOf(4, 8, 12, 16, 24, 32, 48), Spacing.steps)
        assertTrue(Spacing.steps.all { it % 4 == 0 })
        assertTrue(DesignTokens.MINIMUM_TOUCH_TARGET_DP in Spacing.steps)
    }

    @Test
    fun `every Material colour role is a measured token in both themes`() {
        Theme.entries.forEach { theme ->
            MaterialColorRoles.roles.forEach { (role, token) ->
                assertTrue(DesignTokens.tokens(theme).containsKey(token), "$role -> $token is not a measured token")
            }
        }
        assertEquals(36, MaterialColorRoles.roles.size)
        // The fault tone stays Material's error role and no other role.
        assertEquals(setOf("error", "errorContainer"), MaterialColorRoles.roles.filterValues { it == "tone_fault" }.keys)
    }

    @Test
    fun `every Material role pair the theme draws meets the WCAG anchor in both themes`() {
        Theme.entries.forEach { theme ->
            fun hex(role: String) = DesignTokens.hex(theme, MaterialColorRoles.token(role))
            MaterialColorRoles.textPairs.forEach { (fg, bg) ->
                val ratio = Contrast.ratio(hex(fg), hex(bg))
                assertTrue(ratio >= DesignTokens.TEXT_CONTRAST_MIN, "$theme $fg on $bg is ${round2(ratio)}:1")
            }
            MaterialColorRoles.nonTextPairs.forEach { (fg, bg) ->
                val ratio = Contrast.ratio(hex(fg), hex(bg))
                assertTrue(ratio >= DesignTokens.NON_TEXT_CONTRAST_MIN, "$theme $fg on $bg is ${round2(ratio)}:1")
            }
        }
    }

    @Test
    fun `a chip's tone colour reads as text and as a border on both surfaces in both themes`() {
        Theme.entries.forEach { theme ->
            Tone.entries.forEach { tone ->
                ChipColors.surfaces.forEach { surface ->
                    val ratio = Contrast.ratio(DesignTokens.hex(theme, ChipColors.foreground(tone)), DesignTokens.hex(theme, surface))
                    assertTrue(ratio >= DesignTokens.TEXT_CONTRAST_MIN, "$theme ${tone.id} on $surface is ${round2(ratio)}:1")
                }
            }
        }
    }

    @Test
    fun `every tone has its own mark and only the fault tone has the warning mark`() {
        val marks = Tone.entries.associateWith(ToneMark::of)
        assertEquals(Tone.entries.size, marks.values.toSet().size)
        assertEquals(listOf(Tone.SYSTEM_FAULT), marks.filterValues { it == ToneMark.WARNING }.keys.toList())
        assertTrue(LearningTone.entries.none { ToneMark.of(it.tone) == ToneMark.WARNING })
    }

    @Test
    fun `text blocks keep every line as authored and find code paragraphs`() {
        val text = "Atama her zaman iki adımda çalışır:\n\nx = 5\nprint(x)\n\n\nBu yüzden şu satır anlamlıdır ve doğru okunur."
        val blocks = TextBlocks.split(text)
        assertEquals(listOf(BlockKind.PROSE, BlockKind.CODE, BlockKind.PROSE), blocks.map { it.kind })
        assertEquals(text.lines().filter { it.isNotBlank() }, blocks.flatMap { it.text.lines() })
    }

    @Test
    fun `a traceback and a shell session are code, a list of explained steps and an option list are not`() {
        assertTrue(TextBlocks.isCode("Traceback (most recent call last):\n  File \"cozum.py\", line 2, in <module>\n    print(sonuc)\nNameError: name 'sonuc' is not defined"))
        assertTrue(TextBlocks.isCode("mkdir -p kitap/roman\ncd kitap/roman\ncd .."))
        assertTrue(TextBlocks.isCode("int main(void) {\n    return 0;\n}"))
        assertFalse(TextBlocks.isCode("1. Önce sağ taraf hesaplanır (o andaki değerlerle).\n2. Sonra sonuç soldaki isme atanır; o ismin eski değeri unutulur."))
        assertFalse(TextBlocks.isCode("A) Bir şey aramak\nB) Bir şeyi silmek\nC) Programı kapatmak"))
        assertFalse(TextBlocks.isCode("Tırnak meselesi: print(\"7 * 6\") ekrana 7 * 6 yazar; tırnak içindeki her şey yazıdır."))
        // A sentence about code, with short words and apostrophes, is still a sentence (found on the emulator).
        assertFalse(TextBlocks.isCode("Kopyalama: b = a satırı çalıştığı anda a'nın değeri b'ye atanır. Sonra a değişse bile b değişmez; b o anki değeri almıştı."))
        assertTrue(TextBlocks.isCode("cd ../src\nls -a ."))
        // A bare answer with no code character is not claimed as code.
        assertFalse(TextBlocks.isCode("10 7 4 1"))
    }

    @Test
    fun `identifiers become words or are left out, and a study day is written in Turkish`() {
        assertEquals("anlatım", DisplayCopy.activityKind("content_explanation"))
        assertNull(DisplayCopy.activityKind("something_new"))
        assertNull(DisplayCopy.activityKind(null))
        assertEquals("Teknik İngilizce", DisplayCopy.track("technical_english"))
        assertNull(DisplayCopy.track("unknown_track"))
        assertEquals("26 Ekim 2026, Pazartesi", DisplayCopy.studyDay("2026-10-26"))
        assertEquals("1 Ocak 2027, Cuma", DisplayCopy.studyDay("2027-01-01"))
        assertEquals("not-a-date", DisplayCopy.studyDay("not-a-date"))
    }

    @Test
    fun `names fall back to the authored name, never to a guess`() {
        assertEquals("Program çalışma modeli", CurriculumNames.skill(VersionedRef("skill.computing.program_execution_model", 3), "Program Execution Model"))
        assertEquals("Some Skill", CurriculumNames.skill(VersionedRef("skill.x.some", 1), "Some Skill"))
        assertEquals("skill.x.some", CurriculumNames.skill(VersionedRef("skill.x.some", 1), null))
        assertEquals("Koşullar", CurriculumNames.topic(VersionedRef("topic.python.conditionals", 1), "Conditionals"))
        assertEquals("python", CurriculumNames.areaOf("topic.python.conditionals"))
        assertNull(CurriculumNames.areaOf("python.conditionals"))
        assertEquals(CurriculumNames.OTHER_AREA, CurriculumNames.areaLabel("unknown"))
        // Every Topic named belongs to a known area.
        assertTrue(CurriculumNames.topics.keys.all { id -> CurriculumNames.areas.any { it.first == CurriculumNames.areaOf(id) } })
    }

    private fun skillRow(ref: VersionedRef) = SkillProgressRow(
        ref, "Authored",
        SkillPresentationProjection.derive(SkillAxisReading(MasteryAxisState.NOT_YET_EVIDENCED, RetentionAxis.UNTRACKED, WeaknessAxis.NONE,
            PrerequisiteEligibility.ELIGIBLE, evidenced = false)),
        PrerequisiteContext(PrerequisiteEligibility.ELIGIBLE, emptyList(), emptyList(), emptyList(), emptyList()),
    )

    private fun topicRow(id: String, objective: VersionedRef = VersionedRef("objective.$id.o", 1)): TopicRow {
        val ref = VersionedRef(id, 1)
        val member = skillRow(VersionedRef("skill.$id", 1))
        return TopicRow(ref, "Authored", TopicStateProjection.derive(TopicReading(ref, false,
            listOf(TopicMember(member.skill, member.projection, true, emptyList(), emptyList(), false, listOf(objective to TopicCoverage.NOT_COVERED))))))
    }

    @Test
    fun `learn groups Topics by area in the declared order with the same chip Progress shows`() {
        val rows = listOf("topic.english.core_terms", "topic.python.iteration", "topic.c.toolchain", "topic.python.conditionals",
            "topic.python.execution", "topic.zzz.thing").map { topicRow(it) }
        val facts = TopicFacts(true, rows)
        val view = assertNotNull(LearnPresentation.overview(facts))
        assertEquals(listOf("Python", "C", "Teknik İngilizce", CurriculumNames.OTHER_AREA), view.areas.map { it.label })
        // The declared order, not the alphabetical order of the ids (conditionals, execution, iteration).
        assertEquals(listOf("Programın çalışması", "Koşullar", "Döngüler"), view.areas.first().topics.map { it.name })
        assertEquals(LearnCopy.ENGLISH_NOTE, view.areas.single { it.id == "english" }.note)
        // The chip is Progress's chip: one Topic, one truth.
        rows.forEach { row -> assertEquals(TopicPresentation.chip(row), view.areas.flatMap { it.topics }.single { it.topic == row.topic }) }
        assertTrue(view.orderNote.contains("ön koşul zinciri değildir"))
        assertNull(LearnPresentation.overview(null))
        assertNull(LearnPresentation.overview(TopicFacts(false, emptyList())))
        val texts = listOf(view.intro, view.orderNote, LearnCopy.ENGLISH_NOTE)
        listOf("%", "puan", "seviye", "tamamlandı", "başarısız").forEach { w -> assertTrue(texts.none { it.contains(w, ignoreCase = true) }, w) }
    }

    @Test
    fun `an Objective is named by its lesson title, or by its Skill, never by its identifier`() {
        val objective = VersionedRef("objective.topic.python.iteration.trace_loop", 1)
        val row = topicRow("topic.python.iteration", objective)
        val skill = VersionedRef("skill.topic.python.iteration", 1)
        val titled = TopicPresentation.detail(TopicDetailFacts(row, emptyList(), mapOf(skill to "Loops"), mapOf(objective to "Döngüyü izlemek")))
        assertEquals("Döngüyü izlemek", titled.coverage.single().label)
        val untitled = TopicPresentation.detail(TopicDetailFacts(row, emptyList(), mapOf(skill to "Loops")))
        assertEquals("Loops", untitled.coverage.single().label)
        assertFalse(untitled.coverage.single().label.contains("trace loop"))
    }

    @Test
    fun `skill detail and history name Objectives, Topics and days in words`() {
        val skill = VersionedRef("skill.python.conditionals", 2)
        val objective = VersionedRef("objective.python.conditionals.choose_branch", 1)
        val other = VersionedRef("objective.python.conditionals.no_lesson", 1)
        val facts = SkillDetailFacts(
            row = skillRow(skill),
            capabilityStatement = "…",
            objectives = listOf(objective, other).map { ObjectiveProgress(it, true, null, null, EvidenceSummary.of(emptyList()), emptyList()) },
            evidence = EvidenceSummary.of(emptyList()),
            skillNames = emptyMap(),
            topics = listOf(NamedEntity("topic.python.conditionals", 1, "Conditionals")),
            objectiveTitles = mapOf(objective to "Koşullar: hangi kol çalışır?"),
        )
        val view = ProgressPresentation.detail(facts)
        assertEquals(listOf("Koşullar: hangi kol çalışır?", "Koşullar: if, elif, else"), view.objectives.map { it.label })
        assertEquals(listOf("Koşullar"), view.topics)
        assertEquals("26 Ekim 2026, Pazartesi", HistoryDay("2026-10-26", emptyList()).label)
        assertEquals(HistoryCopy.NO_DAY, HistoryDay("", emptyList()).label)
    }

    @Test
    fun `an Objective's lesson title is its newest teach task's, never a practice task's`() {
        val objective = VersionedRef("objective.python.loops.trace", 1)
        fun task(id: String, version: Int, purpose: TaskPurpose, title: String) = AuthoredTask(
            ref = VersionedRef(id, version), title = title, primarySkill = VersionedRef("skill.python.loops", 1),
            targetObjectives = listOf(objective), purpose = purpose, activityKind = "content_explanation",
            serves = setOf(TaskServing.servedBy(purpose).first()), costMinutes = 10, validationStatus = LifecycleStatus.VALIDATED,
            contentOrigin = ContentOrigin.HUMAN_AUTHORED, items = listOf(VersionedRef("item.python.loops.one", 1)),
        )
        val tasks = listOf(
            task("task.python.loops_teach", 1, TaskPurpose.TEACH, "Eski başlık"),
            task("task.python.loops_teach", 2, TaskPurpose.TEACH, "Yeni başlık"),
            task("task.python.loops_practice", 3, TaskPurpose.PRACTICE, "Uygulama başlığı"),
        )
        assertEquals("Yeni başlık", LessonTitles.of(tasks, objective))
        assertNull(LessonTitles.of(tasks.filter { it.purpose != TaskPurpose.TEACH }, objective))
        assertNull(LessonTitles.of(tasks, VersionedRef("objective.python.loops.other", 1)))
    }

    @Test
    fun `the app is still opening until the store and today's facts are both in hand`() {
        val ai = EvaluatorAvailability.UNAVAILABLE
        assertEquals(CrossCuttingState.LOADING, AppHealth.atStartup(StoreStatus.Ready, ai, storeInHand = true, todayInHand = false).blocking)
        assertEquals(CrossCuttingState.LOADING, AppHealth.atStartup(StoreStatus.Ready, ai, storeInHand = false, todayInHand = true).blocking)
        assertTrue(AppHealth.atStartup(StoreStatus.Ready, ai, storeInHand = true, todayInHand = true).showsShell)
        // A failure is never hidden behind "opening".
        assertEquals(CrossCuttingState.ERROR_RECOVERABLE, AppHealth.atStartup(StoreStatus.RecoverableFailure, ai, false, false).blocking)
        // Whatever is in hand, a shell-less health always has something blocking to render.
        listOf(true, false).forEach { s -> listOf(true, false).forEach { t ->
            listOf(StoreStatus.Opening, StoreStatus.Ready, StoreStatus.RecoverableFailure).forEach { status ->
                val h = AppHealth.atStartup(status, ai, s, t)
                assertTrue(h.showsShell == (h.blocking == null))
                if (!(s && t)) assertNotNull(h.blocking)
            }
        } }
    }
}
