---
type: session-log
status: completed
stage_step: 8G
model: WFPX-v0
decision: D-074
date: 2026-08-29
stage_closed: 8
---

# 8G — Wireframe & Prototype Geometry

Kullanıcı 8F PR #15'i merge etti; fresh 8G PRE main üzerinden yapıldı. Üç ref koşulu doğrulandı (`HEAD=main`, `HEAD==origin/main`, açık PR yok) ve beş kanonik kaynak `8F ✅ / 8G active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/WIREFRAME_PROTOTYPE_SPEC.md`
- Machine-readable: `ux/8g_wireframe_prototype/wireframe.yaml`
- Prototip: `ux/8g_wireframe_prototype/prototype.html` (bağlayıcı değil)
- QA: `ux/8g_wireframe_prototype/qa_report.yaml`
- Stale audit: `ux/8g_wireframe_prototype/stale_reference_audit.yaml`
- Research/synthesis: `research/8g_wireframe_prototype_research.md`
- Final: `WFPX-v0 / D-074`
- Independent QA: 222/222 PASS — 3 window class / 6 surface / 52 ölçülen kontrast çifti / 11 forbidden anti-pattern
- Stage 6 / Stage 7 / 8A–8F / external-memory regressions: PASS

## AŞAMA 8 kapandı
8A UXIA-v0 → 8B THUX-v0 → 8C TRUX-v0 → 8D ASUX-v0 → 8E SPWX-v0 → 8F VDSX-v0 → 8G WFPX-v0.

## Durable decisions
Geometry kabul edilmiş anlamı yerleştirir; surface anlamını, region sırasını, state'i veya tone'u değiştiremez. Üç window class tanımlıdır ve destination kimliği/sırası her sınıfta aynıdır; hiçbir sınıf region ekleyip çıkaramaz ve `expanded` detail pane ikinci bir truth kopyası değildir. Altı surface'in region geometry'si sahibi spec'lere karşı doğrulandı. `skill_detail` primary chip ile dört ekseni birlikte gösterir; `progress_overview` yalnız envanter tutar. Focused-flow'da exit ve pause her sınıfta 48dp tam hedef ve sabit konum korur; countdown yoktur. Palet light ve dark için bağımsız ölçüldü ve 52 zorunlu çift geçti; oranlar kanıttır, source of truth değildir. `attention` menekşedir ve kırmızı yalnız `system_fault` içindir. %200 metinde layout reflow eder; state truncate edilmez. Prototip bağlayıcı değildir ve teknoloji seçimi yapmaz.

## Key tensions resolved
1. **Ölçülmüş vs iddia edilmiş palet.** VDSX-v0 kısıtı sabitleyip değerleri bırakmıştı; hex üretip oranları hesaplamamak, ertelemenin amacını sessizce ortadan kaldırırdı. Validator artık her oranı hex'ten yeniden hesaplıyor.
2. **Traffic-light okuması.** `attention` kehribar + `system_fault` kırmızı olsaydı palet bir şiddet rampası olur ve tone tablosu ne derse desin her attention state'i "başarısızlığa bir adım" gibi okunurdu. Hue seçimi bir severity kararı olarak ele alındı: attention menekşe, kırmızı yalnız arızaya.
3. **Pixel comp olmadan geometry.** Markdown/YAML/Python reposu pixel comp'u anlamlı tutamaz ve regression-check edemez; geometry declared structure olarak ifade edildi.
4. **Prototip teknoloji taahhüdü değil.** 9A teknoloji seçiminin sahibi; prototip açıkça bağlayıcı olmayan görselleştirme olarak işaretlendi.

## Method note
Validator ilk turda **beni yakaladı**: `measured_minima.text_on_surface` 6.76 yazmıştım, gerçek minimum 6.08 idi (muted metin `surface_variant` üzerinde). Beyan edilen minimumun hesaplananla eşleşmesini kontrol eden check FAIL verdi; düzeltildi. Ayrıca mutation test: 6 kasıtlı ihlal (kehribar attention, kontrast kırma, Today region sırası kaydırma, progress overview'a oran, prototype binding, assessment chrome'a countdown) 16 check FAIL verdi ve dosya geri alındı.

## Next
9A — Mobil teknoloji seçimi is active-not-executed after POST. AŞAMA 9 başlar. Fresh PRE + explicit user approval required before execution.
