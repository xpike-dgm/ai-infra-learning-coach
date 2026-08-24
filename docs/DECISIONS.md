# Product Decisions Log

Bu dosya kalıcı ürün kararlarını kaydeder. Yeni kararlar eklendikçe eski kararın neden değiştiği de yazılmalıdır.

## D-001 — 3 yıllık gün sayacı gösterilmeyecek

**Durum:** Kabul edildi

`Gün 47 / 1095` gibi bir gösterim kullanılmayacak.

Gerekçe: Geçen zaman öğrenme değildir ve kullanıcıya sahte ilerleme hissi verir.

## D-002 — İlerleme mastery tabanlı olacak

**Durum:** Kabul edildi

Bir dersin tamamlanması tek başına ilerleme değildir. İlerleme, quiz + uygulama + debugging + açıklama + gecikmeli tekrar gibi kanıtlarla ölçülür.

## D-003 — Knowledge graph takvimden öncelikli

**Durum:** Kabul edildi

Takvim yalnızca günlük kapasiteyi yönetir. Hangi konunun ne zaman açılacağını prerequisite ve mastery belirler.

## D-004 — Eksik konu tüm programı dondurmaz

**Durum:** Kabul edildi

Bir konu zayıfsa ona bağımlı konular ertelenir. Bağımsız konular devam edebilir.

## D-005 — Haftalık ve aylık sınavlar programı değiştirecek

**Durum:** Kabul edildi

Sınavlar yalnızca rapor üretmez; remediation ve sonraki görev planına doğrudan etki eder.

## D-006 — İngilizce paralel ilerleyecek

**Durum:** Kabul edildi

A0 İngilizcenin önce ayrı bir kursla tamamlanması beklenmeyecek. Teknik içerikle birlikte A0→B2 ilerleyecek.

## D-007 — Ana kariyer rotası systems → GPU → AI infrastructure

**Durum:** Kabul edildi

Ana sıra:

C → Linux → C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure.

Doğrudan CUDA başlangıcı yapılmayacak.

## D-008 — Uygulama kişisel kullanım için

**Durum:** Kabul edildi

Şimdilik gereksiz olanlar:
- auth
- sosyal özellik
- ödeme
- abonelik
- admin paneli
- organizasyon/rol sistemi
- çok kullanıcılı SaaS mimarisi

## D-009 — Modern ve sade mobil UI

**Durum:** Kabul edildi

Ana ekranın temel amacı “bugün ne yapmalıyım?” sorusunu cevaplamaktır. Dashboard karmaşası, uzun timeline ve gereksiz gamification kullanılmayacaktır.

## D-010 — Streak ana başarı metriği olmayacak

**Durum:** Kabul edildi

Streak gösterilebilir ama mastery'nin önüne geçmez. Kaçırılan günler ceza değil yeniden planlama tetikler.

## D-011 — AI kullanımı yasaklanmayacak

**Durum:** Kabul edildi

Kullanıcı AI araçlarını kullanabilir. Ancak AI çok fazla yardım ettiyse konu mastery kazanmak için ek açıklama/transfer/doğrulama soruları gerekir.

## D-012 — 3 yıllık içeriğin tamamı V1 ön koşulu değil

**Durum:** Kabul edildi

Önce öğrenme motoru + knowledge graph + ilk 8–12 haftalık yüksek kaliteli içerik oluşturulacak. Müfredat uygulama kodundan ayrı tutulacak ve zamanla genişletilecek.

## D-013 — Süreler adaptif, konu sırası daha kalıcı

**Durum:** Kabul edildi

Araştırmalardaki 12 aylık yoğun planın konu sırası referans alınabilir; ancak bir konunun “1 ayda bitmesi” sabit kabul edilmeyecek.

## D-014 — İlk iş hedefi doğrudan CUDA olmak zorunda değil

**Durum:** Kabul edildi

C++ Systems / Systems Software / Linux Infrastructure / Distributed Systems / Performance / uygun SRE-Cloud rolü, AI Infrastructure'a geçiş için köprü olabilir.

## D-015 — Geliştirme aşamalı master plan üzerinden yürütülecek

**Durum:** Kabul edildi — 2026-08-24

Proje, `docs/MASTER_PLAN.md` içindeki aşama ve adımlara göre yürütülecek. Kodlamadan önce ürün çerçevesi, öğrenme motoru, adaptif planner, assessment sistemi, curriculum/knowledge graph, English track, UX ve teknik mimari yeterince netleştirilecek.

Bir adım tamamlandığında yalnızca checkbox işaretlenmeyecek; ilgili spec, tarihli tamamlanma notu ve `docs/PROGRESS_LOG.md` kaydı tutulacak.

## D-016 — Araştırma, kodlama ve test için ayrı AI rolleri kullanılacak

**Durum:** Kabul edildi — 2026-08-24

- **Araştırma AI:** dış bilgi, güncel teknoloji, öğrenme bilimi, curriculum ve karşılaştırmalı araştırmalar.
- **Kodlama AI:** onaylanmış/spec'i netleştirilmiş işleri implement etme, refactor ve bug fix.
- **Test/QA AI:** kodlama AI'dan bağımsız acceptance, edge case ve regression doğrulaması.
- **Ana yönetici/koordinatör:** işi seçer, araştırmayı karara dönüştürür, spec hazırlar, QA sonucuna göre kabul/geri dönüş verir ve GitHub hafızasını günceller.

Kodlama AI'ın kendi implementasyonunu başarılı ilan etmesi tek başına tamamlanma sayılmayacak. Kritik işler bağımsız QA'dan geçecek.

Ayrıntılı protokol: `docs/AI_AGENT_WORKFLOW.md`.

## D-017 — Proje adımları sabit `1A / 1B / ...` kodlarıyla takip edilecek

**Durum:** Kabul edildi — 2026-08-24

19 ana aşama Aşama 1–19 olarak anılacak. Her ana alt adım aşama numarası + harf biçiminde sabit kimliğe sahip olacak: `1A`, `1B`, `2A`, `3C`, `11F` vb.

Sabit yürütme indeksinin kaynağı: `docs/EXECUTION_INDEX.md`.

## D-018 — V1 kapsamı ana adaptif öğrenme döngüsünü eksiksiz çalıştıracak şekilde sınırlandı

**Durum:** Kabul edildi — 2026-08-24

V1, yalnız ekranları olan bir prototip değil; kişisel günlük kullanım için gerçekten çalışan Android release'i hedefler.

V1'de zorunlu ana yetenekler:

- Today/daily plan,
- knowledge graph + prerequisite,
- adaptive planner/replan,
- task runner,
- mastery engine,
- günlük mikro değerlendirme,
- weekly/monthly assessments,
- retention/spaced repetition,
- remediation,
- AI Tutor v1,
- paralel technical English,
- ilk 8–12 haftalık production curriculum,
- progress/weakness görünümü,
- local-first persistence,
- bildirimler,
- modern UI,
- backup/export/restore.

V1 release şartı olmayanlar:

- 3 yıllık curriculum'un tamamı,
- sosyal/ticari özellikler,
- auth ve multi-tenant SaaS,
- cloud multi-device live sync,
- iOS/web/desktop istemcileri,
- gelişmiş career-market engine,
- tam voice-first tutor,
- uygulama içine gömülü tam C/C++ IDE/compiler/sandbox,
- aşırı gamification.

AI explanation/feedback/evaluation için kullanılabilir; ancak mastery/prerequisite/planner çekirdek kuralları tamamen LLM'nin keyfi kararlarına bırakılmayacaktır.

Ayrıntılı kapsam: `docs/V1_SCOPE.md`.

## D-019 — V1 release kabulü ölçülebilir acceptance kriterlerine ve bağımsız QA'ya bağlı olacak

**Durum:** Kabul edildi — 2026-08-24

V1 yalnızca özelliklerin mevcut olmasıyla hazır sayılmayacaktır. `docs/V1_SUCCESS_CRITERIA.md` içindeki acceptance kriterleri P0/P1/P2 olarak sınıflandırılır.

Release için:

- tüm P0 kriterleri PASS olmalı,
- açık kritik P1 fonksiyon hatası olmamalı,
- hard prerequisite ihlali, progress data loss ve kritik yanlış mastery gibi çekirdek kural hataları kabul edilmemeli,
- final kritik akışlar bağımsız Test/QA AI tarafından doğrulanmalı,
- gerçek Android cihazında fresh/update install testleri geçmeli,
- backup/restore ve migration veri kaybı üretmemeli,
- gerçek kullanım pilotu yapılmalıdır.

Mastery threshold, assessment ağırlığı, spaced repetition interval'i ve planner oranları gibi henüz araştırılmamış sayısal parametreler 1C'de keyfi biçimde sabitlenmeyecek; ilgili sonraki aşamalarda araştırma, simülasyon ve pilot verisiyle kesinleştirilecektir.

Ayrıntılı kriterler: `docs/V1_SUCCESS_CRITERIA.md`.
