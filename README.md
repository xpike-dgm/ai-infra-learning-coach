# AI Infra Learning Coach

Kişisel kullanım için tasarlanan adaptif öğrenme ve kariyer koçu mobil uygulaması.

## Yeni Sohbet / Yeni Agent İçin

Bu proje uzun süreli olduğu için sohbet geçmişine güvenilmez. Projeyi devralacak yeni ChatGPT sohbeti veya agent **önce** şu dosyayı okumalıdır:

**`docs/START_HERE.md`**

Ardından orada belirtilen sırayla uzun proje amacı, güncel handoff durumu, kararlar, master plan ve ilerleme günlüğü okunmalıdır.

En önemli kalıcı dosyalar:

- `docs/START_HERE.md` — yeni sohbet için başlangıç ve okuma sırası
- `docs/PROJECT_MASTER_CONTEXT.md` — projenin uzun ve ayrıntılı amacı/felsefesi
- `docs/HANDOFF_STATE.md` — şu anda nerede kaldık ve sıradaki kesin adım
- `PROJECT_CONTEXT.md` — temel proje hafızası
- `docs/DECISIONS.md` — kabul edilmiş kalıcı kararlar
- `docs/MASTER_PLAN.md` — aşama ve alt adım bazlı geliştirme planı
- `docs/PROGRESS_LOG.md` — kronolojik ilerleme kaydı

## Amaç

Kullanıcıya uzun bir kurs listesi vermek yerine, her gün o gün ne çalışması gerektiğini net biçimde söyleyen; öğrenme kanıtlanmadıkça ilerleme saymayan; günlük mini değerlendirmeler, haftalık sınavlar ve aylık yeterlilik sınavlarına göre sonraki çalışma planını otomatik yeniden düzenleyen bir sistem geliştirmek.

Ana kariyer rotası:

**A0 İngilizce + C/Linux → C++/Systems → Concurrency/Networking → Distributed Systems → GPU Architecture → CUDA/Triton → LLM Inference → AI Infrastructure**

## Temel Ürün İlkesi

> Zaman geçirmek ilerleme değildir. Yalnızca ölçülüp kanıtlanmış öğrenme ilerlemedir.

Bu nedenle uygulamada kullanıcıya `Gün 47 / 1095` gibi bir ilerleme sayacı gösterilmez. Yaklaşık 3 yıllık hedef yalnızca arka planda planlama ufku olarak kullanılabilir; gerçek ilerleme konu hakimiyeti, sınav performansı, uygulamalı görevler, gecikmeli tekrarlar ve proje başarısı üzerinden ölçülür.

## Ürün Karakteri

- Tek kullanıcı / kişisel kullanım.
- Güzel, modern ve sade mobil arayüz.
- Gereksiz kullanıcı yönetimi, sosyal özellik, abonelik, ödeme, admin paneli veya kurumsal güvenlik katmanları yok.
- Ana ekranın odağı: **Bugün ne yapmalıyım?**
- Müfredat sabit takvim değil, prerequisite ilişkileri olan bir bilgi haritasıdır.
- Haftalık ve aylık sınav sonuçları gelecekteki programı değiştirir.
- İngilizce teknik eğitimle paralel yürür; ayrı bir ön koşul değildir.

## Diğer Dokümantasyon

- `docs/PRODUCT_VISION.md` — ürün vizyonu ve kullanıcı deneyimi
- `docs/LEARNING_ENGINE.md` — mastery/adaptive learning mantığı
- `docs/CURRICULUM.md` — teknik müfredat omurgası
- `docs/ENGLISH_TRACK.md` — A0→B2 paralel İngilizce rotası
- `docs/RESEARCH_NOTES.md` — araştırma sonuçları ve uyarılar
- `docs/TODO.md` — yakın dönem yapılacaklar

> Not: Bu repo şu an ürün planlama ve kalıcı proje hafızası aşamasındadır. Kodlama mimarisi ve teknoloji seçimi ayrıca kararlaştırılacaktır.
