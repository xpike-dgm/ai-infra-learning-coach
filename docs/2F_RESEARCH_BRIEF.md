# 2F Research Brief — Forgetting / Retention / Spaced Repetition

> **D-050 / D-044 hygiene note (2026-08-25):** Bu tarihsel araştırma/provenance belgesindeki ileride yapılacak aşamalara ait eski numaralar D-044 öncesi planı yansıtabilir. Güncel karşılık için `docs/STAGE_REINDEX_MAP.md` ve `docs/EXECUTION_INDEX.md` kullanılır. Tarihsel araştırma metni sessizce yeniden yazılmamıştır.


**Adım:** 2F — Unutma modeli  
**Durum:** TAMAMLANDI / RAPOR ALINDI VE DEĞERLENDİRİLDİ  
**Tarih:** 2026-08-24

Bu Research AI görevi tamamlandı. Kullanıcı bağımsız Deep Research raporunu sağladı; ana yönetici raporu otomatik kabul etmeyip kritik iddiaları ayrıca doğruladı.

Final çıktılar:
- `docs/2F_RESEARCH_VALIDATION.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- D-032

Final model: **`RVR-v0 — Retention Verification & Risk`**.

Önemli sonuçlar:
- time-based mastery score decay yok,
- mastery ve retention ayrı eksen,
- `review_due` forgetting değildir,
- active H0 delayed verification,
- first failure → `verification_due`,
- natural reuse strict attribution ile retention evidence olabilir,
- no automatic cluster refresh,
- no missed-day backlog dump,
- interval sayıları versioned heuristic + pilot calibration,
- 2F tamamlandı; sonraki adım `3A — Günlük kapasite`.