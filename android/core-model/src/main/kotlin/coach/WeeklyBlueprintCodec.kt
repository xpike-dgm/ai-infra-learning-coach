package coach.model

/**
 * How a weekly blueprint is stored in `assessment_session.blueprint` (13A).
 *
 * `DDM-v0` describes `assessment_session` as "one session, its blocks and boundaries" and 10D left the
 * content column to 13. The blueprint is **truth**: a session's slots are fixed when it is composed, a
 * submitted boundary never changes, and a recomposition is a new row rather than an edit. So, like the
 * planner's trace, it is a strict, versioned text: an unknown version, section, field or value decodes
 * to `null`, never to a guess.
 *
 * Each line is a section name and tab-separated `key=value` fields, values percent-encoded and lists
 * comma-joined encoded items — the same layers `PlanTraceCodec` uses.
 */
object WeeklyBlueprintCodec {
    const val FORMAT = "weekly_blueprint/1"

    fun encode(blueprint: WeeklyAssessmentBlueprint): String = buildList {
        add(FORMAT)
        with(blueprint) {
            add(CodecText.line("blueprint", "cycle" to cycleId, "study_day" to studyDay,
                "curriculum_version" to curriculumVersion.toString(), "truth_watermark" to truthWatermark.toString(),
                "policy" to policyVersion, "evaluator_available" to evaluatorAvailable.toString(),
                "recent_since" to recentSince.orEmpty(), "supersedes" to supersedesSessionId?.toString().orEmpty(),
                "reasons" to CodecText.list(reasonCodes)))
        }
        blueprint.slots.forEach { s ->
            add(CodecText.line("slot", "slot_id" to s.slotId, "role" to s.role.id, "need_key" to s.needKey,
                "trigger" to s.trigger.id, "target_skill" to s.targetSkill.toString(), "criticality" to s.criticality.id,
                "band" to s.band.id, "source_state_refs" to CodecText.list(s.sourceStateRefs), "track" to s.track.orEmpty(),
                "status" to s.status.id, "item" to s.item?.toString().orEmpty(),
                "objectives" to CodecText.list(s.targetObjectives.map(VersionedRef::toString)),
                "evidence_type" to s.evidenceType.orEmpty(), "variant_family" to s.variantFamilyId.orEmpty(),
                "dependency_group" to s.dependencyGroupId.orEmpty(),
                "minutes" to s.expectedActiveMinutes?.toString().orEmpty(), "item_lifecycle" to s.itemLifecycle?.id.orEmpty(),
                "item_required_skills" to CodecText.list(s.itemRequiredSkills.map(VersionedRef::toString)),
                "allowed_tools" to CodecText.list(s.allowedTools),
                "required_for_closure" to s.requiredForSessionClosure.toString(), "reasons" to CodecText.list(s.reasonCodes)))
            s.rejections.forEach { r ->
                add(CodecText.line("rejection", "slot_id" to s.slotId, "item" to r.item.toString(),
                    "reasons" to CodecText.list(r.reasons)))
            }
        }
        blueprint.exclusions.forEach {
            add(CodecText.line("exclusion", "need_key" to it.needKey, "skill" to it.skill.toString(), "reason" to it.reason.id))
        }
    }.joinToString("\n")

    fun decode(stored: String): WeeklyAssessmentBlueprint? = runCatching { decodeOrThrow(stored) }.getOrNull()

    private fun decodeOrThrow(stored: String): WeeklyAssessmentBlueprint {
        val lines = stored.split("\n")
        if (lines.firstOrNull() != FORMAT) throw CodecText.Malformed()
        var head: Map<String, String>? = null
        val slots = mutableListOf<Map<String, String>>()
        val rejections = mutableListOf<Map<String, String>>()
        val exclusions = mutableListOf<PoolExclusion>()
        lines.drop(1).forEach { raw ->
            val (section, f) = CodecText.fields(raw)
            fun v(key: String) = f[key] ?: throw CodecText.Malformed()
            when (section) {
                "blueprint" -> { if (head != null) throw CodecText.Malformed(); head = f }
                "slot" -> slots += f
                "rejection" -> rejections += f
                "exclusion" -> exclusions += PoolExclusion(v("need_key"), CodecText.ref(v("skill")),
                    WeeklyExclusion.entries.single { it.id == v("reason") })
                else -> throw CodecText.Malformed()
            }
        }
        val h = head ?: throw CodecText.Malformed()
        fun hv(key: String) = h[key] ?: throw CodecText.Malformed()
        val bySlot = rejections.groupBy { it["slot_id"] ?: throw CodecText.Malformed() }
        val decodedSlots = slots.map { f ->
            fun v(key: String) = f[key] ?: throw CodecText.Malformed()
            AssessmentBlueprintSlot(
                slotId = v("slot_id"),
                role = BlueprintRole.entries.single { it.id == v("role") },
                needKey = v("need_key"),
                trigger = NeedTrigger.entries.single { it.id == v("trigger") },
                targetSkill = CodecText.ref(v("target_skill")),
                criticality = Criticality.entries.single { it.id == v("criticality") },
                band = PriorityBand.entries.single { it.id == v("band") },
                sourceStateRefs = CodecText.items(v("source_state_refs")),
                track = v("track").ifEmpty { null },
                status = SlotStatus.entries.single { it.id == v("status") },
                item = v("item").ifEmpty { null }?.let(CodecText::ref),
                targetObjectives = CodecText.items(v("objectives")).map(CodecText::ref),
                evidenceType = v("evidence_type").ifEmpty { null },
                variantFamilyId = v("variant_family").ifEmpty { null },
                dependencyGroupId = v("dependency_group").ifEmpty { null },
                expectedActiveMinutes = v("minutes").ifEmpty { null }?.toInt(),
                itemLifecycle = v("item_lifecycle").ifEmpty { null }?.let { id -> LifecycleStatus.entries.single { it.id == id } },
                itemRequiredSkills = CodecText.items(v("item_required_skills")).map(CodecText::ref),
                allowedTools = CodecText.items(v("allowed_tools")),
                requiredForSessionClosure = CodecText.bool(v("required_for_closure")),
                reasonCodes = CodecText.items(v("reasons")),
                rejections = bySlot[v("slot_id")].orEmpty().map { r ->
                    WeeklyItemRejection(CodecText.ref(r["item"] ?: throw CodecText.Malformed()),
                        CodecText.items(r["reasons"] ?: throw CodecText.Malformed()))
                },
            )
        }
        // A rejection that names no slot of this blueprint is not something to guess a home for.
        if (!decodedSlots.map { it.slotId }.containsAll(bySlot.keys)) throw CodecText.Malformed()
        return WeeklyAssessmentBlueprint(
            cycleId = hv("cycle"),
            studyDay = hv("study_day"),
            curriculumVersion = hv("curriculum_version").toInt(),
            truthWatermark = hv("truth_watermark").toLong(),
            policyVersion = hv("policy"),
            evaluatorAvailable = CodecText.bool(hv("evaluator_available")),
            recentSince = hv("recent_since").ifEmpty { null },
            slots = decodedSlots,
            exclusions = exclusions,
            reasonCodes = CodecText.items(hv("reasons")),
            supersedesSessionId = hv("supersedes").ifEmpty { null }?.toLong(),
        )
    }
}

/**
 * The text layers a strict stored trace is made of: tab-separated sections, percent-encoded values,
 * comma-joined lists and pinned references. Decoding anything unexpected throws [Malformed], which a
 * codec turns into `null` — never into a guess.
 */
internal object CodecText {

    class Malformed : RuntimeException()

    fun line(section: String, vararg fields: Pair<String, String>) =
        (listOf(section) + fields.map { (k, v) -> "$k=${escape(v)}" }).joinToString("\t")

    fun fields(raw: String): Pair<String, Map<String, String>> {
        val parts = raw.split("\t")
        val seen = mutableMapOf<String, String>()
        parts.drop(1).forEach { field ->
            val at = field.indexOf('=')
            if (at <= 0) throw Malformed()
            val key = field.substring(0, at)
            if (key in seen) throw Malformed()
            seen[key] = unescape(field.substring(at + 1)) ?: throw Malformed()
        }
        return parts[0] to seen
    }

    fun list(values: List<String>) = values.joinToString(",") { escape(it) }

    fun items(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(",").map { unescape(it) ?: throw Malformed() }

    fun bool(value: String) = when (value) {
        "true" -> true
        "false" -> false
        else -> throw Malformed()
    }

    fun ref(value: String): VersionedRef {
        val at = value.lastIndexOf("@v")
        if (at <= 0) throw Malformed()
        return VersionedRef(value.substring(0, at), value.substring(at + 2).toIntOrNull() ?: throw Malformed())
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
