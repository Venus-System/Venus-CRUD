package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import java.time.OffsetDateTime;

public record UserPatchRequest(
        @Schema(description = "Nome do usuário.", example = "Ana Souza")
        String name,
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        @Email String email,
        @Schema(description = "Senha de acesso.", example = "SenhaForte123")
        String password,
        @Schema(description = "Situação atual do registro.")
        UserStatus status,
        @Schema(description = "Data e hora do último acesso.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime lastLogin
) {
}
