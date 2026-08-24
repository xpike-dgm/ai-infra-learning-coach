# Learning Behavior Rules — Kalıcı Öğrenme Davranışı Kararları

**Durum:** BAĞLAYICI ÜRÜN DAVRANIŞI / bazı sayısal ayrıntılar ilgili ileriki adımlara ertelenmiştir  
**Tarih:** 2026-08-24

Bu belge, ürün yöneticisi ile kullanıcı arasında öğrenme davranışı hakkında yapılan ayrıntılı soru-cevaplarda netleşen kuralları kalıcı hale getirir. Amaç bu kararların yalnız sohbet geçmişinde kalmasını önlemektir.

Bu belge `docs/LEARNING_ENGINE_SPEC.md`, `docs/V1_SCOPE.md`, `docs/V1_SUCCESS_CRITERIA.md` ve `docs/DECISIONS.md` ile birlikte okunmalıdır.

---

# 1. Uygulama yalnız test sistemi değildir; uygulama öğretir

Ana deneyim `dışarıdan öğren → uygulamaya gel → test ol` değildir.

V1'in ana öğrenme döngüsü:

`uygulama içinde öğret → örnek göster → uygulat → ölç → hatayı analiz et → gerekiyorsa yeniden öğret/remediation → yeniden ölç → sonraki planı değiştir`

Uygulama içinde bulunması hedeflenenler:

- kısa ve ayrıntılı konu anlatımı,
- worked example,
- mini alıştırma,
- quiz,
- debugging,
- açıklama/Feynman görevi,
- retention görevi,
- AI Tutor ile alternatif anlatım ve ipucu,
- haftalık/aylık değerlendirmeler.

Gerçek C/C++ uygulamalarında telefon tam IDE olmak zorunda değildir. Kodlama görevi uygulamada verilebilir, kullanıcı PC/editor/terminal üzerinde uygulayıp kod/sonuç/cevabı uygulamaya geri taşıyabilir.

Dış kaynaklar yardımcı olabilir; ancak ürün yalnız link veren bir roadmap/checklist'e dönüşmez.

---

# 2. Kapsamlı öğrenme: gerekli temel beceriler atlanmayacak

Bir Topic'in yalnız giriş kısmını göstermek o Topic'in gerekli seviyede kapsandığı anlamına gelmez.

Örnek analoji:

`Temel aritmetik` hedefleniyorsa yalnız toplama öğretilip çıkarma/çarpma/bölme gibi zorunlu alt beceriler atlanamaz.

Teknik örnek:

`Basic Pointers` için gerekli temel Skill/Learning Objective'lerin yalnız bir kısmı öğretilip Topic tamamlandı sayılamaz.

Bağlayıcı kural:

> Bir seviyenin gerektirdiği zorunlu Learning Objective'ler coverage açısından ele alınmadan o seviyenin coverage'ı tamamlanmış sayılmaz.

Ancak ileri detaylar giriş Topic'ine sıkıştırılmaz. Örneğin pointer arithmetic, double pointer veya function pointer gerekiyorsa ayrı doğru Topic/Skill aşamasında açılır.

Model spiral olabilir: önce temel kullanım, daha sonra dynamic memory/data structures/C++ memory gibi bağlamlarda aynı canonical Skill tekrar kullanılır, güçlendirilir ve yeniden ölçülür.

---

# 3. Topic coverage ile gerçek mastery ayrı şeylerdir

- `Coverage`: Kullanıcının gerekli öğretim içeriği/aktiviteleri görüp görmediği.
- `Mastery`: Kullanıcının Skill/Learning Objective'leri gerçekten yapabildiğine dair kanıt.

Bir Topic'in gerekli coverage'ı tamamlanmış olabilir ancak bağlı Skill'lerde mastery yeterli olmayabilir.

Bir Topic'in yalnız açılması, okunması veya tamamlanması mastery üretmez.

---

# 4. Test tek tip quiz olmayacak

Özellikle kritik teknik Skill'lerde birden fazla evidence biçimi kullanılacaktır:

- kavram sorusu,
- kod çıktısı tahmini,
- code completion,
- coding,
- debugging,
- kendi cümlesiyle açıklama,
- transfer/yeni bağlam sorusu,
- gecikmeli retention.

Örnek: `pointer_dereference` yalnız "*p nedir?" çoktan seçmeli sorusuyla mastered yapılamaz. Kod okuyabilme, uygulayabilme, hata bulabilme ve farklı bağlamda kullanabilme gerektiğinde ayrıca ölçülür.

Kesin evidence ağırlıkları ve threshold'lar 2C–2E'de kararlaştırılacaktır.

---

# 5. Yanlış cevap ceza değil öğrenme sinyalidir

Yanlış cevap geldiğinde sistemin ilk işi `başarısız` etiketi basmak değil, hangi Skill/Objective'in ve mümkünse hangi misconception'ın sorunlu olduğunu anlamaktır.

Temel akış:

`Attempt kaydı → evidence güncelle → olası hata/kök neden sınıflandır → Skill durumunu yeniden değerlendir → gerekirse remediation → yeni doğrulama → planner replan`

Bir yanlış:

- tüm Topic mastery'yi sıfırlamaz,
- bütün curriculum'u dondurmaz,
- otomatik olarak uzun bir ceza çalışması üretmez.

Tekrarlanan ve farklı kanıt türlerinde doğrulanan eksiklik daha güçlü remediation/mastery düşüşü üretebilir.

---

# 6. Yanlış yapılan sorunun birebir aynısı hemen tekrar sorulmayacak

Varsayılan davranış:

> Aynı soruyu ezberletmek yerine aynı Skill/Learning Objective'i farklı bir soru veya farklı bağlam ile tekrar ölç.

Soru bankasında `question family / variant family` mantığı bulunacaktır.

Örnek:

Bugün kullanıcı `write_through_pointer` objective'ini ölçen `Variant A` sorusunu yanlış yaptıysa, ertesi uygun kontrolde aynı cevabı ezberleyebileceği birebir soru yerine `Variant B` veya farklı problem yapısı seçilir.

Aynı soru uzun süre sonra retention için tekrar çıkabilir; fakat yalnız daha önce gördüğü soruyu doğru hatırlaması güçlü mastery kanıtı sayılmaz.

---

# 7. Soru uygunluğu prerequisite-aware olmak zorunda

Her AssessmentItem yalnız target Skill'i değil, soruyu çözmek için gereken ön bilgileri de tanımlamalıdır.

Kavramsal metadata en az şunları desteklemelidir:

- `target_skill / target_objective`
- `required_skills`
- difficulty
- question/evidence type
- variant family
- gerekiyorsa `not_yet_allowed / forbidden concepts` veya eşdeğer eligibility kuralı

Bağlayıcı kural:

> Kullanıcı henüz öğretilmemiş bir prerequisite yüzünden başarısız sayılmayacak.

Örnek: kullanıcı toplama/çarpma/bölmeyi biliyor ama parantez ve işlem önceliği henüz öğretilmediyse bu bilgileri zorunlu kılan karışık soru assessment adayı olamaz.

C örneği: kullanıcı basic pointers biliyor ama dynamic memory/malloc henüz öğretilmediyse basic pointer mastery testi bilmediği `malloc/free` bilgisini zorunlu kılmamalıdır.

Zorluk, gizlice bilinmeyen konu ekleyerek artırılmaz; mümkün olduğunda kullanıcının zaten öğrendiği becerileri daha karmaşık biçimde birleştirerek artırılır.

---

# 8. Soru zorluğu adaptif olacak

Soru havuzu en az basit/orta/zor veya eşdeğer difficulty seviyelerini destekleyecektir.

Genel anlam:

- **Basit:** yeni kavramın temel modelini anlayıp anlamadığını ölçer.
- **Orta:** beceriyi gerçek uygulama/kod/debugging bağlamında kullanabilmeyi ölçer.
- **Zor:** birden fazla bilinen beceriyi birleştirme, daha az ipucu, transfer ve karmaşık uygulama ölçer.

Kullanıcı basit seviyede sürekli zorlanırken zor soruya itilmez. Basit seviyeyi rahat geçen kullanıcıya gereksiz sayıda kolay soru çözdürülmez.

Kritik Skill mastery yalnız kolay sorularla kazanılamaz; kesin minimum evidence gate 2E'de tanımlanacaktır.

---

# 9. Öğrenme hedefi: kritik beceri süre doldu diye terk edilmeyecek

Kariyer rotası için hard prerequisite niteliğindeki bir beceri yalnız `bu konuda çok vakit geçti` gerekçesiyle atlanmayacaktır.

Kullanıcı öğrenemiyorsa yöntem değişir:

- daha sade anlatım,
- farklı örnek/analogy,
- worked example,
- daha küçük micro-drill,
- debugging,
- prerequisite'e kısa geri dönüş,
- AI Tutor ile kişisel açıklama,
- farklı evidence türüyle yeniden test.

Kritik prerequisite zayıfsa ona bağımlı ileri dal bekler.

Ancak bağımsız dallar, örneğin uygun Linux veya Technical English çalışmaları, devam edebilir. Tek bir zayıf Skill bütün programı durdurmaz.

Opsiyonel/düşük ROI bir konu ileride product/curriculum kararıyla deferred olabilir; ancak bu davranış kritik temeli süre dolduğu için yok saymak anlamına gelmez.

---

# 10. Başarısız test günlük çalışma süresini kontrolsüz büyütmez

Planner'ın görevi tüm yeni remediation işlerini mevcut planın üstüne eklemek değildir.

Bağlayıcı kural:

> Remediation ve retention işleri günlük kapasitenin içine yerleştirilir; gerekirse daha düşük öncelikli yeni/practice görevleri ertelenir veya azaltılır.

Örnek:

Kullanıcının 90 dakikalık günlük kapasitesi varsa 25 dakikalık yeni remediation oluştuğunda planın varsayılan hedefi 115 dakikaya çıkmak değil, 90 dakika civarında yeniden paketlenmektir.

Bu nedenle ürün sabit `Salı = Topic X, Çarşamba = Topic Y` takvimi değildir. Günler sonsuza kadar bir gün sağa ötelenmez; her plan güncel state'e göre yeniden üretilir.

Kesin capacity, overflow ve priority algoritması 3A–3F'de tasarlanacaktır.

---

# 11. Gecikmeli test / retention aylar sonra da yapılabilir

Bir Skill bir kere mastered oldu diye sonsuza kadar kalıcı kabul edilmez.

Sistem bir Skill'i günler, haftalar veya aylar sonra yeniden doğrulayabilir. Kesin aralıklar 2F'de belirlenecektir.

İki ay sonra retention sorusu yanlış yapılırsa:

1. tek yanlış bütün mastery'yi sıfırlamaz,
2. farklı bir soru/evidence ile doğrulama yapılabilir,
3. tekrarlanan başarısızlık gerçek unutma sinyalini güçlendirir,
4. Skill `weakening/at risk` benzeri duruma geçebilir,
5. hedefli kısa remediation günlük plana girer,
6. tekrar başarı sonrası Skill yeniden güçlenir,
7. sonraki review interval'i gerekirse kısalabilir.

Kullanıcının ileri Topic'lerde eski Skill'i doğal olarak başarıyla kullanması da uygun koşullarda retention evidence sayılabilir. Böylece gereksiz ayrı tekrar soruları azaltılabilir.

---

# 12. Bilgi/öğretim havuzu önceden doğrulanmış çekirdeğe sahip olacak

V1'in ana curriculum ve öğretim omurgası AI tarafından her gün sıfırdan uydurulmayacaktır.

Her önemli Skill/Objective için mümkün olduğunca önceden hazırlanmış/doğrulanmış içerik türleri bulunabilir:

- kısa anlatım,
- ayrıntılı anlatım,
- worked example,
- yaygın hatalar,
- alternatif/remediation anlatımı,
- practice materyali.

İlk gerçek release için ilk 8–12 haftalık curriculum üretim kalitesinde hazırlanacaktır.

AI gerektiğinde farklı açıklama, kişiselleştirilmiş örnek ve remediation içeriği üretebilir; ancak curriculum'un kapsam/prerequisite gerçekliğini keyfi değiştiremez.

---

# 13. Soru havuzu: doğrulanmış çekirdek + AI ile kontrollü genişleme

Bütün soruların her instance'ını elle yazmak zorunlu değildir.

Soru sistemi şu katmanları desteklemelidir:

- doğrulanmış temel sorular,
- question families,
- parametrik varyasyonlar,
- gerçekten farklı problem yapıları,
- AI-generated kişisel practice/remediation/transfer soruları.

AI'nın ürettiği her soru aynı güven düzeyinde değildir.

Düşük riskli kullanımda AI üretimi daha rahat kullanılabilir:

- practice,
- ek örnek,
- remediation,
- kişisel alıştırma.

Mastery'yi güçlü biçimde değiştiren weekly/monthly veya high-stakes assessment için AI-generated item doğrulama/validator/rubric sürecinden geçmeden güçlü evidence sayılmamalıdır.

Kesin Question Bank schema ve validator 4D–4E'de tasarlanacaktır.

---

# 14. Kullanıcıya özel hata/misconception hafızası tutulacak

Genel Question Bank ile kullanıcının kişisel hata geçmişi birbirinden ayrılır.

Sistem mümkün olduğunda yalnız `Pointers zayıf` demek yerine daha spesifik sinyal tutmalıdır.

Örnek:

- address/value confusion,
- uninitialized pointer misconception,
- dereference syntax problemi,
- coding doğru fakat debugging zayıf.

Bu sinyaller remediation ve soru seçiminde kullanılabilir.

Kesin misconception taxonomy ileriki assessment/tutor adımlarında genişletilecektir.

---

# 15. Planner/backend mantığı: olaydan plana açıklanabilir zincir

Kavramsal ana zincir:

`User Attempt → Evidence → Skill Mastery/Retention → Prerequisite + Remediation değerlendirmesi → Planner priority → DailyPlan`

DailyPlan yalnız kullanıcıya gösterilen metin değildir; sistemde kayıtlanabilen plan verisidir.

Plan task'leri en az kavramsal olarak şunları taşıyabilir:

- task/skill/objective,
- tahmini süre,
- task type,
- priority,
- `reason code` / neden bugün seçildi.

Örnek reason:

`weak_skill`, `failed_assessment`, `due_retention`, `independent_branch`, `parallel_english`.

Kesin veri modeli Aşama 8C, planner algoritması Aşama 3'te tasarlanacaktır.

---

# 16. Çekirdek planner/mastery AI API'ye teslim edilmeyecek

AI API entegrasyonu planlanmaktadır; fakat uygulamanın çekirdek öğrenme durumu tek başına LLM çıktısına bağlı değildir.

Deterministik/test edilebilir uygulama mantığında kalması hedeflenenler:

- canonical curriculum/graph,
- prerequisite kuralları,
- mastery state hesaplama mantığı,
- retention state,
- planner candidate/priority mantığı,
- progress history.

AI'nın güçlü olduğu destek alanları:

- alternatif anlatım,
- kişisel ipucu,
- açık uçlu cevap değerlendirmesine yardımcı olma,
- kod feedback,
- hata/kök neden analizi,
- yeni soru varyasyonu,
- transfer/comprehension sorusu.

AI `Bence öğrendi` diyerek hard prerequisite'i keyfi aşamaz.

---

# 17. AI provider/model kalıcı olarak henüz kilitlenmedi

Uygulamada provider-independent AI adapter/model router yaklaşımı hedeflenmektedir.

Bugün iyi görünen belirli bir model gelecekte değişebilir. Bu nedenle ürünün kalıcı öğrenme davranışı tek bir model adına bağlanmayacaktır.

Model seçimi, maliyet, kalite, latency ve değerlendirme başarısı **8E — AI entegrasyon mimarisi** sırasında kesinleştirilecektir.

Basit/deterministik işler gereksiz AI API çağrısı yapmamalıdır. Quiz answer check, graph lock, planner rule, progress lookup gibi işler mümkün olduğunda local/deterministic çalışarak hem maliyeti hem belirsizliği azaltır.

---

# 18. API key yaklaşımı henüz final mimari kararı değildir

Mobil APK içine sabit/hardcoded gizli API key koymak varsayılan final tasarım değildir.

Kişisel prototipte kullanıcının kendi key'ini uygulama ayarından girmesi ve cihazın güvenli saklama mekanizmasında tutulması değerlendirilebilir. Final güvenlik/proxy/backend kararı 8E'de teknik olarak kesinleştirilecektir.

Bu konu öğrenme motorunun çalışmasını bloke etmez.

---

# 19. Şimdiden kilitlenmeyen sayısal detaylar

Bu soru-cevaplarda davranış yönü netleşmiştir; ancak aşağıdaki değerler keyfi olarak kilitlenmemiştir:

- mastery için tam yüzde/threshold,
- easy/medium/hard kesin oranları,
- kaç farklı evidence zorunlu,
- retention'ın 3/7/14/60 gün gibi kesin aralıkları,
- tek yanlışta mastery'nin kaç puan düşeceği,
- remediation'ın kaç dakika olacağı,
- günlük kapasite overflow toleransı,
- AI-generated sorunun hangi confidence ile mastery'ye gireceği,
- belirli AI provider/model seçimi,
- aylık API bütçesi.

Bunlar ilgili 2B–2F, Aşama 3, Aşama 4, Aşama 8E ve Aşama 17 kalibrasyon adımlarında araştırma/simülasyon/QA/pilot ile belirlenir.

---

# 20. Özet bağlayıcı ilkeler

1. Uygulama öğretir; yalnız test etmez.
2. Gerekli temel Learning Objective'ler coverage'da atlanmaz.
3. Coverage mastery değildir.
4. Kritik Skill tek kolay quiz ile mastered olmaz.
5. Yanlış cevap kişiselleştirilmiş öğrenme sinyalidir.
6. Aynı yanlış soru hemen birebir tekrar edilerek ezber ödüllendirilmez.
7. Öğretilmemiş prerequisite isteyen soru kullanıcıyı değerlendiremez.
8. Zorluk öğrenilmiş kavramların daha karmaşık kullanımından gelir.
9. Kritik prerequisite süre doldu diye terk edilmez; yöntem değişir.
10. Bir zayıf Skill yalnız bağımlı dalı bekletir; bütün programı değil.
11. Remediation günlük süreyi kontrolsüz büyütmez; planner yeniden paketler.
12. Mastered Skill'ler aylar sonra yeniden ölçülebilir.
13. Retention başarısızlığı tüm konuyu sıfırlamaz; doğrula, hedefli onar, yeniden test et.
14. Bilgi bankası doğrulanmış çekirdeğe, soru bankası doğrulanmış çekirdek + kontrollü varyasyonlara dayanır.
15. AI yardımcı öğretmen/evaluator'dır; curriculum/mastery/planner'ın keyfi sahibi değildir.
16. Exact formüller ve model seçimleri uygun ileriki aşamada ayrı ayrı kilitlenecektir.
