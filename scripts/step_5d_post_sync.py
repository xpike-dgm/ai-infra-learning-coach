from pathlib import Path
from collections import defaultdict, deque


def read(p): return Path(p).read_text(encoding='utf-8')
def write(p, s): Path(p).write_text(s, encoding='utf-8')
def replace_once(p, old, new):
    s = read(p)
    c = s.count(old)
    if c != 1:
        raise SystemExit(f'{p}: expected one occurrence, found {c}: {old[:90]!r}')
    write(p, s.replace(old, new))
def append_once(p, marker, block):
    s = read(p)
    if marker in s: return
    write(p, s.rstrip() + '\n\n' + block.strip() + '\n')

# Guard current state
for p in ['PROJECT_CONTEXT.md','docs/START_HERE.md','docs/HANDOFF_STATE.md','docs/STEP_STATUS.md','docs/EXECUTION_INDEX.md','docs/MASTER_PLAN.md']:
    s=read(p)
    if '5D' not in s:
        raise SystemExit(f'{p}: missing 5D pre-state')
qa=read('docs/GRAPH_ARCHITECTURE_QA.md')
if 'GQA-v0' not in qa or 'D-053' not in qa or 'corrective patch sonrası `GQA-v0` PASS' not in qa:
    raise SystemExit('5D QA spec missing completion markers')

# ------------------------------------------------------------------
# FBB-v0 corrective authoring-seed patch
# ------------------------------------------------------------------
fbb='docs/V1_FOUNDATION_BACKBONE.md'

# Seed metadata defaults: authoring-only, not published.
marker = '# 3. V1 backbone scope yapısı'
seed_meta = '''# 2.1 Authoring-seed metadata defaults — 5D clarification

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

'''
s=read(fbb)
if 'seed_schema_version = FBB-v0+GQA-v0' not in s:
    if marker not in s: raise SystemExit('FBB marker 3 missing')
    s=s.replace(marker, seed_meta+marker)
    write(fbb,s)

# normalize two soft edges to hard
replace_once(fbb,
'''skill.programming.state_assignment_model
  --soft/supporting-->
skill.programming.trace_execution_basic''',
'''skill.programming.state_assignment_model
  --hard/evidence_interpretability-->
skill.programming.trace_execution_basic''')
replace_once(fbb,
'''skill.programming.debug_localization_basic
  --soft/supporting-->
skill.engineering.explain_debug_fix_basic''',
'''skill.programming.debug_localization_basic
  --hard/evidence_interpretability-->
skill.engineering.explain_debug_fix_basic''')
# normalize remaining unsupported reason kinds
replace_once(fbb,
'''skill.python.conditionals
  --soft/supporting-->
skill.python.for_iteration''',
'''skill.python.conditionals
  --soft/conceptual_dependency-->
skill.python.for_iteration''')
replace_once(fbb,
'''skill.python.conditionals
  --soft/supporting-->
skill.python.while_termination''',
'''skill.python.conditionals
  --soft/conceptual_dependency-->
skill.python.while_termination''')
replace_once(fbb,
'''skill.linux.process_exit_stdout_stderr_basic
  --soft/supporting-->
skill.c.compile_link_run_basic''',
'''skill.linux.process_exit_stdout_stderr_basic
  --soft/tool_environment_dependency-->
skill.c.compile_link_run_basic''')
replace_once(fbb,
'''skill.dsa.sequence_traversal_linear_search
  --soft/supporting-->
skill.dsa.complexity_growth_intuition''',
'''skill.dsa.sequence_traversal_linear_search
  --soft/conceptual_dependency-->
skill.dsa.complexity_growth_intuition''')

# add minimal hidden-prerequisite corrections to Python branch
old='''skill.python.values_variables_expressions
  --hard/procedural_dependency-->
skill.python.functions_parameters_return

skill.python.for_iteration'''
new='''skill.python.values_variables_expressions
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

skill.python.for_iteration'''
replace_once(fbb, old, new)

# add storage/lifetime hard context
old='''skill.c.pointer_declaration_dereference_basic
  --soft/conceptual_dependency-->
skill.memory.storage_lifetime_intuition'''
new='''skill.c.functions_basic
  --hard/conceptual_dependency-->
skill.memory.storage_lifetime_intuition

skill.c.pointer_declaration_dereference_basic
  --soft/conceptual_dependency-->
skill.memory.storage_lifetime_intuition'''
replace_once(fbb,old,new)

# explicit TopicSkillLink matrix before diagnostics
matrix='''# 11.1 Explicit TopicSkillLink seed matrix — 5D corrective patch

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

'''
s=read(fbb)
if '# 11.1 Explicit TopicSkillLink seed matrix — 5D corrective patch' not in s:
    m='# 12. Diagnostic anchors'
    if m not in s: raise SystemExit('FBB diagnostic marker missing')
    s=s.replace(m,matrix+m)
    write(fbb,s)

# add 5D result note before final decision
append_once(fbb, '# 22. 5D corrective QA addendum', '''
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
''')

# ------------------------------------------------------------------
# Decision D-053
# ------------------------------------------------------------------
append_once('docs/DECISIONS.md','## D-053 — Foundation graph architecture QA = GQA-v0','''
## D-053 — Foundation graph architecture QA = GQA-v0
**Durum:** Kabul edildi — 2026-08-25

- 5D final modeli `GQA-v0 — Foundation Graph Architecture QA` oldu.
- Canonical QA dosyası `docs/GRAPH_ARCHITECTURE_QA.md`.
- İlk FBB-v0 audit'inde explicit TopicSkillLink matrix eksikliği ve KGC controlled vocabulary dışı `reason_kind=supporting` kullanımı blocking structural bulgu olarak saptandı ve düzeltildi.
- Hidden-prerequisite audit'i Python I/O/collections/mapping/files, Python exception/module context, trace→debug chain, debug-fix explanation ve C storage/lifetime context için minimal required hard edge'leri ekledi.
- Corrective patch sonrası hard graph DAG; self/dangling/conflicting edge yok; combined relations cycle üretmiyor.
- Shared capability reuse canonical Skill + TopicSkillLink ile çözülür; clone learner state yasaktır.
- English global technical hard gate değildir; branch isolation korunur.
- `review_due` PRG/RVR gereği hard prerequisite'i otomatik `not_ready` yapmaz.
- FBB entity'leri hâlâ `authoring_seed / not_learner_published`; 6A/6C ratification ve 6H external Research QA öncesi production publish yapılmaz.
- 5D PASS full professional curriculum coverage doğrulaması değildir; 6H independent Research AI zorunluluğu korunur.

Ayrıntı: `docs/GRAPH_ARCHITECTURE_QA.md`.
''')

# ------------------------------------------------------------------
# Execution index
# ------------------------------------------------------------------
replace_once('docs/EXECUTION_INDEX.md','- D-052: 5C final V1 foundation backbone `FBB-v0`.\n','- D-052: 5C final V1 foundation backbone `FBB-v0`.\n- D-053: 5D final foundation graph architecture QA `GQA-v0`; corrective seed patch PASS.\n')
replace_once('docs/EXECUTION_INDEX.md','- [ ] **5D — Graph architecture QA** **AKTİF** — cycle/dead-end/hidden prerequisite, duplicate/reuse ve reachability kontrolü','- [x] **5D — Graph architecture QA** — `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053')
replace_once('docs/EXECUTION_INDEX.md','- [ ] **6A — Granularity + naming standardı** — Domain/Module/Topic/Skill/Objective sınırları, canonical ID, over-fragmentation guard','- [ ] **6A — Granularity + naming standardı** **AKTİF** — Domain/Module/Topic/Skill/Objective sınırları, canonical ID, over-fragmentation guard')
replace_once('docs/EXECUTION_INDEX.md','**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5C`  \n**Aktif:** **`5D — Graph architecture QA`**\n\n5C FBB-v0 / D-052 ile tamamlandı. 5D henüz yürütülmedi; 5D başlamadan yeni PRE-STEP GitHub refresh zorunludur.','**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`  \n**Aktif:** **`6A — Granularity + naming standardı`**\n\n5D GQA-v0 / D-053 ile tamamlandı ve AŞAMA 5 kapandı. 6A henüz yürütülmedi; 6A başlamadan fresh PRE-STEP GitHub refresh zorunludur.')

# ------------------------------------------------------------------
# MASTER_PLAN
# ------------------------------------------------------------------
replace_once('docs/MASTER_PLAN.md','### [ ] 5D — Graph architecture QA — **AKTİF**\n- cycle/dead-end,\n- hidden prerequisite,\n- duplicate canonical Skill / Topic reuse,\n- reachability / branch isolation,\n- scalability/versioning / migration handoff.','### [x] 5D — Graph architecture QA — GQA-v0 / D-053\n**Final:** `docs/GRAPH_ARCHITECTURE_QA.md`\n\n- initial FBB structural blockers bulundu ve corrective authoring-seed patch uygulandı,\n- explicit TopicSkillLink matrix eklendi,\n- KGC reason-kind vocabulary normalize edildi,\n- hidden prerequisite / accidental zero-eligibility riskleri minimal hard edges ile düzeltildi,\n- hard graph DAG / no self-dangling-conflicting edges PASS,\n- English global-gate / branch isolation / reachability fixtures PASS,\n- 6A/6C ratification + 6H external Research QA guard korunuyor.')
replace_once('docs/MASTER_PLAN.md','### [ ] 6A — Granularity + naming standardı','### [ ] 6A — Granularity + naming standardı — **AKTİF**')
# current location at end
s=read('docs/MASTER_PLAN.md')
if '**Aktif:** **`5D — Graph architecture QA`**' in s:
    s=s.replace('**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5C`  \n**Aktif:** **`5D — Graph architecture QA`**','**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`  \n**Aktif:** **`6A — Granularity + naming standardı`**')
    s=s.replace('Bir sonraki yürütme: **5D başlamadan yeni PRE-STEP GitHub refresh → Graph architecture QA → POST-STEP D-050 sync + stale-reference audit.**','Bir sonraki yürütme: **6A başlamadan fresh PRE-STEP GitHub refresh → granularity/naming standardı → POST-STEP D-050 sync + stale-reference audit.**')
    write('docs/MASTER_PLAN.md',s)

# ------------------------------------------------------------------
# STEP_STATUS replace tail
# ------------------------------------------------------------------
s=read('docs/STEP_STATUS.md')
s=s.replace('| **5D — Graph architecture QA** | 🟡 Aktif | FBB-v0 cycle/dead-end/hidden prerequisite/duplicate/reachability açısından doğrulanacak. **Henüz yürütülmedi.** |\n| **6A–20** | ⬜ Bekliyor | 5D sonrası canonical sırada. |','| **5D — Graph architecture QA** | ✅ | GQA-v0 / D-053. Corrective seed patch sonrası architecture QA PASS. |\n| **6A — Granularity + naming standardı** | 🟡 Aktif | AŞAMA 6 naming/granularity contract tasarlanacak. **Henüz yürütülmedi.** |\n| **6B–20** | ⬜ Bekliyor | 6A sonrası canonical sırada. |')
marker='## Son tamamlanan numaralı adım — 5C'
pos=s.find(marker)
if pos<0: raise SystemExit('STEP_STATUS tail marker missing')
tail='''## Son tamamlanan numaralı adım — 5D

**Final:** `GQA-v0 — Foundation Graph Architecture QA` / D-053.  
Ana çıktı: `docs/GRAPH_ARCHITECTURE_QA.md`.

5D sonucu:
- initial FBB-v0 iki blocking structural issue ile başladı,
- explicit TopicSkillLink seed matrix eklendi,
- invalid `reason_kind=supporting` canonical KGC reason kinds'e normalize edildi,
- target evidence'ı contaminate eden hidden prerequisite boşlukları minimal hard edges ile düzeltildi,
- hard graph DAG; self/dangling/conflicting edge yok,
- branch isolation, English global-gate guard, duplicate/reuse, required reachability ve 5C→6/15 handoff fixtures PASS,
- FBB hâlâ authoring_seed/not-learner-published; 6A/6C + 6H öncesi production publish yok.

5D ayrı external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research QA 6H'de zorunlu kalır.

## Aktif adım — 6A Granularity + naming standardı

**6A henüz yürütülmedi.**

6A başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca fresh PRE-STEP GitHub refresh zorunludur.

6A'da özellikle:
- Domain/Module/Topic/Skill/Objective granularity sınırları,
- canonical logical ID convention,
- stable identity vs display label,
- under/over-fragmentation guard,
- language-specific vs shared capability split kriteri,
- Objective atomization/observable-action standardı,
- FBB authoring_seed ratification/refactor kuralları,
- version/migration naming invariants

kesinleştirilecek.
'''
write('docs/STEP_STATUS.md',s[:pos]+tail)

# ------------------------------------------------------------------
# HANDOFF_STATE replace tail from section 8
# ------------------------------------------------------------------
s=read('docs/HANDOFF_STATE.md')
s=s.replace('- **D-052:** FBB-v0 V1 Foundation Backbone; 5C tamamlandı.\n','- **D-052:** FBB-v0 V1 Foundation Backbone; 5C tamamlandı.\n- **D-053:** GQA-v0 Foundation Graph Architecture QA; 5D corrective patch sonrası PASS.\n')
marker='## 8. Tamamlanan aşamalar'
pos=s.find(marker)
if pos<0: raise SystemExit('HANDOFF_STATE marker missing')
newtail='''## 8. D-053 / 5D final özeti

Canonical: `docs/GRAPH_ARCHITECTURE_QA.md`.

GQA-v0:
- initial FBB-v0 audit'inde explicit TopicSkillLink eksikliği ve invalid `reason_kind=supporting` bulundu,
- TopicSkillLink seed matrix FBB'ye eklendi,
- reason kinds KGC controlled vocabulary'ye normalize edildi,
- Python/data/error/module/file, trace/debug, professional debug explanation ve C storage/lifetime hidden-prerequisite riskleri minimal hard edges ile düzeltildi,
- corrected hard graph DAG; self/dangling/conflicting edge yok,
- shared Skill reuse TopicSkillLink ile, clone mastery state yok,
- English global technical hard gate yok,
- F5D-01..F5D-10 fixtures PASS,
- FBB authoring_seed olarak kalır; 6A/6C/6H öncesi learner-published değildir.

5D external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research QA 6H'de zorunlu kalır.

## 9. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6:
  - 6A 🟡 Granularity + naming standardı — aktif, henüz yürütülmedi
  - 6B–6H ⬜
- AŞAMA 7–20 ⬜

## 10. Güncel kesin konum

**Aktif:** `6A — Granularity + naming standardı`  
**6A henüz yürütülmedi.**

## 11. 6A'da kesinleştirilecekler

Ana soru:
> 23 route family yüzlerce/binlerce capability'ye ayrılırken hangi semantic sınırda yeni Domain/Module/Topic/Skill/Objective yaratılmalı ve canonical identity nasıl yıllarca stabil tutulmalı?

Kesinleştirilecek:
- entity-level granularity sınırları,
- canonical logical ID naming convention,
- display label vs identity ayrımı,
- over-fragmentation / under-fragmentation guard,
- shared vs language/tool-specific Skill split kriterleri,
- Objective atomicity + observable action standardı,
- FBB seed ratification / split / merge / rename kuralları,
- KGC migration/versioning uyumu,
- 6B decomposition template handoff'u.

6A full route decomposition yapmaz; 6B–6F bunu kullanır.

## 12. 6A için PRE-STEP doğrudan okunacaklar
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. `docs/GRAPH_ARCHITECTURE_QA.md`
8. `docs/V1_FOUNDATION_BACKBONE.md`
9. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
10. `docs/LEARNING_ENGINE_SPEC.md`
11. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
12. `docs/CURRICULUM_DOMAIN_MAP.md`
13. `docs/PREREQUISITE_POLICY_SPEC.md`
14. `docs/PROJECT_MEMORY_PROTOCOL.md`

6A başlamadan fresh PRE-STEP GitHub refresh zorunludur.
'''
write('docs/HANDOFF_STATE.md',s[:pos]+newtail)

# ------------------------------------------------------------------
# PROJECT_CONTEXT
# ------------------------------------------------------------------
s=read('PROJECT_CONTEXT.md')
s=s.replace('## 7. Curriculum backbone / knowledge graph — 5A–5B tamamlandı','## 7. Curriculum backbone / knowledge graph — AŞAMA 5 tamamlandı')
s=s.replace('**D-052 / FBB-v0:** canonical `docs/V1_FOUNDATION_BACKBONE.md`. V1 başlangıç seed subgraph\'ı zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English olarak tanımlandı. 8–12 hafta calendar gate değil scope-equivalent\'tır; Skill/Objective IDs 6A/6C öncesi `authoring_seed` lifecycle\'ındadır.','**D-052 / FBB-v0:** canonical `docs/V1_FOUNDATION_BACKBONE.md`. V1 başlangıç seed subgraph\'ı zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English olarak tanımlandı. 8–12 hafta calendar gate değil scope-equivalent\'tır; Skill/Objective IDs 6A/6C öncesi `authoring_seed` lifecycle\'ındadır.\n\n**D-053 / GQA-v0:** canonical `docs/GRAPH_ARCHITECTURE_QA.md`. 5D initial structural blockers ve hidden-prerequisite risklerini corrective seed patch ile düzeltti; hard graph DAG, TopicSkillLink/reuse explicit, English global-gate yok, F5D fixtures PASS.')
old='''- AŞAMA 5 devam ediyor:
  - 5A ✅ PDM-v0 / D-049
  - 5B ✅ KGC-v0 / D-051
  - 5C ✅ FBB-v0 / D-052
  - **5D 🟡 Graph architecture QA — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
- AŞAMA 6–20 ⬜

**Sıradaki numaralı çalışma 5D'dir.** 5D başlamadan fresh PRE-STEP GitHub refresh zorunludur.'''
new='''- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6:
  - **6A 🟡 Granularity + naming standardı — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
  - 6B–6H ⬜
- AŞAMA 7–20 ⬜

**Sıradaki numaralı çalışma 6A'dır.** 6A başlamadan fresh PRE-STEP GitHub refresh zorunludur.'''
if old not in s: raise SystemExit('PROJECT_CONTEXT state block missing')
s=s.replace(old,new)
write('PROJECT_CONTEXT.md',s)

# ------------------------------------------------------------------
# START_HERE
# ------------------------------------------------------------------
s=read('docs/START_HERE.md')
needle='''### D-052 — FBB-v0
5C final V1 foundation backbone `docs/V1_FOUNDATION_BACKBONE.md` içinde zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English seed subgraph'ını tanımladı. 8–12 hafta calendar gate değildir; seed IDs 6A/6C ratification öncesi learner-published değildir.
'''
insert=needle+'''\n### D-053 — GQA-v0\n5D final graph architecture QA `docs/GRAPH_ARCHITECTURE_QA.md` içinde FBB seed graph'ı cycle/dead-end/hidden prerequisite/duplicate/reuse/English-global-gate/reachability açısından doğruladı; blocking structural sorunları corrective patch ile düzeltti ve AŞAMA 5'i kapattı.\n'''
if '### D-053 — GQA-v0' not in s:
    if needle not in s: raise SystemExit('START_HERE D-052 needle missing')
    s=s.replace(needle,insert)
s=s.replace('''- 5 Curriculum/knowledge graph backbone — **aktif**
  - 5A ✅ PDM-v0
  - 5B ✅ KGC-v0 / D-051
  - 5C ✅ FBB-v0 / D-052
  - 5D 🟡 Graph architecture QA
- 6 Granular Capability Map''','''- 5 Curriculum/knowledge graph backbone ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- 6 Granular Capability Map — **aktif**
  - 6A 🟡 Granularity + naming standardı
  - 6B–6H ⬜''')
s=s.replace('''### AŞAMA 5 ilerlemesi
- 5A ✅ `PDM-v0 — Professional Domain Backbone` / D-049
- 5B ✅ `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051
- 5C ✅ `FBB-v0 — V1 Foundation Backbone` / D-052
- 5D 🟡 Graph architecture QA

## 9. Güncel çalışma konumu

**Aktif:** **`5D — Graph architecture QA`**  
**5D henüz yürütülmedi.**

5D, `docs/V1_FOUNDATION_BACKBONE.md` içindeki FBB-v0 authoring-seed graph'ını KGC-v0/PRG-v0 invariants altında cycle, dead-end, hidden prerequisite, duplicate semantic Skill, accidental English/global gate, branch isolation ve Objective reachability açısından doğrulayacak. Production content yazmayacak.

5D başlamadan yeni PRE-STEP GitHub refresh zorunlu.''','''### AŞAMA 5 ✅
- 5A ✅ `PDM-v0 — Professional Domain Backbone` / D-049
- 5B ✅ `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051
- 5C ✅ `FBB-v0 — V1 Foundation Backbone` / D-052
- 5D ✅ `GQA-v0 — Foundation Graph Architecture QA` / D-053

### AŞAMA 6 ilerlemesi
- 6A 🟡 Granularity + naming standardı — aktif, henüz yürütülmedi

## 9. Güncel çalışma konumu

**Aktif:** **`6A — Granularity + naming standardı`**  
**6A henüz yürütülmedi.**

6A, KGC-v0 + GQA-v0 üzerinde Domain/Module/Topic/Skill/Objective granularity sınırlarını, canonical logical ID convention'ını, shared-vs-specific capability ayrımını ve over/under-fragmentation guard'larını kilitleyecek.

6A başlamadan yeni PRE-STEP GitHub refresh zorunlu.''')
s=s.replace('D-041, D-042, D-044–D-052','D-041, D-042, D-044–D-053')
s=s.replace('Şu an aktif adım 5D — Graph architecture QA; 5D henüz yürütülmedi.','Şu an aktif adım 6A — Granularity + naming standardı; 6A henüz yürütülmedi.')
write('docs/START_HERE.md',s)

# ------------------------------------------------------------------
# CURRICULUM summary / Granular plan / README / Project master context
# ------------------------------------------------------------------
s=read('docs/CURRICULUM.md')
s=s.replace('**Durum:** 5A DOMAIN BACKBONE + 5B KNOWLEDGE GRAPH CONTRACT TAMAMLANDI / DETAIL AŞAMA 6\'DA','**Durum:** AŞAMA 5 TAMAMLANDI — PDM-v0 + KGC-v0 + FBB-v0 + GQA-v0 / DETAIL AŞAMA 6\'DA')
s=s.replace('5D = graph architecture QA','5D ✅ = GQA-v0 graph architecture QA + corrective seed patch')
s=s.replace('6A–6H = full granular capability map + independent coverage/prerequisite Research QA','6A 🟡 = granularity + naming standardı\n6B–6H = full granular capability map + independent coverage/prerequisite Research QA')
if 'GRAPH_ARCHITECTURE_QA.md' not in s:
    s=s.replace('**5B graph contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`  ','**5B graph contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`  \n**5D graph QA:** `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053  ')
write('docs/CURRICULUM.md',s)

s=read('docs/GRANULAR_CAPABILITY_MAP_PLAN.md')
if '**5D architecture QA input:**' not in s:
    s=s.replace('**5C V1 seed input:** `docs/V1_FOUNDATION_BACKBONE.md` / FBB-v0 / D-052\n','**5C V1 seed input:** `docs/V1_FOUNDATION_BACKBONE.md` / FBB-v0 / D-052\n**5D architecture QA input:** `docs/GRAPH_ARCHITECTURE_QA.md` / GQA-v0 / D-053\n')
write('docs/GRANULAR_CAPABILITY_MAP_PLAN.md',s)

s=read('README.md')
if 'docs/GRAPH_ARCHITECTURE_QA.md' not in s:
    s=s.replace('- `docs/V1_FOUNDATION_BACKBONE.md`', '- `docs/V1_FOUNDATION_BACKBONE.md` — V1 foundation authoring-seed graph / FBB-v0\n- `docs/GRAPH_ARCHITECTURE_QA.md` — 5D foundation graph structural QA / GQA-v0') if '- `docs/V1_FOUNDATION_BACKBONE.md`' in s else s.replace('- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — versioned knowledge-graph schema / KGC-v0\n','- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — versioned knowledge-graph schema / KGC-v0\n- `docs/V1_FOUNDATION_BACKBONE.md` — V1 foundation authoring-seed graph / FBB-v0\n- `docs/GRAPH_ARCHITECTURE_QA.md` — 5D foundation graph structural QA / GQA-v0\n')
write('README.md',s)

append_once('docs/PROJECT_MASTER_CONTEXT.md','## D-053 / 5D architecture guard','''
## D-053 / 5D architecture guard
`GQA-v0 — Foundation Graph Architecture QA` FBB authoring seed'i structural olarak doğrular. Explicit TopicSkillLink, KGC reason-kind vocabulary, minimal hidden-prerequisite hard edges, branch isolation, English global-gate guard ve DAG/reachability invariants AŞAMA 6 decomposition için başlangıç guard'ıdır. Canonical QA: `docs/GRAPH_ARCHITECTURE_QA.md`. FBB seed learner-published değildir; 6A/6C ratification + 6H external Research QA gerekir.
''')

# ------------------------------------------------------------------
# PROGRESS LOG
# ------------------------------------------------------------------
append_once('docs/PROGRESS_LOG.md','### 2026-08-25 — 5D Graph Architecture QA tamamlandı','''
### 2026-08-25 — 5D Graph Architecture QA tamamlandı

**PRE-STEP GitHub refresh**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `PROJECT_CONTEXT` ve 5D handoff'ta listelenen direct specs fresh okundu.
- 5A–5C'nin tamamlandığı, 5D'nin gerçek aktif ve henüz yürütülmemiş adım olduğu doğrulandı.

**Execution / findings**
- FBB-v0 41 Skill / 47 Objective seed ve initial prerequisite graph structural audit'e alındı.
- Initial audit explicit TopicSkillLink matrix eksikliği ve invalid `reason_kind=supporting` kullanımını blocker olarak buldu.
- Python/data/error/module/file, trace/debug, professional debug explanation ve C lifetime context'te hidden-prerequisite/accidental-zero-eligibility riskleri bulundu.
- FBB authoring seed'e corrective patch uygulandı: explicit TopicSkillLink matrix, reason-kind normalization ve minimal hard prerequisite edges.
- Corrected graph hard-edge DAG; self/dangling/conflicting edge yok; combined relations cycle yok.
- F5D-01..F5D-10 branch isolation / English gate / review_due / duplicate reuse / reachability fixtures PASS.

**Research/Coding/Test kararı**
- External Research AI kullanılmadı; 5D internal graph architecture QA'dır. Full coverage/current-industry/prerequisite external Research QA 6H'de zorunlu kalır.
- Physical runtime implementation olmadığı için Coding AI kullanılmadı; structural verification deterministic/static QA ile yapıldı.

**Final:** `GQA-v0 — Foundation Graph Architecture QA` / D-053. Canonical: `docs/GRAPH_ARCHITECTURE_QA.md`.

**POST-STEP**
- D-050 ALWAYS-CHECK living files senkronlandı.
- AŞAMA 5 kapandı; 6A active/not-executed yapıldı.
- FBB corrective authoring-seed patch ve AŞAMA 6 handoff'u ilgili curriculum/context docs'a işlendi.
- Repo-wide stale 5D-active/5D-not-executed ve missing D-053 references tarandı.

**Sonraki kesin adım:** `6A — Granularity + naming standardı`. 6A başlamadan fresh PRE-STEP GitHub refresh zorunlu.
''')

# ------------------------------------------------------------------
# Structural invariants from corrected canonical seed
# ------------------------------------------------------------------
nodes=[
'computing.program_execution_model','computing.source_runtime_artifact_distinction','programming.state_assignment_model','programming.expression_boolean_reasoning','programming.branching_reasoning','programming.iteration_reasoning','programming.function_decomposition','programming.trace_execution_basic','programming.debug_localization_basic','programming.test_case_basic','python.run_repl_script','python.values_variables_expressions','python.input_output_basic','python.conditionals','python.for_iteration','python.while_termination','python.functions_parameters_return','python.sequence_collections_basic','python.mapping_collections_basic','python.exceptions_read_basic','python.modules_imports_basic','python.files_paths_basic','c.compile_link_run_basic','c.declarations_types_expressions','c.conditionals_loops_basic','c.functions_basic','memory.address_value_distinction','c.pointer_declaration_dereference_basic','memory.storage_lifetime_intuition','linux.terminal_filesystem_navigation','linux.process_exit_stdout_stderr_basic','shell.command_options_redirection_basic','git.repository_status_diff','git.stage_commit_history_basic','dsa.sequence_traversal_linear_search','dsa.complexity_growth_intuition','english.recognize_core_technical_labels','english.follow_bilingual_technical_instruction','english.read_simple_terminal_error_fragments','engineering.reproducible_run_notes','engineering.explain_debug_fix_basic']
hard=[
('python.run_repl_script','python.values_variables_expressions'),('programming.state_assignment_model','python.values_variables_expressions'),('programming.expression_boolean_reasoning','python.conditionals'),('programming.iteration_reasoning','python.while_termination'),('python.values_variables_expressions','python.conditionals'),('python.values_variables_expressions','python.for_iteration'),('python.values_variables_expressions','python.while_termination'),('python.values_variables_expressions','python.functions_parameters_return'),('python.values_variables_expressions','python.input_output_basic'),('python.values_variables_expressions','python.sequence_collections_basic'),('python.values_variables_expressions','python.mapping_collections_basic'),('python.run_repl_script','python.exceptions_read_basic'),('python.run_repl_script','python.modules_imports_basic'),('python.values_variables_expressions','python.files_paths_basic'),('python.sequence_collections_basic','dsa.sequence_traversal_linear_search'),('programming.state_assignment_model','programming.trace_execution_basic'),('programming.trace_execution_basic','programming.debug_localization_basic'),('programming.debug_localization_basic','engineering.explain_debug_fix_basic'),('linux.terminal_filesystem_navigation','git.repository_status_diff'),('git.repository_status_diff','git.stage_commit_history_basic'),('linux.terminal_filesystem_navigation','c.compile_link_run_basic'),('programming.state_assignment_model','c.declarations_types_expressions'),('c.compile_link_run_basic','c.declarations_types_expressions'),('c.declarations_types_expressions','c.conditionals_loops_basic'),('c.declarations_types_expressions','c.functions_basic'),('memory.address_value_distinction','c.pointer_declaration_dereference_basic'),('c.declarations_types_expressions','c.pointer_declaration_dereference_basic'),('c.functions_basic','memory.storage_lifetime_intuition'),('programming.iteration_reasoning','dsa.sequence_traversal_linear_search')]
soft=[
('computing.program_execution_model','python.run_repl_script'),('programming.branching_reasoning','python.conditionals'),('python.conditionals','python.for_iteration'),('python.conditionals','python.while_termination'),('programming.function_decomposition','python.functions_parameters_return'),('python.for_iteration','dsa.sequence_traversal_linear_search'),('programming.test_case_basic','engineering.explain_debug_fix_basic'),('linux.terminal_filesystem_navigation','shell.command_options_redirection_basic'),('linux.process_exit_stdout_stderr_basic','c.compile_link_run_basic'),('computing.program_execution_model','c.compile_link_run_basic'),('programming.expression_boolean_reasoning','c.conditionals_loops_basic'),('c.pointer_declaration_dereference_basic','memory.storage_lifetime_intuition'),('dsa.sequence_traversal_linear_search','dsa.complexity_growth_intuition')]
N=set(nodes)
for a,b in hard+soft:
    assert a in N and b in N and a!=b
assert not (set(hard)&set(soft))
def acyclic(edges):
    indeg={n:0 for n in nodes}; out=defaultdict(list)
    for a,b in edges: out[a].append(b); indeg[b]+=1
    q=deque(n for n,d in indeg.items() if d==0); seen=0
    while q:
        n=q.popleft(); seen+=1
        for m in out[n]:
            indeg[m]-=1
            if indeg[m]==0:q.append(m)
    return seen==len(nodes)
assert acyclic(hard)
assert acyclic(hard+soft)
fbbtxt=read(fbb)
assert '--soft/supporting-->' not in fbbtxt
assert '# 11.1 Explicit TopicSkillLink seed matrix — 5D corrective patch' in fbbtxt

print('5D POST-SYNC STRUCTURAL AUDIT: PASS')
