package com.venus.crud.dto.jpa.response.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ScoreCategoryResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome da categoria de score.", example = "Segurança")
        String name,
        @Schema(description = "O que esta categoria avalia.", example = "Mede o risco dos ingredientes para a saúde.")
        String description,
        @Schema(description = "Peso padrão desta categoria no cálculo do score.", example = "0.25")
        BigDecimal defaultWeight,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
