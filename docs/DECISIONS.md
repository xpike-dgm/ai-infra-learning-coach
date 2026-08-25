# Product Decisions Log

Bu dosya kalıcı ürün kararlarını kaydeder. Ayrıntılı teknik davranış ilgili canonical spec dosyalarındadır.

## D-001 — Takvim/gün sayacı ana ilerleme metriği olmayacak
**Durum:** Kabul edildi / süre ufku D-041 ile genişletildi  
`Gün X / toplam gün` professional progress değildir; zaman tek başına readiness vermez.

## D-002 — İlerleme mastery tabanlı olacak
**Durum:** Kabul edildi  
Ders/task completion tek başına öğrenme değildir.

## D-003 — Knowledge graph takvimden öncelikli
**Durum:** Kabul edildi  
Takvim kapasiteyi; prerequisite/mastery ise hangi konunun uygun olduğunu belirler.

## D-004 — Eksik konu tüm programı dondurmaz
**Durum:** Kabul edildi  
Yalnız gerçekten bağımlı branch bekler.

## D-005 — Haftalık/aylık assessment programı değiştirecek
**Durum:** Kabul edildi  
Assessment yalnız rapor/not değildir; canonical evidence/state pipeline üzerinden gelecek planı etkiler.

## D-006 — İngilizce paralel ilerleyecek
**Durum:** Kabul edildi

## D-007 — Ana kariyer rotası systems → GPU → AI infrastructure
**Durum:** Kabul edildi / D-042 ile Python foundation eklendi  
Technical English paralel → Python → C → Linux/Git/Shell → DS&A → Modern C++ → Architecture → OS/Memory → Concurrency → Networking → Distributed/Storage → Cloud/Observability → Performance → GPU → CUDA → Triton → ML/Transformer → Inference/Serving → Multi-GPU → AI Infrastructure → Open Source/projects/capstone.

## D-008 — Uygulama kişisel kullanım için
**Durum:** Kabul edildi  
Auth/payment/social/admin/multi-tenant SaaS varsayılan kapsam dışıdır.

## D-009 — Modern ve sade mobil UI
**Durum:** Kabul edildi  
Ana ekran `Bugün ne yapmalıyım?` sorusunu cevaplar.

## D-010 — Streak ana başarı metriği olmayacak
**Durum:** Kabul edildi

## D-011 — AI kullanımı yasaklanmayacak
**Durum:** Kabul edildi  
AI yardımı öğrenmeyi destekler; independent mastery yerine geçmez.

## D-012 — Full curriculum V1 ön koşulu değil
**Durum:** Kabul edildi / kapsam D-041 ile genişletildi  
V1 learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilebilir.

## D-013 — Süreler adaptif, prerequisite/mastery daha kalıcı
**Durum:** Kabul edildi

## D-014 — İlk iş doğrudan CUDA olmak zorunda değil
**Durum:** Kabul edildi  
C++ Systems, Systems Software, Linux/Infrastructure, Distributed Systems, Performance, uygun SRE/Cloud vb. bridge rol olabilir.

## D-015 — Geliştirme master plan üzerinden aşamalı yürütülecek
**Durum:** Kabul edildi — 2026-08-24

## D-016 — Research / Coding / Test ayrı AI rolleridir
**Durum:** Kabul edildi — 2026-08-24  
Ana yönetici karar/spec/GitHub hafızasından sorumludur. Ayrıntı: `docs/AI_AGENT_WORKFLOW.md`.

## D-017 — Adım kodları canonical indeks üzerinden yürütülecek
**Durum:** Kabul edildi / D-044 future-stage reindex istisnası  
Tamamlanmış adımlar sessizce renumber edilmez; açık plan değişikliğinde henüz başlanmamış future stages yeniden indekslenebilir.

## D-018 — V1 gerçek Android adaptive learning loop'u olacak
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SCOPE.md`.

## D-019 — V1 release acceptance + bağımsız QA'ya bağlı
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SUCCESS_CRITERIA.md`.

## D-020 — Product/V1 non-goals kilitlendi
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/NON_GOALS.md`.

## D-021 — Learning unit organizasyon ve mastery katmanları ayrıdır
**Durum:** Kabul edildi — 2026-08-24  
`Domain → Module → Topic → Skill → Learning Objective`; canonical mastery/prerequisite ana seviyesi Skill, atomik evidence Objective'tir. Ayrıntı: `docs/LEARNING_ENGINE_SPEC.md`.

## D-022 — Öğretme/yanlış/remediation/retention/replan davranışları bağlayıcı
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/LEARNING_BEHAVIOR_RULES.md`.

## D-023 — Topic state Skill verilerinden derived work state olacak
**Durum:** Kabul edildi — 2026-08-24  
`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`. Ayrıntı: `docs/TOPIC_STATE_MACHINE.md`.

## D-024 — Her numaralı adım öncesi/sonrası GitHub beyin tazelemesi zorunlu
**Durum:** Kabul edildi — 2026-08-24  
`PRE-STEP GitHub refresh → çalışma → POST-STEP GitHub sync`. Ayrıntı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

## D-025 — Mastery çok kaynaklı, Objective-matched evidence ister
**Durum:** Kabul edildi — 2026-08-24  
Recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project ayrıdır; time/completion/streak/self-confidence mastery değildir. Ayrıntı: `docs/MASTERY_SIGNALS_SPEC.md`.

## D-026 — AI/ipucu yardımı independent mastery ile eşit değildir
**Durum:** Kabul edildi — 2026-08-24  
H0–H4, timing, provenance ve fresh recheck davranışı. Ayrıntı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`.

## D-027 — MASTER_PLAN canonical execution state ile senkron tutulacak
**Durum:** Kabul edildi — 2026-08-24

## D-028 — Mobil performans/akıcılık first-class requirement
**Durum:** Kabul edildi — 2026-08-24  
Bounded/incremental hesap, async ağır işler ve gerçek cihaz QA zorunlu.

## D-029 — İlk Beta-style mastery candidate'ı
**Durum:** YERİNE D-031 GEÇTİ — 2026-08-24  
Kalibre edilmemiş çarpanlara sahte hassasiyet verdiği için final modelden çıkarıldı.

## D-030 — 2E ayrı Research AI raporu olmadan kapatılamaz
**Durum:** UYGULANDI / TAMAMLANDI — 2026-08-24

## D-031 — Final Mastery Formula v0 = GRE-v0
**Durum:** Kabul edildi — 2026-08-24  
Yalnız eligible + prerequisite-valid + H0 + direct + verified + independent evidence groups; bounded recent evidence, required/critical gates ve verification hysteresis. Ayrıntı: `docs/MASTERY_FORMULA_V0.md`.

## D-032 — Final Retention/Forgetting = RVR-v0
**Durum:** Kabul edildi — 2026-08-24  
Time mastery'yi düşürmez; `review_due` forgetting değildir; ilk clean contradiction `verification_due` üretir. Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md`.

## D-033 — Daily capacity hard budget'tır
**Durum:** Kabul edildi — 2026-08-24  
No auto-overrun; safe split → smaller alternative → defer; deferred task debt değildir. Ayrıntı: `docs/ADAPTIVE_PLANNER_SPEC.md`.

## D-034 — Kalıcı olan eski task değil LearningNeed'dir
**Durum:** Kabul edildi — 2026-08-24  
`State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`; purpose/activity/track/evidence ayrı eksenlerdir. Ayrıntı: `docs/TASK_TAXONOMY_SPEC.md`.

## D-035 — Planner priority = PBR-v0
**Durum:** Kabul edildi — 2026-08-24  
Eligibility önce, P0–P4 semantic bands + deterministic rank vector; starvation/track balance; duration semantic priority'den sonra. Ayrıntı: `docs/PRIORITY_POLICY_SPEC.md`.

## D-036 — Prerequisite = PRG-v0
**Durum:** Kabul edildi — 2026-08-24  
Skill→Skill hard/soft edge; `ready | ready_due | uncertain | not_ready`; branch-local blocking ve contamination guard. Ayrıntı: `docs/PREREQUISITE_POLICY_SPEC.md`.

## D-037 — Hızlı öğrenme = VDW-v0
**Durum:** Kabul edildi — 2026-08-24  
Validated Objective-level diagnostic waiver; diagnostic mastery'nin kolay alternatifi değildir. Ayrıntı: `docs/DIAGNOSTIC_WAIVER_SPEC.md`.

## D-038 — Missed-day/re-entry = SRR-v0
**Durum:** Kabul edildi — 2026-08-24  
Absence failure/debt değildir; stale plan replay edilmez; current-state replan yapılır. Ayrıntı: `docs/MISSED_DAY_RECOVERY_SPEC.md`.

## D-039 — Planner explainability = PDT-v0
**Durum:** Kabul edildi — 2026-08-24  
Structured reason codes + decision trace; user explanation trace'teki factual nedenlerden türetilir. Ayrıntı: `docs/PLANNER_EXPLAINABILITY_SPEC.md`.

## D-040 — Daily micro assessment = DMA-v0
**Durum:** Kabul edildi — 2026-08-24  
Zorunlu günlük quiz/kota değildir; Objective-matched evidence, H0/assistance/provenance, prerequisite fairness, invalid/provisional safety ve evidence→state→replan pipeline kullanır. Ayrıntı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

## D-041 — Uzun vadeli hedef 4+ yıl professional-readiness curriculum
**Durum:** Kabul edildi — 2026-08-24  
4+ yıl countdown değildir; final hedef verified engineering capability + integrated/capstone evidence. V1 full curriculum'u beklemez. Ayrıntı: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## D-042 — Python ana rotanın resmi foundation dilidir
**Durum:** Kabul edildi — 2026-08-25  
Python C/C++'ın yerine geçmez; automation/testing/benchmark, data/ML, infra tooling; ileride typing/testing/async/multiprocessing/networking/profiling/packaging depth içerir.

## D-043 — Standalone specialization-track aşaması
**Durum:** GERİ ÇEKİLDİ / YANLIŞ YORUM — 2026-08-25  
Kullanıcı talebi yanlış yorumlandığı için canonical değildir.

## D-044 — Full rota granular capability map'e ayrılacak; AŞAMA 6 eklendi
**Durum:** Kabul edildi — 2026-08-25  
Her büyük alan `Domain → Module → Topic → Skill → Learning Objective` seviyesine ayrılır; weakness/mastery/remediation mümkün olduğunca Skill/Objective düzeyinde lokalize edilir. AŞAMA 5 graph backbone, AŞAMA 6 detailed taxonomy, AŞAMA 15 first content, AŞAMA 20 full professional content'tir. Ayrıntı: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## D-045 — Weekly assessment = WBA-v0 Weekly Blueprint Assessment
**Durum:** Kabul edildi — 2026-08-25
- Weekly assessment tek overall score/pass-fail değildir; daha geniş Objective/Skill evidence bundle üretir.
- Item seçilmeden önce state-temelli weekly blueprint oluşturulur.
- Role family'leri recent progress, weakness/verification, critical prerequisite, retention, integration/transfer ve gerektiğinde parallel English'tir; fixed quota değildir.
- Fixed soru sayısı/süre/yüzde yoktur; weekly evidence GRE/RVR'ı bypass etmez.
- PRG prerequisite fairness, variant/dependency diversity, H0 assistance standardı, invalid/provisional safety ve GRE/RVR hysteresis korunur.
- Split/pause/resume desteklenir; incomplete/missed weekly exam failure/debt değildir.
- Broad Domain pass/fail yazılmaz; D-044 granular localization korunur.
- 4C için `AssessmentBlueprint / AssessmentBlueprintSlot / AssessmentSessionResult` ortak abstraction'ı kilitlendi.

Ayrıntı: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

## D-046 — Monthly assessment = MCA-v0 Monthly Capability Assessment
**Durum:** Kabul edildi — 2026-08-25
- Monthly assessment tek ay sonu notu veya domain pass/fail değildir; longitudinal capability evidence bundle üretir.
- WBA-v0 common `AssessmentBlueprint / Slot / SessionResult` contract'ı yeniden kullanılır.
- Monthly role family'leri: longitudinal required capability, persistent weakness/verification, critical capability revalidation, delayed retention, cross-topic transfer, integrated application, gerektiğinde Technical English ve professional evidence checkpoint. Bunlar fixed quota değildir.
- Bütün geçmiş curriculum'u cumulative olarak tekrar test etmez; state-temelli bounded longitudinal sampling yapar.
- Recent/older balance fixed yüzdelerle değil decision value, prerequisite risk, retention ve evidence gap ile belirlenir.
- Critical Skill sırf critical olduğu için her ay otomatik test edilmez; gerçek revalidation ihtiyacı gerekir.
- Transfer ve integration daha geniş olabilir fakat yalnız öğretilmiş prerequisites, component-level attribution ve Objective'e uygun evidence kullanır.
- Professional checkpoint final professional-readiness/capstone gate değildir; yalnız uygun evidence üretir.
- Fixed soru sayısı, fixed süre, fixed pass score yoktur; daily hard capacity korunur ve monthly session safe block'lara bölünebilir.
- H0/H1–H4, provenance, invalid/ambiguous/provisional item, root-prerequisite contamination ve GRE/RVR hysteresis kuralları aynen korunur.
- Incomplete/missed monthly assessment failure veya exam debt değildir.
- Raw `Python failed` gibi broad state yazılmaz; weakness D-044 gereği Skill/Objective seviyesine lokalize edilir.
- V1 SC-016 gereği güvenilir persistent/critical gap sonucu planner/curriculum priority'yi gerçekten değiştirebilir.
- 4D Question Bank için assessment scope, blueprint role, target/prerequisite, evidence type, family/context/diversity, rubric/evaluator, trust/version, exposure ve duration metadata handoff'u tanımlandı.

Ayrıntı: `docs/MONTHLY_ASSESSMENT_SPEC.md`.