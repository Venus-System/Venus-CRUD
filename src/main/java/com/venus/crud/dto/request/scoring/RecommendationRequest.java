package com.venus.crud.dto.request.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecommendationRequest(
        @NotNull Long userId,
        @NotNull Long profileTagId,
        @NotNull Long productVersionId,
        @NotNull Long analysisResultId,
        @NotNull RecommendationType recommendationType,
        @NotNull Short confidenceScore,
        @NotNull Integer rankingPosition,
        @NotBlank String reason
) {
}
