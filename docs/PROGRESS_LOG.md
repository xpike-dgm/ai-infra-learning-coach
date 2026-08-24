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
- State transition'ların deterministik ve reason/history ile açıklanabilir olması zorunlu tutuldu.
- Curriculum update ile yeni required objective/skill gelirse mastered Topic'in açıklanabilir biçimde yeniden değerlendirilebilmesi tanımlandı.
- 2B içine mastery/remediation/retention için keyfi sayısal eşik gömülmedi; 2C–2F'ye bırakıldı.

**Üretilen / güncellenen dosyalar**
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/STEP_STATUS.md`
- `docs/EXECUTION_INDEX.md`
- `docs/DECISIONS.md` — D-023
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

**Sonraki kesin adım**
- **`2C — Mastery sinyalleri`**.

---

### 2026-08-24 — Her adım için zorunlu GitHub beyin tazeleme protokolü kilitlendi

- `PRE-STEP GitHub refresh → adımı yürüt → gerekirse Research/Coding/QA → POST-STEP GitHub sync → sonraki adımı aktif yap` akışı tüm proje için zorunlu hale getirildi.
- Aynı sohbet içinde yeni numaralı adıma geçilirken bile PRE-STEP refresh tekrarlanacak.
- Minimum PRE-STEP okuması `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS` ve ilgili güncel spec/davranış dosyalarıdır.
- Yeni sohbetin yalnız GitHub hafızasını okuyarak doğru yerden devam edebilmesi adım kapanış kriteri olarak kabul edildi.

**Üretilen / güncellenen dosyalar**
- `docs/PROJECT_MEMORY_PROTOCOL.md`
- `docs/START_HERE.md`
- `docs/AI_AGENT_WORKFLOW.md`
- `docs/DECISIONS.md` — D-024
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

---

### 2026-08-24 — English A0 prerequisite davranışı netleştirildi

**Kullanıcı geri bildirimi**
- `a/an`, `the`, `to`, temel cümle yapısı gibi İngilizce yapılarını öğretmeden teknik İngilizce cümle üretimi beklemek ürünün kendi prerequisite kuralıyla çelişir.

**Kilitlenen davranış**
- A0 kullanıcıdan henüz öğretilmemiş grammar/function-word yapısını kullanması beklenmeyecek.
- English progression `recognition → controlled production → free production → technical use → transfer/retention` yönünde ilerleyecek.
- Teknik task'in amacı C/Linux bilgisini ölçmekse bilinmeyen English grammar gizli prerequisite olmayacak; gerektiğinde Türkçe/bilingual scaffold kullanılacak.
- English grammar/vocabulary kendi prerequisite graph'ına sahip olacak.
- Kesin grammar sırası ve CEFR tasarımı 6A–6E'ye bırakıldı.

**Çıktı**
- `docs/ENGLISH_FOUNDATION_RULES.md`

---

### 2026-08-24 — 2C Mastery sinyalleri tamamlandı

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `LEARNING_ENGINE_SPEC`, `LEARNING_BEHAVIOR_RULES`, `TOPIC_STATE_MACHINE`, `PROJECT_MEMORY_PROTOCOL` ve `AI_AGENT_WORKFLOW` yeniden okundu.
- Aktif adımın 2C olduğu ve 2A–2B kararlarıyla çelişki bulunmadığı doğrulandı.
- Mastery evidence konusu kritik olduğu için retrieval practice / delayed retention / transfer üzerine kısa dış araştırma doğrulaması yapıldı; araştırma sonucu exact weight/threshold belirlemek için kullanılmadı.

**Tamamlananlar**
- Evidence rolleri `direct/primary`, `corroborating`, `contextual` olarak ayrıldı.
- Concept recognition, concept recall, code reading/output prediction, coding/production, debugging/diagnosis, explanation/justification, transfer/novel application, retention/delayed retrieval ve integrated project/task ayrı mastery sinyalleri olarak tanımlandı.
- Her evidence türünün neyi kanıtlayıp neyi tek başına kanıtlayamayacağı yazıldı.
- Coding mastery için gerçek kullanıcı kod artifact'ı üretme zorunluluğu kilitlendi; MCQ ile doğru kod seçmek coding evidence sayılmayacak.
- Transfer evidence'ın yalnız daha önce öğrenilmiş prerequisite'lerle geçerli olduğu ve bilinmeyen prerequisite contamination'ının target Skill'i cezalandırmaması kilitlendi.
- Retention immediate practice başarısından ayrıldı; ileri Topic'te doğal kullanımın retention/reinforcement evidence olabilmesi tanımlandı.
- Project completion'ın projedeki tüm Skill'leri otomatik mastered yapmaması ve evidence'ın objective bazında ayrıştırılması kararlaştırıldı.
- Time, completion, streak ve self-confidence mastery dışı contextual sinyal olarak sınıflandırıldı.
- Aynı soru/familya tekrarlarının bağımsız evidence gibi mastery'yi şişirmesi engellendi.
- Evidence quality boyutları: correctness/rubric, independence/assistance context, novelty, difficulty, evidence-type fit, prerequisite validity, delay/recency ve evaluator/provenance olarak tanımlandı.
- Invalid/contaminated evidence nedenleri tanımlandı.
- Objective-specific evidence profile yaklaşımı tanımlandı.
- Negative evidence ve misconception tagging yönü tanımlandı.
- AI/hint etkisi 2D'ye; weight/threshold/minimum evidence/confidence 2E'ye bırakıldı.

**Üretilen / güncellenen dosyalar**
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/STEP_STATUS.md`
- `docs/EXECUTION_INDEX.md`
- `docs/DECISIONS.md` — D-025
- `docs/HANDOFF_STATE.md`
- `docs/START_HERE.md`
- `docs/PROGRESS_LOG.md`

**Sonraki kesin adım**
- **`2D — AI / ipucu etkisi`**.
- 2D başlamadan önce D-024 uyarınca yeni PRE-STEP GitHub refresh yapılacak.
