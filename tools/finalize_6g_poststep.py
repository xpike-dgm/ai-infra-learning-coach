from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
TODAY = "2026-08-27"
PKG = ROOT / "curriculum/decomposition/6g_weakness_remediation"
QA = yaml.safe_load((PKG / "qa_report.yaml").read_text(encoding="utf-8"))
C = QA["counts"]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, text: str) -> None:
    p = ROOT / path
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(text, encoding="utf-8")


def replace_required(path: str, old: str, new: str, count: int = 1) -> None:
    text = read(path)
    if old not in text:
        raise SystemExit(f"POST_6G_FAIL {path}: missing marker {old[:120]!r}")
    write(path, text.replace(old, new, count))


def replace_if_present(path: str, old: str, new: str) -> None:
    text = read(path)
    if old in text:
        write(path, text.replace(old, new))


def append_once(path: str, marker: str, block: str) -> None:
    text = read(path)
    if marker in text:
        return
    write(path, text.rstrip() + "\n\n" + block.strip() + "\n")


summary = f'''# WLRM-v0 — Weakness Localization & Remediation Map

**Adım:** 6G  
**Karar:** D-061  
**Durum:** Internal behavior authoring + deterministic QA complete; independent external Research QA pending 6H  
**Canonical dataset:** `curriculum/decomposition/6g_weakness_remediation/`

## 1. Amaç ve kapsam
WLRM-v0, 6C–6F ile kabul edilmiş curriculum registry'si üzerinde learner weakness sinyalinin hangi Objective/Skill'e yazılacağını ve hangi remediation yoluna dönüştürüleceğini formalize eder. 6G yeni curriculum Skill/Objective üretmez ve prerequisite graph'ını değiştirmez.

## 2. Exact coverage
- {C['source_packages']} accepted source package
- {C['skills_covered']} Skill weakness profile
- {C['objectives_covered']} Objective weakness profile
- {C['remediation_routes']} Objective-specific remediation route
- {C['failure_attribution_rules']} deterministic failure-attribution rule
- {C['remediation_strategies']} remediation strategy family
- {C['learning_need_mappings']} LearningNeed/planner mapping
- {C['source_remediation_tags']} existing source remediation tag normalized into the strategy bridge
- {C['open_blocking_reviews']} blocking / {C['open_non_blocking_reviews']} non-blocking review

## 3. Localization invariant
Primary intervention unit Objective'tir; Skill durumu GRE-v0 required/critical Objective gates üzerinden yeniden hesaplanır. Topic/Module/Domain yalnız derived summary/orchestration katmanıdır.

```text
Attempt / Artifact
→ validity + prerequisite + attribution
→ Objective-level weakness signal
→ GRE/RVR verification or remediation state
→ Skill gate recompute
→ branch-local prerequisite effect
→ LearningNeed
→ targeted remediation candidate
→ fresh evidence
→ recompute / close
```

`Bir soru yanlış → Skill başarısız → Topic/Domain reset` canonical davranış değildir.

## 4. Failure attribution safety
- Invalid/ambiguous item, invalid evaluator veya attribution problemi target negative evidence yazamaz.
- Missing/unready prerequisite nedeniyle contaminated attempt target Skill'i cezalandıramaz; prerequisite need açılır ve task/content metadata QA'ya gidebilir.
- Deferred/unattempted task negative evidence değildir.
- Target davranışı gözlenmesini engelleyen environment/tool failure, tool operasyonu target capability değilse learner weakness sayılmaz.
- H1–H4 assisted failure ile provisional/partial evaluator sonucu en fazla `hypothesis` üretir; tek başına confirmed remediation açamaz.
- Mastery öncesi clean H0 + direct + verified + prerequisite-valid negative evidence normal GRE recent window'a girer ve supported weakness üretebilir.
- Mastered Objective/Skill sonrası ilk clean contradiction `verification_due` açar; mastery anında silinmez.
- Fresh/unseen independent recheck tekrar başarısız olursa GRE/RVR yeniden hesaplanır ve gate failure varsa `remediation_required` açılabilir.
- `review_due` tek başına weakness değildir.

## 5. Weakness / misconception state
Generic weakness signal lifecycle:
`none → hypothesis → supported → confirmed → resolved`.

Learner-specific misconception memory curriculum Skill identity'sinden ayrıdır. AI bir misconception hypothesis önerebilir; canonical mastery veya confirmed misconception state'i yalnız LLM kanaatiyle yazılamaz.

## 6. Remediation strategies
WLRM-v0 15 strategy family kullanır: targeted reteach, worked-example reconstruction, micro-drill, state/trace reconstruction, debug localization, prerequisite refresh, failure-scenario replay, measurement replay, production retry, explanation rebuild, misconception contrast, transfer retest, retrieval reinforcement, tool/workflow rehearsal ve fresh independent recheck.

Per-Objective route, accepted `remediation_tags`, Objective evidence profile, required direct evidence ve transfer/artifact gereksinimlerinden deterministic candidate strategy seti türetir.

## 7. Remediation closure
Remediation task'ının tamamlanması remediation'ı kapatmaz. Closure için target Objective'e uygun fresh/context-diverse, H0, direct, verified ve prerequisite-valid evidence GRE/RVR pipeline'ından geçmelidir. Objective transfer veya user-authored artifact gerektiriyorsa closure recheck'i de bunu korur.

Exact same-item immediate repeat güçlü closure evidence değildir. Manual mastery score override yasaktır.

## 8. Planner / prerequisite integration
- weakness hypothesis → `weakness_detected` / diagnose-reinforce-remediate,
- supported pre-mastery weakness → P1 repair path,
- `verification_due` → P1 fresh verification,
- confirmed remediation → varsayılan P1; yalnız gerçek critical/hard-prerequisite integrity blocker varsa P0,
- prerequisite gap → target yerine prerequisite repair need,
- review_due only → retention need, remediation değil.

Confirmed prerequisite weakness yalnız gerçekten dependent hard-prerequisite branch'i etkiler; bağımsız branch'ler devam eder.

## 9. Broad-overreaction guards
- Domain reset yok.
- Topic state weakness'i bütün linked Skills'e geri yaymaz.
- Tek Objective failure GRE gates'i bypass ederek whole-Skill fail flag yazmaz.
- Global project PASS/FAIL bütün component Objectives'e broadcast edilmez.
- Technical English gizli/global technical weakness kaynağı yapılamaz.

## 10. QA
- 6C Foundations regression: PASS
- 6D Systems regression: PASS
- 6E GPU/ML/Inference regression: PASS
- 6F Professional Engineering regression: PASS
- Independent 6G overlay validator: PASS
- exact accepted registry coverage: PASS
- 0 blocking review
- package result: `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`

## 11. Open handoff
- `review.6g.external_behavior_coverage` → 6H independent Research AI
- `review.6g.misconception_taxonomy_expansion` → 14B
- `review.6g.calibration` → 18C
- `review.6g.content_realization` → 15/20

6H external Research QA WLRM-v0 dahil AŞAMA 6 full-route coverage/prerequisite/current-industry validation için zorunludur. 6G internal QA, 6H'nin yerine geçmez; learner publication hâlâ pending'dir.
'''
write("docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md", summary)

# D-061
append_once("docs/DECISIONS.md", "## D-061 — Weakness localization/remediation overlay = WLRM-v0", f'''## D-061 — Weakness localization/remediation overlay = WLRM-v0
**Durum:** Kabul edildi — {TODAY}

- 6G final modeli `WLRM-v0 — Weakness Localization & Remediation Map` oldu.
- Canonical summary `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`; dataset `curriculum/decomposition/6g_weakness_remediation/`.
- Accepted 6C–6F registry'sindeki {C['skills_covered']} Skill ve {C['objectives_covered']} Objective exact covered; {C['remediation_routes']} Objective-specific remediation route üretildi.
- 6G yeni Skill/Objective identity veya prerequisite edge eklemez; learner weakness/remediation overlay'idir.
- {C['failure_attribution_rules']} deterministic failure-attribution rule, {C['remediation_strategies']} remediation strategy family ve {C['learning_need_mappings']} LearningNeed mapping kabul edildi.
- Invalid/ambiguous/prerequisite-contaminated attempt target negative evidence yazamaz; assisted/provisional failure yalnız hypothesis olabilir.
- First clean post-mastery contradiction `verification_due`; confirmed fresh recheck failure GRE/RVR gate recompute sonrası `remediation_required` açabilir.
- `review_due` weakness değildir; broad Topic/Domain reset ve integrated-project broadcast yasaktır.
- Remediation closure fresh H0 + direct + verified + prerequisite-valid evidence ister; task completion veya manual mastery override closure değildir.
- Learner misconception state curriculum identity'den ayrıdır ve LLM tek başına confirmed state/mastery yazamaz.
- Internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`; 0 blocking / {C['open_non_blocking_reviews']} non-blocking review. Independent external Research QA 6H'ye pending ve zorunludur.''')

# EXECUTION_INDEX
replace_required("docs/EXECUTION_INDEX.md",
    "- [ ] **6G — Weakness localization + remediation mapping** **AKTİF** — zayıflığın Skill/Objective düzeyinde ayrı tutulması\n- [ ] **6H — Coverage / prerequisite / Research QA** — eksik/duplicate/hidden prerequisite audit + bağımsız Research AI doğrulaması",
    "- [x] **6G — Weakness localization + remediation mapping** — `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`, `curriculum/decomposition/6g_weakness_remediation/` — WLRM-v0 / D-061\n- [ ] **6H — Coverage / prerequisite / Research QA** **AKTİF** — eksik/duplicate/hidden prerequisite audit + bağımsız Research AI doğrulaması")
append_once("docs/EXECUTION_INDEX.md", "- D-061:", "- D-061: 6G final weakness localization/remediation overlay `WLRM-v0`; 543 Skill + 590 Objective exact coverage, evidence-safe failure attribution ve targeted remediation routes.")
replace_if_present("docs/EXECUTION_INDEX.md", "`6A–6F`", "`6A–6G`")
replace_if_present("docs/EXECUTION_INDEX.md", "**Aktif:** **`6G — Weakness localization + remediation mapping`**", "**Aktif:** **`6H — Coverage / prerequisite / Research QA`**")
replace_if_present("docs/EXECUTION_INDEX.md", "6G henüz yürütülmedi; 6G başlamadan fresh PRE-STEP GitHub refresh zorunludur.", "6G WLRM-v0 / D-061 ile tamamlandı. 6H henüz yürütülmedi; 6H başlamadan fresh PRE-STEP GitHub refresh ve bağımsız Research AI planı zorunludur.")

# STEP_STATUS
replace_required("docs/STEP_STATUS.md",
    "| **6G — Weakness localization + remediation mapping** | 🟡 Aktif | 6C–6F granular graph üzerinde weakness/remediation operational mapping. **Henüz yürütülmedi.** |\n| **6H–20** | ⬜ Bekliyor | 6G sonrası canonical sırada. |",
    "| **6G — Weakness localization + remediation mapping** | ✅ | WLRM-v0 / D-061. 543 Skill + 590 Objective exact weakness/remediation overlay ve 590 route tamamlandı. |\n| **6H — Coverage / prerequisite / Research QA** | 🟡 Aktif | AŞAMA 6 bağımsız external coverage/current-industry/hidden-prerequisite QA. **Henüz yürütülmedi.** |\n| **7–20** | ⬜ Bekliyor | 6H sonrası canonical sırada. |")
text = read("docs/STEP_STATUS.md")
idx = text.find("## Son tamamlanan numaralı adım — 6F")
if idx == -1:
    raise SystemExit("POST_6G_FAIL STEP_STATUS last-completed section missing")
text = text[:idx] + f'''## Son tamamlanan numaralı adım — 6G

**Final:** `WLRM-v0 — Weakness Localization & Remediation Map` / D-061.  
**Ana çıktı:** `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/`.

6G sonucu:
- {C['skills_covered']} accepted Skill / {C['objectives_covered']} accepted Objective exact coverage,
- {C['remediation_routes']} Objective-specific remediation route,
- {C['failure_attribution_rules']} failure-attribution rule / {C['remediation_strategies']} remediation strategy,
- invalid/prerequisite-contaminated false-negative guard,
- assisted/provisional hypothesis-only guard,
- post-mastery `verification_due` hysteresis,
- broad Topic/Domain/project broadcast guard,
- remediation closure only by fresh valid evidence,
- 0 blocking / {C['open_non_blocking_reviews']} non-blocking 6G review,
- independent external Research QA 6H'ye pending.

## Aktif adım — 6H Coverage / prerequisite / Research QA

**6H henüz yürütülmedi.** Fresh PRE-STEP + ayrı bağımsız Research AI zorunludur.
'''
write("docs/STEP_STATUS.md", text)

# MASTER_PLAN
replace_required("docs/MASTER_PLAN.md",
    "### [ ] 6G — Weakness localization + remediation mapping — **AKTİF**\n### [ ] 6H — Coverage / prerequisite / external Research QA",
    f'''### [x] 6G — Weakness localization + remediation mapping — WLRM-v0 / D-061
**Final:** `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/`

- {C['skills_covered']} accepted Skill + {C['objectives_covered']} accepted Objective exact overlay coverage,
- {C['remediation_routes']} Objective remediation route,
- {C['failure_attribution_rules']} failure-attribution rule + {C['remediation_strategies']} remediation strategy family,
- invalid/ambiguous/prerequisite-contaminated attempt target weakness yazmıyor,
- H1–H4/provisional/partial signal confirmed remediation'a atlamıyor,
- first clean post-mastery contradiction `verification_due`,
- `review_due` weakness değil,
- broad Topic/Domain reset ve project-component broadcast yok,
- closure fresh H0/direct/verified/prerequisite-valid evidence ile GRE/RVR üzerinden,
- internal QA PASS, 0 blocking; independent external Research QA 6H'ye pending.

### [ ] 6H — Coverage / prerequisite / external Research QA — **AKTİF**''')
append_once("docs/MASTER_PLAN.md", "- D-061:", "- D-061: 6G final `WLRM-v0 — Weakness Localization & Remediation Map`; canonical summary `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`, dataset `curriculum/decomposition/6g_weakness_remediation/`.")
replace_if_present("docs/MASTER_PLAN.md", "`6A–6F`", "`6A–6G`")
replace_if_present("docs/MASTER_PLAN.md", "**Aktif:** **`6G — Weakness localization + remediation mapping`**", "**Aktif:** **`6H — Coverage / prerequisite / external Research QA`**")

# PROJECT_CONTEXT
insert_marker = "**D-060 / PEM-v0:** canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, dataset `curriculum/decomposition/6f_professional_engineering/`. D23; 1 Domain, 9 Module, 27 Topic, 76 Skill, 87 Objective. Existing D01–D22 technical capability'leri clone edilmeden professional project/capstone context'lerinde reuse edildi; 0 blocking review. External validation 6H'ye pending."
replace_required("PROJECT_CONTEXT.md", insert_marker, insert_marker + f"\n\n**D-061 / WLRM-v0:** canonical summary `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`, dataset `curriculum/decomposition/6g_weakness_remediation/`. {C['skills_covered']} accepted Skill + {C['objectives_covered']} accepted Objective için weakness/remediation overlay; {C['remediation_routes']} route, {C['failure_attribution_rules']} attribution rule, {C['remediation_strategies']} strategy; 0 blocking review. External validation 6H'ye pending.")
replace_required("PROJECT_CONTEXT.md",
    "  - **6G 🟡 Weakness localization + remediation mapping — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n  - 6H ⬜",
    "  - **6G ✅ WLRM-v0 / D-061**\n  - **6H 🟡 Coverage / prerequisite / Research QA — AKTİF, HENÜZ YÜRÜTÜLMEDİ**")
replace_required("PROJECT_CONTEXT.md",
    "**Sıradaki numaralı çalışma 6G'dir.** 6G başlamadan fresh PRE-STEP GitHub refresh; FDM-v0 + SDM-v0 + GIM-v0 + PEM-v0 remediation metadata/review handoff setlerinin yeniden okunması zorunludur.",
    "**Sıradaki numaralı çalışma 6H'dir.** 6H başlamadan fresh PRE-STEP GitHub refresh ve bağımsız external Research AI workflow'u zorunludur; internal 6C–6G QA 6H'nin yerine geçmez.")

# HANDOFF_STATE
append_once("docs/HANDOFF_STATE.md", "## 9.7 D-061 / 6G final özeti", f'''## 9.7 D-061 / 6G final özeti

Canonical summary: `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`.  
Canonical dataset: `curriculum/decomposition/6g_weakness_remediation/`.

WLRM-v0:
- {C['skills_covered']} Skill / {C['objectives_covered']} Objective exact coverage,
- {C['remediation_routes']} Objective-specific remediation route,
- {C['failure_attribution_rules']} evidence-attribution rule + {C['remediation_strategies']} strategy family,
- invalid/prerequisite-contaminated failure target Skill'i cezalandırmaz,
- assisted/provisional signal confirmed remediation değildir,
- first post-mastery contradiction `verification_due`,
- `review_due` weakness değildir,
- broad reset / project broadcast yok,
- remediation closure fresh valid evidence ister,
- internal QA PASS, 0 blocking; 6H external Research QA pending.''')
replace_required("docs/HANDOFF_STATE.md",
    "  - 6G 🟡 Weakness localization + remediation mapping — aktif, henüz yürütülmedi\n  - 6H ⬜",
    "  - 6G ✅ WLRM-v0 / D-061\n  - 6H 🟡 Coverage / prerequisite / Research QA — aktif, henüz yürütülmedi")
replace_required("docs/HANDOFF_STATE.md",
    "**Son tamamlanan:** `6F — PEM-v0 / D-060`\n**Aktif:** `6G — Weakness localization + remediation mapping`\n**6G henüz yürütülmedi.**",
    "**Son tamamlanan:** `6G — WLRM-v0 / D-061`\n**Aktif:** `6H — Coverage / prerequisite / Research QA`\n**6H henüz yürütülmedi. Independent external Research AI zorunludur.**")

# START_HERE
replace_required("docs/START_HERE.md",
    "- 6F ✅ `PEM-v0 — Professional Engineering / Projects Detailed Map` / D-060\n- 6G 🟡 Weakness localization + remediation mapping — aktif, henüz yürütülmedi",
    "- 6F ✅ `PEM-v0 — Professional Engineering / Projects Detailed Map` / D-060\n- 6G ✅ `WLRM-v0 — Weakness Localization & Remediation Map` / D-061\n- 6H 🟡 Coverage / prerequisite / Research QA — aktif, henüz yürütülmedi")
replace_required("docs/START_HERE.md",
    "**Aktif:** **`6G — Weakness localization + remediation mapping`**\n**6G henüz yürütülmedi.**",
    "**Aktif:** **`6H — Coverage / prerequisite / Research QA`**\n**6H henüz yürütülmedi. Independent external Research AI zorunludur.**")
replace_if_present("docs/START_HERE.md", "6G başlamadan yeni PRE-STEP GitHub refresh zorunlu.", "6H başlamadan fresh PRE-STEP GitHub refresh ve bağımsız Research AI workflow'u zorunludur.")
replace_if_present("docs/START_HERE.md", "D-041, D-042, D-044–D-060", "D-041, D-042, D-044–D-061")
replace_if_present("docs/START_HERE.md", "Şu an aktif adım 6G — Weakness localization + remediation mapping; 6G henüz yürütülmedi.", "Şu an aktif adım 6H — Coverage / prerequisite / Research QA; 6H henüz yürütülmedi ve independent external Research AI zorunludur.")
replace_required("docs/START_HERE.md",
    "11l. `curriculum/decomposition/6f_professional_engineering/manifest.yaml`\n11m. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    "11l. `curriculum/decomposition/6f_professional_engineering/manifest.yaml`\n11m. `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`\n11n. `curriculum/decomposition/6g_weakness_remediation/manifest.yaml`\n11o. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`")

# GRANULAR_CAPABILITY_MAP_PLAN — stable stage charter has stale execution annotations and now a new canonical 6G output.
replace_required("docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
    "**Durum:** YÜRÜTÜLÜYOR — 6A–6D TAMAMLANDI / 6E AKTİF",
    "**Durum:** YÜRÜTÜLÜYOR — 6A–6G TAMAMLANDI / 6H AKTİF")
replace_if_present("docs/GRANULAR_CAPABILITY_MAP_PLAN.md", "### 6E — GPU / ML / Inference detailed map 🟡 AKTİF", "### 6E — GPU / ML / Inference detailed map ✅")
replace_if_present("docs/GRANULAR_CAPABILITY_MAP_PLAN.md", "### 6F — Professional engineering / project map", "### 6F — Professional engineering / project map ✅")
replace_if_present("docs/GRANULAR_CAPABILITY_MAP_PLAN.md", "### 6G — Weakness localization ve remediation mapping", "### 6G — Weakness localization ve remediation mapping ✅\nCanonical: `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/` — WLRM-v0 / D-061.")
replace_if_present("docs/GRANULAR_CAPABILITY_MAP_PLAN.md", "### 6H — Coverage + prerequisite + external research QA", "### 6H — Coverage + prerequisite + external research QA 🟡 AKTİF")

# PROGRESS_LOG
append_once("docs/PROGRESS_LOG.md", "### 2026-08-27 — 6G tamamlandı / WLRM-v0 / D-061", f'''### 2026-08-27 — 6G tamamlandı / WLRM-v0 / D-061
- Fresh PRE-STEP ile 6F completion ve 6G scope doğrulandı.
- `WLRM-v0 — Weakness Localization & Remediation Map` üretildi.
- {C['skills_covered']} accepted Skill ve {C['objectives_covered']} accepted Objective exact covered; {C['remediation_routes']} remediation route.
- {C['failure_attribution_rules']} failure-attribution rule, {C['remediation_strategies']} strategy family, {C['learning_need_mappings']} planner mapping.
- 6C/6D/6E/6F regression validator'ları PASS; independent 6G validator PASS.
- 0 blocking / {C['open_non_blocking_reviews']} non-blocking review; 6H independent external Research QA pending.
- D-050 living-memory/Vault sync ve stale-reference audit 6G POST-STEP içinde yürütüldü.''')

# LOCAL_MANAGER_HANDOFF current execution pointers.
for old, new in [
    ("6A–6F completed / 6G active-not-executed", "6A–6G completed / 6H active-not-executed"),
    ("fresh 6G PRE-STEP GitHub refresh", "fresh 6H PRE-STEP GitHub refresh"),
    ("→ 6G execution", "→ 6H execution"),
    ("→ 6G active-not-executed", "→ 6H active-not-executed"),
]:
    replace_if_present("docs/LOCAL_MANAGER_HANDOFF.md", old, new)

# Vault CURRENT_CONTEXT
replace_required("vault/agent/CURRENT_CONTEXT.md",
    "- [[vault/wiki/sources/Professional Engineering Map Source|PEM-v0]]: D23 professional engineering / OSS / project-capstone detailed map paketi tamamlandı.",
    "- [[vault/wiki/sources/Professional Engineering Map Source|PEM-v0]]: D23 professional engineering / OSS / project-capstone detailed map paketi tamamlandı.\n- [[vault/wiki/sources/Weakness Remediation Map Source|WLRM-v0]]: accepted 6C–6F registry için Objective-first weakness localization + remediation overlay tamamlandı.")
replace_required("vault/agent/CURRENT_CONTEXT.md",
    "6A–6F tamamlandı. Son tamamlanan adım **6F — PEM-v0 / D-060**. Aktif adım **6G — Weakness localization + remediation mapping**; henüz yürütülmedi. 6G başlamadan fresh PRE-STEP zorunludur.",
    "6A–6G tamamlandı. Son tamamlanan adım **6G — WLRM-v0 / D-061**. Aktif adım **6H — Coverage / prerequisite / Research QA**; henüz yürütülmedi. 6H başlamadan fresh PRE-STEP ve bağımsız external Research AI zorunludur.")

# Vault OPEN_LOOPS
replace_required("vault/agent/OPEN_LOOPS.md",
    "- [ ] 6G weakness/remediation operationalization **AKTİF**.\n- [ ] 6H independent external Research QA: full coverage, current-industry ve prerequisite audit; 6C–6F external coverage/freshness/capstone-diversity review'ları burada kapanır.",
    "- [x] 6G weakness/remediation operationalization: WLRM-v0 / D-061 tamamlandı; `review.6g.external_behavior_coverage` 6H'ye devredildi.\n- [ ] 6H independent external Research QA **AKTİF**: full coverage, current-industry, prerequisite ve WLRM behavior audit; 6C–6G external coverage/freshness reviews burada ele alınır.")

# Vault execution source
replace_required("vault/wiki/sources/Execution State Source.md",
    "Current execution için birlikte okunması gereken living-memory kaynak seti. 6F PEM-v0 / D-060 tamamlandı; güncel aktif adım 6G Weakness localization + remediation mapping'tir ve yürütme öncesi fresh PRE-STEP zorunludur.",
    "Current execution için birlikte okunması gereken living-memory kaynak seti. 6G WLRM-v0 / D-061 tamamlandı; güncel aktif adım 6H Coverage / prerequisite / Research QA'dır. 6H yürütme öncesi fresh PRE-STEP ve bağımsız external Research AI zorunludur.")

# Vault project
replace_required("vault/wiki/projects/AI Infra Learning Coach Delivery.md",
    'next_action: "6G Weakness localization + remediation mapping için fresh PRE-STEP"',
    'next_action: "6H Coverage / prerequisite / external Research QA için fresh PRE-STEP"')
replace_required("vault/wiki/projects/AI Infra Learning Coach Delivery.md",
    "[[vault/wiki/sources/Execution State Source|Living-memory kaynakları]] 6F PEM-v0 / D-060'ın tamamlandığını ve 6G'nin aktif fakat henüz yürütülmemiş olduğunu belirtir.",
    "[[vault/wiki/sources/Execution State Source|Living-memory kaynakları]] 6G WLRM-v0 / D-061'in tamamlandığını ve 6H'nin aktif fakat henüz yürütülmemiş olduğunu belirtir. 6H independent external Research AI gerektirir.")
replace_required("vault/wiki/projects/AI Infra Learning Coach Delivery.md",
    "- Professional engineering output: [[vault/wiki/sources/Professional Engineering Map Source]]",
    "- Professional engineering output: [[vault/wiki/sources/Professional Engineering Map Source]]\n- Weakness/remediation output: [[vault/wiki/sources/Weakness Remediation Map Source]]")

# New Vault source note.
write("vault/wiki/sources/Weakness Remediation Map Source.md", f'''---
type: source
source_type: repository-document
status: accepted-internal-qa
local_path:
  - "[[docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP|WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md]]"
  - "curriculum/decomposition/6g_weakness_remediation/"
topics:
  - "[[vault/wiki/concepts/Adaptive Learning Engine]]"
  - "[[vault/wiki/concepts/Curriculum Knowledge Graph]]"
---

# Weakness Remediation Map Source

`WLRM-v0 / D-061`, accepted 6C–6F registry'deki {C['skills_covered']} Skill ve {C['objectives_covered']} Objective için evidence-safe weakness localization, {C['remediation_routes']} Objective-specific remediation route ve planner/remediation handoff contract'ıdır.

Ana kaynak: [[docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP|WLRM-v0 summary]].

Independent external Research QA 6H'ye pending'dir.
''')

# Repository MOC
replace_if_present("vault/wiki/mocs/Repository Document Map.md",
    "- [[docs/SYSTEMS_DETAILED_MAP|Systems Detailed Map]]",
    "- [[docs/SYSTEMS_DETAILED_MAP|Systems Detailed Map]]\n- [[docs/GPU_ML_INFERENCE_DETAILED_MAP|GPU / ML / Inference Detailed Map]]\n- [[docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP|Professional Engineering Detailed Map]]\n- [[docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP|Weakness Localization & Remediation Map]]")
replace_required("vault/wiki/mocs/Repository Document Map.md",
    "- [[vault/wiki/sources/Professional Engineering Map Source|PEM-v0 / Professional Engineering Map]]",
    "- [[vault/wiki/sources/Professional Engineering Map Source|PEM-v0 / Professional Engineering Map]]\n- [[vault/wiki/sources/Weakness Remediation Map Source|WLRM-v0 / Weakness Remediation Map]]")

# Session log
write("vault/agent/session-logs/2026-08-27-6g-weakness-remediation.md", f'''---
type: session-log
step: 6G
status: completed
model: WLRM-v0
decision: D-061
---

# 6G — Weakness Localization + Remediation Mapping

- Fresh PRE verified 6F complete / 6G active.
- Generated exact overlay for {C['skills_covered']} Skills and {C['objectives_covered']} Objectives.
- Produced {C['remediation_routes']} remediation routes, {C['failure_attribution_rules']} failure rules, {C['remediation_strategies']} strategies.
- 6C–6F regression validators PASS; independent 6G validator PASS.
- D-050 POST living-memory/Vault synchronization executed.
- Next step: 6H independent external Research QA; not executed in this session.
''')

# Final assertions for state transition.
for path in ["docs/EXECUTION_INDEX.md", "docs/STEP_STATUS.md", "docs/HANDOFF_STATE.md", "PROJECT_CONTEXT.md", "docs/MASTER_PLAN.md", "docs/START_HERE.md"]:
    t = read(path)
    if "D-061" not in t and path != "docs/START_HERE.md":
        raise SystemExit(f"POST_6G_FAIL {path}: D-061 not reflected")
    if "6H" not in t:
        raise SystemExit(f"POST_6G_FAIL {path}: 6H current state missing")

print("POST_6G_SYNC=PASS")
print(f"WLRM_COUNTS skills={C['skills_covered']} objectives={C['objectives_covered']} routes={C['remediation_routes']} rules={C['failure_attribution_rules']} strategies={C['remediation_strategies']}")
