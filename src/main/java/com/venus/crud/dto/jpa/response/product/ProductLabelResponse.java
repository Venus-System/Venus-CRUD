package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.SourceType;
import java.time.OffsetDateTime;

public record ProductLabelResponse(
        Long id,
        Long productVersionId,
        String normalizedText,
        String language,
        SourceType sourceType,
        String sourceReference,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
