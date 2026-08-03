package com.venus.crud.dto.response.ingredient;

import java.time.OffsetDateTime;

public record ProductIngredientResponse(
        Long id,
        Long productVersionId,
        Long ingredientId,
        Integer position,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
