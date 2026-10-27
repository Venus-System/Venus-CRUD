# Justificativa das decisões de Desenvolvimento 2

Aqui está o porquê das decisões que a gente tomou no Venus-CRUD para cada requisito de Desenvolvimento 2. Cada decisão diz o que a gente decidiu, por quê, o que a gente descartou e onde ver no código. Os links levam para a linha exata num commit fixo, então eles não mudam quando o código andar.

## 1. API REST em Java com Spring MVC acessando o Postgres

**O que decidimos:** fazer a API com Spring Boot 3.3.4 e Spring MVC, em Java 21, com as virtual threads ligadas. Os dados ficam no Postgres, no schema `venus`.

**Por quê:** o Spring MVC é o que a matéria pede e é o jeito mais simples de fazer uma API REST que fala com banco relacional. Como o JPA fica esperando o banco responder, a gente ligou as virtual threads, por conta que assim cada requisição esperando o Postgres não segura uma thread do servidor.

**O que descartamos:** o WebFlux, que é reativo e não combina com o JPA, aí a gente teria que trocar todo o acesso ao banco por um driver reativo. Também descartamos espalhar `CompletableFuture` e `@Async` pelos services, que as virtual threads já resolvem sem mudar o código.

**Onde ver:**
- [`pom.xml`, linha 10](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/pom.xml#L10): Spring Boot 3.3.4
- [`pom.xml`, linha 21](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/pom.xml#L21): Java 21
- [`pom.xml`, linha 30](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/pom.xml#L30): Spring MVC (`spring-boot-starter-web`)
- [`application.yml`, linhas 8 a 10](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/resources/application.yml#L8-L10): virtual threads
- [`application.yml`, linha 13](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/resources/application.yml#L13): conexão com o Postgres

## 2. Spring Data JPA

**O que decidimos:** cada tabela do schema `venus` tem a sua entity e o seu repository do Spring Data JPA, e o Hibernate roda com `ddl-auto: validate`. As buscas com paginação devolvem `Slice` em vez de `Page`.

**Por quê:** o banco é do time de banco, que cuida dele no repositório Venus-Banco junto com as functions, procedures e triggers. Com o `validate`, a API não cria nem altera tabela nenhuma, ela só confere se as entities batem com o banco. Se alguma coluna mudar, a API nem sobe, aí a gente descobre na hora e não depois com dado errado. Isso já aconteceu quando a coluna `hair_type` virou `hair_pattern`. O `Slice` é porque o `Page` faz um `COUNT(*)` a mais em toda busca para contar o total, e o `Slice` só diz se tem uma próxima página.

**O que descartamos:** o `ddl-auto: update`, que deixaria a API mexer no banco de outro time, e o `Page` nas buscas. Pelo mesmo motivo descartamos o `Specification` do Spring, que só devolve `Page`.

**Onde ver:**
- [`application.yml`, linha 22](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/resources/application.yml#L22): `ddl-auto: validate`
- [`entity/`](https://github.com/Venus-System/Venus-CRUD/tree/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/entity): as entities, uma pasta por domínio
- [`ProductRepository.java`, linha 37](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/repository/jpa/product/ProductRepository.java#L37): busca devolvendo `Slice`

## 3. Métodos do CRUD pela API

**O que decidimos:** todo recurso segue o mesmo formato de rotas: listar, `search` com os filtros juntos e paginação, buscar por id, `POST` que devolve 201 com o endereço do recurso criado no `Location`, `PUT` que troca tudo, `PATCH` que muda só o que veio e `DELETE` que devolve 204. Nos campos que podem ficar vazios, o `PATCH` usa `JsonNullable` para saber a diferença entre o campo que não veio e o campo que veio vazio. As rotas `/full`, que juntam várias tabelas numa resposta só, são só de leitura.

**Por quê:** com o mesmo formato em tudo, o app e o site já sabem como chamar qualquer rota. O `PATCH` deixa a tela mudar um campo só sem mandar o objeto inteiro de novo. As rotas `/full` existem para a tela que carrega tudo de uma vez não precisar fazer várias requisições, mas a escrita continua nas rotas menores, por conta que cada formulário do app mexe numa tabela só.

**O que descartamos:** um `POST` ou `PUT` que grava várias tabelas de uma vez. Ia ficar confuso saber o que fazer quando só parte do corpo viesse preenchida.

**Onde ver:**
- [`ProductController.java`, linhas 77 a 84](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L77-L84): `POST` com 201 e `Location`
- [`ProductController.java`, linha 94](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L94): `PATCH`
- [`ProductController.java`, linha 103](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L103): `DELETE` com 204
- [`UserListPatchRequest.java`, linha 18](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/dto/jpa/patch/user/UserListPatchRequest.java#L18): `JsonNullable` no `PATCH`
- [README, Rotas](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/README.md#rotas): os grupos de rotas

## 4. Pacotes no padrão MVC

**O que decidimos:** o código fica dividido em `controller`, `service`, `repository`, `entity`, `dto` e `mapper`, e cada um é separado em `jpa` (Postgres) e `mongo` (MongoDB). O controller só recebe a requisição e devolve a resposta, a regra de negócio fica no service e o acesso ao banco fica no repository. A entity nunca sai pela API: a resposta é sempre um DTO `record`, e quem converte é o MapStruct, configurado para dar erro de compilação se algum campo ficar sem mapear. O service é uma classe só, sem interface.

**Por quê:** cada camada tem um motivo só para mudar. Se a rota muda, mexe no controller, e se a regra muda, mexe no service. Não devolver a entity evita mandar campo que não pode sair, como o hash da senha, e evita o Jackson tentar ler uma relação que o Hibernate ainda não carregou. O erro de compilação do MapStruct pega na hora quando alguém cria um campo novo e esquece de tratar.

**O que descartamos:** o par `XService` e `XServiceImpl`. Com uma implementação só, a interface não ajuda em nada e só deixa mais arquivo para ler, e o Mockito já consegue fazer mock da classe direto nos testes.

**Onde ver:**
- [`com/venus/crud/`](https://github.com/Venus-System/Venus-CRUD/tree/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud): os pacotes
- [`VenusMapperConfig.java`, linha 8](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/config/VenusMapperConfig.java#L8): erro quando falta mapear um campo
- [`ProductService.java`, linha 27](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/service/jpa/product/ProductService.java#L27): service sem interface

## 5. Validação das entradas

**O que decidimos:** os DTOs de entrada usam as anotações do Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@Email`), e o controller confere com `@Valid` antes de chamar o service. As constraints do banco (unique, check e not null) continuam lá como uma segunda barreira.

**Por quê:** o erro aparece logo na entrada, antes de qualquer consulta, e volta com a mensagem de cada campo no `details` da resposta. A constraint do banco garante o dado certo mesmo quando duas requisições chegam ao mesmo tempo.

**O que descartamos:** validar com `if` dentro de cada service, que espalharia a mesma checagem por vários lugares, e confiar só no banco, que devolveria um erro sem dizer qual campo está errado.

**Onde ver:**
- [`ProductRequest.java`, linhas 9 a 15](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/dto/jpa/request/product/ProductRequest.java#L9-L15): as anotações no DTO
- [`ProductController.java`, linha 78](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L78): o `@Valid`
- [`GlobalExceptionHandler.java`, linha 105](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/exception/GlobalExceptionHandler.java#L105): a resposta com o erro de cada campo

## 6. Tratamento de exceções centralizado

**O que decidimos:** um `GlobalExceptionHandler` com `@RestControllerAdvice` trata todos os erros e devolve sempre o mesmo formato (`ErrorResponse`). Os erros do banco são traduzidos pelo SQLState: unique e FK viram 409, not null e check viram 422, banco fora do ar vira 503, e erro inesperado vira 500 com uma mensagem genérica.

**Por quê:** o app e o site leem um formato só de erro, e nenhum controller precisa de try/catch. Separar o 409 do 422 diz para quem chamou se o problema é conflito com outro dado ou um dado que faltou. No 500 a mensagem é genérica para não mostrar nome de tabela ou detalhe do banco para quem chama, e o erro completo fica no log.

**O que descartamos:** try/catch em cada controller, e o jeito que a gente usava antes, em que todo erro de integridade virava 409 e escondia quando faltava um campo obrigatório.

**Onde ver:**
- [`GlobalExceptionHandler.java`, linha 31](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/exception/GlobalExceptionHandler.java#L31): o `@RestControllerAdvice`
- [`GlobalExceptionHandler.java`, linha 36](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/exception/GlobalExceptionHandler.java#L36): a mensagem genérica do 500
- [`DataIntegrityViolationTranslator.java`, linhas 19 a 22](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/exception/DataIntegrityViolationTranslator.java#L19-L22): os SQLStates de unique, FK, not null e check
- [`ErrorResponse.java`, linha 6](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/exception/ErrorResponse.java#L6): o formato do erro

## 7. Documentação automática com Swagger

**O que decidimos:** usar o springdoc-openapi, que gera o Swagger a partir do código, e criar dois customizers. Um coloca em todas as rotas os erros que elas podem devolver e o código certo de sucesso (201 e 204), e o outro coloca o cadeado do token nas rotas protegidas.

**Por quê:** a documentação sai do próprio código, então não fica desatualizada. Com os customizers, a gente não precisa escrever os erros à mão em mais de 300 rotas, e quem usa a API já vê no Swagger os erros possíveis e onde precisa de token.

**O que descartamos:** anotar os erros rota por rota, que ia repetir o mesmo texto centenas de vezes e acabar esquecendo alguma.

**Onde ver:**
- [`pom.xml`, linha 76](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/pom.xml#L76): o springdoc
- [`OpenApiErrorResponsesCustomizer.java`, linha 28](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/config/OpenApiErrorResponsesCustomizer.java#L28): os erros e o código de sucesso de cada rota
- [`OpenApiSecurityCustomizer.java`, linha 14](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/config/OpenApiSecurityCustomizer.java#L14): o cadeado nas rotas protegidas
- [README, Swagger](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/README.md#documentação-da-api-swagger): como abrir e testar

## 8. Acionar as procedures e functions da Modelagem de Dados

**O que decidimos:** a API chama as rotinas do banco pelo nome, com uma `@Query` nativa no repository. A function `venus.fn_register_user_access` é chamada com `SELECT` quando o app abre (`POST /api/users/me/access`) e registra o acesso que alimenta o DAU. A procedure `venus.sp_set_user_active` é chamada com `CALL` quando o admin ativa ou desativa um usuário (`PATCH /api/users/{id}/active`).

**Por quê:** as duas regras já existem no banco, feitas pelo time de banco, então a API só chama em vez de repetir a regra em Java. A function é o único jeito de gravar os acessos, por conta que o usuário da API não tem permissão de `INSERT` na tabela `user_access_events`. A procedure já sabe que usuário bloqueado ou pendente não pode ser ativado nem desativado por ela. Quando isso acontece, o banco devolve o erro `VE001`, e a API transforma em 409.

**O que descartamos:** gravar os acessos e trocar o status pelo JPA. Isso repetiria no Java uma regra que já é do banco, e no caso dos acessos a API nem teria permissão.

**Onde ver:**
- [`UserRepository.java`, linha 29](https://github.com/Venus-System/Venus-CRUD/blob/bc63994a5a2756eb5f032515a4f3042037e2cf55/src/main/java/com/venus/crud/repository/jpa/user/UserRepository.java#L29): o `SELECT` da function
- [`UserRepository.java`, linhas 34 a 36](https://github.com/Venus-System/Venus-CRUD/blob/bc63994a5a2756eb5f032515a4f3042037e2cf55/src/main/java/com/venus/crud/repository/jpa/user/UserRepository.java#L34-L36): o `CALL` da procedure
- [`UserService.java`, linha 129](https://github.com/Venus-System/Venus-CRUD/blob/bc63994a5a2756eb5f032515a4f3042037e2cf55/src/main/java/com/venus/crud/service/jpa/user/UserService.java#L129): ativar ou desativar pela procedure
- [`UserController.java`, linha 111](https://github.com/Venus-System/Venus-CRUD/blob/bc63994a5a2756eb5f032515a4f3042037e2cf55/src/main/java/com/venus/crud/controller/jpa/user/UserController.java#L111): a rota do admin
- [`UserRepositoryIntegrationTest.java`](https://github.com/Venus-System/Venus-CRUD/blob/bc63994a5a2756eb5f032515a4f3042037e2cf55/src/test/java/com/venus/crud/repository/jpa/user/UserRepositoryIntegrationTest.java): o teste que chama a procedure num Postgres de verdade

## 9. Autenticação e autorização com Spring Security

**O que decidimos:** a API usa o resource server do Spring Security e aceita dois tokens, separados pelo emissor (`iss`):

- o token do Firebase, de quem usa o app e o site, conferido com as chaves públicas do Google;
- o JWT do admin, que a própria API gera no login do painel (`POST /api/auth/admin/login`, com a senha conferida pelo BCrypt) e assina com HS256.

O admin tem três papéis: `ADMIN`, `MODERATOR` e `ANALYST`. As permissões são por grupo de rota, e nas rotas com dado de usuário o `OwnershipGuard` confere se quem chamou é o dono.

**Por quê:** o login do app já é feito pelo Firebase, então a API só precisa conferir o token e não guarda senha de usuário. O admin não usa o app e precisa de papéis diferentes, por isso ele tem um login próprio. Com o resource server, conferir os dois tokens é configuração do próprio Spring.

**O que descartamos:** o Firebase Admin SDK, que precisaria de um arquivo de conta de serviço guardado no servidor, e um login só para todo mundo, que misturaria o usuário do app com o admin.

**Onde ver:**
- [`SecurityConfig.java`, linha 46](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/config/SecurityConfig.java#L46): as regras de acesso
- [`SecurityConfig.java`, linhas 76 a 84](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/config/SecurityConfig.java#L76-L84): os dois tokens separados pelo emissor
- [`AdminTokenService.java`, linha 53](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/security/AdminTokenService.java#L53): o token do admin com HS256
- [`SecurityRoutes.java`, linha 17](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/security/SecurityRoutes.java#L17): os grupos de rota
- [`OwnershipGuard.java`, linha 48](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/security/OwnershipGuard.java#L48): a checagem de dono
- [README, Segurança](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/README.md#segurança): a tabela de quem pode o quê

## 10. API REST em Java para o banco NoSQL

**O que decidimos:** o MongoDB guarda as sessões de scan, e as rotas dele (`/api/scan-sessions`) ficam dentro do Venus-CRUD, em pacotes separados da parte do Postgres: `controller/mongo`, `service/mongo`, `repository/mongo`, `mapper/mongo`, `dto/mongo` e `document`.

**Por que o Mongo:** o scan é um documento com várias partes, como as fotos, o texto lido da frente e do verso, os ingredientes encontrados, a revisão do admin e a sincronização com o catálogo, e ele ganha campo novo a cada fase. No Mongo o campo novo entra sem migração, e o documento antigo só lê esse campo como vazio. O catálogo continua no Postgres, que é relacional.

**Por que dentro do CRUD:** como era pouca coisa de Mongo, uma coleção só, a gente decidiu colocar na API do CRUD mesmo. Além disso, aprovar um scan grava o produto, a versão, os ingredientes e as fotos no Postgres, e isso usa os repositories JPA do próprio CRUD.

**O que descartamos:** uma API só para o Mongo. Ela teria que repetir a segurança com os dois tokens, o deploy e o CI só para uma coleção, e a sincronização com o catálogo ia virar uma chamada HTTP entre as duas APIs.

**Onde ver:**
- [`ScanSessionController.java`, linha 37](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/controller/mongo/ScanSessionController.java#L37): as rotas do scan
- [`ScanSessionRepository.java`, linha 12](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/repository/mongo/ScanSessionRepository.java#L12): o `MongoRepository`
- [`ScanSession.java`, linha 22](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/document/ScanSession.java#L22): o documento da coleção `scan_sessions`
- [`ScanCatalogSync.java`, linha 97](https://github.com/Venus-System/Venus-CRUD/blob/b990fe70a299119c6cd9d6a408ad3cd035da5c92/src/main/java/com/venus/crud/service/mongo/ScanCatalogSync.java#L97): o scan aprovado indo para o catálogo no Postgres
