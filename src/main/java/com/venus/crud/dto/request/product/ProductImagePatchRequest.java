package com.venus.crud.dto.request.product;

import com.venus.crud.entity.enums.ImageRole;
import com.venus.crud.entity.enums.SourceType;

public record ProductImagePatchRequest(
        Long productVersionId,
        String imageUrl,
        ImageRole imageRole,
        SourceType sourceType,
        String sourceReference
) {
}
