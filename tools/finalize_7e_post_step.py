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
    if old not in text:
        if new in text:
            return
        raise RuntimeError(f"{rel}: required text missing: {old[:120]!r}")
    write(rel, text.replace(old, new))


def sub_required(rel: str, pattern: str, repl: str, flags: int = 0) -> None:
    text = read(rel)
    new_text, count = re.subn(pattern, repl, text, flags=flags)
    if count == 0:
        if repl in text:
            return
        raise RuntimeError(f"{rel}: required regex missing: {pattern}")
    write(rel, new_text)


def append_once(rel: str, marker: str, block: str) -> None:
    text = read(rel)
    if marker in text:
        return
    if not text.endswith("\n"):
        text += "\n"
    write(rel, text + "\n" + block.strip() + "\n")


# 1) Finalize 7E candidate spec and machine-readable policy.
replace_required(
    "docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md",
    "**Durum:** CANDIDATE / QA BEKLİYOR",
    "**Durum:** TAMAMLANDI",
)
replace_required(
    "docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md",
    "**Candidate model:** `TEPM-v0 — Technical English Mastery Profile`",
    "**Final model:** `TEPM-v0 — Technical English Mastery Profile`",
)
replace_required(
    "docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md",
    "**Candidate decision:** `D-067`",
    "**Final decision:** `D-067`",
)

policy_path = ROOT / "curriculum/english/7e_mastery_profile/policy.yaml"
policy = yaml.safe_load(policy_path.read_text(encoding="utf-8"))
policy["status"] = "accepted_7e"
policy.pop("candidate_decision", None)
policy["decision"] = "D-067"
policy_path.write_text(yaml.safe_dump(policy, sort_keys=False, allow_unicode=True, width=160), encoding="utf-8")

# 2) Durable decision and progress log.
append_once(
    "docs/DECISIONS.md",
    "## D-067 — Technical English Mastery Profile = TEPM-v0",
    """
## D-067 — Technical English Mastery Profile = TEPM-v0
**Durum:** Kabul edildi — 2026-08-27

- 7E final modeli `TEPM-v0 — Technical English Mastery Profile` oldu.
- Canonical spec `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`; machine-readable policy `curriculum/english/7e_mastery_profile/policy.yaml`; research basis `research/7e_english_mastery_profile_research.md`.
- 7E yeni mastery engine değildir: exact English Skill/Objective mastery GRE-v0, retention RVR-v0, prerequisite PRG-v0, prior-knowledge validation VDW-v0, remediation WLRM-v0 tarafından sahiplenilmeye devam eder.
- D01 exact 15 Skill identity'si değişmedi; yeni Skill/Objective/prerequisite edge veya numeric English mastery formula üretilmedi.
- 8 learner-facing derived Skill presentation state kabul edildi: not-yet-evidenced, developing-with-support, developing-independent, confirmed-current, confirmed-review-due, confirmation-verification-due, remediation-required, prerequisite-unresolved.
- `review_due` mastery veya base-band demotion değildir; `verification_due` ilk contradiction sonrası current uncertainty gösterir fakat mastery'yi anında silmez.
- Current complete base profile yalnız qualified Technical English A1/A2/B1 summary'dir; exact uneven Skill detail'i her zaman source of truth'a trace edilebilir.
- General-English `You are B1`, official CEFR/certification claim, numeric CEFR average veya compensatory English percentage yasaktır.
- Confirmed remediation current complete band'ı real current evidence'dan yeniden türetebilir; historical higher-band confirmation provenance olarak korunur.
- B2+ aggregate mastery/completion değildir; TECP-v0'nun exact 4 extension-eligible Skill'i için named professional extension evidence olarak gösterilir.
- Assisted, answer-revealed, provisional veya contaminated performance independent-confirmed English mastery üretmez; later assisted practice daha önceki clean mastery'yi tek başına silmez.
- TEIP-v0 integration semantics korunur: technical-only/exposure task English mastery broadcast yapmaz; dual-target evidence component-specific; hidden specialist technical context English failure'a dönüşemez.
- Independent 7E QA: 54/54 check PASS; 15 Skill / 8 presentation state / 3 base band / 4 B2+ extension Skill / 18 safety fixture / 17 reason-code.
- AŞAMA 7 tamamlandı. Sonraki numbered step `8A — Bilgi mimarisi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.
""",
)
append_once(
    "docs/PROGRESS_LOG.md",
    "## 2026-08-27 — 7E English mastery tamamlandı — TEPM-v0 / D-067",
    """
## 2026-08-27 — 7E English mastery tamamlandı — TEPM-v0 / D-067

- Kullanıcı açık onayı sonrası fresh PRE ile 7D complete / 7E active-not-executed state çapraz doğrulandı.
- Council of Europe CEFR profile/context/linkage guidance, ALTE language-for-specific-purposes materyali ve ETS score-profile/validity guidance Research girdisi olarak incelendi.
- External research tek global English label yerine profile reporting yönünü destekledi; hiçbir official certification, numeric English score veya yeni psychometric threshold türetilmedi.
- Existing GRE/RVR/PRG/VDW/WLRM ownership korunarak 8 derived learner-facing English Skill presentation state tanımlandı.
- Qualified A1/A2/B1 Technical English base profile, uneven-profile preservation, review/verification/remediation semantics ve historical-confirmation provenance kilitlendi.
- B2+ yalnız exact 4 TECP extension capability için named evidence olarak tutuldu; aggregate B2+ completion yasaklandı.
- Assisted/provisional/contaminated evidence independent confirmation'dan ayrıldı; TEIP-v0 cross-track attribution guards korundu.
- Independent QA: Stage 6 + 7B + 7C + 7D regressions PASS; 7E validator 54/54 PASS.
- D-050 POST ile AŞAMA 7 kapatıldı ve `8A — Bilgi mimarisi` active-not-executed yapıldı; external-memory + repo-wide stale audit zorunlu final gates olarak çalıştırıldı.

**Sonraki kesin adım:** `8A — Bilgi mimarisi`. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""",
)

# 3) EXECUTION_INDEX: finish Stage 7, activate 8A, rewrite current tail.
replace_required(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **7E — English mastery** **AKTİF**",
    "- [x] **7E — English mastery** — `TEPM-v0 / D-067`",
)
replace_required(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **8A — Bilgi mimarisi**",
    "- [ ] **8A — Bilgi mimarisi** **AKTİF**",
)
idx_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`  
**Son tamamlanan:** **`7E — TEPM-v0 / D-067`**  
**Aktif:** **`8A — Bilgi mimarisi`** — active-not-executed

AŞAMA 7 tamamen tamamlandı: EED-v0 → TECP-v0 → DECP-v0 → TEIP-v0 → TEPM-v0. English mastery truth exact GRE/RVR-backed Skill/Objective state'tir; CEFR yalnız qualified Technical English profile metadata/summary'dir.

8A başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.
"""
sub_required("docs/EXECUTION_INDEX.md", r"# Güncel Konum\n.*\Z", idx_tail, flags=re.S)

# 4) STEP_STATUS table + tail.
replace_required(
    "docs/STEP_STATUS.md",
    "| **7E — English mastery** | 🟡 Aktif | Learner-facing English mastery/profile behavior; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **8–20** | ⬜ Bekliyor | 7E sonrası canonical sırada. |",
    "| **7E — English mastery** | ✅ | TEPM-v0 / D-067. 8 derived Skill state + qualified A1/A2/B1 Technical English profile + B2+ per-capability evidence; QA PASS. |\n| **8A — Bilgi mimarisi** | 🟡 Aktif | UX information architecture; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **8B–20** | ⬜ Bekliyor | 8A sonrası canonical sırada. |",
)
step_tail = """## Son tamamlanan numaralı adım — 7E

**Final:** `TEPM-v0 — Technical English Mastery Profile` / D-067.  
**Ana çıktı:** `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md` + `curriculum/english/7e_mastery_profile/`.

7E sonucu:
- exact D01 15 Skill unchanged; no graph/mastery-engine mutation,
- 8 deterministic learner-facing derived Skill presentation state,
- qualified A1/A2/B1 Technical English base profile; uneven exact Skill detail preserved,
- `review_due` no demotion; `verification_due` uncertainty without instant deletion,
- confirmed remediation current profile'ı real evidence ile recompute eder; historical confirmation provenance korunur,
- no numeric English %, CEFR average, general-English/official/certification overclaim,
- B2+ only per-capability professional extension evidence on exact 4 TECP Skills,
- assisted/provisional/contaminated evidence cannot create independent-confirmed state,
- TEIP-v0 component attribution and contamination guards preserved,
- 18/18 safety fixture and 54/54 independent validator check PASS,
- Stage 6 + accepted 7B + 7C + 7D regressions PASS.

## AŞAMA 7 — tamamlandı

7A EED-v0 / D-063 → 7B TECP-v0 / D-064 → 7C DECP-v0 / D-065 → 7D TEIP-v0 / D-066 → 7E TEPM-v0 / D-067.

## Aktif adım — 8A Bilgi mimarisi

**8A henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
sub_required("docs/STEP_STATUS.md", r"## Son tamamlanan numaralı adım — 7D\n.*\Z", step_tail, flags=re.S)

# 5) MASTER_PLAN: complete 7E, activate 8A, rewrite current tail.
replace_required(
    "docs/MASTER_PLAN.md",
    "### [ ] 7E — English mastery — **AKTİF**",
    """### [x] 7E — English mastery — TEPM-v0 / D-067
**Final:** `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md` + `curriculum/english/7e_mastery_profile/`

- exact D01 15 Skill identity unchanged; no graph/mastery algorithm mutation,
- 8 learner-facing derived Skill presentation state,
- qualified A1/A2/B1 Technical English base profile + uneven exact Skill detail,
- review_due no demotion; verification_due uncertainty/hysteresis preserved,
- remediation can recompute current band while historical confirmation remains,
- B2+ per-capability evidence only for exact 4 TECP extension Skills,
- no general-English/certification/numeric aggregate overclaim,
- assistance/provisional/contamination and TEIP component-attribution guards preserved,
- 18 safety fixture / 54 validator checks PASS.

> **AŞAMA 7 tamamlandı — EED-v0 + TECP-v0 + DECP-v0 + TEIP-v0 + TEPM-v0.**""",
)
replace_required(
    "docs/MASTER_PLAN.md",
    "### [ ] 8A — Bilgi mimarisi",
    "### [ ] 8A — Bilgi mimarisi — **AKTİF**",
)
plan_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`  
**Son tamamlanan:** **`7E — TEPM-v0 / D-067`**  
**Aktif:** **`8A — Bilgi mimarisi`** — henüz yürütülmedi.

Bir sonraki yürütme: **8A fresh PRE-STEP → accepted product/learning/planner/assessment/curriculum/English contracts üzerinden UX information architecture → independent QA → D-050 POST sync + stale audit.**

- D-063: 7A final `EED-v0`.
- D-064: 7B final `TECP-v0`.
- D-065: 7C final `DECP-v0`.
- D-066: 7D final `TEIP-v0`.
- D-067: 7E final `TEPM-v0`; Stage 7 complete.
"""
sub_required("docs/MASTER_PLAN.md", r"# Güncel Konum\n.*\Z", plan_tail, flags=re.S)

# 6) HANDOFF_STATE: replace 7E handoff with final summary + 8A handoff.
replace_required(
    "docs/HANDOFF_STATE.md",
    "## 18. 7E handoff\n\n7E — English mastery; learner-facing granular English mastery/profile/CEFR summary behavior, B2+ evidence presentation ve English-specific mastery/remediation display semantics'ini EED/TECP/DECP/TEIP + GRE/RVR/WLRM contracts üzerinde kesinleştirecektir. 7E fresh PRE + kullanıcı açık onayı olmadan yürütülmez.",
    """## 18. D-067 / 7E final özeti

Canonical: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.  
Policy/QA: `curriculum/english/7e_mastery_profile/`.  
Research: `research/7e_english_mastery_profile_research.md`.

TEPM-v0:
- exact D01 15 Skill identity unchanged,
- 8 learner-facing derived Skill presentation state,
- GRE/RVR/PRG/VDW/WLRM remain canonical state owners,
- qualified A1/A2/B1 Technical English base profile + first-class uneven Skill detail,
- review_due no demotion; verification_due no instant deletion; remediation recomputes current profile from clean evidence,
- historical confirmed band provenance retained,
- no broad/general/official CEFR claim or numeric English aggregate,
- B2+ only named per-capability extension evidence on exact 4 TECP Skills,
- assisted/provisional/contaminated evidence cannot create independent confirmation,
- TEIP cross-track component attribution and contamination guards preserved,
- 18 safety fixture + independent 54-check QA PASS.

AŞAMA 7 tamamlandı.

## 19. 8A handoff

8A — Bilgi mimarisi; accepted product + planner + assessment + granular curriculum + Stage 7 English profile contracts üzerinden uygulamanın ekran/section/navigation information architecture'ını tasarlayacaktır. 8A fresh PRE + kullanıcı açık onayı olmadan yürütülmez.""",
)

# 7) PROJECT_CONTEXT: add 7E summary and advance current state.
ctx = read("PROJECT_CONTEXT.md")
if "### 8.5 7E Technical English Mastery Profile — TEPM-v0 / D-067" not in ctx:
    anchor = "Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.\n"
    if anchor not in ctx:
        raise RuntimeError("PROJECT_CONTEXT: 7D canonical anchor missing")
    ctx = ctx.replace(anchor, anchor + """

### 8.5 7E Technical English Mastery Profile — TEPM-v0 / D-067

English learner-facing mastery görünümü exact D01 15 Skill'in GRE/RVR-backed state'inden türetilir; yeni mastery engine değildir. 8 derived Skill presentation state, qualified A1/A2/B1 Technical English base profile, uneven-profile preservation ve B2+ per-capability extension evidence kabul edildi. `review_due` bandı düşürmez; `verification_due` ilk contradiction sonrası uncertainty gösterir; confirmed remediation current profile'ı clean evidence ile yeniden türetir ve historical confirmation provenance korunur. General-English/official CEFR claim veya numeric aggregate yoktur.

Canonical: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.
""", 1)
write("PROJECT_CONTEXT.md", ctx)
ctx_state = """## 11. Güncel yürütme konumu

- AŞAMA 1–5 ✅
- **AŞAMA 6 ✅ tamamlandı — S6ERQA-v0 / D-062**
  - 6A ✅ GNS-v0 / D-054
  - 6B ✅ FRDB-v0 / D-056
  - 6C ✅ FDM-v0 / D-057
  - 6D ✅ SDM-v0 / D-058
  - 6E ✅ GIM-v0 / D-059
  - 6F ✅ PEM-v0 / D-060
  - 6G ✅ WLRM-v0 / D-061
  - 6H ✅ S6ERQA-v0 / D-062
- **AŞAMA 7 ✅ tamamlandı**
  - 7A ✅ EED-v0 / D-063
  - 7B ✅ TECP-v0 / D-064
  - 7C ✅ DECP-v0 / D-065
  - 7D ✅ TEIP-v0 / D-066
  - 7E ✅ TEPM-v0 / D-067
- **8A 🟡 Bilgi mimarisi — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
- 8B–20 ⬜

Final Stage 6 graph: **549 Skill / 608 Objective / 950 prerequisite edge / 549/549 hard DAG**. WLRM final registry coverage 549/608; 10/10 6H review resolved.

**Sıradaki numaralı çalışma 8A'dır.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.

"""
sub_required("PROJECT_CONTEXT.md", r"## 11\. Güncel yürütme konumu\n.*?(?=## 12\.)", ctx_state, flags=re.S)

# 8) START_HERE: add decisions/read-path and move bootstrap state to 8A.
start = read("docs/START_HERE.md")
if "### D-067 — TEPM-v0" not in start:
    d64 = "### D-064 — TECP-v0\n7B final `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`; D01 15 Skill'i 5 A1 + 5 A2 + 5 B1 context-only Technical English anchor'a bağlar, 4 bounded B2+ professional extension tanımlar ve CEFR mastery/certification overclaim'ini yasaklar.\n"
    if d64 not in start:
        raise RuntimeError("START_HERE: D-064 block missing")
    start = start.replace(d64, d64 + """

### D-065 — DECP-v0
7C final `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`; Technical English common capacity içinde parallel candidate opportunity olarak çalışır, fixed minute/percentage/streak/debt yoktur ve state-driven task mix kullanır.

### D-066 — TEIP-v0
7D final `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`; dört construct-aware integration mode, component attribution, bidirectional contamination guard ve evidence-driven reversible scaffold davranışını kilitler.

### D-067 — TEPM-v0
7E final `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`; exact 15 D01 Skill state'ini 8 derived learner-facing presentation state ve qualified A1/A2/B1 Technical English profile'a projekte eder; B2+ per-capability evidence'dır, general-English/official CEFR veya numeric aggregate değildir.
""", 1)
start = start.replace(
    "- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B ✅ TECP-v0 / D-064; 7C ✅ DECP-v0 / D-065; 7D ✅ TEIP-v0 / D-066; 7E 🟡 active-not-executed**\n- 8 UX",
    "- 7 English parallel line ✅ — **7A EED-v0 / D-063; 7B TECP-v0 / D-064; 7C DECP-v0 / D-065; 7D TEIP-v0 / D-066; 7E TEPM-v0 / D-067**\n- 8 UX — **8A 🟡 active-not-executed**",
)
if "11ab. `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`" not in start:
    anchor = "11aa. `curriculum/english/7c_daily_component/qa_report.yaml`\n"
    if anchor not in start:
        raise RuntimeError("START_HERE: 11aa read-order anchor missing")
    start = start.replace(anchor, anchor + """11ab. `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`
11ac. `curriculum/english/7d_technical_integration/policy.yaml`
11ad. `curriculum/english/7d_technical_integration/qa_report.yaml`
11ae. `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`
11af. `curriculum/english/7e_mastery_profile/policy.yaml`
11ag. `curriculum/english/7e_mastery_profile/qa_report.yaml`
"", 1)
start = start.replace(
    "- AŞAMA 7: **7A ✅ EED-v0 / D-063**, **7B ✅ TECP-v0 / D-064**, **7C ✅ DECP-v0 / D-065**, **7D ✅ TEIP-v0 / D-066**, **7E 🟡 active-not-executed**\n\n7D final: Technical English integration 4 construct-aware mode kullanır; English/CEFR global technical gate değildir, dual-target evidence component-level attribution ile çalışır ve scaffold evidence/task-validity driven'dır.",
    "- AŞAMA 7 ✅ **EED-v0 / D-063 → TECP-v0 / D-064 → DECP-v0 / D-065 → TEIP-v0 / D-066 → TEPM-v0 / D-067**\n\n7E final: English mastery exact GRE/RVR-backed D01 Skill state'inden derived profile olarak sunulur; 8 presentation state, qualified A1/A2/B1 base profile, B2+ per-capability evidence ve no-overclaim guards kabul edildi.",
)
start = re.sub(
    r"## 9\. Güncel çalışma konumu\n.*?(?=## 10\.)",
    """## 9. Güncel çalışma konumu

**Son tamamlanan:** **`7E — TEPM-v0 / D-067`**  
**AŞAMA 7:** **✅ TAMAMLANDI**  
**Aktif:** **`8A — Bilgi mimarisi`**  
**8A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

""",
    start,
    flags=re.S,
)
start = re.sub(
    r"## 10\. Yeni sohbet için kısa komut\n> .*\Z",
    """## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. AŞAMA 7 TEPM-v0 / D-067 ile tamamlandı: exact D01 mastery GRE/RVR-backed Skill state, 8 derived presentation state, qualified A1/A2/B1 Technical English profile, B2+ per-capability evidence, no general-English/official CEFR/numeric overclaim. Aktif step 8A — Bilgi mimarisi; 8A henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`
""",
    start,
    flags=re.S,
)
write("docs/START_HERE.md", start)

# 9) AGENTS bootstrap state.
agents = read("AGENTS.md")
agents = re.sub(
    r"- 7D: ✅ `TEIP-v0 / D-066`.*?- AŞAMA 8–20 bekliyor\.",
    """- 7D: ✅ `TEIP-v0 / D-066` tamamlandı — 4 construct-aware integration mode; component attribution; bidirectional contamination + reversible scaffold guards.
- 7E: ✅ `TEPM-v0 / D-067` tamamlandı — 8 derived Skill presentation state; qualified A1/A2/B1 Technical English profile; B2+ per-capability evidence; no general-English/official CEFR/numeric aggregate overclaim.
- **AŞAMA 7 tamamen tamamlandı.**
- **Aktif adım: 8A — Bilgi mimarisi.**
- **8A henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 8B–20 bekliyor.""",
    agents,
    flags=re.S,
)
agents = agents.replace("**7E'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7E için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.", "**8A'yı bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 8A için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.")
write("AGENTS.md", agents)

# 10) LOCAL_MANAGER_HANDOFF current snapshots + durable addendum.
local = read("docs/LOCAL_MANAGER_HANDOFF.md")
local = local.replace("- AŞAMA 7E 🟡 active-not-executed\n- 8–20 ⬜", "- AŞAMA 7E ✅ TEPM-v0 / D-067\n- AŞAMA 8A 🟡 active-not-executed\n- 8B–20 ⬜")
local = re.sub(
    r"# 22\. Current exact state — en kritik takeover bilgisi\n.*?(?=# 23\.)",
    """# 22. Current exact state — en kritik takeover bilgisi

**Son tamamlanan numaralı adım:** `7E — English mastery`  
**Final:** `TEPM-v0 — Technical English Mastery Profile` / D-067  
**Canonical:** `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md` + `curriculum/english/7e_mastery_profile/`

**AŞAMA 7:** ✅ TAMAMLANDI  
**Aktif adım:** `8A — Bilgi mimarisi`  
**Durum:** **HENÜZ YÜRÜTÜLMEDİ**

7E exact GRE/RVR-backed English Skill state'ini learner-facing derived profile'a dönüştürür; CEFR qualified Technical English summary'dir, general-English certification değildir. B2+ aggregate completion değildir.

8A için:

```text
fresh 8A PRE-STEP GitHub refresh
→ user explicit approval verification
→ 8A execution
→ independent QA
→ D-050 POST sync
→ repo-wide stale-reference audit
```

""",
    local,
    flags=re.S,
)
local = local.replace("7D, technical task içindeki English/bilingual scaffold ve cross-track prerequisite/evidence fairness davranışını tasarlayacaktır. English global technical hard gate olmayacaktır.\n\nKullanıcı 7D'yi onayladığında:", "7D TEIP-v0 ile tamamlandı; technical task içindeki English/bilingual scaffold ve cross-track prerequisite/evidence fairness davranışı accepted canonical state'tir.\n\nHistorical 7D execution example:")
append_marker = "## 7E completion addendum — D-067"
if append_marker not in local:
    local += """

---

## 7E completion addendum — D-067

7E `TEPM-v0 — Technical English Mastery Profile` ile tamamlandı. Canonical: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`; policy/QA: `curriculum/english/7e_mastery_profile/`. Exact 15 D01 Skill korunur; 8 derived Skill presentation state, qualified A1/A2/B1 Technical English base profile, review/verification/remediation hysteresis presentation, first-class uneven profile ve exact 4-Skill B2+ extension evidence semantics kabul edildi. General-English/official CEFR veya numeric aggregate claim yoktur. Independent QA 54/54 PASS. AŞAMA 7 tamamlandı. Current active numbered step 8A'dır; fresh PRE + kullanıcı açık onayı gerekir.
"""
write("docs/LOCAL_MANAGER_HANDOFF.md", local)

# 11) Vault current context + open loops.
vctx = read("vault/agent/CURRENT_CONTEXT.md")
if "`TEPM-v0 / D-067`" not in vctx:
    vctx = vctx.replace("- `TEIP-v0 / D-066`: Technical English integration; 4 construct-aware mode, component attribution, bidirectional contamination guard, evidence-driven reversible scaffold; no global English/CEFR technical gate.\n", "- `TEIP-v0 / D-066`: Technical English integration; 4 construct-aware mode, component attribution, bidirectional contamination guard, evidence-driven reversible scaffold; no global English/CEFR technical gate.\n- `TEPM-v0 / D-067`: Technical English mastery/profile projection; 8 derived Skill presentation state, qualified A1/A2/B1 profile, B2+ per-capability evidence; no broad/general/official CEFR or numeric aggregate.\n")
vctx = re.sub(
    r"## Exact execution state\n\n.*?(?=\n## Immediate open loops)",
    """## Exact execution state

AŞAMA 6 ve AŞAMA 7 tamamlandı. Son tamamlanan adım **7E — TEPM-v0 / D-067**. English mastery truth exact GRE/RVR-backed D01 Skill/Objective state'tir; learner-facing CEFR yalnız qualified Technical English profile summary'dir. Aktif adım **8A — Bilgi mimarisi**; henüz yürütülmedi. 8A başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.

Bu snapshot'tan daha güncel veya çelişen bir iddia varsa [[vault/wiki/sources/Execution State Source|living-memory seti]] kazanır.
""",
    vctx,
    flags=re.S,
)
write("vault/agent/CURRENT_CONTEXT.md", vctx)

loops = read("vault/agent/OPEN_LOOPS.md")
loops = loops.replace("- [ ] 7E English mastery **AKTİF**: technical task'lerde English/bilingual scaffold, construct fairness ve cross-track evidence/prerequisite behavior.", "- [x] 7E English mastery: TEPM-v0 / D-067 ile tamamlandı; 8 derived profile state + qualified A1/A2/B1 Technical English profile + B2+ per-capability evidence.\n- [ ] 8A Bilgi mimarisi **AKTİF**: accepted product/learning/planner/assessment/curriculum/English contracts üzerinden UX information architecture.")
write("vault/agent/OPEN_LOOPS.md", loops)

# 12) Session log as durable provenance for the completed numbered step.
log = ROOT / "vault/agent/session-logs/2026-08-27-7e-english-mastery.md"
if not log.exists():
    log.parent.mkdir(parents=True, exist_ok=True)
    log.write_text("""---
type: session-log
status: completed
step: 7E
decision: D-067
model: TEPM-v0
date: 2026-08-27
---

# 7E — English mastery / TEPM-v0

- Fresh PRE confirmed 7D complete and 7E active; user explicit approval present.
- Research: Council of Europe CEFR profile/context/linkage guidance + ALTE LSP + ETS validity/profile interpretation.
- Canonical output: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.
- Machine-readable policy: `curriculum/english/7e_mastery_profile/policy.yaml`.
- Independent QA: 54/54 PASS; Stage 6 + 7B + 7C + 7D regressions PASS.
- Final model: `TEPM-v0 / D-067`.
- AŞAMA 7 completed; next active step `8A — Bilgi mimarisi`, not executed without fresh PRE + explicit user approval.
""", encoding="utf-8")

print("7E_POST_FINALIZER=PASS")
