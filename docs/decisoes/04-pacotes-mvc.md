# 4. Pacotes no padrão MVC

[Índice](../DECISOES.md) · anterior: [3. Métodos do CRUD](03-metodos-do-crud.md) · próxima: [5. Validação](05-validacao.md)

**O que decidimos:** o código fica dividido em `controller`, `service`, `repository`, `entity`, `dto` e `mapper`, e cada um é separado em `jpa` (Postgres) e `mongo` (MongoDB). O controller só recebe a requisição e devolve a resposta, a regra de negócio fica no service e o acesso ao banco fica no repository. A entity nunca sai pela API: a resposta é sempre um DTO `record`, e quem converte é o MapStruct (4.1). O service é uma classe só, sem interface.

**Por quê:** cada camada tem um motivo só para mudar. Se a rota muda, mexe no controller, e se a regra muda, mexe no service. Não devolver a entity evita mandar campo que não pode sair, como o hash da senha, e evita o Jackson tentar ler uma relação que o Hibernate ainda não carregou.

**O que descartamos:** o par `XService` e `XServiceImpl`. Com uma implementação só, a interface não ajuda em nada e só deixa mais arquivo para ler, e o Mockito já consegue fazer mock da classe direto nos testes.

**Onde ver:**
- [`com/venus/crud/`](https://github.com/Venus-System/Venus-CRUD/tree/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud): os pacotes
- [`ProductService.java`, linha 27](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/product/ProductService.java#L27): service sem interface

## 4.1 Por que o MapStruct

**O que decidimos:** toda conversão entre entity e DTO é feita por um mapper do MapStruct. O mapper é só uma interface, e o MapStruct gera a classe que faz a conversão na hora de compilar. Os 41 mappers usam a mesma configuração, a `VenusMapperConfig`: todos viram bean do Spring (`componentModel = "spring"`) e o build dá erro se algum campo ficar sem mapear (`unmappedTargetPolicy = ERROR`). No `pom.xml`, o processador do MapStruct roda junto com o do Lombok e com o `lombok-mapstruct-binding`, para o MapStruct enxergar os getters e setters que o Lombok gera.

**Por quê:** cada mapper converte um DTO de request, um de patch e um de response, e escrever essas conversões na mão seria centenas de linhas de `setX(getX())` repetidas. Como o código é gerado na compilação, ele é Java normal, sem reflexão na hora que roda, e dá para abrir a classe gerada para ver o que ela faz. Com o `ERROR`, quando alguém cria um campo novo e esquece de tratar no mapper, o build quebra na hora, em vez de o campo ficar vazio sem ninguém perceber.

**O que descartamos:** fazer a conversão na mão dentro do service, que misturaria a regra com cópia de campo, e o ModelMapper, que copia os campos por reflexão na hora que roda e deixa o campo vazio sem avisar quando o nome não bate.

**Onde ver:**
- [`VenusMapperConfig.java`, linhas 6 a 9](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/VenusMapperConfig.java#L6-L9): a configuração de todos os mappers
- [`pom.xml`, linhas 137 a 153](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/pom.xml#L137-L153): o MapStruct rodando junto com o Lombok

## 4.2 Os quatro métodos de cada mapper

**O que decidimos:** tirando o do scan, cada mapper tem quatro métodos: `toEntity` para o `POST`, `toResponse` para a resposta, `updateEntity` para o `PUT` e `patchEntity` para o `PATCH`. O `updateEntity` herda as regras do `toEntity` com `@InheritConfiguration`, e só o `patchEntity` usa o `NullValuePropertyMappingStrategy.IGNORE`, que pula o campo que veio `null`. Os métodos de escrita ignoram o `id`, o `createdAt` e o `updatedAt`, e os do usuário e do admin ignoram também o `passwordHash`.

**Por quê:** o `PUT` troca tudo, então um campo `null` no `PUT` apaga o valor, e no `PATCH` o `null` quer dizer "não mexe". Colocar o `IGNORE` só no `patchEntity` é o que deixa os dois funcionarem assim. O `id` e as datas quem preenche é o banco ([2.2](02-spring-data-jpa.md)), e a senha nunca é copiada pelo mapper: o service faz o hash com BCrypt antes de gravar.

**O que descartamos:** colocar o `IGNORE` na `VenusMapperConfig`, valendo para todos os mappers, que deixaria o `PUT` sem conseguir apagar um campo.

**Onde ver:**
- [`ReviewMapper.java`, linhas 22 a 43](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/mapper/jpa/review/ReviewMapper.java#L22-L43): os métodos de escrita, com o `@InheritConfiguration` na linha 34 e o `IGNORE` na linha 42
- [`UserMapper.java`, linha 21](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/mapper/jpa/user/UserMapper.java#L21): o `passwordHash` ignorado

## 4.3 Id da relação no request e nome curto do autor

**O que decidimos:** o request recebe só o id da relação (`userId`, `productVersionId`), e um método `default` do mapper transforma esse id numa entity só com o id preenchido. Na resposta da avaliação, o método `@Named("shortName")` monta o nome do autor com o primeiro nome e a inicial do último sobrenome: "Maria Souza Lima" vira "Maria L.".

**Por quê:** quem chama a API já tem o id, então não precisa mandar o objeto inteiro, e a entity só com o id já basta para o Hibernate gravar a FK. O nome curto é porque a avaliação aparece para todo mundo no app, e o nome completo do usuário não precisa aparecer. Como isso é só formatar a resposta, a regra ficou no próprio mapper.

**O que descartamos:** receber o objeto da relação inteiro no request, e criar uma classe nova só para montar o nome do autor.

**Onde ver:**
- [`ReviewMapper.java`, linhas 59 a 66](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/mapper/jpa/review/ReviewMapper.java#L59-L66): o id virando entity
- [`ReviewMapper.java`, linhas 45 a 57](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/mapper/jpa/review/ReviewMapper.java#L45-L57): o nome curto do autor

## 4.4 Onde fica cada regra de negócio

**O que decidimos:** cada regra fica numa camada, conforme o tipo dela:

- **Formato do dado** fica no DTO, com o Bean Validation ([seção 5](05-validacao.md)): campo obrigatório, tamanho máximo, e-mail válido.
- **Regra do sistema** fica no service. Por exemplo: o usuário que já avaliou uma versão do produto não pode avaliar de novo, só dá para aprovar ou recusar um scan que está em `PENDING_REVIEW`, e só o `ADMIN` e o `MODERATOR` aprovam ou recusam scan, o `ANALYST` não.
- **Regra que já é do banco** fica no banco, e a API só usa: a procedure que não deixa ativar nem desativar usuário bloqueado ou pendente ([seção 8](08-procedures-e-functions.md)), e o trigger que deixa só uma versão atual por produto, desmarcando a anterior quando entra uma nova.
- **A constraint do banco** fica como segunda barreira da regra do service: a avaliação repetida também é barrada pela unique de usuário e versão na tabela `reviews`.

**Por quê:** cada regra fica no lugar que muda junto com ela. O formato é parte do contrato da rota, a regra do sistema muda quando o negócio muda, e a regra do banco é do time de banco e vale para qualquer API que grava nele, não só para o CRUD. No service, a regra volta com uma mensagem clara, como "O usuario 7 ja avaliou a versao de produto 3", e a unique garante o dado certo quando duas requisições chegam juntas e as duas passam pela checagem do service.

**O que descartamos:** repetir no Java uma regra que já está no banco, como desmarcar a versão anterior no service, e deixar a regra do sistema só para a constraint, que devolveria um erro sem dizer qual regra foi quebrada.

**Onde ver:**
- [`ReviewService.java`, linhas 128 a 136](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/review/ReviewService.java#L128-L136): a avaliação repetida barrada no service
- [`00_schema.sql` do Venus-Banco, linha 764](https://github.com/Venus-System/Venus-Banco/blob/ce343fe6795773a68827939cbcd2c40386d630b2/sql/00_schema.sql#L764): a unique de usuário e versão na tabela `reviews`
- [`ScanReviewService.java`, linhas 90 a 95](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewService.java#L90-L95): o scan só aprovado ou recusado em `PENDING_REVIEW`
- [`ScanReviewValidator.java`, linha 52](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanReviewValidator.java#L52): os papéis que podem aprovar ou recusar scan
- [`00_schema.sql` do Venus-Banco, linhas 993 a 1008](https://github.com/Venus-System/Venus-Banco/blob/ce343fe6795773a68827939cbcd2c40386d630b2/sql/00_schema.sql#L993-L1008): a function do trigger que deixa só uma versão atual
- [`00_schema.sql` do Venus-Banco, linhas 1347 a 1350](https://github.com/Venus-System/Venus-Banco/blob/ce343fe6795773a68827939cbcd2c40386d630b2/sql/00_schema.sql#L1347-L1350): o trigger ligado na tabela `product_versions`
