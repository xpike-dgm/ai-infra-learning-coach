# Project Progress Log

Bu dosya projenin oturumlar arası kalıcı ilerleme günlüğüdür. Ayrıntılı plan `MASTER_PLAN.md` / `EXECUTION_INDEX.md`; bu dosya ne zaman ne yapıldığını ve neden yapıldığını kronolojik olarak kaydeder.

---

### 2026-08-24 — Proje hafızası ve yürütme sistemi kuruldu
- GitHub durable source of truth oldu.
- 19 aşamalı ilk master plan ve acceptance yaklaşımı kuruldu.
- D-024/D-027: her numaralı adımda PRE-STEP refresh + POST-STEP sync zorunlu hale geldi.

---

### 2026-08-24 — AŞAMA 1 tamamlandı
- `1A–1D`: product purpose, V1 scope, success criteria, non-goals.

---

### 2026-08-24 — AŞAMA 2 tamamlandı
- 2A learning units: `Domain → Module → Topic → Skill → Learning Objective`.
- 2B Topic state machine.
- 2C mastery signal taxonomy.
- 2D H0–H4 AI/hint provenance.
- 2E Research AI validation sonrası `GRE-v0 — Gated Recent Evidence` / D-031.
- 2F Research AI validation sonrası `RVR-v0 — Retention Verification & Risk` / D-032.

---

### 2026-08-24 — AŞAMA 3 tamamlandı
- 3A D-033 — hard daily capacity / no task debt.
- 3B D-034 — LearningNeed / TaskCandidate / Evidence.
- 3C D-035 — PBR-v0 semantic priority bands + deterministic rank.
- 3D D-036 — PRG-v0 Skill prerequisite readiness.
- 3E D-037 — VDW-v0 validated diagnostic waiver.
- 3F D-038 — SRR-v0 state-based re-entry.
- 3G D-039 — PDT-v0 structured planner decision trace.
- 3H simulation: **16/16 scenarios PASS, 20/20 invariants PASS, 0 critical contradiction**.

---

### 2026-08-24 — 4A Daily Micro Assessment tamamlandı
- Final: `DMA-v0 — Daily Micro Assessment` / D-040.
- Daily assessment zorunlu quiz/kota değildir.
- Objective-matched evidence, H0/assistance/provenance, prerequisite fairness, invalid/provisional safety ve evidence→GRE/RVR→replan pipeline kilitlendi.
- Ana çıktı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

---

### 2026-08-24 — D-041 professional-readiness kapsam genişletmesi
- Yaklaşık üç yıllık horizon kaldırıldı; rota gerektiğinde 4+ yıl veya daha uzun olabilir.
- Takvim readiness gate değildir.
- Final target = AI Infrastructure / ML Systems / GPU Systems için verified engineering capability.
- Integrated systems, debugging, transfer, performance ve professional capstone evidence hedefe dahil edildi.
- V1 full curriculum'u beklemeyecek.
- Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

---

### 2026-08-25 — D-042 Python foundation
- Python common core'un resmi parçası oldu.
- C/C++ yerine geçmez; automation/testing/benchmark, ML/PyTorch, infra tooling ve ileri Python practices için kullanılır.

---

### 2026-08-25 — D-043 geri çekildi
- Önceki standalone specialization-track aşaması kullanıcı talebinin yanlış yorumuydu.
- Canonical plan'dan çıkarıldı.

---

### 2026-08-25 — D-044 Granular Capability Map / AŞAMA 6
- Asıl ihtiyaç: ana rotadaki bütün büyük alanları `Domain → Module → Topic → Skill → Learning Objective` seviyesine ayrıntılı biçimde bölmek.
- Hedef: `Python zayıf` yerine `Python → Control Flow → Loops → while termination` gibi hedefli weakness/mastery/remediation.
- Yeni **AŞAMA 6 — Granular Capability Map** planlama aşamalarının arasına eklendi.
- Henüz başlanmamış future stages yeniden indekslendi; toplam 20 aşama.
- Ana charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

---

### 2026-08-25 — 4B Weekly Assessment tamamlandı

**PRE-STEP GitHub refresh**
- Fresh canonical state ve assessment/mastery/planner specs yeniden okundu.
- 4A'nın tamamlandığı ve gerçek aktif adımın 4B olduğu doğrulandı.

**Research AI kararı**
- Ayrı Research AI kullanılmadı.
- Scientifically optimal soru/süre/score uydurulmadı; empirical calibration AŞAMA 18'e bırakıldı.

**Final model: `WBA-v0 — Weekly Blueprint Assessment` / D-045**
- Weekly exam tek overall score/pass-fail değildir.
- Önce state-temelli blueprint, sonra item/task seçimi yapılır.
- Recent progress, weakness/verification, critical prerequisite, retention, integration/transfer ve gerektiğinde English role'ları quota olmadan kullanılır.
- Fixed soru sayısı/süre/kategori yüzdesi yoktur.
- Weekly evidence GRE/RVR'ı bypass etmez.
- PRG prerequisite fairness, family diversity, Objective-specific evidence modality, H0 assistance standardı ve invalid/provisional safety korunur.
- Split/pause/resume mümkündür; incomplete/missed weekly exam failure/debt değildir.
- İlk clean contradiction instant unmastery değildir.
- Broad Domain pass/fail yazılmaz; D-044 granular localization korunur.
- 4C için common `AssessmentBlueprint / AssessmentBlueprintSlot / AssessmentSessionResult` abstraction kilitlendi.

**Çıktılar**
- `docs/WEEKLY_ASSESSMENT_SPEC.md`
- D-045

**Sonraki kesin adım:** `4C — Aylık yeterlilik sınavı`.

---

### 2026-08-25 — 4C Monthly Capability Assessment tamamlandı

**PRE-STEP GitHub refresh**
- Fresh olarak `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `PROJECT_MEMORY_PROTOCOL` okundu.
- Doğrudan ilgili `WEEKLY_ASSESSMENT_SPEC`, `PROFESSIONAL_READINESS_TARGET`, `V1_SUCCESS_CRITERIA` ve assessment/mastery/prerequisite bağlamı yeniden doğrulandı.
- 4B'nin gerçekten tamamlandığı ve aktif adımın 4C olduğu doğrulandı.

**Research AI kararı**
- Ayrı Research AI kullanılmadı.
- 4C scientifically optimal soru sayısı, exam süresi, pass score, fixed transfer oranı veya universal critical revalidation cadence seçmedi.
- Empirical assessment UX ve false-positive/false-negative calibration AŞAMA 18'e bırakıldı.

**Final model: `MCA-v0 — Monthly Capability Assessment` / D-046**
- Monthly assessment tek ay sonu notu veya domain pass/fail değildir.
- WBA-v0 common blueprint/result contract'ını monthly extension ile yeniden kullanır.
- Role family'leri: longitudinal required capability, persistent weakness/verification, critical capability revalidation, delayed retention, cross-topic transfer, integrated application, gerektiğinde Technical English ve professional evidence checkpoint.
- Role family'leri fixed quota değildir.
- Monthly assessment bütün geçmiş curriculum'u cumulative olarak tekrar test etmez; state-based bounded longitudinal sampling yapar.
- Recent/older balance fixed yüzdelerle değil canonical state ve decision value ile belirlenir.
- Critical Skill sırf critical olduğu için her ay otomatik retest edilmez; gerçek revalidation ihtiyacı gerekir.
- Transfer/integration weekly'den daha geniş olabilir fakat yalnız öğretilmiş prerequisites ve component-level attribution ile çalışır.
- Persistent weakness tek bir kötü item'dan türetilmez.
- Professional evidence checkpoint final professional-readiness veya capstone gate değildir.
- Fixed soru sayısı, fixed süre, fixed pass score yoktur.
- Daily hard capacity korunur; session multi-block/split/pause/resume olabilir; incomplete/missed monthly exam failure/debt değildir.
- H0/H1–H4, provenance, invalid/ambiguous/provisional item, root-prerequisite contamination ve GRE/RVR hysteresis korunur.
- Raw broad `Python failed` gibi state yazılmaz; D-044 granular Skill/Objective localization korunur.
- V1 SC-016 gereği güvenilir persistent/critical gap yalnız raporda kalmaz; LearningNeed/PBR/PRG/planner üzerinden gelecek planı gerçekten değiştirebilir.
- 4D Question Bank için scope eligibility, blueprint role, target/prerequisite, evidence, family/context/transfer, rubric/evaluator, trust/version, exposure, duration/atomicity ve language/freshness metadata handoff'u tanımlandı.

**Çıktılar**
- `docs/MONTHLY_ASSESSMENT_SPEC.md`
- `docs/DECISIONS.md` — D-046
- canonical POST-STEP state dosyaları + `MASTER_PLAN` senkronu.

**Sonraki kesin adım:** `4D — Soru bankası`.
4D başlamadan yeni PRE-STEP GitHub refresh zorunlu.