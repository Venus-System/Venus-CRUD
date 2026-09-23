package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record RuleEvaluationDetailResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da regra de compatibilidade.", example = "154")
        Long compatibilityRuleId,
        @Schema(description = "Ingrediente referenciado.")
        IngredientResponse ingredient,
        @Schema(description = "Tag de perfil referenciada.")
        ProfileTagResponse profileTag,
        @Schema(description = "Indica se o ingrediente foi casado com o catálogo.", example = "true")
        Boolean wasMatched,
        @Schema(description = "Quanto a regra soma ou subtrai do score.", example = "-5.0")
        BigDecimal scoreDelta,
        @Schema(description = "Quanto esta regra somou ou subtraiu do score final.", example = "-5.0")
        BigDecimal finalDelta,
        @Schema(description = "Texto que explica ao usuário por que o resultado saiu assim.",
                example = "Contém álcool, que resseca pele seca.")
        String explanation
) {
}