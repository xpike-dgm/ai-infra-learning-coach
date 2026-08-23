# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

Bu dosya yeni bir ChatGPT sohbetine veya başka bir agent'a geçerken **mevcut çalışma durumunu** hızlıca devretmek için tutulur. Büyük ürün bağlamı için `PROJECT_MASTER_CONTEXT.md`, sabit çalışma kodları için `EXECUTION_INDEX.md`, ayrıntılı aşamalar için `MASTER_PLAN.md`, kalıcı kararlar için `DECISIONS.md` okunmalıdır.

**Son güncelleme:** 2026-08-24

---

# 1. Projenin Şu Anki Durumu

Proje henüz kodlama aşamasında değildir. Şu anda ürün/öğrenme mantığı kodlamadan önce kesinleştirilmekte ve uzun sohbetler arasında kayıp yaşanmaması için GitHub kalıcı hafıza olarak kullanılmaktadır.

Repo: `xpike-dgm/ai-infra-learning-coach`

Ana hedef ürün:

> Kullanıcıyı birkaç yıllık AI Infrastructure / Systems kariyer rotasında günlük olarak çalıştıran, her gün ne yapacağını belirleyen, gerçek öğrenmeyi ölçen ve sonuçlara göre programı kendisi değiştiren kişisel mobil öğrenme koçu.

---

# 2. Kesinleşen Büyük Kararlar

## 2.1 Kariyer yönü

Ana rota:

**C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Doğrudan CUDA ile başlanmayacak. Klasik Machine Learning ana uzmanlık değil; modellerin çalıştırılması, optimizasyonu ve altyapı tarafı ana hedeftir.

## 2.2 İngilizce

Başlangıç A0/sıfır. İngilizce teknik eğitime başlamadan önce bitirilmeyecek; teknik eğitimle paralel A0→A1→A2→B1→B2 ilerleyecek.

## 2.3 İlerleme mantığı

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Ders izlemek, görev kartı tamamlamak veya gün geçirmek tek başına mastery vermez.

## 2.4 1095 gün

Kullanıcıya `Gün X / 1095` gösterilmeyecek. Yaklaşık 3 yıl yalnız curriculum/planlama ufkudur.

## 2.5 Knowledge graph

Müfredat sabit günlük liste değil, prerequisite ilişkili knowledge graph olacak. Öğrenilmeyen konu bağımlı konuyu bekletir; bağımsız dallar devam eder.

## 2.6 Adaptif planlama

Mastery, retention, sınav sonucu, zayıf alanlar ve günlük süreye göre görevler yeniden seçilecek. Kaçırılan günler ceza değil replan tetikleyicisidir.

## 2.7 Assessment

Günlük mikro değerlendirme, haftalık sınav ve aylık yeterlilik sınavı vardır. Sınavlar yalnız rapor üretmez; gelecekteki programı değiştirir.

## 2.8 AI Tutor

AI yardımı yasak değildir. Ancak AI ile yapılan iş mastery için tek başına yeterli değildir; açıklama, transfer, debugging ve comprehension check ile gerçek anlama doğrulanır.

## 2.9 Kişisel kullanım

Şimdilik auth, abonelik, ödeme, sosyal sistem, admin paneli ve multi-tenant SaaS yoktur.

## 2.10 UI

Modern, profesyonel ve sade mobil UI. Ana ekranın temel sorusu: **“Bugün ne yapmalıyım?”** Ana başarı göstergesi mastery/skill durumudur.

## 2.11 Multi-agent çalışma düzeni

- **Araştırma AI:** dış bilgi, güncel teknoloji, öğrenme bilimi, curriculum ve karşılaştırmalar.
- **Kodlama AI:** onaylanmış spesifikasyonların implementasyonu.
- **Test/QA AI:** bağımsız acceptance, edge case ve regression testi.
- **Ana yönetici/koordinatör:** sıradaki işi seçer, araştırmayı karara dönüştürür, spec hazırlar, QA sonucuna göre kabul/geri dönüş verir ve GitHub hafızasını günceller.

Kritik özelliklerde akış: **Yönetici → gerekirse Araştırma → Spec → Kodlama → Bağımsız QA → PASS kabul / FAIL geri dönüş → GitHub kayıtları**.

## 2.12 Sabit yürütme kodları

Proje bundan sonra **Aşama 1–19** olarak konuşulur. Her ana alt adım sabit bir kimliğe sahiptir: `1A`, `1B`, `2A`, `3C`, `11F` vb.

Kodların tam listesi `docs/EXECUTION_INDEX.md` dosyasındadır. Ayrıntılı checklist ve acceptance kriterleri `docs/MASTER_PLAN.md` içinde kalır.

---

# 3. GitHub'daki Temel Dokümanlar

## İlk okunacaklar

- `docs/START_HERE.md`
- `docs/PROJECT_MASTER_CONTEXT.md`
- `docs/HANDOFF_STATE.md`
- `docs/EXECUTION_INDEX.md`
- `PROJECT_CONTEXT.md`

## Karar ve yürütme

- `docs/DECISIONS.md`
- `docs/MASTER_PLAN.md`
- `docs/AI_AGENT_WORKFLOW.md`
- `docs/PROGRESS_LOG.md`
- `docs/TODO.md`

## Ürün ve öğrenme belgeleri

- `docs/PRODUCT_VISION.md`
- `docs/LEARNING_ENGINE.md`
- `docs/CURRICULUM.md`
- `docs/ENGLISH_TRACK.md`
- `docs/RESEARCH_NOTES.md`

---

# 4. Numaralı Master Plan Özeti

1. Ürün çerçevesi
2. Öğrenme ve mastery modeli
3. Adaptif günlük planner
4. Assessment/sınav sistemi
5. Curriculum knowledge graph
6. İngilizce paralel hat
7. UI/UX
8. Teknik mimari ve veri modeli
9. Mobil proje iskeleti
10. Günlük öğrenme MVP
11. Mastery + adaptive planner implementasyonu
12. Retention/remediation + weekly/monthly exams
13. AI Tutor
14. İlk gerçek curriculum paketi
15. İlerleme/analitik/ayarlar
16. UI polish/accessibility
17. Gerçek kullanım pilotu ve QA
18. Release APK
19. Uzun vadeli curriculum + career readiness

Her ana alt adım `EXECUTION_INDEX.md` içinde `1A`, `1B`, `2A` ... biçiminde tanımlanmıştır.

---

# 5. Açık Konular

Henüz kesinleşmeyen başlıca konular:

- V1 ürün kapsamının son hali
- V1 non-goals
- V1 success criteria
- mastery formülü ve threshold'lar
- assessment ağırlıkları
- spaced repetition algoritması
- adaptive planner decision table
- günlük kapasite modeli
- sınav composition kuralları
- ilk 8–12 haftalık ayrıntılı knowledge graph
- mobil teknoloji seçimi
- local database seçimi
- AI provider/integration mimarisi
- final UI/wireframe

---

# 6. Şu Anda Bulunulan Kesin Adım

**AŞAMA 1 — Ürün Çerçevesini Kilitle**

**Aktif adım: `1A — Ana ürün amacı`**

Aşama 1 sırası:

- `1A` Ana ürün amacı
- `1B` V1 kapsamı
- `1C` Başarı kriterleri
- `1D` Non-goals

Henüz ürün geliştirme adımlarından hiçbiri tamamlandı olarak işaretlenmemiştir.

---

# 7. Yeni Sohbetin Yapacağı İlk İş

Önce `START_HERE.md` okuma sırasını takip et. Sonra doğrudan:

> **`1A — Ana ürün amacı`**

üzerinden devam et.

Daha önce kabul edilmiş kararları yeniden sordurma. Kullanıcı açıkça değiştirmek isterse `DECISIONS.md` içine gerekçesiyle kaydet. Araştırma/kodlama/test görevlerini `AI_AGENT_WORKFLOW.md` protokolüne göre dağıt.

---

# 8. Sohbet Aktarım Mesajı

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. Önce docs/START_HERE.md dosyasını ve oradaki okuma sırasını takip et. Önceki sohbetin devamı gibi davran. HANDOFF_STATE.md ve EXECUTION_INDEX.md içindeki aktif adım kodundan devam et. Daha önce alınmış kararları yeniden sordurma. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kalıcı kararları ve tamamlanan adımları GitHub'a kaydet.`

---

# 9. Güncelleme Kuralı

Bu dosya özellikle şu olaylarda güncellenir:

- aktif `1A / 1B / ...` adımı değiştiğinde
- bir ana aşama tamamlandığında
- teknoloji seçimi kesinleştiğinde
- MVP geliştirme başladığında
- ilk APK çıktığında
- mastery/planner algoritması değiştiğinde
- curriculum yönü değiştiğinde
- AI agent iş bölümü değiştiğinde
- yeni sohbete geçmeden hemen önce

Amaç bu dosyanın her zaman **“şu anda tam olarak hangi adımdayız?”** sorusunu cevaplamasıdır.
