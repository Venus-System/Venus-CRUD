package com.venus.crud.dto.jpa.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record FavoriteResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador do produto.", example = "15")
        Long productId,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt
) {
}
