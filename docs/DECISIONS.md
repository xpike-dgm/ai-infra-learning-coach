# Product Decisions Log

Bu dosya kalıcı ürün kararlarını kaydeder. Ayrıntılı teknik davranış ilgili canonical spec dosyalarındadır.

## D-001 — 3 yıllık gün sayacı gösterilmeyecek
**Durum:** Kabul edildi  
`Gün X / 1095` ana ilerleme metriği değildir.

## D-002 — İlerleme mastery tabanlı olacak
**Durum:** Kabul edildi  
Ders/task completion tek başına öğrenme değildir.

## D-003 — Knowledge graph takvimden öncelikli
**Durum:** Kabul edildi  
Takvim kapasiteyi, prerequisite/mastery konu uygunluğunu belirler.

## D-004 — Eksik konu tüm programı dondurmaz
**Durum:** Kabul edildi  
Yalnız bağımlı dallar bekler.

## D-005 — Haftalık/aylık sınavlar programı değiştirecek
**Durum:** Kabul edildi

## D-006 — İngilizce paralel ilerleyecek
**Durum:** Kabul edildi

## D-007 — Ana kariyer rotası systems → GPU → AI infrastructure
**Durum:** Kabul edildi  
C → Linux → C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure.

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
AI yardımı sonrası gerektiğinde independent comprehension/transfer/production doğrulaması gerekir.

## D-012 — 3 yıllık curriculum V1 ön koşulu değil
**Durum:** Kabul edildi  
İlk 8–12 haftalık production-quality paket + öğrenme motoru önce gelir.

## D-013 — Süreler adaptif, konu bağımlılıkları daha kalıcı
**Durum:** Kabul edildi

## D-014 — İlk iş doğrudan CUDA olmak zorunda değil
**Durum:** Kabul edildi  
C++ Systems / Systems Software / Linux Infrastructure / Distributed Systems / Performance / uygun SRE-Cloud rolleri köprü olabilir.

## D-015 — Geliştirme master plan üzerinden aşamalı yürütülecek
**Durum:** Kabul edildi — 2026-08-24

## D-016 — Araştırma, kodlama ve test ayrı AI rolleridir
**Durum:** Kabul edildi — 2026-08-24  
Research AI dış araştırma; Coding AI implementasyon; Test/QA AI bağımsız doğrulama; ana yönetici karar/spec/GitHub hafızasıdır. Ayrıntı: `docs/AI_AGENT_WORKFLOW.md`.

## D-017 — Sabit `1A / 1B / ...` adım kodları kullanılacak
**Durum:** Kabul edildi — 2026-08-24  
Canonical indeks: `docs/EXECUTION_INDEX.md`.

## D-018 — V1 adaptif öğrenme döngüsünü gerçek Android release olarak çalıştıracak
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SCOPE.md`.

## D-019 — V1 release acceptance kriterleri ve bağımsız QA'ya bağlı
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SUCCESS_CRITERIA.md`.

## D-020 — Ürün/V1 non-goals kilitlendi
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/NON_GOALS.md`.

## D-021 — Öğrenme birimleri organizasyon ve mastery katmanı olarak ayrılacak
**Durum:** Kabul edildi — 2026-08-24  
`Domain → Module → Topic → Skill → Learning Objective`; canonical mastery/prerequisite ana seviyesi Skill. Ayrıntı: `docs/LEARNING_ENGINE_SPEC.md`.

## D-022 — Öğretme, yanlış cevap, remediation, retention ve replan davranışları bağlayıcıdır
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/LEARNING_BEHAVIOR_RULES.md`.

## D-023 — Topic state Skill verilerinden derived, explainable work state olacak
**Durum:** Kabul edildi — 2026-08-24  
`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`. Ayrıntı: `docs/TOPIC_STATE_MACHINE.md`.

## D-024 — Her numaralı adım öncesi/sonrası GitHub beyin tazelemesi zorunlu
**Durum:** Kabul edildi — 2026-08-24  
Akış: `PRE-STEP GitHub refresh → çalışma → gerekirse Research/Coding/QA → POST-STEP GitHub sync`. Ayrıntı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

## D-025 — Mastery çok kaynaklı ve Objective'e uygun evidence ile kanıtlanacak
**Durum:** Kabul edildi — 2026-08-24  
Recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project ayrıdır; time/completion/streak/self-confidence mastery değildir. Coding mastery gerçek user artifact ister. Ayrıntı: `docs/MASTERY_SIGNALS_SPEC.md`.

## D-026 — AI/ipucu yardımı öğrenmeyi destekler fakat independent mastery ile eşit değildir
**Durum:** Kabul edildi — 2026-08-24  
H0–H4, timing, artifact provenance ve fresh recheck davranışı. Ayrıntı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`.

## D-027 — MASTER_PLAN canonical yürütme durumuyla senkron tutulacak
**Durum:** Kabul edildi — 2026-08-24

## D-028 — Mobil performans/akıcılık first-class requirement
**Durum:** Kabul edildi — 2026-08-24  
UI thread ağır mastery/planner/DB/network/AI/code-execution ile bloke edilmez; incremental/local-first ve gerçek cihaz performance QA uygulanır.

## D-029 — İlk Beta-style Mastery Formula candidate'ı
**Durum:** **YERİNE D-031 GEÇTİ — 2026-08-24**

İlk candidate:
```text
alpha = 1 + Σ(wq)
beta  = 1 + Σ(w(1-q))
score = alpha/(alpha+beta)
```
ve direct/corroborating, H0–H4, AI-evaluator numeric multiplier'ları önerilmişti. Bu model ayrı Research AI doğrulamasından sonra finalden çıkarıldı.

## D-030 — 2E ayrı Research AI raporu alınmadan kapatılamaz
**Durum:** UYGULANDI / TAMAMLANDI — 2026-08-24

## D-031 — Final Mastery Formula v0 = Gated Recent Evidence (GRE-v0)
**Durum:** Kabul edildi — 2026-08-24
- Yalnız eligible, prerequisite-valid, H0, direct/primary, verified, independent evidence groups score'a girer.
- H1–H4 independent positive mastery değildir.
- Same/near item dependency/testlet grouping uygulanır.
- Required/critical Objective hard gates vardır.
- İlk clean post-mastery failure instant reset değil `verification_due` üretir.
- Bounded/incremental uygulanır.

Ayrıntı: `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md`.

## D-032 — Final Retention/Forgetting modeli = RVR-v0
**Durum:** Kabul edildi — 2026-08-24
- Mastery ve retention ayrı eksenlerdir.
- Zaman GRE/mastery score'u otomatik düşürmez; `review_due` üretir.
- `review_due` forgetting/hard lock değildir.
- First clean delayed failure → `verification_due`; recheck sonrası GRE doğal yeniden hesaplanır.
- Natural reuse strict attribution ile planned retention evidence olabilir.
- Critical unresolved verification dependent new work'u bekletebilir.
- Missed-day backlog dump yoktur.

Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md`.

## D-033 — Günlük kapasite planner için hard zaman bütçesidir; ihtiyaç oluştu diye gün otomatik uzamaz
**Durum:** Kabul edildi — 2026-08-24
- Explicit günlük süre hard envelope'dur.
- Fixed kategori yüzdesi yoktur.
- Remediation/retention day length'i otomatik büyütmez.
- Safe split → smaller alternative → defer.
- Deferred task ertesi gün borç değildir.
- Capacity deterministic/versioned'dır.

Ayrıntı: `docs/ADAPTIVE_PLANNER_SPEC.md`.

## D-034 — Planner'da kalıcı olan eski task değil açık LearningNeed'dir; task purpose/activity/track/evidence ayrı eksenlerdir
**Durum:** Kabul edildi — 2026-08-24
- Canonical akış `state → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`.
- Deferred TaskCandidate debt değildir; unresolved LearningNeed fresh candidate üretebilir.
- Purpose/activity/track/evidence ayrı eksenlerdir.
- Multi-Skill component evidence ayrı attribution ister.
- Provenance/validation + prerequisite + duration metadata TaskCandidate contract'ındadır.

Ayrıntı: `docs/TASK_TAXONOMY_SPEC.md`.

## D-035 — Planner priority modeli PBR-v0: semantic priority bands + deterministic rank vector
**Durum:** Kabul edildi — 2026-08-24
- Eligibility/trust priority'den önce gelir.
- P0 integrity blocker, P1 repair/verify, P2 maintain/continue, P3 planned progress, P4 reinforce/optimize.
- `review_due` negative evidence/P0 değildir.
- Same-band sorting lexicographic rank vector ile yapılır.
- Starvation guard ve track balance vardır.
- Duration semantic priority'den sonra gelir.
- Priority prerequisite'i bypass edemez.

Ayrıntı: `docs/PRIORITY_POLICY_SPEC.md`.

## D-036 — Final prerequisite modeli = PRG-v0 Prerequisite Readiness Gate
**Durum:** Kabul edildi — 2026-08-24

- Runtime prerequisite canonical olarak `Skill → Skill` düzeyindedir.
- Edge semantics `hard | soft` olarak ayrılır; hard gerçek öğretim/çözüm/evidence zorunluluğudur, soft destekleyicidir ve tek başına kilit üretmez.
- Prerequisite readiness: `ready | ready_due | uncertain | not_ready`.
- `review_due` = `ready_due`; hard lock üretmez.
- Hard `not_ready` dependent candidate'ı bloke eder.
- `verification_due` source Skill'i `uncertain` yapar; critical prerequisite veya strict task'ta dependent yeni work verification çözülene kadar bekler.
- Normal non-strict hard dependency'de `uncertain` conditional eligibility olabilir; tüm curriculum durmaz.
- Task-level `required_skill_ids` exact candidate eligibility için hard requirement'tır.
- Priority prerequisite gate'i bypass edemez. Canonical sıra: state/need → candidate → validation/trust → PRG-v0 eligibility → PBR-v0 priority → capacity fit.
- Yalnız affected dependent branch bekler; independent Linux/English/diğer branches devam eder.
- Started/mastered Topic prerequisite regression nedeniyle geriye `locked` yapılmaz.
- Öğretilmemiş/eksik hard prerequisite içeren task failure'ı target Skill için `invalid/unusable` evidence'dır; negative mastery yazılmaz.
- Missing prerequisite repair/review/verification LearningNeed olarak planner'a geri beslenir.
- Technical English gerçek dependency olmadığı sürece global technical hard blocker değildir.
- Deterministic/bounded readiness output `PrerequisiteDecision` ile açıklanabilir ve ileride 11B Prerequisite Engine'e taşınabilir.

Ayrıntı: `docs/PREREQUISITE_POLICY_SPEC.md`.

## D-037 — Final hızlı öğrenme modeli = VDW-v0 Validated Diagnostic Waiver
**Durum:** Kabul edildi — 2026-08-24

- Diagnostic, GRE-v0'dan daha kolay ikinci bir mastery standardı değildir; aynı evidence/gate kurallarını daha verimli toplama yoludur.
- Kullanıcının `biliyorum` beyanı yalnız diagnostic trigger/scope'tur, evidence değildir.
- Tek kolay quiz / recognition-only sonuç whole-Topic skip veremez.
- Skip canonical olarak Objective bazlı `DiagnosticCoverageWaiver` üretir; coverage waiver current mastery/retention değildir.
- Partial diagnostic yalnız kanıtlanan Objective'lere waiver verir; unresolved kısımlar normal öğrenmeye devam eder.
- `available → mastered` yalnız bütün required coverage waiver/coverage koşulları + GRE-v0 required/critical Skill gates birlikte sağlanınca mümkündür.
- Critical Skill diagnostic'i GRE-v0 critical gates'i aynen korur; coding için H0 user-authored artifact, debugging için H0 diagnosis/fix ve gereken transfer/diversity şartları düşürülemez.
- Waiver üretecek diagnostic evidence H0, prerequisite-valid, verified ve provenance-clean olmalıdır; H1–H4 veya solution exposure skip kanıtı değildir.
- Integrated diagnostic birden çok Objective'i hızlandırabilir fakat component evidence için structural essentiality + separate observability/attribution şarttır; tek project pass whole-topic waiver değildir.
- Diagnostic fail, henüz mastered olmayan prior-knowledge yolunda otomatik `remediation_required` cezası değildir; waiver verilmez ve normal learning başlar.
- Prerequisite contamination target negative evidence üretmez; PRG-v0 diagnostic'te de önce çalışır.
- Diagnostic current daily capacity içinde planlanır ve sonuç GRE → waiver → PRG → Topic state → LearningNeed/PBR → replan sırasıyla sisteme geri beslenir.
- Waiver curriculum/objective versiyonuna bağlıdır; yeni required Objective eski waiver ile otomatik geçilmiş sayılmaz.
- Policy bounded/deterministic ve D-028 ile uyumludur.

Ayrıntı: `docs/DIAGNOSTIC_WAIVER_SPEC.md`.

## D-038 — Final missed-day / re-entry modeli = SRR-v0 State-based Re-entry & Recovery
**Durum:** Kabul edildi — 2026-08-24

- Absence failure, mastery decay, remediation trigger veya task debt değildir.
- Geri dönüşte geçmiş başlanmamış `PlannedTask` ve ephemeral `TaskCandidate` current plan'a replay edilmez; current state'ten fresh `LearningNeed → TaskCandidate` üretimi yapılır.
- Zaman yalnız RVR-v0 retention due/temporal urgency sinyallerini değiştirebilir; sırf uzun ara nedeniyle `review_due → verification_due/at_risk` veya mastered → unmastered olmaz.
- Unresolved verification/remediation ihtiyaçları absence ile silinmez.
- Safe/version-valid paused checkpoint continuation adayı olabilir fakat eligibility/priority/capacity yeniden hesaplanır; otomatik seçilmez.
- Incomplete high-stakes H0/diagnostic/retention attempt negative evidence değildir; gerekiyorsa fresh/unseen candidate üretilir.
- Due-state inventory DailyPlan değildir; çok sayıda due Skill bugünün kapasitesine topluca yığılmaz.
- 1/7/30/60+ gün için ayrı pedagojik threshold yoktur; aynı state-driven recovery policy çalışır. Absence duration informational/urgency bağlamıdır.
- Recovery ayrı gizli priority score üretmez; PBR-v0 P0–P4, PRG-v0 eligibility ve 3A hard capacity korunur.
- Absence günleri starvation counter artırmaz; starvation eligible need'in aktif planlama günlerinde ertelenmesi, retention overdue ise ayrı zaman sinyalidir.
- Integrated recovery task yalnız separately observable/attributable Skill'ler için evidence üretir; sibling/cluster auto-refresh yoktur.
- Uzun ara sonrası new learning globally dondurulmaz; gerçek blocker yoksa güvenli branch'lerde devam edebilir.
- Self-report (`bu arada C kullandım`) automatic retention refresh değildir; diagnostic/verified artifact normal evidence pipeline'ına girebilir.
- Candidate generation/query bounded/incremental olmalı ve D-028 performans kuralını korumalıdır.

Ayrıntı: `docs/MISSED_DAY_RECOVERY_SPEC.md`.
