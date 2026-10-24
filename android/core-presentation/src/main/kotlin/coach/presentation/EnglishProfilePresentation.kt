package coach.presentation

import coach.model.BandCompletion
import coach.model.BaseProfileStatus
import coach.model.CefrBand
import coach.model.ConfirmationSource
import coach.model.EnglishProfile
import coach.model.ExtensionState
import coach.model.SkillProgressRow
import coach.model.VersionedRef

/**
 * `technical_english_profile` (16C, `PWPX-v0`): `TEPM-v0`'s qualified Technical English profile as pure functions.
 *
 * Every sentence here is of the allowed kind (`TEPM-v0` `allowed_wording_examples`): a **Technical English base
 * profile**, text-first, band by band, with the exact Skill gap visible. None of it says what the learner's English is,
 * none is an official or certified level, none is a number, and English is never a gate for technical work.
 */
object EnglishProfileCopy {
    const val TITLE = "Teknik İngilizce profili"
    const val NOT_YET_COMPLETE = "Teknik İngilizce — temel profil henüz tamamlanmadı"
    fun complete(band: CefrBand) = "Teknik İngilizce — ${band.id} temel profili doğrulandı"
    fun completeReviewDue(band: CefrBand) = "Teknik İngilizce — ${band.id} temel profili doğrulandı; tekrar zamanı"
    fun completeVerificationDue(band: CefrBand) = "Teknik İngilizce — ${band.id} temel profili; yeniden kontrol gerekli"
    fun uneven(complete: CefrBand?, next: CefrBand) =
        if (complete == null) "${next.id} becerilerinden bazıları doğrulandı; temel profil henüz tamamlanmadı"
        else "${complete.id} temeli tamam; ${next.id} becerileri gelişiyor"
    fun historical(band: CefrBand) = "Daha önce doğrulanmış en yüksek temel: ${band.id}. Bu geçmiş silinmedi."

    val completionLabels: Map<BandCompletion, String> = mapOf(
        BandCompletion.COMPLETE to "Tamam",
        BandCompletion.INCOMPLETE to "Henüz tamam değil",
        // An unpublished band is not a gap and never a failure (user decision, 2026-10-24).
        BandCompletion.NOT_PUBLISHED to "Henüz yayımlanmadı",
        BandCompletion.RECOMPUTING to "Yeniden hesaplanıyor",
    )
    const val MISSING = "Henüz doğrulanmamış"

    // `qualification_scope: technical_english_text_first`; `general_or_official_cefr_claim: false`.
    const val SCOPE_NOTE = "Bu yalnızca metin odaklı Teknik İngilizce profilidir; genel bir İngilizce düzeyi, resmî bir CEFR düzeyi ya da sertifika değildir."
    // `english_global_technical_gate: false`.
    const val GATE_NOTE = "İngilizce hiçbir teknik iş için kapı değildir."
    const val VALIDATED_ENTRY = "Tanısal girişle doğrulandı"
    const val EXTENSION_TITLE = "B2+ uzantısı (beceri başına)"
    val extensionLabels: Map<ExtensionState, String> = mapOf(
        // No extension item is authored yet, so this is the only answer the profile can give.
        ExtensionState.NO_EXTENSION_EVIDENCE to "Uzantı kanıtı yok — henüz uzantı içeriği yazılmadı",
        ExtensionState.EXTENSION_PRACTICE_ONLY to "Yalnız uzantı pratiği",
        ExtensionState.EXTENSION_EVIDENCE_PRESENT to "Adı konmuş uzantı kanıtı var",
        ExtensionState.EXTENSION_REVIEW_DUE to "Uzantı kanıtı · tekrar zamanı",
        ExtensionState.EXTENSION_VERIFICATION_DUE to "Uzantı kanıtı · yeniden kontrol gerekli",
    )
    const val NO_CURRICULUM = "Müfredat henüz yüklenmedi."
}

data class BandView(val band: CefrBand, val label: String, val missing: List<Pair<VersionedRef, String>>)

data class EnglishSkillGroup(val band: CefrBand, val label: String, val skills: List<SkillChip>, val sources: Map<VersionedRef, String>)

data class EnglishProfileView(
    val headline: String,
    val unevenNote: String?,
    val historicalNote: String?,
    val bands: List<BandView>,
    val groups: List<EnglishSkillGroup>,
    val extensions: List<Pair<String, String>>,
    val scopeNote: String,
    val gateNote: String,
)

object EnglishProfilePresentation {

    fun of(profile: EnglishProfile?): EnglishProfileView? {
        if (profile == null) return null
        val current = profile.currentCompleteBand
        val headline = when (profile.status) {
            BaseProfileStatus.NOT_YET_COMPLETE -> EnglishProfileCopy.NOT_YET_COMPLETE
            BaseProfileStatus.COMPLETE_CURRENT -> EnglishProfileCopy.complete(current!!)
            BaseProfileStatus.COMPLETE_REVIEW_DUE -> EnglishProfileCopy.completeReviewDue(current!!)
            BaseProfileStatus.COMPLETE_VERIFICATION_DUE -> EnglishProfileCopy.completeVerificationDue(current!!)
        }
        val rows: Map<VersionedRef, SkillProgressRow> = profile.reading.skills.mapNotNull { it.row }.associateBy { it.skill }
        fun name(ref: VersionedRef) = rows[ref]?.name ?: ref.logicalId
        // The band above the complete one, when the profile is uneven: the exact next gap, never a broadcast failure.
        val next = CefrBand.entries.firstOrNull { current == null || it > current }
        val uneven = if ("TECHNICAL_ENGLISH_UNEVEN_PROFILE" in profile.reasonCodes && next != null) EnglishProfileCopy.uneven(current, next) else null
        val historical = profile.historicalHighestBand?.takeIf { current == null || it > current }?.let(EnglishProfileCopy::historical)
        val bands = profile.bands.map { line ->
            BandView(line.band, EnglishProfileCopy.completionLabels.getValue(line.completion), line.missing.map { it to name(it) })
        }
        val groups = CefrBand.entries.mapNotNull { band ->
            val skills = profile.reading.skills.filter { it.band == band }.mapNotNull { it.row }
            if (skills.isEmpty()) null else EnglishSkillGroup(
                band = band,
                label = band.id,
                skills = skills.map(ProgressPresentation::chip),
                sources = skills.mapNotNull { s ->
                    if (profile.confirmationSources[s.skill] == ConfirmationSource.VALIDATED_ENTRY_EVIDENCE) s.skill to EnglishProfileCopy.VALIDATED_ENTRY else null
                }.toMap(),
            )
        }
        return EnglishProfileView(
            headline = headline,
            unevenNote = uneven,
            historicalNote = historical,
            bands = bands,
            groups = groups,
            extensions = profile.extensions.map { name(it.skill) to EnglishProfileCopy.extensionLabels.getValue(it.state) },
            scopeNote = EnglishProfileCopy.SCOPE_NOTE,
            gateNote = EnglishProfileCopy.GATE_NOTE,
        )
    }
}
