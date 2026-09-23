package com.venus.crud.dto.jpa.request.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record IngredientCategoryRequest(
        @Schema(description = "Nome da categoria de ingrediente.", example = "Tensoativo")
        @NotBlank String name,
        @Schema(description = "Descrição da categoria de ingrediente.",
                example = "Ingredientes responsáveis pela limpeza e pela espuma.")
        @NotBlank String description
) {
}
