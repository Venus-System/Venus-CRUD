package com.venus.crud.dto.jpa.request.user;

import com.venus.crud.entity.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record UserRequest(
        @Schema(description = "Identificador do usuário no Firebase Authentication.", example = "K3jd9SmQ2xYbN7pLw0Rt")
        @NotBlank String firebaseUid,
        @Schema(description = "Nome do usuário.", example = "Ana Souza")
        @NotBlank String name,
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        @Email String email,
        @Schema(description = "Senha de acesso.", example = "SenhaForte123")
        String password,
        @Schema(description = "Situação atual do registro.")
        @NotNull UserStatus status,
        @Schema(description = "Data e hora do último acesso.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime lastLogin
) {
}
