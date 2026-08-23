# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

Bu dosya yeni bir ChatGPT sohbetine veya başka bir agent'a geçerken **mevcut çalışma durumunu** hızlıca devretmek için tutulur. Büyük ürün bağlamı için `PROJECT_MASTER_CONTEXT.md`, ayrıntılı aşamalar için `MASTER_PLAN.md`, kalıcı kararlar için `DECISIONS.md` okunmalıdır.

**Son güncelleme:** 2026-08-24

---

# 1. Projenin Şu Anki Durumu

Proje henüz kodlama aşamasında değildir.

Şu anda yapılan iş, uygulamanın ürün/öğrenme mantığını kodlamadan önce kesinleştirmek ve ileride sohbet bağlamı kaybolsa bile aynı kararlara geri dönebilecek sağlam bir proje hafızası oluşturmaktır.

Repo:

`xpike-dgm/ai-infra-learning-coach`

Ana hedef ürün:

> Kullanıcıyı yaklaşık birkaç yıllık AI Infrastructure / Systems kariyer rotasında günlük olarak çalıştıran, her gün ne yapacağını belirleyen, gerçek öğrenmeyi ölçen ve sonuçlara göre programı kendisi değiştiren kişisel mobil öğrenme koçu.

---

# 2. Şimdiye Kadar Kesinleşen Büyük Kararlar

## 2.1 Kariyer yönü

Ana rota:

**C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Doğrudan CUDA ile başlanmayacak.

Klasik Machine Learning ana uzmanlık değil; AI modellerinin çalıştırılması, optimizasyonu ve altyapı tarafı ana hedeftir.

## 2.2 İngilizce

Başlangıç A0/sıfır.

İngilizce teknik eğitime başlamadan önce bitirilmeyecek.

İngilizce ve teknik öğrenme paralel gidecek.

Hedef zaman içinde A0→A1→A2→B1→B2 teknik İngilizce gelişimi.

## 2.3 İlerleme mantığı

Kritik ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Ders izlemek, görev kartını tamamlamak veya takvimde gün geçirmek tek başına mastery vermez.

## 2.4 1095 gün gösterimi

Kullanıcıya `Gün X / 1095` gösterilmeyecek.

3 yıllık süre yalnızca yaklaşık curriculum/planlama ufkudur.

## 2.5 Knowledge graph

Müfredat sabit gün listesi olmayacak.

Konular prerequisite ilişkili knowledge graph olarak tutulacak.

Bir konu öğrenilmezse ona bağlı konu bekler; bağımsız dallar devam eder.

## 2.6 Adaptif planlama

Uygulama kullanıcının mastery, retention, sınav sonucu, zayıf alan, günlük süre ve benzeri verilere göre ertesi günün görevlerini otomatik seçecek.

Kaçırılan günler ceza değil yeniden planlama tetikleyicisi olacak.

## 2.7 Sınavlar

- günlük mikro quiz/değerlendirme
- haftalık sınav
- aylık kapsamlı yeterlilik sınavı

Sınavlar yalnız not göstermeyecek; sonraki programı değiştirecek.

## 2.8 AI Tutor

AI yardımı yasak olmayacak.

Ancak AI ile yapılan bir coding görevi mastery için yeterli olmayacak. Kullanıcıya açıklama, transfer, debugging ve kod anlama soruları sorularak gerçekten anlayıp anlamadığı doğrulanacak.

## 2.9 Kişisel kullanım

Şimdilik:

- auth yok
- abonelik yok
- ödeme yok
- sosyal sistem yok
- admin paneli yok
- multi-tenant SaaS yok

Ana odak kişisel öğrenme deneyimi.

## 2.10 UI

Modern, profesyonel, sade mobil UI.

Ana ekranın temel sorusu:

> **Bugün ne yapmalıyım?**

Ana başarı göstergesi mastery/skill durumu olacak; streak veya gün sayısı değil.

## 2.11 Multi-agent geliştirme düzeni

Kullanıcının elinde ayrı amaçlar için kullanılabilecek AI araçları bulunuyor ve proje bunları uzman rollere ayıracak:

- **Araştırma AI:** güncel dış bilgi, teknoloji karşılaştırması, öğrenme bilimi, curriculum ve teknik araştırmalar.
- **Kodlama AI:** onaylanmış spesifikasyonların implementasyonu, refactor ve bug fix.
- **Test/QA AI:** kodlama AI'dan bağımsız acceptance criteria, edge case ve regression testi.
- **Ana yönetici/koordinatör:** sıradaki işi seçer, araştırmayı karara dönüştürür, görev/spec hazırlar, QA sonucuna göre işi kabul veya geri gönderir ve GitHub hafızasını günceller.

Kritik özelliklerde varsayılan akış:

**Yönetici → gerekirse Araştırma → Spec → Kodlama → Bağımsız QA → PASS ise kabul / FAIL ise kodlamaya geri dönüş → GitHub kayıtları**

Her küçük iş araştırma AI'a gönderilmek zorunda değildir. Ayrıntılı protokol `docs/AI_AGENT_WORKFLOW.md` içindedir.

---

# 3. GitHub'da Oluşturulmuş Dokümanlar

## Handoff ve ana hafıza

- `docs/START_HERE.md`
- `docs/PROJECT_MASTER_CONTEXT.md`
- `docs/HANDOFF_STATE.md`
- `PROJECT_CONTEXT.md`

## Ürün ve öğrenme belgeleri

- `docs/PRODUCT_VISION.md`
- `docs/LEARNING_ENGINE.md`
- `docs/CURRICULUM.md`
- `docs/ENGLISH_TRACK.md`
- `docs/RESEARCH_NOTES.md`

## Yönetim belgeleri

- `docs/DECISIONS.md`
- `docs/MASTER_PLAN.md`
- `docs/AI_AGENT_WORKFLOW.md`
- `docs/PROGRESS_LOG.md`
- `docs/TODO.md`

---

# 4. Master Plan Durumu

`docs/MASTER_PLAN.md` oluşturuldu ve proje kapsamlı aşamalara/alt adımlara bölündü.

Plan şu ana alanları kapsıyor:

1. Proje/ürün çerçevesi
2. Mastery ve öğrenme kuralları
3. Adaptif planner
4. Assessment/sınav sistemi
5. Curriculum knowledge graph
6. İngilizce paralel hat
7. UI/UX
8. Teknik mimari
9. Mobil proje/MVP
10. Adaptif sistem implementasyonu
11. Retention ve sınavların implementasyonu
12. AI Tutor
13. İlk gerçek curriculum paketi
14. İlerleme/analitik
15. UI polish
16. Gerçek kullanım pilotu
17. Release APK
18. Uzun vadeli curriculum/kariyer genişlemesi

Her alt adım tamamlandığında:

- `[x]` olarak işaretlenecek
- hemen altında tarihli tamamlanma notu olacak
- karar değişikliği varsa `DECISIONS.md` güncellenecek
- çalışma özeti `PROGRESS_LOG.md` içine yazılacak

---

# 5. Henüz Kesinleşmemiş / Açık Konular

Aşağıdakiler henüz tamamlanmadı:

- V1 ürün kapsamının kesin maddeleri
- V1 non-goals'ın son hali
- V1 başarı kriterleri
- mastery formülü
- teori/coding/debugging/retention ağırlıkları
- mastery threshold değerleri
- spaced repetition algoritması
- adaptive planner decision table
- günlük süre/kapasite modeli
- sınav composition kuralları
- ilk 8–12 haftalık ayrıntılı knowledge graph
- mobil teknoloji seçimi: Flutter vs Kotlin/Compose vs React Native
- local database seçimi
- AI provider/integration mimarisi
- final wireframe/UI tasarımı

Bunları çözmeden doğrudan kodlamaya geçilmemesi kararlaştırılmıştır.

---

# 6. Şu Anda Bulunulan Kesin Aşama

**MASTER PLAN — AŞAMA 0: Proje Çerçevesini Kilitle**

Henüz bu aşamanın alt maddeleri tamamlandı olarak işaretlenmedi.

Önce kesinleştirilmesi gerekenler:

1. Uygulamanın tek cümlelik ana amacı
2. Kullanıcının uygulamadan aldığı temel günlük değer
3. V1'de kesin olacak özellikler
4. V1'de olmayacak özellikler
5. V1 başarı kriterleri

---

# 7. Yeni Sohbetin Yapması Gereken İlk İş

Yeni sohbet önce `START_HERE.md` ve oradaki okuma sırasını takip etmelidir.

Sonrasında doğrudan:

> **Aşama 0.1 — Ürün amacı**

üzerinden devam edilmelidir.

Daha önce alınmış büyük kararlar tekrar tartışmaya açılmamalıdır; kullanıcı açıkça değiştirmek isterse değişiklik `DECISIONS.md` içine gerekçesiyle yazılmalıdır.

Araştırma/kodlama/test görevleri `AI_AGENT_WORKFLOW.md` protokolüne göre ilgili AI rolüne dağıtılmalıdır.

---

# 8. Sohbet Aktarımında Kullanılacak Önerilen Mesaj

Yeni sohbete şu mesaj gönderilebilir:

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. Önce docs/START_HERE.md dosyasını, sonra onun belirttiği sırayla proje hafızası dosyalarını oku. Önceki sohbetin devamı gibi davran. Daha önce alınmış kararları yeniden sordurma. HANDOFF_STATE.md içindeki mevcut aşamadan devam et. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kalıcı karar ve tamamlanan adımları tekrar GitHub'a kaydet.`

---

# 9. Bu Dosyanın Güncelleme Kuralı

Her önemli milestone veya sohbet değişiminde bu dosya güncellenmelidir.

Özellikle şu olaylarda güncelle:

- bir ana aşama tamamlandığında
- teknoloji seçimi kesinleştiğinde
- MVP geliştirme başladığında
- ilk APK çıktığında
- mastery/planner algoritması değiştiğinde
- curriculum yönü değiştiğinde
- AI agent iş bölümü değiştiğinde
- yeni sohbete geçmeden hemen önce

Amaç, bu dosyanın her zaman “şu anda nerede kaldık?” sorusunun güncel cevabı olmasıdır.
