package coach.model

/**
 * `WAAX-v0 / D-106` — wrong-answer analysis and misconception memory, as types (14B).
 *
 * **A wrong answer is information, not a verdict about the learner.** Which Objective it says something about
 * is the weakness engine's decision (`WLRX-v0`); what *kind* of mistake it may have been is a misconception
 * label — and a label is only ever as strong as the evidence policy lets it be. An AI may propose one; it can
 * never make one more than a hypothesis.
 */

/**
 * One entry of the **closed misconception catalog** (`WLRM-v0` misconception contract; user decision,
 * 2026-10-01): authored with the curriculum, versioned with it and pinned to exactly one Objective version.
 * A label nobody declared for an Objective is not stored, whoever proposed it — that is what keeps the memory
 * from filling with the same mistake under a dozen names.
 *
 * [openQuestion] is how a hypothesis may be put to the learner: as a question, never as a finding
 * (`SPWX-v0`, user decision 2026-10-01).
 */
data class MisconceptionRow(
    val ref: VersionedRef,
    val objective: VersionedRef,
    val name: String,
    val openQuestion: String,
) {
    init {
        require(ID.matches(ref.logicalId)) { "a misconception id is misconception.<namespace>.<slug> (GNS-v0): ${ref.logicalId}" }
        require(name.isNotBlank()) { "a misconception has a name" }
        require(openQuestion.isNotBlank() && openQuestion.trimEnd().endsWith("?")) {
            "a misconception is put to the learner as a question: ${ref.logicalId}"
        }
    }

    companion object {
        /** `GNS-v0` §canonical id: lowercase ASCII, dotted namespace, snake_case segments. */
        val ID = Regex("^misconception(\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")
    }
}

/**
 * Where a label on an evidence row came from. The source sets the **ceiling** of what the label can become:
 * a deterministic path (an answer key, a test, a verified checker) may support or confirm it under the same
 * rules as the Objective's weakness; an AI evaluator's proposal never rises above a hypothesis
 * (`WLRM-v0`: an LLM may propose a hypothesis but cannot publish a confirmed misconception).
 */
enum class MisconceptionSource(val id: String, val ceiling: WeaknessSignal) {
    DETERMINISTIC("deterministic", WeaknessSignal.CONFIRMED),
    AI_PROPOSED("ai_proposed", WeaknessSignal.HYPOTHESIS),
}

/**
 * What an evaluation proposes about one Objective (`AIAX-v0` §5.1 `misconception_hypotheses[]`): a label by
 * its logical id. It is a proposal — the catalog decides whether it names anything, the evidence policy how
 * far it may go.
 */
data class MisconceptionHypothesis(val objective: VersionedRef, val misconceptionId: String)

/** A catalog label recorded on one evidence row, with where it came from. */
data class MisconceptionTag(val misconception: VersionedRef, val source: MisconceptionSource)

/**
 * `evidence_event.misconception_tags` (`DDM-v0` §7.1 names the field; 14B is the first to write it), as strict,
 * versioned text: `misconception_tags/1|<logical_id>@v<N>:<source>|…`. It decodes completely or not at all.
 */
object MisconceptionTags {
    const val FORMAT = "misconception_tags/1"

    fun encode(tags: List<MisconceptionTag>): String? {
        if (tags.isEmpty()) return null
        return (listOf(FORMAT) + tags.distinct().map { "${it.misconception.logicalId}@v${it.misconception.version}:${it.source.id}" })
            .joinToString("|")
    }

    /** `null` when the text is not this format; an absent column is no tags. */
    fun decode(text: String?): List<MisconceptionTag>? {
        if (text.isNullOrEmpty()) return emptyList()
        val parts = text.split("|")
        if (parts.first() != FORMAT || parts.size < 2) return null
        return parts.drop(1).map { part ->
            val ref = part.substringBefore(":", missingDelimiterValue = "")
            val source = MisconceptionSource.entries.firstOrNull { it.id == part.substringAfter(":", missingDelimiterValue = "") } ?: return null
            val id = ref.substringBefore("@v", missingDelimiterValue = "")
            val version = ref.substringAfter("@v", missingDelimiterValue = "").toIntOrNull() ?: return null
            if (!MisconceptionRow.ID.matches(id) || version < 1) return null
            MisconceptionTag(VersionedRef(id, version), source)
        }
    }
}

/**
 * The learner's memory of one catalog misconception (`WLRM-v0` lifecycle `none → hypothesis → supported →
 * confirmed → resolved`). [source] is the strongest source that carried it in its current window. It sets no
 * mastery and decides nothing; it guides remediation and item choice (`WLRM-v0` misconception contract).
 */
data class MisconceptionSignalState(
    val misconception: VersionedRef,
    val objective: VersionedRef,
    val skill: VersionedRef,
    val signal: WeaknessSignal = WeaknessSignal.NONE,
    val source: MisconceptionSource? = null,
    val signalEvidenceIds: List<Long> = emptyList(),
    val firstSeenDay: String? = null,
    val lastSeenDay: String? = null,
    val resolutionEvidenceId: Long? = null,
) {
    init {
        require(signal != WeaknessSignal.RESOLVED || resolutionEvidenceId != null) { "a misconception is resolved by evidence, never by time" }
        require(source == null || signal.strength <= source.ceiling.strength || signal == WeaknessSignal.RESOLVED) {
            "a label is never stronger than its source allows"
        }
    }
}

/**
 * What one evidence row of a wrong answer said, after the weakness engine attributed it: the Objective, how it
 * was attributed (`null` when no rule speaks — a repeat of the same item, a diagnostic baseline), and the
 * catalog labels it carried with each label's state after this row.
 */
data class WrongAnswerFinding(
    val evidenceId: Long,
    val objective: VersionedRef,
    val outcome: AttributionOutcome?,
    val misconceptions: List<Pair<MisconceptionRow, WeaknessSignal>>,
)
