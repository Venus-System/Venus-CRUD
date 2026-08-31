package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.ingredient.IngredientAliasResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientCategoryResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientPropertyResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import java.util.List;

public record IngredientFullResponse(
        IngredientResponse ingredient,
        IngredientCategoryResponse category,
        List<IngredientAliasResponse> aliases,
        List<IngredientPropertyResponse> properties,
        List<IngredientEffectDetailResponse> effects
) {
}