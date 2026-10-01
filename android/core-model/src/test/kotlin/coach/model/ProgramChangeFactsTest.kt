package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 13E's facts: gate profiles read from the curriculum as published, and a report that cannot overclaim. */
class ProgramChangeFactsTest {

    private val skill = VersionedRef("skill.python.loops", 1)

    private fun row(criticality: String, required: Boolean = true) = ObjectiveRow(VersionedRef("objective.python.loops.trace", 1), skill,
        required, criticality, listOf("code_reading", "explanation"), listOf("code_reading"), "code_reading")

    @Test
    fun `a gate profile is the curriculum's own required flag, criticality and evidence types`() {
        val critical = ObjectiveGateProfiles.of(row("critical"))
        assertTrue(critical.critical)
        assertTrue(critical.required)
        assertEquals(listOf("code_reading", "explanation"), critical.acceptableEvidenceTypes)
        assertEquals(listOf("code_reading"), critical.directEvidenceTypes)
        assertEquals("code_reading", critical.requiredDirectType)

        val optional = ObjectiveGateProfiles.of(row("standard", required = false))
        assertFalse(optional.critical)
        assertFalse(optional.required)
        // Every other gate parameter stays GRE-v0's default rather than something invented here.
        assertEquals(ObjectiveGateProfile(optional.ref, false, false, optional.acceptableEvidenceTypes,
            optional.directEvidenceTypes, optional.requiredDirectType), optional)
    }

    @Test
    fun `a criticality the curriculum does not define is refused, not read as standard`() {
        assertFailsWith<IllegalArgumentException> { ObjectiveGateProfiles.of(row("important")) }
        assertFailsWith<IllegalArgumentException> { ObjectiveGateProfiles.of(row("")) }
    }

    @Test
    fun `every state change belongs to one of the result's families and records a replan reason`() {
        val families = setOf("confirmed_capabilities", "verification_needed", "persistent_targeted_gaps", "retention_revalidated")
        StateChangeKind.entries.forEach {
            assertTrue(it.family in families, it.id)
            assertTrue(it.reasonCode.startsWith("replan."), it.id)
        }
        // A hypothesis is never filed as a gap (`SPWX-v0`).
        assertEquals("verification_needed", StateChangeKind.WEAKNESS_QUESTION_OPENED.family)
        assertEquals("persistent_targeted_gaps", StateChangeKind.REMEDIATION_OPENED.family)
    }

    @Test
    fun `a report reads forward and says nothing changed only when nothing did`() {
        assertFailsWith<IllegalArgumentException> { ProgramChangeReport(3, 2, emptyList(), emptyList(), emptyList(), emptyList(), false) }
        val empty = ProgramChangeReport(2, 2, emptyList(), emptyList(), emptyList(), listOf(skill), firstPlan = false)
        assertTrue(empty.nothingChanged)
        val change = StateChange(skill, StateChangeKind.CAPABILITY_CONFIRMED, "developing_independent", "confirmed_current")
        assertFalse(empty.copy(stateChanges = listOf(change)).nothingChanged)
        assertFalse(empty.copy(planChanges = listOf(PlanChange(PlanChangeKind.NEED_CLOSED, "k", null, emptyList()))).nothingChanged)
        assertEquals("skill_state:skill.python.loops@v1#capability_confirmed", change.ref)
    }
}
