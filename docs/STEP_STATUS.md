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
| **3E — Hızlı öğrenme** | ✅ | `VDW-v0`: diagnostic = normal GRE evidence path, Objective-level validated coverage waiver, partial skip, false-skip guards. `docs/DIAGNOSTIC_WAIVER_SPEC.md`, D-037. |
| **3F — Kaçırılan günler** | 🟡 Aktif | Uzun ara sonrası backlog dump olmadan current-state recovery ve yeniden giriş planı tasarlanacak. |
| **3G ve sonrası** | ⬜ Bekliyor | 3F kapanışından sonra. |

## Son tamamlanan adım — 3E

Ana çıktı:
- `docs/DIAGNOSTIC_WAIVER_SPEC.md`
- D-037

### 3E final özeti
- Diagnostic ayrı/easier mastery modeli değildir; GRE-v0 gate'leri aynen korunur.
- Self-report yalnız diagnostic trigger/scope'tur.
- Single easy quiz / recognition-only whole-topic skip yoktur.
- Skip Objective bazlı `DiagnosticCoverageWaiver` olarak tutulur; waiver mastery/retention değildir.
- Partial diagnostic yalnız kanıtlanan Objective'leri waive eder.
- `available → mastered` yalnız coverage + required/critical GRE gates birlikte sağlanınca mümkündür.
- Critical Skill diagnostic'te H0 production/debugging/transfer/diversity şartları düşürülemez.
- Integrated diagnostic component evidence için ayrı attribution ister.
- Diagnostic fail prior-knowledge yolunda otomatik remediation cezası değildir.
- PRG-v0 diagnostic'te de önce çalışır; prerequisite contamination target negative evidence değildir.
- H1–H4 / solution exposure waiver üretmez; fresh H0 confirm gerekir.
- Diagnostic daily capacity içindedir ve sonuç GRE → waiver → PRG → Topic → Planner replan'e bağlanır.
- Waiver curriculum/objective versiyonuna bağlıdır.
- VDW-v0 deterministic/bounded'dır.

## Aktif adım — 3F Kaçırılan günler

3F başlamadan `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacaktır.

3F'de kesinleştirilecek:
- 1/7/30/60+ günlük ara sonrası current-state yeniden giriş,
- eski PlannedTask/TaskCandidate kuyruğunu taşımama,
- overdue retention/remediation/verification ihtiyaçlarının yeniden üretimi,
- yüzlerce review/task yığılmasını engelleme,
- critical prerequisite ve P0/P1 işlerin recovery'deki davranışı,
- starvation ile absence ayrımı,
- daily capacity içinde recovery planı,
- kullanıcıya `borcun var` hissi yaratmadan tekrar ritme sokma,
- 3A–3E state/priority/eligibility ile deterministik entegrasyon.
