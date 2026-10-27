package coach.presentation

import coach.model.AttentionQualifier
import coach.model.EvaluatorAvailability
import coach.model.EvidenceKind
import coach.model.EvidenceSummary
import coach.model.MasteryAxisState
import coach.model.NamedEntity
import coach.model.ObjectiveProgress
import coach.model.PrerequisiteContext
import coach.model.PrerequisiteEligibility
import coach.model.ProgressFacts
import coach.model.RecoveryReason
import coach.model.RetentionAxis
import coach.model.SkillAxisReading
import coach.model.SkillDetailFacts
import coach.model.SkillPresentationProjection
import coach.model.SkillPresentationState
import coach.model.SkillProgressRow
import coach.model.StoreStatus
import coach.model.UnevaluatedAxis
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import coach.model.WeaknessSignal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 16A (`SKAX-v0`): Progress counts and never scores, says every state in words, and takes every Skill state from the
 * one derivation — so the overview and `skill_detail` cannot disagree about a Skill.
 */
class ProgressPresentationTest {

    private val ready = AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE)

    private fun row(
        id: String,
        mastery: MasteryAxisState? = MasteryAxisState.NOT_YET_EVIDENCED,
        retention: RetentionAxis = RetentionAxis.UNTRACKED,
        weakness: WeaknessAxis = WeaknessAxis.NONE,
        prerequisite: PrerequisiteEligibility = PrerequisiteEligibility.ELIGIBLE,
        evidenced: Boolean = true,
        stale: Boolean = false,
        blockers: List<VersionedRef> = emptyList(),
    ): SkillProgressRow {
        val reading = SkillAxisReading(mastery, retention, weakness, prerequisite, evidenced, stale)
        return SkillProgressRow(
            skill = VersionedRef(id, 1),
            name = id.substringAfterLast('.'),
            projection = SkillPresentationProjection.derive(reading),
            prerequisite = PrerequisiteContext(prerequisite, blockers, emptyList(), emptyList(), emptyList()),
        )
    }

    private fun facts(vararg rows: SkillProgressRow) = ProgressFacts(curriculumLoaded = true, skills = rows.toList())

    @Test
    fun `the eight labels are SPWX-v0's, locked`() {
        assertEquals(
            listOf("Pekiştirme Gerekli", "Doğrulanmış · Yeniden Kontrol Gerekli", "Doğrulanmış · Tekrar Zamanı", "Doğrulanmış",
                "Ön Koşul Bekliyor", "Bağımsız Gelişiyor", "Destekle Gelişiyor", "Henüz Kanıt Yok"),
            SkillPresentationState.precedence.map(ProgressCopy.stateLabels::getValue),
        )
    }

    @Test
    fun `there are exactly ten Progress states with their declared tones`() {
        assertEquals(10, ProgressState.entries.size)
        ProgressState.entries.forEach { state ->
            val expected = when (state) {
                ProgressState.PARTIAL_PROJECTION_AVAILABLE -> Tone.PENDING_UNRESOLVED
                ProgressState.ERROR_RECOVERABLE, ProgressState.DATA_RECOVERY_REQUIRED -> Tone.SYSTEM_FAULT
                else -> Tone.NEUTRAL
            }
            assertEquals(expected, state.tone, state.id)
            assertTrue(ProgressCopy.stateTexts.getValue(state).isNotBlank())
        }
    }

    /** Every string this layer can show. */
    private fun allCopy(): List<String> = listOf(
        ProgressCopy.stateLabels.values, ProgressCopy.qualifierLabels.values, ProgressCopy.attentionLabels.values,
        ProgressCopy.stateTexts.values, ProgressCopy.axisTitles.values, ProgressCopy.masteryLabels.values,
        ProgressCopy.retentionLabels.values, ProgressCopy.prerequisiteLabels.values, ProgressCopy.weaknessLabels.values,
        ProgressCopy.objectiveWeaknessLabels.values, ProgressCopy.evidenceLabels.values, ProgressCopy.trackLabels.values,
    ).flatten() + listOf(
        ProgressCopy.RECOMPUTING, ProgressCopy.INVENTORY_TITLE, ProgressCopy.INVENTORY_NOTE, ProgressCopy.ATTENTION_TITLE,
        ProgressCopy.ATTENTION_EMPTY, ProgressCopy.NO_CURRICULUM, ProgressCopy.NOT_EVALUATED, ProgressCopy.OBJECTIVE_SHOWN,
        ProgressCopy.OBJECTIVE_NOT_SHOWN, ProgressCopy.OBJECTIVE_REQUIRED, ProgressCopy.BLOCKED_BY, ProgressCopy.UNCERTAIN,
        ProgressCopy.SOFT_GAPS, ProgressCopy.METADATA, ProgressCopy.PREREQUISITES_CLEAR,
    )

    @Test
    fun `no string scores, ranks, levels, streaks or completes`() {
        val forbidden = listOf("%", "puan", "skor", "seviye", "level", "rank", "sıra", "seri", "streak", "tamamla", "başarısız",
            "unut", "geride", "borç")
        allCopy().forEach { text ->
            val lower = text.lowercase(java.util.Locale.ROOT)
            forbidden.forEach { word -> assertFalse(word in lower, "'$text' says '$word'") }
            assertFalse(Regex("""\d+\s*/\s*\d+""").containsMatchIn(text), "'$text' is a ratio")
        }
    }

    @Test
    fun `no projected type holds a total, ratio or percentage to divide by`() {
        val types = listOf(ProgressOverview::class, InventoryLine::class, AttentionLine::class, SkillChip::class, SkillGroup::class,
            SkillDetailView::class, AxisLine::class, EvidenceLine::class, ObjectiveLine::class, PrerequisiteLine::class)
        types.forEach { type ->
            // Backing fields and getters both: a computed property has no field but still a getter.
            val members = type.java.declaredFields.map { it.name to it.type } +
                type.java.declaredMethods.filter { it.name.startsWith("get") && it.parameterCount == 0 }.map { it.name to it.returnType }
            members.forEach { (member, returns) ->
                val name = member.lowercase(java.util.Locale.ROOT)
                listOf("total", "ratio", "percent", "score", "level", "rank", "progress", "streak").forEach { word ->
                    assertFalse(word in name, "${type.simpleName}.$member")
                }
                assertFalse(returns == Double::class.javaPrimitiveType || returns == Float::class.javaPrimitiveType ||
                    returns == java.lang.Double::class.java || returns == java.lang.Float::class.java, "${type.simpleName}.$member: $returns")
            }
        }
    }

    @Test
    fun `a learner with no evidence starts empty, and a waiting Skill is said to wait`() {
        val view = ProgressPresentation.overview(facts(
            row("skill.python.basics", mastery = null, evidenced = false),
            row("skill.python.loops", mastery = null, evidenced = false, prerequisite = PrerequisiteEligibility.BLOCKED),
        ), ready)
        assertEquals(ProgressState.EMPTY_NO_EVIDENCE_YET, view.state)
        assertEquals(listOf(SkillPresentationState.PREREQUISITE_UNRESOLVED to 1, SkillPresentationState.NOT_YET_EVIDENCED to 1),
            view.inventory.map { it.state to it.count })
        assertEquals(listOf(AttentionGroup.PREREQUISITE_BLOCKED), view.attention.map { it.group })
        assertEquals(listOf("python"), view.groups.map { it.track })
    }

    @Test
    fun `no evidence and nothing waiting is still the start, never nothing needs attention`() {
        val view = ProgressPresentation.overview(facts(row("skill.python.basics", mastery = null, evidenced = false)), ready)
        assertEquals(ProgressState.EMPTY_NO_EVIDENCE_YET, view.state)
        assertTrue(view.attention.isEmpty())
    }

    @Test
    fun `attention is grouped by reason and review due is only an opportunity`() {
        val view = ProgressPresentation.overview(facts(
            row("skill.c.a", mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.REVIEW_DUE),
            row("skill.c.b", mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.AT_RISK),
            row("skill.c.c", mastery = MasteryAxisState.CONFIRMATION_VERIFICATION_DUE),
            row("skill.c.d", mastery = MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = WeaknessAxis.REMEDIATION_REQUIRED),
        ), ready)
        assertEquals(ProgressState.READY_WITH_EVIDENCE, view.state)
        assertEquals(
            listOf(AttentionGroup.REMEDIATION_OPEN, AttentionGroup.VERIFICATION_DUE, AttentionGroup.REVIEW_DUE, AttentionGroup.RETENTION_AT_RISK),
            view.attention.map { it.group },
        )
        val reviewChip = view.groups.single().skills.single { it.skill.logicalId == "skill.c.a" }
        assertEquals(Tone.NEUTRAL, reviewChip.tone)
        val atRiskChip = view.groups.single().skills.single { it.skill.logicalId == "skill.c.b" }
        assertEquals("Doğrulanmış", atRiskChip.label)
        assertEquals(listOf(ProgressCopy.qualifierLabels.getValue(AttentionQualifier.RETENTION_AT_RISK)), atRiskChip.qualifiers)
    }

    @Test
    fun `evidence with nothing needing attention never claims mastery`() {
        val view = ProgressPresentation.overview(facts(row("skill.c.a", mastery = MasteryAxisState.DEVELOPING_INDEPENDENT)), ready)
        assertEquals(ProgressState.EMPTY_NO_ATTENTION_NEEDED, view.state)
        assertTrue("öğrenildiği anlamına gelmez" in view.text)
    }

    @Test
    fun `a recomputing Skill is counted apart and has no state`() {
        val partial = ProgressPresentation.overview(facts(
            row("skill.c.a", mastery = MasteryAxisState.DEVELOPING_INDEPENDENT),
            row("skill.c.b", mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.STABLE, stale = true),
        ), ready)
        assertEquals(ProgressState.PARTIAL_PROJECTION_AVAILABLE, partial.state)
        assertEquals(1, partial.recomputing)
        assertEquals(1, partial.inventory.sumOf { it.count })
        val chip = partial.groups.single().skills.single { it.skill.logicalId == "skill.c.b" }
        assertNull(chip.state)
        assertEquals(ProgressCopy.RECOMPUTING, chip.label)
        val all = ProgressPresentation.overview(facts(row("skill.c.b", mastery = null)), ready)
        assertEquals(ProgressState.RECOMPUTING_PROJECTION, all.state)
    }

    @Test
    fun `a blocking health state and an unread store show nothing that is not true`() {
        val recovery = AppHealth.of(StoreStatus.RecoveryRequired(RecoveryReason.entries.first()), EvaluatorAvailability.AVAILABLE)
        assertEquals(ProgressState.DATA_RECOVERY_REQUIRED, ProgressPresentation.overview(facts(row("skill.c.a")), recovery).state)
        assertEquals(ProgressState.ERROR_RECOVERABLE,
            ProgressPresentation.overview(null, AppHealth.of(StoreStatus.RecoverableFailure, EvaluatorAvailability.AVAILABLE)).state)
        val loading = ProgressPresentation.overview(null, ready)
        assertEquals(ProgressState.LOADING_PROJECTION, loading.state)
        assertTrue(loading.inventory.isEmpty() && loading.groups.isEmpty())
        val noCurriculum = ProgressPresentation.overview(ProgressFacts(curriculumLoaded = false, skills = emptyList()), ready)
        assertEquals(ProgressCopy.NO_CURRICULUM, noCurriculum.text)
        val noAi = ProgressPresentation.overview(facts(row("skill.c.a")), AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.UNAVAILABLE))
        assertEquals(listOf(ProgressState.AI_UNAVAILABLE_FULL_STATE_AVAILABLE), noAi.contexts)
    }

    private fun detailFacts(r: SkillProgressRow, objectives: List<ObjectiveProgress> = emptyList()) = SkillDetailFacts(
        row = r, capabilityStatement = "statement", objectives = objectives,
        evidence = EvidenceSummary(EvidenceKind.entries.associateWith { 0 }),
        skillNames = r.prerequisite.hardBlockers.associateWith { "Basics" }, topics = listOf(NamedEntity("topic.c.x", 1, "Loops")),
    )

    @Test
    fun `skill detail keeps all four axes and the very chip the overview shows`() {
        val r = row("skill.c.a", mastery = MasteryAxisState.CONFIRMED_CURRENT, retention = RetentionAxis.REVIEW_DUE)
        val view = ProgressPresentation.detail(detailFacts(r))
        assertEquals(UnevaluatedAxis.entries.toSet(), view.axes.map { it.axis }.toSet())
        assertEquals(4, view.axes.size)
        assertEquals(ProgressPresentation.overview(facts(r), ready).groups.single().skills.single(), view.chip)
        assertEquals("Tekrar zamanı", view.axes.single { it.axis == UnevaluatedAxis.RETENTION }.value)
        assertEquals(listOf("Loops"), view.topics)
    }

    @Test
    fun `a blocker is named, a hypothesis is a question, and evidence kinds stay apart`() {
        val basics = VersionedRef("skill.c.basics", 1)
        val r = row("skill.c.loops", mastery = null, evidenced = false, prerequisite = PrerequisiteEligibility.BLOCKED, blockers = listOf(basics))
        val objective = ObjectiveProgress(VersionedRef("objective.c.loops.trace_loop", 1), true, false, WeaknessSignal.HYPOTHESIS,
            EvidenceSummary(mapOf(EvidenceKind.INDEPENDENT to 1, EvidenceKind.ASSISTED to 2, EvidenceKind.PROVISIONAL to 0, EvidenceKind.NOT_COUNTED to 1)))
        val view = ProgressPresentation.detail(detailFacts(r, listOf(objective)))
        assertEquals(listOf(basics to "Basics"), view.prerequisite.single().skills)
        assertEquals(ProgressCopy.BLOCKED_BY, view.prerequisite.single().title)
        val line = view.objectives.single()
        // Narrowed at 17A: an Objective with no lesson title is named by its Skill, never by its identifier.
        assertEquals("loops", line.label)
        assertEquals("Açık bir soru var", line.weakness)
        assertEquals(listOf(EvidenceKind.INDEPENDENT, EvidenceKind.ASSISTED, EvidenceKind.PROVISIONAL, EvidenceKind.NOT_COUNTED),
            line.evidence.map { it.kind })
        assertEquals(listOf(1, 2, 0, 1), line.evidence.map { it.count })
        // No evidence and nothing written: no weakness is "none", not "not evaluated".
        assertEquals("Yok", view.axes.single { it.axis == UnevaluatedAxis.WEAKNESS }.value)
    }

    @Test
    fun `a Skill an assessment session could measure now is an assessment opportunity, and nothing else is one`() {
        val measured = row("skill.python.read_input", mastery = MasteryAxisState.DEVELOPING_INDEPENDENT)
        val other = row("skill.python.loops", mastery = MasteryAxisState.DEVELOPING_INDEPENDENT)
        val none = ProgressPresentation.overview(facts(measured, other), ready)
        assertTrue(none.attention.none { it.group == AttentionGroup.ASSESSMENT_OPPORTUNITY }, "16H: nothing is an opportunity without a session to measure it")
        val view = ProgressPresentation.overview(facts(measured, other).copy(assessmentOpportunities = setOf(measured.skill)), ready)
        assertEquals(listOf(measured.skill), view.attention.single { it.group == AttentionGroup.ASSESSMENT_OPPORTUNITY }.skills)
    }
}

