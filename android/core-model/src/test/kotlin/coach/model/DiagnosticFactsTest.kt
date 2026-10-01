package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The diagnostic waiver's vocabulary (13F): only the learner opens a diagnostic, a waiver names its evidence, a
 * waived Objective names its waiver, and the stored request decodes strictly or not at all.
 */
class DiagnosticFactsTest {

    private val skill = VersionedRef("skill.c.pointer_basics", 1)
    private fun objective(name: String) = VersionedRef("objective.c.pointer_basics.$name", 1)

    private val scope = DiagnosticScope(
        source = DiagnosticSource.PRIOR_EXPERIENCE_CLAIM,
        studyDay = "2026-10-01",
        curriculumVersion = 3,
        targets = listOf(DiagnosticTarget(objective("address_vs_value"), skill, critical = false),
            DiagnosticTarget(objective("dereference"), skill, critical = true)),
    )

    @Test
    fun `only the learner can open a diagnostic, and every way of asking is the same reason`() {
        assertEquals(listOf("user_requested_fast_path", "prior_experience_claim", "resume_after_external_learning"),
            DiagnosticSource.entries.map { it.id })
        // `planner_diagnostic_opportunity` (18B) and `curriculum_entry_placement` (16D) are not representable here.
        assertTrue(DiagnosticSource.entries.none { it.id == "planner_diagnostic_opportunity" || it.id == "curriculum_entry_placement" })
        DiagnosticSource.entries.forEach { assertEquals("diagnostic.user_requested_fast_path", it.reasonCode) }
    }

    @Test
    fun `every code is in the closed PDT-v0 diagnostic family`() {
        val catalog = ReasonCatalog.codes.getValue(ReasonCodeFamily.DIAGNOSTIC).toSet()
        val used = DiagnosticStage.entries.map { it.reasonCode } + WaiverOutcome.entries.map { it.reasonCode } + listOf(
            DiagnosticCodes.USER_REQUESTED_FAST_PATH, DiagnosticCodes.H0_REQUIRED_FOR_WAIVER, DiagnosticCodes.PREREQUISITE_BLOCKED)
        used.forEach { assertTrue(it in catalog, it) }
    }

    @Test
    fun `stages route checks and are never mastery values`() {
        assertEquals(listOf("probe", "confirm", "critical_confirm", "transfer_confirm"), DiagnosticStage.entries.map { it.id })
        assertEquals(listOf(true, true, false, false, false, false), DiagnosticObjectiveState.entries.map { it.open })
    }

    @Test
    fun `a waiver names the evidence that validated it, and only a waived Objective names one`() {
        assertFailsWith<IllegalArgumentException> { CoverageWaiver(objective("address_vs_value"), skill, 4, emptyList(), 9, "2026-10-01") }
        val waiver = CoverageWaiver(objective("address_vs_value"), skill, 4, listOf(7, 9), 9, "2026-10-01")
        assertEquals("validated_prior_knowledge", waiver.reason)
        val target = scope.targets.first()
        assertFailsWith<IllegalArgumentException> {
            ObjectiveDiagnosis(target, DiagnosticObjectiveState.WAIVED, null, emptyList(), emptyList(), emptyList(), emptyList(), null)
        }
        assertFailsWith<IllegalArgumentException> {
            ObjectiveDiagnosis(target, DiagnosticObjectiveState.NOT_DEMONSTRATED, null, emptyList(), emptyList(), emptyList(), emptyList(), waiver)
        }
        // Only an open Objective has a next stage.
        assertFailsWith<IllegalArgumentException> {
            ObjectiveDiagnosis(target, DiagnosticObjectiveState.PROBE_NEEDED, null, emptyList(), emptyList(), emptyList(), emptyList(), null)
        }
        assertFailsWith<IllegalArgumentException> {
            ObjectiveDiagnosis(target, DiagnosticObjectiveState.NOT_DEMONSTRATED, DiagnosticStage.CONFIRM, emptyList(), emptyList(), emptyList(), emptyList(), null)
        }
    }

    @Test
    fun `a scope checks at least one Objective, each once`() {
        assertFailsWith<IllegalArgumentException> { scope.copy(targets = emptyList()) }
        assertFailsWith<IllegalArgumentException> { scope.copy(targets = scope.targets + scope.targets.first()) }
        assertEquals(listOf(skill), scope.skills)
        assertEquals(scope.targets[1], scope.target(objective("dereference")))
        assertNull(scope.target(objective("write_through")))
    }

    @Test
    fun `a request and a withdrawal round-trip exactly`() {
        val request = DiagnosticRecord.Request(scope)
        assertEquals(request, DiagnosticScopeCodec.decode(DiagnosticScopeCodec.encode(request)))
        val withdrawal = DiagnosticRecord.Withdrawal(12, "2026-10-02")
        assertEquals(withdrawal, DiagnosticScopeCodec.decode(DiagnosticScopeCodec.encode(withdrawal)))
        assertTrue(DiagnosticScopeCodec.encode(request).startsWith("diagnostic_scope/1\n"))
    }

    @Test
    fun `a stored request decodes strictly or not at all`() {
        val text = DiagnosticScopeCodec.encode(DiagnosticRecord.Request(scope))
        assertNull(DiagnosticScopeCodec.decode(text.replace("diagnostic_scope/1", "diagnostic_scope/2")))
        assertNull(DiagnosticScopeCodec.decode(text.replace("prior_experience_claim", "planner_diagnostic_opportunity")))
        assertNull(DiagnosticScopeCodec.decode(text.replace("critical=true", "critical=yes")))
        assertNull(DiagnosticScopeCodec.decode(text + "\ttopic=topic.c.basic_pointers@v1"))
        // The request line takes exactly its three fields: a Topic, or anything else, is refused there too.
        assertNull(DiagnosticScopeCodec.decode(text.replace("curriculum_version=3", "curriculum_version=3\ttopic=topic.c.basic_pointers@v1")))
        assertNull(DiagnosticScopeCodec.decode(text.replace("\tcurriculum_version=3", "")))
        assertNull(DiagnosticScopeCodec.decode(text.lines().take(2).joinToString("\n")))
        assertNull(DiagnosticScopeCodec.decode(text + "\nslot\tid=1"))
        assertNull(DiagnosticScopeCodec.decode("diagnostic_scope/1\nwithdrawal\tsession=x\tstudy_day=2026-10-02"))
        assertNull(DiagnosticScopeCodec.decode("weekly_blueprint/1"))
        assertIs<DiagnosticRecord.Request>(DiagnosticScopeCodec.decode(text))
    }

    @Test
    fun `a coverage hold carries a diagnostic reason`() {
        val hold = CoverageHold(waived = true, reasonCode = DiagnosticCodes.PARTIAL_COVERAGE_WAIVER)
        assertTrue(hold.reasonCode.startsWith("diagnostic."))
    }
}
