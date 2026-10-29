package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AllergyIngredientRequest(
        @Schema(description = "Identificador da alergia.", example = "12")
        @NotNull Long allergyId,
        @Schema(description = "Identificador do ingrediente.", example = "100")
        @NotNull Long ingredientId,
        @Schema(description = "Origem do dado.")
        @NotNull SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
