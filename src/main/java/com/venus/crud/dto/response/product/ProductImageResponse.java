package com.venus.crud.dto.response.product;

import com.venus.crud.entity.enums.ImageRole;
import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record ProductImageResponse(
        Long id,
        Long productVersionId,
        String imageUrl,
        ImageRole imageRole,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
