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
- `EXECUTION_INDEX.md` ve `STEP_STATUS.md` devreye alındı.

---

### 2026-08-24 — Aşama 1 tamamlandı
- `1A` ürün amacı → `docs/PRODUCT_REQUIREMENTS.md`.
- `1B` V1 kapsamı → `docs/V1_SCOPE.md`.
- `1C` P0/P1/P2 success criteria → `docs/V1_SUCCESS_CRITERIA.md`.
- `1D` non-goals → `docs/NON_GOALS.md`.

---

### 2026-08-24 — 2A Bilgi birimleri tamamlandı
- `Domain → Module → Topic → Skill → Learning Objective` modeli kesinleştirildi.
- Skill canonical mastery/prerequisite seviyesi; Topic/Module/Domain derived.
- Topic ↔ Skill many-to-many ve Skill→Skill prerequisite yönü kilitlendi.
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
- State'ler: `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.
- Topic state Skill mastery/coverage/retention/remediation'dan derived orchestration state.
- Çıktı: `docs/TOPIC_STATE_MACHINE.md`.
- Karar: D-023.

---

### 2026-08-24 — Zorunlu GitHub beyin tazeleme protokolü kilitlendi
- `PRE-STEP GitHub refresh → çalışma → gerekirse Research/Coding/QA → POST-STEP GitHub sync` zorunlu.
- Aynı sohbet içinde yeni adımda bile refresh tekrarlanacak.
- Çıktı: `docs/PROJECT_MEMORY_PROTOCOL.md`.
- Karar: D-024.

---

### 2026-08-24 — English A0 prerequisite davranışı netleştirildi
- Öğretilmemiş grammar/function-word yapılarından free production beklenmeyecek.
- Teknik assessment bilinmeyen English grammar'ı gizli prerequisite yapmayacak.
- Çıktı: `docs/ENGLISH_FOUNDATION_RULES.md`.

---

### 2026-08-24 — 2C Mastery sinyalleri tamamlandı
- Direct/corroborating/contextual evidence ayrımı.
- Recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project türleri.
- Coding mastery gerçek user artifact ister.
- Same-family repetition ve invalid/contaminated evidence guardrail'leri.
- Çıktı: `docs/MASTERY_SIGNALS_SPEC.md`.
- Karar: D-025.

---

### 2026-08-24 — 2D AI / ipucu etkisi tamamlandı
- H0–H4, timing, artifact provenance, independent/assisted/practice-only/recheck sınıfları.
- AI-generated code production mastery değildir.
- H3/H4 sonrası fresh/unseen recheck.
- Çıktı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`.
- Karar: D-026.

---

### 2026-08-24 — MASTER_PLAN sync zorunluluğu güçlendirildi
- `MASTER_PLAN` canonical indeksle senkron tutulacak.
- Karar: D-027.

---

### 2026-08-24 — Mobil performans/akıcılık first-class requirement oldu
- UI thread ağır mastery/planner/DB/network/AI/code execution ile bloke edilmeyecek.
- Incremental/cache/index/lazy rendering ve gerçek cihaz performance QA yönü bağlayıcı.
- Karar: D-028.

---

### 2026-08-24 — 2E ilk candidate mastery formülü üretildi
- Ana yönetici kendi dış/web araştırmasıyla Beta-style weighted evidence accumulator + hard gates candidate'ı hazırladı.
- Candidate içinde direct/corroborating, H0–H4 ve AI evaluator numeric multiplier'ları vardı.
- Bu aşama ilk kez yanlışlıkla tamamlandı işaretlendi.

---

### 2026-08-24 — 2E workflow hatası düzeltildi ve yeniden açıldı
- Proje planında 2E için **ayrı Research AI** kullanılması gerektiği halde yalnız ana yöneticinin web araştırması yapılmış olduğu fark edildi.
- 2E yeniden aktif yapıldı, 2F beklemeye alındı.
- `docs/MASTERY_FORMULA_V0.md` candidate olarak işaretlendi.
- D-029 provisional, D-030 ile ayrı Research AI raporu kapanış şartı oldu.

---

### 2026-08-24 — 2E bağımsız Research AI validation tamamlandı; GRE-v0 finalleştirildi

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN` ve ilgili mastery/assistance specs yeniden okundu.
- Canonical aktif adımın 2E Research AI validation olduğu doğrulandı.
- `MASTER_PLAN.md` içinde eski candidate kapanışından kalan drift tespit edildi; POST-STEP'te düzeltildi.

**Research AI girdisi**
- Kullanıcı bağımsız Research AI raporunu sağladı.
- Rapor BKT, AFM/PFA/R-PFA, IRT, mastery criterion, assistance/scaffolding, testlet/LID, programming education ve LLM grading başlıklarını karşılaştırdı.
- Report doğrudan ürün kararı kabul edilmedi.

**Yönetici doğrulaması / düzeltmeleri**
- Assistance dilemma literatürünün belirli H1/H2 numeric penalty'lerini doğrulamadığı teyit edildi; sabit `0.85/0.65/0.35` kaldırıldı.
- Instructional Factors Analysis, farklı instructional intervention türlerini ayrı kategoriler olarak ele almanın yararlı olabildiğini destekledi.
- PFA ve R-PFA'nın fit edilmiş predictive modeller olduğu; cold-start heuristic'imizi doğrudan `Rolling-PFA` diye adlandırmanın doğru olmayacağı ayrıldı.
- Research raporundaki “fractional Beta count matematiksel olarak geçersizdir” iddiası fazla güçlü bulundu; asıl problem candidate'ın calibrated posterior olmaması, farklı boyutları tek multiplier'a indirmesi ve sınırsız-history saturation riskidir.
- Programming education literatürü gerçek writing/production görevlerinin ayrıca ölçülmesini destekledi.
- LLM grading çalışmalarındaki değişken agreement nedeniyle sabit `AI evaluator = 0.80` kaldırıldı.

**Final 2E modeli — `GRE-v0 — Gated Recent Evidence`**
- Mastery score'a yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence group girer.
- H1–H4 formative/remediation/recheck sinyalidir; positive independent mastery score'a girmez.
- Corroborating evidence direct gate'i ikame etmez.
- Same-family/dependent item'lar testlet/dependency group olarak gruplanır.
- Objective recent score = son en fazla `5` eligible independent H0 direct group'un `q_g` ortalaması.
- `0.80` threshold ve window `5` engineering heuristic; UI'da probability/% learned değildir.
- Standard default: en az 2 independent group; critical default: en az 3 group + 2 family/context + non-basic/objective-specific gate.
- Critical coding → H0 user-authored artifact; critical debugging → H0 diagnosis/fix.
- Skill mastery non-compensatory: tüm required/critical Objective gates PASS.
- Tek clean post-mastery negative → `verification_due`; instant reset yok.
- Difficulty multiplier değil gate.
- AI evaluator numeric weight kaldırıldı; `verified | provisional | invalid`.
- Bounded/incremental sufficient-state D-028 performans kuralına uygun.

**Çıktılar**
- `docs/MASTERY_FORMULA_V0.md` final GRE-v0
- `docs/2E_RESEARCH_VALIDATION.md`
- `docs/DECISIONS.md` — D-029 superseded, D-030 fulfilled, D-031 final
- `docs/EXECUTION_INDEX.md`
- `docs/STEP_STATUS.md`
- `docs/HANDOFF_STATE.md`
- `docs/MASTER_PLAN.md`
- `docs/PROGRESS_LOG.md`
- `docs/START_HERE.md` güncellenecek/kontrol edilecek

**Sonraki kesin adım**
- **`2F — Unutma modeli`**.
- 2F başlamadan yeni PRE-STEP GitHub refresh + ayrı Research AI retention/spaced-repetition turu zorunlu.
