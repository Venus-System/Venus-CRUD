package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.enums.VersionStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ProductVersionResponse(
        Long id,
        Long productId,
        String versionName,
        String displayName,
        VersionStatus status,
        Boolean isCurrent,
        String formulaSignature,
        SourceType detectedBy,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
