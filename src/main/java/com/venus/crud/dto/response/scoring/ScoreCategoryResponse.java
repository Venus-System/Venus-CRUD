package com.venus.crud.dto.response.scoring;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ScoreCategoryResponse(
        Long id,
        String name,
        String description,
        BigDecimal defaultWeight,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
