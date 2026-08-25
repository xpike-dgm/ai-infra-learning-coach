# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki canonical adım kodlarının güncel durumunu hızlı takip etmek için tutulur.

## Durum anahtarı
- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-25

| Adım | Durum | Açıklama |
|---|---|---|
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ | `1A–1D` tamamlandı. |
| **AŞAMA 2 — Öğrenme ve Mastery Modeli** | ✅ | `2A–2F` tamamlandı. GRE-v0 + RVR-v0 canonical. |
| **AŞAMA 3 — Adaptif Günlük Planlama Motoru** | ✅ | `3A–3H` tamamlandı. 16/16 scenario + 20/20 invariant PASS. |
| **4A — Günlük mikro değerlendirme** | ✅ | DMA-v0 / D-040. |
| **4B — Haftalık sınav** | ✅ | WBA-v0 / D-045. |
| **4C — Aylık yeterlilik sınavı** | ✅ | MCA-v0 / D-046. |
| **4D — Soru / assessment resource bank** | ✅ | QAB-v0 / D-047. Versioned trusted resource bank, prerequisite/exposure/family/context/evaluator lifecycle. |
| **4E — AI-generated soru doğrulaması** | 🟡 Aktif | AI-generated candidate validation, promotion/use-ceiling, duplicate/ambiguity/correctness/prerequisite/evaluator checks tasarlanacak. |
| **AŞAMA 5–20** | ⬜ Bekliyor | 4E sonrası canonical sırada. |

## Bağlayıcı assessment kararları

### D-040 — DMA-v0
Daily assessment zorunlu quota değildir; Objective-matched evidence ve canonical state pipeline kullanır.

### D-045 — WBA-v0
Weekly assessment item'dan önce blueprint üretir; multi-Skill coverage granular attribution ile çalışır; fixed score/time/quota yoktur; incomplete/missed exam debt değildir.

### D-046 — MCA-v0
Monthly assessment longitudinal state-based sampling, broader transfer/integration ve ihtiyaç-temelli critical revalidation kullanır; cumulative final/pass-score değildir.

### D-047 — QAB-v0
- Bank yalnız MCQ deposu değil, versioned assessment resource bank'tir.
- Logical resource ID + immutable published version ayrıdır; Attempt exact version'a bağlanır.
- Lifecycle/trust/use ceiling ayrı semantics taşır.
- Resource exact Objective/Skill/prerequisite/evidence/scope/role/evaluator/tool/artifact/duration metadata'sı taşır.
- Variant family, dependency/testlet, context family ve transfer profile ayrıdır; same/near item evidence diversity'yi şişiremez.
- Integrated component evidence ayrı observable/attributable olmalıdır.
- Solution exposure per-user state'tir; content freshness ayrı tutulur.
- Deprecated vs invalidated ayrıdır; invalid version historical evidence audit edilebilir.
- Selector bounded/indexed çalışır; full-bank scan hedeflenmez.
- AI-generated resource 4E validation olmadan trusted/high-stakes use'a otomatik yükselmez.

Ana çıktı: `docs/QUESTION_BANK_SPEC.md`.

## Son tamamlanan numaralı adım — 4D

**Final:** `QAB-v0 — Trusted Assessment Resource Bank` / D-047.

4D'de ayrı Research AI kullanılmadı. Adım psychometric item calibration veya AI-validator accuracy eşiği uydurmadı; mevcut DMA/WBA/MCA/GRE/RVR/PRG contract'larını versioned content-bank modeline bağladı. Empirical item difficulty/exposure calibration AŞAMA 18'e; AI-generated validator promotion policy 4E'ye bırakıldı.

## Aktif adım — 4E AI-generated soru doğrulaması

**4E henüz yürütülmedi.**

4E başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.

4E'de özellikle:
- AI-generated candidate giriş/lifecycle,
- schema completeness,
- technical correctness + answer/rubric correctness,
- ambiguity ve multiple-valid-answer detection,
- target Objective/evidence modality fit,
- prerequisite completeness + forbidden concept leakage,
- duplicate/near-duplicate/variant-family classification,
- dependency/testlet/context/transfer validation,
- evaluator/tool/artifact compatibility,
- technology/source freshness,
- automated vs deterministic vs human/manager review gereksinimleri,
- risk-based `use_ceiling` promotion,
- trusted-template inheritance sınırları,
- revalidation/invalidation lifecycle,
- validator uncertainty ve fail-safe behavior

kesinleştirilecek.
