package com.venus.crud.dto.jpa.patch.scoring;

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
