package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientRequest(
        @NotNull Long ingredientCategoryId,
        @NotBlank String inciName,
        @NotBlank String commonName,
        @NotBlank String functionSummary,
        @NotBlank String description,
        @NotNull Short biodegradabilityLevel,
        @NotNull Short irritationRiskLevel,
        @NotNull Short comedogenicityScore,
        @NotNull Short environmentalRiskLevel,
        @NotBlank String safetySummary,
        @NotNull Short scientificConfidence,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
