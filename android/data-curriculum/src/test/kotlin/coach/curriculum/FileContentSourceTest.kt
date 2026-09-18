package coach.curriculum

import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertNull

/** Asking for content that has not been published is a missing resource, never a crash (11A). */
class FileContentSourceTest {

    @Test
    fun `an unpublished resource is absent rather than an exception`() {
        assertNull(FileContentSource().resource(VersionedRef("item.python.loops.q1", 1)))
    }
}
