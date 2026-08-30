package com.venus.crud.dto.jpa.patch.ingredient;

import jakarta.validation.constraints.PositiveOrZero;

public record ProductIngredientPatchRequest(
        Long productVersionId,
        Long ingredientId,
        @PositiveOrZero Integer position
) {
}
