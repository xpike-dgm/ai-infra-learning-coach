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
| **4B — Haftalık sınav** | ✅ | WBA-v0 / D-045. Blueprint-before-items, multi-Skill coverage, no fixed score/time/quota, evidence→replan. |
| **4C — Aylık yeterlilik sınavı** | 🟡 Aktif | Weekly contract üzerinde daha geniş transfer/integration + critical revalidation tasarlanacak. |
| **4D–5D** | ⬜ Bekliyor | 4C sonrası canonical sırada. |
| **AŞAMA 6 — Granular Capability Map** | ⬜ Bekliyor | Full rotayı Module→Topic→Skill→Objective seviyesinde parçalayacak. |
| **AŞAMA 7–20** | ⬜ Bekliyor | D-044 sonrası yeniden indekslenmiş future stages. |

## Proje çapı bağlayıcı kararlar

### D-041 — Professional-readiness kapsamı
- Full curriculum 4+ yıl veya daha uzun sürebilir.
- Final target = verified professional capability; takvim gate değildir.
- V1 full curriculum'u beklemez.

### D-042 — Python foundation
- Python common programming foundation'ın resmi parçasıdır.
- C/C++ yerine geçmez.

### D-043 — Yanlış specialization-stage yorumu
**GERİ ÇEKİLDİ / CANONICAL DEĞİL.**

### D-044 — Granular Capability Map
- AŞAMA 6 eklendi.
- Broad domain weakness yerine Skill/Objective-level localization hedeflenir.
- Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### D-045 — WBA-v0 Weekly Blueprint Assessment
- Weekly exam tek overall pass/fail değildir.
- Önce state-temelli blueprint, sonra validated/prerequisite-valid item/task seçimi yapılır.
- Recent progress, weakness/verification, critical prerequisite, retention, integration/transfer ve gerektiğinde English role'ları vardır; fixed quota değildir.
- Fixed soru sayısı/süre yoktur; daily hard budget aşılmaz, safe split/pause/resume mümkündür.
- Incomplete/missed weekly exam failure/debt/stack değildir.
- H0/assistance/provenance/evaluator/invalid-item guards DMA-v0 ile aynıdır.
- Evidence GRE/RVR/PRG/planner zincirine girer; raw weekly score mastery yazamaz.
- D-044 gereği weakness granular Skill/Objective seviyesine gider.

Ana çıktı: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

## Son tamamlanan numaralı adım — 4B

**Final:** `WBA-v0 — Weekly Blueprint Assessment` / D-045.

4B'de ayrı Research AI kullanılmadı. Bu adım calibrated psychometric soru sayısı/puan/cadence icat etmek yerine mevcut GRE/RVR/PRG/PBR/DMA evidence contract'larını weekly composition'a bağlayan deterministic ürün policy'siydi. Empirik süre/UX/false-positive/false-negative kalibrasyonu pilot aşamasına bırakıldı.

## Aktif adım — 4C Aylık yeterlilik sınavı

**4C henüz yürütülmedi.**

4C başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.

4C'de özellikle:
- monthly scope'un weekly'den farkı,
- daha geniş transfer/integration,
- critical prerequisite/capability revalidation,
- uzun dönem evidence aggregation ama tek final score olmaması,
- capacity / split / incomplete,
- H0/H1–H4 ve invalid/provisional safety,
- D-044 granular Skill/Objective mapping,
- 4D question bank için item/blueprint lifecycle handoff

kesinleştirilecek.
