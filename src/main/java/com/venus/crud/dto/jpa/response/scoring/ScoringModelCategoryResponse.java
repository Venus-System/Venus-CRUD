package com.venus.crud.dto.jpa.response.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ScoringModelCategoryResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do modelo de score.", example = "1")
        Long scoringModelId,
        @Schema(description = "Identificador da categoria de score.", example = "3")
        Long scoreCategoryId,
        @Schema(description = "Peso desta categoria no cálculo do score.", example = "0.30")
        BigDecimal weight,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
