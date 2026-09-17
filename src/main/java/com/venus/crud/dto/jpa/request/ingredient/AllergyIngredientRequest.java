package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotNull;

public record AllergyIngredientRequest(
        @NotNull Long allergyId,
        @NotNull Long ingredientId,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
