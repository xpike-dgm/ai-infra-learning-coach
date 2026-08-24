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
- AI-generated/copied code production mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez.
- English A0 paralel gider; öğretilmemiş grammar gizli prerequisite olamaz.
- Core mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- D-028: uygulama akıcı olmalı; bounded/incremental hesap ve async ağır işler.

## 3. Tamamlanan adımlar

- `1A–1D` ✅ Ürün çerçevesi
- `2A` ✅ Bilgi birimleri — `docs/LEARNING_ENGINE_SPEC.md`
- `2B` ✅ Topic state — `docs/TOPIC_STATE_MACHINE.md`
- `2C` ✅ Mastery signals — `docs/MASTERY_SIGNALS_SPEC.md`
- `2D` ✅ AI/hint — `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `2E` ✅ Mastery Formula v0 — `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md`

## 4. 2E final — GRE-v0

İlk Beta-style candidate ayrı Research AI doğrulaması sonrası finalden çıkarıldı. D-029 artık historical candidate; final karar D-031.

### Canonical final davranış

- Mastery score'a yalnız `valid + prerequisite-valid + H0 + direct + verified + independent` evidence group girer.
- H1–H4 formative/remediation/recheck sinyalidir; positive independent mastery score'a girmez.
- Corroborating evidence direct evidence'ı numeric accumulation ile ikame etmez.
- `dependency_group_id/testlet_id` correlated item'ları tek group yapar; `variant_family_id` diversity için kullanılır.
- Objective score:

```text
W_o = son en fazla 5 eligible independent H0 direct evidence group
recent_direct_score = mean(q_g for g in W_o)
```

- `threshold = 0.80`, `window max = 5` cold-start engineering heuristics; probability veya `% learned` değildir.
- Standard default: score >=0.80 + en az 2 independent group + default 2 family/context + required direct type + no recheck.
- Critical default: en az 3 independent group + 2 family/context + non-basic/objective-specific gate.
- Critical production → H0 user-authored coding artifact.
- Critical debugging → H0 diagnosis/fix evidence.
- Skill mastered = tüm required Objective PASS + tüm critical Objective PASS + unresolved critical recheck yok. Compensatory high average yok.
- İlk clean post-mastery H0 direct negative → `verification_due` + fresh/unseen recheck.
- Difficulty numeric multiplier değildir.
- AI evaluator fixed numeric weight kaldırıldı: `verified | provisional | invalid`; provisional LLM grade critical mastery'yi tek başına geçiremez.
- Bounded sufficient-state D-028 performans gereksinimine uygundur.

Research ayrımı: `0.80`, window `5`, minimum group/family defaultları 17C pilotunda kalibre edilecek.

## 5. Şu anda aktif adım

**AŞAMA 2 — 2F Unutma modeli — AKTİF**

2F başlamadan yeni PRE-STEP refresh + ayrı Research AI turu zorunlu.

Kesinleştirilecek:

- spaced repetition yaklaşımı,
- review interval başlangıcı/büyümesi,
- successful/failed delayed retrieval,
- time-based retention risk / forgetting,
- `mastered → weakening → mastered/remediation_required`,
- natural reuse'un retention evidence sayılması,
- GRE-v0 current mastery ile retention state'in birlikte çalışması,
- tek delayed failure'da instant mastery reset olmaması.

Research raporundaki Half-Life Regression önerisi yalnız bir adaydır; 2F Research AI bunu SM-2/FSRS/HLR/ACT-R vb. uygun alternatiflerle karşılaştırmalıdır.

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
13. `docs/2E_RESEARCH_VALIDATION.md`
14. `docs/ENGLISH_FOUNDATION_RULES.md`
15. `docs/MASTER_PLAN.md`
16. `docs/AI_AGENT_WORKFLOW.md`
17. `docs/PROGRESS_LOG.md`

## 7. Yeni sohbetin ilk işi

Aktif adımı repo üzerinden doğrula ve **2F — Unutma modeli** için PRE-STEP refresh yap. Sonra ayrı Research AI retention/spaced-repetition raporu al; raporu otomatik kabul etme, ürüne sentezle.
