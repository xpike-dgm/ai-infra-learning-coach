# Project Progress Log

Bu dosya projenin oturumlar arası kalıcı ilerleme günlüğüdür. `MASTER_PLAN.md` ana planı gösterir; bu dosya ise ne zaman ne yapıldığını ve neden yapıldığını kronolojik olarak kaydeder.

## Kayıt Formatı

Her önemli çalışma oturumu sonunda şu format kullanılır:

### YYYY-MM-DD — Oturum başlığı

**Tamamlananlar**
- ...

**Alınan kararlar**
- ...

**Üretilen / güncellenen dosyalar**
- ...

**Açık kalan noktalar**
- ...

**Sonraki kesin adım**
- ...

---

### 2026-08-24 — Proje hafızası ve yürütme sistemi kuruldu

**Tamamlananlar**
- Projenin amacı, kariyer rotası ve uygulamanın temel felsefesi kalıcı dokümana alındı.
- “1095 gün gösterilmeyecek” kararı kaydedildi.
- “Zaman geçirmek ilerleme değildir; yalnızca kanıtlanmış öğrenme ilerlemedir” ilkesi ana ürün prensibi olarak kaydedildi.
- Knowledge graph, mastery, haftalık/aylık assessment, remediation, spaced repetition ve İngilizce paralel hat ana bileşenleri tanımlandı.
- Kişisel kullanım nedeniyle auth, ödeme, sosyal özellik ve çok kullanıcılı SaaS karmaşıklığının kapsam dışı olduğu kaydedildi.
- Geliştirme başlamadan önce izlenecek kapsamlı aşamalı master plan oluşturuldu.

**Alınan kararlar**
- Kodlamadan önce ürün, öğrenme motoru, planner, assessment, curriculum, English track, UX ve teknik mimari sırasıyla netleştirilecek.
- Tamamlanan her adım `MASTER_PLAN.md` içinde `[x]` yapılacak ve hemen altında tamamlanma notu bulunacak.
- Kalıcı karar değişiklikleri `DECISIONS.md` içine ayrıca eklenecek.

**Üretilen / güncellenen dosyalar**
- `PROJECT_CONTEXT.md`
- `docs/DECISIONS.md`
- `docs/PRODUCT_VISION.md`
- `docs/LEARNING_ENGINE.md`
- `docs/CURRICULUM.md`
- `docs/ENGLISH_TRACK.md`
- `docs/RESEARCH_NOTES.md`
- `docs/TODO.md`
- `docs/MASTER_PLAN.md`
- `docs/PROGRESS_LOG.md`

**Açık kalan noktalar**
- V1 ürün kapsamının son hali henüz maddeler halinde kilitlenmedi.
- Mastery formülü ve threshold değerleri henüz tasarlanmadı.
- Adaptive planner karar tablosu henüz tasarlanmadı.
- Mobil teknoloji seçimi henüz yapılmadı.

**Sonraki kesin adım**
- `MASTER_PLAN.md` içindeki **Aşama 0 — Proje Çerçevesini Kilitle** tamamlanacak.

---

### 2026-08-24 — Master plan 19 aşamalı ayrıntılı yürütme planına dönüştürüldü

**Tamamlananlar**
- Önceki üst seviye aşama listesi detaylandırıldı.
- Proje Aşama 0–18 olmak üzere toplam 19 ana aşamaya ayrıldı.
- Her aşama; amaç, numaralı alt adımlar, üretilecek çıktılar, tamamlanma kapısı ve aşama sonundaki ürün durumu ile yeniden yazıldı.
- M0–M6 milestone sistemi eklendi.
- Release APK’nin Aşama 17 sonunda hazır kabul edileceği; Aşama 18’in uzun vadeli curriculum ve kariyer genişletmesi olduğu netleştirildi.
- Her adım tamamlandığında `[x]` işaretinin yanında tarihli tamamlanma açıklaması tutulması zorunlu hale getirildi.
- Her aşamada Analyse → Decide → Document → Implement → Test → Mark → Completion Note → Progress Log protokolü tanımlandı.

**Alınan kararlar**
- Bir aşama yalnızca işler yazılmış olduğu için değil, ilgili acceptance/kabul kriterleri test edildiğinde tamamlanmış sayılacak.
- Ana plan artık feature listesi değil, projenin yürütme ve kalite kontrol belgesidir.
- Uygulama Aşama 9’da ilk kez günlük kullanılabilir hale gelmeye başlayacak, Aşama 10’da gerçek adaptif sistem olacak, Aşama 17’de release hazır kabul edilecek.

**Üretilen / güncellenen dosyalar**
- `docs/MASTER_PLAN.md` kapsamlı biçimde genişletildi.
- `docs/PROGRESS_LOG.md` bu kayıtla güncellendi.

**Açık kalan noktalar**
- Aşama 0 henüz tamamlanmadı.
- V1 kapsamı ve success criteria maddeleri birlikte kesinleştirilecek.
- Mastery/Planner tasarımına Aşama 0 tamamlanmadan geçilmeyecek.

**Sonraki kesin adım**
- **Aşama 0.1 Ana ürün amacı** ve ardından **0.2 V1 kapsamı** birlikte kesinleştirilecek.

---

### 2026-08-24 — Sohbet aktarımı ve kalıcı handoff sistemi güçlendirildi

**Tamamlananlar**
- Uzun sohbetlerin zamanla yavaşlaması veya yeni sohbete geçme ihtiyacı için tek noktadan devralma sistemi oluşturuldu.
- Yeni sohbet/agent için ilk okunacak `docs/START_HERE.md` oluşturuldu.
- Uygulamanın neden var olduğunu, kariyer bağlamını, ürün felsefesini, adaptif öğrenme yaklaşımını, sınav/retention/AI tutor mantığını ve uzun vadeli hedefi ayrıntılı anlatan `docs/PROJECT_MASTER_CONTEXT.md` oluşturuldu.
- Projenin tam olarak hangi aşamada olduğunu, açık kararları ve sıradaki kesin adımı gösteren `docs/HANDOFF_STATE.md` oluşturuldu.
- `README.md` yeni handoff yapısını öne çıkaracak şekilde güncellendi.

**Alınan kararlar**
- Sohbet geçmişi hiçbir zaman projenin tek bilgi kaynağı olmayacak.
- Yeni sohbet veya agent ilk olarak `docs/START_HERE.md` okuyacak ve belirtilen doküman sırasını takip edecek.
- Büyük ürün amacı `PROJECT_MASTER_CONTEXT.md`, güncel durum `HANDOFF_STATE.md`, kalıcı kararlar `DECISIONS.md`, yürütme `MASTER_PLAN.md`, kronolojik ilerleme `PROGRESS_LOG.md` üzerinden korunacak.
- Yeni bir sohbete geçmeden önce `HANDOFF_STATE.md` mümkün olduğunca güncel tutulacak.

**Üretilen / güncellenen dosyalar**
- `docs/START_HERE.md`
- `docs/PROJECT_MASTER_CONTEXT.md`
- `docs/HANDOFF_STATE.md`
- `README.md`
- `docs/PROGRESS_LOG.md`

**Açık kalan noktalar**
- Ürün geliştirme açısından yeni bir aşama tamamlanmadı; halen Aşama 0’dayız.
- V1 kapsamı, success criteria ve mastery/planner ayrıntıları henüz kesinleşmedi.

**Sonraki kesin adım**
- `docs/MASTER_PLAN.md` içindeki **Aşama 0.1 — Ana ürün amacı** ile devam et.
