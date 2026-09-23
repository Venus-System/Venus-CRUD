package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.product.ClaimResponse;
import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ProductClaimDetailResponse(
        @Schema(description = "Alegação de marketing referenciada.")
        ClaimResponse claim,
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
        String sourceReference
) {
}
