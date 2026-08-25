# Foundation Graph Architecture QA — GQA-v0

**Adım:** 5D — Graph architecture QA  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `GQA-v0 — Foundation Graph Architecture QA`  
**Karar:** D-053

Bu belge `docs/V1_FOUNDATION_BACKBONE.md` içindeki FBB-v0 authoring-seed graph'ını KGC-v0 / PRG-v0 invariants altında statik ve deterministik olarak doğrular. 5D'nin amacı production lesson yazmak veya full 4+ yıllık curriculum coverage'ını araştırmak değildir; V1 foundation seed graph'ın yapısal olarak güvenli, branch-isolated, machine-verifiable ve AŞAMA 6'ya genişletilebilir olup olmadığını test etmektir.

Bağlayıcı kaynaklar:
- `docs/V1_FOUNDATION_BACKBONE.md` — FBB-v0 / D-052
- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051
- `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0 / D-031
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0 / D-032
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044
- `docs/V1_SCOPE.md`
- `docs/V1_SUCCESS_CRITERIA.md`
- `docs/PROJECT_MEMORY_PROTOCOL.md` — D-050

Ana sonuç:

> **İlk FBB-v0 seed'i iki structural blocker ve birkaç hidden-prerequisite riski içeriyordu. 5D bunları authoring-seed aşamasında düzeltti. Corrective patch sonrası graph 5D acceptance gate'ini PASS eder. Bu PASS full curriculum coverage doğrulaması değildir; 6H external Research QA zorunlu kalır.**

---

# 1. PRE-STEP doğrulaması

Fresh GitHub PRE-STEP refresh ile aşağıdakiler doğrulandı:
- 5A PDM-v0 tamamlandı,
- 5B KGC-v0 tamamlandı,
- 5C FBB-v0 tamamlandı,
- gerçek aktif adım 5D idi ve henüz yürütülmemişti,
- living state dosyaları aynı execution state'i gösteriyordu,
- D-050 PRE/POST + stale-reference kuralı bağlayıcıydı.

5D external job-market/current-industry research adımı değildir. Ayrı Research AI kullanılmadı; bağımsız external coverage/current-industry/prerequisite Research QA 6H'de zorunlu kalır. Physical runtime implementation olmadığı için Coding AI gerekmemiştir.

---

# 2. Audit edilen seed graph

FBB-v0 başlangıç seed envelope'ı:
- 5 Domain family placement: Technical English, Python, C, Linux/Git/Shell, DS&A,
- 10 Module,
- 26 Topic,
- 41 canonical Skill seed,
- 47 Learning Objective seed,
- initial hard/soft Skill prerequisite graph,
- technical / English / professional-workflow scope'ları.

Bu sayılar scientific optimum veya future full curriculum size değildir. Yalnız 5C authoring-seed fixture'ının büyüklüğünü tarif eder.

---

# 3. QA yöntemi

5D iki katman kullandı.

## 3.1 Structural graph checks
- referenced Skill IDs mevcut mu,
- self-edge var mı,
- aynı source-target için hard/soft conflict var mı,
- hard prerequisite graph cycle içeriyor mu,
- combined hard+soft relation graph authoring cycle oluşturuyor mu,
- required Skill'ler teaching Topic'e bağlanabilir mi,
- bütün seed Objective'lerin canonical Skill owner'ı var mı,
- unreachable required capability var mı,
- exact shared capability clone'lanmış mı,
- KGC controlled `reason_kind` vocabulary ihlali var mı.

## 3.2 Behavioral fixtures
FBB-v0 F5D-01..F5D-10 fixture'ları PRG/RVR/English invariants ile birlikte değerlendirildi:
- zero user,
- Python hızlı / Linux zayıf,
- English zayıf / technical güçlü,
- memory address gap,
- `review_due` prerequisite,
- debugging local prerequisite gap,
- duplicate Skill placement,
- no dead-end foundation,
- no cycle,
- AŞAMA 15 content handoff reachability.

---

# 4. İlk audit bulguları

## BLOCKER-01 — `TopicSkillLink` ilişkileri explicit değildi

FBB-v0 organization Topic'lerini ve Skill catalog'unu ayrı ayrı tanımlıyordu fakat KGC-v0'daki canonical `TopicSkillLink` relation'larını machine-verifiable bir matrix olarak açıkça vermiyordu.

Risk:
- Topic → Skill → Objective path'i isim benzerliğine göre varsayılabilirdi,
- cross-domain reuse yanlışlıkla Skill clone'una dönüşebilirdi,
- AŞAMA 15 resource authoring exact Topic/Skill path'ine deterministik bağlanamazdı.

**Düzeltme:** FBB-v0'a explicit primary/reuse `TopicSkillLink` seed matrix eklendi.

## BLOCKER-02 — KGC controlled vocabulary dışında `reason_kind=supporting`

Initial FBB edge notation'ında bazı soft edge'ler `soft/supporting` kullanıyordu. KGC-v0 controlled `reason_kind` listesinde `supporting` yoktur.

**Düzeltme:** Bu edge'ler anlamlarına göre `conceptual_dependency`, `tool_environment_dependency` veya `professional_workflow_dependency` gibi canonical reason kind'lara normalize edildi.

## FINDING-03 — Bazı required seed Skill'ler yanlışlıkla zero-state eligible olabiliyordu

Foundation band'ler runtime gate değildir. Bu nedenle gerçekten prerequisite gerektiren bir Skill'in incoming hard edge'i yoksa planner onu teorik olarak sıfır kullanıcıda eligible görebilir.

Blocking hidden-prerequisite riskleri bulundu:
- Python I/O / collections / mapping / files için basic Python values/state,
- Python exception/module reading için script execution context,
- execution tracing için state-assignment mental model,
- professional debug-fix explanation için gerçek debug localization,
- C storage/lifetime intuition için C function/local-scope context.

**Düzeltme:** yalnız target evidence'ın yorumlanabilirliği için gerçekten gerekli minimal hard edge'ler eklendi. Takvim/band sırası hard gate'e çevrilmedi.

---

# 5. 5D corrective prerequisite patch

Aşağıdaki hard edge'ler FBB authoring seed'ine eklendi:

```text
skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.input_output_basic

skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.sequence_collections_basic

skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.mapping_collections_basic

skill.python.run_repl_script
  --hard/evidence_interpretability-->
skill.python.exceptions_read_basic

skill.python.run_repl_script
  --hard/tool_environment_dependency-->
skill.python.modules_imports_basic

skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.files_paths_basic

skill.programming.state_assignment_model
  --hard/evidence_interpretability-->
skill.programming.trace_execution_basic

skill.programming.debug_localization_basic
  --hard/evidence_interpretability-->
skill.engineering.explain_debug_fix_basic

skill.c.functions_basic
  --hard/conceptual_dependency-->
skill.memory.storage_lifetime_intuition
```

Aynı pair'lerde daha önce bulunan soft edge varsa soft edge kaldırıldı; hard+soft duplicate bırakılmadı.

Bu patch:
- Python branch'i Linux'a global bağlamaz,
- English'i technical branch'e bağlamaz,
- C compile/run'ın mevcut Linux environment dependency'sini korur,
- band numarasını runtime prerequisite'e dönüştürmez.

---

# 6. `reason_kind` normalization

Initial `soft/supporting` edge'leri aşağıdaki canonical semantics'e normalize edildi:

```text
python.conditionals -> python.for_iteration
  soft/conceptual_dependency

python.conditionals -> python.while_termination
  soft/conceptual_dependency

linux.process_exit_stdout_stderr_basic -> c.compile_link_run_basic
  soft/tool_environment_dependency

dsa.sequence_traversal_linear_search -> dsa.complexity_growth_intuition
  soft/conceptual_dependency
```

`state_assignment_model -> trace_execution_basic` ve `debug_localization_basic -> explain_debug_fix_basic` ise 5D hidden-prerequisite audit'i nedeniyle hard edge'e yükseltildi.

---

# 7. Explicit TopicSkillLink seed matrix

Aşağıdaki matrix minimum canonical teaching/reuse placement contract'ıdır. AŞAMA 6C bunu genişletebilir fakat aynı semantic Skill'i clone'layamaz.

## Technical English

```text
topic.english.core_technical_labels
  teach/core -> skill.english.recognize_core_technical_labels

topic.english.bilingual_instruction_fragments
  teach/core -> skill.english.follow_bilingual_technical_instruction

topic.english.terminal_error_fragments
  teach/core -> skill.english.read_simple_terminal_error_fragments
```

## Python / shared programming

```text
topic.python.program_execution_bridge
  teach/core -> skill.computing.program_execution_model
  teach/core -> skill.computing.source_runtime_artifact_distinction
  teach/core -> skill.python.run_repl_script

topic.python.values_state_expressions
  teach/core -> skill.programming.state_assignment_model
  teach/core -> skill.programming.expression_boolean_reasoning
  teach/core -> skill.python.values_variables_expressions

topic.python.input_output_first_programs
  teach/core -> skill.python.input_output_basic
  integrate/supporting -> skill.engineering.reproducible_run_notes

topic.python.conditionals
  teach/core -> skill.programming.branching_reasoning
  reinforce/core -> skill.programming.expression_boolean_reasoning
  teach/core -> skill.python.conditionals

topic.python.iteration
  teach/core -> skill.programming.iteration_reasoning
  teach/core -> skill.python.for_iteration
  teach/core -> skill.python.while_termination

topic.python.execution_trace_debug
  teach/core -> skill.programming.trace_execution_basic
  teach/core -> skill.programming.debug_localization_basic
  teach/core -> skill.python.exceptions_read_basic
  integrate/supporting -> skill.engineering.explain_debug_fix_basic

topic.python.functions
  teach/core -> skill.programming.function_decomposition
  teach/core -> skill.python.functions_parameters_return
  reinforce/supporting -> skill.programming.test_case_basic

topic.python.sequence_collections
  teach/core -> skill.python.sequence_collections_basic

topic.python.mapping_collections
  teach/core -> skill.python.mapping_collections_basic

topic.python.errors_modules_files_intro
  reinforce/core -> skill.python.exceptions_read_basic
  teach/core -> skill.python.modules_imports_basic
  teach/core -> skill.python.files_paths_basic
```

## C / memory

```text
topic.c.compile_link_run
  teach/core -> skill.c.compile_link_run_basic
  reinforce/supporting -> skill.computing.source_runtime_artifact_distinction
  reinforce/supporting -> skill.linux.process_exit_stdout_stderr_basic
  integrate/supporting -> skill.engineering.reproducible_run_notes

topic.c.declarations_expressions_io
  teach/core -> skill.c.declarations_types_expressions
  reinforce/core -> skill.programming.state_assignment_model
  reinforce/supporting -> skill.programming.expression_boolean_reasoning

topic.c.control_flow_functions
  teach/core -> skill.c.conditionals_loops_basic
  teach/core -> skill.c.functions_basic
  reinforce/core -> skill.programming.branching_reasoning
  reinforce/core -> skill.programming.iteration_reasoning
  reinforce/supporting -> skill.programming.function_decomposition

topic.c.address_value_model
  teach/core -> skill.memory.address_value_distinction

topic.c.pointer_dereference_intro
  teach/core -> skill.c.pointer_declaration_dereference_basic

topic.c.storage_lifetime_intro
  teach/core -> skill.memory.storage_lifetime_intuition
```

## Linux / Shell / Git

```text
topic.linux.terminal_filesystem_navigation
  teach/core -> skill.linux.terminal_filesystem_navigation

topic.linux.process_io_exit_status
  teach/core -> skill.linux.process_exit_stdout_stderr_basic

topic.shell.command_options_redirection
  teach/core -> skill.shell.command_options_redirection_basic

topic.git.repository_status_diff
  teach/core -> skill.git.repository_status_diff

topic.git.stage_commit_history
  teach/core -> skill.git.stage_commit_history_basic
```

## DS&A

```text
topic.dsa.sequence_traversal_linear_search
  teach/core -> skill.dsa.sequence_traversal_linear_search
  reinforce/core -> skill.programming.iteration_reasoning

topic.dsa.complexity_growth_intuition
  teach/core -> skill.dsa.complexity_growth_intuition
```

Bu matrix user-specific mastery state yaratmaz; aynı canonical Skill farklı Topic'lerde yalnız yeni `TopicSkillLink` ile reuse edilir.

---

# 8. Structural audit sonucu

Corrective patch sonrası:

| Check | Sonuç |
|---|---|
| Skill ID uniqueness | PASS |
| Referenced prerequisite Skill IDs mevcut | PASS |
| Self prerequisite | PASS — yok |
| Hard/soft same-pair conflict | PASS — yok |
| Hard prerequisite cycle | PASS — DAG |
| Combined hard+soft authoring cycle | PASS — cycle yok |
| KGC `reason_kind` controlled vocabulary | PASS |
| 41 Skill'in canonical Objective owner path'i | PASS |
| 47 Objective exactly-one-Skill seed mapping | PASS |
| Required Skill için teaching/reuse Topic path | PASS |
| Shared Skill clone yerine TopicSkillLink reuse | PASS |
| English → unrelated technical global hard edge | PASS — yok |
| Scope-relative technical/English/workflow separation | PASS |
| Authoring-seed migration safety | PASS WITH GUARD |

`PASS WITH GUARD`: 5C entity'leri learner-published değildir. 6A/6C ratification sırasında semantic split/merge/refactor gerekiyorsa KGC migration manifest kullanılmalıdır; silent overwrite yasaktır.

---

# 9. F5D fixture sonuçları

## F5D-01 — Zero user — PASS
- Python run/bridge, Linux ve English giriş capability'leri erişilebilir.
- Pointer Skill, `address_value_distinction + c.declarations_types_expressions` hard prerequisites nedeniyle zero-state'te eligible değildir.

## F5D-02 — Python hızlı, Linux zayıf — PASS
- Python branch Linux'a global hard bağlı değildir.
- C compile/run Linux terminal environment hard prerequisite'i nedeniyle bekleyebilir.
- English ve diğer bağımsız branch'ler devam eder.

## F5D-03 — English zayıf, technical güçlü — PASS
- Seed graph'ta English→technical global hard edge yoktur.
- English-only exact task gerekiyorsa task/resource metadata explicit language prerequisite taşır.
- Türkçe/bilingual technical variant gerçek technical prerequisite'ler sağlandığında devam edebilir.

## F5D-04 — Memory address gap — PASS
- Pointer Skill bloke olur.
- Python/Git/English bağımsız branch'leri etkilenmez.

## F5D-05 — `review_due` prerequisite — PASS
- RVR-v0 + PRG-v0 gereği `review_due -> ready_due`; hard gate otomatik kapanmaz.

## F5D-06 — Debugging local prerequisite gap — PASS AFTER PATCH
- `state_assignment_model -> trace_execution_basic -> debug_localization_basic` hard interpretability chain'i explicit hale getirildi.
- `debug_localization_basic -> explain_debug_fix_basic` hard interpretability edge'i eklendi.
- Exact task'in ekstra prerequisite'i varsa `TaskCandidate.required_skill_ids[]` yine zorunludur.

## F5D-07 — Duplicate Skill placement — PASS
- `iteration_reasoning`, `state_assignment_model` ve diğer shared capabilities tek canonical Skill ID kullanır; Python/C/DS&A contexts TopicSkillLink ile reuse edilir.

## F5D-08 — No dead-end foundation — PASS
- Required seed capability'ler ya intentional entry capability'dir ya da valid hard prerequisite chain ile erişilebilir.
- Leaf Skill olmak dead-end sayılmaz; dead-end yalnız hiçbir valid teach/eligibility path'i olmayan required capability'dir.

## F5D-09 — No cycle — PASS
- Corrected hard graph DAG'dır.
- Combined hard+soft seed relations'ta da cycle bulunmadı.

## F5D-10 — V1 content handoff reachability — PASS AFTER PATCH
- Explicit TopicSkillLink matrix ile her seed Skill/Objective için deterministic Topic → Skill → Objective authoring path'i vardır.
- AŞAMA 15 yalnız 6A/6C ratified/published IDs üzerinde production resources bağlayacaktır.

---

# 10. Hidden prerequisite policy sonucu

5D'nin amacı bütün future prerequisite'leri tahmin etmek değildir. Seed graph için kural:

1. Target Skill'in anlamlı biçimde öğrenilmesi/evidence attribution'ı source Skill olmadan mümkün değilse graph hard edge kullanır.
2. Source yalnız akıcılık veya scaffold sağlıyorsa soft edge kullanılır.
3. Belirli resource/task generic Skill graph'tan daha fazla prerequisite gerektiriyorsa `TaskCandidate.required_skill_ids[]` / QAB metadata kullanılır.
4. Takvim veya FB band sırası kendi başına hard prerequisite'e çevrilmez.
5. 6C Foundations decomposition yeni hidden prerequisite bulursa seed authoring state explicit migration/ratification ile genişletilir.

---

# 11. Branch isolation invariants

V1 foundation graph için kalıcı architecture guard:

```text
weak English != global technical block
weak Linux != Python block
pointer gap != Python/Git/English block
review_due != not_ready
soft gap != hard block
Domain/Module/Topic ordering != runtime hard gate
```

Bir root/prerequisite problemi yalnız gerçek reverse-dependent neighborhood'u etkiler.

---

# 12. AŞAMA 6 handoff

5D PASS, AŞAMA 6'nın full granular map'ini onaylamaz; yalnız FBB seed architecture'ının genişlemeye uygun olduğunu gösterir.

6A:
- final naming/ID/granularity convention,
- authoring_seed ratification kuralları.

6B:
- 23 route family decomposition template,
- TopicSkillLink/reuse authoring standardı.

6C:
- FBB foundation seeds'i ayrıntılı Python/C/Linux/English/DS&A map içinde ratify/refine eder,
- 5D corrective prerequisite edges'i başlangıç invariant'ı olarak korur fakat daha iyi granularity bulunursa explicit migration kullanır.

6H:
- external Research AI ile missing coverage, hidden prerequisite, duplicate, current relevance ve source quality'yi bağımsız doğrular.

---

# 13. AŞAMA 15 handoff

Production content authoring başlamadan önce:

```text
FBB-v0 + 5D GQA-v0
→ 6A naming/granularity
→ 6C detailed Foundations map
→ 6H external Research QA
→ published graph version
→ AŞAMA 15 content
```

5D PASS, seed ID'lerin doğrudan learner-published yapılmasına izin vermez.

---

# 14. Acceptance gate — 5D

5D ancak aşağıdakilerin tamamı sağlandığında kapanır:

1. hard prerequisite graph cycle içermiyor,
2. self/dangling/conflicting edge yok,
3. KGC reason-kind vocabulary uyumlu,
4. required seed capability'ler reachable/teachable,
5. TopicSkillLink path'leri explicit,
6. duplicate semantic Skill clone yok,
7. hidden prerequisite blocker'lar düzeltildi veya exact task metadata'ya açıkça delege edildi,
8. English global technical gate yok,
9. branch isolation fixtures PASS,
10. `review_due` gate semantics PRG/RVR ile uyumlu,
11. authoring_seed migration guard korunuyor,
12. AŞAMA 6/15 handoff güvenli,
13. full external Research QA'nın 6H'de kalması korunuyor.

**Sonuç:** corrective patch sonrası `GQA-v0` PASS. AŞAMA 5 tamamlanabilir ve sıradaki numaralı adım `6A — Granularity + naming standardı` olabilir.
