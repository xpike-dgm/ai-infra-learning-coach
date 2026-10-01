package coach.engines

import coach.model.FailureRule
import coach.model.MisconceptionRow
import coach.model.MisconceptionSignalState
import coach.model.MisconceptionSource
import coach.model.ObjectiveWeakness
import coach.model.VersionedRef
import coach.model.WeaknessEvent
import coach.model.WeaknessSignal
import coach.model.WrongAnswerFinding

/**
 * Misconception memory (14B, `WAAX-v0 / D-106`) — part of the state family `WLRM-v0` owns (`MSBX-v0`), because
 * the misconception contract is `WLRM-v0`'s own.
 *
 * **A label is never stronger than the evidence it rides on, nor than its source allows.** Each row is attributed
 * by the weakness engine's own rule, in its own order, against the Objective's state at that moment
 * ([WeaknessEngine.rule]); a label the row carries moves exactly as far as that rule would move the Objective —
 * a hypothesis on assisted, provisional or partial work, support on a clean failure before mastery or a first
 * clean contradiction after it, confirmation only when a fresh recheck fails and the mastery engine's gates no
 * longer pass — and **an AI-proposed label stops at a hypothesis** whatever the row (`WLRM-v0`: an LLM may
 * propose a hypothesis but cannot publish a confirmed misconception). Invalid, contested or prerequisite-
 * contaminated work moves no label. A fresh, clean success on the Objective resolves what it can, under the
 * weakness engine's closure rule; time alone resolves nothing.
 *
 * Only catalog labels exist here: memory is replayed for the Objective's declared labels and nothing else.
 * Everything is a pure function of the Objective's evidence, so it can always be rebuilt from truth.
 */
object MisconceptionEngine {

    const val MODEL = "WAAX-v0"
    const val POLICY_VERSION = "WLRM-v0"

    /** How far one row's rule may move a label it carries, before the source's ceiling. `null`: not at all. */
    fun reach(rule: FailureRule?, event: WeaknessEvent): WeaknessSignal? = when (rule) {
        FailureRule.ASSISTED_H1_H4, FailureRule.PROVISIONAL_OR_PARTIAL -> WeaknessSignal.HYPOTHESIS
        FailureRule.CLEAN_PREMASTERY_H0_DIRECT, FailureRule.FIRST_CLEAN_POSTMASTERY_CONTRADICTION -> WeaknessSignal.SUPPORTED
        FailureRule.FRESH_RECHECK_FAIL -> if (!event.masteredAfter) WeaknessSignal.CONFIRMED else WeaknessSignal.SUPPORTED
        else -> null
    }

    /** Replays one Objective's evidence ([events], any order) into the memory of each of its [catalog] labels. */
    fun replay(objective: VersionedRef, skill: VersionedRef, catalog: List<MisconceptionRow>, events: List<WeaknessEvent>): List<MisconceptionSignalState> =
        walk(objective, skill, catalog, events).first

    /**
     * What each row of one Objective said (`WrongAnswerFinding`): how the weakness engine attributed it and where
     * each catalog label it carried stood **after** it. A row no rule speaks for has no outcome.
     */
    fun findings(objective: VersionedRef, skill: VersionedRef, catalog: List<MisconceptionRow>, events: List<WeaknessEvent>): List<WrongAnswerFinding> =
        walk(objective, skill, catalog, events).second

    private fun walk(
        objective: VersionedRef,
        skill: VersionedRef,
        catalog: List<MisconceptionRow>,
        events: List<WeaknessEvent>,
    ): Pair<List<MisconceptionSignalState>, List<WrongAnswerFinding>> {
        val declared = catalog.filter { it.objective == objective }.associateBy { it.ref }
        val states = declared.keys.associateWith { MisconceptionSignalState(it, objective, skill) }.toMutableMap()
        val findings = mutableListOf<WrongAnswerFinding>()
        var weakness = ObjectiveWeakness(objective, skill)
        for (event in events.sortedBy { it.sequence }) {
            val rule = WeaknessEngine.rule(weakness, event)
            if (rule == FailureRule.FRESH_RECOVERY_SUCCESS) {
                states.replaceAll { _, state -> resolve(state, event) }
            } else {
                val reach = reach(rule, event)
                if (reach != null) {
                    // A label the catalog does not declare for this Objective names nothing and moves nothing.
                    event.misconceptionTags.filter { it.misconception in states }.forEach { tag ->
                        val capped = if (reach.strength > tag.source.ceiling.strength) tag.source.ceiling else reach
                        states[tag.misconception] = raise(states.getValue(tag.misconception), event, capped, tag.source)
                    }
                }
            }
            val next = WeaknessEngine.step(weakness, event)
            findings += WrongAnswerFinding(
                evidenceId = event.evidenceId,
                objective = objective,
                outcome = if (rule == null) null else next.lastOutcome,
                misconceptions = event.misconceptionTags.mapNotNull { tag ->
                    declared[tag.misconception]?.let { it to states.getValue(tag.misconception).signal }
                },
            )
            weakness = next
        }
        return states.values.sortedBy { it.misconception.logicalId } to findings
    }

    /** Like the weakness signal: a weaker row adds to an open label, a stronger one escalates it; nothing lowers it. */
    private fun raise(state: MisconceptionSignalState, event: WeaknessEvent, to: WeaknessSignal, source: MisconceptionSource): MisconceptionSignalState {
        val open = state.signal == WeaknessSignal.HYPOTHESIS || state.signal == WeaknessSignal.SUPPORTED || state.signal == WeaknessSignal.CONFIRMED
        val signal = if (!open || to.strength > state.signal.strength) to else state.signal
        val strongest = listOfNotNull(if (open) state.source else null, source).maxBy { it.ceiling.strength }
        return state.copy(
            signal = signal,
            source = strongest,
            signalEvidenceIds = (if (open) state.signalEvidenceIds else emptyList()) + event.evidenceId,
            firstSeenDay = if (open) state.firstSeenDay else event.studyDay,
            lastSeenDay = event.studyDay,
            resolutionEvidenceId = if (open) state.resolutionEvidenceId else null,
        )
    }

    /** `WLRM-v0` §7, as the weakness engine applies it: a confirmed label closes only when the gates pass again. */
    private fun resolve(state: MisconceptionSignalState, event: WeaknessEvent): MisconceptionSignalState {
        val closes = when (state.signal) {
            WeaknessSignal.HYPOTHESIS, WeaknessSignal.SUPPORTED -> true
            WeaknessSignal.CONFIRMED -> event.masteredAfter
            WeaknessSignal.NONE, WeaknessSignal.RESOLVED -> false
        }
        return if (closes) state.copy(signal = WeaknessSignal.RESOLVED, resolutionEvidenceId = event.evidenceId) else state
    }
}
