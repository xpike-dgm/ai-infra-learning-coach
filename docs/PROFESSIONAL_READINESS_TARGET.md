# Professional Readiness Target — AI Infrastructure / ML Systems / GPU Systems

**Durum:** CANONICAL PRODUCT TARGET  
**Tarih:** 2026-08-25  
**Kararlar:** D-041, D-042, D-044

Bu belge AI Infra Learning Coach'un uzun vadeli öğrenme rotasının çıkış hedefini tanımlar.

## 1. Ana hedef

> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl veya daha uzun sürebilecek kapsamlı ve mastery-gated bir öğrenme rotasıyla AI Infrastructure / ML Systems / GPU Systems alanında profesyonel seviyede bağımsız çalışmaya hazırlanacak teknik yeterliliğe ulaştırmak.**

`4+ yıl` bir takvim garantisi veya mezuniyet süresi değildir. Başlangıç seviyesi, günlük kapasite, öğrenme hızı, ara verme ve mastery sonuçları süreyi değiştirir.

```text
elapsed_time != professional_readiness
curriculum_completion != automatic professional_readiness
professional_readiness = verified capability across required professional domains + capstone/transfer evidence
```

## 2. “Profesyonel” ne demektir?

Bu projede professional readiness yalnız ders görmek veya quiz geçmek değildir. Kullanıcının önemli bir AI Infrastructure / ML Systems probleminde:
- problemi anlayabilmesi,
- sistemi tasarlayabilmesi,
- Python/C/C++ ve ilgili araçlarla uygulayabilmesi,
- Linux üzerinde debug edebilmesi,
- memory/OS/concurrency/networking/distributed-system davranışını analiz edebilmesi,
- GPU/CUDA/Triton tarafında performans darboğazını ölçebilmesi,
- LLM inference stack'ini kurup inceleyebilmesi,
- throughput/latency/memory/cost trade-off'larını ölçebilmesi,
- test/benchmark/profiling yapabilmesi,
- production reliability/observability temelini uygulayabilmesi,
- teknik kararlarını yazılı ve sözlü açıklayabilmesi,
- dokümantasyon ve kaynak kod okuyabilmesi,
- yeni bir problemi tutorial kopyalamadan çözebilmesi

gibi davranışları bağımsız evidence ile göstermesidir.

Bu hedef `senior engineer` unvanı veya belirli şirket seviyesini garanti etmez. Gerçek ekip/production tecrübesi iş ortamında ayrıca oluşur.

## 3. Uygulama neyi garanti edemez?

Uygulama:
- iş teklifi garanti edemez,
- belirli maaş garanti edemez,
- üniversite/lisans isteyen HR filtrelerini ortadan kaldıramaz,
- gerçek ekip deneyimini tamamen simüle edemez,
- yalnız curriculum tamamlandı diye profesyonel yetkinlik ilan edemez.

Ancak teknik yetkinliği mümkün olduğunca güçlü gösterecek gerçek projeler, benchmark raporları, capstone'lar, GitHub artifacts, open-source contribution hazırlığı, debugging/performance case study'leri ve independent assessment evidence üretmeyi hedefler.

## 4. Güncel uzun vadeli teknik omurga

```text
Technical English — paralel
→ Python
→ C
→ Linux + Git + Shell
→ Data Structures & Algorithms foundations
→ Modern C++
→ Computer Architecture
→ Operating Systems + Memory
→ Concurrency / Parallel Programming
→ Networking
→ Distributed Systems + Storage / Databases foundations
→ Containers / Cloud / Observability
→ Performance Engineering & Profiling
→ GPU Architecture
→ CUDA
→ Triton
→ ML + Transformer foundations
→ LLM Inference Internals
→ vLLM / SGLang / TensorRT-LLM-style serving systems
→ KV Cache / Batching / Scheduling / Quantization
→ Multi-GPU + NCCL + RDMA
→ AI Infrastructure / GPU Infrastructure
→ Open Source contributions + real large projects + professional capstones
```

Python, C/C++'ın yerine geçmez. Python programlama temeli, automation, testing, benchmark scripting, data/ML ekosistemi ve infrastructure tooling için resmi foundation dilidir; C/C++/CUDA düşük seviye ve performance-critical katmanlarda derinleşir.

Makine öğrenmesi ana uzmanlık dalına dönüşmez; transformer/inference sistemlerini gerçekten anlayacak kadar gerekli matematik, tensor, model architecture ve inference kavramları öğretilir.

## 5. D-044 — Granular capability ilkesi

Ana rota yalnız geniş başlıklar halinde tutulamaz.

Canonical authoring/diagnosis yapısı:

`Domain → Module → Topic → Skill → Learning Objective`

Uygulama `Python zayıf` demekle yetinmemelidir. Mümkün olduğunda zayıflığı `Python → Control Flow → Loops → while termination` gibi Skill/Objective düzeyinde lokalize etmeli ve remediation'ı yalnız ilgili capability'ye yöneltmelidir.

Domain/Module/Topic kullanıcıya anlaşılır summary sağlayabilir; canonical mastery/prerequisite/remediation ana seviyesi Skill'dir, evidence Objective'e bağlanabilir.

Ayrıntı: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## 6. Derinlik ilkesi

Her önemli capability mümkün olduğunca şu katmanlarla öğretilir:

```text
conceptual model
→ guided application
→ independent application
→ debugging
→ explanation
→ transfer
→ delayed retention
→ integrated project
→ performance/production context where applicable
```

Kritik professional Skill yalnız video/metin/MCQ ile tamamlanamaz.

## 7. Professional evidence katmanları

### A. Foundation evidence
- Objective/Skill mastery
- prerequisite correctness
- delayed retention

### B. Applied engineering evidence
- user-authored code
- compiler/test artifacts
- debugging diagnosis/fix
- Linux/system tasks
- profiling/benchmark evidence

### C. Integrated systems evidence
- multi-module project
- concurrency/networking/storage integration
- design trade-off explanation
- failure-mode debugging

### D. GPU / inference evidence
- CUDA/Triton kernels
- profiler use
- memory/bandwidth reasoning
- inference server operation
- latency/throughput measurement
- KV-cache/batching/scheduling experiments
- multi-GPU/distributed inference exercises

### E. Professional capstone evidence
Final professional capstone familyaları gerçekçi constraints altında independent design + implementation + test + profiling + documentation + postmortem/decision explanation istemelidir.

Exact capstone sayısı bilimsel sabit olarak şimdiden kilitlenmez; coverage/diversity AŞAMA 6/15/20 ile tasarlanır.

## 8. Gerçek dünya çalışma alışkanlıkları

Uzun curriculum zaman içinde şunları da öğretir:
- Git ve branch/PR workflow,
- code review alma/verme,
- testing,
- build systems,
- debugging tools,
- profiling,
- documentation,
- issue decomposition,
- design docs,
- reproducible benchmarks,
- logging/metrics/tracing,
- incident/postmortem düşüncesi,
- security/reliability fundamentals,
- open-source repository okuma ve katkı hazırlığı.

## 9. English çıkış hedefi

English paralel hat olmaya devam eder. Amaç yalnız grammar tamamlamak değil; teknik dokümantasyon, GitHub issue/PR, design docs, CUDA/GPU docs, paper, code review, technical interview ve global ekip iletişiminde iş görebilecek seviyeye ilerlemektir.

Exact CEFR çıkış gate'i AŞAMA 7'de araştırma ve curriculum design ile kesinleşir; yalnız takvim geçti diye English professional-ready sayılmaz.

## 10. V1 ile full route ayrımı

```text
V1 application release
    = learning engine + planner + assessment + first production curriculum package

full professional curriculum
    = aynı engine üzerinde yıllar içinde kapsanan ve QA edilen geniş curriculum
```

İlk 8–12 haftalık production-quality içerik V1 release için yeterli olabilir; ancak nihai curriculum bununla sınırlı değildir.

## 11. Curriculum planning / production ayrımı — D-044 sonrası

```text
AŞAMA 5 = knowledge graph schema + domain backbone
AŞAMA 6 = full route granular capability map / weakness-addressable taxonomy
AŞAMA 15 = first 8–12 week production-quality lesson/task/assessment package
AŞAMA 20 = full professional curriculum content expansion + OSS + career + capstones
```

AŞAMA 6 bütün route family'lerini Module/Topic/Skill/Objective seviyesinde kapsamlı biçimde haritalamadan full content üretimine kör geçilmez.

## 12. Professional readiness gate — invariant'lar

1. Takvim süresi tek başına geçiş veremez.
2. Bütün required professional domains'de gerekli Skill gates karşılanmalıdır.
3. Critical systems/GPU/inference Skills bağımsız direct evidence ister.
4. Integrated capstone evidence zorunludur.
5. Sadece tutorial takip edilmiş projeler final evidence değildir.
6. Debugging + transfer + performance evidence bulunmalıdır.
7. Retention açıkları ve unresolved critical verification final readiness'i engeller.
8. AI-generated artifact, kullanıcı bağımsız anlayış/üretim evidence'ı olmadan final readiness'e yazılamaz.
9. Professional readiness tek final quiz veya tek puanla verilmez.
10. Career readiness ve technical readiness ayrı ama bağlı katmanlardır.

## 13. Final karar özeti

> **Ürünün nihai amacı sıfırdan başlayan kullanıcıyı yıllar boyunca adaptif ve kanıt-temelli biçimde geliştirerek AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek teknik derinliğe ulaştırmaktır.**

> **Profesyonel çıkışın gerçek gate'i zaman değil, granular Skill/Objective evidence + bağımsız integrated engineering capability'dir.**
