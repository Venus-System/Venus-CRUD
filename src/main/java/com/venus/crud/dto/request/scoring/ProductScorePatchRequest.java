package com.venus.crud.dto.request.scoring;

import java.time.OffsetDateTime;

public record ProductScorePatchRequest(
        Long productVersionId,
        Long scoringModelId,
        Integer overallScore,
        Integer healthScore,
        Integer environmentalScore,
        Integer ethicalScore,
        Integer performanceScore,
        Integer transparencyScore,
        Short confidenceScore,
        OffsetDateTime calculatedAt
) {
}
