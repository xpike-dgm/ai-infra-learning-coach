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

Proje, `docs/MASTER_PLAN.md` içindeki aşama ve adımlara göre yürütülecek. Kodlama başlamadan önce ürün çerçevesi, öğrenme motoru, adaptif planner, assessment sistemi, curriculum/knowledge graph, English track, UX ve teknik mimari yeterince netleştirilecek.

Bir adım tamamlandığında yalnızca checkbox işaretlenmeyecek; aynı adımın altına tarihli **tamamlanma notu** yazılacak. Bu not ne yapıldığını, hangi kararın çıktığını, hangi dosyanın üretildiğini ve sonraki adıma etkisini açıklayacak. Oturumların kronolojik özeti ayrıca `docs/PROGRESS_LOG.md` içinde tutulacak.

Gerekçe: Proje uzun süreli olduğu için sohbet bağlamına veya hafızaya güvenmeden, neyin neden yapıldığını GitHub üzerinden yeniden kurabilmek.

## D-016 — Araştırma, kodlama ve test için ayrı AI rolleri kullanılacak

**Durum:** Kabul edildi — 2026-08-24

Proje geliştirmesinde mevcut AI araçları uzman rollere ayrılacak:

- **Araştırma AI:** dış bilgi, güncel teknoloji, öğrenme bilimi, curriculum ve karşılaştırmalı araştırmalar.
- **Kodlama AI:** yalnız onaylanmış/spec'i netleştirilmiş işleri implement etme, refactor ve bug fix.
- **Test/QA AI:** kodlama AI'dan bağımsız acceptance criteria, edge case ve regression doğrulaması.
- **Ana yönetici/koordinatör:** hangi işin yapılacağını belirleme, araştırmayı karara dönüştürme, görev paketleme, test sonucunu değerlendirme ve GitHub proje hafızasını güncelleme.

Kodlama AI'ın kendi implementasyonunu 'çalışıyor' olarak raporlaması tek başına tamamlanma sayılmayacak. Kritik geliştirme işleri bağımsız QA'dan geçecek. FAIL durumunda görev kodlama AI'a geri dönecek; PASS sonrası ilgili `MASTER_PLAN.md` adımı tamamlanabilecek.

Her iş zorunlu olarak üç AI'dan geçmeyecek. Dış araştırma gerektirmeyen küçük ve net işler doğrudan Kodlama → QA akışına gidebilir. Mastery algoritması, teknoloji seçimi, curriculum veya güncel bağımlılık kararları gibi riskli konularda Araştırma → Spec → Kodlama → QA akışı tercih edilecek.

Ayrıntılı protokol: `docs/AI_AGENT_WORKFLOW.md`.
