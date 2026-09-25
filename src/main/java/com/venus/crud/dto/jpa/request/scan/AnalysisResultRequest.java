package com.venus.crud.dto.jpa.request.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnalysisResultRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
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
        Integer transparencyScore,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável.", example = "90")
        Short confidenceScore,
        @Schema(description = "Tempo de processamento, em milissegundos.", example = "850")
        @NotNull Integer processingTimeMs,
        @Schema(description = "Situação atual do registro.")
        @NotNull AnalysisStatus status,
        @Schema(description = "Resumo do resultado da análise.", example = "Produto compatível com o seu perfil.")
        @NotBlank String summary
) {
}
