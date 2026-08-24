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
| **2B — Topic durumları** | ✅ Tamamlandı | `locked / available / learning / mastered / weakening / remediation_required` state machine ve geçişleri `docs/TOPIC_STATE_MACHINE.md` içinde kilitlendi. |
| **2C — Mastery sinyalleri** | 🟡 Aktif | Teori, coding, debugging, explanation, transfer, retention, proje ve süre evidence türlerinin neyi kanıtladığı ve neyi tek başına kanıtlayamayacağı tasarlanacak. |
| **2D — AI/ipucu etkisi** | ⬜ Bekliyor | Hint ve AI-assisted task etkisi. |
| **2E — Mastery formülü v0** | ⬜ Bekliyor | Ağırlık, threshold, minimum evidence ve confidence. |
| **2F — Unutma modeli** | ⬜ Bekliyor | Retention, interval ve decay. |
| **3A ve sonrası** | ⬜ Bekliyor | Aşama 2 tamamlandıktan sonra ilerleyecek. |

## Son tamamlanan adım

### 2B — Topic durumları

**Tamamlanma tarihi:** 2026-08-24  
**Ana çıktı:** `docs/TOPIC_STATE_MACHINE.md`

**Kilitleyen kararlar:**

- Topic state mastery'nin kendisi değil; prerequisite, coverage, Skill mastery, retention ve remediation'dan türetilen planner/UX state'idir.
- Canonical state'ler: `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.
- `locked` esas olarak henüz başlanmamış Topic'in hard prerequisite giriş kapısıdır.
- Başlanmış/mastered Topic prerequisite sonradan zayıfladı diye geriye dönük `locked` yapılmaz.
- `mastered` için coverage/validated waiver + required Skill mastery gate gerekir; task/lesson completion yetmez.
- `weakening` daha önce öğrenilmiş Topic'te retention riskini; `remediation_required` hedefli müdahale gerektiren doğrulanmış Skill eksikliğini ifade eder.
- Remediation coverage'ı sıfırlamaz ve bütün curriculum'u durdurmaz.
- Topic state ileri Topic'leri tek başına kilitlemez; canonical prerequisite kararı Skill mastery üzerinden çalışır.
- State transition'lar reason/history ile açıklanabilir olmalıdır.

## Aktif adım

### 2C — Mastery sinyalleri

Şimdi her evidence türünün gerçek öğrenme hakkında ne kadar ve hangi yönden bilgi verdiği tanımlanacak. Amaç 2E'deki formülü yazmadan önce `quiz doğru = öğrendi` gibi yanlış pozitifleri yapısal olarak engelleyecek evidence modelini kurmaktır.
