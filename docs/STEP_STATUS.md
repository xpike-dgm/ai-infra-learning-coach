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
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ Tamamlandı | `1A–1D` tamamlandı. |
| **2A — Bilgi birimleri** | ✅ Tamamlandı | `docs/LEARNING_ENGINE_SPEC.md`. |
| **2B — Topic durumları** | ✅ Tamamlandı | `docs/TOPIC_STATE_MACHINE.md`. |
| **2C — Mastery sinyalleri** | ✅ Tamamlandı | `docs/MASTERY_SIGNALS_SPEC.md`. |
| **2D — AI/ipucu etkisi** | ✅ Tamamlandı | `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`. |
| **2E — Mastery formülü v0** | ✅ Tamamlandı | Ayrı Research AI raporu değerlendirildi; candidate Beta model yerine `GRE-v0 — Gated Recent Evidence` finalleştirildi. `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md`. |
| **2F — Unutma modeli** | 🟡 Aktif | Spaced repetition, review interval, time/retention risk, weakening ve natural reuse araştırılıp tasarlanacak. |
| **3A ve sonrası** | ⬜ Bekliyor | Aşama 2 tamamlandıktan sonra. |

## Son tamamlanan adım — 2E

**Ana çıktılar:**
- `docs/MASTERY_FORMULA_V0.md`
- `docs/2E_RESEARCH_VALIDATION.md`

**Karar:** D-031. D-029 candidate modelinin yerine geçti.

### Final GRE-v0 özeti

- Mastery score'a yalnız valid + H0 + direct + verified + independent evidence group girer.
- H1–H4 assistance formative/remediation/recheck sinyalidir; positive independent mastery score değildir.
- Corroborating evidence direct gate'i ikame etmez.
- Same-family correlation testlet/dependency grouping ile kontrol edilir.
- Objective score: son en fazla 5 eligible independent H0 direct group'un `q_g` ortalaması.
- `0.80` threshold ve window `5` calibration öncesi engineering heuristic'tir.
- Standard Objective default: 2 independent group; critical: 3 independent group + 2 family/context + non-basic/objective-specific gate.
- Critical production: H0 user-authored artifact; critical debugging: H0 diagnosis/fix evidence.
- Skill mastered yalnız tüm required/critical Objective gate'leri PASS ise olur; compensatory average yok.
- İlk clean post-mastery negative → `verification_due`, instant reset yok.
- AI evaluator fixed `0.80` kaldırıldı; `verified | provisional | invalid` modeli getirildi.
- Difficulty numeric multiplier değil.
- Hesap bounded/incremental uygulanabilir; D-028 performans kuralı korunur.

## Aktif adım — 2F Unutma modeli

2F başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yapılacak ve ayrı Research AI turu kullanılacaktır.

Kesinleştirilecek:
- spaced repetition yaklaşımı,
- review interval başlangıcı/büyümesi,
- successful/failed delayed retrieval,
- time-based retention risk/decay,
- `mastered → weakening → mastered/remediation_required`,
- natural reuse'un retention evidence etkisi,
- GRE-v0 mastery state ile retention state entegrasyonu.
