package com.venus.crud.dto.request.product;

import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ProductClaimRequest(
        @NotNull Long productVersionId,
        @NotNull Long claimId,
        @NotNull Boolean wasVerified,
        String verifiedBy,
        OffsetDateTime verifiedAt,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
