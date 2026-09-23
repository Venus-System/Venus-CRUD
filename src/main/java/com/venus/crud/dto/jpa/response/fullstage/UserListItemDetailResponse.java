package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.product.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserListItemDetailResponse(
        @Schema(description = "Produto referenciado.")
        ProductResponse product,
        @Schema(description = "Ordem de exibição.", example = "1")
        Integer positionOrder
) {
}
