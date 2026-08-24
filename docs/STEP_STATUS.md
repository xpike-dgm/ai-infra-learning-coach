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
| **3C — Öncelik puanı** | ✅ | `PBR-v0`: P0–P4 semantic bands, lexicographic rank vector, starvation guard, capacity-aware selection. `docs/PRIORITY_POLICY_SPEC.md`, D-035. |
| **3D — Prerequisite davranışı** | 🟡 Aktif | Hard/soft prerequisite eligibility, dependent wait / independent continue ve priority ile eligibility entegrasyonu tasarlanacak. |
| **3E ve sonrası** | ⬜ Bekliyor | 3D kapanışından sonra. |

## Son tamamlanan adım — 3C

Ana çıktı:
- `docs/PRIORITY_POLICY_SPEC.md`
- D-035

### 3C final özeti
- Priority açık LearningNeed seviyesinde başlar.
- Eligibility priority'den önce gelir.
- P0 integrity blocker; P1 repair/verify; P2 maintain/continue; P3 planned progress; P4 reinforce/optimize.
- `review_due` forgetting/negative evidence değildir.
- Critical etiketi tek başına P0 yapmaz; gerçek blocking gerekir.
- Aynı band içi sıralama additive score değil deterministic rank vector.
- Starvation guard eligible soft need'in süresiz ertelenmesini önler.
- English/parallel track fixed yüzde değil due + starvation/track-balance ile korunur.
- Duration fit semantic priority'den sonra gelir; short-task bias yok.
- Same-need duplicate alternatives bastırılır.
- Capacity dolunca kalan LearningNeed açık kalır; next-day task debt yok.
- PriorityDecisionTrace explainability için zorunlu ara çıktı.

## Aktif adım — 3D Prerequisite davranışı

3D başlamadan `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacaktır.

3D'de kesinleştirilecek:
- hard vs soft prerequisite edge semantics,
- task eligibility,
- critical unresolved verification/remediation nedeniyle dependent branch wait,
- `review_due` tek başına hard lock olmaması,
- bağımsız branch'lerin devam etmesi,
- prerequisite contamination guard,
- prerequisite state değişince replan,
- 3C priority ile 3D eligibility'nin kesin yürütme sırası.
