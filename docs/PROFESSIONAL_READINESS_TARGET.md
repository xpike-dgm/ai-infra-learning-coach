# Professional Readiness Target — AI Infrastructure / ML Systems / GPU Systems

**Durum:** CANONICAL PRODUCT TARGET  
**Tarih:** 2026-08-25  
**Kararlar:** D-041, D-042, D-043

Bu belge AI Infra Learning Coach'un uzun vadeli öğrenme rotasının çıkış hedefini tanımlar.

## 1. Yeni ana hedef

Uygulamanın uzun vadeli curriculum'u yaklaşık üç yıllık bir rota ile sınırlı değildir.

Yeni hedef:

> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl sürebilecek kapsamlı ve mastery-gated bir öğrenme rotasıyla AI Infrastructure / ML Systems / GPU Systems alanında profesyonel seviyede bağımsız çalışmaya hazırlanacak teknik yeterliliğe ulaştırmak.**

`4+ yıl` bir takvim garantisi veya mezuniyet süresi değildir. Kullanıcının başlangıç seviyesi, günlük kapasitesi, öğrenme hızı, ara vermeleri ve mastery sonuçları süreyi değiştirebilir.

Canonical kural:

```text
elapsed_time != professional_readiness
curriculum_completion != automatic professional_readiness
professional_readiness = verified capability across required professional domains + capstone/transfer evidence
```

## 2. “Profesyonel” ne demektir?

Bu projede `professional_readiness`, kullanıcının yalnızca dersleri görmesi veya quizleri geçmesi değildir.

Çıkış hedefi, kullanıcının önemli bir AI Infrastructure / ML Systems probleminde:
- problemi anlayabilmesi,
- sistemi tasarlayabilmesi,
- C/C++/Python ve ilgili araçlarla uygulayabilmesi,
- Linux üzerinde debug edebilmesi,
- concurrency/networking/distributed-system davranışını analiz edebilmesi,
- GPU/CUDA/Triton tarafında performans darboğazını ölçebilmesi,
- LLM inference stack'ini kurup inceleyebilmesi,
- throughput/latency/memory trade-off'larını ölçebilmesi,
- test/benchmark/profiling yapabilmesi,
- production reliability/observability temelini uygulayabilmesi,
- teknik kararlarını yazılı ve sözlü açıklayabilmesi,
- dokümantasyon ve kaynak kod okuyabilmesi,
- yeni bir problemi ezberlenmiş tutorial olmadan çözebilmesi

gibi davranışları bağımsız evidence ile göstermesidir.

Bu hedef `senior engineer` unvanı veya belirli şirket seviyesini garanti etmez. Gerçek ekip/production tecrübesi iş ortamında ayrıca oluşur.

## 3. Uygulama neyi garanti edemez?

Uygulama:
- iş teklifi garanti edemez,
- belirli bir maaş garanti edemez,
- üniversite/lisans isteyen HR filtrelerini ortadan kaldıramaz,
- gerçek ekip deneyimini tamamen simüle edemez,
- yalnız curriculum tamamlandı diye profesyonel yetkinlik ilan edemez.

Ancak ürün, diploma veya iş deneyiminden bağımsız olarak teknik yetkinliği mümkün olduğunca güçlü gösterecek:
- gerçek projeler,
- benchmark raporları,
- capstone'lar,
- GitHub artifacts,
- open-source contribution hazırlığı,
- debugging/performance case study'leri,
- bağımsız assessment evidence

üretmeyi hedefler.

## 4. Uzun vadeli teknik omurga

Ana uzmanlaşma yönü korunur fakat öğretim derinliği büyür:

```text
Technical English
+ Computer Science / Programming Foundations
+ Python
+ C
+ Linux / Tooling
+ Modern C++
+ Data Structures & Algorithms foundations
+ Computer Architecture
+ Operating Systems / Memory
+ Concurrency / Parallel Programming
+ Networking
+ Distributed Systems
+ Databases / Storage fundamentals where infrastructure-relevant
+ Containers / Cloud / Observability foundations
+ Performance Engineering
+ GPU Architecture
+ CUDA
+ Triton
+ ML/Transformer fundamentals needed for inference
+ LLM Inference Internals
+ vLLM / SGLang / TensorRT-LLM-style serving systems
+ Quantization / KV Cache / Batching / Scheduling
+ Multi-GPU / Multi-node Inference
+ AI Infrastructure / GPU Infrastructure
+ Production Reliability / Benchmarking / Capacity Thinking
+ Open Source / Technical Communication / Interview & Career Readiness
+ Specialization Track
```

Python, C/C++'ın yerine geçmez. D-042 ile programlama/automation/testing/benchmark/ML-infra tooling için resmi common foundation ve professional tool olarak eklenmiştir.

Makine öğrenmesi ana uzmanlık dalına dönüşmez; ancak transformer/inference sistemlerini gerçekten anlayacak kadar gerekli matematik, tensor, model architecture ve inference kavramları öğretilir.

## 5. Derinlik ilkesi

Yeni kapsam `daha çok konu listesi` anlamına gelmez. Her önemli domain şu katmanlarla öğretilmelidir:

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

## 6. Professional evidence katmanları

Uzun rotada yalnız Objective/Skill mastery değil, daha büyük ölçekli evidence da gerekir.

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
En az bir final professional capstone familyası, gerçekçi constraints altında bağımsız tasarım + implementation + test + profiling + documentation + postmortem/decision explanation istemelidir.

Exact capstone sayısı şimdiden bilimsel sabit olarak kilitlenmez; coverage ve diversity gereksinimleri 5/14/19/20 aşamalarında tasarlanır.

## 7. Gerçek dünya çalışma alışkanlıkları curriculum'un parçasıdır

Profesyonel readiness yalnız algoritma bilgisi değildir. Uzun curriculum zaman içinde şunları da öğretmelidir:
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

## 8. English çıkış hedefi

English paralel hat olmaya devam eder.

Uzun vadeli hedef yalnız grammar tamamlamak değildir; kullanıcı:
- teknik dokümantasyon,
- GitHub issue/PR,
- design docs,
- CUDA/GPU dokümantasyonu,
- teknik makale/paper,
- code review,
- teknik mülakat,
- global ekip iletişimi

gibi gerçek bağlamlarda iş görebilecek teknik İngilizceye ilerlemelidir.

Exact CEFR çıkış gate'i 6A–6E'de araştırma ve curriculum design ile kesinleşir; yalnız takvim geçti diye English professional-ready sayılmaz.

## 9. V1 ile 4+ yıllık rota ayrımı

Bu karar V1 release'i 4+ yıl boyunca bekletmez.

```text
V1 application release
    = learning engine + planner + assessment + first production curriculum package

full professional curriculum
    = aynı engine üzerinde yıllar içinde kapsanan ve QA edilen geniş curriculum
```

İlk 8–12 haftalık production-quality içerik V1 release için yeterli olabilir; ancak ürünün **nihai curriculum kapsamı** artık o paketle veya yaklaşık üç yıllık horizon ile sınırlı değildir.

## 10. Curriculum expansion ve specialization ilkesi

Aşama 5 ve 14 ilk üretim curriculum paketini oluşturur. Aşama 19 tam professional route'un ortak çekirdeğini genişletir. D-043 ile Aşama 20, bu ortak çekirdeğin üzerine specialization track katmanını kurar.

Her ileri paket:
- prerequisite graph'a bağlanmalı,
- Objective/Skill düzeyinde authoring yapılmalı,
- assessment/evidence contract taşımalı,
- retention/remediation desteklemeli,
- gerçek proje/transfer görevleri içermeli,
- outdated teknoloji bilgisini sürekli QA etmelidir.

Specialization track'ler de aynı kurallara tabidir. Kullanıcı track değiştirirse önceki geçerli mastery/evidence silinmez; yalnız yeni track'in eksik prerequisite/critical capability'leri açılır.

## 11. Specialization track hedefi — D-043

Candidate track family'leri:
- GPU Kernel & Performance Engineering
- LLM Inference / Serving Systems
- Distributed AI Infrastructure / Cluster & Scheduling
- High-Speed Networking & Multi-GPU Systems
- ML Compilers / Runtime Systems
- AI Platform / Reliability / Capacity Engineering

Bu isimler gelecekte değişebilir. Product canonical olarak belirli framework veya iş unvanını değil, kalıcı capability kümelerini takip etmelidir.

Track seçiminde:
- kullanıcı tercihi,
- verified strengths/weaknesses,
- prerequisite readiness,
- evidence quality,
- gerçek career/bridge constraints,
- gerektiğinde güncel market research

değerlendirilebilir. LLM önerisi, hype veya maaş tek başına final track seçmez.

## 12. Professional readiness gate — davranış seviyesi

Final professional-ready state ileride ayrı engine/spec ile kesinleştirilecektir; şimdilik şu invariant'lar bağlayıcıdır:

1. Takvim süresi tek başına geçiş veremez.
2. Bütün required common professional domains'de gerekli Skill gates karşılanmalıdır.
3. Critical systems/GPU/inference Skills bağımsız direct evidence ister.
4. Integrated common-core capstone evidence zorunludur.
5. Seçilen specialization track için track-specific critical evidence ve capstone gerekir.
6. Sadece tutorial takip edilmiş projeler final evidence değildir.
7. Debugging + transfer + performance evidence bulunmalıdır.
8. Retention açıkları ve unresolved critical verification final readiness'i engeller.
9. AI-generated artifact, kullanıcı bağımsız anlayış/üretim evidence'ı olmadan final readiness'e yazılamaz.
10. Professional readiness tek bir final quiz veya tek puanla verilmez.
11. Career readiness ve teknik readiness ayrı ama bağlı katmanlardır.

## 13. Master plan etkisi

- 4B ve mevcut ürün motoru tasarımı devam eder.
- 5A–5D curriculum graph, 4+ yıl hedefini ve future branching'i destekleyecek extensible metadata ile tasarlanır.
- 14A–14H ilk production package'tır; full curriculum değildir.
- 17 pilot/calibration motorun gerçekten öğrenme sağladığını ölçer.
- 19A–19I ortak long-term professional curriculum + open-source + career readiness katmanını genişletir.
- 20A–20G specialization track mapping, selection, branch curriculum/evidence ve track capstone/readiness katmanını tasarlar.
- D-043 mevcut 1–19 adımlarını renumber etmez; AŞAMA 20 sona eklenmiştir.

## 14. Final karar özeti

> **Ürünün nihai amacı yalnız iyi bir başlangıç rotası sağlamak değil; sıfırdan başlayan kullanıcıyı yıllar boyunca adaptif ve kanıt-temelli şekilde geliştirerek AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek teknik derinliğe ulaştırmak ve ortak çekirdek sonrasında doğrulanmış bir uzmanlık dalında derinleşmesini sağlamaktır.**

> **4+ yıl yalnız esnek planlama horizon'ıdır. Profesyonel çıkışın gerçek gate'i zaman değil, bağımsız ve entegre engineering evidence + specialization evidence'dır.**
