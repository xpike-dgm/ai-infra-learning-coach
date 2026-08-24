# Non-Goals — AI Infra Learning Coach

**Adım:** 1D — Non-goals  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge, projenin bilinçli olarak **ne olmayacağını** tanımlar. Amaç iyi fikirleri yasaklamak değil; ürünün ana amacını korumak, V1'i kontrolsüz büyütmemek ve gelecekte başka bir sohbet/agent tarafından kapsamın yanlışlıkla değiştirilmesini önlemektir.

Non-goal iki farklı anlama gelebilir:

1. **Ürün seviyesi non-goal:** Ürünün temel kimliğiyle çeliştiği için varsayılan olarak yapılmayacak şeyler.
2. **V1 non-goal / deferred:** Gelecekte değerlendirilebilir fakat ilk release'in şartı olmayan şeyler.

Bir non-goal gelecekte değiştirilecekse bu sessizce yapılmaz; gerekçe `docs/DECISIONS.md` içine yeni karar olarak kaydedilir.

---

# A. ÜRÜN SEVİYESİNDE NON-GOALS

## NG-01 — Zamanı ilerleme gibi göstermek

Uygulamanın amacı kullanıcının kaç gün çalıştığını veya kaç saat uygulamada kaldığını başarı olarak göstermek değildir.

Bu nedenle ana ilerleme modeli olmayacak şeyler:

- `Gün 47 / 1095`,
- yalnız çalışma saati,
- yalnız ders tamamlama yüzdesi,
- yalnız task checkbox sayısı,
- yalnız streak.

Zaman ve çalışma süresi analitik olarak saklanabilir; fakat **mastery yerine geçmez**.

## NG-02 — Sabit takvimli kurs olmak

Ürün `1. gün konu A, 2. gün konu B, 3. gün konu C` şeklinde herkese aynı sırayı zorlayan statik bir kurs olmayacaktır.

Curriculum'un ana yönü sabit olabilir; fakat günlük görev ve ilerleme prerequisite, mastery, retention, assessment ve kullanılabilir süreye göre değişecektir.

## NG-03 — “Dersi bitirdi = öğrendi” sistemi olmak

Bir metni okumak, videoyu bitirmek, görevi işaretlemek veya bir kez yüksek quiz puanı almak kritik beceriler için otomatik mastery üretmeyecektir.

Ürün gerçek öğrenmeyi çoklu kanıtlarla doğrulamayı hedefler.

## NG-04 — Tamamen LLM tarafından yönetilen curriculum olmak

LLM/AI:

- açıklama,
- soru üretimi,
- feedback,
- alternatif anlatım,
- açık uçlu değerlendirme

gibi alanlarda kullanılabilir.

Ancak AI'nın keyfi biçimde:

- prerequisite atlaması,
- mastery vermesi,
- tüm curriculum'u yeniden yazması,
- kritik planner kurallarını değiştirmesi

ürünün hedefi değildir.

Çekirdek öğrenme kuralları mümkün olduğunca deterministik, test edilebilir ve açıklanabilir kalacaktır.

## NG-05 — AI'nın kullanıcı yerine öğrenmesi

Kullanıcı dış AI araçlarını kullanabilir; ancak ürünün amacı AI'ya kod yazdırıp görevi otomatik tamamlamak değildir.

AI yardımının ardından gerektiğinde comprehension, explanation, debugging veya transfer doğrulaması yapılır.

## NG-06 — Genel amaçlı “her şeyi öğreten” eğitim platformu olmak

Ürün ilk etapta matematikten tarihe kadar bütün alanları öğreten genel eğitim uygulamasına dönüşmeyecektir.

Ana hedef rota:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

## NG-07 — Sosyal ağ olmak

Ürünün temel değeri topluluk ve sosyal etkileşim değildir.

Varsayılan kapsam dışında:

- arkadaş sistemi,
- takipçi sistemi,
- public profile,
- sosyal feed,
- kullanıcılar arası mesajlaşma,
- rekabetçi leaderboard.

## NG-08 — Ticari SaaS ürünü olmak

Bu proje kişisel kullanım içindir. Ürünü ilk günden ticari SaaS gibi tasarlamak hedef değildir.

Kapsam dışında:

- abonelik,
- ödeme,
- faturalandırma,
- organizasyon,
- takım üyeleri,
- rol/RBAC,
- tenant izolasyonu,
- admin/customer-support paneli,
- growth/marketing altyapısı.

## NG-09 — Gamification'ı öğrenmenin önüne geçirmek

XP, coin, level, badge veya streak ileride küçük motivasyon unsurları olarak değerlendirilebilir; ancak mastery'nin yerine geçen ana sistem olmayacaktır.

Özellikle:

- streak kaybetme cezası,
- sahte XP ekonomisi,
- loot/coin döngüsü,
- leaderboard baskısı

ürünün ana karakterine aykırıdır.

## NG-10 — Kaçırılan günleri borç/ceza haline getirmek

Bir kullanıcı birkaç gün çalışmadığında tüm eski günlük görevleri üst üste yığmak hedef değildir.

Sistem mevcut bilgi ve retention durumunu yeniden değerlendirip sağlıklı dönüş planı üretmelidir.

## NG-11 — Telefonu tam bir geliştirme iş istasyonuna çevirmek

Mobil uygulamanın ana görevi öğrenmeyi yönetmek, öğretmek ve ölçmektir; tam IDE olmak değildir.

Telefon içine Visual Studio Code benzeri tam ortam, tam terminal geliştirme sistemi veya karmaşık C/C++ toolchain gömmek ürünün temel hedefi değildir.

Gerçek coding görevleri gerektiğinde PC üzerinde yapılabilir.

## NG-12 — Bilimsel olarak kanıtlanmamış sahte kesinlik üretmek

Mastery `%83.47`, unutma süresi `tam 6.2 gün` veya “bu algoritma öğrenmeyi kesin ölçer” gibi bilimsel dayanağı olmayan kesinlikler sunmak hedef değildir.

Mastery formülü, threshold ve planner katsayıları ürün heuristiği olabilir; bunlar test, simülasyon ve gerçek kullanım ile kalibre edilir ve gerektiğinde değiştirilir.

## NG-13 — İş veya kariyer sonucu garanti etmek

Uygulama kullanıcının teknik yetkinliğini geliştirmeyi hedefler; ancak:

- iş bulma garantisi,
- belirli maaş garantisi,
- belirli sürede CUDA engineer olma garantisi,
- belirli şirketten teklif garantisi

vermez.

Kariyer rotasındaki süreler tahmin/planlama çerçevesidir; performansa göre değişir.

## NG-14 — İngilizceyi teknik eğitimin önünde bariyer yapmak

“Önce B2 İngilizce ol, sonra programlamaya başla” modeli kullanılmayacaktır.

İngilizce ve teknik eğitim paralel ilerler.

---

# B. V1 İÇİN NON-GOALS / SONRAYA BIRAKILANLAR

Aşağıdaki maddeler ürün kimliğiyle zorunlu olarak çelişmez; ancak **V1 release şartı değildir**.

## NG-V1-01 — 3 yıllık curriculum'un tamamını release öncesi üretmek

İlk release için tüm Modern C++, OS, Distributed Systems, CUDA, Triton, NCCL/RDMA ve ileri AI Infrastructure içeriğini bitirmek gerekmeyecektir.

İlk production curriculum yaklaşık ilk 8–12 haftalık temel pakettir. Süre sabit kullanıcı takvimi değil, içerik büyüklüğünü ifade eder.

## NG-V1-02 — iOS, web ve desktop istemcileri

V1 Android odaklıdır. Aynı anda dört platform geliştirmek release şartı değildir.

## NG-V1-03 — Cloud account ve gerçek zamanlı multi-device sync

V1 local-first'tür.

Google/Apple hesap sistemi, merkezi kullanıcı backend'i ve telefonlar arası canlı sync ilk release'in zorunlu parçası değildir.

Backup/export/restore ise veri güvenliği nedeniyle V1 kapsamındadır.

## NG-V1-04 — Tam voice-first AI Tutor

Gerçek zamanlı doğal sesli görüşme, pronunciation engine ve sürekli voice tutor V1 release şartı değildir.

## NG-V1-05 — Uygulama içi tam güvenli code execution sandbox

Mobil veya cloud üzerinde arbitrary C/C++ kod derleyip çalıştıran tam sandbox V1 zorunluluğu değildir.

Coding task yine V1'in parçasıdır; execution yöntemi daha sade olabilir.

## NG-V1-06 — Canlı iş ilanı / career-market engine

V1'in ilk release'inde:

- sürekli iş ilanı tarama,
- şirket tavsiye motoru,
- CV ATS optimizasyonu,
- otomatik job skill matching,
- kapsamlı mock interview platformu

zorunlu değildir.

Bunlar Aşama 19 career-readiness katmanında değerlendirilecektir.

## NG-V1-07 — Sosyal/community özellikleri

Forum, arkadaş, paylaşım, public progress ve leaderboard V1'de olmayacaktır.

## NG-V1-08 — Ödeme/abonelik/admin sistemi

Kişisel kullanım nedeniyle V1 bunları içermez.

## NG-V1-09 — Gelişmiş görsel oyunlaştırma sistemi

Coin economy, görev sandıkları, avatar sistemi, achievement mağazası gibi özellikler V1 release şartı değildir.

## NG-V1-10 — Her possible learning science algoritmasını aynı anda uygulamak

V1'de BKT, IRT, Bayesian mastery, FSRS, reinforcement learning ve benzeri bütün yöntemleri aynı anda kullanmak hedef değildir.

Önce açıklanabilir ve test edilebilir bir V0/V1 yaklaşımı kurulur; daha gelişmiş yöntemler ancak gerçek fayda gösteriyorsa eklenir.

## NG-V1-11 — Gereksiz backend ve DevOps karmaşıklığı

Kişisel local-first uygulama için ilk günden Kubernetes, mikroservisler, event bus, multi-region backend gibi altyapı kurmak hedef değildir.

## NG-V1-12 — Kusursuz ve sonsuz AI provider desteği

V1'de her LLM sağlayıcısını desteklemek gerekmez. Mimari provider abstraction'a hazır olabilir; gerçek sağlayıcı sayısı sınırlı tutulabilir.

---

# C. SCOPE CREEP KARAR KURALI

Yeni bir özellik fikri geldiğinde şu sıra kullanılır:

1. Ürünün ana vaadini doğrudan güçlendiriyor mu?
2. V1'in P0/P1 acceptance kriterlerinden birini karşılamak için gerekli mi?
3. Mevcut kapsam olmadan ana öğrenme döngüsü bozuluyor mu?
4. Eklenmesi mevcut kritik adımları anlamlı biçimde geciktiriyor mu?
5. Bu dosyada non-goal/deferred olarak tanımlanmış mı?

Eğer özellik ana öğrenme döngüsü için gerekli değilse ve V1'i geciktiriyorsa varsayılan karar **sonraya bırakmak** olacaktır.

Non-goal listesinden bir maddenin kapsam içine alınması gerekiyorsa:

- gerekçe yazılır,
- etkilenen aşama/adım belirlenir,
- acceptance kriterleri güncellenir,
- `DECISIONS.md` içine değişiklik kaydı eklenir.

---

# 1D Kabul Kontrolü

1D tamamlandı çünkü:

- ürün seviyesi non-goals ile yalnız V1'e ertelenen özellikler ayrıldı,
- mastery'nin önüne geçen zaman/streak/gamification yaklaşımı kapsam dışı bırakıldı,
- sabit kurs ve tamamen LLM kontrollü curriculum reddedildi,
- kişisel uygulamanın SaaS/social/payment platformuna dönüşmesi engellendi,
- tüm 3 yıllık içeriğin V1 release ön koşulu olmadığı tekrar kilitlendi,
- multi-platform, cloud sync, full voice tutor, full IDE/sandbox ve career-market engine V1 sonrasına bırakıldı,
- bilimsel olmayan sahte kesinlik ve kariyer garantisi reddedildi,
- yeni özellikler için scope-creep karar kuralı tanımlandı.

> **Tamamlanma notu — 2026-08-24:** 1D, daha önce 1A–1C ve `V1_SCOPE.md` içinde dağınık halde bulunan kapsam dışı kararları tek kalıcı belgede birleştirerek tamamlandı. Ürün kimliğini koruyan kalıcı non-goals ile yalnız V1'den ertelenen özellikler birbirinden ayrıldı. Bu adımla Aşama 1 — Ürün Çerçevesini Kilitle tamamlandı. Sonraki aktif adım `2A — Bilgi birimleri`dir.
