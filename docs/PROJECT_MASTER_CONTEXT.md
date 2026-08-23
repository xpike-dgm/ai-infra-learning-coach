# PROJECT MASTER CONTEXT — Uzun Proje Amacı, Felsefe ve Ürün Tanımı

Bu dosya projenin **uzun biçimli ana bağlam belgesidir**. Sohbet geçmişi kaybolsa veya proje başka bir ChatGPT/coding agent oturumuna taşınsa bile, bu dosya okunarak projenin neden var olduğu, neyi çözmek istediği, hangi deneyimi hedeflediği ve hangi kararların arkasında hangi mantığın bulunduğu yeniden kurulabilmelidir.

---

# 1. Projenin Kökeni

Başlangıç problemi, 2026 sonrasında yapay zekâ araçlarının yazılım geliştirme, hata ayıklama, veritabanı sorunlarını çözme, backend kurma, deployment yapma ve benzeri birçok teknik görevi giderek daha fazla otomatikleştirmesi karşısında uzun vadeli kariyer için hangi teknik alana yatırım yapılmasının mantıklı olduğuydu.

Bu amaçla yapılan kapsamlı araştırmalardan çıkan ortak yön, yalnızca yüksek seviyeli uygulama geliştirmeye veya belirli framework/syntax bilgisine yatırım yapmak yerine, yapay zekâ sistemlerinin çalıştığı daha zor ve fiziksel/altyapısal katmanlara yönelmenin daha dayanıklı olduğu yönündeydi.

Seçilen uzun vadeli uzmanlaşma yönü:

**Low-Level Systems → Distributed Systems → GPU/CUDA → AI Infrastructure / ML Systems / GPU Systems**

Öğrenme omurgası:

**C → Linux → Modern C++ → Operating Systems / Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Machine Learning tamamen dışlanmayacaktır. Ancak ana kariyer hedefi klasik model eğitme tarafı değil, modellerin nasıl çalıştırıldığı, hızlandırıldığı, servis edildiği, dağıtıldığı, optimize edildiği ve yüksek performanslı altyapı üzerinde nasıl ölçeklendiği tarafıdır.

---

# 2. İngilizce Gerçeği ve Paralel Eğitim Kararı

Başlangıç İngilizce seviyesi A0/sıfır kabul edilmektedir.

Kritik karar: İngilizce teknik eğitime başlamadan önce bitirilmesi gereken ayrı bir ön koşul değildir.

Yanlış yaklaşım:

> Önce 1 yıl İngilizce öğren, sonra teknik eğitime başla.

Seçilen yaklaşım:

> İngilizce ve teknik eğitim ilk günden itibaren birlikte ilerler.

Örnek gelişim mantığı:

- A0→A1: temel günlük İngilizce, teknik temel kelimeler, compiler/terminal hata mesajlarını fark etmeye başlama.
- A1→A2: Git, man page, basit dokümantasyon ve kısa teknik açıklamalar.
- A2→B1: GitHub issue/PR, teknik yazılar, RFC ve dokümantasyon.
- B1→B2: NVIDIA/CUDA dokümantasyonu, paper okuma, proje anlatma, teknik mülakat ve global ekip iletişimi.

Uygulama, İngilizceyi ayrı bir menüde duran bağımsız kurs olmaktan çok teknik görevlerin içine entegre etmelidir.

---

# 3. Uygulama Neden Gerekiyor?

Klasik yol haritaları genellikle şöyle görünür:

- 3 ay C öğren.
- Sonraki 3 ay C++ öğren.
- 2 ay Linux çalış.
- Sonra networking öğren.
- Daha sonra CUDA'ya geç.

Bu yaklaşım kullanıcı açısından yetersizdir; çünkü günlük düzeyde şu soruyu cevaplamaz:

> **Bugün tam olarak ne yapacağım?**

Ayrıca takvim temelli program, kullanıcı bir konuyu anlayamadığında bile ilerleyebilir. Örneğin pointer temelleri oturmadan dynamic memory veya linked list konularına geçmek, ilerleme görüntüsü oluşturur ama gerçek öğrenme oluşturmaz.

Bu uygulama bu problemi çözmek için tasarlanmaktadır.

Uygulama kullanıcıya uzun bir kurs listesi değil, her gün **uygulanabilir çalışma seansı** vermelidir.

Örnek günlük deneyim:

- 20 dakika teknik İngilizce
- 25 dakika kısa kavram anlatımı
- 30 dakika uygulama
- 20 dakika debugging
- 10 dakika eski konu tekrarı
- 10 dakika günlük mini değerlendirme

Kullanıcı uygulamayı açtığında “bugün ne çalışsam?” diye karar vermek zorunda kalmamalıdır. Planlama yükünü uygulama üstlenmelidir.

---

# 4. Projenin En Önemli Ürün İlkesi

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Bunun sonucu olarak:

- Bir videoyu izlemek ilerleme sayılmaz.
- Bir ders kartını “tamamlandı” yapmak ilerleme sayılmaz.
- 30 gün uygulamaya girmiş olmak tek başına ilerleme sayılmaz.
- 1095 günlük takvimde gün ilerletmek başarı değildir.

İlerleme, kullanıcının ilgili beceriyi gerçekten gösterebilmesine bağlıdır.

Örneğin bir `Basic Pointers` konusu şu sinyallerle ölçülebilir:

- teori soruları
- kod yazma
- debugging
- kendi cümlesiyle açıklama
- transfer sorusu
- birkaç gün sonra gecikmeli tekrar

Kullanıcı teorik testte %95 yapıp kodlama görevinde başarısızsa konu otomatik olarak öğrenilmiş sayılmamalıdır.

---

# 5. Neden 1095 Gün Sayacı Yok?

Yaklaşık 3 yıllık hedef yalnızca arka plandaki planlama ufkudur.

Kullanıcı arayüzünde:

`Gün 47 / 1095`

veya

`Kariyerin %12 tamamlandı`

gibi ifadeler kullanılmayacaktır.

Çünkü bu göstergeler geçen zamanı gerçek yeterlilikle karıştırır.

Bunun yerine kullanıcı şunları görmelidir:

- bugün ne çalışacağı
- hangi konuda olduğu
- konu hakimiyet düzeyi
- hangi becerilerin güçlü/zayıf olduğu
- hangi tekrarların yaklaştığı
- hangi değerlendirmelerin geleceği

Örnek:

- C / Memory & Pointers
- Basic Pointers — Mastery %64
- Hedef: %80
- Bugünkü plan: 1s 45dk
- Zayıf alan: dereference mantığı

---

# 6. Müfredat Takvim Değil, Knowledge Graph Olmalı

Uygulamanın temel veri yapısı “Gün 1, Gün 2, Gün 3” değildir.

Müfredat bir bilgi grafiği olarak modellenmelidir.

Örnek:

Memory Addresses
→ Basic Pointers
→ Pointer Arithmetic
→ Dynamic Memory
→ Linked Lists

Her node/konu için:

- öğrenme hedefleri
- prerequisite'ler
- mastery durumu
- assessment türleri
- retention geçmişi
- remediation seçenekleri

bulunmalıdır.

Bir prerequisite öğrenilmemişse ona bağlı konu açılmaz.

Fakat bağımsız dallar gereksiz yere durmaz.

Örneğin pointer konusunda zayıflık varsa pointer'a bağlı C konuları bekleyebilirken, paralel Linux veya İngilizce hattı devam edebilir.

---

# 7. Adaptif Öğrenme Motorunun Rolü

Adaptif öğrenme motoru uygulamanın karar mekanizmasıdır.

Görevi:

> Kullanıcının geçmiş performansına bakıp bugün hangi görevin ne kadar süreyle ve hangi sırada verilmesi gerektiğine karar vermek.

Girdi sinyalleri ileride şunları içerebilir:

- teori quiz sonucu
- coding task sonucu
- debugging başarısı
- açıklayabilme/Feynman değerlendirmesi
- gecikmeli tekrar sonucu
- cevaplama süresi
- kaç ipucu kullanıldığı
- AI yardım miktarı
- prerequisite topic mastery
- çalışma kapasitesi
- son haftalık/aylık sınav sonucu

Motorun davranışı açıklanabilir olmalıdır.

Örnek:

> “Bugün pointer tekrarına 25 dakika eklendi çünkü son iki debugging görevinde pointer dereference hataları yaptın ve 7 günlük retention testin %52 çıktı.”

Uygulama rastgele veya sadece LLM kararına dayalı bir sistem olmamalıdır. Temel planner kuralları deterministik/ölçülebilir olmalı; AI daha çok açıklama, içerik çeşitlendirme ve tutor görevlerinde kullanılabilir.

---

# 8. Haftalık ve Aylık Sınavların Gerçek Görevi

Sınavlar yalnızca not göstermek için yapılmayacaktır.

Haftalık sınav örneği:

- teori
- coding
- debugging
- teknik İngilizce
- eski konulardan retrieval

Sonuç:

- Functions %91
- Arrays %84
- Memory %58
- Pointers %49
- Linux %88
- English %71

Sistem buradan bir sonraki haftayı değiştirmelidir.

Örneğin:

- pointer tekrar süresini artır
- memory için remediation ekle
- pointer prerequisite isteyen yeni konuyu ertele
- Linux hattına normal devam et

Aylık sınav daha geniş yeterlilik değerlendirmesi olmalıdır:

- teori
- uygulamalı coding
- debugging
- kendi cümlesiyle teknik açıklama
- teknik İngilizce
- retention

---

# 9. Unutma ve Spaced Repetition

Bir konu bir kez yüksek puan aldı diye sonsuza kadar tamamlandı kabul edilmemelidir.

Uygulama belirli aralıklarla tekrar ölçüm yapmalıdır.

Yaklaşık mantık:

- kısa aralık
- birkaç gün sonra
- 1 hafta civarı
- birkaç hafta
- 1 ay
- daha uzun dönem

Kesin algoritma daha sonra tasarlanacaktır.

Retention düşerse topic mastery yeniden düşebilir ve konu plana geri girebilir.

Bu sistemin amacı kurs tamamlama değil, uzun süreli gerçek bilgi tutma olmalıdır.

---

# 10. Remediation: Öğrenemeyince Ne Olacak?

Kullanıcı bir konuyu anlamadığında aynı içeriği tekrar tekrar göstermenin yeterli olmadığı kabul edilmektedir.

Sistem farklı müdahale türleri kullanmalıdır:

- daha basit açıklama
- farklı örnek
- görsel/şematik anlatım
- mikro alıştırma
- kod tamamlama
- debugging
- yanlış örnek analizi
- kendi cümlesiyle açıklama
- prerequisite geri dönüşü

Örneğin pointer konusunda üç kez başarısız olan kullanıcıya sadece aynı pointer dersini yeniden göstermek yerine, `memory address` prerequisite'ine geri dönmek gerekebilir.

---

# 11. AI Tutor'un Rolü

AI uygulamanın tamamı değildir. AI, öğrenme motorunun üzerinde çalışan öğretmen katmanıdır.

AI Tutor ileride:

- kullanıcı seviyesine göre konu anlatabilir
- ipucu verebilir
- doğrudan cevabı söylemeden Socratic yönlendirme yapabilir
- yanlışın kök nedenini analiz edebilir
- farklı örnek üretebilir
- açık uçlu cevapları değerlendirebilir
- kodu açıklatabilir
- debugging görevi oluşturabilir

AI kullanıcının kodunu yazabilir; ancak bu durumda mastery verilmemelidir.

Örneğin kullanıcı AI ile bir kodlama görevini tamamladıysa sistem şunları sorabilir:

- Bu satır neden burada?
- Bu `free()` kaldırılırsa ne olur?
- Bu değişkenin lifetime'ı nedir?
- Burada race condition olabilir mi?
- Aynı mantığı farklı veri yapısında uygula.

Amaç AI kullanımını yasaklamak değil, **AI'nın kullanıcı yerine öğrenmesini engellemektir**.

---

# 12. Günlük Kullanıcı Deneyimi

Ana ekranın temel sorusu:

> **Bugün ne yapmalıyım?**

Ana ekran mümkün olduğunca sade olmalıdır.

Öncelikli öğeler:

1. Bugünkü toplam çalışma süresi
2. Devam eden konu
3. Günlük görev kartları
4. Mastery durumu
5. Yaklaşan değerlendirme
6. Zayıf veya güçlendirilmesi gereken alan
7. Tek ana CTA: Çalışmaya Başla / Devam Et

Uygulama dashboard kalabalığına dönüşmemelidir.

---

# 13. Kişisel Kullanım Kapsamı

Uygulama şu an yalnızca tek kullanıcı için geliştirilecektir.

Bu nedenle ilk sürümde gereksizdir:

- auth/login sistemi
- kullanıcı profilleri sistemi
- organizasyonlar
- arkadaş ekleme
- sosyal feed
- ödeme
- abonelik
- admin paneli
- multi-tenant backend
- kurumsal rol yetkilendirme

Bu karar, geliştirme süresini doğrudan öğrenme deneyimine ayırmak içindir.

Bununla birlikte veri kaybını önleme, local persistence, backup/export ve stabilite önemlidir.

---

# 14. Tasarım Beklentisi

Uygulama profesyonel ve modern görünmelidir.

Beklentiler:

- mobil odaklı
- sade
- temiz tipografi
- açık/koyu tema düşünülebilir
- iyi spacing
- görsel hierarchy
- küçük ama anlamlı animasyonlar
- skill/mastery odaklı grafikler
- gereksiz gamification yok

Streak olabilir; ancak ana başarı metriği olmamalıdır.

Kullanıcı 3 gün ara verdiğinde uygulama onu cezalandırmak yerine planı yeniden hesaplamalıdır.

---

# 15. İlk Curriculum Paketi Neden 3 Yıl Değil?

Uygulama kullanılmaya başlamadan önce tüm 3 yıllık ders içeriğini üretmek gereksiz ve risklidir.

İlk hedef:

- öğrenme motoru
- knowledge graph
- assessment sistemi
- adaptive planner
- ilk 8–12 haftalık yüksek kaliteli curriculum

Bu ilk dönem gerçek kullanımla doğrulandıktan sonra sonraki modüller eklenir.

Bu sayede yanlış pedagojik kararlar 3 yıllık dev içerik üretildikten sonra fark edilmez.

---

# 16. Uzun Vadeli Curriculum Omurgası

Ana teknik alanlar:

1. Technical English
2. Computer Fundamentals
3. C Foundations
4. Memory Foundations
5. Linux
6. Data Structures & Algorithms
7. Modern C++
8. Operating Systems
9. Concurrency
10. Networking
11. Distributed Systems
12. GPU Architecture
13. CUDA
14. Triton
15. ML/LLM Systems Fundamentals
16. LLM Inference Engines
17. Multi-GPU / NCCL / RDMA
18. AI Infrastructure
19. Open Source Contribution
20. Career Readiness / Technical Interview

Bu sıra sabit zaman dilimleri anlamına gelmez. Süre, kullanıcının mastery hızına göre değişebilir.

---

# 17. İlk İş ve Kariyer Köprüsü

Uzun vadeli hedef AI Infrastructure olsa da ilk işin doğrudan CUDA Engineer olması zorunlu değildir.

Muhtemel köprü roller:

- C++ Systems Engineer
- Systems Software Engineer
- Linux/Infrastructure Engineer
- Performance Engineer
- Distributed Systems Engineer
- uygun SRE/Cloud Infrastructure rolü

Türkiye'de C/C++/Linux ve systems tarafı başlangıç köprüsü olabilir. Sonrasında GPU/CUDA ve global AI Infrastructure tarafına yönelmek hedeflenmektedir.

---

# 18. Uygulamanın Başarılı Sayılması İçin Temel Felsefi Kriter

Bu uygulama kullanıcının yalnızca daha fazla içerik tüketmesine neden oluyorsa başarısızdır.

Başarılı uygulama:

- kullanıcının karar yükünü azaltır
- doğru sırada öğrenmesini sağlar
- öğrenmediği konuyu saklamaz
- zayıflığı tespit eder
- unutmayı fark eder
- günlük programı buna göre değiştirir
- AI kullanımına rağmen gerçek anlama seviyesini ölçer
- uzun vadede kullanıcıyı gerçek teknik beceri ve işe hazır portföye taşır

---

# 19. Geliştirme Felsefesi

Kodlamaya başlamadan önce öğrenme ve ürün kuralları yeterince netleştirilecektir.

Ana sıralama:

1. Ürün kapsamını kilitle.
2. Mastery modelini tasarla.
3. Adaptive planner kurallarını tasarla.
4. Assessment sistemini tasarla.
5. Curriculum graph yapısını kur.
6. English track'i kesinleştir.
7. UI/UX akışlarını tasarla.
8. Teknik mimariyi seç.
9. Uygulamayı geliştir.
10. Gerçek kullanıcı pilotu ile kalibre et.
11. Release APK üret.
12. Curriculum'u zamanla genişlet.

Ayrıntılı uygulama planı `docs/MASTER_PLAN.md` içindedir.

---

# 20. Proje Hafızası Kuralı

Bu proje uzun süreli olduğundan sohbet bağlamına güvenilmeyecektir.

Kalıcı kaynaklar:

- `docs/PROJECT_MASTER_CONTEXT.md` — bu dosya, uzun ürün bağlamı.
- `docs/HANDOFF_STATE.md` — güncel proje durumu ve sıradaki kesin adım.
- `PROJECT_CONTEXT.md` — önceki ana bağlam özeti.
- `docs/DECISIONS.md` — alınan kalıcı kararlar.
- `docs/MASTER_PLAN.md` — aşama/adım planı.
- `docs/PROGRESS_LOG.md` — kronolojik ilerleme.

Yeni bir önemli karar alındığında yalnız sohbet içinde bırakılmamalıdır; ilgili GitHub dokümanına yazılmalıdır.

Bu dosyanın amacı, aylar sonra projeye dönüldüğünde bile “biz ne yapıyorduk?” sorusunun tekrar sorulmamasını sağlamaktır.
