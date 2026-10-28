# 10. API REST em Java para o banco NoSQL

[Índice](../DECISOES.md) · anterior: [9. Segurança](09-seguranca.md)

**O que decidimos:** o MongoDB guarda as sessões de scan, e as rotas dele (`/api/scan-sessions`) ficam dentro do Venus-CRUD, em pacotes separados da parte do Postgres: `controller/mongo`, `service/mongo`, `repository/mongo`, `mapper/mongo`, `dto/mongo` e `document`.

**Por que o Mongo:** o scan é um documento com várias partes, como as fotos, o texto lido da frente e do verso, os ingredientes encontrados, a revisão do admin e a sincronização com o catálogo, e ele ganha campo novo a cada fase. No Mongo o campo novo entra sem migração, e o documento antigo só lê esse campo como vazio. O catálogo continua no Postgres, que é relacional.

**Por que o Mongo e o Cloudinary ficam na mesma API:** o scan junta os três. O documento do scan fica no Mongo, as fotos ficam no Cloudinary e, quando o admin aprova, o scan vira produto no Postgres (10.5). Com tudo na mesma API, aprovar é uma chamada só, que usa os repositories JPA do próprio CRUD e aproveita a foto da frente como foto do produto, com o mesmo `public_id` ([3.2](03-metodos-do-crud.md)). Do Mongo era pouca coisa, uma coleção só. E o Cloudinary depende de duas coisas que já estão no CRUD: o registro de cada imagem fica na `media_assets`, no Postgres, e quem pode subir cada imagem é decidido pela segurança da própria API. O avatar só o dono sobe, a capa só o dono da lista, e a foto de produto só o `MODERATOR` ou o `ADMIN` do painel.

**O que descartamos:** uma API só para o Mongo e um serviço separado só para as imagens. Cada um teria que repetir a segurança com os dois tokens, o deploy e o CI, a aprovação do scan ia virar chamadas HTTP entre as APIs, e o serviço de imagens ainda teria que gravar na `media_assets`, no mesmo banco do CRUD.

**Onde ver:**
- [`ScanSessionController.java`, linha 37](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/mongo/ScanSessionController.java#L37): as rotas do scan
- [`ScanSessionRepository.java`, linha 12](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/mongo/ScanSessionRepository.java#L12): o `MongoRepository`
- [`ScanSession.java`, linha 22](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/document/ScanSession.java#L22): o documento da coleção `scan_sessions`
- [`ScanCatalogSync.java`, linha 97](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanCatalogSync.java#L97): o scan aprovado indo para o catálogo no Postgres

## 10.1 Postgres e Mongo convivendo na mesma API

**O que decidimos:** cada módulo do Spring Data só procura repository na sua pasta: o JPA em `repository/jpa` e o Mongo em `repository/mongo`. No Mongo, as datas do scan são `OffsetDateTime`, iguais às do Postgres, e um conversor grava como data do Mongo, em UTC. Os enums do scan são gravados em minúsculo (`pending_review`), igual ao Postgres ([2.1](02-spring-data-jpa.md)). E o `created_at` e o `updated_at` do scan o Spring preenche sozinho, com a auditoria do Mongo.

**Por quê:** com os dois módulos do Spring Data no projeto, sem as pastas separadas o Spring tem que adivinhar de qual banco é cada repository. Com a pasta de cada um, cada repository fica com o seu banco sem dúvida. O driver do Mongo não sabe gravar `OffsetDateTime`, e gravar como data, e não como texto, deixa a fila do admin ordenar pela data (10.2). A auditoria precisou de um `DateTimeProvider` próprio por conta que o padrão do Spring gera a data sem fuso (`LocalDateTime`), e o campo do scan é `OffsetDateTime`. Os enums em minúsculo deixam os mesmos valores nos dois bancos.

**O que descartamos:** gravar a data como texto no Mongo, e deixar os enums do Mongo em maiúsculo, diferentes dos do Postgres.

**Onde ver:**
- [`JpaConfig.java`, linha 7](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/JpaConfig.java#L7): a pasta dos repositories do JPA
- [`MongoConfig.java`, linhas 24 e 25](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/MongoConfig.java#L24-L25): a pasta dos repositories do Mongo e a auditoria
- [`MongoConfig.java`, linhas 29 a 46](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/MongoConfig.java#L29-L46): os conversores e a data com fuso da auditoria
- [`OffsetDateTimeWriteConverter.java`, linha 13](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/document/converter/OffsetDateTimeWriteConverter.java#L13): a data gravada como data do Mongo
- [`ScanSession.java`, linhas 64 a 71](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/document/ScanSession.java#L64-L71): o `created_at` e o `updated_at` preenchidos pelo Spring

## 10.2 Reenvio do mesmo scan não duplica

**O que decidimos:** o app manda um `scanId` (UUID) que ele mesmo gera. A coleção tem um índice único no `scan_id`, que a própria API cria quando sobe, junto com os índices da fila do admin (status e data) e do histórico do usuário. Se o mesmo scan chega de novo, a API devolve o que já está gravado em vez de criar outro.

**Por quê:** o celular pode perder a conexão depois de enviar e mandar de novo sem saber se o primeiro chegou. A busca pelo `scanId` antes de gravar cobre o caso normal, e o índice único cobre dois envios chegando ao mesmo tempo: o segundo bate no índice (`DuplicateKeyException`), e a API devolve o primeiro.

**O que descartamos:** confiar só na busca antes de gravar, que deixaria passar dois envios ao mesmo tempo.

**Onde ver:**
- [`MongoIndexInitializer.java`, linhas 32 a 35](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/MongoIndexInitializer.java#L32-L35): o índice único do `scan_id`
- [`ScanSessionService.java`, linhas 90 a 93](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanSessionService.java#L90-L93): a busca antes de gravar
- [`ScanSessionService.java`, linhas 105 a 110](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanSessionService.java#L105-L110): o envio repetido devolvendo o primeiro

## 10.3 Ingredientes do scan comparados com o catálogo

**O que decidimos:** quando o scan chega, a API arruma o nome de cada ingrediente lido na foto: tira os espaços a mais e a pontuação do fim e deixa em maiúsculo. Depois procura todos os nomes de uma vez pelo nome INCI e, os que não achou, também de uma vez pelos apelidos do catálogo. Cada ingrediente fica com um status:

- `KNOWN`: achou um ingrediente pelo INCI;
- `ALIAS`: achou um pelo apelido;
- `AMBIGUOUS`: achou mais de um, e a API guarda os candidatos;
- `NEW`: não achou nenhum.

Na aprovação, o `KNOWN` e o `ALIAS` são ligados sozinhos, e o `AMBIGUOUS` e o `NEW` precisam de uma decisão do admin: ligar a um ingrediente que já existe (`LINK`), criar um novo (`CREATE`) ou descartar (`DISCARD`).

**Por quê:** o texto lido da foto vem com espaço e pontuação diferentes, e o rótulo às vezes usa um nome que não é o INCI, por isso a busca pelo apelido. Buscar todos de uma vez faz uma lista de 30 ingredientes custar no máximo 2 consultas, e não 30 ou mais, o que pesa com o pool de 3 conexões ([1.1](01-api-rest-e-postgres.md)). E quando o nome aponta para mais de um ingrediente ou para nenhum, a API não escolhe sozinha, por conta que ligar o ingrediente errado muda a análise do produto.

**O que descartamos:** buscar um ingrediente por vez, e escolher sozinho um dos candidatos quando o nome é ambíguo.

**Onde ver:**
- [`IngredientNameNormalizer.java`, linhas 14 a 20](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/IngredientNameNormalizer.java#L14-L20): o nome arrumado
- [`IngredientMatcher.java`, linhas 42 a 60](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/IngredientMatcher.java#L42-L60): a busca de todos de uma vez, pelo INCI e depois pelo apelido
- [`IngredientMatcher.java`, linhas 93 a 104](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/IngredientMatcher.java#L93-L104): o status de cada ingrediente
- [`ScanReviewValidator.java`, linhas 176 a 194](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewValidator.java#L176-L194): a decisão do admin no `AMBIGUOUS` e no `NEW`

## 10.4 Dois admins revisando o mesmo scan

**O que decidimos:** o documento tem um campo `version` com `@Version`, a trava otimista do Spring Data. Se dois admins aprovam ou recusam o mesmo scan ao mesmo tempo, o segundo recebe 409 com a mensagem "O scan foi alterado por outra pessoa. Recarregue e tente de novo.". Quando a API sobe, todo documento sem `version` ganha o valor 0.

**Por quê:** sem a trava, a última gravação vale e apaga a decisão do outro admin, aí o produto podia ir para o catálogo e o scan ficar como recusado. O `version` 0 nos documentos que não têm o campo é porque, sem ele, o Spring acha que o documento é novo e a aprovação falha.

**O que descartamos:** deixar sem trava, com a última gravação valendo.

**Onde ver:**
- [`ScanSession.java`, linhas 33 a 35](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/document/ScanSession.java#L33-L35): o campo `version`
- [`ScanReviewService.java`, linhas 161 a 165](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewService.java#L161-L165): o 409 quando outro admin gravou antes
- [`ScanSessionVersionInitializer.java`, linhas 29 a 35](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/ScanSessionVersionInitializer.java#L29-L35): o `version` 0 nos documentos antigos

## 10.5 Sincronização com o catálogo na aprovação

**O que decidimos:** aprovar um scan faz duas coisas, nessa ordem. Primeiro grava no Mongo o status `APPROVED` junto com uma cópia do que o admin decidiu (`approvedSnapshot`). Depois grava no Postgres, numa transação só, o produto, a versão, os ingredientes e a foto. Se der certo, o scan fica `SYNCED`, com o id do produto e da versão. Se falhar, fica `SYNC_FAILED`, com o erro e o número de tentativas, e o `POST /api/scan-sessions/{id}/sync` tenta de novo a partir da cópia.

Para não duplicar nada, o produto é achado pelo `slug` (marca e nome, sem acento) e a versão pela assinatura da fórmula, que é um SHA-256 dos ids dos ingredientes na ordem do rótulo. Se a gravação bater numa unique do banco por conta de outro scan gravando o mesmo produto, a API tenta mais uma vez.

**Por quê:** o Mongo e o Postgres não têm uma transação juntos. Gravando a decisão no Mongo antes, ela não se perde se o Postgres falhar, e o `/sync` refaz a gravação sem o admin ter que decidir tudo de novo. O `slug` e a assinatura deixam a sincronização rodar de novo sem criar produto nem versão repetidos, e dois scans do mesmo produto com a mesma fórmula caem na mesma versão. A segunda tentativa é para quando dois scans do mesmo produto novo sincronizam juntos: um cria, o outro bate na unique, e na segunda vez ele já acha o produto criado.

**O que descartamos:** gravar no Postgres antes de salvar a decisão no Mongo, que perderia a decisão do admin se o Postgres falhasse no meio.

**Onde ver:**
- [`ScanReviewService.java`, linhas 51 a 63](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewService.java#L51-L63): a aprovação gravando a decisão antes de sincronizar
- [`ScanReviewService.java`, linhas 97 a 116](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewService.java#L97-L116): o `SYNCED` ou o `SYNC_FAILED`
- [`ScanReviewService.java`, linhas 118 a 127](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewService.java#L118-L127): a segunda tentativa quando bate numa unique
- [`ScanReviewService.java`, linhas 76 a 83](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewService.java#L76-L83): o `/sync`
- [`ScanCatalogSync.java`, linhas 96 a 121](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanCatalogSync.java#L96-L121): a gravação no Postgres numa transação só, reaproveitando a versão com a mesma fórmula
- [`ScanSyncKeys.java`, linhas 23 a 38](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanSyncKeys.java#L23-L38): o `slug` e a assinatura da fórmula
