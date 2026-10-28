# 1. API REST em Java com Spring MVC acessando o Postgres

[Índice](../DECISOES.md) · próxima: [2. Spring Data JPA](02-spring-data-jpa.md)

**O que decidimos:** fazer a API com Spring Boot 3.3.4 e Spring MVC, em Java 21, com as virtual threads ligadas. Os dados ficam no Postgres, no schema `venus`.

**Por quê:** o Spring MVC é o que a matéria pede e é o jeito mais simples de fazer uma API REST que fala com banco relacional. Como o JPA fica esperando o banco responder, a gente ligou as virtual threads, por conta que assim cada requisição esperando o Postgres não segura uma thread do servidor.

**O que descartamos:** o WebFlux, que é reativo e não combina com o JPA, aí a gente teria que trocar todo o acesso ao banco por um driver reativo. Também descartamos espalhar `CompletableFuture` e `@Async` pelos services, que as virtual threads já resolvem sem mudar o código.

**Onde ver:**
- [`pom.xml`, linha 10](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/pom.xml#L10): Spring Boot 3.3.4
- [`pom.xml`, linha 21](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/pom.xml#L21): Java 21
- [`pom.xml`, linha 30](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/pom.xml#L30): Spring MVC (`spring-boot-starter-web`)
- [`application.yml`, linhas 8 a 10](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/resources/application.yml#L8-L10): virtual threads
- [`application.yml`, linha 13](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/resources/application.yml#L13): conexão com o Postgres

## 1.1 Pool com no máximo 3 conexões

**O que decidimos:** o Hikari, que é o pool de conexões que o Spring usa, fica com no máximo 3 conexões abertas (`maximum-pool-size: 3`) e com só 1 quando a API está parada (`minimum-idle: 1`). Esses valores ficam fixos no `application.yml`.

**Por quê:** o Postgres está no Aiven, num plano com poucas vagas de conexão, e essas vagas são divididas entre a API no Render, a API rodando na máquina de cada um, a API de Classificação, a API de IA e o DBeaver. Sem configurar nada, o Hikari deixa 10 conexões abertas o tempo todo. No deploy, o Render só desliga a instância antiga depois que a nova sobe, aí só o CRUD chegava a pedir 20 conexões, e o deploy caiu com o erro `remaining connection slots are reserved`. Para o tanto de acesso que o projeto tem, 3 conexões dão conta: quando as 3 estão ocupadas, a próxima requisição espera uma ficar livre em vez de abrir outra. O valor ficou fixo, e não numa variável do `.env`, para valer na API de todo mundo sem cada um mudar o seu `.env`.

**O que descartamos:** deixar o padrão de 10 conexões, e colocar o tamanho do pool numa variável de ambiente.

**Onde ver:**
- [`application.yml`, linhas 16 a 18](https://github.com/Venus-System/Venus-CRUD/blob/f900a30eef7bb99ce21e35b0ac3c011371fbeae3/src/main/resources/application.yml#L16-L18): o tamanho do pool
