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
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ Tamamlandı | `1A–1D` tamamlandı. Product requirements, V1 scope, success criteria ve non-goals kilitli. |
| **2A — Bilgi birimleri** | ✅ Tamamlandı | `Domain → Module → Topic → Skill → Learning Objective` modeli `docs/LEARNING_ENGINE_SPEC.md` içinde kilitlendi. |
| **2B — Topic durumları** | ✅ Tamamlandı | State machine `docs/TOPIC_STATE_MACHINE.md` içinde kilitlendi. |
| **2C — Mastery sinyalleri** | ✅ Tamamlandı | Evidence taxonomy ve false-positive guardrail'leri `docs/MASTERY_SIGNALS_SPEC.md` içinde kilitlendi. |
| **2D — AI/ipucu etkisi** | ✅ Tamamlandı | H0–H4 yardım seviyeleri, timing, artifact authorship, solution exposure, recheck ve objective-specific tool policy `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` içinde kilitlendi. |
| **2E — Mastery formülü v0** | 🟡 Aktif | Evidence aggregation, threshold, minimum independent/diverse evidence, assistance etkisi ve confidence araştırılıp deterministik v0 formülü tasarlanacak. |
| **2F — Unutma modeli** | ⬜ Bekliyor | Retention, interval ve decay. |
| **3A ve sonrası** | ⬜ Bekliyor | Aşama 2 tamamlandıktan sonra ilerleyecek. |

## Son tamamlanan adım

### 2D — AI / ipucu etkisi

**Tamamlanma tarihi:** 2026-08-24  
**Ana çıktı:** `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`

**Kilitleyen kararlar:**

- Assistance content H0–H4 olarak sınıflandırıldı.
- Yardım timing'i ve artifact authorship ayrı metadata olarak tutulacak.
- `independent_evidence`, `assisted_evidence`, `practice_only`, `requires_independent_recheck` yorum sınıfları tanımlandı.
- AI-generated/copied kodun çalışması kullanıcı için direct coding mastery evidence değildir.
- Full/partial solution exposure sonrası fresh/unseen independent recheck gerekir.
- Submit sonrası AI feedback önceki tamamlanmış attempt'i geriye dönük kirletmez.
- Explanation/comprehension, coding production objective'inin yerine geçmez.
- Compiler/test/docs/autocomplete kullanımı objective-specific allowed-tools policy'ye bağlıdır; otomatik penalty değildir.
- Hint istemek tek başına negative mastery değildir.
- External AI için surveillance/cheat-detection yaklaşımı kullanılmayacak.
- AI helper provenance ile AI evaluator provenance ayrıldı.
- Sayısal yardım etkisi 2E'ye bırakıldı.

## Aktif adım

### 2E — Mastery formülü v0

2E'de artık nitel evidence modelini deterministik hesaplama/gate modeline çevireceğiz. Bu adım öğrenme bilimi ve mastery model karşılaştırması gerektirdiği için `AI_AGENT_WORKFLOW.md` uyarınca Research AI kullanılacak; araştırma sonucu doğrudan karar değil, ana yöneticinin spec girdisi olacak.

**2E başlamadan önce `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.**