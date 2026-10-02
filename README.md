# Venus-CRUD

API REST central do **Venus-System**, a plataforma de análise de produtos cosméticos. O usuário escaneia um produto no app, o sistema lê os ingredientes e mostra se o produto combina com o perfil de pele e cabelo dele.

O Venus-CRUD é o dono dos dados. Ele guarda o catálogo (produtos, marcas, ingredientes), os dados dos usuários (perfil, preferências, alergias, listas, avaliações), o cadastro do motor de pontuação e os scans enviados pelo app. O app mobile, a web e o painel de administração conversam com ele por HTTP.

## Sumário

- [Onde ele entra no sistema](#onde-ele-entra-no-sistema)
- [Stack](#stack)
- [O que a API faz](#o-que-a-api-faz)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Como uma requisição passa pelo código](#como-uma-requisição-passa-pelo-código)
- [Segurança](#segurança)
- [Como rodar](#como-rodar)
- [Documentação da API (Swagger)](#documentação-da-api-swagger)
- [Rotas](#rotas)
- [Testes](#testes)
- [CI/CD](#cicd)
- [Como contribuir](#como-contribuir)
- [Licença](#licença)

## Onde ele entra no sistema

```
 Venus-Mobile (Android)  ─┐
 Venus-Web (React)       ─┼──HTTP──>  Venus-CRUD  ──JPA──────>  PostgreSQL (schema venus)
 Painel admin            ─┘               │       ──Mongo────>  MongoDB (sessões de scan)
                                          │       ──SDK──────>  Cloudinary (fotos)
                                          │
              token do Firebase (app) ────┤
              token do admin (JWT nosso) ─┘
```

| Repositório | Papel |
|---|---|
| **Venus-CRUD** (este) | API central: CRUD de todo o schema `venus` e o fluxo dos scans |
| Venus-Banco | Schema PostgreSQL, functions, procedures e triggers. É a fonte da verdade do banco |
| Venus-Classificacao | Motor que calcula a nota 0–100 do produto para cada perfil |
| Venus-AI-api / Venus-AI-Sdk | Agentes de IA (Python/FastAPI) |
| Venus-Mobile | App Android onde o usuário escaneia o produto |
| Venus-Web | Aplicação web: questionário, perfil, busca e produto |

Todos ficam na organização [Venus-System](https://github.com/Venus-System).

## Stack

| Tecnologia | Versão | Para quê |
|---|---|---|
| Java | 21 | Linguagem (com virtual threads ligadas) |
| Spring Boot | 3.3.4 | Web, Data JPA, Data MongoDB, Validation |
| Spring Security + OAuth2 Resource Server | do Boot | Validação dos tokens do Firebase e do admin |
| PostgreSQL | 16 | Banco relacional (schema `venus`) |
| MongoDB | 7 | Sessões de scan |
| MapStruct | 1.5.5.Final | Conversão entity ↔ DTO |
| Lombok | do Boot | Getters, setters e construtores das entities |
| springdoc-openapi | 2.6.0 | Swagger UI |
| jackson-databind-nullable | 0.2.6 | PATCH que diferencia "não enviado" de "enviado como null" |
| Cloudinary (`cloudinary-http5`) | 2.4.0 | Upload e entrega de imagens |
| Testcontainers | do Boot | Teste de integração com Postgres real |
| Maven Wrapper | — | Build (`./mvnw`) |

## O que a API faz

**Catálogo de produtos**
Produtos, versões de produto (fórmula e embalagem mudam com o tempo), marcas, categorias, claims ("vegano", "sem parabenos"), selos, embalagens e a lista de ingredientes de cada versão.

**Ingredientes**
Ingredientes com sinônimos (INCI e nomes populares), categorias em hierarquia (filtrar por uma categoria traz as filhas), propriedades, regulamentações e efeitos.

**Usuários**
Conta, perfil (respostas do questionário), preferências, alergias, tags de perfil, favoritos, listas, avaliações de produto com votos de "útil" e denúncias.

**Pontuação (scoring)**
Cadastro do motor de pontuação: modelos, categorias de nota, regras de compatibilidade, vínculo entre alergia e ingrediente, notas do produto e resultados de análise, recomendações e avaliação de cada regra. O cálculo em si fica no Venus-Classificacao. Aqui ficam os dados.

**Mídia**
Upload de avatar e de fotos de produto no Cloudinary, com tipo, tamanho e dimensão validados pelas regras do `.env`.

**Scan (MongoDB)**
O app envia uma sessão de scan com as fotos e o texto lido da frente e do verso do produto. Um moderador aprova ou recusa. Ao aprovar, o scan é sincronizado com o catálogo: cria ou atualiza o produto, a versão, os ingredientes e as fotos no Postgres. Se a sincronização falhar, dá para tentar de novo em `POST /api/scan-sessions/{id}/sync`.

**Rotas "full"**
Algumas rotas devolvem o objeto completo numa chamada só, para a tela não precisar fazer várias requisições:

| Rota | O que devolve |
|---|---|
| `GET /api/products/{id}/full` | Produto com marca, categoria, versão vigente, fotos, embalagem, rótulo, claims e nota |
| `GET /api/ingredients/{id}/full` | Ingrediente com categoria, sinônimos, propriedades e efeitos |
| `GET /api/reviews/{id}/full` | Avaliação com a contagem de votos de útil e de não útil |
| `GET /api/users/{userId}/full-profile` | Usuário com avatar, perfil, tags, preferências, alergias, favoritos e listas com os itens |
| `GET /api/analysis-results/{id}/full` | Resultado da análise com a nota personalizada e as regras disparadas, cada uma com a explicação |
| `GET /api/product-scores/product-version/{productVersionId}/scoring-model/{scoringModelId}/full` | Nota de uma versão de produto com o modelo e o peso de cada categoria |
| `GET /api/scan-sessions/{scanSessionId}/analysis-result/{analysisResultId}/full` | Scan com a análise completa, o produto completo, a versão analisada e se o produto mudou depois do scan |

## Estrutura do projeto

```
Venus-CRUD/
├── .github/
│   ├── workflows/                 # ci.yml, deploy-qa.yml, deploy-prod.yml
│   └── pull_request_template.md
├── src/
│   ├── main/
│   │   ├── java/com/venus/crud/
│   │   │   ├── config/            # Segurança, Swagger, Cloudinary, Jackson, Mongo, propriedades do .env
│   │   │   ├── controller/
│   │   │   │   ├── jpa/           # Rotas dos dados do Postgres, um pacote por domínio
│   │   │   │   │   └── fullstage/ # Rotas que devolvem o objeto completo
│   │   │   │   └── mongo/         # Rotas das sessões de scan
│   │   │   ├── document/          # Documentos do MongoDB (ScanSession e partes)
│   │   │   ├── dto/
│   │   │   │   ├── jpa/
│   │   │   │   │   ├── request/   # Corpo do POST e do PUT
│   │   │   │   │   ├── patch/     # Corpo do PATCH (campos opcionais)
│   │   │   │   │   └── response/  # O que a API devolve
│   │   │   │   ├── mongo/
│   │   │   │   └── shared/
│   │   │   ├── entity/            # Entities JPA, enums e converters
│   │   │   ├── exception/         # Exceções e o GlobalExceptionHandler
│   │   │   ├── mapper/            # Mappers MapStruct (jpa e mongo)
│   │   │   ├── repository/        # Spring Data (jpa e mongo)
│   │   │   ├── security/          # Tokens, papéis, rotas e checagem de dono
│   │   │   └── service/           # Regras de negócio (jpa e mongo)
│   │   └── resources/
│   │       └── application.yml    # Toda configuração vem de variável de ambiente
│   └── test/
│       ├── java/com/venus/crud/   # Testes de unidade e de integração
│       └── resources/venus-banco/ # Cópia do schema para o teste de integração
├── .env.example                   # Modelo das variáveis de ambiente
├── docker-compose.yml             # Postgres e Mongo locais
├── Dockerfile                     # Imagem da API (build em duas etapas)
└── pom.xml
```

Os pacotes de `controller`, `dto`, `mapper`, `repository` e `service` são divididos em `jpa` (Postgres) e `mongo` (MongoDB). Dentro de `jpa`, cada domínio tem o próprio pacote: `admin`, `ingredient`, `media`, `product`, `review`, `scan`, `scoring`, `shared` e `user`.

## Como uma requisição passa pelo código

```
HTTP ─> Controller ─> Service ─> Repository ─> banco
            │            │
            │            └─> Mapper (entity ↔ DTO)
            └─> devolve um DTO (record)
```

- O **controller** recebe a requisição, valida o corpo (`@Valid`), confere a permissão (`@PreAuthorize`) e chama o service. Ele não tem regra de negócio.
- O **service** tem a regra: busca, valida estado, salva e converte com o mapper.
- A **entity** nunca sai pela API. A resposta é sempre um DTO `record`.
- No **PATCH**, o campo que não veio no corpo fica como está, e o campo enviado como `null` é apagado quando a coluna aceita vazio.
- Os erros passam pelo `GlobalExceptionHandler` e sempre voltam no mesmo formato:

```json
{
  "timestamp": "2026-10-02T10:15:30-03:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto não encontrado",
  "path": "/api/products/999",
  "details": []
}
```

## Segurança

A API aceita dois tipos de token no cabeçalho `Authorization: Bearer <token>`:

| Token | Quem usa | Como é obtido | Papel |
|---|---|---|---|
| Firebase | Usuário do app e da web | Login no Firebase, feito pelo próprio app | `USER` |
| JWT do admin | Painel de administração | `POST /api/auth/admin/login` com e-mail e senha | `ADMIN`, `MODERATOR` ou `ANALYST` |

A API identifica o token pelo emissor (`iss`). O do Firebase é validado com as chaves públicas do Google para o projeto em `FIREBASE_PROJECT_ID`. O do admin é assinado pela própria API com `ADMIN_JWT_SECRET` (HS256).

**Papéis do admin.** Cada papel inclui os de baixo:

| Papel | Pode |
|---|---|
| `ADMIN` | Tudo, inclusive cadastro de pontuação e gestão de admins |
| `MODERATOR` | Alterar o catálogo, aprovar e recusar scans, moderar avaliações |
| `ANALYST` | Consultar scans e dados para análise, sem alterar |

**Regra por grupo de rota**

| Rotas | GET | POST / PUT / PATCH / DELETE |
|---|---|---|
| Swagger e `POST /api/auth/admin/login` | livre | livre |
| Catálogo (produtos, marcas, ingredientes, alergias, tags de perfil, mídia…) | livre | `MODERATOR` |
| Cadastro de pontuação (regras, modelos, categorias de nota, notas de produto…) | livre | `ADMIN` |
| Avaliações e contagem de favoritos e votos | livre | dono ou `MODERATOR` |
| Dados de usuário (perfil, preferências, alergias, listas, favoritos, resultados, scans…) | dono ou papel | dono ou papel |
| Qualquer outra rota | `ADMIN` | `ADMIN` |

"Dono" quer dizer que o usuário do token do Firebase é o mesmo usuário da rota (`/api/users/{userId}/...`) ou o dono do recurso (lista, avaliação, scan). A checagem fica no `OwnershipGuard`, chamado pelo `@PreAuthorize` de cada rota. O admin passa por essa checagem.

**Primeiro admin.** Não existe cadastro aberto de admin: `POST /api/admin-users` exige `ADMIN`. O primeiro tem que ser criado direto no banco, na tabela `admin_users`, com a senha em hash BCrypt.

## Como rodar

### Pré-requisitos

- JDK 21
- Acesso a um PostgreSQL com o schema `venus` criado pelo repositório **Venus-Banco**
- Acesso a um MongoDB
- Conta no Cloudinary (para as rotas de mídia)
- Docker, opcional: para o `docker-compose` e para os testes de integração

### 1. Clonar

```bash
git clone https://github.com/Venus-System/Venus-CRUD.git
cd Venus-CRUD
git checkout develop
```

### 2. Configurar as variáveis de ambiente

```bash
cp .env.example .env
```

Preencha o `.env`. O Spring **não lê o `.env` sozinho**: carregue as variáveis no terminal ou na configuração de execução da IDE (no IntelliJ, pelo plugin EnvFile ou em *Run Configuration → Environment variables*).

| Variável | Descrição |
|---|---|
| `SERVER_PORT` | Porta HTTP da API (padrão `8080`) |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | Endereço do PostgreSQL |
| `DB_USERNAME`, `DB_PASSWORD` | Usuário e senha do PostgreSQL |
| `MONGO_URI` | URI completa do MongoDB, com o nome do banco |
| `CLOUDINARY_CLOUD_NAME` | Nome da conta no Cloudinary |
| `CLOUDINARY_USERS_*` | Chave, segredo e upload preset das fotos de usuário |
| `CLOUDINARY_PRODUCTS_*` | Chave, segredo e upload preset das fotos de produto |
| `MEDIA_ALLOWED_IMAGE_TYPES` | Tipos de imagem aceitos, separados por vírgula |
| `MEDIA_DEFAULT_DELIVERY_TYPE`, `MEDIA_DEFAULT_RESOURCE_TYPE` | Padrões de entrega do Cloudinary (`upload`, `image`) |
| `MEDIA_AVATAR_MAX_*` | Tamanho máximo do avatar, em bytes e pixels |
| `MEDIA_PRODUCT_MAX_*` | Tamanho máximo da foto de produto, em bytes e pixels |
| `ADMIN_JWT_SECRET` | Segredo do token do admin. **No mínimo 32 bytes**, senão a API não sobe |
| `ADMIN_JWT_EXPIRATION` | Validade do token do admin, em ISO-8601 (`PT8H` = 8 horas) |
| `FIREBASE_PROJECT_ID` | Projeto do Firebase que emite o token do app |

Para as credenciais do banco e do Cloudinary, fale com o time.

### 3. Banco de dados

O Hibernate roda com `ddl-auto: validate`: ele **confere** se as tabelas batem com as entities, mas **não cria nem altera nada**. Se faltar uma tabela ou coluna, a API não sobe.

O schema vem do repositório **Venus-Banco**. Toda mudança de tabela, coluna, trigger ou permissão é feita lá, pelo time de banco, e só depois a API muda.

> **Limitação conhecida:** a URL do datasource no `application.yml` usa `sslmode=require`. O Postgres do `docker-compose.yml` sobe sem SSL, então a API não conecta nele sem ajuste. Hoje o jeito que funciona é apontar para o banco compartilhado do time.

Para subir só o MongoDB local:

```bash
docker compose up -d mongo
```

Nesse caso, use `MONGO_URI=mongodb://localhost:27017/venus`.

### 4. Subir a API

```bash
./mvnw spring-boot:run
```

No Windows (PowerShell ou cmd), use `mvnw.cmd spring-boot:run`.

A API sobe em `http://localhost:8080` (ou na porta de `SERVER_PORT`).

### Com Docker

```bash
docker build -t venus-crud .
docker run --env-file .env -p 8080:8080 venus-crud
```

A imagem é feita em duas etapas: compila com JDK 21 e roda com JRE 21 Alpine, com um usuário sem privilégios.

## Documentação da API (Swagger)

Com a API no ar:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`

Para testar uma rota protegida no Swagger:

1. Chame `POST /api/auth/admin/login` com e-mail e senha de um admin (ou pegue um token do Firebase no app).
2. Clique em **Authorize** e cole o token.
3. As rotas protegidas passam a enviar o cabeçalho `Authorization`.

Cada rota mostra no Swagger os erros possíveis (400, 401, 403, 404…).

## Rotas

Todas começam com `/api`. A maioria tem `GET` (lista e por id), `POST`, `PUT`, `PATCH` e `DELETE`, além de buscas específicas. A lista completa com parâmetros fica no Swagger.

| Domínio | Rotas base |
|---|---|
| Autenticação | `/auth/admin` |
| Admin | `/admin-users` |
| Produto | `/products`, `/product-versions`, `/product-categories`, `/brands`, `/claims`, `/product-claims`, `/product-labels`, `/packagings`, `/product-ingredients` |
| Ingrediente | `/ingredients`, `/ingredient-aliases`, `/ingredient-categories`, `/ingredient-properties`, `/ingredient-effects`, `/regulations` |
| Usuário | `/users`, `/user-profiles`, `/user-preferences`, `/user-allergies`, `/user-profile-tags`, `/profile-tags`, `/allergies`, `/favorites`, `/user-lists`, `/user-list-items` |
| Avaliação | `/reviews`, `/review-votes`, `/reports` |
| Pontuação | `/scoring-models`, `/score-categories`, `/scoring-model-categories`, `/compatibility-rules`, `/allergy-ingredients`, `/product-scores`, `/analysis-results`, `/personalized-scores`, `/recommendations`, `/rule-evaluations` |
| Mídia | `/media` |
| Scan (MongoDB) | `/scan-sessions` |

**Fluxo do scan**

| Rota | Quem | O que faz |
|---|---|---|
| `GET /scan-sessions/upload-signatures` | usuário com conta ativa | Devolve a assinatura para o app subir as fotos direto no Cloudinary (vale 1 hora) |
| `POST /scan-sessions` | o próprio usuário | Envia o scan. Reenviar o mesmo `scanId` devolve o scan que já existe |
| `GET /scan-sessions/user/{userId}` | dono ou `ANALYST` | Lista os scans de um usuário |
| `GET /scan-sessions`, `/status/{status}`, `/device/{deviceId}` | `ANALYST` | Consultas do painel |
| `POST /scan-sessions/{id}/approve` | `MODERATOR` | Aprova e já sincroniza com o catálogo |
| `POST /scan-sessions/{id}/reject` | `MODERATOR` | Recusa o scan |
| `POST /scan-sessions/{id}/sync` | `MODERATOR` | Tenta de novo uma sincronização que falhou |

## Testes

```bash
./mvnw verify
```

- **Unidade:** services, mappers, validadores e segurança (tokens, papéis, checagem de dono). Rodam sem banco.
- **Segurança das rotas:** `PreAuthorizeCoverageTest` falha se alguma rota de dados ficar sem `@PreAuthorize`. Os testes `*ControllerSecurityTest` chamam as rotas e conferem o 401 e o 403.
- **Integração:** `ScanCatalogSyncIntegrationTest` sobe um Postgres 16 com Testcontainers, usando a cópia do schema em `src/test/resources/venus-banco/`. Ele precisa de Docker. Sem Docker, é pulado e o resto roda normalmente.

Quando o schema mudar no Venus-Banco, a cópia em `src/test/resources/venus-banco/` precisa ser atualizada.

## CI/CD

| Workflow | Quando roda | O que faz |
|---|---|---|
| `ci.yml` | PR para `develop` ou `main` | `./mvnw verify` |
| `deploy-qa.yml` | push em `develop` | Build, testes e imagem no GHCR com as tags `staging` e `develop-<sha>` |
| `deploy-prod.yml` | push em `main` | Build, testes e imagem no GHCR com as tags `latest` e `main-<sha>` |

A etapa de deploy dos dois ambientes ainda não tem host definido: hoje ela só publica a imagem.

## Como contribuir

1. Crie o branch a partir de `develop`: `feat/...`, `fix/...`, `refactor/...`, `docs/...` ou `ci/...`.
2. Commits no padrão de commits convencionais, em português e no gerúndio. Exemplo: `feat: adicionando o nome do autor na avaliacao`.
3. Rode `./mvnw verify` antes de abrir o PR.
4. Abra o PR para `develop` e preencha o template (`.github/pull_request_template.md`).
5. Mudança de banco não é feita aqui: abra no **Venus-Banco** e descreva no PR o que a API passa a esperar.

Padrões do código:

- A resposta da API é sempre DTO `record`, nunca entity.
- A conversão é feita com MapStruct: não monte DTO na mão no service.
- Toda rota nova de dado de usuário precisa de `@PreAuthorize` (o teste de cobertura cobra).
- Nada de chave, senha ou URL privada no código ou no `application.yml`: tudo vem do `.env`.

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
