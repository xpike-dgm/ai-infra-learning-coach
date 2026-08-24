# AI Infra Learning Coach

Kişisel kullanım için tasarlanan adaptif öğrenme ve kariyer koçu mobil uygulaması.

## Yeni Sohbet / Yeni Agent İçin

Bu proje uzun süreli olduğu için sohbet geçmişine güvenilmez. Projeyi devralacak yeni ChatGPT sohbeti veya agent **önce** şu dosyayı okumalıdır:

**`docs/START_HERE.md`**

En önemli kalıcı dosyalar:

- `docs/START_HERE.md` — yeni sohbet için başlangıç ve okuma sırası
- `docs/PROJECT_MASTER_CONTEXT.md` — uzun proje amacı/felsefesi
- `docs/PROFESSIONAL_READINESS_TARGET.md` — 4+ yıllık professional-readiness çıkış hedefi
- `docs/HANDOFF_STATE.md` — güncel konum ve sıradaki kesin adım
- `PROJECT_CONTEXT.md` — kısa proje hafızası
- `docs/DECISIONS.md` — kalıcı kararlar
- `docs/MASTER_PLAN.md` — aşama/adım geliştirme planı
- `docs/PROGRESS_LOG.md` — kronolojik ilerleme

## Amaç

Kullanıcıya uzun bir kurs listesi vermek yerine, her gün ne çalışması gerektiğini state'e göre söyleyen; öğrenme kanıtlanmadıkça ilerleme saymayan; assessment, retention, remediation ve prerequisite sonuçlarına göre sonraki çalışma planını otomatik yeniden düzenleyen bir sistem geliştirmek.

Ana kariyer rotası:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency/Networking → Distributed Systems → GPU Architecture → CUDA/Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

## D-041 — Genişletilmiş Uzun Vadeli Hedef

Full curriculum artık yaklaşık üç yıllık bir rota ile sınırlı değildir. Gerektiğinde **4+ yıl veya daha uzun** sürebilecek kapsamlı, mastery-gated bir rota hedeflenir.

Final hedef yalnız course completion değil:

> **AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability.**

4+ yıl bir countdown değildir. Professional readiness; Skill mastery, retention, debugging, transfer, profiling/benchmarking, integrated systems ve professional capstone evidence ile belirlenir.

Bu hedef iş teklifi, maaş, seniority veya üniversite/HR filtresi garantisi değildir; gerçek ekip/production deneyimi ayrıca oluşur.

## Temel Ürün İlkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca ölçülüp kanıtlanmış öğrenme ilerlemedir.**

Bu nedenle `Gün X / toplam gün` ana progress metriği değildir. Takvim yalnız capacity/horizon bilgisidir; gerçek ilerleme verified capability üzerinden izlenir.

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

## Öğretim Derinliği

Kritik alanlarda hedef progression:

`concept → guided application → independent application → debugging → explanation → transfer → retention → integrated project → performance/production context`

Uzun rota yalnız C/C++/CUDA syntax öğretmeyecek; Git, testing, build/debug/profiling, distributed systems, observability, design docs, open-source workflow ve professional capstones gibi gerçek engineering davranışlarını da kapsayacaktır.

## Ürün Karakteri

- Tek kullanıcı / kişisel kullanım.
- Modern ve sade mobil UI.
- Ana ekranın odağı: **Bugün ne yapmalıyım?**
- Curriculum sabit takvim değil prerequisite graph.
- Weekly/monthly assessment gelecekteki programı değiştirir.
- English teknik eğitimle paralel yürür.
- AI yardımcı öğretmen/evaluator'dır; mastery/planner'ın keyfi sahibi değildir.
- Streak/task completion mastery değildir.

## Güncel Durum

Aşama 1–3 tamamlandı. 4A DMA-v0 tamamlandı.  
**Aktif adım: 4B — Haftalık sınav.**

D-041 kapsam senkronu tamamlandı; 4B başlamadan yeni PRE-STEP GitHub refresh zorunludur.
