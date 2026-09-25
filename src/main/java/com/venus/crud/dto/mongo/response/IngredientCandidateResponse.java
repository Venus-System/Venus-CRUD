package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record IngredientCandidateResponse(
        @Schema(description = "Identificador do ingrediente possível.", example = "30")
        Long ingredientId,
        @Schema(description = "Nome INCI do ingrediente possível.", example = "PARFUM")
        String inciName
) {
}
