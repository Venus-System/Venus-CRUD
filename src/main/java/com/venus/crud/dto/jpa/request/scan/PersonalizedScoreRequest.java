package com.venus.crud.dto.jpa.request.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PersonalizedScoreRequest(
        @NotNull Long userId,
        @NotNull Long productVersionId,
        @NotNull Long analysisResultId,
        @NotNull Long scoringModelId,
        @NotNull Integer finalScore,
        @NotNull BigDecimal compatibilityPercentage,
        @NotNull RiskLevel riskLevel,
        @NotNull RecommendationLevel recommendationLevel,
        @NotBlank String summary
) {
}
