package com.venus.crud.dto.jpa.patch.ingredient;

public record IngredientCategoryPatchRequest(
        String name,
        String description
) {
}
