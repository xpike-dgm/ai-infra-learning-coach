# V1 Scope — AI Infra Learning Coach

**Adım:** 1B — V1 kapsamı  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge, ilk gerçek release sürümünün hangi yetenekleri içereceğini ve hangi alanların bilinçli olarak sonraya bırakılacağını kilitler.

## V1 kapsam ilkesi

V1'in amacı olabildiğince fazla özellik toplamak değildir. V1, ürünün ana vaadini uçtan uca gerçek biçimde çalıştırmalıdır:

> Kullanıcı uygulamayı açar → bugün ne çalışacağını görür → çalışma görevlerini tamamlar → uygulama gerçekten öğrenip öğrenmediğini ölçer → mastery/retention/prerequisite durumu güncellenir → sonraki plan performansa göre değişir.

Bir özellik bu ana döngüyü doğrulamak veya günlük kullanımı güvenilir hale getirmek için gerekmiyorsa V1'e zorunlu olarak alınmayacaktır.

---

# V1'DE KESİN OLACAKLAR

## 1. Tek kullanıcı ve kişisel kullanım

- Android odaklı kişisel mobil uygulama.
- Hesap açma zorunluluğu yok.
- Çok kullanıcılı SaaS mimarisi yok.
- Kullanıcının başlangıç teknik seviyesi ve İngilizce seviyesi yerel profilde tutulur.
- Günlük çalışma süresi ve temel tercihler ayarlanabilir.

## 2. Today / Bugünkü Çalışma ekranı

Ana ekranın birincil amacı `Bugün ne yapmalıyım?` sorusunu cevaplamaktır.

Kesin bulunacaklar:

- bugünkü toplam tahmini çalışma süresi,
- sıradaki görev,
- günün görev listesi,
- mevcut topic/skill,
- ilgili mastery durumu,
- yaklaşan retention/assessment uyarısı,
- büyük `Çalışmaya Başla / Devam Et` CTA,
- görevin neden bugün seçildiğine dair sade açıklama gerektiğinde gösterilebilir.

`Gün X / 1095` veya kariyerin yüzde kaçının tamamlandığı gibi sahte kesinlik oluşturan metrikler olmayacaktır.

## 3. Knowledge graph ve prerequisite sistemi

V1 curriculum sabit takvim olmayacaktır.

Sistem en az şunları destekleyecek:

- Domain / Module / Topic / Skill / Learning Objective yapısı,
- hard prerequisite,
- soft prerequisite,
- topic durumları,
- prerequisite başarısızsa bağımlı konuyu bekletme,
- bağımsız öğrenme dallarını devam ettirme.

İlk release için graph'ın tamamı 3 yıllık olmak zorunda değildir; ilk gerçek 8–12 haftalık curriculum yüksek kalitede hazırlanacaktır.

## 4. Günlük adaptif planner

V1, statik görev listesi olmayacaktır.

Planner en az şu sinyalleri dikkate alacaktır:

- mastery,
- zayıf skill/topic,
- due retention review,
- prerequisite durumu,
- günlük kullanılabilir süre,
- paralel English ihtiyacı,
- son assessment sonuçları,
- kaçırılmış günler.

Planner şunları yapabilmelidir:

- yeni konu seçmek,
- remediation eklemek,
- retention görevi eklemek,
- bağımlı topic'i ertelemek,
- bağımsız dala devam etmek,
- günlük süre değişirse planı yeniden düzenlemek,
- kullanıcı birkaç gün ara verirse görev borcu yığmak yerine replan yapmak.

## 5. Günlük çalışma akışı / Task Runner

V1 en az şu görev türlerini çalıştırabilmelidir:

- kısa lesson/anlatım,
- reading,
- uygulama/practice,
- quiz,
- coding görevi,
- debugging görevi,
- Feynman/kendi cümlesiyle açıklama,
- English görevi,
- retention/retrieval görevi.

Çalışma oturumu start / pause / resume / complete durumlarını desteklemelidir. Uygulama kapanırsa devam eden oturum mümkün olduğunca geri yüklenmelidir.

## 6. Mastery Engine V1

Bir görev kartının tamamlanması tek başına ilerleme sayılmayacaktır.

V1 mastery sistemi en az şu tür kanıtları kullanabilmelidir:

- teori başarısı,
- coding başarısı,
- debugging başarısı,
- açıklayabilme,
- transfer soruları,
- gecikmeli retention başarısı.

Kritik teknik beceriler yalnız kolay quiz ile `mastered` yapılamaz. Mastery geçmişi saklanır ve zaman içinde güncellenir.

Kesin formül ve threshold değerleri 2E adımında kilitlenecektir.

## 7. Günlük mikro değerlendirme

Her gün bütün görevlerden ayrı dev bir sınav olmak zorunda değildir; fakat öğrenme kanıtı üretmek için kısa ölçümler bulunacaktır.

Desteklenecek temel formatlar:

- çoktan seçmeli,
- kısa cevap,
- kod çıktısı tahmini,
- kod tamamlama,
- mini coding,
- debugging,
- açıklama,
- eski konudan retrieval.

## 8. Haftalık sınav

V1'de haftalık assessment kesin olacaktır.

Amaç yalnız not göstermek değil:

- o haftaki yeni konuları ölçmek,
- eski konuları tekrar test etmek,
- coding/debugging başarısını görmek,
- English hattını ölçmek,
- zayıf skill'leri belirlemek,
- gelecek haftanın planner önceliklerini değiştirmek.

## 9. Aylık yeterlilik sınavı

V1'de aylık comprehensive assessment bulunacaktır.

En az:

- teori,
- uygulama/coding,
- debugging,
- açıklama,
- retention,
- teknik English

boyutlarını kapsayacak ve sonucu curriculum/planner önceliklerine yansıyacaktır.

## 10. Retention / spaced repetition

Mastered bir konu sonsuza kadar bitmiş kabul edilmeyecektir.

V1:

- review schedule tutacak,
- süresi gelen eski skill'i planner'a geri sokacak,
- başarılı tekrar sonrası aralığı büyütecek,
- başarısız tekrar sonrası mastery/önceliği güncelleyecek.

Kesin algoritma 2F / 12C adımlarında kararlaştırılacaktır.

## 11. Remediation

Kullanıcı aynı konuda zorlandığında yalnızca aynı metni yeniden göstermeyeceğiz.

V1 remediation en az şu müdahaleleri destekleyecek:

- daha sade açıklama,
- alternatif örnek,
- mikro alıştırma,
- debugging görevi,
- kolaylaştırılmış task,
- prerequisite'e kısa geri dönüş,
- tekrar test.

## 12. AI Tutor V1

AI Tutor V1 kapsamındadır; fakat ürünün bütün mantığı AI'a teslim edilmeyecektir.

AI Tutor en az:

- açıklama,
- ipucu,
- alternatif anlatım,
- yanlış cevabın olası kök nedenini analiz etme,
- açık uçlu yanıt hakkında feedback,
- kod hakkında feedback,
- AI ile üretilmiş kodu kullanıcının gerçekten anlayıp anlamadığını doğrulayan comprehension/transfer soruları

yapabilecektir.

Temel planner/mastery mantığı mümkün olduğunca deterministik uygulama kurallarında kalacaktır; LLM tek başına kullanıcının kariyer rotasını keyfi biçimde değiştirmeyecektir.

## 13. Teknik İngilizce paralel hattı

İngilizce V1'de ayrı bir gelecekteki özellik değildir; ana curriculum'un paralel parçasıdır.

İlk curriculum paketinde:

- A0 başlangıç görevleri,
- temel grammar/vocabulary,
- teknik vocabulary,
- compiler/terminal mesajları,
- kısa dokümantasyon/README okuma,
- basit teknik yazma

bulunacaktır.

## 14. İlk 8–12 haftalık gerçek curriculum

V1'in release olabilmesi için en az ilk 8–12 haftalık rota üretim kalitesinde olmalıdır.

İlk paket ağırlıklı olarak:

- Computer Fundamentals,
- C Foundations,
- Memory Foundations,
- Linux Foundations,
- başlangıç Data Structures,
- paralel A0→A1/A2 English

konularını içerir.

Kullanıcı hızlı/yavaş öğrendiği için bu `8–12 hafta` sabit takvim anlamına gelmez; yalnızca içerik kapsamının yaklaşık büyüklüğünü ifade eder.

## 15. Progress / Weakness görünümü

V1 kullanıcının gerçek bilgi durumunu göstermelidir.

En az:

- domain/topic mastery,
- zayıf alanlar,
- güçlenen alanlar,
- retention riski,
- assessment geçmişi,
- remediation geçmişi,
- `henüz başlamadı` ile `başarısız` ayrımı

gösterilecektir.

Streak varsa ikincil ve opsiyonel olacaktır; ana başarı metriği değildir.

## 16. Local-first veri saklama

Uygulamanın temel öğrenme verileri cihazda kalıcı tutulacaktır.

V1:

- app restart sonrası progress'i korumalı,
- curriculum ile kullanıcı ilerleme verisini mantıksal olarak ayırmalı,
- migration'a uygun olmalı,
- AI servisi çalışmasa bile temel local öğrenme kayıtları ve mevcut veriler erişilebilir olmalıdır.

## 17. Bildirimler ve günlük kullanım ayarları

V1'de en az:

- günlük çalışma hatırlatması,
- due retention review,
- weekly exam,
- monthly exam

bildirimleri desteklenmelidir.

Ayrıca günlük çalışma süresi, kısa/normal/yoğun gün tercihi ve tema gibi temel ayarlar bulunacaktır.

## 18. Modern ve profesyonel UI

V1 yalnız çalışan mühendislik prototipi olarak release edilmeyecektir.

Release öncesinde:

- modern ve sade tasarım,
- tutarlı typography/spacing,
- dark/light theme,
- net CTA'lar,
- kullanılabilir loading/empty/error states,
- temel accessibility,
- gerekli yerlerde sade mikro animasyonlar

bulunacaktır.

## 19. Backup / export / restore

Kişisel kullanımda dahi aylarca birikecek öğrenme verisinin kaybı kabul edilemez.

Release V1'de:

- progress backup/export,
- restore,
- update/migration sonrası verinin korunması

temel düzeyde bulunacaktır.

---

# V1'DE BİLİNÇLİ OLARAK OLMAYACAK / SONRAYA BIRAKILACAKLAR

## 1. 3 yıllık curriculum'un tamamı

V1 release için C++ → Distributed Systems → CUDA → Triton → AI Infrastructure'ın bütün üretim içeriği hazırlanmayacaktır. Uygulama motoru bunları ileride eklemeye hazır olacak; içerik kullanıldıkça modül modül genişletilecektir.

## 2. Sosyal ve ticari özellikler

V1'de yok:

- kullanıcı topluluğu,
- arkadaş sistemi,
- leaderboard,
- public profile,
- abonelik,
- ödeme,
- mağaza,
- admin paneli,
- organizasyon/rol sistemi.

## 3. Bulut hesabı ve çok cihazlı canlı senkron

İlk release local-first olacaktır. Bulut hesap sistemi ve gerçek zamanlı çok cihaz senkronu V1'in zorunlu parçası değildir.

## 4. iOS / web / desktop istemcisi

V1 Android odaklıdır. Diğer platformlar release şartı değildir.

## 5. Tam kariyer ve iş piyasası motoru

İş ilanlarını canlı tarayıp skill gap çıkarma, CV eşleştirme, şirket önerileri ve gelişmiş mock interview/career readiness sistemi Aşama 19'a bırakılacaktır.

## 6. Tam gelişmiş voice tutor

Sesli konuşma, gerçek zamanlı pronunciation analizi ve tam voice-first tutor V1 release şartı değildir. İngilizce speaking ölçümü ilk sürümde daha sade yöntemlerle ilerleyebilir.

## 7. Uygulama içine tam C/C++ compiler/sandbox gömmek

V1'in amacı telefon üzerinde tam IDE olmak değildir. Coding görevleri uygulama tarafından verilebilir; kullanıcı gerektiğinde bilgisayarda editor/terminal kullanabilir ve yanıt/kod/sonuç uygulamada değerlendirilebilir.

Güvenli, uzak veya cihaz içi code execution sistemi ileride ayrıca değerlendirilebilir. Bu özelliğin yokluğu V1'in ana öğrenme döngüsünü engellemez.

## 8. Aşırı gamification

XP ekonomisi, coin, loot, rekabetçi leaderboard, streak cezası ve benzeri mekanikler V1'in ana odağı değildir.

## 9. Tamamen LLM tarafından kontrol edilen curriculum

LLM'nin her gün keyfi şekilde yeni curriculum üretmesi veya prerequisite/mastery kurallarını atlaması V1 mimarisinde kabul edilmez. AI içerik ve feedback konusunda yardımcı olur; ana öğrenme kuralları uygulama tarafından denetlenir.

---

# V1 RELEASE TANIMI

Bir build'e `V1` diyebilmek için yalnız ekranların bulunması yeterli değildir.

V1 release adayı:

1. ilk 8–12 haftalık gerçek curriculum ile çalışmalı,
2. daily plan → task → assessment → mastery → replan döngüsünü uçtan uca işletmeli,
3. weekly/monthly assessment sonuçlarını gerçekten gelecekteki plana yansıtmalı,
4. retention ve remediation çalışmalı,
5. English paralel hat daily planner içinde görünmeli,
6. kullanıcı verisi app restart/update sonrasında korunmalı,
7. AI Tutor olmadan temel local sistem çökmemeli,
8. ana akışlar gerçek Android cihazında bağımsız QA'dan geçmeli,
9. release APK kurulabilir olmalı.

Ölçülebilir ayrıntılı acceptance kriterleri `1C — Başarı kriterleri` adımında tanımlanacaktır.

---

# 1B KABUL KONTROLÜ

1B tamamlanmıştır çünkü:

- V1'in ana öğrenme döngüsü tanımlandı,
- V1'de zorunlu ürün yetenekleri listelendi,
- içerik kapsamı ilk 8–12 haftalık production curriculum olarak sınırlandı,
- kişisel/Android/local-first sınırı netleştirildi,
- uzun vadeli career/curriculum özellikleri release zorunluluğundan çıkarıldı,
- sosyal/ticari/SaaS karmaşıklığı V1 dışında bırakıldı,
- telefon içinde tam compiler/IDE olmanın V1 gereksinimi olmadığı kararlaştırıldı,
- AI'nın ürünün deterministik öğrenme kurallarının yerine geçmeyeceği netleştirildi.

> **Tamamlanma notu — 2026-08-24:** 1B, ürünün ana vaadini eksiltmeden V1 kapsamını sınırlandıracak şekilde kilitlendi. V1 yalnız bir prototip değil; daily adaptive learning loop, mastery, prerequisite, retention, remediation, weekly/monthly assessment, AI Tutor, parallel English, progress, local persistence ve ilk gerçek curriculum paketini içeren günlük kullanılabilir Android release'i hedefler. Tam 3 yıllık curriculum, sosyal/ticari özellikler, cloud multi-device sync, tam voice tutor, gömülü IDE/compiler ve career-market engine sonraya bırakıldı. Sıradaki adım 1C'de bu kapsam ölçülebilir acceptance kriterlerine dönüştürülecektir.
