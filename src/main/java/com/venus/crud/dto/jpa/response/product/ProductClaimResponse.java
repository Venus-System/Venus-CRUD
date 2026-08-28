package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record ProductClaimResponse(
        Long id,
        Long productVersionId,
        Long claimId,
        Boolean wasVerified,
        String verifiedBy,
        OffsetDateTime verifiedAt,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
