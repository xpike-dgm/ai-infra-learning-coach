# Project Context — Kalıcı Proje Hafızası

Bu dosya, sohbet bağlamı kaybolsa bile projenin neden var olduğunu ve hangi temel kararların verildiğini yeniden kurmak için tutulur. Güncel ayrıntılı bağlam için `docs/START_HERE.md` ve `docs/PROJECT_MASTER_CONTEXT.md` canonical kaynaktır.

## 1. Ana kariyer yönü

Seçilen uzmanlaşma:

**Low-Level Systems → Distributed Systems → GPU/CUDA → AI Infrastructure / ML Systems / GPU Systems**

Ana rota:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Machine Learning tamamen atlanmaz; transformer/inference sistemlerini anlayacak kadar gerekli ML/tensor/model temelleri destek katmanıdır. Ana uzmanlık model training değil, inference ve altyapı tarafıdır.

## 2. D-041 — Yeni uzun vadeli hedef

2026-08-24'te curriculum kapsamı büyütüldü.

- Yaklaşık üç yıllık horizon kaldırıldı.
- Rota gerektiğinde **4+ yıl veya daha uzun** sürebilir.
- 4+ yıl countdown değildir.
- Nihai hedef yalnız course completion değil, **professional-readiness seviyesinde verified engineering capability**.
- Final readiness; required mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 full 4+ year curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

Canonical ayrıntı: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Bu nedenle:
- lesson/task completion mastery değildir,
- streak mastery değildir,
- self-confidence mastery değildir,
- AI-assisted output independent mastery değildir,
- takvim süresi professional readiness değildir.

Canonical mastery modeli GRE-v0; retention modeli RVR-v0'dır.

## 4. İngilizce

Başlangıç English seviyesi A0 kabul edilir. English teknik eğitimin ön koşulu değildir; ilk günden paralel ilerler.

Technical task C/Linux/C++ bilgisini ölçüyorsa bilinmeyen English grammar gizli prerequisite olamaz. Uzun vadede docs, GitHub, design docs, CUDA documentation, papers, interview ve global team communication hedeflenir.

## 5. Uygulamanın günlük amacı

Kullanıcı uygulamayı açtığında ana soru:

> **Bugün ne yapmalıyım?**

Sistem current mastery, retention, prerequisite, remediation, assessment ve daily capacity üzerinden günlük plan üretir.

Curriculum sabit takvim değil knowledge graph'tır.

## 6. Mastery / Planner omurgası

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

## 7. Assessment

4A DMA-v0 tamamlandı:
- daily assessment sabit quiz kotası değildir,
- Objective-matched evidence kullanır,
- H0/assistance/provenance/prerequisite guards vardır,
- invalid/ambiguous item kullanıcıya credit/penalty yazamaz,
- coding/debugging/transfer standardı kısa süre uğruna MCQ'ya düşmez.

Aktif adım: **4B — Haftalık sınav**.

## 8. Professional-readiness depth

Yeni kapsam yalnız daha çok konu eklemek değildir. Kritik alanlarda progression:

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

Uzun curriculum ayrıca Git, testing, build systems, profiling, design docs, observability, incident/postmortem thinking, open-source workflow ve technical communication gibi gerçek engineering davranışlarını da kapsamalıdır.

## 9. Uzun curriculum domain envelope

Uzun vadede en az şu katmanlar hedeflenir:

- Technical English
- Computer/Programming Foundations
- C
- Linux/tooling
- DS&A foundations
- Modern C++
- Computer Architecture
- OS/Memory
- Concurrency/Parallel Programming
- Networking
- Distributed Systems
- infra-relevant Storage/Database fundamentals
- Containers/Cloud/Observability foundations
- Performance Engineering
- GPU Architecture
- CUDA
- Triton
- ML/Transformer fundamentals for inference
- LLM Inference Internals
- serving engines / KV cache / batching / scheduling / quantization
- Multi-GPU / NCCL / RDMA concepts
- AI/GPU Infrastructure
- Open Source / Engineering Practice
- Career / Professional Readiness
- Professional Capstones

## 10. V1 ve full curriculum ayrımı

**V1:** çalışan Android product + adaptive learning engine + first 8–12 week production package.

**Full curriculum:** aynı engine üzerinde yıllar boyunca QA edilerek genişletilen professional route.

V1 release ≠ professional curriculum completion.

## 11. İlk iş / bridge rolleri

Nihai hedef AI Infrastructure olsa da ilk iş doğrudan CUDA Engineer olmak zorunda değildir. Uygun bridge alanlar C/C++ development, Systems Software, Linux/Infrastructure, Distributed/Backend Systems, Performance, uygun SRE/Cloud ve ML/AI Infrastructure intern/junior rolleridir.

Product teknik yetkinliği geliştirebilir fakat job offer, salary, seniority veya degree/HR filtrelerini garanti edemez.

## 12. Güncel yürütme konumu

Tamamlanan: Aşama 1, 2, 3 ve 4A.  
Aktif: **4B — Haftalık sınav**.

D-041 scope sync tamamlandı; 4B henüz yürütülmedi. 4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.
