package coach.curriculum

import coach.model.VersionedRef
import coach.ports.ContentDocument
import coach.ports.ContentPort

/**
 * Implements ContentPort. Curriculum is versioned separately from user state and a published
 * version is never overwritten (LFPS-v0), so lookups are always by logical id and version.
 * Content loading itself is 10D/11.
 */
class FileContentSource : ContentPort {
    override fun resource(ref: VersionedRef): ContentDocument? =
        TODO("Curriculum loading is 10D; 10A fixes the port implementation boundary.")
}
