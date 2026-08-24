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
| **3F — Kaçırılan günler** | ✅ | `SRR-v0`: current-state re-entry, no absence penalty/debt, bounded capacity recovery, starvation≠absence. `docs/MISSED_DAY_RECOVERY_SPEC.md`, D-038. |
| **3G — Açıklanabilir planner** | 🟡 Aktif | Reason codes, deterministic planner decision trace ve end-to-end pseudocode tasarlanacak. |
| **3H** | ⬜ Bekliyor | 3G kapanışından sonra planner simülasyonu. |

## Son tamamlanan adım — 3F

Ana çıktı:
- `docs/MISSED_DAY_RECOVERY_SPEC.md`
- D-038

### 3F final özeti
- Absence failure, mastery decay veya task debt değildir.
- Geçmiş başlanmamış PlannedTask/TaskCandidate current plana replay edilmez.
- Current state → fresh LearningNeed/candidate generation yapılır.
- Zaman yalnız RVR review_due/temporal urgency gibi sinyalleri değiştirebilir; review_due otomatik verification/at_risk değildir.
- Verification/remediation açık need'leri absence ile silinmez.
- Paused safe checkpoint resume adayı olabilir ama otomatik seçilmez; eligibility/priority yeniden hesaplanır.
- Incomplete high-stakes H0 attempt negative evidence değildir; gerekiyorsa fresh/unseen item gelir.
- Due inventory DailyPlan değildir; yüzlerce due Skill tek güne yığılmaz.
- Integrated recovery task yalnız separately attributable Skills için evidence üretir; sibling/cluster auto-refresh yoktur.
- 1/7/30/60+ gün için ayrı bilimsel threshold yok; aynı state-driven policy uygulanır.
- Absence günleri starvation counter artırmaz; retention overdue age ayrı sinyaldir.
- Geri dönüş de 3A hard capacity içindedir; new learning güvenliyse tamamen dondurulmaz.
- SRR-v0 deterministic/bounded ve D-028 ile uyumludur.

## Aktif adım — 3G Açıklanabilir planner

3G başlamadan `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacaktır.

3G'de kesinleştirilecek:
- planner'ın her seçilen/elenen görev için machine-readable reason codes üretmesi,
- PBR/PRG/capacity/retention/remediation/diagnostic/re-entry kararlarının tek decision trace'te birleşmesi,
- kullanıcıya gösterilecek kısa açıklamalar ile internal ayrıntılı trace ayrımı,
- neden bu görev bugün var / neden başka görev gelmedi / neden branch bekliyor soruları,
- deterministic end-to-end planner pseudocode,
- replan reason chain,
- debug/audit için reconstruct edilebilir karar kaydı,
- 3H simulation'ın doğrulayacağı invariants ve trace beklentileri.
