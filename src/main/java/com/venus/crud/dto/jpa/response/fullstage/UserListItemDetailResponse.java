package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.product.ProductResponse;

public record UserListItemDetailResponse(
        ProductResponse product,
        Integer positionOrder
) {
}
