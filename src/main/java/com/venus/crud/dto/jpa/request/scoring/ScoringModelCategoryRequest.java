package com.venus.crud.dto.jpa.request.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ScoringModelCategoryRequest(
        @Schema(description = "Identificador do modelo de score.", example = "1")
        @NotNull Long scoringModelId,
        @Schema(description = "Identificador da categoria de score.", example = "3")
        @NotNull Long scoreCategoryId,
        @Schema(description = "Peso desta categoria no cálculo do score.", example = "0.30")
        @NotNull BigDecimal weight
) {
}
