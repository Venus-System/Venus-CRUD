package com.venus.crud.dto.jpa.response.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PersonalizedScoreResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
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
        BigDecimal compatibilityPercentage,
        @Schema(description = "Nível de risco.")
        RiskLevel riskLevel,
        @Schema(description = "Grau da recomendação.")
        RecommendationLevel recommendationLevel,
        @Schema(description = "Resumo do que puxou a nota para cima ou para baixo.",
                example = "Boa hidratação, mas contém fragrância.")
        String summary,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
