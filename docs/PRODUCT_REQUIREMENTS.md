# Product Requirements — AI Infra Learning Coach

Bu belge ürün gereksinimlerinin kilitlendiği ana kayıttır. `docs/EXECUTION_INDEX.md` içindeki yürütme planıyla birlikte okunur.

## Durum

- **1A — Ana ürün amacı:** TAMAMLANDI
- **Güncel ürün hedefi:** 2026-08-24 kapsam genişletmesiyle professional-readiness odaklı 4+ yıllık esnek curriculum horizon'ı
- **Bağlayıcı ayrıntı:** `docs/PROFESSIONAL_READINESS_TARGET.md`

---

# 1A — Ana Ürün Amacı — TAMAMLANDI

## Tek cümlelik ürün amacı

**AI Infra Learning Coach; sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten, her gün ne öğrenmesi ve ne uygulaması gerektiğini mevcut bilgi durumuna göre belirleyen, yalnızca gerçekten öğrenildiği ölçümlerle kanıtlanan becerileri ilerleme kabul eden ve sınav, tekrar, hata analizi ve performans sonuçlarına göre gelecekteki çalışma planını otomatik yeniden düzenleyen kişisel adaptif mobil öğrenme koçudur.**

## Kullanıcının günlük aldığı temel değer

Kullanıcı uygulamayı açtığında uzun bir kurs listesi, aylara bölünmüş statik yol haritası veya “bugün ne çalışsam?” kararıyla karşılaşmamalıdır. Uygulama o gün için ayrılabilecek süreyi, daha önceki görev ve sınav performansını, unutulma riski taşıyan konuları, prerequisite durumlarını, teknik İngilizce gelişimini ve mevcut mastery seviyelerini dikkate alarak uygulanabilir bir günlük çalışma planı sunmalıdır.

Kullanıcının temel deneyimi şu olmalıdır:

> **“Uygulamayı açıyorum; bugün ne yapmam gerektiğini düşünüp planlamıyorum. Sistem bana doğru sıradaki işi veriyor, beni çalıştırıyor, gerçekten öğrenip öğrenmediğimi ölçüyor ve yarını buna göre değiştiriyor.”**

Bu nedenle ürün yalnızca plan yapan bir uygulama değildir. Öğrenme oturumunu yürütür, ölçer, eksikliği tespit eder, gerektiğinde konuyu farklı biçimde yeniden çalıştırır ve yeterlilik kanıtlanmadan bağımlı konulara geçiş vermez.

## Ürünün çözmek istediği ana problem

Uzun teknik kariyer yollarında sorun yalnızca “hangi konuları öğrenmeliyim?” değildir. Asıl sorunlar şunlardır:

- bugün tam olarak ne çalışılacağının belirsiz olması,
- uzun roadmap'lerin uygulanabilir günlük görevlere dönüşmemesi,
- bir konuyu okumak/izlemek ile gerçekten öğrenmenin karıştırılması,
- temel eksikken ileri konuya geçilmesi,
- öğrenilen bilginin zaman içinde unutulması,
- sınav veya hataların sonraki çalışma planını değiştirmemesi,
- kişinin güçlü ve zayıf alanlarına rağmen herkes için aynı sabit programın uygulanması,
- teknik eğitim ile İngilizcenin birbirinden kopuk yürütülmesi,
- AI araçlarıyla bir görevi tamamlamanın o görevi gerçekten anlamakla karıştırılması,
- birkaç gün ara verildiğinde programın bozulması veya biriken görevlerin kullanıcıya yığılması,
- yıllar süren ileri uzmanlaşma yolunda teorinin gerçek engineering yetkinliğine dönüşmemesi.

Ürün bu sorunları tek sistem içinde çözmeyi hedefler.

## Klasik kurs uygulamasından farkı

Klasik sistem çoğunlukla şu modeli kullanır:

`Ders 1 tamamlandı → Ders 2 açıldı → Ders 3 açıldı`

AI Infra Learning Coach ise şu modeli kullanır:

`Çalış → ölç → mastery güncelle → retention kontrol et → prerequisite kontrol et → gerekiyorsa remediation uygula → uygun sıradaki görevi seç`

Bir videoyu izlemek, metni okumak, görev kartını işaretlemek veya uygulamada geçirilen süre tek başına ilerleme sağlamaz.

## Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Kanıtlanmış öğrenme yalnız quiz değil; hedefe göre şunların uygun kombinasyonuna dayanır:

- teori soruları,
- uygulamalı coding görevleri,
- debugging,
- kod/konu açıklayabilme,
- transfer soruları,
- gecikmeli retention testleri,
- gerçekçi proje/capstone çıktıları,
- profiling/benchmarking ve production-context evidence.

Tek bir kolay quiz veya kullanıcının “öğrendim” demesi kritik bir beceriyi mastered yapmak için yeterli değildir.

## Adaptif davranış

Uygulama kullanıcının performansına göre programı değiştirmelidir. Örneğin kullanıcı pointer konusunda zorlanıyorsa pointer'a bağlı yeni konular ertelenebilir; ancak bağımsız Linux görevleri devam edebilir. Kullanıcı daha önce öğrendiği bir bilgiyi gecikmeli testte doğrulayamıyorsa bu konu yeniden günlük plana girebilir. Kullanıcı çok hızlı ilerliyorsa doğrulama testlerinden sonra gereksiz içeriği atlayabilmelidir.

Dolayısıyla curriculum bir takvim değil, prerequisite ilişkileri bulunan bir bilgi haritasıdır. Takvim yalnızca günlük kapasiteyi belirler; hangi konunun sıradaki doğru konu olduğunu mastery ve knowledge graph belirler.

## Uzun vadeli kariyer hedefiyle ilişki

Ürün genel amaçlı “her şeyi öğreten” bir eğitim uygulaması değildir. Uzun vadeli hedef **AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek teknik derinlik** oluşturmaktır.

Ana yön:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS / Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

2026-08-24 kapsam genişletmesiyle bu rota yalnız yaklaşık üç yıllık bir horizon ile sınırlı değildir. Gerekli öğretim, practice, assessment, retention, proje ve professional-readiness evidence'ı için rota **4+ yıl veya daha uzun** sürebilir.

Bu süre hedef değil, esnek bir planlama ufkudur:

```text
elapsed_time != readiness
professional_readiness = verified capability
```

Bir konunun ne zaman geçileceğini takvim değil yeterlilik belirler.

## Professional readiness çıkış hedefi

Uzun curriculum'u tamamlamanın anlamı yalnız içerik coverage'ı bitirmek değildir. Kullanıcı final seviyede mümkün olduğunca bağımsız biçimde:

- C/C++/Linux systems geliştirme,
- debugging ve profiling,
- concurrency/networking/distributed reasoning,
- GPU/CUDA/Triton geliştirme,
- LLM inference serving ve optimizasyon,
- latency/throughput/memory benchmark analizi,
- multi-GPU / AI Infrastructure temel tasarım ve operasyon,
- teknik dokümantasyon, design decision ve troubleshooting iletişimi

gibi alanlarda gerçek artifact ve transfer evidence üretmelidir.

Bu hedef `senior engineer` unvanı, iş teklifi veya belirli maaş garantisi değildir. Gerçek ekip/production iş deneyimi ayrıca oluşur. Ayrıntı: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## İngilizcenin ürün içindeki rolü

Başlangıç İngilizce seviyesi A0 kabul edilir. İngilizce, teknik eğitimin başlaması için önce tamamlanması gereken ayrı bir ön koşul değildir. Teknik eğitimle paralel ilerler ve zaman içinde compiler/terminal mesajlarından dokümantasyona, GitHub iletişimine, design docs/paper okumaya ve teknik mülakata kadar gerçek kullanım bağlamına bağlanır.

## AI'nın ürün içindeki rolü

AI, kullanıcının yerine öğrenen veya sürekli doğrudan cevabı veren bir kestirme olarak konumlandırılmaz. AI Tutor gerektiğinde:

- açıklama yapar,
- farklı anlatım uygular,
- ipucu verir,
- yanlışın kök nedenini analiz eder,
- açık uçlu cevap ve kodu değerlendirir,
- kullanıcının AI ile üretilmiş kodu gerçekten anlayıp anlamadığını kontrol eder.

AI kullanımı yasak değildir; ancak dış AI yardımıyla görev tamamlanmışsa mastery için Objective'e uygun independent comprehension/transfer/production doğrulaması gerekir.

## Kullanıcıya gösterilecek ilerleme anlayışı

4+ yıllık uzun rota ürünün **takvimsel başarı metriği değildir**. UI'da `Gün 47 / 1460+` veya benzeri bir ana ilerleme metriği kullanılmaz. Aynı şekilde “kariyerin %12'si tamamlandı” gibi sahte kesinlik oluşturan göstergeler ana başarı metriği değildir.

Kullanıcıya anlamlı olan şeyler gösterilir:

- hangi becerileri gerçekten mastered ettiği,
- hangi becerileri geliştirdiği,
- hangi bilgilerin yeniden doğrulama zamanı geldiği,
- hangi alanlarda zorlandığı,
- bugün neden belirli görevleri yaptığı,
- hangi prerequisite'in bir sonraki konuyu tuttuğu,
- professional-readiness için hangi engineering capability katmanlarının henüz kanıtlanmadığı.

## V1 release ile tam curriculum ayrımı

Uzun rota genişledi diye ilk release yıllarca bekletilmez.

- **V1 release:** learning engine, planner, assessment, retention/remediation ve ilk production-quality curriculum paketi.
- **Tam professional curriculum:** aynı ürün motoru üzerinde yıllar boyunca kapsamı genişleyen C/C++ → systems → distributed → GPU/CUDA → inference → AI Infrastructure rotası.

Bu nedenle ilk 8–12 haftalık production-quality curriculum paketi V1 release için geçerli yaklaşım olarak kalır; fakat ürünün nihai eğitim kapsamı artık bu paket veya yaklaşık üç yıllık rota ile sınırlı değildir.

## Ürünün kişiliği

Uygulama kullanıcıyı suçlayan, streak kaybıyla baskılayan veya gereksiz gamification ile yöneten bir ürün olmayacaktır. Birkaç gün ara verilirse görev borcu yığmak yerine mevcut durum yeniden hesaplanacaktır.

Ürün deneyiminin karakteri:

- sakin,
- profesyonel,
- net,
- modern,
- gereksiz kalabalıktan uzak,
- karar yükünü azaltan,
- öğrenme kalitesini zaman geçirilen süreden daha önemli gören bir sistemdir.

## 1A kabul kontrolü

1A'nın ana ürün ilkeleri korunmaktadır. 2026-08-24 kapsam genişletmesi 1A'yı iptal etmez; uzun vadeli çıkış hedefini büyütür.

> **Kapsam genişletme notu — 2026-08-24:** Nihai curriculum horizon'ı 4+ yıla açıldı ve çıkış hedefi `professional_readiness` olarak yükseltildi. Takvim yine mastery yerine geçmez. V1 ilk production curriculum paketiyle daha erken release edilir; uzun curriculum Aşama 19 dahilinde genişletilir.
