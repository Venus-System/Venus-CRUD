package com.venus.crud.dto.response.ingredient;

import com.venus.crud.entity.enums.RestrictionType;
import com.venus.crud.entity.enums.SourceType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record IngredientRegulationResponse(
        Long id,
        Long ingredientId,
        Long regulationId,
        RestrictionType restrictionType,
        BigDecimal maxConcentrationValue,
        String unit,
        String notes,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
