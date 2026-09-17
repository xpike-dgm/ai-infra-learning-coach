package coach.model

/**
 * Where the local store is on its way to being usable (`LFPS-v0` §12, `UXIA-v0` §14).
 *
 * This is a core type on purpose. The storage adapter knows *why* a database cannot be opened;
 * core-presentation decides *what the learner is told*; neither may need the other's types, and a
 * crash is not one of the outcomes.
 */
sealed interface StoreStatus {

    /** The store is being opened, checked and migrated off the main thread. */
    data object Opening : StoreStatus

    /** Integrity checked, schema current, safe for normal use. */
    data object Ready : StoreStatus

    /**
     * Continuing could put the learner's evidence at risk. Nothing has been reset, deleted or
     * recreated, and nothing will be without an explicit, verified restore.
     */
    data class RecoveryRequired(val reason: RecoveryReason) : StoreStatus

    /**
     * An operational failure happened before any data could be read, so integrity is not in
     * question and a safe retry is meaningful.
     */
    data object RecoverableFailure : StoreStatus
}

/** Why a store needs recovery. Every value is a storage-layer condition, never a learner judgement. */
enum class RecoveryReason {
    /** The integrity or foreign-key check failed, or the file is not a readable database. */
    INTEGRITY_CHECK_FAILED,

    /** Written by a newer build. Downgrade is not supported; reverting is a restore (`LFPS-v0` §10). */
    NEWER_SCHEMA,

    /** A forward migration could not complete; the previous state was left intact. */
    MIGRATION_INCOMPLETE,
}

/**
 * Whether open-ended evaluation can currently produce an answer. `UNAVAILABLE` is a capability
 * fact, not a fault: the deterministic core is unaffected (`MSBX-v0` §ai_absence).
 */
enum class EvaluatorAvailability {
    AVAILABLE,
    UNAVAILABLE,
}
