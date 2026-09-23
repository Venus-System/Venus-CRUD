package com.venus.crud.dto.jpa.request.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PersonalizedScoreRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        @NotNull Long productVersionId,
        @Schema(description = "Identificador do resultado de análise.", example = "305")
        @NotNull Long analysisResultId,
        @Schema(description = "Identificador do modelo de score.", example = "1")
        @NotNull Long scoringModelId,
        @Schema(description = "Nota final depois de aplicadas todas as regras, de 0 a 100.", example = "72")
        @NotNull Integer finalScore,
        @Schema(description = "Percentual de compatibilidade entre o produto e o perfil do usuário.", example = "82.5")
        @NotNull BigDecimal compatibilityPercentage,
        @Schema(description = "Nível de risco.")
        @NotNull RiskLevel riskLevel,
        @Schema(description = "Grau da recomendação.")
        @NotNull RecommendationLevel recommendationLevel,
        @Schema(description = "Resumo do que puxou a nota para cima ou para baixo.",
                example = "Boa hidratação, mas contém fragrância.")
        @NotBlank String summary
) {
}
