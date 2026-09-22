package com.venus.crud.dto.jpa.request.scoring;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ScoringModelCategoryRequest(
        @NotNull Long scoringModelId,
        @NotNull Long scoreCategoryId,
        @NotNull BigDecimal weight
) {
}
