package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductIngredientDetailResponse(
        @Schema(description = "Ingrediente referenciado.")
        IngredientResponse ingredient,
        @Schema(description = "Posição do ingrediente na lista do rótulo; 1 é o primeiro.", example = "1")
        Integer position
) {
}
