# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-24  
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`, D-024, D-027.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra ana çıktı + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN` ve gerekiyorsa `DECISIONS` senkronu zorunludur.

## 1. Ürün
Tek kullanıcı için, sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan ve yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

Ana rota:
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

## 2. Bağlayıcı ana kurallar
- Curriculum takvim değil prerequisite graph.
- Canonical mastery/prerequisite seviyesi Skill; evidence Objective'e bağlanabilir.
- Coverage/time/streak/task completion mastery değildir.
- Öğretilmemiş prerequisite yüzünden kullanıcı başarısız sayılmaz.
- Coding mastery gerçek user artifact ister.
- AI yardımı serbest; assisted performance independent mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez.
- English paralel gider; global technical blocker değildir.
- Core mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- D-028: bounded/incremental hesap, async ağır işler, gerçek cihaz performance QA.

## 3. Tamamlanan aşamalar

### AŞAMA 1 ✅
`1A–1D` tamamlandı.

### AŞAMA 2 ✅ — Öğrenme/Mastery
Canonical omurga:
- `GRE-v0 — Gated Recent Evidence` — D-031
- `RVR-v0 — Retention Verification & Risk` — D-032

Kritik: zaman mastery'yi otomatik düşürmez; `review_due` forgetting değildir; assistance independent mastery değildir.

### AŞAMA 3 ✅ — Adaptive Planner
- `3A` ✅ D-033 — hard daily capacity; no auto-overrun / no task debt
- `3B` ✅ D-034 — LearningNeed / TaskCandidate / Evidence ayrımı
- `3C` ✅ PBR-v0 / D-035 — semantic P0–P4 + deterministic rank
- `3D` ✅ PRG-v0 / D-036 — hard/soft Skill prerequisites; branch-local blocking
- `3E` ✅ VDW-v0 / D-037 — validated Objective-level diagnostic waiver
- `3F` ✅ SRR-v0 / D-038 — state-based re-entry; no absence debt
- `3G` ✅ PDT-v0 / D-039 — structured planner decision trace
- `3H` ✅ `docs/PLANNER_SIMULATION_SUITE.md`

### 3H final sonucu
```text
16 / 16 scenarios PASS
20 / 20 PDT-v0 invariants PASS
0 critical cross-spec contradiction
```

Doğrulanan kritik planner davranışları:
- same input + versions → same selected order + semantically equivalent trace,
- invalid/blocked candidate selected olmaz,
- explicit extension yoksa hard capacity aşılmaz,
- review_due forgetting değildir ve hard prerequisite'i otomatik bloklamaz,
- critical metadata tek başına P0 değildir,
- capacity'ye sığmayan higher-priority task gerçek fit nedeni ile defer edilir; lower-priority task sığarsa seçilebilir,
- prerequisite yalnız dependent branch'i bekletir,
- partial diagnostic yalnız validated Objective'leri waive eder,
- long absence stale task backlog'u replay etmez,
- absence failure/debt/starvation değildir,
- replan completed evidence'ı korur,
- deferred task tomorrow debt değildir,
- user-facing explanation internal trace dışına çıkmaz.

**Sınır:** 3H PASS policy/spec-level'dır. Production runtime sanal-user testleri 11F'te, gerçek cihaz/performance benchmark 17E'de ayrıca zorunludur.

## 4. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** başladı

- `4A` 🟡 **Günlük mikro değerlendirme — AKTİF**
- `4B–4E` ⬜ bekliyor

## 5. 4A'da kesinleştirilecekler

Ana soru:
> Günlük öğrenme içinde kullanıcıyı gereksiz sınava boğmadan, hangi Skill/Objective'lerin gerçekten ne kadar öğrenildiğini güvenilir biçimde nasıl ölçeceğiz?

Kesinleştirilecek:
- daily micro assessment purpose ve sınırı,
- teach/practice/assessment separation,
- Skill/Objective selection logic,
- capacity-aware composition,
- sabit bilimsel soru/dakika optimumu uydurmama,
- GRE-v0 direct/corroborating evidence uyumu,
- H0/H1–H4 assistance/provenance,
- prerequisite validation / contamination guard,
- retention/remediation ile çakışma/entegrasyon,
- low-capacity day davranışı,
- invalid/ambiguous item sonucu,
- assessment sonucu → mastery/remediation/replan akışı,
- 4B weekly, 4C monthly, 4D bank ve 4E AI-generated validation için ortak contract.

4A başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 6. İlk okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/LEARNING_ENGINE_SPEC.md`
8. `docs/LEARNING_BEHAVIOR_RULES.md`
9. `docs/TOPIC_STATE_MACHINE.md`
10. `docs/MASTERY_SIGNALS_SPEC.md`
11. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
12. `docs/MASTERY_FORMULA_V0.md`
13. `docs/RETENTION_FORGETTING_SPEC.md`
14. `docs/ADAPTIVE_PLANNER_SPEC.md`
15. `docs/TASK_TAXONOMY_SPEC.md`
16. `docs/PRIORITY_POLICY_SPEC.md`
17. `docs/PREREQUISITE_POLICY_SPEC.md`
18. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
19. `docs/MISSED_DAY_RECOVERY_SPEC.md`
20. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
21. `docs/PLANNER_SIMULATION_SUITE.md`
22. `docs/ENGLISH_FOUNDATION_RULES.md`
23. `docs/MASTER_PLAN.md`
24. `docs/PROGRESS_LOG.md`

## 7. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **4A — Günlük mikro değerlendirme** için yeni PRE-STEP GitHub refresh yap. Aşama 2 GRE/RVR ve Aşama 3 D-033–D-039 + 3H PASS kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.