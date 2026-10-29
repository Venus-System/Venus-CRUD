package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.ingredient.IngredientAliasResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientCategoryResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientPropertyResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record IngredientFullResponse(
        @Schema(description = "Ingrediente referenciado.")
        IngredientResponse ingredient,
        @Schema(description = "Categoria a que o registro pertence.")
        IngredientCategoryResponse category,
        @Schema(description = "Nomes alternativos do ingrediente.")
        List<IngredientAliasResponse> aliases,
        @Schema(description = "Propriedades do ingrediente.")
        List<IngredientPropertyResponse> properties,
        @Schema(description = "Efeitos atribuídos ao ingrediente.")
        List<IngredientEffectDetailResponse> effects
) {
}