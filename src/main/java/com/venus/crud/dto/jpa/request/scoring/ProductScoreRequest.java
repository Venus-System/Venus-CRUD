package com.venus.crud.dto.jpa.request.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ProductScoreRequest(
        @Schema(description = "Identificador da versão do produto.", example = "31")
        @NotNull Long productVersionId,
        @Schema(description = "Identificador do modelo de score.", example = "1")
        @NotNull Long scoringModelId,
        @Schema(description = "Nota geral do produto, de 0 a 100.", example = "76")
        @NotNull Integer overallScore,
        @Schema(description = "Nota de saúde, de 0 a 100.", example = "80")
        @NotNull Integer healthScore,
        @Schema(description = "Nota ambiental, de 0 a 100.", example = "78")
        @NotNull Integer environmentalScore,
        @Schema(description = "Nota ética, de 0 a 100.", example = "85")
        @NotNull Integer ethicalScore,
        @Schema(description = "Nota de desempenho, de 0 a 100.", example = "70")
        @NotNull Integer performanceScore,
        @Schema(description = "Nota de transparência, de 0 a 100.", example = "65")
        @NotNull Integer transparencyScore,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável.", example = "90")
        @NotNull Short confidenceScore,
        @Schema(description = "Data e hora em que o cálculo foi feito.", example = "2026-09-22T14:30:00-03:00")
        @NotNull OffsetDateTime calculatedAt
) {
}
