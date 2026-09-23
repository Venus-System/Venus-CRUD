package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientAliasRequest(
        @Schema(description = "Identificador do ingrediente.", example = "100")
        @NotNull Long ingredientId,
        @Schema(description = "Nome alternativo pelo qual o ingrediente também é conhecido.", example = "Vitamina C")
        @NotBlank String aliasName,
        @Schema(description = "Idioma do nome alternativo, em código ISO.", example = "pt-BR")
        @NotBlank String aliasLanguage,
        @Schema(description = "Origem do dado.")
        @NotNull SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
