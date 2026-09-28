package com.venus.crud.dto.jpa.response.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ProductScoreResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Identificador do modelo de score.", example = "1")
        Long scoringModelId,
        @Schema(description = "Nota geral do produto, de 0 a 100.", example = "76")
        Integer overallScore,
        @Schema(description = "Nota de saúde, de 0 a 100. Nulo quando não há dado para calcular.", example = "80", nullable = true)
        Integer healthScore,
        @Schema(description = "Nota ambiental, de 0 a 100. Nulo quando não há dado para calcular.", example = "78", nullable = true)
        Integer environmentalScore,
        @Schema(description = "Nota ética, de 0 a 100.", example = "85")
        Integer ethicalScore,
        @Schema(description = "Nota de desempenho, de 0 a 100.", example = "70")
        Integer performanceScore,
        @Schema(description = "Nota de transparência, de 0 a 100. Nulo quando não há dado para calcular.", example = "65", nullable = true)
        Integer transparencyScore,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável. Nulo quando não há dado para calcular.", example = "90", nullable = true)
        Short confidenceScore,
        @Schema(description = "Data e hora em que o cálculo foi feito.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime calculatedAt,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
