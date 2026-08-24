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
| **1A — Ana ürün amacı** | ✅ Tamamlandı | Ürünün amacı, günlük değer önerisi ve ana ürün ilkeleri `docs/PRODUCT_REQUIREMENTS.md` içinde kilitlendi. |
| **1B — V1 kapsamı** | ✅ Tamamlandı | V1'in zorunlu yetenekleri ve bilinçli olarak sonraya bırakılan alanlar `docs/V1_SCOPE.md` içinde kilitlendi. |
| **1C — Başarı kriterleri** | ✅ Tamamlandı | Daily planner, mastery, prerequisite, assessment, replan, retention, remediation, AI Tutor, English, persistence ve release için P0/P1/P2 acceptance kriterleri `docs/V1_SUCCESS_CRITERIA.md` içinde kilitlendi. |
| **1D — Non-goals** | 🟡 Aktif | Scope creep'i engellemek için projenin ve V1'in özellikle ne olmayacağı tek listede konsolide edilip kilitlenecek. |
| **2A ve sonrası** | ⬜ Bekliyor | Aşama 1 tamamlanmadan başlanmayacak. |

## Son tamamlanan adım

### 1C — Başarı kriterleri

**Tamamlanma tarihi:** 2026-08-24

**Ana çıktı:** `docs/V1_SUCCESS_CRITERIA.md`

**Özet:** V1 için 49 numaralı acceptance kriteri oluşturuldu. Release kapısı; tüm P0 testlerinin PASS olması, bağımsız QA doğrulaması, veri kaybı olmaması, prerequisite/mastery/planner çekirdek kurallarının ihlal edilmemesi ve gerçek Android cihaz/pilot doğrulaması olarak tanımlandı. Mastery threshold gibi henüz araştırılması gereken sayısal parametreler bilinçli olarak sonraki ilgili aşamalara bırakıldı.

## Aktif adım

### 1D — Non-goals

Bu adımda yapılacak iş: V1 ve projenin özellikle ne olmaya çalışmadığını kalıcı biçimde tanımlamak; gereksiz SaaS, sosyal, gamification, platform, IDE/compiler, tüm 3 yıllık içeriği baştan üretme ve benzeri scope creep alanlarını tek listede kilitlemek. 1D tamamlanınca Aşama 1 tamamlanacaktır.
