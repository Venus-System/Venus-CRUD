package com.venus.crud.dto.jpa.response.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ScoringModelResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome do modelo de score.", example = "Modelo padrão Venus")
        String name,
        @Schema(description = "Versão do modelo.", example = "1.0.0")
        String version,
        @Schema(description = "Descrição do modelo de score.",
                example = "Modelo usado na análise padrão do aplicativo.")
        String description,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        Boolean isActive,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
