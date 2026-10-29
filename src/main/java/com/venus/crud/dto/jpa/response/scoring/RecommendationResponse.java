package com.venus.crud.dto.jpa.response.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record RecommendationResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        Long profileTagId,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Identificador do resultado de análise.", example = "305")
        Long analysisResultId,
        @Schema(description = "Tipo da recomendação.")
        RecommendationType recommendationType,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável.", example = "90")
        Short confidenceScore,
        @Schema(description = "Posição na lista de recomendações.", example = "1")
        Integer rankingPosition,
        @Schema(description = "Motivo da recomendação.", example = "Combina com pele oleosa e não tem fragrância.")
        String reason,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
