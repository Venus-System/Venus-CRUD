package com.venus.crud.dto.jpa.response.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ProfileTagResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome da tag de perfil, como aparece na tela.", example = "Pele acneica")
        String name,
        @Schema(description = "Explicação da tag para o usuário.", example = "Pele com tendência a cravos e espinhas.")
        String description,
        @Schema(description = "Identificador textual estável, em minúsculas e com hífens.", example = "pele-acneica")
        String slug,
        @Schema(description = "Categoria a que o registro pertence.")
        ProfileTagCategory category,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
