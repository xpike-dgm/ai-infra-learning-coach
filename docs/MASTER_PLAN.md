# AI Infra Learning Coach — Master Geliştirme Planı

Bu belge projenin **tek ana yürütme planıdır**. Amaç sadece yapılacak işleri listelemek değil; her aşamada **neden o işi yaptığımızı, hangi sırayla yapacağımızı, ne üretileceğini, nasıl test edileceğini ve ne zaman tamamlanmış sayılacağını** kesinleştirmektir.

Uygulama kişisel kullanım içindir. Ana ürün prensibi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Durum ve kayıt sistemi

- [ ] **Bekliyor** — henüz tamamlanmadı.
- [x] **Tamamlandı** — kabul kriterleri karşılandı.

Bir adım tamamlandığında checkbox `[x]` yapılır ve hemen altına şu formatta açıklama eklenir:

> **Tamamlanma notu — YYYY-MM-DD:** Ne yapıldı, neden bu karar verildi, hangi dosya/kod/çıktı üretildi, test sonucu neydi ve sonraki adıma etkisi nedir?

Kalıcı ürün kararları ayrıca `docs/DECISIONS.md` dosyasına; oturum bazlı ilerleme ise `docs/PROGRESS_LOG.md` dosyasına yazılır.

---

# GENEL MİLESTONE HARİTASI

| Milestone | Aşamalar | Sonuç |
|---|---:|---|
| **M0 — Ürün tanımı kilitli** | 0–6 | Ne yapacağımız ve uygulamanın nasıl davranacağı netleşir. |
| **M1 — Teknik temel hazır** | 7–8 | Mimari, veri modeli ve çalışan mobil iskelet oluşur. |
| **M2 — Kullanılabilir çekirdek V1** | 9–10 | Günlük çalışma + mastery + adaptif planlama telefonda çalışır. |
| **M3 — Gerçek adaptif eğitim ürünü** | 11–13 | Sınav, retention, remediation, AI tutor ve gerçek içerik birlikte çalışır. |
| **M4 — Kaliteli günlük kullanım sürümü** | 14–16 | Analitik, polish, QA ve gerçek kullanım kalibrasyonu tamamlanır. |
| **M5 — Release APK** | 17 | Günlük kullanılabilir release APK hazırdır. |
| **M6 — Uzun vadeli kariyer sistemi** | 18 | Müfredat C++ → CUDA → AI Infrastructure ve kariyer hazırlığına genişler. |

> **Not:** Uygulamanın “hazır” kabul edildiği ana nokta **Aşama 17**'dir. Aşama 18 uzun vadeli içerik genişletme ve kariyer katmanıdır.

---

# AŞAMA 0 — ÜRÜN ÇERÇEVESİNİ KİLİTLE

## Amaç
Uygulamayı kodlamadan önce “Bu ürün tam olarak ne yapıyor, ne yapmıyor ve başarılı olması ne demek?” sorularını yoruma kapatmak.

## 0.1 Ana ürün amacı
- [ ] Uygulamanın tek cümlelik ürün amacını yaz.
- [ ] Kullanıcı uygulamayı sabah açtığında aldığı temel değeri tek paragrafta tanımla.
- [ ] Uygulamanın klasik kurs/todo uygulamasından farkını açıkça yaz.
- [ ] “Kanıtlanmış öğrenme” kavramını ürün gereksinimine dönüştür.
- [ ] Ana kariyer rotasını ürün kapsamına bağla: C → Linux → C++ → Systems → GPU/CUDA → AI Infrastructure.

## 0.2 V1 kapsamı
- [ ] V1’de kesin bulunacak özellikleri listele.
- [ ] V1’de bulunmayacak özellikleri listele.
- [ ] Kişisel kullanım sınırını netleştir.
- [ ] Auth, ödeme, sosyal özellik, admin paneli ve çok kullanıcılı SaaS mimarisinin kapsam dışı olduğunu kaydet.
- [ ] 3 yıllık hedefin UI’da sayaç olmadığını; yalnızca arka plan curriculum ufku olduğunu yaz.

## 0.3 Başarı kriterleri
- [ ] “Günlük plan doğru çalışıyor” kriterini ölçülebilir hale getir.
- [ ] “Bir konu gerçekten öğrenildi” kriterini ölçülebilir hale getir.
- [ ] Haftalık sınavın gelecek haftayı değiştirmesi için acceptance test yaz.
- [ ] Aylık sınavın curriculum önceliklerini değiştirmesi için acceptance test yaz.
- [ ] Kaçırılan günlerde uygulamanın cezalandırmadan yeniden planladığını test kriteri olarak tanımla.

## 0.4 Non-goals
- [ ] Gamification’ın mastery’nin önüne geçmeyeceğini yaz.
- [ ] Gün sayısı/streak’in ana başarı metriği olmayacağını yaz.
- [ ] “Tüm 3 yıllık içeriği V1’e doldurmak” hedefinin olmadığını kaydet.
- [ ] İlk sürümün eğitim motorunu doğrulamaya odaklandığını kaydet.

### Üretilecek çıktılar
- `docs/PRODUCT_REQUIREMENTS.md`
- V1 scope / non-goals bölümü
- V1 success criteria

### Aşama 0 tamamlanma kapısı
Başka bir geliştirici yalnızca dokümanı okuyarak uygulamanın amacını ve V1 kapsamını doğru anlatabiliyorsa tamamlanır.

### Bu aşama sonunda uygulamanın durumu
Henüz kod yoktur; fakat **ne inşa edeceğimiz kesinleşmiştir**.

---

# AŞAMA 1 — ÖĞRENME VE MASTERY MODELİNİ TASARLA

## Amaç
“Dersi tamamladı = öğrendi” yanlışını ortadan kaldıracak ölçüm sistemini tasarlamak.

## 1.1 Bilgi birimleri
- [ ] `Domain → Module → Topic → Skill → Learning Objective` hiyerarşisini kesinleştir.
- [ ] Topic ile skill arasındaki farkı tanımla.
- [ ] Her learning objective’in ölçülebilir olmasını zorunlu kıl.
- [ ] Bir topic’in hangi alt becerilerden oluşacağını tanımla.

## 1.2 Topic durumları
- [ ] `locked` durumunu tanımla.
- [ ] `available` durumunu tanımla.
- [ ] `learning` durumunu tanımla.
- [ ] `mastered` durumunu tanımla.
- [ ] `weakening` / unutuluyor durumunu tanımla.
- [ ] `remediation_required` durumunu tanımla.

## 1.3 Mastery sinyalleri
- [ ] Teori quiz sinyalini tanımla.
- [ ] Kodlama görevi sinyalini tanımla.
- [ ] Debugging sinyalini tanımla.
- [ ] Açıklama/Feynman sinyalini tanımla.
- [ ] Transfer sorusu sinyalini tanımla.
- [ ] Gecikmeli retention testi sinyalini tanımla.
- [ ] Proje performansı sinyalini tanımla.
- [ ] Harcanan süreyi destekleyici sinyal olarak tanımla; tek başına başarı sayma.

## 1.4 AI/ipucu etkisi
- [ ] İpucu kullanım seviyelerini tanımla.
- [ ] AI’dan doğrudan çözüm alınırsa mastery’ye nasıl etki edeceğini belirle.
- [ ] AI yardımı sonrası zorunlu comprehension check tasarla.
- [ ] “Kodu AI yazdı ama kullanıcı gerçekten anladı mı?” doğrulama kuralını yaz.

## 1.5 Mastery formülü v0
- [ ] Sinyal ağırlıklarını belirle.
- [ ] Minimum mastery threshold belirle.
- [ ] Kritik becerilerde zorunlu alt koşullar belirle; örneğin kodlama başarısı yoksa yalnız teoriyle mastery verme.
- [ ] Tek sınavda yüksek puanla yanlış pozitif mastery oluşmasını engelle.
- [ ] Mastery confidence kavramını ekleyip eklemeyeceğimizi kararlaştır.

## 1.6 Unutma modeli
- [ ] İlk spaced repetition aralıklarını belirle.
- [ ] Başarılı tekrar sonrası aralığın nasıl büyüyeceğini belirle.
- [ ] Başarısız tekrar sonrası mastery düşüşünü belirle.
- [ ] Uzun süre kullanılmayan skill için decay mantığını belirle.

### Üretilecek çıktılar
- `docs/LEARNING_ENGINE_SPEC.md`
- Mastery Formula v0
- Topic state machine
- AI-help penalty/verification kuralları

### Aşama 1 tamamlanma kapısı
Aynı kullanıcı performans verisi verildiğinde sistemin neden `%X mastery` verdiği açıklanabilir ve tekrar hesaplanabilir olmalıdır.

### Bu aşama sonunda uygulamanın durumu
Kod yoktur; fakat **“öğrenildi mi?” sorusunun cevabı matematiksel ve mantıksal hale gelmiştir**.

---

# AŞAMA 2 — ADAPTİF GÜNLÜK PLANLAMA MOTORUNU TASARLA

## Amaç
Uygulamanın her gün kullanıcının yerine “Bugün ne çalışmalıyım?” kararını vermesini sağlamak.

## 2.1 Günlük kapasite
- [ ] Günlük normal çalışma süresini tanımlama şekli.
- [ ] Kısa gün / normal gün / yoğun gün seçenekleri.
- [ ] Minimum görev bloğu süresi.
- [ ] Mola politikası.
- [ ] Gün ortasında “bugün daha az vaktim var” değişikliğini destekleme kuralı.

## 2.2 Görev kategorileri
- [ ] Yeni konu öğrenme.
- [ ] Remediation.
- [ ] Retention tekrarı.
- [ ] Kodlama uygulaması.
- [ ] Debugging.
- [ ] İngilizce.
- [ ] Proje çalışması.
- [ ] Gün sonu mikro değerlendirme.

## 2.3 Öncelik puanı
- [ ] Kritik zayıf prerequisite’e öncelik ver.
- [ ] Süresi gelmiş retention tekrarına öncelik ver.
- [ ] Kariyer rotasında sıradaki yeni konuya ağırlık ver.
- [ ] Çok uzun süredir ihmal edilen bağımsız alanı dengele.
- [ ] İngilizce paralel hattının tamamen kaybolmasını engelle.
- [ ] Aynı tür görevin bütün günü kaplamasını önle.

## 2.4 Prerequisite davranışı
- [ ] Hard prerequisite tanımla.
- [ ] Soft prerequisite tanımla.
- [ ] Hard prerequisite başarısızsa bağımlı topic’i kilitle.
- [ ] Bağımsız dalların ilerlemesine izin ver.
- [ ] Bir konudaki başarısızlığın tüm curriculum’u durdurmasını engelle.

## 2.5 Hızlı öğrenme
- [ ] Çok yüksek başlangıç başarısında diagnostic test uygula.
- [ ] Gerçekten bilinen içeriği atlama mekanizması tasarla.
- [ ] Tek kolay quiz ile content skip yapma.
- [ ] Skip edilen konuda ileride retention doğrulaması yap.

## 2.6 Kaçırılan günler
- [ ] 1 gün kaçırıldı senaryosu.
- [ ] 3–4 gün kaçırıldı senaryosu.
- [ ] 1 hafta+ ara senaryosu.
- [ ] Birikmiş tüm görevleri ertesi güne yığmayı yasakla.
- [ ] Öncelikleri yeniden hesaplayarak sağlıklı dönüş planı üret.

## 2.7 Açıklanabilir planner
- [ ] Her plan değişikliği için reason code üret.
- [ ] Kullanıcıya sade dilde “Neden bunu bugün çalışıyorum?” açıklaması göster.
- [ ] Örn: “Pointers retention skorun düştüğü için 20 dk tekrar eklendi.”

## 2.8 Simülasyon
- [ ] Çok hızlı öğrenen öğrenci profili.
- [ ] Tek bir temel konuda takılan profil.
- [ ] Teknikte hızlı, İngilizcede yavaş profil.
- [ ] İngilizcede hızlı, teknikte yavaş profil.
- [ ] Sık gün kaçıran profil.
- [ ] AI’dan sürekli yardım alan profil.
- [ ] En az 20 sanal öğrenci akışıyla planner test et.

### Üretilecek çıktılar
- `docs/ADAPTIVE_PLANNER_SPEC.md`
- Planner decision table
- Planner pseudocode
- Simulation scenarios

### Aşama 2 tamamlanma kapısı
Aynı geçmiş verisiyle planner tutarlı günlük plan üretmeli ve her görevin neden seçildiğini açıklayabilmelidir.

### Bu aşama sonunda uygulamanın durumu
Henüz kod yoktur; fakat **uygulamanın beyni nasıl karar verecek tamamen tanımlanmıştır**.

---

# AŞAMA 3 — SINAV VE DEĞERLENDİRME SİSTEMİNİ TASARLA

## Amaç
Günlük, haftalık ve aylık ölçümlerin gerçekten öğrenmeyi ölçmesini ve planner’ı beslemesini sağlamak.

## 3.1 Günlük mikro değerlendirme
- [ ] Çoktan seçmeli soru türleri.
- [ ] Kısa cevap.
- [ ] Kod çıktısı tahmini.
- [ ] Kod tamamlama.
- [ ] Mini coding task.
- [ ] Debugging sorusu.
- [ ] “Kendi cümlenle açıkla” sorusu.
- [ ] Eski konudan retrieval sorusu.

## 3.2 Haftalık sınav
- [ ] Yeni konuların oranı.
- [ ] Eski/retention konularının oranı.
- [ ] Kodlama bölümünün ağırlığı.
- [ ] Debugging bölümünün ağırlığı.
- [ ] İngilizce bölümünün ağırlığı.
- [ ] Süre ve zorluk modeli.
- [ ] Zayıf alanı sonraki haftaya remediation olarak aktarma.

## 3.3 Aylık yeterlilik sınavı
- [ ] Teori.
- [ ] Uygulamalı kodlama.
- [ ] Debugging.
- [ ] Feynman açıklaması.
- [ ] Teknik İngilizce.
- [ ] 1 aylık retention testi.
- [ ] Birleştirici mini proje/görev gerekip gerekmediğini belirle.

## 3.4 Soru bankası
- [ ] Soru metadata şeması.
- [ ] Topic/skill etiketi.
- [ ] Zorluk etiketi.
- [ ] Soru türü.
- [ ] Beklenen çözüm/rubric.
- [ ] Distractor kalitesi.
- [ ] Aynı sorunun ezberlenmesini önleyen varyasyonlar.

## 3.5 AI-generated soru güvenliği
- [ ] AI sorusu üretirse doğrulama yaklaşımı.
- [ ] Deterministik cevaplı sorularda otomatik validator.
- [ ] Açık uçlu sorularda rubric tabanlı değerlendirme.
- [ ] Hatalı/şüpheli AI sorusunu mastery hesabından çıkarabilme.

### Üretilecek çıktılar
- `docs/ASSESSMENT_SYSTEM_SPEC.md`
- Günlük/haftalık/aylık sınav şablonları
- Question schema
- Scoring rubrics

### Aşama 3 tamamlanma kapısı
Örnek 1 haftalık kullanıcı verisi için sınav sonucu doğru skill’lere yansıyıp gelecek planı değiştirebilmelidir.

### Bu aşama sonunda uygulamanın durumu
Ölçme sistemi kağıt üzerinde tamamdır.

---

# AŞAMA 4 — CURRICULUM VE KNOWLEDGE GRAPH MİMARİSİNİ TASARLA

## Amaç
3 yıllık yolu sabit 1095 günlük liste yerine prerequisite bağlantıları olan genişleyebilir bilgi ağına dönüştürmek.

## 4.1 Ana domain haritası
- [ ] Technical English.
- [ ] Computer Fundamentals.
- [ ] C.
- [ ] Linux.
- [ ] Data Structures & Algorithms.
- [ ] Modern C++.
- [ ] OS & Memory.
- [ ] Concurrency.
- [ ] Networking.
- [ ] Distributed Systems.
- [ ] GPU Architecture.
- [ ] CUDA.
- [ ] Triton.
- [ ] ML/LLM Systems Fundamentals.
- [ ] Inference Engines.
- [ ] Multi-GPU / NCCL / RDMA.
- [ ] AI Infrastructure.
- [ ] Open Source & Career Readiness.

## 4.2 Topic metadata
- [ ] Title.
- [ ] Açıklama.
- [ ] Learning objectives.
- [ ] Prerequisites.
- [ ] Estimated effort.
- [ ] Criticality.
- [ ] Assessment types.
- [ ] Remediation seçenekleri.
- [ ] Kaynaklar.
- [ ] Career relevance.

## 4.3 İlk 8–12 haftalık curriculum
- [ ] Computer fundamentals ayrıntılandır.
- [ ] C foundations ayrıntılandır.
- [ ] Memory foundations ayrıntılandır.
- [ ] Linux fundamentals ayrıntılandır.
- [ ] İlk data structures konularını ekle.
- [ ] Paralel English A0→A1/A2 konularını ekle.
- [ ] Tüm prerequisite linklerini çiz.
- [ ] Her topic için ölçülebilir learning objective yaz.

## 4.4 Curriculum QA
- [ ] Eksik prerequisite kontrolü.
- [ ] Circular dependency kontrolü.
- [ ] Gereksiz akademik dolgu kontrolü.
- [ ] Kariyer hedefiyle ilişki kontrolü.
- [ ] AI çağında düşük ROI ezberleri ayıkla.
- [ ] Aşırı zor topic’in erken yerleştirilip yerleştirilmediğini kontrol et.

### Üretilecek çıktılar
- `docs/CURRICULUM_GRAPH_SPEC.md`
- İlk 8–12 haftalık curriculum dataset
- Graph QA raporu

### Aşama 4 tamamlanma kapısı
İlk 8–12 haftalık bütün topic’ler prerequisite ve learning objective açısından tutarlı bir DAG/graph oluşturmalıdır.

### Bu aşama sonunda uygulamanın durumu
Öğrenilecek ilk gerçek içerik yolunun **iskeleti hazırdır**.

---

# AŞAMA 5 — İNGİLİZCE PARALEL HATTI TASARLA

## Amaç
A0 İngilizceyi teknik eğitimi geciktirmeden A1→A2→B1→B2’ye taşımak.

## 5.1 Başlangıç ölçümü
- [ ] A0 diagnostic.
- [ ] Reading diagnostic.
- [ ] Vocabulary diagnostic.
- [ ] Listening diagnostic.
- [ ] Basit speaking/writing diagnostic.

## 5.2 Seviye hedefleri
- [ ] A1’de compiler/terminal temel mesajlarını anlama.
- [ ] A2’de kısa dokümantasyon ve README okuma/yazma.
- [ ] B1’de GitHub issue/PR ve teknik doküman okuma.
- [ ] B2’de teknik mülakat ve mimari tartışma.

## 5.3 Günlük İngilizce bileşeni
- [ ] Genel temel grammar.
- [ ] High-frequency vocabulary.
- [ ] Technical vocabulary.
- [ ] Compiler error reading.
- [ ] Documentation reading.
- [ ] Commit/README writing.
- [ ] GitHub communication.
- [ ] Listening.
- [ ] Speaking.

## 5.4 Teknik entegrasyon
- [ ] O gün öğrenilen C/Linux kelimelerini English görevine aktar.
- [ ] Zamanla Türkçe açıklama oranını azalt.
- [ ] Teknik kelimeleri yalnız çeviri değil bağlam içinde öğret.
- [ ] İngilizce başarısızlığının teknik prerequisite’i gereksiz yere kilitlemesini engelle.

## 5.5 İngilizce mastery
- [ ] Reading mastery.
- [ ] Writing mastery.
- [ ] Listening mastery.
- [ ] Speaking mastery.
- [ ] Technical vocabulary mastery.
- [ ] Technical explanation mastery.

### Üretilecek çıktılar
- `docs/ENGLISH_TRACK_SPEC.md`
- İlk 12 haftalık English curriculum
- CEFR + technical skill geçiş kriterleri

### Aşama 5 tamamlanma kapısı
İlk 12 haftada İngilizce görevleri teknik görevlerle çakışmadan paralel planlanabilir olmalıdır.

### Bu aşama sonunda uygulamanın durumu
Teknik ve İngilizce eğitim artık **tek birleşik program** olarak tasarlanmıştır.

---

# AŞAMA 6 — ÜRÜN GEREKSİNİMLERİ, EKRANLAR VE UX’İ KİLİTLE

## Amaç
Kod başlamadan önce kullanıcının uygulamada yaşayacağı tüm kritik akışları tasarlamak.

## 6.1 Bilgi mimarisi
- [ ] Ana navigasyon yapısı.
- [ ] Bugün ekranı.
- [ ] Çalışma oturumu.
- [ ] Yol/skill haritası.
- [ ] İlerleme.
- [ ] Sınavlar.
- [ ] Ayarlar.

## 6.2 Ana ekran
- [ ] 1095 gün sayacı yok.
- [ ] Kariyer `% tamamlandı` göstergesi yok.
- [ ] Devam eden topic.
- [ ] Mastery durumu.
- [ ] Bugünkü toplam süre.
- [ ] Bugünkü görevlar.
- [ ] Yaklaşan sınav/retention.
- [ ] Büyük “Çalışmaya Başla / Devam Et” CTA.

## 6.3 Günlük çalışma akışı
- [ ] Görev açılışı.
- [ ] Kısa konu anlatımı.
- [ ] Etkileşimli örnek.
- [ ] Uygulama.
- [ ] Mini ölçüm.
- [ ] Sonraki göreve geçiş.
- [ ] Oturum sonunda gün özeti.

## 6.4 Sınav UX
- [ ] Günlük mini quiz.
- [ ] Haftalık sınav.
- [ ] Aylık sınav.
- [ ] Sonuçları “not” yerine skill breakdown olarak göster.
- [ ] “Programında ne değişti?” bölümünü göster.

## 6.5 Skill/progress UX
- [ ] Domain mastery görünümü.
- [ ] Topic durumları.
- [ ] Zayıf alanlar.
- [ ] Retention riski.
- [ ] “Henüz başlamadı” alanları sahte `%0 başarısızlık` gibi göstermeme.

## 6.6 Tasarım sistemi
- [ ] Görsel yön / moodboard.
- [ ] Renk tokenları.
- [ ] Light/Dark tema.
- [ ] Tipografi.
- [ ] Spacing grid.
- [ ] Kartlar.
- [ ] Butonlar.
- [ ] Inputlar.
- [ ] Progress/mastery göstergeleri.
- [ ] Mikro animasyon ilkeleri.

## 6.7 Wireframe/prototip
- [ ] Ana ekran wireframe.
- [ ] Daily session wireframe.
- [ ] Lesson wireframe.
- [ ] Quiz wireframe.
- [ ] Exam result wireframe.
- [ ] Progress wireframe.
- [ ] Settings wireframe.
- [ ] Kritik kullanıcı akışlarını prototipte test et.

### Üretilecek çıktılar
- `docs/UX_SPEC.md`
- Screen inventory
- Navigation map
- Wireframes/prototype
- Design system spec

### Aşama 6 tamamlanma kapısı
Kod yazmadan uygulamanın başlangıçtan günlük çalışma bitişine kadar tüm kritik ekran akışı anlaşılır olmalıdır.

### Bu aşama sonunda uygulamanın durumu
**Ürün ve tasarım planı hazırdır. M0 tamamlanır.**

---

# AŞAMA 7 — TEKNİK MİMARİ VE VERİ MODELİNİ KESİNLEŞTİR

## Amaç
Mobil uygulamanın hangi teknolojiyle ve hangi veri yapısıyla geliştirileceğini kararlaştırmak.

## 7.1 Mobil teknoloji seçimi
- [ ] Flutter değerlendir.
- [ ] Kotlin + Jetpack Compose değerlendir.
- [ ] React Native değerlendir.
- [ ] Offline kullanım, UI kalitesi, geliştirme hızı ve APK üretimini karşılaştır.
- [ ] Tek teknoloji seç ve karar kaydına ekle.

## 7.2 Veri saklama
- [ ] Local-first yaklaşımı kesinleştir.
- [ ] SQLite/Room/Drift vb. seçimi yap.
- [ ] İçerik verisi ile kullanıcı ilerleme verisini ayır.
- [ ] Migration stratejisi belirle.
- [ ] Veri kaybını önlemek için export/backup temelini planla.

## 7.3 Domain veri modeli
- [ ] Domain.
- [ ] Module.
- [ ] Topic.
- [ ] Skill.
- [ ] LearningObjective.
- [ ] PrerequisiteEdge.
- [ ] LearningTask.
- [ ] AssessmentItem.
- [ ] Attempt.
- [ ] MasteryState.
- [ ] ReviewSchedule.
- [ ] DailyPlan.
- [ ] StudySession.
- [ ] Exam.
- [ ] ExamResult.
- [ ] AIInteraction.

## 7.4 Servis sınırları
- [ ] Curriculum service.
- [ ] Mastery service.
- [ ] Planner service.
- [ ] Assessment service.
- [ ] Retention service.
- [ ] AI tutor adapter.
- [ ] Progress analytics service.

## 7.5 AI entegrasyon mimarisi
- [ ] Provider’dan bağımsız interface.
- [ ] AI kapalıyken temel uygulamanın çalışmaya devam etmesi.
- [ ] Prompt template/version sistemi.
- [ ] AI response validation.
- [ ] Maliyet/log kontrolü kişisel kullanım için sade tasarla.

## 7.6 Test stratejisi
- [ ] Unit test.
- [ ] Planner simulation test.
- [ ] Database migration test.
- [ ] UI smoke test.
- [ ] Golden/snapshot test gerekip gerekmediğini belirle.

### Üretilecek çıktılar
- `docs/TECH_ARCHITECTURE.md`
- `docs/DATA_MODEL.md`
- Architecture diagram
- Technology decision record

### Aşama 7 tamamlanma kapısı
Coding agent hangi klasörün ne işe yaradığını ve modeller arası ilişkileri tahmin etmeden anlayabilmelidir.

### Bu aşama sonunda uygulamanın durumu
Teknik proje planı hazırdır.

---

# AŞAMA 8 — MOBİL PROJE İSKELETİ VE TASARIM SİSTEMİNİ KUR

## Amaç
İlk kez gerçek çalışan uygulama projesini oluşturmak.

## 8.1 Proje kurulumu
- [ ] Mobil proje oluştur.
- [ ] Paket/klasör mimarisini kur.
- [ ] Lint/format kuralları.
- [ ] Build config.
- [ ] Debug/release yapı ayrımı.

## 8.2 Navigation
- [ ] Alt navigasyon veya seçilen navigation modelini uygula.
- [ ] Ana ekran route.
- [ ] Study session route.
- [ ] Progress route.
- [ ] Exams route.
- [ ] Settings route.

## 8.3 Design system
- [ ] Color tokens.
- [ ] Typography tokens.
- [ ] Spacing tokens.
- [ ] Button komponentleri.
- [ ] Card komponentleri.
- [ ] Mastery/progress komponentleri.
- [ ] Light/Dark tema.

## 8.4 Local database
- [ ] Database oluştur.
- [ ] İlk schema migration.
- [ ] Repository layer.
- [ ] Seed data yükleme.
- [ ] Basit read/write testi.

## 8.5 Temel uygulama sağlığı
- [ ] Uygulama açılıyor.
- [ ] Navigation çalışıyor.
- [ ] Tema değişiyor.
- [ ] Local veri app restart sonrası kalıyor.
- [ ] Crash-free temel smoke test.

### Üretilecek çıktılar
- Çalışan mobil proje
- Tasarım sistemi komponentleri
- Local DB v1

### Aşama 8 tamamlanma kapısı
Debug APK/emulator üzerinde temel ekranlar gezilebilir ve veri kalıcıdır.

### Bu aşama sonunda uygulamanın durumu
**İlk çalışan mobil iskelet oluşur. M1 tamamlanır.**

---

# AŞAMA 9 — ÇEKİRDEK GÜNLÜK ÖĞRENME AKIŞI MVP’SİNİ GELİŞTİR

## Amaç
Kullanıcının uygulamayı açıp o günkü planını görmesi ve çalışma oturumunu gerçekten tamamlayabilmesi.

## 9.1 Today ekranı
- [ ] Günlük görev listesi.
- [ ] Toplam tahmini süre.
- [ ] Current topic.
- [ ] Current mastery.
- [ ] “Başla/Devam Et” CTA.

## 9.2 Task runner
- [ ] Lesson task.
- [ ] Reading task.
- [ ] Practice task.
- [ ] Quiz task.
- [ ] Coding/debugging task placeholder.
- [ ] English task.
- [ ] Retention task.

## 9.3 Session state
- [ ] Başlat.
- [ ] Pause.
- [ ] Resume.
- [ ] Skip request.
- [ ] Complete.
- [ ] App kapanırsa session restore.

## 9.4 Günlük mikro quiz
- [ ] Soru göster.
- [ ] Cevap kaydet.
- [ ] Doğru/yanlış geri bildirim.
- [ ] Attempt kaydı.
- [ ] Skill’e bağla.

## 9.5 Gün sonu
- [ ] Tamamlanan görevler.
- [ ] Zorlanılan noktalar.
- [ ] Mastery değişiklikleri.
- [ ] Yarın için planner tetikleme.

### Üretilecek çıktılar
- Günlük çalışma akışı MVP
- Task engine v1
- Quiz v1

### Aşama 9 tamamlanma kapısı
Seed curriculum ile kullanıcı bir günü baştan sona uygulamada çalışabilmelidir.

### Bu aşama sonunda uygulamanın durumu
**İlk defa gerçek günlük kullanım mümkün olur.**

---

# AŞAMA 10 — MASTERY VE ADAPTİF PLANNER’I KODA DÖK

## Amaç
Sabit todo listesini gerçek adaptif öğrenme sistemine dönüştürmek.

## 10.1 Mastery Engine v1
- [ ] Attempt’lerden mastery hesapla.
- [ ] Assessment type ağırlıkları.
- [ ] AI yardım etkisi.
- [ ] Threshold durum geçişleri.
- [ ] Mastery history kaydı.

## 10.2 Prerequisite engine
- [ ] Hard lock.
- [ ] Soft warning.
- [ ] Mastered prerequisite kontrolü.
- [ ] Bağımsız dalların devamı.

## 10.3 Planner Engine v1
- [ ] Günlük kapasite al.
- [ ] Candidate tasks üret.
- [ ] Priority score hesapla.
- [ ] Süreye sığdır.
- [ ] Çeşitlilik kuralı uygula.
- [ ] English minimum payını koru.

## 10.4 Replan
- [ ] Görev başarısız olunca aynı gün/ertesi gün etkisi.
- [ ] Gün kaçırılınca replan.
- [ ] Günlük kapasite değişince replan.
- [ ] Mastery artınca yeni topic aç.

## 10.5 Explanation
- [ ] Planner reason code.
- [ ] UI’da sade açıklama.

## 10.6 Test
- [ ] Sanal kullanıcı testleri.
- [ ] Pointer’da zorlanan senaryo.
- [ ] Hızlı öğrenen senaryo.
- [ ] 1 hafta ara veren senaryo.

### Üretilecek çıktılar
- Mastery Engine v1
- Adaptive Planner v1
- Prerequisite Engine v1

### Aşama 10 tamamlanma kapısı
İki farklı performans geçmişine sahip kullanıcıya sistem gerçekten farklı günlük plan üretmelidir.

### Bu aşama sonunda uygulamanın durumu
**Uygulama artık sabit kurs değil, adaptif öğrenme uygulamasıdır. M2 tamamlanır.**

---

# AŞAMA 11 — HAFTALIK/AYLIK SINAV, RETENTION VE REMEDIATION’I GELİŞTİR

## Amaç
Uzun vadeli öğrenmeyi ve eksik konu müdahalesini gerçek hale getirmek.

## 11.1 Haftalık sınav
- [ ] Otomatik sınav composition.
- [ ] Skill coverage kontrolü.
- [ ] Yeni/eski konu dengesi.
- [ ] Puanlama.
- [ ] Skill breakdown.
- [ ] Sonraki haftaya etkisi.

## 11.2 Aylık sınav
- [ ] Comprehensive assessment oluştur.
- [ ] Kodlama/debugging bölümü.
- [ ] Retention bölümü.
- [ ] English bölümü.
- [ ] Sonuç sonrası curriculum priority update.

## 11.3 Spaced repetition
- [ ] Review schedule oluştur.
- [ ] Due review planner’a aday olur.
- [ ] Başarıda interval genişlet.
- [ ] Başarısızlıkta interval küçült/mastery düşür.

## 11.4 Remediation engine
- [ ] Zayıf kök skill tespiti.
- [ ] Alternatif anlatım paketi.
- [ ] Daha kolay örnek.
- [ ] Debugging görevi.
- [ ] Micro drill.
- [ ] Prerequisite’e geri dönüş.

## 11.5 Program değişiklik raporu
- [ ] “Bu hafta neden değişti?”
- [ ] Hangi topic ertelendi?
- [ ] Hangi remediation eklendi?
- [ ] Hangi eski topic tekrar programa girdi?

### Üretilecek çıktılar
- Weekly Exam v1
- Monthly Exam v1
- Retention Engine v1
- Remediation Engine v1

### Aşama 11 tamamlanma kapısı
Kasıtlı olarak zayıf sonuç verilen test kullanıcısında sonraki plan anlamlı biçimde değişmelidir.

### Bu aşama sonunda uygulamanın durumu
Uygulama **öğrenmeyi haftalar ve aylar boyunca takip edebilir**.

---

# AŞAMA 12 — AI TUTOR VE AKILLI DEĞERLENDİRME KATMANINI GELİŞTİR

## Amaç
AI’yı kod yazan bir kestirme değil, kişiselleştirilmiş öğretmen ve evaluator olarak kullanmak.

## 12.1 Tutor davranış sözleşmesi
- [ ] Doğrudan cevabı ne zaman vermeyeceğini tanımla.
- [ ] Socratic hint katmanları.
- [ ] Kullanıcı seviyesine göre anlatım.
- [ ] Türkçe/İngilizce oranını seviyeye göre ayarla.

## 12.2 Yanlış analizi
- [ ] Yanlış cevabın kök nedenini sınıflandır.
- [ ] Kavram eksikliği vs dikkatsizlik ayrımı.
- [ ] Prerequisite açığı tespiti.
- [ ] Planner’a remediation sinyali gönder.

## 12.3 Alternatif anlatım
- [ ] Basitleştir.
- [ ] Analogy.
- [ ] Memory diagram.
- [ ] Code walkthrough.
- [ ] Counterexample.

## 12.4 Kod değerlendirme
- [ ] Kodun çalışıp çalışmadığını değerlendir.
- [ ] Kod kalitesi sinyali.
- [ ] Hata açıklaması.
- [ ] Kullanıcının kodu gerçekten anlayıp anlamadığını sorularla doğrula.

## 12.5 AI-generated code check
- [ ] “Bu satır neden var?”
- [ ] “Bunu kaldırırsak ne olur?”
- [ ] “Bu kodu farklı girdiye uyarlayabilir misin?”
- [ ] Transfer task ile anlama doğrulaması.

## 12.6 Açık uçlu cevaplar
- [ ] Rubric’e göre değerlendir.
- [ ] Confidence düşükse mastery’ye güçlü sinyal verme.
- [ ] Kullanıcıya spesifik feedback üret.

## 12.7 Provider abstraction
- [ ] AI sağlayıcısı değişse bile tutor interface değişmesin.
- [ ] AI yoksa non-AI fallback.

### Üretilecek çıktılar
- AI Tutor v1
- Open-ended evaluator v1
- Code comprehension checker

### Aşama 12 tamamlanma kapısı
Tutor aynı yanlışta sadece cevabı tekrarlamak yerine farklı pedagojik müdahale uygulayabilmelidir.

### Bu aşama sonunda uygulamanın durumu
Uygulama **kişisel AI öğretmen** niteliği kazanır.

---

# AŞAMA 13 — İLK 8–12 HAFTALIK GERÇEK EĞİTİM İÇERİĞİNİ ÜRET VE QA ET

## Amaç
Motoru gerçek, kaliteli ve sıfır seviyeye uygun içerikle kullanılabilir hale getirmek.

## 13.1 Computer Fundamentals
- [ ] CPU/RAM/storage.
- [ ] Binary basics.
- [ ] Program/process kavramı.
- [ ] Compile/run mantığı.

## 13.2 C Foundations
- [ ] Variables/types.
- [ ] Operators.
- [ ] Conditions.
- [ ] Loops.
- [ ] Functions.
- [ ] Arrays.
- [ ] Strings.
- [ ] Struct başlangıcı.

## 13.3 Memory Foundations
- [ ] Address.
- [ ] Pointer basics.
- [ ] Stack/heap.
- [ ] Lifetime.
- [ ] malloc/free başlangıcı.

## 13.4 Linux Foundations
- [ ] Filesystem.
- [ ] Terminal navigation.
- [ ] Permissions basics.
- [ ] Processes basics.
- [ ] GCC/Clang temel kullanım.
- [ ] Git temel akışı.

## 13.5 İngilizce A0→A1/A2
- [ ] Günlük temel grammar.
- [ ] İlk teknik vocabulary setleri.
- [ ] Error message reading.
- [ ] Basit README/commit tasks.

## 13.6 Assessment content
- [ ] Her topic için yeterli mikro soru.
- [ ] Coding tasks.
- [ ] Debugging tasks.
- [ ] Transfer tasks.
- [ ] Haftalık exam pools.
- [ ] İlk monthly exam.

## 13.7 Content QA
- [ ] Teknik doğruluk.
- [ ] Sıfır seviye anlaşılabilirlik.
- [ ] Prerequisite uyumu.
- [ ] Gereksiz jargon kontrolü.
- [ ] Soru-cevap doğruluğu.
- [ ] Difficulty calibration.

### Üretilecek çıktılar
- İlk 8–12 haftalık production curriculum
- Assessment bank v1
- Content QA report

### Aşama 13 tamamlanma kapısı
Kullanıcı dış kaynağa ihtiyaç duymadan uygulamada ilk 8–12 haftalık çekirdek rotayı çalışabilecek içeriğe sahip olmalıdır.

### Bu aşama sonunda uygulamanın durumu
**Gerçek öğrenme ürünü oluşur. M3 tamamlanır.**

---

# AŞAMA 14 — İLERLEME, ANALİTİK, AYARLAR VE GÜNLÜK KULLANIM ARAÇLARINI TAMAMLA

## Amaç
Kullanıcının durumunu anlamasını sağlamak ancak sahte ilerleme göstergeleri oluşturmamak.

## 14.1 Skill analytics
- [ ] Domain mastery.
- [ ] Topic mastery.
- [ ] Confidence/retention durumu.
- [ ] Zayıf alanlar.
- [ ] Güçlenen alanlar.

## 14.2 Öğrenme geçmişi
- [ ] Çalışma süreleri.
- [ ] Assessment geçmişi.
- [ ] Mastery değişim grafiği.
- [ ] Remediation geçmişi.

## 14.3 Progress gösterim kuralları
- [ ] Gün sayısı başarı metriği değil.
- [ ] Streak ikincil ve opsiyonel.
- [ ] Mastery ana metrik.
- [ ] “Henüz başlamadı” ile “başarısız”ı ayır.

## 14.4 Ayarlar
- [ ] Günlük hedef süre.
- [ ] Kısa/normal/yoğun gün.
- [ ] Tema.
- [ ] Bildirim saati.
- [ ] AI tutor davranış tercihlerinin gerekli olanları.

## 14.5 Bildirimler
- [ ] Bugünkü plan hazır.
- [ ] Due retention review.
- [ ] Weekly exam.
- [ ] Monthly exam.
- [ ] Kaçırılan gün için suçlayıcı olmayan dönüş bildirimi.

### Üretilecek çıktılar
- Progress dashboard
- Weakness view
- Settings
- Notifications

### Aşama 14 tamamlanma kapısı
Kullanıcı “Neyi biliyorum, nerede eksiğim, neden bugün bu görev var?” sorularının cevabını uygulamadan görebilmelidir.

---

# AŞAMA 15 — UI/UX POLISH VE ERİŞİLEBİLİRLİK

## Amaç
Uygulamayı prototip görünümünden çıkarıp her gün açılması keyifli profesyonel mobil ürüne dönüştürmek.

## 15.1 Görsel polish
- [ ] Tüm ekranlarda spacing tutarlılığı.
- [ ] Typography hierarchy.
- [ ] Kart yoğunluğunu azalt.
- [ ] Ana CTA görünürlüğü.
- [ ] Dark/light theme tamamlama.

## 15.2 Motion
- [ ] Task completion mikro animasyonu.
- [ ] Mastery değişim animasyonu.
- [ ] Screen transition.
- [ ] Sınav sonucu animasyonu.
- [ ] Gereksiz animasyonlardan kaçın.

## 15.3 Kullanılabilirlik
- [ ] Büyük touch target.
- [ ] Klavye davranışı.
- [ ] Uzun metin scroll.
- [ ] Hata durumları.
- [ ] Loading/empty states.

## 15.4 Accessibility
- [ ] Kontrast.
- [ ] Font scaling.
- [ ] Screen reader temel etiketleri.
- [ ] Renk tek başına durum belirtmesin.

### Üretilecek çıktılar
- Polished UI
- Accessibility checklist
- UX QA report

### Aşama 15 tamamlanma kapısı
Ana akışlarda placeholder/prototip hissi kalmamalı; UI günlük kullanıma uygun görünmelidir.

---

# AŞAMA 16 — GERÇEK KULLANIM PİLOTU, KALİBRASYON VE QA

## Amaç
Kâğıt üzerinde doğru görünen sistemin gerçek kullanımda da doğru çalıştığını görmek.

## 16.1 Pilot başlangıcı
- [ ] Temiz kullanıcı verisiyle başla.
- [ ] Gerçek günlük süreyi ayarla.
- [ ] En az 2–4 hafta uygulamayı gerçekten kullan.

## 16.2 Planner gözlemi
- [ ] Fazla tekrar veriyor mu?
- [ ] Çok hızlı yeni konu açıyor mu?
- [ ] Günlük süre gerçekçi mi?
- [ ] İngilizce oranı uygun mu?
- [ ] Zayıf konu gerçekten toparlanıyor mu?

## 16.3 Mastery kalibrasyonu
- [ ] Çok kolay mastery veriyor mu?
- [ ] Gereksiz sert mi?
- [ ] Retention düşüşü mantıklı mı?
- [ ] AI yardım penalty’si doğru mu?

## 16.4 Assessment kalibrasyonu
- [ ] Haftalık sınav çok uzun/kısa mı?
- [ ] Soru zorluk dağılımı.
- [ ] Aynı şeyleri ezberletiyor mu?
- [ ] Transfer soruları yeterli mi?

## 16.5 Teknik QA
- [ ] Crash test.
- [ ] App restart recovery.
- [ ] Database integrity.
- [ ] Migration.
- [ ] Offline kullanım.
- [ ] Notification davranışı.
- [ ] Battery/performance temel kontrol.

## 16.6 Düzeltme döngüsü
- [ ] Pilot bulgularını issue listesine çevir.
- [ ] Kritik hataları düzelt.
- [ ] Planner/mastery parametrelerini yeniden kalibre et.
- [ ] İkinci pilot turu yap.

### Üretilecek çıktılar
- Pilot report
- Calibration changes
- QA checklist

### Aşama 16 tamamlanma kapısı
En az birkaç haftalık gerçek kullanımda sistem sürekli elle müdahale gerektirmeden faydalı program üretmelidir.

### Bu aşama sonunda uygulamanın durumu
**Kaliteli günlük kullanım sürümü oluşur. M4 tamamlanır.**

---

# AŞAMA 17 — RELEASE APK VE KULLANIMA HAZIR SÜRÜM

## Amaç
Stabil, kurulabilir ve veri kaybı riski azaltılmış gerçek release sürümünü üretmek.

## 17.1 Release hazırlığı
- [ ] Versioning.
- [ ] Release build config.
- [ ] App icon/name/splash.
- [ ] Debug özelliklerini kapat.
- [ ] Release notes.

## 17.2 Veri güvenilirliği
- [ ] Backup/export.
- [ ] Restore.
- [ ] Database migration testi.
- [ ] Uygulama güncellemesinde progress kaybolmama testi.

## 17.3 Final regression
- [ ] Today flow.
- [ ] Lesson.
- [ ] Quiz.
- [ ] Mastery.
- [ ] Planner.
- [ ] Weekly exam.
- [ ] Monthly exam.
- [ ] Retention.
- [ ] AI tutor.
- [ ] English track.
- [ ] Notifications.
- [ ] Theme.

## 17.4 APK
- [ ] Release APK üret.
- [ ] Gerçek Android cihazına kur.
- [ ] Fresh install test.
- [ ] Update install test.
- [ ] Son smoke test.

## 17.5 Dokümantasyon
- [ ] README güncelle.
- [ ] Kullanım notları.
- [ ] Bilinen limitler.
- [ ] Backup yöntemi.

### Üretilecek çıktılar
- Release APK
- Release notes
- Backup/restore prosedürü

### Aşama 17 tamamlanma kapısı
Release APK gerçek cihazda kurulup günlük eğitim akışında stabil kullanılabiliyorsa tamamlanır.

### Bu aşama sonunda uygulamanın durumu
# ✅ UYGULAMA HAZIRDIR — M5

Bundan sonraki işler uygulamanın temelini bitirmek değil; içeriğini ve kariyer yeteneklerini genişletmektir.

---

# AŞAMA 18 — UZUN VADELİ CURRICULUM VE KARİYER KATMANINI GENİŞLET

## Amaç
Uygulama kullanılırken yıllar içinde gereken ileri curriculum’u ve işe hazırlık özelliklerini eklemek.

## 18.1 Modern C++ paketi
- [ ] RAII.
- [ ] Smart pointers.
- [ ] STL.
- [ ] Move semantics.
- [ ] Templates.
- [ ] Performance profiling.

## 18.2 Systems paketi
- [ ] OS internals.
- [ ] Virtual memory.
- [ ] Processes/threads.
- [ ] Concurrency.
- [ ] Networking.
- [ ] Async I/O.

## 18.3 Distributed Systems paketi
- [ ] RPC.
- [ ] Replication.
- [ ] Consistency.
- [ ] Consensus/Raft.
- [ ] Sharding.
- [ ] Failure handling.

## 18.4 GPU/CUDA paketi
- [ ] GPU architecture.
- [ ] CUDA basics.
- [ ] Memory hierarchy.
- [ ] Coalescing.
- [ ] Shared memory.
- [ ] Warp divergence.
- [ ] Nsight profiling.
- [ ] GEMM/Softmax kernels.

## 18.5 Triton/Inference paketi
- [ ] Triton kernels.
- [ ] PyTorch internals basics.
- [ ] KV cache.
- [ ] Continuous batching.
- [ ] Quantization.
- [ ] vLLM/SGLang concepts.

## 18.6 Multi-GPU / AI Infrastructure
- [ ] NCCL.
- [ ] Tensor parallelism.
- [ ] Pipeline parallelism.
- [ ] RDMA/RoCE/InfiniBand concepts.
- [ ] Cluster scheduling.
- [ ] Inference performance.

## 18.7 Open source
- [ ] GitHub issue reading.
- [ ] Good first issue.
- [ ] PR workflow.
- [ ] Code review English.
- [ ] Merged PR tracking.

## 18.8 Career readiness
- [ ] Gerçek iş ilanı skill matching.
- [ ] “Bu ilana ne kadar hazırım?” analizi.
- [ ] Eksik skill’leri curriculum’a bağlama.
- [ ] CV project evidence.
- [ ] Technical interview practice.
- [ ] English mock interviews.

## 18.9 Sürekli curriculum kalite kontrolü
- [ ] Yeni teknolojileri körü körüne ekleme.
- [ ] Gerçek iş ilanı sinyalleriyle öncelik doğrulama.
- [ ] Eski/düşük ROI içeriği kaldırma.
- [ ] Kullanıcının kariyer rotasına göre ağırlık güncelleme.

### Üretilecek çıktılar
- Genişleyen curriculum paketleri
- Career readiness engine
- Open-source progress tracking

### Aşama 18 tamamlanma tanımı
Bu aşama doğası gereği uzun vadeli ve iteratiftir. İlk tam career-readiness paketi hazırlandığında ana milestone tamamlanmış kabul edilir; curriculum güncellemeleri devam edebilir.

### Bu aşama sonunda uygulamanın durumu
**Kişisel adaptif eğitim uygulaması, tam AI Infrastructure kariyer koçuna dönüşür. M6.**

---

# HER AŞAMADA ZORUNLU ÇALIŞMA PROTOKOLÜ

Her aşama için sıra değişmez:

1. **Analiz et** — problemi ve seçenekleri çıkar.
2. **Karar ver** — yoruma açık noktaları kapat.
3. **Dokümante et** — ilgili spec/decision dosyasını güncelle.
4. **Uygula** — kod veya içerik gerekiyorsa gerçekleştir.
5. **Test et** — kabul kriterini gerçekten çalıştır.
6. **İşaretle** — yalnız test başarılıysa `[x]` yap.
7. **Tamamlanma notu yaz** — neyin yapıldığını ve nedenini kaydet.
8. **Progress log güncelle** — oturum sonucunu `docs/PROGRESS_LOG.md` içine ekle.
9. **Sonraki adıma geç** — başarısız acceptance test varsa aşama tamamlandı sayılmaz.

---

# ŞU ANKİ DURUM

**Aktif aşama:** AŞAMA 0 — Ürün Çerçevesini Kilitle

**Bir sonraki adım:** 0.1 Ana ürün amacı ve 0.2 V1 kapsamını kesinleştirmek.

Kodlamaya henüz başlanmayacak. Önce Aşama 0–7 arasındaki kritik kararlar tamamlanacak; ancak gereksiz dokümantasyon üretmek için proje geciktirilmeyecek. Her spec, doğrudan bir sonraki geliştirme kararını beslemek zorundadır.
