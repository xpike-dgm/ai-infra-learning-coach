# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki sabit adım kodlarının güncel durumunu hızlı takip etmek için tutulur.

## Durum anahtarı
- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-24

| Adım | Durum | Açıklama |
|---|---|---|
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ | `1A–1D` tamamlandı. |
| **AŞAMA 2 — Öğrenme ve Mastery Modeli** | ✅ | `2A–2F` tamamlandı. GRE-v0 + RVR-v0 canonical. |
| **3A — Günlük kapasite** | ✅ | Hard daily budget, no auto-overrun/backlog debt. D-033. |
| **3B — Görev kategorileri** | ✅ | LearningNeed/TaskCandidate/Evidence ayrımı ve canonical task contract. D-034. |
| **3C — Öncelik puanı** | ✅ | PBR-v0 semantic bands + deterministic rank vector. D-035. |
| **3D — Prerequisite davranışı** | ✅ | PRG-v0 hard/soft Skill prerequisites, readiness gate, branch-local blocking. D-036. |
| **3E — Hızlı öğrenme** | ✅ | VDW-v0 validated diagnostic waiver ve false-skip guard. D-037. |
| **3F — Kaçırılan günler** | ✅ | SRR-v0 current-state re-entry, no absence penalty/debt. D-038. |
| **3G — Açıklanabilir planner** | ✅ | `PDT-v0`: structured decision trace, reason-code taxonomy, user/internal explanation split, deterministic end-to-end pseudocode. `docs/PLANNER_EXPLAINABILITY_SPEC.md`, D-039. |
| **3H — Planner simülasyonu** | 🟡 Aktif | 3A–3G planner invariants sanal kullanıcı/scenario suite ile doğrulanacak. |
| **AŞAMA 4 ve sonrası** | ⬜ Bekliyor | 3H kapanışından sonra. |

## Son tamamlanan adım — 3G

Ana çıktı:
- `docs/PLANNER_EXPLAINABILITY_SPEC.md`
- D-039

### 3G final özeti
- Açıklama planner kararından sonra uydurulmaz; structured trace'ten türetilir.
- Internal audit trace ile user-facing kısa açıklama ayrıdır.
- Private chain-of-thought tutulmaz/gösterilmez; yalnız canonical state refs + policy sonuçları + disposition/reason code saklanır.
- Need-level ve Candidate-level decision trace ayrıdır.
- Selected / blocked / invalid / lower-priority / capacity-deferred / split / smaller-alternative / superseded durumları explicit disposition taşır.
- Reason code namespace'leri need, validation, eligibility, retention, priority, capacity, diagnostic, re-entry, selection ve replan olarak tanımlandı.
- User-facing her factual explanation internal trace'te bulunmak zorundadır.
- PRG eligibility → PBR priority → capacity fit sırası trace'te korunur.
- `review_due` forgetting/failure diye; absence debt/failure/starvation diye açıklanamaz.
- Higher-priority task sığmadığı için lower-priority task seçildiyse gerçek capacity-fit nedeni kaydedilir.
- Replan versioned event chain üretir ve completed evidence'ı korur.
- LLM yalnız trace'i paraphrase edebilir; canonical decision source of truth değildir.
- 3A–3G deterministic planner pseudocode'u ve 3H invariant set'i tanımlandı.
- Trace bounded/ref-based ve D-028 ile uyumludur.

## Aktif adım — 3H Planner simülasyonu

3H başlamadan `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacaktır.

3H'de kesinleştirilecek/doğrulanacak:
- sanal kullanıcı profilleri,
- normal progress, remediation, verification, retention, prerequisite block, partial diagnostic, low/high capacity, long absence ve mid-session replan senaryoları,
- aynı input → aynı plan + eşdeğer trace,
- blocked/invalid candidate seçilmeme,
- hard capacity aşılmama,
- branch-local blocking,
- no task debt / no absence debt,
- review_due semantics,
- reason trace doğruluğu,
- 3A–3G acceptance/invariant suite PASS/FAIL sonuçları,
- Aşama 3'ün kapanış kararı.
