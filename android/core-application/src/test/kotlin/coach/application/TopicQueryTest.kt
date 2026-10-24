package coach.application

import coach.model.AcceptedAnswers
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BandCompletion
import coach.model.BaseProfileStatus
import coach.model.CefrBand
import coach.model.CodeTestSuite
import coach.model.ComprehensionCheck
import coach.model.CurriculumPackage
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExplanationVariant
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.LearningNeed
import coach.model.MisconceptionRow
import coach.model.NamedEntity
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.Rubric
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.TechnicalEnglishAnchors
import coach.model.TopicCoverage
import coach.model.TopicProjection
import coach.model.TopicSkillLink
import coach.model.TopicState
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.WeaknessSignal
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 16C (`PWPX-v0`): Topics, the Technical English profile and the weakness context are read from the very Skill rows
 * Progress shows, from recorded attempts and from the stored waivers — and nothing is written.
 */
class TopicQueryTest {

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private class Store(val skills: List<SkillRow>, val objectives: List<ObjectiveRow>, val edges: List<PrerequisiteEdge>) : PersistencePort {
        val evidence = mutableListOf<EvidenceRow>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val attempts = mutableSetOf<VersionedRef>()
        val catalog = mutableMapOf<VersionedRef, List<MisconceptionRow>>()
        var writes = 0

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long = error("16C writes no truth")
        override fun readTruth(kind: String, id: Long): TruthRecord? =
            evidence.firstOrNull { it.id == id }?.let { TruthRecord(kind, StudyTimestamp(0, it.studyDay!!, 0), emptyMap()) }
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) { writes++; projections[record.key] = record }
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence.filter { it.objective == objective }
        override fun truthWatermark(): Long = evidence.maxOfOrNull { it.sequence } ?: 0L
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = edges.filter { it.target == target }
        override fun publishedSkills(): List<SkillRow> = skills
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = evidence.map { it.skill }.distinct()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = objectives.filter { it.parentSkill == skill }
        override fun misconceptionsOf(objective: VersionedRef): List<MisconceptionRow> = catalog[objective].orEmpty()
        override fun attemptedResources(resources: List<VersionedRef>): Set<VersionedRef> = resources.filter { it in attempts }.toSet()
    }

    /** Topics and their links as packages carry them; one teach item per Objective unless [noLesson] names it. */
    private class Content(
        val topics: List<NamedEntity>,
        val links: List<TopicSkillLink>,
        val noLesson: Set<VersionedRef> = emptySet(),
    ) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? =
            CurriculumPackage(version = 1, sourceRefs = "src", provenance = "test", topics = topics, topicSkillLinks = links)
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
        override fun explanationsFor(objective: VersionedRef): List<ExplanationVariant> = emptyList()
        override fun codeTestsFor(item: VersionedRef): CodeTestSuite? = null
        override fun comprehensionChecksFor(item: VersionedRef): List<ComprehensionCheck> = emptyList()
        override fun answerKeyFor(item: VersionedRef): AcceptedAnswers? = null
        override fun rubricFor(item: VersionedRef): Rubric? = null
        override fun lessonItemsFor(objective: VersionedRef): List<VersionedRef> =
            if (objective in noLesson) emptyList() else listOf(lessonItem(objective))
    }

    private companion object {
        fun lessonItem(objective: VersionedRef) = VersionedRef("item.${objective.logicalId.removePrefix("objective.")}.lesson", 1)
    }

    private val basics = VersionedRef("skill.python.basics", 1)
    private val loops = VersionedRef("skill.python.loops", 1)
    private val files = VersionedRef("skill.python.files", 1)
    private val basicsObjective = VersionedRef("objective.python.basics.write", 1)
    private val loopsObjective = VersionedRef("objective.python.loops.trace", 1)
    private val filesObjective = VersionedRef("objective.python.files.read", 1)
    private val core = VersionedRef("topic.python.core", 1)
    private val io = VersionedRef("topic.python.io", 1)

    private fun skillRow(ref: VersionedRef, name: String) =
        SkillRow(ref, name, "statement of $name", "published", "concept", "standard", false, "src", "authored")

    private fun objective(ref: VersionedRef, skill: VersionedRef) =
        ObjectiveRow(ref, skill, true, "standard", listOf("code_writing"), listOf("code_writing"), null)

    private fun edge(source: VersionedRef, target: VersionedRef) =
        PrerequisiteEdge(source, target, 1, "hard", "conceptual_dependency", "default_prg_v0", "published", "authored")

    /** basics → loops inside `core`; loops → files from `core` into `io`. */
    private fun world(noLesson: Set<VersionedRef> = emptySet()): Triple<Store, Content, RecomputeSkillState> {
        val store = Store(
            skills = listOf(skillRow(basics, "Basics"), skillRow(loops, "Loops"), skillRow(files, "Files")),
            objectives = listOf(objective(basicsObjective, basics), objective(loopsObjective, loops), objective(filesObjective, files)),
            edges = listOf(edge(basics, loops), edge(loops, files)),
        )
        val content = Content(
            topics = listOf(NamedEntity(core.logicalId, 1, "Core"), NamedEntity(io.logicalId, 1, "IO")),
            links = listOf(TopicSkillLink(core, basics), TopicSkillLink(core, loops), TopicSkillLink(io, files)),
            noLesson = noLesson,
        )
        return Triple(store, content, RecomputeSkillState(store, Clock("2026-10-01")))
    }

    private var nextEvidence = 1L

    private fun Store.record(skill: VersionedRef, objective: VersionedRef, outcome: EvidenceOutcome, day: String) {
        val id = nextEvidence++
        evidence += EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_writing",
            outcome = outcome, evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = IndependenceClass.INDEPENDENT,
            contested = false, quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null,
            variantFamilyId = "family.$id", resource = VersionedRef("item.$id", 1), studyDay = day)
    }

    private fun Store.confirm(skill: VersionedRef, objective: VersionedRef, recompute: RecomputeSkillState) {
        record(skill, objective, EvidenceOutcome.POSITIVE, "2026-09-30"); recompute.recompute(skill)
        record(skill, objective, EvidenceOutcome.POSITIVE, "2026-10-01"); recompute.recompute(skill)
    }

    private fun TopicQuery.of(topic: VersionedRef) = load().topics.single { it.topic == topic }.projection

    private fun stateOf(p: TopicProjection) = assertIs<TopicProjection.Derived>(p).state

    @Test
    fun `a new learner's Topic with no outside prerequisite is available, and one that needs another Topic's Skill is locked`() {
        val (store, content, _) = world()
        val query = TopicQuery(store, content)
        assertEquals(TopicState.AVAILABLE, stateOf(query.of(core)))
        val io = assertIs<TopicProjection.Derived>(query.of(io))
        assertEquals(TopicState.LOCKED, io.state)
        assertEquals(listOf(loops), io.waitingFor)
        // Reading writes nothing.
        assertEquals(0, store.writes)
    }

    @Test
    fun `an attempt on a lesson item starts the Topic and covers that Objective, even before any evidence`() {
        val (store, content, _) = world()
        store.attempts += lessonItem(basicsObjective)
        val p = assertIs<TopicProjection.Derived>(TopicQuery(store, content).of(core))
        assertEquals(TopicState.LEARNING, p.state)
        val coverage = p.reading.members.flatMap { it.objectives }.toMap()
        assertEquals(TopicCoverage.LESSON_WORKED, coverage[basicsObjective])
        assertEquals(TopicCoverage.NOT_COVERED, coverage[loopsObjective])
    }

    @Test
    fun `a started Topic is not locked again, though its outside prerequisite still waits`() {
        val (store, content, _) = world()
        store.attempts += lessonItem(filesObjective)
        assertEquals(TopicState.LEARNING, stateOf(TopicQuery(store, content).of(io)))
    }

    @Test
    fun `every Skill confirmed is mastered only once every lesson was worked`() {
        val (store, content, recompute) = world()
        store.confirm(basics, basicsObjective, recompute)
        store.confirm(loops, loopsObjective, recompute)
        val query = TopicQuery(store, content)
        val learning = assertIs<TopicProjection.Derived>(query.of(core))
        assertEquals(TopicState.LEARNING, learning.state)
        assertEquals(listOf(basicsObjective, loopsObjective), learning.notCovered)
        store.attempts += listOf(lessonItem(basicsObjective), lessonItem(loopsObjective))
        assertEquals(TopicState.MASTERED, stateOf(query.of(core)))
        // `core` is mastered and `io`'s entry is open now: its outside prerequisite is confirmed.
        assertEquals(TopicState.AVAILABLE, stateOf(query.of(io)))
    }

    @Test
    fun `a stored diagnostic waiver covers its Objective without a lesson attempt, and marks the confirmation's source`() {
        val (store, content, recompute) = world()
        store.confirm(basics, basicsObjective, recompute)
        store.confirm(loops, loopsObjective, recompute)
        store.attempts += lessonItem(basicsObjective)
        store.projections[CoverageRows.key(loopsObjective)] = ProjectionRecord(CoverageRows.key(loopsObjective), "VDW-v0", 0, 0, 1, mapOf(
            "skill_logical_id" to loops.logicalId, "skill_version" to "1", "waiver" to CoverageRows.ACTIVE,
            "waiver_session_id" to "7", "waiver_source_evidence_ids" to "3,4", "waiver_granted_at_sequence" to "4",
            "waiver_granted_on_study_day" to "2026-10-01",
        ))
        val p = assertIs<TopicProjection.Derived>(TopicQuery(store, content).of(core))
        assertEquals(TopicCoverage.DIAGNOSTIC_WAIVER, p.reading.members.flatMap { it.objectives }.toMap()[loopsObjective])
        assertEquals(TopicState.MASTERED, p.state)
    }

    @Test
    fun `an Objective with no authored lesson is never covered`() {
        val (store, content, recompute) = world(noLesson = setOf(loopsObjective))
        store.confirm(basics, basicsObjective, recompute)
        store.confirm(loops, loopsObjective, recompute)
        store.attempts += lessonItem(basicsObjective)
        val p = assertIs<TopicProjection.Derived>(TopicQuery(store, content).of(core))
        assertEquals(TopicState.LEARNING, p.state)
        assertEquals(TopicCoverage.NO_LESSON_AUTHORED, p.reading.members.flatMap { it.objectives }.toMap()[loopsObjective])
    }

    @Test
    fun `a contradiction after mastery is weakening, and a row older than new evidence is recomputing`() {
        val (store, content, recompute) = world()
        store.confirm(basics, basicsObjective, recompute)
        store.confirm(loops, loopsObjective, recompute)
        store.attempts += listOf(lessonItem(basicsObjective), lessonItem(loopsObjective))
        store.record(loops, loopsObjective, EvidenceOutcome.NEGATIVE, "2026-10-02")
        val query = TopicQuery(store, content)
        assertEquals(listOf(loops), assertIs<TopicProjection.Recomputing>(query.of(core)).skills)
        recompute.recompute(loops)
        val p = assertIs<TopicProjection.Derived>(query.of(core))
        assertEquals(TopicState.WEAKENING, p.state)
        assertEquals(listOf(loops), p.retentionSignal)
    }

    @Test
    fun `topic detail names its Skills and what its entry waits for`() {
        val (store, content, _) = world()
        val detail = TopicQuery(store, content).detail(io)!!
        assertEquals(listOf(files), detail.members.map { it.skill })
        assertEquals("Loops", detail.skillNames[loops])
        assertNull(TopicQuery(store, content).detail(VersionedRef("topic.none", 1)))
    }

    @Test
    fun `skill detail carries the catalogued misconceptions the memory holds a signal for, and nothing it does not`() {
        val (store, content, _) = world()
        val shown = VersionedRef("misconception.python.basics.off_by_one", 1)
        val silent = VersionedRef("misconception.python.basics.shadowing", 1)
        store.catalog[basicsObjective] = listOf(
            MisconceptionRow(shown, basicsObjective, "Off by one", "Döngü bir eksik mi dönüyor?"),
            MisconceptionRow(silent, basicsObjective, "Shadowing", "Ad gölgelendi mi?"),
        )
        store.projections["misconception_state:${shown.logicalId}@v1"] =
            ProjectionRecord("misconception_state:${shown.logicalId}@v1", "p", 0, 0, 1, mapOf("state" to "hypothesis"))
        store.projections["misconception_state:${silent.logicalId}@v1"] =
            ProjectionRecord("misconception_state:${silent.logicalId}@v1", "p", 0, 0, 1, mapOf("state" to "none"))
        val detail = ProgressQuery(store, content).detail(basics)!!
        val m = detail.objectives.single().misconceptions.single()
        assertEquals(shown, m.misconception)
        assertEquals(WeaknessSignal.HYPOTHESIS, m.signal)
        assertNull(detail.englishBand)
    }

    // ------------------------------------------------------------------ Technical English profile

    private val a1 = TechnicalEnglishAnchors.anchors.filter { it.second == CefrBand.A1 }.map { VersionedRef(it.first, 1) }

    private fun englishWorld(): Pair<Store, RecomputeSkillState> {
        val objectives = a1.map { skill -> objective(VersionedRef("objective.${skill.logicalId.removePrefix("skill.")}.do", 1), skill) }
        val store = Store(a1.map { skillRow(it, it.logicalId.substringAfterLast('.')) }, objectives, emptyList())
        return store to RecomputeSkillState(store, Clock("2026-10-01"))
    }

    private fun Store.objectiveOf(skill: VersionedRef) = objectives.single { it.parentSkill == skill }.ref

    private val noTopics = Content(emptyList(), emptyList())

    @Test
    fun `the profile reads the English Skills' own states, and an unpublished band is named, never a gap`() {
        val (store, _) = englishWorld()
        val p = EnglishProfileQuery(store, noTopics).load()!!
        assertEquals(BaseProfileStatus.NOT_YET_COMPLETE, p.status)
        assertEquals(listOf(BandCompletion.INCOMPLETE, BandCompletion.NOT_PUBLISHED, BandCompletion.NOT_PUBLISHED), p.bands.map { it.completion })
        assertNull(p.historicalHighestBand)
        assertEquals(0, store.writes)
    }

    @Test
    fun `every A1 Skill confirmed is the A1 base, and the replay keeps the band after a clean recheck fails`() {
        val (store, recompute) = englishWorld()
        a1.forEach { store.confirm(it, store.objectiveOf(it), recompute) }
        val query = EnglishProfileQuery(store, noTopics)
        assertEquals(CefrBand.A1, query.load()!!.currentCompleteBand)
        assertEquals(CefrBand.A1, query.load()!!.historicalHighestBand)
        // A contradiction opens a verification and keeps mastery; a fresh clean failure lets the gates decide again.
        val first = a1.first()
        store.record(first, store.objectiveOf(first), EvidenceOutcome.NEGATIVE, "2026-10-02"); recompute.recompute(first)
        assertEquals(BaseProfileStatus.COMPLETE_VERIFICATION_DUE, query.load()!!.status)
        repeat(3) { i -> store.record(first, store.objectiveOf(first), EvidenceOutcome.NEGATIVE, "2026-10-0${3 + i}"); recompute.recompute(first) }
        val after = query.load()!!
        assertNull(after.currentCompleteBand)
        // The band that was complete stays in the history, read from the evidence and not from a remembered column.
        assertEquals(CefrBand.A1, after.historicalHighestBand)
        assertTrue(after.bands.first().missing.contains(first))
    }
}
