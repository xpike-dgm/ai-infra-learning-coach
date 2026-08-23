# Learning Engine — Mastery ve Adaptif Planlama

## 1. Ana İlke

> Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.

Bir dersi açmak, okumak, video izlemek veya görevi “tamamlandı” yapmak konu mastery puanını tek başına yeterli seviyeye çıkarmaz.

## 2. Kavramlar

### Topic
Tek bir öğrenme birimi. Örnek: `Basic Pointers`.

### Skill Domain
Daha büyük alan. Örnek: `C`, `Linux`, `Concurrency`.

### Prerequisite
Bir konuya geçmeden önce yeterli hakimiyet gereken önceki konu.

### Mastery
Kullanıcının konuyu gerçekten bildiğine dair birleşik puan.

### Retention
Öğrenilmiş konunun zaman içinde ne kadar korunduğuna dair tahmin.

### Remediation
Zayıf kalan konu için ek çalışma paketi.

## 3. Mastery Sinyalleri

İlk tasarımda mastery aşağıdaki kanıtlardan üretilebilir:

- teori quiz puanı
- uygulamalı kodlama görevi
- debugging görevi
- kısa açıklama / Feynman cevabı
- transfer sorusu: kavramı farklı bağlamda kullanabilme
- gecikmeli tekrar sonucu
- kullanılan ipucu sayısı
- AI yardım düzeyi
- görevi tamamlama süresi (düşük ağırlık)

Kesin ağırlıklar ürün testleriyle ayarlanmalıdır.

Örnek başlangıç modeli:

- Quiz: %20
- Uygulama: %25
- Debugging: %15
- Açıklama: %15
- Transfer: %10
- Retention: %15

Bu oranlar sabit ürün kararı değildir; deneysel başlangıçtır.

## 4. Mastery Durumları

Önerilen durumlar:

- `not_started`
- `learning`
- `developing`
- `mastered_provisional`
- `mastered`
- `at_risk`
- `needs_remediation`

Bir konu ilk sınavda yüksek skor alınca hemen kalıcı `mastered` olmamalıdır. Önce gecikmeli tekrar beklenebilir.

## 5. Prerequisite Kuralı

Örnek:

`Memory Addresses` → `Basic Pointers` → `Pointer Arithmetic` → `Dynamic Memory`

`Basic Pointers` gerekli eşiğin altındaysa `Pointer Arithmetic` açılmaz.

Ancak başka bağımsız konu varsa program tamamen durmaz. Örneğin Linux temel komutları paralel ilerleyebilir.

## 6. Günlük Planlayıcı

Her gün için aday görev havuzu şu kaynaklardan oluşur:

1. aktif ana konu
2. prerequisite açıkları
3. retention nedeniyle tekrar zamanı gelen eski konular
4. İngilizce paralel görevleri
5. proje/uygulama çalışmaları
6. haftalık/aylık sınav hazırlığı

Planlayıcı kullanıcının o gün ayırabileceği süreye göre görevleri seçer.

Öncelik örneği:

1. kritik prerequisite açığı
2. unutma riski yüksek mastered konu
3. mevcut ana konu
4. İngilizce günlük blok
5. ileri konu

## 7. Gün Sonu Yeniden Planlama

Günlük görevler bitince sistem:

- yeni mastery değerlerini hesaplar
- yeni remediation ihtiyacını belirler
- prerequisite durumlarını günceller
- ertesi günün aday görevlerini oluşturur

Kullanıcı zayıfsa yeni konu zorla açılmaz. Kullanıcı hızlıysa gereksiz bekleme yapılmaz.

## 8. Haftalık Sınav Mantığı

Haftalık sınav bileşenleri:

- teori
- kodlama
- debugging
- İngilizce
- önceki haftalardan retention soruları

Örnek sonuç:

- Functions %91
- Arrays %84
- Memory %79
- Pointers %51
- Technical English %74

Planlama sonucu:

- Pointers için remediation ekle
- Pointers bağımlı konuları ertele
- bağımsız yeni konulara devam et
- Memory için kısa reinforcement ekle

## 9. Aylık Yeterlilik Sınavı

Ay sonu değerlendirmesi daha geniş kapsamlıdır:

- kavramsal teori
- uygulamalı görev
- bozuk kod düzeltme
- sistemi/kavramı kendi cümlesiyle anlatma
- teknik İngilizce okuma/yazma
- eski konulardan retention

Amaç ayı “geçmek” değil, knowledge graph üzerindeki güvenilir mastery durumlarını güncellemektir.

## 10. Spaced Repetition

Takvim basit kart tekrarından daha akıllı olmalıdır. Konu tekrar zamanı şu sinyallere göre değişebilir:

- son mastery
- son retention başarısı
- konunun kritikliği
- bağımlı konu sayısı
- hata geçmişi
- kullanıcı güveni ile gerçek performans arasındaki fark

İlk prototip basit aralıklarla başlayabilir; daha sonra FSRS/benzeri yaklaşımlar değerlendirilebilir.

## 11. AI Yardımının Etkisi

AI kullanımı yasak değildir. Kariyer rotasının doğası gereği AI araçları aktif kullanılacaktır.

Ancak sistem şu farkı ölçmeye çalışmalıdır:

- kullanıcı problemi kendi çözdü
- ipucu aldı
- açıklama aldı
- kodun bir kısmını AI üretti
- çözümün tamamını AI üretti

AI çok yardım ettiyse mastery kanıtı için ek doğrulama gerekir.

Örnek doğrulama soruları:

- Bu satır neden gerekli?
- Bu `free()` kaldırılırsa ne olur?
- Burada race condition olabilir mi?
- Bu çözümün time complexity'si nedir?
- Aynı problemi farklı veri yapısıyla çöz.

## 12. Öğrenememe Durumu

Aynı konu tekrar tekrar başarısızsa sadece daha fazla aynı soru verilmemelidir.

Remediation stratejileri:

- daha basit anlatım
- görsel model
- analogi
- prerequisite geri dönüşü
- çok küçük alıştırmalar
- worked example
- hata bulma
- pair-programming tarzı AI tutor

Sistem “3 kez kaldın” diye cezalandırmak yerine yanlışın kök nedenini aramalıdır.

## 13. Kaçırılan Günler

Streak ikincil metriktir. Birkaç gün çalışılmadığında:

- kaçırılan tüm görevler birikmiş borç gibi yığılmaz
- retention durumu yeniden değerlendirilir
- günlük kapasiteye göre yeni plan yapılır
- kritik tekrarlar öne alınır

## 14. Hızlı İlerleme

Kullanıcı ön testte ve uygulamada konuyu zaten biliyorsa içerik kısaltılabilir veya atlanabilir. Ancak bunun için gerçek mastery kanıtı gerekir.

## 15. İlerleme Gösterimi

Ana ilerleme göstergeleri:

- domain mastery
- topic mastery
- retention
- zayıf alan sayısı
- sınav trendi
- proje yeterlilikleri
- teknik İngilizce seviyesi

Gösterilmeyecek / ana metrik olmayacak:

- 1095 günden kaç gün geçti
- sadece ders tamamlama yüzdesi
- sadece uygulamada geçirilen süre

## 16. Gelecekteki Career Readiness Engine

İleride gerçek iş ilanı beceri gereksinimleri knowledge graph ile eşleştirilebilir.

Örnek:

`Junior C++ Systems Engineer`
- C++ memory model ✓
- Linux ✓
- concurrency %78
- networking %62

Sonuç: `Hazırlık %X` yerine mümkünse daha açıklanabilir çıktı:

- Hazır alanlar
- Eksik alanlar
- Kanıt gereken projeler
- Mülakat pratiği gereken başlıklar

Bu motor V1 için zorunlu değildir.
