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

## 2. Bağlayıcı ana kurallar
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

## 3. Tamamlanan ana aşamalar
- **AŞAMA 1** `1A–1D` ✅
- **AŞAMA 2** `2A–2F` ✅

Aşama 2 canonical omurgası:
- `GRE-v0 — Gated Recent Evidence`, `docs/MASTERY_FORMULA_V0.md`, D-031.
- `RVR-v0 — Retention Verification & Risk`, `docs/RETENTION_FORGETTING_SPEC.md`, D-032.

## 4. AŞAMA 3 ilerlemesi

### 3A ✅ Günlük kapasite
Ana çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md`. Karar D-033.

- explicit daily time hard budget,
- 30/60/90 editable preset; 10% reserve; 10 dk min block = heuristic,
- no fixed category percentages,
- remediation/retention day length'i otomatik büyütmez,
- time override remaining-plan replan,
- split → smaller alternative → defer,
- deferred task next-day debt değildir,
- deterministic/versioned capacity.

### 3B ✅ Görev kategorileri / TaskCandidate
Ana çıktı: `docs/TASK_TAXONOMY_SPEC.md`. Karar D-034.

Canonical model:

```text
State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent
```

- Kalıcı olan eski task ID değil unresolved `LearningNeed`'dir.
- Deferred candidate failure değildir ve yarına homework debt olarak taşınmaz; need açık ise fresh candidate üretilir.
- `primary_purpose`: `teach | practice | assess | remediate | retain | diagnose | reinforce`.
- `activity_kind` ayrı: explanation/worked example/recall/code reading/coding/debugging/hands-on system/transfer/project/language vb.
- English purpose değil curriculum track; coding/debugging/project purpose değil activity'dir.
- Task category evidence değildir; evidence Attempt/Artifact sonrası GRE/RVR ile oluşur.
- `independence_mode` ayrı; guided practice H0 sayılmaz.
- Multi-Skill integrated task component evidence için structural essentiality + separate observability/attribution gerekir; global project success otomatik component mastery değildir.
- provenance/validation + variant/dependency + prerequisite/tools metadata bulunur.
- 3A duration/splittable/checkpoint/atomic evidence boundary TaskCandidate'a dahildir.
- `paused_progress` gerçek checkpoint'i koruyabilir; `deferred_candidate` yalnız ephemeral candidate'dır.
- 3B fixed priority weight belirlemedi; gerekli ham sinyalleri 3C'ye verdi.

## 5. Güncel kesin konum

**AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla**

- `3A` ✅ Günlük kapasite
- `3B` ✅ Görev kategorileri
- `3C` 🟡 **Öncelik puanı — AKTİF**
- `3D–3H` ⬜ Bekliyor

## 6. 3C'de kesinleştirilecekler

Kullanıcı örneği: bugün açık LearningNeed/TaskCandidate toplamı 80 dk, capacity 50 dk ise **hangi 50 dk seçilecek ve kalan ihtiyaçlar nasıl starvation yaşamadan açık kalacak?**

3C tasarlayacak:
- critical prerequisite / verification_due / remediation / retention / continuing/new learning / English priority ilişkisi,
- urgency vs importance,
- due/overdue ama negative evidence olmayan retention'ın doğru yeri,
- duration/capacity-aware seçim,
- unresolved/deferred LearningNeed starvation guard,
- aynı gün birden fazla critical işte tie-break,
- fixed category percentages olmadan balanced progress,
- deterministic score veya decision hierarchy,
- 3G'nin açıklayacağı priority reason inputs.

3C'de keyfi `remediation = 100 puan` gibi sahte hassasiyet kullanılmamalı. Gerekirse Research AI yalnız adaptive scheduling / priority trade-off'larında gerçek dış kanıt gerektiğinde kullanılır.

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
13. `docs/RETENTION_FORGETTING_SPEC.md`
14. `docs/ADAPTIVE_PLANNER_SPEC.md`
15. `docs/TASK_TAXONOMY_SPEC.md`
16. `docs/ENGLISH_FOUNDATION_RULES.md`
17. `docs/MASTER_PLAN.md`
18. `docs/AI_AGENT_WORKFLOW.md`
19. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **3C — Öncelik puanı** için yeni PRE-STEP GitHub refresh yap. 3A D-033, 3B D-034 ve Aşama 2 GRE/RVR kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.
