package coach.engines

import coach.model.AttributionOutcome
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceSeverity
import coach.model.FailureRule
import coach.model.IndependenceClass
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.ObjectiveWeakness
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.VersionedRef
import coach.model.WeaknessEvent
import coach.model.WeaknessSignal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WeaknessEngineTest {

    private val skill = VersionedRef("skill.c.pointers", 1)
    private val objective = VersionedRef("objective.c.pointers.trace", 1)
    private var seq = 0L

    private fun event(
        outcome: EvidenceOutcome = EvidenceOutcome.NEGATIVE,
        before: Boolean = false,
        after: Boolean = before,
        status: EvaluatorStatus = EvaluatorStatus.VERIFIED,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        direct: Boolean = true,
        prerequisiteValid: Boolean = true,
        solutionExposed: Boolean = false,
        contested: Boolean = false,
        item: String? = null,
        family: String? = null,
        day: String = "2026-10-01",
    ): WeaknessEvent {
        seq += 1
        return WeaknessEvent(seq, seq, day, outcome, status, independence, contested, prerequisiteValid, solutionExposed, direct,
            VersionedRef(item ?: "item.$seq", 1), family ?: "fam.$seq", before, after)
    }

    private fun replay(vararg events: WeaknessEvent) = WeaknessEngine.replay(objective, skill, events.toList())

    @Test
    fun `an attempt nobody can attribute writes nothing against its target`() {
        listOf(
            event(EvidenceOutcome.INVALID),
            event(status = EvaluatorStatus.INVALID),
            event(contested = true),
        ).forEach { e ->
            val w = replay(e)
            assertEquals(WeaknessSignal.NONE, w.signal)
            assertEquals(AttributionOutcome.NOT_ATTRIBUTABLE, w.lastOutcome)
            assertEquals(FailureRule.INVALID_OR_AMBIGUOUS, w.lastRule)
        }
        val contaminated = replay(event(prerequisiteValid = false))
        assertEquals(WeaknessSignal.NONE, contaminated.signal)
        assertEquals(AttributionOutcome.PREREQUISITE_SIGNAL, contaminated.lastOutcome)
    }

    @Test
    fun `assisted, provisional, partial or indirect failure is a hypothesis at most`() {
        listOf(
            event(independence = IndependenceClass.ASSISTED) to FailureRule.ASSISTED_H1_H4,
            event(solutionExposed = true) to FailureRule.ASSISTED_H1_H4,
            event(status = EvaluatorStatus.PROVISIONAL) to FailureRule.PROVISIONAL_OR_PARTIAL,
            event(EvidenceOutcome.PARTIAL) to FailureRule.PROVISIONAL_OR_PARTIAL,
            event(direct = false) to FailureRule.PROVISIONAL_OR_PARTIAL,
        ).forEach { (e, rule) ->
            val w = replay(e)
            assertEquals(WeaknessSignal.HYPOTHESIS, w.signal, e.toString())
            assertEquals(rule, w.lastRule)
        }
        // Many hypotheses are still a hypothesis: help taken never confirms a remediation.
        val many = replay(event(independence = IndependenceClass.ASSISTED), event(independence = IndependenceClass.ASSISTED),
            event(status = EvaluatorStatus.PROVISIONAL))
        assertEquals(WeaknessSignal.HYPOTHESIS, many.signal)
        assertEquals(3, many.signalEvidenceIds.size)
    }

    @Test
    fun `a clean failure before mastery is a supported weakness, not a remediation`() {
        val w = replay(event(independence = IndependenceClass.ASSISTED), event())
        assertEquals(WeaknessSignal.SUPPORTED, w.signal)
        assertEquals(AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED, w.lastOutcome)
        assertFalse(w.verificationOpen)
        // A weaker kind of evidence afterwards never lowers it.
        assertEquals(WeaknessSignal.SUPPORTED, replay(event(), event(independence = IndependenceClass.ASSISTED)).signal)
    }

    @Test
    fun `the first clean contradiction after mastery opens verification and erases nothing`() {
        val w = replay(event(before = true, after = true))
        assertEquals(WeaknessSignal.SUPPORTED, w.signal)
        assertEquals(AttributionOutcome.VERIFICATION_DUE, w.lastOutcome)
        assertTrue(w.verificationOpen)
    }

    @Test
    fun `remediation is confirmed only when the mastery engine's gates no longer pass`() {
        // A fresh recheck fails but mastery still holds: verification stays open, nothing is confirmed.
        val holds = replay(event(before = true, after = true), event(before = true, after = true))
        assertEquals(WeaknessSignal.SUPPORTED, holds.signal)
        assertTrue(holds.verificationOpen)
        // The gates fail: confirmed, whatever item showed it.
        val lost = replay(event(before = true, after = true, item = "item.x", family = "fam.x"),
            event(before = true, after = false, item = "item.x", family = "fam.x"))
        assertEquals(WeaknessSignal.CONFIRMED, lost.signal)
        assertEquals(AttributionOutcome.REMEDIATION_REQUIRED, lost.lastOutcome)
        assertFalse(lost.verificationOpen)
        // The same item failing again while mastery holds is not a fresh recheck.
        val same = replay(event(before = true, after = true, item = "item.x", family = "fam.x"),
            event(before = true, after = true, item = "item.x", family = "fam.x"))
        assertEquals(1, same.signalEvidenceIds.size)
    }

    @Test
    fun `a fresh clean success closes a hypothesis or a supported weakness, and the same item does not`() {
        val hypothesis = replay(event(independence = IndependenceClass.ASSISTED, item = "item.a", family = "fam.a"),
            event(EvidenceOutcome.POSITIVE))
        assertEquals(WeaknessSignal.RESOLVED, hypothesis.signal)
        assertEquals(AttributionOutcome.POSITIVE_RECOVERY_EVIDENCE, hypothesis.lastOutcome)
        val sameItem = replay(event(item = "item.a", family = "fam.a"), event(EvidenceOutcome.POSITIVE, item = "item.a", family = "fam.b"))
        assertEquals(WeaknessSignal.SUPPORTED, sameItem.signal)
        val sameFamily = replay(event(item = "item.a", family = "fam.a"), event(EvidenceOutcome.POSITIVE, item = "item.b", family = "fam.a"))
        assertEquals(WeaknessSignal.SUPPORTED, sameFamily.signal)
        // Help taken never closes anything: the AI may scaffold, not satisfy closure.
        val assisted = replay(event(), event(EvidenceOutcome.POSITIVE, independence = IndependenceClass.ASSISTED))
        assertEquals(WeaknessSignal.SUPPORTED, assisted.signal)
        val verification = replay(event(before = true, after = true), event(EvidenceOutcome.POSITIVE, before = true, after = true))
        assertEquals(WeaknessSignal.RESOLVED, verification.signal)
        assertFalse(verification.verificationOpen)
    }

    @Test
    fun `a confirmed remediation closes only when the gates pass again, not after one success`() {
        val confirmed = listOf(event(before = true, after = true), event(before = true, after = false))
        val oneSuccess = WeaknessEngine.replay(objective, skill, confirmed + event(EvidenceOutcome.POSITIVE, before = false, after = false))
        assertEquals(WeaknessSignal.CONFIRMED, oneSuccess.signal)
        val restored = WeaknessEngine.replay(objective, skill, confirmed + event(EvidenceOutcome.POSITIVE, before = false, after = true))
        assertEquals(WeaknessSignal.RESOLVED, restored.signal)
        assertEquals(restored.resolutionEvidenceId, seq)
        // A new failure after resolution starts a new signal from that day.
        val again = WeaknessEngine.replay(objective, skill, listOf(event(independence = IndependenceClass.ASSISTED), event(EvidenceOutcome.POSITIVE),
            event(day = "2026-10-09")))
        assertEquals(WeaknessSignal.SUPPORTED, again.signal)
        assertEquals("2026-10-09", again.firstSeenDay)
        assertNull(again.resolutionEvidenceId)
    }

    @Test
    fun `the engine supplies weakness needs only for an open, uncovered concern`() {
        fun state(name: String, weakness: String?, mastery: MasteryAxisState? = MasteryAxisState.DEVELOPING_INDEPENDENT,
                  lifecycle: String = "published", critical: Boolean = false) =
            SkillPlanningState(VersionedRef("skill.$name", 1), lifecycle, critical, mastery, RetentionAxis.NOT_YET_EVALUATED, weakness, "skill_state:$name")
        val needs = WeaknessEngine.needs(listOf(
            state("hypothesis", "hypothesis"),
            state("supported", "supported", critical = true),
            state("verifying", "supported", MasteryAxisState.CONFIRMATION_VERIFICATION_DUE),
            state("remediation", "remediation_required"),
            state("resolved", "resolved"),
            state("none", "none"),
            state("unwritten", null),
            state("draft", "supported", lifecycle = "draft"),
        ))
        assertEquals(listOf("weakness_detected:skill.hypothesis@v1", "weakness_detected:skill.supported@v1"), needs.map { it.needKey })
        assertTrue(needs.all { it.trigger == NeedTrigger.WEAKNESS_DETECTED })
        assertEquals(EvidenceSeverity.PARTIAL_OR_UNCERTAIN_CONCERN, needs[0].evidenceSeverity)
        assertEquals(EvidenceSeverity.CLEAN_CONTRADICTION_OR_VERIFICATION_DUE, needs[1].evidenceSeverity)
        assertEquals(listOf("skill_state:supported"), needs[1].sourceStateRefs)
    }

    @Test
    fun `the replay is the same whatever order the events arrive in`() {
        val events = listOf(event(independence = IndependenceClass.ASSISTED), event(), event(EvidenceOutcome.POSITIVE))
        assertEquals(WeaknessEngine.replay(objective, skill, events), WeaknessEngine.replay(objective, skill, events.reversed()))
        assertEquals(ObjectiveWeakness(objective, skill), WeaknessEngine.replay(objective, skill, emptyList()))
    }
}
