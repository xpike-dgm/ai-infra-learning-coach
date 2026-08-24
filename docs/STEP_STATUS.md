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
| **AŞAMA 3 — Adaptif Günlük Planlama Motoru** | ✅ | `3A–3H` tamamlandı. 3H: 16/16 scenario + 20/20 invariant PASS. |
| **4A — Günlük mikro değerlendirme** | ✅ | `DMA-v0`: state-driven, capacity-aware, no daily quiz quota; evidence-safe assessment. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`, D-040. |
| **4B — Haftalık sınav** | 🟡 Aktif | Weekly multi-Skill assessment composition ve sonuçların programı nasıl değiştireceği tasarlanacak. |
| **4C ve sonrası** | ⬜ Bekliyor | 4B kapanışından sonra. |

## Son tamamlanan adım — 4A

Ana çıktı:
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- D-040

### 4A final özeti — DMA-v0
- Daily micro assessment zorunlu günlük quiz/kota değildir.
- `practice`, `assess`, `retain`, `diagnose` purpose'ları ayrıdır.
- Assessment mevcut LearningNeed + Objective evidence gap'lerinden üretilir; ayrı backlog/debt yoktur.
- Fixed soru sayısı, dakika veya günlük yüzde yoktur; 3A hard capacity + PBR priority kullanılır.
- Measurement hedefi coverage/prerequisite açısından adil olmalıdır.
- Mastery/verification için varsayılan H0 independent attempt gerekir.
- H1–H4 yardım öğrenmeye izin verir ama positive independent mastery evidence iddiasını düşürür; yardım istemek negative H0 evidence değildir.
- Submit sonrası feedback önceki attempt'i geriye dönük kirletmez.
- Invalid/ambiguous/prerequisite-contaminated item positive veya negative mastery evidence üretemez.
- Provisional evaluator critical mastery/remediation kararını tek başına belirleyemez.
- Tek doğru item automatic mastery değildir; tek clean post-mastery failure instant unmastery değildir.
- Coding/debugging/transfer Objective'lerinin evidence standardı kısa süre uğruna recognition/MCQ'ya düşürülmez.
- Assessment sonucu `Attempt/Artifact → EvidenceEvent → GRE/RVR → weakness/verification/remediation → PRG/Topic → replan` zinciriyle çalışır.
- New remediation günü otomatik uzatmaz.
- Technical assessment'ta bilinmeyen English grammar gizli prerequisite olamaz.
- 4B–4E için minimum item/result contract ve `assessment.*` reason-code namespace'i tanımlandı.

## Aktif adım — 4B Haftalık sınav

4B başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh zorunludur.

4B'de kesinleştirilecek:
- weekly assessment amacı ve daily micro assessment'tan farkı,
- multi-Skill / Objective coverage kompozisyonu,
- required/critical Skill temsili,
- evidence family/modality diversity,
- current weakness + recent progress + prerequisite risk dengesi,
- capacity ve sınav bölünebilirliği,
- assistance / pause / incomplete davranışı,
- weekly result'ın mastery/remediation/planner/curriculum akışına etkisi,
- false-positive/false-negative korumaları,
- 4C monthly assessment'a ortak contract.