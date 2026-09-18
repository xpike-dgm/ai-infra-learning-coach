package coach.application

import coach.model.StudyTimestamp
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 11A: the read path reports what the store holds and never fills a gap with a plausible default. */
class TodayFactsQueryTest {

    private class RecordingStore(private val published: Boolean) : PersistencePort {
        val writes = mutableListOf<String>()
        override fun <T> inTransaction(block: () -> T): T {
            writes += "transaction"
            return block()
        }
        override fun appendTruth(record: TruthRecord) { writes += "appendTruth" }
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) { writes += "writeProjection" }
        override fun curriculumPublished(): Boolean = published
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
        val facts = TodayFactsQuery(RecordingStore(published = true), clock).load()
        assertNull(facts.plan, "a plan appeared before the planner that writes it (12)")
        assertNull(facts.capacity, "a daily capacity appeared before the learner chose one (16D)")
    }

    @Test
    fun `reading Today writes nothing`() {
        val store = RecordingStore(published = true)
        TodayFactsQuery(store, clock).load()
        assertEquals(emptyList(), store.writes, "the Today read path must not write")
    }
}
