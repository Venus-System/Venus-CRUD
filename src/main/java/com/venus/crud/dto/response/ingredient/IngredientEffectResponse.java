package com.venus.crud.dto.response.ingredient;

import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.EffectStrength;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record IngredientEffectResponse(
        Long id,
        Long ingredientId,
        Long profileTagId,
        EffectCategory effectCategory,
        String effectName,
        String effectDescription,
        EffectStrength effectStrength,
        EvidenceLevel evidenceLevel,
        ReviewStatus reviewStatus,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
