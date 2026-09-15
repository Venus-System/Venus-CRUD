package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.SourceType;

public record AllergyIngredientPatchRequest(
        Long allergyId,
        Long ingredientId,
        SourceType sourceType,
        String sourceReference
) {
}
