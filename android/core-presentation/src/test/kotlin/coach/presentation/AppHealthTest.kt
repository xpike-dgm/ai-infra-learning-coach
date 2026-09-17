package coach.presentation

import coach.model.EvaluatorAvailability
import coach.model.RecoveryReason
import coach.model.StoreStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppHealthTest {

    private val allStatuses: List<StoreStatus> =
        listOf(StoreStatus.Opening, StoreStatus.Ready, StoreStatus.RecoverableFailure) +
            RecoveryReason.entries.map { StoreStatus.RecoveryRequired(it) }

    @Test
    fun `the cross cutting states are UXIA-v0's six in order`() {
        assertEquals(
            listOf("loading", "empty_valid", "error_recoverable", "offline_local_available", "ai_unavailable_core_available", "data_recovery_required"),
            CrossCuttingState.entries.map { it.id },
        )
    }

    @Test
    fun `only the two system conditions wear the fault tone`() {
        val fault = CrossCuttingState.entries.filter { it.tone == Tone.SYSTEM_FAULT }.map { it.id }.toSet()
        assertEquals(SystemFaultState.entries.map { it.id }.toSet(), fault)
    }

    @Test
    fun `every recovery reason blocks normal use and keeps its reason`() {
        RecoveryReason.entries.forEach { reason ->
            EvaluatorAvailability.entries.forEach { evaluator ->
                val health = AppHealth.of(StoreStatus.RecoveryRequired(reason), evaluator)
                assertEquals(CrossCuttingState.DATA_RECOVERY_REQUIRED, health.blocking)
                assertEquals(reason, health.recoveryReason)
                assertFalse(health.showsShell, "the shell was drawn over a store that needs recovery")
            }
        }
    }

    @Test
    fun `a recoverable failure blocks and offers a safe retry`() {
        val health = AppHealth.of(StoreStatus.RecoverableFailure, EvaluatorAvailability.AVAILABLE)
        assertEquals(CrossCuttingState.ERROR_RECOVERABLE, health.blocking)
        assertEquals(setOf(HealthAction.RECHECK), health.actions)
        assertNull(health.recoveryReason)
    }

    @Test
    fun `loading blocks without offering anything to press`() {
        val health = AppHealth.of(StoreStatus.Opening, EvaluatorAvailability.UNAVAILABLE)
        assertEquals(CrossCuttingState.LOADING, health.blocking)
        assertTrue(health.actions.isEmpty())
        assertTrue(health.contexts.isEmpty(), "a working core was claimed before the store was open")
    }

    @Test
    fun `AI being unavailable never blocks and is only claimed alongside a working core`() {
        val ready = AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.UNAVAILABLE)
        assertTrue(ready.normalUseAvailable, "AI absence made the app unusable (V1 criterion 8)")
        assertEquals(listOf(CrossCuttingState.AI_UNAVAILABLE_CORE_AVAILABLE), ready.contexts)

        allStatuses.filter { it != StoreStatus.Ready }.forEach { status ->
            assertFalse(
                CrossCuttingState.AI_UNAVAILABLE_CORE_AVAILABLE in AppHealth.of(status, EvaluatorAvailability.UNAVAILABLE).contexts,
                "core_available was claimed while the store is $status",
            )
        }
        assertTrue(AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE).contexts.isEmpty())
    }

    @Test
    fun `the shell shows exactly when normal use is available`() {
        allStatuses.forEach { status ->
            EvaluatorAvailability.entries.forEach { evaluator ->
                val health = AppHealth.of(status, evaluator)
                assertEquals(status == StoreStatus.Ready, health.showsShell, "shell for $status / $evaluator")
            }
        }
    }

    @Test
    fun `no health action can reset delete or recreate the learner's data`() {
        val destructive = Regex("reset|wipe|delete|clear|recreate|fresh|erase", RegexOption.IGNORE_CASE)
        assertEquals(emptyList(), HealthAction.entries.map { it.name }.filter { destructive.containsMatchIn(it) })
    }
}
