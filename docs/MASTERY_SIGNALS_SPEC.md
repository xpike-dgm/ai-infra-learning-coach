# Mastery Signals Specification — AI Infra Learning Coach

**Adım:** 2C — Mastery sinyalleri  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge kullanıcının bir Skill / Learning Objective'i gerçekten öğrendiğine dair hangi evidence türlerinin toplanacağını, her evidence türünün neyi kanıtladığını, neyi tek başına kanıtlayamayacağını ve mastery hesabına girmeden önce hangi kalite koşullarının aranacağını tanımlar.

Bu belge şu kaynaklarla birlikte bağlayıcıdır:

- `docs/LEARNING_ENGINE_SPEC.md` — canonical mastery seviyesi Skill, atomik evidence seviyesi Learning Objective.
- `docs/LEARNING_BEHAVIOR_RULES.md` — yanlış cevap, soru varyasyonu, prerequisite-aware assessment, retention ve AI sınırları.
- `docs/TOPIC_STATE_MACHINE.md` — Topic state'in Skill evidence/mastery'den türetilmesi.
- `docs/ENGLISH_FOUNDATION_RULES.md` — English task'lerinde öğretilmemiş grammar/vocabulary'nin gizli prerequisite olmaması.

Ana ilke:

> **Mastery tek bir puan, tek bir quiz veya tek bir doğru cevap değildir; hedeflenen becerinin farklı açılardan, uygun bağımsızlık ve bağlam koşullarında gösterilmiş kanıtlarının bütünüdür.**

---

# 1. 2C'nin amacı

2C şu soruları cevaplar:

- Hangi tür kullanıcı davranışları gerçek learning evidence sayılır?
- Bir quiz doğru cevabı neyi kanıtlar, neyi kanıtlamaz?
- Coding, debugging, explanation, transfer, retention ve proje evidence'ı nasıl ayrılır?
- Süre, confidence, task completion gibi bilgiler mastery kanıtı mıdır yoksa yalnız bağlam mıdır?
- Aynı soruyu tekrar tekrar doğru yapmak neden bağımsız evidence değildir?
- Bir Learning Objective hangi evidence türleriyle ölçülebilir?
- Hatalı/uygunsuz assessment evidence'ı mastery'yi neden etkilememelidir?

2C **yüzde, weight, threshold veya kaç kanıt gerekir** sorularını cevaplamaz. Bunlar 2E'ye aittir.

AI/hint yardım seviyelerinin evidence gücünü nasıl değiştireceği 2D'ye aittir.

---

# 2. Evidence'in canonical bağlanma noktası

Her anlamlı evidence mümkün olduğunca şu zincire bağlanır:

`Attempt / Artifact → Learning Objective → Skill → derived Topic state`

Evidence önce atomik hedefe, yani Learning Objective'e bağlanır. Skill mastery daha sonra ilgili objective evidence'larının kurallı birleşiminden çıkar.

Topic, Module veya Domain'e doğrudan "mastery puanı" yazılmaz.

Bir görev birden fazla objective'i ölçüyorsa her objective için hangi bölümün evidence oluşturduğu izlenebilir olmalıdır.

---

# 3. Evidence'in üç rolü

Her sinyal aynı türde değildir. V1 için üç ana evidence rolü tanımlanır.

## 3.1 Direct / primary evidence

Learning Objective'in tarif ettiği davranışı doğrudan gösterir.

Örnek:

Objective: `pointer üzerinden bir int değerini değiştirebilir`.

Kullanıcının kendi yazdığı ve doğru çalışan pointer kodu direct evidence'dır.

## 3.2 Corroborating evidence

Ana beceriyi destekler fakat tek başına objective'in tam yapabilme kapasitesini kanıtlamaz.

Örnek:

Aynı objective için pointer kodunun çıktısını doğru tahmin etmek destekleyicidir; fakat kullanıcının kendisinin kod yazabildiğini tek başına kanıtlamaz.

## 3.3 Contextual signal

Mastery'nin kendisi değildir; evidence'ın yorumlanmasına yardımcı olur.

Örnekler:

- süre,
- kaç denemede çözüldüğü,
- kullanılan yardım/ipucu,
- aynı soru familyasını daha önce görüp görmediği,
- kullanıcı confidence/self-report,
- cihaz/oturum koşulları.

Contextual signal tek başına `mastered` üretmez.

---

# 4. Evidence outcome yönü

Evidence yalnız pozitif olmak zorunda değildir.

Her evidence event kavramsal olarak şu yönlerden birine sahip olabilir:

- **positive:** hedef davranış başarıyla gösterildi,
- **negative:** hedef davranışta gerçek hata/eksiklik gözlendi,
- **partial/mixed:** davranışın yalnız bir kısmı doğru veya rubric'in bazı boyutları karşılandı,
- **invalid/unusable:** evidence mastery hesabında kullanılmamalı.

Önemli kural:

> Tek bir negative evidence bütün Skill mastery'yi sıfırlamaz; fakat farklı ve bağımsız denemelerde tekrarlanan negative evidence daha güçlü zayıflık/remediation sinyali oluşturabilir.

Kesin etkisi 2E'de tanımlanacaktır.

---

# 5. Mastery signal türleri

## 5.1 Concept recognition — kavramı tanıma / seçme

Örnekler:

- çoktan seçmeli soru,
- doğru/yanlış,
- eşleştirme,
- verilen tanımlar arasından doğru olanı seçme.

### Neyi kanıtlayabilir?

- temel kavram ayrımını tanıyabilme,
- terminoloji recognition,
- yanlış seçenekler arasında doğru modeli ayırt edebilme.

### Neyi tek başına kanıtlayamaz?

- bilgiyi yardımsız recall edebilme,
- kod yazabilme,
- problem çözebilme,
- debugging,
- transfer,
- uzun vadeli retention.

### Kural

Recognition evidence özellikle erken öğrenmede faydalıdır ancak kritik teknik Skill'i tek başına mastered yapamaz.

---

## 5.2 Concept recall / short open response — yardımsız hatırlama

Örnekler:

- `&x ne üretir?` sorusuna seçenek olmadan cevap verme,
- kısa tanım yazma,
- temel komutu/terimi hatırlama.

### Neyi kanıtlayabilir?

- bilginin seçenek görmeden zihinden getirilebildiğini,
- recognition'dan daha bağımsız retrieval kapasitesini.

### Neyi tek başına kanıtlayamaz?

- gerçek uygulama becerisi,
- karmaşık transfer,
- coding/debugging yetkinliği.

---

## 5.3 Code reading / output prediction

Örnek:

```c
int x = 5;
int *p = &x;
*p = 12;
printf("%d", x);
```

Kullanıcıdan çıktı ve mümkünse kısa gerekçe istenir.

### Neyi kanıtlayabilir?

- kodun operasyonel anlamını takip edebilme,
- execution model hakkında kavramsal anlayış,
- değişken/state değişimini zihinsel olarak simüle edebilme.

### Neyi tek başına kanıtlayamaz?

- sıfırdan kod üretme,
- gerçek compiler/runtime kullanımı,
- debugging'in tüm boyutları.

Kod çıktısını seçeneklerden seçmek ile çıktıyı kendi üretmek aynı evidence kalitesinde kabul edilmez; kesin kalite etkisi 2E/Aşama 4'te belirlenir.

---

## 5.4 Coding / production evidence

Coding evidence kullanıcının hedeflenen davranışı içeren **kendi kod artifact'ını üretmesini** gerektirir.

Örnek objective:

`Bir pointer kullanarak x değişkeninin değerini değiştirebilir.`

Görev:

```c
int x = 10;
/* pointer kullanarak x'i 25 yap */
```

Kullanıcı kendi çözümünü üretir.

### Coding evidence kaynakları

V1 ve sonraki sürümlerde şunlardan gelebilir:

- uygulama içindeki kısa code editor,
- code completion fakat anlamlı kısmı kullanıcıya bırakılmış görev,
- PC/editor/terminal üzerinde yazılıp uygulamaya yapıştırılan kod,
- compiler/test çıktısıyla birlikte teslim edilen çözüm,
- ileride local/remote code runner tarafından doğrulanan artifact.

### Neyi kanıtlayabilir?

- syntax ve API bilgisini kullanabilme,
- hedef Skill'i gerçekten uygulayabilme,
- çözümü üretim yönünde kurabilme.

### Neyi tek başına kanıtlayamaz?

- kodun neden çalıştığını açıklayabilme,
- farklı bağlama transfer,
- uzun vadeli retention,
- kod AI/başka kaynak tarafından üretildiyse kullanıcının gerçekten anlayıp anlamadığı.

### Kritik kural

Çoktan seçmeli `hangi kod doğru?` sorusu **coding evidence değildir**; recognition/code-reading evidence'dır.

Uygun görevlerde executable output, compiler sonucu veya hidden test case coding evidence'ı güçlendiren doğrulayıcı artifact olabilir; ancak testlerin geçmesi tek başına kullanıcı anlayışının tüm boyutlarını kanıtlamaz.

AI/hint/copy yardımı 2D'de ayrıca modellenir.

---

## 5.5 Debugging / diagnosis evidence

Kullanıcıya hatalı, riskli veya beklenmeyen davranış gösteren code/system durumu verilir ve şunlardan biri/birkaçı istenir:

- hatayı bul,
- nedenini açıkla,
- düzelt,
- fix'in neden doğru olduğunu doğrula.

### Neyi kanıtlayabilir?

- yanlış mental modeli ayırt etme,
- hata neden-sonuç bağlantısı,
- code reading + diagnosis,
- gerçek systems/programming işine yakın problem çözme.

### Neyi tek başına kanıtlayamaz?

- sıfırdan sistem tasarlama veya kod üretme kapasitesinin tamamı,
- retention,
- başka problem sınıflarına transfer.

Aynı bug pattern'inin ezberlenmiş fix'i tekrar tekrar yapılırsa evidence bağımsızlığı düşer; farklı bug yapılarına geçmek gerekir.

---

## 5.6 Explanation / justification / Feynman evidence

Kullanıcıdan cevabın yalnız sonucunu değil, **nedenini kendi ifadeleriyle** açıklaması istenir.

Örnek:

> `*p = 25` neden `x` değerini değiştiriyor?

### Neyi kanıtlayabilir?

- kavramsal bağlantıları,
- yanlış ama çalışan ezber çözüm ihtimalini azaltmayı,
- terminology ve mental model kalitesini,
- belirli objective'lerde sebep-sonuç anlayışını.

### Neyi tek başına kanıtlayamaz?

- gerçek coding üretimi,
- gerçek debugging uygulaması,
- performans altında kullanabilme,
- retention.

Açıklamanın uzun veya süslü olması mastery kanıtı değildir. Rubric hedef concept'lerin doğruluğuna bakmalıdır.

---

## 5.7 Transfer / novel application evidence

Transfer evidence kullanıcının öğrendiği Skill'i **daha önce birebir görmediği bir problem veya bağlamda** uygulamasıdır.

### Neyi kanıtlayabilir?

- ezberlenmiş soru/şablon yerine genellenebilir mental model,
- bir Skill'i farklı yüzey özellikleri altında tanıyıp kullanabilme,
- birden fazla daha önce öğrenilmiş beceriyi bir araya getirebilme.

### Kritik prerequisite kuralı

Transfer sorusu bilinmeyen yeni prerequisite'i gizlice ekleyerek zorlaştırılamaz.

Bir transfer item target Skill dışında `required_skills` tanımlamalı ve kullanıcının bu prerequisite'leri daha önce öğrenmiş olması gerekir.

Öğretilmemiş bir kavram yüzünden başarısız olunan transfer sorusu target Skill için geçerli negative evidence değildir.

### Aynı familya problemi

Sadece sayıları/variable isimlerini değiştirip aynı çözüm şablonunu tekrarlamak gerçek transfer değildir. Question Bank farklı yüzey varyasyonlarının yanında gerçekten farklı problem yapıları da içermelidir.

---

## 5.8 Retention / delayed retrieval evidence

Retention evidence daha önce öğrenilmiş Skill'in **anlamlı bir gecikmeden sonra** yeniden kullanılabilmesini ölçer.

Bu gecikme özel retention sorusu veya daha ileri bir Topic içindeki doğal kullanım yoluyla oluşabilir.

### Neyi kanıtlayabilir?

- kısa süreli performansın ötesinde bilginin korunması,
- Skill'in tekrar erişilebilir olması,
- previously mastered state'in hâlâ güvenilir olup olmadığı.

### Kural

Aynı gün art arda doğru yapılan 10 soru retention evidence değildir.

Retention interval algoritması 2F'de belirlenir.

Bir retention item'daki tek hata mastery'yi otomatik sıfırlamaz; farklı evidence ile doğrulama gerekebilir.

İleri Topic içinde aynı canonical Skill'in bağımsız ve doğru kullanılması uygun koşullarda retention/reinforcement evidence sayılabilir.

---

## 5.9 Integrated task / project evidence

Bir proje veya daha büyük gerçekçi görev birden fazla Skill'i birlikte kullanmayı gerektirir.

Örnekler:

- küçük C programı,
- dosya okuyup işleyen CLI,
- pointer + struct + file I/O kullanılan mini uygulama,
- ileride concurrency/networking sistem görevi.

### Neyi kanıtlayabilir?

- Skill'leri birlikte kullanma,
- gerçek bağlamda üretim,
- task decomposition,
- birden fazla objective için transfer/integration.

### Kritik kural

`Project completed = tüm içindeki Skill'ler mastered` değildir.

Proje evidence'ı objective bazında ayrıştırılmalıdır.

Örnek:

Proje çalışıyor olabilir ama kullanıcı memory cleanup kısmını hiç yazmadıysa `free/manage_lifetime` objective'i için positive evidence oluşmaz.

AI/yardım düzeyi ayrıca 2D ile işaretlenir.

---

# 6. Contextual sinyaller — mastery değildir

## 6.1 Time / latency / fluency

Süre kaydedilebilir ama **doğrudan mastery puanı değildir**.

- Hızlı cevap = otomatik daha iyi öğrenme değildir.
- Yavaş doğru cevap = otomatik başarısızlık değildir.
- Çok uzun süre, çok fazla tekrar veya ciddi tereddüt belirli bağlamlarda confusion/fluency sinyali olabilir.

Süre özellikle accessibility, telefon klavyesi, dış ortam, görev tipi ve dikkat koşullarından etkilenebilir.

Bu nedenle 2E'de bile time varsayılan olarak yardımcı/contextual feature olarak ele alınmalı; doğrudan ana mastery kaynağı yapılmamalıdır.

## 6.2 Task / lesson completion

`Dersi açtı`, `videoyu izledi`, `görevi tamamlandı olarak işaretledi` gibi bilgiler coverage/progress bilgisidir.

Bunlar **mastery evidence değildir**.

## 6.3 Self-reported confidence

`Biliyorum`, `kolaydı`, `zor geldi` gibi kullanıcı beyanları planner/tutor için faydalı olabilir fakat tek başına mastery üretmez.

Confidence ile gerçek performans ayrı tutulur.

## 6.4 Engagement / streak

Streak, uygulamada geçirilen dakika, ardışık gün sayısı veya ekran görüntüleme sayısı mastery değildir.

---

# 7. Evidence kalite boyutları

Aynı evidence type içindeki iki attempt aynı değerde olmayabilir. Her evidence event mümkün olduğunca aşağıdaki kalite boyutlarıyla yorumlanır.

## 7.1 Correctness / rubric quality

Yanıt doğru mu, kısmen doğru mu, hangi rubric boyutları karşılandı?

## 7.2 Independence / assistance context

Kullanıcı ne kadar bağımsız üretti?

Exact hint/AI etkisi 2D'de tasarlanır; 2C seviyesinde yardım bağlamının kaydedilmesi zorunlu kabul edilir.

## 7.3 Novelty / familiarity

- item ilk kez mi görülüyor,
- aynı variant familya mı,
- birebir aynı soru mı,
- daha önce çözüm gösterildi mi?

Birebir tekrar edilen soruların bağımsız evidence değeri düşer.

## 7.4 Difficulty / complexity

Item'ın target objective için beklenen zorluğu kaydedilir.

Zorluk bilinmeyen prerequisite ekleyerek yapay biçimde yükseltilmez.

## 7.5 Evidence-type fit

Evidence türü objective'in kendisini gerçekten ölçüyor mu?

Örnek:

Objective `kendi kodunu yazabilir` ise recognition quiz doğrudan evidence değildir.

## 7.6 Prerequisite validity

Item'ın ihtiyaç duyduğu diğer Skill'ler kullanıcıda mevcut mu?

Değilse başarısızlık target Skill için temiz evidence değildir.

## 7.7 Delay / recency

Evidence yeni öğrenmeden hemen sonra mı, günler/haftalar sonra mı geldi?

Retention interpretation 2F'de kesinleşir.

## 7.8 Evaluator / item trust

Evidence kaynağı:

- doğrulanmış Question Bank item'ı,
- deterministic compiler/test,
- rubric,
- AI-generated/AI-evaluated item,
- insan/manuel kontrol

gibi provenance bilgisi taşımalıdır.

AI-generated/evaluated evidence'ın güven seviyesi 2D, 4D–4E ve 13. aşamada detaylandırılır.

---

# 8. Evidence independence ve çeşitlilik

Mastery confidence aynı türden çok sayıda birbirine bağımlı soruyu çözerek yapay biçimde şişmemelidir.

Örnek:

Aynı pointer sorusunun yalnız `x=5`, `x=8`, `x=10` olarak üç kez verilmesi üç tamamen bağımsız transfer kanıtı değildir.

Evidence diversity şu boyutlardan gelebilir:

- farklı item / problem yapısı,
- farklı evidence modality (recall, coding, debugging, explanation),
- farklı bağlam,
- gecikmeli tekrar,
- farklı Topic içinde doğal kullanım.

Kesin minimum çeşitlilik ve tekrar-deduplication formülü 2E/Aşama 4'te tanımlanacaktır.

---

# 9. Objective-specific evidence profile

Her Learning Objective aynı evidence kombinasyonunu gerektirmez.

Bu nedenle curriculum authoring sırasında objective kavramsal olarak şu bilgileri taşıyabilmelidir:

- `acceptable_evidence_types`
- `direct_evidence_types`
- gerekirse `forbidden_as_solo_mastery` türleri
- objective'in transfer gerektirip gerektirmediği
- objective'in retention takibine tabi olup olmadığı

Kesin database schema 9C'de tasarlanacaktır.

### Örnek A — kavramsal objective

Objective:

`& operatörünün bir adres ürettiğini açıklayabilir.`

Uygun direct evidence:

- concept recall,
- explanation.

Destekleyici evidence:

- recognition,
- code reading.

Coding bu objective için faydalı olabilir ama tek başına açıklama objective'ini kanıtlamaz.

### Örnek B — üretim objective'i

Objective:

`pointer üzerinden bir int değişkeninin değerini değiştiren kod yazabilir.`

Direct evidence:

- coding/production.

Corroborating:

- code reading,
- debugging,
- explanation.

Recognition quiz tek başına yetersizdir.

### Örnek C — debugging objective'i

Objective:

`basit invalid dereference hatasını teşhis edip düzeltebilir.`

Direct evidence:

- debugging/diagnosis + fix.

Destekleyici:

- explanation,
- code reading.

---

# 10. Negative evidence ve misconception sinyali

Yanlış cevap yalnız `0 puan` olarak tutulmamalıdır.

Mümkün olduğunda hata:

- hangi objective'de oluştu,
- hangi misconception familyasına uyuyor,
- syntax mı concept mi,
- prerequisite eksikliği mi,
- dikkatsizlik/tekil hata ihtimali mi,
- aynı hata başka item'larda tekrarlandı mı

şeklinde ayrıştırılabilir.

Örnek pointer misconception'ları:

- address/value confusion,
- `&` ile `*` rolünü karıştırma,
- uninitialized pointer kullanma,
- pointer'ın tuttuğu adres ile pointee value'yu karıştırma.

Misconception taxonomy Aşama 4 ve 13'te genişletilir.

---

# 11. Geçersiz / contamination içeren evidence

Aşağıdaki durumlarda attempt tamamen veya kısmen mastery evidence olarak kullanılmamalıdır:

- soru henüz öğretilmemiş required prerequisite içeriyor,
- soru teknik olarak hatalı/ambiguous,
- doğru cevap kullanıcıya önceden açıkça gösterildi ve hemen aynısı soruldu,
- evaluator sonucu güvenilir biçimde belirleyemiyor,
- görev başka bir Skill'i asıl darboğaz yaptığı halde target Skill'e yanlış attribution yapıyor,
- item'ın answer key/rubric'i hatalı,
- sistem/runner problemi nedeniyle kullanıcı başarısız görünüyor.

Bu durumlar mümkün olduğunda `invalid_reason` ile loglanır; kullanıcı mastery'si cezalandırılmaz.

---

# 12. Assistance ile evidence arasındaki sınır

2C'nin bağlayıcı kararı:

> Yardımlı ve yardımsız performans aynı şey değildir; assistance context evidence ile birlikte saklanmalıdır.

Ancak şunlar 2D'ye bırakılır:

- hint level taxonomy,
- AI answer / code generation etkisi,
- kaç hint sonrası evidence'ın ne kadar düşeceği,
- AI-assisted task sonrası hangi comprehension check'in zorunlu olacağı.

2C bu değerleri uydurmaz.

---

# 13. Evidence event için kavramsal kayıt sözleşmesi

Kesin DB schema 9C'ye ait olmakla birlikte, sistemin evidence yorumlayabilmesi için bir event kavramsal olarak şu alanları taşıyabilmelidir:

```text
EvidenceEvent
- id
- timestamp
- skill_id
- objective_id(s)
- evidence_type
- task/question/artifact_id
- variant_family
- outcome: positive | negative | partial | invalid
- correctness/rubric_result
- difficulty
- novelty/familiarity
- assistance_context
- duration
- prerequisite_snapshot
- delay_since_last_exposure
- evaluator/provenance
- misconception/error tags
- artifact/output reference (varsa)
```

Bu bir database migration değildir; 9C için davranış sözleşmesidir.

---

# 14. Mastery false-positive önleme invariant'ları

V1 mastery sistemi aşağıdaki yanlış pozitiflere izin vermemelidir:

1. **Tek kolay quiz doğru → mastered** olmaz.
2. **Ders/task tamamlandı → mastered** olmaz.
3. **Aynı soruyu ezberleyip tekrar doğru yapmak → bağımsız mastery** sayılmaz.
4. **Code seçeneğini doğru seçmek → coding mastery** sayılmaz.
5. **Project çalıştı → projedeki tüm Skill'ler mastered** sayılmaz.
6. **AI'nın ürettiği kod çalıştı → kullanıcı coding mastery** otomatik sayılmaz.
7. **Hızlı cevap → daha yüksek mastery** otomatik değildir.
8. **Kullanıcı `biliyorum` dedi → mastered** olmaz.
9. **Öğretilmemiş prerequisite yüzünden hata → target Skill negative evidence** olmaz.
10. **Immediate practice başarısı → uzun vadeli retention** kanıtı değildir.

---

# 15. Research doğrulama notu

2C tasarımında kısa dış doğrulama yapıldı.

Kullanılan araştırma yönleri:

- Retrieval practice'ın uzun vadeli retention'ı desteklediğini gösteren Roediger & Butler (2011), *Trends in Cognitive Sciences*, DOI `10.1016/j.tics.2010.09.003`.
- Test-enhanced learning'in transfer etkilerini inceleyen Pan & Rickard (2018) meta-analysis, *Psychological Bulletin*, DOI `10.1037/bul0000151`.
- Farklı örnekler üzerinde retrieval/application yapmanın yeni örneklere transferi geliştirebildiğini gösteren Butler et al. çalışması, DOI `10.1037/xap0000142`.
- Retrieval practice ile delayed recall ilişkisini gösteren Karpicke & Roediger (2008), *Science*, DOI `10.1126/science.1152408`.

Bu kaynaklar ürünün exact weight/threshold değerlerini belirlemek için kullanılmadı. Yalnız şu tasarım ilkelerini desteklemek için kullanıldı:

- immediate performance ile durable learning aynı şey değildir,
- retrieval/delayed evidence önemlidir,
- transfer yeni bağlamla ayrıca ölçülmelidir,
- aynı exact item'a aşırı bağımlılık gerçek genellenebilir mastery'yi olduğundan güçlü gösterebilir.

Kesin sayısal model 2E'de ayrıca araştırma + simülasyon + pilot kalibrasyonuna tabi olacaktır.

---

# 16. 2C'de bilinçli olarak kararlaştırılmayanlar

Aşağıdakiler sonraki adımlara bırakılmıştır:

- hint/AI yardım seviyelerinin evidence gücü → **2D**
- evidence weight'leri → **2E**
- mastery threshold → **2E**
- minimum evidence sayısı/çeşitliliği → **2E**
- confidence aggregation → **2E**
- exact forgetting/decay ve review interval → **2F**
- günlük/haftalık/aylık assessment composition → **Aşama 4**
- Question Bank schema → **4D**
- AI-generated item validation → **4E**
- exact database schema → **9C**
- code evaluator/compiler mimarisi → **9E / Aşama 14**

---

# 17. 2C kabul kriterleri

2C tamamlanmış kabul edilir çünkü:

- mastery signal ile contextual signal ayrıldı,
- direct / corroborating / contextual evidence rolleri tanımlandı,
- concept recognition, recall, code reading, coding, debugging, explanation, transfer, retention ve integrated project evidence'ları ayrı tanımlandı,
- her evidence türünün neyi kanıtlayıp neyi tek başına kanıtlayamayacağı yazıldı,
- time, completion, streak ve self-confidence'ın mastery olmadıkları kilitlendi,
- evidence quality boyutları tanımlandı,
- prerequisite contamination'ın evidence'ı geçersizleştirebilmesi tanımlandı,
- aynı item/familya tekrarının bağımsız evidence olarak şişirilmesi engellendi,
- Objective-specific evidence profile yaklaşımı tanımlandı,
- negative evidence/misconception yaklaşımı tanımlandı,
- coding evidence'ın gerçek kullanıcı üretimi gerektirdiği ve MCQ'nun coding evidence olmadığı kilitlendi,
- project completion'ın alt Skill mastery'lerini otomatik üretmemesi kilitlendi,
- retention'ın immediate correctness'tan ayrı evidence türü olduğu tanımlandı,
- AI/hint etkisi 2D'ye, exact ağırlık ve threshold 2E'ye bilinçli olarak bırakıldı.

---

# 18. Sıradaki adım

## 2D — AI / ipucu etkisi

Bir sonraki adımda:

- hint seviyeleri,
- kullanıcı kendi çözmeden önce/sonra alınan yardım,
- AI explanation,
- AI-generated answer/code,
- copy/paste yardım,
- assisted coding,
- comprehension/transfer recheck,
- yardımlı evidence'ın mastery hesabındaki güven seviyesi

kesin davranış modeli olarak tasarlanacaktır.

2D'de de exact mastery yüzdeleri mümkün olduğunca 2E'ye bırakılacaktır.