package coach.curriculum

import coach.model.VersionedRef
import coach.ports.ContentDocument
import coach.ports.ContentPort

/**
 * Implements ContentPort. Curriculum is versioned separately from user state and a published
 * version is never overwritten (LFPS-v0), so lookups are always by logical id and version.
 *
 * **Nothing is published yet, and this says so instead of throwing.** `ContentPort` already has a
 * word for "no such resource" — `null` — and returning it is the truthful answer while no
 * curriculum has been ingested. It used to be `TODO()`, which would have crashed the first caller;
 * that is the same defect 10E found in the AI adapter, and a screen asking for content it does not
 * have is a missing resource, not a broken app.
 *
 * Curriculum ingestion itself is not 11A's: the first authored content arrives at 15, and the step
 * that first needs it loads it (11D).
 */
class FileContentSource : ContentPort {
    override fun resource(ref: VersionedRef): ContentDocument? = null
}
