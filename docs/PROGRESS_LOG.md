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
- Ürün çerçevesini kilitleme aşaması tamamlanacak.

---

### 2026-08-24 — Master plan 19 aşamalı ayrıntılı yürütme planına dönüştürüldü

**Tamamlananlar**
- Önceki üst seviye aşama listesi detaylandırıldı.
- Proje toplam 19 ana aşamaya ayrıldı.
- Her aşama; amaç, alt adımlar, üretilecek çıktılar, tamamlanma kapısı ve aşama sonundaki ürün durumu ile yeniden yazıldı.
- M0–M6 milestone sistemi eklendi.
- Release APK’nin temel ürünün hazır olduğu nokta, sonrasının uzun vadeli curriculum/kariyer genişletmesi olduğu netleştirildi.
- Her adım tamamlandığında `[x]` işaretinin yanında tarihli tamamlanma açıklaması tutulması zorunlu hale getirildi.

**Alınan kararlar**
- Bir aşama yalnız ilgili acceptance kriterleri test edildiğinde tamamlanmış sayılacak.
- Ana plan feature listesi değil, projenin yürütme ve kalite kontrol belgesi olacak.

**Üretilen / güncellenen dosyalar**
- `docs/MASTER_PLAN.md`
- `docs/PROGRESS_LOG.md`

**Açık kalan noktalar**
- İlk ürün aşaması henüz tamamlanmadı.
- V1 kapsamı ve success criteria birlikte kesinleştirilecek.

**Sonraki kesin adım**
- Ana ürün amacı kesinleştirilecek.

---

### 2026-08-24 — Sohbet aktarımı ve kalıcı handoff sistemi güçlendirildi

**Tamamlananlar**
- Yeni sohbet/agent için `docs/START_HERE.md` oluşturuldu.
- Ayrıntılı ürün bağlamı için `docs/PROJECT_MASTER_CONTEXT.md` oluşturuldu.
- Güncel devralma durumu için `docs/HANDOFF_STATE.md` oluşturuldu.

**Alınan kararlar**
- Sohbet geçmişi projenin tek bilgi kaynağı olmayacak.
- Yeni sohbet önce `START_HERE.md` üzerinden repo hafızasını okuyacak.

**Üretilen / güncellenen dosyalar**
- `docs/START_HERE.md`
- `docs/PROJECT_MASTER_CONTEXT.md`
- `docs/HANDOFF_STATE.md`
- `README.md`

**Açık kalan noktalar**
- Ürün geliştirme açısından henüz ilk adım tamamlanmadı.

**Sonraki kesin adım**
- Ana ürün amacı.

---

### 2026-08-24 — Sabit 1A/1B yürütme numaralandırması eklendi

**Tamamlananlar**
- 19 ana aşama kullanıcıyla konuşurken Aşama 1–19 olarak standardize edildi.
- Her aşamanın ana alt adımlarına kalıcı kodlar verildi: `1A`, `1B`, `1C`, `2A`, `3C`, `11F` vb.
- Tüm eski ayrıntılı master plan başlıklarının yeni kodlarla eşleştirildiği `docs/EXECUTION_INDEX.md` oluşturuldu.
- `START_HERE.md`, `HANDOFF_STATE.md` ve `DECISIONS.md` yeni numaralandırma protokolüne göre güncellendi.

**Alınan kararlar**
- Bundan sonra konuşma ve görev devrinde mümkün olduğunca sabit adım kodu kullanılacak.
- Bir adım tamamlandığında `EXECUTION_INDEX.md` ve ayrıntılı `MASTER_PLAN.md` birlikte güncellenecek.
- Bir kodun anlamı sonradan mümkün olduğunca değiştirilmeyecek; böylece yeni sohbetler ve farklı AI agent’lar aynı referansı kullanabilecek.

**Üretilen / güncellenen dosyalar**
- `docs/EXECUTION_INDEX.md`
- `docs/DECISIONS.md`
- `docs/START_HERE.md`
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

**Açık kalan noktalar**
- `1A` henüz tamamlanmadı.
- V1 kapsamı, success criteria, mastery ve planner ayrıntıları hâlâ sıradaki aşamalarda kesinleştirilecek.

**Sonraki kesin adım**
- **`1A — Ana ürün amacı`**.
