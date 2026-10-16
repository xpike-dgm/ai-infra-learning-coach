package coach.curriculum

import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedTrigger
import coach.model.Criticality
import coach.model.TaskPurpose
import coach.model.VersionedRef
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The eighth package that ships (15H, `CQAX-v0`, `D-121`): `app-wiring/src/main/assets/curriculum_package_v8.txt`, the
 * content-QA supplement, read by the real strict parser after the seven packages it corrects, as the app reads them.
 *
 * It adds edges between Skills already published, the next version of published items it revises (retiring the version
 * each replaces by an appended record), a few new items and the next version of the tasks those change — and overwrites
 * nothing. If the shipped file stops parsing, adds a Skill or a lesson, carries an edge or an item again, leaves a revised
 * item's old version selectable, lets a task present a retired version, publishes a task version out of order, makes the
 * graph cyclic or publishes a critical Objective, this fails.
 */
class ShippedContentQaPackageTest {

    private val assets = File("../app-wiring/src/main/assets")
    private val first = File(assets, "curriculum_package.txt").readText()
    private val later = (2..7).map { File(assets, "curriculum_package_v$it.txt").readText() }
    private val text = File(assets, "curriculum_package_v8.txt").readText()
    private val earlier = (listOf(first) + later).fold(emptyList<PackageFormat.Parsed>()) { read, t -> read + PackageFormat.parse(t, read) }
    private val parsed = PackageFormat.parse(text, earlier)
    private val source = FileContentSource(later = { later + text }, source = { first })

    private val earlierItems = earlier.fold(emptyMap<VersionedRef, coach.model.AssessmentItem>()) { all, p -> all + p.items }
    private val earlierSkills = earlier.flatMap { p -> p.curriculum.skills.map { it.ref } }.toSet()
    private val earlierEdges = earlier.flatMap { it.curriculum.prerequisiteEdges }
    private val earlierTasks = earlier.flatMap { it.tasks }
    private val records = parsed.curriculum.validationRecords
    private val instant = records.map { it.validatedAtInstant }.distinct().single()

    @Test
    fun `the package is version 8, reads after the seven it corrects, and adds no Skill, Objective, Topic, lesson or label`() {
        assertNull(source.failure, "the shipped packages must read together")
        assertEquals((1..8).toList(), source.curriculumPackages().map { it.version })
        val curriculum = parsed.curriculum
        assertTrue(curriculum.skills.isEmpty() && curriculum.objectives.isEmpty() && curriculum.topics.isEmpty())
        assertTrue(curriculum.topicSkillLinks.isEmpty() && curriculum.misconceptions.isEmpty())
        assertTrue(parsed.explanations.isEmpty(), "content QA rewrites no lesson")
        assertTrue(parsed.items.keys.none { it in earlierItems }, "nothing published is carried again")
    }

    @Test
    fun `every edge joins two published Skills, is new, and the whole graph stays acyclic`() {
        val edges = parsed.curriculum.prerequisiteEdges
        assertTrue(edges.isNotEmpty())
        edges.forEach { edge ->
            assertTrue(edge.prerequisite in earlierSkills && edge.target in earlierSkills, "$edge")
            assertTrue(edge.prerequisite != edge.target)
            assertTrue(earlierEdges.none { it.prerequisite == edge.prerequisite && it.target == edge.target }, "$edge is already published")
            assertEquals("published", edge.lifecycleStatus)
            assertEquals("default_prg_v0", edge.strictnessProfile)
        }
        val hard = (earlierEdges + edges).filter { it.edgeKind == "hard" }.groupBy({ it.target }, { it.prerequisite })
        fun reaches(from: VersionedRef, to: VersionedRef, seen: MutableSet<VersionedRef> = mutableSetOf()): Boolean =
            hard[from].orEmpty().any { it == to || (seen.add(it) && reaches(it, to, seen)) }
        earlierSkills.forEach { assertTrue(!reaches(it, it), "$it requires itself through the hard graph") }
    }

    @Test
    fun `a revised item is the next version of a published one, same family, and the version it replaces is retired`() {
        val revised = parsed.items.values.filter { it.ref.version > 1 }
        val added = parsed.items.values.filter { it.ref.version == 1 }
        assertTrue(revised.isNotEmpty() && added.isNotEmpty())
        revised.forEach { item ->
            val old = VersionedRef(item.ref.logicalId, item.ref.version - 1)
            val published = assertNotNull(earlierItems[old], "${item.ref} revises nothing published")
            assertEquals(published.variantFamilyId, item.variantFamilyId, "a revision is the same family: a learner who saw ${old} has seen it")
            assertEquals(published.targetObjectives, item.targetObjectives)
            assertEquals(published.targetSkills, item.targetSkills)
            assertEquals(listOf(LifecycleStatus.RETIRED), records.filter { it.resource == old }.map { it.status }, "$old")
            assertEquals(listOf(LifecycleStatus.VALIDATED), records.filter { it.resource == item.ref }.map { it.status }, "${item.ref}")
        }
        added.forEach { assertEquals(listOf(LifecycleStatus.VALIDATED), records.filter { r -> r.resource == it.ref }.map { r -> r.status }, "${it.ref}") }
        // Nothing else is retired, and every record is this package's own, appended after what it retires.
        assertEquals(revised.map { VersionedRef(it.ref.logicalId, it.ref.version - 1) }.toSet(),
            records.filter { it.status == LifecycleStatus.RETIRED }.map { it.resource }.toSet())
        assertTrue(earlier.flatMap { it.curriculum.validationRecords }.all { it.validatedAtInstant < instant })
    }

    @Test
    fun `every item is judged at its own version by exactly one key, rubric or suite, and keeps its mapped wrong answers`() {
        parsed.items.values.forEach { item ->
            val judges = listOfNotNull(source.answerKeyFor(item.ref), source.rubricFor(item.ref), source.codeTestsFor(item.ref))
            assertEquals(1, judges.size, "${item.ref}")
            assertTrue("@v${item.ref.version}" in item.expectedAnswerOrRubricRef, "${item.ref} is judged by another version")
            assertTrue("external_ai" in item.allowedTools.prohibitedSolutionSources, "${item.ref}")
        }
        // A relabelled choice item whose wrong options an earlier supplement mapped keeps them, now on the new key.
        parsed.answerMisconceptions.forEach { mapping ->
            val key = assertNotNull(source.answerKeyFor(VersionedRef(mapping.key.logicalId.replace("answerkey.", "item."), mapping.key.version)))
            assertEquals(mapping.key, key.ref)
            assertTrue(key.wrongAnswerMisconceptions.any { it.text == mapping.text })
        }
    }

    @Test
    fun `a task is published again only as its next version, presents no retired item, and only the newest is offered`() {
        val retired = records.filter { it.status == LifecycleStatus.RETIRED }.map { it.resource }.toSet()
        assertTrue(parsed.tasks.isNotEmpty())
        parsed.tasks.forEach { task ->
            val newest = earlierTasks.filter { it.ref.logicalId == task.ref.logicalId }.maxOf { it.ref.version }
            assertEquals(newest + 1, task.ref.version, "${task.ref} is not the next version")
            assertTrue(task.items.none { it in retired }, "${task.ref} presents a retired version")
            assertEquals(LifecycleStatus.VALIDATED, task.validationStatus, "${task.ref}")
        }
        // Every task still offered presents only versions that are not retired.
        val offered = earlierTasks.map { it.ref.logicalId }.toSet().map { id -> (earlierTasks + parsed.tasks).filter { it.ref.logicalId == id }.maxBy { it.ref.version } }
        offered.forEach { task -> assertTrue(task.items.none { it in retired }, "${task.ref} still presents a retired item") }
        val storage = VersionedRef("skill.memory.storage_lifetime_intuition", 1)
        val teach = source.taskCandidates(LearningNeed("${NeedTrigger.NEW_LEARNING.id}:$storage", NeedTrigger.NEW_LEARNING,
            listOf(storage), Criticality.REQUIRED)).single { it.purpose == TaskPurpose.TEACH }
        assertTrue(VersionedRef("skill.c.pointer_dereference", 1) in teach.requiredSkills,
            "the lifetime lesson reads through pointers: its lesson now says so (${teach.requiredSkills})")
    }

    @Test
    fun `no Objective of any shipped package is critical, because the store keeps no critical gate fields`() {
        val objectives = (earlier.flatMap { it.curriculum.objectives } + parsed.curriculum.objectives)
        assertEquals(65, objectives.size)
        assertTrue(objectives.all { it.criticality == "standard" }, objectives.filter { it.criticality != "standard" }.toString())
    }
}
