# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?

Tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, curriculum yönünü ve geliştirme planını kalıcı tutar.

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Sistem sabit kurs takvimi değil; gerçek Skill state, prerequisite, retention ve evidence'a göre günlük plan üretir.

## 2. Zorunlu GitHub beyin tazeleme protokolü

Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

> Hiçbir numaralı adım PRE-STEP GitHub refresh yapılmadan başlatılmaz; hiçbir adım ana çıktı ve canonical state dosyaları + `MASTER_PLAN.md` senkronize edilmeden tamamlanmış sayılmaz.

Minimum PRE-STEP:

1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP: ana spec + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN`; yeni karar varsa `DECISIONS`.

## 3. Yeni sohbet/agent okuma sırası

1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT_REQUIREMENTS.md`
8. `docs/V1_SCOPE.md`
9. `docs/V1_SUCCESS_CRITERIA.md`
10. `docs/NON_GOALS.md`
11. `docs/LEARNING_ENGINE_SPEC.md`
12. `docs/LEARNING_BEHAVIOR_RULES.md`
13. `docs/TOPIC_STATE_MACHINE.md`
14. `docs/MASTERY_SIGNALS_SPEC.md`
15. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
16. `docs/MASTERY_FORMULA_V0.md`
17. `docs/2E_RESEARCH_VALIDATION.md`
18. `docs/ENGLISH_FOUNDATION_RULES.md`
19. `docs/MASTER_PLAN.md`
20. `docs/AI_AGENT_WORKFLOW.md`
21. `docs/PROGRESS_LOG.md`

## 4. Ana kariyer/öğrenme yönü

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

English teknik eğitimle paralel ilerler; doğrudan CUDA ile başlanmaz.

## 5. Güncel mastery omurgası

- Canonical mastery Skill seviyesinde; evidence Learning Objective'e bağlanır.
- Coverage/time/streak/task completion mastery değildir.
- Coding mastery gerçek kullanıcı artifact'ı ister.
- AI assistance H0–H4; assisted performance independent mastery ile eşit değildir.
- Same-family/near-duplicate evidence bağımsızlığı şişiremez.
- Tek clean post-mastery yanlış instant reset değildir; fresh verification gerekir.

### Final 2E — `GRE-v0 — Gated Recent Evidence`

İlk Beta-style candidate ayrı Research AI doğrulaması sonrası kaldırıldı. Final karar D-031.

- Mastery score'a yalnız `valid + prerequisite-valid + H0 + direct + verified + independent` evidence group girer.
- H1–H4 formative/remediation/recheck sinyalidir; positive independent mastery score'a girmez.
- Corroborating evidence direct gate'i ikame etmez.
- Correlated items `dependency_group_id/testlet_id` altında tek group olur; `variant_family_id` diversity için kullanılır.
- Objective score:

```text
W_o = son en fazla 5 eligible independent H0 direct evidence group
recent_direct_score = mean(q_g for g in W_o)
```

- V0 threshold `0.80`, window max `5`; ikisi de engineering heuristic ve pilotta kalibre edilir.
- Standard default: 2 independent group + default 2 family/context.
- Critical default: 3 independent group + 2 family/context + non-basic/objective-specific gate.
- Critical coding: H0 user-authored coding artifact.
- Critical debugging: H0 diagnosis/fix evidence.
- Skill mastered = tüm required/critical Objective gates PASS + unresolved critical recheck yok.
- Difficulty numeric multiplier değildir.
- AI evaluator fixed trust multiplier yok: `verified | provisional | invalid`.
- D-028 gereği bounded/incremental implementation yönü korunur.

Ayrıntı:
- `docs/MASTERY_FORMULA_V0.md`
- `docs/2E_RESEARCH_VALIDATION.md`

## 6. Güncel çalışma konumu

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅
- `2B` ✅
- `2C` ✅
- `2D` ✅
- `2E` ✅
- `2F` 🟡 **Unutma modeli — AKTİF**

## 7. 2F'de yapılacaklar

2F başlamadan yeni PRE-STEP GitHub refresh + ayrı Research AI retention/spaced-repetition turu zorunlu.

Araştırılacak/tasarlanacak:

- spaced repetition model yaklaşımı,
- review interval'leri,
- successful/failed delayed retrieval,
- time-based retention risk/decay,
- `mastered → weakening → mastered/remediation_required`,
- natural reuse'un retention evidence sayılması,
- GRE-v0 current mastery ile retention state entegrasyonu.

Research AI, Half-Life Regression'ı tek doğru varsaymayacak; SM-2/FSRS/HLR/ACT-R ve uygun diğer yaklaşımları karşılaştıracak.

## 8. Yeni sohbet için kısa komut

> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. LEARNING_BEHAVIOR_RULES.md, TOPIC_STATE_MACHINE.md, MASTERY_SIGNALS_SPEC.md, AI_ASSISTANCE_EVIDENCE_SPEC.md, MASTERY_FORMULA_V0.md ve 2E_RESEARCH_VALIDATION.md kararlarını koru. Şu an aktif adım 2F — Unutma modeli. Önce retention/spaced-repetition için ayrı Research AI turu yap; raporu otomatik kabul etmeden canonical 2F spec'e sentezle.`
