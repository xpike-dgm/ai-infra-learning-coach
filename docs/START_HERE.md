# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?

Bu repo, tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, müfredat yönünü ve geliştirme planını kalıcı tutar.

Uygulamanın amacı sabit bir kurs takvimi göstermek değildir. Sistem kullanıcının mevcut gerçek bilgi durumuna göre **o gün ne çalışması gerektiğini** belirlemeli, uygulama içinde öğretmeli/uygulatmalı, öğrendiğini evidence ile ölçmeli ve sonuçlara göre sonraki planı yeniden oluşturmalıdır.

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

---

## 2. Zorunlu GitHub beyin tazeleme protokolü

**Bağlayıcı kaynak:** `docs/PROJECT_MEMORY_PROTOCOL.md`

> **Hiçbir numaralı proje adımı GitHub beyin tazelemesi yapılmadan başlatılmaz; hiçbir adım gerekli GitHub hafıza dosyaları ve `MASTER_PLAN.md` ilerleme kaydı senkronize edilmeden tamamlanmış sayılmaz.**

Bu kural yeni sohbetle sınırlı değildir. Aynı sohbet içinde art arda iki adıma geçilirken bile uygulanır.

Her yeni adım öncesi minimum PRE-STEP kontrolü:

1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. başlanacak adımla ilgili en güncel spec/davranış dosyaları

Adım sonunda POST-STEP GitHub sync yapılır. `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` ve `MASTER_PLAN` kontrol edilir/güncellenir; yeni kalıcı karar varsa `DECISIONS.md`, ana çıktı varsa ilgili spec güncellenir.

---

## 3. Yeni sohbet/agent hangi dosyaları hangi sırayla okumalı?

1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/PROJECT_MASTER_CONTEXT.md`
4. `docs/HANDOFF_STATE.md`
5. `docs/EXECUTION_INDEX.md`
6. `docs/STEP_STATUS.md`
7. `PROJECT_CONTEXT.md`
8. `docs/DECISIONS.md`
9. `docs/PRODUCT_REQUIREMENTS.md`
10. `docs/V1_SCOPE.md`
11. `docs/V1_SUCCESS_CRITERIA.md`
12. `docs/NON_GOALS.md`
13. `docs/LEARNING_ENGINE_SPEC.md`
14. `docs/LEARNING_BEHAVIOR_RULES.md`
15. `docs/TOPIC_STATE_MACHINE.md`
16. `docs/MASTERY_SIGNALS_SPEC.md`
17. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
18. `docs/ENGLISH_FOUNDATION_RULES.md`
19. `docs/MASTER_PLAN.md`
20. `docs/AI_AGENT_WORKFLOW.md`
21. `docs/PROGRESS_LOG.md`
22. Gerektiğinde `PRODUCT_VISION.md`, `LEARNING_ENGINE.md`, `CURRICULUM.md`, `ENGLISH_TRACK.md`, `RESEARCH_NOTES.md` ve yeni alan-spec dosyaları.

---

## 4. Yeni sohbet / ana yönetici nasıl devam etmeli?

- Önce GitHub hafızasını tazelemeden projeyi yeniden tasarlama.
- Kullanıcıya daha önce kararlaştırılmış şeyleri tekrar sordurma.
- Aktif adımı `HANDOFF_STATE.md`, `EXECUTION_INDEX.md`, `STEP_STATUS.md` ve `MASTER_PLAN.md` üzerinden doğrula.
- Her yeni numaralı adım başlamadan `PROJECT_MEMORY_PROTOCOL.md` PRE-STEP kontrolünü uygula.
- Sabit `1A`, `2E`, `3C`, `11F` gibi adım kodlarını kullan.
- Yeni kalıcı karar varsa `DECISIONS.md` güncelle.
- Bir adım gerçekten tamamlandıysa ana çıktı/spec ile birlikte `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` ve `MASTER_PLAN` senkronize et.
- Araştırma, implementasyon ve bağımsız test işleri `AI_AGENT_WORKFLOW.md` rol ayrımına göre dağıtılsın.
- Kodlama AI'ın kendi kodunu başarılı ilan etmesi kritik görevlerde yeterli kabul edilmesin.

---

## 5. Ana kariyer/öğrenme yönü

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

Doğrudan CUDA ile başlanmayacaktır. İngilizce önce bitirilmesi gereken ayrı bir kurs değildir; teknik eğitimle paralel ilerler.

---

## 6. Uygulamanın temel öğrenme davranışı

Uygulama:

- her gün adaptif görev planı üretir,
- uygulama içinde öğretir ve uygulatır,
- evidence'ı Learning Objective → Skill seviyesinde toplar,
- quiz/coding/debugging/explanation/transfer/retention/project gibi farklı sinyalleri ayırır,
- tek kolay quiz veya task completion ile mastery vermez,
- coding mastery için gerçek kullanıcı kod artifact'ı ister,
- bilinmeyen prerequisite içeren sorudan kullanıcıyı başarısız saymaz,
- aynı exact soruyu hemen tekrar ederek ezberi mastery gibi saymaz,
- kritik eksikte yalnız bağımlı dalı bekletir,
- retention ile eski Skill'leri tekrar doğrular,
- remediation'ı günlük kapasite içine replan eder,
- AI kullanımına izin verir fakat assisted performance'ı independent mastery evidence ile eşit saymaz,
- AI full solution/code verirse fresh/unseen independent recheck ister,
- compiler/docs/test-runner kullanımını objective-specific tool policy ile yorumlar,
- İngilizcede henüz öğretilmemiş grammar/function-word yapılarından serbest üretim beklemez.

---

## 7. Kapsam sınırı

Bu uygulama kişisel kullanım içindir. Şimdilik çok kullanıcılı SaaS, hesap/rol/organizasyon, sosyal özellikler, ödeme/abonelik, admin paneli ve gereksiz kurumsal altyapı kapsam dışıdır.

---

## 8. Güncel çalışma konumu

**Aktif aşama:** AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla

Tamamlanan:
- `2A` ✅ Bilgi birimleri
- `2B` ✅ Topic durumları
- `2C` ✅ Mastery sinyalleri
- `2D` ✅ AI / ipucu etkisi

**Sıradaki/aktif kesin adım:** **`2E — Mastery formülü v0`**

2E'ye başlanmadan önce `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeniden GitHub PRE-STEP refresh yapılmalı ve `AI_AGENT_WORKFLOW.md` uyarınca **Research AI** kullanılmalıdır.

---

## 9. Yeni sohbet için kısa komut

> `xpike-dgm/ai-infra-learning-coach reposundaki docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md dosyalarından başlayarak belirtilen proje hafızasını oku. Önceki sohbetin devamı gibi davran. Her numaralı adımın başında PRE-STEP GitHub beyin tazelemesi, sonunda POST-STEP GitHub + MASTER_PLAN sync yap. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md içindeki mevcut adım kodundan devam et. Özellikle LEARNING_BEHAVIOR_RULES.md, TOPIC_STATE_MACHINE.md, MASTERY_SIGNALS_SPEC.md, AI_ASSISTANCE_EVIDENCE_SPEC.md ve ENGLISH_FOUNDATION_RULES.md içindeki bağlayıcı kararları koru. Daha önce kabul edilen kararları yeniden açma; yeni kararları ve tamamlanan adımları ilgili GitHub dokümanlarına işle. Araştırma/kodlama/test işleri için AI_AGENT_WORKFLOW.md protokolünü uygula.`