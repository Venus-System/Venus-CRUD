package com.venus.crud.dto.request.product;

import com.venus.crud.entity.enums.ImageRole;
import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductImageRequest(
        @NotNull Long productVersionId,
        @NotBlank String imageUrl,
        @NotNull ImageRole imageRole,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
