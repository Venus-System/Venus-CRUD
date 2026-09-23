package com.venus.crud.dto.jpa.patch.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductIngredientPatchRequest(
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Identificador do ingrediente.", example = "100")
        Long ingredientId,
        @Schema(description = "Posição do ingrediente na lista do rótulo; 1 é o primeiro.", example = "1")
        @PositiveOrZero Integer position
) {
}
