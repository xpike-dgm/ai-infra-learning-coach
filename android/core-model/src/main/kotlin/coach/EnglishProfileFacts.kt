package coach.model

/**
 * The Technical English profile (16C, `PWPX-v0`): `TEPM-v0 / D-067` as a derivation over the Skill states the engines
 * already wrote. It writes no mastery and decides none (`TEPM-v0` invariants): every English Skill uses the same eight
 * presentation states as every other Skill (`SPWX-v0` §11), and the base band is only a reading of them.
 *
 * The band is a **qualified Technical English profile**, text-first. It is never a general English level, an official
 * CEFR level or a certificate, never a number or an average, and English is never a gate for technical work.
 */

/** `TECP-v0`'s three base bands, in their order. B2+ is a per-capability extension, never a band (`TEPM-v0` §7). */
enum class CefrBand(val id: String) {
    A1("A1"),
    A2("A2"),
    B1("B1"),
}

/**
 * `TECP-v0` (`curriculum/english/7b_cefr_progression/alignment.yaml` `skill_alignments`) as data: each canonical English
 * Skill's anchor band and whether it may carry B2+ extension evidence. The 7B file is the source; a validator checks this
 * table against it, entry for entry, so the two cannot drift.
 */
object TechnicalEnglishAnchors {
    val anchors: List<Pair<String, CefrBand>> = listOf(
        "skill.english.recognize_core_technical_labels" to CefrBand.A1,
        "skill.english.technical_noun_phrase_recognition" to CefrBand.A1,
        "skill.english.be_and_simple_present_comprehension" to CefrBand.A1,
        "skill.english.imperative_instruction_comprehension" to CefrBand.A1,
        "skill.english.preposition_function_word_comprehension" to CefrBand.A1,
        "skill.english.negation_question_comprehension" to CefrBand.A2,
        "skill.english.follow_bilingual_technical_instruction" to CefrBand.A2,
        "skill.english.read_simple_terminal_error_fragments" to CefrBand.A2,
        "skill.english.documentation_navigation" to CefrBand.A2,
        "skill.english.write_command_result_note" to CefrBand.A2,
        "skill.english.read_definition_and_constraint" to CefrBand.B1,
        "skill.english.read_procedure_sequence" to CefrBand.B1,
        "skill.english.write_basic_bug_description" to CefrBand.B1,
        "skill.english.explain_simple_technical_process" to CefrBand.B1,
        "skill.english.ask_clarifying_technical_question" to CefrBand.B1,
    )

    val extensionEligible: Set<String> = setOf(
        "skill.english.documentation_navigation",
        "skill.english.read_definition_and_constraint",
        "skill.english.read_procedure_sequence",
        "skill.english.ask_clarifying_technical_question",
    )

    fun bandOf(skillLogicalId: String): CefrBand? = anchors.firstOrNull { it.first == skillLogicalId }?.second
}

/** `TEPM-v0` `base_profile.profile_status_values`, in its order. */
enum class BaseProfileStatus(val id: String) {
    NOT_YET_COMPLETE("not_yet_complete"),
    COMPLETE_CURRENT("complete_current"),
    COMPLETE_REVIEW_DUE("complete_review_due"),
    COMPLETE_VERIFICATION_DUE("complete_verification_due"),
}

/** `TEPM-v0` `confirmation_sources`: a confirmed Skill was learned and verified, or confirmed by validated entry evidence. */
enum class ConfirmationSource(val id: String) {
    LEARNED_AND_VERIFIED("learned_and_verified"),
    VALIDATED_ENTRY_EVIDENCE("validated_entry_evidence"),
}

/**
 * `TEPM-v0` §7's extension states. Only [NO_EXTENSION_EVIDENCE] has a producer: no B2+ extension item is authored yet
 * (15F wrote A1/A2), so nothing can be extension evidence, and saying so is the honest answer.
 */
enum class ExtensionState(val id: String) {
    NO_EXTENSION_EVIDENCE("no_extension_evidence"),
    EXTENSION_PRACTICE_ONLY("extension_practice_only"),
    EXTENSION_EVIDENCE_PRESENT("extension_evidence_present"),
    EXTENSION_REVIEW_DUE("extension_review_due"),
    EXTENSION_VERIFICATION_DUE("extension_verification_due"),
}

/** How one base band stands right now. */
enum class BandCompletion(val id: String) {
    COMPLETE("complete"),
    INCOMPLETE("incomplete"),
    /** One of its anchor Skills is not published: nothing can be said about the band, and it is not a gap. */
    NOT_PUBLISHED("not_published"),
    /** One of its anchor Skills' state cannot be told yet (16A): nothing is claimed. */
    RECOMPUTING("recomputing"),
}

/** One canonical English Skill as the profile reads it. [row] is `null` when the Skill is not published. */
data class EnglishSkillReading(
    val skillLogicalId: String,
    val band: CefrBand,
    val row: SkillProgressRow?,
    /** Some Objective of the Skill was waived by a 13F diagnostic — `validated_entry_evidence` when confirmed. */
    val waived: Boolean,
) {
    /** Currently confirmed: the mastery engine confirmed it and no confirmed remediation is open (`TEPM-v0` §6). */
    val confirmed: Boolean
        get() = (row?.projection as? SkillStateProjection.Derived)?.state in EnglishProfileProjection.confirmedStates
}

/**
 * Everything the profile is derived from. [historicalHighest] is the highest band that was ever complete, found by
 * replaying the mastery engine over the stored evidence in recording order (16B's replay; user decision, 2026-10-24).
 */
data class EnglishProfileReading(
    val skills: List<EnglishSkillReading>,
    val historicalHighest: CefrBand?,
)

data class BandLine(
    val band: CefrBand,
    val completion: BandCompletion,
    /** Anchor Skills of this band that are published and not currently confirmed — the exact gap, Skill by Skill. */
    val missing: List<VersionedRef>,
)

data class ExtensionLine(val skill: VersionedRef, val state: ExtensionState)

/** The derived profile (`TEPM-v0` `profile_trace.base_summary_fields`). */
data class EnglishProfile(
    val currentCompleteBand: CefrBand?,
    val status: BaseProfileStatus,
    val historicalHighestBand: CefrBand?,
    val bands: List<BandLine>,
    val reviewDue: List<VersionedRef>,
    val verificationDue: List<VersionedRef>,
    val remediation: List<VersionedRef>,
    val developing: List<VersionedRef>,
    val confirmationSources: Map<VersionedRef, ConfirmationSource>,
    val extensions: List<ExtensionLine>,
    val reasonCodes: List<String>,
    val reading: EnglishProfileReading,
)

object EnglishProfileProjection {

    const val POLICY_VERSION = "TEPM-v0"

    /** The presentation states that count as currently confirmed; `remediation_required` never does. */
    val confirmedStates: Set<SkillPresentationState> = setOf(
        SkillPresentationState.CONFIRMED_CURRENT,
        SkillPresentationState.CONFIRMED_REVIEW_DUE,
        SkillPresentationState.CONFIRMATION_VERIFICATION_DUE,
    )

    /**
     * `TEPM-v0` `base_profile.highest_complete_rule`: the highest band all of whose anchor Skills **at or below** it are
     * confirmed. One rule for the current band (from the Skills' states now) and the historical one (from the replay), so
     * the two can never disagree about what "complete" means. An unpublished anchor Skill ends the walk: a band that
     * cannot be checked is not complete.
     */
    fun highestComplete(confirmed: Set<String>, published: Set<String>): CefrBand? {
        var highest: CefrBand? = null
        for (band in CefrBand.entries) {
            val anchors = TechnicalEnglishAnchors.anchors.filter { it.second == band }.map { it.first }
            if (anchors.any { it !in published } || anchors.any { it !in confirmed }) break
            highest = band
        }
        return highest
    }

    fun derive(reading: EnglishProfileReading): EnglishProfile {
        val published = reading.skills.filter { it.row != null }
        val bands = CefrBand.entries.map { band ->
            val anchors = reading.skills.filter { it.band == band }
            val completion = when {
                anchors.any { it.row == null } -> BandCompletion.NOT_PUBLISHED
                anchors.any { it.row?.projection is SkillStateProjection.Recomputing } -> BandCompletion.RECOMPUTING
                anchors.all { it.confirmed } -> BandCompletion.COMPLETE
                else -> BandCompletion.INCOMPLETE
            }
            BandLine(band, completion, anchors.filter { it.row != null && !it.confirmed }.map { it.row!!.skill })
        }
        // The current band stops at the first band that is not complete; a recomputing band is never claimed.
        val current = bands.takeWhile { it.completion == BandCompletion.COMPLETE }.lastOrNull()?.band
        val within = current?.let { c -> published.filter { it.band <= c } }.orEmpty()
        fun stateOf(s: EnglishSkillReading) = (s.row?.projection as? SkillStateProjection.Derived)?.state
        val status = when {
            current == null -> BaseProfileStatus.NOT_YET_COMPLETE
            within.any { stateOf(it) == SkillPresentationState.CONFIRMATION_VERIFICATION_DUE } -> BaseProfileStatus.COMPLETE_VERIFICATION_DUE
            within.any { stateOf(it) == SkillPresentationState.CONFIRMED_REVIEW_DUE } -> BaseProfileStatus.COMPLETE_REVIEW_DUE
            else -> BaseProfileStatus.COMPLETE_CURRENT
        }
        fun refsIn(vararg states: SkillPresentationState) = published.filter { stateOf(it) in states }.map { it.row!!.skill }
        val developing = refsIn(SkillPresentationState.DEVELOPING_WITH_SUPPORT, SkillPresentationState.DEVELOPING_INDEPENDENT)
        val sources = published.filter { it.confirmed }.associate {
            it.row!!.skill to if (it.waived) ConfirmationSource.VALIDATED_ENTRY_EVIDENCE else ConfirmationSource.LEARNED_AND_VERIFIED
        }
        val extensions = published.filter { it.skillLogicalId in TechnicalEnglishAnchors.extensionEligible }
            .map { ExtensionLine(it.row!!.skill, ExtensionState.NO_EXTENSION_EVIDENCE) }
        // The historical band never sits below the current one: both use one rule, and the replay ends where mastery is.
        val historical = listOfNotNull(reading.historicalHighest, current).maxOrNull()
        val codes = buildList {
            when (status) {
                BaseProfileStatus.COMPLETE_CURRENT -> add("TECHNICAL_ENGLISH_BASE_BAND_COMPLETE")
                BaseProfileStatus.COMPLETE_REVIEW_DUE -> add("TECHNICAL_ENGLISH_BASE_BAND_REVIEW_DUE")
                BaseProfileStatus.COMPLETE_VERIFICATION_DUE -> add("TECHNICAL_ENGLISH_BASE_BAND_VERIFICATION_DUE")
                BaseProfileStatus.NOT_YET_COMPLETE -> Unit
            }
            // Confirmed Skills above the complete band: the profile is uneven, and that is first-class (`TEPM-v0` §8).
            if (published.any { it.confirmed && (current == null || it.band > current) }) add("TECHNICAL_ENGLISH_UNEVEN_PROFILE")
        }
        return EnglishProfile(
            currentCompleteBand = current,
            status = status,
            historicalHighestBand = historical,
            bands = bands,
            reviewDue = refsIn(SkillPresentationState.CONFIRMED_REVIEW_DUE),
            verificationDue = refsIn(SkillPresentationState.CONFIRMATION_VERIFICATION_DUE),
            remediation = refsIn(SkillPresentationState.REMEDIATION_REQUIRED),
            developing = developing,
            confirmationSources = sources,
            extensions = extensions,
            reasonCodes = codes,
            reading = reading,
        )
    }

    /** `TEPM-v0` `reason_codes` for one Skill's state. */
    fun skillReasonCode(state: SkillPresentationState): String = when (state) {
        SkillPresentationState.NOT_YET_EVIDENCED -> "ENGLISH_SKILL_NOT_YET_EVIDENCED"
        SkillPresentationState.DEVELOPING_WITH_SUPPORT -> "ENGLISH_SKILL_DEVELOPING_ASSISTED"
        SkillPresentationState.DEVELOPING_INDEPENDENT -> "ENGLISH_SKILL_DEVELOPING_INDEPENDENT"
        SkillPresentationState.CONFIRMED_CURRENT -> "ENGLISH_SKILL_CONFIRMED"
        SkillPresentationState.CONFIRMED_REVIEW_DUE -> "ENGLISH_SKILL_REVIEW_DUE"
        SkillPresentationState.CONFIRMATION_VERIFICATION_DUE -> "ENGLISH_SKILL_VERIFICATION_DUE"
        SkillPresentationState.REMEDIATION_REQUIRED -> "ENGLISH_SKILL_REMEDIATION_REQUIRED"
        SkillPresentationState.PREREQUISITE_UNRESOLVED -> "ENGLISH_PREREQUISITE_UNRESOLVED"
    }
}
