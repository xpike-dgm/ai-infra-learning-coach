from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_once(rel: str, old: str, new: str) -> None:
    text = read(rel)
    if old in text:
        text = text.replace(old, new, 1)
        write(rel, text)
        return
    if new in text:
        return
    raise RuntimeError(f"{rel}: expected replacement source not found: {old[:100]!r}")


def append_once(rel: str, marker: str, block: str) -> None:
    text = read(rel)
    if marker in text:
        return
    if not text.endswith("\n"):
        text += "\n"
    text += "\n" + block.strip() + "\n"
    write(rel, text)


# 1) Main 7A artifacts become accepted/final.
replace_once(
    "docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md",
    "**Durum:** CANDIDATE — QA + POST-STEP kapanışı bekliyor",
    "**Durum:** TAMAMLANDI",
)
replace_once(
    "docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md",
    "**Candidate model:** `EED-v0 — English Entry Diagnostic`",
    "**Final model:** `EED-v0 — English Entry Diagnostic`",
)
replace_once(
    "docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md",
    "**Candidate decision:** `D-063`",
    "**Final decision:** `D-063`",
)
blueprint_path = ROOT / "curriculum/english/7a_entry_diagnostic/blueprint.yaml"
blueprint = yaml.safe_load(blueprint_path.read_text(encoding="utf-8"))
blueprint["status"] = "accepted_7a"
blueprint["decision"] = "D-063"
blueprint_path.write_text(yaml.safe_dump(blueprint, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

# 2) Durable decision and progress provenance.
append_once(
    "docs/DECISIONS.md",
    "## D-063 — English Entry Diagnostic = EED-v0",
    """
## D-063 — English Entry Diagnostic = EED-v0
**Durum:** Kabul edildi — 2026-08-27

- 7A final modeli `EED-v0 — English Entry Diagnostic` oldu.
- Canonical spec `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`; machine-readable blueprint `curriculum/english/7a_entry_diagnostic/blueprint.yaml`; research basis `research/7a_english_entry_diagnostic_research.md`.
- Diagnostic final Stage 6 D01 registry'sindeki 15 Technical English Skill + 15 Objective'i exact profile olarak ölçer; broad `English weak` veya tek broad score canonical learner state değildir.
- Canonical English hard prerequisite DAG 16 edge ile aynen tüketilir; root `skill.english.recognize_core_technical_labels`.
- Diagnostic mastery için daha düşük standart kullanmaz: positive mastery/waiver yalnız GRE-v0 + VDW-v0 normal gate'leriyle oluşur.
- Self-report, certificate claim ve confidence routing/context sinyalidir; mastery/evidence değildir.
- Unknown grammar/vocabulary veya specialist technical knowledge target English failure'ına gizli prerequisite olamaz.
- Invalid, ambiguous, technical-context-contaminated veya prerequisite-unresolved attempt target negative evidence yazamaz; downstream dependent Skills topluca failed yapılmaz.
- 15 diagnostic claim için 15 task family tanımlandı; resource/evaluator trust QAB-v0 + AIV-v0 + GRE-v0 zincirine bağlıdır.
- 7A final CEFR level atamaz; `cefr_alignment_status=pending_7B`, `cefr_level=null`. `review.6c.english.cefr_alignment` açık kalır ve 7B'nin ownership'indedir.
- Independent deterministic 7A validator PASS: 15/15 Skills, 15/15 Objectives, 16/16 English hard edge, 15/15 task family; final Stage 6 regression PASS.
- Sonraki numbered step `7B — A1/A2/B1/B2+ teknik hedefleri`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`.
""",
)
append_once(
    "docs/PROGRESS_LOG.md",
    "## 2026-08-27 — 7A English Entry Diagnostic tamamlandı — EED-v0 / D-063",
    """
## 2026-08-27 — 7A English Entry Diagnostic tamamlandı — EED-v0 / D-063

- Kullanıcı açık onayı sonrası fresh PRE-STEP ile Stage 6 completion ve 7A active-not-executed state doğrulandı.
- Council of Europe CEFR/test-development kaynakları ve ETS Evidence-Centered Design kaynakları Research girdisi olarak incelendi; final karar manager tarafından mevcut GRE/VDW/PRG/English contracts ile reconcile edildi.
- D01 final registry'deki 15 English Skill / 15 Objective ve 16 English→English hard prerequisite edge diagnostic scope olarak birebir tüketildi.
- `EED-v0` granular entry profile, prerequisite-aware adaptive probing, self-report non-evidence, technical-context contamination guard, no-downstream-fail-broadcast ve pause/resume semantics'i kilitledi.
- CEFR A1/A2/B1/B2+ mapping 7A'da yapılmadı; `review.6c.english.cefr_alignment` 7B'ye açık bırakıldı.
- `validate_6h_external_reconciliation.py` final Stage 6 regression PASS.
- `validate_english_entry_diagnostic.py` PASS: 15/15 Skill, 15/15 Objective, 16/16 hard edge, 15/15 task family; English hard DAG 15/15.
- D-050 POST living-memory + external-memory + repo-wide stale-reference audit ile 7A kapatıldı; 7B active-not-executed yapıldı.

**Sonraki kesin adım:** `7B — A1/A2/B1/B2+ teknik hedefleri`. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""",
)

# 3) STEP_STATUS table + final/current sections.
replace_once(
    "docs/STEP_STATUS.md",
    "| **7A — İngilizce başlangıç ölçümü** | 🟡 Aktif | Sıradaki canonical numbered step; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7B–20** | ⬜ Bekliyor | 7A sonrası canonical sırada. |",
    "| **7A — İngilizce başlangıç ölçümü** | ✅ | EED-v0 / D-063. 15 Skill / 15 Objective / 16 hard edge diagnostic profile + 15 task family; QA PASS. |\n| **7B — A1/A2/B1/B2+ teknik hedefleri** | 🟡 Aktif | CEFR/technical progression alignment; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7C–20** | ⬜ Bekliyor | 7B sonrası canonical sırada. |",
)
step = read("docs/STEP_STATUS.md")
step_tail = """## Son tamamlanan numaralı adım — 7A

**Final:** `EED-v0 — English Entry Diagnostic` / D-063.  
**Ana çıktı:** `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` + `curriculum/english/7a_entry_diagnostic/`.

7A sonucu:
- 15/15 canonical D01 English Skill,
- 15/15 canonical owner Objective,
- 16/16 English hard prerequisite edge,
- 15 diagnostic task family,
- English hard DAG 15/15,
- self-report/certificate/confidence non-evidence,
- no easier diagnostic mastery threshold,
- no hidden technical/unknown-English prerequisite,
- invalid/prerequisite-contaminated failure target negative evidence yazmıyor,
- CEFR level assignment 7B'ye deferred,
- final Stage 6 regression + independent 7A validator PASS.

## Aktif adım — 7B A1/A2/B1/B2+ teknik hedefleri

**7B henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
step, n = re.subn(r"## Son tamamlanan numaralı adım — 6H\n.*\Z", step_tail, step, count=1, flags=re.S)
if n != 1 and step_tail not in step:
    raise RuntimeError("STEP_STATUS final/current tail not found")
write("docs/STEP_STATUS.md", step)

# 4) EXECUTION_INDEX stage rows + current mirror.
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **7A — Başlangıç ölçümü** **AKTİF**\n- [ ] **7B — A1/A2/B1/B2+ teknik hedefleri**",
    "- [x] **7A — Başlangıç ölçümü** — `EED-v0 / D-063`\n- [ ] **7B — A1/A2/B1/B2+ teknik hedefleri** **AKTİF**",
)
idx = read("docs/EXECUTION_INDEX.md")
idx_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A`  
**Son tamamlanan:** **`7A — EED-v0 / D-063`**  
**Aktif:** **`7B — A1/A2/B1/B2+ teknik hedefleri`** — active-not-executed

7A final diagnostic contract: **15 D01 English Skill / 15 Objective / 16 English hard edge / 15 task family**, CEFR assignment `pending_7B`.

7B başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.
"""
idx, n = re.subn(r"# Güncel Konum\n.*\Z", idx_tail, idx, count=1, flags=re.S)
if n != 1 and idx_tail not in idx:
    raise RuntimeError("EXECUTION_INDEX current tail not found")
write("docs/EXECUTION_INDEX.md", idx)

# 5) MASTER_PLAN Stage 7 and current mirror.
replace_once(
    "docs/MASTER_PLAN.md",
    "### [ ] 7A — Başlangıç ölçümü — **AKTİF**\n### [ ] 7B — A1/A2/B1/B2+ teknik hedefleri",
    "### [x] 7A — Başlangıç ölçümü — EED-v0 / D-063\n**Final:** `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` + `curriculum/english/7a_entry_diagnostic/`\n\n- 15/15 D01 English Skill ve 15/15 owner Objective diagnostic claim olarak kapsandı,\n- canonical 16 English hard edge birebir korundu; hard DAG 15/15,\n- 15 task family claim→evidence→task traceability ile tanımlandı,\n- GRE-v0/VDW-v0 standardı düşürülmedi; self-report evidence değil,\n- unknown-English / specialist-technical hidden prerequisite ve downstream fail broadcast yasak,\n- CEFR level ataması 7B'ye deferred; `review.6c.english.cefr_alignment` açık,\n- final Stage 6 regression + 7A independent deterministic validator PASS.\n\n### [ ] 7B — A1/A2/B1/B2+ teknik hedefleri — **AKTİF**",
)
plan = read("docs/MASTER_PLAN.md")
plan_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A`  
**Son tamamlanan:** **`7A — EED-v0 / D-063`**  
**Aktif:** **`7B — A1/A2/B1/B2+ teknik hedefleri`** — henüz yürütülmedi.

Bir sonraki yürütme: **7B fresh PRE-STEP → EED-v0 diagnostic profile + Stage 6 D01 Skills + CEFR Companion Volume descriptors + `review.6c.english.cefr_alignment` ile technical progression alignment → kullanıcı onaylı execution → D-050 POST sync + stale audit.**

- D-062: Stage 6 final `S6ERQA-v0`.
- D-063: 7A final `EED-v0`; 15 Skill / 15 Objective / 16 English hard edge / 15 diagnostic task family, CEFR assignment pending 7B.
"""
plan, n = re.subn(r"# Güncel Konum\n.*\Z", plan_tail, plan, count=1, flags=re.S)
if n != 1 and plan_tail not in plan:
    raise RuntimeError("MASTER_PLAN current tail not found")
write("docs/MASTER_PLAN.md", plan)

# 6) HANDOFF_STATE current state and append 7A final + 7B handoff.
handoff = read("docs/HANDOFF_STATE.md")
handoff = handoff.replace("- AŞAMA 7A 🟡 active-not-executed\n- 7B–20 ⬜", "- AŞAMA 7A ✅ — EED-v0 / D-063\n- AŞAMA 7B 🟡 active-not-executed\n- 7C–20 ⬜")
handoff = handoff.replace(
    "**Son tamamlanan:** `6H — S6ERQA-v0 / D-062`  \n**Aktif:** `7A — İngilizce başlangıç ölçümü`  \n**7A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "**Son tamamlanan:** `7A — EED-v0 / D-063`  \n**Aktif:** `7B — A1/A2/B1/B2+ teknik hedefleri`  \n**7B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
)
handoff_tail = """## 14. D-063 / 7A final özeti

Canonical: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`.  
Blueprint/QA: `curriculum/english/7a_entry_diagnostic/`.  
Research: `research/7a_english_entry_diagnostic_research.md`.

EED-v0:
- final D01 registry'deki 15 English Skill + 15 Objective exact diagnostic profile scope,
- canonical 16 English hard prerequisite edge ve 15/15 DAG,
- 15 claim/task family,
- prerequisite-aware adaptive probing + pause/resume,
- self-report/certificate/confidence non-evidence,
- diagnostic mastery standardı GRE-v0/VDW-v0'dan daha kolay değil,
- specialist technical knowledge ve unknown grammar/vocabulary hidden prerequisite olamaz,
- invalid/prerequisite-unresolved attempt target negative evidence yazmaz,
- dependent branch failure broadcast yok,
- CEFR final level 7A'da atanmaz; 7B'ye pending,
- final Stage 6 regression + `tools/validate_english_entry_diagnostic.py` PASS.

## 15. 7B handoff

7B — A1/A2/B1/B2+ teknik hedefleri, EED-v0 profile/claims ile Stage 6 D01 capability identities'ini CEFR Companion Volume descriptors ve professional Technical English hedefleriyle hizalayacaktır. `review.6c.english.cefr_alignment` 7B ownership'inde açık kalır. 7B fresh PRE + kullanıcı açık onayı olmadan yürütülmez.
"""
handoff, n = re.subn(r"## 14\. 7A handoff\n.*\Z", handoff_tail, handoff, count=1, flags=re.S)
if n != 1 and handoff_tail not in handoff:
    raise RuntimeError("HANDOFF_STATE 7A handoff tail not found")
write("docs/HANDOFF_STATE.md", handoff)

# 7) PROJECT_CONTEXT English/current snapshot.
context = read("PROJECT_CONTEXT.md")
english_marker = "## 9. Professional-readiness depth"
english_add = """### 8.1 7A English Entry Diagnostic — EED-v0 / D-063

7A başlangıç ölçümü Stage 6 D01 Technical English graph'ını yeniden sınıflandırmaz; 15 Skill / 15 Objective / 16 English hard edge üzerinde prerequisite-aware diagnostic profile üretir. Self-report evidence değildir; diagnostic mastery GRE-v0/VDW-v0 standardını düşürmez; invalid/prerequisite-contaminated failure target weakness yazmaz. CEFR A1/A2/B1/B2+ mapping 7B'ye bırakılmıştır.

Canonical: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`.

"""
if "### 8.1 7A English Entry Diagnostic" not in context:
    if english_marker not in context:
        raise RuntimeError("PROJECT_CONTEXT insertion marker missing")
    context = context.replace(english_marker, english_add + english_marker, 1)
context = context.replace(
    "- **7A 🟡 İngilizce başlangıç ölçümü — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 7B–20 ⬜",
    "- **7A ✅ EED-v0 / D-063 — İngilizce başlangıç ölçümü tamamlandı**\n- **7B 🟡 A1/A2/B1/B2+ teknik hedefleri — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 7C–20 ⬜",
)
context = context.replace(
    "**Sıradaki numaralı çalışma 7A'dır.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
    "**Sıradaki numaralı çalışma 7B'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
)
write("PROJECT_CONTEXT.md", context)

# 8) START_HERE durable bootstrap and current tail.
start = read("docs/START_HERE.md")
if "### D-063 — EED-v0" not in start:
    marker = "## 4. Güncel stage mapping"
    addition = """### D-063 — EED-v0
7A final `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`; D01 15 Skill / 15 Objective / 16 hard edge için prerequisite-aware granular entry diagnostic, 15 task family ve no-premature-CEFR guard'ı kilitlendi.

"""
    if marker not in start:
        raise RuntimeError("START_HERE D-063 insertion marker missing")
    start = start.replace(marker, addition + marker, 1)
start = start.replace("- 7 English parallel line — **7A 🟡 active-not-executed**", "- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B 🟡 active-not-executed**")
start = start.replace(
    "11r. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    "11r. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`\n11s. `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`\n11t. `curriculum/english/7a_entry_diagnostic/blueprint.yaml`\n11u. `curriculum/english/7a_entry_diagnostic/qa_report.yaml`",
    1,
)
start_tail = """## 8. Tamamlanan çekirdek

- AŞAMA 1 ✅ Product framing
- AŞAMA 2 ✅ GRE-v0 + RVR-v0 learning/mastery
- AŞAMA 3 ✅ Adaptive planner — 16/16 scenarios, 20/20 invariants
- AŞAMA 4 ✅ DMA/WBA/MCA/QAB/AIV assessment system
- AŞAMA 5 ✅ PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6 ✅ GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / **S6ERQA-v0 / D-062**
- AŞAMA 7: **7A ✅ EED-v0 / D-063**, **7B 🟡 active-not-executed**

7A final: **15 D01 English Skill / 15 Objective / 16 hard prerequisite edge / 15 diagnostic task family**; CEFR A1/A2/B1/B2+ assignment 7B'ye pending.

## 9. Güncel çalışma konumu

**Son tamamlanan:** **`7A — EED-v0 / D-063`**  
**Aktif:** **`7B — A1/A2/B1/B2+ teknik hedefleri`**  
**7B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. 7A, EED-v0 / D-063 ile tamamlandı: D01 15 Skill / 15 Objective / 16 English hard edge / 15 diagnostic task family; CEFR alignment 7B'ye pending. Aktif step 7B — A1/A2/B1/B2+ teknik hedefleri; 7B henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`
"""
start, n = re.subn(r"## 8\. Tamamlanan çekirdek\n.*\Z", start_tail, start, count=1, flags=re.S)
if n != 1 and start_tail not in start:
    raise RuntimeError("START_HERE current tail not found")
write("docs/START_HERE.md", start)

# 9) AGENTS volatile bootstrap current state.
agents_old = """- AŞAMA 1–5: tamamlandı.
- AŞAMA 6: ✅ tamamlandı — `GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / S6ERQA-v0`.
- 6H final external Research QA: ✅ `S6ERQA-v0 / D-062`; 549 Skill / 608 Objective / 950 prerequisite edge; 549/549 hard DAG; 10/10 6H review resolved.
- **Aktif adım: 7A — İngilizce başlangıç ölçümü.**
- **7A henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 8–20 bekliyor.

**7A'yı bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7A için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula."""
agents_new = """- AŞAMA 1–5: tamamlandı.
- AŞAMA 6: ✅ tamamlandı — `GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / S6ERQA-v0`.
- 6H final external Research QA: ✅ `S6ERQA-v0 / D-062`; 549 Skill / 608 Objective / 950 prerequisite edge; 549/549 hard DAG; 10/10 6H review resolved.
- 7A: ✅ `EED-v0 / D-063` tamamlandı — D01 15 Skill / 15 Objective / 16 English hard edge / 15 diagnostic task family.
- **Aktif adım: 7B — A1/A2/B1/B2+ teknik hedefleri.**
- **7B henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 8–20 bekliyor.

**7B'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7B için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula."""
replace_once("AGENTS.md", agents_old, agents_new)

# 10) LOCAL_MANAGER_HANDOFF top current bootstrap.
local = read("docs/LOCAL_MANAGER_HANDOFF.md")
local = local.replace("current execution state'in `6A–6H completed / 7A active-not-executed` olduğunu doğrula.", "current execution state'in `6A–6H + 7A completed / 7B active-not-executed` olduğunu doğrula.")
local = local.replace("Ancak bundan sonra, 7A için **ayrı bir fresh PRE-STEP GitHub refresh** yap; kullanıcı açık onayı olmadan yürütme.", "Ancak bundan sonra, 7B için **ayrı bir fresh PRE-STEP GitHub refresh** yap; kullanıcı açık onayı olmadan yürütme.")
local = local.replace("7A active-not-executed", "7A completed / 7B active-not-executed")
local = local.replace("Aktif adım 7A", "Son tamamlanan adım 7A; aktif adım 7B")
if "## 7A completion addendum — D-063" not in local:
    local += """

---

## 7A completion addendum — D-063

7A `EED-v0 — English Entry Diagnostic` ile tamamlandı. Canonical: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`; blueprint/QA: `curriculum/english/7a_entry_diagnostic/`. D01 için 15 Skill / 15 Objective / 16 English hard edge / 15 diagnostic task family doğrulandı. CEFR alignment 7B'ye pending. Current active numbered step 7B'dir; fresh PRE + kullanıcı açık onayı gerekir.
"""
write("docs/LOCAL_MANAGER_HANDOFF.md", local)

# 11) Vault current context + open loops.
vctx = read("vault/agent/CURRENT_CONTEXT.md")
if "[[docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC|EED-v0]]" not in vctx:
    marker = "## Exact execution state"
    addition = "- [[docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC|EED-v0]]: D01 Technical English için prerequisite-aware granular giriş diagnostic'i; CEFR alignment 7B'ye pending.\n\n"
    if marker not in vctx:
        raise RuntimeError("CURRENT_CONTEXT marker missing")
    vctx = vctx.replace(marker, addition + marker, 1)
vctx = re.sub(
    r"6A–6H tamamlandı\..*?Bu snapshot'tan daha güncel veya çelişen bir iddia varsa",
    "6A–6H ve 7A tamamlandı. Son tamamlanan adım **7A — EED-v0 / D-063**. Aktif adım **7B — A1/A2/B1/B2+ teknik hedefleri**; henüz yürütülmedi. 7B başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.\n\nBu snapshot'tan daha güncel veya çelişen bir iddia varsa",
    vctx,
    count=1,
    flags=re.S,
)
write("vault/agent/CURRENT_CONTEXT.md", vctx)

loops = read("vault/agent/OPEN_LOOPS.md")
loops = loops.replace(
    "- [ ] 6H independent external Research QA **AKTİF**: full coverage, current-industry, prerequisite ve WLRM behavior audit; 6C–6G external coverage/freshness reviews burada ele alınır.",
    "- [x] 6H independent external Research QA: S6ERQA-v0 / D-062 ile tamamlandı; 10/10 6H review resolved.\n- [x] 7A English entry diagnostic: EED-v0 / D-063 ile tamamlandı.\n- [ ] 7B CEFR + technical progression alignment **AKTİF**: `review.6c.english.cefr_alignment` çözümü ve A1/A2/B1/B2+ technical target metadata.",
)
write("vault/agent/OPEN_LOOPS.md", loops)

print("7A_POST_FINALIZER=PASS")
