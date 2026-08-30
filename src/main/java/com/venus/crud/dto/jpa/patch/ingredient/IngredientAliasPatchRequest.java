package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.SourceType;

public record IngredientAliasPatchRequest(
        Long ingredientId,
        String aliasName,
        String aliasLanguage,
        SourceType sourceType,
        String sourceReference
) {
}
