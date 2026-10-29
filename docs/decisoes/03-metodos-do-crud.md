# 3. Métodos do CRUD pela API

[Índice](../DECISOES.md) · anterior: [2. Spring Data JPA](02-spring-data-jpa.md) · próxima: [4. Pacotes MVC](04-pacotes-mvc.md)

**O que decidimos:** todo recurso segue o mesmo formato de rotas: listar, `search` com os filtros juntos e paginação, buscar por id, `POST` que devolve 201 com o endereço do recurso criado no `Location`, `PUT` que troca tudo, `PATCH` que muda só o que veio e `DELETE` que devolve 204. Nos campos que podem ficar vazios, o `PATCH` usa `JsonNullable` para saber a diferença entre o campo que não veio e o campo que veio vazio: o mapper deixa esses campos de fora e um `@AfterMapping` só aplica o valor quando o campo veio no JSON, mesmo que tenha vindo `null`. As rotas `/full`, que juntam várias tabelas numa resposta só, são só de leitura.

**Por quê:** com o mesmo formato em tudo, o app e o site já sabem como chamar qualquer rota. O `PATCH` deixa a tela mudar um campo só sem mandar o objeto inteiro de novo, e o `JsonNullable` deixa apagar a descrição da lista mandando `null`, sem apagar quando o campo nem veio. As rotas `/full` existem para a tela que carrega tudo de uma vez não precisar fazer várias requisições, mas a escrita continua nas rotas menores, por conta que cada formulário do app mexe numa tabela só.

**O que descartamos:** um `POST` ou `PUT` que grava várias tabelas de uma vez. Ia ficar confuso saber o que fazer quando só parte do corpo viesse preenchida.

**Onde ver:**
- [`ProductController.java`, linhas 77 a 84](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L77-L84): `POST` com 201 e `Location`
- [`ProductController.java`, linha 94](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L94): `PATCH`
- [`ProductController.java`, linha 103](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L103): `DELETE` com 204
- [`UserListPatchRequest.java`, linha 18](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/dto/jpa/patch/user/UserListPatchRequest.java#L18): `JsonNullable` no `PATCH`
- [`UserListMapper.java`, linhas 41 a 45](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/mapper/jpa/user/UserListMapper.java#L41-L45): o `@AfterMapping` que só aplica o campo que veio
- [README, Rotas](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/README.md#rotas): os grupos de rotas

## 3.1 Endereço do recurso criado com o `ServletUriComponentsBuilder`

**O que decidimos:** o `POST` monta o `Location` com o `ServletUriComponentsBuilder.fromCurrentRequest()`, que pega o endereço que o cliente chamou e coloca o id no fim: o `POST /api/products` devolve `Location: .../api/products/42`. Isso fica só no controller. Tem dois casos diferentes:

- a foto do produto é enviada em `/api/product-versions/{id}/photos`, mas é editada e apagada em `/api/media/{id}`, então ali o endereço começa da raiz da API, com o `fromCurrentContextPath()`;
- o avatar do usuário e a capa da lista são um só por usuário e por lista, então o `Location` é o próprio endereço do upload, com o `fromCurrentRequest().build()`.

**Por quê:** o endereço sai certo em qualquer lugar que a API rodar, no `localhost` de cada um, no Render ou no Swagger, sem guardar a URL da API em nenhuma configuração. Ele fica no controller por conta que o endereço é coisa do HTTP, e o service não depende de ter uma requisição: se o service usasse o `fromCurrentRequest()`, os testes do service, que rodam sem servidor, dariam erro.

**O que descartamos:** escrever o endereço na mão (`"http://localhost:8080/api/products/" + id`), que quebraria no Render, uma propriedade com a URL base da API, que teria que mudar em cada ambiente, e devolver o 201 sem o `Location`.

**Onde ver:**
- [`ProductController.java`, linhas 80 a 83](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L80-L83): o `fromCurrentRequest()` com o id
- [`ProductVersionPhotoController.java`, linhas 50 a 53](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/media/ProductVersionPhotoController.java#L50-L53): a foto do produto com o `fromCurrentContextPath()`
- [`UserAvatarController.java`, linha 49](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/media/UserAvatarController.java#L49): o avatar com o próprio endereço do upload

## 3.2 Imagens no Cloudinary

**O que decidimos:** os arquivos das imagens ficam no Cloudinary, e no Postgres fica só o registro de cada uma na `media_assets`, com o `public_id`, a URL e de quem a imagem é. Em cima disso:

- **Duas chaves no mesmo Cloudinary:** uma para as imagens do usuário (avatar e capa da lista) e outra para as fotos de produto, cada uma com o seu upload preset. Quem escolhe a chave é o `MediaPurpose`, que diz se a imagem é do usuário (`isImageOfUser()`).
- **Avatar, capa e foto do produto passam pela API:** o arquivo chega em `multipart`, a API confere o tipo e o tamanho antes de mandar e as dimensões com a resposta do Cloudinary, com os limites do `.env`. O `public_id` é a API que monta, com um UUID no fim (`users/{id}/avatar/{uuid}`). Se a dimensão passar do limite ou o registro no banco falhar, a imagem que acabou de subir é apagada do Cloudinary.
- **As fotos do scan vão direto do app para o Cloudinary:** a API só gera a assinatura, com a chave de produto e o `public_id` já definido (`scans/{scanId}/front` e `scans/{scanId}/back`). No `POST` do scan vai só a referência, a API confere se o `public_id` é o daquele scan e monta a URL ela mesma.
- **Apagar marca como `DELETED`:** o registro fica no banco com o status `DELETED`, e só depois a API tenta apagar a imagem no Cloudinary.
- **Limite do `multipart` em 12 MB**, acima do maior limite do `.env`.

**Por quê:** as duas chaves deixam a chave das fotos de produto separada da chave das imagens do usuário, e cada preset fica com as regras do seu tipo de imagem. Passando pela API, o erro já volta dizendo qual limite foi passado, e o app não precisa saber nada do Cloudinary. O UUID é porque o banco não deixa repetir `public_id`, então cada upload ganha um novo e a imagem antiga continua registrada como `DELETED`. No scan, o upload direto e assinado é o que o guia de Cloudinary do time de banco manda: o segredo da chave nunca sai da API e o JSON do scan fica leve para reenviar ([10.2](10-api-nosql.md)). A chave é a de produto por conta que, quando o admin aprova o scan, a foto da frente vira a foto do produto sem precisar subir de novo. Como o `public_id` e a URL saem da API, o app não consegue gravar a foto em outra pasta nem mandar uma URL qualquer. Marcar como `DELETED` antes é para o banco nunca apontar para uma imagem que já sumiu: se o Cloudinary falhar, o pior que acontece é a imagem sobrar lá, e o erro fica no log. E o `multipart` em 12 MB é porque, se o arquivo passar desse limite, o Tomcat recusa antes do validador rodar e o erro sai genérico (413). Com ele acima dos limites do `.env`, quem responde é o validador, com a mensagem do limite.

**O que descartamos:** uma chave só para todas as imagens, deixar o app escolher o `public_id` ou mandar a URL da foto do scan, apagar o registro do banco junto com a imagem, e deixar o limite do `multipart` igual ao do `.env`.

**Onde ver:**
- [`CloudinaryConfig.java`, linhas 13 a 21](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/CloudinaryConfig.java#L13-L21): as duas chaves
- [`CloudinaryStorageService.java`, linhas 66 a 68](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/media/CloudinaryStorageService.java#L66-L68): a chave escolhida pelo `MediaPurpose`
- [`MediaAssetService.java`, linhas 72 a 79](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/media/MediaAssetService.java#L72-L79): o avatar validado antes de subir e o `public_id` com UUID
- [`MediaAssetService.java`, linhas 128 a 137](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/media/MediaAssetService.java#L128-L137): a imagem apagada do Cloudinary quando algo falha depois do upload
- [`MediaAssetService.java`, linhas 104 a 106](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/media/MediaAssetService.java#L104-L106): marca como `DELETED` e depois apaga no Cloudinary
- [`ScanCloudinaryService.java`, linhas 35 a 51](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanCloudinaryService.java#L35-L51): a assinatura das fotos do scan
- [`ScanSessionRequestValidator.java`, linhas 67 a 69](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanSessionRequestValidator.java#L67-L69): a conferência do `public_id` do scan
- [`ScanCatalogSync.java`, linha 265](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/mongo/ScanCatalogSync.java#L265): a foto da frente do scan virando foto do produto
- [`application.yml`, linhas 47 a 50](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/resources/application.yml#L47-L50): o limite do `multipart`

## 3.3 Perfil e preferências pelo `userId`

**O que decidimos:** as tabelas que têm uma linha só por usuário, como o perfil e as preferências, são lidas e alteradas pelo id do usuário (`GET /api/user-profiles/{userId}`), e não pelo id da própria tabela.

**Por quê:** o app e o site já têm o id do usuário logado, e o id do perfil eles não conhecem. Com o `userId` na rota, a checagem de dono (`@ownership.canAccessUser(#userId)`) usa o próprio valor da rota, sem precisar buscar o perfil antes.

**O que descartamos:** usar o id do perfil na rota, que obrigaria o app a descobrir esse id antes de qualquer chamada.

**Onde ver:**
- [`UserProfileController.java`, linhas 83 e 84](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/user/UserProfileController.java#L83-L84): a rota pelo `userId` com a checagem de dono
- [`UserProfileService.java`, linhas 111 e 112](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/user/UserProfileService.java#L111-L112): o perfil buscado pelo `userId`
