package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanProductDecisionRequest(
        @Schema(description = "Produto que já existe no catálogo. O scan vira uma versão nova dele.", example = "57")
        Long productId,
        @Schema(description = "Nome do produto novo.", example = "Sérum Vitamina C")
        String name,
        @Schema(description = "Marca do produto novo, já cadastrada.", example = "12")
        Long brandId,
        @Schema(description = "Categoria do produto novo, já cadastrada.", example = "4")
        Long productCategoryId
) {
}
