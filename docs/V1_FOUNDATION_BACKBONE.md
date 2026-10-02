# V1 Foundation Backbone — FBB-v0

**Adım:** 5C — İlk 8–12 haftalık curriculum backbone  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `FBB-v0 — V1 Foundation Backbone`  
**Karar:** D-052

Bu belge V1'in ilk production curriculum paketini besleyecek **başlangıç executable curriculum subgraph iskeletini** tanımlar. Amaç gerçek lesson metinlerini, quiz sorularını veya günlük takvimi yazmak değil; AŞAMA 15'in içerik üreteceği ve 5D'nin graph QA yapacağı KGC-v0 uyumlu başlangıç capability omurgasını kilitlemektir.

Bağlayıcı kaynaklar:
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

Ana ilke:

> **“İlk 8–12 hafta” bir takvim veya unlock sayacı değildir. Bu ifade V1'in başlangıç content hacmini/scope'unu tarif eder; gerçek progression mastery + prerequisite + retention + günlük capacity ile belirlenir.**

İkinci ilke:

> **5C production lesson body yazmaz. 5C, AŞAMA 15'in lesson/practice/assessment üretmesi için gerekli Domain/Module/Topic placement, Skill/Objective skeleton, prerequisite ve evidence metadata anchor'larını verir.**

Üçüncü ilke:

> **Technical English ilk günden paraleldir fakat global technical hard gate değildir. English gap yalnız gerçekten English gerektiren task/Objective'i sınırlar.**

---

# 1. 5C'nin sınırı

5C kesinleştirir:
- V1 başlangıç subgraph scope'unu,
- sıfırdan kullanıcı için Computer / Programming Fundamentals giriş köprüsünü,
- Python + C + Linux/Git/Shell + başlangıç DS&A + parallel Technical English yerleşimini,
- KGC-v0 uyumlu Skill/Objective seed skeleton'ını,
- branch-isolated hard/soft Skill prerequisite edge'lerini,
- V1 scope-relative required/optional/critical metadata yaklaşımını,
- evidence / assessment / retention / diagnostic / remediation anchor'larını,
- AŞAMA 15 production-content handoff'unu,
- AŞAMA 5D graph architecture QA fixture'larını.

5C kesinleştirmez:
- bütün 4+ yıllık granular capability map'i → AŞAMA 6,
- global ID/naming/granularity standardının son halini → 6A,
- detailed Foundations capability map'ini → 6C,
- exact English grammar/CEFR progression'ını → AŞAMA 7,
- lesson metinlerini, video/okuma materyalini, gerçek soru bankasını → AŞAMA 15,
- fiziksel DB schema'sını → 9C,
- mastery/prerequisite/retention algoritmasını → GRE/PRG/RVR canonical kalır.

---

# 2. Lifecycle: 5C seed graph production-published değildir

5C'deki logical ID'ler KGC-v0 stilinde **stable authoring seed** olarak kullanılır.

```text
scope_id = scope.v1.foundation_backbone
lifecycle_status = authoring_seed
publication_state = not_learner_published
contract = KGC-v0
```

Sebep:
- 5C'nin görevi V1 subgraph'ı somutlaştırmaktır,
- 6A global granularity/naming standardını,
- 6C Foundations full detailed map'ini
henüz tamamlamamıştır.

Bu nedenle 6A/6C:
- aynı semantic capability ise ID'yi ratify edebilir,
- granularity yanlışsa explicit split/merge/refactor önerebilir,
- fakat bunu sessiz overwrite ile yapamaz; KGC-v0 migration semantics uygulanır.

AŞAMA 15 yalnız 6A/6C tarafından ratify edilmiş/published ID'ler üzerinde production content bağlamalıdır.

Kritik invariant:

```text
5C authoring seed != learner mastery data contract frozen forever
```

---

# 2.1 Authoring-seed metadata defaults — 5D clarification

FBB-v0 learner-published graph olmadığı için 5D corrective patch'i aşağıdaki inherited authoring defaults ile izlenir:

```text
seed_schema_version = FBB-v0+GQA-v0
entity_version = authoring-seed-1 unless explicitly overridden
relation_version = authoring-seed-1 unless explicitly overridden
lifecycle_status = authoring_seed
publication_state = not_learner_published
provenance = internal_design_from_canonical_specs
source_refs = FBB/KGC/PDM/PRG/GRE/RVR/English/V1 contracts
freshness_policy = foundation_stable_pending_6H_external_QA
```

Bunlar published entity metadata'sının yerine geçmez. 6A/6C ratification ve 6H QA sonrasında learner-facing published graph, effective per-entity/per-relation version + provenance/freshness metadata'sını explicit taşır.

---

# 3. V1 backbone scope yapısı

Tek global “foundation passed” boolean kullanılmaz.

Scope'lar:

```text
scope.v1.foundation_backbone.technical
scope.v1.foundation_backbone.english
scope.v1.foundation_backbone.professional_workflow
```

Böylece:
- English progression teknik branch'i global olarak bloke etmez,
- Git/professional workflow eksikliği Python syntax öğrenimini gereksiz kilitlemez,
- fakat belirli task gerçekten Git, terminal veya English prerequisite gerektiriyorsa exact Skill edge/task requirement kullanılabilir.

---

# 4. Progression bands — hafta değildir

5C, calendar week yerine **foundation band** kullanır. Band yalnız authoring/navigation grouping'dir; runtime lock değildir.

## FB0 — Zero-entry bridge
Amaç:
- program/source/runtime mental modeli,
- dosya/path/terminal ile ilk temas,
- Python çalıştırma,
- basic technical-English recognition.

## FB1 — Programming state + Python launch
Amaç:
- value/state/assignment/expression,
- Python variable/expression/I-O,
- Git repository gözlemleme,
- bilingual technical instruction.

## FB2 — Control flow + trace/debug
Amaç:
- boolean/branching/iteration mental model,
- Python conditionals/loops,
- execution trace,
- basic debugging,
- stdout/stderr/exit-code farkı.

## FB3 — Functions + data + workflow
Amaç:
- function decomposition,
- Python function/collection basics,
- test-case düşüncesi,
- Git stage/commit/history,
- shell command composition basics.

## FB4 — C + memory bridge
Amaç:
- compile/link/run,
- C declarations/types/control/functions,
- value vs address,
- pointer declaration/dereference,
- storage/lifetime intuition.

## FB5 — Early DS&A + integration
Amaç:
- sequence traversal / linear search,
- growth/complexity intuition,
- küçük cross-skill debugging/integration artifacts,
- reproducible run notes ve basic technical explanation.

Band'ler katı seri değildir. Örneğin Linux/Git/English FB0–FB3 boyunca paralel olabilir; C branch'i gerekli hard prerequisites hazır olduğunda açılır. Planner yalnız Skill readiness'e göre gate uygular.

---

# 5. Organization layer — Domain / Module / Topic seed placements

5C yeni bir “Computer Fundamentals” career Domain'i yaratmaz. Zero-entry köprüsü organization layer içinde foundation modules/topics olarak yerleştirilir; shared capability identity Skill layer'da tutulur.

## D01 — Technical English

### module.english.zero_entry_technical
- `topic.english.core_technical_labels`
- `topic.english.bilingual_instruction_fragments`
- `topic.english.terminal_error_fragments`

Bu module exact CEFR/grammar sequence değildir. 6C/7A–7E ayrıntıyı belirler.

## D02 — Python

### module.python.computing_programming_bridge
- `topic.python.program_execution_bridge`
- `topic.python.values_state_expressions`
- `topic.python.input_output_first_programs`

### module.python.control_flow_foundations
- `topic.python.conditionals`
- `topic.python.iteration`
- `topic.python.execution_trace_debug`

### module.python.functions_data_foundations
- `topic.python.functions`
- `topic.python.sequence_collections`
- `topic.python.mapping_collections`
- `topic.python.errors_modules_files_intro`

## D03 — C

### module.c.native_programming_bridge
- `topic.c.compile_link_run`
- `topic.c.declarations_expressions_io`
- `topic.c.control_flow_functions`

### module.c.memory_pointer_foundations
- `topic.c.address_value_model`
- `topic.c.pointer_dereference_intro`
- `topic.c.storage_lifetime_intro`

## D04 — Linux + Git + Shell

### module.linux.working_environment_foundations
- `topic.linux.terminal_filesystem_navigation`
- `topic.linux.process_io_exit_status`

### module.shell.command_foundations
- `topic.shell.command_options_redirection`

### module.git.version_control_foundations
- `topic.git.repository_status_diff`
- `topic.git.stage_commit_history`

## D05 — DS&A Foundations

### module.dsa.problem_solving_foundations
- `topic.dsa.sequence_traversal_linear_search`
- `topic.dsa.complexity_growth_intuition`

Professional engineering behaviors separate broad Domain clone'u yaratmadan Skill/project tags ile bu topics içine entegre edilir.

---

# 6. Canonical Skill seed catalog

Aşağıdaki Skill'ler 5C authoring seed subgraph'ının capability atomlarıdır. Display placement değişse bile semantic identity korunur.

## 6.1 Shared computing / programming skills

| Skill ID | Capability statement | Retention | Critical prerequisite? |
|---|---|---|---|
| `skill.computing.program_execution_model` | Source code, interpreter/compiler ve running program arasındaki temel ilişkiyi açıklayabilmek. | standard | false |
| `skill.computing.source_runtime_artifact_distinction` | Source file, executable/script ve runtime output'u ayırt edebilmek. | standard | false |
| `skill.programming.state_assignment_model` | Değişken/state değişimini adım adım izleyebilmek. | standard | true |
| `skill.programming.expression_boolean_reasoning` | Basit expression ve boolean condition sonucunu gerekçeli takip edebilmek. | standard | true |
| `skill.programming.branching_reasoning` | Koşula göre hangi branch'in çalışacağını belirleyebilmek. | standard | false |
| `skill.programming.iteration_reasoning` | Iteration state değişimini ve termination mantığını takip edebilmek. | complex | true |
| `skill.programming.function_decomposition` | Bir problemi küçük input/output sorumluluklarına bölebilmek. | standard | false |
| `skill.programming.trace_execution_basic` | Kısa programın execution trace'ini elle/araçla takip edebilmek. | complex | false |
| `skill.programming.debug_localization_basic` | Basit bir hatanın symptom, location ve likely cause'unu ayırabilmek. | complex | false |
| `skill.programming.test_case_basic` | Basit expected-input/output test case'i tasarlayabilmek. | standard | false |

## 6.2 Python skills

| Skill ID | Capability statement | Retention |
|---|---|---|
| `skill.python.run_repl_script` | REPL/script çalıştırıp output/error'u ayırt edebilmek. | standard |
| `skill.python.values_variables_expressions` | Python'da value, variable, assignment ve basit expression kullanabilmek. | standard |
| `skill.python.input_output_basic` | Basit input/output akışı kurabilmek. | standard |
| `skill.python.conditionals` | `if/elif/else` ile doğru branch davranışı yazabilmek. | standard |
| `skill.python.for_iteration` | Iterable üzerinde `for` iteration kurabilmek. | standard |
| `skill.python.while_termination` | `while` loop için doğru state update ve termination condition kurabilmek. | complex |
| `skill.python.functions_parameters_return` | Function, parameter ve return ile küçük davranışları ayırabilmek. | standard |
| `skill.python.sequence_collections_basic` | List/tuple benzeri sequence verisini temel işlemlerle kullanabilmek. | standard |
| `skill.python.mapping_collections_basic` | Key/value mapping'i temel kullanım için seçip kullanabilmek. | standard |
| `skill.python.exceptions_read_basic` | Basit exception türü + traceback location bilgisini okuyabilmek. | standard |
| `skill.python.modules_imports_basic` | Basit module/import kullanımını anlayıp uygulayabilmek. | standard |
| `skill.python.files_paths_basic` | Basit text-file/path I/O akışını güvenli biçimde kurabilmek. | standard |

## 6.3 C / memory skills

| Skill ID | Capability statement | Retention | Critical prerequisite? |
|---|---|---|---|
| `skill.c.compile_link_run_basic` | Basit C source'u compile edip hata/run sonucunu ayırt edebilmek. | standard | false |
| `skill.c.declarations_types_expressions` | C declaration/type/expression temelini doğru kullanabilmek. | standard | true |
| `skill.c.conditionals_loops_basic` | C syntax'ı ile temel branch/loop davranışı yazabilmek. | standard | false |
| `skill.c.functions_basic` | C function declaration/call/return temelini kullanabilmek. | standard | false |
| `skill.memory.address_value_distinction` | Value ile memory address kavramını ayırt edip basit örnekte açıklayabilmek. | complex | true |
| `skill.c.pointer_declaration_dereference_basic` | Pointer declaration, address-of ve dereference işlemlerini basit güvenli örnekte uygulayabilmek. | complex | true |
| `skill.memory.storage_lifetime_intuition` | Basit local storage/lifetime sınırlarını başlangıç seviyesinde ayırt edebilmek. | complex | false |

## 6.4 Linux / shell / Git skills

| Skill ID | Capability statement | Retention |
|---|---|---|
| `skill.linux.terminal_filesystem_navigation` | Relative/absolute path ile terminalde güvenli navigation yapabilmek. | standard |
| `skill.linux.process_exit_stdout_stderr_basic` | Process, exit status, stdout ve stderr kavramlarını ayırt edebilmek. | standard |
| `skill.shell.command_options_redirection_basic` | Basit command + option/argument ve temel redirection kullanımını okuyup kurabilmek. | standard |
| `skill.git.repository_status_diff` | Repository state'i `status/diff` benzeri araçlarla okuyabilmek. | standard |
| `skill.git.stage_commit_history_basic` | Küçük değişikliği stage/commit edip history'de doğrulayabilmek. | standard |

## 6.5 DS&A skills

| Skill ID | Capability statement | Retention |
|---|---|---|
| `skill.dsa.sequence_traversal_linear_search` | Sequence traversal ile basit linear search tasarlayıp açıklayabilmek. | complex |
| `skill.dsa.complexity_growth_intuition` | Sabit/lineer gibi temel growth farklarını input büyüdükçe davranış üzerinden ayırt edebilmek. | standard |

## 6.6 Technical English seed skills

Bu Skill'ler global technical hard prerequisite değildir.

| Skill ID | Capability statement | Retention |
|---|---|---|
| `skill.english.recognize_core_technical_labels` | Öğretilmiş temel file/run/error/value/input/output gibi teknik etiketleri tanıyabilmek. | factual |
| `skill.english.follow_bilingual_technical_instruction` | Öğretilmiş kelime/grammar ile kısa bilingual technical instruction'ı takip edebilmek. | standard |
| `skill.english.read_simple_terminal_error_fragments` | Öğretilmiş vocabulary ile kısa terminal/compiler/error fragment'larında ana bilgiyi bulabilmek. | standard |

## 6.7 Early professional workflow skills

| Skill ID | Capability statement | Retention |
|---|---|---|
| `skill.engineering.reproducible_run_notes` | Küçük bir programı yeniden çalıştırmak için gerekli environment/command/input/result notunu kaydedebilmek. | standard |
| `skill.engineering.explain_debug_fix_basic` | Basit bir bug için symptom → cause → fix → verification zincirini kısa biçimde açıklayabilmek. | complex |

---

# 7. Learning Objective seed skeleton

5C her Skill için production-final rubric yazmaz; fakat AŞAMA 15'in resources'ı exact Objective'e bağlayabilmesi için observable Objective skeleton'ı verir.

## 7.1 Shared / programming objectives

```text
objective.computing.program_execution_model.explain_pipeline
  -> skill.computing.program_execution_model
  action: verilen kısa örnekte source → translator/runtime → output zincirini açıklama

objective.computing.source_runtime_artifact_distinction.classify_artifacts
  -> skill.computing.source_runtime_artifact_distinction
  action: source/script/executable/output örneklerini doğru sınıflandırma

objective.programming.state_assignment_model.trace_state
  -> skill.programming.state_assignment_model
  action: kısa statement dizisinde state değişimini adım adım izleme

objective.programming.expression_boolean_reasoning.evaluate_condition
  -> skill.programming.expression_boolean_reasoning
  action: basit expression/condition sonucunu gerekçelendirme

objective.programming.branching_reasoning.predict_branch
  -> skill.programming.branching_reasoning
  action: input/state verildiğinde çalışacak branch'i belirleme

objective.programming.iteration_reasoning.trace_iteration
  -> skill.programming.iteration_reasoning
  action: iteration state'ini ve stop condition'ı izleme

objective.programming.iteration_reasoning.detect_nontermination
  -> skill.programming.iteration_reasoning
  action: simple infinite-loop nedenini belirleme

objective.programming.function_decomposition.define_io_responsibility
  -> skill.programming.function_decomposition
  action: küçük problemi function input/output sorumluluklarına ayırma

objective.programming.trace_execution_basic.produce_trace
  -> skill.programming.trace_execution_basic
  action: kısa program için observable execution trace üretme

objective.programming.debug_localization_basic.locate_cause
  -> skill.programming.debug_localization_basic
  action: hata mesajı + küçük programdan likely fault location/cause belirleme

objective.programming.test_case_basic.design_expected_case
  -> skill.programming.test_case_basic
  action: function davranışı için input + expected output test case yazma
```

## 7.2 Python objectives

```text
objective.python.run_repl_script.execute_and_classify_result
objective.python.values_variables_expressions.write_state_change
objective.python.input_output_basic.build_small_io_flow
objective.python.conditionals.write_branch_logic
objective.python.for_iteration.iterate_sequence
objective.python.while_termination.write_terminating_loop
objective.python.while_termination.debug_infinite_loop
objective.python.functions_parameters_return.write_small_function
objective.python.sequence_collections_basic.select_and_use_sequence
objective.python.mapping_collections_basic.select_and_use_mapping
objective.python.exceptions_read_basic.extract_error_type_location
objective.python.modules_imports_basic.import_and_use_module_member
objective.python.files_paths_basic.read_write_small_text_file
```

Her Objective ilgili aynı-named Skill'e bağlanır; örneğin `objective.python.conditionals.write_branch_logic -> skill.python.conditionals`.

## 7.3 C / memory objectives

```text
objective.c.compile_link_run_basic.compile_and_run_small_program
objective.c.compile_link_run_basic.classify_compile_vs_runtime_failure
objective.c.declarations_types_expressions.write_basic_declarations
objective.c.conditionals_loops_basic.write_control_flow
objective.c.functions_basic.write_and_call_function
objective.memory.address_value_distinction.explain_address_vs_value
objective.memory.address_value_distinction.trace_simple_memory_example
objective.c.pointer_declaration_dereference_basic.read_pointed_value
objective.c.pointer_declaration_dereference_basic.modify_value_via_pointer
objective.memory.storage_lifetime_intuition.identify_simple_lifetime_boundary
```

## 7.4 Linux / shell / Git objectives

```text
objective.linux.terminal_filesystem_navigation.navigate_relative_absolute
objective.linux.process_exit_stdout_stderr_basic.classify_process_outputs
objective.shell.command_options_redirection_basic.construct_simple_command
objective.git.repository_status_diff.inspect_change_state
objective.git.stage_commit_history_basic.commit_and_verify_history
```

## 7.5 DS&A objectives

```text
objective.dsa.sequence_traversal_linear_search.implement_search
objective.dsa.sequence_traversal_linear_search.explain_termination_and_result
objective.dsa.complexity_growth_intuition.compare_growth_examples
```

## 7.6 English objectives

```text
objective.english.recognize_core_technical_labels.match_term_meaning
objective.english.follow_bilingual_technical_instruction.execute_known_instruction
objective.english.read_simple_terminal_error_fragments.extract_known_signal
```

English Objective yalnız öğretilmiş grammar/vocabulary prerequisite'leriyle author edilebilir.

## 7.7 Professional workflow objectives

```text
objective.engineering.reproducible_run_notes.record_reproduction_steps
objective.engineering.explain_debug_fix_basic.explain_symptom_cause_fix_verify
```

---

# 8. Evidence profile templates

5C yeni mastery formülü üretmez. Aşağıdaki template'ler GRE-v0/QAB-v0 alanlarını tekrar kullanılabilir authoring shorthand olarak ifade eder.

## EP-CONCEPT-H0
Uygun:
- mental model,
- classification,
- explanation.

```text
acceptable_evidence_types = free_response | explanation | structured_response
requires_user_authored_artifact = false
allowed_tools_policy = no_solution_generation
GRE gates = inherited
```

## EP-CODE-H0
Uygun:
- Python/C production.

```text
acceptable_evidence_types = user_authored_code + deterministic_test/compiler evidence
required_direct_type = authored_code
requires_user_authored_artifact = true
allowed_tools_policy = H0 for independent mastery evidence
GRE gates = inherited
```

## EP-DEBUG-H0
Uygun:
- debug/localization/fix Objectives.

```text
acceptable_evidence_types = diagnosis + fix/artifact + verification
requires_non_basic_evidence = true when Objective profile requires it
requires_user_authored_artifact = task-dependent
GRE gates = inherited
```

## EP-TERMINAL-H0
Uygun:
- Linux/shell/toolchain.

```text
acceptable_evidence_types = command/action artifact + observed result
required_direct_type = hands_on_system_task
GRE gates = inherited
```

## EP-GIT-H0
Uygun:
- Git state/workflow.

```text
acceptable_evidence_types = repository state transition + command/output verification
required_direct_type = hands_on_workflow
GRE gates = inherited
```

## EP-ENGLISH-FOUNDATION
Uygun:
- recognition / controlled production / short known-instruction comprehension.

```text
language_prerequisites = explicit
unknown_grammar_hidden_prerequisite = forbidden
bilingual_scaffold = allowed when target English evidence remains separately observable
GRE gates = inherited
```

No template numeric mastery threshold/count invent etmez; GRE-v0 published defaults/Objective-specific effective profile canonicaldır.

---

# 9. Requirement / criticality policy for V1 seed

## 9.1 Technical scope

`scope.v1.foundation_backbone.technical` içinde listedeki technical Skills varsayılan olarak `required/standard` seed requirement alır.

Aşağıdaki capability'ler daha sonraki evidence yorumlanabilirliğini ciddi etkilediği için `critical_prerequisite=true` overlay taşır:
- `skill.programming.state_assignment_model`
- `skill.programming.expression_boolean_reasoning`
- `skill.programming.iteration_reasoning`
- `skill.c.declarations_types_expressions`
- `skill.memory.address_value_distinction`
- `skill.c.pointer_declaration_dereference_basic`

Bu overlay GRE Objective `criticality=critical` ile otomatik eşit değildir. Objective-level criticality 6C/AŞAMA 15 authoring QA'da gerekçeli olarak çözülür.

## 9.2 English scope

`scope.v1.foundation_backbone.english` içindeki üç seed English Skill `required/standard` olabilir; fakat bu scope'un eksikliği `scope.v1.foundation_backbone.technical` için global blocker değildir.

## 9.3 Professional workflow scope

`skill.engineering.reproducible_run_notes` ve `skill.engineering.explain_debug_fix_basic`, `scope.v1.foundation_backbone.professional_workflow` içinde required seed capability'dir. Ancak syntax öğrenme task'larını global olarak kilitlemez; yalnız professional artifact/evidence gerektiren task'larda exact prerequisite olabilir.

---

# 10. Initial Skill prerequisite edges

Aşağıdaki edge'ler 5C seed graph'ın minimum dependency contract'ıdır. 5D cycle/hidden-prerequisite QA yapacak; 6C full Foundations map'i genişletecektir.

## 10.1 Shared/Python branch

```text
skill.computing.program_execution_model
  --soft/conceptual_dependency-->
skill.python.run_repl_script

skill.python.run_repl_script
  --hard/procedural_dependency-->
skill.python.values_variables_expressions

skill.programming.state_assignment_model
  --hard/evidence_interpretability-->
skill.python.values_variables_expressions

skill.programming.expression_boolean_reasoning
  --hard/conceptual_dependency-->
skill.python.conditionals

skill.programming.branching_reasoning
  --soft/conceptual_dependency-->
skill.python.conditionals

skill.programming.iteration_reasoning
  --hard/conceptual_dependency-->
skill.python.while_termination

skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.conditionals

skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.for_iteration

skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.while_termination

skill.python.conditionals
  --soft/conceptual_dependency-->
skill.python.for_iteration

skill.python.conditionals
  --soft/conceptual_dependency-->
skill.python.while_termination

skill.programming.function_decomposition
  --soft/conceptual_dependency-->
skill.python.functions_parameters_return

skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.functions_parameters_return

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

skill.python.for_iteration
  --soft/conceptual_dependency-->
skill.dsa.sequence_traversal_linear_search

skill.python.sequence_collections_basic
  --hard/procedural_dependency-->
skill.dsa.sequence_traversal_linear_search
```

## 10.2 Debug/testing branch

```text
skill.programming.trace_execution_basic
  --hard/evidence_interpretability-->
skill.programming.debug_localization_basic

skill.programming.state_assignment_model
  --hard/evidence_interpretability-->
skill.programming.trace_execution_basic

skill.programming.debug_localization_basic
  --hard/evidence_interpretability-->
skill.engineering.explain_debug_fix_basic

skill.programming.test_case_basic
  --soft/professional_workflow_dependency-->
skill.engineering.explain_debug_fix_basic
```

## 10.3 Linux / Git branch

```text
skill.linux.terminal_filesystem_navigation
  --hard/tool_environment_dependency-->
skill.git.repository_status_diff

skill.git.repository_status_diff
  --hard/procedural_dependency-->
skill.git.stage_commit_history_basic

skill.linux.terminal_filesystem_navigation
  --soft/tool_environment_dependency-->
skill.shell.command_options_redirection_basic

skill.linux.process_exit_stdout_stderr_basic
  --soft/tool_environment_dependency-->
skill.c.compile_link_run_basic
```

## 10.4 C / memory branch

```text
skill.computing.program_execution_model
  --soft/conceptual_dependency-->
skill.c.compile_link_run_basic

skill.linux.terminal_filesystem_navigation
  --hard/tool_environment_dependency-->
skill.c.compile_link_run_basic

skill.programming.state_assignment_model
  --hard/conceptual_dependency-->
skill.c.declarations_types_expressions

skill.c.compile_link_run_basic
  --hard/tool_environment_dependency-->
skill.c.declarations_types_expressions

skill.programming.expression_boolean_reasoning
  --soft/conceptual_dependency-->
skill.c.conditionals_loops_basic

skill.c.declarations_types_expressions
  --hard/procedural_dependency-->
skill.c.conditionals_loops_basic

skill.c.declarations_types_expressions
  --hard/procedural_dependency-->
skill.c.functions_basic

skill.memory.address_value_distinction
  --hard/conceptual_dependency-->
skill.c.pointer_declaration_dereference_basic

skill.c.declarations_types_expressions
  --hard/procedural_dependency-->
skill.c.pointer_declaration_dereference_basic

skill.c.functions_basic
  --hard/conceptual_dependency-->
skill.memory.storage_lifetime_intuition

skill.c.pointer_declaration_dereference_basic
  --soft/conceptual_dependency-->
skill.memory.storage_lifetime_intuition
```

## 10.5 DS&A branch

```text
skill.programming.iteration_reasoning
  --hard/conceptual_dependency-->
skill.dsa.sequence_traversal_linear_search

skill.dsa.sequence_traversal_linear_search
  --soft/conceptual_dependency-->
skill.dsa.complexity_growth_intuition
```

## 10.6 English

English seed Skills teknik Skill'lere global hard edge oluşturmaz.

Exact technical task English-only ise QAB/Task metadata:

```text
language_prerequisite_skill_ids[]
```

ile gerekli English Skill'i açıkça deklaratif taşır. Aynı technical Objective Türkçe/bilingual variant ile ölçülebiliyorsa English eksikliği target technical evidence'ı bloke etmez.

---

# 11. Cross-domain reuse

5C aşağıdaki capability'leri duplicate etmez:
- `state_assignment_model` Python ve C'de ortak mental model olarak reuse edilir,
- `expression_boolean_reasoning` Python/C control flow'a supporting prerequisite olur,
- `iteration_reasoning` Python/C/DS&A bağlamında reuse edilir,
- `trace_execution_basic` Python/C debugging task'lerinde reuse edilir,
- `debug_localization_basic` language-specific bug contexts içinde aynı canonical capability olarak kullanılabilir,
- `reproducible_run_notes` Python/C/Linux small-project artifacts içinde reuse edilir.

Language-specific syntax/production capability ise ayrı Skill'dir. Python conditionals mastery, C conditionals production mastery'yi bedava vermez.

---

# 11.1 Explicit TopicSkillLink seed matrix — 5D corrective patch

5D, Topic→Skill→Objective reachability'nin isim benzerliğine bırakılmaması için minimum machine-verifiable placement matrix'ini kilitler. AŞAMA 6C bunu genişletebilir; aynı semantic Skill farklı Topic'te clone'lanmaz.

```text
topic.english.core_technical_labels -> teach/core -> skill.english.recognize_core_technical_labels
topic.english.bilingual_instruction_fragments -> teach/core -> skill.english.follow_bilingual_technical_instruction
topic.english.terminal_error_fragments -> teach/core -> skill.english.read_simple_terminal_error_fragments

topic.python.program_execution_bridge -> teach/core -> skill.computing.program_execution_model
topic.python.program_execution_bridge -> teach/core -> skill.computing.source_runtime_artifact_distinction
topic.python.program_execution_bridge -> teach/core -> skill.python.run_repl_script

topic.python.values_state_expressions -> teach/core -> skill.programming.state_assignment_model
topic.python.values_state_expressions -> teach/core -> skill.programming.expression_boolean_reasoning
topic.python.values_state_expressions -> teach/core -> skill.python.values_variables_expressions

topic.python.input_output_first_programs -> teach/core -> skill.python.input_output_basic
topic.python.input_output_first_programs -> integrate/supporting -> skill.engineering.reproducible_run_notes

topic.python.conditionals -> teach/core -> skill.programming.branching_reasoning
topic.python.conditionals -> reinforce/core -> skill.programming.expression_boolean_reasoning
topic.python.conditionals -> teach/core -> skill.python.conditionals

topic.python.iteration -> teach/core -> skill.programming.iteration_reasoning
topic.python.iteration -> teach/core -> skill.python.for_iteration
topic.python.iteration -> teach/core -> skill.python.while_termination

topic.python.execution_trace_debug -> teach/core -> skill.programming.trace_execution_basic
topic.python.execution_trace_debug -> teach/core -> skill.programming.debug_localization_basic
topic.python.execution_trace_debug -> teach/core -> skill.python.exceptions_read_basic
topic.python.execution_trace_debug -> integrate/supporting -> skill.engineering.explain_debug_fix_basic

topic.python.functions -> teach/core -> skill.programming.function_decomposition
topic.python.functions -> teach/core -> skill.python.functions_parameters_return
topic.python.functions -> reinforce/supporting -> skill.programming.test_case_basic

topic.python.sequence_collections -> teach/core -> skill.python.sequence_collections_basic
topic.python.mapping_collections -> teach/core -> skill.python.mapping_collections_basic

topic.python.errors_modules_files_intro -> reinforce/core -> skill.python.exceptions_read_basic
topic.python.errors_modules_files_intro -> teach/core -> skill.python.modules_imports_basic
topic.python.errors_modules_files_intro -> teach/core -> skill.python.files_paths_basic

topic.c.compile_link_run -> teach/core -> skill.c.compile_link_run_basic
topic.c.compile_link_run -> reinforce/supporting -> skill.computing.source_runtime_artifact_distinction
topic.c.compile_link_run -> reinforce/supporting -> skill.linux.process_exit_stdout_stderr_basic
topic.c.compile_link_run -> integrate/supporting -> skill.engineering.reproducible_run_notes

topic.c.declarations_expressions_io -> teach/core -> skill.c.declarations_types_expressions
topic.c.declarations_expressions_io -> reinforce/core -> skill.programming.state_assignment_model
topic.c.declarations_expressions_io -> reinforce/supporting -> skill.programming.expression_boolean_reasoning

topic.c.control_flow_functions -> teach/core -> skill.c.conditionals_loops_basic
topic.c.control_flow_functions -> teach/core -> skill.c.functions_basic
topic.c.control_flow_functions -> reinforce/core -> skill.programming.branching_reasoning
topic.c.control_flow_functions -> reinforce/core -> skill.programming.iteration_reasoning
topic.c.control_flow_functions -> reinforce/supporting -> skill.programming.function_decomposition

topic.c.address_value_model -> teach/core -> skill.memory.address_value_distinction
topic.c.pointer_dereference_intro -> teach/core -> skill.c.pointer_declaration_dereference_basic
topic.c.storage_lifetime_intro -> teach/core -> skill.memory.storage_lifetime_intuition

topic.linux.terminal_filesystem_navigation -> teach/core -> skill.linux.terminal_filesystem_navigation
topic.linux.process_io_exit_status -> teach/core -> skill.linux.process_exit_stdout_stderr_basic
topic.shell.command_options_redirection -> teach/core -> skill.shell.command_options_redirection_basic
topic.git.repository_status_diff -> teach/core -> skill.git.repository_status_diff
topic.git.stage_commit_history -> teach/core -> skill.git.stage_commit_history_basic

topic.dsa.sequence_traversal_linear_search -> teach/core -> skill.dsa.sequence_traversal_linear_search
topic.dsa.sequence_traversal_linear_search -> reinforce/core -> skill.programming.iteration_reasoning
topic.dsa.complexity_growth_intuition -> teach/core -> skill.dsa.complexity_growth_intuition
```

Bu placement matrix learner mastery state yaratmaz; KGC-v0 `TopicSkillLink` semantics'idir.

---

# 12. Diagnostic anchors

5C fixed “placement exam score” üretmez. Diagnostic yalnız Skill/Objective eligibility ile çalışır.

Seed behavior:
- shared mental-model skills → diagnostic eligible,
- Python basics → diagnostic eligible,
- Linux/Git basics → diagnostic eligible,
- C basics → diagnostic eligible,
- pointer/memory skills → diagnostic possible fakat prerequisite-valid H0 evidence gerekir,
- English recognition → diagnostic eligible; bilinmeyen grammar üretim istemez.

Diagnostic PASS content coverage'ı azaltabilir; GRE/VDW kurallarını bypass edip bedava mastery üretmez.

---

# 13. Retention anchors

Skill retention profile tablodaki `factual | standard | complex` değerini kullanır.

5C:
- yeni review interval uydurmaz,
- RVR-v0 defaults'ı değiştirmez,
- review_due'yu failure saymaz.

AŞAMA 15 her required Skill için uygun retention resource family üretebilmelidir:
- factual → recall/recognition,
- standard → fresh short production/explanation,
- complex → fresh code/debug/transfer/system task.

---

# 14. Remediation anchors

Static remediation tags örnek controlled families:

```text
mental_model_reteach
worked_example
state_trace
micro_code_drill
fresh_variant
error_message_walkthrough
debug_localization
prerequisite_repair
terminal_guided_practice
git_state_visualization
bilingual_language_scaffold
```

Bu tag'ler learner weakness state değildir. Runtime failure attribution exact Objective/Skill'e gider; broad Domain tekrarına dönüşmez.

Örnek:

```text
python.while task fail
+ trace correct
+ termination condition wrong
→ remediation target = skill.python.while_termination
→ whole Python domain reset = forbidden
```

---

# 15. Assessment resource authoring needs

AŞAMA 15/QAB authoring, 5C scope'taki required Objective'ler için resource coverage sağlamalıdır.

Minimum contract sabit soru sayısı değildir. Her Objective için evidence profile'ın ihtiyaç duyduğu **uygun modality/family** bulunabilmelidir.

Beklenen resource families:
- concept/explanation items,
- user-authored Python code tasks,
- user-authored C code + compiler/test tasks,
- fresh debugging tasks,
- terminal/filesystem tasks,
- Git repository-state tasks,
- DS&A small implementation/reasoning tasks,
- English recognition/controlled-comprehension items,
- delayed retention variants,
- cross-skill small integration tasks.

Near-duplicate item family evidence diversity'yi şişiremez; QAB-v0/AIV-v0 canonical kurallar korunur.

---

# 16. AŞAMA 15 production-content handoff

AŞAMA 15 şu sırayla çalışmalıdır:

```text
5C seed scope
→ 6A naming/granularity ratification
→ 6C Foundations detailed map + 6H QA
→ published graph version
→ lesson/practice/remediation/resource authoring
→ QAB/AIV validation
→ production curriculum package
```

AŞAMA 15 her published required Objective için en az şu authoring rollerinin karşılanabildiğini doğrulamalıdır:
- teach/explain,
- guided practice,
- independent practice,
- assessable H0 evidence path,
- remediation path,
- retention/reverification path.

Bu ifade her Objective için mutlaka altı ayrı fiziksel resource dosyası demek değildir; tek resource birden fazla uygun role sahip olabilir. Evidence contamination ve solution exposure kuralları korunur.

---

# 17. AŞAMA 5D için graph QA fixture'ları

5D en az aşağıdaki subgraph durumlarını test etmelidir.

## F5D-01 — Zero user
- Python/English/Linux giriş branch'leri erişilebilir olmalı.
- C pointer branch'i zero state'te doğrudan açılmamalı.

## F5D-02 — Python hızlı, Linux zayıf
- Python language branch bağımsız ilerleyebilir.
- C compile/run gibi Linux tool-environment hard prerequisite isteyen task bloke kalabilir.
- Tüm curriculum donmamalı.

## F5D-03 — English zayıf, technical güçlü
- English remediation/learning devam eder.
- Türkçe/bilingual technical task'ler gerçek technical prerequisites sağlanıyorsa devam eder.
- English global gate oluşmamalı.

## F5D-04 — Memory address gap
- `pointer_declaration_dereference_basic` bloke olmalı.
- Python/Git/English bağımsız branch'leri devam etmeli.

## F5D-05 — Review due prerequisite
- RVR `review_due` olan mastered prerequisite PRG-v0 gereği otomatik `not_ready` olmamalı.

## F5D-06 — Debugging local prerequisite gap
- debug task'in exact trace prerequisite'i yoksa target evidence contaminate edilmemeli; prerequisite repair açılmalı.

## F5D-07 — Duplicate Skill placement
- shared `iteration_reasoning` Python/DS&A Topics içinde iki ayrı learner Skill state üretmemeli.

## F5D-08 — No dead-end foundation
- required seed Skill ya terminal/teaching entry point'e ya da geçerli prerequisite chain'e sahip olmalı.
- inaccessible required node bulunmamalı.

## F5D-09 — No cycle
- hard prerequisite graph DAG olmalı.
- soft edge cycle dahi authoring ambiguity yaratıyorsa raporlanmalı.

## F5D-10 — V1 content handoff reachability
- AŞAMA 15'in her published Objective için resource bağlayabileceği Topic/Skill/Objective path'i bulunmalı.

---

# 18. Performance / bounded traversal notu

FBB-v0 küçük olsa da runtime tasarım ilkesi değişmez:
- full graph scan planner primitive'i değildir,
- outgoing/incoming prerequisite adjacency kullanılabilir,
- target Skill'in local dependency neighborhood'u bounded şekilde çözülür,
- Topic→Skill placements indexlenebilir,
- English/technical parallel branches tüm graph traverse edilmeden seçilebilir.

Exact database/index/caching limits 9C/9F/18E calibration'a bırakılır.

---

# 19. Research / Coding / Test AI kararı

5C'de ayrı Research AI kullanılmadı.

Gerekçe:
- 5C'nin görevi external job-market veya full professional coverage doğrulaması değil,
- D-049 PDM-v0, D-051 KGC-v0, V1 scope ve accepted learning contracts üzerinde bounded V1 seed subgraph formalizasyonudur.

Ayrı external Research AI zorunluluğu planlandığı gibi **6H — Coverage / prerequisite / Research QA** adımında kalır. 6H, 5C seed'ini de full Foundations map'le birlikte missing/duplicate/hidden-prerequisite/current-relevance açısından sorgulayabilir.

Coding AI kullanılmadı; physical implementation yoktur.
5D independent graph architecture QA ayrı numaralı sonraki adımdır; 5C içinde PASS ilan edilmez.

---

# 20. Acceptance gate — 5C

5C ancak aşağıdakiler sağlandığında tamamlanır:

1. “8–12 hafta” takvim değil scope-equivalent olarak tanımlandı.
2. Computer/Programming zero-entry bridge var fakat yeni broad career Domain'i gereksiz yaratılmadı.
3. Python, C, Linux/Git/Shell, early DS&A ve Technical English seed placements var.
4. English day-one parallel; global technical hard gate değil.
5. KGC-v0 uyumlu Skill/Objective skeleton mevcut.
6. Shared concept Skill ile language-specific production Skill ayrımı yapıldı.
7. Initial hard/soft prerequisite edges Skill seviyesinde ve branch-isolated.
8. `required/critical/optional` global boolean yerine scope-relative kullanılıyor.
9. Evidence profile templates GRE/QAB/RVR'ı değiştirmeden bağlandı.
10. Diagnostic/retention/remediation anchor'ları tanımlı.
11. Production lesson/task body 5C'ye yanlışlıkla taşınmadı.
12. 6A/6C'nin seed'i explicit migration/ratification ile refine edebileceği lifecycle tanımlı.
13. AŞAMA 15 için teach/practice/assessment/remediation/retention handoff'u var.
14. 5D için cycle/dead-end/hidden-prerequisite/reuse/parallelism fixture'ları var.
15. 6H external Research AI validation scope'u korunuyor.

---

# 21. Final 5C kararı

**Final model:** `FBB-v0 — V1 Foundation Backbone`

Canonical V1 foundation envelope:

```text
Technical English ───────────────────────────────────────────────▶ parallel

Zero-entry computing/programming bridge
      ├──▶ Python foundations ──▶ control flow ──▶ functions/data
      │            │                    │                │
      │            └────▶ trace/debug/test basics ◀─────┘
      │
      ├──▶ Linux / Shell / Git working environment ──────────────┐
      │                                                          │
      └──▶ C compile/run ─▶ C basics ─▶ address/value ─▶ pointer│
                                                                 │
Python iteration + sequence ─────────▶ early DS&A ───────────────┤
                                                                 ▼
                              small integrated/reproducible artifacts
```

Bu diagram calendar sequence değildir. Runtime eligibility yalnız PRG-v0 Skill readiness, learner state ve task metadata ile belirlenir.

**5C tamamlandıktan sonraki numaralı adım:** `5D — Graph architecture QA`.

# 22. 5D corrective QA addendum

Canonical QA: `docs/GRAPH_ARCHITECTURE_QA.md` / GQA-v0 / D-053.

5D initial audit iki blocking structural problem buldu: explicit TopicSkillLink matrix eksikliği ve KGC controlled vocabulary dışında `reason_kind=supporting`. Ayrıca bazı required seed Skills'te hidden-prerequisite/zero-state eligibility riski bulundu. Bu dosyadaki corrective authoring-seed patch bunları düzeltti.

Corrected seed:
- hard prerequisite graph DAG,
- hard+soft same-pair conflict yok,
- self/dangling edge yok,
- reason kind vocabulary KGC-v0 uyumlu,
- Topic→Skill→Objective authoring path explicit,
- English global technical hard gate yok,
- branch isolation korunuyor,
- 6A/6C ratification öncesi learner-published değil.

FBB-v0 authoring seed olduğu için bu QA patch published learner semantic state'i mutate etmez. 6A/6C ratification ve 6H external Research QA öncesi production publish yasaktır.

---

**15A note (2026-10-02, `D-112`):** the first production package ratified §6.1's shared computing/programming subgraph as 6C decomposed it (12 Skills, 13 Objectives, 11 internal edges) from `draft` to `published`, with identities unchanged and each Objective's refined metadata recorded with its reason; §16's authoring roles are met for every Objective. Details: `docs/COMPUTING_FUNDAMENTALS_CONTENT_SPEC.md`.

---

**15B note (2026-10-02, `D-113`):** the second production package ratified §6.2's twelve Python seeds as 6C decomposed them (seventeen Skills), plus the three hard prerequisites outside them (`scope_name_resolution`, `exception_handling`, `string_text_operations`; user decision): 20 Skills, 21 Objectives, 31 edges, from `draft` to `published`, identities unchanged. Details: `docs/PYTHON_FOUNDATIONS_CONTENT_SPEC.md`.

---

**15C note (2026-10-03, `D-114`):** the third production package ratified §6.3's four C seeds as 6C decomposed them (seven Skills), plus `standard_io_basic` and the §6.4 Linux seed `terminal_filesystem_navigation` that the first C Skill depends on (user decisions): 9 Skills, 10 Objectives, 15 edges, `draft` → `published`, identities unchanged. Pointers and memory remain 15D's. Details: `docs/C_FOUNDATIONS_CONTENT_SPEC.md`.
