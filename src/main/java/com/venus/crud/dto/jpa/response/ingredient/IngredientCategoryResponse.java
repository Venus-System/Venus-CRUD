package com.venus.crud.dto.jpa.response.ingredient;

import java.time.OffsetDateTime;

public record IngredientCategoryResponse(
        Long id,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
