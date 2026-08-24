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
| **3A — Günlük kapasite** | ✅ | Hard daily budget, editable presets, no auto-overrun/backlog debt. `docs/ADAPTIVE_PLANNER_SPEC.md`, D-033. |
| **3B — Görev kategorileri** | ✅ | LearningNeed/TaskCandidate/Evidence ayrımı, purpose/activity/track/evidence eksenleri, multi-Skill attribution ve task contract. `docs/TASK_TAXONOMY_SPEC.md`, D-034. |
| **3C — Öncelik puanı** | 🟡 Aktif | Capacity'ye sığmayan açık ihtiyaçlar arasında deterministic/explainable priority ve selection policy tasarlanacak. |
| **3D ve sonrası** | ⬜ Bekliyor | 3C kapanışından sonra. |

## Son tamamlanan adım — 3B

Ana çıktı:
- `docs/TASK_TAXONOMY_SPEC.md`
- D-034

### 3B final özeti
- `LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent` ayrımı canonical.
- Deferred candidate ertesi gün borç değildir; open LearningNeed çözülmediyse fresh candidate üretilir.
- Purpose: `teach | practice | assess | remediate | retain | diagnose | reinforce`.
- Coding/debugging/project activity; English curriculum track'tir.
- Task completion mastery değildir.
- Multi-Skill task component evidence'ı ayrı attribution ister.
- Provenance/validation, variant/dependency, prerequisite/tools ve 3A duration metadata contract'a dahildir.
- Paused progress gerçek checkpoint ile saklanabilir; priority/eligibility ertesi gün yeniden değerlendirilir.
- 3B priority weight belirlemez; ham sinyalleri 3C'ye verir.

## Aktif adım — 3C Öncelik puanı

3C başlamadan `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacaktır.

3C'de kesinleştirilecek:
- critical prerequisite / verification / remediation / retention / continuing learning / new learning / English gibi ihtiyaçların priority ilişkisi,
- urgency vs importance ayrımı,
- duration/capacity-aware seçim,
- ertelenen ama açık kalan LearningNeed'in starvation yaşamaması,
- aynı gün çok sayıda kritik işte tie-break,
- fixed category percentages olmadan dengeli progress,
- deterministic priority score veya decision hierarchy,
- user-visible reason inputs (final reason-code metinleri 3G'de).
