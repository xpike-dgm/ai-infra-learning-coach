package coach.persistence

import coach.model.CurriculumPackage
import coach.model.ObjectiveRow
import coach.model.PublishOutcome
import coach.model.SkillRow
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * 13E's one store refinement against a real SQLite file: a Skill version's published Objectives, read back
 * exactly as they were published, in a stable order, and nothing for a Skill version never published.
 */
class ProgramChangeStorageTest {

    private val v1 = VersionedRef("skill.python.loops", 1)
    private val v2 = VersionedRef("skill.python.loops", 2)
    private val other = VersionedRef("skill.python.functions", 1)

    private fun skill(ref: VersionedRef) = SkillRow(ref, "Loops", "Trace a loop", "stable", "concept", "standard", false, "src", "authored")

    private val trace = ObjectiveRow(VersionedRef("objective.python.loops.trace", 1), v1, true, "critical",
        listOf("code_reading", "explanation"), listOf("code_reading"), "code_reading")
    private val explain = ObjectiveRow(VersionedRef("objective.python.loops.explain", 1), v1, false, "standard",
        listOf("explanation"), emptyList(), null)
    private val call = ObjectiveRow(VersionedRef("objective.python.functions.call", 1), other, true, "standard",
        listOf("code_writing"), listOf("code_writing"), "code_writing")

    @Test
    fun `a Skill version's Objectives come back as published, in a stable order`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val published = CurriculumPackage(
                version = 1, sourceRefs = "curriculum", provenance = "authored",
                skills = listOf(skill(v1), skill(other)),
                // Published out of order on purpose: the read order is the store's, not the package's.
                objectives = listOf(trace, call, explain),
            )
            assertIs<PublishOutcome.Published>(db.publishCurriculum(published, 1_789_000_000_000))

            assertEquals(listOf(explain, trace), db.objectivesOf(v1))
            assertEquals(listOf(call), db.objectivesOf(other))
            // A version that was never published has no Objectives, not the other version's.
            assertEquals(emptyList(), db.objectivesOf(v2))
        }
    }

    @Test
    fun `a new Skill version reads only its own Objectives`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.publishCurriculum(CurriculumPackage(version = 1, sourceRefs = "c", provenance = "authored",
                skills = listOf(skill(v1)), objectives = listOf(trace)), 1_789_000_000_000)
            val traceV2 = trace.copy(ref = VersionedRef("objective.python.loops.trace", 2), parentSkill = v2, criticality = "standard")
            db.publishCurriculum(CurriculumPackage(version = 2, sourceRefs = "c", provenance = "authored",
                skills = listOf(skill(v2)), objectives = listOf(traceV2)), 1_789_000_100_000)

            assertEquals(listOf(trace), db.objectivesOf(v1))
            assertEquals(listOf(traceV2), db.objectivesOf(v2))
        }
    }
}
