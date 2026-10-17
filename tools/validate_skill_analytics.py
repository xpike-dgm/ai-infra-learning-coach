"""Independent 16A QA — SKAX-v0 Skill analytics.

The rules are read from the accepted contracts — `SPWX-v0` (`ux/8e_progress_skill_weakness/progress.yaml`: the eight
states, their precedence and labels, the qualifiers, the overview halves and attention reasons, the skill_detail fields,
the ten semantic states, the counting rules), `VDSX-v0` (`ux/8f_design_system/design_system.yaml`: every tone),
`TEPM-v0` §4.1 (the derivation), `PRG-v0` §7 and `MSBX-v0` (the module rule) — and compared with the real Kotlin, the
step's contract, its tests, spec, research and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a second derivation of the eight states, a ninth
state or at_risk as one, a state guessed where an axis is unwritten, a stale row shown as current, a presentation
column that carries an old value past a new axis, assisted or provisional or contaminated evidence shown as
independent, a percentage, ratio, bar, level or score anywhere on the surface, Progress writing state or deciding what
happens next, and a claim that the screen ran on a device.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16a_skill_analytics/skill_analytics.yaml"
QA_OUT = ROOT / "arch/16a_skill_analytics/qa_report.yaml"
SPEC = ROOT / "docs/SKILL_ANALYTICS_SPEC.md"
RESEARCH = ROOT / "research/16a_skill_analytics_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
SPWX = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
VDSX = ROOT / "ux/8f_design_system/design_system.yaml"
TEPM = ROOT / "docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md"

FACTS = ANDROID / "core-model/src/main/kotlin/coach/SkillPresentationFacts.kt"
PROGRESS_FACTS = ANDROID / "core-model/src/main/kotlin/coach/ProgressFacts.kt"
ASSEMBLY = ANDROID / "core-application/src/main/kotlin/coach/application/SkillStateAssembly.kt"
QUERY = ANDROID / "core-application/src/main/kotlin/coach/application/ProgressQuery.kt"
CHANGES = ANDROID / "core-application/src/main/kotlin/coach/application/ProgramChanges.kt"
MASTERY = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildMastery.kt"
RETENTION = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildRetention.kt"
WEAKNESS = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildWeakness.kt"
PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ProgressPresentation.kt"
SKILL_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/SkillPresentation.kt"
TONE = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/Tone.kt"
SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/ProgressScreen.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
MODEL_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/SkillPresentationProjectionTest.kt"
QUERY_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/ProgressQueryTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/ProgressPresentationTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def load(path: Path):
    return yaml.safe_load(read(path)) if path.is_file() else None


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def strip_strings(source: str) -> str:
    return re.sub(r'"(?:\\.|[^"\\])*"', '""', source)


def body(source: str, signature: str) -> str:
    """The braces-balanced body that follows the first occurrence of [signature]."""
    start = source.find(signature)
    if start < 0:
        return ""
    open_at = source.find("{", start)
    depth = 0
    for i in range(open_at, len(source)):
        depth += {"{": 1, "}": -1}.get(source[i], 0)
        if depth == 0:
            return source[start:i + 1]
    return source[start:]


def enum_ids(source: str, name: str) -> list[str]:
    block = body(source, f"enum class {name}(")
    return re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"', block, re.M)


def finish() -> None:
    passed = sum(1 for r in results if r["result"] == "PASS")
    out = {"model": "SKAX-v0", "stage_step": "16A", "decision": "D-122", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16A QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


# ---------------------------------------------------------------- inputs
for path in (CONTRACT, SPEC, RESEARCH, SPWX, VDSX, TEPM, FACTS, PROGRESS_FACTS, ASSEMBLY, QUERY, CHANGES, MASTERY, RETENTION,
             WEAKNESS, PRESENTATION, SKILL_PRESENTATION, TONE, SCREEN, ACTIVITY, APPLICATION, MODEL_TEST, QUERY_TEST,
             PRESENTATION_TEST, COURSE_TEST):
    check(f"A16A-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
spwx = load(SPWX) or {}
vdsx = load(VDSX) or {}
facts = strip_comments(read(FACTS))
progress_facts = strip_comments(read(PROGRESS_FACTS))
assembly = strip_comments(read(ASSEMBLY))
query = strip_comments(read(QUERY))
changes = strip_comments(read(CHANGES))
presentation = read(PRESENTATION)
presentation_code = strip_comments(presentation)
screen = strip_comments(read(SCREEN))
activity = strip_comments(read(ACTIVITY))

check("A16A-01_model", contract.get("model") == "SKAX-v0" and contract.get("stage_step") == "16A", "")
check("A16A-01_status", contract.get("status") == "accepted_16a" and contract.get("decision") == "D-122", "")
ud = contract.get("user_decisions") or {}
check("A16A-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-17"
      and ud.get("scope") == "skill_projection_detail_and_overview_topic_weakness_english_in_16c_history_in_16b"
      and ud.get("delivery") == "core_and_real_compose_screen_reading_the_store"
      and ud.get("unevaluated_axis") == "recomputing_never_guessed", str(ud))
scope = contract.get("scope") or {}
check("A16A-01_scope_unchanged_rules", all(scope.get(k) is False for k in (
    "schema_changed", "migration_added", "ports_changed", "boundaries_changed", "mastery_rule_changed", "retention_rule_changed",
    "weakness_rule_changed", "gate_rule_changed", "planner_rule_changed")), str(scope))

# ---------------------------------------------------------------- one vocabulary, one derivation
sp = spwx.get("skill_presentation") or {}
kotlin_states = enum_ids(facts, "SkillPresentationState")
check("A16A-02_eight_states_in_precedence", kotlin_states == sp.get("precedence") and len(kotlin_states) == 8
      and sorted(kotlin_states) == sorted(sp.get("states") or []), str(kotlin_states))
check("A16A-02_contract_states", (contract.get("derivation") or {}).get("states") == sp.get("precedence"), "")
all_main = [p for p in ANDROID.rglob("*.kt") if "/src/main/" in p.as_posix() and "/build/" not in p.as_posix()]
definitions = [p for p in all_main if "enum class SkillPresentationState" in read(p)]
check("A16A-02_one_definition", [p.name for p in definitions] == ["SkillPresentationFacts.kt"], str(definitions))
# Only the derivation may produce a state from axes: no other main file turns an axis value into one of the eight.
producers = []
for p in all_main:
    src = strip_comments(read(p))
    if p == FACTS:
        continue
    if re.search(r"->\s*SkillPresentationState\.[A-Z_]+", src) and p.name not in ("Tone.kt",):
        producers.append(p.name)
check("A16A-02_no_second_derivation", producers == [], str(producers))
derive = body(facts, "fun derive(")
order = ["if (reading.projectionStale) return recomputing()",
         "weakness == WeaknessAxis.REMEDIATION_REQUIRED -> SkillPresentationState.REMEDIATION_REQUIRED",
         "mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE -> SkillPresentationState.CONFIRMATION_VERIFICATION_DUE",
         "RetentionAxis.VERIFICATION_DUE -> SkillPresentationState.CONFIRMATION_VERIFICATION_DUE",
         "RetentionAxis.REVIEW_DUE -> SkillPresentationState.CONFIRMED_REVIEW_DUE",
         "RetentionAxis.FRESH, RetentionAxis.STABLE, RetentionAxis.AT_RISK -> SkillPresentationState.CONFIRMED_CURRENT",
         "prerequisite.waits -> SkillPresentationState.PREREQUISITE_UNRESOLVED",
         "mastery == MasteryAxisState.DEVELOPING_WITH_SUPPORT -> SkillPresentationState.DEVELOPING_WITH_SUPPORT",
         "else -> SkillPresentationState.NOT_YET_EVIDENCED",
         "else -> SkillPresentationState.DEVELOPING_INDEPENDENT"]
positions = [derive.find(s) for s in order]
check("A16A-03_rules_in_tepm_order", all(p >= 0 for p in positions) and positions == sorted(positions), str(positions))
check("A16A-03_rule5_only_without_clean_evidence",
      "mastery == MasteryAxisState.NOT_YET_EVIDENCED || mastery == MasteryAxisState.DEVELOPING_WITH_SUPPORT -> {" in derive
      and "DEVELOPING_INDEPENDENT ->" not in derive.split("prerequisite.waits")[0].split("NOT_YET_EVIDENCED || mastery")[-1], "")
tepm = read(TEPM)
check("A16A-03_tepm_rules_present", all(s in tepm for s in ("confirmed WLRM/GRE remediation gate", "`confirmed_review_due`", "`prerequisite_unresolved`")), "")
check("A16A-04_recomputing_not_guessed", all(s in derive for s in (
    "return recomputing(UnevaluatedAxis.MASTERY)", "return recomputing(UnevaluatedAxis.WEAKNESS)",
    "RetentionAxis.UNTRACKED, RetentionAxis.NOT_YET_EVALUATED -> return recomputing(UnevaluatedAxis.RETENTION)",
    "reading.prerequisite ?: return recomputing(UnevaluatedAxis.PREREQUISITE)")), "")
check("A16A-04_recomputing_is_no_state", "NOT_DERIVED = \"not_yet_evaluated\"" in facts and "not_yet_evaluated" not in kotlin_states, "")
qualifiers = enum_ids(facts, "AttentionQualifier")
check("A16A-05_qualifiers_are_spwx_and_vdsx", qualifiers == (spwx.get("multi_axis_presentation") or {}).get("attention_qualifiers")
      and sorted(qualifiers) == sorted((vdsx.get("qualifier_tones") or {}).keys()), str(qualifiers))
check("A16A-05_at_risk_is_a_qualifier", "if (reading.retention == RetentionAxis.AT_RISK) add(AttentionQualifier.RETENTION_AT_RISK)" in derive
      and "at_risk" not in kotlin_states, "")

# ---------------------------------------------------------------- the stored column
for path, src in ((MASTERY, read(MASTERY)), (RETENTION, read(RETENTION)), (WEAKNESS, read(WEAKNESS))):
    code = strip_comments(src)
    check(f"A16A-06_{path.stem}_derives_presentation", "SkillStateRows.presented(" in code
          and '"primary_presentation_state" to carried(' not in code and 'put("primary_presentation_state"' not in code, "")
check("A16A-06_presented_uses_the_derivation", "SkillPresentationProjection.storedId(reading(axes, evidenced = true))" in assembly, "")
recompute = body(changes, "fun recompute(")
check("A16A-07_assembly_last", 0 <= recompute.find("RebuildReadiness(") < recompute.find("AssembleSkillState(persistence, clock).assemble(skill)"), "")
assemble = body(assembly, "fun assemble(")
check("A16A-07_assembly_keeps_watermark", "truthWatermark = row.truthWatermark," in assemble and "truthWatermark()" not in assemble, "")
check("A16A-07_assembly_asks_the_gate", "ResolvePrerequisites(persistence).resolve(candidate(skill)).eligibility" in assemble
      and "SkillStateRows.PREREQUISITE to eligibility.id" in assemble, "")
check("A16A-07_no_truth_written", "appendTruth" not in assembly and "appendTruth" not in query, "")

# ---------------------------------------------------------------- the read
check("A16A-08_query_only_reads", "writeProjection" not in query, "")
row_body = body(query, "private fun row(")
check("A16A-08_query_asks_gate_now", "gate.resolve(AssembleSkillState.candidate(skill.ref))" in row_body
      and "prerequisite = decision.eligibility," in row_body, "")
check("A16A-08_query_detects_staleness", "projectionStale = stored != null && rows.any { it.sequence > stored.truthWatermark }," in row_body, "")
check("A16A-08_query_reads_evidence", "evidenced = rows.isNotEmpty()," in row_body and "SkillPresentationProjection.derive(reading)" in row_body, "")

# ---------------------------------------------------------------- evidence kinds
kinds = enum_ids(progress_facts, "EvidenceKind")
check("A16A-09_evidence_kinds", kinds == ["not_counted", "provisional", "assisted", "independent"]
      and (contract.get("evidence_kinds") or {}).get("order") == kinds, str(kinds))
of = body(progress_facts, "fun of(row: EvidenceRow)")
check("A16A-09_kind_rules", all(s in of for s in ("row.outcome == EvidenceOutcome.INVALID", "row.evaluatorStatus == EvaluatorStatus.INVALID",
      "row.contested || !row.prerequisiteValid -> NOT_COUNTED", "row.evaluatorStatus == EvaluatorStatus.PROVISIONAL -> PROVISIONAL",
      "row.independenceClass != IndependenceClass.INDEPENDENT || row.solutionExposed -> ASSISTED", "else -> INDEPENDENT")), "")
check("A16A-09_spwx_distinguishes", (spwx.get("skill_detail") or {}).get("evidence_summary_distinguishes") == ["independent", "assisted", "provisional", "invalid"], "")

# ---------------------------------------------------------------- the surface
states = enum_ids(presentation_code, "ProgressState")
check("A16A-10_ten_states", states == spwx.get("semantic_states"), str(states))
tones = dict(re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", (?:Tone\.([A-Z_]+)|SystemFaultState\.[A-Z_]+\.tone)\)', body(presentation_code, "enum class ProgressState("), re.M))
surface_tones = vdsx.get("surface_state_tones") or {}
tone_ok = all((surface_tones.get(s) == (t.lower() if t else "system_fault")) for s, t in tones.items()) and len(tones) == 10
check("A16A-10_tones_are_vdsx", tone_ok, str(tones))
groups = enum_ids(presentation_code, "AttentionGroup")
check("A16A-11_attention_reasons", groups == (spwx.get("progress_overview") or {}).get("attention_reasons"), str(groups))
labels = dict(re.findall(r'SkillPresentationState\.([A-Z_]+) to "([^"]+)"', body(presentation, "val stateLabels")))
check("A16A-12_locked_labels", {k.lower(): v for k, v in labels.items()} == sp.get("labels_tr"), str(labels))
literals = re.findall(r'"((?:\\.|[^"\\])*)"', presentation) + re.findall(r'"((?:\\.|[^"\\])*)"', read(SCREEN))
forbidden_words = ["%", "puan", "skor", "seviye", "level", "streak", "tamamlandı", "başarısız", "unuttun"]
bad_literals = [s for s in literals if any(w in s.lower() for w in forbidden_words) or re.search(r"\d+\s*/\s*\d+", s)]
check("A16A-13_no_score_in_copy", bad_literals == [], str(bad_literals[:3]))
code_only = strip_strings(presentation_code)
check("A16A-13_no_division", "/" not in code_only and " Double" not in code_only and "Float" not in code_only, "")
check("A16A-13_group_has_no_count", "data class SkillGroup(val track: String, val label: String, val skills: List<SkillChip>)" in presentation_code, "")
check("A16A-13_screen_has_no_bar", not re.search(r"ProgressIndicator|progress\s*=|Slider|Gauge", screen), "")
check("A16A-14_screen_decides_nothing", "coach.application" not in screen and "coach.model.Skill" not in screen.replace("coach.model.VersionedRef", "")
      and "SkillPresentationProjection" not in screen, "")
check("A16A-14_wired", "selected == Destination.PROGRESS -> ProgressScreen(" in activity
      and "NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.SkillDetail.id)" in activity
      and "ProgressQuery(opened.graph.persistence, opened.graph.content)" in read(APPLICATION), "")
detail = body(presentation_code, "fun detail(facts: SkillDetailFacts)")
check("A16A-15_four_axes_in_detail", all(f"AxisLine(UnevaluatedAxis.{a}" in detail for a in ("MASTERY", "RETENTION", "PREREQUISITE", "WEAKNESS"))
      and "chip = chip(row)," in detail, "")
sd = contract.get("skill_detail") or {}
spwx_fields = [f.replace("_optional", "") for f in (spwx.get("skill_detail") or {}).get("semantic_fields", [])]
check("A16A-15_detail_fields_accounted", sorted(sd.get("present", []) + list((sd.get("deferred") or {}).keys())) == sorted(spwx_fields),
      f"{sorted(set(spwx_fields) ^ set(sd.get('present', []) + list((sd.get('deferred') or {}).keys())))}")
check("A16A-15_hypothesis_is_a_question", 'WeaknessSignal.HYPOTHESIS to "Açık bir soru var"' in presentation, "")

# ---------------------------------------------------------------- boundaries
check("A16A-16_core_application_does_not_see_presentation", "core-presentation" not in read(ANDROID / "core-application/build.gradle.kts"), "")
check("A16A-16_core_model_depends_on_nothing", "project(" not in read(ANDROID / "core-model/build.gradle.kts"), "")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((MODEL_TEST, ["no combination of axes confirms a Skill its mastery engine did not confirm", "at risk is never dropped and never a state",
                                  "evidence no engine has read is recomputing, never nothing yet", "a stale row is never derived, and the stored value is never a ninth state"]),
                    (QUERY_TEST, ["a learner who has done nothing is not yet evidenced, and a Skill waits on its own prerequisite",
                                  "an axis write alone never leaves an old presentation behind",
                                  "the assembled row keeps its watermark and derives the presentation from its own axes"]),
                    (PRESENTATION_TEST, ["no string scores, ranks, levels, streaks or completes", "no projected type holds a total, ratio or percentage to divide by",
                                         "skill detail keeps all four axes and the very chip the overview shows"]),
                    (COURSE_TEST, ["with all eight packages, Progress for a learner who has done nothing is an honest start, in the eight states only",
                                   "the Skills Progress shows as waiting are the ones the planner holds back"])):
    text = read(path)
    for name in names:
        check(f"A16A-17_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
for key in ("mutation_results_kotlin",):
    mr = contract.get(key) if isinstance(contract.get(key), dict) else {}
    mids = [m.get("id") for m in mr.get("mutants", [])]
    check(f"A16A-18_{key}_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
          and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
    check(f"A16A-18_{key}_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16A-18_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16A-18_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") if isinstance(contract.get("device_verification"), dict) else {}
check("A16A-18_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("screen_seen_on_device") is False
      and dv.get("sqlite_read_on_jvm_run") is True, "")

# ---------------------------------------------------------------- spec, research, decision
spec = read(SPEC)
check("A16A-19_spec_status", "**Status:** ACCEPTED — independent 16A QA PASS" in spec and "`D-122`" in spec
      and not re.search(r"\b(?:MK|MV|QA)_RESULT\b|__[A-Z][A-Z_]*__", spec), "no result placeholder is left")
check("A16A-19_spec_next", "**16B — Öğrenme geçmişi**" in spec and "**Not run: T6.**" in spec, "")
research = read(RESEARCH)
check("A16A-19_research", all(ref in research for ref in ("SPWX-v0", "TEPM-v0", "RVR-v0", "VDSX-v0", "MSBX-v0",
                                                         "https://en.wikipedia.org/wiki/Goodhart%27s_law",
                                                         "https://en.wikipedia.org/wiki/Learning_analytics")), "")
check("A16A-19_decision", re.search(r"^#+ .*D-122", read(DECISIONS), re.M) is not None, "D-122 heading in DECISIONS")

finish()
