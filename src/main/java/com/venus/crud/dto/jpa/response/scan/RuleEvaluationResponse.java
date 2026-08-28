package com.venus.crud.dto.jpa.response.scan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RuleEvaluationResponse(
        Long id,
        Long analysisResultId,
        Long compatibilityRuleId,
        Long ingredientId,
        Long profileTagId,
        Boolean wasMatched,
        BigDecimal scoreDelta,
        BigDecimal finalDelta,
        String explanation,
        OffsetDateTime createdAt
) {
}
