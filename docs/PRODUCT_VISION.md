# Product Vision

## Ürün Tanımı

AI Infra Learning Coach, kişisel kullanım için tasarlanmış adaptif mobil öğrenme uygulamasıdır. Kullanıcıya uzun bir kurs kataloğu sunmak yerine, o gün ne çalışması gerektiğini seçer, çalıştırır, ölçer ve bir sonraki planı performansa göre yeniden oluşturur.

## Ana Kullanıcı Sorusu

Uygulama her açıldığında tek soruya cevap vermelidir:

> Bugün ne yapmalıyım?

## Ana Ekran

Ana ekranda uzun vadeli gün sayacı yerine şunlar görünmelidir:

- Bugünkü toplam çalışma süresi
- Devam eden ana konu
- O konudaki mastery / hakimiyet
- Bugünkü görev listesi
- Yaklaşan haftalık veya aylık değerlendirme
- Gerekirse kısa bir “program neden değişti?” açıklaması

Örnek:

- C · Bellek ve Pointerlar
- Pointer Temelleri — %64 mastery
- Hedef — %80
- 20 dk görsel anlatım
- 25 dk alıştırma
- 35 dk kodlama görevi
- 25 dk İngilizce
- 10 dk eski konu tekrarı

## Navigasyon

İlk tasarım için sade alt menü:

1. **Bugün**
2. **Yol Haritası**
3. **İlerleme**
4. **Sınavlar**
5. **Ayarlar**

## Bugün Ekranı

Büyük bir `Çalışmaya Başla` CTA'sı bulunmalı. Görevler sıra halinde tamamlanır. Kullanıcının “hangi dersi seçeyim?” kararı minimuma indirilir.

## Yol Haritası

1095 günlük timeline değil, bilgi ağacı gösterilir.

Örneğin:

- C
  - Variables ✓
  - Conditions ✓
  - Loops ✓
  - Functions ✓
  - Arrays — güçleniyor
  - Memory — güçleniyor
  - Pointers — %64
  - Dynamic Memory — prerequisite bekliyor
- Linux
- C++
- Operating Systems
- Concurrency
- Networking
- Distributed Systems
- GPU Architecture
- CUDA
- Triton
- AI Infrastructure

Kilitler tarihe göre değil prerequisite/mastery durumuna göre oluşur.

## İlerleme Ekranı

Gösterilecek ilerleme türleri:

- alan bazlı mastery
- konu bazlı mastery
- retention / unutma riski
- son sınav performansı
- kodlama başarısı
- İngilizce seviyesi
- güçlü ve zayıf alanlar
- çalışma süresi trendi (ikincil metrik)

`%12 kariyer tamamlandı` gibi yanıltıcı bir metrik kullanılmaz.

## Sınavlar Ekranı

- Günlük mikro değerlendirmeler
- Haftalık sınav geçmişi
- Aylık yeterlilik sınavları
- Yeniden test bekleyen konular
- Sonuçlara bağlı yapılan program değişiklikleri

## Öğrenme Oturumu Deneyimi

Bir oturum sadece içerik tüketimi değildir.

Önerilen akış:

1. Ön bilgi sorusu
2. Kısa açıklama
3. Örnek
4. Etkileşimli soru
5. Uygulama
6. Hata analizi
7. Kendi cümlesiyle açıklama
8. Mini ölçme

Yanlış cevapta aynı metni tekrar göstermek yerine farklı açıklama stratejisi denenir.

## AI Tutor Davranışı

AI öğretmen:

- seviyeye göre anlatır
- doğrudan cevabı vermeden önce ipucu verir
- yanlışın nedenini teşhis eder
- aynı kavram için farklı örnek üretir
- kodu açıklatır
- kullanıcının AI'a yazdırdığı kodu gerçekten anlayıp anlamadığını test eder
- gerektiğinde yeni remedial çalışma önerir

AI öğretmen “tamamlandı” kararını tek başına keyfi vermemeli; mastery engine ile birlikte çalışmalıdır.

## Kişisel Kullanım Nedeniyle Bilerek Eklenmeyecekler

- hesap oluşturma
- sosyal feed
- arkadaş ekleme
- liderlik tablosu
- ödeme
- abonelik
- admin paneli
- organizasyon/rol sistemi
- çok kullanıcılı SaaS altyapısı

## Tasarım Dili

- modern, premium ama sade
- okunabilir tipografi
- açık/koyu tema
- minimal kart kullanımı
- ilerleme renkleri ve grafikler anlamlı olmalı
- gereksiz gamification yok
- streak olabilir fakat mastery'den daha önemli gösterilmez
- başarısızlık cezalandırıcı değil, yönlendirici görünür

## Başarı Tanımı

Ürün başarılıysa kullanıcı:

- bugün ne çalışacağını düşünmez
- eksiklerini sistem otomatik fark eder
- öğrenmeden yeni kritik konuya geçmez
- uzun süre önce öğrendiklerini unutmaz
- teknik İngilizceyi ayrı bir yük gibi değil, eğitimin parçası olarak geliştirir
- zaman içinde gerçek işe hazırlık göstergeleri üretir
