# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki sabit adım kodlarının güncel durumunu hızlı takip etmek için tutulur. Ayrıntılı tanım `EXECUTION_INDEX.md`, tamamlanma gerekçeleri ilgili spec ve `PROGRESS_LOG.md` içindedir.

## Durum anahtarı

- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-24

| Adım | Durum | Açıklama |
|---|---|---|
| **1A — Ana ürün amacı** | ✅ Tamamlandı | Ürünün amacı ve ana ilkeleri `docs/PRODUCT_REQUIREMENTS.md` içinde kilitlendi. |
| **1B — V1 kapsamı** | ✅ Tamamlandı | V1 zorunlu yetenekleri ve deferred alanlar `docs/V1_SCOPE.md` içinde kilitlendi. |
| **1C — Başarı kriterleri** | ✅ Tamamlandı | 49 P0/P1/P2 acceptance kriteri `docs/V1_SUCCESS_CRITERIA.md` içinde tanımlandı. |
| **1D — Non-goals** | ✅ Tamamlandı | Ürün seviyesi non-goals ile V1'den ertelenen özellikler `docs/NON_GOALS.md` içinde konsolide edildi. |
| **2A — Bilgi birimleri** | 🟡 Aktif | Domain → Module → Topic → Skill → Learning Objective hiyerarşisi, her katmanın sorumluluğu ve ölçülebilir öğrenme hedefi standardı tasarlanacak. |
| **2B ve sonrası** | ⬜ Bekliyor | 2A tamamlandıktan sonra sırayla ilerleyecek. |

## Tamamlanan milestone

### AŞAMA 1 — Ürün Çerçevesini Kilitle ✅

**Tamamlanma tarihi:** 2026-08-24

Çıktılar:

- `docs/PRODUCT_REQUIREMENTS.md`
- `docs/V1_SCOPE.md`
- `docs/V1_SUCCESS_CRITERIA.md`
- `docs/NON_GOALS.md`

**Özet:** Ürünün amacı, V1 kapsamı, release başarı kriterleri ve kapsam dışı alanları artık ayrı ve kalıcı dokümanlarda kilitlidir.

## Aktif adım

### 2A — Bilgi birimleri

Bu adımda öğrenme sisteminin veri/pedagoji omurgası kurulacak:

`Domain → Module → Topic → Skill → Learning Objective`

Amaç yalnız isim vermek değil; her katmanın neyi temsil ettiğini, prerequisite ve mastery'nin hangi seviyede tutulacağını ve öğrenme hedeflerinin nasıl ölçülebilir yazılacağını kesinleştirmektir.
