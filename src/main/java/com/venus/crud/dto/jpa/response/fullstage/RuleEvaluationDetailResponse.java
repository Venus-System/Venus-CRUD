package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import java.math.BigDecimal;

public record RuleEvaluationDetailResponse(
        Long id,
        Long compatibilityRuleId,
        IngredientResponse ingredient,
        ProfileTagResponse profileTag,
        Boolean wasMatched,
        BigDecimal scoreDelta,
        BigDecimal finalDelta,
        String explanation
) {
}