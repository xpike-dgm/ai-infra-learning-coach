# Non-Goals — AI Infra Learning Coach

**Adım:** 1D — Non-goals  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**D-041 notu:** Uzun vadeli curriculum 4+ year professional-readiness hedefiyle genişletildi; V1 scope-creep koruması aynen devam eder.

Bu belge projenin bilinçli olarak **ne olmayacağını** tanımlar. Amaç iyi fikirleri yasaklamak değil; ürünün ana amacını korumak, V1'i kontrolsüz büyütmemek ve gelecekte başka bir sohbet/agent tarafından kapsamın yanlışlıkla değiştirilmesini önlemektir.

Non-goal iki anlama gelebilir:
1. **Ürün seviyesi non-goal** — ürün kimliğiyle çelişir.
2. **V1 non-goal / deferred** — ileride yapılabilir ama ilk release şartı değildir.

---

# A. ÜRÜN SEVİYESİNDE NON-GOALS

## NG-01 — Zamanı ilerleme gibi göstermek
Ana ilerleme modeli olmayacak şeyler:
- `Gün X / toplam gün`,
- yalnız çalışma saati,
- yalnız ders completion yüzdesi,
- yalnız task checkbox sayısı,
- yalnız streak.

4+ year horizon da bu kuralı değiştirmez. **Takvim professional-readiness gate'i değildir.**

## NG-02 — Sabit takvimli kurs olmak
Curriculum ana yönü sabit olabilir; günlük task prerequisite, mastery, retention, assessment ve capacity'ye göre değişir.

## NG-03 — “Dersi bitirdi = öğrendi” sistemi olmak
Reading/video/task completion veya tek quiz critical Skill mastery üretmez.

## NG-04 — Tamamen LLM tarafından yönetilen curriculum olmak
LLM explanation/question/feedback üretebilir; prerequisite, mastery, curriculum veya planner'ın canonical kurallarını keyfi değiştiremez.

## NG-05 — AI'nın kullanıcı yerine öğrenmesi
AI-assisted artifact, Objective'e uygun independent evidence olmadan kullanıcı mastery/professional readiness yerine geçmez.

## NG-06 — Genel amaçlı “her şeyi öğreten” platform olmak
Ana rota AI Infrastructure / ML Systems / GPU Systems specialization'dır. Matematik/ML/DB/cloud gibi ek alanlar yalnız bu hedef için gerekli derinlikte dahil edilir.

## NG-07 — Sosyal ağ olmak
Friends/followers/feed/public profile/competitive leaderboard ürünün temel değeri değildir.

## NG-08 — Ticari SaaS ürünü olmak
Subscription/payment/billing/tenant/admin/RBAC vb. varsayılan kapsam dışıdır.

## NG-09 — Gamification'ı öğrenmenin önüne geçirmek
XP/coin/streak mastery'nin yerini alamaz.

## NG-10 — Kaçırılan günleri borç/ceza haline getirmek
Stale daily task backlog'u replay edilmez; current state'ten fresh plan üretilir.

## NG-11 — Telefonu tam geliştirme workstation'ına çevirmek
Mobil app tam IDE olmak zorunda değildir; gerçek coding gerektiğinde PC/editor/terminal üzerinden yapılabilir.

## NG-12 — Bilimsel olmayan sahte kesinlik üretmek
Mastery/probability/time-to-professional gibi kalibre edilmemiş sahte hassas metrikler sunulmaz.

## NG-13 — İş veya kariyer sonucu garanti etmek
Uygulama:
- iş teklifi,
- belirli maaş,
- seniority,
- belirli şirkete giriş,
- üniversite/degree isteyen HR filtrelerini aşma,
- “4 yılda kesin profesyonel olma”

garantisi vermez.

D-041 professional-readiness hedefi **teknik capability target**'ıdır; employment guarantee değildir.

## NG-14 — İngilizceyi teknik eğitimin önünde bariyer yapmak
English ve teknik eğitim paralel ilerler.

---

# B. V1 İÇİN NON-GOALS / SONRAYA BIRAKILANLAR

## NG-V1-01 — Tam 4+ yıllık professional curriculum'u release öncesi üretmek
V1 release için Modern C++ → Systems → Distributed → GPU/CUDA → Triton → Inference → Multi-GPU/AI Infrastructure → open source → professional capstone içeriğinin tamamını bitirmek **gerekmeyecektir**.

İlk production curriculum yaklaşık ilk 8–12 haftalık temel pakettir. Bu içerik büyüklüğüdür, kullanıcıya sabit takvim değildir.

Full target: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## NG-V1-02 — iOS, web ve desktop istemcileri
V1 Android odaklıdır.

## NG-V1-03 — Cloud account ve realtime multi-device sync
V1 local-first'tür. Backup/export/restore yine önemlidir.

## NG-V1-04 — Tam voice-first AI Tutor
Realtime pronunciation/voice-first tutor V1 şartı değildir.

## NG-V1-05 — Uygulama içi tam güvenli arbitrary code sandbox
Coding V1'in parçasıdır; execution yöntemi full embedded IDE/sandbox olmak zorunda değildir.

## NG-V1-06 — Canlı iş ilanı / career-market engine
Continuous job scraping, ATS optimization, company recommendation ve kapsamlı mock-interview platformu V1 şartı değildir; Aşama 19'da değerlendirilebilir.

## NG-V1-07 — Social/community
Forum/friends/public progress/leaderboard V1'de yoktur.

## NG-V1-08 — Ödeme/abonelik/admin
Kişisel kullanım nedeniyle V1 içermez.

## NG-V1-09 — Gelişmiş oyunlaştırma
Coin economy/avatar/shop/loot V1 şartı değildir.

## NG-V1-10 — Her learning-science modelini aynı anda kullanmak
Önce açıklanabilir/test edilebilir GRE/RVR/planner yaklaşımı; advanced modeller yalnız gerçek fayda gösterirse.

## NG-V1-11 — Gereksiz backend/DevOps karmaşıklığı
Kişisel local-first app için ilk günden Kubernetes/microservices/multi-region backend ürün mimarisi kurulmaz. Not: Kubernetes/cloud/observability **öğrenme curriculum'unda** AI Infrastructure için ileride öğretilebilir; bu madde uygulamanın kendi V1 backend mimarisiyle ilgilidir.

## NG-V1-12 — Sonsuz AI provider desteği
Provider abstraction olabilir; V1 her sağlayıcıyı desteklemek zorunda değildir.

---

# C. SCOPE CREEP KARAR KURALI

Yeni özellik için:
1. Ana vaadi doğrudan güçlendiriyor mu?
2. V1 acceptance için gerekli mi?
3. Olmadan ana learning loop bozuluyor mu?
4. Kritik adımları anlamlı biçimde geciktiriyor mu?
5. Bu belgede deferred mı?

Ana loop için gerekli değil ve V1'i geciktiriyorsa varsayılan **sonraya bırak**.

D-041 burada özel bir örnektir: **long-term curriculum hedefi büyütüldü fakat full content V1 içine taşınmadı.** Böylece ürün vizyonu genişlerken V1 scope patlamaz.

---

# 1D Kabul Kontrolü

1D'nin ana non-goal ilkeleri korunur. D-041 sonrası yalnız eski `3 yıllık curriculum'un tamamı V1 şartı değil` ifadesi **`tam 4+ yıllık professional curriculum V1 şartı değil`** olarak genişletilmiştir.

> **Kapsam güncelleme notu — 2026-08-24:** Professional-readiness hedefi ürün kapsamına alındı; job guarantee, time guarantee ve V1'e full curriculum yükleme hâlâ non-goal'dır.
