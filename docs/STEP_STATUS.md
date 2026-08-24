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
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ Tamamlandı | `1A–1D` tamamlandı. |
| **2A — Bilgi birimleri** | ✅ Tamamlandı | Canonical learning-unit modeli `docs/LEARNING_ENGINE_SPEC.md`. |
| **2B — Topic durumları** | ✅ Tamamlandı | State machine `docs/TOPIC_STATE_MACHINE.md`. |
| **2C — Mastery sinyalleri** | ✅ Tamamlandı | Evidence taxonomy `docs/MASTERY_SIGNALS_SPEC.md`. |
| **2D — AI/ipucu etkisi** | ✅ Tamamlandı | H0–H4 yardım/evidence davranışı `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`. |
| **2E — Mastery formülü v0** | 🟡 Aktif | Manager tarafından candidate formula yazıldı; ayrı Research AI doğrulaması henüz yapılmadığı için adım yeniden açıldı. Research raporu değerlendirilip gerekli revizyonlar yapılmadan tamamlanmış sayılmayacak. |
| **2F — Unutma modeli** | ⬜ Bekliyor | 2E Research AI doğrulaması ve kapanışı tamamlandıktan sonra başlayacak. |
| **3A ve sonrası** | ⬜ Bekliyor | Aşama 2 tamamlandıktan sonra ilerleyecek. |

## Son tamamlanan adım

### 2D — AI / ipucu etkisi

**Ana çıktı:** `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`

## Aktif adım

### 2E — Mastery formülü v0 — Research AI doğrulaması

`docs/MASTERY_FORMULA_V0.md` şu anda **candidate/draft v0** olarak ele alınacaktır. Önceki kapanışta ayrı Research AI turu yapılmış gibi yazılması doğru değildi; yapılan şey ana yöneticinin kendi dış/web araştırmasıydı.

2E kapanmadan önce ayrı Research AI raporu şu başlıkları doğrulamalıdır:

- Beta-style accumulator seçiminin uygunluğu ve alternatifleri,
- `0.80` operational threshold'un riskleri,
- direct/corroborating evidence ayrımı,
- H0–H4 assistance katsayılarının savunulabilirliği,
- minimum independent/diverse evidence gate'leri,
- critical production için bağımsız artifact şartı,
- negative evidence / hysteresis / verification_due davranışı,
- BKT / IRT / mastery-learning yaklaşımlarıyla karşılaştırma,
- false-positive / false-negative riskleri,
- pilotta hangi parametrelerin kalibre edilmesi gerektiği.

Research AI raporu ana yönetici tarafından doğrudan kabul edilmeyecek; mevcut 2A–2D bağlayıcı kararlarla karşılaştırılıp `MASTERY_FORMULA_V0.md` revize edilecek. Ardından POST-STEP senkronizasyonu yapılıp 2F aktif hale getirilecek.
