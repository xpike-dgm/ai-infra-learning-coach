# V1 Success & Acceptance Criteria — AI Infra Learning Coach

**Adım:** 1C — Başarı kriterleri  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge, V1'in yalnızca özellikleri mevcut olduğu için değil, ürünün ana vaadini gerçekten yerine getirdiği kanıtlandığında başarılı sayılması için kullanılacak ölçülebilir kabul kriterlerini tanımlar.

Ana ilke:

> **V1'in başarısı ekran sayısıyla değil, doğru öğrenme kararları üretmesi, bu kararları açıklayabilmesi, gerçek öğrenmeyi ölçmesi ve kullanıcı verisini güvenilir biçimde korumasıyla değerlendirilir.**

---

# 1. Test Sonucu Sınıfları

Her kriter bağımsız olarak şu sonuçlardan birini alır:

- **PASS:** Beklenen davranış eksiksiz gerçekleşti.
- **PASS WITH NOTES:** Ana davranış doğru; release engellemeyen küçük sorun/not var.
- **FAIL:** Beklenen davranış gerçekleşmedi veya yanlış karar üretildi.
- **BLOCKED:** Test dış bir bağımlılık nedeniyle henüz yürütülemiyor.

## Öncelik seviyeleri

- **P0 — Release blocker:** Başarısızsa V1 release edilemez.
- **P1 — V1 required:** V1 kapsamındadır; ciddi hata release öncesi düzeltilmelidir. Küçük görsel/ergonomik notlar belgelenebilir.
- **P2 — Kalite iyileştirmesi:** Ürünün ana döngüsünü bozmaz; sonraki patch'e kalabilir.

**Release kapısı:** Tüm P0 kriterleri PASS olmalıdır. Kritik P1 fonksiyon hatası bulunmamalıdır. Kodlama AI'ın kendi raporu yeterli değildir; kritik P0 akışları bağımsız Test/QA AI tarafından da doğrulanmalıdır.

---

# 2. Standart Test Profilleri

Aynı davranışların tekrar üretilebilmesi için en az aşağıdaki test profilleri kullanılacaktır.

## TP-01 — Yeni kullanıcı
- Teknik seviye: sıfır
- İngilizce: A0
- Mastery geçmişi: yok
- Due review: yok
- Günlük kapasite: örnek normal gün

## TP-02 — Tek temel konuda zayıf
- C'nin önceki temel konuları güçlü
- Pointer temelleri mastery eşiğinin altında
- Linux bağımsız dalında ilerleme mümkün

## TP-03 — Retention kaybı
- Bir skill daha önce mastered
- Review zamanı gelmiş
- Gecikmeli tekrar başarısız

## TP-04 — Uzun ara
- En az 7 gün çalışma yok
- Birden fazla eski due review mevcut
- Önceki günlük planlar tamamlanmamış

## TP-05 — AI yardımlı coding
- Coding görevi dış AI yardımıyla tamamlanmış olarak işaretli
- Comprehension doğrulaması henüz yapılmamış

## TP-06 — Hızlı öğrenen kullanıcı
- Diagnostic ve farklı kanıt türlerinde yüksek başarı
- Bazı başlangıç içeriği gerçekten biliniyor

## TP-07 — Teknik/English asimetrisi
- Teknik performans güçlü
- English hattı zayıf veya tersi

Bu profiller Aşama 3 ve Aşama 11'de daha ayrıntılı fixture/test datasına dönüştürülecektir.

---

# 3. Çekirdek Günlük Planlama Kriterleri

## SC-001 — Geçerli günlük plan üretimi — P0

**Verilen:** Geçerli kullanıcı durumu, curriculum graph ve günlük kapasite.  
**Beklenen:** Planner boş olmayan, yalnız geçerli LearningTask referansları içeren ve o gün uygulanabilir bir plan üretir. Planın toplam tahmini süresi kullanıcının seçili günlük kapasitesini aşmamalıdır; varsa tolerans değeri Aşama 3A'da yapılandırılır.

**FAIL örneği:** 90 dakikalık gün için açıklamasız biçimde 180 dakikalık zorunlu görev üretmek.

## SC-002 — Aynı durumdan tutarlı curriculum kararı — P0

Aynı kullanıcı mastery/prerequisite/retention durumu ve aynı planner konfigürasyonu verildiğinde, çekirdek görev seçimi ve reason code'lar tutarlı olmalıdır. AI ile üretilen açıklama metni değişebilir; fakat curriculum kararı LLM rastlantısına göre değişmemelidir.

## SC-003 — Hard prerequisite ihlal edilemez — P0

**Verilen:** `Basic Pointers` hard prerequisite ve configured mastery şartını karşılamıyor.  
**Beklenen:** Ona bağımlı `Pointer Arithmetic` / ilgili ileri task yeni konu olarak planlanmaz.

Hard prerequisite ihlali V1 için doğrudan release blocker'dır.

## SC-004 — Bağımsız öğrenme dalı devam eder — P0

TP-02 profilinde pointer dalı bloke olduğunda, prerequisite bağı olmayan uygun Linux/English veya diğer bağımsız görevler planlanabilmelidir. Tek bir zayıf skill tüm curriculum'u dondurmamalıdır.

## SC-005 — İki farklı kullanıcıya gerçekten farklı plan — P0

Aynı günlük süreye sahip fakat mastery/retention geçmişi farklı iki test profiline planner en az bir anlamlı görev/öncelik farkı üretmelidir. Sistem yalnız kullanıcı adına göre farklı metin gösterip aynı sabit kurs listesini vermemelidir.

## SC-006 — Planner kararı açıklanabilir — P1

Her adaptif olarak eklenen, ertelenen veya önceliklendirilen görev için sistemde bir reason code bulunmalıdır. UI, bunu sade bir cümleye çevirebilmelidir.

Örnek: `RETENTION_DUE → “Pointers bilgisini tekrar kontrol etme zamanı geldiği için 15 dk tekrar eklendi.”`

---

# 4. Mastery ve Öğrenme Kanıtı Kriterleri

## SC-007 — Task completion tek başına mastery veremez — P0

Bir lesson/reading/task kartının yalnız `complete=true` yapılması kritik skill'i `mastered` durumuna geçirmemelidir.

## SC-008 — Tek kolay quiz ile kritik skill mastered olamaz — P0

Kritik coding/systems skill'lerinde tek bir teori quiz başarısı yeterli değildir. Aşama 2E'de tanımlanacak minimum kanıt türleri ve threshold şartları karşılanmadan mastered durumu verilemez.

## SC-009 — Mastery sonucu yeniden hesaplanabilir ve izlenebilir — P0

Her mastery güncellemesinin hangi attempt/evidence kayıtlarından üretildiği izlenebilmelidir. Aynı evidence + aynı mastery konfigürasyonu aynı sonucu üretmelidir.

## SC-010 — Başarısız performans doğru skill'e yansır — P0

Bir debugging/coding/quiz hatası yalnız genel bir “puan düştü” olarak kalmamalı; ilgili skill/topic evidence kaydına bağlanmalıdır. Yanlış skill'in mastery değerini etkilemek FAIL'dir.

## SC-011 — AI yardımı mastery kestirmesi olamaz — P0

TP-05 profilinde AI yardımıyla tamamlanan coding görevi, comprehension/transfer doğrulaması olmadan kritik skill'i mastered yapamaz.

## SC-012 — AI yardımı sonrası comprehension check — P1

AI yardımı işaretli görev sonrasında sistem en az bir uygun doğrulama yolu tetikleyebilmelidir: açıklama, satır analizi, varyasyon, debugging veya transfer task. Başarı/başarısızlık mastery evidence'a yansır.

---

# 5. Assessment Kriterleri

## SC-013 — Günlük mikro değerlendirme evidence üretir — P0

Günlük quiz/coding/debugging/açıklama sonucu Attempt olarak saklanmalı, doğru skill'e bağlanmalı ve mastery engine tarafından kullanılabilir olmalıdır.

## SC-014 — Haftalık sınav gelecek haftayı gerçekten değiştirir — P0

**Test:** Haftalık sınavda seçili bir prerequisite skill kasıtlı olarak zayıf sonuçlandırılır.  
**Beklenen:** Sonraki hafta planner'ında o skill veya remediation önceliği artar ve ona bağlı yeni konu gerekiyorsa ertelenir.

Sadece sınav sonucu ekranında düşük puan göstermek yeterli değildir.

## SC-015 — Haftalık sınav güçlü alanı gereksiz tekrar ettirmez — P1

Aynı sınavda güçlü ve retention riski düşük skill, yalnız haftalık sınavda bulunduğu için gereksiz ağır remediation almaz.

## SC-016 — Aylık sınav curriculum önceliklerini etkiler — P0

Aylık assessment'ta kalıcı bir skill gap tespit edildiğinde, sonraki plan döneminde ilgili domain/topic önceliği değişmelidir. Sonuç yalnız rapor olarak kalamaz.

## SC-017 — Eski konular assessment içinde yeniden ölçülebilir — P1

Weekly/monthly assessment yalnız son öğrenilen konulardan oluşmamalı; retention amacıyla daha eski uygun skill'lerden soru/task seçebilmelidir.

---

# 6. Retention ve Remediation Kriterleri

## SC-018 — Due review yeniden plana girer — P0

Mastered bir skill'in review zamanı geldiğinde, planner uygun süre penceresinde retention görevi oluşturabilmelidir.

## SC-019 — Başarısız delayed review sonucu sistemi değiştirir — P0

TP-03 profilinde gecikmeli tekrar başarısız olduğunda mastery/retention state veya planner önceliği tanımlı kurallara göre değişmelidir. Skill sonsuza kadar mastered kalmamalıdır.

## SC-020 — Başarılı review interval'i ilerletir — P1

Başarılı retention testi sonrası aynı skill'in sonraki review tarihi, Aşama 2F/12C'de seçilecek algoritmaya göre ileri taşınmalıdır.

## SC-021 — Remediation aynı içeriği kör tekrar etmez — P1

Aynı skill'de tekrarlayan başarısızlık sonrası sistem en az iki farklı müdahale türü arasında seçim yapabilmelidir; örneğin alternatif açıklama + mikro debugging. Yalnız aynı lesson metnini sonsuz kez göstermek kabul edilmez.

## SC-022 — Kök prerequisite'e geri dönebilir — P0

Bir skill'deki tekrar eden başarısızlığın nedeni daha temel prerequisite açığı olarak işaretlendiğinde, planner temel skill için remediation ekleyebilmeli ve bağımlı yeni konuyu bekletebilmelidir.

---

# 7. Replan ve Zaman Yönetimi Kriterleri

## SC-023 — 1–7+ gün ara sonrası görev borcu yığılmaz — P0

TP-04 profilinde uygulama kaçırılmış her eski günlük görevi ertesi güne zorunlu olarak kopyalamamalıdır. Planner güncel mastery, due review, prerequisite ve yeni günlük kapasiteyle yeni plan üretmelidir.

## SC-024 — Günlük kapasite değişince plan yeniden sığdırılır — P0

Kullanıcı gün içinde `normal → kısa gün` değiştirirse, tamamlanmamış plan yeni kapasiteye göre yeniden önceliklendirilmelidir. Kritik retention/remediation öncelikleri korunurken düşük öncelikli görevler ertelenebilir.

## SC-025 — Hızlı öğrenen kullanıcı gereksiz başlangıç içeriğini atlayabilir — P1

TP-06 profilinde yalnız bir kolay quiz değil, birden fazla uygun diagnostic/evidence şartı karşılandığında içerik skip edilebilir. Skip edilen skill ileride retention kontrolünden tamamen muaf olmaz.

---

# 8. English Paralel Hat Kriterleri

## SC-026 — English hattı günlük plandan kaybolmaz — P0

English için planner'da tanımlanan minimum/uygun pay, teknik remediation yoğunluğu dışında sistematik olarak sıfırlanmamalıdır. Kesin oran Aşama 3/6'da kararlaştırılır.

## SC-027 — English zayıflığı ilgisiz teknik dalı kilitlemez — P0

English skill'i zayıf olduğu için prerequisite ilişkisi olmayan C/Linux teknik topic'leri otomatik hard-lock olmamalıdır. English ve teknik öğrenme paralel ilerler.

## SC-028 — Teknik içerik English görevine bağlanabilir — P1

O gün öğrenilen teknik vocabulary/compiler mesajları uygun olduğunda English task üretiminde kullanılabilmelidir.

---

# 9. AI Tutor Kriterleri

## SC-029 — AI servis arızası çekirdek planner/mastery'yi bozmaz — P0

AI provider erişilemez olduğunda:

- mevcut curriculum okunabilmeli,
- local progress erişilebilmeli,
- deterministic prerequisite/mastery/planner mantığı çalışmaya devam edebilmeli,
- AI gerektiren görevler açıkça fallback/ertelenmiş durum gösterebilmelidir.

Uygulama sırf LLM yanıt vermedi diye açılamaz hale gelmemelidir.

## SC-030 — AI çekirdek kuralları keyfi aşamaz — P0

AI Tutor, bir hard prerequisite'i “bence hazır” diyerek açamaz, doğrudan mastery state yazamaz ve planner'ın deterministik kurallarını atlayamaz. Bu değişiklikler yalnız tanımlı application/domain service kuralları üzerinden yapılmalıdır.

## SC-031 — AI değerlendirmesi düşük güven durumunu ifade edebilir — P1

Açık uçlu değerlendirmede AI confidence yeterli değilse sistem bunu güçlü mastery evidence olarak kullanmak zorunda değildir; yeniden test/fallback tetiklenebilmelidir.

---

# 10. Curriculum / Knowledge Graph Kriterleri

## SC-032 — Graph bütünlüğü — P0

Release curriculum datasında:

- tüm prerequisite referansları mevcut node'a işaret etmeli,
- hard prerequisite graph'ta cycle bulunmamalı,
- her aktif topic en az bir ölçülebilir learning objective'e sahip olmalı,
- planner tarafından kullanılan her skill/topic benzersiz ID taşımalıdır.

## SC-033 — İlk 8–12 haftalık paket uçtan uca çalışabilir — P0

İlk production curriculum paketi yalnız başlık listesinden oluşmamalı. Kullanılacak her ana topic için en az lesson/task/evidence/assessment bağlantısı bulunmalı ve planner tarafından seçilebilir olmalıdır.

## SC-034 — İçerik QA olmadan production'a alınmaz — P0

İlk curriculum paketinde teknik doğruluk, prerequisite uyumu ve soru-cevap doğruluğu QA'dan geçmeden release etiketi verilemez. Kritik teknik içerik hatası P0 kabul edilir.

---

# 11. Veri, Offline ve Güvenilirlik Kriterleri

## SC-035 — App restart progress kaybettirmez — P0

Uygulama kapatılıp yeniden açıldığında tamamlanmış Attempt'ler, mastery state, review schedule, settings ve tamamlanmamış aktif session'ın desteklenen kısmı korunmalıdır.

## SC-036 — Session recovery — P1

Çalışma oturumu ortasında uygulama kapanırsa kullanıcı başlangıca dönmek zorunda kalmamalıdır. Son güvenli checkpoint'ten devam edebilmelidir.

## SC-037 — Backup → restore veri eşdeğerliği — P0

Backup alınıp temiz kurulum/temiz local state üzerine restore edildiğinde aşağıdaki temel veriler kaybolmamalıdır:

- mastery/history,
- attempts,
- review schedule,
- exam results,
- progress,
- temel user settings.

## SC-038 — Schema migration progress'i korur — P0

En az bir test migration'ında eski DB şeması yeni sürüme yükseltildiğinde kullanıcı progress'i ve ilişkiler korunmalıdır. Migration crash/data loss P0 FAIL'dir.

## SC-039 — Temel kullanım local-first çalışır — P0

İnternet bağlantısı yokken local curriculum/progress görüntüleme, mevcut deterministic plan ve AI gerektirmeyen görevler çalışmalıdır. İnternet gerektiren AI özellikleri açıkça ayrılmalıdır.

---

# 12. UI / Progress / Bildirim Kriterleri

## SC-040 — Sahte zaman ilerlemesi UI'ın ana metriği olamaz — P0

Ana ekran/progress ekranında `Gün X / 1095` veya “kariyer % tamamlandı” ana başarı metriği olarak bulunmamalıdır.

## SC-041 — `Henüz başlamadı` ve `başarısız/zayıf` ayrımı — P1

Kullanıcının hiç çalışmadığı skill ile çalışıp zayıf kaldığı skill görsel/semantik olarak ayrı durumlar olmalıdır.

## SC-042 — Today ekranı tek bakışta eylem üretir — P1

Kullanıcı ana ekranda en az şu bilgileri ek navigasyon gerektirmeden görebilmelidir:

- sıradaki görev,
- bugünkü yaklaşık toplam süre,
- current topic/skill,
- temel mastery durumu,
- `Başla/Devam Et` CTA.

## SC-043 — Kritik bildirimler doğru olaya bağlıdır — P1

Weekly/monthly exam ve due retention bildirimleri yalnız gerçekten ilgili durum oluştuğunda zamanlanmalı; tamamlanmış veya artık due olmayan görev için stale bildirim üretmemelidir.

---

# 13. Release ve Gerçek Kullanım Kriterleri

## SC-044 — Fresh install — P0

Release APK desteklenen gerçek Android cihazında temiz kurulumdan sonra crash olmadan açılmalı, ilk kullanım akışını başlatabilmeli ve local database'i oluşturabilmelidir.

## SC-045 — Update install — P0

Önceki test/release adayı üzerine güncelleme kurulduğunda mevcut progress kaybolmamalı ve migration başarılı olmalıdır.

## SC-046 — Ana regression paketi — P0

Final release öncesinde en az şu akışların tamamı yeniden test edilmelidir:

Today → Study Session → Assessment → Mastery → Planner/Replan → Weekly/Monthly Exam → Retention → Remediation → AI Tutor fallback → English track → Backup/Restore.

Bu zincirde P0 FAIL varken release yapılamaz.

## SC-047 — Bağımsız QA zorunluluğu — P0

Kodlama AI'ın “testler geçti” raporu tek başına release kabulü değildir. P0 acceptance paketinin kritik örnekleri bağımsız Test/QA AI tarafından repo/build üzerinde tekrar doğrulanmalıdır.

## SC-048 — Pilot sırasında hard-rule ihlali sıfır — P0

Gerçek kullanım pilotunda:

- hard prerequisite bypass,
- kullanıcı progress data loss,
- mastered olmayan kritik skill'i yanlış mastered sayma gibi açık çekirdek kural ihlalleri **0** olmalıdır.

## SC-049 — Pilot planlarının çoğu manuel yapısal düzeltme istememeli — P1

En az 14 günlük gerçek kullanım penceresinde üretilen çalışma günlerinin hedef olarak **%90 veya daha fazlasında**, kullanıcı planı “bu görev prerequisite yüzünden imkânsız / süre tamamen yanlış / yanlış dala götürüyor” gibi yapısal bir hata nedeniyle elle düzeltmek zorunda kalmamalıdır.

Bu %90 ürün kalitesi için başlangıç hedefidir; pilot verisi toplandığında gerekçeli olarak yeniden kalibre edilebilir. Kullanıcının yalnız kişisel tercih nedeniyle süre değiştirmesi manuel yapısal düzeltme sayılmaz.

---

# 14. V1 Başarılı Sayılma Kapısı

V1 ancak aşağıdaki koşulların tümü sağlandığında “release-ready” kabul edilir:

1. Tüm **P0** acceptance kriterleri PASS.
2. Açık kritik P1 fonksiyon hatası yok.
3. İlk 8–12 haftalık production curriculum teknik QA'dan geçmiş.
4. En az bir gerçek Android cihazında fresh install + update install testleri geçmiş.
5. Backup/restore ve migration veri kaybı üretmemiş.
6. Planner/mastery/prerequisite kritik senaryoları bağımsız QA tarafından doğrulanmış.
7. En az 14 günlük gerçek kullanım pilotu yapılmış; hard-rule ihlali görülmemiş.
8. Release notlarında bilinen P2 ve kabul edilmiş küçük P1 sorunları açıkça belgelenmiş.

---

# 15. Bilinçli Olarak Şimdi Sayısallaştırılmayan Parametreler

1C başarı kriterleri test edilebilir davranışları kilitler; ancak aşağıdaki sayılar ilgili uzmanlık aşamalarında araştırma/simülasyonla belirlenecektir:

- mastery threshold,
- assessment type ağırlıkları,
- minimum evidence sayısı,
- AI-help penalty miktarı,
- spaced repetition interval formülü,
- daily planner task-share oranları,
- English günlük oranı,
- planner time-budget tolerance,
- exam composition yüzdeleri.

Bu değerleri 1C'de rastgele sabitlemek yerine:

- Aşama 2 — mastery,
- Aşama 3 — planner,
- Aşama 4 — assessment,
- Aşama 6 — English,
- Aşama 17 — pilot kalibrasyonu

sırasında araştırma ve test verisiyle kesinleştireceğiz.

---

# 1C Tamamlanma Kontrolü

1C tamamlandı çünkü V1'in kritik davranışları artık şu alanlarda test edilebilir acceptance kriterlerine bağlıdır:

- daily planner,
- mastery,
- prerequisites,
- weekly/monthly assessment,
- retention,
- remediation,
- missed-day/time-budget replan,
- AI help / AI Tutor sınırları,
- parallel English,
- curriculum graph integrity,
- local persistence,
- backup/restore/migration,
- UI progress semantics,
- release APK,
- bağımsız QA,
- gerçek kullanım pilotu.

> **Tamamlanma notu — 2026-08-24:** 1C kapsamında V1 için P0/P1/P2 öncelikli, senaryoya bağlı ve bağımsız QA ile doğrulanabilir acceptance sistemi oluşturuldu. Henüz bilimsel/ürün testi yapılmadan keyfi mastery/retention yüzdeleri uydurulmadı; bu sayısal parametreler ilgili sonraki aşamalarda kesinleştirilecek. 1D'de V1'in non-goals listesi son kez konsolide edilerek Aşama 1 kapatılacaktır.

---

# Sıradaki Adım

## 1D — Non-goals

V1'in ve projenin özellikle **ne olmaya çalışmadığını** tek bir kalıcı listede kilitlemek; scope creep'i engellemek ve Aşama 1'i tamamlamak.