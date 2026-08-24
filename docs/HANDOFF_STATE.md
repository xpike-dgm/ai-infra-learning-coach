# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

**Son güncelleme:** 2026-08-24  
Repo: `xpike-dgm/ai-infra-learning-coach`

---

# 0. Zorunlu çalışma protokolü

Bağlayıcı kaynak: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-024 / D-027.

> **Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra ana çıktı + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN` ve gerekiyorsa `DECISIONS` senkronu zorunludur.**

Aynı sohbet içinde bile yeni adımda refresh tekrarlanır.

---

# 1. Ana ürün

Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük olarak yöneten, uygulama içinde öğreten/uygulatan, yalnız kanıtlanmış öğrenmeyi ilerleme sayan ve mastery/retention/prerequisite sonuçlarıyla gelecek planı yeniden oluşturan kişisel adaptif Android öğrenme koçu.

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Kariyer yönü:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

---

# 2. Büyük bağlayıcı kurallar

- Curriculum takvim değil prerequisite ilişkili knowledge graph.
- Canonical mastery/prerequisite seviyesi Skill; atomik evidence Learning Objective'e bağlanabilir.
- Coverage mastery değildir.
- Tek quiz/task completion/streak/time mastery değildir.
- Kritik Skill için çok kaynaklı, objective-fit, bağımsız evidence gerekir.
- Coding mastery gerçek kullanıcı coding artifact'ı ister.
- Bilinmeyen prerequisite içeren soru kullanıcıyı cezalandırmaz.
- Aynı exact/familya tekrarları mastery'yi şişiremez.
- AI yardımı serbesttir ama assisted performance independent mastery ile aynı değildir.
- AI-generated/copied code production mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez; doğrulama gerekir.
- English A0 teknik eğitimle paralel ilerler; öğretilmemiş grammar gizli prerequisite olamaz.
- Çekirdek mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- Uygulama performansı D-028 gereği first-class requirement'tır; UI thread ağır iş/AI/network/code execution ile bloke edilmez.

---

# 3. Tamamlanan Aşama 1

`1A–1D` ✅ — ürün amacı, V1 scope, success criteria, non-goals.

---

# 4. Aşama 2 tamamlanan adımlar

## 2A ✅ Bilgi birimleri

`Domain → Module → Topic → Skill → Learning Objective`  
Ana spec: `docs/LEARNING_ENGINE_SPEC.md`  
Karar: D-021.

## 2B ✅ Topic state machine

`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`  
Ana spec: `docs/TOPIC_STATE_MACHINE.md`  
Karar: D-023.

## 2C ✅ Mastery sinyalleri

Direct/corroborating/contextual; recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project evidence; validity/dedup/quality.  
Ana spec: `docs/MASTERY_SIGNALS_SPEC.md`  
Karar: D-025.

## 2D ✅ AI / ipucu etkisi

H0–H4, timing, artifact origin, independent/assisted/practice-only/recheck, solution exposure ve fresh recheck.  
Ana spec: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`  
Karar: D-026.

## 2E 🟡 Mastery formülü v0 — RESEARCH AI DOĞRULAMASI BEKLİYOR

Ana spec: `docs/MASTERY_FORMULA_V0.md`  
Durum: **candidate/draft**.

### Düzeltme

2E'nin ilk candidate formülü ana yöneticinin kendi web/dış araştırmasıyla hazırlandı. Proje planında 2E için ayrı Research AI kullanılması gerektiği halde ayrı agent turu yapılmadan adım yanlışlıkla tamamlandı olarak işaretlendi. Bu nedenle 2E yeniden açıldı; 2F beklemeye alındı.

### Candidate model

```text
alpha = 1 + Σ(w_i × q_i)
beta  = 1 + Σ(w_i × (1-q_i))
objective_score = alpha / (alpha + beta)
```

Şu değerler **candidate** ve Research AI tarafından sorgulanacak:

- direct `1.00`, corroborating `0.50`, contextual `0`.
- H0/H1/H2/H3/H4 `1.00 / 0.85 / 0.65 / 0.35-or-0 / 0`.
- AI evaluator high-confidence candidate `0.80`.
- operational mastery threshold candidate `0.80`.
- standard/critical Objective minimum independent evidence gates.
- critical production için en az bir H0 user-authored direct artifact.
- single clean negative sonrası `verification_due` hysteresis.
- difficulty'nin numeric multiplier değil gate/item-eligibility olarak kullanılması.

Bu değerler şu anda bağlayıcı final 2E kararı değildir.

---

# 5. Şu anda bulunulan kesin adım

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅
- `2B` ✅
- `2C` ✅
- `2D` ✅
- `2E` 🟡 **Mastery formülü v0 / Research AI doğrulaması — AKTİF**
- `2F` ⬜ Bekliyor

## Aktif iş: 2E Research AI validation

Research AI en az şu başlıkları incelemeli:

- Beta-style accumulator uygun mu; daha iyi explainable alternatif var mı?
- BKT / IRT / AFM / PFA / mastery-learning modelleriyle karşılaştırma.
- `0.80` threshold candidate değerinin riskleri.
- direct/corroborating ve H0–H4 katsayılarının kanıt temeli.
- minimum independent/diverse evidence gate'leri.
- critical production için H0 artifact şartı.
- negative evidence + `verification_due` davranışı.
- AI evaluator provenance weight yaklaşımı.
- false-positive / false-negative mastery riskleri.
- V1'de az kullanıcı datasıyla en güvenli yaklaşım.
- hangi parametrelerin yalnız config/pilot calibration olarak kalması gerektiği.

Research raporu ana yöneticinin kendi başına yaptığı web araştırmasının yerine geçen bağımsız doğrulama girdisi olacak; otomatik ürün kararı olmayacak.

2E ancak rapor değerlendirildikten, candidate spec gerekirse revize edildikten ve POST-STEP sync yapıldıktan sonra kapanır.

---

# 6. İlk okuma sırası

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
17. `docs/ENGLISH_FOUNDATION_RULES.md`
18. `docs/MASTER_PLAN.md`
19. `docs/AI_AGENT_WORKFLOW.md`
20. `docs/PROGRESS_LOG.md`

---

# 7. Yeni sohbetin yapacağı ilk iş

Repo hafızasını okuduktan sonra aktif adımı doğrula ve **2E Research AI doğrulaması**ndan devam et. Research raporu gelmeden 2F'ye geçme. 2A–2D kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.