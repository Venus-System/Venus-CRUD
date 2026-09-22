package com.venus.crud.dto.jpa.patch.scoring;

import java.math.BigDecimal;

public record ScoringModelCategoryPatchRequest(
        Long scoringModelId,
        Long scoreCategoryId,
        BigDecimal weight
) {
}
