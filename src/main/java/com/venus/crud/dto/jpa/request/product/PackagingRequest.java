package com.venus.crud.dto.jpa.request.product;

import com.venus.crud.entity.enums.PackagingFormat;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record PackagingRequest(
        @NotNull Long productVersionId,
        @NotNull PackagingMaterial material,
        @NotBlank String materialDetail,
        @NotNull PackagingFormat packagingFormat,
        @NotNull Boolean isRecyclable,
        @NotNull Boolean isRefillable,
        @NotNull Boolean isBiodegradable,
        @PositiveOrZero BigDecimal recycledContentPercentage,
        @NotNull @PositiveOrZero Short confidenceScore,
        @NotNull SourceType sourceType,
        String sourceReference,
        @NotNull Boolean wasManualVerified
) {
}
