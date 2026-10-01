# AI Infra Learning Coach — Numaralı Yürütme İndeksi

Bu belge projenin sabit adım kodlarının canonical indeksidir. Ayrıntılı checklist `docs/MASTER_PLAN.md`, anlık durum `docs/STEP_STATUS.md` içindedir.

## Kullanım kuralı
- Ana aşamalar **1–20**.
- Alt adımlar `1A`, `2E`, `12F` biçiminde kullanılır.
- Tamamlanan `[x]`, bekleyen `[ ]`.
- Her adım öncesi/sonrası `docs/PROJECT_MEMORY_PROTOCOL.md` uygulanır.
- D-050 sonrası `PROJECT_CONTEXT`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` her step kapanışında istisnasız kontrol edilir ve repo-wide stale-reference scan yapılır.
- D-041: full curriculum 4+ yıllık professional-readiness horizon'ına genişletildi.
- D-042: Python ana technical foundation rotasına resmi olarak eklendi.
- D-043: yanlış yorum nedeniyle geri çekildi; standalone specialization stage canonical değildir.
- D-044: **AŞAMA 6 — Granular Capability Map** planlama aşamalarının arasına eklendi.
- D-045: 4B final weekly model `WBA-v0`.
- D-046: 4C final monthly model `MCA-v0`.
- D-047: 4D final assessment resource bank modeli `QAB-v0`.
- D-048: 4E final AI-generated assessment validation modeli `AIV-v0`.
- D-049: 5A final domain backbone modeli `PDM-v0`.
- D-050: living-memory sync + repo-wide stale-reference audit zorunlu.
- D-051: 5B final knowledge-graph contract `KGC-v0`.
- D-052: 5C final V1 foundation backbone `FBB-v0`.
- D-053: 5D final foundation graph architecture QA `GQA-v0`; corrective seed patch PASS.
- D-054: 6A final granularity/naming contract `GNS-v0`; semantic entity boundaries + stable logical ID rules.
- D-055: local çalışan agent main manager rolünü devralabilir; workflow contracts değişmez; transition kendi başına 6B execution değildi.
- D-056: 6B final full-route decomposition blueprint `FRDB-v0`; 6C–6F için ortak machine-readable authoring package + QA contract.
- D-057: 6C final Foundations detailed map `FDM-v0`; D01–D05 package + FBB 41/47 seed ratification + internal graph QA.
- D-058: 6D final Systems detailed map `SDM-v0`; D06–D13 package + 6C registry cross-package reuse + combined hard-graph QA.
- D-059: 6E final GPU / ML / Inference detailed map `GIM-v0`; D14–D22 package + 6C/6D reuse + hard/soft Pass-B + combined hard-graph QA.
- D-099: AŞAMA 13'ün sonuna `13F — Tanısal atlama (VDW-v0)` eklendi; mevcut adımlar yeniden numaralanmadı.
- D-104: 13F final tanısal atlama `VDWX-v0`; şema v7 `diagnostic_coverage` + `VDW-v0` durum ailesi; AŞAMA 13 kapandı.
- D-105: 14A final tutor davranış sözleşmesi `TUTX-v0`; ayrı `TutorPort` (beyanlı uzantı), kayıt izin verilenden az yardım iddia etmez, kaydedilen yardım kanıtın bağımsızlığını belirler; AŞAMA 14 başladı.
- D-106: 14B final yanlış analizi `WAAX-v0`; kapalı misconception kataloğu (curriculum), `misconception_state` hafızası (`WLRM-v0` ailesi), şema v8; AI önerisi hipotezin üstüne çıkmaz.
- D-107: 14C final alternatif anlatım `ALEX-v0`; yedi kapalı biçim, önce yazılı anlatım (kullanıcı kararı), AI alternatifi dersin kendi anlatımına dayanır ve doğrulanmamış diye etiketlenir; `tutor_instructions/2`; şema değişmedi.
- D-108: 14D final kod değerlendirme `CDEX-v0`; testler PC'de koşar ve rapor içe aktarılır (kullanıcı kararı), test yalnız kendi Objective'i için konuşur, test yoksa AI yalnız provisional (kullanıcı kararı); şema değişmedi.
- D-109: 14E final AI yazımı kod anlama kontrolü `ACCX-v0`; önce yazılmış kontrol, yoksa tutor pratiği (kanıt değil), hemen sonra ve isteğe bağlı, `generated_or_copied` bağımsız yeniden kontrol açar (kullanıcı kararları); şema değişmedi.

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
- [x] **1A — Ana ürün amacı** — `docs/PRODUCT_REQUIREMENTS.md`
- [x] **1B — V1 kapsamı** — `docs/V1_SCOPE.md`
- [x] **1C — Başarı kriterleri** — `docs/V1_SUCCESS_CRITERIA.md`
- [x] **1D — Non-goals** — `docs/NON_GOALS.md`

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅
- [x] **2A — Bilgi birimleri** — `docs/LEARNING_ENGINE_SPEC.md`
- [x] **2B — Topic durumları** — `docs/TOPIC_STATE_MACHINE.md`
- [x] **2C — Mastery sinyalleri** — `docs/MASTERY_SIGNALS_SPEC.md`
- [x] **2D — AI/ipucu etkisi** — `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- [x] **2E — Mastery formülü v0** — `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md` — GRE-v0 / D-031
- [x] **2F — Unutma modeli** — `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md` — RVR-v0 / D-032

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla ✅
- [x] **3A — Günlük kapasite** — `docs/ADAPTIVE_PLANNER_SPEC.md` — D-033
- [x] **3B — Görev kategorileri** — `docs/TASK_TAXONOMY_SPEC.md` — D-034
- [x] **3C — Öncelik puanı** — `docs/PRIORITY_POLICY_SPEC.md` — PBR-v0 / D-035
- [x] **3D — Prerequisite davranışı** — `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- [x] **3E — Hızlı öğrenme** — `docs/DIAGNOSTIC_WAIVER_SPEC.md` — VDW-v0 / D-037
- [x] **3F — Kaçırılan günler** — `docs/MISSED_DAY_RECOVERY_SPEC.md` — SRR-v0 / D-038
- [x] **3G — Açıklanabilir planner** — `docs/PLANNER_EXPLAINABILITY_SPEC.md` — PDT-v0 / D-039
- [x] **3H — Planner simülasyonu** — `docs/PLANNER_SIMULATION_SUITE.md`
  - 16/16 scenarios PASS, 20/20 invariants PASS, 0 critical contradiction.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla ✅
- [x] **4A — Günlük mikro değerlendirme** — `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` — DMA-v0 / D-040
- [x] **4B — Haftalık sınav** — `docs/WEEKLY_ASSESSMENT_SPEC.md` — WBA-v0 / D-045
- [x] **4C — Aylık yeterlilik sınavı** — `docs/MONTHLY_ASSESSMENT_SPEC.md` — MCA-v0 / D-046
- [x] **4D — Soru / assessment resource bank** — `docs/QUESTION_BANK_SPEC.md` — QAB-v0 / D-047
- [x] **4E — AI-generated soru doğrulaması** — `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0 / D-048

> **AŞAMA 4 tamamlandı.**

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti
- [x] **5A — Ana domain haritası** — `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049
  - 23 route family canonical high-level envelope,
  - Technical English parallel,
  - Python/C/Linux early complementary foundations,
  - systems → distributed/platform → performance → GPU → inference → AI infra,
  - ML/Transformer supporting domain,
  - professional/OSS/project/capstone cross-cutting evidence layer,
  - domain-level authoring relations != runtime Skill prerequisite.
- [x] **5B — Graph / Topic metadata sözleşmesi** — `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051
- [x] **5C — İlk 8–12 haftalık curriculum backbone** — `docs/V1_FOUNDATION_BACKBONE.md` — FBB-v0 / D-052
- [x] **5D — Graph architecture QA** — `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053

> AŞAMA 5 bütün ayrıntılı konu listesini yazmaz; graph'ın iskeletini kurar. Ayrıntılı decomposition AŞAMA 6'dadır.

---

# AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Böl
Ana charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`

- [x] **6A — Granularity + naming standardı** — `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054
- [x] **6B — Full-route decomposition blueprint** — `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` — FRDB-v0 / D-056
- [x] **6C — Foundations detailed map** — `docs/FOUNDATIONS_DETAILED_MAP.md`, `curriculum/decomposition/6c_foundations/` — FDM-v0 / D-057
- [x] **6D — Systems detailed map** — `docs/SYSTEMS_DETAILED_MAP.md`, `curriculum/decomposition/6d_systems/` — SDM-v0 / D-058
- [x] **6E — GPU / ML / Inference detailed map** — `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, `curriculum/decomposition/6e_gpu_ml_inference/` — GIM-v0 / D-059
- [x] **6F — Professional engineering / project map** — `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, `curriculum/decomposition/6f_professional_engineering/` — PEM-v0 / D-060
- [x] **6G — Weakness localization + remediation mapping** — `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`, `curriculum/decomposition/6g_weakness_remediation/` — WLRM-v0 / D-061
- [x] **6H — Coverage / prerequisite / Research QA** — `docs/STAGE6_EXTERNAL_RESEARCH_QA.md` — S6ERQA-v0 / D-062; 549 Skill / 608 Objective / 950 edge; 549/549 DAG; 10/10 review resolved

---

# AŞAMA 7 — İngilizce Paralel Hattı
- [x] **7A — Başlangıç ölçümü** — `EED-v0 / D-063`
- [x] **7B — A1/A2/B1/B2+ teknik hedefleri** — `TECP-v0 / D-064`
- [x] **7C — Günlük English bileşeni** — `DECP-v0 / D-065`
- [x] **7D — Teknik entegrasyon** — `TEIP-v0 / D-066`
- [x] **7E — English mastery** — `TEPM-v0 / D-067`

---

# AŞAMA 8 — UX ve Ekranlar
- [x] **8A — Bilgi mimarisi** — `UXIA-v0 / D-068`
- [x] **8B — Ana ekran** — `THUX-v0 / D-069`
- [x] **8C — Günlük çalışma akışı** — `TRUX-v0 / D-070`
- [x] **8D — Sınav UX** — `ASUX-v0 / D-071`
- [x] **8E — Skill/progress/weakness UX** — `SPWX-v0 / D-072`
- [x] **8F — Tasarım sistemi** — `VDSX-v0 / D-073`
- [x] **8G — Wireframe/prototip** — `WFPX-v0 / D-074`

---

# AŞAMA 9 — Teknik Mimari ve Veri Modeli
- [x] **9A — Mobil teknoloji seçimi** — `AMTS-v0 / D-075`
- [x] **9B — Veri saklama / local-first** — `LFPS-v0 / D-076`
- [x] **9C — Domain veri modeli** — `DDM-v0 / D-077` — granular Skill/Objective state, assessment-resource versions/exposure/validation records, years-long history, curriculum versioning
- [x] **9D — Servis sınırları** — `MSBX-v0 / D-078`
- [x] **9E — AI entegrasyon mimarisi** — `AIAX-v0 / D-079`
- [x] **9F — Test stratejisi** — `TVSX-v0 / D-081` — **AŞAMA 9 TAMAMLANDI**

---

# AŞAMA 10 — Mobil Proje İskeleti ve Tasarım Sistemini Kur
- [x] **10A — Proje kurulumu** — `MPSX-v0 / D-082`
- [x] **10B — Navigation** — `NSHX-v0 / D-083`
- [x] **10C — Design system implementation** — `DSIX-v0 / D-084`
- [x] **10D — Local database** — `LDBX-v0 / D-085`
- [x] **10E — Temel uygulama sağlığı** — `APHX-v0 / D-086` — **AŞAMA 10 TAMAMLANDI**

---

# AŞAMA 11 — Çekirdek Günlük Öğrenme Akışı MVP’sini Geliştir
- [x] **11A — Today ekranı** — `TDYX-v0 / D-087`
- [x] **11B — Task runner** — `RNRX-v0 / D-088`
- [x] **11C — Session state** — `SESX-v0 / D-089`
- [x] **11D — Günlük mikro quiz** — `DMAX-v0 / D-090`
- [x] **11E — Gün sonu** — `EODX-v0 / D-091` — **AŞAMA 11 TAMAMLANDI**

---

# AŞAMA 12 — Mastery ve Adaptif Planner’ı Koda Dök
- [x] **12A — Mastery Engine v1** — `MSTX-v0 / D-092`
- [x] **12B — Prerequisite Engine** — `PRQX-v0 / D-093`
- [x] **12C — Planner Engine v1** — `PLNX-v0 / D-094`
- [x] **12D — Replan** — `RPLX-v0 / D-095`
- [x] **12E — Explanation / reason codes** — `RSNX-v0 / D-096`
- [x] **12F — Sanal kullanıcı testleri** — `VUSX-v0 / D-097`

---

# AŞAMA 13 — Haftalık/Aylık Sınav, Retention ve Remediation’ı Geliştir
- [x] **13A — Haftalık sınav** — `WBAX-v0 / D-098`
- [x] **13B — Aylık sınav** — `MCAX-v0 / D-100`
- [x] **13C — Spaced repetition** — `RVRX-v0 / D-101`
- [x] **13D — Remediation Engine** — `WLRX-v0 / D-102`
- [x] **13E — Program değişiklik raporu** — `PCRX-v0 / D-103`
- [x] **13F — Tanısal atlama (VDW-v0)** — `VDWX-v0 / D-104`

---

# AŞAMA 14 — AI Tutor ve Akıllı Değerlendirme Katmanını Geliştir
- [x] **14A — Tutor davranış sözleşmesi** — `TUTX-v0 / D-105`
- [x] **14B — Yanlış analizi** — `WAAX-v0 / D-106`
- [x] **14C — Alternatif anlatım** — `ALEX-v0 / D-107`
- [x] **14D — Kod değerlendirme** — `CDEX-v0 / D-108`
- [x] **14E — AI-generated code comprehension check** — `ACCX-v0 / D-109`
- [ ] **14F — Açık uçlu cevap değerlendirme** **AKTİF**
- [ ] **14G — Provider abstraction / fallback**

---

# AŞAMA 15 — İlk 8–12 Haftalık Gerçek Eğitim İçeriğini Üret ve QA Et
- [ ] **15A — Computer / Programming Fundamentals**
- [ ] **15B — Python Foundations**
- [ ] **15C — C Foundations**
- [ ] **15D — Memory Foundations**
- [ ] **15E — Linux / Git / Shell Foundations**
- [ ] **15F — English A0→A1/A2 başlangıç paketi**
- [ ] **15G — Assessment content**
- [ ] **15H — Content QA**

---

# AŞAMA 16 — İlerleme, Analitik, Ayarlar ve Günlük Kullanım Araçları
- [ ] **16A — Skill analytics**
- [ ] **16B — Öğrenme geçmişi**
- [ ] **16C — Progress / weakness gösterim kuralları**
- [ ] **16D — Ayarlar**
- [ ] **16E — Bildirimler**

---

# AŞAMA 17 — UI/UX Polish ve Erişilebilirlik
- [ ] **17A — Görsel polish**
- [ ] **17B — Motion**
- [ ] **17C — Kullanılabilirlik**
- [ ] **17D — Accessibility**

---

# AŞAMA 18 — Gerçek Kullanım Pilotu, Kalibrasyon ve QA
- [ ] **18A — Pilot başlangıcı**
- [ ] **18B — Planner gözlemi**
- [ ] **18C — Mastery kalibrasyonu**
- [ ] **18D — Assessment/item/validator kalibrasyonu**
- [ ] **18E — Teknik / performance QA**
- [ ] **18F — Düzeltme döngüsü**

---

# AŞAMA 19 — Release APK ve Kullanıma Hazır Sürüm
- [ ] **19A — Release hazırlığı**
- [ ] **19B — Veri güvenilirliği**
- [ ] **19C — Final regression**
- [ ] **19D — APK / gerçek cihaz testleri**
- [ ] **19E — Release dokümantasyonu**

> V1 release ≠ full professional curriculum completion.

---

# AŞAMA 20 — Uzun Vadeli Professional Curriculum ve Kariyer Katmanı
- [ ] **20A — Modern C++ + Advanced Python + Professional Tooling paketi**
- [ ] **20B — Systems + Architecture + Performance paketi**
- [ ] **20C — Networking + Distributed Systems + Storage paketi**
- [ ] **20D — GPU Architecture + CUDA paketi**
- [ ] **20E — Triton + ML/Transformer + LLM Inference paketi**
- [ ] **20F — Serving Engines + KV Cache / Batching / Scheduling / Quantization paketi**
- [ ] **20G — Multi-GPU / NCCL / RDMA / AI Infrastructure paketi**
- [ ] **20H — Open Source + Engineering Practice + Career Readiness**
- [ ] **20I — Sürekli Curriculum QA + Büyük Entegre Projeler + Professional Capstones**

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A–8G`, `9A–9F`, `10A–10E`, `11A–11E`, `12A–12F`, `13A–13F`, `14A–14E`  
**Son tamamlanan:** **`14E — ACCX-v0 / D-109`**  
**Aktif:** **`14F — Açık uçlu cevap değerlendirme`** — active-not-executed

**AŞAMA 8, AŞAMA 9 ve AŞAMA 10 tamamlandı.** 10E `APHX-v0` ile uygulama dürüst bir başlangıç kazandı: **store'un hiçbir arızası çökme değil, hiçbir arızası reset değil.** Store süreçte bir kez, arka planda açılıyor; bütünlük migration'dan önce kontrol ediliyor ve migration sonrası tam kontrol ediliyor; her hata `UXIA-v0`nin kabul edilmiş bir state'i; "hiçbir şey sıfırlanmadı" byte karşılaştırmasıyla kanıtlanıyor; recovery ekranında reset temsil edilemez. Kod kontratlara karşı okununca handoff'un bilmediği iki sorun daha çıktı: açılışta bütünlük kontrolü yoktu ve varsayılan build'in AI adaptörü çökecekti. Restore mekanizması kuruldu, kontrolleri 16D'de. Mutation 16/16; ikisi başta yaşadı ve testler güçlendirildi. T6 çalıştırılmadı.

11C `SESX-v0` ile bir duraklatmanın gerçekte neyi sakladığı kilitlendi: işin nerede olduğunu, süresini ya da sonucunu değil; yalnız durable pause yazılır; resume yalnız checkpoint'in kanıtladığını doğrular ve gap eşiği uydurulmadı; working session puansızdır ve saklanmaz. T6 çalıştırılmadı.

11D `DMAX-v0` ile bir ölçüm baştan sona dürüst çalışır hâle geldi: yayımlama tek yol ve üzerine yazmaz, güven mağazanındır, kanıt uyumuna Objective karar verir, tek interior bütün scope'lara hizmet eder ve sonuçta puan yoktur. Mutation koşucusunun hiç koşmadığı bulundu; düzeltildi ve 11C'nin seti yeniden koşuldu. T6 çalıştırılmadı.

11E `EODX-v0` ile gün sonu kilitlendi: hüküm değil zaman sınırı, sayımlar etiketli envanter ve ilerleme değil, değişiklik ancak kanonik engine bildirdiyse, boş gün başarısızlık değil, yarına borç yok. **AŞAMA 11 kapandı**; günlük döngünün her yüzeyi kodda ve planner/içerik olmadığı için uygulama dürüstçe boş duruyor. T6 çalıştırılmadı.

12A `MSTX-v0` ile ilk engine kuruldu: kanıt yorumlanıyor ve mastery ondan projekte ediliyor. Yardımlı, görülmüş, doğrulanmamış, itirazlı ve bozuk prerequisite üzerindeki kanıt skora girmez; Skill non-compensatory; ilk çelişki doğrulama açar; projeksiyon kanıttan yeniden kurulur ve yalnız kendi eksenini yazar. Sabitler kalibre edilmemiş (18C). T6 çalıştırılmadı.

12B `PRQX-v0` ile prerequisite kapısı kuruldu: eksik prerequisite yalnız gerçekten bağlı işi bekletir; `review_due` bloklamaz, soft eksik kilitlemez, priority kapıyı aşamaz, bekleyen ihtiyaç başarısız değildir. Authored kenarların hepsinin `draft` olduğu bulundu ve metadata sorunu olarak adlandırılıyor; bekleyen aday üzerindeki iş `contaminated` yazılır. Yalnız `prerequisite_readiness` yazılır. T6 çalıştırılmadı.

12C `PLNX-v0` ile planner kuruldu: önce semantik öncelik, sonra sığma; priority kapıyı aşamaz, gün uzatılmaz, sığmayan ihtiyaç borç değildir. Starvation eşiği ve reason kodu uydurulmaz; plan izle birlikte tek transaction'da truth olarak yazılır. T6 çalıştırılmadı.

12D `RPLX-v0` ile plan değiştirme kuruldu: gerekçesiz yeni sürüm yok, başlanan iş korunur, yalnız kalan yeniden çözülür, geri dönüşte dünkü plan oynatılmaz ve yokluk borç değildir. Kurulamayan dört madde gerekçesiyle 13/15/16D/18E'ye bağlandı. T6 çalıştırılmadı.

12E `RSNX-v0` ile açıklama kuruldu: bir açıklama karar izinin projeksiyonudur: her cümle izin kaydettiği bir reason code'a ya da izin bir alanında tuttuğu bir olguya dayanır. Planner'ın kaydetmediği bir gerekçe kurulamaz, süreye sığmayan iş 'daha az önemli' diye anlatılmaz, bekleyen iş gerçek Skill blocker'ını adlandırır, `review_due` unutmak değildir ve yokluk borç değildir. Today artık planı okuyor ve iz ancak kendi satırlarını anlatıyorsa güveniliyor. T6 çalıştırılmadı.

12F `VUSX-v0` ile 3H'nin sanal kullanıcıları gerçek kodla koşuldu; sanal kullanıcı durumdur, cevap değil: 3H'nin sanal kullanıcıları artık gerçek kapıdan, planner'dan, replan'dan, depodan, Today'den ve açıklamadan geçiyor. İhtiyaç durumdan, uygunluk kapıdan, seçim planner'dan, açıklama onun yazdığı izden geliyor; koşulamayan senaryo elle simüle edilmez, sahibiyle adlandırılır. S06 (tanısal atlama) koşulamıyor ve 13'e bağlandı. **AŞAMA 12 TAMAMLANDI** — MSTX-v0 → PRQX-v0 → PLNX-v0 → RPLX-v0 → RSNX-v0 → VUSX-v0. T6 çalıştırılmadı.

13A `WBAX-v0` ile haftalık sınav kodda: bir hafta bir kimliktir, kota ya da son tarih değil: haftanın ölçümü öğe seçilmeden önce durumdan kurulur, her Skill tek kez ve `WBA-v0` §9 sırasıyla ölçülür, rol aileleri kota değildir; bir slot yalnız mağazanın güvendiği, kapının izin verdiği ve öğrencinin henüz görmediği bir item ile dolar — görülmüş item, çözümü gösterilmiş varyant ailesi, yakın varyant ya da paylaşılan testlet taze ölçüm değildir ve beyan edilmemiş süre uydurulmaz. Slotlar planner'a, zaten açtığı ihtiyaçların adayları olarak gider: haftanın kendi kuyruğu, bandı ya da dakikası yoktur. Döngü kaydedilmiş çalışma gününün ISO haftasıdır (kullanıcı onayladı); hafta bir kez kurulur, ölçülecek bir şey yoksa hiçbir şey yazılmaz ve kaçırılan hafta borç bırakmaz. Sonuçta puan yoktur, boş bırakmak yanlış değildir, bu oturumda eksik görünen ön koşula dayanan iş `contaminated` yazılır. `assessment_session` şema v3 ile blueprint'ini taşır (dolu fixture'a karşı). `D-099` ile 13F (tanısal atlama) eklendi. T6 çalıştırılmadı.

13B `MCAX-v0` ile aylık sınav kodda: bir ay daha geniş bir penceredir, daha ağır bir sınav değil: aylık ölçüm de öğe seçilmeden önce durumdan kurulur, her Skill tek kez ve `MCA-v0` §7 sırasıyla ölçülür, roller kota ya da yüzde değildir; kritik bir Skill yalnız bir nedenle (açık doğrulama ya da çelişki, vadesi gelmiş tekrar, bağımlı işi bekletmesi) yeniden doğrulanır, kritik olduğu için değil. 13A'nın haftalık kontratı tek ortak kontrata genelleştirildi ve hiçbir haftalık değer değişmedi: aylık etiket kanıta ağırlık eklemez, slotlar planner'ın zaten açtığı ihtiyaçların alternatifleri olarak gider ve ihtiyaç başına en çok bir görev seçilir. Döngü kaydedilmiş çalışma gününün takvim ayıdır; ay bir kez kurulur, önceki aylık oturumu adlandırır ama borç saymaz. Sonucun boylamsal listeleri yalnız kendi rolünün temiz bağımsız kanıtını taşır; temiz negatif hiçbir şeyi yeniden doğrulamaz. Transfer ve profesyonel kontrol noktasının üreticisi uydurulmadı (15). Şema v4 bir haftalık ya da aylık satırın kendi biçiminde blueprint taşımasını trigger ile zorlar (dolu v3 fixture'a karşı). T6 çalıştırılmadı.

13C `RVRX-v0` ile spaced repetition kodda: zamanın geçmesi negatif kanıt değildir: gün yalnız planlanmış bir kontrolün geldiğini söyler — mastery azalmaz, vadesi gelen tekrar unutma değildir, hiçbir şey kilitlenmez ve bir Skill'i doğrulamaya, riske ya da kararlılığa yalnız temiz, bağımsız bir kontrol taşıyabilir. Retention kanıttan yeniden oynatılır ve her satırdan sonra mastery motorunun kararı `RebuildMastery`'nin vereceği gibi yeniden sorulur; ilk temiz çelişki doğrulama açar ve hiçbir şeyi silmez, taze yeniden kontrol aralığı büyütmez, yakın tekrar karmaşık ya da kritik bir tekrarı tek başına taşıyamaz. `review_due` yalnız günden türetilir; planner plan kurmadan önce vadesi gelenleri indeksli sorguyla yeniler ve mevcut ihtiyaçları açar, kuralı değişmez. V0 sayıları `RVR-v0` §20'nin kendisidir (heuristik, 18C). Şema v5 `retention_state`'i tamamlar (dolu v4 fixture'a karşı); `skill_state` en eski eksenin watermark'ını taşır. T6 çalıştırılmadı.

13D `WLRX-v0` ile remediation motoru kodda: başarısız bir deneme başarısız bir Skill değildir: her kanıt satırı `WLRM-v0`'ın on iki atıf kuralıyla, öncelik sırasıyla kendi Objective'ine atfedilir; atfedilemeyen ya da ön koşulu bozuk deneme hedefi suçlamaz, yardım alınmış, provisional ya da kısmi hata en çok bir hipotezdir, mastery sonrası ilk temiz çelişki hiçbir şeyi silmeden doğrulama açar, remediation yalnız mastery motorunun kapıları düştüğünde doğrulanır ve yalnız taze, temiz kanıt kapıları yeniden geçirdiğinde kapanır — biten görev, yardım, aynı item ya da tek başarı kapatmaz. Skill zayıflık ekseni Objective'lerinden türetilir ve hiçbir yere yayılmaz; motor `weakness_detected` ihtiyacını planner'a ve composer'lara kendisi sağlar, bir endişe tek ihtiyaçtır. Düzeltmeler (`evidence_disposition`) satırın üzerine yazılmadan, en yenisi okunarak her motora ulaşır; geriye dönük kök neden contamination'ı yalnız gerçekten bağımlı slotları düzeltir. Şema v6 `weakness_state`'i tamamlar (dolu v5 fixture'a karşı); Topic durumu 16C'ye, yüksek riskli boşluk politikası 18D'ye bağlandı (kullanıcı kararı). T6 çalıştırılmadı.

13E `PCRX-v0` ile program değişiklik raporu kodda: bir rapor iki okumanın farkıdır ve motorların yazdığından fazlasını iddia edemez: bir kanıttan sonra dokunulan Skill'ler kendi motorlarıyla sırayla yeniden hesaplanır (mastery → retention → zayıflık → readiness; profiller yayımlanmış curriculum'dan, bilinmeyen criticality reddedilir), değişiklik yalnız bir eksen gerçekten hareket ettiyse söylenir, kimsenin yazmadığı durum bir 'önce' değildir, vadesi gelen tekrar gündür ve değişiklik değildir, mastery sonrası çelişki bir doğrulamadır ve asla düşüş değildir, hipotez eksik değil sorudur; plan değişikliği iki kayıtlı plan sürümü arasındaki farktır ve ilk plan değişiklik değildir; yalnız kanonik durum değiştiyse planner'ın kendi replan'ı, değişikliğin adlandırdığı olayla çağrılır ve günün bütçesi korunur; hiçbir şey değişmediyse sonuç bunu açıkça söyler. `ASUX-v0` §13.1 aileleri dolduruldu; tek port inceltmesi `objectivesOf`, şema değişmedi. T6 çalıştırılmadı.

13F `VDWX-v0` ile tanısal atlama kodda: bir tanısal yol mastery'ye giden daha kolay bir yol değildir, aynı kanıtı daha erken toplar: öğrencinin isteği yalnız kapsamı adlandırır ve kanıt değildir; bir Objective'in başlangıç anlatımı yalnız `GRE-v0`'ın kendi kapıları tanısal kanıtta ilk kez geçtiğinde atlanır, waiver o kanıtı adlandırır ve kapsamdır — mastery ya da retention değildir; tek kolay soru bir Topic'i atlatmaz; yardım ya da temiz bir kaçırma o Objective'in hızlı yolunu suçlamadan bitirir ve öğretilmemiş bir şeyi bilmemek zayıflık sayılmaz (kullanıcı kararları); tanı sürerken o Objective'in dersi bekler, yalnız gösterilen kısmın dersi atlanır; planlama kanıt okumaz. Şema v7 `diagnostic_coverage` projeksiyonunu ve `VDW-v0` durum ailesini ekler (D-104); 12F'nin S06'sı ve `PDT-v0` invariant 12 gerçek kodla koşuyor. **AŞAMA 13 TAMAMLANDI** — WBAX-v0 → MCAX-v0 → RVRX-v0 → WLRX-v0 → PCRX-v0 → VDWX-v0. T6 çalıştırılmadı.

14A `TUTX-v0` ile tutor davranış sözleşmesi kodda ve **AŞAMA 14 başladı**: tutor istek üzerine öğretir ve asla karar vermez: yalnız sorulana cevap verir, öğrencinin seçtiğinden fazlasını açmaz, öğrencinin ne yapabildiğini iddia etmez ve gerçekten gösterdiği her yardım olduğu gibi kaydedilir — göstermediği hiçbir şey kaydedilmez ve söylediği hiçbir şey kanıt değildir. Beş kapalı istek (`hint`, `explain_differently`, `question`, `explain_mistake`, `gloss`) ve yardım her zaman istenebilir: uymayan istek anına uyan isteğe yönlendirilir. Deneme açıkken cevabın en fazla ne kadar açabileceğini öğrenci seçer, H3/H4 önce ana göre açıklanır; kayıt asla izin verilenden az yardım iddia etmez (gönderilmiş cevaptan sonra `H4`) ve tutor'un kendi seviye beyanı yalnız reddetmek için kullanılır. Cihazdan yalnız mevcut görev çıkar; mesaj core'da kurulur ve cihaz dışında bayt bayt doğrulanır. Yanıt şemaya bağlıdır (`tutor_reply/1`), hükme yer yoktur; reddetme, zaman aşımı ya da kullanılamazlık hiçbir şey kaydetmez ve öğrenciyi suçlamaz, yalnız yazılmış yardım yerine geçer. Kaydedilen yardım artık kanıtın bağımsızlığını belirliyor (`2D` §6) ve gösterilen çözüm o andan itibaren exposure, sonraki kanıt bunu okuyor. Kullanıcı kararları: ayrı `TutorPort` (D-105 beyanlı uzantı, 9D düzenlenmedi), serbest soru + seviyeyi öğrenci seçer, ilk gerçek sağlayıcı çağrısı 14G'de. T6 çalıştırılmadı.

14B `WAAX-v0` ile yanlış analizi kodda: yanlış bir cevap bir bilgidir, öğrenci hakkında bir hüküm değil: misconception etiketi yalnız taşıdığı kanıt ve kaynağı kadar güçlüdür — zayıflık motorunun kendi kuralı Objective'i ne kadar taşıyorsa o kadar ilerler, AI'ın önerisi asla hipotezin üstüne çıkmaz, Objective için beyan edilmemiş etiket hiç saklanmaz ve hipotez öğrenciye yalnız soru olarak sorulur. Kapalı katalog curriculum'da (`misconception`, Objective sürümüne pinli, değişmez); etiketler yalnız yanlış giden satıra, kendi Objective'i için ve katalog beyan ediyorsa yazılır (`deterministic` ya da `ai_proposed`). Hafıza (`misconception_state`, `WLRM-v0` ailesi) zayıflık motorunun 12 kuralıyla yeniden oynatılır; geçersiz, itirazlı ya da ön koşulu bozuk iş hiçbir etiketi oynatmaz ve yalnız taze, temiz kanıt kapatır. Analiz saklanan atfı okur; hiçbir şey yazmaz. Kullanıcı kararları: kapalı katalog; hipotez yalnız yanlış cevaptan hemen sonra açık soru olarak. `review.6g.misconception_taxonomy_expansion` kapandı. T6 çalıştırılmadı.

14C `ALEX-v0` ile alternatif anlatım kodda: anlatım tutmadığında yöntem değişir, kapsam ve doğruluk değişmez: biçimi öğrenci seçer, önce doğrulanmış yazılı anlatım gösterilir ve yalnız yazılmış olan yoksa tutor yazar — dersin kendi anlatımına dayanarak, onunla çelişmemesi söylenerek, doğrulanmamış diye etiketlenerek ve asıl anlatıma dönüş her zaman bir adım uzakta; öğrencinin kendi durumunu gerektiren bir biçimi AI asla yazmaz. Yedi kapalı biçim (`LEARNING_BEHAVIOR_RULES` §9 ve `WLRM-v0` stratejileri): beşini tutor yazabilir; misconception karşılaştırması yalnız yazılıdır (öğrencinin hafızası cihazdan çıkmaz) ve dersin kendi anlatımı bir alternatif değildir. Yazılı anlatımlar içeriktir (`[explanation]` bölümü, Objective sürümüne pinli, `ContentPort.explanationsFor`), mağazaya yayımlanmaz; menü sıralamaz, görüleni işaretler ve hiçbir şey saklamaz; en az açan yazılı anlatım öğrencinin tavanına sığarsa tutor çağrılmaz. Tutor talimatları `tutor_instructions/2` (kural 14 dayanak, kural 15 biçimler). Kullanıcı kararları: biçimi öğrenci seçer; önce yazılmış, yoksa AI. T6 çalıştırılmadı.

14D `CDEX-v0` ile kod değerlendirme kodda: kod çalıştırılarak değerlendirilir, yoksa yalnız bir görüştür: bir kod görevi yalnız dersin kendi testleriyle doğrulanır — öğrencinin bilgisayarında koşulur ve raporu katı okunur — ve bir test yalnız yazıldığı Objective için konuşur; çalışmayan test hiçbir şey ölçmemiştir ve öğrenciye karşı sayılmaz; test yoksa AI kodu yalnız görev provisional sonuca izin veriyorsa ve en çok provisional olarak değerlendirir, doğrulanmış sonuç isteyen görev AI'a hiç sorulmaz; testlerin geçmesi kodun istenen şekilde çalıştığını gösterir, öğrencinin nedenini açıklayabildiğini değil. Testler içeriktir (`[code_test_suite]`/`[code_test]`, item sürümüne pinli, `ContentPort.codeTestsFor`); PC tarafı koşucu `tools/code_test_runner.py` (yalnız standart kütüphane, kabuksuz, süre sınırı yazılır) `code_test_report/1` üretir. Başarısız derleme yalnız yazılmış derleme Objective'ini suçlar; zaman aşımı başarısızlıktır; ortam hatası, başka item ya da suite sürümü için rapor hiçbir şey ölçmez; eksik ya da bozuk rapor asla AI'a düşmez; değerlendirici portu asla `verified` döndüremez. Kullanıcı kararları: testler PC'de koşar ve rapor içe aktarılır; test yoksa AI yalnız provisional. T6 ve C derlemesi çalıştırılmadı.

14E `ACCX-v0` ile AI yazımı kod anlama kontrolü kodda: başkasının yazdığı kodun çalışması öğrenci hakkında hiçbir şey kanıtlamaz; onu açıklayabilmek anlamayı kanıtlar, üretimi değil: öğrenci AI'ın ya da başka bir kaynağın yazdığı ya da büyük ölçüde gösterilmiş bir çözümün verdiği kodu gönderdiğinde hemen ardından bir anlama kontrolü sunulur ve geçilebilir; önce yazılmış kontroller gelir ve cevap anahtarıyla değerlendirilir, yalnız yazılmış kontrol yoksa tutor öğrencinin kendi kodu hakkında soru sorar ve bu pratiktir, kanıt değildir; doğru cevap yazarının beyan ettiği türde kanıttır, item'ın kendi üretimi asla değildir, ve öğrencinin yazmadığı kod üretim Objective'i için bağımsız yeniden kontrol açar. Dört kapalı tür `2D` §9'dan (`line_purpose`, `removal_effect`, `state_effect`, `find_the_bug`); yazılı kontroller içeriktir (`[comprehension_check]`, item sürümüne pinli, `ContentPort.comprehensionChecksFor`); tutor'a altıncı istek `check_understanding` (yalnız cevap donduktan sonra, `tutor_instructions/3`, `tutor_reply/2`); `generated_or_copied` artık `requires_independent_recheck` (öğretim görevinde pratik kalır). Kullanıcı kararları: önce yazılmış, yoksa tutor pratiği; hemen sonra ve isteğe bağlı; AI yazımı kod yeniden kontrol açar. T6 çalıştırılmadı.

14F başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.
