package com.venus.crud.dto.response.scoring;

import java.time.OffsetDateTime;

public record ProductScoreResponse(
        Long id,
        Long productVersionId,
        Long scoringModelId,
        Integer overallScore,
        Integer healthScore,
        Integer environmentalScore,
        Integer ethicalScore,
        Integer performanceScore,
        Integer transparencyScore,
        Short confidenceScore,
        OffsetDateTime calculatedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
