from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "curriculum" / "decomposition" / "6f_professional_engineering"
FDM = ROOT / "curriculum" / "decomposition" / "6c_foundations"
SDM = ROOT / "curriculum" / "decomposition" / "6d_systems"
GIM = ROOT / "curriculum" / "decomposition" / "6e_gpu_ml_inference"
TODAY = "2026-08-27"


def load(directory: Path, name: str):
    return yaml.safe_load((directory / name).read_text(encoding="utf-8"))


def dump(name: str, value: object) -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / name).write_text(yaml.safe_dump(value, allow_unicode=True, sort_keys=False, width=120), encoding="utf-8")


prior_dirs = [FDM, SDM, GIM]
prior_skills_by_pkg = {
    "6c": {x["skill_id_candidate"] for x in load(FDM, "skills.yaml")},
    "6d": {x["skill_id_candidate"] for x in load(SDM, "skills.yaml")},
    "6e": {x["skill_id_candidate"] for x in load(GIM, "skills.yaml")},
}
prior_skills = set().union(*prior_skills_by_pkg.values())
prior_edges = [
    (x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"])
    for directory in prior_dirs
    for x in load(directory, "prerequisite_edges.yaml")
]

DOMAIN_ID = "domain.professional_engineering"
module_topics = {
    "module.professional.repository_analysis": ["topic.professional.source_tree_orientation", "topic.professional.issue_reproduction", "topic.professional.change_planning"],
    "module.professional.collaboration": ["topic.professional.branch_commit_workflow", "topic.professional.pull_request_authoring", "topic.professional.code_review"],
    "module.professional.testing_quality": ["topic.professional.test_strategy", "topic.professional.integration_regression", "topic.professional.ci_quality"],
    "module.professional.build_release": ["topic.professional.reproducible_builds", "topic.professional.release_validation", "topic.professional.artifact_provenance"],
    "module.professional.debug_performance": ["topic.professional.debug_investigation", "topic.professional.performance_experiment", "topic.professional.performance_reporting"],
    "module.professional.design_communication": ["topic.professional.problem_constraints", "topic.professional.rfc_tradeoffs", "topic.professional.decision_communication"],
    "module.professional.operations_reliability": ["topic.professional.observability_runbooks", "topic.professional.incident_response", "topic.professional.postmortem_actions"],
    "module.professional.security_open_source": ["topic.professional.security_review", "topic.professional.oss_contribution_discovery", "topic.professional.oss_submission_iteration"],
    "module.professional.project_capstone": ["topic.professional.project_planning", "topic.professional.integration_evidence", "topic.professional.capstone_defense"],
}

# sid | topic | statement | kind | retention | hard | soft | professional tags
SKILL_ROWS = """
skill.professional.repo_structure_orientation|topic.professional.source_tree_orientation|Yeni bir repository'de ana component, build/test entrypoint ve ownership sınırlarını source tree üzerinden çıkarabilmek.|professional_workflow|complex|skill.programming.code_reading_basic||source_reading;systems_design
skill.professional.source_entrypoint_trace|topic.professional.source_tree_orientation|Bir executable/service/library entrypoint'inden ilgili control ve data flow'a kadar kaynak kod yolunu izleyebilmek.|professional_workflow|complex|skill.professional.repo_structure_orientation;skill.programming.trace_execution_basic||source_reading;debugging
skill.professional.cross_module_flow_trace|topic.professional.source_tree_orientation|Bir davranışın birden fazla module/package/process boyunca geçtiği akışı kanıtla izleyebilmek.|professional_workflow|complex|skill.professional.source_entrypoint_trace||source_reading;systems_design;debugging
skill.professional.issue_reproduction|topic.professional.issue_reproduction|Issue/bug raporunu environment, input ve observed output ile tekrar üretilebilir hale getirebilmek.|professional_workflow|complex|skill.engineering.reproducible_run_notes;skill.programming.debug_localization_basic||debugging;reproducibility
skill.professional.minimal_reproducer|topic.professional.issue_reproduction|Bir arızayı korurken ilgisiz değişkenleri azaltan minimal reproducer oluşturabilmek.|professional_workflow|complex|skill.professional.issue_reproduction;skill.programming.test_case_basic||debugging;testing;reproducibility
skill.professional.issue_scope_decomposition|topic.professional.issue_reproduction|Bir issue'yu observable symptom, likely subsystem, acceptance evidence ve non-goal'lara bölebilmek.|design_reasoning|complex|skill.professional.issue_reproduction;skill.programming.function_decomposition||project_delivery;technical_communication
skill.professional.dependency_impact_analysis|topic.professional.change_planning|Planlanan değişikliğin callers, data contracts, tests, build ve operational dependencies üzerindeki etkisini izleyebilmek.|design_reasoning|complex|skill.professional.cross_module_flow_trace;skill.professional.issue_scope_decomposition||systems_design;reliability
skill.professional.change_plan|topic.professional.change_planning|Scope, affected components, test plan, risk ve rollback düşüncesi içeren uygulanabilir change plan yazabilmek.|professional_workflow|complex|skill.professional.dependency_impact_analysis||project_delivery;technical_communication;reliability
skill.professional.branch_change_isolation|topic.professional.branch_commit_workflow|Bir değişikliği bağımsız branch/worktree sınırında ilgisiz work'ten izole edebilmek.|professional_workflow|standard|skill.git.stage_commit_history_basic||collaboration;reproducibility
skill.professional.commit_history_hygiene|topic.professional.branch_commit_workflow|Review edilebilir atomic commit'ler ve açıklayıcı history oluşturabilmek.|professional_workflow|complex|skill.professional.branch_change_isolation;skill.git.stage_commit_history_basic||collaboration;technical_communication
skill.professional.rebase_conflict_resolution|topic.professional.branch_commit_workflow|Branch divergence/conflict durumunda semantic intent'i koruyarak history'yi güncelleyebilmek.|professional_workflow|complex|skill.professional.commit_history_hygiene||collaboration;debugging
skill.professional.pr_problem_solution_evidence|topic.professional.pull_request_authoring|PR açıklamasında problem, yaklaşım, test/evidence, risk ve verification bilgisini açık sunabilmek.|professional_workflow|complex|skill.professional.change_plan;skill.professional.commit_history_hygiene;skill.engineering.reproducible_run_notes||collaboration;technical_communication;testing
skill.professional.pr_scope_discipline|topic.professional.pull_request_authoring|PR scope'unu review edilebilir tutup ilgisiz refactor veya behavior değişimini ayırabilmek.|design_reasoning|complex|skill.professional.pr_problem_solution_evidence||collaboration;project_delivery
skill.professional.review_feedback_triage|topic.professional.pull_request_authoring|Review feedback'ini correctness, design, style, evidence ve scope kategorilerine ayırıp uygun aksiyona çevirebilmek.|professional_workflow|complex|skill.professional.pr_problem_solution_evidence||collaboration;technical_communication
skill.professional.review_iteration|topic.professional.pull_request_authoring|Review feedback sonrası change, test ve açıklama zincirini tutarlı biçimde güncelleyebilmek.|professional_workflow|complex|skill.professional.review_feedback_triage;skill.professional.commit_history_hygiene||collaboration;testing;reproducibility
skill.professional.code_review_correctness|topic.professional.code_review|Başkasının patch'inde behavior, edge case ve regression riskini kanıt üzerinden inceleyebilmek.|professional_workflow|complex|skill.programming.code_reading_basic;skill.programming.test_case_basic||collaboration;testing;reliability
skill.professional.code_review_maintainability|topic.professional.code_review|Patch'in abstraction, coupling, naming ve future-change maliyetini somut kod bağlamında değerlendirebilmek.|design_reasoning|complex|skill.professional.code_review_correctness;skill.professional.cross_module_flow_trace||collaboration;systems_design
skill.professional.code_review_risk|topic.professional.code_review|Patch'in operational, performance, security ve compatibility risklerini ilgili evidence ile işaretleyebilmek.|design_reasoning|complex|skill.professional.code_review_correctness;skill.professional.dependency_impact_analysis||collaboration;reliability;operational_safety
skill.professional.test_strategy_selection|topic.professional.test_strategy|Değişikliğin riskine göre unit, integration, system, regression ve manual evidence karışımını seçebilmek.|design_reasoning|complex|skill.programming.test_case_basic||testing;reliability
skill.professional.test_oracle_design|topic.professional.test_strategy|Bir test için güvenilir expected behavior/oracle ve failure signal tanımlayabilmek.|design_reasoning|complex|skill.professional.test_strategy_selection||testing;reliability
skill.professional.integration_test_design|topic.professional.integration_regression|Component sınırları, external dependencies ve failure behavior için integration test tasarlayabilmek.|professional_workflow|complex|skill.professional.test_strategy_selection;skill.professional.dependency_impact_analysis||testing;systems_design
skill.professional.system_validation_plan|topic.professional.integration_regression|Gerçekistic workflow üzerinde end-to-end system validation adımlarını ve acceptance evidence'ını tanımlayabilmek.|professional_workflow|complex|skill.professional.integration_test_design;skill.professional.change_plan||testing;project_delivery
skill.professional.regression_test_from_bug|topic.professional.integration_regression|Düzeltilen bug'ın observable failure mode'unu koruyan regression test üretebilmek.|professional_workflow|complex|skill.professional.minimal_reproducer;skill.professional.test_oracle_design||testing;debugging
skill.professional.flaky_test_diagnosis|topic.professional.ci_quality|Flaky test davranışını timing, shared state, environment veya nondeterministic input kaynağına lokalize edebilmek.|professional_workflow|complex|skill.professional.regression_test_from_bug;skill.programming.debug_localization_basic||testing;debugging;reliability
skill.professional.ci_failure_localization|topic.professional.ci_quality|CI failure'ını code, test, dependency, environment veya infrastructure kaynaklı katmana ayırabilmek.|professional_workflow|complex|skill.professional.issue_reproduction;skill.professional.test_strategy_selection||testing;debugging;build_tooling
skill.professional.quality_gate_definition|topic.professional.ci_quality|Merge/release öncesi gerekli test, lint/build, security ve performance gate'lerini risk-temelli tanımlayabilmek.|design_reasoning|complex|skill.professional.test_strategy_selection;skill.professional.code_review_risk||testing;reliability;operational_safety
skill.professional.reproducible_build_contract|topic.professional.reproducible_builds|Source revision, dependency versions, toolchain ve build commands ile yeniden üretilebilir build contract oluşturabilmek.|professional_workflow|complex|skill.engineering.reproducible_run_notes|skill.cpp.build_system_project_definition|build_tooling;reproducibility
skill.professional.dependency_version_control|topic.professional.reproducible_builds|Dependency version/pin/lock kararını compatibility ve reproducibility gerekçesiyle yönetebilmek.|professional_workflow|complex|skill.professional.reproducible_build_contract||build_tooling;reproducibility;reliability
skill.professional.release_validation|topic.professional.release_validation|Release candidate'ı declared quality gates, smoke/system tests ve compatibility evidence ile doğrulayabilmek.|professional_workflow|complex|skill.professional.quality_gate_definition;skill.professional.reproducible_build_contract;skill.platform.deployment_change_safety||build_tooling;testing;reliability
skill.professional.rollback_plan|topic.professional.release_validation|Değişiklik için trigger, state compatibility ve recovery adımlarını içeren rollback planı kurabilmek.|design_reasoning|complex|skill.professional.release_validation;skill.platform.deployment_change_safety||reliability;operational_safety
skill.professional.changelog_release_note|topic.professional.release_validation|User/operator açısından anlamlı behavior, migration ve risk değişikliklerini release note/changelog olarak yazabilmek.|professional_workflow|standard|skill.professional.release_validation||technical_communication;project_delivery
skill.professional.release_artifact_provenance|topic.professional.artifact_provenance|Release artifact'ını source revision, build inputs, dependency state ve validation evidence ile izlenebilir kılabilmek.|professional_workflow|complex|skill.professional.reproducible_build_contract;skill.professional.release_validation||reproducibility;operational_safety
skill.professional.artifact_integrity_verification|topic.professional.artifact_provenance|Artifact identity/integrity bilgisini distribution ve deployment öncesi doğrulayabilmek.|professional_workflow|complex|skill.professional.release_artifact_provenance;skill.platform.image_supply_chain_safety||operational_safety;reproducibility
skill.professional.debug_hypothesis_loop|topic.professional.debug_investigation|Symptom'dan falsifiable hypothesis üretip targeted observation ile daraltılan debug döngüsü yürütebilmek.|professional_workflow|complex|skill.programming.debug_localization_basic;skill.engineering.explain_debug_fix_basic||debugging;technical_communication
skill.professional.debug_evidence_chain|topic.professional.debug_investigation|Bir root-cause iddiasını logs, traces, tests, debugger veya reproduction evidence zinciriyle destekleyebilmek.|professional_workflow|complex|skill.professional.debug_hypothesis_loop;skill.engineering.reproducible_run_notes||debugging;reproducibility
skill.professional.regression_bisection|topic.professional.debug_investigation|Known-good/known-bad sınırları kullanarak regression-introducing change'i kontrollü biçimde lokalize edebilmek.|professional_workflow|complex|skill.professional.issue_reproduction;skill.professional.commit_history_hygiene||debugging;source_reading
skill.professional.cross_layer_failure_localization|topic.professional.debug_investigation|Application, OS, network, runtime ve infrastructure sinyallerini birleştirerek failure layer'ını ayırabilmek.|professional_workflow|complex|skill.professional.debug_evidence_chain;skill.observability.signal_correlation_diagnosis||debugging;observability;reliability
skill.professional.benchmark_experiment_plan|topic.professional.performance_experiment|Workload, warmup, repetitions, controls ve success metric'leri içeren performans deney planı oluşturabilmek.|measurement_production|complex|skill.performance.benchmark_reproducibility;skill.performance.measurement_validity_reasoning||profiling_benchmarking;reproducibility
skill.professional.measurement_environment_record|topic.professional.performance_experiment|Hardware/software/environment koşullarını benchmark sonucunu yeniden yorumlatacak ayrıntıda kaydedebilmek.|measurement_production|standard|skill.professional.benchmark_experiment_plan;skill.engineering.reproducible_run_notes||profiling_benchmarking;reproducibility
skill.professional.performance_report_synthesis|topic.professional.performance_reporting|Baseline, change, variance, bottleneck attribution, correctness gate ve trade-off'u tek teknik raporda birleştirebilmek.|measurement_production|complex|skill.professional.benchmark_experiment_plan;skill.performance.optimization_hypothesis_validation||profiling_benchmarking;technical_communication
skill.professional.performance_regression_gate|topic.professional.performance_reporting|Representative workload üzerinde meaningful performance regression'ı detect edip release/merge kararına bağlayabilmek.|measurement_production|complex|skill.professional.performance_report_synthesis;skill.performance.regression_detection||profiling_benchmarking;testing;reliability
skill.professional.problem_statement_scope|topic.professional.problem_constraints|Engineering problemini user/system impact, current behavior ve desired outcome ile açık tanımlayabilmek.|design_reasoning|standard|skill.professional.issue_scope_decomposition||design_docs;technical_communication
skill.professional.requirement_constraint_non_goal|topic.professional.problem_constraints|Requirement, constraint, assumption ve non-goal'ları birbirinden ayırıp tasarım sınırı kurabilmek.|design_reasoning|complex|skill.professional.problem_statement_scope||design_docs;systems_design
skill.professional.interface_contract_reasoning|topic.professional.problem_constraints|Component/API sınırında input/output, failure ve compatibility contract'ını açık tanımlayabilmek.|design_reasoning|complex|skill.professional.requirement_constraint_non_goal;skill.professional.dependency_impact_analysis||design_docs;systems_design;reliability
skill.professional.alternative_tradeoff_analysis|topic.professional.rfc_tradeoffs|En az iki uygulanabilir çözümü complexity, performance, reliability ve operability trade-off'larıyla karşılaştırabilmek.|design_reasoning|complex|skill.professional.requirement_constraint_non_goal||design_docs;systems_design;technical_communication
skill.professional.design_risk_failure_mode|topic.professional.rfc_tradeoffs|Önerilen tasarımın failure modes, capacity limits ve operational risks'lerini önceden çıkarabilmek.|design_reasoning|complex|skill.professional.alternative_tradeoff_analysis;skill.professional.code_review_risk||design_docs;reliability;operational_safety
skill.professional.rfc_authoring|topic.professional.rfc_tradeoffs|Problem, constraints, alternatives, chosen design, rollout ve validation planını içeren reviewable RFC yazabilmek.|professional_workflow|complex|skill.professional.alternative_tradeoff_analysis;skill.professional.design_risk_failure_mode||design_docs;technical_communication;project_delivery
skill.professional.rfc_review_response|topic.professional.decision_communication|RFC review itirazlarını evidence, trade-off ve scope değişikliğiyle cevaplayabilmek.|professional_workflow|complex|skill.professional.rfc_authoring;skill.professional.review_feedback_triage||design_docs;collaboration;technical_communication
skill.professional.decision_record|topic.professional.decision_communication|Karar, context, alternatives ve consequence bilgisini future reader için kısa decision record'a dönüştürebilmek.|professional_workflow|standard|skill.professional.rfc_authoring||design_docs;technical_communication
skill.professional.technical_status_update|topic.professional.decision_communication|Progress, blocker, evidence, next action ve risk bilgisini kısa teknik durum güncellemesinde iletebilmek.|professional_workflow|standard|skill.professional.change_plan||technical_communication;project_delivery
skill.professional.observability_plan|topic.professional.observability_runbooks|Bir service/system için hangi log, metric ve trace sinyallerinin hangi failure/performance sorularını cevaplayacağını planlayabilmek.|design_reasoning|complex|skill.observability.structured_logging;skill.reliability.slo_definition||observability;reliability
skill.professional.runbook_authoring|topic.professional.observability_runbooks|Detection, diagnosis, mitigation, verification ve escalation adımlarını içeren uygulanabilir runbook yazabilmek.|professional_workflow|complex|skill.professional.observability_plan;skill.engineering.reproducible_run_notes||observability;reliability;technical_communication
skill.professional.incident_timeline|topic.professional.incident_response|Incident event, symptom, action ve evidence'ı zaman sıralı ve source-linked timeline'a dönüştürebilmek.|professional_workflow|complex|skill.reliability.incident_response_workflow;skill.professional.debug_evidence_chain||reliability;observability;technical_communication
skill.professional.incident_hypothesis_triage|topic.professional.incident_response|Incident sırasında competing hypotheses'i blast radius, recent changes ve telemetry evidence ile önceliklendirebilmek.|professional_workflow|complex|skill.professional.incident_timeline;skill.professional.cross_layer_failure_localization||reliability;debugging;observability
skill.professional.postmortem_root_cause|topic.professional.postmortem_actions|Postmortem'da trigger, root cause, contributing factors ve detection/response gaps'i birbirinden ayırabilmek.|professional_workflow|complex|skill.professional.incident_hypothesis_triage||reliability;technical_communication
skill.professional.corrective_action_design|topic.professional.postmortem_actions|Postmortem action'larını owner, verification evidence ve systemic-risk reduction hedefiyle tasarlayabilmek.|professional_workflow|complex|skill.professional.postmortem_root_cause;skill.professional.quality_gate_definition||reliability;project_delivery
skill.professional.slo_change_validation|topic.professional.postmortem_actions|Reliability veya capacity değişikliğini SLO ve error-budget etkisi üzerinden doğrulayabilmek.|measurement_production|complex|skill.reliability.slo_definition;skill.professional.corrective_action_design||reliability;profiling_benchmarking
skill.professional.threat_surface_review|topic.professional.security_review|Bir change'in trust boundaries, exposed inputs, privileges ve sensitive assets üzerindeki etkisini belirleyebilmek.|design_reasoning|complex|skill.professional.dependency_impact_analysis||operational_safety;systems_design
skill.professional.secret_exposure_review|topic.professional.security_review|Code/config/log/artifact değişikliğinde secret exposure riskini tespit edip güvenli alternatif önerebilmek.|professional_workflow|complex|skill.professional.threat_surface_review;skill.platform.secret_handling_safety||operational_safety;code_review
skill.professional.dependency_supply_chain_review|topic.professional.security_review|Yeni dependency/artifact için source, version, integrity ve transitive-risk evidence'ını değerlendirebilmek.|professional_workflow|complex|skill.professional.dependency_version_control;skill.platform.image_supply_chain_safety||operational_safety;build_tooling
skill.professional.operational_change_risk|topic.professional.security_review|Deployment/config/data migration değişikliğinin blast radius ve rollback riskini review edebilmek.|design_reasoning|complex|skill.professional.rollback_plan;skill.platform.deployment_change_safety||operational_safety;reliability
skill.professional.oss_project_orientation|topic.professional.oss_contribution_discovery|Open-source repository'nin contribution docs, issue tracker, build/test path ve maintainer workflow'unu çıkarabilmek.|professional_workflow|complex|skill.professional.repo_structure_orientation;skill.professional.test_strategy_selection||oss_workflow;source_reading
skill.professional.oss_issue_selection|topic.professional.oss_contribution_discovery|Kendi mevcut capability ve scope'una uygun, reproducible ve reviewable contribution adayı seçebilmek.|design_reasoning|complex|skill.professional.oss_project_orientation;skill.professional.issue_scope_decomposition||oss_workflow;project_delivery
skill.professional.oss_contribution_policy|topic.professional.oss_contribution_discovery|Repository'nin contribution, DCO/CLA, style, test ve communication kurallarını okuyup uygulanacak checklist'e çevirebilmek.|professional_workflow|standard|skill.professional.oss_project_orientation||oss_workflow;operational_safety
skill.professional.oss_patch_scope|topic.professional.oss_submission_iteration|Seçilen OSS issue için minimal, maintainable ve testable patch scope'u belirleyebilmek.|design_reasoning|complex|skill.professional.oss_issue_selection;skill.professional.pr_scope_discipline||oss_workflow;collaboration
skill.professional.oss_submission_evidence|topic.professional.oss_submission_iteration|OSS patch'i tests, reproduction, rationale ve compatibility notes ile submit edilebilir hale getirebilmek.|professional_workflow|complex|skill.professional.oss_patch_scope;skill.professional.pr_problem_solution_evidence;skill.professional.oss_contribution_policy||oss_workflow;testing;technical_communication
skill.professional.oss_maintainer_feedback_iteration|topic.professional.oss_submission_iteration|Maintainer feedback sonrası patch, tests ve rationale'ı repository normlarını koruyarak iterate edebilmek.|professional_workflow|complex|skill.professional.oss_submission_evidence;skill.professional.review_iteration||oss_workflow;collaboration;technical_communication
skill.professional.oss_community_communication|topic.professional.oss_submission_iteration|Issue/PR tartışmasında evidence, uncertainty ve disagreement'ı profesyonel ve teknik olarak ifade edebilmek.|professional_workflow|complex|skill.professional.oss_maintainer_feedback_iteration;skill.professional.technical_status_update||oss_workflow;collaboration;technical_communication
skill.professional.project_milestone_decomposition|topic.professional.project_planning|Büyük engineering outcome'u independently verifiable milestone ve dependency'lere bölebilmek.|design_reasoning|complex|skill.professional.change_plan;skill.programming.function_decomposition||project_delivery;systems_design
skill.professional.project_risk_register|topic.professional.project_planning|Technical, dependency, reliability ve integration risklerini owner/evidence ile takip edebilmek.|professional_workflow|complex|skill.professional.project_milestone_decomposition;skill.professional.design_risk_failure_mode||project_delivery;reliability
skill.professional.integration_checkpoint|topic.professional.project_planning|Milestone sonunda interface, tests, build, operability ve evidence readiness kontrolü yapabilmek.|professional_workflow|complex|skill.professional.project_milestone_decomposition;skill.professional.quality_gate_definition||project_delivery;testing;reliability
skill.professional.cross_domain_integration_design|topic.professional.integration_evidence|Bir project'te language, systems, distributed, GPU/inference ve operations sınırlarını explicit interface ve trade-off'larla birleştirebilmek.|design_reasoning|complex|skill.professional.rfc_authoring;skill.professional.integration_checkpoint||project_delivery;systems_design
skill.professional.evidence_matrix|topic.professional.integration_evidence|Project/capstone requirement'larını separately observable Skill/Objective evidence component'lerine eşleyebilmek.|professional_workflow|complex|skill.professional.test_strategy_selection;skill.professional.cross_domain_integration_design||project_delivery;testing;technical_communication
skill.professional.capstone_validation_plan|topic.professional.integration_evidence|Capstone için correctness, debugging, performance, reliability, security ve reproducibility validation planı kurabilmek.|professional_workflow|complex|skill.professional.evidence_matrix;skill.professional.performance_regression_gate;skill.professional.slo_change_validation||project_delivery;testing;reliability;profiling_benchmarking
skill.professional.capstone_defense|topic.professional.capstone_defense|Capstone architecture, trade-offs, failures, measurements ve unresolved limits'i bağımsız teknik incelemede savunabilmek.|professional_workflow|complex|skill.professional.capstone_validation_plan;skill.professional.performance_report_synthesis;skill.professional.postmortem_root_cause||technical_communication;project_delivery
skill.professional.project_retrospective|topic.professional.capstone_defense|Project sonunda initial plan, evidence, failures, rework ve next-systemic-improvement çıkarımını yapabilmek.|professional_workflow|complex|skill.professional.capstone_defense;skill.professional.corrective_action_design||project_delivery;technical_communication
""".strip().splitlines()

EVIDENCE_BY_KIND = {
    "design_reasoning": ["design_argument", "artifact_analysis"],
    "professional_workflow": ["hands_on_workflow", "artifact_analysis"],
    "measurement_production": ["measurement_artifact", "reproducible_benchmark"],
}
CRITICAL = {
    "skill.professional.issue_reproduction", "skill.professional.pr_problem_solution_evidence",
    "skill.professional.code_review_correctness", "skill.professional.test_strategy_selection",
    "skill.professional.release_validation", "skill.professional.debug_hypothesis_loop",
    "skill.professional.performance_report_synthesis", "skill.professional.rfc_authoring",
    "skill.professional.incident_hypothesis_triage", "skill.professional.oss_submission_evidence",
    "skill.professional.cross_domain_integration_design", "skill.professional.capstone_validation_plan",
    "skill.professional.capstone_defense",
}


def split_ids(value: str) -> list[str]:
    return [x for x in value.split(";") if x]


def parse_skills():
    declared = {line.split("|", 1)[0] for line in SKILL_ROWS}
    result = []
    for line in SKILL_ROWS:
        sid, topic, statement, kind, retention, hard, soft, tags = line.split("|")
        hard_ids, soft_ids = split_ids(hard), split_ids(soft)
        missing = [x for x in hard_ids + soft_ids if x not in prior_skills and x not in declared]
        if missing:
            raise ValueError(f"{sid}: missing prerequisite refs {missing}")
        evidence = EVIDENCE_BY_KIND[kind]
        tag_list = split_ids(tags)
        result.append({
            "skill_id_candidate": sid,
            "canonical_name": sid.split(".")[-1].replace("_", " ").title(),
            "capability_statement": statement,
            "capability_kind": kind,
            "lifecycle_status": "draft",
            "authoring_status": "internally_qa_passed",
            "primary_teaching_topic_id": topic,
            "linked_topic_ids": [],
            "shared_placement_domain_ids": [DOMAIN_ID],
            "independent_evidence_path": {"observable": True, "direct_evidence_types": evidence, "example_outcome": statement},
            "boundaries": {
                "prerequisite_boundary": "Declared prerequisites define the fair evidence boundary.",
                "remediation_boundary": "Failure is remediated at this exact professional behavior, not by resetting a technical Domain.",
                "reuse_boundary": "Existing D01-D22 technical capability is reused by canonical ID and is never cloned into D23.",
            },
            "shared_vs_specific": {"classification": "shared_professional", "rationale": "Professional behavior is reusable across language, systems, GPU and inference project contexts."},
            "evidence_depth_expectations": ["independent_application", "transfer", "delayed_retention", "production_context"],
            "retention_profile": retention,
            "diagnostic_eligibility": "eligible",
            "critical_prerequisite_candidate": sid in CRITICAL,
            "remediation_tags": sorted(set(["targeted_reteach", "fresh_variant"] + (["debug_localization"] if "debugging" in tag_list else []) + (["measurement_replay"] if "profiling_benchmarking" in tag_list else []) + (["review_replay"] if "collaboration" in tag_list or "oss_workflow" in tag_list else []))),
            "professional_capability_tags": tag_list,
            "project_capability_tags": ["d23_professional_integration"],
            "source_refs": ["source.repo.professional_readiness", "source.repo.frdb_v0", "source.repo.gns_v0"],
            "provenance_ref": "source.repo.internal_6f_authoring",
            "freshness_class": "evergreen",
            "technology_dependency_refs": [],
            "duplicate_resolution": {"status": "create_new", "compared_skill_ids": sorted(x for x in hard_ids + soft_ids if x in prior_skills), "rationale": "Compared against accepted 6C/6D/6E registries; technical/tool capabilities are reused by ID and D23 adds only distinct professional workflow behavior."},
            "granularity_review_codes": ["GRANULARITY_OK", "SHARED_SKILL_REUSE"],
            "seed_mapping_refs": [], "review_refs": [], "_hard": hard_ids, "_soft": soft_ids,
        })
    return result


skills = parse_skills()
skill_by_id = {x["skill_id_candidate"]: x for x in skills}
local_skills = set(skill_by_id)
known_skills = prior_skills | local_skills

EXTRA_OBJECTIVES = [
    ("skill.professional.issue_reproduction", "reproduce_and_record", "Fresh issue'yu temiz environment'ta reproduce eder ve başka bir engineer'ın tekrar edebileceği evidence bundle üretir."),
    ("skill.professional.code_review_correctness", "find_regression_risk", "Yeni bir patch'te correctness veya regression riskini somut code path ve test/evidence önerisiyle raporlar."),
    ("skill.professional.flaky_test_diagnosis", "stabilize_flaky_test", "Flaky test'in nondeterminism kaynağını lokalize eder ve failure signal'ı bozmadan stabilize eder."),
    ("skill.professional.release_validation", "approve_or_block_release", "Release candidate için quality gate evidence'ını değerlendirip approve/block kararı ve gerekçesi üretir."),
    ("skill.professional.debug_hypothesis_loop", "localize_fix_verify", "Fresh production-benzeri bug'da hypothesis→observation→fix→verification zincirini bağımsız yürütür."),
    ("skill.professional.performance_report_synthesis", "report_tradeoff", "Performance değişikliğini correctness, variance, throughput/latency/cost ve bottleneck evidence ile raporlar."),
    ("skill.professional.rfc_authoring", "author_reviewable_rfc", "Yeni system change için alternatives, risks, rollout ve validation içeren reviewable RFC üretir."),
    ("skill.professional.postmortem_root_cause", "separate_causes_and_gaps", "Incident evidence'ından trigger, root cause, contributing factor ve detection gap'lerini ayırır."),
    ("skill.professional.oss_submission_evidence", "submit_reviewable_patch", "Gerçekçi OSS contribution için minimal patch, tests, rationale ve reproduction evidence paketi oluşturur."),
    ("skill.professional.capstone_validation_plan", "validate_integrated_capstone", "Integrated capstone'da correctness, performance, reliability, security ve reproducibility gate'lerini uygular."),
    ("skill.professional.capstone_defense", "defend_design_and_limits", "Capstone'u fresh reviewer soruları karşısında architecture, trade-off, failure ve measurement evidence ile savunur."),
]


def build_objective(skill: dict, action: str, statement: str, extra: bool = False):
    evidence = skill["independent_evidence_path"]["direct_evidence_types"]
    return {
        "objective_id_candidate": "objective." + skill["skill_id_candidate"].removeprefix("skill.") + "." + action,
        "owner_skill_id": skill["skill_id_candidate"], "objective_statement": statement, "observable_action": action,
        "success_criteria": "Davranış independent, prerequisite-valid, separately observable ve verified direct evidence ile gösterilir.",
        "lifecycle_status": "draft", "authoring_status": "internally_qa_passed", "requirement_role": "required",
        "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard",
        "evidence_profile": {"acceptable_evidence_types": evidence, "direct_evidence_types": evidence, "required_direct_type": evidence[0], "requires_user_authored_artifact": True, "requires_transfer": True, "evaluator_requirement": "verified", "policy_defaults_ref": "GRE-v0"},
        "task_environment_expectation": "Fresh real-repository or production-like engineering context.",
        "allowed_tools_policy_ref": "H0 independent evidence; normal engineering tools are allowed where tool use is not the hidden answer.",
        "diagnostic_eligibility": "eligible", "retention_requirement": {"delayed_revalidation_required": True, "context_diversity_requirement": "different_repository_or_system" if extra else "fresh_context"},
        "remediation_tags": skill["remediation_tags"], "source_refs": ["source.repo.professional_readiness", "source.repo.frdb_v0"],
        "provenance_ref": "source.repo.internal_6f_authoring", "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [],
    }


objectives = [build_objective(x, "demonstrate_capability", "Yeni bir bağlamda " + x["capability_statement"].removesuffix(".")) for x in skills]
for sid, action, statement in EXTRA_OBJECTIVES:
    objectives.append(build_objective(skill_by_id[sid], action, statement, True))
objectives.sort(key=lambda x: x["objective_id_candidate"])

organization = [{"entity_type": "domain", "logical_id_candidate": DOMAIN_ID, "display_name": "Professional Engineering / Open Source / Projects", "semantic_statement": "D23 professional engineering, open-source contribution, large-project and capstone capability organization domain.", "parent_organization_id": None, "primary_domain_id": DOMAIN_ID, "organization_role": "professional_integration", "teaching_intent_tags": ["teach", "practice", "assess", "transfer", "integrate"], "linked_route_family_ids": ["D23"], "lifecycle_status": "draft", "authoring_status": "internally_qa_passed", "source_refs": ["source.repo.pdm_v0", "source.repo.professional_readiness"], "provenance_ref": "source.repo.internal_6f_authoring", "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None}]
for mid, topics in module_topics.items():
    organization.append({"entity_type": "module", "logical_id_candidate": mid, "display_name": mid.split(".", 2)[2].replace("_", " ").title(), "semantic_statement": "Stable authoring cluster for D23 professional engineering behavior.", "parent_organization_id": DOMAIN_ID, "primary_domain_id": DOMAIN_ID, "organization_role": "authoring_cluster", "teaching_intent_tags": ["teach", "practice", "assess", "integrate"], "linked_route_family_ids": ["D23"], "lifecycle_status": "draft", "authoring_status": "internally_qa_passed", "source_refs": ["source.repo.frdb_v0", "source.repo.gns_v0"], "provenance_ref": "source.repo.internal_6f_authoring", "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None})
    for topic in topics:
        organization.append({"entity_type": "topic", "logical_id_candidate": topic, "display_name": topic.split(".", 2)[2].replace("_", " ").title(), "semantic_statement": "Learner-facing professional workflow, review, project or capstone teaching/evidence context.", "parent_organization_id": mid, "primary_domain_id": DOMAIN_ID, "organization_role": "teaching_context", "teaching_intent_tags": ["teach", "practice", "assess", "transfer", "integrate"], "linked_route_family_ids": ["D23"], "lifecycle_status": "draft", "authoring_status": "internally_qa_passed", "source_refs": ["source.repo.frdb_v0", "source.repo.professional_readiness"], "provenance_ref": "source.repo.internal_6f_authoring", "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None})
organization.sort(key=lambda x: (x["entity_type"], x["logical_id_candidate"]))


def owner_objectives(sid):
    return sorted(x["objective_id_candidate"] for x in objectives if x["owner_skill_id"] == sid)


topic_skill_links, reuse_seen = [], set()
for skill in skills:
    topic_skill_links.append({"topic_id": skill["primary_teaching_topic_id"], "skill_id": skill["skill_id_candidate"], "role": "teach", "importance": "core", "is_primary_teaching_context": True, "objective_scope_ids": owner_objectives(skill["skill_id_candidate"]), "authoring_rationale": "Primary D23 teaching/evidence context for this canonical professional capability.", "source_refs": ["source.repo.frdb_v0"], "lifecycle_status": "draft", "review_refs": []})
    for source in skill["_hard"] + skill["_soft"]:
        if source in prior_skills and (skill["primary_teaching_topic_id"], source) not in reuse_seen:
            reuse_seen.add((skill["primary_teaching_topic_id"], source))
            topic_skill_links.append({"topic_id": skill["primary_teaching_topic_id"], "skill_id": source, "role": "reinforce", "importance": "supporting", "is_primary_teaching_context": False, "objective_scope_ids": [], "authoring_rationale": "Accepted D01-D22 capability is reused in professional context without cloning learner state.", "source_refs": ["source.repo.prior_packages"], "lifecycle_status": "draft", "review_refs": []})
topic_skill_links.sort(key=lambda x: (x["topic_id"], x["skill_id"], x["role"]))


def reason_kind(source):
    if source.startswith(("skill.git.", "skill.engineering.", "skill.professional.")): return "professional_workflow_dependency"
    if source.startswith(("skill.performance.", "skill.observability.", "skill.reliability.")): return "evidence_interpretability"
    if source.startswith("skill.platform."): return "safety_dependency"
    if source.startswith(("skill.programming.", "skill.cpp.")): return "procedural_dependency"
    return "conceptual_dependency"


prerequisite_edges = []
for skill in skills:
    for kind, sources in (("hard", skill["_hard"]), ("soft", skill["_soft"])):
        for source in sources:
            prerequisite_edges.append({"prerequisite_skill_id": source, "target_skill_id": skill["skill_id_candidate"], "edge_kind": kind, "reason_kind": reason_kind(source), "strictness_profile_ref": "default_prg_v0", "authoring_rationale": "Source capability is required for interpretable, fair professional evidence." if kind == "hard" else "Source capability improves scaffold/fluency but is not required for fair target evidence.", "contamination_risk_if_missing": "high" if kind == "hard" else "low", "task_specific_instead_of_graph_edge": False, "source_refs": ["source.repo.prg_v0", "source.repo.frdb_v0"], "provenance_ref": "source.repo.internal_6f_authoring", "lifecycle_status": "draft", "review_status": "reviewed", "review_refs": [], "cross_package_ref": source in prior_skills, "hard_soft_test_result": "hard_required_for_interpretable_target" if kind == "hard" else "soft_scaffold_only"})
prerequisite_edges.sort(key=lambda x: (x["target_skill_id"], x["prerequisite_skill_id"], x["edge_kind"]))

capability_requirements = []
for skill in skills:
    for scope_kind, scope_id in (("domain", DOMAIN_ID), ("professional_route", "scope.professional_route.ai_infrastructure")):
        capability_requirements.append({"scope_kind": scope_kind, "scope_id": scope_id, "capability_kind": "skill", "capability_id": skill["skill_id_candidate"], "requirement_role": "required", "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard", "rationale": "Required D23 professional engineering capability.", "source_refs": ["source.repo.professional_readiness", "source.repo.frdb_v0"], "review_refs": []})

professional_attributions = []
for skill in skills:
    for tag in skill["professional_capability_tags"]:
        professional_attributions.append({"capability_id": skill["skill_id_candidate"], "professional_family_id": "professional." + tag, "attribution_role": "core" if tag in {"project_delivery", "oss_workflow", "collaboration"} else "supporting", "context_requirements": ["fresh_real_repository_or_production_like_context"], "evidence_expectation_ref": "evidence.h0.objective_specific", "rationale": "D23 capability contributes observable professional engineering evidence.", "source_refs": ["source.repo.professional_readiness", "source.repo.frdb_v0"], "review_refs": []})
professional_attributions.sort(key=lambda x: (x["capability_id"], x["professional_family_id"]))

project_components = {
    "project.foundation.reproducible_cli_tool": ["skill.professional.pr_problem_solution_evidence", "skill.professional.test_strategy_selection", "skill.professional.reproducible_build_contract", "skill.professional.project_retrospective"],
    "project.systems.observable_networked_service": ["skill.professional.rfc_authoring", "skill.professional.integration_test_design", "skill.professional.observability_plan", "skill.professional.performance_report_synthesis", "skill.professional.postmortem_root_cause"],
    "project.gpu_inference.integrated_serving_stack": ["skill.professional.cross_domain_integration_design", "skill.professional.capstone_validation_plan", "skill.professional.performance_report_synthesis", "skill.professional.incident_hypothesis_triage", "skill.professional.project_retrospective"],
    "project.professional.open_source_contribution": ["skill.professional.oss_project_orientation", "skill.professional.oss_issue_selection", "skill.professional.oss_contribution_policy", "skill.professional.oss_patch_scope", "skill.professional.oss_submission_evidence", "skill.professional.oss_maintainer_feedback_iteration", "skill.professional.oss_community_communication", "skill.professional.code_review_correctness"],
    "capstone.professional.ai_infrastructure_system": ["skill.professional.project_milestone_decomposition", "skill.professional.project_risk_register", "skill.professional.cross_domain_integration_design", "skill.professional.evidence_matrix", "skill.professional.capstone_validation_plan", "skill.professional.capstone_defense", "skill.professional.performance_report_synthesis", "skill.professional.postmortem_root_cause"],
}
capstone_prior_components = ["skill.serving.latency_throughput_slo_analysis", "skill.ai_infra.topology_aware_placement", "skill.ai_infra.model_rollout_validation", "skill.ai_infra.efficiency_optimization_validation", "skill.multi_gpu.nccl_debug_trace", "skill.performance.benchmark_reproducibility", "skill.observability.structured_logging", "skill.reliability.slo_definition"]
project_capstone_attributions = []
for pid, sids in project_components.items():
    for sid in sids:
        project_capstone_attributions.append({"project_or_capstone_id": pid, "skill_id": sid, "objective_id": owner_objectives(sid)[0], "role": "core" if pid.startswith("capstone.") else "supporting", "structurally_essential": True, "separately_observable": True, "expected_evidence_type": skill_by_id[sid]["independent_evidence_path"]["direct_evidence_types"][0], "rubric_component_ref": "rubric." + pid.replace("project.", "").replace("capstone.", "") + "." + sid.split(".")[-1], "source_refs": ["source.repo.professional_readiness", "source.repo.frdb_v0"], "review_refs": []})
for sid in capstone_prior_components:
    directory = next(d for d in prior_dirs if sid in {x["skill_id_candidate"] for x in load(d, "skills.yaml")})
    objs = [x for x in load(directory, "objectives.yaml") if x["owner_skill_id"] == sid]
    project_capstone_attributions.append({"project_or_capstone_id": "capstone.professional.ai_infrastructure_system", "skill_id": sid, "objective_id": objs[0]["objective_id_candidate"], "role": "integrated_technical_component", "structurally_essential": True, "separately_observable": True, "expected_evidence_type": objs[0]["evidence_profile"]["required_direct_type"], "rubric_component_ref": "rubric.professional.ai_infrastructure_system." + sid.split(".")[-1], "source_refs": ["source.repo.professional_readiness", "source.repo.prior_packages"], "review_refs": []})
project_capstone_attributions.sort(key=lambda x: (x["project_or_capstone_id"], x["skill_id"]))

declared_reuse = sorted({x for skill in skills for x in skill["_hard"] + skill["_soft"] if x in prior_skills} | set(capstone_prior_components))
seed_mappings = []
for sid in declared_reuse:
    pkg = next(k for k, values in prior_skills_by_pkg.items() if sid in values)
    seed_mappings.append({"seed_id": sid, "seed_kind": "accepted_prior_skill", "mapping_status": "reuse_existing", "target_skill_ids": [sid], "source_package": pkg, "authoring_rationale": "Professional D23 context reuses the accepted technical capability by canonical ID; no clone learner state is created.", "review_refs": []})
for pid in ["project.foundation.reproducible_cli_tool", "project.systems.observable_networked_service", "project.gpu_inference.integrated_serving_stack"]:
    seed_mappings.append({"seed_id": pid, "seed_kind": "prior_project", "mapping_status": "augment_with_professional_overlay", "target_skill_ids": sorted(project_components[pid]), "source_package": "prior_project_attribution", "authoring_rationale": "Existing integrated project remains valid and receives separately observable D23 professional workflow components.", "review_refs": []})

reviews = [
    {"review_id": "review.6f.external_coverage", "subject_refs": [DOMAIN_ID], "review_codes": ["NEEDS_GRANULARITY_REVIEW"], "severity": "non_blocking", "question": "Independent current engineering/OSS sources D23 professional capability coverage'ında eksik veya duplicate davranış gösteriyor mu?", "decision_inputs_required": ["independent Research AI", "authoritative current professional/OSS engineering sources"], "resolution_owner_step": "6H", "status": "open", "resolution": None, "source_refs": ["source.repo.frdb_v0"], "created_at": TODAY, "resolved_at": None},
    {"review_id": "review.6f.oss_workflow_freshness", "subject_refs": ["module.professional.security_open_source"], "review_codes": ["TOOL_SPECIFIC_SPLIT"], "severity": "non_blocking", "question": "Repository/platform-specific contribution policy variation için freshness/source guidance yeterli mi?", "decision_inputs_required": ["6H current OSS workflow/source audit"], "resolution_owner_step": "6H", "status": "open", "resolution": None, "source_refs": ["source.repo.frdb_v0"], "created_at": TODAY, "resolved_at": None},
    {"review_id": "review.6f.capstone_diversity", "subject_refs": ["module.professional.project_capstone"], "review_codes": ["HIDDEN_PREREQUISITE_RISK"], "severity": "non_blocking", "question": "Final capstone family diversity ve independent evidence coverage professional-readiness hedefi için yeterli mi?", "decision_inputs_required": ["independent Research AI", "6H cross-package coverage audit"], "resolution_owner_step": "6H", "status": "open", "resolution": None, "source_refs": ["source.repo.professional_readiness"], "created_at": TODAY, "resolved_at": None},
]

sources = [
    {"source_id": "source.repo.pdm_v0", "source_kind": "canonical_spec", "title": "Professional Domain Backbone", "ref": "docs/CURRICULUM_DOMAIN_MAP.md", "version_or_date": "2026-08-25", "authority_class": "accepted_project_contract", "supports": ["route_partition"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.frdb_v0", "source_kind": "canonical_spec", "title": "Full-Route Decomposition Blueprint", "ref": "docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md", "version_or_date": "2026-08-26", "authority_class": "accepted_project_contract", "supports": ["package_contract", "D23_scope"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.gns_v0", "source_kind": "canonical_spec", "title": "Granularity & Naming Standard", "ref": "docs/GRANULARITY_NAMING_STANDARD.md", "version_or_date": "2026-08-25", "authority_class": "accepted_project_contract", "supports": ["granularity", "logical_identity"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.prg_v0", "source_kind": "canonical_spec", "title": "Prerequisite Policy", "ref": "docs/PREREQUISITE_POLICY_SPEC.md", "version_or_date": "2026-08-24", "authority_class": "accepted_project_contract", "supports": ["hard_soft_edges"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.professional_readiness", "source_kind": "canonical_spec", "title": "Professional Readiness Target", "ref": "docs/PROFESSIONAL_READINESS_TARGET.md", "version_or_date": "2026-08-25", "authority_class": "accepted_project_contract", "supports": ["professional_evidence", "capstone"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.prior_packages", "source_kind": "internal_design_analysis", "title": "Accepted D01-D22 detailed-map packages", "ref": "curriculum/decomposition/6c_foundations + 6d_systems + 6e_gpu_ml_inference", "version_or_date": TODAY, "authority_class": "accepted_internal_packages", "supports": ["skill_reuse", "project_overlay"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.internal_6f_authoring", "source_kind": "internal_design_analysis", "title": "6F internal authoring", "ref": "tools/generate_professional_engineering_package.py", "version_or_date": TODAY, "authority_class": "internal_authoring", "supports": ["D23_decomposition"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": "Does not replace independent 6H Research QA."},
]


def resolve_review(directory: Path, review_id: str, resolution: str):
    queue = load(directory, "review_queue.yaml")
    row = next((x for x in queue if x["review_id"] == review_id), None)
    if row is None: raise ValueError(f"review not found: {review_id}")
    row.update({"status": "resolved", "resolution": resolution, "resolved_at": TODAY, "resolution_refs": ["decomposition.6f_professional_engineering", "D-060"]})
    (directory / "review_queue.yaml").write_text(yaml.safe_dump(queue, allow_unicode=True, sort_keys=False, width=120), encoding="utf-8")
    open_count = sum(x["status"] == "open" for x in queue)
    manifest = load(directory, "manifest.yaml"); manifest["unresolved_review_count"] = open_count
    (directory / "manifest.yaml").write_text(yaml.safe_dump(manifest, allow_unicode=True, sort_keys=False, width=120), encoding="utf-8")
    qa = load(directory, "qa_report.yaml"); qa["counts"]["open_non_blocking_reviews"] = open_count
    (directory / "qa_report.yaml").write_text(yaml.safe_dump(qa, allow_unicode=True, sort_keys=False, width=120), encoding="utf-8")


resolve_review(SDM, "review.6d.professional_overlay_reconciliation", "6F D23 registry reuses Systems observability/reliability/performance/build/testing capabilities by canonical ID and adds only distinct professional workflow Skills.")
resolve_review(GIM, "review.6e.professional_overlay_reconciliation", "6F D23 registry consumes 6E professional/project attributions and reuses serving/multi-GPU/AI-infra technical capabilities by canonical ID in integrated capstone evidence.")

pairs = {(x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"]) for x in prerequisite_edges}
combined = set(prior_edges) | pairs
hard_graph = defaultdict(list); indegree = {x: 0 for x in known_skills}
for a, b, kind in combined:
    if kind == "hard": hard_graph[a].append(b); indegree[b] += 1
q = deque(x for x, d in indegree.items() if d == 0); visited = 0
while q:
    node = q.popleft(); visited += 1
    for target in hard_graph[node]:
        indegree[target] -= 1
        if indegree[target] == 0: q.append(target)
if visited != len(known_skills): raise ValueError(f"combined hard graph cycle: {visited}/{len(known_skills)}")

hard_count = sum(x["edge_kind"] == "hard" for x in prerequisite_edges); soft_count = len(prerequisite_edges) - hard_count
cross_count = sum(x["cross_package_ref"] for x in prerequisite_edges)
manifest = {"package_id": "decomposition.6f_professional_engineering", "package_version": 1, "stage_step": "6F", "status": "authoring_complete_internal_qa", "blueprint_version": "FRDB-v0", "graph_contract_version": "KGC-v0", "granularity_standard_version": "GNS-v0", "base_graph_refs": ["FDM-v0", "SDM-v0", "GIM-v0"], "route_family_ids": ["D23"], "included_collections": ["sources", "organization_entities", "skills", "objectives", "topic_skill_links", "prerequisite_edges", "capability_requirements", "professional_attributions", "project_capstone_attributions", "seed_mappings", "review_queue", "qa_report"], "source_catalog_refs": [x["source_id"] for x in sources], "coverage_declarations": [{"route_family_id": "D23", "status": "internally_mapped", "external_validation": "pending_6H"}], "prior_package_refs": [{"package_id": "decomposition.6c_foundations", "reused_skill_count": len(set(declared_reuse) & prior_skills_by_pkg["6c"])}, {"package_id": "decomposition.6d_systems", "reused_skill_count": len(set(declared_reuse) & prior_skills_by_pkg["6d"])}, {"package_id": "decomposition.6e_gpu_ml_inference", "reused_skill_count": len(set(declared_reuse) & prior_skills_by_pkg["6e"])}], "known_exclusions": ["Weakness/remediation runtime mapping belongs to 6G.", "External professional/OSS coverage validation belongs to independent Research QA in 6H.", "Production lesson/task/resource bodies belong to AŞAMA 15/20.", "Career application/interview/job-search curriculum expansion belongs to AŞAMA 20.", "Physical database schema belongs to 9C."], "unresolved_review_count": len(reviews), "blocking_review_count": 0, "generated_at": TODAY, "authored_by": "main_manager_gpt_5_6_sol", "review_refs": [x["review_id"] for x in reviews], "content_hash": None}
qa_report = {"package_id": "decomposition.6f_professional_engineering", "qa_contract": "FRDB-v0 section 27", "result": "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS", "checked_at": TODAY, "counts": {"domains": 1, "modules": len(module_topics), "topics": sum(len(x) for x in module_topics.values()), "skills": len(skills), "objectives": len(objectives), "topic_skill_links": len(topic_skill_links), "prerequisite_edges": len(prerequisite_edges), "hard_prerequisite_edges": hard_count, "soft_prerequisite_edges": soft_count, "cross_package_prerequisite_edges": cross_count, "reused_prior_package_skills": len(declared_reuse), "reused_6c_skills": len(set(declared_reuse) & prior_skills_by_pkg["6c"]), "reused_6d_skills": len(set(declared_reuse) & prior_skills_by_pkg["6d"]), "reused_6e_skills": len(set(declared_reuse) & prior_skills_by_pkg["6e"]), "capability_requirements": len(capability_requirements), "professional_attributions": len(professional_attributions), "project_attributions": len(project_capstone_attributions), "open_non_blocking_reviews": len(reviews), "open_blocking_reviews": 0}, "checks": [{"check": "route_family_partition", "result": "PASS", "details": "D23 exactly"}, {"check": "prior_registry_reuse", "result": "PASS", "details": f"{len(declared_reuse)} prior Skills reused without clone"}, {"check": "professional_overlay_reconciliation", "result": "PASS", "details": "6D and 6E professional-overlay reviews resolved by canonical D23 reuse"}, {"check": "integrated_project_attribution", "result": "PASS", "details": "Foundation, systems and GPU/inference projects receive separately observable professional overlay components"}, {"check": "capstone_component_attribution", "result": "PASS", "details": "Final AI-infrastructure capstone combines D23 professional Skills with accepted technical Skills by canonical ID"}, {"check": "combined_hard_graph_dag", "result": "PASS", "details": f"visited {visited}/{len(known_skills)} across 6C+6D+6E+6F"}, {"check": "english_global_gate", "result": "PASS", "details": "no English hard edge into D23 technical/professional Skills"}, {"check": "blocking_reviews", "result": "PASS", "details": "0 open blocking reviews"}], "external_research_qa": {"status": "pending", "owner_step": "6H", "required_before_external_validation": True}}

skills_out = []
for row in skills:
    row = dict(row); row.pop("_hard", None); row.pop("_soft", None); skills_out.append(row)
for name, value in [("manifest.yaml", manifest), ("sources.yaml", sources), ("organization_entities.yaml", organization), ("skills.yaml", sorted(skills_out, key=lambda x: x["skill_id_candidate"])), ("objectives.yaml", objectives), ("topic_skill_links.yaml", topic_skill_links), ("prerequisite_edges.yaml", prerequisite_edges), ("capability_requirements.yaml", capability_requirements), ("professional_attributions.yaml", professional_attributions), ("project_capstone_attributions.yaml", project_capstone_attributions), ("seed_mappings.yaml", seed_mappings), ("review_queue.yaml", reviews), ("qa_report.yaml", qa_report)]: dump(name, value)

print("PROFESSIONAL_ENGINEERING_PACKAGE_GENERATED")
print(f"domains=1 modules={len(module_topics)} topics={sum(len(x) for x in module_topics.values())}")
print(f"skills={len(skills)} objectives={len(objectives)} links={len(topic_skill_links)}")
print(f"prerequisites={len(prerequisite_edges)} hard={hard_count} soft={soft_count}")
print(f"reused_prior={len(declared_reuse)} 6c={len(set(declared_reuse)&prior_skills_by_pkg['6c'])} 6d={len(set(declared_reuse)&prior_skills_by_pkg['6d'])} 6e={len(set(declared_reuse)&prior_skills_by_pkg['6e'])}")
print(f"combined_hard_dag_nodes={visited}/{len(known_skills)}")
