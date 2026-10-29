package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanOcrResponse(
        @Schema(description = "Texto lido na frente da embalagem.")
        ScanOcrFrontResponse front,
        @Schema(description = "Texto lido no verso da embalagem.")
        ScanOcrBackResponse back
) {
}
