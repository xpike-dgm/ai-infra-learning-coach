from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_required(rel: str, old: str, new: str) -> None:
    text = read(rel)
    if new in text and old not in text:
        return
    if old not in text:
        raise RuntimeError(f"{rel}: required replacement source missing: {old[:120]!r}")
    write(rel, text.replace(old, new))


def sub_required(rel: str, pattern: str, repl: str, flags: int = 0) -> None:
    text = read(rel)
    new, count = re.subn(pattern, repl, text, flags=flags)
    if count == 0:
        if repl in text:
            return
        raise RuntimeError(f"{rel}: required regex source missing: {pattern}")
    write(rel, new)


def append_once(rel: str, marker: str, block: str) -> None:
    text = read(rel)
    if marker in text:
        return
    if not text.endswith("\n"):
        text += "\n"
    text += "\n" + block.strip() + "\n"
    write(rel, text)


# 1) Finalize candidate spec + policy.
replace_required(
    "docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md",
    "**Durum:** CANDIDATE — QA + POST-STEP kapanışı bekliyor",
    "**Durum:** TAMAMLANDI",
)
replace_required(
    "docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md",
    "**Candidate model:** `TEIP-v0 — Technical English Integration Policy`",
    "**Final model:** `TEIP-v0 — Technical English Integration Policy`",
)
replace_required(
    "docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md",
    "**Candidate decision:** `D-066`",
    "**Final decision:** `D-066`",
)
replace_required(
    "docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md",
    "`TEIP-v0 / D-066` is accepted only after independent deterministic QA + D-050 POST.",
    "`TEIP-v0 / D-066` was accepted after independent deterministic QA + D-050 POST.",
)

policy_path = ROOT / "curriculum/english/7d_technical_integration/policy.yaml"
policy = yaml.safe_load(policy_path.read_text(encoding="utf-8"))
policy["status"] = "accepted_7d"
policy.pop("candidate_decision", None)
policy["decision"] = "D-066"
policy_path.write_text(yaml.safe_dump(policy, sort_keys=False, allow_unicode=True, width=160), encoding="utf-8")

# 2) Durable decision + chronological progress.
append_once(
    "docs/DECISIONS.md",
    "## D-066 — Technical English Integration Policy = TEIP-v0",
    """
## D-066 — Technical English Integration Policy = TEIP-v0
**Durum:** Kabul edildi — 2026-08-27

- 7D final modeli `TEIP-v0 — Technical English Integration Policy` oldu.
- Canonical spec `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`; machine-readable policy `curriculum/english/7d_technical_integration/policy.yaml`; research basis `research/7d_technical_english_integration_research.md`.
- Task/resource/prompt language target construct ile aynı şey değildir; technical ve English construct'leri ayrı attribution taşır.
- Exactly four integration mode kabul edildi: `technical_only_localized`, `technical_with_english_exposure`, `dual_target_integrated`, `english_primary_technical_context`.
- Technical-only task'lerde non-target English Turkish/bilingual/gloss support ile neutralize edilebilir; English global hard gate veya English mastery attribution oluşmaz.
- Authentic English exposure tek başına English mastery değildir; construct-essential language ya ready olmalı ya construct-valid support ile neutralize edilmelidir.
- Dual-target task her iki target/prerequisite family'yi explicit taşır ve component-level rubric/evidence ister; global task PASS/FAIL component'lere broadcast edilemez.
- English-primary technical-context task'ta specialist technical ignorance English failure'a dönüşemez; context ready/controlled/scaffolded olmalıdır.
- 7 contamination reason-code ve bidirectional contamination guard kabul edildi: unknown English technical negative evidence'ı, hidden specialist technical context English negative evidence'ı geçersiz kılar.
- Scaffold evidence + task validity driven ve reversible'dır; fixed Turkish/English ratio, fixed fading day count veya fixed integration quota yoktur.
- One integrated task multiple LearningNeed'e hizmet edebilir; duration bir kez sayılır, priority track sayısıyla çarpılmaz, completion bütün need/evidence'ı otomatik kapatmaz.
- Authentic/translated/glossed/AI-generated support QAB/AIV/prerequisite/freshness ve semantic-integrity guard'larını aşamaz.
- D01 canonical 15 Skill identity'si ve Stage 6 graph değişmedi; 7D yeni Skill/Objective/prerequisite edge üretmedi.
- Independent 7D QA: 49/49 check PASS; 15 canonical English Skill / 4 mode / 15 safety fixture / 7 contamination code.
- Sonraki numbered step `7E — English mastery`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.
""",
)
append_once(
    "docs/PROGRESS_LOG.md",
    "## 2026-08-27 — 7D Technical English integration tamamlandı — TEIP-v0 / D-066",
    """
## 2026-08-27 — 7D Technical English integration tamamlandı — TEIP-v0 / D-066

- Kullanıcı açık onayı sonrası fresh PRE ile 7C accepted / 7D active-not-executed state doğrulandı.
- Council of Europe CEFR plurilingual/mediation guidance, ALTE language-for-specific-purposes testing guidance ve ETS construct-irrelevant language-demand guidance Research girdisi olarak incelendi; dış kaynaklardan fixed ratio/threshold türetilmedi.
- Technical-only, technical-with-English-exposure, dual-target-integrated ve English-primary-technical-context olmak üzere dört construct-aware mode tanımlandı.
- Technical ve English targets/prerequisites/rubrics/evidence ayrı tutuldu; global integrated PASS broadcast yasaklandı.
- English-caused technical failure ve specialist-technical-context-caused English failure için bidirectional contamination guard tanımlandı.
- Scaffold evidence/task-validity driven, reversible ve no-fixed-ratio/no-fixed-day olarak kilitlendi.
- Authentic docs/terminal/errors/man/API/CUDA-style source kullanımı prerequisite + QAB/AIV/freshness/integrity guard'larına bağlandı.
- İlk QA turunda yalnız YAML future-stage key typing uyuşmazlığı bulundu; semantic policy değişmeden string-key normalization yapıldı.
- İkinci core QA: Stage 6 + 7B + 7C regressions PASS; 7D 49/49 check PASS.
- D-050 POST living-memory + external-memory + repo-wide stale audit ile 7D kapatıldı; 7E active-not-executed yapıldı.

**Sonraki kesin adım:** `7E — English mastery`. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""",
)

# 3) EXECUTION_INDEX stage map + current tail.
replace_required(
    "docs/EXECUTION_INDEX.md",
    "- [x] **7C — Günlük English bileşeni** — `DECP-v0 / D-065`\n- [ ] **7D — Teknik entegrasyon** **AKTİF**\n- [ ] **7E — English mastery**",
    "- [x] **7C — Günlük English bileşeni** — `DECP-v0 / D-065`\n- [x] **7D — Teknik entegrasyon** — `TEIP-v0 / D-066`\n- [ ] **7E — English mastery** **AKTİF**",
)
idx_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7D`  
**Son tamamlanan:** **`7D — TEIP-v0 / D-066`**  
**Aktif:** **`7E — English mastery`** — active-not-executed

7D final: **15 canonical English Skill / 4 construct-aware integration mode / 15 safety fixture / 7 contamination reason-code**. English/CEFR global technical gate yok; dual-target evidence component-level attribution ile çalışır.

7E başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.
"""
sub_required("docs/EXECUTION_INDEX.md", r"# Güncel Konum\n.*\Z", idx_tail, flags=re.S)

# 4) STEP_STATUS table + current tail.
replace_required(
    "docs/STEP_STATUS.md",
    "| **7D — Teknik entegrasyon** | 🟡 Aktif | English↔technical curriculum integration/scaffold behavior; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7E–20** | ⬜ Bekliyor | 7D sonrası canonical sırada. |",
    "| **7D — Teknik entegrasyon** | ✅ | TEIP-v0 / D-066. 4 construct-aware integration mode + component attribution + bidirectional contamination/scaffold guards; QA PASS. |\n| **7E — English mastery** | 🟡 Aktif | Learner-facing English mastery/profile behavior; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **8–20** | ⬜ Bekliyor | 7E sonrası canonical sırada. |",
)
step_tail = """## Son tamamlanan numaralı adım — 7D

**Final:** `TEIP-v0 — Technical English Integration Policy` / D-066.  
**Ana çıktı:** `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md` + `curriculum/english/7d_technical_integration/`.

7D sonucu:
- exact D01 15 canonical Skill unchanged,
- 4 integration mode: technical-only localized / technical+English exposure / dual-target / English-primary technical-context,
- technical/English target + prerequisite + evidence attribution ayrı,
- English/CEFR global technical gate yok,
- 7 bidirectional contamination reason-code,
- fixed Turkish/English ratio veya fixed scaffold-fading schedule yok,
- scaffold evidence/task-validity driven ve reversible,
- authentic/translated/AI support QAB/AIV/prerequisite/freshness guard'larına bağlı,
- 15/15 safety fixture ve 49/49 independent validator check PASS,
- Stage 6 + accepted 7B + accepted 7C regressions PASS.

## Aktif adım — 7E English mastery

**7E henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
sub_required("docs/STEP_STATUS.md", r"## Son tamamlanan numaralı adım — 7C\n.*\Z", step_tail, flags=re.S)

# 5) MASTER_PLAN Stage 7 + current tail.
replace_required(
    "docs/MASTER_PLAN.md",
    "### [x] 7C — Günlük English bileşeni — DECP-v0 / D-065\n**Final:** `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` + `curriculum/english/7c_daily_component/`",
    "### [x] 7C — Günlük English bileşeni — DECP-v0 / D-065\n**Final:** `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` + `curriculum/english/7c_daily_component/`",
)
# Replace compact active lines regardless of surrounding 7C detail.
text = read("docs/MASTER_PLAN.md")
text = text.replace("### [ ] 7D — Teknik entegrasyon — **AKTİF**\n### [ ] 7E — English mastery", "### [x] 7D — Teknik entegrasyon — TEIP-v0 / D-066\n**Final:** `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md` + `curriculum/english/7d_technical_integration/`\n\n- exact D01 15 Skill identity unchanged; no graph mutation,\n- 4 construct-aware integration mode,\n- technical/English target-prerequisite-attribution separation,\n- bidirectional construct-contamination guards,\n- evidence-driven reversible scaffold; no fixed ratio/day quota,\n- authentic resource + translation/gloss integrity guard,\n- 15 safety fixture / 49 validator checks PASS.\n\n### [ ] 7E — English mastery — **AKTİF**")
if "### [x] 7D — Teknik entegrasyon — TEIP-v0 / D-066" not in text:
    raise RuntimeError("MASTER_PLAN Stage 7 active lines not found")
write("docs/MASTER_PLAN.md", text)
plan_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7D`  
**Son tamamlanan:** **`7D — TEIP-v0 / D-066`**  
**Aktif:** **`7E — English mastery`** — henüz yürütülmedi.

Bir sonraki yürütme: **7E fresh PRE-STEP → EED-v0 + TECP-v0 + DECP-v0 + TEIP-v0 + GRE/RVR/WLRM contracts ile learner-facing English mastery/profile behavior → independent QA → D-050 POST sync + stale audit.**

- D-063: 7A final `EED-v0`.
- D-064: 7B final `TECP-v0`.
- D-065: 7C final `DECP-v0`.
- D-066: 7D final `TEIP-v0`; 4 integration mode, component attribution, contamination/scaffold safety.
"""
sub_required("docs/MASTER_PLAN.md", r"# Güncel Konum\n.*\Z", plan_tail, flags=re.S)

# 6) HANDOFF_STATE: replace 7D handoff with final 7D + 7E handoff and current-state clauses.
handoff = read("docs/HANDOFF_STATE.md")
handoff = handoff.replace(
    "## 17. 7D handoff\n\n7D — Teknik entegrasyon; EED-v0 + TECP-v0 + DECP-v0 ile English Foundation Rules / technical prerequisite / assessment evidence contracts'ını birleştirerek Python/C/Linux/GPU vb. teknik task'lerde English/bilingual scaffold'ın ne zaman ve nasıl kullanılacağını tasarlayacaktır. English global hard gate olmayacaktır. 7D fresh PRE + kullanıcı açık onayı olmadan yürütülmez.",
    """## 17. D-066 / 7D final özeti

Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.  
Policy/QA: `curriculum/english/7d_technical_integration/`.  
Research: `research/7d_technical_english_integration_research.md`.

TEIP-v0:
- 15 canonical English Skill identity unchanged,
- exactly 4 construct-aware integration mode,
- technical/English targets + prerequisites + evidence separate,
- English/CEFR global technical gate forbidden,
- dual-target overall PASS broadcast forbidden,
- bidirectional language/technical-context contamination guards,
- evidence/task-validity driven reversible scaffold; no fixed ratio/day quota,
- authentic resources + translation/gloss/AI support validity guards,
- 15 safety fixture + independent 49-check QA PASS.

## 18. 7E handoff

7E — English mastery; learner-facing granular English mastery/profile/CEFR summary behavior, B2+ evidence presentation ve English-specific mastery/remediation display semantics'ini EED/TECP/DECP/TEIP + GRE/RVR/WLRM contracts üzerinde kesinleştirecektir. 7E fresh PRE + kullanıcı açık onayı olmadan yürütülmez."""
)
# Current exact state section if present.
handoff = re.sub(
    r"\*\*Son tamamlanan:\*\* `7C — DECP-v0 / D-065`\s+\*\*Aktif:\*\* `7D — Teknik entegrasyon`\s+\*\*7D henüz yürütülmedi\.[^\n]*\*\*",
    "**Son tamamlanan:** `7D — TEIP-v0 / D-066`  \n**Aktif:** `7E — English mastery`  \n**7E henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    handoff,
    flags=re.I,
)
# Generic stage summaries.
handoff = handoff.replace("- AŞAMA 7C ✅ — DECP-v0 / D-065\n- AŞAMA 7D 🟡 active-not-executed", "- AŞAMA 7C ✅ — DECP-v0 / D-065\n- AŞAMA 7D ✅ — TEIP-v0 / D-066\n- AŞAMA 7E 🟡 active-not-executed")
handoff = handoff.replace("- AŞAMA 7D 🟡 active-not-executed", "- AŞAMA 7D ✅ — TEIP-v0 / D-066\n- AŞAMA 7E 🟡 active-not-executed")
write("docs/HANDOFF_STATE.md", handoff)

# 7) PROJECT_CONTEXT: add 7D canonical summary and advance current state.
ctx = read("PROJECT_CONTEXT.md")
marker = "### 8.4 7D Technical English Integration — TEIP-v0 / D-066"
if marker not in ctx:
    anchor = "Canonical: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.\n\n\n## 9. Professional-readiness depth"
    block = """Canonical: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.

### 8.4 7D Technical English Integration — TEIP-v0 / D-066

Technical task'in dili target construct ile eşit sayılmaz. TEIP-v0 dört mode tanımlar: technical-only localized, technical-with-English-exposure, dual-target integrated ve English-primary technical-context. English/CEFR global technical gate değildir; technical ve English target/prerequisite/evidence attribution ayrı tutulur. Hidden English technical negative evidence'ı, hidden specialist technical context English negative evidence'ı contaminate eder. Scaffold evidence/task-validity driven ve reversible'dır; fixed Turkish/English ratio/fading day yoktur.

Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.


## 9. Professional-readiness depth"""
    if anchor not in ctx:
        raise RuntimeError("PROJECT_CONTEXT 7D insertion anchor missing")
    ctx = ctx.replace(anchor, block, 1)
ctx = ctx.replace("- **7D 🟡 Teknik entegrasyon — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 7E–20 ⬜", "- **7D ✅ TEIP-v0 / D-066 — Teknik entegrasyon tamamlandı**\n- **7E 🟡 English mastery — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 8–20 ⬜")
ctx = ctx.replace("**Sıradaki numaralı çalışma 7D'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.", "**Sıradaki numaralı çalışma 7E'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.")
write("PROJECT_CONTEXT.md", ctx)

# 8) START_HERE current stage map / core / current command.
start = read("docs/START_HERE.md")
start = start.replace("- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B ✅ TECP-v0 / D-064; 7C ✅ DECP-v0 / D-065; 7D 🟡 active-not-executed**", "- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B ✅ TECP-v0 / D-064; 7C ✅ DECP-v0 / D-065; 7D ✅ TEIP-v0 / D-066; 7E 🟡 active-not-executed**")
start = start.replace("- AŞAMA 7: **7A ✅ EED-v0 / D-063**, **7B ✅ TECP-v0 / D-064**, **7C ✅ DECP-v0 / D-065**, **7D 🟡 active-not-executed**", "- AŞAMA 7: **7A ✅ EED-v0 / D-063**, **7B ✅ TECP-v0 / D-064**, **7C ✅ DECP-v0 / D-065**, **7D ✅ TEIP-v0 / D-066**, **7E 🟡 active-not-executed**")
start = start.replace("7C final: Technical English common capacity içinde daily candidate opportunity olarak çalışır; fixed minute/percentage/completion/streak/debt yoktur; PBR balance/starvation ve state-driven task mix kullanılır.", "7D final: Technical English integration 4 construct-aware mode kullanır; English/CEFR global technical gate değildir, dual-target evidence component-level attribution ile çalışır ve scaffold evidence/task-validity driven'dır.")
start = re.sub(r"\*\*Son tamamlanan:\*\* \*\*`7C — DECP-v0 / D-065`\*\*\s+\*\*Aktif:\*\* \*\*`7D — Teknik entegrasyon`\*\*\s+\*\*7D henüz yürütülmedi\.[^\n]*\*\*", "**Son tamamlanan:** **`7D — TEIP-v0 / D-066`**  \n**Aktif:** **`7E — English mastery`**  \n**7E henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**", start)
start = re.sub(r"> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS\.md \+ SESSION_START \+ START_HERE \+ PROJECT_MEMORY_PROTOCOL ile başla\..*?`\s*\Z", "> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. 7D, TEIP-v0 / D-066 ile tamamlandı: 4 construct-aware integration mode, component attribution, bidirectional contamination guard, evidence-driven reversible scaffold; English/CEFR global technical gate değildir. Aktif step 7E — English mastery; 7E henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`", start, flags=re.S)
write("docs/START_HERE.md", start)

# 9) AGENTS current state.
ag = read("AGENTS.md")
ag = ag.replace("- 7C: ✅ `DECP-v0 / D-065` tamamlandı — common-capacity daily candidate opportunity; no fixed minute/percentage/streak/debt; PBR balance/starvation + state-driven task mix.\n- **Aktif adım: 7D — Teknik entegrasyon.**\n- **7D henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.", "- 7C: ✅ `DECP-v0 / D-065` tamamlandı — common-capacity daily candidate opportunity; no fixed minute/percentage/streak/debt; PBR balance/starvation + state-driven task mix.\n- 7D: ✅ `TEIP-v0 / D-066` tamamlandı — 4 construct-aware integration mode; component attribution; bidirectional contamination + reversible scaffold guards.\n- **Aktif adım: 7E — English mastery.**\n- **7E henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.")
ag = ag.replace("**7D'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7D için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.", "**7E'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7E için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.")
write("AGENTS.md", ag)

# 10) Vault current context + open loop.
vc = read("vault/agent/CURRENT_CONTEXT.md")
if "`TEIP-v0 / D-066`" not in vc:
    vc = vc.replace("- `DECP-v0 / D-065`: Daily Technical English common capacity içinde candidate opportunity; fixed quota/streak/debt yok; PBR balance/starvation + state-driven task mix.", "- `DECP-v0 / D-065`: Daily Technical English common capacity içinde candidate opportunity; fixed quota/streak/debt yok; PBR balance/starvation + state-driven task mix.\n- `TEIP-v0 / D-066`: Technical English integration; 4 construct-aware mode, component attribution, bidirectional contamination guard, evidence-driven reversible scaffold; no global English/CEFR technical gate.")
vc = re.sub(r"AŞAMA 6 ve 7A–7C tamamlandı\..*?7D başlamadan fresh PRE-STEP \+ kullanıcı açık onayı zorunludur\.", "AŞAMA 6 ve 7A–7D tamamlandı. Son tamamlanan adım **7D — TEIP-v0 / D-066**. 7D technical/English construct'lerini component-level attribution ile ayırır; English/CEFR global technical gate değildir ve scaffold evidence/task-validity driven'dır. Aktif adım **7E — English mastery**; henüz yürütülmedi. 7E başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.", vc, flags=re.S)
write("vault/agent/CURRENT_CONTEXT.md", vc)

loops = read("vault/agent/OPEN_LOOPS.md")
loops = loops.replace("- [ ] 7D Technical integration **AKTİF**: English↔technical construct/scaffold/attribution behavior.", "- [x] 7D Technical integration: TEIP-v0 / D-066 ile tamamlandı; 4 integration mode + component attribution + contamination/scaffold guards.\n- [ ] 7E English mastery **AKTİF**: learner-facing granular English mastery/profile/CEFR summary behavior.")
# handle older wording if present
loops = loops.replace("- [ ] 7D Technical integration **AKTİF**", "- [x] 7D Technical integration: TEIP-v0 / D-066 ile tamamlandı\n- [ ] 7E English mastery **AKTİF**")
write("vault/agent/OPEN_LOOPS.md", loops)

# 11) LOCAL_MANAGER_HANDOFF current summary and addendum.
lm = read("docs/LOCAL_MANAGER_HANDOFF.md")
# Common current summary forms after 7C.
lm = lm.replace("- AŞAMA 7C ✅ DECP-v0 / D-065\n- AŞAMA 7D 🟡 ACTIVE — NOT EXECUTED", "- AŞAMA 7C ✅ DECP-v0 / D-065\n- AŞAMA 7D ✅ TEIP-v0 / D-066\n- AŞAMA 7E 🟡 ACTIVE — NOT EXECUTED")
lm = lm.replace("AŞAMA 7D 🟡 ACTIVE — NOT EXECUTED", "AŞAMA 7D ✅ TEIP-v0 / D-066\nAŞAMA 7E 🟡 ACTIVE — NOT EXECUTED")
# If current exact section exists from prior finalizer.
lm = re.sub(r"\*\*Son tamamlanan numaralı adım:\*\* `7C — Günlük English bileşeni`.*?\*\*Durum:\*\* \*\*HENÜZ YÜRÜTÜLMEDİ\*\*", "**Son tamamlanan numaralı adım:** `7D — Teknik entegrasyon`  \n**Final:** `TEIP-v0 — Technical English Integration Policy` / D-066  \n**Canonical:** `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md` + `curriculum/english/7d_technical_integration/`\n\n**Aktif adım:** `7E — English mastery`  \n**Durum:** **HENÜZ YÜRÜTÜLMEDİ**", lm, flags=re.S)
append_once(
    "docs/LOCAL_MANAGER_HANDOFF.md",
    "## 7D completion addendum — D-066",
    """
## 7D completion addendum — D-066

7D `TEIP-v0 — Technical English Integration Policy` ile tamamlandı. Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`; policy/QA: `curriculum/english/7d_technical_integration/`. Exact 15 D01 Skill korunur; 4 construct-aware mode, technical/English component attribution, bidirectional contamination guard, evidence-driven reversible scaffold ve authentic-resource integrity semantics kabul edildi. English/CEFR global technical gate değildir. Independent QA 49/49 PASS. Current active numbered step 7E'dir; fresh PRE + kullanıcı açık onayı gerekir.
""",
)
write("docs/LOCAL_MANAGER_HANDOFF.md", lm)

print("7D_POST_FINALIZER=PASS")
