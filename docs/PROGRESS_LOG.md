# Project Progress Log

Bu dosya projenin oturumlar arası kalıcı ilerleme günlüğüdür. `MASTER_PLAN.md` ana planı gösterir; bu dosya ise ne zaman ne yapıldığını ve neden yapıldığını kronolojik olarak kaydeder.

## Kayıt Formatı

Her önemli çalışma oturumu sonunda:

- tamamlananlar,
- alınan kararlar,
- üretilen/güncellenen dosyalar,
- açık kalan noktalar,
- sonraki kesin adım

kaydedilir.

---

### 2026-08-24 — Proje hafızası ve yürütme sistemi kuruldu

**Tamamlananlar**
- Projenin amacı, kariyer rotası ve uygulamanın temel felsefesi kalıcı dokümana alındı.
- “1095 gün gösterilmeyecek” kararı kaydedildi.
- “Zaman geçirmek ilerleme değildir; yalnızca kanıtlanmış öğrenme ilerlemedir” ilkesi ana ürün prensibi olarak kaydedildi.
- Knowledge graph, mastery, haftalık/aylık assessment, remediation, spaced repetition ve İngilizce paralel hat ana bileşenleri tanımlandı.
- Kişisel kullanım nedeniyle auth, ödeme, sosyal özellik ve çok kullanıcılı SaaS karmaşıklığı kapsam dışı bırakıldı.

**Alınan kararlar**
- Kodlamadan önce ürün, öğrenme motoru, planner, assessment, curriculum, English track, UX ve teknik mimari netleştirilecek.
- Tamamlanan işler GitHub üzerinde kalıcı notlarla tutulacak.

---

### 2026-08-24 — Master plan 19 aşamalı yürütme planına dönüştürüldü

**Tamamlananlar**
- Proje 19 ana aşamaya ayrıldı.
- Her aşama amaç, alt adım, çıktı ve tamamlanma kapısıyla tanımlandı.
- Release APK'nin temel ürünün hazır olduğu nokta olduğu netleştirildi.

**Alınan kararlar**
- Bir aşama yalnız acceptance kriterleri sağlandığında tamamlanmış sayılacak.

---

### 2026-08-24 — Sohbet aktarımı ve kalıcı handoff sistemi güçlendirildi

**Tamamlananlar**
- `START_HERE.md`, `PROJECT_MASTER_CONTEXT.md` ve `HANDOFF_STATE.md` oluşturuldu.

**Alınan kararlar**
- Sohbet geçmişi projenin tek bilgi kaynağı olmayacak.
- GitHub kalıcı proje hafızası olarak kullanılacak.

---

### 2026-08-24 — Sabit 1A/1B yürütme numaralandırması eklendi

**Tamamlananlar**
- 19 ana aşama Aşama 1–19 olarak standardize edildi.
- Alt adımlara `1A`, `1B`, `2A`, `3C`, `11F` gibi sabit kodlar verildi.
- `docs/EXECUTION_INDEX.md` ve `docs/STEP_STATUS.md` oluşturuldu.

**Alınan kararlar**
- Sohbetlerde ve AI görevlerinde sabit adım kodları kullanılacak.

---

### 2026-08-24 — 1A Ana ürün amacı tamamlandı

**Tamamlananlar**
- Ürünün tek cümlelik resmi amacı yazıldı.
- Kullanıcının günlük temel değeri tanımlandı.
- Klasik kurs/todo uygulamasından farkı netleştirildi.
- Kanıtlanmış öğrenme ilkesi ürün gereksinimine dönüştürüldü.
- Kariyer rotası, English ve AI Tutor ürün amacıyla bağlandı.

**Üretilen / güncellenen dosyalar**
- `docs/PRODUCT_REQUIREMENTS.md`
- `docs/STEP_STATUS.md`

**Sonraki kesin adım**
- `1B — V1 kapsamı`.

---

### 2026-08-24 — 1B V1 kapsamı tamamlandı

**Tamamlananlar**
- V1'in ana ürün vaadini eksiltmeden minimum ama eksiksiz kapsamı kilitlendi.
- V1'in Android odaklı, kişisel ve local-first günlük kullanım release'i olması kararlaştırıldı.
- Daily planner, task runner, mastery, prerequisite, daily assessment, weekly/monthly exams, retention, remediation, AI Tutor, parallel English, progress, notifications ve backup/restore V1 kapsamına alındı.
- İlk release curriculum kapsamı ilk 8–12 haftalık production-quality içerikle sınırlandı.
- V1 dışında bırakılan başlıca alanlar netleştirildi: tam 3 yıllık curriculum, social/commerce, cloud multi-device sync, iOS/web/desktop, gelişmiş career-market engine, tam voice tutor, gömülü tam IDE/compiler ve aşırı gamification.
- AI'nın çekirdek planner/mastery/prerequisite kurallarını keyfi biçimde kontrol etmemesi kararlaştırıldı.

**Alınan kararlar**
- V1 yalnız prototip değil, gerçek günlük kullanım için release adayıdır.
- Telefon uygulaması tam IDE olmaya çalışmayacak; coding görevleri gerektiğinde PC üzerinde uygulanabilir.
- İlk release motoru genişlemeye hazır olacak ancak tüm ileri curriculum release ön koşulu olmayacak.

**Üretilen / güncellenen dosyalar**
- `docs/V1_SCOPE.md`
- `docs/STEP_STATUS.md`
- `docs/DECISIONS.md`
- `docs/HANDOFF_STATE.md`

**Açık kalan noktalar**
- V1 başarı kriterleri henüz ölçülebilir testlere çevrilmemişti.

**Sonraki kesin adım**
- `1C — Başarı kriterleri`.

---

### 2026-08-24 — 1C V1 başarı kriterleri tamamlandı

**Tamamlananlar**
- V1 için P0/P1/P2 öncelik seviyeleri tanımlandı.
- PASS / PASS WITH NOTES / FAIL / BLOCKED test sonuç modeli tanımlandı.
- Yeni kullanıcı, tek prerequisite'te zayıf kullanıcı, retention kaybı, 7 günlük ara, AI-assisted coding, hızlı öğrenen ve English/technical asimetrisi için standart test profilleri oluşturuldu.
- Daily planner, prerequisite, mastery, assessment, retention, remediation, replan, English, AI Tutor sınırları, curriculum graph, local persistence, backup/restore, migration, UI, release ve pilot için toplam **49 acceptance kriteri** yazıldı.
- Hard prerequisite bypass, progress data loss, task completion/tek kolay quiz ile yanlış mastery ve AI'nın çekirdek kuralları atlaması P0 release blocker olarak tanımlandı.
- Haftalık ve aylık assessment'ın yalnız puan göstermesi değil, sonraki planı gerçekten değiştirmesi zorunlu acceptance davranışı yapıldı.
- 7+ gün ara sonrası eski görevlerin kullanıcıya borç olarak yığılması yasaklandı; replan zorunlu hale getirildi.
- AI provider kapalıyken deterministic planner/mastery/prerequisite ve local progress'in çalışmaya devam etmesi P0 kriteri yapıldı.
- Fresh install, update install, backup/restore, migration ve bağımsız QA final release kapısına eklendi.
- En az 14 günlük gerçek pilotta hard-rule ihlali sıfır hedefi ve planların en az %90'ında manuel yapısal düzeltme gerekmemesi başlangıç kalite hedefi olarak tanımlandı.

**Alınan kararlar**
- Tüm P0 acceptance kriterleri PASS olmadan V1 release edilmeyecek.
- Kodlama AI'ın kendi “testler geçti” raporu tek başına kritik kabul için yeterli olmayacak; bağımsız Test/QA AI doğrulaması aranacak.
- Mastery threshold, evidence weight, AI-help penalty, spaced repetition interval, planner oranları ve English payı gibi sayısal parametreler 1C'de rastgele sabitlenmeyecek. İlgili Aşama 2/3/4/6/17 çalışmalarında araştırma, simülasyon ve pilot ile belirlenecek.

**Üretilen / güncellenen dosyalar**
- `docs/V1_SUCCESS_CRITERIA.md`
- `docs/STEP_STATUS.md`
- `docs/EXECUTION_INDEX.md`
- `docs/DECISIONS.md`
- `docs/HANDOFF_STATE.md`
- `docs/PROGRESS_LOG.md`

**Açık kalan noktalar**
- Aşama 1'in son adımı olan consolidated non-goals henüz kilitlenmedi.

**Sonraki kesin adım**
- **`1D — Non-goals`**.
