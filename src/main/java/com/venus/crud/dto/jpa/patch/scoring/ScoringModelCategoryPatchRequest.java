package com.venus.crud.dto.jpa.patch.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record ScoringModelCategoryPatchRequest(
        @Schema(description = "Identificador do modelo de score.", example = "1")
        Long scoringModelId,
        @Schema(description = "Identificador da categoria de score.", example = "3")
        Long scoreCategoryId,
        @Schema(description = "Peso desta categoria no cálculo do score.", example = "0.30")
        BigDecimal weight
) {
}
