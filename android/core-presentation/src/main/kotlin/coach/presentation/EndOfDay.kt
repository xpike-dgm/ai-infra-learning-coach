package coach.presentation

import coach.model.DayChange
import coach.model.DayInventory
import coach.model.DayRecord

/**
 * The end of the day as a projection (11E).
 *
 * The day summary answers **"what did today actually record?"** — not "how did today go". It is the
 * same canonical truth Progress projects, scoped to one study day, so it uses `SPWX-v0`'s
 * presentation states and counting rules rather than a vocabulary of its own.
 *
 * What it can never say is the point: no day succeeded or failed, no streak, no percentage, no
 * minutes studied as progress, no debt carried into tomorrow, and no claim that activity was
 * learning. Those are not discouraged here — there is no field to put them in.
 */

/** `SPWX-v0`'s presentation states, in its order. The day summary is a projection like any other. */
enum class DaySummaryState(val id: String) {
    LOADING_PROJECTION("loading_projection"),
    RECOMPUTING_PROJECTION("recomputing_projection"),
    READY_WITH_EVIDENCE("ready_with_evidence"),
    EMPTY_NO_EVIDENCE_YET("empty_no_evidence_yet"),
    EMPTY_NO_ATTENTION_NEEDED("empty_no_attention_needed"),
    PARTIAL_PROJECTION_AVAILABLE("partial_projection_available"),
    OFFLINE_LOCAL_CAPABLE("offline_local_capable"),
    AI_UNAVAILABLE_FULL_STATE_AVAILABLE("ai_unavailable_full_state_available"),
    ERROR_RECOVERABLE("error_recoverable"),
    DATA_RECOVERY_REQUIRED("data_recovery_required"),
}

/** Tones copied from `VDSX-v0`'s accepted map. An empty day is neutral, because it is not a failure. */
val DaySummaryState.tone: Tone
    get() = when (this) {
        DaySummaryState.PARTIAL_PROJECTION_AVAILABLE -> Tone.PENDING_UNRESOLVED
        DaySummaryState.ERROR_RECOVERABLE, DaySummaryState.DATA_RECOVERY_REQUIRED -> Tone.SYSTEM_FAULT
        DaySummaryState.LOADING_PROJECTION,
        DaySummaryState.RECOMPUTING_PROJECTION,
        DaySummaryState.READY_WITH_EVIDENCE,
        DaySummaryState.EMPTY_NO_EVIDENCE_YET,
        DaySummaryState.EMPTY_NO_ATTENTION_NEEDED,
        DaySummaryState.OFFLINE_LOCAL_CAPABLE,
        DaySummaryState.AI_UNAVAILABLE_FULL_STATE_AVAILABLE -> Tone.NEUTRAL
    }

/**
 * Everything the day summary is allowed to know. Each field is something an owner already decided:
 * the inventory is what the store really holds, the changes are what canonical engines reported
 * (12), health is `APHX-v0`'s and the study day is the clock's.
 */
data class DaySummaryInput(
    val record: DayRecord,
    val health: AppHealth,
    val loading: Boolean = false,
    /** A kind that could not be read at all — the summary says so instead of counting it as zero. */
    val unreadKinds: Set<String> = emptySet(),
)

/**
 * What the end of the day renders. There is no `streak`, `percent`, `minutes`, `score`, `goal`,
 * `debt` or `carriedOver` field, and no place to add one without changing this type.
 */
data class DaySummaryView(
    val state: DaySummaryState,
    val studyDay: String,
    val inventory: DayInventory,
    val changes: List<DayChange>,
    val unreadKinds: Set<String>,
    val contexts: List<CrossCuttingState>,
) {
    /** Counts are inventory and say so, per `SPWX-v0`; they are never divided into a ratio. */
    val countsAreLabelledInventory: Boolean get() = inventory.isLabelledInventory

    /** An empty day is not a failed day. It is a day with nothing recorded, and that is all. */
    val isFailure: Boolean get() = false
}

object EndOfDay {

    /**
     * Precedence follows `THUX-v0`'s order and `APHX-v0`'s rule that a store that cannot be trusted
     * supersedes normal presentation: recovery, then a recoverable fault, then loading, then a
     * partial read, then the honest empty day, then the day that recorded something.
     *
     * `recomputing_projection` and `empty_no_attention_needed` are `SPWX-v0` states this surface
     * cannot produce yet: the first needs a projection being rebuilt and the second needs the
     * attention model, both owned by 12 and 16. They are named here, unproduced, rather than
     * quietly dropped from the vocabulary.
     */
    fun of(input: DaySummaryInput): DaySummaryView {
        val contexts = input.health.contexts
        val blocking = input.health.blocking
        val state = when {
            // The two system conditions are APHX-v0's and are reported, never re-decided here.
            blocking == CrossCuttingState.DATA_RECOVERY_REQUIRED -> DaySummaryState.DATA_RECOVERY_REQUIRED
            blocking == CrossCuttingState.ERROR_RECOVERABLE -> DaySummaryState.ERROR_RECOVERABLE
            blocking != null || input.loading -> DaySummaryState.LOADING_PROJECTION
            input.unreadKinds.isNotEmpty() -> DaySummaryState.PARTIAL_PROJECTION_AVAILABLE
            !input.record.inventory.recordedAnything && input.record.changes.isEmpty() ->
                DaySummaryState.EMPTY_NO_EVIDENCE_YET
            else -> DaySummaryState.READY_WITH_EVIDENCE
        }
        return DaySummaryView(
            state = state,
            studyDay = input.record.studyDay,
            // A projection that could not be read fully reports what it has and says so; it does not
            // present a partial count as the day's inventory.
            inventory = if (state == DaySummaryState.LOADING_PROJECTION) DayInventory(emptyMap()) else input.record.inventory,
            // A change is claimed only when a canonical engine reported one. With no evidence
            // pipeline yet (12), this is empty and the summary says plainly that nothing changed.
            changes = input.record.changes,
            unreadKinds = input.unreadKinds,
            contexts = contexts,
        )
    }
}

/**
 * The day summary's fixed sentences. Their wording is 14's; their meaning is canonical.
 */
object DayCopy {
    const val NOTHING_RECORDED =
        "Bugün kayda geçen bir çalışma yok. Bu bir başarısızlık değil ve yarına borç bırakmaz."

    const val RECORDED_IS_NOT_PROGRESS =
        "Aşağıdakiler bugün kaydedilenlerin dökümü; ilerleme ölçüsü değil. Neyin kanıtlandığına " +
            "kanıt değerlendirmesi karar verir."

    const val NOTHING_CHANGED =
        "Kanonik durumda bugün bir değişiklik olmadı. Buradan bir ilerleme iddiası üretilmiyor."

    const val PARTIAL =
        "Bugünün dökümü eksik okundu. Okunamayan kısım sıfır sayılmadı."

    const val TOMORROW =
        "Yarın bugünden devralınan bir borç yok: plan yarının durumundan yeniden kurulur."
}
