package com.venus.crud.dto.jpa.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AdminUserPasswordChangeRequest(
        @Schema(description = "Senha atual do administrador, exigida para confirmar a troca.",
                example = "SenhaAtual123")
        @NotBlank String currentPassword,
        @Schema(description = "Nova senha do administrador.", example = "NovaSenha123")
        @NotBlank String newPassword
) {
}
