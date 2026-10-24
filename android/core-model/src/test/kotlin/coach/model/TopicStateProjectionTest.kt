package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * 16C (`PWPX-v0`): a Topic's state is `TSM-v0` §10's first rule that holds, read from its Skills' own states, whether
 * work on it was ever recorded, and whether each Objective was taught or waived — never an average and never a guess.
 */
class TopicStateProjectionTest {

    private val topic = VersionedRef("topic.python.loops", 1)
    private val a = VersionedRef("skill.python.a", 1)
    private val b = VersionedRef("skill.python.b", 1)
    private val outside = VersionedRef("skill.python.outside", 1)
    private val oa = VersionedRef("objective.python.a.do", 1)
    private val ob = VersionedRef("objective.python.b.do", 1)

    private fun reading(
        mastery: MasteryAxisState? = MasteryAxisState.NOT_YET_EVIDENCED,
        retention: RetentionAxis = RetentionAxis.UNTRACKED,
        weakness: WeaknessAxis = WeaknessAxis.NONE,
        evidenced: Boolean = mastery != MasteryAxisState.NOT_YET_EVIDENCED,
        stale: Boolean = false,
    ) = SkillPresentationProjection.derive(
        SkillAxisReading(mastery, retention, weakness, PrerequisiteEligibility.ELIGIBLE, evidenced, projectionStale = stale)
    )

    private val confirmed = reading(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.FRESH)

    private fun member(
        skill: VersionedRef,
        projection: SkillStateProjection = reading(),
        startable: Boolean = true,
        blockers: List<VersionedRef> = emptyList(),
        uncertain: List<VersionedRef> = emptyList(),
        metadata: Boolean = false,
        coverage: TopicCoverage = TopicCoverage.LESSON_WORKED,
    ) = TopicMember(skill, projection, startable, blockers, uncertain, metadata, listOf((if (skill == a) oa else ob) to coverage))

    private fun derive(started: Boolean, vararg members: TopicMember) = TopicStateProjection.derive(TopicReading(topic, started, members.toList()))

    private fun stateOf(p: TopicProjection) = assertIs<TopicProjection.Derived>(p).state

    @Test
    fun `an unstarted Topic is available when one of its Skills can start, and locked only when none can`() {
        assertEquals(TopicState.AVAILABLE, stateOf(derive(false, member(a), member(b))))
        // One Skill open, another waiting on an outside Skill: the Topic can be entered (user decision, 2026-10-24).
        val open = assertIs<TopicProjection.Derived>(derive(false, member(a), member(b, startable = false, blockers = listOf(outside))))
        assertEquals(TopicState.AVAILABLE, open.state)
        assertTrue(open.waitingFor.isEmpty())
        val locked = assertIs<TopicProjection.Derived>(derive(false, member(a, startable = false, blockers = listOf(outside)), member(b, startable = false)))
        assertEquals(TopicState.LOCKED, locked.state)
        assertEquals(listOf(outside), locked.waitingFor)
        // An uncertain outside prerequisite keeps the gate closed, and so the Topic (`PRG-v0`).
        assertEquals(TopicState.LOCKED, stateOf(derive(false, member(a, startable = false, uncertain = listOf(outside)))))
        // Unreadable prerequisite metadata fails closed and is named as such, not as a blocker Skill.
        val metadata = assertIs<TopicProjection.Derived>(derive(false, member(a, startable = false, metadata = true)))
        assertEquals(TopicState.LOCKED, metadata.state)
        assertTrue(metadata.metadataProblem && metadata.waitingFor.isEmpty())
        assertEquals("hard_prerequisites_satisfied", (derive(false, member(a)) as TopicProjection.Derived).reasonCode)
    }

    @Test
    fun `a started Topic never goes back to locked, whatever its entry says now`() {
        val started = assertIs<TopicProjection.Derived>(derive(true, member(a, startable = false, blockers = listOf(outside)), member(b, startable = false)))
        assertEquals(TopicState.LEARNING, started.state)
        assertTrue(started.waitingFor.isEmpty())
        assertEquals("topic_started", started.reasonCode)
    }

    @Test
    fun `mastered needs every Skill confirmed and every Objective covered — finishing the lessons is not enough`() {
        assertEquals(TopicState.MASTERED, stateOf(derive(true, member(a, confirmed), member(b, confirmed))))
        // Every lesson worked, one Skill not confirmed: still learning, and the Skill is named.
        val unconfirmed = assertIs<TopicProjection.Derived>(derive(true, member(a, confirmed), member(b, reading(MasteryAxisState.DEVELOPING_INDEPENDENT))))
        assertEquals(TopicState.LEARNING, unconfirmed.state)
        assertEquals(listOf(b), unconfirmed.notYetConfirmed)
        // Every Skill confirmed, one lesson never worked: still learning, and the Objective is named.
        val uncovered = assertIs<TopicProjection.Derived>(derive(true, member(a, confirmed), member(b, confirmed, coverage = TopicCoverage.NOT_COVERED)))
        assertEquals(TopicState.LEARNING, uncovered.state)
        assertEquals(listOf(ob), uncovered.notCovered)
        // A diagnostic waiver covers; a missing lesson never does.
        assertEquals(TopicState.MASTERED, stateOf(derive(true, member(a, confirmed), member(b, confirmed, coverage = TopicCoverage.DIAGNOSTIC_WAIVER))))
        assertEquals(TopicState.LEARNING, stateOf(derive(true, member(a, confirmed), member(b, confirmed, coverage = TopicCoverage.NO_LESSON_AUTHORED))))
    }

    @Test
    fun `a scheduled review keeps the Topic mastered, while at_risk or an open verification is weakening`() {
        val reviewDue = reading(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE)
        assertEquals(TopicState.MASTERED, stateOf(derive(true, member(a, confirmed), member(b, reviewDue))))
        val atRisk = reading(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.AT_RISK)
        val weakening = assertIs<TopicProjection.Derived>(derive(true, member(a, confirmed), member(b, atRisk)))
        assertEquals(TopicState.WEAKENING, weakening.state)
        assertEquals(listOf(b), weakening.retentionSignal)
        val verification = reading(MasteryAxisState.CONFIRMATION_VERIFICATION_DUE, RetentionAxis.FRESH)
        assertEquals(TopicState.WEAKENING, stateOf(derive(true, member(a, confirmed), member(b, verification))))
        assertEquals("retention_risk_confirmed", weakening.reasonCode)
    }

    @Test
    fun `a confirmed localized remediation need comes first and names only its own Skill`() {
        val remediation = reading(MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = WeaknessAxis.REMEDIATION_REQUIRED)
        val p = assertIs<TopicProjection.Derived>(derive(true, member(a, confirmed), member(b, remediation)))
        assertEquals(TopicState.REMEDIATION_REQUIRED, p.state)
        assertEquals(listOf(b), p.remediation)
        // A hypothesis or a supported signal is not remediation (`SPWX-v0` §10).
        val supported = reading(MasteryAxisState.DEVELOPING_INDEPENDENT, weakness = WeaknessAxis.SUPPORTED)
        assertEquals(TopicState.LEARNING, stateOf(derive(true, member(a, confirmed), member(b, supported))))
    }

    @Test
    fun `a Skill whose state cannot be told makes a started Topic recomputing, never a guessed state`() {
        val stale = reading(MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.FRESH, stale = true)
        val p = assertIs<TopicProjection.Recomputing>(derive(true, member(a, confirmed), member(b, stale)))
        assertEquals(listOf(b), p.skills)
    }

    @Test
    fun `the same reading always gives the same state`() {
        val r = TopicReading(topic, true, listOf(member(a, confirmed), member(b, reading(MasteryAxisState.DEVELOPING_WITH_SUPPORT))))
        assertEquals(TopicStateProjection.derive(r), TopicStateProjection.derive(r))
        assertEquals(6, TopicState.entries.size)
    }
}
