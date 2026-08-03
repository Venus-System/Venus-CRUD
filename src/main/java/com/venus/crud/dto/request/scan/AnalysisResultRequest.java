package com.venus.crud.dto.request.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnalysisResultRequest(
        @NotNull Long scanSessionId,
        @NotNull Long userId,
        @NotNull Long productVersionId,
        @NotNull Long scoringModelId,
        @NotNull Integer overallScore,
        @NotNull Integer healthScore,
        @NotNull Integer environmentalScore,
        @NotNull Integer ethicalScore,
        @NotNull Integer performanceScore,
        @NotNull Integer transparencyScore,
        @NotNull Short confidenceScore,
        @NotNull Integer processingTimeMs,
        @NotNull AnalysisStatus status,
        @NotBlank String summary
) {
}
