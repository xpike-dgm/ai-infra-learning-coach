package coach.application

import coach.engines.TransferEngine
import coach.model.LearningNeed
import coach.model.PrerequisiteCandidate
import coach.model.SkillPlanningState
import coach.model.VersionedRef
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * Reads the facts [TransferEngine] decides on (15G, `D-120`): for each learned Skill, whether an authored cross-topic
 * transfer item could measure it now — trusted by the store, admitted by the prerequisite gate, unseen by the learner —
 * and whether a clean transfer measurement already exists. The planner and the monthly composer read the same answer,
 * exactly as they both read the weakness owner's needs (13D): a transfer slot serves a need the planner already opened.
 */
internal object TransferPlanning {

    fun needs(persistence: PersistencePort, content: ContentPort, states: List<SkillPlanningState>): List<LearningNeed> =
        TransferEngine.needs(states, openFor(persistence, content, states))

    private fun openFor(persistence: PersistencePort, content: ContentPort, states: List<SkillPlanningState>): Set<VersionedRef> {
        val gate by lazy { ResolvePrerequisites(persistence) }
        return states.filter(TransferEngine::learned).mapNotNull { state ->
            // Trust is the store's, never the item's own claim (11D).
            val items = content.assessmentItemsFor(state.skill).filter(TransferEngine::isTransferItem)
                .mapNotNull { StoreTrust.apply(persistence, it) }.filter(TransferEngine::trusted)
            if (items.isEmpty()) return@mapNotNull null
            val evidence = persistence.objectivesOf(state.skill).flatMap { persistence.evidenceFor(it.ref) }
            if (TransferEngine.measured(evidence, items.map { it.ref }.toSet())) return@mapNotNull null
            val seen = persistence.exposuresFor(items.map { it.ref }, items.map { it.variantFamilyId }.distinct())
            val solved = seen.filter { it.solutionExposed }.mapNotNull { it.variantFamilyId }.toSet()
            val shown = seen.map { it.resource }.toSet()
            val admitted = items.any { item ->
                item.ref !in shown && item.variantFamilyId !in solved &&
                    !gate.resolve(PrerequisiteCandidate(candidateId = item.ref.toString(), target = state.skill,
                        requiredSkills = item.requiredSkills)).eligibility.waits
            }
            state.skill.takeIf { admitted }
        }.toSet()
    }
}
