"""Independent 14B QA — WAAX-v0 Wrong-Answer Analysis & Misconception Memory.

The lifecycle is read from `WLRM-v0`'s misconception and state contracts, the proposal field from `AIAX-v0` §5.1, the
hypothesis rule from `SPWX-v0` §10.1, the open review from the 6G queue, and all of them are compared with the real
Kotlin — never with 14B's own contract alone. The rules the step exists for must be **unrepresentable** as a PASS here:
an undeclared label stored, an AI proposal above a hypothesis, a label rising faster than its Objective, unattributable
work moving a label, a misconception on a right answer, resolution by time, a hypothesis stated as a finding, and an
analysis that recomputes its own attribution.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/14b_wrong_answer_analysis/wrong_answer_analysis.yaml"
SPEC = ROOT / "docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md"
RESEARCH = ROOT / "research/14b_wrong_answer_analysis_research.md"
QA_OUT = ROOT / "arch/14b_wrong_answer_analysis/qa_report.yaml"
G6 = ROOT / "curriculum/decomposition/6g_weakness_remediation"
AIAX = ROOT / "docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md"
SPWX = ROOT / "docs/PROGRESS_SKILL_UX_SPEC.md"
LBR = ROOT / "docs/LEARNING_BEHAVIOR_RULES.md"
DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
TUTX = ROOT / "arch/14a_tutor_contract/tutor_contract.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/MisconceptionFacts.kt"
EVAL_KT = ANDROID / "core-model/src/main/kotlin/coach/Evaluation.kt"
WEAK_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/WeaknessFacts.kt"
EVIDENCE_KT = ANDROID / "core-model/src/main/kotlin/coach/EvidenceFacts.kt"
PKG_KT = ANDROID / "core-model/src/main/kotlin/coach/CurriculumPackage.kt"
ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/MisconceptionEngine.kt"
WEAK_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/WeaknessEngine.kt"
OWN_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/EngineOwnership.kt"
PIPE_KT = ANDROID / "core-application/src/main/kotlin/coach/application/EvidencePipeline.kt"
REBUILD_KT = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildWeakness.kt"
ANALYZE_KT = ANDROID / "core-application/src/main/kotlin/coach/application/AnalyzeWrongAnswer.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/WrongAnswerPresentation.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SQL_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
STORE_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/CurriculumStore.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIG_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"

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


def load(path: Path):
    try:
        return yaml.safe_load(read(path)) or {}
    except yaml.YAMLError:
        return {}


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, ENGINE_KT, ANALYZE_KT, PRES_KT):
    check(f"E14B-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = load(CONTRACT)
mc, sc, queue = load(G6 / "misconception_contract.yaml"), load(G6 / "state_contract.yaml"), load(G6 / "review_queue.yaml")
facts_raw, pres_raw = read(FACTS_KT), read(PRES_KT)
facts, evaluation, weak_facts, evidence, pkg = (strip_comments(facts_raw), strip_comments(read(EVAL_KT)), strip_comments(read(WEAK_FACTS_KT)),
                                                strip_comments(read(EVIDENCE_KT)), strip_comments(read(PKG_KT)))
engine, weak, own = strip_comments(read(ENGINE_KT)), strip_comments(read(WEAK_KT)), strip_comments(read(OWN_KT))
pipe, rebuild, analyze, pres = strip_comments(read(PIPE_KT)), strip_comments(read(REBUILD_KT)), strip_comments(read(ANALYZE_KT)), strip_comments(pres_raw)
ports, sql, store, schema, mig, fmt = (strip_comments(read(PORTS_KT)), strip_comments(read(SQL_KT)), strip_comments(read(STORE_KT)),
                                       strip_comments(read(SCHEMA_KT)), strip_comments(read(MIG_KT)), strip_comments(read(FORMAT_KT)))
aiax, spwx, lbr, spec_text, research_text = read(AIAX), read(SPWX), read(LBR), read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- contract head and the accepted texts it rests on
check("E14B-01_model", contract.get("model") == "WAAX-v0", str(contract.get("model")))
check("E14B-01_status", contract.get("status") == "accepted_14b", str(contract.get("status")))
check("E14B-01_decision", contract.get("decision") == "D-106", str(contract.get("decision")))
scope = contract.get("scope", {})
for key, expected in {"closed_catalog": True, "labels_written_on_evidence": True, "misconception_memory": True, "analysis_reported": True,
                      "schema_changed": True, "projection_added": True, "curriculum_table_added": True, "state_family_added": False,
                      "weakness_rule_changed": False, "mastery_rule_changed": False, "planner_rule_changed": False, "labels_authored": False,
                      "memory_used_for_selection": False, "app_calls_analysis": False}.items():
    check(f"E14B-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E14B-01_user_decisions", contract.get("user_decisions") == {"date": "2026-10-01", "catalog": "closed_curriculum_authored",
      "undeclared_label_stored": False, "hypothesis_shown": "only_as_open_question_right_after_the_answer"}, str(contract.get("user_decisions")))
mc_rules = mc.get("rules", [])
check("E14B-01_wlrm_llm_hypothesis_only", "LLM_may_propose_a_hypothesis_but_cannot_publish_confirmed_misconception_without_evidence_policy" in mc_rules, str(mc_rules))
check("E14B-01_wlrm_no_mastery", "misconception_memory_guides_remediation_and_item_selection_but_does_not_directly_set_mastery" in mc_rules, "")
check("E14B-01_wlrm_not_by_time", "resolved_requires_subsequent_evidence_or_explicit_runtime_resolution_not_time_alone" in mc_rules, "")
check("E14B-01_aiax_hypotheses", "misconception_hypotheses[]   # hypotheses, never confirmed weakness" in aiax, "AIAX-v0 §5.1 moved")
check("E14B-01_spwx_question", "hypothesis  -> may appear at most as an open question, never as a deficiency" in spwx, "SPWX-v0 §10.1 moved")
check("E14B-01_lbr_signal", "Yanlış cevap ceza değil öğrenme sinyalidir" in lbr, "LEARNING_BEHAVIOR_RULES §5 moved")
review = next((r for r in queue if isinstance(r, dict) and r.get("review_id") == "review.6g.misconception_taxonomy_expansion"), {})
check("E14B-01_review_owned_here", review.get("resolution_owner_step") == "14B", str(review))

# ---------------------------------------------------------------- vocabularies from the accepted contracts
lifecycle = mc.get("states", [])
kotlin_signals = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", \d\)', body(weak_facts, "enum class WeaknessSignal"), re.M)
check("E14B-02_lifecycle_from_wlrm", ["none"] + lifecycle == kotlin_signals == contract.get("memory", {}).get("lifecycle")
      and sc.get("weakness_signal_states") == kotlin_signals, f"{lifecycle} vs {kotlin_signals}")
sources = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", WeaknessSignal\.([A-Z]+)\)', body(facts, "enum class MisconceptionSource"), re.M)
check("E14B-02_sources_and_ceilings", sources == [("deterministic", "CONFIRMED"), ("ai_proposed", "HYPOTHESIS")]
      and contract.get("memory", {}).get("source_ceiling") == {"deterministic": "confirmed", "ai_proposed": "hypothesis"}, str(sources))
check("E14B-02_schema_sources", "const val MISCONCEPTION_SOURCES = \"'', 'deterministic', 'ai_proposed'\"" in schema, "schema sources")
mc_fields = [f.rstrip("?") for f in mc.get("fields", [])]
for name in ("evidence_refs", "first_seen_at", "last_seen_at", "resolution_evidence_refs"):
    check(f"E14B-02_contract_field_{name}", name in mc_fields, f"{name} not in WLRM-v0's contract")

# ---------------------------------------------------------------- the closed catalog
row_params = re.findall(r"val (\w+):", body(facts, "data class MisconceptionRow(").split(") {")[0])
check("E14B-03_catalog_row", row_params == ["ref", "objective", "name", "openQuestion"], str(row_params))
check("E14B-03_id_pattern", 'Regex("^misconception(\\\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")' in facts, "catalog id pattern")
check("E14B-03_question", 'openQuestion.trimEnd().endsWith("?")' in facts and "CHECK (trim(open_question) LIKE '%?')" in schema, "a label is stated")
v8 = schema[schema.find("val v8: List<String>"):schema.find("const val MISCONCEPTION_SOURCES")] if "val v8: List<String>" in schema else ""
check("E14B-03_table", "CREATE TABLE IF NOT EXISTS misconception (" in v8 and
      "FOREIGN KEY (objective_logical_id, objective_version) REFERENCES objective (logical_id, version)" in v8, "catalog table")
check("E14B-03_immutable", 'val curriculumExtensionTables = listOf("misconception")' in schema and "curriculumExtensionTables.flatMap" in v8
      and "is immutable: publish a new version instead" in v8, "catalog editable")
check("E14B-03_unresolved_refused", 'misconceptions.filterNot { resolves(OBJECTIVE, it.objective, objectiveKeys) }' in pkg, "a label pinned to nothing")
check("E14B-03_version_refused", 'curriculum.misconceptions.filter { it.ref.version != expected }' in store, "a label of another version")
check("E14B-03_earlier_objective", 'CurriculumPackage.OBJECTIVE -> "objective"' in store, "an earlier published Objective")
check("E14B-03_port_refinement", "fun misconceptionsOf(objective: VersionedRef): List<MisconceptionRow>" in ports
      and "WHERE objective_logical_id = ? AND objective_version = ?" in store
      and contract.get("port_refinements") == [{"port": "PersistencePort", "method": "misconceptionsOf"}], "port refinement")
check("E14B-03_package_section", '"misconception" to setOf("logical_id", "version", "objective", "name", "open_question"),' in fmt
      and 'sections.filter { it.name == "misconception" }.map { reader.misconception(it) }' in fmt, "package section")
ddm = load(DDM)
check("E14B-03_ddm_unedited", "misconception" not in [e["id"] for e in ddm.get("curriculum_entities", [])]
      and "misconception_state" not in [e["id"] for e in ddm.get("projection_entities", [])], "DDM-v0 edited")
check("E14B-03_extensions_declared", contract.get("curriculum_extension", {}).get("table") == "misconception"
      and contract.get("projection_extension") == {"table": "misconception_state", "grain": "per_catalog_label", "owner": "WLRM-v0", "decision": "D-106"},
      str(contract.get("projection_extension")))

# ---------------------------------------------------------------- labels on evidence
check("E14B-04_eval_field", evaluation.count("val misconceptionHypotheses: List<MisconceptionHypothesis> = emptyList(),") == 2, "AIAX-v0 §5.1 field")
record = body(pipe, "fun record(")
check("E14B-04_only_wrong", ".filter { it.signal == OutcomeSignal.NOT_MET || it.signal == OutcomeSignal.PARTIALLY_MET }" in record, "a right answer carries labels")
check("E14B-04_only_declared", "catalog.filter { it.ref.logicalId in proposed }" in record and "persistence.misconceptionsOf(component.objectiveRef)" in record, "undeclared labels")
check("E14B-04_own_objective", "hypotheses.filter { it.objective == component.objectiveRef }" in record, "another Objective's proposal")
check("E14B-04_sources", "is EvaluationResult.Verified -> evaluation.misconceptionHypotheses to MisconceptionSource.DETERMINISTIC" in record
      and "is EvaluationResult.Provisional -> evaluation.misconceptionHypotheses to MisconceptionSource.AI_PROPOSED" in record, "sources")
check("E14B-04_written", 'MisconceptionTags.encode(tags[component.objectiveRef].orEmpty())?.let { put("misconception_tags", it) }' in record, "tags not written")
check("E14B-04_pending_writes_nothing", "is EvaluationResult.EvaluationPending -> return Recorded(emptyList())" in record, "a non-answer writes")
check("E14B-04_format", 'const val FORMAT = "misconception_tags/1"' in facts and "if (parts.first() != FORMAT || parts.size < 2) return null" in facts, "format")
check("E14B-04_read", "misconceptionTags = MisconceptionTags.decode(if (statement.isNull(20)) null else statement.getText(20)).orEmpty()," in sql
      and "e.misconception_tags" in body(sql, "override fun evidenceFor("), "tags not read")

# ---------------------------------------------------------------- the memory
reach = body(engine, "fun reach(")
for fragment in ("FailureRule.ASSISTED_H1_H4, FailureRule.PROVISIONAL_OR_PARTIAL -> WeaknessSignal.HYPOTHESIS",
                 "FailureRule.CLEAN_PREMASTERY_H0_DIRECT, FailureRule.FIRST_CLEAN_POSTMASTERY_CONTRADICTION -> WeaknessSignal.SUPPORTED",
                 "FailureRule.FRESH_RECHECK_FAIL -> if (!event.masteredAfter) WeaknessSignal.CONFIRMED else WeaknessSignal.SUPPORTED",
                 "else -> null"):
    check(f"E14B-05_reach_{fragment[:30]}", fragment in reach, f"missing {fragment!r}")
reach_ids = [r["rule"] for r in contract.get("memory", {}).get("reach", [])]
rule_ids = re.findall(r'\("(failure\.[a-z0-9_]+)", \d+\)', body(weak_facts, "enum class FailureRule"))
check("E14B-05_reach_rules_exist", all(r in rule_ids for r in reach_ids) and len(rule_ids) == 12, f"{reach_ids} vs {len(rule_ids)} rules")
walk = body(engine, "private fun walk(")
check("E14B-05_engine_rule", "val rule = WeaknessEngine.rule(weakness, event)" in walk and "val next = WeaknessEngine.step(weakness, event)" in walk,
      "the memory attributes by itself")
check("E14B-05_cap", "val capped = if (reach.strength > tag.source.ceiling.strength) tag.source.ceiling else reach" in walk, "the source ceiling ignored")
check("E14B-05_declared_only", "event.misconceptionTags.filter { it.misconception in states }" in walk
      and "val declared = catalog.filter { it.objective == objective }.associateBy { it.ref }" in walk, "undeclared label moves")
check("E14B-05_resolution", "if (rule == FailureRule.FRESH_RECOVERY_SUCCESS) {" in walk and "WeaknessSignal.CONFIRMED -> event.masteredAfter" in body(engine, "private fun resolve("),
      "resolution")
raise_fn = body(engine, "private fun raise(")
check("E14B-05_never_lowered", "val signal = if (!open || to.strength > state.signal.strength) to else state.signal" in raise_fn, "a label lowered")
check("E14B-05_not_by_time", 'require(signal != WeaknessSignal.RESOLVED || resolutionEvidenceId != null)' in facts
      and "CHECK (state <> 'resolved' OR coalesce(resolution_evidence_id, '') <> '')" in v8, "resolution by time")
check("E14B-05_ai_ceiling_in_store", "CHECK (source <> 'ai_proposed' OR state IN ('hypothesis', 'resolved'))" in v8, "store allows an AI label above a hypothesis")
check("E14B-05_weakness_engine_unchanged", "misconception" not in body(weak, "fun rule(").lower() and "misconception" not in body(weak, "fun step(").lower(),
      "the weakness rules changed")
check("E14B-05_owner", 'const val POLICY_VERSION = "WLRM-v0"' in engine and 'WEAKNESS("WLRM-v0", "weakness_and_remediation_state")' in own
      and "MISCONCEPTION" not in own, "a new family or another policy")
check("E14B-05_written_in_rebuild", "misconceptions.forEach { MisconceptionRows.write(persistence, it, today, watermark, version, builtAt) }" in rebuild
      and "misconceptionTags = row.misconceptionTags," in rebuild, "rebuild")

# ---------------------------------------------------------------- the analysis and what is said
analyze_fn = body(analyze, "fun analyze(")
check("E14B-06_stored_attribution", "WeaknessEvents.of(persistence, skill, profiles)" in analyze_fn and "MisconceptionEngine.findings(" in analyze_fn
      and "WeaknessEvents.of(persistence, skill, profiles)" in body(rebuild, "fun rebuild("), "the analysis recomputes")
check("E14B-06_writes_nothing", not re.search(r"persistence\.(appendTruth|writeProjection|inTransaction)", analyze) and "Evaluator" not in analyze
      and "Tutor" not in analyze, "the analysis writes or asks")
summary = body(pres, "fun of(")
check("E14B-06_only_after_blame", "findings.filter { it.outcome in speaksAboutTarget }" in summary, "a label after a blameless row")
speaks = re.findall(r"AttributionOutcome\.([A-Z_]+)", body(pres, "private val speaksAboutTarget"))
check("E14B-06_speaks_set", [s.lower() for s in speaks] == contract.get("analysis", {}).get("labels_mentioned_only_after"), str(speaks))
check("E14B-06_hypothesis_question", "openQuestions = relevant.filter { it.second == WeaknessSignal.HYPOTHESIS }.map { it.first.openQuestion }" in summary,
      "a hypothesis stated")
check("E14B-06_named_only_supported", "it.second == WeaknessSignal.SUPPORTED || it.second == WeaknessSignal.CONFIRMED }" in summary, "a hypothesis or resolved label named")
outcome_fn = body(pres, "fun outcome(")
check("E14B-06_every_outcome", all(f"AttributionOutcome.{o.upper()}" in outcome_fn for o in sc.get("attempt_attribution_outcomes", [])) and "null ->" in outcome_fn,
      "an attribution without a sentence")
copy = re.findall(r'"([^"]*[a-zçğıöşü][^"]*)"', body(pres_raw, "object WrongAnswerCopy"))
lowered = [s.lower() for s in copy]
for word in ("%", "puan", "başarısız", "zayıf", "eksik", "öğrendin", "ustalaştın", "geride", "borç", "tembel", "dikkatsiz"):
    check(f"E14B-06_copy_no_{word.strip()[:12]}", all(word not in s for s in lowered), f"{word!r} in copy")
check("E14B-06_copy_no_number", not re.search(r"\d", " ".join(copy)), "a number in copy")
check("E14B-06_no_combining_dot", chr(0x0307) not in pres_raw + facts_raw, "U+0307 in copy")

# ---------------------------------------------------------------- storage and boundaries
check("E14B-07_schema_version", "const val VERSION = 8" in schema and "7 to Schema.v8," in mig and contract.get("schema_version") == 8, "schema version")
check("E14B-07_projection", '"misconception_state" to listOf("misconception_logical_id", "misconception_version")' in sql
      and '"diagnostic_coverage", "misconception_state",' in schema, "projection key")
check("E14B-07_truth_untouched", not re.search(r"ALTER TABLE (evidence_event|attempt|assessment_session)", v8) and "truthTables" not in v8, "truth altered")
msbx = load(MSBX)
interfaces = sorted(re.findall(r"^interface (\w+)", ports, re.M))
tutx = load(TUTX)
check("E14B-07_ports", interfaces == sorted([p["id"] for p in msbx.get("ports", {}).get("set", [])] + [tutx.get("port_extension", {}).get("port")])
      and contract.get("port_count") == 5, str(interfaces))

# ---------------------------------------------------------------- tests that carry the rules
suites = contract.get("suites", {})
named_tests = {
    "model": ["a catalog label has a dotted misconception id and is put to the learner as a question", "stored tags decode completely or not at all",
              "an AI-proposed label can never be held stronger than a hypothesis", "a label is resolved by evidence, never by time"],
    "engine": ["an AI-proposed label stays a hypothesis however clean the row", "a deterministic label on a clean failure before mastery is supported",
               "a deterministic label is confirmed only when a fresh recheck fails and the gates no longer pass",
               "invalid, contested or prerequisite-contaminated work moves no label", "a label the catalog does not declare for the Objective names nothing",
               "a fresh clean success resolves an open label, a confirmed one only when the gates pass again"],
    "application": ["only catalog labels on a row that went wrong are recorded, each with where it came from",
                    "the rebuild remembers every catalog label, carried or not", "the analysis a learner reads is the stored attribution, not a second opinion"],
    "presentation": ["a hypothesis is only ever put as its open question", "a label on a row that blamed nothing is not mentioned"],
    "storage": ["migrating a populated schema-7 database adds the catalog and the memory and preserves every truth row",
                "the store refuses an AI-proposed label above a hypothesis, a resolution without evidence and an unknown state"],
    "format": ["a misconception label is read strictly and pinned to its Objective"],
}
for suite, names in named_tests.items():
    text = read(ROOT / suites.get(suite, {}).get("file", "missing"))
    for name in names:
        check(f"E14B-08_{suite}_{name[:40]}", f"`{name}`" in text, f"missing test: {name}")

# ---------------------------------------------------------------- honesty
mutation = contract.get("mutation_results", {}) if isinstance(contract.get("mutation_results"), dict) else {}
check("E14B-09_mutation_all_detected", mutation.get("detected") == mutation.get("total") == len(mutation.get("mutants", [])) >= 40,
      f"{mutation.get('detected')}/{mutation.get('total')}")
check("E14B-09_mutation_control", mutation.get("negative_control_result") == "survived_as_expected" and mutation.get("compile_failure_is_detection") is False
      and mutation.get("only_14b_suites_run") is True and mutation.get("final_run_is_a_single_clean_run") is True
      and mutation.get("harness_verified_to_run_gradle") is True, "harness honesty")
vm = contract.get("validator_mutation", {}) if isinstance(contract.get("validator_mutation"), dict) else {}
check("E14B-09_validator_mutation", vm.get("detected") == vm.get("total") and (vm.get("total") or 0) >= 20 and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E14B-09_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
device = contract.get("device_verification", {})
check("E14B-09_device_not_claimed", device.get("t6_run") is False and device.get("claimed") is False, "a device result is claimed")
loops = contract.get("open_loops", {})
for loop, owner in (("authored_labels_and_deterministic_keys", "15"), ("memory_for_item_choice_and_contrast_content", "15"),
                    ("progress_view_of_misconceptions", "16C"), ("app_calls_analysis", "16D"), ("catalog_expansion_and_escalation_calibration", "18"),
                    ("code_evaluation_labels", "14D"), ("llm_evaluator_proposals", "14F")):
    check(f"E14B-09_open_{loop[:30]}", str(loops.get(loop, {}).get("owner")) == owner, f"{loop} not owned")
forbidden = set(contract.get("forbidden_patterns", []))
for pattern in ("undeclared_label_stored", "ai_label_above_hypothesis", "label_faster_than_objective_attribution", "unattributable_work_moves_label",
                "misconception_on_right_answer", "label_resolved_by_time", "hypothesis_stated_as_finding", "label_after_blameless_row",
                "wrong_answer_as_verdict", "analysis_recomputes_attribution"):
    check(f"E14B-10_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
check("E14B-10_review_resolved", contract.get("review_resolved", {}).get("id") == "review.6g.misconception_taxonomy_expansion"
      and review.get("status") == "resolved" and "14B" in str(review.get("resolution_refs", "")), str(review))
for fragment in ["**Status:** ACCEPTED — independent 14B QA PASS", "**Decision:** `D-106`", "A wrong answer is information, not a verdict about the learner",
                 "Not run: T6", "14C — Alternatif anlatım"]:
    check(f"E14B-11_spec_{fragment[:26]}", fragment in spec_text, f"spec: {fragment!r}")
check("E14B-11_spec_mutation_count", f"Mutation {mutation.get('detected')}/{mutation.get('total')}" in spec_text, "spec mutation count")
for fragment in ["No web research pass was needed", "WLRM-v0", "AIAX-v0", "user decisions", "review.6g.misconception_taxonomy_expansion"]:
    check(f"E14B-11_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E14B-12_no_repeated_context_heading", len(context_headings) == len(set(context_headings)), "repeated context heading")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E14B-12_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)), "repeated master plan step")
check("E14B-12_no_combining_dot", all(chr(0x0307) not in read(p) for p in (SPEC, RESEARCH, CONTRACT, FACTS_KT, PRES_KT)), "U+0307 in 14B text")

passed_count = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "WAAX-v0", "stage_step": "14B", "decision": "D-106",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed_count, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"14B_WRONG_ANSWER_ANALYSIS_QA={report['result']}")
print(f"checks={passed_count}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
