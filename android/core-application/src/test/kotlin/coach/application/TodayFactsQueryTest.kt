package coach.application

import coach.model.CurriculumPackage
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
import kotlin.test.assertNull
import kotlin.test.assertTrue
import coach.model.SkillRow
import coach.model.PrerequisiteEdge
import coach.model.StoredPlan
import coach.ports.StoredTruth

/** 11A: the read path reports what the store holds and never fills a gap with a plausible default. */
class TodayFactsQueryTest {

    private class RecordingStore(private val published: Boolean) : PersistencePort {
        val writes = mutableListOf<String>()
        override fun <T> inTransaction(block: () -> T): T {
            writes += "transaction"
            return block()
        }
        override fun appendTruth(record: TruthRecord): Long { writes += "appendTruth"; return 0 }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) { writes += "writeProjection" }
        override fun curriculumPublished(): Boolean = published
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome =
            error("this use case publishes no curriculum")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
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
        override fun now() =
            StudyTimestamp(instantEpochMillis = 1_789_000_000_000, studyDay = "2026-09-17", utcOffsetSeconds = 3 * 3600)
    }

    @Test
    fun `the study day comes from the clock port, never from the system clock`() {
        val facts = TodayFactsQuery(RecordingStore(published = true), clock).load()
        assertEquals("2026-09-17", facts.studyDay)
    }

    @Test
    fun `an unpublished curriculum and a published one are reported apart`() {
        assertTrue(TodayFactsQuery(RecordingStore(published = true), clock).load().curriculumLoaded)
        assertEquals(false, TodayFactsQuery(RecordingStore(published = false), clock).load().curriculumLoaded)
    }

    @Test
    fun `no plan and no capacity are invented while the planner and settings do not exist`() {
        // 12E: a plan and its capacity now come only from what the planner stored. With nothing stored,
        // nothing appears — no plausible default, no capacity the learner never chose (16D).
        val facts = TodayFactsQuery(RecordingStore(published = true), clock).load()
        assertNull(facts.plan, "a plan appeared that no planner stored")
        assertNull(facts.capacity, "a daily capacity appeared that no stored plan recorded")
    }

    @Test
    fun `reading Today writes nothing`() {
        val store = RecordingStore(published = true)
        TodayFactsQuery(store, clock).load()
        assertEquals(emptyList(), store.writes, "the Today read path must not write")
    }
}
