# PROJECT MASTER CONTEXT — Uzun Proje Amacı, Felsefe ve Ürün Tanımı

Bu dosya projenin uzun biçimli **stabil** ana bağlam belgesidir. Sohbet geçmişi kaybolsa bile, projenin neden var olduğu, neyi çözmek istediği ve hangi uzun ömürlü kararların bağlayıcı olduğu buradan yeniden kurulabilmelidir.

**Son büyük kapsam/plan güncellemesi:** 2026-08-25 — D-041 / D-042 / D-044 / D-049  
**Dosya rolü — D-050:** Bu dosya volatile `aktif adım` kaydı tutmaz. Current execution source of truth: `docs/STEP_STATUS.md`, `docs/HANDOFF_STATE.md`, `docs/EXECUTION_INDEX.md`, `docs/MASTER_PLAN.md` ve `PROJECT_CONTEXT.md`.

---

# 1. Projenin Kökeni

Başlangıç problemi, AI araçlarının yüksek seviyeli yazılım geliştirme görevlerini giderek daha fazla otomatikleştirmesi karşısında uzun vadeli kariyer için hangi teknik alana yatırım yapılmasının daha dayanıklı olduğuydu.

Seçilen uzun vadeli yön:

**Low-Level Systems → Distributed Systems → GPU/CUDA → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

Güncel ana öğrenme omurgası:

**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering / Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

ML dışlanmaz; fakat ana uzmanlık klasik model eğitmek değildir. ML/transformer bilgisi inference sistemlerini, tensor hesaplarını, serving ve GPU performansını gerçekten anlayacak kadar destek katmanı olarak öğretilir.

---

# 2. Professional Readiness — D-041

İlk yaklaşık üç yıllık üst ufuk kaldırıldı.

> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl veya daha uzun sürebilecek kapsamlı, mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek teknik seviyeye taşımak.**

```text
elapsed_time != progress
elapsed_time != professional_readiness
professional_readiness = verified capability
```

4+ yıl countdown değildir. Skill, transfer, debugging, performance, retention ve capstone evidence gerçek gate'tir.

---

# 3. “Profesyonel Olmak” Bu Projede Ne Demektir?

Uzun rotanın çıkışında kullanıcı mümkün olduğunca bağımsız biçimde:
- Python/C/C++ ile uygun seviyede engineering code yazabilmeli,
- Linux üzerinde debugging/tooling kullanabilmeli,
- memory/OS/concurrency davranışını açıklayıp sorun çözebilmeli,
- networking ve distributed systems trade-off'larını anlayabilmeli,
- profiler/benchmark ile bottleneck ölçebilmeli,
- GPU architecture ve CUDA execution/memory modelini uygulayabilmeli,
- Triton/CUDA kernel üretip değerlendirebilmeli,
- LLM inference stack'inin ana bileşenlerini anlayabilmeli,
- KV cache, batching, scheduling, quantization ve serving trade-off'larını test edebilmeli,
- vLLM/SGLang/TensorRT-LLM-benzeri sistemleri kullanmanın ötesinde davranışlarını inceleyebilmeli,
- multi-GPU / NCCL / RDMA / distributed inference temel problemlerini anlayabilmeli,
- observability/reliability/capacity yaklaşımı geliştirebilmeli,
- design decision, benchmark ve debugging sonucunu teknik olarak açıklayabilmeli,
- dokümantasyon/source code okuyup tutorial dışı probleme transfer yapabilmeli.

Bu hedef senior title, iş teklifi veya maaş garantisi değildir. Gerçek ekip/production deneyimi ayrıca oluşur.

---

# 4. Python — D-042

Python resmi common foundation dilidir. C/C++ yerine geçmez.

Python:
- programlama temeli,
- automation,
- testing,
- benchmark scripting,
- data/tensor işlemleri,
- PyTorch/ML ekosistemi,
- infrastructure tooling

için kullanılır.

İleri Python; typing, testing, packaging, async/concurrency, multiprocessing, networking, profiling ve infra/ML bağlamına kadar gider.

---

# 5. Üniversite / İş Deneyimi Gerçeği

Ürün teknik yetkinliği geliştirebilir fakat diploma veya deneyim filtrelerini kontrol edemez.

Bu nedenle uzun career-readiness katmanı mümkün olduğunca:
- ciddi GitHub projects,
- reproducible benchmarks,
- systems/GPU case studies,
- capstone artifacts,
- open-source contribution hazırlığı/katkıları,
- teknik yazı/design docs,
- interview-ready problem solving

üretmeye yardım eder.

Bunlar diploma filtresini garantiyle aşmaz; teknik yetkinliği görünür hale getirir.

---

# 6. İngilizce Paralel Eğitim

English teknik eğitime başlamadan önce bitirilmesi gereken prerequisite değildir.

Başlangıç seviyesi A0/sıfır olabilir. Öğretilmemiş grammar/vocabulary teknik assessment'ta gizli prerequisite olamaz.

Uzun hedef; compiler/terminal messages, docs, GitHub issues/PRs, design docs/RFCs, CUDA/NVIDIA docs, papers, code review, technical interview ve global team communication bağlamında iş görebilmektir.

Granular English capability haritası AŞAMA 6C'de; exact English progression, CEFR hedefleri, cadence ve teknik entegrasyon **AŞAMA 7**'de kesinleştirilir.

---

# 7. En Önemli Ürün İlkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Bunun sonucu:
- video izlemek mastery değildir,
- lesson complete mastery değildir,
- streak mastery değildir,
- self-confidence mastery değildir,
- task completion mastery değildir,
- AI'nın kullanıcı adına çözüm üretmesi independent mastery değildir.

Canonical mastery modeli GRE-v0; retention modeli RVR-v0'dır.

---

# 8. Curriculum Takvim Değil Knowledge Graph'tır

Ana hiyerarşi:

```text
Domain → Module → Topic → Skill → Learning Objective
```

Canonical mastery/prerequisite ana seviyesi Skill'dir; evidence Objective'e bağlanabilir.

Runtime prerequisite mümkün olduğunca `Skill → Skill` çözülür. Eksik bir Skill yalnız gerçekten bağımlı branch'i bekletir.

---

# 9. Granular Capability Map — D-044

Ana rotanın yalnız `Python`, `Linux`, `CUDA` gibi geniş başlıklardan oluşması yeterli değildir.

Uygulama, gerçek zayıflığı mümkün olduğunda Skill/Objective düzeyinde görmelidir.

Örnek:

```text
Python overall: learning
  Variables: strong
  Conditionals: mastered
  Loops: remediation_required
    for iteration: weak
    while termination: weak
    break/continue: stable
  Functions: learning
```

Bu yüzden **AŞAMA 6 — Granular Capability Map**, Technical English'ten AI Infrastructure ve capstone'a kadar bütün rotayı `Module → Topic → Skill → Objective` seviyesinde kapsamlı şekilde bölecektir.

AŞAMA 6 ayrıca canonical IDs, prerequisite edges, required/criticality, evidence type, retention relevance, diagnostic/remediation tags, cross-domain reuse, project/capstone mapping ve freshness/version metadata tasarlayacaktır.

Ayrıntı: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

---

# 10. Professional Domain Backbone — D-049

AŞAMA 5A çıktısı `PDM-v0 — Professional Domain Backbone` ile 23 broad route family high-level envelope olarak formalize edildi.

Canonical: `docs/CURRICULUM_DOMAIN_MAP.md`.

Ana ilkeler:
- Technical English parallel track,
- Python/C/Linux early complementary foundation,
- systems → distributed/platform → performance → GPU → inference → multi-GPU → AI/GPU Infrastructure convergence,
- Performance Engineering cross-cutting,
- ML/Transformer supporting domain,
- professional engineering / OSS / projects / capstones route boyunca artan evidence layer,
- security/reliability ve gerekli math/numerical capability hidden prerequisite olamaz,
- vendor/tool adı stable systems concept'in yerine geçmez,
- domain relation authoring guidance'dır; runtime hard prerequisite yine Skill→Skill PRG-v0'dır.

---

# 11. Adaptif Learning / Planner Motoru

```text
App teaches
→ user practices
→ system measures
→ EvidenceEvent
→ GRE/RVR state update
→ prerequisite / Topic state
→ LearningNeed
→ planner priority + capacity
→ next tasks / remediation / retention / verification
```

Planner'ın temel kararları deterministik ve explainable olmalıdır; LLM keyfi olarak mastery/prerequisite/priority yazamaz.

Aşama 3 canonical modelleri:
- D-033 hard daily capacity / no task debt
- D-034 LearningNeed / TaskCandidate / Evidence ayrımı
- PBR-v0 / D-035 semantic priority bands
- PRG-v0 / D-036 prerequisite readiness
- VDW-v0 / D-037 validated diagnostic waiver
- SRR-v0 / D-038 state-based re-entry
- PDT-v0 / D-039 structured decision trace

3H: 16/16 scenarios PASS, 20/20 invariants PASS.

---

# 12. Günlük Kapasite

Kullanıcının ayırdığı günlük süre hard budget'tır. Remediation, retention veya uzun hedef gerekçesiyle gün otomatik uzatılmaz.

Tamamlanmamış task ertesi gün borç değildir; current state'ten fresh plan üretilir.

---

# 13. Assessment — AŞAMA 4 tamamlandı

Assessment yalnız not üretmez; future state ve planı değiştirir.

Canonical assessment foundation:
- DMA-v0 / D-040 — daily micro assessment; sabit günlük quiz değildir.
- WBA-v0 / D-045 — weekly blueprint assessment; tek overall pass-score değildir.
- MCA-v0 / D-046 — monthly capability assessment; broader transfer/integration evidence.
- QAB-v0 / D-047 — versioned trusted AssessmentResource bank.
- AIV-v0 / D-048 — AI-generated resource validation ve use-ceiling policy.

D-044 sonrası blueprint/item attribution granular Skill/Objective IDs kullanır. AI/assessment katmanı GRE/RVR/PRG pipeline'ını bypass etmez.

---

# 14. Retention / Forgetting

> **Zamanın geçmesi negative evidence değildir. Zaman yalnız yeniden doğrulama ihtiyacını artırabilir.**

`review_due` forgetting değildir. First clean contradiction instant unmastery üretmez; verification gerekir.

---

# 15. Remediation

Remediation geniş Domain'i körlemesine tekrar ettirmemelidir. D-044 sonrası müdahale mümkün olduğunca exact Skill/Objective weakness'e hedeflenir.

Müdahaleler:
- sade açıklama,
- farklı mental model,
- worked example,
- micro-drill,
- coding/debugging,
- prerequisite repair,
- farklı modality,
- fresh independent verification.

---

# 16. AI Tutor'un Rolü

AI öğretmen/feedback katmanıdır; canonical learning state'in sahibi değildir.

AI açıklama, hint, alternatif örnek, root-cause analysis, code/open response feedback, remediation content ve comprehension/transfer check sağlayabilir.

H1–H4 assistance positive independent mastery değildir.

---

# 17. Daha Kapsamlı Öğretim İlkesi

```text
conceptual model
→ guided application
→ independent application
→ debugging
→ explanation
→ transfer
→ delayed retention
→ integrated project
→ performance / production context
```

C++ syntax listesiyle, CUDA kernel syntax ile, LLM inference API kullanımıyla sınırlı kalmaz.

---

# 18. Professional Engineering Evidence

1. Foundation evidence — Skill/Objective mastery + retention.
2. Applied evidence — user-authored code, debugging, system tasks, tests.
3. Integrated systems evidence — multi-component projects + trade-offs.
4. Performance/GPU evidence — profiler, benchmark, CUDA/Triton/inference experiments.
5. Professional capstone evidence — independent design + implementation + test + profiling + documentation + postmortem.

Exact capstone sayısı şimdiden uydurulmaz; coverage/diversity AŞAMA 6/15/20'de tasarlanır.

---

# 19. Curriculum Planning / Production Ayrımı

```text
AŞAMA 5 = graph/schema/domain backbone
AŞAMA 6 = detailed capability taxonomy / weakness-addressable map
AŞAMA 15 = first 8–12 week production content package
AŞAMA 20 = full professional content expansion + open source + career + capstones
```

Bu ayrım, henüz taxonomy netleşmeden binlerce lesson/task üretme riskini azaltır.

---

# 20. V1 Neden Full Curriculum'u Beklemiyor?

V1 adaptive planner, mastery/prerequisite, assessment, retention/remediation, AI Tutor, English parallel track, local persistence ve ilk 8–12 haftalık production-quality curriculum ile release edilebilir.

Full 4+ year professional curriculum V1 ön koşulu değildir.

---

# 21. Gerçek Dünya Çalışma Alışkanlıkları

Professional curriculum zaman içinde Git/branches/PR, code review, tests, build systems, debugger/profiler, documentation, issue decomposition, design docs, reproducible benchmarks, logs/metrics/tracing, incident/postmortem, reliability/security ve open-source contribution workflow öğretmelidir.

---

# 22. İlk İş ve Kariyer Köprüsü

Nihai hedef AI Infrastructure olsa da ilk işin doğrudan CUDA/Inference Engineer olması zorunlu değildir.

Uygun bridge alanlar:
- C/C++ development,
- Systems Software,
- Linux/Infrastructure,
- Backend/Distributed Systems — systems depth varsa,
- Performance Engineering,
- uygun SRE/Cloud Infrastructure,
- ML/AI Infrastructure intern/junior fırsatları.

---

# 23. Güncel Stage Mapping — D-044 sonrası

- 1 Product framing
- 2 Learning/mastery
- 3 Adaptive planner
- 4 Assessment system
- 5 Curriculum / knowledge graph backbone
- 6 Granular Capability Map
- 7 English parallel line
- 8 UX
- 9 Architecture / Data Model
- 10 Mobile Skeleton
- 11 Daily Learning MVP
- 12 Mastery / Planner implementation
- 13 Assessment / Retention / Remediation implementation
- 14 AI Tutor / Evaluation
- 15 First 8–12 week production content
- 16 Analytics / Settings
- 17 Polish / Accessibility
- 18 Pilot / Calibration / QA
- 19 Release APK
- 20 Full Professional Curriculum / Career / Capstones

D-043 standalone specialization AŞAMA 20 kararı geri çekilmiştir.

---

# 24. Geliştirme Felsefesi ve Execution State

Canonical yürütme:

`PRE-STEP GitHub refresh → gerekirse Research/Coding/QA → spec/implementation → değerlendirme → POST-STEP GitHub sync → repo-wide stale-reference audit`

**Bu stabil dosya aktif adımı hardcode etmez.** Güncel execution state için:
- `docs/STEP_STATUS.md`
- `docs/HANDOFF_STATE.md`
- `docs/EXECUTION_INDEX.md`
- `docs/MASTER_PLAN.md`
- `PROJECT_CONTEXT.md`

okunmalıdır.

---

# 25. Proje Hafızası Kuralı — D-024 / D-027 / D-050

GitHub durable source of truth'tur. Her numaralı adımın PRE/POST senkronu zorunludur.

D-050 ile yaşayan dosya rol matrisi ve stale-reference kontrolü `docs/PROJECT_MEMORY_PROTOCOL.md` içinde bağlayıcı hale getirilmiştir. `PROJECT_CONTEXT.md` kısa current snapshot, `START_HERE` bootstrap, `HANDOFF_STATE` detailed current handoff, `STEP_STATUS` hızlı current state; bu dosya ise uzun/stabil bağlamdır.

---

# 26. Güncel Kapsam Özeti

> **AI Infra Learning Coach sıfırdan başlayıp yıllar boyunca kanıt-temelli, adaptif ve kapsamlı biçimde ilerleyen; finalde AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek engineering capability üretmeyi hedefleyen kişisel öğrenme sistemidir.**

> **Takvim hedef değildir. Final gate; granular Skill/Objective mastery + retention + transfer + debugging + performance + integrated capstone evidence'dır.**