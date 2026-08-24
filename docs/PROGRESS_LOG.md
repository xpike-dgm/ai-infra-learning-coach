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
- Yapının katı bir ağaç olmadığı; `Domain/Module/Topic` ile `Skill/Learning Objective` katmanlarının farklı sorumlulukları olduğu tanımlandı.
- Curriculum organizasyon katmanı `Domain → Module → Topic` olarak ayrıldı.
- Gerçek öğrenme/ölçüm katmanı `Skill → Learning Objective` olarak ayrıldı.
- Canonical mastery'nin ana planner/prerequisite seviyesi `Skill` olarak belirlendi.
- Evidence'ın en atomik olarak `Learning Objective` seviyesine bağlanabilmesi kararlaştırıldı.
- Topic/Module/Domain mastery'nin ayrı bağımsız puanlar değil, Skill verilerinden derived görünüm olması kararlaştırıldı.
- Aynı Skill'in birden fazla Topic içinde `teach / practice / assess / reinforce` rolüyle kullanılabileceği ve duplicate mastery yaratılmayacağı kesinleştirildi.
- Topic ↔ Skill ilişkisinin many-to-many olabilmesi kararlaştırıldı.
- Runtime prerequisite ana olarak `Skill → Skill` edge şeklinde tanımlandı; Topic prerequisite authoring kolaylığı olabilir fakat gerçek kilit Skill mastery üzerinden çalışacak.
- Module/Domain seviyesinde kaba hard-lock varsayılan yaklaşım olmaktan çıkarıldı.
- Cross-domain Skill dependency desteklendi.
- Technical English'in aynı skill modelinde yer alacağı fakat gerçek bağımlılık yoksa teknik rotayı global hard-lock etmeyeceği kilitlendi.
- Learning Objective için gözlemlenebilir/ölçülebilir yazım standardı oluşturuldu; yalnız `oku`, `izle`, `tamamla` objective sayılmayacak.
- LearningTask ve AssessmentItem'ın yalnız Topic'e değil hedeflediği Skill/Learning Objective'e bağlanması zorunlu tasarım ilkesi yapıldı.
- C pointers ve Technical English compiler-error örnekleriyle model doğrulandı.
- Stable canonical ID yaklaşımı tanımlandı; kesin DB şeması 8C'ye bırakıldı.

**Alınan kararlar**
- Topic completion hiçbir zaman doğrudan mastery değildir.
- Mastery ve prerequisite için ana gerçeklik Skill katmanıdır.
- Topic/Module/Domain progress, raporlama/UX için derived olacaktır.
- Duplicate skill yaratmak yerine farklı topic'ler aynı canonical skill'e bağlanacaktır.
- 2A'da mastery threshold, evidence weight, state machine veya spaced repetition değeri uydurulmadı; bunlar 2B–2F'ye bırakıldı.

**Üretilen / güncellenen dosyalar**
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/STEP_STATUS.md`
- `docs/EXECUTION_INDEX.md`
- `docs/DECISIONS.md` — D-021
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

**Açık kalan noktalar**
- Topic state machine henüz tasarlanmadı.
- Mastery evidence, AI-help impact, mastery formula ve forgetting model sonraki 2B–2F adımlarında kesinleştirilecek.

**Sonraki kesin adım**
- **`2B — Topic durumları`**.

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
- Aktif adım değişmedi: **`2B — Topic durumları`**.
- 2B ve sonraki planner/assessment/retention tasarımları `LEARNING_BEHAVIOR_RULES.md` ile çelişemez.
