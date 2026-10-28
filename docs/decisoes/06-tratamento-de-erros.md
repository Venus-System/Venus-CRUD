# 6. Tratamento de exceções centralizado

[Índice](../DECISOES.md) · anterior: [5. Validação](05-validacao.md) · próxima: [7. Swagger](07-swagger.md)

**O que decidimos:** um `GlobalExceptionHandler` com `@RestControllerAdvice` trata todos os erros e devolve sempre o mesmo formato (`ErrorResponse`). Os erros do banco são traduzidos pelo SQLState: unique e FK viram 409, not null e check viram 422, banco fora do ar vira 503, e erro inesperado vira 500 com uma mensagem genérica.

**Por quê:** o app e o site leem um formato só de erro, e nenhum controller precisa de try/catch. Separar o 409 do 422 diz para quem chamou se o problema é conflito com outro dado ou um dado que faltou. No 500 a mensagem é genérica para não mostrar nome de tabela ou detalhe do banco para quem chama, e o erro completo fica no log.

**O que descartamos:** try/catch em cada controller, e o jeito que a gente usava antes, em que todo erro de integridade virava 409 e escondia quando faltava um campo obrigatório.

**Onde ver:**
- [`GlobalExceptionHandler.java`, linha 31](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/exception/GlobalExceptionHandler.java#L31): o `@RestControllerAdvice`
- [`GlobalExceptionHandler.java`, linha 36](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/exception/GlobalExceptionHandler.java#L36): a mensagem genérica do 500
- [`DataIntegrityViolationTranslator.java`, linhas 19 a 22](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/exception/DataIntegrityViolationTranslator.java#L19-L22): os SQLStates de unique, FK, not null e check
- [`ErrorResponse.java`, linha 6](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/exception/ErrorResponse.java#L6): o formato do erro

## 6.1 `executeOrFail` em cada service

**O que decidimos:** as chamadas ao repository passam pelo `executeOrFail`, um método privado do service que recebe a chamada e a mensagem do que estava sendo feito. Erro de integridade vai para o tradutor do SQLState (409 ou 422). Qualquer outro erro do banco é logado com a mensagem do service, e aí a falha passageira, como conexão caída ou timeout, vira 503, e o resto vira 500.

**Por quê:** o log já sai dizendo o que o service estava fazendo, como "Falha ao consultar favoritos do usuario", e isso ajuda a achar o problema no log do Render. O 503 diz para o app que vale tentar de novo depois, e o 500 não. O método ficou no próprio service para dar para ler o fluxo de cima para baixo sem abrir outra classe.

**O que descartamos:** tirar as 52 cópias do `executeOrFail` e traduzir tudo no `GlobalExceptionHandler`.

**Onde ver:**
- [`ProductService.java`, linhas 119 a 128](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/service/jpa/product/ProductService.java#L119-L128): o `executeOrFail`
- [`DataAccessFailureTranslator.java`, linhas 13 a 24](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/exception/DataAccessFailureTranslator.java#L13-L24): a separação entre falha passageira e o resto
