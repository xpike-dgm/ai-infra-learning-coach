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
| **2A — Bilgi birimleri** | ✅ Tamamlandı | `Domain → Module → Topic → Skill → Learning Objective` modeli `docs/LEARNING_ENGINE_SPEC.md` içinde kilitlendi. Curriculum organizasyonu ile gerçek learning/mastery katmanı ayrıldı. |
| **2B — Topic durumları** | 🟡 Aktif | Topic state machine; locked/available/learning/mastered/weakening/remediation_required durumları ve geçişleri tasarlanacak. |
| **2C — Mastery sinyalleri** | ⬜ Bekliyor | 2B sonrası teori/coding/debugging/explanation/transfer/retention evidence modeli tasarlanacak. |
| **2D — AI/ipucu etkisi** | ⬜ Bekliyor | Hint ve AI-assisted task etkisi. |
| **2E — Mastery formülü v0** | ⬜ Bekliyor | Ağırlık, threshold, minimum evidence ve confidence. |
| **2F — Unutma modeli** | ⬜ Bekliyor | Retention, interval ve decay. |
| **3A ve sonrası** | ⬜ Bekliyor | Aşama 2 tamamlandıktan sonra ilerleyecek. |

## Son tamamlanan adım

### 2A — Bilgi birimleri

**Tamamlanma tarihi:** 2026-08-24  
**Ana çıktı:** `docs/LEARNING_ENGINE_SPEC.md`

**Kilitleyen kararlar:**

- `Domain → Module → Topic` curriculum organizasyon katmanıdır.
- `Skill → Learning Objective` gerçek öğrenme ve ölçüm katmanıdır.
- Canonical mastery'nin ana planner/prerequisite seviyesi `Skill`'dir.
- Evidence en atomik olarak `Learning Objective` seviyesine bağlanabilir.
- Topic/Module/Domain mastery doğrudan bağımsız gerçeklik değil, Skill verilerinden türetilen derived görünümlerdir.
- Aynı Skill birden fazla Topic içinde teach/practice/assess/reinforce rolüyle kullanılabilir; duplicate mastery yaratılmaz.
- Runtime prerequisite ana olarak `Skill → Skill` çalışır.
- Cross-domain bağlantılar mümkündür; Technical English varsayılan olarak teknik ilerlemeyi global hard-lock etmez.
- Learning Objective gözlemlenebilir ve ölçülebilir eylem olarak yazılmalıdır; `oku/izle/tamamla` tek başına objective değildir.

## Aktif adım

### 2B — Topic durumları

Şimdi Topic'in `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required` gibi durumlarının kesin anlamı ve state transition kuralları oluşturulacaktır. Topic state, coverage ile mastery'nin birbirine karıştırılmasına izin vermeyecek şekilde tasarlanacaktır.
