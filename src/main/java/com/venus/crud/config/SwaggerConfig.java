package com.venus.crud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI venusCrudOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Venus CRUD API")
                        .description("Documentação da API do projeto Venus CRUD")
                        .version("v0.0.1"));
    }
}