package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientPropertyRequest(
        @NotNull Long ingredientId,
        @NotBlank String propertyName,
        @NotBlank String propertyValue,
        @NotBlank String unit,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
