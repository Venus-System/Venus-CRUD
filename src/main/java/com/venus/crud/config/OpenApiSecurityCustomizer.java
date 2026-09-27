package com.venus.crud.config;

import com.venus.crud.security.SecurityErrorHandler;
import com.venus.crud.security.SecurityRoutes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OpenApiSecurityCustomizer implements OpenApiCustomizer {

    static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }
        openApi.getPaths().forEach((path, pathItem) -> pathItem.readOperationsMap().forEach((method, operation) -> {
            if (SecurityRoutes.isPublic(HttpMethod.valueOf(method.name()), path)) {
                return;
            }
            operation.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
            OpenApiErrorResponsesCustomizer.addResponse(operation.getResponses(), HttpStatus.UNAUTHORIZED,
                    "Token ausente, inválido ou vencido. Mande o token no cabeçalho Authorization: Bearer <token>.",
                    OpenApiErrorResponsesCustomizer.example(HttpStatus.UNAUTHORIZED,
                            SecurityErrorHandler.MISSING_TOKEN_MESSAGE, path, List.of()));
            OpenApiErrorResponsesCustomizer.addResponse(operation.getResponses(), HttpStatus.FORBIDDEN,
                    "O token é válido, mas não tem permissão para esta rota ou para este registro.",
                    OpenApiErrorResponsesCustomizer.example(HttpStatus.FORBIDDEN,
                            SecurityErrorHandler.ACCESS_DENIED_MESSAGE, path, List.of()));
        }));
    }
}
