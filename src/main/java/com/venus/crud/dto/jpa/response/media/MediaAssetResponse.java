package com.venus.crud.dto.jpa.response.media;

import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.MediaStatus;
import java.time.OffsetDateTime;

public record MediaAssetResponse(
        Long mediaAssetId,
        MediaPurpose purpose,
        String url,
        String publicId,
        String altText,
        Integer width,
        Integer height,
        String format,
        Integer sortOrder,
        MediaStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
