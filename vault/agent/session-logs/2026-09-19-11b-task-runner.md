---
type: session-log
status: completed
stage_step: 11B
model: RNRX-v0
decision: D-088
date: 2026-09-19
---

# 11B — Task runner

11A merge edildi (CI iki check yeşil); fresh 11B PRE main üzerinden yapıldı ve beş kanonik kaynak `11A ✅ / 11B active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/TASK_RUNNER_SPEC.md`
- Machine-readable: `arch/11b_task_runner/task_runner.yaml`
- QA: `arch/11b_task_runner/qa_report.yaml`
- Stale audit: `arch/11b_task_runner/stale_reference_audit.yaml`
- Research/synthesis: `research/11b_task_runner_research.md`
- Code: `core-model/.../AttemptFacts.kt`, `core-presentation/.../TaskRunner.kt`, `core-application/.../SubmitAttempt.kt`, `app-ui/.../TaskRunnerScreen.kt`, `app-wiring`
- Final: `RNRX-v0 / D-088`
- Tests: TaskRunnerTest 18, SubmitAttemptTest 7, T2 +3 (T2 toplam 50); implementasyon mutation 18/18
- Independent QA: 147/147 PASS; validator mutation 18/18
- T6: çalıştırılmadı

## Found on main before runner code
- 11A'nın sync betiği `"İ".lower()` ile U+0307 üretmiş ve beş Türkçe kelimeye sızdırmıştı.
- Aynı betik yarıda çöküp parça parça yeniden koşulduğu için `HANDOFF_STATE`te mükerrer D-087 satırı, `MASTER_PLAN`da mükerrer "11B" başlığı ve `PROJECT_CONTEXT`te eksik 12.13 bölümü kalmıştı.
- Hepsi düzeltildi; validator artık hem U+0307'yi hem living dokümanlarda bitişik mükerrer satırı yakalıyor.

## Durable decisions
Runner bir execution surface'tir. Girişte her koşul doğrulanmalı; doğrulanamayan `unmet`, `failed` değil; bugün hiçbir görev başlatılamaz. Yardım hep istenebilir, istenmeden verilmez, H3/H4 sonuç açıklanmadan verilmez — kapsam koşulu olmadan. Deneme tek transaction'dır (attempt + artifact + provenance + assistance) ve evidence yazmaz. Türetilmiş olgular saklanmaz; `DDM-v0`nin adlandırmadığı alanlar uydurulmaz ve sahipleriyle kaydedilir. `appendTruth` satır id'si döndürür.

## Key tensions resolved
1. **Kabul edilmiş bir kuralın makul bir okuması yine de sessiz bir daraltmadır.** H3/H4 açıklaması literal uygulandı.
2. **Tesadüfen doğru olan değer garanti değildir.** Tonlar UI'dan core'a taşındı ve teste bağlandı.
3. **Doğrulanamayan, başarısız değildir.** Alan adı `unmet`.
4. **Atomiklik gerçek motorda kanıtlanmalı ama modül sınırı buna izin vermiyor.** Kanıt T1 (şekil) ve T2 (depolama) olarak bölündü.
5. **Yarıda çöken bir betik tekrar koşulursa iz bırakır.** Kalan sync adımları idempotent yapıldı ve mükerrer satır guard'a bağlandı.

## Next
11C — Session state is active-not-executed after POST. Fresh PRE + explicit user approval required.
