# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki sabit adım kodlarının güncel durumunu hızlı takip etmek için tutulur.

## Durum anahtarı
- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-25

| Adım | Durum | Açıklama |
|---|---|---|
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ | `1A–1D` tamamlandı. D-041 uzun vadeli hedefi genişletti; V1 ayrımı korunuyor. |
| **AŞAMA 2 — Öğrenme ve Mastery Modeli** | ✅ | `2A–2F` tamamlandı. GRE-v0 + RVR-v0 canonical. |
| **AŞAMA 3 — Adaptif Günlük Planlama Motoru** | ✅ | `3A–3H` tamamlandı. 3H: 16/16 scenario + 20/20 invariant PASS. |
| **4A — Günlük mikro değerlendirme** | ✅ | DMA-v0 / D-040 tamamlandı. |
| **4B — Haftalık sınav** | 🟡 Aktif | Weekly multi-Skill assessment composition ve sonuçların programı nasıl değiştireceği tasarlanacak. |
| **4C–19I** | ⬜ Bekliyor | Canonical sırayla yürütülecek; V1 ve uzun professional core bu aralıkta. |
| **AŞAMA 20 — Uzmanlık Dalları / Track Sistemi** | ⬜ Bekliyor | D-043 ile eklendi; ortak core sonrası specialization mapping, seçim, track curriculum ve capstone gates tasarlanacak. |

## Proje çapı kapsam / rota kararları

### D-041 — Professional-readiness kapsamı
**Durum:** KABUL EDİLDİ / CANONICAL

- Full curriculum **4+ yıl veya daha uzun** sürebilir.
- Final target = AI Infrastructure / ML Systems / GPU Systems için professional-readiness seviyesinde verified engineering capability.
- V1 full curriculum'u beklemez; first 8–12 week production package + learning engine release ayrımı korunur.

### D-042 — Python foundation
**Durum:** KABUL EDİLDİ / CANONICAL

- Python common programming foundation'a eklendi.
- C/C++ yerine geçmez; automation, testing, benchmark, ML/PyTorch ve infra tooling için tamamlayıcı ana dildir.

### D-043 — Specialization tracks / AŞAMA 20
**Durum:** KABUL EDİLDİ / CANONICAL

- Mevcut 1–19 kodları korunarak AŞAMA 20 eklendi.
- Ortak systems/distributed/GPU/inference core sonrası rota uzmanlık dallarına ayrılacak.
- Candidate track'ler: GPU Kernel & Performance; LLM Inference/Serving; Distributed AI Infrastructure/Cluster; High-Speed Networking & Multi-GPU; ML Compilers/Runtime; AI Platform/Reliability & Capacity.
- Track değişimi önceki valid mastery'yi silmez; yalnız eksik prerequisites/evidence açılır.

## Son tamamlanan numaralı adım — 4A

Ana çıktı:
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- D-040

DMA-v0:
- daily assessment quota değildir,
- fixed soru/dakika/yüzde yok,
- H0/assistance/provenance/prerequisite safety,
- invalid/provisional item guard,
- Objective-matched evidence,
- GRE/RVR/remediation/replan integration.

## Aktif adım — 4B Haftalık sınav

**4B henüz yürütülmedi.** D-041/D-042/D-043 scope-route sync, 4B execution değildir.

4B başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh zorunludur.

4B'de kesinleştirilecek:
- weekly assessment amacı ve DMA-v0'dan farkı,
- multi-Skill / Objective coverage blueprint,
- required/critical Skill temsili,
- evidence family/modality/context diversity,
- weakness + recent progress + prerequisite risk dengesi,
- capacity ve sınav bölünebilirliği,
- assistance / pause / incomplete davranışı,
- weekly result'ın mastery/remediation/planner/curriculum akışına etkisi,
- false-positive/false-negative korumaları,
- D-041 professional-readiness ve D-043 future specialization evidence yapısıyla uyum,
- 4C monthly assessment'a ortak contract.
