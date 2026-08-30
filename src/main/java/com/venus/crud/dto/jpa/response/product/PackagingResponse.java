package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.PackagingFormat;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.enums.SourceType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PackagingResponse(
        Long id,
        Long productVersionId,
        PackagingMaterial material,
        String materialDetail,
        PackagingFormat packagingFormat,
        Boolean isRecyclable,
        Boolean isRefillable,
        Boolean isBiodegradable,
        BigDecimal recycledContentPercentage,
        Short confidenceScore,
        SourceType sourceType,
        String sourceReference,
        Boolean wasManualVerified,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
