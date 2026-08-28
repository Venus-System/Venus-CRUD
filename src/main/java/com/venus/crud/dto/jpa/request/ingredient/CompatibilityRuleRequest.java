package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CompatibilityRuleRequest(
        @NotNull Long ingredientEffectId,
        @NotNull Long scoringModelId,
        @NotNull EffectType effectType,
        @NotNull Integer scoreDelta,
        @NotNull BigDecimal weight,
        @NotNull Integer priority,
        @NotNull Boolean hasConcentrationFactor,
        @NotNull Boolean isEnabled,
        @NotNull EvidenceLevel evidenceLevel,
        @NotBlank String reason,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
