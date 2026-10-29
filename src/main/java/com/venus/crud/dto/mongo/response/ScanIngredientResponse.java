package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanIngredientResponse(
        @Schema(description = "Posição do ingrediente na lista do rótulo, começando em 1.", example = "12")
        Integer position,
        @Schema(description = "Nome do ingrediente exatamente como o OCR leu.", example = "Coumarln")
        String rawName,
        @Schema(description = "Nome normalizado usado na busca no catálogo.", example = "COUMARLN")
        String normalizedName,
        @Schema(description = "Resultado da busca no catálogo.")
        IngredientMatchResponse match
) {
}
