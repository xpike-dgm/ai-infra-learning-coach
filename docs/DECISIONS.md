# Product Decisions Log

Bu dosya kalıcı ürün kararlarını kaydeder. Yeni kararlar eklendikçe eski kararın neden değiştiği de yazılmalıdır.

## D-001 — 3 yıllık gün sayacı gösterilmeyecek

**Durum:** Kabul edildi

`Gün X / 1095` gösterilmeyecek. Geçen zaman öğrenme değildir.

## D-002 — İlerleme mastery tabanlı olacak

**Durum:** Kabul edildi

Bir dersin tamamlanması tek başına ilerleme değildir. İlerleme quiz + uygulama + debugging + açıklama + transfer + gecikmeli tekrar gibi kanıtlarla ölçülür.

## D-003 — Knowledge graph takvimden öncelikli

**Durum:** Kabul edildi

Takvim günlük kapasiteyi yönetir. Konu açılmasını prerequisite ve mastery belirler.

## D-004 — Eksik konu tüm programı dondurmaz

**Durum:** Kabul edildi

Zayıf konu yalnız bağımlı dalları erteler; bağımsız konular devam edebilir.

## D-005 — Haftalık ve aylık sınavlar programı değiştirecek

**Durum:** Kabul edildi

Sınavlar yalnız rapor üretmez; remediation ve sonraki plana doğrudan etki eder.

## D-006 — İngilizce paralel ilerleyecek

**Durum:** Kabul edildi

A0 İngilizce önce ayrı bir kursla bitirilmeyecek; teknik içerikle paralel A0→B2 ilerleyecek.

## D-007 — Ana kariyer rotası systems → GPU → AI infrastructure

**Durum:** Kabul edildi

C → Linux → C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure.

Doğrudan CUDA başlangıcı yapılmayacak.

## D-008 — Uygulama kişisel kullanım için

**Durum:** Kabul edildi

Auth, sosyal özellik, ödeme, abonelik, admin paneli, organizasyon/rol sistemi ve multi-tenant SaaS varsayılan kapsam dışıdır.

## D-009 — Modern ve sade mobil UI

**Durum:** Kabul edildi

Ana ekranın temel amacı “bugün ne yapmalıyım?” sorusunu cevaplamaktır.

## D-010 — Streak ana başarı metriği olmayacak

**Durum:** Kabul edildi

Streak gösterilebilse bile mastery'nin önüne geçmez. Kaçırılan günler ceza değil replan tetikler.

## D-011 — AI kullanımı yasaklanmayacak

**Durum:** Kabul edildi

AI yardımı kullanılabilir; ancak yoğun AI yardımı sonrası comprehension/transfer doğrulaması gerekir.

## D-012 — 3 yıllık içeriğin tamamı V1 ön koşulu değil

**Durum:** Kabul edildi

Önce öğrenme motoru + knowledge graph + ilk 8–12 haftalık yüksek kaliteli içerik oluşturulacak; curriculum zamanla genişletilecek.

## D-013 — Süreler adaptif, konu sırası daha kalıcı

**Durum:** Kabul edildi

Araştırma yol haritalarındaki konu sırası referans olabilir; fakat süreler kişiye göre adaptiftir.

## D-014 — İlk iş hedefi doğrudan CUDA olmak zorunda değil

**Durum:** Kabul edildi

C++ Systems / Systems Software / Linux Infrastructure / Distributed Systems / Performance / uygun SRE-Cloud rolleri köprü olabilir.

## D-015 — Geliştirme aşamalı master plan üzerinden yürütülecek

**Durum:** Kabul edildi — 2026-08-24

Kodlamadan önce ürün çerçevesi, öğrenme motoru, planner, assessment, curriculum, English, UX ve teknik mimari yeterince netleştirilecek. Tamamlanan her adım ilgili spec, completion note ve `PROGRESS_LOG.md` kaydıyla tutulacak.

## D-016 — Araştırma, kodlama ve test için ayrı AI rolleri kullanılacak

**Durum:** Kabul edildi — 2026-08-24

- Araştırma AI: dış bilgi ve karşılaştırmalı araştırma.
- Kodlama AI: onaylanmış spesifikasyonun implementasyonu.
- Test/QA AI: bağımsız acceptance, edge case ve regression doğrulaması.
- Ana yönetici/koordinatör: iş seçimi, spec, karar ve GitHub hafızası.

Kritik işlerde coding AI'ın kendi raporu tek başına kabul değildir. Ayrıntı: `docs/AI_AGENT_WORKFLOW.md`.

## D-017 — Proje adımları sabit `1A / 1B / ...` kodlarıyla takip edilecek

**Durum:** Kabul edildi — 2026-08-24

19 aşama ve sabit alt adım kimlikleri `docs/EXECUTION_INDEX.md` içinde tutulur.

## D-018 — V1 ana adaptif öğrenme döngüsünü eksiksiz çalıştıracak şekilde sınırlandı

**Durum:** Kabul edildi — 2026-08-24

V1 gerçek günlük kullanım için Android release hedefler. Today/daily plan, knowledge graph/prerequisite, adaptive planner, task runner, mastery, assessments, retention, remediation, AI Tutor, parallel English, ilk production curriculum, progress, local persistence, notifications, polished UI ve backup/restore kapsam içidir.

Tam 3 yıllık curriculum, sosyal/ticari özellikler, multi-device sync, iOS/web/desktop, gelişmiş career engine, full voice tutor, full IDE/compiler/sandbox ve ağır gamification V1 release şartı değildir.

Ayrıntı: `docs/V1_SCOPE.md`.

## D-019 — V1 release kabulü ölçülebilir acceptance kriterlerine ve bağımsız QA'ya bağlı olacak

**Durum:** Kabul edildi — 2026-08-24

`docs/V1_SUCCESS_CRITERIA.md` içindeki P0/P1/P2 kriterleri kullanılacak. Tüm P0 kriterleri PASS olmadan release yapılmayacak. Kritik final akışları bağımsız QA doğrulayacak; data loss, prerequisite bypass ve kritik yanlış mastery release blocker'dır.

Mastery threshold, evidence weight, spaced repetition interval ve planner oranları ilgili sonraki aşamalarda araştırma/simülasyon/pilot ile belirlenecektir.

## D-020 — Ürün ve V1 non-goals resmi olarak kilitlendi

**Durum:** Kabul edildi — 2026-08-24

Scope creep'i önlemek için ürün seviyesi non-goals ile yalnız V1'den ertelenen özellikler birbirinden ayrıldı.

Ana sınırlar: zaman/streak/task completion mastery yerine geçmeyecek; ürün sabit takvimli kurs, tamamen LLM kontrollü curriculum, genel amaçlı eğitim platformu, sosyal/ticari SaaS veya tam mobil IDE olmayacak; missed-day görev borcu, sahte bilimsel kesinlik ve kariyer garantisi kullanılmayacak.

Ayrıntı: `docs/NON_GOALS.md`.

## D-021 — Öğrenme birimleri organizasyon ve mastery katmanı olarak ayrılacak

**Durum:** Kabul edildi — 2026-08-24

Öğrenme modeli `Domain → Module → Topic → Skill → Learning Objective` olarak tanımlandı; ancak bu yapı katı bir ağaç olarak yorumlanmayacak.

Bağlayıcı kararlar:

- `Domain → Module → Topic` curriculum organizasyon katmanıdır.
- `Skill → Learning Objective` gerçek öğrenme ve ölçüm katmanıdır.
- Canonical mastery'nin ana planner/prerequisite seviyesi `Skill`'dir.
- Evidence en atomik olarak `Learning Objective` seviyesine bağlanabilir.
- Topic/Module/Domain mastery değerleri bağımsız gerçeklik değil, Skill verilerinden **derived** edilir.
- Aynı Skill birden fazla Topic içinde kullanılabilir; duplicate Skill/mastery oluşturulmaz. Topic ↔ Skill ilişkisi many-to-many destekler.
- Runtime prerequisite'in ana birimi `Skill → Skill` edge'dir. Topic prerequisite authoring kolaylığı olabilir fakat gerçek kilit Skill mastery üzerinden çalışır.
- Module/Domain seviyesinde kaba hard-lock varsayılan değildir.
- Cross-domain Skill bağlantıları mümkündür.
- Technical English ayrı Skill'lerle aynı modele oturur; gerçek bir teknik bağımlılık yoksa teknik ilerlemeyi global hard-lock etmez.
- Learning Objective gözlemlenebilir/ölçülebilir eylem olarak yazılır; yalnız `oku`, `izle`, `tamamla` objective sayılmaz.
- LearningTask ve AssessmentItem hedeflediği Skill/Learning Objective'lere bağlanmalıdır; task completion tek başına mastery değildir.

Ayrıntılı spesifikasyon: `docs/LEARNING_ENGINE_SPEC.md`.
