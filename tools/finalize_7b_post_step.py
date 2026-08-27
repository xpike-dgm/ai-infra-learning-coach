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
    if new in text:
        return
    if old not in text:
        raise RuntimeError(f"{rel}: replacement source missing: {old[:120]!r}")
    write(rel, text.replace(old, new, 1))


def append_once(rel: str, marker: str, block: str) -> None:
    text = read(rel)
    if marker in text:
        return
    if not text.endswith("\n"):
        text += "\n"
    text += "\n" + block.strip() + "\n"
    write(rel, text)


# 1) Finalize canonical 7B spec and alignment dataset.
replace_once(
    "docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md",
    "**Durum:** CANDIDATE — QA + POST-STEP kapanışı bekliyor",
    "**Durum:** TAMAMLANDI",
)
replace_once(
    "docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md",
    "**Candidate model:** `TECP-v0 — Technical English CEFR Progression`",
    "**Final model:** `TECP-v0 — Technical English CEFR Progression`",
)
replace_once(
    "docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md",
    "**Candidate decision:** `D-064`",
    "**Final decision:** `D-064`",
)

alignment_path = ROOT / "curriculum/english/7b_cefr_progression/alignment.yaml"
alignment = yaml.safe_load(alignment_path.read_text(encoding="utf-8"))
alignment["status"] = "accepted_7b"
alignment.pop("candidate_decision", None)
alignment["decision"] = "D-064"
alignment_path.write_text(yaml.safe_dump(alignment, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

# 2) Resolve the 6C CEFR alignment review without changing D01 identities.
review_path = ROOT / "curriculum/decomposition/6c_foundations/review_queue.yaml"
reviews = yaml.safe_load(review_path.read_text(encoding="utf-8"))
review = next((row for row in reviews if row.get("review_id") == "review.6c.english.cefr_alignment"), None)
if review is None:
    raise RuntimeError("review.6c.english.cefr_alignment missing")
review["status"] = "resolved"
review["resolution"] = (
    "D01 identities remain unchanged. TECP-v0 maps the exact 15 Skills as context-only CEFR progression metadata: "
    "5 A1 + 5 A2 + 5 B1 base anchors, with B2+ limited to bounded professional evidence-depth extension for selected existing Skills. "
    "The current D01 profile is text-first, so plain general-English/CEFR-certification claims are forbidden. "
    "No new Skill, Objective, prerequisite edge, mastery algorithm or English-to-technical global gate is introduced by 7B."
)
review["resolved_at"] = "2026-08-27"
review["resolution_refs"] = [
    "docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md",
    "curriculum/english/7b_cefr_progression/alignment.yaml",
    "research/7b_technical_english_cefr_research.md",
]
review_path.write_text(yaml.safe_dump(reviews, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

# 3) Durable decision and progress log.
append_once(
    "docs/DECISIONS.md",
    "## D-064 — Technical English CEFR Progression = TECP-v0",
    """
## D-064 — Technical English CEFR Progression = TECP-v0
**Durum:** Kabul edildi — 2026-08-27

- 7B final modeli `TECP-v0 — Technical English CEFR Progression` oldu.
- Canonical spec `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`; machine-readable alignment `curriculum/english/7b_cefr_progression/alignment.yaml`; research basis `research/7b_technical_english_cefr_research.md`.
- Accepted D01 15 Skill identity'si değişmeden exactly-one base CEFR-aligned Technical English anchor aldı: 5 A1 / 5 A2 / 5 B1.
- Canonical 16 English hard prerequisite edge'in tamamında band monotonicity PASS; CEFR metadata yeni prerequisite edge üretmez.
- CEFR alignment learner mastery state, raw score, Council of Europe certification veya general-English level claim değildir. Canonical source exact Skill/Objective evidence + GRE/VDW state'tir.
- Current D01 text-first profile olduğu için `English B1/B2`, `CEFR-certified` veya `official level` gibi unqualified claims yasaktır; yalnız qualified Technical English profile wording kullanılabilir.
- Pre-A1 zero-entry scaffold context'i olabilir fakat ayrı target gate/failure label/technical blocker değildir.
- B2+ yeni mastery bandı veya silent Skill expansion değildir; yalnız `documentation_navigation`, `read_definition_and_constraint`, `read_procedure_sequence`, `ask_clarifying_technical_question` için semantic boundary içinde bounded professional evidence-depth extension'dır.
- `simple/basic/bilingual` semantics taşıyan mevcut Skills B2+ görünümü için otomatik genişletilemez; daha geniş capability gerekirse GNS-v0/KGC-v0 normal capability review gerekir.
- 10 controlled CEFR scale-family ref authoring/provenance metadata'sı olarak kullanılır; descriptor family refs Skill identity/evidence rule değildir.
- `review.6c.english.cefr_alignment` 7B tarafından `CONTEXT_ONLY_NO_SPLIT` olarak resolved edildi; yeni Skill/Objective/prerequisite gerekmedi.
- 7A EED-v0 artifact'ındaki `pending_7B` alanı tarihsel handoff kontratı olarak korunur; current CEFR alignment source TECP-v0'dır.
- Final Stage 6 regression, 7A EED regression ve independent 7B validator PASS.
- Sonraki numbered step `7C — Günlük English bileşeni`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.
""",
)
append_once(
    "docs/PROGRESS_LOG.md",
    "## 2026-08-27 — 7B Technical English CEFR Progression tamamlandı — TECP-v0 / D-064",
    """
## 2026-08-27 — 7B Technical English CEFR Progression tamamlandı — TECP-v0 / D-064

- Kullanıcı açık onayı sonrası fresh PRE-STEP ile main `61ce26b...`, 7A completion ve 7B active-not-executed state doğrulandı.
- Council of Europe CEFR Companion Volume/descriptor/test guidance ve ALTE language-for-specific-purposes guidance Research girdisi olarak kullanıldı; final karar mevcut EED/GRE/VDW/PRG/GNS/KGC contracts ile reconcile edildi.
- D01 exact 15 Skill context-only progression metadata ile 5 A1 + 5 A2 + 5 B1 base anchor'a bağlandı.
- 16/16 English hard prerequisite edge için base-band monotonicity doğrulandı.
- B2+ yalnız 4 canonical Skill'de bounded professional evidence-depth extension olarak tanımlandı; silent semantic expansion ve synthetic B2+ mastery state yasaklandı.
- Current D01 text-first olduğu için plain general-English CEFR/certification claim yasaklandı; uneven Technical English profile korunur.
- `review.6c.english.cefr_alignment` resolved edildi; no split/no new Skill/Objective/prerequisite edge.
- EED-v0 historical `pending_7B` handoff marker'ı geriye dönük değiştirilmedi; current alignment source TECP-v0 oldu.
- Stage 6 regression + EED-v0 regression + independent 7B validator PASS.
- D-050 POST living-memory, external-memory ve repo-wide stale-reference audit ile 7B kapatıldı; 7C active-not-executed yapıldı.

**Sonraki kesin adım:** `7C — Günlük English bileşeni`. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""",
)

# 4) STEP_STATUS table and current tail.
replace_once(
    "docs/STEP_STATUS.md",
    "| **7B — A1/A2/B1/B2+ teknik hedefleri** | 🟡 Aktif | CEFR/technical progression alignment; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7C–20** | ⬜ Bekliyor | 7B sonrası canonical sırada. |",
    "| **7B — A1/A2/B1/B2+ teknik hedefleri** | ✅ | TECP-v0 / D-064. 15 Skill = 5 A1 + 5 A2 + 5 B1; 4 bounded B2+ extensions; 16/16 band-monotonic hard edges; QA PASS. |\n| **7C — Günlük English bileşeni** | 🟡 Aktif | Daily English cadence/task-mix design; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7D–20** | ⬜ Bekliyor | 7C sonrası canonical sırada. |",
)
step = read("docs/STEP_STATUS.md")
step_tail = """## Son tamamlanan numaralı adım — 7B

**Final:** `TECP-v0 — Technical English CEFR Progression` / D-064.  
**Ana çıktı:** `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md` + `curriculum/english/7b_cefr_progression/`.

7B sonucu:
- D01 15/15 canonical Skill aligned,
- base anchors = 5 A1 / 5 A2 / 5 B1,
- 16/16 canonical English hard edge band-monotonic,
- 4 bounded B2+ professional evidence-depth extension,
- 10 controlled CEFR scale-family ref,
- CEFR metadata mastery/certification/general-English claim değil,
- no new Skill/Objective/prerequisite edge,
- `review.6c.english.cefr_alignment` resolved,
- Stage 6 + EED-v0 + 7B validator PASS.

## Aktif adım — 7C Günlük English bileşeni

**7C henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
step, n = re.subn(r"## Son tamamlanan numaralı adım — 7A\n.*\Z", step_tail, step, count=1, flags=re.S)
if n != 1 and step_tail not in step:
    raise RuntimeError("STEP_STATUS current tail not found")
write("docs/STEP_STATUS.md", step)

# 5) EXECUTION_INDEX Stage 7 + current tail.
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **7B — A1/A2/B1/B2+ teknik hedefleri** **AKTİF**\n- [ ] **7C — Günlük English bileşeni**\n- [ ] **7D — Teknik entegrasyon**",
    "- [x] **7B — A1/A2/B1/B2+ teknik hedefleri** — `TECP-v0 / D-064`\n- [ ] **7C — Günlük English bileşeni** **AKTİF**\n- [ ] **7D — Teknik entegrasyon**",
)
idx = read("docs/EXECUTION_INDEX.md")
idx_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7B`  
**Son tamamlanan:** **`7B — TECP-v0 / D-064`**  
**Aktif:** **`7C — Günlük English bileşeni`** — active-not-executed

7B final: D01 **15 Skill = 5 A1 / 5 A2 / 5 B1**, 16/16 band-monotonic English hard edge ve **4 bounded B2+ professional extension**. CEFR alignment mastery/certification değildir.

7C başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.
"""
idx, n = re.subn(r"# Güncel Konum\n.*\Z", idx_tail, idx, count=1, flags=re.S)
if n != 1 and idx_tail not in idx:
    raise RuntimeError("EXECUTION_INDEX current tail not found")
write("docs/EXECUTION_INDEX.md", idx)

# 6) MASTER_PLAN Stage 7 and current tail.
replace_once(
    "docs/MASTER_PLAN.md",
    "### [ ] 7B — A1/A2/B1/B2+ teknik hedefleri — **AKTİF**\n### [ ] 7C — Günlük English bileşeni",
    "### [x] 7B — A1/A2/B1/B2+ teknik hedefleri — TECP-v0 / D-064\n**Final:** `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md` + `curriculum/english/7b_cefr_progression/`\n\n- exact 15 D01 Skill context-only CEFR progression metadata ile aligned,\n- 5 A1 / 5 A2 / 5 B1 base anchor; 16/16 hard edge band-monotonic,\n- 4 bounded B2+ professional evidence-depth extension,\n- no general-English certification/official-level claim,\n- no new Skill/Objective/prerequisite edge veya numeric CEFR formula,\n- `review.6c.english.cefr_alignment` resolved,\n- Stage 6 + EED-v0 + 7B validator PASS.\n\n### [ ] 7C — Günlük English bileşeni — **AKTİF**",
)
plan = read("docs/MASTER_PLAN.md")
plan_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7B`  
**Son tamamlanan:** **`7B — TECP-v0 / D-064`**  
**Aktif:** **`7C — Günlük English bileşeni`** — henüz yürütülmedi.

Bir sonraki yürütme: **7C fresh PRE-STEP → EED-v0 + TECP-v0 profile/bands + adaptive planner capacity/priority contracts ile daily English cadence/task-mix tasarımı → kullanıcı onaylı execution → D-050 POST sync + stale audit.**

- D-063: 7A final `EED-v0`.
- D-064: 7B final `TECP-v0`; 15 Skill = 5 A1 + 5 A2 + 5 B1, 4 bounded B2+ extension, CEFR review resolved.
"""
plan, n = re.subn(r"# Güncel Konum\n.*\Z", plan_tail, plan, count=1, flags=re.S)
if n != 1 and plan_tail not in plan:
    raise RuntimeError("MASTER_PLAN current tail not found")
write("docs/MASTER_PLAN.md", plan)

# 7) HANDOFF current state + 7B/7C handoff.
handoff = read("docs/HANDOFF_STATE.md")
handoff = handoff.replace("- AŞAMA 7B 🟡 active-not-executed\n- 7C–20 ⬜", "- AŞAMA 7B ✅ — TECP-v0 / D-064\n- AŞAMA 7C 🟡 active-not-executed\n- 7D–20 ⬜")
handoff = handoff.replace(
    "**Son tamamlanan:** `7A — EED-v0 / D-063`  \n**Aktif:** `7B — A1/A2/B1/B2+ teknik hedefleri`  \n**7B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "**Son tamamlanan:** `7B — TECP-v0 / D-064`  \n**Aktif:** `7C — Günlük English bileşeni`  \n**7C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
)
old_tail = """## 15. 7B handoff

7B — A1/A2/B1/B2+ teknik hedefleri, EED-v0 profile/claims ile Stage 6 D01 capability identities'ini CEFR Companion Volume descriptors ve professional Technical English hedefleriyle hizalayacaktır. `review.6c.english.cefr_alignment` 7B ownership'inde açık kalır. 7B fresh PRE + kullanıcı açık onayı olmadan yürütülmez."""
new_tail = """## 15. D-064 / 7B final özeti

Canonical: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.  
Alignment/QA: `curriculum/english/7b_cefr_progression/`.  
Research: `research/7b_technical_english_cefr_research.md`.

TECP-v0:
- 15/15 D01 Skill context-only CEFR progression alignment,
- 5 A1 / 5 A2 / 5 B1 base anchor,
- 16/16 English hard edge band-monotonic,
- 4 bounded B2+ professional evidence-depth extension,
- 10 controlled CEFR scale-family ref,
- current D01 text-first; no general-English/official/certification overclaim,
- CEFR metadata != mastery state,
- no new Skill/Objective/prerequisite edge,
- `review.6c.english.cefr_alignment` resolved,
- EED-v0 historical `pending_7B` handoff marker preserved,
- Stage 6 + EED-v0 + 7B validator PASS.

## 16. 7C handoff

7C — Günlük English bileşeni; EED-v0 diagnostic frontier + TECP-v0 band/profile metadata + adaptive planner capacity/priority contracts üzerinde daily cadence, task mix ve pause/resume behavior tasarlayacaktır. 7C fresh PRE + kullanıcı açık onayı olmadan yürütülmez."""
if new_tail not in handoff:
    if old_tail not in handoff:
        raise RuntimeError("HANDOFF 7B tail missing")
    handoff = handoff.replace(old_tail, new_tail, 1)
write("docs/HANDOFF_STATE.md", handoff)

# 8) PROJECT_CONTEXT English + current execution.
context = read("PROJECT_CONTEXT.md")
english_marker = "Canonical: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`."
english_add = """

### 8.2 7B Technical English CEFR Progression — TECP-v0 / D-064

D01 15 Skill context-only CEFR-aligned Technical English progression metadata aldı: 5 A1 + 5 A2 + 5 B1 base anchor; 4 canonical Skill bounded B2+ professional evidence-depth extension taşıyor. CEFR alignment mastery/certification/general-English level değildir; source of truth exact Skill/Objective evidence'dır. `review.6c.english.cefr_alignment` resolved edildi.

Canonical: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.
"""
if "### 8.2 7B Technical English CEFR Progression" not in context:
    if english_marker not in context:
        raise RuntimeError("PROJECT_CONTEXT English marker missing")
    context = context.replace(english_marker, english_marker + english_add, 1)
context = context.replace(
    "- **7B 🟡 A1/A2/B1/B2+ teknik hedefleri — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 7C–20 ⬜",
    "- **7B ✅ TECP-v0 / D-064 — A1/A2/B1/B2+ technical progression tamamlandı**\n- **7C 🟡 Günlük English bileşeni — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 7D–20 ⬜",
)
context = context.replace(
    "**Sıradaki numaralı çalışma 7B'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
    "**Sıradaki numaralı çalışma 7C'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
)
write("PROJECT_CONTEXT.md", context)

# 9) START_HERE decision, stage map, read order and current tail.
start = read("docs/START_HERE.md")
if "### D-064 — TECP-v0" not in start:
    marker = "### D-063 — EED-v0\n7A final `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`; D01 15 Skill / 15 Objective / 16 hard edge için prerequisite-aware granular entry diagnostic, 15 task family ve no-premature-CEFR guard'ı kilitlendi."
    addition = marker + "\n\n### D-064 — TECP-v0\n7B final `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`; D01 15 Skill'i 5 A1 + 5 A2 + 5 B1 context-only Technical English anchor'a bağlar, 4 bounded B2+ professional extension tanımlar ve CEFR mastery/certification overclaim'ini yasaklar."
    if marker not in start:
        raise RuntimeError("START_HERE D-063 marker missing")
    start = start.replace(marker, addition, 1)
start = start.replace(
    "- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B 🟡 active-not-executed**",
    "- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B ✅ TECP-v0 / D-064; 7C 🟡 active-not-executed**",
)
start = start.replace(
    "11u. `curriculum/english/7a_entry_diagnostic/qa_report.yaml`\n12. `docs/LEARNING_ENGINE_SPEC.md`",
    "11u. `curriculum/english/7a_entry_diagnostic/qa_report.yaml`\n11v. `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`\n11w. `curriculum/english/7b_cefr_progression/alignment.yaml`\n11x. `curriculum/english/7b_cefr_progression/qa_report.yaml`\n12. `docs/LEARNING_ENGINE_SPEC.md`",
)
start_tail = """## 8. Tamamlanan çekirdek

- AŞAMA 1 ✅ Product framing
- AŞAMA 2 ✅ GRE-v0 + RVR-v0 learning/mastery
- AŞAMA 3 ✅ Adaptive planner — 16/16 scenarios, 20/20 invariants
- AŞAMA 4 ✅ DMA/WBA/MCA/QAB/AIV assessment system
- AŞAMA 5 ✅ PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6 ✅ GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / **S6ERQA-v0 / D-062**
- AŞAMA 7: **7A ✅ EED-v0 / D-063**, **7B ✅ TECP-v0 / D-064**, **7C 🟡 active-not-executed**

7B final: **15 D01 Skill = 5 A1 / 5 A2 / 5 B1**, 16/16 band-monotonic English hard edge, 4 bounded B2+ professional extension; CEFR metadata mastery/certification değildir.

## 9. Güncel çalışma konumu

**Son tamamlanan:** **`7B — TECP-v0 / D-064`**  
**Aktif:** **`7C — Günlük English bileşeni`**  
**7C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. 7B, TECP-v0 / D-064 ile tamamlandı: 15 D01 Skill = 5 A1 + 5 A2 + 5 B1; 16/16 band-monotonic English hard edge; 4 bounded B2+ extension; CEFR alignment mastery/certification değildir. Aktif step 7C — Günlük English bileşeni; 7C henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`
"""
start, n = re.subn(r"## 8\. Tamamlanan çekirdek\n.*\Z", start_tail, start, count=1, flags=re.S)
if n != 1 and start_tail not in start:
    raise RuntimeError("START_HERE current tail missing")
write("docs/START_HERE.md", start)

# 10) AGENTS volatile current state.
agents = read("AGENTS.md")
agents_old = """- 7A: ✅ `EED-v0 / D-063` tamamlandı — D01 15 Skill / 15 Objective / 16 English hard edge / 15 diagnostic task family.
- **Aktif adım: 7B — A1/A2/B1/B2+ teknik hedefleri.**
- **7B henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 8–20 bekliyor.

**7B'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7B için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula."""
agents_new = """- 7A: ✅ `EED-v0 / D-063` tamamlandı — D01 15 Skill / 15 Objective / 16 English hard edge / 15 diagnostic task family.
- 7B: ✅ `TECP-v0 / D-064` tamamlandı — 15 Skill = 5 A1 + 5 A2 + 5 B1; 4 bounded B2+ extension; CEFR review resolved.
- **Aktif adım: 7C — Günlük English bileşeni.**
- **7C henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 8–20 bekliyor.

**7C'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7C için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula."""
if agents_new not in agents:
    if agents_old not in agents:
        raise RuntimeError("AGENTS current block missing")
    agents = agents.replace(agents_old, agents_new, 1)
write("AGENTS.md", agents)

# 11) Local manager handoff current-state text + addendum.
local = read("docs/LOCAL_MANAGER_HANDOFF.md")
local = local.replace("7A completed / 7B active-not-executed", "7A–7B completed / 7C active-not-executed")
local = local.replace("7B active-not-executed", "7B completed / 7C active-not-executed")
local = local.replace("7B için **ayrı bir fresh PRE-STEP GitHub refresh** yap", "7C için **ayrı bir fresh PRE-STEP GitHub refresh** yap")
local = local.replace("Aktif adım 7B", "Son tamamlanan adım 7B; aktif adım 7C")
if "## 7B completion addendum — D-064" not in local:
    local += """

---

## 7B completion addendum — D-064

7B `TECP-v0 — Technical English CEFR Progression` ile tamamlandı. Canonical: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`; dataset/QA: `curriculum/english/7b_cefr_progression/`. D01 15 Skill = 5 A1 + 5 A2 + 5 B1 context-only anchor; 16/16 English hard edge band-monotonic; 4 bounded B2+ professional extension. CEFR metadata mastery/certification değildir. `review.6c.english.cefr_alignment` resolved. Current active numbered step 7C'dir; fresh PRE + kullanıcı açık onayı gerekir.
"""
write("docs/LOCAL_MANAGER_HANDOFF.md", local)

# 12) Vault current context and open loops.
vctx = read("vault/agent/CURRENT_CONTEXT.md")
if "TECP-v0" not in vctx:
    marker = "- [[docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC|EED-v0]]: D01 Technical English için prerequisite-aware granular giriş diagnostic'i; CEFR alignment 7B'ye pending."
    if marker not in vctx:
        raise RuntimeError("CURRENT_CONTEXT EED marker missing")
    vctx = vctx.replace(marker, marker + "\n- `TECP-v0 / D-064`: D01 CEFR-aligned Technical English progression; 5 A1 + 5 A2 + 5 B1 base anchors, 4 bounded B2+ extension; CEFR != mastery/certification.", 1)
vctx = re.sub(
    r"AŞAMA 6 tamamen tamamlandı\. 7A da tamamlandı\..*?Bu snapshot'tan daha güncel veya çelişen bir iddia varsa",
    "AŞAMA 6 ve 7A–7B tamamlandı. Son tamamlanan adım **7B — TECP-v0 / D-064**. 7B final: 15 D01 Skill = 5 A1 + 5 A2 + 5 B1; 16/16 band-monotonic English hard edge; 4 bounded B2+ extension. Aktif adım **7C — Günlük English bileşeni**; henüz yürütülmedi. 7C başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.\n\nBu snapshot'tan daha güncel veya çelişen bir iddia varsa",
    vctx,
    count=1,
    flags=re.S,
)
write("vault/agent/CURRENT_CONTEXT.md", vctx)

loops = read("vault/agent/OPEN_LOOPS.md")
loops = loops.replace(
    "- [ ] 7B CEFR + technical progression alignment **AKTİF**: `review.6c.english.cefr_alignment` çözümü ve A1/A2/B1/B2+ technical target metadata.",
    "- [x] 7B CEFR + technical progression alignment: TECP-v0 / D-064 ile tamamlandı; `review.6c.english.cefr_alignment` resolved.\n- [ ] 7C Daily English component **AKTİF**: cadence, task mix, planner capacity ve pause/resume behavior tasarımı.",
)
write("vault/agent/OPEN_LOOPS.md", loops)

print("7B_POST_FINALIZER=PASS")
