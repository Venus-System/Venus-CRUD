package com.venus.crud.dto.jpa.patch.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record PersonalizedScorePatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Identificador do resultado de análise.", example = "305")
        Long analysisResultId,
        @Schema(description = "Identificador do modelo de score.", example = "1")
        Long scoringModelId,
        @Schema(description = "Nota final depois de aplicadas todas as regras, de 0 a 100.", example = "72")
        Integer finalScore,
        @Schema(description = "Percentual de compatibilidade entre o produto e o perfil do usuário.", example = "82.5")
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal compatibilityPercentage,
        @Schema(description = "Nível de risco.")
        RiskLevel riskLevel,
        @Schema(description = "Grau da recomendação.")
        RecommendationLevel recommendationLevel,
        @Schema(description = "Resumo do que puxou a nota para cima ou para baixo.",
                example = "Boa hidratação, mas contém fragrância.")
        String summary
) {
}
