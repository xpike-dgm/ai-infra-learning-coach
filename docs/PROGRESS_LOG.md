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
- Ayrı Research AI turu yapılmadan 2E'nin yanlışlıkla kapatıldığı fark edildi.
- 2E yeniden aktif yapıldı, 2F beklemeye alındı.
- D-029 provisional, D-030 ayrı Research AI kapanış şartı oldu.

---

### 2026-08-24 — 2E bağımsız Research AI validation tamamlandı; GRE-v0 finalleştirildi
- Research AI raporu otomatik kabul edilmedi; BKT/PFA/IRT/assistance/testlet/programming education/LLM grading ayrı değerlendirildi.
- Beta-style accumulator ve sabit assistance/AI-evaluator multiplier'ları kaldırıldı.
- Final `GRE-v0 — Gated Recent Evidence`.
- Çıktılar: `MASTERY_FORMULA_V0.md`, `2E_RESEARCH_VALIDATION.md`, D-031.

---

### 2026-08-24 — 2F Retention / Forgetting araştırması başlatıldı

**PRE-STEP**
- Handoff/index/status/decisions/master plan ve GRE-v0 / Topic state / learning behavior yeniden okundu.
- Aktif adımın 2F olduğu doğrulandı.
- `docs/2F_RESEARCH_BRIEF.md` oluşturuldu.
- Separate Deep Research raporu gelmeden adımın kapanmaması kararlaştırıldı.

---

### 2026-08-24 — 2F Research AI validation tamamlandı; RVR-v0 finalleştirildi

**Research AI girdisi**
- Kullanıcı kapsamlı Deep Research raporu sağladı.
- Rapor spacing/retrieval, Bjork storage/retrieval, SM-2, FSRS, HLR, ACT-R, DAS3H, BKT forgetting, R-PFA, complex-skill retention, natural reuse, backlog ve prerequisite policy başlıklarını inceledi.

**Yönetici doğrulaması / düzeltmeleri**
- Cepeda spacing çalışmasının tek evrensel `%10–20` interval kuralı vermediği ayrıldı.
- Expanding spacing'in her koşulda equal spacing'den üstün olmadığı korundu.
- Güncel FSRS-6'nın 21 parametre kullandığı ve personal history azsa default params ile çalışabildiği doğrulandı; buna rağmen flashcard-domain defaults complex coding Skill'lerine canonical model yapılmadı.
- DAS3H multi-skill attribution'a referans oldu; fakat global project success → all Skills refresh çıkarımı reddedildi.
- `24–48h`, `1–2/3–5/5–7 gün`, EF, max interval, 1.5x overdue ve daily 8–10 gibi sayılar research constant değil heuristic/calibration olarak sınıflandı.
- Root/cluster refresh propagation reddedildi.
- Confirmed forgetting'te GRE score'u elle `0.50` yapma reddedildi; yeni evidence GRE-v0'u doğal yeniden hesaplar.

**Final `RVR-v0 — Retention Verification & Risk`**
- mastery ve retention ayrı eksen,
- time-based GRE score decay yok,
- retention states `untracked/fresh/stable/review_due/verification_due/at_risk`,
- review_due forgetting değil,
- delayed verification Skill türüne uygun H0 direct verified evidence,
- first failure → verification_due; recheck fail → GRE recalc/remediation,
- natural reuse strict structural + H0 + separate attribution + context diversity ile strong evidence,
- auto cluster refresh yok,
- critical verification_due unresolved iken dependent new work bekleyebilir,
- missed-day backlog dump yok,
- bounded/incremental local state,
- interval defaults versioned heuristic + 17C calibration.

**Çıktılar**
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/2F_RESEARCH_VALIDATION.md`
- `docs/2F_RESEARCH_BRIEF.md` fulfilled
- `docs/DECISIONS.md` — D-032
- canonical POST-STEP state dosyaları senkronlandı.

**AŞAMA 2:** ✅ TAMAMLANDI.

**Sonraki kesin adım:** `3A — Günlük kapasite`.
3A başlamadan yeni PRE-STEP GitHub refresh zorunlu.
