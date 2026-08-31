---
type: session-log
status: completed
stage_step: 10A
model: MPSX-v0
decision: D-082
date: 2026-08-31
---

# 10A — Mobile Project Skeleton

9F merge edildikten sonra fresh 10A PRE main üzerinden yapıldı; beş kanonik kaynak `9F ✅ / 10A active-not-executed` gösterdi. Kullanıcı açık onay verdi. Hedef cihaz bilgisi kullanıcıdan alındı ve bu adımda kayda geçti.

## Result
- Canonical: `docs/PROJECT_SETUP_SPEC.md`
- Machine-readable: `arch/10a_project_setup/project_setup.yaml`
- QA: `arch/10a_project_setup/qa_report.yaml`
- Stale audit: `arch/10a_project_setup/stale_reference_audit.yaml`
- Research/synthesis: `research/10a_project_setup_research.md`
- Project: `android/` — 10 modül, 18 Kotlin dosyası, 8 test
- Final: `MPSX-v0 / D-082`
- Independent QA: 127/127 PASS; mutation test 7/7 yakalandı
- Sweep: 27/27 `tools/validate_*.py` PASS

## Durable decisions
Çalıştırılmamış hiçbir şey iddia edilmez. Toolchain 2026-08-31'de doğrulandı ve tarihiyle kaydedildi. Hedef cihaz Poco M6 Pro / API 36 olarak kaydedildi ve `AMTS-v0` §8.1 kapandı; `minSdk` 26 en düşük shim'siz seviye olduğu için yükseltilmedi. On modül `android/` altında ve bağımlılıklar `boundaries.yaml`a eşit. Yalnız iki `app-*` modülü Android modülüdür. `verifyModuleBoundaries` build'i düşürüyor ve üçüncü parti analyser kullanılmadı. Adaptörsüz build gerçekten geçiyor ve `NullEvaluator` ürünle sevk ediliyor. DI framework, ORM, HTTP client ve architecture-rule library yok. Saat tek yerde okunuyor, dynamic colour hiçbir yerde yok, key repoya giremiyor. CI T3/T1/iki build ve tam validator glob'unu koşuyor; T6 CI'da değil.

## Key tensions resolved
1. **Spec ile çalışan kod arasındaki fark.** Bu adıma kadar doğrulama iç tutarlılıktı; burada hakem build. İlk denemede iki pin yanlış çıktı ve bu, adımın kendi invariant'ının en iyi kanıtı oldu.
2. **`minSdk`: politika mı, cihaz mı?** Cihaz API 36 olduğu için 36'ya çekmek cazipti; §8.1 en düşük shim'siz seviyeyi istiyor ve 26 `java.time` sınırı. Politikaya uyuldu.
3. **Dependency kuralını ne zorlayacak?** Bir architecture-rule library güncellik riski ve bağımlılık ekleyecekti; kural Gradle'ın kendi verisiyle tam ifade edilebiliyordu.
4. **Adaptörsüzlüğü nasıl gerçek kılmalı?** Runtime bayrağı bir dal olurdu; source-set seçimiyle adaptör sınıfı hiç classpath'e girmiyor ve build gerçekten adaptörsüz.
5. **ORM mü, el yazımı SQL mi?** Append-only'nin storage seviyesinde zorlanması ve T2'nin JVM'de koşması gerekiyordu; bundled driver + el yazımı SQL ikisini de veriyor.
6. **Emülatör device tier sayılır mı?** Sayılmaz. CI'da T6 yok, çünkü release gate kontrol etmediği bir şeyi iddia edemez.

## Method note
Library güncelliği `AI_AGENT_WORKFLOW` §3 gereği araştırma işi olduğundan hatırlamayla değil web kaynaklarıyla doğrulandı ve her pin tarihiyle kaydedildi. Validator 10A'nın kendi contract'ını değil gerçek Gradle ve Kotlin dosyalarını okuyor; bunları `boundaries.yaml`, `TVSX-v0`, `AMTS-v0`, `AIAX-v0` ve cihazın kendi API seviyesine karşı doğruluyor. Boundary task'ı mutation-test edilirken kendi cycle raporlamasındaki hata bulundu ve düzeltildi.

## Next
10B — Navigation is active-not-executed after POST. Fresh PRE + explicit user approval required.
