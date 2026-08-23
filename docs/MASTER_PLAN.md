# AI Infra Learning Coach — Master Geliştirme Planı

Bu dosya projenin ana yürütme planıdır. Kodlamaya, içerik üretimine veya büyük mimari kararlara bu plandaki ilgili aşama netleşmeden geçilmez.

## Durum Sistemi

- [ ] **Bekliyor** — henüz başlanmadı.
- [x] **Tamamlandı** — kabul kriterleri karşılandı.

Bir adım tamamlandığında aynı satırın altına şu formatta not eklenir:

> **Tamamlanma notu — YYYY-MM-DD:** Ne yapıldı, hangi karar alındı, hangi dosya/çıktı üretildi ve sonraki adıma etkisi.

Önemli karar değişiklikleri ayrıca `docs/DECISIONS.md` içine yazılır. Günlük/oturum bazlı ilerleme özeti `docs/PROGRESS_LOG.md` içinde tutulur.

---

# AŞAMA 0 — Proje Çerçevesini Kilitle

**Amaç:** Ne yaptığımızı, ne yapmadığımızı ve başarının ne anlama geldiğini kesinleştirmek.

## 0.1 Ürün amacı
- [ ] Uygulamanın tek cümlelik ana amacını kesinleştir.
- [ ] Kullanıcının uygulamayı açtığında aldığı temel değeri tanımla.
- [ ] “Zaman geçirmek ilerleme değildir; kanıtlanmış öğrenme ilerlemedir” ilkesini ürün gereksinimine dönüştür.

## 0.2 Kapsam
- [ ] V1’de kesin olacak özellikleri listele.
- [ ] V1’de kesin olmayacak özellikleri listele.
- [ ] Kişisel kullanım sınırını teknik ve ürün açısından tanımla.
- [ ] 3 yıllık hedefin bir sayaç değil, arka plan curriculum ufku olduğunu kesinleştir.

## 0.3 Başarı kriterleri
- [ ] “Uygulama başarılı” demek için ölçülebilir V1 kriterlerini yaz.
- [ ] Günlük görev üretiminin doğruluk kriterini belirle.
- [ ] Mastery ölçümünün minimum güven şartlarını belirle.
- [ ] Haftalık/aylık sınavların planı gerçekten değiştirdiğini doğrulayan kriterleri yaz.

### Aşama 0 çıkışı
- Ürün amacı ve kapsam belgesi
- V1 success criteria
- Non-goals listesi

### Aşama 0 tamamlanma kapısı
Aşama 1’e geçmeden önce “uygulama ne yapacak?” sorusunun yoruma açık kalmaması gerekir.

---

# AŞAMA 1 — Öğrenme Sisteminin Kurallarını Tasarla

**Amaç:** Uygulamanın pedagojik/ölçme mantığını koddan bağımsız olarak tanımlamak.

## 1.1 Bilgi birimi modeli
- [ ] `Domain → Module → Topic → Skill → Learning Objective` hiyerarşisini kesinleştir.
- [ ] Her topic için prerequisite tanımını belirle.
- [ ] Hard prerequisite ile soft prerequisite farkını tanımla.
- [ ] Bir konunun “başlanabilir”, “öğreniliyor”, “öğrenildi”, “zayıfladı” durumlarını tanımla.

## 1.2 Mastery modeli
- [ ] Mastery puanına hangi sinyallerin gireceğini kesinleştir.
- [ ] Teori quiz ağırlığını belirle.
- [ ] Kodlama görevi ağırlığını belirle.
- [ ] Debugging ağırlığını belirle.
- [ ] Kendi cümlesiyle açıklama/Feynman ağırlığını belirle.
- [ ] Gecikmeli tekrar/retention ağırlığını belirle.
- [ ] AI/ipucu kullanımının mastery’ye etkisini tanımla.
- [ ] Minimum mastery threshold değerlerini belirle.
- [ ] Tek yüksek sınav puanının yanlış pozitif oluşturmasını engelleyen kuralı yaz.

## 1.3 Unutma ve retention
- [ ] Spaced repetition yaklaşımını seç.
- [ ] İlk tekrar pencerelerini belirle.
- [ ] Tekrar başarısızlığında mastery düşüş mantığını tanımla.
- [ ] Uzun süre kullanılmayan beceriler için decay kuralını belirle.

## 1.4 Remediation
- [ ] Zayıf konu tespit kriterlerini yaz.
- [ ] Aynı anlatımı tekrar etmek yerine farklı remediation tiplerini tanımla.
- [ ] Görsel açıklama, mikro alıştırma, debugging, kod tamamlama, açıklama sorusu gibi müdahale tiplerini sınıflandır.
- [ ] Bir konunun kaç başarısız denemeden sonra daha temel prerequisite’e geri dönmesi gerektiğini belirle.

### Aşama 1 çıkışı
- `LEARNING_ENGINE_SPEC.md`
- Mastery formülü v0
- Retention/spaced repetition spec
- Remediation kuralları

### Aşama 1 tamamlanma kapısı
Aynı öğrenci verisi verildiğinde sistemin neden belirli bir konuyu tekrar ettirdiği açıklanabilir olmalıdır.

---

# AŞAMA 2 — Adaptif Planlama Motorunu Tasarla

**Amaç:** “Bugün ne çalışmalıyım?” sorusuna deterministik ve açıklanabilir cevap verecek motoru tanımlamak.

## 2.1 Günlük kapasite modeli
- [ ] Kullanıcının günlük ayırabildiği süreyi nasıl tanımlayacağını belirle.
- [ ] Minimum/normal/yoğun gün seçeneklerini tanımla.
- [ ] Mola ve görev sürelerinin nasıl bölüneceğini belirle.

## 2.2 Görev seçme önceliği
- [ ] Zayıf topic önceliği.
- [ ] Yaklaşan retention tekrar önceliği.
- [ ] Yeni topic önceliği.
- [ ] İngilizce paralel hat payı.
- [ ] Proje/uygulama görevi payı.
- [ ] Gün sonu mikro değerlendirme payı.

## 2.3 Prerequisite ve ilerleme
- [ ] Bağımlı konuların nasıl kilitleneceğini belirle.
- [ ] Bağımsız dalların nasıl devam edeceğini belirle.
- [ ] Çok güçlü performansta content skip doğrulaması tasarla.
- [ ] Bir konuda tökezlemenin tüm curriculum’u durdurmasını engelle.

## 2.4 Program yeniden hesaplama
- [ ] Kaçırılan 1 gün senaryosu.
- [ ] Kaçırılan birkaç gün senaryosu.
- [ ] Çok hızlı öğrenme senaryosu.
- [ ] Haftalık sınav sonrası yeniden planlama.
- [ ] Aylık sınav sonrası yeniden planlama.
- [ ] “Plan neden değişti?” açıklama üretme kuralı.

## 2.5 Planner simülasyonu
- [ ] En az 10 yapay öğrenci profili oluştur.
- [ ] Güçlü öğrenci senaryosunu test et.
- [ ] Pointer gibi tek bir konuda zorlanan öğrenci senaryosunu test et.
- [ ] İngilizcede geri kalan ama teknikte hızlı öğrenci senaryosunu test et.
- [ ] 7 gün ara veren kullanıcı senaryosunu test et.

### Aşama 2 çıkışı
- `ADAPTIVE_PLANNER_SPEC.md`
- Planner pseudocode/decision table
- Senaryo testleri

### Aşama 2 tamamlanma kapısı
Motorun örnek kullanıcı geçmişinden ertesi günün görev listesini tutarlı biçimde üretebilmesi gerekir.

---

# AŞAMA 3 — Sınav ve Değerlendirme Sistemini Tasarla

**Amaç:** “Öğrendi mi?” sorusunu güvenilir şekilde ölçmek.

## 3.1 Günlük mikro değerlendirme
- [ ] Quiz formatları.
- [ ] Kısa coding task formatı.
- [ ] Debugging formatı.
- [ ] Açıklama sorusu formatı.
- [ ] Eski konu retrieval sorusu formatı.

## 3.2 Haftalık sınav
- [ ] Bölüm yapısını belirle.
- [ ] Yeni konu / eski konu oranını belirle.
- [ ] Sınav zorluk adaptasyonunu tanımla.
- [ ] Başarısız alanların sonraki haftaya taşınma kuralını kesinleştir.

## 3.3 Aylık yeterlilik sınavı
- [ ] Teori bölümü.
- [ ] Kodlama bölümü.
- [ ] Debugging bölümü.
- [ ] Açıklama/Feynman bölümü.
- [ ] Teknik İngilizce bölümü.
- [ ] Retention bölümü.

## 3.4 Soru kalitesi
- [ ] Aynı cevabı ezberlemeyi önlemek için varyasyon üretme kuralı.
- [ ] Transfer soruları.
- [ ] Kolay/orta/zor soru etiketleri.
- [ ] Soru bankası ile AI-generated soruların birlikte kullanım modeli.

### Aşama 3 çıkışı
- `ASSESSMENT_SYSTEM_SPEC.md`
- Günlük/haftalık/aylık sınav şablonları
- Puanlama rubric’leri

---

# AŞAMA 4 — Curriculum ve Knowledge Graph Mimarisini Kur

**Amaç:** 3 yıllık yolun sabit gün listesi değil, genişleyebilir bilgi haritası olarak modellenmesi.

## 4.1 Ana domainler
- [ ] Technical English
- [ ] Computer Fundamentals
- [ ] C
- [ ] Linux
- [ ] Data Structures & Algorithms
- [ ] Modern C++
- [ ] Operating Systems & Memory
- [ ] Concurrency
- [ ] Networking
- [ ] Distributed Systems
- [ ] GPU Architecture
- [ ] CUDA
- [ ] Triton
- [ ] ML/LLM Systems Fundamentals
- [ ] Inference Engines
- [ ] Multi-GPU / NCCL / RDMA
- [ ] AI Infrastructure
- [ ] Open Source & Career Readiness

## 4.2 İlk 8–12 haftalık graph
- [ ] Computer Fundamentals alt konuları.
- [ ] C Foundations alt konuları.
- [ ] Memory Foundations alt konuları.
- [ ] Linux Foundations alt konuları.
- [ ] İlk veri yapıları.
- [ ] A0→A1/A2 English paralel konuları.
- [ ] Her topic için learning objectives.
- [ ] Her topic için prerequisite bağlantıları.
- [ ] Her topic için assessment türleri.

## 4.3 Curriculum kalite kontrolü
- [ ] Gereksiz konu var mı kontrol et.
- [ ] Eksik prerequisite var mı kontrol et.
- [ ] İş hedefiyle ilgisiz akademik dolgu var mı kontrol et.
- [ ] AI çağında düşük ROI ezberleri ayıkla.

### Aşama 4 çıkışı
- `CURRICULUM_GRAPH_SPEC.md`
- İlk 8–12 haftalık graph verisi
- Curriculum QA raporu

---

# AŞAMA 5 — İngilizce Paralel Hattı Kesinleştir

**Amaç:** İngilizceyi ayrı bir bekleme aşaması değil, teknik eğitimin içinde ilerleyen ikinci hat yapmak.

## 5.1 Seviye modeli
- [ ] A0 başlangıç ölçümü.
- [ ] A1 teknik hedefleri.
- [ ] A2 teknik hedefleri.
- [ ] B1 teknik hedefleri.
- [ ] B2 global iş/mülakat hedefleri.

## 5.2 Günlük entegrasyon
- [ ] Genel temel İngilizce payı.
- [ ] Teknik vocabulary payı.
- [ ] Compiler/terminal message okuma.
- [ ] Documentation okuma.
- [ ] README/commit yazma.
- [ ] GitHub issue/PR yazma.
- [ ] Teknik konuşma ve mock interview.

## 5.3 Ölçme
- [ ] Reading.
- [ ] Writing.
- [ ] Listening.
- [ ] Speaking.
- [ ] Technical vocabulary.
- [ ] Technical explanation.

### Aşama 5 çıkışı
- `ENGLISH_TRACK_SPEC.md`
- İlk 12 haftalık paralel English görevleri
- Seviye geçiş kriterleri

---

# AŞAMA 6 — Ürün Gereksinimleri ve UX Akışlarını Kilitle

**Amaç:** Kod başlamadan önce uygulamanın nasıl davranacağını ve hangi ekranların gerektiğini kesinleştirmek.

## 6.1 Ekran listesi
- [ ] Ana ekran / Bugünkü çalışma.
- [ ] Günlük çalışma akışı.
- [ ] Topic/lesson ekranı.
- [ ] Quiz ekranı.
- [ ] Coding/debugging görev ekranı.
- [ ] Haftalık sınav.
- [ ] Aylık sınav.
- [ ] Skill/mastery görünümü.
- [ ] Knowledge graph / yol görünümü.
- [ ] Zayıf alanlar.
- [ ] Sınav geçmişi.
- [ ] Ayarlar / günlük süre.

## 6.2 Ana ekran
- [ ] Gün sayacı kullanma.
- [ ] Kariyer yüzde tamamlandı göstergesi kullanma.
- [ ] Devam eden topic ve mastery göster.
- [ ] Bugünkü toplam süreyi göster.
- [ ] Bugünkü görevları göster.
- [ ] Yaklaşan assessment göster.
- [ ] Tek ana CTA: “Çalışmaya Başla / Devam Et”.

## 6.3 Tasarım sistemi
- [ ] Renk/token sistemi.
- [ ] Tipografi.
- [ ] Spacing.
- [ ] Kart/buton/input komponentleri.
- [ ] Light/Dark yaklaşımı.
- [ ] Animasyon ilkeleri.
- [ ] Grafik ve mastery gösterim dili.

## 6.4 Wireframe ve prototip
- [ ] Low-fidelity wireframe.
- [ ] Kritik akışların prototipi.
- [ ] Ana ekran tasarım onayı.
- [ ] Sınav ekranı tasarım onayı.
- [ ] İlerleme ekranı tasarım onayı.

### Aşama 6 çıkışı
- `PRODUCT_REQUIREMENTS.md`
- `UX_FLOWS.md`
- `DESIGN_SYSTEM.md`
- Onaylı ekran/wireframe seti

---

# AŞAMA 7 — Teknik Mimariyi Kesinleştir

**Amaç:** Uygulamayı gereksiz karmaşıklaştırmadan sürdürülebilir teknik temel kurmak.

## 7.1 Mobil teknoloji seçimi
- [ ] Flutter vs Kotlin/Compose vs React Native karşılaştır.
- [ ] Tek teknoloji seç ve gerekçesini kaydet.
- [ ] Android APK üretim sürecini doğrula.

## 7.2 Veri mimarisi
- [ ] Local-first database seç.
- [ ] Topic/skill/prerequisite tabloları.
- [ ] Task/session tabloları.
- [ ] Assessment/attempt tabloları.
- [ ] Mastery snapshot/history.
- [ ] Spaced repetition schedule.
- [ ] Planner decisions/history.
- [ ] English skill state.

## 7.3 AI entegrasyon sınırı
- [ ] AI olmadan çalışması gereken çekirdek özellikleri tanımla.
- [ ] AI tutor ile planner’ı birbirinden ayır.
- [ ] AI provider abstraction belirle.
- [ ] Prompt/version kayıt yaklaşımını belirle.
- [ ] AI cevabı başarısızsa fallback davranışını tanımla.

## 7.4 Veri kaybını önleme
- [ ] Local backup/export.
- [ ] Restore.
- [ ] Database migration yaklaşımı.

### Aşama 7 çıkışı
- `TECHNICAL_ARCHITECTURE.md`
- `DATA_MODEL.md`
- Teknoloji kararı

---

# AŞAMA 8 — Çekirdek MVP’yi Geliştir

**Amaç:** AI tutor olmadan bile günlük çalışma ve mastery döngüsünü çalıştıran ilk gerçek uygulama.

## 8.1 Uygulama iskeleti
- [ ] Proje kurulumu.
- [ ] Navigation.
- [ ] Tema.
- [ ] Database.
- [ ] Seed curriculum loader.

## 8.2 Günlük çalışma
- [ ] Bugünkü görev ekranı.
- [ ] Task start/pause/complete.
- [ ] Topic lesson akışı.
- [ ] Session kayıtları.

## 8.3 Ölçme
- [ ] Quiz engine.
- [ ] Coding/debugging görev temel modeli.
- [ ] Attempt kaydı.
- [ ] Gün sonu değerlendirmesi.

## 8.4 Mastery v0
- [ ] Mastery hesaplama.
- [ ] Topic durumları.
- [ ] Prerequisite unlock/lock.
- [ ] Mastery history.

## 8.5 Planner v0
- [ ] Ertesi gün planı oluştur.
- [ ] Retention tekrarlarını ekle.
- [ ] Zayıf topic remediation ekle.
- [ ] Bağımsız yeni topic’leri devam ettir.

### Aşama 8 çıkışı
- Çalışan Android debug APK
- İlk curriculum ile uçtan uca günlük öğrenme döngüsü

### Aşama 8 tamamlanma kapısı
Kullanıcı bir gün çalışıp değerlendirme aldıktan sonra uygulama ertesi günün planını otomatik ve açıklanabilir şekilde değiştirebilmelidir.

---

# AŞAMA 9 — Adaptif Öğrenme V1

**Amaç:** MVP’deki basit kuralları gerçek adaptif öğrenme davranışına yükseltmek.

## 9.1 Remediation engine
- [ ] Zayıf topic tespiti.
- [ ] Müdahale tipi seçimi.
- [ ] prerequisite geri dönüşü.

## 9.2 Retention engine
- [ ] Spaced repetition scheduler.
- [ ] Forgetting/decay güncellemesi.
- [ ] Retrieval success takibi.

## 9.3 Replanning
- [ ] Haftalık sınav sonrası.
- [ ] Aylık sınav sonrası.
- [ ] Kaçırılan gün sonrası.
- [ ] Çok hızlı mastery sonrası.

## 9.4 Açıklanabilirlik
- [ ] “Bu görev neden eklendi?” bilgisi.
- [ ] “Bu konu neden ertelendi?” bilgisi.
- [ ] “Mastery neden düştü/yükseldi?” bilgisi.

### Aşama 9 çıkışı
- Adaptif planner v1
- Scenario regression test suite

---

# AŞAMA 10 — Haftalık ve Aylık Sınavları Uygula

**Amaç:** Gerçek öğrenme kanıtını daha güçlü değerlendirmelerle toplamak.

- [ ] Haftalık sınav akışı.
- [ ] Haftalık sonuç analizi.
- [ ] Haftalık remediation entegrasyonu.
- [ ] Aylık yeterlilik sınavı.
- [ ] Aylık sonuç analizi.
- [ ] Aylık plan yeniden yapılandırma.
- [ ] Eski konulardan retention bölümü.

### Aşama 10 çıkışı
- Çalışan weekly/monthly assessment sistemi

---

# AŞAMA 11 — AI Tutor V1

**Amaç:** AI’ı cevap veren chatbot değil, adaptif öğretmen olarak kullanmak.

## 11.1 Öğretme davranışı
- [ ] Kullanıcının seviyesine göre anlatım.
- [ ] Doğrudan cevabı verme yerine ipucu katmanları.
- [ ] Alternatif anlatım.
- [ ] Örnek üretimi.
- [ ] Yanlışın kök nedenini bulma.

## 11.2 Kod öğrenme doğrulaması
- [ ] Kodu açıklatma.
- [ ] Satır değişirse ne olur soruları.
- [ ] Bug buldurma.
- [ ] Transfer soruları.
- [ ] AI ile yazılmış kod için comprehension gate.

## 11.3 Açık uçlu değerlendirme
- [ ] Feynman explanation değerlendirme.
- [ ] Rubric tabanlı puanlama.
- [ ] Hallucination/yanlış puanlama riskine karşı guardrail.

### Aşama 11 çıkışı
- AI Tutor v1
- Tutor behavior test set

---

# AŞAMA 12 — İlk 8–12 Haftalık İçeriği Üret ve QA Et

**Amaç:** Motoru gerçek kullanımda besleyecek yüksek kaliteli başlangıç curriculum’u oluşturmak.

- [ ] Günlük lesson içerikleri.
- [ ] Günlük practice görevleri.
- [ ] Mikro quiz bankası.
- [ ] Debugging görevleri.
- [ ] Coding görevleri.
- [ ] English görevleri.
- [ ] Haftalık sınavlar.
- [ ] İlk aylık sınav.
- [ ] Cevap/rubric QA.
- [ ] Prerequisite QA.

### Aşama 12 çıkışı
- Üretime hazır ilk curriculum paketi

---

# AŞAMA 13 — İlerleme, Analitik ve Görselleştirme

**Amaç:** Geçen günü değil, gerçek yetkinliği görünür yapmak.

- [ ] Domain mastery görünümü.
- [ ] Topic mastery detayları.
- [ ] Retention sağlığı.
- [ ] Zayıf konular.
- [ ] Sınav trendleri.
- [ ] Technical English seviye görünümü.
- [ ] Çalışma süresi ikincil metrik olarak.
- [ ] Streak yalnızca ikincil bilgi.

### Aşama 13 çıkışı
- Skill/mastery odaklı progress ekranı

---

# AŞAMA 14 — UX Polish ve Kişisel Kullanım Hazırlığı

- [ ] Light/Dark theme son hali.
- [ ] Mikro animasyonlar.
- [ ] Loading/error/empty state’ler.
- [ ] Bildirimler.
- [ ] Günlük çalışma reminder’ı.
- [ ] Offline davranış kontrolü.
- [ ] Backup/export/restore.
- [ ] Performans optimizasyonu.
- [ ] Crash testleri.

---

# AŞAMA 15 — Gerçek Kullanım Pilotu

**Amaç:** Teorik planın gerçek günlük kullanımda işe yarayıp yaramadığını doğrulamak.

## 15.1 Pilot
- [ ] En az 14 günlük gerçek kullanım.
- [ ] Günlük sürtünme noktalarını kaydet.
- [ ] Yanlış planner kararlarını kaydet.
- [ ] Yanlış mastery kararlarını kaydet.
- [ ] Çok kolay/zor görevları kaydet.

## 15.2 Düzeltme
- [ ] Planner ağırlıklarını ayarla.
- [ ] Mastery threshold’larını ayarla.
- [ ] Sınav zorluğunu ayarla.
- [ ] UI sürtünmelerini düzelt.

### Aşama 15 çıkışı
- Pilot QA raporu
- V1 release candidate

---

# AŞAMA 16 — Release APK

- [ ] Release build.
- [ ] Temiz kurulum testi.
- [ ] Upgrade/migration testi.
- [ ] Backup/restore testi.
- [ ] APK üret.
- [ ] Sürüm notu yaz.

### Aşama 16 çıkışı
- Günlük kullanılabilir V1 APK

---

# AŞAMA 17 — Curriculum Genişletme

V1’den sonra motor değişmeden curriculum katmanı büyütülür.

- [ ] Modern C++ paketi.
- [ ] OS/Memory paketi.
- [ ] Concurrency paketi.
- [ ] Networking paketi.
- [ ] Distributed Systems paketi.
- [ ] GPU Architecture paketi.
- [ ] CUDA paketi.
- [ ] Triton paketi.
- [ ] LLM Systems paketi.
- [ ] Inference Engines paketi.
- [ ] NCCL/RDMA/Multi-GPU paketi.
- [ ] AI Infrastructure paketi.
- [ ] Open Source contribution paketi.
- [ ] Interview/Career readiness paketi.

---

# AŞAMA 18 — Kariyer Hazırlık Katmanı

- [ ] Portföy proje takibi.
- [ ] GitHub contribution hedefleri.
- [ ] Teknik CV readiness.
- [ ] Systems/C++ interview hazırlığı.
- [ ] CUDA/GPU interview hazırlığı.
- [ ] Technical English mock interview.
- [ ] Hedef iş rolü skill-gap karşılaştırması.

---

# ŞU ANKİ ÇALIŞMA SIRASI

Kodlamaya hemen başlanmayacak. İlk aktif sıra:

1. **Aşama 0 — Proje çerçevesini kilitle**
2. **Aşama 1 — Öğrenme sistemi kuralları**
3. **Aşama 2 — Adaptif planner spesifikasyonu**
4. **Aşama 3 — Assessment sistemi**
5. **Aşama 4 — Knowledge graph/curriculum mimarisi**
6. **Aşama 5 — English paralel hat**
7. **Aşama 6 — PRD/UX/tasarım**
8. **Aşama 7 — Teknik mimari**
9. Ancak bundan sonra **Aşama 8 — Kodlama**

Bu sıralama, uygulamanın güzel görünen fakat öğrenme mantığı zayıf bir todo uygulamasına dönüşmesini engellemek için bilinçli olarak seçilmiştir.
