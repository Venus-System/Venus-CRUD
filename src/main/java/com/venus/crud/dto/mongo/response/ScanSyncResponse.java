package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ScanSyncResponse(
        @Schema(description = "Produto criado ou reaproveitado no catálogo.", example = "57")
        Long productId,
        @Schema(description = "Versão do produto criada ou reaproveitada.", example = "130")
        Long productVersionId,
        @Schema(description = "Data e hora em que a sincronização deu certo.", example = "2026-09-27T10:15:02-03:00")
        OffsetDateTime syncedAt,
        @Schema(description = "Quantas vezes a sincronização foi tentada.", example = "1")
        Integer attempts,
        @Schema(description = "Motivo da última falha. Fica vazio quando a sincronização dá certo.",
                example = "Marca nao encontrada com id 12")
        String lastError,
        @Schema(description = "Data e hora da última falha.", example = "2026-09-27T10:15:02-03:00")
        OffsetDateTime failedAt
) {
}
