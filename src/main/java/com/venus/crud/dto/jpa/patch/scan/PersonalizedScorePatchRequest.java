package com.venus.crud.dto.jpa.patch.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record PersonalizedScorePatchRequest(
        Long userId,
        Long productVersionId,
        Long analysisResultId,
        Long scoringModelId,
        Integer finalScore,
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal compatibilityPercentage,
        RiskLevel riskLevel,
        RecommendationLevel recommendationLevel,
        String summary
) {
}
