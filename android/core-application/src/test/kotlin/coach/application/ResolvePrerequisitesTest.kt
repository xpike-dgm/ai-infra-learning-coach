package coach.application

import coach.model.CurriculumPackage
import coach.model.EvidenceRow
import coach.model.MasteryAxisState
import coach.model.ObjectiveEvidenceProfile
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteEligibility
import coach.model.PrerequisiteReadiness
import coach.model.PublishOutcome
import coach.model.ReadinessNote
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StudyTimestamp
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Asking the gate, and rebuilding the one state family it owns. The storage itself is proven
 * against SQLite in T2; here the question is what is read, what is written, and what never is.
 */
class ResolvePrerequisitesTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val linkedList = VersionedRef("skill.c.linked_list_insert", 1)
    private val memoryAddress = VersionedRef("skill.c.memory_address", 1)
    private val malloc = VersionedRef("skill.c.dynamic_memory_basic", 1)

    private class GraphStore : PersistencePort {
        val skills = mutableMapOf<VersionedRef, SkillRow>()
        val edges = mutableListOf<PrerequisiteEdge>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val written = mutableListOf<ProjectionRecord>()
        var curriculumVersion: Int? = 1

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long = error("the prerequisite gate writes no truth")
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) {
            written += record
            projections[record.key] = record
        }
        override fun curriculumPublished(): Boolean = curriculumVersion != null
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome =
            error("the gate publishes nothing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("the gate reads no evidence")
        override fun truthWatermark(): Long = 99
        override fun latestCurriculumVersion(): Int? = curriculumVersion
        override fun skill(ref: VersionedRef): SkillRow? = skills[ref]
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> =
            edges.filter { it.target == target }
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-21", 3 * 3600)
    }

    private fun GraphStore.publish(vararg refs: VersionedRef, critical: Set<VersionedRef> = emptySet()) {
        refs.forEach {
            skills[it] = SkillRow(it, it.logicalId, "capability", "published", "concept", "standard",
                it in critical, "src", "authored")
        }
    }

    private fun GraphStore.hard(from: VersionedRef, to: VersionedRef, lifecycle: String = "published") {
        edges += PrerequisiteEdge(from, to, 1, "hard", "conceptual_dependency", "default_prg_v0", lifecycle, "authored")
    }

    private fun GraphStore.mastery(
        skill: VersionedRef,
        mastery: MasteryAxisState,
        retention: String = "not_yet_evaluated",
        weakness: String = "not_yet_evaluated",
        watermark: Long = 40,
    ) {
        projections["skill_state:${skill.logicalId}@v${skill.version}"] = ProjectionRecord(
            key = "skill_state:${skill.logicalId}@v${skill.version}",
            policyVersion = "GRE-v0", truthWatermark = watermark, builtAtInstant = 1, inputCurriculumVersion = 1,
            payload = mapOf(
                "mastery_axis_state" to mastery.id,
                "retention_axis_state" to retention,
                "prerequisite_axis_state" to "not_yet_evaluated",
                "weakness_axis_state" to weakness,
                "primary_presentation_state" to mastery.id,
            ),
        )
    }

    @Test
    fun `readiness is read from the axes their engines wrote`() {
        val store = GraphStore().apply {
            publish(pointer, linkedList)
            hard(pointer, linkedList)
            mastery(pointer, MasteryAxisState.CONFIRMED_CURRENT, retention = "review_due")
        }
        val decision = ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList))
        assertEquals(PrerequisiteEligibility.ELIGIBLE, decision.eligibility)
        assertEquals(listOf(pointer), decision.reviewDueSkills)

        store.mastery(pointer, MasteryAxisState.CONFIRMED_CURRENT, weakness = "remediation_required")
        assertEquals(PrerequisiteEligibility.BLOCKED,
            ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList)).eligibility)
    }

    @Test
    fun `a Skill with no mastery projection is not ready and says mastery was not evaluated`() {
        val store = GraphStore().apply {
            publish(pointer, linkedList)
            hard(pointer, linkedList)
        }
        val readiness = ResolvePrerequisites(store).readinessOf(pointer)
        assertEquals(PrerequisiteReadiness.NOT_READY, readiness.readiness)
        assertTrue(ReadinessNote.MASTERY_NOT_YET_EVALUATED in readiness.notes)
        assertEquals(PrerequisiteEligibility.BLOCKED,
            ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList)).eligibility)
    }

    @Test
    fun `a critical prerequisite is read from the published Skill`() {
        val store = GraphStore().apply {
            publish(pointer, linkedList, critical = setOf(pointer))
            hard(pointer, linkedList)
            mastery(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        }
        assertEquals(PrerequisiteEligibility.BLOCKED,
            ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList)).eligibility)

        store.publish(pointer)
        assertEquals(PrerequisiteEligibility.CONDITIONAL_ELIGIBLE,
            ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList)).eligibility)
    }

    @Test
    fun `a target on a cycle is invalid metadata`() {
        val store = GraphStore().apply {
            publish(pointer, linkedList, memoryAddress)
            hard(pointer, linkedList)
            hard(memoryAddress, pointer)
            hard(linkedList, memoryAddress)
            listOf(pointer, memoryAddress).forEach { mastery(it, MasteryAxisState.CONFIRMED_CURRENT) }
        }
        val decision = ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList))
        assertEquals(PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA, decision.eligibility)
        assertEquals(listOf("prerequisite_cycle:$linkedList"), decision.metadataProblems)
    }

    @Test
    fun `an unpublished required Skill is invalid metadata`() {
        val store = GraphStore().apply { publish(linkedList) }
        val decision = ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList, listOf(malloc)))
        assertEquals(PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA, decision.eligibility)
    }

    @Test
    fun `the decision names the snapshot it was made from`() {
        val store = GraphStore().apply {
            publish(pointer, linkedList)
            hard(pointer, linkedList)
            mastery(pointer, MasteryAxisState.CONFIRMED_CURRENT, watermark = 40)
        }
        val decision = ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList))
        assertEquals(listOf("skill_state:${pointer.logicalId}@v1#watermark=40"), decision.readinessSnapshotRefs)
    }

    @Test
    fun `resolving writes nothing at all`() {
        val store = GraphStore().apply {
            publish(pointer, linkedList)
            hard(pointer, linkedList)
            mastery(pointer, MasteryAxisState.CONFIRMED_CURRENT)
        }
        ResolvePrerequisites(store).resolve(PrerequisiteCandidate("c1", linkedList))
        assertTrue(store.written.isEmpty())
    }

    @Test
    fun `it writes only prerequisite_readiness, with the provenance of what it read`() {
        val store = GraphStore().apply {
            publish(pointer)
            mastery(pointer, MasteryAxisState.CONFIRMED_CURRENT, retention = "review_due", watermark = 40)
        }
        val rebuilt = RebuildReadiness(store, clock).rebuild(pointer)
        assertTrue(rebuilt.written)
        val row = store.written.single()
        assertEquals("prerequisite_readiness:${pointer.logicalId}@v1", row.key)
        assertEquals(mapOf("state" to "ready_due"), row.payload)
        assertEquals("PRG-v0", row.policyVersion)
        // It saw what the mastery row had seen — not the store's newer watermark.
        assertEquals(40, row.truthWatermark)
        assertEquals(1, row.inputCurriculumVersion)
        assertEquals(clock.now().instantEpochMillis, row.builtAtInstant)
        assertFalse(store.written.any { it.key.startsWith("skill_state:") })
    }

    @Test
    fun `with no mastery row the readiness row says it saw no truth`() {
        val store = GraphStore().apply { publish(pointer) }
        RebuildReadiness(store, clock).rebuild(pointer)
        val row = store.written.single()
        assertEquals("not_ready", row.payload.getValue("state"))
        assertEquals(0, row.truthWatermark)
    }

    @Test
    fun `with nothing published nothing is written`() {
        val store = GraphStore().apply {
            curriculumVersion = null
            mastery(pointer, MasteryAxisState.CONFIRMED_CURRENT)
        }
        val rebuilt = RebuildReadiness(store, clock).rebuild(pointer)
        assertFalse(rebuilt.written)
        assertTrue(store.written.isEmpty())
    }

    @Test
    fun `rebuilding twice from the same state writes the same row`() {
        val store = GraphStore().apply {
            publish(pointer)
            mastery(pointer, MasteryAxisState.DEVELOPING_INDEPENDENT)
        }
        RebuildReadiness(store, clock).rebuild(pointer)
        RebuildReadiness(store, clock).rebuild(pointer)
        assertEquals(store.written[0], store.written[1])
    }
}
