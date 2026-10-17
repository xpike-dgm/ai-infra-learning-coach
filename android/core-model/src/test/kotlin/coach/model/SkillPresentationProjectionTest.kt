package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * 16A (`SKAX-v0`): one Skill's learner-facing state is `TEPM-v0` §4.1's precedence over the four axes as their engines
 * wrote them — and where an axis a rule needs is unwritten, it is "recomputing", never a guess (user decision).
 */
class SkillPresentationProjectionTest {

    private fun reading(
        mastery: MasteryAxisState? = MasteryAxisState.NOT_YET_EVIDENCED,
        retention: RetentionAxis = RetentionAxis.UNTRACKED,
        weakness: WeaknessAxis = WeaknessAxis.NONE,
        prerequisite: PrerequisiteEligibility? = PrerequisiteEligibility.ELIGIBLE,
        evidenced: Boolean = true,
        stale: Boolean = false,
        qualifiers: Set<AttentionQualifier> = emptySet(),
    ) = SkillAxisReading(mastery, retention, weakness, prerequisite, evidenced, stale, qualifiers)

    private fun state(r: SkillAxisReading): SkillPresentationState =
        assertIs<SkillStateProjection.Derived>(SkillPresentationProjection.derive(r)).state

    private fun recomputing(r: SkillAxisReading): SkillStateProjection.Recomputing =
        assertIs<SkillStateProjection.Recomputing>(SkillPresentationProjection.derive(r))

    @Test
    fun `there are exactly eight states and their order is the declared precedence`() {
        assertEquals(
            listOf("remediation_required", "confirmation_verification_due", "confirmed_review_due", "confirmed_current",
                "prerequisite_unresolved", "developing_independent", "developing_with_support", "not_yet_evidenced"),
            SkillPresentationState.precedence.map { it.id },
        )
    }

    @Test
    fun `a learner who has done nothing is not yet evidenced, or waits on a prerequisite`() {
        val nothing = reading(mastery = null, retention = RetentionAxis.NOT_YET_EVALUATED, weakness = WeaknessAxis.NOT_YET_EVALUATED, evidenced = false)
        assertEquals(SkillPresentationState.NOT_YET_EVIDENCED, state(nothing))
        assertEquals(SkillPresentationState.PREREQUISITE_UNRESOLVED, state(nothing.copy(prerequisite = PrerequisiteEligibility.BLOCKED)))
        assertEquals(SkillPresentationState.PREREQUISITE_UNRESOLVED,
            state(nothing.copy(prerequisite = PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA)))
    }

    @Test
    fun `evidence no engine has read is recomputing, never nothing yet`() {
        val r = recomputing(reading(mastery = null, weakness = WeaknessAxis.NOT_YET_EVALUATED, evidenced = true))
        assertEquals(listOf(UnevaluatedAxis.MASTERY), r.unevaluated)
    }

    @Test
    fun `a row older than the newest evidence is recomputing, whatever it says`() {
        val r = recomputing(reading(mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.STABLE, stale = true))
        assertTrue(r.stale)
    }

    @Test
    fun `a confirmed Skill the retention engine has not scheduled is recomputing`() {
        listOf(RetentionAxis.UNTRACKED, RetentionAxis.NOT_YET_EVALUATED).forEach { retention ->
            assertEquals(listOf(UnevaluatedAxis.RETENTION),
                recomputing(reading(mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = retention)).unevaluated)
        }
    }

    @Test
    fun `an unwritten weakness axis on evidenced work is recomputing`() {
        assertEquals(listOf(UnevaluatedAxis.WEAKNESS),
            recomputing(reading(mastery = MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = WeaknessAxis.NOT_YET_EVALUATED)).unevaluated)
    }

    @Test
    fun `rule 1 an open remediation comes first`() {
        assertEquals(SkillPresentationState.REMEDIATION_REQUIRED,
            state(reading(mastery = MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = WeaknessAxis.REMEDIATION_REQUIRED)))
        assertEquals(SkillPresentationState.REMEDIATION_REQUIRED, state(reading(
            mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.REVIEW_DUE, weakness = WeaknessAxis.REMEDIATION_REQUIRED)))
    }

    @Test
    fun `rule 2 a first clean contradiction is a verification, not a demotion (E7E-F03)`() {
        assertEquals(SkillPresentationState.CONFIRMATION_VERIFICATION_DUE,
            state(reading(mastery = MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, retention = RetentionAxis.VERIFICATION_DUE)))
        assertEquals(SkillPresentationState.CONFIRMATION_VERIFICATION_DUE,
            state(reading(mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.VERIFICATION_DUE)))
    }

    @Test
    fun `rule 3 review due keeps the confirmation, even months overdue (E7E-F02, F16)`() {
        assertEquals(SkillPresentationState.CONFIRMED_REVIEW_DUE,
            state(reading(mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.REVIEW_DUE)))
    }

    @Test
    fun `rule 4 confirmed and current, and at risk is a qualifier beside it`() {
        listOf(RetentionAxis.FRESH, RetentionAxis.STABLE).forEach {
            assertEquals(SkillPresentationState.CONFIRMED_CURRENT, state(reading(mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = it)))
        }
        val atRisk = assertIs<SkillStateProjection.Derived>(SkillPresentationProjection.derive(
            reading(mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.AT_RISK)))
        assertEquals(SkillPresentationState.CONFIRMED_CURRENT, atRisk.state)
        assertEquals(listOf(AttentionQualifier.RETENTION_AT_RISK), atRisk.qualifiers)
    }

    @Test
    fun `rule 5 a missing prerequisite holds work that has no clean independent evidence (E7E-F11)`() {
        assertEquals(SkillPresentationState.PREREQUISITE_UNRESOLVED,
            state(reading(mastery = MasteryAxisState.DEVELOPING_WITH_SUPPORT, prerequisite = PrerequisiteEligibility.BLOCKED)))
        // Clean independent evidence is interpretable; it stays what it is.
        assertEquals(SkillPresentationState.DEVELOPING_INDEPENDENT,
            state(reading(mastery = MasteryAxisState.DEVELOPING_INDEPENDENT, prerequisite = PrerequisiteEligibility.BLOCKED)))
        // A confirmed Skill is never re-labelled as waiting.
        assertEquals(SkillPresentationState.CONFIRMED_CURRENT, state(reading(
            mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.STABLE, prerequisite = PrerequisiteEligibility.BLOCKED)))
    }

    @Test
    fun `a rule that needs the prerequisite answer and has none is recomputing`() {
        assertEquals(listOf(UnevaluatedAxis.PREREQUISITE),
            recomputing(reading(mastery = MasteryAxisState.NOT_YET_EVIDENCED, prerequisite = null)).unevaluated)
    }

    @Test
    fun `rules 6 to 8 independent, assisted only, otherwise (E7E-F05, F06)`() {
        assertEquals(SkillPresentationState.DEVELOPING_INDEPENDENT, state(reading(mastery = MasteryAxisState.DEVELOPING_INDEPENDENT)))
        assertEquals(SkillPresentationState.DEVELOPING_WITH_SUPPORT, state(reading(mastery = MasteryAxisState.DEVELOPING_WITH_SUPPORT)))
        assertEquals(SkillPresentationState.NOT_YET_EVIDENCED, state(reading(mastery = MasteryAxisState.NOT_YET_EVIDENCED)))
        // Only invalid evidence (E7E-F18): the mastery engine found nothing to count.
        assertEquals(SkillPresentationState.NOT_YET_EVIDENCED, state(reading(mastery = MasteryAxisState.NOT_YET_EVIDENCED, evidenced = true)))
    }

    /** Every combination of every axis value, so no ordering of the rules can slip past one case. */
    private fun everyReading(): Sequence<SkillAxisReading> = sequence {
        for (mastery in MasteryAxisState.entries + listOf(null))
            for (retention in RetentionAxis.entries)
                for (weakness in WeaknessAxis.entries)
                    for (prerequisite in PrerequisiteEligibility.entries + listOf(null))
                        for (evidenced in listOf(true, false))
                            for (stale in listOf(true, false))
                                for (q in listOf(emptySet(), setOf(AttentionQualifier.PROVISIONAL_EVALUATION_PRESENT)))
                                    yield(SkillAxisReading(mastery, retention, weakness, prerequisite, evidenced, stale, q))
    }

    @Test
    fun `no combination of axes confirms a Skill its mastery engine did not confirm`() {
        val confirmed = setOf(SkillPresentationState.CONFIRMED_CURRENT, SkillPresentationState.CONFIRMED_REVIEW_DUE)
        everyReading().forEach { r ->
            val p = SkillPresentationProjection.derive(r)
            if (p is SkillStateProjection.Derived && p.state in confirmed) {
                assertEquals(MasteryAxisState.CONFIRMED_CURRENT, r.mastery, "$r")
            }
        }
    }

    @Test
    fun `at risk is never dropped and never a state`() {
        everyReading().forEach { r ->
            val p = SkillPresentationProjection.derive(r)
            if (p is SkillStateProjection.Derived) {
                assertEquals(r.retention == RetentionAxis.AT_RISK, AttentionQualifier.RETENTION_AT_RISK in p.qualifiers, "$r")
                assertTrue(p.qualifiers.toSet().containsAll(r.evidenceQualifiers), "$r")
            }
        }
    }

    @Test
    fun `remediation is shown exactly when the weakness axis opened it`() {
        everyReading().forEach { r ->
            val p = SkillPresentationProjection.derive(r)
            if (p is SkillStateProjection.Derived) {
                assertEquals(r.weakness == WeaknessAxis.REMEDIATION_REQUIRED, p.state == SkillPresentationState.REMEDIATION_REQUIRED, "$r")
            }
        }
    }

    @Test
    fun `a stale row is never derived, and the stored value is never a ninth state`() {
        everyReading().forEach { r ->
            val p = SkillPresentationProjection.derive(r)
            if (r.projectionStale) assertIs<SkillStateProjection.Recomputing>(p, "$r")
            val stored = SkillPresentationProjection.storedId(r)
            assertTrue(stored == SkillPresentationProjection.NOT_DERIVED || SkillPresentationState.of(stored) != null, stored)
            // The same reading always gives the same answer.
            assertEquals(p, SkillPresentationProjection.derive(r))
        }
    }

    @Test
    fun `review due never wears the attention of a verification or a remediation`() {
        everyReading().filter { it.retention == RetentionAxis.REVIEW_DUE && it.weakness != WeaknessAxis.REMEDIATION_REQUIRED &&
            it.mastery == MasteryAxisState.CONFIRMED_CURRENT && !it.projectionStale &&
            !(it.evidenced && it.weakness == WeaknessAxis.NOT_YET_EVALUATED) }.forEach { r ->
            assertEquals(SkillPresentationState.CONFIRMED_REVIEW_DUE, (SkillPresentationProjection.derive(r) as SkillStateProjection.Derived).state)
        }
    }

    // ---------------------------------------------------------------- evidence kinds and qualifiers

    private var nextId = 1L

    private fun row(
        outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        status: EvaluatorStatus = EvaluatorStatus.VERIFIED,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        contested: Boolean = false,
        prerequisiteValid: Boolean = true,
        solutionExposed: Boolean = false,
    ): EvidenceRow {
        val id = nextId++
        return EvidenceRow(id, id, VersionedRef("objective.x.y", 1), VersionedRef("skill.x", 1), "code_writing", outcome, status,
            independence, contested, null, null, null, prerequisiteValid = prerequisiteValid, solutionExposed = solutionExposed)
    }

    @Test
    fun `evidence is shown by kind, and a row that cannot count is never shown as counting`() {
        assertEquals(EvidenceKind.INDEPENDENT, EvidenceKind.of(row()))
        assertEquals(EvidenceKind.INDEPENDENT, EvidenceKind.of(row(outcome = EvidenceOutcome.NEGATIVE)))
        assertEquals(EvidenceKind.ASSISTED, EvidenceKind.of(row(independence = IndependenceClass.ASSISTED)))
        assertEquals(EvidenceKind.ASSISTED, EvidenceKind.of(row(independence = IndependenceClass.PRACTICE_ONLY)))
        assertEquals(EvidenceKind.ASSISTED, EvidenceKind.of(row(independence = IndependenceClass.REQUIRES_INDEPENDENT_RECHECK)))
        assertEquals(EvidenceKind.ASSISTED, EvidenceKind.of(row(solutionExposed = true)))
        assertEquals(EvidenceKind.PROVISIONAL, EvidenceKind.of(row(status = EvaluatorStatus.PROVISIONAL)))
        assertEquals(EvidenceKind.NOT_COUNTED, EvidenceKind.of(row(outcome = EvidenceOutcome.INVALID)))
        assertEquals(EvidenceKind.NOT_COUNTED, EvidenceKind.of(row(status = EvaluatorStatus.INVALID)))
        assertEquals(EvidenceKind.NOT_COUNTED, EvidenceKind.of(row(contested = true)))
        assertEquals(EvidenceKind.NOT_COUNTED, EvidenceKind.of(row(prerequisiteValid = false, status = EvaluatorStatus.PROVISIONAL)))
        val summary = EvidenceSummary.of(listOf(row(), row(status = EvaluatorStatus.PROVISIONAL), row(contested = true)))
        assertEquals(3, summary.total)
        assertEquals(1, summary.counts[EvidenceKind.INDEPENDENT])
    }

    @Test
    fun `a recheck stays open until an independent row on that Objective comes after it`() {
        val recheck = row(independence = IndependenceClass.REQUIRES_INDEPENDENT_RECHECK)
        assertTrue(AttentionQualifier.REQUIRES_INDEPENDENT_RECHECK in EvidenceQualifiers.of(listOf(listOf(recheck))))
        assertFalse(AttentionQualifier.REQUIRES_INDEPENDENT_RECHECK in EvidenceQualifiers.of(listOf(listOf(recheck, row()))))
        assertTrue(AttentionQualifier.REQUIRES_INDEPENDENT_RECHECK in EvidenceQualifiers.of(listOf(listOf(recheck, row(independence = IndependenceClass.ASSISTED)))))
        assertTrue(AttentionQualifier.CONTESTED_EVIDENCE_PRESENT in EvidenceQualifiers.of(listOf(listOf(row(contested = true)))))
        assertTrue(AttentionQualifier.PROVISIONAL_EVALUATION_PRESENT in EvidenceQualifiers.of(listOf(listOf(row(status = EvaluatorStatus.PROVISIONAL)))))
        assertEquals(emptySet(), EvidenceQualifiers.of(listOf(listOf(row(), row(outcome = EvidenceOutcome.NEGATIVE)))))
    }
}
