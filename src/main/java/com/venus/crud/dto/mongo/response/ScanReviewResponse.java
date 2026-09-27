package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;

public record ScanReviewResponse(
        @Schema(description = "Administrador que aprovou ou recusou o scan.", example = "3")
        Long decidedByAdminId,
        @Schema(description = "Data e hora da decisão.", example = "2026-09-27T10:15:00-03:00")
        OffsetDateTime decidedAt,
        @Schema(description = "Motivo informado pelo administrador. Obrigatório na recusa.", example = "Foto do verso ilegível")
        String reason,
        @Schema(description = "Decisões do administrador por ingrediente, do jeito que foram enviadas.")
        List<ScanIngredientDecisionResponse> ingredientDecisions
) {
}
