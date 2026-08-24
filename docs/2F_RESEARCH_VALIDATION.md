# 2F Research Validation — Retention / Forgetting / Spaced Repetition

**Adım:** 2F  
**Tarih:** 2026-08-24  
**Durum:** TAMAMLANDI — Research AI raporu yönetici tarafından doğrulandı ve ürün kararına sentezlendi.

Bu belge kullanıcının sağladığı bağımsız Research AI raporunun hangi bölümlerinin kabul edildiğini, hangilerinin fazla güçlü veya kalibrasyonsuz bulunduğunu ve final `RETENTION_FORGETTING_SPEC.md` kararlarına nasıl çevrildiğini kaydeder.

## 1. Research AI raporundan güçlü biçimde kabul edilen yönler

- Spacing/distributed practice uzun vadeli retention için massed practice'e göre genelde daha iyidir.
- Retrieval practice pasif restudy'den daha güçlü retention kanıtı/öğrenme olayıdır.
- `mastery` ile zamansal `retention/retrievability` aynı tek score içinde eritilmemelidir.
- Zaman geçmesi tek başına negatif performance evidence değildir; GRE-v0 mastery score'u sırf gün geçti diye düşürülmemelidir.
- Post-mastery tek clean failure instant reset yerine fresh verification gerektirmelidir.
- Flashcard/fact scheduler'ları coding/debugging/transfer gibi karmaşık production Skill'lerine körü körüne uygulanmamalıdır.
- Multi-skill görevler retention için değerli olabilir; fakat component Skill attribution ayrı doğrulanmalıdır.
- Exact item repeat gerçek Skill retention'ını fazla iyimser gösterebilir; farklı family/context tercih edilmelidir.
- Long absence sonrası backlog dump yerine güncel risk/prerequisite/capacity üzerinden representative verification yapılmalıdır.
- V1 local/deterministic/bounded/incremental tutulmalı; population-fit ağır model ilk sürüm zorunluluğu değildir.

## 2. Yönetici tarafından düzeltilen / yumuşatılan iddialar

### 2.1 `optimum spacing = final delay'in %10–20'si` evrensel değildir
Cepeda et al. 2008 optimal gap'in final test delay ile arttığını gösterir; oran kısa ve uzun retention horizonlarında aynı değildir. Tek bir yüzde formülü V1'e hard-code edilmeyecektir.

### 2.2 Expanding schedule her zaman üstün değildir
Spaced retrieval güçlüdür; ancak equal/expanding/contracting relative schedule arasında her durumda tek kazanan yoktur. V1 belirli bir expanding sequence'i `research-proven` diye sunmaz.

### 2.3 FSRS cold-start hakkında rapor fazla güçlüydü
Güncel FSRS-6 21 parametre kullanır. Kişisel history az olduğunda default parametrelerle çalışabilir; default'lar büyük flashcard review corpus'larından türetilmiştir. Bu yüzden `FSRS cold-start'ta çalışamaz` doğru değildir. Asıl sorun, bu parametrelerin bizim coding/debugging/transfer görevlerine domain-valid olduğunun kanıtlanmamış olmasıdır.

### 2.4 DAS3H `natural reuse = bütün alt Skills full refresh` kanıtlamaz
DAS3H item'ların birden fazla KC/Skill ile etiketlenmesini ve geçmiş practice/forgetting etkisini modellemeyi destekler. Ancak üst görev başarılı olduğu için tüm tagged Skills'in otomatik bağımsız mastery/retention kanıtı olduğu sonucu çıkmaz. D-025 korunur: `project/task success ≠ all component Skills mastered`.

Natural reuse yalnız hedef Skill görev için structurally essential ise, H0 ise, target davranış ayrı rubric/trace ile verified ise ve farklı yeterli context/family sağlıyorsa strong retention evidence olabilir.

### 2.5 `24–48 saat` hysteresis süresi research-supported sabit değildir
Tek failure sonrası fresh recheck fikri güçlüdür; fakat tam `24–48h` aralığının her Skill türü için bilimsel zorunluluk olduğuna yeterli temel yoktur. V0 `verification_delay_days = 1` kullanabilir fakat bu engineering heuristic olacaktır.

### 2.6 Exact interval/EF/max-overdue sayıları kalibrasyon ister
Beceri türüne özgü exact ilk aralıklar, `EF=1.6–2.2`, `90/180 gün`, `1.5x overdue` gibi sayılar evrensel research constant değildir. V1 için versioned başlangıç ayarı olabilir; 17C pilotunda kalibre edilmelidir.

### 2.7 Günlük `8–10 review` tavanı 2F kararı olmayacak
Daily review sayısı ayrı capacity/planner problemidir. 3A–3F günlük kapasite ve priority kuralları belirleyecektir. 2F yalnız retention task'larının backlog dump oluşturmaması gerektiğini söyler.

### 2.8 `cluster refresh propagation` reddedildi
Bir root Skill başarılı diye komşu/descendant Skills otomatik refresh almaz. Tek bir integrated task birden fazla Skill'e retention evidence üretebilir; fakat her Skill'in target davranışı task içinde gerçekten gerekli ve ayrı doğrulanabilir olmalıdır.

### 2.9 Confirmed forgetting'te GRE score elle `0.50` yapılmayacak
İkinci independent failure sonrası `score=0.50` gibi elle atama yapılmaz. Yeni H0 direct failures normal GRE-v0 evidence history'sine girer; current mastery gate doğal olarak yeniden hesaplanır. Hysteresis yalnız ilk contradiction'ın anında mastery silmesini önler.

## 3. Akademik / teknik doğrulama notları

- Cepeda et al. (2008), *Spacing effects in learning: a temporal ridgeline of optimal retention*, Psychological Science, DOI `10.1111/j.1467-9280.2008.02209.x`.
- Roediger & Butler (2011), *The critical role of retrieval practice in long-term retention*, Trends in Cognitive Sciences, DOI `10.1016/j.tics.2010.09.003`.
- Bjork & Bjork (1992), *A New Theory of Disuse and an Old Theory of Stimulus Fluctuation* — storage strength / retrieval strength ayrımı teorik referans olarak kullanılır; ürün state'i doğrudan bu teorinin ölçülmüş latent parametreleri olduğunu iddia etmez.
- Settles & Meeder (2016), *A Trainable Spaced Repetition Model for Language Learning*, ACL, DOI `10.18653/v1/P16-1174`; HLR language-learning data ile fit edilmiştir.
- Choffin et al. (2019), *DAS3H: Modeling Student Learning and Forgetting for Optimally Scheduling Distributed Practice of Skills*, EDM 2019; multi-skill + forgetting yaklaşımı için referans.
- Galyardt & Goldin (2014), *Recent-Performance Factors Analysis*, EDM; recent performance'ın predictive value'su için referans, ancak final retention scheduler'ımız fit edilmiş R-PFA modeli değildir.
- FSRS-6 güncel resmi algoritma dokümanı 21 parameter D/S/R formülünü ve personal history yetersizken default parameter kullanımını açıklar. V1'in complex-skill domain'i için doğrudan calibrated model olarak kabul edilmez.

## 4. Final ürün sentezi

2F final modeli tam SM-2, FSRS, HLR, ACT-R, DAS3H veya R-PFA implementasyonu değildir.

Final yaklaşım: **`RVR-v0 — Retention Verification & Risk`**.

- GRE-v0 current mastery ile retention scheduling ayrı tutulur.
- Time → evidence score decay değil `review_due` üretir.
- Gerçek Skill state yalnız yeni valid evidence ile değişir.
- Review task'ları active H0 retrieval/production/debugging/transfer kullanır.
- Natural reuse yalnız ayrı attribution gate'lerini geçerse planned review yerine geçer.
- İlk post-mastery failure → `verification_due`; fresh recheck.
- Recheck failure → GRE-v0 yeniden değerlendirme + targeted remediation.
- `review_due` tek başına Topic `weakening` veya prerequisite hard-block değildir.
- Critical prerequisite'te unresolved actual contradiction (`verification_due`) dependent new work'u bekletebilir.
- No automatic cluster refresh; no arbitrary score reset.
- Scheduling constants versioned engineering heuristics ve pilot-calibration girdisidir.

Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md`.
