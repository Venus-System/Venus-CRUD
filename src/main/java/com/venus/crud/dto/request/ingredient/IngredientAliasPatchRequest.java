package com.venus.crud.dto.request.ingredient;

import com.venus.crud.entity.enums.SourceType;

public record IngredientAliasPatchRequest(
        Long ingredientId,
        String aliasName,
        String aliasLanguage,
        SourceType sourceType,
        String sourceReference
) {
}
