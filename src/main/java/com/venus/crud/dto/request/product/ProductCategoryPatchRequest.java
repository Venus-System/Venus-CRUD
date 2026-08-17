package com.venus.crud.dto.request.product;

public record ProductCategoryPatchRequest(
        String name,
        String description
) {
}
