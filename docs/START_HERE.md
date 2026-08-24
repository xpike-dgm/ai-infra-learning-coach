# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?

Bu repo, tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, müfredat yönünü ve geliştirme planını kalıcı tutar.

Uygulamanın amacı sabit kurs takvimi göstermek değildir. Sistem kullanıcının gerçek bilgi durumuna göre **o gün ne çalışması gerektiğini** belirler, uygulama içinde öğretir/uygulatır, evidence ile ölçer ve sonuçlara göre sonraki planı yeniden oluşturur.

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

---

## 2. Zorunlu GitHub beyin tazeleme protokolü

**Bağlayıcı kaynak:** `docs/PROJECT_MEMORY_PROTOCOL.md`

> **Hiçbir numaralı adım PRE-STEP GitHub refresh yapılmadan başlatılmaz; hiçbir adım gerekli GitHub hafıza dosyaları ve `MASTER_PLAN.md` senkronize edilmeden tamamlanmış sayılmaz.**

Minimum PRE-STEP:

1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP: ana spec + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN`; yeni kalıcı karar varsa `DECISIONS.md`.

---

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
17. `docs/ENGLISH_FOUNDATION_RULES.md`
18. `docs/MASTER_PLAN.md`
19. `docs/AI_AGENT_WORKFLOW.md`
20. `docs/PROGRESS_LOG.md`
21. Gerektiğinde diğer alan-spec ve research dosyaları.

---

## 4. Ana yönetici davranışı

- Repo hafızasını okumadan projeyi yeniden tasarlama.
- Kullanıcıya daha önce kararlaştırılmış şeyleri tekrar sordurma.
- Aktif adımı handoff/index/status/master plan ile doğrula.
- Her numaralı adımda PRE/POST sync uygula.
- Yeni kalıcı kararları `DECISIONS.md` içine yaz.
- Research/Coding/Test rol ayrımını `AI_AGENT_WORKFLOW.md` ile koru.
- Coding AI'ın kendi test raporu kritik işlerde tek başına kabul değildir.

---

## 5. Ana kariyer/öğrenme yönü

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

English teknik eğitimle paralel ilerler; doğrudan CUDA ile başlanmaz.

---

## 6. Güncel mastery omurgası

- Canonical mastery Skill seviyesinde; evidence Learning Objective'e bağlanabilir.
- Evidence: recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project.
- Coverage/time/streak/task completion mastery değildir.
- AI assistance H0–H4; assisted performance independent mastery ile eşit değildir.
- AI-generated/copied code production mastery değildir.
- `MASTERY_FORMULA_V0.md` score + hard-gate modeli:
  - `alpha = 1 + Σ(wq)`
  - `beta = 1 + Σ(w(1-q))`
  - `objective_score = alpha/(alpha+beta)`
  - operational threshold `0.80`, probability değildir.
  - direct `1.0`, corroborating `0.5`.
  - H0/H1/H2/H3/H4 v0 `1.00/0.85/0.65/0.35-or-0/0`.
  - required/critical Objective hard gates.
  - critical production için H0 user-authored direct artifact.
  - same-family repeat mastery'yi şişiremez.
  - tek post-mastery yanlış → `verification_due`, anında reset yok.
- Formula constants versioned ve pilotta kalibre edilebilir.
- D-028: performans first-class requirement; mastery/planner incremental hesaplanabilir tasarlanmalı.

---

## 7. Güncel çalışma konumu

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅ Bilgi birimleri
- `2B` ✅ Topic durumları
- `2C` ✅ Mastery sinyalleri
- `2D` ✅ AI/ipucu etkisi
- `2E` ✅ Mastery formülü v0
- `2F` 🟡 **Unutma modeli — AKTİF**

2F başlamadan önce PRE-STEP GitHub refresh ve retention/spaced-repetition için Research AI / dış araştırma yapılmalıdır.

---

## 8. 2F'de yapılacaklar

- spaced repetition yaklaşımı,
- review interval'leri,
- successful/failed delayed retrieval davranışı,
- time-based retention risk,
- `mastered → weakening → mastered/remediation_required`,
- doğal reuse'un retention evidence sayılması,
- 2E score ile retention state entegrasyonu,
- tek retention hatasında otomatik reset olmaması.

---

## 9. Yeni sohbet için kısa komut

> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Özellikle LEARNING_BEHAVIOR_RULES.md, TOPIC_STATE_MACHINE.md, MASTERY_SIGNALS_SPEC.md, AI_ASSISTANCE_EVIDENCE_SPEC.md ve MASTERY_FORMULA_V0.md içindeki bağlayıcı kararları koru. Şu an aktif adım 2F — Unutma modeli. Önce retention/spaced-repetition Research AI turu yap, sonra canonical spec üret.`