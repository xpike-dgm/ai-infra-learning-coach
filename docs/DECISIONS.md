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
C → Linux → C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure. Doğrudan CUDA başlangıcı yapılmayacak.

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
V1 gerçek günlük kullanım için Android release hedefler. Today/daily plan, knowledge graph/prerequisite, adaptive planner, task runner, mastery, assessments, retention, remediation, AI Tutor, parallel English, ilk production curriculum, progress, local persistence, notifications, polished UI ve backup/restore kapsam içidir. Tam 3 yıllık curriculum, sosyal/ticari özellikler, multi-device sync, iOS/web/desktop, gelişmiş career engine, full voice tutor, full IDE/compiler/sandbox ve ağır gamification V1 release şartı değildir. Ayrıntı: `docs/V1_SCOPE.md`.

## D-019 — V1 release kabulü ölçülebilir acceptance kriterlerine ve bağımsız QA'ya bağlı olacak
**Durum:** Kabul edildi — 2026-08-24
`docs/V1_SUCCESS_CRITERIA.md` içindeki P0/P1/P2 kriterleri kullanılacak. Tüm P0 kriterleri PASS olmadan release yapılmayacak. Kritik final akışları bağımsız QA doğrulayacak; data loss, prerequisite bypass ve kritik yanlış mastery release blocker'dır. Mastery threshold, evidence weight, spaced repetition interval ve planner oranları ilgili sonraki aşamalarda araştırma/simülasyon/pilot ile belirlenecektir.

## D-020 — Ürün ve V1 non-goals resmi olarak kilitlendi
**Durum:** Kabul edildi — 2026-08-24
Scope creep'i önlemek için ürün seviyesi non-goals ile yalnız V1'den ertelenen özellikler birbirinden ayrıldı. Ana sınırlar: zaman/streak/task completion mastery yerine geçmeyecek; ürün sabit takvimli kurs, tamamen LLM kontrollü curriculum, genel amaçlı eğitim platformu, sosyal/ticari SaaS veya tam mobil IDE olmayacak; missed-day task debt, sahte bilimsel kesinlik ve kariyer garantisi kullanılmayacak. Ayrıntı: `docs/NON_GOALS.md`.

## D-021 — Öğrenme birimleri organizasyon ve mastery katmanı olarak ayrılacak
**Durum:** Kabul edildi — 2026-08-24
Öğrenme modeli `Domain → Module → Topic → Skill → Learning Objective` olarak tanımlandı; ancak katı ağaç değildir. `Domain/Module/Topic` curriculum organizasyon, `Skill/Learning Objective` gerçek learning/measurement katmanıdır. Skill canonical mastery/prerequisite seviyesidir; Topic/Module/Domain mastery derived edilir. Topic ↔ Skill many-to-many desteklenir; runtime prerequisite ana olarak Skill → Skill çalışır. Technical English aynı skill modelinde yaşar ama gerçek dependency yoksa teknik rotayı global hard-lock etmez. Ayrıntı: `docs/LEARNING_ENGINE_SPEC.md`.

## D-022 — Öğretme, soru seçimi, yanlış cevap, retention ve replan davranışları kalıcı olarak kilitlendi
**Durum:** Kabul edildi — 2026-08-24
- uygulama yalnız test etmez; öğretir, uygulatır ve ölçer,
- gerekli temel Objective'ler coverage'da atlanmaz,
- yanlış cevap ceza değil evidence/remediation sinyalidir,
- exact yanlış soru hemen tekrar edilerek ezber ödüllendirilmez,
- öğretilmemiş prerequisite isteyen soru kullanıcıyı başarısız saymaz,
- zorluk bilinen kavramların daha karmaşık kullanımından gelir,
- kritik prerequisite süre dolduğu için terk edilmez,
- remediation günlük kapasite içine replan edilir,
- mastered Skill'ler retention ile tekrar ölçülür; tek hata sıfırlamaz,
- bilgi/soru bankası doğrulanmış çekirdek + kontrollü varyasyon yaklaşımı kullanır,
- çekirdek mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
Ayrıntı: `docs/LEARNING_BEHAVIOR_RULES.md`.

## D-023 — Topic state machine Skill verilerinden türetilen açıklanabilir bir durum modeli olacak
**Durum:** Kabul edildi — 2026-08-24
V1 Topic state'leri `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`. Topic state mastery'nin kendisi değildir; prerequisite uygunluğu, coverage, canonical Skill mastery, retention ve remediation'dan derived edilir. Started/mastered Topic prerequisite regression yüzünden geriye dönük locked yapılmaz. Mastered için required coverage/validated waiver + required/critical Skill mastery gate gerekir. Ayrıntı: `docs/TOPIC_STATE_MACHINE.md`.

## D-024 — Her numaralı adım öncesi ve sonrası GitHub proje hafızası senkronizasyonu zorunludur
**Durum:** Kabul edildi — 2026-08-24
Canonical döngü: `PRE-STEP GitHub refresh → adımı yürüt → gerekirse Research/Coding/QA → POST-STEP GitHub sync → sonraki adımı aktif yap`. Minimum PRE-STEP: `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS` ve ilgili güncel spec. `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` yeni durumu yansıtmadan adım tamamlanmış sayılmaz. Ayrıntı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

## D-025 — Mastery çok kaynaklı ve Objective'e uygun evidence ile kanıtlanacak
**Durum:** Kabul edildi — 2026-08-24
Evidence atomik olarak Learning Objective'e, oradan Skill'e bağlanır. Roller `direct/primary`, `corroborating`, `contextual`; ana türler recognition, recall, code reading, coding, debugging, explanation, transfer, retention, integrated project. Coding mastery gerçek user artifact ister. Transfer yalnız öğrenilmiş prereq ile geçerlidir. Time/completion/streak/self-confidence mastery değildir. Same-family tekrarlar bağımsız evidence şişiremez; invalid/contaminated evidence kullanıcıyı cezalandırmaz. Ayrıntı: `docs/MASTERY_SIGNALS_SPEC.md`.

## D-026 — AI/ipucu yardımı öğrenmeyi destekler fakat bağımsız mastery kanıtıyla eşit sayılmaz
**Durum:** Kabul edildi — 2026-08-24
Assistance `H0 none`, `H1 orientation`, `H2 targeted conceptual hint`, `H3 partial solution/scaffold`, `H4 full solution/answer exposure`. Timing ve artifact origin ayrı tutulur. AI'nın tam kodu/cevabı kullanıcı production mastery'si değildir. H3/H4 solution exposure sonrası fresh/unseen independent recheck gerekir. Compiler/test/docs/autocomplete objective-specific allowed-tools policy'ye göre yorumlanır. Hint istemek tek başına negative mastery değildir. Ayrıntı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`.

## D-027 — MASTER_PLAN canonical yürütme durumuyla senkron tutulacak
**Durum:** Kabul edildi — 2026-08-24
`EXECUTION_INDEX.md` sabit adım kimliklerinin canonical indeksidir; `MASTER_PLAN.md` ayrıntılı checklist'tir. Her POST-STEP'te `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` ve `MASTER_PLAN` karşılığı kontrol edilip güncellenir.

## D-028 — Mobil performans ve akıcılık birinci sınıf ürün gereksinimidir
**Durum:** Kabul edildi — 2026-08-24
Uygulama ağır, takılan veya gereksiz kaynak tüketen yapıya dönüşmeyecek. UI thread üzerinde ağır mastery/planner hesapları, büyük DB taramaları, network/AI veya code execution çalıştırılmayacak. Async network/AI, local-first cache/index/incremental update, lazy rendering ve gereksiz polling'den kaçınma yönü bağlayıcıdır. Release öncesi startup, ekran geçişi, büyük veri, memory/battery ve jank/ANR gerçek cihazda test edilecek. Exact performans bütçeleri 8F/17E'de ölçülecek.

## D-029 — Mastery Formula v0 score + hard gate + verification modeli olacak

**Durum:** PROVISIONAL / Research AI doğrulaması bekliyor — 2026-08-24

Bu karar 2E'nin candidate tasarımıdır; henüz kalıcı bağlayıcı final karar değildir. Candidate model:

- Beta-style weighted evidence accumulator + hard mastery gates.
- Objective score candidate formülü:
  - `alpha = 1 + Σ(w_i × q_i)`
  - `beta = 1 + Σ(w_i × (1-q_i))`
  - `objective_score = alpha/(alpha+beta)`
- `q_i` rubric doğruluğu `[0,1]`; invalid evidence hesaba girmez.
- `w_i = role_weight × assistance_weight × provenance_weight`.
- Candidate role weight: direct `1.00`, corroborating `0.50`, contextual `0`.
- Candidate assistance: H0 `1.00`, H1 `0.85`, H2 `0.65`, H3 `0.35` veya `0`, H4 `0`.
- Candidate AI evaluator high-confidence provenance `0.80`.
- Candidate operational mastery threshold `0.80`.
- Difficulty candidate modelde numeric score multiplier değil, gate/item-eligibility girdisi.
- Same-item/same-family inflation engellenir.
- Required/critical Objective'lerde hard gates; critical production için en az bir H0 user-authored direct artifact adayı.
- Tek clean negative evidence sonrası anında reset yerine `verification_due` candidate davranışı.
- Formula traceable/versioned ve incremental hesaplamaya uygun tasarlanır.

**Düzeltme:** İlk 2E kapanışında ayrı Research AI turu yapılmış gibi davranıldı; gerçekte yapılan araştırma ana yöneticinin kendi web/dış araştırmasıydı. D-016 ve `AI_AGENT_WORKFLOW.md` gereği 2E ayrı Research AI doğrulaması almadan kapanmış sayılmayacaktır.

Ayrıntılı candidate spec: `docs/MASTERY_FORMULA_V0.md`.

## D-030 — 2E ayrı Research AI raporu alınmadan kapatılamaz

**Durum:** Kabul edildi — 2026-08-24

2E yeniden açılmıştır. Ayrı Research AI raporu candidate mastery formülünü akademik/teknik kaynaklarla sorgulayacak; ana yönetici raporu mevcut 2A–2D kararlarıyla karşılaştırıp gerekli revizyonları yapacaktır. Research AI raporu otomatik ürün kararı değildir. Rapor değerlendirilmeden, `MASTERY_FORMULA_V0.md` finalleştirilmeden ve canonical POST-STEP dosyaları senkronize edilmeden 2F'ye geçilmez.