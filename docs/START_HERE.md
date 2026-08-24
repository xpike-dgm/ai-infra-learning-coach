# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?
Tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, curriculum yönünü ve geliştirme planını kalıcı tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Sistem sabit kurs takvimi değil; gerçek Skill state, prerequisite, retention ve evidence'a göre günlük plan üretir.

## 2. Zorunlu GitHub beyin tazeleme protokolü
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

> Hiçbir numaralı adım PRE-STEP GitHub refresh yapılmadan başlatılmaz; hiçbir adım ana çıktı ve canonical state dosyaları + `MASTER_PLAN.md` senkronize edilmeden tamamlanmış sayılmaz.

Minimum PRE-STEP:
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP: ana spec + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN`; yeni karar varsa `DECISIONS`.

## 3. Yeni sohbet/agent okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT_REQUIREMENTS.md`
8. `docs/V1_SCOPE.md`
9. `docs/V1_SUCCESS_CRITERIA.md`
10. `docs/NON_GOALS.md`
11. `docs/LEARNING_ENGINE_SPEC.md`
12. `docs/LEARNING_BEHAVIOR_RULES.md`
13. `docs/TOPIC_STATE_MACHINE.md`
14. `docs/MASTERY_SIGNALS_SPEC.md`
15. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
16. `docs/MASTERY_FORMULA_V0.md`
17. `docs/2E_RESEARCH_VALIDATION.md`
18. `docs/RETENTION_FORGETTING_SPEC.md`
19. `docs/2F_RESEARCH_VALIDATION.md`
20. `docs/ENGLISH_FOUNDATION_RULES.md`
21. `docs/MASTER_PLAN.md`
22. `docs/AI_AGENT_WORKFLOW.md`
23. `docs/PROGRESS_LOG.md`

## 4. Ana kariyer/öğrenme yönü
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

English teknik eğitimle paralel ilerler; doğrudan CUDA ile başlanmaz.

## 5. Tamamlanan öğrenme/mastery omurgası — AŞAMA 2 ✅

### GRE-v0 — Gated Recent Evidence
- Canonical mastery Skill seviyesinde; evidence Learning Objective'e bağlanır.
- Score'a yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence group girer.
- H1–H4 independent mastery score'a girmez.
- Same/near item dependency/testlet grouping.
- Bounded recent window max 5; threshold 0.80 — heuristic/calibration.
- Required/critical hard gates; critical coding H0 user artifact, debugging H0 diagnosis/fix.
- First clean post-mastery failure → verification_due.
- Difficulty numeric multiplier değil; fixed AI trust multiplier yok.

Ayrıntı: `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md` — D-031.

### RVR-v0 — Retention Verification & Risk
- Mastery ve retention ayrı eksen.
- Time-based GRE/mastery score decay yok.
- Retention states: `untracked`, `fresh`, `stable`, `review_due`, `verification_due`, `at_risk`.
- `review_due` forgetting değildir; tek başına Topic weakening/hard lock değildir.
- Delayed review target Skill'e uygun H0 direct verified evidence ister.
- First delayed failure → verification_due; fresh recheck.
- Recheck failure → evidence GRE'ye girer, gates normal yeniden hesaplanır; elle score reset yok.
- Natural reuse yalnız structurally essential + H0 + separately verified + context-diverse ise planned review yerine geçebilir.
- Global project success component Skills'i otomatik refresh etmez; auto cluster refresh yok.
- Critical verification_due unresolved iken dependent yeni work bekleyebilir.
- Long absence backlog dump yok.
- Interval defaults versioned engineering heuristic + 17C calibration.
- Bounded/incremental/local implementation.

Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md` — D-032.

## 6. Güncel çalışma konumu

**AŞAMA 2:** ✅ TAMAMLANDI (`2A–2F`)

**AŞAMA 3 — Adaptif Günlük Planlama Motoru**
- `3A` 🟡 **Günlük kapasite — AKTİF**
- `3B–3H` ⬜ bekliyor.

## 7. 3A'da yapılacaklar
3A başlamadan yeni PRE-STEP GitHub refresh zorunlu.

Kesinleştirilecek:
- günlük gerçek capacity modeli,
- kısa/normal/yoğun gün profilleri,
- minimum viable study block,
- target süre ve tolerans,
- remediation/retention nedeniyle planın kontrolsüz uzamaması,
- user bugün daha az/fazla zamanı olduğunu söylerse replan,
- overflow/defer behavior,
- capacity'nin task selection'a vereceği deterministic contract,
- 3B–3G'nin kullanacağı temel süre/bütçe primitive'leri.

3A'da exact optimum çalışma dakikası bilimsel sabit diye uydurulmayacak. Gerekirse Research AI yalnız kanıt/trade-off için kullanılacak.

## 8. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Aşama 2'nin LEARNING_BEHAVIOR_RULES, TOPIC_STATE_MACHINE, MASTERY_SIGNALS_SPEC, AI_ASSISTANCE_EVIDENCE_SPEC, MASTERY_FORMULA_V0/GRE-v0 ve RETENTION_FORGETTING_SPEC/RVR-v0 bağlayıcı kararlarını koru. Şu an aktif adım 3A — Günlük kapasite.`
