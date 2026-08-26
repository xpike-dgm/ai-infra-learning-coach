from __future__ import annotations

from collections import defaultdict
from pathlib import Path
from typing import Any
import yaml

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "curriculum" / "decomposition" / "6g_weakness_remediation"
TODAY = "2026-08-27"
PACKAGES = [
    ("6c", ROOT / "curriculum/decomposition/6c_foundations"),
    ("6d", ROOT / "curriculum/decomposition/6d_systems"),
    ("6e", ROOT / "curriculum/decomposition/6e_gpu_ml_inference"),
    ("6f", ROOT / "curriculum/decomposition/6f_professional_engineering"),
]


def load(path: Path) -> Any:
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def dump(name: str, data: Any) -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / name).write_text(
        yaml.safe_dump(data, sort_keys=False, allow_unicode=True, width=120),
        encoding="utf-8",
    )


skills: list[dict[str, Any]] = []
objectives: list[dict[str, Any]] = []
edges: list[dict[str, Any]] = []
skill_source: dict[str, str] = {}
objective_source: dict[str, str] = {}

for package_id, directory in PACKAGES:
    for row in load(directory / "skills.yaml"):
        sid = row["skill_id_candidate"]
        if sid in skill_source:
            raise SystemExit(f"duplicate accepted Skill across source packages: {sid}")
        skill_source[sid] = package_id
        skills.append(row)
    for row in load(directory / "objectives.yaml"):
        oid = row["objective_id_candidate"]
        if oid in objective_source:
            raise SystemExit(f"duplicate accepted Objective across source packages: {oid}")
        objective_source[oid] = package_id
        objectives.append(row)
    edges.extend(load(directory / "prerequisite_edges.yaml"))

skill_by_id = {x["skill_id_candidate"]: x for x in skills}
objective_by_id = {x["objective_id_candidate"]: x for x in objectives}
objectives_by_skill: dict[str, list[dict[str, Any]]] = defaultdict(list)
for row in objectives:
    owner = row["owner_skill_id"]
    if owner not in skill_by_id:
        raise SystemExit(f"Objective owner outside accepted registry: {row['objective_id_candidate']} -> {owner}")
    objectives_by_skill[owner].append(row)

hard_dependents: dict[str, set[str]] = defaultdict(set)
hard_prereqs: dict[str, set[str]] = defaultdict(set)
for row in edges:
    if row.get("edge_kind") == "hard":
        source = row["prerequisite_skill_id"]
        target = row["target_skill_id"]
        if source in skill_by_id and target in skill_by_id:
            hard_dependents[source].add(target)
            hard_prereqs[target].add(source)

SOURCE_CATALOG = [
    {
        "source_id": "source.repo.learning_behavior",
        "source_kind": "canonical_spec",
        "title": "Learning Behavior Rules",
        "ref": "docs/LEARNING_BEHAVIOR_RULES.md",
        "supports": ["failure_interpretation", "targeted_remediation", "misconception_memory"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
    {
        "source_id": "source.repo.gre_v0",
        "source_kind": "canonical_spec",
        "title": "GRE-v0 Gated Recent Evidence",
        "ref": "docs/MASTERY_FORMULA_V0.md",
        "supports": ["negative_evidence", "verification_hysteresis", "objective_skill_gates"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
    {
        "source_id": "source.repo.rvr_v0",
        "source_kind": "canonical_spec",
        "title": "RVR-v0 Retention Verification & Risk",
        "ref": "docs/RETENTION_FORGETTING_SPEC.md",
        "supports": ["verification_due", "at_risk", "retention_remediation"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
    {
        "source_id": "source.repo.prg_v0",
        "source_kind": "canonical_spec",
        "title": "PRG-v0 Prerequisite Readiness Gate",
        "ref": "docs/PREREQUISITE_POLICY_SPEC.md",
        "supports": ["prerequisite_contamination", "branch_local_blocking"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
    {
        "source_id": "source.repo.task_taxonomy",
        "source_kind": "canonical_spec",
        "title": "Planner Task Taxonomy & TaskCandidate Contract",
        "ref": "docs/TASK_TAXONOMY_SPEC.md",
        "supports": ["learning_need", "remediate_purpose", "evidence_contract"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
    {
        "source_id": "source.repo.pbr_v0",
        "source_kind": "canonical_spec",
        "title": "PBR-v0 Priority Bands & Rank Vector",
        "ref": "docs/PRIORITY_POLICY_SPEC.md",
        "supports": ["repair_priority", "integrity_blocker", "capacity_safe_repair"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
    {
        "source_id": "source.repo.topic_state",
        "source_kind": "canonical_spec",
        "title": "Topic State Machine",
        "ref": "docs/TOPIC_STATE_MACHINE.md",
        "supports": ["derived_topic_state", "broad_reset_guard"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
    {
        "source_id": "source.repo.stage6_charter",
        "source_kind": "canonical_spec",
        "title": "Granular Capability Map Plan",
        "ref": "docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
        "supports": ["6g_scope", "weakness_localization"],
        "freshness_class": "evergreen",
        "checked_at": TODAY,
    },
]
for package_id, directory in PACKAGES:
    SOURCE_CATALOG.append({
        "source_id": f"source.repo.{package_id}_accepted_registry",
        "source_kind": "accepted_authoring_dataset",
        "title": f"Accepted {package_id.upper()} Skill/Objective registry",
        "ref": str(directory.relative_to(ROOT)),
        "supports": ["skill_registry", "objective_registry", "remediation_metadata"],
        "freshness_class": "versioned_dataset",
        "checked_at": TODAY,
    })

STATE_CONTRACT = {
    "model_id": "WLRM-v0",
    "principle": "Weakness is localized from attributable evidence at Objective/Skill level; broad Topic/Domain weakness is derived summary only.",
    "attempt_attribution_outcomes": [
        "not_attributable",
        "content_or_environment_issue",
        "prerequisite_signal",
        "objective_weakness_hypothesis",
        "objective_weakness_supported",
        "verification_due",
        "remediation_required",
        "positive_recovery_evidence",
    ],
    "weakness_signal_states": ["none", "hypothesis", "supported", "confirmed", "resolved"],
    "invariants": [
        "attempt_failure_is_not_automatically_skill_failure",
        "invalid_or_prerequisite_contaminated_attempt_never_writes_target_negative_evidence",
        "assisted_or_provisional_failure_cannot_directly_confirm_remediation_required",
        "first_clean_post_mastery_contradiction_opens_verification_due_before_mastery_erasure",
        "confirmed_repeated_failure_is_recomputed_through_GRE_not_manual_score_override",
        "remediation_closure_requires_new_evidence_not_task_completion",
        "topic_domain_states_are_derived_and_never_the_primary_remediation_target",
        "dependent_branch_blocking_is_skill_local_and_prerequisite_aware",
        "review_due_without_negative_evidence_is_not_weakness",
        "global_project_failure_does_not_propagate_to_all_tagged_objectives",
    ],
    "source_refs": [
        "source.repo.learning_behavior", "source.repo.gre_v0", "source.repo.rvr_v0",
        "source.repo.prg_v0", "source.repo.task_taxonomy", "source.repo.pbr_v0", "source.repo.topic_state",
    ],
}

STRATEGIES = [
    {
        "strategy_id": "strategy.targeted_reteach",
        "name": "Targeted reteach",
        "purpose": "Repair the exact conceptual/procedural gap without resetting the parent Topic or Domain.",
        "activity_kinds": ["content_explanation", "explanation_justification"],
        "assistance_policy": "guided_allowed",
        "evidence_ceiling": "corroborating",
    },
    {
        "strategy_id": "strategy.worked_example",
        "name": "Worked example reconstruction",
        "purpose": "Show and reconstruct one prerequisite-valid solution path before independent retry.",
        "activity_kinds": ["worked_example", "code_reading_trace"],
        "assistance_policy": "guided_allowed",
        "evidence_ceiling": "corroborating",
    },
    {
        "strategy_id": "strategy.micro_drill",
        "name": "Focused micro drill",
        "purpose": "Practice only the localized sub-behavior with bounded scope.",
        "activity_kinds": ["recognition_selection", "recall_free_response", "coding_production"],
        "assistance_policy": "guided_allowed_then_independent",
        "evidence_ceiling": "direct_if_H0_valid_verified",
    },
    {
        "strategy_id": "strategy.state_trace_reconstruction",
        "name": "State/trace reconstruction",
        "purpose": "Externalize intermediate state, control/data flow, shape or lifecycle transitions to repair a reasoning gap.",
        "activity_kinds": ["code_reading_trace", "explanation_justification"],
        "assistance_policy": "guided_allowed",
        "evidence_ceiling": "corroborating_or_direct_by_objective_profile",
    },
    {
        "strategy_id": "strategy.debug_localization",
        "name": "Debug localization drill",
        "purpose": "Reproduce symptom, isolate cause, fix and verify in a fresh bug/context.",
        "activity_kinds": ["debugging_diagnosis", "hands_on_system_task"],
        "assistance_policy": "guided_allowed_then_H0_recheck",
        "evidence_ceiling": "direct_if_H0_valid_verified",
    },
    {
        "strategy_id": "strategy.prerequisite_refresh",
        "name": "Prerequisite refresh",
        "purpose": "Repair a real prerequisite gap before retrying the target; never misattribute the prerequisite failure to the target.",
        "activity_kinds": ["content_explanation", "worked_example", "practice"],
        "assistance_policy": "guided_allowed",
        "evidence_ceiling": "belongs_to_prerequisite_objective",
    },
    {
        "strategy_id": "strategy.failure_scenario_replay",
        "name": "Failure scenario replay",
        "purpose": "Recreate a representative failure mode and reason through detection, containment and correction.",
        "activity_kinds": ["debugging_diagnosis", "hands_on_system_task", "transfer_problem"],
        "assistance_policy": "guided_allowed_then_independent",
        "evidence_ceiling": "direct_if_H0_valid_verified",
    },
    {
        "strategy_id": "strategy.measurement_replay",
        "name": "Measurement replay",
        "purpose": "Repeat a controlled measurement/benchmark/profile and repair interpretation or experimental-design errors.",
        "activity_kinds": ["hands_on_system_task", "explanation_justification"],
        "assistance_policy": "guided_allowed_then_independent",
        "evidence_ceiling": "direct_if_H0_valid_verified",
    },
    {
        "strategy_id": "strategy.production_retry",
        "name": "Fresh production retry",
        "purpose": "Re-produce the target artifact independently in a fresh context after repair.",
        "activity_kinds": ["coding_production", "hands_on_system_task", "integrated_project_task"],
        "assistance_policy": "H0_required_for_closure_evidence",
        "evidence_ceiling": "direct",
    },
    {
        "strategy_id": "strategy.explanation_rebuild",
        "name": "Explanation/model rebuild",
        "purpose": "Reconstruct the mental model, tradeoff argument or causal explanation with explicit assumptions.",
        "activity_kinds": ["explanation_justification", "transfer_problem"],
        "assistance_policy": "guided_allowed_then_independent",
        "evidence_ceiling": "direct_if_objective_allows_explanation",
    },
    {
        "strategy_id": "strategy.misconception_contrast",
        "name": "Misconception contrast",
        "purpose": "Contrast the learner's suspected misconception against a correct counterexample without treating the hypothesis as fact.",
        "activity_kinds": ["worked_example", "recognition_selection", "explanation_justification"],
        "assistance_policy": "guided_allowed",
        "evidence_ceiling": "corroborating",
    },
    {
        "strategy_id": "strategy.transfer_retest",
        "name": "Fresh transfer retest",
        "purpose": "Verify repaired capability in a materially different unseen context when transfer is required.",
        "activity_kinds": ["transfer_problem", "integrated_project_task"],
        "assistance_policy": "H0_required_for_closure_evidence",
        "evidence_ceiling": "direct",
    },
    {
        "strategy_id": "strategy.retrieval_reinforcement",
        "name": "Retrieval reinforcement",
        "purpose": "Use short retrieval/reconstruction when concern is retention-oriented but confirmed remediation is not yet warranted.",
        "activity_kinds": ["recall_free_response", "code_reading_trace", "transfer_problem"],
        "assistance_policy": "H0_preferred",
        "evidence_ceiling": "direct_if_RVR_conditions_hold",
    },
    {
        "strategy_id": "strategy.tool_workflow_rehearsal",
        "name": "Tool/workflow rehearsal",
        "purpose": "Repeat a bounded professional or tool operation where the operation itself is the target capability.",
        "activity_kinds": ["hands_on_system_task", "integrated_project_task"],
        "assistance_policy": "guided_allowed_then_independent",
        "evidence_ceiling": "direct_if_tool_use_is_target",
    },
    {
        "strategy_id": "strategy.fresh_independent_recheck",
        "name": "Fresh independent recheck",
        "purpose": "Close verification/remediation only with prerequisite-valid, fresh, H0, direct and verified evidence appropriate to the Objective.",
        "activity_kinds": ["assessment_by_objective_profile"],
        "assistance_policy": "H0_required",
        "evidence_ceiling": "direct",
    },
]
STRATEGY_IDS = {x["strategy_id"] for x in STRATEGIES}


def add_once(seq: list[str], value: str) -> None:
    if value not in seq:
        seq.append(value)


def strategies_for(tags: list[str], direct_types: list[str], required_direct_type: str | None, requires_transfer: bool) -> list[str]:
    result: list[str] = []
    all_text = " ".join(tags).lower()
    for tag in tags:
        t = tag.lower()
        if "prereq" in t or "foundation" in t:
            add_once(result, "strategy.prerequisite_refresh")
        if "trace" in t or "state" in t or "shape" in t:
            add_once(result, "strategy.state_trace_reconstruction")
        if "debug" in t or "localiz" in t or "diagnos" in t:
            add_once(result, "strategy.debug_localization")
        if "failure" in t or "incident" in t or "scenario" in t:
            add_once(result, "strategy.failure_scenario_replay")
        if "measure" in t or "benchmark" in t or "profile" in t or "latency" in t:
            add_once(result, "strategy.measurement_replay")
        if "worked" in t or "example" in t:
            add_once(result, "strategy.worked_example")
        if "micro" in t or "drill" in t or "practice" in t:
            add_once(result, "strategy.micro_drill")
        if "misconception" in t or "contrast" in t:
            add_once(result, "strategy.misconception_contrast")
        if "explain" in t or "reason" in t or "model" in t:
            add_once(result, "strategy.explanation_rebuild")
        if "tool" in t or "workflow" in t or "command" in t or "config" in t:
            add_once(result, "strategy.tool_workflow_rehearsal")
        if "reteach" in t:
            add_once(result, "strategy.targeted_reteach")
        if "retrieval" in t or "retention" in t:
            add_once(result, "strategy.retrieval_reinforcement")

    types = set(direct_types)
    required = required_direct_type or ""
    if any(x in required or x in types for x in ["debugging", "debugging_diagnosis"]):
        add_once(result, "strategy.debug_localization")
    if required in {"coding", "coding_production", "hands_on_system_task", "hands_on_workflow", "component_artifact", "measurement_artifact"} or types & {
        "coding", "coding_production", "hands_on_system_task", "hands_on_workflow", "component_artifact", "measurement_artifact"
    }:
        add_once(result, "strategy.production_retry")
    if required in {"measurement", "measurement_artifact", "artifact_analysis", "system_observation"} or types & {
        "measurement", "measurement_artifact", "artifact_analysis", "system_observation"
    }:
        add_once(result, "strategy.measurement_replay")
    if required in {"explanation", "design_argument"} or types & {"explanation", "design_argument"}:
        add_once(result, "strategy.explanation_rebuild")
    if not result or "targeted_reteach" in all_text:
        add_once(result, "strategy.targeted_reteach")
    if requires_transfer:
        add_once(result, "strategy.transfer_retest")
    add_once(result, "strategy.fresh_independent_recheck")
    return result


FAILURE_RULES = [
    {
        "rule_id": "failure.invalid_or_ambiguous",
        "priority": 10,
        "when": ["evaluator_status=invalid OR item/task ambiguous OR attribution invalid"],
        "target_effect": "not_attributable",
        "weakness_signal": "none",
        "gre_effect": "exclude_from_mastery_evidence",
        "learning_need_effect": "none_for_target",
        "qa_effect": "content_or_evaluator_review",
    },
    {
        "rule_id": "failure.prerequisite_contamination",
        "priority": 20,
        "when": ["required hard/task prerequisite not ready", "attempt nevertheless executed"],
        "target_effect": "not_attributable",
        "weakness_signal": "none_for_target",
        "gre_effect": "no_target_negative_evidence",
        "learning_need_effect": "open_or_update_prerequisite_need",
        "qa_effect": "TASK_PREREQUISITE_METADATA_INVALID when metadata was wrong",
    },
    {
        "rule_id": "failure.unattempted_or_deferred",
        "priority": 30,
        "when": ["candidate not selected OR task deferred OR user did not attempt target behavior"],
        "target_effect": "not_attributable",
        "weakness_signal": "none",
        "gre_effect": "none",
        "learning_need_effect": "existing_need_may_remain_open",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.environment_outside_target",
        "priority": 40,
        "when": ["tool/environment/infrastructure failure prevented observation and tool operation was not target behavior"],
        "target_effect": "content_or_environment_issue",
        "weakness_signal": "none",
        "gre_effect": "no_target_negative_evidence",
        "learning_need_effect": "repair_environment_or_choose_alternative_candidate",
        "qa_effect": "environment_or_task_review",
    },
    {
        "rule_id": "failure.assisted_h1_h4",
        "priority": 50,
        "when": ["target failure or dependence observed with assistance H1-H4"],
        "target_effect": "objective_weakness_hypothesis",
        "weakness_signal": "hypothesis",
        "gre_effect": "not_positive_independent_mastery_and_not_direct_negative_override",
        "learning_need_effect": "weakness_detected_or_fresh_independent_recheck",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.provisional_or_partial",
        "priority": 60,
        "when": ["evaluator_status=provisional OR result materially partial/uncertain"],
        "target_effect": "objective_weakness_hypothesis",
        "weakness_signal": "hypothesis",
        "gre_effect": "do_not_confirm_mastery_or_heavy_remediation",
        "learning_need_effect": "fresh_verification_or_targeted_reinforcement",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.clean_premastery_h0_direct",
        "priority": 70,
        "when": ["H0", "direct", "verified", "prerequisite-valid", "target not currently mastered", "meaningful negative/partial quality"],
        "target_effect": "objective_weakness_supported",
        "weakness_signal": "supported",
        "gre_effect": "enter_normal_recent_evidence_window",
        "learning_need_effect": "weakness_detected_with_targeted_repair_candidate",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.first_clean_postmastery_contradiction",
        "priority": 80,
        "when": ["H0", "direct", "verified", "prerequisite-valid", "target previously/currently mastered", "first meaningful clean contradiction"],
        "target_effect": "verification_due",
        "weakness_signal": "supported_not_confirmed",
        "gre_effect": "open_contradiction_flag_without_immediate_mastery_erasure",
        "learning_need_effect": "verification_due",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.fresh_recheck_fail",
        "priority": 90,
        "when": ["verification_due already open", "fresh/unseen H0 direct verified prerequisite-valid recheck fails"],
        "target_effect": "remediation_required",
        "weakness_signal": "confirmed",
        "gre_effect": "recompute_objective_and_skill_gates_from_evidence",
        "learning_need_effect": "remediation_required",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.integrated_global_outcome_guard",
        "priority": 100,
        "when": ["project/integrated task has global failure or success"],
        "target_effect": "component_attribution_only",
        "weakness_signal": "only_for_structurally_essential_separately_observable_component",
        "gre_effect": "no_broadcast_to_tagged_objectives",
        "learning_need_effect": "component_specific_only",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.review_due_without_negative_evidence",
        "priority": 110,
        "when": ["retention_state=review_due", "no clean negative evidence"],
        "target_effect": "no_weakness",
        "weakness_signal": "none",
        "gre_effect": "mastery_unchanged",
        "learning_need_effect": "retention_review_due_not_remediation",
        "qa_effect": "none",
    },
    {
        "rule_id": "failure.fresh_recovery_success",
        "priority": 120,
        "when": ["fresh H0 direct verified prerequisite-valid evidence satisfies current Objective gate/recheck"],
        "target_effect": "positive_recovery_evidence",
        "weakness_signal": "resolve_or_recompute",
        "gre_effect": "normal_GRE_RVR_recompute",
        "learning_need_effect": "close_only_when_resolved_condition_true",
        "qa_effect": "none",
    },
]

LEARNING_NEED_MAPPINGS = [
    {
        "mapping_id": "need.map.weakness_hypothesis",
        "source_condition": "objective weakness hypothesis from assisted/provisional/partial signal",
        "trigger_kind": "weakness_detected",
        "default_priority_band": "P1_or_P2_by_evidence_severity",
        "primary_purposes": ["diagnose", "reinforce", "remediate"],
        "closure": "fresh attributable evidence resolves hypothesis or escalates it",
    },
    {
        "mapping_id": "need.map.supported_premastery_weakness",
        "source_condition": "clean attributable pre-mastery H0 direct failure",
        "trigger_kind": "weakness_detected",
        "default_priority_band": "P1",
        "primary_purposes": ["remediate", "practice", "assess"],
        "closure": "target Objective returns to normal learning/mastery path with new evidence",
    },
    {
        "mapping_id": "need.map.verification_due",
        "source_condition": "first clean contradiction after mastery",
        "trigger_kind": "verification_due",
        "default_priority_band": "P1",
        "primary_purposes": ["assess", "retain"],
        "closure": "fresh independent recheck resolves contradiction",
    },
    {
        "mapping_id": "need.map.confirmed_remediation",
        "source_condition": "confirmed Objective/Skill gate failure requiring repair",
        "trigger_kind": "remediation_required",
        "default_priority_band": "P1",
        "priority_override": "P0 only when a critical/hard prerequisite integrity condition truly blocks dependent required work",
        "primary_purposes": ["remediate", "practice", "assess"],
        "closure": "new prerequisite-valid evidence restores required Objective/Skill gates; task completion alone never closes",
    },
    {
        "mapping_id": "need.map.prerequisite_gap",
        "source_condition": "failure attributable to missing/unready prerequisite rather than target",
        "trigger_kind": "remediation_required_or_continue_learning_on_prerequisite",
        "default_priority_band": "P0_or_P1_if_blocking_else_normal_prerequisite_priority",
        "primary_purposes": ["remediate", "teach", "practice", "assess"],
        "closure": "prerequisite readiness restored by its own evidence",
    },
    {
        "mapping_id": "need.map.retention_review_only",
        "source_condition": "review_due with no negative evidence",
        "trigger_kind": "retention_review_due",
        "default_priority_band": "P2_if_critical_or_strongly_overdue_else_P3",
        "primary_purposes": ["retain"],
        "closure": "strong delayed verification or valid natural reuse",
    },
]

AGGREGATION_GUARDS = {
    "rules": [
        {
            "guard_id": "guard.no_domain_reset",
            "rule": "A failure localized to one Objective/Skill cannot reset or reteach an entire Domain by default.",
        },
        {
            "guard_id": "guard.no_topic_broadcast",
            "rule": "Topic weakening/remediation_required is derived from required Skill evidence; Topic state never broadcasts weakness back to all linked Skills.",
        },
        {
            "guard_id": "guard.no_skill_broadcast_from_one_incidental_objective",
            "rule": "One Objective failure affects Skill state only through GRE required/critical gates and hysteresis; no manual whole-Skill fail flag bypasses GRE.",
        },
        {
            "guard_id": "guard.no_project_broadcast",
            "rule": "Global project pass/fail cannot write evidence or weakness to every tagged Objective; component must be structurally essential and separately observable.",
        },
        {
            "guard_id": "guard.branch_local_prerequisite_effect",
            "rule": "Confirmed prerequisite weakness blocks only dependent hard-prerequisite work; independent branches remain schedulable.",
        },
        {
            "guard_id": "guard.review_due_not_weakness",
            "rule": "Time-based review_due is maintenance scheduling, not negative learner evidence.",
        },
        {
            "guard_id": "guard.english_not_global_failure",
            "rule": "Unknown Technical English cannot be silently interpreted as technical Skill weakness unless it is an explicit fair task prerequisite.",
        },
    ]
}

MISCONCEPTION_CONTRACT = {
    "entity": "LearnerMisconceptionSignal",
    "identity_scope": "learner_specific_runtime_state_not_curriculum_skill_identity",
    "fields": [
        "signal_id", "learner_id", "skill_id", "objective_id?", "misconception_code_or_label",
        "state", "evidence_refs", "first_seen_at", "last_seen_at", "resolution_evidence_refs",
    ],
    "states": ["hypothesis", "supported", "confirmed", "resolved"],
    "rules": [
        "LLM_may_propose_a_hypothesis_but_cannot_publish_confirmed_misconception_without_evidence_policy",
        "misconception_memory_guides_remediation_and_item_selection_but_does_not_directly_set_mastery",
        "a_label_may_be_merged_or_renamed_without_changing_canonical_Skill_identity",
        "resolved_requires_subsequent_evidence_or_explicit_runtime_resolution_not_time_alone",
    ],
}

skill_profiles: list[dict[str, Any]] = []
objective_profiles: list[dict[str, Any]] = []
remediation_routes: list[dict[str, Any]] = []
all_source_tags: set[str] = set()

for sid in sorted(skill_by_id):
    skill = skill_by_id[sid]
    owned = sorted(objectives_by_skill[sid], key=lambda x: x["objective_id_candidate"])
    if not owned:
        raise SystemExit(f"accepted Skill without Objective: {sid}")
    tags = sorted(set(skill.get("remediation_tags") or []))
    all_source_tags.update(tags)
    skill_profiles.append({
        "skill_id": sid,
        "source_package": skill_source[sid],
        "objective_ids": [x["objective_id_candidate"] for x in owned],
        "required_objective_ids": [x["objective_id_candidate"] for x in owned if x.get("requirement_role") == "required"],
        "critical_objective_ids": [x["objective_id_candidate"] for x in owned if x.get("criticality") == "critical"],
        "diagnostic_eligibility": skill.get("diagnostic_eligibility"),
        "retention_profile": skill.get("retention_profile"),
        "critical_prerequisite_candidate": bool(skill.get("critical_prerequisite_candidate")),
        "hard_prerequisite_skill_ids": sorted(hard_prereqs.get(sid, set())),
        "hard_dependent_skill_ids": sorted(hard_dependents.get(sid, set())),
        "source_remediation_tags": tags,
        "localization_unit": "objective_first_then_skill_gate",
        "broad_parent_reset_allowed": False,
        "weakness_confirmation_rule": "Use Objective-attributable evidence and GRE/RVR gates; never set Skill weakness from a broad Topic/Domain label.",
        "resolution_rule": "Recompute required/critical Objective gates and unresolved verification after new evidence; task completion alone is insufficient.",
    })

for oid in sorted(objective_by_id):
    obj = objective_by_id[oid]
    sid = obj["owner_skill_id"]
    profile = obj["evidence_profile"]
    tags = sorted(set(obj.get("remediation_tags") or []))
    all_source_tags.update(tags)
    direct_types = list(profile.get("direct_evidence_types") or [])
    strategies = strategies_for(tags, direct_types, profile.get("required_direct_type"), bool(profile.get("requires_transfer")))
    unknown = set(strategies) - STRATEGY_IDS
    if unknown:
        raise SystemExit(f"unknown strategy generated for {oid}: {sorted(unknown)}")
    objective_profiles.append({
        "objective_id": oid,
        "owner_skill_id": sid,
        "source_package": objective_source[oid],
        "requirement_role": obj.get("requirement_role"),
        "criticality": obj.get("criticality"),
        "diagnostic_eligibility": obj.get("diagnostic_eligibility"),
        "observable_action": obj.get("observable_action"),
        "required_direct_type": profile.get("required_direct_type"),
        "direct_evidence_types": direct_types,
        "requires_transfer": bool(profile.get("requires_transfer")),
        "requires_user_authored_artifact": bool(profile.get("requires_user_authored_artifact")),
        "source_remediation_tags": tags,
        "candidate_strategy_ids": strategies,
        "localization_unit": "objective",
        "broad_parent_reset_allowed": False,
        "failure_confirmation": {
            "assisted_or_provisional": "hypothesis_only",
            "clean_premastery_H0_direct_verified": "supported_weakness_and_normal_GRE_negative_evidence",
            "first_clean_postmastery_contradiction": "verification_due",
            "failed_fresh_recheck_or_GRE_gate_failure": "confirmed_remediation_required",
        },
        "recheck_contract": {
            "fresh_variant_or_context_required": True,
            "H0_required_for_closure": True,
            "prerequisite_valid_required": True,
            "direct_evidence_required": True,
            "evaluator_verified_required": True,
            "required_direct_type": profile.get("required_direct_type"),
            "transfer_required_when_objective_requires_transfer": bool(profile.get("requires_transfer")),
            "user_authored_artifact_required_when_objective_requires_it": bool(profile.get("requires_user_authored_artifact")),
        },
    })
    remediation_routes.append({
        "route_id": "route.remediation." + oid.removeprefix("objective."),
        "objective_id": oid,
        "skill_id": sid,
        "entry_signals": ["hypothesis", "supported", "verification_due", "confirmed"],
        "candidate_strategy_ids": strategies,
        "precondition_checks": ["target_attribution_valid", "prerequisite_valid", "task_content_valid"],
        "planner_mapping_ids": [
            "need.map.weakness_hypothesis",
            "need.map.supported_premastery_weakness",
            "need.map.verification_due",
            "need.map.confirmed_remediation",
        ],
        "closure_conditions": [
            "fresh_evidence_processed_by_GRE_RVR",
            "objective_required_gate_satisfied_when_applicable",
            "no_unresolved_verification_due_for_target",
            "skill_gate_recomputed_if_skill_remediation_was_active",
        ],
        "forbidden_shortcuts": [
            "task_completion_closes_remediation",
            "exact_same_item_immediate_retest_as_strong_evidence",
            "broad_topic_or_domain_reset",
            "manual_mastery_score_override",
        ],
    })

TAG_CATALOG = []
for tag in sorted(all_source_tags):
    inferred = strategies_for([tag], [], None, False)
    TAG_CATALOG.append({
        "source_remediation_tag": tag,
        "mapped_strategy_ids": inferred,
        "mapping_kind": "deterministic_tag_bridge_with_objective_evidence_overlay",
    })

REVIEW_QUEUE = [
    {
        "review_id": "review.6g.external_behavior_coverage",
        "severity": "non_blocking",
        "question": "Independent learning-science/engineering sources reveal a missing weakness-localization or remediation safety behavior?",
        "decision_inputs_required": ["independent Research AI", "authoritative learning/assessment sources"],
        "resolution_owner_step": "6H",
        "status": "open",
    },
    {
        "review_id": "review.6g.misconception_taxonomy_expansion",
        "severity": "non_blocking",
        "question": "Which domain-specific misconception labels should be production-authored beyond the generic runtime misconception contract?",
        "decision_inputs_required": ["real learner errors", "AI tutor/error-analysis design"],
        "resolution_owner_step": "14B",
        "status": "open",
    },
    {
        "review_id": "review.6g.calibration",
        "severity": "non_blocking",
        "question": "Which supported-vs-confirmed escalation heuristics and remediation burden require pilot calibration?",
        "decision_inputs_required": ["pilot evidence", "false-positive/false-negative remediation analysis"],
        "resolution_owner_step": "18C",
        "status": "open",
    },
    {
        "review_id": "review.6g.content_realization",
        "severity": "non_blocking",
        "question": "Which remediation strategy templates need prevalidated production content for V1 and later full-route expansion?",
        "decision_inputs_required": ["AŞAMA 15 authoring", "AŞAMA 20 full curriculum expansion"],
        "resolution_owner_step": "15/20",
        "status": "open",
    },
]

counts = {
    "source_packages": len(PACKAGES),
    "skills_covered": len(skill_profiles),
    "objectives_covered": len(objective_profiles),
    "failure_attribution_rules": len(FAILURE_RULES),
    "remediation_strategies": len(STRATEGIES),
    "remediation_routes": len(remediation_routes),
    "learning_need_mappings": len(LEARNING_NEED_MAPPINGS),
    "source_remediation_tags": len(TAG_CATALOG),
    "open_non_blocking_reviews": len(REVIEW_QUEUE),
    "open_blocking_reviews": 0,
}

manifest = {
    "package_id": "decomposition.6g_weakness_remediation",
    "model_id": "WLRM-v0",
    "stage_step": "6G",
    "status": "authoring_complete_internal_qa",
    "decision_candidate": "D-061",
    "scope": "Cross-route weakness localization and remediation overlay for accepted 6C-6F Skill/Objective registries.",
    "base_package_refs": ["decomposition.6c_foundations", "decomposition.6d_systems", "decomposition.6e_gpu_ml_inference", "decomposition.6f_professional_engineering"],
    "adds_new_curriculum_skills": False,
    "adds_new_curriculum_objectives": False,
    "included_collections": [
        "sources.yaml", "state_contract.yaml", "skill_weakness_profiles.yaml", "objective_weakness_profiles.yaml",
        "failure_attribution_rules.yaml", "remediation_strategies.yaml", "remediation_routes.yaml",
        "learning_need_mappings.yaml", "aggregation_guards.yaml", "misconception_contract.yaml", "tag_catalog.yaml",
        "review_queue.yaml", "qa_report.yaml",
    ],
    "counts": counts,
    "external_research_qa": {"status": "pending", "owner_step": "6H"},
    "checked_at": TODAY,
}

qa = {
    "package_id": manifest["package_id"],
    "model_id": "WLRM-v0",
    "result": "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS",
    "checked_at": TODAY,
    "counts": counts,
    "checks": [
        {"check": "accepted_registry_coverage", "result": "PASS", "details": f"{len(skill_profiles)} Skills / {len(objective_profiles)} Objectives covered exactly"},
        {"check": "no_new_curriculum_identity", "result": "PASS", "details": "6G adds overlay metadata only; no new Skill/Objective identity"},
        {"check": "objective_first_localization", "result": "PASS", "details": "Every Objective has a weakness profile and remediation route"},
        {"check": "broad_overreaction_guard", "result": "PASS", "details": "No profile permits broad parent reset"},
        {"check": "prerequisite_contamination_guard", "result": "PASS", "details": "Target negative evidence is prohibited when prerequisite contamination exists"},
        {"check": "mastery_hysteresis_guard", "result": "PASS", "details": "First clean post-mastery contradiction maps to verification_due"},
        {"check": "remediation_closure_evidence", "result": "PASS", "details": "Closure requires fresh H0 direct verified prerequisite-valid evidence through GRE/RVR"},
        {"check": "planner_integration", "result": "PASS", "details": "LearningNeed + PBR P0/P1/P2/P3 mappings are explicit"},
        {"check": "project_component_guard", "result": "PASS", "details": "Integrated outcome cannot broadcast weakness/evidence to all tagged Objectives"},
    ],
    "external_research_qa": {"status": "pending", "owner_step": "6H", "required_before_stage6_external_validation": True},
}

# Write deterministic overlay.
dump("manifest.yaml", manifest)
dump("sources.yaml", SOURCE_CATALOG)
dump("state_contract.yaml", STATE_CONTRACT)
dump("skill_weakness_profiles.yaml", skill_profiles)
dump("objective_weakness_profiles.yaml", objective_profiles)
dump("failure_attribution_rules.yaml", FAILURE_RULES)
dump("remediation_strategies.yaml", STRATEGIES)
dump("remediation_routes.yaml", remediation_routes)
dump("learning_need_mappings.yaml", LEARNING_NEED_MAPPINGS)
dump("aggregation_guards.yaml", AGGREGATION_GUARDS)
dump("misconception_contract.yaml", MISCONCEPTION_CONTRACT)
dump("tag_catalog.yaml", TAG_CATALOG)
dump("review_queue.yaml", REVIEW_QUEUE)
dump("qa_report.yaml", qa)

print("WEAKNESS_REMEDIATION_OVERLAY_GENERATED")
print(f"skills={len(skill_profiles)} objectives={len(objective_profiles)} routes={len(remediation_routes)}")
print(f"failure_rules={len(FAILURE_RULES)} strategies={len(STRATEGIES)} tags={len(TAG_CATALOG)}")
print(f"open_blocking_reviews=0 open_non_blocking_reviews={len(REVIEW_QUEUE)}")
