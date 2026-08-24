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
| **AŞAMA 2 — Öğrenme ve Mastery Modeli** | ✅ | `2A–2F` tamamlandı. GRE-v0 + RVR-v0 canonical. D-044 ile granularity clarification eklendi; aşama yeniden açılmadı. |
| **AŞAMA 3 — Adaptif Günlük Planlama Motoru** | ✅ | `3A–3H` tamamlandı. 3H: 16/16 scenario + 20/20 invariant PASS. |
| **4A — Günlük mikro değerlendirme** | ✅ | DMA-v0 / D-040 tamamlandı. |
| **4B — Haftalık sınav** | 🟡 Aktif | Weekly multi-Skill assessment composition ve sonuçların programı nasıl değiştireceği tasarlanacak. |
| **4C–5D** | ⬜ Bekliyor | 4B sonrası canonical sırada. |
| **AŞAMA 6 — Granular Capability Map** | ⬜ Bekliyor | Full rotayı Module→Topic→Skill→Objective seviyesinde bölerek weakness localization ve targeted remediation'ı mümkün kılacak. |
| **AŞAMA 7–20** | ⬜ Bekliyor | D-044 sonrası yeniden indekslenmiş future stages. |

## Proje çapı kapsam / rota kararları

### D-041 — Professional-readiness kapsamı
**Durum:** KABUL EDİLDİ / CANONICAL
- Full curriculum 4+ yıl veya daha uzun sürebilir.
- Final target = verified professional capability.
- V1 full curriculum'u beklemez.

### D-042 — Python foundation
**Durum:** KABUL EDİLDİ / CANONICAL
- Python common programming foundation'a eklendi.
- C/C++ yerine geçmez; automation, testing, benchmark, ML/PyTorch ve infra tooling için tamamlayıcı ana dildir.

### D-043 — Specialization tracks / eski AŞAMA 20
**Durum:** GERİ ÇEKİLDİ / YANLIŞ YORUM
- Kullanıcının talebi uzmanlık dallarına ayırmak değildi.
- Bu nedenle eski standalone specialization stage canonical plan'dan kaldırıldı.

### D-044 — Granular Capability Map / yeni AŞAMA 6
**Durum:** KABUL EDİLDİ / CANONICAL
- AŞAMA 5 graph/schema backbone olarak kalır.
- Yeni AŞAMA 6, ana rotadaki her büyük alanı ayrıntılı `Module → Topic → Skill → Learning Objective` yapısına böler.
- Hedef, `Python zayıf` gibi kaba tanı yerine `Python → Control Flow → Loops → while termination` gibi hedefli weakness localization yapabilmektir.
- Technical English, Python, C, Linux/Git/Shell, DS&A, C++, architecture, OS/memory, concurrency, networking, distributed/storage, cloud/observability, performance, GPU, CUDA, Triton, ML/Transformer, inference, serving, KV/batching/scheduling/quantization, multi-GPU/NCCL/RDMA, AI infrastructure, open source/projects/capstone kapsam içindedir.
- AŞAMA 6 coverage/prerequisite Research QA içerir.
- Eski henüz başlanmamış 6–19 aşamaları birer sıra kaydırıldı; toplam aşama sayısı 20 olarak kaldı.

Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## Son tamamlanan numaralı adım — 4A
Ana çıktı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`, D-040.

## Aktif adım — 4B Haftalık sınav

**4B henüz yürütülmedi.** D-041/D-042/D-044 plan sync, 4B execution değildir.

4B başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh zorunludur.

4B'de kesinleştirilecek:
- weekly assessment amacı ve DMA-v0'dan farkı,
- multi-Skill / Objective coverage blueprint,
- required/critical Skill temsili,
- evidence family/modality/context diversity,
- weakness + recent progress + prerequisite risk dengesi,
- capacity ve sınav bölünebilirliği,
- assistance / pause / incomplete davranışı,
- weekly result'ın mastery/remediation/planner akışına etkisi,
- false-positive/false-negative korumaları,
- future AŞAMA 6 granular Skill IDs ile uyum,
- 4C monthly assessment'a ortak contract.
