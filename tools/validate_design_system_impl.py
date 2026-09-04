"""Independent 10C QA — DSIX-v0 Design System Implementation.

The theme is validated against the accepted design system, not against its own contract. Every
token in the Kotlin source is compared byte-for-byte with `WFPX-v0`'s measured palette, every
state→tone assignment with `VDSX-v0`'s accepted map, and **every contrast ratio is recomputed
here from the hex values** rather than read from a stored number — the same discipline that
caught a hand-declared minimum at 8G.

If a token were quietly adjusted to make a check pass, this fails.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]

CONTRACT = ROOT / "arch/10c_design_system/design_system_impl.yaml"
SPEC = ROOT / "docs/DESIGN_SYSTEM_IMPL_SPEC.md"
RESEARCH = ROOT / "research/10c_design_system_research.md"
QA_OUT = ROOT / "arch/10c_design_system/qa_report.yaml"

DESIGN_SYSTEM = ROOT / "ux/8f_design_system/design_system.yaml"
WIREFRAME = ROOT / "ux/8g_wireframe_prototype/wireframe.yaml"
TECHNOLOGY = ROOT / "arch/9a_mobile_technology/technology.yaml"

TOKENS_KT = ROOT / "android/core-presentation/src/main/kotlin/coach/presentation/DesignTokens.kt"
TONE_KT = ROOT / "android/core-presentation/src/main/kotlin/coach/presentation/Tone.kt"
TOKENS_TEST = ROOT / "android/core-presentation/src/test/kotlin/coach/presentation/DesignSystemTest.kt"
THEME_KT = ROOT / "android/app-ui/src/main/kotlin/coach/ui/CoachTheme.kt"
ANDROID = ROOT / "android"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


# ---- WCAG 2.2, recomputed here. Nothing below reads a stored ratio. -------------------
def _channel(value: int) -> float:
    c = value / 255.0
    return c / 12.92 if c <= 0.03928 else ((c + 0.055) / 1.055) ** 2.4


def luminance(hex_color: str) -> float:
    h = hex_color.lstrip("#")
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    return 0.2126 * _channel(r) + 0.7152 * _channel(g) + 0.0722 * _channel(b)


def contrast(a: str, b: str) -> float:
    la, lb = luminance(a), luminance(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)


contract = load(CONTRACT)
vdsx = load(DESIGN_SYSTEM)
wfpx = load(WIREFRAME)
amts = load(TECHNOLOGY)

for path in (TOKENS_KT, TONE_KT, TOKENS_TEST, THEME_KT, SPEC, RESEARCH):
    check(f"E10C-00_exists_{path.name}", path.is_file(), f"missing {path}")

tokens_src = read(TOKENS_KT)
tone_src = read(TONE_KT)
tokens_test = read(TOKENS_TEST)
theme_src = read(THEME_KT)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity
check("E10C-01_model", contract.get("model") == "DSIX-v0", str(contract.get("model")))
check("E10C-01_status", contract.get("status") == "accepted_10c", str(contract.get("status")))
check("E10C-01_decision", contract.get("decision") == "D-084", str(contract.get("decision")))
for key in ("design_system_changed", "geometry_changed", "state_vocabulary_changed",
            "palette_revised", "new_visual_semantics_introduced"):
    check(f"E10C-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("aesthetic_quality_claim", "rendering_performance_claim"):
    check(f"E10C-01_noclaim_{key}", contract.get("scope", {}).get(key, "missing") is None,
          f"{key}={contract.get('scope', {}).get(key)}")

# ------------------------------------ tokens match the measured palette exactly
def kotlin_token_map(block_name: str) -> dict[str, str]:
    block = re.search(rf"private val {block_name}: Map<String, String> = mapOf\((.*?)\n    \)",
                      tokens_src, re.S)
    if not block:
        return {}
    return dict(re.findall(r'"([a-z_]+)" to "(#[0-9A-Fa-f]{6})"', block.group(1)))


kotlin_palette = {"light": kotlin_token_map("light"), "dark": kotlin_token_map("dark")}
for theme in ("light", "dark"):
    accepted = wfpx["palette"]["themes"][theme]
    found = kotlin_palette[theme]
    check(f"E10C-02_token_set_{theme}", set(found) == set(accepted),
          f"missing={sorted(set(accepted) - set(found))} extra={sorted(set(found) - set(accepted))}")
    mismatched = {k: (found.get(k), v) for k, v in accepted.items() if found.get(k) != v}
    check(f"E10C-02_token_values_{theme}", not mismatched, f"mismatched={mismatched}")
check("E10C-02_token_count", all(len(kotlin_palette[t]) == 18 for t in ("light", "dark")),
      f"light={len(kotlin_palette['light'])} dark={len(kotlin_palette['dark'])}")
check("E10C-02_palette_not_revised", contract["palette"].get("revised_here") is False
      and wfpx["palette"].get("measured_per_theme") is True,
      "10C may not revise the measured palette")
check("E10C-02_dark_not_inversion", wfpx["palette"].get("dark_is_inversion_of_light") is False,
      "WFPX-v0 measured each theme separately")

# ---------------------------------- contrast, recomputed from those same tokens
text_pairs = [("on_surface", "surface"), ("on_surface", "surface_variant"),
              ("on_surface_muted", "surface"), ("on_surface_muted", "surface_variant")]
non_text_pairs = [("outline", "surface"), ("outline", "surface_variant"),
                  ("focus_ring", "surface"), ("focus_ring", "surface_variant")]
text_min = wfpx["palette"]["contrast_requirements"]["text_on_surface_min"]
non_text_min = wfpx["palette"]["contrast_requirements"]["non_text_and_boundary_min"]
tone_min = wfpx["palette"]["contrast_requirements"]["text_on_tone_min"]

measured = {"text_on_surface": [], "non_text_and_boundary": [], "text_on_tone": []}
for theme in ("light", "dark"):
    palette = kotlin_palette[theme]
    for fg, bg in text_pairs:
        ratio = contrast(palette[fg], palette[bg])
        measured["text_on_surface"].append(ratio)
        check(f"E10C-03_text_{theme}_{fg}_on_{bg}", ratio >= text_min,
              f"{round(ratio, 2)}:1 < {text_min}")
    for fg, bg in non_text_pairs:
        ratio = contrast(palette[fg], palette[bg])
        measured["non_text_and_boundary"].append(ratio)
        check(f"E10C-03_nontext_{theme}_{fg}_on_{bg}", ratio >= non_text_min,
              f"{round(ratio, 2)}:1 < {non_text_min}")
    for tone, (container, on_container) in wfpx["tone_token_map"].items():
        ratio = contrast(palette[on_container], palette[container])
        measured["text_on_tone"].append(ratio)
        check(f"E10C-03_tone_text_{theme}_{tone}", ratio >= tone_min,
              f"{round(ratio, 2)}:1 < {tone_min}")
        boundary = contrast(palette[container], palette["surface"])
        measured["non_text_and_boundary"].append(boundary)
        check(f"E10C-03_tone_boundary_{theme}_{tone}", boundary >= non_text_min,
              f"{round(boundary, 2)}:1 < {non_text_min}")

# The recorded minima must still be the truth about these tokens.
for key, recorded in wfpx["palette"]["measured_minima"].items():
    computed = round(min(measured[key]), 2)
    check(f"E10C-04_minimum_{key}", abs(computed - recorded) < 0.01,
          f"recomputed={computed} recorded={recorded}")
check("E10C-04_recomputed_not_asserted",
      contract["contrast"].get("recomputed_from_hex") is True
      and contract["contrast"].get("asserted_from_stored_ratio") is False
      and wfpx["palette"].get("validator_recomputes_from_hex") is True,
      "contrast must be recomputed, never asserted")
check("E10C-04_product_test_recomputes",
      "Contrast.ratio(" in tokens_test and "fun ratio(" in tokens_src,
      "the product's own suite must recompute contrast too")
check("E10C-04_both_themes_in_test", "Theme.entries.forEach" in tokens_test,
      "both themes must be checked by the product test")

# ------------------------------------------------------------------ tones
accepted_tones = [t["id"] for t in vdsx["tones"]]
kotlin_tones = re.findall(r'^\s{4}([A-Z_]+)\("([a-z_]+)"\),', tone_src, re.M)
kotlin_tone_ids = [tid for _, tid in kotlin_tones if tid in set(accepted_tones)]
check("E10C-05_tone_count", len(accepted_tones) == 6, f"vdsx tones={accepted_tones}")
check("E10C-05_tone_ids_match", set(kotlin_tone_ids) == set(accepted_tones),
      f"kotlin={sorted(set(kotlin_tone_ids))} vdsx={sorted(accepted_tones)}")
check("E10C-05_tone_token_map_matches",
      set(contract["tones"]["ids"]) == set(wfpx["tone_token_map"].keys()),
      f"contract={sorted(contract['tones']['ids'])} wfpx={sorted(wfpx['tone_token_map'])}")

# ------------------------------------- the fault tone is structurally unavailable
check("E10C-06_learning_tone_type_exists", "enum class LearningTone" in tone_src,
      "a separate learning-tone type must exist")
learning_block = re.search(r"enum class LearningTone\(val tone: Tone\) \{(.*?)\n\}", tone_src, re.S)
learning_values = re.findall(r"^\s{4}([A-Z_]+)\(", learning_block.group(1), re.M) if learning_block else []
check("E10C-06_learning_tone_count", len(learning_values) == 5, f"values={learning_values}")
check("E10C-06_no_fault_in_learning_tone", "SYSTEM_FAULT" not in "".join(learning_values),
      "LearningTone must not contain a fault value")
check("E10C-06_skill_tone_returns_learning_tone",
      "val SkillPresentationState.tone: LearningTone" in tone_src,
      "a Skill state must be typed so it cannot return the fault tone")
check("E10C-06_upstream_forbids",
      vdsx["severity_rule"].get("learning_state_may_use_fault_tone") is False
      and vdsx["invariants"].get("learning_state_uses_fault_tone") is False,
      "VDSX-v0 forbids the fault tone on a learning state")
check("E10C-06_tested", "no learning state can wear the fault tone" in tokens_test,
      "the prohibition must be tested")
check("E10C-06_enforcement_recorded",
      contract["learning_tone"].get("enforcement") == "structural_type_has_no_fault_value",
      str(contract["learning_tone"].get("enforcement")))

# ----------------------------------------- state→tone maps match the accepted maps
accepted_skill_tones = vdsx["skill_state_tones"]
kotlin_state_tones = dict(re.findall(
    r"SkillPresentationState\.([A-Z_]+) -> LearningTone\.([A-Z_]+)", tone_src))
tone_name_by_id = {t["id"]: t["id"].upper() for t in vdsx["tones"]}
for state_id, tone_id in accepted_skill_tones.items():
    kotlin_key = state_id.upper()
    expected = tone_name_by_id[tone_id]
    check(f"E10C-07_skill_tone_{state_id}", kotlin_state_tones.get(kotlin_key) == expected,
          f"kotlin={kotlin_state_tones.get(kotlin_key)} vdsx={expected}")
check("E10C-07_skill_tone_count", len(accepted_skill_tones) == 8, f"{len(accepted_skill_tones)}")
check("E10C-07_contract_map_matches", contract["skill_state_tone_map"] == accepted_skill_tones,
      "the contract's map must equal VDSX-v0's")

accepted_qualifiers = vdsx["qualifier_tones"]
check("E10C-08_qualifier_map_matches", contract["qualifier_tone_map"] == accepted_qualifiers,
      f"contract={contract['qualifier_tone_map']} vdsx={accepted_qualifiers}")
for qid in accepted_qualifiers:
    check(f"E10C-08_qualifier_present_{qid}", f'"{qid}"' in tone_src, f"missing qualifier {qid}")

# --------------------------------------------- attention grouping and fault states
check("E10C-09_grouping_named_function", "fun toneInAttentionGroup" in tone_src,
      "the grouping rule must be a named function so it can be tested")
check("E10C-09_grouping_does_not_upgrade",
      vdsx["severity_rule"].get("attention_group_membership_changes_tone") is False
      and "appearing in an attention group does not upgrade a tone" in tokens_test,
      "grouping must not change a tone, and it must be tested")
accepted_fault_states = vdsx["fault_tone_allowed_states"]
check("E10C-10_fault_states_match",
      contract["system_fault_states"]["ids"] == accepted_fault_states,
      f"contract={contract['system_fault_states']['ids']} vdsx={accepted_fault_states}")
for fid in accepted_fault_states:
    check(f"E10C-10_fault_state_{fid}", f'"{fid}"' in tone_src, f"missing {fid}")
check("E10C-10_material_error_reserved",
      "error = fault" in theme_src and "onError = onFault" in theme_src,
      "Material's error role must carry the system fault tone and nothing else")

# --------------------------------------------------------- dynamic colour absent
kotlin_files = list(ANDROID.rglob("*.kt"))
dynamic_hits = [str(p.relative_to(ROOT)) for p in kotlin_files
                if re.search(r"dynamic(Light|Dark)ColorScheme", read(p))]
check("E10C-11_no_dynamic_colour_anywhere", not dynamic_hits, f"found in {dynamic_hits}")
check("E10C-11_fixed_schemes_used",
      "lightColorScheme()" in theme_src and "darkColorScheme()" in theme_src,
      "the theme must be built from the fixed schemes")
check("E10C-11_upstream_forbids", amts["invariants"].get("dynamic_color_enabled") is False,
      "AMTS-v0 disables dynamic colour")
check("E10C-11_contract_records_scan",
      contract["dynamic_colour"].get("enforcement") == "source_scan"
      and contract["dynamic_colour"].get("mention_in_prose_also_fails") is True,
      "the scan is a plain text scan and the contract must say so")

# ------------------------------------------------------ targets, scaling, colour
check("E10C-12_touch_target",
      vdsx["spacing_and_targets"]["minimum_touch_target_dp"] == 48
      and "MINIMUM_TOUCH_TARGET_DP = 48" in tokens_src
      and "fun Modifier.minimumTouchTarget" in theme_src,
      "the 48dp floor must be a real modifier")
check("E10C-12_stricter_than_wcag",
      vdsx["spacing_and_targets"].get("adopts_stricter_platform_rule") is True
      and vdsx["spacing_and_targets"].get("wcag_target_floor_px") == 24,
      "VDSX-v0 adopts the stricter platform rule")
check("E10C-13_text_scales",
      wfpx["text_scaling"]["max_supported_percent"] == 200
      and "listOf(100, 150, 200)" in tokens_src,
      "the supported text scales must reach 200%")
check("E10C-14_state_has_text", "stateDescription = label" in theme_src,
      "a state chip must expose its state as text")
check("E10C-14_chip_renders_text", "Text(" in theme_src and "text = label" in theme_src,
      "a state chip must render a text label")
check("E10C-14_not_colour_only",
      contract["colour_alone"].get("state_conveyed_by_colour_only") is False
      and vdsx["invariants"].get("state_conveyed_by_color_alone", False) is False,
      "state may not be carried by colour alone")

# ------------------------------------------------------------ Turkish casing
case_hits = []
for path in kotlin_files:
    text = read(path)
    for match in re.finditer(r"\.(uppercase|lowercase|capitalize)\(\s*\)", text):
        case_hits.append(f"{path.relative_to(ROOT)}:{match.group(0)}")
check("E10C-15_no_locale_naive_case", not case_hits, f"locale-naive transforms: {case_hits}")
check("E10C-15_upstream_forbids",
      amts["invariants"].get("default_locale_case_transform_allowed") is False
      and amts["invariants"].get("locked_labels_case_transformed_for_styling") is False,
      "AMTS-v0 forbids default-locale transforms and styling-driven casing")
check("E10C-15_contract_records",
      contract["turkish_casing"].get("default_locale_case_transform_present") is False,
      str(contract["turkish_casing"].get("default_locale_case_transform_present")))

# ------------------------------------------------------------ runs and honesty
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E10C-16_runs_recorded", len(runs) >= 4, f"count={len(runs)}")
check("E10C-16_all_pass", all(r["result"] == "PASS" for r in runs.values()),
      str({k: v["result"] for k, v in runs.items()}))
check("E10C-16_no_adapter_still_builds",
      any("-PwithAiAdapter=false" in r["command"] for r in runs.values()),
      "the no-adapter build must still be verified")
check("E10C-16_corrections_recorded",
      len(contract.get("build_corrections", [])) >= 1
      and all(c.get("found_by") for c in contract["build_corrections"]),
      "what the checks corrected must be recorded rather than quietly fixed")

# ------------------------------------------------------ forbidden patterns
forbidden = set(contract.get("forbidden_design_impl_patterns", []))
for pattern in [
    "deriving_the_palette_from_the_wallpaper",
    "naming_a_dynamic_scheme_builder_anywhere_including_prose",
    "asserting_a_contrast_ratio_instead_of_recomputing_it",
    "revising_a_measured_token_to_make_a_check_pass",
    "giving_a_learning_state_the_fault_tone",
    "letting_a_component_choose_a_different_tone_than_the_state_declares",
    "upgrading_a_tone_because_the_state_appears_in_an_attention_group",
    "conveying_state_by_colour_alone",
    "an_interactive_target_below_the_accepted_floor",
    "a_locale_naive_case_transform_on_a_locked_label",
    "introducing_a_visual_severity_the_canonical_state_does_not_claim",
]:
    check(f"E10C-17_forbidden_{pattern[:34]}", pattern in forbidden, f"missing={pattern}")

# --------------------------------------------------------- spec and synthesis
for fragment in [
    "**Status:** ACCEPTED — independent 10C QA PASS",
    "**Decision:** `D-084`",
    "recomputed",
    "LearningTone",
    "48dp",
]:
    check(f"E10C-18_spec_{fragment[:28]}", fragment in spec_text, f"missing={fragment!r}")
check("E10C-18_spec_records_correction", "prose" in spec_text and "source scan" in spec_text,
      "the spec must record the check that caught its own comment")
for fragment in ["measured, not chosen", "structurally"]:
    check(f"E10C-19_research_{fragment[:24]}", fragment.lower() in research_text.lower(),
          f"missing={fragment!r}")

# -------------------------------------------------------------- acceptance
acceptance = contract.get("acceptance", {})
check("E10C-20_required_models",
      {"VDSX-v0", "WFPX-v0", "SPWX-v0", "MSBX-v0", "MPSX-v0", "NSHX-v0"}
      <= set(acceptance.get("required_previous_models", [])),
      str(acceptance.get("required_previous_models")))
for key in ("independent_qa_required", "build_must_be_run_not_asserted",
            "contrast_must_be_recomputed_not_asserted", "stage6_regression_required",
            "stage7_regression_required", "stage8_regression_required",
            "stage9_regression_required", "stage10a_regression_required",
            "stage10b_regression_required"):
    check(f"E10C-20_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

# ------------------------------------------------------------------ report
passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "DSIX-v0",
    "stage_step": "10C",
    "decision": "D-084",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results),
    "checks_passed": passed,
    "checks_failed": len(failures),
    "tokens_per_theme": len(kotlin_palette["light"]),
    "recomputed_minima": {k: round(min(v), 2) for k, v in measured.items()},
    "checks": results,
    "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"10C_DESIGN_SYSTEM_IMPL_QA={report['result']}")
print(f"checks={passed}/{len(results)} tokens_per_theme={len(kotlin_palette['light'])} "
      f"minima={report['recomputed_minima']}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not failures else 1)
