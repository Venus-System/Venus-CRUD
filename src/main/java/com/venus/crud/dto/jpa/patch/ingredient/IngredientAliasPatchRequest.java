package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;

public record IngredientAliasPatchRequest(
        @Schema(description = "Identificador do ingrediente.", example = "100")
        Long ingredientId,
        @Schema(description = "Nome alternativo pelo qual o ingrediente também é conhecido.", example = "Vitamina C")
        String aliasName,
        @Schema(description = "Idioma do nome alternativo, em código ISO.", example = "pt-BR")
        String aliasLanguage,
        @Schema(description = "Origem do dado.")
        SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
