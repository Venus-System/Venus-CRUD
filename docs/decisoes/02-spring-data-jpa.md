# 2. Spring Data JPA

[Índice](../DECISOES.md) · anterior: [1. API REST e Postgres](01-api-rest-e-postgres.md) · próxima: [3. Métodos do CRUD](03-metodos-do-crud.md)

**O que decidimos:** cada tabela do schema `venus` tem a sua entity e o seu repository do Spring Data JPA, e o Hibernate roda com `ddl-auto: validate`. As buscas com paginação devolvem `Slice` em vez de `Page`.

**Por quê:** o banco é do time de banco, que cuida dele no repositório Venus-Banco junto com as functions, procedures e triggers. Com o `validate`, a API não cria nem altera tabela nenhuma, ela só confere se as entities batem com o banco. Se alguma coluna mudar, a API nem sobe, aí a gente descobre na hora e não depois com dado errado. Isso já aconteceu quando a coluna `hair_type` virou `hair_pattern`. O `Slice` é porque o `Page` faz um `COUNT(*)` a mais em toda busca para contar o total, e o `Slice` só diz se tem uma próxima página.

**O que descartamos:** o `ddl-auto: update`, que deixaria a API mexer no banco de outro time, e o `Page` nas buscas. Pelo mesmo motivo descartamos o `Specification` do Spring, que só devolve `Page`.

**Onde ver:**
- [`application.yml`, linha 22](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/resources/application.yml#L22): `ddl-auto: validate`
- [`entity/`](https://github.com/Venus-System/Venus-CRUD/tree/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity): as entities, uma pasta por domínio
- [`ProductRepository.java`, linha 37](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/jpa/product/ProductRepository.java#L37): busca devolvendo `Slice`

## 2.1 Enums em minúsculo no banco

**O que decidimos:** os enums do banco são em minúsculo (`product_photo`, `useful`) e os do Java são em maiúsculo (`PRODUCT_PHOTO`, `USEFUL`). Quem troca de um para o outro é o `LowercaseEnumConverter`. Cada enum gravado no banco tem um conversor que só herda dele, são 34 no total, todos com `autoApply = true` para valer em todas as entities sem anotar campo por campo. E a URL do banco tem o `stringtype=unspecified`.

**Por quê:** o `@Enumerated(EnumType.STRING)`, que é o padrão do JPA, grava o nome do jeito que está no Java, em maiúsculo, e o banco recusa, por conta que o tipo enum dele só aceita os valores em minúsculo. Além disso, o driver do Postgres manda todo texto como `varchar`, e o Postgres não converte `varchar` sozinho para o tipo enum da coluna. Com o `stringtype=unspecified`, o texto vai sem tipo e o próprio Postgres converte para o enum certo.

**O que descartamos:** o `@Enumerated(EnumType.STRING)`, e escrever os enums do Java em minúsculo, que fugiria do padrão de nome do Java.

**Onde ver:**
- [`LowercaseEnumConverter.java`, linhas 13 a 21](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity/converter/LowercaseEnumConverter.java#L13-L21): a troca entre maiúsculo e minúsculo
- [`AgeRangeConverter.java`, linha 6](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity/converter/AgeRangeConverter.java#L6): um dos conversores, com o `autoApply`
- [`application.yml`, linha 13](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/resources/application.yml#L13): o `stringtype=unspecified` na URL

## 2.2 Datas de criação e de atualização feitas pelo banco

**O que decidimos:** o `created_at` e o `updated_at` ficam nas classes base `BaseEntity` e `AuditableEntity`, com `insertable = false`, `updatable = false`, o `@Generated` do Hibernate e sem setter.

**Por quê:** quem preenche essas datas é o banco: o `created_at` pelo `DEFAULT now()` e o `updated_at` pelo trigger do time de banco. Então a API nunca manda essas colunas, e o `@Generated` faz o Hibernate ler o valor que o banco gravou, aí a resposta do `POST` e do `PUT` já volta com a data certa. Sem setter, nenhum service consegue mudar essas datas por engano.

**O que descartamos:** o `@CreationTimestamp` e o `@UpdateTimestamp` do Hibernate, que usariam a hora do servidor da API e repetiriam no Java uma regra que já é do banco.

**Onde ver:**
- [`BaseEntity.java`, linhas 27 a 30](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity/shared/BaseEntity.java#L27-L30): o `created_at`
- [`AuditableEntity.java`, linhas 19 a 22](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity/shared/AuditableEntity.java#L19-L22): o `updated_at`

## 2.3 Campos calculados com `@Formula`

**O que decidimos:** três campos das respostas são calculados por uma subconsulta dentro do próprio `SELECT` da entity, com o `@Formula` do Hibernate: a primeira foto da versão atual do produto (`imageUrl`), a capa da lista (`coverUrl`) e a quantidade de votos úteis da avaliação (`usefulVotes`).

**Por quê:** o app precisa desses valores nas listagens. Sem eles, para mostrar a foto de cada produto o app teria que chamar o `/full` de cada um. Com o `@Formula`, o valor já vem na mesma consulta da lista, sem coluna nova no banco do outro time e sem classe nova na API.

**O que descartamos:** uma coluna nova no banco, que o time de banco teria que criar e que precisaria ser atualizada a cada foto ou voto novo, e uma consulta separada para cada item da lista.

**Onde ver:**
- [`Product.java`, linhas 48 a 52](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity/product/Product.java#L48-L52): a foto do produto
- [`UserList.java`, linhas 46 a 48](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity/user/UserList.java#L46-L48): a capa da lista
- [`Review.java`, linhas 51 e 52](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/entity/review/Review.java#L51-L52): os votos úteis

## 2.4 `open-in-view` desligado e relação carregada com `@EntityGraph`

**O que decidimos:** o `open-in-view` fica desligado, e quando a resposta precisa de um dado de outra tabela o repository traz a relação junto com `@EntityGraph`. As avaliações, por exemplo, já vêm com o usuário para montar o nome do autor.

**Por quê:** com o `open-in-view` ligado, que é o padrão do Spring Boot, a conexão com o banco fica presa até a resposta terminar de ser escrita, e com um pool de 3 conexões ([1.1](01-api-rest-e-postgres.md)) isso faz falta. Desligado, a conexão volta para o pool quando o service termina. E sem o `@EntityGraph`, cada avaliação da lista faria mais uma consulta para buscar o usuário, aí uma página de 20 avaliações virava 21 consultas. Com o `@EntityGraph`, o usuário vem no mesmo `SELECT`, com um `JOIN`.

**O que descartamos:** deixar o `open-in-view` ligado, e colocar `FetchType.EAGER` na entity, que traria o usuário em toda consulta de avaliação, mesmo quando a resposta não usa.

**Onde ver:**
- [`application.yml`, linha 27](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/resources/application.yml#L27): o `open-in-view: false`
- [`ReviewRepository.java`, linhas 19 e 20](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/jpa/review/ReviewRepository.java#L19-L20): as avaliações vindo com o usuário

## 2.5 Buscas com todos os filtros juntos e sem acento

**O que decidimos:** cada busca com mais de um filtro tem uma `@Query` só, em que cada filtro vale quando vem preenchido: `(:filtro is null or condição)`. Nos filtros de texto e de enum, a checagem é `cast(:param as String) is null`. No nome do produto e do ingrediente, a comparação usa `unaccent(lower(...))` dos dois lados.

**Por quê:** assim o site pode mandar nome, marca e categoria juntos e todos valem ao mesmo tempo, e um filtro novo é só uma linha a mais na query. O `cast` é por conta do `stringtype=unspecified` (2.1): com ele, um texto `null` vai sem tipo, o Postgres não consegue descobrir o tipo e dá erro no `is null`, aí o `cast` diz que é texto. O `unaccent` faz quem digita "serum" achar o "Sérum". Em volta dele também tem um `cast(... as String)`, por conta que o Hibernate não conhece a function `unaccent` e sem isso não deixa usar o `like` com ela.

**O que descartamos:** um método do repository para cada combinação de filtros, que com 3 filtros já seriam 8 métodos, o `Specification` do Spring, que só devolve `Page`, e registrar a `unaccent` no Hibernate, que pedia uma classe nova e um arquivo de configuração só para isso.

**Onde ver:**
- [`ProductRepository.java`, linhas 17 a 38](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/jpa/product/ProductRepository.java#L17-L38): a busca de produtos com os filtros juntos
- [`ProductRepository.java`, linhas 19 a 21](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/jpa/product/ProductRepository.java#L19-L21): o `cast` e o `unaccent` no filtro do nome
