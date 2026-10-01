"""Independent 12B QA — PRQX-v0 Prerequisite Engine.

The implementation is validated against the accepted contracts, not against its own. The readiness
values, eligibility values and reason inputs are read out of `PRG-v0`'s own text
(`PREREQUISITE_POLICY_SPEC.md`), the retention values out of `RVR-v0`'s, the edge lifecycles out of
`KGC-v0`'s, the ownership out of `MSBX-v0`, and the claims about the authored graph out of the graph
files themselves — each compared with the actual Kotlin.

The checks that matter most are structural: a priority input, a readiness score, a failure field and
a store that filters edges away must all be **unrepresentable**, not merely absent today.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

def schema_versions_owned(schema_text: str) -> bool:
    """Narrowed at 13A: this step added no migration. Any schema version beyond 2 must be declared by the
    accepted later contract that added it (`schema_migration` in its arch yaml), so an unowned move still fails."""
    import glob as _glob
    import yaml as _yaml
    match = re.search(r"const val VERSION = (\d+)", schema_text)
    if not match:
        return False
    version = int(match.group(1))
    owned = set()
    for path in _glob.glob(str(ROOT / "arch" / "*" / "*.yaml")):
        try:
            doc = _yaml.safe_load(open(path, encoding="utf-8"))
        except Exception:
            continue
        migration = doc.get("schema_migration") if isinstance(doc, dict) else None
        if isinstance(migration, dict) and migration.get("from") is not None and migration.get("to") is not None:
            owned.add((int(migration["from"]), int(migration["to"])))
    return all((v - 1, v) in owned for v in range(3, version + 1))


ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/12b_prerequisite_engine/prerequisite_engine.yaml"
SPEC = ROOT / "docs/PREREQUISITE_ENGINE_IMPL_SPEC.md"
RESEARCH = ROOT / "research/12b_prerequisite_engine_research.md"
QA_OUT = ROOT / "arch/12b_prerequisite_engine/qa_report.yaml"

PRG = ROOT / "docs/PREREQUISITE_POLICY_SPEC.md"
RVR = ROOT / "docs/RETENTION_FORGETTING_SPEC.md"
KGC = ROOT / "docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
GRAPH_GLOB = "curriculum/decomposition/**/prerequisite_edges.yaml"

ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/PrerequisiteEngine.kt"
OWNERSHIP_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/EngineOwnership.kt"
FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/PrerequisiteFacts.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/ResolvePrerequisites.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ADAPTER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
STORE_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/CurriculumStore.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"

FACTS_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/PrerequisiteFactsTest.kt"
ENGINE_TEST = ANDROID / "core-engines/src/test/kotlin/coach/engines/PrerequisiteEngineTest.kt"
APP_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/ResolvePrerequisitesTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/PrerequisiteStorageTest.kt"

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

    The parameter list is balanced first: a default value inside it contains an `=` long before the
    body starts, and a reader that stops there silently returns an empty string — a check that then
    passes on anything. 11D and 12A both hit that defect; every body read here is also checked to be
    non-empty, so a reader failure is a FAIL rather than a silent PASS.
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


def class_body(source: str, cls: str) -> str:
    start = source.find(f"class {cls}")
    if start < 0:
        return ""
    brace = source.find("{", source.find(")", start))
    depth = 0
    for i in range(brace, len(source)):
        depth += {"{": 1, "}": -1}.get(source[i], 0)
        if depth == 0:
            return source[brace + 1:i]
    return ""


def params(source: str, signature: str) -> list[str]:
    start = source.find(signature)
    if start < 0:
        return []
    cursor = source.find("(", start)
    depth, end = 0, cursor
    for i in range(cursor, len(source)):
        depth += {"(": 1, ")": -1}.get(source[i], 0)
        if depth == 0:
            end = i
            break
    return re.findall(r"(?:^|,|\()\s*(?:val\s+)?(\w+)\s*:", source[cursor:end + 1])


def constructor_params(source: str, cls: str) -> list[str]:
    match = re.search(rf"class {cls}\b[^(]*\((.*?)\n\)", source, re.S)
    return re.findall(r"val (\w+)\s*:", match.group(1)) if match else []


def text_block_after(text: str, anchor: str) -> list[str]:
    """The lines of the first fenced block after [anchor] in a contract's own prose."""
    at = text.find(anchor)
    if at < 0:
        return []
    block = re.search(r"```text\n(.*?)```", text[at:], re.S)
    return [line.strip() for line in block.group(1).splitlines() if line.strip()] if block else []


def camel(snake: str) -> str:
    head, *rest = snake.split("_")
    return head + "".join(w.capitalize() for w in rest)


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


contract = load(CONTRACT)
msbx = load(MSBX)
prg = read(PRG)
rvr = read(RVR)
kgc = read(KGC)

for path in (ENGINE_KT, FACTS_KT, APP_KT, FACTS_TEST, ENGINE_TEST, APP_TEST, T2_TEST, SPEC, RESEARCH):
    check(f"E12B-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

engine = strip_comments(read(ENGINE_KT))
ownership = strip_comments(read(OWNERSHIP_KT))
facts = strip_comments(read(FACTS_KT))
app = strip_comments(read(APP_KT))
ports = strip_comments(read(PORTS_KT))
adapter = strip_comments(read(ADAPTER_KT))
store = strip_comments(read(STORE_KT))
schema = read(SCHEMA_KT)
facts_test = read(FACTS_TEST)
engine_test = read(ENGINE_TEST)
app_test = read(APP_TEST)
t2_test = read(T2_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E12B-01_model", contract.get("model") == "PRQX-v0", str(contract.get("model")))
check("E12B-01_status", contract.get("status") == "accepted_12b", str(contract.get("status")))
check("E12B-01_decision", contract.get("decision") == "D-093", str(contract.get("decision")))
for key in ("planner_implemented", "replan_implemented", "reason_text_implemented", "retention_engine_implemented",
            "weakness_engine_implemented", "topic_state_implemented", "skill_state_axis_written",
            "presentation_state_derived", "schema_changed", "migration_added", "index_added",
            "interfaces_added", "boundaries_changed", "surface_semantics_changed"):
    check(f"E12B-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("readiness_score_claim", "priority_input_claim"):
    check(f"E12B-01_no_{key}", contract["scope"].get(key) is None, f"{key} was claimed")

# ---------------------------------------------------------------- vocabularies, read out of the contracts
prg_readiness = text_block_after(prg, "Her prerequisite için semantik readiness çıkarır")
check("E12B-02_readiness_read_from_prg", prg_readiness == ["ready", "ready_due", "uncertain", "not_ready"],
      f"prg={prg_readiness}")
check("E12B-02_readiness_equals_prg", enum_ids(facts, "PrerequisiteReadiness") == prg_readiness,
      f"kotlin={enum_ids(facts, 'PrerequisiteReadiness')}")
check("E12B-02_readiness_equals_contract", contract["readiness"]["values"] == prg_readiness,
      f"contract={contract['readiness']['values']}")

prg_decision = re.search(r"PrerequisiteDecision\n(.*?)```", prg, re.S)
prg_decision_text = prg_decision.group(1) if prg_decision else ""
prg_eligibility = re.findall(r"^\s{4}(\w+)$", prg_decision_text, re.M)
check("E12B-02_eligibility_read_from_prg", len(prg_eligibility) == 5, f"prg={prg_eligibility}")
check("E12B-02_eligibility_equals_prg", enum_ids(facts, "PrerequisiteEligibility") == prg_eligibility,
      f"kotlin={enum_ids(facts, 'PrerequisiteEligibility')}")

prg_fields = [re.sub(r"\[\]$", "", f) for f in re.findall(r"^- (\w+(?:\[\])?)", prg_decision_text, re.M)]
decision_fields = constructor_params(facts, "PrerequisiteDecision")
for field in prg_fields:
    name = {"candidate_id": "candidateId", "hard_blocker_skill_ids": "hardBlockerSkills",
            "uncertain_skill_ids": "uncertainSkills", "soft_gap_skill_ids": "softGapSkills"}.get(field, camel(field))
    check(f"E12B-02_decision_field_{field}", name in decision_fields, f"missing {name} in {decision_fields}")
FORBIDDEN_FIELD_WORDS = ("priority", "score", "percent", "fail", "rank", "probability", "grade", "streak")
check("E12B-02_decision_has_no_verdict_field",
      not [f for f in decision_fields if any(w in f.lower() for w in FORBIDDEN_FIELD_WORDS)],
      f"fields={decision_fields}")
check("E12B-02_readiness_has_no_number",
      not [f for f in constructor_params(facts, "SkillReadiness") if re.search(r"score|percent|value\b", f, re.I)]
      and "Double" not in class_body(facts, "SkillReadiness") + str(constructor_params(facts, "SkillReadiness")),
      f"fields={constructor_params(facts, 'SkillReadiness')}")

prg_reasons_block = text_block_after(prg, "3D en az şu reason input'larını üretir")
check("E12B-03_reasons_read_from_prg", len(prg_reasons_block) == 9, f"prg={prg_reasons_block}")
kotlin_reasons = enum_ids(facts, "PrerequisiteReason")
check("E12B-03_reasons_are_prg_names", set(kotlin_reasons) <= set(prg_reasons_block),
      f"not in PRG: {sorted(set(kotlin_reasons) - set(prg_reasons_block))}")
owned = contract["reason_inputs"]
check("E12B-03_every_prg_reason_has_an_owner",
      set(owned["produced_here"]) | set(owned["owned_elsewhere"]) == set(prg_reasons_block),
      f"unowned={sorted(set(prg_reasons_block) - set(owned['produced_here']) - set(owned['owned_elsewhere']))}")
check("E12B-03_produced_here_is_the_kotlin_set", set(owned["produced_here"]) == set(kotlin_reasons),
      f"kotlin={kotlin_reasons}")

rvr_retention = text_block_after(rvr, "### Retention ekseni")
check("E12B-04_retention_read_from_rvr", len(rvr_retention) == 6, f"rvr={rvr_retention}")
kotlin_retention = enum_ids(facts, "RetentionAxis")
check("E12B-04_retention_is_rvr_plus_unevaluated", kotlin_retention == rvr_retention + ["not_yet_evaluated"],
      f"kotlin={kotlin_retention}")
check("E12B-04_unknown_retention_not_guessed", "?: NOT_YET_EVALUATED" in facts,
      "an unknown retention value is guessed")

kgc_lifecycle = text_block_after(kgc, "Curriculum entity baseline lifecycle")
check("E12B-05_lifecycle_read_from_kgc",
      kgc_lifecycle == ["draft", "published", "deprecated", "retired", "invalidated"], f"kgc={kgc_lifecycle}")
in_force = re.search(r"EDGE_IN_EFFECT = setOf\(([^)]*)\)", engine)
in_force_values = re.findall(r'"(\w+)"', in_force.group(1)) if in_force else []
check("E12B-05_in_force_lifecycles", sorted(in_force_values) == sorted(contract["graph"]["in_force_lifecycles"])
      == ["deprecated", "published"], f"kotlin={in_force_values}")
requirements = body(engine, "fun requirements(")
check("E12B-05_requirements_body_read", len(requirements) > 200, "the requirements reader returned nothing")
for lifecycle, fragment in (("retired", "edge.lifecycleStatus == EDGE_RETIRED -> return@forEach"),
                            ("invalidated", '"invalidated" ->\n                    problems += "${MetadataProblem.EDGE_INVALIDATED.id}'),
                            ("draft", '"draft" ->\n                    problems += "${MetadataProblem.EDGE_NOT_PUBLISHED.id}'),
                            ("unknown", "!in EDGE_IN_EFFECT ->\n                    problems += \"${MetadataProblem.UNKNOWN_EDGE_LIFECYCLE.id}")):
    check(f"E12B-05_lifecycle_{lifecycle}_handled", fragment in requirements, f"{lifecycle} edges are not handled as KGC-v0 says")
check("E12B-05_retired_constant", 'EDGE_RETIRED = "retired"' in engine, "the retired lifecycle is not the KGC value")
check("E12B-05_newest_edge_version", "versions.maxBy { it.edgeVersion }" in body(engine, "fun latestEdges("),
      "the newest edge version is not the one in force")
check("E12B-05_metadata_problem_vocabulary",
      enum_ids(facts, "MetadataProblem") == contract["metadata_problems"], f"kotlin={enum_ids(facts, 'MetadataProblem')}")

# The claims about the authored graph are checked against the graph itself.
graph_edges = []
for path in sorted(ROOT.glob(GRAPH_GLOB)):
    graph_edges.extend(yaml.safe_load(path.read_text(encoding="utf-8")) or [])
profiles = {e.get("strictness_profile_ref") for e in graph_edges}
check("E12B-06_one_strictness_profile_in_the_graph", profiles == {"default_prg_v0"}, f"profiles={profiles}")
check("E12B-06_engine_accepts_only_that_profile",
      'DEFAULT_STRICTNESS_PROFILE = "default_prg_v0"' in engine
      and "edge.strictnessProfile != DEFAULT_STRICTNESS_PROFILE ->" in requirements
      and contract["graph"]["accepted_strictness_profiles"] == ["default_prg_v0"],
      "another strictness profile could be accepted")
lifecycles = {e.get("lifecycle_status") for e in graph_edges}
hard_count = sum(1 for e in graph_edges if e.get("edge_kind") == "hard")
check("E12B-06_draft_finding_is_true", lifecycles == {"draft"} and len(graph_edges) == 950 and hard_count == 851,
      f"lifecycles={lifecycles} edges={len(graph_edges)} hard={hard_count}")
check("E12B-06_draft_is_reported_not_dropped", contract["graph"]["draft_edge"] == "reported_as_edge_not_published",
      str(contract["graph"]["draft_edge"]))

# ---------------------------------------------------------------- readiness
readiness = body(engine, "fun readiness(")
check("E12B-07_readiness_body_read", len(readiness) > 200, "the readiness reader returned nothing")
when = readiness[readiness.find("val readiness = when"):]
order = [when.find(f) for f in ("remediation -> PrerequisiteReadiness.NOT_READY",
                                "MasteryAxisState.CONFIRMATION_VERIFICATION_DUE -> PrerequisiteReadiness.UNCERTAIN",
                                "inputs.mastery != MasteryAxisState.CONFIRMED_CURRENT -> PrerequisiteReadiness.NOT_READY")]
check("E12B-07_readiness_precedence", all(i >= 0 for i in order) and order == sorted(order),
      f"positions={order}")
for label, fragment in (("review_due_is_ready_due", "RetentionAxis.REVIEW_DUE -> PrerequisiteReadiness.READY_DUE"),
                        ("verification_or_at_risk_uncertain",
                         "RetentionAxis.VERIFICATION_DUE, RetentionAxis.AT_RISK -> PrerequisiteReadiness.UNCERTAIN"),
                        ("unevaluated_is_ready",
                         "RetentionAxis.UNTRACKED, RetentionAxis.NOT_YET_EVALUATED -> PrerequisiteReadiness.READY")):
    check(f"E12B-07_{label}", fragment in readiness, f"missing {fragment}")
check("E12B-07_review_due_never_not_ready",
      not re.search(r"REVIEW_DUE[^\n]*->\s*PrerequisiteReadiness\.(NOT_READY|UNCERTAIN)", readiness),
      "review_due is made to block")
for note in ("MASTERY_NOT_YET_EVALUATED", "RETENTION_NOT_YET_EVALUATED", "REMEDIATION_NOT_YET_EVALUATED"):
    check(f"E12B-07_names_{note.lower()}", f"add(ReadinessNote.{note})" in readiness, f"{note} is never named")

# ---------------------------------------------------------------- eligibility
decide = body(engine, "fun decide(")
check("E12B-08_decide_body_read", len(decide) > 500, "the decide reader returned nothing")
decide_params = params(engine, "fun decide(")
check("E12B-08_no_priority_input",
      decide_params and not [p for p in decide_params if re.search(r"priority|rank|score|urgency", p, re.I)],
      f"params={decide_params}")
check("E12B-08_fails_closed", "val value = state?.readiness ?: PrerequisiteReadiness.NOT_READY" in decide,
      "a missing readiness does not fail closed")
check("E12B-08_strict_is_critical_or_requested",
      "candidate.requiresStrictPrerequisiteConfidence || criticalPrerequisite(skill)" in decide,
      "strictness is not the source's critical flag or the candidate's request")
check("E12B-08_hard_wins", "if (all.any { it.kind == EdgeKind.HARD }) EdgeKind.HARD else EdgeKind.SOFT" in decide,
      "a Skill needed both ways is not needed hard")
soft = decide[decide.find("EdgeKind.SOFT -> when (value)"):]
soft = soft[:soft.find("val eligibility")]
check("E12B-08_soft_branch_found", len(soft) > 50, "the soft branch could not be read")
check("E12B-08_soft_gap_never_blocks", "hardBlockers" not in soft and "strictUncertain" not in soft,
      "a soft requirement can block")
precedence = decide[decide.find("val eligibility = when"):]
order = [precedence.find(f) for f in ("requirements.problems.isNotEmpty() -> PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA",
                                      "hardBlockers.isNotEmpty() || strictUncertain -> PrerequisiteEligibility.BLOCKED",
                                      "conditional -> PrerequisiteEligibility.CONDITIONAL_ELIGIBLE",
                                      "softGaps.isNotEmpty() -> PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT",
                                      "else -> PrerequisiteEligibility.ELIGIBLE")]
check("E12B-08_eligibility_precedence", all(i >= 0 for i in order) and order == sorted(order), f"positions={order}")
check("E12B-08_deterministic_order", ".sortedWith(compareBy({ it.first.logicalId }, { it.first.version }))" in decide,
      "the decision depends on the order its inputs arrive in")
check("E12B-08_task_requirement_hard",
      "PrerequisiteRequirement(skill, EdgeKind.HARD, RequirementSource.TASK_REQUIRED)" in requirements,
      "a task requirement is not hard")
check("E12B-08_matrix_in_contract",
      contract["eligibility"]["matrix"]["uncertain"] == {"hard_normal": "conditional_eligible", "hard_strict": "blocked",
                                                         "soft": "eligible_with_support"}
      and contract["eligibility"]["matrix"]["ready_due"]["hard_strict"] == "eligible",
      "the contract's matrix is not PRG-v0 §4/§5")
waits = re.search(r"val waits: Boolean get\(\) = ([^\n]+)", facts)
check("E12B-08_only_blocked_or_invalid_waits",
      waits is not None and waits.group(1).strip() == "this == BLOCKED || this == INVALID_PREREQUISITE_METADATA",
      str(waits and waits.group(1)))

cycle = body(engine, "fun onCycle(")
check("E12B-09_cycle_body_read", len(cycle) > 100, "the cycle reader returned nothing")
check("E12B-09_cycle_detected", "if (skill == target) return true" in cycle, "a cycle is never reported")
check("E12B-09_cycle_walk_bounded", "if (!seen.add(skill)) continue" in cycle, "the walk can read a Skill twice")
check("E12B-09_cycle_follows_edges_in_force",
      ".filter { it.lifecycleStatus in EDGE_IN_EFFECT }" in body(engine, "private fun sourcesInForce("),
      "a retired edge can close a cycle")

# ---------------------------------------------------------------- contamination
check("E12B-10_waiting_candidate_is_contaminated",
      "get() = if (eligibility.waits) PrerequisiteSnapshot.CONTAMINATED else eligibility.id" in facts,
      "work on a candidate that should have waited is not marked contaminated")
check("E12B-10_value_is_cores", 'const val CONTAMINATED = "contaminated"' in facts
      and "const val CONTAMINATED = PrerequisiteSnapshot.CONTAMINATED" in adapter,
      "the adapter owns its own contamination value")
check("E12B-10_adapter_reads_it", "statement.getText(14) != CONTAMINATED" in adapter,
      "the adapter no longer reads the snapshot back as contamination")

# ---------------------------------------------------------------- the application layer
resolve_cls = class_body(app, "ResolvePrerequisites")
rebuild_cls = class_body(app, "RebuildReadiness")
check("E12B-11_app_bodies_read", len(resolve_cls) > 300 and len(rebuild_cls) > 300, "the application reader returned nothing")
check("E12B-11_resolving_writes_nothing",
      "writeProjection" not in resolve_cls and "appendTruth" not in resolve_cls and "inTransaction" not in resolve_cls,
      "asking the gate writes")
check("E12B-11_rebuild_writes_no_truth", "appendTruth" not in rebuild_cls, "the rebuild writes truth")
check("E12B-11_rebuild_writes_only_its_family",
      rebuild_cls.count("writeProjection(") == 1 and 'key = "prerequisite_readiness:' in rebuild_cls
      and "skill_state:" not in rebuild_cls.replace("skillStateKey", ""),
      "the rebuild writes a family it does not own")
check("E12B-11_watermark_from_input", "truthWatermark = row?.truthWatermark ?: 0L," in rebuild_cls
      and "persistence.truthWatermark()" not in rebuild_cls,
      "the readiness row claims more truth than its input saw")
check("E12B-11_no_unpinned_projection",
      "persistence.latestCurriculumVersion() ?: return Rebuilt(readiness, written = false)" in rebuild_cls,
      "a readiness row can be written with nothing published")
check("E12B-11_provenance_complete", all(f in rebuild_cls for f in
                                         ("policyVersion", "truthWatermark", "builtAtInstant", "inputCurriculumVersion")),
      "a projection is written without full provenance")
check("E12B-11_policy_is_prg", 'const val PREREQUISITE_POLICY_VERSION = "PRG-v0"' in engine,
      "the projection is not stamped with PRG-v0")
inputs = body(app, "fun inputsFrom(")
check("E12B-11_inputs_body_read", len(inputs) > 200, "the inputs reader returned nothing")
check("E12B-11_reads_the_three_axes", all(f'axes["{a}"]' in inputs for a in
                                          ("mastery_axis_state", "retention_axis_state", "weakness_axis_state")),
      "an axis PRG-v0 reads is not read")
check("E12B-11_remediation_only_when_said", "REMEDIATION_REQUIRED -> true" in inputs
      and 'const val REMEDIATION_REQUIRED = "remediation_required"' in app,
      "remediation is read from something other than the weakness axis saying so")
check("E12B-11_critical_from_the_store", "persistence.skill(it)?.criticalPrerequisite == true" in resolve_cls,
      "the critical flag is not read from the published Skill")
# The call and the name being present is not enough: V41 kept both and dropped the result, so the
# check asserts that the cycle actually reaches the problems the decision is made from.
check("E12B-11_cycle_reported",
      "val cyclic = PrerequisiteEngine.onCycle(candidate.target)" in resolve_cls
      and "val cycle = \"${MetadataProblem.PREREQUISITE_CYCLE.id}:${candidate.target}\"" in resolve_cls
      and "val checked = if (cyclic) requirements.copy(problems = requirements.problems + cycle) else requirements" in resolve_cls
      and "requirements = checked," in resolve_cls,
      "a cycle through the target is not reported")
msbx_prg = next((e for e in msbx["engine_ownership"] if e["engine"] == "PRG-v0"), {})
check("E12B-11_msbx_ownership", msbx_prg.get("owns") == "prerequisite_readiness"
      and 'READINESS("PRG-v0", "prerequisite_readiness")' in ownership
      and contract["projection"]["family"] == "prerequisite_readiness",
      "the family PRG-v0 owns is not the one written")
check("E12B-11_msbx_reads", set(msbx_prg.get("may_read", [])) == {"mastery", "retention", "curriculum"},
      f"may_read={msbx_prg.get('may_read')}")

# ---------------------------------------------------------------- ports and storage
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx_ports = [p["id"] for p in msbx["ports"]["set"]]
check("E12B-12_port_count", sorted(interfaces) == sorted(msbx_ports), f"interfaces={interfaces}")
for method in ("skill", "prerequisiteEdgesInto"):
    check(f"E12B-12_refinement_{method}", f"fun {method}(" in ports, f"missing {method}")
edges_read = body(store, "fun prerequisiteEdgesInto(")
check("E12B-12_edges_body_read", len(edges_read) > 200, "the edge reader returned nothing")
check("E12B-12_edges_version_pinned",
      '"WHERE target_skill_logical_id = ? AND target_skill_version = ? "' in edges_read,
      "edges are read without the target's version pin")
check("E12B-12_edges_unfiltered", "lifecycle_status =" not in edges_read and "lifecycle_status IN" not in edges_read,
      "the store filters edges the engine should see")
check("E12B-12_edges_stable_order",
      "ORDER BY prerequisite_skill_logical_id, prerequisite_skill_version, edge_version" in edges_read,
      "the edge read has no stable order")
skill_read = body(store, "fun skill(")
for name, text in (("edges", edges_read), ("skill", skill_read)):
    check(f"E12B-12_{name}_read_only", text and not re.search(r"\b(INSERT|UPDATE|DELETE)\b", text), f"{name} read writes")
check("E12B-12_critical_flag_read", "criticalPrerequisite = statement.getLong(5) == 1L," in skill_read,
      "the critical flag is misread")
check("E12B-12_adapter_delegates", "override fun skill(ref: VersionedRef): SkillRow? = curriculumStore.skill(ref)" in adapter
      and "curriculumStore.prerequisiteEdgesInto(target)" in adapter, "the adapter does not delegate to the store")
check("E12B-12_schema_version_unchanged", schema_versions_owned(schema), "the schema version moved without an owning contract")
check("E12B-12_no_index_on_edges", "ON skill_prerequisite_edge" not in schema, "an index was added")
check("E12B-12_no_score_column", not re.search(r"readiness_(score|percent)", schema), "a readiness number was stored")

# ---------------------------------------------------------------- tests
for name in ["readiness has exactly the four values PRG-v0 names",
             "eligibility has exactly the five values PRG-v0 names",
             "work on a candidate that should have waited is recorded as contaminated evidence",
             "a decision has no priority, score or failure field"]:
    check(f"E12B-13_facts_test_{name[:42]}", f"`{name}`" in facts_test, f"missing test: {name}")
for name in ["readiness has four values and review_due is not not_ready",
             "a missing hard prerequisite blocks only the work that depends on it",
             "a critical prerequisite that is only review_due does not block",
             "a critical prerequisite with verification due blocks new dependent work",
             "a normal hard prerequisite with verification due is conditionally eligible, not blocked",
             "a soft gap never blocks",
             "a task-level requirement blocks even when the graph does not mention it",
             "English is never a hidden prerequisite",
             "a completed remediation without new evidence does not unblock",
             "a draft edge is reported, never silently dropped",
             "a cycle is found, and the walk is bounded by the graph",
             "the same inputs always give the same decision, whatever order they arrive in",
             "a missing readiness fails closed",
             "an axis nobody has evaluated yet is named, never read as bad news"]:
    check(f"E12B-13_engine_test_{name[:42]}", f"`{name}`" in engine_test, f"missing test: {name}")
for name in ["readiness is read from the axes their engines wrote",
             "a target on a cycle is invalid metadata",
             "resolving writes nothing at all",
             "it writes only prerequisite_readiness, with the provenance of what it read",
             "with nothing published nothing is written",
             "rebuilding twice from the same state writes the same row"]:
    check(f"E12B-13_app_test_{name[:42]}", f"`{name}`" in app_test, f"missing test: {name}")
for name in ["edges into a target come back in every lifecycle and version, in a stable order",
             "an edge into another version of the target is not returned",
             "reading the graph writes nothing",
             "an attempt made on a blocked candidate is read back as contaminated"]:
    check(f"E12B-13_t2_test_{name[:42]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E12B-14_device_not_claimed", device["t6_run"] is False and device["claimed"] is False,
      "a device result is claimed")
mutation = contract["mutation_results"]
check("E12B-14_mutation_all_detected",
      mutation["detected"] == mutation["total"] == len(mutation["mutants"]) >= 30,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E12B-14_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True,
      "the mutation harness was not verified to have run anything")
check("E12B-14_mutation_negative_control", mutation.get("negative_control_result") == "survived_as_expected",
      "the harness was never shown able to report a survivor")
vm = contract["validator_mutation"]
check("E12B-14_validator_mutation", vm["detected"] == vm["total"] >= 20
      and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E12B-14_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E12B-14_not_verified_named", len(contract.get("not_verified", [])) >= 3, "what was not verified must be named")
check("E12B-14_not_reachable_in_app", contract["wiring"]["reachable_in_app_today"] is False,
      "the contract claims the gate is reachable in the app")
boundaries = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("12C", "12D", "12E", "13", "15"):
    check(f"E12B-14_boundary_{owner}", owner in boundaries, f"missing {owner}")
forbidden = set(contract.get("forbidden_prerequisite_patterns", []))
for pattern in ("review_due_made_not_ready", "soft_gap_hard_locking_a_task", "priority_bypassing_the_gate",
                "untaught_prerequisite_failure_written_against_the_target", "english_as_a_hidden_technical_prerequisite",
                "remediation_completion_counted_as_readiness", "blocked_need_recorded_as_failed",
                "draft_edge_silently_dropped", "readiness_as_a_number"):
    check(f"E12B-15_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 12B QA PASS", "**Decision:** `D-093`",
                 "only the work that really depends on it", "T6", "draft", "MUTATION"]:
    present = fragment in spec_text if fragment != "MUTATION" else "MUTATION_SUMMARY_PLACEHOLDER" not in spec_text
    check(f"E12B-16_spec_{fragment[:26]}", present, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "PRG-v0", "draft", "one watermark"]:
    check(f"E12B-17_research_{fragment[:24]}", fragment in research_text, f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E12B-18_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
# 12A's sync wrote the 12B MASTER_PLAN heading twice and nothing caught it; a step heading repeated
# under a different suffix is the same defect, so headings are compared by step id.
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E12B-18_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)),
      f"repeated={sorted(h for h in set(plan_steps) if plan_steps.count(h) > 1)}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "PRQX-v0", "stage_step": "12B", "decision": "D-093",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"12B_PREREQUISITE_ENGINE_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
