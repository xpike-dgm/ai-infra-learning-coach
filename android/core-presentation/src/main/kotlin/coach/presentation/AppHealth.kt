package coach.presentation

import coach.model.EvaluatorAvailability
import coach.model.RecoveryReason
import coach.model.StoreStatus

/**
 * `UXIA-v0` §14's six cross-cutting surface states, in its order, each with its `VDSX-v0` tone.
 *
 * Only the two system conditions wear the fault tone. Being without AI, or offline, is a
 * capability fact while the local core works, so those stay neutral (`VDSX-v0`).
 */
enum class CrossCuttingState(val id: String, val tone: Tone) {
    LOADING("loading", Tone.NEUTRAL),
    EMPTY_VALID("empty_valid", Tone.NEUTRAL),
    ERROR_RECOVERABLE("error_recoverable", SystemFaultState.ERROR_RECOVERABLE.tone),
    OFFLINE_LOCAL_AVAILABLE("offline_local_available", Tone.NEUTRAL),
    AI_UNAVAILABLE_CORE_AVAILABLE("ai_unavailable_core_available", Tone.NEUTRAL),
    DATA_RECOVERY_REQUIRED("data_recovery_required", SystemFaultState.DATA_RECOVERY_REQUIRED.tone),
}

/**
 * What the learner can do from a health state.
 *
 * There is exactly one action, and it reads. **No reset, wipe, delete or recreate action exists**,
 * so a screen that offers to "start fresh" over uncertain data cannot be written — `LFPS-v0`
 * forbids silent progress reset, and an unsilent button that does the same thing is no better.
 * Restore is the recovery action `LFPS-v0` names; its controls are 16D's.
 */
enum class HealthAction {
    /** Re-run the same non-mutating open: read, integrity check, migrate if needed. */
    RECHECK,
}

/**
 * The app's health as the shell renders it.
 *
 * [blocking] is set when normal use is not safe or not yet possible; the shell is then suspended,
 * because destinations drawn over a store that is not open would show content that is not true.
 * [contexts] are non-blocking and are only ever reported **alongside** a working core.
 */
data class AppHealth(
    val blocking: CrossCuttingState?,
    val recoveryReason: RecoveryReason?,
    val contexts: List<CrossCuttingState>,
    val actions: Set<HealthAction>,
) {
    val normalUseAvailable: Boolean get() = blocking == null
    val showsShell: Boolean get() = normalUseAvailable

    companion object {
        /**
         * The whole rule. Precedence follows `THUX-v0`: data recovery supersedes everything,
         * a recoverable error comes next, loading after that.
         *
         * `ai_unavailable_core_available` is named for a working core, so it is only produced
         * when the core actually works; it never supersedes normal use (`THUX-v0` precedence).
         */
        fun of(store: StoreStatus, evaluator: EvaluatorAvailability): AppHealth = when (store) {
            is StoreStatus.RecoveryRequired -> AppHealth(
                blocking = CrossCuttingState.DATA_RECOVERY_REQUIRED,
                recoveryReason = store.reason,
                contexts = emptyList(),
                actions = setOf(HealthAction.RECHECK),
            )
            StoreStatus.RecoverableFailure -> AppHealth(
                blocking = CrossCuttingState.ERROR_RECOVERABLE,
                recoveryReason = null,
                contexts = emptyList(),
                actions = setOf(HealthAction.RECHECK),
            )
            StoreStatus.Opening -> AppHealth(
                blocking = CrossCuttingState.LOADING,
                recoveryReason = null,
                contexts = emptyList(),
                actions = emptySet(),
            )
            StoreStatus.Ready -> AppHealth(
                blocking = null,
                recoveryReason = null,
                contexts = when (evaluator) {
                    EvaluatorAvailability.AVAILABLE -> emptyList()
                    EvaluatorAvailability.UNAVAILABLE -> listOf(CrossCuttingState.AI_UNAVAILABLE_CORE_AVAILABLE)
                },
                actions = emptySet(),
            )
        }
    }
}
