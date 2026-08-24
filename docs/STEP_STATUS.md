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
| **3D — Prerequisite davranışı** | ✅ | `PRG-v0`: hard/soft Skill prerequisites, readiness gate, branch-local blocking, review_due no-lock, contamination guard. `docs/PREREQUISITE_POLICY_SPEC.md`, D-036. |
| **3E — Hızlı öğrenme** | 🟡 Aktif | Diagnostic/skip/validated waiver ve false-skip guard tasarlanacak. |
| **3F ve sonrası** | ⬜ Bekliyor | 3E kapanışından sonra. |

## Son tamamlanan adım — 3D

Ana çıktı:
- `docs/PREREQUISITE_POLICY_SPEC.md`
- D-036

### 3D final özeti
- Runtime prerequisite canonical `Skill → Skill`.
- Edge `hard | soft`.
- Readiness: `ready | ready_due | uncertain | not_ready`.
- `review_due` hard lock değildir.
- Hard `not_ready` dependent candidate'ı bloke eder.
- Critical/strict `verification_due` dependent yeni work'u bekletebilir.
- Normal uncertain dependency conditional eligibility olabilir; bütün curriculum durmaz.
- Task-level `required_skill_ids` exact candidate hard requirement'tır.
- Priority prerequisite'i bypass edemez.
- Yalnız affected branch bekler; independent branches devam eder.
- Started Topic prerequisite regression ile `locked` olmaz.
- Prerequisite contamination target negative evidence değildir.
- Missing prerequisite repair/review/verification need olarak planner'a geri döner.
- English gerçek dependency değilse global technical blocker değildir.
- PRG-v0 deterministic/bounded'dır.

## Aktif adım — 3E Hızlı öğrenme

3E başlamadan `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacaktır.

3E'de kesinleştirilecek:
- kullanıcının zaten bildiği Topic/Skill'i nasıl güvenilir diagnostic ile göstereceği,
- `available → mastered` diagnostic yolu,
- coverage waiver / skip semantics,
- tek kolay quiz ile skip yasağı,
- critical Skill için daha güçlü diagnostic evidence,
- partial diagnostic sonucu ve yalnız bilinen Objective'lerin atlanması,
- false-positive skip guard,
- diagnostic assistance/provenance,
- diagnostic sonrası GRE/PRG/planner replan entegrasyonu.
