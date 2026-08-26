# Foundations Detailed Map — FDM-v0

**Adım:** 6C — Foundations detailed map
**Durum:** TAMAMLANDI
**Tarih:** 2026-08-26
**Final model:** `FDM-v0 — Foundations Detailed Map`
**Karar:** D-057

Bu belge D01–D05 Foundations route family'lerinin FRDB-v0 uyumlu machine-readable authoring package'ını, FBB-v0 seed ratification sonucunu ve 6C internal QA kararını özetler.

Canonical dataset:

`curriculum/decomposition/6c_foundations/`

Bağlayıcı girdiler:
- `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` — FRDB-v0 / D-056,
- `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054,
- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051,
- `docs/V1_FOUNDATION_BACKBONE.md` — FBB-v0 / D-052,
- `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053,
- `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049,
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036,
- `docs/ENGLISH_FOUNDATION_RULES.md`.

Ana sonuç:

> **D01–D05 artık broad başlık listesi değildir. 132 canonical Skill candidate ve 137 atomic Objective ile weakness, prerequisite, evidence ve remediation'ın alt capability seviyesinde çalışabileceği internally-QA-passed authoring map'tir. External coverage/current-industry validation 6H'ye kadar pending kalır.**

---

# 1. Package özeti

| Koleksiyon | Sonuç |
|---|---:|
| Domain | 5 |
| Module | 14 |
| Topic | 46 |
| Skill | 132 |
| Learning Objective | 137 |
| TopicSkillLink | 145 |
| Skill prerequisite edge | 200 |
| FBB Skill seed mapping | 41/41 |
| FBB Objective seed mapping | 47/47 |
| Açık blocking review | 0 |
| Açık non-blocking review | 2 |

Internal package sonucu:

`PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`

Package learner-published değildir. `status=authoring_complete_internal_qa`; external coverage gate 6H'de, production resource authoring AŞAMA 15'tedir.

---

# 2. D01 — Technical English

Technical English 2 Module ve 6 Topic altında granular capability graph'ına ayrıldı:

- core technical vocabulary,
- noun phrase / article / plural recognition,
- `be` ve simple-present comprehension,
- imperative instruction comprehension,
- preposition/function-word comprehension,
- negation/question comprehension,
- bilingual instruction following,
- terminal/compiler error-fragment reading,
- documentation navigation,
- definition/constraint ve procedure reading,
- command/result note ve basic bug-description writing,
- simple technical process explanation,
- clarifying technical question production.

English Skill'lerinden unrelated technical Skill'lere hard edge yoktur. Technical target English değilse bilingual/Turkish scaffold kullanılabilir. CEFR alignment, cadence ve scaffold-reduction policy AŞAMA 7'ye bırakılmıştır.

---

# 3. D02 — Python

Python 4 Module ve 21 Topic altında aşağıdaki independent capability family'lerine ayrıldı:

## 3.1 Core execution ve language model

- interpreter/REPL/script execution,
- value/type behavior,
- assignment/name binding,
- expression evaluation,
- input/output,
- conditionals,
- `for` iteration,
- `while` termination,
- break/continue/loop-else,
- comprehension transformation.

## 3.2 Data ve function model

- string/text operations,
- bytes↔text boundary,
- list, tuple, set ve mapping davranışları,
- functions/parameters/return,
- scope/name resolution,
- argument binding,
- first-class callables,
- iterator protocol,
- generator functions.

## 3.3 Software engineering

- traceback/error reading,
- exception handling,
- debugging tools,
- modules/imports,
- path handling,
- text-file I/O,
- class/object model,
- data-model protocols,
- type hints/static checking,
- unit testing ve test doubles,
- virtual environments/dependencies,
- package layout/metadata,
- CLI argument parsing.

## 3.4 Systems, data ve performance reuse

- OS/subprocess interaction,
- basic network-client behavior,
- async task/cancellation/timeout coordination,
- thread/process/async choice,
- tabular transformation,
- NumPy shape/dtype/broadcasting,
- tensor device/dtype behavior,
- profiler-backed measurement,
- benchmark automation.

Tool/library-specific Skills version-sensitive olarak işaretlidir; stable programming reasoning shared canonical Skills olarak C/DS&A bağlamlarında reuse edilir.

---

# 4. D03 — C

C 3 Module ve 10 Topic altında şu capability sınırlarına ayrıldı:

- translation/compile/link/run model,
- declarations/types/conversions,
- expression evaluation ve side-effect safety,
- standard I/O,
- conditionals, `for`, `while`,
- functions/prototypes,
- scope/linkage/storage duration,
- address/value reasoning,
- pointer formation ve dereference,
- array bounds/indexing ve array/pointer distinction,
- C string null termination,
- struct/enum data model,
- dynamic allocation lifecycle ve allocation-size safety,
- header/translation-unit interface,
- multi-file build,
- compiler diagnostic reading,
- debugger trace,
- sanitizer/undefined-behavior localization,
- file I/O error handling,
- bit-mask reasoning.

Pointer seed'i tek broad state olarak tutulmadı: pointer formation ve dereference ayrı evidence/remediation sınırlarıdır. Memory-address ve lifetime reasoning shared Skills olarak korunur.

---

# 5. D04 — Linux + Git + Shell

D04 3 Module ve 8 Topic altında ayrıldı.

Linux:
- filesystem navigation,
- permissions/ownership,
- link/file identity,
- process/exit/stdout/stderr model,
- signals/job control,
- process inspection,
- environment/PATH resolution,
- tool help/discovery.

Shell:
- command/option/argument/quoting,
- expansion safety,
- pipeline/redirection,
- script control flow ve fail-fast behavior.

Git:
- working-tree/index inspection,
- stage/commit/history verification,
- branch/merge model,
- history inspection,
- remote synchronization,
- conflict resolution,
- safe recovery/undo.

Komut adları ayrı mastery atomları değildir; repository state transition ve shell data-flow capability'leri evidence/remediation değerine göre ayrılmıştır.

---

# 6. D05 — DS&A Foundations

DS&A 2 Module ve 5 Topic altında aşağıdaki engineering-focused Skills'e ayrıldı:

- complexity growth intuition,
- asymptotic-bound reasoning,
- invariant tabanlı correctness,
- sequence traversal,
- linear search,
- binary search,
- stack/queue behavior,
- linked-structure reasoning,
- hash-table trade-offs,
- recursive problem decomposition,
- sorting comparison reasoning,
- tree traversal,
- graph traversal.

Competitive-programming coverage hedeflenmez. DSA capability'leri systems/storage/performance için gerekli correctness ve complexity transfer sınırında tutulur.

---

# 7. FBB-v0 ratification sonucu

FBB-v0 learner-published olmadığı için evidence migration yapılmadı; bütün mapping'ler `not_applicable_not_published` taşır.

Skill dispositions:
- 33 seed `ratify_as_is`,
- 8 seed `split_required`.

Split edilen broad seed family'leri:
- Python values/variables/expressions,
- Python sequence collections,
- Python files/paths,
- C declarations/types/expressions,
- C conditionals/loops,
- C pointer declaration/dereference,
- shell command/options/redirection,
- DS&A traversal/linear search.

Objective dispositions:
- 38 seed `ratify_as_is`,
- 9 seed owner/ID normalization ile ratify edildi.

Exact old→new kayıtları `seed_mappings.yaml` içindedir. Sessiz overwrite veya bedava mastery yoktur.

---

# 8. Prerequisite ve branch-isolation sonucu

200 hard/soft edge için:
- bütün source/target refs mevcut,
- self edge yok,
- aynı pair'de hard/soft conflict yok,
- hard graph DAG,
- edge direction source→target,
- Domain/Topic completion gate yok,
- English→unrelated technical hard gate yok,
- soft gap hard block üretmiyor,
- GQA-v0 corrective prerequisites daraltılmış target Skills'e taşındı.

Exact task/resource'a özgü prerequisites graph'a şişirilmez; QAB/Task `required_skill_ids[]` metadata'sına bırakılır.

---

# 9. Evidence, remediation ve professional attribution

Her Skill:
- observable direct evidence path,
- evidence depth,
- retention profile,
- diagnostic eligibility,
- targeted remediation tags,
- shared-vs-specific rationale,
- duplicate resolver sonucu,
- provenance/freshness,
- scope-relative requirement

taşır.

Her Objective exactly one Skill'e bağlıdır; H0 independent evidence ve verified evaluator beklentisini korur. Integrated project PASS toplu evidence değildir. Foundation CLI-tool candidate attribution'ları yalnız structurally essential ve separately observable component'ler için yazılmıştır.

---

# 10. Açık review'lar

Blocking review yoktur.

Non-blocking:
1. English capability'lerinin CEFR/technical progression metadata'sına bağlanması → AŞAMA 7B.
2. D01–D05 external coverage/current relevance/hidden-prerequisite doğrulaması → 6H independent Research AI.

Bu review'lar 6C internal authoring completion'ını engellemez; package'ı externally validated veya learner-published yapmaz.

---

# 11. Deterministik authoring/QA

`tools/generate_foundations_package.py` canonical contract girdilerinden package'ı deterministik üretir ve şu kontrolleri fail-fast uygular:

- route partition,
- unique Skill/Objective IDs,
- Objective owner ve Skill→Objective reachability,
- Topic refs,
- prerequisite refs/self/conflict/DAG,
- 41/47 FBB mapping completeness,
- seed result refs,
- English global-gate guard,
- blocking review count.

Generator production runtime kodu veya physical DB schema değildir; authoring package bakım/validation aracıdır.

---

# 12. 6D handoff

6D Systems detailed map:
- 6C `skills.yaml` registry'sini duplicate resolver input'u yapar,
- C/Linux/DS&A/shared programming Skills'i clone'lamaz,
- declared prerequisites için 6C IDs'lerini kullanır,
- aynı FRDB-v0 collection ve QA contract'ını uygular,
- 6D başlamadan fresh PRE-STEP GitHub refresh yapar.

**6C sonrası numaralı adım:** `6D — Systems detailed map`.
