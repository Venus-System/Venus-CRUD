package com.venus.crud.dto.jpa.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BrandRequest(
        @NotBlank String name,
        String country,
        String website,
        @NotNull Boolean hasCrueltyFreeClaim,
        @NotNull Boolean hasVeganClaim,
        @NotNull Boolean isBrazilian
) {
}
