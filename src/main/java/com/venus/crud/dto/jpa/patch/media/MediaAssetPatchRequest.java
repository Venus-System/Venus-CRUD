package com.venus.crud.dto.jpa.patch.media;

public record MediaAssetPatchRequest(
        String altText,
        Integer sortOrder
) {
}
