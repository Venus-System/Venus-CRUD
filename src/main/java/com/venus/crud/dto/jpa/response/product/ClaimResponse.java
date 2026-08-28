package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.ClaimType;
import java.time.OffsetDateTime;

public record ClaimResponse(
        Long id,
        String name,
        String description,
        ClaimType claimType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
