package com.venus.crud.dto.jpa.patch.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.enums.VersionStatus;
import java.time.LocalDate;

public record ProductVersionPatchRequest(
        Long productId,
        String versionName,
        String displayName,
        VersionStatus status,
        Boolean isCurrent,
        String formulaSignature,
        SourceType detectedBy,
        LocalDate effectiveFrom,
        LocalDate effectiveTo
) {
}
