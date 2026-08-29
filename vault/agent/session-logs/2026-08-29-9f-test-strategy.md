---
type: session-log
status: completed
stage_step: 9F
model: TVSX-v0
decision: D-081
date: 2026-08-29
---

# 9F — Test & Verification Strategy

9E merge edildikten sonra fresh 9F PRE main üzerinden yapıldı; beş kanonik kaynak `9E ✅ / 9F active-not-executed` gösterdi. Kullanıcı açık onay verdi ve aynı turda `D-080` dağıtım kapsamı kararını da yazmamı istedi.

## Result
- Canonical: `docs/TEST_STRATEGY_SPEC.md`
- Machine-readable: `arch/9f_test_strategy/test_strategy.yaml`
- QA: `arch/9f_test_strategy/qa_report.yaml`
- Stale audit: `arch/9f_test_strategy/stale_reference_audit.yaml`
- Research/synthesis: `research/9f_test_strategy_research.md`
- Final: `TVSX-v0 / D-081` — **AŞAMA 9 kapandı**
- Independent QA: 288/288 PASS — 66 register entry / 6 tier / 9 negative check / 11 gate condition / 19 forbidden pattern
- Mutation test: 8 deliberate violations → 8 detected
- Sweep: 26/26 `tools/validate_*.py` PASS

## Durable decisions
Üzerine hiçbir şeyin düşmediği bir garanti bir tercihtir: kabul edilmiş her invariant'ın, ihlal edildiğinde FAIL veren adı konmuş bir sahibi vardır ve sahipsiz bir invariant tek başına release'i bloklar. Altı doğrulama katmanı vardır ve cihaz katmanı en küçüğüdür. Coverage yüzdesi release gate değildir; gate invariant coverage'dır. Bir şeyi yasaklayan her kural için yasaklanan denenir ve reddedilmesi şart koşulur. Append-only schema seviyesinde doğrulanır; migration'lar dolu fixture'lara karşı çalıştırılır ve evidence/exposure/provenance birebir korunur. Hiçbir check canlı AI provider çağırmaz; yedi sonucun tamamı kayıtlı yanıtlarla üretilir. Null-evaluator yolu `ai-adapter` olmadan build alınarak doğrulanır. Determinizm enjekte saat ve tekrarlanan byte-identical koşularla egzersiz edilir; flaky check düşmüş check'tir ve retry-to-green yasaktır. Altı severity sınıfı vardır ve `evidence_correctness` her zaman bloklar. Release gate 11 koşuldur ve `tools/validate_*.py` glob'unun tamamını içerir. Invariant register'ın her kaydı upstream kabul edilmiş kontratta gerçekten var olan bir anahtardır; 9F yeni ürün semantiği icat etmez.

## Key tensions resolved
1. **Yapı da çürür.** Check'i olmayan bir dependency kuralı bir yorumdur; negatif testi olmayan bir append-only schema'sı kimsenin doğrulamadığı bir SQL varsayımıdır. `LFPS-v0`nin "dolu veriye karşı test edilebilir migration" gereksiniminin bu adıma kadar sahibi yoktu.
2. **Coverage yüzdesi yanlış gate.** Proxy sayıları bu projede her yerde reddedildi; bir yüzde, invariant'lar kontrolsüzken yükselebilir.
3. **Yeşil testler hiçbir şey iddia etmeyebilir.** Spec seviyesinde bu ders zaten alınmıştı (validator'lar mutation-tested; 8G validator'ı yazarının elle beyan ettiği bir sayıyı yakaladı). Aynı disiplin ürün suite'ine taşındı.
4. **Asıl tehlikeli testler negatif olanlar.** Doğrunun çalıştığını değil, yanlışın imkânsız olduğunu doğrulamak gerekir.
5. **Canlı provider çağrısı testte kabul edilemez.** Deterministik değildir, kullanıcının parasını harcar ve hataları atfedilemez kılar.
6. **Flaky test burada başarısız testten kötüdür.** Deterministik olmak zorunda olan bir planner'da "bir daha çalıştır"ı normalleştirmek, yasaklanan kusur sınıfını gizler.
7. **Geçen bir suite pedagojik doğruluk kanıtı değildir** ve öyle sunulamaz.

## Method note
Validator kararı upstream kontratlara doğrular: 66 register invariant'ının her biri kaynak yaml'da beyan edilen değeriyle aranır, AI sonuç kümesi `AIAX-v0` taksonomisiyle birebir karşılaştırılır, kontrast çapaları/48dp `WFPX-v0`den ve migration/restore beklentileri `LFPS-v0`den okunur, beş upstream deferral ilgili spec'lerin ham metninde aranır ve `V1_SCOPE`in on kriteri metinden parse edilir. Ayrıca spec ve contract'ın hiçbir somut test library'si adlandırmadığı algoritmik kontrol edilir. Validator kendi iki hatasını yakaladı: her katmanın invariant sahiplenmesi şartı T6 için yanlıştı ve daraltıldı; `truth` terimi ürünün kendi sözlüğüyle çakıştığı için library taramasından çıkarıldı.

## Next
10A — Proje kurulumu is active-not-executed after POST. Fresh PRE + explicit user approval required. Two inherited obligations: the `AMTS-v0` §9 bounded verification list and the target Android device, still unrecorded in the repo.
