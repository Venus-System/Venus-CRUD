package com.venus.crud.dto.jpa.request.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProductIngredientRequest(
        @Schema(description = "Identificador da versão do produto.", example = "31")
        @NotNull Long productVersionId,
        @Schema(description = "Identificador do ingrediente.", example = "100")
        @NotNull Long ingredientId,
        @Schema(description = "Posição do ingrediente na lista do rótulo; 1 é o primeiro.", example = "1")
        @NotNull Integer position
) {
}
