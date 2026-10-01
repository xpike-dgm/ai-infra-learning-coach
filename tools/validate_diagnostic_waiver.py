"""Independent 13F QA — VDWX-v0 Validated Diagnostic Waiver Implementation.

The diagnostic reason codes are read out of `PDT-v0` §8.7 and the closed catalog, the gates out of the mastery engine
itself, the sources out of `VDW-v0` §3, the outcomes out of `VDW-v0` §10, and all of them are compared with the real
Kotlin — never with 13F's own contract alone.

The checks that matter most are structural: a lower bar for a diagnostic, a waiver where the gates first passed on
ordinary learning, a waiver after the fast path ended, a self-report counted, a waiver read as mastery, an untaught
miss turned into a weakness, a lesson skipped for what was not shown, and planning that reads evidence must be
**unrepresentable** as a PASS here.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/13f_diagnostic_waiver/diagnostic_waiver.yaml"
SPEC = ROOT / "docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md"
RESEARCH = ROOT / "research/13f_diagnostic_waiver_research.md"
QA_OUT = ROOT / "arch/13f_diagnostic_waiver/qa_report.yaml"
VDW = ROOT / "docs/DIAGNOSTIC_WAIVER_SPEC.md"
PDT = ROOT / "docs/PLANNER_EXPLAINABILITY_SPEC.md"
SUITE_3H = ROOT / "docs/PLANNER_SIMULATION_SUITE.md"
DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
VUSX = ROOT / "arch/12f_virtual_user_tests/virtual_users.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/DiagnosticFacts.kt"
EVIDENCE_KT = ANDROID / "core-model/src/main/kotlin/coach/EvidenceFacts.kt"
PLANNER_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/PlannerFacts.kt"
WEAKNESS_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/WeaknessFacts.kt"
CHANGE_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/ProgramChangeFacts.kt"
REASONS_KT = ANDROID / "core-model/src/main/kotlin/coach/ReasonCodes.kt"
ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/DiagnosticWaiverEngine.kt"
MASTERY_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/MasteryEngine.kt"
PLANNER_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
WEAKNESS_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/WeaknessEngine.kt"
CHANGE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/ProgramChangeEngine.kt"
OWNERSHIP_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/EngineOwnership.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/Diagnostics.kt"
PLAN_APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
CHANGES_APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/ProgramChanges.kt"
REBUILD_WEAKNESS_KT = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildWeakness.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/DiagnosticPresentation.kt"
CHANGE_PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ProgramChangePresentation.kt"
EXPLAIN_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/PlannerExplanation.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SQL_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"

def declared_port_extensions(root):
    """Ports added after MSBX-v0 by an accepted later contract (14A, D-105), never by an unrecorded edit."""
    import yaml as _yaml
    path = root / "arch/14a_tutor_contract/tutor_contract.yaml"
    tutor = (_yaml.safe_load(path.read_text(encoding="utf-8")) or {}) if path.is_file() else {}
    ext = tutor.get("port_extension") or {}
    return [ext["port"]] if tutor.get("status") == "accepted_14a" and ext.get("decision") == "D-105" else []


results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


def body(source: str, signature: str) -> str:
    at = source.find(signature)
    if at < 0:
        return ""
    open_at = source.find("{", at)
    if open_at < 0:
        return ""
    depth = 0
    for index in range(open_at, len(source)):
        if source[index] == "{":
            depth += 1
        elif source[index] == "}":
            depth -= 1
            if depth == 0:
                return source[at:index + 1]
    return ""


def flat(text: str) -> str:
    return re.sub(r"\s+", " ", text)


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, ENGINE_KT, APP_KT, PRES_KT):
    check(f"E13F-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = yaml.safe_load(read(CONTRACT)) or {}
facts, evidence, planner_facts = strip_comments(read(FACTS_KT)), strip_comments(read(EVIDENCE_KT)), strip_comments(read(PLANNER_FACTS_KT))
weakness_facts, change_facts, reasons = strip_comments(read(WEAKNESS_FACTS_KT)), strip_comments(read(CHANGE_FACTS_KT)), strip_comments(read(REASONS_KT))
engine, mastery, planner = strip_comments(read(ENGINE_KT)), strip_comments(read(MASTERY_KT)), strip_comments(read(PLANNER_KT))
weakness, change, ownership = strip_comments(read(WEAKNESS_KT)), strip_comments(read(CHANGE_KT)), strip_comments(read(OWNERSHIP_KT))
app, plan_app, changes_app = strip_comments(read(APP_KT)), strip_comments(read(PLAN_APP_KT)), strip_comments(read(CHANGES_APP_KT))
rebuild_weakness, pres, change_pres = strip_comments(read(REBUILD_WEAKNESS_KT)), strip_comments(read(PRES_KT)), strip_comments(read(CHANGE_PRES_KT))
explain, ports, sql = strip_comments(read(EXPLAIN_KT)), strip_comments(read(PORTS_KT)), strip_comments(read(SQL_KT))
schema, migrations = strip_comments(read(SCHEMA_KT)), strip_comments(read(MIGRATIONS_KT))
pres_raw, change_pres_raw = read(PRES_KT), read(CHANGE_PRES_KT)
vdw, pdt, suite3h, spec_text, research_text = read(VDW), read(PDT), read(SUITE_3H), read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- contract head and the accepted texts it rests on
check("E13F-01_model", contract.get("model") == "VDWX-v0", str(contract.get("model")))
check("E13F-01_status", contract.get("status") == "accepted_13f", str(contract.get("status")))
check("E13F-01_decision", contract.get("decision") == "D-104", str(contract.get("decision")))
check("E13F-01_policy", contract.get("policy") == "VDW-v0", str(contract.get("policy")))
scope = contract.get("scope", {})
for key, expected in {"waiver_granted_from_diagnostic_evidence": True, "diagnostic_recorded_as_truth": True,
                      "adaptive_routing": True, "planner_coverage_holds": True, "result_reported": True, "s06_runs": True,
                      "schema_changed": True, "projection_added": True, "state_family_added": True, "mastery_rule_changed": False,
                      "gate_threshold_changed": False, "retention_rule_changed": False, "planner_priority_rule_changed": False,
                      "weakness_rule_narrowed": True, "topic_state_decided": False, "app_calls_diagnostic": False,
                      "planner_initiated_diagnostic": False}.items():
    check(f"E13F-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E13F-01_vdw_principle", "Diagnostic, mastery için daha düşük standart kullanan kestirme yol değildir." in vdw, "VDW-v0 principle moved")
check("E13F-01_vdw_no_easier", "diagnostic_mastery_threshold != easier_threshold" in vdw, "VDW-v0 §2 moved")
check("E13F-01_s06", "## S06 — Partial diagnostic fast path" in suite3h and "waiver yalnız O1 + O2" in suite3h, "3H S06 moved")
check("E13F-01_invariant_12", "12. Partial diagnostic yalnız validated Objective'leri waive eder ve açıklama bunu doğru yansıtır." in pdt, "PDT-v0 invariant 12 moved")

# ---------------------------------------------------------------- vocabularies, read from VDW-v0, PDT-v0 and the catalog
pdt_block = re.search(r"## 8\.7 Diagnostic / VDW-v0\s*```text\n(.*?)```", pdt, re.S)
pdt_codes = pdt_block.group(1).split() if pdt_block else []
check("E13F-02_pdt_family", len(pdt_codes) == 10 and all(c.startswith("diagnostic.") for c in pdt_codes), str(pdt_codes))
catalog = re.findall(r'"(diagnostic\.[a-z0-9_]+)"', body(reasons, "ReasonCodeFamily.DIAGNOSTIC to listOf("))
check("E13F-02_catalog_is_pdt", catalog == pdt_codes, str(catalog))
used = re.findall(r'const val [A-Z_0-9]+ = "(diagnostic\.[a-z0-9_]+)"', facts)
check("E13F-02_codes_in_family", bool(used) and set(used) <= set(pdt_codes), f"{sorted(set(used) - set(pdt_codes))}")
codes_in_code = set(re.findall(r'"(diagnostic\.[a-z0-9_]+)"', facts + engine + planner + app + explain))
check("E13F-02_no_invented_code", codes_in_code <= set(pdt_codes), f"{sorted(codes_in_code - set(pdt_codes))}")
vdw_sources = re.search(r"# 3\. Diagnostic ne zaman açılabilir\?.*?```text\n(.*?)```", vdw, re.S)
vdw_sources = vdw_sources.group(1).split() if vdw_sources else []
sources = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\)', body(facts, "enum class DiagnosticSource"), re.M)
check("E13F-02_sources_from_vdw", bool(sources) and set(sources) <= set(vdw_sources), f"{sources} vs {vdw_sources}")
check("E13F-02_only_the_learner", "planner_diagnostic_opportunity" not in sources and "curriculum_entry_placement" not in sources,
      "a source other than the learner")
check("E13F-02_one_source_reason", 'val reasonCode: String get() = DiagnosticCodes.USER_REQUESTED_FAST_PATH' in facts, "a source invents a reason")
vdw_stages = re.search(r"`diagnostic_stage` örnekleri:\s*```text\n(.*?)```", vdw, re.S)
vdw_stages = vdw_stages.group(1).split() if vdw_stages else []
stages = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", ', body(facts, "enum class DiagnosticStage"), re.M)
check("E13F-02_stages_from_vdw", stages == vdw_stages, f"{stages} vs {vdw_stages}")
vdw_outcomes = re.search(r"Diagnostic sonucu üç kaba çıktı üretebilir:\s*```text\n(.*?)```", vdw, re.S)
vdw_outcomes = vdw_outcomes.group(1).split() if vdw_outcomes else []
outcomes = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", ', body(facts, "enum class WaiverOutcome"), re.M)
check("E13F-02_outcomes_from_vdw", outcomes == vdw_outcomes, f"{outcomes} vs {vdw_outcomes}")
states = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", (true|false)\)', body(facts, "enum class DiagnosticObjectiveState"), re.M)
check("E13F-02_states_equal_contract", [s[0] for s in states] == contract.get("objective_states"), str(states))
check("E13F-02_only_two_open", [s[0] for s in states if s[1] == "true"] == ["probe_needed", "confirm_needed"], str(states))
check("E13F-02_waiver_reason", 'const val REASON = "validated_prior_knowledge"' in facts and "reason = validated_prior_knowledge" in vdw, "waiver reason")
check("E13F-02_waiver_names_evidence", "require(sourceEvidenceIds.isNotEmpty())" in facts, "a waiver without evidence")
check("E13F-02_waived_names_waiver", "require((state == DiagnosticObjectiveState.WAIVED) == (waiver != null))" in facts, "a waived state without its waiver")
check("E13F-02_no_topic_in_scope", "topic" not in body(facts, "data class DiagnosticScope(").lower(), "the scope holds a Topic")

codec = body(facts, "object DiagnosticScopeCodec")
check("E13F-03_codec_format", 'const val FORMAT = "diagnostic_scope/1"' in codec, "format")
check("E13F-03_codec_strict", 'if (head.keys != setOf("source", "study_day", "curriculum_version")) throw CodecText.Malformed()' in codec
      and 'if (kind != "target" || f.keys != setOf("objective", "skill", "critical")) throw CodecText.Malformed()' in codec
      and "runCatching { decodeOrThrow(stored) }.getOrNull()" in codec, "the codec guesses")

# ---------------------------------------------------------------- the waiver asks GRE-v0's own gates
waiver_fn = body(engine, "fun waiver(")
gates_fn = body(engine, "fun gatesPass(")
check("E13F-04_gates_are_masterys", "MasteryEngine.decide(profile, rows)" in gates_fn
      and "decision.failedGates.isEmpty() && !decision.unresolvedRecheck" in gates_fn, "the waiver has gates of its own")
check("E13F-04_no_held_over_state", "previouslyMastered" not in gates_fn and "unresolvedVerification" not in gates_fn, "hysteresis in the waiver")
check("E13F-04_no_threshold_here", not re.search(r"\b0\.\d+\b|THRESHOLD|MIN_[A-Z_]+ *=", engine), "a number in the waiver")
check("E13F-04_mastery_threshold_unchanged", "const val OBJECTIVE_MASTERY_THRESHOLD_V0 = 0.80" in mastery
      and "const val MIN_INDEPENDENT_GROUPS_STANDARD = 2" in mastery and "const val MIN_INDEPENDENT_GROUPS_CRITICAL = 3" in mastery,
      "GRE-v0 constants moved")
check("E13F-04_first_pass_only", "val session = sessionOf[row.id] ?: return null" in waiver_fn and "if (!gatesPass(profile, prefix)) continue" in waiver_fn,
      "a later diagnostic pass waives what learning covered")
check("E13F-04_ended_fast_path", "diagnosticSession(row)?.takeIf { it !in ended }" in waiver_fn
      and "if (session != null && endsFastPath(row, profile)) ended += session" in waiver_fn, "evidence after the fast path ended waives")
check("E13F-04_sources_are_window", "window(profile, prefix).flatMap { group -> group.rows.map { it.id } }.sorted()" in waiver_fn, "sources not the window")
check("E13F-04_window_is_masterys", "MasteryEngine.recentWindow(MasteryEngine.groupsOf(rows.filter { MasteryEngine.exclusions(it, profile).isEmpty() }))" in engine,
      "a window of the waiver's own")
check("E13F-04_engine_pure", "import coach.ports" not in engine and "persistence" not in engine.lower(), "the engine reads a store")

ends = body(engine, "fun endsFastPath(")
check("E13F-05_prerequisite_first", 0 <= ends.find("if (!row.prerequisiteValid) return false") < ends.find("if (assisted(row)) return true"),
      "a missing prerequisite ends the fast path")
check("E13F-05_assistance_ends", "if (assisted(row)) return true" in ends
      and "row.independenceClass != IndependenceClass.INDEPENDENT || row.solutionExposed" in engine, "help does not end the fast path")
check("E13F-05_unmeasurable_ends_nothing", "if (row.outcome == EvidenceOutcome.INVALID || row.evaluatorStatus == EvaluatorStatus.INVALID || row.contested) return false" in ends,
      "an unmeasurable answer ends the fast path")
check("E13F-05_clean_miss", "val clean = row.evaluatorStatus == EvaluatorStatus.VERIFIED && row.evidenceType in profile.directEvidenceTypes" in ends
      and "return clean && (row.outcome == EvidenceOutcome.NEGATIVE || row.outcome == EvidenceOutcome.PARTIAL)" in ends, "what a clean miss is")
diagnose = body(engine, "fun diagnose(")
order = [diagnose.find(s) for s in ("if (waiver != null) return result(DiagnosticObjectiveState.WAIVED",
                                    "inThisDiagnostic.firstOrNull { endsFastPath(it, profile) }",
                                    "if (gatesPass(profile, ordered)) return result(DiagnosticObjectiveState.ALREADY_DEMONSTRATED")]
check("E13F-05_diagnose_order", all(p >= 0 for p in order) and order == sorted(order), str(order))
check("E13F-05_stage_rules", "window.isEmpty() && inThisDiagnostic.isEmpty() -> DiagnosticStage.PROBE" in diagnose
      and "profile.critical -> DiagnosticStage.CRITICAL_CONFIRM" in diagnose
      and '"transfer_evidence" in decision.failedGates -> DiagnosticStage.TRANSFER_CONFIRM' in diagnose, "stage rules")

outcome_fn = body(engine, "fun outcome(")
check("E13F-06_full_needs_mastery", "covered && diagnoses.map { it.target.skill }.all { it in masteredSkills }" in outcome_fn,
      "full without the mastery engine")
check("E13F-06_covered", "it.state == DiagnosticObjectiveState.WAIVED || it.state == DiagnosticObjectiveState.ALREADY_DEMONSTRATED" in outcome_fn, "covered")
check("E13F-06_none_when_none", "if (diagnoses.none { it.state == DiagnosticObjectiveState.WAIVED }) return WaiverOutcome.NONE" in outcome_fn, "none")
check("E13F-06_no_waiver_not_early", "if (outcome != WaiverOutcome.NONE || !inProgress) add(outcome.reasonCode)" in engine, "no waiver said before checking")

# ---------------------------------------------------------------- the planner
needs_fn = body(engine, "fun needs(")
check("E13F-07_need_trigger", "trigger = NeedTrigger.DIAGNOSTIC_OPPORTUNITY," in needs_fn and "decisionValue = DecisionValue.DECISIVE," in needs_fn, "need")
check("E13F-07_need_only_open", "if (diagnoses.none { it.target.skill == skill && it.state.open }) return@mapNotNull null" in needs_fn, "a closed diagnostic opens a need")
check("E13F-07_need_on_route", "if (!PlannerEngine.onRoute(state.lifecycleStatus)) return@mapNotNull null" in needs_fn, "off-route need")
band = body(planner, "fun band(")
check("E13F-07_p3", "NeedTrigger.NEW_LEARNING, NeedTrigger.PARALLEL_TRACK_DUE, NeedTrigger.DIAGNOSTIC_OPPORTUNITY -> PriorityBand.P3" in band,
      "a diagnostic is not planned progress")
route = flat(body(engine, "fun route("))
for needle, why in (("item.independenceMode == IndependenceMode.H0_REQUIRED &&", "h0"),
                    ("item.ref !in used && item.ref !in seen && item.variantFamilyId !in exposedFamilies &&", "fresh"),
                    ("item.expectedActiveMinutes != null &&", "minutes"),
                    ("AssessmentScope.DAILY_MICRO in item.scopeEligibility &&", "daily"),
                    ("ItemSelection.fit(item, AssessmentIntent.MASTERY_EVIDENCE, AssessmentScope.DAILY_MICRO, profiles, evaluatorAvailable)", "fit"),
                    ("is ItemFit.Usable -> fit.ceiling.permits(needed)", "ceiling"),
                    ("val needed = if (profile.critical) UseCeiling.CRITICAL_MASTERY_ELIGIBLE else UseCeiling.STANDARD_MASTERY_ELIGIBLE", "critical"),
                    ('("min_variant_families" !in failed || item.variantFamilyId !in diagnosis.windowVariantFamilies) &&', "diversity"),
                    ("(!confirming || item.dependencyGroupId == null || item.dependencyGroupId !in diagnosis.windowDependencyGroups) &&", "group"),
                    ('("required_direct_type" !in failed || item.evidenceType == profile.requiredDirectType) &&', "direct"),
                    ('("non_basic_evidence" !in failed || item.difficultyClass != DifficultyClass.BASIC.id) &&', "non_basic"),
                    ('("transfer_evidence" !in failed || item.difficultyClass == DifficultyClass.TRANSFER_INTEGRATION.id)', "transfer")):
    check(f"E13F-07_route_{why}", needle in route, f"routing lost {why}")
# Trust is the store's validation record through the item's ceiling: an unvalidated item is practice at most (AIV-v0),
# and the planner's own high-stakes rule refuses an untrusted diagnose candidate a second time.
assessment_facts = strip_comments(read(ANDROID / "core-model/src/main/kotlin/coach/AssessmentFacts.kt"))
check("E13F-07_trust_is_the_ceiling", "if (item.lifecycleStatus == LifecycleStatus.CANDIDATE) ceiling = ceiling.atMost(UseCeiling.PRACTICE_ONLY)" in assessment_facts
      and "private val HIGH_STAKES = setOf(TaskPurpose.ASSESS, TaskPurpose.RETAIN, TaskPurpose.DIAGNOSE)" in planner, "an unvalidated item can carry waiver evidence")
cand = engine[engine.find("fun candidate("):]
check("E13F-07_candidate", all(s in cand for s in ("purpose = TaskPurpose.DIAGNOSE,", "atomicEvidenceBoundary = true,",
                                                   "targetObjectives = listOf(diagnosis.target.objective),", "costMinutes = item.expectedActiveMinutes!!,")),
      "candidate shape")

hold = body(planner, "private fun coverageHold(")
check("E13F-08_lessons_only", "if (candidate.purpose != TaskPurpose.TEACH || candidate.targetObjectives.isEmpty()) return null" in hold, "practice held")
check("E13F-08_every_objective", "val holds = candidate.targetObjectives.map { coverage[it] ?: return null }" in hold, "a lesson with an unshown Objective skipped")
check("E13F-08_checking_holds", "if (!holds.all { it.waived }) return CoverageHold(waived = false, reasonCode = DiagnosticCodes.USER_REQUESTED_FAST_PATH)" in hold, "still-checked lesson called waived")
plan_fn = body(planner, "fun plan(")
check("E13F-08_hold_before_trust", 0 <= plan_fn.find("hold != null ->") < plan_fn.find("!candidate.validationStatus.selectable ->"), "trust before coverage")
check("E13F-08_dispositions", "if (hold.waived) CandidateDisposition.RESOLVED_BEFORE_SELECTION else CandidateDisposition.CONDITIONAL_NOT_SELECTED" in plan_fn, "dispositions")
check("E13F-08_blocked_says_so", "DiagnosticCodes.PREREQUISITE_BLOCKED.takeIf { candidate.purpose == TaskPurpose.DIAGNOSE }" in plan_fn, "a waiting diagnostic is silent")
check("E13F-08_held_not_selected", '"no_blocked_or_invalid_candidate_selected" to assessed.none { !it.usable && it.candidate.id in selectedIds }' in plan_fn,
      "a held lesson can be selected")
check("E13F-08_task_declares_objectives", "val targetObjectives: List<VersionedRef> = emptyList()," in planner_facts, "TaskCandidate target Objectives")

build = body(plan_app, "fun build(")
check("E13F-09_wired", all(s in build for s in ("DiagnosticPlanning.read(persistence, states)", "diagnostic?.needs.orEmpty()",
                                                 "DiagnosticPlanning.candidates(persistence, content, diagnostic, needs, evaluatorAvailable)",
                                                 "coverage = coverage,")), "planner not wired")
check("E13F-09_evaluator_unknown_is_false", "private val evaluatorAvailable: Boolean = false," in plan_app, "an unknown evaluator assumed")
planning = body(app, "internal object DiagnosticPlanning")
check("E13F-09_planning_reads_no_evidence", "evidenceFor" not in planning, "planning reads evidence")
check("E13F-09_holds_read_projection", "CoverageRows.read(persistence, objective)" in planning
      and "planning != null && r.diagnosticSessionId == planning.sessionId && r.state?.open == true ->" in planning, "holds")

# ---------------------------------------------------------------- storage of the request and the projection
active = body(app, "fun active(persistence: PersistencePort)")
check("E13F-10_newest_diagnostic", "persistence.latestAssessmentSessionIn(AssessmentScope.DAILY_MICRO, DiagnosticScopeCodec.FORMAT)" in active
      and "as? DiagnosticRecord.Request ?: return null" in active, "which diagnostic is open")
cache = body(app, "class Cache(")
check("E13F-10_only_daily_diagnostics", "if (row.payload[\"scope\"] != AssessmentScope.DAILY_MICRO.storedAs) return@getOrPut null" in cache
      and "return id.takeIf { scope(id)?.target(objective) != null }" in cache, "another session counts as a diagnostic")
request = body(app, "fun request(source: DiagnosticSource")
check("E13F-10_request_required_only", "profiles.filter { it.required || it.critical }" in request, "optional Objectives checked")
check("E13F-10_request_not_retested", "if (waived || DiagnosticWaiverEngine.gatesPass(profile, rows)) return@forEach" in request, "shown work retested")
check("E13F-10_request_published", "if (row.lifecycleStatus != PUBLISHED) return Requested.Refused(" in request, "deprecated fast path")
check("E13F-10_request_is_scope_only", "\"evidence_event\"" not in app and request.count("appendTruth(") == 1, "a request writes evidence")
check("E13F-10_withdrawal", "DiagnosticRecord.Withdrawal(active.sessionId, now.studyDay)" in body(app, "fun withdraw("), "withdrawal")
check("E13F-10_unreadable_not_guessed", "if (missing.isNotEmpty()) return Read.Unreadable(missing)" in body(app, "fun read(): Read"), "a guessed result")
check("E13F-10_rebuild_writes_own_family", "policyVersion = DiagnosticWaiverEngine.POLICY_VERSION," in app
      and "appendTruth" not in body(app, "class RebuildDiagnosticCoverage"), "the rebuild writes truth or another policy")
check("E13F-10_recompute_order", 0 <= changes_app.find("RebuildWeakness(persistence, clock).rebuild(skill, profiles)")
      < changes_app.find("RebuildDiagnosticCoverage(persistence, clock).rebuild(skill, profiles)")
      < changes_app.find("RebuildReadiness(persistence, clock).rebuild(skill)"), "recompute order")
check("E13F-10_session_on_evidence", "val assessmentSessionId: Long? = null," in evidence
      and "(SELECT a.assessment_session_id FROM attempt a WHERE a.id = e.source_attempt_id)" in sql, "evidence forgets its session")

v7 = schema[schema.find("val v7: List<String>"):]
mig = contract.get("schema_migration", {})
check("E13F-11_version", "const val VERSION = 7" in schema and "6 to Schema.v7," in migrations and mig.get("from") == 6 and mig.get("to") == 7, str(mig))
check("E13F-11_projection_listed", '"diagnostic_coverage",' in body(schema, "val projectionTables = listOf(")
      and '"diagnostic_coverage" to listOf("objective_logical_id", "objective_version"),' in sql, "projection not registered")
check("E13F-11_waiver_values", "const val COVERAGE_WAIVERS = \"'none', 'active'\"" in schema and "CHECK (waiver IN ($COVERAGE_WAIVERS))" in v7, "waiver values")
check("E13F-11_active_names_evidence", "CHECK (waiver <> 'active' OR (coalesce(waiver_session_id, '') <> '' AND coalesce(waiver_source_evidence_ids, '') <> ''))" in v7,
      "an active waiver without evidence")
check("E13F-11_state_values", "CHECK (diagnostic_state IN ($DIAGNOSTIC_STATES))" in v7 and "CHECK (diagnostic_stage IN ($DIAGNOSTIC_STAGES))" in v7, "state values")
schema_states = re.search(r'const val DIAGNOSTIC_STATES =\s*"([^"]+)"', schema)
schema_states = re.findall(r"'([a-z_]*)'", schema_states.group(1)) if schema_states else []
check("E13F-11_states_match_model", schema_states == [""] + [s[0] for s in states], str(schema_states))
schema_stages = re.search(r'const val DIAGNOSTIC_STAGES = "([^"]+)"', schema)
schema_stages = re.findall(r"'([a-z_]*)'", schema_stages.group(1)) if schema_stages else []
check("E13F-11_stages_match_model", schema_stages == [""] + stages, str(schema_stages))
check("E13F-11_daily_format", "WHEN NEW.scope = 'daily' AND NEW.blueprint IS NOT NULL AND substr(NEW.blueprint, 1, 17) <> 'diagnostic_scope/'" in v7, "daily format")
check("E13F-11_provenance", "$PROJECTION_PROVENANCE" in v7, "projection without provenance")
check("E13F-11_truth_untouched", "truthTables" not in v7 and not re.search(r"ALTER TABLE (assessment_session|attempt|evidence_event)", v7), "truth altered")
ddm = yaml.safe_load(read(DDM)) or {}
ddm_projection = [e["id"] for e in ddm.get("projection_entities", [])]
check("E13F-11_ddm_unedited", "diagnostic_coverage" not in ddm_projection and len(ddm_projection) == 8, str(ddm_projection))
check("E13F-11_extension_declared", contract.get("projection_extension") == {"table": "diagnostic_coverage", "grain": "per_objective", "owner": "VDW-v0", "decision": "D-104"},
      str(contract.get("projection_extension")))
msbx = yaml.safe_load(read(MSBX)) or {}
check("E13F-11_msbx_unedited", all(e["engine"] != "VDW-v0" for e in msbx.get("engine_ownership", [])), "MSBX-v0 edited")
check("E13F-11_family_declared", 'DIAGNOSTIC("VDW-v0", "diagnostic_coverage_state"),' in ownership
      and contract.get("engine_ownership_extension") == {"engine": "VDW-v0", "owns": "diagnostic_coverage_state", "decision": "D-104"},
      str(contract.get("engine_ownership_extension")))
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("E13F-11_port_count", sorted(interfaces) == sorted([p["id"] for p in msbx["ports"]["set"]] + declared_port_extensions(ROOT)) and contract.get("port_count") == 4, str(interfaces))
check("E13F-11_refinement", "fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth?" in ports
      and contract.get("port_refinements") == [{"port": "PersistencePort", "method": "latestAssessmentSessionIn"}], str(contract.get("port_refinements")))

# ---------------------------------------------------------------- weakness narrowing (user decision)
rule = body(weakness, "fun rule(")
check("E13F-12_baseline_rule", "event.diagnosticBaseline && !event.masteredBefore -> null" in rule, "a baseline miss is a weakness")
check("E13F-12_baseline_after_validity", 0 <= rule.find("!event.prerequisiteValid -> FailureRule.PREREQUISITE_CONTAMINATION") < rule.find("event.diagnosticBaseline"),
      "the baseline rule outranks validity")
check("E13F-12_rule_set_unchanged", len(re.findall(r'^\s+[A-Z0-9_]+\("failure\.[a-z_0-9]+", \d+\)', body(weakness_facts, "enum class FailureRule"), re.M)) == 12,
      "WLRM-v0's twelve rules changed")
check("E13F-12_baseline_field", "val diagnosticBaseline: Boolean = false," in weakness_facts, "baseline field")
check("E13F-12_learning_ends_baseline", "val baseline = inDiagnostic && !learnedHere" in rebuild_weakness and "if (!inDiagnostic) learnedHere = true" in rebuild_weakness,
      "the baseline outlives ordinary learning")
check("E13F-12_user_decision", contract.get("user_decisions", {}).get("diagnostic_baseline_miss_is_weakness") is False, "decision not recorded")

# ---------------------------------------------------------------- report extension
kinds = re.findall(r'^\s+([A-Z_]+)\("([a-z_]+)", "([a-z_]+)", "([a-z_.]+)"\)', body(change_facts, "enum class StateChangeKind"), re.M)
by_id = {k[1]: k for k in kinds}
check("E13F-13_waived_kind", by_id.get("coverage_waived", ("",) * 4)[2:] == ("confirmed_capabilities", "replan.prerequisite_state_changed"), str(by_id.get("coverage_waived")))
check("E13F-13_withdrawn_kind", by_id.get("coverage_waiver_withdrawn", ("",) * 4)[2:] == ("not_reliably_measured", "replan.evidence_state_changed"),
      str(by_id.get("coverage_waiver_withdrawn")))
check("E13F-13_kinds_declared", contract.get("state_change_extension") == ["coverage_waived", "coverage_waiver_withdrawn"], str(contract.get("state_change_extension")))
cov = body(change, "fun coverageChange(")
check("E13F-13_unwritten_not_before", "if (c0 == UNWRITTEN) {\n            if (c1 != UNWRITTEN) unknown += after.skill\n            return null\n        }" in cov, "an unwritten waiver is a before")
check("E13F-13_granted_only_from_none", "c0 == WAIVER_NONE && c1 == WAIVER_ACTIVE ->" in cov and "c0 == WAIVER_ACTIVE && c1 == WAIVER_NONE ->" in cov, "transitions")
trigger = body(changes_app, "fun triggerFor(")
tr = re.findall(r"StateChangeKind\.([A-Z_]+) in kinds -> ReplanTrigger\.([A-Z_]+)", trigger)
check("E13F-13_trigger", tr == [("REMEDIATION_OPENED", "NEW_REMEDIATION_CREATED"), ("VERIFICATION_OPENED", "NEW_VERIFICATION_DUE_CREATED"),
                                ("COVERAGE_WAIVED", "DIAGNOSTIC_WAIVER_GRANTED")], str(tr))
check("E13F-13_event_is_12d", 'DIAGNOSTIC_WAIVER_GRANTED("diagnostic_waiver_granted", "replan.prerequisite_state_changed"),' in planner_facts, "12D's event moved")

# ---------------------------------------------------------------- presentation
copy = re.findall(r'"([^"]*[a-zçğıöşü][^"]*)"', body(pres_raw, "object DiagnosticCopy"))
lowered = [s.lower() for s in copy]
for word in ("%", "puan", "başarısız", "geçti", "kaldın", "eksik", "zayıf", "öğrendin", "ustalaştın", "not:", "borç"):
    check(f"E13F-14_copy_no_{word.strip()[:12]}", all(word not in s for s in lowered), f"{word!r} in copy")
line_fn = body(pres, "fun line(")
skipped_states = [m for m in re.findall(r"DiagnosticObjectiveState\.([A-Z_]+) -> \"([^\"]*)\"", line_fn) if "atlanıyor" in m[1]]
check("E13F-14_only_waived_skipped", [s[0] for s in skipped_states] == ["WAIVED"], str(skipped_states))
check("E13F-14_help_not_penalty", "Yardım istemek cezalandırılmaz." in pres_raw, "help worded as a penalty")
check("E13F-14_no_number", not re.search(r"\d", " ".join(copy)), "a number in copy")
check("E13F-14_change_names_objective", "label(change.objective ?: change.skill)" in change_pres, "a waiver said of the whole Skill")
check("E13F-14_change_templates", "StateChangeKind.COVERAGE_WAIVED to" in change_pres and "StateChangeKind.COVERAGE_WAIVER_WITHDRAWN to" in change_pres, "templates")
check("E13F-14_explanation_skipped", ".filter { it.needKey == need.needKey && it.disposition in COVERED }" in explain, "the explanation hides what was skipped")
check("E13F-14_explanation_waiting", "val coverage = if (need.disposition == NeedDisposition.NO_VALID_CANDIDATE && DiagnosticCodes.USER_REQUESTED_FAST_PATH in need.finalReasonCodes)" in explain,
      "a waiting need explained as having no task")
check("E13F-14_no_combining_dot", chr(0x0307) not in pres_raw + change_pres_raw, "U+0307 in copy")

# ---------------------------------------------------------------- S06 and the virtual users
vusx = yaml.safe_load(read(VUSX)) or {}
check("E13F-15_s06_runs", contract.get("s06", {}).get("run") is True and contract.get("s06", {}).get("invariant_12") == "covered", str(contract.get("s06")))
check("E13F-15_12f_pointed_here", (vusx.get("scenarios", {}).get("S06") or {}).get("owner") in (13, "13", "13F"), str((vusx.get("scenarios", {}) or {}).get("S06")))

suites = contract.get("suites", {})
named_tests = {
    "model": ["only the learner can open a diagnostic, and every way of asking is the same reason",
              "a waiver names the evidence that validated it, and only a waived Objective names one",
              "a stored request decodes strictly or not at all"],
    "engine": ["gates that first pass on diagnostic evidence waive the starting lesson and name the evidence",
               "gates that first pass on ordinary learning waive nothing, whatever a diagnostic shows later",
               "one item, or the same family twice, never waives anything",
               "help, a seen solution, an unverified evaluation, a missing prerequisite or a contested item never count",
               "nothing gathered after the fast path ended can waive the lesson",
               "a clean miss in this diagnostic ends the fast path, it is no remediation",
               "not knowing something never taught is not a weakness",
               "a lesson whose Objectives were all waived is not taught again",
               "a lesson the learner's fast path is still checking waits for it"],
    "scenarios": ["S06 a partial diagnostic waives only the validated Objectives and the Skill is not mastered"],
    "application": ["saying I know this opens a diagnostic and waives nothing",
                    "the planner offers the diagnostic's checks, holds the lessons it is checking, and reads no evidence",
                    "S06 a partial diagnostic waives only the validated Objectives and the plan and the result say exactly that",
                    "help taken ends the fast path for that Objective, and a later success does not waive it",
                    "a waiver whose evidence is corrected away is withdrawn and its lesson comes back"],
    "presentation": ["S06 the result says only the validated parts are skipped",
                     "S06 the plan's explanation says only the validated parts are skipped, from the trace alone"],
    "storage": ["migrating a populated schema-6 database adds diagnostic_coverage and preserves every truth row",
                "a daily session carries content only as the learner's diagnostic"],
}
for suite, names in named_tests.items():
    text = read(ROOT / suites.get(suite, {}).get("file", "missing"))
    for name in names:
        check(f"E13F-16_{suite}_{name[:40]}", f"`{name}`" in text, f"missing test: {name}")

# ---------------------------------------------------------------- honesty
mutation = contract.get("mutation_results", {})
check("E13F-17_mutation_all_detected", mutation.get("detected") == mutation.get("total") == len(mutation.get("mutants", [])) >= 40,
      f"{mutation.get('detected')}/{mutation.get('total')}")
check("E13F-17_mutation_control", mutation.get("negative_control_result") == "survived_as_expected" and mutation.get("compile_failure_is_detection") is False
      and mutation.get("only_diagnostic_suites_run") is True and mutation.get("final_run_is_a_single_clean_run") is True
      and mutation.get("harness_verified_to_run_gradle") is True, "harness honesty")
vm = contract.get("validator_mutation", {})
check("E13F-17_validator_mutation", vm.get("detected") == vm.get("total") and (vm.get("total") or 0) >= 20 and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E13F-17_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
device = contract.get("device_verification", {})
check("E13F-17_device_not_claimed", device.get("t6_run") is False and device.get("claimed") is False, "a device result is claimed")
loops = contract.get("open_loops", {})
for loop, owner in (("topic_state_from_waivers", "16C"), ("app_calls_diagnostic", "16D"), ("entry_placement", "16D"),
                    ("planner_initiated_diagnostic", "18B"), ("waiver_across_curriculum_versions", "15"), ("authored_items_and_lessons", "15"),
                    ("waiver_replay_cost", "18E"), ("wording", "14")):
    check(f"E13F-17_open_{loop[:30]}", str(loops.get(loop, {}).get("owner")) == owner, f"{loop} not owned")
forbidden = set(contract.get("forbidden_patterns", []))
for pattern in ("self_report_as_evidence", "lower_bar_for_diagnostic", "single_item_topic_waiver", "waiver_after_fast_path_ended",
                "waiver_where_learning_passed_first", "waiver_as_mastery", "untaught_miss_as_weakness", "help_as_penalty",
                "teaching_while_diagnosing", "skipping_unshown_objective", "planning_reads_evidence"):
    check(f"E13F-18_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 13F QA PASS", "**Decision:** `D-104`", "A diagnostic is not an easier road to mastery",
                 "Not run: T6", "14A — Tutor davranış sözleşmesi", "AŞAMA 13 is complete"]:
    check(f"E13F-19_spec_{fragment[:26]}", fragment in spec_text, f"spec: {fragment!r}")
check("E13F-19_spec_mutation_count", f"Mutation {mutation.get('detected')}/{mutation.get('total')}" in spec_text, "spec mutation count")
for fragment in ["No web research pass was needed", "VDW-v0", "16C", "16D", "user decisions"]:
    check(f"E13F-19_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E13F-20_no_repeated_context_heading", len(context_headings) == len(set(context_headings)), "repeated context heading")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E13F-20_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)), "repeated master plan step")
combining = chr(0x0307)
check("E13F-20_no_combining_dot", all(combining not in read(p) for p in (SPEC, RESEARCH, CONTRACT, FACTS_KT, PRES_KT)), "U+0307 in 13F text")

passed_count = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "VDWX-v0", "stage_step": "13F", "decision": "D-104",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed_count, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"13F_DIAGNOSTIC_WAIVER_QA={report['result']}")
print(f"checks={passed_count}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
