package com.venus.crud.dto.jpa.patch.product;

import com.venus.crud.entity.enums.SourceType;

public record ProductLabelPatchRequest(
        Long productVersionId,
        String normalizedText,
        String language,
        SourceType sourceType,
        String sourceReference
) {
}
