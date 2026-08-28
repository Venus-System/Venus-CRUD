package com.venus.crud.dto.jpa.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotNull Long brandId,
        @NotNull Long productCategoryId,
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String slug,
        @NotNull Boolean isActive
) {
}
