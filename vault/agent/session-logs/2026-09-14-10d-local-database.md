---
type: session-log
status: completed
stage_step: 10D
model: LDBX-v0
decision: D-085
date: 2026-09-14
---

# 10D — Local Database

10C merge edildikten sonra fresh 10D PRE main üzerinden yapıldı; beş kanonik kaynak `10C ✅ / 10D active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/LOCAL_DATABASE_SPEC.md`
- Machine-readable: `arch/10d_local_database/local_database.yaml`
- QA: `arch/10d_local_database/qa_report.yaml`
- Stale audit: `arch/10d_local_database/stale_reference_audit.yaml`
- Research/synthesis: `research/10d_local_database_research.md`
- Code: `android/data-persistence/.../Schema.kt`, `.../Migrations.kt`, `.../SqlitePersistence.kt`
- Final: `LDBX-v0 / D-085`
- T2: 22 test, JVM üzerinde gerçek SQLite; implementasyon mutation 9/9
- Independent QA: 163/163 PASS; validator mutation 9/9
- Sweep: 30/30 `tools/validate_*.py` PASS

## Durable decisions
Storage engine mimarinin yasakladığını reddeder ve her ret denenerek kanıtlanır. Truth ve curriculum tablolarında UPDATE ve DELETE abort eder. Her `DDM-v0` değer kümesi aynı listeden üretilen bir CHECK'tir. User truth'tan curriculum'a foreign key yoktur. Offset dakika tutulur, tam dakika olmayan reddedilir. Tek global truth sequence projection watermark'ıdır. Migration ileri-yönlü ve adım başına transaction'lıdır. Adapter kolon gereksinimlerini veritabanından okur. Aynı şema JVM'de ve cihazda koşar.

## Key tensions resolved
1. **Taslağa karşı yazılmış suite, taslağın kontrat hatasını yakalayamaz.** İlk şema DDM'den sapmıştı ve testleri geçiyordu; yalnız kontratı okuyan validator yakaladı.
2. **Hatırlanan API bir tahmindir.** Doküman render olmayınca API jar'dan `javap` ile okundu.
3. **Append-only engine'in reddi olmalı.** Adapter'da update metodu olmaması başka her kod yolunu açık bırakır.
4. **İkinci kolon listesi kayar.** Adapter SQLite'a soruyor.
5. **Sessiz kesme bilgi kaybıdır.** Tam dakika olmayan offset reddediliyor.
6. **Erken düşen migration testi atomikliği kanıtlamaz.** Hata kısmi değişiklikten sonra enjekte edildi.
7. **JVM build'i cihazı kanıtlamaz.** Android varyantı ve arm64 native kütüphane APK içinde doğrulandı.

## Method note
Validator 10D'nin kendi kontratını değil gerçek Kotlin DDL'ini `DDM-v0`nin `data_model.yaml`ı ve `LFPS-v0`a karşı okuyor: entity envanterleri, her izinli değer kümesi, zaman kolonları, projection provenance, metadata ve index intent. Validator'ın kendi mutation testi ilk taslağın üç hatasını geri enjekte etti ve üçünü de reddetti. Implementasyon mutation'ı dokuz mutant uyguladı; migration atomikliği mutant'ı başta kaçtı ve testi güçlendirildikten sonra yakalandı.

## Next
10E — Temel uygulama sağlığı is active-not-executed after POST. Fresh PRE + explicit user approval required.
