package com.venus.crud.dto.response.ingredient;

import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record IngredientResponse(
        Long id,
        Long ingredientCategoryId,
        String inciName,
        String commonName,
        String functionSummary,
        String description,
        Short biodegradabilityLevel,
        Short irritationRiskLevel,
        Short comedogenicityScore,
        Short environmentalRiskLevel,
        String safetySummary,
        Short scientificConfidence,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
