package com.venus.crud.dto.jpa.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        @NotBlank @Email String email,
        @Schema(description = "Senha de acesso.", example = "SenhaForte123")
        @NotBlank String password
) {
}
