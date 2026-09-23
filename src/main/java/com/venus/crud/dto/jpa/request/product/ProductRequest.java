package com.venus.crud.dto.jpa.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @Schema(description = "Identificador da marca.", example = "3")
        @NotNull Long brandId,
        @Schema(description = "Identificador da categoria do produto.", example = "2")
        @NotNull Long productCategoryId,
        @Schema(description = "Nome do produto.", example = "Shampoo Hidratante 400ml")
        @NotBlank String name,
        @Schema(description = "Descrição do produto.", example = "Shampoo para cabelos secos, sem sulfato.")
        @NotBlank String description,
        @Schema(description = "Identificador textual estável, em minúsculas e com hífens.", example = "pele-acneica")
        @NotBlank String slug,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        @NotNull Boolean isActive
) {
}
