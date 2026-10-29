package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ProductClaimResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Identificador da alegação.", example = "8")
        Long claimId,
        @Schema(description = "Indica se o dado foi verificado.", example = "true")
        Boolean wasVerified,
        @Schema(description = "Quem verificou o dado.", example = "equipe-venus")
        String verifiedBy,
        @Schema(description = "Data e hora da verificação.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime verifiedAt,
        @Schema(description = "Origem do dado.")
        SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
