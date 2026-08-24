# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki sabit adım kodlarının güncel durumunu hızlı takip etmek için tutulur.

## Durum anahtarı
- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-24

| Adım | Durum | Açıklama |
|---|---|---|
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ | `1A–1D` tamamlandı. |
| **AŞAMA 2 — Öğrenme ve Mastery Modeli** | ✅ | `2A–2F` tamamlandı. GRE-v0 + RVR-v0 canonical. |
| **3A — Günlük kapasite** | ✅ | Hard daily budget, editable presets, reserve/min-block, no auto-overrun, dynamic remaining-time replan, no backlog debt. `docs/ADAPTIVE_PLANNER_SPEC.md`, D-033. |
| **3B — Görev kategorileri** | 🟡 Aktif | Planner'ın üretebileceği task taxonomy ve ortak task contract tasarlanacak. |
| **3C ve sonrası** | ⬜ Bekliyor | 3B kapanışından sonra. |

## Son tamamlanan adım — 3A

Ana çıktı:
- `docs/ADAPTIVE_PLANNER_SPEC.md` — 3A bölümü
- D-033

### 3A final capacity özeti
- Kullanıcının explicit günlük süresi hard budget.
- V0 editable short/normal/intensive presetler: 30/60/90 dk; bilimsel optimum değil.
- V0 10% planning reserve ve 10 dk minimum plannable block engineering heuristic.
- Fixed kategori yüzdeleri yok.
- Remediation/retention ortaya çıkınca gün uzamaz; kalan budget replan edilir.
- Time override session ortasında da yapılabilir.
- Unfinished/planned-but-not-started task negative evidence değildir.
- Task sığmazsa safe split → smaller eligible alternative → defer.
- Deferred task ertesi gün borç kuyruğu değildir.
- Duration estimates future user pace adaptation destekler.
- Wall-clock ve active-learning süreleri ayrılabilir.
- Capacity calculation deterministic/versioned ve LLM'den bağımsız.

## Aktif adım — 3B Görev kategorileri

3B başlamadan `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacak.

3B'de kesinleştirilecek:
- canonical task categories,
- teach / practice / assessment / coding / debugging / retention / remediation / English / project gibi kategorilerin ayrımı,
- bir task'ın primary purpose ile evidence type ayrımı,
- task metadata/contract,
- splittable / duration / prerequisite / target Skill-Objective alanları,
- multi-Skill/integrated task attribution sınırları,
- 3C priority'nin kullanacağı task candidate primitive.
