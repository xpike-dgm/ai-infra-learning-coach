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
| **AŞAMA 2 — Öğrenme ve Mastery Modeli** | ✅ | `2A–2F` tamamlandı. GRE-v0 + RVR-v0 canonical. D-044 granularity clarification ile uyumlu. |
| **AŞAMA 3 — Adaptif Günlük Planlama Motoru** | ✅ | `3A–3H` tamamlandı. 3H: 16/16 scenario + 20/20 invariant PASS. |
| **4A — Günlük mikro değerlendirme** | ✅ | DMA-v0 / D-040. |
| **4B — Haftalık sınav** | ✅ | WBA-v0 / D-045. Blueprint-before-items, multi-Skill evidence, no fixed score/time/quota. |
| **4C — Aylık yeterlilik sınavı** | ✅ | MCA-v0 / D-046. Longitudinal sampling, broader transfer/integration, critical revalidation, no cumulative/pass-score model. |
| **4D — Soru bankası** | 🟡 Aktif | Trusted item/task bank schema, lifecycle, variant/dependency/context metadata ve blueprint matching tasarlanacak. |
| **4E–5D** | ⬜ Bekliyor | 4D sonrası canonical sırada. |
| **AŞAMA 6 — Granular Capability Map** | ⬜ Bekliyor | Full rotayı Module→Topic→Skill→Objective seviyesinde parçalayacak. |
| **AŞAMA 7–20** | ⬜ Bekliyor | D-044 sonrası yeniden indekslenmiş future stages. |

## Bağlayıcı assessment kararları

### D-040 — DMA-v0
Daily assessment zorunlu quota değildir; Objective-matched evidence ve canonical state pipeline kullanır.

### D-045 — WBA-v0
Weekly assessment item'dan önce blueprint üretir; multi-Skill coverage granular attribution ile çalışır; fixed score/time/quota yoktur; incomplete/missed exam debt değildir.

### D-046 — MCA-v0
- Monthly assessment ay sonu notu/domain pass-fail değildir.
- WBA common blueprint/result abstraction'ını kullanır.
- Longitudinal required capability, persistent weakness/verification, critical revalidation, delayed retention, cross-topic transfer, integrated application, gerektiğinde English/professional checkpoint role'ları vardır; quota değildir.
- Bütün geçmiş curriculum'u cumulative olarak sınamaz; state-based bounded sampling yapar.
- Recent/older balance fixed yüzde değildir.
- Critical Skill otomatik monthly retest değildir; gerçek revalidation ihtiyacı gerekir.
- Transfer/integration prerequisite-safe ve component-attributable'dır.
- Professional checkpoint final professional-readiness gate değildir.
- Daily hard capacity korunur; split/pause/resume mümkündür; incomplete/missed monthly exam failure/debt değildir.
- H0/H1–H4, invalid/provisional safety, root contamination ve GRE/RVR hysteresis korunur.
- D-044 gereği weakness Skill/Objective seviyesinde lokalize edilir.
- V1 SC-016 gereği güvenilir persistent/critical gap planner/curriculum priority'yi gerçekten değiştirebilir.

Ana çıktı: `docs/MONTHLY_ASSESSMENT_SPEC.md`.

## Son tamamlanan numaralı adım — 4C

**Final:** `MCA-v0 — Monthly Capability Assessment` / D-046.

4C'de ayrı Research AI kullanılmadı. Scientifically optimal soru sayısı/süre/pass score/role oranı veya universal critical revalidation cadence uydurulmadı; empirical calibration AŞAMA 18'e bırakıldı.

## Aktif adım — 4D Soru bankası

**4D henüz yürütülmedi.**

4D başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.

4D'de özellikle:
- trusted item/task bank production schema,
- stable ID + versioning/lifecycle,
- target Skill/Objective + required prerequisites,
- evidence/activity type,
- daily/weekly/monthly scope + blueprint-role eligibility,
- variant family / dependency group / context-transfer structure,
- integrated component attribution,
- rubric/answer key/evaluator requirements,
- allowed tools + artifact requirements,
- validation/trust/content origin,
- exposure/solution leakage handling,
- difficulty/complexity semantics,
- estimated duration/atomicity,
- language/scaffold/freshness metadata,
- selection/indexing/performance contract,
- 4E AI-generated candidate validation handoff

kesinleştirilecek.