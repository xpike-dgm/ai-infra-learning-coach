# Product Vision

## Ürün Tanımı

AI Infra Learning Coach, kişisel kullanım için tasarlanmış adaptif mobil öğrenme uygulamasıdır. Kullanıcıya uzun bir kurs kataloğu sunmak yerine, o gün ne çalışması gerektiğini current Skill/mastery/retention/prerequisite state'ine göre seçer, çalıştırır, ölçer ve sonraki planı evidence'a göre yeniden oluşturur.

## Uzun Vadeli Vizyon — D-041

Ürün yalnız temel bir roadmap veya birkaç yıllık başlangıç koçu değildir.

> **Nihai hedef, sıfırdan başlayan kullanıcıyı gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir curriculum ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability seviyesine taşımaktır.**

4+ yıl bir countdown değildir. Final readiness zamanla değil mastery, retention, debugging, transfer, performance ve integrated project/capstone evidence ile belirlenir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## Ana Kullanıcı Sorusu

Uygulama her açıldığında öncelikle şu soruya cevap vermelidir:

> **Bugün ne yapmalıyım?**

## Ana Ekran

Uzun vadeli gün sayacı yerine:
- bugünkü toplam çalışma süresi,
- devam eden ana Topic/Skill,
- discrete learning state,
- bugünkü görev listesi,
- yaklaşan assessment/retention,
- gerektiğinde `neden bugün?` açıklaması

gösterilir.

Örnek:
- `C · Bellek ve Pointerlar`
- `Pointer Dereference — Doğrulama Bekliyor`
- `15 dk remediation`
- `12 dk fresh coding check`
- `20 dk English`

UI `Mastery %64 / hedef %80` gibi score'u gerçek öğrenme yüzdesiymiş gibi sunmak zorunda değildir; GRE-v0 internal operational score olabilir, kullanıcıya semantik state tercih edilir.

## Navigasyon

İlk tasarım için olası sade alt menü:
1. **Bugün**
2. **Yol Haritası / Knowledge Graph**
3. **İlerleme**
4. **Sınavlar**
5. **Ayarlar**

Exact navigation 7A'da kesinleşir.

## Bugün Ekranı

Büyük `Çalışmaya Başla / Devam Et` CTA'sı bulunur. Kullanıcının `hangi dersi seçeyim?` karar yükü minimuma indirilir.

## Yol Haritası

Timeline değil knowledge graph gösterilir.

Örnek üst seviye rota:
- Computer Fundamentals
- C
- Linux
- Modern C++
- Computer Architecture / OS / Memory
- Concurrency
- Networking
- Distributed Systems
- Performance Engineering
- GPU Architecture
- CUDA
- Triton
- LLM Inference
- Multi-GPU / AI Infrastructure
- Open Source / Professional Readiness

Kilitler tarihe göre değil canonical prerequisite/readiness durumuna göre oluşur.

## İlerleme Ekranı

Gösterilebilecek anlamlı ilerleme türleri:
- mastered / learning / verification / remediation state'leri,
- required Skill coverage,
- retention due/risk state,
- assessment evidence,
- coding/debugging/transfer capability,
- integrated project/capstone evidence,
- Technical English capability,
- güçlü/zayıf alanlar,
- çalışma süresi trendi — ikincil metrik.

`Kariyerin %12 tamamlandı` veya `Gün X / 1460` gibi sahte kesinlik kullanılmaz.

Uzun vadede professional-readiness dimensions gösterilebilir; exact UX 7E/15C'de tasarlanır.

## Sınavlar Ekranı

- Daily micro assessment history,
- weekly assessment,
- monthly comprehensive assessment,
- verification/recheck,
- retention checks,
- programı değiştiren sonuçların açıklaması.

Daily assessment DMA-v0'a göre her gün zorunlu quiz değildir.

## Öğrenme Oturumu Deneyimi

Bir oturum yalnız content consumption değildir. Görev türüne göre:

```text
conceptual model
→ example / guided practice
→ independent attempt
→ feedback / error analysis
→ explanation
→ fresh verification / transfer
```

Uzun curriculum'da kritik Skills ileride debugging, delayed retention, integrated project ve performance/production context ile yeniden kullanılır.

## AI Tutor Davranışı

AI:
- seviyeye göre açıklar,
- hint verir,
- root cause analiz eder,
- farklı örnek/remediation üretir,
- code/open answer feedback verir,
- AI-assisted artifact sonrası comprehension/transfer kontrolü üretir.

AI tek başına mastery/prerequisite/planner state yazamaz. H1–H4 assisted performance positive independent mastery değildir.

## Professional Öğretim Derinliği

D-041 sonrası kapsam yalnız daha fazla Topic değildir. Kritik technical domains için hedef progression:

`concept → guided → independent → debugging → explanation → transfer → retention → integrated project → performance/production context`

Uzun rota Git/testing/build/debug/profiling/design docs/observability/open-source workflow gibi professional engineering practices'i de kapsar.

## V1 ile Full Curriculum Ayrımı

V1, full 4+ year content bitmeden release edilir.

V1 hedefi:
- gerçek adaptive learning engine,
- assessment/mastery/prerequisite/planner,
- retention/remediation,
- AI Tutor,
- parallel English,
- ilk 8–12 haftalık production-quality curriculum,
- güvenilir Android release.

Full professional curriculum aynı engine üzerinde yıllar içinde genişler.

## Kişisel Kullanım Nedeniyle Bilerek Eklenmeyecekler

- çok kullanıcılı SaaS ihtiyaçları,
- social feed/friends/leaderboard,
- payment/subscription/admin,
- gereksiz enterprise role system.

## Tasarım Dili

- modern ve sade,
- profesyonel,
- iyi typography/spacing,
- dark/light,
- anlamlı visual hierarchy,
- no fake gamification pressure,
- başarısızlığı ceza değil learning signal olarak gösterme,
- D-028 gereği akıcı/performance-first mobil deneyim.

## Başarı Tanımı

Ürün başarılıysa kullanıcı:
- bugün ne çalışacağını planlamak zorunda kalmaz,
- eksikleri state/evidence ile görünür olur,
- untaught prerequisite yüzünden cezalandırılmaz,
- critical Skill kanıtlanmadan dependent work'a kör geçmez,
- retention ve remediation doğru zamanda gelir,
- AI yardımına rağmen independent ability ayrı ölçülür,
- yıllar içinde C/C++/systems/GPU/inference tarafında gerçek engineering artifacts üretir,
- professional capstone ve portfolio seviyesinde kanıt biriktirir,
- AI Infrastructure / ML Systems / GPU Systems işlerine hazırlanabilecek teknik capability'ye yaklaşır.

Bu başarı job offer/seniority garantisi değildir; `docs/PROFESSIONAL_READINESS_TARGET.md` sınırları geçerlidir.
