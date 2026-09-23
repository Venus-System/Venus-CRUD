package com.venus.crud.dto.jpa.patch.product;
import io.swagger.v3.oas.annotations.media.Schema;


public record ProductPatchRequest(
        @Schema(description = "Identificador da marca.", example = "3")
        Long brandId,
        @Schema(description = "Identificador da categoria do produto.", example = "2")
        Long productCategoryId,
        @Schema(description = "Nome do produto.", example = "Shampoo Hidratante 400ml")
        String name,
        @Schema(description = "Descrição do produto.", example = "Shampoo para cabelos secos, sem sulfato.")
        String description,
        @Schema(description = "Identificador textual estável, em minúsculas e com hífens.", example = "pele-acneica")
        String slug,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        Boolean isActive
) {
}
