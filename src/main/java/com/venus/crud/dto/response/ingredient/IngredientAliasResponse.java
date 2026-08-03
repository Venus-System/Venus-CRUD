package com.venus.crud.dto.response.ingredient;

import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record IngredientAliasResponse(
        Long id,
        Long ingredientId,
        String aliasName,
        String aliasLanguage,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
