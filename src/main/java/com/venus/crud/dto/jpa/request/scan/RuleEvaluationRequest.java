package com.venus.crud.dto.jpa.request.scan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RuleEvaluationRequest(
        @Schema(description = "Identificador do resultado de análise.", example = "305")
        @NotNull Long analysisResultId,
        @Schema(description = "Identificador da regra de compatibilidade.", example = "154")
        @NotNull Long compatibilityRuleId,
        @Schema(description = "Identificador do ingrediente.", example = "100")
        @NotNull Long ingredientId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        @NotNull Long profileTagId,
        @Schema(description = "Indica se o ingrediente foi casado com o catálogo.", example = "true")
        @NotNull Boolean wasMatched,
        @Schema(description = "Quanto a regra soma ou subtrai do score.", example = "-5.0")
        @NotNull BigDecimal scoreDelta,
        @Schema(description = "Quanto esta regra somou ou subtraiu do score final.", example = "-5.0")
        @NotNull BigDecimal finalDelta,
        @Schema(description = "Texto que explica ao usuário por que o resultado saiu assim.",
                example = "Contém álcool, que resseca pele seca.")
        @NotBlank String explanation
) {
}
