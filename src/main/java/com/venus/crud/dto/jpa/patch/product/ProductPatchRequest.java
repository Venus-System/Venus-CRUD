package com.venus.crud.dto.jpa.patch.product;

public record ProductPatchRequest(
        Long brandId,
        Long productCategoryId,
        String name,
        String description,
        String slug,
        Boolean isActive
) {
}
