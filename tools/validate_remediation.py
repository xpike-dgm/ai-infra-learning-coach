"""Independent 13D QA — WLRX-v0 Weakness Localization & Remediation Implementation.

The lifecycle, the attribution outcomes, the twelve rules and the need mappings are read out of the accepted
`WLRM-v0` dataset itself (`curriculum/decomposition/6g_weakness_remediation/`) and compared with the real Kotlin,
never with 13D's own contract alone.

The checks that matter most are structural: one failure failing a Skill, help or a provisional evaluation
confirming a remediation, a contradiction erasing mastery, a remediation closed by a finished task or one
success, a weakness broadcast beyond its Objective, a correction written over a stored row, and a second
weakness need for a concern the planner already opens must be **unrepresentable** as a PASS here.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
DATASET = ROOT / "curriculum/decomposition/6g_weakness_remediation"

CONTRACT = ROOT / "arch/13d_remediation_engine/remediation.yaml"
SPEC = ROOT / "docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md"
RESEARCH = ROOT / "research/13d_remediation_engine_research.md"
QA_OUT = ROOT / "arch/13d_remediation_engine/qa_report.yaml"
WLRM = ROOT / "docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
PLANNER_CONTRACT = ROOT / "arch/12c_planner_engine/planner_engine.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/WeaknessFacts.kt"
ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/WeaknessEngine.kt"
PLANNER_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
MASTERY_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/MasteryEngine.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildWeakness.kt"
TIMELINE_KT = ANDROID / "core-application/src/main/kotlin/coach/application/MasteryTimeline.kt"
RETENTION_APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildRetention.kt"
BUILD_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
BLUEPRINT_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BlueprintAssessment.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
SQL_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"

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
    depth = 0
    for index in range(open_at, len(source)):
        if source[index] == "{":
            depth += 1
        elif source[index] == "}":
            depth -= 1
            if depth == 0:
                return source[at:index + 1]
    return ""


def enum_ids(source: str, name: str) -> list[str]:
    return re.findall(r'^\s+[A-Z_0-9]+\("([a-z_.0-9]+)"', body(source, f"enum class {name}"), re.M)


def load(name: str):
    return yaml.safe_load(read(DATASET / name)) or {}


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, ENGINE_KT, APP_KT, TIMELINE_KT):
    check(f"E13D-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = yaml.safe_load(read(CONTRACT)) or {}
state_contract, rules_yaml, mappings = load("state_contract.yaml"), load("failure_attribution_rules.yaml"), load("learning_need_mappings.yaml")
guards = load("aggregation_guards.yaml")
facts, engine, app = strip_comments(read(FACTS_KT)), strip_comments(read(ENGINE_KT)), strip_comments(read(APP_KT))
planner, mastery, timeline = strip_comments(read(PLANNER_KT)), strip_comments(read(MASTERY_KT)), strip_comments(read(TIMELINE_KT))
retention_app, build, blueprint = strip_comments(read(RETENTION_APP_KT)), strip_comments(read(BUILD_KT)), strip_comments(read(BLUEPRINT_KT))
ports, schema, migrations, sql = strip_comments(read(PORTS_KT)), strip_comments(read(SCHEMA_KT)), strip_comments(read(MIGRATIONS_KT)), strip_comments(read(SQL_KT))
spec_text, research_text, wlrm = read(SPEC), read(RESEARCH), read(WLRM)

# ---------------------------------------------------------------- contract head
check("E13D-01_model", contract.get("model") == "WLRX-v0", str(contract.get("model")))
check("E13D-01_status", contract.get("status") == "accepted_13d", str(contract.get("status")))
check("E13D-01_decision", contract.get("decision") == "D-102", str(contract.get("decision")))
check("E13D-01_semantics", contract.get("weakness_semantics") == "WLRM-v0", str(contract.get("weakness_semantics")))
scope = contract.get("scope", {})
for key, expected in {"weakness_axis_written": True, "objective_local_attribution": True, "weakness_needs_supplied": True,
                      "dispositions_read": True, "retroactive_contamination": True, "schema_changed": True, "migration_added": True,
                      "interfaces_added": False, "boundaries_changed": False, "planner_rule_changed": False, "gate_rule_changed": False,
                      "mastery_rule_changed": False, "topic_state_implemented": False, "remediation_content_implemented": False,
                      "misconception_memory_implemented": False, "app_calls_rebuild": False}.items():
    check(f"E13D-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E13D-01_wlrm_localization", "`Bir soru yanlış → Skill başarısız → Topic/Domain reset` canonical davranış değildir." in wlrm, "WLRM-v0 §3 moved")

# ---------------------------------------------------------------- vocabularies, read from the WLRM-v0 dataset
check("E13D-02_lifecycle_equals_dataset", enum_ids(facts, "WeaknessSignal") == state_contract.get("weakness_signal_states")
      == contract.get("lifecycle", {}).get("signals"), f"{enum_ids(facts, 'WeaknessSignal')}")
check("E13D-02_outcomes_equal_dataset", enum_ids(facts, "AttributionOutcome") == state_contract.get("attempt_attribution_outcomes")
      == contract.get("attribution", {}).get("outcomes"), f"{enum_ids(facts, 'AttributionOutcome')}")
dataset_rules = sorted(rules_yaml, key=lambda r: r["priority"])
kotlin_rules = re.findall(r'^\s+[A-Z_0-9]+\("(failure\.[a-z_0-9]+)", (\d+)\)', body(facts, "enum class FailureRule"), re.M)
check("E13D-02_rules_equal_dataset", [(r["rule_id"], r["priority"]) for r in dataset_rules] == [(i, int(p)) for i, p in kotlin_rules]
      and len(kotlin_rules) == 12, f"{kotlin_rules}")
check("E13D-02_rules_contract", contract.get("attribution", {}).get("rules") == [r["rule_id"] for r in dataset_rules], "contract rules")
check("E13D-02_axis_values", enum_ids(facts, "WeaknessAxis") == contract.get("axis", {}).get("values") + ["not_yet_evaluated"], str(enum_ids(facts, "WeaknessAxis")))
check("E13D-02_remediation_value_shared", 'REMEDIATION_REQUIRED("remediation_required")' in facts
      and 'REMEDIATION_REQUIRED = "remediation_required"' in planner, "the axis value the planner reads drifted")
for invariant in ("assisted_or_provisional_failure_cannot_directly_confirm_remediation_required",
                  "first_clean_post_mastery_contradiction_opens_verification_due_before_mastery_erasure",
                  "remediation_closure_requires_new_evidence_not_task_completion",
                  "review_due_without_negative_evidence_is_not_weakness",
                  "ai_assistance_may_scaffold_remediation_but_never_satisfies_independent_closure_evidence"):
    check(f"E13D-02_invariant_{invariant[:40]}", invariant in state_contract.get("invariants", []), f"dataset invariant moved: {invariant}")

# ---------------------------------------------------------------- attribution
rule_fn = body(engine, "fun rule(")
order = ["FailureRule.INVALID_OR_AMBIGUOUS", "FailureRule.PREREQUISITE_CONTAMINATION", "FailureRule.FRESH_RECOVERY_SUCCESS",
         "FailureRule.ASSISTED_H1_H4", "FailureRule.PROVISIONAL_OR_PARTIAL", "FailureRule.CLEAN_PREMASTERY_H0_DIRECT",
         "FailureRule.FIRST_CLEAN_POSTMASTERY_CONTRADICTION", "FailureRule.FRESH_RECHECK_FAIL"]
positions = [rule_fn.find(r) for r in order]
check("E13D-03_rules_in_priority_order", all(p >= 0 for p in positions) and positions == sorted(positions), f"{positions}")
check("E13D-03_invalid_first", "event.outcome == EvidenceOutcome.INVALID || event.evaluatorStatus == EvaluatorStatus.INVALID || event.contested ->" in rule_fn,
      "an unattributable row is attributed")
check("E13D-03_contamination_spares_target", "!event.prerequisiteValid -> FailureRule.PREREQUISITE_CONTAMINATION" in rule_fn, "contamination blames the target")
check("E13D-03_assisted_hypothesis", "event.independence != IndependenceClass.INDEPENDENT || event.solutionExposed -> FailureRule.ASSISTED_H1_H4" in rule_fn,
      "help taken is not a hypothesis")
check("E13D-03_uncertain_hypothesis", "event.evaluatorStatus != EvaluatorStatus.VERIFIED || event.outcome == EvidenceOutcome.PARTIAL || !event.direct ->" in rule_fn,
      "uncertain evidence is more than a hypothesis")
check("E13D-03_confirm_on_gates", "!event.masteredAfter -> FailureRule.FRESH_RECHECK_FAIL" in rule_fn
      and "if (!event.masteredAfter) {\n                    raise(state, event, WeaknessSignal.CONFIRMED" in engine, "confirmation without the gates")
step = body(engine, "fun step(")
check("E13D-03_hypothesis_effect", "FailureRule.ASSISTED_H1_H4, FailureRule.PROVISIONAL_OR_PARTIAL ->\n                raise(state, event, WeaknessSignal.HYPOTHESIS" in step,
      "help or uncertainty raises more than a hypothesis")
check("E13D-03_postmastery_opens_verification", "AttributionOutcome.VERIFICATION_DUE, rule).copy(verificationOpen = true)" in step, "no verification opened")
check("E13D-03_never_rows_inert", "FailureRule.UNATTEMPTED_OR_DEFERRED, FailureRule.ENVIRONMENT_OUTSIDE_TARGET,\n            FailureRule.INTEGRATED_GLOBAL_OUTCOME_GUARD, FailureRule.REVIEW_DUE_WITHOUT_NEGATIVE_EVIDENCE -> state" in step,
      "a never-a-row rule changes state")
raise_fn = body(engine, "private fun raise(")
check("E13D-03_never_lowers", "if (!open || to.strength > state.signal.strength) to else state.signal" in raise_fn, "a failure lowers a signal")
check("E13D-03_no_retention_read", "RetentionAxis" not in engine and "retention" not in rule_fn.lower(), "weakness reads retention")
clean = facts[facts.find("val clean: Boolean"):facts.find("data class ObjectiveWeakness")]
for cond in ("!contested", "prerequisiteValid", "!solutionExposed", "direct", "evaluatorStatus == EvaluatorStatus.VERIFIED",
             "independence == IndependenceClass.INDEPENDENT"):
    check(f"E13D-03_clean_{cond[:24]}", cond in clean, f"clean lacks {cond}")

# ---------------------------------------------------------------- closure
recover = body(engine, "private fun recover(")
check("E13D-04_closes_open_signals", "WeaknessSignal.HYPOTHESIS, WeaknessSignal.SUPPORTED -> true" in recover, "a fresh success does not close")
check("E13D-04_confirmed_needs_gates", "WeaknessSignal.CONFIRMED -> event.masteredAfter" in recover, "one success closes a remediation")
check("E13D-04_recovery_clean_and_fresh", "if (event.clean && state.isFresh(event)) FailureRule.FRESH_RECOVERY_SUCCESS else null" in rule_fn,
      "help or a repeat closes")
fresh = body(facts, "fun isFresh(")
check("E13D-04_fresh_item_and_family", "event.resource !in signalResources" in fresh and "event.variantFamilyId !in signalFamilies" in fresh, "freshness")
init = body(facts, "data class ObjectiveWeakness(")
check("E13D-04_resolution_needs_evidence", "require(signal != WeaknessSignal.RESOLVED || resolutionEvidenceId != null)" in init, "resolved without evidence")
check("E13D-04_open_needs_evidence", "signalEvidenceIds.isNotEmpty() && firstSeenDay != null" in init, "a weakness without evidence")
closure = contract.get("closure", {})
check("E13D-04_contract", closure.get("task_completion_closes") is False and closure.get("assisted_success_closes") is False
      and closure.get("confirmed_closed_only_when_gates_pass") is True and closure.get("manual_mastery_override") is False, str(closure))
check("E13D-04_wlrm_closure_text", "Remediation task'ının tamamlanması remediation'ı kapatmaz." in wlrm, "§7 moved")

# ---------------------------------------------------------------- axis and broadcast
axis = body(facts, "fun of(objectives")
check("E13D-05_axis_order", 0 <= axis.find("WeaknessSignal.CONFIRMED in signals") < axis.find("WeaknessSignal.SUPPORTED in signals")
      < axis.find("WeaknessSignal.HYPOTHESIS in signals") < axis.find("WeaknessSignal.RESOLVED in signals"), "axis precedence")
check("E13D-05_no_broadcast", not re.search(r"(?i)\btopic|\bdomain|\bdependents?\b|prerequisiteEdges", engine + app),
      "weakness reaches beyond its Objective")
check("E13D-05_guards_present", {g["guard_id"] for g in guards.get("rules", [])} >= {"guard.no_domain_reset", "guard.no_topic_broadcast",
      "guard.no_project_broadcast", "guard.review_due_not_weakness", "guard.ai_scaffold_not_closure"}, "dataset guards moved")

# ---------------------------------------------------------------- needs
needs = body(engine, "fun needs(")
mapping_ids = {m["mapping_id"]: m for m in mappings}
check("E13D-06_mappings_present", {"need.map.weakness_hypothesis", "need.map.supported_premastery_weakness",
      "need.map.confirmed_remediation", "need.map.verification_due"} <= set(mapping_ids), str(sorted(mapping_ids)))
check("E13D-06_trigger", mapping_ids.get("need.map.weakness_hypothesis", {}).get("trigger_kind") == "weakness_detected"
      and "NeedTrigger.WEAKNESS_DETECTED" in needs, "the supplied trigger is not weakness_detected")
check("E13D-06_supplied_values", "WeaknessAxis.SUPPORTED.id -> EvidenceSeverity.CLEAN_CONTRADICTION_OR_VERIFICATION_DUE" in needs
      and "WeaknessAxis.HYPOTHESIS.id -> EvidenceSeverity.PARTIAL_OR_UNCERTAIN_CONCERN" in needs and "else -> return@mapNotNull null" in needs,
      "the wrong axes supply a need")
check("E13D-06_one_concern_one_need", "if (state.mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE) return@mapNotNull null" in needs
      and "REMEDIATION_REQUIRED" not in needs, "a concern gets two needs")
check("E13D-06_on_route_only", "if (state.lifecycleStatus !in ON_ROUTE) return@mapNotNull null" in needs
      and 'setOf("published", "deprecated")' in engine, "an off-route Skill gets a need")
planner_contract = yaml.safe_load(read(PLANNER_CONTRACT)) or {}
check("E13D-06_owner_supplied_per_12c", "weakness_detected" in planner_contract.get("needs", {}).get("supplied_by_owners", []), "12C lists weakness_detected as planner-generated")
check("E13D-06_planner_rule_unchanged", "NeedTrigger.WEAKNESS_DETECTED" not in body(planner, "fun needsFromSkillStates("), "the planner opens weakness itself")
check("E13D-06_build_receives", "PlannerEngine.needsFromSkillStates(states) + WeaknessEngine.needs(states)" in body(build, "fun build("), "the planner is not supplied")
check("E13D-06_composer_receives", "(ownerNeeds + WeaknessEngine.needs(states)).distinctBy { it.needKey }" in body(blueprint, "fun compose(evaluatorAvailable"),
      "the composers are not supplied")

# ---------------------------------------------------------------- dispositions
eff = body(facts, "fun effective(")
check("E13D-07_contested", "CONTESTED -> row.copy(contested = true)" in eff, "contested ignored")
check("E13D-07_prereq_invalidation", "if (reason == PREREQUISITE_CONTAMINATED) row.copy(prerequisiteValid = false)" in eff, "prerequisite invalidation")
check("E13D-07_other_invalidation", "else row.copy(evaluatorStatus = EvaluatorStatus.INVALID)" in eff and "SUPERSEDED -> row.copy(evaluatorStatus = EvaluatorStatus.INVALID)" in eff,
      "an invalidated evaluation stands")
check("E13D-07_reinstated", "null, REINSTATED -> row" in eff, "reinstatement ignored")
check("E13D-07_outcome_never_rewritten", "outcome =" not in eff, "the learner's outcome is rewritten")
disposition_consts = dict(re.findall(r'const val (INVALIDATED|CONTESTED|SUPERSEDED|REINSTATED) = "(\w+)"', facts))
listed = re.search(r"val VALUES = listOf\(([^)]*)\)", facts)
stored_values = re.search(r'val dispositionValues = listOf\(([^)]*)\)', schema)
check("E13D-07_values_equal_schema", listed is not None and stored_values is not None
      and sorted(disposition_consts.get(n.strip(), n) for n in listed.group(1).split(",")) == sorted(re.findall(r'"(\w+)"', stored_values.group(1))),
      "disposition vocabulary drifted")
efor = body(sql, "override fun evidenceFor(")
check("E13D-07_newest_decides", efor.count("ORDER BY d.sequence DESC LIMIT 1") == 2, "not the newest disposition")
check("E13D-07_store_applies", "EvidenceDispositions.effective(" in efor, "the store does not read dispositions")
check("E13D-07_no_update", not re.search(r"UPDATE evidence_event", sql + app), "a row is edited")
check("E13D-07_mastery_untouched", "disposition" not in mastery.lower(), "the mastery engine was changed to read dispositions")
record = body(app, "fun record(")
check("E13D-07_record_validates", "require(disposition in EvidenceDispositions.VALUES)" in record and "require(decidedBy in DECIDED_BY)" in record
      and "require(reasonCode.isNotBlank())" in record and 'persistence.readTruth("evidence_event", evidenceId)' in record
      and "persistence.inTransaction" in record, "a disposition is not validated")

# ---------------------------------------------------------------- retroactive contamination
retro = body(app, "fun apply(")
check("E13D-08_dependents_only", "BlueprintComposer.contaminatedBy(slot, failed) && slot.targetSkill !in failed" in retro, "independent branches touched")
check("E13D-08_clean_failure_root", "BlueprintComposer.cleanlyFailedSkills(blueprint, outcomes)" in retro, "root not a clean failure")
check("E13D-08_idempotent", "if (!stored.prerequisiteValid) return@mapNotNull null" in retro and ".filterNot { it.prerequisiteContaminated }" in retro, "corrected twice")
check("E13D-08_disposition_values", "EvidenceDispositions.INVALIDATED, EvidenceDispositions.PREREQUISITE_CONTAMINATED, \"deterministic_rule\"" in retro, "disposition values")
for owner in ("arch/13a_weekly_assessment/weekly_assessment.yaml", "arch/13b_monthly_assessment/monthly_assessment.yaml"):
    check(f"E13D-08_loop_owned_{owner[5:8]}", "13D" in read(ROOT / owner), f"{owner} no longer names 13D")

# ---------------------------------------------------------------- shared timeline and rebuild
rebuild = body(app, "fun rebuild(")
check("E13D-09_watermark_first", 0 <= rebuild.find("persistence.truthWatermark()") < rebuild.find("MasteryTimeline.rowsOf("), "watermark after evidence")
check("E13D-09_day_required", "require(rows.all { it.second.studyDay != null })" in rebuild, "a row with no day")
check("E13D-09_shared_timeline", "MasteryTimeline.of(skill, profiles, rows)" in rebuild and "MasteryTimeline.of(skill, profiles, rows)" in retention_app,
      "two timelines")
check("E13D-09_timeline_mirrors", all(n in body(timeline, "fun of(") for n in ("previouslyMastered = previous in MASTERED,",
      "unresolvedVerification = previous == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE,", "previouslyMastered = previous == MasteryAxisState.CONFIRMED_CURRENT,")),
      "the timeline differs from RebuildMastery")
check("E13D-09_no_truth", "appendTruth" not in rebuild and "appendTruth" not in body(app, "fun writeAxis("), "a rebuild writes truth")
axis_write = body(app, "fun writeAxis(")
check("E13D-09_min_watermark", "minOf(existing?.truthWatermark ?: watermark, watermark)" in axis_write, "skill_state claims newer truth")
for column in ("mastery_axis_state", "retention_axis_state", "prerequisite_axis_state"):
    check(f"E13D-09_carries_{column}", f'"{column}" to carried("{column}")' in axis_write, f"{column} overwritten")

# ---------------------------------------------------------------- storage
# Narrowed at 13F: version 6 is 13D's, and every later version must be owned by its step's contract.
_schema_version = int((re.search(r"const val VERSION = (\d+)", schema) or re.search(r"(0)", "0")).group(1))
_owned_versions = {m.get("to") for f in ROOT.glob("arch/*/*.yaml")
                   for m in [(yaml.safe_load(f.read_text(encoding="utf-8")) or {}).get("schema_migration") or {}] if isinstance(m, dict)}
check("E13D-10_schema_version_6", _schema_version >= 6 and all(v in _owned_versions for v in range(6, _schema_version + 1)), "schema version")
v6 = body(schema, "val v6")
added = re.findall(r"ALTER TABLE weakness_state ADD COLUMN (\w+)", v6)
check("E13D-10_columns_equal_contract", added == contract.get("schema_migration", {}).get("columns"), str(added))
signals = re.search(r'const val WEAKNESS_SIGNALS = "([^"]+)"', schema)
check("E13D-10_value_set", signals is not None and [s.strip(" '") for s in signals.group(1).split(",")] == state_contract.get("weakness_signal_states"),
      "stored lifecycle drifted")
check("E13D-10_triggers", "BEFORE INSERT ON weakness_state" in v6 and "BEFORE UPDATE ON weakness_state" in v6
      and v6.count("WHEN NEW.state NOT IN ($WEAKNESS_SIGNALS)") == 2, "lifecycle not enforced")
check("E13D-10_index", "CREATE INDEX IF NOT EXISTS weakness_by_skill ON weakness_state (skill_logical_id, skill_version)" in v6, "index")
check("E13D-10_migration_step", "5 to Schema.v6" in migrations and "4 to Schema.v5" in migrations, "migration steps")
mig = contract.get("schema_migration", {})
check("E13D-10_migration_owned", mig.get("from") == 5 and mig.get("to") == 6 and mig.get("owner") == "13D" and mig.get("populated_fixture_tested") is True, str(mig))

# ---------------------------------------------------------------- ports
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx = yaml.safe_load(read(MSBX)) or {}
check("E13D-11_port_count", sorted(interfaces) == sorted([p["id"] for p in msbx["ports"]["set"]] + declared_port_extensions(ROOT)) and contract.get("port_count") == 4, str(interfaces))
check("E13D-11_no_new_port_method", "weakness" not in ports.lower() and contract.get("port_refinements") == [], "a weakness port method")

# ---------------------------------------------------------------- tests named
suites = contract.get("suites", {})
named_tests = {
    "model": ["the lifecycle, the attribution outcomes and the twelve rules are WLRM-v0's, in order",
              "a disposition is read, never written over the row - the newest decides and reinstated restores",
              "a closing check must be fresh - not the item or the family that showed the weakness"],
    "engine": ["an attempt nobody can attribute writes nothing against its target", "assisted, provisional, partial or indirect failure is a hypothesis at most",
               "the first clean contradiction after mastery opens verification and erases nothing",
               "remediation is confirmed only when the mastery engine's gates no longer pass",
               "a confirmed remediation closes only when the gates pass again, not after one success",
               "the engine supplies weakness needs only for an open, uncovered concern"],
    "application": ["a supported weakness reaches the planner and the weekly composer as the owner's need",
                    "lost mastery after a contradiction is a confirmed remediation the gate and the planner already read",
                    "only new evidence closes a remediation - assisted success and a finished task do not",
                    "a root shown missing later in the session corrects the answers that needed it, and only those"],
    "storage": ["migrating a populated schema-5 database completes weakness_state and preserves every truth row",
                "weakness_state holds only the WLRM-v0 lifecycle, on insert, upsert and update",
                "a disposition is read back by the store, the newest decides, and the row itself never changes"],
}
for suite, names in named_tests.items():
    text = read(ROOT / suites.get(suite, {}).get("file", "missing"))
    for name in names:
        check(f"E13D-12_{suite}_{name[:40]}", f"`{name}`" in text, f"missing test: {name}")

# ---------------------------------------------------------------- narrowed gates and decisions
retention_validator = read(ROOT / "tools/validate_retention.py")
check("E13D-13_13c_gates_narrowed", "Narrowed at 13D" in retention_validator and "MasteryTimeline.rowsOf(" in retention_validator
      and 'check("E13C-08_replay_shared"' in retention_validator, "13C gates not narrowed")
check("E13D-13_13c_test_narrowed", "assertTrue(Schema.VERSION >= 5)" in read(ANDROID / "data-persistence/src/test/kotlin/coach/persistence/RetentionStorageTest.kt"),
      "13C test not narrowed")
check("E13D-13_recorded", len(contract.get("living_gates_narrowed", [])) == 4, "narrowing not recorded")
decisions = contract.get("user_decisions", {})
check("E13D-13_user_decisions", decisions.get("topic_state_machine", {}).get("owner") == "16C"
      and decisions.get("high_stakes_gap_policy", {}).get("owner") == "18D", str(decisions))

# ---------------------------------------------------------------- honesty
mutation = contract.get("mutation_results", {})
check("E13D-14_mutation_all_detected", mutation.get("detected") == mutation.get("total") == len(mutation.get("mutants", [])) >= 40,
      f"{mutation.get('detected')}/{mutation.get('total')}")
check("E13D-14_mutation_control", mutation.get("negative_control_result") == "survived_as_expected" and mutation.get("compile_failure_is_detection") is False
      and mutation.get("only_weakness_suites_run") is True and mutation.get("final_run_is_a_single_clean_run") is True, "harness honesty")
vm = contract.get("validator_mutation", {})
check("E13D-14_validator_mutation", vm.get("detected") == vm.get("total") and (vm.get("total") or 0) >= 20 and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E13D-14_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
device = contract.get("device_verification", {})
check("E13D-14_device_not_claimed", device.get("t6_run") is False and device.get("claimed") is False, "a device result is claimed")
loops = contract.get("open_loops", {})
for loop, owner in (("topic_state_machine", "16C"), ("high_stakes_gap_policy", "18D"), ("remediation_content_from_routes", "15"),
                    ("misconception_memory", "14B"), ("rebuild_and_retroactive_contamination_from_app", "16D"), ("calibration", "18C")):
    check(f"E13D-14_open_{loop[:30]}", str(loops.get(loop, {}).get("owner")) == owner, f"{loop} not owned")
forbidden = set(contract.get("forbidden_weakness_patterns", []))
for pattern in ("one_failure_fails_skill_topic_or_domain", "unattributable_attempt_blames_target", "assisted_or_provisional_confirms_remediation",
                "postmastery_contradiction_erases_mastery", "closure_by_task_completion", "closure_by_help_same_item_or_one_success",
                "weakness_broadcast", "correction_written_over_row", "contaminated_answer_counted_against_target", "review_due_as_weakness"):
    check(f"E13D-15_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 13D QA PASS", "**Decision:** `D-102`", "A failed attempt is not a failed Skill",
                 "Not run: T6", "Mutation 44/44", "13E — Program değişiklik raporu"]:
    check(f"E13D-16_spec_{fragment[:26]}", fragment in spec_text, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "WLRM-v0", "16C", "18D"]:
    check(f"E13D-17_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E13D-18_no_repeated_context_heading", len(context_headings) == len(set(context_headings)), "repeated context heading")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E13D-18_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)), "repeated master plan step")
combining = chr(0x0307)
check("E13D-18_no_combining_dot", all(combining not in read(p) for p in (SPEC, RESEARCH, CONTRACT, FACTS_KT)), "U+0307 in 13D text")

passed_count = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "WLRX-v0", "stage_step": "13D", "decision": "D-102",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed_count, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"13D_REMEDIATION_QA={report['result']}")
print(f"checks={passed_count}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
