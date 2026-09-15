package com.venus.crud.dto.jpa.response.ingredient;

import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record AllergyIngredientResponse(
        Long id,
        Long allergyId,
        Long ingredientId,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
