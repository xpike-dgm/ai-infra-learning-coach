# Project Progress Log

Bu dosya projenin oturumlar arası kalıcı ilerleme günlüğüdür. Ayrıntılı plan `MASTER_PLAN.md` / `EXECUTION_INDEX.md`; bu dosya ise ne zaman ne yapıldığını ve neden yapıldığını kronolojik olarak kaydeder.

---

### 2026-08-24 — Proje hafızası ve yürütme sistemi kuruldu

- Proje amacı, kariyer rotası, mastery ilkesi, knowledge graph, assessment, retention ve parallel English yönü kalıcı hale getirildi.
- GitHub kalıcı proje hafızası olarak belirlendi.

---

### 2026-08-24 — Master plan 19 aşamalı yürütme planına dönüştürüldü

- Proje 19 ana aşamaya ve acceptance kapılarına ayrıldı.
- Release APK'nin temel ürünün hazır olduğu nokta olduğu netleştirildi.

---

### 2026-08-24 — Sohbet aktarımı ve kalıcı handoff sistemi güçlendirildi

- `START_HERE.md`, `PROJECT_MASTER_CONTEXT.md`, `HANDOFF_STATE.md` oluşturuldu.
- Sohbet geçmişinin tek bilgi kaynağı olmaması kararlaştırıldı.

---

### 2026-08-24 — Sabit 1A/1B yürütme numaralandırması eklendi

- Aşamalar 1–19 olarak standardize edildi.
- `1A`, `1B`, `2A`, `3C`, `11F` gibi sabit adım kodları oluşturuldu.
- `EXECUTION_INDEX.md` ve `STEP_STATUS.md` devreye alındı.

---

### 2026-08-24 — 1A Ana ürün amacı tamamlandı

- Ürünün resmi amacı, günlük değer önerisi, klasik course/todo farkı, kanıtlanmış öğrenme ilkesi ve kariyer rotası ilişkisi kilitlendi.
- Çıktı: `docs/PRODUCT_REQUIREMENTS.md`.

---

### 2026-08-24 — 1B V1 kapsamı tamamlandı

- V1 Android odaklı gerçek günlük kullanım release'i olarak sınırlandı.
- Daily planner, mastery/prerequisite, assessments, retention, remediation, AI Tutor, parallel English, ilk production curriculum, local persistence, notifications, UI ve backup/restore V1 kapsamına alındı.
- Tam 3 yıllık curriculum, cloud sync, diğer platformlar, full voice tutor, full IDE/compiler ve career-market engine sonraya bırakıldı.
- Çıktı: `docs/V1_SCOPE.md`.

---

### 2026-08-24 — 1C V1 başarı kriterleri tamamlandı

- V1 için P0/P1/P2 acceptance modeli ve PASS / PASS WITH NOTES / FAIL / BLOCKED test sonuçları tanımlandı.
- Toplam 49 acceptance kriteri yazıldı.
- Tüm P0 kriterleri PASS olmadan release yapılmaması kararlaştırıldı.
- Kritik final akışları için bağımsız Test/QA AI zorunlu hale getirildi.
- Çıktı: `docs/V1_SUCCESS_CRITERIA.md`.

---

### 2026-08-24 — 1D Non-goals tamamlandı ve Aşama 1 kapandı

- Ürün seviyesi non-goals ile yalnız V1'e ertelenen özellikler ayrıldı.
- Sabit kurs, time/streak progress, tamamen LLM controlled curriculum, SaaS/social scope creep, missed-day task debt, full mobile IDE kimliği, sahte bilimsel kesinlik ve kariyer garantisi reddedildi.
- Çıktı: `docs/NON_GOALS.md`.
- **AŞAMA 1 tamamlandı.**

---

### 2026-08-24 — 2A Bilgi birimleri tamamlandı

**Tamamlananlar**
- Öğrenme motorunun resmî yapısı `Domain → Module → Topic → Skill → Learning Objective` olarak kesinleştirildi.
- `Domain/Module/Topic` curriculum organizasyon; `Skill/Learning Objective` gerçek öğrenme/ölçüm katmanı olarak ayrıldı.
- Canonical mastery'nin ana planner/prerequisite seviyesi Skill olarak belirlendi.
- Topic/Module/Domain mastery'nin Skill verilerinden derived görünüm olması kararlaştırıldı.
- Topic ↔ Skill many-to-many ilişki desteklendi ve duplicate mastery yasaklandı.
- Runtime prerequisite ana olarak Skill → Skill edge şeklinde tanımlandı.
- Technical English'in aynı skill modelinde yer alacağı fakat gerçek bağımlılık yoksa teknik rotayı global hard-lock etmeyeceği kilitlendi.
- Learning Objective için gözlemlenebilir/ölçülebilir yazım standardı oluşturuldu.
- LearningTask ve AssessmentItem'ın hedeflediği Skill/Learning Objective'e bağlanması zorunlu tasarım ilkesi yapıldı.
- Stable canonical ID yaklaşımı tanımlandı; kesin DB şeması 8C'ye bırakıldı.

**Üretilen / güncellenen dosyalar**
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/STEP_STATUS.md`
- `docs/EXECUTION_INDEX.md`
- `docs/DECISIONS.md` — D-021
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

**Sonraki kesin adım**
- `2B — Topic durumları`.

---

### 2026-08-24 — Ayrıntılı öğrenme davranışı soru-cevapları kalıcılaştırıldı

**Tamamlananlar**
- Uygulamanın yalnız test eden değil, uygulama içinde öğretip uygulatıp ölçen bir ürün olması netleştirildi.
- Bir Topic'in gerekli temel Learning Objective'leri coverage açısından atlamaması; ileri detayların doğru sonraki Topic/Skill'e bırakılması kararlaştırıldı.
- Yanlış cevabın ceza değil evidence/remediation sinyali olduğu ve birebir aynı sorunun hemen tekrar edilerek ezber ödüllendirilmemesi kilitlendi.
- AssessmentItem'ların prerequisite-aware olması; henüz öğretilmemiş kavram gerektiren soruların kullanıcıyı başarısız sayamaması kararlaştırıldı.
- Basit/orta/zor zorluk davranışı ve zorluğun bilinen kavramların daha karmaşık kullanımıyla artması ilkesi kaydedildi.
- Kritik prerequisite'in süre dolduğu için terk edilmemesi; yalnız bağımlı dalın beklemesi ve bağımsız dalların devam etmesi kalıcılaştırıldı.
- Başarısız test sonrası remediation'ın mevcut günlük kapasite içine yerleştirilmesi; günün kontrolsüz uzatılmaması ve planın dinamik replan edilmesi kaydedildi.
- Gecikmeli retention'ın aylar sonra da yapılabilmesi; tek retention hatasının tüm mastery'yi sıfırlamaması ve hedefli doğrulama/onarma akışı kilitlendi.
- Bilgi havuzunun doğrulanmış çekirdeğe, soru havuzunun doğrulanmış çekirdek + question family/variants + kontrollü AI üretimine dayanması kararlaştırıldı.
- Kullanıcıya özel misconception/hata geçmişinin remediation ve soru seçimini besleyebilmesi kaydedildi.
- AI API'nin destek katmanı olduğu; çekirdek mastery/prerequisite/planner'ın LLM'nin keyfi kontrolüne verilmemesi tekrar kilitlendi.
- Belirli model adı, mastery yüzdesi, retention günleri ve benzeri sayısal detayların ileriki ilgili adımlarda kesinleştirilmesi kararlaştırıldı.

**Üretilen / güncellenen dosyalar**
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/DECISIONS.md` — D-022
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

**Durum etkisi**
- Aktif adım değişmedi: `2B — Topic durumları`.

---

### 2026-08-24 — 2B Topic state machine tamamlandı

**Tamamlananlar**
- V1 için canonical Topic state'ler `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required` olarak kesinleştirildi.
- Topic state'in bağımsız mastery gerçekliği değil; prerequisite uygunluğu, coverage, canonical Skill mastery, retention ve remediation girdilerinden türetilen planner/UX state'i olduğu kilitlendi.
- `locked` state'in esas olarak henüz başlanmamış Topic'in hard prerequisite giriş kapısı olduğu belirlendi.
- Başlanmış veya mastered Topic'in prerequisite Skill sonradan zayıfladığı için geriye dönük `locked` yapılmaması kararlaştırıldı.
- `available → learning → mastered` ana ilerleme yolu tanımlandı.
- Güvenilir diagnostic/skip kanıtında `available → mastered` istisnası desteklendi; tek kolay quiz ile skip/mastery yasaklandı.
- `mastered → weakening → mastered` retention recovery yolu tanımlandı.
- `learning/weakening/mastered → remediation_required → learning/mastered` hedefli onarım yolları tanımlandı.
- `mastered` için required coverage veya validated diagnostic waiver + required/critical Skill mastery gate birlikte zorunlu hale getirildi.
- Remediation'ın geçmiş coverage'ı sıfırlamaması ve bütün curriculum'u durdurmaması kararlaştırıldı.
- Topic state'in ileri Topic kilidinin canonical kaynağı olmadığı; gerçek prerequisite yetkisinin Skill mastery'de kalacağı tekrar kilitlendi.
- Topic state ile coverage'ın ayrı veri kavramları olması belirlendi.
- State transition'ların deterministik ve reason/history ile açıklanabilir olması zorunlu tutuldu.
- Curriculum update ile yeni required objective/skill gelirse mastered Topic'in açıklanabilir biçimde yeniden değerlendirilebilmesi tanımlandı.
- 2B içine mastery/remediation/retention için keyfi sayısal eşik gömülmedi; 2C–2F'ye bırakıldı.

**Alınan kararlar**
- Topic state, Skill mastery'nin üstünde kullanıcı/planner için derived orchestration katmanıdır.
- `weakening` öğrenmenin silinmesi değil retention riskidir.
- `remediation_required` ceza değil hedefli onarım ihtiyacıdır.
- Bir Topic'in state'i tek başına bağımlı Topic'leri kilitlemez; Skill-level prerequisite gate karar verir.

**Üretilen / güncellenen dosyalar**
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/STEP_STATUS.md`
- `docs/EXECUTION_INDEX.md`
- `docs/DECISIONS.md` — D-023
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

**Açık kalan noktalar**
- Hangi evidence türünün neyi ne kadar güçlü kanıtladığı henüz tanımlanmadı.
- AI/hint etkisi, sayısal mastery formula ve retention algoritması açık.

**Sonraki kesin adım**
- **`2C — Mastery sinyalleri`**.
