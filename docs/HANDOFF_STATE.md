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

## 2E ✅ Mastery formülü v0

Ana spec: `docs/MASTERY_FORMULA_V0.md`  
Karar: D-029.

### 2E canonical model

```text
alpha = 1 + Σ(w_i × q_i)
beta  = 1 + Σ(w_i × (1-q_i))
objective_score = alpha / (alpha + beta)
```

- `q_i`: objective rubric quality `[0,1]`.
- `w_i = role_weight × assistance_weight × provenance_weight`.
- direct `1.00`, corroborating `0.50`, contextual `0`.
- H0/H1/H2/H3/H4 v0: `1.00 / 0.85 / 0.65 / 0.35-or-0 / 0`.
- AI evaluator high-confidence rubric v0 `0.80`; low-confidence/invalid `0` + recheck.
- Operational threshold `0.80`; bilimsel sabit veya “%80 öğrendi” değildir.
- Difficulty score multiplier değildir; critical gate/item eligibility için kullanılır.
- Same family/exact repeat independent evidence sayısını şişiremez.
- Required/critical Objective hard gate; yüksek average kritik açığı gizleyemez.
- Critical Objective: HIGH support, en az 3 independent group, 2 family/context, non-basic evidence; production için en az bir H0 user-authored direct artifact.
- Skill mastered: tüm required/critical Objective gate'leri PASS + skill_score >=0.80 + unresolved recheck yok.
- Bir mastered Objective'te ilk clean independent negative → `verification_due`; anında reset yok.
- Formula versioned ve pilot 17C'de false-positive/false-negative verisine göre kalibre edilecek.
- D-028 için incremental aggregate/sufficient-state yaklaşımı tasarlandı; full-history scan her ekranda zorunlu olmayacak.

### 2E research sonucu

- BKT/knowledge-tracing yaklaşımları değerlendirildi; common `0.95` threshold'un evrensel olmadığı ve bağlama göre kalibrasyon gerektiği görüldü.
- IRT'nin item difficulty/discrimination'ı veriyle kalibre ettiği dikkate alınarak v0'da keyfi difficulty multiplier kullanılmadı.
- V1 için explainable gate + Beta-style evidence accumulator seçildi; BKT/IRT fitting yeterli veri oluşana kadar ertelendi.

---

# 5. Şu anda bulunulan kesin adım

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅
- `2B` ✅
- `2C` ✅
- `2D` ✅
- `2E` ✅
- `2F` 🟡 **Unutma modeli — AKTİF**

## Aktif iş: 2F

Kesinleştirilecek:

- spaced repetition yaklaşımı,
- ilk review interval'leri,
- successful delayed retrieval sonrası interval büyümesi,
- failed review sonrası interval/remediation,
- time/retention risk ve decay davranışı,
- `mastered → weakening → mastered/remediation_required`,
- doğal ileri-topic reuse'un retention evidence sayılması,
- 2E `MasteryEvidenceScore` ile retention state'in birlikte kullanımı,
- tek retention yanlışında otomatik mastery reset olmaması.

**2F için Research AI / dış learning-science araştırması kullanılmalıdır.**

2F başlamadan yeni PRE-STEP GitHub refresh zorunludur.

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

Repo hafızasını okuduktan sonra aktif adımı doğrula ve **2F — Unutma modeli** adımına geç. Önce D-024 PRE-STEP refresh yap; ardından retention/spaced-repetition Research AI turu yürüt. 2A–2E kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.