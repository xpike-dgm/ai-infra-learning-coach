# Product Requirements — AI Infra Learning Coach

Bu belge ürün gereksinimlerinin kilitlendiği ana kayıttır. `docs/EXECUTION_INDEX.md` içindeki Aşama 1 adımları tamamlandıkça bu belge genişletilir.

## Durum

- **Aktif aşama:** Aşama 1 — Ürün Çerçevesini Kilitle
- **Tamamlanan adım:** 1A — Ana ürün amacı
- **Sıradaki adım:** 1B — V1 kapsamı
- **1A tamamlanma tarihi:** 2026-08-24

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
- birkaç gün ara verildiğinde programın bozulması veya biriken görevlerin kullanıcıya yığılması.

Ürün bu sorunları tek sistem içinde çözmeyi hedefler.

## Klasik kurs uygulamasından farkı

Klasik sistem çoğunlukla şu modeli kullanır:

`Ders 1 tamamlandı → Ders 2 açıldı → Ders 3 açıldı`

AI Infra Learning Coach ise şu modeli kullanır:

`Çalış → ölç → mastery güncelle → retention kontrol et → prerequisite kontrol et → gerekiyorsa remediation uygula → uygun sıradaki görevi seç`

Bir videoyu izlemek, metni okumak, görev kartını işaretlemek veya uygulamada geçirilen süre tek başına ilerleme sağlamaz.

## Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

“Kanıtlanmış öğrenme” ileride Aşama 2'de matematiksel olarak kesinleştirilecek; ancak ürün seviyesinde şu tür kanıtların kombinasyonuna dayanacaktır:

- teori soruları,
- uygulamalı coding görevleri,
- debugging,
- kod/konu açıklayabilme,
- transfer soruları,
- gecikmeli retention testleri,
- gerektiğinde proje çıktıları.

Tek bir kolay quiz veya kullanıcının “öğrendim” demesi kritik bir beceriyi mastered yapmak için yeterli olmayacaktır.

## Adaptif davranış

Uygulama kullanıcının performansına göre programı değiştirmelidir. Örneğin kullanıcı pointer konusunda zorlanıyorsa pointer'a bağlı yeni konular ertelenebilir; ancak bağımsız Linux görevleri devam edebilir. Kullanıcı daha önce öğrendiği bir bilgiyi gecikmeli testte unutmuşsa bu konu yeniden günlük plana girebilir. Kullanıcı çok hızlı ilerliyorsa doğrulama testlerinden sonra gereksiz içeriği atlayabilmelidir.

Dolayısıyla curriculum bir takvim değil, prerequisite ilişkileri bulunan bir bilgi haritasıdır. Takvim yalnızca günlük kapasiteyi belirler; hangi konunun sıradaki doğru konu olduğunu mastery ve knowledge graph belirler.

## Uzun vadeli kariyer hedefiyle ilişki

Ürün genel amaçlı “her şeyi öğreten” bir eğitim uygulaması olarak tasarlanmayacaktır. Temel uzun vadeli hedef AI Infrastructure / ML Systems / GPU Systems tarafında güçlü bir teknik yetkinlik oluşturmaktır.

Ana yön:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS / Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Bu sıra sabit sürelerle bağlanmayacaktır. Bir konunun ne zaman geçileceğini takvim değil yeterlilik belirler.

## İngilizcenin ürün içindeki rolü

Başlangıç İngilizce seviyesi A0 kabul edilir. İngilizce, teknik eğitimin başlaması için önce tamamlanması gereken ayrı bir ön koşul değildir. Teknik eğitimle paralel ilerler ve zaman içinde compiler/terminal mesajlarından dokümantasyona, GitHub iletişimine ve teknik mülakata kadar gerçek kullanım bağlamına bağlanır.

## AI'nın ürün içindeki rolü

AI, kullanıcının yerine öğrenen veya sürekli doğrudan cevabı veren bir kestirme olarak konumlandırılmayacaktır. AI Tutor gerektiğinde:

- açıklama yapar,
- farklı anlatım uygular,
- ipucu verir,
- yanlışın kök nedenini analiz eder,
- açık uçlu cevap ve kodu değerlendirir,
- kullanıcının AI ile üretilmiş kodu gerçekten anlayıp anlamadığını kontrol eder.

AI kullanımı yasak değildir; ancak dış AI yardımıyla görev tamamlanmışsa mastery için ek comprehension/transfer doğrulaması gerekebilir.

## Kullanıcıya gösterilecek ilerleme anlayışı

Yaklaşık üç yıllık rota ürünün arka plan planlama ufkudur. UI'da `Gün 47 / 1095` gibi bir ilerleme metriği kullanılmayacaktır. Aynı şekilde “kariyerin %12'si tamamlandı” gibi sahte kesinlik oluşturan göstergeler ürünün ana başarı metriği değildir.

Kullanıcıya anlamlı olan şeyler gösterilir:

- hangi becerileri gerçekten mastered ettiği,
- hangi becerileri geliştirdiği,
- hangi bilgilerin unutulma riski taşıdığı,
- hangi alanlarda zorlandığı,
- bugün neden belirli görevleri yaptığı,
- hangi prerequisite'in bir sonraki konuyu tuttuğu.

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

1A aşağıdaki noktalar net olduğu için tamamlandı:

- ürünün tek cümlelik amacı tanımlandı,
- kullanıcının günlük aldığı temel değer tanımlandı,
- klasik course/todo sistemlerinden temel farkı açıklandı,
- “kanıtlanmış öğrenme” ana ürün gereksinimi haline getirildi,
- adaptif davranış ve knowledge graph ilişkisi tanımlandı,
- kariyer rotası ürün kapsamına bağlandı,
- İngilizce ve AI'nın ürün içindeki temel rolü belirtildi,
- gün/süre tabanlı sahte ilerleme yaklaşımı reddedildi.

> **Tamamlanma notu — 2026-08-24:** 1A mevcut proje kararları birleştirilerek kilitlendi. Ürün, sabit roadmap veya todo uygulaması değil; günlük planlama, uygulama, ölçme, mastery, retention ve yeniden planlamayı tek döngüde birleştiren kişisel adaptif öğrenme koçu olarak tanımlandı. Bu tanım 1B V1 kapsamının sınırlarını belirlemek için temel kabul edilecektir.

---

# Sıradaki Adım

## 1B — V1 kapsamı

Bir sonraki adımda şu soruyu kesinleştireceğiz:

> **Bu vizyonun ilk gerçek sürümünde hangi özellikler kesin bulunacak, hangileri özellikle sonraya bırakılacak?**
