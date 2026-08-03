package com.venus.crud.dto.response.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import java.time.OffsetDateTime;

public record RecommendationResponse(
        Long id,
        Long userId,
        Long profileTagId,
        Long productVersionId,
        Long analysisResultId,
        RecommendationType recommendationType,
        Short confidenceScore,
        Integer rankingPosition,
        String reason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
