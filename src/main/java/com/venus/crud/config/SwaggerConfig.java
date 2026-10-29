package com.venus.crud.config;

import com.venus.crud.exception.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI venusCrudOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Venus CRUD API")
                        .description(description())
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Venus System")
                                .url("https://github.com/Venus-System/Venus-CRUD")))
                .components(new Components()
                        .addSchemas(OpenApiErrorResponsesCustomizer.ERROR_SCHEMA_NAME, errorResponseSchema())
                        .addSecuritySchemes(OpenApiSecurityCustomizer.SECURITY_SCHEME_NAME, bearerScheme()));
    }

    private String description() {
        return """
                API central do Venus System: catálogo de produtos e ingredientes, perfil e preferências do \
                usuário, avaliações, scores e sessões de scan.

                **Erros** — toda resposta de erro usa o mesmo corpo (`ErrorResponse`), com `timestamp`, \
                `status`, `error`, `message`, `path` e `details`. O `details` só vem preenchido em erro de \
                validação, com uma linha por campo recusado.

                **Listagens paginadas** — aceitam `page`, `size` e `sort` (ex: `sort=name,asc`) e devolvem um \
                `Slice`: traz `content` e `last`, e não traz contagem total de registros.

                **Autenticação** — o catálogo é aberto para leitura. As outras rotas exigem o cabeçalho \
                `Authorization: Bearer <token>`, com um de dois tokens: o **ID token do Firebase**, que o app recebe \
                no login do usuário, ou o **token de administrador**, devolvido por `POST /api/auth/admin/login` e \
                válido por 8 horas. Sem token, a resposta é 401; com um token sem permissão para a rota ou para o \
                registro, 403. Use o botão **Authorize** para testar aqui.
                """;
    }

    private SecurityScheme bearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("ID token do Firebase (usuário do app) ou o token de POST /api/auth/admin/login (administrador).");
    }

    private Schema<?> errorResponseSchema() {
        return ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class).resolveAsRef(false))
                .schema;
    }
}
