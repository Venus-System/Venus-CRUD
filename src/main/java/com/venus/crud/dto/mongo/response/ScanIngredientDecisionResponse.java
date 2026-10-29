package com.venus.crud.dto.mongo.response;

import com.venus.crud.entity.enums.IngredientDecisionAction;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScanIngredientDecisionResponse(
        @Schema(description = "Posição do ingrediente no rótulo.", example = "12")
        Integer position,
        @Schema(description = "LINK liga a um ingrediente do catálogo, CREATE cria um novo, DISCARD tira da lista.")
        IngredientDecisionAction action,
        @Schema(description = "Ingrediente do catálogo ligado a esta posição.", example = "812")
        Long ingredientId,
        @Schema(description = "Nome INCI do ingrediente criado.", example = "Coumarin")
        String inciName
) {
}
