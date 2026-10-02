"""Independent 11D QA — DMAX-v0 Daily Micro Assessment.

The implementation is validated against the accepted contracts, not against its own. The session
states, scopes, intents, result families, `not_reliably_measured` sources and recomposition
conditions are read out of `ASUX-v0`'s `session.yaml`; the tones out of `VDSX-v0`; the curriculum
columns out of the schema itself; the port set out of `MSBX-v0` — each compared with the actual
Kotlin source.

Several checks are structural: what must be unrepresentable (a scored result, a self-promoting item,
an overwritten curriculum version, a truncated artifact body) is looked for in the code with
comments stripped.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/11d_daily_micro_assessment/daily_micro.yaml"
SPEC = ROOT / "docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md"
RESEARCH = ROOT / "research/11d_daily_micro_assessment_research.md"
QA_OUT = ROOT / "arch/11d_daily_micro_assessment/qa_report.yaml"

SESSION_YAML = ROOT / "ux/8d_assessment_session/session.yaml"
VDSX = ROOT / "ux/8f_design_system/design_system.yaml"
DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/AssessmentFacts.kt"
BODY_KT = ANDROID / "core-model/src/main/kotlin/coach/ArtifactBody.kt"
PACKAGE_KT = ANDROID / "core-model/src/main/kotlin/coach/CurriculumPackage.kt"
SESSION_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/AssessmentSession.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/DailyMicroAssessment.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
STORE_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/CurriculumStore.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SOURCE_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/AssessmentSessionScreen.kt"
APPLICATION_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"

FACTS_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/AssessmentFactsTest.kt"
SESSION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/AssessmentSessionTest.kt"
APP_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/DailyMicroAssessmentTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/CurriculumPublishingTest.kt"
FORMAT_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/PackageFormatTest.kt"

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
    start = source.find(signature)
    if start < 0:
        return ""
    open_at = source.find("{", start + len(signature))
    if open_at < 0:
        return ""
    depth = 0
    for i in range(open_at, len(source)):
        depth += {"{": 1, "}": -1}.get(source[i], 0)
        if depth == 0:
            return source[open_at + 1:i]
    return ""


def declaration(source: str, signature: str) -> str:
    """A declaration's own text, brace-bodied or expression-bodied.

    Kotlin lets a function be `fun f() = expr`, which has no brace body at all; reading only brace
    bodies is how a check silently inspects an empty string and passes on anything.
    """
    start = source.find(signature)
    if start < 0:
        return ""
    brace = source.find("{", start + len(signature))
    equals = source.find("=", start + len(signature))
    if brace >= 0 and (equals < 0 or brace < equals):
        return body(source, signature)
    end = source.find("\n\n", start)
    return source[start:end if end > 0 else len(source)]


def constructor_params(source: str, cls: str) -> list[str]:
    match = re.search(rf"class {cls}\b[^(]*\((.*?)\n\)", source, re.S)
    return re.findall(r"val (\w+)\s*:", match.group(1)) if match else []


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


contract = load(CONTRACT)
session_contract = load(SESSION_YAML)
vdsx = load(VDSX)
ddm = load(DDM)
msbx = load(MSBX)

for path in (FACTS_KT, BODY_KT, PACKAGE_KT, SESSION_KT, APP_KT, STORE_KT, FORMAT_KT,
             FACTS_TEST, SESSION_TEST, APP_TEST, T2_TEST, FORMAT_TEST, SPEC, RESEARCH):
    check(f"E11D-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

facts = strip_comments(read(FACTS_KT))
artifact_body = strip_comments(read(BODY_KT))
package = strip_comments(read(PACKAGE_KT))
session = strip_comments(read(SESSION_KT))
app = strip_comments(read(APP_KT))
ports = strip_comments(read(PORTS_KT))
store = strip_comments(read(STORE_KT))
schema = read(SCHEMA_KT)
fmt = strip_comments(read(FORMAT_KT))
source = strip_comments(read(SOURCE_KT))
screen = strip_comments(read(SCREEN_KT))
application = strip_comments(read(APPLICATION_KT))
facts_test = read(FACTS_TEST)
session_test = read(SESSION_TEST)
app_test = read(APP_TEST)
t2_test = read(T2_TEST)
format_test = read(FORMAT_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E11D-01_model", contract.get("model") == "DMAX-v0", str(contract.get("model")))
check("E11D-01_status", contract.get("status") == "accepted_11d", str(contract.get("status")))
check("E11D-01_decision", contract.get("decision") == "D-090", str(contract.get("decision")))
for key in ("evidence_written", "planner_implemented", "weekly_or_monthly_blueprint_implemented",
            "mastery_engine_implemented", "content_authored_here", "schema_changed", "migration_added",
            "ports_added", "boundaries_changed", "surface_semantics_changed"):
    check(f"E11D-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("passing_threshold", "percentage_grade", "fixed_question_count", "countdown_clock"):
    check(f"E11D-01_no_{key}", contract["scope"].get(key) is None, f"{key} was claimed")

# ---------------------------------------------------------------- vocabularies from ASUX-v0
check("E11D-02_states", enum_ids(session, "SessionState") == session_contract["semantic_states"],
      f"kotlin={enum_ids(session, 'SessionState')}")
check("E11D-02_scopes", enum_ids(facts, "AssessmentScope") == session_contract["session_model"]["assessment_scopes"],
      f"kotlin={enum_ids(facts, 'AssessmentScope')}")
check("E11D-02_intents", enum_ids(facts, "AssessmentIntent") == session_contract["session_model"]["assessment_intents"],
      f"kotlin={enum_ids(facts, 'AssessmentIntent')}")
check("E11D-02_result_families",
      enum_ids(session, "ResultFamily") == session_contract["result_presentation"]["semantic_families"],
      f"kotlin={enum_ids(session, 'ResultFamily')}")
check("E11D-02_not_reliably_measured",
      enum_ids(session, "NotReliablyMeasured") == session_contract["result_presentation"]["not_reliably_measured_sources"],
      f"kotlin={enum_ids(session, 'NotReliablyMeasured')}")
check("E11D-02_recomposition",
      enum_ids(session, "RecompositionCondition") == session_contract["pause_resume_recomposition"]["recomposition_conditions"],
      f"kotlin={enum_ids(session, 'RecompositionCondition')}")
check("E11D-02_boundary_kinds",
      enum_ids(session, "BoundaryKind") == session_contract["block_structure"]["boundary_kinds"],
      f"kotlin={enum_ids(session, 'BoundaryKind')}")
check("E11D-02_lifecycle",
      enum_ids(facts, "LifecycleStatus") == ["draft", "candidate", "validated", "trusted", "deprecated",
                                             "invalidated", "retired"],
      f"kotlin={enum_ids(facts, 'LifecycleStatus')}")
check("E11D-02_use_ceiling",
      enum_ids(facts, "UseCeiling") == ["practice_only", "low_stakes_assessment", "standard_mastery_eligible",
                                        "critical_mastery_eligible"],
      f"kotlin={enum_ids(facts, 'UseCeiling')}")
check("E11D-02_content_origin", len(enum_ids(facts, "ContentOrigin")) == 5, str(enum_ids(facts, "ContentOrigin")))

# ---------------------------------------------------------------- tones from VDSX-v0
tones = vdsx["surface_state_tones"]
tone_body = body(session, "val SessionState.tone")


def tone_map(source: str) -> dict[str, str]:
    """state id -> tone id, read from the `when` arms, including grouped ones."""
    mapping: dict[str, str] = {}
    for names, value in re.findall(r"([A-Za-z_.,\s]+?)->\s*Tone\.([A-Z_]+)", source):
        for name in re.sub(r"\s", "", names).split(","):
            if name.startswith("SessionState."):
                mapping[name.removeprefix("SessionState.")] = value.lower()
    return mapping


kotlin_tones = tone_map(tone_body)
for state in enum_ids(session, "SessionState"):
    expected = tones.get(state)
    if expected is None:
        continue
    check(f"E11D-03_tone_{state}", kotlin_tones.get(state.upper()) == expected,
          f"kotlin={kotlin_tones.get(state.upper())} vdsx={expected}")
check("E11D-03_every_state_has_a_tone", len(kotlin_tones) == len(enum_ids(session, "SessionState")),
      f"mapped={len(kotlin_tones)} states={len(enum_ids(session, 'SessionState'))}")
check("E11D-03_fault_tone_only_system",
      {s for s, t in kotlin_tones.items() if t == "system_fault"} == {"ERROR_RECOVERABLE", "DATA_RECOVERY_REQUIRED"},
      f"fault states={[s for s, t in kotlin_tones.items() if t == 'system_fault']}")
check("E11D-03_ui_names_no_tone", "Tone." not in screen or "tone = " in screen and "Tone.SYSTEM_FAULT" not in screen,
      "app-ui chooses a tone")

# ---------------------------------------------------------------- item trust
ceiling = body(facts, "fun effectiveCeiling(")
check("E11D-04_unselectable_has_no_ceiling", "if (!item.lifecycleStatus.selectable) return null" in ceiling,
      "a draft or invalidated item still gets a ceiling")
check("E11D-04_candidate_is_practice_only",
      "LifecycleStatus.CANDIDATE) ceiling = ceiling.atMost(UseCeiling.PRACTICE_ONLY)" in ceiling,
      "an unvalidated item is not capped at practice")
check("E11D-04_ai_generated_capped",
      "ContentOrigin.AI_GENERATED" in ceiling and "UseCeiling.STANDARD_MASTERY_ELIGIBLE" in ceiling,
      "an AI-generated item is not capped below critical")
check("E11D-04_provisional_evaluator_capped",
      "EvaluatorStatusRequirement.PROVISIONAL_ALLOWED" in ceiling and "UseCeiling.LOW_STAKES_ASSESSMENT" in ceiling,
      "a provisional evaluator does not cap the item")
check("E11D-04_deterministic_required", "item.evaluatorRequirement.deterministicRequired" in ceiling,
      "a required deterministic check is not enforced")
check("E11D-04_evaluator_absent_caps", "!evaluatorAvailable" in ceiling, "an unavailable evaluator does not cap the item")
check("E11D-04_ceiling_only_lowers", "atMost" in ceiling and "atLeast" not in facts,
      "the ceiling can be raised")
check("E11D-04_declared_is_the_start", "var ceiling = item.declaredUseCeiling" in ceiling,
      "the ceiling does not start from what the item declared")
required = body(facts, "fun required(intent: AssessmentIntent)")
check("E11D-04_mastery_requires_standard",
      "MASTERY_EVIDENCE -> UseCeiling.STANDARD_MASTERY_ELIGIBLE" in required
      and "VERIFICATION -> UseCeiling.STANDARD_MASTERY_ELIGIBLE" in required,
      "a mastery intent does not require the standard ceiling")

fit = body(facts, "fun fit(")
check("E11D-05_objective_decides_fit", "profile.acceptableEvidenceTypes" in fit,
      "the Objective's profile does not decide evidence fit")
check("E11D-05_direct_type_for_mastery", "profile.requiredDirectType" in fit and "directEvidenceTypes" in fit,
      "a mastery measurement does not require the Objective's direct type")
check("E11D-05_unknown_profile_unfit", "OBJECTIVE_PROFILE_UNKNOWN" in fit, "an unknown Objective profile is assumed fit")
check("E11D-05_scope_checked", "scope !in item.scopeEligibility" in fit, "scope eligibility is not checked")

# ---------------------------------------------------------------- trust comes from the store
offer = body(app, "fun offer(")
check("E11D-06_store_decides_trust", "persistence.latestValidation(ref)?.status ?: LifecycleStatus.CANDIDATE" in offer,
      "the item document decides its own trust")
check("E11D-06_unpublished_is_unknown", "persistence.resourceVersion(ref) ?: return ItemOffer.Unknown" in offer,
      "an unpublished item is served anyway")
check("E11D-06_missing_document_is_unknown", "content.assessmentItem(ref) ?: return ItemOffer.Unknown" in offer,
      "an item with no authored document is served anyway")
check("E11D-06_exposure_only_when_served",
      "recordExposure" in body(offer, "is ItemFit.Usable ->") or
      ("is ItemFit.Usable" in offer and offer.index("recordExposure") > offer.index("is ItemFit.Usable")),
      "exposure is recorded for an item that was not served")
check("E11D-06_unusable_records_nothing", "is ItemFit.NotUsable -> ItemOffer.Unusable(fit.reasons)" in offer,
      "an unusable item records something")
check("E11D-06_exposure_kinds",
      'const val ITEM_VERSION_SEEN = "item_version_seen"' in app and 'const val SOLUTION_EXPOSURE = "solution_exposure"' in app,
      "the exposure kinds are not DDM-v0's")
check("E11D-06_serving_writes_no_evidence", "evidence_event" not in app and '"attempt"' not in app,
      "serving an item writes evidence or an attempt")

# ---------------------------------------------------------------- curriculum ingestion
refusal = body(store, "fun refusal(")
check("E11D-07_no_overwrite", "if (versionExists(curriculum.version)) return PublishOutcome.AlreadyPublished" in refusal,
      "a published version can be overwritten")
check("E11D-07_unresolved_refused", "unresolvedReferences(::isPublished)" in refusal,
      "references are not resolved against the package and the store")
# Narrowed at 15B (`PYFX-v0` / `D-113`, user decision): an entity's version is its own semantic revision, not the
# package's, so a later package may bring version-1 entities; what is refused is carrying an already published entity.
check("E11D-07_version_mismatch_refused", "republishedEntities(curriculum)" in refusal,
      "an already published entity can be carried again")
check("E11D-07_one_transaction",
      "inTransaction { curriculumStore.write(" in strip_comments(read(ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt")),
      "publishing is not one transaction")
check("E11D-07_refusal_before_write",
      "curriculumStore.refusal(curriculum)?.let { return it }" in
      strip_comments(read(ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt")),
      "a refusal is decided after writing starts")
check("E11D-07_publishes_no_truth", "appendTruth" not in store, "publishing writes truth rows")

ddl = re.search(r"CREATE TABLE IF NOT EXISTS assessment_resource_version \((.*?)\n\s*\)", schema, re.S)
ddl_columns = set(re.findall(r"^\s*(\w+)\s+(?:TEXT|INTEGER)", ddl.group(1) if ddl else "", re.M))
ddm_resource = next(e for e in ddm["curriculum_entities"] if e["id"] == "assessment_resource_version")
check("E11D-08_schema_unchanged", ddl_columns == set(ddm_resource["fields"]) | {"logical_id", "version"},
      f"schema={sorted(ddl_columns)} ddm={sorted(ddm_resource['fields'])}")
check("E11D-08_no_invented_item_columns",
      not ({"use_ceiling", "scope_eligibility", "independence_mode", "difficulty_class", "target_objectives"} & ddl_columns),
      "item metadata DDM-v0 does not name was added as columns")

# ---------------------------------------------------------------- authored package parsing
check("E11D-09_format_versioned", 'const val FORMAT = "curriculum_package/1"' in fmt, "the package format is not versioned")
check("E11D-09_unknown_section_refused", "unknown section" in fmt, "an unknown section is accepted")
check("E11D-09_unknown_key_refused", "unknown key" in fmt, "an unknown key is accepted")
check("E11D-09_repeated_key_refused", "repeated key" in fmt, "a repeated key is accepted")
check("E11D-09_unpinned_ref_refused", "is not a pinned reference" in fmt, "an unpinned reference is accepted")
check("E11D-09_refuses_whole_package", "if (reasons.isNotEmpty() || curriculum == null) throw ParseFailure(reasons)" in fmt,
      "a package with problems is partly imported")
check("E11D-09_failed_parse_serves_nothing",
      # Narrowed at 15B (`D-113`): the packages are read together; a failure leaves `loaded` null, so nothing is served.
      "parsed?.documents" in source and "parsed?.items" in source and "loaded?.first()?.curriculum" in source
      and "loaded.orEmpty()" in source,
      "a failed parse still serves fragments")
check("E11D-09_no_package_is_null", "source() ?: return@lazy null" in source, "a missing package is not null")

# ---------------------------------------------------------------- session interior
check("E11D-10_boundary_is_submission_unit", "fun submit(session: AssessmentSessionView, boundaryId: String)" in session,
      "the submission unit is not the boundary")
check("E11D-10_item_boundary_one_item",
      "kind == BoundaryKind.TESTLET || items.size == 1" in session, "an item boundary may carry several items")
transition = body(session, "private fun transition(")
check("E11D-10_frozen_is_final",
      "if (current == BoundaryStatus.FROZEN && !allowFrozen) return BoundaryOutcome.Refused(BoundaryRefusal.ALREADY_FROZEN)" in transition,
      "a frozen boundary can be changed")
check("E11D-10_navigable_are_open", "filter { session.statusOf(it.id) == BoundaryStatus.OPEN }" in declaration(session, "fun navigable("),
      "navigation is not limited to open boundaries")
check("E11D-10_skip_is_its_own_status", "BoundaryStatus.UNSUBMITTED" in declaration(session, "fun skip("),
      "skipping is not represented as unsubmitted")
check("E11D-10_assistance_not_blocked", "const val BLOCKED: Boolean = false" in session, "assistance can be blocked")
consequence = declaration(session, "fun consequenceOf(")
check("E11D-10_revealing_help_is_practice_only",
      "level.revealsTargetReasoning) AssistanceConsequence.PRACTICE_ONLY_SOLUTION_EXPOSED" in consequence,
      "revealing help does not become practice-only")
check("E11D-10_no_assisted_mastery_evidence",
      "val producesIndependentMasteryEvidence: Boolean get() = false" in session,
      "an assisted attempt can produce independent mastery evidence")
check("E11D-10_never_schedules_recheck", "val schedulesRecheck: Boolean get() = false" in session,
      "the session schedules the recheck")
check("E11D-10_recomposition_spares_frozen",
      ".filter { session.statusOf(it) != BoundaryStatus.FROZEN }" in declaration(session, "fun recompose("),
      "recomposition deletes completed evidence")
check("E11D-10_dispute_is_contested", "BoundaryStatus.CONTESTED" in declaration(session, "fun contest("),
      "a dispute does not hold evidence as contested")
check("E11D-10_dispute_does_not_invalidate", "invalid" not in declaration(session, "fun contest(").lower(),
      "a dispute invalidates the item")

result_of = body(session, "fun of(")
check("E11D-11_change_needs_canonical", "canonicalChanges[family].orEmpty()" in result_of,
      "a result claims a change nobody reported")
check("E11D-11_partial_from_unresolved",
      "statuses.any { it == BoundaryStatus.OPEN || it == BoundaryStatus.UNSUBMITTED }" in result_of,
      "an unresolved session is not partial")
check("E11D-11_unsubmitted_is_unreliable", "NotReliablyMeasured.UNSUBMITTED_SLOT" in result_of,
      "a skipped slot is not surfaced as not-reliably-measured")
check("E11D-11_contested_is_unreliable", "NotReliablyMeasured.CONTESTED_ITEM" in result_of,
      "a contested item is not surfaced as not-reliably-measured")
FORBIDDEN_FIELDS = ("score", "grade", "percent", "threshold", "countdown", "timer", "streak", "rank")
for cls in ("SessionResult", "AssessmentSessionView", "Boundary", "AssessmentItem"):
    fields = constructor_params(session + facts, cls)
    check(f"E11D-11_no_verdict_field_{cls}",
          not [f for f in fields if any(w in f.lower() for w in FORBIDDEN_FIELDS)], f"{cls} fields={fields}")
check("E11D-11_counts_are_separate", "data class InformationalCounts" in session and
      "val isInformationalOnly: Boolean get() = true" in session, "raw counts are not kept separate from state")

# ---------------------------------------------------------------- artifact body
check("E11D-12_inline_or_refused",
      "if (body.length > MAX_BODY_CHARS) Stored.TooLarge(body.length)" in declaration(artifact_body, "fun store("),
      "an oversized body is not refused")
check("E11D-12_not_truncated", "take(" not in artifact_body and "substring(0" not in artifact_body,
      "a body is truncated to fit")
check("E11D-12_foreign_ref_is_null", "if (!contentRef.startsWith(PREFIX)) null" in declaration(artifact_body, "fun read("),
      "a foreign content reference is guessed at")
check("E11D-12_no_new_table", "artifact_body" not in schema, "a table was invented for artifact bodies")

# ---------------------------------------------------------------- ports and boundaries
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx_ports = [p["id"] for p in msbx["ports"]["set"]]
check("E11D-13_port_count", sorted(interfaces) == sorted(list(msbx_ports) + declared_port_extensions(ROOT)), f"interfaces={interfaces} msbx={msbx_ports}")
persistence_methods = re.findall(r"fun (?:<T> )?(\w+)\(", body(ports, "interface PersistencePort"))
for method in ("publishCurriculum", "resourceVersion", "latestValidation", "objectiveProfile"):
    check(f"E11D-13_refinement_{method}", method in persistence_methods, f"missing {method}")
content_methods = re.findall(r"fun (\w+)\(", body(ports, "interface ContentPort"))
# 11D owns `assessmentItem` and `curriculumPackage` on this port, not the rest of it: 12C refined the same
# interface (`taskCandidates`), exactly as 11D itself refined PersistencePort after 11C. So this check was
# narrowed from an exact list to what 11D actually decided (the E11C-10 precedent).
check("E11D-13_content_methods", content_methods[:3] == ["resource", "assessmentItem", "curriculumPackage"],
      f"methods={content_methods}")
check("E11D-13_ingestion_on_store_thread", "IngestCurriculum(" in application and "openApp" in application,
      "ingestion does not run where the store is opened")
# Narrowed by 15A (CPFX-v0 / D-112), the step that owns authoring: an asset may now ship, but only the one generated
# from authored content by tools/build_curriculum_package.py — never a hand-written package here.
_asset = ANDROID / "app-wiring/src/main/assets/curriculum_package.txt"
check("E11D-13_no_asset_ships",
      (not _asset.exists()) or _asset.read_text(encoding="utf-8").splitlines()[1].startswith("# Generated by tools/build_curriculum_package.py"),
      "content was authored here; 15 owns authoring")

# ---------------------------------------------------------------- tests exist
for name in ["a validated, deterministic, well-attributed item fits a mastery measurement",
             "the Objective decides what counts as evidence for it, not the item",
             "an unvalidated item is practice at best, whatever it declares about itself",
             "a body too large is refused rather than truncated"]:
    check(f"E11D-14_t1_model_{name[:40]}", f"`{name}`" in facts_test, f"missing test: {name}")
for name in ["submission freezes, and a frozen boundary can never be revisited or resubmitted",
             "skipping is not incorrect and leaves the need unresolved",
             "revealing help converts the mode explicitly and raises a recheck it never schedules",
             "a state change is claimed only when canonical state actually changed",
             "not reliably measured is first class and is never folded into incorrect",
             "the session view can hold no score, grade, percentage or countdown"]:
    check(f"E11D-14_t1_session_{name[:40]}", f"`{name}`" in session_test, f"missing test: {name}")
for name in ["the store's validation record decides trust, not the item's claim about itself",
             "an unusable item records no exposure, because the learner never saw it",
             "ingestion publishes the authored package once and never overwrites it"]:
    check(f"E11D-14_t1_app_{name[:40]}", f"`{name}`" in app_test, f"missing test: {name}")
for name in ["a published version is never overwritten",
             "a package with an unresolved reference is refused whole and writes nothing",
             "published curriculum cannot be updated or deleted at the storage layer",
             "publishing writes nothing into the user regions"]:
    check(f"E11D-14_t2_{name[:40]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")
for name in ["anything the format does not define refuses the whole package",
             "a package that does not parse serves nothing at all, and says why"]:
    check(f"E11D-14_t5_{name[:40]}", f"`{name}`" in format_test, f"missing T5 test: {name}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E11D-15_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "a device result is claimed")
mutation = contract["mutation_results"]
check("E11D-15_mutation_all_detected",
      mutation["detected"] == mutation["total"] == len(mutation["mutants"]) >= 20,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E11D-15_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True,
      "the mutation harness was not verified to have run anything")
check("E11D-15_11c_corrected", contract.get("corrections", {}).get("11c_mutation_record") is not None,
      "11C's invalidated mutation record is not corrected here")
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E11D-15_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E11D-15_not_verified_named", len(contract.get("not_verified", [])) >= 3, "what was not verified must be named")
boundaries = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("12", "13", "14", "15", "16B", "18D"):
    check(f"E11D-15_boundary_{owner}", owner in boundaries, f"missing {owner}")
forbidden = set(contract.get("forbidden_daily_micro_patterns", []))
for pattern in ("item_promoting_itself_past_its_validation", "item_declaring_its_own_evidence_fit",
                "exposure_recorded_for_an_item_never_shown", "published_curriculum_version_overwritten",
                "artifact_body_truncated_to_fit", "submitted_boundary_edited_or_resubmitted",
                "skipping_treated_as_incorrect", "result_with_a_score_grade_percentage_or_threshold",
                "state_change_claimed_without_canonical_change"):
    check(f"E11D-16_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 11D QA PASS", "**Decision:** `D-090`",
                 "practice_only", "data:", "T6", "11C"]:
    check(f"E11D-17_spec_{fragment[:24]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["No web research pass was needed", "ASUX-v0", "mutation harness"]:
    check(f"E11D-18_research_{fragment[:24]}", fragment in research_text, f"missing={fragment!r}")

# 11C's POST sync inserted the same PROJECT_CONTEXT section four times, because its replacement
# text contained its own anchor. A living document must not repeat a numbered section heading.
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E11D-19_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
for living in ("AGENTS.md", "PROJECT_CONTEXT.md", "docs/START_HERE.md", "docs/STEP_STATUS.md",
               "docs/HANDOFF_STATE.md", "docs/MASTER_PLAN.md"):
    lines = read(ROOT / living).splitlines()
    repeated = [n + 1 for n in range(1, len(lines)) if len(lines[n].strip()) > 25 and lines[n] == lines[n - 1]]
    check(f"E11D-19_no_adjacent_duplicate_{living.split('/')[-1]}", not repeated, f"lines={repeated}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "DMAX-v0", "stage_step": "11D", "decision": "D-090",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"11D_DAILY_MICRO_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
