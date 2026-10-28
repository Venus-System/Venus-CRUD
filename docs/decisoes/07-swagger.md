# 7. Documentação automática com Swagger

[Índice](../DECISOES.md) · anterior: [6. Tratamento de erros](06-tratamento-de-erros.md) · próxima: [8. Procedures e functions](08-procedures-e-functions.md)

**O que decidimos:** usar o springdoc-openapi, que gera o Swagger a partir do código, e criar dois customizers. Um coloca em todas as rotas os erros que elas podem devolver e o código certo de sucesso (201 e 204), e o outro coloca o cadeado do token nas rotas protegidas.

**Por quê:** a documentação sai do próprio código, então não fica desatualizada. Com os customizers, a gente não precisa escrever os erros à mão em mais de 300 rotas, e quem usa a API já vê no Swagger os erros possíveis e onde precisa de token.

**O que descartamos:** anotar os erros rota por rota, que ia repetir o mesmo texto centenas de vezes e acabar esquecendo alguma.

**Onde ver:**
- [`pom.xml`, linha 76](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/pom.xml#L76): o springdoc
- [`OpenApiErrorResponsesCustomizer.java`, linha 28](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/OpenApiErrorResponsesCustomizer.java#L28): os erros e o código de sucesso de cada rota
- [`OpenApiSecurityCustomizer.java`, linha 14](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/java/com/venus/crud/config/OpenApiSecurityCustomizer.java#L14): o cadeado nas rotas protegidas
- [README, Swagger](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/README.md#documentação-da-api-swagger): como abrir e testar
