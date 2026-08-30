package com.venus.crud.dto.jpa.request.product;

import jakarta.validation.constraints.NotBlank;

public record ProductCategoryRequest(
        @NotBlank String name,
        @NotBlank String description
) {
}
