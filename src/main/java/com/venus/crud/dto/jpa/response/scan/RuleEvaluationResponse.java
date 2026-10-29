package com.venus.crud.dto.jpa.response.scan;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RuleEvaluationResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do resultado de análise.", example = "305")
        Long analysisResultId,
        @Schema(description = "Identificador da regra de compatibilidade.", example = "154")
        Long compatibilityRuleId,
        @Schema(description = "Identificador do ingrediente.", example = "100")
        Long ingredientId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        Long profileTagId,
        @Schema(description = "Indica se o ingrediente foi casado com o catálogo.", example = "true")
        Boolean wasMatched,
        @Schema(description = "Quanto a regra soma ou subtrai do score.", example = "-5.0")
        BigDecimal scoreDelta,
        @Schema(description = "Quanto esta regra somou ou subtraiu do score final.", example = "-5.0")
        BigDecimal finalDelta,
        @Schema(description = "Texto que explica ao usuário por que o resultado saiu assim.",
                example = "Contém álcool, que resseca pele seca.")
        String explanation,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt
) {
}
