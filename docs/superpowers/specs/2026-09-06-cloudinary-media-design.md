# Mídia com Cloudinary — design

Data: 06/09/2026
Base: `venus_cloudinary_guide_backend_web_mobile.docx` (04/09/2026)
Escopo: avatar de usuário e fotos de versão de produto no Venus-CRUD (Spring Boot 3.3.4, Java 21)

## 1. Objetivo

Implementar a arquitetura de mídia descrita no guia: o binário vive no Cloudinary, o
registro canônico vive em `venus.media_assets`, e o backend é o único que fala com o
Cloudinary. Web e Mobile consomem uma representação de mídia estável e nunca montam URL
na mão.

O projeto não tem hoje nenhum campo `avatar_*` ou `photo_*` mapeado em `User` ou
`ProductVersion`, então a seção 12 do guia ("não voltar a gravar nas colunas antigas") não
exige remoção de código: não há lógica antiga no backend.

## 2. Decisões

| Decisão | Escolha | Motivo |
| --- | --- | --- |
| Migration do banco | Já executada | Confirmado pelo usuário; `ddl-auto: validate` sobe normalmente |
| Identidade do asset | `public_id` único por upload (`.../{uuid}`) | Respeita `UNIQUE (provider, public_id)` e permite soft delete auditável |
| Troca de avatar | Marcar anterior como `deleted`, inserir novo | É o fluxo que o índice parcial `ux_media_user_avatar` contempla |
| Exclusão | Soft delete (`status = deleted`) + `destroy` best-effort | Guia §10: preferir marcar `deleted` a apagar antes de garantir o resultado externo |
| `delivery_type` | Sempre `upload` (público), lido da configuração | Todas as imagens do Venus são públicas na tela |
| Credenciais Cloudinary | Dois pares de chaves, roteados por `purpose` | É o que o `.env` define; isola a chave de produto da chave de usuário |
| Views `v_user_avatars` / `v_product_photos` | Não mapeadas em Java | O projeto não tem entidade de view; derived query usa o mesmo índice parcial |
| Campo id no DTO | `mediaAssetId` | O guia é o contrato já publicado para Web e Mobile |
| Integração nos agregados | Sim | `UserFullProfileResponse` e `ProductFullResponse` passam a expor mídia |

## 3. Padrão a seguir

O código novo replica o template já usado em todo o projeto, sem inventar estilo:

- Entidade estende `AuditableEntity` → `BaseEntity`; `created_at`/`updated_at` são geridos
  pelo banco (`@Generated`, `insertable = false`, `updatable = false`)
- Enum Java em MAIÚSCULO + `XConverter extends LowercaseEnumConverter` com
  `@Converter(autoApply = true)`
- Repository com derived queries e `@EntityGraph` para as FKs lidas junto
- Mapper MapStruct com `config = VenusMapperConfig.class` (`unmappedTargetPolicy = ERROR`)
- Service com `executeOrFail(Supplier, String)` traduzindo `DataIntegrityViolationException`
  → `DuplicateResourceException` e `DataAccessException` → `ServiceUnavailableException`
- Controller sob `/api/...`, devolvendo `ResponseEntity`
- DTOs como `record`, sem comentários no código, sem interface + impl de service

## 4. Configuração

### 4.1 `pom.xml`

Adicionar a dependência do SDK oficial:

```xml
<dependency>
  <groupId>com.cloudinary</groupId>
  <artifactId>cloudinary-http5</artifactId>
  <version>${cloudinary.version}</version>
</dependency>
```

A versão exata é resolvida no Maven Central no momento da implementação e fixada em
`<properties>`. O guia §5.2 alerta explicitamente contra copiar uma versão antiga de
exemplo. A validação é o build do projeto.

### 4.2 `application.yml`

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 12MB
      max-request-size: 12MB

cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME}
  users:
    api-key: ${CLOUDINARY_USERS_API_KEY}
    api-secret: ${CLOUDINARY_USERS_API_SECRET}
    upload-preset: ${CLOUDINARY_USERS_UPLOAD_PRESET}
  products:
    api-key: ${CLOUDINARY_PRODUCTS_API_KEY}
    api-secret: ${CLOUDINARY_PRODUCTS_API_SECRET}
    upload-preset: ${CLOUDINARY_PRODUCTS_UPLOAD_PRESET}

venus:
  media:
    allowed-image-types: ${MEDIA_ALLOWED_IMAGE_TYPES}
    default-delivery-type: ${MEDIA_DEFAULT_DELIVERY_TYPE}
    default-resource-type: ${MEDIA_DEFAULT_RESOURCE_TYPE}
    avatar:
      max-bytes: ${MEDIA_AVATAR_MAX_BYTES}
      max-width: ${MEDIA_AVATAR_MAX_WIDTH}
      max-height: ${MEDIA_AVATAR_MAX_HEIGHT}
    product:
      max-bytes: ${MEDIA_PRODUCT_MAX_BYTES}
      max-width: ${MEDIA_PRODUCT_MAX_WIDTH}
      max-height: ${MEDIA_PRODUCT_MAX_HEIGHT}
```

O limite de multipart (12 MB) é deliberadamente maior que o maior limite de negócio
(10 MB para produto). Se fosse igual ou menor, o Tomcat rejeitaria o arquivo antes do
validador rodar e o cliente receberia um erro genérico em vez da mensagem específica de
tamanho.

O `.env` já contém todas as variáveis acima e está no `.gitignore`.

### 4.3 `config/CloudinaryConfig.java`

Dois beans `Cloudinary` com o mesmo `cloud_name` e pares de credenciais diferentes,
distinguidos por `@Qualifier`:

- `usersCloudinary` — `CLOUDINARY_USERS_*`
- `productsCloudinary` — `CLOUDINARY_PRODUCTS_*`

Mais dois records `@ConfigurationProperties`:

- `CloudinaryProperties` (`cloudinary`) — `cloudName`, `users`, `products`, onde cada
  credencial é um record aninhado `Account(apiKey, apiSecret, uploadPreset)`
- `MediaProperties` (`venus.media`) — `allowedImageTypes` (`List<String>`),
  `defaultDeliveryType`, `defaultResourceType`, `avatar` e `product` como record aninhado
  `Limits(maxBytes, maxWidth, maxHeight)`

## 5. Domínio

### 5.1 Enums e converters (`entity/enums/`, `entity/converter/`)

| Enum | Valores | Coluna |
| --- | --- | --- |
| `MediaPurpose` | `AVATAR`, `PRODUCT_PHOTO` | `purpose` |
| `MediaStatus` | `PENDING`, `ACTIVE`, `DELETED`, `FAILED` | `status` |
| `MediaDeliveryType` | `UPLOAD`, `AUTHENTICATED` | `delivery_type` |

Cada um com seu converter estendendo `LowercaseEnumConverter` e `@Converter(autoApply = true)`,
igual aos 30 converters já existentes. `LOWERCASE` de `PRODUCT_PHOTO` dá `product_photo`,
que é exatamente o valor esperado pela CHECK constraint.

`provider` e `resource_type` **não** viram enum: são colunas de valor único no desenho atual
(`cloudinary` / `image`) e um enum de um valor só seria ruído. `provider` recebe a constante
`"cloudinary"` no service; `resource_type` vem de `venus.media.default-resource-type`.

`MediaDeliveryType` é modelado com os dois valores porque a coluna aceita ambos, mas o
service sempre grava o default da configuração (`upload`). O cliente nunca escolhe
(guia §9).

### 5.2 `entity/media/MediaAsset.java`

```java
@Entity
@Table(name = "media_assets")
@AttributeOverride(name = "id", column = @Column(name = "media_asset_id"))
public class MediaAsset extends AuditableEntity
```

| Campo Java | Coluna | Observação |
| --- | --- | --- |
| `user` | `fk_user_id` | `@ManyToOne(LAZY)`, nullable |
| `productVersion` | `fk_product_version_id` | `@ManyToOne(LAZY)`, nullable |
| `purpose` | `purpose` | `MediaPurpose`, not null |
| `provider` | `provider` | `String`, not null |
| `resourceType` | `resource_type` | `String`, not null |
| `deliveryType` | `delivery_type` | `MediaDeliveryType`, not null |
| `publicId` | `public_id` | `String`, not null |
| `assetId` | `asset_id` | `String` |
| `version` | `version` | `Long`; campo simples, **sem** `@Version` (não é lock otimista) |
| `secureUrl` | `secure_url` | `String` |
| `format` | `format` | `String` |
| `width` / `height` | `width` / `height` | `Integer` |
| `bytes` | `bytes` | `Long` |
| `folder` | `folder` | `String` |
| `originalFilename` | `original_filename` | `String` |
| `altText` | `alt_text` | `String` |
| `sortOrder` | `sort_order` | `Integer` |
| `status` | `status` | `MediaStatus`, not null |

As duas FKs são nullable porque a CHECK do banco é quem garante a exclusividade
(avatar aponta só para `users`, foto aponta só para `product_versions`). A entidade não
duplica essa regra; o service é quem preenche o par correto.

### 5.3 `repository/jpa/media/MediaAssetRepository.java`

```java
@EntityGraph(attributePaths = "user")
Optional<MediaAsset> findByUserIdAndPurposeAndStatusIn(
        Long userId, MediaPurpose purpose, Collection<MediaStatus> statuses);

@EntityGraph(attributePaths = "productVersion")
List<MediaAsset> findByProductVersionIdAndPurposeAndStatusInOrderBySortOrderAscIdAsc(
        Long productVersionId, MediaPurpose purpose, Collection<MediaStatus> statuses);

@Query("select coalesce(max(m.sortOrder), -1) from MediaAsset m "
     + "where m.productVersion.id = :productVersionId "
     + "and m.purpose = com.venus.crud.entity.enums.MediaPurpose.PRODUCT_PHOTO "
     + "and m.status in :statuses")
int findMaxSortOrder(Long productVersionId, Collection<MediaStatus> statuses);
```

As leituras sempre passam `statuses = (PENDING, ACTIVE)`, que é o mesmo predicado dos dois
índices parciais do banco — a query aproveita o índice em vez de recriar a lógica.

O `-1` no `coalesce` faz o primeiro `sortOrder` calculado ser `0`, coerente com o guia
(avatar fica em `0`, primeira foto de produto também).

### 5.4 DTOs (`dto/jpa/{response,patch}/media/`)

```java
public record MediaAssetResponse(
        Long mediaAssetId,
        MediaPurpose purpose,
        String url,
        String publicId,
        String altText,
        Integer width,
        Integer height,
        String format,
        Integer sortOrder,
        MediaStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

public record MediaAssetPatchRequest(
        String altText,
        Integer sortOrder
) {}
```

`url` é `secure_url`. `publicId` vai junto porque o guia §7.1 permite devolvê-lo para
diagnóstico/admin — mas a camada visual não deve depender dele.

Não existe `MediaAssetRequest`: a criação é sempre `multipart/form-data`, não JSON.

### 5.5 `mapper/jpa/media/MediaAssetMapper.java`

```java
@Mapper(config = VenusMapperConfig.class)
public interface MediaAssetMapper {

    @Mapping(target = "mediaAssetId", source = "id")
    @Mapping(target = "url", source = "secureUrl")
    MediaAssetResponse toResponse(MediaAsset entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(MediaAssetPatchRequest request, @MappingTarget MediaAsset entity);
}
```

O `patchEntity` precisa de `@Mapping(target = ..., ignore = true)` para todos os campos que
o PATCH não toca, porque `VenusMapperConfig` usa `unmappedTargetPolicy = ERROR`.

## 6. Serviços

Três colaboradores, cada um com uma responsabilidade.

### 6.1 `MediaFileValidator`

`validate(MultipartFile file, MediaPurpose purpose)`. Lê os limites de `MediaProperties`
conforme o purpose e verifica, nesta ordem:

1. arquivo não vazio
2. `content-type` está em `allowed-image-types`
3. `size` ≤ `max-bytes` do purpose
4. dimensões ≤ `max-width` / `max-height` do purpose

A verificação de dimensão decodifica os bytes com `ImageIO.read` **antes** do upload. Não
dá para usar o `width`/`height` que o Cloudinary devolve: nesse ponto o arquivo já subiu, e
rejeitar exigiria um `destroy` de compensação para um caso que é previsível localmente.
Se `ImageIO.read` devolver `null` (bytes que não são imagem decodificável apesar do
content-type declarado), o arquivo é rejeitado.

Qualquer falha lança `InvalidFileException` com mensagem específica.

### 6.2 `CloudinaryStorageService`

Gateway fino sobre o SDK. Não conhece JPA nem `MediaAsset`.

```java
CloudinaryUpload upload(MultipartFile file, MediaPurpose purpose, String publicId);
void destroy(String publicId, MediaPurpose purpose);
```

`purpose` seleciona **o bean `Cloudinary` e o upload preset**: `AVATAR` usa
`usersCloudinary` + `venus-users`, `PRODUCT_PHOTO` usa `productsCloudinary` +
`venus-products`. O `destroy` também precisa do purpose — tentar apagar um asset de produto
com a chave de users falha.

Parâmetros do upload:

```java
ObjectUtils.asMap(
    "public_id", publicId,
    "upload_preset", preset,
    "resource_type", mediaProperties.defaultResourceType(),
    "type", mediaProperties.defaultDeliveryType(),
    "overwrite", false
)
```

O retorno é o record:

```java
public record CloudinaryUpload(
        String publicId, String assetId, Long version, String secureUrl,
        String format, Integer width, Integer height, Long bytes, String folder
) {}
```

**Importante:** o `publicId` gravado no banco é o que **volta na resposta**, nunca o que foi
enviado. Se o preset `venus-users`/`venus-products` definir um `folder` no dashboard, o
Cloudinary concatena esse folder com o `public_id` enviado — ler o valor de volta é o que
mantém o registro correto sem o backend precisar conhecer a configuração do preset. É
também o que o guia §5.4 passo 6 manda fazer.

`IOException` e erros do SDK viram `ServiceUnavailableException`, no mesmo espírito do
`executeOrFail` dos outros services.

### 6.3 `MediaAssetWriter`

Bean com **todo** o acesso a banco, cada método `@Transactional`. Usa `executeOrFail`
idêntico ao dos demais services.

```java
void ensureUserExists(Long userId);                    // readOnly
void ensureProductVersionExists(Long productVersionId); // readOnly
MediaAsset registerAvatar(Long userId, CloudinaryUpload upload, String originalFilename);
MediaAsset registerProductPhoto(Long productVersionId, CloudinaryUpload upload,
                                String altText, Integer sortOrder, String originalFilename);
MediaAsset markDeleted(Long mediaAssetId);
MediaAsset patch(Long mediaAssetId, MediaAssetPatchRequest request);
```

`registerAvatar` faz, na mesma transação:

1. busca o avatar em `(PENDING, ACTIVE)` do usuário
2. se existir, seta `status = DELETED` e chama `saveAndFlush`
3. insere o novo com `status = ACTIVE`

O `saveAndFlush` do passo 2 é **obrigatório**. O índice parcial `ux_media_user_avatar` é
verificado por statement: sem o flush explícito, o Hibernate pode ordenar o INSERT antes do
UPDATE e a transação falha com violação de unicidade.

`registerProductPhoto` resolve `sortOrder` nulo como `findMaxSortOrder(...) + 1`.

### 6.4 `MediaAssetService`

Orquestra. Os métodos de leitura são `@Transactional(readOnly = true)`; os de escrita
**não** são anotados.

```java
MediaAssetResponse findAvatar(Long userId);
List<MediaAssetResponse> findProductPhotos(Long productVersionId);
MediaAssetResponse uploadAvatar(Long userId, MultipartFile file);
MediaAssetResponse uploadProductPhoto(Long productVersionId, MultipartFile file,
                                      String altText, Integer sortOrder);
MediaAssetResponse patch(Long mediaAssetId, MediaAssetPatchRequest request);
void delete(Long mediaAssetId);
```

**Por que a escrita não é transacional aqui.** O upload no Cloudinary e o INSERT no
PostgreSQL são sistemas diferentes: a transação do banco não desfaz o upload (guia §10). A
compensação precisa rodar **depois** que o rollback aconteceu, o que é impossível dentro do
próprio método transacional. E chamar um método `@Transactional` do mesmo bean não
funcionaria: a auto-invocação passa por baixo do proxy do Spring e a anotação é ignorada em
silêncio. Daí o par `MediaAssetService` (orquestra, sem transação) + `MediaAssetWriter`
(bean separado, transacional) — o proxy é atravessado de verdade.

Fluxo de `uploadAvatar` (o de foto de produto é idêntico trocando o purpose):

```
1. validator.validate(file, AVATAR)
2. writer.ensureUserExists(userId)          → 404 antes de gastar upload
3. publicId = "users/" + userId + "/avatar/" + UUID.randomUUID()
4. upload = storage.upload(file, AVATAR, publicId)
5. try   → writer.registerAvatar(userId, upload, file.getOriginalFilename())
   catch → storage.destroy(upload.publicId(), AVATAR)   [compensação, guia §10]
            log.error(...) e relança a exceção original
6. mapper.toResponse(saved)
```

O passo 2 vem antes do 4 de propósito: validar a existência da entidade depois do upload
deixaria um asset órfão no Cloudinary por um erro totalmente previsível.

Se a compensação do passo 5 **também** falhar, o `destroy` é registrado em log de erro com
o `public_id` e a exceção original é relançada mesmo assim. O asset fica órfão no
Cloudinary; a rotina de reconciliação que o guia §10 sugere não faz parte deste escopo
(ver seção 10).

`delete` faz: `writer.markDeleted(id)` (transacional, resolve 404) e depois
`storage.destroy(...)` best-effort. Se o `destroy` falhar, a linha permanece `deleted` e a
falha vai para log — é a ordem que o guia §10 recomenda para ser auditável.

`public_id` sempre montado pelo backend (guia §11):

- avatar: `users/{userId}/avatar/{uuid}`
- foto: `products/{productId}/versions/{versionId}/{uuid}`

## 7. Controllers (`controller/jpa/media/`)

### `UserAvatarController` — `/api/users/{userId}/avatar`

| Método | Entrada | Saída |
| --- | --- | --- |
| `POST` | `multipart/form-data`, `file` | `201 Created` + `MediaAssetResponse`, header `Location` |
| `GET` | — | `200` + `MediaAssetResponse`, ou `404` se não houver ativo |
| `DELETE` | — | `204 No Content` |

### `ProductVersionPhotoController` — `/api/product-versions/{productVersionId}/photos`

| Método | Entrada | Saída |
| --- | --- | --- |
| `POST` | `multipart/form-data`: `file`, `altText` (opcional), `sortOrder` (opcional) | `201 Created` + `MediaAssetResponse` |
| `GET` | — | `200` + `List<MediaAssetResponse>` ordenada por `sortOrder, mediaAssetId` |

### `MediaAssetController` — `/api/media/{mediaAssetId}`

| Método | Entrada | Saída |
| --- | --- | --- |
| `PATCH` | `MediaAssetPatchRequest` (`altText`, `sortOrder`) | `200` + `MediaAssetResponse` |
| `DELETE` | — | `204 No Content` |

O `PATCH` é também o endpoint de reordenação do guia §6 (`REORDER`): mexe só em
`sort_order`, sem novo upload.

O `Location` do `POST` é montado com `ServletUriComponentsBuilder`, como nos demais
controllers do projeto.

Não há colisão de rota: `UserController` e `UserFullProfileController` já dividem
`/api/users`, e os mapeamentos existentes (`/{id}`, `/search`, `/{userId}/full-profile`) não
conflitam com `/{userId}/avatar`. O mesmo vale para `/api/product-versions/{id}/photos`
frente a `/{id}`, `/search` e `/product/{productId}` do `ProductVersionController`.

## 8. Tratamento de erros

Novo `InvalidFileException extends RuntimeException` em `exception/`, e dois handlers novos
no `GlobalExceptionHandler`:

| Exceção | Status | Situação |
| --- | --- | --- |
| `InvalidFileException` | `400 Bad Request` | tipo, tamanho ou dimensão fora da regra |
| `MaxUploadSizeExceededException` | `413 Payload Too Large` | arquivo maior que o limite do multipart |

O handler hoje cobre `404`, `409`, `503`, validação de body e o genérico `500`. Não havia
caminho de `400` para erro de negócio; upload inválido é o primeiro caso que precisa disso.

## 9. Integração nos agregados

`UserFullProfileResponse` ganha o campo `MediaAssetResponse avatar` (nulo quando não há).
`UserFullProfileService` recebe `MediaAssetRepository` e `MediaAssetMapper` no construtor e
resolve o avatar com o mesmo `executeOrFail` que usa para os outros blocos.

`ProductFullResponse` ganha `List<MediaAssetResponse> photos`, com as fotos da
`currentVersion` (lista vazia quando não há `currentVersion`). `ProductFullService` faz a
busca dentro do bloco `if (currentVersion != null)` que já existe, junto de packaging, label
e claims.

`ProductVersionResponse` **não** é alterado. `VenusMapperConfig` usa
`unmappedTargetPolicy = ERROR`: adicionar um campo lá quebraria a compilação do
`ProductVersionMapper` e de todos os mappers que o reutilizam. Foto de produto aparece só no
agregado.

## 10. Fora de escopo

Itens do guia que **não** entram nesta entrega, com o motivo:

- **Autenticação e autorização.** `SecurityConfig` é `anyRequest().permitAll()` e não existe
  filtro de JWT/Firebase no projeto. Os passos "validar token" e "verificar se o usuário
  pode alterar aquele userId" (guia §5.4 passos 1–2, §11) não têm como ser implementados.
  Os endpoints de upload nascem abertos. **É dívida técnica registrada, não um esquecimento.**
- **Estado `PENDING`.** O guia trata o registro `PENDING` prévio como opcional ("quando esse
  fluxo for adotado", §1). Aqui o registro nasce `ACTIVE` depois do upload confirmado, com
  compensação em caso de falha. `PENDING` e `FAILED` existem no enum e nas queries, mas
  nenhum fluxo os grava.
- **Rotina de reconciliação** de assets órfãos no Cloudinary e retry de `destroy` (guia §10).
- **Entrega `authenticated`** e geração de URL assinada (guia §9).
- **URLs transformadas / thumbnails** (guia §8). A API devolve `secure_url` como veio.
- **Testes de integração.** O projeto não tem nenhum teste hoje; adicionar a suíte é um
  trabalho próprio, fora deste desenho.

## 11. Arquivos

**Novos (23):**

```
config/CloudinaryConfig.java
config/CloudinaryProperties.java
config/MediaProperties.java

entity/enums/MediaPurpose.java
entity/enums/MediaStatus.java
entity/enums/MediaDeliveryType.java
entity/converter/MediaPurposeConverter.java
entity/converter/MediaStatusConverter.java
entity/converter/MediaDeliveryTypeConverter.java
entity/media/MediaAsset.java

repository/jpa/media/MediaAssetRepository.java
mapper/jpa/media/MediaAssetMapper.java
dto/jpa/response/media/MediaAssetResponse.java
dto/jpa/patch/media/MediaAssetPatchRequest.java

service/jpa/media/CloudinaryUpload.java
service/jpa/media/CloudinaryStorageService.java
service/jpa/media/MediaFileValidator.java
service/jpa/media/MediaAssetWriter.java
service/jpa/media/MediaAssetService.java

controller/jpa/media/UserAvatarController.java
controller/jpa/media/ProductVersionPhotoController.java
controller/jpa/media/MediaAssetController.java

exception/InvalidFileException.java
```

**Alterados (7):**

```
pom.xml                                        (dependência + property de versão)
src/main/resources/application.yml             (multipart, cloudinary, venus.media)
exception/GlobalExceptionHandler.java          (400 e 413)
dto/jpa/response/fullstage/UserFullProfileResponse.java   (+ avatar)
dto/jpa/response/fullstage/ProductFullResponse.java       (+ photos)
service/jpa/fullstage/UserFullProfileService.java
service/jpa/fullstage/ProductFullService.java
```

O `.env` já está completo e não precisa de alteração.

## 12. Verificação

O usuário roda o build. Depois de `mvn clean package`, a verificação manual é:

1. `POST /api/users/{id}/avatar` com JPEG válido → `201`, e a linha aparece em
   `venus.v_user_avatars`
2. Repetir o upload para o mesmo usuário → `201`, e a query de duplicidade do guia §16
   (`GROUP BY fk_user_id HAVING COUNT(*) > 1`) continua devolvendo zero linhas
3. `POST` com PDF, com arquivo de 20 MB e com PNG de 5000×5000 → `400` nos três, com
   mensagens diferentes
4. `POST /api/product-versions/{id}/photos` três vezes → `sortOrder` 0, 1, 2; o `GET`
   devolve nessa ordem
5. `PATCH /api/media/{id}` com `sortOrder` trocado → o `GET` reflete a nova ordem
6. `DELETE /api/media/{id}` → `204`, linha vira `status = deleted`, some do `GET`, e o
   asset some do dashboard do Cloudinary
7. `GET /api/users/{id}/full-profile` traz `avatar` preenchido; `GET /api/products/{id}/full`
   traz `photos` ordenado
8. Credenciais inválidas no `.env` → `503` com mensagem de indisponibilidade, não `500`
