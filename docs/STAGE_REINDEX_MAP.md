# Stage Reindex Map — D-044 Sonrası Legacy Future-Reference Çevirisi

**Durum:** CANONICAL REFERENCE MAP  
**Tarih:** 2026-08-25  
**Bağlayıcı kararlar:** D-017, D-044, D-050

Bu dosya, D-044 ile **AŞAMA 6 — Granular Capability Map** eklendiğinde henüz başlanmamış future stage'lerin yeniden numaralanması nedeniyle eski tamamlanmış spec/research dosyalarında kalabilecek future-stage referanslarını güvenli biçimde yorumlamak için tutulur.

Bu dosya yeni execution plan değildir. Canonical güncel stage/adım kimlikleri her zaman:
- `docs/EXECUTION_INDEX.md`
- `docs/MASTER_PLAN.md`

üzerinden doğrulanır.

## 1. Reindex'in sınırı

D-044 eklenirken tamamlanmış **AŞAMA 1–5 kimlikleri değiştirilmedi**. Yeni granular planning aşaması AŞAMA 6 olarak eklendi ve o tarihte henüz başlanmamış eski AŞAMA 6–19 blokları bir sıra ileri taşındı.

Canonical mapping:

| D-044 öncesi future stage | D-044 sonrası canonical stage | Anlam |
|---|---|---|
| AŞAMA 6 | **AŞAMA 7** | Technical English parallel line |
| AŞAMA 7 | **AŞAMA 8** | UX / screens |
| AŞAMA 8 | **AŞAMA 9** | Architecture / data model |
| AŞAMA 9 | **AŞAMA 10** | Mobile skeleton |
| AŞAMA 10 | **AŞAMA 11** | Daily learning MVP |
| AŞAMA 11 | **AŞAMA 12** | Mastery / prerequisite / planner implementation |
| AŞAMA 12 | **AŞAMA 13** | Assessment / retention / remediation implementation |
| AŞAMA 13 | **AŞAMA 14** | AI Tutor / evaluation |
| AŞAMA 14 | **AŞAMA 15** | First 8–12 week production content |
| AŞAMA 15 | **AŞAMA 16** | Progress / analytics / settings |
| AŞAMA 16 | **AŞAMA 17** | UI/UX polish / accessibility |
| AŞAMA 17 | **AŞAMA 18** | Pilot / calibration / QA |
| AŞAMA 18 | **AŞAMA 19** | Release APK |
| AŞAMA 19 | **AŞAMA 20** | Full professional curriculum / career / capstones |

## 2. Alt-adım referansları

Birçok eski future reference aynı harf korunarak bir stage ileri taşınır, fakat **harfi kör biçimde çevirmek yasaktır**. D-044 sonrası bazı future-stage checklist'leri genişletildi/değiştirildi. Bu nedenle semantik iş adı `EXECUTION_INDEX.md` üzerinden doğrulanmalıdır.

Bilinen güvenli örnekler:

```text
old 6C English daily/cadence      -> current 7C
old 7E progress/skill UX          -> current 8E
old 8C architecture/data schema   -> current 9C
old 8F test strategy              -> current 9F
old 9C design-system/mobile step  -> current 10C   # yalnız eski anlam gerçekten buysa
old 10x daily-flow implementation -> current 11x
old 11F planner virtual tests     -> current 12F
old 12x assessment implementation -> current 13x
old 13F open-answer evaluation    -> current 14F
old 14x first-content production  -> current 15x
old 15x analytics/settings        -> current 16x
old 16x polish/accessibility      -> current 17x
old 17B planner pilot observation -> current 18B
old 17C mastery calibration       -> current 18C
old 17E technical/performance QA  -> current 18E
old 18x release                   -> current 19x
old 19x professional curriculum   -> current 20x
```

Bir referansın semantiği tabloda açık değilse **otomatik string replacement yapılmaz**; current plan içinde iş adı aranır.

## 3. Living/current dosya kuralı

Aşağıdaki yaşayan dosyalarda legacy numara bırakılmaz:
- `PROJECT_CONTEXT.md`
- `docs/START_HERE.md`
- `docs/HANDOFF_STATE.md`
- `docs/STEP_STATUS.md`
- `docs/EXECUTION_INDEX.md`
- `docs/MASTER_PLAN.md`
- `README.md` içinde varsa current-navigation ifadeleri

Bunlar daima current canonical stage numarasını kullanmalıdır.

## 4. Stable spec / historical research kuralı

D-044 öncesinde tamamlanmış stable spec veya research validation dosyasında bir future-stage referansı bulunursa iki güvenli seçenek vardır:

1. Dosya davranışını değiştirmeden yalnız future-reference current numaraya düzeltilir, **veya**
2. Geçmiş provenance'ı korumak daha doğruysa legacy referans tarihsel bırakılır ve bu map üzerinden yorumlanır.

Historical bir metnin geçmişte gerçekten kullanılan step numarasını anlattığı durumlarda sessiz rewrite yapılmaz.

Önemli ayrım:

```text
historical completed-step identity != stale future-stage pointer
```

Örneğin `3H` her zaman 3H olarak kalır. Fakat 3H dosyasındaki o tarihte gelecek için yazılmış `11F planner testleri` pointer'ı bugün current plan karşılığı olan `12F` olarak yorumlanır.

## 5. D-050 repo hygiene kullanımı

Her POST-STEP repo-wide stale-reference kontrolünde:
- değişen stage/adım isimleri aranır,
- yaşayan dosyalar literal olarak düzeltilir,
- stable/historical belgelerdeki legacy future-pointer'lar bu map ile sınıflandırılır,
- belirsiz mapping varsa `EXECUTION_INDEX` semantiği esas alınır.

Bu map, stale referansları görmezden gelme izni değildir; **yanlış toplu renumber yapmadan güvenli cleanup yapma aracıdır.**

## 6. Güncel execution notu

Bu dosyanın oluşturulduğu anda canonical durum:
- 1–4 ✅
- 5A ✅ PDM-v0 / D-049
- **5B 🟡 Graph / Topic metadata sözleşmesi — aktif, henüz yürütülmedi**

Current state değiştiğinde bu bölüm source-of-truth olarak kullanılmaz; `STEP_STATUS.md` ve `HANDOFF_STATE.md` okunur.