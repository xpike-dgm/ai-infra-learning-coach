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
- `1A–1D` ürün amacı, V1 scope, success criteria ve non-goals tamamlandı.

---

### 2026-08-24 — 2A–2D öğrenme/mastery davranışı tamamlandı
- Learning-unit hiyerarşisi, Topic state machine, mastery evidence taxonomy ve AI/hint provenance kuralları kilitlendi.
- Çıktılar: `LEARNING_ENGINE_SPEC`, `TOPIC_STATE_MACHINE`, `MASTERY_SIGNALS_SPEC`, `AI_ASSISTANCE_EVIDENCE_SPEC`.
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
- İlk Beta-style candidate ve sabit assistance/evaluator multiplier'ları kaldırıldı.
- Final `GRE-v0 — Gated Recent Evidence`.
- Çıktılar: `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md`.
- Karar: D-031.

---

### 2026-08-24 — 2F Research AI validation sonrası RVR-v0 finalleştirildi
- Mastery-retention ayrıldı, time-based mastery decay reddedildi, review/verification/natural reuse/critical-prereq/backlog davranışı kilitlendi.
- Final `RVR-v0 — Retention Verification & Risk`.
- Çıktılar: `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md`.
- Karar: D-032.
- **AŞAMA 2 tamamlandı.**

---

### 2026-08-24 — 3A Günlük kapasite tamamlandı

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `LEARNING_BEHAVIOR_RULES` ve `RETENTION_FORGETTING_SPEC` yeniden okundu.
- Aktif adımın 3A olduğu ve Aşama 2 GRE/RVR kararlarıyla çelişki olmadığı doğrulandı.
- Ayrı Research AI kullanılmadı; adım bilimsel optimum çalışma süresi seçmek yerine user-controlled capacity contract tasarımıydı.

**Final 3A capacity contract**
- Kullanıcının explicit günlük süresi planner'ın hard envelope'u.
- Capacity source: today override → selected profile → scheduled default → normal profile.
- V0 editable presetler `30/60/90 dk`; `10%` planning reserve ve `10 dk` minimum plannable block engineering heuristic.
- Fixed task-category yüzdeleri yok.
- Remediation/retention ortaya çıkınca gün otomatik uzamaz; remaining capacity replan edilir.
- Kullanıcı session ortasında daha az/fazla süre söylerse remaining plan yeniden üretilir.
- Unfinished veya planned-but-not-started task failure/mastery evidence değildir.
- Task sığmıyorsa safe split → smaller alternative → defer.
- Deferred task lineer next-day debt değildir; current-state replan yapılır.
- Critical task bile user explicit extension olmadan budget'ı aşmaz.
- Duration metadata, future user pace adaptation ve active-vs-wall-clock timing contract'ı tanımlandı.
- Capacity resolver deterministic/versioned ve LLM'den bağımsız.

**Çıktı**
- `docs/ADAPTIVE_PLANNER_SPEC.md` — 3A.
- `docs/DECISIONS.md` — D-033.

---

### 2026-08-24 — 3B Görev kategorileri / TaskCandidate contract tamamlandı

**PRE-STEP**
- 3A sonrası yeni GitHub refresh yapıldı; `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `ADAPTIVE_PLANNER_SPEC`, mastery/assistance ve English prerequisite kuralları yeniden okundu.
- Aktif adımın 3B olduğu doğrulandı.
- Ayrı Research AI kullanılmadı; 3B mevcut bağlayıcı learning/mastery davranışlarını planner primitive'lerine dönüştüren ürün/mimari adımıydı.

**Final 3B modeli**
- `State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent` zinciri canonical oldu.
- Ertelenen task'ın kendisi kalıcı borç değildir; unresolved `LearningNeed` kalır ve sonraki planda fresh candidate doğurabilir.
- `primary_purpose`: `teach | practice | assess | remediate | retain | diagnose | reinforce`.
- `activity_kind` ayrı eksendir: explanation, worked example, recall, coding, debugging, hands-on system task, transfer, integrated project, language activity vb.
- English ayrı purpose değil curriculum track; coding/debugging/project purpose değil activity türüdür.
- Task category evidence değildir; completion mastery üretmez.
- Guided/independent/H0 requirement ayrı assistance/independence metadata'sıdır.
- Multi-Skill task'ta global project success component Skills'e otomatik evidence vermez; Objective bazlı structural essentiality + separate observability/attribution gerekir.
- Task provenance/validation, variant/dependency, prerequisite/tools ve 3A duration/splittable/checkpoint metadata'sı contract'a bağlandı.
- `paused_progress` gerçek checkpoint'i koruyabilir; `deferred_candidate` ephemeral'dır ve debt değildir.
- 3C'nin kullanacağı ham priority sinyalleri TaskCandidate'a eklendi fakat 3B priority weight uydurmadı.
- Candidate generation bounded/deterministic ve D-028 performans kuralıyla uyumlu.

**Çıktılar**
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/DECISIONS.md` — D-034
- canonical POST-STEP state dosyaları ve `MASTER_PLAN` senkronlandı.

**Sonraki kesin adım:** `3C — Öncelik puanı`.
3C başlamadan yeni PRE-STEP GitHub refresh zorunlu.
