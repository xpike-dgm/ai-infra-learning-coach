# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya, proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?

Bu repo, tek kullanıcı için geliştirilecek kişisel bir mobil öğrenme uygulamasının ürün hafızasını, kararlarını, müfredat yönünü ve geliştirme planını kalıcı tutar.

Uygulamanın amacı kullanıcıya yıllara yayılan sabit bir kurs takvimi göstermek değildir. Uygulama her gün kullanıcının mevcut gerçek bilgi durumunu değerlendirerek **o gün tam olarak ne çalışması gerektiğini** belirlemeli, çalışmayı uygulama içinde yürütmeli, öğrendiğini ölçmeli ve sonuçlara göre sonraki günleri yeniden planlamalıdır.

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Bu nedenle `1095 gün`, yüzde kariyer tamamlandı, yalnızca ders izleme/işaretleme gibi sahte ilerleme göstergeleri ürünün merkezinde olmayacaktır.

---

## 2. Zorunlu GitHub beyin tazeleme protokolü

**Bağlayıcı kaynak:** `docs/PROJECT_MEMORY_PROTOCOL.md`

> **Hiçbir numaralı proje adımı (`1A`, `2C`, `3A`, `11F` vb.) GitHub beyin tazelemesi yapılmadan başlatılmaz; hiçbir adım gerekli GitHub hafıza dosyaları güncellenmeden tamamlanmış sayılmaz.**

Bu kural yeni sohbetle sınırlı değildir. Aynı sohbet içinde art arda iki adıma geçilirken bile uygulanır.

Her yeni adım öncesi minimum PRE-STEP kontrolü:

1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. başlanacak adımla ilgili en güncel spec/davranış dosyaları

Adım sonunda POST-STEP GitHub sync yapılır. Durum değişikliğini yansıtan `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` kontrol edilir/güncellenir; yeni kalıcı karar varsa `DECISIONS.md`, adımın ana çıktısı varsa ilgili spec dosyası güncellenir.

---

## 3. Yeni bir sohbet/agent hangi dosyaları hangi sırayla okumalı?

1. `docs/START_HERE.md` — bu dosya.
2. `docs/PROJECT_MEMORY_PROTOCOL.md` — her adım için zorunlu PRE-STEP / POST-STEP GitHub hafıza döngüsü.
3. `docs/PROJECT_MASTER_CONTEXT.md` — ürünün uzun ve ayrıntılı amacı, felsefesi ve hedefi.
4. `docs/HANDOFF_STATE.md` — proje nerede, neler tamamlandı, açık kararlar ve sıradaki kesin adım.
5. `docs/EXECUTION_INDEX.md` — sabit `1A / 1B / 2A / ...` adım kodları ve yürütme haritası.
6. `docs/STEP_STATUS.md` — güncel aktif adımın hızlı doğrulaması.
7. `PROJECT_CONTEXT.md` — orijinal kalıcı proje hafızası ve kariyer rotası.
8. `docs/DECISIONS.md` — kabul edilmiş kalıcı kararlar.
9. `docs/PRODUCT_REQUIREMENTS.md`
10. `docs/V1_SCOPE.md`
11. `docs/V1_SUCCESS_CRITERIA.md`
12. `docs/NON_GOALS.md`
13. `docs/LEARNING_ENGINE_SPEC.md`
14. `docs/LEARNING_BEHAVIOR_RULES.md`
15. `docs/TOPIC_STATE_MACHINE.md`
16. `docs/MASTER_PLAN.md`
17. `docs/AI_AGENT_WORKFLOW.md`
18. `docs/PROGRESS_LOG.md`
19. Gerekli konuya göre `PRODUCT_VISION.md`, `LEARNING_ENGINE.md`, `CURRICULUM.md`, `ENGLISH_TRACK.md`, `RESEARCH_NOTES.md` ve yeni alan-spec dosyaları.

---

## 4. Yeni sohbet / ana yönetici nasıl devam etmeli?

- Önce GitHub hafızasını tazelemeden projeyi yeniden tasarlamaya çalışma.
- Kullanıcıya daha önce kararlaştırılmış şeyleri tekrar tekrar sordurma.
- Güncel çalışma konumunu `HANDOFF_STATE.md`, `EXECUTION_INDEX.md` ve `STEP_STATUS.md` üzerinden doğrula.
- Her yeni numaralı adım başlamadan `PROJECT_MEMORY_PROTOCOL.md` PRE-STEP kontrolünü uygula.
- Proje adımlarından söz ederken sabit `1A`, `3C`, `11F` gibi kodları kullan.
- Yeni kalıcı karar alınırsa `docs/DECISIONS.md` güncellensin.
- Bir plan adımı gerçekten tamamlandıysa ilgili spec/çıktı ile birlikte `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE` ve `PROGRESS_LOG` senkronize edilsin.
- `MASTER_PLAN.md` ilgili ayrıntılı checklist/completion notu için gerekiyorsa güncellensin.
- Büyük ürün amacı veya ürün felsefesi değişirse `docs/PROJECT_MASTER_CONTEXT.md` güncellensin.
- Araştırma, implementasyon ve bağımsız test işleri `docs/AI_AGENT_WORKFLOW.md` içindeki rol ayrımına göre dağıtılsın.
- Kodlama AI'ın kendi kodunu başarılı ilan etmesi kritik görevlerde yeterli kabul edilmesin; bağımsız QA sonucu aranmalıdır.

---

## 5. Şu anki ana kariyer/öğrenme yönü

Temel teknik rota:

**A0 İngilizce + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

Doğrudan CUDA ile başlanmayacaktır. İngilizce önce bitirilmesi gereken ayrı bir kurs değildir; teknik eğitimle paralel ilerler.

---

## 6. Uygulamanın temel davranışı

Uygulama:

- Her gün görev listesi üretir.
- Her görevi süre, amaç ve beklenen çıktı ile gösterir.
- Quiz, coding, debugging, açıklama, transfer ve gecikmeli tekrar gibi sinyallerle mastery ölçer.
- Bir konu gerçekten öğrenilmediyse ona bağlı konuları açmaz.
- Bağımsız konuları gereksiz yere durdurmaz.
- Haftalık ve aylık sınav sonuçlarına göre gelecekteki planı değiştirir.
- Unutulan konuları tekrar programa sokar.
- Kaçırılan günleri ceza olarak değil yeniden planlama problemi olarak ele alır.
- Çok hızlı öğrenilen konularda doğrulama yaparak ilerlemeyi hızlandırabilir.
- AI kullanımını yasaklamaz; fakat AI yardımıyla yapılan işi kullanıcının gerçekten anlayıp anlamadığını ayrıca ölçer.

---

## 7. Kapsam sınırı

Bu uygulama kişisel kullanım içindir. Şimdilik şu alanlara zaman harcanmayacaktır:

- çok kullanıcılı SaaS mimarisi
- hesap/rol/organizasyon sistemi
- sosyal özellikler
- ödeme/abonelik
- admin paneli
- gereksiz kurumsal güvenlik katmanları

Veri kaybını önleme, yedekleme ve uygulama kararlılığı yine önemlidir.

---

## 8. Güncel çalışma konumu

**Aktif aşama:** AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla

Tamamlanan:
- `2A` ✅ Bilgi birimleri
- `2B` ✅ Topic durumları

**Sıradaki/aktif kesin adım:** **`2C — Mastery sinyalleri`**

2C'ye başlanmadan önce `PROJECT_MEMORY_PROTOCOL.md` uyarınca tekrar GitHub PRE-STEP refresh yapılmalıdır.

---

## 9. Yeni sohbet için kısa komut

> `xpike-dgm/ai-infra-learning-coach reposundaki docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md dosyalarından başlayarak belirtilen proje hafızasını oku. Önceki sohbetin devamı gibi davran. Her numaralı adımın başında PRE-STEP GitHub beyin tazelemesi, sonunda POST-STEP GitHub sync yap. HANDOFF_STATE.md, EXECUTION_INDEX.md ve STEP_STATUS.md içindeki mevcut adım kodundan devam et. Daha önce kabul edilen kararları yeniden açma; yeni kararları ve tamamlanan adımları ilgili GitHub dokümanlarına işle. Araştırma/kodlama/test işleri için AI_AGENT_WORKFLOW.md protokolünü uygula.`