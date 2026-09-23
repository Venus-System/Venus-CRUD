package com.venus.crud.dto.jpa.request.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecommendationRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        @NotNull Long profileTagId,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        @NotNull Long productVersionId,
        @Schema(description = "Identificador do resultado de análise.", example = "305")
        @NotNull Long analysisResultId,
        @Schema(description = "Tipo da recomendação.")
        @NotNull RecommendationType recommendationType,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável.", example = "90")
        @NotNull Short confidenceScore,
        @Schema(description = "Posição na lista de recomendações.", example = "1")
        @NotNull Integer rankingPosition,
        @Schema(description = "Motivo da recomendação.", example = "Combina com pele oleosa e não tem fragrância.")
        @NotBlank String reason
) {
}
