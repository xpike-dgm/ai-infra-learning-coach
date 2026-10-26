package coach.application

import coach.model.AllowedToolsPolicy
import coach.model.AnswerResult
import coach.model.AnsweredItem
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.AssistanceLevel
import coach.model.AssistanceTiming
import coach.model.ContentOrigin
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatusRequirement
import coach.model.ExplanationForm
import coach.model.ExplanationVariant
import coach.model.IndependenceMode
import coach.model.InstructionMode
import coach.model.LifecycleStatus
import coach.model.RunnableTask
import coach.model.TaskPurpose
import coach.model.TutorIntent
import coach.model.UseCeiling
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** 16G (`HLPX-v0`): what one ask from inside a run may carry, before any rule has looked at it. */
class RunHelpTest {

    private val skill = VersionedRef("skill.python.read_input", 1)
    private val objective = VersionedRef("objective.python.read_input.read_line", 1)
    private val other = VersionedRef("objective.python.read_input.other", 1)

    private val run = RunnableTask(
        plannedTaskId = 7, position = 0, planVersionId = 3, task = VersionedRef("task.python.read_input.practice", 1), title = "Pratik",
        purpose = TaskPurpose.PRACTICE, skill = skill, targetObjectives = listOf(objective), plannedMinutes = 10, needKey = "continue_learning:$skill",
        lesson = listOf(
            ExplanationVariant(VersionedRef("explanation.python.read_input.canonical", 1), objective, ExplanationForm.CANONICAL, null, "Ders metni."),
            ExplanationVariant(VersionedRef("explanation.python.read_input.other", 1), other, ExplanationForm.CANONICAL, null, "Başka hedef."),
        ),
        items = listOf(VersionedRef("item.python.read_input.f01", 1)),
    )

    private val item = AssessmentItem(
        ref = VersionedRef("item.python.read_input.f01", 1), targetObjectives = listOf(objective), targetSkills = listOf(skill), requiredSkills = emptyList(),
        evidenceType = "code_execution", expectedAnswerOrRubricRef = "codetest.python.read_input@v1",
        evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, false, "policy.v1"),
        allowedTools = AllowedToolsPolicy(listOf("interpreter")), independenceMode = IndependenceMode.H0_REQUIRED, difficultyClass = "core",
        lifecycleStatus = LifecycleStatus.VALIDATED, contentOrigin = ContentOrigin.HUMAN_AUTHORED, declaredUseCeiling = UseCeiling.entries.first(),
        scopeEligibility = setOf(AssessmentScope.entries.first()), variantFamilyId = "family.python.read_input",
    )

    @Test
    fun `while the answer is open the ask is during the attempt, keeps the learner's level and carries no answer`() {
        val ask = RunHelp.askFor(run, item, "Bir satır oku.", TutorIntent.HINT, frozen = null, frozenWork = "taslak", ceiling = AssistanceLevel.H2)
        assertEquals(AssistanceTiming.DURING_ATTEMPT, ask.timing)
        assertEquals(AssistanceLevel.H2, ask.ceiling)
        assertNull(ask.context.learnerWork, "nothing the learner has not submitted leaves the device")
        assertEquals("Ders metni.", ask.context.canonicalExplanation, "only the course's explanation of this question's Objectives")
        assertEquals(InstructionMode.TURKISH_PRIMARY, ask.instructionMode)
        assertEquals(TaskPurpose.PRACTICE, ask.purpose)
        assertNull(RunHelp.askFor(run, item, "p", TutorIntent.HINT, null, null, question = "neden?").learnerQuestion, "only a question carries question text")
    }

    @Test
    fun `once the answer froze no level is asked, the frozen answer travels, and the timing says whether it matched`() {
        val right = AnsweredItem(item.ref, 1, AnswerResult.RIGHT, listOf(1))
        val after = RunHelp.askFor(run, item, "p", TutorIntent.QUESTION, right, "print(input())", ceiling = AssistanceLevel.H1, question = "neden?")
        assertEquals(AssistanceTiming.AFTER_SUBMIT, after.timing)
        assertNull(after.ceiling)
        assertEquals("print(input())", after.context.learnerWork)
        assertEquals("neden?", after.learnerQuestion)
        assertEquals(AssistanceTiming.AFTER_FAILURE, RunHelp.askFor(run, item, "p", TutorIntent.EXPLAIN_MISTAKE, right.copy(result = AnswerResult.PARTLY), "x").timing)
        assertEquals(AssistanceTiming.AFTER_FAILURE, RunHelp.askFor(run, item, "p", TutorIntent.EXPLAIN_MISTAKE, right.copy(result = AnswerResult.NOT_RIGHT), "x").timing)
    }
}
