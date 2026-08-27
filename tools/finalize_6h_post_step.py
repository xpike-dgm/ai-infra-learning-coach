from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
TODAY = "2026-08-27"
MODEL = "S6ERQA-v0"
DECISION = "D-062"


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str, required: bool = True) -> str:
    count = text.count(old)
    if count == 0:
        if required:
            raise RuntimeError(f"missing replacement anchor: {label}")
        return text
    return text.replace(old, new, 1)


def regex_once(text: str, pattern: str, repl: str, label: str, flags: int = 0, required: bool = True) -> str:
    out, count = re.subn(pattern, repl, text, count=1, flags=flags)
    if count == 0 and required:
        raise RuntimeError(f"missing regex anchor: {label}")
    return out


# AGENTS.md — bootstrap must point to the next not-executed step.
path = "AGENTS.md"
text = read(path)
block = """## Güncel execution state

- AŞAMA 1–5: tamamlandı.
- AŞAMA 6: ✅ tamamlandı — `GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / S6ERQA-v0`.
- 6H final external Research QA: ✅ `S6ERQA-v0 / D-062`; 549 Skill / 608 Objective / 950 prerequisite edge; 549/549 hard DAG; 10/10 6H review resolved.
- **Aktif adım: 7A — İngilizce başlangıç ölçümü.**
- **7A henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 8–20 bekliyor.

**7A'yı bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7A için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.

"""
text = regex_once(text, r"## Güncel execution state\n.*?(?=## Ana ürün ilkesi)", block, "AGENTS current state", flags=re.S)
write(path, text)

# EXECUTION_INDEX.md
path = "docs/EXECUTION_INDEX.md"
text = read(path)
if "D-062: 6H final" not in text:
    text = replace_once(
        text,
        "- D-059: 6E final GPU / ML / Inference detailed map `GIM-v0`; D14–D22 package + 6C/6D reuse + hard/soft Pass-B + combined hard-graph QA.\n",
        "- D-059: 6E final GPU / ML / Inference detailed map `GIM-v0`; D14–D22 package + 6C/6D reuse + hard/soft Pass-B + combined hard-graph QA.\n"
        "- D-060: 6F final Professional Engineering / Projects map `PEM-v0`; D23 professional/OSS/project-capstone layer.\n"
        "- D-061: 6G final Weakness Localization & Remediation Map `WLRM-v0`; Objective-first weakness/remediation overlay.\n"
        "- D-062: 6H final Stage 6 External Research QA `S6ERQA-v0`; 3 independent evaluator reconciliation + corrective graph/freshness/evidence patch.\n",
        "execution decision list",
    )
text = replace_once(
    text,
    "- [ ] **6H — Coverage / prerequisite / Research QA** **AKTİF** — eksik/duplicate/hidden prerequisite audit + bağımsız Research AI doğrulaması",
    "- [x] **6H — Coverage / prerequisite / Research QA** — `docs/STAGE6_EXTERNAL_RESEARCH_QA.md` — S6ERQA-v0 / D-062; 549 Skill / 608 Objective / 950 edge; 549/549 DAG; 10/10 review resolved",
    "execution 6H row",
)
text = replace_once(text, "- [ ] **7A — Başlangıç ölçümü**", "- [ ] **7A — Başlangıç ölçümü** **AKTİF**", "execution 7A active")
write(path, text)

# STEP_STATUS.md — compact current-state file.
path = "docs/STEP_STATUS.md"
text = read(path)
text = text.replace(
    "| **6G — Weakness localization + remediation mapping** | ✅ | WLRM-v0 / D-061. 543 Skill + 590 Objective exact weakness/remediation overlay ve 590 route tamamlandı. |",
    "| **6G — Weakness localization + remediation mapping** | ✅ | WLRM-v0 / D-061. Final 6H-patched registry için 549 Skill + 608 Objective exact weakness/remediation coverage. |",
)
text = replace_once(
    text,
    "| **6H — Coverage / prerequisite / Research QA** | 🟡 Aktif | AŞAMA 6 bağımsız external coverage/current-industry/hidden-prerequisite QA. **Henüz yürütülmedi.** |",
    "| **6H — Coverage / prerequisite / Research QA** | ✅ | S6ERQA-v0 / D-062. 3 bağımsız evaluator reconcile edildi; 6 stable Skill + freshness/evidence patch; 549/549 hard DAG; 10/10 review resolved. |",
    "step status 6H row",
)
text = replace_once(
    text,
    "| **7–20** | ⬜ Bekliyor | 6H sonrası canonical sırada. |",
    "| **7A — İngilizce başlangıç ölçümü** | 🟡 Aktif | Sıradaki canonical numbered step; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7B–20** | ⬜ Bekliyor | 7A sonrası canonical sırada. |",
    "step status next rows",
)
new_tail = """## Son tamamlanan numaralı adım — 6H

**Final:** `S6ERQA-v0 — Stage 6 External Research QA` / D-062.  
**Ana çıktı:** `docs/STAGE6_EXTERNAL_RESEARCH_QA.md` + `research/6h_external_research_ai_report.md` + `curriculum/decomposition/6h_research_qa/`.

6H sonucu:
- 3 bağımsız evaluator: PASS WITH REQUIRED CHANGES,
- corrective reconciliation sonrası 549 Skill / 608 Objective / 950 prerequisite edge,
- combined hard graph 549/549 DAG,
- 6 yeni stable capability: NUMA locality/affinity, CUDA async data pipeline, speculative decoding trade-off, prefill/decode disaggregation, MoE routing, expert parallel sharding,
- fast-moving vendor/tool ayrıntıları version-scoped Objective/example olarak tutuldu,
- WLRM 549/608 exact coverage,
- 10/10 6H-owned review resolved,
- final external reconciliation QA PASS.

## Aktif adım — 7A İngilizce başlangıç ölçümü

**7A henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
text = regex_once(text, r"## Son tamamlanan numaralı adım — 6G\n.*\Z", new_tail, "step status tail", flags=re.S)
write(path, text)

# PROJECT_CONTEXT.md
path = "PROJECT_CONTEXT.md"
text = read(path)
text = text.replace("AŞAMA 6, Technical English'ten AI Infrastructure ve professional capstone'a kadar bütün rotayı ölçülebilir alt Skill/Objective haritasına bölecektir.", "AŞAMA 6, Technical English'ten AI Infrastructure ve professional capstone'a kadar bütün rotayı ölçülebilir alt Skill/Objective haritasına böldü ve S6ERQA-v0 / D-062 ile bağımsız external Research QA'dan geçti.")
for phrase in [
    "External validation 6H'ye pending.",
    "External validation 6H'ye pending",
    "External validation 6H'ye pending.",
]:
    text = text.replace(phrase, "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
text = text.replace("External validation 6H'ye pending", "External validation S6ERQA-v0 / D-062 ile tamamlandı")
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
# Exact recurring wording in this snapshot.
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
text = text.replace("External validation 6H'ye pending", "External validation S6ERQA-v0 / D-062 ile tamamlandı")
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
# Replace known package snapshot suffixes regardless of capitalization.
text = re.sub(r"External validation 6H'ye pending\.?", "External validation S6ERQA-v0 / D-062 ile tamamlandı.", text, flags=re.I)
text = re.sub(r"External validation 6H'ye pending", "External validation S6ERQA-v0 / D-062 ile tamamlandı", text, flags=re.I)
text = re.sub(r"External validation 6H'ye pending", "External validation S6ERQA-v0 / D-062 ile tamamlandı", text, flags=re.I)
# Actual lines use 'External validation 6H'ye pending' and one 'External validation'. Also cover 'External validation 6H'ye pending.'
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
text = text.replace("External validation 6H'ye pending", "External validation S6ERQA-v0 / D-062 ile tamamlandı")
# Replace literal lower-case variants used in map paragraphs.
text = text.replace("External validation 6H'ye pending.", "External validation S6ERQA-v0 / D-062 ile tamamlandı.")
text = text.replace("External validation 6H'ye pending", "External validation S6ERQA-v0 / D-062 ile tamamlandı")
# The current file uses `External validation 6H'ye pending.` exactly for several packages and `External validation` can be lower-case after punctuation.
text = re.sub(r"External validation 6H'ye pending\.?", "External validation S6ERQA-v0 / D-062 ile tamamlandı.", text)
text = re.sub(r"External validation 6H'ye pending", "External validation S6ERQA-v0 / D-062 ile tamamlandı", text)
# Replace explicit current-state section.
current = """## 11. Güncel yürütme konumu

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
- **7A 🟡 İngilizce başlangıç ölçümü — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
- 7B–20 ⬜

Final Stage 6 graph: **549 Skill / 608 Objective / 950 prerequisite edge / 549/549 hard DAG**. WLRM final registry coverage 549/608; 10/10 6H review resolved.

**Sıradaki numaralı çalışma 7A'dır.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.

"""
text = regex_once(text, r"## 11\. Güncel yürütme konumu\n.*?(?=## 12\.)", current, "project context current state", flags=re.S)
if "**D-062 / S6ERQA-v0:**" not in text:
    insertion = "\n**D-062 / S6ERQA-v0:** canonical `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`. Üç bağımsız external evaluator reconcile edildi; corrective patch sonrası Stage 6 final registry 549 Skill / 608 Objective / 950 prerequisite edge, hard DAG 549/549 ve WLRM exact coverage 549/608. 10/10 6H review resolved.\n"
    text = text.replace("\n## 8. İngilizce\n", insertion + "\n## 8. İngilizce\n")
write(path, text)

# HANDOFF_STATE.md — collapse the volatile tail into current final state while preserving historical earlier summaries.
path = "docs/HANDOFF_STATE.md"
text = read(path)
new_tail = """## 10. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6 ✅ — GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / S6ERQA-v0
- AŞAMA 7A 🟡 active-not-executed
- 7B–20 ⬜

## 11. Güncel kesin konum

**Son tamamlanan:** `6H — S6ERQA-v0 / D-062`  
**Aktif:** `7A — İngilizce başlangıç ölçümü`  
**7A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

## 12. D-061 / 6G final özeti — external QA sonrası

Canonical summary: `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`.  
Canonical dataset: `curriculum/decomposition/6g_weakness_remediation/`.

WLRM-v0 final Stage 6 registry'de:
- 549 Skill / 608 Objective exact coverage,
- 608 Objective-specific remediation route,
- invalid/prerequisite-contaminated false-negative guard,
- assisted/provisional evidence ceiling,
- first post-mastery contradiction `verification_due`,
- broad reset / project broadcast guard,
- fresh H0/direct/verified closure,
- 6H ile guidance fading + mastered-target reverification-first + AI-scaffold-not-closure guard'ları eklendi.

## 13. D-062 / 6H final özeti

Canonical: `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`.  
Reconciliation: `research/6h_external_research_ai_report.md`.  
Dataset/QA: `curriculum/decomposition/6h_research_qa/`.

S6ERQA-v0:
- üç bağımsız evaluator başlangıç snapshot'ına `PASS WITH REQUIRED CHANGES` verdi,
- canonical reconciliation 6 stable capability ekledi: NUMA locality/affinity, CUDA async data pipeline, speculative decoding trade-off, prefill/decode disaggregation, MoE routing, expert parallel sharding,
- tool/vendor/model-specific fast-moving ayrıntılar stable Skill şişirmeden version-scoped Objective/example olarak tutuldu,
- final Stage 6 = 23 route / 549 Skill / 608 Objective / 950 prerequisite edge,
- combined hard graph 549/549 DAG,
- WLRM 549/608 exact coverage,
- 10/10 6H-owned review resolved,
- `tools/validate_6h_external_reconciliation.py` PASS.

## 14. 7A handoff

7A — İngilizce başlangıç ölçümü, AŞAMA 7'nin ilk numbered step'idir. Stage 6'nın granular Technical English Skills'i input olarak kullanır; exact başlangıç-placement/measurement davranışı 7A'da fresh PRE sonrası tasarlanır. Stage 7 ilerlemesi kullanıcı onayı olmadan başlatılmaz.
"""
text = regex_once(text, r"## 10\. Tamamlanan aşamalar\n.*\Z", new_tail, "handoff volatile tail", flags=re.S)
# Historical lines saying external QA was pending are retained only in older model-completion sections; make their historical nature explicit.
text = text.replace("6H external Research QA pending; learner-published değil.", "6C/6D completion anında 6H external Research QA pending idi; D-062 ile daha sonra external validation tamamlandı; production content yine AŞAMA 15/20 kapsamındadır.")
text = text.replace("external Research QA 6H'ye pending.", "6F completion anında external Research QA 6H'ye pending idi; D-062 ile tamamlandı.")
write(path, text)

# START_HERE.md
path = "docs/START_HERE.md"
text = read(path)
if "### D-061 — WLRM-v0" not in text:
    anchor = "### D-060 — PEM-v0\n6F final `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md` + `curriculum/decomposition/6f_professional_engineering/` package'ı D23 professional engineering/OSS/project-capstone layer'ını 76 Skill / 87 Objective seviyesine ayırdı; prior technical capability'ler clone edilmeden reuse edildi ve 6D/6E professional overlay review'ları kapatıldı.\n"
    addition = anchor + "\n### D-061 — WLRM-v0\n6G final `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/` Objective-first weakness/remediation modelidir; 6H patch sonrası final registry için 549 Skill / 608 Objective exact coverage taşır.\n\n### D-062 — S6ERQA-v0\n6H final `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`; üç bağımsız evaluator reconcile edildi, corrective patch sonrası Stage 6 549 Skill / 608 Objective / 950 edge ve 549/549 hard DAG ile external Research QA PASS oldu.\n"
    text = replace_once(text, anchor, addition, "start here decision additions")
stage_mapping = """## 4. Güncel stage mapping
- 1 Product framing ✅
- 2 Learning/mastery ✅
- 3 Adaptive planner ✅
- 4 Assessment system ✅
- 5 Curriculum/knowledge graph backbone ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- 6 Granular Capability Map ✅ — S6ERQA-v0 / D-062
  - 6A ✅ GNS-v0 / D-054
  - 6B ✅ FRDB-v0 / D-056
  - 6C ✅ FDM-v0 / D-057
  - 6D ✅ SDM-v0 / D-058
  - 6E ✅ GIM-v0 / D-059
  - 6F ✅ PEM-v0 / D-060
  - 6G ✅ WLRM-v0 / D-061
  - 6H ✅ S6ERQA-v0 / D-062
- 7 English parallel line — **7A 🟡 active-not-executed**
- 8 UX
- 9 Architecture/data model
- 10 Mobile skeleton
- 11 Daily learning MVP
- 12 Mastery/planner implementation
- 13 Assessment/retention/remediation implementation
- 14 AI Tutor/evaluation
- 15 First 8–12 week production content
- 16 Analytics/settings
- 17 Polish/accessibility
- 18 Pilot/calibration/QA
- 19 Release APK
- 20 Full professional curriculum/career/capstones

"""
text = regex_once(text, r"## 4\. Güncel stage mapping\n.*?(?=## 5\.)", stage_mapping, "start here stage mapping", flags=re.S)
# Add final 6H canonical docs to reading list.
if "docs/STAGE6_EXTERNAL_RESEARCH_QA.md" not in text[text.find("## 6. Yeni sohbet/agent okuma sırası"):]:
    text = text.replace("11o. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`", "11o. `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`\n11p. `research/6h_external_research_ai_report.md`\n11q. `curriculum/decomposition/6h_research_qa/final_qa_report.yaml`\n11r. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`")
write(path, text)

# MASTER_PLAN.md
path = "docs/MASTER_PLAN.md"
text = read(path)
text = text.replace("- 543 accepted Skill + 590 accepted Objective exact overlay coverage,", "- final 6H-patched registry için 549 accepted Skill + 608 accepted Objective exact overlay coverage,")
text = text.replace("- 590 Objective remediation route,", "- 608 Objective remediation route,")
text = text.replace("- internal QA PASS, 0 blocking; independent external Research QA 6H'ye pending.", "- internal QA PASS; D-062 external Research QA sonrası final registry coverage 549/608 ve 6H external behavior review resolved.")
completion = """### [x] 6H — Coverage / prerequisite / external Research QA — S6ERQA-v0 / D-062
**Final:** `docs/STAGE6_EXTERNAL_RESEARCH_QA.md` + `research/6h_external_research_ai_report.md` + `curriculum/decomposition/6h_research_qa/`

- 3 independent external evaluator → `PASS WITH REQUIRED CHANGES`,
- 6 stable capability addition: NUMA locality/affinity, CUDA async data movement pipeline, speculative decoding trade-off, prefill/decode disaggregation, MoE routing, expert parallel sharding,
- hidden prerequisite + DRA/runtime/freshness + D23/WLRM corrective patch applied,
- vendor/model-specific fast-moving details kept as version-scoped Objective/example unless GNS-v0 independence test passes,
- final Stage 6 = 23 route / 549 Skill / 608 Objective / 950 prerequisite edge,
- combined hard graph 549/549 DAG,
- WLRM exact coverage 549 Skill / 608 Objective,
- 10/10 6H-owned reviews resolved,
- historical 6C–6G regressions + final external reconciliation validator PASS,
- Stage 6 complete; next step 7A.
"""
text = replace_once(text, "### [ ] 6H — Coverage / prerequisite / external Research QA — **AKTİF**", completion, "master plan 6H")
text = replace_once(text, "### [ ] 7A — Başlangıç ölçümü", "### [ ] 7A — Başlangıç ölçümü — **AKTİF**", "master plan 7A")
write(path, text)

# DECISIONS.md — append only once.
path = "docs/DECISIONS.md"
text = read(path)
if "## D-062 — Stage 6 External Research QA = S6ERQA-v0" not in text:
    text = text.rstrip() + """

## D-062 — Stage 6 External Research QA = S6ERQA-v0
**Durum:** Kabul edildi — 2026-08-27

- 6H final modeli `S6ERQA-v0 — Stage 6 External Research QA` oldu.
- Canonical çıktı `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`; external evaluator reconciliation `research/6h_external_research_ai_report.md`.
- Üç bağımsız evaluator Stage 6 başlangıç snapshot'ına `PASS WITH REQUIRED CHANGES` verdi; öneriler GNS-v0/KGC-v0/PRG-v0 ile manager tarafından bağımsız reconcile edildi.
- Stable capability independence testini geçen 6 yeni Skill eklendi: NUMA locality/affinity, CUDA async data movement pipeline, speculative decoding trade-off, prefill/decode disaggregation, MoE routing dataflow, expert-parallel sharding.
- Tool/vendor/model özel fast-moving ayrıntılar (ör. DRA API isimleri, CUDA/Triton current mechanisms, MLA/MTP/DualPipe/FlashInfer/NVFP4/CUTLASS/CuTe) otomatik canonical Skill yapılmaz; ayrı learner-state testi geçmiyorsa version-scoped Objective/example/freshness metadata olarak tutulur.
- Final Stage 6 registry: 23 route family / 549 Skill / 608 Objective / 950 prerequisite edge; combined hard graph 549/549 DAG.
- WLRM final registry'nin 549 Skill / 608 Objective'ini exact kapsar; guidance fading + mastered-target reverification-first + AI-scaffold-not-closure guard'ları eklenmiştir.
- D23 final readiness global capstone pass ile component mastery vermez; ayrı attributable component evidence, en az üç materially distinct evidence family ve operations/failure evidence ister.
- 10/10 6H-owned review resolved; `tools/validate_6h_external_reconciliation.py` final PASS.
- AŞAMA 6 tamamlandı. Sonraki numbered step `7A — İngilizce başlangıç ölçümü`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.
""" + "\n"
write(path, text)

# PROGRESS_LOG.md — append historical record.
path = "docs/PROGRESS_LOG.md"
text = read(path)
if "## 2026-08-27 — 6H S6ERQA-v0" not in text:
    text = text.rstrip() + """

## 2026-08-27 — 6H S6ERQA-v0 / D-062 tamamlandı

- Fresh PRE-STEP sonrası kullanıcı onayıyla 6H yürütüldü.
- Internal structural preflight 23/23 route, 543/590 başlangıç registry ve hard DAG üzerinde PASS verdi.
- D-016 gereği manager araştırmasından bağımsız üç external Research AI evaluator kullanıldı; üçü de `PASS WITH REQUIRED CHANGES` verdi.
- External reports canonical IDs, source quality, freshness ve GNS-v0 granularity kurallarıyla reconcile edildi; vendor/model özel öneriler otomatik stable Skill yapılmadı.
- 6 stable Skill eklendi; hidden prerequisite/freshness/professional-evidence/WLRM safety patch uygulandı.
- Final registry: 549 Skill / 608 Objective / 950 prerequisite edge; hard DAG 549/549.
- WLRM patched registry üzerinde regenerate edildi: 549 Skill / 608 Objective / 608 route exact coverage.
- 10/10 6H-owned review resolved.
- 6C–6F historical validators PASS, 6G historical validator PASS, structural QA PASS, final `validate_6h_external_reconciliation.py` PASS.
- Canonical model: `S6ERQA-v0 / D-062`; Stage 6 kapandı, 7A active-not-executed oldu.
""" + "\n"
write(path, text)

# vault current context
path = "vault/agent/CURRENT_CONTEXT.md"
text = read(path)
if "S6ERQA-v0" not in text:
    text = text.replace("- [[vault/wiki/sources/Weakness Remediation Map Source|WLRM-v0]]: accepted 6C–6F registry için Objective-first weakness localization + remediation overlay tamamlandı.", "- [[vault/wiki/sources/Weakness Remediation Map Source|WLRM-v0]]: final Stage 6 registry için Objective-first weakness localization + remediation overlay tamamlandı.\n- `S6ERQA-v0 / D-062`: Stage 6 bağımsız external Research QA tamamlandı; canonical `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`.")
text = regex_once(text, r"## Exact execution state\n\n.*?(?=\nBu snapshot)", "## Exact execution state\n\nAŞAMA 6 tamamen tamamlandı. Son tamamlanan adım **6H — S6ERQA-v0 / D-062**. Final Stage 6: 549 Skill / 608 Objective / 950 edge; hard DAG 549/549; 10/10 6H review resolved. Aktif adım **7A — İngilizce başlangıç ölçümü**; henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.\n", "vault current state", flags=re.S)
write(path, text)

# LOCAL_MANAGER_HANDOFF — targeted bootstrap/state corrections; preserve long stable project explanation.
path = "docs/LOCAL_MANAGER_HANDOFF.md"
text = read(path)
text = text.replace("current execution state'in `6A–6G completed / 6H active-not-executed` olduğunu doğrula.", "current execution state'in `6A–6H completed / 7A active-not-executed` olduğunu doğrula.")
text = text.replace("Ancak bundan sonra, 6G için **ayrı bir fresh PRE-STEP GitHub refresh** yap.", "Ancak bundan sonra, 7A için **ayrı bir fresh PRE-STEP GitHub refresh** yap; kullanıcı açık onayı olmadan yürütme.")
text = text.replace("6H external Research QA pending", "6H external Research QA D-062 ile tamamlandı")
text = text.replace("6H'de zorunlu", "6H'de zorunluydu ve D-062 ile tamamlandı")
# Append current handoff marker if absent.
if "## Current execution addendum — D-062" not in text:
    text = text.rstrip() + """

## Current execution addendum — D-062 / 2026-08-27

- AŞAMA 6 ✅ tamamen tamamlandı; 6H final `S6ERQA-v0 / D-062`.
- Final Stage 6 graph: 23 route / 549 Skill / 608 Objective / 950 prerequisite edge / 549/549 hard DAG.
- WLRM final coverage: 549 Skill / 608 Objective; 10/10 6H review resolved.
- Canonical 6H: `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`.
- Aktif numbered step: **7A — İngilizce başlangıç ölçümü**, active-not-executed.
- 7A başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""" + "\n"
write(path, text)

# GRANULAR_CAPABILITY_MAP_PLAN is the Stage 6 charter; mark its final QA step complete if exact anchor exists.
path = "docs/GRANULAR_CAPABILITY_MAP_PLAN.md"
text = read(path)
text = text.replace("### [ ] 6H", "### [x] 6H")
if "S6ERQA-v0 / D-062" not in text:
    text = text.rstrip() + "\n\n## 6H completion addendum — S6ERQA-v0 / D-062\n\nStage 6 external Research QA üç bağımsız evaluator + canonical reconciliation ile tamamlandı. Final registry 549 Skill / 608 Objective / 950 prerequisite edge; hard DAG 549/549; 10/10 6H review resolved. Canonical: `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`.\n"
write(path, text)

# Final 6H machine-readable status/report.
status_path = ROOT / "curriculum/decomposition/6h_research_qa/status.yaml"
status = {
    "stage_step": "6H",
    "status": "PASS_EXTERNAL_RESEARCH_RECONCILED",
    "stage6_complete": True,
    "model": "S6ERQA-v0",
    "decision": "D-062",
    "checked_at": TODAY,
    "independent_external_research_ai": {"status": "complete", "evaluator_count": 3, "satisfies_d016": True},
    "final_counts": {"route_families": 23, "skills": 549, "objectives": 608, "prerequisite_edges": 950, "hard_dag_nodes": "549/549", "resolved_6h_reviews": "10/10"},
    "canonical_summary": "docs/STAGE6_EXTERNAL_RESEARCH_QA.md",
    "reconciliation_report": "research/6h_external_research_ai_report.md",
    "next_step": {"step": "7A", "status": "active_not_executed", "requires_fresh_pre_and_user_approval": True},
}
status_path.write_text(yaml.safe_dump(status, sort_keys=False, allow_unicode=True, width=120), encoding="utf-8")

final_report = {
    "stage_step": "6H",
    "model": "S6ERQA-v0",
    "decision": "D-062",
    "result": "PASS",
    "external_verdict_before_correction": "PASS_WITH_REQUIRED_CHANGES",
    "independent_evaluators": 3,
    "counts": {"route_families": 23, "skills": 549, "objectives": 608, "prerequisite_edges": 950, "hard_dag_nodes": 549, "hard_dag_total_nodes": 549, "wlrm_skill_coverage": 549, "wlrm_objective_coverage": 608, "resolved_6h_reviews": 10},
    "stable_skill_additions": [
        "skill.os.numa_locality_affinity",
        "skill.cuda.async_data_movement_pipeline",
        "skill.optimization.speculative_decoding_tradeoff",
        "skill.serving.prefill_decode_disaggregation",
        "skill.ml.moe_routing_dataflow",
        "skill.multi_gpu.expert_parallel_sharding",
    ],
    "qa": {"historical_6c_6f_regressions": "PASS", "historical_6g_regression": "PASS", "combined_structural": "PASS", "external_reconciliation_validator": "PASS", "review_closure": "10/10"},
    "canonical_summary": "docs/STAGE6_EXTERNAL_RESEARCH_QA.md",
    "external_reconciliation": "research/6h_external_research_ai_report.md",
}
(ROOT / "curriculum/decomposition/6h_research_qa/final_qa_report.yaml").write_text(yaml.safe_dump(final_report, sort_keys=False, allow_unicode=True, width=120), encoding="utf-8")

print("6H_POST_FINALIZER=PASS")
