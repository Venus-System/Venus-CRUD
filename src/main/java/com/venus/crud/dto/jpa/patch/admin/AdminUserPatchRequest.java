package com.venus.crud.dto.jpa.patch.admin;

import com.venus.crud.entity.enums.AdminRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public record AdminUserPatchRequest(
        @Schema(description = "Nome do administrador.", example = "Ana Souza")
        String name,
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        @Email String email,
        @Schema(description = "Papel do administrador no sistema.")
        AdminRole role,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        Boolean isActive
) {
}
