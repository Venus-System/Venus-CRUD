package com.venus.crud.dto.jpa.patch.product;
import io.swagger.v3.oas.annotations.media.Schema;


public record BrandPatchRequest(
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
        Boolean isBrazilian
) {
}
