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
| **2E — Mastery formülü v0** | ✅ Tamamlandı | Beta-style weighted evidence accumulator + hard gates + confidence/verification modeli `docs/MASTERY_FORMULA_V0.md` içinde kilitlendi. |
| **2F — Unutma modeli** | 🟡 Aktif | Spaced repetition, retention interval, decay/weakening ve doğal reuse davranışı araştırılıp tasarlanacak. |
| **3A ve sonrası** | ⬜ Bekliyor | Aşama 2 tamamlandıktan sonra ilerleyecek. |

## Son tamamlanan adım

### 2E — Mastery formülü v0

**Tamamlanma tarihi:** 2026-08-24  
**Ana çıktı:** `docs/MASTERY_FORMULA_V0.md`

**Kilitleyen kararlar:**

- Mastery yalnız score değildir; `MasteryEvidenceScore + hard gates + confidence/verification` birlikte karar verir.
- Objective score için Beta-style accumulator kullanılır: `alpha = 1 + Σ(wq)`, `beta = 1 + Σ(w(1-q))`, `score = alpha/(alpha+beta)`.
- Operational v0 threshold `0.80`; bilimsel sabit veya “%80 öğrendi” anlamına gelmez ve pilotta kalibre edilir.
- `direct=1.0`, `corroborating=0.5`, contextual evidence score üretmez.
- H0–H4 assistance v0 katsayıları `1.00 / 0.85 / 0.65 / 0.35-or-0 / 0` olarak versionlanır.
- Difficulty'ye keyfi score multiplier verilmez; critical gate ve item eligibility için kullanılır.
- Same-item/same-family tekrarları bağımsız evidence'ı şişiremez.
- Required/critical Objective'ler hard gate'tir; yüksek average kritik eksiği gizleyemez.
- Critical production Objective en az bir H0 user-authored direct artifact gerektirir.
- Skill mastered için tüm required/critical Objective gate'leri geçmelidir.
- Tek yeni yanlış mastered Skill'i anında silmez; `verification_due` ile fresh doğrulama gerekir.
- Formula incremental hesaplamaya uygun tasarlandı; D-028 performans gereksinimi korundu.
- Numeric constants `mastery_formula_version=v0` olarak kalibre edilebilir konfigürasyondur.

## Aktif adım

### 2F — Unutma modeli

2F'de 2E'nin mastery state'i zaman ve gecikmeli retrieval ile birleştirilecek. Spaced repetition aralıkları, successful/failed delayed review, `mastered → weakening`, doğal reuse ve review scheduling için Research AI / dış araştırma kullanılacak.

**2F başlamadan önce `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.**