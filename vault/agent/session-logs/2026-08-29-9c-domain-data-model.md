---
type: session-log
status: completed
stage_step: 9C
model: DDM-v0
decision: D-077
date: 2026-08-29
---

# 9C — Domain Data Model

9B merge edildikten sonra fresh 9C PRE main üzerinden yapıldı; üç ref koşulu doğrulandı ve beş kanonik kaynak `9B ✅ / 9C active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/DOMAIN_DATA_MODEL_SPEC.md`
- Machine-readable: `arch/9c_domain_data_model/data_model.yaml`
- QA: `arch/9c_domain_data_model/qa_report.yaml`
- Stale audit: `arch/9c_domain_data_model/stale_reference_audit.yaml`
- Research/synthesis: `research/9c_domain_data_model_research.md`
- Final: `DDM-v0 / D-077`
- Independent QA: 114/114 PASS — 11 curriculum / 12 truth / 8 projection entity / 15 forbidden pattern
- Stage 6 / Stage 7 / AŞAMA 8 / 9A / 9B / external-memory regressions: PASS

## Durable decisions
Schema mimariyi uygular; `LFPS-v0`nin truth/projection, append-only, exposure kalıcılığı ve version pinning garantileri yapısaldır. Üç store bölgesi vardır ve curriculum store'dan user store'a foreign key yoktur. Versiyonlu kimlik `(logical_id, version)` composite'tir ve her user referansı version taşır. Truth tablolarında UPDATE/DELETE yolu yoktur; düzeltme append edilen `evidence_disposition`'dır. Dört bağımsız evidence ekseni dört ayrı kolondur. Her timestamp'li satır instant, learner-local study day ve UTC offset saklar. Her projection satırı policy version, truth watermark, build time ve input curriculum version kaydeder. `SPWX-v0` dört Skill ekseni storage'da da ayrıdır. Exposure seçim yolunda indekslenir ve asla silinmez. Physical schema library-neutral'dır; core-visible modelde platform tipi yoktur.

## Key tensions resolved
1. **Bir schema bir mimariyi sessizce yürürlükten kaldırabilir.** Mutable `outcome` kolonu + disposition tablosu yokluğu, `LFPS-v0`nin iki garantisini birden siler ve hiçbir test bunu bildirmez.
2. **Pinning ancak foreign key version taşırsa gerçektir.** Yalnız logical ID ile referans, curriculum güncellenince sessizce en yeni version'a kayar.
3. **Dört eksen birleştirilemez.** Her birleştirme somut bir ayrımı yok ediyor — provisional/settled veya bağımsız/cevabı-görmüş.
4. **Zaman tek değer değil.** DST ve seyahat sonrası instant ile study day birbirinden güvenilir türetilemez; ikisi de + offset saklanmalı.
5. **Exposure sorgulanabilir olmalı.** Garanti bir retention vaadi değil, seçim yolundaki bir lookup.

## Method note
Validator modeli kendine değil kaynak kontratlara karşı doğrular: LFPS truth/projection listeleri, KGC Skill alanları ve published-version değişmezliği, GNS ID kuralları, GRE EvidenceEvent alanları (ve o sözleşmenin gerçekten 9C'ye devredildiği metin kontrolü), TRUX assistance/provenance değerleri, ASUX evaluator status, SPWX dört eksen ve `recomputing_projection`. Mutation test: 7 kasıtlı ihlal 6 check FAIL verdi.

## Next
9D — Servis sınırları is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
