---
type: session-log
status: completed
stage_step: 9A
model: AMTS-v0
decision: D-075
date: 2026-08-29
stage_opened: 9
---

# 9A — Mobile Technology Selection

8G merge edildikten sonra fresh 9A PRE main üzerinden yapıldı; üç ref koşulu doğrulandı ve beş kanonik kaynak `AŞAMA 8 kapalı / 9A active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/MOBILE_TECHNOLOGY_SPEC.md`
- Machine-readable: `arch/9a_mobile_technology/technology.yaml`
- QA: `arch/9a_mobile_technology/qa_report.yaml`
- Stale audit: `arch/9a_mobile_technology/stale_reference_audit.yaml`
- Research/synthesis: `research/9a_mobile_technology_research.md`
- Final: `AMTS-v0 / D-075`
- Independent QA: 100/100 PASS
- Stage 6 / Stage 7 / AŞAMA 8 / external-memory regressions: PASS

## Durable decisions
Teknoloji seçimi kabul edilmiş kontratlara hizmet eder; çelişkide kontrat kazanır. Android native, V1'de cross-platform UI katmanı yok. Kotlin + Jetpack Compose. Material 3 yalnız substrate, `VDSX-v0` token'ları otoriter, **dynamic colour kapalı**. Domain core saf Kotlin; Android API, UI toolkit, networking ve AI client bağımlılığı yasak. Üç window class `WFPX-v0` ile birebir. Default-locale case transform yasak; Türkçe `i ↔ İ` / `ı ↔ I` round-trip eder; kilitli `SPWX-v0` etiketleri case-transform edilemez. `minSdk` politikadır (working default API 26), 10A'da gerçek cihaza karşı doğrulanacaktır. Kurulabilir APK ve gerçek cihaz QA zorunlu.

## Bu adım araştırma açısından farklıydı
8B–8G'nin hepsi "separate external Research AI gerekmedi" dedi çünkü internal contract synthesis'ti. 9A değil: `AI_AGENT_WORKFLOW.md` §3 hem "Hangisini seçmeliyiz?" hem "framework/library güncelliği" sorularını Araştırma AI'a yönlendirir. Pozisyon ikiye ayrıldı — seçim mantığı kabul edilmiş kontratlardan türetilir ve güncel veriye ihtiyaç duymaz; library güncelliği duyar. Bu yüzden 6 maddelik **bounded verification list** üretildi ve 10A'ya devredildi. Güncellik iddiası kesinleştirilmedi.

## Key tensions resolved
1. **Cross-platform katman maliyetini hak ediyor mu?** V1 tek platforma çıkıp diğerlerini dışladığı için fayda yok, maliyet var — ve maliyet tam olarak AŞAMA 8'in kontrata bağladığı üç yere iniyor.
2. **Kapıyı yine de açık tutmak.** Taşınabilir parça UI değil domain core yapıldı; sert dependency kuralı, bugün cross-platform toolkit almaktan daha iyi bir hedge.
3. **Design system library'si design system ile savaşır.** Material You paleti duvar kağıdından türetir ve 8G'nin ölçülmüş paletini, kontrast kanıtını ve hue politikasını sessizce yok ederdi. Dynamic colour kapatıldı.
4. **Türkçe casing platform-API meselesi.** Default-locale transform codebase'de yasaklandı; identifier karşılaştırmaları locale-sensitive casing kullanamaz.

## Method note
Validator seçimi kontratlara karşı doğrular, opinion'a karşı değil: window class'lar `WFPX-v0`dan, hedef/focus kontrastı ve Türkçe glyph seti `VDSX-v0`dan, kilitli etiketler `SPWX-v0`dan, destination sayısı `UXIA-v0`dan, platform/APK/cihaz-QA kısıtları `V1_SCOPE.md` metninden, Araştırma AI yönlendirmesi `AI_AGENT_WORKFLOW.md` metninden okunur. Mutation test: 6 kasıtlı ihlal 7 check FAIL verdi.

## Open loops raised
- 10A'ya devredilen 6 maddelik verification list (güncel Compose/M3 adaptive API'leri, dynamic colour kapatma mekanizması, `minSdk` doğrulaması, screen-reader semantics, reduced-motion tespiti, compatibility library ihtiyacı).
- **Hedef Android cihaz repoda kayıtlı değil**; `minSdk` doğrulaması için 10A'da gerekli.

## Next
9B — Veri saklama / local-first is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
