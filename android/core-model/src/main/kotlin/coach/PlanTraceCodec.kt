package coach.model

/**
 * How a plan's `PDT-v0` trace is stored in `planner_decision_trace.trace` (12C).
 *
 * `DDM-v0` gives the trace one text column and `planned_task` no columns for purpose, title, activity or
 * minutes. Rather than invent columns (10D), everything a later reader needs travels in this versioned
 * text, keyed by the planned task's position. The format is strict in both directions: an unknown
 * version, an unknown section or a malformed field decodes to `null`, never to a guess.
 *
 * Each line is a section name and tab-separated `key=value` fields; values are percent-encoded so a
 * title can contain anything, and lists are comma-joined encoded items.
 */
object PlanTraceCodec {
    const val FORMAT = "planner_trace/1"

    fun encode(trace: PlanTrace): String = buildList {
        add(FORMAT)
        add(line("plan", "generation_kind" to trace.generationKind, "study_day" to trace.studyDay,
            "curriculum_version" to trace.curriculumVersion.toString(), "truth_watermark" to trace.truthWatermark.toString(),
            "skills_not_on_route" to trace.skillsNotOnRoute.toString()))
        trace.policyVersions.forEach { (key, value) -> add(line("policy", "key" to key, "value" to value)) }
        with(trace.capacity) {
            add(line("capacity", "source" to source.id, "hard" to hardBudgetMinutes.toString(),
                "planning" to planningBudgetMinutes.toString(), "reserve_relaxed" to reserveRelaxed.toString(),
                "below_minimum_block" to belowMinimumBlock.toString()))
        }
        add(line("plan_reasons", "codes" to list(trace.planReasonCodes)))
        trace.invariantChecks.forEach { (name, holds) -> add(line("invariant", "name" to name, "holds" to holds.toString())) }
        trace.selected.forEach {
            add(line("selected", "position" to it.position.toString(), "candidate_id" to it.candidateId,
                "need_key" to it.needKey, "purpose" to it.purpose.id, "activity_kind" to it.activityKind,
                "title" to it.title, "primary_skill" to it.primarySkill.toString(), "track" to it.track.orEmpty(),
                "estimated" to it.estimatedMinutes.toString(), "planned" to it.plannedMinutes.toString(),
                "split" to it.split.toString()))
        }
        trace.needs.forEach {
            add(line("need", "need_key" to it.needKey, "trigger" to it.trigger.id,
                "target_skills" to list(it.targetSkills.map(VersionedRef::toString)),
                "source_state_refs" to list(it.sourceStateRefs), "band" to it.band.id,
                "scope" to it.rank.blockingScope.id, "criticality" to it.rank.criticality.id,
                "severity" to it.rank.evidenceSeverity.id, "urgency" to it.rank.temporalUrgency.id,
                "starvation" to it.rank.starvation.id, "continuation" to it.rank.continuation.id,
                "decision_value" to it.rank.decisionValue.id, "track_balance" to it.rank.trackBalance.id,
                "duration_fit" to it.rank.durationFit.id, "tie_break" to it.rank.tieBreakKey,
                "priority_reasons" to list(it.priorityReasonCodes), "selected_candidate" to it.selectedCandidateId.orEmpty(),
                "disposition" to it.disposition.id, "final_reasons" to list(it.finalReasonCodes)))
        }
        trace.candidates.forEach {
            add(line("candidate", "candidate_id" to it.candidateId, "need_key" to it.needKey,
                "validation" to it.validationStatus.id, "eligibility" to it.eligibility?.id.orEmpty(),
                "cost" to it.costMinutes.toString(), "disposition" to it.disposition.id, "reasons" to list(it.reasonCodes)))
        }
    }.joinToString("\n")

    fun decode(stored: String): PlanTrace? = runCatching { decodeOrThrow(stored) }.getOrNull()

    private class Malformed : RuntimeException()

    private fun decodeOrThrow(stored: String): PlanTrace {
        val lines = stored.split("\n")
        if (lines.firstOrNull() != FORMAT) throw Malformed()
        var plan: Map<String, String>? = null
        var capacity: DailyCapacity? = null
        var planReasons: List<String>? = null
        val policies = linkedMapOf<String, String>()
        val invariants = linkedMapOf<String, Boolean>()
        val selected = mutableListOf<PlannedEntry>()
        val needs = mutableListOf<NeedTrace>()
        val candidates = mutableListOf<CandidateTrace>()
        lines.drop(1).forEach { raw ->
            val parts = raw.split("\t")
            val f = parts.drop(1).associate { field ->
                val at = field.indexOf('=')
                if (at <= 0) throw Malformed()
                field.substring(0, at) to (unescape(field.substring(at + 1)) ?: throw Malformed())
            }
            fun v(key: String) = f[key] ?: throw Malformed()
            when (parts[0]) {
                "plan" -> plan = f
                "policy" -> policies[v("key")] = v("value")
                "capacity" -> capacity = DailyCapacity(
                    source = CapacitySource.entries.single { it.id == v("source") },
                    hardBudgetMinutes = v("hard").toInt(), planningBudgetMinutes = v("planning").toInt(),
                    reserveRelaxed = bool(v("reserve_relaxed")), belowMinimumBlock = bool(v("below_minimum_block")),
                )
                "plan_reasons" -> planReasons = items(v("codes"))
                "invariant" -> invariants[v("name")] = bool(v("holds"))
                "selected" -> selected += PlannedEntry(
                    position = v("position").toInt(), candidateId = v("candidate_id"), needKey = v("need_key"),
                    purpose = TaskPurpose.entries.single { it.id == v("purpose") }, activityKind = v("activity_kind"),
                    title = v("title"), primarySkill = ref(v("primary_skill")), track = v("track").ifEmpty { null },
                    estimatedMinutes = v("estimated").toInt(), plannedMinutes = v("planned").toInt(), split = bool(v("split")),
                )
                "need" -> needs += NeedTrace(
                    needKey = v("need_key"), trigger = NeedTrigger.entries.single { it.id == v("trigger") },
                    targetSkills = items(v("target_skills")).map(::ref), sourceStateRefs = items(v("source_state_refs")),
                    band = PriorityBand.entries.single { it.id == v("band") },
                    rank = RankVector(
                        blockingScope = BlockingScope.entries.single { it.id == v("scope") },
                        criticality = Criticality.entries.single { it.id == v("criticality") },
                        evidenceSeverity = EvidenceSeverity.entries.single { it.id == v("severity") },
                        temporalUrgency = TemporalUrgency.entries.single { it.id == v("urgency") },
                        starvation = StarvationBucket.entries.single { it.id == v("starvation") },
                        continuation = ContinuationValue.entries.single { it.id == v("continuation") },
                        decisionValue = DecisionValue.entries.single { it.id == v("decision_value") },
                        trackBalance = TrackBalance.entries.single { it.id == v("track_balance") },
                        durationFit = DurationFit.entries.single { it.id == v("duration_fit") },
                        tieBreakKey = v("tie_break"),
                    ),
                    priorityReasonCodes = items(v("priority_reasons")),
                    selectedCandidateId = v("selected_candidate").ifEmpty { null },
                    disposition = NeedDisposition.entries.single { it.id == v("disposition") },
                    finalReasonCodes = items(v("final_reasons")),
                )
                "candidate" -> candidates += CandidateTrace(
                    candidateId = v("candidate_id"), needKey = v("need_key"),
                    validationStatus = LifecycleStatus.entries.single { it.id == v("validation") },
                    eligibility = v("eligibility").ifEmpty { null }?.let { id -> PrerequisiteEligibility.entries.single { it.id == id } },
                    costMinutes = v("cost").toInt(),
                    disposition = CandidateDisposition.entries.single { it.id == v("disposition") },
                    reasonCodes = items(v("reasons")),
                )
                else -> throw Malformed()
            }
        }
        val p = plan ?: throw Malformed()
        return PlanTrace(
            generationKind = p["generation_kind"] ?: throw Malformed(),
            studyDay = p["study_day"] ?: throw Malformed(),
            curriculumVersion = (p["curriculum_version"] ?: throw Malformed()).toInt(),
            truthWatermark = (p["truth_watermark"] ?: throw Malformed()).toLong(),
            policyVersions = policies,
            capacity = capacity ?: throw Malformed(),
            needs = needs,
            candidates = candidates,
            selected = selected,
            planReasonCodes = planReasons ?: throw Malformed(),
            invariantChecks = invariants,
            skillsNotOnRoute = (p["skills_not_on_route"] ?: throw Malformed()).toInt(),
        )
    }

    private fun line(section: String, vararg fields: Pair<String, String>) =
        (listOf(section) + fields.map { (k, v) -> "$k=${escape(v)}" }).joinToString("\t")

    /** A list is comma-joined items, each escaped first so an item can itself contain a comma. */
    private fun list(values: List<String>) = values.joinToString(",") { escape(it) }

    private fun items(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(",").map { unescape(it) ?: throw Malformed() }

    private fun bool(value: String) = when (value) {
        "true" -> true
        "false" -> false
        else -> throw Malformed()
    }

    private fun ref(value: String): VersionedRef {
        val at = value.lastIndexOf("@v")
        if (at <= 0) throw Malformed()
        return VersionedRef(value.substring(0, at), value.substring(at + 2).toInt())
    }

    private const val HEX = "0123456789ABCDEF"
    private val UNRESERVED = ('A'..'Z').toSet() + ('a'..'z') + ('0'..'9') + setOf('-', '.', '_', '~', '@', ':', '/')

    private fun escape(value: String): String = buildString {
        value.toByteArray(Charsets.UTF_8).forEach { byte ->
            val char = byte.toInt().toChar()
            if (byte >= 0 && char in UNRESERVED) append(char)
            else append('%').append(HEX[(byte.toInt() shr 4) and 0xF]).append(HEX[byte.toInt() and 0xF])
        }
    }

    /** The encoder's inverse, reading its own two layers: `%` escapes inside a field, then a list. */
    private fun unescape(encoded: String): String? {
        val bytes = ArrayList<Byte>(encoded.length)
        var i = 0
        while (i < encoded.length) {
            val char = encoded[i]
            when {
                char == '%' -> {
                    if (i + 2 >= encoded.length) return null
                    val value = encoded.substring(i + 1, i + 3).toIntOrNull(16) ?: return null
                    bytes += value.toByte()
                    i += 3
                }
                char in UNRESERVED || char == ',' -> {
                    bytes += char.code.toByte()
                    i += 1
                }
                else -> return null
            }
        }
        return bytes.toByteArray().toString(Charsets.UTF_8)
    }
}
