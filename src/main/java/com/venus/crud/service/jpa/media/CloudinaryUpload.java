package com.venus.crud.service.jpa.media;

public record CloudinaryUpload(
        String publicId,
        String assetId,
        Long version,
        String secureUrl,
        String format,
        Integer width,
        Integer height,
        Long bytes,
        String folder
) {
}
