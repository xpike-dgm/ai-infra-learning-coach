"""Independent 14E QA — ACCX-v0 AI-Generated Code Comprehension.

The rules are read from `2D` §4, §8, §9 and §17, `V1_SUCCESS_CRITERIA` SC-011/SC-012 and `TRUX-v0` §9, and compared with
the real Kotlin — never with 14E's own contract alone. The rules the step exists for must be **unrepresentable** as a
PASS here: running someone else's code counted as the learner's production, a comprehension answer counted as
production, a check offered on the learner's own work or on suspicion, a skip counted as wrong, a tutor-written question
recorded as evidence or offered while a written one exists, and AI-written code left as practice that opens no recheck.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/14e_code_comprehension/code_comprehension.yaml"
SPEC = ROOT / "docs/CODE_COMPREHENSION_IMPL_SPEC.md"
RESEARCH = ROOT / "research/14e_code_comprehension_research.md"
QA_OUT = ROOT / "arch/14e_code_comprehension/qa_report.yaml"
AAE = ROOT / "docs/AI_ASSISTANCE_EVIDENCE_SPEC.md"
SC = ROOT / "docs/V1_SUCCESS_CRITERIA.md"
DWF = ROOT / "docs/DAILY_WORKING_FLOW_SPEC.md"
TUTX = ROOT / "arch/14a_tutor_contract/tutor_contract.yaml"
DECISIONS = ROOT / "docs/DECISIONS.md"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/ComprehensionFacts.kt"
INTERP_KT = ANDROID / "core-model/src/main/kotlin/coach/AssistanceInterpretation.kt"
TUTOR_KT = ANDROID / "core-model/src/main/kotlin/coach/TutorFacts.kt"
INSTR_KT = ANDROID / "core-model/src/main/kotlin/coach/TutorInstructions.kt"
USE_KT = ANDROID / "core-application/src/main/kotlin/coach/application/CheckUnderstanding.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ComprehensionPresentation.kt"
MASTERY_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/MasteryEngine.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SOURCE_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
PKG_KT = ANDROID / "core-model/src/main/kotlin/coach/CurriculumPackage.kt"

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


def expression(source: str, signature: str) -> str:
    """A member up to the next declaration at its own indentation (expression-bodied functions included)."""
    at = source.find(signature)
    if at < 0:
        return ""
    line_start = source.rfind("\n", 0, at) + 1
    indent = at - line_start
    end = re.search(r"\n {0,%d}(?=[^\s)])" % indent, source[at:])
    return source[at:at + (end.start() if end else len(source) - at)]


def params(source: str, signature: str) -> str:
    at = source.find(signature)
    if at < 0:
        return ""
    open_at = source.find("(", at)
    depth = 0
    for index in range(open_at, len(source)):
        if source[index] == "(":
            depth += 1
        elif source[index] == ")":
            depth -= 1
            if depth == 0:
                return source[open_at:index + 1]
    return ""


def load(path: Path):
    try:
        return yaml.safe_load(read(path)) or {}
    except yaml.YAMLError:
        return {}


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, USE_KT, PRES_KT):
    check(f"E14E-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = load(CONTRACT)
facts, interp, tutor = strip_comments(read(FACTS_KT)), strip_comments(read(INTERP_KT)), strip_comments(read(TUTOR_KT))
instr, use, pres, mastery = strip_comments(read(INSTR_KT)), strip_comments(read(USE_KT)), strip_comments(read(PRES_KT)), strip_comments(read(MASTERY_KT))
ports, schema, fmt, source, pkg = (strip_comments(read(PORTS_KT)), strip_comments(read(SCHEMA_KT)), strip_comments(read(FORMAT_KT)),
                                   strip_comments(read(SOURCE_KT)), strip_comments(read(PKG_KT)))
aae, sc, dwf, spec_text, research_text = read(AAE), read(SC), read(DWF), read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- contract head and the accepted texts it rests on
check("E14E-01_model", contract.get("model") == "ACCX-v0", str(contract.get("model")))
check("E14E-01_status", contract.get("status") == "accepted_14e", str(contract.get("status")))
check("E14E-01_decision", contract.get("decision") == "D-109", str(contract.get("decision")))
scope = contract.get("scope", {})
for key, expected in {"written_checks_as_content": True, "offered_after_submission": True, "tutor_practice_intent": True,
                      "generated_code_opens_recheck": True, "schema_changed": False, "state_family_added": False, "mastery_rule_changed": False,
                      "weakness_rule_changed": False, "planner_rule_changed": False, "comprehension_counts_as_production": False,
                      "check_mandatory": False, "checks_authored": False, "app_calls_check": False}.items():
    check(f"E14E-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E14E-01_user_decisions", contract.get("user_decisions") == {"date": "2026-10-02", "question_source": "written_first_otherwise_tutor_practice",
      "timing": "right_after_submission_optional", "generated_code": "opens_independent_recheck"}, str(contract.get("user_decisions")))
for phrase, cid in (("artifact'ın çalışması, kullanıcının coding/production mastery'si için direct positive evidence oluşturmaz", "2d_4"),
                    ("target objective gerekiyorsa independent recheck açılır", "2d_8a"),
                    ("fakat bu tek başına `coding/production` evidence'a dönüşmez", "2d_8e"),
                    ("Recheck türü hedef Learning Objective'in davranışıyla eşleşmelidir", "2d_9"),
                    ("Kullanıcı yalnız AI kodunu açıklayabildi → coding production mastered.", "2d_17_3")):
    check(f"E14E-01_source_{cid}", phrase in aae, f"2D moved: {phrase[:50]}")
check("E14E-01_sc011", "comprehension/transfer doğrulaması olmadan kritik skill'i mastered yapamaz" in sc, "SC-011 moved")
check("E14E-01_sc012", "en az bir uygun doğrulama yolu tetikleyebilmelidir" in sc, "SC-012 moved")
check("E14E-01_asked_not_inferred", "provenance is **asked, not inferred**" in dwf, "TRUX-v0 §9 moved")

# ---------------------------------------------------------------- kinds, from 2D §9
kinds = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\)', body(facts, "enum class ComprehensionKind"), re.M)
check("E14E-02_kinds", kinds == contract.get("kinds", {}).get("ids") == ["line_purpose", "removal_effect", "state_effect", "find_the_bug"], str(kinds))
check("E14E-02_kind_sources_in_2d", all(q in aae for q in ("“Bu satır neden gerekli?”", "“Bunu kaldırırsak ne olur?”", "“Bu çözümdeki hatayı bul.”")), "2D §9 questions moved")
check("E14E-02_no_production_kind", not re.search(r"apply|again|variation|write|produce", " ".join(kinds)), str(kinds))

# ---------------------------------------------------------------- when it is offered
offered = expression(facts, "fun isOffered(")
check("E14E-03_generated_offered", "submission.provenance == ProvenanceOrigin.GENERATED_OR_COPIED" in offered, "")
check("E14E-03_mixed_offered", "submission.provenance == ProvenanceOrigin.MIXED_AUTHORSHIP" in offered, "")
check("E14E-03_shown_solution_offered", "it.scope == AssistanceScope.TARGET_OBJECTIVE && it.timing in beforeTheAnswerFroze && it.level.revealsTargetReasoning" in offered, "")
check("E14E-03_before_freeze_only", "setOf(AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT)" in facts, "")
check("E14E-03_not_on_suspicion", not re.search(r"UNKNOWN_PROVENANCE|USER_AUTHORED\b|USER_AUTHORED_WITH_ASSISTANCE|artifactContentRef|length|contains\(", offered), offered[:200])
offer = expression(facts, "fun offer(")
check("E14E-03_written_first", offer.find("written.isNotEmpty() -> ComprehensionOffer.Written") >= 0
      and offer.find("written.isNotEmpty() -> ComprehensionOffer.Written") < offer.find("else -> ComprehensionOffer.TutorPractice")
      and offer.find("!isOffered(submission) -> ComprehensionOffer.NotOffered") >= 0, offer[:200])

# ---------------------------------------------------------------- written checks and what an answer proves
chk = body(facts, "data class ComprehensionCheck")
check("E14E-04_id", 'Regex("^comprehension(\\\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")' in chk, "")
check("E14E-04_choices", "choices.size >= 2 && choices.keys.all { it in CHOICE_KEYS }" in chk and 'val CHOICE_KEYS = listOf("a", "b", "c", "d")' in chk, "")
check("E14E-04_answer_a_choice", "require(answer in choices)" in chk, "")
ans = body(facts, "fun answer(item: AssessmentItem, check: ComprehensionCheck, chosen: String?)")
check("E14E-04_never_production", "require(check.evidenceType != item.evidenceType)" in ans, "2D §8 scenario E")
check("E14E-04_targets_only", "require(check.objective in item.targetObjectives)" in ans and "require(check.item == item.ref)" in ans, "")
check("E14E-04_skip_writes_nothing", "if (chosen.isNullOrBlank()) return ComprehensionResult.Skipped" in ans, "")
check("E14E-04_key", "val correct = chosen == check.answer" in ans and "require(chosen in check.choices)" in ans, "")
check("E14E-04_one_component", "componentResults = listOf(ComponentResult(check.objective, if (correct) OutcomeSignal.MET else OutcomeSignal.NOT_MET))" in ans, "")
check("E14E-04_evaluator", 'EvaluatorRef(EVALUATOR_PROVIDER, EVALUATOR_MODEL, "${check.ref.logicalId}@v${check.ref.version}")' in ans
      and 'const val EVALUATOR_PROVIDER = "deterministic"' in facts, "")
check("E14E-04_no_numbers", not re.search(r"\d+\.\d+|threshold|percent|ratio|score", ans + offered, re.I), "")
check("E14E-04_mastery_reads_direct_types", "row.evidenceType !in profile.directEvidenceTypes" in mastery, "the engine's own profile decides what counts")

# ---------------------------------------------------------------- independence of AI-written work
branches = re.findall(r"(\w+(?:\.\w+)*) == [\w.]+ -> IndependenceClass\.([A-Z_]+)|-> IndependenceClass\.([A-Z_]+)", body(interp, "fun independence("))
classes = [b[1] or b[2] for b in branches]
after = [r["class"] for r in contract.get("independence_narrowing", {}).get("after", [])]
check("E14E-05_rules_after", [c.lower() for c in classes] == after, f"{classes} vs {after}")
indep = body(interp, "fun independence(")
check("E14E-05_generated_rechecks", "provenance == ProvenanceOrigin.GENERATED_OR_COPIED -> IndependenceClass.REQUIRES_INDEPENDENT_RECHECK" in indep, "2D §8 scenario A")
check("E14E-05_teach_before_provenance", indep.find("purpose == TaskPurpose.TEACH -> IndependenceClass.PRACTICE_ONLY") >= 0
      and indep.find("purpose == TaskPurpose.TEACH") < indep.find("provenance == ProvenanceOrigin.GENERATED_OR_COPIED"), "")
check("E14E-05_unknown_accuses_no_one", "UNKNOWN_PROVENANCE" not in indep, "")
tutx = load(TUTX)
check("E14E-05_before_is_14a", [r["class"] for r in tutx.get("independence", {}).get("rules", [])]
      == [r["class"] for r in contract.get("independence_narrowing", {}).get("before", [])], "the declared 'before' is 14A's list")
check("E14E-05_recheck_opens_in_engine", "it.independenceClass == IndependenceClass.REQUIRES_INDEPENDENT_RECHECK" in mastery, "MasteryEngine unresolved recheck")

# ---------------------------------------------------------------- the tutor's practice
intents = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\)', body(tutor, "enum class TutorIntent"), re.M)
ext = contract.get("tutor_extension", {})
check("E14E-06_intent_appended", intents[:5] == [i["id"] for i in tutx.get("intents", [])] and intents[5:] == ext.get("intents") == ["check_understanding"], str(intents))
prep = body(tutor, "fun prepare(ask: TutorAsk)")
block = body(prep, "TutorIntent.CHECK_UNDERSTANDING ->")
check("E14E-06_only_after_freeze", "if (!answerFrozen) return TutorPreparation.Redirect(TutorIntent.HINT, TutorRedirectReason.NO_ANSWER_IS_FROZEN_YET)" in block
      and "if (ask.timing == null) return TutorPreparation.Redirect(TutorIntent.EXPLAIN_DIFFERENTLY" in block, block[:200])
check("E14E-06_needs_code", "if (ask.context.learnerWork.isNullOrBlank()) return TutorPreparation.Incomplete(TutorMissing.FROZEN_ANSWER)" in block, "")
check("E14E-06_fields_only_for_check", "require((ask.checkQuestion == null && ask.learnerAnswer == null) || ask.intent == TutorIntent.CHECK_UNDERSTANDING)" in prep, "")
check("E14E-06_answer_with_question", "require(ask.learnerAnswer == null || !ask.checkQuestion.isNullOrBlank())" in prep, "")
req_fields = re.findall(r"val (\w+):", params(tutor, "class TutorRequest internal constructor"))
check("E14E-06_request_fields", req_fields[-2:] == ["checkQuestion", "learnerAnswer"], str(req_fields))
check("E14E-06_versions", f'const val VERSION = "{ext.get("instructions_version")}"' in instr and f'const val REPLY_SCHEMA_ID = "{ext.get("reply_schema")}"' in instr
      and ext.get("instructions_version") == "tutor_instructions/3" and ext.get("reply_schema") == "tutor_reply/2", "")
check("E14E-06_rule_16", "16. check_understanding" in instr and "ask exactly one short question" in instr and "Never grade the answer." in instr, "")
check("E14E-06_sections", 'section("check_question", request.checkQuestion)' in instr and 'section("learner_answer", request.learnerAnswer)' in instr
      and ext.get("message_sections") == ["check_question", "learner_answer"], "")
check("E14E-06_neutralised", re.search(r'tags = listOf\([^)]*"check_question", "learner_answer"\)', instr) is not None, "")
practice = body(use, "fun practice(")
check("E14E-06_written_first", practice.find("if (content.comprehensionChecksFor(item.ref).isNotEmpty()) return null") >= 0
      and practice.find("comprehensionChecksFor") < practice.find("ask.ask("), practice[:200])
check("E14E-06_intent_required", "require(request.intent == TutorIntent.CHECK_UNDERSTANDING)" in practice, "")
check("E14E-06_never_evidence", not re.search(r"RecordEvidence|appendTruth|evidence_event|EvaluationResult", practice), "")
check("E14E-06_use_records_nothing", not re.search(r"appendTruth|writeProjection|inTransaction|RecordEvidence", use), "the use case decides; the caller records")
check("E14E-06_answer_only_written", "require(check in content.comprehensionChecksFor(item.ref))" in body(use, "fun answer("), "")

# ---------------------------------------------------------------- content, ports, storage
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("E14E-07_port_count", len(interfaces) == contract.get("port_count") == 5, str(interfaces))
check("E14E-07_content_refinement", "fun comprehensionChecksFor(item: VersionedRef): List<ComprehensionCheck>" in body(ports, "interface ContentPort")
      and contract.get("port_refinements") == [{"port": "ContentPort", "method": "comprehensionChecksFor"}], "")
check("E14E-07_source_pins_version", "parsed?.comprehensionChecks.orEmpty().filter { it.item == item }" in source, "")
check("E14E-07_section", re.search(r'KNOWN_SECTIONS = setOf\([^)]*"comprehension_check"', fmt, re.S) is not None, "")
keys = re.search(r'"comprehension_check" to setOf\(([^)]*)\)', fmt)
check("E14E-07_keys", keys is not None and re.findall(r'"(\w+)"', keys.group(1)) == ["logical_id", "version", "item", "objective", "kind", "evidence_type",
      "prompt", "choice_a", "choice_b", "choice_c", "choice_d", "answer"], keys.group(1) if keys else "")
check("E14E-07_strict", "check(section)" in body(fmt, "fun comprehensionCheck(section: Section)"), "")
check("E14E-07_not_published", "comprehension" not in pkg.lower() and "comprehension" not in schema.lower(), "content, never published")
check("E14E-07_schema_unchanged", "const val VERSION = 8" in schema and contract.get("schema_version") == 8, "")

# ---------------------------------------------------------------- what is said
copy = body(pres, "object ComprehensionCopy")
p = contract.get("presentation", {})
for key in ("offer", "not_production", "tutor_practice", "recheck"):
    check(f"E14E-08_copy_{key}", p.get(key, "?") in copy, key)
check("E14E-08_not_offered_silent", "ComprehensionOffer.NotOffered -> null" in pres, "")
check("E14E-08_result_says_not_production", "add(ComprehensionCopy.NOT_PRODUCTION)" in body(pres, "fun result("), "")
check("E14E-08_practice_labelled", "listOf(ComprehensionCopy.TUTOR_PRACTICE) + TutorPresentation.notes(outcome)" in pres, "")
check("E14E-08_recheck_only_when_opened", ".takeIf { independence == IndependenceClass.REQUIRES_INDEPENDENT_RECHECK }" in pres, "")
strings = " ".join(re.findall(r'"([^"]*)"', copy))
check("E14E-08_no_numbers", not re.search(r"\d|%", strings), strings[:200])
check("E14E-08_no_accusation", not re.search(r"kopya|hile|başarısız|puan|cheat", strings, re.I), "")
check("E14E-08_skip_not_wrong", "geçmek yanlış sayılmaz" in copy and "bu yanlış sayılmadı" in copy, "")

# ---------------------------------------------------------------- narrowed gates are declared and still pass the rest
gates = {g["file"]: g["checks"] for g in contract.get("narrowed_gates", [])}
tv, av = read(ROOT / "tools/validate_tutor_contract.py"), read(ROOT / "tools/validate_alternative_explanation.py")
check("E14E-09_14a_narrowed", gates.get("tools/validate_tutor_contract.py") == ["E14A-02_intents", "E14A-05_message_sections", "E14A-06_version", "E14A-07_interpretation_order"]
      and tv.count("Narrowed at 14E") >= 3 and "arch/14e_code_comprehension/code_comprehension.yaml" in tv, "")
check("E14E-09_14c_narrowed", gates.get("tools/validate_alternative_explanation.py") == ["E14C-07_version_raised", "E14C-07_reply_schema_unchanged", "E14C-07_canonical_is_material"]
      and av.count("Narrowed at 14E") >= 2, "")

# ---------------------------------------------------------------- verification claims
for name, suite in (contract.get("suites") or {}).items():
    check(f"E14E-10_suite_{name}", (ROOT / suite.get("file", "")).is_file(), suite.get("file", ""))
mr = contract.get("mutation_results", {})
ids = [m.get("id") for m in mr.get("mutants", [])]
check("E14E-10_mutation_all_detected", mr.get("total") == mr.get("detected") == len(ids) and len(set(ids)) == len(ids) and len(ids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E14E-10_compile_failure_not_detection", mr.get("compile_failure_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E14E-10_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation", {})
check("E14E-10_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs", [])
check("E14E-10_runs_pass", len(runs) == 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification", {})
check("E14E-10_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False, "")
check("E14E-10_forbidden", len(contract.get("forbidden_patterns", [])) == 7, str(contract.get("forbidden_patterns")))
check("E14E-10_open_loops", set(contract.get("open_loops", {})) == {"written_checks_for_real_tasks", "check_rendering_and_app_call",
      "adapter_prompt_for_check", "free_text_answers", "trusting_tutor_questions"}, str(contract.get("open_loops")))

# ---------------------------------------------------------------- spec, research, decision
check("E14E-11_spec_status", "**Status:** ACCEPTED — independent 14E QA PASS" in spec_text and "`D-109`" in spec_text, "")
check("E14E-11_spec_next", "**14F — Açık uçlu cevap değerlendirme**" in spec_text, "")
check("E14E-11_spec_t6", "**Not run: T6.**" in spec_text, "")
check("E14E-11_research", "No web research pass was needed" in research_text, "")
check("E14E-11_decision", re.search(r"^#+ .*D-109", read(DECISIONS), re.M) is not None, "D-109 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {"model": "ACCX-v0", "stage_step": "14E", "decision": "D-109", "result": "PASS" if not failures else "FAIL",
          "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"14E QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
