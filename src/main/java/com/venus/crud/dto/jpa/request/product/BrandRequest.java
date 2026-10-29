package com.venus.crud.dto.jpa.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BrandRequest(
        @Schema(description = "Nome da marca.", example = "Natura")
        @NotBlank String name,
        @Schema(description = "País, em código ISO de duas letras.", example = "BR")
        String country,
        @Schema(description = "Site oficial.", example = "https://www.marca.com.br")
        String website,
        @Schema(description = "Indica se o produto declara não testar em animais.", example = "true")
        @NotNull Boolean hasCrueltyFreeClaim,
        @Schema(description = "Indica se o produto se declara vegano.", example = "true")
        @NotNull Boolean hasVeganClaim,
        @Schema(description = "Indica se a marca é brasileira.", example = "true")
        @NotNull Boolean isBrazilian
) {
}
