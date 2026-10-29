# 5. Validação das entradas

[Índice](../DECISOES.md) · anterior: [4. Pacotes MVC](04-pacotes-mvc.md) · próxima: [6. Tratamento de erros](06-tratamento-de-erros.md)

**O que decidimos:** os DTOs de entrada usam as anotações do Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@Email`), e o controller confere com `@Valid` antes de chamar o service. As constraints do banco (unique, check e not null) continuam lá como uma segunda barreira.

**Por quê:** o erro aparece logo na entrada, antes de qualquer consulta, e volta com a mensagem de cada campo no `details` da resposta. A constraint do banco garante o dado certo mesmo quando duas requisições chegam ao mesmo tempo.

**O que descartamos:** validar com `if` dentro de cada service, que espalharia a mesma checagem por vários lugares, e confiar só no banco, que devolveria um erro sem dizer qual campo está errado.

**Onde ver:**
- [`ProductRequest.java`, linhas 9 a 15](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/dto/jpa/request/product/ProductRequest.java#L9-L15): as anotações no DTO
- [`ProductController.java`, linha 78](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/controller/jpa/product/ProductController.java#L78): o `@Valid`
- [`GlobalExceptionHandler.java`, linha 105](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/exception/GlobalExceptionHandler.java#L105): a resposta com o erro de cada campo
