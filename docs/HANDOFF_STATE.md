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
Ana çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md` — 3A bölümü.  
Karar: D-033.

Canonical davranış:
- Explicit daily available minutes planner'ın hard budget'ıdır.
- Capacity source priority: today override → selected profile → scheduled default → normal profile.
- V0 editable presetler `short=30`, `normal=60`, `intensive=90` dakika; science constant değildir.
- V0 `10%` planning reserve ve `10 dk` minimum plannable block engineering heuristic.
- Fixed new-learning/remediation/retention/English percentages yoktur.
- Yeni remediation/retention ortaya çıkınca day length otomatik büyümez; remaining plan yeniden paketlenir.
- Session sırasında user süreyi artırır/azaltırsa yalnız remaining plan replan edilir.
- Unfinished/planned-but-not-started task failure evidence değildir.
- Task sığmazsa safe split → smaller eligible task → defer.
- Deferred işler next-day debt/backlog değildir; current state'ten yeniden candidate generation yapılır.
- Critical task bile explicit user extension olmadan hard budget'ı aşamaz.
- Duration estimates future user pace adaptation ve active-vs-wall-clock ayrımını destekler.
- Capacity resolver deterministic/versioned; LLM süreyi keyfi değiştiremez.

## 5. Güncel kesin konum

**AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla**

- `3A` ✅ Günlük kapasite
- `3B` 🟡 **Görev kategorileri — AKTİF**
- `3C–3H` ⬜ Bekliyor

## 6. 3B'de kesinleştirilecekler
- canonical planner task categories,
- teaching / guided practice / independent practice / assessment / coding / debugging / retention / remediation / English / project ayrımı,
- `task category` ile `evidence type`ın aynı şey olmaması,
- primary purpose / target Skill-Objective / prerequisites / duration / splittable metadata,
- integrated multi-Skill task attribution,
- generated/remediation task provenance,
- 3C priority motorunun kullanacağı canonical `TaskCandidate` contract.

3B için ayrı Research AI ancak task taxonomy konusunda dış pedagojik kanıt gerçekten gerekiyorsa kullanılmalıdır; bu adım büyük ölçüde mevcut 2A–2F davranışlarının planner primitive'ine dönüştürülmesidir.

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
15. `docs/MASTER_PLAN.md`
16. `docs/AI_AGENT_WORKFLOW.md`
17. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **3B — Görev kategorileri** için yeni PRE-STEP GitHub refresh yap. 3A D-033 capacity contract'ını ve Aşama 2 GRE/RVR kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.
