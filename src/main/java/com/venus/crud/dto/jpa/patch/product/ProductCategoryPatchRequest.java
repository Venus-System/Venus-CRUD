package com.venus.crud.dto.jpa.patch.product;
import io.swagger.v3.oas.annotations.media.Schema;


public record ProductCategoryPatchRequest(
        @Schema(description = "Nome da categoria de produto.", example = "Shampoo")
        String name,
        @Schema(description = "Descrição da categoria de produto.", example = "Produtos para limpeza dos fios.")
        String description
) {
}
