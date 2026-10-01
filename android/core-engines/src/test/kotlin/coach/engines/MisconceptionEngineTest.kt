package coach.engines

import coach.model.AttributionOutcome
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.IndependenceClass
import coach.model.MisconceptionRow
import coach.model.MisconceptionSource
import coach.model.MisconceptionTag
import coach.model.VersionedRef
import coach.model.WeaknessEvent
import coach.model.WeaknessSignal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Misconception memory (14B, `WAAX-v0`): a label moves only as far as the weakness engine's own rule moves its
 * Objective, never further than its source allows, and only if the catalog declares it.
 */
class MisconceptionEngineTest {

    private val skill = VersionedRef("skill.c.pointers", 1)
    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val other = VersionedRef("objective.c.pointers.address_of", 1)
    private val addressValue = MisconceptionRow(VersionedRef("misconception.c.pointers.address_value", 1), objective,
        "adres ile değer karışıklığı", "Adres ile değeri karıştırmış olabilir misin?")
    private val starAmp = MisconceptionRow(VersionedRef("misconception.c.pointers.star_amp_roles", 1), objective,
        "* ile & rolleri", "* ile & işaretlerinin rolünü karıştırmış olabilir misin?")
    private val elsewhere = MisconceptionRow(VersionedRef("misconception.c.pointers.address_of_lvalue", 1), other,
        "& yalnız lvalue üzerinde", "& işaretinin neye uygulanabildiğini karıştırmış olabilir misin?")
    private val catalog = listOf(addressValue, starAmp, elsewhere)

    private var next = 1L

    private fun event(
        outcome: EvidenceOutcome = EvidenceOutcome.NEGATIVE,
        status: EvaluatorStatus = EvaluatorStatus.VERIFIED,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        contested: Boolean = false,
        prerequisiteValid: Boolean = true,
        masteredBefore: Boolean = false,
        masteredAfter: Boolean = false,
        baseline: Boolean = false,
        family: String? = null,
        tags: List<MisconceptionTag> = emptyList(),
    ): WeaknessEvent {
        val id = next++
        return WeaknessEvent(
            evidenceId = id, sequence = id, studyDay = "2026-10-0${(id % 9) + 1}", outcome = outcome, evaluatorStatus = status,
            independence = independence, contested = contested, prerequisiteValid = prerequisiteValid, solutionExposed = false,
            direct = true, resource = VersionedRef("item.c.pointers.$id", 1), variantFamilyId = family ?: "family.$id",
            masteredBefore = masteredBefore, masteredAfter = masteredAfter, diagnosticBaseline = baseline, misconceptionTags = tags,
        )
    }

    private fun det(row: MisconceptionRow) = MisconceptionTag(row.ref, MisconceptionSource.DETERMINISTIC)
    private fun ai(row: MisconceptionRow) = MisconceptionTag(row.ref, MisconceptionSource.AI_PROPOSED)

    private fun signalOf(events: List<WeaknessEvent>, row: MisconceptionRow = addressValue) =
        MisconceptionEngine.replay(objective, skill, catalog, events).single { it.misconception == row.ref }.signal

    @Test
    fun `an AI-proposed label stays a hypothesis however clean the row`() {
        assertEquals(WeaknessSignal.HYPOTHESIS, signalOf(listOf(event(tags = listOf(ai(addressValue))))))
        assertEquals(WeaknessSignal.HYPOTHESIS, signalOf(listOf(
            event(masteredBefore = true, masteredAfter = true, tags = listOf(ai(addressValue))),
            event(masteredBefore = true, masteredAfter = false, tags = listOf(ai(addressValue))),
        )))
    }

    @Test
    fun `a deterministic label on a clean failure before mastery is supported`() {
        val state = MisconceptionEngine.replay(objective, skill, catalog, listOf(event(tags = listOf(det(addressValue)))))
            .single { it.misconception == addressValue.ref }
        assertEquals(WeaknessSignal.SUPPORTED, state.signal)
        assertEquals(MisconceptionSource.DETERMINISTIC, state.source)
        assertEquals(listOf(1L), state.signalEvidenceIds)
    }

    @Test
    fun `a deterministic label is confirmed only when a fresh recheck fails and the gates no longer pass`() {
        assertEquals(WeaknessSignal.SUPPORTED, signalOf(listOf(event(masteredBefore = true, masteredAfter = true, tags = listOf(det(addressValue))))))
        assertEquals(WeaknessSignal.CONFIRMED, signalOf(listOf(
            event(masteredBefore = true, masteredAfter = true, tags = listOf(det(addressValue))),
            event(masteredBefore = true, masteredAfter = false, tags = listOf(det(addressValue))),
        )))
        // A fresh recheck that fails while the gates still pass keeps verification open: supported, not confirmed.
        assertEquals(WeaknessSignal.SUPPORTED, signalOf(listOf(
            event(masteredBefore = true, masteredAfter = true, tags = listOf(det(addressValue))),
            event(masteredBefore = true, masteredAfter = true, tags = listOf(det(addressValue))),
        )))
    }

    @Test
    fun `assisted, provisional or partial work makes a label a hypothesis at most`() {
        assertEquals(WeaknessSignal.HYPOTHESIS, signalOf(listOf(event(independence = IndependenceClass.ASSISTED, tags = listOf(det(addressValue))))))
        assertEquals(WeaknessSignal.HYPOTHESIS, signalOf(listOf(event(status = EvaluatorStatus.PROVISIONAL, tags = listOf(det(addressValue))))))
        assertEquals(WeaknessSignal.HYPOTHESIS, signalOf(listOf(event(outcome = EvidenceOutcome.PARTIAL, tags = listOf(det(addressValue))))))
    }

    @Test
    fun `invalid, contested or prerequisite-contaminated work moves no label`() {
        assertEquals(WeaknessSignal.NONE, signalOf(listOf(event(outcome = EvidenceOutcome.INVALID, tags = listOf(det(addressValue))))))
        assertEquals(WeaknessSignal.NONE, signalOf(listOf(event(contested = true, tags = listOf(det(addressValue))))))
        assertEquals(WeaknessSignal.NONE, signalOf(listOf(event(prerequisiteValid = false, tags = listOf(det(addressValue))))))
        assertEquals(WeaknessSignal.NONE, signalOf(listOf(event(baseline = true, tags = listOf(det(addressValue))))))
    }

    @Test
    fun `a label the catalog does not declare for the Objective names nothing`() {
        val undeclared = MisconceptionTag(VersionedRef("misconception.c.pointers.invented", 1), MisconceptionSource.DETERMINISTIC)
        val states = MisconceptionEngine.replay(objective, skill, catalog, listOf(event(tags = listOf(undeclared, det(elsewhere)))))
        assertEquals(listOf(addressValue.ref, starAmp.ref), states.map { it.misconception })
        assertEquals(setOf(WeaknessSignal.NONE), states.map { it.signal }.toSet())
    }

    @Test
    fun `a fresh clean success resolves an open label, a confirmed one only when the gates pass again`() {
        val supported = listOf(event(family = "family.a", tags = listOf(det(addressValue))))
        assertEquals(WeaknessSignal.RESOLVED, signalOf(supported + event(outcome = EvidenceOutcome.POSITIVE, family = "family.b")))
        val sameFamily = listOf(event(family = "family.c", tags = listOf(det(addressValue))))
        assertEquals(WeaknessSignal.SUPPORTED, signalOf(sameFamily + event(outcome = EvidenceOutcome.POSITIVE, family = "family.c")),
            "the same family is not a fresh check")
        val confirmed = listOf(
            event(masteredBefore = true, masteredAfter = true, family = "family.d", tags = listOf(det(addressValue))),
            event(masteredBefore = true, masteredAfter = false, family = "family.e", tags = listOf(det(addressValue))),
        )
        assertEquals(WeaknessSignal.CONFIRMED, signalOf(confirmed + event(outcome = EvidenceOutcome.POSITIVE, family = "family.f", masteredAfter = false)))
        assertEquals(WeaknessSignal.RESOLVED, signalOf(confirmed + event(outcome = EvidenceOutcome.POSITIVE, family = "family.g", masteredAfter = true)))
    }

    @Test
    fun `nothing lowers a label, and a new failure after it was resolved starts a new window`() {
        val events = listOf(
            event(family = "family.a", tags = listOf(det(addressValue))),
            event(independence = IndependenceClass.ASSISTED, tags = listOf(ai(addressValue))),
        )
        val held = MisconceptionEngine.replay(objective, skill, catalog, events).single { it.misconception == addressValue.ref }
        assertEquals(WeaknessSignal.SUPPORTED, held.signal)
        assertEquals(MisconceptionSource.DETERMINISTIC, held.source)
        assertEquals(listOf(1L, 2L), held.signalEvidenceIds)

        val again = events + event(outcome = EvidenceOutcome.POSITIVE, family = "family.z") + event(independence = IndependenceClass.ASSISTED, tags = listOf(ai(addressValue)))
        val restarted = MisconceptionEngine.replay(objective, skill, catalog, again).single { it.misconception == addressValue.ref }
        assertEquals(WeaknessSignal.HYPOTHESIS, restarted.signal)
        assertEquals(MisconceptionSource.AI_PROPOSED, restarted.source)
        assertEquals(listOf(4L), restarted.signalEvidenceIds)
        assertNull(restarted.resolutionEvidenceId)
    }

    @Test
    fun `findings name each row's attribution and its labels' state after it`() {
        val events = listOf(
            event(prerequisiteValid = false, tags = listOf(det(addressValue))),
            event(tags = listOf(det(addressValue), ai(starAmp))),
            event(family = "family.1", tags = listOf(det(addressValue))),
            // A success on a family that already showed the signal is not a fresh check: no rule speaks for it.
            event(outcome = EvidenceOutcome.POSITIVE, family = "family.1"),
        )
        val findings = MisconceptionEngine.findings(objective, skill, catalog, events)
        assertEquals(listOf(AttributionOutcome.PREREQUISITE_SIGNAL, AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED, AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED, null),
            findings.map { it.outcome })
        assertEquals(listOf(addressValue to WeaknessSignal.NONE), findings[0].misconceptions)
        assertEquals(listOf(addressValue to WeaknessSignal.SUPPORTED, starAmp to WeaknessSignal.HYPOTHESIS), findings[1].misconceptions)
    }
}
