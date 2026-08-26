---
type: workflow
status: active
---

# Knowledge Base Workflow

## Ingest → compile → query → improve

1. Yeni materyal `vault/inbox/` içine alınır.
2. Değişmeden saklanacak kopya `vault/raw/` altında tutulur.
3. Kaynak için `[[vault/templates/Source Note|Source Note]]` ile provenance ve özet yazılır.
4. Kavram makaleleri `wiki/concepts/` altında kaynak notlarına bağlanır.
5. Araştırma sorusu `queries/` altında çalışılır; çıktı `outputs/` altına yazılır.
6. Kalıcı bulgular ilgili kavram notlarına geri bağlanır.
7. `health-checks/` ile kırık bağlantı, kaynaksız iddia ve çelişki kontrol edilir.

## Existing corpus

İlk corpus: [[vault/wiki/mocs/Canonical Document Map|AI Infra Learning Coach canonical documents]].
