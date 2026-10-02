# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

**Local manager takeover — D-055:** Repo yerel çalışan ana yönetici agent'a devrediliyorsa root `AGENTS.md` ve `docs/LOCAL_MANAGER_HANDOFF.md` bu dosyayla birlikte ilk bootstrap setidir. Local takeover sırasında repo içindeki tüm Markdown dosyaları ayrıca tamamen okunmalıdır.

## 1. Bu repo ne için var?
Tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, curriculum yönünü ve geliştirme planını kalıcı tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Sistem sabit kurs takvimi değil; gerçek Skill state, prerequisite, retention, evidence ve günlük capacity'ye göre plan üretir.

## 2. Güncel uzun vadeli hedef — D-041
> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability seviyesine taşımak.**

4+ yıl countdown değildir. Professional readiness mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ile belirlenir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

V1 full 4+ year curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

## 3. Güncel rota/plan kararları

### D-042 — Python foundation
Python common technical foundation'ın resmi parçasıdır; C/C++ yerine geçmez.

### D-043 — geri çekildi
Standalone specialization-stage yorumu canonical değildir.

### D-044 — Granular Capability Map
AŞAMA 6 ana öğrenme rotasının her büyük alanını `Domain → Module → Topic → Skill → Learning Objective` seviyesinde ayrıntılandıracak. Amaç broad weakness yerine exact capability localization/remediation.

Canonical: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### D-045 — WBA-v0
Weekly assessment blueprint-before-items; no fixed score/time/quota; granular evidence.

### D-046 — MCA-v0
Monthly assessment longitudinal state-based capability sampling; broader transfer/integration; no cumulative final/pass score.

### D-047 — QAB-v0
Question Bank yalnız MCQ değil, versioned AssessmentResource bank'idir. Stable logical ID + immutable version, lifecycle/use ceiling, exact target/prerequisite/evidence metadata, family/dependency/context/freshness ve bounded selection vardır.

### D-048 — AIV-v0
AI-generated assessment resource candidate olarak başlar; correctness/ambiguity/prerequisite/evaluator/freshness/safety validation olmadan trust/use-ceiling promotion yoktur.

### D-049 — PDM-v0 Professional Domain Backbone
Canonical: `docs/CURRICULUM_DOMAIN_MAP.md`.

- 23 ana route family high-level professional envelope olarak kilitlendi.
- Technical English parallel track.
- Python + C + Linux/Git/Shell complementary early foundations; DS&A supporting foundation.
- Systems core → distributed/platform → performance → GPU/accelerator → inference → multi-GPU → AI/GPU Infrastructure convergence yapısı.
- Performance route boyunca cross-cutting capability.
- ML/Transformer inference için supporting domain; generic ML research specialization değil.
- Open Source / engineering practice / projects / capstones route boyunca artan professional evidence layer.
- Security/reliability ve gerekli math/numerical capability hidden prerequisite bırakılamaz.
- Tool/vendor adı stable systems concept'in yerine geçmez.
- Domain-level ilişkiler authoring guidance; runtime hard prerequisite Skill→Skill PRG-v0.

### D-050 — Living memory sync + stale-reference audit
Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md`.

- Her numaralı adım sonunda yaşayan state dosyaları istisnasız kontrol edilir.
- `PROJECT_CONTEXT.md` kısa current snapshot olarak eski step'te bırakılamaz.
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` mandatory POST-check setindedir.
- Her step kapanışında stale active-step, old stage number, deleted/renamed file ve superseded decision/model referansları repo-wide taranır.
- Stable specs active step'i kopyalamaz; yalnız davranış/cross-reference değişirse güncellenir.

### D-051 — KGC-v0
5B final graph contract `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` içinde versioned curriculum identity, Topic↔Skill placement, Skill prerequisite, Objective evidence profile, scope-relative requirement, retention/remediation/English/professional attribution ve conservative graph migration semantics'ini kilitledi.

### D-052 — FBB-v0
5C final V1 foundation backbone `docs/V1_FOUNDATION_BACKBONE.md` içinde zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English seed subgraph'ını tanımladı. 8–12 hafta calendar gate değildir; seed IDs 6A/6C ratification öncesi learner-published değildir.

### D-053 — GQA-v0
5D final graph architecture QA `docs/GRAPH_ARCHITECTURE_QA.md` içinde FBB seed graph'ı cycle/dead-end/hidden prerequisite/duplicate/reuse/English-global-gate/reachability açısından doğruladı; blocking structural sorunları corrective patch ile düzeltti ve AŞAMA 5'i kapattı.

### D-054 — GNS-v0
6A final `docs/GRANULARITY_NAMING_STANDARD.md` standardı Domain/Module/Topic/Skill/Objective semantic sınırlarını, Skill atomization testini, under/over-fragmentation guard'larını, shared-vs-specific capability split'ini, stable logical ID convention'ını ve FBB seed ratification/refactor lifecycle'ını kilitledi.

### D-055 — Local manager takeover
Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. GitHub durable source of truth, D-024/D-027/D-050 PRE/POST protokolü ve Research/Coding/Test bağımsızlığı değişmez. Canonical bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`. Bu transition kendi başına 6B'yi yürütmedi; sonraki kullanıcı onaylı numbered work normal protokolle ilerledi.

### D-056 — FRDB-v0
6B final `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` contract'ı 23 route family'yi 6C–6F package'larına atadı; ortak machine-readable authoring collections, duplicate/reuse, prerequisite, FBB mapping, source/freshness, review queue ve QA sözleşmesini kilitledi.

### D-057 — FDM-v0
6C final `docs/FOUNDATIONS_DETAILED_MAP.md` + `curriculum/decomposition/6c_foundations/` package'ı D01–D05'i 132 Skill / 137 Objective seviyesine ayırdı; FBB 41/47 mapping complete, hard graph DAG ve internal QA PASS.

### D-058 — SDM-v0
6D final `docs/SYSTEMS_DETAILED_MAP.md` + `curriculum/decomposition/6d_systems/` package'ı D06–D13'ü 192 Skill / 207 Objective seviyesine ayırdı; 43 accepted 6C Skill clone'lanmadan reuse edildi, 6C+6D birleşik hard graph DAG 324/324 ve internal QA PASS.

### D-059 — GIM-v0
6E final `docs/GPU_ML_INFERENCE_DETAILED_MAP.md` + `curriculum/decomposition/6e_gpu_ml_inference/` package'ı D14–D22'yi 143 Skill / 159 Objective seviyesine ayırdı; 59 prior Skill clone'lanmadan reuse edildi, FRDB hard/soft Pass-B audit PASS ve 6C+6D+6E combined hard graph DAG 467/467.

### D-060 — PEM-v0
6F final `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md` + `curriculum/decomposition/6f_professional_engineering/` package'ı D23 professional engineering/OSS/project-capstone layer'ını 76 Skill / 87 Objective seviyesine ayırdı; prior technical capability'ler clone edilmeden reuse edildi ve 6D/6E professional overlay review'ları kapatıldı.

### D-061 — WLRM-v0
6G final `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/` Objective-first weakness/remediation modelidir; 6H patch sonrası final registry için 549 Skill / 608 Objective exact coverage taşır.

### D-062 — S6ERQA-v0
6H final `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`; üç bağımsız evaluator reconcile edildi, corrective patch sonrası Stage 6 549 Skill / 608 Objective / 950 edge ve 549/549 hard DAG ile external Research QA PASS oldu.

### D-063 — EED-v0
7A final `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`; D01 15 Skill / 15 Objective / 16 hard edge için prerequisite-aware granular entry diagnostic, 15 task family ve no-premature-CEFR guard'ı kilitlendi.

### D-064 — TECP-v0
7B final `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`; D01 15 Skill'i 5 A1 + 5 A2 + 5 B1 context-only Technical English anchor'a bağlar, 4 bounded B2+ professional extension tanımlar ve CEFR mastery/certification overclaim'ini yasaklar.


### D-065 — DECP-v0
7C final `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`; Technical English common capacity içinde parallel candidate opportunity olarak çalışır, fixed minute/percentage/streak/debt yoktur ve state-driven task mix kullanır.

### D-066 — TEIP-v0
7D final `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`; dört construct-aware integration mode, component attribution, bidirectional contamination guard ve evidence-driven reversible scaffold davranışını kilitler.

### D-067 — TEPM-v0
7E final `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`; exact 15 D01 Skill state'ini 8 derived learner-facing presentation state ve qualified A1/A2/B1 Technical English profile'a projekte eder; B2+ per-capability evidence'dır, general-English/official CEFR veya numeric aggregate değildir.

### D-068 — UXIA-v0
8A final `docs/INFORMATION_ARCHITECTURE_SPEC.md`; Today/Learn/Progress/Profile semantic shell, shared detail surfaces ve focused flows kilitlendi.

### D-069 — THUX-v0
8B final `docs/TODAY_HOME_SCREEN_SPEC.md`; Today/Home action-first hierarchy, current PlannedTask queue, hard-capacity context, PDT-v0 reason projection ve truthful empty/degraded states kilitlendi. Fixed visual geometry sonraki adımlardadır.

### D-070 — TRUX-v0
8C final `docs/DAILY_WORKING_FLOW_SPEC.md`; Task Runner execution-surface sınırı, emergent ungraded working session, shared focused-flow frame, deterministic entry/resume revalidation, non-punitive assistance escalation, asked-not-inferred provenance, in-flight replan koruması ve `evaluation_pending` truthfulness kilitlendi. Visual sistem 8F/8G'ye bırakıldı.

### D-071 — ASUX-v0
8D final `docs/ASSESSMENT_SESSION_UX_SPEC.md`; üç scope için tek assessment session interior, atomic evidence boundary submission, frozen submitted boundary, skip != incorrect, disclosed independence/tools, non-punitive in-session assistance, beş koşullu slot recomposition, contested item dispute, provisional/invalid güvenliği ve semantic result kilitlendi. Pass/fail banner, grade, threshold ve gradebook yasak.

### D-072 — SPWX-v0
8E final `docs/PROGRESS_SKILL_UX_SPEC.md`; TEPM-v0'dan genelleştirilen tek 8-state Skill vokabüleri, qualifier olarak `at_risk`, sıralanan fakat çökertilmeyen multi-axis truth, kilitlenmiş 8 Skill + 6 Topic etiketi, inventory-only counting, hypothesis != deficiency, `remediation_task_completed != remediation_closed`, streak olmayan learning history ve gradebook olmayan longitudinal assessment_report kilitlendi. Mastery yüzdesi, competence ratio, career bar, level/rank ve streak calendar yasak.

### D-073 — VDSX-v0
8F final `docs/DESIGN_SYSTEM_SPEC.md`; design system expression layer'dır (`visual_severity <= canonical_severity`). 6 tone; 46 surface + 8 Skill + 6 Topic + 4 qualifier state eksiksiz eşlendi; `system_fault` yalnız gerçek arızaya izinli; attention grubu tone yükseltmez. WCAG çapalı kontrast (tema başına ölçüm), en az 48dp hedef, %200 metin, Türkçe casing koruması, ikna edici olmayan motion ve competence progress-bar yasağı kilitlendi. Somut hex 8G/10'da ölçülerek üretilir.

### D-074 — WFPX-v0
8G final `docs/WIREFRAME_PROTOTYPE_SPEC.md`; concrete geometry ve ölçülmüş palet kilitlendi ve **AŞAMA 8 kapandı**. 3 window class, 6 surface region geometry'si sahibi spec'lere karşı doğrulanmış, focused-flow exit 48dp sabit, 52 kontrast çifti hesaplanarak ölçülmüş (min 6.08 metin / 3.79 non-text), `attention` menekşe ve kırmızı yalnız `system_fault` — traffic-light rampası yok. `prototype.html` bağlayıcı değildir.

### D-075 — AMTS-v0
9A final `docs/MOBILE_TECHNOLOGY_SPEC.md`; platform ve UI teknolojisi seçildi. Android native (V1'de cross-platform UI katmanı yok), Kotlin + Jetpack Compose, Material 3 yalnız substrate ve **dynamic colour kapalı**, domain core saf Kotlin ve Android/UI/network/AI bağımsız, window class'lar WFPX-v0 ile birebir, default-locale case transform yasak, `minSdk` politika olarak tanımlı. Framework güncelliği 10A'daki 6 maddelik bounded verification list'e bırakıldı.

### D-076 — LFPS-v0
9B final `docs/LOCAL_FIRST_PERSISTENCE_SPEC.md`; local-first persistence kilitlendi. Kanıt source of truth, öğrenci state'i yeniden hesaplanabilir projeksiyon; truth kayıtları append-only; storage engine SQLite; persistence interface'leri core-owned; curriculum ve user state ayrı versiyonlanır; exposure kayıtları kalıcı ve kaybı veri kaybıdır; bir eylem bir transaction; migration forward-only; restore atomik ve doğrulanmış; bozulma `data_recovery_required` yüzeyler ve sessiz reset yasaktır; V1'de kanıt budanmaz. ORM library 10A'ya, physical schema 9C'ye bırakıldı.

### D-077 — DDM-v0
9C final `docs/DOMAIN_DATA_MODEL_SPEC.md`; domain veri modeli kilitlendi. Schema mimariyi uygular: üç store bölgesi, `(logical_id, version)` composite kimlik ve yapısal pinning, truth tablolarında UPDATE yolu olmayan append-only tasarım, dört ayrı evidence ekseni, append edilen disposition, her timestamp'te instant + study day + offset, watermark'lı projection provenance, seçim yolunda indekslenen exposure ve library-neutral physical schema. ORM 10A'ya, boundary'ler 9D'ye bırakıldı.

### D-078 — MSBX-v0
9D final `docs/SERVICE_BOUNDARIES_SPEC.md`; modül ve servis sınırları kilitlendi. 10 modül, içe-doğru dependency kuralı (`core-*` asla `data-*`/`ai-*`/`app-*`'e bağımlı olamaz), 4 port, port olarak saat, core'da rastgelelik yok, ürünle sevk edilen null evaluator, engine başına tek state ailesi, `core-application`da transaction sınırı ve core'da presentation projection. DI/build 10A'ya, AI davranışı 9E'ye bırakıldı.

### D-111 — PRVX-v0
14G final `docs/PROVIDER_ADAPTER_IMPL_SPEC.md`; sağlayıcı adaptörü kodda ve **AŞAMA 14 kapandı**. Sağlayıcı portların arkasında değiştirilebilir bir ayrıntıdır: yalnız öğrencinin bu cihazda şifreli saklanan kendi anahtarı bir çağrı yapabilir; anahtar yoksa cihazdan hiçbir şey çıkmaz ve üründe hiçbir şey çalışmayı bırakmaz; gönderilen tam olarak core'un kurduğudur — talimatları, mesajı, şeması — ve sağlayıcının söylediği hiçbir şey tek bir şemaya tam uyan nesne olmadıkça cevap olmaz; red red'dir, zaman aşımı çağrıyı bitirir ve öğrencinin arkasından hiçbir şey yeniden denenmez. OpenAI; tek sağlayıcı ve tek tip düşüş; anahtar ekranı Profile'da (kullanıcı kararları); şema değişmedi. 129/129 QA PASS, mutation 41/41. T6 ve canlı çağrı çalıştırılmadı.

### D-110 — OREX-v0
14F final `docs/OPEN_RESPONSE_EVALUATION_IMPL_SPEC.md`; açık uçlu cevap değerlendirme kodda. Serbest metin bir cevap dersin doğru cevabın neyi içerdiğini söylediği şeye göre değerlendirilir, nasıl kulağa geldiğine göre değil: kısa cevap dersin kabul edilen cevap listesiyle doğrulanır; uzun cevap rubric'iyle, kriter kriter değerlendirilir — AI yalnız her kriterin karşılanıp karşılanmadığını söyleyebilir, bunun her Objective için ne anlama geldiğine core karar verir ve AI'ın kararı asla provisional'dan fazlası değildir; doğrulanmış sonuç isteyen görev AI'a hiç sorulmaz; hiçbir şey cevap vermezse cevap bekler, hiçbir şey yazılmaz, öğrenci rubric'le kendi cevabını kontrol edebilir ve yalnız öğrenci isterse yeniden değerlendirilir. Kısa cevap kabul edilen listeyle; AI yoksa bekler + öz-kontrol, yalnız istenince yeniden (kullanıcı kararları); şema değişmedi. 117/117 QA PASS, mutation 49/49. T6 çalıştırılmadı.

### D-109 — ACCX-v0
14E final `docs/CODE_COMPREHENSION_IMPL_SPEC.md`; AI yazımı kod anlama kontrolü kodda. Başkasının yazdığı kodun çalışması öğrenci hakkında hiçbir şey kanıtlamaz; onu açıklayabilmek anlamayı kanıtlar, üretimi değil: öğrenci AI'ın ya da başka bir kaynağın yazdığı ya da büyük ölçüde gösterilmiş bir çözümün verdiği kodu gönderdiğinde hemen ardından bir anlama kontrolü sunulur ve geçilebilir; önce yazılmış kontroller gelir ve cevap anahtarıyla değerlendirilir, yalnız yazılmış kontrol yoksa tutor öğrencinin kendi kodu hakkında soru sorar ve bu pratiktir, kanıt değildir; doğru cevap yazarının beyan ettiği türde kanıttır, item'ın kendi üretimi asla değildir, ve öğrencinin yazmadığı kod üretim Objective'i için bağımsız yeniden kontrol açar. Önce yazılmış, yoksa tutor pratiği; hemen sonra, isteğe bağlı; AI yazımı kod yeniden kontrol açar (kullanıcı kararları); şema değişmedi. 112/112 QA PASS, mutation 43/43. T6 çalıştırılmadı.

### D-108 — CDEX-v0
14D final `docs/CODE_EVALUATION_IMPL_SPEC.md`; kod değerlendirme kodda. Kod çalıştırılarak değerlendirilir, yoksa yalnız bir görüştür: bir kod görevi yalnız dersin kendi testleriyle doğrulanır — öğrencinin bilgisayarında koşulur ve raporu katı okunur — ve bir test yalnız yazıldığı Objective için konuşur; çalışmayan test hiçbir şey ölçmemiştir ve öğrenciye karşı sayılmaz; test yoksa AI kodu yalnız görev provisional sonuca izin veriyorsa ve en çok provisional olarak değerlendirir, doğrulanmış sonuç isteyen görev AI'a hiç sorulmaz; testlerin geçmesi kodun istenen şekilde çalıştığını gösterir, öğrencinin nedenini açıklayabildiğini değil. Testler PC'de koşar ve rapor içe aktarılır; test yoksa AI yalnız provisional (kullanıcı kararları); şema değişmedi. 122/122 QA PASS, mutation 49/49. T6 ve C derlemesi çalıştırılmadı.

### D-107 — ALEX-v0
14C final `docs/ALTERNATIVE_EXPLANATION_IMPL_SPEC.md`; alternatif anlatım kodda. Anlatım tutmadığında yöntem değişir, kapsam ve doğruluk değişmez: biçimi öğrenci seçer, önce doğrulanmış yazılı anlatım gösterilir ve yalnız yazılmış olan yoksa tutor yazar — dersin kendi anlatımına dayanarak, onunla çelişmemesi söylenerek, doğrulanmamış diye etiketlenerek ve asıl anlatıma dönüş her zaman bir adım uzakta; öğrencinin kendi durumunu gerektiren bir biçimi AI asla yazmaz. Biçimi öğrenci seçer; önce yazılmış, yoksa AI (kullanıcı kararları); `tutor_instructions/2`; şema değişmedi. 123/123 QA PASS, mutation 42/42. T6 çalıştırılmadı.

### D-106 — WAAX-v0
14B final `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md`; yanlış analizi ve misconception hafızası kodda. Yanlış bir cevap bir bilgidir, öğrenci hakkında bir hüküm değil: misconception etiketi yalnız taşıdığı kanıt ve kaynağı kadar güçlüdür — zayıflık motorunun kendi kuralı Objective'i ne kadar taşıyorsa o kadar ilerler, AI'ın önerisi asla hipotezin üstüne çıkmaz, Objective için beyan edilmemiş etiket hiç saklanmaz ve hipotez öğrenciye yalnız soru olarak sorulur. Kapalı katalog curriculum'da (kullanıcı kararı), hafıza `WLRM-v0` ailesinde, şema v8; hipotez yalnız açık soru (kullanıcı kararı). 154/154 QA PASS, mutation 51/51. T6 çalıştırılmadı.

### D-105 — TUTX-v0
14A final `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md`; tutor davranış sözleşmesi kodda ve **AŞAMA 14 başladı**. Tutor istek üzerine öğretir ve asla karar vermez: yalnız sorulana cevap verir, öğrencinin seçtiğinden fazlasını açmaz, öğrencinin ne yapabildiğini iddia etmez ve gerçekten gösterdiği her yardım olduğu gibi kaydedilir — göstermediği hiçbir şey kaydedilmez ve söylediği hiçbir şey kanıt değildir. Ayrı `TutorPort` (D-105 beyanlı uzantı, 9D düzenlenmedi); deneme açıkken seviyeyi öğrenci seçer; kaydedilen yardım kanıtın bağımsızlığını belirler; gösterilen çözüm exposure ve sonraki kanıt onu okur; ilk gerçek çağrı 14G. 219/219 QA PASS, mutation 68/68. T6 çalıştırılmadı.

### D-104 — VDWX-v0
13F final `docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md`; tanısal atlama kodda ve **AŞAMA 13 kapandı**. Bir tanısal yol mastery'ye giden daha kolay bir yol değildir, aynı kanıtı daha erken toplar: tanısal yolu yalnız öğrenci açar, beyan kanıt değildir; waiver yalnız `GRE-v0` kapıları tanısal kanıtta ilk kez geçince verilir ve kapsamdır, mastery değil; yardım ya da temiz kaçırma hızlı yolu suçsuz bitirir ve öğretilmemiş şeyi bilmemek zayıflık değildir; tanı sürerken ders bekler, yalnız gösterilen atlanır; planlama kanıt okumaz. Şema v7 `diagnostic_coverage`; S06 ve invariant 12 koşuyor. 220/220 QA PASS, mutation 69/69. T6 çalıştırılmadı.

### D-103 — PCRX-v0
13E final `docs/PROGRAM_CHANGE_REPORT_IMPL_SPEC.md`; program değişiklik raporu kodda. Bir rapor iki okumanın farkıdır: dokunulan Skill kendi motorlarıyla yeniden hesaplanır, değişiklik yalnız bir eksen gerçekten hareket ettiyse söylenir, yazılmamış durum 'önce' değildir, vadesi gelen tekrar değişiklik değildir, çelişki doğrulamadır, plan farkı iki kayıtlı sürüm arasındadır ve yalnız durum değiştiyse planner yeniden plan yapar. `objectivesOf`, şema değişmedi. 162/162 QA PASS, mutation 42/42. T6 çalıştırılmadı.

### D-102 — WLRX-v0
13D final `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md`; remediation motoru kodda. Başarısız bir deneme başarısız bir Skill değildir: 12 atıf kuralı Objective düzeyinde, yardım en çok hipotez, doğrulama ve kapanış mastery kapılarını izler, biten görev kapatmaz; motor `weakness_detected` sağlar; dispozisyonlar okunur ve geriye dönük contamination yalnız bağımlı slotları düzeltir; Topic → 16C, boşluk politikası → 18D; şema v6. 165/165 QA PASS, mutation 44/44. T6 çalıştırılmadı.

### D-101 — RVRX-v0
13C final `docs/RETENTION_IMPL_SPEC.md`; spaced repetition kodda. Zamanın geçmesi negatif kanıt değildir: retention kanıttan, her satırdaki mastery kararıyla yeniden oynatılır, ilk temiz hata doğrulama açar ve silmez, taze yeniden kontrol aralığı büyütmez, `review_due` yalnız günden türetilir ve planner plan öncesi yeniler; V0 sayıları `RVR-v0` §20 (18C); şema v5. 164/164 QA PASS, mutation 47/47. T6 çalıştırılmadı.

### D-100 — MCAX-v0
13B final `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md`; aylık sınav kodda. Bir ay daha geniş bir penceredir, daha ağır bir sınav değil: 13A kontratı tek ortak kontrata genelleştirildi (haftalık değer değişmedi), kritik Skill yalnız nedenle yeniden doğrulanır, aylık etiket ağırlık eklemez, slotlar planner'ın alternatif adayı; takvim ayı, önceki oturum borç değil; transfer/kontrol noktası üreticisi uydurulmadı (15); şema v4. 259/259 QA PASS, mutation 48/48. T6 çalıştırılmadı.

### D-099 — 13F eklendi
Kullanıcı kararıyla AŞAMA 13'ün sonuna `13F — Tanısal atlama (VDW-v0)` eklendi; mevcut adımlar yeniden numaralanmadı.

### D-098 — WBAX-v0
13A final `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md`; haftalık sınav kodda. Bir hafta bir kimliktir, kota ya da son tarih değil: havuz planner'ın ihtiyaçları, bir Skill tek kez, slot yalnız güvenilir ve taze item'la, slotlar planner'ın adayı ve haftanın kendi dakikası yok; döngü ISO hafta (kullanıcı onayladı), kaçırılan hafta borç değil; puan yok; şema v3 `assessment_session.blueprint`. 210/210 QA PASS, mutation 42/42. T6 çalıştırılmadı.

### D-097 — VUSX-v0
12F final `docs/VIRTUAL_USER_TESTS_SPEC.md`; 3H'nin sanal kullanıcıları gerçek kodla koşuldu. Sanal kullanıcı durumdur, cevap değil: ihtiyaç durumdan, karar gerçek kapıdan, plan gerçek planner'dan, açıklanan iz planner'ın yazdığından gelir. 16 senaryodan 15'i koşuldu; S06 (`VDW-v0`) koşulamıyor ve 13'e bağlandı. Dönen öğrencinin due envanteri açıklamada artık tek satır. 148/148 QA PASS, mutation 27/27. T6 çalıştırılmadı. **AŞAMA 12 kapandı.**

### D-096 — RSNX-v0
12E final `docs/PLANNER_EXPLANATION_IMPL_SPEC.md`; açıklama kuruldu. Açıklama karar izinin projeksiyonudur: her cümle izin kaydettiği bir katalog koduna ya da iz olgusuna dayanır ve başka türlü kurulamaz. Katalog `PDT-v0` §8 + `PRG-v0` §20. İz adayların ilgili Skill'lerini kaydeder (`planner_trace/3`). Today planı okuyor; iz kendi satırlarını anlatmıyorsa plan okunamaz ve tahmin edilmez; korunan iş yeniden başlatılmaz. Süreye sığmayan iş daha az önemli değildir, bekleyen iş blocker'ını adlandırır, `review_due` unutmak değildir, yokluk borç değildir. 219/219 QA PASS, mutation 52/52. T6 çalıştırılmadı.

### D-095 — RPLX-v0
12D final `docs/REPLAN_SPEC.md`; replan kuruldu. Bir plan düzenlenmez, gerekçesi olan yeni bir sürümle değiştirilir: plan yoksa initial, başka günün planıysa re-entry, bugünün planı ve bir olay varsa replan; aynı gün olay yoksa hiçbir şey yazılmaz. Başlanan iş korunur (çağıran bildirir, önceki plana karşı doğrulanır), yalnız kalan yeniden çözülür, gün kendiliğinden büyümez. Re-entry dünkü planı oynatmaz; yokluk borç, başarısızlık ya da çürüme değildir. Güvenli duraklatma P2, high-stakes duraklatma devam ettirilmez. İz `planner_trace/2`. 157/157 QA PASS, mutation 33/33. T6 çalıştırılmadı.

### D-094 — PLNX-v0
12C final `docs/PLANNER_ENGINE_IMPL_SPEC.md`; planner kuruldu. Önce semantik öncelik, sonra fiziksel sığma: priority bloklanmış ya da güvenilmeyen görevi kurtaramaz, kapasite önceliği yeniden yazmaz, gün uzatılmaz, sığmayan ihtiyaç 'daha az önemli' diye etiketlenmez ve borç değildir. Bantlar ve rank vektörü `PBR-v0`, toplanmaz, rastgele tie-break yok, kritik etiket tek başına P0 değil. İhtiyaçlar her motorun kendi ekseninden. Starvation eşiği ve reason kodu uydurulmaz. Plan tek transaction'da truth; `planned_task`ın taşıyamadığı her şey `planner_trace/1` izinde. 226/226 QA PASS, mutation 55/55. T6 çalıştırılmadı.

### D-093 — PRQX-v0
12B final `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md`; prerequisite kapısı kuruldu. Bir eksik prerequisite yalnız gerçekten ona bağlı işi bekletir: `review_due` bloklamaz, soft eksik kilitlemez, task'in kendi gereksinimi hard'dır, priority kapıyı aşamaz ve bekleyen ihtiyaç başarısız değildir. Readiness dört değerlidir ve sayı değildir; değerlendirilmemiş eksen adlandırılır, kötü haber sayılmaz; kapı eksik bilgide kapalı kalır. Authored 950 kenarın hepsi `draft` bulundu ve metadata sorunu olarak adlandırılıyor. Bekleyen aday üzerindeki iş `contaminated` yazılır. Yalnız `prerequisite_readiness` yazılır; `skill_state` birleştirmesi 12D'de. 184/184 QA PASS, mutation 44/44. T6 çalıştırılmadı.

### D-092 — MSTX-v0
12A final `docs/MASTERY_ENGINE_IMPL_SPEC.md`; ilk engine kuruldu ve **AŞAMA 12 başladı**. Mastery tek bir soru sorar: yardımsız yapabiliyor mu? Yardımlı, görülmüş, doğrulanmamış, itirazlı ve bozuk prerequisite üzerindeki kanıt skora girmez ve bu ceza değildir. Bağımlı grup tek gruptur, pencere son beş gruptur, ortalama eşit ağırlıklıdır; Skill ancak her required ve critical Objective kendi başına geçerse mastered olur; ilk çelişki doğrulama açar, silmez; ölçülemeyen cevap sıfır değildir; projeksiyon kanıttan yeniden kurulur, truth yazmaz ve yalnız kendi eksenini yazar. Sabitler `GRE-v0`ün kalibre edilmemiş sezgileridir (18C). 164/164 QA PASS, mutation 33/33. T6 çalıştırılmadı.

### D-091 — EODX-v0
11E final `docs/END_OF_DAY_SPEC.md`; gün sonu kilitlendi ve **AŞAMA 11 kapandı**. Gün sonu bir hüküm değil, zamanda bir sınırdır: gün çalışma günü değiştiği için kapanır ve kimsenin durumu değişmez. Sayımlar etiketli envanterdir (oran/yüzde/hedef yok) ve ilerleme değildir; değişiklik ancak kanonik bir engine bildirdiyse iddia edilir; okunamayan sayım sıfır sayılmaz; boş gün nötrdür; günler arası boşluk çizilmez; yarına borç geçmez; satırın günü instant'tan yeniden hesaplanmaz. Yeni surface yok, grafik yok. 128/128 QA PASS, mutation 20/20. T6 çalıştırılmadı.

### D-090 — DMAX-v0
11D final `docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md`; bir ölçüm baştan sona dürüst çalışır hâle geldi. Curriculum tek yazma yolundan tek transaction'da yayımlanır ve yayımlanmış sürüm asla üzerine yazılmaz; authored paket katı ayrıştırılır ya da hiç sunulmaz. Güven mağazanın validation kaydıdır; etkin tavan en kısıtlayıcı kuraldır ve deklare edileni aşamaz; kanıt uyumuna Objective karar verir; exposure yalnız gerçekten gösterildiğinde yazılır. Tek interior bütün scope'lara hizmet eder: gönderilen sınır donar, boş bırakmak yanlış değildir, yardım engellenmez, sonuç semantiktir ve puan alanı yoktur. Kısa artifact gövdesi referansın içinde taşınır ya da reddedilir. Mutation koşucusunun hiç koşmadığı bulundu ve 11C'nin seti yeniden koşuldu. 188/188 QA PASS, mutation 27/27. T6 çalıştırılmadı.

### D-089 — SESX-v0
11C final `docs/SESSION_STATE_SPEC.md`; durmanın neyi sakladığı kilitlendi. Bir duraklatma işin nerede olduğunu saklar, ne kadar sürdüğünü ya da ne kadar iyi gittiğini değil. Checkpoint `ResumeContext` + high-stakes işareti; sürümlü, katı çözülen tek append-only satır; tüketildi bayrağı yok. Yalnız dört koşulu doğrulanmış pause durable'dır; mid-segment pause yazılmaz ve kaydedilmiş gösterilmez. Resume yalnız checkpoint'in kanıtladığını doğrular; gap eşiği uydurulmadı; bugün hiçbir checkpoint devam ettirilemez ve Today checkpoint sunmaz. Working session emergent, puansız ve saklanmaz; bir kez, `TRUX-v0` sebebiyle biter. `readTruth` port incelmesi. 146/146 QA PASS, mutation 20/20. T6 çalıştırılmadı.

### D-088 — RNRX-v0
11B final `docs/TASK_RUNNER_SPEC.md`; Task Runner kuruldu. Runner bir execution surface'tir; planner, mastery, prerequisite ya da evidence otoritesi değil. Girişte her koşul doğrulanmalı ve doğrulanamayan `unmet` diye adlandırılır; bugün hiçbir görev başlatılamaz. Yardım hep istenebilir, istenmeden verilmez, H3/H4 açıklamasız verilmez. Deneme tek transaction (attempt + artifact + provenance + assistance), evidence yazılmaz. Main'de 11A'dan kalan U+0307 bulundu ve validator ile korunuyor. 147/147 QA PASS, mutation 18/18. T6 çalıştırılmadı.

### D-087 — TDYX-v0
11A final `docs/TODAY_INTERIOR_SPEC.md`; ilk destination interior'u kuruldu. Today kanonik planner/state gerçeğinin projeksiyonudur ve kendisine verilmeyeni hesaplamaz: planner 12'de olduğu için ekran plan uydurmaz, dürüst boş/yükleniyor state'lerini gösterir. 12 state, 6 adımlı precedence, 7 purpose, 8 reason ve 7 attention family, region sırası ve tonlar sahiplerinden kopyalandı. Bayat plan, blocked görev, değiştirilen plan ve doğrulanmamış oturum süzülür; reason planner trace'i olmadan kurulamaz ve serbest metin taşımaz; satırda mastery/score alanı, sunumda türetilmiş kapasite hükmü yoktur. `empty_valid` üretiliyor. Kodu kontratlara karşı okumak içerik portundaki `TODO()`yu ve `Surface` kaydındaki başlatma-sırası hatasını buldu. 150/150 QA PASS, mutation 16/16. T6 çalıştırılmadı.

### D-086 — APHX-v0
10E final `docs/APP_HEALTH_SPEC.md`; uygulama dürüst bir başlangıç kazandı ve **AŞAMA 10 kapandı**. Store'un hiçbir arızası çökme ya da reset değil: store süreçte bir kez arka planda açılıyor (latch'li JVM testi), bütünlük migration'dan önce `quick_check` + FK ve migration sonrası tam `integrity_check` ile kontrol ediliyor, her hata `UXIA-v0`nin kabul edilmiş bir state'i ve sebep tip olarak taşınıyor, "hiçbir şey sıfırlanmadı" byte karşılaştırmasıyla kanıtlanıyor, tek aksiyon `RECHECK` ve reset temsil edilemez. Kod kontratlara karşı okununca açılışta bütünlük kontrolünün olmadığı ve varsayılan build'in AI adaptörünün çökeceği bulundu. Restore mekanizması kuruldu (kontroller 16D). 152/152 QA PASS, mutation 16/16 (ikisi test güçlendirilince). T6 çalıştırılmadı.

### D-085 — LDBX-v0
10D final `docs/LOCAL_DATABASE_SPEC.md`; `DDM-v0` fiziksel şeması gerçek SQLite oldu. Storage engine yasakları reddediyor ve her ret denenerek kanıtlanıyor: truth/curriculum tablolarında abort eden UPDATE/DELETE trigger'ları, CHECK olarak DDM değer kümeleri, yapısal pinning, user→curriculum FK yok, dakika cinsinden offset ve tam dakika olmayanın reddi, global truth sequence watermark, ileri-yönlü transaction'lı migration dolu fixture'a karşı test. İlk taslak DDM'den sapmıştı (yanlış outcome değerleri, saniye offset, eksik entity'ler); kontratı okuyan validator yakaladı ve kabulden önce düzeltildi. arm64-v8a native kütüphane APK'da doğrulandı. 163/163 QA PASS, mutation 9/9.

### D-084 — DSIX-v0
10C final `docs/DESIGN_SYSTEM_IMPL_SPEC.md`; tasarım sistemi koda geçti. Token'lar `core-presentation`da düz veri, çünkü kontrast hatırlanan bir orandan iddia edilmek yerine cihazsız bir JVM testinde yeniden hesaplanmalı. Palet birebir kopyalandı ve revize edilmedi; kayıtlı minimumlar (6.08 / 3.79 / 6.06) token'lardan yeniden türetildi. `LearningTone` beş değerli ve fault değeri yok → learning state'e fault tonu vermek yazılamaz; Material `error` rolü yalnız `system_fault` taşır. Attention grubu tonu yükseltmez (adı konmuş, test edilen fonksiyon). 48dp modifier, %200 metin, metin olarak verilen state, locale-naive casing yok. Dynamic colour scan'i bu adımın kendi yorumunu yakaladı; gate gevşetilmedi. 146/146 QA PASS, mutation-tested 8/8.

### D-083 — NSHX-v0
10B final `docs/NAVIGATION_SHELL_SPEC.md`; shell kuruldu. Navigasyon kuralları `core-presentation`da saf fonksiyonlar, Compose'da değil. Dört destination kabul edilmiş sırada ve enum sırası kanonik; yasak top-level id yok; kanonik entity başına tek surface objesi (çelişkili Skill detail temsil edilemez); contextual edge kümesi kapalı ve `ia.yaml` ile karşılaştırılıyor; focused flow shell'i askıya alıyor ve `showsShell`/`requiresSafeExit` türetiliyor; dönüş kuralı deterministik; window class'lar core'da hesaplanıyor ve yalnız çizimi değiştiriyor; metin etiketi + `stateDescription` + `traversalIndex`. 104/104 QA PASS, mutation-tested 8/8.

### D-082 — MPSX-v0
10A final `docs/PROJECT_SETUP_SPEC.md`; repodaki ilk çalıştırılabilir çıktı. Çalıştırılmamış hiçbir şey iddia edilmez: build, Gradle 9.5 pininin ve `kotlin.android` plugin'inin yanlış olduğunu anında gösterdi. Toolchain 2026-08-31'de doğrulandı (AGP 9.3.0 / Gradle 9.7.1 / Kotlin 2.4.0 / Compose BOM 2026.08.00 / adaptive 1.3.0 / androidx.sqlite 2.7.0; minSdk 26, targetSdk 36, compileSdk 37). Hedef cihaz kaydedildi: **Poco M6 Pro, Android 16 / API 36**. `AMTS-v0` §9'un altı maddesi kapandı. On modül `android/` altında ve bağımlılıklar `boundaries.yaml`a eşit; `verifyModuleBoundaries` build'i düşürüyor; `-PwithAiAdapter=false` adaptörsüz build geçiyor (V1 kriteri 8). DI framework/ORM/HTTP client yok; saat tek yerde okunuyor; dynamic colour yok; key repoya giremiyor.

### D-081 — TVSX-v0
9F final `docs/TEST_STRATEGY_SPEC.md`; doğrulama stratejisi kilitlendi ve **AŞAMA 9 kapandı**. Üzerine hiçbir şeyin düşmediği bir garanti bir tercihtir; kabul edilmiş her invariant'ın adı konmuş bir sahibi vardır ve sahipsiz invariant release'i bloklar. 6 katman (T1 pure domain, T2 persistence contract, T3 structural, T4 presentation & accessibility, T5 adapter & integration, T6 device smoke) ve cihaz katmanı en küçüğüdür. Coverage yüzdesi gate değildir; gate invariant coverage'dır. Yasaklanan denenip reddedilmesi şart koşulur; append-only schema seviyesinde, migration dolu fixture'lara karşı doğrulanır; hiçbir check canlı AI provider çağırmaz; null-evaluator yolu `ai-adapter` olmadan build alınarak doğrulanır; determinizm enjekte saatle egzersiz edilir ve flaky check düşmüş check'tir; 11 koşullu release gate `tools/validate_*.py` glob'unun tamamını içerir. Somut library/version ve CI 10A'ya bırakıldı.

### D-080 — Dağıtım kapsamı
Ürün kişisel kullanım içindir; store dağıtımı ve halka açık paylaşım yoktur, repo private'dır. Store yayın gereksinimleri, dağıtım imzalama seremonisi, güvenlik amaçlı obfuscation/pinning ve cihaz matrisi kapsam dışıdır; `minSdk` ve device QA tek hedef cihaza sabitlenir. Kanıt kuralları, AI yetki sınırları ve `key_in_platform_secure_storage` invariant'ı **değişmez**; API key repoya commit edilmez.

### D-079 — AIAX-v0
9E final `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md`; `EvaluatorPort` arkasındaki AI entegrasyonu kilitlendi. AI port arkasında yardımcıdır ve mastery/retention/prerequisite/planner/curriculum truth üzerinde otorite kazanamaz — AI önerir, deterministic engine'ler karar verir. `LEARNING_BEHAVIOR_RULES` §17 model seçimi ve §18 güvenlik/proxy/backend kararları burada kapatıldı. Evaluator çıktısı schema-constrained; schema'ya uymayan yanıt hüküm değil hatadır; kalibre edilmemiş LLM değerlendirmesi `provisional`; **refusal yanlış cevap değildir** ve her yanıtsızlık `evaluation_pending`e düşüp evidence yazmaz; timeout bütçesi uçtan uca; model adı konfigürasyonda, provider-independent adapter + router; deterministik iş asla AI çağırmaz; APK'da hardcoded/paylaşılan key yok ve V1'de backend proxy yok, öğrenci kendi key'ini platform secure storage'da tutar; yalnız mevcut attempt için gereken asgari içerik cihazdan çıkar; generated item untrusted girer; her AI-türevli evidence satırı evaluator_ref kaydeder. Prompt/rubric metni ve tutor UX 14'e, kalibrasyon 18'e, SDK çağrı noktaları 10A/14'e, test stratejisi 9F'ye bırakıldı.

## 4. Güncel stage mapping
- 1 Product framing ✅
- 2 Learning/mastery ✅
- 3 Adaptive planner ✅
- 4 Assessment system ✅
- 5 Curriculum/knowledge graph backbone ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- 6 Granular Capability Map ✅ — S6ERQA-v0 / D-062
  - 6A ✅ GNS-v0 / D-054
  - 6B ✅ FRDB-v0 / D-056
  - 6C ✅ FDM-v0 / D-057
  - 6D ✅ SDM-v0 / D-058
  - 6E ✅ GIM-v0 / D-059
  - 6F ✅ PEM-v0 / D-060
  - 6G ✅ WLRM-v0 / D-061
  - 6H ✅ S6ERQA-v0 / D-062
- 7 English parallel line ✅ — **7A EED-v0 / D-063; 7B TECP-v0 / D-064; 7C DECP-v0 / D-065; 7D TEIP-v0 / D-066; 7E TEPM-v0 / D-067**
- 8 UX — **8A ✅ UXIA-v0 / D-068; 8B ✅ THUX-v0 / D-069; 8C ✅ TRUX-v0 / D-070; 8D ✅ ASUX-v0 / D-071; 8E ✅ SPWX-v0 / D-072; 8F ✅ VDSX-v0 / D-073; 8G ✅ WFPX-v0 / D-074**
  - **AŞAMA 8 ✅ tamamlandı**
- 9 Architecture — **9A ✅ AMTS-v0 / D-075; 9B ✅ LFPS-v0 / D-076; 9C ✅ DDM-v0 / D-077; 9D ✅ MSBX-v0 / D-078; 9E ✅ AIAX-v0 / D-079**
  - 9F ✅ TVSX-v0 / D-081
  - **AŞAMA 9 ✅ tamamlandı**
- 10 Mobile skeleton — **10A ✅ MPSX-v0 / D-082; 10B ✅ NSHX-v0 / D-083; 10C ✅ DSIX-v0 / D-084; 10D ✅ LDBX-v0 / D-085; 10E ✅ APHX-v0 / D-086**
  - **AŞAMA 10 ✅ tamamlandı**
- 11 Daily learning MVP — **11A ✅ TDYX-v0 / D-087; 11B ✅ RNRX-v0 / D-088; 11C ✅ SESX-v0 / D-089; 11D ✅ DMAX-v0 / D-090; 11E ✅ EODX-v0 / D-091**
  - **AŞAMA 11 ✅ tamamlandı**
- 12 Mastery/planner implementation — **12A ✅ MSTX-v0 / D-092; 12B ✅ PRQX-v0 / D-093; 12C ✅ PLNX-v0 / D-094; 12D ✅ RPLX-v0 / D-095; 12E ✅ RSNX-v0 / D-096; 12F ✅ VUSX-v0 / D-097**
  - **AŞAMA 12 ✅ tamamlandı**
- 13 Assessment/retention/remediation implementation — **13A ✅ WBAX-v0 / D-098**, **13B ✅ MCAX-v0 / D-100**, **13C ✅ RVRX-v0 / D-101**, **13D ✅ WLRX-v0 / D-102**, **13E ✅ PCRX-v0 / D-103**, **13F ✅ VDWX-v0 / D-104** (tanısal atlama, D-099 ile eklendi)
  - **AŞAMA 13 ✅ tamamlandı**
- 14 AI Tutor / intelligent evaluation — **14A ✅ TUTX-v0 / D-105** (tutor davranış sözleşmesi); **14B ✅ WAAX-v0 / D-106** (yanlış analizi); **14C ✅ ALEX-v0 / D-107** (alternatif anlatım); **14D ✅ CDEX-v0 / D-108** (kod değerlendirme); **14E ✅ ACCX-v0 / D-109** (AI-generated code comprehension check); **14F ✅ OREX-v0 / D-110** (açık uçlu cevap değerlendirme); **14G ✅ PRVX-v0 / D-111** (provider abstraction/fallback)
  - **AŞAMA 14 ✅ tamamlandı**
- 15 İlk 8–12 haftalık gerçek eğitim içeriği — **15A 🟡 active-not-executed** (Computer / Programming Fundamentals)
- 9 Architecture/data model
- 10 Mobile skeleton
- 11 Daily learning MVP
- 12 Mastery/planner implementation
- 13 Assessment/retention/remediation implementation
- 14 AI Tutor/evaluation
- 15 First 8–12 week production content
- 16 Analytics/settings
- 17 Polish/accessibility
- 18 Pilot/calibration/QA
- 19 Release APK
- 20 Full professional curriculum/career/capstones

## 5. Zorunlu GitHub beyin tazeleme protokolü
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

> Hiçbir numaralı adım PRE-STEP GitHub refresh yapılmadan başlatılmaz; hiçbir adım D-050 living-memory kontrolü + repo-wide stale-reference scan tamamlanmadan kapanmış sayılmaz.

Minimum PRE-STEP:
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP ALWAYS-CHECK:
1. ana spec/çıktı
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/HANDOFF_STATE.md`
5. `docs/PROGRESS_LOG.md`
6. `docs/MASTER_PLAN.md`
7. `PROJECT_CONTEXT.md`
8. `docs/START_HERE.md`
9. `docs/DECISIONS.md`
10. repo-wide stale-reference scan

README ve `PROJECT_MASTER_CONTEXT` volatile aktif step taşımaz; yalnız kendi rolü gerçekten etkilenirse güncellenir.

## 6. Yeni sohbet/agent okuma sırası
> D-044 öncesi bir stable/historical belgede eski future-stage numarası görülürse, current execution'ı değiştirmeden önce `docs/STAGE_REINDEX_MAP.md` ile karşılığı doğrulanır.

0. `AGENTS.md`
0a. `docs/LOCAL_MANAGER_HANDOFF.md`
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `PROJECT_CONTEXT.md`
4. `docs/HANDOFF_STATE.md`
5. `docs/EXECUTION_INDEX.md`
6. `docs/STEP_STATUS.md`
7. `docs/DECISIONS.md`
8. `docs/PRODUCT_REQUIREMENTS.md`
9. `docs/PROFESSIONAL_READINESS_TARGET.md`
10. `docs/CURRICULUM_DOMAIN_MAP.md`
11. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
11a. `docs/V1_FOUNDATION_BACKBONE.md`
11b. `docs/GRAPH_ARCHITECTURE_QA.md`
11c. `docs/GRANULARITY_NAMING_STANDARD.md`
11d. `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`
11e. `docs/FOUNDATIONS_DETAILED_MAP.md`
11f. `curriculum/decomposition/6c_foundations/manifest.yaml`
11g. `docs/SYSTEMS_DETAILED_MAP.md`
11h. `curriculum/decomposition/6d_systems/manifest.yaml`
11i. `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`
11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`
11k. `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`
11l. `curriculum/decomposition/6f_professional_engineering/manifest.yaml`
11m. `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`
11n. `curriculum/decomposition/6g_weakness_remediation/manifest.yaml`
11o. `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`
11p. `research/6h_external_research_ai_report.md`
11q. `curriculum/decomposition/6h_research_qa/final_qa_report.yaml`
11r. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
11s. `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`
11t. `curriculum/english/7a_entry_diagnostic/blueprint.yaml`
11u. `curriculum/english/7a_entry_diagnostic/qa_report.yaml`
11v. `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`
11w. `curriculum/english/7b_cefr_progression/alignment.yaml`
11x. `curriculum/english/7b_cefr_progression/qa_report.yaml`
11y. `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`
11z. `curriculum/english/7c_daily_component/policy.yaml`
11aa. `curriculum/english/7c_daily_component/qa_report.yaml`
11ab. `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`
11ac. `curriculum/english/7d_technical_integration/policy.yaml`
11ad. `curriculum/english/7d_technical_integration/qa_report.yaml`
11ae. `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`
11af. `curriculum/english/7e_mastery_profile/policy.yaml`
11ag. `curriculum/english/7e_mastery_profile/qa_report.yaml`
11ah. `docs/INFORMATION_ARCHITECTURE_SPEC.md`
11ai. `ux/8a_information_architecture/ia.yaml`
11aj. `ux/8a_information_architecture/qa_report.yaml`
11ak. `docs/TODAY_HOME_SCREEN_SPEC.md`
11al. `ux/8b_today_home/home.yaml`
11am. `ux/8b_today_home/qa_report.yaml`
12. `docs/LEARNING_ENGINE_SPEC.md`
13. `docs/LEARNING_BEHAVIOR_RULES.md`
14. `docs/MASTERY_SIGNALS_SPEC.md`
15. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
16. `docs/MASTERY_FORMULA_V0.md`
17. `docs/RETENTION_FORGETTING_SPEC.md`
18. `docs/ADAPTIVE_PLANNER_SPEC.md`
19. `docs/TASK_TAXONOMY_SPEC.md`
20. `docs/PRIORITY_POLICY_SPEC.md`
21. `docs/PREREQUISITE_POLICY_SPEC.md`
22. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
23. `docs/WEEKLY_ASSESSMENT_SPEC.md`
24. `docs/MONTHLY_ASSESSMENT_SPEC.md`
25. `docs/QUESTION_BANK_SPEC.md`
26. `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`
27. `docs/ENGLISH_FOUNDATION_RULES.md`
28. `docs/MASTER_PLAN.md`
29. `docs/PROGRESS_LOG.md`

`docs/LEARNING_ENGINE.md` yalnız historical/superseded pointer'dır; canonical learning-engine kaynağı değildir. `docs/ENGLISH_TRACK.md` yalnız non-canonical seed notes'tur.

## 7. Ana kariyer/öğrenme yönü
**Technical English (parallel) → Python → C → Linux + Git + Shell → Data Structures & Algorithms foundations → Modern C++ → Computer Architecture → Operating Systems + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases foundations → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM / SGLang / TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

Bu lineer takvim değildir. PDM-v0 high-level boundaries'i, KGC-v0 graph contract'ı ve AŞAMA 6 granular Skill prerequisites gerçek executable route'u belirleyecek.

## 8. Tamamlanan çekirdek

- AŞAMA 1 ✅ Product framing
- AŞAMA 2 ✅ GRE-v0 + RVR-v0 learning/mastery
- AŞAMA 3 ✅ Adaptive planner — 16/16 scenarios, 20/20 invariants
- AŞAMA 4 ✅ DMA/WBA/MCA/QAB/AIV assessment system
- AŞAMA 5 ✅ PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6 ✅ GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / **S6ERQA-v0 / D-062**
- AŞAMA 7 ✅ **EED-v0 / D-063 → TECP-v0 / D-064 → DECP-v0 / D-065 → TEIP-v0 / D-066 → TEPM-v0 / D-067**
- AŞAMA 8A ✅ **UXIA-v0 / D-068**
- AŞAMA 8B ✅ **THUX-v0 / D-069**
- AŞAMA 8C ✅ **TRUX-v0 / D-070**
- AŞAMA 8D ✅ **ASUX-v0 / D-071**
- AŞAMA 8E ✅ **SPWX-v0 / D-072**
- AŞAMA 8F ✅ **VDSX-v0 / D-073**
- AŞAMA 8G ✅ **WFPX-v0 / D-074**
- **AŞAMA 8 ✅ TAMAMLANDI**
- AŞAMA 9A ✅ **AMTS-v0 / D-075**
- AŞAMA 9B ✅ **LFPS-v0 / D-076**
- AŞAMA 9C ✅ **DDM-v0 / D-077**
- AŞAMA 9D ✅ **MSBX-v0 / D-078**
- AŞAMA 9E ✅ **AIAX-v0 / D-079**
- AŞAMA 9F ✅ **TVSX-v0 / D-081**
- **AŞAMA 9 ✅ TAMAMLANDI**
- AŞAMA 10A ✅ **MPSX-v0 / D-082**
- AŞAMA 10B ✅ **NSHX-v0 / D-083**
- AŞAMA 10C ✅ **DSIX-v0 / D-084**
- AŞAMA 10D ✅ **LDBX-v0 / D-085**
- AŞAMA 10E ✅ **APHX-v0 / D-086**
- **AŞAMA 10 ✅ TAMAMLANDI**
- AŞAMA 11A ✅ **TDYX-v0 / D-087**
- AŞAMA 11B ✅ **RNRX-v0 / D-088**
- AŞAMA 11C ✅ **SESX-v0 / D-089**
- AŞAMA 11D ✅ **DMAX-v0 / D-090**
- AŞAMA 11E ✅ **EODX-v0 / D-091**
- **AŞAMA 11 ✅ TAMAMLANDI**
- AŞAMA 12A ✅ **MSTX-v0 / D-092**
- AŞAMA 12B ✅ **PRQX-v0 / D-093**
- AŞAMA 12C ✅ **PLNX-v0 / D-094**
- AŞAMA 12D ✅ **RPLX-v0 / D-095**
- AŞAMA 12E ✅ **RSNX-v0 / D-096**
- AŞAMA 12F ✅ **VUSX-v0 / D-097**
- **AŞAMA 12 ✅ TAMAMLANDI**
- AŞAMA 13A ✅ **WBAX-v0 / D-098**
- AŞAMA 13B ✅ **MCAX-v0 / D-100**
- AŞAMA 13C ✅ **RVRX-v0 / D-101**
- AŞAMA 13D ✅ **WLRX-v0 / D-102**
- AŞAMA 13E ✅ **PCRX-v0 / D-103**
- AŞAMA 13F ✅ **VDWX-v0 / D-104**
- **AŞAMA 13 ✅ TAMAMLANDI**
- AŞAMA 14A ✅ **TUTX-v0 / D-105**
- AŞAMA 14B ✅ **WAAX-v0 / D-106**
- AŞAMA 14C ✅ **ALEX-v0 / D-107**
- AŞAMA 14D ✅ **CDEX-v0 / D-108**
- AŞAMA 14E ✅ **ACCX-v0 / D-109**
- AŞAMA 14F ✅ **OREX-v0 / D-110**
- AŞAMA 14G ✅ **PRVX-v0 / D-111**
- **AŞAMA 14 ✅ TAMAMLANDI**
- AŞAMA 15A 🟡 **active-not-executed**

7E final: English mastery exact GRE/RVR-backed D01 Skill state'inden derived profile olarak sunulur; 8 presentation state, qualified A1/A2/B1 base profile, B2+ per-capability evidence ve no-overclaim guards kabul edildi.

## 9. Güncel çalışma konumu

**Son tamamlanan:** **`14G — PRVX-v0 / D-111`**  
**Aktif:** **`15A — Computer / Programming Fundamentals`**  
**15A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. 8A UXIA-v0 / D-068, 8B THUX-v0 / D-069, 8C TRUX-v0 / D-070, 8D ASUX-v0 / D-071, 8E SPWX-v0 / D-072, 8F VDSX-v0 / D-073 ve 8G WFPX-v0 / D-074 tamamlandı; AŞAMA 8 kapandı. 9A AMTS-v0 / D-075, 9B LFPS-v0 / D-076, 9C DDM-v0 / D-077, 9D MSBX-v0 / D-078, 9E AIAX-v0 / D-079 ve 9F TVSX-v0 / D-081 tamamlandı; AŞAMA 9 kapandı. D-080 ile dağıtım kapsamı kişisel kullanıma kilitlendi. 10A MPSX-v0 / D-082 ile ilk çalıştırılabilir iskelet kuruldu ve hedef cihaz (Poco M6 Pro, API 36) kaydedildi. 10B NSHX-v0 / D-083 ile shell kuruldu, 10C DSIX-v0 / D-084 ile tasarım sistemi koda geçti, 10D LDBX-v0 / D-085 ile yerel veritabanı kuruldu ve 10E APHX-v0 / D-086 ile uygulama sağlığı kuruldu; AŞAMA 10 kapandı. Today action-first; queue current selected PlannedTasks; capacity time budget; reasons PDT-v0 trace-derived; completion != mastery; missed-day debt yok; assessment/English contextual. Task Runner execution surface'tir; session emergent/ungraded; shared focused-flow frame assessment session tarafından da devralınır; assistance non-punitive ve recheck planner-owned; provenance sorulur. Assessment session evidence-collection workflow'udur; üç scope tek interior; atomic boundary submission; submitted boundary frozen; skip != incorrect; result semantic ve pass/fail banner yok. Progress canonical evidence state projection'ıdır; tek 8-state Skill vokabüleri bütün Skill'lere uygulanır; `at_risk` qualifier'dır; multi-axis truth çökertilmez; Progress sayabilir fakat puanlayamaz; remediation task completion closure değildir. Design system expression layer'dır ve canonical state'in iddia etmediği severity'yi ekleyemez; hiçbir learning state alarm tonu almaz. Geometry kabul edilmiş anlamı yerleştirir ve state/label/tone değiştiremez; palet tema başına ölçüldü ve traffic-light rampası yoktur. Teknoloji Android native + Kotlin/Compose'dur; dynamic colour kapalıdır ve domain core saf Kotlin'dir. Kanıt source of truth'tur ve öğrenci state'i yeniden hesaplanabilir projeksiyondur; exposure kayıtları kalıcıdır. Schema mimariyi uygular; pinning yapısaldır ve truth tablolarında UPDATE yolu yoktur. Sınırlar garantileri yapısal yapar: core `data-*`/`ai-*`/`app-*`'e bağımlı olamaz ve null evaluator ürünle sevk edilir. AI port arkasında yardımcıdır ve otorite değildir; refusal yanlış cevap değildir ve her yanıtsızlık `evaluation_pending`e düşer; APK'da key yok, öğrenci kendi key'ini girer; yalnız asgari attempt içeriği cihazdan çıkar. Garanti check'siz tercihtir; her invariant'ın adı konmuş sahibi vardır; coverage yüzdesi gate değildir; yasaklanan denenip reddedilmesi şart koşulur; hiçbir check canlı AI provider çağırmaz; null-evaluator yolu adapter'sız build ile doğrulanır; flaky check düşmüş check'tir. Çalıştırılmamış hiçbir şey iddia edilmez; on modül `android/` altında ve bağımlılıklar kontrata eşit; `verifyModuleBoundaries` build'i düşürür; adaptörsüz build geçer. Navigasyon kuralları core'da yaşar; enum sırası kanonik; kanonik entity başına tek surface; edge kümesi kapalı; focused flow shell'i askıya alır ve çıkışı türetilir. Token'lar core'da ve kontrast yeniden hesaplanır; learning state'e fault tonu temsil edilemez; attention grubu tonu yükseltmez. Storage engine yasakları reddeder ve her ret denenerek kanıtlanır; ilk taslağın DDM sapmaları kontratı okuyan validator'la yakalandı. Store'un hiçbir arızası çökme ya da reset değildir; bütünlük yazmadan önce kontrol edilir, "hiçbir şey sıfırlanmadı" byte ile kanıtlanır ve recovery ekranında reset temsil edilemez. Today kanonik gerçeğin projeksiyonudur ve kendisine verilmeyeni hesaplamaz; bayat plan, blocked görev ve doğrulanmamış oturum temsil edilemez; `empty_valid` üretiliyor. Task Runner bir execution surface'tir; girişte hiçbir koşul varsayılmaz, H3/H4 açıklamasız verilmez, deneme tek transaction'dır ve evidence yazmaz. Bir duraklatma işin nerede olduğunu saklar, ne kadar sürdüğünü değil; yalnız durable pause yazılır; resume yalnız checkpoint'in kanıtladığını doğrular ve gap eşiği uydurulmaz; working session puansızdır ve saklanmaz. Bir ölçüm artık baştan sona dürüst çalışır: curriculum tek yazma yolundan yayımlanır ve üzerine yazılmaz, güven mağazanın validation kaydıdır, kanıt uyumuna Objective karar verir, tek interior bütün scope'lara hizmet eder ve sonuçta puan yoktur. Gün sonu bir hüküm değil zamanda bir sınırdır: sayımlar etiketli envanterdir ve ilerleme değildir, değişiklik ancak kanonik bir engine bildirdiyse iddia edilir, boş gün başarısızlık değildir ve yarına borç geçmez. AŞAMA 11 kapandı. Mastery yardımsız yapılanı sorar: yardımlı, görülmüş, doğrulanmamış ve bozuk prerequisite üzerindeki kanıt skora girmez; Skill ancak her required ve critical Objective kendi başına geçerse mastered olur; ilk çelişki doğrulama açar, silmez; projeksiyon kanıttan yeniden kurulur. Prerequisite kapısı yalnız gerçekten bağlı işi bekletir: review_due bloklamaz, soft eksik kilitlemez, priority kapıyı aşamaz, draft kenar düşürülmez ve bekleyen aday üzerindeki iş contaminated yazılır. Planner önce semantik önceliğe sonra sığmaya bakar: priority kapıyı aşamaz, gün uzatılmaz, sığmayan ihtiyaç borç değildir, starvation eşiği uydurulmaz ve plan izle birlikte truth olarak yazılır. Bir plan gerekçesiz değiştirilmez ve düzenlenmez: başlanan iş korunur, yalnız kalan yeniden çözülür, geri dönüşte dünkü plan oynatılmaz ve yokluk borç değildir. Açıklama karar izinin projeksiyonudur: izde olmayan gerekçe kurulamaz, süreye sığmayan iş daha az önemli değildir, bekleyen iş blocker'ını adlandırır ve Today planı yalnız iz kendi satırlarını anlatıyorsa okur. 3H'nin sanal kullanıcıları gerçek koddan geçer: sanal kullanıcı durumdur, cevap değildir ve koşulamayan senaryo elle simüle edilmez. AŞAMA 12 kapandı. Haftalık sınav kodda: bir hafta bir kimliktir, kota ya da son tarih değil; havuz planner'ın ihtiyaçları, slot yalnız güvenilir ve taze item'la, slotlar planner'ın adayı, kaçırılan hafta borç değil ve sonuçta puan yok. 13F (tanısal atlama) D-099 ile eklendi. Aylık sınav kodda: bir ay daha geniş bir penceredir, daha ağır bir sınav değil; haftalık kontrat tek ortak kontrata genelleştirildi, kritik Skill yalnız nedenle yeniden doğrulanır ve aylık etiket ağırlık eklemez. Spaced repetition kodda: zamanın geçmesi negatif kanıt değildir; retention kanıttan yeniden oynatılır, ilk temiz hata doğrulama açar ve `review_due` yalnız günden türetilir. Remediation motoru kodda: başarısız bir deneme başarısız bir Skill değildir; atıf Objective düzeyinde, kapanış yalnız taze kanıtla ve mastery kapıları geçince. Program değişiklik raporu kodda: bir rapor iki okumanın farkıdır; değişiklik yalnız eksen gerçekten hareket ettiyse söylenir ve yalnız durum değiştiyse planner yeniden plan yapar. Tanısal atlama kodda: tanısal yol daha kolay bir yol değildir, aynı kanıtı daha erken toplar; waiver yalnız kapılar tanısal kanıtta ilk kez geçince verilir ve kapsamdır, mastery değil; yalnız gösterilen kısmın dersi atlanır. AŞAMA 13 kapandı. 14A TUTX-v0 / D-105 ile tutor davranış sözleşmesi kodda: tutor istek üzerine öğretir ve asla karar vermez; gösterdiği her yardım olduğu gibi kaydedilir, göstermediği hiçbir şey kaydedilmez. 14B WAAX-v0 / D-106 ile yanlış analizi kodda: yanlış bir cevap bir bilgidir, hüküm değil; AI önerisi hipotezin üstüne çıkmaz, beyan edilmemiş etiket saklanmaz. 14C ALEX-v0 / D-107 ile alternatif anlatım kodda: anlatım tutmadığında yöntem değişir, kapsam ve doğruluk değişmez; önce yazılı anlatım, AI alternatifi dersin anlatımına dayanır ve doğrulanmamış diye etiketlenir. 14D CDEX-v0 / D-108 ile kod değerlendirme kodda: kod çalıştırılarak değerlendirilir, yoksa yalnız bir görüştür; test yalnız kendi Objective'i için konuşur, AI yalnız testsiz ve izin veren görevde provisional. 14E ACCX-v0 / D-109 ile AI yazımı kod anlama kontrolü kodda: başkasının yazdığı kodun çalışması öğrenci hakkında bir şey kanıtlamaz, açıklamak anlamayı kanıtlar ama üretimi değil; AI yazımı kod bağımsız yeniden kontrol açar. 14F OREX-v0 / D-110 ile açık uçlu cevap değerlendirme kodda: serbest metin cevap dersin söylediğine göre değerlendirilir; AI yalnız kriter bulgusu önerir, core karar verir, en çok provisional. 14G PRVX-v0 / D-111 ile sağlayıcı adaptörü kodda ve AŞAMA 14 kapandı: sağlayıcı portların arkasında değiştirilebilir bir ayrıntıdır; yalnız öğrencinin kendi anahtarı çağrı yapabilir. Aktif step 15A — Computer / Programming Fundamentals; 15A henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`
