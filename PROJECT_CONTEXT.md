# Project Context — Kalıcı Proje Hafızası

Bu dosya, sohbet bağlamı kaybolsa bile projenin neden var olduğunu ve hangi temel kararların verildiğini yeniden kurmak için tutulur. Güncel ayrıntılı bağlam için `docs/START_HERE.md` ve `docs/PROJECT_MASTER_CONTEXT.md` canonical kaynaktır.

## 1. Ana kariyer yönü

Seçilen uzmanlaşma:

**Low-Level Systems → Distributed Systems → GPU/CUDA → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

Güncel ana rota:

**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + professional capstones**

Python D-042 ile resmi foundation dilidir; C/C++ yerine geçmez. ML tamamen atlanmaz; inference sistemlerini anlayacak kadar gerekli tensor/model/transformer temelleri destek katmanıdır.

## 2. D-041 — Uzun vadeli hedef

- Yaklaşık üç yıllık horizon kaldırıldı.
- Rota gerektiğinde **4+ yıl veya daha uzun** sürebilir.
- 4+ yıl countdown değildir.
- Nihai hedef yalnız course completion değil, professional-readiness seviyesinde verified engineering capability.
- Final readiness; required mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 full 4+ year curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. D-044 — Granular Capability Map

Ana rota yalnız geniş domain isimleri halinde tutulmayacaktır.

Canonical hierarchy:

`Domain → Module → Topic → Skill → Learning Objective`

Gerçek mastery/prerequisite/weakness/remediation mümkün olduğunca Skill/Objective seviyesinde çalışır. Domain/Module/Topic daha geniş derived summary olabilir.

Örnek hedef:

`Python → Control Flow → Loops → while termination`

Böylece sistem bütün Python'ı tekrar ettirmek yerine exact weakness'e hedefli reteach/practice/retest üretebilir.

Yeni **AŞAMA 6 — Granular Capability Map**, Technical English'ten AI Infrastructure ve professional capstone'a kadar bütün rotayı bu granularity'de haritalayacaktır.

Canonical: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## 4. Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Bu nedenle lesson/task completion, streak, self-confidence, AI-assisted output veya takvim süresi tek başına mastery/readiness değildir.

Canonical mastery modeli GRE-v0; retention modeli RVR-v0'dır.

## 5. İngilizce

Başlangıç English seviyesi A0 kabul edilebilir. English teknik eğitimin ön koşulu değildir; ilk günden paralel ilerler.

Technical task bilinmeyen English grammar yüzünden haksız başarısızlık üretmemelidir. Uzun vadede docs, GitHub, design docs, CUDA docs, papers, interview ve global team communication hedeflenir.

D-044 ile Technical English de granular capability map'e ayrılır. English davranış/ölçüm tasarımı **AŞAMA 7**'dedir.

## 6. Uygulamanın günlük amacı

Ana soru:

> **Bugün ne yapmalıyım?**

Sistem current mastery, retention, prerequisite, remediation, assessment ve daily capacity üzerinden günlük plan üretir.

Curriculum sabit takvim değil knowledge graph'tır.

## 7. Mastery / Planner omurgası

- GRE-v0 — valid + prerequisite-valid + H0 + direct + verified + independent evidence.
- RVR-v0 — time mastery'yi düşürmez; review_due forgetting değildir.
- D-033 — daily capacity hard budget.
- D-034 — LearningNeed / TaskCandidate / Evidence ayrımı.
- PBR-v0 — semantic priority.
- PRG-v0 — Skill-level prerequisite readiness.
- VDW-v0 — validated diagnostic waiver.
- SRR-v0 — absence debt/failure değildir; stale plan replay edilmez.
- PDT-v0 — planner decisions structured reason trace ile explainable.

3H simulation: **16/16 scenarios PASS, 20/20 invariants PASS**.

## 8. Assessment

4A DMA-v0 tamamlandı:
- daily assessment sabit quiz kotası değildir,
- Objective-matched evidence kullanır,
- H0/assistance/provenance/prerequisite guards vardır,
- invalid/ambiguous item credit/penalty yazamaz,
- coding/debugging/transfer standardı kısa süre uğruna düşürülemez.

Aktif adım: **4B — Haftalık sınav**.

## 9. Professional-readiness depth

```text
concept
→ guided application
→ independent application
→ debugging
→ explanation
→ transfer
→ delayed retention
→ integrated project
→ performance / production context
```

Uzun curriculum ayrıca Git, testing, build systems, profiling, design docs, observability, incident/postmortem thinking, open-source workflow ve technical communication kapsar.

## 10. Curriculum planning / production ayrımı

D-044 sonrası:

```text
AŞAMA 5 = graph/schema/domain backbone
AŞAMA 6 = detailed granular capability map
AŞAMA 15 = first 8–12 week production content
AŞAMA 20 = full professional curriculum + OSS + career + capstones
```

AŞAMA 6 bütün route'u ölçülebilir alt Skill/Objective'lere ayırır; AŞAMA 15 ve 20 bu haritaya gerçek lesson/task/assessment/project content bağlar.

## 11. V1 ve full curriculum ayrımı

**V1:** çalışan Android product + adaptive learning engine + first 8–12 week production package.

**Full curriculum:** aynı engine üzerinde yıllar boyunca QA edilerek genişletilen professional route.

V1 release ≠ professional curriculum completion.

## 12. İlk iş / bridge rolleri

Nihai hedef AI Infrastructure olsa da ilk iş doğrudan CUDA Engineer olmak zorunda değildir. Uygun bridge alanlar C/C++ development, Systems Software, Linux/Infrastructure, Distributed/Backend Systems, Performance, uygun SRE/Cloud ve ML/AI Infrastructure intern/junior rolleridir.

Product job offer, salary, seniority veya degree/HR filtrelerini garanti edemez.

## 13. Güncel yürütme konumu

Tamamlanan: Aşama 1, 2, 3 ve 4A.  
Aktif: **4B — Haftalık sınav**.

Current stage map:
- 5 graph/schema backbone
- **6 granular capability map**
- 7 English
- 8 UX
- 9 architecture/data
- 10 skeleton
- 11 daily MVP
- 12 mastery/planner implementation
- 13 assessment implementation
- 14 AI Tutor
- 15 first content
- 16 analytics
- 17 polish
- 18 pilot/QA
- 19 release APK
- 20 full professional curriculum/career/capstones

D-043 standalone specialization stage kararı geri çekildi. D-044 plan sync tamamlandı; 4B henüz yürütülmedi. 4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.
