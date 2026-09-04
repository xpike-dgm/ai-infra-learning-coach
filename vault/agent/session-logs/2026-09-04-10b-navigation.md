---
type: session-log
status: completed
stage_step: 10B
model: NSHX-v0
decision: D-083
date: 2026-09-04
---

# 10B — Navigation Shell

10A merge edildikten sonra fresh 10B PRE main üzerinden yapıldı; beş kanonik kaynak `10A ✅ / 10B active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/NAVIGATION_SHELL_SPEC.md`
- Machine-readable: `arch/10b_navigation/navigation.yaml`
- QA: `arch/10b_navigation/qa_report.yaml`
- Stale audit: `arch/10b_navigation/stale_reference_audit.yaml`
- Research/synthesis: `research/10b_navigation_research.md`
- Code: `android/core-presentation/.../Navigation.kt`, `android/app-ui/.../AppShell.kt`
- Final: `NSHX-v0 / D-083`
- Independent QA: 104/104 PASS; mutation test 8/8 yakalandı
- Sweep: 28/28 `tools/validate_*.py` PASS

## Durable decisions
Navigasyon kuralları `core-presentation`da yaşar, UI toolkit'inde değil. Dört destination kabul edilmiş sırada ve enum bildirim sırası kanoniktir. Yasak on bir top-level id'nin hiçbiri destination değildir. Kanonik entity başına tek surface objesi vardır. Contextual edge kümesi sayılı ve kapalıdır. Focused flow shell'i askıya alır ve `showsShell` ile `requiresSafeExit` surface'tan türetilir. Dönüş kuralı deterministiktir ve origin geçerliliği açık girdidir. Window class'lar `WFPX-v0` breakpoint'lerinden core'da hesaplanır ve yalnız çizimi değiştirir. Türkçe destination etiketleri çalışma microcopy'sidir ve sahibi 14'tür.

## Key tensions resolved
1. **Olağan yol kararı yanlış yere koyuyor.** Route string'li bir `NavHost` dört kabul edilmiş kararı Compose'a taşırdı; kimlik ve sıra bir modele aittir.
2. **Paylaşılan surface gerçekten tek olmalı.** Origin başına route tutmak yasaklanan çelişkili detay sayfasını yazmayı kolaylaştırırdı; tek obje onu temsil edilemez yapıyor.
3. **"Her şey her şeyi açabilir" sessizce hiyerarşi iddiasına dönüşür.** Kapalı edge kümesi, browse yolunun prerequisite yapısı gibi okunmasını engelliyor.
4. **İki bağımsız bayrak tehlikeli bir durum yaratır.** `showsShell` ve `requiresSafeExit` türetilince "gizli shell + çıkış yok" var olamıyor.
5. **Dönüş kuralı bir replan'in mahsur bırakabileceği yer.** Origin geçerliliği varsayılmıyor, açıkça soruluyor.
6. **Adaptif render ikinci bir IA olmamalı.** Window class yalnız çizimi değiştiriyor; eşikler core'da, toolkit'in bucketing'inde değil.

## Method note
Validator 10B'nin kendi kontratını değil gerçek Kotlin kaynağını okuyor: destination id ve sırası, yasak top-level kümesi, 15 contextual edge, 6 shared detail ve dönüş semantiği `UXIA-v0`nin `ia.yaml`ından; window class'lar ve shell eşlemesi `WFPX-v0`nin `wireframe.yaml`ından alınıp karşılaştırılıyor. Ayrıca app-ui'ın destination id hardcode etmediği ve breakpoint hesaplamadığı kontrol ediliyor. Mutation test 8/8 yakaladı. İki validator hatası çalıştırma sırasında bulundu ve düzeltildi: `MSBX-v0` presentation anahtarı yanlış adla aranıyordu ve spec `NavigationSuiteScaffold`ı adlandırmıyordu.

## Next
10C — Design system implementation is active-not-executed after POST. Fresh PRE + explicit user approval required.
