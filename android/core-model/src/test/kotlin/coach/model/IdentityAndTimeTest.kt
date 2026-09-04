package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** DDM-v0 guarantees expressed as checks rather than as comments (TVSX-v0 §14). */
class IdentityAndTimeTest {

    @Test
    fun `a reference always carries a version`() {
        assertEquals("skill.os.paging@v3", VersionedRef("skill.os.paging", 3).toString())
        assertFailsWith<IllegalArgumentException> { VersionedRef("skill.os.paging", 0) }
    }

    @Test
    fun `a timestamp carries instant study day and offset`() {
        val stamp = StudyTimestamp(
            instantEpochMillis = 1_756_600_000_000,
            studyDay = "2026-08-31",
            utcOffsetSeconds = 3 * 3600,
        )
        assertEquals("2026-08-31", stamp.studyDay)
        assertEquals(3 * 3600, stamp.utcOffsetSeconds)
    }

    @Test
    fun `a study day must be a real local date`() {
        assertFailsWith<IllegalArgumentException> {
            StudyTimestamp(0, "31-08-2026", 0)
        }
    }
}
