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

### 2026-08-24 — Aşama 1 tamamlandı
- `1A–1D` ürün amacı, V1 scope, success criteria ve non-goals tamamlandı.

---

### 2026-08-24 — 2A–2D öğrenme/mastery davranışı tamamlandı
- Learning-unit hiyerarşisi, Topic state machine, mastery evidence taxonomy ve AI/hint provenance kuralları kilitlendi.
- Kararlar: D-021, D-023, D-025, D-026.

---

### 2026-08-24 — Zorunlu GitHub beyin tazeleme protokolü kilitlendi
- Her adım PRE-STEP refresh + POST-STEP sync ile yürütülüyor.
- Kararlar: D-024, D-027.

---

### 2026-08-24 — Mobil performans/akıcılık first-class requirement oldu
- Local/incremental/async tasarım ve gerçek cihaz QA yönü bağlayıcı.
- Karar: D-028.

---

### 2026-08-24 — 2E Research AI validation sonrası GRE-v0 finalleştirildi
- Final `GRE-v0 — Gated Recent Evidence`.
- Karar: D-031.

---

### 2026-08-24 — 2F Research AI validation sonrası RVR-v0 finalleştirildi
- Final `RVR-v0 — Retention Verification & Risk`.
- Karar: D-032.
- **AŞAMA 2 tamamlandı.**

---

### 2026-08-24 — 3A Günlük kapasite tamamlandı
- Explicit günlük süre hard budget.
- Remediation/retention günü otomatik uzatmaz.
- Safe split → smaller alternative → defer; deferred task next-day debt değildir.
- Çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md`.
- Karar: D-033.

---

### 2026-08-24 — 3B Görev kategorileri / TaskCandidate contract tamamlandı
- `State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent` canonical oldu.
- Purpose/activity/track/evidence eksenleri ayrıldı.
- Unresolved LearningNeed kalıcı; old task ID homework debt değildir.
- Çıktı: `docs/TASK_TAXONOMY_SPEC.md`.
- Karar: D-034.

---

### 2026-08-24 — 3C Priority / Selection Policy tamamlandı
- Final `PBR-v0 — Priority Bands & Rank Vector`.
- Eligibility priority'den önce; P0–P4 semantic bands + deterministic rank vector.
- Starvation/track-balance guard; duration semantic priority'den sonra.
- Çıktı: `docs/PRIORITY_POLICY_SPEC.md`.
- Karar: D-035.

---

### 2026-08-24 — 3D Prerequisite davranışı tamamlandı

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `LEARNING_ENGINE_SPEC`, `TOPIC_STATE_MACHINE`, `RETENTION_FORGETTING_SPEC`, `PRIORITY_POLICY_SPEC`, `TASK_TAXONOMY_SPEC` ve `LEARNING_BEHAVIOR_RULES` yeniden okundu.
- Aktif adımın 3D olduğu doğrulandı.
- Ayrı Research AI kullanılmadı; 3D mevcut Skill-level prerequisite, GRE/RVR ve PBR kararlarını deterministic eligibility sözleşmesine dönüştüren ürün/mimari adımıydı.

**Final `PRG-v0 — Prerequisite Readiness Gate`**
- Runtime prerequisite canonical `Skill → Skill`.
- Edge semantics `hard | soft`.
- Readiness `ready | ready_due | uncertain | not_ready`.
- `review_due` = ready_due; hard lock değil.
- Hard not_ready dependent candidate'ı bloke eder.
- Critical/strict verification_due dependent yeni work'u fresh verification çözülene kadar bekletebilir.
- Normal uncertain hard dependency conditional eligibility olabilir; tek contradiction tüm curriculum'u dondurmaz.
- Task-level `required_skill_ids` exact candidate hard requirement'tır.
- Exact planner order: state/need → candidate → validation/trust → prerequisite eligibility → PBR priority → capacity fit.
- Yalnız affected dependent branch bekler; independent branches devam eder.
- Started/mastered Topic prerequisite regression ile `locked` yapılmaz.
- Prerequisite contamination target Skill için invalid/unusable evidence'dır; negative mastery yazılmaz.
- Missing prerequisite repair/review/verification LearningNeed olarak planner'a geri beslenir.
- Technical English gerçek dependency değilse global technical blocker değildir.
- Resolver deterministic/bounded ve D-028 ile uyumlu.

**Çıktılar**
- `docs/PREREQUISITE_POLICY_SPEC.md`
- `docs/DECISIONS.md` — D-036

---

### 2026-08-24 — 3E Hızlı öğrenme / validated diagnostic waiver tamamlandı

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `MASTERY_FORMULA_V0`, `TOPIC_STATE_MACHINE`, `AI_ASSISTANCE_EVIDENCE_SPEC`, `PREREQUISITE_POLICY_SPEC` ve ilgili planner kuralları yeniden okundu.
- Aktif adımın 3E olduğu doğrulandı.
- Ayrı Research AI kullanılmadı; yeni diagnostic mastery threshold'u seçmek yerine mevcut GRE-v0 evidence standardını hızlı fakat güvenilir placement/waiver yoluna uygulama kararı alındı.

**Final `VDW-v0 — Validated Diagnostic Waiver`**
- Diagnostic GRE-v0'dan daha kolay ikinci mastery sistemi değildir.
- Kullanıcı self-report'u yalnız diagnostic'i başlatır; evidence değildir.
- Tek kolay quiz / recognition-only whole-topic skip yoktur.
- Skip Objective-level `DiagnosticCoverageWaiver` olarak modellenir; waiver mastery/retention değildir.
- Partial diagnostic yalnız kanıtlanan Objective'leri waive eder.
- `available → mastered` yalnız bütün required coverage + required/critical GRE gates birlikte sağlanınca mümkündür.
- Critical coding/debugging/transfer/diversity gate'leri diagnostic için düşürülemez.
- Waiver kanıtı H0, prerequisite-valid, verified ve provenance-clean olmalıdır.
- Integrated diagnostic birden çok Objective'i hızlandırabilir fakat component attribution ayrı doğrulanır.
- Prior-knowledge diagnostic fail otomatik remediation cezası değildir; normal learning'e dönülür.
- PRG-v0 diagnostic'te de eligibility/contamination guard sağlar.
- Diagnostic günlük capacity içindedir ve sonuç GRE → waiver → PRG → Topic → Planner replan zincirine girer.
- Waiver curriculum/objective version'a bağlıdır; yeni requirement eski waiver ile otomatik geçmez.
- Policy bounded/deterministic ve D-028 ile uyumlu.

**Çıktılar**
- `docs/DIAGNOSTIC_WAIVER_SPEC.md`
- `docs/DECISIONS.md` — D-037
- canonical POST-STEP state dosyaları ve `MASTER_PLAN` senkronlandı.

**Sonraki kesin adım:** `3F — Kaçırılan günler`.
3F başlamadan yeni PRE-STEP GitHub refresh zorunlu.
