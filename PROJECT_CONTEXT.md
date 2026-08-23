# Project Context — Kalıcı Proje Hafızası

Bu dosya, sohbet bağlamı kaybolsa bile projenin neden var olduğunu ve hangi kararların verildiğini yeniden kurabilmek için tutulur.

## 1. Başlangıç Noktası

Amaç, yapay zekâ çağında uzun vadeli ve zor ikame edilen bir teknik kariyer rotası seçmekti. Yapılan iki kapsamlı araştırmanın ortak yönü, yüzeysel uygulama geliştirme yerine **low-level systems / AI infrastructure** tarafına ilerlemenin daha dayanıklı bir uzmanlık oluşturduğu yönündeydi.

Seçilen ana rota:

**C → Linux → Modern C++ → Operating Systems / Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Machine Learning tamamen atlanmayacak; ancak ana uzmanlık olarak klasik ML yerine, modellerin nasıl çalıştırıldığı, optimize edildiği ve ölçeklendiği taraf öğrenilecek.

## 2. İngilizce Durumu

Başlangıç İngilizce seviyesi: **A0 / sıfır**.

Karar: İngilizce teknik eğitimin ön koşulu yapılmayacak. İngilizce ve teknik eğitim aynı anda ilerleyecek.

Hedef mantık:

- A0→A1: temel İngilizce + derleyici/terminal mesajları
- A1→A2: dokümantasyon, Git, man page okuma
- A2→B1: GitHub issue/PR, teknik yazı ve RFC okuma
- B1→B2: teknik dokümantasyon, paper, proje anlatımı ve mülakat

İngilizce, genel kurstan bağımsız bir blok olarak değil, günlük teknik görevlerle entegre edilmelidir.

## 3. Uygulamanın Ortaya Çıkış Nedeni

Uzun vadeli müfredatın “4 ay bunu çalış, sonra şuna geç” şeklinde olması istenmiyor. Kullanıcı uygulamayı açtığında **o gün tam olarak ne yapacağını** görmeli.

Beklenen deneyim:

- Günlük görevler dakika bazında planlanır.
- Her görevden sonra kısa ölçme yapılabilir.
- Haftalık sınav vardır.
- Ay sonunda kapsamlı yeterlilik sınavı vardır.
- Eksik konu sonraki haftaya/aya taşınır.
- Eksik konunun prerequisite olduğu yeni konu ertelenir.
- Bağımsız konular gereksiz yere durdurulmaz.
- Eski konular gecikmeli tekrarlarla yeniden test edilir.
- Kullanıcı hızlı öğrenirse plan hızlanabilir; zorlanırsa yavaşlar.
- Birkaç gün ara verilirse sistem cezalandırmak yerine programı yeniden hesaplar.

## 4. En Kritik Ürün Kararı

**1095 gün sayacı kullanıcı arayüzünde gösterilmeyecek.**

Üç yıllık hedef yaklaşık bir planlama ufkudur; gerçek ilerleme değildir.

Ana prensip:

> Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.

Bir dersin okunması/izlenmesi/işaretlenmesi tek başına ilerleme sağlamaz.

Örneğin Pointer Temelleri için:

- teori testi
- kodlama görevi
- debugging görevi
- kendi cümlesiyle açıklama
- birkaç gün sonraki tekrar

ölçümlerinin toplamı yeterli değilse konu **öğrenilmedi** sayılır ve bağımlı konu açılmaz.

## 5. Ana Ekran Felsefesi

Ana ekran kariyerin yüzde kaçının veya kaç günün geçtiğini göstermeye odaklanmaz.

Öncelik sırası:

1. Bugünkü çalışma
2. Devam eden konu
3. Konu hakimiyeti
4. Bugünkü görevler ve süreleri
5. Yaklaşan değerlendirme
6. Zayıf / güçlendirilmesi gereken alanlar

Örnek:

- Devam eden konu: C / Bellek ve Pointerlar
- Pointer Temelleri — Hakimiyet %64
- Hedef %80
- “Çalışmaya Devam Et”

## 6. Bilgi Haritası, Takvim Değil

Müfredat bir knowledge graph olarak modellenmelidir.

Örnek:

Memory addresses
→ Basic pointers
→ Pointer arithmetic
→ Dynamic memory
→ Data structures

Bir konu prerequisite ise yeterli mastery olmadan sonraki konu açılmaz. Takvim yalnızca “bugün ne kadar çalışacağız?” sorusunu cevaplar; knowledge graph ise “bugün ne çalışmalıyız?” sorusunu cevaplar.

## 7. Adaptif Öğrenme

Uygulama yalnızca sınav notuna bakmamalı. İleride mastery hesabında şu sinyaller kullanılabilir:

- teori quiz başarısı
- kodlama görevi başarısı
- debugging başarısı
- açıklayabilme
- gecikmeli tekrar
- harcanan süre
- ipucu/AI yardım miktarı
- proje performansı
- önceki prerequisite konuların durumu

Kullanıcı AI ile kod yazdırabilir, fakat uygulama anladığını kanıtlaması için kodu açıklatmalı ve varyasyon soruları sormalıdır.

## 8. Değerlendirme Sistemi

### Günlük

- mikro quiz
- kısa uygulama
- eski konudan tekrar

### Haftalık

- teori
- kodlama
- hata bulma/debugging
- İngilizce
- eski konular

Hafta sınavında zayıf kalan alanlar sonraki haftaya eklenir; bu alanlara bağımlı yeni konular ileri kayar.

### Aylık

Daha geniş yeterlilik sınavı:

- teorik bölüm
- uygulamalı kodlama
- debugging
- açıklama / Feynman tarzı anlatım
- teknik İngilizce
- eski konulardan retention testi

## 9. Spaced Repetition

Bir konu bir kez başarılı oldu diye kalıcı tamamlandı sayılmaz. Uygulama konuya göre tekrar pencereleri oluşturmalıdır. Yaklaşık örnek: 1 gün, birkaç gün, 1 hafta, birkaç hafta, 1 ay, daha uzun aralıklar. Kesin algoritma daha sonra tasarlanacak.

## 10. Kariyer Hedefi

Ana hedef: **AI Infrastructure / ML Systems / GPU Systems**.

İlk işe girişin doğrudan CUDA Engineer olması şart değil. Daha gerçekçi köprü roller:

- C++ Systems Engineer
- Systems Software Engineer
- Linux/Infrastructure Engineer
- Cloud/SRE (uygun sistem derinliğiyle)
- Distributed Systems Engineer
- Performance Engineer

Sonraki uzmanlaşma: GPU Architecture, CUDA/Triton, inference engines, NCCL/RDMA, vLLM/SGLang benzeri sistemler.

## 11. Kişisel Kullanım Kısıtı

Bu ürün tek kişi için geliştirilecek.

Şimdilik gereksiz olanlar:

- kullanıcı kayıt/giriş sistemi
- sosyal özellikler
- arkadaşlar
- ödeme/abonelik
- admin paneli
- çok kiracılı mimari
- kurumsal RBAC
- gereksiz backend karmaşıklığı

Veri güvenliği ve veri kaybını önleme yine önemlidir; ancak ürün mimarisi çok kullanıcılı SaaS gibi tasarlanmayacaktır.

## 12. Tasarım Beklentisi

- modern
- profesyonel
- sade
- mobil odaklı
- açık/koyu tema düşünülebilir
- büyük ve anlaşılır ana CTA: günlük çalışmayı başlat
- gereksiz dashboard kalabalığı yok
- ilerleme grafikleri gün sayısı yerine mastery/skill odaklı

## 13. Geliştirme Stratejisi

İlk sürümde 3 yıllık tüm içerik elle tamamlanmak zorunda değildir. Önce öğrenme motoru ve ilk müfredat parçası doğru kurulmalı. Müfredat uygulama kodundan ayrı veri/model katmanında tutulmalıdır.

Önerilen yaklaşım:

1. Ürün ve öğrenme motorunu netleştir.
2. Knowledge graph veri modelini kur.
3. İlk 8–12 haftalık içerikle motoru doğrula.
4. Gerçek kullanım geri bildirimine göre adaptasyon algoritmasını düzelt.
5. Sonraki curriculum modüllerini ekle.

## 14. Araştırmalardan Alınan Kritik Dersler

- Doğrudan CUDA'ya atlamak yerine aşamalı systems yaklaşımı daha mantıklı.
- C/C++, Linux, memory, OS ve concurrency GPU optimizasyonunun temelidir.
- Türkiye’de C/C++/Linux sistem rolleri başlangıç köprüsü olabilir.
- Global AI infrastructure için güçlü teknik İngilizce ve açık kaynak katkısı önemlidir.
- 12 aylık yoğun araştırma yol haritaları çok agresif olabilir; konu sırası değerlidir ama süreler kişiye göre adaptif olmalıdır.

## 15. Bu Dosya Nasıl Kullanılmalı?

Yeni bir AI/agent projeye dahil olduğunda önce bu dosyayı, ardından `docs/DECISIONS.md`, `docs/PRODUCT_VISION.md` ve `docs/LEARNING_ENGINE.md` dosyalarını okumalıdır. Yeni kalıcı kararlar burada veya `docs/DECISIONS.md` içinde güncellenmelidir.
