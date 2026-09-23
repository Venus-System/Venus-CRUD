package com.venus.crud.dto.jpa.patch.ingredient;
import io.swagger.v3.oas.annotations.media.Schema;


public record IngredientCategoryPatchRequest(
        @Schema(description = "Nome da categoria de ingrediente.", example = "Tensoativo")
        String name,
        @Schema(description = "Descrição da categoria de ingrediente.",
                example = "Ingredientes responsáveis pela limpeza e pela espuma.")
        String description
) {
}
