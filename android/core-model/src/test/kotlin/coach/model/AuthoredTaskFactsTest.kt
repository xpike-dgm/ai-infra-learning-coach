package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** An authored task serves a need the planner opened, about its own Skill, and nothing else (15A, `CPFX-v0`). */
class AuthoredTaskFactsTest {

    private val skill = VersionedRef("skill.programming.state_assignment_model", 1)
    private val other = VersionedRef("skill.computing.program_execution_model", 1)
    private val objective = VersionedRef("objective.programming.state_assignment_model.trace_state", 1)

    private fun task(
        purpose: TaskPurpose = TaskPurpose.TEACH,
        serves: Set<NeedTrigger> = setOf(NeedTrigger.NEW_LEARNING),
        activity: String = "content_explanation",
        explanations: List<VersionedRef> = listOf(VersionedRef("explanation.programming.trace_state.canonical", 1)),
        items: List<VersionedRef> = emptyList(),
        required: List<VersionedRef> = emptyList(),
        id: String = "task.programming.state_assignment_model.teach",
    ) = AuthoredTask(
        ref = VersionedRef(id, 1),
        title = "Değişken ve atama",
        primarySkill = skill,
        targetObjectives = listOf(objective),
        purpose = purpose,
        activityKind = activity,
        serves = serves,
        costMinutes = 12,
        validationStatus = LifecycleStatus.VALIDATED,
        contentOrigin = ContentOrigin.AI_GENERATED,
        requiredSkills = required,
        explanations = explanations,
        items = items,
    )

    private fun need(trigger: NeedTrigger, about: VersionedRef = skill) =
        LearningNeed("${trigger.id}:$about", trigger, listOf(about), Criticality.REQUIRED)

    @Test
    fun `a task serves only a need it declares, and only about its own Skill`() {
        val teach = task()
        val candidate = assertNotNull(teach.candidateFor(need(NeedTrigger.NEW_LEARNING)))
        assertEquals("new_learning:$skill", candidate.needKey)
        assertEquals(TaskPurpose.TEACH, candidate.purpose)
        assertEquals(listOf(objective), candidate.targetObjectives)
        assertEquals(12, candidate.costMinutes)
        assertNull(teach.candidateFor(need(NeedTrigger.CONTINUE_LEARNING)), "a lesson does not answer a need it was not written for")
        assertNull(teach.candidateFor(need(NeedTrigger.NEW_LEARNING, other)), "content never answers a need about another Skill")
    }

    @Test
    fun `one task offered to two needs is two candidates`() {
        val repair = task(TaskPurpose.REMEDIATE, setOf(NeedTrigger.REMEDIATION_REQUIRED, NeedTrigger.WEAKNESS_DETECTED),
            id = "task.programming.state_assignment_model.repair")
        val a = assertNotNull(repair.candidateFor(need(NeedTrigger.REMEDIATION_REQUIRED)))
        val b = assertNotNull(repair.candidateFor(need(NeedTrigger.WEAKNESS_DETECTED)))
        assertTrue(a.id != b.id)
    }

    @Test
    fun `a purpose serves only the needs the accepted contracts give it`() {
        assertEquals(setOf(NeedTrigger.NEW_LEARNING), TaskServing.servedBy(TaskPurpose.TEACH))
        assertEquals(setOf(NeedTrigger.VERIFICATION_DUE), TaskServing.servedBy(TaskPurpose.ASSESS))
        assertEquals(setOf(NeedTrigger.RETENTION_REVIEW_DUE), TaskServing.servedBy(TaskPurpose.RETAIN))
        assertTrue(TaskServing.servedBy(TaskPurpose.DIAGNOSE).isEmpty(), "a diagnostic is composed from the open diagnostic, never authored")
        assertFailsWith<IllegalArgumentException>("a lesson cannot claim to verify") {
            task(serves = setOf(NeedTrigger.NEW_LEARNING, NeedTrigger.VERIFICATION_DUE))
        }
        assertFailsWith<IllegalArgumentException> { task(TaskPurpose.DIAGNOSE, setOf(NeedTrigger.DIAGNOSTIC_OPPORTUNITY)) }
    }

    @Test
    fun `a task is a real piece of content`() {
        assertFailsWith<IllegalArgumentException> { task(activity = "quiz") }
        assertFailsWith<IllegalArgumentException> { task(explanations = emptyList(), items = emptyList()) }
        assertFailsWith<IllegalArgumentException> { task(id = "lesson.state") }
        assertFailsWith<IllegalArgumentException> { task(required = listOf(skill)) }
        assertEquals(12, TaskServing.ACTIVITY_KINDS.size)
    }
}
