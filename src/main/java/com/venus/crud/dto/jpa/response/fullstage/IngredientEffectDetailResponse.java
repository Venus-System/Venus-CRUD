package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.EffectStrength;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;

public record IngredientEffectDetailResponse(
        Long id,
        ProfileTagResponse profileTag,
        EffectCategory effectCategory,
        String effectName,
        String effectDescription,
        EffectStrength effectStrength,
        EvidenceLevel evidenceLevel,
        ReviewStatus reviewStatus,
        SourceType sourceType,
        String sourceReference
) {
}