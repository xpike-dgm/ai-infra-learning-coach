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

ve direct/corroborating, H0–H4, AI-evaluator numeric multiplier'ları önerilmişti. Bu model ayrı Research AI doğrulamasından sonra finalden çıkarıldı. Tarihsel candidate ayrıntısı Git history ve `docs/2E_RESEARCH_VALIDATION.md` içinde açıklanır.

## D-030 — 2E ayrı Research AI raporu alınmadan kapatılamaz
**Durum:** UYGULANDI / TAMAMLANDI — 2026-08-24  
Kullanıcı bağımsız Research AI raporunu sağladı; ana yönetici raporu otomatik kabul etmeyip mevcut 2A–2D kararları ve seçili akademik kaynaklarla değerlendirdi.

## D-031 — Final Mastery Formula v0 = Gated Recent Evidence (GRE-v0)
**Durum:** Kabul edildi — 2026-08-24

2E final modeli:

- Mastery score'a yalnız **eligible, prerequisite-valid, H0, direct/primary, verified, bağımsız evidence group** girer.
- H1–H4 assisted evidence öğrenme/remediation/recheck için saklanır fakat positive independent mastery score'a girmez.
- Corroborating evidence direct evidence eksikliğini numeric birikimle telafi edemez; diagnostic/support katmanında kullanılır.
- Same-item/near-variant correlation `dependency_group_id/testlet_id` ile gruplanır; `variant_family_id` diversity için kullanılır.
- Objective score son en fazla `5` eligible independent H0 direct evidence group'un `q_g` ortalamasıdır:
  `recent_direct_score = mean(q_g)`.
- `recent_window_max_groups_v0 = 5` ve `objective_mastery_threshold_v0 = 0.80` **engineering heuristic** olup 17C pilotunda kalibre edilir; probability veya `% learned` değildir.
- Standard required Objective default: score `>=0.80`, en az 2 bağımsız H0 direct group, default 2 family/context, required direct type, no unresolved recheck.
- Critical Objective default: en az 3 bağımsız H0 direct group, en az 2 family/context, non-basic evidence ve Objective-specific production/debugging/transfer gate'leri.
- Coding/production critical Objective en az bir gerçek H0 user-authored coding artifact gerektirir.
- Debugging critical Objective en az bir bağımsız H0 diagnosis/fix evidence gerektirir.
- Skill mastered: **tüm required Objective PASS + tüm critical Objective PASS + unresolved critical recheck yok**. Yüksek average kritik açığı telafi edemez.
- İlk temiz post-mastery H0 direct failure mastery'yi anında silmez; `verification_due` açar ve fresh/unseen recheck ister.
- Difficulty numeric multiplier değildir; item eligibility / non-basic / transfer gate olarak kullanılır.
- Sabit `AI evaluator = 0.80` kaldırıldı. Evaluator sonucu `verified | provisional | invalid` olarak tutulur; provisional LLM grading critical mastery'yi tek başına geçiremez.
- Explainability trace ve bounded/incremental sufficient-state zorunludur; D-028 korunur.
- Time decay / half-life / spaced repetition 2F kapsamıdır.

Ayrıntı: `docs/MASTERY_FORMULA_V0.md` ve `docs/2E_RESEARCH_VALIDATION.md`.
