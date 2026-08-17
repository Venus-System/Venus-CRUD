package com.venus.crud.dto.request.scan;

import java.math.BigDecimal;

public record RuleEvaluationPatchRequest(
        Long analysisResultId,
        Long compatibilityRuleId,
        Long ingredientId,
        Long profileTagId,
        Boolean wasMatched,
        BigDecimal scoreDelta,
        BigDecimal finalDelta,
        String explanation
) {
}
