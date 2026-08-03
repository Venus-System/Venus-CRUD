package com.venus.crud.dto.request.scan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RuleEvaluationRequest(
        @NotNull Long analysisResultId,
        @NotNull Long compatibilityRuleId,
        @NotNull Long ingredientId,
        @NotNull Long profileTagId,
        @NotNull Boolean wasMatched,
        @NotNull BigDecimal scoreDelta,
        @NotNull BigDecimal finalDelta,
        @NotBlank String explanation
) {
}
