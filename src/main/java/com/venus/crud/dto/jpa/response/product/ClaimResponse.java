package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.ClaimType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ClaimResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome da alegação.", example = "Vegano")
        String name,
        @Schema(description = "Descrição da alegação.", example = "Produto sem ingredientes de origem animal.")
        String description,
        @Schema(description = "Tipo da alegação.")
        ClaimType claimType,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
