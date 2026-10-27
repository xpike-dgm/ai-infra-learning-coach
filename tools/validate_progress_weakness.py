"""Independent 16C QA — PWPX-v0 Topic state, topic_detail, the weakness context and the Technical English profile.

The rules are read from the accepted contracts — `TSM-v0` (`docs/TOPIC_STATE_MACHINE.md`: the six states, §10's order,
§14's reason codes), `SPWX-v0` (`ux/8e_progress_skill_weakness/progress.yaml`: the Topic labels, the weakness display
rules), `TEPM-v0` (`curriculum/english/7e_mastery_profile/policy.yaml`: statuses, extension states, reason codes,
forbidden wording), `TECP-v0` (`curriculum/english/7b_cefr_progression/alignment.yaml`: the anchors), `VDSX-v0`
(`ux/8f_design_system/design_system.yaml`: Topic tones) and `UXIA-v0` (`ux/8a_information_architecture/ia.yaml`: edges) —
and compared with the real Kotlin, the shipped packages, the contract, the tests, the spec, the research and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a Topic shown as an average, a percentage or a
prerequisite claim; a Topic called locked while one of its Skills can start, or a started Topic locked again; a Topic
mastered by finished lessons alone; a review due shown as decay; a hypothesis shown as a deficiency or a remediation
closed by a task; an English level stated as general, official or numeric, a band claimed that is unpublished or
recomputing, English used as a gate; and any of it written to the store.
"""

from __future__ import annotations

import glob
import re
import sys
from collections import defaultdict
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16c_progress_weakness/progress_weakness.yaml"
QA_OUT = ROOT / "arch/16c_progress_weakness/qa_report.yaml"
SPEC = ROOT / "docs/PROGRESS_WEAKNESS_SPEC.md"
RESEARCH = ROOT / "research/16c_progress_weakness_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
TSM = ROOT / "docs/TOPIC_STATE_MACHINE.md"
SPWX = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
TEPM = ROOT / "curriculum/english/7e_mastery_profile/policy.yaml"
TECP = ROOT / "curriculum/english/7b_cefr_progression/alignment.yaml"
VDSX = ROOT / "ux/8f_design_system/design_system.yaml"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
ASSETS = ANDROID / "app-wiring/src/main/assets"

TOPIC_FACTS = ANDROID / "core-model/src/main/kotlin/coach/TopicFacts.kt"
ENGLISH_FACTS = ANDROID / "core-model/src/main/kotlin/coach/EnglishProfileFacts.kt"
PROGRESS_FACTS = ANDROID / "core-model/src/main/kotlin/coach/ProgressFacts.kt"
PORTS = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ADAPTER = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
CONTENT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
TOPIC_QUERY = ANDROID / "core-application/src/main/kotlin/coach/application/TopicQuery.kt"
ENGLISH_QUERY = ANDROID / "core-application/src/main/kotlin/coach/application/EnglishProfileQuery.kt"
PROGRESS_QUERY = ANDROID / "core-application/src/main/kotlin/coach/application/ProgressQuery.kt"
TOPIC_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TopicPresentation.kt"
ENGLISH_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/EnglishProfilePresentation.kt"
PROGRESS_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ProgressPresentation.kt"
TONE = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/Tone.kt"
TOPIC_SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/TopicScreen.kt"
ENGLISH_SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/EnglishProfileScreen.kt"
PROGRESS_SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/ProgressScreen.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
TOPIC_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/TopicStateProjectionTest.kt"
ENGLISH_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/EnglishProfileProjectionTest.kt"
QUERY_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/TopicQueryTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/TopicPresentationTest.kt"
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


def body(source: str, signature: str) -> str:
    start = source.find(signature)
    if start < 0:
        return ""
    depth, opened = 0, False
    for i in range(start, len(source)):
        if source[i] == "{":
            depth, opened = depth + 1, True
        elif source[i] == "}":
            depth -= 1
            if opened and depth == 0:
                return source[start:i + 1]
    return source[start:]


def expression(source: str, signature: str) -> str:
    """An expression-bodied function (`fun f(...) = ...`): from its signature to the first blank line. A brace scan
    would run past it into whatever function comes next."""
    start = source.find(signature)
    if start < 0:
        return ""
    end = source.find("\n\n", start)
    return source[start:end if end > 0 else len(source)]


def map_block(source: str, name: str) -> str:
    """A `val name ... = mapOf(` block up to its closing parenthesis on its own line."""
    m = re.search(rf"val {name}\b[^=]*= mapOf\((.*?)\n\s*\)\n", source, re.S)
    return m.group(1) if m else ""


def enum_ids(source: str, name: str) -> list[str]:
    return re.findall(r'^\s+[A-Z0-9_]+\("([A-Za-z0-9_]+)"', body(source, f"enum class {name}("), re.M)


def strings(source: str) -> list[str]:
    return re.findall(r'"((?:[^"\\]|\\.)*)"', source)


def finish() -> None:
    passed = sum(1 for r in results if r["result"] == "PASS")
    out = {"model": "PWPX-v0", "stage_step": "16C", "decision": "D-124", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16C QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


for path in (CONTRACT, SPEC, RESEARCH, TSM, SPWX, TEPM, TECP, VDSX, IA, TOPIC_FACTS, ENGLISH_FACTS, PROGRESS_FACTS, PORTS,
             ADAPTER, CONTENT, TOPIC_QUERY, ENGLISH_QUERY, PROGRESS_QUERY, TOPIC_PRESENTATION, ENGLISH_PRESENTATION,
             PROGRESS_PRESENTATION, TONE, TOPIC_SCREEN, ENGLISH_SCREEN, PROGRESS_SCREEN, ACTIVITY, APPLICATION, TOPIC_TEST,
             ENGLISH_TEST, QUERY_TEST, PRESENTATION_TEST, COURSE_TEST):
    check(f"A16C-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
spwx = load(SPWX) or {}
tepm = load(TEPM) or {}
tecp = load(TECP) or {}
vdsx = load(VDSX) or {}
ia = load(IA) or {}
tsm = read(TSM)
topic = strip_comments(read(TOPIC_FACTS))
english = strip_comments(read(ENGLISH_FACTS))
topic_query = strip_comments(read(TOPIC_QUERY))
english_query = strip_comments(read(ENGLISH_QUERY))
progress_query = strip_comments(read(PROGRESS_QUERY))
topic_presentation = strip_comments(read(TOPIC_PRESENTATION))
english_presentation = strip_comments(read(ENGLISH_PRESENTATION))
progress_presentation = strip_comments(read(PROGRESS_PRESENTATION))
tone = strip_comments(read(TONE))
activity = strip_comments(read(ACTIVITY))

# ---------------------------------------------------------------- contract
check("A16C-01_model", contract.get("model") == "PWPX-v0" and contract.get("stage_step") == "16C" and contract.get("decision") == "D-124", "")
ud = contract.get("user_decisions") or {}
check("A16C-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-24"
      and ud.get("topic_entry") == "an_unstarted_topic_is_available_when_one_of_its_skills_can_start_locked_only_when_none_can"
      and ud.get("coverage_gate") == "the_objective_s_teach_task_has_a_recorded_attempt_or_a_13f_diagnostic_waiver"
      and ud.get("english_history") == "replay_the_mastery_engine_over_the_evidence_no_new_table"
      and ud.get("english_unpublished_band") == "named_not_published_never_incomplete_or_failed", str(ud))
scope = contract.get("scope") or {}
check("A16C-01_scope", all(scope.get(k) is False for k in ("schema_changed", "migration_added", "new_truth_table", "boundaries_changed",
      "mastery_rule_changed", "retention_rule_changed", "weakness_rule_changed", "gate_rule_changed", "planner_rule_changed",
      "topic_state_written", "english_profile_written")) and scope.get("ports_changed") == "refinements_only" and scope.get("screen_drawn") is True, "")

# ---------------------------------------------------------------- 1. Topic states, labels, tones (TSM-v0, SPWX-v0, VDSX-v0)
tp = spwx.get("topic_presentation") or {}
states = enum_ids(read(TOPIC_FACTS), "TopicState")
check("A16C-02_six_states_tsm_order", states == tp.get("internal_states") == ["locked", "available", "learning", "mastered", "weakening", "remediation_required"], str(states))
labels = dict(re.findall(r'TopicState\.([A-Z_]+) to "([^"]+)"', map_block(read(TOPIC_PRESENTATION), "stateLabels")))
check("A16C-02_labels_are_spwx", {k.lower(): v for k, v in labels.items()} == tp.get("labels_tr"), str(labels))
tones = {k.lower(): v.lower() for k, v in re.findall(r"TopicState\.([A-Z_]+) -> LearningTone\.([A-Z_]+)", body(tone, "val TopicState.tone"))}
check("A16C-02_tones_are_vdsx", tones == vdsx.get("topic_state_tones") == (contract.get("topic") or {}).get("tones"), str(tones))
check("A16C-02_no_fault_tone", "SYSTEM_FAULT" not in enum_ids(read(TONE), "LearningTone") and "SYSTEM_FAULT" not in body(tone, "val TopicState.tone"), "")
tsm_codes = set(re.findall(r"^- `([a-z_]+)`$", tsm.split("# 14.")[1].split("# 15.")[0], re.M)) if "# 14." in tsm else set()
codes = set(re.findall(r'TopicState\.[A-Z_]+ to "([a-z_]+)"', map_block(topic, "reasonCodes")))
check("A16C-02_reason_codes_are_tsm_14", codes and codes <= tsm_codes and "hard_prerequisites_satisfied" in codes, f"{sorted(codes)} vs {sorted(tsm_codes)}")

# ---------------------------------------------------------------- 2. the derivation (TSM-v0 §10)
derive = body(topic, "fun derive(reading: TopicReading)")
check("A16C-03_one_startable_skill_opens", "if (!reading.started) {" in derive
      and "return derived(if (reading.members.any { it.startable }) TopicState.AVAILABLE else TopicState.LOCKED)" in derive, "")
started_branch = derive.split("TopicState.AVAILABLE else TopicState.LOCKED)", 1)
check("A16C-03_locked_only_before_start", len(started_branch) == 2 and "TopicState.LOCKED" not in started_branch[1],
      "LOCKED is produced only in the not-started branch")
check("A16C-03_waiting_named_only_when_locked", "waitingFor = if (state == TopicState.LOCKED) waitingFor else emptyList()," in derive
      and "metadataProblem = state == TopicState.LOCKED && metadata," in derive, "")
check("A16C-03_recomputing_never_guessed", "if (recomputing.isNotEmpty()) return TopicProjection.Recomputing(recomputing, reading)" in derive, "")
order = [derive.find(s) for s in ("remediation.isNotEmpty() -> derived(TopicState.REMEDIATION_REQUIRED",
                                  "notYetConfirmed.isEmpty() && notCovered.isEmpty() ->",
                                  "derived(TopicState.WEAKENING", "derived(TopicState.MASTERED)", "else -> derived(TopicState.LEARNING")]
check("A16C-03_tsm_10_2_order", all(i >= 0 for i in order) and order == sorted(order), str(order))
confirmed = expression(topic, "private fun confirmed(")
signal = expression(topic, "private fun retentionSignal(")
check("A16C-03_verification_keeps_confirmation", "MasteryAxisState.CONFIRMED_CURRENT" in confirmed and "MasteryAxisState.CONFIRMATION_VERIFICATION_DUE" in confirmed, "")
check("A16C-03_review_due_is_not_weakening", "REVIEW_DUE" not in signal and "RetentionAxis.AT_RISK" in signal and "RetentionAxis.VERIFICATION_DUE" in signal, "")
covered = dict(re.findall(r'^\s+([A-Z_]+)\("[a-z_]+", (true|false)\)', body(read(TOPIC_FACTS), "enum class TopicCoverage("), re.M))
check("A16C-03_only_lesson_or_waiver_covers", {k for k, v in covered.items() if v == "true"} == {"LESSON_WORKED", "DIAGNOSTIC_WAIVER"}
      and set(covered) == {"LESSON_WORKED", "DIAGNOSTIC_WAIVER", "NOT_COVERED", "NO_LESSON_AUTHORED"}, str(covered))
check("A16C-03_no_average_or_percentage", not re.search(r"\b(?:average|percent|ratio|score)\w*\b|\.average\(|/ ?members\.size", topic, re.I), "")

# ---------------------------------------------------------------- 3. the read
check("A16C-04_startable_is_the_gate", "startable = !m.prerequisite.eligibility.waits," in topic_query, "")
check("A16C-04_started_by_attempt_or_evidence", "val started = members.any { it.projection.reading.evidenced } || attempted.isNotEmpty()" in topic_query, "")
check("A16C-04_outside_only_named", "fun outside(refs: List<VersionedRef>) = refs.filter { it.logicalId !in memberIds }" in topic_query, "")
cov = body(topic_query, "private fun coverage(")
check("A16C-04_coverage_order", [cov.find(s) for s in ("lesson.any { it in attempted } -> TopicCoverage.LESSON_WORKED",
      "CoverageRows.read(persistence, objective)?.waiver != null -> TopicCoverage.DIAGNOSTIC_WAIVER",
      "lesson.isEmpty() -> TopicCoverage.NO_LESSON_AUTHORED", "else -> TopicCoverage.NOT_COVERED")] == sorted(
      cov.find(s) for s in ("lesson.any { it in attempted } -> TopicCoverage.LESSON_WORKED",
      "CoverageRows.read(persistence, objective)?.waiver != null -> TopicCoverage.DIAGNOSTIC_WAIVER",
      "lesson.isEmpty() -> TopicCoverage.NO_LESSON_AUTHORED", "else -> TopicCoverage.NOT_COVERED")) and "TopicCoverage.LESSON_WORKED" in cov, "")
check("A16C-04_same_rows_as_progress", "fun load(progress: ProgressFacts = ProgressQuery(persistence, content).load())" in topic_query
      and "TopicQuery(opened.graph.persistence, opened.graph.content).load(facts)" in strip_comments(read(APPLICATION)), "")
for name, source in (("topic", topic_query), ("english", english_query)):
    check(f"A16C-04_{name}_query_only_reads", not re.search(r"appendTruth|writeProjection|inTransaction", source), "")
writers = [p.name for p in ANDROID.rglob("*.kt") if "/src/main/" in p.as_posix()
           and re.search(r'"(?:topic_state|english_profile):', strip_comments(read(p)))]
check("A16C-04_nothing_writes_topic_or_english_rows", writers == [], str(writers))

# ---------------------------------------------------------------- 4. the English profile (TEPM-v0, TECP-v0)
anchors = re.findall(r'"(skill\.english\.[a-z_]+)" to CefrBand\.(A1|A2|B1)', body(english, "val anchors"))
expected = [(s["skill_id"], s["anchor_band"]) for s in tecp.get("skill_alignments", [])]
check("A16C-05_anchors_are_tecp", anchors == expected and len(anchors) == 15, f"{len(anchors)} vs {len(expected)}")
eligible = set(re.findall(r'"(skill\.english\.[a-z_]+)"', body(english, "val extensionEligible")))
check("A16C-05_extension_eligible_is_tecp", eligible == {s["skill_id"] for s in tecp.get("skill_alignments", []) if s.get("b2_plus_extension_eligible")}
      == set((tepm.get("b2_plus_extension") or {}).get("eligible_skill_ids", [])), str(sorted(eligible)))
base = tepm.get("base_profile") or {}
check("A16C-05_bands_are_tepm", enum_ids(read(ENGLISH_FACTS), "CefrBand") == base.get("allowed_bands"), "")
check("A16C-05_statuses_are_tepm", enum_ids(read(ENGLISH_FACTS), "BaseProfileStatus") == base.get("profile_status_values"), "")
check("A16C-05_sources_are_tepm", enum_ids(read(ENGLISH_FACTS), "ConfirmationSource") == tepm.get("confirmation_sources"), "")
check("A16C-05_extension_states_are_tepm", enum_ids(read(ENGLISH_FACTS), "ExtensionState") == (tepm.get("b2_plus_extension") or {}).get("presentation_states"), "")
used_codes = set(re.findall(r'"([A-Z][A-Z0-9_]{6,})"', english))
check("A16C-05_reason_codes_are_tepm", used_codes and used_codes <= set(tepm.get("reason_codes", [])), str(sorted(used_codes - set(tepm.get("reason_codes", [])))))
confirmed_states = set(re.findall(r"SkillPresentationState\.([A-Z_]+)", body(english, "val confirmedStates")))
check("A16C-05_confirmed_never_remediation", confirmed_states == {"CONFIRMED_CURRENT", "CONFIRMED_REVIEW_DUE", "CONFIRMATION_VERIFICATION_DUE"}, str(confirmed_states))
walk = body(english, "fun highestComplete(")
check("A16C-05_band_rule_at_or_below", "if (anchors.any { it !in published } || anchors.any { it !in confirmed }) break" in walk, "")
check("A16C-05_unpublished_and_recomputing_named", "anchors.any { it.row == null } -> BandCompletion.NOT_PUBLISHED" in english
      and "anchors.any { it.row?.projection is SkillStateProjection.Recomputing } -> BandCompletion.RECOMPUTING" in english
      and "bands.takeWhile { it.completion == BandCompletion.COMPLETE }" in english, "")
check("A16C-05_review_never_demotes", "BaseProfileStatus.COMPLETE_REVIEW_DUE" in english and "SkillPresentationState.CONFIRMED_REVIEW_DUE" in body(english, "val confirmedStates"), "")
check("A16C-05_history_replayed_never_below", "MasteryEngine.replay(skill, profiles, rows)" in english_query
      and "EnglishProfileProjection.highestComplete(mastered, published)" in english_query
      and "val historical = listOfNotNull(reading.historicalHighest, current).maxOrNull()" in english, "")
check("A16C-05_no_number_for_english", not re.search(r"val \w*(?:score|percent|average|level)\w*\s*:", english + english_presentation, re.I), "")

# ---------------------------------------------------------------- 5. wording
forbidden = [r"İngilizcen\b", r"\bresm[iî] (?:A|B)[12]\b", r"sertifikalı", r"\bgeçtin\b", r"\bseviyen\b", r"başarısız", r"zayıfsın", r"%"]
copy = strings(english_presentation) + strings(topic_presentation) + strings(body(progress_presentation, "fun misconceptionLine("))
copy += strings(body(progress_presentation, "val remediationTexts")) + re.findall(r'REMEDIATION_CLOSURE = "([^"]+)"', progress_presentation)
bad = [s for s in copy for w in forbidden if re.search(w, s, re.I)]
check("A16C-06_no_level_claim_or_blame", bad == [], str(bad[:3]))
check("A16C-06_english_scope_and_gate_said", "genel bir İngilizce düzeyi, resmî bir CEFR düzeyi ya da sertifika değildir" in english_presentation
      and "İngilizce hiçbir teknik iş için kapı değildir." in english_presentation, "")
check("A16C-06_unpublished_named", 'BandCompletion.NOT_PUBLISHED to "Henüz yayımlanmadı"' in english_presentation, "")
check("A16C-06_headline_is_qualified", all(h.startswith("Teknik İngilizce — ") for h in re.findall(r'"(Teknik İngilizce — [^"]*)"', english_presentation))
      and len(re.findall(r'"Teknik İngilizce — ', english_presentation)) >= 4, "")
check("A16C-06_locked_is_not_a_verdict", "Kilitli kalıcı bir hüküm değildir" in topic_presentation and "ön koşul değildir" in topic_presentation, "")

# ---------------------------------------------------------------- 6. weakness context (SPWX-v0 §10)
mline = body(progress_presentation, "fun misconceptionLine(")
check("A16C-07_hypothesis_is_the_catalog_question", 'WeaknessSignal.HYPOTHESIS -> "Açık soru: $openQuestion"' in mline and "WeaknessSignal.NONE -> null" in mline, "")
check("A16C-07_closure_said_task_never_closes", "Bir görevi bitirmek tek başına kapatmaz" in progress_presentation
      and "closure = if (reading.weakness == WeaknessAxis.REMEDIATION_REQUIRED) ProgressCopy.REMEDIATION_CLOSURE else null," in progress_presentation, "")
check("A16C-07_only_held_signals_read", "signal?.takeIf { it != WeaknessSignal.NONE }" in progress_query and "MisconceptionRows.key(m.ref)" in progress_query, "")
check("A16C-07_spwx_display_rules", (spwx.get("weakness_presentation") or {}).get("display_rules", {}).get("hypothesis") == "open_question_at_most", "")

# ---------------------------------------------------------------- 7. ports and adapters
ports = read(PORTS)
check("A16C-08_port_refinements_defaulted", "fun attemptedResources(resources: List<VersionedRef>): Set<VersionedRef> = emptySet()" in ports
      and "fun lessonItemsFor(objective: VersionedRef): List<VersionedRef> = emptyList()" in ports, "")
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("A16C-08_port_count_unchanged", sorted(interfaces) == sorted(["ClockPort", "PersistencePort", "ContentPort", "EvaluatorPort", "TutorPort"]), str(interfaces))
check("A16C-08_adapter_reads_attempts", "SELECT DISTINCT resource_logical_id, resource_version FROM attempt WHERE" in read(ADAPTER), "")
check("A16C-08_lessons_are_teach_tasks", "it.purpose == TaskPurpose.TEACH && objective in it.targetObjectives" in read(CONTENT), "")

# ---------------------------------------------------------------- 8. screens and edges (UXIA-v0)
edges = [tuple(e) for e in (ia.get("navigation_edges") or {}).get("contextual", [])] or \
        [tuple(e) for e in re.findall(r"- \[(\w+), (\w+)\]", read(IA))]
# Narrowed at 17E (`RUXX-v0` / `D-138`): technical_english_profile -> skill_detail was added to UXIA-v0 by the user's
# decision, recorded as an amendment in 17E's contract; without that record the edge still fails.
_amended = "technical_english_profile -> skill_detail" in read(ROOT / "arch/17e_remaining_ux/remaining_ux.yaml")
check("A16C-09_edges_accepted", all(e in edges for e in (("progress", "topic_detail"), ("topic_detail", "skill_detail"),
      ("progress", "technical_english_profile"))) and (("technical_english_profile", "skill_detail") not in edges or _amended), "")
check("A16C-09_wired_on_accepted_edges", all(s in activity for s in (
    "NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.TopicDetail.id)",
    "NavigationGraph.canOpen(Surface.TopicDetail.id, Surface.SkillDetail.id)",
    "NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.TechnicalEnglishProfile.id)")), "")
english_screen = strip_comments(read(ENGLISH_SCREEN))
# Narrowed at 17E (`D-138`): the profile links a Skill only through the accepted edge, and the activity asks the graph.
check("A16C-09_english_profile_links_no_skill", "onOpenSkill" not in english_screen
      or (("technical_english_profile", "skill_detail") in edges and _amended
          and "NavigationGraph.canOpen(Surface.TechnicalEnglishProfile.id, Surface.SkillDetail.id)" in activity),
      "no accepted technical_english_profile -> skill_detail edge")
for path in (TOPIC_SCREEN, ENGLISH_SCREEN, PROGRESS_SCREEN):
    s = strip_comments(read(path))
    check(f"A16C-09_screen_decides_nothing_{path.stem}", "coach.application" not in s
          and not re.search(r"ProgressIndicator|LazyVerticalGrid|Calendar|heatmap|\d+\s*%", s), "")

# ---------------------------------------------------------------- 9. the shipped data (independent recomputation)
links: dict[str, set[str]] = defaultdict(set)
hard: list[tuple[str, str]] = []
tasks: list[dict] = []
for f in sorted(glob.glob(str(ASSETS / "curriculum_package*.txt"))):
    section, cur = None, {}

    def flush() -> None:
        if section == "[topic_skill]":
            links[cur["topic"]].add(cur["skill"].split("@")[0])
        elif section == "[prerequisite_edge]" and cur.get("edge_kind") == "hard":
            hard.append((cur["prerequisite"].split("@")[0], cur["target"].split("@")[0]))
        elif section == "[task]":
            tasks.append(dict(cur))

    for line in read(Path(f)).split("\n"):
        line = line.strip()
        if line.startswith("["):
            if section:
                flush()
            section, cur = line, {}
        elif "=" in line and section:
            k, v = line.split("=", 1)
            cur[k] = v
    if section:
        flush()
roots = {s for members in links.values() for s in members} - {t for _, t in hard}
open_topics = [t for t, members in links.items() if members & roots]
check("A16C-10_new_learner_four_open", len(links) == 27 and len(open_topics) == 4, f"{len(links)} topics, {len(open_topics)} open")
first_rule_locked_startable = [t for t, m in links.items() if (m & roots) and any(d in m and s not in m for s, d in hard)]
check("A16C-10_first_rule_disagreed", len(first_rule_locked_startable) >= 1
      and "the_first_entry_rule_locked_topics_the_planner_was_offering" in (contract.get("found") or []), str(first_rule_locked_startable))
teach_items = {i for t in tasks if t.get("purpose") == "teach" for i in t.get("items", "").split(",") if i}
other_items = {i for t in tasks if t.get("purpose") != "teach" for i in t.get("items", "").split(",") if i}
teach_objectives = {o for t in tasks if t.get("purpose") == "teach" for o in t.get("target_objectives", "").split(",") if o}
check("A16C-10_lesson_items_disjoint", teach_items and not (teach_items & other_items)
      and (contract.get("topic") or {}).get("lesson_items_disjoint_from_practice_items") is True, f"{len(teach_items)} vs {len(other_items)}")
check("A16C-10_every_objective_has_a_lesson", len(teach_objectives) == 65, str(len(teach_objectives)))
published_english = {s for m in links.values() for s in m if s.startswith("skill.english.")}
check("A16C-10_b1_unpublished_a1_a2_published", {b: sum(1 for s, band in expected if band == b and s in published_english) for b in ("A1", "A2", "B1")}
      == (contract.get("english_profile") or {}).get("published_anchors") == {"A1": 5, "A2": 5, "B1": 0}, "")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((TOPIC_TEST, ["an unstarted Topic is available when one of its Skills can start, and locked only when none can",
                                  "a started Topic never goes back to locked, whatever its entry says now",
                                  "mastered needs every Skill confirmed and every Objective covered — finishing the lessons is not enough",
                                  "a scheduled review keeps the Topic mastered, while at_risk or an open verification is weakening"]),
                    (ENGLISH_TEST, ["E7E-F01 partial A2 is A1 complete with the exact A2 gap visible and an uneven profile",
                                    "E7E-F04 and F15 a confirmed remediation lowers the current band and the history stays",
                                    "nothing evidenced is not yet complete, and an unpublished band is named as such, never as a gap"]),
                    (QUERY_TEST, ["an attempt on a lesson item starts the Topic and covers that Objective, even before any evidence",
                                  "a stored diagnostic waiver covers its Objective without a lesson attempt, and marks the confirmation's source",
                                  "every A1 Skill confirmed is the A1 base, and the replay keeps the band after a clean recheck fails"]),
                    (PRESENTATION_TEST, ["skill detail shows a hypothesis only as the catalog's question and remediation with what closes it",
                                         "the English profile uses allowed wording only, names an unpublished band and keeps its history"]),
                    (COURSE_TEST, ["with all eight packages, a new learner's Topic is available exactly when one of its Skills can start, and nothing is written",
                                   "with all eight packages, the Technical English profile is honestly not complete, B1 is not published and nothing is claimed"])):
    text = read(path)
    for name in names:
        check(f"A16C-11_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("A16C-12_kotlin_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("A16C-12_kotlin_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16C-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16C-12_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") or {}
check("A16C-12_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("screen_seen_on_device") is False
      and dv.get("sqlite_read_on_jvm_run") is True, "")

# ---------------------------------------------------------------- spec, research, decision
spec = read(SPEC)
check("A16C-13_spec_status", "**Status:** ACCEPTED — independent 16C QA PASS" in spec and "`D-124`" in spec
      and not re.search(r"__[A-Z][A-Z_]*__", spec), "no result placeholder is left")
check("A16C-13_spec_next", "**16D — Ayarlar**" in spec and "**Not run: T6.**" in spec, "")
research = read(RESEARCH)
check("A16C-13_research", all(r in research for r in ("TSM-v0", "SPWX-v0", "TEPM-v0", "TECP-v0", "VDSX-v0", "UXIA-v0",
      "https://en.wikipedia.org/wiki/Common_European_Framework_of_Reference_for_Languages",
      "https://en.wikipedia.org/wiki/Mastery_learning")), "")
check("A16C-13_decision", re.search(r"^#+ .*D-124", read(DECISIONS), re.M) is not None, "D-124 heading in DECISIONS")

finish()
