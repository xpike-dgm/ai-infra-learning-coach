package coach.persistence

import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.MisconceptionRow
import coach.model.MisconceptionSource
import coach.model.MisconceptionTag
import coach.model.MisconceptionTags
import coach.model.NamedEntity
import coach.model.ObjectiveRow
import coach.model.PublishOutcome
import coach.model.SkillRow
import coach.model.StudyTimestamp
import coach.model.TopicSkillLink
import coach.model.VersionedRef
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The closed misconception catalog and the learner's misconception memory against real SQLite (14B, `TVSX-v0` T2):
 * schema v8 on a populated v7 database, the catalog published, pinned and immutable with its curriculum, tags read
 * back on the evidence row, and the store itself refusing what the contract forbids.
 */
class MisconceptionStorageTest {

    private val at = StudyTimestamp(1_790_000_000_000, "2026-10-01", 3 * 3600)
    private val skill = VersionedRef("skill.c.pointers", 1)
    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val topic = VersionedRef("topic.c.pointers", 1)
    private val addressValue = MisconceptionRow(VersionedRef("misconception.c.pointers.address_value", 1), objective,
        "adres ile değer karışıklığı", "Adres ile değeri karıştırmış olabilir misin?")

    private fun <T> withStore(block: (SqlitePersistence) -> T): T = SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use(block)

    private fun pkg(misconceptions: List<MisconceptionRow> = listOf(addressValue), version: Int = 1) = CurriculumPackage(
        version = version, sourceRefs = "curriculum", provenance = "authored",
        topics = listOf(NamedEntity(topic.logicalId, topic.version, "Pointers")),
        skills = listOf(SkillRow(skill, "Pointers", "Write through a pointer", "published", "concept", "standard", false, "src", "authored")),
        objectives = listOf(ObjectiveRow(objective, skill, true, "standard", listOf("coding"), listOf("coding"))),
        topicSkillLinks = listOf(TopicSkillLink(topic, skill)),
        misconceptions = misconceptions,
    )

    private fun memory(state: String, source: String, resolution: String = "") = ProjectionRecord(
        key = "misconception_state:${addressValue.ref.logicalId}@v1", policyVersion = "WLRM-v0", truthWatermark = 1,
        builtAtInstant = at.instantEpochMillis, inputCurriculumVersion = 1,
        payload = mapOf(
            "objective_logical_id" to objective.logicalId, "objective_version" to "1", "skill_logical_id" to skill.logicalId,
            "skill_version" to "1", "state" to state, "source" to source, "signal_evidence_ids" to "3",
            "first_seen_on_study_day" to "2026-10-01", "last_seen_on_study_day" to "2026-10-01",
            "resolution_evidence_id" to resolution, "as_of_study_day" to "2026-10-01",
        ),
    )

    @Test
    fun `migrating a populated schema-7 database adds the catalog and the memory and preserves every truth row`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 7)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { Fixtures.truthContent(it) }
        SqlitePersistence.open(file.absolutePath).use { db ->
            assertTrue(Schema.VERSION >= 8)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.truthTables.forEach { assertEquals(before[it], after[it], it) }
            val catalog = db.query("PRAGMA table_info(misconception)") { it.getText(1) }
            assertEquals(listOf("logical_id", "version", "objective_logical_id", "objective_version", "name", "open_question"), catalog)
            val memory = db.query("PRAGMA table_info(misconception_state)") { it.getText(1) }
            listOf("state", "source", "signal_evidence_ids", "resolution_evidence_id", "policy_version", "truth_watermark").forEach { assertTrue(it in memory, it) }
        }
    }

    @Test
    fun `the catalog is published with its curriculum, read back pinned, and never edited`() {
        withStore { db ->
            assertIs<PublishOutcome.Published>(db.publishCurriculum(pkg(), at.instantEpochMillis))
            assertEquals(listOf(addressValue), db.misconceptionsOf(objective))
            assertEquals(emptyList(), db.misconceptionsOf(VersionedRef(objective.logicalId, 2)))
            assertFailsWith<Throwable> { db.execute("UPDATE misconception SET name = 'rewritten'") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM misconception") }
            assertEquals(listOf(addressValue), db.misconceptionsOf(objective))
        }
    }

    @Test
    fun `a label pinned to no published Objective refuses the whole package`() {
        withStore { db ->
            val stray = MisconceptionRow(VersionedRef("misconception.c.pointers.stray", 1), VersionedRef("objective.c.pointers.missing", 1),
                "yok", "Bu olabilir mi?")
            val outcome = assertIs<PublishOutcome.Refused>(db.publishCurriculum(pkg(listOf(addressValue, stray)), at.instantEpochMillis))
            assertTrue(outcome.reasons.any { "misconception.c.pointers.stray" in it })
            assertEquals(0L, db.count("misconception"))
            assertEquals(0L, db.count("skill"))
        }
    }

    @Test
    fun `a published label is never carried again, and a later package may pin a new one to an earlier Objective`() {
        // Narrowed at 15B (`D-113`, user decision): an entity carries its own version, not the package's; what stays
        // refused is carrying a label that is already published.
        withStore { db ->
            assertIs<PublishOutcome.Published>(db.publishCurriculum(pkg(), at.instantEpochMillis))
            val again = CurriculumPackage(version = 2, sourceRefs = "curriculum", provenance = "authored", misconceptions = listOf(addressValue))
            assertIs<PublishOutcome.Refused>(db.publishCurriculum(again, at.instantEpochMillis))
            val later = CurriculumPackage(version = 2, sourceRefs = "curriculum", provenance = "authored",
                misconceptions = listOf(MisconceptionRow(VersionedRef("misconception.c.pointers.star_amp_roles", 1), objective,
                    "* ile & rolleri", "* ile & rollerini karıştırmış olabilir misin?")))
            assertIs<PublishOutcome.Published>(db.publishCurriculum(later, at.instantEpochMillis))
            assertEquals(setOf("misconception.c.pointers.address_value", "misconception.c.pointers.star_amp_roles"),
                db.misconceptionsOf(objective).map { it.ref.logicalId }.toSet())
        }
    }

    @Test
    fun `stored tags read back on the evidence row they were written with`() {
        withStore { db ->
            db.publishCurriculum(pkg(), at.instantEpochMillis)
            val tags = listOf(MisconceptionTag(addressValue.ref, MisconceptionSource.DETERMINISTIC))
            db.inTransaction {
                val attempt = db.appendTruth(Fixtures.attempt())
                val id = db.appendTruth(TruthRecord("evidence_event", at, mapOf(
                    "skill_logical_id" to skill.logicalId, "skill_version" to "1", "evidence_type" to "coding",
                    "source_attempt_id" to attempt.toString(), "outcome" to "negative", "evaluator_status" to "verified",
                    "independence_class" to "independent", "contested" to "0", "misconception_tags" to MisconceptionTags.encode(tags)!!,
                )))
                db.appendTruth(TruthRecord("evidence_event_objective", at, mapOf(
                    "evidence_event_id" to id.toString(), "objective_logical_id" to objective.logicalId, "objective_version" to "1")))
            }
            assertEquals(tags, db.evidenceFor(objective).single().misconceptionTags)
        }
    }

    @Test
    fun `the store refuses an AI-proposed label above a hypothesis, a resolution without evidence and an unknown state`() {
        withStore { db ->
            assertFailsWith<Throwable> { db.writeProjection(memory("supported", "ai_proposed")) }
            assertFailsWith<Throwable> { db.writeProjection(memory("confirmed", "ai_proposed")) }
            assertFailsWith<Throwable> { db.writeProjection(memory("resolved", "deterministic")) }
            assertFailsWith<Throwable> { db.writeProjection(memory("suspected", "deterministic")) }
            assertFailsWith<Throwable> { db.writeProjection(memory("hypothesis", "llm")) }
            db.writeProjection(memory("hypothesis", "ai_proposed"))
            db.writeProjection(memory("confirmed", "deterministic"))
            db.writeProjection(memory("resolved", "ai_proposed", resolution = "9"))
            assertEquals("resolved", db.readProjection("misconception_state:${addressValue.ref.logicalId}@v1")!!.payload["state"])
        }
    }
}
