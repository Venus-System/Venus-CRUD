package com.venus.crud.dto.jpa.response.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record AnalysisResultResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Identificador do modelo de score.", example = "1")
        Long scoringModelId,
        @Schema(description = "Nota geral do produto, de 0 a 100.", example = "76")
        Integer overallScore,
        @Schema(description = "Nota de saúde, de 0 a 100.", example = "80")
        Integer healthScore,
        @Schema(description = "Nota ambiental, de 0 a 100.", example = "78")
        Integer environmentalScore,
        @Schema(description = "Nota ética, de 0 a 100.", example = "85")
        Integer ethicalScore,
        @Schema(description = "Nota de desempenho, de 0 a 100.", example = "70")
        Integer performanceScore,
        @Schema(description = "Nota de transparência, de 0 a 100.", example = "65")
        Integer transparencyScore,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável.", example = "90")
        Short confidenceScore,
        @Schema(description = "Tempo de processamento, em milissegundos.", example = "850")
        Integer processingTimeMs,
        @Schema(description = "Situação atual do registro.")
        AnalysisStatus status,
        @Schema(description = "Resumo do resultado da análise.", example = "Produto compatível com o seu perfil.")
        String summary,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
