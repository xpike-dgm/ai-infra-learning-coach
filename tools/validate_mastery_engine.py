"""Independent 12A QA — MSTX-v0 Mastery Engine v1.

The implementation is validated against the accepted contracts, not against its own. The constants,
gates, aggregation rule and hysteresis are read out of `GRE-v0`'s own text (`MASTERY_FORMULA_V0.md`)
where it states them, the axes and projection provenance out of `DDM-v0`, the mastery axis values out
of `SPWX-v0`, the engine ownership out of `MSBX-v0` — each compared with the actual Kotlin.

The checks that matter most are structural: an average across Objectives, a mastery percentage, a
score for something that could not be measured, and an engine writing another engine's axis must all
be **unrepresentable**, not merely absent today.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/12a_mastery_engine/mastery_engine.yaml"
SPEC = ROOT / "docs/MASTERY_ENGINE_IMPL_SPEC.md"
RESEARCH = ROOT / "research/12a_mastery_engine_research.md"
QA_OUT = ROOT / "arch/12a_mastery_engine/qa_report.yaml"

GRE = ROOT / "docs/MASTERY_FORMULA_V0.md"
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/MasteryEngine.kt"
OWNERSHIP_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/EngineOwnership.kt"
FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/EvidenceFacts.kt"
PIPELINE_KT = ANDROID / "core-application/src/main/kotlin/coach/application/EvidencePipeline.kt"
REBUILD_KT = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildMastery.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ADAPTER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"

ENGINE_TEST = ANDROID / "core-engines/src/test/kotlin/coach/engines/MasteryEngineTest.kt"
PIPELINE_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/EvidencePipelineTest.kt"
REBUILD_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/RebuildMasteryTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/EvidenceStorageTest.kt"

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


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"//[^\n]*", "", source)


def enum_ids(source: str, name: str) -> list[str]:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return re.findall(r'\(\s*"([A-Za-z0-9_]+)"', match.group(1)) if match else []


def body(source: str, signature: str) -> str:
    """A declaration's own text, whether it has a brace body or an expression body.

    The parameter list is balanced first: a default value inside it (`x: Boolean = false`) contains
    an `=` long before the body starts, and a reader that stops at the first `=` silently returns an
    empty string — a check that then passes on anything. 11D hit the same class of defect.
    """
    start = source.find(signature)
    if start < 0:
        return ""
    cursor = source.find("(", start)
    if cursor < 0:
        return ""
    depth = 0
    for i in range(cursor, len(source)):
        depth += {"(": 1, ")": -1}.get(source[i], 0)
        if depth == 0:
            cursor = i + 1
            break
    rest = source[cursor:]
    brace = rest.find("{")
    equals = rest.find("=")
    if brace >= 0 and (equals < 0 or brace < equals):
        depth = 0
        for i in range(cursor + brace, len(source)):
            depth += {"{": 1, "}": -1}.get(source[i], 0)
            if depth == 0:
                return source[cursor + brace + 1:i]
        return ""
    end = source.find("\n\n", cursor)
    return source[start:end if end > 0 else len(source)]


def constructor_params(source: str, cls: str) -> list[str]:
    match = re.search(rf"class {cls}\b[^(]*\((.*?)\n\s*\)", source, re.S)
    return re.findall(r"val (\w+)\s*:", match.group(1)) if match else []


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


contract = load(CONTRACT)
progress = load(PROGRESS)
ddm = load(DDM)
msbx = load(MSBX)
gre = read(GRE)

for path in (ENGINE_KT, FACTS_KT, PIPELINE_KT, REBUILD_KT, ENGINE_TEST, PIPELINE_TEST, REBUILD_TEST,
             T2_TEST, SPEC, RESEARCH):
    check(f"E12A-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

engine = strip_comments(read(ENGINE_KT))
ownership = strip_comments(read(OWNERSHIP_KT))
facts = strip_comments(read(FACTS_KT))
pipeline = strip_comments(read(PIPELINE_KT))
rebuild = strip_comments(read(REBUILD_KT))
ports = strip_comments(read(PORTS_KT))
adapter = strip_comments(read(ADAPTER_KT))
schema = read(SCHEMA_KT)
engine_test = read(ENGINE_TEST)
pipeline_test = read(PIPELINE_TEST)
rebuild_test = read(REBUILD_TEST)
t2_test = read(T2_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E12A-01_model", contract.get("model") == "MSTX-v0", str(contract.get("model")))
check("E12A-01_status", contract.get("status") == "accepted_12a", str(contract.get("status")))
check("E12A-01_decision", contract.get("decision") == "D-092", str(contract.get("decision")))
for key in ("retention_engine_implemented", "prerequisite_engine_implemented", "planner_implemented",
            "topic_state_implemented", "weakness_engine_implemented", "rubrics_implemented",
            "threshold_calibrated", "schema_changed", "migration_added", "ports_added",
            "boundaries_changed", "surface_semantics_changed"):
    check(f"E12A-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("mastery_percentage_claim", "probability_or_confidence_claim"):
    check(f"E12A-01_no_{key}", contract["scope"].get(key) is None, f"{key} was claimed")

# ---------------------------------------------------------------- constants, read out of GRE-v0
gre_constants = {
    "RECENT_WINDOW_MAX_GROUPS_V0": re.search(r"RECENT_WINDOW_MAX_GROUPS_V0 = (\d+)", gre),
    "OBJECTIVE_MASTERY_THRESHOLD_V0": re.search(r"OBJECTIVE_MASTERY_THRESHOLD_V0 = ([\d.]+)", gre),
}
for name, match in gre_constants.items():
    declared = match.group(1) if match else None
    in_kotlin = re.search(rf"const val {name} = ([\d.]+)", engine)
    check(f"E12A-02_constant_{name}", declared is not None and in_kotlin is not None
          and float(in_kotlin.group(1)) == float(declared), f"gre={declared} kotlin={in_kotlin and in_kotlin.group(1)}")
# §8 and §9 minimums are stated in prose; the contract records them and the code must agree with it.
for name, key in (("MIN_INDEPENDENT_GROUPS_STANDARD", "min_independent_groups_standard"),
                  ("MIN_INDEPENDENT_GROUPS_CRITICAL", "min_independent_groups_critical"),
                  ("MIN_VARIANT_FAMILIES_STANDARD", "min_variant_families_standard"),
                  ("MIN_VARIANT_FAMILIES_CRITICAL", "min_variant_families_critical")):
    declared = contract["declared_constants"][key]
    in_kotlin = re.search(rf"const val {name} = (\d+)", engine)
    check(f"E12A-02_minimum_{name}", in_kotlin is not None and int(in_kotlin.group(1)) == declared,
          f"contract={declared} kotlin={in_kotlin and in_kotlin.group(1)}")
check("E12A-02_formula_version", 'const val MASTERY_FORMULA_VERSION = "GRE-v0"' in engine,
      "the projection is not stamped with GRE-v0")
for key in ("is_probability", "is_confidence", "is_ability_estimate", "shown_to_learner_as_percentage"):
    check(f"E12A-02_declared_{key}", contract["declared_constants"][key] is False, f"{key} was claimed")
check("E12A-02_calibration_owner", contract["declared_constants"]["calibrated_at"] == "18C",
      "the constants are not recorded as uncalibrated")

# ---------------------------------------------------------------- eligibility
exclusions = body(engine, "fun exclusions(")
for rule, fragment in (
    ("not_direct_evidence_for_objective", "profile.directEvidenceTypes"),
    ("not_independent", "IndependenceClass.INDEPENDENT"),
    ("evaluator_not_verified", "EvaluatorStatus.VERIFIED"),
    ("outcome_invalid", "EvidenceOutcome.INVALID"),
    ("contested", "row.contested"),
    ("prerequisite_contaminated", "row.prerequisiteValid"),
    ("solution_exposed", "row.solutionExposed"),
    ("no_group_result", "row.quality == null"),
):
    check(f"E12A-03_excludes_{rule}", fragment in exclusions, f"{rule} is not enforced")
check("E12A-03_exclusion_vocabulary",
      set(enum_ids(facts, "EvidenceExclusion")) == set(contract["eligibility"]["excluded_kinds"]),
      f"kotlin={enum_ids(facts, 'EvidenceExclusion')}")
check("E12A-03_group_result_is_bounded",
      'require(quality == null || quality in 0.0..1.0)' in facts,
      "a group result outside [0,1] can be constructed")
check("E12A-03_assisted_is_not_a_penalty", contract["eligibility"]["assisted_evidence_is_penalty"] is False
      and contract["eligibility"]["assisted_evidence_kept_as_formative"] is True,
      "assisted evidence is recorded as a penalty")

# ---------------------------------------------------------------- grouping, window, score
grouping = body(engine, "fun groupsOf(")
check("E12A-04_dependency_group_is_one_group", "it.dependencyGroupId ?:" in grouping,
      "a dependency group does not collapse into one group")
check("E12A-04_window_bounded", "takeLast(RECENT_WINDOW_MAX_GROUPS_V0)" in body(engine, "fun recentWindow("),
      "the window is not bounded")
decide = body(engine, "fun decide(")
check("E12A-04_equal_weighted_mean", "window.map { it.quality }.average()" in decide,
      "the score is not an equal-weighted mean of the window")
check("E12A-04_no_multipliers", not re.search(r"multiplier|weightOf|\*\s*(assist|difficulty|recency)", engine, re.I),
      "a multiplier appears in the engine")

# ---------------------------------------------------------------- gates and aggregation
for gate in ("recent_direct_score", "min_independent_groups", "min_variant_families", "required_direct_type",
             "no_unresolved_recheck", "no_unresolved_verification"):
    check(f"E12A-05_gate_{gate}", f'gate("{gate}"' in decide, f"missing gate {gate}")
check("E12A-05_critical_non_basic", 'gate("non_basic_evidence"' in decide, "a critical Objective may pass on basic evidence")
check("E12A-05_user_authored_gate", 'gate("user_authored_artifact"' in decide, "the user-authored artifact gate is missing")
check("E12A-05_transfer_gate", 'gate("transfer_evidence"' in decide, "the transfer gate is missing")

skill = body(engine, "fun decideSkill(")
check("E12A-06_non_compensatory", "gating.all { it.passed }" in skill,
      "a Skill is not gated on every required and critical Objective")
check("E12A-06_no_average_across_objectives",
      not re.search(r"decisions\.(map|mapNotNull)\s*\{[^}]*recentDirectScore[^}]*\}\s*\.average\(\)", skill),
      "a Skill is decided on an average of its Objectives")
check("E12A-06_optional_not_gating", "it.required || it.critical" in skill, "an optional Objective gates the Skill")
check("E12A-06_critical_recheck_blocks", "unresolvedCriticalRecheck" in skill,
      "an unresolved critical recheck does not block the Skill")

# ---------------------------------------------------------------- hysteresis
check("E12A-07_first_contradiction_keeps_mastery", "firstContradiction" in decide
      and "objectivePassed || firstContradiction" in decide,
      "the first contradiction unmasters instead of opening verification")
check("E12A-07_failed_recheck_resolves",
      "val recheckAlsoFailed = contradicted && unresolvedVerification" in decide
      and "if (recheckAlsoFailed) false else" in decide,
      "a failed recheck does not resolve the verification")
check("E12A-07_contradiction_needs_eligible_evidence",
      "eligible.any {" in decide and "EvidenceOutcome.NEGATIVE" in decide,
      "a contradiction is counted from evidence that never qualified")

# ---------------------------------------------------------------- support band and trace
band = body(engine, "fun supportBand(")
check("E12A-08_band_values", enum_ids(facts, "SupportBand") == ["low", "medium", "high"],
      f"kotlin={enum_ids(facts, 'SupportBand')}")
check("E12A-08_failing_is_low", "!passed -> SupportBand.LOW" in band, "a failing Objective is not LOW")
trace_fields = constructor_params(engine, "MasteryDecisionTrace")
for field in ("masteryFormulaVersion", "objectiveRecentScores", "windowGroupKeys", "passedGates", "failedGates",
              "excludedEvidence", "assistedEvidenceCount", "unresolvedRechecks", "evaluatorStatusSummary",
              "decision", "reasonCodes"):
    check(f"E12A-08_trace_{field}", field in trace_fields, f"the trace does not carry {field}")

FORBIDDEN = ("percent", "confidence", "probability", "level", "grade", "rank", "streak")
for cls in ("ObjectiveDecision", "SkillDecision", "MasteryDecisionTrace", "EvidenceGroup"):
    fields = constructor_params(engine, cls)
    check(f"E12A-08_no_verdict_field_{cls}",
          not [f for f in fields if any(w in f.lower() for w in FORBIDDEN)], f"{cls} fields={fields}")

# ---------------------------------------------------------------- the pipeline
record = body(pipeline, "fun record(")
check("E12A-09_pending_writes_nothing",
      "is EvaluationResult.EvaluationPending -> return Recorded(emptyList())" in record,
      "a pending evaluation writes evidence")
check("E12A-09_one_transaction", record.count("inTransaction") == 1, "evidence is not written in one transaction")
for axis in ("outcome", "evaluator_status", "independence_class", "contested"):
    check(f"E12A-09_axis_{axis}", f'put("{axis}"' in record, f"{axis} is not written")
check("E12A-09_objective_version_pinned",
      '"objective_version" to component.objectiveRef.version.toString()' in record,
      "the Objective attribution is not version-pinned")
check("E12A-09_unmeasurable_is_invalid",
      "OutcomeSignal.NOT_RELIABLY_MEASURED -> EvidenceOutcome.INVALID" in pipeline,
      "an unmeasurable answer is recorded as something other than invalid")
check("E12A-09_unmeasurable_has_no_result",
      "OutcomeSignal.NOT_RELIABLY_MEASURED -> null" in pipeline, "an unmeasurable answer is scored")
check("E12A-09_provisional_stays_provisional",
      "is EvaluationResult.Provisional -> EvaluatorStatus.PROVISIONAL" in pipeline,
      "a provisional evaluation is written as verified")
check("E12A-09_evaluator_recorded", 'put("evaluator"' in record, "the evaluator that produced the row is not recorded")
check("E12A-09_writes_no_projection", "writeProjection" not in pipeline, "the pipeline writes a projection")

# ---------------------------------------------------------------- the projection
rebuild_body = body(rebuild, "fun rebuild(")
check("E12A-10_watermark_before_evidence",
      rebuild_body.index("truthWatermark()") < rebuild_body.index("evidenceFor("),
      "the watermark is read after the evidence")
check("E12A-10_no_truth_written", "appendTruth" not in rebuild, "the rebuild writes truth")
check("E12A-10_provenance_complete",
      all(field in rebuild_body for field in
          ("policyVersion", "truthWatermark", "builtAtInstant", "inputCurriculumVersion")),
      "a projection is written without full provenance")
check("E12A-10_other_axes_carried",
      'existing?.payload?.get("retention_axis_state")' in rebuild_body
      and 'existing?.payload?.get("prerequisite_axis_state")' in rebuild_body
      and 'existing?.payload?.get("weakness_axis_state")' in rebuild_body,
      "the engine overwrites another engine's axis")
check("E12A-10_no_unpinned_projection",
      "curriculumVersion ?: return" in rebuild_body,
      "a projection is pinned to a curriculum version that may not exist")
check("E12A-10_axis_values_are_spwx",
      set(enum_ids(facts, "MasteryAxisState")) <= set(progress["skill_presentation"]["states"]),
      f"kotlin={enum_ids(facts, 'MasteryAxisState')}")
check("E12A-10_engine_owns_mastery",
      'MASTERY("GRE-v0", "mastery_state")' in ownership,
      "the ownership map no longer says GRE-v0 owns mastery")

# ---------------------------------------------------------------- ports and schema
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx_ports = [p["id"] for p in msbx["ports"]["set"]]
check("E12A-11_port_count", sorted(interfaces) == sorted(list(msbx_ports) + declared_port_extensions(ROOT)), f"interfaces={interfaces}")
for method in ("evidenceFor", "truthWatermark", "latestCurriculumVersion"):
    check(f"E12A-11_refinement_{method}", f"fun {method}(" in ports, f"missing {method}")
evidence_read = body(adapter, "override fun evidenceFor(")
check("E12A-11_evidence_version_pinned",
      "o.objective_logical_id = ? AND o.objective_version = ?" in evidence_read,
      "evidence is read without its version pin")
check("E12A-11_evidence_read_only", not re.search(r"\b(INSERT|UPDATE|DELETE)\b", evidence_read),
      "reading evidence writes")
ddl = re.search(r"CREATE TABLE IF NOT EXISTS evidence_event \((.*?)\n\s*\)\n", schema, re.S)
ddl_text = ddl.group(1) if ddl else ""
for axis in ("outcome", "evaluator_status", "independence_class", "contested"):
    check(f"E12A-12_schema_axis_{axis}", re.search(rf"^\s*{axis}\s", ddl_text, re.M) is not None,
          f"{axis} is not its own column")
check("E12A-12_schema_unchanged", "mastery_score" not in schema and "mastery_percent" not in schema,
      "a score column was added to the schema")

# ---------------------------------------------------------------- tests
for name in ["one easy correct answer is not mastery",
             "the same question answered ten times is still one kind of evidence",
             "five correct answers with help are not independent evidence",
             "code the AI wrote does not pass an Objective that requires the learner's own artifact",
             "a wrong answer caused by an untaught prerequisite is excluded, not counted against the learner",
             "a critical Objective cannot be passed on basic evidence alone",
             "the first clean contradiction opens verification and does not unmaster",
             "a failed recheck resolves the verification and lets the gates decide again",
             "a Skill is mastered only when every required and critical Objective passes",
             "every decision explains itself, including what it excluded and why"]:
    check(f"E12A-13_engine_test_{name[:42]}", f"`{name}`" in engine_test, f"missing test: {name}")
for name in ["a pending evaluation writes nothing at all",
             "what could not be measured is invalid with no result, never a wrong answer",
             "the four axes are written separately and never collapsed"]:
    check(f"E12A-13_pipeline_test_{name[:42]}", f"`{name}`" in pipeline_test, f"missing test: {name}")
for name in ["it writes only the mastery axis and carries the other engines' axes",
             "rebuilding twice from the same evidence writes the same row",
             "the rebuild writes no truth at all"]:
    check(f"E12A-13_rebuild_test_{name[:42]}", f"`{name}`" in rebuild_test, f"missing test: {name}")
for name in ["an evidence row and its attribution commit together or not at all",
             "an Objective sees only evidence pinned to its own version, oldest first",
             "reading evidence writes nothing"]:
    check(f"E12A-13_t2_test_{name[:42]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E12A-14_device_not_claimed", device["t6_run"] is False and device["claimed"] is False,
      "a device result is claimed")
mutation = contract["mutation_results"]
check("E12A-14_mutation_all_detected",
      mutation["detected"] == mutation["total"] == len(mutation["mutants"]) >= 20,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E12A-14_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True,
      "the mutation harness was not verified to have run anything")
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E12A-14_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E12A-14_not_verified_named", len(contract.get("not_verified", [])) >= 3, "what was not verified must be named")
check("E12A-14_not_reachable_in_app", contract["wiring"]["reachable_in_app_today"] is False,
      "the contract claims the engine is reachable in the app")
boundaries = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("12B", "12C", "13", "14", "18C"):
    check(f"E12A-14_boundary_{owner}", owner in boundaries, f"missing {owner}")
forbidden = set(contract.get("forbidden_mastery_patterns", []))
for pattern in ("assisted_work_counted_as_independent_mastery", "provisional_evaluation_settling_a_gate",
                "exposed_solution_counted_as_fresh_evidence", "compensatory_average_across_objectives",
                "instant_unmastery_on_one_contradiction", "unmeasurable_answer_scored_zero",
                "mastery_percentage_or_probability", "engine_writing_another_engines_axis"):
    check(f"E12A-15_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 12A QA PASS", "**Decision:** `D-092`",
                 "without help", "T6", "18C"]:
    check(f"E12A-16_spec_{fragment[:26]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["No web research pass was needed", "GRE-v0", "hysteresis"]:
    check(f"E12A-17_research_{fragment[:24]}", fragment in research_text, f"missing={fragment!r}")

context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E12A-18_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "MSTX-v0", "stage_step": "12A", "decision": "D-092",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"12A_MASTERY_ENGINE_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
