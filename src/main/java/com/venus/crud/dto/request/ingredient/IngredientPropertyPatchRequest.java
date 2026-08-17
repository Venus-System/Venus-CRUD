package com.venus.crud.dto.request.ingredient;

import com.venus.crud.entity.enums.SourceType;

public record IngredientPropertyPatchRequest(
        Long ingredientId,
        String propertyName,
        String propertyValue,
        String unit,
        SourceType sourceType,
        String sourceReference
) {
}
