package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientPropertyRequest(
        @Schema(description = "Identificador do ingrediente.", example = "100")
        @NotNull Long ingredientId,
        @Schema(description = "Nome da propriedade.", example = "pH")
        @NotBlank String propertyName,
        @Schema(description = "Valor da propriedade.", example = "5.5")
        @NotBlank String propertyValue,
        @Schema(description = "Unidade de medida do valor.", example = "mL")
        @NotBlank String unit,
        @Schema(description = "Origem do dado.")
        @NotNull SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
