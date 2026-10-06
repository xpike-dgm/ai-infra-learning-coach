package coach.curriculum

import coach.model.AssessmentScope
import coach.model.Criticality
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MonthlyRole
import coach.model.NeedTrigger
import coach.model.VersionedRef
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The seventh package that ships (15G, `ACNX-v0`, `D-120`): `app-wiring/src/main/assets/curriculum_package_v7.txt`, a
 * supplement read by the real strict parser after the six packages it adds to, as the app reads them. It adds items,
 * new versions of tasks whose item lists grew, cross-topic transfer items and wrong-option misconception keys — and
 * changes nothing that was published. If the shipped file stops parsing, carries anything again, overwrites a published
 * version, lets a task spend a transfer item or maps a wrong answer to another Objective's label, this fails.
 */
class ShippedAssessmentPackageTest {

    private val assets = File("../app-wiring/src/main/assets")
    private val first = File(assets, "curriculum_package.txt").readText()
    private val later = (2..6).map { File(assets, "curriculum_package_v$it.txt").readText() }
    private val text = File(assets, "curriculum_package_v7.txt").readText()
    private val earlier = (listOf(first) + later).fold(emptyList<PackageFormat.Parsed>()) { read, t -> read + PackageFormat.parse(t, read) }
    private val parsed = PackageFormat.parse(text, earlier)
    private val source = FileContentSource(later = { later + text }, source = { first })

    private val earlierItems = earlier.flatMap { it.items.keys }.toSet()
    private val earlierObjectives = earlier.flatMap { p -> p.curriculum.objectives.map { it.ref } }.toSet()
    private val earlierTasks = earlier.flatMap { it.tasks }.associateBy { it.ref }

    @Test
    fun `the package is version 7, reads after the six it adds to, and adds no Skill, Objective, edge, Topic or label`() {
        assertNull(source.failure, "the shipped packages must read together")
        assertEquals((1..7).toList(), source.curriculumPackages().map { it.version })
        val curriculum = parsed.curriculum
        assertTrue(curriculum.skills.isEmpty() && curriculum.objectives.isEmpty() && curriculum.prerequisiteEdges.isEmpty())
        assertTrue(curriculum.topics.isEmpty() && curriculum.topicSkillLinks.isEmpty() && curriculum.misconceptions.isEmpty())
        assertTrue(parsed.explanations.isEmpty(), "a supplement writes no lesson")
        assertTrue(parsed.items.keys.none { it in earlierItems }, "nothing published is carried again")
        assertTrue(parsed.items.values.all { it.ref.version == 1 })
        assertEquals(parsed.items.keys, curriculum.resources.map { it.ref }.toSet(), "every added item is a published resource")
    }

    @Test
    fun `every added item measures an Objective already published and was validated, with nothing left a candidate`() {
        parsed.items.values.forEach { item ->
            assertTrue(item.targetObjectives.isNotEmpty() && item.targetObjectives.all { it in earlierObjectives }, "${item.ref}")
            assertEquals(item, source.assessmentItem(item.ref), "${item.ref} is not served with the earlier packages")
        }
        val records = parsed.curriculum.validationRecords.associate { it.resource to it.status }
        assertEquals(parsed.items.keys, records.keys)
        assertTrue(records.values.all { it == LifecycleStatus.VALIDATED })
        assertTrue(parsed.tasks.all { it.validationStatus == LifecycleStatus.VALIDATED })
    }

    @Test
    fun `every item is judged by exactly one of a key, a rubric or a test suite`() {
        parsed.items.values.forEach { item ->
            val judges = listOfNotNull(source.answerKeyFor(item.ref), source.rubricFor(item.ref), source.codeTestsFor(item.ref))
            assertEquals(1, judges.size, "${item.ref}")
            assertTrue("external_ai" in item.allowedTools.prohibitedSolutionSources, "${item.ref}: an AI never does the learner's work")
        }
    }

    @Test
    fun `a task is published again only as version 2 of a published task whose items grew, and only the newest is offered`() {
        assertTrue(parsed.tasks.isNotEmpty())
        parsed.tasks.forEach { task ->
            assertEquals(2, task.ref.version, "${task.ref}")
            val published = assertNotNull(earlierTasks[VersionedRef(task.ref.logicalId, 1)], "${task.ref} has no published version 1")
            assertTrue(task.items.containsAll(published.items) && task.items.size > published.items.size, "${task.ref} only grows")
            assertEquals(published.primarySkill, task.primarySkill)
            assertEquals(published.purpose, task.purpose)
            assertEquals(published.serves, task.serves)
            assertEquals(published.explanations, task.explanations, "a supplement writes no lesson")
            val need = LearningNeed("${task.serves.first().id}:${task.primarySkill}", task.serves.first(), listOf(task.primarySkill),
                Criticality.REQUIRED)
            val offered = source.taskCandidates(need).map { it.id }
            assertTrue(offered.any { it.startsWith("authored:${task.ref}:") }, "$offered")
            assertTrue(offered.none { it.startsWith("authored:${published.ref}:") }, "the published version stays readable, never offered")
        }
    }

    @Test
    fun `a transfer item names its cross-topic context and is reserved for the month's transfer slot, never spent by a task`() {
        val transfer = parsed.items.values.filter { it.transferProfile != null }
        assertTrue(transfer.isNotEmpty())
        val inTasks = (earlier.flatMap { it.tasks } + parsed.tasks).flatMap { it.items }.toSet()
        transfer.forEach { item ->
            assertTrue(item.transferProfile!!.crossTopic, "${item.ref}")
            assertNotNull(item.contextFamilyId, "${item.ref}")
            assertTrue(AssessmentScope.MONTHLY_CAPABILITY in item.scopeEligibility, "${item.ref}")
            assertTrue(MonthlyRole.CROSS_TOPIC_TRANSFER in item.blueprintRoles, "${item.ref}")
            assertTrue(item.ref !in inTasks, "${item.ref} would be seen before its slot")
        }
        // Every other added item claims no transfer: a harder item of the same lesson is an application (`AIV-v0` §16).
        assertTrue(parsed.items.values.filter { it.transferProfile == null }.none { MonthlyRole.CROSS_TOPIC_TRANSFER in it.blueprintRoles })
    }

    @Test
    fun `every wrong-option key names a label of its own key's Objective, and an accepted answer never names one`() {
        assertTrue(parsed.answerMisconceptions.isNotEmpty())
        val keys = (earlier.flatMap { it.answerKeys } + parsed.answerKeys).map { it.ref }.toSet()
        val labels = earlier.flatMap { p -> p.curriculum.misconceptions }.associateBy { it.ref }
        parsed.answerMisconceptions.forEach { m ->
            assertTrue(m.key in keys, "${m.key}")
            val served = parsed.items.keys.plus(earlierItems).firstNotNullOfOrNull { ref -> source.answerKeyFor(ref)?.takeIf { it.ref == m.key } }
            val key = assertNotNull(served, "${m.key} is not served")
            assertEquals(assertNotNull(labels[m.misconception]).objective, key.objective, "${m.key} '${m.text}'")
            assertTrue(!key.accepts(m.text))
            assertEquals(m.misconception, key.misconceptionFor(m.text))
        }
        // Mapped wrong answers change nothing a key accepts.
        earlier.flatMap { it.answerKeys }.forEach { published ->
            val now = parsed.items.keys.plus(earlierItems).firstNotNullOfOrNull { ref -> source.answerKeyFor(ref)?.takeIf { it.ref == published.ref } }
            if (now != null) assertEquals(published.answers, now.answers)
        }
    }

    @Test
    fun `the supplement answers no need a task did not answer before, and a diagnostic is still answered by nothing`() {
        val skills = earlier.flatMap { p -> p.curriculum.skills.map { it.ref } }
        skills.forEach { skill ->
            for (trigger in NeedTrigger.entries) {
                val need = LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)
                val before = FileContentSource(later = { later }, source = { first }).taskCandidates(need).map { it.purpose }
                assertEquals(before, source.taskCandidates(need).map { it.purpose }, "$trigger for $skill")
            }
        }
    }
}
