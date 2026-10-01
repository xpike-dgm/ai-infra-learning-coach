package coach.application

import coach.model.CurriculumPackage
import coach.model.DayRecordKind
import coach.model.ObjectiveEvidenceProfile
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.StudyTimestamp
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.EvidenceRow
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import coach.model.SkillRow
import coach.model.PrerequisiteEdge
import coach.model.StoredPlan
import coach.ports.StoredTruth

/** Reading one day's inventory: it counts, and does nothing else (11E). */
class DayCloseFactsTest {

    private class CountingStore(
        private val counts: Map<Pair<String, String>, Int>,
        private val unreadable: Set<String> = emptySet(),
    ) : PersistencePort {
        val reads = mutableListOf<Pair<String, String>>()
        var writes = 0

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long {
            writes += 1
            return 1
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) {
            writes += 1
        }
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome =
            error("the day summary publishes nothing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int {
            reads += kind to studyDay
            if (kind in unreadable) error("$kind cannot be counted")
            return counts[kind to studyDay] ?: 0
        }
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = emptyList()
        override fun truthWatermark(): Long = 0
        override fun latestCurriculumVersion(): Int? = null
        override fun skill(ref: VersionedRef): SkillRow? = null
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = emptyList()
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSession(scope: coach.model.AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<coach.model.ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()

        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-21", 3 * 3600)
    }

    @Test
    fun `the day is the clock's study day, and every kind is counted against it`() {
        val store = CountingStore(mapOf("attempt" to "2026-09-21" to 2, "exposure_record" to "2026-09-21" to 3))
        val loaded = DayCloseFacts(store, clock).load()

        assertEquals("2026-09-21", loaded.record.studyDay)
        assertEquals(2, loaded.record.inventory.countOf(DayRecordKind.ATTEMPTS_RECORDED))
        assertEquals(3, loaded.record.inventory.countOf(DayRecordKind.ITEMS_SEEN))
        assertEquals(0, loaded.record.inventory.countOf(DayRecordKind.EVIDENCE_INTERPRETED))
        assertEquals(DayRecordKind.entries.map { it.truthTable }, store.reads.map { it.first })
        assertTrue(store.reads.all { it.second == "2026-09-21" })
    }

    @Test
    fun `yesterday is read as yesterday, never as today`() {
        val store = CountingStore(mapOf("attempt" to "2026-09-20" to 7))
        val loaded = DayCloseFacts(store, clock).load(studyDay = "2026-09-20")
        assertEquals("2026-09-20", loaded.record.studyDay)
        assertEquals(7, loaded.record.inventory.countOf(DayRecordKind.ATTEMPTS_RECORDED))
    }

    @Test
    fun `a kind that cannot be read is named unread, not counted as zero`() {
        val store = CountingStore(mapOf("attempt" to "2026-09-21" to 1), unreadable = setOf("evidence_event"))
        val loaded = DayCloseFacts(store, clock).load()
        assertEquals(setOf("evidence_interpreted"), loaded.unreadKinds)
        assertTrue(DayRecordKind.EVIDENCE_INTERPRETED !in loaded.record.inventory.counts)
    }

    @Test
    fun `reading a day writes nothing and claims no change`() {
        val store = CountingStore(mapOf("attempt" to "2026-09-21" to 4))
        val loaded = DayCloseFacts(store, clock).load()
        assertEquals(0, store.writes)
        assertTrue(loaded.record.changes.isEmpty(), "the day summary invented a change")
        assertEquals(null, loaded.record.openNeedCount)
    }
}
