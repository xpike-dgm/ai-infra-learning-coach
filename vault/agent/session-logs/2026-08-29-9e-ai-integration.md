---
type: session-log
status: completed
stage_step: 9E
model: AIAX-v0
decision: D-079
date: 2026-08-29
---

# 9E — AI Integration Architecture

9D merge edildikten sonra fresh 9E PRE main üzerinden yapıldı; üç ref koşulu doğrulandı ve beş kanonik kaynak `9D ✅ / 9E active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md`
- Machine-readable: `arch/9e_ai_integration/ai_integration.yaml`
- QA: `arch/9e_ai_integration/qa_report.yaml`
- Stale audit: `arch/9e_ai_integration/stale_reference_audit.yaml`
- Research/synthesis: `research/9e_ai_integration_research.md`
- Final: `AIAX-v0 / D-079`
- Independent QA: 86/86 PASS — 8 AI-may / 9 AI-may-never / 7 outcome / 18 forbidden pattern
- Stage 6 / Stage 7 / AŞAMA 8 / 9A / 9B / 9C / 9D / external-memory regressions: PASS

## Durable decisions
AI bir port arkasındaki yardımcıdır ve asla bir otorite değildir: `AI proposes. Deterministic engines decide.` Mastery ve retention yazamaz, prerequisite'i karşılayamaz veya aşamaz, planner priority/rank/capacity'yi değiştiremez, assessment quota koyamaz, "öğrendi" diyemez, kendi ürettiği item'ı doğrulayamaz, weakness'i tek başına confirmed yapamaz ve refusal/timeout/error'ı olumsuz sonuca çeviremez. Evaluator çıktısı schema-constrained'dir ve schema'ya uymayan yanıt bir hükümdür değil **hata**dır; serbest metinden hüküm ayrıştırmak yasaktır. Kalibre edilmemiş LLM değerlendirmesi `provisional`dır ve `verified` deterministik bir yol ister. Yedi sonuçlu taksonomi refusal'ı failure'dan ayırır ve her yanıtsızlık `evaluation_pending`e düşüp evidence yazmaz. Timeout bütçesi uçtan ucadır ve retry'ları kapsar. Model adı konfigürasyonda yaşar, core'da değil; adapter provider-independent'tır ve somut kimlikler 10A/14'te yeniden doğrulanır. Deterministik iş asla AI çağırmaz. APK'da hardcoded veya paylaşılan key yoktur ve V1'de backend proxy yoktur; öğrenci kendi key'ini girer ve key platform secure storage'da tutulur. Yalnız mevcut attempt için gereken asgari içerik cihazdan çıkar. Generated item untrusted girer ve generator ile validator ayrıdır. Her AI-türevli evidence satırı provider, model ve prompt/schema version kaydeder.

## Key tensions resolved
1. **Refusal bir yanlış cevap değildir.** Güvenlik sınıflandırıcıları reddedip normal bir yanıt döndürebilir; adapter her yanıtsızlığı başarısız değerlendirme sayarsa öğrenci kendi kod örneğinde bir filtre tetiklendiği için negative evidence alır. Bu entegrasyondaki en zarar verici yanlış okuma budur.
2. **Serbest metin ayrıştırma sessiz bir doğruluk tehlikesidir.** Bir misparse hata gibi değil hüküm gibi görünür; bu yüzden yanıt schema-constrained ve doğrulanmadığında reddedilir.
3. **Timeout bütçeleri göründükleri şey değildir.** SDK client'ları varsayılan olarak retry yapar; wall-clock timeout × deneme sayısına ulaşabilir, bu yüzden bütçe uçtan uca tanımlandı.
4. **Provider bağımsızlığı ile somut seçim.** §17 ikisini birden istiyor; model adı konfigürasyona konarak ve hiçbir kalıcı öğrenme semantiği ona bağlanmayarak çözüldü.
5. **Credential'ın gerçek bir tehdit modeli var.** APK'daki geliştirici key'i her kuruluma tek bir paylaşılan sır gönderir; kullanıcının kendi cihazındaki kendi key'i ise sırrı zaten elinde tutana aittir.
6. **Local-first uzak bir API ile karşılaşıyor.** Ürün başka türlü cihazdan hiç çıkmadığı için ne gönderilebileceği bir implementation detayı değil mimari karardır.
7. **AI gerçekten faydalıdır ve aşırı kısıtlanmamalıdır.** Amaç AI'ı azaltmak değil, yetkisini sınırlamaktır.

## Method note
Model ve API bilgileri hatırlamadan değil paketli `claude-api` skill referansından alındı; o referans da cache'li olduğu için implementation'da yeniden doğrulama kayıtlı bir gereksinim. Validator kararı kaynak kontrat metinlerine ve yaml'larına karşı doğrular (LEARNING_BEHAVIOR_RULES §17/§18 devri, AIV-v0 yetersizlik hükmü, TRUX/ASUX `evaluation_pending` evidence yasağı, MSBX null evaluator, DDM evaluator alanları, LFPS export içeriği) ve spec'in hiçbir somut model kimliğini kanonik yapmadığını algoritmik kontrol eder. Mutation test: 7 kasıtlı ihlal 7 check FAIL verdi.

## Next
9F — Test stratejisi is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
