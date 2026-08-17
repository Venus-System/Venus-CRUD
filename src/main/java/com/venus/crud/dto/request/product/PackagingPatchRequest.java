package com.venus.crud.dto.request.product;

import com.venus.crud.entity.enums.PackagingFormat;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record PackagingPatchRequest(
        Long productVersionId,
        PackagingMaterial material,
        String materialDetail,
        PackagingFormat packagingFormat,
        Boolean isRecyclable,
        Boolean isRefillable,
        Boolean isBiodegradable,
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal recycledContentPercentage,
        @PositiveOrZero Short confidenceScore,
        SourceType sourceType,
        String sourceReference,
        Boolean wasManualVerified
) {
}
