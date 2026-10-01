# AI Assistance & Hint Evidence Specification — AI Infra Learning Coach

**Adım:** 2D — AI / ipucu etkisi  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge, kullanıcı bir öğrenme veya assessment görevinde ipucu, AI açıklaması, AI-generated cevap/kod, copy/paste, compiler/test feedback veya başka bir yardım kullandığında bu yardımın learning evidence açısından nasıl yorumlanacağını tanımlar.

Bu belge şu kaynaklarla birlikte bağlayıcıdır:

- `docs/LEARNING_ENGINE_SPEC.md` — canonical mastery seviyesi Skill, atomik evidence seviyesi Learning Objective.
- `docs/LEARNING_BEHAVIOR_RULES.md` — AI yardımcı öğretmen/evaluator'dır; mastery/planner'ın keyfi sahibi değildir.
- `docs/MASTERY_SIGNALS_SPEC.md` — assistance context evidence'ın kalite boyutlarından biridir.
- `docs/TOPIC_STATE_MACHINE.md` — Topic state doğrudan AI kararıyla değil canonical Skill state'lerinden türetilir.
- `docs/ENGLISH_FOUNDATION_RULES.md` — bilinmeyen English prerequisite yardım ihtiyacını yanlış mastery sinyaline dönüştürmez.

Ana ilke:

> **AI ve ipucu kullanımı yasak değildir; fakat yardımla ortaya çıkan performans, kullanıcının yardımsız yapabildiğine dair kanıtla aynı şey değildir.**

İkinci ana ilke:

> **Yardım öğrenmeyi destekleyebilir; çözümü görmek ise aynı item'ın bağımsız mastery kanıtı olma gücünü azaltır veya ortadan kaldırır.**

2D exact yüzde, weight, threshold veya mastery puanı belirlemez. Bunlar 2E'ye aittir.

---

# 1. 2D'nin amacı

2D şu soruları kesinleştirir:

- Hangi yardım türleri sadece yönlendirme, hangileri çözüm ifşasıdır?
- Yardımın görevin hangi anında verildiği neden önemlidir?
- AI kodu yazarsa kullanıcı coding mastery kazanmış sayılır mı?
- Kullanıcı kendi kodunu yazıp sonra AI'dan review alırsa önceki evidence bozulur mu?
- Bir çözüm gösterildikten sonra mastery nasıl tekrar doğrulanır?
- Compiler/test feedback, documentation veya normal developer araçları her zaman “hile” midir?
- External AI kullanımı nasıl ele alınır?
- Yardım isteği negative mastery midir?
- Hangi durumda comprehension / transfer recheck zorunlu hale gelir?

---

# 2. Assistance tek boyutlu bir “kaç hint aldı?” sayacı değildir

Assistance en az dört ayrı boyutta yorumlanır:

1. **content level** — yardım ne kadarını açığa çıkardı?
2. **timing** — yardım ilk attempt'ten önce mi, sırasında mı, submit sonrası mı geldi?
3. **source/provenance** — built-in hint, AI Tutor, compiler, documentation, external AI, insan vb.
4. **artifact origin** — son cevap/kod gerçekten kullanıcı tarafından mı üretildi, karışık mı, doğrudan kopya mı?

Bu ayrım yapılmadan yalnız `hint_count = 2` gibi bir sayı mastery için yeterli değildir.

---

# 3. Canonical assistance content seviyeleri

V1 için yardım içeriği beş seviyede sınıflandırılır.

## H0 — `none / independent`

Kullanıcı target task'in çözümünü etkileyen bir yardım almadan attempt üretir.

Normal task yönergesi, daha önce öğretilmiş içerik ve assessment'ın izin verdiği standart araçlar H0'ı otomatik bozmaz.

## H1 — `orientation / strategic nudge`

Çözümü veya target bilgiyi vermeden düşünme yönü sağlar.

Örnekler:

- “Address ile value farkını tekrar düşün.”
- “Önce hangi değişkenin değişmesini istediğini belirle.”
- “Hata mesajındaki satır numarasına bak.”

H1, kullanıcının kalan çözümü kendisinin üretmesine hâlâ geniş alan bırakır.

## H2 — `targeted conceptual hint`

İlgili kavramı, yöntemi veya problem bölgesini belirgin biçimde işaret eder fakat exact cevabı/çözümü tamamlamaz.

Örnekler:

- “Burada değeri pointer üzerinden değiştirmek için dereference gerekir.”
- “Sorun pointer'ın initialize edilmemiş olmasıyla ilgili.”

Bu yardım target reasoning'in anlamlı bir kısmını açığa çıkardığı için H1'den daha fazla contamination oluşturur.

## H3 — `partial solution / scaffold`

Çözümün önemli bir parçası, intermediate step, syntax parçası veya kod iskeleti verilir.

Örnekler:

```c
int *p = &x;
/* kalan satırı sen tamamla */
```

veya debugging görevinde doğrudan problemli satır + fix yönünün büyük kısmının gösterilmesi.

H3 attempt hâlâ öğretici olabilir fakat kullanıcı full solution'ı bağımsız üretmiş sayılmaz.

## H4 — `full solution / answer exposure`

Beklenen cevap, tam çözüm veya hedef Skill'in asıl performansını doğrudan karşılayan artifact kullanıcıya gösterilir.

Örnekler:

- doğru şıkkı söylemek,
- tam C çözümünü üretmek,
- debugging fix'ini doğrudan vermek,
- açık uçlu cevabı kullanıcının yerine yazmak.

H4 sonrası aynı item kullanıcı için **solution-exposed** kabul edilir.

---

# 4. Artifact origin ayrı tutulur

Assistance level ile son artifact'ın kaynağı aynı şey değildir.

Kavramsal artifact origin değerleri:

- `user_authored` — anlamlı çözümü kullanıcı üretti,
- `user_authored_with_assistance` — kullanıcı üretti fakat yardım aldı,
- `mixed_authorship` — kullanıcı + AI/başka kaynak önemli parçaları birlikte oluşturdu,
- `generated_or_copied` — çözümün esas kısmı AI/başka kaynak tarafından üretildi veya kopyalandı,
- `unknown_provenance` — güvenilir biçimde bilinmiyor.

Kritik kural:

> **`generated_or_copied` artifact'ın çalışması, kullanıcının coding/production mastery'si için direct positive evidence oluşturmaz.**

Bu artifact yine öğrenme materyali, code-reading, explanation veya sonraki comprehension task'i için kullanılabilir.

---

# 5. Assistance timing

Aynı yardım farklı zamanda farklı evidence etkisine sahiptir.

## 5.1 `before_attempt`

Kullanıcı ilk cevabını üretmeden yardım alır.

Yardım target çözüm yolunu açığa çıkarıyorsa o attempt tam bağımsız değildir.

## 5.2 `during_attempt`

Kullanıcı çözüm üretirken hint/AI desteği alır.

Attempt assistance metadata ile birlikte değerlendirilir; assistance seviyesi yükseldikçe independent-performance iddiası zayıflar.

## 5.3 `after_submit`

Kullanıcı cevabını önce gönderir, ardından feedback/AI açıklaması alır.

Kritik kural:

> Submit anında tamamlanmış önceki attempt evidence'ı, sonradan verilen açıklama nedeniyle geriye dönük kirlenmez.

Ancak çözüm gösterildikten sonra **aynı item'ın yeni attempt'i** bağımsız yeni evidence sayılmaz.

## 5.4 `after_failure / remediation`

Yanlış attempt sonrası açıklama, worked example veya full solution öğretim amacıyla verilebilir.

Bu kullanım ceza değildir. Fakat çözüm ifşa edilmişse doğrulama yeni/unseen bir variant ile yapılır.

---

# 6. Evidence kullanım sınıfları

2D sayısal weight vermez; bunun yerine evidence'ın assistance nedeniyle hangi **yorum sınıfında** olduğunu belirler.

## `independent_evidence`

- target çözüm kullanıcı tarafından bağımsız üretildi,
- çözümü açığa çıkaran yardım yok,
- task'in izin verdiği araç politikası ihlal edilmedi.

Bu sınıf 2E'de strongest eligible evidence kategorilerinden biri olabilir.

## `assisted_evidence`

- kullanıcı anlamlı performans üretti,
- fakat H1/H2 benzeri yardım aldı veya objective'in izin verdiği ölçüde scaffold kullandı.

Bu evidence tamamen çöpe atılmaz; ancak independent evidence ile aynı kabul edilmez.

Exact ağırlık/threshold 2E'ye bırakılır.

## `practice_only`

- H3/H4 yardım sonucu çözümün önemli kısmı gösterildi,
- veya artifact büyük ölçüde generated/copied,
- veya task açıkça öğretim/worksheet scaffold modundaydı.

Bu attempt öğrenme history'sinde tutulur fakat **tek başına independent mastery gate'i geçiremez**.

## `requires_independent_recheck`

H3/H4 veya solution exposure sonrası target Objective için yeni bağımsız doğrulama gerekir.

Bu bir ceza değildir; amaç `çözümü gördü` ile `artık kendi yapabiliyor` arasındaki farkı ölçmektir.

---

# 7. Yardım sonrası recheck kuralları

## 7.1 Exact aynı soru varsayılan recheck değildir

Çözümü gösterilmiş item hemen yeniden sorulup doğru yapılırsa bu güçlü mastery kanıtı değildir.

Recheck:

- aynı Skill / Learning Objective'i hedeflemeli,
- farklı unseen variant veya farklı problem yapısı kullanmalı,
- yalnız daha önce öğretilmiş prerequisite'leri gerektirmeli,
- mümkün olduğunca solution exposure'dan bağımsız olmalı.

## 7.2 H4 / full solution sonrası

Target Skill kritikse independent recheck zorunludur.

Coding için uygun recheck örnekleri:

- yeni küçük problemde aynı Skill ile sıfırdan kod yazma,
- gösterilen çözümü farklı girdiye/koşula bağımsız uyarlama,
- kritik satırların nedenini açıklama + ardından yeni coding variant.

Explanation tek başına coding production objective'ini tamamen yerine geçmez.

## 7.3 H3 / partial solution sonrası

Eksik kalan bölüm gerçekten target Objective'in ana davranışıysa yeni independent variant gerekir.

Scaffold yalnız target dışı boilerplate sağladıysa task objective-specific policy'ye göre assisted/direct evidence kalabilir.

## 7.4 H1/H2 sonrası

Her küçük hint için ayrı uzun sınav üretmek zorunlu değildir.

Kullanıcı hint sonrası anlamlı çözümü kendisi tamamladıysa assisted evidence kaydedilir. Kritik Skill mastery gate'inde yeterli bağımsız evidence bulunup bulunmadığını 2E değerlendirir.

---

# 8. AI-generated kod için bağlayıcı davranış

## Senaryo A — AI tüm kodu yazdı, kullanıcı çalıştırdı

Sonuç:

- programın çalışması `artifact_execution` / practice sonucu olabilir,
- kullanıcı için direct `coding/production` mastery evidence değildir,
- target objective gerekiyorsa independent recheck açılır.

## Senaryo B — kullanıcı kodu yazdı, AI submit sonrası review yaptı

Sonuç:

- yardım öncesi tamamlanan coding attempt kendi evidence niteliğini korur,
- AI feedback sonraki öğrenme/remediation içindir.

## Senaryo C — kullanıcı yazarken AI küçük conceptual hint verdi

Sonuç:

- attempt assisted evidence'dır,
- tamamen geçersiz sayılmaz,
- exact etkisi 2E'de belirlenir.

## Senaryo D — AI kritik kod satırlarını yazdı, kullanıcı geri kalanını tamamladı

Sonuç:

- artifact `mixed_authorship`,
- AI'nın yazdığı kısmın target Objective'i karşıladığı yerde direct coding evidence oluşmaz,
- kullanıcının gerçekten ürettiği diğer objective'ler ayrı evidence üretebilir.

## Senaryo E — kullanıcı AI kodunu açıklayabiliyor

Sonuç:

- doğru açıklama `explanation/comprehension` evidence olabilir,
- fakat bu tek başına `coding/production` evidence'a dönüşmez,
- coding objective için yeni bağımsız üretim gerekir.

---

# 9. Comprehension check ile production check aynı şey değildir

AI yardımı sonrası sistem şu soruları kullanabilir:

- “Bu satır neden gerekli?”
- “Bunu kaldırırsak ne olur?”
- “Buradaki `*p` neyi değiştiriyor?”
- “Aynı mantığı farklı değişkenlerle uygula.”
- “Bu çözümdeki hatayı bul.”

Bunlar understanding'i doğrulayabilir.

Ancak objective `kendi kodunu yazabilir` ise yalnız açıklama sorularını geçmek production mastery için yeterli değildir.

Kural:

> **Recheck türü hedef Learning Objective'in davranışıyla eşleşmelidir.**

---

# 10. Compiler, test runner, documentation ve normal araçlar

Her araç kullanımı otomatik olarak AI assistance/cheating değildir.

Bir task/objective `allowed_tools_policy` veya eşdeğer davranış tanımına sahip olmalıdır.

Örnekler:

## Compiler feedback

Gerçek coding workflow'unda compiler error görmek doğal olabilir.

- Objective `C'de çalışan kod üretebilir` ise compiler kullanımı beklenen araç olabilir.
- Objective `belirli syntax'ı yardımsız recall eder` ise compiler/autocomplete yoğun kullanımı independence'i etkileyebilir.

## Test runner

Hidden tests correctness validator olabilir; test sonucu tek başına understanding kanıtı değildir.

## Documentation

API/documentation kullanımı bazı gerçek-world görevlerde tamamen normaldir.

Fakat objective özellikle memory recall ölçüyorsa documentation kullanımı task policy'ye aykırı olabilir.

## Autocomplete / IDE

Standart autocomplete her task'te otomatik contamination sayılmaz. Objective'e göre izin politikası belirlenir.

Ana kural:

> **Araç kullanımının evidence etkisi, “araç kullanıldı mı?” sorusundan değil, tool'un target Objective'in ölçmek istediği davranışı kullanıcı yerine yapıp yapmadığından çıkar.**

Exact tool policy Aşama 4 ve 8/13 implementation adımlarında metadata'ya dönüştürülür.

---

# 11. External AI / başka kaynak kullanımı

Bu kişisel öğrenme ürününün amacı kullanıcıyı gözetlemek veya “hile yakalamak” değildir.

Sistem external AI kullanımını her zaman güvenilir biçimde tespit edemeyeceğini kabul eder.

Bu nedenle:

- kullanıcı kendi isteğiyle `AI kullandım` / yardım seviyesi bildirebilir,
- built-in AI Tutor etkileşimleri otomatik loglanabilir,
- dışarıdan yapıştırılan kod için provenance kesin bilinmiyorsa `unknown_provenance` tutulabilir,
- sistem kullanıcıyı kanıtsız biçimde “AI ile yaptın” diye suçlamaz.

Mastery tasarımı mümkün olduğunca fresh/unseen transfer ve independent recheck sayesinde copy/paste kaynaklı false-positive'i azaltır.

---

# 12. Yardım istemek negative mastery değildir

Kullanıcının:

- “anlamadım” demesi,
- hint istemesi,
- AI Tutor'dan farklı açıklama istemesi,
- worked example istemesi

tek başına negative mastery evidence değildir.

Bunlar learning/tutor için diagnostic/contextual sinyallerdir.

Ancak kullanıcı farklı bağımsız görevlerde sürekli H3/H4 düzeyinde yardım olmadan target davranışı gösteremiyorsa bu durum doğrudan “hint istedi” diye değil, **bağımsız positive evidence eksikliği + gerçek performans sonuçları** üzerinden mastery/remediation kararını etkiler.

---

# 13. Teaching mode ile assessment mode ayrımı

## Teaching / practice mode

Amaç öğrenmektir.

- hint rahat kullanılabilir,
- full solution gerektiğinde gösterilebilir,
- AI alternatif açıklama verebilir,
- mistake üzerinden worked example yapılabilir.

Ancak bu assisted practice attempt'i tek başına mastery gate olarak kullanılmaz.

## Assessment / mastery-check mode

Amaç mevcut bağımsız kapasiteyi ölçmektir.

- allowed assistance daha sınırlı olabilir,
- çözümü açığa çıkaran yardım item'ı independent mastery evidence olmaktan çıkarır,
- kullanıcı yardım isterse assessment learning moduna dönüşebilir ve sonra fresh recheck planlanabilir.

Weekly/monthly sınavlarda tam allowed-assistance politikası Aşama 4'te kesinleşir.

---

# 14. Objective-specific assistance policy

Her Learning Objective aynı yardım politikasına sahip olmak zorunda değildir.

Kavramsal olarak objective/task şu metadata'yı destekleyebilmelidir:

```text
assistance_policy
- allowed_tools
- max_help_class_for_independent_evidence
- solution_exposure_requires_recheck
- required_recheck_evidence_types
- artifact_authorship_required
```

Kesin DB schema 9C'ye aittir.

Örnek:

Objective:
`pointer üzerinden değeri değiştiren kodu kendi yazabilir`

- compiler: allowed
- full AI code generation: independent evidence için allowed değil
- H1 orientation: assisted evidence olabilir
- H4 solution exposure: independent recheck gerekir
- direct evidence type: coding/production

---

# 15. EvidenceEvent assistance context sözleşmesi

`docs/MASTERY_SIGNALS_SPEC.md` içindeki `assistance_context` kavramsal olarak şu alanları destekleyebilmelidir:

```text
AssistanceContext
- source: none | built_in_hint | ai_tutor | external_ai | documentation | compiler | test_runner | peer | other
- help_level: H0 | H1 | H2 | H3 | H4
- timing: before_attempt | during_attempt | after_submit | after_failure
- solution_exposure: none | conceptual | partial | full
- artifact_origin: user_authored | user_authored_with_assistance | mixed_authorship | generated_or_copied | unknown_provenance
- task_mode: teaching | practice | assessment | retention
- allowed_by_task_policy: true | false | unknown
- recheck_required: true | false
- related_interaction/artifact reference
```

Bu bir DB migration değildir; 9C için davranış sözleşmesidir.

---

# 16. AI evaluator ile AI helper birbirine karıştırılmaz

AI bir kullanıcının açık uçlu cevabını rubric'e göre değerlendiriyorsa bu **kullanıcıya verilen yardım** değildir.

İki ayrı kavram tutulur:

- `assistance provenance` — kullanıcı çözüm üretirken ne yardım aldı?
- `evaluator provenance` — sonucu kim/ne değerlendirdi?

AI evaluator'ın güvenilirliği, validator ve confidence politikası 4E / 14F / 9E'de ayrıca ele alınacaktır.

AI evaluator `mastered` state'ini tek başına keyfi biçimde set edemez; yalnız evidence üretimine/yorumuna katkı verir.

---

# 17. False-positive mastery guardrail'leri

2D sonrasında sistem aşağıdaki durumlara izin vermemelidir:

1. AI tam kodu yazdı + test geçti → kullanıcı coding mastered.
2. Full solution gösterildi + aynı soru hemen tekrar doğru → independent mastery.
3. Kullanıcı yalnız AI kodunu açıklayabildi → coding production mastered.
4. AI submit sonrası feedback verdi → önceki bağımsız attempt geriye dönük assisted sayıldı.
5. Compiler/documentation kullanıldı → her durumda otomatik mastery penalty.
6. Hint istedi → otomatik negative evidence.
7. External AI kullanımı kanıtsız tahmin edilip kullanıcı cezalandırıldı.
8. Partial AI artifact içindeki tüm Skill'ler kullanıcı tarafından yapılmış sayıldı.
9. Assessment sırasında full answer reveal sonrası aynı item güçlü evidence olmaya devam etti.
10. AI evaluator sonucu tek başına hard prerequisite'i bypass etti.

---

# 18. 2D'de bilinçli olarak kararlaştırılmayanlar

Aşağıdakiler sonraki adımlara bırakılır:

- H0/H1/H2/H3/H4 evidence weight'leri → **2E**
- minimum independent evidence sayısı/çeşitliliği → **2E**
- mastery threshold ve confidence → **2E**
- assistance sonrası mastery'nin tam kaç puan değişeceği → **2E**
- spaced repetition / forgetting etkisi → **2F**
- weekly/monthly exam allowed-help politikası → **4A–4C**
- task/question metadata schema → **4D / 9C**
- AI-generated item validation → **4E**
- provider/model seçimi → **9E**
- AI Tutor UI ve gerçek prompt davranışları → **14A–14G**
- code runner/compiler entegrasyonu → **9E / 14D**

---

# 19. 2D kabul kriterleri

2D tamamlanmış kabul edilir çünkü:

- assistance level taxonomy H0–H4 tanımlandı,
- assistance timing ayrı boyut olarak tanımlandı,
- artifact authorship/provenance ayrıldı,
- independent / assisted / practice-only / requires-recheck evidence yorumları tanımlandı,
- full solution sonrası fresh/unseen recheck zorunluluğu kilitlendi,
- AI-generated code'un kullanıcı coding mastery'si sayılmaması kesinleştirildi,
- submit sonrası AI feedback'in önceki attempt'i geriye dönük kirletmemesi tanımlandı,
- comprehension evidence ile production evidence ayrıldı,
- compiler/test/docs/autocomplete kullanımının objective-specific tool policy'ye bağlı olması kararlaştırıldı,
- yardım istemenin tek başına negative mastery olmadığı kilitlendi,
- external AI için surveillance/cheat-detection yaklaşımı reddedildi,
- teaching/practice ve assessment mode ayrıldı,
- AI evaluator ile AI helper provenance ayrıldı,
- exact ağırlık/threshold değerleri 2E'ye bırakıldı.

---

# 20. Sıradaki adım

## 2E — Mastery formülü v0

2E'de Research AI kullanılarak:

- evidence aggregation yaklaşımı,
- objective → skill hesaplama,
- minimum independent/diverse evidence gate'leri,
- mastery threshold,
- assistance etkisinin sayısal/kurallı karşılığı,
- confidence,
- false-positive / false-negative dengesi

araştırılacak, ardından deterministik Mastery Formula v0 yazılacaktır.

---

**14E note (2026-10-02, `D-109`):** §8 scenario A is now in code: work the learner says was generated or copied is `requires_independent_recheck` on a task that measures them (practice in a teaching task), so the mastery engine opens the recheck. §9's comprehension questions are offered right after such a submission and are skippable; a written check is judged by its answer key and is evidence of its own declared type, never of the item's production (scenario E); a tutor-written question is practice and never evidence. Details: `docs/CODE_COMPREHENSION_IMPL_SPEC.md`.
