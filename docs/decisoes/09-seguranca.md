# 9. Autenticação e autorização com Spring Security

[Índice](../DECISOES.md) · anterior: [8. Procedures e functions](08-procedures-e-functions.md) · próxima: [10. API NoSQL](10-api-nosql.md)

**O que decidimos:** a API usa o resource server do Spring Security e aceita dois tokens, separados pelo emissor (`iss`):

- o token do Firebase, de quem usa o app e o site, conferido com as chaves públicas do Google;
- o JWT do admin, que a própria API gera no login do painel (`POST /api/auth/admin/login`, com a senha conferida pelo BCrypt) e assina com HS256.

O admin tem três papéis: `ADMIN`, `MODERATOR` e `ANALYST`. As permissões são por grupo de rota, e nas rotas com dado de usuário o `OwnershipGuard` confere se quem chamou é o dono.

**Por quê:** o login do app já é feito pelo Firebase, então a API só precisa conferir o token e não guarda senha de usuário. O admin não usa o app e precisa de papéis diferentes, por isso ele tem um login próprio. Com o resource server, conferir os dois tokens é configuração do próprio Spring.

**O que descartamos:** o Firebase Admin SDK, que precisaria de um arquivo de conta de serviço guardado no servidor, e um login só para todo mundo, que misturaria o usuário do app com o admin.

**Onde ver:**
- [`SecurityConfig.java`, linha 46](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/SecurityConfig.java#L46): as regras de acesso
- [`SecurityConfig.java`, linhas 76 a 84](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/SecurityConfig.java#L76-L84): os dois tokens separados pelo emissor
- [`AdminTokenService.java`, linha 53](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/security/AdminTokenService.java#L53): o token do admin com HS256
- [`SecurityRoutes.java`, linha 17](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/security/SecurityRoutes.java#L17): os grupos de rota
- [`OwnershipGuard.java`, linha 48](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/security/OwnershipGuard.java#L48): a checagem de dono
- [README, Segurança](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/README.md#segurança): a tabela de quem pode o quê

## 9.1 Erro de segurança no mesmo formato

**O que decidimos:** o `SecurityErrorHandler` responde o 401 (sem token ou com token inválido) e o 403 (sem permissão) com o mesmo `ErrorResponse` dos outros erros, e a mensagem diz o que aconteceu, como "Token invalido ou expirado.".

**Por quê:** o 401 e o 403 acontecem no filtro do Spring Security, antes de chegar no controller, então o `GlobalExceptionHandler` nem fica sabendo deles. Sem esse handler, os dois voltavam sem corpo, e o app e o site teriam que tratar dois formatos de erro.

**O que descartamos:** deixar a resposta padrão do Spring Security.

**Onde ver:**
- [`SecurityErrorHandler.java`, linhas 36 a 58](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/security/SecurityErrorHandler.java#L36-L58): o 401 e o 403 com o `ErrorResponse`
- [`SecurityConfig.java`, linhas 64 a 70](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/SecurityConfig.java#L64-L70): o handler ligado no Spring Security
