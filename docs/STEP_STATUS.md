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
| **2C — Mastery sinyalleri** | ✅ Tamamlandı | Evidence taxonomy, direct/corroborating/contextual roller, coding/debugging/explanation/transfer/retention/project sinyalleri ve false-positive guardrail'leri `docs/MASTERY_SIGNALS_SPEC.md` içinde kilitlendi. |
| **2D — AI/ipucu etkisi** | 🟡 Aktif | Hint seviyeleri, AI-assisted cevap/kod, copy/paste, yardım sonrası comprehension/transfer recheck ve assisted evidence güveni tasarlanacak. |
| **2E — Mastery formülü v0** | ⬜ Bekliyor | Ağırlık, threshold, minimum evidence ve confidence. |
| **2F — Unutma modeli** | ⬜ Bekliyor | Retention, interval ve decay. |
| **3A ve sonrası** | ⬜ Bekliyor | Aşama 2 tamamlandıktan sonra ilerleyecek. |

## Son tamamlanan adım

### 2C — Mastery sinyalleri

**Tamamlanma tarihi:** 2026-08-24  
**Ana çıktı:** `docs/MASTERY_SIGNALS_SPEC.md`

**Kilitleyen kararlar:**

- Mastery tek quiz/puan değil, Objective/Skill'e bağlı çok kaynaklı evidence bütünüdür.
- Evidence rolleri `direct/primary`, `corroborating`, `contextual` olarak ayrıldı.
- Concept recognition, recall, code reading, coding, debugging, explanation, transfer, retention ve integrated project evidence türleri ayrı tanımlandı.
- Coding evidence kullanıcının gerçek kod artifact'ı üretmesini gerektirir; doğru kod seçeneğini işaretlemek coding evidence değildir.
- Transfer yalnız daha önce öğrenilmiş prerequisite'lerle geçerli evidence sayılır.
- Retention immediate başarıdan ayrıdır ve gecikmeli/doğal yeniden kullanım evidence'ı olabilir.
- Project completion projedeki tüm Skill'leri otomatik mastered yapmaz; evidence objective bazında ayrıştırılır.
- Time, lesson/task completion, streak ve self-confidence mastery'nin kendisi değildir.
- Aynı soru/familya tekrarları bağımsız evidence gibi mastery'yi şişiremez.
- Yanlış/ambiguous veya bilinmeyen prerequisite içeren item'lardan gelen evidence geçersiz sayılabilir ve kullanıcıyı cezalandırmaz.
- Assistance context evidence ile birlikte tutulur; exact AI/hint etkisi 2D'ye bırakıldı.
- Weight, threshold, minimum evidence ve confidence 2E'ye bırakıldı.

## Aktif adım

### 2D — AI / ipucu etkisi

Bir sonraki tasarım, kullanıcının aldığı yardımın evidence yorumunu nasıl değiştirdiğini kesinleştirecek. AI/hint kullanımı yasaklanmayacak; fakat AI tarafından üretilmiş cevap/kod, kullanıcının bağımsız mastery kanıtı gibi değerlendirilmeyecek. Exact yardım seviyeleri ve comprehension/transfer recheck kuralları 2D'de tasarlanacak.

**2D başlamadan önce `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.**