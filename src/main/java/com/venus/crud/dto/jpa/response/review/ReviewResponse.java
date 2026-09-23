package com.venus.crud.dto.jpa.response.review;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ReviewResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Nota dada pelo usuário.", example = "4.5")
        BigDecimal rating,
        @Schema(description = "Título da avaliação.", example = "Ótimo para pele seca")
        String title,
        @Schema(description = "Comentário do usuário sobre o produto.",
                example = "Ressecou minha pele depois de duas semanas.")
        String comment,
        @Schema(description = "Indica se o uso do ingrediente foi verificado.", example = "true")
        Boolean verifiedUse,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
