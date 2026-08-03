package com.venus.crud.dto.response.product;

import java.time.OffsetDateTime;

public record BrandResponse(
        Long id,
        String name,
        String country,
        String website,
        Boolean hasCrueltyFreeClaim,
        Boolean hasVeganClaim,
        Boolean isBrazilian,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
