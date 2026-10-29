package com.venus.crud.dto.jpa.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ProductCategoryRequest(
        @Schema(description = "Nome da categoria de produto.", example = "Shampoo")
        @NotBlank String name,
        @Schema(description = "Descrição da categoria de produto.", example = "Produtos para limpeza dos fios.")
        @NotBlank String description
) {
}
