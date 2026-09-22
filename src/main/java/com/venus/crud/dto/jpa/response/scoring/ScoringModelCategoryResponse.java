package com.venus.crud.dto.jpa.response.scoring;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ScoringModelCategoryResponse(
        Long id,
        Long scoringModelId,
        Long scoreCategoryId,
        BigDecimal weight,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
