# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-24  
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`, D-024, D-027.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra ana çıktı + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN` ve gerekiyorsa `DECISIONS` senkronu zorunludur.

## 1. Ürün
Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan, yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

Ana rota:
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

## 2. Büyük bağlayıcı kurallar
- Curriculum takvim değil prerequisite graph.
- Canonical mastery/prerequisite seviyesi Skill; evidence Objective'e bağlanabilir.
- Coverage/time/streak/task completion mastery değildir.
- Öğretilmemiş prerequisite yüzünden kullanıcı başarısız sayılmaz.
- Coding mastery gerçek user artifact ister.
- Same-item/family tekrarları mastery'yi şişiremez.
- AI yardımı serbest; assisted performance independent mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez.
- English A0 paralel gider; öğretilmemiş grammar gizli prerequisite olamaz.
- Core mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- D-028: uygulama akıcı; bounded/incremental hesap ve async ağır işler.

## 3. Tamamlanan AŞAMA 1
`1A–1D` ✅

## 4. Tamamlanan AŞAMA 2
`2A–2F` ✅

### 2A
`Domain → Module → Topic → Skill → Learning Objective`; Skill canonical. `docs/LEARNING_ENGINE_SPEC.md` — D-021.

### 2B
Topic states: `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`. `docs/TOPIC_STATE_MACHINE.md` — D-023.

### 2C
Mastery evidence taxonomy, direct/corroborating/contextual, coding/debugging/transfer/retention/project. `docs/MASTERY_SIGNALS_SPEC.md` — D-025.

### 2D
H0–H4 assistance/provenance/recheck. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` — D-026.

### 2E — GRE-v0
`docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md` — D-031.

- score yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence,
- bounded recent window max 5, threshold 0.80; heuristic/calibration,
- required/critical hard gates,
- critical coding H0 user artifact, debugging H0 diagnosis/fix,
- first contradiction → verification_due,
- no fixed AI trust or difficulty multiplier.

### 2F — RVR-v0
`docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md` — D-032.

- mastery ve retention ayrı eksen,
- time-based GRE score decay yok,
- retention states: `untracked`, `fresh`, `stable`, `review_due`, `verification_due`, `at_risk`,
- `review_due` forgetting değildir,
- delayed retention Skill'e uygun H0 evidence ister,
- first delayed failure → verification; repeated clean failure → GRE gates recalc/remediation,
- natural reuse yalnız structurally essential + H0 + separately verified + context-diverse ise strong retention evidence,
- automatic cluster refresh yok,
- critical `verification_due` unresolved iken dependent yeni work bekleyebilir,
- long absence backlog dump yok,
- interval defaults heuristic/calibration,
- local bounded/incremental state.

## 5. Güncel kesin konum

**AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla**

- `3A` 🟡 **Günlük kapasite — AKTİF**
- `3B–3H` ⬜ Bekliyor

3A başlamadan yeni PRE-STEP GitHub refresh zorunludur.

## 6. 3A'da kesinleştirilecekler
- kullanıcının günlük gerçek zaman/capacity modeli,
- kısa/normal/yoğun gün profilleri,
- minimum viable study block,
- plan hedef süresinin üstüne remediation/retention nedeniyle kontrolsüz büyümemesi,
- capacity'nin new learning / practice / retention / remediation arasında nasıl harcanacağına temel contract,
- user `bugün 20 dk var` / `bugün 2 saat var` dediğinde replan,
- overflow: ertelenecek işler ve carry-over yerine current-state replan,
- planner'ın süre tahminlerinin belirsizlik toleransı,
- 3B–3G için kullanılacak deterministic capacity output.

3A'da Research AI yalnız learning-session duration / cognitive fatigue / planning trade-off gibi gerçekten dış kanıt gerekiyorsa kullanılmalıdır; keyfi optimum dakika bilimsel gerçek diye kilitlenmemelidir.

## 7. İlk okuma sırası
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
13. `docs/2E_RESEARCH_VALIDATION.md`
14. `docs/RETENTION_FORGETTING_SPEC.md`
15. `docs/2F_RESEARCH_VALIDATION.md`
16. `docs/ENGLISH_FOUNDATION_RULES.md`
17. `docs/MASTER_PLAN.md`
18. `docs/AI_AGENT_WORKFLOW.md`
19. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **3A — Günlük kapasite** için PRE-STEP refresh yap. Aşama 2'nin GRE-v0 + RVR-v0 kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.
