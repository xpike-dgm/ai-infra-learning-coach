package coach.application

import coach.model.NamedEntity
import coach.model.TopicCoverage
import coach.model.ProgressFacts
import coach.model.SkillProgressRow
import coach.model.TopicDetailFacts
import coach.model.TopicFacts
import coach.model.TopicMember
import coach.model.TopicReading
import coach.model.TopicRow
import coach.model.TopicStateProjection
import coach.model.VersionedRef
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * Reads every Topic's state (16C, `PWPX-v0`; `TSM-v0`, `SPWX-v0` §§6, 9). It only reads and writes nothing.
 *
 * Each Skill's state is 16A's — the very rows Progress shows — so a Topic can never contradict its own Skills. On top of
 * them it reads three facts: whether the gate lets one of its Skills start (its entry) and, when not, which outside
 * Skills it waits for; whether work on the Topic was ever recorded (started); and whether each Objective's lesson was
 * worked or diagnostically waived (coverage).
 * [TopicStateProjection.derive] decides.
 *
 * Placement comes from the authored packages, as 16A's did; placement is never a prerequisite (`SPWX-v0` §6).
 */
class TopicQuery(
    private val persistence: PersistencePort,
    private val content: ContentPort,
) {
    fun load(progress: ProgressFacts = ProgressQuery(persistence, content).load()): TopicFacts {
        if (!progress.curriculumLoaded) return TopicFacts(curriculumLoaded = false, topics = emptyList())
        val bySkill = progress.skills.associateBy { it.skill.logicalId }
        return TopicFacts(curriculumLoaded = true, topics = topics().mapNotNull { row(it, bySkill) })
    }

    /** `topic_detail`: the row and its Skills, each the same row Progress shows. `null` for a Topic nothing places. */
    fun detail(topic: VersionedRef, progress: ProgressFacts = ProgressQuery(persistence, content).load()): TopicDetailFacts? {
        val entity = topics().firstOrNull { VersionedRef(it.logicalId, it.version) == topic } ?: return null
        val bySkill = progress.skills.associateBy { it.skill.logicalId }
        val row = row(entity, bySkill) ?: return null
        val members = row.projection.reading.members.mapNotNull { bySkill[it.skill.logicalId] }
        val named = (members.map { it.skill } + row.projection.reading.members.flatMap { it.externalBlockers + it.externalUncertain })
            .distinct().associateWith { persistence.skill(it)?.canonicalName ?: it.logicalId }
        val titles = row.projection.reading.members.flatMap { it.objectives.map { (objective, _) -> objective } }
            .mapNotNull { objective -> content.lessonTitleFor(objective)?.let { objective to it } }.toMap()
        return TopicDetailFacts(row = row, members = members, skillNames = named, objectiveTitles = titles)
    }

    /** The newest version of every authored Topic, in a stable order. */
    private fun topics(): List<NamedEntity> =
        content.curriculumPackages().flatMap { it.topics }.groupBy { it.logicalId }.values
            .map { versions -> versions.maxBy { it.version } }.sortedBy { it.logicalId }

    private fun row(topic: NamedEntity, bySkill: Map<String, SkillProgressRow>): TopicRow? {
        val ref = VersionedRef(topic.logicalId, topic.version)
        val members = content.curriculumPackages().flatMap { it.topicSkillLinks }.filter { it.topic == ref }
            .mapNotNull { bySkill[it.skill.logicalId] }.distinctBy { it.skill }
        // A Topic that places no published Skill has no state to derive; it is not shown rather than shown empty.
        if (members.isEmpty()) return null
        val memberIds = members.map { it.skill.logicalId }.toSet()
        val objectives = members.associate { m -> m.skill to persistence.objectivesOf(m.skill).map { it.ref } }
        val lessons = objectives.values.flatten().associateWith { content.lessonItemsFor(it) }
        val items = members.flatMap { m -> content.assessmentItemsFor(m.skill).map { it.ref } } + lessons.values.flatten()
        val attempted = persistence.attemptedResources(items.distinct())
        val started = members.any { it.projection.reading.evidenced } || attempted.isNotEmpty()
        val reading = TopicReading(
            topic = ref,
            started = started,
            members = members.map { m ->
                fun outside(refs: List<VersionedRef>) = refs.filter { it.logicalId !in memberIds }
                TopicMember(
                    skill = m.skill,
                    projection = m.projection,
                    startable = !m.prerequisite.eligibility.waits,
                    externalBlockers = outside(m.prerequisite.hardBlockers),
                    externalUncertain = outside(m.prerequisite.uncertain),
                    metadataProblem = m.prerequisite.metadataProblems.isNotEmpty(),
                    objectives = objectives.getValue(m.skill).map { objective -> objective to coverage(objective, lessons.getValue(objective), attempted) },
                )
            },
        )
        return TopicRow(topic = ref, name = topic.name, projection = TopicStateProjection.derive(reading))
    }

    /** `TSM-v0` §12 (user decision, 2026-10-24): the lesson was worked, or a diagnostic waived it — nothing else covers. */
    private fun coverage(objective: VersionedRef, lesson: List<VersionedRef>, attempted: Set<VersionedRef>): TopicCoverage = when {
        lesson.any { it in attempted } -> TopicCoverage.LESSON_WORKED
        CoverageRows.read(persistence, objective)?.waiver != null -> TopicCoverage.DIAGNOSTIC_WAIVER
        lesson.isEmpty() -> TopicCoverage.NO_LESSON_AUTHORED
        else -> TopicCoverage.NOT_COVERED
    }
}
