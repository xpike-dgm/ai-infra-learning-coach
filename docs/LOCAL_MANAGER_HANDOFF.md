# Local Manager Handoff — AI Infra Learning Coach

**Amaç:** Yerel çalışan ana yönetici/koordinatör agent'a projeyi kayıpsız devretmek.  
**Durum:** CURRENT MANAGER TAKEOVER BOOTSTRAP  
**Tarih:** 2026-08-26  
**Repo:** `xpike-dgm/ai-infra-learning-coach`  
**Branch:** `main`

> Bu belge geniş bir takeover özetidir; **canonical spec'lerin yerine geçmez**. Eksiksiz devralma ancak repo içindeki tüm Markdown dosyaları okunup current-state kaynakları fresh doğrulandıktan sonra tamamlanmış sayılır.

---

# 0. İlk 15 dakika — zorunlu takeover prosedürü

Yerel ana yönetici hiçbir numaralı proje adımına başlamadan önce şunları yapmalıdır:

1. `AGENTS.md` dosyasını oku.
2. Bu dosyayı (`docs/LOCAL_MANAGER_HANDOFF.md`) baştan sona oku.
3. `docs/START_HERE.md` oku.
4. `docs/PROJECT_MEMORY_PROTOCOL.md` oku.
5. `PROJECT_CONTEXT.md`, `docs/HANDOFF_STATE.md`, `docs/EXECUTION_INDEX.md`, `docs/STEP_STATUS.md`, `docs/DECISIONS.md`, `docs/MASTER_PLAN.md` dosyalarını fresh oku.
6. Repo içindeki tüm Markdown dosyalarının envanterini çıkar ve **tamamını oku**. Yalnız bu handoff'a güvenerek karar verme.
7. `git status`, current branch ve HEAD'i doğrula; kullanıcı açıkça istemedikçe local uncommitted değişiklikleri bozma.
8. Current execution state'in `AŞAMA 6 ✅ / AŞAMA 7 ✅ / 8A ✅ UXIA-v0 / D-068 / 8B ✅ THUX-v0 / D-069 / 8C ✅ TRUX-v0 / D-070 / 8D ✅ ASUX-v0 / D-071 / 8E ✅ SPWX-v0 / D-072 / 8F ✅ VDSX-v0 / D-073 / 8G ✅ WFPX-v0 / D-074 / 9A ✅ AMTS-v0 / D-075 / 9B ✅ LFPS-v0 / D-076 / 9C ✅ DDM-v0 / D-077 / 9D ✅ MSBX-v0 / D-078 / 9E ✅ AIAX-v0 / D-079 / 9F ✅ TVSX-v0 / D-081 / 10A ✅ MPSX-v0 / D-082 / 10B ✅ NSHX-v0 / D-083 / 10C ✅ DSIX-v0 / D-084 / 10D ✅ LDBX-v0 / D-085 / 10E ✅ APHX-v0 / D-086 / 11A ✅ TDYX-v0 / D-087 / 11B ✅ RNRX-v0 / D-088 / 11C ✅ SESX-v0 / D-089 / 11D ✅ DMAX-v0 / D-090 / 11E ✅ EODX-v0 / D-091 / 12A ✅ MSTX-v0 / D-092 / 12B ✅ PRQX-v0 / D-093`; AŞAMA 8, AŞAMA 9, AŞAMA 10 ve AŞAMA 11'in kapandığını, AŞAMA 12'nin başladığını ve aktif adımın `12C active-not-executed` olduğunu living-memory setiyle doğrula.
9. Ancak bundan sonra, aktif numbered step için **ayrı bir fresh PRE-STEP GitHub refresh** yap; kullanıcı açık onayı olmadan yürütme.

Önerilen local komutlar:

```bash
git status --short --branch
git pull --ff-only
find . -type f -name '*.md' -not -path './.git/*' -print | sort
```

Repo içeriğini topluca incelemek gerekiyorsa agent kendi context kapasitesine göre dosyaları gruplar halinde okumalıdır; dosya listesine bakmak okuma sayılmaz.

---

# 1. Yetki ve source-of-truth hiyerarşisi

## 1.1 Durable source of truth

GitHub/repo kalıcı hafızadır. Sohbet hafızası, agent scratchpad'i veya önceki yöneticinin özeti repo ile çelişirse repo doğrulanır.

## 1.2 Current execution source of truth

Current step/state için birlikte doğrulanacak yaşayan kaynaklar:

- `docs/EXECUTION_INDEX.md` — canonical adım kodları ve sıra,
- `docs/STEP_STATUS.md` — hızlı current state,
- `docs/HANDOFF_STATE.md` — ayrıntılı current handoff,
- root `PROJECT_CONTEXT.md` — kısa current snapshot,
- `docs/MASTER_PLAN.md` — ayrıntılı checklist/completion durumu,
- `docs/START_HERE.md` — yeni agent bootstrap.

Bunlardan biri stale ise D-050 gereği düzeltilir; tek bir dosya kör source-of-truth olarak kullanılmaz.

## 1.3 Kalıcı karar source of truth

`docs/DECISIONS.md`.

Ayrıntılı davranış her decision'ın işaret ettiği canonical spec'tedir.

## 1.4 Historical / noncanonical kaynaklar

- `docs/LEARNING_ENGINE.md` = `HISTORICAL / SUPERSEDED` pointer; canonical learning engine kaynağı değildir.
- `docs/ENGLISH_TRACK.md` = `NON-CANONICAL SEED NOTES`; exact CEFR final kararı değildir.
- D-029 = D-031 ile superseded.
- D-043 = **GERİ ÇEKİLDİ / NONCANONICAL**. Standalone specialization stage olarak geri getirilmez.
- Eski future-stage numaraları görülürse `docs/STAGE_REINDEX_MAP.md` ile current karşılığı kontrol edilir.
- Eski `docs/TODO.md` obsolete/stale olduğu için silinmiştir; yeniden source-of-truth yapılmaz.

---

# 2. Projenin tek cümlelik amacı

**AI Infra Learning Coach; sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten, her gün ne öğrenmesi ve ne uygulaması gerektiğini mevcut bilgi durumuna göre belirleyen, yalnızca gerçekten öğrenildiği ölçümlerle kanıtlanan becerileri ilerleme kabul eden ve assessment, retention, remediation, prerequisite ve performance sonuçlarına göre gelecekteki çalışma planını otomatik yeniden düzenleyen kişisel adaptif Android öğrenme koçudur.**

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Kullanıcının ana deneyimi:

```text
uygulamayı aç
→ bugün ne yapacağın hazır
→ öğren / uygula
→ gerçekten öğrenip öğrenmediğin ölçülsün
→ exact Skill/Objective state güncellensin
→ retention/prerequisite kontrol edilsin
→ gerekiyorsa remediation
→ gelecek plan yeniden hesaplansın
```

Ürün statik course player değildir.

---

# 3. Kullanıcı ve ürün kapsamı

- Tek kullanıcı / kişisel kullanım.
- Android odaklı.
- V1 local-first.
- Auth/payment/social/admin/multi-tenant SaaS ana kapsam değildir.
- Modern, sade, profesyonel UI.
- Ana ekranın sorusu: **“Bugün ne yapmalıyım?”**
- Streak ana başarı metriği değildir.
- `Gün X / toplam gün`, career completion yüzdesi veya yalnız geçirilen süre gerçek ilerleme/readiness metriği değildir.
- Mobil uygulama coach'tur; full workstation/IDE olmak zorunda değildir. Coding artifact'leri PC/editor/terminal üzerinde üretilebilir.
- AI servisinin erişilememesi deterministic local core'u çökertecek tasarım değildir.

---

# 4. Uzun vadeli professional-readiness hedefi — D-041

Full curriculum yaklaşık üç yıllık üst sınırla kısıtlı değildir. **4+ yıl veya daha uzun** sürebilir; bu süre countdown/gate değildir.

Canonical denklem:

```text
elapsed_time != professional_readiness
curriculum_completion != automatic professional_readiness
professional_readiness = verified capability across required professional domains + capstone/transfer evidence
```

Final hedef, kullanıcıyı AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek bağımsız teknik capability seviyesine taşımaktır.

Profesyonel readiness yalnız ders/quiz geçmek değildir. Kullanıcı önemli problemler üzerinde mümkün olduğunca bağımsız biçimde:

- problemi anlamalı,
- sistemi tasarlamalı,
- Python/C/C++ ile uygulamalı,
- Linux üzerinde debug etmeli,
- memory/OS/concurrency/networking/distributed davranışı analiz etmeli,
- GPU/CUDA/Triton performance darboğazını ölçmeli,
- LLM inference stack'ini kurup incelemeli,
- throughput/latency/memory/cost trade-off'larını ölçmeli,
- test/benchmark/profiling yapmalı,
- reliability/observability temellerini uygulamalı,
- teknik kararlarını açıklamalı,
- dokümantasyon ve source code okuyabilmeli,
- tutorial kopyalamadan yeni problem çözebilmelidir.

Uygulama iş teklifi, maaş, senior title, HR/degree filtrelerini aşma veya gerçek ekip tecrübesi garantisi vermez.

## Professional evidence katmanları

A. Foundation evidence  
B. Applied engineering evidence  
C. Integrated systems evidence  
D. GPU/inference evidence  
E. Professional capstone evidence

Final readiness:
- critical Skill'lerde independent direct evidence,
- debugging,
- transfer,
- delayed retention,
- performance/profiling,
- integrated project/capstone
ister.

AI-generated artifact tek başına final readiness evidence değildir.

---

# 5. Canonical uzun teknik rota — 23 route family

Bu liste roadmap summary'dir; runtime lineer takvim değildir.

1. Technical English — parallel
2. Python
3. C
4. Linux + Git + Shell
5. Data Structures & Algorithms foundations
6. Modern C++
7. Computer Architecture
8. Operating Systems + Memory
9. Concurrency / Parallel Programming
10. Networking
11. Distributed Systems + Storage / Databases foundations
12. Containers / Cloud / Observability
13. Performance Engineering & Profiling
14. GPU Architecture
15. CUDA
16. Triton
17. ML + Transformer foundations
18. LLM Inference Internals
19. vLLM / SGLang / TensorRT-LLM-style serving systems
20. KV Cache / Batching / Scheduling / Quantization
21. Multi-GPU + NCCL + RDMA
22. AI Infrastructure / GPU Infrastructure
23. Open Source contributions + real large projects + professional capstones

Ana yön:

```text
strong systems/distributed engineer
→ GPU/CUDA
→ LLM inference/serving
→ AI/GPU infrastructure
```

Python official common foundation'dır fakat C/C++/CUDA'nın yerine geçmez.

Bridge kariyer rolleri mümkündür: C++ Systems, Systems Software, Linux/Infrastructure, Distributed Systems, Performance, uygun Backend/SRE/Cloud vb. İlk işin doğrudan CUDA olmak zorunda olmadığı kabul edilmiştir.

---

# 6. Domain backbone — PDM-v0 / D-049

Canonical: `docs/CURRICULUM_DOMAIN_MAP.md`.

Domainler takvim/mastery atomu değildir. High-level roller:

```text
parallel_track
common_foundation
systems_core
distributed_platform_core
performance_core
accelerator_core
supporting_domain
inference_systems_core
target_infrastructure
professional_evidence_layer
```

Kilit kararlar:

- Technical English tüm rota boyunca paralel.
- Python + C + Linux/Git/Shell complementary early foundations; katı seri kurs değil.
- DS&A supporting common foundation.
- Systems core: Modern C++, Architecture, OS/Memory, Concurrency, Networking.
- Distributed/platform: Distributed Systems + Storage/DB + Containers/Cloud/Observability.
- Performance Engineering sona bırakılan optimization chapter değil; route boyunca büyür.
- GPU Architecture → CUDA/Triton systems/performance temelinin üstüne gelir.
- Triton GPU/CUDA mental modelini bypass etmez.
- ML/Transformer generic ML research specialization değil, inference için gereken supporting domain.
- Gerekli math/numerical bilgisi hidden prerequisite bırakılamaz.
- LLM Inference, serving ve KV/batching/scheduling/quantization ayrı ama bağlı family'ler.
- Multi-GPU/NCCL/RDMA = networking + distributed + GPU convergence.
- AI/GPU Infrastructure = systems + distributed + cloud/observability + performance + inference + multi-GPU integration target.
- OSS/engineering practice/projects/capstones yalnız sonda başlamaz; route boyunca artar.
- Security/reliability/observability ilgili alanlara cross-cutting dağıtılır; yanlışlıkla ayrı dev cybersecurity specialization yapılmaz.
- Vendor/tool adı stable concept'in yerine geçmez; freshness/version metadata ile ayrılır.
- Domain-level authoring relations runtime hard prerequisite değildir.

---

# 7. Curriculum knowledge graph — KGC-v0 / D-051

Canonical: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

Resmî model:

```text
Domain → Module → Topic
        organization / teaching / navigation

Skill → Learning Objective
        capability / evidence / mastery
```

Kritik invariant:

```text
Topic completed != Skill mastered
Domain progress != professional readiness
```

## Skill

- canonical reusable capability identity,
- learner için tek mastery/retention state,
- farklı Topic/Domain'de clone'lanmaz,
- reuse `TopicSkillLink` ile yapılır.

## Learning Objective

- exactly one canonical Skill'e bağlı,
- atomic observable evidence target,
- `required | optional` requirement role,
- `standard | critical` criticality.

## Prerequisite

Canonical runtime dependency:

```text
SkillPrerequisiteEdge
source Skill → target Skill
hard | soft
```

Domain/Module/Topic relations authoring guidance'dır.

## Graph migration

Published semantic state immutable/versioned. Split/merge/refactor historical evidence'ı sessizce silmez ve learner'a bedava mastery vermez.

Compatibility:

```text
fully_compatible
compatible_with_reverification
not_automatically_transferable
```

## Performance

Runtime full-graph scan'e dayanmayacak. Adjacency, reverse dependency, indexes/cache/bounded traversal. Exact physical DB/index budgets AŞAMA 9/18'de.

---

# 8. Granularity ve ID standardı — GNS-v0 / D-054

Canonical: `docs/GRANULARITY_NAMING_STANDARD.md`.

Bu 6A'nın final çıktısıdır ve AŞAMA 6'nın geri kalanında bağlayıcıdır.

## Organization vs capability

- Domain/Module/Topic = organization/teaching context.
- Skill = reusable learner capability state.
- Objective = atomic observable evidence target.

## Yeni Skill ne zaman açılır?

Semantik karar; sayısal puan formülü yok.

Ayrı Skill güçlü adaydır eğer:
- independent evidence anlamlı,
- independent failure/remediation anlamlı,
- prerequisite boundary anlamlı,
- cross-context reuse boundary anlamlı,
- evidence/depth/retention/professional requirement anlamlı biçimde ayrışıyor.

Sadece:
- keyword,
- punctuation,
- lesson sırası,
- story/context,
- Topic placement
farkı yeni Skill gerektirmez.

## Under-fragmentation guard

Skill zayıf sonucu hâlâ “neyi tekrar etmeliyiz?” sorusunu cevaplamıyorsa, objectives bağımsız failure profile taşıyorsa, dependency/evidence/remediation yalnız bir alt davranışa aitse split review gerekir.

## Over-fragmentation guard

İki candidate her zaman aynı prerequisite/evidence/remediation ile birlikte hareket ediyor ve ayrı state planner kararını değiştirmiyorsa gereksiz mikro Skill açma.

## Shared vs specific

- Dil/araçtan bağımsız mental model gerçek anlamda aynıysa shared Skill.
- Language syntax/runtime/library/procedure capability'nin özünü değiştiriyorsa language-specific Skill.
- Tool kullanımının kendisi professional capability ise tool-specific Skill olabilir.
- Aynı capability farklı senaryoda uygulanıyorsa yeni context/resource/transfer evidence, yeni Skill değil.

## Canonical ID

```text
<entity_type>.<semantic_namespace>.<semantic_slug>[.<subslug>]
```

Kurallar:
- lowercase ASCII,
- dotted namespace,
- segment içinde snake_case,
- locale/order/version bağımsız,
- display label'dan bağımsız.

ID'ye gömülmez:
- week/day,
- stage numarası,
- FB band,
- release v1/v2,
- entity version,
- lesson sıra numarası,
- locale,
- difficulty/mastery state,
- required/critical/optional rolü.

`basic/advanced/intro` yalnız gerçek semantic scope taşıyorsa kabul edilir.

## FBB seed ratification statuses

6C'de her relevant seed:

```text
ratify_as_is
ratify_with_display_edit
normalize_logical_id
split_required
merge_with_existing
rehome_placement_only
deprecate_seed
needs_granularity_review
```

ile incelenir.

---

# 9. V1 Foundation Backbone — FBB-v0 / D-052

Canonical: `docs/V1_FOUNDATION_BACKBONE.md`.

V1 “ilk 8–12 hafta” = sabit calendar unlock değil; başlangıç content scope büyüklüğü.

Seed subgraph:
- Computer/Programming zero-entry bridge,
- Python,
- C,
- Linux/Git/Shell,
- early DS&A,
- day-one parallel Technical English.

FBB seed'leri:

```text
lifecycle_status = authoring_seed
publication_state = not_learner_published
```

6A/6C ratification ve 6H external Research QA öncesi production-published değildir.

Progression bands FB0–FB5 yalnız authoring/navigation grouping; runtime week/gate değildir.

5D GQA-v0 corrective patch sonrası:
- explicit TopicSkillLink matrix var,
- invalid reason kinds normalize,
- hidden prerequisite boşluklarına minimal hard edges eklendi,
- hard graph DAG,
- self/dangling/conflicting edge yok,
- English global gate yok,
- branch isolation korunuyor.

5D PASS full curriculum coverage doğrulaması değildir.

---

# 10. Learning model — AŞAMA 2

## D-021 — unit model

```text
Domain → Module → Topic → Skill → Learning Objective
```

Canonical mastery/prerequisite ana seviyesi Skill; atomik evidence Objective.

## D-023 — Topic state machine

Derived work states:

```text
locked
available
learning
mastered
weakening
remediation_required
```

Topic state bağımsız mastery truth değildir; Skill state'lerinden türetilir.

Bir random error bütün Topic'i weakening/remediation yapmaz.

## D-025 — mastery signals

Evidence türleri birbirinden ayrılır:
- recognition,
- recall,
- code reading,
- coding,
- debugging,
- explanation,
- transfer,
- delayed retention,
- project/artifact.

Time/completion/streak/self-confidence mastery değildir.

## D-026 — AI assistance evidence

H0–H4 yardım/provenance modeli vardır. Independent mastery için H0 clean evidence kritik rol oynar. Solution-exposed/AI-assisted evidence'ın güven seviyesi düşürülür; fresh recheck gerekebilir.

## GRE-v0 / D-031 — mastery

Canonical: `docs/MASTERY_FORMULA_V0.md`.

Final yaklaşım ağırlıklı sahte puan değil, gated evidence modelidir.

Mastery için temel olarak:
- eligible,
- prerequisite-valid,
- H0 independent,
- direct,
- verified,
- Objective-matched,
- independent evidence groups,
- family diversity
aranır.

Standard Skill'lerde genel cold-start policy farklı independent groups/family diversity ister; critical Skill daha güçlü independent/direct/context coverage ister. Exact davranışı spec'ten oku; keyfi yeni threshold icat etme.

Bir fresh clean contradiction ile mastery anında çökmez; hysteresis/verification kullanılır.

## RVR-v0 / D-032 — retention

Time tek başına negative evidence değildir.

Retention semantic states:

```text
untracked
fresh
stable
review_due
verification_due
at_risk
```

`review_due != forgotten`.

İlk clean post-mastery fail tipik olarak `verification_due` üretir; tek hata broad regression yaratmaz.

---

# 11. Adaptive planner — AŞAMA 3

## D-033 — daily hard capacity

Daily time gerçek hard budget'tır.

```text
safe split
→ smaller valid alternative
→ defer
```

Auto-overrun yok. Deferred task debt değildir.

## D-034 — task taxonomy / LearningNeed

Kalıcı olan stale task değil `LearningNeed`'dir.

```text
State
→ LearningNeed
→ TaskCandidate
→ PlannedTask
→ Attempt / Artifact
→ EvidenceEvent
→ state update
→ replan
```

Purpose/activity/track/evidence ayrı eksenlerdir.

## PBR-v0 / D-035 — priority

Eligibility/trust önce; sonra semantic priority bands P0–P4 + deterministic rank vector. Weighted “magic score” yok.

Duration semantic priority'den sonra planı capacity'ye sığdırmak için kullanılır.

Starvation/track balance guard vardır.

## PRG-v0 / D-036 — prerequisite

Skill→Skill hard/soft.

Readiness:

```text
ready
ready_due
uncertain
not_ready
```

- `ready_due` hard gate'i otomatik bozmaz.
- `not_ready` hard dependent work'u bloke eder.
- soft prerequisite hiçbir zaman tek başına hard block üretmez.
- missing prerequisite yalnız dependent branch'i etkiler.
- prerequisite contamination target failure'ı yanlış Skill'e yazmamalıdır.
- English global hard gate değildir.

## VDW-v0 / D-037 — diagnostic waiver

Hızlı öğrenen kullanıcı validated Objective-level diagnostic evidence ile bazı instruction/practice'i atlayabilir. Diagnostic mastery'nin kolay alternatifi değildir; self-report mastery vermez.

## SRR-v0 / D-038 — missed day

Absence failure/decay/debt değildir. Eski günlük planlar replay edilmez; current state'ten replan edilir.

## PDT-v0 / D-039 — explainability

Planner structured reason codes ve deterministic semantic decision trace üretir. User explanation yalnız trace'teki factual nedenlere dayanır; private chain-of-thought kaydı değildir.

## 3H simulation

- 16/16 scenarios PASS
- 20/20 invariants PASS
- 0 critical contradiction

Bu spec-level simulation'dır; runtime implementation daha sonra test edilecek.

---

# 12. Assessment system — AŞAMA 4

## DMA-v0 / D-040 — Daily Micro Assessment

Daily quiz kotası değildir. Planner yalnız ihtiyaç olduğunda assessment koyar.

Amaçlar:
- practice,
- assess,
- retain,
- diagnose.

Assessment raw score doğrudan broad mastery yazmaz.

Pipeline:

```text
Attempt/Artifact
→ validity/prerequisite/assistance/provenance/evaluator/attribution
→ EvidenceEvents
→ GRE/RVR
→ planner replan
```

No fixed question count/time/percentage.

## WBA-v0 / D-045 — Weekly

Item'dan önce state-based blueprint.

Role families:
- recent_required_progress,
- weakness_or_verification,
- critical_prerequisite_confidence,
- retention_due,
- integration_or_transfer,
- parallel_english.

Bunlar kota değildir.

No fixed score/time/category percentage. Weekly label evidence'a extra ağırlık vermez. Session split/pause/resume olabilir. Missed/incomplete assessment debt/failure değildir.

## MCA-v0 / D-046 — Monthly

Longitudinal state-based sampling; daha geniş transfer/integration.

Role families:
- longitudinal_required_capability,
- persistent_weakness_or_verification,
- critical_capability_revalidation,
- delayed_retention_sampling,
- cross_topic_transfer,
- integrated_application,
- parallel_technical_english,
- professional_evidence_checkpoint.

Cumulative everything exam değildir; professional checkpoint final readiness değildir.

## QAB-v0 / D-047 — Assessment Resource Bank

Question bank yalnız MCQ değildir.

Resource kinds arasında:
- recognition,
- recall,
- code-reading,
- coding,
- debugging,
- hands-on system,
- explanation,
- transfer,
- integrated,
- language,
- testlet,
- parameterized template.

Stable `resource_id` + immutable published `resource_version`.

Lifecycle:

```text
draft | candidate | validated | trusted | deprecated | invalidated | retired
```

Use ceiling lifecycle'dan ayrıdır:

```text
practice_only
low_stakes_assessment
standard_mastery_eligible
critical_mastery_eligible
```

`variant_family != dependency_group/testlet != context_family != transfer_profile`.

Same/near variants independent evidence diversity'yi şişiremez. Translation yeni independent family değildir.

Integrated task global PASS bütün tagged Objectives'e yayılmaz. Her Objective attribution ayrı observable/prerequisite-valid/evaluator-sufficient olmalıdır.

Difficulty numeric mastery multiplier değildir. Learner exposure freshness ile technology/content freshness ayrıdır.

Resource doğrudan `mastery_delta` taşımaz.

## AIV-v0 / D-048 — AI-generated resource validation

AI output kendi correctness proof'u değildir.

Generated resource başlangıçta:

```text
content_origin = ai_generated
lifecycle_status = candidate
selection_eligible = false
validated_use_ceiling = none
```

Validation ayrı boyutlarda yapılır:
- schema/canonical refs,
- technical correctness,
- answer/rubric/test correctness,
- ambiguity/multiple valid answers,
- Objective fit,
- evidence modality fit,
- prerequisite completeness,
- forbidden-not-yet leakage,
- English fairness,
- duplicate/family/dependency/context/transfer,
- integrated attribution,
- evaluator/tool/artifact compatibility,
- freshness,
- execution/environment safety.

Generator self-review veya majority vote truth değildir. Deterministic/executable/reference-grounded validation öncelikli.

Practice-only yanlış içeriği kullanıcıya gösterme izni değildir.

Validator disagreement promotion'ı durdurur.

Confirmed content bug'ta resource invalidated olabilir ve historical evidence review/repair gerekir; learner cezalandırılmaz.

Empirical validator/evaluator thresholds daha sonra AŞAMA 14F/18'de calibration ile çözülür.

---

# 13. Technical English — bağlayıcı güvenlik kuralları

- Başlangıç A0 olabilir.
- İlk günden teknik hatla paralel.
- English tüm teknik eğitimin global prerequisite'i değildir.
- Bilinmeyen grammar/function word teknik failure'a dönüşemez.
- Gerekirse Turkish/bilingual scaffold kullanılır.
- Teknik task gerçekten English gerektiriyorsa explicit language prerequisite/task metadata kullanılır.
- CEFR seviyesi mastery atomu değildir.
- Granular English map 6C; CEFR progression/cadence/integration detayları AŞAMA 7.
- Long-term hedef docs, terminal/compiler errors, README/man, GitHub issues/PRs, design docs, GPU/CUDA docs, papers, code review, interview/global team communication.

---

# 14. Mobile performance — D-028

Performance/akıcılık first-class requirement.

- ağır computation UI thread'de çalışmaz,
- AI/network async,
- graph traversal bounded/indexed/cache/incremental,
- gereksiz polling yok,
- long history full-rescan varsayımı yok,
- real Android device QA zorunlu,
- “çalışıyor ama lag'li” kabul edilebilir final kalite değildir.

Exact budgets 9F/18E'de kalibre edilir; 6A/6B'de fake ms/MB target uydurma.

---

# 15. V1 kapsamı

Canonical: `docs/V1_SCOPE.md`.

V1 full 4+ yıllık content'i beklemez. V1'in uçtan uca vaadi:

```text
Today plan
→ Task Runner
→ Attempt/Artifact
→ mastery/retention/prerequisite update
→ remediation/replan
```

V1 kesin kapsam özet:
- single-user Android,
- Today screen,
- knowledge graph/prerequisite,
- adaptive daily planner,
- Task Runner,
- GRE-v0 mastery,
- DMA/WBA/MCA,
- retention + remediation,
- AI Tutor,
- parallel Technical English,
- first 8–12 week production curriculum,
- granular progress/weakness,
- local-first persistence,
- settings/reminders,
- professional UI,
- backup/export/restore.

V1 bilinçli non-goals:
- full professional content'in tamamı,
- social/payment/community,
- realtime multi-device cloud sync,
- iOS/web/desktop clients,
- full live job market engine,
- full voice-first tutor,
- telefona full C/C++ IDE,
- aşırı gamification,
- tamamen LLM-controlled curriculum.

V1 release professional curriculum completion değildir.

---

# 16. V1 acceptance felsefesi

Canonical: `docs/V1_SUCCESS_CRITERIA.md`.

Test sonuçları:

```text
PASS
PASS WITH NOTES
FAIL
BLOCKED
```

Priorities:

```text
P0 release blocker
P1 V1 required
P2 quality improvement
```

Kritik invariants arasında:
- daily plan capacity'yi aşmamalı,
- same state deterministic core decision üretmeli,
- hard prerequisite ihlal edilemez,
- independent branch devam etmeli,
- farklı learner state farklı plan üretmeli,
- task completion mastery veremez,
- AI yardım mastery shortcut olamaz,
- weekly/monthly assessment gelecekteki planı gerçekten değiştirmeli,
- due retention ve remediation çalışmalı,
- missed-day debt oluşmamalı,
- English global gate olmamalı,
- AI provider failure deterministic core'u bozmamalı,
- persistence/restart/migration güvenilir olmalı,
- kritik flows gerçek cihaz + independent QA görmeli.

Exact SC-001... acceptance listesi için dosyanın tamamını oku.

---

# 17. Non-goals ve ürün karakteri

Canonical: `docs/NON_GOALS.md`.

Genel guard:
- ürün bir generic LMS veya social education network değildir,
- streak/XP economy ile manipülatif engagement hedeflemez,
- AI'ı deterministic core'un sahibi yapmaz,
- broad course completion'ı professional readiness diye sunmaz,
- takvimi readiness truth yapmaz,
- tek bir universal weighted mastery score ile bütün pedagojik semantiği ezmez.

Ürün karakteri:
- sakin,
- profesyonel,
- net,
- modern,
- karar yükünü azaltan,
- kullanıcıyı ara verdi diye cezalandırmayan.

---

# 18. Multi-AI / Agent iş bölümü — D-016

Canonical: `docs/AI_AGENT_WORKFLOW.md`.

Ana yönetici artık local çalışan agent olabilir; **rolün sorumlulukları değişmez**.

## Manager

- PRE refresh,
- state consistency,
- next-step seçimi,
- Research/Coding/Test delegation,
- araştırmayı karara dönüştürme,
- uygulanabilir spec/task yazma,
- QA sonucunu değerlendirme,
- FAIL'i tekrar coding'e döndürme,
- yalnız acceptance sağlanınca completion,
- POST living-memory sync,
- stale-reference audit,
- GitHub durable memory.

## Research AI

Dış/güncel bilgi, learning science, technology comparison, curriculum/job-market/current docs vb. Araştırma output'u karara dönüşmeden önce manager tarafından değerlendirilir.

## Coding AI

Onaylanmış spec'i implement eder. Kendi implementasyonunu nihai PASS ilan etmez.

## Test AI

Bağımsız acceptance/edge/regression doğrular:

```text
PASS
PASS WITH NOTES
FAIL
BLOCKED
```

FAIL ise iş tamamlanmaz.

## Önemli araştırma dersi

2E sırasında manager bir noktada ayrı Research AI sözü vermiş olmasına rağmen kendi web research'üyle adımı kapatmaya kalkmıştı. Kullanıcı düzeltti; D-030 ile 2E yeniden açıldı ve gerçek ayrı Research AI raporu gelmeden kapanmadı.

**Tekrar etme:** Bir adım için explicit “ayrı Research AI zorunlu” denmişse kendi araştırmanı onun yerine sayma.

Her step otomatik Research AI istemez. 6H'de independent external Research AI **zorunlu**.

---

# 19. GitHub beyin tazeleme protokolü — D-024 / D-027 / D-050

Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md`.

Ana kural:

> Hiçbir numaralı proje adımı PRE-STEP GitHub refresh yapılmadan başlatılmaz; hiçbir numaralı adım ana output + gerekli living-memory sync + MASTER_PLAN + repo-wide stale-reference audit yapılmadan tamamlanmış sayılmaz.

## PRE-STEP minimum

1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. başlanacak adımın relevant canonical specs

Aynı sohbet/terminal session içinde sonraki numaralı adıma geçerken bile PRE yeniden yapılır.

PRE doğrular:
- real active step,
- previous completed,
- living docs consistency,
- binding decisions,
- expected output,
- deferred topics,
- conflict canonical source,
- Research/Coding/Test need.

## POST-STEP ALWAYS-CHECK

1. adımın ana output/spec'i
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/HANDOFF_STATE.md`
5. `docs/PROGRESS_LOG.md`
6. `docs/MASTER_PLAN.md`
7. `PROJECT_CONTEXT.md`
8. `docs/START_HERE.md`
9. `docs/DECISIONS.md`
10. repo-wide stale-reference scan

Etkilenirse README, PROJECT_MASTER_CONTEXT, product/V1/curriculum/stable specs de güncellenir.

## D-050 dosya rolleri

- `PROJECT_CONTEXT.md` = kısa yaşayan snapshot; stale bırakılamaz.
- `START_HERE.md` = yeni sohbet/agent bootstrap; active step güncel.
- `HANDOFF_STATE.md` = detailed current handoff.
- `STEP_STATUS.md` = hızlı state table.
- `EXECUTION_INDEX.md` = canonical step IDs/order.
- `MASTER_PLAN.md` = detailed checklist/completion notes.
- `PROGRESS_LOG.md` = chronological append-style history.
- `DECISIONS.md` = permanent decision log.
- `PROJECT_MASTER_CONTEXT.md` = long/stable context; volatile active step hardcode etmez.
- `README.md` = human entry; current state'i duplicate etmek yerine source'a yönlendirir.
- Stable specs active step değişti diye rewrite edilmez; yalnız actual cross-reference/superseded contract problemi varsa.

## Repo hygiene geçmişi

Bir audit'te `PROJECT_CONTEXT.md` 4B'de stale kalmıştı. D-050 bu nedenle güçlendirildi.

Aynı cleanup'ta:
- obsolete `TODO.md` silindi,
- README/PROJECT_MASTER_CONTEXT volatile current state'ten arındırıldı,
- LEARNING_ENGINE historical pointer,
- ENGLISH_TRACK noncanonical seed yapıldı,
- old stage references tarandı.

6A kapanışında START_HERE üst stage mapping'inde stale `6A 🟡` satırı ayrıca yakalandı ve düzeltildi.

**Ders:** “Bir dosya doğru” demek repo memory doğru demek değildir. Post-step repo-wide current-state drift ara.

---

# 20. Kalıcı decision özeti — D-001...D-058

Bu liste hızlı index'tir. Exact semantik için `docs/DECISIONS.md` tamamını oku.

- D-001: calendar/day counter main progress metric değil.
- D-002: progress mastery-based.
- D-003: knowledge graph calendar'dan öncelikli.
- D-004: missing skill tüm programı dondurmaz.
- D-005: weekly/monthly assessment future programı değiştirir.
- D-006: English parallel.
- D-007: systems → GPU → AI infrastructure ana yönü; D-042 ile Python foundation.
- D-008: single-user personal product.
- D-009: modern/simple mobile UI; Today focus.
- D-010: streak main success metric değil.
- D-011: AI kullanımı yasak değil; independent mastery yerine geçmez.
- D-012: full curriculum V1 prerequisite değil.
- D-013: durations adaptive; mastery/prerequisite daha kalıcı.
- D-014: first job direct CUDA olmak zorunda değil.
- D-015: staged master plan.
- D-016: Research/Coding/Test ayrı AI rolleri.
- D-017: canonical execution step IDs; explicit future reindex mümkündür.
- D-018: V1 gerçek Android adaptive learning loop.
- D-019: V1 release independent QA/acceptance'a bağlı.
- D-020: product/V1 non-goals locked.
- D-021: organization vs mastery unit separation.
- D-022: teaching/error/remediation/retention/replan behaviors binding.
- D-023: Topic state derived Skill work state.
- D-024: every numbered step PRE/POST GitHub refresh.
- D-025: multi-source Objective-matched mastery evidence.
- D-026: AI/hint evidence != independent mastery.
- D-027: MASTER_PLAN canonical state ile sync.
- D-028: mobile performance first-class.
- D-029: old Beta-style weighted mastery candidate superseded.
- D-030: 2E separate Research AI report olmadan kapanamaz — applied.
- D-031: GRE-v0 final mastery.
- D-032: RVR-v0 retention.
- D-033: daily capacity hard budget.
- D-034: persistent entity LearningNeed; stale task değil.
- D-035: PBR-v0 planner priority.
- D-036: PRG-v0 prerequisite.
- D-037: VDW-v0 diagnostic waiver.
- D-038: SRR-v0 missed-day recovery.
- D-039: PDT-v0 explainable planner.
- D-040: DMA-v0 daily assessment.
- D-041: 4+ year professional-readiness target.
- D-042: Python official common foundation.
- D-043: withdrawn/noncanonical specialization-stage interpretation.
- D-044: AŞAMA 6 granular capability map inserted.
- D-045: WBA-v0 weekly.
- D-046: MCA-v0 monthly.
- D-047: QAB-v0 trusted assessment resource bank.
- D-048: AIV-v0 AI-generated resource validation.
- D-049: PDM-v0 professional domain backbone.
- D-050: living-memory sync + repo-wide stale-reference audit.
- D-051: KGC-v0 versioned curriculum graph.
- D-052: FBB-v0 V1 foundation backbone.
- D-053: GQA-v0 foundation graph architecture QA.
- D-054: GNS-v0 granularity & naming standard.
- D-055: local-running agent main manager role'ü devraldı; project contracts değişmedi.
- D-056: FRDB-v0 full-route decomposition authoring blueprint ve ortak QA-ready output contract.
- D-057: FDM-v0 D01–D05 detailed map, FBB 41/47 mapping ve internal graph QA.
- D-058: SDM-v0 D06–D13 detailed map, 6C cross-package reuse ve birleşik hard-graph QA.

D-055–D-058 exact semantiği için `docs/DECISIONS.md` canonical kayıttır.

---

# 21. Tamamlanan proje aşamaları — current summary

- AŞAMA 1 ✅ Product framing
- AŞAMA 2 ✅ Learning/mastery — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ Adaptive planner — PBR/PRG/VDW/SRR/PDT + simulations PASS
- AŞAMA 4 ✅ Assessment — DMA/WBA/MCA/QAB/AIV
- AŞAMA 5 ✅ Curriculum/knowledge-graph backbone
- AŞAMA 6 ✅ Granular Capability Map + external Research QA — S6ERQA-v0 / D-062
- AŞAMA 7A ✅ EED-v0 / D-063
- AŞAMA 7B ✅ TECP-v0 / D-064
- AŞAMA 7C ✅ DECP-v0 / D-065
- AŞAMA 7D ✅ TEIP-v0 / D-066
- AŞAMA 7E ✅ TEPM-v0 / D-067
- AŞAMA 8A ✅ UXIA-v0 / D-068
- AŞAMA 8B ✅ THUX-v0 / D-069
- AŞAMA 8C ✅ TRUX-v0 / D-070
- AŞAMA 8D ✅ ASUX-v0 / D-071
- AŞAMA 8E ✅ SPWX-v0 / D-072
- AŞAMA 8F ✅ VDSX-v0 / D-073
- AŞAMA 8G ✅ WFPX-v0 / D-074
- **AŞAMA 8 ✅ TAMAMLANDI**
- AŞAMA 9A ✅ AMTS-v0 / D-075
- AŞAMA 9B ✅ LFPS-v0 / D-076
- AŞAMA 9C ✅ DDM-v0 / D-077
- AŞAMA 9D ✅ MSBX-v0 / D-078
- AŞAMA 9E ✅ AIAX-v0 / D-079
- AŞAMA 9F ✅ TVSX-v0 / D-081
- **AŞAMA 9 ✅ TAMAMLANDI**
- AŞAMA 10A ✅ MPSX-v0 / D-082
- AŞAMA 10B ✅ NSHX-v0 / D-083
- AŞAMA 10C ✅ DSIX-v0 / D-084
- AŞAMA 10D ✅ LDBX-v0 / D-085
- AŞAMA 10E ✅ APHX-v0 / D-086
- **AŞAMA 10 ✅ TAMAMLANDI**
- AŞAMA 11A ✅ TDYX-v0 / D-087
- AŞAMA 11B ✅ RNRX-v0 / D-088
- AŞAMA 11C ✅ SESX-v0 / D-089
- AŞAMA 11D ✅ DMAX-v0 / D-090
- AŞAMA 11E ✅ EODX-v0 / D-091
- **AŞAMA 11 TAMAMLANDI**
- AŞAMA 12A ✅ MSTX-v0 / D-092
- AŞAMA 12B ✅ PRQX-v0 / D-093
- AŞAMA 12C 🟡 active-not-executed
- 12D–20 ⬜

# 22. Current exact state — en kritik takeover bilgisi

**Son tamamlanan numaralı adım:** `12B — Prerequisite Engine`  
**Final:** `PRQX-v0 — Prerequisite Engine` / D-093  
**Canonical:** `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md` + `arch/12b_prerequisite_engine/`

**AŞAMA 7:** ✅ TAMAMLANDI  
**AŞAMA 8A:** ✅ TAMAMLANDI  
**AŞAMA 8B:** ✅ TAMAMLANDI  
**AŞAMA 8C:** ✅ TAMAMLANDI  
**AŞAMA 8D:** ✅ TAMAMLANDI  
**AŞAMA 8E:** ✅ TAMAMLANDI  
**AŞAMA 8F:** ✅ TAMAMLANDI  
**AŞAMA 8G:** ✅ TAMAMLANDI  
**AŞAMA 8:** ✅ TAMAMLANDI  
**AŞAMA 9A:** ✅ TAMAMLANDI  
**AŞAMA 9B:** ✅ TAMAMLANDI  
**AŞAMA 9C:** ✅ TAMAMLANDI  
**AŞAMA 9D:** ✅ TAMAMLANDI  
**AŞAMA 9E:** ✅ TAMAMLANDI  
**AŞAMA 9F:** ✅ TAMAMLANDI  
**AŞAMA 9:** ✅ TAMAMLANDI  
**AŞAMA 10A:** ✅ TAMAMLANDI  
**AŞAMA 10B:** ✅ TAMAMLANDI  
**AŞAMA 10C:** ✅ TAMAMLANDI  
**AŞAMA 10D:** ✅ TAMAMLANDI  
**AŞAMA 10E:** ✅ TAMAMLANDI  
**AŞAMA 10:** ✅ TAMAMLANDI  
**AŞAMA 11A:** ✅ TAMAMLANDI  
**AŞAMA 11B:** ✅ TAMAMLANDI  
**AŞAMA 11C:** ✅ TAMAMLANDI  
**AŞAMA 11D:** ✅ TAMAMLANDI  
**AŞAMA 11E:** ✅ TAMAMLANDI  
**AŞAMA 11:** ✅ TAMAMLANDI  
**AŞAMA 12A:** ✅ TAMAMLANDI  
**AŞAMA 12B:** ✅ TAMAMLANDI  
**Aktif adım:** `12C — Planner Engine v1`  
**Durum:** **HENÜZ YÜRÜTÜLMEDİ**

8C focused günlük çalışma akışını kilitledi: Task Runner bir execution surface'tir; working session emergent ve ungraded'dır; tek shared focused-flow frame hem Task Runner hem assessment session tarafından devralınır ve assessment interior 8D'ye aittir. Entry/resume revalidation deterministiktir; assistance non-punitive ve talep üzerine escalate eder; solution exposure sonrası same-item mastery path yoktur ve recheck scheduling planner-owned kalır; provenance sorulur, çıkarsanmaz; in-flight run replan'dan korunur; AI evaluator yoksa attempt `evaluation_pending` olur ve evidence yazılmaz.

12C için:

```text
fresh 12C PRE-STEP GitHub refresh
→ user explicit approval verification
→ 12C execution
→ independent QA
→ D-050 POST sync
→ repo-wide stale-reference audit
```

# 23. Tamamlanan 6B'nin görevi ve final çıktısı

Ana soru:

> **GNS-v0 standardını 23 route family'nin tamamında tutarlı biçimde uygulayacak ortak decomposition authoring blueprint'i ve çıktı contract'ı nasıl olmalı?**

6B gerçek full route node listesini yazmadı. 6C–6F'nin aynı kuralla ayrıntılı harita üretmesini sağlayan blueprint'i kilitledi.

Kesinleştirilecek:
- Domain/Module/Topic decomposition row yapısı,
- Skill candidate authoring template,
- Learning Objective candidate template,
- canonical logical ID candidate fields,
- parent/placement/shared placements,
- independent evidence path,
- prerequisite boundary,
- remediation boundary,
- reuse boundary,
- shared-vs-specific rationale,
- evidence/depth expectation,
- retention/freshness note,
- source/provenance,
- lifecycle status,
- GNS granularity review code,
- FBB seed mapping,
- duplicate resolver workflow,
- cross-domain canonical Skill reuse workflow,
- prerequisite candidate declaration (`hard|soft` + reason),
- requirement/criticality metadata,
- professional/project/capstone tags,
- technology freshness/provenance capture,
- 6C–6F common output format,
- machine-readable/QA-ready package expectations,
- unresolved `needs_granularity_review` handling.

6A'nın 6B handoff alanları canonical olarak şunları içerir:

```text
entity_type
logical_id_candidate
display_name
semantic_statement
primary_placement
linked/shared placements
parent organization entity
candidate Skill owner / Objective owner
independent evidence path
prerequisite boundary
remediation boundary
reuse boundary
shared_vs_specific rationale
evidence/depth expectation
retention/freshness note
source/provenance
lifecycle_status
granularity_review_code
seed_mapping_if_any
```

6B'nin final canonical çıktısı `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` içindeki **FRDB-v0** ve D-056'dır. Blueprint 23 route family'yi 6C–6F paketlerine bağlar; ortak authoring satırlarını, duplicate/prerequisite/review iş akışını, çapraz paket reconciliation'ı ve 6C–6H handoff/QA kapılarını tanımlar.

6B physical DB schema değildir; 9C'ye kadar implementation storage formatı kilitlenmez.

## 6B Research/Coding/Test kararı — uygulanmış sonuç

Fresh PRE değerlendirmesinde 6B internal authoring-contract formalizasyonu olduğu için separate external Research AI ve runtime Coding AI gerekmedi. Static contract QA uygulandı. 6H external independent Research AI zorunluluğu aynen korunur.

6B'de full professional coverage araştırması yapıldı diye 6H görevini iptal etme.

---

# 24. AŞAMA 6'nın kalan adımları

## 6C — Foundations detailed map ✅ FDM-v0 / D-057

Canonical summary: `docs/FOUNDATIONS_DETAILED_MAP.md`.

Canonical dataset: `curriculum/decomposition/6c_foundations/`.

Final: 5 Domain / 14 Module / 46 Topic / 132 Skill / 137 Objective / 145 TopicSkillLink / 200 prerequisite edge; FBB 41/47 mapping complete; hard graph DAG; 0 blocking review. External validation 6H'ye pending.

Granular decomposition:
- Technical English,
- Python,
- C,
- Linux/Git/Shell,
- DS&A foundations.

FBB 41 Skill / 47 Objective authoring seed'leri GNS-v0 ile tek tek ratify/split/normalize edildi.

Python yalnız broad “Python Foundations” kalmayacak. En az değerlendirilmesi gereken family'ler:
- syntax/values/types,
- variables/assignment,
- operators/expressions,
- I/O,
- conditionals,
- loops,
- strings,
- lists/tuples/sets/dicts,
- functions/parameters/return,
- scope,
- modules/imports,
- files/paths,
- exceptions,
- debugging,
- comprehensions/iteration model,
- classes/objects where required,
- typing,
- testing,
- venv/dependency management,
- packaging,
- CLI/automation,
- subprocess/OS interaction,
- networking basics,
- async/concurrency,
- multiprocessing,
- profiling/performance,
- data manipulation,
- NumPy/tensor/PyTorch-facing Python,
- infra/benchmark scripting.

Bu liste 6C package'ında capability rows'a dönüştürüldü; 6H external Research QA bulguları explicit correction/migration gerektirebilir.

## 6D — Systems detailed map ✅

Canonical: `docs/SYSTEMS_DETAILED_MAP.md` + `curriculum/decomposition/6d_systems/` — SDM-v0 / D-058.

- Modern C++
- Computer Architecture
- OS/Memory
- Concurrency/Parallelism
- Networking
- Distributed Systems
- Storage/DB
- Containers/Cloud/Observability
- Performance/Profiling

Bu liste 6D package'ında 192 Skill / 207 Objective satırına dönüştürüldü; 6H external Research QA bulguları explicit correction/migration gerektirebilir.

## 6E — GPU / ML / Inference detailed map

- GPU Architecture
- CUDA
- Triton
- ML/Transformer support depth
- LLM inference internals
- serving engines
- KV cache
- batching
- scheduling
- quantization
- Multi-GPU
- NCCL
- RDMA
- AI/GPU Infrastructure

## 6F — Professional engineering / project map

- Git/branch/PR/code review,
- testing,
- build systems,
- debugging,
- profiling,
- docs,
- issue decomposition,
- design docs,
- reproducible benchmarks,
- observability,
- incident/postmortem,
- security/reliability basics,
- OSS workflow,
- integrated projects,
- capstone capability decomposition.

## 6G — Weakness localization + remediation mapping

Amaç broad `Python weak` yerine exact weakness state → targeted reteach/practice/retest mapping.

Örn:

```text
Python overall: learning
  conditionals: mastered
  loops:
    for_iteration: stable
    while_termination: weak
```

Planner yalnız eksik capability'yi hedeflemeli; broad domain reset guard.

## 6H — Coverage / prerequisite / external Research QA

**Independent Research AI zorunlu.**

Audit:
- missing domain/capability,
- hidden prerequisites,
- duplicate Skills,
- dead-end/cycle,
- cross-domain reuse,
- current industry/tool relevance,
- curriculum professional-readiness coverage.

Research output automatic decision değildir; manager değerlendirip canonical map'e uygular.

---

# 25. AŞAMA 7–20 — yapılacakların tam current planı

## AŞAMA 7 — English parallel line
- 7A Başlangıç ölçümü
- 7B A1/A2/B1/B2+ technical targets
- 7C Daily English component
- 7D Technical integration
- 7E English mastery

## AŞAMA 8 — UX / Screens
- 8A Information architecture
- 8B Home/Today
- 8C Daily study flow
- 8D Assessment UX
- 8E Skill/progress/weakness UX
- 8F Design system
- 8G Wireframe/prototype

## AŞAMA 9 — Technical architecture / data
- 9A Mobile technology selection
- 9B Local-first persistence
- 9C Domain data model
- 9D Service boundaries
- 9E AI integration architecture
- 9F Test strategy / performance budgets

9C must cover granular Skill/Objective state, resource versions/lifecycle, AI validation records/use ceilings, curriculum graph identities/versions, exposure, years-long history, migrations.

## AŞAMA 10 — Mobile skeleton
- 10A Project setup
- 10B Navigation
- 10C Design system implementation
- 10D Local DB
- 10E App health

## AŞAMA 11 — Daily Learning MVP
- 11A Today
- 11B Task Runner
- 11C Session state
- 11D Daily micro assessment
- 11E End-of-day

## AŞAMA 12 — Mastery + Planner implementation
- 12A Mastery Engine v1
- 12B Prerequisite Engine
- 12C Planner Engine v1
- 12D Replan
- 12E Reason codes
- 12F Virtual-user tests

## AŞAMA 13 — Assessment + Retention + Remediation implementation
- 13A Weekly assessment
- 13B Monthly assessment
- 13C Spaced repetition
- 13D Remediation Engine
- 13E Program-change report

## AŞAMA 14 — AI Tutor / intelligent evaluation
- 14A Tutor behavior contract
- 14B Wrong-answer analysis
- 14C Alternative explanation
- 14D Code evaluation
- 14E AI-generated code comprehension check
- 14F Open-ended response evaluation
- 14G Provider abstraction/fallback

## AŞAMA 15 — First 8–12 week production content
- 15A Computer/Programming Fundamentals
- 15B Python Foundations
- 15C C Foundations
- 15D Memory Foundations
- 15E Linux/Git/Shell Foundations
- 15F English A0→A1/A2 start package
- 15G Assessment content
- 15H Content QA

## AŞAMA 16 — Analytics / history / settings
- 16A Skill analytics
- 16B Learning history
- 16C Progress/weakness rules
- 16D Settings
- 16E Notifications

## AŞAMA 17 — UI/UX polish
- 17A Visual polish
- 17B Motion
- 17C Usability
- 17D Accessibility

## AŞAMA 18 — Pilot / calibration / QA
- 18A Pilot start
- 18B Planner observation
- 18C Mastery calibration
- 18D Assessment/item/exposure/validator calibration
- 18E Technical/performance QA
- 18F Fix loop

## AŞAMA 19 — Release APK
- 19A Release preparation
- 19B Data reliability
- 19C Final regression
- 19D APK / real device
- 19E Release docs

V1 release != professional curriculum completion.

## AŞAMA 20 — Long-term professional curriculum / career
- 20A Modern C++ + Advanced Python + Professional Tooling
- 20B Systems + Architecture + Performance
- 20C Networking + Distributed + Storage
- 20D GPU Architecture + CUDA
- 20E Triton + ML/Transformer + LLM Inference
- 20F Serving Engines + KV / Batching / Scheduling / Quantization
- 20G Multi-GPU / NCCL / RDMA / AI Infrastructure
- 20H Open Source + Engineering Practice + Career Readiness
- 20I Continuous Curriculum QA + large integrated projects + professional capstones

---

# 26. Kod başladığında branch/PR davranışı

Canonical AI workflow tavsiyesi:

- `main` = accepted stable state.
- Feature/fix branch = coding work.
- Important feature PR'larında acceptance criteria.
- Coding AI kendi işini final onaylamaz.
- Independent Test/QA PASS olmadan önemli work main'e merge edilmez.
- Kişisel proje olduğu için gereksiz enterprise bureaucracy kurma; rollback/test bağımsızlığını koru.

---

# 27. Tasarımda sessizce değiştirilemeyecek invariants

1. Time != mastery/readiness.
2. Completion != mastery.
3. Streak != mastery.
4. Self-confidence != mastery.
5. LLM != curriculum/planner/mastery source of truth.
6. Domain/Topic completion != canonical Skill mastery.
7. Prerequisite runtime main unit = Skill.
8. Missing prerequisite only dependent branch'i bloke eder.
9. English global technical hard gate değildir.
10. Unknown English hidden technical prerequisite olamaz.
11. A single random failure broad regression yaratmaz.
12. `review_due` forgetting değildir.
13. Absence failure/debt değildir.
14. Daily capacity hard budget.
15. Deferred work stale task debt'e dönüşmez.
16. Same semantic Skill farklı Topics'te clone'lanmaz.
17. Integrated task PASS bütün tagged Objectives'e otomatik yayılmaz.
18. AI-generated content self-certified trusted olamaz.
19. Near duplicate independent evidence diversity sağlamaz.
20. Translation new independent family değildir.
21. Published semantic graph/resource state silent overwrite edilmez.
22. Split/merge learner'a bedava mastery vermez.
23. Tool/vendor names stable concepts'in yerini almaz.
24. Performance full graph scan/UI-thread heavy work üzerine kurulmaz.
25. No invented “scientific” thresholds without accepted source/calibration.
26. D-043 noncanonicaldır.
27. Every numbered step fresh PRE and full POST memory sync.
28. Completion claim POST bitmeden yapılmaz.

---

# 28. Geçmişte yakalanan hatalar — tekrar edilmemeli

## Hata 1 — Research AI sözü tutulmadan step kapatma

2E manager kendi web research'ünü separate Research AI yerine saymaya kalktı. Kullanıcı düzeltti. D-030 ile step re-open edildi. Explicit independent research requirement varsa gerçekten ayrı role git.

## Hata 2 — `PROJECT_CONTEXT.md` stale kaldı

Repo bir noktada 4B state'i taşıdı. D-050 bu yüzden permanent hygiene rule oldu.

## Hata 3 — Future stage reindex drift

D-044 ile yeni AŞAMA 6 eklendiğinde old future-stage refs repo-wide düzeltilmek zorunda kaldı. Reindex yaparsan bütün repo search zorunlu.

## Hata 4 — START_HERE duplicate current-state drift

6A POST audit'te bir bölüm doğruyken üst stage-map stale kaldı. Living docs içindeki birden fazla current state snippet'i de taranmalı.

## Hata 5 — İlk mastery candidate sahte hassas weighted modeldi

D-029 replaced by GRE-v0. Yeni numeric weights/percentages/thresholds ancak gerçek gerekçe/calibration ile.

## Hata 6 — FBB structural assumptions

5D initial graph audit TopicSkillLink eksikliği, invalid reason kind ve hidden prerequisites buldu. Organization/placement isimlerinden graph ilişkisi varsayma; explicit relation üret.

---

# 29. Canonical dosya okuma sırası — takeover için

İlk takeover'da **tüm Markdown dosyaları okunacak**. Aşağıdaki sıra özellikle önemlidir:

1. `AGENTS.md`
2. `docs/LOCAL_MANAGER_HANDOFF.md`
3. `docs/START_HERE.md`
4. `docs/PROJECT_MEMORY_PROTOCOL.md`
5. `PROJECT_CONTEXT.md`
6. `docs/HANDOFF_STATE.md`
7. `docs/EXECUTION_INDEX.md`
8. `docs/STEP_STATUS.md`
9. `docs/DECISIONS.md`
10. `docs/MASTER_PLAN.md`
11. `docs/PRODUCT_REQUIREMENTS.md`
12. `docs/PRODUCT_VISION.md`
13. `docs/V1_SCOPE.md`
14. `docs/V1_SUCCESS_CRITERIA.md`
15. `docs/NON_GOALS.md`
16. `docs/PROFESSIONAL_READINESS_TARGET.md`
17. `docs/CURRICULUM_DOMAIN_MAP.md`
18. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
19. `docs/V1_FOUNDATION_BACKBONE.md`
20. `docs/GRAPH_ARCHITECTURE_QA.md`
21. `docs/GRANULARITY_NAMING_STANDARD.md`
22. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
23. `docs/CURRICULUM.md`
24. `docs/LEARNING_ENGINE_SPEC.md`
25. `docs/LEARNING_BEHAVIOR_RULES.md`
26. `docs/TOPIC_STATE_MACHINE.md`
27. `docs/MASTERY_SIGNALS_SPEC.md`
28. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
29. `docs/MASTERY_FORMULA_V0.md`
30. `docs/2E_RESEARCH_VALIDATION.md`
31. `docs/RETENTION_FORGETTING_SPEC.md`
32. `docs/2F_RESEARCH_BRIEF.md`
33. `docs/2F_RESEARCH_VALIDATION.md`
34. `docs/ADAPTIVE_PLANNER_SPEC.md`
35. `docs/TASK_TAXONOMY_SPEC.md`
36. `docs/PRIORITY_POLICY_SPEC.md`
37. `docs/PREREQUISITE_POLICY_SPEC.md`
38. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
39. `docs/MISSED_DAY_RECOVERY_SPEC.md`
40. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
41. `docs/PLANNER_SIMULATION_SUITE.md`
42. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
43. `docs/WEEKLY_ASSESSMENT_SPEC.md`
44. `docs/MONTHLY_ASSESSMENT_SPEC.md`
45. `docs/QUESTION_BANK_SPEC.md`
46. `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`
47. `docs/ENGLISH_FOUNDATION_RULES.md`
48. `docs/AI_AGENT_WORKFLOW.md`
49. `docs/STAGE_REINDEX_MAP.md`
50. `docs/PROJECT_MASTER_CONTEXT.md`
51. `docs/PROGRESS_LOG.md`
52. `README.md`
53. `docs/RESEARCH_NOTES.md`
54. `docs/LEARNING_ENGINE.md` — historical pointer olarak oku
55. `docs/ENGLISH_TRACK.md` — noncanonical seed olarak oku

Repo takeover sırasında mevcut inventory bu listeden farklıysa yeni dosyaları da oku. Bu liste repo scan'in yerine geçmez.

---

# 30. Local manager'ın ilk cevap vermeden önce doğrulaması gereken sorular

Agent kendi kendine şu soruları cevaplayabilmelidir:

1. Product'un gerçek amacı nedir?
2. Neden Day X/progress % kullanılmıyor?
3. Professional readiness nasıl tanımlanıyor?
4. V1 ile full curriculum farkı nedir?
5. 23 route family hangileri?
6. Domain/Module/Topic ile Skill/Objective farkı nedir?
7. Mastery truth hangi seviyede?
8. GRE/RVR/PRG/PBR ne yapıyor?
9. AI assistance evidence neden H0'dan farklı?
10. Weekly/monthly assessment neden scorecard değil?
11. QAB lifecycle/use ceiling nedir?
12. AIV neden self-validation/majority vote kullanmıyor?
13. English neden global hard gate değil?
14. GNS-v0 new Skill kararını nasıl veriyor?
15. FBB neden learner-published değil?
16. 5D hangi structural sorunları düzeltti?
17. D-050 POST seti hangi dosyaları kapsıyor?
18. D-043 neden kullanılmamalı?
19. Current exact active step nedir?
20. FRDB-v0'nun scope'u nedir ve neyi özellikle yapmaz?
21. 6H'de neden independent Research AI zorunlu?
22. Coding başladığında QA/branch davranışı ne?

Bu sorulardan biri belirsizse, aktif numaralı adıma başlamadan ilgili canonical dosya yeniden okunmalıdır.

---

# 31. Manager transition — D-055

Kullanıcı yönetici rolünü local çalışan agent'a devretme kararı verdi.

Bu geçişin anlamı:
- ana manager/koordinatör artık local agent olabilir,
- mevcut ürün/curriculum/mastery/planner/assessment kararları **değişmez**,
- GitHub durable source of truth olmaya devam eder,
- local agent da D-024/D-027/D-050 protokolüne aynen uyar,
- Research/Coding/Test bağımsız rol ayrımı aynen korunur,
- local manager terminal/repo erişimini kullanarak daha güçlü repo-wide audit yapabilir,
- bu transition numaralı 6B adımı değildi ve kendi başına 6B'yi yürütmedi.

D-055 kalıcı karar olarak `docs/DECISIONS.md` içine sync edilmiştir. Daha sonraki kullanıcı onaylı çalışma 6B'yi D-056 / FRDB-v0 ile tamamlamıştır.

---

# 32. Local agent'a verilecek kısa takeover komutu

Kullanıcı yeni local-manager oturumunda şu durable promptu kullanabilir:

> **Bu reponun ana proje yöneticisisin. Önce root `AGENTS.md`, `vault/agent/SESSION_START.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `docs/START_HERE.md` ve `docs/PROJECT_MEMORY_PROTOCOL.md` dosyalarını oku. Current execution'ı `EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN` ile fresh çapraz doğrula. Canonical decisions/specs ile historical/noncanonical notları ayır. Current active numbered step'i fresh PRE-STEP ve kullanıcı açık onayı olmadan yürütme. Her numbered step sonunda D-050 living-memory sync + repo-wide stale-reference audit uygula.**

---

# 33. Final takeover state

Bu living handoff'un current canonical execution özeti:

```text
AŞAMA 1–6 ✅
AŞAMA 7 ✅ — EED-v0 → TECP-v0 → DECP-v0 → TEIP-v0 → TEPM-v0
8A ✅ UXIA-v0 / D-068
8B ✅ THUX-v0 / D-069
8C ✅ TRUX-v0 / D-070
8D ✅ ASUX-v0 / D-071
8E ✅ SPWX-v0 / D-072
8F ✅ VDSX-v0 / D-073
8G ✅ WFPX-v0 / D-074
AŞAMA 8 ✅ TAMAMLANDI
9A ✅ AMTS-v0 / D-075
9B ✅ LFPS-v0 / D-076
9C ✅ DDM-v0 / D-077
9D ✅ MSBX-v0 / D-078
9E ✅ AIAX-v0 / D-079
9F ✅ TVSX-v0 / D-081
AŞAMA 9 ✅ TAMAMLANDI
10A ✅ MPSX-v0 / D-082
10B ✅ NSHX-v0 / D-083
10C ✅ DSIX-v0 / D-084
10D ✅ LDBX-v0 / D-085
10E ✅ APHX-v0 / D-086
AŞAMA 10 ✅ TAMAMLANDI
11A ✅ TDYX-v0 / D-087
11B ✅ RNRX-v0 / D-088
11C ✅ SESX-v0 / D-089
11D ✅ DMAX-v0 / D-090
11E ✅ EODX-v0 / D-091
AŞAMA 11 ✅ TAMAMLANDI
12A ✅ MSTX-v0 / D-092
12B ✅ PRQX-v0 / D-093
12C 🟡 active-not-executed
12D–20 ⬜
```

10D final:
- storage engine mimarinin yasakladığını reddeder; her ret denenerek kanıtlanır,
- ilk taslak `DDM-v0`den sapmıştı ve kendi testleri geçiyordu; kontratı okuyan validator yakaladı,
- API çözülmüş jar'dan `javap` ile okundu,
- 11 curriculum / 13 truth / 8 projection tablosu; user→curriculum FK yok,
- abort eden UPDATE/DELETE trigger'ları; CHECK değer kümeleri; yapısal pinning,
- dakika offset, tam dakika olmayan reddedilir; global truth sequence watermark,
- migration ileri-yönlü, transaction'lı, dolu fixture'a karşı içerikle test edildi,
- adapter kolonları SQLite'tan okur; arm64-v8a native kütüphane APK'da doğrulandı,
- 22 T2 check, mutation 9/9; independent QA 163/163 PASS; 30/30 sweep PASS.

10C final:
- tasarım sistemi kanonik state'in iddia etmediği severity'yi ekleyemez,
- token'lar `core-presentation`da düz veri; kontrast cihazsız JVM testinde yeniden hesaplanır,
- palet birebir kopyalandı, revize edilmedi; kayıtlı minimumlar token'lardan yeniden türetildi ve tuttu,
- `LearningTone`da fault değeri yok → learning state'e fault tonu yazılamaz; Material `error` yalnız `system_fault`,
- sekiz state'in her birinin tam bir tonu; üçü bilinçle nötr,
- attention grubu tonu yükseltmez (adı konmuş, test edilen fonksiyon),
- 48dp modifier, %200 metin, metin olarak verilen state, locale-naive casing yok,
- dynamic colour scan'i kendi yorumumuzu yakaladı; gate gevşetilmedi,
- independent 10C QA 146/146 PASS, mutation-tested 8/8; 29/29 sweep PASS.

10B final:
- navigasyon kuralları `core-presentation`da saf fonksiyonlar, UI toolkit'inde değil,
- dört destination kabul edilmiş sırada; enum sırası kanonik, senkron tutulacak ikinci liste yok,
- on bir yasak top-level id'nin hiçbiri destination değil,
- kanonik entity başına tek surface objesi; çelişkili Skill detail temsil edilemez,
- contextual edge kümesi kapalı ve `ia.yaml` ile karşılaştırılıyor,
- focused flow shell'i askıya alır; `showsShell`/`requiresSafeExit` türetilir, çıkışsız gizli shell inşa edilemez,
- dönüş kuralı deterministik; bayat origin öğrenciyi mahsur bırakmaz,
- window class'lar `WFPX-v0` breakpoint'lerinden core'da hesaplanır ve yalnız çizimi değiştirir,
- metin etiketi + `stateDescription` + `traversalIndex`; ikon tek anlam taşıyıcısı değil,
- 4 run çalıştırıldı; adaptörsüz build hâlâ geçiyor,
- independent 10B QA 104/104 PASS, mutation-tested 8/8; 28/28 sweep PASS.

10A final:
- repodaki ilk çalıştırılabilir çıktı; çalıştırılmamış hiçbir şey iddia edilmez,
- build iki yanlış pin'i yakaladı: Gradle 9.5 dağıtımı yok (→ 9.7.1), AGP 9+ `kotlin.android`ı reddediyor,
- toolchain 2026-08-31'de doğrulandı; minSdk 26 / targetSdk 36 / compileSdk 37,
- hedef cihaz kaydedildi: Poco M6 Pro / 2312FPCA6G / Android 16 (API 36); `AMTS-v0` §8.1 kapandı,
- `AMTS-v0` §9'un altı maddesi kapandı; 26'da compatibility library gerekmiyor,
- on modül `android/` altında, bağımlılıklar `boundaries.yaml`a eşit ve makine kontrol ediyor,
- yalnız iki `app-*` modülü Android modülü; T2 ve adaptör doğrulaması cihaz dışında koşabiliyor,
- `verifyModuleBoundaries` build'i düşürüyor, mutation-tested; cycle raporlaması o sırada düzeltildi,
- adaptörsüz build geçiyor (9 modül) ve `NullEvaluator` sevk ediliyor — V1 kriteri 8 wiring,
- 4 run kaydedildi (T3, T1, iki build); APK üretildi,
- DI/ORM/HTTP client/architecture-rule library yok; saat tek yerde; dynamic colour yok; key repoya giremiyor,
- independent 10A QA 127/127 PASS, mutation-tested 7/7; 27/27 sweep PASS.

9F final:
- üzerine hiçbir şeyin düşmediği bir garanti bir tercihtir; her invariant'ın adı konmuş sahibi vardır,
- 6 katman; cihaz katmanı en küçüğü, T3 build time'da düşer,
- coverage yüzdesi gate değil; gate invariant coverage ve sahipsiz invariant tek başına bloklar,
- negatif doğrulama zorunlu: yasaklanan denenir ve reddedilmesi şart koşulur,
- append-only schema seviyesinde; migration dolu fixture'lara karşı, evidence/exposure/provenance birebir korunur,
- hiçbir check canlı AI provider çağırmaz; 7 sonuç kayıtlı yanıtlarla, payload'da history/mastery/plan/profile yok,
- null-evaluator yolu `ai-adapter` olmadan build alınarak doğrulanır (V1 kriteri 8),
- determinizm enjekte saatle egzersiz edilir; flaky check düşmüş check'tir, retry-to-green yasak,
- 6 severity sınıfı; `evidence_correctness` her zaman bloklar,
- 11 koşullu release gate; 10 V1 kriteri eşlenir ve `validate_*.py` glob'unun tamamı geçmeli,
- 66 kayıtlı invariant, hepsi upstream kontratta gerçekten var olan anahtarlar,
- independent 9F QA 288/288 PASS, mutation-tested 8/8; 26/26 sweep PASS.

9E final:
- AI bir port arkasındaki yardımcıdır ve otorite değildir; AI önerir, deterministic engine'ler karar verir,
- `LEARNING_BEHAVIOR_RULES` §17 model seçimi ve §18 güvenlik/proxy/backend kararları burada kapatıldı,
- evaluator çıktısı schema-constrained; schema'ya uymayan yanıt hüküm değil hatadır; serbest metin ayrıştırma yasak,
- kalibre edilmemiş LLM değerlendirmesi `provisional`; `verified` deterministik yol ister,
- 7 sonuçlu taksonomi; **refusal yanlış cevap değildir**; her yanıtsızlık `evaluation_pending`, evidence yazmaz,
- timeout bütçesi uçtan uca ve retry'ları kapsar; sessiz arka plan retry'ı yok,
- model adı konfigürasyonda, core'da değil; provider-independent adapter + router; currency 10A/14'te doğrulanacak,
- deterministik iş asla AI çağırmaz; maliyet evidence kuralını zayıflatamaz,
- APK'da hardcoded/paylaşılan key yok; V1'de backend proxy yok; key platform secure storage'da,
- yalnız asgari attempt içeriği cihazdan çıkar; history/mastery/plan/profile asla gitmez,
- generated item untrusted girer; generator ≠ validator; her evidence satırında evaluator_ref,
- independent 9E QA 86/86 PASS (mutation-tested).

9D final:
- sınırlar garantileri yapısal yapar; core'un AI'sız çalışması dependency kuralıdır,
- 10 modül, içe-doğru bağımlılık, asiklik graf; `core-*` → `data-*`/`ai-*`/`app-*` yasak,
- `app-wiring` composition root; domain logic içermez,
- 4 port: Persistence, Content, Clock, Evaluator — hepsi core'da ve platform tipsiz,
- saat bir port; core sistem saatini okumaz,
- core'da rastgelelik yok; beraberlik beyan edilmiş total ordering ile,
- null evaluator ürünle sevk edilir; app `ai-adapter` olmadan build/run olur,
- engine başına tek state ailesi; planner learner state yazmaz,
- transaction `core-application`da; presentation projection `core-presentation`da,
- independent 9D QA 93/93 PASS (graf hesaplanarak doğrulandı).

9C final:
- schema mimariyi uygular; LFPS-v0 garantileri yapısal,
- üç store bölgesi; curriculum→user foreign key yok,
- `(logical_id, version)` composite kimlik; her user referansı version taşır,
- truth tablolarında UPDATE/DELETE yolu yok; düzeltme append edilen disposition,
- dört bağımsız evidence ekseni ayrı kolon,
- her timestamp'te instant + study day + UTC offset,
- her projection satırında policy version + truth watermark,
- SPWX dört ekseni storage'da da ayrı; exposure seçim yolunda indeksli,
- physical schema library-neutral; core'da platform tipi yok,
- independent 9C QA 114/114 PASS.

9B final:
- kanıt source of truth; öğrenci state'i yeniden hesaplanabilir projeksiyon,
- truth kayıtları append-only; geçersiz kanıt silinmez işaretlenir,
- storage engine SQLite; ORM library 10A'ya bırakıldı,
- persistence interface'leri core-owned; core signature'ında storage/Android/fs tipi yok,
- curriculum ve user state ayrı versiyonlanır; curriculum güncellemesi learner state değiştiremez,
- exposure kayıtları kalıcı ve first-class; kaybı veri kaybıdır,
- bir eylem bir transaction; `evaluation_pending` evidence yazmaz,
- migration forward-only ve kanıtı yok etmez; restore atomik ve doğrulanmış,
- bozulma `data_recovery_required`; sessiz reset yasak; tutarsız projeksiyon recomputation ile onarılır,
- V1'de kanıt budanmaz,
- independent 9B QA 100/100 PASS.

9A final:
- teknoloji seçimi kontratlara hizmet eder; çelişkide kontrat kazanır,
- Android native; V1'de cross-platform UI katmanı yok,
- taşınabilirlik hedge'i UI değil domain core,
- Kotlin + Jetpack Compose,
- Material 3 yalnız substrate; **dynamic colour kapalı**,
- domain core saf Kotlin: Android/UI/network/AI bağımlılığı yasak,
- 3 window class WFPX-v0 ile birebir,
- accessibility gereksinimleri platform mekanizmalarına eşlendi,
- default-locale case transform yasak; Türkçe casing round-trip eder,
- `minSdk` politika; API 26 working default, 10A'da cihazda doğrulanacak,
- kurulabilir APK + gerçek cihaz QA zorunlu,
- 6 maddelik verification list 10A'ya devredildi,
- independent 9A QA 100/100 PASS.

8G final:
- geometry kabul edilmiş anlamı yerleştirir; semantic/state/label/tone/ownership değiştirmez,
- 3 window class; destination kimliği ve sırası her sınıfta değişmez,
- 6 surface region geometry'si sahibi spec'lere karşı çapraz doğrulandı,
- `skill_detail` chip + dört ekseni birlikte gösterir; `progress_overview` yalnız envanter,
- focused-flow exit/pause her sınıfta 48dp sabit; countdown yok,
- palet tema başına ölçüldü; 52 kontrast çifti geçti (min 6.08 / 3.79 / 6.06),
- `attention` menekşe, kırmızı yalnız `system_fault` — traffic-light rampası yok,
- kontrast her çalıştırmada hex'ten yeniden hesaplanır,
- `prototype.html` bağlayıcı değildir; teknoloji seçimi yapmaz,
- independent 8G QA 222/222 PASS.

8F final:
- design system expression layer'dır; `visual_severity <= canonical_severity`,
- tam 6 tone; tone anlamdan atanır, histen değil,
- 46 surface + 8 Skill + 6 Topic + 4 qualifier state eksiksiz eşlendi,
- `system_fault` yalnız gerçek arızaya izinli; hiçbir learning state alarm tonu almaz,
- attention grubu tone yükseltmez; `confirmed_review_due` ve `weakening` neutral,
- WCAG çapalı kontrast tema başına ölçülür; renk asla tek taşıyıcı değil,
- en az 48dp hedef, %200 metin desteği, Türkçe casing koruması,
- motion ikna edici değil; countdown, task-completion ödülü, decay ve streak yasak,
- 18 component; hiçbiri surface/state icat edemez,
- progress-bar yalnız bounded factual konum için,
- somut hex 8G/10'da ölçülerek üretilir,
- independent 8F QA 121/121 PASS.

8E final:
- Progress canonical evidence state'in projection'ıdır; mastery engine/score/gradebook değildir,
- TEPM-v0'nin 8 derived presentation state'i ve precedence'ı bütün Skill'lere genelleştirildi; ikinci vokabüler yok,
- `at_risk` attention qualifier'dır; dokuzuncu primary state değildir ve düşürülmez,
- multi-axis truth sıralanır, çökertilmez; `skill_detail` her ekseni incelenebilir tutar,
- 8 Skill + 6 Topic Türkçe etiketi kilitlendi; internal ID'ler değişmedi,
- Topic state derived orchestration'dır; prerequisite iddiası, Skill ortalaması ve yüzde yok,
- Progress sayabilir fakat puanlayamaz; count yalnız etiketli inventory'dir,
- yalnız `supported`/`confirmed` weakness gösterilir; AI hypothesis confirmed olamaz,
- `remediation_task_completed != remediation_closed`,
- learning history streak calendar değildir; assessment_report session'ları score'a toplayamaz,
- independent 8E QA 128/128 PASS.

8D final:
- assessment session bir evidence-collection workflow'udur; gradebook veya score-based mastery authority değildir,
- üç scope için tek interior; scope yalnız displayed context ve ek evidence ağırlığı yok,
- submission birimi atomic evidence boundary; bölünmez ve kısmen puanlanmaz,
- submitted boundary frozen; unsubmitted boundary açık blok içinde gezilebilir,
- skip meşru; `unsubmitted_boundary != incorrect`,
- independence mode + allowed tools cevap öncesi açıklanır; objective-appropriate tool H0'ı bozmaz,
- in-session assistance engellenmez ve cezalandırılmaz; recheck planner-owned kalır,
- beş koşullu slot recomposition; completed valid evidence silinmez,
- incomplete session partial; exam debt yok; item dispute contested tutar, auto-invalidate etmez,
- provisional her yerde etiketli; invalid ne kredi ne ceza,
- result semantic (6 family); pass/fail banner, grade ve threshold yasak; `not_reliably_measured` first-class,
- independent 8D QA 107/107 PASS.

8C final:
- Task Runner execution surface'tir; planner/mastery/prerequisite/evidence authority değildir,
- working session emergent ve ungraded'dır; required count/duration/percentage yoktur,
- shared focused-flow frame hem Task Runner hem assessment session tarafından devralınır; assessment interior 8D'dedir,
- lifecycle `enter → orient → work → submit → resolve → transition`; entry/resume revalidation deterministiktir,
- assistance non-punitive'dir ve yalnız talep üzerine H1→H4 yükselir; H3/H4 öncesi consequence açıklanır,
- solution exposure sonrası same-item mastery path yoktur; recheck scheduling planner-owned kalır,
- provenance sorulur, çıkarsanmaz; dürüst beyan cezasızdır,
- in-flight run replan'dan korunur; continuity recomputed planner selection kullanır,
- `evaluation_pending` evidence yazmaz ve pass/fail değildir,
- independent 8C QA 123/123 PASS.

**Sıradaki gerçek numbered work:** `12C — Planner Engine v1`.

**12C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**


---

## 7D completion addendum — D-066

7D `TEIP-v0 — Technical English Integration Policy` ile tamamlandı. Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`; policy/QA: `curriculum/english/7d_technical_integration/`. Exact 15 D01 Skill korunur; 4 construct-aware mode, technical/English component attribution, bidirectional contamination guard, evidence-driven reversible scaffold ve authentic-resource integrity semantics kabul edildi. English/CEFR global technical gate değildir. Independent QA 49/49 PASS. Current active numbered step 7E'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 7E completion addendum — D-067

7E `TEPM-v0 — Technical English Mastery Profile` ile tamamlandı. Canonical: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`; policy/QA: `curriculum/english/7e_mastery_profile/`. Exact 15 D01 Skill korunur; 8 derived Skill presentation state, qualified A1/A2/B1 Technical English base profile, review/verification/remediation hysteresis presentation, first-class uneven profile ve exact 4-Skill B2+ extension evidence semantics kabul edildi. General-English/official CEFR veya numeric aggregate claim yoktur. Independent QA 54/54 PASS. AŞAMA 7 tamamlandı. Current active numbered step 8A'dır; fresh PRE + kullanıcı açık onayı gerekir.


---

# 8A completion addendum — D-068

8A `UXIA-v0 — Adaptive Learning Information Architecture` ile tamamlandı.

- semantic primary shell: Today / Learn / Progress / Profile,
- Assessment/Technical English/AI/remediation-retention contextual; ayrı top-level silo değil,
- shared Topic/Skill/explanation/report/English-profile/history detail family,
- focused Task Runner + assessment-session flows,
- UI hierarchy prerequisite truth veya mastery source of truth değildir,
- PDT-v0 explanation, TEPM-v0 profile ve canonical engine ownership korunur,
- independent QA 49/49 PASS.

**8A sonrası:** `8B — Ana ekran` THUX-v0 / D-069 ile tamamlandı. Güncel active step için yukarıdaki Final takeover state bölümünü kullan.


---

# 8B completion addendum — D-069

8B `THUX-v0 — Today Home UX` ile tamamlandı.

- Today action-first canonical planner/state projection,
- 5 semantic content region,
- current selected PlannedTask queue; no candidate/backlog/debt leakage,
- hard daily-capacity context + today override replan,
- PDT-v0 bounded trace-backed reasons,
- contextual assessment + Technical English,
- completion != mastery; missed day != debt,
- 12 semantic loading/ready/empty/offline/AI-degraded/recovery state,
- independent QA 90/90 PASS; Stage 6/7/8A + external-memory regressions PASS.

**Sıradaki gerçek numbered work:** `12C — Planner Engine v1`.  
**12C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**


---

## 9E completion addendum — D-079

9E `AIAX-v0 — AI Integration Architecture` ile tamamlandı. Canonical: `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md`; contract/QA: `arch/9e_ai_integration/`; synthesis: `research/9e_ai_integration_research.md`.

`MSBX-v0`nin `EvaluatorPort`'u arkasındaki AI davranışı kilitlendi. AI bir yardımcıdır ve mastery, retention, prerequisite, planner veya curriculum truth üzerinde asla otorite kazanmaz; yokluğu ya da hatası negative evidence üretmez. `LEARNING_BEHAVIOR_RULES` §17 (model seçimi) ve §18 (güvenlik/proxy/backend) bu adıma devredilmişti ve ikisi de kapatıldı. AI'ın gerçek katkıları korundu; amaç yetkiyi sınırlamak, AI'ı azaltmak değil. Evaluator çıktısı schema-constrained'dir ve schema'ya uymayan yanıt bir hatadır — serbest metinden hüküm ayrıştırmak yasak, çünkü bir misparse hüküm gibi görünür. Kalibre edilmemiş LLM değerlendirmesi `provisional`dır. **Refusal bir yanlış cevap değildir**: `refused`, `timed_out`, `transport_error`, `invalid_response` ve `unavailable` hepsi `evaluation_pending`e düşer ve evidence yazmaz. Timeout bütçesi uçtan ucadır. Model adı konfigürasyonda yaşar; adapter provider-independent'tır ve somut model kimliklerinin güncelliği 10A/14'te yeniden doğrulanacaktır. Deterministik iş asla AI çağırmaz. APK'ya hardcoded veya paylaşılan key konmaz ve V1'de backend proxy yoktur; öğrenci kendi key'ini girer, key platform secure storage'da tutulur ve log/export/backup/diagnostics'te görünmez. Yalnız mevcut attempt için gereken asgari içerik cihazdan çıkar. Generated item untrusted girer ve generator ile validator ayrıdır. Her AI-türevli evidence satırı provider, model ve prompt/schema version kaydeder. Independent QA 86/86 PASS; Stage 6/7/8 + 9A–9D + external-memory regressions PASS. Current active numbered step 9F'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 9F completion addendum — D-081

9F `TVSX-v0 — Test & Verification Strategy` ile tamamlandı ve **AŞAMA 9 kapandı**. Canonical: `docs/TEST_STRATEGY_SPEC.md`; contract/QA: `arch/9f_test_strategy/`; synthesis: `research/9f_test_strategy_research.md`.

Ana invariant: **üzerine hiçbir şeyin düşmediği bir garanti bir tercihtir.** AŞAMA 9 vaatleri yapıya çevirmişti; check'i olmayan bir dependency kuralı bir yorum, negatif testi olmayan bir append-only schema'sı kimsenin doğrulamadığı bir varsayımdır. Beş kanonik spec test stratejisini bu adıma devretmişti ve hepsi karşılandı. Altı katman tanımlandı ve cihaz katmanı en küçüktür. Coverage yüzdesi gate değildir; gate invariant coverage'dır ve sahipsiz bir invariant tek başına bloklar. Negatif doğrulama zorunludur. Append-only schema seviyesinde, migration'lar dolu fixture'lara karşı doğrulanır. Hiçbir check canlı AI provider çağırmaz. Null-evaluator yolu `ai-adapter` olmadan build alınarak doğrulanır. Determinizm enjekte saatle egzersiz edilir ve flaky check düşmüş check'tir. Release gate 11 koşuldur ve `tools/validate_*.py` glob'unun tamamını içerir. 66 kayıtlı invariant'ın her biri upstream kontratta gerçekten var olan bir anahtardır. Independent QA 288/288 PASS, mutation-tested 8/8; 26/26 validator sweep PASS. Ayrıca `D-080` ile dağıtım kapsamı kişisel kullanıma kilitlendi: store QA yok, device QA tek cihaz, kanıt ve AI yetki kuralları değişmez. Current active numbered step 10A'dır; fresh PRE + kullanıcı açık onayı gerekir ve 10A iki devralınmış yükümlülük taşır: `AMTS-v0` §9 bounded verification list ve repoda kayıtlı olmayan hedef cihaz.


---

## 10A completion addendum — D-082

10A `MPSX-v0 — Mobile Project Skeleton` ile tamamlandı ve **AŞAMA 10 başladı**. Canonical: `docs/PROJECT_SETUP_SPEC.md`; contract/QA: `arch/10a_project_setup/`; synthesis: `research/10a_project_setup_research.md`; proje: `android/`.

Ana invariant: **çalıştırılmamış hiçbir şey iddia edilmez.** 1A–9F birbirine karşı doğrulanan spec üretiyordu; 10A'nın çıktısını bir makine çalıştırıyor. Build, bu adımın hafızadan iddia edeceği iki şeyi anında yanlışladı — Gradle `9.5.0` dağıtımı çözülmüyor (pin 9.7.1) ve AGP 9.0+ `org.jetbrains.kotlin.android`ı reddediyor — ve ikisi de gizlenmeden kaydedildi. Toolchain 2026-08-31'de doğrulandı; `minSdk` 26 / `targetSdk` 36 / `compileSdk` 37. **Hedef cihaz kaydedildi**: Poco M6 Pro, `2312FPCA6G`, Android 16 / API 36 — `AMTS-v0` §8.1'in açık maddesi kapandı ve `D-080` gereği bu tek hedef cihazdır. `minSdk` cihaz seviyesine yükseltilmedi çünkü §8.1 shim gerektirmeyen en düşük seviyeyi istiyor. `AMTS-v0` §9'un altı maddesi güncel kaynaklarla kapandı. On modül `android/` altında ve her modülün beyan ettiği bağımlılıklar `boundaries.yaml`a eşit; yalnız iki `app-*` modülü Android modülü olduğu için T2 ve adaptör doğrulaması cihaz dışında koşabiliyor. `verifyModuleBoundaries` yasak kenarı, tanınmayan katmanı, composition-root ayrıcalığını ve hesaplanan cycle'ı bulup build'i düşürüyor; mutation-test edildi ve kendi cycle raporlaması düzeltildi. `-PwithAiAdapter=false` ile adaptörsüz build geçiyor ve `NullEvaluator` seçiliyor; V1 kriteri 8 tek komutla gösterilebilir. Saat tek yerde okunuyor, dynamic colour hiçbir yerde yok, key repoya giremiyor. CI T3/T1/iki build ve `validate_*.py` glob'unun tamamını koşuyor; T6 kasıtlı olarak yok. Independent QA 127/127 PASS, mutation-tested 7/7; 27/27 sweep PASS. Current active numbered step 10B'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 10B completion addendum — D-083

10B `NSHX-v0 — Navigation Shell` ile tamamlandı. Canonical: `docs/NAVIGATION_SHELL_SPEC.md`; contract/QA: `arch/10b_navigation/`; synthesis: `research/10b_navigation_research.md`; kod: `android/core-presentation/.../Navigation.kt` ve `android/app-ui/.../AppShell.kt`.

10B dış araştırma gerektirmedi: ihtiyaç duyulabilecek tek ekosistem bilgisi olan adaptive navigation API'leri 10A'da doğrulanıp pinlenmişti. Ana invariant: **navigasyon kuralları `core-presentation`da yaşar, UI toolkit'inde değil.** Route string'leriyle dolu bir `NavHost` destination kümesini, sırasını, paylaşılan surface kimliğini ve dönüş kuralını Compose'a sokardı; `MSBX-v0` bunu presentation state için zaten reddetmişti. Dört destination kabul edilmiş sırada ve **enum bildirim sırası kanoniktir**. On bir yasak top-level id'nin hiçbiri destination değildir. **Kanonik entity başına tek surface objesi** vardır, yani `UXIA-v0`nin yasakladığı çelişkili Skill detail sayfaları temsil edilemez. Contextual edge kümesi kapalıdır ve validator onu `ia.yaml` ile karşılaştırır. **Focused flow shell'i askıya alır** ve `showsShell` ile `requiresSafeExit` türetildiği için çıkışsız gizli shell inşa edilemez; detail pane de bastırılır. Dönüş kuralı deterministiktir ve origin geçerliliğini açık girdi alır. Window class'lar `WFPX-v0` breakpoint'lerinden core'da hesaplanır ve yalnız çizimi değiştirir. Her destination metin etiketi, `stateDescription` ve `traversalIndex` taşır. Dört run çalıştırıldı ve adaptörsüz build hâlâ geçiyor, yani shell V1 kriteri 8'i zayıflatmadı. Independent QA 104/104 PASS, mutation-tested 8/8; 28/28 sweep PASS. Current active numbered step 10C'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 10C completion addendum — D-084

10C `DSIX-v0 — Design System Implementation` ile tamamlandı. Canonical: `docs/DESIGN_SYSTEM_IMPL_SPEC.md`; contract/QA: `arch/10c_design_system/`; synthesis: `research/10c_design_system_research.md`; kod: `android/core-presentation/.../DesignTokens.kt`, `.../Tone.kt` ve `android/app-ui/.../CoachTheme.kt`.

Ana invariant: tasarım sistemi kanonik state'in iddia etmediği severity'yi ekleyemez ve bir kural temsil edilemez kılınabiliyorsa öyle yapılır. Token'lar tema dosyasında değil `core-presentation`da düz veridir, çünkü hex'ler yalnız Compose'da yaşasaydı doğal kısayol hatırlanan bir oranı iddia etmek olurdu — 8G'de tam olarak bu başarısız oldu. Palet birebir kopyalandı ve revize edilmedi; kontrast iki temada hex'ten yeniden hesaplandı ve kayıtlı minimumlar (6.08 / 3.79 / 6.06) token'lardan yeniden türetilip tuttu. `LearningTone` beş değerlidir ve atanacak bir fault değeri olmadığı için bir learning state'e fault tonu vermek yazılamaz; Material'ın `error` rolü yalnız `system_fault` taşır. Sekiz Skill state'inin her birinin tam bir tonu vardır ve üçü bilinçle nötrdür çünkü bekleme başarısızlık değildir. Attention grubunda görünmek tonu değiştirmez ve bu adı konmuş, test edilen bir fonksiyondur. 48dp bir modifier'dır, metin %200'e ölçeklenir, state her zaman metin olarak da verilir ve locale-naive case transform yoktur. Dynamic colour scan'i bu adımın kendi yorumunu yakaladı ve gate gevşetilmek yerine yorum yeniden yazıldı. Independent QA 146/146 PASS, mutation-tested 8/8; 29/29 sweep PASS. Current active numbered step 10D'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 10D completion addendum — D-085

10D `LDBX-v0 — Local Database` ile tamamlandı. Canonical: `docs/LOCAL_DATABASE_SPEC.md`; contract/QA: `arch/10d_local_database/`; synthesis: `research/10d_local_database_research.md`; kod: `android/data-persistence`.

Ana invariant: storage engine mimarinin yasakladığını reddeder ve her ret denenerek kanıtlanır. **İlk taslak `DDM-v0`den sapmıştı** — outcome ekseninde 10A'nın evaluator sinyali enum'u kullanılmıştı, iki eksende değerler eksikti, offset saniye tutuluyordu ve 12 truth entity yerine 4 tablo vardı. Kendi testleri aynı taslağa karşı yazıldığı için geçiyordu; kontratı okuyan validator yakaladı ve şema kabulden önce yeniden yazıldı. Library API'si çözülmüş jar'dan `javap` ile okundu. 11 değişmez curriculum, 13 append-only truth ve 8 yeniden kurulabilir projection tablosu var; user truth'tan curriculum'a foreign key yok. Truth ve curriculum tablolarında abort eden UPDATE/DELETE trigger'ları envanterden üretiliyor; her DDM değer kümesi CHECK; pinning yapısal; offset dakika ve tam dakika olmayan reddediliyor; tek global truth sequence watermark. Migration ileri-yönlü ve transaction'lı, dolu fixture'a karşı içerikle test edildi. Adapter kolonları SQLite'tan okuyor. Aynı şema JVM'de ve cihazda koşuyor; arm64-v8a native kütüphane APK içinde doğrulandı. 22 T2 check ve mutation 9/9 — biri başta kaçtı ve test güçlendirildi. Independent QA 163/163 PASS; 30/30 sweep PASS. Current active numbered step 10E'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 10E completion addendum — D-086

10E `APHX-v0 — App Health` ile tamamlandı ve **AŞAMA 10 kapandı**. Canonical: `docs/APP_HEALTH_SPEC.md`; contract/QA: `arch/10e_app_health/`; synthesis: `research/10e_app_health_research.md`; kod: `core-model` (`StoreHealth.kt`), `core-application` (`StoreStartup.kt`), `core-presentation` (`AppHealth.kt`), `data-persistence` (`StoreOpener.kt`, `Backup.kt`), `ai-adapter`, `app-ui` (`HealthSurface.kt`), `app-wiring` (`CoachApplication.kt`).

Ana invariant: store'un hiçbir arızası çökme değildir ve hiçbir arızası reset değildir. Handoff'un iki sorununa (main thread açılışı, çökme olan `DataRecoveryRequired`) ek olarak kod kontratlara karşı okununca iki sorun daha bulundu: açılışta bütünlük kontrolü yoktu ve varsayılan build'in AI adaptörü `TODO()` ile çökecekti. Store süreçte bir kez arka planda açılıyor (latch'li JVM testi, yeni port yok); `quick_check` + FK migration'dan önce, tam `integrity_check` sonra (fark bir fixture ile kanıtlandı); sebep `core-model` tipi; yedi bozulma/versiyon biçiminde dosya byte byte değişmiyor; altı `UXIA-v0` state'i `VDSX-v0` tonlarıyla, shell yalnız normal kullanımda, tek aksiyon `RECHECK` ve reset temsil edilemez. Kullanıcı kararıyla restore mekanizması burada, Profile kontrolleri 16D'de: doğrulanmış export, kopya üzerinde doğrulama, atomik rename, eski hot journal kenara, birleştirme yok. Mutation 16/16 — M08 ve M09 başta yaşadı ve testler güçlendirildi. T6 çalıştırılmadı; telefon bağlı değildi. Independent QA 152/152 PASS, validator mutation 12/12; 31/31 sweep PASS. Current active numbered step 11A'dır; fresh PRE + kullanıcı açık onayı gerekir.


---

## 11B completion addendum — D-088

11B `RNRX-v0 — Task Runner` ile tamamlandı. Canonical: `docs/TASK_RUNNER_SPEC.md`; contract/QA: `arch/11b_task_runner/`; synthesis: `research/11b_task_runner_research.md`; kod: `core-model` (`AttemptFacts.kt`), `core-presentation` (`TaskRunner.kt`), `core-application` (`SubmitAttempt.kt`), `app-ui` (`TaskRunnerScreen.kt`).

Ana invariant: runner bir execution surface'tir; planner, mastery, prerequisite ya da evidence otoritesi değildir. Runner kodundan önce main'de 11A'nın sync betiğinden kalan U+0307 bulundu, düzeltildi ve validator ile korunuyor. Girişte her koşul doğrulanmalı, doğrulanamayan `unmet`; bugün hiçbir görev başlatılamaz. Yardım hep istenebilir, istenmeden verilmez, H3/H4 açıklamasız verilmez (kapsam koşulu olmadan). Deneme tek transaction: attempt + artifact + provenance + assistance, evidence yok. Mutation 18/18; independent QA 147/147; validator mutation 18/18; 33/33 sweep PASS. T6 çalıştırılmadı.


---

## 11C completion addendum — D-089

11C `SESX-v0 — Session State` ile tamamlandı. Canonical: `docs/SESSION_STATE_SPEC.md`; contract/QA: `arch/11c_session_state/`; synthesis: `research/11c_session_state_research.md`; kod: `core-model` (`SessionFacts.kt`), `core-presentation` (`SessionState.kt`), `core-application` (`ResumeCheckpoints.kt`), `PersistencePort.readTruth`.

Ana invariant: bir duraklatma işin nerede olduğunu saklar, ne kadar sürdüğünü ya da ne kadar iyi gittiğini değil. Checkpoint `ResumeContext` + high-stakes işareti; sürümlü, katı çözülen tek append-only satır. Yalnız durable pause yazılır ve kaydedilmiş gösterilir. Resume yalnız checkpoint'in kanıtladığını doğrular; gap eşiği uydurulmadı; bugün devam ettirilebilen checkpoint yok ve Today checkpoint sunmaz. Working session emergent, puansız ve saklanmaz. Main'de 11A'dan kalan iki bozuk kelime düzeltildi ve korunuyor. Mutation 20/20; independent QA 146/146; validator mutation 20/20; 34/34 sweep PASS. T6 çalıştırılmadı. Current active numbered step 11D'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 11D completion addendum — D-090

11D `DMAX-v0 — Daily Micro Assessment Implementation` ile tamamlandı. Canonical: `docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md`; contract/QA: `arch/11d_daily_micro_assessment/`; synthesis: `research/11d_daily_micro_assessment_research.md`.

Ana invariant: assessment session bir kanıt toplama akışıdır ve bir item yalnız validation'ının, evaluator'ının ve Objective'in kanıt profilinin izin verdiğini taşıyabilir. Curriculum bölgesinin hiç yazıcısı yoktu; yayımlama tek yol, tek transaction ve yayımlanmış sürüm üzerine yazılmaz. Authored paket katı ayrıştırılır ya da hiç sunulmaz. Güven mağazanın validation kaydıdır; tavan en kısıtlayıcı kuraldır ve deklare edileni aşmaz; kanıt uyumuna Objective karar verir. Exposure yalnız gerçekten gösterilen için yazılır. Tek interior bütün scope'lara hizmet eder; gönderilen sınır donar, boş bırakmak yanlış değildir, yardım engellenmez ve sonuç puansızdır. Kısa artifact gövdesi referansın içinde taşınır ya da reddedilir. **Mutation koşucusunun Gradle'ı hiç çalıştırmadığı bulundu**; düzeltildi, 11D 27/27 ve 11C 20/20 yeniden koşuldu. 188/188 QA PASS, validator mutation 27/27, 35/35 sweep. T6 çalıştırılmadı. Current active numbered step 11E'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 11E completion addendum — D-091

11E `EODX-v0 — End of Day` ile tamamlandı ve **AŞAMA 11 kapandı**. Canonical: `docs/END_OF_DAY_SPEC.md`; contract/QA: `arch/11e_end_of_day/`; synthesis: `research/11e_end_of_day_research.md`.

Ana invariant: gün sonu bir hüküm değil, zamanda bir sınırdır. Kabul edilmiş bir gün-sonu spec'i yoktu; her kural bir sahipten türetildi. Gün satırın kaydettiği çalışma günüdür ve instant'tan yeniden hesaplanmaz; yeni gün boş başlar ve sınırı hiçbir şey geçmez. Sayımlar etiketli envanterdir (toplam/oran/yüzde/hedef yok) ve ilerleme değildir; değişiklik ancak kanonik bir engine bildirdiyse iddia edilir; okunamayan sayım adlandırılır; boş gün nötrdür; günler arası boşluk çizilmez. Today'in gün bağlamında render edilir, yeni surface ve grafik yoktur. `countTruth` hiçbir şey yazmayan bir port incelmesidir. Mutation 20/20 (biri test güçlendirilince), 128/128 QA PASS, validator mutation 26/26, 36/36 sweep. T6 çalıştırılmadı. Current active numbered step 12A'dır; fresh PRE + kullanıcı açık onayı gerekir.


---

## 12A completion addendum — D-092

12A `MSTX-v0 — Mastery Engine v1` ile tamamlandı ve **AŞAMA 12 başladı**. Canonical: `docs/MASTERY_ENGINE_IMPL_SPEC.md`; contract/QA: `arch/12a_mastery_engine/`; synthesis: `research/12a_mastery_engine_research.md`.

Ana invariant: mastery tek bir soru sorar — yardımsız yapabiliyor mu? Yardımlı, görülmüş, doğrulanmamış, itirazlı ve bozuk prerequisite üzerindeki kanıt skora girmez ve bunların hiçbiri ceza değildir. Kanıtı hiçbir şey yazmıyordu; pipeline deneme başına tek transaction yazıyor, Objective sürümü pinli, yanıtsız değerlendirme hiçbir şey yazmıyor ve ölçülemeyen cevap sıfır değil. Bağımlı grup tek grup, pencere son beş, ortalama eşit ağırlıklı; Skill non-compensatory; histerezisin iki yarısı da var. Projeksiyon kanıttan yeniden kurulur, truth yazmaz, provenance taşır ve yalnız kendi eksenini yazar. Sabitler `GRE-v0`ün kalibre edilmemiş sezgileri (18C). Mutation 33/33 (ikisi test güçlendirilince), 164/164 QA PASS, validator mutation 37/37, 37/37 sweep. T6 çalıştırılmadı ve motor uygulamada erişilebilir değil. Current active numbered step 12B'dir; fresh PRE + kullanıcı açık onayı gerekir.


---

## 12B completion addendum — D-093

12B `PRQX-v0 — Prerequisite Engine` ile tamamlandı. Canonical: `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md`; contract/QA: `arch/12b_prerequisite_engine/`; synthesis: `research/12b_prerequisite_engine_research.md`.

Ana invariant: bir eksik prerequisite yalnız gerçekten ona bağlı işi bekletir. `review_due` bloklamaz, soft eksik kilitlemez, task'in kendi gereksinimi hard'dır, priority kapıyı aşamaz ve bekleyen ihtiyaç başarısız değildir. Readiness dört değerli ve sayı değil; değerlendirilmemiş eksen adlandırılır ve kötü haber sayılmaz; kapı eksik bilgide kapalı kalır. Authored 950 kenarın hepsi `draft` bulundu ve metadata sorunu olarak adlandırılıyor. Bekleyen aday üzerindeki iş `contaminated` yazılır. Yalnız `prerequisite_readiness` yazılır; `skill_state` tek watermark altında 12D'de birleşecek. Mutation 44/44, 184/184 QA PASS, validator mutation 42/42, 38/38 sweep. T6 çalıştırılmadı ve kapı uygulamada erişilebilir değil. Current active numbered step 12C'dir; fresh PRE + kullanıcı açık onayı gerekir.
