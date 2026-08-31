# V1 Scope — AI Infra Learning Coach

**Adım:** 1B — V1 kapsamı  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Bağlayıcı:** D-041, D-042, D-044

Bu belge ilk gerçek release'in hangi yetenekleri içereceğini ve hangi alanların bilinçli olarak sonraya bırakılacağını kilitler.

## V1 kapsam ilkesi

V1'in amacı olabildiğince fazla özellik toplamak değildir. V1 ürünün ana vaadini uçtan uca gerçek biçimde çalıştırmalıdır:

> Kullanıcı uygulamayı açar → bugün ne çalışacağını görür → çalışır → sistem gerçekten öğrenip öğrenmediğini ölçer → mastery/retention/prerequisite durumu güncellenir → sonraki plan performansa göre değişir.

Yeni long-term hedef professional readiness olsa da V1 tüm 4+ yıllık content'i beklemez. V1 motorun gerçek çalıştığını ve ilk production curriculum paketinin güvenilir olduğunu kanıtlayan release'tir.

---

# V1'DE KESİN OLACAKLAR

## 1. Tek kullanıcı / kişisel kullanım
- Android odaklı kişisel mobil uygulama.
- Auth/payment/multi-tenant SaaS zorunluluğu yok.
- Başlangıç teknik ve English state local profilde tutulur.
- **D-080:** ürün hiçbir uygulama merkezine yüklenmeyecek ve halka açık paylaşılmayacaktır. Store yayın gereksinimleri, dağıtım imzalama seremonisi, güvenlik amaçlı obfuscation/pinning ve cihaz matrisi kapsam dışıdır; `minSdk` ve device QA tek hedef cihaza sabitlenir. Kanıt/evidence ve AI yetki kuralları bundan **etkilenmez**.
- Günlük çalışma süresi ayarlanabilir.

## 2. Today / Bugünkü Çalışma
Ana ekran `Bugün ne yapmalıyım?` sorusunu cevaplar.

En az:
- bugünkü çalışma süresi,
- sıradaki görev,
- görev listesi,
- current topic/skill,
- mastery/retention durumu,
- büyük başlat/devam CTA,
- görevin neden seçildiği

gösterilir.

`Gün X / toplam gün` veya career-completion yüzdesi ana metric değildir.

## 3. Knowledge graph / prerequisite
V1 en az:
- `Domain → Module → Topic → Skill → Learning Objective`,
- hard/soft prerequisites,
- Topic states,
- dependent branch blocking,
- independent branch continuation

destekler.

D-044 gereği V1'in gerçek content node'ları, yalnız `Python` / `C` gibi broad labels değil, mümkün olduğunca weakness-addressable canonical Skill/Objective IDs kullanır.

Full 4+ year graph data V1'de tamamlanmak zorunda değildir; schema/backbone tüm rota için extensible olmalıdır.

## 4. Günlük adaptif planner
Planner mastery, granular weakness, retention, prerequisite, daily capacity, English, assessment ve missed-day state'i dikkate alır.

## 5. Task Runner
En az:
- lesson/explanation,
- reading,
- practice,
- quiz,
- coding,
- debugging,
- explanation/Feynman,
- English,
- retention/retrieval

görevleri çalıştırabilir.

Pause/resume ve app restart sonrası session recovery desteklenir.

## 6. Mastery Engine V1
Task completion mastery değildir. Teori, coding, debugging, explanation, transfer ve delayed retention evidence desteklenir. Canonical model GRE-v0'dır.

## 7. Günlük mikro değerlendirme
DMA-v0 kullanılır. Sabit günlük quiz kotası değildir; Objective-matched evidence ve assistance/provenance/prerequisite safety uygulanır.

## 8. Haftalık assessment
Yalnız not üretmez; Skill/Objective evidence ve sonraki planner değişikliği üretir.

## 9. Aylık assessment
Daha geniş theory/application/debugging/explanation/retention/English evidence üretir ve curriculum state'e geri beslenir.

## 10. Retention
RVR-v0 davranışı korunur. Time mastery'yi otomatik düşürmez; delayed verification ve planner entegrasyonu vardır.

## 11. Remediation
Remediation broad Domain'i kör tekrar ettirmez. D-044 doğrultusunda mümkün olduğunca exact weak Skill/Objective'e hedeflenir: simpler explanation, alternative example, micro-practice, debugging, prerequisite repair, fresh recheck.

## 12. AI Tutor V1
AI açıklama/hint/alternative explanation/root-cause/code/open-response feedback sağlayabilir; canonical mastery/planner state'i keyfi değiştiremez.

## 13. Technical English parallel line
English ilk günden paralel ilerler. İlk paket A0 başlangıç, temel grammar/vocabulary, technical vocabulary, compiler/terminal messages, README/docs reading ve basic writing içerir.

## 14. İlk 8–12 haftalık production curriculum
D-042/D-044 sonrası ilk paket ağırlıklı olarak:
- Computer / Programming Fundamentals,
- **Python Foundations**,
- C Foundations,
- Memory Foundations,
- Linux + Git + Shell Foundations,
- başlangıç DS&A,
- parallel A0→A1/A2 English

içerir.

`8–12 hafta` sabit takvim değil içerik kapsam büyüklüğüdür. Gerçek content AŞAMA 15'te, AŞAMA 6 granular map'ine bağlı olarak üretilir.

## 15. Progress / Weakness görünümü
V1 en az:
- broad Domain/Topic derived summary,
- granular weak/strong Skill görünümü,
- retention/verification state,
- assessment/remediation history,
- `not_started` ile `failed/weak` ayrımı

gösterebilmelidir.

## 16. Local-first persistence
Restart/update sonrası progress korunur; curriculum data ile user state ayrılır; migration desteklenir.

## 17. Bildirim / ayarlar
Daily reminder, retention due, weekly/monthly assessment ve çalışma süresi/profile/theme ayarları bulunur.

## 18. Modern/professional UI
Tutarlı typography/spacing, dark/light, loading/empty/error states, accessibility ve uygun motion bulunur.

## 19. Backup / export / restore
Temel progress backup/export/restore ve migration veri koruması bulunur.

---

# V1'DE BİLİNÇLİ OLARAK OLMAYACAK / SONRAYA BIRAKILACAKLAR

## 1. Tam 4+ yıllık professional curriculum
Modern C++ → systems → distributed → GPU/CUDA → Triton → inference/serving → multi-GPU/AI Infrastructure → OSS → professional capstone production content'inin tamamı V1 ön koşulu değildir.

AŞAMA 6 full capability map'i tasarlar; **AŞAMA 20** full professional content expansion'ı üretir.

## 2. Sosyal/ticari özellikler
Community/friends/leaderboard/public profile/subscription/payment/admin yoktur.

## 3. Realtime multi-device cloud sync
V1 local-first'tür.

## 4. iOS/web/desktop istemcileri
V1 Android odaklıdır.

## 5. Tam kariyer / canlı iş piyasası motoru
Continuous job scraping, ATS optimization, company recommendation ve comprehensive career engine V1 şartı değildir; uzun vadeli AŞAMA 20 career-readiness katmanında değerlendirilebilir.

## 6. Full voice-first tutor
V1 şartı değildir.

## 7. Telefona tam C/C++ IDE/sandbox gömmek
Mobil uygulama full workstation değildir; coding artifact PC/editor/terminal ile üretilebilir.

## 8. Aşırı gamification
XP economy/coin/loot/competitive leaderboard/streak cezası ana odak değildir.

## 9. Tamamen LLM kontrollü curriculum
LLM prerequisite/mastery/planner/curriculum source of truth değildir.

---

# V1 RELEASE TANIMI

Bir build'e `V1` denebilmesi için:
1. ilk 8–12 haftalık production curriculum ile çalışmalı,
2. daily plan → task → assessment → mastery → replan döngüsü uçtan uca çalışmalı,
3. weekly/monthly assessment gelecek planı değiştirmeli,
4. retention/remediation çalışmalı,
5. English parallel track planner içinde görünmeli,
6. granular Skill weakness doğru attribution ile saklanıp hedefli remediation üretebilmeli,
7. user data restart/update sonrası korunmalı,
8. AI Tutor yokken deterministic local core çökmemeli,
9. kritik akışlar gerçek Android cihazında bağımsız QA'dan geçmeli,
10. release APK kurulabilir olmalı.

V1 release **professional curriculum completion** anlamına gelmez.

---

# 1B KABUL KONTROLÜ

1B'nin ana sınırları korunmuştur. D-041 long-term hedefi, D-042 Python foundation'ı ve D-044 granular diagnosis/content mapping'i netleştirmiştir; full professional content hâlâ V1'e yüklenmez.
