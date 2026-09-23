package com.venus.crud.config;

import com.venus.crud.exception.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
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
                        .addSchemas(OpenApiErrorResponsesCustomizer.ERROR_SCHEMA_NAME, errorResponseSchema()));
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
                """;
    }

    private Schema<?> errorResponseSchema() {
        return ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class).resolveAsRef(false))
                .schema;
    }
}
