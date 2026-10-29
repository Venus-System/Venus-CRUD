package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;

public record IngredientPropertyPatchRequest(
        @Schema(description = "Identificador do ingrediente.", example = "100")
        Long ingredientId,
        @Schema(description = "Nome da propriedade.", example = "pH")
        String propertyName,
        @Schema(description = "Valor da propriedade.", example = "5.5")
        String propertyValue,
        @Schema(description = "Unidade de medida do valor.", example = "mL")
        String unit,
        @Schema(description = "Origem do dado.")
        SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
