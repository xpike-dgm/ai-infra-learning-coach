# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya, proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?

Bu repo, tek kullanıcı için geliştirilecek kişisel bir mobil öğrenme uygulamasının ürün hafızasını, kararlarını, müfredat yönünü ve geliştirme planını kalıcı tutar.

Uygulamanın amacı kullanıcıya yıllara yayılan sabit bir kurs takvimi göstermek değildir. Uygulama her gün kullanıcının mevcut gerçek bilgi durumunu değerlendirerek **o gün tam olarak ne çalışması gerektiğini** belirlemeli, çalışmayı uygulama içinde yürütmeli, öğrendiğini ölçmeli ve sonuçlara göre sonraki günleri yeniden planlamalıdır.

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Bu nedenle `1095 gün`, yüzde kariyer tamamlandı, yalnızca ders izleme/işaretleme gibi sahte ilerleme göstergeleri ürünün merkezinde olmayacaktır.

## 2. Yeni bir sohbet/agent hangi dosyaları hangi sırayla okumalı?

1. `docs/START_HERE.md` — bu dosya.
2. `docs/PROJECT_MASTER_CONTEXT.md` — ürünün uzun ve ayrıntılı amacı, felsefesi, hedefi, kullanıcı deneyimi ve nedenleri.
3. `docs/HANDOFF_STATE.md` — şu anda proje nerede, neler tamamlandı, açık kararlar ve sıradaki kesin adım.
4. `PROJECT_CONTEXT.md` — orijinal kalıcı proje hafızası ve kariyer rotası.
5. `docs/DECISIONS.md` — kabul edilmiş kalıcı kararlar ve gelecekte değişirse değişiklik gerekçeleri.
6. `docs/MASTER_PLAN.md` — aşama ve alt adım bazlı ana yürütme planı.
7. `docs/PROGRESS_LOG.md` — kronolojik ilerleme günlüğü.
8. Gerekli konuya göre `PRODUCT_VISION.md`, `LEARNING_ENGINE.md`, `CURRICULUM.md`, `ENGLISH_TRACK.md`, `RESEARCH_NOTES.md`.

## 3. Yeni sohbet nasıl devam etmeli?

Yeni sohbet/agent şu kurallara uymalı:

- Önce yukarıdaki dosyaları okumadan projeyi yeniden tasarlamaya çalışma.
- Kullanıcıya daha önce kararlaştırılmış şeyleri tekrar tekrar sordurma.
- Yeni bir kalıcı karar alınırsa `docs/DECISIONS.md` güncellensin.
- Bir plan adımı gerçekten tamamlandıysa `docs/MASTER_PLAN.md` içinde `[x]` yapılıp altına tarihli tamamlanma notu yazılsın.
- Her önemli çalışma oturumunda `docs/PROGRESS_LOG.md` güncellensin.
- Güncel durum değiştiğinde `docs/HANDOFF_STATE.md` de güncellensin.
- Büyük ürün amacı veya ürün felsefesi değişirse `docs/PROJECT_MASTER_CONTEXT.md` güncellensin.

## 4. Şu anki ana kariyer/öğrenme yönü

Temel teknik rota:

**A0 İngilizce + C/Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

Doğrudan CUDA ile başlanmayacaktır. İngilizce de önce bitirilmesi gereken ayrı bir kurs değildir; teknik eğitimle paralel ilerler.

## 5. Uygulamanın temel davranışı

Uygulama:

- Her gün görev listesi üretir.
- Her görevi süre, amaç ve beklenen çıktı ile gösterir.
- Quiz, coding, debugging, açıklama ve gecikmeli tekrar gibi sinyallerle mastery ölçer.
- Bir konu gerçekten öğrenilmediyse ona bağlı konuları açmaz.
- Bağımsız konuları gereksiz yere durdurmaz.
- Haftalık ve aylık sınav sonuçlarına göre gelecekteki planı değiştirir.
- Unutulan konuları tekrar programa sokar.
- Kaçırılan günleri ceza olarak değil yeniden planlama problemi olarak ele alır.
- Çok hızlı öğrenilen konularda doğrulama yaparak ilerlemeyi hızlandırabilir.
- AI kullanımını yasaklamaz; fakat AI yardımıyla yapılan işi kullanıcının gerçekten anlayıp anlamadığını ayrıca ölçer.

## 6. Kapsam sınırı

Bu uygulama kişisel kullanım içindir. Şimdilik şu alanlara zaman harcanmayacaktır:

- çok kullanıcılı SaaS mimarisi
- hesap/rol/organizasyon sistemi
- sosyal özellikler
- ödeme/abonelik
- admin paneli
- gereksiz kurumsal güvenlik katmanları

Veri kaybını önleme, yedekleme ve uygulama kararlılığı yine önemlidir.

## 7. Yeni sohbet için kısa komut

Yeni bir ChatGPT sohbetinde repo bağlandıktan sonra şu şekilde devam edilebilir:

> `xpike-dgm/ai-infra-learning-coach reposundaki docs/START_HERE.md dosyasından başlayarak belirtilen proje hafızası dosyalarını oku. Önceki sohbetin devamı gibi davran. HANDOFF_STATE.md içindeki mevcut durum ve sonraki kesin adımdan devam et. Daha önce kabul edilen kararları yeniden açma; yeni bir karar alınırsa ilgili GitHub dokümanlarını güncelle.`

Bu yöntem, sohbet uzayıp yavaşladığında yeni sohbete geçişte bağlam kaybını minimuma indirmek için tasarlanmıştır.
