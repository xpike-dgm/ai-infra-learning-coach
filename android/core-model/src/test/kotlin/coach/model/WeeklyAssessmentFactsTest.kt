package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WeeklyAssessmentFactsTest {

    private val skillA = VersionedRef("skill.c.pointer_dereference", 1)
    private val skillB = VersionedRef("skill.python.loops", 2)
    private val objA = VersionedRef("objective.c.pointer_dereference.trace", 1)
    private val itemA = VersionedRef("item.c.pointer.trace_01", 1)
    private val itemB = VersionedRef("item.python.loops.debug_01", 3)

    private fun slot(id: String, skill: VersionedRef, item: VersionedRef?, family: String? = null, group: String? = null) =
        AssessmentBlueprintSlot(
            slotId = id, role = BlueprintRole.RETENTION_DUE, needKey = "retention_review_due:$skill",
            trigger = NeedTrigger.RETENTION_REVIEW_DUE, targetSkill = skill, criticality = Criticality.REQUIRED,
            band = PriorityBand.P3, sourceStateRefs = listOf("skill_state:$skill#watermark=4"), track = null,
            status = if (item == null) SlotStatus.NO_VALID_ITEM else SlotStatus.READY, item = item,
            targetObjectives = if (item == null) emptyList() else listOf(objA),
            evidenceType = item?.let { "code_reading" }, variantFamilyId = family, dependencyGroupId = group,
            expectedActiveMinutes = item?.let { 12 }, itemLifecycle = item?.let { LifecycleStatus.VALIDATED },
            allowedTools = listOf("compiler", "terminal"),
        )

    private fun blueprint(vararg slots: AssessmentBlueprintSlot) = AssessmentBlueprint(
        scope = AssessmentScope.WEEKLY_BLUEPRINT,
        cycleId = "2026-W40", studyDay = "2026-10-01", curriculumVersion = 3, truthWatermark = 91,
        policyVersion = "WBA-v0", evaluatorAvailable = false, recentSince = null, slots = slots.toList(),
        exclusions = listOf(PoolExclusion("new_learning:$skillB", skillB, BlueprintExclusion.NOT_TAUGHT_YET)),
        reasonCodes = listOf(WeeklyReasonCodes.DUE, WeeklyReasonCodes.BLUEPRINT_GENERATED),
    )

    @Test
    fun `the cycle is the ISO week of the recorded study day`() {
        assertEquals("2026-W40", WeeklyCycle.of("2026-10-01"))
        // Monday starts the week; Sunday still belongs to the week before it.
        assertEquals("2026-W40", WeeklyCycle.of("2026-09-28"))
        assertEquals("2026-W39", WeeklyCycle.of("2026-09-27"))
        // The ISO week-based year, not the calendar year, names the week across New Year.
        assertEquals("2026-W53", WeeklyCycle.of("2027-01-01"))
        assertEquals("2027-W01", WeeklyCycle.of("2027-01-04"))
        assertEquals("2020-W01", WeeklyCycle.of("2019-12-30"))
    }

    @Test
    fun `the six roles are WBA-v0's, in its order, and selection follows section 9`() {
        assertEquals(
            listOf("recent_required_progress", "weakness_or_verification", "critical_prerequisite_confidence",
                "retention_due", "integration_or_transfer", "parallel_english"),
            BlueprintRole.entries.map { it.id },
        )
        assertEquals(
            listOf(BlueprintRole.WEAKNESS_OR_VERIFICATION, BlueprintRole.CRITICAL_PREREQUISITE_CONFIDENCE,
                BlueprintRole.RECENT_REQUIRED_PROGRESS, BlueprintRole.RETENTION_DUE,
                BlueprintRole.INTEGRATION_OR_TRANSFER, BlueprintRole.PARALLEL_ENGLISH),
            BlueprintRole.SELECTION_ORDER,
        )
        // Retention measured in a weekly session is still retention (DMA-v0 §2).
        assertEquals(TaskPurpose.RETAIN, BlueprintRole.RETENTION_DUE.purpose)
        BlueprintRole.entries.filterNot { it == BlueprintRole.RETENTION_DUE }.forEach { assertEquals(TaskPurpose.ASSESS, it.purpose) }
    }

    @Test
    fun `the weekly reason codes are WBA-v0 section 29, nineteen and in order`() {
        assertEquals(19, WeeklyReasonCodes.CATALOG.size)
        assertEquals(WeeklyReasonCodes.CATALOG.toSet().size, WeeklyReasonCodes.CATALOG.size)
        assertTrue(WeeklyReasonCodes.CATALOG.all { it.startsWith("assessment.weekly.") })
        BlueprintRole.entries.forEach { assertTrue(it.reasonCode in WeeklyReasonCodes.CATALOG) }
        assertEquals("assessment.weekly.due", WeeklyReasonCodes.CATALOG.first())
        assertEquals("assessment.weekly.no_exam_debt", WeeklyReasonCodes.CATALOG.last())
    }

    @Test
    fun `a slot is ready exactly when it has an item, minutes and the item's trust`() {
        assertFailsWith<IllegalArgumentException> {
            slot("slot-1", skillA, itemA).copy(expectedActiveMinutes = null)
        }
        assertFailsWith<IllegalArgumentException> {
            slot("slot-1", skillA, null).copy(status = SlotStatus.READY)
        }
        assertFailsWith<IllegalArgumentException> {
            slot("slot-1", skillA, null).copy(requiredForSessionClosure = true)
        }
        assertEquals(IndependenceMode.H0_REQUIRED, slot("slot-1", skillA, itemA).independenceMode)
    }

    @Test
    fun `a blueprint measures one Skill once and never repeats a family or a dependency group`() {
        assertFailsWith<IllegalArgumentException> { blueprint(slot("slot-1", skillA, itemA), slot("slot-2", skillA, itemB)) }
        assertFailsWith<IllegalArgumentException> {
            blueprint(slot("slot-1", skillA, itemA, family = "f1"), slot("slot-2", skillB, itemB, family = "f1"))
        }
        assertFailsWith<IllegalArgumentException> {
            blueprint(slot("slot-1", skillA, itemA, group = "g1"), slot("slot-2", skillB, itemB, group = "g1"))
        }
        val ok = blueprint(slot("slot-1", skillA, itemA, family = "f1"), slot("slot-2", skillB, null))
        assertEquals(12, ok.expectedActiveMinutes)
        assertEquals(listOf(BlueprintBlock("block-retention_due", BlueprintRole.RETENTION_DUE, listOf("slot-1"))), ok.blocks)
    }

    @Test
    fun `nothing in the blueprint or the result can hold a score`() {
        val forbidden = listOf("score", "grade", "percent", "pass", "threshold", "questionCount", "duration")
        listOf(AssessmentBlueprint::class, AssessmentBlueprintSlot::class, AssessmentBlueprintResult::class).forEach { type ->
            type.java.declaredFields.map { it.name }.forEach { field ->
                forbidden.forEach { word -> assertTrue(!field.contains(word, ignoreCase = true), "${type.simpleName}.$field") }
            }
        }
    }

    @Test
    fun `the codec reads back exactly what it wrote`() {
        val stored = blueprint(
            slot("slot-1", skillA, itemA, family = "fam/one", group = "g, 1").copy(
                reasonCodes = listOf("assessment.weekly.slot_retention_due"),
                rejections = listOf(SlotItemRejection(itemB, listOf("already_seen", "variant_family_in_use"))),
                itemRequiredSkills = listOf(skillB), requiredForSessionClosure = true,
            ),
            slot("slot-2", skillB, null),
        ).copy(recentSince = "2026-09-24", supersedesSessionId = 7)
        val text = WeeklyBlueprintCodec.encode(stored)
        assertEquals(WeeklyBlueprintCodec.FORMAT, text.lines().first())
        assertEquals(stored, WeeklyBlueprintCodec.decode(text))
    }

    @Test
    fun `the codec refuses rather than guesses`() {
        val text = WeeklyBlueprintCodec.encode(blueprint(slot("slot-1", skillA, itemA)))
        assertNotNull(WeeklyBlueprintCodec.decode(text))
        assertNull(WeeklyBlueprintCodec.decode(text.replaceFirst("weekly_blueprint/1", "weekly_blueprint/2")))
        assertNull(WeeklyBlueprintCodec.decode(text + "\nsurprise\tx=1"))
        assertNull(WeeklyBlueprintCodec.decode(text.replace("\trole=retention_due", "\trole=exam")))
        assertNull(WeeklyBlueprintCodec.decode(text.replace("\tstatus=ready", "")))
        assertNull(WeeklyBlueprintCodec.decode(text.replace("\tstatus=ready", "\tstatus=ready\tstatus=ready")))
        assertNull(WeeklyBlueprintCodec.decode(text + "\nrejection\tslot_id=slot-9\titem=item.x@v1\treasons=already_seen"))
        // A ready slot that lost its minutes is not a slot the store holds.
        assertNull(WeeklyBlueprintCodec.decode(text.replace("\tminutes=12", "\tminutes=")))
    }

    @Test
    fun `an unsubmitted slot has no attempt and no evidence`() {
        assertFailsWith<IllegalArgumentException> { BlueprintSlotOutcome("slot-1", submitted = false, attemptId = 4) }
    }

    @Test
    fun `scopes keep the interior's ids and the store's values`() {
        assertEquals(listOf("daily", "weekly", "monthly"), AssessmentScope.entries.map { it.storedAs })
    }
}
