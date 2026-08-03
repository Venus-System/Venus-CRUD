package com.venus.crud.dto.response.ingredient;

import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record IngredientPropertyResponse(
        Long id,
        Long ingredientId,
        String propertyName,
        String propertyValue,
        String unit,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
