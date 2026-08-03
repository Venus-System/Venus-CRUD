package com.venus.crud.dto.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientAliasRequest(
        @NotNull Long ingredientId,
        @NotBlank String aliasName,
        @NotBlank String aliasLanguage,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
