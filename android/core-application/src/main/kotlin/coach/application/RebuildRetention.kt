package coach.application

import coach.engines.MasteryEngine
import coach.engines.RetentionEngine
import coach.model.EvidenceRow
import coach.model.MasteryAxisState
import coach.model.ObjectiveGateProfile
import coach.model.RetentionAxis
import coach.model.RetentionEvent
import coach.model.RetentionPolicyV0
import coach.model.RetentionProfile
import coach.model.RetentionReason
import coach.model.RetentionSnapshot
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord

/**
 * Rebuilding one Skill's retention from evidence (13C) — the one state family `RVR-v0` owns (`MSBX-v0`).
 *
 * Like mastery, the projection is **rebuilt, never edited**: the Skill's evidence is replayed in the
 * order it was recorded, and for every row the mastery engine is asked what it would have decided once
 * that row existed — exactly the decision a rebuild after that row produces. The retention engine then
 * schedules around those decisions. Nothing here decides mastery, and nothing here reads a clock to change
 * a learner's state: the day only says whether a scheduled review has come.
 *
 * It writes `retention_state` and the retention axis of `skill_state` — the first engine besides mastery
 * to write that row. The row then carries the **oldest** watermark of the axes on it, so it can never claim
 * to have seen more truth than any one of its axes did.
 */
class RebuildRetention(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Rebuilt(
        val skill: VersionedRef,
        val snapshot: RetentionSnapshot,
        val axis: RetentionAxis,
        val written: Boolean,
    )

    fun rebuild(skill: VersionedRef, profiles: List<ObjectiveGateProfile>): Rebuilt {
        // Watermark first: the projection can never claim to have seen more truth than it did.
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion()
        val row = persistence.skill(skill)
        val profile = RetentionProfile.of(row?.retentionProfile)
        val critical = row?.criticalPrerequisite == true

        val rows = profiles.flatMap { p -> persistence.evidenceFor(p.ref).map { p.ref to it } }
            .distinctBy { it.second.id }
            .sortedBy { it.second.sequence }
        require(rows.all { it.second.studyDay != null }) { "an evidence row with no study day cannot be placed in time" }

        val snapshot = RetentionEngine.replay(skill, profile, critical, events(skill, profiles, rows))
        val today = clock.now().studyDay
        val axis = snapshot.axisOn(today)
        val version = curriculumVersion ?: return Rebuilt(skill, snapshot, axis, written = false)
        persistence.inTransaction {
            RetentionRows.write(persistence, snapshot, today, watermark, version, clock.now().instantEpochMillis)
        }
        return Rebuilt(skill, snapshot, axis, written = true)
    }

    /**
     * Each row as a retention event, with the mastery decision before and after it. The replay mirrors
     * [RebuildMastery] exactly: the previous axis feeds the next decision the same way the stored axis
     * feeds a rebuild, so the timeline is the one a rebuild after every row would have written.
     */
    private fun events(skill: VersionedRef, profiles: List<ObjectiveGateProfile>, rows: List<Pair<VersionedRef, EvidenceRow>>): List<RetentionEvent> {
        val byObjective = profiles.associateBy { it.ref }
        var previous: MasteryAxisState? = null
        return rows.mapIndexed { index, (objective, row) ->
            val prefix = rows.take(index + 1)
            val decisions = profiles.map { profile ->
                MasteryEngine.decide(
                    profile = profile,
                    rows = prefix.filter { it.first == profile.ref }.map { it.second },
                    previouslyMastered = previous in MASTERED,
                    unresolvedVerification = previous == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE,
                )
            }
            val decision = MasteryEngine.decideSkill(
                skill = skill,
                decisions = decisions,
                profiles = byObjective,
                previouslyMastered = previous == MasteryAxisState.CONFIRMED_CURRENT,
                allRows = prefix.map { it.second },
            )
            val before = previous in MASTERED
            previous = decision.axisState
            val earlier = rows.take(index).map { it.second }
            RetentionEvent(
                evidenceId = row.id,
                sequence = row.sequence,
                studyDay = row.studyDay!!,
                outcome = row.outcome,
                evaluatorStatus = row.evaluatorStatus,
                independence = row.independenceClass,
                contested = row.contested,
                prerequisiteValid = row.prerequisiteValid,
                solutionExposed = row.solutionExposed,
                direct = byObjective[objective]?.directEvidenceTypes?.contains(row.evidenceType) == true,
                // `RVR-v0` §13: an item or variant family this Skill already met is a near repeat.
                nearRepeat = earlier.any { other ->
                    (row.resource != null && other.resource == row.resource) ||
                        (row.variantFamilyId != null && other.variantFamilyId == row.variantFamilyId)
                },
                resource = row.resource,
                masteredBefore = before,
                masteredAfter = decision.axisState in MASTERED,
            )
        }
    }

    private companion object {
        /** Historical mastery is kept while a contradiction is being verified (`GRE-v0`, `RVR-v0` §9). */
        val MASTERED = setOf(MasteryAxisState.CONFIRMED_CURRENT, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
    }
}

/**
 * Moving scheduled reviews to `review_due` when their day comes (13C, `RVR-v0` §7).
 *
 * This is the only thing time does to retention, and it changes no learner's competence: only `fresh` and
 * `stable` schedules whose review day has arrived move, found by the indexed due query (§19) rather than a
 * scan, and each keeps the watermark it had — no truth was read, only the day. A stored row that cannot be
 * read is left as it is and named, never guessed into a state.
 */
class RefreshDueRetention(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Refreshed(val nowDue: List<VersionedRef>, val unreadable: List<VersionedRef>)

    fun refresh(): Refreshed {
        val version = persistence.latestCurriculumVersion() ?: return Refreshed(emptyList(), emptyList())
        val now = clock.now()
        val nowDue = mutableListOf<VersionedRef>()
        val unreadable = mutableListOf<VersionedRef>()
        persistence.retentionDueBy(now.studyDay).forEach { skill ->
            val row = persistence.readProjection(RetentionRows.key(skill))
            val snapshot = row?.let { RetentionRows.decode(skill, it) }
            if (row == null || snapshot == null) {
                unreadable += skill
                return@forEach
            }
            if (snapshot.axisOn(now.studyDay) != RetentionAxis.REVIEW_DUE) return@forEach
            persistence.inTransaction {
                RetentionRows.write(persistence, snapshot, now.studyDay, row.truthWatermark, version, now.instantEpochMillis)
            }
            nowDue += skill
        }
        return Refreshed(nowDue, unreadable)
    }
}

/**
 * `retention_state` as stored (13C): `RVR-v0` §18's fields, one column each. An empty column is "none".
 * Decoding is strict — an unknown state, profile or reason code is not guessed at, it is unreadable.
 */
internal object RetentionRows {

    fun key(skill: VersionedRef) = "retention_state:${skill.logicalId}@v${skill.version}"

    /** Writes the Skill's retention row and its retention axis on `skill_state`, as of [today]. */
    fun write(
        persistence: PersistencePort,
        snapshot: RetentionSnapshot,
        today: String,
        truthWatermark: Long,
        curriculumVersion: Int,
        builtAt: Long,
    ) {
        val axis = snapshot.axisOn(today)
        persistence.writeProjection(
            ProjectionRecord(
                key = key(snapshot.skill),
                policyVersion = RetentionPolicyV0.POLICY_VERSION,
                truthWatermark = truthWatermark,
                builtAtInstant = builtAt,
                inputCurriculumVersion = curriculumVersion,
                payload = mapOf(
                    "state" to axis.id,
                    "retention_profile" to snapshot.profile?.id.orEmpty(),
                    "critical_prerequisite" to if (snapshot.critical) "1" else "0",
                    "current_interval_days" to snapshot.intervalDays?.toString().orEmpty(),
                    "next_review_on_study_day" to snapshot.nextReviewDay.orEmpty(),
                    "last_strong_retention_on_study_day" to snapshot.lastStrongRetentionDay.orEmpty(),
                    "last_retention_evidence_id" to snapshot.lastRetentionEvidenceId?.toString().orEmpty(),
                    "successful_delayed_review_count" to snapshot.successfulDelayedReviews.toString(),
                    "unresolved_verification_evidence_id" to snapshot.unresolvedVerificationEvidenceId?.toString().orEmpty(),
                    "verification_failure_on_study_day" to snapshot.verificationFailureDay.orEmpty(),
                    "at_risk_reason_codes" to snapshot.atRiskReasons.joinToString(",") { it.id },
                    "last_natural_reuse_on_study_day" to snapshot.lastNaturalReuseDay.orEmpty(),
                    "reason_codes" to snapshot.reasonsOn(today).joinToString(",") { it.id },
                    "as_of_study_day" to today,
                ),
            )
        )
        val stateKey = ResolvePrerequisites.skillStateKey(snapshot.skill)
        val existing = persistence.readProjection(stateKey)
        fun carried(column: String) = existing?.payload?.get(column)?.ifEmpty { null } ?: NOT_YET_EVALUATED
        persistence.writeProjection(
            ProjectionRecord(
                key = stateKey,
                // The row's policy is its mastery engine's; retention only adds its own axis to it.
                policyVersion = existing?.policyVersion ?: RetentionPolicyV0.POLICY_VERSION,
                truthWatermark = minOf(existing?.truthWatermark ?: truthWatermark, truthWatermark),
                builtAtInstant = builtAt,
                inputCurriculumVersion = curriculumVersion,
                payload = mapOf(
                    "mastery_axis_state" to carried("mastery_axis_state"),
                    "retention_axis_state" to axis.id,
                    "prerequisite_axis_state" to carried("prerequisite_axis_state"),
                    "weakness_axis_state" to carried("weakness_axis_state"),
                    "primary_presentation_state" to carried("primary_presentation_state"),
                ),
            )
        )
    }

    fun decode(skill: VersionedRef, row: ProjectionRecord): RetentionSnapshot? = runCatching {
        val p = row.payload
        fun v(key: String) = p[key]?.ifEmpty { null }
        fun reasons(key: String) = v(key)?.split(",")?.map { id -> RetentionReason.entries.single { it.id == id } }.orEmpty()
        val profileId = v("retention_profile")
        val profile = profileId?.let { id -> RetentionProfile.entries.single { it.id == id } }
        RetentionSnapshot(
            skill = skill,
            profile = profile,
            critical = when (v("critical_prerequisite")) { "1" -> true; "0" -> false; else -> error("critical") },
            state = RetentionAxis.entries.single { it.id == v("state") },
            intervalDays = v("current_interval_days")?.toInt(),
            nextReviewDay = v("next_review_on_study_day"),
            lastStrongRetentionDay = v("last_strong_retention_on_study_day"),
            lastRetentionEvidenceId = v("last_retention_evidence_id")?.toLong(),
            successfulDelayedReviews = v("successful_delayed_review_count")?.toInt() ?: error("count"),
            unresolvedVerificationEvidenceId = v("unresolved_verification_evidence_id")?.toLong(),
            verificationFailureDay = v("verification_failure_on_study_day"),
            atRiskReasons = reasons("at_risk_reason_codes"),
            lastNaturalReuseDay = v("last_natural_reuse_on_study_day"),
            // The stored codes include the day's `review_due` ones; a schedule is re-derived, not re-read.
            reasons = reasons("reason_codes").filterNot { it in DAY_CODES },
        )
    }.getOrNull()

    private val DAY_CODES = setOf(RetentionReason.REVIEW_DUE, RetentionReason.CRITICAL_REVIEW_DUE, RetentionReason.FIRST_DELAYED_REVIEW)

    /** What an axis says before its engine has ever written it (12A's carried value). */
    private const val NOT_YET_EVALUATED = "not_yet_evaluated"
}
