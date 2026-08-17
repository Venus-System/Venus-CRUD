package com.venus.crud.dto.request.product;

import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record ProductClaimPatchRequest(
        Long productVersionId,
        Long claimId,
        Boolean wasVerified,
        String verifiedBy,
        OffsetDateTime verifiedAt,
        SourceType sourceType,
        String sourceReference
) {
}
