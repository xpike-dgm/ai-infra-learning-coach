from pathlib import Path


def read(path: str) -> str:
    return Path(path).read_text(encoding="utf-8")


def write(path: str, text: str) -> None:
    Path(path).write_text(text, encoding="utf-8")


def replace_once(path: str, old: str, new: str) -> None:
    text = read(path)
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected one occurrence, found {count}: {old!r}")
    write(path, text.replace(old, new, 1))


def append_once(path: str, marker: str, block: str) -> None:
    text = read(path)
    if marker in text:
        return
    write(path, text.rstrip() + "\n\n" + block.strip() + "\n")


def replace_from_heading(path: str, heading: str, new_tail: str) -> None:
    text = read(path)
    pos = text.find(heading)
    if pos < 0:
        raise SystemExit(f"{path}: heading not found: {heading}")
    write(path, text[:pos] + new_tail.strip() + "\n")


# Main output guard
spec = read("docs/V1_FOUNDATION_BACKBONE.md")
for marker in ["FBB-v0", "D-052", "**Durum:** TAMAMLANDI", "5D — Graph architecture QA"]:
    if marker not in spec:
        raise SystemExit(f"5C spec missing marker: {marker}")

# DECISIONS
append_once(
    "docs/DECISIONS.md",
    "## D-052 — V1 foundation backbone = FBB-v0",
    """
## D-052 — V1 foundation backbone = FBB-v0
**Durum:** Kabul edildi — 2026-08-25

- 5C final modeli `FBB-v0 — V1 Foundation Backbone` oldu.
- “İlk 8–12 hafta” calendar unlock değildir; V1 başlangıç content hacmini/scope'unu ifade eder. Runtime progression mastery + prerequisite + retention + daily capacity ile belirlenir.
- Canonical çıktı `docs/V1_FOUNDATION_BACKBONE.md`.
- V1 foundation subgraph; Computer/Programming zero-entry bridge, Python, C, Linux/Git/Shell, early DS&A ve day-one parallel Technical English'i KGC-v0 üzerinde bağlar.
- 5C Skill/Objective ID'leri `authoring_seed / not_learner_published` lifecycle'ındadır; 6A/6C global naming/granularity standardı ile ratify veya explicit KGC migration üzerinden refine edilir.
- Tek global `foundation_passed` gate yoktur. Technical, English ve professional-workflow scope'ları ayrıdır; English global technical hard prerequisite değildir.
- Shared programming mental-model Skills ile language-specific production Skills ayrıdır; Python mastery C syntax mastery'yi bedava vermez.
- Initial runtime dependencies yalnız Skill→Skill hard/soft PRG-v0 edge'leridir; Domain/Module/Topic placement hard lock üretmez.
- Evidence templates GRE/QAB/RVR'ı değiştirmez; yeni numeric mastery threshold/count icat edilmez.
- Diagnostic, retention ve remediation anchor'ları exact Skill/Objective attribution'a bağlıdır; broad Domain reset yasaktır.
- AŞAMA 15 production content, 6A/6C/6H sonrası ratified/published graph ID'lerine bağlanır.
- 5D FBB-v0 subgraph'ı cycle, dead-end, hidden prerequisite, duplicate Skill/reuse ve reachability açısından QA edecektir.
- Ayrı external Research AI 5C'de kullanılmadı; full coverage/current-industry/prerequisite bağımsız Research QA 6H'de zorunlu kalır.

Ayrıntı: `docs/V1_FOUNDATION_BACKBONE.md`.
""",
)

# EXECUTION_INDEX
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- D-051: 5B final knowledge-graph contract `KGC-v0`.\n",
    "- D-051: 5B final knowledge-graph contract `KGC-v0`.\n- D-052: 5C final V1 foundation backbone `FBB-v0`.\n",
)
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **5C — İlk 8–12 haftalık curriculum backbone** **AKTİF** — V1 başlangıç alt grafiğinin iskeleti\n- [ ] **5D — Graph architecture QA** — cycle/dead-end/hidden prerequisite ve genişleme kontrolü",
    "- [x] **5C — İlk 8–12 haftalık curriculum backbone** — `docs/V1_FOUNDATION_BACKBONE.md` — FBB-v0 / D-052\n- [ ] **5D — Graph architecture QA** **AKTİF** — cycle/dead-end/hidden prerequisite, duplicate/reuse ve reachability kontrolü",
)
replace_once(
    "docs/EXECUTION_INDEX.md",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5B`  \n**Aktif:** **`5C — İlk 8–12 haftalık curriculum backbone`**\n\n5B KGC-v0 / D-051 ile tamamlandı. 5C henüz yürütülmedi; 5C başlamadan yeni PRE-STEP GitHub refresh zorunludur.",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5C`  \n**Aktif:** **`5D — Graph architecture QA`**\n\n5C FBB-v0 / D-052 ile tamamlandı. 5D henüz yürütülmedi; 5D başlamadan yeni PRE-STEP GitHub refresh zorunludur.",
)

# STEP_STATUS
replace_once(
    "docs/STEP_STATUS.md",
    "| **5C — İlk 8–12 haftalık curriculum backbone** | 🟡 Aktif | KGC-v0 üzerinde V1 başlangıç alt graph iskeleti kurulacak. **Henüz yürütülmedi.** |\n| **5D–20** | ⬜ Bekliyor | 5C sonrası canonical sırada. |",
    "| **5C — İlk 8–12 haftalık curriculum backbone** | ✅ | FBB-v0 / D-052. V1 foundation authoring-seed subgraph tamamlandı. |\n| **5D — Graph architecture QA** | 🟡 Aktif | FBB-v0 cycle/dead-end/hidden prerequisite/duplicate/reachability açısından doğrulanacak. **Henüz yürütülmedi.** |\n| **6A–20** | ⬜ Bekliyor | 5D sonrası canonical sırada. |",
)
replace_from_heading(
    "docs/STEP_STATUS.md",
    "## Son tamamlanan numaralı adım — 5B",
    """
## Son tamamlanan numaralı adım — 5C

**Final:** `FBB-v0 — V1 Foundation Backbone` / D-052.  
Ana çıktı: `docs/V1_FOUNDATION_BACKBONE.md`.

5C kararları:
- 8–12 hafta takvim değil scope-equivalent content envelope,
- zero-entry Computer/Programming bridge yeni broad career Domain'i yaratmadan early topics'e yerleştirildi,
- Python + C + Linux/Git/Shell + early DS&A + Technical English başlangıç subgraph'ı tanımlandı,
- Technical English day-one parallel fakat global technical hard gate değil,
- KGC-v0 uyumlu Skill/Objective authoring-seed skeleton tanımlandı,
- 6A/6C ratification öncesi lifecycle `authoring_seed / not_learner_published`,
- shared programming mental model ile language-specific production capability ayrıldı,
- initial Skill→Skill hard/soft prerequisite edges PRG-v0 semantics ile tanımlandı,
- evidence/retention/diagnostic/remediation anchor'ları GRE/QAB/RVR canonical davranışına bağlandı,
- AŞAMA 15 production-content handoff'u ve 5D QA fixture'ları tanımlandı.

Ayrı Research AI kullanılmadı; external full-route coverage/current-industry/prerequisite Research QA 6H'de zorunlu kalır.

## Aktif adım — 5D Graph architecture QA

**5D henüz yürütülmedi.**

5D başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca fresh PRE-STEP GitHub refresh zorunludur.

5D'de özellikle:
- hard prerequisite DAG / cycle kontrolü,
- inaccessible required Skill / dead-end kontrolü,
- hidden prerequisite ve task interpretability riski,
- duplicate semantic Skill / Topic reuse kontrolü,
- English global-gate ihlali kontrolü,
- branch isolation / independent continuation,
- FBB-v0 Objective/Topic reachability,
- KGC version/migration uyumu,
- AŞAMA 6 ve AŞAMA 15 genişleme handoff güvenliği

doğrulanacak.
""",
)

# HANDOFF_STATE
replace_once(
    "docs/HANDOFF_STATE.md",
    "- **D-051:** KGC-v0 Versioned Curriculum Knowledge Graph Contract; 5B tamamlandı.\n",
    "- **D-051:** KGC-v0 Versioned Curriculum Knowledge Graph Contract; 5B tamamlandı.\n- **D-052:** FBB-v0 V1 Foundation Backbone; 5C tamamlandı.\n",
)
replace_from_heading(
    "docs/HANDOFF_STATE.md",
    "## 6. D-051 / 5B final özeti",
    """
## 6. D-051 / 5B final özeti

Canonical: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

KGC-v0 organization (`Domain → Module → Topic`) ile capability/evidence (`Skill → Learning Objective`) identity'sini ayırır; Topic↔Skill many-to-many reuse, Skill→Skill hard/soft prerequisites, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness ve conservative graph migration contract'ını tanımlar.

## 7. D-052 / 5C final özeti

Canonical: `docs/V1_FOUNDATION_BACKBONE.md`.

FBB-v0:
- V1 “8–12 hafta” ifadesini calendar gate değil scope-equivalent content envelope olarak kullanır,
- zero-entry Computer/Programming bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English seed subgraph'ını tanımlar,
- Technical/English/professional-workflow scope'larını ayırır; English global technical blocker değildir,
- KGC-v0 uyumlu Skill/Objective logical IDs üretir fakat 6A/6C öncesi lifecycle `authoring_seed / not_learner_published` kalır,
- shared mental-model Skills ile language-specific production Skills'i ayırır,
- initial hard/soft Skill prerequisite edges PRG-v0 semantics ile tanımlar,
- GRE/QAB/RVR uyumlu evidence/retention/diagnostic/remediation anchor'ları verir,
- AŞAMA 15 production authoring ihtiyaçlarını ve 5D graph-QA fixture'larını tanımlar.

5C ayrı Research AI kullanmadı; 6H external coverage/current-industry/prerequisite Research QA zorunlu kalır.

## 8. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 devam ediyor:
  - 5A ✅ PDM-v0 / D-049
  - 5B ✅ KGC-v0 / D-051
  - 5C ✅ FBB-v0 / D-052
  - 5D 🟡 Graph architecture QA — aktif, henüz yürütülmedi
- AŞAMA 6–20 ⬜

## 9. Güncel kesin konum

**Aktif:** `5D — Graph architecture QA`  
**5D henüz yürütülmedi.**

## 10. 5D'de kesinleştirilecekler

Ana soru:
> FBB-v0 V1 seed graph, KGC-v0/PRG-v0 invariants altında cycle, dead-end, hidden prerequisite, duplicate semantic Skill, accidental global gate veya unreachable Objective üretmeden güvenli biçimde genişleyebilir mi?

Kesinleştirilecek:
- hard-edge cycle/DAG kontrolü,
- required-node reachability/dead-end kontrolü,
- duplicate Skill ve Topic↔Skill reuse doğruluğu,
- hidden prerequisite / evidence contamination kontrolü,
- English global-gate guard,
- independent branch continuation,
- scope-relative requirement tutarlılığı,
- authoring_seed → 6A/6C ratification/migration uyumu,
- 5C→6/15 handoff güvenliği.

5D production content üretmez ve 6H external Research QA'nın yerine geçmez.

## 11. 5D için PRE-STEP doğrudan okunacaklar
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. `docs/V1_FOUNDATION_BACKBONE.md`
8. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
9. `docs/CURRICULUM_DOMAIN_MAP.md`
10. `docs/LEARNING_ENGINE_SPEC.md`
11. `docs/PREREQUISITE_POLICY_SPEC.md`
12. `docs/MASTERY_FORMULA_V0.md`
13. `docs/RETENTION_FORGETTING_SPEC.md`
14. `docs/ENGLISH_FOUNDATION_RULES.md`
15. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
16. `docs/V1_SCOPE.md`
17. `docs/V1_SUCCESS_CRITERIA.md`
18. `docs/PROJECT_MEMORY_PROTOCOL.md`

5D başlamadan fresh PRE-STEP GitHub refresh zorunludur.
""",
)

# MASTER_PLAN
replace_once(
    "docs/MASTER_PLAN.md",
    "- D-051: 5B final knowledge-graph contract `KGC-v0`; canonical file `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.\n",
    "- D-051: 5B final knowledge-graph contract `KGC-v0`; canonical file `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.\n- D-052: 5C final V1 foundation backbone `FBB-v0`; canonical file `docs/V1_FOUNDATION_BACKBONE.md`.\n",
)
replace_once(
    "docs/MASTER_PLAN.md",
    "### [ ] 5C — İlk 8–12 haftalık curriculum backbone — **AKTİF**\n### [ ] 5D — Graph architecture QA\n- cycle/dead-end,\n- hidden prerequisite,\n- duplicate canonical Skill,\n- scalability/versioning.",
    "### [x] 5C — İlk 8–12 haftalık curriculum backbone — FBB-v0 / D-052\n**Final:** `docs/V1_FOUNDATION_BACKBONE.md`\n\n**5C final coverage:**\n- 8–12 hafta = scope-equivalent, calendar gate değil,\n- zero-entry Computer/Programming bridge,\n- Python/C/Linux/Git/Shell/early DS&A/parallel English seed subgraph,\n- KGC-v0 Skill/Objective authoring-seed skeleton,\n- PRG-v0 hard/soft prerequisite edges,\n- scope-relative technical/English/professional-workflow requirements,\n- GRE/QAB/RVR evidence-retention-diagnostic-remediation anchors,\n- 6A/6C ratification lifecycle,\n- AŞAMA 15 authoring handoff + 5D QA fixtures.\n\n### [ ] 5D — Graph architecture QA — **AKTİF**\n- cycle/dead-end,\n- hidden prerequisite,\n- duplicate canonical Skill / Topic reuse,\n- reachability / branch isolation,\n- scalability/versioning / migration handoff.",
)
replace_once(
    "docs/MASTER_PLAN.md",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5B`  \n**Aktif:** **`5C — İlk 8–12 haftalık curriculum backbone`**\n\nBir sonraki yürütme: **5C başlamadan yeni PRE-STEP GitHub refresh → V1 başlangıç curriculum backbone → POST-STEP D-050 sync + stale-reference audit.**",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5C`  \n**Aktif:** **`5D — Graph architecture QA`**\n\nBir sonraki yürütme: **5D başlamadan yeni PRE-STEP GitHub refresh → FBB-v0 graph architecture QA → POST-STEP D-050 sync + stale-reference audit.**",
)

# PROJECT_CONTEXT
replace_once(
    "PROJECT_CONTEXT.md",
    "**D-051 / KGC-v0:** canonical `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`. Organization layer ile capability identity ayrıldı; Skill reusable canonical identity, Topic↔Skill many-to-many, Objective exactly-one-Skill, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness, immutable graph versioning ve conservative migration contract'ı kilitlendi.\n",
    "**D-051 / KGC-v0:** canonical `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`. Organization layer ile capability identity ayrıldı; Skill reusable canonical identity, Topic↔Skill many-to-many, Objective exactly-one-Skill, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness, immutable graph versioning ve conservative migration contract'ı kilitlendi.\n\n**D-052 / FBB-v0:** canonical `docs/V1_FOUNDATION_BACKBONE.md`. V1 başlangıç seed subgraph'ı zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English olarak tanımlandı. 8–12 hafta calendar gate değil scope-equivalent'tır; Skill/Objective IDs 6A/6C öncesi `authoring_seed` lifecycle'ındadır.\n",
)
replace_once(
    "PROJECT_CONTEXT.md",
    "  - 5B ✅ KGC-v0 / D-051\n  - **5C 🟡 İlk 8–12 haftalık curriculum backbone — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n  - 5D ⬜",
    "  - 5B ✅ KGC-v0 / D-051\n  - 5C ✅ FBB-v0 / D-052\n  - **5D 🟡 Graph architecture QA — AKTİF, HENÜZ YÜRÜTÜLMEDİ**",
)
replace_once(
    "PROJECT_CONTEXT.md",
    "**Sıradaki numaralı çalışma 5C'dir.** 5C başlamadan fresh PRE-STEP GitHub refresh zorunludur.",
    "**Sıradaki numaralı çalışma 5D'dir.** 5D başlamadan fresh PRE-STEP GitHub refresh zorunludur.",
)

# START_HERE
replace_once(
    "docs/START_HERE.md",
    "### D-051 — KGC-v0\n5B final graph contract `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` içinde versioned curriculum identity, Topic↔Skill placement, Skill prerequisite, Objective evidence profile, scope-relative requirement, retention/remediation/English/professional attribution ve conservative graph migration semantics'ini kilitledi.\n",
    "### D-051 — KGC-v0\n5B final graph contract `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` içinde versioned curriculum identity, Topic↔Skill placement, Skill prerequisite, Objective evidence profile, scope-relative requirement, retention/remediation/English/professional attribution ve conservative graph migration semantics'ini kilitledi.\n\n### D-052 — FBB-v0\n5C final V1 foundation backbone `docs/V1_FOUNDATION_BACKBONE.md` içinde zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English seed subgraph'ını tanımladı. 8–12 hafta calendar gate değildir; seed IDs 6A/6C ratification öncesi learner-published değildir.\n",
)
replace_once(
    "docs/START_HERE.md",
    "  - 5B ✅ KGC-v0 / D-051\n  - 5C 🟡 İlk 8–12 haftalık curriculum backbone\n  - 5D ⬜",
    "  - 5B ✅ KGC-v0 / D-051\n  - 5C ✅ FBB-v0 / D-052\n  - 5D 🟡 Graph architecture QA",
)
replace_once(
    "docs/START_HERE.md",
    "11. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`\n11b. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    "11. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`\n11a. `docs/V1_FOUNDATION_BACKBONE.md`\n11b. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
)
replace_once(
    "docs/START_HERE.md",
    "- 5B ✅ `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051\n- 5C 🟡 İlk 8–12 haftalık curriculum backbone",
    "- 5B ✅ `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051\n- 5C ✅ `FBB-v0 — V1 Foundation Backbone` / D-052\n- 5D 🟡 Graph architecture QA",
)
replace_from_heading(
    "docs/START_HERE.md",
    "## 9. Güncel çalışma konumu",
    """
## 9. Güncel çalışma konumu

**Aktif:** **`5D — Graph architecture QA`**  
**5D henüz yürütülmedi.**

5D, `docs/V1_FOUNDATION_BACKBONE.md` içindeki FBB-v0 authoring-seed graph'ını KGC-v0/PRG-v0 invariants altında cycle, dead-end, hidden prerequisite, duplicate semantic Skill, accidental English/global gate, branch isolation ve Objective reachability açısından doğrulayacak. Production content yazmayacak.

5D başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044–D-052 kararlarını oku; D-043 geri çekilmiştir. PROJECT_CONTEXT, CURRICULUM_DOMAIN_MAP, CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT, V1_FOUNDATION_BACKBONE, HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 5D — Graph architecture QA; 5D henüz yürütülmedi.`
""",
)

# PROGRESS_LOG
append_once(
    "docs/PROGRESS_LOG.md",
    "### 2026-08-25 — 5C V1 Foundation Backbone tamamlandı",
    """
---

### 2026-08-25 — 5C V1 Foundation Backbone tamamlandı

**PRE-STEP GitHub refresh**
- Kullanıcı onayı sonrası fresh olarak `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `PROJECT_CONTEXT` ve 5C için doğrudan ilgili KGC/PDM/Learning Engine/PRG/GRE/RVR/English/Granular Map/V1 scope-success/memory protocol belgeleri yeniden okundu.
- 5B'nin KGC-v0 / D-051 ile tamamlandığı, 5C'nin gerçek aktif adım olduğu ve henüz yürütülmediği doğrulandı.

**Research/Coding/Test AI kararı**
- Ayrı Research AI kullanılmadı: 5C external full-route coverage veya current-industry araştırması değil, accepted PDM/KGC/V1 contracts üzerinde bounded foundation seed-subgraph formalizasyonudur.
- Physical coding yoktur. Independent graph architecture QA ayrı numaralı 5D adımıdır.
- External coverage/current-industry/prerequisite Research AI doğrulaması 6H'de zorunlu kalır.

**Final model: `FBB-v0 — V1 Foundation Backbone` / D-052**
- 8–12 hafta calendar unlock değil scope-equivalent content envelope olarak tanımlandı.
- Zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English organization/capability seed'i oluşturuldu.
- KGC-v0 uyumlu Skill/Objective authoring-seed logical IDs tanımlandı; 6A/6C öncesi learner-published değildir.
- Technical/English/professional workflow scopes ayrıldı; English global technical gate olmadı.
- Shared mental-model Skills ile language-specific production Skills ayrıldı.
- Initial PRG-v0 hard/soft Skill prerequisite edges ve branch-isolation semantics tanımlandı.
- GRE/QAB/RVR uyumlu evidence templates, retention/diagnostic/remediation anchors ve AŞAMA 15 handoff'u tanımlandı.
- 5D için cycle/dead-end/hidden prerequisite/duplicate/reachability fixture seti oluşturuldu.

**POST-STEP sync**
- Living state 5C tamamlandı / 5D aktif-henüz-yürütülmedi olarak senkronlandı.
- `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `MASTER_PLAN`, `PROJECT_CONTEXT`, `START_HERE`, `DECISIONS` güncellendi.
- `CURRICULUM`, `GRANULAR_CAPABILITY_MAP_PLAN` ve README yeni FBB-v0 pointer'ına hizalandı.
- Repo-wide stale 5C active/not-executed ve FBB-v0 pointer audit'i uygulandı; historical log referansları historical olarak korundu.

**Sonraki kesin adım:** `5D — Graph architecture QA`.
5D başlamadan fresh PRE-STEP GitHub refresh zorunlu.
""",
)

# CURRICULUM summary
replace_once(
    "docs/CURRICULUM.md",
    "**Durum:** 5A DOMAIN BACKBONE + 5B KNOWLEDGE GRAPH CONTRACT TAMAMLANDI / DETAIL AŞAMA 6'DA  \n**Canonical kararlar:** D-041, D-042, D-044, D-049, D-051  \n**5A ana kaynak:** `docs/CURRICULUM_DOMAIN_MAP.md`  \n**5B graph contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`  \n**Granular decomposition charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    "**Durum:** 5A DOMAIN BACKBONE + 5B GRAPH CONTRACT + 5C V1 FOUNDATION BACKBONE TAMAMLANDI / DETAIL AŞAMA 6'DA  \n**Canonical kararlar:** D-041, D-042, D-044, D-049, D-051, D-052  \n**5A ana kaynak:** `docs/CURRICULUM_DOMAIN_MAP.md`  \n**5B graph contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`  \n**5C V1 foundation backbone:** `docs/V1_FOUNDATION_BACKBONE.md`  \n**Granular decomposition charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
)
replace_once(
    "docs/CURRICULUM.md",
    "Bu dosya hızlı curriculum özetidir. 5A domain-level canonical ilişkileri `docs/CURRICULUM_DOMAIN_MAP.md`; 5B entity/relation/version/migration sözleşmesini `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` tanımlar. Gerçek Module/Topic/Skill/Learning Objective dataset'i AŞAMA 6 tamamlanmadan “full curriculum” sayılmaz.",
    "Bu dosya hızlı curriculum özetidir. 5A domain-level canonical ilişkileri `docs/CURRICULUM_DOMAIN_MAP.md`; 5B entity/relation/version/migration sözleşmesini `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`; 5C V1 başlangıç authoring-seed subgraph'ını `docs/V1_FOUNDATION_BACKBONE.md` tanımlar. Gerçek full-route Module/Topic/Skill/Learning Objective dataset'i AŞAMA 6 tamamlanmadan “full curriculum” sayılmaz.",
)
replace_once(
    "docs/CURRICULUM.md",
    "5B ✅ = KGC-v0 graph / metadata contract\n5C = first 8–12 week V1 backbone\n5D = graph architecture QA",
    "5B ✅ = KGC-v0 graph / metadata contract\n5C ✅ = FBB-v0 first 8–12 week scope-equivalent V1 foundation backbone\n5D = graph architecture QA — active next",
)

# GRANULAR_CAPABILITY_MAP_PLAN handoff pointer
replace_once(
    "docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
    "**5B canonical schema contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` / KGC-v0 / D-051\n",
    "**5B canonical schema contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` / KGC-v0 / D-051\n**5C V1 seed input:** `docs/V1_FOUNDATION_BACKBONE.md` / FBB-v0 / D-052\n",
)

# README navigation + stable FBB summary
replace_once(
    "README.md",
    "- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — versioned knowledge-graph schema / KGC-v0\n- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — full route'u ölçülebilir alt becerilere ayıracak AŞAMA 6 charter'ı",
    "- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — versioned knowledge-graph schema / KGC-v0\n- `docs/V1_FOUNDATION_BACKBONE.md` — V1 başlangıç capability seed-subgraph / FBB-v0\n- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — full route'u ölçülebilir alt becerilere ayıracak AŞAMA 6 charter'ı",
)
replace_once(
    "README.md",
    "## Temel Ürün İlkesi",
    "## D-052 — V1 Foundation Backbone\n\nİlk 8–12 haftalık V1 başlangıç scope'u `docs/V1_FOUNDATION_BACKBONE.md` içinde FBB-v0 olarak tanımlıdır. Bu bir calendar unlock planı değildir; zero-entry bridge + Python/C/Linux/Git/Shell/early DS&A/parallel Technical English için KGC-v0 uyumlu authoring-seed subgraph'tır. Production lesson/task content'i AŞAMA 15'te, 6A/6C/6H ratification ve QA sonrasında üretilir.\n\n## Temel Ürün İlkesi",
)

# Final semantic audit of living state
living = [
    "PROJECT_CONTEXT.md",
    "docs/START_HERE.md",
    "docs/HANDOFF_STATE.md",
    "docs/STEP_STATUS.md",
    "docs/EXECUTION_INDEX.md",
    "docs/MASTER_PLAN.md",
]
required = {
    "PROJECT_CONTEXT.md": ["5C ✅ FBB-v0 / D-052", "5D 🟡 Graph architecture QA"],
    "docs/START_HERE.md": ["D-052 — FBB-v0", "5D 🟡 Graph architecture QA", "5D henüz yürütülmedi"],
    "docs/HANDOFF_STATE.md": ["D-052 / 5C final özeti", "Aktif:** `5D — Graph architecture QA`", "5D henüz yürütülmedi"],
    "docs/STEP_STATUS.md": ["5C — İlk 8–12 haftalık curriculum backbone** | ✅", "5D — Graph architecture QA** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **5C — İlk 8–12 haftalık curriculum backbone**", "[ ] **5D — Graph architecture QA** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 5C — İlk 8–12 haftalık curriculum backbone — FBB-v0 / D-052", "[ ] 5D — Graph architecture QA — **AKTİF**"],
}
for path in living:
    text = read(path)
    for needle in required[path]:
        if needle not in text:
            raise SystemExit(f"living audit fail: {path} missing {needle!r}")

for path in living:
    text = read(path)
    for stale in [
        "**Aktif:** **`5C — İlk 8–12 haftalık curriculum backbone`**",
        "5C 🟡 İlk 8–12 haftalık curriculum backbone",
        "5C henüz yürütülmedi",
    ]:
        if stale in text:
            raise SystemExit(f"stale living state in {path}: {stale}")

for path in [
    "README.md",
    "PROJECT_CONTEXT.md",
    "docs/START_HERE.md",
    "docs/HANDOFF_STATE.md",
    "docs/CURRICULUM.md",
    "docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
]:
    if "V1_FOUNDATION_BACKBONE.md" not in read(path):
        raise SystemExit(f"FBB pointer missing in {path}")

if "## D-052 — V1 foundation backbone = FBB-v0" not in read("docs/DECISIONS.md"):
    raise SystemExit("D-052 missing")

print("5C POST-SYNC LIVING-STATE AUDIT: PASS")
