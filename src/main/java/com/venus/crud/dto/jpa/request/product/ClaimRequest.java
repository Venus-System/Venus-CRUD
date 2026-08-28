package com.venus.crud.dto.jpa.request.product;

import com.venus.crud.entity.enums.ClaimType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClaimRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull ClaimType claimType
) {
}
