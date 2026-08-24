# Project Progress Log

Bu dosya projenin oturumlar arası kalıcı ilerleme günlüğüdür. Ayrıntılı plan `MASTER_PLAN.md` / `EXECUTION_INDEX.md`; bu dosya ise ne zaman ne yapıldığını ve neden yapıldığını kronolojik olarak kaydeder.

---

### 2026-08-24 — Proje hafızası ve yürütme sistemi kuruldu
- Proje amacı, kariyer rotası, mastery ilkesi, knowledge graph, assessment, retention ve parallel English yönü kalıcı hale getirildi.
- GitHub kalıcı proje hafızası olarak belirlendi.

---

### 2026-08-24 — Master plan 19 aşamalı yürütme planına dönüştürüldü
- Proje 19 ana aşamaya ve acceptance kapılarına ayrıldı.
- Release APK'nin temel ürünün hazır olduğu nokta olduğu netleştirildi.

---

### 2026-08-24 — Sohbet aktarımı ve kalıcı handoff sistemi güçlendirildi
- `START_HERE.md`, `PROJECT_MASTER_CONTEXT.md`, `HANDOFF_STATE.md` oluşturuldu.
- Sohbet geçmişinin tek bilgi kaynağı olmaması kararlaştırıldı.

---

### 2026-08-24 — Sabit 1A/1B yürütme numaralandırması eklendi
- Aşamalar 1–19 olarak standardize edildi.
- `1A`, `1B`, `2A`, `3C`, `11F` gibi sabit adım kodları oluşturuldu.
- `EXECUTION_INDEX.md` ve `STEP_STATUS.md` devreye alındı.

---

### 2026-08-24 — Aşama 1 tamamlandı
- `1A` ürün amacı → `docs/PRODUCT_REQUIREMENTS.md`.
- `1B` V1 kapsamı → `docs/V1_SCOPE.md`.
- `1C` P0/P1/P2 success criteria → `docs/V1_SUCCESS_CRITERIA.md`.
- `1D` non-goals → `docs/NON_GOALS.md`.
- AŞAMA 1 kapandı.

---

### 2026-08-24 — 2A Bilgi birimleri tamamlandı
- `Domain → Module → Topic → Skill → Learning Objective` modeli kesinleştirildi.
- Skill canonical mastery/prerequisite seviyesi; Topic/Module/Domain derived.
- Topic ↔ Skill many-to-many ve Skill→Skill prerequisite yönü kilitlendi.
- Technical English gereksiz global hard-lock olmayacak.
- Çıktı: `docs/LEARNING_ENGINE_SPEC.md`.
- Karar: D-021.

---

### 2026-08-24 — Öğrenme davranışı kuralları kalıcılaştırıldı
- Uygulama öğretir → uygulatır → ölçer → remediation/retest yapar.
- Coverage/mastery ayrımı, prerequisite-aware assessment, no exact immediate repeat, adaptive difficulty, no uncontrolled remediation time, delayed retention ve controlled AI soru genişletmesi kilitlendi.
- Çıktı: `docs/LEARNING_BEHAVIOR_RULES.md`.
- Karar: D-022.

---

### 2026-08-24 — 2B Topic state machine tamamlandı
- Canonical state'ler: `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.
- Topic state Skill mastery/coverage/retention/remediation'dan derived orchestration state olarak tanımlandı.
- Started/mastered Topic prerequisite regression yüzünden geriye dönük `locked` yapılmayacak.
- Çıktı: `docs/TOPIC_STATE_MACHINE.md`.
- Karar: D-023.

---

### 2026-08-24 — Her adım için zorunlu GitHub beyin tazeleme protokolü kilitlendi
- `PRE-STEP GitHub refresh → adımı yürüt → gerekirse Research/Coding/QA → POST-STEP GitHub sync → sonraki adımı aktif yap` zorunlu hale geldi.
- Aynı sohbet içinde yeni adımda bile refresh tekrarlanacak.
- Çıktı: `docs/PROJECT_MEMORY_PROTOCOL.md`.
- Karar: D-024.

---

### 2026-08-24 — English A0 prerequisite davranışı netleştirildi
- Öğretilmemiş `a/an`, `the`, `to`, cümle yapısı vb. bilinmeden free production beklenmeyecek.
- English progression `recognition → controlled production → free production → technical use → transfer/retention`.
- Teknik task'te bilinmeyen English grammar gizli prerequisite olamaz.
- Çıktı: `docs/ENGLISH_FOUNDATION_RULES.md`.

---

### 2026-08-24 — 2C Mastery sinyalleri tamamlandı
**PRE-STEP**
- Handoff/index/status/decisions ve ilgili öğrenme specs yeniden okundu.
- Retrieval/delayed retention/transfer literatürü kısa dış research ile doğrulandı.

**Tamamlananlar**
- Evidence rolleri `direct/primary`, `corroborating`, `contextual`.
- Recognition, recall, code reading, coding, debugging, explanation, transfer, retention, integrated project ayrı evidence türleri.
- Coding mastery gerçek kullanıcı artifact'ı ister; MCQ coding evidence değildir.
- Transfer yalnız bilinen prerequisites ile geçerli.
- Time/completion/streak/self-confidence mastery değildir.
- Same-family repetition, invalid/contaminated evidence ve misconception tagging kuralları tanımlandı.
- Objective-specific evidence profile yönü kilitlendi.

**Çıktı**
- `docs/MASTERY_SIGNALS_SPEC.md`
- D-025.

---

### 2026-08-24 — 2D AI / ipucu etkisi tamamlandı
**PRE-STEP**
- Handoff/index/status/decisions, mastery signals, behavior, memory protocol ve AI workflow tekrar okundu.
- Exact sayısal weight seçilmediği için ayrı research turu gerekmemişti.

**Tamamlananlar**
- H0 none, H1 orientation, H2 targeted hint, H3 partial solution/scaffold, H4 full solution.
- Assistance timing ve artifact authorship ayrı tutuldu.
- `independent_evidence`, `assisted_evidence`, `practice_only`, `requires_independent_recheck` sınıfları.
- AI-generated/copied code direct production mastery değildir.
- H3/H4 sonrası fresh/unseen recheck zorunluluğu.
- Submit sonrası feedback önceki attempt'i kirletmez.
- Compiler/test/docs/autocomplete objective-specific allowed-tools policy ile yorumlanır.
- External AI için surveillance yerine provenance + recheck.

**Çıktı**
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- D-026.
- `MASTER_PLAN` canonical 1–19 indeksle senkronlandı; D-027.

---

### 2026-08-24 — Mobil performans/akıcılık kalıcı gereksinim olarak eklendi
- UI thread ağır mastery/planner/DB/network/AI/code execution ile bloke edilmeyecek.
- Async işlemler, incremental/cache/index yaklaşımı, lazy rendering, gereksiz polling'den kaçınma ve gerçek cihaz performance QA yönü bağlayıcı oldu.
- Exact performance bütçeleri 8F/17E'de ölçülecek.
- Karar: D-028.

---

### 2026-08-24 — 2E Mastery Formula v0 tamamlandı

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `LEARNING_ENGINE_SPEC`, `MASTERY_SIGNALS_SPEC`, `AI_ASSISTANCE_EVIDENCE_SPEC`, `TOPIC_STATE_MACHINE`, `LEARNING_BEHAVIOR_RULES` ve `V1_SUCCESS_CRITERIA` yeniden okundu.
- Aktif adımın 2E olduğu ve D-028 dahil mevcut bağlayıcı kararlarla çelişki olmadığı doğrulandı.

**Research AI / dış araştırma**
- Mastery Learning, Bayesian Knowledge Tracing ve Item Response Theory yönleri karşılaştırıldı.
- BKT'de `0.95` gibi threshold'ların sık kullanıldığı fakat evrensel bilimsel sabit olmadığı; 2025 EDM çalışmasında belirli bağlamda `0.98` eşiğinin daha iyi sonraki performans ilişkisi gösterdiği görüldü.
- IRT item difficulty/discrimination'ı veriyle kalibre ettiği için elde item data yokken `hard = 1.3x` gibi sahte hassasiyet kullanılmaması kararlaştırıldı.
- V1 için açıklanabilir, local/deterministic ve az veriyle çalışan gate + evidence accumulator seçildi.

**Tamamlanan model**
- Objective `MasteryEvidenceScore` Beta-style accumulator:
  - `alpha = 1 + Σ(wq)`
  - `beta = 1 + Σ(w(1-q))`
  - `score = alpha/(alpha+beta)`
- `q`: rubric quality `[0,1]`.
- `w = role × assistance × provenance`.
- Direct `1.00`, corroborating `0.50`, contextual `0`.
- H0/H1/H2/H3/H4 v0: `1.00 / 0.85 / 0.65 / 0.35-or-0 / 0`.
- AI evaluator high-confidence rubric v0 `0.80`; low confidence/invalid `0` + recheck.
- Operational threshold `0.80`; probability veya “%80 öğrendi” anlamı yok.
- Difficulty numeric multiplier değil; critical gate/item eligibility input'u.
- Same-item/same-family dedup/diversity guard.
- Standard ve critical Objective hard gate'leri.
- Critical production için en az bir H0 user-authored direct artifact.
- Skill mastered için tüm required/critical Objective gate'leri + skill score + no unresolved recheck.
- Tek clean negative mastered Skill'i anında düşürmez; `verification_due` + fresh confirmation.
- Decision trace ve formula versioning tanımlandı.
- D-028'e uyum için incremental aggregate/sufficient-state tasarımı eklendi.
- Numeric constants pilot 17C'de false-positive/false-negative verisiyle kalibre edilecek.

**Üretilen / güncellenen dosyalar**
- `docs/MASTERY_FORMULA_V0.md`
- `docs/DECISIONS.md` — D-029
- `docs/EXECUTION_INDEX.md`
- `docs/STEP_STATUS.md`
- `docs/HANDOFF_STATE.md`
- `docs/MASTER_PLAN.md`
- `docs/PROGRESS_LOG.md`
- `docs/START_HERE.md` kontrol/güncelleme kapsamına alındı.

**Sonraki kesin adım**
- **`2F — Unutma modeli`**.
- 2F başlamadan yeni PRE-STEP GitHub refresh ve retention/spaced-repetition Research AI turu zorunlu.