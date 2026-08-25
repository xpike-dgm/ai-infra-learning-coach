# Parallel English Track — HISTORICAL / NON-CANONICAL SEED NOTES

**Durum:** PLANNING SEED / CANONICAL DEĞİL  
**Hygiene clarification:** D-050 — 2026-08-25

Bu dosya Technical English hattı için erken fikir ve içerik seed notlarını korur. Başlıktaki eski `A0 → B2` önerisi **kilitlenmiş final CEFR exit gate'i değildir**.

Canonical davranış kaynakları:
- `docs/ENGLISH_FOUNDATION_RULES.md` — prerequisite ve A0 güvenlik kuralları,
- `docs/CURRICULUM_DOMAIN_MAP.md` — Technical English'in parallel-track rolü,
- AŞAMA 6C — granular English capability map,
- AŞAMA 7A–7E — başlangıç ölçümü, CEFR/technical hedefler, cadence, teknik entegrasyon ve English mastery tasarımı.

Aşağıdaki içerik AŞAMA 6/7 araştırma ve curriculum design sırasında kullanılabilecek **seed** materyaldir; final sıra/threshold/cadence olarak uygulanmaz.

---

## Ana İlke

İngilizce teknik eğitimin ön koşulu değildir. Uzun vadeli AI Infrastructure kariyeri için kritik bir parallel capability'dir; teknik eğitimle aynı anda ilerler.

## A0 → A1 seed

Hedef fikirleri:
- temel cümle yapıları
- en sık kullanılan temel kelimeler
- teknik terimlere alışma
- compiler/terminal hata mesajlarını sözlük/AI yardımıyla çözme

Teknik bağlam örnekleri:
- variable
- value
- memory
- address
- error
- function
- input
- output
- allocate
- release

Muhtemel çıktılar:
- basit kod yorumları İngilizce
- çok kısa commit mesajları
- hata mesajındaki ana kelimeleri tanıma

## A1 → A2 seed

Hedef fikirleri:
- kısa teknik dokümantasyon okuyabilmek
- Linux man page yapısını tanımak
- Git/GitHub terminolojisini anlamak

Muhtemel çıktılar:
- İngilizce README'nin basit bölümlerini yazmak
- commit mesajları
- issue başlığını anlayabilmek
- kısa teknik sorular yazabilmek

## A2 → B1 seed

Hedef fikirleri:
- orta uzunlukta dokümantasyon
- GitHub issue/PR tartışmaları
- temel RFC ve teknik makale okuma
- bir problemi yazılı şekilde açıklama

Muhtemel çıktılar:
- İngilizce issue açmak
- PR açıklaması yazmak
- 1–2 sayfalık teknik proje özeti
- hata raporu yazmak

## B1 ve üzeri seed

Hedef fikirleri:
- NVIDIA/CUDA dokümantasyonu
- daha yoğun sistem yazıları
- teknik konferans/video içeriği
- paper özetleri
- teknik mülakat
- proje sunumu
- asenkron global ekip iletişimi
- architecture discussion

Muhtemel çıktılar:
- teknik özet yazmak
- benchmark sonucunu İngilizce açıklamak
- sistem tasarım kararını yazılı savunmak
- C++ memory modelini sözlü anlatabilmek
- debugging sürecini İngilizce anlatabilmek
- GPU optimization kararını savunabilmek
- mock interview tamamlamak

## Günlük Entegrasyon Seed'i

İlk aşamada bir English bloğu şu tür işlerden oluşabilir:
- temel gramer
- aktif kelime
- dinleme
- kısa okuma

Teknik çalışma boyunca da İngilizce doğal biçimde kullanılabilir:
- teknik terimler İngilizce bırakılır,
- IDE/terminal dili mümkünse İngilizce,
- Git commit mesajları İngilizce,
- README İngilizce,
- dokümantasyon önce orijinalinden görülür, sonra gerekirse açıklama alınır.

Exact cadence, süre veya sabit oran **bu dosyada kilitlenmez**; AŞAMA 7C ve planner state'i belirler.

## AI Kullanım Seed'i

AI şu tür scaffold sağlayabilir:

> “Bu teknik paragrafı Türkçe açıkla ama pointer, heap, allocation, lifetime gibi terimleri İngilizce bırak.”

Ancak destek takvime göre değil evidence/mastery'ye göre azaltılmalıdır.

## Ölçme Seed'i

İngilizce ilerlemesi yalnız uygulamada geçirilen süreyle ölçülmez. Muhtemel evidence/activity family'leri:
- teknik kelime tanıma
- kısa reading comprehension
- compiler error açıklama
- README yazma
- issue/PR yazma
- dinleme
- sözlü proje anlatma
- mock interview

## Profil Seed'i

Tek bir yüzde yerine alt capability özetleri düşünülebilir:
- General foundation
- Technical vocabulary
- Documentation reading
- Technical writing
- Listening
- Speaking / interview

CEFR seviyesi özet gösterge olabilir; **exact final mapping ve gate AŞAMA 7'de araştırılıp kilitlenecektir.**