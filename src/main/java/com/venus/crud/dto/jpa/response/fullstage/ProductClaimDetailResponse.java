package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.product.ClaimResponse;
import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record ProductClaimDetailResponse(
        ClaimResponse claim,
        Boolean wasVerified,
        String verifiedBy,
        OffsetDateTime verifiedAt,
        SourceType sourceType,
        String sourceReference
) {
}
