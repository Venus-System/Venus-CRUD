package com.venus.crud.dto.jpa.patch.product;

public record ProductCategoryPatchRequest(
        String name,
        String description
) {
}
