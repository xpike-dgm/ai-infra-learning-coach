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
- Fresh olarak `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN` okundu.
- Doğrudan ilgili `DAILY_MICRO_ASSESSMENT_SPEC`, `MASTERY_SIGNALS_SPEC`, `AI_ASSISTANCE_EVIDENCE_SPEC`, `MASTERY_FORMULA_V0`, `RETENTION_FORGETTING_SPEC`, `TASK_TAXONOMY_SPEC`, `PRIORITY_POLICY_SPEC`, `PREREQUISITE_POLICY_SPEC`, `ADAPTIVE_PLANNER_SPEC`, `PLANNER_EXPLAINABILITY_SPEC`, `LEARNING_BEHAVIOR_RULES`, `ENGLISH_FOUNDATION_RULES`, `V1_SUCCESS_CRITERIA`, `GRANULAR_CAPABILITY_MAP_PLAN` yeniden okundu.
- 4A'nın tamamlandığı ve gerçek aktif adımın 4B olduğu doğrulandı.

**Research AI kararı**
- Ayrı Research AI kullanılmadı.
- 4B, “bilimsel optimum 30 soru / 60 dakika / %70 geçme” gibi psychometric sabitler seçmedi; mevcut GRE/RVR/PRG/PBR/DMA contract'larını weekly composition'a bağlayan deterministic product-policy adımı olarak yürütüldü.
- Empirik duration/UX/false-positive/false-negative calibration AŞAMA 18 pilotuna bırakıldı.

**Final model: `WBA-v0 — Weekly Blueprint Assessment` / D-045**
- Weekly exam tek overall score/pass-fail değildir.
- Önce state-temelli blueprint, sonra item/task seçimi yapılır.
- Blueprint role family'leri: recent required progress, weakness/verification, critical prerequisite confidence, retention due, integration/transfer ve gerektiğinde parallel English.
- Bu role'lar fixed quota değildir; fixed soru sayısı/süre/kategori yüzdesi yoktur.
- Weekly evidence GRE/RVR'ı bypass etmez veya extra weight almaz.
- PRG prerequisite fairness, root-cause contamination, variant/dependency diversity ve Objective-specific evidence modality korunur.
- Weekly session safe boundaries arasında split/pause/resume olabilir; incomplete veya missed weekly exam failure/debt/stack değildir.
- H0 varsayılan independent measurement; H1–H4 positive independent mastery değildir; H3/H4 fresh/unseen recheck gerektirir.
- Invalid/ambiguous/prerequisite-contaminated/provisional item strong mastery-changing karar veremez.
- İlk clean post-mastery contradiction instant unmastery değil `verification_due` üretir.
- Raw weekly sonuç broad `Python failed` gibi coarse state yazamaz; weakness D-044 gereği Skill/Objective düzeyinde lokalize edilir.
- Result yalnız `Attempt/Artifact → EvidenceEvent → GRE/RVR → weakness/verification/remediation → PRG/Topic → planner/replan` zinciriyle programı değiştirir.
- 4C için common `AssessmentBlueprint / AssessmentBlueprintSlot / AssessmentSessionResult` abstraction kilitlendi.

**Çıktılar**
- `docs/WEEKLY_ASSESSMENT_SPEC.md`
- `docs/DECISIONS.md` — D-045
- canonical POST-STEP state dosyaları + `MASTER_PLAN` senkronu.

**Sonraki kesin adım:** `4C — Aylık yeterlilik sınavı`.
4C başlamadan yeni PRE-STEP GitHub refresh zorunlu.
