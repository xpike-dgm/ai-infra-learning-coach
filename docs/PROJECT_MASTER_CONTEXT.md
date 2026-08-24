# PROJECT MASTER CONTEXT — Uzun Proje Amacı, Felsefe ve Ürün Tanımı

Bu dosya projenin **uzun biçimli ana bağlam belgesidir**. Sohbet geçmişi kaybolsa veya proje başka bir ChatGPT/coding agent oturumuna taşınsa bile, bu dosya okunarak projenin neden var olduğu, neyi çözmek istediği, hangi deneyimi hedeflediği ve hangi kararların arkasında hangi mantığın bulunduğu yeniden kurulabilmelidir.

**Son büyük kapsam güncellemesi:** 2026-08-24 — D-041 / `docs/PROFESSIONAL_READINESS_TARGET.md`

---

# 1. Projenin Kökeni

Başlangıç problemi, yapay zekâ araçlarının yüksek seviyeli yazılım geliştirme görevlerini giderek daha fazla otomatikleştirmesi karşısında uzun vadeli kariyer için hangi teknik alana yatırım yapılmasının daha dayanıklı olduğuydu.

Seçilen uzun vadeli uzmanlaşma yönü:

**Low-Level Systems → Distributed Systems → GPU/CUDA → AI Infrastructure / ML Systems / GPU Systems**

Ana öğrenme omurgası:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Machine Learning dışlanmaz; fakat ana uzmanlık klasik model eğitmek değildir. ML/transformer bilgisi, inference sistemlerini, tensor hesaplarını, serving ve GPU performansını gerçekten anlayacak derinlikte destek katmanı olarak öğretilir.

---

# 2. 2026-08-24 Kapsam Genişletmesi — Professional Readiness

İlk plan yaklaşık üç yıllık bir öğrenme ufkuna dayanıyordu. Bu sınır kaldırıldı.

Yeni ana hedef:

> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl veya daha uzun sürebilecek kapsamlı, mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek teknik seviyeye taşımak.**

`4+ yıl` bir mezuniyet sayacı değildir. Süre kullanıcının kapasitesine, öğrenme hızına, mevcut bilgisine, remediation/retention ihtiyacına ve yaşam koşullarına göre değişebilir.

Bağlayıcı eşitlik:

```text
elapsed_time != progress
elapsed_time != professional_readiness
professional_readiness = verified capability
```

Kullanıcı 4 yıl uygulamayı açtı diye profesyonel sayılmaz. Tersine gerekli Skill, transfer, debugging, performance ve capstone evidence'ını daha farklı bir sürede tamamlarsa takvim onu gereksiz yere bekletmez.

Ayrıntı: `docs/PROFESSIONAL_READINESS_TARGET.md`.

---

# 3. “Profesyonel Olmak” Bu Projede Ne Demektir?

Bu ürün `profesyonel` kelimesini yalnız içerik coverage'ı veya sertifika anlamında kullanmaz.

Uzun rotanın çıkışında kullanıcı mümkün olduğunca bağımsız biçimde:

- C/C++ ile systems-level kod yazabilmeli,
- Linux üzerinde gerçek debugging/tooling kullanabilmeli,
- memory/OS/concurrency davranışını açıklayıp sorun çözebilmeli,
- networking ve distributed systems trade-off'larını anlayabilmeli,
- performance bottleneck ölçüp profiler/benchmark kullanabilmeli,
- GPU architecture ve CUDA execution/memory modelini uygulayabilmeli,
- Triton/CUDA kernel üretip değerlendirebilmeli,
- LLM inference stack'inin ana bileşenlerini anlayabilmeli,
- KV cache, batching, scheduling, quantization ve serving trade-off'larını test edebilmeli,
- vLLM/SGLang/TensorRT-LLM-benzeri sistemleri kullanmanın ötesinde davranışlarını inceleyebilmeli,
- multi-GPU / multi-node inference temel problemlerini çözebilmeli,
- observability/reliability/capacity yaklaşımı geliştirebilmeli,
- design decision, benchmark ve debugging sonucunu teknik olarak açıklayabilmeli,
- dokümantasyon/source code okuyup tutorial dışı probleme transfer yapabilmeli.

Bu hedef **senior engineer unvanı, iş teklifi veya maaş garantisi değildir**. Gerçek ekip, code review, production incident ve organizasyon deneyiminin tamamı uygulama içinde simüle edilemez. Ürün bunun yerine bu ortamlara girmeye yetecek teknik readiness ve güçlü evidence/portfolio üretmeyi hedefler.

---

# 4. Üniversite / İş Deneyimi Gerçeği

Ürün teknik yetkinliği geliştirebilir fakat şirketlerin diploma veya deneyim filtrelerini kontrol edemez.

Bu nedenle uzun vadeli career-readiness katmanı yalnız “konuları öğrendin” dememeli; mümkün olduğunca şu kanıtları üretmeye yardım etmelidir:

- ciddi GitHub projects,
- reproducible benchmarks,
- systems/GPU case studies,
- capstone artifacts,
- open-source contribution hazırlığı ve katkılar,
- teknik yazı/design docs,
- interview-ready problem solving.

Bunlar diploma filtresini garantiyle aşmaz; ancak teknik yetkinliği görünür hale getirir.

---

# 5. İngilizce Paralel Eğitim Kararı

Başlangıç English seviyesi A0/sıfır kabul edilir.

Kritik karar:

> **English, teknik eğitime başlamadan önce bitirilmesi gereken ayrı bir prerequisite değildir.**

English ve teknik eğitim ilk günden paralel ilerler. Öğretilmemiş grammar/vocabulary teknik assessment'ta gizli prerequisite olamaz.

Uzun vadeli hedef yalnız grammar tamamlamak değil, kullanıcının:

- compiler/terminal messages,
- man pages/docs,
- GitHub issues/PRs,
- design docs/RFCs,
- CUDA/NVIDIA documentation,
- technical papers,
- code review,
- technical interview,
- global team communication

gibi gerçek bağlamlarda çalışabilecek Technical English seviyesine ilerlemesidir.

Exact CEFR progression 6A–6E'de araştırma/curriculum design ile kesinleştirilecektir.

---

# 6. Uygulama Neden Gerekiyor?

Klasik yol haritaları “3 ay C, sonra C++, sonra Linux...” gibi sabit takvim verir. Bu yaklaşım iki temel sorunu çözmez:

1. Kullanıcı bugün ne yapacağını hâlâ kendisi planlamak zorundadır.
2. Takvim ilerlerken gerçek prerequisite/mastery eksikleri saklanabilir.

Uygulamanın günlük sorusu:

> **Bugün tam olarak ne yapmalıyım?**

Sistem bunu current mastery, retention, prerequisite, remediation, assessment ve günlük capacity üzerinden çözmelidir.

Kullanıcı uzun course listesi değil, uygulanabilir günlük çalışma seansı görmelidir.

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

Canonical mastery modeli GRE-v0'dır: yalnız uygun, prerequisite-valid, H0, direct, verified ve bağımsız evidence gereken gate'leri karşılayabilir.

---

# 8. Curriculum Takvim Değil Knowledge Graph'tır

Ana hiyerarşi:

```text
Domain → Module → Topic → Skill → Learning Objective
```

Canonical mastery/prerequisite ana seviyesi Skill'dir; evidence atomik Objective'e bağlanabilir.

Runtime prerequisite mümkün olduğunca `Skill → Skill` çözülür. Hard prerequisite hazır değilse yalnız ona gerçekten bağımlı branch bekler; Linux/English veya başka bağımsız work devam edebilir.

`review_due` forgetting değildir ve tek başına hard lock üretmez.

---

# 9. Adaptif Learning / Planner Motoru

Ana öğrenme döngüsü:

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

- D-033 — hard daily capacity / no task debt
- D-034 — LearningNeed / TaskCandidate / Evidence ayrımı
- PBR-v0 / D-035 — semantic priority bands + deterministic rank
- PRG-v0 / D-036 — prerequisite readiness
- VDW-v0 / D-037 — validated diagnostic waiver
- SRR-v0 / D-038 — state-based re-entry / no absence debt
- PDT-v0 / D-039 — structured decision trace

3H spec simulation sonucu: 16/16 scenarios PASS, 20/20 invariants PASS.

---

# 10. Günlük Kapasite ve 4+ Yıllık Horizon

Uzun curriculum, günlük planı büyütme hakkı vermez.

Kullanıcının ayırdığı günlük süre hard budget'tır. Remediation, retention veya “4 yıllık hedefe yetişme” gerekçesiyle gün otomatik uzatılmaz.

Tamamlanmamış task ertesi gün borç değildir. Current state'ten fresh plan üretilir.

Bu nedenle 4+ yıllık horizon kullanıcıya baskı yapan countdown değil, curriculum depth için açık alan sağlar.

---

# 11. Assessment'ın Rolü

Assessment yalnız not üretmez; future state ve planı değiştirir.

Daily micro assessment DMA-v0'a göre sabit günlük quiz değildir. Gerçek measurement need varsa, Objective'e uygun modality ile capacity içinde seçilir.

Weekly/monthly assessment daha geniş evidence coverage sağlayacaktır; tasarımı Aşama 4'te tamamlanır.

Assessment invalid/ambiguous/prerequisite-contaminated ise kullanıcıya mastery credit/penalty yazamaz.

---

# 12. Retention / Forgetting

Canonical RVR-v0 ilkesi:

> **Zamanın geçmesi negative evidence değildir. Zaman yalnız yeniden doğrulama ihtiyacını artırabilir.**

`review_due` = forgetting değildir.

İlk clean post-mastery contradiction instant unmastery üretmez; `verification_due` ve fresh recheck gerekir. Repeated valid evidence GRE gate'lerini gerçekten düşürürse remediation oluşabilir.

Uzun absence task debt veya mastery cezası değildir.

---

# 13. Remediation

Kullanıcı zorlandığında aynı içeriği körlemesine tekrar etmek yerine sistem uygun müdahaleyi seçmelidir:

- daha sade açıklama,
- farklı mental model/analogy,
- worked example,
- micro-drill,
- coding/debugging,
- prerequisite repair,
- farklı modality,
- fresh independent verification.

Remediation günlük kapasite içine girer; günü otomatik uzatmaz.

---

# 14. AI Tutor'un Rolü

AI öğretmen/feedback katmanıdır, canonical learning state'in sahibi değildir.

AI:
- açıklama,
- hint,
- alternatif örnek,
- root-cause analysis,
- code/open response feedback,
- remediation content,
- comprehension/transfer check

sağlayabilir.

H1–H4 assistance positive independent mastery değildir. AI-generated/copy artifact production mastery yerine geçmez.

---

# 15. Daha Kapsamlı Öğretim İlkesi

Yeni kapsamın anlamı yalnız daha fazla başlık eklemek değildir.

Kritik domain'lerde öğretim mümkün olduğunca şu progression'ı taşır:

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

Örneğin C++ yalnız syntax listesi olmayacak; memory ownership, RAII, concurrency, tooling, debugging, build/test ve performance bağlamına taşınacaktır.

CUDA yalnız kernel syntax olmayacak; execution model, memory hierarchy, profiling, occupancy/bandwidth/latency, correctness ve inference bağlantısıyla öğretilmelidir.

LLM inference yalnız API kullanmak olmayacak; serving engine internals, KV cache, batching/scheduling, quantization, GPU memory/performance ve distributed inference davranışlarına ilerlemelidir.

---

# 16. Professional Engineering Evidence

Uzun rotada yalnız küçük Objective evidence'ı yeterli değildir. Professional-readiness için katmanlı evidence gerekir:

1. **Foundation evidence** — Skill/Objective mastery + retention.
2. **Applied evidence** — user-authored code, debugging, system task, test artifacts.
3. **Integrated systems evidence** — multi-component project ve trade-off reasoning.
4. **Performance/GPU evidence** — profiler, benchmark, CUDA/Triton/inference experiments.
5. **Professional capstone evidence** — realistic constraints altında independent design + implementation + test + profiling + documentation + postmortem/decision explanation.

Exact capstone sayısı şimdiden uydurulmaz; coverage/diversity 5/14/19 aşamalarında tasarlanır.

---

# 17. Uzun Vadeli Curriculum Omurgası

Yeni genişletilmiş alanlar:

1. Technical English
2. Computer Fundamentals
3. Programming Foundations / problem solving
4. C
5. Linux / tooling
6. Data Structures & Algorithms foundations
7. Modern C++
8. Computer Architecture
9. Operating Systems / Memory
10. Concurrency / Parallel Programming
11. Networking
12. Distributed Systems
13. Databases / Storage — infra için gerekli depth
14. Containers / Cloud / Observability foundations
15. Performance Engineering
16. GPU Architecture
17. CUDA
18. Triton
19. ML / Transformer fundamentals needed for inference
20. LLM Inference Internals
21. Serving engines: vLLM / SGLang / TensorRT-LLM style systems
22. Quantization / KV Cache / Batching / Scheduling
23. Multi-GPU / NCCL / RDMA / distributed inference
24. AI Infrastructure / GPU Infrastructure
25. Reliability / capacity / benchmarking
26. Open Source / technical communication
27. Career readiness / technical interview
28. Professional capstone / integrated readiness

Bu bir sabit calendar değildir. Aşama 5 gerçek prerequisite graph yapısını, Aşama 14 ilk production package'ı, Aşama 19 full professional expansion'ı taşır.

---

# 18. V1 Neden Tüm 4+ Yıllık Curriculum'u Beklemiyor?

V1 release'in amacı motorun gerçek çalıştığını kanıtlamaktır.

V1:
- adaptive planner,
- mastery/prerequisite,
- assessment,
- retention/remediation,
- AI Tutor,
- English parallel track,
- local persistence,
- ilk 8–12 haftalık production-quality curriculum

ile release edilebilir.

Full 4+ year professional curriculum V1 ön koşulu değildir.

Bu ayrım iki riski önler:
- uygulama hiçbir zaman release olmadan yıllarca içerik yazmak,
- henüz doğrulanmamış learning engine üzerine dev curriculum inşa etmek.

---

# 19. Gerçek Dünya Çalışma Alışkanlıkları

Professional curriculum zaman içinde şunları da öğretmelidir:

- Git / branches / PR workflow,
- code review,
- tests,
- build systems,
- debugger/profiler,
- documentation,
- issue decomposition,
- design docs,
- reproducible benchmarks,
- logs/metrics/tracing,
- incident/postmortem thinking,
- reliability/security fundamentals,
- open-source source tree okuma ve contribution workflow.

Bunlar yan beceri değil, professional engineering davranışının parçasıdır.

---

# 20. İlk İş ve Kariyer Köprüsü

Nihai hedef AI Infrastructure olsa da ilk işin doğrudan CUDA/Inference Engineer olması zorunlu değildir.

Uygun bridge alanlar:
- C/C++ development,
- Systems Software,
- Linux/Infrastructure,
- Backend/Distributed Systems — systems depth varsa,
- Performance Engineering,
- uygun SRE/Cloud Infrastructure,
- ML/AI Infrastructure intern/junior fırsatları.

Career strategy teknoloji rotasını bozmaz; ilk iş, final hedefe useful engineering experience taşıyan bir köprü olabilir.

---

# 21. Başarı Felsefesi

Bu uygulama yalnız daha fazla içerik tüketimine neden oluyorsa başarısızdır.

Başarılı ürün:
- karar yükünü azaltır,
- doğru prerequisite sırasını korur,
- gerçek mastery'yi ölçer,
- zayıflığı saklamaz,
- retention'ı yeniden doğrular,
- yanlışta remediation üretir,
- AI yardımını provenance ile yorumlar,
- gerçek code/debugging/performance/transfer yaptırır,
- yıllar içinde integrated engineering depth oluşturur,
- kullanıcıyı güçlü professional portfolio/capstone evidence'a taşır.

---

# 22. Geliştirme Felsefesi

Kodlamadan önce behavior/spec katmanı yeterince netleştirilir.

Canonical yürütme:

`PRE-STEP GitHub refresh → gerekirse Research/Coding/QA → spec/implementation → değerlendirme → POST-STEP GitHub sync`

Ana 1–19 yürütme planı korunur. Yeni 4+ yıl kapsamı özellikle Aşama 5, 14 ve 19'u daha derin hale getirir; mevcut 4B assessment tasarım akışı devam eder.

---

# 23. Proje Hafızası Kuralı

Kalıcı source of truth GitHub'dır.

Özellikle:
- `docs/START_HERE.md`
- `docs/PROJECT_MEMORY_PROTOCOL.md`
- `docs/HANDOFF_STATE.md`
- `docs/EXECUTION_INDEX.md`
- `docs/STEP_STATUS.md`
- `docs/DECISIONS.md`
- `docs/PRODUCT_REQUIREMENTS.md`
- `docs/PROFESSIONAL_READINESS_TARGET.md`
- `docs/MASTER_PLAN.md`
- `docs/PROGRESS_LOG.md`

Yeni önemli kararlar yalnız sohbet içinde bırakılmaz.

---

# 24. Güncel Kapsam Özeti

> **AI Infra Learning Coach artık yalnız birkaç yıllık bir roadmap uygulaması değildir. Sıfırdan başlayıp yıllar boyunca kanıt-temelli, adaptif ve kapsamlı biçimde ilerleyen; finalde AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek engineering capability üretmeyi hedefleyen kişisel öğrenme sistemidir.**

> **Takvim hedef değildir. 4+ yıl yalnız esnek horizon'dır. Final gate; mastery + retention + transfer + debugging + performance + integrated capstone evidence'dır.**
