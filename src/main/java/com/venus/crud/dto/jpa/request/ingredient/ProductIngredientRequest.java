package com.venus.crud.dto.jpa.request.ingredient;

import jakarta.validation.constraints.NotNull;

public record ProductIngredientRequest(
        @NotNull Long productVersionId,
        @NotNull Long ingredientId,
        @NotNull Integer position
) {
}
