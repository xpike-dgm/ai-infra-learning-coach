from pathlib import Path
import re


def read(path):
    return Path(path).read_text(encoding='utf-8')


def write(path, content):
    Path(path).write_text(content, encoding='utf-8')


def replace_once(path, old, new):
    s = read(path)
    count = s.count(old)
    if count != 1:
        raise SystemExit(f'{path}: expected exactly 1 match, found {count}: {old[:120]!r}')
    write(path, s.replace(old, new, 1))


def regex_once(path, pattern, repl, flags=re.S):
    s = read(path)
    out, count = re.subn(pattern, repl, s, count=1, flags=flags)
    if count != 1:
        raise SystemExit(f'{path}: regex expected 1 match, found {count}: {pattern[:120]!r}')
    write(path, out)


def append_once(path, marker, block):
    s = read(path)
    if marker in s:
        return
    write(path, s.rstrip() + '\n\n' + block.strip() + '\n')


# Guard the 6A output exists and current state was 6A-active before sync.
spec = read('docs/GRANULARITY_NAMING_STANDARD.md')
if 'GNS-v0 — Granularity & Naming Standard' not in spec or 'D-054' not in spec:
    raise SystemExit('6A canonical spec missing GNS-v0/D-054 markers')
for p in ['PROJECT_CONTEXT.md','docs/START_HERE.md','docs/HANDOFF_STATE.md','docs/STEP_STATUS.md','docs/EXECUTION_INDEX.md','docs/MASTER_PLAN.md']:
    if '6A' not in read(p):
        raise SystemExit(f'{p}: expected pre-sync 6A context')

# ------------------------------------------------------------------
# EXECUTION_INDEX
# ------------------------------------------------------------------
idx='docs/EXECUTION_INDEX.md'
replace_once(idx,
'- D-053: 5D final foundation graph architecture QA `GQA-v0`; corrective seed patch PASS.\n',
'- D-053: 5D final foundation graph architecture QA `GQA-v0`; corrective seed patch PASS.\n- D-054: 6A final granularity/naming contract `GNS-v0`; semantic entity boundaries + stable logical ID rules.\n')
replace_once(idx,
'- [ ] **6A — Granularity + naming standardı** **AKTİF** — Domain/Module/Topic/Skill/Objective sınırları, canonical ID, over-fragmentation guard\n- [ ] **6B — Full-route decomposition blueprint** — bütün ana teknik/English rotası için ortak decomposition şablonu\n',
'- [x] **6A — Granularity + naming standardı** — `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054\n- [ ] **6B — Full-route decomposition blueprint** **AKTİF** — bütün ana teknik/English rotası için ortak decomposition şablonu\n')
regex_once(idx,
r'# Güncel Konum\n\n\*\*Tamamlanan:\*\*.*\Z',
'''# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A`  
**Aktif:** **`6B — Full-route decomposition blueprint`**

6A GNS-v0 / D-054 ile tamamlandı. 6B henüz yürütülmedi; 6B başlamadan fresh PRE-STEP GitHub refresh zorunludur.''')

# ------------------------------------------------------------------
# STEP_STATUS
# ------------------------------------------------------------------
status='docs/STEP_STATUS.md'
replace_once(status,
'| **6A — Granularity + naming standardı** | 🟡 Aktif | AŞAMA 6 naming/granularity contract tasarlanacak. **Henüz yürütülmedi.** |\n| **6B–20** | ⬜ Bekliyor | 6A sonrası canonical sırada. |',
'| **6A — Granularity + naming standardı** | ✅ | GNS-v0 / D-054. Semantic granularity + stable logical ID standardı tamamlandı. |\n| **6B — Full-route decomposition blueprint** | 🟡 Aktif | 23 route family için ortak decomposition/authoring blueprint tasarlanacak. **Henüz yürütülmedi.** |\n| **6C–20** | ⬜ Bekliyor | 6B sonrası canonical sırada. |')
regex_once(status,
r'## Son tamamlanan numaralı adım — 5D.*\Z',
'''## Son tamamlanan numaralı adım — 6A

**Final:** `GNS-v0 — Granularity & Naming Standard` / D-054.  
Ana çıktı: `docs/GRANULARITY_NAMING_STANDARD.md`.

6A sonucu:
- Domain/Module/Topic/Skill/Objective semantic sınırları kilitlendi,
- Skill granularity independent evidence/remediation/prerequisite/reuse temelli hale geldi,
- under/over-fragmentation guard'ları tanımlandı,
- shared vs language/tool/context-specific Skill split kriterleri tanımlandı,
- Objective atomicity/observable-action standardı tanımlandı,
- logical ID formatı stable/locale-independent/version-free yapıldı,
- display/localization/alias ile identity ayrıldı,
- FBB seed ratification/split/merge/normalize lifecycle'ı tanımlandı,
- 6B decomposition authoring handoff'u tanımlandı.

6A ayrı external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research QA 6H'de zorunlu kalır.

## Aktif adım — 6B Full-route decomposition blueprint

**6B henüz yürütülmedi.**

6B başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca fresh PRE-STEP GitHub refresh zorunludur.

6B'de özellikle:
- 23 route family için tek ortak decomposition row/template contract'ı,
- Domain→Module→Topic authoring blueprint'i,
- Skill/Objective candidate üretim akışı,
- duplicate resolver + shared Skill reuse akışı,
- source/provenance/freshness alanları,
- GNS-v0 reason-code/granularity review entegrasyonu,
- 6C–6F detailed-map paketlerinin ortak çıktı biçimi

kesinleştirilecek.''')

# ------------------------------------------------------------------
# HANDOFF_STATE — append decision summary then replace current-tail sections.
# ------------------------------------------------------------------
handoff='docs/HANDOFF_STATE.md'
replace_once(handoff,
'- **D-053:** GQA-v0 Foundation Graph Architecture QA; 5D corrective patch sonrası PASS.\n',
'- **D-053:** GQA-v0 Foundation Graph Architecture QA; 5D corrective patch sonrası PASS.\n- **D-054:** GNS-v0 Granularity & Naming Standard; 6A semantic decomposition/ID contract tamamlandı.\n')
regex_once(handoff,
r'## 9\. Tamamlanan aşamalar.*\Z',
'''## 9. D-054 / 6A final özeti

Canonical: `docs/GRANULARITY_NAMING_STANDARD.md`.

GNS-v0:
- organization (`Domain/Module/Topic`) ile capability/evidence (`Skill/Objective`) granularity sınırını operational hale getirir,
- yeni Skill kararını independent evidence + remediation + prerequisite + reuse ayrımına bağlar,
- under/over-fragmentation guard'larını tanımlar,
- shared mental-model capability ile language/tool-specific production capability ayrımını standartlaştırır,
- Objective'i exactly-one-Skill altında atomic observable evidence target olarak sınırlar,
- logical ID formatını lowercase ASCII dotted namespace + snake_case segment şeklinde; locale/order/version bağımsız olarak kilitler,
- week/stage/release/band/difficulty/role bilgisinin logical ID'ye gömülmesini yasaklar,
- display/localization/alias değişimini identity değişiminden ayırır,
- split/merge/re-home/objective-move işlemlerini KGC migration semantics'e bağlar,
- FBB authoring seed'leri için ratify/normalize/split/merge/rehome/deprecate/review status contract'ı tanımlar,
- 6B ortak decomposition authoring template'ine zorunlu alanları devreder.

6A external Research AI kullanmadı; 6H independent Research AI zorunluluğu korunur.

## 10. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6:
  - 6A ✅ GNS-v0 / D-054
  - 6B 🟡 Full-route decomposition blueprint — aktif, henüz yürütülmedi
  - 6C–6H ⬜
- AŞAMA 7–20 ⬜

## 11. Güncel kesin konum

**Aktif:** `6B — Full-route decomposition blueprint`  
**6B henüz yürütülmedi.**

## 12. 6B'de kesinleştirilecekler

Ana soru:
> GNS-v0 standardını 23 route family'nin tamamında tutarlı biçimde uygulayacak ortak decomposition authoring blueprint'i ve çıktı contract'ı nasıl olmalı?

Kesinleştirilecek:
- domain/module/topic decomposition row yapısı,
- Skill/Objective candidate authoring template'i,
- GNS-v0 granularity review reason-code kullanımı,
- duplicate resolver ve cross-domain shared Skill reuse workflow'u,
- prerequisite candidate declaration biçimi,
- evidence/remediation/retention/professional metadata authoring alanları,
- source/provenance/freshness capture,
- FBB seed mapping alanı,
- 6C–6F paketlerinin ortak machine-readable/QA-ready çıktı şekli.

6B gerçek full route node listesini tamamlamaz; ortak blueprint'i kilitler.

## 13. 6B için PRE-STEP doğrudan okunacaklar
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. `docs/GRANULARITY_NAMING_STANDARD.md`
8. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
9. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
10. `docs/CURRICULUM_DOMAIN_MAP.md`
11. `docs/V1_FOUNDATION_BACKBONE.md`
12. `docs/GRAPH_ARCHITECTURE_QA.md`
13. `docs/LEARNING_ENGINE_SPEC.md`
14. `docs/PREREQUISITE_POLICY_SPEC.md`
15. `docs/PROJECT_MEMORY_PROTOCOL.md`

6B başlamadan fresh PRE-STEP GitHub refresh zorunludur.''')

# ------------------------------------------------------------------
# MASTER_PLAN
# ------------------------------------------------------------------
plan='docs/MASTER_PLAN.md'
replace_once(plan,
'### [ ] 6A — Granularity + naming standardı — **AKTİF**\n### [ ] 6B — Full-route decomposition blueprint\n',
'''### [x] 6A — Granularity + naming standardı — GNS-v0 / D-054
**Final:** `docs/GRANULARITY_NAMING_STANDARD.md`

**6A final coverage:**
- Domain/Module/Topic/Skill/Objective semantic granularity boundaries,
- Capability Independence Test,
- under/over-fragmentation guards,
- Skill vs Objective split rule,
- shared vs language/tool/context-specific Skill policy,
- stable logical ID convention,
- display/localization/alias vs identity separation,
- version/split/merge/re-home migration rules,
- FBB seed ratification statuses,
- 6B decomposition-template handoff.

### [ ] 6B — Full-route decomposition blueprint — **AKTİF**
''')
regex_once(plan,
r'# Güncel Konum\n\n\*\*Tamamlanan:\*\*.*\Z',
'''# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A`  
**Aktif:** **`6B — Full-route decomposition blueprint`**

Bir sonraki yürütme: **6B başlamadan fresh PRE-STEP GitHub refresh → GNS-v0 üzerinde full-route decomposition blueprint → POST-STEP D-050 sync + stale-reference audit.**''')

# ------------------------------------------------------------------
# PROJECT_CONTEXT
# ------------------------------------------------------------------
ctx='PROJECT_CONTEXT.md'
replace_once(ctx,
'**D-053 / GQA-v0:** canonical `docs/GRAPH_ARCHITECTURE_QA.md`. 5D initial structural blockers ve hidden-prerequisite risklerini corrective seed patch ile düzeltti; hard graph DAG, TopicSkillLink/reuse explicit, English global-gate yok, F5D fixtures PASS.\n',
'''**D-053 / GQA-v0:** canonical `docs/GRAPH_ARCHITECTURE_QA.md`. 5D initial structural blockers ve hidden-prerequisite risklerini corrective seed patch ile düzeltti; hard graph DAG, TopicSkillLink/reuse explicit, English global-gate yok, F5D fixtures PASS.

**D-054 / GNS-v0:** canonical `docs/GRANULARITY_NAMING_STANDARD.md`. 6A Skill/Objective atomization, under/over-fragmentation, shared-vs-specific capability, stable logical ID ve FBB seed ratification/refactor kurallarını kilitledi.
''')
regex_once(ctx,
r'## 11\. Güncel yürütme konumu\n.*?(?=\n## 12\.)',
'''## 11. Güncel yürütme konumu

- AŞAMA 1 ✅
- AŞAMA 2 ✅
- AŞAMA 3 ✅
- AŞAMA 4 ✅
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6:
  - **6A ✅ GNS-v0 / D-054**
  - **6B 🟡 Full-route decomposition blueprint — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
  - 6C–6H ⬜
- AŞAMA 7–20 ⬜

**Sıradaki numaralı çalışma 6B'dir.** 6B başlamadan fresh PRE-STEP GitHub refresh zorunludur.
''')

# ------------------------------------------------------------------
# START_HERE
# ------------------------------------------------------------------
start='docs/START_HERE.md'
replace_once(start,
'''### D-053 — GQA-v0
5D final graph architecture QA `docs/GRAPH_ARCHITECTURE_QA.md` içinde FBB seed graph'ı cycle/dead-end/hidden prerequisite/duplicate/reuse/English-global-gate/reachability açısından doğruladı; blocking structural sorunları corrective patch ile düzeltti ve AŞAMA 5'i kapattı.
''',
'''### D-053 — GQA-v0
5D final graph architecture QA `docs/GRAPH_ARCHITECTURE_QA.md` içinde FBB seed graph'ı cycle/dead-end/hidden prerequisite/duplicate/reuse/English-global-gate/reachability açısından doğruladı; blocking structural sorunları corrective patch ile düzeltti ve AŞAMA 5'i kapattı.

### D-054 — GNS-v0
6A final `docs/GRANULARITY_NAMING_STANDARD.md` standardı Domain/Module/Topic/Skill/Objective semantic sınırlarını, Skill atomization testini, under/over-fragmentation guard'larını, shared-vs-specific capability split'ini, stable logical ID convention'ını ve FBB seed ratification/refactor lifecycle'ını kilitledi.
''')
replace_once(start,
'11b. `docs/GRAPH_ARCHITECTURE_QA.md`\n11c. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`\n',
'11b. `docs/GRAPH_ARCHITECTURE_QA.md`\n11c. `docs/GRANULARITY_NAMING_STANDARD.md`\n11d. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`\n')
replace_once(start,
'''### AŞAMA 6 ilerlemesi
- 6A 🟡 Granularity + naming standardı — aktif, henüz yürütülmedi

## 9. Güncel çalışma konumu

**Aktif:** **`6A — Granularity + naming standardı`**  
**6A henüz yürütülmedi.**

6A, KGC-v0 + GQA-v0 üzerinde Domain/Module/Topic/Skill/Objective granularity sınırlarını, canonical logical ID convention'ını, shared-vs-specific capability ayrımını ve over/under-fragmentation guard'larını kilitleyecek.

6A başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044–D-053 kararlarını oku; D-043 geri çekilmiştir. PROJECT_CONTEXT, CURRICULUM_DOMAIN_MAP, CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT, V1_FOUNDATION_BACKBONE, GRAPH_ARCHITECTURE_QA, HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 6A — Granularity + naming standardı; 6A henüz yürütülmedi.`''',
'''### AŞAMA 6 ilerlemesi
- 6A ✅ `GNS-v0 — Granularity & Naming Standard` / D-054
- 6B 🟡 Full-route decomposition blueprint — aktif, henüz yürütülmedi

## 9. Güncel çalışma konumu

**Aktif:** **`6B — Full-route decomposition blueprint`**  
**6B henüz yürütülmedi.**

6B, GNS-v0'ı 23 route family'nin tamamında kullanılabilecek ortak decomposition authoring blueprint'ine dönüştürecek.

6B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044–D-054 kararlarını oku; D-043 geri çekilmiştir. PROJECT_CONTEXT, CURRICULUM_DOMAIN_MAP, CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT, V1_FOUNDATION_BACKBONE, GRAPH_ARCHITECTURE_QA, GRANULARITY_NAMING_STANDARD, HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 6B — Full-route decomposition blueprint; 6B henüz yürütülmedi.`''')

# ------------------------------------------------------------------
# DECISIONS
# ------------------------------------------------------------------
dec='docs/DECISIONS.md'
append_once(dec, '## D-054 — Granularity & Naming Standard = GNS-v0', '''## D-054 — Granularity & Naming Standard = GNS-v0
**Durum:** Kabul edildi — 2026-08-25

- 6A final modeli `GNS-v0 — Granularity & Naming Standard` oldu.
- Canonical çıktı `docs/GRANULARITY_NAMING_STANDARD.md`.
- Domain/Module/Topic organization granularity'si ile Skill/Objective capability/evidence granularity'si ayrı semantic testlerle tanımlandı.
- Yeni Skill kararı takvim veya keyword'e değil independent evidence, independent remediation, prerequisite boundary ve cross-context reuse değerine bağlandı.
- Under-fragmentation ve over-fragmentation guard'ları zorunlu authoring QA oldu.
- Aynı semantic capability farklı Topic/Domain placements'ta clone'lanmaz; canonical Skill + TopicSkillLink reuse edilir.
- Shared mental-model capability ile language/tool-specific production capability ayrı Skill olabilmesi için explicit split kriteri tanımlandı.
- Learning Objective exactly-one-Skill altında atomic observable evidence target olarak tutulur; content-instance adı Objective identity olamaz.
- Logical ID convention lowercase ASCII dotted namespace + snake_case segment; locale/order/version bağımsızdır.
- Week/day/stage/FB band/release/version/difficulty/requirement role logical ID'ye gömülmez.
- `basic/advanced/intro` yalnız gerçek semantic scope ifade ediyorsa kullanılabilir; FBB seed'deki vague level slug'ları 6C ratification'da review edilir.
- Display/localization/alias değişimi logical identity değişimi değildir.
- Split/merge/re-home/objective-move KGC-v0 conservative migration semantics ile çözülür; bedava mastery yoktur.
- FBB authoring seed'leri 6C'de `ratify_as_is | ratify_with_display_edit | normalize_logical_id | split_required | merge_with_existing | rehome_placement_only | deprecate_seed | needs_granularity_review` status'larından biriyle değerlendirilir.
- 6B ortak decomposition template'i GNS-v0 reason-code ve authoring alanlarını tüketmek zorundadır.
- 6A external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research AI doğrulaması 6H'de zorunlu kalır.

Ayrıntı: `docs/GRANULARITY_NAMING_STANDARD.md`.''')

# ------------------------------------------------------------------
# PROGRESS_LOG
# ------------------------------------------------------------------
progress='docs/PROGRESS_LOG.md'
append_once(progress, '### 2026-08-25 — 6A Granularity + Naming Standard tamamlandı', '''### 2026-08-25 — 6A Granularity + Naming Standard tamamlandı

**PRE-STEP GitHub refresh**
- Kullanıcı onayı sonrası `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `PROJECT_CONTEXT` fresh okundu.
- 5D GQA-v0 / D-053 ile AŞAMA 5'in tamamlandığı, gerçek aktif adımın 6A olduğu ve 6A'nın henüz yürütülmediği doğrulandı.
- 6A direct inputs fresh okundu: `GRAPH_ARCHITECTURE_QA`, `V1_FOUNDATION_BACKBONE`, `CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT`, `LEARNING_ENGINE_SPEC`, `GRANULAR_CAPABILITY_MAP_PLAN`, `CURRICULUM_DOMAIN_MAP`, `PREREQUISITE_POLICY_SPEC`, `PROJECT_MEMORY_PROTOCOL`.

**Research/Coding/Test kararı**
- Ayrı external Research AI kullanılmadı: 6A current-industry coverage araştırması değil, accepted graph/learning contracts üzerinde authoring granularity + naming standard formalizasyonudur.
- Coding/Test AI kullanılmadı: physical runtime implementation yoktur.
- External independent coverage/current-industry/prerequisite Research AI zorunluluğu 6H'de korunur.

**Final model: `GNS-v0 — Granularity & Naming Standard` / D-054**
- Domain/Module/Topic/Skill/Objective semantic granularity boundaries operational hale getirildi.
- Capability Independence Test ile Skill split/keep/reuse kararı evidence/remediation/prerequisite/reuse sınırına bağlandı.
- under/over-fragmentation guards tanımlandı.
- shared vs language/tool/context-specific capability policy tanımlandı.
- Objective atomicity/observable-action standardı tanımlandı.
- logical ID formatı stable/locale-independent/version-free hale getirildi; volatile sequence/release metadata ID dışında tutuldu.
- display/localization/alias vs identity ayrımı ve KGC-compatible version/split/merge/re-home migration kuralları tanımlandı.
- FBB authoring seed ratification status contract'ı ve 6B authoring handoff'u oluşturuldu.

**POST-STEP sync**
- D-050 ALWAYS-CHECK seti 6A completed / 6B active-not-executed state'ine senkronlandı.
- `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `MASTER_PLAN`, `PROJECT_CONTEXT`, `START_HERE`, `DECISIONS`, `PROGRESS_LOG` güncellendi.
- `GRANULAR_CAPABILITY_MAP_PLAN`, `CURRICULUM`, `PROJECT_MASTER_CONTEXT` ve README GNS-v0 pointer/semantics ile hizalandı.
- MASTER_PLAN'da önceki 5D kapanışından kalmış stale “Bir sonraki yürütme: 5D” satırı düzeltildi.
- Repo-wide 6A active/not-executed, stale 5D-current ve missing D-054/GNS-v0 pointer audit'i uygulandı.

**Sonraki kesin adım:** `6B — Full-route decomposition blueprint`. 6B başlamadan fresh PRE-STEP GitHub refresh zorunlu.''')

# ------------------------------------------------------------------
# GRANULAR_CAPABILITY_MAP_PLAN
# ------------------------------------------------------------------
gmap='docs/GRANULAR_CAPABILITY_MAP_PLAN.md'
replace_once(gmap,
'**Durum:** PLANLANDI / HENÜZ YÜRÜTÜLMEDİ  \n',
'**Durum:** YÜRÜTÜLÜYOR — 6A TAMAMLANDI / 6B AKTİF  \n')
replace_once(gmap,
'**5D architecture QA input:** `docs/GRAPH_ARCHITECTURE_QA.md` / GQA-v0 / D-053\n',
'**5D architecture QA input:** `docs/GRAPH_ARCHITECTURE_QA.md` / GQA-v0 / D-053\n**6A granularity/naming standard:** `docs/GRANULARITY_NAMING_STANDARD.md` / GNS-v0 / D-054\n')
replace_once(gmap,
'''### 6A — Granularity ve naming standardı
- Domain/Module/Topic/Skill/Objective sınırları
- canonical ID convention
- atomization / over-fragmentation guard

### 6B — Full-route decomposition blueprint
''',
'''### 6A — Granularity ve naming standardı ✅
Canonical: `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054.
- Domain/Module/Topic/Skill/Objective semantic sınırları
- stable canonical logical ID convention
- under/over-fragmentation guard
- shared-vs-specific Skill split policy
- Objective atomicity + FBB seed ratification lifecycle

### 6B — Full-route decomposition blueprint 🟡 AKTİF
''')

# ------------------------------------------------------------------
# CURRICULUM summary
# ------------------------------------------------------------------
curr='docs/CURRICULUM.md'
replace_once(curr,
'**Durum:** 5A DOMAIN BACKBONE + 5B GRAPH CONTRACT + 5C V1 FOUNDATION BACKBONE TAMAMLANDI / DETAIL AŞAMA 6\'DA  \n**Canonical kararlar:** D-041, D-042, D-044, D-049, D-051, D-052  \n',
'**Durum:** AŞAMA 5 TAMAMLANDI / AŞAMA 6 YÜRÜTÜLÜYOR — 6A GNS-v0 TAMAMLANDI, 6B AKTİF  \n**Canonical kararlar:** D-041, D-042, D-044, D-049, D-051, D-052, D-053, D-054  \n')
replace_once(curr,
'**Granular decomposition charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`\n',
'**6A granularity/naming standard:** `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054  \n**Granular decomposition charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`\n')
replace_once(curr,
'''5B ✅ = KGC-v0 graph / metadata contract
5C ✅ = FBB-v0 first 8–12 week scope-equivalent V1 foundation backbone
5D ✅ = GQA-v0 graph architecture QA + corrective seed patch — active next
6A 🟡 = granularity + naming standardı
6B–6H = full granular capability map + independent coverage/prerequisite Research QA
''',
'''5B ✅ = KGC-v0 graph / metadata contract
5C ✅ = FBB-v0 first 8–12 week scope-equivalent V1 foundation backbone
5D ✅ = GQA-v0 graph architecture QA + corrective seed patch
6A ✅ = GNS-v0 granularity + naming standardı
6B 🟡 = full-route decomposition blueprint — active next
6C–6H = detailed capability maps + weakness/remediation + independent coverage/prerequisite Research QA
''')

# ------------------------------------------------------------------
# PROJECT_MASTER_CONTEXT stable addition
# ------------------------------------------------------------------
pmc='docs/PROJECT_MASTER_CONTEXT.md'
append_once(pmc, '## D-054 / 6A granularity and identity guard', '''## D-054 / 6A granularity and identity guard
`GNS-v0 — Granularity & Naming Standard` (`docs/GRANULARITY_NAMING_STANDARD.md`) AŞAMA 6 decomposition için kalıcı semantic standardıdır.

- Organization node'ları (`Domain/Module/Topic`) learner mastery atomu değildir.
- Skill ayrı learner state/remediation/prerequisite/reuse anlamı taşıyan canonical capability'dir.
- Objective exactly-one-Skill altında atomic observable evidence target'tır.
- Skill split/keep kararı independent evidence/remediation/prerequisite/reuse sınırına göre verilir; keyword veya calendar sırası yeterli değildir.
- Stable logical ID locale/order/version bağımsızdır; display/localization/alias identity değildir.
- FBB authoring seed'leri 6C'de GNS-v0 ile explicit ratify/normalize/split/merge/re-home review'undan geçer.
- 6H external Research QA zorunluluğu korunur.''')

# ------------------------------------------------------------------
# README stable navigation addition
# ------------------------------------------------------------------
readme='README.md'
replace_once(readme,
'- `docs/GRAPH_ARCHITECTURE_QA.md` — 5D foundation graph structural QA / GQA-v0 — V1 başlangıç capability seed-subgraph / FBB-v0\n- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — full route\'u ölçülebilir alt becerilere ayıracak AŞAMA 6 charter\'ı\n',
'- `docs/GRAPH_ARCHITECTURE_QA.md` — 5D foundation graph structural QA / GQA-v0\n- `docs/GRANULARITY_NAMING_STANDARD.md` — 6A semantic granularity + stable logical ID standardı / GNS-v0\n- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — full route\'u ölçülebilir alt becerilere ayıracak AŞAMA 6 charter\'ı\n')
replace_once(readme,
'''## D-052 — V1 Foundation Backbone

İlk 8–12 haftalık V1 başlangıç scope'u `docs/V1_FOUNDATION_BACKBONE.md` içinde FBB-v0 olarak tanımlıdır. Bu bir calendar unlock planı değildir; zero-entry bridge + Python/C/Linux/Git/Shell/early DS&A/parallel Technical English için KGC-v0 uyumlu authoring-seed subgraph'tır. Production lesson/task content'i AŞAMA 15'te, 6A/6C/6H ratification ve QA sonrasında üretilir.
''',
'''## D-052 — V1 Foundation Backbone

İlk 8–12 haftalık V1 başlangıç scope'u `docs/V1_FOUNDATION_BACKBONE.md` içinde FBB-v0 olarak tanımlıdır. Bu bir calendar unlock planı değildir; zero-entry bridge + Python/C/Linux/Git/Shell/early DS&A/parallel Technical English için KGC-v0 uyumlu authoring-seed subgraph'tır. Production lesson/task content'i AŞAMA 15'te, 6A/6C/6H ratification ve QA sonrasında üretilir.

## D-053 — Foundation Graph Architecture QA

5D `docs/GRAPH_ARCHITECTURE_QA.md` içinde FBB-v0 seed graph'ın TopicSkillLink, prerequisite, DAG, branch-isolation ve English global-gate invariants'ını doğruladı ve corrective seed patch'i kilitledi.

## D-054 — Granularity & Naming Standard

6A `docs/GRANULARITY_NAMING_STANDARD.md` içinde GNS-v0 olarak tamamlandı. Skill atomization artık independent evidence/remediation/prerequisite/reuse anlamına göre yapılır; stable logical ID'ler curriculum sırası, release/version veya display label'dan bağımsızdır. FBB seed'leri 6C'de bu standarda göre ratify/refactor edilir.
''')

# ------------------------------------------------------------------
# Structural / stale-reference audit
# ------------------------------------------------------------------
living = [
    'PROJECT_CONTEXT.md',
    'docs/START_HERE.md',
    'docs/HANDOFF_STATE.md',
    'docs/STEP_STATUS.md',
    'docs/EXECUTION_INDEX.md',
    'docs/MASTER_PLAN.md',
]
for p in living:
    txt = read(p)
    if '6B' not in txt:
        raise SystemExit(f'{p}: missing 6B current state after sync')
    if 'GNS-v0' not in txt and p != 'docs/MASTER_PLAN.md':
        raise SystemExit(f'{p}: missing GNS-v0 durable pointer after sync')

for p in living:
    txt = read(p)
    bad = [
        '6A henüz yürütülmedi',
        '6A 🟡 Granularity + naming standardı — aktif, henüz yürütülmedi',
        '**Aktif:** **`6A — Granularity + naming standardı`**',
    ]
    for needle in bad:
        if needle in txt:
            raise SystemExit(f'{p}: stale current 6A reference remains: {needle}')

if 'Bir sonraki yürütme: **5D' in read('docs/MASTER_PLAN.md'):
    raise SystemExit('MASTER_PLAN stale 5D next-execution reference remains')
if '5D ✅ = GQA-v0 graph architecture QA + corrective seed patch — active next' in read('docs/CURRICULUM.md'):
    raise SystemExit('CURRICULUM stale 5D active-next reference remains')

# Repo-wide informational scan. Historical logs/specs are allowed to mention old state.
needles = ['6A henüz yürütülmedi', '6A — Granularity + naming standardı', 'D-054', 'GNS-v0']
for needle in needles:
    hits=[]
    for p in sorted(Path('.').rglob('*.md')):
        try:
            lines=p.read_text(encoding='utf-8').splitlines()
        except Exception:
            continue
        for i,line in enumerate(lines,1):
            if needle in line:
                hits.append(f'{p}:{i}: {line.strip()}')
    print(f'[{needle}] hits={len(hits)}')
    for h in hits[:60]: print(h)

print('6A POST-SYNC STRUCTURAL AUDIT: PASS')
