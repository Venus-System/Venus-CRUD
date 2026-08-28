package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.SourceType;

public record IngredientPatchRequest(
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
        String sourceReference
) {
}
