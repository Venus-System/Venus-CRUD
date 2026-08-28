package com.venus.crud.dto.jpa.patch.product;

public record BrandPatchRequest(
        String name,
        String country,
        String website,
        Boolean hasCrueltyFreeClaim,
        Boolean hasVeganClaim,
        Boolean isBrazilian
) {
}
