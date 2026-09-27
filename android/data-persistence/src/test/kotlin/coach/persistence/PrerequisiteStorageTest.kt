package coach.persistence

import coach.model.CurriculumPackage
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteSnapshot
import coach.model.PublishOutcome
import coach.model.SkillRow
import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The prerequisite graph and the readiness projection against real SQLite (12B, `TVSX-v0` tier T2).
 *
 * `data-persistence` may not depend on `core-engines` or `core-application`, so — as in 11B and
 * 12A — the gate's decisions are T1 checks, and what needs the real store lives here: that the graph
 * reads back pinned to its target's version and unfiltered, that reading it writes nothing, and that
 * work done on a candidate that should have waited reaches the mastery engine as contaminated.
 */
class PrerequisiteStorageTest {

    private val at = StudyTimestamp(1_789_000_000_000, "2026-09-21", 3 * 3600)
    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val memoryAddress = VersionedRef("skill.c.memory_address", 1)
    private val linkedList = VersionedRef("skill.c.linked_list_insert", 1)
    private val linkedListV2 = VersionedRef("skill.c.linked_list_insert", 2)

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use(block)

    private fun skillRow(ref: VersionedRef, critical: Boolean = false) =
        SkillRow(ref, ref.logicalId, "capability", "published", "concept", "standard", critical, "src", "authored")

    private fun edge(from: VersionedRef, to: VersionedRef, version: Int = 1, kind: String = "hard", lifecycle: String = "published") =
        PrerequisiteEdge(from, to, version, kind, "conceptual_dependency", "default_prg_v0", lifecycle, "authored")

    private fun SqlitePersistence.publishGraph() {
        val v1 = CurriculumPackage(
            version = 1, sourceRefs = "curriculum/decomposition", provenance = "authored",
            skills = listOf(skillRow(pointer, critical = true), skillRow(memoryAddress), skillRow(linkedList)),
            prerequisiteEdges = listOf(
                edge(pointer, linkedList, version = 1, kind = "hard"),
                edge(pointer, linkedList, version = 2, kind = "soft", lifecycle = "draft"),
                edge(memoryAddress, linkedList, kind = "soft", lifecycle = "retired"),
                edge(memoryAddress, pointer),
            ),
        )
        assertIs<PublishOutcome.Published>(publishCurriculum(v1, at.instantEpochMillis))
        val v2 = CurriculumPackage(
            version = 2, sourceRefs = "curriculum/decomposition", provenance = "authored",
            skills = listOf(skillRow(linkedListV2)),
            prerequisiteEdges = listOf(edge(memoryAddress, linkedListV2)),
        )
        assertIs<PublishOutcome.Published>(publishCurriculum(v2, at.instantEpochMillis))
    }

    @Test
    fun `edges into a target come back in every lifecycle and version, in a stable order`() {
        withStore { db ->
            db.publishGraph()
            val edges = db.prerequisiteEdgesInto(linkedList)
            assertEquals(
                listOf(memoryAddress to 1, pointer to 1, pointer to 2),
                edges.map { it.prerequisite to it.edgeVersion },
            )
            assertEquals(listOf("retired", "published", "draft"), edges.map { it.lifecycleStatus })
            assertEquals(listOf("soft", "hard", "soft"), edges.map { it.edgeKind })
            assertTrue(edges.all { it.target == linkedList && it.strictnessProfile == "default_prg_v0" })
        }
    }

    @Test
    fun `an edge into another version of the target is not returned`() {
        withStore { db ->
            db.publishGraph()
            assertEquals(listOf(memoryAddress), db.prerequisiteEdgesInto(linkedListV2).map { it.prerequisite })
            assertTrue(db.prerequisiteEdgesInto(linkedList).none { it.target == linkedListV2 })
            assertEquals(emptyList(), db.prerequisiteEdgesInto(VersionedRef("skill.unknown", 1)))
        }
    }

    @Test
    fun `a Skill reads back with its critical flag, and an unpublished version reads as null`() {
        withStore { db ->
            db.publishGraph()
            assertEquals(true, db.skill(pointer)?.criticalPrerequisite)
            assertEquals(false, db.skill(linkedList)?.criticalPrerequisite)
            assertEquals(skillRow(pointer, critical = true), db.skill(pointer))
            assertNull(db.skill(VersionedRef(pointer.logicalId, 7)))
        }
    }

    @Test
    fun `reading the graph writes nothing`() {
        withStore { db ->
            db.publishGraph()
            val watermark = db.truthWatermark()
            val edges = db.count("skill_prerequisite_edge")
            repeat(3) {
                db.prerequisiteEdgesInto(linkedList)
                db.skill(pointer)
            }
            assertEquals(watermark, db.truthWatermark())
            assertEquals(edges, db.count("skill_prerequisite_edge"))
            assertEquals(0, db.count("prerequisite_readiness"))
        }
    }

    @Test
    fun `a readiness row round-trips with its provenance`() {
        withStore { db ->
            db.publishGraph()
            val row = ProjectionRecord(
                key = "prerequisite_readiness:${pointer.logicalId}@v1",
                policyVersion = "PRG-v0", truthWatermark = 0, builtAtInstant = at.instantEpochMillis,
                inputCurriculumVersion = 2, payload = mapOf("state" to "not_ready"),
            )
            db.writeProjection(row)
            assertEquals(row, db.readProjection(row.key))
            assertNull(db.readProjection("prerequisite_readiness:${pointer.logicalId}@v2"))
        }
    }

    @Test
    fun `an attempt made on a blocked candidate is read back as contaminated`() {
        withStore { db ->
            val objective = VersionedRef("objective.c.linked_list_insert.trace", 1)
            db.inTransaction {
                val attemptId = db.appendTruth(Fixtures.attempt())
                val evidenceId = db.appendTruth(
                    TruthRecord(
                        "evidence_event", at,
                        mapOf(
                            "skill_logical_id" to linkedList.logicalId,
                            "skill_version" to "1",
                            "evidence_type" to "code_reading",
                            "source_attempt_id" to attemptId.toString(),
                            "outcome" to "negative",
                            "evaluator_status" to "verified",
                            "independence_class" to "independent",
                            "contested" to "0",
                            "correctness_or_rubric_result" to "0.0",
                            "prerequisite_snapshot" to PrerequisiteSnapshot.CONTAMINATED,
                        ),
                    )
                )
                db.appendTruth(
                    TruthRecord(
                        "evidence_event_objective", at,
                        mapOf(
                            "evidence_event_id" to evidenceId.toString(),
                            "objective_logical_id" to objective.logicalId,
                            "objective_version" to "1",
                        ),
                    )
                )
            }
            // The failure is kept in history, and the engine is told it says nothing about the target.
            assertEquals(false, db.evidenceFor(objective).single().prerequisiteValid)
            assertEquals(PrerequisiteSnapshot.CONTAMINATED, SqlitePersistence.CONTAMINATED)
        }
    }
}
