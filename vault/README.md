# Personal Research Knowledge Base

Bu klasör, root `app` Obsidian vault'u içindeki, LLM tarafından sürdürülen araştırma bilgi alanıdır.

## Akış

1. Kaynakları `raw/` veya `inbox/` içine al.
2. Kaynak kayıtlarını `wiki/sources/` altında özetle ve birbirine bağla.
3. Kavramları `wiki/concepts/` altında atomik makalelere derle.
4. Soruları `queries/`, üretilen cevapları `outputs/` altında sakla.
5. `health-checks/` ile eksik bağlantı, çelişki ve güncellik kontrolleri yap.

`indexes/` klasörü vault'un hızlı giriş noktasıdır. `wiki/` içeriği kanonik bilgi, `raw/` ise değişmeden saklanan ham girdidir.

Obsidian'da root `app` klasörünü vault olarak aç. Root `.obsidian/` ayarları yeni notları `inbox/` alanına, ekleri de `assets/attachments/` alanına yönlendirir.

## LLM çalışma kuralı

Ham kaynağı değiştirme. Her iddia için kaynak bağlantısı ve mümkünse erişim tarihi yaz. Belirsiz bilgiyi `needs-review` etiketiyle işaretle; tahmini bilgiyi gerçekmiş gibi derleme.

## Sohbetler arası hafıza

Yeni bir agent sohbeti için [[vault/agent/SESSION_START|Session Start]] ile başla. Bu not, durable proje hafızasını hangi kaynaklardan ve hangi sırayla yeniden kuracağını tanımlar; kısa güncel briefing için [[vault/agent/CURRENT_CONTEXT|Current Context]] kullanılır.
