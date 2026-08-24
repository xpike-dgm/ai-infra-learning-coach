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
- Çıktılar: `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md`.
- Karar: D-031.

---

### 2026-08-24 — 2F Research AI validation sonrası RVR-v0 finalleştirildi
- Final `RVR-v0 — Retention Verification & Risk`.
- Çıktılar: `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md`.
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
- Multi-Skill attribution, provenance, variant/dependency, prerequisite ve duration contract tanımlandı.
- Çıktı: `docs/TASK_TAXONOMY_SPEC.md`.
- Karar: D-034.

---

### 2026-08-24 — 3C Priority / Selection Policy tamamlandı

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `TASK_TAXONOMY_SPEC`, `RETENTION_FORGETTING_SPEC` ve `LEARNING_BEHAVIOR_RULES` yeniden okundu.
- Aktif adımın 3C olduğu doğrulandı.
- Ayrı Research AI kullanılmadı; sahte bilimsel numeric weight aramak yerine mevcut bağlayıcı state/evidence/capacity modelini deterministik selection policy'ye dönüştürme adımıydı.

**Final `PBR-v0 — Priority Bands & Rank Vector`**
- Priority open LearningNeed seviyesinde başlar; old task ID priority taşımaz.
- Eligibility/trust priority'den önce gelir.
- P0 integrity blocker, P1 repair/verify, P2 maintain/continue, P3 planned progress, P4 reinforce/optimize.
- Critical etiketi tek başına P0 yapmaz; gerçek dependency blocking gerekir.
- `review_due` forgetting değildir; standard due normal progress/maintenance bandında kalır, actual failure verification'a yükseltir.
- Aynı band içi weighted sum değil lexicographic rank vector: blocking → criticality → evidence severity → urgency → starvation → continuation → decision value → track balance → duration fit → stable tie-break.
- `score/minute`, task-count maximization ve opak knapsack canonical değildir.
- Starvation guard eligible ama sürekli ertelenen soft need'leri korur; task debt üretmez.
- Parallel English fixed yüzde değil due + starvation/track-balance ile korunur.
- Capacity yetmezse safe split → smaller alternative → defer; daha yüksek priority fiziksel olarak sığmıyorsa gün boş bırakılmaz, sonraki fit need seçilebilir.
- Same-need duplicate alternatives bastırılır; açık teach→practice→assess mini-chain exception olabilir.
- `PriorityDecisionTrace` explainability için zorunlu machine-readable ara çıktı.
- Policy deterministic/bounded ve D-028 ile uyumlu.

**Çıktılar**
- `docs/PRIORITY_POLICY_SPEC.md`
- `docs/DECISIONS.md` — D-035
- canonical POST-STEP state dosyaları ve `MASTER_PLAN` senkronlandı.

**Sonraki kesin adım:** `3D — Prerequisite davranışı`.
3D başlamadan yeni PRE-STEP GitHub refresh zorunlu.
