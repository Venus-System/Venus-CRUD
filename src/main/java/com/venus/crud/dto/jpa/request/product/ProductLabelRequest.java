package com.venus.crud.dto.jpa.request.product;

import com.venus.crud.entity.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductLabelRequest(
        @NotNull Long productVersionId,
        @NotBlank String normalizedText,
        @NotBlank String language,
        @NotNull SourceType sourceType,
        String sourceReference
) {
}
