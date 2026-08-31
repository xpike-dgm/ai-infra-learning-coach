package coach.engines

/**
 * MSBX-v0 §engine_ownership: each engine owns exactly one state family and never writes
 * another's. Allowing an engine to write someone else's state would quietly reintroduce the
 * second source of truth that every earlier stage forbade.
 *
 * The engines themselves arrive with the features that need them (11–18). This declaration
 * exists so the ownership map has a home in code from the first commit rather than living only
 * in a document.
 */
enum class EngineStateFamily(val engine: String, val owns: String) {
    MASTERY("GRE-v0", "mastery_state"),
    RETENTION("RVR-v0", "retention_state"),
    READINESS("PRG-v0", "prerequisite_readiness"),
    TOPIC("TSM-v0", "topic_state"),
    WEAKNESS("WLRM-v0", "weakness_and_remediation_state"),
    PRIORITY("PBR-v0", "candidate_priority_and_rank"),
    PLAN("PDT-v0", "plan_versions_planned_tasks_and_decision_traces"),
    ENGLISH("TEPM-v0", "technical_english_profile"),
}
