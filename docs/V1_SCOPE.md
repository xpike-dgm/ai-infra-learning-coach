# V1 Scope — AI Infra Learning Coach

**Adım:** 1B — V1 kapsamı  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Kapsam genişletme notu:** 4+ yıllık professional curriculum hedefi V1 release kapsamından ayrıdır; ayrıntı `docs/PROFESSIONAL_READINESS_TARGET.md`.

Bu belge, ilk gerçek release sürümünün hangi yetenekleri içereceğini ve hangi alanların bilinçli olarak sonraya bırakılacağını kilitler.

## V1 kapsam ilkesi

V1'in amacı olabildiğince fazla özellik toplamak değildir. V1, ürünün ana vaadini uçtan uca gerçek biçimde çalıştırmalıdır:

> Kullanıcı uygulamayı açar → bugün ne çalışacağını görür → çalışma görevlerini tamamlar → uygulama gerçekten öğrenip öğrenmediğini ölçer → mastery/retention/prerequisite durumu güncellenir → sonraki plan performansa göre değişir.

Bir özellik bu ana döngüyü doğrulamak veya günlük kullanımı güvenilir hale getirmek için gerekmiyorsa V1'e zorunlu olarak alınmayacaktır.

Yeni uzun vadeli hedef professional readiness olsa da V1, tüm 4+ yıllık içeriğin bitmesini beklemez. V1 motorun gerçek çalıştığını ve ilk production curriculum paketinin güvenilir olduğunu kanıtlayan release'tir.

---

# V1'DE KESİN OLACAKLAR

## 1. Tek kullanıcı ve kişisel kullanım
- Android odaklı kişisel mobil uygulama.
- Hesap açma zorunluluğu yok.
- Çok kullanıcılı SaaS mimarisi yok.
- Kullanıcının başlangıç teknik seviyesi ve İngilizce seviyesi yerel profilde tutulur.
- Günlük çalışma süresi ve temel tercihler ayarlanabilir.

## 2. Today / Bugünkü Çalışma ekranı
Ana ekranın birincil amacı `Bugün ne yapmalıyım?` sorusunu cevaplamaktır.

Kesin bulunacaklar:
- bugünkü toplam tahmini çalışma süresi,
- sıradaki görev,
- günün görev listesi,
- mevcut topic/skill,
- ilgili mastery durumu,
- yaklaşan retention/assessment uyarısı,
- büyük `Çalışmaya Başla / Devam Et` CTA,
- görevin neden bugün seçildiğine dair sade açıklama.

`Gün X / toplam gün` veya kariyerin yüzde kaçının tamamlandığı gibi sahte kesinlik oluşturan metrikler olmayacaktır.

## 3. Knowledge graph ve prerequisite sistemi
V1 curriculum sabit takvim olmayacaktır.

Sistem en az şunları destekleyecek:
- Domain / Module / Topic / Skill / Learning Objective yapısı,
- hard prerequisite,
- soft prerequisite,
- topic durumları,
- prerequisite başarısızsa bağımlı konuyu bekletme,
- bağımsız öğrenme dallarını devam ettirme.

İlk release için graph'ın tamamı 4+ yıllık professional curriculum olmak zorunda değildir; ilk gerçek 8–12 haftalık curriculum yüksek kalitede hazırlanacaktır. Veri modeli ileride tüm professional rota eklenebilecek kadar extensible olmalıdır.

## 4. Günlük adaptif planner
Planner en az şu sinyalleri dikkate alacaktır:
- mastery,
- zayıf skill/topic,
- due retention review,
- prerequisite durumu,
- günlük kullanılabilir süre,
- paralel English ihtiyacı,
- son assessment sonuçları,
- kaçırılmış günler.

Planner yeni konu, remediation, retention, branch blocking/continuation ve missed-day replan davranışlarını destekler.

## 5. Günlük çalışma akışı / Task Runner
V1 en az şu görev türlerini çalıştırabilmelidir:
- kısa lesson/anlatım,
- reading,
- uygulama/practice,
- quiz,
- coding,
- debugging,
- Feynman/kendi cümlesiyle açıklama,
- English,
- retention/retrieval.

Start / pause / resume / complete desteklenir; uygulama kapanırsa devam eden oturum mümkün olduğunca geri yüklenir.

## 6. Mastery Engine V1
Bir görev kartının tamamlanması tek başına ilerleme sayılmaz.

V1 mastery sistemi teori, coding, debugging, explanation, transfer ve delayed retention evidence'ını desteklemelidir. Kritik teknik beceriler yalnız kolay quiz ile `mastered` yapılamaz.

Canonical model GRE-v0'dır.

## 7. Günlük mikro değerlendirme
Daily assessment sabit quiz kotası değildir. Canonical model DMA-v0'dır; Objective'e uygun assessment, assistance/provenance/prerequisite guard ve evidence→replan davranışı desteklenir.

## 8. Haftalık sınav
V1'de haftalık assessment bulunur ve yalnız not üretmez; new learning, eski Skill'ler, coding/debugging, English, weakness ve planner değişikliği için evidence üretir.

## 9. Aylık yeterlilik sınavı
V1'de daha geniş comprehensive assessment bulunur. Teori, uygulama/coding, debugging, explanation, retention ve Technical English boyutları curriculum/planner'a geri beslenir.

## 10. Retention / spaced repetition
Mastered bir konu sonsuza kadar bitmiş kabul edilmez. Review scheduling, delayed evidence, verification ve planner entegrasyonu desteklenir. Canonical RVR-v0 davranışı korunur.

## 11. Remediation
V1 daha sade açıklama, alternatif örnek, micro-practice, debugging, prerequisite dönüşü ve fresh recheck gibi hedefli remediation yöntemlerini destekler.

## 12. AI Tutor V1
AI Tutor açıklama, hint, alternatif anlatım, root-cause feedback, code/open response feedback ve AI-assisted artifact sonrası comprehension/transfer kontrolü sağlayabilir. Core mastery/planner kurallarını keyfi değiştiremez.

## 13. Teknik İngilizce paralel hattı
İngilizce ilk günden teknik eğitimle paralel ilerler. İlk pakette A0 başlangıç, temel grammar/vocabulary, teknik vocabulary, compiler/terminal messages, README/docs okuma ve basit teknik yazma bulunur.

## 14. İlk 8–12 haftalık gerçek curriculum
V1 release için ilk 8–12 haftalık rota production kalitesinde olmalıdır.

İlk paket ağırlıklı olarak:
- Computer Fundamentals,
- C Foundations,
- Memory Foundations,
- Linux Foundations,
- başlangıç Data Structures,
- paralel A0→A1/A2 English

konularını içerir.

`8–12 hafta` sabit takvim değildir; içerik kapsam büyüklüğüdür.

## 15. Progress / Weakness görünümü
V1 en az domain/topic mastery, zayıf/güçlenen alanlar, retention risk/due durumu, assessment/remediation geçmişi ve `henüz başlamadı` ile `başarısız` ayrımını göstermelidir.

## 16. Local-first veri saklama
App restart sonrası progress korunur; curriculum ve user state ayrılır; migration desteklenir; AI servisi çalışmasa bile temel öğrenme verileri erişilebilir kalır.

## 17. Bildirimler ve günlük kullanım ayarları
Daily reminder, due retention, weekly exam, monthly exam ve temel çalışma süresi/profile/theme ayarları bulunur.

## 18. Modern ve profesyonel UI
V1 yalnız çalışan prototip değildir. Modern/sade UI, tutarlı typography/spacing, dark/light theme, loading/empty/error states, accessibility ve uygun micro-motion bulunur.

## 19. Backup / export / restore
Progress backup/export/restore ve migration sonrası veri koruma temel düzeyde bulunur.

---

# V1'DE BİLİNÇLİ OLARAK OLMAYACAK / SONRAYA BIRAKILACAKLAR

## 1. Tam 4+ yıllık professional curriculum
V1 release için Modern C++ → Systems → Distributed Systems → GPU/CUDA → Triton → LLM Inference → Multi-GPU / AI Infrastructure ve professional capstone katmanlarının bütün production içeriği hazırlanmayacaktır.

Uygulama motoru bu rotayı destekleyecek şekilde tasarlanır; curriculum daha sonra QA edilmiş paketler halinde genişletilir. Nihai kapsam `docs/PROFESSIONAL_READINESS_TARGET.md` ile tanımlanır.

## 2. Sosyal ve ticari özellikler
V1'de community, friend system, leaderboard, public profile, subscription/payment/store/admin/organization sistemi yoktur.

## 3. Bulut hesabı ve çok cihazlı canlı senkron
İlk release local-first'tür; cloud account/realtime multi-device sync zorunlu değildir.

## 4. iOS / web / desktop istemcisi
V1 Android odaklıdır.

## 5. Tam kariyer ve iş piyasası motoru
Canlı iş ilanı tarama, skill-gap matching, CV/company recommendation ve gelişmiş mock interview/career-readiness sistemi Aşama 19'a bırakılır.

## 6. Tam gelişmiş voice tutor
Realtime pronunciation/voice-first tutor V1 release şartı değildir.

## 7. Uygulama içine tam C/C++ compiler/sandbox gömmek
Telefon tam IDE olmak zorunda değildir. Coding task uygulamada verilebilir; kullanıcı bilgisayarda editor/terminal kullanıp artifact/result döndürebilir. Güvenli remote/local execution daha sonra değerlendirilebilir.

## 8. Aşırı gamification
XP economy, coin, loot, competitive leaderboard ve streak cezası ana odak değildir.

## 9. Tamamen LLM tarafından kontrol edilen curriculum
LLM curriculum/prerequisite/mastery kurallarını keyfi değiştiremez.

---

# V1 RELEASE TANIMI

Bir build'e `V1` diyebilmek için:
1. ilk 8–12 haftalık gerçek curriculum ile çalışmalı,
2. daily plan → task → assessment → mastery → replan döngüsünü uçtan uca işletmeli,
3. weekly/monthly assessment sonuçlarını gelecekteki plana yansıtmalı,
4. retention/remediation çalışmalı,
5. English parallel track daily planner içinde görünmeli,
6. kullanıcı verisi restart/update sonrası korunmalı,
7. AI Tutor olmadan temel local sistem çökmemeli,
8. ana akışlar gerçek Android cihazında bağımsız QA'dan geçmeli,
9. release APK kurulabilir olmalıdır.

V1 release **professional curriculum completion** anlamına gelmez. V1, profesyonel rotayı yıllar boyunca çalıştırabilecek ürün motorunun ilk güvenilir release'idir.

---

# 1B KABUL KONTROLÜ

1B'nin ana sınırları korunmuştur. 2026-08-24 kapsam genişletmesi yalnız uzun vadeli curriculum hedefini büyütmüştür:

- V1 ilk production curriculum paketiyle release edilir,
- tam 4+ yıllık professional curriculum V1 ön koşulu değildir,
- long-term professional-readiness hedefi Aşama 19 ve curriculum expansion üzerinden ilerler.

> **Kapsam genişletme notu — 2026-08-24:** Önceki `tam 3 yıllık curriculum V1 dışında` ifadesi, `tam 4+ yıllık professional curriculum V1 dışında` olarak güncellendi. V1 scope dar ve uygulanabilir kalırken nihai eğitim hedefi büyütüldü.
