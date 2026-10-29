package com.venus.crud.dto.jpa.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record BrandResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome da marca.", example = "Natura")
        String name,
        @Schema(description = "País, em código ISO de duas letras.", example = "BR")
        String country,
        @Schema(description = "Site oficial.", example = "https://www.marca.com.br")
        String website,
        @Schema(description = "Indica se o produto declara não testar em animais.", example = "true")
        Boolean hasCrueltyFreeClaim,
        @Schema(description = "Indica se o produto se declara vegano.", example = "true")
        Boolean hasVeganClaim,
        @Schema(description = "Indica se a marca é brasileira.", example = "true")
        Boolean isBrazilian,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
