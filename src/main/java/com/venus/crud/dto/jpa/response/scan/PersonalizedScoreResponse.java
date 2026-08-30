package com.venus.crud.dto.jpa.response.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PersonalizedScoreResponse(
        Long id,
        Long userId,
        Long productVersionId,
        Long analysisResultId,
        Long scoringModelId,
        Integer finalScore,
        BigDecimal compatibilityPercentage,
        RiskLevel riskLevel,
        RecommendationLevel recommendationLevel,
        String summary,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
