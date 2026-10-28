# 8. Acionar as procedures e functions da Modelagem de Dados

[Índice](../DECISOES.md) · anterior: [7. Swagger](07-swagger.md) · próxima: [9. Segurança](09-seguranca.md)

**O que decidimos:** a API chama as rotinas do banco pelo nome, com uma `@Query` nativa no repository. A function `venus.fn_register_user_access` é chamada com `SELECT` quando o app abre (`POST /api/users/me/access`) e registra o acesso que alimenta o DAU. A procedure `venus.sp_set_user_active` é chamada com `CALL` quando o admin ativa ou desativa um usuário (`PATCH /api/users/{id}/active`).

**Por quê:** as duas regras já existem no banco, feitas pelo time de banco, então a API só chama em vez de repetir a regra em Java. A function é o único jeito de gravar os acessos, por conta que o usuário da API não tem permissão de `INSERT` na tabela `user_access_events`. A procedure já sabe que usuário bloqueado ou pendente não pode ser ativado nem desativado por ela. Quando isso acontece, o banco devolve o erro `VE001`, e a API transforma em 409.

**O que descartamos:** gravar os acessos e trocar o status pelo JPA. Isso repetiria no Java uma regra que já é do banco, e no caso dos acessos a API nem teria permissão.

**Onde ver:**
- [`UserRepository.java`, linha 29](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/jpa/user/UserRepository.java#L29): o `SELECT` da function
- [`UserRepository.java`, linhas 34 a 36](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/jpa/user/UserRepository.java#L34-L36): o `CALL` da procedure
- [`UserService.java`, linha 129](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/user/UserService.java#L129): ativar ou desativar pela procedure
- [`UserController.java`, linha 111](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/user/UserController.java#L111): a rota do admin
- [`UserRepositoryIntegrationTest.java`](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/test/java/com/venus/crud/repository/jpa/user/UserRepositoryIntegrationTest.java): o teste que chama a procedure num Postgres de verdade

## 8.1 Ler o usuário de novo depois da procedure

**O que decidimos:** o `CALL` da procedure tem `@Modifying(clearAutomatically = true)`. O service busca o usuário antes de chamar a procedure, para devolver 404 se ele não existir, e busca de novo depois, para devolver o status novo.

**Por quê:** a procedure muda a linha direto no banco, sem o Hibernate saber. Sem o `clearAutomatically`, a segunda busca viria do cache do Hibernate, com o usuário da primeira busca, e a resposta voltaria com o status antigo.

**O que descartamos:** trocar o status na mão no Java só para montar a resposta, que poderia devolver um valor diferente do que a procedure gravou.

**Onde ver:**
- [`UserRepository.java`, linha 34](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/repository/jpa/user/UserRepository.java#L34): o `clearAutomatically`
- [`UserService.java`, linhas 130 a 138](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/user/UserService.java#L130-L138): a busca antes e depois da procedure
