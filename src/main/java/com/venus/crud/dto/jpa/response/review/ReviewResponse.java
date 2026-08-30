package com.venus.crud.dto.jpa.response.review;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ReviewResponse(
        Long id,
        Long userId,
        Long productVersionId,
        BigDecimal rating,
        String title,
        String comment,
        Boolean verifiedUse,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
