# Planner Simulation Suite — 3H

**Adım:** 3H — Planner simülasyonu  
**Durum:** TAMAMLANDI — PASS  
**Tarih:** 2026-08-24  
**Kapsam:** 3A–3G policy-level deterministic simulation

Bu belge Aşama 3'te tasarlanan Adaptive Planner sözleşmelerini sanal kullanıcı/state senaryolarında zorlayarak doğrular.

Bağlayıcı kaynaklar:
- `docs/ADAPTIVE_PLANNER_SPEC.md` — 3A
- `docs/TASK_TAXONOMY_SPEC.md` — 3B
- `docs/PRIORITY_POLICY_SPEC.md` — PBR-v0 / 3C
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / 3D
- `docs/DIAGNOSTIC_WAIVER_SPEC.md` — VDW-v0 / 3E
- `docs/MISSED_DAY_RECOVERY_SPEC.md` — SRR-v0 / 3F
- `docs/PLANNER_EXPLAINABILITY_SPEC.md` — PDT-v0 / 3G
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0

---

# 1. Test türü ve sınırı

Bu 3H çalışması **spec-level / policy simulation**'dır. Henüz production planner kodu yoktur; dolayısıyla bu belge:
- planner contract'larının birbirleriyle mantıksal olarak çalıştığını,
- deterministic seçim/gating/capacity/replan kurallarının zor senaryolarda çelişmediğini,
- PDT-v0 reason trace'in gerçek kararı açıklayabildiğini

doğrular.

Bu PASS şunların yerine geçmez:
- 11F gerçek Planner Engine sanal kullanıcı testleri,
- 17B planner pilot gözlemi,
- 17E gerçek cihaz/performance benchmark.

D-028 için burada doğrulanan şey mimari/algoritmik beklentidir: bounded current-state lookup, bounded candidate alternatives ve ref-based trace. Gerçek latency/RAM/CPU ölçümü sonraki teknik aşamalardadır.

---

# 2. Simulation oracle

Her senaryo şu canonical sırayla değerlendirilir:

```text
current state
→ semantic LearningNeed generation/dedupe
→ bounded candidate generation
→ validation/trust
→ PRG-v0 prerequisite eligibility
→ PBR-v0 priority/rank
→ 3A capacity fit/split/smaller/defer
→ PlannedTask
→ PDT-v0 decision trace
```

Re-entry varsa SRR-v0 current-state regeneration önce uygulanır. Diagnostic varsa VDW-v0 yalnız valid GRE-compatible evidence üzerinden waiver üretebilir. Retention zamanı RVR-v0 state'ini günceller fakat zaman tek başına negative evidence değildir.

PASS kuralı:
- Kritik invariant ihlali = scenario FAIL.
- FAIL halinde Aşama 3 kapatılamaz; ilgili spec düzeltilip senaryo tekrar çalıştırılır.
- Bu turda kritik invariant ihlali bulunmadı.

---

# 3. Sanal kullanıcı profilleri

```text
U-NOVICE      : yeni başlayan, normal yeni öğrenme + English
U-BLOCKED     : critical prerequisite üzerinde unresolved verification
U-REVIEW      : mastered Skill review_due, yeni negative evidence yok
U-TIGHT       : çok düşük/azalan günlük kapasite
U-FASTPATH    : daha önce bazı konuları öğrenmiş, diagnostic isteyen kullanıcı
U-RETURNING   : uzun aradan dönen, çok sayıda due state'i olan kullanıcı
U-REMEDIATION : session sırasında yeni weakness/remediation oluşan kullanıcı
U-AUDIT       : determinism/trace/invalid/duplicate stres profili
```

---

# 4. Scenario sonuçları

## S01 — Normal progress / 60 dk gün

**State**
- hard capacity = 60 dk; V0 planning budget ≈ 54 dk,
- C new-learning need = P3, 20 dk,
- Technical English parallel need = P3, 10 dk,
- optional integration = P4, 30 dk,
- prerequisites valid.

**Beklenen**
- C 20 dk + English 10 dk seçilir,
- optional 30 dk kalan planning budget'a güvenli biçimde sığmıyorsa defer edilir,
- defer failure/debt değildir,
- bütün selected task'lar açık need + selection reason taşır.

**Sonuç:** PASS.

---

## S02 — Critical prerequisite verification dependent branch'i bekletiyor

**State**
- `pointer_dereference`: historical mastery var fakat `verification_due`, `critical_prerequisite=true`,
- Linked List yeni task'i strict olarak pointer'a bağlı,
- pointer fresh verification = P0, 15 dk,
- Linked List new learning = P3 fakat PRG blocked,
- English = P3, 10 dk ve bağımsız,
- hard capacity = 30 dk; planning budget ≈ 27 dk.

**Beklenen plan**
- pointer verification 15 dk,
- English 10 dk,
- Linked List seçilmez.

**Beklenen trace**
```text
pointer verification:
  need.verification_due
  priority.p0_integrity_blocker
  selection.selected

linked list:
  eligibility.blocked_critical_verification
  selection.blocked_prerequisite
  related_ref = skill.pointer_dereference

english:
  priority.p3_planned_progress
  selection.selected
```

**Sonuç:** PASS. Yalnız dependent branch bekledi; bağımsız hat devam etti.

---

## S03 — `review_due` prerequisite'i hard-lock yapmıyor

**State**
- pointer GRE-v0 mastered,
- retention = `review_due`, yeni negative evidence yok,
- pointer Linked List için hard prerequisite,
- pointer retention = P2, 10 dk,
- Linked List new learning = P3, 15 dk,
- hard capacity = 30 dk; planning budget ≈ 27 dk.

**Beklenen**
- PRG readiness = `ready_due`, dependent task eligible,
- retention review 10 dk + Linked List 15 dk sığar,
- user explanation `review zamanı geldi`; `unuttun/başarısızsın` demez.

**Sonuç:** PASS.

---

## S04 — Daha yüksek priority task kalan kapasiteye sığmıyor

**State**
- kalan planning budget = 12 dk,
- P1 remediation candidate = 25 dk, unsplittable, smaller eligible alternative yok,
- P3 English task = 10 dk, eligible.

**Beklenen**
- P1 semantik olarak daha yüksek priority kalır fakat capacity-deferred,
- P3 10 dk task seçilebilir,
- trace `P3 daha önemliydi` demez.

**Beklenen trace**
```text
P1:
  priority.p1_repair_or_verify
  capacity.deferred_not_enough_time
  selection.not_selected_capacity

P3:
  priority.p3_planned_progress
  capacity.selected_within_budget
  selection.selected
```

**Sonuç:** PASS.

---

## S05 — 8 dakikalık micro-session

**State**
- hard capacity = 8 dk,
- eligible 5 dk micro retrieval,
- 20 dk heavy new-topic task.

**Beklenen**
- sistem günü failure saymaz,
- reserve gerektiğinde micro-task için esnetilebilir fakat hard 8 dk aşılmaz,
- 5 dk task seçilebilir,
- 20 dk task defer edilir; debt değildir.

**Sonuç:** PASS.

---

## S06 — Partial diagnostic fast path

**State**
`Basic Pointers` required Objective set'i:
- O1 Address vs Value,
- O2 Pointer Declaration,
- O3 Dereference,
- O4 Write Through Pointer.

Kullanıcı fast path ister. Valid H0 diagnostic evidence yalnız O1 ve O2 için GRE-compatible gate'leri karşılar. O3/O4 için yeterli independent evidence yoktur.

**Beklenen**
- waiver yalnız O1 + O2,
- O3/O4 normal learning/confirm akışında kalır,
- whole Topic otomatik mastered olmaz,
- explanation yalnız doğrulanan bölümlerin atlandığını söyler.

**Sonuç:** PASS.

---

## S07 — 30 günlük ara + 25 stale task + 80 review_due Skill

**State**
- last active ≈ 30 gün önce,
- history'de 25 başlanmamış eski PlannedTask,
- current RVR inventory'de 80 Skill review_due,
- critical verification = P0, 15 dk,
- critical retention = P2, 10 dk,
- C new learning = P3, 15 dk,
- English = P3, 10 dk,
- hard capacity = 50 dk; planning budget ≈ 45 dk.

**Beklenen**
- 25 stale task replay edilmez,
- 80 due Skill = 80 Today task değildir,
- current state'ten fresh needs üretilir,
- örnek plan: P0 15 + P2 10 + C 15 = 40 dk; English kalan 5 dk'ya sığmıyorsa defer,
- absence failure/debt/starvation değildir,
- mastery sırf 30 gün geçti diye düşmez.

**Sonuç:** PASS.

---

## S08 — Re-entry + safe paused checkpoint

**State**
- 7 günlük ara,
- version-valid/safe C checkpoint = continue-learning P2, 20 dk,
- verification = P1, 15 dk,
- English = P3, 10 dk,
- hard capacity = 50 dk; planning budget ≈ 45 dk.

**Beklenen**
- checkpoint otomatik ilk sıraya konmaz,
- P1 verification önce,
- ardından P2 continuation,
- ardından P3 English sığarsa seçilir,
- checkpoint yalnız continuation value nedeniyle avantaj taşır; debt değildir.

**Sonuç:** PASS.

---

## S09 — Session ortasında kullanıcı kalan süreyi azaltıyor

**State**
- initial plan birden fazla task içeriyor,
- ilk task tamamlandı ve valid evidence üretti,
- kullanıcı `15 dakikam kaldı` diyor.

**Beklenen**
- `PlannerReplanEvent = remaining_time_changed`,
- yeni plan version üretilir,
- completed task/evidence korunur,
- yalnız unstarted/remaining task'lar yeni remaining capacity altında tekrar çözülür,
- user duration reduction geçmiş başarıyı geri almaz.

**Sonuç:** PASS.

---

## S10 — Session sırasında yeni remediation oluşuyor

**State**
- kullanıcı ilk C task'ında actionable weakness üretir,
- yeni remediation need = P1, 15 dk,
- unstarted normal C new-learning = P3, 20 dk,
- English = P3, 10 dk,
- günün hard budget'ı değişmedi.

**Beklenen**
- `replan.new_remediation_created`,
- completed evidence korunur,
- P1 remediation remaining plan'a girer,
- lower-priority görevler gerekiyorsa defer edilir,
- gün otomatik 15 dk uzatılmaz.

**Sonuç:** PASS.

---

## S11 — Invalid / untrusted high-stakes candidate priority ile kurtarılamıyor

**State**
- critical verification need yüksek priority,
- Candidate A: high-stakes için `invalid/untrusted`,
- Candidate B: aynı need için valid/trusted alternative veya yok.

**Beklenen**
- Candidate A asla selected olmaz,
- valid B varsa B değerlendirilir,
- B yoksa need `no_valid_candidate`; priority invalid task'i seçtirmez.

**Sonuç:** PASS.

---

## S12 — Aynı semantic need için duplicate alternatives

**State**
- tek `pointer retention` LearningNeed,
- Candidate A = 12 dk coding,
- Candidate B = 10 dk debugging,
- ikisi de aynı need'i karşılayan alternatives.

**Beklenen**
- yalnız bir alternative selected,
- diğeri `superseded_same_need_alternative`,
- iki ayrı retention borcu/task'i oluşmaz.

**Sonuç:** PASS.

---

## S13 — `critical` etiketi tek başına P0 üretmiyor

**State**
- Skill `critical_prerequisite=true`,
- state healthy (`mastered + stable`),
- unresolved contradiction/remediation yok,
- şu anda bloke ettiği required path yok,
- yalnız normal reinforcement/new-learning context var.

**Beklenen**
- sırf `critical` metadata nedeniyle P0 yok,
- ihtiyaç gerçek semantic trigger'ına göre P2/P3/P4 olabilir,
- trace sahte `integrity_blocker` üretmez.

**Sonuç:** PASS.

---

## S14 — Same-input determinism

S01 state snapshot, candidate metadata, capacity input, curriculum/content versions ve policy/config versions değiştirilmeden iki kez çözülür.

**Beklenen**
- selected task sırası aynı,
- final disposition'lar aynı,
- reason code semantiği aynı,
- timestamp/UUID farklı olabilir.

**Sonuç:** PASS.

---

## S15 — User-facing explanation trace dışına çıkmıyor

S02/S03/S04/S07 üzerinde açıklama üretimi kontrol edilir.

**Beklenen**
- exact blocker yalnız trace'teki Skill ref'ten gelir,
- `review_due` için forgetting iddiası yok,
- absence için debt/failure iddiası yok,
- capacity-fit yüzünden seçilmeyen P1 task `lower priority` diye yanlış anlatılmaz,
- LLM kapalı olsa template fallback aynı temel nedeni üretebilir.

**Sonuç:** PASS.

---

## S16 — Uzun ara sonrası yarım kalmış high-stakes H0 attempt

**State**
- kullanıcı diagnostic/retention H0 attempt'ini tamamlamadan uygulamadan ayrılmış,
- uzun süre sonra geri dönüyor.

**Beklenen**
- incomplete attempt negative evidence değildir,
- eski partial attempt mastery/retention PASS/FAIL olarak skorlanmaz,
- gerekiyorsa fresh/unseen candidate üretilir,
- solution exposure varsa existing H3/H4/recheck kuralları korunur.

**Sonuç:** PASS.

---

# 5. PDT-v0 20 invariant sonucu

| # | Invariant | Kapsayan senaryo | Sonuç |
|---|---|---|---|
| 1 | Same input → same plan + equivalent trace | S14 | PASS |
| 2 | blocked/invalid candidate selected olmaz | S02, S11 | PASS |
| 3 | explicit extension yoksa hard capacity aşılmaz | S01, S04, S05, S07, S10 | PASS |
| 4 | her selected task açık need + reason'a bağlı | S01–S15 | PASS |
| 5 | seçilmeyen eligible candidate disposition taşır | S01, S04, S07, S12 | PASS |
| 6 | hard prerequisite exact Skill ref ile açıklanır | S02 | PASS |
| 7 | review_due forgetting/failure diye anlatılmaz | S03, S15 | PASS |
| 8 | absence negative evidence/debt/starvation değildir | S07, S15, S16 | PASS |
| 9 | re-entry stale unstarted backlog replay etmez | S07 | PASS |
| 10 | high-priority fit etmiyorsa gerçek capacity nedeni korunur | S04 | PASS |
| 11 | aynı semantic need duplicate selected task üretmez | S12 | PASS |
| 12 | partial diagnostic yalnız validated Objective'leri waive eder | S06 | PASS |
| 13 | blocker yalnız dependent branch'i bekletir | S02 | PASS |
| 14 | replan completed evidence'ı korur | S09, S10 | PASS |
| 15 | user explanation facts ⊆ internal trace | S15 | PASS |
| 16 | LLM olmadan template fallback mümkün | S15 | PASS |
| 17 | trace/candidate work bounded-current-state yaklaşımında kalabilir | S07, S11, S12 | PASS* |
| 18 | critical etiketi tek başına P0 değildir | S13 | PASS |
| 19 | review_due prerequisite'i otomatik hard-block yapmaz | S03 | PASS |
| 20 | deferred task tomorrow debt olarak açıklanmaz | S01, S04, S05, S07 | PASS |

`PASS*`: policy/architecture düzeyinde PASS. Runtime latency/memory ölçümü henüz yapılmamıştır; 11F/17E'de gerçek implementasyon benchmark'ı zorunludur.

---

# 6. Cross-spec contradiction check

Simulation sırasında şu kritik sınırlar ayrıca kontrol edildi:

```text
GRE mastery != RVR retention schedule
LearningNeed != TaskCandidate != Evidence
validation/PRG eligibility BEFORE priority
priority BEFORE capacity fit
review_due != forgetting
absence != negative evidence/debt/starvation
coverage waiver != current mastery/retention
critical metadata != automatic P0
completed task != automatic mastery
```

**Sonuç:** 3A–3G arasında Aşama 3'ü bloke eden contradiction bulunmadı.

Bir dokümantasyon drift'i tespit edildi: `ADAPTIVE_PLANNER_SPEC.md` üst bilgisi hâlâ yalnız `3A` tamamlanmış gibi görünüyordu. Bu davranış hatası değildir; 3H POST-STEP sync'te Aşama 3 complete olarak güncellenecektir.

---

# 7. Aşama 3 acceptance kararı

3H scenario suite:

```text
16 / 16 scenarios PASS
20 / 20 PDT-v0 invariants PASS
0 critical policy contradiction
```

Dolayısıyla **AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla = PASS / TAMAMLANDI**.

Bu kabul şu anlama gelir:
- planner design contract implementation'a taşınabilecek kadar tanımlıdır,
- seçim, eligibility, capacity, diagnostic, re-entry ve explainability davranışları birlikte tutarlıdır,
- bundan sonraki assessment/curriculum/UX/architecture aşamaları bu contract'ları tüketebilir.

Bu kabul production planner kodunun test edildiği anlamına gelmez. Gerçek implementasyon 11A–11F ve pilot/QA 17A–17F'te tekrar doğrulanacaktır.

---

# 8. Sonraki adım

Aşama 3 kapanışından sonra canonical sıradaki adım:

**4A — Günlük mikro değerlendirme**

4A başlamadan yeni PRE-STEP GitHub refresh zorunludur.