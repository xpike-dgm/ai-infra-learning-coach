---
type: session-log
status: completed
stage_step: 9D
model: MSBX-v0
decision: D-078
date: 2026-08-29
---

# 9D — Module & Service Boundaries

9C merge edildikten sonra fresh 9D PRE main üzerinden yapıldı; üç ref koşulu doğrulandı ve beş kanonik kaynak `9C ✅ / 9D active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/SERVICE_BOUNDARIES_SPEC.md`
- Machine-readable: `arch/9d_service_boundaries/boundaries.yaml`
- QA: `arch/9d_service_boundaries/qa_report.yaml`
- Stale audit: `arch/9d_service_boundaries/stale_reference_audit.yaml`
- Research/synthesis: `research/9d_service_boundaries_research.md`
- Final: `MSBX-v0 / D-078`
- Independent QA: 93/93 PASS — 10 modül / 4 port / 8 engine / 15 forbidden pattern
- Stage 6 / Stage 7 / AŞAMA 8 / 9A / 9B / 9C / external-memory regressions: PASS

## Durable decisions
Sınırlar garantileri yapısal hâle getirir. On modül ve katı içe-doğru dependency kuralı vardır; `core-*` asla `data-*`, `ai-*` veya `app-*`'e bağımlı olamaz ve graf asiklikdir. Core'un dışarıdan ihtiyaç duyduğu her şey port'tur: `PersistencePort`, `ContentPort`, `ClockPort`, `EvaluatorPort`. Saat bir port'tur ve core sistem saatini doğrudan okumaz. Core'da rastgelelik yoktur; beraberlikler beyan edilmiş total ordering ile çözülür. Null evaluator ürünle sevk edilir ve app `ai-adapter` olmadan build edilip çalışır. Her engine tam olarak bir state ailesine sahiptir ve başkasınınkini yazmaz. Transaction sınırı `core-application`da, presentation projection `core-presentation`dadır. `app-wiring` composition root'tur ve domain logic içermez.

## Key tensions resolved
1. **"Core AI olmadan çalışır" bir vaatti.** Hiçbir şey yarın bir engine'in AI client import etmesini engellemiyordu. Yıllarca herkesin hatırlamasına bağlı bir vaat garanti değil; dependency kuralı garanti.
2. **Determinizm sessizce ölür.** Bir engine içindeki tek bir sistem saati çağrısı `ADAPTIVE_PLANNER_SPEC` §18'i bozar ve hata bug gibi değil flakiness gibi görünür. Saat port'a çevrildi.
3. **Zaman ortam gerçeği değil girdi.** `DDM-v0` her kayıtta instant + study day + offset istiyor; core saati doğrudan okusa bu mantık test edilemez ve timezone davranışı kullanıcı seyahat edene kadar görünmez olurdu.
4. **Presentation UI toolkit'te yaşayamaz.** `SPWX-v0` presentation state'i deterministik projeksiyon yapıyor; Compose içinde olsaydı üründeki en güvenlik-kritik etiketleme yalnız cihazda test edilebilirdi.
5. **Engine'ler birbirinin state'ini yazamaz.** Aksi hâlde önceki her aşamanın yasakladığı ikinci source of truth sessizce geri gelirdi.

## Method note
Validator dependency grafını **iddia etmez, hesaplar**: bilinmeyen bağımlılıkları, cycle'ı (renklendirmeli DFS) ve forbidden layer edge'lerini graf üzerinden bulur. Ayrıca kararı upstream kontratlara karşı doğrular — AMTS/LFPS'in boundary layout'u gerçekten 9D'ye devrettiğini, DDM'nin üç zaman değerini istediğini, planner spec'inin determinizmi şart koştuğunu, TRUX/ASUX'un `evaluation_pending` için evidence'ı yasakladığını ve V1 kriteri 8'in metinde bulunduğunu. Mutation test: 6 kasıtlı ihlal 8 check FAIL verdi.

## Next
9E — AI entegrasyon mimarisi is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
