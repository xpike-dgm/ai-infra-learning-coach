package coach.engines

import coach.model.EdgeKind
import coach.model.MasteryAxisState
import coach.model.PrerequisiteCandidate
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteEligibility
import coach.model.PrerequisiteReadiness
import coach.model.PrerequisiteReason
import coach.model.ReadinessInputs
import coach.model.ReadinessNote
import coach.model.RequirementSource
import coach.model.RetentionAxis
import coach.model.SkillReadiness
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `PRG-v0` as code. Its worked examples (§23) and its anti-patterns (§22) are tests here: each one
 * is a plausible way a learning product either locks a learner out for no reason or lets them fail
 * on something nobody taught them.
 */
class PrerequisiteEngineTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val linkedList = VersionedRef("skill.c.linked_list_insert", 1)
    private val filesystem = VersionedRef("skill.linux.filesystem_navigation", 1)
    private val shortcuts = VersionedRef("skill.linux.advanced_shell_shortcuts", 1)
    private val processes = VersionedRef("skill.linux.process_inspection", 1)
    private val malloc = VersionedRef("skill.c.dynamic_memory_basic", 1)
    private val english = VersionedRef("skill.english.present_simple", 1)
    private val memoryAddress = VersionedRef("skill.c.memory_address", 1)

    private fun edge(
        from: VersionedRef,
        to: VersionedRef,
        kind: String = "hard",
        lifecycle: String = "published",
        version: Int = 1,
        strictness: String = PrerequisiteEngine.DEFAULT_STRICTNESS_PROFILE,
    ) = PrerequisiteEdge(from, to, version, kind, "conceptual_dependency", strictness, lifecycle, "authored")

    private fun inputs(
        skill: VersionedRef,
        mastery: MasteryAxisState? = MasteryAxisState.CONFIRMED_CURRENT,
        retention: RetentionAxis = RetentionAxis.FRESH,
        remediation: Boolean? = false,
    ) = ReadinessInputs(skill, mastery, retention, remediation, snapshotRef = "skill_state:$skill")

    private fun ready(skill: VersionedRef, value: PrerequisiteReadiness, remediation: Boolean = false) =
        SkillReadiness(skill, value, remediation, emptyList(), "skill_state:$skill")

    private fun decide(
        target: VersionedRef,
        edges: List<PrerequisiteEdge>,
        readiness: Map<VersionedRef, SkillReadiness>,
        required: List<VersionedRef> = emptyList(),
        strict: Boolean = false,
        critical: Set<VersionedRef> = emptySet(),
        published: (VersionedRef) -> Boolean = { true },
    ) = PrerequisiteCandidate("candidate:$target", target, required, strict).let { candidate ->
        PrerequisiteEngine.decide(
            candidate,
            PrerequisiteEngine.requirements(candidate, edges, published),
            readiness,
        ) { it in critical }
    }

    @Test
    fun `readiness has four values and review_due is not not_ready`() {
        fun of(mastery: MasteryAxisState?, retention: RetentionAxis) =
            PrerequisiteEngine.readiness(inputs(pointer, mastery, retention)).readiness

        assertEquals(PrerequisiteReadiness.READY, of(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.FRESH))
        assertEquals(PrerequisiteReadiness.READY, of(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.STABLE))
        assertEquals(PrerequisiteReadiness.READY_DUE, of(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE))
        assertEquals(PrerequisiteReadiness.UNCERTAIN, of(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.VERIFICATION_DUE))
        assertEquals(PrerequisiteReadiness.UNCERTAIN, of(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.AT_RISK))
        assertEquals(PrerequisiteReadiness.UNCERTAIN, of(MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, RetentionAxis.FRESH))
        listOf(null, MasteryAxisState.NOT_YET_EVIDENCED, MasteryAxisState.DEVELOPING_WITH_SUPPORT,
            MasteryAxisState.DEVELOPING_INDEPENDENT).forEach { mastery ->
            assertEquals(PrerequisiteReadiness.NOT_READY, of(mastery, RetentionAxis.FRESH), "$mastery")
        }
    }

    @Test
    fun `open remediation makes a mastered prerequisite not ready, and says why`() {
        val readiness = PrerequisiteEngine.readiness(inputs(pointer, remediation = true))
        assertEquals(PrerequisiteReadiness.NOT_READY, readiness.readiness)
        assertTrue(readiness.remediationRequired)

        val decision = decide(linkedList, listOf(edge(pointer, linkedList)), mapOf(pointer to readiness))
        assertEquals(PrerequisiteEligibility.BLOCKED, decision.eligibility)
        assertEquals(listOf(PrerequisiteReason.BLOCKED_PREREQUISITE_REMEDIATION_REQUIRED), decision.reasonInputs)
    }

    @Test
    fun `a completed remediation without new evidence does not unblock`() {
        // Remediation closed, but mastery has not been re-established by fresh evidence.
        val readiness = PrerequisiteEngine.readiness(
            inputs(pointer, mastery = MasteryAxisState.DEVELOPING_INDEPENDENT, remediation = false)
        )
        assertEquals(PrerequisiteReadiness.NOT_READY, readiness.readiness)
    }

    @Test
    fun `an axis nobody has evaluated yet is named, never read as bad news`() {
        val readiness = PrerequisiteEngine.readiness(
            inputs(pointer, retention = RetentionAxis.NOT_YET_EVALUATED, remediation = null)
        )
        assertEquals(PrerequisiteReadiness.READY, readiness.readiness)
        assertEquals(
            listOf(ReadinessNote.RETENTION_NOT_YET_EVALUATED, ReadinessNote.REMEDIATION_NOT_YET_EVALUATED),
            readiness.notes,
        )
        val unknownMastery = PrerequisiteEngine.readiness(inputs(pointer, mastery = null))
        assertEquals(PrerequisiteReadiness.NOT_READY, unknownMastery.readiness)
        assertEquals(listOf(ReadinessNote.MASTERY_NOT_YET_EVALUATED), unknownMastery.notes)
    }

    @Test
    fun `a missing hard prerequisite blocks only the work that depends on it`() {
        // PRG-v0 §23 example A.
        val readiness = mapOf(
            pointer to ready(pointer, PrerequisiteReadiness.NOT_READY),
            memoryAddress to ready(memoryAddress, PrerequisiteReadiness.READY),
        )
        val dependent = decide(linkedList, listOf(edge(pointer, linkedList)), readiness)
        assertEquals(PrerequisiteEligibility.BLOCKED, dependent.eligibility)
        assertEquals(listOf(pointer), dependent.hardBlockerSkills)
        assertEquals(listOf(PrerequisiteReason.BLOCKED_MISSING_HARD_PREREQUISITE), dependent.reasonInputs)

        // The blocker's own teaching task is not blocked by what depends on it (§17).
        val repair = decide(pointer, listOf(edge(memoryAddress, pointer)), readiness)
        assertEquals(PrerequisiteEligibility.ELIGIBLE, repair.eligibility)

        // An independent branch and a parallel English track carry on.
        assertEquals(PrerequisiteEligibility.ELIGIBLE, decide(filesystem, emptyList(), readiness).eligibility)
        assertEquals(PrerequisiteEligibility.ELIGIBLE, decide(english, emptyList(), readiness).eligibility)
    }

    @Test
    fun `a critical prerequisite that is only review_due does not block`() {
        // PRG-v0 §23 example B.
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList)),
            mapOf(pointer to ready(pointer, PrerequisiteReadiness.READY_DUE)),
            critical = setOf(pointer),
        )
        assertEquals(PrerequisiteEligibility.ELIGIBLE, decision.eligibility)
        assertEquals(listOf(pointer), decision.reviewDueSkills)
        assertEquals(listOf(PrerequisiteReason.ELIGIBLE_PREREQUISITE_REVIEW_DUE_NOT_BLOCKING), decision.reasonInputs)
    }

    @Test
    fun `a critical prerequisite with verification due blocks new dependent work`() {
        // PRG-v0 §23 example C.
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList)),
            mapOf(pointer to ready(pointer, PrerequisiteReadiness.UNCERTAIN)),
            critical = setOf(pointer),
        )
        assertEquals(PrerequisiteEligibility.BLOCKED, decision.eligibility)
        assertEquals(listOf(pointer), decision.uncertainSkills)
        assertTrue(decision.hardBlockerSkills.isEmpty(), "an uncertain Skill is not a missing one")
        assertEquals(listOf(PrerequisiteReason.BLOCKED_CRITICAL_PREREQUISITE_VERIFICATION_DUE), decision.reasonInputs)
    }

    @Test
    fun `a normal hard prerequisite with verification due is conditionally eligible, not blocked`() {
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList)),
            mapOf(pointer to ready(pointer, PrerequisiteReadiness.UNCERTAIN)),
        )
        assertEquals(PrerequisiteEligibility.CONDITIONAL_ELIGIBLE, decision.eligibility)
        assertEquals(listOf(PrerequisiteReason.CONDITIONAL_PREREQUISITE_UNCERTAIN), decision.reasonInputs)
    }

    @Test
    fun `a candidate asking for strict confidence blocks on an uncertain prerequisite`() {
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList)),
            mapOf(pointer to ready(pointer, PrerequisiteReadiness.UNCERTAIN)),
            strict = true,
        )
        assertEquals(PrerequisiteEligibility.BLOCKED, decision.eligibility)
        assertTrue(decision.requiresStrictPrerequisiteConfidence)
    }

    @Test
    fun `a soft gap never blocks`() {
        // PRG-v0 §23 example D.
        listOf(PrerequisiteReadiness.NOT_READY, PrerequisiteReadiness.UNCERTAIN).forEach { value ->
            val decision = decide(
                processes, listOf(edge(shortcuts, processes, kind = "soft")),
                mapOf(shortcuts to ready(shortcuts, value)),
                strict = true, critical = setOf(shortcuts),
            )
            assertEquals(PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT, decision.eligibility, value.id)
            assertEquals(listOf(shortcuts), decision.softGapSkills)
            assertEquals(listOf(PrerequisiteReason.ELIGIBLE_WITH_SOFT_PREREQUISITE_GAP), decision.reasonInputs)
        }
    }

    @Test
    fun `a task-level requirement blocks even when the graph does not mention it`() {
        // PRG-v0 §6 and §23 example E: a pointer item that silently needs malloc.
        val readiness = mapOf(
            pointer to ready(pointer, PrerequisiteReadiness.READY),
            malloc to ready(malloc, PrerequisiteReadiness.NOT_READY),
        )
        val decision = decide(linkedList, listOf(edge(pointer, linkedList)), readiness, required = listOf(malloc))
        assertEquals(PrerequisiteEligibility.BLOCKED, decision.eligibility)
        assertEquals(listOf(malloc), decision.hardBlockerSkills)
        assertEquals(
            RequirementSource.TASK_REQUIRED,
            PrerequisiteEngine.requirements(
                PrerequisiteCandidate("c", linkedList, listOf(malloc)), emptyList()) { true }
                .requirements.single().source,
        )
    }

    @Test
    fun `a Skill needed both softly and by the task is needed hard`() {
        val decision = decide(
            processes, listOf(edge(shortcuts, processes, kind = "soft")),
            mapOf(shortcuts to ready(shortcuts, PrerequisiteReadiness.NOT_READY)),
            required = listOf(shortcuts),
        )
        assertEquals(PrerequisiteEligibility.BLOCKED, decision.eligibility)
        assertTrue(decision.softGapSkills.isEmpty())
    }

    @Test
    fun `English is never a hidden prerequisite`() {
        // PRG-v0 §16: every English Skill is not ready, and a technical target that declares no
        // English dependency is still eligible — the gate reads only what is declared.
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList)),
            mapOf(
                pointer to ready(pointer, PrerequisiteReadiness.READY),
                english to ready(english, PrerequisiteReadiness.NOT_READY),
            ),
        )
        assertEquals(PrerequisiteEligibility.ELIGIBLE, decision.eligibility)
        assertFalse(english in decision.hardBlockerSkills + decision.softGapSkills + decision.uncertainSkills)
    }

    @Test
    fun `a missing readiness fails closed`() {
        val decision = decide(linkedList, listOf(edge(pointer, linkedList)), readiness = emptyMap())
        assertEquals(PrerequisiteEligibility.BLOCKED, decision.eligibility)
        assertEquals(listOf(pointer), decision.hardBlockerSkills)
    }

    @Test
    fun `a draft edge is reported, never silently dropped`() {
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList, lifecycle = "draft")),
            mapOf(pointer to ready(pointer, PrerequisiteReadiness.NOT_READY)),
        )
        assertEquals(PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA, decision.eligibility)
        assertEquals(listOf("edge_not_published:$pointer"), decision.metadataProblems)
    }

    @Test
    fun `an invalidated or unknown edge lifecycle is a metadata problem`() {
        listOf("invalidated" to "edge_invalidated", "wip" to "unknown_edge_lifecycle").forEach { (lifecycle, problem) ->
            val decision = decide(
                linkedList, listOf(edge(pointer, linkedList, lifecycle = lifecycle)),
                mapOf(pointer to ready(pointer, PrerequisiteReadiness.READY)),
            )
            assertEquals(PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA, decision.eligibility, lifecycle)
            assertTrue(decision.metadataProblems.single().startsWith(problem), decision.metadataProblems.toString())
        }
    }

    @Test
    fun `a retired edge no longer gates and a deprecated one still does`() {
        val readiness = mapOf(pointer to ready(pointer, PrerequisiteReadiness.NOT_READY))
        assertEquals(
            PrerequisiteEligibility.ELIGIBLE,
            decide(linkedList, listOf(edge(pointer, linkedList, lifecycle = "retired")), readiness).eligibility,
        )
        assertEquals(
            PrerequisiteEligibility.BLOCKED,
            decide(linkedList, listOf(edge(pointer, linkedList, lifecycle = "deprecated")), readiness).eligibility,
        )
    }

    @Test
    fun `the newest edge version is the one in force`() {
        val readiness = mapOf(pointer to ready(pointer, PrerequisiteReadiness.NOT_READY))
        val softenedLater = listOf(
            edge(pointer, linkedList, kind = "hard", version = 1),
            edge(pointer, linkedList, kind = "soft", version = 2),
        )
        assertEquals(PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT, decide(linkedList, softenedLater, readiness).eligibility)
        assertEquals(
            PrerequisiteEligibility.BLOCKED,
            decide(linkedList, softenedLater.reversed().map {
                if (it.edgeVersion == 1) it.copy(edgeVersion = 3) else it
            }, readiness).eligibility,
        )
    }

    @Test
    fun `an unknown strictness profile is a metadata problem, not a guess`() {
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList, strictness = "lenient_v9")),
            mapOf(pointer to ready(pointer, PrerequisiteReadiness.READY)),
        )
        assertEquals(PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA, decision.eligibility)
    }

    @Test
    fun `an unpublished required Skill or target is a metadata problem`() {
        val decision = decide(
            linkedList, emptyList(), emptyMap(), required = listOf(malloc), published = { it != malloc },
        )
        assertEquals(PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA, decision.eligibility)
        assertEquals(listOf("unpublished_required_skill:$malloc"), decision.metadataProblems)

        val unpublishedTarget = decide(linkedList, emptyList(), emptyMap(), published = { it != linkedList })
        assertEquals(listOf("unpublished_target:$linkedList"), unpublishedTarget.metadataProblems)
    }

    @Test
    fun `a Skill that requires itself is a metadata problem`() {
        val decision = decide(linkedList, emptyList(), emptyMap(), required = listOf(linkedList))
        assertEquals(PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA, decision.eligibility)
    }

    @Test
    fun `a cycle is found, and the walk is bounded by the graph`() {
        val graph = mapOf(
            linkedList to listOf(edge(pointer, linkedList)),
            pointer to listOf(edge(memoryAddress, pointer)),
            memoryAddress to listOf(edge(linkedList, memoryAddress)),
        )
        val reads = mutableListOf<VersionedRef>()
        assertTrue(PrerequisiteEngine.onCycle(linkedList) { reads += it; graph[it].orEmpty() })
        assertTrue(reads.size <= graph.size + 1, "the walk read $reads")

        val acyclic = graph - memoryAddress
        assertFalse(PrerequisiteEngine.onCycle(linkedList) { acyclic[it].orEmpty() })

        // A diamond is not a cycle, and each Skill's edges are still read once.
        val diamond = mapOf(
            linkedList to listOf(edge(pointer, linkedList), edge(malloc, linkedList)),
            pointer to listOf(edge(memoryAddress, pointer)),
            malloc to listOf(edge(memoryAddress, malloc)),
        )
        val diamondReads = mutableListOf<VersionedRef>()
        assertFalse(PrerequisiteEngine.onCycle(linkedList) { diamondReads += it; diamond[it].orEmpty() })
        assertEquals(diamondReads.size, diamondReads.toSet().size, "read twice: $diamondReads")
    }

    @Test
    fun `a retired edge cannot close a cycle`() {
        val graph = mapOf(
            linkedList to listOf(edge(pointer, linkedList)),
            pointer to listOf(edge(linkedList, pointer, lifecycle = "retired")),
        )
        assertFalse(PrerequisiteEngine.onCycle(linkedList) { graph[it].orEmpty() })
    }

    @Test
    fun `the same inputs always give the same decision, whatever order they arrive in`() {
        val edges = listOf(edge(pointer, linkedList), edge(malloc, linkedList), edge(shortcuts, linkedList, kind = "soft"))
        val readiness = mapOf(
            pointer to ready(pointer, PrerequisiteReadiness.NOT_READY),
            malloc to ready(malloc, PrerequisiteReadiness.NOT_READY),
            shortcuts to ready(shortcuts, PrerequisiteReadiness.UNCERTAIN),
        )
        val first = decide(linkedList, edges, readiness)
        val second = decide(linkedList, edges.reversed(), readiness.entries.reversed().associate { it.key to it.value })
        assertEquals(first, second)
        assertEquals(listOf(malloc, pointer), first.hardBlockerSkills)

        // The graph's edges arrive sorted by the store, but a task's own requirements arrive in whatever
        // order the candidate lists them; the decision must not depend on that either.
        val taskFirst = decide(linkedList, emptyList(), readiness, required = listOf(pointer, malloc, shortcuts))
        val taskSecond = decide(linkedList, emptyList(), readiness, required = listOf(shortcuts, malloc, pointer))
        assertEquals(taskFirst.copy(candidateId = "x"), taskSecond.copy(candidateId = "x"))
        assertEquals(listOf(malloc, pointer), taskFirst.hardBlockerSkills)
    }

    @Test
    fun `the decision names the snapshots it was made from and the policy that made it`() {
        val decision = decide(
            linkedList, listOf(edge(pointer, linkedList)),
            mapOf(pointer to ready(pointer, PrerequisiteReadiness.READY)),
        )
        assertEquals(listOf("skill_state:$pointer"), decision.readinessSnapshotRefs)
        assertEquals("PRG-v0", decision.prerequisitePolicyVersion)
        assertEquals(EdgeKind.HARD, PrerequisiteEngine.requirements(
            PrerequisiteCandidate("c", linkedList), listOf(edge(pointer, linkedList))) { true }.requirements.single().kind)
    }
}
