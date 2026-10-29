package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanImagesResponse(
        @Schema(description = "Foto da frente da embalagem.")
        ScanImageResponse front,
        @Schema(description = "Foto do verso da embalagem.")
        ScanImageResponse back
) {
}
