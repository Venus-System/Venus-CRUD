package com.venus.crud.dto.request.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.enums.VersionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProductVersionRequest(
        @NotNull Long productId,
        @NotBlank String versionName,
        @NotBlank String displayName,
        @NotNull VersionStatus status,
        @NotNull Boolean isCurrent,
        @NotBlank String formulaSignature,
        @NotNull SourceType detectedBy,
        @NotNull LocalDate effectiveFrom,
        LocalDate effectiveTo
) {
}
