package com.venus.crud.dto.jpa.request.scoring;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ProductScoreRequest(
        @NotNull Long productVersionId,
        @NotNull Long scoringModelId,
        @NotNull Integer overallScore,
        @NotNull Integer healthScore,
        @NotNull Integer environmentalScore,
        @NotNull Integer ethicalScore,
        @NotNull Integer performanceScore,
        @NotNull Integer transparencyScore,
        @NotNull Short confidenceScore,
        @NotNull OffsetDateTime calculatedAt
) {
}
