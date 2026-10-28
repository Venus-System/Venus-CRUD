# Justificativa das decisões de Desenvolvimento 2

Aqui está o porquê das decisões que a gente tomou no Venus-CRUD para cada requisito de Desenvolvimento 2. Cada requisito tem o seu arquivo: ele começa pela escolha principal do requisito e depois mostra as partes que são só deste projeto. Toda decisão diz o que a gente decidiu, por quê, o que a gente descartou e onde ver no código.

Os links levam para a linha exata num commit fixo, o `f900a30` no Venus-CRUD e o `ce343fe` no Venus-Banco, então eles não mudam quando o código andar.

| Requisito | O que tem | Arquivo |
|---|---|---|
| 1. API REST em Java com Spring MVC acessando o Postgres | Spring Boot 3.3.4 com Spring MVC, Java 21 com virtual threads e o pool de 3 conexões. | [01-api-rest-e-postgres.md](decisoes/01-api-rest-e-postgres.md) |
| 2. Spring Data JPA | `ddl-auto: validate`, `Slice`, enums em minúsculo, datas feitas pelo banco, `@Formula`, `@EntityGraph` e buscas com os filtros juntos. | [02-spring-data-jpa.md](decisoes/02-spring-data-jpa.md) |
| 3. Métodos do CRUD pela API | O formato das rotas, o `PATCH` com `JsonNullable`, o `ServletUriComponentsBuilder`, as imagens no Cloudinary e as rotas pelo `userId`. | [03-metodos-do-crud.md](decisoes/03-metodos-do-crud.md) |
| 4. Pacotes no padrão MVC | As camadas, por que o MapStruct, os quatro métodos de cada mapper e onde fica cada regra de negócio. | [04-pacotes-mvc.md](decisoes/04-pacotes-mvc.md) |
| 5. Validação das entradas | Bean Validation com `@Valid` e a constraint do banco como segunda barreira. | [05-validacao.md](decisoes/05-validacao.md) |
| 6. Tratamento de exceções centralizado | O `GlobalExceptionHandler`, o SQLState virando 409, 422 ou 503, e o `executeOrFail`. | [06-tratamento-de-erros.md](decisoes/06-tratamento-de-erros.md) |
| 7. Documentação automática com Swagger | O springdoc e os customizers dos erros e do token. | [07-swagger.md](decisoes/07-swagger.md) |
| 8. Acionar as procedures e functions da Modelagem de Dados | O `SELECT` da function, o `CALL` da procedure e a leitura do usuário depois dela. | [08-procedures-e-functions.md](decisoes/08-procedures-e-functions.md) |
| 9. Autenticação e autorização com Spring Security | Os dois tokens separados pelo emissor, os papéis, a checagem de dono e o erro de segurança no mesmo formato. | [09-seguranca.md](decisoes/09-seguranca.md) |
| 10. API REST em Java para o banco NoSQL | Por que o Mongo e o Cloudinary ficam na mesma API, o reenvio sem duplicar, os ingredientes, a trava dos admins e a sincronização com o catálogo. | [10-api-nosql.md](decisoes/10-api-nosql.md) |
