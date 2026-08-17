package com.venus.crud.dto.request.ingredient;

import com.venus.crud.entity.enums.RestrictionType;
import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record IngredientRegulationPatchRequest(
        Long ingredientId,
        Long regulationId,
        RestrictionType restrictionType,
        @PositiveOrZero BigDecimal maxConcentrationValue,
        String unit,
        String notes,
        SourceType sourceType,
        String sourceReference
) {
}
