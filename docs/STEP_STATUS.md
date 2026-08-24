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
| **3A — Günlük kapasite** | 🟡 Aktif | Kısa/normal/yoğun gün, capacity budget, minimum block, overflow/remediation/retention sınırı tasarlanacak. |
| **3B ve sonrası** | ⬜ Bekliyor | 3A kapanışından sonra. |

## Son tamamlanan adım — 2F

Ana çıktılar:
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/2F_RESEARCH_VALIDATION.md`
- D-032

### Final RVR-v0 özeti
- Mastery ve retention ayrı eksen.
- Time-based GRE score decay yok.
- Retention: `untracked | fresh | stable | review_due | verification_due | at_risk`.
- `review_due` forgetting değildir; Topic weakening/hard prereq block üretmez.
- Delayed verification target Skill'e uygun H0 direct verified evidence ister.
- First failure → `verification_due`; fresh recheck.
- Recheck failure → normal GRE evidence + gates yeniden hesaplanır; score elle resetlenmez.
- Natural reuse strict attribution ile planned review yerine geçebilir.
- Automatic component/cluster refresh yok.
- Critical `verification_due` unresolved iken dependent yeni work bekleyebilir.
- Missed-day backlog dump yok.
- Initial intervals/growth/max değerleri v0 engineering heuristic ve 17C calibration girdisi.
- Bounded/incremental implementation D-028 ile uyumlu.

## Aktif adım — 3A Günlük kapasite

3A başlamadan `PROJECT_MEMORY_PROTOCOL.md` gereği yeni PRE-STEP refresh yapılacaktır.

3A'da kesinleştirilecek:
- günlük capacity modeli,
- kısa/normal/yoğun gün profilleri,
- minimum viable study block,
- new learning vs remediation vs retention capacity davranışı,
- planın hedef sürenin üstüne kontrolsüz büyümemesi,
- overflow/defer davranışı,
- kullanıcı o gün daha az/fazla zamanı olduğunu söylediğinde replan,
- ileride 3B–3G'nin kullanacağı capacity contract.
