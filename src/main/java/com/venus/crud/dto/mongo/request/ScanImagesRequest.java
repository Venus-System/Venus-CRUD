package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

public record ScanImagesRequest(
        @Schema(description = "Foto da frente da embalagem.")
        @Valid ScanImageRequest front,
        @Schema(description = "Foto do verso da embalagem.")
        @Valid ScanImageRequest back
) {
}
