package com.venus.crud.dto.request.ingredient;

import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.EffectStrength;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientEffectRequest(
        @NotNull Long ingredientId,
        @NotNull Long profileTagId,
        @NotNull EffectCategory effectCategory,
        @NotBlank String effectName,
        @NotBlank String effectDescription,
        @NotNull EffectStrength effectStrength,
        @NotNull EvidenceLevel evidenceLevel,
        @NotNull ReviewStatus reviewStatus,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
