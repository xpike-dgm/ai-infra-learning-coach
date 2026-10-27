package coach.wiring

import coach.curriculum.FileContentSource
import coach.curriculum.PackageFormat
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.presentation.BlockKind
import coach.presentation.CurriculumNames
import coach.presentation.DisplayCopy
import coach.presentation.TextBlocks
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 17A (`VPLX-v0 / D-133`) against the eight shipped packages: every name the screens show is a word, never an
 * identifier, and drawing code in the mono role never changes a line of what was authored.
 */
class VisualPolishCourseTest {

    private val names = listOf("", "_v2", "_v3", "_v4", "_v5", "_v6", "_v7", "_v8")
    private val texts = names.map { File("src/main/assets/curriculum_package$it.txt").readText() }
    private val parsed = texts.fold(emptyList<PackageFormat.Parsed>()) { earlier, text -> earlier + PackageFormat.parse(text, earlier) }
    private val content = FileContentSource(later = { texts.drop(1) }, source = { texts.first() })

    @Test
    fun `every shipped Topic and Skill has a Turkish name and every Topic a known area`() {
        val topics = parsed.flatMap { it.curriculum.topics }.map { it.logicalId }.toSet()
        val skills = parsed.flatMap { it.curriculum.skills }.map { it.ref.logicalId }.toSet()
        assertEquals(27, topics.size)
        assertEquals(60, skills.size)
        assertEquals(topics, CurriculumNames.topics.keys, "the Topic names are exactly the shipped Topics")
        assertEquals(skills, CurriculumNames.skills.keys, "the Skill names are exactly the shipped Skills")
        val known = CurriculumNames.areas.map { it.first }.toSet()
        assertTrue(topics.all { CurriculumNames.areaOf(it) in known })
        // A name is a label, never a claim about the learner.
        (CurriculumNames.topics.values + CurriculumNames.skills.values).forEach { name ->
            listOf("%", "tamamlandı", "öğrenildi", "başarı", "seviye").forEach { w -> assertTrue(!name.contains(w, ignoreCase = true), "$w in $name") }
        }
    }

    @Test
    fun `every shipped activity kind has a Turkish label`() {
        val kinds = parsed.flatMap { it.tasks }.map { it.activityKind }.toSet()
        assertTrue(kinds.isNotEmpty())
        assertTrue(kinds.all { DisplayCopy.activityKind(it) != null }, "unlabelled: ${kinds.filter { DisplayCopy.activityKind(it) == null }}")
    }

    @Test
    fun `every Objective a lesson teaches is named by that lesson's own title`() {
        val teach = parsed.flatMap { it.tasks }.filter { it.purpose == TaskPurpose.TEACH }
        val taught = teach.flatMap { it.targetObjectives }.toSet()
        assertTrue(taught.size >= 60)
        taught.forEach { objective ->
            val title = assertNotNull(content.lessonTitleFor(objective), "$objective")
            assertTrue(title.isNotBlank() && !title.contains("objective."))
            // The newest teach task's own title — never a practice task's, never an older version's.
            val newest = teach.filter { objective in it.targetObjectives }.sortedWith(compareByDescending<coach.model.AuthoredTask> { it.ref.version }
                .thenBy { it.ref.logicalId }).first()
            assertEquals(newest.title, title)
        }
        // An Objective only practice tasks name has no lesson title.
        val practiceOnly = parsed.flatMap { it.tasks }.filter { it.purpose != TaskPurpose.TEACH }.flatMap { it.targetObjectives }.toSet() - taught
        practiceOnly.forEach { assertEquals(null, content.lessonTitleFor(it), "$it") }
        assertEquals(null, content.lessonTitleFor(VersionedRef("objective.none.nothing", 1)))
    }

    @Test
    fun `code blocks in shipped lessons and prompts keep every line as authored`() {
        val authored = parsed.flatMap { p -> p.documents.values + p.explanations.map { it.text } }
        var code = 0
        authored.forEach { text ->
            val blocks = TextBlocks.split(text)
            assertEquals(text.lines().filter { it.isNotBlank() }, blocks.flatMap { it.text.lines() })
            code += blocks.count { it.kind == BlockKind.CODE }
        }
        // Recorded so a change to the rule is visible: the shipped course has hundreds of code paragraphs.
        println("17A code paragraphs in the shipped course: $code of ${authored.sumOf { TextBlocks.split(it).size }}")
        assertTrue(code > 500, "code paragraphs: $code")
    }
}
