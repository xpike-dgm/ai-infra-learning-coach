from pathlib import Path
import re


def read(path):
    return Path(path).read_text(encoding="utf-8")


def write(path, text):
    Path(path).write_text(text, encoding="utf-8")


def replace_once(path, old, new):
    text = read(path)
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected 1 occurrence, found {count}: {old[:90]!r}")
    write(path, text.replace(old, new, 1))


def append_once(path, marker, block):
    text = read(path)
    if marker not in text:
        write(path, text.rstrip() + "\n\n" + block.strip() + "\n")


# Verify the canonical 5B output exists before advancing state.
spec = read("docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md")
for needle in ["KGC-v0", "D-051", "**Durum:** TAMAMLANDI"]:
    if needle not in spec:
        raise SystemExit(f"5B spec missing {needle!r}")

# D-051 decision.
append_once(
    "docs/DECISIONS.md",
    "## D-051 — Curriculum knowledge graph contract = KGC-v0",
    """
## D-051 — Curriculum knowledge graph contract = KGC-v0
**Durum:** Kabul edildi — 2026-08-25

- 5B final modeli `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` oldu.
- Curriculum iki ayrı katmanda modellenir: `Domain → Module → Topic` organization; `Skill → Learning Objective` capability/evidence.
- Skill canonical reusable capability identity'dir; farklı Topic/Domain placements yeni learner mastery kaydı yaratmaz.
- Topic↔Skill many-to-many `TopicSkillLink` ile çözülür; Objective exactly one canonical Skill'e bağlıdır.
- Runtime prerequisite canonical olarak versioned `SkillPrerequisiteEdge` (`hard | soft`) kullanır; Domain/Module/Topic relations yalnız authoring guidance'dır.
- `required / critical / optional` geniş curriculum scope'larında scope-relative capability semantics'tir; global broad-domain boolean ile bütün rota kilitlenmez.
- Objective evidence profile GRE-v0 gate alanlarını taşır; QAB resource link'i mastery evidence'ın kendisi değildir.
- Skill retention profile RVR-v0 ile; diagnostic/remediation metadata runtime learner state'ten ayrı şekilde bağlanır.
- Technical English global technical hard gate değildir; language dependency yalnız gerçekten gerekli capability/task'ta explicit modellenir.
- Professional/project/capstone attribution granular Skill/Objective seviyesinde tutulur; project PASS bütün tagged capability'lere otomatik evidence vermez.
- Published entity/edge/graph semantic state immutable versionlanır; split/merge/refactor learner'a bedava mastery veremez ve historical evidence'ı sessizce silemez.
- Graph migration `fully_compatible | compatible_with_reverification | not_automatically_transferable` evidence compatibility semantiğini explicit taşır.
- Provenance/freshness ile stable systems concept ve fast-moving tool/vendor content ayrılır.
- D-028 gereği runtime full-graph scan'e dayanmaz; adjacency/reverse-dependency/index/cache contract'ı zorunludur, exact DB/index budgets 9C/9F/18E'ye bırakılır.
- 5C ilk 8–12 haftalık V1 alt graph'ını KGC-v0 ile kuracak; AŞAMA 6 full granular decomposition'u aynı contract üzerinde yapacaktır.

Ayrıntı: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.
""",
)

# EXECUTION_INDEX.
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- D-050: living-memory sync + repo-wide stale-reference audit zorunlu.\n",
    "- D-050: living-memory sync + repo-wide stale-reference audit zorunlu.\n- D-051: 5B final knowledge-graph contract `KGC-v0`.\n",
)
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **5B — Graph / Topic metadata sözleşmesi** **AKTİF** — Domain/Module/Topic/Skill/Objective schema, relations, prerequisite/evidence/retention/version contract",
    "- [x] **5B — Graph / Topic metadata sözleşmesi** — `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051",
)
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **5C — İlk 8–12 haftalık curriculum backbone** — V1 başlangıç alt grafiğinin iskeleti",
    "- [ ] **5C — İlk 8–12 haftalık curriculum backbone** **AKTİF** — V1 başlangıç alt grafiğinin iskeleti",
)
text = read("docs/EXECUTION_INDEX.md")
old_tail = """**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A`  
**Aktif:** **`5B — Graph / Topic metadata sözleşmesi`**

D-050 repository hygiene maintenance numaralı adım değildir; 5B hâlâ henüz yürütülmedi. 5B başlamadan yeni PRE-STEP GitHub refresh zorunludur."""
new_tail = """**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5B`  
**Aktif:** **`5C — İlk 8–12 haftalık curriculum backbone`**

5B KGC-v0 / D-051 ile tamamlandı. 5C henüz yürütülmedi; 5C başlamadan yeni PRE-STEP GitHub refresh zorunludur."""
if old_tail not in text:
    raise SystemExit("EXECUTION_INDEX: current-state tail mismatch")
write("docs/EXECUTION_INDEX.md", text.replace(old_tail, new_tail, 1))

# MASTER_PLAN.
replace_once(
    "docs/MASTER_PLAN.md",
    "- D-050: living-memory sync + repo-wide stale-reference audit her numaralı step kapanışında zorunludur.\n",
    "- D-050: living-memory sync + repo-wide stale-reference audit her numaralı step kapanışında zorunludur.\n- D-051: 5B final knowledge-graph contract `KGC-v0`; canonical file `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.\n",
)
replace_once(
    "docs/MASTER_PLAN.md",
    "### [ ] 5B — Graph / Topic metadata sözleşmesi — **AKTİF**\nKesinleştirilecek:",
    "### [x] 5B — Graph / Topic metadata sözleşmesi — KGC-v0 / D-051\n**Final:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`\n\n**5B final coverage:**",
)
replace_once(
    "docs/MASTER_PLAN.md",
    "- indexing/bounded traversal/performance contract.\n\n### [ ] 5C — İlk 8–12 haftalık curriculum backbone",
    """- indexing/bounded traversal/performance contract.

KGC-v0 ayrıca organization-vs-capability identity, scope-relative requirement, conservative graph migration, professional/project attribution ve D-028 bounded graph traversal invariants'ını kilitledi. Ayrı Research AI kullanılmadı; 5B mevcut accepted specs arasında internal contract formalizasyonuydu. External coverage/current-industry validation 6H'de zorunlu kalır.

### [ ] 5C — İlk 8–12 haftalık curriculum backbone — **AKTİF**""",
)

# STEP_STATUS rows and tail.
replace_once(
    "docs/STEP_STATUS.md",
    "| **5B — Graph / Topic metadata sözleşmesi** | 🟡 Aktif | Domain/Module/Topic/Skill/Objective graph schema ve metadata contract tasarlanacak. **Henüz yürütülmedi.** |\n| **5C–20** | ⬜ Bekliyor | 5B sonrası canonical sırada. |",
    "| **5B — Graph / Topic metadata sözleşmesi** | ✅ | KGC-v0 / D-051. Versioned curriculum knowledge graph contract tamamlandı. |\n| **5C — İlk 8–12 haftalık curriculum backbone** | 🟡 Aktif | KGC-v0 üzerinde V1 başlangıç alt graph iskeleti kurulacak. **Henüz yürütülmedi.** |\n| **5D–20** | ⬜ Bekliyor | 5C sonrası canonical sırada. |",
)
text = read("docs/STEP_STATUS.md")
marker = "## Son tamamlanan numaralı adım — 5A"
pos = text.find(marker)
if pos < 0:
    raise SystemExit("STEP_STATUS: old tail marker missing")
write("docs/STEP_STATUS.md", text[:pos] + """## Son tamamlanan numaralı adım — 5B

**Final:** `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051.  
Ana çıktı: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

5B kararları:
- organization layer `Domain → Module → Topic`; capability/evidence layer `Skill → Learning Objective`,
- Skill canonical identity; Topic↔Skill many-to-many placement,
- Objective exactly one canonical Skill,
- Skill→Skill hard/soft prerequisite edges PRG-v0 ile aynı semantics,
- scope-relative required/critical/optional capability requirements,
- GRE Objective evidence profile, QAB refs, RVR retention metadata,
- learner-specific weakness state ile static remediation metadata ayrımı,
- Technical English hidden-prerequisite guard,
- professional/project/capstone granular attribution,
- provenance/freshness + immutable entity/graph versions,
- conservative split/merge/refactor migration,
- bounded/indexed traversal/performance contract.

5B ayrı Research AI kullanmadı; existing accepted specs'i internal graph contract'a formalize etti. Full external coverage/current-industry/prerequisite Research QA 6H'de zorunlu kalır.

## Aktif adım — 5C İlk 8–12 haftalık curriculum backbone

**5C henüz yürütülmedi.**

5C başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.

5C'de özellikle:
- KGC-v0 ile V1 başlangıç subgraph'ı,
- Computer/Programming Foundations giriş köprüsü,
- Python + C + Linux/Git/Shell + gerekli early DS&A/English capability placements,
- canonical Skill/Objective IDs,
- initial hard/soft prerequisite edges,
- required/critical Objective profiles,
- V1 assessment/retention/diagnostic metadata anchors,
- first 8–12 week authoring scope sınırı,
- AŞAMA 15 production-content handoff'u

kesinleştirilecek.
""")

# HANDOFF_STATE: add decision and replace current-state tail.
replace_once(
    "docs/HANDOFF_STATE.md",
    "- **D-050:** living-memory sync + repo-wide stale-reference audit zorunlu; exact file-role matrix `PROJECT_MEMORY_PROTOCOL.md` içinde.\n",
    "- **D-050:** living-memory sync + repo-wide stale-reference audit zorunlu; exact file-role matrix `PROJECT_MEMORY_PROTOCOL.md` içinde.\n- **D-051:** KGC-v0 Versioned Curriculum Knowledge Graph Contract; 5B tamamlandı.\n",
)
text = read("docs/HANDOFF_STATE.md")
marker = "## 6. Tamamlanan aşamalar"
pos = text.find(marker)
if pos < 0:
    raise SystemExit("HANDOFF_STATE: old tail marker missing")
write("docs/HANDOFF_STATE.md", text[:pos] + """## 6. D-051 / 5B final özeti

Canonical: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

KGC-v0:
- curriculum organization (`Domain → Module → Topic`) ile capability/evidence (`Skill → Learning Objective`) ayrıdır,
- Skill canonical ve reusable identity'dir; Topic↔Skill many-to-many placement learner mastery state'ini çoğaltmaz,
- Objective exactly one Skill'e bağlıdır,
- runtime prerequisite versioned Skill→Skill hard/soft edge'dir; broad domain relations authoring guidance'dır,
- required/critical/optional scope-relative capability requirement olarak modellenir,
- GRE Objective evidence profile, QAB assessment refs, RVR retention, diagnostic/remediation ve English safety metadata graph'a bağlandı,
- professional/project/capstone attribution granular ve non-compensatory tutuldu,
- source/provenance/freshness ve immutable entity/graph versioning tanımlandı,
- split/merge/refactor migration historical evidence'ı korur fakat mastery'yi kör kopyalamaz,
- D-028 için adjacency/reverse-dependency/index/cache ve bounded traversal contract'ı tanımlandı.

5B ayrı Research AI kullanmadı; existing accepted contracts arasında schema/semantics formalizasyonuydu. External full-route coverage/current-industry/prerequisite Research QA 6H'de zorunlu kalır.

## 7. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 devam ediyor:
  - 5A ✅ PDM-v0 / D-049
  - 5B ✅ KGC-v0 / D-051
  - 5C 🟡 İlk 8–12 haftalık curriculum backbone — aktif, henüz yürütülmedi
  - 5D ⬜
- AŞAMA 6–20 ⬜

## 8. Güncel kesin konum

**Aktif:** `5C — İlk 8–12 haftalık curriculum backbone`  
**5C henüz yürütülmedi.**

## 9. 5C'de kesinleştirilecekler

Ana soru:
> KGC-v0 ve PDM-v0 kullanılarak, V1'in ilk 8–12 haftasını besleyecek fakat production lesson body yazmaya başlamayacak ilk executable curriculum subgraph nasıl kurulmalı?

Kesinleştirilecek:
- V1 başlangıç scope'u ve giriş köprüsü,
- early Domain/Module/Topic placements,
- canonical Skill/Objective skeleton,
- Python/C/Linux/Git/Shell/English early parallelism,
- gerekli early DS&A/memory/debugging foundations,
- hard/soft Skill prerequisite edges,
- Objective required/critical/evidence profiles,
- retention/diagnostic/remediation metadata anchors,
- V1 assessment-resource authoring ihtiyaçları,
- AŞAMA 15 production-content handoff'u,
- 5D graph QA için fixture/subgraph input'u.

5C full professional curriculum değildir ve AŞAMA 6 full-route decomposition'un yerine geçmez.

## 10. 5C için PRE-STEP doğrudan okunacaklar
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
8. `docs/CURRICULUM_DOMAIN_MAP.md`
9. `docs/LEARNING_ENGINE_SPEC.md`
10. `docs/PREREQUISITE_POLICY_SPEC.md`
11. `docs/MASTERY_FORMULA_V0.md`
12. `docs/RETENTION_FORGETTING_SPEC.md`
13. `docs/ENGLISH_FOUNDATION_RULES.md`
14. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
15. `docs/V1_SCOPE.md`
16. `docs/V1_SUCCESS_CRITERIA.md`
17. `docs/PROJECT_MEMORY_PROTOCOL.md`

5C başlamadan fresh PRE-STEP GitHub refresh zorunludur.
""")

# PROJECT_CONTEXT.
replace_once("PROJECT_CONTEXT.md", "## 7. Curriculum backbone — 5A tamamlandı", "## 7. Curriculum backbone / knowledge graph — 5A–5B tamamlandı")
replace_once(
    "PROJECT_CONTEXT.md",
    "- Domain-level relations authoring guidance; runtime hard prerequisite Skill→Skill PRG-v0.\n\n## 8. İngilizce",
    """- Domain-level relations authoring guidance; runtime hard prerequisite Skill→Skill PRG-v0.

**D-051 / KGC-v0:** canonical `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`. Organization layer ile capability identity ayrıldı; Skill reusable canonical identity, Topic↔Skill many-to-many, Objective exactly-one-Skill, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness, immutable graph versioning ve conservative migration contract'ı kilitlendi.

## 8. İngilizce""",
)
replace_once(
    "PROJECT_CONTEXT.md",
    "  - 5A ✅ PDM-v0 / D-049\n  - **5B 🟡 Graph / Topic metadata sözleşmesi — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n  - 5C–5D ⬜",
    "  - 5A ✅ PDM-v0 / D-049\n  - 5B ✅ KGC-v0 / D-051\n  - **5C 🟡 İlk 8–12 haftalık curriculum backbone — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n  - 5D ⬜",
)
replace_once(
    "PROJECT_CONTEXT.md",
    "**Sıradaki numaralı çalışma 5B'dir.** 5B başlamadan fresh PRE-STEP GitHub refresh zorunludur.",
    "**Sıradaki numaralı çalışma 5C'dir.** 5C başlamadan fresh PRE-STEP GitHub refresh zorunludur.",
)

# START_HERE.
replace_once(
    "docs/START_HERE.md",
    "- Stable specs active step'i kopyalamaz; yalnız davranış/cross-reference değişirse güncellenir.\n\n## 4. Güncel stage mapping",
    """- Stable specs active step'i kopyalamaz; yalnız davranış/cross-reference değişirse güncellenir.

### D-051 — KGC-v0
5B final graph contract `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` içinde versioned curriculum identity, Topic↔Skill placement, Skill prerequisite, Objective evidence profile, scope-relative requirement, retention/remediation/English/professional attribution ve conservative graph migration semantics'ini kilitledi.

## 4. Güncel stage mapping""",
)
replace_once(
    "docs/START_HERE.md",
    "  - 5A ✅ PDM-v0\n  - 5B 🟡 Graph / Topic metadata sözleşmesi\n  - 5C–5D ⬜",
    "  - 5A ✅ PDM-v0\n  - 5B ✅ KGC-v0 / D-051\n  - 5C 🟡 İlk 8–12 haftalık curriculum backbone\n  - 5D ⬜",
)
replace_once(
    "docs/START_HERE.md",
    "10. `docs/CURRICULUM_DOMAIN_MAP.md`\n11. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    "10. `docs/CURRICULUM_DOMAIN_MAP.md`\n11. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`\n11b. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
)
replace_once(
    "docs/START_HERE.md",
    "Bu lineer takvim değildir. PDM-v0 high-level boundaries'i, 5B graph contract'ı ve AŞAMA 6 granular Skill prerequisites gerçek executable route'u belirleyecek.",
    "Bu lineer takvim değildir. PDM-v0 high-level boundaries'i, KGC-v0 graph contract'ı ve AŞAMA 6 granular Skill prerequisites gerçek executable route'u belirleyecek.",
)
replace_once(
    "docs/START_HERE.md",
    "### AŞAMA 5 ilerlemesi\n- 5A ✅ `PDM-v0 — Professional Domain Backbone` / D-049\n- 5B 🟡 Graph / Topic metadata sözleşmesi",
    "### AŞAMA 5 ilerlemesi\n- 5A ✅ `PDM-v0 — Professional Domain Backbone` / D-049\n- 5B ✅ `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051\n- 5C 🟡 İlk 8–12 haftalık curriculum backbone",
)
text = read("docs/START_HERE.md")
p1 = text.find("## 9. Güncel çalışma konumu")
p2 = text.find("## 10. Yeni sohbet için kısa komut")
if p1 < 0 or p2 <= p1:
    raise SystemExit("START_HERE: section 9/10 markers missing")
section9 = """## 9. Güncel çalışma konumu

**Aktif:** **`5C — İlk 8–12 haftalık curriculum backbone`**  
**5C henüz yürütülmedi.**

5C, PDM-v0 + KGC-v0 üzerinde V1'in ilk 8–12 haftalık executable curriculum subgraph'ını kuracak: giriş köprüsü, early Domain/Module/Topic placements, canonical Skill/Objective skeleton, hard/soft prerequisites, Objective evidence/criticality, retention/diagnostic/remediation ve English safety metadata anchor'ları. Production lesson/task body AŞAMA 15'e aittir.

5C başlamadan yeni PRE-STEP GitHub refresh zorunlu.

"""
text = text[:p1] + section9 + text[p2:]
write("docs/START_HERE.md", text)
replace_once(
    "docs/START_HERE.md",
    "> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044–D-050 kararlarını oku; D-043 geri çekilmiştir. PROJECT_CONTEXT, CURRICULUM_DOMAIN_MAP, HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 5B — Graph / Topic metadata sözleşmesi; 5B henüz yürütülmedi.`",
    "> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044–D-051 kararlarını oku; D-043 geri çekilmiştir. PROJECT_CONTEXT, CURRICULUM_DOMAIN_MAP, CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT, HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 5C — İlk 8–12 haftalık curriculum backbone; 5C henüz yürütülmedi.`",
)

# CURRICULUM summary.
replace_once(
    "docs/CURRICULUM.md",
    "**Durum:** 5A DOMAIN BACKBONE TAMAMLANDI / DETAIL AŞAMA 6'DA  \n**Canonical kararlar:** D-041, D-042, D-044, D-049  \n**5A ana kaynak:** `docs/CURRICULUM_DOMAIN_MAP.md`  \n**Granular decomposition charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    "**Durum:** 5A DOMAIN BACKBONE + 5B KNOWLEDGE GRAPH CONTRACT TAMAMLANDI / DETAIL AŞAMA 6'DA  \n**Canonical kararlar:** D-041, D-042, D-044, D-049, D-051  \n**5A ana kaynak:** `docs/CURRICULUM_DOMAIN_MAP.md`  \n**5B graph contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`  \n**Granular decomposition charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
)
replace_once(
    "docs/CURRICULUM.md",
    "Bu dosya hızlı curriculum özetidir. 5A sonrası domain-level canonical ilişkiler ve sınırlar `docs/CURRICULUM_DOMAIN_MAP.md` içindedir. Gerçek Module/Topic/Skill/Learning Objective dataset'i AŞAMA 6 tamamlanmadan “full curriculum” sayılmaz.",
    "Bu dosya hızlı curriculum özetidir. 5A domain-level canonical ilişkileri `docs/CURRICULUM_DOMAIN_MAP.md`; 5B entity/relation/version/migration sözleşmesini `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` tanımlar. Gerçek Module/Topic/Skill/Learning Objective dataset'i AŞAMA 6 tamamlanmadan “full curriculum” sayılmaz.",
)
replace_once("docs/CURRICULUM.md", "5B = graph / metadata contract\n5C = first 8–12 week V1 backbone", "5B ✅ = KGC-v0 graph / metadata contract\n5C = first 8–12 week V1 backbone")

# GRANULAR plan: consume 5B contract rather than redefining it.
replace_once(
    "docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
    "**Karar:** D-044\n",
    "**Karar:** D-044  \n**5B canonical schema contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` / KGC-v0 / D-051\n",
)
replace_once(
    "docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
    "## 6. Her node için planlanacak metadata\n\nAŞAMA 6 final haritasında, uygun seviyede en az şu bilgiler bulunmalıdır:",
    "## 6. Her node için planlanacak metadata\n\nAŞAMA 6 final haritası KGC-v0 entity/relation/version contract'ına uymalıdır. Uygun seviyede en az şu bilgiler bulunmalıdır:",
)

# PDM 5B handoff becomes a completed pointer.
replace_once(
    "docs/CURRICULUM_DOMAIN_MAP.md",
    "Bu şema **curriculum display shortcut**'ıdır. AŞAMA 5B/6 gerçek graph'ta bazı branch'lerin daha erken paralel ilerlemesine izin verecektir.",
    "Bu şema **curriculum display shortcut**'ıdır. KGC-v0 + AŞAMA 6 granular graph bazı branch'lerin daha erken paralel ilerlemesine izin verir; gerçek runtime gate PRG-v0 Skill edges'idir.",
)
replace_once(
    "docs/CURRICULUM_DOMAIN_MAP.md",
    "# 13. AŞAMA 5B handoff\n\n5B graph/metadata sözleşmesi aşağıdaki 5A çıktısını formalize etmelidir:",
    "# 13. AŞAMA 5B sonucu — KGC-v0\n\n5B tamamlandı. Canonical graph/metadata sözleşmesi `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` / KGC-v0 ile aşağıdaki 5A çıktısını formalize eder:",
)
replace_once(
    "docs/CURRICULUM_DOMAIN_MAP.md",
    "5B bu document'taki ASCII route'u doğrudan hard-coded linear sequence'e dönüştürmemelidir.",
    "KGC-v0 bu document'taki ASCII route'u hard-coded linear sequence'e dönüştürmez; organization placement ile runtime Skill prerequisites ayrıdır.",
)
replace_once(
    "docs/CURRICULUM_DOMAIN_MAP.md",
    "Bu text order **roadmap summary**'dir; canonical planner davranışı linear değildir. Gerçek executable curriculum graph AŞAMA 5B/6'da Skill-level edges ile oluşturulur.",
    "Bu text order **roadmap summary**'dir; canonical planner davranışı linear değildir. Graph contract KGC-v0 ile kilitlenmiştir; gerçek executable capability dataset ve Skill-level edge seti AŞAMA 6'da bu contract üzerinde oluşturulur.",
)

# Stable master context.
replace_once(
    "docs/PROJECT_MASTER_CONTEXT.md",
    "**Son büyük kapsam/plan güncellemesi:** 2026-08-25 — D-041 / D-042 / D-044 / D-049  ",
    "**Son büyük kapsam/plan güncellemesi:** 2026-08-25 — D-041 / D-042 / D-044 / D-049 / D-051  ",
)
replace_once(
    "docs/PROJECT_MASTER_CONTEXT.md",
    "AŞAMA 6 ayrıca canonical IDs, prerequisite edges, required/criticality, evidence type, retention relevance, diagnostic/remediation tags, cross-domain reuse, project/capstone mapping ve freshness/version metadata tasarlayacaktır.\n\nAyrıntı: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.",
    "5B / KGC-v0 canonical identity, prerequisite edge, requirement/evidence, retention/remediation, English safety, professional/project attribution, provenance/freshness ve version/migration contract'ını kilitlemiştir. AŞAMA 6 bu contract'ı kullanarak gerçek granular capability dataset'ini üretecek; 6A exact naming/granularity standardını finalize edecektir.\n\nAyrıntı: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` ve `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.",
)
replace_once(
    "docs/PROJECT_MASTER_CONTEXT.md",
    "- domain relation authoring guidance'dır; runtime hard prerequisite yine Skill→Skill PRG-v0'dır.\n\n---\n\n# 11. Adaptif Learning / Planner Motoru",
    "- domain relation authoring guidance'dır; runtime hard prerequisite yine Skill→Skill PRG-v0'dır.\n\n## KGC-v0 — D-051\nKnowledge graph schema organization placement ile canonical capability identity'yi ayırır; Skill reusable identity, Objective atomic evidence target'ıdır. Topic↔Skill many-to-many, Skill→Skill hard/soft prerequisites, scope-relative requirement, immutable graph/entity versioning ve conservative migration semantics canonicaldır.\n\n---\n\n# 11. Adaptif Learning / Planner Motoru",
)

# README stable navigation and architecture overview.
replace_once(
    "README.md",
    "- `docs/CURRICULUM_DOMAIN_MAP.md` — high-level professional domain backbone / PDM-v0\n",
    "- `docs/CURRICULUM_DOMAIN_MAP.md` — high-level professional domain backbone / PDM-v0\n- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — versioned knowledge-graph schema / KGC-v0\n",
)
replace_once(
    "README.md",
    "## D-049 — Professional Domain Backbone\n\nHigh-level curriculum envelope `docs/CURRICULUM_DOMAIN_MAP.md` içinde PDM-v0 olarak tanımlıdır. 23 ana route family, parallel/common/core/supporting/target/professional-evidence rolleriyle birbirine bağlanır. Bu harita takvim değildir; runtime prerequisite'ler Skill seviyesinde çözülür.\n\n## Temel Ürün İlkesi",
    """## D-049 — Professional Domain Backbone

High-level curriculum envelope `docs/CURRICULUM_DOMAIN_MAP.md` içinde PDM-v0 olarak tanımlıdır. 23 ana route family, parallel/common/core/supporting/target/professional-evidence rolleriyle birbirine bağlanır. Bu harita takvim değildir; runtime prerequisite'ler Skill seviyesinde çözülür.

## D-051 — Versioned Curriculum Knowledge Graph

5B çıktısı `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` içinde KGC-v0 olarak tanımlıdır. Organization placement (`Domain/Module/Topic`) canonical capability identity'den (`Skill/Objective`) ayrıdır; Topic↔Skill many-to-many reuse, Skill→Skill hard/soft prerequisites, scope-relative requirement/evidence metadata, provenance/freshness, immutable graph versions ve conservative migration kuralları burada kilitlenmiştir.

## Temel Ürün İlkesi""",
)

# PROGRESS_LOG append-only record.
append_once(
    "docs/PROGRESS_LOG.md",
    "### 2026-08-25 — 5B Curriculum Knowledge Graph Contract tamamlandı",
    """
---

### 2026-08-25 — 5B Curriculum Knowledge Graph Contract tamamlandı

**PRE-STEP GitHub refresh**
- Fresh olarak `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `PROJECT_CONTEXT` ve 5B için handoff'ta listelenen doğrudan ilgili specs okundu.
- 5A'nın tamamlandığı, 5B'nin gerçek aktif adım olduğu ve henüz yürütülmediği doğrulandı.
- PDM-v0, Learning Engine, PRG-v0, GRE-v0, RVR-v0, QAB-v0, AIV-v0, English foundation ve AŞAMA 6 charter constraints birlikte yeniden kontrol edildi.

**Research/Coding/Test AI kararı**
- Ayrı Research AI kullanılmadı: 5B external coverage/job-market araştırması değil, mevcut accepted canonical specs arasında internal logical graph contract formalizasyonudur.
- Coding/Test AI kullanılmadı: bu adım physical DB/runtime implementation değil spec/architecture contract'tır.
- Full coverage/current-industry/hidden-prerequisite independent Research QA planlandığı gibi 6H'de zorunlu kalır.

**Final model: `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051**
- Organization (`Domain → Module → Topic`) ve capability/evidence (`Skill → Learning Objective`) katmanları ayrıldı.
- Skill canonical reusable identity, Objective exactly-one-Skill atomic evidence target olarak kilitlendi.
- Topic↔Skill many-to-many placement; Skill→Skill hard/soft PRG edge contract tanımlandı.
- Scope-relative required/critical/optional capability requirement semantiği getirildi.
- GRE Objective evidence profile, QAB binding, RVR retention, diagnostic/remediation ve Technical English safety metadata bağlandı.
- Professional/project/capstone attribution granular ve non-compensatory yapıldı.
- Provenance/freshness, immutable entity/graph versioning ve conservative split/merge/refactor migration tanımlandı.
- D-028 için indexed/bounded traversal ve reverse-dependency/cache invariants tanımlandı.
- 5C V1 başlangıç subgraph handoff'u ve AŞAMA 6 full granular decomposition handoff'u açıklandı.

**POST-STEP sync**
- Living state 5B tamamlandı / 5C aktif-henüz-yürütülmedi olarak senkronlandı.
- `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `MASTER_PLAN`, `PROJECT_CONTEXT`, `START_HERE`, `DECISIONS` güncellendi.
- `CURRICULUM`, `CURRICULUM_DOMAIN_MAP`, `GRANULAR_CAPABILITY_MAP_PLAN`, `PROJECT_MASTER_CONTEXT` ve README yeni KGC-v0 canonical pointer'ına hizalandı.
- Repo-wide 5B active/not-executed ve graph-contract reference taraması uygulanarak living docs drift kontrolü yapıldı.

**Sonraki kesin adım:** `5C — İlk 8–12 haftalık curriculum backbone`.
5C başlamadan yeni PRE-STEP GitHub refresh zorunlu.
""",
)

# Final living-state audit.
checks = {
    "PROJECT_CONTEXT.md": ["5B ✅ KGC-v0 / D-051", "5C 🟡", "AKTİF, HENÜZ YÜRÜTÜLMEDİ"],
    "docs/START_HERE.md": ["5B ✅ `KGC-v0", "5C 🟡", "5C henüz yürütülmedi"],
    "docs/HANDOFF_STATE.md": ["5B ✅ KGC-v0 / D-051", "5C 🟡", "5C henüz yürütülmedi"],
    "docs/STEP_STATUS.md": ["5B — Graph / Topic metadata sözleşmesi** | ✅", "5C — İlk 8–12 haftalık curriculum backbone** | 🟡 Aktif", "5C henüz yürütülmedi"],
    "docs/EXECUTION_INDEX.md": ["[x] **5B — Graph / Topic metadata sözleşmesi**", "[ ] **5C — İlk 8–12 haftalık curriculum backbone** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 5B — Graph / Topic metadata sözleşmesi — KGC-v0 / D-051", "[ ] 5C — İlk 8–12 haftalık curriculum backbone — **AKTİF**"],
}
for path, needles in checks.items():
    text = read(path)
    for needle in needles:
        if needle not in text:
            raise SystemExit(f"{path}: missing post-state marker {needle!r}")

stale_needles = [
    "5B 🟡 Graph / Topic metadata sözleşmesi",
    "5B henüz yürütülmedi",
    "5B başlamadan fresh PRE-STEP",
    "5B başlamadan yeni PRE-STEP",
    "**Aktif:** **`5B — Graph / Topic metadata sözleşmesi`**",
]
for path in checks:
    text = read(path)
    for needle in stale_needles:
        if needle in text:
            raise SystemExit(f"{path}: stale 5B state remains: {needle!r}")

for path in [
    "README.md",
    "docs/CURRICULUM.md",
    "docs/CURRICULUM_DOMAIN_MAP.md",
    "docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
    "docs/PROJECT_MASTER_CONTEXT.md",
    "docs/START_HERE.md",
    "docs/HANDOFF_STATE.md",
    "PROJECT_CONTEXT.md",
]:
    if "CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md" not in read(path):
        raise SystemExit(f"{path}: KGC canonical pointer missing")

if "## D-051 — Curriculum knowledge graph contract = KGC-v0" not in read("docs/DECISIONS.md"):
    raise SystemExit("D-051 missing from DECISIONS")

print("\nRemaining Markdown lines containing 5B for manual stale-reference classification:")
for path in sorted(list(Path(".").glob("*.md")) + list(Path("docs").glob("*.md"))):
    hits = []
    for line_no, line in enumerate(read(path).splitlines(), 1):
        if "5B" in line:
            hits.append((line_no, line.strip()))
    if hits:
        print(f"[{path.as_posix()}]")
        for line_no, line in hits:
            print(f"  L{line_no}: {line}")

print("\n5B POST-SYNC CONSISTENCY AUDIT: PASS")
