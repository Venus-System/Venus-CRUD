package com.venus.crud.dto.jpa.request.admin;

import com.venus.crud.entity.enums.AdminRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminUserRequest(
        @Schema(description = "Nome do administrador.", example = "Ana Souza")
        @NotBlank String name,
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        @NotBlank @Email String email,
        @Schema(description = "Papel do administrador no sistema.")
        @NotNull AdminRole role,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        @NotNull Boolean isActive,
        @Schema(description = "Senha de acesso.", example = "SenhaForte123")
        @NotBlank String password
) {
}