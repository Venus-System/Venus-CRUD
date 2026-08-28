package com.venus.crud.dto.jpa.request.ingredient;

import jakarta.validation.constraints.NotBlank;

public record IngredientCategoryRequest(
        @NotBlank String name,
        @NotBlank String description
) {
}
