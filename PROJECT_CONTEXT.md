# Project Context — Kısa Yaşayan Proje Hafızası

**Son senkron:** 2026-08-28
**Dosya rolü:** Kısa current snapshot. Her numaralı adım sonunda D-050 / `docs/PROJECT_MEMORY_PROTOCOL.md` gereği kontrol edilir ve execution state değiştiyse güncellenir.

Bu dosya sohbet bağlamı kaybolsa bile projenin yönünü ve **şu an nerede olduğumuzu** hızlıca yeniden kurmak için tutulur. Ayrıntılı bootstrap için `docs/START_HERE.md`, uzun/stabil bağlam için `docs/PROJECT_MASTER_CONTEXT.md` canonicaldır.

## 1. Ana kariyer yönü

Seçilen uzmanlaşma:

**Low-Level Systems → Distributed Systems → GPU/CUDA → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

Güncel ana rota:

**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + professional capstones**

Python D-042 ile resmi foundation dilidir; C/C++ yerine geçmez. ML, inference sistemlerini anlayacak gerekli tensor/model/transformer derinliğinde supporting domain olarak tutulur.

## 2. Uzun vadeli hedef — D-041

- Rota gerektiğinde **4+ yıl veya daha uzun** sürebilir.
- 4+ yıl countdown değildir.
- Nihai hedef course completion değil, professional-readiness seviyesinde verified engineering capability'dir.
- Final readiness; required mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 full 4+ year curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. Granular Capability Map — D-044

Canonical hierarchy:

`Domain → Module → Topic → Skill → Learning Objective`

Gerçek mastery/prerequisite/weakness/remediation mümkün olduğunca Skill/Objective seviyesinde çalışır. `Python zayıf` gibi broad sonuçlar yalnız derived summary olabilir.

AŞAMA 6, Technical English'ten AI Infrastructure ve professional capstone'a kadar bütün rotayı ölçülebilir alt Skill/Objective haritasına böldü ve S6ERQA-v0 / D-062 ile bağımsız external Research QA'dan geçti.

Canonical: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## 4. Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Lesson/task completion, streak, self-confidence, AI-assisted output veya takvim süresi tek başına mastery/readiness değildir.

## 5. Mastery / Planner omurgası

- GRE-v0 / D-031 — valid + prerequisite-valid + H0 + direct + verified + independent evidence.
- RVR-v0 / D-032 — time mastery'yi düşürmez; `review_due` forgetting değildir.
- D-033 — daily capacity hard budget.
- D-034 — LearningNeed / TaskCandidate / Evidence ayrımı.
- PBR-v0 / D-035 — semantic priority.
- PRG-v0 / D-036 — Skill-level prerequisite readiness.
- VDW-v0 / D-037 — validated diagnostic waiver.
- SRR-v0 / D-038 — absence debt/failure değildir; stale plan replay edilmez.
- PDT-v0 / D-039 — structured planner decision trace.

3H simulation: **16/16 scenarios PASS, 20/20 invariants PASS**.

## 6. Assessment foundation — AŞAMA 4 tamamlandı

- 4A ✅ DMA-v0 / D-040 — Daily Micro Assessment.
- 4B ✅ WBA-v0 / D-045 — Weekly Blueprint Assessment.
- 4C ✅ MCA-v0 / D-046 — Monthly Capability Assessment.
- 4D ✅ QAB-v0 / D-047 — Trusted Assessment Resource Bank.
- 4E ✅ AIV-v0 / D-048 — AI Assessment Resource Validation.

Assessment raw score ile broad domain pass/fail yazmaz; Objective-level evidence canonical GRE/RVR/PRG/planner pipeline'ına girer.

## 7. Curriculum backbone / knowledge graph — AŞAMA 5 tamamlandı

**D-049 / `PDM-v0 — Professional Domain Backbone`** canonical kaynak: `docs/CURRICULUM_DOMAIN_MAP.md`.

- 23 route family high-level envelope olarak kilitlendi.
- Technical English parallel track.
- Python + C + Linux/Git/Shell complementary early foundations; DS&A supporting foundation.
- Systems → distributed/platform → performance → GPU/accelerator → inference → multi-GPU → AI/GPU Infrastructure convergence.
- Performance route boyunca cross-cutting capability.
- Open Source / engineering practice / projects / capstones route boyunca artan professional evidence layer.
- Security/reliability ve gerekli math/numerical capability hidden prerequisite bırakılmayacak.
- Tool/vendor isimleri stable systems concept'in yerine geçmeyecek.
- Domain-level relations authoring guidance; runtime hard prerequisite Skill→Skill PRG-v0.

**D-051 / KGC-v0:** canonical `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`. Organization layer ile capability identity ayrıldı; Skill reusable canonical identity, Topic↔Skill many-to-many, Objective exactly-one-Skill, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness, immutable graph versioning ve conservative migration contract'ı kilitlendi.

**D-052 / FBB-v0:** canonical `docs/V1_FOUNDATION_BACKBONE.md`. V1 başlangıç seed subgraph'ı zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English olarak tanımlandı. 8–12 hafta calendar gate değil scope-equivalent'tır; Skill/Objective IDs 6A/6C öncesi `authoring_seed` lifecycle'ındadır.

**D-053 / GQA-v0:** canonical `docs/GRAPH_ARCHITECTURE_QA.md`. 5D initial structural blockers ve hidden-prerequisite risklerini corrective seed patch ile düzeltti; hard graph DAG, TopicSkillLink/reuse explicit, English global-gate yok, F5D fixtures PASS.

**D-054 / GNS-v0:** canonical `docs/GRANULARITY_NAMING_STANDARD.md`. 6A Skill/Objective atomization, under/over-fragmentation, shared-vs-specific capability, stable logical ID ve FBB seed ratification/refactor kurallarını kilitledi.

**D-056 / FRDB-v0:** canonical `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`. 6B, 23 route family için 6C–6F common machine-readable authoring package, entity/relation row, duplicate/reuse, prerequisite, FBB mapping, source/freshness, review queue ve QA contract'ını kilitledi.

**D-057 / FDM-v0:** canonical summary `docs/FOUNDATIONS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6c_foundations/`. D01–D05; 5 Domain, 14 Module, 46 Topic, 132 Skill, 137 Objective, 145 TopicSkillLink ve 200 prerequisite edge ile internally mapped; FBB 41/47 seed mapping complete, hard graph DAG, 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-058 / SDM-v0:** canonical summary `docs/SYSTEMS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6d_systems/`. D06–D13; 8 Domain, 21 Module, 64 Topic, 192 Skill, 207 Objective, 224 TopicSkillLink ve 313 prerequisite edge (259 hard / 54 soft). 43 accepted 6C Skill clone'lanmadan reuse edildi; 6C+6D birleşik hard graph DAG 324/324; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-059 / GIM-v0:** canonical summary `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, dataset `curriculum/decomposition/6e_gpu_ml_inference/`. D14–D22; 9 Domain, 27 Module, 70 Topic, 143 Skill, 159 Objective, 230 TopicSkillLink ve 279 prerequisite edge (247 hard / 32 soft). 59 prior Skill clone'lanmadan reuse edildi; 6C+6D+6E combined hard graph DAG 467/467; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-060 / PEM-v0:** canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, dataset `curriculum/decomposition/6f_professional_engineering/`. D23; 1 Domain, 9 Module, 27 Topic, 76 Skill, 87 Objective. Existing D01–D22 technical capability'leri clone edilmeden professional project/capstone context'lerinde reuse edildi; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-061 / WLRM-v0:** canonical summary `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`, dataset `curriculum/decomposition/6g_weakness_remediation/`. 543 accepted Skill + 590 accepted Objective için weakness/remediation overlay; 590 route, 12 attribution rule, 15 strategy; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-062 / S6ERQA-v0:** canonical `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`. Üç bağımsız external evaluator reconcile edildi; corrective patch sonrası Stage 6 final registry 549 Skill / 608 Objective / 950 prerequisite edge, hard DAG 549/549 ve WLRM exact coverage 549/608. 10/10 6H review resolved.

## 8. İngilizce

English teknik eğitimin global ön koşulu değildir; ilk günden paralel ilerler. Bilinmeyen grammar/vocabulary teknik assessment'ta gizli prerequisite olamaz. Granular English capability map AŞAMA 6C / FDM-v0'da tamamlandı; 6D Systems package'ında da English→technical hard gate yoktur. English-specific progression/CEFR/cadence/integration AŞAMA 7'de kesinleşir.

### 8.1 7A English Entry Diagnostic — EED-v0 / D-063

7A başlangıç ölçümü Stage 6 D01 Technical English graph'ını yeniden sınıflandırmaz; 15 Skill / 15 Objective / 16 English hard edge üzerinde prerequisite-aware diagnostic profile üretir. Self-report evidence değildir; diagnostic mastery GRE-v0/VDW-v0 standardını düşürmez; invalid/prerequisite-contaminated failure target weakness yazmaz. CEFR A1/A2/B1/B2+ mapping 7B'ye bırakılmıştır.

Canonical: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`.

### 8.2 7B Technical English CEFR Progression — TECP-v0 / D-064

D01 15 Skill context-only CEFR-aligned Technical English progression metadata aldı: 5 A1 + 5 A2 + 5 B1 base anchor; 4 canonical Skill bounded B2+ professional evidence-depth extension taşıyor. CEFR alignment mastery/certification/general-English level değildir; source of truth exact Skill/Objective evidence'dır. `review.6c.english.cefr_alignment` resolved edildi.

Canonical: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.


### 8.3 7C Daily English Component — DECP-v0 / D-065

Technical English common daily hard capacity içinde parallel track olarak çalışır. Active study day'de open + eligible + safe English need varsa en az bir candidate üretilir; selection/mastery garantisi değildir. Fixed daily minute/percentage/completion/streak/debt yoktur. PBR-v0 track-balance/starvation semantics'i reuse edilir; task mix exact Skill/Objective state'inden türetilir ve spacing RVR-v0'da kalır. Technical-task integration 7D'ye, learner-facing English mastery/CEFR behavior 7E'ye bırakılmıştır.

Canonical: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.

### 8.4 7D Technical English Integration — TEIP-v0 / D-066

Technical task'in dili target construct ile eşit sayılmaz. TEIP-v0 dört mode tanımlar: technical-only localized, technical-with-English-exposure, dual-target integrated ve English-primary technical-context. English/CEFR global technical gate değildir; technical ve English target/prerequisite/evidence attribution ayrı tutulur. Hidden English technical negative evidence'ı, hidden specialist technical context English negative evidence'ı contaminate eder. Scaffold evidence/task-validity driven ve reversible'dır; fixed Turkish/English ratio/fading day yoktur.

Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.


### 8.5 7E Technical English Mastery Profile — TEPM-v0 / D-067

English learner-facing mastery görünümü exact D01 15 Skill'in GRE/RVR-backed state'inden türetilir; yeni mastery engine değildir. 8 derived Skill presentation state, qualified A1/A2/B1 Technical English base profile, uneven-profile preservation ve B2+ per-capability extension evidence kabul edildi. `review_due` bandı düşürmez; `verification_due` ilk contradiction sonrası uncertainty gösterir; confirmed remediation current profile'ı clean evidence ile yeniden türetir ve historical confirmation provenance korunur. General-English/official CEFR claim veya numeric aggregate yoktur.

Canonical: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.


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

```text
AŞAMA 5 = graph/schema/domain backbone
AŞAMA 6 = detailed granular capability map
AŞAMA 15 = first 8–12 week production content
AŞAMA 20 = full professional curriculum + OSS + career + capstones
```

## 10.1 Local manager takeover — D-055

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Canonical bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; GitHub durable source of truth ve D-024/D-027/D-050 PRE/POST workflow değişmez. D-055 transition kendi başına numbered step değildir; sonraki 6B execution normal onay/protokol ile tamamlanmıştır.

## 11. Güncel yürütme konumu

- AŞAMA 1–5 ✅
- **AŞAMA 6 ✅ tamamlandı — S6ERQA-v0 / D-062**
  - 6A ✅ GNS-v0 / D-054
  - 6B ✅ FRDB-v0 / D-056
  - 6C ✅ FDM-v0 / D-057
  - 6D ✅ SDM-v0 / D-058
  - 6E ✅ GIM-v0 / D-059
  - 6F ✅ PEM-v0 / D-060
  - 6G ✅ WLRM-v0 / D-061
  - 6H ✅ S6ERQA-v0 / D-062
- **AŞAMA 7 ✅ tamamlandı**
  - 7A ✅ EED-v0 / D-063
  - 7B ✅ TECP-v0 / D-064
  - 7C ✅ DECP-v0 / D-065
  - 7D ✅ TEIP-v0 / D-066
  - 7E ✅ TEPM-v0 / D-067
- **8A ✅ Bilgi mimarisi — UXIA-v0 / D-068**
- **8B ✅ Ana ekran — THUX-v0 / D-069**
- **8C ✅ Günlük çalışma akışı — TRUX-v0 / D-070**
- **8D ✅ Sınav UX — ASUX-v0 / D-071**
- **8E ✅ Skill/progress/weakness UX — SPWX-v0 / D-072**
- **8F ✅ Tasarım sistemi — VDSX-v0 / D-073**
- **8G ✅ Wireframe/prototip — WFPX-v0 / D-074**
- **AŞAMA 8 ✅ TAMAMLANDI**
- **9A ✅ Mobil teknoloji seçimi — AMTS-v0 / D-075**
- **9B ✅ Veri saklama / local-first — LFPS-v0 / D-076**
- **9C ✅ Domain veri modeli — DDM-v0 / D-077**
- **9D ✅ Servis sınırları — MSBX-v0 / D-078**
- **9E ✅ AI entegrasyon mimarisi — AIAX-v0 / D-079**
- **9F ✅ Test stratejisi — TVSX-v0 / D-081**
- **AŞAMA 9 ✅ TAMAMLANDI**
- **10A ✅ Proje kurulumu — MPSX-v0 / D-082**
- **10B ✅ Navigation — NSHX-v0 / D-083**
- **10C ✅ Design system implementation — DSIX-v0 / D-084**
- **10D ✅ Local database — LDBX-v0 / D-085**
- **10E ✅ Temel uygulama sağlığı — APHX-v0 / D-086**
- **AŞAMA 10 ✅ TAMAMLANDI**
- **11A ✅ Today ekranı — TDYX-v0 / D-087**
- **11B ✅ Task runner — RNRX-v0 / D-088**
- **11C ✅ Session state — SESX-v0 / D-089**
- **11D ✅ Günlük mikro quiz — DMAX-v0 / D-090**
- **11E ✅ Gün sonu — EODX-v0 / D-091**
- **AŞAMA 11 ✅ TAMAMLANDI**
- **12A ✅ Mastery Engine v1 — MSTX-v0 / D-092**
- **12B ✅ Prerequisite Engine — PRQX-v0 / D-093**
- **12C ✅ Planner Engine v1 — PLNX-v0 / D-094**
- **12D ✅ Replan — RPLX-v0 / D-095**
- **12E ✅ Explanation / reason codes — RSNX-v0 / D-096**
- **12F ✅ Sanal kullanıcı testleri — VUSX-v0 / D-097**
- **AŞAMA 12 ✅ TAMAMLANDI**
- **13A ✅ Haftalık sınav — WBAX-v0 / D-098**
- **13B ✅ Aylık sınav — MCAX-v0 / D-100**
- **13C 🟡 Spaced repetition — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
- 13D–13F, 14–20 ⬜ (13F D-099 ile eklendi)

Final Stage 6 graph: **549 Skill / 608 Objective / 950 prerequisite edge / 549/549 hard DAG**. WLRM final registry coverage 549/608; 10/10 6H review resolved.

**Sıradaki numaralı çalışma 13C'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.

## 11.1 8A UX Information Architecture — UXIA-v0 / D-068

AŞAMA 8'in semantic information architecture temeli kilitlendi. Primary shell exactly four destination kullanır: `Today · Learn · Progress · Profile`; normal entry `Today`'dir. Assessment, Technical English, AI Tutor, retention/remediation gibi engine/workflow'lar ayrı top-level silo değildir. Exact Skill detail shared surface'tir; planner explanation PDT-v0 reason trace'ten türetilir; browse hierarchy prerequisite graph yerine geçmez. Visual layout/design system ve runtime navigation implementation 8B–10'a bırakılmıştır.

Canonical: `docs/INFORMATION_ARCHITECTURE_SPEC.md` / D-068.

## 11.2 8B Today Home UX — THUX-v0 / D-069

Today/Home semantic contract action-first olarak kilitlendi. Dominant content current valid action'dır; queue yalnız selected PlannedTask'lardan oluşur. Daily capacity time budget context'idir, mastery/progress değildir; Today current-day override ile replan tetikleyebilir. Planner reasons PDT-v0 trace facts'ten bounded biçimde türetilir. Assessment ve Technical English contextual kalır; quota/streak/debt/gradebook shortcuts yoktur. Missed-day fresh-plan, offline/local-core, AI-degraded ve data-recovery semantics açıkça ayrılır. Fixed visual/card/pixel geometry 8F/8G'ye; Task Runner interaction choreography 8C'de kilitlenmiştir.

Canonical: `docs/TODAY_HOME_SCREEN_SPEC.md` / D-069.

## 11.3 8C Daily Working Flow — TRUX-v0 / D-070

Focused günlük çalışma akışı kilitlendi. Task Runner bir execution surface'tir; planner, mastery engine, prerequisite engine veya evidence evaluator değildir. Working session emergent ve ungraded'dır; required task count/duration/percentage yoktur. Tek bir shared focused-flow frame hem Task Runner hem assessment session tarafından devralınır; assessment interior 8D'ye aittir. Lifecycle `enter → orient → work → submit → resolve → transition` olup entry/resume revalidation deterministiktir ve prerequisite/content-version bypass edilemez. Assistance daima talep edilebilir, yalnız talep üzerine H1→H4 yükselir ve H3/H4 öncesi consequence ölçüm dilinde açıklanır; solution exposure sonrası same-item mastery path yoktur ve recheck scheduling planner-owned kalır. Provenance sorulur, çıkarsanmaz; dürüst beyan cezasızdır. In-flight run replan'dan korunur; continuity recomputed planner selection kullanır. AI evaluator yoksa attempt `evaluation_pending` olur ve evidence yazılmaz. Guilt/streak/debt framing, countdown pressure ve cached-list ilerleme yasaktır.

Canonical: `docs/DAILY_WORKING_FLOW_SPEC.md` / D-070.

## 11.4 8D Assessment Session UX — ASUX-v0 / D-071

Assessment session interior ve result presentation kilitlendi. Session bir evidence-collection workflow'udur; gradebook, score-based mastery authority veya ikinci state engine değildir. Üç scope (daily/weekly/monthly) için **tek interior** kullanılır; scope yalnız gösterilen context'tir ve ek evidence ağırlığı kazandırmaz. Submission birimi atomic evidence boundary'dir; bölünmez ve kısmen puanlanmaz. Submit edilen boundary dondurulur; açık blok içinde submit edilmemiş boundary'ler serbestçe gezilebilir. Skip meşrudur ve incorrect sayılmaz. `h0_required` varsayılanı ile allowed-tools policy cevap öncesi açıklanır; objective-appropriate tool kullanımı H0'ı bozmaz. Yardım engellenmez; H1/H2 assisted, H3/H4 solution-exposed olur ve fresh unseen item gerektirir; conversion açık ve cezasızdır, recheck planner-owned kalır. Resume'da unresolved slot beş koşuldan biriyle recompose edilir ve completed valid evidence silinmez. Incomplete session partial olabilir; exam debt yoktur. Item dispute evidence'ı contested tutar fakat auto-invalidate etmez ve undo button değildir. Provisional her yerde etiketlidir; invalid ne kredi ne ceza verir. Result semantic'tir ve altı family kullanır; pass/fail banner, yüzde/harf notu, geçme eşiği ve broad score yasaktır. `not_reliably_measured` first-class'tır. State-change iddiası yalnız canonical değişimde yapılır; mastered Skill'de ilk contradiction `verification_due` açar.

Canonical: `docs/ASSESSMENT_SESSION_UX_SPEC.md` / D-071.

## 11.5 8E Progress / Skill / Weakness UX — SPWX-v0 / D-072

Progress domain sunumu kilitlendi. Progress canonical evidence state'in projection'ıdır; mastery engine, score, competence yüzdesi, career tracker veya streak dashboard değildir. `TEPM-v0`nin 8 derived presentation state'i ve precedence'ı bütün Skill'lere genelleştirildi; technical Skill'ler için ikinci bir etiket sistemi üretilmedi ve TEPM-v0 değişmedi. 8 Türkçe Skill etiketi ile 6 Türkçe Topic etiketi kilitlendi; internal state ID'leri sabit kaldı. `at_risk` dokuzuncu state değil, attention qualifier'dır. Multi-axis truth sıralanır fakat çökertilmez: primary state eksenlerin yerini almaz ve `skill_detail` her ekseni ayrı incelenebilir tutar. Topic state derived orchestration'dır; prerequisite iddiası, Skill ortalaması ve yüzde yoktur. Progress sayabilir fakat puanlayamaz — count yalnız etiketli inventory'dir ve competence ima etmek için total'e bölünmez. Yalnız `supported`/`confirmed` weakness gösterilir; AI hypothesis confirmed olamaz; `remediation_task_completed != remediation_closed`. Learning history streak calendar değildir; assessment_report longitudinal'dir ve session'ları score'a toplayamaz. `review_due` nötr, `verification_due` history silmez.

Canonical: `docs/PROGRESS_SKILL_UX_SPEC.md` / D-072.

## 11.6 8F Visual Design System — VDSX-v0 / D-073

Görsel ifade katmanı kilitlendi. Design system canonical state'in iddia etmediği hiçbir anlamı, severity'yi, aciliyeti veya hiyerarşiyi ekleyemez: `visual_severity <= canonical_severity`. Tam altı tone vardır ve tone bir state'in ne anlama geldiğinden atanır, ne kadar alarm verici hissettirdiğinden değil. 8A–8E'nin 46 surface state'i, 8 Skill state'i, 6 Topic state'i ve 4 qualifier'ı eksiksiz eşlendi. `system_fault` yalnız gerçek teknik arızaya (`error_recoverable`, `data_recovery_required`) izinlidir; hiçbir learning state alarm tonu alamaz ve attention grubunda görünmek tone yükseltmez — bu yüzden `confirmed_review_due` ve Topic `weakening` neutral kalır. Kontrast WCAG 1.4.3/1.4.11'e çapalandı ve tema başına ölçülür; renk asla tek taşıyıcı değildir; dokunma hedefi en az 48dp'dir ve içerik %200 metin boyutunda kullanılabilir kalır. Türkçe casing korundu: locale-naive case transform yasak. Motion'ın ikna edici rolü yoktur; countdown, task-completion ödül animasyonu, decay ve streak animasyonu yasaktır. Progress-bar yalnız bounded factual konum için kullanılabilir; competence, career, oran ve level için yasaktır. Somut hex paleti kilitlenmedi; token role'leri, tone eşlemeleri ve kontrast kısıtları kilitlendi.

Canonical: `docs/DESIGN_SYSTEM_SPEC.md` / D-073.

## 11.7 8G Wireframe & Prototype Geometry — WFPX-v0 / D-074

Concrete geometry ve ölçülmüş palet kilitlendi; AŞAMA 8 kapandı. Geometry kabul edilmiş anlamı yerleştirir ve hiçbir surface anlamını, region sırasını, state'i veya tone'u değiştiremez. Üç window class (compact/medium/expanded) tanımlıdır; dört destination'ın kimliği ve sırası her sınıfta aynıdır ve hiçbir sınıf region ekleyip çıkaramaz. Altı surface'in region geometry'si sahibi spec'lere karşı doğrulandı: `skill_detail` primary chip ile dört ekseni birlikte gösterir, `progress_overview` yalnız envanter tutar, focused-flow'da exit ve pause her sınıfta 48dp sabit kalır ve countdown yoktur. Palet light ve dark için bağımsız ölçüldü; 52 zorunlu kontrast çifti geçer (min 6.08 metin / 3.79 non-text / 6.06 tone-üstü metin) ve oranlar her validator çalıştırmasında hex'ten yeniden hesaplanır. `attention` menekşedir ve kırmızı yalnız `system_fault` içindir — böylece palet bir yeşil→sarı→kırmızı şiddet rampası oluşturmaz. %200 metinde layout reflow eder ve state truncate edilmez. `prototype.html` bağlayıcı değildir; implementasyon veya teknoloji seçimi yapmaz.

Canonical: `docs/WIREFRAME_PROTOTYPE_SPEC.md` / D-074.

## 12.1 9A Mobil teknoloji seçimi — AMTS-v0 / D-075

Platform ve UI teknolojisi seçildi; AŞAMA 9 başladı. Teknoloji seçimi kabul edilmiş kontratlara hizmet eder ve hiçbirini zayıflatamaz — bir library default'u kontratla çelişirse kontrat kazanır. **Android native** seçildi ve V1'de cross-platform UI katmanı yoktur: V1 tek platforma çıkıp iOS/web/desktop'ı açıkça dışladığı için cross-platform fayda mevcut değildir, maliyeti ise tam olarak AŞAMA 8'in kontrata bağladığı üç yere iner (screen-reader state, Türkçe casing, adaptive navigation). Taşınabilirlik hedge'i UI değil domain core'dur. **Kotlin + Jetpack Compose** kullanılır; declarative/state-driven toolkit, token ve state olarak tanımlı design system'e doğrudan karşılık gelir. Material 3 yalnız substrate'tir ve **dynamic colour kapatılmıştır** — ölçülmüş paleti, tema başına kontrast kanıtını ve menekşe-değil-kehribar hue politikasını yok ederdi. **Domain core saf Kotlin'dir** ve Android API, UI toolkit, networking ya da AI client'a bağımlı olamaz; bu, "AI Tutor yokken core çökmemeli" kriterini konvansiyon değil yapı hâline getirir. Üç window class `WFPX-v0` ile birebir eşleşir. Default-locale case transform yasaktır ve Türkçe `i ↔ İ` / `ı ↔ I` round-trip etmelidir. `minSdk` bir politikadır (working default API 26) ve 10A'da gerçek cihaza karşı doğrulanacaktır. Framework güncelliği iddiası kesinleştirilmedi; 6 maddelik bounded verification list 10A'ya devredildi.

Canonical: `docs/MOBILE_TECHNOLOGY_SPEC.md` / D-075.

## 12.2 9B Local-first persistence — LFPS-v0 / D-076

Veri saklama mimarisi kilitlendi. **Kanıt source of truth'tur; öğrenci state'i onun yeniden hesaplanabilir projeksiyonudur.** Attempt, artifact, evidence event, assistance metadata, provenance, exposure kaydı, assessment session ve planner trace append-only truth kayıtlarıdır; mastery, retention, readiness, Topic state, weakness ve English profile cache'tir ve her an yeniden kurulabilir. Öğrencinin kanıtladığı yetkinliği yalnız yeni kanıt değiştirebilir; geçersiz kanıt silinmez, işaretlenir. Storage engine SQLite'tır; ORM library seçilmedi ve 10A'ya bırakıldı. Persistence interface'leri core'a aittir, platform onları implemente eder ve core signature'ında storage/Android/fs tipi bulunmaz. Curriculum ve user state ayrı saklanır ve ayrı versiyonlanır; user kayıtları curriculum version'ını pin'ler ve bir curriculum güncellemesi tek başına learner state değiştiremez. Exposure kayıtları kalıcı ve first-class'tır — kaybı veri kaybıdır, çünkü kaybolursa solution-exposed bir item taze bağımsız kontrol olarak sunulabilir. Bir öğrenci eylemi bir transaction'dır ve `evaluation_pending` evidence yazmaz. Migration forward-only'dir ve kanıtı asla yok etmez; restore atomik ve doğrulanmıştır. Bozulma `data_recovery_required` yüzeyler, sessiz reset yasaktır ve tutarsız projeksiyon recomputation ile onarılır. V1'de kanıt budanmaz.

Canonical: `docs/LOCAL_FIRST_PERSISTENCE_SPEC.md` / D-076.

## 12.3 9C Domain veri modeli — DDM-v0 / D-077

Veri modeli kilitlendi. **Schema mimariyi uygular**: `LFPS-v0`nin truth/projection ayrımı, append-only kuralı, exposure kalıcılığı ve version pinning garantileri burada konvansiyon değil yapısaldır — çünkü bir schema bir mimariyi sessizce yürürlükten kaldırabilir. Üç store bölgesi vardır ve curriculum store'dan user store'a foreign key yoktur. Versiyonlu kimlik `(logical_id, version)` composite'tir ve her user referansı version taşır; yalnız logical ID ile referans yasaktır çünkü version anahtarda olmazsa referans curriculum güncellendiği anda sessizce en yeni version'a kayar. Truth tablolarında UPDATE/DELETE yolu yoktur; düzeltme append edilen bir `evidence_disposition` satırıdır. Bir evidence satırının dört bağımsız ekseni dört ayrı kolondur: outcome, evaluator_status, independence_class ve contested. Her timestamp'li satır instant, learner-local study day ve UTC offset saklar — DST veya seyahat sonrası hiçbiri diğerinden güvenilir türetilemez. Her projection satırı policy version ve truth watermark kaydeder, böylece bayatlık tespit edilebilir. Exposure seçim yolundaki lookup için indekslenir ve asla silinmez. Physical schema library-neutral'dır ve core'un gördüğü modelde platform tipi yoktur.

Canonical: `docs/DOMAIN_DATA_MODEL_SPEC.md` / D-077.

## 12.4 9D Servis sınırları — MSBX-v0 / D-078

Modül ve servis sınırları kilitlendi. **Sınırlar garantileri yapısal hâle getirir**: "core AI ve ağ olmadan ayakta kalır" artık yıllarca hatırlanması gereken bir vaat değil, dependency kuralının bir özelliğidir. On modül ve katı içe-doğru bağımlılık kuralı vardır; `core-*` asla `data-*`, `ai-*` veya `app-*`'e bağımlı olamaz, graf asiklikdir ve `app-wiring` her implementasyonu bilen tek modüldür. Core'un dışarıdan ihtiyaç duyduğu her şey port'tur: `PersistencePort`, `ContentPort`, `ClockPort`, `EvaluatorPort`. **Saat bir port'tur** — zaman ortam gerçeği değil girdidir; aksi hâlde timezone mantığı test edilemez ve planner çıktısı beyan edilen girdilerinin fonksiyonu olmaktan çıkardı. **Core'da rastgelelik yoktur**; beraberlikler beyan edilmiş total ordering ile çözülür ve seeded random reddedilmiştir. **Null evaluator ürünle sevk edilir**; uygulama `ai-adapter` olmadan build edilip çalışır ve bu durumda open-ended attempt `evaluation_pending` olup evidence yazmaz — V1 kriteri 8 umutla değil wiring ile karşılanır. Her engine tam olarak bir state ailesine sahiptir ve başkasınınkini yazmaz; planner hiçbir learner state yazmaz. Transaction sınırı `core-application`da, presentation projection `core-presentation`dadır; `app-ui` yalnız render eder.

Canonical: `docs/SERVICE_BOUNDARIES_SPEC.md` / D-078.

## 12.5 9E AI entegrasyon mimarisi — AIAX-v0 / D-079

`EvaluatorPort` arkasındaki AI davranışı kilitlendi. **AI bir port arkasındaki yardımcıdır ve asla bir otorite değildir**: mastery yazamaz, retention/review scheduling'i değiştiremez, prerequisite'i karşılayamaz veya aşamaz, planner priority/rank/capacity'yi değiştiremez, assessment quota koyamaz, kendi ürettiği item'ı doğrulayamaz, weakness'i tek başına confirmed yapamaz ve refusal/timeout/error'ı olumsuz sonuca çeviremez. Kural: **AI önerir, deterministic engine'ler karar verir.** İki kanonik spec kararı açıkça bu adıma devretmişti ve ikisi de burada kapatıldı: `LEARNING_BEHAVIOR_RULES` §17 model seçimi ve §18 güvenlik/proxy/backend. AI'ın gerçek katkıları korundu — alternatif anlatım, istenen seviyede ipucu, açık uçlu cevabın değerlendirilmesine yardım, kod feedback'i, kök neden analizi, misconception hipotezi ve soru varyantı taslağı; amaç AI'ı azaltmak değil yetkisini sınırlamaktır. Evaluator çıktısı **schema-constrained**'dir ve schema'ya uymayan yanıt bir hüküm değil **hata**dır; serbest metinden hüküm ayrıştırmak yasaktır çünkü bir misparse hata gibi değil hüküm gibi görünür. Kalibre edilmemiş LLM değerlendirmesi **`provisional`**'dır; bilgilendirebilir ve confirmation need açabilir fakat tek başına critical mastery gate'i geçemez, `verified` deterministik bir yol ister. **Refusal bir yanlış cevap değildir**: yedi sonuçlu taksonomide `refused`, `timed_out`, `transport_error`, `invalid_response` ve `unavailable` aynı biçimde `evaluation_pending`e düşer ve evidence yazmaz — aksi hâlde öğrenci, kendi kod örneğinde bir güvenlik filtresi tetiklendiği için negative evidence alırdı. Timeout bütçesi **uçtan ucadır** ve retry'ları kapsar; per-call timeout kullanıcıya verilen bir garanti değildir ve kullanıcının key'ine karşı sessiz arka plan retry'ı yoktur. Model adı **konfigürasyonda** yaşar, core'da değil; provider-independent adapter ve router vardır, task class'a göre varsayılanlar gerekçesiyle kayıtlıdır ve somut model kimlikleri konfigürasyon değeridir, güncellikleri 10A/14'te yeniden doğrulanmak zorundadır. **Deterministik iş asla AI çağırmaz** ve maliyet hiçbir evidence kuralını zayıflatmanın gerekçesi değildir. Credential kararı: **APK'da hardcoded veya paylaşılan key yoktur ve V1'de backend proxy yoktur**; öğrenci kendi key'ini girer, key platform secure storage'da tutulur, log/export/backup/diagnostics'te asla görünmez, provider endpoint'i dışında hiçbir yere gönderilmez ve uygulama hiç key girilmeden tamamen kullanılabilir. Gizlilik sınırı: yalnız mevcut attempt'i değerlendirmek için gereken asgari içerik cihazdan çıkabilir; evidence history, mastery state, plan, profile, exposure, provenance ve planner trace'leri **asla** gitmez ve AI kapalıyken cihazdan hiçbir şey çıkmaz. Generated item untrusted girer, generator ile validator ayrıdır ve doğrulanmamış item güçlü mastery-changing evidence üretemez. Her AI-türevli evidence satırı provider, model ve prompt/schema version kaydeder. Prompt metni ve rubric ifadesi 14'e, tutor UX 14'e, evaluator kalibrasyonu 18'e, somut SDK çağrı noktaları 10A/14'e ve test stratejisi 9F'ye bırakıldı.

Canonical: `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md` / D-079.

## 12.6 9F Test stratejisi — TVSX-v0 / D-081

Doğrulama stratejisi kilitlendi ve **AŞAMA 9 kapandı**. Ana invariant: **üzerine hiçbir şeyin düşmediği bir garanti bir tercihtir.** AŞAMA 9 vaatleri yapıya çevirdi; yapı da tek başına çürür — check'i olmayan bir dependency kuralı bir yorumdur, negatif testi olmayan bir append-only schema'sı ise kimsenin doğrulamadığı bir SQL varsayımıdır. Bu yüzden kabul edilmiş her invariant'ın, ihlal edildiğinde FAIL veren adı konmuş bir sahibi vardır. Altı katman tanımlandı — T1 pure domain, T2 persistence contract, T3 structural (build time'da düşer), T4 presentation & accessibility, T5 adapter & integration, T6 device smoke — ve **cihaz katmanı en küçüğüdür**: cihaz dışında doğrulanabilen her şey cihaz dışında doğrulanır. **Coverage yüzdesi release gate değildir**; bu proje learner state için proxy sayıları zaten reddediyor ve bir yüzde, önemli invariant'lar kontrolsüz kalırken yükselebilir — gate **invariant coverage**'dır ve sahipsiz bir invariant tek başına bloklar. **Negatif doğrulama katman gereksinimidir:** bir şeyi yasaklayan her kural için yasaklanan denenir ve reddedilmesi şart koşulur; yalnız izinli yolu çalıştıran bir check yasak hakkında hiçbir şey kanıtlamaz. Append-only schema seviyesinde doğrulanır; migration'lar **dolu fixture'lara** karşı çalıştırılır ve evidence, exposure ile provenance birebir korunur, derived state atılıp yeniden kurulabilir ve rebuild aynı projeksiyonu üretmelidir. **Hiçbir check canlı AI provider çağırmaz** — canlı çağrı deterministik değildir, kullanıcının parasını harcar ve hataları atfedilemez kılar; yedi sonucun tamamı kayıtlı yanıtlarla üretilir ve payload'ın history/mastery/plan/profile taşımadığı doğrulanır. **Null-evaluator yolu `ai-adapter` olmadan build alınarak** doğrulanır, yalnız stub'layarak değil; V1 kriteri 8 böylece umut değil wiring olur. Determinizm iddia edilmez, enjekte saat ve tekrarlanan byte-identical koşularla egzersiz edilir; **ara ara geçen bir check düşmüş bir check'tir** ve retry-to-green yasaktır. Altı severity sınıfı vardır ve `evidence_correctness` her zaman bloklar, çünkü öğrencinin ne yapabildiğine dair yanlış bir iddia kozmetik bir kusur değildir. Release gate 11 koşuldur, kısmi geçiş yoktur, on V1 kriteri eşlenir ve `tools/validate_*.py` glob'unun tamamı geçmelidir. Invariant register 66 kayıttır ve her biri upstream kabul edilmiş kontratta gerçekten var olan bir anahtardır; 9F hiçbir yeni ürün semantiği icat etmez. Geçen bir suite'in kanıtlamadıkları da açıkça yazıldı: modellerin doğruluğu, pedagojik doğruluk ve öğrenme kalitesi. Tooling kapasite olarak adlandırıldı; somut library/version 10A'ya aittir ve güncellik doğrulanır, iddia edilmez.

Canonical: `docs/TEST_STRATEGY_SPEC.md` / D-081.

## 12.8 10A Proje kurulumu — MPSX-v0 / D-082

Repodaki **ilk çalıştırılabilir çıktı** üretildi ve AŞAMA 10 başladı. Ana invariant: **çalıştırılmamış hiçbir şey iddia edilmez.** 1A–9F arası her adım birbirine karşı doğrulanan spec üretiyordu; 10A'nın çıktısını bir makine çalıştırıyor ve bu "doğrulanmış"ın anlamını değiştiriyor — build ya geçer ya geçmez, iç tutarlılık bunun yerine geçmez. Nitekim build, bu adımın hafızadan iddia edeceği iki şeyi anında yanlışladı: AGP uyumluluk tablosundan okunan Gradle `9.5.0` dağıtımı çözülmüyor (wrapper 404 verdi; pin **9.7.1** oldu) ve Android modüllerine uygulanan `org.jetbrains.kotlin.android` plugin'ini **AGP 9.0+ açıkça reddediyor** çünkü Kotlin desteği artık yerleşik. İkisi de sessizce düzeltilmek yerine kaydedildi. Toolchain 2026-08-31'de doğrulandı: AGP 9.3.0, Gradle 9.7.1, Kotlin 2.4.0, Compose BOM 2026.08.00, adaptive 1.3.0, androidx.sqlite 2.7.0; `compileSdk` 37 (Compose 1.12 API 37'ye derleniyor), `targetSdk` 36, `minSdk` 26. **Hedef cihaz kaydedildi** — Poco M6 Pro, `2312FPCA6G`, Android 16 / API 36 — ve `AMTS-v0` §8.1'in "henüz repoda kayıtlı değil" maddesi kapandı; `D-080` gereği bu tek hedef cihazdır. `minSdk` cihazın seviyesine yükseltilmedi çünkü §8.1 shim gerektirmeyen **en düşük** seviyeyi istiyor ve 26 `java.time`ın native olduğu seviyedir. `AMTS-v0` §9'un altı maddesi güncel kaynaklarla kapandı ve 26'da hiçbir compatibility library gerekmiyor. On modül `android/` altında ve her modülün beyan ettiği project dependency'ler `boundaries.yaml`daki `depends_on` ile **eşit**, makine tarafından kontrol ediliyor. Yalnız iki `app-*` modülü Android modülüdür; `data-*` ve `ai-*`ın Android plugin'i uygulamaması `TVSX-v0` T2'nin ve adaptör doğrulamasının **cihaz dışında** koşmasını sağlar. `verifyModuleBoundaries` yasak kenarı, tanınmayan katmanı, composition-root ayrıcalığını ve renklendirmeli DFS ile hesaplanan cycle'ı kontrol edip build'i düşürüyor; üçüncü parti architecture-rule library kullanılmadı. Kural mutation-test edildi ve o sırada check'in kendi cycle **raporlaması** hatalı bulunup düzeltildi. **Adaptörsüz build gerçekten alınıyor**: `-PwithAiAdapter=false` ile 9 modül yapılandırılıyor ve `core-application`da sevk edilen `NullEvaluator` seçiliyor — V1 kriteri 8 artık tek komutla gösterilebilir. Saat tam olarak tek yerde okunuyor, dynamic colour hiçbir yerde çağrılmıyor ve `D-080` gereği key/keystore repoya giremiyor.

Canonical: `docs/PROJECT_SETUP_SPEC.md` / D-082.

## 12.9 10B Navigation — NSHX-v0 / D-083

Shell kuruldu. Ana invariant: **navigasyon kuralları `core-presentation`da yaşar, UI toolkit'inde değil.** Route string'leriyle dolu bir `NavHost` dört kabul edilmiş kararı — destination kümesi, sırası, paylaşılan surface kimliği ve focused-flow dönüş kuralı — Compose'un içine sokardı; `MSBX-v0` bunu presentation state için zaten reddetmişti, çünkü o hâlde üründeki en güvenlik-kritik etiketleme yalnız cihazda test edilebilir olurdu. Dört destination kabul edilmiş sırada (`today → learn → progress → profile`) ve **enum bildirim sırası kanoniktir**; senkron tutulacak ikinci bir liste olmadığı için yeniden sıralama kazara olamaz. On bir yasak top-level id'nin hiçbiri destination değildir. **Kanonik entity başına tek surface objesi** vardır: Skill detail Today, Learn ve Progress'ten erişilir ve her seferinde aynı objedir, yani `UXIA-v0`nin yasakladığı çelişkili Skill detail sayfaları caydırılmış değil **temsil edilemez**. Contextual edge kümesi sayılı ve **kapalıdır** ve validator onu `ia.yaml` ile karşılaştırır; her surface her şeyi açabilseydi öğrencinin izlediği yol curriculum yapısı gibi görünmeye başlardı — §10.2'nin yasakladığı şey. **Focused flow shell'i askıya alır** ve `showsShell` ile `requiresSafeExit` surface'tan türetilir, böylece "gizli shell + çıkış yok" durumu inşa edilemez; detail pane de focused flow sırasında bastırılır. Dönüş kuralı deterministiktir ve origin geçerliliğini açık bir girdi olarak alır, yani bir replan öğrenciyi bayat bir yüzeyde mahsur bırakamaz. Window class'lar `WFPX-v0` breakpoint'lerinden **core'da** hesaplanır (toolkit'in kendi bucketing'inden değil) ve yalnız çizimi değiştirir; küme, sıra ve anlam üç sınıfta da aynıdır. Her destination metin etiketi taşır, ikonun content description'ı null'dır, seçim `stateDescription` ile verilir ve `traversalIndex` kanonik sırayı izler.

Canonical: `docs/NAVIGATION_SHELL_SPEC.md` / D-083.

## 12.10 10C Design system implementation — DSIX-v0 / D-084

Tasarım sistemi koda geçti. Ana invariant: **tasarım sistemi, kanonik state'in iddia etmediği severity'yi ekleyemez** — ve bir kural yalnız gözden geçirilmek yerine temsil edilemez kılınabiliyorsa öyle yapılır. **Token'lar tema dosyasında değil `core-presentation`da düz veri**: hex değerleri yalnız Compose'un içinde yaşasaydı kontrastı doğrulamak UI toolkit'i gerektirirdi ve doğal kısayol, hesaplamak yerine hatırlanan bir oranı iddia etmek olurdu — bu kısayol 8G'de zaten bir kez başarısız oldu ve yanlış bir minimumu yalnız yeniden hesaplama yakaladı. Palet birebir kopyalandı ve **revize edilmedi**; validator Kotlin'deki her token'ı `WFPX-v0` ile bayt bayt karşılaştırıyor, yani bir check'i geçirmek için sessiz bir ayar düşerdi. Kontrast iki temada da hex'ten yeniden hesaplanıyor ve kayıtlı minimumlar (6.08 / 3.79 / 6.06) token'lardan yeniden türetilip birebir tuttu. **Fault tone bir learning state için temsil edilemez**: `Tone` altı değerli ama `LearningTone` beş değerli ve atanacak bir fault değeri yok; gözden geçirilecek kod yolu yok çünkü yazılacak ifade yok. Material'ın `error` rolü yalnız `system_fault` taşıyor, böylece bir bileşen "hata rengi"ne uzanıp öğrencinin state'ine uygulayamıyor. Sekiz Skill state'inin her birinin tam bir tonu var ve üçü bilinçle nötr: bekleme başarısızlık değil. **Attention grubunda görünmek tonu değiştirmiyor** ve bu adı konmuş, test edilen bir fonksiyon. 48dp `minimumTouchTarget()` modifier'ı, %100/150/200 metin ölçeği, metin olarak verilen state ve hiçbir yerde locale-naive case transform yok. Dynamic colour kapalı ve bu bir yokluk olduğu için source scan ile denetleniyor — scan bu adımın **kendi yorumunu** yakaladı ve gate gevşetilmek yerine yorum yeniden yazıldı.

Canonical: `docs/DESIGN_SYSTEM_IMPL_SPEC.md` / D-084.

## 12.11 10D Local database — LDBX-v0 / D-085

`DDM-v0`nin fiziksel şeması gerçek bir SQLite veritabanı oldu. Ana invariant: **storage engine, mimarinin yasakladığını reddeder** — append-only truth, değişmez curriculum, version pinning ve kalıcı exposure, çağıran kodun uyacağına güvenilen kurallar değil, SQLite'ın reddettiği ifadelerdir ve her ret denenerek kanıtlanır. **İlk taslak yanlıştı ve bu kaydedildi:** şema kontrattan değil önceki adımların hafızasından yazılmıştı ve kendi testleri aynı taslağa karşı yazıldığı için geçiyordu. Validator yazılırken taslak `data_model.yaml`a karşı okununca sapmalar çıktı — outcome ekseninde 10A'nın evaluator **sinyali** enum'u (`met/not_met…`) kullanılmıştı oysa kabul edilmiş değerler `positive/negative/partial/invalid`; iki eksende değerler eksikti; offset saniye tutuluyordu oysa alan `utc_offset_minutes`; 12 truth entity yerine 4 tablo vardı. Şema kabulden önce yeniden yazıldı. Kalıcı ders: bir taslağa karşı yazılmış suite o taslağın kontratı yanlış okumasını yakalayamaz, yalnız kontratı okuyan bir check yakalar. Library API'si hatırlamadan değil çözülmüş jar'dan `javap` ile okundu. Üç store bölgesi DDM ile birebir: 11 değişmez curriculum, 13 append-only truth ve 8 yeniden kurulabilir projection tablosu; user truth'tan curriculum'a foreign key yok. Her truth ve curriculum tablosunda abort eden UPDATE/DELETE trigger'ları envanterden üretiliyor. Her DDM değer kümesi aynı Kotlin listesinden üretilen bir CHECK. Pinning yapısal. Offset dakika tutuluyor ve tam dakika olmayan bir değer kesilmek yerine reddediliyor. Tek global truth sequence projection watermark'ı; her projection satırında tam provenance var ve port tipi de taşıyor. Migration ileri-yönlü ve transaction'lı, dolu fixture'a karşı satır satır içerikle test edildi. Adapter kolon gereksinimlerini SQLite'tan okuyor, kendi listesini tutmuyor. Aynı şema JVM'de ve cihazda koşuyor — arm64-v8a native kütüphanesi APK içinde doğrulandı. 22 T2 check; mutation 9/9, biri başta kaçtı ve test güçlendirildi.

Canonical: `docs/LOCAL_DATABASE_SPEC.md` / D-085.

## 12.12 10E Temel uygulama sağlığı — APHX-v0 / D-086

Uygulama dürüst bir başlangıç kazandı ve **AŞAMA 10 kapandı**. Ana invariant: **store'un hiçbir arızası çökme değildir ve hiçbir arızası reset değildir.** Handoff iki sorun söylüyordu — veritabanı main thread'de açılıyordu ve `DataRecoveryRequired` bir çökmeydi — ama kod kontratlara karşı okununca iki sorun daha çıktı: `LFPS-v0` §12'nin şart koştuğu **açılışta bütünlük kontrolü yoktu**, ve **varsayılan build'in AI adaptörü `TODO()` ile çökecekti** — CI'ın normal build dediği adaptörlü build güvensiz olandı. Store artık sürecin: `CoachApplication` bir `StoreStartup`ı bir kez, arka plan thread'inde başlatıyor ve activity'ler yalnız gözlüyor; orkestrasyon `core-application`da olduğu için "çağıranın thread'inde açılmaz" latch'te tutulan bir opener ile JVM testinde kanıtlanıyor. Yeni port eklenmedi. `StoreOpener` asla fırlatmıyor ve **yazmadan önce kontrol ediyor**: `quick_check` + `foreign_key_check`, sonra ileri migration, migration koştuysa tam `integrity_check` — iki kontrol arasındaki fark varsayılmadı, yalnız tam kontrolün görebildiği bir index fixture'ı ile kanıtlandı. Sebep `core-model` tipi olarak taşınıyor, mesaj ayrıştırılmıyor. **"Hiçbir şey sıfırlanmadı" byte ile kanıtlanıyor**: yedi bozulma/versiyon biçiminin her birinde dosya önce ve sonra byte byte karşılaştırılıyor, çünkü yalnız status assert eden bir test recovery raporlayıp sessizce boş veritabanı yaratan bir opener'ı geçirirdi. Öğrenci `UXIA-v0`nin altı cross-cutting state'ini `VDSX-v0` tonlarıyla görüyor; shell yalnız normal kullanım mümkünken çiziliyor; `ai_unavailable_core_available` yalnız core çalışırken üretiliyor; **tek aksiyon `RECHECK` ve reset temsil edilemez**. Kullanıcının kararıyla restore **mekanizması** burada kuruldu, Profile kontrolleri 16D'de: arşiv ürünün kendi şemasında; export yapıldığı an doğrulanıyor; restore arşivi kopyalayıp yalnız kopyayı doğruluyor ve migrate ediyor, tek atomik rename ile değiştiriyor, eski canlı dosyanın hot journal'ını önce kenara alıyor ve reddedilen arşiv hem canlı profili hem arşivi byte byte değiştirmiyor. Mutation 16/16, ama ikisi başta yaşadı: sahte journal SQLite için hot değildi ve tablo kümesi kontrolü hiç egzersiz edilmiyordu — iki test de güçlendirildi. **T6 çalıştırılmadı**; telefon bağlı değildi ve hiçbir cihaz sonucu iddia edilmiyor.

Canonical: `docs/APP_HEALTH_SPEC.md` / D-086.

## 12.13 11A Today ekranı — TDYX-v0 / D-087

İlk destination interior'u kuruldu ve **AŞAMA 11 başladı**. Ana invariant: **Today kanonik planner/state gerçeğinin projeksiyonudur** ve kendisine verilmeyen hiçbir şeyi hesaplamaz; planner 12'de olduğu için ekran plan uydurmak yerine dürüst boş/yükleniyor state'lerini gösteriyor. Kodu kontratlara karşı okumak iki kusur buldu: `FileContentSource.resource` `TODO()` idi ve `Surface.all` başlatılmış bir `val` olduğu için kayıt null içerebiliyordu. Vokabülerler (`THUX-v0`nin 12 state'i ve 6 adımlı precedence'ı, yedi purpose, 8 reason ve 7 attention family, region sırası, tonlar) sahiplerinden kopyalandı. Reason'da serbest metin, satırda mastery alanı, sunumda türetilmiş kapasite hükmü yok; bayat plan, blocked görev, değiştirilen plan ve doğrulanmamış oturum süzülür. `empty_valid` burada üretiliyor. Read path plan okumuyor: `planned_task` purpose/gerekçe/süre kolonlarını taşımıyor ve eksiği varsayılanla doldurmak iddia üretmek olurdu. Mutation 16/16; validator 150/150. **T6 çalıştırılmadı.**

Canonical: `docs/TODAY_INTERIOR_SPEC.md` / D-087.

_Bu bölüm 11A'nın POST'unda eksik kaldı (sync betiği bu dosyaya ulaşmadan çöktü) ve 11B POST'unda eklendi._

## 12.14 11B Task runner — RNRX-v0 / D-088

Task Runner kuruldu ve ürünün öğrenci işini yazan ilk yolu açıldı. Ana invariant: **runner bir execution surface'tir** — planner, mastery otoritesi, prerequisite otoritesi ya da evidence evaluator değil. Runner kodundan önce main'de 11A'nın kendi sync betiğinden kalan kusurlar bulundu: `"İ".lower()` Python'da U+0307 birleşik noktası üretiyor ve beş Türkçe kelimeye sızmıştı; betiğin çöküp yeniden koşması da `HANDOFF_STATE` ve `MASTER_PLAN`da mükerrer satır ve burada eksik bir bölüm bırakmıştı. Hepsi düzeltildi ve validator artık U+0307'yi tarıyor. Vokabülerler `TRUX-v0`den, tonlar `VDSX-v0`den ve core'da. **Girişte hiçbir şey varsayılmaz:** her koşul doğrulanmalı, doğrulanamayan `unmet` diye adlandırılır; Today yalnız görevinin seçili olduğunu doğrulayabildiği için bugün hiçbir görev başlatılamaz ve dürüst sonuç budur. Yardım her zaman istenebilir, istenmeden verilmez, H3/H4 sonuç ölçüm diliyle açıklanmadan verilmez — kural kapsam koşulu olmadan, yazıldığı gibi uygulanıyor. **Bir deneme bir transaction'dır:** attempt, artifact, provenance ve assistance birlikte ya da hiç; evidence yazılmaz, türetilmiş olgular saklanmaz, `DDM-v0`nin adlandırmadığı alanlar uydurulmaz. Mutation 18/18; validator 147/147. **T6 çalıştırılmadı.**

Canonical: `docs/TASK_RUNNER_SPEC.md` / D-088.

## 12.15 11C Session state — SESX-v0 / D-089

Durmanın gerçekte neyi sakladığı kilitlendi. Ana invariant: **bir duraklatma işin nerede olduğunu saklar, ne kadar sürdüğünü ya da ne kadar iyi gittiğini değil.** Checkpoint `SRR-v0` `ResumeContext`'idir ve 10D'nin bu adıma bıraktığı `resume_checkpoint.context` kolonuna yazılır; `TRUX-v0`nin high-stakes pause için şart koştuğu **işaret** alan listesinde olmadığı için saklanan durable pause türü olarak eklendi. Biçim sürümlü (`resume_context/1`) ve **katı** çözülür: tam okunamayan hiçbir şey tahminle okunmaz ve okunamayan satır yok sayılmaz. Bir pause bir transaction ve tek append-only satırdır; attempt, evidence ya da projection yazmaz; tüketildi/son bayrağı yoktur. Sıradan bir pause yalnız dört güvenli-checkpoint koşulunun hepsi doğrulanınca durable olur; aksi `mid_segment_pause`'dur, yazılmaz ve runner state'ini değiştirmez. Resume yalnız checkpoint'in kanıtladığını doğrular: çözülen ve artifact göstermeyen bağlam için state bütünlüğü, sıradan pause için high-stakes koşulu. **High-stakes gap eşiği uydurulmadı** (13, kalibrasyon 18D). Bugün hiçbir checkpoint devam ettirilemez ve Today checkpoint sunmaz. Working session emergent, puansız ve saklanmaz (`DDM-v0` adlandırmıyor; tarih 16B); başlayan ilk koşuyla başlar, bir kez ve `TRUX-v0` sebebiyle biter. `readTruth` kayıtlı port incelmesi; şema değişmedi. Main'de 11A'dan kalan iki bozuk kelime (`çalıştırılmadı`, `doğrulanmamış`) düzeltildi ve korunuyor. Mutation 20/20; validator 146/146. **T6 çalıştırılmadı.**

Canonical: `docs/SESSION_STATE_SPEC.md` / D-089.

## 12.7 Dağıtım kapsamı — D-080

Ürün tek kullanıcı içindir, hiçbir uygulama merkezine yüklenmeyecek ve halka açık paylaşılmayacaktır; repo private'dır. Bu, `V1_SCOPE` §1'in tek-kullanıcı ilkesini genişletmez, **dağıtım** boyutunu kilitler. Düşen iş: store yayın gereksinimleri, dağıtım imzalama seremonisi, güvenlik amaçlı obfuscation/pinning, çok kullanıcı/hesap sistemi ve cihaz matrisi — AŞAMA 19 buna göre daralır. `minSdk` ve device QA tek hedef cihaza sabitlenir; **hedef cihaz hâlâ repoda kayıtlı değildir**. Değişmeyenler: AI yetki sınırları, `refusal != yanlış cevap`, schema-constrained evaluator çıktısı, `evaluation_pending` kuralları, append-only ve exposure kalıcılığı — bunlar yabancılardan korunmak için değil, kullanıcının kendi kanıt kaydını bozmamak için vardır. `AIAX-v0`ın asgari-içerik sınırı korunur, gerekçesi bu kapsamda maliyet ve AI-kapalı yolun çalışır kalmasıdır. `key_in_platform_secure_storage` invariant'ı korunur; gevşetilirse açık amendment olarak yapılır. API key repoya commit edilmez.

## 12. Proje hafızası / repository hygiene — D-050

Her numaralı adım sonunda yaşayan current-state dosyaları istisnasız kontrol edilir. `PROJECT_CONTEXT.md`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` eski step'te bırakılamaz. Ayrıca repo-wide stale-reference taraması yapılır.

Dosya rol matrisi ve exact checklist: `docs/PROJECT_MEMORY_PROTOCOL.md`.

D-043 standalone specialization-stage kararı geri çekilmiştir; canonical değildir.

## 12.16 11D Günlük mikro quiz — DMAX-v0 / D-090

Bir ölçümün baştan sona dürüst çalışması kilitlendi. Ana invariant: **assessment session bir kanıt toplama akışıdır** ve **bir item yalnız validation'ının, evaluator'ının ve Objective'in kendi kanıt profilinin izin verdiği kadarını taşıyabilir.**

Curriculum bölgesinin hiç yazıcısı yoktu; `publishCurriculum` tek yazma yolu oldu: tek transaction, yazmadan önce karar verilen ret, yayımlanmış sürümün asla üzerine yazılmaması, düzeltmenin yeni sürüm olması. Authored paket katı ayrıştırılır; bilinmeyen bölüm/anahtar, tekrar eden anahtar, eksik alan, pinlenmemiş referans ya da bilinmeyen değer paketin tamamını reddeder ve ayrıştırılamayan paket hiçbir parçasıyla sunulmaz. `DDM-v0`nin adlandırmadığı item alanları sütun uydurmak yerine authored içerikte kalır.

**Güven mağazanın validation kaydıdır**, item'ın kendi iddiası değil; etkin kullanım tavanı uygulanabilir en kısıtlayıcı kuraldır ve deklare edileni asla aşamaz. Kanıt uyumuna Objective karar verir; mastery ölçümü Objective'in direct tipini gerektirir. Exposure yalnız item gerçekten sunulduğunda ve çözüm gösterildiğinde yazılır.

Tek interior bütün scope'lara hizmet eder: gönderilen sınır donar, gönderilmemiş sınırlar serbestçe gezilir, boş bırakmak yanlış sayılmaz, yardım engellenmez ve sonucu açıklanır, beş koşullu recomposition tamamlanmış kanıta dokunmaz, itiraz kanıtı contested tutar ve sonuç semantiktir — puan, yüzde, not ya da eşik alanı **yoktur**. Kısa artifact gövdesi referansın içinde (`data:` URI) taşınır ya da reddedilir; kırpılmaz.

**Mutation koşucusunun Gradle'ı hiç çalıştırmadığı bulundu** (`cmd /c gradlew.bat` çalışma dizininden çözülmüyor). Düzeltildi; 11D 27/27 (üçü testler güçlendirildikten sonra) ve **11C'nin seti dürüstçe yeniden koşuldu: 20/20**. 11C'nin POST betiğinin bıraktığı üç mükerrer `12.15` bölümü de temizlendi. Validator 188/188, kendi mutation testi 27/27. **T6 çalıştırılmadı.**

Canonical: `docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md` / D-090.

## 12.17 11E Gün sonu — EODX-v0 / D-091

Günün sonunda neyin dürüstçe söylenebileceği kilitlendi ve **AŞAMA 11 kapandı**. Ana invariant: **gün sonu bir hüküm değil, zamanda bir sınırdır.** Gün, öğrencinin çalışma günü değiştiği için kapanır — öğrenci bir şeyi bitirdiği ya da bitiremediği için değil — ve kapanınca kimsenin durumu değişmez.

Kabul edilmiş bir gün-sonu spec'i yoktu; kurallar uydurulmadı, parçaların sahiplerinden türetildi: state'ler ve sayma kuralları `SPWX-v0`, yokluk anlamı `SRR-v0`, günün kendisi `DDM-v0`nin üç değerli zamanı, öncelik `APHX-v0`, tonlar `VDSX-v0`, render bölgesi `THUX-v0`.

**Gün, satırın kaydettiği çalışma günüdür** ve instant'tan yeniden hesaplanmaz — yeniden hesaplamak, yaz saati ya da bir uçuşun işi bir günden diğerine sessizce taşımasının yoludur. Sayım satırın kendi gün kolonuna karşı yapılır; kendi günü olmayan tablo sayılmaz. **Yeni gün boş başlar:** envanter, yarım plan ya da yükümlülük sınırı geçmez ve gün geriye dönmez; günü kapatan bir öğrenci aksiyonu yoktur.

**Söylenebilenler:** yazılanların etiketli envanteri (deneme, görülen soru, kaydedilen durak, değerlendirilen kanıt) — toplam, oran, yüzde ya da hedef yok; yalnız kanonik bir engine bildirdiyse bir değişiklik; okunamayan sayım "okunamadı" olarak. **Söylenemeyenler:** günün başarılı/başarısız olduğu, seri, tamamlanma yüzdesi, "çalışılan dakika", yarına borç, etkinliğin öğrenme sayılması.

Boş gün `empty_no_evidence_yet` ve nötr; günler arası boşluk hiç çizilmez. Özet Today'in gün bağlamında render ediliyor; yeni surface yok, grafik/halka/takvim ızgarası yok. `countTruth` kayıtlı port incelmesi ve hiçbir şey yazmıyor. Mutation 20/20 (E20 ikinci bir korumanın birincisini maskelemesi yüzünden ilk turda kaçtı; T2 kontrolü artık her reddin kendi kuralını adlandırdığını doğruluyor). Validator 128/128, kendi mutation testi 26/26. **T6 çalıştırılmadı.**

Canonical: `docs/END_OF_DAY_SPEC.md` / D-091.

## 12.18 12A Mastery Engine v1 — MSTX-v0 / D-092

İlk engine kuruldu: kanıt yorumlanıyor ve mastery ondan projekte ediliyor. Ana invariant: **mastery tek bir soru sorar — yardımsız yapabiliyor mu?** Yardımlı iş, görülmüş çözüm, doğrulanmamış değerlendirme, itirazlı soru ve bozuk prerequisite üzerinde yapılmış iş skora girmez; hiçbiri ceza değildir, başka bir sorunun cevabıdır.

11B ve 11D kasten kanıt yazmamıştı, bu yüzden `DDM-v0`nin dört ekseni ürün tarafından hiç yazılmamıştı. `RecordEvidence` bunu kapatıyor: deneme başına tek transaction, hedeflenen her Objective için bir satır, Objective'in sürümü kendi satırında pinli. **Yanıtsız değerlendirme hiçbir şey yazmaz** (`AIAX-v0`: refusal yanlış cevap değildir) ve **ölçülemeyen cevap sıfır değildir** — `invalid` olarak, sonuçsuz yazılır; sıfır öğrencinin yaptığı bir şeydir, bu değil.

Motor `GRE-v0`ün kendisi: bağımlı grup tek gruptur (aynı soruyu on kez yanıtlamak bağımsız kanıtı şişirmez), pencere son beş gruptur, ortalama eşit ağırlıklıdır ve hiçbir çarpan yoktur. Standart ve kritik kapılar ayrıdır; kritik Objective yalnız basic kanıtla geçemez. **Skill ancak her required ve critical Objective kendi başına geçerse mastered olur** — ortalama olsaydı bir Objective'deki parlak sonuç eksik olanı gizlerdi. Histerezisin iki yarısı da var: ilk temiz çelişki doğrulama açar ve mastery'yi silmez; yeniden kontrol de düşerse doğrulama kapanır ve kapılar yeniden karar verir.

Projeksiyon **yeniden kurulur, düzenlenmez**: aynı kanıt aynı satırı üretir, truth yazılmaz, yalnız mastery ekseni yazılır ve diğer motorların eksenleri taşınır. Her satır `policy_version`, `truth_watermark`, `built_at_instant` ve `input_curriculum_version` taşır; watermark kanıttan **önce** okunur, böylece rebuild sırasında düşen bir yazma satırı sessizce yanlış değil, tespit edilebilir biçimde bayat yapar. Yayımlanmış curriculum yoksa hiçbir şey yazılmaz.

Her sabit (`5`, `0.80`, `2`, `3`) `GRE-v0`ün kalibre edilmemiş cold-start sezgisidir ve 18C'nin sahipliğindedir; hiçbiri olasılık, güven ya da yüzde değildir. Mutation 33/33 (G26 ve G33 ilk turda kaçtı: biri tüm fixture'ların v1 olması, diğeri aralık koşulunu hiç deneyen bir test olmaması yüzünden). Validator 164/164, kendi mutation testi 37/37 — validator'ın imza okuyucusunda 11D'dekiyle aynı sınıf hata bulundu ve düzeltildi. **T6 çalıştırılmadı** ve motor uygulamada erişilebilir değil: değerlendirme üreten bir yol yok (12C).

Canonical: `docs/MASTERY_ENGINE_IMPL_SPEC.md` / D-092.

## 12.19 12B Prerequisite Engine — PRQX-v0 / D-093

İkinci engine kuruldu: hedef üzerindeki işin yorumlanabilir ve adil kanıt üretip üretmeyeceğine karar veren kapı. Ana invariant: **bir eksik prerequisite yalnız gerçekten ona bağlı işi bekletir.** `review_due` unutma değildir ve bloklamaz, soft eksik hiçbir şeyi kilitlemez, priority kapıyı aşamaz ve bekleyen aday başarısız bir ihtiyaç değildir.

Readiness `PRG-v0`ın dört değeridir (`ready`, `ready_due`, `uncertain`, `not_ready`) ve sayı değildir; mastery, retention ve weakness eksenlerinden okunur, harmanlanmaz. Açık remediation hazır olmamaktır; çelişen mastery `uncertain`dir; kapanan remediation yeni kanıt olmadan kilidi açmaz. **Henüz değerlendirilmemiş eksen adlandırılır, kötü haber sayılmaz**: retention motoru (13) yokken doğrulanmış bir Skill'in tekrar gerektirdiğini hiçbir şey söylemedi. Eligibility `PRG-v0` §4/§5 matrisi: normal hard + `uncertain` koşullu uygundur, kritik ya da strict istenmişse bekler; task'in kendi gereksinimi graph söylemese de hard'dır; kapı eksik readiness'te kapalı kalır; karar sıradan bağımsızdır.

Kod yazmadan önce bulunanlar: **authored 950 kenarın hepsi `draft`**, 851'i hard. `KGC-v0` §27 draft'a runtime seçimi vermiyor; "draft'ı yok say" okuması içerik sevk edildiği anda her hard prerequisite'i sessizce düşürürdü. Draft kenar ne düşürülüyor ne sessizce uygulanıyor: `edge_not_published` olarak adlandırılıyor ve aday bekliyor. Graph'ta tek strictness profili var (`default_prg_v0`); başkası tahmin değil metadata sorunu. `contamination_risk_if_missing` DDM'de kolon değil (15). `skill_state` dört motorun eksenini tek watermark altında taşıyor; her motor kendi eksenini kendi watermark'ıyla yazsaydı başka motorun bayat ekseni güncel görünürdü — bu yüzden 12B yalnız sahibi olduğu `prerequisite_readiness`ı yazıyor ve birleştirme 12D'nin. `contaminated` değeri adaptördeydi; artık core'un.

Bekleması gereken aday üzerindeki deneme `contaminated` snapshot'ı yazar ve mastery motoru onu skordan dışlar: öğretilmemiş bir şeydeki hata hedefe karşı yazılmaz. Readiness satırının watermark'ı mastery satırınınkidir, yoksa `0`dır. İki port incelmesi (`skill`, `prerequisiteEdgesInto`); yeni arayüz, şema değişikliği, migration ya da index yok. Mutation 44/44, validator 184/184, kendi mutation testi 42/42. **T6 çalıştırılmadı** ve kapı uygulamada erişilebilir değil: onu soran planner yok (12C).

Canonical: `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md` / D-093.

## 12.20 12C Planner Engine v1 — PLNX-v0 / D-094

Üçüncü engine kuruldu: bugün hangi açık ihtiyaçların, hangi görevle, öğrencinin gerçekten ayırdığı süre içinde karşılanacağına karar veren planner. Ana invariant: **önce semantik öncelik, sonra fiziksel sığma.** Priority bloklanmış ya da güvenilmeyen görevi kurtaramaz, kapasite önceliği yeniden yazmaz, gün uzatılmaz ve hiçbir görevin karşılamadığı ihtiyaç açık kalır — yarının borcu da başarısızlık da değildir.

Sıra `PDT-v0` §17'nin kendisi: güncel durum → ihtiyaçlar → sınırlı adaylar → güven → `PRG-v0` uygunluğu → `PBR-v0` önceliği → kapasiteye sığma. Kapasite D-033'ün sırasıyla çözülür (bugünkü değişiklik → günün profili → planlı varsayılan → normal profil); sert bütçe asla aşılmaz, planlama bütçesi `%10` rezervi tutar ve `10` dakikanın altında yeni öğretim yapılmaz. İhtiyaçlar her motorun kendi ekseninden açılır; yazılmamış eksen ve rotada olmayan Skill hiçbir şey açmaz. Bantlar ve on alanlı rank vektörü `PBR-v0`: alan alan karşılaştırılır, toplanmaz, rastgele tie-break yok; P0 gerçek bir blocker ister, kritik etiket tek başına yetmez; `review_due` bakımdır. Seçim: sığ → güvenli böl → küçük alternatif → ertele; atomik kanıt sınırı bölünmez, bir ihtiyaca tek görev.

Kod yazmadan önce bulunanlar: `planned_task` bir Today satırını taşıyamıyor (yalnız Skill ve pozisyon); hiçbir authored görev yok; hiçbir yerde starvation eşiği yok (7C bunu uydurmayı açıkça reddetmişti); retention ve weakness henüz yazılmadı; authored her Skill `draft`. Çözüm: `PDT-v0` izinin tamamı — seçilen her görevin amacı, başlığı, etkinliği ve dakikası dahil — `planner_decision_trace.trace` kolonunda katı ve sürümlü `planner_trace/1` biçiminde; kolon uydurulmadı. Adaylar içeriğin bir ihtiyaca cevabı (`taskCandidates`), dosya adaptörü bugün boş döner ve ihtiyaç 'geçerli aday yok' olarak kaydedilir, reason kodu uydurulmaz. Starvation baskısı girdi; ürün hiçbirini vermez (18C). İhtiyaç başına aday sınırı `5`, öğrenme anlamı olmayan mühendislik sınırı (18E).

Plan truth'tur: `plan_version`, `planned_task` ve iz tek transaction'da eklenir ve asla düzenlenmez; watermark durumdan önce okunur. İki port incelmesi (`publishedSkills`, `taskCandidates`); yeni arayüz, şema değişikliği ya da migration yok. Mutation 55/55, validator 226/226, kendi mutation testi 46/46. **T6 çalıştırılmadı** ve planner uygulamada çağrılmıyor: kapasite ayarı (16D), authored görev (15) ve Today'in izden gerekçe göstermesi (12E) yok.

Canonical: `docs/PLANNER_ENGINE_IMPL_SPEC.md` / D-094.

## 12.21 12D Replan — RPLX-v0 / D-095

Planın nasıl değiştirildiği kodda. Ana invariant: **bir plan düzenlenmez, gerekçesi olan yeni bir sürümle değiştirilir.** Öğrencinin başladığı iş korunur, yalnız başlanmamış kalan yeniden çözülür, gün kendiliğinden büyümez ve yokluktan dönüş hiçbir şeyi tekrar oynatmaz — yokluk borç, başarısızlık ya da çürüme değildir.

Üretim türü depodan okunur: plan yoksa `initial`, en yeni plan başka bir çalışma gününe aitse `reentry` (bir olay gelse bile), bugünün planı varsa ve olay geldiyse `replan`. **Aynı gün olay yoksa yeni sürüm yazılmaz** — 12C'nin `build()`u bu durumda gerekçesiz ikinci bir initial plan yazıyordu; sözleşme değil davranış düzeltildi. Olaylar D-033 §16, `PBR-v0` §17 ve `PRG-v0` §19'dan; her biri `PDT-v0` §8.10 kodunu taşır, kodu olmayana (`task_completed`) kod uydurulmaz; odak tercihi olmadığı için `user_focus_changed` kabul edilmez (16D).

Kalan bütçe D-033 §8: bugün için yeni kapasite günü değiştirir, bildirilen kalan süre kalanın kendisidir, diğer her olay günü korur; korunan dakikalar üstten düşer, kalan asla negatif olmaz ve günle aynı kurala uyar. **Depo hangi planlı görevin başladığını söyleyemiyor** (`attempt`in `planned_task` bağı yok), bu yüzden devam eden işin sahibi olan çağıran bildirir; her pozisyon önceki plana karşı doğrulanır, bilinmeyen pozisyon replan'ı reddeder ve reddedilen replan hiçbir şey yazmaz. Korunan görevler önde, değişmeden ve işaretli; korunan görevin ihtiyacı iki kez karşılanmaz.

Re-entry `SRR-v0`: dünkü plan tekrar oynatılmaz ve hiçbir şeyi korunmaz, günün normal bütçesi geçerlidir, yokluk starvation'ı beslemez; iz `SRR-v0` §17 bağlamını ve `PDT-v0` §8.8 kodlarını taşır — hiçbiri skor, ceza ya da borç değil. Güvenli duraklatma (11C) açık devam ihtiyacını P2 yapar ama otomatik seçmez; high-stakes duraklatma bağımsız iş olarak devam ettirilmez. İz `planner_trace/2`; `/1` katı biçimde okunmaya devam eder.

12D'ye devredilip bugün kurulamayanlar uydurulmadan yeniden bağlandı: `skill_state` birleştirmesi 13, deneme sonrası recompute zinciri ve deneme→planlı görev bağı 15, planner'ı uygulamadan çağırmak 16D, bağımlıların ters invalidation'ı 18E. Mutation 33/33, validator 157/157, kendi mutation testi 38/38. **T6 çalıştırılmadı** ve planner uygulamada çağrılmıyor.

Canonical: `docs/REPLAN_SPEC.md` / D-095.

## 12.22 12E Explanation / reason codes — RSNX-v0 / D-096

Planner'ın kararını öğrencinin okuduğu açıklamaya çeviren katman ve Today'in planı okuması kodda. Ana invariant: **bir açıklama karar izinin projeksiyonudur: her cümle izin kaydettiği bir reason code'a ya da izin bir alanında tuttuğu bir olguya dayanır. Planner'ın kaydetmediği bir gerekçe kurulamaz, süreye sığmayan iş 'daha az önemli' diye anlatılmaz, bekleyen iş gerçek Skill blocker'ını adlandırır, `review_due` unutmak değildir ve yokluk borç değildir.**

Kod yazmadan önce bulunanlar: iz bir blocker'ı adlandıramıyordu — kapının cevabı karar ve sıralama için kullanılıp atılıyordu, açıklama anında kapıyı yeniden koşmak ise planner'ın vermediği bir kararla açıklamak olurdu; bu yüzden her aday izi ilgili Skill'leri planlama anında kaydediyor (`PDT-v0` §7 `related_refs`) ve iz `planner_trace/3` oldu, `/2` ve `/1` bunu iddia edemiyor. Today planı okuyamıyordu (`plan = null`) ve `latestPlan()` satır id'si vermiyordu; artık planın kendi `planned_task` satırları konum sırasıyla dönüyor (yeni port yok). Planner'ın yazdığı ad alansız `independent_branch_available` `PRG-v0` §20'nin açıklama girdisi; katalog `PDT-v0` §8'in on ailesini ve §20'yi sırasıyla tutuyor.

Plan yalnız izi kendi satırlarını anlatıyorsa okunur (aynı gün, aynı konumlar ve Skill'ler, tekrar yok, seçilen her görevin ihtiyaç kararı); değilse okunamaz, Today `error_recoverable` gösterir ve hiçbir şey tahmin edilmez. Her satır kendi `planned_task` id'sine işaret eder, dakikalar bugün planlanan kısımdır, kapasite planner'ın kaydıdır; korunan iş planın parçasıdır ama yeniden başlatılacak iş olarak sunulmaz; replan ve bekleyen ihtiyaç açıklamaya bağlanan attention olur. Satır aileleri ihtiyacın tetikleyicisinden, devam ettirilen güvenli duraklatmadan, sığdırmadan ve hattan gelir; doğrulanmamış zayıflık 'onarılan' değil 'doğrulanan' durumdur; İngilizce yalnız paralel hattın kendi ritminde sebeptir; 11A'nın devam etiketi artık başlanmış demiyor.

Açıklama dört parça: neden bugün (önce ihtiyaç, sonra belirleyici öncelik nedeni, ilgili Skill'leriyle uygunluk ve sığdırma; bant asla neden olarak gösterilmez), neden bugün değil (süreye sığmayan iş daha az önemli değil; bekleyen ihtiyaç Skill'lerini adlandırır; görevi olmayan ihtiyaca kod uydurulmaz), plan neden değişti (tetikleyici kodu ya da yalnız değiştiği; yokluk başarısızlık ve borç değil) ve ne zaman yeniden bakılacağı (asla tarih değil). Şablonlar `DayCopy` gibi çekirdekte; her katalog kodunun cümlesi var ve hiçbiri unutmayı iddia etmiyor. Mutation 52/52, validator 219/219, kendi mutation testi 40/40. **T6 çalıştırılmadı** ve planner uygulamada çağrılmıyor.

Canonical: `docs/PLANNER_EXPLANATION_IMPL_SPEC.md` / D-096.

## 12.23 12F Sanal kullanıcı testleri — VUSX-v0 / D-097

3H'nin sanal kullanıcıları artık gerçek koddan geçiyor. Ana invariant: **sanal kullanıcı durumdur, cevap değil: 3H'nin sanal kullanıcıları artık gerçek kapıdan, planner'dan, replan'dan, depodan, Today'den ve açıklamadan geçiyor. İhtiyaç durumdan, uygunluk kapıdan, seçim planner'dan, açıklama onun yazdığı izden geliyor; koşulamayan senaryo elle simüle edilmez, sahibiyle adlandırılır.**

3H bu senaryoları kod yokken, politika seviyesinde ve akıl yürüterek geçirmişti ve PASS'inin 12F'nin yerine geçmediğini söylemişti. Sanal kullanıcılar bir kez, `core-engines` test fixture'ı olarak tanımlandı; motor seviyesi gerçek kapı ve planner'ı, yolculuklar `BuildDailyPlan` ile re-entry, duraklatma, replan ve Today'i, açıklama testleri aynı planların izlerini koşuyor. Hiçbir sanal kullanıcı planner'a kapı kararı, açıklamaya elle yazılmış iz ya da durumun açmayacağı bir ihtiyaç vermiyor; yolculuk deposu planlama sırasında kanıt geçmişi okunursa testi düşürüyor.

16 senaryodan 15'i koşuldu. S06 koşulamıyor: `VDW-v0`'ın tanısal atlamasının uygulaması ve plan içinde sahibi yoktu; invariant 12 ile birlikte 13'e bağlandı. Invariant 17 yapısal olarak doğrulandı (ihtiyaç başına en çok beş aday, bugünün durumuyla sınırlı iz, geçmiş büyüdükçe artmayan okuma); gecikme ve bellek 18E'de, cihazda.

Koşmak iki şey buldu. Otuz gün sonra dönen öğrencinin 78 due becerisi `planner_explanation`da 78 ayrı "bugün değil" satırıydı — `SRR-v0` §9.1'in yasakladığı backlog; aynı kaydedilmiş nedenle gelmeyen ihtiyaçlar artık hiçbir şeyi düşürmeyen tek satır ve 12E spec'ine açık not düşüldü. 3H'nin S07 örnek günü açıklayıcıymış: her due becerinin görevi varsa `PBR-v0` aciliyeti günü tekrarlarla doldurur ve yeni öğrenme süre için bekler; kural değiştirilmedi, koruma 18C'nin eşikleri. Mutation 27/27 yalnız sanal kullanıcı testleri koşarken; validator 148/148, kendi mutation testi 30/30. **T6 çalıştırılmadı.** **AŞAMA 12 tamamlandı.**

Canonical: `docs/VIRTUAL_USER_TESTS_SPEC.md` / D-097.

## 12.24 13A Haftalık sınav — WBAX-v0 / D-098

Haftalık sınav kodda. Ana invariant: **bir hafta bir kimliktir, kota ya da son tarih değil.** Ölçmeye değer olan durumdan gelir, bir Skill tek kez ölçülür, güvenilir ve taze bir item'ı olmayan slot kapsama değildir, hafta kendi dakikasını ve kuyruğunu eklemez ve sınavı yapılmadan geçen bir hafta geride hiçbir şey bırakmaz.

Kod yazmadan önce bulunanlar: hiçbir ürün kodu `assessment_session` yazmamıştı ve blueprint için yeri yoktu (10D bunu 13'e bırakmıştı); item modelinde `QAB-v0`ın süresi ve rol uygunluğu yoktu; deneme oturumunu adlandıramıyordu; exposure okunamıyordu; `WBA-v0` döngü sınırı tanımlamıyordu; `VDW-v0` 13'ün hiçbir alt adımında değildi.

Döngü kaydedilmiş çalışma gününün ISO haftasıdır — ürün varsayılanı, bilimsel değer değil, kullanıcı onayladı. Hafta bir kez kurulur; aynı hafta hiçbir şey yazmaz; ölçülecek bir şey yoksa hiçbir şey yazılmaz; yeni hafta güncel durumdan taze kurulur ve iki kaçırılan hafta yapısal olarak yığılamaz.

Havuz planner'ın eksenlerden açtığı ihtiyaçlardır (artı sahiplerinin verdiği paralel hat ve entegrasyon); composer kendi ihtiyacını açmaz. Doğrulama, bağımlı işi gerçekten bekleten kritik Skill (planner izinden), son blueprint'ten beri kanıt alan devam, retention (`retain` kalır), İngilizce paralel hat; yeni öğrenme, tanı ve açık remediation ölçülmez. Bir Skill tek kez, §9 sırasında; bant ve rank planner'ınki.

Item seçimi `QAB-v0` §31–§33: indeksli yüzler önce, okuma 5 ile sınırlı (18E), en kısa asla önce değil; her ret bir kural adlandırır (mağaza güveni, kapalı kalan kapı, çözülmüş aile, görülmüş item, aile/testlet tekrarı, beyan edilmemiş süre). Slotlar planner'a mevcut ihtiyaçların adayı olarak gider; `BuildDailyPlan` bu haftanın sunulmamış slotlarını ekler; gün uzatılmaz.

Tek interior; araç beyanı kesişim; deneme oturumunu adlandırır. Sonuç `WBA-v0` §27: puan yok, boş bırakmak yanlış değil, değişiklik yalnız motorların bildirdiği; bu oturumda eksik görünen ön koşula dayanan iş `contaminated` yazılır (ileriye dönük; geriye dönük 13D). Şema v3 `assessment_session.blueprint` + CHECK, katı `weekly_blueprint/1`, yeniden kompozisyon ekler. Dört port incelmesi; port sayısı dört. Beş 12x yaşayan kapı daraltıldı. `D-099` ile `13F — Tanısal atlama (VDW-v0)` eklendi. Mutation 42/42, validator 210/210, kendi mutation testi 25/25. **T6 çalıştırılmadı.**

Canonical: `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md` / D-098.

## 12.25 13B Aylık sınav — MCAX-v0 / D-100

Aylık sınav kodda. Ana invariant: **bir ay daha geniş bir penceredir, daha ağır bir sınav değil.** Ölçmeye değer olan yine durumdan gelir, bir Skill tek kez ölçülür, kritik bir Skill yalnız bir nedenle yeniden doğrulanır, aylık etiket kanıta ağırlık eklemez, ay kendi dakikasını ve kuyruğunu eklemez ve sınavı yapılmadan geçen bir ay geride hiçbir şey bırakmaz.

Kod yazmadan önce bulunanlar: `MCA-v0` §4 aylığı ortak kontratın uzantısı sayıyor ama 13A kontratı yalnız haftalık adlarla ve haftalık rollere sabit yazmıştı; v3 CHECK'i aylık satırı kapsamıyordu; item yalnız haftalık rol beyan edebiliyordu; iki aylık rolün (transfer, profesyonel kontrol noktası) üreticisi yok; `MCA-v0` ay sınırı tanımlamıyor.

Kontrat genelleştirildi: `AssessmentBlueprint` kapsamını taşır, slot rolü `SlotRole`'dür, karışık blueprint temsil edilemez ve ortak kompozisyon tek `BlueprintComposer`'dır. Hiçbir haftalık değer değişmedi; 13A testleri yalnız tip adları güncellenerek geçiyor.

Döngü kaydedilmiş çalışma gününün takvim ayıdır — haftalık kuralı izleyen ürün varsayılanı. Ay bir kez kurulur; yeni ay önceki aylık oturumu adlandırır, pencereyi ondan ölçer ve borç taşımaz. Havuz planner'ın ihtiyaçlarıdır: kritik Skill yalnız açık doğrulama/çelişki, vadesi gelmiş tekrar ya da bağımlı işi bekletmesi nedeniyle yeniden doğrulanır; kalıcı endişe motorun durumundan gelir; pencere içindeki required devam boylamsal örnektir; supporting/optional değildir; tekrar `retain` kalır. Transfer ve kontrol noktasının üreticisi uydurulmadı (15).

Slotlar yalnız aylık kapsama uygun ve aylık role beyanlı item alır; planner'a mevcut ihtiyaçların alternatif adayı olarak gider ve ihtiyaç başına en çok bir görev seçilir. Sonuçta puan yok; dört boylamsal liste yalnız kendi rolünün temiz kanıtını taşır; temiz negatif yeniden doğrulama değildir. Şema v4 biçim trigger'ı; `monthly_blueprint/1`; port sayısı dört. 13A validator'ı daraltıldı. Mutation 48/48, validator 259/259, kendi mutation testi 28/28. **T6 çalıştırılmadı.**

Canonical: `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md` / D-100.
