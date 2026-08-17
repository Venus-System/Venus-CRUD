package com.venus.crud.dto.request.scan;

import com.venus.crud.entity.enums.AnalysisStatus;

public record AnalysisResultPatchRequest(
        Long scanSessionId,
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
        String summary
) {
}
