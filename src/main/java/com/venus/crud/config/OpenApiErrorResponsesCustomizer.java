package com.venus.crud.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

@Component
public class OpenApiErrorResponsesCustomizer implements OperationCustomizer {

    static final String ERROR_SCHEMA_NAME = "ErrorResponse";

    private static final String ERROR_SCHEMA_REF = "#/components/schemas/" + ERROR_SCHEMA_NAME;
    private static final String APPLICATION_JSON = "application/json";
    private static final String OK = "200";
    private static final String EXEMPLO_TIMESTAMP = "2026-09-22T14:30:00-03:00";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        boolean hasRequestBody = false;
        boolean hasPathVariable = false;
        boolean hasUpload = false;
        boolean hasTypedParameter = false;

        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
            if (parameter.hasParameterAnnotation(RequestBody.class)) {
                hasRequestBody = true;
            }
            if (parameter.hasParameterAnnotation(PathVariable.class)) {
                hasPathVariable = true;
            }
            if (MultipartFile.class.isAssignableFrom(parameter.getParameterType())) {
                hasUpload = true;
            }
            if (isTypedParameter(parameter)) {
                hasTypedParameter = true;
            }
        }

        ApiResponses responses = operation.getResponses();
        applySuccessStatus(responses, handlerMethod);

        if (hasRequestBody || hasUpload || hasTypedParameter) {
            addResponse(responses, HttpStatus.BAD_REQUEST,
                    "Dados inválidos. O campo details traz uma linha por campo ou parâmetro recusado.",
                    example(HttpStatus.BAD_REQUEST, "Validation failed", "/api/users",
                            List.of("email: nao pode estar em branco")));
        }
        if (hasRequestBody || hasUpload) {
            addResponse(responses, HttpStatus.CONFLICT,
                    "Já existe um registro com os mesmos dados únicos, ou a mudança de estado não é permitida.",
                    example(HttpStatus.CONFLICT, "Ja existe um usuario com o email contato@venus.com", "/api/users",
                            List.of()));
        }
        if (hasPathVariable) {
            addResponse(responses, HttpStatus.NOT_FOUND,
                    "Nenhum registro encontrado para o identificador informado.",
                    example(HttpStatus.NOT_FOUND, "Usuario nao encontrado com id 42", "/api/users/42", List.of()));
        }
        if (hasUpload) {
            addResponse(responses, HttpStatus.PAYLOAD_TOO_LARGE,
                    "O arquivo enviado excede o tamanho máximo aceito pelo servidor.",
                    example(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo enviado excede o tamanho maximo aceito pelo servidor.",
                            "/api/users/42/avatar", List.of()));
        }
        addResponse(responses, HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro inesperado no servidor.",
                example(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                        "/api/users", List.of()));

        return operation;
    }

    private void applySuccessStatus(ApiResponses responses, HandlerMethod handlerMethod) {
        ApiResponse sucesso = responses.remove(OK);
        if (sucesso == null) {
            return;
        }
        if (returnsNoBody(handlerMethod)) {
            responses.addApiResponse(String.valueOf(HttpStatus.NO_CONTENT.value()),
                    new ApiResponse().description("Operação concluída. A resposta não tem corpo."));
            return;
        }
        if (handlerMethod.hasMethodAnnotation(PostMapping.class)) {
            responses.addApiResponse(String.valueOf(HttpStatus.CREATED.value()), sucesso
                    .description("Registro criado.")
                    .addHeaderObject("Location", new Header()
                            .description("URI do registro criado.")
                            .schema(new StringSchema().format("uri"))));
            return;
        }
        responses.addApiResponse(OK, sucesso);
    }

    private boolean returnsNoBody(HandlerMethod handlerMethod) {
        Class<?> corpo = ResolvableType.forMethodReturnType(handlerMethod.getMethod()).getGeneric(0).resolve();
        return Void.class.equals(corpo);
    }

    private boolean isTypedParameter(MethodParameter parameter) {
        boolean bound = parameter.hasParameterAnnotation(PathVariable.class)
                || parameter.hasParameterAnnotation(RequestParam.class);
        Class<?> type = parameter.getParameterType();
        return bound && !String.class.equals(type) && !MultipartFile.class.isAssignableFrom(type);
    }

    private Map<String, Object> example(HttpStatus status, String message, String path, List<String> details) {
        Map<String, Object> exemplo = new LinkedHashMap<>();
        exemplo.put("timestamp", EXEMPLO_TIMESTAMP);
        exemplo.put("status", status.value());
        exemplo.put("error", status.getReasonPhrase());
        exemplo.put("message", message);
        exemplo.put("path", path);
        exemplo.put("details", details);
        return exemplo;
    }

    private void addResponse(ApiResponses responses, HttpStatus status, String description, Map<String, Object> example) {
        String code = String.valueOf(status.value());
        if (responses.containsKey(code)) {
            return;
        }
        responses.addApiResponse(code, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(APPLICATION_JSON, new MediaType()
                        .schema(new Schema<>().$ref(ERROR_SCHEMA_REF))
                        .example(example))));
    }
}
