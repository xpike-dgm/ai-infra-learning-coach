package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 16C (`PWPX-v0`): the Technical English base band is `TEPM-v0`'s one rule over the Skills' own states — the highest band
 * all of whose anchor Skills at or below it are confirmed — and the fixtures `TEPM-v0` names hold.
 */
class EnglishProfileProjectionTest {

    private fun row(id: String, state: SkillPresentationState?): SkillProgressRow {
        val mastery = when (state) {
            SkillPresentationState.CONFIRMED_CURRENT, SkillPresentationState.CONFIRMED_REVIEW_DUE -> MasteryAxisState.CONFIRMED_CURRENT
            SkillPresentationState.CONFIRMATION_VERIFICATION_DUE -> MasteryAxisState.CONFIRMATION_VERIFICATION_DUE
            SkillPresentationState.DEVELOPING_INDEPENDENT, SkillPresentationState.REMEDIATION_REQUIRED -> MasteryAxisState.DEVELOPING_INDEPENDENT
            SkillPresentationState.DEVELOPING_WITH_SUPPORT -> MasteryAxisState.DEVELOPING_WITH_SUPPORT
            else -> MasteryAxisState.NOT_YET_EVIDENCED
        }
        val retention = if (state == SkillPresentationState.CONFIRMED_REVIEW_DUE) RetentionAxis.REVIEW_DUE else RetentionAxis.FRESH
        val weakness = if (state == SkillPresentationState.REMEDIATION_REQUIRED) WeaknessAxis.REMEDIATION_REQUIRED else WeaknessAxis.NONE
        val reading = SkillAxisReading(mastery, retention, weakness, PrerequisiteEligibility.ELIGIBLE,
            evidenced = mastery != MasteryAxisState.NOT_YET_EVIDENCED, projectionStale = state == null)
        return SkillProgressRow(VersionedRef(id, 1), id.substringAfterLast('.'), SkillPresentationProjection.derive(reading),
            PrerequisiteContext(PrerequisiteEligibility.ELIGIBLE, emptyList(), emptyList(), emptyList(), emptyList()))
    }

    /** The anchors with a state each; a band left out of [states] is unpublished. `null` state = recomputing. */
    private fun profile(states: Map<String, SkillPresentationState?>, historical: CefrBand? = null, waived: Set<String> = emptySet()) =
        EnglishProfileProjection.derive(
            EnglishProfileReading(
                skills = TechnicalEnglishAnchors.anchors.map { (id, band) ->
                    EnglishSkillReading(id, band, if (id in states) row(id, states[id]) else null, id in waived)
                },
                historicalHighest = historical,
            )
        )

    private fun ids(band: CefrBand) = TechnicalEnglishAnchors.anchors.filter { it.second == band }.map { it.first }
    private fun all(band: CefrBand, state: SkillPresentationState?) = ids(band).associateWith { state }

    @Test
    fun `nothing evidenced is not yet complete, and an unpublished band is named as such, never as a gap`() {
        val p = profile(all(CefrBand.A1, SkillPresentationState.NOT_YET_EVIDENCED) + all(CefrBand.A2, SkillPresentationState.NOT_YET_EVIDENCED))
        assertNull(p.currentCompleteBand)
        assertEquals(BaseProfileStatus.NOT_YET_COMPLETE, p.status)
        assertEquals(listOf(BandCompletion.INCOMPLETE, BandCompletion.INCOMPLETE, BandCompletion.NOT_PUBLISHED), p.bands.map { it.completion })
        assertTrue(p.bands.last().missing.isEmpty())
        assertEquals(listOf("skill.english.documentation_navigation"), p.extensions.map { it.skill.logicalId })
        assertEquals(ExtensionState.NO_EXTENSION_EVIDENCE, p.extensions.single().state)
    }

    @Test
    fun `E7E-F01 partial A2 is A1 complete with the exact A2 gap visible and an uneven profile`() {
        val a2 = ids(CefrBand.A2)
        val states = all(CefrBand.A1, SkillPresentationState.CONFIRMED_CURRENT) +
            a2.associateWith { if (it == a2.first()) SkillPresentationState.DEVELOPING_INDEPENDENT else SkillPresentationState.CONFIRMED_CURRENT }
        val p = profile(states)
        assertEquals(CefrBand.A1, p.currentCompleteBand)
        assertEquals(BaseProfileStatus.COMPLETE_CURRENT, p.status)
        assertEquals(listOf(a2.first()), p.bands[1].missing.map { it.logicalId })
        assertTrue("TECHNICAL_ENGLISH_UNEVEN_PROFILE" in p.reasonCodes)
        assertTrue("TECHNICAL_ENGLISH_BASE_BAND_COMPLETE" in p.reasonCodes)
    }

    @Test
    fun `E7E-F02 and F16 a review due keeps the band, with review due and no demotion`() {
        val p = profile(all(CefrBand.A1, SkillPresentationState.CONFIRMED_CURRENT) + mapOf(ids(CefrBand.A1).first() to SkillPresentationState.CONFIRMED_REVIEW_DUE))
        assertEquals(CefrBand.A1, p.currentCompleteBand)
        assertEquals(BaseProfileStatus.COMPLETE_REVIEW_DUE, p.status)
        // The history never sits below the current band, even when the replay had nothing to say.
        assertEquals(CefrBand.A1, p.historicalHighestBand)
    }

    @Test
    fun `E7E-F03 a first clean contradiction is complete with verification due, never an instant deletion`() {
        val p = profile(all(CefrBand.A1, SkillPresentationState.CONFIRMED_CURRENT) + mapOf(ids(CefrBand.A1).first() to SkillPresentationState.CONFIRMATION_VERIFICATION_DUE))
        assertEquals(CefrBand.A1, p.currentCompleteBand)
        assertEquals(BaseProfileStatus.COMPLETE_VERIFICATION_DUE, p.status)
    }

    @Test
    fun `E7E-F04 and F15 a confirmed remediation lowers the current band and the history stays`() {
        val p = profile(
            all(CefrBand.A1, SkillPresentationState.CONFIRMED_CURRENT) + all(CefrBand.A2, SkillPresentationState.CONFIRMED_CURRENT) +
                mapOf(ids(CefrBand.A2).last() to SkillPresentationState.REMEDIATION_REQUIRED),
            historical = CefrBand.A2,
        )
        assertEquals(CefrBand.A1, p.currentCompleteBand)
        assertEquals(CefrBand.A2, p.historicalHighestBand)
        assertEquals(listOf(ids(CefrBand.A2).last()), p.remediation.map { it.logicalId })
    }

    @Test
    fun `E7E-F17 strong Skills with one required gap are not a complete band, and nothing is averaged`() {
        val a1 = ids(CefrBand.A1)
        val p = profile(a1.associateWith { if (it == a1.last()) SkillPresentationState.DEVELOPING_WITH_SUPPORT else SkillPresentationState.CONFIRMED_CURRENT })
        assertNull(p.currentCompleteBand)
        assertEquals(listOf(a1.last()), p.bands.first().missing.map { it.logicalId })
    }

    @Test
    fun `a recomputing anchor is never claimed complete`() {
        val a1 = ids(CefrBand.A1)
        val p = profile(a1.associateWith { if (it == a1.first()) null else SkillPresentationState.CONFIRMED_CURRENT })
        assertEquals(BandCompletion.RECOMPUTING, p.bands.first().completion)
        assertNull(p.currentCompleteBand)
    }

    @Test
    fun `E7E-F07 a diagnostically waived confirmed Skill names validated entry evidence as its source`() {
        val first = ids(CefrBand.A1).first()
        val p = profile(all(CefrBand.A1, SkillPresentationState.CONFIRMED_CURRENT), waived = setOf(first))
        assertEquals(ConfirmationSource.VALIDATED_ENTRY_EVIDENCE, p.confirmationSources.getValue(VersionedRef(first, 1)))
        assertEquals(ConfirmationSource.LEARNED_AND_VERIFIED, p.confirmationSources.getValue(VersionedRef(ids(CefrBand.A1)[1], 1)))
    }

    @Test
    fun `the band rule stops at an unpublished band and every code is one TEPM names`() {
        val a1 = ids(CefrBand.A1).toSet()
        val a2 = ids(CefrBand.A2).toSet()
        assertEquals(CefrBand.A2, EnglishProfileProjection.highestComplete(a1 + a2, a1 + a2))
        assertNull(EnglishProfileProjection.highestComplete(a2, a1 + a2))
        assertEquals(CefrBand.A1, EnglishProfileProjection.highestComplete(a1 + a2, a1))
        assertEquals(15, TechnicalEnglishAnchors.anchors.size)
        assertEquals(listOf(5, 5, 5), CefrBand.entries.map { b -> TechnicalEnglishAnchors.anchors.count { it.second == b } })
    }
}
