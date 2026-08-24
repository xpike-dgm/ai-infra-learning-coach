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

Ana sınırlar: zaman/streak/task completion mastery yerine geçmeyecek; ürün sabit takvimli kurs, tamamen LLM kontrollü curriculum, genel amaçlı eğitim platformu, sosyal/ticari SaaS veya tam mobil IDE olmayacak; missed-day task debt, sahte bilimsel kesinlik ve kariyer garantisi kullanılmayacak.

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

## D-022 — Öğretme, soru seçimi, yanlış cevap, retention ve replan davranışları kalıcı olarak kilitlendi

**Durum:** Kabul edildi — 2026-08-24

Kullanıcıyla yapılan ayrıntılı ürün soru-cevaplarından çıkan aşağıdaki davranışlar bağlayıcı kabul edilmiştir:

- uygulama yalnız test etmez; ana konuları uygulama içinde öğretir, uygulatır ve ölçer,
- gerekli temel Learning Objective'ler coverage açısından atlanmaz; ileri detaylar doğru sonraki Topic/Skill'e bırakılır,
- yanlış cevap ceza değil evidence/remediation sinyalidir,
- yanlış yapılan sorunun birebir aynısı hemen tekrar edilerek ezber ödüllendirilmez; aynı Skill farklı varyasyon/bağlamla yeniden ölçülür,
- kullanıcı henüz öğretilmemiş prerequisite isteyen bir sorudan başarısız sayılmaz,
- zorluk gizli yeni kavram eklemekle değil, öğrenilmiş kavramların daha karmaşık kullanım/transferiyle artırılır,
- kritik prerequisite süre dolduğu için terk edilmez; öğretim yöntemi/remediation değişir ve yalnız bağımlı dal bekler,
- başarısız test nedeniyle günlük çalışma süresi kontrolsüz büyütülmez; remediation mevcut kapasite içine yerleştirilir ve daha düşük öncelikli görevler replan edilir,
- mastered Skill'ler haftalar/aylar sonra retention ile tekrar ölçülebilir; tek retention hatası mastery'yi sıfırlamaz, doğrulama ve hedefli onarım uygulanır,
- bilgi bankası doğrulanmış çekirdeğe; soru bankası doğrulanmış çekirdek + question family/variant + kontrollü AI üretimine dayanır,
- kullanıcıya özel misconception/hata geçmişi soru ve remediation seçiminde kullanılabilir,
- çekirdek mastery/prerequisite/planner kararları LLM'nin keyfi kontrolünde değildir,
- AI provider/model ve sayısal mastery/retention/planner parametreleri henüz kalıcı olarak kilitlenmemiştir; ilgili ileriki adımlarda kararlaştırılacaktır.

Ayrıntılı davranış spesifikasyonu: `docs/LEARNING_BEHAVIOR_RULES.md`.

## D-023 — Topic state machine Skill verilerinden türetilen açıklanabilir bir durum modeli olacak

**Durum:** Kabul edildi — 2026-08-24

V1 Topic state'leri:

- `locked`
- `available`
- `learning`
- `mastered`
- `weakening`
- `remediation_required`

Bağlayıcı kararlar:

- Topic state mastery'nin kendisi değildir; prerequisite uygunluğu, coverage, canonical Skill mastery, retention ve remediation verilerinden türetilir.
- `locked`, esas olarak henüz başlanmamış Topic'in hard prerequisite giriş kapısıdır.
- Bir Topic başladıktan veya mastered olduktan sonra prerequisite Skill sonradan zayıfladı diye geriye dönük `locked` yapılmaz; Skill-level gating ile `weakening` / `remediation_required` davranışı kullanılır.
- `available`, giriş prerequisite'leri karşılanmış fakat henüz başlanmamış Topic'tir.
- `learning`, çalışma başlamış ancak coverage + mastery çıkış koşulları henüz tamamlanmamış Topic'tir.
- `mastered`, required coverage veya validated diagnostic waiver ile birlikte required/critical Skill mastery gate'leri sağlandığında oluşur; lesson/task completion tek başına yeterli değildir.
- `weakening`, daha önce mastered olmuş Topic'te doğrulanmış retention riskini gösterir ve bütün Topic'in sıfırlandığı anlamına gelmez.
- `remediation_required`, bir veya daha fazla required/critical Skill için hedefli onarım gerektiğini gösterir; coverage'ı sıfırlamaz ve bağımsız curriculum dallarını durdurmaz.
- Topic state tek başına ileri Topic kilidi değildir; canonical prerequisite yetkisi Skill mastery'dedir.
- State transition'lar deterministik ve reason/history ile açıklanabilir olmalıdır.
- Sayısal mastery/remediation/retention eşikleri 2C–2F tamamlanmadan Topic state içine keyfi olarak gömülmeyecektir.

Ayrıntılı state machine: `docs/TOPIC_STATE_MACHINE.md`.

## D-024 — Her numaralı adım öncesi ve sonrası GitHub proje hafızası senkronizasyonu zorunludur

**Durum:** Kabul edildi — 2026-08-24

GitHub bu projenin durable source of truth kaynağıdır. Bu nedenle her `1A / 2C / 3A / ...` adımı için aşağıdaki çalışma döngüsü zorunludur:

`PRE-STEP GitHub refresh → adımı yürüt → gerekirse Research/Coding/QA → POST-STEP GitHub sync → sonraki adımı aktif yap`

Bağlayıcı kurallar:

- Aynı sohbet içinde bir sonraki numaralı adıma geçilse bile PRE-STEP refresh atlanmaz.
- PRE-STEP sırasında minimum `HANDOFF_STATE.md`, `EXECUTION_INDEX.md`, `STEP_STATUS.md`, `DECISIONS.md` ve ilgili güncel spec/davranış dosyaları okunur.
- Amaç kullanıcının daha önce verdiği cevapları tekrar sordurmamak, stale sohbet bağlamıyla karar vermemek ve repo/sohbet drift'ini engellemektir.
- Adım sonunda ana çıktı ve etkilenen canonical hafıza dosyaları güncellenir.
- `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE` ve `PROGRESS_LOG` yeni durumu yansıttığı kontrol edilmeden numaralı adım tamamlanmış sayılmaz.
- Yeni kalıcı karar varsa `DECISIONS.md` güncellenir; büyük ürün amacı değişirse ilgili üst seviye context dosyaları da senkronize edilir.
- Önceki sohbet hiç bilinmese bile yalnız GitHub hafızasını okuyarak projenin doğru noktadan devam edebilmesi bir adım kapanış kriteridir.

Ayrıntılı protokol: `docs/PROJECT_MEMORY_PROTOCOL.md`.

## D-025 — Mastery çok kaynaklı ve Objective'e uygun evidence ile kanıtlanacak

**Durum:** Kabul edildi — 2026-08-24

2C ile mastery evidence modeli aşağıdaki şekilde kilitlendi:

- Evidence atomik olarak Learning Objective'e, oradan canonical Skill'e bağlanır.
- Evidence rolleri `direct/primary`, `corroborating` ve `contextual` olarak ayrılır.
- Concept recognition, concept recall, code reading/output prediction, coding/production, debugging/diagnosis, explanation/justification, transfer/novel application, retention/delayed retrieval ve integrated project ayrı evidence türleridir.
- Hiçbir evidence türü bütün Skill'ler için evrensel olarak en güçlü değildir; Learning Objective kendi uygun evidence profile'ına sahip olmalıdır.
- Coding mastery için kullanıcı gerçek kod artifact'ı üretmelidir; doğru kodu seçeneklerden seçmek coding evidence değildir.
- Transfer evidence yalnız kullanıcı tarafından daha önce öğrenilmiş prerequisite'leri kullanıyorsa target Skill için geçerlidir.
- Retention, immediate performance'dan ayrı değerlendirilir; gecikmeli veya ileri Topic içindeki doğal yeniden kullanım evidence olabilir.
- Project completion içindeki tüm Skill'leri otomatik mastered yapmaz; evidence objective bazında ayrıştırılır.
- Süre, lesson/task completion, streak ve self-confidence tek başına mastery evidence değildir.
- Aynı soru veya çok yakın variant'ların tekrarı bağımsız evidence gibi mastery'yi şişiremez.
- Hatalı/ambiguous item, bilinmeyen prerequisite, evaluator/system problemi veya answer leakage nedeniyle contamination oluşursa evidence `invalid` kabul edilebilir ve kullanıcı cezalandırılmaz.
- Yardım/AI bağlamı evidence ile birlikte kaydedilir; exact assistance etkisi 2D'de belirlenir.
- Evidence weight, threshold, minimum çeşitlilik ve confidence formülü 2E'ye bırakılmıştır.

Ayrıntılı spesifikasyon: `docs/MASTERY_SIGNALS_SPEC.md`.