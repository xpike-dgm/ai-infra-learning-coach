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
- Coding mastery gerçek user-authored artifact ister.
- AI yardımı serbest; assisted performance independent mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez.
- English paralel gider; global technical blocker değildir.
- Core mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- D-028: bounded/incremental hesap, async ağır işler, gerçek cihaz performance QA.

## 3. Tamamlanan aşamalar

### AŞAMA 1 ✅
`1A–1D` tamamlandı.

### AŞAMA 2 ✅ — Öğrenme/Mastery
- `GRE-v0 — Gated Recent Evidence` — D-031
- `RVR-v0 — Retention Verification & Risk` — D-032

Kritik: time-decay mastery yok; `review_due` forgetting değildir; H1–H4 positive independent mastery değildir.

### AŞAMA 3 ✅ — Adaptive Planner
- `3A` D-033 — hard daily capacity / no task debt
- `3B` D-034 — LearningNeed / TaskCandidate / Evidence ayrımı
- `3C` PBR-v0 / D-035 — semantic bands + deterministic rank
- `3D` PRG-v0 / D-036 — hard/soft prerequisites, branch-local blocking
- `3E` VDW-v0 / D-037 — validated diagnostic waiver
- `3F` SRR-v0 / D-038 — current-state re-entry / no absence debt
- `3G` PDT-v0 / D-039 — structured decision trace
- `3H` planner simulation PASS

3H sonucu:
```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

## 4. AŞAMA 4 ilerlemesi

### 4A ✅ Günlük mikro değerlendirme — DMA-v0 / D-040
Ana çıktı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

Canonical davranış:
- daily micro assessment zorunlu günlük quiz/kota değildir,
- fixed soru sayısı, fixed dakika veya günlük yüzde yoktur,
- `practice`, `assess`, `retain`, `diagnose` ayrıdır,
- assessment existing LearningNeed/evidence-gap bağlamından üretilir; ayrı assessment backlog/debt yoktur,
- intents: `checkpoint | mastery_evidence | verification | integration_check`,
- Objective-matched evidence type seçilir; kısa süre uğruna evidence standardı düşmez,
- mastery/verification için varsayılan H0 independent attempt,
- H1–H4 yardım öğrenmeye izin verir fakat positive independent mastery değildir; yardım istemek negative H0 evidence değildir,
- submit sonrası feedback önceki H0 attempt'i geriye dönük kirletmez,
- H3/H4 solution exposure sonrası fresh/unseen recheck gerekir,
- PRG prerequisite fairness assessment öncesi zorunludur,
- invalid/ambiguous/prerequisite-contaminated item mastery credit/penalty üretmez,
- provisional evaluator critical mastery/remediation kararını tek başına belirleyemez,
- tek doğru item automatic mastery değildir,
- tek clean post-mastery failure instant unmastery değildir; GRE/RVR verification hysteresis korunur,
- coding/debugging/transfer Objective'leri gerçek target behavior ile ölçülür,
- multi-Skill task yalnız separately observable/attributable component'lere evidence verir,
- assessment sonucu `Attempt/Artifact → EvidenceEvent → GRE/RVR → weakness/verification/remediation → PRG/Topic → replan` zinciriyle çalışır,
- new remediation günü otomatik uzatmaz,
- technical assessment'ta bilinmeyen English grammar/vocabulary gizli prerequisite olamaz,
- 4B–4E için minimum item/result contract + `assessment.*` reason-code namespace'i tanımlıdır.

## 5. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` 🟡 **Haftalık sınav — AKTİF**
- `4C–4E` ⬜ bekliyor

## 6. 4B'de kesinleştirilecekler

Ana soru:
> Haftalık sınav günlük mikro assessment'tan ne zaman daha geniş bir kanıt sağlamalı ve çok sayıda Skill/Objective'i tek sınavda nasıl adil, güvenilir ve aşırı test yükü yaratmadan temsil etmeli?

Kesinleştirilecek:
- weekly assessment purpose/scope,
- DMA-v0'dan farkı,
- Skill/Objective blueprint ve required/critical coverage,
- evidence modality/family/context diversity,
- current weakness + recent progress + prerequisite risk dengesi,
- fixed bilimsel soru sayısı/puan uydurmadan composition,
- weekly sınav süresi ve günlük capacity ile ilişkisi,
- atomic/bölünebilir sınav davranışı,
- H0/H1–H4, pause, incomplete ve solution exposure,
- invalid/ambiguous/provisional item güvenliği,
- weekly result → GRE/RVR/remediation/PRG/planner etkisi,
- tek kötü haftalık sınava aşırı tepki vermeyen hysteresis,
- 4C monthly assessment için ortak blueprint/result contract.

4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 7. İlk okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/LEARNING_ENGINE_SPEC.md`
8. `docs/LEARNING_BEHAVIOR_RULES.md`
9. `docs/MASTERY_SIGNALS_SPEC.md`
10. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
11. `docs/MASTERY_FORMULA_V0.md`
12. `docs/RETENTION_FORGETTING_SPEC.md`
13. `docs/ADAPTIVE_PLANNER_SPEC.md`
14. `docs/TASK_TAXONOMY_SPEC.md`
15. `docs/PRIORITY_POLICY_SPEC.md`
16. `docs/PREREQUISITE_POLICY_SPEC.md`
17. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
18. `docs/MISSED_DAY_RECOVERY_SPEC.md`
19. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
20. `docs/PLANNER_SIMULATION_SUITE.md`
21. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
22. `docs/ENGLISH_FOUNDATION_RULES.md`
23. `docs/MASTER_PLAN.md`
24. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **4B — Haftalık sınav** için yeni PRE-STEP GitHub refresh yap. Aşama 2 GRE/RVR, Aşama 3 D-033–D-039 + 3H PASS ve 4A DMA-v0/D-040 kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.