package com.venus.crud.dto.mongo.request;

import com.venus.crud.entity.enums.IngredientDecisionAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ScanIngredientDecisionRequest(
        @Schema(description = "Posição do ingrediente no rótulo, a mesma do scan.", example = "12")
        @NotNull Integer position,
        @Schema(description = "LINK liga a um ingrediente do catálogo, CREATE cria um novo, DISCARD tira da lista.")
        @NotNull IngredientDecisionAction action,
        @Schema(description = "Ingrediente do catálogo, para LINK.", example = "812")
        Long ingredientId,
        @Schema(description = "Nome INCI do ingrediente novo, para CREATE.", example = "Coumarin")
        String inciName
) {
}
