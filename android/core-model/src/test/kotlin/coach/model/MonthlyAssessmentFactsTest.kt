package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MonthlyAssessmentFactsTest {

    private val skillA = VersionedRef("skill.c.pointer_dereference", 1)
    private val skillB = VersionedRef("skill.python.loops", 2)
    private val objA = VersionedRef("objective.c.pointer_dereference.trace", 1)
    private val itemA = VersionedRef("item.c.pointer.trace_01", 1)

    private fun slot(id: String, skill: VersionedRef, item: VersionedRef?, role: SlotRole = MonthlyRole.DELAYED_RETENTION_SAMPLING) =
        AssessmentBlueprintSlot(
            slotId = id, role = role, needKey = "retention_review_due:$skill",
            trigger = NeedTrigger.RETENTION_REVIEW_DUE, targetSkill = skill, criticality = Criticality.REQUIRED,
            band = PriorityBand.P3, sourceStateRefs = listOf("skill_state:$skill#watermark=4"), track = null,
            status = if (item == null) SlotStatus.NO_VALID_ITEM else SlotStatus.READY, item = item,
            targetObjectives = if (item == null) emptyList() else listOf(objA),
            evidenceType = item?.let { "code_reading" }, variantFamilyId = item?.let { "fam.a" },
            expectedActiveMinutes = item?.let { 20 }, itemLifecycle = item?.let { LifecycleStatus.TRUSTED },
        )

    private fun monthly(vararg slots: AssessmentBlueprintSlot) = AssessmentBlueprint(
        scope = AssessmentScope.MONTHLY_CAPABILITY,
        cycleId = "2026-10", studyDay = "2026-10-01", curriculumVersion = 3, truthWatermark = 91,
        policyVersion = "MCA-v0", evaluatorAvailable = false, recentSince = "2026-09-02", slots = slots.toList(),
        exclusions = listOf(PoolExclusion("new_learning:$skillB", skillB, BlueprintExclusion.NOT_TAUGHT_YET)),
        reasonCodes = listOf(MonthlyReasonCodes.DUE, MonthlyReasonCodes.BLUEPRINT_GENERATED),
        priorSessionId = 12,
    )

    @Test
    fun `the cycle is the calendar month of the recorded study day`() {
        assertEquals("2026-10", MonthlyCycle.of("2026-10-01"))
        assertEquals("2026-10", MonthlyCycle.of("2026-10-31"))
        assertEquals("2026-09", MonthlyCycle.of("2026-09-30"))
        assertEquals("2027-01", MonthlyCycle.of("2027-01-01"))
        assertEquals("2028-02", MonthlyCycle.of("2028-02-29"))
    }

    @Test
    fun `the eight roles are MCA-v0's, in its order, and selection follows section 7`() {
        assertEquals(
            listOf("longitudinal_required_capability", "persistent_weakness_or_verification", "critical_capability_revalidation",
                "delayed_retention_sampling", "cross_topic_transfer", "integrated_application", "parallel_technical_english",
                "professional_evidence_checkpoint"),
            MonthlyRole.entries.map { it.id },
        )
        assertEquals(
            listOf(MonthlyRole.PERSISTENT_WEAKNESS_OR_VERIFICATION, MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION,
                MonthlyRole.LONGITUDINAL_REQUIRED_CAPABILITY, MonthlyRole.DELAYED_RETENTION_SAMPLING,
                MonthlyRole.CROSS_TOPIC_TRANSFER, MonthlyRole.INTEGRATED_APPLICATION,
                MonthlyRole.PARALLEL_TECHNICAL_ENGLISH, MonthlyRole.PROFESSIONAL_EVIDENCE_CHECKPOINT),
            MonthlyRole.SELECTION_ORDER,
        )
        assertEquals(MonthlyRole.entries.toSet(), MonthlyRole.SELECTION_ORDER.toSet())
        // Retention sampled in a monthly session is still retention (DMA-v0 §2).
        assertEquals(TaskPurpose.RETAIN, MonthlyRole.DELAYED_RETENTION_SAMPLING.purpose)
        MonthlyRole.entries.filterNot { it == MonthlyRole.DELAYED_RETENTION_SAMPLING }.forEach { assertEquals(TaskPurpose.ASSESS, it.purpose) }
        // Each MCA-v0 §27 longitudinal list has the role that feeds it; weekly roles feed none.
        assertEquals(RoleEvidenceKind.CRITICAL_REVALIDATION, MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION.evidenceKind)
        assertEquals(RoleEvidenceKind.RETENTION_REVALIDATION, MonthlyRole.DELAYED_RETENTION_SAMPLING.evidenceKind)
        assertEquals(RoleEvidenceKind.TRANSFER, MonthlyRole.CROSS_TOPIC_TRANSFER.evidenceKind)
        assertEquals(RoleEvidenceKind.INTEGRATION, MonthlyRole.INTEGRATED_APPLICATION.evidenceKind)
        assertTrue(BlueprintRole.entries.all { it.evidenceKind == null })
        // No monthly role id collides with a weekly one: a role can never decode in the other scope.
        assertTrue(MonthlyRole.entries.map { it.id }.none { id -> BlueprintRole.entries.any { it.id == id } })
    }

    @Test
    fun `the monthly reason codes are MCA-v0 section 29, twenty-one and in order`() {
        assertEquals(21, MonthlyReasonCodes.CATALOG.size)
        assertEquals(MonthlyReasonCodes.CATALOG.toSet().size, MonthlyReasonCodes.CATALOG.size)
        assertTrue(MonthlyReasonCodes.CATALOG.all { it.startsWith("assessment.monthly.") })
        MonthlyRole.entries.forEach { assertTrue(it.reasonCode in MonthlyReasonCodes.CATALOG) }
        assertEquals("assessment.monthly.due", MonthlyReasonCodes.CATALOG.first())
        assertEquals("assessment.monthly.replan_after_result", MonthlyReasonCodes.CATALOG.last())
        val codes: ScopeReasonCodes = MonthlyReasonCodes
        listOf(codes.due, codes.blueprintGenerated, codes.noEligibleTarget, codes.noValidItem, codes.partialSession,
            codes.incompleteNotFailure, codes.assistanceRecheckRequired, codes.invalidItemReplaced,
            codes.prerequisiteContaminated, codes.evidenceRecorded, codes.replanAfterResult, codes.noExamDebt,
        ).forEach { assertTrue(it in MonthlyReasonCodes.CATALOG, it) }
        assertEquals(MonthlyReasonCodes, BlueprintScopes.codes(AssessmentScope.MONTHLY_CAPABILITY))
        assertEquals(WeeklyReasonCodes, BlueprintScopes.codes(AssessmentScope.WEEKLY_BLUEPRINT))
    }

    @Test
    fun `a blueprint holds only its own scope's roles, and only a month names a prior session`() {
        assertFailsWith<IllegalArgumentException> { monthly(slot("slot-1", skillA, itemA, role = BlueprintRole.RETENTION_DUE)) }
        val weekly = AssessmentBlueprint(
            scope = AssessmentScope.WEEKLY_BLUEPRINT, cycleId = "2026-W40", studyDay = "2026-10-01", curriculumVersion = 3,
            truthWatermark = 91, policyVersion = "WBA-v0", evaluatorAvailable = false, recentSince = null,
            slots = emptyList(), exclusions = emptyList(), reasonCodes = emptyList(),
        )
        assertFailsWith<IllegalArgumentException> { weekly.copy(slots = listOf(slot("slot-1", skillA, itemA))) }
        assertFailsWith<IllegalArgumentException> { weekly.copy(priorSessionId = 3) }
        assertFailsWith<IllegalArgumentException> { weekly.copy(scope = AssessmentScope.DAILY_MICRO) }
        // Blocks follow the monthly selection order, not the listing order.
        val two = monthly(
            slot("slot-1", skillA, itemA, role = MonthlyRole.DELAYED_RETENTION_SAMPLING),
            slot("slot-2", skillB, VersionedRef("item.python.loops.trace", 1), role = MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION)
                .copy(variantFamilyId = "fam.b"),
        )
        assertEquals(listOf("block-critical_capability_revalidation", "block-delayed_retention_sampling"), two.blocks.map { it.id })
        assertEquals(40, two.expectedActiveMinutes)
        // §5 lists longitudinal first; §7 selects a persistent concern first, and the blocks follow §7.
        val listedFirst = monthly(
            slot("slot-1", skillA, itemA, role = MonthlyRole.LONGITUDINAL_REQUIRED_CAPABILITY),
            slot("slot-2", skillB, VersionedRef("item.python.loops.trace", 1), role = MonthlyRole.PERSISTENT_WEAKNESS_OR_VERIFICATION)
                .copy(variantFamilyId = "fam.b"),
        )
        assertEquals(listOf("block-persistent_weakness_or_verification", "block-longitudinal_required_capability"), listedFirst.blocks.map { it.id })
    }

    @Test
    fun `the monthly codec reads back exactly what it wrote, prior session included`() {
        val stored = monthly(
            slot("slot-1", skillA, itemA).copy(
                reasonCodes = listOf("assessment.monthly.slot_delayed_retention"),
                rejections = listOf(SlotItemRejection(VersionedRef("item.x", 2), listOf("already_seen"))),
                requiredForSessionClosure = true,
            ),
            slot("slot-2", skillB, null, role = MonthlyRole.INTEGRATED_APPLICATION),
        ).copy(supersedesSessionId = 15)
        val text = MonthlyBlueprintCodec.encode(stored)
        assertEquals(MonthlyBlueprintCodec.FORMAT, text.lines().first())
        assertTrue("\tprior_session=12" in text)
        assertEquals(stored, MonthlyBlueprintCodec.decode(text))
        assertEquals(stored, BlueprintCodecs.decode(AssessmentScope.MONTHLY_CAPABILITY, text))
        assertEquals(text, BlueprintCodecs.encode(stored))
        assertEquals(stored.copy(priorSessionId = null), MonthlyBlueprintCodec.decode(MonthlyBlueprintCodec.encode(stored.copy(priorSessionId = null))))
    }

    @Test
    fun `a format never reads the other scope's text`() {
        val text = MonthlyBlueprintCodec.encode(monthly(slot("slot-1", skillA, itemA)))
        assertNotNull(MonthlyBlueprintCodec.decode(text))
        assertNull(WeeklyBlueprintCodec.decode(text))
        assertNull(BlueprintCodecs.decode(AssessmentScope.WEEKLY_BLUEPRINT, text))
        assertNull(BlueprintCodecs.decode(AssessmentScope.DAILY_MICRO, text))
        // Relabelled as weekly, the monthly head and roles are still refused.
        assertNull(WeeklyBlueprintCodec.decode(text.replaceFirst("monthly_blueprint/1", "weekly_blueprint/1")))
        assertNull(MonthlyBlueprintCodec.decode(text.replace("\tprior_session=12", "")))
        assertNull(MonthlyBlueprintCodec.decode(text.replace("\trole=delayed_retention_sampling", "\trole=retention_due")))
        assertNull(MonthlyBlueprintCodec.decode(text.replaceFirst("monthly_blueprint/1", "monthly_blueprint/2")))
        assertFailsWith<IllegalArgumentException> { WeeklyBlueprintCodec.encode(monthly()) }
        val weekly = monthly().copy(scope = AssessmentScope.WEEKLY_BLUEPRINT, priorSessionId = null, cycleId = "2026-W40")
        assertFailsWith<IllegalArgumentException> { MonthlyBlueprintCodec.encode(weekly) }
        assertNull(MonthlyBlueprintCodec.decode(WeeklyBlueprintCodec.encode(weekly)))
        // A head field one format does not have is not read past: the weekly head names no prior session.
        val weeklyText = WeeklyBlueprintCodec.encode(weekly)
        assertNotNull(WeeklyBlueprintCodec.decode(weeklyText))
        assertNull(WeeklyBlueprintCodec.decode(weeklyText.replace("\treasons=", "\tprior_session=3\treasons=")))
    }

    @Test
    fun `the monthly result carries the longitudinal lists and still nothing that could hold a score`() {
        val forbidden = listOf("score", "grade", "percent", "pass", "threshold", "questionCount", "duration", "ready", "readiness")
        AssessmentBlueprintResult::class.java.declaredFields.map { it.name }.forEach { field ->
            forbidden.forEach { word -> assertTrue(!field.contains(word, ignoreCase = true), field) }
        }
        val fields = AssessmentBlueprintResult::class.java.declaredFields.map { it.name }
        listOf("revalidatedCriticalSkills", "revalidatedRetentionSkills", "transferEvidenceObjectives", "integratedEvidenceObjectives")
            .forEach { assertTrue(it in fields, it) }
    }
}
