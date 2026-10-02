"""Independent 14G QA — PRVX-v0 Provider Adapter.

The rules are read from `AIAX-v0` §5–§11 and `TVSX-v0` §9 and compared with the real Kotlin, the manifests and the build
script — never with 14G's own contract alone. The rules the step exists for must be **unrepresentable** as a PASS here:
a key in the APK or in plain storage or a log, a call without the learner's key, network in the no-AI build, the
provider keeping the call, anything but core's instructions/message/schema sent, a refusal read as an answer, free text
read as a verdict, a retry after a refusal/invalid reply/rejected key/timeout, a background retry, a model name in core,
and a check that calls a live provider.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/14g_provider_adapter/provider_adapter.yaml"
SPEC = ROOT / "docs/PROVIDER_ADAPTER_IMPL_SPEC.md"
RESEARCH = ROOT / "research/14g_provider_adapter_research.md"
QA_OUT = ROOT / "arch/14g_provider_adapter/qa_report.yaml"
AIAX = ROOT / "docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md"
TVSX = ROOT / "docs/TEST_STRATEGY_SPEC.md"
DECISIONS = ROOT / "docs/DECISIONS.md"

AI = ANDROID / "ai-adapter/src/main/kotlin/coach/ai"
AI_TEST = ANDROID / "ai-adapter/src/test/kotlin/coach/ai"
CLIENT_KT, PROVIDER_KT, JSON_KT = AI / "OpenAiResponses.kt", AI / "Provider.kt", AI / "Json.kt"
TUTOR_KT, EVAL_KT, CHECK_KT = AI / "AiTutor.kt", AI / "AiEvaluator.kt", AI / "ConnectionCheck.kt"
CODEI_KT = ANDROID / "core-model/src/main/kotlin/coach/CodeEvaluationInstructions.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/AiSettingsPresentation.kt"
UI_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/AiSettingsScreen.kt"
WIRING = ANDROID / "app-wiring"
AIWIRING_KT = WIRING / "src/withAi/kotlin/coach/wiring/AiWiring.kt"
NOAI_KT = WIRING / "src/withoutAi/kotlin/coach/wiring/AiWiring.kt"
APPLICATION_KT = WIRING / "src/main/kotlin/coach/wiring/CoachApplication.kt"
MAIN_MANIFEST = WIRING / "src/main/AndroidManifest.xml"
AI_MANIFEST = WIRING / "src/withAi/AndroidManifest.xml"
WIRING_GRADLE = WIRING / "build.gradle.kts"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"

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


def load(path: Path):
    try:
        return yaml.safe_load(read(path)) or {}
    except yaml.YAMLError:
        return {}


def between(source: str, start: str, end: str) -> str:
    a = source.find(start)
    if a < 0:
        return ""
    b = source.find(end, a + len(start))
    return source[a:b if b >= 0 else len(source)]


for path in (CONTRACT, SPEC, RESEARCH, CLIENT_KT, PROVIDER_KT, JSON_KT, TUTOR_KT, EVAL_KT, CHECK_KT, CODEI_KT, PRES_KT, UI_KT, AIWIRING_KT, NOAI_KT, AI_MANIFEST):
    check(f"E14G-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = load(CONTRACT)
client, provider, json_src = strip_comments(read(CLIENT_KT)), strip_comments(read(PROVIDER_KT)), strip_comments(read(JSON_KT))
tutor, evaluator, conn = strip_comments(read(TUTOR_KT)), strip_comments(read(EVAL_KT)), strip_comments(read(CHECK_KT))
codei, pres, ui = strip_comments(read(CODEI_KT)), strip_comments(read(PRES_KT)), strip_comments(read(UI_KT))
aiwiring, noai, application = strip_comments(read(AIWIRING_KT)), strip_comments(read(NOAI_KT)), strip_comments(read(APPLICATION_KT))
main_manifest, ai_manifest, gradle = read(MAIN_MANIFEST), read(AI_MANIFEST), read(WIRING_GRADLE)
aiax, tvsx, spec_text, research_text = read(AIAX), read(TVSX), read(SPEC), read(RESEARCH)
adapter_all = "\n".join(strip_comments(read(p)) for p in sorted(AI.glob("*.kt")))
core_all = "\n".join(strip_comments(read(p)) for d in ("core-model", "core-ports", "core-engines", "core-application", "core-presentation")
                     for p in sorted((ANDROID / d / "src/main").rglob("*.kt")))

# ---------------------------------------------------------------- contract head and the accepted texts it rests on
check("E14G-01_model", contract.get("model") == "PRVX-v0", str(contract.get("model")))
check("E14G-01_status", contract.get("status") == "accepted_14g", str(contract.get("status")))
check("E14G-01_decision", contract.get("decision") == "D-111", str(contract.get("decision")))
scope = contract.get("scope", {})
for key, expected in {"provider_client": True, "router_by_task_class": True, "key_on_device_encrypted": True, "settings_screen": True,
                      "connection_check": True, "code_evaluation_instructions_in_core": True, "schema_changed": False, "core_names_a_model": False,
                      "developer_key_shipped": False, "backend_proxy": False, "second_provider_or_model": False, "background_retry": False,
                      "live_call_in_any_check": False, "live_call_run_by_claude": False}.items():
    check(f"E14G-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E14G-01_user_decisions", contract.get("user_decisions") == {"date": "2026-10-02", "provider": "openai",
      "fallback": "single_provider_uniform_degradation", "key_entry": "small_settings_screen_in_14g"}, str(contract.get("user_decisions")))
for phrase, cid in (("No developer or shared API key ships in the APK", "no_dev_key"), ("No backend proxy exists in V1", "no_proxy"),
                    ("the key never appears in logs, crash reports, exports, backups or diagnostics", "key_never_logged"),
                    ("the **model name lives in configuration**, never in the core", "model_in_config"),
                    ("the adapter declares an **end-to-end budget** covering all attempts", "budget"),
                    ("retries are bounded and never unbounded or unthrottled", "bounded"),
                    ("The adapter must therefore inspect the stop reason before reading content", "stop_reason"),
                    ("with AI disabled, nothing leaves the device", "nothing_leaves"),
                    ("removing the key returns the app to the null-evaluator path with no data loss", "remove_safe")):
    check(f"E14G-01_aiax_{cid}", phrase in aiax, f"AIAX-v0 moved: {phrase[:50]}")
check("E14G-01_tvsx_no_live", "**No check calls a live AI provider.**" in tvsx, "TVSX-v0 §9 moved")

# ---------------------------------------------------------------- the reference is recorded with the build
cfg = between(provider, "data class ProviderConfig(", ") {")
check("E14G-02_reference_recorded", 'val referenceCheckedOn: String = "2026-10-02"' in cfg and "developers.openai.com/api/reference/resources/responses/methods/create" in cfg
      and contract.get("provider", {}).get("reference_checked_on") == "2026-10-02", "AIAX-v0 §8.2")
check("E14G-02_research_pass", "2026-10-02" in research_text and "Nothing was sent to the provider; no account or key was used." in research_text, "")
check("E14G-02_endpoint", 'val endpoint: String = "https://api.openai.com/v1/responses"' in cfg and 'require(endpoint.startsWith("https://"))' in provider, "")
check("E14G-02_router", re.findall(r'^\s+([A-Z_]+)\("([a-z_]+)"\)', between(provider, "enum class TaskClass", "}"), re.M) ==
      [("TUTOR_HELP", "tutor_help"), ("OPEN_RESPONSE_EVALUATION", "open_response_evaluation"), ("CODE_EVALUATION", "code_evaluation")]
      and contract.get("router", {}).get("task_classes") == ["tutor_help", "open_response_evaluation", "code_evaluation"], "")
check("E14G-02_model_in_config", 'TaskClass.entries.associateWith { "gpt-6-astra" }' in cfg and contract.get("router", {}).get("default_model") == "gpt-6-astra"
      and "require(TaskClass.entries.all { !models[it].isNullOrBlank() })" in provider, "")
check("E14G-02_no_model_in_core", not re.search(r"gpt-|openai|api\.openai|claude-|anthropic", core_all, re.I), "core never names a provider or model")
check("E14G-02_budget_defaults", "val budgetMillis: Long = 60_000" in cfg and "val maxAttempts: Int = 2" in cfg
      and contract.get("budget", {}).get("default_millis") == 60000 and contract.get("budget", {}).get("max_attempts") == 2, "")

# ---------------------------------------------------------------- the request
body = between(client, "private fun requestBody(", "private fun read(")
check("E14G-03_store_false", '"store" to Json.bool(false)' in body, "the provider keeps nothing")
check("E14G-03_strict_schema", '"type" to Json.str("json_schema")' in body and '"strict" to Json.bool(true)' in body and '"schema" to schemaJson' in body, "")
check("E14G-03_only_core_content", re.findall(r'"(\w+)" to ', body)[:5] == ["model", "instructions", "input", "text", "format"]
      and '"instructions" to Json.str(instructions)' in body and '"input" to Json.str(message)' in body, str(re.findall(r'"(\w+)" to ', body)))
call = between(client, "fun call(", "private fun requestBody(")
check("E14G-03_no_key_no_call", call.find("?: return ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE)") >= 0
      and call.find("?: return ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE)") < call.find("transport.post("), "")
check("E14G-03_key_in_header_only", '"Authorization" to "Bearer $key"' in call and "requestBody(model, instructions, schemaName, schema, message)" in call, "")
check("E14G-03_no_logging", not re.search(r"\bLog\.|println|printStackTrace|Logger|System\.out|System\.err", adapter_all), "nothing in the adapter logs")
check("E14G-03_no_library", not re.search(r"import (okhttp3|retrofit2|io\.ktor|kotlinx\.serialization|com\.google\.gson|com\.squareup|com\.openai)", adapter_all)
      and "implementation(" not in read(ANDROID / "ai-adapter/build.gradle.kts").replace('implementation(project(":core-model"))', "").replace('implementation(project(":core-ports"))', ""), "")
check("E14G-03_tutor_sends_core", 'TutorInstructions.TEXT, schemaName(TutorInstructions.REPLY_SCHEMA_ID)' in tutor and "TutorInstructions.REPLY_SCHEMA, TutorInstructions.userMessage(request)" in tutor, "")
check("E14G-03_open_sends_core", "OpenResponseInstructions.TEXT, schemaName(OpenResponseInstructions.REPLY_SCHEMA_ID)" in evaluator
      and "OpenResponseInstructions.userMessage(request.objectiveRefs, request.promptText, request.rubric, request.learnerResponse, request.misconceptionCatalog)" in evaluator, "")
check("E14G-03_code_sends_core", "CodeEvaluationInstructions.TEXT, schemaName(CodeEvaluationInstructions.REPLY_SCHEMA_ID)" in evaluator
      and "CodeEvaluationInstructions.userMessage(request.objectiveRefs, request.promptText, request.learnerResponse, request.misconceptionCatalog)" in evaluator, "")
check("E14G-03_check_sends_nothing_personal", 'const val MESSAGE = "ping"' in conn and "client.call(TaskClass.TUTOR_HELP, INSTRUCTIONS, SCHEMA_NAME, SCHEMA, MESSAGE)" in conn, "")

# ---------------------------------------------------------------- reading the reply, in the order AIAX-v0 §6 requires
reader = between(client, "private fun read(", "\n}\n") or client[client.find("private fun read("):]
order = [reader.find(s) for s in ('status == "incomplete"', 'if (status != "completed")', '== "refusal" }) return ProviderOutcome.NotDelivered(PendingReason.REFUSED)', '"output_text"', "Json.parse(text)")]
check("E14G-04_stop_reason_first", all(i >= 0 for i in order) and order == sorted(order), str(order))
check("E14G-04_content_filter_refused", 'if (reason == "content_filter") PendingReason.REFUSED else PendingReason.INVALID_RESPONSE' in reader, "")
check("E14G-04_exactly_one_text", "texts.singleOrNull()" in reader and "as? JsonValue.Obj" in reader, "")
check("E14G-04_tutor_exact_fields", 'json.fields.keys != setOf("intent", "text", "revealed_level", "instruction_mode")' in tutor, "")
check("E14G-04_open_exact_fields", 'json.fields.keys != setOf("findings", "misconception_hypotheses")' in evaluator and 'obj.fields.keys != setOf("criterion", "verdict")' in evaluator, "")
check("E14G-04_code_exact_fields", 'json.fields.keys != setOf("components", "misconception_hypotheses")' in evaluator and 'obj.fields.keys != setOf("objective", "signal")' in evaluator, "")
check("E14G-04_core_decides_open", "EvaluationResult.Provisional(emptyList(), ref(delivered.model, OpenResponseInstructions.REPLY_SCHEMA_ID), hypotheses, findings)" in evaluator, "")
check("E14G-04_never_verified", "EvaluationResult.Verified" not in evaluator, "an AI is never more than provisional")
check("E14G-04_no_guessing", "?.let { byId[it] } ?: return invalid()" in evaluator and "if (ids.any { it !in request.misconceptionCatalog }) return null" in evaluator, "")
check("E14G-04_routed", "if (request.rubric.isNotEmpty()) openResponse(client, request) else code(client, request)" in evaluator, "")
check("E14G-04_json_strict", all(f in json_src for f in ("duplicate key", "trailing text", "unescaped control character")), "")
check("E14G-04_no_free_text", not re.search(r"\.contains\(\"(met|correct|yes|pass)|Regex\(\"(met|correct)", adapter_all, re.I), "no verdict read out of free text")

# ---------------------------------------------------------------- failure mapping, budget, retries
check("E14G-05_key_rejected", "response.status == 401 || response.status == 403 -> return ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE, keyRejected = true)" in call, "")
check("E14G-05_retried_only", "response.status == 408 || response.status == 429 || response.status >= 500 -> {" in call
      and "response.status !in 200..299 -> return ProviderOutcome.NotDelivered(PendingReason.TRANSPORT_ERROR)" in call, "")
check("E14G-05_timeout_ends", "catch (timeout: SocketTimeoutException) {\n                return ProviderOutcome.NotDelivered(PendingReason.TIMED_OUT)" in call, "")
check("E14G-05_budget_end_to_end", "val remaining = config.budgetMillis - (monotonicMillis() - started)" in call and "transport.post(config.endpoint, headers, body, remaining)" in call
      and "if (remaining <= 0) return ProviderOutcome.NotDelivered(PendingReason.TIMED_OUT)" in call, "")
check("E14G-05_bounded", "while (attempt < config.maxAttempts)" in call and "require(budgetMillis > 0 && maxAttempts >= 1)" in provider, "")
check("E14G-05_no_background", not re.search(r"Thread\(|Executors|Timer|schedule|WorkManager|coroutine|launch\s*\{", adapter_all), "nothing retries in the background")
check("E14G-05_no_client_unavailable", "val client = client ?: return TutorReply.NotDelivered(PendingReason.UNAVAILABLE)" in tutor
      and "val client = client ?: return EvaluationResult.EvaluationPending(PendingReason.UNAVAILABLE)" in evaluator, "")
check("E14G-05_availability_needs_key", "client != null && !keys?.current().isNullOrBlank()" in evaluator, "")

# ---------------------------------------------------------------- the key and the builds
check("E14G-06_keystore", all(f in aiwiring for f in ('"AndroidKeyStore"', '"AES/GCM/NoPadding"', "KeyProperties.BLOCK_MODE_GCM", "setKeySize(256)")), "")
check("E14G-06_no_backup_dir", "KeystoreKeySource(context.noBackupFilesDir)" in aiwiring and 'android:allowBackup="false"' in main_manifest, "")
check("E14G-06_no_plain_storage", not re.search(r"SharedPreferences|getSharedPreferences|DataStore|writeText\(key|putString", aiwiring), "")
check("E14G-06_no_logging", not re.search(r"\bLog\.|println|printStackTrace", aiwiring + noai), "")
check("E14G-06_remove_deletes_both", "file.delete()" in aiwiring and "keyStore().deleteEntry(ALIAS)" in aiwiring, "")
check("E14G-06_prime_off_main", "primeAi()" in between(application, "private fun openApp()", "is StoreOpener.Result.Opened"), "presence is read on the store thread")
check("E14G-06_not_in_store", not re.search(r"(?i)api_key|apikey|ai_key", read(SCHEMA_KT)), "the key never enters the store")
check("E14G-06_no_key_in_repo", not re.search(r"sk-[A-Za-z0-9]{20,}", "\n".join(read(p) for p in ANDROID.rglob("*.kt") if "/build/" not in str(p).replace("\\", "/"))), "")
main_lines = [l for l in main_manifest.splitlines()]
ai_lines = [l for l in ai_manifest.splitlines()]
extra = [l.strip() for l in ai_lines if l not in main_lines]
check("E14G-06_internet_only_with_ai", "android.permission.INTERNET" not in main_manifest and extra[-1:] == ['<uses-permission android:name="android.permission.INTERNET" />']
      and all(l in ai_lines for l in main_lines), str(extra))
check("E14G-06_manifest_per_build", 'manifest.srcFile(if (withAiAdapter) "src/withAi/AndroidManifest.xml" else "src/main/AndroidManifest.xml")' in gradle, "")
check("E14G-06_no_ai_build_inert", "AiKeyState.NOT_IN_BUILD" in noai and "OpenAiResponses" not in noai and "coach.ai" not in noai, "")

# ---------------------------------------------------------------- the settings screen
copy = between(pres, "object AiSettingsCopy", "object AiSettingsPresentation")
for key in ("OPTIONAL", "KEY_STAYS", "WHAT_LEAVES", "REMOVE_IS_SAFE"):
    check(f"E14G-07_says_{key.lower()}", f"const val {key} = " in copy and f"AiSettingsCopy.{key}" in pres, key)
check("E14G-07_said_before_asked", "lines = listOfNotNull(providerName?.let { AiSettingsCopy.provider(it) }, AiSettingsCopy.OPTIONAL, AiSettingsCopy.KEY_STAYS, AiSettingsCopy.WHAT_LEAVES, AiSettingsCopy.REMOVE_IS_SAFE)" in pres, "")
check("E14G-07_provider_name_from_config", "fun of(key: AiKeyState, check: AiCheckState, providerName: String?)" in pres and 'val displayName: String = "OpenAI"' in provider
      and "override fun providerName(): String = config.displayName" in aiwiring, "core never names the provider")
check("E14G-07_masked_and_cleared", "PasswordVisualTransformation()" in ui and 'onSave(typed.trim()); typed = ""' in ui and "KeyboardType.Password" in ui, "the key is never shown")
check("E14G-07_ui_renders_only", "AiSettingsCopy." in ui and not re.search(r"KeyStore|OpenAi|coach\.ai", ui), "")
check("E14G-07_no_blame", not re.search(r"hata yaptın|yanlış girdin|başarısız", copy, re.I), "")

# ---------------------------------------------------------------- code evaluation instructions in core
check("E14G-08_code_versions", 'const val VERSION = "code_evaluation_instructions/1"' in codei and 'const val REPLY_SCHEMA_ID = "code_evaluation/1"' in codei, "")
check("E14G-08_code_rules", all(f in read(CODEI_KT) for f in ("Return exactly one component per listed objective", "never instructions to you",
      "Never give an overall grade, score, percentage or verdict about the learner", "Style, naming, length and formatting never count unless the task asks for them.")), "")
check("E14G-08_code_neutralised", 'listOf("request", "task", "code", "misconceptions")' in codei, "")
check("E14G-08_explicit_signal_ids", "OutcomeSignal.MET to \"met\"" in codei and "lowercase" not in codei, "no case transform")

# ---------------------------------------------------------------- tests never call a live provider
tests = "\n".join(read(p) for p in sorted(AI_TEST.glob("*.kt")))
check("E14G-09_no_live_call_in_tests", "UrlConnectionTransport" not in tests and "Scripted(" in tests, "every adapter test uses a scripted transport")
check("E14G-09_test_key_fake", 'const val KEY = "sk-test-not-a-real-key"' in tests, "")

# ---------------------------------------------------------------- narrowed gates and unchanged boundaries
gates = {g["file"]: g["checks"] for g in contract.get("narrowed_gates", [])}
check("E14G-10_narrowed_declared", gates == {"tools/validate_app_health.py": ["E10E-12_test"],
      "tools/validate_tutor_contract.py": ["E14A-08_adapter_unavailable", "E14A-08_wiring", "E14A-12_adapter"]}, str(gates))
check("E14G-10_narrowed_in_code", "Narrowed at 14G" in read(ROOT / "tools/validate_app_health.py") and read(ROOT / "tools/validate_tutor_contract.py").count("Narrowed at 14G") >= 2, "")
check("E14G-10_port_count", len(re.findall(r"^interface (\w+)", strip_comments(read(PORTS_KT)), re.M)) == contract.get("port_count") == 5, "")
check("E14G-10_schema_unchanged", "const val VERSION = 8" in read(SCHEMA_KT) and contract.get("schema_version") == 8, "")

# ---------------------------------------------------------------- verification claims
for name, suite in (contract.get("suites") or {}).items():
    check(f"E14G-11_suite_{name}", (ROOT / suite.get("file", "")).is_file(), suite.get("file", ""))
mr = contract.get("mutation_results", {})
ids = [m.get("id") for m in mr.get("mutants", [])]
check("E14G-11_mutation_all_detected", mr.get("total") == mr.get("detected") == len(ids) and len(set(ids)) == len(ids) and len(ids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E14G-11_compile_failure_not_detection", mr.get("compile_failure_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E14G-11_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation", {})
check("E14G-11_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs", [])
check("E14G-11_runs_pass", len(runs) == 7 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification", {})
check("E14G-11_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("live_call_run") is False, "")
check("E14G-11_forbidden", len(contract.get("forbidden_patterns", [])) == 10, str(contract.get("forbidden_patterns")))

# ---------------------------------------------------------------- spec, research, decision
check("E14G-12_spec_status", "**Status:** ACCEPTED — independent 14G QA PASS" in spec_text and "`D-111`" in spec_text, "")
check("E14G-12_spec_next", "**15A — Computer / Programming Fundamentals**" in spec_text and "**AŞAMA 14 is complete**" in spec_text, "")
check("E14G-12_spec_not_run", "**Not run: T6, and no live provider call.**" in spec_text, "")
check("E14G-12_decision", re.search(r"^#+ .*D-111", read(DECISIONS), re.M) is not None, "D-111 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {"model": "PRVX-v0", "stage_step": "14G", "decision": "D-111", "result": "PASS" if not failures else "FAIL",
          "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"14G QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
