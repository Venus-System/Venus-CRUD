package com.venus.crud.dto.request.ingredient;

import com.venus.crud.entity.enums.RestrictionType;
import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record IngredientRegulationRequest(
        @NotNull Long ingredientId,
        @NotNull Long regulationId,
        @NotNull RestrictionType restrictionType,
        BigDecimal maxConcentrationValue,
        @NotBlank String unit,
        @NotBlank String notes,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
