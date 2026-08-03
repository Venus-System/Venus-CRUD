package com.venus.crud.dto.response.product;

import java.time.OffsetDateTime;

public record ProductResponse(
        Long id,
        Long brandId,
        Long productCategoryId,
        String name,
        String description,
        String slug,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
