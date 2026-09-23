package com.venus.crud.dto.jpa.response.admin;

import com.venus.crud.entity.enums.AdminRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record AdminUserResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome do administrador.", example = "Ana Souza")
        String name,
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        String email,
        @Schema(description = "Papel do administrador no sistema.")
        AdminRole role,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        Boolean isActive,
        @Schema(description = "Indica se o administrador já tem senha local cadastrada.", example = "true")
        Boolean hasPassword,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
