# Non-Goals — AI Infra Learning Coach

**Adım:** 1D — Non-goals  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Bağlayıcı:** D-041, D-042, D-044

Bu belge projenin bilinçli olarak **ne olmayacağını** tanımlar. Amaç ürünün ana amacını korumak ve V1 scope creep'i önlemektir.

Non-goal iki anlama gelebilir:
1. **Ürün seviyesi non-goal** — ürün kimliğiyle çelişir.
2. **V1 non-goal / deferred** — ileride yapılabilir ama ilk release şartı değildir.

---

# A. ÜRÜN SEVİYESİNDE NON-GOALS

## NG-01 — Zamanı ilerleme gibi göstermek
Ana ilerleme modeli olmayacak şeyler:
- `Gün X / toplam gün`,
- yalnız çalışma saati,
- yalnız lesson completion,
- yalnız task checkbox,
- yalnız streak.

4+ year horizon professional-readiness gate'i değildir.

## NG-02 — Sabit takvimli kurs olmak
Günlük task prerequisite, mastery, retention, assessment ve capacity'ye göre değişir.

## NG-03 — “Dersi bitirdi = öğrendi” sistemi olmak
Reading/video/task completion veya tek quiz critical Skill mastery üretmez.

## NG-04 — Tamamen LLM tarafından yönetilen curriculum olmak
LLM explanation/question/feedback üretebilir; canonical prerequisite/mastery/planner kurallarını keyfi değiştiremez.

## NG-05 — AI'nın kullanıcı yerine öğrenmesi
AI-assisted artifact independent evidence olmadan mastery/professional readiness yerine geçmez.

## NG-06 — Genel amaçlı “her şeyi öğreten” platform olmak
Ana rota AI Infrastructure / ML Systems / GPU Systems'tır. Ek alanlar hedef için gereken derinlikte dahil edilir.

## NG-07 — Sosyal ağ olmak
Friends/followers/feed/public profile/competitive leaderboard temel değer değildir.

## NG-08 — Ticari SaaS ürünü olmak
Subscription/payment/billing/tenant/admin/RBAC varsayılan kapsam dışıdır.

## NG-09 — Gamification'ı öğrenmenin önüne geçirmek
XP/coin/streak mastery'nin yerini alamaz.

## NG-10 — Kaçırılan günleri borç/ceza yapmak
Stale daily backlog replay edilmez; current state'ten fresh plan üretilir.

## NG-11 — Telefonu tam development workstation yapmak
Mobil app full IDE olmak zorunda değildir; coding gerektiğinde PC/editor/terminal kullanılabilir.

## NG-12 — Bilimsel olmayan sahte kesinlik üretmek
Kalibre edilmemiş mastery probability/time-to-professional gibi sahte hassas metrikler sunulmaz.

## NG-13 — İş veya kariyer sonucu garanti etmek
Uygulama job offer, belirli maaş, seniority, belirli şirkete giriş, degree/HR filtresini aşma veya “4 yılda kesin profesyonel olma” garantisi vermez.

## NG-14 — İngilizceyi teknik eğitimin önünde bariyer yapmak
English ve teknik eğitim paralel ilerler.

## NG-15 — Geniş domain etiketiyle gerçek zayıflığı gizlemek
D-044 sonrası yalnız `Python zayıf` gibi broad sonuç üretip hangi alt Skill'in sorunlu olduğunu saklamak hedef ürün davranışı değildir. Weakness mümkün olduğunca Skill/Objective seviyesinde lokalize edilir.

---

# B. V1 İÇİN NON-GOALS / SONRAYA BIRAKILANLAR

## NG-V1-01 — Tam 4+ yıllık professional curriculum'u release öncesi üretmek
V1 release için Modern C++ → Systems → Distributed → GPU/CUDA → Triton → Inference → Multi-GPU/AI Infrastructure → OSS → professional capstone content'inin tamamını bitirmek gerekmez.

İlk production curriculum yaklaşık ilk 8–12 haftalık temel pakettir. Full route taxonomy AŞAMA 6'da planlanır; full production expansion AŞAMA 20'dedir.

## NG-V1-02 — iOS, web ve desktop istemcileri
V1 Android odaklıdır.

## NG-V1-03 — Cloud account ve realtime multi-device sync
V1 local-first'tür. Backup/export/restore önemlidir.

## NG-V1-04 — Tam voice-first AI Tutor
V1 şartı değildir.

## NG-V1-05 — Uygulama içi full arbitrary-code sandbox
Coding V1'in parçasıdır; execution yöntemi tam embedded IDE/sandbox olmak zorunda değildir.

## NG-V1-06 — Canlı iş ilanı / career-market engine
Continuous job scraping, ATS optimization, company recommendation ve comprehensive mock-interview platformu V1 şartı değildir; **AŞAMA 20** career-readiness katmanında değerlendirilebilir.

## NG-V1-07 — Social/community
V1'de yoktur.

## NG-V1-08 — Ödeme/abonelik/admin
Kişisel kullanım nedeniyle V1 içermez.

## NG-V1-09 — Gelişmiş oyunlaştırma
Coin/avatar/shop/loot V1 şartı değildir.

## NG-V1-10 — Her learning-science modelini aynı anda kullanmak
Önce açıklanabilir/test edilebilir GRE/RVR/planner; advanced modeller yalnız gerçek fayda gösterirse.

## NG-V1-11 — Gereksiz backend/DevOps karmaşıklığı
Kişisel local-first app için ilk günden Kubernetes/microservices/multi-region backend kurulmaz. Bunlar curriculum'da ileride öğretilebilir; bu madde app'in kendi V1 mimarisiyle ilgilidir.

## NG-V1-12 — Sonsuz AI provider desteği
V1 her provider'ı desteklemek zorunda değildir.

---

# C. SCOPE CREEP KARAR KURALI

Yeni özellik için:
1. Ana vaadi doğrudan güçlendiriyor mu?
2. V1 acceptance için gerekli mi?
3. Olmadan ana learning loop bozuluyor mu?
4. Kritik adımları anlamlı biçimde geciktiriyor mu?
5. Bu belgede deferred mı?

Ana loop için gerekli değil ve V1'i geciktiriyorsa varsayılan **sonraya bırak**.

D-041 long-term target'ı büyütürken full content'i V1'e taşımadı. D-044 full route'u planlama seviyesinde granular hale getirir; bu da V1 öncesi yıllarca bütün lesson content'ini üretmek anlamına gelmez.

---

# 1D Kabul Kontrolü

1D'nin ana non-goal ilkeleri korunur. D-041/D-042/D-044 ile long-term route ve granularity netleşmiştir; job guarantee, time guarantee ve V1'e full curriculum yükleme hâlâ non-goal'dır.
