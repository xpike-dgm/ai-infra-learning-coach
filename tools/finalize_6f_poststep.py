from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
TODAY = "2026-08-27"
QA = yaml.safe_load((ROOT / "curriculum/decomposition/6f_professional_engineering/qa_report.yaml").read_text(encoding="utf-8"))
C = QA["counts"]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, text: str) -> None:
    p = ROOT / path
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(text, encoding="utf-8")


def replace_once(path: str, old: str, new: str) -> None:
    text = read(path)
    if old not in text:
        raise SystemExit(f"POST_SYNC_FAIL {path}: marker not found: {old[:100]!r}")
    write(path, text.replace(old, new, 1))


def regex_once(path: str, pattern: str, repl: str) -> None:
    text = read(path)
    new, n = re.subn(pattern, repl, text, count=1, flags=re.S)
    if n != 1:
        raise SystemExit(f"POST_SYNC_FAIL {path}: regex matched {n}: {pattern[:100]!r}")
    write(path, new)


def append_once(path: str, marker: str, block: str) -> None:
    text = read(path)
    if marker in text:
        return
    write(path, text.rstrip() + "\n\n" + block.strip() + "\n")


summary = f'''# PEM-v0 — Professional Engineering / Projects Detailed Map

**Adım:** 6F  
**Karar:** D-060  
**Durum:** Internal authoring + deterministic QA complete; external Research QA pending 6H  
**Canonical dataset:** `curriculum/decomposition/6f_professional_engineering/`

## Kapsam
D23 Open Source Contributions + Real Large Projects + Professional Capstones ayrıntılandırıldı. Cross-cutting professional layer; repository/source reading, issue reproduction/change planning, Git/branch/PR/code review, testing/CI quality, build/release/reproducibility, debugging, profiling/benchmark reporting, design docs/RFC, observability/reliability/incident/postmortem, security/operational safety, OSS workflow ve integrated project/capstone evidence davranışlarını kapsar.

## Final sayılar
- {C['domains']} Domain / {C['modules']} Module / {C['topics']} Topic
- {C['skills']} Skill / {C['objectives']} Objective / {C['topic_skill_links']} TopicSkillLink
- {C['prerequisite_edges']} prerequisite edge: {C['hard_prerequisite_edges']} hard / {C['soft_prerequisite_edges']} soft
- {C['cross_package_prerequisite_edges']} cross-package prerequisite edge
- {C['reused_prior_package_skills']} prior Skill reuse: {C['reused_6c_skills']} Foundation + {C['reused_6d_skills']} Systems + {C['reused_6e_skills']} GPU/ML/Inference
- {C['capability_requirements']} capability requirement
- {C['professional_attributions']} professional attribution
- {C['project_attributions']} project/capstone attribution

## Professional overlay sonucu
- Existing D01–D22 technical/testing/build/debug/profiling/observability/reliability capability'leri clone edilmedi; canonical Skill ID reuse edildi.
- 6C `project.foundation.reproducible_cli_tool`, 6D `project.systems.observable_networked_service` ve 6E `project.gpu_inference.integrated_serving_stack` D23 professional workflow Skill'leriyle augment edildi.
- Yeni `project.professional.open_source_contribution` familyası tanımlandı.
- Yeni `capstone.professional.ai_infrastructure_system` final integrated capstone familyası tanımlandı; professional D23 behavior ile accepted technical Skill evidence aynı capstone içinde separately-observable component olarak bağlandı.
- Integrated project/capstone PASS bütün tagged Objective'lere otomatik mastery vermez; component evidence ayrı kalır.

## Review reconciliation
- `review.6d.professional_overlay_reconciliation` 6F tarafından resolved edildi.
- `review.6e.professional_overlay_reconciliation` 6F tarafından resolved edildi.
- 6D'de 2, 6E'de 3 external/freshness/math non-blocking review açık kalır ve 6H'ye aittir.

## QA
- 6C regression validator: PASS
- 6D regression validator: PASS
- 6E regression validator: PASS
- Independent 6F package validator: PASS
- 6C+6D+6E+6F combined hard graph: DAG (validator sonucu)
- Package result: `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`
- Open 6F reviews: 0 blocking / {C['open_non_blocking_reviews']} non-blocking
- Independent external Research QA 6H'de zorunlu; package henüz externally validated / learner-published değildir.
'''
write("docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md", summary)

# DECISIONS
append_once("docs/DECISIONS.md", "## D-060 — Professional engineering / projects detailed map = PEM-v0", f'''## D-060 — Professional engineering / projects detailed map = PEM-v0
**Durum:** Kabul edildi — {TODAY}

- 6F final modeli `PEM-v0 — Professional Engineering / Projects Detailed Map` oldu.
- Canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`; dataset `curriculum/decomposition/6f_professional_engineering/`.
- D23 = {C['domains']} Domain / {C['modules']} Module / {C['topics']} Topic / {C['skills']} Skill / {C['objectives']} Objective / {C['topic_skill_links']} TopicSkillLink.
- {C['prerequisite_edges']} prerequisite edge = {C['hard_prerequisite_edges']} hard / {C['soft_prerequisite_edges']} soft; {C['cross_package_prerequisite_edges']} cross-package edge.
- {C['reused_prior_package_skills']} prior Skill clone'lanmadan reuse edildi: {C['reused_6c_skills']} 6C + {C['reused_6d_skills']} 6D + {C['reused_6e_skills']} 6E.
- Existing foundation/systems/GPU-inference projects D23 professional workflow evidence ile augment edildi; yeni OSS contribution project ve integrated AI-infrastructure capstone familyası tanımlandı.
- `review.6d.professional_overlay_reconciliation` ve `review.6e.professional_overlay_reconciliation` resolved edildi.
- Internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`; 0 blocking / {C['open_non_blocking_reviews']} open 6F non-blocking review. Independent external Research QA 6H'ye pending; learner publication yapılmadı.''')

# PROJECT_CONTEXT
replace_once("PROJECT_CONTEXT.md",
"**D-059 / GIM-v0:** canonical summary `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, dataset `curriculum/decomposition/6e_gpu_ml_inference/`. D14–D22; 9 Domain, 27 Module, 70 Topic, 143 Skill, 159 Objective, 230 TopicSkillLink ve 279 prerequisite edge (247 hard / 32 soft). 59 prior Skill clone'lanmadan reuse edildi; 6C+6D+6E combined hard graph DAG 467/467; 0 blocking review. External validation 6H'ye pending.",
"**D-059 / GIM-v0:** canonical summary `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, dataset `curriculum/decomposition/6e_gpu_ml_inference/`. D14–D22; 9 Domain, 27 Module, 70 Topic, 143 Skill, 159 Objective, 230 TopicSkillLink ve 279 prerequisite edge (247 hard / 32 soft). 59 prior Skill clone'lanmadan reuse edildi; 6C+6D+6E combined hard graph DAG 467/467; 0 blocking review. External validation 6H'ye pending.\n\n**D-060 / PEM-v0:** canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, dataset `curriculum/decomposition/6f_professional_engineering/`. D23; %d Domain, %d Module, %d Topic, %d Skill, %d Objective. Existing D01–D22 technical capability'leri clone edilmeden professional project/capstone context'lerinde reuse edildi; 0 blocking review. External validation 6H'ye pending." % (C['domains'], C['modules'], C['topics'], C['skills'], C['objectives']))
replace_once("PROJECT_CONTEXT.md",
"  - **6F 🟡 Professional engineering / project map — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n  - 6G–6H ⬜",
"  - **6F ✅ PEM-v0 / D-060**\n  - **6G 🟡 Weakness localization + remediation mapping — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n  - 6H ⬜")
replace_once("PROJECT_CONTEXT.md",
"**Sıradaki numaralı çalışma 6F'dir.** 6F başlamadan fresh PRE-STEP GitHub refresh ve açık `review.6d.professional_overlay_reconciliation` girdisinin yeniden okunması zorunludur.",
"**Sıradaki numaralı çalışma 6G'dir.** 6G başlamadan fresh PRE-STEP GitHub refresh; FDM-v0 + SDM-v0 + GIM-v0 + PEM-v0 remediation metadata/review handoff setlerinin yeniden okunması zorunludur.")

# STEP_STATUS current section
replace_once("docs/STEP_STATUS.md",
"| **6F — Professional engineering / project map** | 🟡 Aktif | Testing/build/debug/profiling, OSS workflow, large projects ve capstone capability decomposition. **Henüz yürütülmedi.** |\n| **6G–20** | ⬜ Bekliyor | 6F sonrası canonical sırada. |",
"| **6F — Professional engineering / project map** | ✅ | PEM-v0 / D-060. D23 professional workflow + OSS + project/capstone map tamamlandı. |\n| **6G — Weakness localization + remediation mapping** | 🟡 Aktif | 6C–6F granular graph üzerinde weakness/remediation operational mapping. **Henüz yürütülmedi.** |\n| **6H–20** | ⬜ Bekliyor | 6G sonrası canonical sırada. |")
regex_once("docs/STEP_STATUS.md", r"## Son tamamlanan numaralı adım — 6E.*\Z", f'''## Son tamamlanan numaralı adım — 6F

**Final:** `PEM-v0 — Professional Engineering / Projects Detailed Map` / D-060.  
**Ana çıktı:** `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md` + `curriculum/decomposition/6f_professional_engineering/`.

6F sonucu:
- D23 için {C['domains']} Domain / {C['modules']} Module / {C['topics']} Topic,
- {C['skills']} Skill / {C['objectives']} Objective / {C['topic_skill_links']} TopicSkillLink,
- {C['prerequisite_edges']} prerequisite edge ({C['hard_prerequisite_edges']} hard / {C['soft_prerequisite_edges']} soft),
- {C['reused_prior_package_skills']} prior Skill reuse; 6D ve 6E professional-overlay review'ları resolved,
- Foundation/Systems/GPU-Inference project familyaları professional evidence ile augment edildi; OSS project + integrated AI Infra capstone tanımlandı,
- 0 blocking / {C['open_non_blocking_reviews']} non-blocking 6F review,
- external Research QA 6H'ye pending.

## Aktif adım — 6G Weakness localization + remediation mapping

**6G henüz yürütülmedi.** Fresh PRE-STEP GitHub refresh zorunludur.
''')

# EXECUTION_INDEX
replace_once("docs/EXECUTION_INDEX.md",
"- [ ] **6F — Professional engineering / project map** **AKTİF** — testing/build/debug/profiling, OSS workflow, large projects, capstone capability decomposition\n- [ ] **6G — Weakness localization + remediation mapping** — zayıflığın Skill/Objective düzeyinde ayrı tutulması",
"- [x] **6F — Professional engineering / project map** — `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, `curriculum/decomposition/6f_professional_engineering/` — PEM-v0 / D-060\n- [ ] **6G — Weakness localization + remediation mapping** **AKTİF** — zayıflığın Skill/Objective düzeyinde ayrı tutulması")
append_once("docs/EXECUTION_INDEX.md", "- D-060:", "- D-060: 6F final Professional Engineering / Projects detailed map `PEM-v0`; D23 professional workflow + OSS + integrated project/capstone decomposition ve prior-package professional overlay reconciliation.")
text = read("docs/EXECUTION_INDEX.md")
text = text.replace("**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6E`", "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6F`")
text = text.replace("**Aktif:** **`6F — Professional engineering / project map`**\n\n6E GIM-v0 / D-059 ile tamamlandı. 6F henüz yürütülmedi; 6F başlamadan fresh PRE-STEP GitHub refresh zorunludur.", "**Aktif:** **`6G — Weakness localization + remediation mapping`**\n\n6F PEM-v0 / D-060 ile tamamlandı. 6G henüz yürütülmedi; 6G başlamadan fresh PRE-STEP GitHub refresh zorunludur.")
write("docs/EXECUTION_INDEX.md", text)

# MASTER_PLAN
regex_once("docs/MASTER_PLAN.md", r"### \[ \] 6F — Professional engineering / project map — \*\*AKTİF\*\*.*?(?=### \[ \] 6G — Weakness localization \+ remediation mapping)", f'''### [x] 6F — Professional engineering / project map — PEM-v0 / D-060
**Final:** `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md` + `curriculum/decomposition/6f_professional_engineering/`

- D23 = {C['domains']} Domain / {C['modules']} Module / {C['topics']} Topic,
- {C['skills']} Skill / {C['objectives']} Objective / {C['topic_skill_links']} TopicSkillLink,
- {C['prerequisite_edges']} prerequisite edge = {C['hard_prerequisite_edges']} hard / {C['soft_prerequisite_edges']} soft,
- {C['reused_prior_package_skills']} prior Skill clone'lanmadan reuse edildi,
- Git/PR/code review, testing/CI, build/release, debug/perf, design/RFC, ops/reliability, security, OSS ve project/capstone behavior granularlaştırıldı,
- 6D + 6E professional-overlay review'ları resolved,
- internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`, 0 blocking,
- external Research QA 6H'ye pending; learner publication yapılmadı.

''')
replace_once("docs/MASTER_PLAN.md", "### [ ] 6G — Weakness localization + remediation mapping", "### [ ] 6G — Weakness localization + remediation mapping — **AKTİF**")
append_once("docs/MASTER_PLAN.md", "- D-060:", "- D-060: 6F final Professional Engineering / Projects detailed map `PEM-v0`; canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, dataset `curriculum/decomposition/6f_professional_engineering/`.")
text = read("docs/MASTER_PLAN.md")
text = text.replace("**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6E`\n**Aktif:** **`6F — Professional engineering / project map`**", "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6F`\n**Aktif:** **`6G — Weakness localization + remediation mapping`**")
write("docs/MASTER_PLAN.md", text)

# START_HERE
text = read("docs/START_HERE.md")
if "### D-060 — PEM-v0" not in text:
    anchor = "### D-059 — GIM-v0\n6E final `docs/GPU_ML_INFERENCE_DETAILED_MAP.md` + `curriculum/decomposition/6e_gpu_ml_inference/` package'ı D14–D22'yi 143 Skill / 159 Objective seviyesine ayırdı; 59 prior Skill clone'lanmadan reuse edildi, FRDB hard/soft Pass-B audit PASS ve 6C+6D+6E combined hard graph DAG 467/467."
    if anchor not in text: raise SystemExit("POST_SYNC_FAIL START_HERE D-059 anchor")
    text = text.replace(anchor, anchor + f"\n\n### D-060 — PEM-v0\n6F final `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md` + `curriculum/decomposition/6f_professional_engineering/` package'ı D23 professional engineering/OSS/project-capstone layer'ını {C['skills']} Skill / {C['objectives']} Objective seviyesine ayırdı; prior technical capability'ler clone edilmeden reuse edildi ve 6D/6E professional overlay review'ları kapatıldı.", 1)
text = text.replace("  - 6F 🟡 Professional engineering / project map\n  - 6G–6H ⬜", "  - 6F ✅ PEM-v0 / D-060\n  - 6G 🟡 Weakness localization + remediation mapping\n  - 6H ⬜")
text = text.replace("- 6F 🟡 Professional engineering / project map — aktif, henüz yürütülmedi", "- 6F ✅ `PEM-v0 — Professional Engineering / Projects Detailed Map` / D-060\n- 6G 🟡 Weakness localization + remediation mapping — aktif, henüz yürütülmedi")
text = text.replace("**Aktif:** **`6F — Professional engineering / project map`**\n**6F henüz yürütülmedi.**", "**Aktif:** **`6G — Weakness localization + remediation mapping`**\n**6G henüz yürütülmedi.**")
text = text.replace("6F başlamadan yeni PRE-STEP GitHub refresh zorunlu.", "6G başlamadan yeni PRE-STEP GitHub refresh zorunlu.")
text = text.replace("11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`", "11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`\n11k. `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`\n11l. `curriculum/decomposition/6f_professional_engineering/manifest.yaml`")
write("docs/START_HERE.md", text)

# HANDOFF_STATE current tail + D-060 summary
text = read("docs/HANDOFF_STATE.md")
if "## 9.6 D-060 / 6F final özeti" not in text:
    insert = f'''## 9.6 D-060 / 6F final özeti

Canonical summary: `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`.  
Canonical dataset: `curriculum/decomposition/6f_professional_engineering/`.

PEM-v0:
- D23 için {C['domains']} Domain / {C['modules']} Module / {C['topics']} Topic,
- {C['skills']} Skill / {C['objectives']} Objective / {C['topic_skill_links']} TopicSkillLink,
- {C['prerequisite_edges']} edge ({C['hard_prerequisite_edges']} hard / {C['soft_prerequisite_edges']} soft),
- {C['reused_prior_package_skills']} prior Skill canonical ID ile reuse,
- testing/build/debug/profiling/Git-PR-review/design-doc/ops/security/OSS/project-capstone overlay granularlaştırıldı,
- 6D ve 6E professional-overlay review'ları resolved,
- internal QA PASS_WITH_OPEN_NON_BLOCKING_REVIEWS; external Research QA 6H'ye pending.

'''
    marker = "## 10. Tamamlanan aşamalar"
    if marker not in text: raise SystemExit("POST_SYNC_FAIL HANDOFF section 10 marker")
    text = text.replace(marker, insert + marker, 1)
text = text.replace("  - 6F 🟡 Professional engineering / project map — aktif, henüz yürütülmedi\n  - 6G–6H ⬜", "  - 6F ✅ PEM-v0 / D-060\n  - 6G 🟡 Weakness localization + remediation mapping — aktif, henüz yürütülmedi\n  - 6H ⬜")
text = re.sub(r"## 11\. Güncel kesin konum.*\Z", "## 11. Güncel kesin konum\n\n**Son tamamlanan:** `6F — PEM-v0 / D-060`\n**Aktif:** `6G — Weakness localization + remediation mapping`\n**6G henüz yürütülmedi.**\n", text, flags=re.S)
write("docs/HANDOFF_STATE.md", text)

# AGENTS
replace_once("AGENTS.md", "- AŞAMA 6E: ✅ `GIM-v0 / D-059` tamamlandı.\n- **Aktif adım: 6F — Professional engineering / project map.**\n- **6F henüz yürütülmedi.**\n- 6G–6H ve AŞAMA 7–20 bekliyor.\n\n**6F'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6F için ayrıca fresh PRE-STEP refresh yap.", "- AŞAMA 6E: ✅ `GIM-v0 / D-059` tamamlandı.\n- AŞAMA 6F: ✅ `PEM-v0 / D-060` tamamlandı.\n- **Aktif adım: 6G — Weakness localization + remediation mapping.**\n- **6G henüz yürütülmedi.**\n- 6H ve AŞAMA 7–20 bekliyor.\n\n**6G'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6G için ayrıca fresh PRE-STEP refresh yap.")

# LOCAL_MANAGER_HANDOFF volatile current markers
text = read("docs/LOCAL_MANAGER_HANDOFF.md")
text = text.replace("6A–6E completed / 6F active-not-executed", "6A–6F completed / 6G active-not-executed")
text = text.replace("6F için **ayrı bir fresh PRE-STEP GitHub refresh**", "6G için **ayrı bir fresh PRE-STEP GitHub refresh**")
text = text.replace("6F'yi", "6G'yi") if "6F'yi bu dosyayı" in text else text
text = text.replace("6F 🟡 ACTIVE / NOT EXECUTED", "6F ✅ PEM-v0 / D-060\n- 6G 🟡 ACTIVE / NOT EXECUTED")
write("docs/LOCAL_MANAGER_HANDOFF.md", text)

# Prior summary review counts
text = read("docs/SYSTEMS_DETAILED_MAP.md")
text = text.replace("| Açık non-blocking review | 3 |", "| Açık non-blocking review | 2 |")
if "6F reconciliation sonucu:" not in text:
    text += "\n\n## 6F reconciliation sonucu\n\n`review.6d.professional_overlay_reconciliation` PEM-v0 / D-060 ile resolved edildi. D23 registry observability, reliability, performance, build ve testing capability'lerini canonical 6D Skill ID'leriyle reuse eder; 6D Skill/Objective/prerequisite semantics değiştirilmedi. 6D'de 2 external/freshness non-blocking review 6H'ye açık kalır.\n"
write("docs/SYSTEMS_DETAILED_MAP.md", text)

text = read("docs/GPU_ML_INFERENCE_DETAILED_MAP.md")
text = text.replace("- Open reviews: 0 blocking / 4 non-blocking", "- Open reviews: 0 blocking / 3 non-blocking")
text = text.replace("- 6E external coverage, tool/runtime freshness ve math/numerical coverage audit'leri independent external Research QA için 6H'ye pending kalır.\n- Package 6H tamamlanana kadar external-validation/learner-publication açısından final sayılmaz.", "- `review.6e.professional_overlay_reconciliation` PEM-v0 / D-060 ile resolved edildi; D23 registry 6E professional/project attributions'ını canonical ID reuse ile reconcile eder.\n- 6E external coverage, tool/runtime freshness ve math/numerical coverage audit'leri independent external Research QA için 6H'ye pending kalır.\n- Package 6H tamamlanana kadar external-validation/learner-publication açısından final sayılmaz.")
write("docs/GPU_ML_INFERENCE_DETAILED_MAP.md", text)

# PROGRESS_LOG
append_once("docs/PROGRESS_LOG.md", "6F — Professional engineering / project map — PEM-v0 / D-060", f'''## {TODAY} — 6F — Professional engineering / project map — PEM-v0 / D-060
- D23 professional engineering/OSS/large-project/capstone package üretildi.
- {C['skills']} Skill / {C['objectives']} Objective / {C['prerequisite_edges']} prerequisite edge.
- {C['reused_prior_package_skills']} prior Skill clone'lanmadan reused.
- 6D ve 6E professional-overlay review'ları resolved edildi.
- Independent 6F validator + prior regression validators PASS.
- External Research QA 6H'ye pending; 6G current active step olarak açıldı.''')

# Vault current context
text = read("vault/agent/CURRENT_CONTEXT.md")
text = text.replace("- [[vault/wiki/sources/GPU ML Inference Map Source|GIM-v0]]: GPU/ML/inference D14–D22 detailed map paketi tamamlandı.", "- [[vault/wiki/sources/GPU ML Inference Map Source|GIM-v0]]: GPU/ML/inference D14–D22 detailed map paketi tamamlandı.\n- [[vault/wiki/sources/Professional Engineering Map Source|PEM-v0]]: D23 professional engineering / OSS / project-capstone detailed map paketi tamamlandı.")
text = text.replace("6A–6E tamamlandı. Son tamamlanan adım **6E — GIM-v0 / D-059**. Aktif adım **6F — Professional engineering / project map**; henüz yürütülmedi. 6F başlamadan fresh PRE-STEP zorunludur.", "6A–6F tamamlandı. Son tamamlanan adım **6F — PEM-v0 / D-060**. Aktif adım **6G — Weakness localization + remediation mapping**; henüz yürütülmedi. 6G başlamadan fresh PRE-STEP zorunludur.")
text = text.replace("last_verified: 2026-08-26", f"last_verified: {TODAY}")
write("vault/agent/CURRENT_CONTEXT.md", text)

text = read("vault/agent/OPEN_LOOPS.md")
text = text.replace("last_reviewed: 2026-08-26", f"last_reviewed: {TODAY}")
text = text.replace("- [ ] 6F Professional Engineering detailed map **AKTİF**: `review.6d.professional_overlay_reconciliation` ile observability/reliability/performance-report overlay'lerini ve 6E project/professional attributions'ını reconcile etmek.\n- [ ] 6G weakness/remediation operationalization.", "- [x] 6F Professional Engineering detailed map: PEM-v0 / D-060 tamamlandı; 6D + 6E professional-overlay reconciliation resolved.\n- [ ] 6G weakness/remediation operationalization **AKTİF**.")
text = text.replace("`review.6d.external_coverage` ve `review.6d.platform_tool_freshness` burada kapanır.", "6C–6F external coverage/freshness/capstone-diversity review'ları burada kapanır.")
write("vault/agent/OPEN_LOOPS.md", text)

# Vault source/project
write("vault/wiki/sources/Professional Engineering Map Source.md", f'''---
type: source
source_type: repository-artifact
status: accepted-internal-map
canonical_document: "[[docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP|Professional Engineering Detailed Map]]"
canonical_dataset: "curriculum/decomposition/6f_professional_engineering/"
---

# Professional Engineering Map Source

PEM-v0 / D-060, D23 Professional Engineering + Open Source + Real Projects + Capstone granular authoring package'ıdır.

- {C['domains']} Domain / {C['modules']} Module / {C['topics']} Topic
- {C['skills']} Skill / {C['objectives']} Objective
- {C['reused_prior_package_skills']} prior Skill canonical reuse
- 6D/6E professional overlay reviews resolved
- 6H external Research QA pending

## Links
- [[vault/wiki/projects/AI Infra Learning Coach Delivery]]
- [[vault/wiki/sources/Execution State Source]]
''')
replace_once("vault/wiki/sources/Execution State Source.md", "Current execution için birlikte okunması gereken living-memory kaynak seti. 6E GIM-v0 / D-059 tamamlandı; güncel aktif adım 6F Professional engineering / project map'tir ve yürütme öncesi fresh PRE-STEP zorunludur.", "Current execution için birlikte okunması gereken living-memory kaynak seti. 6F PEM-v0 / D-060 tamamlandı; güncel aktif adım 6G Weakness localization + remediation mapping'tir ve yürütme öncesi fresh PRE-STEP zorunludur.")
text = read("vault/wiki/projects/AI Infra Learning Coach Delivery.md")
text = text.replace('next_action: "6F Professional engineering / project map için fresh PRE-STEP"', 'next_action: "6G Weakness localization + remediation mapping için fresh PRE-STEP"')
text = text.replace("6E GIM-v0 / D-059'un tamamlandığını ve 6F'nin aktif fakat henüz yürütülmemiş olduğunu belirtir.", "6F PEM-v0 / D-060'ın tamamlandığını ve 6G'nin aktif fakat henüz yürütülmemiş olduğunu belirtir.")
text = text.replace("- GPU/ML/Inference output: [[vault/wiki/sources/GPU ML Inference Map Source]]", "- GPU/ML/Inference output: [[vault/wiki/sources/GPU ML Inference Map Source]]\n- Professional engineering output: [[vault/wiki/sources/Professional Engineering Map Source]]")
write("vault/wiki/projects/AI Infra Learning Coach Delivery.md", text)

# Repository map gets a navigation link without becoming volatile state source.
text = read("vault/wiki/mocs/Repository Document Map.md")
if "Professional Engineering Map Source" not in text:
    text = text.rstrip() + "\n- [[vault/wiki/sources/Professional Engineering Map Source|PEM-v0 / Professional Engineering Map]]\n"
write("vault/wiki/mocs/Repository Document Map.md", text)

# Session handoff
write(f"vault/agent/session-logs/{TODAY}-6f-professional-engineering.md", f'''---
type: session-handoff
status: completed
step: 6F
---

# 6F Professional Engineering session handoff

PEM-v0 / D-060 tamamlandı. D23 package `curriculum/decomposition/6f_professional_engineering/` altında; {C['skills']} Skill, {C['objectives']} Objective ve {C['reused_prior_package_skills']} prior Skill reuse içerir. 6D/6E professional overlay review'ları resolved. External Research QA 6H'ye pending. Current active step 6G; henüz yürütülmedi ve fresh PRE-STEP gerekir.
''')

print("POST_6F_SYNC=PASS")
print(f"PEM_COUNTS skills={C['skills']} objectives={C['objectives']} edges={C['prerequisite_edges']} reused={C['reused_prior_package_skills']}")
