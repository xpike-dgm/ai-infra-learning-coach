# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

Bu dosya yeni bir ChatGPT sohbetine veya başka bir agent'a geçerken mevcut çalışma durumunu hızlıca devretmek için tutulur.

**Son güncelleme:** 2026-08-24

Repo: `xpike-dgm/ai-infra-learning-coach`

---

# 1. Ana Ürün

> Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten; her gün ne çalışacağını mevcut bilgi durumuna göre belirleyen; yalnız kanıtlanmış öğrenmeyi ilerleme kabul eden; mastery, retention, assessment ve prerequisite sonuçlarına göre gelecekteki programı yeniden düzenleyen kişisel adaptif Android öğrenme koçu.

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Ana teknik yön:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

---

# 2. Kesinleşen Büyük Kurallar

- İngilizce A0'dan teknik eğitimle paralel ilerler.
- `Gün X / 1095` ve sahte kariyer yüzde ilerlemesi ana metrik değildir.
- Curriculum sabit takvim değil prerequisite ilişkili knowledge graph'tır.
- Eksik konu yalnız bağımlı dalları bekletir; bağımsız dallar devam eder.
- Planner mastery, retention, assessment, prerequisite ve günlük kapasiteye göre plan üretir.
- Günlük mikro assessment + haftalık + aylık sınav gelecekteki programı değiştirir.
- AI yardımı mümkündür; fakat AI ile tamamlanan iş gerçek anlama yerine geçmez.
- Uygulama kişisel kullanım içindir; auth/payment/social/admin/multi-tenant SaaS varsayılan kapsam dışıdır.
- Kritik geliştirme akışı: **Yönetici → gerekirse Araştırma AI → Spec → Kodlama AI → bağımsız Test/QA AI → PASS/FAIL → GitHub kaydı**.
- Proje Aşama 1–19 ve `1A / 1B / ...` sabit kodlarıyla yürütülür.
- Non-goals kapsam değişikliği sessizce yapılamaz; yeni decision kaydı gerekir.

---

# 3. Tamamlanan Aşamalar / Adımlar

## ✅ AŞAMA 1 — Ürün Çerçevesini Kilitle

- `1A` ✅ Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
- `1B` ✅ V1 kapsamı — `docs/V1_SCOPE.md`
- `1C` ✅ Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
- `1D` ✅ Non-goals — `docs/NON_GOALS.md`

Aşama 1 sonucu: ne inşa ettiğimiz, V1'in sınırı, ne zaman başarılı sayılacağı ve ne yapmayacağımız kilitlidir.

## ✅ 2A — Bilgi birimleri

Ana çıktı: `docs/LEARNING_ENGINE_SPEC.md`

Kilitleyen yapı:

`Domain → Module → Topic → Skill → Learning Objective`

Fakat model katı bir ağaç değildir.

### Curriculum organizasyon katmanı

`Domain → Module → Topic`

### Gerçek learning/mastery katmanı

`Skill → Learning Objective`

Bağlayıcı kararlar:

- Canonical mastery'nin ana planner/prerequisite seviyesi `Skill`.
- Evidence en atomik olarak `Learning Objective` seviyesine bağlanabilir.
- Topic/Module/Domain mastery Skill verilerinden derived edilir.
- Topic completion mastery değildir.
- Aynı Skill birden fazla Topic'te kullanılabilir; duplicate mastery yaratılmaz.
- Topic ↔ Skill many-to-many ilişki destekler.
- Runtime prerequisite ana olarak `Skill → Skill` çalışır.
- Cross-domain Skill dependency mümkündür.
- Technical English teknik programı yalnız gerçek dependency varsa hard-lock edebilir; global kapı değildir.
- Learning Objective gözlemlenebilir ve ölçülebilir eylem olarak yazılır.
- Task ve assessment yalnız Topic'e değil hedeflediği Skill/Objective'e bağlanmalıdır.

Kalıcı karar: `docs/DECISIONS.md` D-021.

---

# 4. Şu Anda Bulunulan Kesin Adım

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅ Bilgi birimleri
- `2B` 🟡 **Topic durumları — AKTİF**
- `2C` ⬜ Mastery sinyalleri
- `2D` ⬜ AI/ipucu etkisi
- `2E` ⬜ Mastery formülü v0
- `2F` ⬜ Unutma modeli

## Aktif iş: 2B

Topic state machine tasarlanacak.

Kesinleştirilmesi gerekenler:

- `locked`
- `available`
- `learning`
- `mastered`
- `weakening`
- `remediation_required`
- her state'in kesin anlamı,
- hangi olay/kanıt ile state değiştiği,
- coverage ile mastery'nin birbirine karışmaması,
- Skill mastery ile Topic state arasındaki ilişki,
- retention düşüşünün state'e etkisi,
- remediation sonrası geri dönüş yolları,
- planner'ın state'leri nasıl yorumlayacağı.

---

# 5. Hâlâ Açık Ana Konular

- topic state machine (`2B`)
- mastery evidence modeli (`2C`)
- AI-help impact (`2D`)
- mastery formülü / threshold / confidence (`2E`)
- spaced repetition / decay (`2F`)
- adaptive planner decision table
- assessment composition
- curriculum graph
- English mastery
- UX/wireframe
- mobil teknoloji seçimi
- local database
- AI provider architecture

---

# 6. İlk Okuma Sırası

1. `docs/START_HERE.md`
2. `docs/PROJECT_MASTER_CONTEXT.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `PROJECT_CONTEXT.md`
7. `docs/DECISIONS.md`
8. `docs/PRODUCT_REQUIREMENTS.md`
9. `docs/V1_SCOPE.md`
10. `docs/V1_SUCCESS_CRITERIA.md`
11. `docs/NON_GOALS.md`
12. `docs/LEARNING_ENGINE_SPEC.md`
13. `docs/MASTER_PLAN.md`
14. `docs/AI_AGENT_WORKFLOW.md`
15. `docs/PROGRESS_LOG.md`

---

# 7. Yeni Sohbetin Yapacağı İlk İş

Repo hafızasını okuduktan sonra doğrudan:

> **`2B — Topic durumları`**

adımından devam et.

Aşama 1 ve 2A kararlarını kullanıcı açıkça değiştirmedikçe yeniden tartışmaya açma.

---

# 8. Sohbet Aktarım Mesajı

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. docs/START_HERE.md dosyasından başlayıp belirtilen sırayı oku. Önceki sohbetin devamı gibi davran. STEP_STATUS.md ve HANDOFF_STATE.md içindeki aktif adım kodundan devam et. Daha önce alınmış kararları yeniden sordurma. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kararları ve tamamlanan adımları GitHub'a kaydet.`
