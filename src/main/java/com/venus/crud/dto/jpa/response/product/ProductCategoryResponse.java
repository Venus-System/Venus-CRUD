package com.venus.crud.dto.jpa.response.product;

import java.time.OffsetDateTime;

public record ProductCategoryResponse(
        Long id,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
