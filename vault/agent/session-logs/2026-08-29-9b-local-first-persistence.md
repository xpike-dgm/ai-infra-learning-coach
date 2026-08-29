---
type: session-log
status: completed
stage_step: 9B
model: LFPS-v0
decision: D-076
date: 2026-08-29
---

# 9B — Local-First Persistence

9A merge edildikten sonra fresh 9B PRE main üzerinden yapıldı; üç ref koşulu doğrulandı ve beş kanonik kaynak `9A ✅ / 9B active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/LOCAL_FIRST_PERSISTENCE_SPEC.md`
- Machine-readable: `arch/9b_local_first_persistence/persistence.yaml`
- QA: `arch/9b_local_first_persistence/qa_report.yaml`
- Stale audit: `arch/9b_local_first_persistence/stale_reference_audit.yaml`
- Research/synthesis: `research/9b_local_first_persistence_research.md`
- Final: `LFPS-v0 / D-076`
- Independent QA: 100/100 PASS — 8 truth record / 7 derived projection / 17 forbidden pattern
- Stage 6 / Stage 7 / AŞAMA 8 / 9A / external-memory regressions: PASS

## Durable decisions
Kanıt source of truth'tur; mastery, retention, prerequisite readiness, Topic state, weakness ve Technical English profile onun yeniden hesaplanabilir projeksiyonudur. Truth kayıtları append-only'dir ve geçersiz kanıt silinmez, işaretlenir. Storage engine SQLite'tır; ORM library 10A'ya bırakıldı. Persistence interface'leri core'a aittir ve core signature'ında storage/Android/fs tipi bulunmaz. Curriculum ve user state ayrı saklanır ve ayrı versiyonlanır; curriculum güncellemesi tek başına learner state değiştiremez. Exposure kayıtları kalıcı ve first-class'tır ve kaybı veri kaybıdır. Bir öğrenci eylemi bir transaction'dır; `evaluation_pending` evidence yazmaz. Migration forward-only'dir ve kanıtı yok etmez; restore atomik ve doğrulanmıştır. Bozulma `data_recovery_required` yüzeyler, sessiz reset yasaktır ve tutarsız projeksiyon recomputation ile onarılır. V1'de kanıt budanmaz.

## Key tensions resolved
1. **Source of truth ne?** Derived state otoriter olsaydı bir migration veya bug, hiçbir kanıt değişmeden öğrencinin "kanıtladığı" şeyi değiştirebilirdi. Kanıtı truth, state'i projeksiyon yapmak bu bozulma sınıfını yapısal olarak kaldırdı ve `recomputing_projection` state'ine gerçek karşılık verdi.
2. **Exposure kayıtları sonsuza dek load-bearing.** Bir exposure kaydı kaybolursa solution-exposed item daha sonra taze bağımsız kontrol olarak sunulabilir. Hiçbir şey çökmez; ürün sessizce sahte independent evidence üretmeye başlar. Sistemdeki en sessiz hata biçimi ve engellenebileceği tek yer storage katmanı.
3. **Kısmi yazma, UI'ın yalan söylemeye başlamasının yolu.** Assistance metadata'sız attempt veya provenance'sız evidence, ürünün sekiz aşamada yasakladığı şeyi üretir. Tek-eylem-tek-transaction kuralı bunu kapattı.
4. **Core purity vs platform veritabanı.** Core Android'e bağımlı olamaz ama okuma/yazma yapmak zorunda. Bağımlılık ters çevrildi, taviz verilmedi.

## Method note
Validator kararı kontratlara karşı doğrular: curriculum/user ayrımı `V1_SCOPE.md` metninden, published-version değişmezliği `KGC-v0` metninden, exposure ayrımı `QUESTION_BANK_SPEC.md` metninden, exposed-item yeniden kullanım yasağı `ASUX-v0`dan, `evaluation_pending` evidence yasağı hem `TRUX-v0` hem `ASUX-v0`dan, `data_recovery_required` ve `recomputing_projection` gerçekliği `SPWX-v0`dan, core purity `AMTS-v0`dan okunur. İlk çalıştırmada `TRUX-v0` içindeki anahtar adını yanlış varsaydım (`evidence_written` yerine `evaluation_pending_writes_evidence`); validator KeyError verdi ve düzeltildi. Mutation test: 7 kasıtlı ihlal 6 check FAIL verdi.

## Next
9C — Domain veri modeli is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
