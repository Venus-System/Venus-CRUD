package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.SourceType;
import java.math.BigDecimal;

public record CompatibilityRulePatchRequest(
        Long ingredientEffectId,
        Long scoringModelId,
        EffectType effectType,
        Integer scoreDelta,
        BigDecimal weight,
        Integer priority,
        Boolean hasConcentrationFactor,
        Boolean isEnabled,
        EvidenceLevel evidenceLevel,
        String reason,
        SourceType sourceType,
        String sourceReference
) {
}
