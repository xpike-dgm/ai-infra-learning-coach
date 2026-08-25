# AI Infra Learning Coach

Kişisel kullanım için tasarlanan adaptif öğrenme ve kariyer koçu mobil uygulaması.

## Yeni Sohbet / Yeni Agent İçin

Bu proje uzun süreli olduğu için sohbet geçmişine güvenilmez. Projeyi devralacak yeni ChatGPT sohbeti veya agent **önce** şu dosyayı okumalıdır:

**`docs/START_HERE.md`**

En önemli kalıcı dosyalar:

- `docs/START_HERE.md` — yeni sohbet için başlangıç ve okuma sırası
- `docs/HANDOFF_STATE.md` — ayrıntılı güncel execution handoff
- `docs/STEP_STATUS.md` — kısa güncel execution tablosu
- `PROJECT_CONTEXT.md` — kısa yaşayan proje snapshot'ı
- `docs/PROJECT_MASTER_CONTEXT.md` — uzun ve stabil proje amacı/felsefesi
- `docs/PROFESSIONAL_READINESS_TARGET.md` — 4+ yıllık professional-readiness çıkış hedefi
- `docs/CURRICULUM_DOMAIN_MAP.md` — high-level professional domain backbone / PDM-v0
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — full route'u ölçülebilir alt becerilere ayıracak AŞAMA 6 charter'ı
- `docs/DECISIONS.md` — kalıcı kararlar
- `docs/MASTER_PLAN.md` — aşama/adım geliştirme planı
- `docs/EXECUTION_INDEX.md` — canonical aşama/adım indeksi
- `docs/PROGRESS_LOG.md` — kronolojik ilerleme
- `docs/PROJECT_MEMORY_PROTOCOL.md` — zorunlu PRE/POST GitHub hafıza senkronu ve dosya rol matrisi
- `docs/STAGE_REINDEX_MAP.md` — D-044 öncesi future-stage referanslarının current karşılık haritası

## Amaç

Kullanıcıya uzun bir kurs listesi vermek yerine, her gün ne çalışması gerektiğini state'e göre söyleyen; öğrenme kanıtlanmadıkça ilerleme saymayan; assessment, retention, remediation ve prerequisite sonuçlarına göre sonraki çalışma planını otomatik yeniden düzenleyen bir sistem geliştirmek.

## Güncel Ana Öğrenme Rotası

**Technical English (parallel) → Python → C → Linux + Git + Shell → Data Structures & Algorithms foundations → Modern C++ → Computer Architecture → Operating Systems + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases foundations → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM / SGLang / TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source contributions + real large projects + professional capstones**

Python, C/C++'ın yerine geçmez; programming foundation, automation, testing, benchmark scripting ve ML/infra tooling için resmi tamamlayıcı dildir.

## D-041 — Genişletilmiş Uzun Vadeli Hedef

Full curriculum yaklaşık üç yıllık rota ile sınırlı değildir. Gerektiğinde **4+ yıl veya daha uzun** sürebilecek kapsamlı, mastery-gated bir rota hedeflenir.

Final hedef yalnız course completion değil:

> **AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability.**

4+ yıl countdown değildir. Professional readiness; Skill mastery, retention, debugging, transfer, profiling/benchmarking, integrated systems ve professional capstone evidence ile belirlenir.

Bu hedef iş teklifi, maaş, seniority veya üniversite/HR filtresi garantisi değildir; gerçek ekip/production deneyimi ayrıca oluşur.

## D-044 — Granular Capability Map

Uygulama yalnız `Python zayıf` veya `Linux güçlü` gibi kaba domain sonuçları göstermemelidir.

Canonical learning structure:

`Domain → Module → Topic → Skill → Learning Objective`

Örneğin sistem gerektiğinde:

`Python → Control Flow → Loops → while termination`

seviyesinde zayıflığı tespit edip yalnız o alt capability için remediation üretebilmelidir.

Bu nedenle planlama aşamalarının arasına **AŞAMA 6 — Granular Capability Map** eklendi. Bütün ana rota burada kapsamlı alt kavram/skill/objective haritasına ayrılacaktır.

## D-049 — Professional Domain Backbone

High-level curriculum envelope `docs/CURRICULUM_DOMAIN_MAP.md` içinde PDM-v0 olarak tanımlıdır. 23 ana route family, parallel/common/core/supporting/target/professional-evidence rolleriyle birbirine bağlanır. Bu harita takvim değildir; runtime prerequisite'ler Skill seviyesinde çözülür.

## Temel Ürün İlkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca ölçülüp kanıtlanmış öğrenme ilerlemedir.**

`Gün X / toplam gün` ana progress metriği değildir. Takvim yalnız capacity/horizon bilgisidir; gerçek ilerleme verified capability üzerinden izlenir.

## V1 ile Full Curriculum Ayrımı

**V1 release**, full 4+ year curriculum'un bitmesini beklemez.

V1:
- adaptive learning engine,
- mastery/prerequisite/planner,
- assessment/retention/remediation,
- AI Tutor,
- parallel English,
- local persistence,
- ilk 8–12 haftalık production-quality curriculum

ile gerçek Android release olabilir.

Full professional curriculum aynı engine üzerinde modül modül genişletilir.

Canonical üretim ayrımı:

`AŞAMA 5 graph/schema backbone → AŞAMA 6 granular capability map → AŞAMA 15 first production content → AŞAMA 20 full professional content/capstones`

## Öğretim Derinliği

Kritik alanlarda hedef progression:

`concept → guided application → independent application → debugging → explanation → transfer → retention → integrated project → performance/production context`

Uzun rota yalnız syntax öğretmeyecek; Git, testing, build/debug/profiling, distributed systems, observability, design docs, open-source workflow ve professional capstones gibi gerçek engineering davranışlarını da kapsayacaktır.

## Ürün Karakteri

- Tek kullanıcı / kişisel kullanım.
- Modern ve sade mobil UI.
- Ana ekranın odağı: **Bugün ne yapmalıyım?**
- Curriculum sabit takvim değil prerequisite graph.
- Weekly/monthly assessment gelecekteki programı değiştirir.
- English teknik eğitimle paralel yürür.
- AI yardımcı öğretmen/evaluator'dır; mastery/planner'ın keyfi sahibi değildir.
- Streak/task completion mastery değildir.
- Weakness mümkün olduğunca Skill/Objective seviyesinde lokalize edilir.

## Güncel Execution Durumu

README volatile aktif adımı bilinçli olarak tekrar etmez. Bunun source of truth'u:

- `docs/STEP_STATUS.md`
- `docs/HANDOFF_STATE.md`
- `docs/EXECUTION_INDEX.md`
- `docs/MASTER_PLAN.md`
- `PROJECT_CONTEXT.md`

D-050 gereği bu yaşayan dosyalar her numaralı adım kapanışında senkron kontrolünden geçer. Böylece README'nin eski bir `aktif adım` iddiasıyla drift oluşturması engellenir.