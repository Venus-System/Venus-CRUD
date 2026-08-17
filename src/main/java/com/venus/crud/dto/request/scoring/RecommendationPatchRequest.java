package com.venus.crud.dto.request.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import jakarta.validation.constraints.PositiveOrZero;

public record RecommendationPatchRequest(
        Long userId,
        Long profileTagId,
        Long productVersionId,
        Long analysisResultId,
        RecommendationType recommendationType,
        Short confidenceScore,
        @PositiveOrZero Integer rankingPosition,
        String reason
) {
}
