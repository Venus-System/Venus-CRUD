package com.venus.crud.dto.response.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
import java.time.OffsetDateTime;

public record AnalysisResultResponse(
        Long id,
        Long userId,
        Long productVersionId,
        Long scoringModelId,
        Integer overallScore,
        Integer healthScore,
        Integer environmentalScore,
        Integer ethicalScore,
        Integer performanceScore,
        Integer transparencyScore,
        Short confidenceScore,
        Integer processingTimeMs,
        AnalysisStatus status,
        String summary,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
